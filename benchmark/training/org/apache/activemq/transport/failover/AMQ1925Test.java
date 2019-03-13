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
package org.apache.activemq.transport.failover;


import Session.SESSION_TRANSACTED;
import java.net.URI;
import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TransactionRolledBackException;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.Logger;


/**
 * TestCase showing the message-destroying described in AMQ-1925
 */
public class AMQ1925Test extends TestCase implements ExceptionListener {
    private static final Logger log = Logger.getLogger(AMQ1925Test.class);

    private static final String QUEUE_NAME = "test.amq1925";

    private static final String PROPERTY_MSG_NUMBER = "NUMBER";

    private static final int MESSAGE_COUNT = 10000;

    private BrokerService bs;

    private URI tcpUri;

    private ActiveMQConnectionFactory cf;

    private JMSException exception;

    public void testAMQ1925_TXBegin() throws Exception {
        Connection connection = cf.createConnection();
        connection.start();
        connection.setExceptionListener(this);
        Session session = connection.createSession(true, SESSION_TRANSACTED);
        MessageConsumer consumer = session.createConsumer(session.createQueue(AMQ1925Test.QUEUE_NAME));
        boolean restartDone = false;
        for (int i = 0; i < (AMQ1925Test.MESSAGE_COUNT); i++) {
            Message message = consumer.receive(5000);
            TestCase.assertNotNull(message);
            if ((i == 222) && (!restartDone)) {
                // Simulate broker failure & restart
                bs.stop();
                bs = new BrokerService();
                bs.setPersistent(true);
                bs.setUseJmx(true);
                bs.addConnector(tcpUri);
                bs.start();
                restartDone = true;
            }
            TestCase.assertEquals(i, message.getIntProperty(AMQ1925Test.PROPERTY_MSG_NUMBER));
            try {
                session.commit();
            } catch (TransactionRolledBackException expectedOnOccasion) {
                AMQ1925Test.log.info(("got rollback: " + expectedOnOccasion));
                i--;
            }
        }
        TestCase.assertNull(consumer.receive(500));
        consumer.close();
        session.close();
        connection.close();
        assertQueueEmpty();
        TestCase.assertNull(("no exception on connection listener: " + (exception)), exception);
    }

    public void testAMQ1925_TXCommited() throws Exception {
        Connection connection = cf.createConnection();
        connection.start();
        Session session = connection.createSession(true, SESSION_TRANSACTED);
        MessageConsumer consumer = session.createConsumer(session.createQueue(AMQ1925Test.QUEUE_NAME));
        for (int i = 0; i < (AMQ1925Test.MESSAGE_COUNT); i++) {
            Message message = consumer.receive(5000);
            TestCase.assertNotNull(message);
            TestCase.assertEquals(i, message.getIntProperty(AMQ1925Test.PROPERTY_MSG_NUMBER));
            session.commit();
            if (i == 222) {
                // Simulate broker failure & restart
                bs.stop();
                bs = new BrokerService();
                bs.setPersistent(true);
                bs.setUseJmx(true);
                bs.addConnector(tcpUri);
                bs.start();
            }
        }
        TestCase.assertNull(consumer.receive(500));
        consumer.close();
        session.close();
        connection.close();
        assertQueueEmpty();
    }
}

