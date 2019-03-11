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


import Session.AUTO_ACKNOWLEDGE;
import java.io.IOException;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Stomp.NULL;


public class StompSubscriptionRemoveTest extends StompTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(StompSubscriptionRemoveTest.class);

    private static final String COMMAND_MESSAGE = "MESSAGE";

    private static final String HEADER_MESSAGE_ID = "message-id";

    @Test(timeout = 60000)
    public void testRemoveSubscriber() throws Exception {
        stompConnect();
        Connection connection = cf.createConnection("system", "manager");
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(new ActiveMQQueue(getQueueName()));
        Message message = session.createTextMessage("Testas");
        for (int idx = 0; idx < 2000; ++idx) {
            producer.send(message);
            StompSubscriptionRemoveTest.LOG.debug(("Sending: " + idx));
        }
        producer.close();
        session.close();
        connection.close();
        String connectFrame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        stompConnection.receiveFrame();
        String frame = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:client\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        int messagesCount = 0;
        int count = 0;
        while (count < 2) {
            String receiveFrame = stompConnection.receiveFrame();
            StompSubscriptionRemoveTest.LOG.debug(("Received: " + receiveFrame));
            Assert.assertEquals("Unexpected frame received", StompSubscriptionRemoveTest.COMMAND_MESSAGE, getCommand(receiveFrame));
            String messageId = getHeaderValue(receiveFrame, StompSubscriptionRemoveTest.HEADER_MESSAGE_ID);
            String ackmessage = (((("ACK\n" + (StompSubscriptionRemoveTest.HEADER_MESSAGE_ID)) + ":") + messageId) + "\n\n") + (NULL);
            stompConnection.sendFrame(ackmessage);
            // Thread.sleep(1000);
            ++messagesCount;
            ++count;
        } 
        stompDisconnect();
        Thread.sleep(1000);
        stompConnect();
        connectFrame = ("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL);
        stompConnection.sendFrame(connectFrame);
        stompConnection.receiveFrame();
        frame = (((("SUBSCRIBE\n" + "destination:/queue/") + (getQueueName())) + "\n") + "ack:client\n\n") + (NULL);
        stompConnection.sendFrame(frame);
        try {
            while (count != 2000) {
                String receiveFrame = stompConnection.receiveFrame();
                StompSubscriptionRemoveTest.LOG.debug(("Received: " + receiveFrame));
                Assert.assertEquals("Unexpected frame received", StompSubscriptionRemoveTest.COMMAND_MESSAGE, getCommand(receiveFrame));
                String messageId = getHeaderValue(receiveFrame, StompSubscriptionRemoveTest.HEADER_MESSAGE_ID);
                String ackmessage = (((("ACK\n" + (StompSubscriptionRemoveTest.HEADER_MESSAGE_ID)) + ":") + (messageId.trim())) + "\n\n") + (NULL);
                stompConnection.sendFrame(ackmessage);
                // Thread.sleep(1000);
                ++messagesCount;
                ++count;
            } 
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        stompConnection.sendFrame("DISCONNECT\n\n");
        stompConnection.close();
        StompSubscriptionRemoveTest.LOG.info(("Total messages received: " + messagesCount));
        Assert.assertTrue(("Messages received after connection loss: " + messagesCount), (messagesCount >= 2000));
        // The first ack messages has no chance complete, so we receiving more
        // messages
        // Don't know how to list subscriptions for the broker. Currently you
        // can check using JMX console. You'll see
        // Subscription without any connections
    }
}

