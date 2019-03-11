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
package org.apache.activemq.transport.amqp;


import Session.SESSION_TRANSACTED;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class JmsTransactedMessageOrderTest extends JMSClientTestSupport {
    protected static final Logger LOG = LoggerFactory.getLogger(JmsTransactedMessageOrderTest.class);

    private final int prefetch;

    public JmsTransactedMessageOrderTest(int prefetch) {
        this.prefetch = prefetch;
    }

    @Test
    public void testMessageOrderAfterRollback() throws Exception {
        sendMessages(5);
        int counter = 0;
        while ((counter++) < 20) {
            JmsTransactedMessageOrderTest.LOG.info("Creating connection using prefetch of: {}", prefetch);
            JmsConnectionFactory cf = new JmsConnectionFactory(getAmqpURI(("jms.prefetchPolicy.all=" + (prefetch))));
            connection = cf.createConnection();
            connection.start();
            Session session = connection.createSession(true, SESSION_TRANSACTED);
            Queue queue = session.createQueue(getDestinationName());
            MessageConsumer consumer = session.createConsumer(queue);
            Message message = consumer.receive(5000);
            Assert.assertNotNull(message);
            Assert.assertTrue((message instanceof TextMessage));
            JmsTransactedMessageOrderTest.LOG.info("Read message = {}", getText());
            int sequenceID = message.getIntProperty("sequenceID");
            Assert.assertEquals(0, sequenceID);
            session.rollback();
            session.close();
            connection.close();
        } 
    }
}

