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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.AdvisoryBroker;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.SessionInfo;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ4853Test {
    private static final transient Logger LOG = LoggerFactory.getLogger(AMQ4853Test.class);

    private static BrokerService brokerService;

    private static final String BROKER_ADDRESS = "tcp://localhost:0";

    private static final ActiveMQQueue DESTINATION = new ActiveMQQueue("TEST.QUEUE");

    private CountDownLatch cycleDoneLatch;

    private String connectionUri;

    @Test
    public void testEqualsNeeded() throws Exception {
        // setup
        AdvisoryBroker testObj = ((AdvisoryBroker) (AMQ4853Test.brokerService.getBroker().getAdaptor(AdvisoryBroker.class)));
        ActiveMQDestination destination = new ActiveMQQueue("foo");
        ConnectionInfo connectionInfo = createConnectionInfo();
        ConnectionContext connectionContext = new ConnectionContext(connectionInfo);
        connectionContext.setBroker(AMQ4853Test.brokerService.getBroker());
        SessionInfo sessionInfo = createSessionInfo(connectionInfo);
        for (int j = 1; j <= 5; j++) {
            ConsumerInfo consumerInfo = createConsumerInfo(sessionInfo, j, destination);
            testObj.addConsumer(connectionContext, consumerInfo);
        }
        for (int j = 1; j <= 5; j++) {
            ConsumerInfo consumerInfo = createConsumerInfo(sessionInfo, j, destination);
            testObj.removeConsumer(connectionContext, consumerInfo);
        }
        Assert.assertEquals(0, testObj.getAdvisoryConsumers().size());
    }

    class Consumer implements MessageListener {
        Connection connection;

        Session session;

        Destination destination;

        MessageConsumer consumer;

        Consumer() throws JMSException {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(connectionUri);
            connection = factory.createConnection();
            session = connection.createSession(false, AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(AMQ4853Test.DESTINATION);
            consumer.setMessageListener(this);
            connection.start();
        }

        @Override
        public void onMessage(Message message) {
        }

        public void close() {
            try {
                connection.close();
            } catch (Exception e) {
            }
            connection = null;
            session = null;
            consumer = null;
        }
    }

    class FixedDelyConsumer implements Runnable {
        private final CyclicBarrier barrier;

        private final int sleepInterval;

        public FixedDelyConsumer(CyclicBarrier barrier) {
            this.barrier = barrier;
            this.sleepInterval = 1000;
        }

        public FixedDelyConsumer(CyclicBarrier barrier, int sleepInterval) {
            this.barrier = barrier;
            this.sleepInterval = sleepInterval;
        }

        @Override
        public void run() {
            while (!(done())) {
                try {
                    AMQ4853Test.Consumer consumer = new AMQ4853Test.Consumer();
                    TimeUnit.MILLISECONDS.sleep(sleepInterval);
                    consumer.close();
                    barrier.await();
                } catch (Exception ex) {
                    return;
                }
            } 
        }
    }
}

