package example.ui.application;

import java.util.Collection;

import org.vaadin.navigator7.Navigator.NavigationEvent;
import org.vaadin.navigator7.Navigator.NavigationListener;
import org.vaadin.navigator7.window.HeaderFooterFixedAppLevelWindow;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.MenuBar.MenuItem;

/** Defines the template (header/footer/...) of our application level windows
 * 
 * Demo of: Header/Footer templating. Note the subtle overriding of createComponents to refine the layout apprearance.
 *          NavigationListener to get any page transition event.
 *          Navigator.navigateTo()
 * 
 * @author John Rizzo - BlackBeltFactory.com
 */
public class MyAppLevelWindow extends HeaderFooterFixedAppLevelWindow {

    @Override
    protected Component createHeader() {
        VerticalLayout header = new VerticalLayout();
        header.addStyleName("header");  // Application specific style.
        header.setWidth("100%");
        header.setHeight("100px");
        
        ////// Hello
        Label l = new Label("Hello, I'm a HEADER with a menu.");
        l.setWidth(null);
        header.addComponent(l);
        header.setComponentAlignment(l, Alignment.TOP_RIGHT);

        ///// NavigationListener label
        final Label navLabel = new Label();
        navLabel.setWidth(null);
        header.addComponent(navLabel);
        header.setComponentAlignment(navLabel, Alignment.TOP_RIGHT);
        getNavigator().addNavigationListener( new NavigationListener() {
            @Override  public void pageChanged(NavigationEvent event) {
                navLabel.setValue("NavigationListener: pageClass = "+ event.getPageClass() +
                                                  " -- params = " + event.getParams());
            }
        });

        ///// Menu
        MenuBar menuBar = new MenuBar();
        menuBar.setWidth("100%");
        header.addComponent(menuBar);
        header.setComponentAlignment(menuBar, Alignment.BOTTOM_LEFT);
        
        // Create one menu item with each page of the application
        // this is little bit artificial in the example. In a business application, you manually select (name) the pages to put in the menu, instead of having a loop.
        // something like: 
        //        menuBar.addItem("Manage Your Tickets", new MenuBar.Command() {
        //            public void menuSelected(MenuItem selectedItem) {
        //                getNavigator().navigateTo(Ticket.class);
        //            }
        //        });
        Collection<Class <? extends Component>> pageClassColl = MyNavigableApplication.getCurrent().getNavigatorConfig().getPagesClass();
        for (final Class<? extends Component> pageClass : pageClassColl) {
            menuBar.addItem(pageClass.getSimpleName(), new MenuBar.Command() {
                public void menuSelected(MenuItem selectedItem) {
                    getNavigator().navigateTo(pageClass);
                }
            });
        }

        return header;
    }

    @Override
    protected Component createFooter() {
        VerticalLayout vLayout = new VerticalLayout();
        vLayout.setWidth("100%");
        
        Label ll = new Label("Hello, I'm a footer!");
        ll.setWidth(null);
        vLayout.addComponent(ll);
        vLayout.setComponentAlignment(ll, Alignment.TOP_CENTER);
        
        
        Label l = new Label("Developped by John Rizzo in 2010 for Vaadin.");
        l.setWidth(null);
        vLayout.addComponent(l);
        vLayout.setComponentAlignment(l, Alignment.BOTTOM_CENTER);
        vLayout.setHeight("200px");
        return vLayout; 
    }

    @Override
    protected ComponentContainer createComponents() {
        ComponentContainer result = super.createComponents();
        this.getFooterBand().addStyleName("footer");   // We apply the footer to the whole outer band, not only to the fixed width inner band.
        return result;
    }

}
