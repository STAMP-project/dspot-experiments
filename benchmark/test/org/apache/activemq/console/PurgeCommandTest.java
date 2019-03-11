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
package org.apache.activemq.console;


import javax.jms.ConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * Tests for the purge command.
 */
public class PurgeCommandTest {
    private BrokerService brokerService;

    private ConnectionFactory factory;

    @Rule
    public TestName name = new TestName();

    @Test(timeout = 30000)
    public void testPurge() throws Exception {
        produce(10);
        QueueViewMBean queueView = getProxyToQueue(getDestinationName());
        Assert.assertEquals(10, queueView.getQueueSize());
        executePurge(getDestinationName());
        Assert.assertEquals(0, queueView.getQueueSize());
    }

    @Test(timeout = 30000)
    public void testPurgeWithReset() throws Exception {
        produce(20);
        consume(10);
        QueueViewMBean queueView = getProxyToQueue(getDestinationName());
        Assert.assertEquals(10, queueView.getQueueSize());
        Assert.assertEquals(20, queueView.getEnqueueCount());
        Assert.assertEquals(10, queueView.getDequeueCount());
        // Normal purge doesn't change stats.
        executePurge(getDestinationName());
        Assert.assertEquals(0, queueView.getQueueSize());
        Assert.assertEquals(20, queueView.getEnqueueCount());
        Assert.assertEquals(20, queueView.getDequeueCount());
        // Purge on empty leaves stats alone.
        executePurge(getDestinationName());
        Assert.assertEquals(0, queueView.getQueueSize());
        Assert.assertEquals(20, queueView.getEnqueueCount());
        Assert.assertEquals(20, queueView.getDequeueCount());
        executePurge(("--reset " + (getDestinationName())));
        // Purge on empty with reset clears stats.
        Assert.assertEquals(0, queueView.getQueueSize());
        Assert.assertEquals(0, queueView.getEnqueueCount());
        Assert.assertEquals(0, queueView.getDequeueCount());
        produce(20);
        consume(10);
        Assert.assertEquals(10, queueView.getQueueSize());
        Assert.assertEquals(20, queueView.getEnqueueCount());
        Assert.assertEquals(10, queueView.getDequeueCount());
        executePurge(("--reset " + (getDestinationName())));
        // Purge on non-empty with reset clears stats.
        Assert.assertEquals(0, queueView.getQueueSize());
        Assert.assertEquals(0, queueView.getEnqueueCount());
        Assert.assertEquals(0, queueView.getDequeueCount());
    }
}

