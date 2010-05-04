package org.vaadin.navigator7;

import javax.servlet.http.HttpServletRequest;

import org.vaadin.navigator7.window.NavigableAppLevelWindow;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.ui.Window;

/** Application that instantiates NavigableAppLevelWindow and manages browser tabs correctly (one AppLevelWindow per tab).
 * It implements the thread local pattern to enable the easy (for you) retrieval of the current application and AppLevelWindow, wherever you are in your code (with static no arg methods). 
 * It is bound to a NavigatorConfig that you may use to know the pages managed by your application. 
 * 
 * @author John Rizzo - BlackBeltFactory.com
 *
 */
public abstract class NavigableApplication extends Application implements TransactionListener {

    transient protected NavigatorConfig navigatorConfig;
    
    /** Don't hesitate to change this value with another descendant of UriAnalyser in your constructor's descendant. */
    transient protected ParamUriAnalyzer uriAnalyzer = new ParamUriAnalyzer();  

    static protected ThreadLocal<NavigableApplication> currentApplication = new ThreadLocal<NavigableApplication>();
    static protected ThreadLocal<NavigableAppLevelWindow> currentNavigableAppLevelWindow = new ThreadLocal<NavigableAppLevelWindow>();
    static protected ThreadLocal<String> veryInitialUriFragment = new ThreadLocal<String>();  // Trick until further version of Vaadin. See comment in transactionListener below

    
    
    public NavigableApplication() {
        navigatorConfig = new NavigatorConfig();
    }
    
    public void registerPages(Class[] pageClasses) {
        navigatorConfig.registerPages(pageClasses);
    }

    /** Scans the pages annotated with @Page in the classpath, for the sub-package of the package given as parameter */
    public void registerPages(String packageName) {
        // do some scanning of @Page annotated classes and add them to the navigator.
        navigatorConfig.registerPages(packageName);
    }

    /** Don't override me, because you are not supposed to create any window by yourself (yes, I dare, I close the API with final ;-) */
    @Override
    public final void init() {
        // Defensive programming.
        if (navigatorConfig.getPagesClass().size() == 0) {
            throw new IllegalStateException("No page in configuration. You should register pages in the constructor of your NavigableApplication sub-class," +
            		" typically by calling NavigableApplication.registerPages() method." +
            		" If you did, then no page has been found. Check your settings as the presence of the @Page annotation on your pages.");
        }
        
        // Register a transaction listener that updates our ThreadLocal with each request
        if (getContext() != null) {
            getContext().addTransactionListener(this);
        }   

        boolean currentWasNull = false;  // During initialization, we trigger code that might needs getCurrent(), but TransactionListener has not been set yet and will not be called.
        if (getCurrent() == null) {
            currentApplication.set(this);
            currentWasNull = true;
        }

        
        NavigableAppLevelWindow navWin;
        setMainWindow(navWin = createNewNavigableAppLevelWindow("AppLevelWindow"));
        
//        navWin.pageContainer = navWin.createComponents(); 

        
        if (currentWasNull == true) {
            currentApplication.remove();
        }
    }
    
    public NavigableAppLevelWindow createNewNavigableAppLevelWindow(String name) {
        NavigableAppLevelWindow appLevelWindow = createNewNavigableAppLevelWindow();
        appLevelWindow.setName(name);   // Thank to this name, we can easily sort application level windows from floating windows (dialog boxes).
                                            // we could leave the name null and it would be set to a random number "012345679", but the form "AppLevelWindow_0123456789" is probably clearer when debugging.
        return appLevelWindow;
    }

    /** Please override me, to instantiate your application's NavigableAppLevelWindow descendant */  
    public abstract NavigableAppLevelWindow createNewNavigableAppLevelWindow();


