package org.vaadin.navigator7.uri;

public interface TypeConvertor {

    /** Override me (seldom, not usual) to provide a special conversion, for example from valueStr into an enum.
     * Don't forget EntityUriAnalyzer.findEntity will automatically be called for entity retrieval, you don't have to handle that here.
     *  
     * @param type The object that you return should be of that type.
     * @param valueStr The value to convert into an Object.
     * @return null if you cannot convert (or don't want to convert, because you expect your descendant of EntityUriAnalyzer.findEntity() to provide the object).
     */
    public Object convertSpecialType(Class<?> type, String valueStr);

}
