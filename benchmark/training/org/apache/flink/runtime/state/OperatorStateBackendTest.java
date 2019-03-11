/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.state;


import IntSerializer.INSTANCE;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeutils.SimpleTypeSerializerSnapshot;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.TypeSerializerSnapshot;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.core.fs.CloseableRegistry;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputView;
import org.apache.flink.core.testutils.OneShotLatch;
import org.apache.flink.runtime.checkpoint.CheckpointOptions;
import org.apache.flink.runtime.checkpoint.StateObjectCollection;
import org.apache.flink.runtime.concurrent.FutureUtils;
import org.apache.flink.runtime.execution.Environment;
import org.apache.flink.runtime.state.memory.MemCheckpointStreamFactory;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.runtime.util.BlockerCheckpointStreamFactory;
import org.apache.flink.runtime.util.BlockingCheckpointOutputStream;
import org.apache.flink.util.Preconditions;
import org.junit.Assert;
import org.junit.Test;


public class OperatorStateBackendTest {
    private final ClassLoader classLoader = getClass().getClassLoader();

    private final Collection<OperatorStateHandle> emptyStateHandles = Collections.emptyList();

    @Test
    public void testCreateOnAbstractStateBackend() throws Exception {
        // we use the memory state backend as a subclass of the AbstractStateBackend
        final AbstractStateBackend abstractStateBackend = new MemoryStateBackend();
        CloseableRegistry cancelStreamRegistry = new CloseableRegistry();
        final OperatorStateBackend operatorStateBackend = abstractStateBackend.createOperatorStateBackend(OperatorStateBackendTest.createMockEnvironment(), "test-operator", emptyStateHandles, cancelStreamRegistry);
        Assert.assertNotNull(operatorStateBackend);
        Assert.assertTrue(operatorStateBackend.getRegisteredStateNames().isEmpty());
        Assert.assertTrue(operatorStateBackend.getRegisteredBroadcastStateNames().isEmpty());
    }

