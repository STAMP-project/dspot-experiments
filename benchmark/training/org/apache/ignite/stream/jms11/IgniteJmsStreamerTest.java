/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.stream.jms11;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerRegistry;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.DestinationStatistics;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.security.SimpleAuthenticationPlugin;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.IgniteException;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Test for {@link JmsStreamer}. Tests both queues and topics.
 *
 * @author Raul Kripalani
 */
public class IgniteJmsStreamerTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final int CACHE_ENTRY_COUNT = 100;

    /**
     *
     */
    private static final String QUEUE_NAME = "ignite.test.queue";

    /**
     *
     */
    private static final String TOPIC_NAME = "ignite.test.topic";

    /**
     *
     */
    private static final Map<String, String> TEST_DATA = new HashMap<>();

    static {
        for (int i = 1; i <= (IgniteJmsStreamerTest.CACHE_ENTRY_COUNT); i++)
            IgniteJmsStreamerTest.TEST_DATA.put(Integer.toString(i), ("v" + i));

    }

    /**
     *
     */
    private BrokerService broker;

    /**
     *
     */
    private ConnectionFactory connFactory;

    /**
     * Constructor.
     */
    public IgniteJmsStreamerTest() {
        super(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueueFromName() throws Exception {
        Destination dest = new ActiveMQQueue(IgniteJmsStreamerTest.QUEUE_NAME);
        // produce messages into the queue
        produceObjectMessages(dest, false);
        try (IgniteDataStreamer<String, String> dataStreamer = grid().dataStreamer(DEFAULT_CACHE_NAME)) {
            JmsStreamer<ObjectMessage, String, String> jmsStreamer = newJmsStreamer(ObjectMessage.class, dataStreamer);
            jmsStreamer.setDestinationType(Queue.class);
            jmsStreamer.setDestinationName(IgniteJmsStreamerTest.QUEUE_NAME);
            // subscribe to cache PUT events and return a countdown latch starting at CACHE_ENTRY_COUNT
            CountDownLatch latch = subscribeToPutEvents(IgniteJmsStreamerTest.CACHE_ENTRY_COUNT);
            jmsStreamer.start();
            // all cache PUT events received in 10 seconds
            latch.await(10, TimeUnit.SECONDS);
            assertAllCacheEntriesLoaded();
            jmsStreamer.stop();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTopicFromName() throws InterruptedException, JMSException {
        Destination dest = new ActiveMQTopic(IgniteJmsStreamerTest.TOPIC_NAME);
        // should not produced messages until subscribed to the topic; otherwise they will be missed because this is not
        // a durable subscriber (for which a dedicated test exists)
        try (IgniteDataStreamer<String, String> dataStreamer = grid().dataStreamer(DEFAULT_CACHE_NAME)) {
            JmsStreamer<ObjectMessage, String, String> jmsStreamer = newJmsStreamer(ObjectMessage.class, dataStreamer);
            jmsStreamer.setDestinationType(Topic.class);
            jmsStreamer.setDestinationName(IgniteJmsStreamerTest.TOPIC_NAME);
            // subscribe to cache PUT events and return a countdown latch starting at CACHE_ENTRY_COUNT
            CountDownLatch latch = subscribeToPutEvents(IgniteJmsStreamerTest.CACHE_ENTRY_COUNT);
            jmsStreamer.start();
            // produce messages
            produceObjectMessages(dest, false);
            // all cache PUT events received in 10 seconds
            latch.await(10, TimeUnit.SECONDS);
            assertAllCacheEntriesLoaded();
            jmsStreamer.stop();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueueFromExplicitDestination() throws Exception {
        Destination dest = new ActiveMQQueue(IgniteJmsStreamerTest.QUEUE_NAME);
        // produce messages into the queue
        produceObjectMessages(dest, false);
        try (IgniteDataStreamer<String, String> dataStreamer = grid().dataStreamer(DEFAULT_CACHE_NAME)) {
            JmsStreamer<ObjectMessage, String, String> jmsStreamer = newJmsStreamer(ObjectMessage.class, dataStreamer);
            jmsStreamer.setDestination(dest);
            // subscribe to cache PUT events and return a countdown latch starting at CACHE_ENTRY_COUNT
            CountDownLatch latch = subscribeToPutEvents(IgniteJmsStreamerTest.CACHE_ENTRY_COUNT);
            // start the streamer
            jmsStreamer.start();
            // all cache PUT events received in 10 seconds
            latch.await(10, TimeUnit.SECONDS);
            assertAllCacheEntriesLoaded();
            jmsStreamer.stop();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTopicFromExplicitDestination() throws InterruptedException, JMSException {
        Destination dest = new ActiveMQTopic(IgniteJmsStreamerTest.TOPIC_NAME);
        // should not produced messages until subscribed to the topic; otherwise they will be missed because this is not
        // a durable subscriber (for which a dedicated test exists)
        try (IgniteDataStreamer<String, String> dataStreamer = grid().dataStreamer(DEFAULT_CACHE_NAME)) {
            JmsStreamer<ObjectMessage, String, String> jmsStreamer = newJmsStreamer(ObjectMessage.class, dataStreamer);
            jmsStreamer.setDestination(dest);
            // subscribe to cache PUT events and return a countdown latch starting at CACHE_ENTRY_COUNT
            CountDownLatch latch = subscribeToPutEvents(IgniteJmsStreamerTest.CACHE_ENTRY_COUNT);
            jmsStreamer.start();
            // produce messages
            produceObjectMessages(dest, false);
            // all cache PUT events received in 10 seconds
            latch.await(10, TimeUnit.SECONDS);
            assertAllCacheEntriesLoaded();
            jmsStreamer.stop();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testInsertMultipleCacheEntriesFromOneMessage() throws Exception {
        Destination dest = new ActiveMQQueue(IgniteJmsStreamerTest.QUEUE_NAME);
        // produce A SINGLE MESSAGE, containing all data, into the queue
        produceStringMessages(dest, true);
        try (IgniteDataStreamer<String, String> dataStreamer = grid().dataStreamer(DEFAULT_CACHE_NAME)) {
            JmsStreamer<TextMessage, String, String> jmsStreamer = newJmsStreamer(TextMessage.class, dataStreamer);
            jmsStreamer.setDestination(dest);
            // subscribe to cache PUT events and return a countdown latch starting at CACHE_ENTRY_COUNT
            CountDownLatch latch = subscribeToPutEvents(IgniteJmsStreamerTest.CACHE_ENTRY_COUNT);
            jmsStreamer.start();
            // all cache PUT events received in 10 seconds
            latch.await(10, TimeUnit.SECONDS);
            assertAllCacheEntriesLoaded();
            jmsStreamer.stop();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDurableSubscriberStartStopStart() throws Exception {
        Destination dest = new ActiveMQTopic(IgniteJmsStreamerTest.TOPIC_NAME);
        try (IgniteDataStreamer<String, String> dataStreamer = grid().dataStreamer(DEFAULT_CACHE_NAME)) {
            JmsStreamer<TextMessage, String, String> jmsStreamer = newJmsStreamer(TextMessage.class, dataStreamer);
            jmsStreamer.setDestination(dest);
            jmsStreamer.setDurableSubscription(true);
            jmsStreamer.setClientId(Long.toString(System.currentTimeMillis()));
            jmsStreamer.setDurableSubscriptionName("ignite-test-durable");
            // we start the streamer so that the durable subscriber registers itself
            jmsStreamer.start();
            // we stop it immediately
            jmsStreamer.stop();
            // we assert that there are no clients of the broker (to make sure we disconnected properly)
            assertEquals(0, broker.getCurrentConnections());
            // we send messages while we're still away
            produceStringMessages(dest, false);
            // subscribe to cache PUT events and return a countdown latch starting at CACHE_ENTRY_COUNT
            CountDownLatch latch = subscribeToPutEvents(IgniteJmsStreamerTest.CACHE_ENTRY_COUNT);
            jmsStreamer.start();
            // all cache PUT events received in 10 seconds
            latch.await(10, TimeUnit.SECONDS);
            assertAllCacheEntriesLoaded();
            jmsStreamer.stop();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueueMessagesConsumedInBatchesCompletionSizeBased() throws Exception {
        Destination dest = new ActiveMQQueue(IgniteJmsStreamerTest.QUEUE_NAME);
        // produce multiple messages into the queue
        produceStringMessages(dest, false);
        try (IgniteDataStreamer<String, String> dataStreamer = grid().dataStreamer(DEFAULT_CACHE_NAME)) {
            JmsStreamer<TextMessage, String, String> jmsStreamer = newJmsStreamer(TextMessage.class, dataStreamer);
            jmsStreamer.setDestination(dest);
            jmsStreamer.setBatched(true);
            jmsStreamer.setBatchClosureSize(99);
            // disable time-based session commits
            jmsStreamer.setBatchClosureMillis(0);
            // subscribe to cache PUT events and return a countdown latch starting at CACHE_ENTRY_COUNT
            CountDownLatch latch = subscribeToPutEvents(IgniteJmsStreamerTest.CACHE_ENTRY_COUNT);
            jmsStreamer.start();
            // all cache PUT events received in 10 seconds
            latch.await(10, TimeUnit.SECONDS);
            assertAllCacheEntriesLoaded();
            // we expect all entries to be loaded, but still one (uncommitted) message should remain in the queue
            // as observed by the broker
            DestinationStatistics qStats = broker.getBroker().getDestinationMap().get(dest).getDestinationStatistics();
            assertEquals(1, qStats.getMessages().getCount());
            assertEquals(1, qStats.getInflight().getCount());
            jmsStreamer.stop();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueueMessagesConsumedInBatchesCompletionTimeBased() throws Exception {
        Destination dest = new ActiveMQQueue(IgniteJmsStreamerTest.QUEUE_NAME);
        // produce multiple messages into the queue
        produceStringMessages(dest, false);
        try (IgniteDataStreamer<String, String> dataStreamer = grid().dataStreamer(DEFAULT_CACHE_NAME)) {
            JmsStreamer<TextMessage, String, String> jmsStreamer = newJmsStreamer(TextMessage.class, dataStreamer);
            jmsStreamer.setDestination(dest);
            jmsStreamer.setBatched(true);
            jmsStreamer.setBatchClosureMillis(2000);
            // disable size-based session commits
            jmsStreamer.setBatchClosureSize(0);
            // subscribe to cache PUT events and return a countdown latch starting at CACHE_ENTRY_COUNT
            CountDownLatch latch = subscribeToPutEvents(IgniteJmsStreamerTest.CACHE_ENTRY_COUNT);
            DestinationStatistics qStats = broker.getBroker().getDestinationMap().get(dest).getDestinationStatistics();
            jmsStreamer.start();
            // all messages are still inflight
            assertEquals(IgniteJmsStreamerTest.CACHE_ENTRY_COUNT, qStats.getMessages().getCount());
            assertEquals(0, qStats.getDequeues().getCount());
            // wait a little bit
            Thread.sleep(100);
            // all messages are still inflight
            assertEquals(IgniteJmsStreamerTest.CACHE_ENTRY_COUNT, qStats.getMessages().getCount());
            assertEquals(0, qStats.getDequeues().getCount());
            // now let the scheduler execute
            Thread.sleep(2100);
            // all messages are committed
            assertEquals(0, qStats.getMessages().getCount());
            assertEquals(IgniteJmsStreamerTest.CACHE_ENTRY_COUNT, qStats.getDequeues().getCount());
            latch.await(5, TimeUnit.SECONDS);
            assertAllCacheEntriesLoaded();
            jmsStreamer.stop();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGenerateNoEntries() throws Exception {
        Destination dest = new ActiveMQQueue(IgniteJmsStreamerTest.QUEUE_NAME);
        // produce multiple messages into the queue
        produceStringMessages(dest, false);
        try (IgniteDataStreamer<String, String> dataStreamer = grid().dataStreamer(DEFAULT_CACHE_NAME)) {
            JmsStreamer<TextMessage, String, String> jmsStreamer = newJmsStreamer(TextMessage.class, dataStreamer);
            // override the transformer with one that generates no cache entries
            jmsStreamer.setTransformer(TestTransformers.generateNoEntries());
            jmsStreamer.setDestination(dest);
            // subscribe to cache PUT events and return a countdown latch starting at CACHE_ENTRY_COUNT
            CountDownLatch latch = subscribeToPutEvents(1);
            jmsStreamer.start();
            // no cache PUT events were received in 3 seconds, i.e. CountDownLatch does not fire
            assertFalse(latch.await(3, TimeUnit.SECONDS));
            jmsStreamer.stop();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTransactedSessionNoBatching() throws Exception {
        Destination dest = new ActiveMQQueue(IgniteJmsStreamerTest.QUEUE_NAME);
        // produce multiple messages into the queue
        produceStringMessages(dest, false);
        try (IgniteDataStreamer<String, String> dataStreamer = grid().dataStreamer(DEFAULT_CACHE_NAME)) {
            JmsStreamer<TextMessage, String, String> jmsStreamer = newJmsStreamer(TextMessage.class, dataStreamer);
            jmsStreamer.setTransacted(true);
            jmsStreamer.setDestination(dest);
            // subscribe to cache PUT events and return a countdown latch starting at CACHE_ENTRY_COUNT
            CountDownLatch latch = subscribeToPutEvents(IgniteJmsStreamerTest.CACHE_ENTRY_COUNT);
            jmsStreamer.start();
            // all cache PUT events received in 10 seconds
            latch.await(10, TimeUnit.SECONDS);
            assertAllCacheEntriesLoaded();
            jmsStreamer.stop();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueueMultipleThreads() throws Exception {
        Destination dest = new ActiveMQQueue(IgniteJmsStreamerTest.QUEUE_NAME);
        // produce messages into the queue
        produceObjectMessages(dest, false);
        try (IgniteDataStreamer<String, String> dataStreamer = grid().dataStreamer(DEFAULT_CACHE_NAME)) {
            JmsStreamer<ObjectMessage, String, String> jmsStreamer = newJmsStreamer(ObjectMessage.class, dataStreamer);
            jmsStreamer.setDestination(dest);
            jmsStreamer.setThreads(5);
            // subscribe to cache PUT events and return a countdown latch starting at CACHE_ENTRY_COUNT
            CountDownLatch latch = subscribeToPutEvents(IgniteJmsStreamerTest.CACHE_ENTRY_COUNT);
            // start the streamer
            jmsStreamer.start();
            DestinationStatistics qStats = broker.getBroker().getDestinationMap().get(dest).getDestinationStatistics();
            assertEquals(5, qStats.getConsumers().getCount());
            // all cache PUT events received in 10 seconds
            latch.await(10, TimeUnit.SECONDS);
            // assert that all consumers received messages - given that the prefetch is 1
            for (Subscription subscription : broker.getBroker().getDestinationMap().get(dest).getConsumers())
                assertTrue(((subscription.getDequeueCounter()) > 0));

            assertAllCacheEntriesLoaded();
            jmsStreamer.stop();
        }
    }

    /**
     * Test for ExceptionListener functionality.
     *
     * @throws Exception
     * 		If fails.
     */
    @Test
    public void testExceptionListener() throws Exception {
        // restart broker with auth plugin
        if (broker.isStarted())
            broker.stop();

        broker.waitUntilStopped();
        broker.setPlugins(new BrokerPlugin[]{ new SimpleAuthenticationPlugin(new ArrayList()) });
        broker.start(true);
        connFactory = new org.apache.activemq.ActiveMQConnectionFactory(BrokerRegistry.getInstance().findFirst().getVmConnectorURI());
        final List<Throwable> lsnrExceptions = new LinkedList<>();
        final CountDownLatch latch = new CountDownLatch(1);
        Destination dest = new ActiveMQQueue(IgniteJmsStreamerTest.QUEUE_NAME);
        try (IgniteDataStreamer<String, String> dataStreamer = grid().dataStreamer(DEFAULT_CACHE_NAME)) {
            JmsStreamer<ObjectMessage, String, String> jmsStreamer = newJmsStreamer(ObjectMessage.class, dataStreamer);
            jmsStreamer.setExceptionListener(new ExceptionListener() {
                @Override
                public void onException(JMSException e) {
                    System.out.println("ERROR");
                    lsnrExceptions.add(e);
                    latch.countDown();
                }
            });
            jmsStreamer.setDestination(dest);
            GridTestUtils.assertThrowsWithCause(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    jmsStreamer.start();
                    return null;
                }
            }, SecurityException.class);
            assertTrue(latch.await(10, TimeUnit.SECONDS));
            assertTrue(((lsnrExceptions.size()) > 0));
            GridTestUtils.assertThrowsWithCause(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    jmsStreamer.stop();
                    return null;
                }
            }, IgniteException.class);
        }
    }
}

