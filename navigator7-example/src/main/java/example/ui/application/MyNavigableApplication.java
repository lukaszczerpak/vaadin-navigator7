package example.ui.application;

import org.vaadin.navigator7.NavigableApplication;
import org.vaadin.navigator7.WebApplication;
import org.vaadin.navigator7.window.NavigableAppLevelWindow;

import example.ui.page.DashboardPage;
import example.ui.page.EditorPage;
import example.ui.page.ParamTestPage;
import example.ui.page.TicketPage;

/**
 * In your NavigableApplication class, you define which Window to create (MyAppLevelWindow).
 * 
 * @author John Rizzo - BlackBeltFactory.com
 */
public class MyNavigableApplication extends NavigableApplication {

//    @Override
//    public void init() { ....
//      We don't override the init() method because we don't create the Window instance ourselves (the Navigator does that; and what is a Window by the way, don't wanna know about that notion: we have pages).
    
    public MyNavigableApplication() {
        setTheme("navigator7");
    }

    @Override
    public NavigableAppLevelWindow createNewNavigableAppLevelWindow() {
        return new MyAppLevelWindow();
    }
    
}
