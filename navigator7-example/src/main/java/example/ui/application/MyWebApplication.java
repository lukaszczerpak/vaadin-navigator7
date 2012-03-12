package example.ui.application;

import org.vaadin.navigator7.WebApplication;
import org.vaadin.navigator7.Navigator.NavigationEvent;
import org.vaadin.navigator7.interceptor.NavigationWarningInterceptor;
import org.vaadin.navigator7.interceptor.PageChangeListenersInterceptor;
import org.vaadin.navigator7.interceptor.PageChangeListenersInterceptor.PageChangeListener;

import example.ui.page.DashboardPage;
import example.ui.page.EditorPage;
import example.ui.page.ParamTestPage;
import example.ui.page.ProductAPage;
import example.ui.page.ProductBPage;
import example.ui.page.SeoPage;
import example.ui.page.TicketPage;

/** 
 * 
 * @author John Rizzo - BlackBeltFactory.com
 *
 */
public class MyWebApplication extends WebApplication {


    public MyWebApplication() {
        // We need to do that in the constructor (and not later), to ensure that the init method in the ancestor has the PageTemplate and the pages.
        registerPages(new Class[] {
                DashboardPage.class,
                EditorPage.class,
                TicketPage.class,
                ParamTestPage.class,
                ProductAPage.class,
                ProductBPage.class,
                SeoPage.class,
        });
        setUriAnalyzer( new MyUriAnalyzer() );
    }
    
    @Override
    protected void registerInterceptors() {
        // 1st interceptor to call: check if user really wanna quit.
        registerInterceptor( new NavigationWarningInterceptor() );
        
        // 2nd interceptor: for listening all those who want to be notified of any page change (for all users).
        // In the header of every window, we have a label to update for every change.
        PageChangeListenersInterceptor pclsInterceptor = new PageChangeListenersInterceptor();
        pclsInterceptor.addPageChangeListener( new PageChangeListener() {
            @Override  public void pageChanged(NavigationEvent event) {
                ((MyAppLevelWindow)MyNavigableApplication.getCurrentNavigableAppLevelWindow()).getNavLabel()
                .setValue("PageChangeListener: pageClass = "+ event.getPageClass() +
                        " -- params = " + event.getParams());
                // Note: in a real application, I typically update a google tracker here (attached to MyAppLevelWindow, as our example navLabel is).
            }
        });
        registerInterceptor( pclsInterceptor );

        
        super.registerInterceptors();   // Default interceptors.
    }

    
    
}
