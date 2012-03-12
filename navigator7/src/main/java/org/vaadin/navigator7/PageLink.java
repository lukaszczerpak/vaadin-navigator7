package org.vaadin.navigator7;


import com.vaadin.ui.Component;
import com.vaadin.ui.Link;

public class PageLink extends Link {
    
    public PageLink(String caption, Class<? extends Component> pageClass) {
        super(caption, new PageResource(pageClass));
    }

    public PageLink(String caption, Class<? extends Component> pageClass, String param) {
        super(caption, new PageResource(pageClass, param));
    }

    /** For descendants to have a flexible constructor super to call */
    protected PageLink() {
        super();
    }

//    public PageLink(String caption, Class<? extends Component> pageClass, UriParam uriParam) {
//        super(caption, new PageResource(pageClass, uriParam));
//    }
//     
}