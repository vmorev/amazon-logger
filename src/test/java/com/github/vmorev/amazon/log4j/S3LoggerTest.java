package com.github.vmorev.amazon.log4j;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3LoggerTest {

    @Test
    public void testLogSave() {
        Logger log = LoggerFactory.getLogger("testLogSaveInstance");
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
