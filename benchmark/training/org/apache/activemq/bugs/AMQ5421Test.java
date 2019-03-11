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
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ5421Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ5421Test.class);

    private static final int DEST_COUNT = 1000;

    private final Destination[] destination = new Destination[AMQ5421Test.DEST_COUNT];

    private final MessageProducer[] producer = new MessageProducer[AMQ5421Test.DEST_COUNT];

    private BrokerService brokerService;

    private String connectionUri;

    @Test
    public void testManyTempDestinations() throws Exception {
        Connection connection = createConnectionFactory().createConnection();
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        for (int i = 0; i < (AMQ5421Test.DEST_COUNT); i++) {
            destination[i] = session.createTemporaryQueue();
            AMQ5421Test.LOG.debug("Created temp queue: [}", i);
        }
        for (int i = 0; i < (AMQ5421Test.DEST_COUNT); i++) {
            producer[i] = session.createProducer(destination[i]);
            AMQ5421Test.LOG.debug("Created producer: {}", i);
            TextMessage msg = session.createTextMessage((" testMessage " + i));
            producer[i].send(msg);
            AMQ5421Test.LOG.debug("message sent: {}", i);
            MessageConsumer consumer = session.createConsumer(destination[i]);
            Message message = consumer.receive(1000);
            Assert.assertTrue(message.equals(msg));
        }
        for (int i = 0; i < (AMQ5421Test.DEST_COUNT); i++) {
            producer[i].close();
        }
        connection.close();
    }
}

