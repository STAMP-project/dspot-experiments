/**
 * Copyright (c) 2010-2019. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.axonserver.connector.query;


import io.axoniq.axonserver.grpc.query.QueryResponse;
import java.util.Objects;
import java.util.Optional;
import org.axonframework.axonserver.connector.AxonServerConfiguration;
import org.axonframework.messaging.MetaData;
import org.axonframework.queryhandling.GenericQueryResponseMessage;
import org.axonframework.queryhandling.QueryResponseMessage;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.junit.Assert;
import org.junit.Test;


public class GrpcBackedResponseMessageTest {
    private static final GrpcBackedResponseMessageTest.TestQueryResponse TEST_QUERY_RESPONSE = new GrpcBackedResponseMessageTest.TestQueryResponse("aggregateId", 42);

    private static final String REQUEST_MESSAGE_ID = "request-message-id";

    private final Serializer serializer = XStreamSerializer.defaultSerializer();

    private final QuerySerializer querySerializer = new QuerySerializer(serializer, serializer, new AxonServerConfiguration());

    @Test
    public void testGetIdentifierReturnsTheSameIdentifierAsSpecifiedInTheQueryResponseMessage() {
        QueryResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testQueryResponseMessage = GenericQueryResponseMessage.asResponseMessage(GrpcBackedResponseMessageTest.TEST_QUERY_RESPONSE);
        QueryResponse testQueryResponse = querySerializer.serializeResponse(testQueryResponseMessage, GrpcBackedResponseMessageTest.REQUEST_MESSAGE_ID);
        GrpcBackedResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testSubject = new GrpcBackedResponseMessage(testQueryResponse, serializer);
        Assert.assertEquals(testQueryResponse.getMessageIdentifier(), testSubject.getIdentifier());
    }

    @Test
    public void testGetMetaDataReturnsTheSameMapAsWasInsertedInTheQueryResponseMessage() {
        MetaData expectedMetaData = MetaData.with("some-key", "some-value");
        QueryResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testQueryResponseMessage = GenericQueryResponseMessage.<GrpcBackedResponseMessageTest.TestQueryResponse>asResponseMessage(GrpcBackedResponseMessageTest.TEST_QUERY_RESPONSE).withMetaData(expectedMetaData);
        QueryResponse testQueryResponse = querySerializer.serializeResponse(testQueryResponseMessage, GrpcBackedResponseMessageTest.REQUEST_MESSAGE_ID);
        GrpcBackedResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testSubject = new GrpcBackedResponseMessage(testQueryResponse, serializer);
        Assert.assertEquals(expectedMetaData, testSubject.getMetaData());
    }

    @Test
    public void testGetPayloadReturnsAnIdenticalObjectAsInsertedThroughTheQueryResponseMessage() {
        GrpcBackedResponseMessageTest.TestQueryResponse expectedQuery = GrpcBackedResponseMessageTest.TEST_QUERY_RESPONSE;
        QueryResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testQueryResponseMessage = GenericQueryResponseMessage.asResponseMessage(expectedQuery);
        QueryResponse testQueryResponse = querySerializer.serializeResponse(testQueryResponseMessage, GrpcBackedResponseMessageTest.REQUEST_MESSAGE_ID);
        GrpcBackedResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testSubject = new GrpcBackedResponseMessage(testQueryResponse, serializer);
        Assert.assertEquals(expectedQuery, testSubject.getPayload());
    }

    @Test
    public void testGetPayloadReturnsNullIfTheQueryResponseMessageDidNotContainAnyPayload() {
        QueryResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testQueryResponseMessage = GenericQueryResponseMessage.asResponseMessage(GrpcBackedResponseMessageTest.TestQueryResponse.class, new IllegalArgumentException("some-exception"));
        QueryResponse testQueryResponse = querySerializer.serializeResponse(testQueryResponseMessage, GrpcBackedResponseMessageTest.REQUEST_MESSAGE_ID);
        GrpcBackedResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testSubject = new GrpcBackedResponseMessage(testQueryResponse, serializer);
        Assert.assertNull(testSubject.getPayload());
    }

    @Test
    public void testGetPayloadTypeReturnsTheTypeOfTheInsertedQueryResponseMessage() {
        QueryResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testQueryResponseMessage = GenericQueryResponseMessage.asResponseMessage(GrpcBackedResponseMessageTest.TEST_QUERY_RESPONSE);
        QueryResponse testQueryResponse = querySerializer.serializeResponse(testQueryResponseMessage, GrpcBackedResponseMessageTest.REQUEST_MESSAGE_ID);
        GrpcBackedResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testSubject = new GrpcBackedResponseMessage(testQueryResponse, serializer);
        Assert.assertEquals(GrpcBackedResponseMessageTest.TestQueryResponse.class, testSubject.getPayloadType());
    }

    @Test
    public void testGetPayloadTypeReturnsNullIfTheQueryResponseMessageDidNotContainAnyPayload() {
        QueryResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testQueryResponseMessage = GenericQueryResponseMessage.asResponseMessage(GrpcBackedResponseMessageTest.TestQueryResponse.class, new IllegalArgumentException("some-exception"));
        QueryResponse testQueryResponse = querySerializer.serializeResponse(testQueryResponseMessage, GrpcBackedResponseMessageTest.REQUEST_MESSAGE_ID);
        GrpcBackedResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testSubject = new GrpcBackedResponseMessage(testQueryResponse, serializer);
        Assert.assertNull(testSubject.getPayloadType());
    }

    @Test
    public void testIsExceptionalReturnsTrueForAnExceptionalQueryResponseMessage() {
        QueryResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testQueryResponseMessage = GenericQueryResponseMessage.asResponseMessage(GrpcBackedResponseMessageTest.TestQueryResponse.class, new IllegalArgumentException("some-exception"));
        QueryResponse testQueryResponse = querySerializer.serializeResponse(testQueryResponseMessage, GrpcBackedResponseMessageTest.REQUEST_MESSAGE_ID);
        GrpcBackedResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testSubject = new GrpcBackedResponseMessage(testQueryResponse, serializer);
        Assert.assertTrue(testSubject.isExceptional());
    }

    @Test
    public void testOptionalExceptionResultReturnsTheExceptionAsAsInsertedThroughTheQueryResponseMessage() {
        IllegalArgumentException expectedException = new IllegalArgumentException("some-exception");
        QueryResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testQueryResponseMessage = GenericQueryResponseMessage.asResponseMessage(GrpcBackedResponseMessageTest.TestQueryResponse.class, expectedException);
        QueryResponse testQueryResponse = querySerializer.serializeResponse(testQueryResponseMessage, GrpcBackedResponseMessageTest.REQUEST_MESSAGE_ID);
        GrpcBackedResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testSubject = new GrpcBackedResponseMessage(testQueryResponse, serializer);
        Optional<Throwable> result = testSubject.optionalExceptionResult();
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(expectedException.getMessage(), result.get().getMessage());
    }

    @Test
    public void testWithMetaDataCompletelyReplacesTheInitialMetaDataMap() {
        MetaData testMetaData = MetaData.with("some-key", "some-value");
        QueryResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testQueryResponseMessage = GenericQueryResponseMessage.<GrpcBackedResponseMessageTest.TestQueryResponse>asResponseMessage(GrpcBackedResponseMessageTest.TEST_QUERY_RESPONSE).withMetaData(testMetaData);
        QueryResponse testQueryResponse = querySerializer.serializeResponse(testQueryResponseMessage, GrpcBackedResponseMessageTest.REQUEST_MESSAGE_ID);
        GrpcBackedResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testSubject = new GrpcBackedResponseMessage(testQueryResponse, serializer);
        MetaData replacementMetaData = MetaData.with("some-other-key", "some-other-value");
        testSubject = testSubject.withMetaData(replacementMetaData);
        MetaData resultMetaData = testSubject.getMetaData();
        Assert.assertFalse(resultMetaData.containsKey(testMetaData.keySet().iterator().next()));
        Assert.assertEquals(replacementMetaData, resultMetaData);
    }

    @Test
    public void testAndMetaDataAppendsToTheExistingMetaData() {
        MetaData testMetaData = MetaData.with("some-key", "some-value");
        QueryResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testQueryResponseMessage = GenericQueryResponseMessage.<GrpcBackedResponseMessageTest.TestQueryResponse>asResponseMessage(GrpcBackedResponseMessageTest.TEST_QUERY_RESPONSE).withMetaData(testMetaData);
        QueryResponse testQueryResponse = querySerializer.serializeResponse(testQueryResponseMessage, GrpcBackedResponseMessageTest.REQUEST_MESSAGE_ID);
        GrpcBackedResponseMessage<GrpcBackedResponseMessageTest.TestQueryResponse> testSubject = new GrpcBackedResponseMessage(testQueryResponse, serializer);
        MetaData additionalMetaData = MetaData.with("some-other-key", "some-other-value");
        testSubject = testSubject.andMetaData(additionalMetaData);
        MetaData resultMetaData = testSubject.getMetaData();
        Assert.assertTrue(resultMetaData.containsKey(testMetaData.keySet().iterator().next()));
        Assert.assertTrue(resultMetaData.containsKey(additionalMetaData.keySet().iterator().next()));
    }

    private static class TestQueryResponse {
        private final String queryModelId;

        private final int someFilterValue;

        private TestQueryResponse(String queryModelId, int someFilterValue) {
            this.queryModelId = queryModelId;
            this.someFilterValue = someFilterValue;
        }

        @SuppressWarnings("unused")
        public String getQueryModelId() {
            return queryModelId;
        }

        @SuppressWarnings("unused")
        public int getSomeFilterValue() {
            return someFilterValue;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            GrpcBackedResponseMessageTest.TestQueryResponse that = ((GrpcBackedResponseMessageTest.TestQueryResponse) (o));
            return ((someFilterValue) == (that.someFilterValue)) && (Objects.equals(queryModelId, that.queryModelId));
        }

        @Override
        public int hashCode() {
            return Objects.hash(queryModelId, someFilterValue);
        }
    }
}

