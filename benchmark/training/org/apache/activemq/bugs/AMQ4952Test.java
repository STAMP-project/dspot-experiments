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


import Session.CLIENT_ACKNOWLEDGE;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerFilter;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.Message;
import org.apache.activemq.util.Wait;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test creates a broker network with two brokers - producerBroker (with a
 * message producer attached) and consumerBroker (with consumer attached)
 * <p/>
 * Simulates network duplicate message by stopping and restarting the
 * consumerBroker after message (with message ID ending in 120) is persisted to
 * consumerBrokerstore BUT BEFORE ack sent to the producerBroker over the
 * network connection. When the network connection is reestablished the
 * producerBroker resends message (with messageID ending in 120).
 * <p/>
 * Expectation:
 * <p/>
 * With the following policy entries set, would expect the duplicate message to
 * be read from the store and dispatched to the consumer - where the duplicate
 * could be detected by consumer.
 * <p/>
 * PolicyEntry policy = new PolicyEntry(); policy.setQueue(">");
 * policy.setEnableAudit(false); policy.setUseCache(false);
 * policy.setExpireMessagesPeriod(0);
 * <p/>
 * <p/>
 * Note 1: Network needs to use replaywhenNoConsumers so enabling the
 * networkAudit to avoid this scenario is not feasible.
 * <p/>
 * NOTE 2: Added a custom plugin to the consumerBroker so that the
 * consumerBroker shutdown will occur after a message has been persisted to
 * consumerBroker store but before an ACK is sent back to ProducerBroker. This
 * is just a hack to ensure producerBroker will resend the message after
 * shutdown.
 */
@RunWith(Parameterized.class)
public class AMQ4952Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ4952Test.class);

    protected static final int MESSAGE_COUNT = 1;

    protected BrokerService consumerBroker;

    protected BrokerService producerBroker;

    protected ActiveMQQueue QUEUE_NAME = new ActiveMQQueue("duptest.store");

    private CountDownLatch stopConsumerBroker;

    private CountDownLatch consumerBrokerRestarted;

    private CountDownLatch consumerRestartedAndMessageForwarded;

    private EmbeddedDataSource localDataSource;

    @Parameterized.Parameter(0)
    public boolean enableCursorAudit;

    @Test
    public void testConsumerBrokerRestart() throws Exception {
        Callable consumeMessageTask = new Callable() {
            @Override
            public Object call() throws Exception {
                int receivedMessageCount = 0;
                ActiveMQConnectionFactory consumerFactory = new ActiveMQConnectionFactory("failover:(tcp://localhost:2006)?randomize=false&backup=false");
                Connection consumerConnection = consumerFactory.createConnection();
                try {
                    consumerConnection.setClientID("consumer");
                    consumerConnection.start();
                    Session consumerSession = consumerConnection.createSession(false, CLIENT_ACKNOWLEDGE);
                    MessageConsumer messageConsumer = consumerSession.createConsumer(QUEUE_NAME);
                    while (true) {
                        TextMessage textMsg = ((TextMessage) (messageConsumer.receive(1000)));
                        if (textMsg == null) {
                            textMsg = ((TextMessage) (messageConsumer.receive(4000)));
                        }
                        if (textMsg == null) {
                            return receivedMessageCount;
                        }
                        receivedMessageCount++;
                        AMQ4952Test.LOG.info("*** receivedMessageCount {} message has MessageID {} ", receivedMessageCount, textMsg.getJMSMessageID());
                        // on first delivery ensure the message is pending an
                        // ack when it is resent from the producer broker
                        if ((textMsg.getJMSMessageID().endsWith("1")) && (receivedMessageCount == 1)) {
                            AMQ4952Test.LOG.info("Waiting for restart...");
                            consumerRestartedAndMessageForwarded.await(90, TimeUnit.SECONDS);
                        }
                        textMsg.acknowledge();
                    } 
                } finally {
                    consumerConnection.close();
                }
            }
        };
        Runnable consumerBrokerResetTask = new Runnable() {
            @Override
            public void run() {
                try {
                    // wait for signal
                    stopConsumerBroker.await();
                    AMQ4952Test.LOG.info("********* STOPPING CONSUMER BROKER");
                    consumerBroker.stop();
                    consumerBroker.waitUntilStopped();
                    AMQ4952Test.LOG.info("***** STARTING CONSUMER BROKER");
                    // do not delete messages on startup
                    consumerBroker = createConsumerBroker(false);
                    AMQ4952Test.LOG.info("***** CONSUMER BROKER STARTED!!");
                    consumerBrokerRestarted.countDown();
                    Assert.assertTrue("message forwarded on time", Wait.waitFor(new Wait.Condition() {
                        @Override
                        public boolean isSatisified() throws Exception {
                            AMQ4952Test.LOG.info(("ProducerBroker totalMessageCount: " + (producerBroker.getAdminView().getTotalMessageCount())));
                            return (producerBroker.getAdminView().getTotalMessageCount()) == 0;
                        }
                    }));
                    consumerRestartedAndMessageForwarded.countDown();
                } catch (Exception e) {
                    AMQ4952Test.LOG.error("Exception when stopping/starting the consumerBroker ", e);
                }
            }
        };
        ExecutorService executor = Executors.newFixedThreadPool(2);
        // start consumerBroker start/stop task
        executor.execute(consumerBrokerResetTask);
        // start consuming messages
        Future<Integer> numberOfConsumedMessage = executor.submit(consumeMessageTask);
        produceMessages();
        // Wait for consumer to finish
        int totalMessagesConsumed = numberOfConsumedMessage.get();
        StringBuffer contents = new StringBuffer();
        boolean messageInStore = isMessageInJDBCStore(localDataSource, contents);
        AMQ4952Test.LOG.debug(("****number of messages received " + totalMessagesConsumed));
        Assert.assertEquals("number of messages received", 2, totalMessagesConsumed);
        Assert.assertEquals("messages left in store", true, messageInStore);
        Assert.assertTrue(("message is in dlq: " + (contents.toString())), contents.toString().contains("DLQ"));
    }

    /**
     * plugin used to ensure consumerbroker is restared before the network
     * message from producerBroker is acked
     */
    class MyTestPlugin implements BrokerPlugin {
        @Override
        public Broker installPlugin(Broker broker) throws Exception {
            return new AMQ4952Test.MyTestBroker(broker);
        }
    }

    class MyTestBroker extends BrokerFilter {
        public MyTestBroker(Broker next) {
            super(next);
        }

        @Override
        public void send(ProducerBrokerExchange producerExchange, org.apache.activemq.command.Message messageSend) throws Exception {
            super.send(producerExchange, messageSend);
            AMQ4952Test.LOG.error(("Stopping broker on send:  " + (messageSend.getMessageId().getProducerSequenceId())));
            stopConsumerBroker.countDown();
            producerExchange.getConnectionContext().setDontSendReponse(true);
        }
    }
}