    @Test
    public void testRegisterStatesWithoutTypeSerializer() throws Exception {
        // prepare an execution config with a non standard type registered
        final Class<?> registeredType = FutureTask.class;
        // validate the precondition of this test - if this condition fails, we need to pick a different
        // example serializer
        Assert.assertFalse(((new KryoSerializer(File.class, new ExecutionConfig()).getKryo().getDefaultSerializer(registeredType)) instanceof JavaSerializer));
        final ExecutionConfig cfg = new ExecutionConfig();
        cfg.registerTypeWithKryoSerializer(registeredType, JavaSerializer.class);
        final OperatorStateBackend operatorStateBackend = build();
        ListStateDescriptor<File> stateDescriptor = new ListStateDescriptor("test", File.class);
        ListStateDescriptor<String> stateDescriptor2 = new ListStateDescriptor("test2", String.class);
        ListState<File> listState = operatorStateBackend.getListState(stateDescriptor);
        Assert.assertNotNull(listState);
        ListState<String> listState2 = operatorStateBackend.getListState(stateDescriptor2);
        Assert.assertNotNull(listState2);
        Assert.assertEquals(2, operatorStateBackend.getRegisteredStateNames().size());
        // make sure that type registrations are forwarded
        TypeSerializer<?> serializer = ((PartitionableListState<?>) (listState)).getStateMetaInfo().getPartitionStateSerializer();
        Assert.assertTrue((serializer instanceof KryoSerializer));
        Assert.assertTrue(((((KryoSerializer<?>) (serializer)).getKryo().getSerializer(registeredType)) instanceof JavaSerializer));
        Iterator<String> it = listState2.get().iterator();
        Assert.assertFalse(it.hasNext());
        listState2.add("kevin");
        listState2.add("sunny");
        it = listState2.get().iterator();
        Assert.assertEquals("kevin", it.next());
        Assert.assertEquals("sunny", it.next());
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void testRegisterStates() throws Exception {
        final OperatorStateBackend operatorStateBackend = build();
        ListStateDescriptor<Serializable> stateDescriptor1 = new ListStateDescriptor("test1", new JavaSerializer());
        ListStateDescriptor<Serializable> stateDescriptor2 = new ListStateDescriptor("test2", new JavaSerializer());
        ListStateDescriptor<Serializable> stateDescriptor3 = new ListStateDescriptor("test3", new JavaSerializer());
        ListState<Serializable> listState1 = operatorStateBackend.getListState(stateDescriptor1);
        Assert.assertNotNull(listState1);
        Assert.assertEquals(1, operatorStateBackend.getRegisteredStateNames().size());
        Iterator<Serializable> it = listState1.get().iterator();
        Assert.assertFalse(it.hasNext());
        listState1.add(42);
        listState1.add(4711);
        it = listState1.get().iterator();
        Assert.assertEquals(42, it.next());
        Assert.assertEquals(4711, it.next());
        Assert.assertFalse(it.hasNext());
        ListState<Serializable> listState2 = operatorStateBackend.getListState(stateDescriptor2);
        Assert.assertNotNull(listState2);
        Assert.assertEquals(2, operatorStateBackend.getRegisteredStateNames().size());
        Assert.assertFalse(it.hasNext());
        listState2.add(7);
        listState2.add(13);
        listState2.add(23);
        it = listState2.get().iterator();
        Assert.assertEquals(7, it.next());
        Assert.assertEquals(13, it.next());
        Assert.assertEquals(23, it.next());
        Assert.assertFalse(it.hasNext());
        ListState<Serializable> listState3 = operatorStateBackend.getUnionListState(stateDescriptor3);
        Assert.assertNotNull(listState3);
        Assert.assertEquals(3, operatorStateBackend.getRegisteredStateNames().size());
        Assert.assertFalse(it.hasNext());
        listState3.add(17);
        listState3.add(3);
        listState3.add(123);
        it = listState3.get().iterator();
        Assert.assertEquals(17, it.next());
        Assert.assertEquals(3, it.next());
        Assert.assertEquals(123, it.next());
        Assert.assertFalse(it.hasNext());
        ListState<Serializable> listState1b = operatorStateBackend.getListState(stateDescriptor1);
        Assert.assertNotNull(listState1b);
        listState1b.add(123);
        it = listState1b.get().iterator();
        Assert.assertEquals(42, it.next());
        Assert.assertEquals(4711, it.next());
        Assert.assertEquals(123, it.next());
        Assert.assertFalse(it.hasNext());
        it = listState1.get().iterator();
        Assert.assertEquals(42, it.next());
        Assert.assertEquals(4711, it.next());
        Assert.assertEquals(123, it.next());
        Assert.assertFalse(it.hasNext());
        it = listState1b.get().iterator();
        Assert.assertEquals(42, it.next());
        Assert.assertEquals(4711, it.next());
        Assert.assertEquals(123, it.next());
        Assert.assertFalse(it.hasNext());
        try {
            operatorStateBackend.getUnionListState(stateDescriptor2);
            Assert.fail("Did not detect changed mode");
        } catch (IllegalStateException ignored) {
        }
        try {
            operatorStateBackend.getListState(stateDescriptor3);
            Assert.fail("Did not detect changed mode");
        } catch (IllegalStateException ignored) {
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCorrectClassLoaderUsedOnSnapshot() throws Exception {
        AbstractStateBackend abstractStateBackend = new MemoryStateBackend(4096);
        final Environment env = OperatorStateBackendTest.createMockEnvironment();
        CloseableRegistry cancelStreamRegistry = new CloseableRegistry();
        OperatorStateBackend operatorStateBackend = abstractStateBackend.createOperatorStateBackend(env, "test-op-name", emptyStateHandles, cancelStreamRegistry);
        AtomicInteger copyCounter = new AtomicInteger(0);
        TypeSerializer<Integer> serializer = new OperatorStateBackendTest.VerifyingIntSerializer(env.getUserClassLoader(), copyCounter);
        // write some state
        ListStateDescriptor<Integer> stateDescriptor = new ListStateDescriptor("test", serializer);
        ListState<Integer> listState = operatorStateBackend.getListState(stateDescriptor);
        listState.add(42);
        AtomicInteger keyCopyCounter = new AtomicInteger(0);
        AtomicInteger valueCopyCounter = new AtomicInteger(0);
        TypeSerializer<Integer> keySerializer = new OperatorStateBackendTest.VerifyingIntSerializer(env.getUserClassLoader(), keyCopyCounter);
        TypeSerializer<Integer> valueSerializer = new OperatorStateBackendTest.VerifyingIntSerializer(env.getUserClassLoader(), valueCopyCounter);
        MapStateDescriptor<Integer, Integer> broadcastStateDesc = new MapStateDescriptor("test-broadcast", keySerializer, valueSerializer);
        BroadcastState<Integer, Integer> broadcastState = operatorStateBackend.getBroadcastState(broadcastStateDesc);
        broadcastState.put(1, 2);
        broadcastState.put(3, 4);
        broadcastState.put(5, 6);
        CheckpointStreamFactory streamFactory = new MemCheckpointStreamFactory(4096);
        RunnableFuture<SnapshotResult<OperatorStateHandle>> runnableFuture = operatorStateBackend.snapshot(1, 1, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation());
        FutureUtils.runIfNotDoneAndGet(runnableFuture);
        // make sure that the copy method has been called
        Assert.assertTrue(((copyCounter.get()) > 0));
        Assert.assertTrue(((keyCopyCounter.get()) > 0));
        Assert.assertTrue(((valueCopyCounter.get()) > 0));
    }

    /**
     * Int serializer which verifies that the given classloader is set for the copy operation
     */
    private static final class VerifyingIntSerializer extends TypeSerializer<Integer> {
        private static final long serialVersionUID = -5344563614550163898L;

        private transient ClassLoader classLoader;

        private transient AtomicInteger atomicInteger;

        private VerifyingIntSerializer(ClassLoader classLoader, AtomicInteger atomicInteger) {
            this.classLoader = Preconditions.checkNotNull(classLoader);
            this.atomicInteger = Preconditions.checkNotNull(atomicInteger);
        }

        @Override
        public boolean isImmutableType() {
            // otherwise the copy method won't be called for the deepCopy operation
            return false;
        }

        @Override
        public TypeSerializer<Integer> duplicate() {
            return this;
        }

        @Override
        public Integer createInstance() {
            return 0;
        }

        @Override
        public Integer copy(Integer from) {
            Assert.assertEquals(classLoader, Thread.currentThread().getContextClassLoader());
            atomicInteger.incrementAndGet();
            return INSTANCE.copy(from);
        }

        @Override
        public Integer copy(Integer from, Integer reuse) {
            Assert.assertEquals(classLoader, Thread.currentThread().getContextClassLoader());
            atomicInteger.incrementAndGet();
            return INSTANCE.copy(from, reuse);
        }

        @Override
        public int getLength() {
            return INSTANCE.getLength();
        }

        @Override
        public void serialize(Integer record, DataOutputView target) throws IOException {
            INSTANCE.serialize(record, target);
        }

        @Override
        public Integer deserialize(DataInputView source) throws IOException {
            return INSTANCE.deserialize(source);
        }

        @Override
        public Integer deserialize(Integer reuse, DataInputView source) throws IOException {
            return INSTANCE.deserialize(reuse, source);
        }

        @Override
        public void copy(DataInputView source, DataOutputView target) throws IOException {
            Assert.assertEquals(classLoader, Thread.currentThread().getContextClassLoader());
            atomicInteger.incrementAndGet();
            INSTANCE.copy(source, target);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof OperatorStateBackendTest.VerifyingIntSerializer;
        }

        @Override
        public int hashCode() {
            return getClass().hashCode();
        }

        @Override
        public TypeSerializerSnapshot<Integer> snapshotConfiguration() {
            return new OperatorStateBackendTest.VerifyingIntSerializerSnapshot();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class VerifyingIntSerializerSnapshot extends SimpleTypeSerializerSnapshot<Integer> {
        public VerifyingIntSerializerSnapshot() {
            super(() -> new org.apache.flink.runtime.state.VerifyingIntSerializer(Thread.currentThread().getContextClassLoader(), new AtomicInteger()));
        }
    }

    @Test
    public void testSnapshotEmpty() throws Exception {
        final AbstractStateBackend abstractStateBackend = new MemoryStateBackend(4096);
        CloseableRegistry cancelStreamRegistry = new CloseableRegistry();
        final OperatorStateBackend operatorStateBackend = abstractStateBackend.createOperatorStateBackend(OperatorStateBackendTest.createMockEnvironment(), "testOperator", emptyStateHandles, cancelStreamRegistry);
        CheckpointStreamFactory streamFactory = new MemCheckpointStreamFactory(4096);
        RunnableFuture<SnapshotResult<OperatorStateHandle>> snapshot = operatorStateBackend.snapshot(0L, 0L, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation());
        SnapshotResult<OperatorStateHandle> snapshotResult = FutureUtils.runIfNotDoneAndGet(snapshot);
        OperatorStateHandle stateHandle = snapshotResult.getJobManagerOwnedSnapshot();
        Assert.assertNull(stateHandle);
    }

    @Test
    public void testSnapshotBroadcastStateWithEmptyOperatorState() throws Exception {
        final AbstractStateBackend abstractStateBackend = new MemoryStateBackend(4096);
        OperatorStateBackend operatorStateBackend = abstractStateBackend.createOperatorStateBackend(OperatorStateBackendTest.createMockEnvironment(), "testOperator", emptyStateHandles, new CloseableRegistry());
        final MapStateDescriptor<Integer, Integer> broadcastStateDesc = new MapStateDescriptor("test-broadcast", BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO);
        final Map<Integer, Integer> expected = new HashMap<>(3);
        expected.put(1, 2);
        expected.put(3, 4);
        expected.put(5, 6);
        final BroadcastState<Integer, Integer> broadcastState = operatorStateBackend.getBroadcastState(broadcastStateDesc);
        broadcastState.putAll(expected);
        final CheckpointStreamFactory streamFactory = new MemCheckpointStreamFactory(4096);
        OperatorStateHandle stateHandle = null;
        try {
            RunnableFuture<SnapshotResult<OperatorStateHandle>> snapshot = operatorStateBackend.snapshot(0L, 0L, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation());
            SnapshotResult<OperatorStateHandle> snapshotResult = FutureUtils.runIfNotDoneAndGet(snapshot);
            stateHandle = snapshotResult.getJobManagerOwnedSnapshot();
            Assert.assertNotNull(stateHandle);
            final Map<Integer, Integer> retrieved = new HashMap<>();
            operatorStateBackend = OperatorStateBackendTest.recreateOperatorStateBackend(operatorStateBackend, abstractStateBackend, StateObjectCollection.singleton(stateHandle));
            BroadcastState<Integer, Integer> retrievedState = operatorStateBackend.getBroadcastState(broadcastStateDesc);
            for (Map.Entry<Integer, Integer> e : retrievedState.entries()) {
                retrieved.put(e.getKey(), e.getValue());
            }
            Assert.assertEquals(expected, retrieved);
            // remove an element from both expected and stored state.
            retrievedState.remove(1);
            expected.remove(1);
            snapshot = operatorStateBackend.snapshot(1L, 1L, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation());
            snapshotResult = FutureUtils.runIfNotDoneAndGet(snapshot);
            stateHandle.discardState();
            stateHandle = snapshotResult.getJobManagerOwnedSnapshot();
            retrieved.clear();
            operatorStateBackend = OperatorStateBackendTest.recreateOperatorStateBackend(operatorStateBackend, abstractStateBackend, StateObjectCollection.singleton(stateHandle));
            retrievedState = operatorStateBackend.getBroadcastState(broadcastStateDesc);
            for (Map.Entry<Integer, Integer> e : retrievedState.immutableEntries()) {
                retrieved.put(e.getKey(), e.getValue());
            }
            Assert.assertEquals(expected, retrieved);
            // remove all elements from both expected and stored state.
            retrievedState.clear();
            expected.clear();
            snapshot = operatorStateBackend.snapshot(2L, 2L, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation());
            snapshotResult = FutureUtils.runIfNotDoneAndGet(snapshot);
            if (stateHandle != null) {
                stateHandle.discardState();
            }
            stateHandle = snapshotResult.getJobManagerOwnedSnapshot();
            retrieved.clear();
            operatorStateBackend = OperatorStateBackendTest.recreateOperatorStateBackend(operatorStateBackend, abstractStateBackend, StateObjectCollection.singleton(stateHandle));
            retrievedState = operatorStateBackend.getBroadcastState(broadcastStateDesc);
            for (Map.Entry<Integer, Integer> e : retrievedState.immutableEntries()) {
                retrieved.put(e.getKey(), e.getValue());
            }
            Assert.assertTrue(expected.isEmpty());
            Assert.assertEquals(expected, retrieved);
            if (stateHandle != null) {
                stateHandle.discardState();
                stateHandle = null;
            }
        } finally {
            operatorStateBackend.close();
            operatorStateBackend.dispose();
            if (stateHandle != null) {
                stateHandle.discardState();
            }
        }
    }

    @Test
    public void testSnapshotRestoreSync() throws Exception {
        AbstractStateBackend abstractStateBackend = new MemoryStateBackend((2 * 4096));
        OperatorStateBackend operatorStateBackend = abstractStateBackend.createOperatorStateBackend(OperatorStateBackendTest.createMockEnvironment(), "test-op-name", emptyStateHandles, new CloseableRegistry());
        ListStateDescriptor<Serializable> stateDescriptor1 = new ListStateDescriptor("test1", new JavaSerializer());
        ListStateDescriptor<Serializable> stateDescriptor2 = new ListStateDescriptor("test2", new JavaSerializer());
        ListStateDescriptor<Serializable> stateDescriptor3 = new ListStateDescriptor("test3", new JavaSerializer());
        MapStateDescriptor<Serializable, Serializable> broadcastStateDescriptor1 = new MapStateDescriptor("test4", new JavaSerializer(), new JavaSerializer());
        MapStateDescriptor<Serializable, Serializable> broadcastStateDescriptor2 = new MapStateDescriptor("test5", new JavaSerializer(), new JavaSerializer());
        MapStateDescriptor<Serializable, Serializable> broadcastStateDescriptor3 = new MapStateDescriptor("test6", new JavaSerializer(), new JavaSerializer());
        ListState<Serializable> listState1 = operatorStateBackend.getListState(stateDescriptor1);
        ListState<Serializable> listState2 = operatorStateBackend.getListState(stateDescriptor2);
        ListState<Serializable> listState3 = operatorStateBackend.getUnionListState(stateDescriptor3);
        BroadcastState<Serializable, Serializable> broadcastState1 = operatorStateBackend.getBroadcastState(broadcastStateDescriptor1);
        BroadcastState<Serializable, Serializable> broadcastState2 = operatorStateBackend.getBroadcastState(broadcastStateDescriptor2);
        BroadcastState<Serializable, Serializable> broadcastState3 = operatorStateBackend.getBroadcastState(broadcastStateDescriptor3);
        listState1.add(42);
        listState1.add(4711);
        listState2.add(7);
        listState2.add(13);
        listState2.add(23);
        listState3.add(17);
        listState3.add(18);
        listState3.add(19);
        listState3.add(20);
        broadcastState1.put(1, 2);
        broadcastState1.put(2, 5);
        broadcastState2.put(2, 5);
        CheckpointStreamFactory streamFactory = new MemCheckpointStreamFactory((2 * 4096));
        RunnableFuture<SnapshotResult<OperatorStateHandle>> snapshot = operatorStateBackend.snapshot(1L, 1L, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation());
        SnapshotResult<OperatorStateHandle> snapshotResult = FutureUtils.runIfNotDoneAndGet(snapshot);
        OperatorStateHandle stateHandle = snapshotResult.getJobManagerOwnedSnapshot();
        try {
            operatorStateBackend.close();
            operatorStateBackend.dispose();
            operatorStateBackend = abstractStateBackend.createOperatorStateBackend(OperatorStateBackendTest.createMockEnvironment(), "testOperator", StateObjectCollection.singleton(stateHandle), new CloseableRegistry());
            Assert.assertEquals(3, operatorStateBackend.getRegisteredStateNames().size());
            Assert.assertEquals(3, operatorStateBackend.getRegisteredBroadcastStateNames().size());
            listState1 = operatorStateBackend.getListState(stateDescriptor1);
            listState2 = operatorStateBackend.getListState(stateDescriptor2);
            listState3 = operatorStateBackend.getUnionListState(stateDescriptor3);
            broadcastState1 = operatorStateBackend.getBroadcastState(broadcastStateDescriptor1);
            broadcastState2 = operatorStateBackend.getBroadcastState(broadcastStateDescriptor2);
            broadcastState3 = operatorStateBackend.getBroadcastState(broadcastStateDescriptor3);
            Assert.assertEquals(3, operatorStateBackend.getRegisteredStateNames().size());
            Assert.assertEquals(3, operatorStateBackend.getRegisteredBroadcastStateNames().size());
            Iterator<Serializable> it = listState1.get().iterator();
            Assert.assertEquals(42, it.next());
            Assert.assertEquals(4711, it.next());
            Assert.assertFalse(it.hasNext());
            it = listState2.get().iterator();
            Assert.assertEquals(7, it.next());
            Assert.assertEquals(13, it.next());
            Assert.assertEquals(23, it.next());
            Assert.assertFalse(it.hasNext());
            it = listState3.get().iterator();
            Assert.assertEquals(17, it.next());
            Assert.assertEquals(18, it.next());
            Assert.assertEquals(19, it.next());
            Assert.assertEquals(20, it.next());
            Assert.assertFalse(it.hasNext());
            Iterator<Map.Entry<Serializable, Serializable>> bIt = broadcastState1.iterator();
            Assert.assertTrue(bIt.hasNext());
            Map.Entry<Serializable, Serializable> entry = bIt.next();
            Assert.assertEquals(1, entry.getKey());
            Assert.assertEquals(2, entry.getValue());
            Assert.assertTrue(bIt.hasNext());
            entry = bIt.next();
            Assert.assertEquals(2, entry.getKey());
            Assert.assertEquals(5, entry.getValue());
            Assert.assertFalse(bIt.hasNext());
            bIt = broadcastState2.iterator();
            Assert.assertTrue(bIt.hasNext());
            entry = bIt.next();
            Assert.assertEquals(2, entry.getKey());
            Assert.assertEquals(5, entry.getValue());
            Assert.assertFalse(bIt.hasNext());
            bIt = broadcastState3.iterator();
            Assert.assertFalse(bIt.hasNext());
            operatorStateBackend.close();
            operatorStateBackend.dispose();
        } finally {
            stateHandle.discardState();
        }
    }

    @Test
    public void testSnapshotRestoreAsync() throws Exception {
        OperatorStateBackend operatorStateBackend = build();
        ListStateDescriptor<OperatorStateBackendTest.MutableType> stateDescriptor1 = new ListStateDescriptor("test1", new JavaSerializer<OperatorStateBackendTest.MutableType>());
        ListStateDescriptor<OperatorStateBackendTest.MutableType> stateDescriptor2 = new ListStateDescriptor("test2", new JavaSerializer<OperatorStateBackendTest.MutableType>());
        ListStateDescriptor<OperatorStateBackendTest.MutableType> stateDescriptor3 = new ListStateDescriptor("test3", new JavaSerializer<OperatorStateBackendTest.MutableType>());
        MapStateDescriptor<OperatorStateBackendTest.MutableType, OperatorStateBackendTest.MutableType> broadcastStateDescriptor1 = new MapStateDescriptor("test4", new JavaSerializer<OperatorStateBackendTest.MutableType>(), new JavaSerializer<OperatorStateBackendTest.MutableType>());
        MapStateDescriptor<OperatorStateBackendTest.MutableType, OperatorStateBackendTest.MutableType> broadcastStateDescriptor2 = new MapStateDescriptor("test5", new JavaSerializer<OperatorStateBackendTest.MutableType>(), new JavaSerializer<OperatorStateBackendTest.MutableType>());
        MapStateDescriptor<OperatorStateBackendTest.MutableType, OperatorStateBackendTest.MutableType> broadcastStateDescriptor3 = new MapStateDescriptor("test6", new JavaSerializer<OperatorStateBackendTest.MutableType>(), new JavaSerializer<OperatorStateBackendTest.MutableType>());
        ListState<OperatorStateBackendTest.MutableType> listState1 = operatorStateBackend.getListState(stateDescriptor1);
        ListState<OperatorStateBackendTest.MutableType> listState2 = operatorStateBackend.getListState(stateDescriptor2);
        ListState<OperatorStateBackendTest.MutableType> listState3 = operatorStateBackend.getUnionListState(stateDescriptor3);
        BroadcastState<OperatorStateBackendTest.MutableType, OperatorStateBackendTest.MutableType> broadcastState1 = operatorStateBackend.getBroadcastState(broadcastStateDescriptor1);
        BroadcastState<OperatorStateBackendTest.MutableType, OperatorStateBackendTest.MutableType> broadcastState2 = operatorStateBackend.getBroadcastState(broadcastStateDescriptor2);
        BroadcastState<OperatorStateBackendTest.MutableType, OperatorStateBackendTest.MutableType> broadcastState3 = operatorStateBackend.getBroadcastState(broadcastStateDescriptor3);
        listState1.add(OperatorStateBackendTest.MutableType.of(42));
        listState1.add(OperatorStateBackendTest.MutableType.of(4711));
        listState2.add(OperatorStateBackendTest.MutableType.of(7));
        listState2.add(OperatorStateBackendTest.MutableType.of(13));
        listState2.add(OperatorStateBackendTest.MutableType.of(23));
        listState3.add(OperatorStateBackendTest.MutableType.of(17));
        listState3.add(OperatorStateBackendTest.MutableType.of(18));
        listState3.add(OperatorStateBackendTest.MutableType.of(19));
        listState3.add(OperatorStateBackendTest.MutableType.of(20));
        broadcastState1.put(OperatorStateBackendTest.MutableType.of(1), OperatorStateBackendTest.MutableType.of(2));
        broadcastState1.put(OperatorStateBackendTest.MutableType.of(2), OperatorStateBackendTest.MutableType.of(5));
        broadcastState2.put(OperatorStateBackendTest.MutableType.of(2), OperatorStateBackendTest.MutableType.of(5));
        BlockerCheckpointStreamFactory streamFactory = new BlockerCheckpointStreamFactory((1024 * 1024));
        OneShotLatch waiterLatch = new OneShotLatch();
        OneShotLatch blockerLatch = new OneShotLatch();
        streamFactory.setWaiterLatch(waiterLatch);
        streamFactory.setBlockerLatch(blockerLatch);
        RunnableFuture<SnapshotResult<OperatorStateHandle>> runnableFuture = operatorStateBackend.snapshot(1, 1, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation());
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(runnableFuture);
        // wait until the async checkpoint is in the write code, then continue
        waiterLatch.await();
        // do some mutations to the state, to test if our snapshot will NOT reflect them
        listState1.add(OperatorStateBackendTest.MutableType.of(77));
        broadcastState1.put(OperatorStateBackendTest.MutableType.of(32), OperatorStateBackendTest.MutableType.of(97));
        int n = 0;
        for (OperatorStateBackendTest.MutableType mutableType : listState2.get()) {
            if ((++n) == 2) {
                // allow the write code to continue, so that we could do changes while state is written in parallel.
                blockerLatch.trigger();
            }
            mutableType.setValue(((mutableType.getValue()) + 10));
        }
        listState3.clear();
        broadcastState2.clear();
        operatorStateBackend.getListState(new ListStateDescriptor("test4", new JavaSerializer<OperatorStateBackendTest.MutableType>()));
        // run the snapshot
        SnapshotResult<OperatorStateHandle> snapshotResult = runnableFuture.get();
        OperatorStateHandle stateHandle = snapshotResult.getJobManagerOwnedSnapshot();
        try {
            operatorStateBackend.close();
            operatorStateBackend.dispose();
            AbstractStateBackend abstractStateBackend = new MemoryStateBackend(4096);
            CloseableRegistry cancelStreamRegistry = new CloseableRegistry();
            operatorStateBackend = abstractStateBackend.createOperatorStateBackend(OperatorStateBackendTest.createMockEnvironment(), "testOperator", StateObjectCollection.singleton(stateHandle), cancelStreamRegistry);
            Assert.assertEquals(3, operatorStateBackend.getRegisteredStateNames().size());
            Assert.assertEquals(3, operatorStateBackend.getRegisteredBroadcastStateNames().size());
            listState1 = operatorStateBackend.getListState(stateDescriptor1);
            listState2 = operatorStateBackend.getListState(stateDescriptor2);
            listState3 = operatorStateBackend.getUnionListState(stateDescriptor3);
            broadcastState1 = operatorStateBackend.getBroadcastState(broadcastStateDescriptor1);
            broadcastState2 = operatorStateBackend.getBroadcastState(broadcastStateDescriptor2);
            broadcastState3 = operatorStateBackend.getBroadcastState(broadcastStateDescriptor3);
            Assert.assertEquals(3, operatorStateBackend.getRegisteredStateNames().size());
            Assert.assertEquals(3, operatorStateBackend.getRegisteredBroadcastStateNames().size());
            Iterator<OperatorStateBackendTest.MutableType> it = listState1.get().iterator();
            Assert.assertEquals(42, it.next().value);
            Assert.assertEquals(4711, it.next().value);
            Assert.assertFalse(it.hasNext());
            it = listState2.get().iterator();
            Assert.assertEquals(7, it.next().value);
            Assert.assertEquals(13, it.next().value);
            Assert.assertEquals(23, it.next().value);
            Assert.assertFalse(it.hasNext());
            it = listState3.get().iterator();
            Assert.assertEquals(17, it.next().value);
            Assert.assertEquals(18, it.next().value);
            Assert.assertEquals(19, it.next().value);
            Assert.assertEquals(20, it.next().value);
            Assert.assertFalse(it.hasNext());
            Iterator<Map.Entry<OperatorStateBackendTest.MutableType, OperatorStateBackendTest.MutableType>> bIt = broadcastState1.iterator();
            Assert.assertTrue(bIt.hasNext());
            Map.Entry<OperatorStateBackendTest.MutableType, OperatorStateBackendTest.MutableType> entry = bIt.next();
            Assert.assertEquals(1, entry.getKey().value);
            Assert.assertEquals(2, entry.getValue().value);
            Assert.assertTrue(bIt.hasNext());
            entry = bIt.next();
            Assert.assertEquals(2, entry.getKey().value);
            Assert.assertEquals(5, entry.getValue().value);
            Assert.assertFalse(bIt.hasNext());
            bIt = broadcastState2.iterator();
            Assert.assertTrue(bIt.hasNext());
            entry = bIt.next();
            Assert.assertEquals(2, entry.getKey().value);
            Assert.assertEquals(5, entry.getValue().value);
            Assert.assertFalse(bIt.hasNext());
            bIt = broadcastState3.iterator();
            Assert.assertFalse(bIt.hasNext());
            operatorStateBackend.close();
            operatorStateBackend.dispose();
        } finally {
            stateHandle.discardState();
        }
        executorService.shutdown();
    }

    @Test
    public void testSnapshotAsyncClose() throws Exception {
        DefaultOperatorStateBackend operatorStateBackend = new DefaultOperatorStateBackendBuilder(OperatorStateBackendTest.class.getClassLoader(), new ExecutionConfig(), true, emptyStateHandles, new CloseableRegistry()).build();
        ListStateDescriptor<OperatorStateBackendTest.MutableType> stateDescriptor1 = new ListStateDescriptor("test1", new JavaSerializer<OperatorStateBackendTest.MutableType>());
        ListState<OperatorStateBackendTest.MutableType> listState1 = operatorStateBackend.getOperatorState(stateDescriptor1);
        listState1.add(OperatorStateBackendTest.MutableType.of(42));
        listState1.add(OperatorStateBackendTest.MutableType.of(4711));
        MapStateDescriptor<OperatorStateBackendTest.MutableType, OperatorStateBackendTest.MutableType> broadcastStateDescriptor1 = new MapStateDescriptor("test4", new JavaSerializer<OperatorStateBackendTest.MutableType>(), new JavaSerializer<OperatorStateBackendTest.MutableType>());
        BroadcastState<OperatorStateBackendTest.MutableType, OperatorStateBackendTest.MutableType> broadcastState1 = operatorStateBackend.getBroadcastState(broadcastStateDescriptor1);
        broadcastState1.put(OperatorStateBackendTest.MutableType.of(1), OperatorStateBackendTest.MutableType.of(2));
        broadcastState1.put(OperatorStateBackendTest.MutableType.of(2), OperatorStateBackendTest.MutableType.of(5));
        BlockerCheckpointStreamFactory streamFactory = new BlockerCheckpointStreamFactory((1024 * 1024));
        OneShotLatch waiterLatch = new OneShotLatch();
        OneShotLatch blockerLatch = new OneShotLatch();
        streamFactory.setWaiterLatch(waiterLatch);
        streamFactory.setBlockerLatch(blockerLatch);
        RunnableFuture<SnapshotResult<OperatorStateHandle>> runnableFuture = operatorStateBackend.snapshot(1, 1, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation());
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(runnableFuture);
        // wait until the async checkpoint is in the write code, then continue
        waiterLatch.await();
        operatorStateBackend.close();
        blockerLatch.trigger();
        try {
            runnableFuture.get(60, TimeUnit.SECONDS);
            Assert.fail();
        } catch (CancellationException expected) {
        }
    }

    @Test
    public void testSnapshotAsyncCancel() throws Exception {
        DefaultOperatorStateBackend operatorStateBackend = new DefaultOperatorStateBackendBuilder(OperatorStateBackendTest.class.getClassLoader(), new ExecutionConfig(), true, emptyStateHandles, new CloseableRegistry()).build();
        ListStateDescriptor<OperatorStateBackendTest.MutableType> stateDescriptor1 = new ListStateDescriptor("test1", new JavaSerializer<OperatorStateBackendTest.MutableType>());
        ListState<OperatorStateBackendTest.MutableType> listState1 = operatorStateBackend.getOperatorState(stateDescriptor1);
        listState1.add(OperatorStateBackendTest.MutableType.of(42));
        listState1.add(OperatorStateBackendTest.MutableType.of(4711));
        BlockerCheckpointStreamFactory streamFactory = new BlockerCheckpointStreamFactory((1024 * 1024));
        OneShotLatch waiterLatch = new OneShotLatch();
        OneShotLatch blockerLatch = new OneShotLatch();
        streamFactory.setWaiterLatch(waiterLatch);
        streamFactory.setBlockerLatch(blockerLatch);
        RunnableFuture<SnapshotResult<OperatorStateHandle>> runnableFuture = operatorStateBackend.snapshot(1, 1, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation());
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(runnableFuture);
        // wait until the async checkpoint is in the stream's write code, then continue
        waiterLatch.await();
        // cancel the future, which should close the underlying stream
        runnableFuture.cancel(true);
        for (BlockingCheckpointOutputStream stream : streamFactory.getAllCreatedStreams()) {
            Assert.assertTrue(stream.isClosed());
        }
        // we allow the stream under test to proceed
        blockerLatch.trigger();
        try {
            runnableFuture.get(60, TimeUnit.SECONDS);
            Assert.fail();
        } catch (CancellationException ignore) {
        }
    }

    static final class MutableType implements Serializable {
        private static final long serialVersionUID = 1L;

        private int value;

        public MutableType() {
            this(0);
        }

        public MutableType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            OperatorStateBackendTest.MutableType that = ((OperatorStateBackendTest.MutableType) (o));
            return (value) == (that.value);
        }

        @Override
        public int hashCode() {
            return value;
        }

        static OperatorStateBackendTest.MutableType of(int value) {
            return new OperatorStateBackendTest.MutableType(value);
        }
    }
}

