package org.vaadin.navigator7;

import org.vaadin.navigator7.interceptor.PageInvocation;
import org.vaadin.navigator7.uri.ParamPageResource;
import org.vaadin.navigator7.uri.UriAnalyzer;
import org.vaadin.navigator7.window.NavigableAppLevelWindow;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.Window;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedListener;

/** Responsible for detecting URI changes and triggers Page changes. 
 * Each instance of NavigableAppLevelWindow contains its instance of a Navigator.
 * 
 * @author John Rizzo - BlackBeltFactory.com
 */
public class Navigator 
            extends CustomComponent  // because it needs to hold a UriFragmentUtility. Else it may not need to be a Component in a Window. 
            implements FragmentChangedListener {

    
    /** Kind of forward to another page */
    public void navigateTo (Class<? extends Component> pageClass) {
        navigateTo(pageClass, (String)null);
    }

    
    /** Kind of forward to another page */
    public void navigateTo(PageResource pageResource) {
        navigateTo(pageResource.getPageClass(), pageResource.getParams());
    }

    /** Kind of forward to another page
     * 
     * @param pageClass
     * @param params String to add in the URI, after the page name. Updates the URL displayed in the browser. Set "" if you need no parameter.
     */
    public void navigateTo (Class<? extends Component> pageClass, String params) {
        // Starts interceptors chain call.
        invokeInterceptors(pageClass, params, true);
    }

    /** Rebuild (reinstantiates) the current page, and calls the PageParamListener (the page) with the current parameters (to display/select the right data). */
    public void reloadCurrentPage(){
        // We need the params (we already know the screen name and class).
        WebApplication webApp = getNavigableAppLevelWindow().getNavigableApplication().getWebApplication();  // Sometimes we are called via a file v6 upload listener (and in that case WebApplication.getCurrent() is null) 
        String[] fragment = webApp.getUriAnalyzer().extractPageNameAndParamsFromFragment(uriFragmentUtility.getFragment());
        String params;
        if (fragment != null && fragment.length > 1)  {
            params = fragment[1];
        } else {
            params = null;
        }
        
        invokeInterceptors(getNavigableAppLevelWindow().getPage().getClass(), params, false);
//        checkParamsThenInstantiatePage(((NavigableAppLevelWindow)getWindow()).getPage().getClass(), params, false);
    }

    
    /** Updates the url in the browser (to enable bookmarking).
     * This method is called by the current page, with appropriate parameters (as "auctionid=123")
     * We add the screen name: "auction/auctionid=123"
     * No NavigationEvent is fired when this method is called, because the we don't want the page to "react".
     */
    public void setUriParams(String params) {
        Component currentPage = getNavigableAppLevelWindow().getPage();
        
        // Just defensive coding
        if (currentPage == null) {
            throw new IllegalStateException("There is no current page. There should be at this late stage, when this method is called.");
        }
        
        uriFragmentUtility.setFragment(
                WebApplication.getCurrent().getUriAnalyzer().buildFragmentFromPageAndParameters(currentPage.getClass(), params, false),
                false);
    }


    /** Shows a notification in case of the uri is invalid (contains a page name, but an invalid one).
     * Override this if you prefer another action in case of invalid URI (probably wrongly typed in the browser by the visitor). */
    protected void handleInvalidUri(String message) {
        getWindow().showNotification("Invalid URL<br/>",
                "If it is a link from within our site, thank you to report the problem.<br/>" + message,
                Window.Notification.TYPE_HUMANIZED_MESSAGE);
    }

    

    /**
     * Fired when a page is changed.
     * 
     * @author John Rizzo
     */
    public static class NavigationEvent extends Component.Event {

        UriAnalyzer uriAnalyzer; // could be retrieved by the ParamChangeListener from the Navigator. Given here for convenience.
        Class<? extends Component> pageClass;
        String params;
        
        /**
         * New instance of text change event.
         * 
         * @param source the Source of the event = navigator.
         */
        public NavigationEvent(Navigator source, UriAnalyzer uriAnalyzer, 
                Class<? extends Component> pageClass, String params) {
            super(source);
            this.pageClass = pageClass;
            this.params = params;
        }

        /**
         * Gets the Navigator who fired the event. From it, you can retrieve the concerned AppLevelWindow.
         */
        public Navigator getNavigator() {
            return (Navigator) getSource();
        }

        public Class<? extends Component> getPageClass() {
            return pageClass;
        }

        public String getParams() {
            return params;
        }
    }

    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INTERNAL STUFF        INTERNAL STUFF            INTERNAL STUFF         INTERNAL STUFF         INTERNAL STUFF  //  
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INTERNAL STUFF        INTERNAL STUFF            INTERNAL STUFF         INTERNAL STUFF         INTERNAL STUFF  //  
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INTERNAL STUFF        INTERNAL STUFF            INTERNAL STUFF         INTERNAL STUFF         INTERNAL STUFF  //  
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INTERNAL STUFF        INTERNAL STUFF            INTERNAL STUFF         INTERNAL STUFF         INTERNAL STUFF  //  
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INTERNAL STUFF        INTERNAL STUFF            INTERNAL STUFF         INTERNAL STUFF         INTERNAL STUFF  //  
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INTERNAL STUFF        INTERNAL STUFF            INTERNAL STUFF         INTERNAL STUFF         INTERNAL STUFF  //  
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INTERNAL STUFF        INTERNAL STUFF            INTERNAL STUFF         INTERNAL STUFF         INTERNAL STUFF  //  
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INTERNAL STUFF        INTERNAL STUFF            INTERNAL STUFF         INTERNAL STUFF         INTERNAL STUFF  //  
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INTERNAL STUFF        INTERNAL STUFF            INTERNAL STUFF         INTERNAL STUFF         INTERNAL STUFF  //  
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    
    UriFragmentUtility uriFragmentUtility;
    
    

    public Navigator() {
        // To handle the url changes and for bookmarking.
        uriFragmentUtility = new UriFragmentUtility();
        uriFragmentUtility.addListener(this);

        setCompositionRoot(uriFragmentUtility);
    }
    
    
    /** uri has the form of: "http://domain.com/appName/#user/userid=555", here we get the "user/userid=555".
     * We extract the first part, before the 1st "/" ("user"), and we select a new center component based on that.
     * That component takes place in the main area of the layout,
     * Then we give the rest of the string  to that component ("userid=555").
     */
    @Override
    public void fragmentChanged(FragmentChangedEvent source) {
        String fragment = source.getUriFragmentUtility().getFragment();

        // Get the pageName and params from the URI
        String[] names = WebApplication.getCurrent().getUriAnalyzer().extractPageNameAndParamsFromFragment(fragment);
        String pageName = names[0];
        String params = names[1];

        // Get the page class from the page name.
        Class<? extends Component> pageClass;
        if (pageName == null || "".equals(pageName.trim())) {
            pageClass = WebApplication.getCurrent().getNavigatorConfig().getHomePageClass();
        } else {
            // Do we know that name (that URI) ?
            pageClass = WebApplication.getCurrent().getNavigatorConfig().getPageClass(pageName);
            if (pageClass == null) {  // Page does not exist in our config (url hacking?)
                if (! fragment.startsWith("/")) {
                    handleInvalidUri("No page with name '" + pageName+"'.");
                    params = fragment; // Let's give the full fragment to the home page, maybe it will find something useful in it.
                } else { 
                    // It starts with "/" and means there is no page name on purpose because it's the home page.
                    // i.e. http://mycompany.com/#/param1/param2     In that case param1 is no page name but a parameter of the home page.
                    // => we display no error message

                    // What we believed to be the page name is probably part of the parameters for the home page.
                    params = fragment.substring(1);  // i.e.  "param1/param2"
                }
                
                pageClass = WebApplication.getCurrent().getNavigatorConfig().getHomePageClass();
            }
        }


        Component currentPage = NavigableApplication.getCurrentNavigableAppLevelWindow().getPage();
        if (currentPage == null || ! currentPage.getClass().equals(pageClass)) { // We need to change to a new page
            // We don't call navigateTo(), because we don't want the uri to be changed (we are just answering a change notification).
            invokeInterceptors(pageClass, params, false);
        } else {
            // We don't reinstantiate the page, we just warn it that its parameters changed.
            invokeInterceptors(currentPage, params, false);
//            checkParamsThenNotifyListener(currentPage, params);
        }
    }



//    /** Kind of forward to another page
//     * 
//     * @param pageClass
//     * @param params String to add in the URI, after the page name. Updates the URL displayed in the browser. Set "" if you need no parameter.
//     */
//    public void navigateTo (Class<? extends Component> pageClass, UriParam uriParam) {
//        // Starts interceptors chain call.
//        navigateTo(pageClass, ParamInjector.generateFragment(uriParam));
//    }


    
    /** Don't call this directly. Prefer navigateTo
     * Starts Interceptors chain invocation, that usually ends up with page instantiation.
     * 
     * @param pageClass
     * @param params String to add in the URI, after the page name. Updates the URL displayed in the browser. Set "" if you need no parameter.
     */
    public void invokeInterceptors (Class<? extends Component> pageClass, String params, boolean needToChangeUri) {
        // Starts interceptors chain call.
        PageInvocation pageInvocation = new PageInvocation(this, pageClass, params, needToChangeUri);
        pageInvocation.invoke();  // Will ultimately call Navigator.placePage
    }

    
    /** Don't call this directly. Prefer navigateTo
     * Starts Interceptors chain invocation, reusing the current (given) page.
     * 
     * @param page already instantiated (probably an url fragment change).
     * @param params String to add in the URI, after the page name. Updates the URL displayed in the browser. Set "" if you need no parameter.
     */
    public void invokeInterceptors (Component page, String params, boolean needToChangeUri) {
        // Starts interceptors chain call.
        PageInvocation pageInvocation = new PageInvocation(this, page, params, needToChangeUri);
        pageInvocation.invoke();  // Will ultimately call Navigator.placePage
    }
        
    
    

    
//    /** don't reinstantiate the page but check the params and if they are ok, notify the page if its a PageChangeListener.
//     * Else the user is notified of an URL problem and page is not instantiated. 
//     */
//    public void checkParamsThenNotifyListener(Component page, String params) {
//        UriParam uriParam;
//        try {
//            uriParam = UriParam.activate(page.getClass(), params);
//        } catch (ParameterValidationException e) {
//            // User has already been notified of the problem.
//            // Do nothing
//            return;
//        }
//        notifyParamsChangedListener(page, uriParam, params);
//    }

    
//    /** call instantiateAndPlacePage if UriParam correctly instantiated and validated.
//     * Else the user is notified of an URL problem and page is not instantiated. 
//     */
//    public void checkParamsThenInstantiatePage(Class<? extends Component> pageClass, String params, boolean needToChangeUri) {
//        UriParam uriParam;
//        try {
//            uriParam = UriParam.activate(pageClass, params);
//        } catch (ParameterValidationException e) {
//            // User has already been notified of the problem.
//            // Do nothing
//            return;
//        }
//        instantiateAndPlacePage(pageClass, uriParam, params, needToChangeUri);
//    }
    
    /** Don't call this method (except in rare cases). Prefer navigateTo().
     * Instantiates and place the page in the PageTemplate. 
     * Notifies the new page that the parameters changed (if it implements PageParamListener) 
     * This does not check the NavigationWarner mechanism and do change the page. */
    public void placePage(Component page, String params, boolean needToChangeUri) {
        getNavigableAppLevelWindow().changePage(page);
               
        if (needToChangeUri) {
            setUriParams(params);
        }
    }
    
    /** method called in a special case, the MainWindow has just been instantiated and not FragmentChangedEvent will be fired because there is no fragment (home page). */
    // SEE: http://vaadin.com/forum/-/message_boards/message/57240
    //   Probably to be removed with Vaadin 7 and the notion of application level window.
    public void initializeHomePageAsFristPage() {
        invokeInterceptors(WebApplication.getCurrent().getNavigatorConfig().getHomePageClass(), null, false);
    }

    
//    // Pass part of the url to the screen (that has its own conventions for analyzing it)
//    protected void notifyParamsChangedListener(Component page, UriParam uriParam, String params) {
//        if (page instanceof ParamChangeListener) {  
//            NavigationEvent event = new NavigationEvent(this, WebApplication.getCurrent().getUriAnalyzer(), page.getClass(), uriParam, params);
//            ((ParamChangeListener)page).paramChanged(event);
//        } else {  // Page probably does not want to be notified twice... 
//            UriParam.callParamChangedMethodIfAny(page, uriParam);
//        }
//
//    }
//    

    




    public NavigableAppLevelWindow getNavigableAppLevelWindow() {
        return (NavigableAppLevelWindow)this.getWindow();
    }

    
    
//    public void addNavigationListener(NavigationListener navL) {
//        navigationListenerList.add(navL);
//    }
//
//    
//    
//    public void notifyNavigationListenersPageChanged(Class<? extends Component> pageClass, UriParam uriParam, String params) {
//        NavigationEvent event = new NavigationEvent(this, WebApplication.getCurrent().getUriAnalyzer(), pageClass, uriParam, params);
//        for (NavigationListener navL : navigationListenerList) {
//            navL.pageChanged(event);
//        }
//    }
//    
    


    
}
