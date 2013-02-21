package com.github.vmorev.amazon.log4j;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.github.vmorev.amazon.AmazonService;
import com.github.vmorev.amazon.S3Bucket;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

public class S3LoggerTest {
    private static Logger log;
    private static Properties props;
    private S3Bucket bucket;

    @BeforeClass
    public static void setUpClass() {
        props = new Properties();
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

    @Before
    public void setUp() {
        bucket = new S3Bucket().withCredentials(props.getProperty("log4j.appender.s3logger.accessKey"), props.getProperty("log4j.appender.s3logger.secretKey"))
                .withName(props.getProperty("log4j.appender.s3logger.s3logBucket"));
        bucket.createBucket();
    }

    @After
    public void cleanUp() {
        bucket.deleteBucket();
    }

    @Test
    public void testLogSave() throws Exception {

        log.error("Test exception", new Exception());
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

        long tryCount = 0;
        long objCount;
        long exceptedFilesCount = 31 / Long.valueOf(props.getProperty("log4j.appender.s3logger.batchSize"));
        do {
            final long[] count = new long[1];
            bucket.listObjectSummaries(new AmazonService.ListFunc<S3ObjectSummary>() {
                public void process(S3ObjectSummary obj) throws Exception {
                    count[0] += 1;
                }
            });
            objCount = count[0];
            tryCount++;
            synchronized (this) {
                wait(3000);
            }
        } while (objCount < exceptedFilesCount && tryCount < 3);

        assertTrue(exceptedFilesCount == objCount || (exceptedFilesCount - 1) == objCount);
    }
}
