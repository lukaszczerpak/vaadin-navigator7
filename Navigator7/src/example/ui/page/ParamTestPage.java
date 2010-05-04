package example.ui.page;

import org.vaadin.navigator7.NavigableApplication;
import org.vaadin.navigator7.PageResource;
import org.vaadin.navigator7.ParamUriAnalyzer;
import org.vaadin.navigator7.Navigator.NavigationEvent;
import org.vaadin.navigator7.Navigator.ParamChangeListener;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import example.ui.application.MyNavigableApplication;


/** Demo of automatic "Page" postfix removal in page name.
 *          usage of UriAnalyzer to ease parameters processing.
 * 
 * @author John Rizzo - BlackBeltFactory.com
 */
// Page name will be "ParamTest". If you really want it to be "ParamTestPage", don't rename you class ParamTestPagePage ;-). Use the @Page(uriName="ParamTest") annotation instead.
public class ParamTestPage extends VerticalLayout implements ParamChangeListener {

    Label userIdLabel = new Label();
    Label nameCountryLabel = new Label();
    Label ssnLabel = new Label();
    
    public ParamTestPage() {
        this.setSpacing(true);
        
        this.addComponent(new Label("This page wants to have the following parameters structure: \"value1/userid=value2/ssn=value3\" <br/> "+
                "where userid is optional, but ssn is mandatory.<br/>" +
                "if userid is there, it must be a long.<br/> "+
                "The first parameter has any value, but it is mandatory.<br/> ",
                Label.CONTENT_XHTML));
        
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        this.addComponent(hl);

        PageResource pr;
        Label l;
        
        ////// Valid examples.
        VerticalLayout col1 = new VerticalLayout();
        hl.addComponent(col1);
        col1.addComponent(new Label("Valid examples:"));

        pr = new PageResource(ParamTestPage.class, "John-Rizzo-Belgium/userId=123/ssn=xxxxxx");
        col1.addComponent( new Link(pr.getURL(), pr) );
        col1.addComponent(new Label("&nbsp;<br/>", Label.CONTENT_XHTML));
        
        pr = new PageResource(ParamTestPage.class, "John-Rizzo-Belgium/ssn=xxxxxx/userId=123");
        col1.addComponent( new Link(pr.getURL(), pr) );
        col1.addComponent(new Label("&nbsp; order of named parameters is not important (userid and ssn) <br/><br/>", Label.CONTENT_XHTML));

        

        ////// Invalid examples.
        VerticalLayout col2 = new VerticalLayout();
        hl.addComponent(col2);
        col2.addComponent(new Label("Invalid examples:"));

        ParamUriAnalyzer analyzer = NavigableApplication.getCurrent().getUriAnalyzer();
        
        pr = new PageResource(ParamTestPage.class, analyzer.getFragment("userId", "AAA"));
        // Alternative: pr = new PageResource(ParamTestPage.class, "userId=AAA");
        col2.addComponent(new Link(pr.getURL() , pr));
        col2.addComponent(new Label("&nbsp; 1st param contains a = sign while it's not supposed to;" +
        		" userid is not numeric; ssn is missing<br/>&nbsp;", Label.CONTENT_XHTML));

        pr = new PageResource(ParamTestPage.class, "userId=123/ssn=zzzzzz");
        col2.addComponent(new Link(pr.getURL() , pr));
        col2.addComponent(l=new Label("&nbsp; idem about the 1st missing non named parameter.<br/>" +
        		"Unfortunately, not detected by the UriAnalyzer that thinks 'userId=123'<br./>" +
        		"is the param at postion 0 (name and country)/<br/> " +
        		"Your business logic would probably figure out that 'userId=123'<br/>" +
        		"is not a valid name-country value (not found in the DB typically)." +
                "<br/>&nbsp;", Label.CONTENT_XHTML));
        l.setHeight(null);

        pr = new PageResource(ParamTestPage.class, "John-Rizzo-Belgium/ssn=xxxxxx/userId=1234567890123456789");
        col2.addComponent(new Link(pr.getURL() , pr));
        col2.addComponent(new Label("&nbsp; In this page (outside the UriAnalyzer), we check that the userId should be smaller than 1000<br/>&nbsp;", Label.CONTENT_XHTML));
      
        
        Panel panel = new Panel("Current Parameters (last valid ones, at least):");
        addComponent(panel);
        panel.addComponent(nameCountryLabel);
        panel.addComponent(userIdLabel);
        panel.addComponent(ssnLabel);
    }

    @Override
    public void paramChanged(NavigationEvent event) {
        ParamUriAnalyzer analyzer = MyNavigableApplication.getCurrent().getUriAnalyzer();
        
        // 1st parameter.
        String nameAndCountry = analyzer.getMandatoryString(event.getParams(), 0);  // Position 0.
        if (nameAndCountry == null) { return; }  // analyzer already displayed a message to the end-user.
        
        // userId.
        Long userId = analyzer.getLong(event.getParams(), "userId");
        // Additionnal business logic check:
        if (userId != null && userId.longValue() >= 1000 ) {
            Window currentWindow = NavigableApplication.getCurrentNavigableAppLevelWindow();
            currentWindow.showNotification("URL problem: userId cannot be > 1000 in our business logic<br/>",
                    event.getParams(), Window.Notification.TYPE_HUMANIZED_MESSAGE);
        }
        
        // ssn
        String ssn = analyzer.getMandatoryString(event.getParams(), "ssn");
        if (ssn == null) { return; }  // analyzer already displayed a message to the end-user.
        
        
        ///// From this point, all parameters are valid and we can continue displaying corresponding data.
        
        nameCountryLabel.setValue("param 0 (name & country) = " + nameAndCountry);

        if (userId != null) {
            userIdLabel.setValue("userId = " + userId);
        } else {
            userIdLabel.setValue("There is no userId, but it's ok, it's an optionnal parameter.");
        }
        
        ssnLabel.setValue("ssn = " + ssn);
    }
}
