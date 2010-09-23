package org.vaadin.navigator7.uri;

import java.util.SortedMap;
import java.util.TreeMap;

import org.vaadin.navigator7.PageResource;
import org.vaadin.navigator7.WebApplication;

import com.vaadin.ui.Component;

/** Resource for page using @Param annotations */
public class ParamPageResource extends PageResource {
    
    protected Object[] posParams;
    protected SortedMap<String, Object> namedParams = new TreeMap<String,Object>();
    
    public ParamPageResource(Class<? extends Component> pageClass, Object ... posParams) {
        super(pageClass);
        this.posParams = posParams;
    }
    
    /** getURL() is called when the Link is being rendered (=> all the params should be there, and we can throw exceptions if something is not consistent with the @Param annotations)
     * Contrary to its ancestor, not all the params are ready in the constructor, because of the addParam() method. */
    @Override
    public String getURL(){
        return WebApplication.getCurrent().getUriAnalyzer().
            buildFragmentFromPageAndParameters(pageClass, getParams(), true);
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
