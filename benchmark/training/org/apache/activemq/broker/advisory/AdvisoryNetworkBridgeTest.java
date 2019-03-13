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
package org.apache.activemq.broker.advisory;


import Session.AUTO_ACKNOWLEDGE;
import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.BrokerInfo;


public class AdvisoryNetworkBridgeTest extends TestCase {
    BrokerService broker1;

    BrokerService broker2;

    public void testAdvisory() throws Exception {
        createBroker1();
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://broker1");
        Connection conn = factory.createConnection();
        Session sess = conn.createSession(false, AUTO_ACKNOWLEDGE);
        conn.start();
        MessageConsumer consumer = sess.createConsumer(AdvisorySupport.getNetworkBridgeAdvisoryTopic());
        Thread.sleep(1000);
        createBroker2();
        ActiveMQMessage advisory = ((ActiveMQMessage) (consumer.receive(2000)));
        TestCase.assertNotNull(advisory);
        TestCase.assertTrue(((advisory.getDataStructure()) instanceof BrokerInfo));
        TestCase.assertTrue(advisory.getBooleanProperty("started"));
        assertCreatedByDuplex(advisory.getBooleanProperty("createdByDuplex"));
        broker2.stop();
        broker2.waitUntilStopped();
        advisory = ((ActiveMQMessage) (consumer.receive(2000)));
        TestCase.assertNotNull(advisory);
        TestCase.assertTrue(((advisory.getDataStructure()) instanceof BrokerInfo));
        TestCase.assertFalse(advisory.getBooleanProperty("started"));
        conn.close();
    }

    public void testAddConsumerLater() throws Exception {
        createBroker1();
        createBroker2();
        Thread.sleep(1000);
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://broker1");
        Connection conn = factory.createConnection();
        Session sess = conn.createSession(false, AUTO_ACKNOWLEDGE);
        conn.start();
        MessageConsumer consumer = sess.createConsumer(AdvisorySupport.getNetworkBridgeAdvisoryTopic());
        ActiveMQMessage advisory = ((ActiveMQMessage) (consumer.receive(2000)));
        TestCase.assertNotNull(advisory);
        TestCase.assertTrue(((advisory.getDataStructure()) instanceof BrokerInfo));
        TestCase.assertTrue(advisory.getBooleanProperty("started"));
        assertCreatedByDuplex(advisory.getBooleanProperty("createdByDuplex"));
        broker2.stop();
        broker2.waitUntilStopped();
        advisory = ((ActiveMQMessage) (consumer.receive(2000)));
        TestCase.assertNotNull(advisory);
        TestCase.assertTrue(((advisory.getDataStructure()) instanceof BrokerInfo));
        TestCase.assertFalse(advisory.getBooleanProperty("started"));
        consumer = sess.createConsumer(AdvisorySupport.getNetworkBridgeAdvisoryTopic());
        advisory = ((ActiveMQMessage) (consumer.receive(1000)));
        TestCase.assertNull(advisory);
        conn.close();
    }
}

