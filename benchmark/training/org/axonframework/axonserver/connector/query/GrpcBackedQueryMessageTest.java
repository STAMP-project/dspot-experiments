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


import io.axoniq.axonserver.grpc.query.QueryRequest;
import java.util.Objects;
import org.axonframework.axonserver.connector.AxonServerConfiguration;
import org.axonframework.messaging.MetaData;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryMessage;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test all the functions provided on the {@link GrpcBackedQueryMessage}. The {@link QueryRequest} to be passed to a
 * GrpcBackedQueryMessage is created by using the {@link QuerySerializer}.
 *
 * @author Steven van Beelen
 */
public class GrpcBackedQueryMessageTest {
    private static final GrpcBackedQueryMessageTest.TestQuery TEST_QUERY = new GrpcBackedQueryMessageTest.TestQuery("aggregateId", 42);

    private static final ResponseType<String> RESPONSE_TYPE = ResponseTypes.instanceOf(String.class);

    private static final int NUMBER_OF_RESULTS = 42;

    private static final int TIMEOUT = 1000;

    private static final int PRIORITY = 1;

    private final Serializer serializer = XStreamSerializer.defaultSerializer();

    private final QuerySerializer querySerializer = new QuerySerializer(serializer, serializer, new AxonServerConfiguration());

    @Test
    public void testGetQueryNameReturnsTheNameOfTheQueryAsSpecifiedInTheQueryRequest() {
        QueryMessage<GrpcBackedQueryMessageTest.TestQuery, String> testQueryMessage = new org.axonframework.queryhandling.GenericQueryMessage(GrpcBackedQueryMessageTest.TEST_QUERY, GrpcBackedQueryMessageTest.RESPONSE_TYPE);
        QueryRequest testQueryRequest = querySerializer.serializeRequest(testQueryMessage, GrpcBackedQueryMessageTest.NUMBER_OF_RESULTS, GrpcBackedQueryMessageTest.TIMEOUT, GrpcBackedQueryMessageTest.PRIORITY);
        GrpcBackedQueryMessage<GrpcBackedQueryMessageTest.TestQuery, String> testSubject = new GrpcBackedQueryMessage(testQueryRequest, serializer, serializer);
        Assert.assertEquals(testQueryRequest.getQuery(), testSubject.getQueryName());
    }

    @Test
    public void testGetResponseTypeReturnsTheTypeAsSpecifiedInTheQueryRequest() {
        ResponseType<String> expectedResponseType = GrpcBackedQueryMessageTest.RESPONSE_TYPE;
        QueryMessage<GrpcBackedQueryMessageTest.TestQuery, String> testQueryMessage = new org.axonframework.queryhandling.GenericQueryMessage(GrpcBackedQueryMessageTest.TEST_QUERY, expectedResponseType);
        QueryRequest testQueryRequest = querySerializer.serializeRequest(testQueryMessage, GrpcBackedQueryMessageTest.NUMBER_OF_RESULTS, GrpcBackedQueryMessageTest.TIMEOUT, GrpcBackedQueryMessageTest.PRIORITY);
        GrpcBackedQueryMessage<GrpcBackedQueryMessageTest.TestQuery, String> testSubject = new GrpcBackedQueryMessage(testQueryRequest, serializer, serializer);
        Assert.assertEquals(expectedResponseType.getExpectedResponseType(), testSubject.getResponseType().getExpectedResponseType());
    }

    @Test
    public void testGetIdentifierReturnsTheSameIdentifierAsSpecifiedInTheQueryRequest() {
        QueryMessage<GrpcBackedQueryMessageTest.TestQuery, String> testQueryMessage = new org.axonframework.queryhandling.GenericQueryMessage(GrpcBackedQueryMessageTest.TEST_QUERY, GrpcBackedQueryMessageTest.RESPONSE_TYPE);
        QueryRequest testQueryRequest = querySerializer.serializeRequest(testQueryMessage, GrpcBackedQueryMessageTest.NUMBER_OF_RESULTS, GrpcBackedQueryMessageTest.TIMEOUT, GrpcBackedQueryMessageTest.PRIORITY);
        GrpcBackedQueryMessage<GrpcBackedQueryMessageTest.TestQuery, String> testSubject = new GrpcBackedQueryMessage(testQueryRequest, serializer, serializer);
        Assert.assertEquals(testQueryRequest.getMessageIdentifier(), testSubject.getIdentifier());
    }

    @Test
    public void testGetMetaDataReturnsTheSameMapAsWasInsertedInTheQueryRequest() {
        MetaData expectedMetaData = MetaData.with("some-key", "some-value");
        QueryMessage<GrpcBackedQueryMessageTest.TestQuery, String> testQueryMessage = new org.axonframework.queryhandling.GenericQueryMessage(GrpcBackedQueryMessageTest.TEST_QUERY, GrpcBackedQueryMessageTest.RESPONSE_TYPE).withMetaData(expectedMetaData);
        QueryRequest testQueryRequest = querySerializer.serializeRequest(testQueryMessage, GrpcBackedQueryMessageTest.NUMBER_OF_RESULTS, GrpcBackedQueryMessageTest.TIMEOUT, GrpcBackedQueryMessageTest.PRIORITY);
        GrpcBackedQueryMessage<GrpcBackedQueryMessageTest.TestQuery, String> testSubject = new GrpcBackedQueryMessage(testQueryRequest, serializer, serializer);
        Assert.assertEquals(expectedMetaData, testSubject.getMetaData());
    }

