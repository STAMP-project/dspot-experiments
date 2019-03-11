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


import TimelineDelegationTokenIdentifier.KIND_NAME;
import YarnConfiguration.TIMELINE_SERVICE_CLIENT_MAX_RETRIES;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.CollectorInfo;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineEntities;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineEntity;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.security.client.TimelineDelegationTokenIdentifier;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestTimelineClientV2Impl {
    private static final Logger LOG = LoggerFactory.getLogger(TestTimelineClientV2Impl.class);

    private TestTimelineClientV2Impl.TestV2TimelineClient client;

    private static final long TIME_TO_SLEEP = 150L;

    private static final String EXCEPTION_MSG = "Exception in the content";

    @Rule
    public TestName currTestName = new TestName();

    private YarnConfiguration conf;

    private class TestV2TimelineClientForExceptionHandling extends TimelineV2ClientImpl {
        public TestV2TimelineClientForExceptionHandling(ApplicationId id) {
            super(id);
        }

        private boolean throwYarnException;

        public void setThrowYarnException(boolean throwYarnException) {
            this.throwYarnException = throwYarnException;
        }

        public boolean isThrowYarnException() {
            return throwYarnException;
        }

        @Override
        protected void putObjects(URI base, String path, MultivaluedMap<String, String> params, Object obj) throws IOException, YarnException {
            if (throwYarnException) {
                throw new YarnException(TestTimelineClientV2Impl.EXCEPTION_MSG);
            } else {
                throw new IOException("Failed to get the response from the timeline server.");
            }
        }
    }

    private class TestV2TimelineClient extends TestTimelineClientV2Impl.TestV2TimelineClientForExceptionHandling {
        private boolean sleepBeforeReturn;

        private List<TimelineEntities> publishedEntities;

        public TimelineEntities getPublishedEntities(int putIndex) {
            Assert.assertTrue("Not So many entities Published", (putIndex < (publishedEntities.size())));
            return publishedEntities.get(putIndex);
        }

        public void setSleepBeforeReturn(boolean sleepBeforeReturn) {
            this.sleepBeforeReturn = sleepBeforeReturn;
        }

        public int getNumOfTimelineEntitiesPublished() {
            return publishedEntities.size();
        }

        public TestV2TimelineClient(ApplicationId id) {
            super(id);
            publishedEntities = new ArrayList<TimelineEntities>();
        }

        protected void putObjects(String path, MultivaluedMap<String, String> params, Object obj) throws IOException, YarnException {
            if (isThrowYarnException()) {
                throw new YarnException("ActualException");
            }
            publishedEntities.add(((TimelineEntities) (obj)));
            if (sleepBeforeReturn) {
                try {
                    Thread.sleep(TestTimelineClientV2Impl.TIME_TO_SLEEP);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Test
    public void testExceptionMultipleRetry() {
        TestTimelineClientV2Impl.TestV2TimelineClientForExceptionHandling c = new TestTimelineClientV2Impl.TestV2TimelineClientForExceptionHandling(ApplicationId.newInstance(0, 0));
        int maxRetries = 2;
        conf.setInt(TIMELINE_SERVICE_CLIENT_MAX_RETRIES, maxRetries);
        c.init(conf);
        start();
        c.setTimelineCollectorInfo(CollectorInfo.newInstance("localhost:12345"));
        try {
            c.putEntities(new TimelineEntity());
        } catch (IOException e) {
            Assert.fail("YARN exception is expected");
        } catch (YarnException e) {
            Throwable cause = e.getCause();
            Assert.assertTrue("IOException is expected", (cause instanceof IOException));
            Assert.assertTrue("YARN exception is expected", cause.getMessage().contains(("TimelineClient has reached to max retry times : " + maxRetries)));
        }
        c.setThrowYarnException(true);
        try {
            c.putEntities(new TimelineEntity());
        } catch (IOException e) {
            Assert.fail("YARN exception is expected");
        } catch (YarnException e) {
            Throwable cause = e.getCause();
            Assert.assertTrue("YARN exception is expected", (cause instanceof YarnException));
            Assert.assertTrue("YARN exception is expected", cause.getMessage().contains(TestTimelineClientV2Impl.EXCEPTION_MSG));
        }
        stop();
    }

    @Test
    public void testPostEntities() throws Exception {
        try {
            client.putEntities(TestTimelineClientV2Impl.generateEntity("1"));
        } catch (YarnException e) {
            Assert.fail("Exception is not expected");
        }
    }

    @Test
    public void testASyncCallMerge() throws Exception {
        client.setSleepBeforeReturn(true);
        try {
            client.putEntitiesAsync(TestTimelineClientV2Impl.generateEntity("1"));
            Thread.sleep(((TestTimelineClientV2Impl.TIME_TO_SLEEP) / 2));
            // by the time first put response comes push 2 entities in the queue
            client.putEntitiesAsync(TestTimelineClientV2Impl.generateEntity("2"));
            client.putEntitiesAsync(TestTimelineClientV2Impl.generateEntity("3"));
        } catch (YarnException e) {
            Assert.fail("Exception is not expected");
        }
        for (int i = 0; i < 4; i++) {
            if ((client.getNumOfTimelineEntitiesPublished()) == 2) {
                break;
            }
            Thread.sleep(TestTimelineClientV2Impl.TIME_TO_SLEEP);
        }
        Assert.assertEquals("two merged TimelineEntities needs to be published", 2, client.getNumOfTimelineEntitiesPublished());
        TimelineEntities secondPublishedEntities = client.getPublishedEntities(1);
        Assert.assertEquals("Merged TimelineEntities Object needs to 2 TimelineEntity Object", 2, secondPublishedEntities.getEntities().size());
        Assert.assertEquals("Order of Async Events Needs to be FIFO", "2", secondPublishedEntities.getEntities().get(0).getId());
        Assert.assertEquals("Order of Async Events Needs to be FIFO", "3", secondPublishedEntities.getEntities().get(1).getId());
    }

    @Test
    public void testSyncCall() throws Exception {
        try {
            // sync entity should not be be merged with Async
            client.putEntities(TestTimelineClientV2Impl.generateEntity("1"));
            client.putEntitiesAsync(TestTimelineClientV2Impl.generateEntity("2"));
            client.putEntitiesAsync(TestTimelineClientV2Impl.generateEntity("3"));
            // except for the sync call above 2 should be merged
            client.putEntities(TestTimelineClientV2Impl.generateEntity("4"));
        } catch (YarnException e) {
            Assert.fail("Exception is not expected");
        }
        for (int i = 0; i < 4; i++) {
            if ((client.getNumOfTimelineEntitiesPublished()) == 3) {
                break;
            }
            Thread.sleep(TestTimelineClientV2Impl.TIME_TO_SLEEP);
        }
        printReceivedEntities();
        Assert.assertEquals("TimelineEntities not published as desired", 3, client.getNumOfTimelineEntitiesPublished());
        TimelineEntities firstPublishedEntities = client.getPublishedEntities(0);
        Assert.assertEquals("sync entities should not be merged with async", 1, firstPublishedEntities.getEntities().size());
        // test before pushing the sync entities asyncs are merged and pushed
        TimelineEntities secondPublishedEntities = client.getPublishedEntities(1);
        Assert.assertEquals("async entities should be merged before publishing sync", 2, secondPublishedEntities.getEntities().size());
        Assert.assertEquals("Order of Async Events Needs to be FIFO", "2", secondPublishedEntities.getEntities().get(0).getId());
        Assert.assertEquals("Order of Async Events Needs to be FIFO", "3", secondPublishedEntities.getEntities().get(1).getId());
        // test the last entity published is sync put
        TimelineEntities thirdPublishedEntities = client.getPublishedEntities(2);
        Assert.assertEquals("sync entities had to be published at the last", 1, thirdPublishedEntities.getEntities().size());
        Assert.assertEquals("Expected last sync Event is not proper", "4", thirdPublishedEntities.getEntities().get(0).getId());
    }

    @Test
    public void testExceptionCalls() throws Exception {
        client.setThrowYarnException(true);
        try {
            client.putEntitiesAsync(TestTimelineClientV2Impl.generateEntity("1"));
        } catch (YarnException e) {
            Assert.fail("Async calls are not expected to throw exception");
        }
        try {
            client.putEntities(TestTimelineClientV2Impl.generateEntity("2"));
            Assert.fail("Sync calls are expected to throw exception");
        } catch (YarnException e) {
            Assert.assertEquals("Same exception needs to be thrown", "ActualException", e.getCause().getMessage());
        }
    }

    @Test
    public void testConfigurableNumberOfMerges() throws Exception {
        client.setSleepBeforeReturn(true);
        try {
            // At max 3 entities need to be merged
            client.putEntitiesAsync(TestTimelineClientV2Impl.generateEntity("1"));
            client.putEntitiesAsync(TestTimelineClientV2Impl.generateEntity("2"));
            client.putEntitiesAsync(TestTimelineClientV2Impl.generateEntity("3"));
            client.putEntitiesAsync(TestTimelineClientV2Impl.generateEntity("4"));
            client.putEntities(TestTimelineClientV2Impl.generateEntity("5"));
            client.putEntitiesAsync(TestTimelineClientV2Impl.generateEntity("6"));
            client.putEntitiesAsync(TestTimelineClientV2Impl.generateEntity("7"));
            client.putEntitiesAsync(TestTimelineClientV2Impl.generateEntity("8"));
            client.putEntitiesAsync(TestTimelineClientV2Impl.generateEntity("9"));
            client.putEntitiesAsync(TestTimelineClientV2Impl.generateEntity("10"));
        } catch (YarnException e) {
            Assert.fail("No exception expected");
        }
        // not having the same logic here as it doesn't depend on how many times
        // events are published.
        Thread.sleep((2 * (TestTimelineClientV2Impl.TIME_TO_SLEEP)));
        printReceivedEntities();
        for (TimelineEntities publishedEntities : client.publishedEntities) {
            Assert.assertTrue((("Number of entities should not be greater than 3 for each publish," + " but was ") + (publishedEntities.getEntities().size())), ((publishedEntities.getEntities().size()) <= 3));
        }
    }

    @Test
    public void testSetTimelineToken() throws Exception {
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        Assert.assertEquals(0, ugi.getTokens().size());
        Assert.assertNull("Timeline token in v2 client should not be set", client.currentTimelineToken);
        Token token = Token.newInstance(new byte[0], "kind", new byte[0], "service");
        client.setTimelineCollectorInfo(CollectorInfo.newInstance(null, token));
        Assert.assertNull(("Timeline token in v2 client should not be set as token kind " + "is unexepcted."), client.currentTimelineToken);
        Assert.assertEquals(0, ugi.getTokens().size());
        token = Token.newInstance(new byte[0], KIND_NAME.toString(), new byte[0], null);
        client.setTimelineCollectorInfo(CollectorInfo.newInstance(null, token));
        Assert.assertNull(("Timeline token in v2 client should not be set as serice is " + "not set."), client.currentTimelineToken);
        Assert.assertEquals(0, ugi.getTokens().size());
        TimelineDelegationTokenIdentifier ident = new TimelineDelegationTokenIdentifier(new Text(ugi.getUserName()), new Text("renewer"), null);
        ident.setSequenceNumber(1);
        token = Token.newInstance(ident.getBytes(), KIND_NAME.toString(), new byte[0], "localhost:1234");
        client.setTimelineCollectorInfo(CollectorInfo.newInstance(null, token));
        Assert.assertEquals(1, ugi.getTokens().size());
        Assert.assertNotNull("Timeline token should be set in v2 client.", client.currentTimelineToken);
        Assert.assertEquals(token, client.currentTimelineToken);
        ident.setSequenceNumber(20);
        Token newToken = Token.newInstance(ident.getBytes(), KIND_NAME.toString(), new byte[0], "localhost:1234");
        client.setTimelineCollectorInfo(CollectorInfo.newInstance(null, newToken));
        Assert.assertEquals(1, ugi.getTokens().size());
        Assert.assertNotEquals(token, client.currentTimelineToken);
        Assert.assertEquals(newToken, client.currentTimelineToken);
    }

    @Test
    public void testAfterStop() throws Exception {
        client.setSleepBeforeReturn(true);
        try {
            // At max 3 entities need to be merged
            client.putEntities(TestTimelineClientV2Impl.generateEntity("1"));
            for (int i = 2; i < 20; i++) {
                client.putEntitiesAsync(TestTimelineClientV2Impl.generateEntity(("" + i)));
            }
            stop();
            try {
                client.putEntitiesAsync(TestTimelineClientV2Impl.generateEntity("50"));
                Assert.fail("Exception expected");
            } catch (YarnException e) {
                // expected
            }
        } catch (YarnException e) {
            Assert.fail("No exception expected");
        }
        // not having the same logic here as it doesn't depend on how many times
        // events are published.
        for (int i = 0; i < 5; i++) {
            TimelineEntities publishedEntities = client.publishedEntities.get(((client.publishedEntities.size()) - 1));
            TimelineEntity timelineEntity = publishedEntities.getEntities().get(((publishedEntities.getEntities().size()) - 1));
            if (!(timelineEntity.getId().equals("19"))) {
                Thread.sleep((2 * (TestTimelineClientV2Impl.TIME_TO_SLEEP)));
            }
        }
        printReceivedEntities();
        TimelineEntities publishedEntities = client.publishedEntities.get(((client.publishedEntities.size()) - 1));
        TimelineEntity timelineEntity = publishedEntities.getEntities().get(((publishedEntities.getEntities().size()) - 1));
        Assert.assertEquals("", "19", timelineEntity.getId());
    }
}

