package org.vaadin.navigator7.uri;

public interface ExtraValidator {
    
    /** Override me (seldom, not usual) to provide a complex validation logic as "if param1 is not provided, then param2 is required".
     * Returning a non null Strings stops the navigation logic and displays the message.
     * This method is called after parameter valued injection in fields => you probably use fields values and don't user the provided fragment parameter.
     *  
     * @param fragment may be useless if you validate on the injected fields.
     * @return null if no problem. Message to show to user in case of validation error. */ 
    public String extraValidate(String fragment);
    
}
