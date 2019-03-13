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
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.spring.ConsumerBean;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// https://issues.apache.org/jira/browse/AMQ-6643
public class VirtualTopicWildcardTest {
    private static final Logger LOG = LoggerFactory.getLogger(VirtualTopicWildcardTest.class);

    protected int total = 3;

    protected Connection connection;

    BrokerService brokerService;

    @Test
    public void testWildcardAndSimpleConsumerShareMessages() throws Exception {
        ConsumerBean messageList1 = new ConsumerBean("1:");
        ConsumerBean messageList2 = new ConsumerBean("2:");
        ConsumerBean messageList3 = new ConsumerBean("3:");
        messageList1.setVerbose(true);
        messageList2.setVerbose(true);
        messageList3.setVerbose(true);
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination producerDestination = new ActiveMQTopic("VirtualTopic.TEST.A.IT");
        Destination destination1 = new ActiveMQQueue("Consumer.1.VirtualTopic.TEST.>");
        Destination destination2 = new ActiveMQQueue("Consumer.1.VirtualTopic.TEST.A.IT");
        Destination destination3 = new ActiveMQQueue("Consumer.1.VirtualTopic.TEST.B.IT");
        VirtualTopicWildcardTest.LOG.info(("Sending to: " + producerDestination));
        VirtualTopicWildcardTest.LOG.info(((((("Consuming from: " + destination1) + " and ") + destination2) + ", and ") + destination3));
        MessageConsumer c1 = session.createConsumer(destination1, null);
        MessageConsumer c2 = session.createConsumer(destination2, null);
        // this consumer should get no messages
        MessageConsumer c3 = session.createConsumer(destination3, null);
        c1.setMessageListener(messageList1);
        c2.setMessageListener(messageList2);
        c3.setMessageListener(messageList3);
        // create topic producer
        MessageProducer producer = session.createProducer(producerDestination);
        TestCase.assertNotNull(producer);
        for (int i = 0; i < (total); i++) {
            producer.send(createMessage(session, i));
        }
        assertMessagesArrived(messageList1, messageList2);
        TestCase.assertEquals(0, messageList3.getMessages().size());
    }
}

