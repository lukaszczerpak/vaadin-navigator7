package example.ui.application;

import org.vaadin.navigator7.Navigator.NavigationEvent;
import org.vaadin.navigator7.interceptor.PageChangeListenersInterceptor.PageChangeListener;
import org.vaadin.navigator7.uri.ParamPageResource;
import org.vaadin.navigator7.window.HeaderFooterFixedAppLevelWindow;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.MenuBar.MenuItem;

import example.ui.page.DashboardPage;
import example.ui.page.EditorPage;
import example.ui.page.ParamTestPage;
import example.ui.page.ProductAPage;
import example.ui.page.ProductBPage;
import example.ui.page.SeoPage;
import example.ui.page.TicketPage;

/** Defines the template (header/footer/...) of our application level windows
 * 
 * Demo of: Header/Footer templating. Note the subtle overriding of createComponents to refine the layout appearance.
 *          NavigationListener to get any page transition event.
 *          Navigator.navigateTo()
 * 
 * @author John Rizzo - BlackBeltFactory.com
 */
public class MyAppLevelWindow extends HeaderFooterFixedAppLevelWindow {

    Label navLabel;  // Label showing global navigation events (useless, just for the demo). 
    
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
        navLabel = new Label();
        navLabel.setWidth(null);
        header.addComponent(navLabel);
        header.setComponentAlignment(navLabel, Alignment.TOP_RIGHT);
        
        ///// The (commented) code below is a very tricky huge memory leak: 
        // do not accumulate in the global interceptor, references to inner classes (that you never remove from the list of listeners)
        // that (the inner classes) point to their outer class (the Window, with all the UI widgets in it) which cannot be garbage collected.
        // The correct version of this is in MyWebApplication.
//        ((MyWebApplication)MyWebApplication.getCurrent())
//            .getPageChangeListenerInterceptor()
//            .addPageChangeListener( new PageChangeListener() {
//            @Override  public void pageChanged(NavigationEvent event) {
//                navLabel.setValue("PageChangeListener: pageClass = "+ event.getPageClass() +
//                                                  " -- params = " + event.getParams());
//            }
//        });

        ///// Menu
        // Design note: this example is bad: 
        // the menu bar should be real <a href="..."> links to each page.
        // Here, on the contrary, when user click, we go back to the server and execute the MenuBar.Command inner class.
        // It internally forwards to the page.
        // But the link does not appears in the browser.
        // Vaadin should propose a MenuBar that also accepts Links as items (instead of Command classes).
        
        
        MenuBar menuBar = new MenuBar();
        menuBar.setWidth("100%");
        header.addComponent(menuBar);
        header.setComponentAlignment(menuBar, Alignment.BOTTOM_LEFT);
        

        menuBar.addItem("DashBoard", new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                getNavigator().navigateTo(DashboardPage.class);
            }
        });
        menuBar.addItem("Manage Your Tickets", new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                getNavigator().navigateTo(TicketPage.class);
            }
        });
        menuBar.addItem("Editor", new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                getNavigator().navigateTo(EditorPage.class);
            }
        });
        menuBar.addItem("Product A 34", new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                // If I had a product in a variable p, I'd have written:
//                getNavigator().navigateTo(new ParamPageResource(ProductAPage.class, p)); 
                getNavigator().navigateTo(ProductAPage.class, "34"); 
            }
        });
        menuBar.addItem("Product B 34", new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                // If I had a product in a variable p, I'd have written:
                // getNavigator().navigateTo(Product.class, p.getId().toString());
                getNavigator().navigateTo(ProductBPage.class, "34"); 
            }
        });
        menuBar.addItem("ParamTestPage", new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                getNavigator().navigateTo(
                    new ParamPageResource(ParamTestPage.class, "Albator-Forever")
                        .addParam("ssn", "xxx.xxx.xxx"));
            }
        });
        menuBar.addItem("SeoPage", new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                getNavigator().navigateTo(SeoPage.class);
            }
        });

        
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

    public Label getNavLabel() {
        return navLabel;
    }

}
