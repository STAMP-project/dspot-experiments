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


import ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE;
import DeliveryMode.PERSISTENT;
import Session.AUTO_ACKNOWLEDGE;
import Session.CLIENT_ACKNOWLEDGE;
import Session.DUPS_OK_ACKNOWLEDGE;
import Session.SESSION_TRANSACTED;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TopicSubscriptionZeroPrefetchTest {
    private static final Logger LOG = LoggerFactory.getLogger(TopicSubscriptionZeroPrefetchTest.class);

    @Rule
    public TestName name = new TestName();

    private Connection connection;

    private Session session;

    private ActiveMQTopic destination;

    private MessageProducer producer;

    private MessageConsumer consumer;

    private BrokerService brokerService;

    /* test non durable topic subscription with prefetch set to zero */
    @Test(timeout = 60000)
    public void testTopicConsumerPrefetchZero() throws Exception {
        ActiveMQTopic consumerDestination = new ActiveMQTopic(((getTopicName()) + "?consumer.retroactive=true&consumer.prefetchSize=0"));
        consumer = session.createConsumer(consumerDestination);
        // publish messages
        Message txtMessage = session.createTextMessage("M");
        producer.send(txtMessage);
        Message consumedMessage = consumer.receiveNoWait();
        Assert.assertNotNull("should have received a message the published message", consumedMessage);
    }

    @Test(timeout = 60000)
    public void testTopicConsumerPrefetchZeroClientAckLoopReceive() throws Exception {
        ActiveMQTopic consumerDestination = new ActiveMQTopic(((getTopicName()) + "?consumer.retroactive=true&consumer.prefetchSize=0"));
        Session consumerClientAckSession = connection.createSession(false, CLIENT_ACKNOWLEDGE);
        consumer = consumerClientAckSession.createConsumer(consumerDestination);
        final int count = 10;
        for (int i = 0; i < count; i++) {
            Message txtMessage = session.createTextMessage(("M:" + i));
            producer.send(txtMessage);
        }
        for (int i = 0; i < count; i++) {
            Message consumedMessage = consumer.receive();
            Assert.assertNotNull((("should have received message[" + i) + "]"), consumedMessage);
        }
    }

    @Test(timeout = 60000)
    public void testTopicConsumerPrefetchZeroClientAckLoopTimedReceive() throws Exception {
        ActiveMQTopic consumerDestination = new ActiveMQTopic(((getTopicName()) + "?consumer.retroactive=true&consumer.prefetchSize=0"));
        Session consumerClientAckSession = connection.createSession(false, CLIENT_ACKNOWLEDGE);
        consumer = consumerClientAckSession.createConsumer(consumerDestination);
        final int count = 10;
        for (int i = 0; i < count; i++) {
            Message txtMessage = session.createTextMessage(("M:" + i));
            producer.send(txtMessage);
        }
        for (int i = 0; i < count; i++) {
            Message consumedMessage = consumer.receive(2000);
            Assert.assertNotNull((("should have received message[" + i) + "]"), consumedMessage);
        }
    }

    @Test(timeout = 60000)
    public void testTopicConsumerPrefetchZeroClientAckLoopReceiveNoWait() throws Exception {
        ActiveMQTopic consumerDestination = new ActiveMQTopic(((getTopicName()) + "?consumer.retroactive=true&consumer.prefetchSize=0"));
        Session consumerClientAckSession = connection.createSession(false, CLIENT_ACKNOWLEDGE);
        consumer = consumerClientAckSession.createConsumer(consumerDestination);
        final int count = 10;
        for (int i = 0; i < count; i++) {
            Message txtMessage = session.createTextMessage(("M:" + i));
            producer.send(txtMessage);
        }
        for (int i = 0; i < count; i++) {
            Message consumedMessage = consumer.receiveNoWait();
            Assert.assertNotNull((("should have received message[" + i) + "]"), consumedMessage);
        }
    }

    @Test(timeout = 60000)
    public void testTopicConsumerPrefetchZeroConcurrentProduceConsumeAutoAck() throws Exception {
        doTestTopicConsumerPrefetchZeroConcurrentProduceConsume(AUTO_ACKNOWLEDGE);
    }

    @Test(timeout = 60000)
    public void testTopicConsumerPrefetchZeroConcurrentProduceConsumeClientAck() throws Exception {
        doTestTopicConsumerPrefetchZeroConcurrentProduceConsume(CLIENT_ACKNOWLEDGE);
    }

    @Test(timeout = 60000)
    public void testTopicConsumerPrefetchZeroConcurrentProduceConsumeDupsOk() throws Exception {
        doTestTopicConsumerPrefetchZeroConcurrentProduceConsume(DUPS_OK_ACKNOWLEDGE);
    }

    @Test(timeout = 60000)
    public void testTopicConsumerPrefetchZeroConcurrentProduceConsumeTransacted() throws Exception {
        doTestTopicConsumerPrefetchZeroConcurrentProduceConsume(SESSION_TRANSACTED);
    }

    @Test(timeout = 60000)
    public void testTopicConsumerPrefetchZeroConcurrentProduceConsumeTransactedComitInBatches() throws Exception {
        doTestTopicConsumerPrefetchZeroConcurrentProduceConsume(SESSION_TRANSACTED);
    }

    @Test(timeout = 60000)
    public void testTopicConsumerPrefetchZeroConcurrentProduceConsumeIndividual() throws Exception {
        doTestTopicConsumerPrefetchZeroConcurrentProduceConsume(INDIVIDUAL_ACKNOWLEDGE);
    }

    /* test durable topic subscription with prefetch zero */
    @Test(timeout = 60000)
    public void testDurableTopicConsumerPrefetchZero() throws Exception {
        ActiveMQTopic consumerDestination = new ActiveMQTopic(((getTopicName()) + "?consumer.prefetchSize=0"));
        consumer = session.createDurableSubscriber(consumerDestination, "mysub1");
        // publish messages
        Message txtMessage = session.createTextMessage("M");
        producer.send(txtMessage);
        Message consumedMessage = consumer.receive(100);
        Assert.assertNotNull("should have received a message the published message", consumedMessage);
    }

    @Test(timeout = 420000)
    public void testReceiveTimeoutRespectedWithExpiryProcessing() throws Exception {
        ActiveMQTopic consumerDestination = new ActiveMQTopic(((getTopicName()) + "?consumer.prefetchSize=0"));
        for (int i = 0; i < 500; i++) {
            consumer = session.createDurableSubscriber(consumerDestination, ("mysub-" + i));
            consumer.close();
        }
        for (int i = 0; i < 1000; i++) {
            producer.send(session.createTextMessage("RTR"), PERSISTENT, 0, 5000);
        }
        consumer = session.createDurableSubscriber(consumerDestination, "mysub3");
        for (int i = 0; i < 10; i++) {
            long timeStamp = System.currentTimeMillis();
            consumer.receive(1000);
            long duration = (System.currentTimeMillis()) - timeStamp;
            TopicSubscriptionZeroPrefetchTest.LOG.info(((("Duration: " + i) + " : ") + duration));
            Assert.assertTrue(("Delay about 500: " + i), (duration < 1500));
        }
    }
}

