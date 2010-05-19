package org.vaadin.navigator7;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.terminal.gwt.server.ApplicationServlet;

/** Extends Vaadin Servlet to instantiate the WebApplication class (or descendant) 
 * and attach it to the ServletContext. 
 * 
 * @author John Rizzo - BlackBeltFactory.com
 * 
 * */
public class NavigableApplicationServlet extends ApplicationServlet {

    public static final String WEBAPPLICATION_CONTEXT_ATTRIBUTE_NAME = WebApplication.class.getName();
    private Class<? extends WebApplication> applicationClass;

    /**
     * Called by the servlet container to indicate to a servlet that the servlet
     * is being placed into service.
     * 
     * @param servletConfig
     *            the object containing the servlet's configuration and
     *            initialization parameters
     * @throws javax.servlet.ServletException
     *             if an exception has occurred that interferes with the
     *             servlet's normal operation.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void init(javax.servlet.ServletConfig servletConfig)
            throws javax.servlet.ServletException {
        super.init(servletConfig);

        // Loads the application class using the same class loader
        // as the servlet itself

        // Gets the application class name
        final String applicationClassName = servletConfig
                .getInitParameter("webApplication");
        if (applicationClassName == null) {  // Using defaults.
            applicationClass = WebApplication.class;
        } else {
            Class<?> clazz;
            try {
                clazz = getClassLoader().loadClass(applicationClassName);
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
            getServletContext().setAttribute(WEBAPPLICATION_CONTEXT_ATTRIBUTE_NAME, webApplication);
            webApplication.setServletContext(getServletContext());
        }
    }
    
    /** I'd prefer to do that in a Filter, but it would be against the Vaadin current architecture 
     * Note that Vaadin TransactionListeners have no access to the ServletContext => we cannot use TransactionListeners. */
    @SuppressWarnings("unchecked")
    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        WebApplication.currentServletContext.set(getServletContext());
        super.service(request, response);
        WebApplication.currentServletContext.remove();
    }
    
}
