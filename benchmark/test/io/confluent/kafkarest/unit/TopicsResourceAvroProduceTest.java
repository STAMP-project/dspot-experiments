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
package io.confluent.kafkarest.unit;


import EmbeddedFormat.AVRO;
import Errors.KEY_SCHEMA_MISSING_ERROR_CODE;
import Errors.VALUE_SCHEMA_MISSING_ERROR_CODE;
import com.fasterxml.jackson.databind.JsonNode;
import io.confluent.kafkarest.DefaultKafkaRestContext;
import io.confluent.kafkarest.KafkaRestApplication;
import io.confluent.kafkarest.KafkaRestConfig;
import io.confluent.kafkarest.MetadataObserver;
import io.confluent.kafkarest.ProducerPool;
import io.confluent.kafkarest.RecordMetadataOrException;
import io.confluent.kafkarest.TestUtils;
import io.confluent.kafkarest.entities.AvroTopicProduceRecord;
import io.confluent.kafkarest.entities.PartitionOffset;
import io.confluent.kafkarest.entities.ProduceResponse;
import io.confluent.kafkarest.entities.TopicProduceRequest;
import io.confluent.rest.EmbeddedServerTestHarness;
import io.confluent.rest.RestConfigException;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.avro.Schema;
import org.apache.kafka.common.TopicPartition;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


// This test is much lighter than the Binary one since they would otherwise be mostly duplicated
// -- this just sanity checks the Jersey processing of these requests.
public class TopicsResourceAvroProduceTest extends EmbeddedServerTestHarness<KafkaRestConfig, KafkaRestApplication> {
    private MetadataObserver mdObserver;

    private ProducerPool producerPool;

    private DefaultKafkaRestContext ctx;

    private static final String topicName = "topic1";

    private static final String keySchemaStr = "{\"name\":\"int\",\"type\": \"int\"}";

    private static final String valueSchemaStr = "{\"type\": \"record\", " + (((("\"name\":\"test\"," + "\"fields\":[{") + "  \"name\":\"field\", ") + "  \"type\": \"int\"") + "}]}");

    private static final Schema keySchema = new Schema.Parser().parse(TopicsResourceAvroProduceTest.keySchemaStr);

    private static final Schema valueSchema = new Schema.Parser().parse(TopicsResourceAvroProduceTest.valueSchemaStr);

    private static final JsonNode[] testKeys = new JsonNode[]{ TestUtils.jsonTree("1"), TestUtils.jsonTree("2") };

    private static final JsonNode[] testValues = new JsonNode[]{ TestUtils.jsonTree("{\"field\": 1}"), TestUtils.jsonTree("{\"field\": 2}") };

    private List<AvroTopicProduceRecord> produceRecordsWithPartitionsAndKeys;

    private static final TopicPartition tp0 = new TopicPartition(TopicsResourceAvroProduceTest.topicName, 0);

    private static final TopicPartition tp1 = new TopicPartition(TopicsResourceAvroProduceTest.topicName, 1);

    private List<RecordMetadataOrException> produceResults = Arrays.asList(new RecordMetadataOrException(new org.apache.kafka.clients.producer.RecordMetadata(TopicsResourceAvroProduceTest.tp0, 0L, 0L, 0L, 0L, 1, 1), null), new RecordMetadataOrException(new org.apache.kafka.clients.producer.RecordMetadata(TopicsResourceAvroProduceTest.tp0, 0L, 1L, 0L, 0L, 1, 1), null), new RecordMetadataOrException(new org.apache.kafka.clients.producer.RecordMetadata(TopicsResourceAvroProduceTest.tp1, 0L, 0L, 0L, 0L, 1, 1), null));

    private static final List<PartitionOffset> offsetResults = Arrays.asList(new PartitionOffset(0, 0L, null, null), new PartitionOffset(0, 1L, null, null), new PartitionOffset(1, 0L, null, null));

