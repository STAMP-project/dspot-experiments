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


import io.confluent.kafkarest.entities.BinaryTopicProduceRecord;
import io.confluent.kafkarest.entities.PartitionOffset;
import java.util.Arrays;
import java.util.List;
import kafka.serializer.Decoder;
import kafka.serializer.DefaultDecoder;
import org.junit.Test;


public class ProducerTopicAutoCreationTest extends AbstractProducerTest {
    private static final String topicName = "nonexistant";

    private static final Decoder<byte[]> binaryDecoder = new DefaultDecoder(null);

    private final List<BinaryTopicProduceRecord> topicRecords = Arrays.asList(new BinaryTopicProduceRecord("key".getBytes(), "value".getBytes()), new BinaryTopicProduceRecord("key".getBytes(), "value2".getBytes()), new BinaryTopicProduceRecord("key".getBytes(), "value3".getBytes()), new BinaryTopicProduceRecord("key".getBytes(), "value4".getBytes()));

    private final List<PartitionOffset> partitionOffsets = Arrays.asList(new PartitionOffset(0, 0L, null, null), new PartitionOffset(0, 1L, null, null), new PartitionOffset(0, 2L, null, null), new PartitionOffset(0, 3L, null, null));

    @Test
    public void testProduceToMissingTopic() {
        // Should create topic
        testProduceToTopic(ProducerTopicAutoCreationTest.topicName, topicRecords, ProducerTopicAutoCreationTest.binaryDecoder, ProducerTopicAutoCreationTest.binaryDecoder, partitionOffsets, false);
    }
}

