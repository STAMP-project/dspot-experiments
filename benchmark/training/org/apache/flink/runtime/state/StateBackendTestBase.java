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
package org.apache.flink.runtime.state;


import HighAvailabilityServices.DEFAULT_JOB_ID;
import IntSerializer.INSTANCE;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.state.AggregatingState;
import org.apache.flink.api.common.state.AggregatingStateDescriptor;
import org.apache.flink.api.common.state.FoldingState;
import org.apache.flink.api.common.state.FoldingStateDescriptor;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.base.FloatSerializer;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.api.java.typeutils.runtime.PojoSerializer;
import org.apache.flink.api.java.typeutils.runtime.kryo.JavaSerializer;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.core.memory.DataOutputViewStreamWrapper;
import org.apache.flink.core.testutils.CheckedThread;
import org.apache.flink.core.testutils.OneShotLatch;
import org.apache.flink.queryablestate.KvStateID;
import org.apache.flink.runtime.checkpoint.CheckpointOptions;
import org.apache.flink.runtime.checkpoint.StateAssignmentOperation;
import org.apache.flink.runtime.execution.Environment;
import org.apache.flink.runtime.operators.testutils.DummyEnvironment;
import org.apache.flink.runtime.query.KvStateRegistry;
import org.apache.flink.runtime.query.KvStateRegistryListener;
import org.apache.flink.runtime.state.internal.InternalAggregatingState;
import org.apache.flink.runtime.state.internal.InternalKvState;
import org.apache.flink.runtime.state.internal.InternalListState;
import org.apache.flink.runtime.state.internal.InternalReducingState;
import org.apache.flink.runtime.state.internal.InternalValueState;
import org.apache.flink.runtime.util.BlockerCheckpointStreamFactory;
import org.apache.flink.shaded.guava18.com.google.common.base.Joiner;
import org.apache.flink.types.IntValue;
import org.apache.flink.util.IOUtils;
import org.apache.flink.util.StateMigrationException;
import org.apache.flink.util.TestLogger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static VoidNamespaceSerializer.INSTANCE;


/**
 * Tests for the {@link KeyedStateBackend} and {@link OperatorStateBackend} as produced
 * by various {@link StateBackend}s.
 */
@SuppressWarnings("serial")
public abstract class StateBackendTestBase<B extends AbstractStateBackend> extends TestLogger {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    // lazily initialized stream storage
    private CheckpointStorageLocation checkpointStorageLocation;

