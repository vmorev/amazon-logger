package com.github.vmorev.amazon.log4j.support;

import com.amazonaws.services.s3.AmazonS3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: Valentin_Morev
 * Date: 06.02.13
 */
public class LogCache {
    private static volatile LogCache instance;

    private List<LogCacheLine> logLines;
    private boolean isOpen;
    private AWSHelper helper;
    private int batchSize;

    public static LogCache getInstance() {
        LogCache tmpInstance = instance;
        if (tmpInstance == null) {
            synchronized (LogCache.class) {
                tmpInstance = instance;
                if (tmpInstance == null) {
                    instance = tmpInstance = new LogCache();
                }
            }
        }
        return tmpInstance;
    }

    private class ShutDownHook extends Thread {
        public void run() {
            close();
            System.out.println(this.getClass().getName() + " all logs were saved");
        }
    }

    private class MessagesPusher extends Thread {
        private Collection<LogCacheLine> logLines2Sync;

        public void setLogLines2Sync(Collection<LogCacheLine> logLines2Sync) {
            this.logLines2Sync = logLines2Sync;
        }

        public void run() {
            if (logLines2Sync != null && logLines2Sync.size() > 0) {
                String key = helper.getS3LogBucket() + "-" + logLines.size() + "-" + System.currentTimeMillis() + ".json";
                try {
                    helper.saveS3Object(helper.getS3LogBucket(), key, logLines);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public LogCache() {
        logLines = new ArrayList<>();
        Runtime.getRuntime().addShutdownHook(new ShutDownHook());
    }

    public void setHelper(AWSHelper helper) {
        this.helper = helper;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    private void init() {
        try {
            if (helper == null)
                throw new Exception("AWS connection was not established");

            AmazonS3 s3 = helper.getS3Client();
            if (!s3.doesBucketExist(helper.getS3LogBucket()))
                s3.createBucket(helper.getS3LogBucket());

            isOpen = true;
        } catch (Exception e) {
            e.printStackTrace();
            isOpen = false;
        }
    }

    private void pushMessages() {
        if (!isOpen)
            init();

        MessagesPusher pusher = new MessagesPusher();
        pusher.setLogLines2Sync(Collections.unmodifiableCollection(logLines));
        pusher.start();

        logLines = new ArrayList<>();
    }

    public void addMessage(LogCacheLine line) {
        logLines.add(line);

        if (logLines.size() >= batchSize)
            pushMessages();
    }

    public void close() {
        if (logLines.size() > 0)
            pushMessages();
    }

}
