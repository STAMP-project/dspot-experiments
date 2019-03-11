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
package org.apache.activemq.broker.util;


import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.junit.Test;


/**
 * Tests TraceBrokerPathPlugin by creating two brokers linked by a network connector, and checking to see if the consuming end receives the expected value in the trace property
 *
 * @author Raul Kripalani
 */
public class TraceBrokerPathPluginTest extends TestCase {
    BrokerService brokerA;

    BrokerService brokerB;

    TransportConnector tcpConnectorA;

    TransportConnector tcpConnectorB;

    MessageProducer producer;

    MessageConsumer consumer;

    Connection connectionA;

    Connection connectionB;

    Session sessionA;

    Session sessionB;

    String queue = "TEST.FOO";

    String traceProperty = "BROKER_PATH";

    @Test
    public void testTraceBrokerPathPlugin() throws Exception {
        Message sentMessage = sessionA.createMessage();
        producer.send(sentMessage);
        Message receivedMessage = consumer.receive(1000);
        // assert we got the message
        TestCase.assertNotNull(receivedMessage);
        // assert we got the same message ID we sent
        TestCase.assertEquals(sentMessage.getJMSMessageID(), receivedMessage.getJMSMessageID());
        TestCase.assertEquals("brokerA,brokerB", receivedMessage.getStringProperty(traceProperty));
    }
}

