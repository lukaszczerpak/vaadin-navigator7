package org.vaadin.navigator7;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Component;

/**
 * 
 * @author John Rizzo - BlackBeltFactory.com
 */
public class PageResource extends ExternalResource{

    // Because Java does not support constructor inheritance.
    public PageResource(String url) {
        super(url);
    }
    
    public PageResource(Class<? extends Component> pageClass) {
        this(pageClass, null);
    }

    public PageResource(Class<? extends Component> pageClass, String params) {
        super(WebApplication.getCurrent().getUriAnalyzer().buildFragmentFromPageAndParameters(pageClass, params, true) );
        // For example  "#Auction/123456"
    }

}
