/**
 * Copyright 2017 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.commons;


import com.github.ambry.clustermap.MockHelixPropertyStore;
import com.github.ambry.config.HelixPropertyStoreConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.helix.ZNRecord;
import org.apache.helix.store.HelixPropertyStore;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link HelixNotifier}.
 */
public class HelixNotifierTest {
    private static final int ZK_CLIENT_CONNECT_TIMEOUT_MS = 20 * 1000;

    private static final int ZK_CLIENT_SESSION_TIMEOUT_MS = 20 * 1000;

    private static final String ZK_CLIENT_CONNECT_STRING = "dummyHost:dummyPort";

    private static final String STORAGE_ROOT_PATH = "/ambry/testCluster/helixPropertyStore";

    private static final long LATCH_TIMEOUT_MS = 1000;

    private static final List<String> receivedTopicsByListener0 = new ArrayList<>();

    private static final List<String> receivedTopicsByListener1 = new ArrayList<>();

    private static final List<String> receivedMessagesByListener0 = new ArrayList<>();

    private static final List<String> receivedMessagesByListener1 = new ArrayList<>();

    private static final AtomicReference<CountDownLatch> latch0 = new AtomicReference<>();

    private static final AtomicReference<CountDownLatch> latch1 = new AtomicReference<>();

    private static final List<TopicListener<String>> listeners = new ArrayList<>();

    private static final List<String> refTopics = new ArrayList<>();

    private static final List<String> refMessages = new ArrayList<>();

    private static final HelixPropertyStoreConfig storeConfig = HelixNotifierTest.getHelixStoreConfig(HelixNotifierTest.ZK_CLIENT_CONNECT_STRING, HelixNotifierTest.ZK_CLIENT_SESSION_TIMEOUT_MS, HelixNotifierTest.ZK_CLIENT_CONNECT_TIMEOUT_MS, HelixNotifierTest.STORAGE_ROOT_PATH);

    private final Map<String, MockHelixPropertyStore<ZNRecord>> storeKeyToMockStoreMap = new HashMap<>();

    private HelixNotifier helixNotifier;

    /**
     * Tests normal operations using a single {@link HelixNotifier} and two {@link TopicListener}s.
     *
     * @throws Exception
     * 		Any unexpected exceptions.
     */
    @Test
    public void testHelixNotifier() throws Exception {
        // Subscribe a topic and publish a message for the topic
        helixNotifier.subscribe(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.listeners.get(0));
        HelixNotifierTest.latch0.set(new CountDownLatch(1));
        helixNotifier.publish(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.refMessages.get(0));
        awaitLatchOrTimeout(HelixNotifierTest.latch0.get(), HelixNotifierTest.LATCH_TIMEOUT_MS);
        Assert.assertEquals("Received topic is different from expected", HelixNotifierTest.refTopics.get(0), HelixNotifierTest.receivedTopicsByListener0.get(0));
        Assert.assertEquals("Received message is different from expected", HelixNotifierTest.refMessages.get(0), HelixNotifierTest.receivedMessagesByListener0.get(0));
        // publish a different message for the topic
        resetListeners();
        HelixNotifierTest.latch0.set(new CountDownLatch(1));
        helixNotifier.publish(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.refMessages.get(1));
        awaitLatchOrTimeout(HelixNotifierTest.latch0.get(), HelixNotifierTest.LATCH_TIMEOUT_MS);
        Assert.assertEquals("Received topic is different from expected", HelixNotifierTest.refTopics.get(0), HelixNotifierTest.receivedTopicsByListener0.get(0));
        Assert.assertEquals("Received message is different from expected", HelixNotifierTest.refMessages.get(1), HelixNotifierTest.receivedMessagesByListener0.get(0));
        // Subscribe to a different topic and publish message for that topic
        resetListeners();
        helixNotifier.subscribe(HelixNotifierTest.refTopics.get(1), HelixNotifierTest.listeners.get(0));
        HelixNotifierTest.latch0.set(new CountDownLatch(1));
        helixNotifier.publish(HelixNotifierTest.refTopics.get(1), HelixNotifierTest.refMessages.get(0));
        awaitLatchOrTimeout(HelixNotifierTest.latch0.get(), HelixNotifierTest.LATCH_TIMEOUT_MS);
        Assert.assertEquals("Received topic is different from expected", HelixNotifierTest.refTopics.get(1), HelixNotifierTest.receivedTopicsByListener0.get(0));
        Assert.assertEquals("Received message is different from expected", HelixNotifierTest.refMessages.get(0), HelixNotifierTest.receivedMessagesByListener0.get(0));
    }

