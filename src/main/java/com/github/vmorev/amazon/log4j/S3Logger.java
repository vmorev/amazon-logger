package com.github.vmorev.amazon.log4j;

import com.github.vmorev.amazon.log4j.support.AWSHelper;
import com.github.vmorev.amazon.log4j.support.LogCache;
import com.github.vmorev.amazon.log4j.support.LogCacheLine;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * User: Valentin_Morev
 * Date: 06.02.13
 */
public class S3Logger extends AppenderSkeleton {
    private LogCache cache;
    private String accessKey;
    private String secretKey;
    private String s3logBucket;
    private int batchSize;

    public void setBatchSize(String batchSize) {
        this.batchSize = OptionConverter.toInt("".equals(batchSize) ? null : batchSize, 1);
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setS3logBucket(String s3logBucket) {
        this.s3logBucket = s3logBucket;
    }

    protected void append(LoggingEvent event) {
        if(cache == null) {
            cache = LogCache.getInstance();
            AWSHelper helper = new AWSHelper(accessKey, secretKey);
            helper.setS3logBucket(s3logBucket);
            cache.setHelper(helper);
            cache.setBatchSize(batchSize);
        }

        LogCacheLine line = new LogCacheLine();
        line.setMessage((layout != null) ? layout.format(event) : event.getRenderedMessage());
        line.setLevel(event.getLevel().toString());
        line.setOriginator(event.getLocationInformation().fullInfo);
        line.setStackTrace(event.getThrowableStrRep());
        line.setTimeStamp(event.getTimeStamp());
        cache.addMessage(line);
    }

    public void close() {
        if(cache != null)
            cache.close();
    }

    public boolean requiresLayout() {
        return false;
    }
}
