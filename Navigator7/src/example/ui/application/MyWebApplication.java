package example.ui.application;

import org.vaadin.navigator7.WebApplication;
import org.vaadin.navigator7.interceptor.NavigationWarningInterceptor;

import example.ui.page.Dashboard;
import example.ui.page.Editor;
import example.ui.page.ParamTestPage;
import example.ui.page.Ticket;

/** 
 * 
 * @author John Rizzo - BlackBeltFactory.com
 *
 */
public class MyWebApplication extends WebApplication {

    public MyWebApplication() {
        // We need to do that in the constructor (and not later), to ensure that the init method in the ancestor has the PageTemplate and the pages.
        registerPages(new Class[] {Dashboard.class, Editor.class, Ticket.class, ParamTestPage.class});
        registerInterceptor( new NavigationWarningInterceptor() );
    }
    
}
