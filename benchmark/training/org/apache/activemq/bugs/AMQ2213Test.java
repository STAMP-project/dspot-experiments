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
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;


public class AMQ2213Test {
    BrokerService broker;

    ConnectionFactory factory;

    Connection connection;

    Session session;

    Queue queue;

    MessageConsumer consumer;

    @Test
    public void testEqualsGenericSession() throws JMSException {
        Assert.assertNotNull(this.connection);
        Session sess = this.connection.createSession(false, AUTO_ACKNOWLEDGE);
        Assert.assertTrue(sess.equals(sess));
    }

    @Test
    public void testEqualsTopicSession() throws JMSException {
        Assert.assertNotNull(this.connection);
        Assert.assertTrue(((this.connection) instanceof TopicConnection));
        TopicSession sess = ((TopicConnection) (this.connection)).createTopicSession(false, AUTO_ACKNOWLEDGE);
        Assert.assertTrue(sess.equals(sess));
    }

    @Test
    public void testEqualsQueueSession() throws JMSException {
        Assert.assertNotNull(this.connection);
        Assert.assertTrue(((this.connection) instanceof QueueConnection));
        QueueSession sess = ((QueueConnection) (this.connection)).createQueueSession(false, AUTO_ACKNOWLEDGE);
        Assert.assertTrue(sess.equals(sess));
    }
}

