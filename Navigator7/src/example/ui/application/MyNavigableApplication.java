package example.ui.application;

import org.vaadin.navigator7.NavigableApplication;
import org.vaadin.navigator7.window.NavigableAppLevelWindow;

import example.ui.page.Dashboard;
import example.ui.page.Editor;
import example.ui.page.ParamTestPage;
import example.ui.page.Ticket;

/**
 * In your NavigableApplication class, you define the list of pages,
 * and your descendent of NavigableAppLevelWindow that the application must instantiate.
 * 
 * @author John Rizzo - BlackBeltFactory.com
 */
public class MyNavigableApplication extends NavigableApplication {

//    @Override
//    public void init() { ....
//      We don't override the init() method because we don't create the Window instance ourselves (the Navigator does that; and what is a Window by the way, don't wanna know about that notion: we have pages).
    
    public MyNavigableApplication() {
        // We need to do that in the constructor (and not later), to ensure that the init method in the ancestor has the PageTemplate and the pages.
        registerPages(new Class[] {Dashboard.class, Editor.class, Ticket.class, ParamTestPage.class});
        setTheme("navigator7");
    }

    @Override
    public NavigableAppLevelWindow createNewNavigableAppLevelWindow() {
        return new MyAppLevelWindow();
    }
    
}
