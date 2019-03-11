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


import AdvisorySupport.MSG_PROPERTY_USAGE_COUNT;
import AdvisorySupport.MSG_PROPERTY_USAGE_NAME;
import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.Topic;
import org.apache.activemq.util.DefaultTestAppender;
import org.apache.activemq.util.Wait;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.log4j.Logger.getLogger;


public class TopicProducerFlowControlTest extends TestCase implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(TopicProducerFlowControlTest.class);

    private static final String brokerName = "testBroker";

    private static final String brokerUrl = "vm://" + (TopicProducerFlowControlTest.brokerName);

    protected static final int destinationMemLimit = 2097152;// 2MB


    private static final AtomicLong produced = new AtomicLong();

    private static final AtomicLong consumed = new AtomicLong();

    private static final int numMessagesToSend = 50000;

    private BrokerService broker;

    public void testTopicProducerFlowControl() throws Exception {
        // Create the connection factory
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(TopicProducerFlowControlTest.brokerUrl);
        connectionFactory.setAlwaysSyncSend(true);
        connectionFactory.setProducerWindowSize(1024);
        ActiveMQPrefetchPolicy prefetchPolicy = new ActiveMQPrefetchPolicy();
        prefetchPolicy.setAll(5000);
        connectionFactory.setPrefetchPolicy(prefetchPolicy);
        // Start the test destination listener
        Connection c = connectionFactory.createConnection();
        c.start();
        Session listenerSession = c.createSession(false, 1);
        Destination destination = createDestination(listenerSession);
        listenerSession.createConsumer(destination).setMessageListener(new TopicProducerFlowControlTest());
        final AtomicInteger blockedCounter = new AtomicInteger(0);
        listenerSession.createConsumer(new org.apache.activemq.command.ActiveMQTopic(((AdvisorySupport.FULL_TOPIC_PREFIX) + ">"))).setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    if (((blockedCounter.get()) % 100) == 0) {
                        TopicProducerFlowControlTest.LOG.info(((((("Got full advisory, usageName: " + (message.getStringProperty(MSG_PROPERTY_USAGE_NAME))) + ", usageCount: ") + (message.getLongProperty(MSG_PROPERTY_USAGE_COUNT))) + ", blockedCounter: ") + (blockedCounter.get())));
                    }
                    blockedCounter.incrementAndGet();
                } catch (Exception error) {
                    error.printStackTrace();
                    TopicProducerFlowControlTest.LOG.error("missing advisory property", error);
                }
            }
        });
        final AtomicInteger warnings = new AtomicInteger();
        Appender appender = new DefaultTestAppender() {
            @Override
            public void doAppend(LoggingEvent event) {
                if ((event.getLevel().equals(Level.WARN)) && (event.getMessage().toString().contains("Usage Manager memory limit reached"))) {
                    TopicProducerFlowControlTest.LOG.info(("received  log message: " + (event.getMessage())));
                    warnings.incrementAndGet();
                }
            }
        };
        org.apache.log4j.Logger log4jLogger = getLogger(Topic.class);
        log4jLogger.addAppender(appender);
        try {
            // Start producing the test messages
            final Session session = connectionFactory.createConnection().createSession(false, AUTO_ACKNOWLEDGE);
            final MessageProducer producer = session.createProducer(destination);
            Thread producingThread = new Thread("Producing Thread") {
                public void run() {
                    try {
                        for (long i = 0; i < (TopicProducerFlowControlTest.numMessagesToSend); i++) {
                            producer.send(session.createTextMessage("test"));
                            long count = TopicProducerFlowControlTest.produced.incrementAndGet();
                            if ((count % 10000) == 0) {
                                TopicProducerFlowControlTest.LOG.info((("Produced " + count) + " messages"));
                            }
                        }
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                            producer.close();
                            session.close();
                        } catch (Exception e) {
                        }
                    }
                }
            };
            producingThread.start();
            Wait.waitFor(new Wait.Condition() {
                public boolean isSatisified() throws Exception {
                    return (TopicProducerFlowControlTest.consumed.get()) == (TopicProducerFlowControlTest.numMessagesToSend);
                }
            }, ((5 * 60) * 1000));// give it plenty of time before failing

            TestCase.assertEquals("Didn't produce all messages", TopicProducerFlowControlTest.numMessagesToSend, TopicProducerFlowControlTest.produced.get());
            TestCase.assertEquals("Didn't consume all messages", TopicProducerFlowControlTest.numMessagesToSend, TopicProducerFlowControlTest.consumed.get());
            TestCase.assertTrue("Producer got blocked", Wait.waitFor(new Wait.Condition() {
                public boolean isSatisified() throws Exception {
                    return (blockedCounter.get()) > 0;
                }
            }, (5 * 1000)));
            TopicProducerFlowControlTest.LOG.info(((("BlockedCount: " + (blockedCounter.get())) + ", Warnings:") + (warnings.get())));
            TestCase.assertTrue("got a few warnings", ((warnings.get()) > 1));
            TestCase.assertTrue("warning limited", ((warnings.get()) < (blockedCounter.get())));
        } finally {
            log4jLogger.removeAppender(appender);
        }
    }
}

