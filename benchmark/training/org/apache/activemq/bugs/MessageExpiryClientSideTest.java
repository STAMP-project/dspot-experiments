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
package org.apache.activemq.bugs;


import Session.AUTO_ACKNOWLEDGE;
import java.util.Enumeration;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;


public class MessageExpiryClientSideTest {
    private ActiveMQConnection connection;

    private BrokerService broker;

    private volatile Exception connectionError;

    /**
     * check if the pull request (prefetch=1) times out when the expiry occurs
     * on the client side.
     */
    @Test(timeout = 30000)
    public void testConsumerReceivePrefetchOneRedeliveryZero() throws Exception {
        connection.getPrefetchPolicy().setQueuePrefetch(1);
        connection.start();
        // push message to queue
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("timeout.test");
        MessageProducer producer = session.createProducer(queue);
        TextMessage textMessage = session.createTextMessage("test Message");
        producer.send(textMessage);
        session.close();
        // try to consume message
        session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(queue);
        Message message = consumer.receive(1000);
        // message should be null as it should have expired and the
        // consumer.receive(timeout) should return null.
        Assert.assertNull(message);
        session.close();
        Assert.assertNull(connectionError);
    }

    /**
     * check if the pull request (prefetch=0) times out when the expiry occurs
     * on the client side.
     */
    @Test(timeout = 30000)
    public void testConsumerReceivePrefetchZeroRedeliveryZero() throws Exception {
        connection.getPrefetchPolicy().setQueuePrefetch(0);
        connection.start();
        // push message to queue
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("timeout.test");
        MessageProducer producer = session.createProducer(queue);
        TextMessage textMessage = session.createTextMessage("test Message");
        producer.send(textMessage);
        session.close();
        // try to consume message
        session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(queue);
        Message message = consumer.receive(1000);
        // message should be null as it should have expired and the
        // consumer.receive(timeout) should return null.
        Assert.assertNull(message);
        session.close();
        Assert.assertNull(connectionError);
    }

    /**
     * check if the pull request (prefetch=0) times out when the expiry occurs
     * on the client side.
     */
    @Test(timeout = 30000)
    public void testQueueBrowserPrefetchZeroRedeliveryZero() throws Exception {
        connection.getPrefetchPolicy().setQueuePrefetch(0);
        connection.start();
        // push message to queue
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("timeout.test");
        MessageProducer producer = session.createProducer(queue);
        TextMessage textMessage = session.createTextMessage("test Message");
        producer.send(textMessage);
        session.close();
        // try to consume message
        session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        QueueBrowser browser = session.createBrowser(queue);
        Message message = null;
        Enumeration<?> enumeration = browser.getEnumeration();
        while (enumeration.hasMoreElements()) {
            message = ((Message) (enumeration.nextElement()));
        } 
        // message should be null as it should have expired and the
        // consumer.receive(timeout) should return null.
        Assert.assertNull(message);
        session.close();
        Assert.assertNull(connectionError);
    }

    /**
     * check if the browse with (prefetch=1) times out when the expiry occurs
     * on the client side.
     */
    @Test(timeout = 30000)
    public void testQueueBrowserPrefetchOneRedeliveryZero() throws Exception {
        connection.getPrefetchPolicy().setQueuePrefetch(1);
        connection.start();
        // push message to queue
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("timeout.test");
        MessageProducer producer = session.createProducer(queue);
        TextMessage textMessage = session.createTextMessage("test Message");
        producer.send(textMessage);
        session.close();
        // try to consume message
        session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        QueueBrowser browser = session.createBrowser(queue);
        Message message = null;
        Enumeration<?> enumeration = browser.getEnumeration();
        while (enumeration.hasMoreElements()) {
            message = ((Message) (enumeration.nextElement()));
        } 
        // message should be null as it should have expired and the
        // consumer.receive(timeout) should return null.
        Assert.assertNull(message);
        session.close();
        Assert.assertNull(connectionError);
    }
}

