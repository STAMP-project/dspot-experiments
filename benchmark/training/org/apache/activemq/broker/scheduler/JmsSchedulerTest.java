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


import ScheduledMessage.AMQ_SCHEDULED_CRON;
import ScheduledMessage.AMQ_SCHEDULED_DELAY;
import ScheduledMessage.AMQ_SCHEDULED_PERIOD;
import ScheduledMessage.AMQ_SCHEDULED_REPEAT;
import Session.AUTO_ACKNOWLEDGE;
import Session.SESSION_TRANSACTED;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.util.ProducerThread;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JmsSchedulerTest extends JobSchedulerTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(JmsSchedulerTest.class);

    @Test
    public void testCron() throws Exception {
        final int COUNT = 10;
        final AtomicInteger count = new AtomicInteger();
        Connection connection = createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(destination);
        final CountDownLatch latch = new CountDownLatch(COUNT);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                count.incrementAndGet();
                latch.countDown();
                JmsSchedulerTest.LOG.info("Received scheduled message, waiting for {} more", latch.getCount());
            }
        });
        connection.start();
        MessageProducer producer = session.createProducer(destination);
        TextMessage message = session.createTextMessage("test msg");
        long time = 1000;
        message.setStringProperty(AMQ_SCHEDULED_CRON, "* * * * *");
        message.setLongProperty(AMQ_SCHEDULED_DELAY, time);
        message.setLongProperty(AMQ_SCHEDULED_PERIOD, 500);
        message.setIntProperty(AMQ_SCHEDULED_REPEAT, (COUNT - 1));
        producer.send(message);
        producer.close();
        Thread.sleep(500);
        SchedulerBroker sb = ((SchedulerBroker) (this.broker.getBroker().getAdaptor(SchedulerBroker.class)));
        JobScheduler js = sb.getJobScheduler();
        List<Job> list = js.getAllJobs();
        Assert.assertEquals(1, list.size());
        latch.await(240, TimeUnit.SECONDS);
        Assert.assertEquals(COUNT, count.get());
        connection.close();
    }

    @Test
    public void testSchedule() throws Exception {
        final int COUNT = 1;
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
        MessageProducer producer = session.createProducer(destination);
        TextMessage message = session.createTextMessage("test msg");
        message.setLongProperty(AMQ_SCHEDULED_DELAY, time);
        producer.send(message);
        producer.close();
        // make sure the message isn't delivered early
        Thread.sleep(2000);
        Assert.assertEquals(latch.getCount(), COUNT);
        latch.await(5, TimeUnit.SECONDS);
        Assert.assertEquals(latch.getCount(), 0);
        connection.close();
    }

    @Test
    public void testTransactedSchedule() throws Exception {
        final int COUNT = 1;
        Connection connection = createConnection();
        final Session session = connection.createSession(true, SESSION_TRANSACTED);
        MessageConsumer consumer = session.createConsumer(destination);
        final CountDownLatch latch = new CountDownLatch(COUNT);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    session.commit();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                latch.countDown();
            }
        });
        connection.start();
        long time = 5000;
        MessageProducer producer = session.createProducer(destination);
        TextMessage message = session.createTextMessage("test msg");
        message.setLongProperty(AMQ_SCHEDULED_DELAY, time);
        producer.send(message);
        session.commit();
        producer.close();
        // make sure the message isn't delivered early
        Thread.sleep(2000);
        Assert.assertEquals(latch.getCount(), COUNT);
        latch.await(10, TimeUnit.SECONDS);
        Assert.assertEquals(latch.getCount(), 0);
        connection.close();
    }

    @Test
    public void testScheduleRepeated() throws Exception {
        final int NUMBER = 10;
        final AtomicInteger count = new AtomicInteger();
        Connection connection = createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(destination);
        final CountDownLatch latch = new CountDownLatch(NUMBER);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                count.incrementAndGet();
                latch.countDown();
                JmsSchedulerTest.LOG.info("Received scheduled message, waiting for {} more", latch.getCount());
            }
        });
        connection.start();
        MessageProducer producer = session.createProducer(destination);
        TextMessage message = session.createTextMessage("test msg");
        long time = 1000;
        message.setLongProperty(AMQ_SCHEDULED_DELAY, time);
        message.setLongProperty(AMQ_SCHEDULED_PERIOD, 500);
        message.setIntProperty(AMQ_SCHEDULED_REPEAT, (NUMBER - 1));
        producer.send(message);
        producer.close();
        Assert.assertEquals(latch.getCount(), NUMBER);
        latch.await(10, TimeUnit.SECONDS);
        Assert.assertEquals(0, latch.getCount());
        // wait a little longer - make sure we only get NUMBER of replays
        Thread.sleep(1000);
        Assert.assertEquals(NUMBER, count.get());
        connection.close();
    }

    @Test
    public void testScheduleRestart() throws Exception {
        // send a message
        Connection connection = createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        connection.start();
        MessageProducer producer = session.createProducer(destination);
        TextMessage message = session.createTextMessage("test msg");
        long time = 5000;
        message.setLongProperty(AMQ_SCHEDULED_DELAY, time);
        producer.send(message);
        producer.close();
        // restart broker
        broker.stop();
        broker.waitUntilStopped();
        broker = createBroker(false);
        broker.start();
        broker.waitUntilStarted();
        // consume the message
        connection = createConnection();
        connection.start();
        session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(destination);
        Message msg = consumer.receive(10000);
        Assert.assertNotNull("Didn't receive the message", msg);
        // send another message
        producer = session.createProducer(destination);
        message.setLongProperty(AMQ_SCHEDULED_DELAY, time);
        producer.send(message);
        producer.close();
        connection.close();
    }

    @Test
    public void testJobSchedulerStoreUsage() throws Exception {
        // Shrink the store limit down so we get the producer to block
        broker.getSystemUsage().getJobSchedulerUsage().setLimit((10 * 1024));
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
        Connection connection = factory.createConnection();
        connection.start();
        Session sess = connection.createSession(false, AUTO_ACKNOWLEDGE);
        final long time = 5000;
        final ProducerThread producer = new ProducerThread(sess, destination) {
            @Override
            protected Message createMessage(int i) throws Exception {
                Message message = super.createMessage(i);
                message.setLongProperty(AMQ_SCHEDULED_DELAY, time);
                return message;
            }
        };
        producer.setMessageCount(100);
        producer.start();
        MessageConsumer consumer = sess.createConsumer(destination);
        final CountDownLatch latch = new CountDownLatch(100);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                latch.countDown();
            }
        });
        // wait for the producer to block, which should happen immediately, and also wait long
        // enough for the delay to elapse.  We should see no deliveries as the send should block
        // on the first message.
        Thread.sleep(10000L);
        Assert.assertEquals(100, latch.getCount());
        // Increase the store limit so the producer unblocks.  Everything should enqueue at this point.
        broker.getSystemUsage().getJobSchedulerUsage().setLimit(((1024 * 1024) * 33));
        // Wait long enough that the messages are enqueued and the delivery delay has elapsed.
        Thread.sleep(10000L);
        // Make sure we sent all the messages we expected to send
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (producer.getSentCount()) == (producer.getMessageCount());
            }
        }, 20000L);
        Assert.assertEquals("Producer didn't send all messages", producer.getMessageCount(), producer.getSentCount());
        // Make sure we got all the messages we expected to get
        latch.await(20000L, TimeUnit.MILLISECONDS);
        Assert.assertEquals("Consumer did not receive all messages.", 0, latch.getCount());
        connection.close();
    }
}

