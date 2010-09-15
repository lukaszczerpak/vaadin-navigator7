package org.vaadin.navigator7;

import org.vaadin.navigator7.uri.UriAnalyzer;

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
        super(
            /* WebApplication.getCurrent().getUriAnalyzer(). */ // No, because of batch jobs:  http://vaadin.com/forum/-/message_boards/message/216481?_19_delta=10&_19_keywords=&_19_advancedSearch=false&_19_andOperator=true&cur=3#_19_message_216481  
            UriAnalyzer.
                buildFragmentFromPageAndParameters(pageClass, params, true)
        );
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
