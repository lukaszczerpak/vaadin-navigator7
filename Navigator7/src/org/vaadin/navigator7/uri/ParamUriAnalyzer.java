package org.vaadin.navigator7.uri;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.navigator7.NavigableApplication;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

/**
 * by default, parameters are separated by "/", and values are separated from keys by "=".
 *   e.g.: "userId=222/subject=Hello/How-Are-You".
 *         The last parameter "How-Are-You" has no key (name), but only a value.
 * You may change the separators through the properties paramSeparator and valueSeparator.
 *   For example, you may prefer "&" to separate parameters: "userId=222&subject=Hello&How-Are-You" 
 * 
 * 
 * Some parameters are position based. 
 *   e.g.: "1234/ABCD/5678" we have 3 parameters.
 *         Param at position 0 is "1234".
 *         Param at position 5 is null.
 *         
 * Some parameters are key based.
 *   e.g.: "id=987/k=abc", we have 2 parameters.
 *         The name of the first is "id" and its avlue is "978".
 *         The value of the parameter "z" is null.
 *         
 * Mixes are possible.
 *   e.g.: "246/AAA/userId=0231"
 *         The value of the parameter "userId" is "0231".
 *         The value at position 1 is "AAA".
 * 
 * Detected problem during parameters analysis are reported to the end user through the reportProblemWithFragment() method.
 * 
 * @author John Rizzo - BlackBeltFactory.com
 *
 */
public class ParamUriAnalyzer extends UriAnalyzer {

    protected String paramsSeparator = "/";   // e.g.: param1=value/param2=222/param3=ABC   Could be "&" also (change the value in the constructor of your descendant).
    protected String valueSeparator = "=";    // e.g.: id=123  (key is id, value is 123)

    
    /** Return the amount of params */
    public int countParams(String fragment) {
        return fragment.split(paramsSeparator).length;
    }
    
    /**  Returns the value of the parameter of the given key. Returns null if parameter not found.
     * 
     *   if fragment is "auctionid=123/brol=abc";
     *    if key is auctionid, it returns "123"
     *    if key is hello, it returns null
     */
    public String getString(String fragment, String key) {
        String[] pairs = fragment.split(paramsSeparator);
        for (String pair : pairs) {
            String[] keyValue = pair.split(valueSeparator);
            if (keyValue.length == 2 && keyValue[0].equals(key)) {
                return keyValue[1];
            }
        }
        return null;
    }

    /** Idem getString, and shows a message to the end-user if parameter is missing */
    public String getMandatoryString(String fragment, String key) {
        String result = getString(fragment, key);
        if (result == null) {
            reportProblemWithFragment("Parameter '"+key+"' not found in URL, while it is mandatory.", fragment);
        }
        return result;
    }
    

    /**  Returns the value of the parameter at the given position. Returns null if parameter not found.
     * Positions start counting at 0 (1st parameter is position 0).
     * 
     *   if fragment is "123/abc";
     *    if key is auctionid, it returns "123"
     *    if key is hello, it returns null
     */
    public String getString(String fragment, int position) {
        if (fragment == null) {return null;}  // Not found...
        String[] pairs = fragment.split(paramsSeparator);
        if (pairs.length > position) {  // Ok, found.
            return pairs[position];
        } else { // Not so many parameters
            return null;
        }
    }

    /** Idem getString, and shows a message to the end-user if parameter is missing */
    public String getMandatoryString(String fragment, int position) {
        String result = getString(fragment, position);
        if (result == null) {
            reportProblemWithFragment("Parameter n°"+position+" not found in URL, while it is mandatory.", fragment);
        }
        return result;
    }

    
    /** Returns a parameter which is supposed to be a long (a String in the uri converted into a long).
     * This is typically the surrogate PK of a DB record (entity). 
     * 
     * Returns null if not found (no id specified in url, or invalid url)
     * if fragment is "auctionid=123/brol=abc";
     *    if paramName is auctionid, it returns 123
     *    if paramName is hello, it returns null
     *    if paramName is brol, it returns null (because it's not a number) and displays an error message to the user.
     * if fragment is null, returns null */
    public Long getLong(String fragment, String paramName) {
        String value = getString(fragment, paramName);
        if (value == null) {  // Not found
            return null;
        }
        try {
            Long result;
            result = Long.parseLong(value);
            return result;
        } catch(Exception e) {
            // parameter found, but it's not a number.
            reportProblemWithFragment("Invalid parameter '"+paramName+"' in URL which is expected to be a number.", fragment);
            return null;
        }
    }

