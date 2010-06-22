package example.ui.page;

import org.vaadin.navigator7.ParamChangeListener;
import org.vaadin.navigator7.ParamPageLink;
import org.vaadin.navigator7.Navigator.NavigationEvent;
import org.vaadin.navigator7.uri.Param;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import example.model.Product;

/** Demo of the EntityUriAnalayzer (MyUriAnalyzer) with @Param (and the ParamInjectInterceptor). */
public class ProductAPage extends VerticalLayout implements ParamChangeListener {

    @Param(pos=0, required=true) Product p;          // "34"
    @Param(pos=1)                String value1;      // "AAAA"
    @Param(pos=2)                String value2;      // "BBBB"
    
    @Param                       String namedValue;  // "namedValue=CCCC"
    
    
    @Override
    public void paramChanged(NavigationEvent navigationEvent) {
        removeAllComponents();
        
        setSpacing(true);
        addComponent( new Label("Demo of the EntityUriAnalyzer with the @Param annotation."));
        addComponent( new Label("This is product " + p.getLabel()));
        addComponent( new Label("The product id is " + p.getId()));
        addComponent( new Label("value1 = "+value1));
        addComponent( new Label("value2 = "+value2));
        addComponent( new Label("namedValue = "+namedValue));
        
        // this is makes more sense from another page...
        addComponent( new ParamPageLink("This is a link to myself.", ProductAPage.class,
                p, "AAAA", "BBBB")
                .addParam("namedValue", "CCCC") );

        // a lot of UI code (biggest part of the page) should follow here.
        // .....
    }
    
}
