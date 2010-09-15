package example.ui.application;

import org.vaadin.navigator7.WebApplication;
import org.vaadin.navigator7.interceptor.NavigationWarningInterceptor;
import org.vaadin.navigator7.interceptor.PageChangeListenersInterceptor;

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

    private PageChangeListenersInterceptor pageChangeListenerInterceptor;

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
        registerInterceptor( pageChangeListenerInterceptor = new PageChangeListenersInterceptor() );
        super.registerInterceptors();   // Default interceptors.
    }

    public PageChangeListenersInterceptor getPageChangeListenerInterceptor() {
        return pageChangeListenerInterceptor;
    }
    
    
}
