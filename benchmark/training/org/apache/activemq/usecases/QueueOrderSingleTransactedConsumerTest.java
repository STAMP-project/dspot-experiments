/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.usecases;


import Session.SESSION_TRANSACTED;
import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QueueOrderSingleTransactedConsumerTest {
    private static final Logger LOG = LoggerFactory.getLogger(QueueOrderSingleTransactedConsumerTest.class);

    BrokerService broker = null;

    ActiveMQQueue dest = new ActiveMQQueue("Queue");

    @Test
    public void testSingleConsumerTxRepeat() throws Exception {
        // effect the broker sequence id that is region wide
        ActiveMQQueue dummyDest = new ActiveMQQueue("AnotherQueue");
        publishMessagesWithOrderProperty(10, 0, dest);
        publishMessagesWithOrderProperty(1, 0, dummyDest);
        publishMessagesWithOrderProperty(10, 10, dest);
        publishMessagesWithOrderProperty(1, 0, dummyDest);
        publishMessagesWithOrderProperty(10, 20, dest);
        publishMessagesWithOrderProperty(1, 0, dummyDest);
        publishMessagesWithOrderProperty(5, 30, dest);
        consumeVerifyOrderRollback(20);
        consumeVerifyOrderRollback(10);
        consumeVerifyOrderRollback(5);
    }

    @Test
    public void testSingleSessionXConsumerTxRepeat() throws Exception {
        publishMessagesWithOrderProperty(50);
        Connection connection = getConnectionFactory().createConnection();
        connection.start();
        Session session = connection.createSession(true, SESSION_TRANSACTED);
        MessageConsumer messageConsumer = consumeVerifyOrder(session, 20);
        messageConsumer.close();
        session.rollback();
        messageConsumer = consumeVerifyOrder(session, 10);
        messageConsumer.close();
        session.rollback();
        messageConsumer = consumeVerifyOrder(session, 5);
        messageConsumer.close();
        session.commit();
        connection.close();
    }

    @Test
    public void tesXConsumerTxRepeat() throws Exception {
        publishMessagesWithOrderProperty(10);
        Connection connection = getConnectionFactory().createConnection();
        connection.start();
        Session session = connection.createSession(true, SESSION_TRANSACTED);
        MessageConsumer messageConsumer = consumeVerifyOrder(session, 6);
        messageConsumer.close();
        messageConsumer = consumeVerifyOrder(session, 4, 6);
        // rollback before close, so there are two consumers in the mix
        session.rollback();
        messageConsumer.close();
        messageConsumer = consumeVerifyOrder(session, 10);
        session.commit();
        messageConsumer.close();
        connection.close();
    }

    @Test
    public void testSingleTxXConsumerTxRepeat() throws Exception {
        publishMessagesWithOrderProperty(10);
        Connection connection = getConnectionFactory().createConnection();
        connection.start();
        Session session = connection.createSession(true, SESSION_TRANSACTED);
        MessageConsumer messageConsumer = consumeVerifyOrder(session, 6);
        messageConsumer.close();
        messageConsumer = consumeVerifyOrder(session, 4, 6);
        messageConsumer.close();
        session.rollback();
        messageConsumer = consumeVerifyOrder(session, 10);
        session.commit();
        messageConsumer.close();
        connection.close();
    }
}