    /**
     * Tests two {@link TopicListener}s subscribe to the same topic through the same {@link HelixNotifier}.
     *
     * @throws Exception
     * 		Any unexpected exceptions.
     */
    @Test
    public void testTwoListenersForTheSameTopic() throws Exception {
        helixNotifier.subscribe(HelixNotifierTest.refTopics.get(1), HelixNotifierTest.listeners.get(0));
        helixNotifier.subscribe(HelixNotifierTest.refTopics.get(1), HelixNotifierTest.listeners.get(1));
        HelixNotifierTest.latch0.set(new CountDownLatch(1));
        HelixNotifierTest.latch1.set(new CountDownLatch(1));
        helixNotifier.publish(HelixNotifierTest.refTopics.get(1), HelixNotifierTest.refMessages.get(1));
        awaitLatchOrTimeout(HelixNotifierTest.latch0.get(), HelixNotifierTest.LATCH_TIMEOUT_MS);
        awaitLatchOrTimeout(HelixNotifierTest.latch1.get(), HelixNotifierTest.LATCH_TIMEOUT_MS);
        Assert.assertEquals("Received topic is different from expected", HelixNotifierTest.refTopics.get(1), HelixNotifierTest.receivedTopicsByListener0.get(0));
        Assert.assertEquals("Received message is different from expected", HelixNotifierTest.refMessages.get(1), HelixNotifierTest.receivedMessagesByListener0.get(0));
        Assert.assertEquals("Received topic is different from expected", HelixNotifierTest.refTopics.get(1), HelixNotifierTest.receivedTopicsByListener1.get(0));
        Assert.assertEquals("Received message is different from expected", HelixNotifierTest.refMessages.get(1), HelixNotifierTest.receivedMessagesByListener1.get(0));
    }

    /**
     * Tests two {@link TopicListener}s subscribe to two different topics through the same {@link HelixNotifier}.
     *
     * @throws Exception
     * 		Any unexpected exceptions.
     */
    @Test
    public void testTwoListenersForDifferentTopics() throws Exception {
        helixNotifier.subscribe(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.listeners.get(0));
        helixNotifier.subscribe(HelixNotifierTest.refTopics.get(1), HelixNotifierTest.listeners.get(1));
        HelixNotifierTest.latch0.set(new CountDownLatch(1));
        HelixNotifierTest.latch1.set(new CountDownLatch(1));
        helixNotifier.publish(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.refMessages.get(0));
        helixNotifier.publish(HelixNotifierTest.refTopics.get(1), HelixNotifierTest.refMessages.get(1));
        awaitLatchOrTimeout(HelixNotifierTest.latch0.get(), HelixNotifierTest.LATCH_TIMEOUT_MS);
        awaitLatchOrTimeout(HelixNotifierTest.latch1.get(), HelixNotifierTest.LATCH_TIMEOUT_MS);
        Assert.assertEquals("Received topic is different from expected", HelixNotifierTest.refTopics.get(0), HelixNotifierTest.receivedTopicsByListener0.get(0));
        Assert.assertEquals("Received message is different from expected", HelixNotifierTest.refMessages.get(0), HelixNotifierTest.receivedMessagesByListener0.get(0));
        Assert.assertEquals("Received topic is different from expected", HelixNotifierTest.refTopics.get(1), HelixNotifierTest.receivedTopicsByListener1.get(0));
        Assert.assertEquals("Received message is different from expected", HelixNotifierTest.refMessages.get(1), HelixNotifierTest.receivedMessagesByListener1.get(0));
    }

    /**
     * Tests one {@link TopicListener} simultaneously listens to two different topics through the same {@link HelixNotifier}.
     *
     * @throws Exception
     * 		Any unexpected exceptions.
     */
    @Test
    public void testOneListenerForTwoDifferentTopics() throws Exception {
        helixNotifier.subscribe(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.listeners.get(0));
        helixNotifier.subscribe(HelixNotifierTest.refTopics.get(1), HelixNotifierTest.listeners.get(0));
        HelixNotifierTest.latch0.set(new CountDownLatch(2));
        helixNotifier.publish(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.refMessages.get(0));
        helixNotifier.publish(HelixNotifierTest.refTopics.get(1), HelixNotifierTest.refMessages.get(1));
        awaitLatchOrTimeout(HelixNotifierTest.latch0.get(), HelixNotifierTest.LATCH_TIMEOUT_MS);
        Assert.assertEquals("Received topics are different from expected", new HashSet<>(HelixNotifierTest.refTopics), new HashSet<>(HelixNotifierTest.receivedTopicsByListener0));
        Assert.assertEquals("Received messages are different from expected", new HashSet<>(HelixNotifierTest.refMessages), new HashSet<>(HelixNotifierTest.receivedMessagesByListener0));
    }

