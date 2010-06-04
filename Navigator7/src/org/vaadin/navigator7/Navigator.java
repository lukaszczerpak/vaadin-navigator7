package org.vaadin.navigator7;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.vaadin.navigator7.interceptor.Interceptor;
import org.vaadin.navigator7.interceptor.PageInvocation;
import org.vaadin.navigator7.window.NavigableAppLevelWindow;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
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

    UriFragmentUtility uriFragmentUtility;
    
    
    protected List<NavigationListener> navigationListenerList = new ArrayList<NavigationListener>();

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
        final String params = names[1];

        // Get the page class from the page name.
        Class<? extends Component> pageClass;
        if (pageName == null) {
            pageClass = WebApplication.getCurrent().getNavigatorConfig().getHomePageClass();
        } else {
            // Do we know that name (that URI) ?
            pageClass = WebApplication.getCurrent().getNavigatorConfig().getPageClass(pageName);
            if (pageClass == null) {  // Page does not exist in our config (url hacking?)
                handleInvalidUri();
                pageClass = WebApplication.getCurrent().getNavigatorConfig().getHomePageClass();
            }
        }


        Component currentPage = NavigableApplication.getCurrentNavigableAppLevelWindow().getPage();
        if (currentPage == null || ! currentPage.getClass().equals(pageClass)) { // We need to change to a new page
            // We don't call navigateTo(), because we don't want the uri to be changed (we are just answering a change notification).
            invokeInterceptors(pageClass, params, false);
        } else {
            // We don't reinstantiate the page, we just warn it that its parameters changed.
            notifyParamsChangedListener(currentPage, params);
        }
    }

    /** Kind of forward to another page */
    public void navigateTo (Class<? extends Component> pageClass) {
        navigateTo(pageClass, null);
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

    /** Don't call this directly. Prefer navigateTo
     * Starts Interceptors chain invocation, that usually ends up with page instantiation.
     * 
     * @param pageClass
     * @param params String to add in the URI, after the page name. Updates the URL displayed in the browser. Set "" if you need no parameter.
     */
    public void invokeInterceptors (Class<? extends Component> pageClass, String params, boolean needToChangeUri) {
        // Starts interceptors chain call.
        PageInvocation pageInvocation = new PageInvocation(this, pageClass, params, needToChangeUri);
        pageInvocation.invoke();  // Will ultimately call   instantiateAndPlacePageWithUriChange(pageClass, params);
    }

    
        
    
    
    /** Rebuild (reinstantiates) the current page, and calls the PageParamListener (the page) with the current parameters (to display/select the right data). */
    public void reloadCurrentPage(){
        // We need the params (we already know the screen name and class).
        String[] fragment = WebApplication.getCurrent().getUriAnalyzer().extractPageNameAndParamsFromFragment(uriFragmentUtility.getFragment());
        String params;
        if (fragment != null && fragment.length > 1)  {
            params = fragment[1];
        } else {
            params = null;
        }
    
        
        instantiateAndPlacePage(((NavigableAppLevelWindow)getWindow()).getPage().getClass(),
                                                  params, false);
    }
    
    /** Don't call this method (except in rare cases). Prefer navigateTo().
     * Instantiates and place the page in the PageTemplate. 
     * Notifies the new page that the parameters changed (if it implements PageParamListener) 
     * This does not check the NavigationWarner mechanism and do change the page. */
    public void instantiateAndPlacePage(Class<? extends Component> pageClass, String params, boolean needToChangeUri) {
        Component page;
        try {
            // instantiate page like: auctionPage = new AuctionPage();
            page = (Component) pageClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Problem while creating page class ["+pageClass+"]. Probably bug. Does your page class have a no-arg constructor?", e);
        }

        getNavigableAppLevelWindow().changePage(page);
        
        notifyParamsChangedListener(page, params);
        notifyNavigationListenersPageChanged(pageClass, params);
        
        if (needToChangeUri) {
            setUriParams(params);
        }
    }
    
    /** method called in a special case, the MainWindow has just been instantiated and not FragmentChangedEvent will be fired because there is no fragment (home page). */
    // SEE: http://vaadin.com/forum/-/message_boards/message/57240
    //   Probably to be removed with Vaadin 7 and the notion of application level window.
    public void initializeHomePageAsFristPage() {
        instantiateAndPlacePage(WebApplication.getCurrent().getNavigatorConfig().getHomePageClass(), null, false);
    }

    
    // Pass part of the url to the screen (that has its own conventions for analyzing it)
    @SuppressWarnings("unchecked")
    protected void notifyParamsChangedListener(Component page, String params) {
        NavigationEvent event = new NavigationEvent(this, WebApplication.getCurrent().getUriAnalyzer(), page.getClass(), params);
        if (page instanceof ParamChangeListener) {  
            ((ParamChangeListener)page).paramChanged(event);
        } 
    }
    

    
    /** Shows a notification in case of the uri is invalid (contains a page name, but an invalid one).
     * Override this if you prefer another action in case of invalid URI (probably wrongly typed in the browser by the visitor). */
    protected void handleInvalidUri() {
        getWindow().showNotification("Invalid URL<br/>",
                "If it is a link from within our site, thank you to report the problem.",
                Window.Notification.TYPE_HUMANIZED_MESSAGE);
    }

    
    /** Updates the url in the browser (to enable bookmarking).
     * This method is called by the current page, with appropriate parameters (as "auctionid=123")
     * We add the screen name: "auction/auctionid=123"
     * No NavigationEvent is fired when this method is called.
     */
    public void setUriParams(String params) {
        Component currentPage = getNavigableAppLevelWindow().getPage();
        
        // Just defensive coding
        if (currentPage == null) {
            throw new IllegalStateException("There is no current page. There should be at this late stage, when this method is called.");
        }
        
        uriFragmentUtility.setFragment(WebApplication.getCurrent().getUriAnalyzer().buildFragmentFromPageAndParameters(currentPage.getClass(), params, false), false);
    }






    public NavigableAppLevelWindow getNavigableAppLevelWindow() {
     return (NavigableAppLevelWindow)this.getWindow();
    }
    
    
    public void addNavigationListener(NavigationListener navL) {
        navigationListenerList.add(navL);
    }

    
    
    public void notifyNavigationListenersPageChanged(Class<? extends Component> pageClass, String params) {
        NavigationEvent event = new NavigationEvent(this, WebApplication.getCurrent().getUriAnalyzer(), pageClass, params);
        for (NavigationListener navL : navigationListenerList) {
            navL.pageChanged(event);
        }
    }
    
    
    
    /**
     * Fired when a page is changed.
     * 
     * @author John Rizzo
     */
    public class NavigationEvent extends Component.Event {

        UriAnalyzer uriAnalyzer; // could be retrieved by the ParamChangeListener from the Navigator. Given here for convenience.
        Class<? extends Component> pageClass;
        String params;
        
        /**
         * New instance of text change event.
         * 
         * @param source the Source of the event = navigator.
         */
        public NavigationEvent(Navigator source, UriAnalyzer uriAnalyzer, Class<? extends Component> pageClass, String params) {
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

    /** Interface warns your application of NavigationEvents
     * Implemented by those that want to be warned systematically of every page change for that Application.
     *
     * Could be useful, for example to tell a GoogleAnalyticsTracker of uri changes.
     * 
     * @author John Rizzo
     */
    public interface NavigationListener extends Serializable {
        /** Only for page changes. Not called if only the parameters changed */
        public void pageChanged(NavigationEvent navigationEvent);
    }



    /** Implemented by pages (Components playing the role of pages)
     * Implemented by pages that want to be told when parameters change FOR THEM (difference with NavigationListener)
     * 
     * @author John Rizzo - BlackBeltFactory.com
     **/
    public interface ParamChangeListener {
        /** If uri is "http://server.com/#auction/toto/auctionid=33"
         * newParams we get = "toto/auctionid=33" (and we are probably the AuctionPage class implementing PageParamsListener)
         * If there is no parameter in the uri (as "#auction" or "#auction/"), then newParams is null.
         */
        public void paramChanged(NavigationEvent navigationEvent);
    }

       
}
