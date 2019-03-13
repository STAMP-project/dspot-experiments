/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.axonserver.connector.query.subscription;


import io.axoniq.axonserver.grpc.query.QueryProviderOutbound;
import io.axoniq.axonserver.grpc.query.QueryResponse;
import io.axoniq.axonserver.grpc.query.QueryUpdate;
import io.axoniq.axonserver.grpc.query.QueryUpdateCompleteExceptionally;
import io.axoniq.axonserver.grpc.query.SubscriptionQuery;
import io.axoniq.axonserver.grpc.query.SubscriptionQueryResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.axonframework.axonserver.connector.AxonServerConfiguration;
import org.axonframework.queryhandling.GenericSubscriptionQueryMessage;
import org.axonframework.queryhandling.QueryResponseMessage;
import org.axonframework.queryhandling.SubscriptionQueryMessage;
import org.axonframework.queryhandling.SubscriptionQueryUpdateMessage;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sara Pellegrini
 */
public class SubscriptionMessageSerializerTest {
    private final Serializer xStreamSerializer = XStreamSerializer.builder().build();

    private final Serializer jacksonSerializer = JacksonSerializer.builder().build();

    private final AxonServerConfiguration configuration = new AxonServerConfiguration() {
        {
            setClientId("client");
            setComponentName("component");
        }
    };

    private final SubscriptionMessageSerializer testSubject = new SubscriptionMessageSerializer(jacksonSerializer, xStreamSerializer, configuration);

    @Test
    public void testInitialResponse() {
        Map<String, ?> metadata = new HashMap<String, Object>() {
            {
                this.put("firstKey", "firstValue");
                this.put("secondKey", "secondValue");
            }
        };
        QueryResponseMessage message = new org.axonframework.queryhandling.GenericQueryResponseMessage(String.class, "Result", metadata);
        QueryProviderOutbound grpcMessage = testSubject.serialize(message, "subscriptionId");
        Assert.assertEquals("subscriptionId", grpcMessage.getSubscriptionQueryResponse().getSubscriptionIdentifier());
        QueryResponse initialResponse = grpcMessage.getSubscriptionQueryResponse().getInitialResult();
        QueryResponseMessage<Object> deserialized = testSubject.deserialize(initialResponse);
        Assert.assertEquals(message.getIdentifier(), deserialized.getIdentifier());
        Assert.assertEquals(message.getPayload(), deserialized.getPayload());
        Assert.assertEquals(message.getPayloadType(), deserialized.getPayloadType());
        Assert.assertEquals(message.getMetaData(), deserialized.getMetaData());
    }

    @Test
    public void testUpdate() {
        List<String> payload = new ArrayList<>();
        payload.add("A");
        payload.add("B");
        SubscriptionQueryUpdateMessage message = new org.axonframework.queryhandling.GenericSubscriptionQueryUpdateMessage(payload);
        QueryProviderOutbound grpcMessage = testSubject.serialize(message, "subscriptionId");
        Assert.assertEquals("subscriptionId", grpcMessage.getSubscriptionQueryResponse().getSubscriptionIdentifier());
        QueryUpdate update = grpcMessage.getSubscriptionQueryResponse().getUpdate();
        SubscriptionQueryUpdateMessage<Object> deserialized = testSubject.deserialize(update);
        Assert.assertEquals(message.getIdentifier(), deserialized.getIdentifier());
        Assert.assertEquals(message.getPayload(), deserialized.getPayload());
        Assert.assertEquals(message.getPayloadType(), deserialized.getPayloadType());
        Assert.assertEquals(message.getMetaData(), deserialized.getMetaData());
    }

    @Test
    public void testSubscriptionQueryMessage() {
        GenericSubscriptionQueryMessage<String, Integer, Integer> message = new GenericSubscriptionQueryMessage("query", "MyQueryName", instanceOf(int.class), instanceOf(int.class));
        SubscriptionQuery grpcMessage = testSubject.serialize(message);
        SubscriptionQueryMessage<Object, Object, Object> deserialized = testSubject.deserialize(grpcMessage);
        Assert.assertEquals(message.getIdentifier(), deserialized.getIdentifier());
        Assert.assertEquals(message.getPayload(), deserialized.getPayload());
        Assert.assertEquals(message.getPayloadType(), deserialized.getPayloadType());
        Assert.assertEquals(message.getMetaData(), deserialized.getMetaData());
        Assert.assertEquals(message.getQueryName(), deserialized.getQueryName());
        Assert.assertTrue(message.getResponseType().matches(deserialized.getResponseType().responseMessagePayloadType()));
        Assert.assertTrue(message.getUpdateResponseType().matches(deserialized.getUpdateResponseType().responseMessagePayloadType()));
    }

    @Test
    public void testComplete() {
        QueryProviderOutbound grpcMessage = testSubject.serializeComplete("subscriptionId");
        Assert.assertEquals("subscriptionId", grpcMessage.getSubscriptionQueryResponse().getSubscriptionIdentifier());
    }

    @Test
    public void testCompleteExceptionally() {
        QueryProviderOutbound grpcMessage = testSubject.serializeCompleteExceptionally("subscriptionId", new RuntimeException("Error"));
        SubscriptionQueryResponse subscriptionQueryResponse = grpcMessage.getSubscriptionQueryResponse();
        Assert.assertEquals("subscriptionId", subscriptionQueryResponse.getSubscriptionIdentifier());
        QueryUpdateCompleteExceptionally completeExceptionally = subscriptionQueryResponse.getCompleteExceptionally();
        Assert.assertEquals("Error", completeExceptionally.getErrorMessage().getMessage());
    }
}

