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

        WebApplication.init(servletConfig, getServletContext(), getClassLoader());
    }
    
    /** I'd prefer to do that in a Filter, but it would be against the Vaadin current architecture 
     * Note that Vaadin TransactionListeners have no access to the ServletContext => we cannot use TransactionListeners. */
    @SuppressWarnings("unchecked")
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        WebApplication.beforeService(request, response, getServletContext());
        super.service(request, response);
        WebApplication.afterService(request, response, getServletContext());
    }
    
}
