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


import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.QueueView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/* demonstrates that with nio it does not make sense to block on the broker but thread pool
shold grow past initial corepoolsize of 10
 */
public class NIOAsyncSendWithPFCTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(NIOAsyncSendWithPFCTest.class);

    private static String TRANSPORT_URL = "nio://0.0.0.0:0";

    private static final String DESTINATION_ONE = "testQ1";

    private static final String DESTINATION_TWO = "testQ2";

    private static final int MESSAGES_TO_SEND = 100;

    private static int NUMBER_OF_PRODUCERS = 10;

    /**
     * Test creates 10 producer who send to a single destination using Async mode.
     * Producer flow control kicks in for that destination. When producer flow control is blocking sends
     * Test tries to create another JMS connection to the nio.
     */
    public void testAsyncSendPFCNewConnection() throws Exception {
        BrokerService broker = createBroker();
        broker.waitUntilStarted();
        ExecutorService executorService = Executors.newFixedThreadPool(NIOAsyncSendWithPFCTest.NUMBER_OF_PRODUCERS);
        QueueView queueView = getQueueView(broker, NIOAsyncSendWithPFCTest.DESTINATION_ONE);
        try {
            for (int i = 0; i < (NIOAsyncSendWithPFCTest.NUMBER_OF_PRODUCERS); i++) {
                executorService.submit(new NIOAsyncSendWithPFCTest.ProducerTask());
            }
            // wait till producer follow control kicks in
            waitForProducerFlowControl(broker, queueView);
            try {
                sendMessages(1, NIOAsyncSendWithPFCTest.DESTINATION_TWO, false);
            } catch (Exception ex) {
                NIOAsyncSendWithPFCTest.LOG.error("Ex on send  new connection", ex);
                TestCase.fail(("*** received the following exception when creating addition producer new connection:" + ex));
            }
        } finally {
            broker.stop();
            broker.waitUntilStopped();
        }
    }

    public void testAsyncSendPFCExistingConnection() throws Exception {
        BrokerService broker = createBroker();
        broker.waitUntilStarted();
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", ((NIOAsyncSendWithPFCTest.TRANSPORT_URL) + "?wireFormat.maxInactivityDuration=5000"));
        ActiveMQConnection exisitngConnection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        ExecutorService executorService = Executors.newFixedThreadPool(NIOAsyncSendWithPFCTest.NUMBER_OF_PRODUCERS);
        QueueView queueView = getQueueView(broker, NIOAsyncSendWithPFCTest.DESTINATION_ONE);
        try {
            for (int i = 0; i < (NIOAsyncSendWithPFCTest.NUMBER_OF_PRODUCERS); i++) {
                executorService.submit(new NIOAsyncSendWithPFCTest.ProducerTask());
            }
            // wait till producer follow control kicks in
            waitForProducerFlowControl(broker, queueView);
            TestCase.assertTrue("Producer view blocked", getProducerView(broker, NIOAsyncSendWithPFCTest.DESTINATION_ONE).isProducerBlocked());
            try {
                Session producerSession = exisitngConnection.createSession(false, AUTO_ACKNOWLEDGE);
            } catch (Exception ex) {
                NIOAsyncSendWithPFCTest.LOG.error("Ex on create session", ex);
                TestCase.fail(("*** received the following exception when creating producer session:" + ex));
            }
        } finally {
            broker.stop();
            broker.waitUntilStopped();
        }
    }

    public void testSyncSendPFCExistingConnection() throws Exception {
        BrokerService broker = createBroker();
        broker.waitUntilStarted();
        ExecutorService executorService = Executors.newFixedThreadPool(NIOAsyncSendWithPFCTest.NUMBER_OF_PRODUCERS);
        QueueView queueView = getQueueView(broker, NIOAsyncSendWithPFCTest.DESTINATION_ONE);
        try {
            for (int i = 0; i < (NIOAsyncSendWithPFCTest.NUMBER_OF_PRODUCERS); i++) {
                executorService.submit(new NIOAsyncSendWithPFCTest.ProducerTask(true));
            }
            // wait till producer follow control kicks in
            waitForProducerFlowControl(broker, queueView);
            TestCase.assertTrue("Producer view blocked", getProducerView(broker, NIOAsyncSendWithPFCTest.DESTINATION_ONE).isProducerBlocked());
        } finally {
            broker.stop();
            broker.waitUntilStopped();
        }
    }

    class ProducerTask implements Runnable {
        boolean sync = false;

        ProducerTask() {
            this(false);
        }

        ProducerTask(boolean sync) {
            this.sync = sync;
        }

        @Override
        public void run() {
            try {
                // send X messages
                sendMessages(NIOAsyncSendWithPFCTest.MESSAGES_TO_SEND, NIOAsyncSendWithPFCTest.DESTINATION_ONE, sync);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

