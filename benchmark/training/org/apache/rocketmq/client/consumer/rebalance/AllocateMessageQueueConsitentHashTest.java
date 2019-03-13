/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.client.consumer.rebalance;


import java.util.List;
import java.util.Random;
import org.apache.rocketmq.common.message.MessageQueue;
import org.junit.Assert;
import org.junit.Test;


public class AllocateMessageQueueConsitentHashTest {
    private String topic;

    private static final String CID_PREFIX = "CID-";

    @Test
    public void testCurrentCIDNotExists() {
        String currentCID = String.valueOf(Integer.MAX_VALUE);
        List<String> consumerIdList = createConsumerIdList(2);
        List<MessageQueue> messageQueueList = createMessageQueueList(6);
        List<MessageQueue> result = new AllocateMessageQueueConsistentHash().allocate("", currentCID, messageQueueList, consumerIdList);
        printMessageQueue(result, "testCurrentCIDNotExists");
        Assert.assertEquals(result.size(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCurrentCIDIllegalArgument() {
        List<String> consumerIdList = createConsumerIdList(2);
        List<MessageQueue> messageQueueList = createMessageQueueList(6);
        new AllocateMessageQueueConsistentHash().allocate("", "", messageQueueList, consumerIdList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMessageQueueIllegalArgument() {
        String currentCID = "0";
        List<String> consumerIdList = createConsumerIdList(2);
        new AllocateMessageQueueConsistentHash().allocate("", currentCID, null, consumerIdList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConsumerIdIllegalArgument() {
        String currentCID = "0";
        List<MessageQueue> messageQueueList = createMessageQueueList(6);
        new AllocateMessageQueueConsistentHash().allocate("", currentCID, messageQueueList, null);
    }

    @Test
    public void testAllocate1() {
        testAllocate(20, 10);
    }

    @Test
    public void testAllocate2() {
        testAllocate(10, 20);
    }

    @Test
    public void testRun100RandomCase() {
        for (int i = 0; i < 10; i++) {
            int consumerSize = (new Random().nextInt(20)) + 1;// 1-20

            int queueSize = (new Random().nextInt(20)) + 1;// 1-20

            testAllocate(queueSize, consumerSize);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
    }
}

