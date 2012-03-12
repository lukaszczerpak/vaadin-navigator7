package org.vaadin.navigator7.window;

import org.vaadin.navigator7.window.NavigableAppLevelWindow;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;

/** 
*Initializes the width of a new page to be displayed 
*@author Mathieu Nachman, Faton Alia, John Rizzo - BlackBeltFactory.com
*/
public abstract class  FluidAppLevelWindow extends NavigableAppLevelWindow  {

	    /** Sets the page in the window, at the right place (and removes the previous one)
	     * Override this (don't forget to call super) if you want to do something everytime a new page is being placed */
	    @Override
	    synchronized public void changePage(Component pageParam) {
	        super.changePage(pageParam); // Does most of the job.
	        pageParam.setWidth("100%");
	    }
	  
	    @Override
	    protected Layout createMainLayout(){
	    	HorizontalLayout mainLayout = new HorizontalLayout();
	    	mainLayout.setWidth("100%");
	    	return mainLayout;
	    }

}
