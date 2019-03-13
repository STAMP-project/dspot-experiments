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
package org.apache.activemq.store.kahadb;


import Session.AUTO_ACKNOWLEDGE;
import java.net.URI;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ5626Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ5626Test.class);

    private final String QUEUE_NAME = "TesQ";

    private final String KAHADB_DIRECTORY = "target/activemq-data/";

    private BrokerService brokerService;

    private URI brokerUri;

    @Test(timeout = 30000)
    public void testPriorityMessages() throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUri);
        ActiveMQConnection connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(session.createQueue(QUEUE_NAME));
        Message message = session.createMessage();
        // 0,1
        producer.setPriority(9);
        producer.send(message);
        producer.send(message);
        // 2,3
        producer.setPriority(4);
        producer.send(message);
        producer.send(message);
        connection.close();
        stopRestartBroker();
        connectionFactory = new ActiveMQConnectionFactory(brokerUri);
        connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        connection.start();
        session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        producer = session.createProducer(session.createQueue(QUEUE_NAME));
        // 4
        producer.setPriority(4);
        producer.send(message);
        displayQueueViews(brokerService);
        // consume 5
        MessageConsumer jmsConsumer = session.createConsumer(session.createQueue(QUEUE_NAME));
        for (int i = 0; i < 5; i++) {
            message = jmsConsumer.receive(4000);
            Assert.assertNotNull(("Got message i=" + i), message);
            AMQ5626Test.LOG.info(((("received: " + (message.getJMSMessageID())) + ", priority:") + (message.getJMSPriority())));
        }
        connection.close();
    }
}

