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
package org.apache.activemq.broker.jmx;


import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Used to verify that the BrokerView accessed while the BrokerSerivce is waiting
 * for a Slow Store startup to complete doesn't throw unexpected NullPointerExceptions.
 */
public class BrokerViewSlowStoreStartupTest {
    private static final Logger LOG = LoggerFactory.getLogger(BrokerViewSlowStoreStartupTest.class);

    private final CountDownLatch holdStoreStart = new CountDownLatch(1);

    private final String brokerName = "brokerViewTest";

    private BrokerService broker;

    private Thread startThread;

    @Test(timeout = 120000)
    public void testBrokerViewOnSlowStoreStart() throws Exception {
        // Ensure we have an Admin View.
        Assert.assertTrue(Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (broker.getAdminView()) != null;
            }
        }));
        final BrokerView view = broker.getAdminView();
        try {
            view.getBrokerName();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getBrokerId();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getTotalEnqueueCount();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getTotalDequeueCount();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getTotalConsumerCount();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getTotalProducerCount();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getTotalMessageCount();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getTotalMessagesCached();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.resetStatistics();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.enableStatistics();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.disableStatistics();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.isStatisticsEnabled();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getTopics();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getQueues();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getTemporaryTopics();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getTemporaryQueues();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getTopicSubscribers();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getDurableTopicSubscribers();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getQueueSubscribers();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getTemporaryTopicSubscribers();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getTemporaryQueueSubscribers();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getInactiveDurableTopicSubscribers();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getTopicProducers();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getQueueProducers();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getTemporaryTopicProducers();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getTemporaryQueueProducers();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.getDynamicDestinationProducers();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.removeConnector("tcp");
            Assert.fail("Should have thrown an NoSuchElementException");
        } catch (NoSuchElementException e) {
        }
        try {
            view.removeNetworkConnector("tcp");
            Assert.fail("Should have thrown an NoSuchElementException");
        } catch (NoSuchElementException e) {
        }
        try {
            view.addTopic("TEST");
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.addQueue("TEST");
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.removeTopic("TEST");
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.removeQueue("TEST");
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.createDurableSubscriber("1", "2", "3", "4");
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        try {
            view.destroyDurableSubscriber("1", "2");
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
        }
        holdStoreStart.countDown();
        startThread.join();
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (view.getBroker()) != null;
            }
        });
        Assert.assertNotNull(view.getBroker());
        try {
            view.getBrokerName();
        } catch (Exception e) {
            Assert.fail(("caught an exception getting the Broker property: " + (e.getClass().getName())));
        }
        try {
            view.getBrokerId();
        } catch (IllegalStateException e) {
            Assert.fail(("caught an exception getting the Broker property: " + (e.getClass().getName())));
        }
        try {
            view.getTotalEnqueueCount();
        } catch (IllegalStateException e) {
            Assert.fail(("caught an exception getting the Broker property: " + (e.getClass().getName())));
        }
    }
}

