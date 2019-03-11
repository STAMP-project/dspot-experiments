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


import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Assert;
import org.junit.Test;


public class AMQ6579Test {
    protected BrokerService brokerService;

    protected Connection connection;

    protected Session session;

    protected ActiveMQTopic topic;

    protected Destination amqDestination;

    protected MessageConsumer consumer;

    /**
     * Test that messages are expired properly on a topic subscription when a
     * constant pending limit strategy is set and that future messages are
     * dispatched properly so that the consumer isn't blocked
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 10000)
    public void testExpireWithPendingLimitStrategy() throws Exception {
        // Send 5 messages that are not expired to fill up prefetch
        // followed by 5 messages that can be expired
        // then another 5 messages that won't expire
        // Make sure 10 messages are received
        sendMessages(5, 0);
        sendMessages(5, 1);
        sendMessages(5, 0);
        // should get 10 messages as the middle 5 should expire
        Assert.assertEquals(10, receiveMessages());
    }
}

