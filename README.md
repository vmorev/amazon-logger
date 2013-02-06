Log4j appender which saves logs to Amazon.
============

Current implementation is quite simple and supports only S3 as a storage. 
It batches messages as per configurable batch size and store each batch to separate file in S3 bucket.

My task list is:
- [ ] separate thread to push messages to server
- [ ] shutdown hook to save messages on application termination
- [ ] error handler instead of e.printStackTrace()
- [ ] another Amazon services support like SNS, SQS, SDB, DynamoDB

Feel free to create pull requests or issues :)

Log4j configuration example:

```properties
log4j.rootLogger=WARN, stdout, s3logger

log4j.appender.s3logger=com.github.vmorev.amazon.log4j.S3Logger
log4j.appender.s3logger.accessKey=YOUR_KEY
log4j.appender.s3logger.secretKey=YOUR_SECRET_KEY
log4j.appender.s3logger.s3logBucket=YOUR_LOG_BUCKET
log4j.appender.s3logger.batchSize=10000

log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d %p [%c] %m%n
```
