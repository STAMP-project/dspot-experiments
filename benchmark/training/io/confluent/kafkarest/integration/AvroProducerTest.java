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


import com.fasterxml.jackson.databind.JsonNode;
import io.confluent.kafka.serializers.KafkaAvroDecoder;
import io.confluent.kafkarest.TestUtils;
import io.confluent.kafkarest.entities.AvroProduceRecord;
import io.confluent.kafkarest.entities.AvroTopicProduceRecord;
import io.confluent.kafkarest.entities.Partition;
import io.confluent.kafkarest.entities.PartitionOffset;
import io.confluent.kafkarest.entities.PartitionReplica;
import java.util.Arrays;
import java.util.List;
import org.apache.avro.Schema;
import org.junit.Test;


// This test is much lighter than the Binary one which exercises all variants. Since binary
// covers most code paths well, this just tries to exercise Avro-specific parts.
public class AvroProducerTest extends ClusterTestHarness {
    private static final String topicName = "topic1";

    private static final List<Partition> partitions = Arrays.asList(new Partition(0, 0, Arrays.asList(new PartitionReplica(0, true, true), new PartitionReplica(1, false, false))));

    private KafkaAvroDecoder avroDecoder;

    // This test assumes that AvroConverterTest is good enough and testing one primitive type for
    // keys and one complex type for records is sufficient.
    private static final String keySchemaStr = "{\"name\":\"int\",\"type\": \"int\"}";

    private static final String valueSchemaStr = "{\"type\": \"record\", " + (((("\"name\":\"test\"," + "\"fields\":[{") + "  \"name\":\"field\", ") + "  \"type\": \"int\"") + "}]}");

    private static final Schema valueSchema = new Schema.Parser().parse(AvroProducerTest.valueSchemaStr);

    private static final JsonNode[] testKeys = new JsonNode[]{ TestUtils.jsonTree("1"), TestUtils.jsonTree("2"), TestUtils.jsonTree("3"), TestUtils.jsonTree("4") };

    private static final JsonNode[] testValues = new JsonNode[]{ TestUtils.jsonTree("{\"field\": 1}"), TestUtils.jsonTree("{\"field\": 2}"), TestUtils.jsonTree("{\"field\": 3}"), TestUtils.jsonTree("{\"field\": 4}") };

    // Produce to topic inputs & results
    private final List<AvroTopicProduceRecord> topicRecordsWithPartitionsAndKeys = Arrays.asList(new AvroTopicProduceRecord(AvroProducerTest.testKeys[0], AvroProducerTest.testValues[0], 0), new AvroTopicProduceRecord(AvroProducerTest.testKeys[1], AvroProducerTest.testValues[1], 1), new AvroTopicProduceRecord(AvroProducerTest.testKeys[2], AvroProducerTest.testValues[2], 1), new AvroTopicProduceRecord(AvroProducerTest.testKeys[3], AvroProducerTest.testValues[3], 2));

    private final List<PartitionOffset> partitionOffsetsWithPartitionsAndKeys = Arrays.asList(new PartitionOffset(0, 0L, null, null), new PartitionOffset(0, 1L, null, null), new PartitionOffset(1, 0L, null, null), new PartitionOffset(1, 1L, null, null));

    // Produce to partition inputs & results
    private final List<AvroProduceRecord> partitionRecordsOnlyValues = Arrays.asList(new AvroProduceRecord(AvroProducerTest.testValues[0]), new AvroProduceRecord(AvroProducerTest.testValues[1]), new AvroProduceRecord(AvroProducerTest.testValues[2]), new AvroProduceRecord(AvroProducerTest.testValues[3]));

    private final List<PartitionOffset> producePartitionOffsetOnlyValues = Arrays.asList(new PartitionOffset(0, 0L, null, null), new PartitionOffset(0, 1L, null, null), new PartitionOffset(0, 2L, null, null), new PartitionOffset(0, 3L, null, null));

    public AvroProducerTest() {
        super(1, true);
    }

    @Test
    public void testProduceToTopicWithPartitionsAndKeys() {
        testProduceToTopic(topicRecordsWithPartitionsAndKeys, partitionOffsetsWithPartitionsAndKeys);
    }

    @Test
    public void testProduceToPartitionOnlyValues() {
        testProduceToPartition(partitionRecordsOnlyValues, producePartitionOffsetOnlyValues);
    }
}

