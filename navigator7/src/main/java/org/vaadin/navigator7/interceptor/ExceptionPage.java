package org.vaadin.navigator7.interceptor;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/** Replaces a page that produced an exception when instantiating or initializing.
 * Displays the exception details to the end user. */
public class ExceptionPage extends VerticalLayout {

    public ExceptionPage(Exception exception, Class<? extends Component> pageClass, String params) {
        // Get the stacktrace in a string
        StringWriter buffer = new StringWriter();
        exception.printStackTrace(new PrintWriter(buffer));
        
        // Label on the page.
        Label label = new Label("<h1>Ooops,</h1>it seems that your page request triggered a problem on the server.<br/>" +
        		"Please accept our apologies.<br/><br/>"
                + "page class: <i>" + pageClass + "</i><br/>" 
                + "parameters: <i>" + params + "</i><br/>"
                + "<br/><br/><pre>" + buffer.toString() + "</pre>" 
                , Label.CONTENT_XHTML);
        addComponent(label);
    }
}
