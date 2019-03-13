package com.netflix.config.sources;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.netflix.config.PollResult;
import com.sun.net.httpserver.HttpServer;
import org.junit.Assert;
import org.junit.Test;


public class S3ConfigurationSourceTest {
    static final boolean INITIAL = false;

    static final Object CHECK_POINT = null;

    HttpServer fakeS3;

    AmazonS3Client client;

    public S3ConfigurationSourceTest() {
    }

    @Test
    public void testPoll_shouldLoadSomeData() throws Exception {
        S3ConfigurationSource instance = new S3ConfigurationSource(client, "bucketname", "standard-key.txt");
        PollResult result = instance.poll(S3ConfigurationSourceTest.INITIAL, S3ConfigurationSourceTest.CHECK_POINT);
        Assert.assertNotNull(result);
        Assert.assertEquals("true", result.getComplete().get("loaded"));
        Assert.assertEquals(1, result.getComplete().size());
    }

    @Test(expected = AmazonServiceException.class)
    public void testPoll_fileNotFound() throws Exception {
        S3ConfigurationSource instance = new S3ConfigurationSource(client, "bucketname", "404.txt");
        PollResult result = instance.poll(S3ConfigurationSourceTest.INITIAL, S3ConfigurationSourceTest.CHECK_POINT);
        Assert.assertNotNull(result);
        Assert.assertEquals("true", result.getComplete().get("loaded"));
        Assert.assertEquals(1, result.getComplete().size());
    }
}

