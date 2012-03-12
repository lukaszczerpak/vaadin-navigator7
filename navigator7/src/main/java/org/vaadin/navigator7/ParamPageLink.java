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
    // TODO: improve this method to make the name String optional. If only one field of the page
    // can get the parameter according to it's type, there is no ambiguity and the name is not needed for the named field.
    // If there is an ambiguity, throw a RuntimeException that asks to use the addParam method with the explicit name given.
    // When that is implemented, the constructor should also accept named parameters (with no explicit name given).
    public ParamPageLink addParam(String name, Object value) {
        paramPageResource.addParam(name, value);
        return this;
    }
}