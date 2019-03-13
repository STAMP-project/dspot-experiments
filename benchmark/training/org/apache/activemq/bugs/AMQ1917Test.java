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


import ActiveMQDestination.QUEUE_TYPE;
import DeliveryMode.NON_PERSISTENT;
import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;


public class AMQ1917Test extends TestCase {
    private static final int NUM_MESSAGES = 4000;

    private static final int NUM_THREADS = 10;

    private static final String REQUEST_QUEUE = "mock.in.queue";

    private static final String REPLY_QUEUE = "mock.out.queue";

    private Destination requestDestination = ActiveMQDestination.createDestination(AMQ1917Test.REQUEST_QUEUE, QUEUE_TYPE);

    private Destination replyDestination = ActiveMQDestination.createDestination(AMQ1917Test.REPLY_QUEUE, QUEUE_TYPE);

    private CountDownLatch roundTripLatch = new CountDownLatch(AMQ1917Test.NUM_MESSAGES);

    private CountDownLatch errorLatch = new CountDownLatch(1);

    private ThreadPoolExecutor tpe;

    private final String BROKER_URL = "tcp://localhost:0";

    private String connectionUri;

    private BrokerService broker = null;

    private boolean working = true;

    // trival session/producer pool
    final Session[] sessions = new Session[AMQ1917Test.NUM_THREADS];

    final MessageProducer[] producers = new MessageProducer[AMQ1917Test.NUM_THREADS];

    public void testLoadedSendRecieveWithCorrelationId() throws Exception {
        ActiveMQConnectionFactory connectionFactory = new org.apache.activemq.org.apache.activemq.ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(connectionUri);
        Connection connection = connectionFactory.createConnection();
        setupReceiver(connection);
        connection = connectionFactory.createConnection();
        connection.start();
        // trival session/producer pool
        for (int i = 0; i < (AMQ1917Test.NUM_THREADS); i++) {
            sessions[i] = connection.createSession(false, AUTO_ACKNOWLEDGE);
            producers[i] = sessions[i].createProducer(requestDestination);
        }
        for (int i = 0; i < (AMQ1917Test.NUM_MESSAGES); i++) {
            AMQ1917Test.MessageSenderReceiver msr = new AMQ1917Test.MessageSenderReceiver(requestDestination, replyDestination, ("Test Message : " + i));
            tpe.execute(msr);
        }
        while (!(roundTripLatch.await(4000, TimeUnit.MILLISECONDS))) {
            if (errorLatch.await(1000, TimeUnit.MILLISECONDS)) {
                TestCase.fail("there was an error, check the console for thread or thread allocation failure");
                break;
            }
        } 
        working = false;
    }

    class MessageSenderReceiver implements Runnable {
        Destination reqDest;

        Destination replyDest;

        String origMsg;

        public MessageSenderReceiver(Destination reqDest, Destination replyDest, String msg) throws Exception {
            this.replyDest = replyDest;
            this.reqDest = reqDest;
            this.origMsg = msg;
        }

        private int getIndexFromCurrentThread() {
            String name = Thread.currentThread().getName();
            String num = name.substring(((name.lastIndexOf('-')) + 1));
            int idx = (Integer.parseInt(num)) - 1;
            TestCase.assertTrue(("idx is in range: idx=" + idx), (idx < (AMQ1917Test.NUM_THREADS)));
            return idx;
        }

        public void run() {
            try {
                // get thread session and producer from pool
                int threadIndex = getIndexFromCurrentThread();
                Session session = sessions[threadIndex];
                MessageProducer producer = producers[threadIndex];
                final Message sendJmsMsg = session.createTextMessage(origMsg);
                producer.setDeliveryMode(NON_PERSISTENT);
                producer.send(sendJmsMsg);
                String jmsId = sendJmsMsg.getJMSMessageID();
                String selector = ("JMSCorrelationID='" + jmsId) + "'";
                MessageConsumer consumer = session.createConsumer(replyDest, selector);
                Message receiveJmsMsg = consumer.receive(2000);
                consumer.close();
                if (receiveJmsMsg == null) {
                    errorLatch.countDown();
                    TestCase.fail(((("Unable to receive response for:" + (origMsg)) + ", with selector=") + selector));
                } else {
                    // System.out.println("received response message :"
                    // + ((TextMessage) receiveJmsMsg).getText()
                    // + " with selector : " + selector);
                    roundTripLatch.countDown();
                }
            } catch (JMSException e) {
                TestCase.fail(("unexpected exception:" + e));
            }
        }
    }

    public class LimitedThreadFactory implements ThreadFactory {
        int threadCount;

        private ThreadFactory factory;

        public LimitedThreadFactory(ThreadFactory threadFactory) {
            this.factory = threadFactory;
        }

        public Thread newThread(Runnable arg0) {
            if ((++(threadCount)) > (AMQ1917Test.NUM_THREADS)) {
                errorLatch.countDown();
                TestCase.fail("too many threads requested");
            }
            return factory.newThread(arg0);
        }
    }
}

