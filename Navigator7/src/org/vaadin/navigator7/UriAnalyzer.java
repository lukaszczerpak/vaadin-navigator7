package org.vaadin.navigator7;

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
        return fragmentParts;
    }
    
    
    /** Creates a string for the URI. e.g. "Auction/1234".  In the browser it will be "http://domain.com/MyApplication/#Auction/1234".
     * @param params may be null if no param.
     * @param withAnchor true means you want a # in front of the result (as "#Auction/1234"  */
    public String buildFragmentFromPageAndParameters(Class<? extends Component> pageClass, String params, boolean withAnchor) {
        if ("".equals(params)) {
            params = null;  // One case for both.
        }
        NavigatorConfig navigatorConfig = NavigableApplication.getCurrent().getNavigatorConfig();
        Class<? extends Component> homePageClass = navigatorConfig.getHomePageClass();
        
        String pageName = navigatorConfig.getPageName(pageClass);

        String paramsFragment = (params==null ? ""   // Don't show the "/" in case there is no param. 
                                 : "/"+params);
        
        String anchor = withAnchor ? "#" : "";
        
        String result = pageClass == homePageClass ?
                   anchor +           paramsFragment   // For the home page we don't tell the page name. But want # also for the home page because: see http://vaadin.com/forum/-/message_boards/message/69700
                : (anchor + pageName + paramsFragment); // Normal case
        return result;
    }
    
}