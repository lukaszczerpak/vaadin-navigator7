package org.vaadin.navigator7.uri;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.vaadin.navigator7.WebApplication;

import com.vaadin.ui.Component;

/** Introspection code for manipulating @Param annotated fields */
public class ParamInjector {

    //////////////////////////////// fragment (String) production //////////////////////////////////////////
    //////////////////////////////// fragment (String) production //////////////////////////////////////////
    //////////////////////////////// fragment (String) production //////////////////////////////////////////
    //////////////////////////////// fragment (String) production //////////////////////////////////////////
    //////////////////////////////// fragment (String) production //////////////////////////////////////////
    //////////////////////////////// fragment (String) production //////////////////////////////////////////
    //////////////////////////////// fragment (String) production //////////////////////////////////////////
    //////////////////////////////// fragment (String) production //////////////////////////////////////////
    //////////////////////////////// fragment (String) production //////////////////////////////////////////
    
    
    /** Called by ParamPageResource to get part of the URL 
     * posParam may not contain holes. example: a, b, d.
     * @Param(pos=0) String a;
     * @Param(pos=1) String b;
     * @Param(pos=3) String d;
     * This is is forbidden because there is no parameter to be placed at position 2
     * 
     * @param posParam contains the positional parameters in the correct order, but maybe with holes.*/
    public static String generateFragment(Class<? extends Component> pageClass, 
            Object[] posParams, Map<String, Object> namedParams) {

        List<Field> paramFields = findAnnotatedFields(pageClass, Param.class);

        ////// 1. We list the positional fields (and detect eventual duplicates) 
        SortedMap<Integer, Field> posFieldMap = new TreeMap<Integer, Field>();
        for(Field field : paramFields) {
            Param paramAnnotation = field.getAnnotation(Param.class);
            if (paramAnnotation.pos() > -1)  { // Positional field
                if (posFieldMap.get(paramAnnotation.pos()) != null) {  // Ooops, there is already a value for that position.
                    throw new RuntimeException("In class "+pageClass+", two fields annotated with @Param have the same position (@Param(pos=X))" +
                            posFieldMap.get(paramAnnotation.pos()) + " and " + field + ". " +
                            "Please change the position of one of them.");
                }
                // Remember the position and field.
                posFieldMap.put(paramAnnotation.pos(), field);
            }
        }
        
        /// 1.B We detect holes in the definition @Param(pos=...).
        List<Field> posFields = new ArrayList<Field>();  // We'll fill this:
        for (int pos : posFieldMap.keySet()) { // keySet is sorted.
            if (posFields.size() < pos) { // else they are equals.
                throw new RuntimeException("In class "+pageClass+", the field "+posFieldMap.get(pos)+" is annotated @Param(pos="+pos+"), " +
                		"but no other field is annotated with @Param(pos="+(pos-1)+"). " +
                        "These kind of position holes are forbidden.");
            }
            posFields.add(posFieldMap.get(pos));
        }
        
        /// 1.C We detect if too many parameters have been provided.
        if (posFieldMap.size() < posParams.length) {
            throw new RuntimeException("In class "+pageClass+", you have defined "+posFieldMap.size()+" field(s). "+
                    "But you provide "+posParams.length+" (more = too many) parameter values.");
        }
        
               
        ////// 2. We process the posParams to build the first half of the fragment.
        ParamUriAnalyzer paramUriAnalyzer = WebApplication.getCurrent().getUriAnalyzer();
        String fragment = null;
        for (int pos = 0; pos < posParams.length; pos++) {
            Object value = posParams[pos];
            Field field = posFieldMap.get(pos);
            Param paramAnnotation = field.getAnnotation(Param.class);
            String paramValueStr;
            
            if (value == null) {
                if (paramAnnotation.required()) {
                    throw new RuntimeException("In class "+pageClass+", the field "+ field +" is required at position "+pos
                           + " but you have provided a null value for that position.");
                }
                paramValueStr = "";  // In the resulting string, we'll have ".../previousVal//nextVal/..."
            } else {  // there is a value
                if (!field.getType().isAssignableFrom(value.getClass())) {  // Not compatible
                    throw new RuntimeException("In class "+pageClass+", the field "+ field
                            + " of type "+ field.getType() +" is not compatible with the provided parameter value "
                            + " which is of type "+value.getClass()+", and of value '"+value+"'.");
                }
                paramValueStr = convertObjectToString(value, field);
            }
            fragment = paramUriAnalyzer.addFragment(fragment, paramValueStr); 
        }
        
        
        
        ////// 3. We map the named fields of the class (and detect eventual name duplicates)
        Map<String, Field> namedFieldMap = new TreeMap<String, Field>();
        for(Field field : paramFields) {
            Param paramAnnotation = field.getAnnotation(Param.class);
            if (paramAnnotation.pos() == -1)  {  // No position found (i.e. not @Param(pos=3)) => Name based.
                String name = getParameterName(field, paramAnnotation);

                // Duplicate check.
                if (namedFieldMap.get(name) != null) {  // Ooops, there is already a value for that name.
                    throw new RuntimeException("In class "+pageClass+", two named fields (non positional) annotated with @Param " +
                    		"have the same name (" +name+ "): " +
                            namedFieldMap.get(paramAnnotation.pos()) + " and " + field + ". " +
                            "Did you forget to specify a position @Param(pos=...) on one of them?");
                }
                
                // Remember the name and field.
                namedFieldMap.put(name, field);
            }
        }
        
        // 3.B. Is there a required named field for which we provide no value?
        for (String name : namedFieldMap.keySet()) {
            Field field = namedFieldMap.get(name);
            Param paramAnnotation = field.getAnnotation(Param.class);
            if (paramAnnotation.required()) {
                if (namedParams == null || namedParams.get(name) == null) {
                    throw new RuntimeException("In class "+pageClass+", the named field " + field + 
                            " is required. But your provide no parameter with name "+name);
                }
            }
        }
        
        ////// 4. We process the namedParams to build the second half of the fragment.
        if (namedParams != null) {
            for (String name : namedParams.keySet()) {
                Field field = namedFieldMap.get(name);
                if (field == null) {
                    throw new RuntimeException(
                            "You provide a named parameter (name="+name+")," +
                            " but there is no @Param anotated named field with that name "+
                            "in class "+pageClass+".");

                }
                Object value = namedParams.get(name);

                String paramValueStr = convertObjectToString(value, field);
                fragment = paramUriAnalyzer.addFragment(fragment, name, paramValueStr); 
            }
        }
        
        return fragment;
    }



