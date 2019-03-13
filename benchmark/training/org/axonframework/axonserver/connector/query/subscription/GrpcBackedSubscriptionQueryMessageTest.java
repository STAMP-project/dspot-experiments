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
package org.axonframework.axonserver.connector.query.subscription;


import io.axoniq.axonserver.grpc.query.SubscriptionQuery;
import java.util.Objects;
import org.axonframework.axonserver.connector.AxonServerConfiguration;
import org.axonframework.messaging.MetaData;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.SubscriptionQueryMessage;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test all the functions provided on the {@link GrpcBackedSubscriptionQueryMessage}. The {@link SubscriptionQuery} to
 * be passed to a GrpcBackedSubscriptionQueryMessage is created by using the {@link SubscriptionMessageSerializer}.
 *
 * @author Steven van Beelen
 */
public class GrpcBackedSubscriptionQueryMessageTest {
    private static final GrpcBackedSubscriptionQueryMessageTest.TestQuery TEST_QUERY = new GrpcBackedSubscriptionQueryMessageTest.TestQuery("aggregateId", 42);

    private static final ResponseType<String> RESPONSE_TYPE = ResponseTypes.instanceOf(String.class);

    private final Serializer serializer = XStreamSerializer.defaultSerializer();

    private final SubscriptionMessageSerializer subscriptionMessageSerializer = new SubscriptionMessageSerializer(serializer, serializer, new AxonServerConfiguration());

    @Test
    public void testGetUpdateResponseTypeReturnsTheTypeAsSpecifiedInTheSubscriptionQuery() {
        ResponseType<String> expectedUpdateResponseType = GrpcBackedSubscriptionQueryMessageTest.RESPONSE_TYPE;
        SubscriptionQueryMessage testSubscriptionQueryMessage = new org.axonframework.queryhandling.GenericSubscriptionQueryMessage(GrpcBackedSubscriptionQueryMessageTest.TEST_QUERY, GrpcBackedSubscriptionQueryMessageTest.RESPONSE_TYPE, expectedUpdateResponseType);
        SubscriptionQuery testSubscriptionQuery = subscriptionMessageSerializer.serialize(testSubscriptionQueryMessage);
        GrpcBackedSubscriptionQueryMessage<GrpcBackedSubscriptionQueryMessageTest.TestQuery, String, String> testSubject = new GrpcBackedSubscriptionQueryMessage(testSubscriptionQuery, serializer, serializer);
        Assert.assertEquals(expectedUpdateResponseType.getExpectedResponseType(), testSubject.getResponseType().getExpectedResponseType());
    }

    @Test
    public void testGetQueryNameReturnsTheNameOfTheQueryAsSpecifiedInTheSubscriptionQuery() {
        SubscriptionQueryMessage testSubscriptionQueryMessage = new org.axonframework.queryhandling.GenericSubscriptionQueryMessage(GrpcBackedSubscriptionQueryMessageTest.TEST_QUERY, GrpcBackedSubscriptionQueryMessageTest.RESPONSE_TYPE, GrpcBackedSubscriptionQueryMessageTest.RESPONSE_TYPE);
        SubscriptionQuery testSubscriptionQuery = subscriptionMessageSerializer.serialize(testSubscriptionQueryMessage);
        GrpcBackedSubscriptionQueryMessage<GrpcBackedSubscriptionQueryMessageTest.TestQuery, String, String> testSubject = new GrpcBackedSubscriptionQueryMessage(testSubscriptionQuery, serializer, serializer);
        Assert.assertEquals(testSubscriptionQuery.getQueryRequest().getQuery(), testSubject.getQueryName());
    }

