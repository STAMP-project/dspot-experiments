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
package org.apache.activemq.proxy;


import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Session;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.ConsumerThread;
import org.apache.activemq.util.ProducerThread;


public class ProxyFailoverTest extends TestCase {
    BrokerService proxyBroker;

    BrokerService remoteBroker;

    public void testFailover() throws Exception {
        ActiveMQConnectionFactory producerFactory = new ActiveMQConnectionFactory("failover:(tcp://localhost:61616,tcp://localhost:61626)?randomize=false");
        Connection producerConnection = producerFactory.createConnection();
        producerConnection.start();
        Session producerSession = producerConnection.createSession(false, AUTO_ACKNOWLEDGE);
        ProducerThread producer = new ProducerThread(producerSession, producerSession.createQueue("ProxyTest"));
        producer.setSleep(10);
        producer.start();
        ActiveMQConnectionFactory consumerFactory = new ActiveMQConnectionFactory("tcp://localhost:51618");
        Connection consumerConnection = consumerFactory.createConnection();
        consumerConnection.start();
        Session consumerSession = consumerConnection.createSession(false, AUTO_ACKNOWLEDGE);
        ConsumerThread consumer = new ConsumerThread(consumerSession, consumerSession.createQueue("ProxyTest"));
        consumer.start();
        TimeUnit.SECONDS.sleep(15);
        remoteBroker.stop();
        remoteBroker.waitUntilStopped();
        startRemoteBroker(false);
        producer.join();
        consumer.join();
        TestCase.assertEquals(1000, consumer.getReceived());
    }
}

