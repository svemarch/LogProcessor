package ru.aptu.warehouse.tester.services;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/23/12
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoggerProperty {
    Map<String, String> properties = new HashMap<String, String>();

    public LoggerProperty(Map<String, String> properties) {
        this.properties = properties;
    }

    public LoggerProperty() {
        properties.put("db.url", "jdbc:mysql://localhost/warehouse_tester");
        properties.put("db.username", "root");
        properties.put("db.pwd", "");
        properties.put("db.driver.class", "org.gjt.mm.mysql.Driver");
    }
    
    public String get(String propertyName) {
        return properties.get(propertyName);
    }
}
