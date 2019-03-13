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
package org.apache.activemq.broker.virtual;


import Session.AUTO_ACKNOWLEDGE;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.EmbeddedBrokerTestSupport;
import org.apache.activemq.spring.ConsumerBean;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class CompositeQueueTest extends EmbeddedBrokerTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeQueueTest.class);

    protected int total = 10;

    protected Connection connection;

    public String messageSelector1;

    public String messageSelector2 = null;

    @Test
    public void testVirtualTopicCreation() throws Exception {
        if ((connection) == null) {
            connection = createConnection();
        }
        connection.start();
        ConsumerBean messageList1 = new ConsumerBean();
        ConsumerBean messageList2 = new ConsumerBean();
        messageList1.setVerbose(true);
        messageList2.setVerbose(true);
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination producerDestination = getProducerDestination();
        Destination destination1 = getConsumer1Dsetination();
        Destination destination2 = getConsumer2Dsetination();
        CompositeQueueTest.LOG.info(("Sending to: " + producerDestination));
        CompositeQueueTest.LOG.info(((("Consuming from: " + destination1) + " and ") + destination2));
        MessageConsumer c1 = session.createConsumer(destination1, messageSelector1);
        MessageConsumer c2 = session.createConsumer(destination2, messageSelector2);
        c1.setMessageListener(messageList1);
        c2.setMessageListener(messageList2);
        // create topic producer
        MessageProducer producer = session.createProducer(producerDestination);
        assertNotNull(producer);
        for (int i = 0; i < (total); i++) {
            producer.send(createMessage(session, i));
        }
        assertMessagesArrived(messageList1, messageList2);
        assertOriginalDestination(messageList1, messageList2);
    }
}

