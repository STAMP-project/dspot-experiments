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
package org.apache.activemq.transport.nio;


import javax.jms.Connection;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.util.ConsumerThread;
import org.apache.activemq.util.ProducerThread;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NIOSSLLoadTest {
    private static final Logger LOG = LoggerFactory.getLogger(NIOSSLLoadTest.class);

    BrokerService broker;

    Connection connection;

    Session session;

    public static final String KEYSTORE_TYPE = "jks";

    public static final String PASSWORD = "password";

    public static final String SERVER_KEYSTORE = "src/test/resources/server.keystore";

    public static final String TRUST_KEYSTORE = "src/test/resources/client.keystore";

    public static final int PRODUCER_COUNT = 10;

    public static final int CONSUMER_COUNT = 10;

    public static final int MESSAGE_COUNT = 1000;

    final ConsumerThread[] consumers = new ConsumerThread[NIOSSLLoadTest.CONSUMER_COUNT];

    TransportConnector connector;

    @Test
    public void testLoad() throws Exception {
        Queue dest = session.createQueue("TEST");
        for (int i = 0; i < (NIOSSLLoadTest.PRODUCER_COUNT); i++) {
            ProducerThread producer = new ProducerThread(session, dest);
            producer.setMessageCount(NIOSSLLoadTest.MESSAGE_COUNT);
            producer.start();
        }
        for (int i = 0; i < (NIOSSLLoadTest.CONSUMER_COUNT); i++) {
            ConsumerThread consumer = new ConsumerThread(session, dest);
            consumer.setMessageCount(NIOSSLLoadTest.MESSAGE_COUNT);
            consumer.start();
            consumers[i] = consumer;
        }
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getReceived()) == ((NIOSSLLoadTest.PRODUCER_COUNT) * (NIOSSLLoadTest.MESSAGE_COUNT));
            }
        }, 60000);
        Assert.assertEquals(((NIOSSLLoadTest.PRODUCER_COUNT) * (NIOSSLLoadTest.MESSAGE_COUNT)), getReceived());
    }
}

