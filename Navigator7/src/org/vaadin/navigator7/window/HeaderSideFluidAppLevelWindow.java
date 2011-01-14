package org.vaadin.navigator7.window;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

/** FloatAppLevelWindow has a typical left side menu and header structure around the Page.
 * Extend this class to define your menu and header. 
 * 
 * @author Mathieu Nachman, Faton Alia, John Rizzo - BlackBeltFactory.com
 */
public abstract class HeaderSideFluidAppLevelWindow extends FluidAppLevelWindow {
	
	
	

	@Override
	protected ComponentContainer createComponents() {
		HorizontalLayout windowContentLayout = (HorizontalLayout)this.getContent();
		
		Component side = createSide();
		side.setWidth("200px");
		windowContentLayout.addComponent(side);

		
		VerticalLayout headerContentLayout = new VerticalLayout();
        windowContentLayout.addComponent(headerContentLayout);
		windowContentLayout.setExpandRatio(headerContentLayout,1.0f);
		headerContentLayout.setWidth("100%");
		
		Component header = createHeader();
		header.setWidth("100%");
		headerContentLayout.addComponent(header);
		
		CssLayout content =   new CssLayout();  // Will be super.pageContainer.
		content.setWidth("100%");
		headerContentLayout.addComponent(content);
		headerContentLayout.setExpandRatio(content, 1.0f);
		
		return content;
	}
	
	/** called by the template method createComponents. Override to provide your header component (probably an HorizontalLayout). 
    * Your method should set a fixed height to the returned component (as "150px", for example) */
    protected abstract Component createHeader();
    
    /** called by the template method createComponents. Override to provide your side component (proably a VerticalLayout).
     * Your method should set a fixed width to the returned component (as "200px", for example) */
    protected abstract Component createSide();
	
	
}
