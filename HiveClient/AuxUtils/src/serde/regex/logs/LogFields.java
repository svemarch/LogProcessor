package serde.regex.logs;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 4/24/12
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class LogFields {
    protected static Map<String, String> fieldPatterns = new HashMap<String, String>();
    protected static Map<String, String> fieldFormats = new HashMap<String, String>();
    
    public boolean checkFieldExists(String fieldName) {
        return (fieldPatterns.containsKey(fieldName.toLowerCase()) &&
                    fieldFormats.containsKey(fieldName.toLowerCase()));
    }
    
    public String getFieldPattern(String fieldName) {
        return fieldPatterns.get(fieldName.toLowerCase());
    }
    
    public String getFieldFormat(String fieldName) {
        return fieldFormats.get(fieldName.toLowerCase());
    }

}
