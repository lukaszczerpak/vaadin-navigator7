package org.vaadin.navigator7.interceptor;

import org.vaadin.navigator7.uri.ParamInjector;

import com.vaadin.ui.Component;

/** If the page is a ParamPage, injects parameter values.
 * 
 * @author John Rizzo - BlackBeltFactory.com
 *
 */
public class ParamInjectInterceptor implements Interceptor {


    @Override
    public void intercept(PageInvocation pageInvocation) {
        if (ParamInjector.containsParamAnnotation( pageInvocation.getPageClass() )) {  
            // We don't call pageInvocation.getPageInstance() before we are sure it contains @Param fields,
            // because getPageInstance() will probably trigger page instantiation, and we only do it if necessary. 
            Component page = pageInvocation.getPageInstance();
            if (ParamInjector.verifyAndInjectParams( page, pageInvocation.getParams() )) {
                pageInvocation.invoke();
            } // else we stop page invocation chain because of bad parameters (notification shown to user already).
        } else {  // Not ParamPage => we do nothing special
            pageInvocation.invoke();
        }
    }
}