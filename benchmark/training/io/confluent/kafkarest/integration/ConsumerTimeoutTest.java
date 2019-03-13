/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.kafkarest.integration;


import EmbeddedFormat.BINARY;
import Versions.KAFKA_V1_JSON_BINARY;
import io.confluent.kafkarest.entities.BinaryConsumerRecord;
import org.junit.Test;


public class ConsumerTimeoutTest extends AbstractConsumerTest {
    private static final String topicName = "test";

    private static final String groupName = "testconsumergroup";

    private static final Integer requestTimeout = 500;

    // This is pretty large since there is sometimes significant overhead to doing a read (e.g.
    // checking topic existence in ZK)
    private static final Integer instanceTimeout = 1000;

    private static final Integer slackTime = 1100;

    @Test
    public void testConsumerTimeout() throws InterruptedException {
        String instanceUri = startConsumeMessages(ConsumerTimeoutTest.groupName, ConsumerTimeoutTest.topicName, BINARY, KAFKA_V1_JSON_BINARY);
        // Even with identical timeouts, should be able to consume multiple times without the
        // instance timing out
        consumeForTimeout(instanceUri, ConsumerTimeoutTest.topicName, KAFKA_V1_JSON_BINARY, KAFKA_V1_JSON_BINARY, new javax.ws.rs.core.GenericType<java.util.List<BinaryConsumerRecord>>() {});
        consumeForTimeout(instanceUri, ConsumerTimeoutTest.topicName, KAFKA_V1_JSON_BINARY, KAFKA_V1_JSON_BINARY, new javax.ws.rs.core.GenericType<java.util.List<BinaryConsumerRecord>>() {});
        // Then sleep long enough for it to expire
        Thread.sleep(((ConsumerTimeoutTest.instanceTimeout) + (ConsumerTimeoutTest.slackTime)));
        consumeForNotFoundError(instanceUri, ConsumerTimeoutTest.topicName);
    }
}

