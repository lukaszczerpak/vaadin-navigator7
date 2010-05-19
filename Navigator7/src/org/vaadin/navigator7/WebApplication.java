package org.vaadin.navigator7;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;

/**
 * You may extend this class to manually register your pages. If you do that, you must specify the class name as an init parameter of the NavigableApplicationServlet (as MyWebApplication in this example):
 * <pre><servlet-class>org.vaadin.navigator7.NavigableApplicationServlet</servlet-class>
    <init-param>
        <description>Vaadin application class to start</description>
        <param-name>application</param-name>
        <param-value>example.ui.application.MyNavigableApplication</param-value>
    </init-param>
    <init-param>
        <description>Navigator7 WebApplication class to start (optionnal)</description>
        <param-name>webApplication</param-name>
        <param-value>example.ui.application.MyWebApplication</param-value>
    </init-param>
  </servlet>
 * </pre>
 * 
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

    static protected ThreadLocal<ServletContext> currentServletContext = new ThreadLocal<ServletContext>();

    /** Don't hesitate to use this method ;-) */
    public static WebApplication getCurrent() {
        return (WebApplication)currentServletContext.get().getAttribute(NavigableApplicationServlet.WEBAPPLICATION_CONTEXT_ATTRIBUTE_NAME);
    }
    

    ///////////////////////////////////////////// Navigable stuff /////////////////////////////////////////////////////
    
    protected NavigatorConfig navigatorConfig = new NavigatorConfig();
    
    /** Don't hesitate to change this value with another descendant of UriAnalyser in your constructor's descendant. */
    protected ParamUriAnalyzer uriAnalyzer = new ParamUriAnalyzer();

    protected ServletContext servletContext;  

    

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


    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
