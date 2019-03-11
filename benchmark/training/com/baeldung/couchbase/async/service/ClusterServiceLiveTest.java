package com.baeldung.couchbase.async.service;


import com.baeldung.couchbase.async.AsyncIntegrationTest;
import com.baeldung.couchbase.async.AsyncIntegrationTestConfig;
import com.couchbase.client.java.Bucket;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AsyncIntegrationTestConfig.class })
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class })
public class ClusterServiceLiveTest extends AsyncIntegrationTest {
    @Autowired
    private ClusterService couchbaseService;

    private Bucket defaultBucket;

    @Test
    public void whenOpenBucket_thenBucketIsNotNull() throws Exception {
        defaultBucket = couchbaseService.openBucket("default", "");
        Assert.assertNotNull(defaultBucket);
        Assert.assertFalse(defaultBucket.isClosed());
        defaultBucket.close();
    }
}

