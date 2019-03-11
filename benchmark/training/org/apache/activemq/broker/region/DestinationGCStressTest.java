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
package org.apache.activemq.broker.region;


import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.util.DefaultTestAppender;
import org.apache.activemq.util.Wait;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.log4j.Logger.getLogger;


public class DestinationGCStressTest {
    protected static final Logger logger = LoggerFactory.getLogger(DestinationGCStressTest.class);

    private BrokerService brokerService;

    @Test(timeout = 60000)
    public void testClashWithPublishAndGC() throws Exception {
        org.apache.log4j.Logger log4jLogger = getLogger(RegionBroker.class);
        final AtomicBoolean failed = new AtomicBoolean(false);
        Appender appender = new DefaultTestAppender() {
            @Override
            public void doAppend(LoggingEvent event) {
                if ((event.getLevel().equals(Level.ERROR)) && (event.getMessage().toString().startsWith("Failed to remove inactive"))) {
                    DestinationGCStressTest.logger.info(("received unexpected log message: " + (event.getMessage())));
                    failed.set(true);
                }
            }
        };
        log4jLogger.addAppender(appender);
        try {
            final AtomicInteger max = new AtomicInteger(20000);
            final ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost?create=false");
            factory.setWatchTopicAdvisories(false);
            Connection connection = factory.createConnection();
            connection.start();
            Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
            final MessageConsumer messageConsumer = session.createConsumer(new ActiveMQTopic(">"));
            ExecutorService executorService = Executors.newCachedThreadPool();
            for (int i = 0; i < 1; i++) {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Connection c = factory.createConnection();
                            c.start();
                            Session s = c.createSession(false, AUTO_ACKNOWLEDGE);
                            MessageProducer producer = s.createProducer(null);
                            Message message = s.createTextMessage();
                            int j;
                            while ((j = max.decrementAndGet()) > 0) {
                                producer.send(new ActiveMQTopic(("A." + j)), message);
                            } 
                        } catch (Exception ignored) {
                            ignored.printStackTrace();
                        }
                    }
                });
            }
            executorService.shutdown();
            executorService.awaitTermination(60, TimeUnit.SECONDS);
            DestinationGCStressTest.logger.info("Done");
            connection.close();
        } finally {
            log4jLogger.removeAppender(appender);
        }
        Assert.assertFalse("failed on unexpected log event", failed.get());
    }

    @Test(timeout = 60000)
    public void testAddRemoveWildcardWithGc() throws Exception {
        org.apache.log4j.Logger log4jLogger = getLogger(RegionBroker.class);
        final AtomicBoolean failed = new AtomicBoolean(false);
        Appender appender = new DefaultTestAppender() {
            @Override
            public void doAppend(LoggingEvent event) {
                if ((event.getLevel().equals(Level.ERROR)) && (event.getMessage().toString().startsWith("Failed to remove inactive"))) {
                    DestinationGCStressTest.logger.info(("received unexpected log message: " + (event.getMessage())));
                    failed.set(true);
                }
            }
        };
        log4jLogger.addAppender(appender);
        try {
            final AtomicInteger max = new AtomicInteger(10000);
            final ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost?create=false");
            factory.setWatchTopicAdvisories(false);
            Connection connection = factory.createConnection();
            connection.start();
            final Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
            ExecutorService executorService = Executors.newCachedThreadPool();
            for (int i = 0; i < 1; i++) {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Connection c = factory.createConnection();
                            c.start();
                            Session s = c.createSession(false, AUTO_ACKNOWLEDGE);
                            MessageProducer producer = s.createProducer(null);
                            Message message = s.createTextMessage();
                            int j;
                            while ((j = max.decrementAndGet()) > 0) {
                                producer.send(new ActiveMQTopic(("A." + j)), message);
                            } 
                        } catch (Exception ignored) {
                            ignored.printStackTrace();
                        }
                    }
                });
            }
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 1000; i++) {
                        try {
                            MessageConsumer messageConsumer = session.createConsumer(new ActiveMQTopic(">"));
                            messageConsumer.close();
                        } catch (Exception ignored) {
                        }
                    }
                }
            });
            executorService.shutdown();
            executorService.awaitTermination(60, TimeUnit.SECONDS);
            DestinationGCStressTest.logger.info("Done");
            Wait.waitFor(new Wait.Condition() {
                @Override
                public boolean isSatisified() throws Exception {
                    int len = getTopicRegion().getDestinationMap().size();
                    DestinationGCStressTest.logger.info(("Num topics: " + len));
                    return len == 0;
                }
            });
            connection.close();
        } finally {
            log4jLogger.removeAppender(appender);
        }
        Assert.assertFalse("failed on unexpected log event", failed.get());
    }

    @Test(timeout = 60000)
    public void testAllDestsSeeSub() throws Exception {
        final AtomicInteger foundDestWithMissingSub = new AtomicInteger(0);
        final AtomicInteger max = new AtomicInteger(20000);
        final ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost?create=false");
        factory.setWatchTopicAdvisories(false);
        Connection connection = factory.createConnection();
        connection.start();
        final Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 1; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Connection c = factory.createConnection();
                        c.start();
                        Session s = c.createSession(false, AUTO_ACKNOWLEDGE);
                        MessageProducer producer = s.createProducer(null);
                        Message message = s.createTextMessage();
                        int j;
                        while ((j = max.decrementAndGet()) > 0) {
                            producer.send(new ActiveMQTopic(("A." + j)), message);
                        } 
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            });
        }
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    try {
                        MessageConsumer messageConsumer = session.createConsumer(new ActiveMQTopic(">"));
                        if (destMissingSub(foundDestWithMissingSub)) {
                            break;
                        }
                        messageConsumer.close();
                    } catch (Exception ignored) {
                    }
                }
            }
        });
        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.SECONDS);
        connection.close();
        Assert.assertEquals("no dests missing sub", 0, foundDestWithMissingSub.get());
    }
}

