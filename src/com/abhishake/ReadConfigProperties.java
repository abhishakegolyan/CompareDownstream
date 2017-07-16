package com.abhishake;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by abhishakegolyan on 16/7/17.
 */
public class ReadConfigProperties {

    int failcountmax;
    int readbufferlines;
    String comparelogfilename;
    String compareSoucefilename;


    public int getReadbufferlines() throws IOException {
        Properties prop = new Properties();
        InputStream input = null;

        input = new FileInputStream("config.properties");

        // load a properties file
        prop.load(input);

        return Integer.parseInt(prop.getProperty("readbufferlines"));
    }

    public int getFailcountmax() throws IOException {
        Properties prop = new Properties();
        InputStream input = null;

        input = new FileInputStream("config.properties");

        // load a properties file
        prop.load(input);

        return Integer.parseInt(prop.getProperty("failcountmax"));
    }
    public String getCompareSoucefilenames() throws IOException {
        Properties prop = new Properties();
        InputStream input = null;

        input = new FileInputStream("config.properties");

        // load a properties file
        prop.load(input);

        return prop.getProperty("compareSoucefilename");
    }

    public String getComparelogfilename() throws IOException {
        Properties prop = new Properties();
        InputStream input = null;

        input = new FileInputStream("config.properties");

        // load a properties file
        prop.load(input);

        return prop.getProperty("comparelogfilename");
    }

}