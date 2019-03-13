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


import Session.SESSION_TRANSACTED;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.junit.Assert;
import org.junit.Test;


public class AMQ6125Test {
    private BrokerService broker;

    @Test
    public void testRollbackWithNoPolicy() throws Exception {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(broker.getVmConnectorURI());
        cf.setRedeliveryPolicyMap(new RedeliveryPolicyMap());
        Connection connection = cf.createConnection();
        connection.start();
        Session session = connection.createSession(true, SESSION_TRANSACTED);
        Queue queue = session.createTemporaryQueue();
        MessageProducer producer = session.createProducer(queue);
        producer.send(session.createMessage());
        session.commit();
        MessageConsumer consumer = session.createConsumer(queue);
        Message message = consumer.receive(2000);
        Assert.assertNotNull(message);
        session.rollback();
    }
}

