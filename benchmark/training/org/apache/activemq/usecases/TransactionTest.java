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
package org.apache.activemq.usecases;


import Session.AUTO_ACKNOWLEDGE;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author pragmasoft
 */
public final class TransactionTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionTest.class);

    private volatile String receivedText;

    private Session producerSession;

    private Session consumerSession;

    private Destination queue;

    private MessageProducer producer;

    private MessageConsumer consumer;

    private Connection connection;

    private final CountDownLatch latch = new CountDownLatch(1);

    public void testTransaction() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        connection = factory.createConnection();
        queue = new ActiveMQQueue((((getClass().getName()) + ".") + (getName())));
        producerSession = connection.createSession(false, AUTO_ACKNOWLEDGE);
        consumerSession = connection.createSession(true, 0);
        producer = producerSession.createProducer(queue);
        consumer = consumerSession.createConsumer(queue);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message m) {
                try {
                    TextMessage tm = ((TextMessage) (m));
                    receivedText = tm.getText();
                    latch.countDown();
                    TransactionTest.LOG.info(("consumer received message :" + (receivedText)));
                    consumerSession.commit();
                    TransactionTest.LOG.info("committed transaction");
                } catch (JMSException e) {
                    try {
                        consumerSession.rollback();
                        TransactionTest.LOG.info("rolled back transaction");
                    } catch (JMSException e1) {
                        TransactionTest.LOG.info(e1.toString());
                        e1.printStackTrace();
                    }
                    TransactionTest.LOG.info(e.toString());
                    e.printStackTrace();
                }
            }
        });
        connection.start();
        TextMessage tm = null;
        try {
            tm = producerSession.createTextMessage();
            tm.setText(("Hello, " + (new Date())));
            producer.send(tm);
            TransactionTest.LOG.info(("producer sent message :" + (tm.getText())));
        } catch (JMSException e) {
            e.printStackTrace();
        }
        TransactionTest.LOG.info("Waiting for latch");
        latch.await(2, TimeUnit.SECONDS);
        TestCase.assertNotNull(receivedText);
        TransactionTest.LOG.info(("test completed, destination=" + (receivedText)));
    }
}

