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


import ConstraintViolationExceptionMapper.UNPROCESSABLE_ENTITY;
import ConstraintViolationExceptionMapper.UNPROCESSABLE_ENTITY_CODE;
import EmbeddedFormat.BINARY;
import Response.Status.INTERNAL_SERVER_ERROR;
import io.confluent.kafkarest.DefaultKafkaRestContext;
import io.confluent.kafkarest.Errors;
import io.confluent.kafkarest.KafkaRestApplication;
import io.confluent.kafkarest.KafkaRestConfig;
import io.confluent.kafkarest.MetadataObserver;
import io.confluent.kafkarest.ProducerPool;
import io.confluent.kafkarest.RecordMetadataOrException;
import io.confluent.kafkarest.TestUtils;
import io.confluent.kafkarest.entities.BinaryTopicProduceRecord;
import io.confluent.kafkarest.entities.PartitionOffset;
import io.confluent.rest.EmbeddedServerTestHarness;
import io.confluent.rest.RestConfigException;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.RetriableException;
import org.easymock.EasyMock;
import org.junit.Test;


public class TopicsResourceBinaryProduceTest extends EmbeddedServerTestHarness<KafkaRestConfig, KafkaRestApplication> {
    private MetadataObserver mdObserver;

    private ProducerPool producerPool;

    private DefaultKafkaRestContext ctx;

    private static final String topicName = "topic1";

    private List<BinaryTopicProduceRecord> produceRecordsOnlyValues;

    private List<BinaryTopicProduceRecord> produceRecordsWithKeys;

    private List<BinaryTopicProduceRecord> produceRecordsWithPartitions;

    private List<BinaryTopicProduceRecord> produceRecordsWithPartitionsAndKeys;

    private List<BinaryTopicProduceRecord> produceRecordsWithNullValues;

    private List<RecordMetadataOrException> produceResults;

    private List<PartitionOffset> offsetResults;

    private List<BinaryTopicProduceRecord> produceExceptionData;

    private List<RecordMetadataOrException> produceGenericExceptionResults;

    private List<RecordMetadataOrException> produceKafkaExceptionResults;

    private List<PartitionOffset> kafkaExceptionResults;

    private List<RecordMetadataOrException> produceKafkaRetriableExceptionResults;

    private List<PartitionOffset> kafkaRetriableExceptionResults;

    private static final String exceptionMessage = "Error message";

    public TopicsResourceBinaryProduceTest() throws RestConfigException {
        mdObserver = EasyMock.createMock(MetadataObserver.class);
        producerPool = EasyMock.createMock(ProducerPool.class);
        ctx = new DefaultKafkaRestContext(config, mdObserver, producerPool, null, null, null, null);
        addResource(new io.confluent.kafkarest.resources.TopicsResource(ctx));
        produceRecordsOnlyValues = Arrays.asList(new BinaryTopicProduceRecord("value".getBytes()), new BinaryTopicProduceRecord("value2".getBytes()));
        produceRecordsWithKeys = Arrays.asList(new BinaryTopicProduceRecord("key".getBytes(), "value".getBytes()), new BinaryTopicProduceRecord("key2".getBytes(), "value2".getBytes()));
        produceRecordsWithPartitions = Arrays.asList(new BinaryTopicProduceRecord("value".getBytes(), 0), new BinaryTopicProduceRecord("value2".getBytes(), 0));
        produceRecordsWithPartitionsAndKeys = Arrays.asList(new BinaryTopicProduceRecord("key".getBytes(), "value".getBytes(), 0), new BinaryTopicProduceRecord("key2".getBytes(), "value2".getBytes(), 0));
        produceRecordsWithNullValues = Arrays.asList(new BinaryTopicProduceRecord("key".getBytes(), ((byte[]) (null))), new BinaryTopicProduceRecord("key2".getBytes(), ((byte[]) (null))));
        TopicPartition tp0 = new TopicPartition(TopicsResourceBinaryProduceTest.topicName, 0);
        produceResults = Arrays.asList(new RecordMetadataOrException(new org.apache.kafka.clients.producer.RecordMetadata(tp0, 0L, 0L, 0L, 0L, 1, 1), null), new RecordMetadataOrException(new org.apache.kafka.clients.producer.RecordMetadata(tp0, 0L, 1L, 0L, 0L, 1, 1), null));
        offsetResults = Arrays.asList(new PartitionOffset(0, 0L, null, null), new PartitionOffset(0, 1L, null, null));
        produceExceptionData = Arrays.asList(new BinaryTopicProduceRecord(((byte[]) (null)), ((byte[]) (null))));
        produceGenericExceptionResults = Arrays.asList(new RecordMetadataOrException(null, new Exception(TopicsResourceBinaryProduceTest.exceptionMessage)));
        produceKafkaExceptionResults = Arrays.asList(new RecordMetadataOrException(null, new KafkaException(TopicsResourceBinaryProduceTest.exceptionMessage)));
        kafkaExceptionResults = Arrays.asList(new PartitionOffset(null, null, Errors.KAFKA_ERROR_ERROR_CODE, TopicsResourceBinaryProduceTest.exceptionMessage));
        produceKafkaRetriableExceptionResults = Arrays.asList(new RecordMetadataOrException(null, new RetriableException(TopicsResourceBinaryProduceTest.exceptionMessage) {}));
        kafkaRetriableExceptionResults = Arrays.asList(new PartitionOffset(null, null, Errors.KAFKA_RETRIABLE_ERROR_ERROR_CODE, TopicsResourceBinaryProduceTest.exceptionMessage));
    }

