package org.vaadin.navigator7;


import org.vaadin.navigator7.uri.ParamPageResource;

import com.vaadin.ui.Component;

public class ParamPageLink extends PageLink {
    
    protected ParamPageResource paramPageResource;
    
    /** verifies the parameters according to the @Param annotations 
     * 
     *@param posParams positional params in the correct order.
     *
     **/ 
    public ParamPageLink(String caption, Class<? extends Component> pageClass, Object ... posParams) {
        super();
        setCaption(caption);
        paramPageResource = new ParamPageResource(pageClass, posParams);
        setResource(paramPageResource);
    }

    /** Add a named param */
    public ParamPageLink addParam(String name, Object value) {
        paramPageResource.addParam(name, value);
        return this;
    }
}