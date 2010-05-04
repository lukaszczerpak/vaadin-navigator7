package org.vaadin.navigator7.window;


import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Layout;

/** The place taken by your pages is fixed in width (as opposed to "fluid").
 * You need to override createComponents() if you directly extend this class.
 * 
 * Write another direct descendant of NavigableappLevelWindow, to create a "fluid" window.
 * Probably very simplistic, unless you want a header and footer. In that case you will probably have to copy/paste a few lines of code from HeaderFooterFixedAppLevelWindow.
 * 
 * @author John Rizzo - BlackBeltFactory.com
 * 
 **/

public abstract class FixedApplLevelWindow extends NavigableAppLevelWindow {

    private int pageWidth = 980;  // In pixels. 980px is a good size for 1027*768 res screens. You can change this default by calling setPageWidth().


    /** Sets the page in the window, at the right place (and removes the previous one)
     * Override this (don't forget to call super) if you want to do something everytime a new page is being placed */
    @Override
    synchronized public void changePage(Component pageParam) {
        super.changePage(pageParam); // Does most of the job.
        
        prepareInnerBand(page);
        page.addStyleName("FixedPageTemplate-layoutPage"); 
    }
    
    /** Create a layout around the given component, and prepare the component to have a fixed width inside the band */
    protected Layout createBandLayout(Component innerComponent) {
        Layout result = new CssLayout();
        result.addStyleName("FixedPageTemplate-bandOuterLayout");
        if (innerComponent != null) {
            prepareInnerBand(innerComponent);
            result.addComponent(innerComponent);
        }
        return result;
    }

    /** Prepare the component to have a fixed width inside the template.
     * You probably don't call this method directly. Usually, you call createBandLayout() */
    protected void prepareInnerBand(Component innerComponent) {
        innerComponent.addStyleName("FixedPageTemplate-bandInnerLayout");
        innerComponent.setWidth(getPageWidth(), Component.UNITS_PIXELS);
    }
    
    
    public int getPageWidth() {
        return pageWidth;
    }

    /** Attention, if you change the page width, it should be done BEFORE createComponents executes (and it's called by the constructor...), because it uses the value.
     * A good way is to override createComponents in your descendant:
     * @Override
     * createComponents() {
     *     setPageWidth(1500);  // before...
     *     super.createComponent();
     * }
     * @param pageWidth
     */
    public void setPageWidth(int pageWidth) {
        this.pageWidth = pageWidth;
    }


}