    @Test
    public void testProduceToTopicOnlyValues() {
        testProduceToTopicSuccess(produceRecordsOnlyValues);
    }

    @Test
    public void testProduceToTopicByKey() {
        testProduceToTopicSuccess(produceRecordsWithKeys);
    }

    @Test
    public void testProduceToTopicByPartition() {
        testProduceToTopicSuccess(produceRecordsWithPartitions);
    }

    @Test
    public void testProduceToTopicWithPartitionAndKey() {
        testProduceToTopicSuccess(produceRecordsWithPartitionsAndKeys);
    }

    @Test
    public void testProduceToTopicWithNullValues() {
        testProduceToTopicSuccess(produceRecordsWithNullValues);
    }

    @Test
    public void testProduceToTopicFailure() {
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES) {
            for (String requestMediatype : TestUtils.V1_REQUEST_ENTITY_TYPES_BINARY) {
                // null offsets triggers a generic exception
                Response rawResponse = produceToTopic("topic1", mediatype.header, requestMediatype, BINARY, produceRecordsWithKeys, null);
                TestUtils.assertErrorResponse(INTERNAL_SERVER_ERROR, rawResponse, mediatype.expected);
                EasyMock.reset(mdObserver, producerPool);
            }
        }
    }

    @Test
    public void testProduceInvalidRequest() {
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES) {
            for (String requestMediatype : TestUtils.V1_REQUEST_ENTITY_TYPES_BINARY) {
                Response response = request("/topics/topic1", mediatype.header).post(Entity.entity("{}", requestMediatype));
                TestUtils.assertErrorResponse(UNPROCESSABLE_ENTITY, response, UNPROCESSABLE_ENTITY_CODE, null, mediatype.expected);
                // Invalid base64 encoding
                response = request("/topics/topic1", mediatype.header).post(Entity.entity("{\"records\":[{\"value\":\"aGVsbG8==\"}]}", requestMediatype));
                TestUtils.assertErrorResponse(UNPROCESSABLE_ENTITY, response, UNPROCESSABLE_ENTITY_CODE, null, mediatype.expected);
            }
        }
    }

    @Test
    public void testProduceToTopicGenericException() {
        // No results expected since a non-Kafka exception should cause an HTTP-level error
        testProduceToTopicException(produceGenericExceptionResults, null);
    }

    @Test
    public void testProduceToTopicKafkaException() {
        testProduceToTopicException(produceKafkaExceptionResults, kafkaExceptionResults);
    }

    @Test
    public void testProduceToTopicKafkaRetriableException() {
        testProduceToTopicException(produceKafkaRetriableExceptionResults, kafkaRetriableExceptionResults);
    }
}

