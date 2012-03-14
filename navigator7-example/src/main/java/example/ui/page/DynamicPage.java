package example.ui.page;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.navigator7.Page;

import java.util.Date;

/**
 * @author lukes
 */
@Page(uriName = "dynamicPage")
public class DynamicPage
        extends CustomComponent
{
    public DynamicPage()
    {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setSizeFull();
        setCompositionRoot(mainLayout);

        mainLayout.addComponent(new Label("New Dynamic Page, created at" + new Date().toString()));
    }
}
