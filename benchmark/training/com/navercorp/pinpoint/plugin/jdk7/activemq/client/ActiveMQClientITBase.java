/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.jdk7.activemq.client;


import ServiceType.ASYNC;
import ServiceType.INTERNAL_METHOD;
import ServiceType.STAND_ALONE;
import com.navercorp.pinpoint.bootstrap.plugin.test.Expectations;
import com.navercorp.pinpoint.bootstrap.plugin.test.ExpectedTrace;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifier;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifierHolder;
import com.navercorp.pinpoint.plugin.jdk7.activemq.client.util.ActiveMQClientITHelper;
import com.navercorp.pinpoint.plugin.jdk7.activemq.client.util.MessageConsumerBuilder;
import com.navercorp.pinpoint.plugin.jdk7.activemq.client.util.MessageProducerBuilder;
import com.navercorp.test.pinpoint.plugin.activemq.MessagePrinter;
import com.navercorp.test.pinpoint.plugin.activemq.MessageReceiveHandler;
import com.navercorp.test.pinpoint.plugin.activemq.MessageReceiver;
import com.navercorp.test.pinpoint.plugin.activemq.PollingMessageReceiver;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Test;


/**
 *
 *
 * @author HyunGil Jeong
 */
public abstract class ActiveMQClientITBase {
    public static final String ACTIVEMQ_CLIENT = "ACTIVEMQ_CLIENT";

    public static final String ACTIVEMQ_CLIENT_INTERNAL = "ACTIVEMQ_CLIENT_INTERNAL";

