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


import Stomp.Headers.Message.EXPIRATION_TIME;
import Stomp.Headers.Message.MESSAGE_ID;
import Stomp.Headers.Message.ORIGINAL_DESTINATION;
import Stomp.Headers.Message.REDELIVERED;
import Stomp.Headers.Message.SUBSCRIPTION;
import Stomp.Headers.Message.TIMESTAMP;
import Stomp.Headers.Message.USERID;
import Stomp.Headers.RECEIPT_REQUESTED;
import Stomp.Headers.Response.RECEIPT_ID;
import Stomp.Headers.Send.PERSISTENT;
import Stomp.Headers.Send.REPLY_TO;
import Stomp.Headers.TRANSFORMATION_ERROR;
import Stomp.Transformations;
import Stomp.Transformations.JMS_OBJECT_XML;
import com.thoughtworks.xstream.XStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.management.ObjectName;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Stomp.NULL;


public class StompTest extends StompTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(StompTest.class);

    protected Connection connection;

    protected Session session;

    protected ActiveMQQueue queue;

    protected XStream xstream;

    private final String xmlObject = "<pojo>\n" + (("  <name>Dejan</name>\n" + "  <city>Belgrade</city>\n") + "</pojo>");

    private String xmlMap = "<map>\n" + (((((((("  <entry>\n" + "    <string>name</string>\n") + "    <string>Dejan</string>\n") + "  </entry>\n") + "  <entry>\n") + "    <string>city</string>\n") + "    <string>Belgrade</string>\n") + "  </entry>\n") + "</map>\n");

    private final String jsonObject = "{\"pojo\":{" + (("\"name\":\"Dejan\"," + "\"city\":\"Belgrade\"") + "}}");

    private String jsonMap = "{\"map\":{" + (((("\"entry\":[" + "{\"string\":[\"name\",\"Dejan\"]},") + "{\"string\":[\"city\",\"Belgrade\"]}") + "]") + "}}");

    @Test(timeout = 60000)
    public void testConnect() throws Exception {
        String connectFrame = ("CONNECT\n" + ((("login:system\n" + "passcode:manager\n") + "request-id:1\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Assert.assertTrue(f.startsWith("CONNECTED"));
        Assert.assertTrue(((f.indexOf("response-id:1")) >= 0));
    }

    @Test(timeout = 60000)
    public void testSendMessage() throws Exception {
        MessageConsumer consumer = session.createConsumer(queue);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(frame);
        TextMessage message = ((TextMessage) (consumer.receive(2500)));
        Assert.assertNotNull(message);
        Assert.assertEquals("Hello World", message.getText());
        // Make sure that the timestamp is valid - should
        // be very close to the current time.
        long tnow = System.currentTimeMillis();
        long tmsg = message.getJMSTimestamp();
        Assert.assertTrue(((Math.abs((tnow - tmsg))) < 1000));
    }

    @Test(timeout = 60000)
    public void testJMSXGroupIdCanBeSet() throws Exception {
        MessageConsumer consumer = session.createConsumer(queue);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = ((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "JMSXGroupID:TEST\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(frame);
        TextMessage message = ((TextMessage) (consumer.receive(2500)));
        Assert.assertNotNull(message);
        Assert.assertEquals("TEST", getGroupID());
    }

    @Test(timeout = 60000)
    public void testSendMessageWithCustomHeadersAndSelector() throws Exception {
        MessageConsumer consumer = session.createConsumer(queue, "foo = 'abc'");
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SEND\n" + (("foo:abc\n" + "bar:123\n") + "destination:/queue/")) + (getQueueName())) + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(frame);
        TextMessage message = ((TextMessage) (consumer.receive(2500)));
        Assert.assertNotNull(message);
        Assert.assertEquals("Hello World", message.getText());
        Assert.assertEquals("foo", "abc", message.getStringProperty("foo"));
        Assert.assertEquals("bar", "123", message.getStringProperty("bar"));
    }

    @Test(timeout = 60000)
    public void testSendMessageWithDelay() throws Exception {
        MessageConsumer consumer = session.createConsumer(queue);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SEND\n" + ("AMQ_SCHEDULED_DELAY:2000\n" + "destination:/queue/")) + (getQueueName())) + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(frame);
        TextMessage message = ((TextMessage) (consumer.receive(1000)));
        Assert.assertNull(message);
        message = ((TextMessage) (consumer.receive(2500)));
        Assert.assertNotNull(message);
    }

    @Test(timeout = 60000)
    public void testSendMessageWithStandardHeaders() throws Exception {
        MessageConsumer consumer = session.createConsumer(queue);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SEND\n" + (((((("correlation-id:c123\n" + "priority:3\n") + "type:t345\n") + "JMSXGroupID:abc\n") + "foo:abc\n") + "bar:123\n") + "destination:/queue/")) + (getQueueName())) + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(frame);
        TextMessage message = ((TextMessage) (consumer.receive(2500)));
        Assert.assertNotNull(message);
        Assert.assertEquals("Hello World", message.getText());
        Assert.assertEquals("JMSCorrelationID", "c123", message.getJMSCorrelationID());
        Assert.assertEquals("getJMSType", "t345", message.getJMSType());
        Assert.assertEquals("getJMSPriority", 3, message.getJMSPriority());
        Assert.assertEquals("foo", "abc", message.getStringProperty("foo"));
        Assert.assertEquals("bar", "123", message.getStringProperty("bar"));
        Assert.assertEquals("JMSXGroupID", "abc", message.getStringProperty("JMSXGroupID"));
        ActiveMQTextMessage amqMessage = ((ActiveMQTextMessage) (message));
        Assert.assertEquals("GroupID", "abc", amqMessage.getGroupID());
    }

    @Test(timeout = 60000)
    public void testSendMessageWithNoPriorityReceivesDefault() throws Exception {
        MessageConsumer consumer = session.createConsumer(queue);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SEND\n" + ("correlation-id:c123\n" + "destination:/queue/")) + (getQueueName())) + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(frame);
        TextMessage message = ((TextMessage) (consumer.receive(2500)));
        Assert.assertNotNull(message);
        Assert.assertEquals("Hello World", message.getText());
        Assert.assertEquals("getJMSPriority", 4, message.getJMSPriority());
    }

    @Test(timeout = 60000)
    public void testSendFrameWithInvalidAction() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        final int connectionCount = getProxyToBroker().getCurrentConnectionsCount();
        frame = (((("SED\n" + ("AMQ_SCHEDULED_DELAY:2000\n" + "destination:/queue/")) + (getQueueName())) + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("ERROR"));
        Assert.assertTrue("Should drop connection", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return connectionCount > (getProxyToBroker().getCurrentConnectionsCount());
            }
        }));
    }

    @Test(timeout = 60000)
    public void testReceipts() throws Exception {
        StompConnection receiver = new StompConnection();
        receiver.open(createSocket());
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        receiver.sendFrame(frame);
        frame = receiver.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:auto\n\n") + (NULL);
        receiver.sendFrame(frame);
        frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "receipt: msg-1\n") + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(frame);
        frame = receiver.receiveFrame();
        Assert.assertTrue(frame.startsWith("MESSAGE"));
        Assert.assertTrue("Stomp Message does not contain receipt request", ((frame.indexOf(RECEIPT_REQUESTED)) == (-1)));
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("RECEIPT"));
        Assert.assertTrue("Receipt contains correct receipt-id", ((frame.indexOf(RECEIPT_ID)) >= 0));
        frame = ("DISCONNECT\n" + ("receipt: dis-1\n" + "\n\n")) + (NULL);
        receiver.sendFrame(frame);
        frame = receiver.receiveFrame();
        Assert.assertTrue(frame.startsWith("RECEIPT"));
        Assert.assertTrue("Receipt contains correct receipt-id", ((frame.indexOf(RECEIPT_ID)) >= 0));
        receiver.close();
        MessageConsumer consumer = session.createConsumer(queue);
        frame = (((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "receipt: msg-1\n") + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("RECEIPT"));
        Assert.assertTrue("Receipt contains correct receipt-id", ((frame.indexOf(RECEIPT_ID)) >= 0));
        TextMessage message = ((TextMessage) (consumer.receive(10000)));
        Assert.assertNotNull(message);
        Assert.assertNull("JMS Message does not contain receipt request", message.getStringProperty(RECEIPT_REQUESTED));
    }

    @Test(timeout = 60000)
    public void testSubscriptionReceipts() throws Exception {
        final int done = 20;
        int count = 0;
        int receiptId = 0;
        do {
            StompConnection sender = new StompConnection();
            sender.open(createSocket());
            String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
            sender.sendFrame(frame);
            frame = sender.receiveFrame();
            Assert.assertTrue(frame.startsWith("CONNECTED"));
            frame = ((((((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "receipt: ") + (receiptId++)) + "\n") + "Hello World:") + (count++)) + "\n\n") + (NULL);
            sender.sendFrame(frame);
            frame = sender.receiveFrame();
            Assert.assertTrue(("" + frame), frame.startsWith("RECEIPT"));
            sender.disconnect();
            StompConnection receiver = new StompConnection();
            receiver.open(createSocket());
            frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
            receiver.sendFrame(frame);
            frame = receiver.receiveFrame();
            Assert.assertTrue(frame.startsWith("CONNECTED"));
            frame = (((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "receipt: ") + (receiptId++)) + "\n\n") + (NULL);
            receiver.sendFrame(frame);
            frame = receiver.receiveFrame();
            Assert.assertTrue(("" + frame), frame.startsWith("RECEIPT"));
            Assert.assertTrue("Receipt contains receipt-id", ((frame.indexOf(RECEIPT_ID)) >= 0));
            frame = receiver.receiveFrame();
            Assert.assertTrue(("" + frame), frame.startsWith("MESSAGE"));
            // remove suscription  so we don't hang about and get next message
            frame = (((((("UNSUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "receipt: ") + (receiptId++)) + "\n\n") + (NULL);
            receiver.sendFrame(frame);
            frame = receiver.receiveFrame();
            Assert.assertTrue(("" + frame), frame.startsWith("RECEIPT"));
            receiver.disconnect();
        } while (count < done );
    }

    @Test(timeout = 60000)
    public void testSubscribeWithAutoAck() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        sendMessage(name.getMethodName());
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("MESSAGE"));
    }

    @Test(timeout = 60000)
    public void testSubscribeWithAutoAckAndBytesMessage() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        sendBytesMessage(new byte[]{ 1, 2, 3, 4, 5 });
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("MESSAGE"));
        Pattern cl = Pattern.compile("Content-length:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher clMmatcher = cl.matcher(frame);
        Assert.assertTrue(clMmatcher.find());
        Assert.assertEquals("5", clMmatcher.group(1));
        Assert.assertFalse(Pattern.compile("type:\\s*null", Pattern.CASE_INSENSITIVE).matcher(frame).find());
    }

    @Test(timeout = 60000)
    public void testBytesMessageWithNulls() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = ((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\ncontent-length:5") + " \n\n") + "\u0001\u0002\u0000\u0004\u0005") + (NULL);
        stompConnection.sendFrame(frame);
        frame = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame message = stompConnection.receive();
        Assert.assertTrue(message.getAction().startsWith("MESSAGE"));
        String length = message.getHeaders().get("content-length");
        Assert.assertEquals("5", length);
        Assert.assertEquals(5, message.getContent().length);
    }

    @Test(timeout = 60000)
    public void testSendMultipleBytesMessages() throws Exception {
        final int MSG_COUNT = 50;
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        for (int ix = 0; ix < MSG_COUNT; ix++) {
            frame = ((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\ncontent-length:5") + " \n\n") + "\u0001\u0002\u0000\u0004\u0005") + (NULL);
            stompConnection.sendFrame(frame);
        }
        frame = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        for (int ix = 0; ix < MSG_COUNT; ix++) {
            StompFrame message = stompConnection.receive();
            Assert.assertTrue(message.getAction().startsWith("MESSAGE"));
            String length = message.getHeaders().get("content-length");
            Assert.assertEquals("5", length);
            Assert.assertEquals(5, message.getContent().length);
        }
    }

    @Test(timeout = 60000)
    public void testSubscribeWithMessageSentWithProperties() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        MessageProducer producer = session.createProducer(queue);
        TextMessage message = session.createTextMessage("Hello World");
        message.setStringProperty("s", "value");
        message.setBooleanProperty("n", false);
        message.setByteProperty("byte", ((byte) (9)));
        message.setDoubleProperty("d", 2.0);
        message.setFloatProperty("f", ((float) (6.0)));
        message.setIntProperty("i", 10);
        message.setLongProperty("l", 121);
        message.setShortProperty("s", ((short) (12)));
        producer.send(message);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("MESSAGE"));
    }

    @Test(timeout = 60000)
    public void testMessagesAreInOrder() throws Exception {
        int ctr = 10;
        String[] data = new String[ctr];
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        for (int i = 0; i < ctr; ++i) {
            data[i] = (getName()) + i;
            sendMessage(data[i]);
        }
        for (int i = 0; i < ctr; ++i) {
            frame = stompConnection.receiveFrame();
            Assert.assertTrue("Message not in order", ((frame.indexOf(data[i])) >= 0));
        }
        // sleep a while before publishing another set of messages
        TimeUnit.MILLISECONDS.sleep(500);
        for (int i = 0; i < ctr; ++i) {
            data[i] = ((getName()) + ":second:") + i;
            sendMessage(data[i]);
        }
        for (int i = 0; i < ctr; ++i) {
            frame = stompConnection.receiveFrame();
            Assert.assertTrue("Message not in order", ((frame.indexOf(data[i])) >= 0));
        }
    }

    @Test(timeout = 60000)
    public void testSubscribeWithAutoAckAndSelector() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = ((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "selector: foo = \'zzz\'\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        sendMessage("Ignored message", "foo", "1234");
        sendMessage("Real message", "foo", "zzz");
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("MESSAGE"));
        Assert.assertTrue(("Should have received the real message but got: " + frame), ((frame.indexOf("Real message")) > 0));
    }

    @Test(timeout = 60000)
    public void testSubscribeWithAutoAckAndNumericSelector() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = ((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "selector: foo = 42\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        // Ignored
        frame = (((("SEND\n" + ("foo:abc\n" + "destination:/queue/")) + (getQueueName())) + "\n\n") + "Ignored Message") + (NULL);
        stompConnection.sendFrame(frame);
        // Matches
        frame = (((("SEND\n" + ("foo:42\n" + "destination:/queue/")) + (getQueueName())) + "\n\n") + "Real Message") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("MESSAGE"));
        Assert.assertTrue(("Should have received the real message but got: " + frame), ((frame.indexOf("Real Message")) > 0));
    }

    @Test(timeout = 60000)
    public void testSubscribeWithAutoAckAndBooleanSelector() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = ((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "selector: foo = true\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        // Ignored
        frame = (((("SEND\n" + ("foo:false\n" + "destination:/queue/")) + (getQueueName())) + "\n\n") + "Ignored Message") + (NULL);
        stompConnection.sendFrame(frame);
        // Matches
        frame = (((("SEND\n" + ("foo:true\n" + "destination:/queue/")) + (getQueueName())) + "\n\n") + "Real Message") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("MESSAGE"));
        Assert.assertTrue(("Should have received the real message but got: " + frame), ((frame.indexOf("Real Message")) > 0));
    }

    @Test(timeout = 60000)
    public void testSubscribeWithAutoAckAnFloatSelector() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = ((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "selector: foo = 3.14159\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        // Ignored
        frame = (((("SEND\n" + ("foo:6.578\n" + "destination:/queue/")) + (getQueueName())) + "\n\n") + "Ignored Message") + (NULL);
        stompConnection.sendFrame(frame);
        // Matches
        frame = (((("SEND\n" + ("foo:3.14159\n" + "destination:/queue/")) + (getQueueName())) + "\n\n") + "Real Message") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("MESSAGE"));
        Assert.assertTrue(("Should have received the real message but got: " + frame), ((frame.indexOf("Real Message")) > 0));
    }

    @Test(timeout = 60000)
    public void testSubscribeWithClientAck() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:client\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        sendMessage(getName());
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("MESSAGE"));
        stompDisconnect();
        // message should be received since message was not acknowledged
        MessageConsumer consumer = session.createConsumer(queue);
        TextMessage message = ((TextMessage) (consumer.receive(2500)));
        Assert.assertNotNull(message);
        Assert.assertTrue(message.getJMSRedelivered());
    }

    @Test(timeout = 60000)
    public void testSubscribeWithClientAckedAndContentLength() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:client\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        sendMessage(getName());
        StompFrame msg = stompConnection.receive();
        Assert.assertTrue(msg.getAction().equals("MESSAGE"));
        HashMap<String, String> ackHeaders = new HashMap<String, String>();
        ackHeaders.put("message-id", msg.getHeaders().get("message-id"));
        ackHeaders.put("content-length", "8511");
        StompFrame ack = new StompFrame("ACK", ackHeaders);
        stompConnection.sendFrame(ack.format());
        final QueueViewMBean queueView = getProxyToQueue(getQueueName());
        Assert.assertTrue("dequeue complete", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                StompTest.LOG.info(((((("queueView, enqueue:" + (queueView.getEnqueueCount())) + ", dequeue:") + (queueView.getDequeueCount())) + ", inflight:") + (queueView.getInFlightCount())));
                return (queueView.getDequeueCount()) == 1;
            }
        }, TimeUnit.SECONDS.toMillis(5), TimeUnit.MILLISECONDS.toMillis(25)));
        stompDisconnect();
        // message should not be received since it was acknowledged
        MessageConsumer consumer = session.createConsumer(queue);
        TextMessage message = ((TextMessage) (consumer.receive(500)));
        Assert.assertNull(message);
    }

    @Test(timeout = 60000)
    public void testUnsubscribe() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        // send a message to our queue
        sendMessage("first message");
        // receive message from socket
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("MESSAGE"));
        // remove suscription
        frame = ((((("UNSUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "receipt:1") + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(("" + frame), frame.startsWith("RECEIPT"));
        // send a message to our queue
        sendMessage("second message");
        try {
            frame = stompConnection.receiveFrame(500);
            StompTest.LOG.info(("Received frame: " + frame));
            Assert.fail("No message should have been received since subscription was removed");
        } catch (SocketTimeoutException e) {
        }
    }

    @Test(timeout = 60000)
    public void testTransactionCommit() throws Exception {
        MessageConsumer consumer = session.createConsumer(queue);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        String f = stompConnection.receiveFrame();
        Assert.assertTrue(f.startsWith("CONNECTED"));
        frame = ("BEGIN\n" + ("transaction: tx1\n" + "\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = (((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "transaction: tx1\n") + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(frame);
        frame = ("COMMIT\n" + ("transaction: tx1\n" + "\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        TextMessage message = ((TextMessage) (consumer.receive(10000)));
        Assert.assertNotNull("Should have received a message", message);
    }

    @Test(timeout = 60000)
    public void testTransactionRollback() throws Exception {
        MessageConsumer consumer = session.createConsumer(queue);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        String f = stompConnection.receiveFrame();
        Assert.assertTrue(f.startsWith("CONNECTED"));
        frame = ("BEGIN\n" + ("transaction: tx1\n" + "\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = (((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "transaction: tx1\n") + "\n") + "first message") + (NULL);
        stompConnection.sendFrame(frame);
        // rollback first message
        frame = ("ABORT\n" + ("transaction: tx1\n" + "\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = ("BEGIN\n" + ("transaction: tx1\n" + "\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = (((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "transaction: tx1\n") + "\n") + "second message") + (NULL);
        stompConnection.sendFrame(frame);
        frame = ("COMMIT\n" + ("transaction: tx1\n" + "\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        // only second msg should be received since first msg was rolled back
        TextMessage message = ((TextMessage) (consumer.receive(10000)));
        Assert.assertNotNull(message);
        Assert.assertEquals("second message", message.getText().trim());
    }

    @Test(timeout = 60000)
    public void testDisconnectedClientsAreRemovedFromTheBroker() throws Exception {
        assertClients(1);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        assertClients(2);
        // now lets kill the stomp connection
        stompConnection.close();
        assertClients(1);
    }

    @Test(timeout = 60000)
    public void testConnectNotAuthenticatedWrongUser() throws Exception {
        String frame = ("CONNECT\n" + ("login: dejanb\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        try {
            String f = stompConnection.receiveFrame();
            Assert.assertTrue(f.startsWith("ERROR"));
        } catch (IOException socketMayBeClosedFirstByBroker) {
        }
    }

    @Test(timeout = 60000)
    public void testConnectNotAuthenticatedWrongPassword() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode: dejanb\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        try {
            String f = stompConnection.receiveFrame();
            Assert.assertTrue(f.startsWith("ERROR"));
        } catch (IOException socketMayBeClosedFirstByBroker) {
        }
    }

    @Test(timeout = 60000)
    public void testSendNotAuthorized() throws Exception {
        String frame = ("CONNECT\n" + ("login:guest\n" + "passcode:password\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SEND\n" + "destination:/queue/USERS.") + (getQueueName())) + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(frame);
        String f = stompConnection.receiveFrame();
        Assert.assertTrue(f.startsWith("ERROR"));
    }

    @Test(timeout = 60000)
    public void testSubscribeNotAuthorized() throws Exception {
        String frame = ("CONNECT\n" + ("login:guest\n" + "passcode:password\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SUBSCRIBE\n" + "destination:/queue/USERS.") + (getQueueName())) + "\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("ERROR"));
    }

    @Test(timeout = 60000)
    public void testSubscribeWithReceiptNotAuthorized() throws Exception {
        String frame = ("CONNECT\n" + ("login:guest\n" + "passcode:password\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((((("SUBSCRIBE\n" + "destination:/queue/USERS.") + (getQueueName())) + "\n") + "ack:auto\n") + "receipt:1\n") + "\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("ERROR"));
        Assert.assertTrue("Error Frame did not contain receipt-id", ((frame.indexOf(RECEIPT_ID)) >= 0));
    }

    @Test(timeout = 60000)
    public void testSubscribeWithInvalidSelector() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = ((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "selector:foo.bar = 1\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("ERROR"));
    }

    @Test(timeout = 60000)
    public void testTransformationUnknownTranslator() throws Exception {
        MessageConsumer consumer = session.createConsumer(queue);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "transformation:test") + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(frame);
        TextMessage message = ((TextMessage) (consumer.receive(2500)));
        Assert.assertNotNull(message);
        Assert.assertEquals("Hello World", message.getText());
    }

    @Test(timeout = 60000)
    public void testTransformationFailed() throws Exception {
        MessageConsumer consumer = session.createConsumer(queue);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = ((((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "transformation:") + (Transformations.JMS_OBJECT_XML)) + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(frame);
        TextMessage message = ((TextMessage) (consumer.receive(2500)));
        Assert.assertNotNull(message);
        Assert.assertNotNull(message.getStringProperty(TRANSFORMATION_ERROR));
        Assert.assertEquals("Hello World", message.getText());
    }

    @Test(timeout = 60000)
    public void testTransformationSendXMLObject() throws Exception {
        MessageConsumer consumer = session.createConsumer(queue);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = ((((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "transformation:") + (Transformations.JMS_OBJECT_XML)) + "\n\n") + (xmlObject)) + (NULL);
        stompConnection.sendFrame(frame);
        Message message = consumer.receive(2500);
        Assert.assertNotNull(message);
        StompTest.LOG.info("Broke sent: {}", message);
        Assert.assertTrue((message instanceof ObjectMessage));
        ObjectMessage objectMessage = ((ObjectMessage) (message));
        SamplePojo object = ((SamplePojo) (objectMessage.getObject()));
        Assert.assertEquals("Dejan", object.getName());
    }

    @Test(timeout = 60000)
    public void testTransformationSendJSONObject() throws Exception {
        MessageConsumer consumer = session.createConsumer(queue);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = ((((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "transformation:") + (Transformations.JMS_OBJECT_JSON)) + "\n\n") + (jsonObject)) + (NULL);
        stompConnection.sendFrame(frame);
        ObjectMessage message = ((ObjectMessage) (consumer.receive(2500)));
        Assert.assertNotNull(message);
        SamplePojo object = ((SamplePojo) (message.getObject()));
        Assert.assertEquals("Dejan", object.getName());
    }

    @Test(timeout = 60000)
    public void testTransformationSubscribeXML() throws Exception {
        MessageProducer producer = session.createProducer(new ActiveMQQueue(("USERS." + (getQueueName()))));
        ObjectMessage message = session.createObjectMessage(new SamplePojo("Dejan", "Belgrade"));
        producer.send(message);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((((((("SUBSCRIBE\n" + "destination:/queue/USERS.") + (getQueueName())) + "\n") + "ack:auto") + "\n") + "transformation:") + (Transformations.JMS_OBJECT_XML)) + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.trim().endsWith(xmlObject));
    }

    @Test(timeout = 60000)
    public void testTransformationReceiveJSONObject() throws Exception {
        MessageProducer producer = session.createProducer(new ActiveMQQueue(("USERS." + (getQueueName()))));
        ObjectMessage message = session.createObjectMessage(new SamplePojo("Dejan", "Belgrade"));
        producer.send(message);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((((((("SUBSCRIBE\n" + "destination:/queue/USERS.") + (getQueueName())) + "\n") + "ack:auto") + "\n") + "transformation:") + (Transformations.JMS_OBJECT_JSON)) + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.trim().endsWith(jsonObject));
    }

    @Test(timeout = 60000)
    public void testTransformationReceiveXMLObject() throws Exception {
        MessageProducer producer = session.createProducer(new ActiveMQQueue(("USERS." + (getQueueName()))));
        ObjectMessage message = session.createObjectMessage(new SamplePojo("Dejan", "Belgrade"));
        producer.send(message);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((((((("SUBSCRIBE\n" + "destination:/queue/USERS.") + (getQueueName())) + "\n") + "ack:auto") + "\n") + "transformation:") + (Transformations.JMS_OBJECT_XML)) + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.trim().endsWith(xmlObject));
    }

    @Test(timeout = 60000)
    public void testTransformationReceiveObject() throws Exception {
        MessageProducer producer = session.createProducer(new ActiveMQQueue(("USERS." + (getQueueName()))));
        ObjectMessage message = session.createObjectMessage(new SamplePojo("Dejan", "Belgrade"));
        producer.send(message);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((((((("SUBSCRIBE\n" + "destination:/queue/USERS.") + (getQueueName())) + "\n") + "ack:auto") + "\n") + "transformation:") + (Transformations.JMS_OBJECT_XML)) + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.trim().endsWith(xmlObject));
    }

    @Test(timeout = 60000)
    public void testTransformationReceiveXMLObjectAndMap() throws Exception {
        MessageProducer producer = session.createProducer(new ActiveMQQueue(("USERS." + (getQueueName()))));
        ObjectMessage objMessage = session.createObjectMessage(new SamplePojo("Dejan", "Belgrade"));
        producer.send(objMessage);
        MapMessage mapMessage = session.createMapMessage();
        mapMessage.setString("name", "Dejan");
        mapMessage.setString("city", "Belgrade");
        producer.send(mapMessage);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((((((("SUBSCRIBE\n" + "destination:/queue/USERS.") + (getQueueName())) + "\n") + "ack:auto") + "\n") + "transformation:") + (Transformations.JMS_XML)) + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.trim().endsWith(xmlObject));
        StompFrame xmlFrame = stompConnection.receive();
        Map<String, String> map = createMapFromXml(xmlFrame.getBody());
        Assert.assertTrue(map.containsKey("name"));
        Assert.assertTrue(map.containsKey("city"));
        Assert.assertTrue(map.get("name").equals("Dejan"));
        Assert.assertTrue(map.get("city").equals("Belgrade"));
    }

    @Test(timeout = 60000)
    public void testTransformationReceiveJSONObjectAndMap() throws Exception {
        MessageProducer producer = session.createProducer(new ActiveMQQueue(("USERS." + (getQueueName()))));
        ObjectMessage objMessage = session.createObjectMessage(new SamplePojo("Dejan", "Belgrade"));
        producer.send(objMessage);
        MapMessage mapMessage = session.createMapMessage();
        mapMessage.setString("name", "Dejan");
        mapMessage.setString("city", "Belgrade");
        producer.send(mapMessage);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((((((("SUBSCRIBE\n" + "destination:/queue/USERS.") + (getQueueName())) + "\n") + "ack:auto") + "\n") + "transformation:") + (Transformations.JMS_JSON)) + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame json = stompConnection.receive();
        StompTest.LOG.info("Transformed frame: {}", json);
        SamplePojo pojo = createObjectFromJson(json.getBody());
        Assert.assertTrue(pojo.getCity().equals("Belgrade"));
        Assert.assertTrue(pojo.getName().equals("Dejan"));
        json = stompConnection.receive();
        StompTest.LOG.info("Transformed frame: {}", json);
        Map<String, String> map = createMapFromJson(json.getBody());
        Assert.assertTrue(map.containsKey("name"));
        Assert.assertTrue(map.containsKey("city"));
        Assert.assertTrue(map.get("name").equals("Dejan"));
        Assert.assertTrue(map.get("city").equals("Belgrade"));
    }

    @Test(timeout = 60000)
    public void testTransformationSendAndReceiveXmlMap() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:auto") + "\n") + "transformation:") + (Transformations.JMS_XML)) + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = ((((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "transformation:") + (Transformations.JMS_MAP_JSON)) + "\n\n") + (jsonMap)) + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame xmlFrame = stompConnection.receive();
        StompTest.LOG.info("Received Frame: {}", xmlFrame.getBody());
        Map<String, String> map = createMapFromXml(xmlFrame.getBody());
        Assert.assertTrue(map.containsKey("name"));
        Assert.assertTrue(map.containsKey("city"));
        Assert.assertEquals("Dejan", map.get("name"));
        Assert.assertEquals("Belgrade", map.get("city"));
        Assert.assertTrue(xmlFrame.getHeaders().containsValue("jms-map-xml"));
    }

    @Test(timeout = 60000)
    public void testTransformationSendAndReceiveJsonMap() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:auto") + "\n") + "transformation:") + (Transformations.JMS_JSON)) + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = ((((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "transformation:") + (Transformations.JMS_MAP_XML)) + "\n\n") + (xmlMap)) + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame json = stompConnection.receive();
        StompTest.LOG.info("Received Frame: {}", json.getBody());
        Assert.assertNotNull(json);
        Assert.assertTrue(json.getHeaders().containsValue("jms-map-json"));
        Map<String, String> map = createMapFromJson(json.getBody());
        Assert.assertTrue(map.containsKey("name"));
        Assert.assertTrue(map.containsKey("city"));
        Assert.assertEquals("Dejan", map.get("name"));
        Assert.assertEquals("Belgrade", map.get("city"));
    }

    @Test(timeout = 60000)
    public void testTransformationReceiveBytesMessage() throws Exception {
        MessageProducer producer = session.createProducer(new ActiveMQQueue(("USERS." + (getQueueName()))));
        BytesMessage message = session.createBytesMessage();
        message.writeBytes(new byte[]{ 1, 2, 3, 4, 5 });
        producer.send(message);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((((((("SUBSCRIBE\n" + "destination:/queue/USERS.") + (getQueueName())) + "\n") + "ack:auto") + "\n") + "transformation:") + (Transformations.JMS_XML)) + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("MESSAGE"));
        Pattern cl = Pattern.compile("Content-length:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher clMmatcher = cl.matcher(frame);
        Assert.assertTrue(clMmatcher.find());
        Assert.assertEquals("5", clMmatcher.group(1));
        Assert.assertFalse(Pattern.compile("type:\\s*null", Pattern.CASE_INSENSITIVE).matcher(frame).find());
    }

    @Test(timeout = 60000)
    public void testTransformationNotOverrideSubscription() throws Exception {
        MessageProducer producer = session.createProducer(new ActiveMQQueue(("USERS." + (getQueueName()))));
        ObjectMessage message = session.createObjectMessage(new SamplePojo("Dejan", "Belgrade"));
        message.setStringProperty("transformation", JMS_OBJECT_XML.toString());
        producer.send(message);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((((((("SUBSCRIBE\n" + "destination:/queue/USERS.") + (getQueueName())) + "\n") + "ack:auto") + "\n") + "transformation:") + (Transformations.JMS_OBJECT_JSON)) + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.trim().endsWith(jsonObject));
    }

    @Test(timeout = 60000)
    public void testTransformationIgnoreTransformation() throws Exception {
        MessageProducer producer = session.createProducer(new ActiveMQQueue(("USERS." + (getQueueName()))));
        ObjectMessage message = session.createObjectMessage(new SamplePojo("Dejan", "Belgrade"));
        message.setStringProperty("transformation", JMS_OBJECT_XML.toString());
        producer.send(message);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = ((((("SUBSCRIBE\n" + "destination:/queue/USERS.") + (getQueueName())) + "\n") + "ack:auto") + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.endsWith("\n\n"));
    }

    @Test(timeout = 60000)
    public void testTransformationSendXMLMap() throws Exception {
        MessageConsumer consumer = session.createConsumer(queue);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = ((((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "transformation:") + (Transformations.JMS_MAP_XML)) + "\n\n") + (xmlMap)) + (NULL);
        stompConnection.sendFrame(frame);
        MapMessage message = ((MapMessage) (consumer.receive(2500)));
        Assert.assertNotNull(message);
        Assert.assertEquals(message.getString("name"), "Dejan");
    }

    @Test(timeout = 60000)
    public void testTransformationSendJSONMap() throws Exception {
        MessageConsumer consumer = session.createConsumer(queue);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = ((((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "transformation:") + (Transformations.JMS_MAP_JSON)) + "\n\n") + (jsonMap)) + (NULL);
        stompConnection.sendFrame(frame);
        MapMessage message = ((MapMessage) (consumer.receive(2500)));
        Assert.assertNotNull(message);
        Assert.assertEquals(message.getString("name"), "Dejan");
    }

    @Test(timeout = 60000)
    public void testTransformationReceiveXMLMap() throws Exception {
        MessageProducer producer = session.createProducer(new ActiveMQQueue(("USERS." + (getQueueName()))));
        MapMessage message = session.createMapMessage();
        message.setString("name", "Dejan");
        message.setString("city", "Belgrade");
        producer.send(message);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = ((((((("SUBSCRIBE\n" + "destination:/queue/USERS.") + (getQueueName())) + "\n") + "ack:auto\n") + "transformation:") + (Transformations.JMS_MAP_XML)) + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame xmlFrame = stompConnection.receive();
        StompTest.LOG.info("Received Frame: {}", xmlFrame.getBody());
        Map<String, String> map = createMapFromXml(xmlFrame.getBody());
        Assert.assertTrue(map.containsKey("name"));
        Assert.assertTrue(map.containsKey("city"));
        Assert.assertEquals("Dejan", map.get("name"));
        Assert.assertEquals("Belgrade", map.get("city"));
    }

    @Test(timeout = 60000)
    public void testTransformationReceiveJSONMap() throws Exception {
        MessageProducer producer = session.createProducer(new ActiveMQQueue(("USERS." + (getQueueName()))));
        MapMessage message = session.createMapMessage();
        message.setString("name", "Dejan");
        message.setString("city", "Belgrade");
        producer.send(message);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = ((((((("SUBSCRIBE\n" + "destination:/queue/USERS.") + (getQueueName())) + "\n") + "ack:auto\n") + "transformation:") + (Transformations.JMS_MAP_JSON)) + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame json = stompConnection.receive();
        StompTest.LOG.info("Received Frame: {}", json.getBody());
        Assert.assertNotNull(json);
        Map<String, String> map = createMapFromJson(json.getBody());
        Assert.assertTrue(map.containsKey("name"));
        Assert.assertTrue(map.containsKey("city"));
        Assert.assertEquals("Dejan", map.get("name"));
        Assert.assertEquals("Belgrade", map.get("city"));
    }

    @Test(timeout = 60000)
    public void testDurableUnsub() throws Exception {
        // get broker JMX view
        String domain = "org.apache.activemq";
        ObjectName brokerName = new ObjectName((domain + ":type=Broker,brokerName=localhost"));
        final BrokerViewMBean view = ((BrokerViewMBean) (brokerService.getManagementContext().newProxyInstance(brokerName, BrokerViewMBean.class, true)));
        // connect
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\nclient-id:test\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 0);
        // subscribe
        frame = (((("SUBSCRIBE\n" + "destination:/topic/") + (getQueueName())) + "\n") + "ack:auto\nactivemq.subscriptionName:test\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        // wait a bit for MBean to get refreshed
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (view.getDurableTopicSubscribers().length) == 1;
            }
        });
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 1);
        // disconnect
        frame = "DISCONNECT\nclient-id:test\n\n" + (NULL);
        stompConnection.sendFrame(frame);
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 1;
            }
        }, TimeUnit.SECONDS.toMillis(5), TimeUnit.MILLISECONDS.toMillis(25));
        // reconnect
        stompConnect();
        // connect
        frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\nclient-id:test\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        // unsubscribe
        frame = (((("UNSUBSCRIBE\n" + "destination:/topic/") + (getQueueName())) + "\n") + "activemq.subscriptionName:test\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = ("DISCONNECT\n" + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return ((view.getDurableTopicSubscribers().length) == 0) && ((view.getInactiveDurableTopicSubscribers().length) == 0);
            }
        }, TimeUnit.SECONDS.toMillis(5), TimeUnit.MILLISECONDS.toMillis(25));
        Assert.assertEquals(view.getDurableTopicSubscribers().length, 0);
        Assert.assertEquals(view.getInactiveDurableTopicSubscribers().length, 0);
    }

    @Test(timeout = 60000)
    public void testDurableSubAttemptOnQueueFails() throws Exception {
        // get broker JMX view
        String domain = "org.apache.activemq";
        ObjectName brokerName = new ObjectName((domain + ":type=Broker,brokerName=localhost"));
        BrokerViewMBean view = ((BrokerViewMBean) (brokerService.getManagementContext().newProxyInstance(brokerName, BrokerViewMBean.class, true)));
        // connect
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\nclient-id:test\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        Assert.assertEquals(view.getQueueSubscribers().length, 0);
        // subscribe
        frame = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:auto\nactivemq.subscriptionName:test\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("ERROR"));
        Assert.assertEquals(view.getQueueSubscribers().length, 0);
    }

    @Test(timeout = 60000)
    public void testMessageIdHeader() throws Exception {
        stompConnection.connect("system", "manager");
        stompConnection.begin("tx1");
        stompConnection.send(("/queue/" + (getQueueName())), "msg", "tx1", null);
        stompConnection.commit("tx1");
        stompConnection.subscribe(("/queue/" + (getQueueName())));
        StompFrame stompMessage = stompConnection.receive();
        Assert.assertNull(stompMessage.getHeaders().get("transaction"));
    }

    @Test(timeout = 60000)
    public void testPrefetchSizeOfOneClientAck() throws Exception {
        stompConnection.connect("system", "manager");
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("activemq.prefetchSize", "1");
        stompConnection.subscribe(("/queue/" + (getQueueName())), "client", headers);
        // send messages using JMS
        sendMessage("message 1");
        sendMessage("message 2");
        sendMessage("message 3");
        sendMessage("message 4");
        sendMessage("message 5");
        StompFrame frame = stompConnection.receive();
        Assert.assertEquals(frame.getBody(), "message 1");
        try {
            StompFrame frameNull = stompConnection.receive(500);
            if (frameNull != null) {
                Assert.fail("Should not have received the second message");
            }
        } catch (SocketTimeoutException soe) {
        }
        stompConnection.ack(frame);
        StompFrame frame1 = stompConnection.receive();
        Assert.assertEquals(frame1.getBody(), "message 2");
        try {
            StompFrame frameNull = stompConnection.receive(500);
            if (frameNull != null) {
                Assert.fail("Should not have received the third message");
            }
        } catch (SocketTimeoutException soe) {
        }
        stompConnection.ack(frame1);
        StompFrame frame2 = stompConnection.receive();
        Assert.assertEquals(frame2.getBody(), "message 3");
        try {
            StompFrame frameNull = stompConnection.receive(500);
            if (frameNull != null) {
                Assert.fail("Should not have received the fourth message");
            }
        } catch (SocketTimeoutException soe) {
        }
        stompConnection.ack(frame2);
        StompFrame frame3 = stompConnection.receive();
        Assert.assertEquals(frame3.getBody(), "message 4");
        try {
            StompFrame frameNull = stompConnection.receive(500);
            if (frameNull != null) {
                Assert.fail("Should not have received the fifth message");
            }
        } catch (SocketTimeoutException soe) {
        }
        stompConnection.ack(frame3);
        StompFrame frame4 = stompConnection.receive();
        Assert.assertEquals(frame4.getBody(), "message 5");
        try {
            StompFrame frameNull = stompConnection.receive(500);
            if (frameNull != null) {
                Assert.fail("Should not have received any more messages");
            }
        } catch (SocketTimeoutException soe) {
        }
        stompConnection.ack(frame4);
        try {
            StompFrame frameNull = stompConnection.receive(500);
            if (frameNull != null) {
                Assert.fail("Should not have received the any more messages");
            }
        } catch (SocketTimeoutException soe) {
        }
    }

    @Test(timeout = 60000)
    public void testPrefetchSize() throws Exception {
        stompConnection.connect("system", "manager");
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("activemq.prefetchSize", "1");
        stompConnection.subscribe(("/queue/" + (getQueueName())), "client", headers);
        // send messages using JMS
        sendMessage("message 1");
        sendMessage("message 2");
        sendMessage("message 3");
        sendMessage("message 4");
        sendMessage("message 5");
        StompFrame frame = stompConnection.receive(20000);
        Assert.assertEquals(frame.getBody(), "message 1");
        stompConnection.begin("tx1");
        stompConnection.ack(frame, "tx1");
        StompFrame frame1 = stompConnection.receive();
        Assert.assertEquals(frame1.getBody(), "message 2");
        try {
            StompFrame frame2 = stompConnection.receive(500);
            if (frame2 != null) {
                Assert.fail("Should not have received the second message");
            }
        } catch (SocketTimeoutException soe) {
        }
        stompConnection.ack(frame1, "tx1");
        Thread.sleep(1000);
        stompConnection.abort("tx1");
        stompConnection.begin("tx2");
        // Previously delivered message need to get re-acked...
        stompConnection.ack(frame, "tx2");
        stompConnection.ack(frame1, "tx2");
        StompFrame frame3 = stompConnection.receive(20000);
        Assert.assertEquals(frame3.getBody(), "message 3");
        stompConnection.ack(frame3, "tx2");
        StompFrame frame4 = stompConnection.receive(20000);
        Assert.assertEquals(frame4.getBody(), "message 4");
        stompConnection.ack(frame4, "tx2");
        stompConnection.commit("tx2");
        stompConnection.begin("tx3");
        StompFrame frame5 = stompConnection.receive(20000);
        Assert.assertEquals(frame5.getBody(), "message 5");
        stompConnection.ack(frame5, "tx3");
        stompConnection.commit("tx3");
        stompDisconnect();
    }

    @Test(timeout = 60000)
    public void testTransactionsWithMultipleDestinations() throws Exception {
        stompConnection.connect("system", "manager");
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("activemq.prefetchSize", "1");
        headers.put("activemq.exclusive", "true");
        stompConnection.subscribe("/queue/test1", "client", headers);
        stompConnection.begin("ID:tx1");
        headers.clear();
        headers.put("receipt", "ID:msg1");
        stompConnection.send("/queue/test2", "test message", "ID:tx1", headers);
        stompConnection.commit("ID:tx1");
        // make sure connection is active after commit
        Thread.sleep(1000);
        stompConnection.send("/queue/test1", "another message");
        StompFrame frame = stompConnection.receive(500);
        Assert.assertNotNull(frame);
    }

    @Test(timeout = 60000)
    public void testTempDestination() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SUBSCRIBE\n" + "destination:/temp-queue/") + (getQueueName())) + "\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = (((("SEND\n" + "destination:/temp-queue/") + (getQueueName())) + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame message = stompConnection.receive(1000);
        Assert.assertEquals("Hello World", message.getBody());
    }

    @Test(timeout = 60000)
    public void testJMSXUserIDIsSetInMessage() throws Exception {
        MessageConsumer consumer = session.createConsumer(queue);
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(frame);
        TextMessage message = ((TextMessage) (consumer.receive(5000)));
        Assert.assertNotNull(message);
        Assert.assertEquals("system", message.getStringProperty(USERID));
    }

    @Test(timeout = 60000)
    public void testJMSXUserIDIsSetInStompMessage() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = (((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame message = stompConnection.receive(5000);
        Assert.assertEquals("system", message.getHeaders().get(USERID));
    }

    @Test(timeout = 60000)
    public void testClientSetMessageIdIsIgnored() throws Exception {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(MESSAGE_ID, "Thisisnotallowed");
        headers.put(TIMESTAMP, "1234");
        headers.put(REDELIVERED, "true");
        headers.put(SUBSCRIPTION, "Thisisnotallowed");
        headers.put(USERID, "Thisisnotallowed");
        stompConnection.connect("system", "manager");
        stompConnection.send(("/queue/" + (getQueueName())), "msg", null, headers);
        stompConnection.subscribe(("/queue/" + (getQueueName())));
        StompFrame stompMessage = stompConnection.receive();
        Map<String, String> mess_headers = new HashMap<String, String>();
        mess_headers = stompMessage.getHeaders();
        Assert.assertFalse("Thisisnotallowed".equals(mess_headers.get(MESSAGE_ID)));
        Assert.assertTrue("1234".equals(mess_headers.get(TIMESTAMP)));
        Assert.assertNull(mess_headers.get(REDELIVERED));
        Assert.assertNull(mess_headers.get(SUBSCRIPTION));
        Assert.assertEquals("system", mess_headers.get(USERID));
    }

    @Test(timeout = 60000)
    public void testExpire() throws Exception {
        stompConnection.connect("system", "manager");
        HashMap<String, String> headers = new HashMap<String, String>();
        long timestamp = (System.currentTimeMillis()) - 100;
        headers.put(EXPIRATION_TIME, String.valueOf(timestamp));
        headers.put(PERSISTENT, "true");
        stompConnection.send(("/queue/" + (getQueueName())), "msg", null, headers);
        stompConnection.subscribe("/queue/ActiveMQ.DLQ");
        StompFrame stompMessage = stompConnection.receive(35000);
        Assert.assertNotNull(stompMessage);
        Assert.assertEquals(stompMessage.getHeaders().get(ORIGINAL_DESTINATION), ("/queue/" + (getQueueName())));
    }

    @Test(timeout = 60000)
    public void testDefaultJMSReplyToDest() throws Exception {
        stompConnection.connect("system", "manager");
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(REPLY_TO, "JustAString");
        headers.put(PERSISTENT, "true");
        stompConnection.send(("/queue/" + (getQueueName())), "msg-with-reply-to", null, headers);
        stompConnection.subscribe(("/queue/" + (getQueueName())));
        StompFrame stompMessage = stompConnection.receive(1000);
        Assert.assertNotNull(stompMessage);
        Assert.assertEquals(("" + stompMessage), stompMessage.getHeaders().get(REPLY_TO), "JustAString");
    }

    @Test(timeout = 60000)
    public void testPersistent() throws Exception {
        stompConnection.connect("system", "manager");
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(Stomp.Headers.Message.PERSISTENT, "true");
        stompConnection.send(("/queue/" + (getQueueName())), "hello", null, headers);
        stompConnection.subscribe(("/queue/" + (getQueueName())));
        StompFrame stompMessage = stompConnection.receive();
        Assert.assertNotNull(stompMessage);
        Assert.assertNotNull(stompMessage.getHeaders().get(Stomp.Headers.Message.PERSISTENT));
        Assert.assertEquals(stompMessage.getHeaders().get(Stomp.Headers.Message.PERSISTENT), "true");
    }

    @Test(timeout = 60000)
    public void testPersistentDefaultValue() throws Exception {
        stompConnection.connect("system", "manager");
        HashMap<String, String> headers = new HashMap<String, String>();
        stompConnection.send(("/queue/" + (getQueueName())), "hello", null, headers);
        stompConnection.subscribe(("/queue/" + (getQueueName())));
        StompFrame stompMessage = stompConnection.receive();
        Assert.assertNotNull(stompMessage);
        Assert.assertNull(stompMessage.getHeaders().get(Stomp.Headers.Message.PERSISTENT));
    }

    @Test(timeout = 60000)
    public void testReceiptNewQueue() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + 1234) + "\n") + "id:8fee4b8-4e5c9f66-4703-e936-3") + "\n") + "receipt:8fee4b8-4e5c9f66-4703-e936-2") + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame receipt = stompConnection.receive();
        Assert.assertTrue(receipt.getAction().startsWith("RECEIPT"));
        Assert.assertEquals("8fee4b8-4e5c9f66-4703-e936-2", receipt.getHeaders().get("receipt-id"));
        frame = ((((("SEND\n" + "destination:/queue/") + (getQueueName())) + 123) + "\ncontent-length:0") + " \n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = (((((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + 123) + "\n") + "id:8fee4b8-4e5c9f66-4703-e936-2") + "\n") + "receipt:8fee4b8-4e5c9f66-4703-e936-1") + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        receipt = stompConnection.receive();
        Assert.assertTrue(receipt.getAction().startsWith("RECEIPT"));
        Assert.assertEquals("8fee4b8-4e5c9f66-4703-e936-1", receipt.getHeaders().get("receipt-id"));
        StompFrame message = stompConnection.receive();
        Assert.assertTrue(message.getAction().startsWith("MESSAGE"));
        String length = message.getHeaders().get("content-length");
        Assert.assertEquals("0", length);
        Assert.assertEquals(0, message.getContent().length);
    }

    @Test(timeout = 60000)
    public void testTransactedClientAckBrokerStats() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        sendMessage(getName());
        sendMessage(getName());
        stompConnection.begin("tx1");
        frame = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:client\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame message = stompConnection.receive();
        Assert.assertTrue(message.getAction().equals("MESSAGE"));
        stompConnection.ack(message, "tx1");
        message = stompConnection.receive();
        Assert.assertTrue(message.getAction().equals("MESSAGE"));
        stompConnection.ack(message, "tx1");
        stompConnection.commit("tx1");
        frame = ("DISCONNECT\n" + "\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        final QueueViewMBean queueView = getProxyToQueue(getQueueName());
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (queueView.getDequeueCount()) == 2;
            }
        });
        Assert.assertEquals(2, queueView.getDispatchCount());
        Assert.assertEquals(2, queueView.getDequeueCount());
        Assert.assertEquals(0, queueView.getQueueSize());
    }

    @Test(timeout = 60000)
    public void testReplytoModification() throws Exception {
        String replyto = "some destination";
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        frame = (((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n") + "reply-to:") + replyto) + "\n\nhello world") + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame message = stompConnection.receive();
        Assert.assertTrue(message.getAction().equals("MESSAGE"));
        Assert.assertEquals(replyto, message.getHeaders().get("reply-to"));
    }

    @Test(timeout = 60000)
    public void testReplyToDestinationNaming() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        doTestActiveMQReplyToTempDestination("topic");
        doTestActiveMQReplyToTempDestination("queue");
    }

    @Test(timeout = 60000)
    public void testSendNullBodyTextMessage() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        sendMessage(null);
        frame = stompConnection.receiveFrame();
        Assert.assertNotNull("Message not received", frame);
    }

    @Test(timeout = 60000)
    public void testReplyToAcrossConnections() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        doReplyToAcrossConnections("topic");
        doReplyToAcrossConnections("queue");
    }

    @Test(timeout = 60000)
    public void testDisconnectDoesNotDeadlockBroker() throws Exception {
        for (int i = 0; i < 20; ++i) {
            doTestConnectionLeak();
        }
    }

    @Test(timeout = 60000)
    public void testHeaderValuesAreTrimmed1_0() throws Exception {
        String connectFrame = ("CONNECT\n" + (((("login:system\n" + "passcode:manager\n") + "accept-version:1.0\n") + "host:localhost\n") + "\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        StompTest.LOG.debug(("Broker sent: " + f));
        Assert.assertTrue(f.startsWith("CONNECTED"));
        String message = ((((((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\ntest1: value") + "\ntest2:value ") + "\ntest3: value ") + "\n\n") + "Hello World") + (NULL);
        stompConnection.sendFrame(message);
        String frame = ((((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "id:12345\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame received = stompConnection.receive();
        Assert.assertTrue(received.getAction().equals("MESSAGE"));
        Assert.assertEquals("value", received.getHeaders().get("test1"));
        Assert.assertEquals("value", received.getHeaders().get("test2"));
        Assert.assertEquals("value", received.getHeaders().get("test3"));
        frame = (((("UNSUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "id:12345\n\n") + (NULL);
        stompConnection.sendFrame(frame);
    }

    @Test(timeout = 60000)
    public void testSendReceiveBigMessage() throws Exception {
        String frame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(frame);
        frame = stompConnection.receiveFrame();
        Assert.assertTrue(frame.startsWith("CONNECTED"));
        frame = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:auto\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        int size = 100;
        char[] bigBodyArray = new char[size];
        Arrays.fill(bigBodyArray, 'a');
        String bigBody = new String(bigBodyArray);
        frame = (((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n\n") + bigBody) + (NULL);
        stompConnection.sendFrame(frame);
        StompFrame sframe = stompConnection.receive();
        Assert.assertNotNull(sframe);
        Assert.assertEquals("MESSAGE", sframe.getAction());
        Assert.assertEquals(bigBody, sframe.getBody());
        size = 3000000;
        bigBodyArray = new char[size];
        Arrays.fill(bigBodyArray, 'a');
        bigBody = new String(bigBodyArray);
        frame = (((("SEND\n" + "destination:/queue/") + (getQueueName())) + "\n\n") + bigBody) + (NULL);
        stompConnection.sendFrame(frame);
        sframe = stompConnection.receive(5000);
        Assert.assertNotNull(sframe);
        Assert.assertEquals("MESSAGE", sframe.getAction());
        Assert.assertEquals(bigBody, sframe.getBody());
    }

    @Test(timeout = 60000)
    public void testAckInTransactionTopic() throws Exception {
        doTestAckInTransaction(true);
    }

    @Test(timeout = 60000)
    public void testAckInTransactionQueue() throws Exception {
        doTestAckInTransaction(false);
    }
}

