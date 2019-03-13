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
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * Test cases to investigate subscription problems
 */
public class AMQ2174Test {
    @Rule
    public TestName testName = new TestName();

    private BrokerService broker;

    private ActiveMQConnectionFactory cf;

    @Test(timeout = 60000)
    public void testChangeDurableSub() throws Exception {
        Connection connection = cf.createConnection();
        connection.setClientID(testName.getMethodName());
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Topic destination = session.createTopic(testName.getMethodName());
        MessageConsumer consumer = session.createDurableSubscriber(destination, testName.getMethodName(), "color = 'red'", false);
        consumer.close();
        sendMessages();
        consumer = session.createDurableSubscriber(destination, testName.getMethodName(), "color = 'red'", false);
        Message received = consumer.receive(2000);
        Assert.assertNotNull(received);
        Assert.assertEquals("red", received.getStringProperty("color"));
        Assert.assertNull(consumer.receive(10));
        consumer.close();
        sendMessages();
        consumer = session.createDurableSubscriber(destination, testName.getMethodName(), "color = 'green'", false);
        received = consumer.receive(500);
        Assert.assertNull(received);
        sendMessages();
        received = consumer.receive(2000);
        Assert.assertNotNull(received);
        Assert.assertEquals("green", received.getStringProperty("color"));
        Assert.assertNull(consumer.receive(10));
        consumer.close();
        consumer = session.createDurableSubscriber(destination, testName.getMethodName());
        sendMessages();
        received = consumer.receive(2000);
        Assert.assertNotNull(received);
        Assert.assertEquals("red", received.getStringProperty("color"));
        received = consumer.receive(2000);
        Assert.assertNotNull(received);
        Assert.assertEquals("green", received.getStringProperty("color"));
        received = consumer.receive(2000);
        Assert.assertNotNull(received);
        Assert.assertEquals("blue", received.getStringProperty("color"));
        Assert.assertNull(consumer.receive(10));
        consumer.close();
    }
}

