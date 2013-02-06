package com.github.vmorev.amazon.log4j.support;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: Valentin_Morev
 * Date: 21.01.13
 */
public class AWSHelper {

    private String accessKey;
    private String secretKey;
    private String s3logBucket;
    private AWSCredentials credentials;
    private AmazonS3 s3client;

    public AWSHelper(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public void setS3logBucket(String s3logBucket) {
        this.s3logBucket = s3logBucket;
    }

    public String getS3LogBucket() {
        return s3logBucket;
    }

    private AWSCredentials getCredentials() {
        if (credentials == null)
            credentials = new BasicAWSCredentials(accessKey, secretKey);
        return credentials;
    }

    public AmazonS3 getS3Client() throws IOException {
        if (s3client == null)
            s3client = new AmazonS3Client(getCredentials());
        return s3client;
    }

    public <T> void saveS3Object(String bucket, String key, T obj) throws IOException {
        saveS3Object(bucket, key, obj, new ObjectMetadata());
    }

    public <T> void saveS3Object(String bucket, String key, T obj, ObjectMetadata metadata) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inStream = new ByteArrayInputStream(mapper.writeValueAsBytes(obj));
        metadata.setContentLength(inStream.available());
        getS3Client().putObject(bucket, key, inStream, metadata);
    }
}
