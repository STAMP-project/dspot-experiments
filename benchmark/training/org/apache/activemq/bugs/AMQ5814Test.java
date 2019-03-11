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
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;


public class AMQ5814Test {
    private BrokerService brokerService;

    private String openwireClientUrl;

    @Test(timeout = 30000)
    public void testProduceConsumeWithAuthorization() throws Exception {
        ConnectionFactory factory = new ActiveMQConnectionFactory(openwireClientUrl);
        Connection connection1 = factory.createConnection("subscriber", "123");
        Session session1 = connection1.createSession(false, AUTO_ACKNOWLEDGE);
        Topic wildCarded = session1.createTopic("dcu.>");
        MessageConsumer consumer = session1.createConsumer(wildCarded);
        connection1.start();
        Connection connection2 = factory.createConnection("publisher", "123");
        Session session2 = connection2.createSession(false, AUTO_ACKNOWLEDGE);
        Topic named = session2.createTopic("dcu.id");
        MessageProducer producer = session2.createProducer(named);
        producer.send(session2.createTextMessage("test"));
        Assert.assertNotNull(consumer.receive(2000));
        connection1.close();
        connection2.close();
    }
}

