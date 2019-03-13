/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.web;


import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.management.ObjectName;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.web.config.SystemPropertiesConfiguration;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="http://www.christianposta.com/blog">Christian Posta</a>

You can use this class to connect up to a running web console and run some queries.
Used to work through https://issues.apache.org/jira/browse/AMQ-4272 but would be useful
in any scenario where you need access to the underlying broker in the web-console to hack
at it
 */
public class RemoteJMXBrokerTest {
    private BrokerService brokerService;

    /**
     * Test that we can query the remote broker...
     * Specifically this tests that the domain and objectnames are correct (type and brokerName
     * instead of Type and BrokerName, which they were)
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnectRemoteBrokerFacade() throws Exception {
        RemoteJMXBrokerFacade brokerFacade = new RemoteJMXBrokerFacade();
        SystemPropertiesConfiguration configuration = new SystemPropertiesConfiguration();
        brokerFacade.setConfiguration(configuration);
        ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=remoteBroker");
        Set<ObjectName> queryResult = brokerFacade.queryNames(query, null);
        System.out.println(("Number: " + (queryResult.size())));
        Assert.assertEquals(1, queryResult.size());
    }

    /**
     * Before AMQ-5896 there was the possibility of an InstanceNotFoundException when
     * brokerFacade.getQueue if a destination was deleted after the initial list was looked
     * up but before iterating over the list to find the right destination by name.
     */
    @Test(timeout = 10000)
    public void testGetDestinationRaceCondition() throws Exception {
        final CountDownLatch getQueuesLatch = new CountDownLatch(1);
        final CountDownLatch destDeletionLatch = new CountDownLatch(1);
        // Adding a pause so we can test the case where the destination is
        // deleted in between calling getQueues() and iterating over the list
        // and calling getName() on the DestinationViewMBean
        // See AMQ-5896
        RemoteJMXBrokerFacade brokerFacade = new RemoteJMXBrokerFacade() {
            @Override
            protected DestinationViewMBean getDestinationByName(Collection<? extends DestinationViewMBean> collection, String name) {
                try {
                    // we are done getting the queue collection so let thread know
                    // to remove destination
                    getQueuesLatch.countDown();
                    // wait until other thread is done removing destination
                    destDeletionLatch.await();
                } catch (InterruptedException e) {
                }
                return super.getDestinationByName(collection, name);
            }
        };
        SystemPropertiesConfiguration configuration = new SystemPropertiesConfiguration();
        brokerFacade.setConfiguration(configuration);
        // Create the destination
        final ActiveMQQueue queue = new ActiveMQQueue("queue.test");
        brokerService.getDestination(queue);
        // after 1 second delete
        ExecutorService service = Executors.newCachedThreadPool();
        service.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    // wait for confirmation that the queue list was obtained
                    getQueuesLatch.await();
                    brokerService.removeDestination(queue);
                    // let original thread know destination was deleted
                    destDeletionLatch.countDown();
                } catch (Exception e) {
                }
            }
        });
        // Assert that the destination is now null because it was deleted in another thread
        // during iteration
        Assert.assertNull(brokerFacade.getQueue(queue.getPhysicalName()));
        service.shutdown();
    }
}

