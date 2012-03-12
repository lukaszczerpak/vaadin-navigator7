package org.vaadin.navigator7.window;


import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Layout;

/** FixedAppLevelWindow that has the typical Header/Footer structure around the Page.
 * Extend this class to define your header and footer. 
 * 
 * @author John Rizzo - BlackBeltFactory.com
 */
public abstract class HeaderFooterFixedAppLevelWindow extends FixedApplLevelWindow {
    
    protected Component header;   // Fixed width (see ancestor.pageWidth), inside headerBand. 
    protected Layout headerBand;  // Directly contains header. Goes from the left edge of the browser to the right edge.

    protected Component footer;   // Fixed width (see ancestor.pageWidth), inside footerBand. 
    protected Layout footerBand;  // Directly contains footer. Goes from the left edge of the browser to the right edge.

    @Override
    protected ComponentContainer createComponents() {
        Layout windowContentLayout = (Layout)this.getContent();
        
        // Header
        header = createHeader();
        headerBand = createBandLayout( header );
        windowContentLayout.addComponent(headerBand);

        // Center page area
        Layout pageBand = createBandLayout(null);
        windowContentLayout.addComponent(pageBand);
        
        // Footer
        footer = createFooter();
        footerBand = createBandLayout( footer );
        windowContentLayout.addComponent(footerBand);

        return pageBand;
    }

    /** called by the template method createComponents. Override to provide your header component. */
    protected abstract Component createHeader();

    /** called by the template method createComponents. Override to provide your footer component */
    protected abstract Component createFooter();

    
    
    
    public Component getHeader() {
        return header;
    }

    public Layout getHeaderBand() {
        return headerBand;
    }

    public Component getFooter() {
        return footer;
    }

    public Layout getFooterBand() {
        return footerBand;
    }
}
