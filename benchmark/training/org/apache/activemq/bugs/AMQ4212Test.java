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


import java.util.concurrent.TimeUnit;
import javax.management.ObjectName;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ4212Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ4212Test.class);

    private BrokerService service;

    private String connectionUri;

    private ActiveMQConnectionFactory cf;

    private final int MSG_COUNT = 256;

    @Test
    public void testDurableSubPrefetchRecovered() throws Exception {
        ActiveMQQueue queue = new ActiveMQQueue("MyQueue");
        ActiveMQTopic topic = new ActiveMQTopic("MyDurableTopic");
        // Send to a Queue to create some journal files
        sendMessages(queue);
        AMQ4212Test.LOG.info("There are currently [{}] journal log files.", getNumberOfJournalFiles());
        createInactiveDurableSub(topic);
        Assert.assertTrue("Should have an inactive durable sub", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                ObjectName[] subs = service.getAdminView().getInactiveDurableTopicSubscribers();
                return (subs != null) && ((subs.length) == 1) ? true : false;
            }
        }));
        // Now send some more to the queue to create even more files.
        sendMessages(queue);
        AMQ4212Test.LOG.info("There are currently [{}] journal log files.", getNumberOfJournalFiles());
        Assert.assertTrue(((getNumberOfJournalFiles()) > 1));
        AMQ4212Test.LOG.info("Restarting the broker.");
        restartBroker();
        AMQ4212Test.LOG.info("Restarted the broker.");
        AMQ4212Test.LOG.info("There are currently [{}] journal log files.", getNumberOfJournalFiles());
        Assert.assertTrue(((getNumberOfJournalFiles()) > 1));
        Assert.assertTrue("Should have an inactive durable sub", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                ObjectName[] subs = service.getAdminView().getInactiveDurableTopicSubscribers();
                return (subs != null) && ((subs.length) == 1) ? true : false;
            }
        }));
        // Clear out all queue data
        service.getAdminView().removeQueue(queue.getQueueName());
        Assert.assertTrue(("Less than two journal files expected, was " + (getNumberOfJournalFiles())), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getNumberOfJournalFiles()) <= 2;
            }
        }, TimeUnit.MINUTES.toMillis(2)));
        AMQ4212Test.LOG.info("Sending {} Messages to the Topic.", MSG_COUNT);
        // Send some messages to the inactive destination
        sendMessages(topic);
        AMQ4212Test.LOG.info("Attempt to consume {} messages from the Topic.", MSG_COUNT);
        Assert.assertEquals(MSG_COUNT, consumeFromInactiveDurableSub(topic));
        AMQ4212Test.LOG.info("Recovering the broker.");
        recoverBroker();
        AMQ4212Test.LOG.info("Recovering the broker.");
        Assert.assertTrue("Should have an inactive durable sub", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                ObjectName[] subs = service.getAdminView().getInactiveDurableTopicSubscribers();
                return (subs != null) && ((subs.length) == 1) ? true : false;
            }
        }));
    }

    @Test
    public void testDurableAcksNotDropped() throws Exception {
        ActiveMQQueue queue = new ActiveMQQueue("MyQueue");
        ActiveMQTopic topic = new ActiveMQTopic("MyDurableTopic");
        // Create durable sub in first data file.
        createInactiveDurableSub(topic);
        Assert.assertTrue("Should have an inactive durable sub", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                ObjectName[] subs = service.getAdminView().getInactiveDurableTopicSubscribers();
                return (subs != null) && ((subs.length) == 1) ? true : false;
            }
        }));
        // Send to a Topic
        sendMessages(topic, 1);
        // Send to a Queue to create some journal files
        sendMessages(queue);
        AMQ4212Test.LOG.info("Before consume there are currently [{}] journal log files.", getNumberOfJournalFiles());
        // Consume all the Messages leaving acks behind.
        consumeDurableMessages(topic, 1);
        AMQ4212Test.LOG.info("After consume there are currently [{}] journal log files.", getNumberOfJournalFiles());
        // Now send some more to the queue to create even more files.
        sendMessages(queue);
        AMQ4212Test.LOG.info("More Queued. There are currently [{}] journal log files.", getNumberOfJournalFiles());
        Assert.assertTrue(((getNumberOfJournalFiles()) > 1));
        AMQ4212Test.LOG.info("Restarting the broker.");
        restartBroker();
        AMQ4212Test.LOG.info("Restarted the broker.");
        AMQ4212Test.LOG.info("There are currently [{}] journal log files.", getNumberOfJournalFiles());
        Assert.assertTrue(((getNumberOfJournalFiles()) > 1));
        Assert.assertTrue("Should have an inactive durable sub", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                ObjectName[] subs = service.getAdminView().getInactiveDurableTopicSubscribers();
                return (subs != null) && ((subs.length) == 1) ? true : false;
            }
        }));
        // Clear out all queue data
        service.getAdminView().removeQueue(queue.getQueueName());
        Assert.assertTrue(("Less than three journal file expected, was " + (getNumberOfJournalFiles())), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getNumberOfJournalFiles()) <= 3;
            }
        }, TimeUnit.MINUTES.toMillis(3)));
        // See if we receive any message they should all be acked.
        tryConsumeExpectNone(topic);
        AMQ4212Test.LOG.info("There are currently [{}] journal log files.", getNumberOfJournalFiles());
        AMQ4212Test.LOG.info("Recovering the broker.");
        recoverBroker();
        AMQ4212Test.LOG.info("Recovering the broker.");
        AMQ4212Test.LOG.info("There are currently [{}] journal log files.", getNumberOfJournalFiles());
        Assert.assertTrue("Should have an inactive durable sub", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                ObjectName[] subs = service.getAdminView().getInactiveDurableTopicSubscribers();
                return (subs != null) && ((subs.length) == 1) ? true : false;
            }
        }));
        // See if we receive any message they should all be acked.
        tryConsumeExpectNone(topic);
        Assert.assertTrue(("Less than three journal file expected, was " + (getNumberOfJournalFiles())), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getNumberOfJournalFiles()) == 1;
            }
        }, TimeUnit.MINUTES.toMillis(1)));
    }
}