    @Test
    public void testGetResponseTypeReturnsTheTypeAsSpecifiedInTheSubscriptionQuery() {
        ResponseType<String> expectedResponseType = GrpcBackedSubscriptionQueryMessageTest.RESPONSE_TYPE;
        SubscriptionQueryMessage testSubscriptionQueryMessage = new org.axonframework.queryhandling.GenericSubscriptionQueryMessage(GrpcBackedSubscriptionQueryMessageTest.TEST_QUERY, expectedResponseType, GrpcBackedSubscriptionQueryMessageTest.RESPONSE_TYPE);
        SubscriptionQuery testSubscriptionQuery = subscriptionMessageSerializer.serialize(testSubscriptionQueryMessage);
        GrpcBackedSubscriptionQueryMessage<GrpcBackedSubscriptionQueryMessageTest.TestQuery, String, String> testSubject = new GrpcBackedSubscriptionQueryMessage(testSubscriptionQuery, serializer, serializer);
        Assert.assertEquals(expectedResponseType.getExpectedResponseType(), testSubject.getResponseType().getExpectedResponseType());
    }

    @Test
    public void testGetIdentifierReturnsTheSameIdentifierAsSpecifiedInTheSubscriptionQuery() {
        SubscriptionQueryMessage testSubscriptionQueryMessage = new org.axonframework.queryhandling.GenericSubscriptionQueryMessage(GrpcBackedSubscriptionQueryMessageTest.TEST_QUERY, GrpcBackedSubscriptionQueryMessageTest.RESPONSE_TYPE, GrpcBackedSubscriptionQueryMessageTest.RESPONSE_TYPE);
        SubscriptionQuery testSubscriptionQuery = subscriptionMessageSerializer.serialize(testSubscriptionQueryMessage);
        GrpcBackedSubscriptionQueryMessage<GrpcBackedSubscriptionQueryMessageTest.TestQuery, String, String> testSubject = new GrpcBackedSubscriptionQueryMessage(testSubscriptionQuery, serializer, serializer);
        Assert.assertEquals(testSubscriptionQuery.getSubscriptionIdentifier(), testSubject.getIdentifier());
    }

    @Test
    public void testGetMetaDataReturnsTheSameMapAsWasInsertedInTheSubscriptionQuery() {
        MetaData expectedMetaData = MetaData.with("some-key", "some-value");
        SubscriptionQueryMessage testSubscriptionQueryMessage = new org.axonframework.queryhandling.GenericSubscriptionQueryMessage(GrpcBackedSubscriptionQueryMessageTest.TEST_QUERY, GrpcBackedSubscriptionQueryMessageTest.RESPONSE_TYPE, GrpcBackedSubscriptionQueryMessageTest.RESPONSE_TYPE).withMetaData(expectedMetaData);
        SubscriptionQuery testSubscriptionQuery = subscriptionMessageSerializer.serialize(testSubscriptionQueryMessage);
        GrpcBackedSubscriptionQueryMessage<GrpcBackedSubscriptionQueryMessageTest.TestQuery, String, String> testSubject = new GrpcBackedSubscriptionQueryMessage(testSubscriptionQuery, serializer, serializer);
        Assert.assertEquals(expectedMetaData, testSubject.getMetaData());
    }

    @Test
    public void testGetPayloadReturnsAnIdenticalObjectAsInsertedThroughTheSubscriptionQuery() {
        GrpcBackedSubscriptionQueryMessageTest.TestQuery expectedQuery = GrpcBackedSubscriptionQueryMessageTest.TEST_QUERY;
        SubscriptionQueryMessage testSubscriptionQueryMessage = new org.axonframework.queryhandling.GenericSubscriptionQueryMessage(expectedQuery, GrpcBackedSubscriptionQueryMessageTest.RESPONSE_TYPE, GrpcBackedSubscriptionQueryMessageTest.RESPONSE_TYPE);
        SubscriptionQuery testSubscriptionQuery = subscriptionMessageSerializer.serialize(testSubscriptionQueryMessage);
        GrpcBackedSubscriptionQueryMessage<GrpcBackedSubscriptionQueryMessageTest.TestQuery, String, String> testSubject = new GrpcBackedSubscriptionQueryMessage(testSubscriptionQuery, serializer, serializer);
        Assert.assertEquals(expectedQuery, testSubject.getPayload());
    }

