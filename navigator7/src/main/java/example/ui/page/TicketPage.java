package example.ui.page;

import org.vaadin.navigator7.Page;
import org.vaadin.navigator7.ParamChangeListener;
import org.vaadin.navigator7.Navigator.NavigationEvent;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import example.ui.application.MyNavigableApplication;


/**
 * Demo of ParamChangeListener
 *         Navigator.setUriParam()
 *         Navigator.reloadCurrentPage()
 *         Navigator.navigateTo()
 * 
 * @author John Rizzo - BlackBeltFactory.com
 */
@Page
public class TicketPage extends CustomComponent implements ParamChangeListener  {

    VerticalLayout layout = new VerticalLayout();
    TextField tf = new TextField("Ticket number");
    Label lastParamsDetectedLabel = new Label();
    Button navigateTo = new Button("Reload Page (navigateTo) to ticket in field");
    Button reload = new Button("Reload Page to same ticket (from URI)");
    Button updateUri = new Button("Just update the URI");
    
    
    Panel details = new Panel();


    @SuppressWarnings("serial")
    public TicketPage() {
        setCompositionRoot(layout);
        layout.setSpacing(true);
        layout.addComponent(tf);
        layout.addComponent(details);
        layout.addComponent(lastParamsDetectedLabel);

        layout.addComponent(layoutButtonAndText(
            new Label("Click this button to reload (rebuild, which can be long) the page with ticket number in the field."),
            navigateTo));
        navigateTo.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                MyNavigableApplication
                   .getCurrentNavigableAppLevelWindow()
                   .getNavigator()
                   .navigateTo(TicketPage.class, tf.toString());
            }
        });

        layout.addComponent(layoutButtonAndText(
                new Label("Click this button to reload (rebuild, which can be long) the page with ticket number already in the URI."),
                reload));
        reload.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                MyNavigableApplication
                .getCurrentNavigableAppLevelWindow()
                .getNavigator()
                .reloadCurrentPage();
            }
        });

        
        layout.addComponent(layoutButtonAndText(
                new Label("Click this button to just set the URI with the correct value (for bookmarking)."),
                updateUri));
        updateUri.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                MyNavigableApplication
                .getCurrentNavigableAppLevelWindow()
                .getNavigator()
                .setUriParams(tf.toString());
            }
        });

    }

    
    protected Layout layoutButtonAndText(Label label, Button button) {
        Layout layout = new VerticalLayout();
        layout.addComponent(label);
        layout.addComponent(button);
        return layout;
    }
    

    /** Called if:
     * URL changed manually by the visitor in the browser.
     * Reload button pressed.
     * NOT called because of an updateUri button click (important ;-).
     */
    @Override
    public void paramChanged(NavigationEvent navigationEvent) {
        lastParamsDetectedLabel.setValue("Last parameters detected: " + navigationEvent.getParams());
        if (navigationEvent.getParams() == null) {
            tf.setValue("");
            details.setVisible(false);
        } else {
            tf.setValue(navigationEvent.getParams());
            details.setVisible(true);
            details.setCaption("Ticket #" + navigationEvent.getParams());
        }      
    }




}
