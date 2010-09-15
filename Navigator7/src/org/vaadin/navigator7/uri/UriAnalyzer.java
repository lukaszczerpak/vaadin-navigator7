package org.vaadin.navigator7.uri;

import org.vaadin.navigator7.NavigatorConfig;
import org.vaadin.navigator7.WebApplication;

import com.vaadin.ui.Component;

/**
 * This level of class is able to separate the page name from parameters.
 * It is needed by the Navigator class.
 * 
 * But it provides no manipulation of the parameters. This could be the propose of your class extending this (if the way that my descendent handles parameters does not suit you).
 * 
 * This class (and it's descendants) is stateless, and we share the instance for all the navigators instances of an application.
 * Override the NavigableApplication.createNewUriAnalyzer() method to make the system use your class.
 * 
 * @author John Rizzo - BlackBeltFactory.com
 */
public class UriAnalyzer {

    
    /** 
     * uri has the form of: "user/userid=555", e.g. from this url: "http://domain.com/appName/#user/userid=555"
     * We extract the first part, before the 1st "/" ("user"), and we select a new center component based on that.
     * 
     * Returns an array of 2 strings. The 1st is the screen name ("user"), the second is the rest of the string ("userid=555")
     * If there are no params, the second element of the array is an empty string.
     *
     */
    public String[] extractPageNameAndParamsFromFragment(String fragment) {
        if (fragment == null || fragment.trim().equals("")) {
            return new String[] {null, null};
        }

        String[] fragmentParts = fragment.split("/", 2);  // Max in 2 parts. We split "user/userid=555/big=true" into "user" and "userid=555/big=true". Separating parameters from each other is not our problem here.
        if (fragmentParts.length == 1) {
            fragmentParts = new String[] { fragmentParts[0], null };
        }
        
        // We remove the eventual "!" from page name (added for crawlable pages).
        if (fragmentParts[0].startsWith("!")) {
            fragmentParts[0] = fragmentParts[0].substring(1);
        }
        
        return fragmentParts;
    }
    
    
    /** Creates a string for the URI. e.g. "Auction/1234".  In the browser it will be "http://domain.com/MyApplication/#Auction/1234".
     * 
     * This poor method is static because PageResource needs it and may itself be used in a batch thread outside a web application.
     * See explanation here: http://vaadin.com/forum/-/message_boards/message/216481?_19_delta=10&_19_keywords=&_19_advancedSearch=false&_19_andOperator=true&cur=3#_19_message_216481
     * 
     * @param params may be null if no param.
     * @param withAnchor true means you want a # in front of the result (as "#Auction/1234"  */
    public
    static  // See why in Javadoc above.    
    String buildFragmentFromPageAndParameters(Class<? extends Component> pageClass, String params, boolean withAnchor) {
        if ("".equals(params)) {
            params = null;  // One case for both.
        }
        
        WebApplication webApp = WebApplication.getCurrent();
        
        String pageName;
        boolean isCrawlable;
        Class<? extends Component> homePageClass;
        
        
        if (webApp != null) { // Normal case, we are in a web thread
            NavigatorConfig navigatorConfig = webApp.getNavigatorConfig();
            homePageClass = navigatorConfig.getHomePageClass();

            pageName = navigatorConfig.getPageName(pageClass);
            isCrawlable = navigatorConfig.isPageCrawlable(pageClass);
            
        } else { // We are probably called in the context of a batch
            pageName = NavigatorConfig.computePageName(pageClass);
            isCrawlable = NavigatorConfig.computeIsCrawlable(pageClass);
            homePageClass = null;  // We just can't know unless we have a navigatorConfig instance.
        }
        
        if (isCrawlable) {
            pageName = "!" + pageName;
        }

        String paramsFragment = (params==null ? ""   // Don't show the "/" in case there is no param. 
                                 : "/"+params);
        
        String anchor = withAnchor ? "#" : "";
        
        String result = pageClass == homePageClass ?
                   anchor +           paramsFragment   // For the home page we don't tell the page name. But want # also for the home page because: see http://vaadin.com/forum/-/message_boards/message/69700
                : (anchor + pageName + paramsFragment); // Normal case
        return result;
    }
    
}