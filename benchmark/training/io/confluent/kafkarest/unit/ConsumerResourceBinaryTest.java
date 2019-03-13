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


import Invocation.Builder;
import io.confluent.kafkarest.BinaryConsumerState;
import io.confluent.kafkarest.TestUtils;
import io.confluent.kafkarest.Versions;
import io.confluent.kafkarest.entities.BinaryConsumerRecord;
import io.confluent.kafkarest.entities.ConsumerRecord;
import io.confluent.kafkarest.entities.CreateConsumerInstanceResponse;
import io.confluent.kafkarest.entities.EmbeddedFormat;
import io.confluent.kafkarest.entities.TopicPartitionOffset;
import io.confluent.rest.RestConfigException;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class ConsumerResourceBinaryTest extends AbstractConsumerResourceTest {
    public ConsumerResourceBinaryTest() throws RestConfigException {
        super();
    }

    @Test
    public void testReadCommit() {
        List<? extends ConsumerRecord<byte[], byte[]>> expectedReadLimit = Arrays.asList(new BinaryConsumerRecord(AbstractConsumerResourceTest.topicName, "key1".getBytes(), "value1".getBytes(), 0, 10));
        List<? extends ConsumerRecord<byte[], byte[]>> expectedReadNoLimit = Arrays.asList(new BinaryConsumerRecord(AbstractConsumerResourceTest.topicName, "key2".getBytes(), "value2".getBytes(), 1, 15), new BinaryConsumerRecord(AbstractConsumerResourceTest.topicName, "key3".getBytes(), "value3".getBytes(), 2, 20));
        List<TopicPartitionOffset> expectedOffsets = Arrays.asList(new TopicPartitionOffset(AbstractConsumerResourceTest.topicName, 0, 10, 10), new TopicPartitionOffset(AbstractConsumerResourceTest.topicName, 1, 15, 15), new TopicPartitionOffset(AbstractConsumerResourceTest.topicName, 2, 20, 20));
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES_BINARY) {
            for (String requestMediatype : TestUtils.V1_REQUEST_ENTITY_TYPES) {
                expectCreateGroup(new io.confluent.kafkarest.entities.ConsumerInstanceConfig(EmbeddedFormat.BINARY));
                expectReadTopic(AbstractConsumerResourceTest.topicName, BinaryConsumerState.class, 10, expectedReadLimit, null);
                expectReadTopic(AbstractConsumerResourceTest.topicName, BinaryConsumerState.class, expectedReadNoLimit, null);
                expectCommit(expectedOffsets, null);
                EasyMock.replay(consumerManager);
                Response response = request(("/consumers/" + (AbstractConsumerResourceTest.groupName)), mediatype.header).post(Entity.entity(null, requestMediatype));
                TestUtils.assertOKResponse(response, mediatype.expected);
                final CreateConsumerInstanceResponse createResponse = TestUtils.tryReadEntityOrLog(response, CreateConsumerInstanceResponse.class);
                // Read with size limit
                String readUrl = ((instanceBasePath(createResponse)) + "/topics/") + (AbstractConsumerResourceTest.topicName);
                Invocation.Builder builder = getJerseyTest().target(readUrl).queryParam("max_bytes", 10).request();
                if ((mediatype.header) != null) {
                    builder.accept(mediatype.header);
                }
                Response readLimitResponse = builder.get();
                // Most specific default is different when retrieving embedded data
                String expectedMediatype = ((mediatype.header) != null) ? mediatype.expected : Versions.KAFKA_V1_JSON_BINARY;
                TestUtils.assertOKResponse(readLimitResponse, expectedMediatype);
                final List<BinaryConsumerRecord> readLimitResponseRecords = TestUtils.tryReadEntityOrLog(readLimitResponse, new javax.ws.rs.core.GenericType<List<BinaryConsumerRecord>>() {});
                Assert.assertEquals(expectedReadLimit, readLimitResponseRecords);
                // Read without size limit
                Response readResponse = request(readUrl, mediatype.header).get();
                TestUtils.assertOKResponse(readResponse, expectedMediatype);
                final List<BinaryConsumerRecord> readResponseRecords = TestUtils.tryReadEntityOrLog(readResponse, new javax.ws.rs.core.GenericType<List<BinaryConsumerRecord>>() {});
                Assert.assertEquals(expectedReadNoLimit, readResponseRecords);
                String commitUrl = (instanceBasePath(createResponse)) + "/offsets/";
                Response commitResponse = request(commitUrl, mediatype.header).post(Entity.entity(null, requestMediatype));
                TestUtils.assertOKResponse(response, mediatype.expected);
                final List<TopicPartitionOffset> committedOffsets = TestUtils.tryReadEntityOrLog(commitResponse, new javax.ws.rs.core.GenericType<List<TopicPartitionOffset>>() {});
                Assert.assertEquals(expectedOffsets, committedOffsets);
                EasyMock.verify(consumerManager);
                EasyMock.reset(consumerManager);
            }
        }
    }
}

