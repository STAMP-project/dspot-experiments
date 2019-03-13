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
package org.apache.activemq.broker.util;


import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.junit.Test;


public class TimeStampingBrokerPluginTest extends TestCase {
    BrokerService broker;

    TransportConnector tcpConnector;

    MessageProducer producer;

    MessageConsumer consumer;

    Connection connection;

    Session session;

    Destination destination;

    String queue = "TEST.FOO";

    long expiry = 500;

    @Test
    public void testExpirationSet() throws Exception {
        // Create a messages
        Message sentMessage = session.createMessage();
        // Tell the producer to send the message
        long beforeSend = System.currentTimeMillis();
        producer.send(sentMessage);
        // Create a MessageConsumer from the Session to the Topic or Queue
        consumer = session.createConsumer(destination);
        // Wait for a message
        Message receivedMessage = consumer.receive(1000);
        // assert we got the same message ID we sent
        TestCase.assertEquals(sentMessage.getJMSMessageID(), receivedMessage.getJMSMessageID());
        // assert message timestamp is in window
        TestCase.assertTrue((("Expiration should be not null" + (receivedMessage.getJMSExpiration())) + "\n"), ((Long.valueOf(receivedMessage.getJMSExpiration())) != null));
        // assert message expiration is in window
        TestCase.assertTrue(((((("Before send: " + beforeSend) + " Msg ts: ") + (receivedMessage.getJMSTimestamp())) + " Msg Expiry: ") + (receivedMessage.getJMSExpiration())), ((beforeSend <= (receivedMessage.getJMSExpiration())) && ((receivedMessage.getJMSExpiration()) <= ((receivedMessage.getJMSTimestamp()) + (expiry)))));
    }

    @Test
    public void testExpirationCelingSet() throws Exception {
        // Create a messages
        Message sentMessage = session.createMessage();
        // Tell the producer to send the message
        long beforeSend = System.currentTimeMillis();
        long sendExpiry = beforeSend + ((expiry) * 22);
        sentMessage.setJMSExpiration(sendExpiry);
        producer.send(sentMessage);
        // Create a MessageConsumer from the Session to the Topic or Queue
        consumer = session.createConsumer(destination);
        // Wait for a message
        Message receivedMessage = consumer.receive(1000);
        // assert we got the same message ID we sent
        TestCase.assertEquals(sentMessage.getJMSMessageID(), receivedMessage.getJMSMessageID());
        // assert message timestamp is in window
        TestCase.assertTrue((("Expiration should be not null" + (receivedMessage.getJMSExpiration())) + "\n"), ((Long.valueOf(receivedMessage.getJMSExpiration())) != null));
        // assert message expiration is in window
        TestCase.assertTrue(((((("Sent expiry: " + sendExpiry) + " Recv ts: ") + (receivedMessage.getJMSTimestamp())) + " Recv expiry: ") + (receivedMessage.getJMSExpiration())), ((beforeSend <= (receivedMessage.getJMSExpiration())) && ((receivedMessage.getJMSExpiration()) <= ((receivedMessage.getJMSTimestamp()) + (expiry)))));
    }

    @Test
    public void testExpirationDLQ() throws Exception {
        // Create a messages
        Message sentMessage = session.createMessage();
        // Tell the producer to send the message
        long beforeSend = System.currentTimeMillis();
        long sendExpiry = beforeSend + (expiry);
        sentMessage.setJMSExpiration(sendExpiry);
        producer.send(sentMessage);
        // Create a MessageConsumer from the Session to the Topic or Queue
        consumer = session.createConsumer(destination);
        Thread.sleep(((expiry) + 250));
        // Wait for a message
        Message receivedMessage = consumer.receive(1000);
        // Message should roll to DLQ
        TestCase.assertNull(receivedMessage);
        // Close old consumer, setup DLQ listener
        consumer.close();
        consumer = session.createConsumer(session.createQueue(("DLQ." + (queue))));
        // Get mesage from DLQ
        receivedMessage = consumer.receive(1000);
        // assert we got the same message ID we sent
        TestCase.assertEquals(sentMessage.getJMSMessageID(), receivedMessage.getJMSMessageID());
        // assert message timestamp is in window
        // System.out.println("Recv: " + receivedMessage.getJMSExpiration());
        TestCase.assertEquals((("Expiration should be zero" + (receivedMessage.getJMSExpiration())) + "\n"), receivedMessage.getJMSExpiration(), 0);
    }
}

