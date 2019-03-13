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
package org.apache.activemq.ra;


import Session.AUTO_ACKNOWLEDGE;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.resource.ResourceException;
import javax.transaction.xa.XAException;
import org.junit.Assert;
import org.junit.Test;


public class UnsubscribeResubscribeTest {
    private static final String DEFAULT_HOST = "vm://localhost?broker.persistent=false";

    private ConnectionManagerAdapter connectionManager = new ConnectionManagerAdapter();

    private ActiveMQManagedConnectionFactory managedConnectionFactory;

    private ConnectionFactory connectionFactory;

    private ManagedConnectionProxy connection;

    private ActiveMQManagedConnection managedConnection;

    @Test(timeout = 60000)
    public void testUnsubscribeResubscribe() throws JMSException, ResourceException, XAException {
        getConnection();
        Assert.assertNotNull(managedConnection);
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic("topic");
        TopicSubscriber sub = session.createDurableSubscriber(topic, "sub");
        Message message = session.createTextMessage("text message");
        MessageProducer producer = session.createProducer(topic);
        producer.send(message);
        sub.close();
        session.unsubscribe("sub");
        sub = session.createDurableSubscriber(topic, "sub");
    }
}

