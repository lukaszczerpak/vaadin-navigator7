package org.vaadin.navigator7;



/** convenient methods to get (i.e. JPA) Entities from values (primary keys) in the URIs.
 * Pages can directly turn URI parameters into entities loaded from your DB. 
 * 
 * This class suppose that your primary keys are Long (or long).
 * It's probably the case if you use surrogate keys with JPA.
 * Else, you may have to write more specific methods in your descendant UriAnalyzer class (probably inspired from the methods here).
 * 
 * @author John Rizzo - BlackBeltFactory.com
 *
 * @param <E> A common ancestor class for all your entities. Could be Identifiable or BaseEntity if you have such a class. At worst, it is Object. Define this parameter in the class definition of your descendant of EntityUriAnalyzer.
 */
public abstract class EntityUriAnalyzer<E> extends ParamUriAnalyzer {

    final public static String MAINID = "id";

    /** Override this method to define how to access your DB.
     * It's probably as simple as: return entityManager.find(entityClass, pk);
     * What depends much on your application is how you get a valid instance from your entityManager.
     * If you JPA static utility methods, it's easy.
     * If you use Spring, you have 2 options:
     * - Make your descendent of EntityUriAnalyzer a Spring bean (with the @Component annotation)
     *   and define a @PersistenceContext EntityManager em attribute (or an @Autowired generic dao). 
     *   To make it work, you probably need to activate compile time weaving on your class, and annotate it @Configurable.  
     * - Use the WebApplicationContext.getBean() method to get a reference to your generic dao.
     *   The problem is to get this Spring WebApplicationContext.
     *   It's easy if you have the ServletContext: WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext)
     *   but then the problem is to have the ServletContext.
     *   A way to have it is ... to put it in a ThreadLocal from a Filter or from a Vaadin TransactionListener.
     *   
     * Note that this EntityUriAnalyzer is not bound to JPA/Hibernate. It could work with JDBC or anything else.
     * ... as long as you have a generic way to return an entity object from it's id and class. 
     * 
     * @param pk Primary key of the entity. Convert this string in whatever JPA needs (as a Long) in the overrideng method.
     * @return returns null if no data found for that pk.
     */
    protected abstract E findEntity(Class<? extends E> entityClass, String pk);

    
    public E getEntity(String params, String key, Class<? extends E> entityClass) {
        E entity = null;
        String id = getString(params, key); 
        if (id != null) { 
            entity = findEntity(entityClass, id);
        }
        return entity;
    }

    
    
    public E getMandatoryEntity(String params, String key, Class<? extends E> entityClass) {
        if ( null != getMandatoryString(params, key)) {  // Notification displayed if missing parameter. 
            return getEntity(params, key, entityClass);  // Maybe null, but a notification will have been displayed in case of something is strange.
        } else { // We have no id.
            return null;
        } 
    }

    
    public E getEntity(String params, int position, Class<? extends E> entityClass) {
        E entity = null;
        String id = getString(params, position); 
        if (id != null) { 
            entity = findEntity(entityClass, id);
        }
        return entity;
    }

    public E getMandatoryEntity(String params, int position, Class<? extends E> entityClass) {
        if ( null != getMandatoryString(params, position)) {  // Notification displayed if missing parameter. 
            return getEntity(params, position, entityClass);  // Maybe null, but a notification will have been displayed in case of something is strange.
        } else { // We have no id.
            return null;
        } 
    }

    
    
    /** Returns the (non fully qualified) class name.
     * If entityClass = Auction.class, then the expected parameter name is "Auction".
     * i.e. "Foo=123/Auction=abc".
     * 
     * Override this method if you'd like another names from classes. For example, you may prefer "AuctionId" (simple class name + "Id").
     * 
     * @param entityClass
     * @return
     */
    protected String getEnityParamName(Class<? extends E> entityClass) {
        return entityClass.getSimpleName();
    }
    
}
