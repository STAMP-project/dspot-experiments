/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.queryablestate.network;


import LongSerializer.INSTANCE;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.queryablestate.client.VoidNamespace;
import org.apache.flink.queryablestate.client.state.serialization.KvStateSerializer;
import org.apache.flink.runtime.state.heap.HeapKeyedStateBackend;
import org.apache.flink.runtime.state.internal.InternalListState;
import org.apache.flink.runtime.state.internal.InternalMapState;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link KvStateSerializer}.
 */
@RunWith(Parameterized.class)
public class KvStateRequestSerializerTest {
    @Parameterized.Parameter
    public boolean async;

    /**
     * Tests key and namespace serialization utils.
     */
    @Test
    public void testKeyAndNamespaceSerialization() throws Exception {
        TypeSerializer<Long> keySerializer = LongSerializer.INSTANCE;
        TypeSerializer<String> namespaceSerializer = StringSerializer.INSTANCE;
        long expectedKey = (Integer.MAX_VALUE) + 12323L;
        String expectedNamespace = "knilf";
        byte[] serializedKeyAndNamespace = KvStateSerializer.serializeKeyAndNamespace(expectedKey, keySerializer, expectedNamespace, namespaceSerializer);
        Tuple2<Long, String> actual = KvStateSerializer.deserializeKeyAndNamespace(serializedKeyAndNamespace, keySerializer, namespaceSerializer);
        Assert.assertEquals(expectedKey, actual.f0.longValue());
        Assert.assertEquals(expectedNamespace, actual.f1);
    }

    /**
     * Tests key and namespace deserialization utils with too few bytes.
     */
    @Test(expected = IOException.class)
    public void testKeyAndNamespaceDeserializationEmpty() throws Exception {
        KvStateSerializer.deserializeKeyAndNamespace(new byte[]{  }, INSTANCE, StringSerializer.INSTANCE);
    }

    /**
     * Tests key and namespace deserialization utils with too few bytes.
     */
    @Test(expected = IOException.class)
    public void testKeyAndNamespaceDeserializationTooShort() throws Exception {
        KvStateSerializer.deserializeKeyAndNamespace(new byte[]{ 1 }, INSTANCE, StringSerializer.INSTANCE);
    }

    /**
     * Tests key and namespace deserialization utils with too many bytes.
     */
    @Test(expected = IOException.class)
    public void testKeyAndNamespaceDeserializationTooMany1() throws Exception {
        // Long + null String + 1 byte
        KvStateSerializer.deserializeKeyAndNamespace(new byte[]{ 1, 1, 1, 1, 1, 1, 1, 1, 42, 0, 2 }, INSTANCE, StringSerializer.INSTANCE);
    }

    /**
     * Tests key and namespace deserialization utils with too many bytes.
     */
    @Test(expected = IOException.class)
    public void testKeyAndNamespaceDeserializationTooMany2() throws Exception {
        // Long + null String + 2 bytes
        KvStateSerializer.deserializeKeyAndNamespace(new byte[]{ 1, 1, 1, 1, 1, 1, 1, 1, 42, 0, 2, 2 }, INSTANCE, StringSerializer.INSTANCE);
    }

    /**
     * Tests value serialization utils.
     */
    @Test
    public void testValueSerialization() throws Exception {
        TypeSerializer<Long> valueSerializer = LongSerializer.INSTANCE;
        long expectedValue = (Long.MAX_VALUE) - 1292929292L;
        byte[] serializedValue = KvStateSerializer.serializeValue(expectedValue, valueSerializer);
        long actualValue = KvStateSerializer.deserializeValue(serializedValue, valueSerializer);
        Assert.assertEquals(expectedValue, actualValue);
    }

    /**
     * Tests value deserialization with too few bytes.
     */
    @Test(expected = IOException.class)
    public void testDeserializeValueEmpty() throws Exception {
        KvStateSerializer.deserializeValue(new byte[]{  }, INSTANCE);
    }

    /**
     * Tests value deserialization with too few bytes.
     */
    @Test(expected = IOException.class)
    public void testDeserializeValueTooShort() throws Exception {
        // 1 byte (incomplete Long)
        KvStateSerializer.deserializeValue(new byte[]{ 1 }, INSTANCE);
    }