    /** Idem getLong, and shows a message to the end-user if parameter is missing */
    public Long getMandatoryLong(String fragment, String key) {
        Long result = getLong(fragment, key);
        if (result == null) {
            reportProblemWithFragment("Parameter '"+key+"' not found in URL, while it is mandatory.", fragment);
        }
        return result;
    }

    
    /** Returns a parameter which is supposed to be a long (a String in the uri converted into a long).
     * This is typically the surrogate PK of a DB record (entity). 
     * 
     * Returns null if not found (no id specified in url, or invalid url)
     * if fragment is "auctionid=123/brol=abc";
     *    if paramName is auctionid, it returns 123
     *    if paramName is hello, it returns null
     *    if paramName is brol, it returns null (because it's not a number) and displays an error message to the user.
     * if fragment is null, returns null */
    public Long getLong(String fragment, int position) {
        String value = getString(fragment, position);
        if (value == null) {  // Not found
            return null;
        }
        try {
            Long result;
            result = Long.parseLong(value);
            return result;
        } catch(Exception e) {
            // parameter found, but it's not a number.
            reportProblemWithFragment("Invalid parameter '"+value+"' at position '"+position+"' in URL which is expected to be a number.", fragment);
            return null;
        }
    }

    /** Idem getLong, and shows a message to the end-user if parameter is missing */
    public Long getMandatoryLong(String fragment, int position) {
        Long result = getLong(fragment, position);
        if (result == null) {
            reportProblemWithFragment("Parameter n°"+position+"' not found in URL, while it is mandatory.", fragment);
        }
        return result;
    }

    
    
    

    /** Easy method to build a fragment from key/values
     * The result will probably be given as parameter to Navigator.setUriParams() by the page.
     */
    public String getFragment(String paramName, String paramValue) {
        return (paramName  != null ? paramName  + valueSeparator + paramValue  : "");
    }    

    /** Easy method to build a fragment from key/values
     * The result will probably be given as parameter to Navigator.setUriParams() by the page.
     */
    public String getFragment(String paramName, String paramValue, String paramName2, String paramValue2) {
        return (paramName  != null ?                   paramName  + valueSeparator + paramValue  : "")
             + (paramName2 != null ? paramsSeparator + paramName2 + valueSeparator + paramValue2 : "");
    }

    /** Easy method to build a fragment from key/values
     * The result will probably be given as parameter to Navigator.setUriParams() by the page.
     */
    public String getFragment(String paramName, String paramValue, String paramName2, String paramValue2, String paramName3, String paramValue3) {
        return (paramName  != null ?                   paramName  + valueSeparator + paramValue  : "")
             + (paramName2 != null ? paramsSeparator + paramName2 + valueSeparator + paramValue2 : "")
             + (paramName3 != null ? paramsSeparator + paramName3 + valueSeparator + paramValue3 : "");
    }   
    
    public String getFragment(Class<? extends Component> pageClass, Object ... posParams) {
        return ParamInjector.generateFragment(pageClass, posParams, null);   
    }
    
    
    /** Easy method to add a named parameter to an existing fragment
     */
    public String addFragment(String fragment, String paramName, String paramValue) {
        String start;
        if (fragment != null && !"".equals(fragment)) {  // There are previous values already.
            start = fragment + paramsSeparator;   // We separate from the previous value with a "/"
        } else {
            start = "";
        }
        return start + paramName  + valueSeparator + paramValue;  // "..../name=value"
    }    

    /** Easy method to add a positional parameter to an existing fragment
     */
    public String addFragment(String fragment, String paramValue) {
        String start;
        if (fragment != null && !"".equals(fragment)) {  // There are previous values already.
            start = fragment + paramsSeparator;   // We separate from the previous value with a "/"
        } else {
            start = "";
        }
        return start + paramValue;  // "..../value"
    }    


    



    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**  Utility String extraction method.
     *   if fragment is "javacorefundamentals&brol=abc";
     *   javacorefundamentals is returned
     */
    public List<String> extractNonPairStringFromUriFragment(String fragment) {
        String[] pairs = fragment.split(paramsSeparator);
        List<String> result = new ArrayList<String>();
        for (String pair : pairs) {
            if(!pair.contains(valueSeparator)){
                result.add(pair);
            }
        }
        return result;
    }
    

    
    /**
     * Shows a notification to the user when he has set an illegal URI in the browser (invalid link or url hacking).
     * This is convenient, to free the page from the responsibility of handling these low level fragment strings problems.
     *
     * Override this method to do another action.
     * 
     * @param problemDescription is not i18n, sorry. This framework should be enhanced if someone needs to support that.
     */
    public void reportProblemWithFragment(String problemDescription, String fragment) {
        Window currentWindow = NavigableApplication.getCurrentNavigableAppLevelWindow();
        currentWindow.showNotification(problemDescription + "<br/>", fragment, Window.Notification.TYPE_HUMANIZED_MESSAGE);
    }

    /** Perform last chance convertion
     * Override me for application-wide convertion, as for example, a Lanugage enum (FR, EN,...) to be converted from String "FR" to the language enum value Language.FR
     * If you need it for only one page, make your page implement TypeConvertor.
     * The other direction of the convertion (object to string) is done with Object.toString().
     * 
     * @param fragment should be useless. May be used to display a complete error message. */
    public Object convertSpecialType(Class<?> type, String valueStr, String fragment) {
        reportProblemWithFragment("Invalid parameter value "+valueStr, fragment);
        return null;  // Convertion failed, by default. Your method would probably try to actually convert first.
    }
    
}
