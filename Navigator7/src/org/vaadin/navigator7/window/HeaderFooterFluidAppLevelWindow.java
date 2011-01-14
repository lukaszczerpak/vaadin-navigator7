package org.vaadin.navigator7.window;

import org.vaadin.navigator7.window.HeaderFooterFixedAppLevelWindow;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

/** HeaderFooterFluidAppLevelWindow has a header/footer structure around the Page.
 * Extend this class to define your menu and header. 
 * 
 * @author Mathieu Nachman, Faton Alia, John Rizzo - BlackBeltFactory.com
 */
public abstract class HeaderFooterFluidAppLevelWindow extends FluidAppLevelWindow {

	@Override
	protected ComponentContainer createComponents() {
		VerticalLayout windowContentLayout = (VerticalLayout)this.getContent();
		
		Component header = createHeader();
		windowContentLayout.addComponent(header);
		header.setWidth("100%");
		
		CssLayout content =   new CssLayout();  // Will be super.pageContainer.
		windowContentLayout.addComponent(content);
		content.setWidth("100%");
		
		Component footer = createFooter();
		windowContentLayout.addComponent(footer);
		footer.setWidth("100%");
		
		return content;
	}
	
	 /** called by the template method createComponents. Override to provide your header component. */
    protected abstract Component createHeader();

    /** called by the template method createComponents. Override to provide your footer component */
    protected abstract Component createFooter();
    
    @Override
    protected Layout createMainLayout(){
    	VerticalLayout mainLayout = new VerticalLayout();
    	mainLayout.setWidth("100%");
    	return mainLayout;
    }
}
