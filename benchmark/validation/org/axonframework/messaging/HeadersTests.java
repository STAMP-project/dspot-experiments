/**
 * Copyright (c) 2010-2018. Axon Framework
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
package org.axonframework.messaging;


import java.util.HashMap;
import java.util.Map;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.serialization.SerializedObject;
import org.axonframework.serialization.Serializer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link Headers}
 *
 * @author Nakul Mishra
 */
public class HeadersTests {
    private Serializer serializer;

    @Test
    public void testMessageIdText() {
        Assert.assertThat(MESSAGE_ID, CoreMatchers.is("axon-message-id"));
    }

    @Test
    public void testMessageTypeText() {
        Assert.assertThat(MESSAGE_TYPE, CoreMatchers.is("axon-message-type"));
    }

    @Test
    public void testMessageRevisionText() {
        Assert.assertThat(MESSAGE_REVISION, CoreMatchers.is("axon-message-revision"));
    }

    @Test
    public void testMessageTimeStampText() {
        Assert.assertThat(MESSAGE_TIMESTAMP, CoreMatchers.is("axon-message-timestamp"));
    }

    @Test
    public void testMessageAggregateIdText() {
        Assert.assertThat(AGGREGATE_ID, CoreMatchers.is("axon-message-aggregate-id"));
    }

    @Test
    public void testMessageAggregateSeqText() {
        Assert.assertThat(AGGREGATE_SEQ, CoreMatchers.is("axon-message-aggregate-seq"));
    }

    @Test
    public void testMessageAggregateTypeText() {
        Assert.assertThat(AGGREGATE_TYPE, CoreMatchers.is("axon-message-aggregate-type"));
    }

    @Test
    public void testMessageMetadataText() {
        Assert.assertThat(MESSAGE_METADATA, CoreMatchers.is("axon-metadata"));
    }

    @Test
    public void testGeneratingDefaultMessagingHeaders() {
        EventMessage<Object> message = GenericEventMessage.asEventMessage("foo");
        SerializedObject<byte[]> serializedObject = message.serializePayload(serializer, byte[].class);
        Map<String, Object> expected = new HashMap<String, Object>() {
            {
                put(MESSAGE_ID, message.getIdentifier());
                put(MESSAGE_TYPE, serializedObject.getType().getName());
                put(MESSAGE_REVISION, serializedObject.getType().getRevision());
                put(MESSAGE_TIMESTAMP, message.getTimestamp());
            }
        };
        Assert.assertThat(defaultHeaders(message, serializedObject), CoreMatchers.is(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGeneratingDefaultMessagingHeaders_InvalidSerializedObject() {
        EventMessage<Object> message = GenericEventMessage.asEventMessage("foo");
        defaultHeaders(message, null);
    }

    @Test
    public void testGeneratingDomainMessagingHeaders() {
        DomainEventMessage<String> message = domainMessage();
        SerializedObject<byte[]> serializedObject = message.serializePayload(serializer, byte[].class);
        Map<String, Object> expected = new HashMap<String, Object>() {
            {
                put(MESSAGE_ID, message.getIdentifier());
                put(MESSAGE_TYPE, serializedObject.getType().getName());
                put(MESSAGE_REVISION, serializedObject.getType().getRevision());
                put(MESSAGE_TIMESTAMP, message.getTimestamp());
                put(AGGREGATE_ID, message.getAggregateIdentifier());
                put(AGGREGATE_SEQ, message.getSequenceNumber());
                put(AGGREGATE_TYPE, message.getType());
            }
        };
        Assert.assertThat(defaultHeaders(message, serializedObject), CoreMatchers.is(expected));
    }
}

