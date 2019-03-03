package com.heroku.base;

import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import static com.heroku.constants.TestConstant.*;

/***
 * Base class. To be extended by all test classes within framework.
 * It consists of common denominators used across all tests like loading config properties from resource.
 */
public class TestBase {

    public static Properties PROPS;
    private static Logger LOGGER = Logger.getLogger(TestBase.class.getName());

    public TestBase() {
        try {
            ClassLoader classLoader = TestBase.class.getClassLoader();
            PropertyConfigurator.configure(classLoader.getResource(LOG4J_PROPS_PATH));
            PROPS = new Properties();
            FileInputStream ip = new FileInputStream(classLoader.getResource(TEST_PROPS_PATH).getFile());
            PROPS.load(ip);
            LOGGER.info("Test properties loaded!");
        } catch (IOException e) {
            LOGGER.error("Failed to load Test Properties");
        }

    }
}

