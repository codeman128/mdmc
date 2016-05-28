package com.pk.publisher.testutils;

import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by PavelK on 5/28/2016.
 */
public class Utils {

    public static Properties loadConfig (String name) throws Exception {
        Properties properties = null;
        String path = null;
        try {
            path = System.getProperty("user.dir").replace("\\", "/");
            path = path + "/"+name+".cnfg";
            System.out.println("Load configuration: " + path);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        FileInputStream file = new FileInputStream(path);
        properties = new Properties();
        properties.load(file);
        file.close();

        System.out.println("-------------------------------------------------------------------------");
        Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = (String)properties.get(key);
            System.out.println(key + ": " + value);
        }
        System.out.println("-------------------------------------------------------------------------");
        return properties;
    }



}
