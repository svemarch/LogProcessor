package ru.aptu.warehouse.tester.services;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/23/12
 * Time: 3:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoggerPropertyReader {
    public static LoggerProperty read(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file)); 
        String line = null;
        Map<String, String> values = new HashMap<String, String>();
        while ((line = reader.readLine()) != null) {
            String tokens[] = line.split("=");
            values.put(tokens[0], tokens.length > 1 ? tokens[1] : "");
        }
        reader.close();
        return new LoggerProperty(values);
    }
}
