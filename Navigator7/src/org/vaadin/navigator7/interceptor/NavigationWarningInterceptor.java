package org.vaadin.navigator7.interceptor;

import org.vaadin.navigator7.NavigableApplication;
import org.vaadin.navigator7.interceptor.NavigationWarningInterceptor.NavigationWarner.DefaultNavigatorWarningDialogMaker;
import org.vaadin.navigator7.interceptor.NavigationWarningInterceptor.NavigationWarner.NavigatorWarningDialogMaker;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/** Example of Interceptor.
 * If the target page implements NavigationWarner, this interceptor shows a dialog box to ask the user if he is sure to navigate away from the current page.
 * 
 * 
 * @author John Rizzo - BlackBeltFactory.com
 *
 */
public class NavigationWarningInterceptor implements Interceptor {

    NavigatorWarningDialogMaker navigatorWarningDialogMaker = new DefaultNavigatorWarningDialogMaker(); 

    @Override
    public void intercept(PageInvocation pageInvocation) {
        if (needToWaitWarningDialogBoxBeforeLeaving(pageInvocation)) {
            return;  // The listener of the modal dialog box being shown will eventually continue the invocation chain later. 
        } else { // Else, we continue to change the page. The current page implements no NavigationWarner, or it says that no data could currently be lost.
            pageInvocation.invoke();
        }

    }


    public boolean needToWaitWarningDialogBoxBeforeLeaving(PageInvocation pageInvocation) {
        // Check if the user really wants to leave the current page.
        Component currentPage = pageInvocation.getNavigator().getNavigableAppLevelWindow().getPage();
        if (currentPage instanceof NavigationWarner) {
            NavigationWarner warnerCurrentPage = (NavigationWarner)currentPage;
            
            String warn = warnerCurrentPage.getWarningForNavigatingFrom();
            if (warn != null && warn.length() > 0) {
                getNavigatorWarningDialogMaker().createWarningDialog(warn, pageInvocation);
                return true;  // The listener of the modal dialog box being shown will eventually request a page change later. 
            }
        }
        return false;
    }
    
    

    public NavigatorWarningDialogMaker getNavigatorWarningDialogMaker() {
        return navigatorWarningDialogMaker;
    }

    public void setNavigatorWarningDialogMaker(
            NavigatorWarningDialogMaker navigatorWarningDialogMaker) {
        this.navigatorWarningDialogMaker = navigatorWarningDialogMaker;
    }




    /** Implemented by the pages that want to show a warning message to the user before he leaves to another page. 
     * 
     * @author Joonas
     */
    public interface NavigationWarner {
        /**
         * Get a warning that should be shown to user before navigating away
         * from the page.
         * 
         * If the current page is in state where navigating away from it could
         * lead to data loss, this method should return a message that will be
         * shown to user before he confirms that he will leave the page. If
         * there is no need to ask questions from user, this should return null.
         * 
         * @return Message to be shown or null if the page may be changed without warning the end-user.
         */
        public String getWarningForNavigatingFrom();



        /** Used (probably implemented) by the pages who are not happy with the DefaultNavigationWarner.
         * 
         * @author John Rizzo - BlackBeltFactory.com
         *
         */
        public interface NavigatorWarningDialogMaker {
            /** Creates and displays a modal dialog box (window) that asks if the user is sure to leave the current page.
             * 
             *  Typically the dialog contains an "continue" button with that code:
             * 
            Button cont = new Button("Continue",  new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    main.removeWindow(wDialog);
                    pageInvocation.invoke();
                }
            });
             * @See DefaultNavigatorWarningDialogMaker for an example. 
             * @param warningMessage  message returned by the page we are leaving through the NavigationWarner.getWarningForNavigatingFrom() method.
             * @param pageClass  to be passed to the Navigator.navigateTo() method, in the ok button ClickListener.
             * @param params   to be passed to the Navigator.navigateTo() method, in the ok button ClickListener.
             */
            public void createWarningDialog(String warningMessage,  PageInvocation pageInvocation);
        }

        /** Default implementation.
         */
        class DefaultNavigatorWarningDialogMaker implements NavigatorWarningDialogMaker {

            @Override
            public void createWarningDialog(String warningMessage, final  PageInvocation pageInvocation) {
                VerticalLayout lo = new VerticalLayout();
                lo.setMargin(true);
                lo.setSpacing(true);
                lo.setWidth("400px");
                final Window wDialog = new Window("Warning", lo);
                wDialog.setModal(true);
                final Window main = NavigableApplication.getCurrentNavigableAppLevelWindow();
                main.addWindow(wDialog);
                lo.addComponent(new Label(warningMessage));
                lo.addComponent(new Label("If you do not want to navigate away from the current page, press Cancel."));

                Button cancel = new Button("Cancel", new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        main.removeWindow(wDialog);
                        // Don't continue page invocation.
                    }
                });

                Button cont = new Button("Continue",  new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        main.removeWindow(wDialog);
                        pageInvocation.invoke();
                    }
                });


                HorizontalLayout h = new HorizontalLayout();
                h.addComponent(cancel);
                h.addComponent(cont);
                h.setSpacing(true);
                lo.addComponent(h);
                lo.setComponentAlignment(h, "r");

            }

        }
    }
}