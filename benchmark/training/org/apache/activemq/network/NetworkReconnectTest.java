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
package org.apache.activemq.network;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * These test cases are used to verifiy that network connections get re
 * established in all broker restart scenarios.
 *
 * @author chirino
 */
public class NetworkReconnectTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(NetworkReconnectTest.class);

    private BrokerService producerBroker;

    private BrokerService consumerBroker;

    private ActiveMQConnectionFactory producerConnectionFactory;

    private ActiveMQConnectionFactory consumerConnectionFactory;

    private Destination destination;

    private ArrayList<Connection> connections = new ArrayList<Connection>();

    public void testWithProducerBrokerRestart() throws Exception {
        startProducerBroker();
        startConsumerBroker();
        MessageConsumer consumer = createConsumer();
        AtomicInteger counter = createConsumerCounter(producerConnectionFactory);
        waitForConsumerToArrive(counter);
        String messageId = sendMessage();
        Message message = consumer.receive(1000);
        TestCase.assertEquals(messageId, message.getJMSMessageID());
        TestCase.assertNull(consumer.receiveNoWait());
        // Restart the first broker...
        stopProducerBroker();
        startProducerBroker();
        counter = createConsumerCounter(producerConnectionFactory);
        waitForConsumerToArrive(counter);
        messageId = sendMessage();
        message = consumer.receive(1000);
        TestCase.assertEquals(messageId, message.getJMSMessageID());
        TestCase.assertNull(consumer.receiveNoWait());
    }
}

