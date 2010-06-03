package org.vaadin.navigator7;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    public static final String WEBAPPLICATION_CONTEXT_ATTRIBUTE_NAME = WebApplication.class.getName();

    /** @See http://vaadin.com/forum/-/message_boards/message/155212?_19_delta=10&_19_keywords=&_19_advancedSearch=false&_19_andOperator=true&cur=2#_19_message_166110   */
    static protected ThreadLocal<ServletContext> currentServletContext = new ThreadLocal<ServletContext>();

    /** Don't hesitate to use this method ;-) */
    public static WebApplication getCurrent() {
        return (WebApplication)currentServletContext.get().getAttribute(WEBAPPLICATION_CONTEXT_ATTRIBUTE_NAME);
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

    /**
     * Should be called by NavigableApplicationServlet, or by your servlet extending com.vaadin.terminal.gwt.server.ApplicationServlet
     */
    @SuppressWarnings("unchecked")
    public static void init(ServletConfig servletConfig, ServletContext servletContext, ClassLoader classLoader)
            throws javax.servlet.ServletException {

        // Loads the application class using the same class loader
        // as the servlet itself

        // Gets the application class name, then class.
        final String applicationClassName = servletConfig.getInitParameter("webApplication");
        Class<? extends WebApplication> applicationClass;
        if (applicationClassName == null) {  // Using defaults.
            applicationClass = WebApplication.class;
        } else {
            Class<?> clazz;
            try {
                clazz = classLoader.loadClass(applicationClassName);
            } catch (final ClassNotFoundException e) {
                throw new ServletException("Failed to load application class: "
                        + applicationClassName);
            }
            if (! WebApplication.class.isAssignableFrom(clazz)) {
                throw new ServletException("Class given as parameter is no subclass of WebApplication (and it should): "
                        + clazz.getName() );
                
            }
            applicationClass = (Class<? extends WebApplication>) clazz;
            
            // Ok, we got the class, let's instantiate it.
            WebApplication webApplication; 
            try {
                webApplication = applicationClass.newInstance();
            } catch (final IllegalAccessException e) {
                throw new ServletException("Failed to instantiate WebApplication (sub?) class: " + applicationClass.getName(), e);
            } catch (final InstantiationException e) {
                throw new ServletException("Failed to instantiate WebApplication (sub?) class: " + applicationClass.getName(), e);
            }
            servletContext.setAttribute(WEBAPPLICATION_CONTEXT_ATTRIBUTE_NAME, webApplication);
            webApplication.setServletContext(servletContext);
        }
    }
    
    /** Should be called by NavigableApplicationServlet, or by your servlet extending com.vaadin.terminal.gwt.server.ApplicationServlet
     * I'd prefer to do that in a Filter, but it would be against the Vaadin current architecture 
     * Note that Vaadin TransactionListeners have no access to the ServletContext => we cannot use TransactionListeners. */
    @SuppressWarnings("unchecked")
    static public void beforeService(HttpServletRequest request,
            HttpServletResponse response, ServletContext servletContext) throws ServletException, IOException {
        WebApplication.currentServletContext.set(servletContext);
    }

    /** Should be called by NavigableApplicationServlet, or by your servlet extending com.vaadin.terminal.gwt.server.ApplicationServlet */
    @SuppressWarnings("unchecked")
    static public void afterService(HttpServletRequest request,
            HttpServletResponse response, ServletContext servletContext) throws ServletException, IOException {
        WebApplication.currentServletContext.remove();
    }

    
}
