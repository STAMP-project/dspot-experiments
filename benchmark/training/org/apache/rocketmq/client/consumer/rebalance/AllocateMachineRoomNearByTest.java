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


import AllocateMachineRoomNearby.MachineRoomResolver;
import java.util.Random;
import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.common.message.MessageQueue;
import org.junit.Test;


public class AllocateMachineRoomNearByTest {
    private static final String CID_PREFIX = "CID-";

    private final String topic = "topic_test";

    private final MachineRoomResolver machineRoomResolver = new AllocateMachineRoomNearby.MachineRoomResolver() {
        @Override
        public String brokerDeployIn(MessageQueue messageQueue) {
            return messageQueue.getBrokerName().split("-")[0];
        }

        @Override
        public String consumerDeployIn(String clientID) {
            return clientID.split("-")[0];
        }
    };

    private final AllocateMessageQueueStrategy allocateMessageQueueStrategy = new AllocateMachineRoomNearby(new AllocateMessageQueueAveragely(), machineRoomResolver);

    @Test
    public void test1() {
        testWhenIDCSizeEquals(5, 20, 10, false);
        testWhenIDCSizeEquals(5, 20, 20, false);
        testWhenIDCSizeEquals(5, 20, 30, false);
        testWhenIDCSizeEquals(5, 20, 0, false);
    }

    @Test
    public void test2() {
        testWhenConsumerIDCIsMore(5, 1, 10, 10, false);
        testWhenConsumerIDCIsMore(5, 1, 10, 5, false);
        testWhenConsumerIDCIsMore(5, 1, 10, 20, false);
        testWhenConsumerIDCIsMore(5, 1, 10, 0, false);
    }

    @Test
    public void test3() {
        testWhenConsumerIDCIsLess(5, 2, 10, 10, false);
        testWhenConsumerIDCIsLess(5, 2, 10, 5, false);
        testWhenConsumerIDCIsLess(5, 2, 10, 20, false);
        testWhenConsumerIDCIsLess(5, 2, 10, 0, false);
    }

    @Test
    public void testRun10RandomCase() {
        for (int i = 0; i < 10; i++) {
            int consumerSize = (new Random().nextInt(200)) + 1;// 1-200

            int queueSize = (new Random().nextInt(100)) + 1;// 1-100

            int brokerIDCSize = (new Random().nextInt(10)) + 1;// 1-10

            int consumerIDCSize = (new Random().nextInt(10)) + 1;// 1-10

            if (brokerIDCSize == consumerIDCSize) {
                testWhenIDCSizeEquals(brokerIDCSize, queueSize, consumerSize, false);
            } else
                if (brokerIDCSize > consumerIDCSize) {
                    testWhenConsumerIDCIsLess(brokerIDCSize, (brokerIDCSize - consumerIDCSize), queueSize, consumerSize, false);
                } else {
                    testWhenConsumerIDCIsMore(brokerIDCSize, (consumerIDCSize - brokerIDCSize), queueSize, consumerSize, false);
                }

        }
    }
}

