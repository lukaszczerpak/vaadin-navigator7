package org.vaadin.navigator7.uri;

import java.util.HashMap;
import java.util.Map;

import org.vaadin.navigator7.PageResource;
import org.vaadin.navigator7.WebApplication;

import com.vaadin.ui.Component;

/** Resource for page using @Param annotations */
public class ParamPageResource extends PageResource {
    
    protected Object[] posParams;
    protected Map<String, Object> namedParams = new HashMap<String,Object>();
    
    public ParamPageResource(Class<? extends Component> pageClass, Object ... posParams) {
        super(pageClass);
        this.posParams = posParams;
    }
    
    /** getURL() is called when the Link is being rendered (=> all the params should be there, and we can throw exceptions if something is not consistend with the @Param annotations) */
    @Override
    public String getURL(){
        return WebApplication.getCurrent().getUriAnalyzer()
            .buildFragmentFromPageAndParameters(pageClass, getParams(), true);
    }
    
    @Override
    public String getParams() {
        return ParamInjector.generateFragment(pageClass, posParams, namedParams);   
    }

    public synchronized  ParamPageResource addParam(String name, Object value) {
        if (namedParams.get(name) != null) {
            throw new RuntimeException("Trying to add named param that has already been added (same name = '"+name+"')");
        }
        namedParams.put(name, value);
        return this;
    }
}