    /**
     * Tests value deserialization with too many bytes.
     */
    @Test(expected = IOException.class)
    public void testDeserializeValueTooMany1() throws Exception {
        // Long + 1 byte
        KvStateSerializer.deserializeValue(new byte[]{ 1, 1, 1, 1, 1, 1, 1, 1, 2 }, INSTANCE);
    }

    /**
     * Tests value deserialization with too many bytes.
     */
    @Test(expected = IOException.class)
    public void testDeserializeValueTooMany2() throws Exception {
        // Long + 2 bytes
        KvStateSerializer.deserializeValue(new byte[]{ 1, 1, 1, 1, 1, 1, 1, 1, 2, 2 }, INSTANCE);
    }

    /**
     * Tests list serialization utils.
     */
    @Test
    public void testListSerialization() throws Exception {
        final long key = 0L;
        final HeapKeyedStateBackend<Long> longHeapKeyedStateBackend = getLongHeapKeyedStateBackend(key);
        final InternalListState<Long, VoidNamespace, Long> listState = longHeapKeyedStateBackend.createInternalState(VoidNamespaceSerializer.INSTANCE, new org.apache.flink.api.common.state.ListStateDescriptor("test", LongSerializer.INSTANCE));
        KvStateRequestSerializerTest.testListSerialization(key, listState);
    }

    /**
     * Tests list deserialization with too few bytes.
     */
    @Test
    public void testDeserializeListEmpty() throws Exception {
        List<Long> actualValue = KvStateSerializer.deserializeList(new byte[]{  }, INSTANCE);
        Assert.assertEquals(0, actualValue.size());
    }

    /**
     * Tests list deserialization with too few bytes.
     */
    @Test(expected = IOException.class)
    public void testDeserializeListTooShort1() throws Exception {
        // 1 byte (incomplete Long)
        KvStateSerializer.deserializeList(new byte[]{ 1 }, INSTANCE);
    }

    /**
     * Tests list deserialization with too few bytes.
     */
    @Test(expected = IOException.class)
    public void testDeserializeListTooShort2() throws Exception {
        // Long + 1 byte (separator) + 1 byte (incomplete Long)
        KvStateSerializer.deserializeList(new byte[]{ 1, 1, 1, 1, 1, 1, 1, 1, 2, 3 }, INSTANCE);
    }

    /**
     * Tests map serialization utils.
     */
    @Test
    public void testMapSerialization() throws Exception {
        final long key = 0L;
        final HeapKeyedStateBackend<Long> longHeapKeyedStateBackend = getLongHeapKeyedStateBackend(key);
        final InternalMapState<Long, VoidNamespace, Long, String> mapState = ((InternalMapState<Long, VoidNamespace, Long, String>) (longHeapKeyedStateBackend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, new org.apache.flink.api.common.state.MapStateDescriptor("test", LongSerializer.INSTANCE, StringSerializer.INSTANCE))));
        KvStateRequestSerializerTest.testMapSerialization(key, mapState);
    }

    /**
     * Tests map deserialization with too few bytes.
     */
    @Test
    public void testDeserializeMapEmpty() throws Exception {
        Map<Long, String> actualValue = KvStateSerializer.deserializeMap(new byte[]{  }, INSTANCE, StringSerializer.INSTANCE);
        Assert.assertEquals(0, actualValue.size());
    }

    /**
     * Tests map deserialization with too few bytes.
     */
    @Test(expected = IOException.class)
    public void testDeserializeMapTooShort1() throws Exception {
        // 1 byte (incomplete Key)
        KvStateSerializer.deserializeMap(new byte[]{ 1 }, INSTANCE, StringSerializer.INSTANCE);
    }

    /**
     * Tests map deserialization with too few bytes.
     */
    @Test(expected = IOException.class)
    public void testDeserializeMapTooShort2() throws Exception {
        // Long (Key) + 1 byte (incomplete Value)
        KvStateSerializer.deserializeMap(new byte[]{ 1, 1, 1, 1, 1, 1, 1, 1, 0 }, INSTANCE, INSTANCE);
    }

    /**
     * Tests map deserialization with too few bytes.
     */
    @Test(expected = IOException.class)
    public void testDeserializeMapTooShort3() throws Exception {
        // Long (Key1) + Boolean (false) + Long (Value1) + 1 byte (incomplete Key2)
        KvStateSerializer.deserializeMap(new byte[]{ 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 3 }, INSTANCE, INSTANCE);
    }
}

