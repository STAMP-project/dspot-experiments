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


import Session.AUTO_ACKNOWLEDGE;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.util.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ4485Test extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ4485Test.class);

    BrokerService broker;

    ActiveMQConnectionFactory factory;

    final int messageCount = 20;

    int memoryLimit = 40 * 1024;

    final ActiveMQQueue destination = new ActiveMQQueue(("QUEUE." + (this.getClass().getName())));

    final Vector<Throwable> exceptions = new Vector<Throwable>();

    final CountDownLatch slowSendResume = new CountDownLatch(1);

    public void testOutOfOrderTransactionCompletionOnMemoryLimit() throws Exception {
        Set<Integer> expected = new HashSet<Integer>();
        final Vector<Session> sessionVector = new Vector<Session>();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 1; i <= (messageCount); i++) {
            sessionVector.add(send(i, 1, true));
            expected.add(i);
        }
        // get parallel commit so that the sync writes are batched
        for (int i = 0; i < (messageCount); i++) {
            final int id = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        sessionVector.get(id).commit();
                    } catch (Exception fail) {
                        exceptions.add(fail);
                    }
                }
            });
        }
        final DestinationViewMBean queueViewMBean = ((DestinationViewMBean) (broker.getManagementContext().newProxyInstance(broker.getAdminView().getQueues()[0], DestinationViewMBean.class, false)));
        // not sure how many messages will get enqueued
        TimeUnit.SECONDS.sleep(3);
        if (false)
            TestCase.assertTrue((("all " + (messageCount)) + " on the q"), Wait.waitFor(new Wait.Condition() {
                @Override
                public boolean isSatisified() throws Exception {
                    AMQ4485Test.LOG.info(("enqueueCount: " + (queueViewMBean.getEnqueueCount())));
                    return (messageCount) == (queueViewMBean.getEnqueueCount());
                }
            }));

        AMQ4485Test.LOG.info("Big send to blow available destination usage before slow send resumes");
        send(((messageCount) + 1), (35 * 1024), true).commit();
        // consume and verify all received
        Connection cosumerConnection = factory.createConnection();
        cosumerConnection.start();
        MessageConsumer consumer = cosumerConnection.createSession(false, AUTO_ACKNOWLEDGE).createConsumer(destination);
        for (int i = 1; i <= ((messageCount) + 1); i++) {
            BytesMessage bytesMessage = ((BytesMessage) (consumer.receive(10000)));
            TestCase.assertNotNull(((("Got message: " + i) + ", ") + expected), bytesMessage);
            MessageId mqMessageId = getMessageId();
            AMQ4485Test.LOG.info(((((("got: " + expected) + ", ") + mqMessageId) + ", NUM=") + (getProperty("NUM"))));
            expected.remove(getProperty("NUM"));
        }
    }
}

