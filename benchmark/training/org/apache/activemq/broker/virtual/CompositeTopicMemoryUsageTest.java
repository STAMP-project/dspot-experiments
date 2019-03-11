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
package org.apache.activemq.broker.virtual;


import junit.framework.Assert;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test to ensure the CompositeTopic Memory Usage returns to zero after messages forwarded to underlying queues
 */
public class CompositeTopicMemoryUsageTest {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeTopicMemoryUsageTest.class);

    public int messageSize = 5 * 1024;

    public int messageCount = 1000;

    ActiveMQTopic target = new ActiveMQTopic("target");

    BrokerService brokerService;

    ActiveMQConnectionFactory connectionFactory;

    @Test
    public void testMemoryUsage() throws Exception {
        startBroker(4, true);
        messageSize = 20 * 1024;
        produceMessages(20, target);
        long memoryUsage = getMemoryUsageForTopic(target.getPhysicalName());
        Assert.assertEquals("MemoryUsage should be zero", 0L, memoryUsage);
        brokerService.stop();
        brokerService.waitUntilStopped();
    }
}

