/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.state;


import OperatorStateHandle.Mode;
import OperatorStateHandle.Mode.BROADCAST;
import OperatorStateHandle.Mode.UNION;
import StateDescriptor.Type;
import StateDescriptor.Type.AGGREGATING;
import StateDescriptor.Type.FOLDING;
import StateDescriptor.Type.LIST;
import StateDescriptor.Type.MAP;
import StateDescriptor.Type.REDUCING;
import StateDescriptor.Type.UNKNOWN;
import StateDescriptor.Type.VALUE;
import StateMetaInfoSnapshotReadersWriters.StateTypeHint.KEYED_STATE;
import StateMetaInfoSnapshotReadersWriters.StateTypeHint.OPERATOR_STATE;
import java.util.ArrayList;
import java.util.List;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.base.DoubleSerializer;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.core.memory.ByteArrayInputStreamWithPos;
import org.apache.flink.core.memory.ByteArrayOutputStreamWithPos;
import org.apache.flink.runtime.state.metainfo.StateMetaInfoReader;
import org.apache.flink.runtime.state.metainfo.StateMetaInfoSnapshot;
import org.apache.flink.runtime.state.metainfo.StateMetaInfoSnapshotReadersWriters;
import org.junit.Assert;
import org.junit.Test;


public class SerializationProxiesTest {
    @Test
    public void testKeyedBackendSerializationProxyRoundtrip() throws Exception {
        TypeSerializer<?> keySerializer = IntSerializer.INSTANCE;
        TypeSerializer<?> namespaceSerializer = LongSerializer.INSTANCE;
        TypeSerializer<?> stateSerializer = DoubleSerializer.INSTANCE;
        List<StateMetaInfoSnapshot> stateMetaInfoList = new ArrayList<>();
        stateMetaInfoList.add(new RegisteredKeyValueStateBackendMetaInfo(Type.VALUE, "a", namespaceSerializer, stateSerializer).snapshot());
        stateMetaInfoList.add(new RegisteredKeyValueStateBackendMetaInfo(Type.VALUE, "b", namespaceSerializer, stateSerializer).snapshot());
        stateMetaInfoList.add(new RegisteredKeyValueStateBackendMetaInfo(Type.VALUE, "c", namespaceSerializer, stateSerializer).snapshot());
        KeyedBackendSerializationProxy<?> serializationProxy = new KeyedBackendSerializationProxy(keySerializer, stateMetaInfoList, true);
        byte[] serialized;
        try (ByteArrayOutputStreamWithPos out = new ByteArrayOutputStreamWithPos()) {
            serializationProxy.write(new org.apache.flink.core.memory.DataOutputViewStreamWrapper(out));
            serialized = out.toByteArray();
        }
        serializationProxy = new KeyedBackendSerializationProxy(Thread.currentThread().getContextClassLoader());
        try (ByteArrayInputStreamWithPos in = new ByteArrayInputStreamWithPos(serialized)) {
            serializationProxy.read(new org.apache.flink.core.memory.DataInputViewStreamWrapper(in));
        }
        Assert.assertTrue(serializationProxy.isUsingKeyGroupCompression());
        Assert.assertTrue(((serializationProxy.getKeySerializerSnapshot()) instanceof IntSerializer.IntSerializerSnapshot));
        assertEqualStateMetaInfoSnapshotsLists(stateMetaInfoList, serializationProxy.getStateMetaInfoSnapshots());
    }

