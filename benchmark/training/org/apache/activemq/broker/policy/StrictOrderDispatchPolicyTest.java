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


import org.apache.activemq.broker.TopicSubscriptionTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;


@RunWith(BlockJUnit4ClassRunner.class)
public class StrictOrderDispatchPolicyTest extends TopicSubscriptionTest {
    @Test
    @Override
    public void testOneProducerTwoConsumersLargeMessagesOnePrefetch() throws Exception {
        super.testOneProducerTwoConsumersLargeMessagesOnePrefetch();
        assertReceivedMessagesAreOrdered();
    }

    @Test
    @Override
    public void testOneProducerTwoConsumersSmallMessagesOnePrefetch() throws Exception {
        super.testOneProducerTwoConsumersSmallMessagesOnePrefetch();
        assertReceivedMessagesAreOrdered();
    }

    @Test
    @Override
    public void testOneProducerTwoConsumersSmallMessagesLargePrefetch() throws Exception {
        super.testOneProducerTwoConsumersSmallMessagesLargePrefetch();
        assertReceivedMessagesAreOrdered();
    }

    @Test
    @Override
    public void testOneProducerTwoConsumersLargeMessagesLargePrefetch() throws Exception {
        super.testOneProducerTwoConsumersLargeMessagesLargePrefetch();
        assertReceivedMessagesAreOrdered();
    }

    @Test
    @Override
    public void testOneProducerManyConsumersFewMessages() throws Exception {
        super.testOneProducerManyConsumersFewMessages();
        assertReceivedMessagesAreOrdered();
    }

    @Test
    @Override
    public void testOneProducerManyConsumersManyMessages() throws Exception {
        super.testOneProducerManyConsumersManyMessages();
        assertReceivedMessagesAreOrdered();
    }

    @Test
    @Override
    public void testManyProducersOneConsumer() throws Exception {
        super.testManyProducersOneConsumer();
        assertReceivedMessagesAreOrdered();
    }

    @Test
    @Override
    public void testManyProducersManyConsumers() throws Exception {
        super.testManyProducersManyConsumers();
        assertReceivedMessagesAreOrdered();
    }
}

