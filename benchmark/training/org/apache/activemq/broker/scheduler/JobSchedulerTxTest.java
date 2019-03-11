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
package org.apache.activemq.broker.scheduler;


import ScheduledMessage.AMQ_SCHEDULED_DELAY;
import Session.AUTO_ACKNOWLEDGE;
import Session.SESSION_TRANSACTED;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.junit.Assert;
import org.junit.Test;


public class JobSchedulerTxTest extends JobSchedulerTestSupport {
    @Test
    public void testTxSendWithRollback() throws Exception {
        final int COUNT = 10;
        Connection connection = createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(destination);
        final CountDownLatch latch = new CountDownLatch(COUNT);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                latch.countDown();
            }
        });
        connection.start();
        long time = 5000;
        Session producerSession = connection.createSession(true, SESSION_TRANSACTED);
        MessageProducer producer = producerSession.createProducer(destination);
        for (int i = 0; i < COUNT; ++i) {
            TextMessage message = session.createTextMessage("test msg");
            message.setLongProperty(AMQ_SCHEDULED_DELAY, time);
            producer.send(message);
        }
        producer.close();
        producerSession.rollback();
        // make sure the message isn't delivered early
        Thread.sleep(2000);
        Assert.assertEquals(COUNT, latch.getCount());
        latch.await(5, TimeUnit.SECONDS);
        Assert.assertEquals(COUNT, latch.getCount());
    }

    @Test
    public void testTxSendWithCommit() throws Exception {
        final int COUNT = 10;
        Connection connection = createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(destination);
        final CountDownLatch latch = new CountDownLatch(COUNT);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                latch.countDown();
            }
        });
        connection.start();
        long time = 5000;
        Session producerSession = connection.createSession(true, SESSION_TRANSACTED);
        MessageProducer producer = producerSession.createProducer(destination);
        for (int i = 0; i < COUNT; ++i) {
            TextMessage message = session.createTextMessage("test msg");
            message.setLongProperty(AMQ_SCHEDULED_DELAY, time);
            producer.send(message);
        }
        producer.close();
        producerSession.commit();
        // make sure the message isn't delivered early
        Thread.sleep(2000);
        Assert.assertEquals(COUNT, latch.getCount());
        latch.await(5, TimeUnit.SECONDS);
        Assert.assertEquals(0, latch.getCount());
    }
}

