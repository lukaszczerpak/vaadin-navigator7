package org.vaadin.navigator7;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Component;

/**
 * 
 * @author John Rizzo - BlackBeltFactory.com
 */
public class PageResource extends ExternalResource{

    protected String params;
    
    // Descendants (ParamPageResource) need it.
    protected Class<? extends Component> pageClass;
    
    // Because Java does not support constructor inheritance.
    public PageResource(String url) {
        super(url);
    }
    
    public PageResource(Class<? extends Component> pageClass) {
        this(pageClass, (String)null);
    }

    public PageResource(Class<? extends Component> pageClass, String params) {
        super(WebApplication.getCurrent().getUriAnalyzer().buildFragmentFromPageAndParameters(pageClass, params, true) );
        // For example  "#Auction/123456"
        this.pageClass = pageClass;
        this.params = params;
    }


    public String getParams() {
        return params;
    }

    public Class<? extends Component> getPageClass() {
        return pageClass;
    }
    
    

//    public PageResource(Class<? extends Component> pageClass, UriParam uriParam) {
//        super(WebApplication.getCurrent().getUriAnalyzer()
//                .buildFragmentFromPageAndParameters(pageClass, uriParam.generateFragment(), true) );
//        // For example  "#Auction/123456"
//        this.pageClass = pageClass;
//    }

}
