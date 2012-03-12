package example.ui.page;

import org.vaadin.navigator7.Page;
import org.vaadin.navigator7.PageResource;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * Demo of @Page, 
 *         home page concept
 *         PageResource link
 * 
 * @author John Rizzo - BlackBeltFactory.com
 */
@Page(uriName="dash")
@SuppressWarnings("serial")
public class DashboardPage extends CustomComponent {

    GridLayout gl = new GridLayout(3, 3);

    public DashboardPage() {
//        setSizeFull();    Non sense in a fixed web design (FixedPageTemplagte).
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setSizeFull();
        setCompositionRoot(mainLayout);

        mainLayout.addComponent( new Label("Note the uri: '#dash' instead of 'Dashboard' (page class name)." +
        		" This is due to the @Page(uriName=\"dash\") annotation on the Dashboard class." ));

        mainLayout.addComponent( new Label("Dashboard is also the default home page. Try to remove the URI from the URL, e.g. http://localhost:8080/Navigator7 instead of http://localhost:8080/Navigator7#dash" ));

        VerticalLayout vLayout = new VerticalLayout();
        vLayout.addComponent(new Label("This home page is displayed (with a notification if you type a wrong URL. Try this:"));
        PageResource pageResource = new PageResource(TicketPage.class, "ABC");
        vLayout.addComponent(new Link(pageResource.getURL(), pageResource));
        String wrongUrl = "#NonExistingPage";
        vLayout.addComponent(new Link(wrongUrl, new ExternalResource(wrongUrl)));
        mainLayout.addComponent( vLayout );

        mainLayout.addComponent(gl);
        gl.setSizeFull();
        gl.setSpacing(true);
        for (int i = 0; i < 9; i++) {
            Panel p = new Panel("Board " + i);
            gl.addComponent(p);
            p.setSizeFull();
        }
    }

}
