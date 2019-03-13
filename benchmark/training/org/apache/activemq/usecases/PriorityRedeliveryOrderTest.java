/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.usecases;


import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Sends X messages with a sequence number held in a JMS property "appId"
 * Uses all priority 4 message (normal priority)
 * closed the consumer connection multiple times so the already prefetched messages will be available
 * for dispatch again.
 */
public class PriorityRedeliveryOrderTest {
    private static final Logger LOG = LoggerFactory.getLogger(PriorityRedeliveryOrderTest.class);

    private static final String DESTINATION = "testQ1";

    private static final int MESSAGES_TO_SEND = 1000;

    private static final int MESSAGES_PER_CONSUMER = 200;

    private int consumedAppId = -1;

    private int totalConsumed;

    BrokerService broker;

    @Test
    public void testMessageDeliveryOrderAfterPrefetch() throws Exception {
        // send X messages with with a sequence number number in the message property.
        sendMessages(PriorityRedeliveryOrderTest.MESSAGES_TO_SEND);
        for (int i = 0; i < ((PriorityRedeliveryOrderTest.MESSAGES_TO_SEND) / (PriorityRedeliveryOrderTest.MESSAGES_PER_CONSUMER)); i++) {
            totalConsumed += consumeMessages(PriorityRedeliveryOrderTest.MESSAGES_PER_CONSUMER);
        }
        Assert.assertEquals("number of messages consumed should be equal to number of messages sent", PriorityRedeliveryOrderTest.MESSAGES_TO_SEND, totalConsumed);
    }
}