    private static String getParameterName(Field field, Param paramAnnotation) {
        String name;
        if (paramAnnotation.name().equals("")) {  // There is no explicit name in the annotation
            // Let's take the name of the field.
            name = field.getName();
        } else {  // There is an explicit name in the annotation @Param(name="thereIsAName")
            name = paramAnnotation.name();
        }
        return name;
    }

        
    
    /** Converts the @Param annotated fields to a string (URI fragment)
     * annotatedObject fields are supposed to contain correct values. */
    public static String generateFragment(Object annotatedObject) {
        try {
            // Scan annotated fields, and build a list (correct order for the result String) of values.

            // * @param posFieldMap Parameters that should be at a specified
            // position (i.e. @Param(pos=2)). The key is the position.
            // We don't use a List or array because we don't know the size
            // upfront but we would add values at any position (in any order).
            // => simple with Map.
            SortedMap<Integer, Field> posFieldMap = new TreeMap<Integer, Field>();

            // @param nameFieldList Parameters that should be associated with a
            // name (i.e. @Param(name="id");
            // We don't use a map because we want to preserve the definition
            // order of the field in the class (different from alphabetical by
            // name).
            // Each element is a new Object[2] array. First entry is the name.
            // Second entry is the Field.
            List<Object[]> nameFieldList = new ArrayList<Object[]>();

            // Complementary, we use a set to easily search already inserted names in nameValueList.
            // We need it to throw an exception if two fields have the same @Param name.
            Set<String> namesAlreadyInNameFieldList = new HashSet<String>();


            List<Field> paramFields = findAnnotatedFields(annotatedObject.getClass(), Param.class);
            for(Field field : paramFields) {
                field.setAccessible(true);  // Enable access to private fields.
                Param paramAnnotation = field.getAnnotation(Param.class);

                boolean required = paramAnnotation.required();
                if (required && !field.getType().isPrimitive() && field.get(annotatedObject)==null) {
                    throw new RuntimeException("Missing value for required field " + field + ". Please provide a (non null) value or user @Param(required=false).");
                }
                if (field.getType().isPrimitive() || field.get(annotatedObject)!=null) {  // There is a value

                    addFieldToCollections(annotatedObject.getClass(), posFieldMap,
                            nameFieldList, namesAlreadyInNameFieldList, field,
                            paramAnnotation);
                }
            }        

            
            
            ////// We have extracted the names and values in nameValueList and in posValueMap
            ////// Now we build the String fragment
            // We loop for the values of positionned field. When there is a "hole" (between 2 positions), we insert a named value.
            ParamUriAnalyzer paramUriAnalyzer = WebApplication.getCurrent().getUriAnalyzer();
            String fragment = "";  // We are appending values (sometimes with names) to it.
            int currentPosInFragment = 0;  // +1 each time we append a value to the fragment
            Iterator<Integer> posIter = posFieldMap.keySet().iterator();  // Positions of the positional params. .keySet() is ordered because it's a SortedMap.
            Iterator<Object[]> nameIter = nameFieldList.iterator(); // Iterator on the names parameters.
            while (posIter.hasNext() || nameIter.hasNext() ) {  // While some value has to be inserted in the fragment.
                Integer nextPos;
                if (posIter.hasNext()) {
                    nextPos = posIter.next();  // Position of next positionalParam
                } else {
                    nextPos = null;  // No more positional param
                }

                // Fill with named params until nextPos
                while ((nextPos == null && nameIter.hasNext())  // there is no positional param by well named param 
                        ||  (nextPos != null && currentPosInFragment < nextPos)) {  // We have a free place (non taken by a positional param)
                    // Let's insert a named parameter.
                    if (nameIter.hasNext()) {  // There is one more named param
                        Object[] nameValueArray = nameIter.next();
                        String valueStr = convertFieldToString(annotatedObject, (Field)nameValueArray[1]);
                        fragment = paramUriAnalyzer.addFragment(fragment, (String)nameValueArray[0], valueStr);
                        currentPosInFragment++;
                    } else {
                        throw new RuntimeException("No value to provide at position " + currentPosInFragment
                                + " (no positional parameter, and no more names parameter). It's a kind of 'hole' in your UriParam definition.");
                    }
                }

                // Add nextPos
                if (nextPos != null) {
                    fragment = paramUriAnalyzer.addFragment(fragment, 
                            convertFieldToString(annotatedObject, posFieldMap.get(nextPos)));
                    currentPosInFragment++;
                }
            }


            return fragment;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    

    private static void addFieldToCollections(Class annotatedClass,
            SortedMap<Integer, Field> posFieldMap,
            List<Object[]> nameFieldList,
            Set<String> namesAlreadyInNameFieldList, Field field,
            Param paramAnnotation) {
        // At a specific position ?
        if (paramAnnotation.pos() > -1) { // Greater than the default value => there is a pos attribute given in @Param(pos=3) => Yes, at a specific position.
            if (posFieldMap.get(paramAnnotation.pos()) != null) {  // Ooops, there is already a value for that position.
                throw new RuntimeException("In class "+annotatedClass+", two fields annotated with @Param have the same position (@Param(pos=X))" +
                        posFieldMap.get(paramAnnotation.pos()) + " and " + field + ". " +
                        "Please change the position of one of them.");
            }
            // Remember the name and value.
            posFieldMap.put(paramAnnotation.pos(), field);

        } else {  // No position found (i.e. not @Param(pos=3)) => Name based.
            String name;
            name = getParameterName(field, paramAnnotation);

            // Duplicate check.
            if(namesAlreadyInNameFieldList.contains(name)) {
                throw new RuntimeException("Fields having the same name (field name of @Param(name=...) value) '"+name+" found; including field " + field);
            }
            namesAlreadyInNameFieldList.add(name);

            // Remember name and value
            Object[] nameValueArray = new Object[2];
            nameValueArray[0] = name;
            nameValueArray[1] = field;
            nameFieldList.add(nameValueArray);
        }
    }


/////////////////////////////////////////////// Param from URI injection into page ////////////////////////////    
/////////////////////////////////////////////// Param from URI injection into page ////////////////////////////    
/////////////////////////////////////////////// Param from URI injection into page ////////////////////////////    
/////////////////////////////////////////////// Param from URI injection into page ////////////////////////////    
/////////////////////////////////////////////// Param from URI injection into page ////////////////////////////    
/////////////////////////////////////////////// Param from URI injection into page ////////////////////////////    
/////////////////////////////////////////////// Param from URI injection into page ////////////////////////////    
/////////////////////////////////////////////// Param from URI injection into page ////////////////////////////    
/////////////////////////////////////////////// Param from URI injection into page ////////////////////////////    
/////////////////////////////////////////////// Param from URI injection into page ////////////////////////////    
/////////////////////////////////////////////// Param from URI injection into page ////////////////////////////    
/////////////////////////////////////////////// Param from URI injection into page ////////////////////////////    
/////////////////////////////////////////////// Param from URI injection into page ////////////////////////////    
/////////////////////////////////////////////// Param from URI injection into page ////////////////////////////    
/////////////////////////////////////////////// Param from URI injection into page ////////////////////////////    
/////////////////////////////////////////////// Param from URI injection into page ////////////////////////////    
    
    /** 
     * @param cleanUpNonGivenParams true if we have to set to null parameters not given in the params String. Useful in case use stay on the page but change a param in the URI. We inject that new param but we also nullify others.
     * @return false if params not valid (user has been notified) */
    public static boolean verifyAndInjectParams(Component page, String params, boolean cleanUpNonGivenParams) {
        String problem = validateAndInject(page, params, cleanUpNonGivenParams);

        if (problem!=null) {
            ParamUriAnalyzer paramUriAnalyzer = WebApplication.getCurrent().getUriAnalyzer();
            paramUriAnalyzer.reportProblemWithFragment(problem, params);
            return false;
        }
        
        return problem == null;
    }


    
    /** Perform the annotation based validation, 
     * then (if no problem found) call extraValidate if annotatedObject implements ExtraValidator */ 
    static public String validateAndInject(Object annotatedObject, String fragment, boolean cleanUpNonGivenParams) {
        ParamUriAnalyzer paramUriAnalyzer = WebApplication.getCurrent().getUriAnalyzer();
        String problem = null;
        
        // Scan annotated fields
        List<Field> paramFields = findAnnotatedFields(annotatedObject.getClass(), Param.class);
        for(Field field : paramFields) {
            Param paramAnnotation = field.getAnnotation(Param.class);
            
            //// Extract the String (valueStr) for this param.
            String valueStr;
            if (fragment == null) {  // No parameter => no value
                valueStr = null;
            } else {
                if (paramAnnotation.pos() > -1) {  // Position provided
                    valueStr = paramUriAnalyzer.getString(fragment, paramAnnotation.pos());
                } else { // extraction based on name.
                    valueStr = paramUriAnalyzer.getString(fragment, getParameterName(field, paramAnnotation));
                }
            }
            
            //// Check required presence
            boolean required = paramAnnotation.required();
            if (required && valueStr==null) {
                problem = "Required value for parameter ";
                if (paramAnnotation.pos() > -1) {  // Position provided
                    problem += "at position " + paramAnnotation.pos();
                } else { // extraction based on name.
                    problem += "named '"+getParameterName(field, paramAnnotation)+"'";
                }
                problem += " not found.";
                return problem;
            }

            //// Convert that String into the excepted type.
            // The code below is probably much weaker than a specialized annotation library (that I cannot use before Vaadin7)
            if (valueStr!=null) { // If a value is given, we certainly assign.                  
                problem = convertAndAssignField(annotatedObject, field, valueStr);
                if (problem!=null) {
                    return problem;  // We stop here.
                }
            } else if (cleanUpNonGivenParams) { // if we have to cleanup, and there is no value to assign
                assignFieldToNullIfPossible(annotatedObject, field);
            }

            
        }
        
        if (annotatedObject instanceof ExtraValidator) {
            problem = ((ExtraValidator)annotatedObject).extraValidate(fragment);
        }
        return problem;  // Null in most cases (means no problem).
    }

    
    static private void assignFieldToNullIfPossible(Object o, Field field) {
        try {
            field.setAccessible(true);  // Enable access to private fields.
            field.set(o, null);
        } catch (Exception e) {
            // Do nothing, we tried but it may not be possible (i.e. a primitive type).
        }
    }


    /** 
     * 
     * @param field
     * @param valueStr should not be null
     * @return non null if problem (as string to int conversion problem).
     */
    // This method probably exists (and is more robust) in a reflection framework. Use the framework instead of the code below when Vaadin7 will have selected its reflection framework.
    static private String convertAndAssignField(Object o, Field field, String valueStr) {
        field.setAccessible(true);  // Enable access to private fields.
        Class type = field.getType();
        try {
            if (type.equals(int.class)) {
                field.setInt(o, Integer.parseInt(valueStr));
            } else if (type.equals(long.class)) {
                field.setLong(o, Long.parseLong(valueStr));
            } else if (type.equals(byte.class)) {
                field.setByte(o, Byte.parseByte(valueStr));
            } else if (type.equals(short.class)) {
                field.setShort(o, Short.parseShort(valueStr));
            } else if (type.equals(float.class)) {
                field.setFloat(o, Float.parseFloat(valueStr));
            } else if (type.equals(double.class)) {
                field.setDouble(o, Double.parseDouble(valueStr));
            } else if (type.equals(boolean.class)) {
                field.setBoolean(o, Boolean.parseBoolean(valueStr));
            } else {
                Object value;
                if (type.equals(String.class)) {
                    value = valueStr;
                } else if (type.equals(Integer.class)) {
                    value = new Integer(valueStr);
                } else if (type.equals(Long.class)) {
                    value = new Long(valueStr);
                } else if (type.equals(Byte.class)) {
                    value = new Byte(valueStr);
                } else if (type.equals(Float.class)) {
                    value = new Float(valueStr);
                } else if (type.equals(Double.class)) {
                    value = new Double(valueStr);
                } else if (type.equals(Boolean.class)) {
                    value = new Boolean(valueStr);
                } else {
                    // At this point, the field is no basic type
                    // => it is an entity, or it is a special field to be converted by the descendant of UriParam.
                    value = null;
                    
                    // Is it a special field that the object wants to convert?
                    if (o instanceof TypeConvertor) {
                        value = ((TypeConvertor)o).convertSpecialType(type, valueStr);
                        if (value !=null && !type.isAssignableFrom(value.getClass())) {
                            throw new RuntimeException("Your overriden (page).convertSpecialType method returned an object of type ("+value.getClass()+") incompatible with the expected type that we have provided as parameter ("+type+")");
                        }

                    }

                    
                    
                    if (value == null) { // No, descendant did not want to convert that

                        // Is it an application-wide special type (probably an entity)?
                        ParamUriAnalyzer paramUriAnalyzer = WebApplication.getCurrent().getUriAnalyzer();
                        value = paramUriAnalyzer.convertSpecialType(type, valueStr, null);   // Will probably call EntityUriAnalyzer.findEntity().

                        if (value !=null && !type.isAssignableFrom(value.getClass())) {
                            throw new RuntimeException("Your overriden (ParamUriAnalyzer).convertSpecialType method returned an object of type ("+value.getClass()+") incompatible with the expected type that we have provided as parameter ("+type+")");
                        }
                        
                    }

                    if(value == null) { // Still not converted. Let's try an enum conversion.
                        if (Enum.class.isAssignableFrom(type)) {  // Is it an enum.
                            try{
                                value = Enum.valueOf(type, valueStr);
                            } catch (Exception e) {
                                // Do nothing, just cannot convert and value remains null.
                            }
                        }
                    }
                    
                    if(value == null) {
                        return "Cannot convert value '"+valueStr+"' into type " + type;
                    }
                }
                field.set(o, value);
            }
        } catch (NumberFormatException e) {
            return "The value '"+valueStr+"' in URL is expected to be a number, but it seems not to be a valid number. (field " +field.getName()+")";
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Cannot convert or assign value '"+valueStr+"' into field "+ field + ". At this level it's not a simple conversion problem, it's a bug (impossible case?)");
        }
        return null;  // Success.
    }


    // This method probably exists (and is more robust) in a reflection framework. Use the framework instead of the code below when Vaadin7 will have selected its reflection framework.
    static public String convertFieldToString(Object o, Field field) {
        field.setAccessible(true);  // Enable access to private fields.
        Class type = field.getType();
        try {
            if (type.equals(int.class)) {
                return Integer.toString(field.getInt(o));
            } else if (type.equals(long.class)) {
                return Long.toString(field.getLong(o));
            } else if (type.equals(byte.class)) {
                return Byte.toString(field.getByte(o));
            } else if (type.equals(short.class)) {
                return Short.toString(field.getShort(o));
            } else if (type.equals(float.class)) {
                return Float.toString(field.getFloat(o));
            } else if (type.equals(double.class)) {
                return Double.toString(field.getDouble(o));
            } else if (type.equals(boolean.class)) {
                return Boolean.toString(field.getBoolean(o));
            } else if (type.equals(String.class) 
                 || type.equals(Integer.class)  
                 || type.equals(Long.class)  
                 || type.equals(Byte.class)  
                 || type.equals(Float.class)  
                 || type.equals(Double.class)  
                 || type.equals(Boolean.class)  
            ) { 
                return field.get(o).toString();
            } else {
                // At this point, the field is no basic type
                // => it is an entity, or it's something special (as an enum)
                Object value = field.get(o);

                String result = convertEntityToString(value);
                if (result != null) {
                    return result;
                } else {
                    return value.toString();
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Cannot get field '"+field+"' value. At this level it's not a simple conversion problem, it's a bug (impossible case?)");
        }
    }

    /** probably calls param.toString(), but checks before that it is compatible with the Field definition */
    // This method probably exists (and is more robust) in a reflection framework. Use the framework instead of the code below when Vaadin7 will have selected its reflection framework.
    static public String convertObjectToString(Object value, Field field) {
        field.setAccessible(true);  // Enable access to private fields.
        Class type = field.getType();
        if (! areTypesCompatible(type, value.getClass()) ) {
            throw new RuntimeException("Parameter value '"+value+"' provided for field '"+field+"' has no compatible type. " +
                    "Value type = "+value.getClass()+". Field type = "+type+
            		" It's probably a bug in your code (when creating a link to a ParamPage?).");
        }
        
        String result = convertEntityToString(value);
        if (result != null) {
            return result;
        } else {  // Case for all the basic types (Integer, String, Date,...)
            return value.toString();
        }

    }

    
    /** true if a value of type classSource could be assigned in a field of type classTarget */
    // This method probably exists (and is more robust) in a reflection framework. Use the framework instead of the code below when Vaadin7 will have selected its reflection framework.
    static private boolean areTypesCompatible(Class<?> classTarget, Class<?> classSource) {
        String s1 = classTarget.getSimpleName().toLowerCase();
        String s2 = classSource.getSimpleName().toLowerCase();
//        System.out.println("|"+s1 + "|  |"+s2+"|" + s1.equals(s2));
        return classTarget.isAssignableFrom(classSource) 
            || s1.equals(s2)  // Example "boolean" vs "Boolean".
            || ( classTarget.getSimpleName().equals("int") && classSource.getSimpleName().equals("Integer"))
            || ( classTarget.getSimpleName().equals("Integer") && classSource.getSimpleName().equals("int"))
            || ( classTarget.getSimpleName().equals("char") && classSource.getSimpleName().equals("Character"))
            || ( classTarget.getSimpleName().equals("Character") && classSource.getSimpleName().equals("char"));
    }

    /** returns null if the parameter is no entity (it could be a Double, for example)
     * returns null if this application has no EntityUriAnalyzer. */
    static protected String convertEntityToString(Object entity) {
        // Is it an entity?
        ParamUriAnalyzer paramUriAnalyzer = WebApplication.getCurrent().getUriAnalyzer();
        if (paramUriAnalyzer instanceof EntityUriAnalyzer<?>) { // This application supports entity retreival from params.
            EntityUriAnalyzer<?> entityUriAnalyzer = (EntityUriAnalyzer<?>)paramUriAnalyzer;
            try {
                return entityUriAnalyzer.getObjectEntityFragmentValue(entity);
            } catch(Exception e) {  // It was probably not an entity... (e is probably an InvalidClassCastException)
                // Do nothing, because maybe we should not have called the entityUriAnalyzer for that kind of field.
            }
        } 
        return null;
    }
    
    
    
    
    // Move/Change to use an annotation framework (the one that Vaadin7 will use)
    protected static List<Field> findAnnotatedFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        List<Field> fields = new ArrayList<Field>();
        while(!clazz.equals(Object.class)){
            for (Field f : clazz.getDeclaredFields()) {
                if (f.getAnnotation(annotationClass) != null) {
                    fields.add(f);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }



    public static boolean containsParamAnnotation(Class<? extends Component> pageClass) {
        List<Field> paramFields = findAnnotatedFields(pageClass, Param.class);
        return paramFields.size() > 0;
    }




}
