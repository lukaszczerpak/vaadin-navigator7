package org.vaadin.navigator7.interceptor;

import java.util.List;

import org.vaadin.navigator7.Navigator;
import org.vaadin.navigator7.WebApplication;

import com.vaadin.ui.Component;

/** Context of execution for the interceptors chain, then for the page.
 * In your interceptor, you typically do something, then call pageInvocation.invoke(), then maybe something else.
 * You may also choose to change the destination page (PageInvocation.setPageClass()) or params.
 * From your interceptor, you are not obliged to call invoke. If you have a good reason not to move to the target page, you may, for example, display a notification, and just don't call invoke (stay on the current page).
 * 
 * @author John Rizzo - BlackBeltFactory.com
 *
 */
public class PageInvocation {
    
    protected int currentInterceptorIndex = -1;  // When invoke is called for the first time, it's not from within an, interceptor (=> there is no current interceptor yet).
    protected Navigator navigator;
    protected Class<? extends Component> pageClass;
    protected String params;
    
    /** true => we'll set the URI (with the page name and params) when invoking the page.
     * when the page change results from an URI change event, we don't want to rechange the URI.
     * If an interceptor decides to change the pageClass and/or params, it may change needToChangeUri to true to have a correct URI in the browser.
     */
    protected boolean needToChangeUri; 
    
    
    
    public PageInvocation(Navigator navigator,
            Class<? extends Component> pageClass, String params,
            boolean needToChangeUri) {
        super();
        this.navigator = navigator;
        this.pageClass = pageClass;
        this.params = params;
        this.needToChangeUri = needToChangeUri;
    }


    /** Invokes the next step in processing this PageInvocation
     * Call it if you want to go further to navigating to the page (eventually through next interceptors) */
    public void invoke() {
        // Selects next Interceptor
        List<Interceptor> interceptors = WebApplication.getCurrent().getNavigatorConfig().getInterceptorList();
        if (currentInterceptorIndex+1 < interceptors.size()) {  // There is one more interceptor to go through.
            currentInterceptorIndex++;
            Interceptor nextInterceptor = interceptors.get(currentInterceptorIndex);
            nextInterceptor.intercept(this);
        } else {  // No more interceptor: go to the page.
            navigator.instantiateAndPlacePage(pageClass, params, needToChangeUri);
        }
    }

    public Class<? extends Component> getPageClass() {
        return pageClass;
    }

    /** An interceptor may change the destination page (set an error page, for example) */
    public void setPageClass(Class<? extends Component> pageClass) {
        this.pageClass = pageClass;
    }

    public String getParams() {
        return params;
    }

    /** An interceptor may change the params that will be used to instantiate the page */
    public void setParams(String params) {
        this.params = params;
    }

    public Navigator getNavigator() {
        return navigator;
    }

    public boolean isNeedToChangeUri() {
        return needToChangeUri;
    }

    public void setNeedToChangeUri(boolean needToChangeUri) {
        this.needToChangeUri = needToChangeUri;
    }


}
