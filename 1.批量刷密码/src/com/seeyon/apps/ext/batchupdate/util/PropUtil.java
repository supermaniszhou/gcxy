package com.seeyon.apps.ext.batchupdate.util;

import java.io.*;
import java.util.Properties;

public class PropUtil {

    private Properties p;

    public PropUtil() {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        File file = new File(path, "config/pwd.properties");
        InputStream is = null;
        try {
            p = new Properties();
            is = new FileInputStream(file);
            p.load(new InputStreamReader(is, "UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getValueByKey(String key) {
        return p.getProperty(key);
    }

    public Properties getP() {
        return p;
    }

    public void setP(Properties p) {
        this.p = p;
    }
}