    public TopicsResourceAvroProduceTest() throws RestConfigException {
        mdObserver = EasyMock.createMock(MetadataObserver.class);
        producerPool = EasyMock.createMock(ProducerPool.class);
        ctx = new DefaultKafkaRestContext(config, mdObserver, producerPool, null, null, null, null);
        addResource(new io.confluent.kafkarest.resources.TopicsResource(ctx));
        produceRecordsWithPartitionsAndKeys = Arrays.asList(new AvroTopicProduceRecord(TopicsResourceAvroProduceTest.testKeys[0], TopicsResourceAvroProduceTest.testValues[0], 0), new AvroTopicProduceRecord(TopicsResourceAvroProduceTest.testKeys[1], TopicsResourceAvroProduceTest.testValues[1], 0));
    }

    @Test
    public void testProduceToTopicWithPartitionAndKey() {
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES) {
            for (String requestMediatype : TestUtils.V1_REQUEST_ENTITY_TYPES_AVRO) {
                final TopicProduceRequest request = new TopicProduceRequest();
                request.setRecords(produceRecordsWithPartitionsAndKeys);
                request.setKeySchema(TopicsResourceAvroProduceTest.keySchemaStr);
                request.setValueSchema(TopicsResourceAvroProduceTest.valueSchemaStr);
                Response rawResponse = produceToTopic(TopicsResourceAvroProduceTest.topicName, mediatype.header, requestMediatype, AVRO, request, produceResults);
                TestUtils.assertOKResponse(rawResponse, mediatype.expected);
                ProduceResponse response = TestUtils.tryReadEntityOrLog(rawResponse, ProduceResponse.class);
                Assert.assertEquals(TopicsResourceAvroProduceTest.offsetResults, response.getOffsets());
                Assert.assertEquals(((Integer) (1)), response.getKeySchemaId());
                Assert.assertEquals(((Integer) (2)), response.getValueSchemaId());
                EasyMock.reset(mdObserver, producerPool);
            }
        }
        // Test using schema IDs
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES) {
            for (String requestMediatype : TestUtils.V1_REQUEST_ENTITY_TYPES_AVRO) {
                final TopicProduceRequest request = new TopicProduceRequest();
                request.setRecords(produceRecordsWithPartitionsAndKeys);
                request.setKeySchemaId(1);
                request.setValueSchemaId(2);
                Response rawResponse = produceToTopic(TopicsResourceAvroProduceTest.topicName, mediatype.header, requestMediatype, AVRO, request, produceResults);
                TestUtils.assertOKResponse(rawResponse, mediatype.expected);
                EasyMock.reset(mdObserver, producerPool);
            }
        }
    }

    @Test
    public void testMissingKeySchema() {
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES) {
            for (String requestMediatype : TestUtils.V1_REQUEST_ENTITY_TYPES_AVRO) {
                final TopicProduceRequest request = new TopicProduceRequest();
                request.setRecords(produceRecordsWithPartitionsAndKeys);
                request.setValueSchema(TopicsResourceAvroProduceTest.valueSchemaStr);
                produceToTopicExpectFailure(TopicsResourceAvroProduceTest.topicName, mediatype.header, requestMediatype, request, mediatype.expected, KEY_SCHEMA_MISSING_ERROR_CODE);
            }
        }
    }

    @Test
    public void testMissingValueSchema() {
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES) {
            for (String requestMediatype : TestUtils.V1_REQUEST_ENTITY_TYPES_AVRO) {
                final TopicProduceRequest request = new TopicProduceRequest();
                request.setRecords(produceRecordsWithPartitionsAndKeys);
                request.setKeySchema(TopicsResourceAvroProduceTest.keySchemaStr);
                produceToTopicExpectFailure(TopicsResourceAvroProduceTest.topicName, mediatype.header, requestMediatype, request, mediatype.expected, VALUE_SCHEMA_MISSING_ERROR_CODE);
            }
        }
    }
}

