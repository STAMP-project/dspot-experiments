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
package org.apache.activemq.usecases;


import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;


public class AMQStackOverFlowTest extends TestCase {
    private static final String URL1 = "tcp://localhost:61616";

    private static final String URL2 = "tcp://localhost:61617";

    public void testStackOverflow() throws Exception {
        BrokerService brokerService1 = null;
        BrokerService brokerService2 = null;
        try {
            brokerService1 = createBrokerService("broker1", AMQStackOverFlowTest.URL1, AMQStackOverFlowTest.URL2);
            brokerService1.start();
            brokerService2 = createBrokerService("broker2", AMQStackOverFlowTest.URL2, AMQStackOverFlowTest.URL1);
            brokerService2.start();
            final ActiveMQConnectionFactory cf1 = new ActiveMQConnectionFactory(AMQStackOverFlowTest.URL1);
            cf1.setUseAsyncSend(false);
            final ActiveMQConnectionFactory cf2 = new ActiveMQConnectionFactory(AMQStackOverFlowTest.URL2);
            cf2.setUseAsyncSend(false);
            final JmsTemplate template1 = new JmsTemplate(cf1);
            template1.setReceiveTimeout(10000);
            template1.send("test.q", new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage("test");
                }
            });
            final JmsTemplate template2 = new JmsTemplate(cf2);
            template2.setReceiveTimeout(10000);
            final Message m = template2.receive("test.q");
            TestCase.assertTrue((m instanceof TextMessage));
            final TextMessage tm = ((TextMessage) (m));
            TestCase.assertEquals("test", tm.getText());
            template2.send("test2.q", new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage("test2");
                }
            });
            final Message m2 = template1.receive("test2.q");
            TestCase.assertNotNull(m2);
            TestCase.assertTrue((m2 instanceof TextMessage));
            final TextMessage tm2 = ((TextMessage) (m2));
            TestCase.assertEquals("test2", tm2.getText());
        } finally {
            brokerService1.stop();
            brokerService1 = null;
            brokerService2.stop();
            brokerService2 = null;
        }
    }
}