    @Test
    public void testQueuePull() throws Exception {
        // Given
        final String testQueueName = "TestPullQueue";
        final ActiveMQQueue testQueue = new ActiveMQQueue(testQueueName);
        final String testMessage = "Hello World for Queue!";
        final CountDownLatch consumerLatch = new CountDownLatch(1);
        final AtomicReference<Exception> exception = new AtomicReference<Exception>();
        // create producer
        final ActiveMQSession producerSession = ActiveMQClientITHelper.createSession(getProducerBrokerName(), getProducerBrokerUrl());
        final MessageProducer producer = new MessageProducerBuilder(producerSession, testQueue).waitTillStarted().build();
        final TextMessage expectedTextMessage = producerSession.createTextMessage(testMessage);
        // create and start message receiver
        final ActiveMQSession consumerSession = ActiveMQClientITHelper.createSession(getConsumerBrokerName(), getConsumerBrokerUrl());
        final MessageConsumer consumer = new MessageConsumerBuilder(consumerSession, testQueue).waitTillStarted().build();
        final MessageReceiver messageReceiver = new MessageReceiver(consumer);
        final Thread consumerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    messageReceiver.receiveMessage(1000L);
                } catch (Exception e) {
                    exception.set(e);
                } finally {
                    consumerLatch.countDown();
                }
            }
        });
        // When
        producer.send(expectedTextMessage);
        consumerThread.start();
        consumerLatch.await(1L, TimeUnit.SECONDS);
        // Then
        assertNoConsumerError(exception.get());
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();
        // Wait till all traces are recorded (consumer traces are recorded from another thread)
        verifier.awaitTraceCount(7, 100, 5000);
        verifier.verifyTraceCount(7);
        verifyProducerSendEvent(verifier, testQueue, producerSession);
        verifyConsumerConsumeEvent(verifier, testQueue, consumerSession);
        // Separate transaction for the consumer's request to receive the message
        verifier.verifyTrace(Expectations.root(STAND_ALONE.getName(), "Entry Point Process", null, null, null, null));
        Method receiveMessageMethod = MessageReceiver.class.getDeclaredMethod("receiveMessage", long.class);
        verifier.verifyTrace(Expectations.event(INTERNAL_METHOD.getName(), receiveMessageMethod));
        Method receiveMethod = ActiveMQMessageConsumer.class.getDeclaredMethod("receive", long.class);
        verifier.verifyTrace(Expectations.event(ActiveMQClientITBase.ACTIVEMQ_CLIENT_INTERNAL, receiveMethod, Expectations.annotation("activemq.message", getMessageAsString(expectedTextMessage))));
        Method printMessageMethod = MessagePrinter.class.getDeclaredMethod("printMessage", Message.class);
        verifier.verifyTrace(Expectations.event(INTERNAL_METHOD.getName(), printMessageMethod));
        verifier.verifyTraceCount(0);
    }

    @Test
    public void testQueuePush() throws Exception {
        // Given
        final String testQueueName = "TestPushQueue";
        final ActiveMQQueue testQueue = new ActiveMQQueue(testQueueName);
        final String testMessage = "Hello World for Queue!";
        final MessagePrinter messagePrinter = new MessagePrinter();
        final CountDownLatch consumerLatch = new CountDownLatch(1);
        final AtomicReference<Exception> exception = new AtomicReference<Exception>();
        // create producer
        final ActiveMQSession producerSession = ActiveMQClientITHelper.createSession(getProducerBrokerName(), getProducerBrokerUrl());
        final MessageProducer producer = new MessageProducerBuilder(producerSession, testQueue).waitTillStarted().build();
        final TextMessage expectedTextMessage = producerSession.createTextMessage(testMessage);
        // create consumer
        final ActiveMQSession consumerSession = ActiveMQClientITHelper.createSession(getConsumerBrokerName(), getConsumerBrokerUrl());
        new MessageConsumerBuilder(consumerSession, testQueue).waitTillStarted().withMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    messagePrinter.printMessage(message);
                } catch (Exception e) {
                    exception.set(e);
                } finally {
                    consumerLatch.countDown();
                }
            }
        }).build();
        // When
        producer.send(expectedTextMessage);
        consumerLatch.await(1L, TimeUnit.SECONDS);
        // Then
        assertNoConsumerError(exception.get());
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();
        // Wait till all traces are recorded (consumer traces are recorded from another thread)
        verifier.awaitTraceCount(4, 100, 5000);
        verifier.verifyTraceCount(4);
        verifyProducerSendEvent(verifier, testQueue, producerSession);
        verifyConsumerConsumeEvent(verifier, testQueue, consumerSession);
        Method printMessageMethod = MessagePrinter.class.getDeclaredMethod("printMessage", Message.class);
        verifier.verifyTrace(Expectations.event(INTERNAL_METHOD.getName(), printMessageMethod));
        verifier.verifyTraceCount(0);
    }

    @Test
    public void testQueuePoll() throws Exception {
        // Given
        final String testQueueName = "TestPollQueue";
        final ActiveMQQueue testQueue = new ActiveMQQueue(testQueueName);
        final String testMessage = "Hello World for Queue!";
        // create producer
        final ActiveMQSession producerSession = ActiveMQClientITHelper.createSession(getProducerBrokerName(), getProducerBrokerUrl());
        final MessageProducer producer = new MessageProducerBuilder(producerSession, testQueue).waitTillStarted().build();
        final TextMessage expectedTextMessage = producerSession.createTextMessage(testMessage);
        // create consumer
        final ActiveMQSession consumerSession = ActiveMQClientITHelper.createSession(getConsumerBrokerName(), getConsumerBrokerUrl());
        final MessageConsumer consumer = new MessageConsumerBuilder(consumerSession, testQueue).waitTillStarted().build();
        final MessageReceiveHandler messageReceiveHandler = new MessageReceiveHandler();
        final PollingMessageReceiver pollingMessageReceiver = new PollingMessageReceiver(consumer, messageReceiveHandler);
        // When
        pollingMessageReceiver.start();
        producer.send(expectedTextMessage);
        messageReceiveHandler.await(5000L);
        pollingMessageReceiver.stop();
        // Then
        assertNoConsumerError(pollingMessageReceiver.getException());
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();
        // Wait till all traces are recorded (consumer traces are recorded from another thread)
        verifier.awaitTraceCount(6, 100, 5000);
        verifier.verifyTraceCount(6);
        verifyProducerSendEvent(verifier, testQueue, producerSession);
        verifyConsumerConsumeEvent(verifier, testQueue, consumerSession);
        verifier.verifyTrace(Expectations.event(ASYNC.getName(), "Asynchronous Invocation"));
        Method handleMessageMethod = MessageReceiveHandler.class.getDeclaredMethod("handleMessage", Message.class);
        verifier.verifyTrace(Expectations.event(INTERNAL_METHOD.getName(), handleMessageMethod));
        Method printMessageMethod = MessagePrinter.class.getDeclaredMethod("printMessage", Message.class);
        verifier.verifyTrace(Expectations.event(INTERNAL_METHOD.getName(), printMessageMethod));
        verifier.verifyTraceCount(0);
    }

    @Test
    public void testTopicPull() throws Exception {
        // Given
        final String testTopicName = "TestPullTopic";
        final ActiveMQTopic testTopic = new ActiveMQTopic(testTopicName);
        final String testMessage = "Hello World for Topic!";
        final CountDownLatch consumerLatch = new CountDownLatch(2);
        final AtomicReference<Exception> exception = new AtomicReference<Exception>();
        // create producer
        final ActiveMQSession producerSession = ActiveMQClientITHelper.createSession(getProducerBrokerName(), getProducerBrokerUrl());
        final MessageProducer producer = new MessageProducerBuilder(producerSession, testTopic).waitTillStarted().build();
        final TextMessage expectedTextMessage = producerSession.createTextMessage(testMessage);
        // create 2 consumers
        final ActiveMQSession consumer1Session = ActiveMQClientITHelper.createSession(getConsumerBrokerName(), getConsumerBrokerUrl());
        final MessageConsumer consumer1 = new MessageConsumerBuilder(consumer1Session, testTopic).waitTillStarted().build();
        final MessageReceiver messageReceiver1 = new MessageReceiver(consumer1);
        final Thread consumer1Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    messageReceiver1.receiveMessage(1000L);
                } catch (Exception e) {
                    exception.set(e);
                } finally {
                    consumerLatch.countDown();
                }
            }
        });
        final ActiveMQSession consumer2Session = ActiveMQClientITHelper.createSession(getConsumerBrokerName(), getConsumerBrokerUrl());
        final MessageConsumer consumer2 = new MessageConsumerBuilder(consumer2Session, testTopic).waitTillStarted().build();
        final MessageReceiver messageReceiver2 = new MessageReceiver(consumer2);
        final Thread consumer2Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    messageReceiver2.receiveMessage(1000L);
                } catch (Exception e) {
                    exception.set(e);
                } finally {
                    consumerLatch.countDown();
                }
            }
        });
        // When
        producer.send(expectedTextMessage);
        consumer1Thread.start();
        consumer2Thread.start();
        consumerLatch.await(1L, TimeUnit.SECONDS);
        // Then
        assertNoConsumerError(exception.get());
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();
        // Wait till all traces are recorded (consumer traces are recorded from another thread)
        verifier.awaitTraceCount(13, 100, 5000);
        verifier.verifyTraceCount(13);
        verifyProducerSendEvent(verifier, testTopic, producerSession);
        verifyConsumerConsumeEvent(verifier, testTopic, consumer1Session);
        verifyConsumerConsumeEvent(verifier, testTopic, consumer2Session);
        // Separate transaction for the consumer's request to receive the message
        Method receiveMessageMethod = MessageReceiver.class.getDeclaredMethod("receiveMessage", long.class);
        Method receiveMethod = ActiveMQMessageConsumer.class.getDeclaredMethod("receive", long.class);
        Method printMessageMethod = MessagePrinter.class.getDeclaredMethod("printMessage", Message.class);
        for (int i = 0; i < 2; ++i) {
            verifier.verifyDiscreteTrace(Expectations.root(STAND_ALONE.getName(), "Entry Point Process", null, null, null, null), Expectations.event(INTERNAL_METHOD.getName(), receiveMessageMethod), Expectations.event(ActiveMQClientITBase.ACTIVEMQ_CLIENT_INTERNAL, receiveMethod, Expectations.annotation("activemq.message", getMessageAsString(expectedTextMessage))), Expectations.event(INTERNAL_METHOD.getName(), printMessageMethod));
        }
        verifier.verifyTraceCount(0);
    }

    @Test
    public void testTopicPush() throws Exception {
        // Given
        final String testTopicName = "TestPushTopic";
        final ActiveMQTopic testTopic = new ActiveMQTopic(testTopicName);
        final String testMessage = "Hello World for Topic!";
        final MessagePrinter messagePrinter = new MessagePrinter();
        final CountDownLatch consumerLatch = new CountDownLatch(2);
        final AtomicReference<Exception> exception = new AtomicReference<Exception>();
        // create producer
        final ActiveMQSession producerSession = ActiveMQClientITHelper.createSession(getProducerBrokerName(), getProducerBrokerUrl());
        final MessageProducer producer = new MessageProducerBuilder(producerSession, testTopic).waitTillStarted().build();
        final TextMessage expectedTextMessage = producerSession.createTextMessage(testMessage);
        // create 2 consumers
        final MessageListener messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    messagePrinter.printMessage(message);
                } catch (Exception e) {
                    exception.set(e);
                } finally {
                    consumerLatch.countDown();
                }
            }
        };
        final ActiveMQSession consumer1Session = ActiveMQClientITHelper.createSession(getConsumerBrokerName(), getConsumerBrokerUrl());
        new MessageConsumerBuilder(consumer1Session, testTopic).withMessageListener(messageListener).waitTillStarted().build();
        ActiveMQSession consumer2Session = ActiveMQClientITHelper.createSession(getConsumerBrokerName(), getConsumerBrokerUrl());
        new MessageConsumerBuilder(consumer2Session, testTopic).withMessageListener(messageListener).waitTillStarted().build();
        // When
        producer.send(expectedTextMessage);
        consumerLatch.await(1L, TimeUnit.SECONDS);
        // Then
        assertNoConsumerError(exception.get());
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();
        // Wait till all traces are recorded (consumer traces are recorded from another thread)
        verifier.awaitTraceCount(7, 100, 5000);
        verifier.verifyTraceCount(7);
        verifyProducerSendEvent(verifier, testTopic, producerSession);
        verifyConsumerConsumeEvent(verifier, testTopic, consumer1Session);
        verifyConsumerConsumeEvent(verifier, testTopic, consumer2Session);
        Method printMessageMethod = MessagePrinter.class.getDeclaredMethod("printMessage", Message.class);
        for (int i = 0; i < 2; ++i) {
            verifier.verifyTrace(Expectations.event(INTERNAL_METHOD.getName(), printMessageMethod));
        }
        verifier.verifyTraceCount(0);
    }

    @Test
    public void testTopicPoll() throws Exception {
        // Given
        final String testTopicName = "TestPollTopic";
        final ActiveMQTopic testTopic = new ActiveMQTopic(testTopicName);
        final String testMessage = "Hello World for Topic!";
        // create producer
        final ActiveMQSession producerSession = ActiveMQClientITHelper.createSession(getProducerBrokerName(), getProducerBrokerUrl());
        final MessageProducer producer = new MessageProducerBuilder(producerSession, testTopic).waitTillStarted().build();
        final TextMessage expectedTextMessage = producerSession.createTextMessage(testMessage);
        // create 2 consumers
        final ActiveMQSession consumer1Session = ActiveMQClientITHelper.createSession(getConsumerBrokerName(), getConsumerBrokerUrl());
        final MessageConsumer consumer1 = new MessageConsumerBuilder(consumer1Session, testTopic).waitTillStarted().build();
        final MessageReceiveHandler messageReceiveHandler1 = new MessageReceiveHandler();
        final PollingMessageReceiver pollingMessageReceiver1 = new PollingMessageReceiver(consumer1, messageReceiveHandler1);
        final ActiveMQSession consumer2Session = ActiveMQClientITHelper.createSession(getConsumerBrokerName(), getConsumerBrokerUrl());
        final MessageConsumer consumer2 = new MessageConsumerBuilder(consumer2Session, testTopic).waitTillStarted().build();
        final MessageReceiveHandler messageReceiveHandler2 = new MessageReceiveHandler();
        final PollingMessageReceiver pollingMessageReceiver2 = new PollingMessageReceiver(consumer2, messageReceiveHandler2);
        // When
        pollingMessageReceiver1.start();
        pollingMessageReceiver2.start();
        producer.send(expectedTextMessage);
        messageReceiveHandler1.await(5000L);
        messageReceiveHandler2.await(5000L);
        pollingMessageReceiver1.stop();
        pollingMessageReceiver2.stop();
        // Then
        assertNoConsumerError(pollingMessageReceiver1.getException());
        assertNoConsumerError(pollingMessageReceiver2.getException());
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();
        // Wait till all traces are recorded (consumer traces are recorded from another thread)
        verifier.awaitTraceCount(11, 100, 5000);
        verifier.verifyTraceCount(11);
        verifyProducerSendEvent(verifier, testTopic, producerSession);
        verifyConsumerConsumeEvent(verifier, testTopic, consumer1Session);
        verifyConsumerConsumeEvent(verifier, testTopic, consumer2Session);
        ExpectedTrace asyncTrace = Expectations.event(ASYNC.getName(), "Asynchronous Invocation");
        Method handleMessageMethod = MessageReceiveHandler.class.getDeclaredMethod("handleMessage", Message.class);
        ExpectedTrace handleMessageTrace = Expectations.event(INTERNAL_METHOD.getName(), handleMessageMethod);
        Method printMessageMethod = MessagePrinter.class.getDeclaredMethod("printMessage", Message.class);
        ExpectedTrace printMessageTrace = Expectations.event(INTERNAL_METHOD.getName(), printMessageMethod);
        for (int i = 0; i < 2; ++i) {
            verifier.verifyDiscreteTrace(asyncTrace, handleMessageTrace, printMessageTrace);
        }
        verifier.verifyTraceCount(0);
    }
}