    /**
     * Tests unsubscribing a topic. This test is meaningful when using {@link MockHelixPropertyStore}, where
     * a single thread guarantees to know if {@link TopicListener#onMessage(String, Object)} has been
     * called or not.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testUnsubscribeTopic() throws Exception {
        // Subscribe a topic and publish a message for the topic
        helixNotifier.subscribe(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.listeners.get(0));
        HelixNotifierTest.latch0.set(new CountDownLatch(1));
        helixNotifier.publish(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.refMessages.get(0));
        awaitLatchOrTimeout(HelixNotifierTest.latch0.get(), HelixNotifierTest.LATCH_TIMEOUT_MS);
        Assert.assertEquals("Received topic is different from expected", HelixNotifierTest.refTopics.get(0), HelixNotifierTest.receivedTopicsByListener0.get(0));
        Assert.assertEquals("Received message is different from expected", HelixNotifierTest.refMessages.get(0), HelixNotifierTest.receivedMessagesByListener0.get(0));
        // unsubscribe the listener
        helixNotifier.unsubscribe(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.listeners.get(0));
        HelixNotifierTest.latch0.set(new CountDownLatch(1));
        helixNotifier.publish(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.refMessages.get(0));
        try {
            awaitLatchOrTimeout(HelixNotifierTest.latch0.get(), 1);
            Assert.fail("should have thrown");
        } catch (TimeoutException e) {
            // expected, since the TopicListener would not be called, and the latch would not be counted down.
        }
        // unsubscribe the listener again should be silent
        helixNotifier.unsubscribe(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.listeners.get(0));
    }

    /**
     * Tests unsubscribing a non-existent {@link TopicListener}. This should not throw any exception.
     */
    @Test
    public void testUnsubscribeNonExistentListeners() {
        // unsubscribe a non-existent listener should be silent
        helixNotifier.unsubscribe(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.listeners.get(0));
    }

    /**
     * Tests publishing a message to a topic without any {@link TopicListener}. This test is meaningful
     * when using {@link MockHelixPropertyStore}, where a single thread guarantees to know if
     * {@link TopicListener#onMessage(String, Object)} has been called or not.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void publishMessageToTopicWithNoListeners() throws Exception {
        HelixNotifierTest.latch0.set(new CountDownLatch(1));
        helixNotifier.publish(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.refMessages.get(0));
        try {
            awaitLatchOrTimeout(HelixNotifierTest.latch0.get(), 1);
            Assert.fail("should have thrown");
        } catch (TimeoutException e) {
            // expected, since the TopicListener would not be called, and the latch would not be counted down.
        }
    }

    /**
     * Tests a {@link TopicListener} subscribes a topic from one {@link HelixNotifier}, and receives a message sent
     * through another {@link HelixNotifier} for the same topic.
     *
     * @throws Exception
     * 		Any unexpected exceptions.
     */
    @Test
    public void testOneListenerTwoNotifiers() throws Exception {
        helixNotifier.subscribe(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.listeners.get(0));
        HelixNotifierTest.latch0.set(new CountDownLatch(1));
        HelixNotifier helixNotifier2 = new HelixNotifier(getMockHelixStore(HelixNotifierTest.storeConfig));
        helixNotifier2.publish(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.refMessages.get(0));
        awaitLatchOrTimeout(HelixNotifierTest.latch0.get(), HelixNotifierTest.LATCH_TIMEOUT_MS);
        Assert.assertEquals(1, HelixNotifierTest.receivedTopicsByListener0.size());
        Assert.assertEquals(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.receivedTopicsByListener0.get(0));
        Assert.assertEquals(1, HelixNotifierTest.receivedMessagesByListener0.size());
        Assert.assertEquals(HelixNotifierTest.refMessages.get(0), HelixNotifierTest.receivedMessagesByListener0.get(0));
    }

