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


import DeliveryMode.PERSISTENT;
import Message.DEFAULT_PRIORITY;
import Session.AUTO_ACKNOWLEDGE;
import java.util.Deque;
import java.util.LinkedList;
import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.management.openmbean.CompositeData;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class AMQ6117Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ6117Test.class);

    private BrokerService broker;

    @Test
    public void testViewIsStale() throws Exception {
        final int MSG_COUNT = 10;
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(broker.getVmConnectorURI());
        Connection connection = cf.createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("Test-Queue");
        Queue dlq = session.createQueue("ActiveMQ.DLQ");
        MessageProducer producer = session.createProducer(queue);
        // Ensure there is a DLQ in existence to start.
        session.createProducer(dlq);
        for (int i = 0; i < MSG_COUNT; ++i) {
            producer.send(session.createMessage(), PERSISTENT, DEFAULT_PRIORITY, 1000);
        }
        final QueueViewMBean queueView = getProxyToQueue(dlq.getQueueName());
        Assert.assertTrue("Message should be DLQ'd", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (queueView.getQueueSize()) == MSG_COUNT;
            }
        }));
        AMQ6117Test.LOG.info("DLQ has captured all expired messages");
        Deque<String> browsed = new LinkedList<String>();
        CompositeData[] elements = queueView.browse();
        Assert.assertEquals(MSG_COUNT, elements.length);
        for (CompositeData element : elements) {
            String messageID = ((String) (element.get("JMSMessageID")));
            AMQ6117Test.LOG.debug("MessageID: {}", messageID);
            browsed.add(messageID);
        }
        String removedMsgId = browsed.removeFirst();
        Assert.assertTrue(queueView.removeMessage(removedMsgId));
        Assert.assertEquals((MSG_COUNT - 1), queueView.getQueueSize());
        elements = queueView.browse();
        Assert.assertEquals((MSG_COUNT - 1), elements.length);
        for (CompositeData element : elements) {
            String messageID = ((String) (element.get("JMSMessageID")));
            AMQ6117Test.LOG.debug("MessageID: {}", messageID);
            Assert.assertFalse(messageID.equals(removedMsgId));
        }
    }
}

