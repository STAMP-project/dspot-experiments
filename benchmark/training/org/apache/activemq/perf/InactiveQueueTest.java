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
package org.apache.activemq.perf;


import Message.DEFAULT_TIME_TO_LIVE;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Session.AUTO_ACKNOWLEDGE;
import javax.jms.javax.jms.Message;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class InactiveQueueTest extends TestCase {
    private static final transient Logger LOG = LoggerFactory.getLogger(InactiveQueueTest.class);

    private static final int MESSAGE_COUNT = 0;

    private static final String DEFAULT_PASSWORD = "";

    private static final String USERNAME = "testuser";

    private static final String QUEUE_NAME = "testevent";

    private static final int DELIVERY_MODE = DeliveryMode.PERSISTENT;

    private static final int DELIVERY_PRIORITY = javax.jms.Message;

    ActiveMQConnectionFactory connectionFactory;

    BrokerService broker;

    private Connection connection;

    private MessageProducer publisher;

    private Destination destination;

    private Session session;

    public void testNoSubscribers() throws Exception {
        connection = connectionFactory.createConnection(InactiveQueueTest.USERNAME, InactiveQueueTest.DEFAULT_PASSWORD);
        TestCase.assertNotNull(connection);
        connection.start();
        session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        TestCase.assertNotNull(session);
        destination = session.createQueue(InactiveQueueTest.QUEUE_NAME);
        TestCase.assertNotNull(destination);
        publisher = session.createProducer(destination);
        TestCase.assertNotNull(publisher);
        MapMessage msg = session.createMapMessage();
        TestCase.assertNotNull(msg);
        msg.setString("key1", "value1");
        int loop;
        for (loop = 0; loop < (InactiveQueueTest.MESSAGE_COUNT); loop++) {
            msg.setInt("key2", loop);
            publisher.send(msg, InactiveQueueTest.DELIVERY_MODE, InactiveQueueTest.DELIVERY_PRIORITY, DEFAULT_TIME_TO_LIVE);
            if ((loop % 500) == 0) {
                InactiveQueueTest.LOG.debug((("Sent " + loop) + " messages"));
            }
        }
        Thread.sleep(1000000);
        TestCase.assertEquals(loop, InactiveQueueTest.MESSAGE_COUNT);
        publisher.close();
        session.close();
        connection.stop();
        connection.stop();
    }
}