    @Test
    public void testGetKeys() throws Exception {
        final int namespace1ElementsNum = 1000;
        final int namespace2ElementsNum = 1000;
        String fieldName = "get-keys-test";
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        try {
            final String ns1 = "ns1";
            ValueState<Integer> keyedState1 = backend.getPartitionedState(ns1, StringSerializer.INSTANCE, new ValueStateDescriptor(fieldName, IntSerializer.INSTANCE));
            for (int key = 0; key < namespace1ElementsNum; key++) {
                backend.setCurrentKey(key);
                keyedState1.update((key * 2));
            }
            final String ns2 = "ns2";
            ValueState<Integer> keyedState2 = backend.getPartitionedState(ns2, StringSerializer.INSTANCE, new ValueStateDescriptor(fieldName, IntSerializer.INSTANCE));
            for (int key = namespace1ElementsNum; key < (namespace1ElementsNum + namespace2ElementsNum); key++) {
                backend.setCurrentKey(key);
                keyedState2.update((key * 2));
            }
            // valid for namespace1
            try (Stream<Integer> keysStream = backend.getKeys(fieldName, ns1).sorted()) {
                PrimitiveIterator.OfInt actualIterator = keysStream.mapToInt(( value) -> value.intValue()).iterator();
                for (int expectedKey = 0; expectedKey < namespace1ElementsNum; expectedKey++) {
                    Assert.assertTrue(actualIterator.hasNext());
                    Assert.assertEquals(expectedKey, actualIterator.nextInt());
                }
                Assert.assertFalse(actualIterator.hasNext());
            }
            // valid for namespace2
            try (Stream<Integer> keysStream = backend.getKeys(fieldName, ns2).sorted()) {
                PrimitiveIterator.OfInt actualIterator = keysStream.mapToInt(( value) -> value.intValue()).iterator();
                for (int expectedKey = namespace1ElementsNum; expectedKey < (namespace1ElementsNum + namespace2ElementsNum); expectedKey++) {
                    Assert.assertTrue(actualIterator.hasNext());
                    Assert.assertEquals(expectedKey, actualIterator.nextInt());
                }
                Assert.assertFalse(actualIterator.hasNext());
            }
        } finally {
            IOUtils.closeQuietly(backend);
            backend.dispose();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBackendUsesRegisteredKryoDefaultSerializer() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        Environment env = new DummyEnvironment();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE, env);
        // cast because our test serializer is not typed to TestPojo
        env.getExecutionConfig().addDefaultKryoSerializer(StateBackendTestBase.TestPojo.class, ((Class) (StateBackendTestBase.ExceptionThrowingTestSerializer.class)));
        TypeInformation<StateBackendTestBase.TestPojo> pojoType = new org.apache.flink.api.java.typeutils.GenericTypeInfo(StateBackendTestBase.TestPojo.class);
        // make sure that we are in fact using the KryoSerializer
        Assert.assertTrue(((pojoType.createSerializer(env.getExecutionConfig())) instanceof KryoSerializer));
        ValueStateDescriptor<StateBackendTestBase.TestPojo> kvId = new ValueStateDescriptor("id", pojoType);
        ValueState<StateBackendTestBase.TestPojo> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        // we will be expecting ExpectedKryoTestException to be thrown,
        // because the ExceptionThrowingTestSerializer should be used
        int numExceptions = 0;
        backend.setCurrentKey(1);
        try {
            // backends that eagerly serializes (such as RocksDB) will fail here
            state.update(new StateBackendTestBase.TestPojo("u1", 1));
        } catch (StateBackendTestBase.ExpectedKryoTestException e) {
            numExceptions++;
        } catch (Exception e) {
            if ((e.getCause()) instanceof StateBackendTestBase.ExpectedKryoTestException) {
                numExceptions++;
            } else {
                throw e;
            }
        }
        try {
            // backends that lazily serializes (such as memory state backend) will fail here
            runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        } catch (StateBackendTestBase.ExpectedKryoTestException e) {
            numExceptions++;
        } catch (Exception e) {
            if ((e.getCause()) instanceof StateBackendTestBase.ExpectedKryoTestException) {
                numExceptions++;
            } else {
                throw e;
            }
        }
        Assert.assertEquals("Didn't see the expected Kryo exception.", 1, numExceptions);
        backend.dispose();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBackendUsesRegisteredKryoDefaultSerializerUsingGetOrCreate() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        Environment env = new DummyEnvironment();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE, env);
        // cast because our test serializer is not typed to TestPojo
        env.getExecutionConfig().addDefaultKryoSerializer(StateBackendTestBase.TestPojo.class, ((Class) (StateBackendTestBase.ExceptionThrowingTestSerializer.class)));
        TypeInformation<StateBackendTestBase.TestPojo> pojoType = new org.apache.flink.api.java.typeutils.GenericTypeInfo(StateBackendTestBase.TestPojo.class);
        // make sure that we are in fact using the KryoSerializer
        Assert.assertTrue(((pojoType.createSerializer(env.getExecutionConfig())) instanceof KryoSerializer));
        pojoType.createSerializer(env.getExecutionConfig());
        ValueStateDescriptor<StateBackendTestBase.TestPojo> kvId = new ValueStateDescriptor("id", pojoType);
        ValueState<StateBackendTestBase.TestPojo> state = backend.getOrCreateKeyedState(VoidNamespaceSerializer.INSTANCE, kvId);
        Assert.assertTrue((state instanceof InternalValueState));
        ((InternalValueState) (state)).setCurrentNamespace(VoidNamespace.INSTANCE);
        // we will be expecting ExpectedKryoTestException to be thrown,
        // because the ExceptionThrowingTestSerializer should be used
        int numExceptions = 0;
        backend.setCurrentKey(1);
        try {
            // backends that eagerly serializes (such as RocksDB) will fail here
            state.update(new StateBackendTestBase.TestPojo("u1", 1));
        } catch (StateBackendTestBase.ExpectedKryoTestException e) {
            numExceptions++;
        } catch (Exception e) {
            if ((e.getCause()) instanceof StateBackendTestBase.ExpectedKryoTestException) {
                numExceptions++;
            } else {
                throw e;
            }
        }
        try {
            // backends that lazily serializes (such as memory state backend) will fail here
            runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        } catch (StateBackendTestBase.ExpectedKryoTestException e) {
            numExceptions++;
        } catch (Exception e) {
            if ((e.getCause()) instanceof StateBackendTestBase.ExpectedKryoTestException) {
                numExceptions++;
            } else {
                throw e;
            }
        }
        Assert.assertEquals("Didn't see the expected Kryo exception.", 1, numExceptions);
        backend.dispose();
    }

    @Test
    public void testBackendUsesRegisteredKryoSerializer() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        Environment env = new DummyEnvironment();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE, env);
        env.getExecutionConfig().registerTypeWithKryoSerializer(StateBackendTestBase.TestPojo.class, StateBackendTestBase.ExceptionThrowingTestSerializer.class);
        TypeInformation<StateBackendTestBase.TestPojo> pojoType = new org.apache.flink.api.java.typeutils.GenericTypeInfo(StateBackendTestBase.TestPojo.class);
        // make sure that we are in fact using the KryoSerializer
        Assert.assertTrue(((pojoType.createSerializer(env.getExecutionConfig())) instanceof KryoSerializer));
        ValueStateDescriptor<StateBackendTestBase.TestPojo> kvId = new ValueStateDescriptor("id", pojoType);
        ValueState<StateBackendTestBase.TestPojo> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        // we will be expecting ExpectedKryoTestException to be thrown,
        // because the ExceptionThrowingTestSerializer should be used
        int numExceptions = 0;
        backend.setCurrentKey(1);
        try {
            // backends that eagerly serializes (such as RocksDB) will fail here
            state.update(new StateBackendTestBase.TestPojo("u1", 1));
        } catch (StateBackendTestBase.ExpectedKryoTestException e) {
            numExceptions++;
        } catch (Exception e) {
            if ((e.getCause()) instanceof StateBackendTestBase.ExpectedKryoTestException) {
                numExceptions++;
            } else {
                throw e;
            }
        }
        try {
            // backends that lazily serializes (such as memory state backend) will fail here
            runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        } catch (StateBackendTestBase.ExpectedKryoTestException e) {
            numExceptions++;
        } catch (Exception e) {
            if ((e.getCause()) instanceof StateBackendTestBase.ExpectedKryoTestException) {
                numExceptions++;
            } else {
                throw e;
            }
        }
        Assert.assertEquals("Didn't see the expected Kryo exception.", 1, numExceptions);
        backend.dispose();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBackendUsesRegisteredKryoSerializerUsingGetOrCreate() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        Environment env = new DummyEnvironment();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE, env);
        env.getExecutionConfig().registerTypeWithKryoSerializer(StateBackendTestBase.TestPojo.class, StateBackendTestBase.ExceptionThrowingTestSerializer.class);
        TypeInformation<StateBackendTestBase.TestPojo> pojoType = new org.apache.flink.api.java.typeutils.GenericTypeInfo(StateBackendTestBase.TestPojo.class);
        // make sure that we are in fact using the KryoSerializer
        Assert.assertTrue(((pojoType.createSerializer(env.getExecutionConfig())) instanceof KryoSerializer));
        ValueStateDescriptor<StateBackendTestBase.TestPojo> kvId = new ValueStateDescriptor("id", pojoType);
        ValueState<StateBackendTestBase.TestPojo> state = backend.getOrCreateKeyedState(VoidNamespaceSerializer.INSTANCE, kvId);
        Assert.assertTrue((state instanceof InternalValueState));
        ((InternalValueState) (state)).setCurrentNamespace(VoidNamespace.INSTANCE);
        // we will be expecting ExpectedKryoTestException to be thrown,
        // because the ExceptionThrowingTestSerializer should be used
        int numExceptions = 0;
        backend.setCurrentKey(1);
        try {
            // backends that eagerly serializes (such as RocksDB) will fail here
            state.update(new StateBackendTestBase.TestPojo("u1", 1));
        } catch (StateBackendTestBase.ExpectedKryoTestException e) {
            numExceptions++;
        } catch (Exception e) {
            if ((e.getCause()) instanceof StateBackendTestBase.ExpectedKryoTestException) {
                numExceptions++;
            } else {
                throw e;
            }
        }
        try {
            // backends that lazily serializes (such as memory state backend) will fail here
            runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        } catch (StateBackendTestBase.ExpectedKryoTestException e) {
            numExceptions++;
        } catch (Exception e) {
            if ((e.getCause()) instanceof StateBackendTestBase.ExpectedKryoTestException) {
                numExceptions++;
            } else {
                throw e;
            }
        }
        Assert.assertEquals("Didn't see the expected Kryo exception.", 1, numExceptions);
        backend.dispose();
    }

    /**
     * Verify state restore resilience when:
     *  - snapshot was taken without any Kryo registrations, specific serializers or default serializers for the state type
     *  - restored with the state type registered (no specific serializer)
     *
     * This test should not fail, because de- / serialization of the state should not be performed with Kryo's default
     * {@link com.esotericsoftware.kryo.serializers.FieldSerializer}.
     */
    @Test
    public void testKryoRegisteringRestoreResilienceWithRegisteredType() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        Environment env = new DummyEnvironment();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE, env);
        TypeInformation<StateBackendTestBase.TestPojo> pojoType = new org.apache.flink.api.java.typeutils.GenericTypeInfo(StateBackendTestBase.TestPojo.class);
        // make sure that we are in fact using the KryoSerializer
        Assert.assertTrue(((pojoType.createSerializer(env.getExecutionConfig())) instanceof KryoSerializer));
        ValueStateDescriptor<StateBackendTestBase.TestPojo> kvId = new ValueStateDescriptor("id", pojoType);
        ValueState<StateBackendTestBase.TestPojo> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        // ============== create snapshot - no Kryo registration or specific / default serializers ==============
        // make some more modifications
        backend.setCurrentKey(1);
        state.update(new StateBackendTestBase.TestPojo("u1", 1));
        backend.setCurrentKey(2);
        state.update(new StateBackendTestBase.TestPojo("u2", 2));
        KeyedStateHandle snapshot = runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        backend.dispose();
        // ====================================== restore snapshot  ======================================
        env.getExecutionConfig().registerKryoType(StateBackendTestBase.TestPojo.class);
        backend = restoreKeyedBackend(INSTANCE, snapshot, env);
        snapshot.discardState();
        state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        backend.setCurrentKey(1);
        Assert.assertEquals(state.value(), new StateBackendTestBase.TestPojo("u1", 1));
        backend.setCurrentKey(2);
        Assert.assertEquals(state.value(), new StateBackendTestBase.TestPojo("u2", 2));
        backend.dispose();
    }

    /**
     * Verify state restore resilience when:
     *  - snapshot was taken without any Kryo registrations, specific serializers or default serializers for the state type
     *  - restored with a default serializer for the state type
     *
     * <p> The default serializer used on restore is {@link CustomKryoTestSerializer}, which deliberately
     * fails only on deserialization. We use the deliberate deserialization failure to acknowledge test success.
     *
     * @throws Exception
     * 		expects {@link ExpectedKryoTestException} to be thrown.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testKryoRegisteringRestoreResilienceWithDefaultSerializer() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        Environment env = new DummyEnvironment();
        AbstractKeyedStateBackend<Integer> backend = null;
        try {
            backend = createKeyedBackend(INSTANCE, env);
            TypeInformation<StateBackendTestBase.TestPojo> pojoType = new org.apache.flink.api.java.typeutils.GenericTypeInfo(StateBackendTestBase.TestPojo.class);
            // make sure that we are in fact using the KryoSerializer
            Assert.assertTrue(((pojoType.createSerializer(env.getExecutionConfig())) instanceof KryoSerializer));
            ValueStateDescriptor<StateBackendTestBase.TestPojo> kvId = new ValueStateDescriptor("id", pojoType);
            ValueState<StateBackendTestBase.TestPojo> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
            // ============== create snapshot - no Kryo registration or specific / default serializers ==============
            // make some more modifications
            backend.setCurrentKey(1);
            state.update(new StateBackendTestBase.TestPojo("u1", 1));
            backend.setCurrentKey(2);
            state.update(new StateBackendTestBase.TestPojo("u2", 2));
            KeyedStateHandle snapshot = runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
            backend.dispose();
            // ========== restore snapshot - should use default serializer (ONLY SERIALIZATION) ==========
            // cast because our test serializer is not typed to TestPojo
            env.getExecutionConfig().addDefaultKryoSerializer(StateBackendTestBase.TestPojo.class, ((Class) (StateBackendTestBase.CustomKryoTestSerializer.class)));
            backend = restoreKeyedBackend(INSTANCE, snapshot, env);
            // re-initialize to ensure that we create the KryoSerializer from scratch, otherwise
            // initializeSerializerUnlessSet would not pick up our new config
            kvId = new ValueStateDescriptor("id", pojoType);
            state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
            backend.setCurrentKey(1);
            // update to test state backends that eagerly serialize, such as RocksDB
            state.update(new StateBackendTestBase.TestPojo("u1", 11));
            KeyedStateHandle snapshot2 = runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
            snapshot.discardState();
            backend.dispose();
            // ========= restore snapshot - should use default serializer (FAIL ON DESERIALIZATION) =========
            // cast because our test serializer is not typed to TestPojo
            env.getExecutionConfig().addDefaultKryoSerializer(StateBackendTestBase.TestPojo.class, ((Class) (StateBackendTestBase.CustomKryoTestSerializer.class)));
            // on the second restore, since the custom serializer will be used for
            // deserialization, we expect the deliberate failure to be thrown
            expectedException.expect(CoreMatchers.anyOf(CoreMatchers.isA(StateBackendTestBase.ExpectedKryoTestException.class), Matchers.<Throwable>hasProperty("cause", CoreMatchers.isA(StateBackendTestBase.ExpectedKryoTestException.class))));
            // state backends that eagerly deserializes (such as the memory state backend) will fail here
            backend = restoreKeyedBackend(INSTANCE, snapshot2, env);
            state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
            backend.setCurrentKey(1);
            // state backends that lazily deserializes (such as RocksDB) will fail here
            state.value();
            snapshot2.discardState();
            backend.dispose();
        } finally {
            // ensure to release native resources even when we exit through exception
            IOUtils.closeQuietly(backend);
            backend.dispose();
        }
    }

    /**
     * Verify state restore resilience when:
     *  - snapshot was taken without any Kryo registrations, specific serializers or default serializers for the state type
     *  - restored with a specific serializer for the state type
     *
     * <p> The specific serializer used on restore is {@link CustomKryoTestSerializer}, which deliberately
     * fails only on deserialization. We use the deliberate deserialization failure to acknowledge test success.
     *
     * @throws Exception
     * 		expects {@link ExpectedKryoTestException} to be thrown.
     */
    @Test
    public void testKryoRegisteringRestoreResilienceWithRegisteredSerializer() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        Environment env = new DummyEnvironment();
        AbstractKeyedStateBackend<Integer> backend = null;
        try {
            backend = createKeyedBackend(INSTANCE, env);
            TypeInformation<StateBackendTestBase.TestPojo> pojoType = new org.apache.flink.api.java.typeutils.GenericTypeInfo(StateBackendTestBase.TestPojo.class);
            // make sure that we are in fact using the KryoSerializer
            Assert.assertTrue(((pojoType.createSerializer(env.getExecutionConfig())) instanceof KryoSerializer));
            ValueStateDescriptor<StateBackendTestBase.TestPojo> kvId = new ValueStateDescriptor("id", pojoType);
            ValueState<StateBackendTestBase.TestPojo> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
            // ============== create snapshot - no Kryo registration or specific / default serializers ==============
            // make some more modifications
            backend.setCurrentKey(1);
            state.update(new StateBackendTestBase.TestPojo("u1", 1));
            backend.setCurrentKey(2);
            state.update(new StateBackendTestBase.TestPojo("u2", 2));
            KeyedStateHandle snapshot = runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
            backend.dispose();
            // ========== restore snapshot - should use specific serializer (ONLY SERIALIZATION) ==========
            env.getExecutionConfig().registerTypeWithKryoSerializer(StateBackendTestBase.TestPojo.class, StateBackendTestBase.CustomKryoTestSerializer.class);
            backend = restoreKeyedBackend(INSTANCE, snapshot, env);
            // re-initialize to ensure that we create the KryoSerializer from scratch, otherwise
            // initializeSerializerUnlessSet would not pick up our new config
            kvId = new ValueStateDescriptor("id", pojoType);
            state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
            backend.setCurrentKey(1);
            // update to test state backends that eagerly serialize, such as RocksDB
            state.update(new StateBackendTestBase.TestPojo("u1", 11));
            KeyedStateHandle snapshot2 = runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
            snapshot.discardState();
            backend.dispose();
            // ========= restore snapshot - should use specific serializer (FAIL ON DESERIALIZATION) =========
            env.getExecutionConfig().registerTypeWithKryoSerializer(StateBackendTestBase.TestPojo.class, StateBackendTestBase.CustomKryoTestSerializer.class);
            // on the second restore, since the custom serializer will be used for
            // deserialization, we expect the deliberate failure to be thrown
            expectedException.expect(CoreMatchers.anyOf(CoreMatchers.isA(StateBackendTestBase.ExpectedKryoTestException.class), Matchers.<Throwable>hasProperty("cause", CoreMatchers.isA(StateBackendTestBase.ExpectedKryoTestException.class))));
            // state backends that eagerly deserializes (such as the memory state backend) will fail here
            backend = restoreKeyedBackend(INSTANCE, snapshot2, env);
            state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
            backend.setCurrentKey(1);
            // state backends that lazily deserializes (such as RocksDB) will fail here
            state.value();
            backend.dispose();
        } finally {
            // ensure that native resources are also released in case of exception
            if (backend != null) {
                backend.dispose();
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testKryoRestoreResilienceWithDifferentRegistrationOrder() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        Environment env = new DummyEnvironment();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        // register A first then B
        env.getExecutionConfig().registerKryoType(StateBackendTestBase.TestNestedPojoClassA.class);
        env.getExecutionConfig().registerKryoType(StateBackendTestBase.TestNestedPojoClassB.class);
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE, env);
        try {
            TypeInformation<StateBackendTestBase.TestPojo> pojoType = new org.apache.flink.api.java.typeutils.GenericTypeInfo(StateBackendTestBase.TestPojo.class);
            // make sure that we are in fact using the KryoSerializer
            Assert.assertTrue(((pojoType.createSerializer(env.getExecutionConfig())) instanceof KryoSerializer));
            ValueStateDescriptor<StateBackendTestBase.TestPojo> kvId = new ValueStateDescriptor("id", pojoType);
            ValueState<StateBackendTestBase.TestPojo> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
            // access the internal state representation to retrieve the original Kryo registration ids;
            // these will be later used to check that on restore, the new Kryo serializer has reconfigured itself to
            // have identical mappings
            InternalKvState internalKvState = ((InternalKvState) (state));
            KryoSerializer<StateBackendTestBase.TestPojo> kryoSerializer = ((KryoSerializer<StateBackendTestBase.TestPojo>) (internalKvState.getValueSerializer()));
            int mainPojoClassRegistrationId = kryoSerializer.getKryo().getRegistration(StateBackendTestBase.TestPojo.class).getId();
            int nestedPojoClassARegistrationId = kryoSerializer.getKryo().getRegistration(StateBackendTestBase.TestNestedPojoClassA.class).getId();
            int nestedPojoClassBRegistrationId = kryoSerializer.getKryo().getRegistration(StateBackendTestBase.TestNestedPojoClassB.class).getId();
            // ============== create snapshot of current configuration ==============
            // make some more modifications
            backend.setCurrentKey(1);
            state.update(new StateBackendTestBase.TestPojo("u1", 1, new StateBackendTestBase.TestNestedPojoClassA(1.0, 2), new StateBackendTestBase.TestNestedPojoClassB(2.3, "foo")));
            backend.setCurrentKey(2);
            state.update(new StateBackendTestBase.TestPojo("u2", 2, new StateBackendTestBase.TestNestedPojoClassA(2.0, 5), new StateBackendTestBase.TestNestedPojoClassB(3.1, "bar")));
            KeyedStateHandle snapshot = runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
            backend.dispose();
            // ========== restore snapshot, with a different registration order in the configuration ==========
            env = new DummyEnvironment();
            env.getExecutionConfig().registerKryoType(StateBackendTestBase.TestNestedPojoClassB.class);// this time register B first

            env.getExecutionConfig().registerKryoType(StateBackendTestBase.TestNestedPojoClassA.class);
            backend = restoreKeyedBackend(INSTANCE, snapshot, env);
            // re-initialize to ensure that we create the KryoSerializer from scratch, otherwise
            // initializeSerializerUnlessSet would not pick up our new config
            kvId = new ValueStateDescriptor("id", pojoType);
            state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
            // verify that on restore, the serializer that the state handle uses has reconfigured itself to have
            // identical Kryo registration ids compared to the previous execution
            internalKvState = ((InternalKvState) (state));
            kryoSerializer = ((KryoSerializer<StateBackendTestBase.TestPojo>) (internalKvState.getValueSerializer()));
            Assert.assertEquals(mainPojoClassRegistrationId, kryoSerializer.getKryo().getRegistration(StateBackendTestBase.TestPojo.class).getId());
            Assert.assertEquals(nestedPojoClassARegistrationId, kryoSerializer.getKryo().getRegistration(StateBackendTestBase.TestNestedPojoClassA.class).getId());
            Assert.assertEquals(nestedPojoClassBRegistrationId, kryoSerializer.getKryo().getRegistration(StateBackendTestBase.TestNestedPojoClassB.class).getId());
            backend.setCurrentKey(1);
            // update to test state backends that eagerly serialize, such as RocksDB
            state.update(new StateBackendTestBase.TestPojo("u1", 11, new StateBackendTestBase.TestNestedPojoClassA(22.1, 12), new StateBackendTestBase.TestNestedPojoClassB(1.23, "foobar")));
            // this tests backends that lazily serialize, such as memory state backend
            runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
            snapshot.discardState();
        } finally {
            backend.dispose();
        }
    }

    @Test
    public void testPojoRestoreResilienceWithDifferentRegistrationOrder() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        Environment env = new DummyEnvironment();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        // register A first then B
        env.getExecutionConfig().registerPojoType(StateBackendTestBase.TestNestedPojoClassA.class);
        env.getExecutionConfig().registerPojoType(StateBackendTestBase.TestNestedPojoClassB.class);
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE, env);
        try {
            TypeInformation<StateBackendTestBase.TestPojo> pojoType = TypeExtractor.getForClass(StateBackendTestBase.TestPojo.class);
            // make sure that we are in fact using the PojoSerializer
            Assert.assertTrue(((pojoType.createSerializer(env.getExecutionConfig())) instanceof PojoSerializer));
            ValueStateDescriptor<StateBackendTestBase.TestPojo> kvId = new ValueStateDescriptor("id", pojoType);
            ValueState<StateBackendTestBase.TestPojo> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
            // ============== create snapshot of current configuration ==============
            // make some more modifications
            backend.setCurrentKey(1);
            state.update(new StateBackendTestBase.TestPojo("u1", 1, new StateBackendTestBase.TestNestedPojoClassA(1.0, 2), new StateBackendTestBase.TestNestedPojoClassB(2.3, "foo")));
            backend.setCurrentKey(2);
            state.update(new StateBackendTestBase.TestPojo("u2", 2, new StateBackendTestBase.TestNestedPojoClassA(2.0, 5), new StateBackendTestBase.TestNestedPojoClassB(3.1, "bar")));
            KeyedStateHandle snapshot = runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
            backend.dispose();
            // ========== restore snapshot, with a different registration order in the configuration ==========
            env = new DummyEnvironment();
            env.getExecutionConfig().registerPojoType(StateBackendTestBase.TestNestedPojoClassB.class);// this time register B first

            env.getExecutionConfig().registerPojoType(StateBackendTestBase.TestNestedPojoClassA.class);
            backend = restoreKeyedBackend(INSTANCE, snapshot, env);
            // re-initialize to ensure that we create the PojoSerializer from scratch, otherwise
            // initializeSerializerUnlessSet would not pick up our new config
            kvId = new ValueStateDescriptor("id", pojoType);
            state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
            backend.setCurrentKey(1);
            // update to test state backends that eagerly serialize, such as RocksDB
            state.update(new StateBackendTestBase.TestPojo("u1", 11, new StateBackendTestBase.TestNestedPojoClassA(22.1, 12), new StateBackendTestBase.TestNestedPojoClassB(1.23, "foobar")));
            // this tests backends that lazily serialize, such as memory state backend
            runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
            snapshot.discardState();
        } finally {
            backend.dispose();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testValueState() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        ValueStateDescriptor<String> kvId = new ValueStateDescriptor("id", String.class);
        TypeSerializer<Integer> keySerializer = IntSerializer.INSTANCE;
        TypeSerializer<VoidNamespace> namespaceSerializer = INSTANCE;
        ValueState<String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        @SuppressWarnings("unchecked")
        InternalKvState<Integer, VoidNamespace, String> kvState = ((InternalKvState<Integer, VoidNamespace, String>) (state));
        // this is only available after the backend initialized the serializer
        TypeSerializer<String> valueSerializer = kvId.getSerializer();
        // some modifications to the state
        backend.setCurrentKey(1);
        Assert.assertNull(state.value());
        Assert.assertNull(StateBackendTestBase.getSerializedValue(kvState, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        state.update("1");
        backend.setCurrentKey(2);
        Assert.assertNull(state.value());
        Assert.assertNull(StateBackendTestBase.getSerializedValue(kvState, 2, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        state.update("2");
        backend.setCurrentKey(1);
        Assert.assertEquals("1", state.value());
        Assert.assertEquals("1", StateBackendTestBase.getSerializedValue(kvState, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        // draw a snapshot
        KeyedStateHandle snapshot1 = runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        // make some more modifications
        backend.setCurrentKey(1);
        state.update("u1");
        backend.setCurrentKey(2);
        state.update("u2");
        backend.setCurrentKey(3);
        state.update("u3");
        // draw another snapshot
        KeyedStateHandle snapshot2 = runSnapshot(backend.snapshot(682375462379L, 4, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        // validate the original state
        backend.setCurrentKey(1);
        Assert.assertEquals("u1", state.value());
        Assert.assertEquals("u1", StateBackendTestBase.getSerializedValue(kvState, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.setCurrentKey(2);
        Assert.assertEquals("u2", state.value());
        Assert.assertEquals("u2", StateBackendTestBase.getSerializedValue(kvState, 2, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.setCurrentKey(3);
        Assert.assertEquals("u3", state.value());
        Assert.assertEquals("u3", StateBackendTestBase.getSerializedValue(kvState, 3, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.dispose();
        backend = restoreKeyedBackend(INSTANCE, snapshot1);
        snapshot1.discardState();
        ValueState<String> restored1 = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        @SuppressWarnings("unchecked")
        InternalKvState<Integer, VoidNamespace, String> restoredKvState1 = ((InternalKvState<Integer, VoidNamespace, String>) (restored1));
        backend.setCurrentKey(1);
        Assert.assertEquals("1", restored1.value());
        Assert.assertEquals("1", StateBackendTestBase.getSerializedValue(restoredKvState1, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.setCurrentKey(2);
        Assert.assertEquals("2", restored1.value());
        Assert.assertEquals("2", StateBackendTestBase.getSerializedValue(restoredKvState1, 2, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.dispose();
        backend = restoreKeyedBackend(INSTANCE, snapshot2);
        snapshot2.discardState();
        ValueState<String> restored2 = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        @SuppressWarnings("unchecked")
        InternalKvState<Integer, VoidNamespace, String> restoredKvState2 = ((InternalKvState<Integer, VoidNamespace, String>) (restored2));
        backend.setCurrentKey(1);
        Assert.assertEquals("u1", restored2.value());
        Assert.assertEquals("u1", StateBackendTestBase.getSerializedValue(restoredKvState2, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.setCurrentKey(2);
        Assert.assertEquals("u2", restored2.value());
        Assert.assertEquals("u2", StateBackendTestBase.getSerializedValue(restoredKvState2, 2, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.setCurrentKey(3);
        Assert.assertEquals("u3", restored2.value());
        Assert.assertEquals("u3", StateBackendTestBase.getSerializedValue(restoredKvState2, 3, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.dispose();
    }

    @Test
    public void testValueStateWorkWithTtl() throws Exception {
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        try {
            ValueStateDescriptor<StateBackendTestBase.MutableLong> kvId = new ValueStateDescriptor("id", StateBackendTestBase.MutableLong.class);
            kvId.enableTimeToLive(StateTtlConfig.newBuilder(Time.seconds(1)).build());
            ValueState<StateBackendTestBase.MutableLong> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
            backend.setCurrentKey(1);
            state.update(new StateBackendTestBase.MutableLong());
            state.value();
        } finally {
            backend.close();
            backend.dispose();
        }
    }

    /**
     * Tests {@link ValueState#value()} and
     * {@link InternalKvState#getSerializedValue(byte[], TypeSerializer, TypeSerializer, TypeSerializer)}
     * accessing the state concurrently. They should not get in the way of each
     * other.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testValueStateRace() throws Exception {
        final AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        final Integer namespace = 1;
        final ValueStateDescriptor<String> kvId = new ValueStateDescriptor("id", String.class);
        final TypeSerializer<Integer> keySerializer = IntSerializer.INSTANCE;
        final TypeSerializer<Integer> namespaceSerializer = IntSerializer.INSTANCE;
        final ValueState<String> state = backend.getPartitionedState(namespace, INSTANCE, kvId);
        // this is only available after the backend initialized the serializer
        final TypeSerializer<String> valueSerializer = kvId.getSerializer();
        @SuppressWarnings("unchecked")
        final InternalKvState<Integer, Integer, String> kvState = ((InternalKvState<Integer, Integer, String>) (state));
        /**
         * 1) Test that ValueState#value() before and after
         * KvState#getSerializedValue(byte[]) return the same value.
         */
        // set some key and namespace
        final int key1 = 1;
        backend.setCurrentKey(key1);
        kvState.setCurrentNamespace(2);
        state.update("2");
        Assert.assertEquals("2", state.value());
        // query another key and namespace
        Assert.assertNull(StateBackendTestBase.getSerializedValue(kvState, 3, keySerializer, namespace, INSTANCE, valueSerializer));
        // the state should not have changed!
        Assert.assertEquals("2", state.value());
        // re-set values
        kvState.setCurrentNamespace(namespace);
        /**
         * 2) Test two threads concurrently using ValueState#value() and
         * KvState#getSerializedValue(byte[]).
         */
        // some modifications to the state
        final int key2 = 10;
        backend.setCurrentKey(key2);
        Assert.assertNull(state.value());
        Assert.assertNull(StateBackendTestBase.getSerializedValue(kvState, key2, keySerializer, namespace, namespaceSerializer, valueSerializer));
        state.update("1");
        final CheckedThread getter = new CheckedThread("State getter") {
            @Override
            public void go() throws Exception {
                while (!(isInterrupted())) {
                    Assert.assertEquals("1", state.value());
                } 
            }
        };
        final CheckedThread serializedGetter = new CheckedThread("Serialized state getter") {
            @Override
            public void go() throws Exception {
                while ((!(isInterrupted())) && (getter.isAlive())) {
                    final String serializedValue = StateBackendTestBase.getSerializedValue(kvState, key2, keySerializer, namespace, namespaceSerializer, valueSerializer);
                    Assert.assertEquals("1", serializedValue);
                } 
            }
        };
        getter.start();
        serializedGetter.start();
        // run both threads for max 100ms
        Timer t = new Timer("stopper");
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                getter.interrupt();
                serializedGetter.interrupt();
                this.cancel();
            }
        }, 100);
        // wait for both threads to finish
        try {
            // serializedGetter will finish if its assertion fails or if
            // getter is not alive any more
            serializedGetter.sync();
            // if serializedGetter crashed, getter will not know -> interrupt just in case
            getter.interrupt();
            getter.sync();
            t.cancel();// if not executed yet

        } finally {
            // clean up
            backend.dispose();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultipleValueStates() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE, 1, new KeyGroupRange(0, 0), new DummyEnvironment());
        ValueStateDescriptor<String> desc1 = new ValueStateDescriptor("a-string", StringSerializer.INSTANCE);
        ValueStateDescriptor<Integer> desc2 = new ValueStateDescriptor("an-integer", IntSerializer.INSTANCE);
        ValueState<String> state1 = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, desc1);
        ValueState<Integer> state2 = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, desc2);
        // some modifications to the state
        backend.setCurrentKey(1);
        Assert.assertNull(state1.value());
        Assert.assertNull(state2.value());
        state1.update("1");
        // state2 should still have nothing
        Assert.assertEquals("1", state1.value());
        Assert.assertNull(state2.value());
        state2.update(13);
        // both have some state now
        Assert.assertEquals("1", state1.value());
        Assert.assertEquals(13, ((int) (state2.value())));
        // draw a snapshot
        KeyedStateHandle snapshot1 = runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        backend.dispose();
        backend = restoreKeyedBackend(INSTANCE, 1, new KeyGroupRange(0, 0), Collections.singletonList(snapshot1), new DummyEnvironment());
        snapshot1.discardState();
        backend.setCurrentKey(1);
        state1 = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, desc1);
        state2 = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, desc2);
        // verify that they are still the same
        Assert.assertEquals("1", state1.value());
        Assert.assertEquals(13, ((int) (state2.value())));
        backend.dispose();
    }

    /**
     * This test verifies that passing {@code null} to {@link ValueState#update(Object)} acts
     * the same as {@link ValueState#clear()}.
     *
     * @throws Exception
     * 		
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testValueStateNullUpdate() throws Exception {
        // precondition: LongSerializer must fail on null value. this way the test would fail
        // later if null values where actually stored in the state instead of acting as clear()
        try {
            LongSerializer.INSTANCE.serialize(null, new DataOutputViewStreamWrapper(new ByteArrayOutputStream()));
            Assert.fail("Should fail with NullPointerException");
        } catch (NullPointerException e) {
            // alrighty
        }
        CheckpointStreamFactory streamFactory = createStreamFactory();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        ValueStateDescriptor<Long> kvId = new ValueStateDescriptor("id", LongSerializer.INSTANCE, 42L);
        ValueState<Long> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        // some modifications to the state
        backend.setCurrentKey(1);
        // verify default value
        Assert.assertEquals(42L, ((long) (state.value())));
        state.update(1L);
        Assert.assertEquals(1L, ((long) (state.value())));
        backend.setCurrentKey(2);
        Assert.assertEquals(42L, ((long) (state.value())));
        backend.setCurrentKey(1);
        state.clear();
        Assert.assertEquals(42L, ((long) (state.value())));
        state.update(17L);
        Assert.assertEquals(17L, ((long) (state.value())));
        state.update(null);
        Assert.assertEquals(42L, ((long) (state.value())));
        // draw a snapshot
        KeyedStateHandle snapshot1 = runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        backend.dispose();
        backend = restoreKeyedBackend(INSTANCE, snapshot1);
        snapshot1.discardState();
        backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        backend.dispose();
    }

    @Test
    @SuppressWarnings("unchecked,rawtypes")
    public void testListState() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        ListStateDescriptor<String> kvId = new ListStateDescriptor("id", String.class);
        TypeSerializer<Integer> keySerializer = IntSerializer.INSTANCE;
        TypeSerializer<VoidNamespace> namespaceSerializer = INSTANCE;
        ListState<String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        @SuppressWarnings("unchecked")
        InternalKvState<Integer, VoidNamespace, String> kvState = ((InternalKvState<Integer, VoidNamespace, String>) (state));
        // this is only available after the backend initialized the serializer
        TypeSerializer<String> valueSerializer = kvId.getElementSerializer();
        Joiner joiner = Joiner.on(",");
        // some modifications to the state
        backend.setCurrentKey(1);
        Assert.assertNull(state.get());
        Assert.assertNull(StateBackendTestBase.getSerializedList(kvState, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        state.add("1");
        backend.setCurrentKey(2);
        Assert.assertNull(state.get());
        Assert.assertNull(StateBackendTestBase.getSerializedList(kvState, 2, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        state.update(Arrays.asList("2"));
        backend.setCurrentKey(1);
        Assert.assertEquals("1", joiner.join(state.get()));
        Assert.assertEquals("1", joiner.join(StateBackendTestBase.getSerializedList(kvState, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer)));
        // draw a snapshot
        KeyedStateHandle snapshot1 = runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        // make some more modifications
        backend.setCurrentKey(1);
        state.add("u1");
        backend.setCurrentKey(2);
        state.add("u2");
        backend.setCurrentKey(3);
        state.add("u3");
        // draw another snapshot
        KeyedStateHandle snapshot2 = runSnapshot(backend.snapshot(682375462379L, 4, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        // validate the original state
        backend.setCurrentKey(1);
        Assert.assertEquals("1,u1", joiner.join(state.get()));
        Assert.assertEquals("1,u1", joiner.join(StateBackendTestBase.getSerializedList(kvState, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer)));
        backend.setCurrentKey(2);
        Assert.assertEquals("2,u2", joiner.join(state.get()));
        Assert.assertEquals("2,u2", joiner.join(StateBackendTestBase.getSerializedList(kvState, 2, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer)));
        backend.setCurrentKey(3);
        Assert.assertEquals("u3", joiner.join(state.get()));
        Assert.assertEquals("u3", joiner.join(StateBackendTestBase.getSerializedList(kvState, 3, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer)));
        backend.dispose();
        // restore the first snapshot and validate it
        backend = restoreKeyedBackend(INSTANCE, snapshot1);
        snapshot1.discardState();
        ListState<String> restored1 = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        @SuppressWarnings("unchecked")
        InternalKvState<Integer, VoidNamespace, String> restoredKvState1 = ((InternalKvState<Integer, VoidNamespace, String>) (restored1));
        backend.setCurrentKey(1);
        Assert.assertEquals("1", joiner.join(restored1.get()));
        Assert.assertEquals("1", joiner.join(StateBackendTestBase.getSerializedList(restoredKvState1, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer)));
        backend.setCurrentKey(2);
        Assert.assertEquals("2", joiner.join(restored1.get()));
        Assert.assertEquals("2", joiner.join(StateBackendTestBase.getSerializedList(restoredKvState1, 2, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer)));
        backend.dispose();
        // restore the second snapshot and validate it
        backend = restoreKeyedBackend(INSTANCE, snapshot2);
        snapshot2.discardState();
        ListState<String> restored2 = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        @SuppressWarnings("unchecked")
        InternalKvState<Integer, VoidNamespace, String> restoredKvState2 = ((InternalKvState<Integer, VoidNamespace, String>) (restored2));
        backend.setCurrentKey(1);
        Assert.assertEquals("1,u1", joiner.join(restored2.get()));
        Assert.assertEquals("1,u1", joiner.join(StateBackendTestBase.getSerializedList(restoredKvState2, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer)));
        backend.setCurrentKey(2);
        Assert.assertEquals("2,u2", joiner.join(restored2.get()));
        Assert.assertEquals("2,u2", joiner.join(StateBackendTestBase.getSerializedList(restoredKvState2, 2, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer)));
        backend.setCurrentKey(3);
        Assert.assertEquals("u3", joiner.join(restored2.get()));
        Assert.assertEquals("u3", joiner.join(StateBackendTestBase.getSerializedList(restoredKvState2, 3, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer)));
        backend.dispose();
    }

    /**
     * This test verifies that all ListState implementations are consistent in not allowing
     * adding {@code null}.
     */
    @Test
    public void testListStateAddNull() throws Exception {
        AbstractKeyedStateBackend<String> keyedBackend = createKeyedBackend(StringSerializer.INSTANCE);
        final ListStateDescriptor<Long> stateDescr = new ListStateDescriptor("my-state", Long.class);
        try {
            ListState<Long> state = keyedBackend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, stateDescr);
            keyedBackend.setCurrentKey("abc");
            Assert.assertNull(state.get());
            expectedException.expect(NullPointerException.class);
            state.add(null);
        } finally {
            keyedBackend.close();
            keyedBackend.dispose();
        }
    }

    /**
     * This test verifies that all ListState implementations are consistent in not allowing
     * {@link ListState#addAll(List)} to be called with {@code null} entries in the list of entries
     * to add.
     */
    @Test
    public void testListStateAddAllNullEntries() throws Exception {
        AbstractKeyedStateBackend<String> keyedBackend = createKeyedBackend(StringSerializer.INSTANCE);
        final ListStateDescriptor<Long> stateDescr = new ListStateDescriptor("my-state", Long.class);
        try {
            ListState<Long> state = keyedBackend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, stateDescr);
            keyedBackend.setCurrentKey("abc");
            Assert.assertNull(state.get());
            expectedException.expect(NullPointerException.class);
            List<Long> adding = new ArrayList<>();
            adding.add(3L);
            adding.add(null);
            adding.add(5L);
            state.addAll(adding);
        } finally {
            keyedBackend.close();
            keyedBackend.dispose();
        }
    }

    /**
     * This test verifies that all ListState implementations are consistent in not allowing
     * {@link ListState#addAll(List)} to be called with {@code null}.
     */
    @Test
    public void testListStateAddAllNull() throws Exception {
        AbstractKeyedStateBackend<String> keyedBackend = createKeyedBackend(StringSerializer.INSTANCE);
        final ListStateDescriptor<Long> stateDescr = new ListStateDescriptor("my-state", Long.class);
        try {
            ListState<Long> state = keyedBackend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, stateDescr);
            keyedBackend.setCurrentKey("abc");
            Assert.assertNull(state.get());
            expectedException.expect(NullPointerException.class);
            state.addAll(null);
        } finally {
            keyedBackend.close();
            keyedBackend.dispose();
        }
    }

    /**
     * This test verifies that all ListState implementations are consistent in not allowing
     * {@link ListState#addAll(List)} to be called with {@code null} entries in the list of entries
     * to add.
     */
    @Test
    public void testListStateUpdateNullEntries() throws Exception {
        AbstractKeyedStateBackend<String> keyedBackend = createKeyedBackend(StringSerializer.INSTANCE);
        final ListStateDescriptor<Long> stateDescr = new ListStateDescriptor("my-state", Long.class);
        try {
            ListState<Long> state = keyedBackend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, stateDescr);
            keyedBackend.setCurrentKey("abc");
            Assert.assertNull(state.get());
            expectedException.expect(NullPointerException.class);
            List<Long> adding = new ArrayList<>();
            adding.add(3L);
            adding.add(null);
            adding.add(5L);
            state.update(adding);
        } finally {
            keyedBackend.close();
            keyedBackend.dispose();
        }
    }

    /**
     * This test verifies that all ListState implementations are consistent in not allowing
     * {@link ListState#addAll(List)} to be called with {@code null}.
     */
    @Test
    public void testListStateUpdateNull() throws Exception {
        AbstractKeyedStateBackend<String> keyedBackend = createKeyedBackend(StringSerializer.INSTANCE);
        final ListStateDescriptor<Long> stateDescr = new ListStateDescriptor("my-state", Long.class);
        try {
            ListState<Long> state = keyedBackend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, stateDescr);
            keyedBackend.setCurrentKey("abc");
            Assert.assertNull(state.get());
            expectedException.expect(NullPointerException.class);
            state.update(null);
        } finally {
            keyedBackend.close();
            keyedBackend.dispose();
        }
    }

    @Test
    public void testListStateAPIs() throws Exception {
        AbstractKeyedStateBackend<String> keyedBackend = createKeyedBackend(StringSerializer.INSTANCE);
        final ListStateDescriptor<Long> stateDescr = new ListStateDescriptor("my-state", Long.class);
        try {
            ListState<Long> state = keyedBackend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, stateDescr);
            keyedBackend.setCurrentKey("def");
            Assert.assertNull(state.get());
            state.add(17L);
            state.add(11L);
            Assert.assertThat(state.get(), StateBackendTestBase.containsInAnyOrder(17L, 11L));
            // update(emptyList) should remain the value null
            state.update(Collections.emptyList());
            Assert.assertNull(state.get());
            state.update(Arrays.asList(10L, 16L));
            Assert.assertThat(state.get(), StateBackendTestBase.containsInAnyOrder(16L, 10L));
            Assert.assertThat(state.get(), StateBackendTestBase.containsInAnyOrder(16L, 10L));
            keyedBackend.setCurrentKey("abc");
            Assert.assertNull(state.get());
            keyedBackend.setCurrentKey("g");
            Assert.assertNull(state.get());
            Assert.assertNull(state.get());
            state.addAll(Collections.emptyList());
            Assert.assertNull(state.get());
            state.addAll(Arrays.asList(3L, 4L));
            Assert.assertThat(state.get(), StateBackendTestBase.containsInAnyOrder(3L, 4L));
            Assert.assertThat(state.get(), StateBackendTestBase.containsInAnyOrder(3L, 4L));
            state.addAll(new ArrayList());
            Assert.assertThat(state.get(), StateBackendTestBase.containsInAnyOrder(3L, 4L));
            state.addAll(Arrays.asList(5L, 6L));
            Assert.assertThat(state.get(), StateBackendTestBase.containsInAnyOrder(3L, 4L, 5L, 6L));
            state.addAll(new ArrayList());
            Assert.assertThat(state.get(), StateBackendTestBase.containsInAnyOrder(3L, 4L, 5L, 6L));
            Assert.assertThat(state.get(), StateBackendTestBase.containsInAnyOrder(3L, 4L, 5L, 6L));
            state.update(Arrays.asList(1L, 2L));
            Assert.assertThat(state.get(), StateBackendTestBase.containsInAnyOrder(1L, 2L));
            keyedBackend.setCurrentKey("def");
            Assert.assertThat(state.get(), StateBackendTestBase.containsInAnyOrder(10L, 16L));
            state.clear();
            Assert.assertNull(state.get());
            keyedBackend.setCurrentKey("g");
            state.add(3L);
            state.add(2L);
            state.add(1L);
            keyedBackend.setCurrentKey("def");
            Assert.assertNull(state.get());
            keyedBackend.setCurrentKey("g");
            Assert.assertThat(state.get(), StateBackendTestBase.containsInAnyOrder(1L, 2L, 3L, 2L, 1L));
            state.update(Arrays.asList(5L, 6L));
            Assert.assertThat(state.get(), StateBackendTestBase.containsInAnyOrder(5L, 6L));
            state.clear();
            // make sure all lists / maps are cleared
            Assert.assertThat("State backend is not empty.", keyedBackend.numKeyValueStateEntries(), Is.is(0));
        } finally {
            keyedBackend.close();
            keyedBackend.dispose();
        }
    }

    @Test
    public void testListStateMerging() throws Exception {
        AbstractKeyedStateBackend<String> keyedBackend = createKeyedBackend(StringSerializer.INSTANCE);
        final ListStateDescriptor<Long> stateDescr = new ListStateDescriptor("my-state", Long.class);
        final Integer namespace1 = 1;
        final Integer namespace2 = 2;
        final Integer namespace3 = 3;
        try {
            InternalListState<String, Integer, Long> state = ((InternalListState<String, Integer, Long>) (keyedBackend.getPartitionedState(0, INSTANCE, stateDescr)));
            // populate the different namespaces
            // - abc spreads the values over three namespaces
            // - def spreads the values over two namespaces (one empty)
            // - ghi is empty
            // - jkl has all elements already in the target namespace
            // - mno has all elements already in one source namespace
            keyedBackend.setCurrentKey("abc");
            state.setCurrentNamespace(namespace1);
            state.add(33L);
            state.add(55L);
            state.setCurrentNamespace(namespace2);
            state.add(22L);
            state.add(11L);
            state.setCurrentNamespace(namespace3);
            state.add(44L);
            keyedBackend.setCurrentKey("def");
            state.setCurrentNamespace(namespace1);
            state.add(11L);
            state.add(44L);
            state.setCurrentNamespace(namespace3);
            state.add(22L);
            state.add(55L);
            state.add(33L);
            keyedBackend.setCurrentKey("jkl");
            state.setCurrentNamespace(namespace1);
            state.add(11L);
            state.add(22L);
            state.add(33L);
            state.add(44L);
            state.add(55L);
            keyedBackend.setCurrentKey("mno");
            state.setCurrentNamespace(namespace3);
            state.add(11L);
            state.add(22L);
            state.add(33L);
            state.add(44L);
            state.add(55L);
            keyedBackend.setCurrentKey("abc");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertThat(state.get(), StateBackendTestBase.containsInAnyOrder(11L, 22L, 33L, 44L, 55L));
            keyedBackend.setCurrentKey("def");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertThat(state.get(), StateBackendTestBase.containsInAnyOrder(11L, 22L, 33L, 44L, 55L));
            keyedBackend.setCurrentKey("ghi");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertNull(state.get());
            keyedBackend.setCurrentKey("jkl");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertThat(state.get(), StateBackendTestBase.containsInAnyOrder(11L, 22L, 33L, 44L, 55L));
            keyedBackend.setCurrentKey("mno");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertThat(state.get(), StateBackendTestBase.containsInAnyOrder(11L, 22L, 33L, 44L, 55L));
            // make sure all lists / maps are cleared
            keyedBackend.setCurrentKey("abc");
            state.setCurrentNamespace(namespace1);
            state.clear();
            keyedBackend.setCurrentKey("def");
            state.setCurrentNamespace(namespace1);
            state.clear();
            keyedBackend.setCurrentKey("ghi");
            state.setCurrentNamespace(namespace1);
            state.clear();
            keyedBackend.setCurrentKey("jkl");
            state.setCurrentNamespace(namespace1);
            state.clear();
            keyedBackend.setCurrentKey("mno");
            state.setCurrentNamespace(namespace1);
            state.clear();
            Assert.assertThat("State backend is not empty.", keyedBackend.numKeyValueStateEntries(), Is.is(0));
        } finally {
            keyedBackend.close();
            keyedBackend.dispose();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReducingState() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        ReducingStateDescriptor<String> kvId = new ReducingStateDescriptor("id", new StateBackendTestBase.AppendingReduce(), String.class);
        TypeSerializer<Integer> keySerializer = IntSerializer.INSTANCE;
        TypeSerializer<VoidNamespace> namespaceSerializer = INSTANCE;
        ReducingState<String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        @SuppressWarnings("unchecked")
        InternalKvState<Integer, VoidNamespace, String> kvState = ((InternalKvState<Integer, VoidNamespace, String>) (state));
        // this is only available after the backend initialized the serializer
        TypeSerializer<String> valueSerializer = kvId.getSerializer();
        // some modifications to the state
        backend.setCurrentKey(1);
        Assert.assertNull(state.get());
        Assert.assertNull(StateBackendTestBase.getSerializedValue(kvState, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        state.add("1");
        backend.setCurrentKey(2);
        Assert.assertNull(state.get());
        Assert.assertNull(StateBackendTestBase.getSerializedValue(kvState, 2, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        state.add("2");
        backend.setCurrentKey(1);
        Assert.assertEquals("1", state.get());
        Assert.assertEquals("1", StateBackendTestBase.getSerializedValue(kvState, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        // draw a snapshot
        KeyedStateHandle snapshot1 = runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        // make some more modifications
        backend.setCurrentKey(1);
        state.add("u1");
        backend.setCurrentKey(2);
        state.add("u2");
        backend.setCurrentKey(3);
        state.add("u3");
        // draw another snapshot
        KeyedStateHandle snapshot2 = runSnapshot(backend.snapshot(682375462379L, 4, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        // validate the original state
        backend.setCurrentKey(1);
        Assert.assertEquals("1,u1", state.get());
        Assert.assertEquals("1,u1", StateBackendTestBase.getSerializedValue(kvState, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.setCurrentKey(2);
        Assert.assertEquals("2,u2", state.get());
        Assert.assertEquals("2,u2", StateBackendTestBase.getSerializedValue(kvState, 2, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.setCurrentKey(3);
        Assert.assertEquals("u3", state.get());
        Assert.assertEquals("u3", StateBackendTestBase.getSerializedValue(kvState, 3, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.dispose();
        // restore the first snapshot and validate it
        backend = restoreKeyedBackend(INSTANCE, snapshot1);
        snapshot1.discardState();
        ReducingState<String> restored1 = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        @SuppressWarnings("unchecked")
        InternalKvState<Integer, VoidNamespace, String> restoredKvState1 = ((InternalKvState<Integer, VoidNamespace, String>) (restored1));
        backend.setCurrentKey(1);
        Assert.assertEquals("1", restored1.get());
        Assert.assertEquals("1", StateBackendTestBase.getSerializedValue(restoredKvState1, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.setCurrentKey(2);
        Assert.assertEquals("2", restored1.get());
        Assert.assertEquals("2", StateBackendTestBase.getSerializedValue(restoredKvState1, 2, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.dispose();
        // restore the second snapshot and validate it
        backend = restoreKeyedBackend(INSTANCE, snapshot2);
        snapshot2.discardState();
        ReducingState<String> restored2 = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        @SuppressWarnings("unchecked")
        InternalKvState<Integer, VoidNamespace, String> restoredKvState2 = ((InternalKvState<Integer, VoidNamespace, String>) (restored2));
        backend.setCurrentKey(1);
        Assert.assertEquals("1,u1", restored2.get());
        Assert.assertEquals("1,u1", StateBackendTestBase.getSerializedValue(restoredKvState2, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.setCurrentKey(2);
        Assert.assertEquals("2,u2", restored2.get());
        Assert.assertEquals("2,u2", StateBackendTestBase.getSerializedValue(restoredKvState2, 2, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.setCurrentKey(3);
        Assert.assertEquals("u3", restored2.get());
        Assert.assertEquals("u3", StateBackendTestBase.getSerializedValue(restoredKvState2, 3, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.dispose();
    }

    @Test
    public void testReducingStateAddAndGet() throws Exception {
        final ReducingStateDescriptor<Long> stateDescr = new ReducingStateDescriptor("my-state", ( a, b) -> a + b, Long.class);
        AbstractKeyedStateBackend<String> keyedBackend = createKeyedBackend(StringSerializer.INSTANCE);
        try {
            ReducingState<Long> state = keyedBackend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, stateDescr);
            keyedBackend.setCurrentKey("abc");
            Assert.assertNull(state.get());
            keyedBackend.setCurrentKey("def");
            Assert.assertNull(state.get());
            state.add(17L);
            state.add(11L);
            Assert.assertEquals(28L, state.get().longValue());
            keyedBackend.setCurrentKey("abc");
            Assert.assertNull(state.get());
            keyedBackend.setCurrentKey("g");
            Assert.assertNull(state.get());
            state.add(1L);
            state.add(2L);
            keyedBackend.setCurrentKey("def");
            Assert.assertEquals(28L, state.get().longValue());
            state.clear();
            Assert.assertNull(state.get());
            keyedBackend.setCurrentKey("g");
            state.add(3L);
            state.add(2L);
            state.add(1L);
            keyedBackend.setCurrentKey("def");
            Assert.assertNull(state.get());
            keyedBackend.setCurrentKey("g");
            Assert.assertEquals(9L, state.get().longValue());
            state.clear();
            // make sure all lists / maps are cleared
            Assert.assertThat("State backend is not empty.", keyedBackend.numKeyValueStateEntries(), Is.is(0));
        } finally {
            keyedBackend.close();
            keyedBackend.dispose();
        }
    }

    @Test
    public void testReducingStateMerging() throws Exception {
        final ReducingStateDescriptor<Long> stateDescr = new ReducingStateDescriptor("my-state", ( a, b) -> a + b, Long.class);
        final Integer namespace1 = 1;
        final Integer namespace2 = 2;
        final Integer namespace3 = 3;
        final Long expectedResult = 165L;
        AbstractKeyedStateBackend<String> keyedBackend = createKeyedBackend(StringSerializer.INSTANCE);
        try {
            final InternalReducingState<String, Integer, Long> state = ((InternalReducingState<String, Integer, Long>) (keyedBackend.getPartitionedState(0, INSTANCE, stateDescr)));
            // populate the different namespaces
            // - abc spreads the values over three namespaces
            // - def spreads the values over two namespaces (one empty)
            // - ghi is empty
            // - jkl has all elements already in the target namespace
            // - mno has all elements already in one source namespace
            keyedBackend.setCurrentKey("abc");
            state.setCurrentNamespace(namespace1);
            state.add(33L);
            state.add(55L);
            state.setCurrentNamespace(namespace2);
            state.add(22L);
            state.add(11L);
            state.setCurrentNamespace(namespace3);
            state.add(44L);
            keyedBackend.setCurrentKey("def");
            state.setCurrentNamespace(namespace1);
            state.add(11L);
            state.add(44L);
            state.setCurrentNamespace(namespace3);
            state.add(22L);
            state.add(55L);
            state.add(33L);
            keyedBackend.setCurrentKey("jkl");
            state.setCurrentNamespace(namespace1);
            state.add(11L);
            state.add(22L);
            state.add(33L);
            state.add(44L);
            state.add(55L);
            keyedBackend.setCurrentKey("mno");
            state.setCurrentNamespace(namespace3);
            state.add(11L);
            state.add(22L);
            state.add(33L);
            state.add(44L);
            state.add(55L);
            keyedBackend.setCurrentKey("abc");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertEquals(expectedResult, state.get());
            keyedBackend.setCurrentKey("def");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertEquals(expectedResult, state.get());
            keyedBackend.setCurrentKey("ghi");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertNull(state.get());
            keyedBackend.setCurrentKey("jkl");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertEquals(expectedResult, state.get());
            keyedBackend.setCurrentKey("mno");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertEquals(expectedResult, state.get());
            // make sure all lists / maps are cleared
            keyedBackend.setCurrentKey("abc");
            state.setCurrentNamespace(namespace1);
            state.clear();
            keyedBackend.setCurrentKey("def");
            state.setCurrentNamespace(namespace1);
            state.clear();
            keyedBackend.setCurrentKey("ghi");
            state.setCurrentNamespace(namespace1);
            state.clear();
            keyedBackend.setCurrentKey("jkl");
            state.setCurrentNamespace(namespace1);
            state.clear();
            keyedBackend.setCurrentKey("mno");
            state.setCurrentNamespace(namespace1);
            state.clear();
            Assert.assertThat("State backend is not empty.", keyedBackend.numKeyValueStateEntries(), Is.is(0));
        } finally {
            keyedBackend.close();
            keyedBackend.dispose();
        }
    }

    @Test
    public void testAggregatingStateAddAndGetWithMutableAccumulator() throws Exception {
        final AggregatingStateDescriptor<Long, StateBackendTestBase.MutableLong, Long> stateDescr = new AggregatingStateDescriptor("my-state", new StateBackendTestBase.MutableAggregatingAddingFunction(), StateBackendTestBase.MutableLong.class);
        AbstractKeyedStateBackend<String> keyedBackend = createKeyedBackend(StringSerializer.INSTANCE);
        try {
            AggregatingState<Long, Long> state = keyedBackend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, stateDescr);
            keyedBackend.setCurrentKey("abc");
            Assert.assertNull(state.get());
            keyedBackend.setCurrentKey("def");
            Assert.assertNull(state.get());
            state.add(17L);
            state.add(11L);
            Assert.assertEquals(28L, state.get().longValue());
            keyedBackend.setCurrentKey("abc");
            Assert.assertNull(state.get());
            keyedBackend.setCurrentKey("g");
            Assert.assertNull(state.get());
            state.add(1L);
            state.add(2L);
            keyedBackend.setCurrentKey("def");
            Assert.assertEquals(28L, state.get().longValue());
            state.clear();
            Assert.assertNull(state.get());
            keyedBackend.setCurrentKey("g");
            state.add(3L);
            state.add(2L);
            state.add(1L);
            keyedBackend.setCurrentKey("def");
            Assert.assertNull(state.get());
            keyedBackend.setCurrentKey("g");
            Assert.assertEquals(9L, state.get().longValue());
            state.clear();
            // make sure all lists / maps are cleared
            Assert.assertThat("State backend is not empty.", keyedBackend.numKeyValueStateEntries(), Is.is(0));
        } finally {
            keyedBackend.close();
            keyedBackend.dispose();
        }
    }

    @Test
    public void testAggregatingStateMergingWithMutableAccumulator() throws Exception {
        final AggregatingStateDescriptor<Long, StateBackendTestBase.MutableLong, Long> stateDescr = new AggregatingStateDescriptor("my-state", new StateBackendTestBase.MutableAggregatingAddingFunction(), StateBackendTestBase.MutableLong.class);
        final Integer namespace1 = 1;
        final Integer namespace2 = 2;
        final Integer namespace3 = 3;
        final Long expectedResult = 165L;
        AbstractKeyedStateBackend<String> keyedBackend = createKeyedBackend(StringSerializer.INSTANCE);
        try {
            InternalAggregatingState<String, Integer, Long, Long, Long> state = ((InternalAggregatingState<String, Integer, Long, Long, Long>) (keyedBackend.getPartitionedState(0, INSTANCE, stateDescr)));
            // populate the different namespaces
            // - abc spreads the values over three namespaces
            // - def spreads the values over two namespaces (one empty)
            // - ghi is empty
            // - jkl has all elements already in the target namespace
            // - mno has all elements already in one source namespace
            keyedBackend.setCurrentKey("abc");
            state.setCurrentNamespace(namespace1);
            state.add(33L);
            state.add(55L);
            state.setCurrentNamespace(namespace2);
            state.add(22L);
            state.add(11L);
            state.setCurrentNamespace(namespace3);
            state.add(44L);
            keyedBackend.setCurrentKey("def");
            state.setCurrentNamespace(namespace1);
            state.add(11L);
            state.add(44L);
            state.setCurrentNamespace(namespace3);
            state.add(22L);
            state.add(55L);
            state.add(33L);
            keyedBackend.setCurrentKey("jkl");
            state.setCurrentNamespace(namespace1);
            state.add(11L);
            state.add(22L);
            state.add(33L);
            state.add(44L);
            state.add(55L);
            keyedBackend.setCurrentKey("mno");
            state.setCurrentNamespace(namespace3);
            state.add(11L);
            state.add(22L);
            state.add(33L);
            state.add(44L);
            state.add(55L);
            keyedBackend.setCurrentKey("abc");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertEquals(expectedResult, state.get());
            keyedBackend.setCurrentKey("def");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertEquals(expectedResult, state.get());
            keyedBackend.setCurrentKey("ghi");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertNull(state.get());
            keyedBackend.setCurrentKey("jkl");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertEquals(expectedResult, state.get());
            keyedBackend.setCurrentKey("mno");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertEquals(expectedResult, state.get());
            // make sure all lists / maps are cleared
            keyedBackend.setCurrentKey("abc");
            state.setCurrentNamespace(namespace1);
            state.clear();
            keyedBackend.setCurrentKey("def");
            state.setCurrentNamespace(namespace1);
            state.clear();
            keyedBackend.setCurrentKey("ghi");
            state.setCurrentNamespace(namespace1);
            state.clear();
            keyedBackend.setCurrentKey("jkl");
            state.setCurrentNamespace(namespace1);
            state.clear();
            keyedBackend.setCurrentKey("mno");
            state.setCurrentNamespace(namespace1);
            state.clear();
            Assert.assertThat("State backend is not empty.", keyedBackend.numKeyValueStateEntries(), Is.is(0));
        } finally {
            keyedBackend.close();
            keyedBackend.dispose();
        }
    }

    @Test
    public void testAggregatingStateAddAndGetWithImmutableAccumulator() throws Exception {
        final AggregatingStateDescriptor<Long, Long, Long> stateDescr = new AggregatingStateDescriptor("my-state", new StateBackendTestBase.ImmutableAggregatingAddingFunction(), Long.class);
        AbstractKeyedStateBackend<String> keyedBackend = createKeyedBackend(StringSerializer.INSTANCE);
        try {
            AggregatingState<Long, Long> state = keyedBackend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, stateDescr);
            keyedBackend.setCurrentKey("abc");
            Assert.assertNull(state.get());
            keyedBackend.setCurrentKey("def");
            Assert.assertNull(state.get());
            state.add(17L);
            state.add(11L);
            Assert.assertEquals(28L, state.get().longValue());
            keyedBackend.setCurrentKey("abc");
            Assert.assertNull(state.get());
            keyedBackend.setCurrentKey("g");
            Assert.assertNull(state.get());
            state.add(1L);
            state.add(2L);
            keyedBackend.setCurrentKey("def");
            Assert.assertEquals(28L, state.get().longValue());
            state.clear();
            Assert.assertNull(state.get());
            keyedBackend.setCurrentKey("g");
            state.add(3L);
            state.add(2L);
            state.add(1L);
            keyedBackend.setCurrentKey("def");
            Assert.assertNull(state.get());
            keyedBackend.setCurrentKey("g");
            Assert.assertEquals(9L, state.get().longValue());
            state.clear();
            // make sure all lists / maps are cleared
            Assert.assertThat("State backend is not empty.", keyedBackend.numKeyValueStateEntries(), Is.is(0));
        } finally {
            keyedBackend.close();
            keyedBackend.dispose();
        }
    }

    @Test
    public void testAggregatingStateMergingWithImmutableAccumulator() throws Exception {
        final AggregatingStateDescriptor<Long, Long, Long> stateDescr = new AggregatingStateDescriptor("my-state", new StateBackendTestBase.ImmutableAggregatingAddingFunction(), Long.class);
        final Integer namespace1 = 1;
        final Integer namespace2 = 2;
        final Integer namespace3 = 3;
        final Long expectedResult = 165L;
        AbstractKeyedStateBackend<String> keyedBackend = createKeyedBackend(StringSerializer.INSTANCE);
        try {
            InternalAggregatingState<String, Integer, Long, Long, Long> state = ((InternalAggregatingState<String, Integer, Long, Long, Long>) (keyedBackend.getPartitionedState(0, INSTANCE, stateDescr)));
            // populate the different namespaces
            // - abc spreads the values over three namespaces
            // - def spreads the values over two namespaces (one empty)
            // - ghi is empty
            // - jkl has all elements already in the target namespace
            // - mno has all elements already in one source namespace
            keyedBackend.setCurrentKey("abc");
            state.setCurrentNamespace(namespace1);
            state.add(33L);
            state.add(55L);
            state.setCurrentNamespace(namespace2);
            state.add(22L);
            state.add(11L);
            state.setCurrentNamespace(namespace3);
            state.add(44L);
            keyedBackend.setCurrentKey("def");
            state.setCurrentNamespace(namespace1);
            state.add(11L);
            state.add(44L);
            state.setCurrentNamespace(namespace3);
            state.add(22L);
            state.add(55L);
            state.add(33L);
            keyedBackend.setCurrentKey("jkl");
            state.setCurrentNamespace(namespace1);
            state.add(11L);
            state.add(22L);
            state.add(33L);
            state.add(44L);
            state.add(55L);
            keyedBackend.setCurrentKey("mno");
            state.setCurrentNamespace(namespace3);
            state.add(11L);
            state.add(22L);
            state.add(33L);
            state.add(44L);
            state.add(55L);
            keyedBackend.setCurrentKey("abc");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertEquals(expectedResult, state.get());
            keyedBackend.setCurrentKey("def");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertEquals(expectedResult, state.get());
            keyedBackend.setCurrentKey("ghi");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertNull(state.get());
            keyedBackend.setCurrentKey("jkl");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertEquals(expectedResult, state.get());
            keyedBackend.setCurrentKey("mno");
            state.mergeNamespaces(namespace1, Arrays.asList(namespace2, namespace3));
            state.setCurrentNamespace(namespace1);
            Assert.assertEquals(expectedResult, state.get());
            // make sure all lists / maps are cleared
            keyedBackend.setCurrentKey("abc");
            state.setCurrentNamespace(namespace1);
            state.clear();
            keyedBackend.setCurrentKey("def");
            state.setCurrentNamespace(namespace1);
            state.clear();
            keyedBackend.setCurrentKey("ghi");
            state.setCurrentNamespace(namespace1);
            state.clear();
            keyedBackend.setCurrentKey("jkl");
            state.setCurrentNamespace(namespace1);
            state.clear();
            keyedBackend.setCurrentKey("mno");
            state.setCurrentNamespace(namespace1);
            state.clear();
            Assert.assertThat("State backend is not empty.", keyedBackend.numKeyValueStateEntries(), Is.is(0));
        } finally {
            keyedBackend.close();
            keyedBackend.dispose();
        }
    }

    @Test
    @SuppressWarnings("unchecked,rawtypes")
    public void testFoldingState() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        FoldingStateDescriptor<Integer, String> kvId = new FoldingStateDescriptor("id", "Fold-Initial:", new StateBackendTestBase.AppendingFold(), String.class);
        TypeSerializer<Integer> keySerializer = IntSerializer.INSTANCE;
        TypeSerializer<VoidNamespace> namespaceSerializer = INSTANCE;
        FoldingState<Integer, String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        @SuppressWarnings("unchecked")
        InternalKvState<Integer, VoidNamespace, String> kvState = ((InternalKvState<Integer, VoidNamespace, String>) (state));
        // this is only available after the backend initialized the serializer
        TypeSerializer<String> valueSerializer = kvId.getSerializer();
        // some modifications to the state
        backend.setCurrentKey(1);
        Assert.assertNull(state.get());
        Assert.assertNull(StateBackendTestBase.getSerializedValue(kvState, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        state.add(1);
        backend.setCurrentKey(2);
        Assert.assertNull(state.get());
        Assert.assertNull(StateBackendTestBase.getSerializedValue(kvState, 2, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        state.add(2);
        backend.setCurrentKey(1);
        Assert.assertEquals("Fold-Initial:,1", state.get());
        Assert.assertEquals("Fold-Initial:,1", StateBackendTestBase.getSerializedValue(kvState, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        // draw a snapshot
        KeyedStateHandle snapshot1 = runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        // make some more modifications
        backend.setCurrentKey(1);
        state.clear();
        state.add(101);
        backend.setCurrentKey(2);
        state.add(102);
        backend.setCurrentKey(3);
        state.add(103);
        // draw another snapshot
        KeyedStateHandle snapshot2 = runSnapshot(backend.snapshot(682375462379L, 4, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        // validate the original state
        backend.setCurrentKey(1);
        Assert.assertEquals("Fold-Initial:,101", state.get());
        Assert.assertEquals("Fold-Initial:,101", StateBackendTestBase.getSerializedValue(kvState, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.setCurrentKey(2);
        Assert.assertEquals("Fold-Initial:,2,102", state.get());
        Assert.assertEquals("Fold-Initial:,2,102", StateBackendTestBase.getSerializedValue(kvState, 2, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.setCurrentKey(3);
        Assert.assertEquals("Fold-Initial:,103", state.get());
        Assert.assertEquals("Fold-Initial:,103", StateBackendTestBase.getSerializedValue(kvState, 3, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.dispose();
        // restore the first snapshot and validate it
        backend = restoreKeyedBackend(INSTANCE, snapshot1);
        snapshot1.discardState();
        FoldingState<Integer, String> restored1 = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        @SuppressWarnings("unchecked")
        InternalKvState<Integer, VoidNamespace, String> restoredKvState1 = ((InternalKvState<Integer, VoidNamespace, String>) (restored1));
        backend.setCurrentKey(1);
        Assert.assertEquals("Fold-Initial:,1", restored1.get());
        Assert.assertEquals("Fold-Initial:,1", StateBackendTestBase.getSerializedValue(restoredKvState1, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.setCurrentKey(2);
        Assert.assertEquals("Fold-Initial:,2", restored1.get());
        Assert.assertEquals("Fold-Initial:,2", StateBackendTestBase.getSerializedValue(restoredKvState1, 2, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.dispose();
        // restore the second snapshot and validate it
        backend = restoreKeyedBackend(INSTANCE, snapshot2);
        snapshot1.discardState();
        @SuppressWarnings("unchecked")
        FoldingState<Integer, String> restored2 = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        @SuppressWarnings("unchecked")
        InternalKvState<Integer, VoidNamespace, String> restoredKvState2 = ((InternalKvState<Integer, VoidNamespace, String>) (restored2));
        backend.setCurrentKey(1);
        Assert.assertEquals("Fold-Initial:,101", restored2.get());
        Assert.assertEquals("Fold-Initial:,101", StateBackendTestBase.getSerializedValue(restoredKvState2, 1, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.setCurrentKey(2);
        Assert.assertEquals("Fold-Initial:,2,102", restored2.get());
        Assert.assertEquals("Fold-Initial:,2,102", StateBackendTestBase.getSerializedValue(restoredKvState2, 2, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.setCurrentKey(3);
        Assert.assertEquals("Fold-Initial:,103", restored2.get());
        Assert.assertEquals("Fold-Initial:,103", StateBackendTestBase.getSerializedValue(restoredKvState2, 3, keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, valueSerializer));
        backend.dispose();
    }

    @Test
    @SuppressWarnings("unchecked,rawtypes")
    public void testMapState() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        AbstractKeyedStateBackend<String> backend = createKeyedBackend(StringSerializer.INSTANCE);
        MapStateDescriptor<Integer, String> kvId = new MapStateDescriptor("id", Integer.class, String.class);
        TypeSerializer<String> keySerializer = StringSerializer.INSTANCE;
        TypeSerializer<VoidNamespace> namespaceSerializer = INSTANCE;
        MapState<Integer, String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        @SuppressWarnings("unchecked")
        InternalKvState<String, VoidNamespace, Map<Integer, String>> kvState = ((InternalKvState<String, VoidNamespace, Map<Integer, String>>) (state));
        // these are only available after the backend initialized the serializer
        TypeSerializer<Integer> userKeySerializer = kvId.getKeySerializer();
        TypeSerializer<String> userValueSerializer = kvId.getValueSerializer();
        // some modifications to the state
        backend.setCurrentKey("1");
        Assert.assertNull(state.get(1));
        Assert.assertNull(StateBackendTestBase.getSerializedMap(kvState, "1", keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, userKeySerializer, userValueSerializer));
        state.put(1, "1");
        backend.setCurrentKey("2");
        Assert.assertNull(state.get(2));
        Assert.assertNull(StateBackendTestBase.getSerializedMap(kvState, "2", keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, userKeySerializer, userValueSerializer));
        state.put(2, "2");
        // put entry with different userKeyOffset
        backend.setCurrentKey("11");
        state.put(11, "11");
        backend.setCurrentKey("1");
        Assert.assertTrue(state.contains(1));
        Assert.assertEquals("1", state.get(1));
        Assert.assertEquals(new HashMap<Integer, String>() {
            {
                put(1, "1");
            }
        }, StateBackendTestBase.getSerializedMap(kvState, "1", keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, userKeySerializer, userValueSerializer));
        Assert.assertEquals(new HashMap<Integer, String>() {
            {
                put(11, "11");
            }
        }, StateBackendTestBase.getSerializedMap(kvState, "11", keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, userKeySerializer, userValueSerializer));
        // draw a snapshot
        KeyedStateHandle snapshot1 = runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        // make some more modifications
        backend.setCurrentKey("1");
        state.put(1, "101");
        backend.setCurrentKey("2");
        state.put(102, "102");
        backend.setCurrentKey("3");
        state.put(103, "103");
        state.putAll(new HashMap<Integer, String>() {
            {
                put(1031, "1031");
                put(1032, "1032");
            }
        });
        // draw another snapshot
        KeyedStateHandle snapshot2 = runSnapshot(backend.snapshot(682375462379L, 4, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        // validate the original state
        backend.setCurrentKey("1");
        Assert.assertEquals("101", state.get(1));
        Assert.assertEquals(new HashMap<Integer, String>() {
            {
                put(1, "101");
            }
        }, StateBackendTestBase.getSerializedMap(kvState, "1", keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, userKeySerializer, userValueSerializer));
        backend.setCurrentKey("2");
        Assert.assertEquals("102", state.get(102));
        Assert.assertEquals(new HashMap<Integer, String>() {
            {
                put(2, "2");
                put(102, "102");
            }
        }, StateBackendTestBase.getSerializedMap(kvState, "2", keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, userKeySerializer, userValueSerializer));
        backend.setCurrentKey("3");
        Assert.assertTrue(state.contains(103));
        Assert.assertEquals("103", state.get(103));
        Assert.assertEquals(new HashMap<Integer, String>() {
            {
                put(103, "103");
                put(1031, "1031");
                put(1032, "1032");
            }
        }, StateBackendTestBase.getSerializedMap(kvState, "3", keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, userKeySerializer, userValueSerializer));
        List<Integer> keys = new ArrayList<>();
        for (Integer key : state.keys()) {
            keys.add(key);
        }
        List<Integer> expectedKeys = Arrays.asList(103, 1031, 1032);
        Assert.assertEquals(keys.size(), expectedKeys.size());
        keys.removeAll(expectedKeys);
        Assert.assertTrue(keys.isEmpty());
        List<String> values = new ArrayList<>();
        for (String value : state.values()) {
            values.add(value);
        }
        List<String> expectedValues = Arrays.asList("103", "1031", "1032");
        Assert.assertEquals(values.size(), expectedValues.size());
        values.removeAll(expectedValues);
        Assert.assertTrue(values.isEmpty());
        // make some more modifications
        backend.setCurrentKey("1");
        state.clear();
        backend.setCurrentKey("2");
        state.remove(102);
        backend.setCurrentKey("3");
        final String updateSuffix = "_updated";
        Iterator<Map.Entry<Integer, String>> iterator = state.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> entry = iterator.next();
            if ((entry.getValue().length()) != 4) {
                iterator.remove();
            } else {
                entry.setValue(((entry.getValue()) + updateSuffix));
            }
        } 
        // validate the state
        backend.setCurrentKey("1");
        backend.setCurrentKey("2");
        Assert.assertFalse(state.contains(102));
        backend.setCurrentKey("3");
        for (Map.Entry<Integer, String> entry : state.entries()) {
            Assert.assertEquals((4 + (updateSuffix.length())), entry.getValue().length());
            Assert.assertTrue(entry.getValue().endsWith(updateSuffix));
        }
        backend.dispose();
        // restore the first snapshot and validate it
        backend = restoreKeyedBackend(StringSerializer.INSTANCE, snapshot1);
        snapshot1.discardState();
        MapState<Integer, String> restored1 = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        @SuppressWarnings("unchecked")
        InternalKvState<String, VoidNamespace, Map<Integer, String>> restoredKvState1 = ((InternalKvState<String, VoidNamespace, Map<Integer, String>>) (restored1));
        backend.setCurrentKey("1");
        Assert.assertEquals("1", restored1.get(1));
        Assert.assertEquals(new HashMap<Integer, String>() {
            {
                put(1, "1");
            }
        }, StateBackendTestBase.getSerializedMap(restoredKvState1, "1", keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, userKeySerializer, userValueSerializer));
        backend.setCurrentKey("2");
        Assert.assertEquals("2", restored1.get(2));
        Assert.assertEquals(new HashMap<Integer, String>() {
            {
                put(2, "2");
            }
        }, StateBackendTestBase.getSerializedMap(restoredKvState1, "2", keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, userKeySerializer, userValueSerializer));
        backend.dispose();
        // restore the second snapshot and validate it
        backend = restoreKeyedBackend(StringSerializer.INSTANCE, snapshot2);
        snapshot2.discardState();
        @SuppressWarnings("unchecked")
        MapState<Integer, String> restored2 = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        @SuppressWarnings("unchecked")
        InternalKvState<String, VoidNamespace, Map<Integer, String>> restoredKvState2 = ((InternalKvState<String, VoidNamespace, Map<Integer, String>>) (restored2));
        backend.setCurrentKey("1");
        Assert.assertEquals("101", restored2.get(1));
        Assert.assertEquals(new HashMap<Integer, String>() {
            {
                put(1, "101");
            }
        }, StateBackendTestBase.getSerializedMap(restoredKvState2, "1", keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, userKeySerializer, userValueSerializer));
        backend.setCurrentKey("2");
        Assert.assertEquals("102", restored2.get(102));
        Assert.assertEquals(new HashMap<Integer, String>() {
            {
                put(2, "2");
                put(102, "102");
            }
        }, StateBackendTestBase.getSerializedMap(restoredKvState2, "2", keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, userKeySerializer, userValueSerializer));
        backend.setCurrentKey("3");
        Assert.assertEquals("103", restored2.get(103));
        Assert.assertEquals(new HashMap<Integer, String>() {
            {
                put(103, "103");
                put(1031, "1031");
                put(1032, "1032");
            }
        }, StateBackendTestBase.getSerializedMap(restoredKvState2, "3", keySerializer, VoidNamespace.INSTANCE, namespaceSerializer, userKeySerializer, userValueSerializer));
        backend.dispose();
    }

    /**
     * Verify iterator of {@link MapState} supporting arbitrary access, see [FLINK-10267] to know more details.
     */
    @Test
    public void testMapStateIteratorArbitraryAccess() throws Exception {
        MapStateDescriptor<Integer, Long> kvId = new MapStateDescriptor("id", Integer.class, Long.class);
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        try {
            MapState<Integer, Long> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
            backend.setCurrentKey(1);
            int stateSize = 4096;
            for (int i = 0; i < stateSize; i++) {
                state.put(i, (i * 2L));
            }
            Iterator<Map.Entry<Integer, Long>> iterator = state.iterator();
            int iteratorCount = 0;
            while (iterator.hasNext()) {
                Map.Entry<Integer, Long> entry = iterator.next();
                Assert.assertEquals(iteratorCount, ((int) (entry.getKey())));
                switch ((ThreadLocalRandom.current().nextInt()) % 3) {
                    case 0 :
                        // remove twice
                        iterator.remove();
                        try {
                            iterator.remove();
                            Assert.fail();
                        } catch (IllegalStateException e) {
                            // ignore expected exception
                        }
                        break;
                    case 1 :
                        // hasNext -> remove
                        iterator.hasNext();
                        iterator.remove();
                        break;
                    case 2 :
                        // nothing to do
                        break;
                }
                iteratorCount++;
            } 
            Assert.assertEquals(stateSize, iteratorCount);
        } finally {
            backend.dispose();
        }
    }

    /**
     * Verify that {@link ValueStateDescriptor} allows {@code null} as default.
     */
    @Test
    public void testValueStateNullAsDefaultValue() throws Exception {
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        ValueStateDescriptor<String> kvId = new ValueStateDescriptor("id", String.class, null);
        ValueState<String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        backend.setCurrentKey(1);
        Assert.assertNull(state.value());
        state.update("Ciao");
        Assert.assertEquals("Ciao", state.value());
        state.clear();
        Assert.assertNull(state.value());
        backend.dispose();
    }

    /**
     * Verify that an empty {@code ValueState} will yield the default value.
     */
    @Test
    public void testValueStateDefaultValue() throws Exception {
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        ValueStateDescriptor<String> kvId = new ValueStateDescriptor("id", String.class, "Hello");
        ValueState<String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        backend.setCurrentKey(1);
        Assert.assertEquals("Hello", state.value());
        state.update("Ciao");
        Assert.assertEquals("Ciao", state.value());
        state.clear();
        Assert.assertEquals("Hello", state.value());
        backend.dispose();
    }

    /**
     * Verify that an empty {@code ReduceState} yields {@code null}.
     */
    @Test
    public void testReducingStateDefaultValue() throws Exception {
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        ReducingStateDescriptor<String> kvId = new ReducingStateDescriptor("id", new StateBackendTestBase.AppendingReduce(), String.class);
        ReducingState<String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        backend.setCurrentKey(1);
        Assert.assertNull(state.get());
        state.add("Ciao");
        Assert.assertEquals("Ciao", state.get());
        state.clear();
        Assert.assertNull(state.get());
        backend.dispose();
    }

    /**
     * Verify that an empty {@code FoldingState} yields {@code null}.
     */
    @Test
    public void testFoldingStateDefaultValue() throws Exception {
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        FoldingStateDescriptor<Integer, String> kvId = new FoldingStateDescriptor("id", "Fold-Initial:", new StateBackendTestBase.AppendingFold(), String.class);
        FoldingState<Integer, String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        backend.setCurrentKey(1);
        Assert.assertNull(state.get());
        state.add(1);
        state.add(2);
        Assert.assertEquals("Fold-Initial:,1,2", state.get());
        state.clear();
        Assert.assertNull(state.get());
        backend.dispose();
    }

    /**
     * Verify that an empty {@code ListState} yields {@code null}.
     */
    @Test
    public void testListStateDefaultValue() throws Exception {
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        ListStateDescriptor<String> kvId = new ListStateDescriptor("id", String.class);
        ListState<String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        backend.setCurrentKey(1);
        Assert.assertNull(state.get());
        state.update(Arrays.asList("Ciao", "Bello"));
        Assert.assertThat(state.get(), StateBackendTestBase.containsInAnyOrder("Ciao", "Bello"));
        state.clear();
        Assert.assertNull(state.get());
        backend.dispose();
    }

    /**
     * Verify that an empty {@code MapState} yields {@code null}.
     */
    @Test
    public void testMapStateDefaultValue() throws Exception {
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        MapStateDescriptor<String, String> kvId = new MapStateDescriptor("id", String.class, String.class);
        MapState<String, String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        backend.setCurrentKey(1);
        Assert.assertNull(state.entries());
        state.put("Ciao", "Hello");
        state.put("Bello", "Nice");
        Assert.assertNotNull(state.entries());
        Assert.assertEquals(state.get("Ciao"), "Hello");
        Assert.assertEquals(state.get("Bello"), "Nice");
        state.clear();
        Assert.assertNull(state.entries());
        backend.dispose();
    }

    @Test
    public void testSnapshotNonAccessedState() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        AbstractKeyedStateBackend<String> backend = createKeyedBackend(StringSerializer.INSTANCE);
        final String stateName = "test-name";
        try {
            MapStateDescriptor<Integer, String> kvId = new MapStateDescriptor(stateName, Integer.class, String.class);
            MapState<Integer, String> mapState = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
            // write some state to be snapshotted
            backend.setCurrentKey("1");
            mapState.put(11, "foo");
            backend.setCurrentKey("2");
            mapState.put(8, "bar");
            backend.setCurrentKey("3");
            mapState.put(91, "hello world");
            // take a snapshot, and then restore backend with snapshot
            KeyedStateHandle snapshot = runSnapshot(backend.snapshot(1L, 2L, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
            backend.dispose();
            backend = restoreKeyedBackend(StringSerializer.INSTANCE, snapshot);
            // now take a snapshot again without accessing the state
            snapshot = runSnapshot(backend.snapshot(2L, 3L, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
            backend.dispose();
            // we restore again and try to access previous state
            backend = restoreKeyedBackend(StringSerializer.INSTANCE, snapshot);
            mapState = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
            backend.setCurrentKey("1");
            Assert.assertEquals("foo", mapState.get(11));
            backend.setCurrentKey("2");
            Assert.assertEquals("bar", mapState.get(8));
            backend.setCurrentKey("3");
            Assert.assertEquals("hello world", mapState.get(91));
            snapshot.discardState();
        } finally {
            backend.dispose();
        }
    }

    /**
     * This test verifies that state is correctly assigned to key groups and that restore
     * restores the relevant key groups in the backend.
     *
     * <p>We have ten key groups. Initially, one backend is responsible for all ten key groups.
     * Then we snapshot, split up the state and restore in to backends where each is responsible
     * for five key groups. Then we make sure that the state is only available in the correct
     * backend.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testKeyGroupSnapshotRestore() throws Exception {
        final int MAX_PARALLELISM = 10;
        CheckpointStreamFactory streamFactory = createStreamFactory();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        final AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE, MAX_PARALLELISM, new KeyGroupRange(0, (MAX_PARALLELISM - 1)), new DummyEnvironment());
        ValueStateDescriptor<String> kvId = new ValueStateDescriptor("id", String.class);
        ValueState<String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        // keys that fall into the first half/second half of the key groups, respectively
        int keyInFirstHalf = 17;
        int keyInSecondHalf = 42;
        Random rand = new Random(0);
        // for each key, determine into which half of the key-group space they fall
        int firstKeyHalf = KeyGroupRangeAssignment.assignKeyToParallelOperator(keyInFirstHalf, MAX_PARALLELISM, 2);
        int secondKeyHalf = KeyGroupRangeAssignment.assignKeyToParallelOperator(keyInFirstHalf, MAX_PARALLELISM, 2);
        while (firstKeyHalf == secondKeyHalf) {
            keyInSecondHalf = rand.nextInt();
            secondKeyHalf = KeyGroupRangeAssignment.assignKeyToParallelOperator(keyInSecondHalf, MAX_PARALLELISM, 2);
        } 
        backend.setCurrentKey(keyInFirstHalf);
        state.update("ShouldBeInFirstHalf");
        backend.setCurrentKey(keyInSecondHalf);
        state.update("ShouldBeInSecondHalf");
        KeyedStateHandle snapshot = runSnapshot(backend.snapshot(0, 0, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        List<KeyedStateHandle> firstHalfKeyGroupStates = StateAssignmentOperation.getKeyedStateHandles(Collections.singletonList(snapshot), KeyGroupRangeAssignment.computeKeyGroupRangeForOperatorIndex(MAX_PARALLELISM, 2, 0));
        List<KeyedStateHandle> secondHalfKeyGroupStates = StateAssignmentOperation.getKeyedStateHandles(Collections.singletonList(snapshot), KeyGroupRangeAssignment.computeKeyGroupRangeForOperatorIndex(MAX_PARALLELISM, 2, 1));
        backend.dispose();
        // backend for the first half of the key group range
        final AbstractKeyedStateBackend<Integer> firstHalfBackend = restoreKeyedBackend(INSTANCE, MAX_PARALLELISM, new KeyGroupRange(0, 4), firstHalfKeyGroupStates, new DummyEnvironment());
        // backend for the second half of the key group range
        final AbstractKeyedStateBackend<Integer> secondHalfBackend = restoreKeyedBackend(INSTANCE, MAX_PARALLELISM, new KeyGroupRange(5, 9), secondHalfKeyGroupStates, new DummyEnvironment());
        ValueState<String> firstHalfState = firstHalfBackend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        firstHalfBackend.setCurrentKey(keyInFirstHalf);
        Assert.assertTrue(firstHalfState.value().equals("ShouldBeInFirstHalf"));
        firstHalfBackend.setCurrentKey(keyInSecondHalf);
        Assert.assertTrue(((firstHalfState.value()) == null));
        ValueState<String> secondHalfState = secondHalfBackend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        secondHalfBackend.setCurrentKey(keyInFirstHalf);
        Assert.assertTrue(((secondHalfState.value()) == null));
        secondHalfBackend.setCurrentKey(keyInSecondHalf);
        Assert.assertTrue(secondHalfState.value().equals("ShouldBeInSecondHalf"));
        firstHalfBackend.dispose();
        secondHalfBackend.dispose();
    }

    @Test
    public void testRestoreWithWrongKeySerializer() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        // use an IntSerializer at first
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        ValueStateDescriptor<String> kvId = new ValueStateDescriptor("id", String.class);
        ValueState<String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        // write some state
        backend.setCurrentKey(1);
        state.update("1");
        backend.setCurrentKey(2);
        state.update("2");
        // draw a snapshot
        KeyedStateHandle snapshot1 = runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        backend.dispose();
        // restore with the wrong key serializer
        try {
            restoreKeyedBackend(DoubleSerializer.INSTANCE, snapshot1);
            Assert.fail("should recognize wrong key serializer");
        } catch (StateMigrationException ignored) {
            // expected
        } catch (BackendBuildingException ignored) {
            Assert.assertTrue(((ignored.getCause()) instanceof StateMigrationException));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testValueStateRestoreWithWrongSerializers() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        try {
            ValueStateDescriptor<String> kvId = new ValueStateDescriptor("id", String.class);
            ValueState<String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
            backend.setCurrentKey(1);
            state.update("1");
            backend.setCurrentKey(2);
            state.update("2");
            // draw a snapshot
            KeyedStateHandle snapshot1 = runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
            backend.dispose();
            // restore the first snapshot and validate it
            backend = restoreKeyedBackend(INSTANCE, snapshot1);
            snapshot1.discardState();
            @SuppressWarnings("unchecked")
            TypeSerializer<String> fakeStringSerializer = ((TypeSerializer<String>) ((TypeSerializer<?>) (FloatSerializer.INSTANCE)));
            try {
                kvId = new ValueStateDescriptor("id", fakeStringSerializer);
                state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
                state.value();
                Assert.fail("should recognize wrong serializers");
            } catch (StateMigrationException ignored) {
                // expected
            }
        } finally {
            backend.dispose();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListStateRestoreWithWrongSerializers() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        try {
            ListStateDescriptor<String> kvId = new ListStateDescriptor("id", String.class);
            ListState<String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
            backend.setCurrentKey(1);
            state.add("1");
            backend.setCurrentKey(2);
            state.add("2");
            // draw a snapshot
            KeyedStateHandle snapshot1 = runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
            backend.dispose();
            // restore the first snapshot and validate it
            backend = restoreKeyedBackend(INSTANCE, snapshot1);
            snapshot1.discardState();
            @SuppressWarnings("unchecked")
            TypeSerializer<String> fakeStringSerializer = ((TypeSerializer<String>) ((TypeSerializer<?>) (FloatSerializer.INSTANCE)));
            try {
                kvId = new ListStateDescriptor("id", fakeStringSerializer);
                state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
                state.get();
                Assert.fail("should recognize wrong serializers");
            } catch (StateMigrationException ignored) {
                // expected
            }
        } finally {
            backend.dispose();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReducingStateRestoreWithWrongSerializers() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        try {
            ReducingStateDescriptor<String> kvId = new ReducingStateDescriptor("id", new StateBackendTestBase.AppendingReduce(), StringSerializer.INSTANCE);
            ReducingState<String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
            backend.setCurrentKey(1);
            state.add("1");
            backend.setCurrentKey(2);
            state.add("2");
            // draw a snapshot
            KeyedStateHandle snapshot1 = runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
            backend.dispose();
            // restore the first snapshot and validate it
            backend = restoreKeyedBackend(INSTANCE, snapshot1);
            snapshot1.discardState();
            @SuppressWarnings("unchecked")
            TypeSerializer<String> fakeStringSerializer = ((TypeSerializer<String>) ((TypeSerializer<?>) (FloatSerializer.INSTANCE)));
            try {
                kvId = new ReducingStateDescriptor("id", new StateBackendTestBase.AppendingReduce(), fakeStringSerializer);
                state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
                state.get();
                Assert.fail("should recognize wrong serializers");
            } catch (StateMigrationException ignored) {
                // expected
            }
        } finally {
            backend.dispose();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMapStateRestoreWithWrongSerializers() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        try {
            MapStateDescriptor<String, String> kvId = new MapStateDescriptor("id", StringSerializer.INSTANCE, StringSerializer.INSTANCE);
            MapState<String, String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
            backend.setCurrentKey(1);
            state.put("1", "First");
            backend.setCurrentKey(2);
            state.put("2", "Second");
            // draw a snapshot
            KeyedStateHandle snapshot1 = runSnapshot(backend.snapshot(682375462378L, 2, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
            backend.dispose();
            // restore the first snapshot and validate it
            backend = restoreKeyedBackend(INSTANCE, snapshot1);
            snapshot1.discardState();
            @SuppressWarnings("unchecked")
            TypeSerializer<String> fakeStringSerializer = ((TypeSerializer<String>) ((TypeSerializer<?>) (FloatSerializer.INSTANCE)));
            try {
                kvId = new MapStateDescriptor("id", fakeStringSerializer, StringSerializer.INSTANCE);
                state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
                state.entries();
                Assert.fail("should recognize wrong serializers");
            } catch (StateMigrationException ignored) {
                // expected
            }
            backend.dispose();
        } finally {
            backend.dispose();
        }
    }

    @Test
    public void testCopyDefaultValue() throws Exception {
        final AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        ValueStateDescriptor<IntValue> kvId = new ValueStateDescriptor("id", IntValue.class, new IntValue((-1)));
        ValueState<IntValue> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        backend.setCurrentKey(1);
        IntValue default1 = state.value();
        backend.setCurrentKey(2);
        IntValue default2 = state.value();
        Assert.assertNotNull(default1);
        Assert.assertNotNull(default2);
        Assert.assertEquals(default1, default2);
        Assert.assertFalse((default1 == default2));
        backend.dispose();
    }

    /**
     * Previously, it was possible to create partitioned state with
     * <code>null</code> namespace. This test makes sure that this is
     * prohibited now.
     */
    @Test
    public void testRequireNonNullNamespace() throws Exception {
        final AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        ValueStateDescriptor<IntValue> kvId = new ValueStateDescriptor("id", IntValue.class, new IntValue((-1)));
        try {
            backend.getPartitionedState(null, VoidNamespaceSerializer.INSTANCE, kvId);
            Assert.fail("Did not throw expected NullPointerException");
        } catch (NullPointerException ignored) {
        }
        try {
            backend.getPartitionedState(VoidNamespace.INSTANCE, null, kvId);
            Assert.fail("Did not throw expected NullPointerException");
        } catch (NullPointerException ignored) {
        }
        try {
            backend.getPartitionedState(null, null, kvId);
            Assert.fail("Did not throw expected NullPointerException");
        } catch (NullPointerException ignored) {
        }
        backend.dispose();
    }

    /**
     * Tests registration with the KvStateRegistry.
     */
    @Test
    public void testQueryableStateRegistration() throws Exception {
        DummyEnvironment env = new DummyEnvironment();
        KvStateRegistry registry = env.getKvStateRegistry();
        CheckpointStreamFactory streamFactory = createStreamFactory();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE, env);
        KeyGroupRange expectedKeyGroupRange = backend.getKeyGroupRange();
        KvStateRegistryListener listener = Mockito.mock(KvStateRegistryListener.class);
        registry.registerListener(DEFAULT_JOB_ID, listener);
        ValueStateDescriptor<Integer> desc = new ValueStateDescriptor("test", IntSerializer.INSTANCE);
        desc.setQueryable("banana");
        backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, desc);
        // Verify registered
        Mockito.verify(listener, Mockito.times(1)).notifyKvStateRegistered(ArgumentMatchers.eq(env.getJobID()), ArgumentMatchers.eq(env.getJobVertexId()), ArgumentMatchers.eq(expectedKeyGroupRange), ArgumentMatchers.eq("banana"), ArgumentMatchers.any(KvStateID.class));
        KeyedStateHandle snapshot = runSnapshot(backend.snapshot(682375462379L, 4, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
        backend.dispose();
        Mockito.verify(listener, Mockito.times(1)).notifyKvStateUnregistered(ArgumentMatchers.eq(env.getJobID()), ArgumentMatchers.eq(env.getJobVertexId()), ArgumentMatchers.eq(expectedKeyGroupRange), ArgumentMatchers.eq("banana"));
        backend.dispose();
        // Initialize again
        backend = restoreKeyedBackend(INSTANCE, snapshot, env);
        snapshot.discardState();
        backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, desc);
        // Verify registered again
        Mockito.verify(listener, Mockito.times(2)).notifyKvStateRegistered(ArgumentMatchers.eq(env.getJobID()), ArgumentMatchers.eq(env.getJobVertexId()), ArgumentMatchers.eq(expectedKeyGroupRange), ArgumentMatchers.eq("banana"), ArgumentMatchers.any(KvStateID.class));
        backend.dispose();
    }

    @Test
    public void testEmptyStateCheckpointing() {
        try {
            CheckpointStreamFactory streamFactory = createStreamFactory();
            SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
            AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
            ListStateDescriptor<String> kvId = new ListStateDescriptor("id", String.class);
            // draw a snapshot
            KeyedStateHandle snapshot = runSnapshot(backend.snapshot(682375462379L, 1, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
            Assert.assertNull(snapshot);
            backend.dispose();
            backend = restoreKeyedBackend(INSTANCE, snapshot);
            backend.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNumStateEntries() throws Exception {
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        ValueStateDescriptor<String> kvId = new ValueStateDescriptor("id", String.class);
        Assert.assertEquals(0, backend.numKeyValueStateEntries());
        ValueState<String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
        backend.setCurrentKey(0);
        state.update("hello");
        state.update("ciao");
        Assert.assertEquals(1, backend.numKeyValueStateEntries());
        backend.setCurrentKey(42);
        state.update("foo");
        Assert.assertEquals(2, backend.numKeyValueStateEntries());
        backend.setCurrentKey(0);
        state.clear();
        Assert.assertEquals(1, backend.numKeyValueStateEntries());
        backend.setCurrentKey(42);
        state.clear();
        Assert.assertEquals(0, backend.numKeyValueStateEntries());
        backend.dispose();
    }

    private static class AppendingReduce implements ReduceFunction<String> {
        @Override
        public String reduce(String value1, String value2) throws Exception {
            return (value1 + ",") + value2;
        }
    }

    /**
     * The purpose of this test is to check that parallel snapshots are possible, and work even if a previous snapshot
     * is still running and blocking.
     */
    @Test
    public void testParallelAsyncSnapshots() throws Exception {
        OneShotLatch blocker = new OneShotLatch();
        OneShotLatch waiter = new OneShotLatch();
        BlockerCheckpointStreamFactory streamFactory = new BlockerCheckpointStreamFactory((1024 * 1024));
        streamFactory.setWaiterLatch(waiter);
        streamFactory.setBlockerLatch(blocker);
        streamFactory.setAfterNumberInvocations(10);
        final AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        try {
            if (!(backend.supportsAsynchronousSnapshots())) {
                return;
            }
            // insert some data to the backend.
            InternalValueState<Integer, VoidNamespace, Integer> valueState = backend.createInternalState(VoidNamespaceSerializer.INSTANCE, new ValueStateDescriptor("test", IntSerializer.INSTANCE));
            valueState.setCurrentNamespace(VoidNamespace.INSTANCE);
            for (int i = 0; i < 10; ++i) {
                backend.setCurrentKey(i);
                valueState.update(i);
            }
            RunnableFuture<SnapshotResult<KeyedStateHandle>> snapshot1 = backend.snapshot(0L, 0L, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation());
            Thread runner1 = new Thread(snapshot1, "snapshot-1-runner");
            runner1.start();
            // after this call returns, we have a running snapshot-1 that is blocked in IO.
            waiter.await();
            // do some updates in between the snapshots.
            for (int i = 5; i < 15; ++i) {
                backend.setCurrentKey(i);
                valueState.update((i + 1));
            }
            // we don't want to block the second snapshot.
            streamFactory.setWaiterLatch(null);
            streamFactory.setBlockerLatch(null);
            RunnableFuture<SnapshotResult<KeyedStateHandle>> snapshot2 = backend.snapshot(1L, 1L, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation());
            Thread runner2 = new Thread(snapshot2, "snapshot-2-runner");
            runner2.start();
            // snapshot-2 should run and succeed, while snapshot-1 is still running and blocked in IO.
            snapshot2.get();
            // we release the blocking IO so that snapshot-1 can also finish and succeed.
            blocker.trigger();
            snapshot1.get();
        } finally {
            backend.dispose();
        }
    }

    @Test
    public void testNonConcurrentSnapshotTransformerAccess() throws Exception {
        BlockerCheckpointStreamFactory streamFactory = new BlockerCheckpointStreamFactory((1024 * 1024));
        AbstractKeyedStateBackend<Integer> backend = null;
        try {
            backend = createKeyedBackend(INSTANCE);
            new StateSnapshotTransformerTest(backend, streamFactory).testNonConcurrentSnapshotTransformerAccess();
        } finally {
            if (backend != null) {
                IOUtils.closeQuietly(backend);
                backend.dispose();
            }
        }
    }

    @Test
    public void testAsyncSnapshot() throws Exception {
        OneShotLatch waiter = new OneShotLatch();
        BlockerCheckpointStreamFactory streamFactory = new BlockerCheckpointStreamFactory((1024 * 1024));
        streamFactory.setWaiterLatch(waiter);
        AbstractKeyedStateBackend<Integer> backend = null;
        KeyedStateHandle stateHandle = null;
        try {
            backend = createKeyedBackend(INSTANCE);
            InternalValueState<Integer, VoidNamespace, Integer> valueState = backend.createInternalState(VoidNamespaceSerializer.INSTANCE, new ValueStateDescriptor("test", IntSerializer.INSTANCE));
            valueState.setCurrentNamespace(VoidNamespace.INSTANCE);
            for (int i = 0; i < 10; ++i) {
                backend.setCurrentKey(i);
                valueState.update(i);
            }
            RunnableFuture<SnapshotResult<KeyedStateHandle>> snapshot = backend.snapshot(0L, 0L, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation());
            Thread runner = new Thread(snapshot);
            runner.start();
            for (int i = 0; i < 20; ++i) {
                backend.setCurrentKey(i);
                valueState.update((i + 1));
                if (10 == i) {
                    waiter.await();
                }
            }
            runner.join();
            SnapshotResult<KeyedStateHandle> snapshotResult = snapshot.get();
            stateHandle = snapshotResult.getJobManagerOwnedSnapshot();
            // test isolation
            for (int i = 0; i < 20; ++i) {
                backend.setCurrentKey(i);
                Assert.assertEquals((i + 1), ((int) (valueState.value())));
            }
        } finally {
            if (null != backend) {
                IOUtils.closeQuietly(backend);
                backend.dispose();
            }
        }
        Assert.assertNotNull(stateHandle);
        backend = null;
        try {
            backend = restoreKeyedBackend(INSTANCE, stateHandle);
            InternalValueState<Integer, VoidNamespace, Integer> valueState = backend.createInternalState(VoidNamespaceSerializer.INSTANCE, new ValueStateDescriptor("test", IntSerializer.INSTANCE));
            valueState.setCurrentNamespace(VoidNamespace.INSTANCE);
            for (int i = 0; i < 10; ++i) {
                backend.setCurrentKey(i);
                Assert.assertEquals(i, ((int) (valueState.value())));
            }
            backend.setCurrentKey(11);
            Assert.assertNull(valueState.value());
        } finally {
            if (null != backend) {
                IOUtils.closeQuietly(backend);
                backend.dispose();
            }
        }
    }

    /**
     * Since {@link AbstractKeyedStateBackend#getKeys(String, Object)} does't support concurrent modification
     * and {@link AbstractKeyedStateBackend#applyToAllKeys(Object, TypeSerializer, StateDescriptor,
     * KeyedStateFunction)} rely on it to get keys from backend. So we need this unit test to verify the concurrent
     * modification with {@link AbstractKeyedStateBackend#applyToAllKeys(Object, TypeSerializer, StateDescriptor, KeyedStateFunction)}.
     */
    @Test
    public void testConcurrentModificationWithApplyToAllKeys() throws Exception {
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        try {
            ListStateDescriptor<String> listStateDescriptor = new ListStateDescriptor("foo", StringSerializer.INSTANCE);
            ListState<String> listState = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, listStateDescriptor);
            for (int i = 0; i < 100; ++i) {
                backend.setCurrentKey(i);
                listState.add(("Hello" + i));
            }
            // valid state value via applyToAllKeys().
            backend.applyToAllKeys(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, listStateDescriptor, new KeyedStateFunction<Integer, ListState<String>>() {
                @Override
                public void process(Integer key, ListState<String> state) throws Exception {
                    Assert.assertEquals(("Hello" + key), state.get().iterator().next());
                }
            });
            // clear state via applyToAllKeys().
            backend.applyToAllKeys(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, listStateDescriptor, new KeyedStateFunction<Integer, ListState<String>>() {
                @Override
                public void process(Integer key, ListState<String> state) throws Exception {
                    state.clear();
                }
            });
            // valid that state has been cleared.
            backend.applyToAllKeys(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, listStateDescriptor, new KeyedStateFunction<Integer, ListState<String>>() {
                @Override
                public void process(Integer key, ListState<String> state) throws Exception {
                    Assert.assertFalse(state.get().iterator().hasNext());
                }
            });
            // clear() with add() in applyToAllKeys()
            backend.applyToAllKeys(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, listStateDescriptor, new KeyedStateFunction<Integer, ListState<String>>() {
                @Override
                public void process(Integer key, ListState<String> state) throws Exception {
                    state.add(("Hello" + key));
                    state.clear();
                    state.add(("Hello_" + key));
                }
            });
            // valid state value via applyToAllKeys().
            backend.applyToAllKeys(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, listStateDescriptor, new KeyedStateFunction<Integer, ListState<String>>() {
                @Override
                public void process(Integer key, ListState<String> state) throws Exception {
                    final Iterator<String> it = state.get().iterator();
                    Assert.assertEquals(("Hello_" + key), it.next());
                    Assert.assertFalse(it.hasNext());// finally verify we have no more elements

                }
            });
        } finally {
            IOUtils.closeQuietly(backend);
            backend.dispose();
        }
    }

    @Test
    public void testApplyToAllKeysLambdaFunction() throws Exception {
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        try {
            ListStateDescriptor<String> listStateDescriptor = new ListStateDescriptor("foo", StringSerializer.INSTANCE);
            ListState<String> listState = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, listStateDescriptor);
            for (int i = 0; i < 100; ++i) {
                backend.setCurrentKey(i);
                listState.add(("Hello" + i));
            }
            // valid state value via applyToAllKeys().
            backend.applyToAllKeys(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, listStateDescriptor, (Integer key,ListState<String> state) -> assertEquals(("Hello" + key), state.get().iterator().next()));
        } finally {
            IOUtils.closeQuietly(backend);
            backend.dispose();
        }
    }

    @Test
    public void testAsyncSnapshotCancellation() throws Exception {
        OneShotLatch blocker = new OneShotLatch();
        OneShotLatch waiter = new OneShotLatch();
        BlockerCheckpointStreamFactory streamFactory = new BlockerCheckpointStreamFactory((1024 * 1024));
        streamFactory.setWaiterLatch(waiter);
        streamFactory.setBlockerLatch(blocker);
        streamFactory.setAfterNumberInvocations(10);
        final AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        try {
            if (!(backend.supportsAsynchronousSnapshots())) {
                return;
            }
            InternalValueState<Integer, VoidNamespace, Integer> valueState = backend.createInternalState(VoidNamespaceSerializer.INSTANCE, new ValueStateDescriptor("test", IntSerializer.INSTANCE));
            valueState.setCurrentNamespace(VoidNamespace.INSTANCE);
            for (int i = 0; i < 10; ++i) {
                backend.setCurrentKey(i);
                valueState.update(i);
            }
            RunnableFuture<SnapshotResult<KeyedStateHandle>> snapshot = backend.snapshot(0L, 0L, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation());
            Thread runner = new Thread(snapshot);
            runner.start();
            // wait until the code reached some stream read
            waiter.await();
            // close the backend to see if the close is propagated to the stream
            IOUtils.closeQuietly(backend);
            // unblock the stream so that it can run into the IOException
            blocker.trigger();
            runner.join();
            try {
                snapshot.get();
                Assert.fail("Close was not propagated.");
            } catch (CancellationException ex) {
                // ignore
            }
        } finally {
            backend.dispose();
        }
    }

    private static class AppendingFold implements FoldFunction<Integer, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public String fold(String acc, Integer value) throws Exception {
            return (acc + ",") + value;
        }
    }

    @Test
    public void testMapStateGetKeys() throws Exception {
        final int namespace1ElementsNum = 1000;
        final int namespace2ElementsNum = 1000;
        String fieldName = "get-keys-test";
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        try {
            final String ns1 = "ns1";
            MapState<String, Integer> keyedState1 = backend.getPartitionedState(ns1, StringSerializer.INSTANCE, new MapStateDescriptor(fieldName, StringSerializer.INSTANCE, IntSerializer.INSTANCE));
            for (int key = 0; key < namespace1ElementsNum; key++) {
                backend.setCurrentKey(key);
                keyedState1.put("he", (key * 2));
                keyedState1.put("ho", (key * 2));
            }
            final String ns2 = "ns2";
            MapState<String, Integer> keyedState2 = backend.getPartitionedState(ns2, StringSerializer.INSTANCE, new MapStateDescriptor(fieldName, StringSerializer.INSTANCE, IntSerializer.INSTANCE));
            for (int key = namespace1ElementsNum; key < (namespace1ElementsNum + namespace2ElementsNum); key++) {
                backend.setCurrentKey(key);
                keyedState2.put("he", (key * 2));
                keyedState2.put("ho", (key * 2));
            }
            // valid for namespace1
            try (Stream<Integer> keysStream = backend.getKeys(fieldName, ns1).sorted()) {
                PrimitiveIterator.OfInt actualIterator = keysStream.mapToInt(( value) -> value.intValue()).iterator();
                for (int expectedKey = 0; expectedKey < namespace1ElementsNum; expectedKey++) {
                    Assert.assertTrue(actualIterator.hasNext());
                    Assert.assertEquals(expectedKey, actualIterator.nextInt());
                }
                Assert.assertFalse(actualIterator.hasNext());
            }
            // valid for namespace2
            try (Stream<Integer> keysStream = backend.getKeys(fieldName, ns2).sorted()) {
                PrimitiveIterator.OfInt actualIterator = keysStream.mapToInt(( value) -> value.intValue()).iterator();
                for (int expectedKey = namespace1ElementsNum; expectedKey < (namespace1ElementsNum + namespace2ElementsNum); expectedKey++) {
                    Assert.assertTrue(actualIterator.hasNext());
                    Assert.assertEquals(expectedKey, actualIterator.nextInt());
                }
                Assert.assertFalse(actualIterator.hasNext());
            }
        } finally {
            IOUtils.closeQuietly(backend);
            backend.dispose();
        }
    }

    @Test
    public void testCheckConcurrencyProblemWhenPerformingCheckpointAsync() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        Environment env = new DummyEnvironment();
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE, env);
        ExecutorService executorService = Executors.newScheduledThreadPool(1);
        try {
            long checkpointID = 0;
            List<Future> futureList = new ArrayList();
            for (int i = 0; i < 10; ++i) {
                ValueStateDescriptor<Integer> kvId = new ValueStateDescriptor(("id" + i), IntSerializer.INSTANCE);
                ValueState<Integer> state = backend.getOrCreateKeyedState(VoidNamespaceSerializer.INSTANCE, kvId);
                ((InternalValueState) (state)).setCurrentNamespace(VoidNamespace.INSTANCE);
                backend.setCurrentKey(i);
                state.update(i);
                futureList.add(runSnapshotAsync(executorService, backend.snapshot((checkpointID++), System.currentTimeMillis(), streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation())));
            }
            for (Future future : futureList) {
                future.get(20, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            Assert.fail();
        } finally {
            backend.dispose();
            executorService.shutdown();
        }
    }

    public static class TestPojo implements Serializable {
        private String strField;

        private Integer intField;

        private StateBackendTestBase.TestNestedPojoClassA kryoClassAField;

        private StateBackendTestBase.TestNestedPojoClassB kryoClassBField;

        public TestPojo() {
        }

        public TestPojo(String strField, Integer intField) {
            this.strField = strField;
            this.intField = intField;
            this.kryoClassAField = null;
            this.kryoClassBField = null;
        }

        public TestPojo(String strField, Integer intField, StateBackendTestBase.TestNestedPojoClassA classAField, StateBackendTestBase.TestNestedPojoClassB classBfield) {
            this.strField = strField;
            this.intField = intField;
            this.kryoClassAField = classAField;
            this.kryoClassBField = classBfield;
        }

        public String getStrField() {
            return strField;
        }

        public void setStrField(String strField) {
            this.strField = strField;
        }

        public Integer getIntField() {
            return intField;
        }

        public void setIntField(Integer intField) {
            this.intField = intField;
        }

        public StateBackendTestBase.TestNestedPojoClassA getKryoClassAField() {
            return kryoClassAField;
        }

        public void setKryoClassAField(StateBackendTestBase.TestNestedPojoClassA kryoClassAField) {
            this.kryoClassAField = kryoClassAField;
        }

        public StateBackendTestBase.TestNestedPojoClassB getKryoClassBField() {
            return kryoClassBField;
        }

        public void setKryoClassBField(StateBackendTestBase.TestNestedPojoClassB kryoClassBField) {
            this.kryoClassBField = kryoClassBField;
        }

        @Override
        public String toString() {
            return ((((("TestPojo{" + "strField='") + (strField)) + '\'') + ", intField=") + (intField)) + '}';
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            StateBackendTestBase.TestPojo testPojo = ((StateBackendTestBase.TestPojo) (o));
            return (((strField.equals(testPojo.strField)) && (intField.equals(testPojo.intField))) && ((((kryoClassAField) == null) && ((testPojo.kryoClassAField) == null)) || (kryoClassAField.equals(testPojo.kryoClassAField)))) && ((((kryoClassBField) == null) && ((testPojo.kryoClassBField) == null)) || (kryoClassBField.equals(testPojo.kryoClassBField)));
        }

        @Override
        public int hashCode() {
            int result = strField.hashCode();
            result = (31 * result) + (intField.hashCode());
            if ((kryoClassAField) != null) {
                result = (31 * result) + (kryoClassAField.hashCode());
            }
            if ((kryoClassBField) != null) {
                result = (31 * result) + (kryoClassBField.hashCode());
            }
            return result;
        }
    }

    public static class TestNestedPojoClassA implements Serializable {
        private Double doubleField;

        private Integer intField;

        public TestNestedPojoClassA() {
        }

        public TestNestedPojoClassA(Double doubleField, Integer intField) {
            this.doubleField = doubleField;
            this.intField = intField;
        }

        public Double getDoubleField() {
            return doubleField;
        }

        public void setDoubleField(Double doubleField) {
            this.doubleField = doubleField;
        }

        public Integer getIntField() {
            return intField;
        }

        public void setIntField(Integer intField) {
            this.intField = intField;
        }

        @Override
        public String toString() {
            return ((((("TestNestedPojoClassA{" + "doubleField='") + (doubleField)) + '\'') + ", intField=") + (intField)) + '}';
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            StateBackendTestBase.TestNestedPojoClassA testNestedPojoClassA = ((StateBackendTestBase.TestNestedPojoClassA) (o));
            if (!(doubleField.equals(testNestedPojoClassA.doubleField)))
                return false;

            return intField.equals(testNestedPojoClassA.intField);
        }

        @Override
        public int hashCode() {
            int result = doubleField.hashCode();
            result = (31 * result) + (intField.hashCode());
            return result;
        }
    }

    public static class TestNestedPojoClassB implements Serializable {
        private Double doubleField;

        private String strField;

        public TestNestedPojoClassB() {
        }

        public TestNestedPojoClassB(Double doubleField, String strField) {
            this.doubleField = doubleField;
            this.strField = strField;
        }

        public Double getDoubleField() {
            return doubleField;
        }

        public void setDoubleField(Double doubleField) {
            this.doubleField = doubleField;
        }

        public String getStrField() {
            return strField;
        }

        public void setStrField(String strField) {
            this.strField = strField;
        }

        @Override
        public String toString() {
            return ((((("TestNestedPojoClassB{" + "doubleField='") + (doubleField)) + '\'') + ", strField=") + (strField)) + '}';
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            StateBackendTestBase.TestNestedPojoClassB testNestedPojoClassB = ((StateBackendTestBase.TestNestedPojoClassB) (o));
            if (!(doubleField.equals(testNestedPojoClassB.doubleField)))
                return false;

            return strField.equals(testNestedPojoClassB.strField);
        }

        @Override
        public int hashCode() {
            int result = doubleField.hashCode();
            result = (31 * result) + (strField.hashCode());
            return result;
        }
    }

    /**
     * We throw this in our {@link ExceptionThrowingTestSerializer}.
     */
    private static class ExpectedKryoTestException extends RuntimeException {}

    /**
     * Kryo {@code Serializer} that throws an expected exception. We use this to ensure
     * that the state backend correctly uses a specified Kryo serializer.
     */
    public static class ExceptionThrowingTestSerializer extends JavaSerializer {
        @Override
        public void write(Kryo kryo, Output output, Object object) {
            throw new StateBackendTestBase.ExpectedKryoTestException();
        }

        @Override
        public Object read(Kryo kryo, Input input, Class type) {
            throw new StateBackendTestBase.ExpectedKryoTestException();
        }
    }

    /**
     * Our custom version of {@link JavaSerializer} for checking whether restore with a registered
     * serializer works when no serializer was previously registered.
     *
     * <p>This {@code Serializer} can only be used for writing, not for reading. With this we
     * verify that state that was serialized without a registered {@code Serializer} is in fact
     * not restored with a {@code Serializer} that was later registered.
     */
    public static class CustomKryoTestSerializer extends JavaSerializer {
        @Override
        public void write(Kryo kryo, Output output, Object object) {
            super.write(kryo, output, object);
        }

        @Override
        public Object read(Kryo kryo, Input input, Class type) {
            throw new StateBackendTestBase.ExpectedKryoTestException();
        }
    }

    @SuppressWarnings("serial")
    private static class MutableAggregatingAddingFunction implements AggregateFunction<Long, StateBackendTestBase.MutableLong, Long> {
        @Override
        public StateBackendTestBase.MutableLong createAccumulator() {
            return new StateBackendTestBase.MutableLong();
        }

        @Override
        public StateBackendTestBase.MutableLong add(Long value, StateBackendTestBase.MutableLong accumulator) {
            accumulator.value += value;
            return accumulator;
        }

        @Override
        public Long getResult(StateBackendTestBase.MutableLong accumulator) {
            return accumulator.value;
        }

        @Override
        public StateBackendTestBase.MutableLong merge(StateBackendTestBase.MutableLong a, StateBackendTestBase.MutableLong b) {
            a.value += b.value;
            return a;
        }
    }

    @SuppressWarnings("serial")
    private static class ImmutableAggregatingAddingFunction implements AggregateFunction<Long, Long, Long> {
        @Override
        public Long createAccumulator() {
            return 0L;
        }

        @Override
        public Long add(Long value, Long accumulator) {
            return accumulator += value;
        }

        @Override
        public Long getResult(Long accumulator) {
            return accumulator;
        }

        @Override
        public Long merge(Long a, Long b) {
            return a + b;
        }
    }

    private static final class MutableLong {
        long value;
    }
}