    @Test
    public void testKeyedStateMetaInfoSerialization() throws Exception {
        String name = "test";
        TypeSerializer<?> namespaceSerializer = LongSerializer.INSTANCE;
        TypeSerializer<?> stateSerializer = DoubleSerializer.INSTANCE;
        StateMetaInfoSnapshot metaInfo = new RegisteredKeyValueStateBackendMetaInfo(Type.VALUE, name, namespaceSerializer, stateSerializer).snapshot();
        byte[] serialized;
        try (ByteArrayOutputStreamWithPos out = new ByteArrayOutputStreamWithPos()) {
            StateMetaInfoSnapshotReadersWriters.getWriter().writeStateMetaInfoSnapshot(metaInfo, new org.apache.flink.core.memory.DataOutputViewStreamWrapper(out));
            serialized = out.toByteArray();
        }
        try (ByteArrayInputStreamWithPos in = new ByteArrayInputStreamWithPos(serialized)) {
            final StateMetaInfoReader reader = StateMetaInfoSnapshotReadersWriters.getReader(StateMetaInfoSnapshotReadersWriters.CURRENT_STATE_META_INFO_SNAPSHOT_VERSION, KEYED_STATE);
            metaInfo = reader.readStateMetaInfoSnapshot(new org.apache.flink.core.memory.DataInputViewStreamWrapper(in), Thread.currentThread().getContextClassLoader());
        }
        Assert.assertEquals(name, metaInfo.getName());
    }

    @Test
    public void testOperatorBackendSerializationProxyRoundtrip() throws Exception {
        TypeSerializer<?> stateSerializer = DoubleSerializer.INSTANCE;
        TypeSerializer<?> keySerializer = DoubleSerializer.INSTANCE;
        TypeSerializer<?> valueSerializer = StringSerializer.INSTANCE;
        List<StateMetaInfoSnapshot> stateMetaInfoSnapshots = new ArrayList<>();
        stateMetaInfoSnapshots.add(new RegisteredOperatorStateBackendMetaInfo("a", stateSerializer, Mode.SPLIT_DISTRIBUTE).snapshot());
        stateMetaInfoSnapshots.add(new RegisteredOperatorStateBackendMetaInfo("b", stateSerializer, Mode.SPLIT_DISTRIBUTE).snapshot());
        stateMetaInfoSnapshots.add(new RegisteredOperatorStateBackendMetaInfo("c", stateSerializer, Mode.UNION).snapshot());
        List<StateMetaInfoSnapshot> broadcastStateMetaInfoSnapshots = new ArrayList<>();
        broadcastStateMetaInfoSnapshots.add(new RegisteredBroadcastStateBackendMetaInfo("d", Mode.BROADCAST, keySerializer, valueSerializer).snapshot());
        broadcastStateMetaInfoSnapshots.add(new RegisteredBroadcastStateBackendMetaInfo("e", Mode.BROADCAST, valueSerializer, keySerializer).snapshot());
        OperatorBackendSerializationProxy serializationProxy = new OperatorBackendSerializationProxy(stateMetaInfoSnapshots, broadcastStateMetaInfoSnapshots);
        byte[] serialized;
        try (ByteArrayOutputStreamWithPos out = new ByteArrayOutputStreamWithPos()) {
            serializationProxy.write(new org.apache.flink.core.memory.DataOutputViewStreamWrapper(out));
            serialized = out.toByteArray();
        }
        serializationProxy = new OperatorBackendSerializationProxy(Thread.currentThread().getContextClassLoader());
        try (ByteArrayInputStreamWithPos in = new ByteArrayInputStreamWithPos(serialized)) {
            serializationProxy.read(new org.apache.flink.core.memory.DataInputViewStreamWrapper(in));
        }
        assertEqualStateMetaInfoSnapshotsLists(stateMetaInfoSnapshots, serializationProxy.getOperatorStateMetaInfoSnapshots());
        assertEqualStateMetaInfoSnapshotsLists(broadcastStateMetaInfoSnapshots, serializationProxy.getBroadcastStateMetaInfoSnapshots());
    }

