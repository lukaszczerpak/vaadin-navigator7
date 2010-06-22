package example.ui.page;

import org.vaadin.navigator7.PageLink;
import org.vaadin.navigator7.ParamChangeListener;
import org.vaadin.navigator7.WebApplication;
import org.vaadin.navigator7.Navigator.NavigationEvent;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import example.model.Product;
import example.ui.application.MyUriAnalyzer;

/** Demo of the EntityUriAnalayzer (MyUriAnalyzer) but with no @Param annotation. We use the ParamChangeListener instead */
public class ProductBPage extends VerticalLayout implements ParamChangeListener {

    
    @Override
    public void paramChanged(NavigationEvent navigationEvent) {
        // Analyzses the parameters and retreives the product.
        MyUriAnalyzer uriAnalyzer = (MyUriAnalyzer)WebApplication.getCurrent().getUriAnalyzer();
        Product p = (Product) uriAnalyzer.getMandatoryEntity(navigationEvent.getParams(), 0, Product.class);
        if (p == null) { 
            return;  // User has already been visually notified of the missing parameter.
        }

        
        removeAllComponents();
        
        setSpacing(true);
        addComponent( new Label("Demo of the EntityUriAnalyzer, but without the UriParam mechanism (simple ParamChangeListener instead)."));
        addComponent( new Label("This is product " + p.getLabel()));
        addComponent( new Label("The product id is " + p.getId()));
        
        // this is makes more sense from another page...
        addComponent( new PageLink("This is a link to myself.", ProductBPage.class, p.getId().toString()) );

        // a lot of UI code (biggest part of the page) should follow here.
        // .....
    }
    



    
    
}
