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


import Session.DUPS_OK_ACKNOWLEDGE;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An AMQ-2401 Test
 */
public class AMQ2401Test extends TestCase implements MessageListener {
    private BrokerService broker;

    private ActiveMQConnectionFactory factory;

    private static final int SEND_COUNT = 500;

    private static final int CONSUMER_COUNT = 50;

    private static final int PRODUCER_COUNT = 1;

    private static final int LOG_INTERVAL = 10;

    private static final Logger LOG = LoggerFactory.getLogger(AMQ2401Test.class);

    private final ArrayList<AMQ2401Test.Service> services = new ArrayList<AMQ2401Test.Service>(((AMQ2401Test.CONSUMER_COUNT) + (AMQ2401Test.PRODUCER_COUNT)));

    private int count = 0;

    private CountDownLatch latch;

    public void testDupsOk() throws Exception {
        AMQ2401Test.TestProducer p = null;
        AMQ2401Test.TestConsumer c = null;
        try {
            latch = new CountDownLatch(AMQ2401Test.SEND_COUNT);
            for (int i = 0; i < (AMQ2401Test.CONSUMER_COUNT); i++) {
                AMQ2401Test.TestConsumer consumer = new AMQ2401Test.TestConsumer();
                consumer.start();
                services.add(consumer);
            }
            for (int i = 0; i < (AMQ2401Test.PRODUCER_COUNT); i++) {
                AMQ2401Test.TestProducer producer = new AMQ2401Test.TestProducer();
                producer.start();
                services.add(producer);
            }
            waitForMessageReceipt(TimeUnit.SECONDS.toMillis(30));
        } finally {
            if (p != null) {
                p.close();
            }
            if (c != null) {
                c.close();
            }
        }
    }

    private interface Service {
        public void start() throws Exception;

        public void close();
    }

    private class TestProducer implements Runnable , AMQ2401Test.Service {
        Thread thread;

        BytesMessage message;

        Connection connection;

        Session session;

        MessageProducer producer;

        TestProducer() throws Exception {
            thread = new Thread(this, "TestProducer");
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, DUPS_OK_ACKNOWLEDGE);
            producer = session.createProducer(session.createQueue("AMQ2401Test"));
        }

        @Override
        public void start() {
            thread.start();
        }

        @Override
        public void run() {
            int count = (AMQ2401Test.SEND_COUNT) / (AMQ2401Test.PRODUCER_COUNT);
            for (int i = 1; i <= count; i++) {
                try {
                    if ((i % (AMQ2401Test.LOG_INTERVAL)) == 0) {
                        AMQ2401Test.LOG.debug(("Sending: " + i));
                    }
                    message = session.createBytesMessage();
                    message.writeBytes(new byte[1024]);
                    producer.send(message);
                } catch (JMSException jmse) {
                    jmse.printStackTrace();
                    break;
                }
            }
        }

        @Override
        public void close() {
            try {
                connection.close();
            } catch (JMSException e) {
            }
        }
    }

    private class TestConsumer implements Runnable , AMQ2401Test.Service {
        ActiveMQConnection connection;

        Session session;

        MessageConsumer consumer;

        TestConsumer() throws Exception {
            factory.setOptimizeAcknowledge(false);
            connection = ((ActiveMQConnection) (factory.createConnection()));
            session = connection.createSession(false, DUPS_OK_ACKNOWLEDGE);
            consumer = session.createConsumer(session.createQueue("AMQ2401Test"));
            consumer.setMessageListener(AMQ2401Test.this);
        }

        @Override
        public void start() throws Exception {
            connection.start();
        }

        @Override
        public void close() {
            try {
                connection.close();
            } catch (JMSException e) {
            }
        }

        @Override
        public void run() {
            while ((latch.getCount()) > 0) {
                try {
                    onMessage(consumer.receive());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } 
        }
    }
}

