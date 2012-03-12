package org.vaadin.navigator7.interceptor;


/** 
 * An interceptor is a stateless class that follows the interceptor pattern, as found in Filter and in AOP languages.
 * Interceptors are objects that dynamically intercept Page invocations. They provide the developer with the opportunity to define code that can be executed before and/or after the execution of an action. They also have the ability to prevent a page from being invoked. 
 * Interceptors provide developers a way to encapsulate common functionality in a re-usable form that can be applied to one or more Pages.
 * Interceptors must be stateless and not assume that a new instance will be created for each request or Page. 
 * Interceptors may do some processing before and/or after delegating the rest of the processing using PageInvocation.invoke().
 * 
 * Navigator7 Interceptor differs from Struts 2 Interceptors and Servlet Filters the following ways:
 * - There is one Navigator7 Interceptor invocation for each NavigationEvent (=> once when we move to the page and instanciate it), while Filters and Struts 2 Interceptors are called for every request (included when you click a button on a page).
 * - Navigator7 Interceptors are not configured into an external xml file, and you cannot define a set of pages for which they should be applied and others for which they should not be applied. Just program it inside the interceptor if you need to activate it for specific pages. This should probably change if we want to use reusable interceptors written by others only for specific pages, but it's not the case yet. Let's keep it simple for now. 
 *
 * To have an idea of what interceptors can be used for, see Struts 2 documentation (while some examples are meaningless in a Vaadin context): 
 * http://struts.apache.org/2.x/docs/interceptors.html
 * 
 * A typical usage would be security: the interceptor can check the logged-in user in the session, and can check annotations on your Page classes, and forward to an error page in case of mismatch.
 * 
 * Register your interceptor in your class extending WebApplication, by calling WebApplication.registerInterceptor().
 * 
 * 
 * @author John Rizzo - BlackBeltFactory.com
 *
 */
public interface Interceptor {
    public void intercept(PageInvocation pageInvocation);
}
