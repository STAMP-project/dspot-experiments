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


import io.axoniq.axonserver.grpc.query.QueryUpdate;
import java.util.Objects;
import org.axonframework.axonserver.connector.AxonServerConfiguration;
import org.axonframework.messaging.MetaData;
import org.axonframework.queryhandling.GenericSubscriptionQueryUpdateMessage;
import org.axonframework.queryhandling.SubscriptionQueryUpdateMessage;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test all the functions provided on the {@link GrpcBackedQueryUpdateMessage}. The {@link QueryUpdate} to be passed to
 * a GrpcBackedQueryUpdateMessage is created by using the {@link SubscriptionMessageSerializer}.
 *
 * @author Steven van Beelen
 */
public class GrpcBackedQueryUpdateMessageTest {
    private static final GrpcBackedQueryUpdateMessageTest.TestQueryUpdate TEST_QUERY_UPDATE = new GrpcBackedQueryUpdateMessageTest.TestQueryUpdate("aggregateId", 42);

    private static final String SUBSCRIPTION_ID = "subscription-id";

    private final Serializer serializer = XStreamSerializer.defaultSerializer();

    private final SubscriptionMessageSerializer subscriptionMessageSerializer = new SubscriptionMessageSerializer(serializer, serializer, new AxonServerConfiguration());

    @Test
    public void testGetIdentifierReturnsTheSameIdentifierAsSpecifiedInTheQueryUpdate() {
        SubscriptionQueryUpdateMessage<Object> testSubscriptionQueryUpdateMessage = GenericSubscriptionQueryUpdateMessage.asUpdateMessage(GrpcBackedQueryUpdateMessageTest.TEST_QUERY_UPDATE);
        QueryUpdate testQueryUpdate = subscriptionMessageSerializer.serialize(testSubscriptionQueryUpdateMessage, GrpcBackedQueryUpdateMessageTest.SUBSCRIPTION_ID).getSubscriptionQueryResponse().getUpdate();
        GrpcBackedQueryUpdateMessage<GrpcBackedQueryUpdateMessageTest.TestQueryUpdate> testSubject = new GrpcBackedQueryUpdateMessage(testQueryUpdate, serializer);
        Assert.assertEquals(testQueryUpdate.getMessageIdentifier(), testSubject.getIdentifier());
    }

    @Test
    public void testGetMetaDataReturnsTheSameMapAsWasInsertedInTheQueryUpdate() {
        MetaData expectedMetaData = MetaData.with("some-key", "some-value");
        SubscriptionQueryUpdateMessage<Object> testSubscriptionQueryUpdateMessage = GenericSubscriptionQueryUpdateMessage.asUpdateMessage(GrpcBackedQueryUpdateMessageTest.TEST_QUERY_UPDATE).withMetaData(expectedMetaData);
        QueryUpdate testQueryUpdate = subscriptionMessageSerializer.serialize(testSubscriptionQueryUpdateMessage, GrpcBackedQueryUpdateMessageTest.SUBSCRIPTION_ID).getSubscriptionQueryResponse().getUpdate();
        GrpcBackedQueryUpdateMessage<GrpcBackedQueryUpdateMessageTest.TestQueryUpdate> testSubject = new GrpcBackedQueryUpdateMessage(testQueryUpdate, serializer);
        Assert.assertEquals(expectedMetaData, testSubject.getMetaData());
    }

    @Test
    public void testGetPayloadReturnsAnIdenticalObjectAsInsertedThroughTheQueryUpdate() {
        GrpcBackedQueryUpdateMessageTest.TestQueryUpdate expectedQueryUpdate = GrpcBackedQueryUpdateMessageTest.TEST_QUERY_UPDATE;
        SubscriptionQueryUpdateMessage<Object> testSubscriptionQueryUpdateMessage = GenericSubscriptionQueryUpdateMessage.asUpdateMessage(expectedQueryUpdate);
        QueryUpdate testQueryUpdate = subscriptionMessageSerializer.serialize(testSubscriptionQueryUpdateMessage, GrpcBackedQueryUpdateMessageTest.SUBSCRIPTION_ID).getSubscriptionQueryResponse().getUpdate();
        GrpcBackedQueryUpdateMessage<GrpcBackedQueryUpdateMessageTest.TestQueryUpdate> testSubject = new GrpcBackedQueryUpdateMessage(testQueryUpdate, serializer);
        Assert.assertEquals(expectedQueryUpdate, testSubject.getPayload());
    }

