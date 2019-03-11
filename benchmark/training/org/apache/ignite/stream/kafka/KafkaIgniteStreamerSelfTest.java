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
package org.apache.ignite.stream.kafka;


import java.util.Map;
import java.util.concurrent.TimeoutException;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests {@link KafkaStreamer}.
 */
public class KafkaIgniteStreamerSelfTest extends GridCommonAbstractTest {
    /**
     * Embedded Kafka.
     */
    private TestKafkaBroker embeddedBroker;

    /**
     * Count.
     */
    private static final int CNT = 100;

    /**
     * Test topic.
     */
    private static final String TOPIC_NAME = "page_visits";

    /**
     * Kafka partition.
     */
    private static final int PARTITIONS = 4;

    /**
     * Kafka replication factor.
     */
    private static final int REPLICATION_FACTOR = 1;

    /**
     * Topic message key prefix.
     */
    private static final String KEY_PREFIX = "192.168.2.";

    /**
     * Topic message value URL.
     */
    private static final String VALUE_URL = ",www.example.com,";

    /**
     * Constructor.
     */
    public KafkaIgniteStreamerSelfTest() {
        super(true);
    }

    /**
     * Tests Kafka streamer.
     *
     * @throws TimeoutException
     * 		If timed out.
     * @throws InterruptedException
     * 		If interrupted.
     */
    @Test
    public void testKafkaStreamer() throws InterruptedException, TimeoutException {
        embeddedBroker.createTopic(KafkaIgniteStreamerSelfTest.TOPIC_NAME, KafkaIgniteStreamerSelfTest.PARTITIONS, KafkaIgniteStreamerSelfTest.REPLICATION_FACTOR);
        Map<String, String> keyValMap = produceStream(KafkaIgniteStreamerSelfTest.TOPIC_NAME);
        consumerStream(KafkaIgniteStreamerSelfTest.TOPIC_NAME, keyValMap);
    }
}

