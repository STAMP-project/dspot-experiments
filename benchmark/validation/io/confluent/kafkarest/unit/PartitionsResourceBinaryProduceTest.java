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
import io.confluent.kafkarest.AdminClientWrapper;
import io.confluent.kafkarest.DefaultKafkaRestContext;
import io.confluent.kafkarest.KafkaRestApplication;
import io.confluent.kafkarest.KafkaRestConfig;
import io.confluent.kafkarest.ProducerPool;
import io.confluent.kafkarest.RecordMetadataOrException;
import io.confluent.kafkarest.TestUtils;
import io.confluent.kafkarest.entities.BinaryProduceRecord;
import io.confluent.kafkarest.entities.BinaryTopicProduceRecord;
import io.confluent.kafkarest.entities.PartitionOffset;
import io.confluent.kafkarest.entities.ProduceResponse;
import io.confluent.kafkarest.entities.TopicProduceRequest;
import io.confluent.rest.EmbeddedServerTestHarness;
import io.confluent.rest.RestConfigException;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.apache.kafka.common.TopicPartition;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class PartitionsResourceBinaryProduceTest extends EmbeddedServerTestHarness<KafkaRestConfig, KafkaRestApplication> {
    private AdminClientWrapper adminClientWrapper;

    private ProducerPool producerPool;

    private DefaultKafkaRestContext ctx;

    private final String topicName = "topic1";

    private List<BinaryProduceRecord> produceRecordsOnlyValues;

    private List<BinaryProduceRecord> produceRecordsWithKeys;

    private List<RecordMetadataOrException> produceResults;

    private final List<PartitionOffset> offsetResults;

    public PartitionsResourceBinaryProduceTest() throws RestConfigException {
        adminClientWrapper = EasyMock.createMock(AdminClientWrapper.class);
        producerPool = EasyMock.createMock(ProducerPool.class);
        ctx = new DefaultKafkaRestContext(config, null, producerPool, null, null, null, adminClientWrapper);
        addResource(new io.confluent.kafkarest.resources.TopicsResource(ctx));
        addResource(new io.confluent.kafkarest.resources.PartitionsResource(ctx));
        produceRecordsOnlyValues = Arrays.asList(new BinaryProduceRecord("value".getBytes()), new BinaryProduceRecord("value2".getBytes()));
        produceRecordsWithKeys = Arrays.asList(new BinaryProduceRecord("key".getBytes(), "value".getBytes()), new BinaryProduceRecord("key2".getBytes(), "value2".getBytes()));
        TopicPartition tp0 = new TopicPartition(topicName, 0);
        produceResults = Arrays.asList(new RecordMetadataOrException(new org.apache.kafka.clients.producer.RecordMetadata(tp0, 0L, 0L, 0L, 0L, 1, 1), null), new RecordMetadataOrException(new org.apache.kafka.clients.producer.RecordMetadata(tp0, 0L, 1L, 0L, 0L, 1, 1), null));
        offsetResults = Arrays.asList(new PartitionOffset(0, 0L, null, null), new PartitionOffset(0, 1L, null, null));
    }

    @Test
    public void testProduceToPartitionOnlyValues() {
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES) {
            for (String requestMediatype : TestUtils.V1_REQUEST_ENTITY_TYPES_BINARY) {
                Response rawResponse = produceToPartition(topicName, 0, mediatype.header, requestMediatype, BINARY, produceRecordsOnlyValues, produceResults);
                TestUtils.assertOKResponse(rawResponse, mediatype.expected);
                ProduceResponse response = TestUtils.tryReadEntityOrLog(rawResponse, ProduceResponse.class);
                Assert.assertEquals(offsetResults, response.getOffsets());
                Assert.assertEquals(null, response.getKeySchemaId());
                Assert.assertEquals(null, response.getValueSchemaId());
                EasyMock.reset(adminClientWrapper, producerPool);
            }
        }
    }

    @Test
    public void testProduceToPartitionByKey() {
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES) {
            for (String requestMediatype : TestUtils.V1_REQUEST_ENTITY_TYPES_BINARY) {
                Response rawResponse = produceToPartition(topicName, 0, mediatype.header, requestMediatype, BINARY, produceRecordsWithKeys, produceResults);
                TestUtils.assertOKResponse(rawResponse, mediatype.expected);
                ProduceResponse response = TestUtils.tryReadEntityOrLog(rawResponse, ProduceResponse.class);
                Assert.assertEquals(offsetResults, response.getOffsets());
                Assert.assertEquals(null, response.getKeySchemaId());
                Assert.assertEquals(null, response.getValueSchemaId());
                EasyMock.reset(adminClientWrapper, producerPool);
            }
        }
    }

    @Test
    public void testProduceToPartitionFailure() {
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES) {
            for (String requestMediatype : TestUtils.V1_REQUEST_ENTITY_TYPES_BINARY) {
                // null offsets triggers a generic exception
                Response rawResponse = produceToPartition(topicName, 0, mediatype.header, requestMediatype, BINARY, produceRecordsWithKeys, null);
                TestUtils.assertErrorResponse(INTERNAL_SERVER_ERROR, rawResponse, mediatype.expected);
                EasyMock.reset(adminClientWrapper, producerPool);
            }
        }
    }

    @Test
    public void testProduceInvalidRequest() {
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES) {
            for (String requestMediatype : TestUtils.V1_REQUEST_ENTITY_TYPES_BINARY) {
                EasyMock.expect(adminClientWrapper.topicExists(topicName)).andReturn(true);
                EasyMock.replay(adminClientWrapper);
                Response response = request((("/topics/" + (topicName)) + "/partitions/0"), mediatype.header).post(Entity.entity("{}", requestMediatype));
                TestUtils.assertErrorResponse(UNPROCESSABLE_ENTITY, response, UNPROCESSABLE_ENTITY_CODE, null, mediatype.expected);
                EasyMock.verify();
                // Invalid base64 encoding
                response = request((("/topics/" + (topicName)) + "/partitions/0"), mediatype.header).post(Entity.entity("{\"records\":[{\"value\":\"aGVsbG8==\"}]}", requestMediatype));
                TestUtils.assertErrorResponse(UNPROCESSABLE_ENTITY, response, UNPROCESSABLE_ENTITY_CODE, null, mediatype.expected);
                EasyMock.verify();
                // Invalid data -- include partition in request
                TopicProduceRequest topicRequest = new TopicProduceRequest();
                topicRequest.setRecords(Arrays.asList(new BinaryTopicProduceRecord("key".getBytes(), "value".getBytes(), 0)));
                response = request((("/topics/" + (topicName)) + "/partitions/0"), mediatype.header).post(Entity.entity(topicRequest, requestMediatype));
                TestUtils.assertErrorResponse(UNPROCESSABLE_ENTITY, response, UNPROCESSABLE_ENTITY_CODE, null, mediatype.expected);
                EasyMock.verify();
                EasyMock.reset(adminClientWrapper, producerPool);
            }
        }
    }
}