    @Test
    public void testGetPayloadReturnsAnIdenticalObjectAsInsertedThroughTheQueryRequest() {
        GrpcBackedQueryMessageTest.TestQuery expectedQuery = GrpcBackedQueryMessageTest.TEST_QUERY;
        QueryMessage<GrpcBackedQueryMessageTest.TestQuery, String> testQueryMessage = new org.axonframework.queryhandling.GenericQueryMessage(expectedQuery, GrpcBackedQueryMessageTest.RESPONSE_TYPE);
        QueryRequest testQueryRequest = querySerializer.serializeRequest(testQueryMessage, GrpcBackedQueryMessageTest.NUMBER_OF_RESULTS, GrpcBackedQueryMessageTest.TIMEOUT, GrpcBackedQueryMessageTest.PRIORITY);
        GrpcBackedQueryMessage<GrpcBackedQueryMessageTest.TestQuery, String> testSubject = new GrpcBackedQueryMessage(testQueryRequest, serializer, serializer);
        Assert.assertEquals(expectedQuery, testSubject.getPayload());
    }

    @Test
    public void testGetPayloadTypeReturnsTheTypeOfTheInsertedQueryRequest() {
        QueryMessage<GrpcBackedQueryMessageTest.TestQuery, String> testQueryMessage = new org.axonframework.queryhandling.GenericQueryMessage(GrpcBackedQueryMessageTest.TEST_QUERY, GrpcBackedQueryMessageTest.RESPONSE_TYPE);
        QueryRequest testQueryRequest = querySerializer.serializeRequest(testQueryMessage, GrpcBackedQueryMessageTest.NUMBER_OF_RESULTS, GrpcBackedQueryMessageTest.TIMEOUT, GrpcBackedQueryMessageTest.PRIORITY);
        GrpcBackedQueryMessage<GrpcBackedQueryMessageTest.TestQuery, String> testSubject = new GrpcBackedQueryMessage(testQueryRequest, serializer, serializer);
        Assert.assertEquals(GrpcBackedQueryMessageTest.TestQuery.class, testSubject.getPayloadType());
    }

    @Test
    public void testWithMetaDataCompletelyReplacesTheInitialMetaDataMap() {
        MetaData testMetaData = MetaData.with("some-key", "some-value");
        QueryMessage<GrpcBackedQueryMessageTest.TestQuery, String> testQueryMessage = new org.axonframework.queryhandling.GenericQueryMessage(GrpcBackedQueryMessageTest.TEST_QUERY, GrpcBackedQueryMessageTest.RESPONSE_TYPE).withMetaData(testMetaData);
        QueryRequest testQueryRequest = querySerializer.serializeRequest(testQueryMessage, GrpcBackedQueryMessageTest.NUMBER_OF_RESULTS, GrpcBackedQueryMessageTest.TIMEOUT, GrpcBackedQueryMessageTest.PRIORITY);
        GrpcBackedQueryMessage<GrpcBackedQueryMessageTest.TestQuery, String> testSubject = new GrpcBackedQueryMessage(testQueryRequest, serializer, serializer);
        MetaData replacementMetaData = MetaData.with("some-other-key", "some-other-value");
        testSubject = testSubject.withMetaData(replacementMetaData);
        MetaData resultMetaData = testSubject.getMetaData();
        Assert.assertFalse(resultMetaData.containsKey(testMetaData.keySet().iterator().next()));
        Assert.assertEquals(replacementMetaData, resultMetaData);
    }

    @Test
    public void testAndMetaDataAppendsToTheExistingMetaData() {
        MetaData testMetaData = MetaData.with("some-key", "some-value");
        QueryMessage<GrpcBackedQueryMessageTest.TestQuery, String> testQueryMessage = new org.axonframework.queryhandling.GenericQueryMessage(GrpcBackedQueryMessageTest.TEST_QUERY, GrpcBackedQueryMessageTest.RESPONSE_TYPE).withMetaData(testMetaData);
        QueryRequest testQueryRequest = querySerializer.serializeRequest(testQueryMessage, GrpcBackedQueryMessageTest.NUMBER_OF_RESULTS, GrpcBackedQueryMessageTest.TIMEOUT, GrpcBackedQueryMessageTest.PRIORITY);
        GrpcBackedQueryMessage<GrpcBackedQueryMessageTest.TestQuery, String> testSubject = new GrpcBackedQueryMessage(testQueryRequest, serializer, serializer);
        MetaData additionalMetaData = MetaData.with("some-other-key", "some-other-value");
        testSubject = testSubject.andMetaData(additionalMetaData);
        MetaData resultMetaData = testSubject.getMetaData();
        Assert.assertTrue(resultMetaData.containsKey(testMetaData.keySet().iterator().next()));
        Assert.assertTrue(resultMetaData.containsKey(additionalMetaData.keySet().iterator().next()));
    }

    private static class TestQuery {
        private final String queryModelId;

        private final int someFilterValue;

        private TestQuery(String queryModelId, int someFilterValue) {
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
            GrpcBackedQueryMessageTest.TestQuery that = ((GrpcBackedQueryMessageTest.TestQuery) (o));
            return ((someFilterValue) == (that.someFilterValue)) && (Objects.equals(queryModelId, that.queryModelId));
        }

        @Override
        public int hashCode() {
            return Objects.hash(queryModelId, someFilterValue);
        }
    }
}

