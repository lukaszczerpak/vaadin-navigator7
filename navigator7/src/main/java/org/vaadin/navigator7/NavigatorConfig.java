package org.vaadin.navigator7;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vaadin.navigator7.interceptor.Interceptor;

import com.vaadin.ui.Component;

/** Contains the list of classes used as pages in NavigableApplication NavigableAppLevelWindows.
 * Can tell a page class from page name in uri and vis versa.
 * 
 * @author John Rizzo - BlackBeltFactory.com
 */
public class NavigatorConfig implements Serializable {
    
    // The key is lowercase. For example, with the value AuctionEditorPage.class, we have the key "auctioneditor" in this map. The goal is to be key insesitive when accepting incoming uris. 
    private HashMap<String, Class<? extends Component>> uriToClass = new HashMap<String, Class<? extends Component>>();

    // The value is CamelCase. For example, with the key AuctionEditorPage.class, we have the value "AuctionEditor" in this map. The goal is to produce easy to read CamelCase uris.
    private HashMap<Class<? extends Component>, String> classToUri = new HashMap<Class<? extends Component>, String>();

    private Class<? extends Component> homePageClass;  // Class used for uri with no page name (as, for example just "http://domain.com/").
    
    protected List<Interceptor> interceptorList = new ArrayList<Interceptor>();
    
    // Pages for which Google bots should navigate through. Adds a ! in URIs.
    private Set<Class<? extends Component>> crawlablePages = new HashSet<Class<? extends Component>>();
    
    /** Scans the pages annotated with @Page in the classpath, for the sub-package of the package given as parameter */
    public void registerPages(String packageName) {
        // TODO: do some scanning of @Page annotated classes and add them to the navigator.
        // See http://stackoverflow.com/questions/259140/scanning-java-annotations-at-runtime
        // It depends on what system Vaadin 7 will use to autodetect annotations.
        // Question asked there: http://vaadin.com/forum/-/message_boards/message/152638
        throw new UnsupportedOperationException("This feature has not been implemented yet. Use the other registerPages method, taking an array of classes as parameter.");
    }
    
    public void registerPages(Class[] pageClasses) {
        if (pageClasses.length == 0) {
            throw new IllegalArgumentException("Your array of classes is empty and it should at least contain one page");
        }
        
        for (Class clazz : pageClasses) {
            registerPage(clazz);
        }
    }

    public void registerPage(Class clazz) {
        if (! Component.class.isAssignableFrom(clazz)) {  // In other words, does pageClass extend Component?
            throw new IllegalArgumentException("Given classes should extend Component. One of the classes does not: "+clazz);
        }
        Class<? extends Component> pageClass = (Class<? extends Component>)clazz;


        addPageClass(pageClass);
    }

    public void unregisterPage(Class clazz) {
        if (! Component.class.isAssignableFrom(clazz)) {  // In other words, does pageClass extend Component?
            throw new IllegalArgumentException("Given classes should extend Component. One of the classes does not: "+clazz);
        }
        Class<? extends Component> pageClass = (Class<? extends Component>)clazz;


        removePageClass(pageClass);
    }

    public String computePageName(Class<? extends Component> pageClass) { 
        // Discover the name through the @Page annotation.
        String pageName;
        Page pageAnnotation = pageClass.getAnnotation(Page.class);

        if (pageAnnotation == null) {  // Class not annotated => default name.
            pageName = computePageNameFromClassName(pageClass.getSimpleName());

        } else { // Class annotated
            if (pageAnnotation.uriName() != null && ! "".equals(pageAnnotation.uriName().trim())) {
                pageName = pageAnnotation.uriName();
            } else {  // No name given in the annotation => use default.
                pageName = computePageNameFromClassName(pageClass.getSimpleName());
            }
        }
        return pageName;
    }

    public boolean computeIsCrawlable(Class<? extends Component> pageClass) {
        Page pageAnnotation = pageClass.getAnnotation(Page.class);
        return (pageAnnotation != null && pageAnnotation.crawlable());
    }
    
    public void addPageClass(Class<? extends Component> pageClass) {
        if (computeIsCrawlable(pageClass)) {
            crawlablePages.add(pageClass);
        }

        String pageName = computePageName(pageClass);
        
        // Add the page in maps.
        String lowerCasePageName = pageName.toLowerCase();
        if (uriToClass.containsKey(lowerCasePageName)) {
            throw new IllegalArgumentException("Adding a page with a name that has already been added in the configuration: ["+lowerCasePageName+"]");
        }
        uriToClass.put(lowerCasePageName, pageClass); // lowercase here (see comment on Map definition)

        if (classToUri.containsKey(pageClass)) {
            throw new IllegalArgumentException("Adding a page with a class that has already been added in the configuration: ["+pageClass+"]");
        }
        classToUri.put(pageClass, pageName);  // Uppercase here (see comment on Map definition)
    }

    public void removePageClass(Class<? extends Component> pageClass) {
        if (computeIsCrawlable(pageClass)) {
            crawlablePages.remove(pageClass);
        }

        if (!classToUri.containsKey(pageClass)) {
            throw new IllegalArgumentException("Removing a page with a class that has not been added in the configuration: ["+pageClass+"]");
        }
        String pageName = classToUri.get(pageClass);
        String lowerCasePageName = pageName.toLowerCase();

        classToUri.remove(pageClass);
        uriToClass.remove(lowerCasePageName);
    }

    public Class<? extends Component> getHomePageClass() {
        return homePageClass == null
                ? homePageClass = uriToClass.values().iterator().next()
                : homePageClass;
    }

    public void setHomePageClass(Class<? extends Component> hpClassParam) {
        this.homePageClass = hpClassParam;
    }
    
    
    /** Removes an optional "Page" postfix from the end of the given String.
     * If the given class name is "MyGreatPage", then the result will be "MyGreat".
     * If the given class name is "Hello", then the result will be "Hello".
     */
    public String computePageNameFromClassName(String simpleName) {
        if (simpleName.endsWith("Page")) {
            simpleName = simpleName.substring(0, simpleName.length()-4); // Remove the word "Page".
        }

        return simpleName;
    }

    public Collection<Class<? extends Component>> getPagesClass() {
        return classToUri.keySet();
    }

    public Class<? extends Component> getPageClass(String pageName) {
        return uriToClass.get(pageName.toLowerCase());
    }

    public String getPageName(Class<? extends Component> pageClass) {
        String result = classToUri.get(pageClass);
        // Defensive coding
        if (result == null) {  // Not found.
            throw new IllegalArgumentException("Bug: a given page class would have not a name? " +
            		"The caller probably does not expect that." +
            		" You probably did not include that page in your configuration. " +
            		" You can include it by calling the registerPage() method in your descendant of WebApplication." +
            		" Page class = "+ pageClass);
        }
        return result;
    }


    public List<Interceptor> getInterceptorList() {
        return interceptorList;
    }

    public boolean isPageCrawlable(Class<? extends Component> pageClass) {
        return crawlablePages.contains(pageClass);
    }
    
    



}
