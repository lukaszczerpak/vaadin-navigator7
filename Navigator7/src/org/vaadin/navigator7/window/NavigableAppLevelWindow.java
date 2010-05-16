package org.vaadin.navigator7.window;

import org.vaadin.navigator7.NavigableApplication;
import org.vaadin.navigator7.Navigator;
import org.vaadin.navigator7.WebApplication;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Layout;

/** AppLevelWindow that owns a Navigator and a Page.
 * The Navigator changes the current page of this Window when appropriate.
 * 
 * @author John Rizzo - BlackBeltFactory.com
 *
 */
public abstract class NavigableAppLevelWindow extends AppLevelWindow {

    private Navigator navigator;
    
    protected Component page;  // Current page being displayed. null if no page set yet.
    public ComponentContainer pageContainer;  // Contains page (there could be no page yet, so we cannot rely on this.page.getParent() because this.page could be null. Instantiated by descendants.
    

    @Override
    public void attach() {
        // Main layout creation. Do that before you add anything to the Window.
        Layout main = new CssLayout(); 
        main.addStyleName("PageTemplate-mainLayout");
//        main.setWidth("100%");   If you do that instead of the addStyleName (containing a width:100%;) above, Vaadin JavaScript will recompute the width everytime you resize the browser.
        this.setContent(main);    

        // Must be done after calling this.setConent(main), as for any component added to the window.
        this.navigator = new Navigator();
        this.addComponent(navigator);

        pageContainer = createComponents();  // Let descendants add components in this.getContent().
        pageContainer.addStyleName("FixedPageTemplate-bandOuterLayoutPage");
    }

    
    /** Should fill the window with template components (as a header, footer,...) including a placeholder for the current page.
     * @return the container (probably a Layout) of the page, where the pages should be created and replaced as the user navigates. */
    protected abstract ComponentContainer createComponents();
    
    /** Sets the page in the window, at the right place (and removes the previous one) */
    synchronized public void changePage(Component pageParam) {
        pageContainer.removeAllComponents();  // It is supposed to contain only the previous page (which we don't know the class at all, except it's a Component).
        this.page = pageParam;
        pageContainer.addComponent(page);
    }

    
    
    
    public NavigableApplication getNavigableApplication() {
        return (NavigableApplication)getApplication();
    }

    public Navigator getNavigator() {
        return navigator;
    }

    /** Could return null if no page set yet (for example, during construction) */
    public Component getPage() {
        return page;
    }


    public ComponentContainer getPageContainer() {
        // Defensive coding.
        if (pageContainer == null) {
            throw new IllegalStateException("bug: A page container must be set, by a descendant extending this class (probably in the overriden createComponents() method.");
        }
 
        return pageContainer;
    }



    
    
    
}