    @Test
    public void testGetPayloadTypeReturnsTheTypeOfTheInsertedSubscriptionQuery() {
        SubscriptionQueryMessage testSubscriptionQueryMessage = new org.axonframework.queryhandling.GenericSubscriptionQueryMessage(GrpcBackedSubscriptionQueryMessageTest.TEST_QUERY, GrpcBackedSubscriptionQueryMessageTest.RESPONSE_TYPE, GrpcBackedSubscriptionQueryMessageTest.RESPONSE_TYPE);
        SubscriptionQuery testSubscriptionQuery = subscriptionMessageSerializer.serialize(testSubscriptionQueryMessage);
        GrpcBackedSubscriptionQueryMessage<GrpcBackedSubscriptionQueryMessageTest.TestQuery, String, String> testSubject = new GrpcBackedSubscriptionQueryMessage(testSubscriptionQuery, serializer, serializer);
        Assert.assertEquals(GrpcBackedSubscriptionQueryMessageTest.TestQuery.class, testSubject.getPayloadType());
    }

    @Test
    public void testWithMetaDataCompletelyReplacesTheInitialMetaDataMap() {
        MetaData testMetaData = MetaData.with("some-key", "some-value");
        SubscriptionQueryMessage testSubscriptionQueryMessage = new org.axonframework.queryhandling.GenericSubscriptionQueryMessage(GrpcBackedSubscriptionQueryMessageTest.TEST_QUERY, GrpcBackedSubscriptionQueryMessageTest.RESPONSE_TYPE, GrpcBackedSubscriptionQueryMessageTest.RESPONSE_TYPE).withMetaData(testMetaData);
        SubscriptionQuery testSubscriptionQuery = subscriptionMessageSerializer.serialize(testSubscriptionQueryMessage);
        GrpcBackedSubscriptionQueryMessage<GrpcBackedSubscriptionQueryMessageTest.TestQuery, String, String> testSubject = new GrpcBackedSubscriptionQueryMessage(testSubscriptionQuery, serializer, serializer);
        MetaData replacementMetaData = MetaData.with("some-other-key", "some-other-value");
        testSubject = testSubject.withMetaData(replacementMetaData);
        MetaData resultMetaData = testSubject.getMetaData();
        Assert.assertFalse(resultMetaData.containsKey(testMetaData.keySet().iterator().next()));
        Assert.assertEquals(replacementMetaData, resultMetaData);
    }

    @Test
    public void testAndMetaDataAppendsToTheExistingMetaData() {
        MetaData testMetaData = MetaData.with("some-key", "some-value");
        SubscriptionQueryMessage testSubscriptionQueryMessage = new org.axonframework.queryhandling.GenericSubscriptionQueryMessage(GrpcBackedSubscriptionQueryMessageTest.TEST_QUERY, GrpcBackedSubscriptionQueryMessageTest.RESPONSE_TYPE, GrpcBackedSubscriptionQueryMessageTest.RESPONSE_TYPE).withMetaData(testMetaData);
        SubscriptionQuery testSubscriptionQuery = subscriptionMessageSerializer.serialize(testSubscriptionQueryMessage);
        GrpcBackedSubscriptionQueryMessage<GrpcBackedSubscriptionQueryMessageTest.TestQuery, String, String> testSubject = new GrpcBackedSubscriptionQueryMessage(testSubscriptionQuery, serializer, serializer);
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
            GrpcBackedSubscriptionQueryMessageTest.TestQuery that = ((GrpcBackedSubscriptionQueryMessageTest.TestQuery) (o));
            return ((someFilterValue) == (that.someFilterValue)) && (Objects.equals(queryModelId, that.queryModelId));
        }

        @Override
        public int hashCode() {
            return Objects.hash(queryModelId, someFilterValue);
        }
    }
}

