package org.vaadin.navigator7.interceptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.vaadin.navigator7.WebApplication;
import org.vaadin.navigator7.Navigator.NavigationEvent;

import com.vaadin.ui.Component;

/** If the page is a ParamPage, injects parameter values.
 * 
 * @author John Rizzo - BlackBeltFactory.com
 *
 */
public class PageChangeListenersInterceptor implements Interceptor {

    protected List<PageChangeListener> pageChangeListenerList = new ArrayList<PageChangeListener>();

    public void addPageChangeListener(PageChangeListener navL) {
        pageChangeListenerList.add(navL);
    }

    
    @Override
    public void intercept(PageInvocation pageInvocation) {
        pageInvocation.invoke();

        // After invoke, the interceptor chain has been called and the page has been placed.
        if (pageInvocation.isPagePlaced()  // Maybe another interceptor did interrupt the chain. We would not notify if the page had not been actually placed.
                && pageChangeListenerList.size() > 0) {
            // After invoke, the interceptor chain has been called and the page has been placed.
            Component page = pageInvocation.getPageInstance();
            NavigationEvent event = new NavigationEvent(pageInvocation.getNavigator(),
                    WebApplication.getCurrent().getUriAnalyzer(),
                    page.getClass(), pageInvocation.getParams());
            for (PageChangeListener pCL : pageChangeListenerList) {
                pCL.pageChanged(event);
            }
        }
    }
    
    

    
    /** Interface warns your application of NavigationEvents
     * Implemented by those that want to be warned systematically of every page change for that Application.
     *
     * Could be useful, for example to tell a GoogleAnalyticsTracker of uri changes.
     * 
     * @author John Rizzo
     */
    public interface PageChangeListener extends Serializable {
        /** Only for page changes. Not called if only the parameters changed */
        public void pageChanged(NavigationEvent navigationEvent);
    }


}