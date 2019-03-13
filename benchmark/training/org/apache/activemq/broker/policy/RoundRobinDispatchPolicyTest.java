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
package org.apache.activemq.broker.policy;


import org.apache.activemq.broker.QueueSubscriptionTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;


@RunWith(BlockJUnit4ClassRunner.class)
public class RoundRobinDispatchPolicyTest extends QueueSubscriptionTest {
    @Test(timeout = 60 * 1000)
    public void testOneProducerTwoConsumersSmallMessagesOnePrefetch() throws Exception {
        super.testOneProducerTwoConsumersSmallMessagesOnePrefetch();
        // Ensure that each consumer should have received at least one message
        // We cannot guarantee that messages will be equally divided, since
        // prefetch is one
        assertEachConsumerReceivedAtLeastXMessages(1);
    }

    @Test(timeout = 60 * 1000)
    public void testOneProducerTwoConsumersSmallMessagesLargePrefetch() throws Exception {
        super.testOneProducerTwoConsumersSmallMessagesLargePrefetch();
        assertMessagesDividedAmongConsumers();
    }

    @Test(timeout = 60 * 1000)
    public void testOneProducerTwoConsumersLargeMessagesOnePrefetch() throws Exception {
        super.testOneProducerTwoConsumersLargeMessagesOnePrefetch();
        // Ensure that each consumer should have received at least one message
        // We cannot guarantee that messages will be equally divided, since
        // prefetch is one
        assertEachConsumerReceivedAtLeastXMessages(1);
    }

    @Test(timeout = 60 * 1000)
    public void testOneProducerTwoConsumersLargeMessagesLargePrefetch() throws Exception {
        super.testOneProducerTwoConsumersLargeMessagesLargePrefetch();
        assertMessagesDividedAmongConsumers();
    }

    @Test(timeout = 60 * 1000)
    public void testOneProducerManyConsumersFewMessages() throws Exception {
        super.testOneProducerManyConsumersFewMessages();
        // Since there are more consumers, each consumer should have received at
        // most one message only
        assertMessagesDividedAmongConsumers();
    }

    @Test(timeout = 60 * 1000)
    public void testOneProducerManyConsumersManyMessages() throws Exception {
        super.testOneProducerManyConsumersManyMessages();
        assertMessagesDividedAmongConsumers();
    }

    @Test(timeout = 60 * 1000)
    public void testManyProducersManyConsumers() throws Exception {
        super.testManyProducersManyConsumers();
        assertMessagesDividedAmongConsumers();
    }

    @Test(timeout = 60 * 1000)
    public void testOneProducerTwoMatchingConsumersOneNotMatchingConsumer() throws Exception {
        // Create consumer that won't consume any message
        createMessageConsumer(createConnectionFactory().createConnection(), createDestination(), "JMSPriority<1");
        super.testOneProducerTwoConsumersSmallMessagesLargePrefetch();
        assertMessagesDividedAmongConsumers();
    }
}

