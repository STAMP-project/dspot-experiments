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
package org.apache.activemq.transport.amqp;


import Session.AUTO_ACKNOWLEDGE;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JMSConcurrentConsumersTest extends AmqpTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(JMSConcurrentConsumersTest.class);

    private static final Integer ITERATIONS = 400;

    private static final Integer CONSUMER_COUNT = 4;// At least 2 consumers are


    // required to reproduce
    // the original issue
    public static final String TEXT_MESSAGE = "TextMessage: ";

    private CountDownLatch latch;

    private CountDownLatch initLatch;

    @Test(timeout = 60000)
    public void testSendWithMultipleConsumersTCP() throws Exception {
        doTestSendWithMultipleConsumers(amqpURI);
    }

    @Test(timeout = 60000)
    public void testSendWithMultipleConsumersNIO() throws Exception {
        doTestSendWithMultipleConsumers(amqpNioURI);
    }

    @Test(timeout = 60000)
    public void testSendWithMultipleConsumersSSL() throws Exception {
        doTestSendWithMultipleConsumers(amqpSslURI);
    }

    @Test(timeout = 60000)
    public void testSendWithMultipleConsumersNIOPlusSSL() throws Exception {
        doTestSendWithMultipleConsumers(amqpNioPlusSslURI);
    }

    static class ConsumerTask implements Callable<Boolean> {
        protected static final Logger LOG = LoggerFactory.getLogger(JMSConcurrentConsumersTest.ConsumerTask.class);

        private final String destinationName;

        private final String consumerName;

        private final CountDownLatch messagesReceived;

        private final URI amqpURI;

        private final int expectedMessageCount;

        private final CountDownLatch started;

        public ConsumerTask(CountDownLatch started, String destinationName, URI amqpURI, String consumerName, CountDownLatch latch, int expectedMessageCount) {
            this.started = started;
            this.destinationName = destinationName;
            this.amqpURI = amqpURI;
            this.consumerName = consumerName;
            this.messagesReceived = latch;
            this.expectedMessageCount = expectedMessageCount;
        }

        @Override
        public Boolean call() throws Exception {
            JMSConcurrentConsumersTest.ConsumerTask.LOG.debug(((consumerName) + " starting"));
            Connection connection = null;
            try {
                connection = JMSClientContext.INSTANCE.createConnection(amqpURI, "admin", "admin", false);
                Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
                Destination destination = session.createTopic(destinationName);
                MessageConsumer consumer = session.createConsumer(destination);
                connection.start();
                started.countDown();
                int receivedCount = 0;
                while (receivedCount < (expectedMessageCount)) {
                    Message message = consumer.receive(2000);
                    if (message == null) {
                        JMSConcurrentConsumersTest.ConsumerTask.LOG.error("consumer {} got null message on iteration {}", consumerName, receivedCount);
                        return false;
                    }
                    if (!(message instanceof TextMessage)) {
                        JMSConcurrentConsumersTest.ConsumerTask.LOG.error("consumer {} expected text message on iteration {} but got {}", consumerName, receivedCount, message.getClass().getCanonicalName());
                        return false;
                    }
                    TextMessage tm = ((TextMessage) (message));
                    if (!(tm.getText().equals(((JMSConcurrentConsumersTest.TEXT_MESSAGE) + receivedCount)))) {
                        JMSConcurrentConsumersTest.ConsumerTask.LOG.error("consumer {} expected {} got message [{}]", consumerName, receivedCount, tm.getText());
                        return false;
                    }
                    JMSConcurrentConsumersTest.ConsumerTask.LOG.trace("consumer {} expected {} got message [{}]", consumerName, receivedCount, tm.getText());
                    messagesReceived.countDown();
                    receivedCount++;
                } 
            } catch (Exception e) {
                JMSConcurrentConsumersTest.ConsumerTask.LOG.error(("UnexpectedException in " + (consumerName)), e);
            } finally {
                try {
                    connection.close();
                } catch (JMSException ignoreMe) {
                }
            }
            return true;
        }
    }
}

