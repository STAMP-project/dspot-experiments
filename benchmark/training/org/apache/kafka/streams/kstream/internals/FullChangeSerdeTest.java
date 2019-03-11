/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.kstream.internals;


import java.util.Collections;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class FullChangeSerdeTest {
    private final FullChangeSerde<String> serde = new FullChangeSerde(Serdes.String());

    @Test
    public void shouldRoundTripNull() {
        final byte[] serialized = serde.serializer().serialize(null, null);
        MatcherAssert.assertThat(serde.deserializer().deserialize(null, serialized), CoreMatchers.nullValue());
    }

    @Test
    public void shouldRoundTripNullChange() {
        final byte[] serialized = serde.serializer().serialize(null, new Change(null, null));
        MatcherAssert.assertThat(serde.deserializer().deserialize(null, serialized), Is.is(new Change(null, null)));
    }

    @Test
    public void shouldRoundTripOldNull() {
        final byte[] serialized = serde.serializer().serialize(null, new Change("new", null));
        MatcherAssert.assertThat(serde.deserializer().deserialize(null, serialized), Is.is(new Change("new", null)));
    }

    @Test
    public void shouldRoundTripNewNull() {
        final byte[] serialized = serde.serializer().serialize(null, new Change(null, "old"));
        MatcherAssert.assertThat(serde.deserializer().deserialize(null, serialized), Is.is(new Change(null, "old")));
    }

    @Test
    public void shouldRoundTripChange() {
        final byte[] serialized = serde.serializer().serialize(null, new Change("new", "old"));
        MatcherAssert.assertThat(serde.deserializer().deserialize(null, serialized), Is.is(new Change("new", "old")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldConfigureSerde() {
        final Serde<Void> mock = EasyMock.mock(Serde.class);
        mock.configure(Collections.emptyMap(), false);
        EasyMock.expectLastCall();
        EasyMock.replay(mock);
        final FullChangeSerde<Void> serde = new FullChangeSerde(mock);
        serde.configure(Collections.emptyMap(), false);
        EasyMock.verify(mock);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCloseSerde() {
        final Serde<Void> mock = EasyMock.mock(Serde.class);
        mock.close();
        EasyMock.expectLastCall();
        EasyMock.replay(mock);
        final FullChangeSerde<Void> serde = new FullChangeSerde(mock);
        serde.close();
        EasyMock.verify(mock);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldConfigureSerializer() {
        final Serde<Void> mockSerde = EasyMock.mock(Serde.class);
        final Serializer<Void> mockSerializer = EasyMock.mock(Serializer.class);
        EasyMock.expect(mockSerde.serializer()).andReturn(mockSerializer);
        EasyMock.replay(mockSerde);
        mockSerializer.configure(Collections.emptyMap(), false);
        EasyMock.expectLastCall();
        EasyMock.replay(mockSerializer);
        final Serializer<Change<Void>> serializer = new FullChangeSerde(mockSerde).serializer();
        serializer.configure(Collections.emptyMap(), false);
        EasyMock.verify(mockSerde);
        EasyMock.verify(mockSerializer);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCloseSerializer() {
        final Serde<Void> mockSerde = EasyMock.mock(Serde.class);
        final Serializer<Void> mockSerializer = EasyMock.mock(Serializer.class);
        EasyMock.expect(mockSerde.serializer()).andReturn(mockSerializer);
        EasyMock.replay(mockSerde);
        mockSerializer.close();
        EasyMock.expectLastCall();
        EasyMock.replay(mockSerializer);
        final Serializer<Change<Void>> serializer = new FullChangeSerde(mockSerde).serializer();
        serializer.close();
        EasyMock.verify(mockSerde);
        EasyMock.verify(mockSerializer);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldConfigureDeserializer() {
        final Serde<Void> mockSerde = EasyMock.mock(Serde.class);
        final Deserializer<Void> mockDeserializer = EasyMock.mock(Deserializer.class);
        EasyMock.expect(mockSerde.deserializer()).andReturn(mockDeserializer);
        EasyMock.replay(mockSerde);
        mockDeserializer.configure(Collections.emptyMap(), false);
        EasyMock.expectLastCall();
        EasyMock.replay(mockDeserializer);
        final Deserializer<Change<Void>> serializer = new FullChangeSerde(mockSerde).deserializer();
        serializer.configure(Collections.emptyMap(), false);
        EasyMock.verify(mockSerde);
        EasyMock.verify(mockDeserializer);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCloseDeserializer() {
        final Serde<Void> mockSerde = EasyMock.mock(Serde.class);
        final Deserializer<Void> mockDeserializer = EasyMock.mock(Deserializer.class);
        EasyMock.expect(mockSerde.deserializer()).andReturn(mockDeserializer);
        EasyMock.replay(mockSerde);
        mockDeserializer.close();
        EasyMock.expectLastCall();
        EasyMock.replay(mockDeserializer);
        final Deserializer<Change<Void>> serializer = new FullChangeSerde(mockSerde).deserializer();
        serializer.close();
        EasyMock.verify(mockSerde);
        EasyMock.verify(mockDeserializer);
    }
}

