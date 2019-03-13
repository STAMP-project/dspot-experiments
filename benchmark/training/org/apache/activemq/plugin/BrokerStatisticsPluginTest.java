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
package org.apache.activemq.plugin;


import Message.DEFAULT_PRIORITY;
import Session.AUTO_ACKNOWLEDGE;
import StatisticsBroker.STATS_BROKER_PREFIX;
import StatisticsBroker.STATS_BROKER_RESET_HEADER;
import javax.jms.Connection;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static StatisticsBroker.STATS_DESTINATION_PREFIX;


/**
 * A BrokerStatisticsPluginTest
 * A testcase for https://issues.apache.org/activemq/browse/AMQ-2379
 */
public class BrokerStatisticsPluginTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(BrokerStatisticsPluginTest.class);

    private Connection connection;

    private BrokerService broker;

    public void testBrokerStats() throws Exception {
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue replyTo = session.createTemporaryQueue();
        MessageConsumer consumer = session.createConsumer(replyTo);
        Queue query = session.createQueue(STATS_BROKER_PREFIX);
        MessageProducer producer = session.createProducer(query);
        Message msg = session.createMessage();
        msg.setJMSReplyTo(replyTo);
        producer.send(msg);
        MapMessage reply = ((MapMessage) (consumer.receive((10 * 1000))));
        TestCase.assertNotNull(reply);
        TestCase.assertTrue(reply.getMapNames().hasMoreElements());
        TestCase.assertTrue(((reply.getJMSTimestamp()) > 0));
        TestCase.assertEquals(DEFAULT_PRIORITY, reply.getJMSPriority());
        /* for (Enumeration e = reply.getMapNames();e.hasMoreElements();) {
        String name = e.nextElement().toString();
        System.err.println(name+"="+reply.getObject(name));
        }
         */
    }

    public void testBrokerStatsReset() throws Exception {
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue replyTo = session.createTemporaryQueue();
        MessageConsumer consumer = session.createConsumer(replyTo);
        Queue testQueue = session.createQueue("Test.Queue");
        Queue query = session.createQueue(STATS_BROKER_PREFIX);
        MessageProducer producer = session.createProducer(null);
        producer.send(testQueue, session.createMessage());
        Message msg = session.createMessage();
        msg.setJMSReplyTo(replyTo);
        producer.send(query, msg);
        MapMessage reply = ((MapMessage) (consumer.receive((10 * 1000))));
        TestCase.assertNotNull(reply);
        TestCase.assertTrue(reply.getMapNames().hasMoreElements());
        TestCase.assertTrue(((reply.getLong("enqueueCount")) >= 1));
        msg = session.createMessage();
        msg.setBooleanProperty(STATS_BROKER_RESET_HEADER, true);
        msg.setJMSReplyTo(replyTo);
        producer.send(query, msg);
        reply = ((MapMessage) (consumer.receive((10 * 1000))));
        TestCase.assertNotNull(reply);
        TestCase.assertTrue(reply.getMapNames().hasMoreElements());
        TestCase.assertEquals(0, reply.getLong("enqueueCount"));
        TestCase.assertTrue(((reply.getJMSTimestamp()) > 0));
        TestCase.assertEquals(DEFAULT_PRIORITY, reply.getJMSPriority());
    }

    public void testDestinationStats() throws Exception {
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue replyTo = session.createTemporaryQueue();
        MessageConsumer consumer = session.createConsumer(replyTo);
        Queue testQueue = session.createQueue("Test.Queue");
        MessageProducer producer = session.createProducer(null);
        Queue query = session.createQueue(((STATS_DESTINATION_PREFIX) + (testQueue.getQueueName())));
        Message msg = session.createMessage();
        producer.send(testQueue, msg);
        msg.setJMSReplyTo(replyTo);
        producer.send(query, msg);
        MapMessage reply = ((MapMessage) (consumer.receive((10 * 1000))));
        TestCase.assertNotNull(reply);
        TestCase.assertTrue(reply.getMapNames().hasMoreElements());
        TestCase.assertTrue(((reply.getJMSTimestamp()) > 0));
        TestCase.assertEquals(DEFAULT_PRIORITY, reply.getJMSPriority());
        TestCase.assertTrue(((reply.getLong("averageMessageSize")) > 0));
        /* for (Enumeration e = reply.getMapNames();e.hasMoreElements();) {
        String name = e.nextElement().toString();
        System.err.println(name+"="+reply.getObject(name));
        }
         */
    }

    public void testDestinationStatsWithDot() throws Exception {
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue replyTo = session.createTemporaryQueue();
        MessageConsumer consumer = session.createConsumer(replyTo);
        Queue testQueue = session.createQueue("Test.Queue");
        MessageProducer producer = session.createProducer(null);
        Queue query = session.createQueue((((STATS_DESTINATION_PREFIX) + ".") + (testQueue.getQueueName())));
        Message msg = session.createMessage();
        producer.send(testQueue, msg);
        msg.setJMSReplyTo(replyTo);
        producer.send(query, msg);
        MapMessage reply = ((MapMessage) (consumer.receive((10 * 1000))));
        TestCase.assertNotNull(reply);
        TestCase.assertTrue(reply.getMapNames().hasMoreElements());
        TestCase.assertTrue(((reply.getJMSTimestamp()) > 0));
        TestCase.assertEquals(DEFAULT_PRIORITY, reply.getJMSPriority());
        /* for (Enumeration e = reply.getMapNames();e.hasMoreElements();) {
        String name = e.nextElement().toString();
        System.err.println(name+"="+reply.getObject(name));
        }
         */
    }
}

