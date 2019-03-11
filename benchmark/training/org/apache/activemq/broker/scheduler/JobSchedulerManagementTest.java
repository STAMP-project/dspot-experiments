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
import ScheduledMessage.AMQ_SCHEDULED_ID;
import ScheduledMessage.AMQ_SCHEDULER_ACTION;
import ScheduledMessage.AMQ_SCHEDULER_ACTION_BROWSE;
import ScheduledMessage.AMQ_SCHEDULER_ACTION_END_TIME;
import ScheduledMessage.AMQ_SCHEDULER_ACTION_REMOVE;
import ScheduledMessage.AMQ_SCHEDULER_ACTION_REMOVEALL;
import ScheduledMessage.AMQ_SCHEDULER_ACTION_START_TIME;
import ScheduledMessage.AMQ_SCHEDULER_MANAGEMENT_DESTINATION;
import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.util.IdGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JobSchedulerManagementTest extends JobSchedulerTestSupport {
    private static final transient Logger LOG = LoggerFactory.getLogger(JobSchedulerManagementTest.class);

    @Test
    public void testRemoveAllScheduled() throws Exception {
        final int COUNT = 5;
        Connection connection = createConnection();
        // Setup the scheduled Message
        scheduleMessage(connection, TimeUnit.SECONDS.toMillis(6), COUNT);
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        // Create the Browse Destination and the Reply To location
        Destination management = session.createTopic(AMQ_SCHEDULER_MANAGEMENT_DESTINATION);
        // Create the eventual Consumer to receive the scheduled message
        MessageConsumer consumer = session.createConsumer(destination);
        final CountDownLatch latch = new CountDownLatch(COUNT);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                latch.countDown();
            }
        });
        connection.start();
        // Send the remove request
        MessageProducer producer = session.createProducer(management);
        Message request = session.createMessage();
        request.setStringProperty(AMQ_SCHEDULER_ACTION, AMQ_SCHEDULER_ACTION_REMOVEALL);
        producer.send(request);
        // Now wait and see if any get delivered, none should.
        latch.await(10, TimeUnit.SECONDS);
        Assert.assertEquals(latch.getCount(), COUNT);
        connection.close();
    }

    @Test
    public void testRemoveAllScheduledAtTime() throws Exception {
        final int COUNT = 3;
        Connection connection = createConnection();
        // Setup the scheduled Message
        scheduleMessage(connection, TimeUnit.SECONDS.toMillis(6));
        scheduleMessage(connection, TimeUnit.SECONDS.toMillis(15));
        scheduleMessage(connection, TimeUnit.SECONDS.toMillis(20));
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        // Create the Browse Destination and the Reply To location
        Destination management = session.createTopic(AMQ_SCHEDULER_MANAGEMENT_DESTINATION);
        Destination browseDest = session.createTemporaryQueue();
        // Create the eventual Consumer to receive the scheduled message
        MessageConsumer consumer = session.createConsumer(destination);
        final CountDownLatch latch = new CountDownLatch(COUNT);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                latch.countDown();
            }
        });
        // Create the "Browser"
        MessageConsumer browser = session.createConsumer(browseDest);
        final CountDownLatch browsedLatch = new CountDownLatch(COUNT);
        browser.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                browsedLatch.countDown();
                JobSchedulerManagementTest.LOG.debug(("Scheduled Message Browser got Message: " + message));
            }
        });
        connection.start();
        long start = (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(10));
        long end = (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(30));
        // Send the remove request
        MessageProducer producer = session.createProducer(management);
        Message request = session.createMessage();
        request.setStringProperty(AMQ_SCHEDULER_ACTION, AMQ_SCHEDULER_ACTION_REMOVEALL);
        request.setStringProperty(AMQ_SCHEDULER_ACTION_START_TIME, Long.toString(start));
        request.setStringProperty(AMQ_SCHEDULER_ACTION_END_TIME, Long.toString(end));
        producer.send(request);
        // Send the browse request
        request = session.createMessage();
        request.setStringProperty(AMQ_SCHEDULER_ACTION, AMQ_SCHEDULER_ACTION_BROWSE);
        request.setJMSReplyTo(browseDest);
        producer.send(request);
        // now see if we got back only the one remaining message.
        latch.await(10, TimeUnit.SECONDS);
        Assert.assertEquals(2, browsedLatch.getCount());
        // Now wait and see if any get delivered, none should.
        latch.await(10, TimeUnit.SECONDS);
        Assert.assertEquals(2, latch.getCount());
        connection.close();
    }

    @Test
    public void testBrowseAllScheduled() throws Exception {
        final int COUNT = 10;
        Connection connection = createConnection();
        // Setup the scheduled Message
        scheduleMessage(connection, TimeUnit.SECONDS.toMillis(9), COUNT);
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        // Create the Browse Destination and the Reply To location
        Destination requestBrowse = session.createTopic(AMQ_SCHEDULER_MANAGEMENT_DESTINATION);
        Destination browseDest = session.createTemporaryQueue();
        // Create the eventual Consumer to receive the scheduled message
        MessageConsumer consumer = session.createConsumer(destination);
        final CountDownLatch latch = new CountDownLatch(COUNT);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                latch.countDown();
            }
        });
        // Create the "Browser"
        MessageConsumer browser = session.createConsumer(browseDest);
        final CountDownLatch browsedLatch = new CountDownLatch(COUNT);
        browser.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                browsedLatch.countDown();
                JobSchedulerManagementTest.LOG.debug(("Scheduled Message Browser got Message: " + message));
            }
        });
        connection.start();
        // Send the browse request
        MessageProducer producer = session.createProducer(requestBrowse);
        Message request = session.createMessage();
        request.setStringProperty(AMQ_SCHEDULER_ACTION, AMQ_SCHEDULER_ACTION_BROWSE);
        request.setJMSReplyTo(browseDest);
        producer.send(request);
        // make sure the message isn't delivered early because we browsed it
        Thread.sleep(2000);
        Assert.assertEquals(latch.getCount(), COUNT);
        // now see if we got all the scheduled messages on the browse
        // destination.
        latch.await(10, TimeUnit.SECONDS);
        Assert.assertEquals(browsedLatch.getCount(), 0);
        // now check that they all got delivered
        latch.await(10, TimeUnit.SECONDS);
        Assert.assertEquals(latch.getCount(), 0);
        connection.close();
    }

    @Test
    public void testBrowseWindowlScheduled() throws Exception {
        final int COUNT = 10;
        Connection connection = createConnection();
        // Setup the scheduled Message
        scheduleMessage(connection, TimeUnit.SECONDS.toMillis(5));
        scheduleMessage(connection, TimeUnit.SECONDS.toMillis(10), COUNT);
        scheduleMessage(connection, TimeUnit.SECONDS.toMillis(20));
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        // Create the Browse Destination and the Reply To location
        Destination requestBrowse = session.createTopic(AMQ_SCHEDULER_MANAGEMENT_DESTINATION);
        Destination browseDest = session.createTemporaryQueue();
        // Create the eventual Consumer to receive the scheduled message
        MessageConsumer consumer = session.createConsumer(destination);
        final CountDownLatch latch = new CountDownLatch((COUNT + 2));
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                latch.countDown();
            }
        });
        // Create the "Browser"
        MessageConsumer browser = session.createConsumer(browseDest);
        final CountDownLatch browsedLatch = new CountDownLatch(COUNT);
        browser.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                browsedLatch.countDown();
                JobSchedulerManagementTest.LOG.debug(("Scheduled Message Browser got Message: " + message));
            }
        });
        connection.start();
        long start = (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(6));
        long end = (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(15));
        // Send the browse request
        MessageProducer producer = session.createProducer(requestBrowse);
        Message request = session.createMessage();
        request.setStringProperty(AMQ_SCHEDULER_ACTION, AMQ_SCHEDULER_ACTION_BROWSE);
        request.setStringProperty(AMQ_SCHEDULER_ACTION_START_TIME, Long.toString(start));
        request.setStringProperty(AMQ_SCHEDULER_ACTION_END_TIME, Long.toString(end));
        request.setJMSReplyTo(browseDest);
        producer.send(request);
        // make sure the message isn't delivered early because we browsed it
        Thread.sleep(2000);
        Assert.assertEquals((COUNT + 2), latch.getCount());
        // now see if we got all the scheduled messages on the browse
        // destination.
        latch.await(15, TimeUnit.SECONDS);
        Assert.assertEquals(0, browsedLatch.getCount());
        // now see if we got all the scheduled messages on the browse
        // destination.
        latch.await(20, TimeUnit.SECONDS);
        Assert.assertEquals(0, latch.getCount());
        connection.close();
    }

    @Test
    public void testRemoveScheduled() throws Exception {
        final int COUNT = 10;
        Connection connection = createConnection();
        // Setup the scheduled Message
        scheduleMessage(connection, TimeUnit.SECONDS.toMillis(9), COUNT);
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        // Create the Browse Destination and the Reply To location
        Destination management = session.createTopic(AMQ_SCHEDULER_MANAGEMENT_DESTINATION);
        Destination browseDest = session.createTemporaryQueue();
        // Create the eventual Consumer to receive the scheduled message
        MessageConsumer consumer = session.createConsumer(destination);
        MessageProducer producer = session.createProducer(management);
        final CountDownLatch latch = new CountDownLatch(COUNT);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                latch.countDown();
            }
        });
        // Create the "Browser"
        Session browseSession = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer browser = browseSession.createConsumer(browseDest);
        connection.start();
        // Send the browse request
        Message request = session.createMessage();
        request.setStringProperty(AMQ_SCHEDULER_ACTION, AMQ_SCHEDULER_ACTION_BROWSE);
        request.setJMSReplyTo(browseDest);
        producer.send(request);
        // Browse all the Scheduled Messages.
        for (int i = 0; i < COUNT; ++i) {
            Message message = browser.receive(2000);
            Assert.assertNotNull(message);
            try {
                Message remove = session.createMessage();
                remove.setStringProperty(AMQ_SCHEDULER_ACTION, AMQ_SCHEDULER_ACTION_REMOVE);
                remove.setStringProperty(AMQ_SCHEDULED_ID, message.getStringProperty(AMQ_SCHEDULED_ID));
                producer.send(remove);
            } catch (Exception e) {
            }
        }
        // now check that they all got removed and are not delivered.
        latch.await(11, TimeUnit.SECONDS);
        Assert.assertEquals(COUNT, latch.getCount());
        connection.close();
    }

    @Test
    public void testRemoveNotScheduled() throws Exception {
        Connection connection = createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        // Create the Browse Destination and the Reply To location
        Destination management = session.createTopic(AMQ_SCHEDULER_MANAGEMENT_DESTINATION);
        MessageProducer producer = session.createProducer(management);
        try {
            // Send the remove request
            Message remove = session.createMessage();
            remove.setStringProperty(AMQ_SCHEDULER_ACTION, AMQ_SCHEDULER_ACTION_REMOVEALL);
            remove.setStringProperty(AMQ_SCHEDULED_ID, new IdGenerator().generateId());
            producer.send(remove);
        } catch (Exception e) {
            Assert.fail("Caught unexpected exception during remove of unscheduled message.");
        } finally {
            connection.close();
        }
    }

    @Test
    public void testBrowseWithSelector() throws Exception {
        Connection connection = createConnection();
        // Setup the scheduled Message
        scheduleMessage(connection, TimeUnit.SECONDS.toMillis(9));
        scheduleMessage(connection, TimeUnit.SECONDS.toMillis(10));
        scheduleMessage(connection, TimeUnit.SECONDS.toMillis(5));
        scheduleMessage(connection, TimeUnit.SECONDS.toMillis(45));
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        // Create the Browse Destination and the Reply To location
        Destination requestBrowse = session.createTopic(AMQ_SCHEDULER_MANAGEMENT_DESTINATION);
        Destination browseDest = session.createTemporaryTopic();
        // Create the "Browser"
        MessageConsumer browser = session.createConsumer(browseDest, ((ScheduledMessage.AMQ_SCHEDULED_DELAY) + " = 45000"));
        connection.start();
        // Send the browse request
        MessageProducer producer = session.createProducer(requestBrowse);
        Message request = session.createMessage();
        request.setStringProperty(AMQ_SCHEDULER_ACTION, AMQ_SCHEDULER_ACTION_BROWSE);
        request.setJMSReplyTo(browseDest);
        producer.send(request);
        // Now try and receive the one we selected
        Message message = browser.receive(5000);
        Assert.assertNotNull(message);
        Assert.assertEquals(45000, message.getLongProperty(AMQ_SCHEDULED_DELAY));
        // Verify that original destination was preserved
        Assert.assertEquals(destination, getOriginalDestination());
        // Now check if there are anymore, there shouldn't be
        message = browser.receive(5000);
        Assert.assertNull(message);
        connection.close();
    }
}

