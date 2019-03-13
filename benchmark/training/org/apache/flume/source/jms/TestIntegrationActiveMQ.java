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
package org.apache.flume.source.jms;


import JMSDestinationLocator.JNDI;
import JMSSourceConfiguration.BATCH_SIZE;
import JMSSourceConfiguration.CLIENT_ID;
import JMSSourceConfiguration.CREATE_DURABLE_SUBSCRIPTION;
import JMSSourceConfiguration.DESTINATION_LOCATOR;
import JMSSourceConfiguration.DESTINATION_NAME;
import JMSSourceConfiguration.DESTINATION_TYPE;
import JMSSourceConfiguration.DESTINATION_TYPE_QUEUE;
import JMSSourceConfiguration.DESTINATION_TYPE_TOPIC;
import JMSSourceConfiguration.DURABLE_SUBSCRIPTION_NAME;
import Status.BACKOFF;
import Status.READY;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.apache.activemq.broker.BrokerService;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class TestIntegrationActiveMQ {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestIntegrationActiveMQ.class);

    private static final String INITIAL_CONTEXT_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";

    public static final String BROKER_BIND_URL = "tcp://localhost:61516";

    private static final String DESTINATION_NAME = "test";

    // specific for dynamic queues on ActiveMq
    public static final String JNDI_PREFIX = "dynamicQueues/";

    private enum TestMode {

        WITH_AUTHENTICATION,
        WITHOUT_AUTHENTICATION;}

    private File baseDir;

    private File tmpDir;

    private File dataDir;

    private BrokerService broker;

    private Context context;

    private JMSSource source;

    private List<Event> events;

    private final String jmsUserName;

    private final String jmsPassword;

    public TestIntegrationActiveMQ(TestIntegrationActiveMQ.TestMode testMode) {
        TestIntegrationActiveMQ.LOGGER.info("Testing with test mode {}", testMode);
        switch (testMode) {
            case WITH_AUTHENTICATION :
                jmsUserName = "user";
                jmsPassword = "pass";
                break;
            case WITHOUT_AUTHENTICATION :
                jmsUserName = null;
                jmsPassword = null;
                break;
            default :
                throw new IllegalArgumentException(("Unhandled test mode: " + testMode));
        }
    }

    @Test
    public void testQueueLocatedWithJndi() throws Exception {
        context.put(JMSSourceConfiguration.DESTINATION_NAME, ((TestIntegrationActiveMQ.JNDI_PREFIX) + (TestIntegrationActiveMQ.DESTINATION_NAME)));
        context.put(DESTINATION_LOCATOR, JNDI.name());
        testQueue();
    }

    @Test
    public void testQueue() throws Exception {
        context.put(DESTINATION_TYPE, DESTINATION_TYPE_QUEUE);
        source.configure(context);
        source.start();
        Thread.sleep(500L);
        List<String> expected = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            expected.add(String.valueOf(i));
        }
        putQueue(expected);
        Thread.sleep(500L);
        Assert.assertEquals(READY, source.process());
        Assert.assertEquals(BACKOFF, source.process());
        Assert.assertEquals(expected.size(), events.size());
        List<String> actual = Lists.newArrayList();
        for (Event event : events) {
            actual.add(new String(event.getBody(), Charsets.UTF_8));
        }
        Collections.sort(expected);
        Collections.sort(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testTopic() throws Exception {
        context.put(DESTINATION_TYPE, DESTINATION_TYPE_TOPIC);
        source.configure(context);
        source.start();
        Thread.sleep(500L);
        List<String> expected = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            expected.add(String.valueOf(i));
        }
        putTopic(expected);
        Thread.sleep(500L);
        Assert.assertEquals(READY, source.process());
        Assert.assertEquals(BACKOFF, source.process());
        Assert.assertEquals(expected.size(), events.size());
        List<String> actual = Lists.newArrayList();
        for (Event event : events) {
            actual.add(new String(event.getBody(), Charsets.UTF_8));
        }
        Collections.sort(expected);
        Collections.sort(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDurableSubscription() throws Exception {
        context.put(DESTINATION_TYPE, DESTINATION_TYPE_TOPIC);
        context.put(CLIENT_ID, "FLUME");
        context.put(DURABLE_SUBSCRIPTION_NAME, "SOURCE1");
        context.put(CREATE_DURABLE_SUBSCRIPTION, "true");
        context.put(BATCH_SIZE, "10");
        source.configure(context);
        source.start();
        Thread.sleep(5000L);
        List<String> expected = Lists.newArrayList();
        List<String> input = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            input.add(("before " + (String.valueOf(i))));
        }
        expected.addAll(input);
        putTopic(input);
        Thread.sleep(500L);
        Assert.assertEquals(READY, source.process());
        Assert.assertEquals(BACKOFF, source.process());
        source.stop();
        Thread.sleep(500L);
        input = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            input.add(("during " + (String.valueOf(i))));
        }
        expected.addAll(input);
        putTopic(input);
        source.start();
        Thread.sleep(500L);
        input = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            input.add(("after " + (String.valueOf(i))));
        }
        expected.addAll(input);
        putTopic(input);
        Assert.assertEquals(READY, source.process());
        Assert.assertEquals(READY, source.process());
        Assert.assertEquals(BACKOFF, source.process());
        Assert.assertEquals(expected.size(), events.size());
        List<String> actual = Lists.newArrayList();
        for (Event event : events) {
            actual.add(new String(event.getBody(), Charsets.UTF_8));
        }
        Collections.sort(expected);
        Collections.sort(actual);
        Assert.assertEquals(expected, actual);
    }
}

