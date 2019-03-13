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
package org.apache.kafka.streams.state.internals;


import java.util.Arrays;
import java.util.List;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.Window;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.WindowedSerdes;
import org.apache.kafka.streams.kstream.internals.SessionWindow;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class SessionKeySchemaTest {
    private final String key = "key";

    private final String topic = "topic";

    private final long startTime = 50L;

    private final long endTime = 100L;

    private final Serde<String> serde = Serdes.String();

    private final Window window = new SessionWindow(startTime, endTime);

    private final Windowed<String> windowedKey = new Windowed(key, window);

    private final Serde<Windowed<String>> keySerde = new WindowedSerdes.SessionWindowedSerde<>(serde);

    private final SessionKeySchema sessionKeySchema = new SessionKeySchema();

    private DelegatingPeekingKeyValueIterator<Bytes, Integer> iterator;

    @Test
    public void shouldFetchExactKeysSkippingLongerKeys() {
        final Bytes key = Bytes.wrap(new byte[]{ 0 });
        final List<Integer> result = getValues(sessionKeySchema.hasNextCondition(key, key, 0, Long.MAX_VALUE));
        MatcherAssert.assertThat(result, IsEqual.equalTo(Arrays.asList(2, 4)));
    }

    @Test
    public void shouldFetchExactKeySkippingShorterKeys() {
        final Bytes key = Bytes.wrap(new byte[]{ 0, 0 });
        final HasNextCondition hasNextCondition = sessionKeySchema.hasNextCondition(key, key, 0, Long.MAX_VALUE);
        final List<Integer> results = getValues(hasNextCondition);
        MatcherAssert.assertThat(results, IsEqual.equalTo(Arrays.asList(1, 5)));
    }

    @Test
    public void shouldFetchAllKeysUsingNullKeys() {
        final HasNextCondition hasNextCondition = sessionKeySchema.hasNextCondition(null, null, 0, Long.MAX_VALUE);
        final List<Integer> results = getValues(hasNextCondition);
        MatcherAssert.assertThat(results, IsEqual.equalTo(Arrays.asList(1, 2, 3, 4, 5, 6)));
    }

    @Test
    public void testUpperBoundWithLargeTimestamps() {
        final Bytes upper = sessionKeySchema.upperRange(Bytes.wrap(new byte[]{ 10, 11, 12 }), Long.MAX_VALUE);
        MatcherAssert.assertThat("shorter key with max timestamp should be in range", ((upper.compareTo(SessionKeySchema.toBinary(new Windowed(Bytes.wrap(new byte[]{ 10 }), new SessionWindow(Long.MAX_VALUE, Long.MAX_VALUE))))) >= 0));
        MatcherAssert.assertThat("shorter key with max timestamp should be in range", ((upper.compareTo(SessionKeySchema.toBinary(new Windowed(Bytes.wrap(new byte[]{ 10, 11 }), new SessionWindow(Long.MAX_VALUE, Long.MAX_VALUE))))) >= 0));
        MatcherAssert.assertThat(upper, IsEqual.equalTo(SessionKeySchema.toBinary(new Windowed(Bytes.wrap(new byte[]{ 10 }), new SessionWindow(Long.MAX_VALUE, Long.MAX_VALUE)))));
    }

    @Test
    public void testUpperBoundWithKeyBytesLargerThanFirstTimestampByte() {
        final Bytes upper = sessionKeySchema.upperRange(Bytes.wrap(new byte[]{ 10, ((byte) (143)), ((byte) (159)) }), Long.MAX_VALUE);
        MatcherAssert.assertThat("shorter key with max timestamp should be in range", ((upper.compareTo(SessionKeySchema.toBinary(new Windowed(Bytes.wrap(new byte[]{ 10, ((byte) (143)) }), new SessionWindow(Long.MAX_VALUE, Long.MAX_VALUE))))) >= 0));
        MatcherAssert.assertThat(upper, IsEqual.equalTo(SessionKeySchema.toBinary(new Windowed(Bytes.wrap(new byte[]{ 10, ((byte) (143)), ((byte) (159)) }), new SessionWindow(Long.MAX_VALUE, Long.MAX_VALUE)))));
    }

    @Test
    public void testUpperBoundWithZeroTimestamp() {
        final Bytes upper = sessionKeySchema.upperRange(Bytes.wrap(new byte[]{ 10, 11, 12 }), 0);
        MatcherAssert.assertThat(upper, IsEqual.equalTo(SessionKeySchema.toBinary(new Windowed(Bytes.wrap(new byte[]{ 10 }), new SessionWindow(0, Long.MAX_VALUE)))));
    }

    @Test
    public void testLowerBoundWithZeroTimestamp() {
        final Bytes lower = sessionKeySchema.lowerRange(Bytes.wrap(new byte[]{ 10, 11, 12 }), 0);
        MatcherAssert.assertThat(lower, IsEqual.equalTo(SessionKeySchema.toBinary(new Windowed(Bytes.wrap(new byte[]{ 10, 11, 12 }), new SessionWindow(0, 0)))));
    }

    @Test
    public void testLowerBoundMatchesTrailingZeros() {
        final Bytes lower = sessionKeySchema.lowerRange(Bytes.wrap(new byte[]{ 10, 11, 12 }), Long.MAX_VALUE);
        MatcherAssert.assertThat("appending zeros to key should still be in range", ((lower.compareTo(SessionKeySchema.toBinary(new Windowed(Bytes.wrap(new byte[]{ 10, 11, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }), new SessionWindow(Long.MAX_VALUE, Long.MAX_VALUE))))) < 0));
        MatcherAssert.assertThat(lower, IsEqual.equalTo(SessionKeySchema.toBinary(new Windowed(Bytes.wrap(new byte[]{ 10, 11, 12 }), new SessionWindow(0, 0)))));
    }

    @Test
    public void shouldSerializeDeserialize() {
        final byte[] bytes = keySerde.serializer().serialize(topic, windowedKey);
        final Windowed<String> result = keySerde.deserializer().deserialize(topic, bytes);
        Assert.assertEquals(windowedKey, result);
    }

    @Test
    public void shouldSerializeNullToNull() {
        Assert.assertNull(keySerde.serializer().serialize(topic, null));
    }

    @Test
    public void shouldDeSerializeEmtpyByteArrayToNull() {
        Assert.assertNull(keySerde.deserializer().deserialize(topic, new byte[0]));
    }

    @Test
    public void shouldDeSerializeNullToNull() {
        Assert.assertNull(keySerde.deserializer().deserialize(topic, null));
    }

    @Test
    public void shouldConvertToBinaryAndBack() {
        final byte[] serialized = SessionKeySchema.toBinary(windowedKey, serde.serializer(), "dummy");
        final Windowed<String> result = SessionKeySchema.from(serialized, Serdes.String().deserializer(), "dummy");
        Assert.assertEquals(windowedKey, result);
    }

    @Test
    public void shouldExtractEndTimeFromBinary() {
        final byte[] serialized = SessionKeySchema.toBinary(windowedKey, serde.serializer(), "dummy");
        Assert.assertEquals(endTime, SessionKeySchema.extractEndTimestamp(serialized));
    }

    @Test
    public void shouldExtractStartTimeFromBinary() {
        final byte[] serialized = SessionKeySchema.toBinary(windowedKey, serde.serializer(), "dummy");
        Assert.assertEquals(startTime, SessionKeySchema.extractStartTimestamp(serialized));
    }

    @Test
    public void shouldExtractWindowFromBindary() {
        final byte[] serialized = SessionKeySchema.toBinary(windowedKey, serde.serializer(), "dummy");
        Assert.assertEquals(window, SessionKeySchema.extractWindow(serialized));
    }

    @Test
    public void shouldExtractKeyBytesFromBinary() {
        final byte[] serialized = SessionKeySchema.toBinary(windowedKey, serde.serializer(), "dummy");
        Assert.assertArrayEquals(key.getBytes(), SessionKeySchema.extractKeyBytes(serialized));
    }

    @Test
    public void shouldExtractKeyFromBinary() {
        final byte[] serialized = SessionKeySchema.toBinary(windowedKey, serde.serializer(), "dummy");
        Assert.assertEquals(windowedKey, SessionKeySchema.from(serialized, serde.deserializer(), "dummy"));
    }

    @Test
    public void shouldExtractBytesKeyFromBinary() {
        final Bytes bytesKey = Bytes.wrap(key.getBytes());
        final Windowed<Bytes> windowedBytesKey = new Windowed(bytesKey, window);
        final Bytes serialized = SessionKeySchema.toBinary(windowedBytesKey);
        Assert.assertEquals(windowedBytesKey, SessionKeySchema.from(serialized));
    }
}