    @Test
    public void testGetPayloadTypeReturnsTheTypeOfTheInsertedQueryUpdate() {
        SubscriptionQueryUpdateMessage<Object> testSubscriptionQueryUpdateMessage = GenericSubscriptionQueryUpdateMessage.asUpdateMessage(GrpcBackedQueryUpdateMessageTest.TEST_QUERY_UPDATE);
        QueryUpdate testQueryUpdate = subscriptionMessageSerializer.serialize(testSubscriptionQueryUpdateMessage, GrpcBackedQueryUpdateMessageTest.SUBSCRIPTION_ID).getSubscriptionQueryResponse().getUpdate();
        GrpcBackedQueryUpdateMessage<GrpcBackedQueryUpdateMessageTest.TestQueryUpdate> testSubject = new GrpcBackedQueryUpdateMessage(testQueryUpdate, serializer);
        Assert.assertEquals(GrpcBackedQueryUpdateMessageTest.TestQueryUpdate.class, testSubject.getPayloadType());
    }

    @Test
    public void testWithMetaDataCompletelyReplacesTheInitialMetaDataMap() {
        MetaData testMetaData = MetaData.with("some-key", "some-value");
        SubscriptionQueryUpdateMessage<Object> testSubscriptionQueryUpdateMessage = GenericSubscriptionQueryUpdateMessage.asUpdateMessage(GrpcBackedQueryUpdateMessageTest.TEST_QUERY_UPDATE).withMetaData(testMetaData);
        QueryUpdate testQueryUpdate = subscriptionMessageSerializer.serialize(testSubscriptionQueryUpdateMessage, GrpcBackedQueryUpdateMessageTest.SUBSCRIPTION_ID).getSubscriptionQueryResponse().getUpdate();
        GrpcBackedQueryUpdateMessage<GrpcBackedQueryUpdateMessageTest.TestQueryUpdate> testSubject = new GrpcBackedQueryUpdateMessage(testQueryUpdate, serializer);
        MetaData replacementMetaData = MetaData.with("some-other-key", "some-other-value");
        testSubject = testSubject.withMetaData(replacementMetaData);
        MetaData resultMetaData = testSubject.getMetaData();
        Assert.assertFalse(resultMetaData.containsKey(testMetaData.keySet().iterator().next()));
        Assert.assertEquals(replacementMetaData, resultMetaData);
    }

    @Test
    public void testAndMetaDataAppendsToTheExistingMetaData() {
        MetaData testMetaData = MetaData.with("some-key", "some-value");
        SubscriptionQueryUpdateMessage<Object> testSubscriptionQueryUpdateMessage = GenericSubscriptionQueryUpdateMessage.asUpdateMessage(GrpcBackedQueryUpdateMessageTest.TEST_QUERY_UPDATE).withMetaData(testMetaData);
        QueryUpdate testQueryUpdate = subscriptionMessageSerializer.serialize(testSubscriptionQueryUpdateMessage, GrpcBackedQueryUpdateMessageTest.SUBSCRIPTION_ID).getSubscriptionQueryResponse().getUpdate();
        GrpcBackedQueryUpdateMessage<GrpcBackedQueryUpdateMessageTest.TestQueryUpdate> testSubject = new GrpcBackedQueryUpdateMessage(testQueryUpdate, serializer);
        MetaData additionalMetaData = MetaData.with("some-other-key", "some-other-value");
        testSubject = testSubject.andMetaData(additionalMetaData);
        MetaData resultMetaData = testSubject.getMetaData();
        Assert.assertTrue(resultMetaData.containsKey(testMetaData.keySet().iterator().next()));
        Assert.assertTrue(resultMetaData.containsKey(additionalMetaData.keySet().iterator().next()));
    }

    private static class TestQueryUpdate {
        private final String queryModelId;

        private final int someFilterValue;

        private TestQueryUpdate(String queryModelId, int someFilterValue) {
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
            GrpcBackedQueryUpdateMessageTest.TestQueryUpdate that = ((GrpcBackedQueryUpdateMessageTest.TestQueryUpdate) (o));
            return ((someFilterValue) == (that.someFilterValue)) && (Objects.equals(queryModelId, that.queryModelId));
        }

        @Override
        public int hashCode() {
            return Objects.hash(queryModelId, someFilterValue);
        }
    }
}