    /** see ancestor useful JavaDoc */
    @SuppressWarnings("unchecked")
    @Override
    public Window getWindow(String name) {
        // We check application state here because superclass returns null if
        // a) the application is not running anymore
        // b) it doesn't have a window for the given name
        if (!isRunning()) {
            return null;
        }
        
        Window result;
        
        // Is that an existing window?
        Window existingWindow =  super.getWindow(name);
        if (existingWindow != null) {  // Yes, existing window, nothing to be created.
            result = existingWindow;
            
        } else {  // New case where we create one instance of the main window per browser window/tab that the user opens in the same HttpSession (= in the same Application instance).
            ///// Is it a request for a new main window (from another browser tab, for the same user/session) ?
            ///// If the name is like MainWindowName_01234567890 (where the number is random), then it is a request for a new instance of main window.
            ///// else, it's noise (floating window?)
            
            if  (! isNameOfAppLevelWindow(name)) {  // Already happened during debugging with Vaadin 6.0 or 6.1 (don't remember)
                return null;
            }
            
            
            /////// We create a new instance of a App level Window.
            result = this.createNewNavigableAppLevelWindow(name); // we could leave the name null and it would be set to a random number "012345679", but the form "AppLevelWindow_0123456789" is probably clearer when debugging.
            addWindow(result);
        }
        
        if  (isNameOfAppLevelWindow(name)) {
            NavigableAppLevelWindow navigableAppLevelWindow = (NavigableAppLevelWindow)result;
            currentNavigableAppLevelWindow.set(navigableAppLevelWindow);

            
            // SEE: http://vaadin.com/forum/-/message_boards/message/57240
            //   Probably to be removed with Vaadin 7 and the notion of application level window.
            if ("".equals(getVeryInitialUriFragment())) { // This case is different from null. If "", then it's for the home page.
                if (navigableAppLevelWindow.getPage() == null) {  // screen not decided yet
                    navigableAppLevelWindow.getNavigator().initializeHomePageAsFristPage();  // Then it should be the home page (we expect no #pageName uri in the current URL).
                }
            }
        }

        return result;
    }   
    /** Is name like AppLevelWindow_01234567890   ?? */
    private boolean isNameOfAppLevelWindow(String name) {
        Window primaryMainWindow = getMainWindow();
        if (primaryMainWindow == null) {
            // We may be in a strange case where Application.getWindow() is called before Application.setMainWindow() has been called. Would it be a Vaadin bug?
            return false;
        }
        String[] nameParts = name.split("_");
        return name.equals(primaryMainWindow.getName()) ||
              (nameParts.length == 2  &&  nameParts[0].equals(primaryMainWindow.getName()));
    }

    /**
     * @return the current application instance
     */
    public static NavigableApplication getCurrent() {
        return currentApplication.get();
    }

    /**
     * @return the current application level Window instance
     */
    public static NavigableAppLevelWindow getCurrentNavigableAppLevelWindow() {
        return currentNavigableAppLevelWindow.get();
    }

    public static String getVeryInitialUriFragment() {
        return veryInitialUriFragment.get();
    }
    
    /**
     * TransactionListener
     */
    @Override
    public void transactionStart(Application application, Object transactionData) {
        if (this != application) { // It does not concern us.
            return;
        } 
    
        if (getCurrent() == null) {
            currentApplication.set(this);
        }
        
        // SEE: http://vaadin.com/forum/-/message_boards/message/57240
        //   Probably to be removed with Vaadin 7 when the uri mechanism will be more deeply integrated in the core of Vaadin.
        // This tries to detect if it's an initial request (HomeScreen should be rendered) or not.
        // If we don't do that, the HomeScreen is redisplayed before every page when we use a direct url (or a link).
        // Better solution should come with Vaadin 7.
        HttpServletRequest request = (HttpServletRequest) transactionData;
        veryInitialUriFragment.set(request.getParameter("fr"));  // Could be null.
    }

    @Override
    public void transactionEnd(Application application, Object transactionData) {
        if (this != application) { // It does not concern us.
            return;
        } 

        currentApplication.remove();
        currentNavigableAppLevelWindow.remove();
        veryInitialUriFragment.remove();
    }

    public NavigatorConfig getNavigatorConfig() {
        return navigatorConfig;
    }

    public ParamUriAnalyzer getUriAnalyzer() {
        return uriAnalyzer;
    }
    
    


}