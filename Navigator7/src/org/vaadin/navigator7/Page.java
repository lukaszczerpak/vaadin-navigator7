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
    String uriName() default "";
}
