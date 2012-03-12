package org.vaadin.navigator7;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Applied to a Component that plays the role of a page in a Vaadin NavigableApplication. 
 * @author John Rizzo - BlackBeltFactory.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Page {
    /** By default the name of the page in the uri is the class name without the "Page" postfix.
     * If a class name is ProductPage.class, the name used in the uri will be "Product".
     * If you don't like that (i.e. if you want a tiny uri with as "p" as page name) you can set the page name here. */
    String uriName() default "";
    
    /** Crawlable pages are browsed by search engines as google bot.
     * Browser don't take into account the URI (what's after the #), except if it starts with a "!"
     * If you want that a "!" is inserted in front of the page name in links produced by the UriAnalyzer,
     * this attribute must be true.
     * Specify true for public pages that should be analyzed by search engines.
     * 
     * http://code.google.com/web/ajaxcrawling/
     *  */
    boolean crawlable() default false;
}
