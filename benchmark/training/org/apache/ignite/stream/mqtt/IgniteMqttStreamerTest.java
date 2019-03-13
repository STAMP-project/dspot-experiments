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
package org.apache.ignite.stream.mqtt;


import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.junit.Test;


/**
 * Test for {@link MqttStreamer}.
 */
public class IgniteMqttStreamerTest extends GridCommonAbstractTest {
    /**
     * The test data.
     */
    private static final Map<Integer, String> TEST_DATA = new HashMap<>();

    /**
     * Topic name for single topic tests.
     */
    private static final String SINGLE_TOPIC_NAME = "abc";

    /**
     * Topic names for multiple topic tests.
     */
    private static final List<String> MULTIPLE_TOPIC_NAMES = Arrays.asList("def", "ghi", "jkl", "mno");

    /**
     * The AMQ broker with an MQTT interface.
     */
    private BrokerService broker;

    /**
     * The MQTT client.
     */
    private MqttClient client;

    /**
     * The broker URL.
     */
    private String brokerUrl;

    /**
     * The broker port. *
     */
    private int port;

    /**
     * The MQTT streamer currently under test.
     */
    private MqttStreamer<Integer, String> streamer;

    /**
     * The UUID of the currently active remote listener.
     */
    private UUID remoteLsnr;

    /**
     * The Ignite data streamer.
     */
    private IgniteDataStreamer<Integer, String> dataStreamer;

    static {
        for (int i = 0; i < 100; i++)
            IgniteMqttStreamerTest.TEST_DATA.put(i, ("v" + i));

    }

