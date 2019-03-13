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
package org.apache.activemq.security;


import Session.AUTO_ACKNOWLEDGE;
import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;


public class JaasNetworkTest extends TestCase {
    BrokerService broker1;

    BrokerService broker2;

    public void testNetwork() throws Exception {
        System.setProperty("javax.net.ssl.trustStore", "src/test/resources/org/apache/activemq/security/client.ts");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");
        System.setProperty("javax.net.ssl.trustStoreType", "jks");
        System.setProperty("javax.net.ssl.keyStore", "src/test/resources/org/apache/activemq/security/client.ks");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
        System.setProperty("javax.net.ssl.keyStoreType", "jks");
        ActiveMQConnectionFactory producerFactory = new ActiveMQConnectionFactory("ssl://localhost:61617?verifyHostName=false");
        Connection producerConn = producerFactory.createConnection();
        Session producerSess = producerConn.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer producer = producerSess.createProducer(new ActiveMQQueue("test"));
        producerConn.start();
        TextMessage sentMessage = producerSess.createTextMessage("test");
        producer.send(sentMessage);
        ActiveMQConnectionFactory consumerFactory = new ActiveMQConnectionFactory("ssl://localhost:61618?verifyHostName=false");
        Connection consumerConn = consumerFactory.createConnection();
        Session consumerSess = consumerConn.createSession(false, AUTO_ACKNOWLEDGE);
        consumerConn.start();
        MessageConsumer consumer = consumerSess.createConsumer(new ActiveMQQueue("test"));
        TextMessage receivedMessage = ((TextMessage) (consumer.receive(100)));
        TestCase.assertEquals(sentMessage, receivedMessage);
    }
}

