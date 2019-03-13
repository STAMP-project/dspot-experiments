/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.store.jdbc;


import Session.SESSION_TRANSACTED;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.RegionBroker;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.util.DefaultIOExceptionHandler;
import org.apache.log4j.Appender;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.log4j.Logger.getLogger;


public class JDBCConcurrentDLQTest {
    private static final Logger LOG = LoggerFactory.getLogger(JDBCConcurrentDLQTest.class);

    BrokerService broker;

    JDBCPersistenceAdapter jdbcPersistenceAdapter;

    Appender appender = null;

    final AtomicBoolean gotError = new AtomicBoolean(false);

    @Test
    public void testConcurrentDlqOk() throws Exception {
        final Destination dest = new ActiveMQQueue("DD");
        final ActiveMQConnectionFactory amq = new ActiveMQConnectionFactory(broker.getTransportConnectorByScheme("tcp").getPublishableConnectString());
        amq.setWatchTopicAdvisories(false);
        broker.setIoExceptionHandler(new DefaultIOExceptionHandler() {
            @Override
            public void handle(IOException exception) {
                JDBCConcurrentDLQTest.LOG.error("handle IOException from store", exception);
                gotError.set(true);
            }
        });
        getLogger(RegionBroker.class).addAppender(appender);
        getLogger(JDBCPersistenceAdapter.class).addAppender(appender);
        final int numMessages = 100;
        final AtomicInteger consumed = new AtomicInteger(numMessages);
        produceMessages(amq, dest, numMessages);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 50; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Connection connection = null;
                    Session session = null;
                    MessageConsumer consumer = null;
                    try {
                        connection = amq.createConnection();
                        connection.setExceptionListener(new ExceptionListener() {
                            public void onException(JMSException e) {
                                e.printStackTrace();
                            }
                        });
                        // set custom redelivery policy with 0 retries to force move to DLQ
                        RedeliveryPolicy queuePolicy = new RedeliveryPolicy();
                        queuePolicy.setMaximumRedeliveries(0);
                        ((ActiveMQConnection) (connection)).setRedeliveryPolicy(queuePolicy);
                        connection.start();
                        session = connection.createSession(true, SESSION_TRANSACTED);
                        consumer = session.createConsumer(dest);
                        while (((consumed.get()) > 0) && (!(gotError.get()))) {
                            Message message = consumer.receive(4000);
                            if (message != null) {
                                consumed.decrementAndGet();
                                session.rollback();
                            }
                        } 
                    } catch (Exception e) {
                        JDBCConcurrentDLQTest.LOG.error("Error on consumption", e);
                        gotError.set(true);
                    } finally {
                        try {
                            if (connection != null) {
                                connection.close();
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            });
        }
        executorService.shutdown();
        boolean allComplete = executorService.awaitTermination(60, TimeUnit.SECONDS);
        executorService.shutdownNow();
        JDBCConcurrentDLQTest.LOG.info(("Total messages: " + (broker.getAdminView().getTotalMessageCount())));
        JDBCConcurrentDLQTest.LOG.info(("Total enqueues: " + (broker.getAdminView().getTotalEnqueueCount())));
        JDBCConcurrentDLQTest.LOG.info(("Total deueues: " + (broker.getAdminView().getTotalDequeueCount())));
        Assert.assertTrue(allComplete);
        Assert.assertEquals("all consumed", 0L, consumed.get());
        Assert.assertEquals("all messages get to the dlq", (numMessages * 2), broker.getAdminView().getTotalEnqueueCount());
        Assert.assertEquals("all messages acked", numMessages, broker.getAdminView().getTotalDequeueCount());
        Assert.assertFalse("no error", gotError.get());
    }
}

