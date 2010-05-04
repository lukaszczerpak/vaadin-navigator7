package org.vaadin.navigator7;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import com.vaadin.ui.Button.ClickListener;
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
    NavigatorConfig navigatorConfig;
    UriAnalyzer uriAnalyzer; 
    
    NavigatorWarningDialogMaker navigatorWarningDialogMaker = new DefaultNavigatorWarningDialogMaker(); 
    
    protected List<NavigationListener> navigationListenerList = new ArrayList<NavigationListener>();

    public Navigator(NavigatorConfig aNavigatorConfig, UriAnalyzer aUriAnalyzer) {
        this.navigatorConfig = aNavigatorConfig;
        this.uriAnalyzer = aUriAnalyzer;
        
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
        String[] names = getUriAnalyzer().extractPageNameAndParamsFromFragment(fragment);
        String pageName = names[0];
        final String params = names[1];

        // Get the page class from the page name.
        Class<? extends Component> pageClass;
        if (pageName == null) {
            pageClass = navigatorConfig.getHomePageClass();
        } else {
            // Do we know that name (that URI) ?
            pageClass = navigatorConfig.getPageClass(pageName);
            if (pageClass == null) {  // Page does not exist in our config (url hacking?)
                handleInvalidUri();
                pageClass = navigatorConfig.getHomePageClass();
            }
        }

        if (needToWaitWarningDialogBoxBeforeLeaving(pageClass, params)) {
            return;  // The listener of the modal dialog box being shown will eventually request a page change later. 
        } // Else, we continue to change the page. The current page implements no NavigationWarner, or it says that no data could currently be lost.

        Component currentPage = NavigableApplication.getCurrentNavigableAppLevelWindow().getPage();
        if (currentPage == null || ! currentPage.getClass().equals(pageClass)) { // We need to change to a new screen
            // We don't call navigateTo(), because we don't want the uri to be changed (we are just answering a change notification).
            instantiateAndPlacePageWithoutChangingUri(pageClass, params);
        } else {
            // We don't reinstantiate the page, we just warn it that its parameters changed.
            notifyParamsChangedListener(pageClass, params);
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
        if (needToWaitWarningDialogBoxBeforeLeaving(pageClass, params)) {
            return;  // The listener of the modal dialog box being shown will eventually request a page change later. 
        } // Else, we continue to change the page. The current page implements no NavigationWarner, or it says that no data could currently be lost.

        instantiateAndPlacePageWithoutChangingUri(pageClass, params);
        setUriParams(params);
    }
    
    /** Rebuild (reinstantiates) the current page, and calls the PageParamListener (the page) with the current parameters (to display/select the right data). */
    public void reloadCurrentPage(){
        // We need the params (we already know the screen name and class).
        String[] fragment = getUriAnalyzer().extractPageNameAndParamsFromFragment(uriFragmentUtility.getFragment());
        String params;
        if (fragment != null && fragment.length > 1)  {
            params = fragment[1];
        } else {
            params = null;
        }
    
        
        instantiateAndPlacePageWithoutChangingUri(((NavigableAppLevelWindow)getWindow()).getPage().getClass(),
                                                  params);
    }
    
    /** Don't call this method (except in rare cases). Prefer navigateTo().
     * Instantiates and place the page in the PageTemplate. 
     * Notifies the new page that the parameters changed (if it implements PageParamListener) 
     * This does not check the NavigationWarner mechanism and do change the page. */
    public void instantiateAndPlacePageWithoutChangingUri(Class<? extends Component> pageClass, String params) {
        Component page;
        try {
            // instantiate page like: auctionPage = new AuctionPage();
            page = (Component) pageClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Problem while creating page class ["+pageClass+"]. Probably bug. Does your page class have a no-arg constructor?", e);
        }

        getNavigableAppLevelWindow().changePage(page);
        
        notifyParamsChangedListener(pageClass, params);
        notifyNavigationListenersPageChanged(pageClass, params);
    }
    
    /** method called in a special case, the MainWindow has just been instantiated and not FragmentChangedEvent will be fired because there is no fragment (home page). */
    // SEE: http://vaadin.com/forum/-/message_boards/message/57240
    //   Probably to be removed with Vaadin 7 and the notion of application level window.
    public void initializeHomePageAsFristPage() {
        instantiateAndPlacePageWithoutChangingUri(navigatorConfig.getHomePageClass(), null);
    }

    
    // Pass part of the url to the screen (that has its own conventions for analyzing it)
    @SuppressWarnings("unchecked")
    protected void notifyParamsChangedListener(Class<? extends Component> pageClass, String params) {
        NavigationEvent event = new NavigationEvent(this, uriAnalyzer, pageClass, params);
        Component currentPage = getNavigableAppLevelWindow().getPage();
        if (currentPage instanceof ParamChangeListener) {  
            ((ParamChangeListener)currentPage).paramChanged(event);
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
        
        uriFragmentUtility.setFragment(getUriAnalyzer().buildFragmentFromPageAndParameters(currentPage.getClass(), params, false), false);
    }


    public boolean needToWaitWarningDialogBoxBeforeLeaving(Class<? extends Component> pageClass, String params) {
        // Check if the user really wants to leave the current page.
        Component currentPage = getNavigableAppLevelWindow().getPage();
        if (currentPage instanceof NavigationWarner) {
            NavigationWarner warnerCurrentPage = (NavigationWarner)currentPage;
            
            String warn = warnerCurrentPage.getWarningForNavigatingFrom();
            if (warn != null && warn.length() > 0) {
                getNavigatorWarningDialogMaker().createWarningDialog(warn, pageClass, params);
                return true;  // The listener of the modal dialog box being shown will eventually request a page change later. 
            }
        }
        return false;
    }
    
    
    public NavigatorWarningDialogMaker getNavigatorWarningDialogMaker() {
        return navigatorWarningDialogMaker;
    }

    public void setNavigatorWarningDialogMaker(
            NavigatorWarningDialogMaker navigatorWarningDialogMaker) {
        this.navigatorWarningDialogMaker = navigatorWarningDialogMaker;
    }



    /** Implemented by the pages that want to show a warning message to the user before he leaves to another page. 
     * 
     * @author Joonas
     */
    public interface NavigationWarner {
        /**
         * Get a warning that should be shown to user before navigating away
         * from the page.
         * 
         * If the current page is in state where navigating away from it could
         * lead to data loss, this method should return a message that will be
         * shown to user before he confirms that he will leave the screen. If
         * there is no need to ask questions from user, this should return null.
         * 
         * @return Message to be shown or null if the page may be changed without warning the end-user.
         */
        public String getWarningForNavigatingFrom();

    }

    /** Used (probably implemented) by the pages who are not happy with the DefaultNavigationWarner.
     * 
     * @author John Rizzo - BlackBeltFactory.com
     *
     */
    public interface NavigatorWarningDialogMaker {
        /** Creates and displays a modal dialog box (window) that asks if the user is sure to leave the current page.
         * 
         *  Typically the dialog contains an "continue" button with that code:
         * 
                Button cont = new Button("Continue",  new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        Navigator navigator = Navigator.getInstance(MyNavigableApplication.getCurrentMainWindow());
                        navigator.instantiateAndPlacePageWithoutChangingUri(pageClass, params);
                        main.removeWindow(wDialog);
                    }
                });
         * 
         * @param warningMessage  message returned by the page we are leaving through the NavigationWarner.getWarningForNavigatingFrom() method.
         * @param pageClass  to be passed to the Navigator.navigateTo() method, in the ok button ClickListener.
         * @param params   to be passed to the Navigator.navigateTo() method, in the ok button ClickListener.
         */
        public void createWarningDialog(String warningMessage, Class<? extends Component> pageClass, String params);
    }

    /** Default implementation.
     */
    class DefaultNavigatorWarningDialogMaker implements NavigatorWarningDialogMaker {

        @Override
        public void createWarningDialog(String warningMessage, final Class<? extends Component> pageClass, final String params) {
            VerticalLayout lo = new VerticalLayout();
            lo.setMargin(true);
            lo.setSpacing(true);
            lo.setWidth("400px");
            final Window wDialog = new Window("Warning", lo);
            wDialog.setModal(true);
            final Window main = getWindow();
            main.addWindow(wDialog);
            lo.addComponent(new Label(warningMessage));
            lo.addComponent(new Label("If you do not want to navigate away from the current screen, press Cancel."));

            Button cancel = new Button("Cancel", new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    main.removeWindow(wDialog);
                }
            });

            Button cont = new Button("Continue",  new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    instantiateAndPlacePageWithoutChangingUri(pageClass, params);
                    main.removeWindow(wDialog);
                }
            });

            
            HorizontalLayout h = new HorizontalLayout();
            h.addComponent(cancel);
            h.addComponent(cont);
            h.setSpacing(true);
            lo.addComponent(h);
            lo.setComponentAlignment(h, "r");
            
        }
        
    }

    public NavigableAppLevelWindow getNavigableAppLevelWindow() {
     return (NavigableAppLevelWindow)this.getWindow();
    }
    
    
    public void addNavigationListener(NavigationListener navL) {
        navigationListenerList.add(navL);
    }

    
    
    public NavigatorConfig getNavigatorConfig() {
        return navigatorConfig;
    }


    public UriAnalyzer getUriAnalyzer() {
        return uriAnalyzer;
    }


    public void notifyNavigationListenersPageChanged(Class<? extends Component> pageClass, String params) {
        NavigationEvent event = new NavigationEvent(this, uriAnalyzer, pageClass, params);
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