    /**
     * Tests when publishing a message to a {@link TopicListener}, if the {@link TopicListener#onMessage(String, Object)}
     * method throws exception, it will not crash the {@link HelixNotifier}.
     */
    @Test
    public void testPublishMessageToBadListener() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        TopicListener listener = ( topic, message) -> {
            latch.countDown();
            throw new RuntimeException("Exception thrown from TopicListener");
        };
        String topic = "topic";
        helixNotifier.subscribe(topic, listener);
        helixNotifier.publish(topic, "message");
        awaitLatchOrTimeout(latch, HelixNotifierTest.LATCH_TIMEOUT_MS);
    }

    /**
     * Tests a number of bad inputs.
     *
     * @throws Exception
     * 		Any unexpected exceptions.
     */
    @Test
    public void testBadInputs() throws Exception {
        // subscribe to null topic
        try {
            helixNotifier.subscribe(null, HelixNotifierTest.listeners.get(0));
            Assert.fail("Should have thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
        // subscribe using null listener
        try {
            helixNotifier.subscribe(HelixNotifierTest.refTopics.get(0), null);
            Assert.fail("Should have thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
        // publish message to a null topic
        try {
            helixNotifier.publish(null, HelixNotifierTest.refMessages.get(0));
            Assert.fail("Should have thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
        // publish null message to a topic
        try {
            helixNotifier.publish(HelixNotifierTest.refTopics.get(0), null);
            Assert.fail("Should have thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
        // unsubscribe a null listener from a topic
        try {
            helixNotifier.unsubscribe(HelixNotifierTest.refTopics.get(0), null);
            Assert.fail("Should have thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
        // unsubscribe a listener from a null topic
        try {
            helixNotifier.unsubscribe(null, HelixNotifierTest.listeners.get(0));
            Assert.fail("Should have thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
        // pass null storeConfig to construct a HelixNotifier
        try {
            new HelixNotifier(HelixNotifierTest.ZK_CLIENT_CONNECT_STRING, ((HelixPropertyStoreConfig) (null)));
            Assert.fail("Should have thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
        // pass null store to construct a HelixNotifier
        try {
            new HelixNotifier(((HelixPropertyStore<ZNRecord>) (null)));
            Assert.fail("Should have thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * Tests when publishing a message through {@link HelixNotifier} fails, it will not throw exception or fail the
     * caller's thread.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testFailToPublishMessage() throws Exception {
        helixNotifier = new HelixNotifier(new MockHelixPropertyStore<ZNRecord>(true, false));
        helixNotifier.publish(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.refMessages.get(0));
    }

    /**
     * Tests a corner case when a {@link HelixNotifier} sends messages to local {@link TopicListener}s, there is a
     * slight chance that when the {@link HelixNotifier} tries to fetch the message, it is already deleted by someone
     * else, and may fetch {@code null}. This test ensures that in this case no exception will be thrown.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testReadNullRecordWhenSendMessageToLocalListeners() throws Exception {
        helixNotifier = new HelixNotifier(new MockHelixPropertyStore<ZNRecord>(false, true));
        helixNotifier.publish(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.refMessages.get(0));
        helixNotifier.subscribe(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.listeners.get(0));
        HelixNotifierTest.latch0.set(new CountDownLatch(1));
        helixNotifier.publish(HelixNotifierTest.refTopics.get(0), HelixNotifierTest.refMessages.get(0));
        Assert.assertEquals("TopicListener incorrectly called when the record is null", 1, HelixNotifierTest.latch0.get().getCount());
    }

    /**
     * A {@link TopicListener} for test purpose. It will count down the passed in latch after it has processed the
     * message.
     */
    private class ListenerForTest implements TopicListener<String> {
        private final AtomicReference<CountDownLatch> latch;

        private final List<String> receivedTopics;

        private final List<String> receivedMessages;

        public ListenerForTest(AtomicReference<CountDownLatch> latch, List<String> receivedTopics, List<String> receivedMessages) {
            this.latch = latch;
            this.receivedTopics = receivedTopics;
            this.receivedMessages = receivedMessages;
        }

        @Override
        public void onMessage(String topic, String message) {
            System.out.println(((("Topic is: " + topic) + ", referenceMessage1 is: ") + message));
            receivedTopics.add(topic);
            receivedMessages.add(message);
            if ((latch.get().getCount()) > 0) {
                latch.get().countDown();
            } else {
                // since this callback is called in the main thread, it is ok to fail the test here.
                Assert.fail("Countdown latch has already been counted down to 0");
            }
        }
    }
}

