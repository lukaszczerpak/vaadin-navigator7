package org.vaadin.navigator7.window;

import com.vaadin.ui.Component;

/** The fact that this class extends HeaderFooterFIXEDAppLevelWindow is questionable,
 * because it is not "Fixed". In Vaadin7, another solution would probably be better.
 * An alternative is to copy/paste the HeaderFooter code from HeaderFooterFixedAppLevelWindow which would be even worse.
 * 
 * http://vaadin.com/forum/-/message_boards/message/267744;jsessionid=5303D4D88430DCB26113A4A5F09D2527?_19_keywords=&_19_advancedSearch=false&_19_andOperator=true&_19_delta=75#_19_message_267744
 * @author John
 *
 */
public abstract class HeaderFooterFloatAppLevelWindow extends HeaderFooterFixedAppLevelWindow {


    public HeaderFooterFloatAppLevelWindow() {
        super();
    }
    
    protected void prepareInnerBand(Component innerComponent) {
        super.prepareInnerBand(innerComponent);
        innerComponent.setWidth("100%");
    }

}
