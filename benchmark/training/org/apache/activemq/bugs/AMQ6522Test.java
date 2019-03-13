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
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ6522Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ6522Test.class);

    private BrokerService broker;

    private ActiveMQConnectionFactory connectionFactory;

    private final Destination destination = new ActiveMQQueue("large_message_queue");

    private String connectionUri;

    @Test
    public void verifyMessageExceedsJournalRestartRecoveryCheck() throws Exception {
        Connection connection = connectionFactory.createConnection();
        connection.start();
        try {
            Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);
            BytesMessage message = session.createBytesMessage();
            message.writeBytes(new byte[(33 * 1024) * 1024]);
            producer.send(message);
        } finally {
            connection.close();
        }
        tearDown();
        initBroker(false);
        connection = connectionFactory.createConnection();
        connection.start();
        try {
            Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(destination);
            Assert.assertNotNull("Got message after restart", consumer.receive(20000));
        } finally {
            connection.close();
        }
    }
}