    /**
     * Constructor.
     */
    public IgniteMqttStreamerTest() {
        super(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConnectDisconnect() throws Exception {
        // configure streamer
        streamer.setSingleTupleExtractor(IgniteMqttStreamerTest.singleTupleExtractor());
        streamer.setTopic(IgniteMqttStreamerTest.SINGLE_TOPIC_NAME);
        streamer.setBlockUntilConnected(true);
        // action time: repeat 10 times; make sure the connection state is kept correctly every time
        for (int i = 0; i < 10; i++) {
            streamer.start();
            assertTrue(streamer.isConnected());
            streamer.stop();
            assertFalse(streamer.isConnected());
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSingleTopic_NoQoS_OneEntryPerMessage() throws Exception {
        // configure streamer
        streamer.setSingleTupleExtractor(IgniteMqttStreamerTest.singleTupleExtractor());
        streamer.setTopic(IgniteMqttStreamerTest.SINGLE_TOPIC_NAME);
        // subscribe to cache PUT events
        CountDownLatch latch = subscribeToPutEvents(50);
        // action time
        streamer.start();
        // send messages
        sendMessages(Arrays.asList(IgniteMqttStreamerTest.SINGLE_TOPIC_NAME), 0, 50, false);
        // assertions
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertCacheEntriesLoaded(50);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleTopics_NoQoS_OneEntryPerMessage() throws Exception {
        // configure streamer
        streamer.setSingleTupleExtractor(IgniteMqttStreamerTest.singleTupleExtractor());
        streamer.setTopics(IgniteMqttStreamerTest.MULTIPLE_TOPIC_NAMES);
        // subscribe to cache PUT events
        CountDownLatch latch = subscribeToPutEvents(50);
        // action time
        streamer.start();
        // send messages
        sendMessages(IgniteMqttStreamerTest.MULTIPLE_TOPIC_NAMES, 0, 50, false);
        // assertions
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertCacheEntriesLoaded(50);
        assertTrue(((broker.getBroker().getDestinationMap().size()) >= 4));
        assertTrue(broker.getBroker().getDestinationMap().containsKey(new ActiveMQTopic("def")));
        assertTrue(broker.getBroker().getDestinationMap().containsKey(new ActiveMQTopic("ghi")));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSingleTopic_NoQoS_MultipleEntriesOneMessage() throws Exception {
        // configure streamer
        streamer.setMultipleTupleExtractor(IgniteMqttStreamerTest.multipleTupleExtractor());
        streamer.setTopic(IgniteMqttStreamerTest.SINGLE_TOPIC_NAME);
        // subscribe to cache PUT events
        CountDownLatch latch = subscribeToPutEvents(50);
        // action time
        streamer.start();
        // send messages
        sendMessages(Arrays.asList(IgniteMqttStreamerTest.SINGLE_TOPIC_NAME), 0, 50, true);
        // assertions
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertCacheEntriesLoaded(50);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleTopics_NoQoS_MultipleEntriesOneMessage() throws Exception {
        // configure streamer
        streamer.setMultipleTupleExtractor(IgniteMqttStreamerTest.multipleTupleExtractor());
        streamer.setTopics(IgniteMqttStreamerTest.MULTIPLE_TOPIC_NAMES);
        // subscribe to cache PUT events
        CountDownLatch latch = subscribeToPutEvents(50);
        // action time
        streamer.start();
        // send messages
        sendMessages(IgniteMqttStreamerTest.MULTIPLE_TOPIC_NAMES, 0, 50, true);
        // assertions
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertCacheEntriesLoaded(50);
        assertTrue(((broker.getBroker().getDestinationMap().size()) >= 4));
        assertTrue(broker.getBroker().getDestinationMap().containsKey(new ActiveMQTopic("def")));
        assertTrue(broker.getBroker().getDestinationMap().containsKey(new ActiveMQTopic("ghi")));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSingleTopic_NoQoS_ConnectOptions_Durable() throws Exception {
        // configure streamer
        streamer.setSingleTupleExtractor(IgniteMqttStreamerTest.singleTupleExtractor());
        streamer.setTopic(IgniteMqttStreamerTest.SINGLE_TOPIC_NAME);
        MqttConnectOptions connOptions = new MqttConnectOptions();
        connOptions.setCleanSession(false);
        streamer.setConnectOptions(connOptions);
        // subscribe to cache PUT events
        CountDownLatch latch = subscribeToPutEvents(50);
        // action time
        streamer.start();
        // send messages
        sendMessages(Arrays.asList(IgniteMqttStreamerTest.SINGLE_TOPIC_NAME), 0, 50, false);
        // assertions
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertCacheEntriesLoaded(50);
        // explicitly stop the streamer
        streamer.stop();
        // send messages while stopped
        sendMessages(Arrays.asList(IgniteMqttStreamerTest.SINGLE_TOPIC_NAME), 50, 50, false);
        latch = subscribeToPutEvents(50);
        // start the streamer again
        streamer.start();
        // assertions - make sure that messages sent during disconnection were also received
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertCacheEntriesLoaded(100);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSingleTopic_NoQoS_Reconnect() throws Exception {
        // configure streamer
        streamer.setSingleTupleExtractor(IgniteMqttStreamerTest.singleTupleExtractor());
        streamer.setRetryWaitStrategy(WaitStrategies.noWait());
        streamer.setRetryStopStrategy(StopStrategies.neverStop());
        streamer.setTopic(IgniteMqttStreamerTest.SINGLE_TOPIC_NAME);
        // subscribe to cache PUT events
        CountDownLatch latch = subscribeToPutEvents(50);
        // action time
        streamer.start();
        // send messages
        sendMessages(Arrays.asList(IgniteMqttStreamerTest.SINGLE_TOPIC_NAME), 0, 50, false);
        // assertions
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertCacheEntriesLoaded(50);
        // now shutdown the broker, wait 2 seconds and start it again
        broker.stop();
        broker.start(true);
        broker.waitUntilStarted();
        Thread.sleep(2000);
        client.connect();
        // let's ensure we have 2 connections: Ignite and our test
        assertEquals(2, broker.getTransportConnectorByScheme("mqtt").getConnections().size());
        // subscribe to cache PUT events again
        latch = subscribeToPutEvents(50);
        // send messages
        sendMessages(Arrays.asList(IgniteMqttStreamerTest.SINGLE_TOPIC_NAME), 50, 50, false);
        // assertions
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertCacheEntriesLoaded(100);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSingleTopic_NoQoS_RetryOnce() throws Exception {
        // configure streamer
        streamer.setSingleTupleExtractor(IgniteMqttStreamerTest.singleTupleExtractor());
        streamer.setRetryWaitStrategy(WaitStrategies.noWait());
        streamer.setRetryStopStrategy(StopStrategies.stopAfterAttempt(1));
        streamer.setTopic(IgniteMqttStreamerTest.SINGLE_TOPIC_NAME);
        // subscribe to cache PUT events
        CountDownLatch latch = subscribeToPutEvents(50);
        // action time
        streamer.start();
        // send messages
        sendMessages(Arrays.asList(IgniteMqttStreamerTest.SINGLE_TOPIC_NAME), 0, 50, false);
        // assertions
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertCacheEntriesLoaded(50);
        // now shutdown the broker, wait 2 seconds and start it again
        broker.stop();
        broker.start(true);
        broker.waitUntilStarted();
        client.connect();
        // lets send messages and ensure they are not received, because our retrier desisted
        sendMessages(Arrays.asList(IgniteMqttStreamerTest.SINGLE_TOPIC_NAME), 50, 50, false);
        Thread.sleep(3000);
        assertNull(grid().cache(DEFAULT_CACHE_NAME).get(50));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleTopics_MultipleQoS_OneEntryPerMessage() throws Exception {
        // configure streamer
        streamer.setSingleTupleExtractor(IgniteMqttStreamerTest.singleTupleExtractor());
        streamer.setTopics(IgniteMqttStreamerTest.MULTIPLE_TOPIC_NAMES);
        streamer.setQualitiesOfService(Arrays.asList(1, 1, 1, 1));
        // subscribe to cache PUT events
        CountDownLatch latch = subscribeToPutEvents(50);
        // action time
        streamer.start();
        // send messages
        sendMessages(IgniteMqttStreamerTest.MULTIPLE_TOPIC_NAMES, 0, 50, false);
        // assertions
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertCacheEntriesLoaded(50);
        assertTrue(((broker.getBroker().getDestinationMap().size()) >= 4));
        assertTrue(broker.getBroker().getDestinationMap().containsKey(new ActiveMQTopic("def")));
        assertTrue(broker.getBroker().getDestinationMap().containsKey(new ActiveMQTopic("ghi")));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleTopics_MultipleQoS_Mismatch() throws Exception {
        // configure streamer
        streamer.setSingleTupleExtractor(IgniteMqttStreamerTest.singleTupleExtractor());
        streamer.setTopics(IgniteMqttStreamerTest.MULTIPLE_TOPIC_NAMES);
        streamer.setQualitiesOfService(Arrays.asList(1, 1, 1));
        try {
            streamer.start();
        } catch (Exception ignored) {
            return;
        }
        fail("Expected an exception reporting invalid parameters");
    }
}