    @Test
    public void testOperatorStateMetaInfoSerialization() throws Exception {
        String name = "test";
        TypeSerializer<?> stateSerializer = DoubleSerializer.INSTANCE;
        StateMetaInfoSnapshot snapshot = new RegisteredOperatorStateBackendMetaInfo(name, stateSerializer, Mode.UNION).snapshot();
        byte[] serialized;
        try (ByteArrayOutputStreamWithPos out = new ByteArrayOutputStreamWithPos()) {
            StateMetaInfoSnapshotReadersWriters.getWriter().writeStateMetaInfoSnapshot(snapshot, new org.apache.flink.core.memory.DataOutputViewStreamWrapper(out));
            serialized = out.toByteArray();
        }
        try (ByteArrayInputStreamWithPos in = new ByteArrayInputStreamWithPos(serialized)) {
            final StateMetaInfoReader reader = StateMetaInfoSnapshotReadersWriters.getReader(StateMetaInfoSnapshotReadersWriters.CURRENT_STATE_META_INFO_SNAPSHOT_VERSION, OPERATOR_STATE);
            snapshot = reader.readStateMetaInfoSnapshot(new org.apache.flink.core.memory.DataInputViewStreamWrapper(in), Thread.currentThread().getContextClassLoader());
        }
        RegisteredOperatorStateBackendMetaInfo<?> restoredMetaInfo = new RegisteredOperatorStateBackendMetaInfo(snapshot);
        Assert.assertEquals(name, restoredMetaInfo.getName());
        Assert.assertEquals(UNION, restoredMetaInfo.getAssignmentMode());
        Assert.assertEquals(stateSerializer, restoredMetaInfo.getPartitionStateSerializer());
    }

    @Test
    public void testBroadcastStateMetaInfoSerialization() throws Exception {
        String name = "test";
        TypeSerializer<?> keySerializer = DoubleSerializer.INSTANCE;
        TypeSerializer<?> valueSerializer = StringSerializer.INSTANCE;
        StateMetaInfoSnapshot snapshot = new RegisteredBroadcastStateBackendMetaInfo(name, Mode.BROADCAST, keySerializer, valueSerializer).snapshot();
        byte[] serialized;
        try (ByteArrayOutputStreamWithPos out = new ByteArrayOutputStreamWithPos()) {
            StateMetaInfoSnapshotReadersWriters.getWriter().writeStateMetaInfoSnapshot(snapshot, new org.apache.flink.core.memory.DataOutputViewStreamWrapper(out));
            serialized = out.toByteArray();
        }
        try (ByteArrayInputStreamWithPos in = new ByteArrayInputStreamWithPos(serialized)) {
            final StateMetaInfoReader reader = StateMetaInfoSnapshotReadersWriters.getReader(StateMetaInfoSnapshotReadersWriters.CURRENT_STATE_META_INFO_SNAPSHOT_VERSION, OPERATOR_STATE);
            snapshot = reader.readStateMetaInfoSnapshot(new org.apache.flink.core.memory.DataInputViewStreamWrapper(in), Thread.currentThread().getContextClassLoader());
        }
        RegisteredBroadcastStateBackendMetaInfo<?, ?> restoredMetaInfo = new RegisteredBroadcastStateBackendMetaInfo(snapshot);
        Assert.assertEquals(name, restoredMetaInfo.getName());
        Assert.assertEquals(BROADCAST, restoredMetaInfo.getAssignmentMode());
        Assert.assertEquals(keySerializer, restoredMetaInfo.getKeySerializer());
        Assert.assertEquals(valueSerializer, restoredMetaInfo.getValueSerializer());
    }

    /**
     * This test fixes the order of elements in the enum which is important for serialization. Do not modify this test
     * except if you are entirely sure what you are doing.
     */
    @Test
    public void testFixTypeOrder() {
        // ensure all elements are covered
        Assert.assertEquals(7, Type.values().length);
        // fix the order of elements to keep serialization format stable
        Assert.assertEquals(0, UNKNOWN.ordinal());
        Assert.assertEquals(1, VALUE.ordinal());
        Assert.assertEquals(2, LIST.ordinal());
        Assert.assertEquals(3, REDUCING.ordinal());
        Assert.assertEquals(4, FOLDING.ordinal());
        Assert.assertEquals(5, AGGREGATING.ordinal());
        Assert.assertEquals(6, MAP.ordinal());
    }
}

