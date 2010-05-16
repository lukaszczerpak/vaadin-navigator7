package org.vaadin.navigator7;

/**
 * There should be one instance of this for the whole web application = 1 instance per servlet context.
 * The name is confusing with com.vaadin.Application (which has one instance per HttpSession) because
 * probably com.vaadin.Application should be renamed "UserContext" (for Java web developers, the term "Application" means ServletContext)
 * 
 * See http://vaadin.com/forum/-/message_boards/message/155212#_19_message_159169
 * 
 * Most of the attributes and methods should probably go in a "NavigableWebApplication" sub class, named in the web.xml Vaadin servlet definition (as init parameter).
 * 
 * @author John Rizzo - BlackBeltFactory.com
 *
 */
public class WebApplication {

    // class level Singleton to simplify for the moment.
    // If integrated into Vaadin 7, please bind this to the ServletContext (which you remember the instance here instead of the WebApplication)
    // when the Vaadin servlet is called for the first time.
    // In the web.xml Vaadin servlet definition, we should have the descendant of this class that should be instantiated 
    // (instead of the descendant of com.vaadin.Application as we have now). 
    protected static WebApplication instance = new WebApplication(); 
    
    public static WebApplication getInstance() {
        return instance;
    }

    ///////////////////////////////////////////// Navigable stuff /////////////////////////////////////////////////////
    
    protected NavigatorConfig navigatorConfig = new NavigatorConfig();
    
    /** Don't hesitate to change this value with another descendant of UriAnalyser in your constructor's descendant. */
    protected ParamUriAnalyzer uriAnalyzer = new ParamUriAnalyzer();  

    
    public WebApplication() {

    }

    public void registerPages(Class[] pageClasses) {
        navigatorConfig.registerPages(pageClasses);
    }

    /** Scans the pages annotated with @Page in the classpath, for the sub-package of the package given as parameter */
    public void registerPages(String packageName) {
        // do some scanning of @Page annotated classes and add them to the navigator.
        navigatorConfig.registerPages(packageName);
    }

    public NavigatorConfig getNavigatorConfig() {
        return navigatorConfig;
    }

    public ParamUriAnalyzer getUriAnalyzer() {
        return uriAnalyzer;
    }

    public void setUriAnalyzer(ParamUriAnalyzer paramUriAnalyzer) {
        uriAnalyzer = paramUriAnalyzer;
    }

}
