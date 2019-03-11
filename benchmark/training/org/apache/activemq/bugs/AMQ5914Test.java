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


import Message.DEFAULT_DELIVERY_MODE;
import Message.DEFAULT_PRIORITY;
import Session.AUTO_ACKNOWLEDGE;
import Session.SESSION_TRANSACTED;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public class AMQ5914Test {
    private ActiveMQConnection connection;

    private BrokerService broker;

    private String connectionUri;

    @Rule
    public TestName name = new TestName();

    @Test(timeout = 20000)
    public void testConsumerReceivePrefetchZeroMessageExpiredInFlight() throws Exception {
        connection.start();
        connection.getPrefetchPolicy().setAll(0);
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(name.getMethodName());
        MessageProducer producer = session.createProducer(queue);
        TextMessage expiredMessage = session.createTextMessage("expired message");
        TextMessage validMessage = session.createTextMessage("valid message");
        producer.send(expiredMessage, DEFAULT_DELIVERY_MODE, DEFAULT_PRIORITY, 1000);
        producer.send(validMessage);
        session.close();
        session = connection.createSession(true, SESSION_TRANSACTED);
        MessageConsumer consumer = session.createConsumer(queue);
        Message message = consumer.receive(3000);
        Assert.assertNotNull(message);
        TextMessage received = ((TextMessage) (message));
        Assert.assertEquals("expired message", received.getText());
        // Rollback allow the first message to expire.
        session.rollback();
        Thread.sleep(1500);
        // Consume again, this should fetch the second valid message via a pull.
        message = consumer.receive(3000);
        Assert.assertNotNull(message);
        received = ((TextMessage) (message));
        Assert.assertEquals("valid message", received.getText());
    }
}

