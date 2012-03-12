package org.vaadin.navigator7.uri;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Applied to a field of an UriParam descendant.
 * @See UriParam for an example of UriParam descendant.
 * 
 * If URI fragment = "myField=1111/BBBB"
 * 
 * REQUIRED -------------------
 * By default, values are not required. Except primitive types.
 * A primitive type must be required (and is required by default) because when UriParam builds an URI fragment to create a link,
 * it takes the values of your fields. If a field is null, it provides no value (but the field must not be required).
 * If the field is a primitive, you cannot provide a null value => there is always a value => we say that the field is always requiered (because primitive).
 * => use Wrapper types (Integer, Long, Float, Boolean,...) for optional values.
 * @Param
 * long myField;  // not required. If no parameter value provided, it will keep its initial value (0 in this case)
 * 
 * @Param(required=false)   // Don't do that. Primitives are always required. Unfortunately we cannot detect this case and throw an exception (because we cannot know if the required attribute is false because of a default or a provided value).   
 * long myField;            
 * 
 * @Param
 * String myField;    // Required (by default because it's not a primitive type).
 * 
 * NAME ------------------------
 * By default, the name = the name of the field.
 * @Param(name="myField")   // Useless 'name' attribute.
 * long myField;
 * 
 * @Param         // Same as @Param(name="myField");
 * long myField;   
 * 
 * @Param(pos=0)  // Different: position based retrieval, no name expected in url.
 * long myField;
 * 
 * 
 * POSITION -------------------
 * The second parameter in the example "myField=1111/BBBB" has no name. It is in position 1 (1st = 0)
 * @Param(pos=1)
 * String mySecondField;
 * 
 * If you use a position for a named field, it will contain the name and the value.
 * @Param(pos=0)
 * String myFirstField;   // Will contain "myField=1111" (and not just "1111", because this is no named field but a positional field)
 * 
 * @Param(pos=0)
 * int myFirstField;      // Will show a conversion message to the user ("myField=1111" cannot be converted into an int (while "1111" well)).
 * 
 * 
 * CLEAN-UP -------------------
 * Fields having the @Param annotation are cleaned up (assigned to null) when the URI changes if no value is provided.
 * When the user goes to a page and it is instantiated, no clean-up happen, you can safely assign default values to your parameters in the constructor.
 * After that, when the user on that page changes the parameters in the URL (by clicking a link or manually),
 * ParamChangeListenerInterceptor calls your page .paramChanged() method.
 * Before paramChanged() is called, the ParamInjectInterceptor will set values to your @Param fields from the strings of the URI.
 * If, for a field, no value is given, then cleanup occurs (that field is assigned to null), so you can make the difference from having that value being provided in the URI.
 * 
 * 
 * 
 * @author John Rizzo - BlackBeltFactory.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Param {
    boolean required() default false;
    int pos() default -1;  // -1 means that we should use name() or use the name of the field.
    String name() default "";
}
