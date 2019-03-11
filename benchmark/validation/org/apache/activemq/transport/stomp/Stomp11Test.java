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
package org.apache.activemq.transport.stomp;


import Stomp.Headers.Message.BROWSER;
import Stomp.Headers.Message.DESTINATION;
import Stomp.Headers.Message.SUBSCRIPTION;
import Stomp.Headers.Response.RECEIPT_ID;
import Stomp.Responses.MESSAGE;
import Stomp.V1_1;
import java.io.DataInputStream;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.management.ObjectName;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Stomp.NULL;


public class Stomp11Test extends StompTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(Stomp11Test.class);

    private Connection connection;

    private Session session;

    private ActiveMQQueue queue;

    @Test(timeout = 60000)
    public void testConnect() throws Exception {
        String connectFrame = ("STOMP\n" + ((((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "host:localhost\n") + "request-id:1\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        Assert.assertTrue(f.startsWith("CONNECTED"));
        Assert.assertTrue(((f.indexOf("response-id:1")) >= 0));
        Assert.assertTrue(((f.indexOf("version:1.1")) >= 0));
        Assert.assertTrue(((f.indexOf("session:")) >= 0));
    }

    @Test(timeout = 60000)
    public void testConnectedNeverEncoded() throws Exception {
        String connectFrame = ("STOMP\n" + ((((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "host:localhost\n") + "request-id:1\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        Assert.assertTrue(f.startsWith("CONNECTED"));
        Assert.assertTrue(((f.indexOf("response-id:1")) >= 0));
        Assert.assertTrue(((f.indexOf("version:1.1")) >= 0));
        Assert.assertTrue(((f.indexOf("session:")) >= 0));
        int sessionHeader = f.indexOf("session:");
        f = f.substring((sessionHeader + ("session:".length())));
        Stomp11Test.LOG.info(("session header follows: " + f));
        Assert.assertTrue(f.startsWith("ID:"));
    }

    @Test(timeout = 60000)
    public void testConnectWithVersionOptions() throws Exception {
        String connectFrame = ("STOMP\n" + (((("login:system\n" + "passcode:manager\n") + "accept-version:1.0,1.1\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        Assert.assertTrue(f.startsWith("CONNECTED"));
        Assert.assertTrue(((f.indexOf("version:1.1")) >= 0));
        Assert.assertTrue(((f.indexOf("session:")) >= 0));
    }

    @Test(timeout = 60000)
    public void testConnectWithValidFallback() throws Exception {
        String connectFrame = ("STOMP\n" + (((("login:system\n" + "passcode:manager\n") + "accept-version:1.0,10.1\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        Assert.assertTrue(f.startsWith("CONNECTED"));
        Assert.assertTrue(((f.indexOf("version:1.0")) >= 0));
        Assert.assertTrue(((f.indexOf("session:")) >= 0));
    }

    @Test(timeout = 60000)
    public void testConnectWithInvalidFallback() throws Exception {
        String connectFrame = ("STOMP\n" + (((("login:system\n" + "passcode:manager\n") + "accept-version:9.0,10.1\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        Assert.assertTrue(f.startsWith("ERROR"));
        Assert.assertTrue(((f.indexOf("version")) >= 0));
        Assert.assertTrue(((f.indexOf("message:")) >= 0));
    }

    @Test(timeout = 60000)
    public void testHeartbeats() throws Exception {
        String connectFrame = ("STOMP\n" + ((((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "heart-beat:0,1000\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame().trim();
        Stomp11Test.LOG.info(("Broker sent: " + f));
        Assert.assertTrue("Failed to receive a connected frame.", f.startsWith("CONNECTED"));
        Assert.assertTrue("Frame should have a versoion 1.1 header.", ((f.indexOf("version:1.1")) >= 0));
        Assert.assertTrue("Frame should have a heart beat header.", ((f.indexOf("heart-beat:")) >= 0));
        Assert.assertTrue("Frame should have a session header.", ((f.indexOf("session:")) >= 0));
        stompConnection.getStompSocket().getOutputStream().write('\n');
        DataInputStream in = new DataInputStream(stompConnection.getStompSocket().getInputStream());
        in.read();
        {
            long startTime = System.currentTimeMillis();
            int input = in.read();
            Assert.assertEquals("did not receive the correct hear beat value", '\n', input);
            long endTime = System.currentTimeMillis();
            Assert.assertTrue("Broker did not send KeepAlive in time", ((endTime - startTime) >= 900));
        }
        {
            long startTime = System.currentTimeMillis();
            int input = in.read();
            Assert.assertEquals("did not receive the correct hear beat value", '\n', input);
            long endTime = System.currentTimeMillis();
            Assert.assertTrue("Broker did not send KeepAlive in time", ((endTime - startTime) >= 900));
        }
    }

    @Test(timeout = 60000)
    public void testHeartbeatsDropsIdleConnection() throws Exception {
        String connectFrame = ("STOMP\n" + ((((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "heart-beat:1000,0\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Assert.assertTrue(f.startsWith("CONNECTED"));
        Assert.assertTrue(((f.indexOf("version:1.1")) >= 0));
        Assert.assertTrue(((f.indexOf("heart-beat:")) >= 0));
        Assert.assertTrue(((f.indexOf("session:")) >= 0));
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        long startTime = System.currentTimeMillis();
        try {
            f = stompConnection.receiveFrame();
            Stomp11Test.LOG.debug(("Broker sent: " + f));
            Assert.fail();
        } catch (Exception e) {
        }
        long endTime = System.currentTimeMillis();
        Assert.assertTrue("Broker did close idle connection in time.", ((endTime - startTime) >= 1000));
    }

    @Test(timeout = 60000)
    public void testHeartbeatsKeepsConnectionOpen() throws Exception {
        String connectFrame = ("STOMP\n" + ((((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "heart-beat:2000,0\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Assert.assertTrue(f.startsWith("CONNECTED"));
        Assert.assertTrue(((f.indexOf("version:1.1")) >= 0));
        Assert.assertTrue(((f.indexOf("heart-beat:")) >= 0));
        Assert.assertTrue(((f.indexOf("session:")) >= 0));
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        String message = (((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(message);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    Stomp11Test.LOG.info("Sending next KeepAlive");
                    stompConnection.keepAlive();
                } catch (Exception e) {
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(20);
        String frame = ((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "id:12345\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame stompFrame = stompConnection.receive();
        Assert.assertTrue(stompFrame.getAction().equals("MESSAGE"));
        service.shutdownNow();
    }

    @Test(timeout = 60000)
    public void testSendAfterMissingHeartbeat() throws Exception {
        String connectFrame = ("STOMP\n" + ((((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "heart-beat:1000,0\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Assert.assertTrue(f.startsWith("CONNECTED"));
        Assert.assertTrue(((f.indexOf("version:1.1")) >= 0));
        Assert.assertTrue(((f.indexOf("heart-beat:")) >= 0));
        Assert.assertTrue(((f.indexOf("session:")) >= 0));
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 0;
            }
        }, TimeUnit.SECONDS.toMillis(5), TimeUnit.MILLISECONDS.toMillis(25));
        try {
            String message = ((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "receipt:1\n\n") + "Hello World") + (NULL);
            stompConnection.sendFrame(message);
            stompConnection.receiveFrame();
            Assert.fail("SEND frame has been accepted after missing heart beat");
        } catch (Exception ex) {
            Stomp11Test.LOG.info(ex.getMessage());
        }
    }

    @Test(timeout = 60000)
    public void testRejectInvalidHeartbeats1() throws Exception {
        String connectFrame = ("STOMP\n" + ((((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "heart-beat:0\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        Assert.assertTrue(f.startsWith("ERROR"));
        Assert.assertTrue(((f.indexOf("heart-beat")) >= 0));
        Assert.assertTrue(((f.indexOf("message:")) >= 0));
    }

    @Test(timeout = 60000)
    public void testRejectInvalidHeartbeats2() throws Exception {
        String connectFrame = ("STOMP\n" + ((((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "heart-beat:T,0\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        Assert.assertTrue(f.startsWith("ERROR"));
        Assert.assertTrue(((f.indexOf("heart-beat")) >= 0));
        Assert.assertTrue(((f.indexOf("message:")) >= 0));
    }

    @Test(timeout = 60000)
    public void testRejectInvalidHeartbeats3() throws Exception {
        String connectFrame = ("STOMP\n" + ((((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "heart-beat:100,10,50\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        Assert.assertTrue(f.startsWith("ERROR"));
        Assert.assertTrue(((f.indexOf("heart-beat")) >= 0));
        Assert.assertTrue(((f.indexOf("message:")) >= 0));
    }

    @Test(timeout = 60000)
    public void testSubscribeAndUnsubscribe() throws Exception {
        String connectFrame = ("STOMP\n" + (((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        Assert.assertTrue(f.startsWith("CONNECTED"));
        String message = (((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(message);
        String frame = ((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "id:12345\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame stompFrame = stompConnection.receive();
        Assert.assertTrue(stompFrame.getAction().equals("MESSAGE"));
        frame = ((((("UNSUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "receipt:1\n") + "id:12345\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        stompFrame = stompConnection.receive();
        Assert.assertTrue(stompFrame.getAction().equals("RECEIPT"));
        stompConnection.sendFrame(message);
        try {
            frame = stompConnection.receiveFrame(2000);
            Stomp11Test.LOG.info(("Received frame: " + frame));
            Assert.fail("No message should have been received since subscription was removed");
        } catch (SocketTimeoutException e) {
        }
    }

    @Test(timeout = 60000)
    public void testSubscribeWithNoId() throws Exception {
        String connectFrame = ("STOMP\n" + (((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        Assert.assertTrue(f.startsWith("CONNECTED"));
        String frame = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("ERROR"));
    }

    @Test(timeout = 60000)
    public void testUnsubscribeWithNoId() throws Exception {
        String connectFrame = ("STOMP\n" + (((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        Assert.assertTrue(f.startsWith("CONNECTED"));
        String frame = (((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "receipt:1\n") + "id:12345\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("RECEIPT"));
        frame = (((("UNSUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("ERROR"));
    }

    @Test(timeout = 60000)
    public void testAckMessageWithId() throws Exception {
        String connectFrame = ("STOMP\n" + (((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        Assert.assertTrue(f.startsWith("CONNECTED"));
        String message = (((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(message);
        String frame = ((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "id:12345\n") + "ack:client\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame received = stompConnection.receive();
        Assert.assertTrue(received.getAction().equals("MESSAGE"));
        frame = ((("ACK\n" + ("subscription:12345\n" + "message-id:")) + (received.getHeaders().get("message-id"))) + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = (((("UNSUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "id:12345\n\n") + (NULL);
        stompConnection.sendFrame(frame);
    }

    @Test(timeout = 60000)
    public void testAckMessageWithNoId() throws Exception {
        String connectFrame = ("STOMP\n" + (((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        Assert.assertTrue(f.startsWith("CONNECTED"));
        String message = (((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(message);
        String subscribe = ((((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "activemq.prefetchSize=1") + "\n") + "id:12345\n") + "ack:client\n\n") + (NULL);
        stompConnection.sendFrame(subscribe);
        StompFrame received = stompConnection.receive();
        Stomp11Test.LOG.info("Received Frame: {}", received);
        Assert.assertTrue(("Expected MESSAGE but got: " + (received.getAction())), received.getAction().equals("MESSAGE"));
        String ack = ((("ACK\n" + "message-id:") + (received.getHeaders().get("message-id"))) + "\n\n") + (NULL);
        stompConnection.sendFrame(ack);
        StompFrame error = stompConnection.receive();
        Stomp11Test.LOG.info("Received Frame: {}", error);
        Assert.assertTrue(("Expected ERROR but got: " + (error.getAction())), error.getAction().equals("ERROR"));
        String unsub = (((("UNSUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "id:12345\n\n") + (NULL);
        stompConnection.sendFrame(unsub);
    }

    @Test(timeout = 60000)
    public void testSubscribeWithWildcardSubscription() throws Exception {
        String connectFrame = ("STOMP\n" + (((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        Assert.assertTrue(f.startsWith("CONNECTED"));
        String message = ("SEND\n" + (("destination:/queue/a.b.c" + "\n\n") + "Hello World")) + (NULL);
        stompConnection.sendFrame(message);
        message = ("SEND\n" + (("destination:/queue/a.b" + "\n\n") + "Hello World")) + (NULL);
        stompConnection.sendFrame(message);
        String frame = ("SUBSCRIBE\n" + ((("destination:/queue/a.b.>" + "\n") + "id:12345\n") + "ack:auto\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame received = stompConnection.receive();
        Assert.assertNotNull(received);
        received = stompConnection.receive();
        Assert.assertNotNull(received);
    }

    @Test(timeout = 60000)
    public void testQueueBrowerSubscription() throws Exception {
        final int MSG_COUNT = 10;
        String connectFrame = ("STOMP\n" + (((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        Assert.assertTrue(f.startsWith("CONNECTED"));
        for (int i = 0; i < MSG_COUNT; ++i) {
            String message = (((((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "receipt:0\n") + "\n") + "Hello World {") + i) + "}") + (NULL);
            stompConnection.sendFrame(message);
            StompFrame repsonse = stompConnection.receive();
            Assert.assertEquals("0", repsonse.getHeaders().get(RECEIPT_ID));
        }
        String subscribe = ((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "id:12345\n") + "browser:true\n\n") + (NULL);
        stompConnection.sendFrame(subscribe);
        for (int i = 0; i < MSG_COUNT; ++i) {
            StompFrame message = stompConnection.receive();
            Assert.assertEquals(MESSAGE, message.getAction());
            Assert.assertEquals("12345", message.getHeaders().get(SUBSCRIPTION));
        }
        // We should now get a browse done message
        StompFrame browseDone = stompConnection.receive();
        Stomp11Test.LOG.debug(("Browse Done: " + (browseDone.toString())));
        Assert.assertEquals(MESSAGE, browseDone.getAction());
        Assert.assertEquals("12345", browseDone.getHeaders().get(SUBSCRIPTION));
        Assert.assertEquals("end", browseDone.getHeaders().get(BROWSER));
        Assert.assertTrue(((browseDone.getHeaders().get(DESTINATION)) != null));
        String unsub = ((((("UNSUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "receipt:1\n") + "id:12345\n\n") + (NULL);
        stompConnection.sendFrame(unsub);
        String receipt = stompConnection.receiveFrame();
        Assert.assertTrue(receipt.contains("RECEIPT"));
        subscribe = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "id:12345\n\n") + (NULL);
        stompConnection.sendFrame(subscribe);
        for (int i = 0; i < MSG_COUNT; ++i) {
            StompFrame message = stompConnection.receive();
            Assert.assertEquals(MESSAGE, message.getAction());
            Assert.assertEquals("12345", message.getHeaders().get(SUBSCRIPTION));
        }
        stompConnection.sendFrame(unsub);
    }

    @Test(timeout = 60000)
    public void testSendMessageWithStandardHeadersEncoded() throws Exception {
        MessageConsumer consumer = session.createConsumer(queue);
        String frame = ("CONNECT\n" + ((("login:system\n" + "passcode:manager\n") + "accept-version:1.1") + "\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SEND\n" + (((((("correlation-id:c1\\:\\n\\23\n" + "priority:3\n") + "type:t34:5\n") + "JMSXGroupID:abc\n") + "foo:a\\bc\n") + "bar:123\n") + "destination:/queue/")) + (getQueueName())) + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(frame);
        TextMessage message = ((TextMessage) (consumer.receive(2500)));
        Assert.assertNotNull(message);
        Assert.assertEquals("Hello World", message.getText());
        Assert.assertEquals("JMSCorrelationID", "c1\\:\n\\23", message.getJMSCorrelationID());
        Assert.assertEquals("getJMSType", "t34:5", message.getJMSType());
        Assert.assertEquals("getJMSPriority", 3, message.getJMSPriority());
        Assert.assertEquals("foo", "a\\bc", message.getStringProperty("foo"));
        Assert.assertEquals("bar", "123", message.getStringProperty("bar"));
        Assert.assertEquals("JMSXGroupID", "abc", message.getStringProperty("JMSXGroupID"));
        ActiveMQTextMessage amqMessage = ((ActiveMQTextMessage) (message));
        Assert.assertEquals("GroupID", "abc", amqMessage.getGroupID());
    }

    @Test(timeout = 60000)
    public void testSendMessageWithRepeatedEntries() throws Exception {
        MessageConsumer consumer = session.createConsumer(queue);
        String frame = ("CONNECT\n" + ((("login:system\n" + "passcode:manager\n") + "accept-version:1.1") + "\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SEND\n" + (((((("value:newest" + "\n") + "value:older") + "\n") + "value:oldest") + "\n") + "destination:/queue/")) + (getQueueName())) + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(frame);
        TextMessage message = ((TextMessage) (consumer.receive(2500)));
        Assert.assertNotNull(message);
        Assert.assertEquals("Hello World", message.getText());
        Assert.assertEquals("newest", message.getStringProperty("value"));
    }

    @Test(timeout = 60000)
    public void testSubscribeWithMessageSentWithEncodedProperties() throws Exception {
        String frame = ("CONNECT\n" + ((("login:system\n" + "passcode:manager\n") + "accept-version:1.1") + "\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = ((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "id:12345\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        MessageProducer producer = session.createProducer(queue);
        TextMessage message = session.createTextMessage("Hello World");
        message.setStringProperty("s", "\\value:");
        producer.send(message);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(("" + frame), frame.startsWith("MESSAGE"));
        int start = (frame.indexOf("\ns:")) + 3;
        final String expectedEncoded = "\\\\value\\c";
        final String headerVal = frame.substring(start, (start + (expectedEncoded.length())));
        Assert.assertEquals(("" + frame), expectedEncoded, headerVal);
    }

    @Test(timeout = 60000)
    public void testNackMessage() throws Exception {
        String connectFrame = ("STOMP\n" + (((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        Assert.assertTrue(f.startsWith("CONNECTED"));
        String message = (((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\npersistent:true\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(message);
        String frame = ((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "id:12345\n") + "ack:client\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame received = stompConnection.receive();
        Assert.assertTrue(received.getAction().equals("MESSAGE"));
        // nack it
        frame = ((("NACK\n" + ("subscription:12345\n" + "message-id:")) + (received.getHeaders().get("message-id"))) + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = (((("UNSUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "id:12345\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        // consume it from dlq
        frame = ("SUBSCRIBE\n" + (("destination:/queue/ActiveMQ.DLQ\n" + "id:12345\n") + "ack:client\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame receivedDLQ = stompConnection.receive(200);
        Assert.assertEquals(receivedDLQ.getHeaders().get("message-id"), received.getHeaders().get("message-id"));
        frame = ((("ACK\n" + ("subscription:12345\n" + "message-id:")) + (received.getHeaders().get("message-id"))) + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = ("UNSUBSCRIBE\n" + ("destination:/queue/ActiveMQ.DLQ\n" + "id:12345\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
    }

    @Test(timeout = 60000)
    public void testHeaderValuesAreNotWSTrimmed() throws Exception {
        stompConnection.setVersion(V1_1);
        String connectFrame = ("STOMP\n" + (((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + f));
        Assert.assertTrue(f.startsWith("CONNECTED"));
        String message = ((((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\ntest1: value") + "\ntest2:value ") + "\ntest3: value ") + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(message);
        String frame = ((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "id:12345\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame received = stompConnection.receive();
        Assert.assertTrue(received.getAction().equals("MESSAGE"));
        Assert.assertEquals(" value", received.getHeaders().get("test1"));
        Assert.assertEquals("value ", received.getHeaders().get("test2"));
        Assert.assertEquals(" value ", received.getHeaders().get("test3"));
        frame = (((("UNSUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "id:12345\n\n") + (NULL);
        stompConnection.sendFrame(frame);
    }

    @Test(timeout = 60000)
    public void testDurableSubAndUnSubOnTwoTopics() throws Exception {
        stompConnection.setVersion(V1_1);
        String domain = "org.apache.activemq";
        ObjectName brokerName = new ObjectName((domain + ":type=Broker,brokerName=localhost"));
        BrokerViewMBean view = ((BrokerViewMBean) (brokerService.getManagementContext().newProxyInstance(brokerName, BrokerViewMBean.class, true)));
        String connectFrame = ("STOMP\n" + ((((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "host:localhost\n") + "client-id:test\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String frame = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + frame));
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 0);
        // subscribe to first destination durably
        frame = (((((((("SUBSCRIBE\n" + "destination:/topic/") + (getQueueName())) + "1") + "\n") + "ack:auto\n") + "receipt:1\n") + "id:durablesub-1\n") + "activemq.subscriptionName:test1\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame receipt = stompConnection.receive();
        Stomp11Test.LOG.debug(("Broker sent: " + receipt));
        Assert.assertTrue(receipt.getAction().startsWith("RECEIPT"));
        Assert.assertEquals("1", receipt.getHeaders().get("receipt-id"));
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 1);
        // subscribe to second destination durably
        frame = (((((((("SUBSCRIBE\n" + "destination:/topic/") + (getQueueName())) + "2") + "\n") + "ack:auto\n") + "receipt:2\n") + "id:durablesub-2\n") + "activemq.subscriptionName:test2\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        receipt = stompConnection.receive();
        Stomp11Test.LOG.debug(("Broker sent: " + receipt));
        Assert.assertTrue(receipt.getAction().startsWith("RECEIPT"));
        Assert.assertEquals("2", receipt.getHeaders().get("receipt-id"));
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 2);
        frame = "DISCONNECT\nclient-id:test\n\n" + (NULL);
        stompConnection.sendFrame(frame);
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 0;
            }
        }, TimeUnit.SECONDS.toMillis(5), TimeUnit.MILLISECONDS.toMillis(25));
        // reconnect and send some messages to the offline subscribers and then try to get
        // them after subscribing again.
        stompConnect();
        stompConnection.sendFrame(connectFrame);
        frame = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + frame));
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 0);
        Assert.assertEquals(view.getInactiveDurableTopicSubscribers().length, 2);
        // unsubscribe from topic 1
        frame = (((((("UNSUBSCRIBE\n" + "destination:/topic/") + (getQueueName())) + "1\n") + "id:durablesub-1\n") + "receipt:3\n") + "activemq.subscriptionName:test1\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        receipt = stompConnection.receive();
        Stomp11Test.LOG.debug(("Broker sent: " + frame));
        Assert.assertTrue(receipt.getAction().startsWith("RECEIPT"));
        Assert.assertEquals("3", receipt.getHeaders().get("receipt-id"));
        Assert.assertEquals(view.getInactiveDurableTopicSubscribers().length, 1);
        // unsubscribe from topic 2
        frame = (((((("UNSUBSCRIBE\n" + "destination:/topic/") + (getQueueName())) + "2\n") + "id:durablesub-2\n") + "receipt:4\n") + "activemq.subscriptionName:test2\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        receipt = stompConnection.receive();
        Stomp11Test.LOG.debug(("Broker sent: " + frame));
        Assert.assertTrue(receipt.getAction().startsWith("RECEIPT"));
        Assert.assertEquals("4", receipt.getHeaders().get("receipt-id"));
        Assert.assertEquals(view.getInactiveDurableTopicSubscribers().length, 0);
    }

    @Test(timeout = 60000)
    public void testDurableSubAndUnSubFlow() throws Exception {
        stompConnection.setVersion(V1_1);
        String domain = "org.apache.activemq";
        ObjectName brokerName = new ObjectName((domain + ":type=Broker,brokerName=localhost"));
        BrokerViewMBean view = ((BrokerViewMBean) (brokerService.getManagementContext().newProxyInstance(brokerName, BrokerViewMBean.class, true)));
        String connectFrame = ("STOMP\n" + ((((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "host:localhost\n") + "client-id:test\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String frame = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + frame));
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 0);
        // subscribe to first destination durably
        frame = (((((((("SUBSCRIBE\n" + "destination:/topic/") + (getQueueName())) + "1") + "\n") + "ack:auto\n") + "receipt:1\n") + "id:durablesub-1\n") + "activemq.subscriptionName:test1\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame receipt = stompConnection.receive();
        Stomp11Test.LOG.debug(("Broker sent: " + receipt));
        Assert.assertTrue(receipt.getAction().startsWith("RECEIPT"));
        Assert.assertEquals("1", receipt.getHeaders().get("receipt-id"));
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 1);
        // attempt to remove the durable subscription while there is an active subscription
        frame = (((((("UNSUBSCRIBE\n" + "destination:/topic/") + (getQueueName())) + "1\n") + "id:durablesub-1\n") + "receipt:3\n") + "activemq.subscriptionName:test1\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        receipt = stompConnection.receive();
        Stomp11Test.LOG.debug(("Broker sent: " + receipt));
        Assert.assertTrue(receipt.getAction().startsWith("ERROR"));
        Assert.assertEquals("3", receipt.getHeaders().get("receipt-id"));
        Assert.assertEquals(view.getInactiveDurableTopicSubscribers().length, 0);
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 1);
        // attempt to remove the subscriber leaving the durable sub in place.
        frame = ((((("UNSUBSCRIBE\n" + "destination:/topic/") + (getQueueName())) + "1\n") + "id:durablesub-1\n") + "receipt:4\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        receipt = stompConnection.receive();
        Stomp11Test.LOG.debug(("Broker sent: " + receipt));
        Assert.assertTrue(receipt.getAction().startsWith("RECEIPT"));
        Assert.assertEquals("4", receipt.getHeaders().get("receipt-id"));
        Assert.assertEquals(view.getInactiveDurableTopicSubscribers().length, 1);
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 0);
        // attempt to remove the durable subscription which should succeed since there are no
        // active durable subscribers
        frame = (((((("UNSUBSCRIBE\n" + "destination:/topic/") + (getQueueName())) + "1\n") + "id:durablesub-1\n") + "receipt:5\n") + "activemq.subscriptionName:test1\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        receipt = stompConnection.receive();
        Stomp11Test.LOG.debug(("Broker sent: " + receipt));
        Assert.assertTrue(receipt.getAction().startsWith("RECEIPT"));
        Assert.assertEquals("5", receipt.getHeaders().get("receipt-id"));
        Assert.assertEquals(view.getInactiveDurableTopicSubscribers().length, 0);
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 0);
    }

    @Test(timeout = 60000)
    public void testMultipleDurableSubsWithOfflineMessages() throws Exception {
        stompConnection.setVersion(V1_1);
        final BrokerViewMBean view = getProxyToBroker();
        String connectFrame = ("STOMP\n" + ((((("login:system\n" + "passcode:manager\n") + "accept-version:1.1\n") + "host:localhost\n") + "client-id:test\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String frame = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + frame));
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 0);
        // subscribe to first destination durably
        frame = (((((((("SUBSCRIBE\n" + "destination:/topic/") + (getQueueName())) + "1") + "\n") + "ack:auto\n") + "receipt:1\n") + "id:durablesub-1\n") + "activemq.subscriptionName:test1\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame receipt = stompConnection.receive();
        Stomp11Test.LOG.debug(("Broker sent: " + receipt));
        Assert.assertTrue(receipt.getAction().startsWith("RECEIPT"));
        Assert.assertEquals("1", receipt.getHeaders().get("receipt-id"));
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 1);
        // subscribe to second destination durably
        frame = (((((((("SUBSCRIBE\n" + "destination:/topic/") + (getQueueName())) + "2") + "\n") + "ack:auto\n") + "receipt:2\n") + "id:durablesub-2\n") + "activemq.subscriptionName:test2\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        receipt = stompConnection.receive();
        Stomp11Test.LOG.debug(("Broker sent: " + receipt));
        Assert.assertTrue(receipt.getAction().startsWith("RECEIPT"));
        Assert.assertEquals("2", receipt.getHeaders().get("receipt-id"));
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 2);
        frame = "DISCONNECT\nclient-id:test\n\n" + (NULL);
        stompConnection.sendFrame(frame);
        Assert.assertTrue(Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (view.getCurrentConnectionsCount()) == 1;
            }
        }, TimeUnit.SECONDS.toMillis(5), TimeUnit.MILLISECONDS.toMillis(25)));
        // reconnect and send some messages to the offline subscribers and then try to get
        // them after subscribing again.
        stompConnect();
        stompConnection.sendFrame(connectFrame);
        frame = stompConnection.receiveFrame();
        Stomp11Test.LOG.debug(("Broker sent: " + frame));
        Assert.assertTrue(frame.contains("CONNECTED"));
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 0);
        Assert.assertEquals(view.getInactiveDurableTopicSubscribers().length, 2);
        frame = (((((("SEND\n" + "destination:/topic/") + (getQueueName())) + "1\n") + "receipt:10\n") + "\n") + "Hello World 1") + (NULL);
        stompConnection.sendFrame(frame);
        receipt = stompConnection.receive();
        Assert.assertEquals("10", receipt.getHeaders().get(RECEIPT_ID));
        frame = (((((("SEND\n" + "destination:/topic/") + (getQueueName())) + "2\n") + "receipt:11\n") + "\n") + "Hello World 2") + (NULL);
        stompConnection.sendFrame(frame);
        receipt = stompConnection.receive();
        Assert.assertEquals("11", receipt.getHeaders().get(RECEIPT_ID));
        // subscribe to first destination durably
        frame = (((((((("SUBSCRIBE\n" + "destination:/topic/") + (getQueueName())) + "1") + "\n") + "ack:auto\n") + "receipt:3\n") + "id:durablesub-1\n") + "activemq.subscriptionName:test1\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        receipt = stompConnection.receive();
        Stomp11Test.LOG.debug(("Broker sent: " + receipt));
        Assert.assertTrue(receipt.getAction().startsWith("RECEIPT"));
        Assert.assertEquals("3", receipt.getHeaders().get("receipt-id"));
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 1);
        StompFrame message = stompConnection.receive();
        Assert.assertEquals(MESSAGE, message.getAction());
        Assert.assertEquals("durablesub-1", message.getHeaders().get(SUBSCRIPTION));
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 1);
        Assert.assertEquals(view.getInactiveDurableTopicSubscribers().length, 1);
        // subscribe to second destination durably
        frame = (((((((("SUBSCRIBE\n" + "destination:/topic/") + (getQueueName())) + "2") + "\n") + "ack:auto\n") + "receipt:4\n") + "id:durablesub-2\n") + "activemq.subscriptionName:test2\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        receipt = stompConnection.receive();
        Stomp11Test.LOG.debug(("Broker sent: " + receipt));
        Assert.assertTrue(receipt.getAction().startsWith("RECEIPT"));
        Assert.assertEquals("4", receipt.getHeaders().get("receipt-id"));
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 2);
        message = stompConnection.receive();
        Assert.assertEquals(MESSAGE, message.getAction());
        Assert.assertEquals("durablesub-2", message.getHeaders().get(SUBSCRIPTION));
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 2);
        Assert.assertEquals(view.getInactiveDurableTopicSubscribers().length, 0);
    }

    @Test(timeout = 60000)
    public void testTransactionRollbackAllowsSecondAckOutsideTXClientAck() throws Exception {
        doTestTransactionRollbackAllowsSecondAckOutsideTXClientAck("client");
    }

    @Test(timeout = 60000)
    public void testTransactionRollbackAllowsSecondAckOutsideTXClientIndividualAck() throws Exception {
        doTestTransactionRollbackAllowsSecondAckOutsideTXClientAck("client-individual");
    }

    @Test(timeout = 60000)
    public void testAckMessagesInTransactionOutOfOrderWithTXClientAck() throws Exception {
        doTestAckMessagesInTransactionOutOfOrderWithTXClientAck("client");
    }

    @Test(timeout = 60000)
    public void testAckMessagesInTransactionOutOfOrderWithTXClientIndividualAck() throws Exception {
        doTestAckMessagesInTransactionOutOfOrderWithTXClientAck("client-individual");
    }
}

