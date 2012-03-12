package example.ui.page;

import org.vaadin.navigator7.Page;
import org.vaadin.navigator7.interceptor.NavigationWarningInterceptor.NavigationWarner;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

/**
 * Demo of NavigationWarner.
 * 
 * @author John Rizzo - BlackBeltFactory.com
 */
@SuppressWarnings("serial")
@Page(uriName="E-D-I-T-O-R")  // Useless but funny. Will appear in the URI.
public class EditorPage extends CustomComponent implements NavigationWarner {

    TextField tf = new TextField();
    VerticalLayout lo = new VerticalLayout();
    Button save = new Button("Save");
    boolean saved = true;

    public String getWarningForNavigatingFrom() {
        return saved ? null : "The text you are editing has not been saved.";
    }

    public EditorPage() {
//        setSizeFull();   // Non sense in our example of FixedPageTemplate.
        lo.addComponent( new Label("Type some text and try to go to another page (menu or URL typing) before saving.") );
        
        lo.addComponent(tf);
        tf.setRows(10);
        tf.setSizeFull();
        tf.setImmediate(true);
        tf.setRows(20);
        lo.setSizeFull();
        lo.setExpandRatio(tf, 1.0F);
        lo.addComponent(save);
        lo.setSpacing(true);
        lo.setComponentAlignment(save, "r");
        setCompositionRoot(lo);
        save.addListener(new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                saved = true;
            }
        });
        tf.addListener(new Property.ValueChangeListener() {

            public void valueChange(ValueChangeEvent event) {
                saved = false;
            }
        });
    }

}
