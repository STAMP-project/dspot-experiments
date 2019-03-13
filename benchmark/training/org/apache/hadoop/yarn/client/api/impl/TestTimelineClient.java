/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.client.api.impl;


import ClientResponse.Status.FORBIDDEN;
import ClientResponse.Status.INTERNAL_SERVER_ERROR;
import ClientResponse.Status.OK;
import CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION;
import Policy.HTTPS_ONLY;
import TimelinePutResponse.TimelinePutError.IO_EXCEPTION;
import YarnConfiguration.TIMELINE_SERVICE_CLIENT_MAX_RETRIES;
import YarnConfiguration.TIMELINE_SERVICE_CLIENT_RETRY_INTERVAL_MS;
import YarnConfiguration.TIMELINE_SERVICE_ENABLED;
import YarnConfiguration.YARN_HTTP_POLICY_KEY;
import com.sun.jersey.api.client.ClientHandlerException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import org.apache.hadoop.yarn.api.records.timeline.TimelinePutResponse;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.security.client.TimelineDelegationTokenIdentifier;
import org.junit.Assert;
import org.junit.Test;


public class TestTimelineClient {
    private TimelineClientImpl client;

    private TimelineWriter spyTimelineWriter;

    private String keystoresDir;

    private String sslConfDir;

    @Test
    public void testPostEntities() throws Exception {
        TestTimelineClient.mockEntityClientResponse(spyTimelineWriter, OK, false, false);
        try {
            TimelinePutResponse response = client.putEntities(TestTimelineClient.generateEntity());
            Assert.assertEquals(0, response.getErrors().size());
        } catch (YarnException e) {
            Assert.fail("Exception is not expected");
        }
    }

    @Test
    public void testPostEntitiesWithError() throws Exception {
        TestTimelineClient.mockEntityClientResponse(spyTimelineWriter, OK, true, false);
        try {
            TimelinePutResponse response = client.putEntities(TestTimelineClient.generateEntity());
            Assert.assertEquals(1, response.getErrors().size());
            Assert.assertEquals("test entity id", response.getErrors().get(0).getEntityId());
            Assert.assertEquals("test entity type", response.getErrors().get(0).getEntityType());
            Assert.assertEquals(IO_EXCEPTION, response.getErrors().get(0).getErrorCode());
        } catch (YarnException e) {
            Assert.fail("Exception is not expected");
        }
    }

    @Test
    public void testPostIncompleteEntities() throws Exception {
        try {
            client.putEntities(new TimelineEntity());
            Assert.fail("Exception should have been thrown");
        } catch (YarnException e) {
        }
    }

    @Test
    public void testPostEntitiesNoResponse() throws Exception {
        TestTimelineClient.mockEntityClientResponse(spyTimelineWriter, INTERNAL_SERVER_ERROR, false, false);
        try {
            client.putEntities(TestTimelineClient.generateEntity());
            Assert.fail("Exception is expected");
        } catch (YarnException e) {
            Assert.assertTrue(e.getMessage().contains("Failed to get the response from the timeline server."));
        }
    }

    @Test
    public void testPostEntitiesConnectionRefused() throws Exception {
        TestTimelineClient.mockEntityClientResponse(spyTimelineWriter, null, false, true);
        try {
            client.putEntities(TestTimelineClient.generateEntity());
            Assert.fail("RuntimeException is expected");
        } catch (RuntimeException re) {
            Assert.assertTrue((re instanceof ClientHandlerException));
        }
    }

    @Test
    public void testPutDomain() throws Exception {
        TestTimelineClient.mockDomainClientResponse(spyTimelineWriter, OK, false);
        try {
            client.putDomain(TestTimelineClient.generateDomain());
        } catch (YarnException e) {
            Assert.fail("Exception is not expected");
        }
    }

    @Test
    public void testPutDomainNoResponse() throws Exception {
        TestTimelineClient.mockDomainClientResponse(spyTimelineWriter, FORBIDDEN, false);
        try {
            client.putDomain(TestTimelineClient.generateDomain());
            Assert.fail("Exception is expected");
        } catch (YarnException e) {
            Assert.assertTrue(e.getMessage().contains("Failed to get the response from the timeline server."));
        }
    }

    @Test
    public void testPutDomainConnectionRefused() throws Exception {
        TestTimelineClient.mockDomainClientResponse(spyTimelineWriter, null, true);
        try {
            client.putDomain(TestTimelineClient.generateDomain());
            Assert.fail("RuntimeException is expected");
        } catch (RuntimeException re) {
            Assert.assertTrue((re instanceof ClientHandlerException));
        }
    }

