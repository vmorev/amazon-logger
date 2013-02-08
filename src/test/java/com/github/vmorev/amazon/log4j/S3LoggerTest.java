package com.github.vmorev.amazon.log4j;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import java.io.IOException;
import java.util.Properties;

public class S3LoggerTest {
    private static Logger log;

    @BeforeClass
    public static void setUpClass() {
        Properties props = new Properties();
        try {
            props.load(ClassLoader.getSystemResource("log4j.local.properties").openStream());
            if (!props.isEmpty()) {
                LogManager.resetConfiguration();
                PropertyConfigurator.configure(props);
            }
            log = LoggerFactory.getLogger("S3LoggerTest");
        } catch (IOException e) {
            //just skip, no local config exist
        }
    }

    @Test
    public void testLogSave() {
        log.error("Test exception ", new Exception());
        for (int i = 0; i < 15; i++)
            log.error("This is a test error " + i);
        for (int i = 0; i < 15; i++)
            log.warn("This is a test warn " + i);
        for (int i = 0; i < 15; i++)
            log.info("This is a test info " + i);
        for (int i = 0; i < 15; i++)
            log.debug("This is a test debug " + i);
        for (int i = 0; i < 15; i++)
            log.trace("This is a test trace " + i);
    }
}
