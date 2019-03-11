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
package org.apache.activemq.transport.mqtt;


import MqttConnectOptions.MQTT_VERSION_3_1;
import MqttConnectOptions.MQTT_VERSION_3_1_1;
import Session.AUTO_ACKNOWLEDGE;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.util.Wait;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PahoMQTTTest extends MQTTTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(PahoMQTTTest.class);

    @Test(timeout = 90000)
    public void testLotsOfClients() throws Exception {
        final int CLIENTS = Integer.getInteger("PahoMQTTTest.CLIENTS", 100);
        PahoMQTTTest.LOG.info("Using: {} clients", CLIENTS);
        ActiveMQConnection activeMQConnection = ((ActiveMQConnection) (cf.createConnection()));
        activeMQConnection.start();
        Session s = activeMQConnection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = createConsumer(s, "test");
        final AtomicInteger receiveCounter = new AtomicInteger();
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                receiveCounter.incrementAndGet();
            }
        });
        final AtomicReference<Throwable> asyncError = new AtomicReference<Throwable>();
        final CountDownLatch connectedDoneLatch = new CountDownLatch(CLIENTS);
        final CountDownLatch disconnectDoneLatch = new CountDownLatch(CLIENTS);
        final CountDownLatch sendBarrier = new CountDownLatch(1);
        for (int i = 0; i < CLIENTS; i++) {
            Thread.sleep(10);
            new Thread(null, null, ("client:" + i)) {
                @Override
                public void run() {
                    try {
                        MqttClient client = new MqttClient(("tcp://localhost:" + (getPort())), Thread.currentThread().getName(), new MemoryPersistence());
                        client.connect();
                        connectedDoneLatch.countDown();
                        sendBarrier.await();
                        for (int i = 0; i < 10; i++) {
                            Thread.sleep(1000);
                            client.publish("test", "hello".getBytes(StandardCharsets.UTF_8), 1, false);
                        }
                        client.disconnect();
                        client.close();
                    } catch (Throwable e) {
                        e.printStackTrace();
                        asyncError.set(e);
                    } finally {
                        disconnectDoneLatch.countDown();
                    }
                }
            }.start();
        }
        connectedDoneLatch.await();
        Assert.assertNull(("Async error: " + (asyncError.get())), asyncError.get());
        sendBarrier.countDown();
        PahoMQTTTest.LOG.info("All clients connected... waiting to receive sent messages...");
        // We should eventually get all the messages.
        within(30, TimeUnit.SECONDS, new MQTTTestSupport.Task() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(((receiveCounter.get()) == (CLIENTS * 10)));
            }
        });
        PahoMQTTTest.LOG.info("All messages received.");
        disconnectDoneLatch.await();
        Assert.assertNull(("Async error: " + (asyncError.get())), asyncError.get());
    }

    @Test(timeout = 90000)
    public void testSendAndReceiveMQTT() throws Exception {
        ActiveMQConnection activeMQConnection = ((ActiveMQConnection) (cf.createConnection()));
        activeMQConnection.start();
        Session s = activeMQConnection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = createConsumer(s, "test");
        MqttClient client = new MqttClient(("tcp://localhost:" + (getPort())), "clientid", new MemoryPersistence());
        client.connect();
        client.publish("test", "hello".getBytes(StandardCharsets.UTF_8), 1, false);
        Message msg = consumer.receive((100 * 5));
        Assert.assertNotNull(msg);
        client.disconnect();
        client.close();
    }

    @Test(timeout = 90000)
    public void testSubs() throws Exception {
        final PahoMQTTTest.DefaultListener listener = new PahoMQTTTest.DefaultListener();
        // subscriber connects and creates durable sub
        MqttClient client = createClient(false, "receive", listener);
        final String ACCOUNT_PREFIX = "test/";
        client.subscribe((ACCOUNT_PREFIX + "1/2/3"));
        client.subscribe((ACCOUNT_PREFIX + "a/+/#"));
        client.subscribe((ACCOUNT_PREFIX + "#"));
        Assert.assertTrue(((client.getPendingDeliveryTokens().length) == 0));
        String expectedResult = "should get everything";
        client.publish((ACCOUNT_PREFIX + "1/2/3/4"), expectedResult.getBytes(StandardCharsets.UTF_8), 0, false);
        // One delivery for topic  ACCOUNT_PREFIX + "#"
        String result = listener.messageQ.poll(20, TimeUnit.SECONDS).getValue();
        Assert.assertTrue(((client.getPendingDeliveryTokens().length) == 0));
        Assert.assertEquals(expectedResult, result);
        expectedResult = "should get everything";
        client.publish((ACCOUNT_PREFIX + "a/1/2"), expectedResult.getBytes(StandardCharsets.UTF_8), 0, false);
        // One delivery for topic  ACCOUNT_PREFIX + "a/1/2"
        result = listener.messageQ.poll(20, TimeUnit.SECONDS).getValue();
        Assert.assertEquals(expectedResult, result);
        // One delivery for topic  ACCOUNT_PREFIX + "#"
        result = listener.messageQ.poll(20, TimeUnit.SECONDS).getValue();
        Assert.assertEquals(expectedResult, result);
        Assert.assertTrue(((client.getPendingDeliveryTokens().length) == 0));
        client.unsubscribe((ACCOUNT_PREFIX + "a/+/#"));
        client.unsubscribe((ACCOUNT_PREFIX + "#"));
        Assert.assertTrue(((client.getPendingDeliveryTokens().length) == 0));
        expectedResult = "should still get 1/2/3";
        client.publish((ACCOUNT_PREFIX + "1/2/3"), expectedResult.getBytes(StandardCharsets.UTF_8), 0, false);
        // One delivery for topic  ACCOUNT_PREFIX + "1/2/3"
        result = listener.messageQ.poll(20, TimeUnit.SECONDS).getValue();
        Assert.assertEquals(expectedResult, result);
        Assert.assertTrue(((client.getPendingDeliveryTokens().length) == 0));
        client.disconnect();
        client.close();
    }

    @Test(timeout = 90000)
    public void testOverlappingTopics() throws Exception {
        final PahoMQTTTest.DefaultListener listener = new PahoMQTTTest.DefaultListener();
        // subscriber connects and creates durable sub
        MqttClient client = createClient(false, "receive", listener);
        final String ACCOUNT_PREFIX = "test/";
        // *****************************************
        // check a simple # subscribe works
        // *****************************************
        client.subscribe((ACCOUNT_PREFIX + "#"));
        Assert.assertTrue(((client.getPendingDeliveryTokens().length) == 0));
        String expectedResult = "hello mqtt broker on hash";
        client.publish((ACCOUNT_PREFIX + "a/b/c"), expectedResult.getBytes(StandardCharsets.UTF_8), 0, false);
        String result = listener.messageQ.poll(20, TimeUnit.SECONDS).getValue();
        Assert.assertEquals(expectedResult, result);
        Assert.assertTrue(((client.getPendingDeliveryTokens().length) == 0));
        expectedResult = "hello mqtt broker on a different topic";
        client.publish((ACCOUNT_PREFIX + "1/2/3/4/5/6"), expectedResult.getBytes(StandardCharsets.UTF_8), 0, false);
        result = listener.messageQ.poll(20, TimeUnit.SECONDS).getValue();
        Assert.assertEquals(expectedResult, result);
        Assert.assertTrue(((client.getPendingDeliveryTokens().length) == 0));
        // *****************************************
        // now subscribe on a topic that overlaps the root # wildcard - we
        // should still get everything
        // *****************************************
        client.subscribe((ACCOUNT_PREFIX + "1/2/3"));
        Assert.assertTrue(((client.getPendingDeliveryTokens().length) == 0));
        expectedResult = "hello mqtt broker on explicit topic";
        client.publish((ACCOUNT_PREFIX + "1/2/3"), expectedResult.getBytes(StandardCharsets.UTF_8), 0, false);
        // One message from topic subscription on ACCOUNT_PREFIX + "#"
        result = listener.messageQ.poll(20, TimeUnit.SECONDS).getValue();
        Assert.assertEquals(expectedResult, result);
        // One message from topic subscription on ACCOUNT_PREFIX + "1/2/3"
        result = listener.messageQ.poll(20, TimeUnit.SECONDS).getValue();
        Assert.assertEquals(expectedResult, result);
        Assert.assertTrue(((client.getPendingDeliveryTokens().length) == 0));
        expectedResult = "hello mqtt broker on some other topic";
        client.publish((ACCOUNT_PREFIX + "a/b/c/d/e"), expectedResult.getBytes(StandardCharsets.UTF_8), 0, false);
        result = listener.messageQ.poll(20, TimeUnit.SECONDS).getValue();
        Assert.assertEquals(expectedResult, result);
        Assert.assertTrue(((client.getPendingDeliveryTokens().length) == 0));
        // *****************************************
        // now unsub hash - we should only get called back on 1/2/3
        // *****************************************
        client.unsubscribe((ACCOUNT_PREFIX + "#"));
        Assert.assertTrue(((client.getPendingDeliveryTokens().length) == 0));
        expectedResult = "this should not come back...";
        client.publish((ACCOUNT_PREFIX + "1/2/3/4"), expectedResult.getBytes(StandardCharsets.UTF_8), 0, false);
        Assert.assertNull(listener.messageQ.poll(3, TimeUnit.SECONDS));
        Assert.assertTrue(((client.getPendingDeliveryTokens().length) == 0));
        expectedResult = "this should not come back either...";
        client.publish((ACCOUNT_PREFIX + "a/b/c"), expectedResult.getBytes(StandardCharsets.UTF_8), 0, false);
        Assert.assertNull(listener.messageQ.poll(3, TimeUnit.SECONDS));
        Assert.assertTrue(((client.getPendingDeliveryTokens().length) == 0));
        client.disconnect();
        client.close();
    }

    @Test(timeout = 90000)
    public void testCleanSession() throws Exception {
        String topic = "test";
        final PahoMQTTTest.DefaultListener listener = new PahoMQTTTest.DefaultListener();
        // subscriber connects and creates durable sub
        PahoMQTTTest.LOG.info("Connecting durable subscriber...");
        MqttClient client = createClient(false, "receive", listener);
        // subscribe and wait for the retain message to arrive
        PahoMQTTTest.LOG.info("Subscribing durable subscriber...");
        client.subscribe(topic, 1);
        Assert.assertTrue(((client.getPendingDeliveryTokens().length) == 0));
        disconnect(client);
        PahoMQTTTest.LOG.info("Disconnected durable subscriber.");
        // Publish message with QoS 1
        MqttClient client2 = createClient(true, "publish", listener);
        PahoMQTTTest.LOG.info("Publish message with QoS 1...");
        String expectedResult = "QOS 1 message";
        client2.publish(topic, expectedResult.getBytes(StandardCharsets.UTF_8), 1, false);
        waitForDelivery(client2);
        // Publish message with QoS 0
        PahoMQTTTest.LOG.info("Publish message with QoS 0...");
        expectedResult = "QOS 0 message";
        client2.publish(topic, expectedResult.getBytes(StandardCharsets.UTF_8), 0, false);
        waitForDelivery(client2);
        // subscriber reconnects
        PahoMQTTTest.LOG.info("Reconnecting durable subscriber...");
        MqttClient client3 = createClient(false, "receive", listener);
        PahoMQTTTest.LOG.info("Subscribing durable subscriber...");
        client3.subscribe(topic, 1);
        Assert.assertTrue(Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (listener.received.get()) == 2;
            }
        }, TimeUnit.SECONDS.toMillis(15), TimeUnit.MILLISECONDS.toMillis(100)));
        Assert.assertEquals(2, listener.received.get());
        disconnect(client3);
        PahoMQTTTest.LOG.info("Disconnected durable subscriber.");
        // make sure we consumed everything
        Assert.assertTrue(listener.received.compareAndSet(2, 0));
        PahoMQTTTest.LOG.info("Reconnecting durable subscriber...");
        MqttClient client4 = createClient(false, "receive", listener);
        PahoMQTTTest.LOG.info("Subscribing durable subscriber...");
        client4.subscribe(topic, 1);
        TimeUnit.SECONDS.sleep(3);
        Assert.assertEquals(0, listener.received.get());
        client2.disconnect();
        client2.close();
        client4.disconnect();
        client4.close();
    }

    @Test(timeout = 90000)
    public void testClientIdSpecialChars() throws Exception {
        testClientIdSpecialChars(MQTT_VERSION_3_1);
        testClientIdSpecialChars(MQTT_VERSION_3_1_1);
    }

    @Test(timeout = 300000)
    public void testActiveMQWildCards1() throws Exception {
        final PahoMQTTTest.DefaultListener listener = new PahoMQTTTest.DefaultListener();
        MqttClient client = createClient(false, "receive", listener);
        final String ACCOUNT_PREFIX = "test/";
        client.subscribe((ACCOUNT_PREFIX + "a/#"));
        Assert.assertTrue(((client.getPendingDeliveryTokens().length) == 0));
        String expectedResult = "should get this 1";
        String topic = ACCOUNT_PREFIX + "a/b/1.2.3*4>";
        client.publish(topic, expectedResult.getBytes(), 0, false);
        AbstractMap.SimpleEntry<String, String> entry = listener.messageQ.poll(20, TimeUnit.SECONDS);
        Assert.assertEquals(topic, entry.getKey());
        Assert.assertEquals(expectedResult, entry.getValue());
        Assert.assertTrue(((client.getPendingDeliveryTokens().length) == 0));
    }

    static class DefaultListener implements MqttCallback {
        final AtomicInteger received = new AtomicInteger();

        final BlockingQueue<AbstractMap.SimpleEntry<String, String>> messageQ = new ArrayBlockingQueue<AbstractMap.SimpleEntry<String, String>>(10);

        @Override
        public void connectionLost(Throwable cause) {
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            PahoMQTTTest.LOG.info("Received: {}", message);
            received.incrementAndGet();
            messageQ.put(new AbstractMap.SimpleEntry(topic, new String(message.getPayload(), StandardCharsets.UTF_8)));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
        }
    }
}

