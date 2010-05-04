package org.vaadin.navigator7;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Component;

/**
 * 
 * @author John Rizzo - BlackBeltFactory.com
 */
public class PageResource extends ExternalResource{

    public PageResource(Class<? extends Component> pageClass) {
        this(pageClass, null);
    }

    public PageResource(Class<? extends Component> pageClass, String params) {
        super(NavigableApplication.getCurrent().getUriAnalyzer().buildFragmentFromPageAndParameters(pageClass, params, true) );
        // For example  "#Auction/123456"
    }

}