    @Test
    public void testCheckRetryCount() throws Exception {
        try {
            YarnConfiguration conf = new YarnConfiguration();
            conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
            conf.setInt(TIMELINE_SERVICE_CLIENT_MAX_RETRIES, (-2));
            createTimelineClient(conf);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains(TIMELINE_SERVICE_CLIENT_MAX_RETRIES));
        }
        try {
            YarnConfiguration conf = new YarnConfiguration();
            conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
            conf.setLong(TIMELINE_SERVICE_CLIENT_RETRY_INTERVAL_MS, 0);
            createTimelineClient(conf);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains(TIMELINE_SERVICE_CLIENT_RETRY_INTERVAL_MS));
        }
        int newMaxRetries = 5;
        long newIntervalMs = 500;
        YarnConfiguration conf = new YarnConfiguration();
        conf.setInt(TIMELINE_SERVICE_CLIENT_MAX_RETRIES, newMaxRetries);
        conf.setLong(TIMELINE_SERVICE_CLIENT_RETRY_INTERVAL_MS, newIntervalMs);
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        TimelineClientImpl client = createTimelineClient(conf);
        try {
            // This call should fail because there is no timeline server
            client.putEntities(TestTimelineClient.generateEntity());
            Assert.fail(("Exception expected! " + "Timeline server should be off to run this test. "));
        } catch (RuntimeException ce) {
            Assert.assertTrue(("Handler exception for reason other than retry: " + (ce.getMessage())), ce.getMessage().contains("Connection retries limit exceeded"));
            // we would expect this exception here, check if the client has retried
            Assert.assertTrue("Retry filter didn't perform any retries! ", client.connector.connectionRetry.getRetired());
        }
    }

    @Test
    public void testDelegationTokenOperationsRetry() throws Exception {
        int newMaxRetries = 5;
        long newIntervalMs = 500;
        YarnConfiguration conf = new YarnConfiguration();
        conf.setInt(TIMELINE_SERVICE_CLIENT_MAX_RETRIES, newMaxRetries);
        conf.setLong(TIMELINE_SERVICE_CLIENT_RETRY_INTERVAL_MS, newIntervalMs);
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        // use kerberos to bypass the issue in HADOOP-11215
        conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        UserGroupInformation.setConfiguration(conf);
        TimelineClientImpl client = createTimelineClient(conf);
        TimelineClientImpl clientFake = createTimelineClientFakeTimelineClientRetryOp(conf);
        TestTimelineClient.TestTimelineDelegationTokenSecretManager dtManager = new TestTimelineClient.TestTimelineDelegationTokenSecretManager();
        try {
            startThreads();
            Thread.sleep(3000);
            try {
                // try getting a delegation token
                client.getDelegationToken(UserGroupInformation.getCurrentUser().getShortUserName());
                TestTimelineClient.assertFail();
            } catch (RuntimeException ce) {
                assertException(client, ce);
            }
            try {
                // try renew a delegation token
                TimelineDelegationTokenIdentifier timelineDT = new TimelineDelegationTokenIdentifier(new Text("tester"), new Text("tester"), new Text("tester"));
                client.renewDelegationToken(new org.apache.hadoop.security.token.Token<TimelineDelegationTokenIdentifier>(timelineDT.getBytes(), dtManager.createPassword(timelineDT), timelineDT.getKind(), new Text("0.0.0.0:8188")));
                TestTimelineClient.assertFail();
            } catch (RuntimeException ce) {
                assertException(client, ce);
            }
            try {
                // try cancel a delegation token
                TimelineDelegationTokenIdentifier timelineDT = new TimelineDelegationTokenIdentifier(new Text("tester"), new Text("tester"), new Text("tester"));
                client.cancelDelegationToken(new org.apache.hadoop.security.token.Token<TimelineDelegationTokenIdentifier>(timelineDT.getBytes(), dtManager.createPassword(timelineDT), timelineDT.getKind(), new Text("0.0.0.0:8188")));
                TestTimelineClient.assertFail();
            } catch (RuntimeException ce) {
                assertException(client, ce);
            }
            // Test DelegationTokenOperationsRetry on SocketTimeoutException
            try {
                TimelineDelegationTokenIdentifier timelineDT = new TimelineDelegationTokenIdentifier(new Text("tester"), new Text("tester"), new Text("tester"));
                clientFake.cancelDelegationToken(new org.apache.hadoop.security.token.Token<TimelineDelegationTokenIdentifier>(timelineDT.getBytes(), dtManager.createPassword(timelineDT), timelineDT.getKind(), new Text("0.0.0.0:8188")));
                TestTimelineClient.assertFail();
            } catch (RuntimeException ce) {
                assertException(clientFake, ce);
            }
        } finally {
            client.stop();
            clientFake.stop();
            stopThreads();
        }
    }

    @Test
    public void testTimelineClientCleanup() throws Exception {
        YarnConfiguration conf = new YarnConfiguration();
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        conf.setInt(TIMELINE_SERVICE_CLIENT_MAX_RETRIES, 0);
        conf.set(YARN_HTTP_POLICY_KEY, HTTPS_ONLY.name());
        setupSSLConfig(conf);
        client = createTimelineClient(conf);
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        while ((threadGroup.getParent()) != null) {
            threadGroup = threadGroup.getParent();
        } 
        Thread[] threads = new Thread[threadGroup.activeCount()];
        threadGroup.enumerate(threads);
        Thread reloaderThread = null;
        for (Thread thread : threads) {
            if (((thread.getName()) != null) && (thread.getName().contains("Truststore reloader thread"))) {
                reloaderThread = thread;
            }
        }
        Assert.assertTrue("Reloader is not alive", reloaderThread.isAlive());
        client.close();
        boolean reloaderStillAlive = true;
        for (int i = 0; i < 10; i++) {
            reloaderStillAlive = reloaderThread.isAlive();
            if (!reloaderStillAlive) {
                break;
            }
            Thread.sleep(1000);
        }
        Assert.assertFalse("Reloader is still alive", reloaderStillAlive);
    }

    private static class TestTimelineDelegationTokenSecretManager extends AbstractDelegationTokenSecretManager<TimelineDelegationTokenIdentifier> {
        public TestTimelineDelegationTokenSecretManager() {
            super(100000, 100000, 100000, 100000);
        }

        @Override
        public TimelineDelegationTokenIdentifier createIdentifier() {
            return new TimelineDelegationTokenIdentifier();
        }

        @Override
        public synchronized byte[] createPassword(TimelineDelegationTokenIdentifier identifier) {
            return super.createPassword(identifier);
        }
    }
}

