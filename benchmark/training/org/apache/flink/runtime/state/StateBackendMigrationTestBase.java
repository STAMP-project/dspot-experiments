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
import java.io.IOException;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.TypeSerializerSchemaCompatibility;
import org.apache.flink.api.common.typeutils.TypeSerializerSnapshot;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputView;
import org.apache.flink.runtime.checkpoint.CheckpointOptions;
import org.apache.flink.runtime.testutils.statemigration.TestType;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.StateMigrationException;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the {@link KeyedStateBackend} and {@link OperatorStateBackend} as produced
 * by various {@link StateBackend}s.
 *
 * <p>The tests in this test base focuses on the verification of state serializers usage when they are
 * either compatible or requiring state migration after restoring the state backends.
 */
@SuppressWarnings("serial")
public abstract class StateBackendMigrationTestBase<B extends AbstractStateBackend> extends TestLogger {
    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    // lazily initialized stream storage
    private CheckpointStorageLocation checkpointStorageLocation;

    // -------------------------------------------------------------------------------
    // Tests for keyed ValueState
    // -------------------------------------------------------------------------------
    @Test
    public void testKeyedValueStateMigration() throws Exception {
        final String stateName = "test-name";
        testKeyedValueStateUpgrade(new org.apache.flink.api.common.state.ValueStateDescriptor(stateName, new TestType.V1TestTypeSerializer()), // restore with a V2 serializer that has a different schema
        new org.apache.flink.api.common.state.ValueStateDescriptor(stateName, new TestType.V2TestTypeSerializer()));
    }

    @Test
    public void testKeyedValueStateSerializerReconfiguration() throws Exception {
        final String stateName = "test-name";
        testKeyedValueStateUpgrade(new org.apache.flink.api.common.state.ValueStateDescriptor(stateName, new TestType.V1TestTypeSerializer()), // the test fails if this serializer is used instead of a reconfigured new serializer
        new org.apache.flink.api.common.state.ValueStateDescriptor(stateName, new TestType.ReconfigurationRequiringTestTypeSerializer()));
    }

    @Test
    public void testKeyedValueStateRegistrationFailsIfNewStateSerializerIsIncompatible() throws Exception {
        final String stateName = "test-name";
        try {
            testKeyedValueStateUpgrade(new org.apache.flink.api.common.state.ValueStateDescriptor(stateName, new TestType.V1TestTypeSerializer()), new org.apache.flink.api.common.state.ValueStateDescriptor(stateName, new TestType.IncompatibleTestTypeSerializer()));
            Assert.fail("should have failed");
        } catch (Exception expected) {
            Assert.assertTrue(ExceptionUtils.findThrowable(expected, StateMigrationException.class).isPresent());
        }
    }

    // -------------------------------------------------------------------------------
    // Tests for keyed ListState
    // -------------------------------------------------------------------------------
    @Test
    public void testKeyedListStateMigration() throws Exception {
        final String stateName = "test-name";
        testKeyedListStateUpgrade(new org.apache.flink.api.common.state.ListStateDescriptor(stateName, new TestType.V1TestTypeSerializer()), // restore with a V2 serializer that has a different schema
        new org.apache.flink.api.common.state.ListStateDescriptor(stateName, new TestType.V2TestTypeSerializer()));
    }

    @Test
    public void testKeyedListStateSerializerReconfiguration() throws Exception {
        final String stateName = "test-name";
        testKeyedListStateUpgrade(new org.apache.flink.api.common.state.ListStateDescriptor(stateName, new TestType.V1TestTypeSerializer()), // the test fails if this serializer is used instead of a reconfigured new serializer
        new org.apache.flink.api.common.state.ListStateDescriptor(stateName, new TestType.ReconfigurationRequiringTestTypeSerializer()));
    }

    @Test
    public void testKeyedListStateRegistrationFailsIfNewStateSerializerIsIncompatible() throws Exception {
        final String stateName = "test-name";
        try {
            testKeyedListStateUpgrade(new org.apache.flink.api.common.state.ListStateDescriptor(stateName, new TestType.V1TestTypeSerializer()), new org.apache.flink.api.common.state.ListStateDescriptor(stateName, new TestType.IncompatibleTestTypeSerializer()));
            Assert.fail("should have failed");
        } catch (Exception expected) {
            Assert.assertTrue(ExceptionUtils.findThrowable(expected, StateMigrationException.class).isPresent());
        }
    }

    // -------------------------------------------------------------------------------
    // Tests for keyed priority queue state
    // -------------------------------------------------------------------------------
    @Test
    public void testPriorityQueueStateCreationFailsIfNewSerializerIsNotCompatible() throws Exception {
        CheckpointStreamFactory streamFactory = createStreamFactory();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(INSTANCE);
        try {
            InternalPriorityQueue<TestType> internalPriorityQueue = backend.create("testPriorityQueue", new TestType.V1TestTypeSerializer());
            internalPriorityQueue.add(new TestType("key-1", 123));
            internalPriorityQueue.add(new TestType("key-2", 346));
            internalPriorityQueue.add(new TestType("key-1", 777));
            KeyedStateHandle snapshot = runSnapshot(backend.snapshot(1L, 2L, streamFactory, CheckpointOptions.forCheckpointWithDefaultLocation()), sharedStateRegistry);
            backend.dispose();
            backend = restoreKeyedBackend(INSTANCE, snapshot);
            backend.create("testPriorityQueue", new TestType.IncompatibleTestTypeSerializer());
            Assert.fail("should have failed");
        } catch (Exception e) {
            Assert.assertTrue(ExceptionUtils.findThrowable(e, StateMigrationException.class).isPresent());
        } finally {
            backend.dispose();
        }
    }

    // -------------------------------------------------------------------------------
    // Tests for key serializer in keyed state backends
    // -------------------------------------------------------------------------------
    @Test
    public void testStateBackendRestoreFailsIfNewKeySerializerRequiresMigration() throws Exception {
        try {
            testKeySerializerUpgrade(new TestType.V1TestTypeSerializer(), new TestType.V2TestTypeSerializer());
            Assert.fail("should have failed");
        } catch (Exception expected) {
            // the new key serializer requires migration; this should fail the restore
            Assert.assertTrue(ExceptionUtils.findThrowable(expected, StateMigrationException.class).isPresent());
        }
    }

    @Test
    public void testStateBackendRestoreSucceedsIfNewKeySerializerRequiresReconfiguration() throws Exception {
        testKeySerializerUpgrade(new TestType.V1TestTypeSerializer(), new TestType.ReconfigurationRequiringTestTypeSerializer());
    }

    @Test
    public void testStateBackendRestoreFailsIfNewKeySerializerIsIncompatible() throws Exception {
        try {
            testKeySerializerUpgrade(new TestType.V1TestTypeSerializer(), new TestType.IncompatibleTestTypeSerializer());
            Assert.fail("should have failed");
        } catch (Exception expected) {
            // the new key serializer is incompatible; this should fail the restore
            Assert.assertTrue(ExceptionUtils.findThrowable(expected, StateMigrationException.class).isPresent());
        }
    }

    // -------------------------------------------------------------------------------
    // Tests for namespace serializer in keyed state backends
    // -------------------------------------------------------------------------------
    @Test
    public void testKeyedStateRegistrationFailsIfNewNamespaceSerializerRequiresMigration() throws Exception {
        try {
            testNamespaceSerializerUpgrade(new TestType.V1TestTypeSerializer(), new TestType.V2TestTypeSerializer());
            Assert.fail("should have failed");
        } catch (Exception expected) {
            // the new namespace serializer requires migration; this should fail the restore
            Assert.assertTrue(ExceptionUtils.findThrowable(expected, StateMigrationException.class).isPresent());
        }
    }

    @Test
    public void testKeyedStateRegistrationSucceedsIfNewNamespaceSerializerRequiresReconfiguration() throws Exception {
        testNamespaceSerializerUpgrade(new TestType.V1TestTypeSerializer(), new TestType.ReconfigurationRequiringTestTypeSerializer());
    }

    @Test
    public void testKeyedStateRegistrationFailsIfNewNamespaceSerializerIsIncompatible() throws Exception {
        try {
            testNamespaceSerializerUpgrade(new TestType.V1TestTypeSerializer(), new TestType.IncompatibleTestTypeSerializer());
            Assert.fail("should have failed");
        } catch (Exception expected) {
            // the new namespace serializer is incompatible; this should fail the restore
            Assert.assertTrue(ExceptionUtils.findThrowable(expected, StateMigrationException.class).isPresent());
        }
    }

    // -------------------------------------------------------------------------------
    // Operator state backend partitionable list state tests
    // -------------------------------------------------------------------------------
    @Test
    public void testOperatorParitionableListStateMigration() throws Exception {
        final String stateName = "partitionable-list-state";
        testOperatorPartitionableListStateUpgrade(new org.apache.flink.api.common.state.ListStateDescriptor(stateName, new TestType.V1TestTypeSerializer()), // restore with a V2 serializer that has a different schema
        new org.apache.flink.api.common.state.ListStateDescriptor(stateName, new TestType.V2TestTypeSerializer()));
    }

    @Test
    public void testOperatorParitionableListStateSerializerReconfiguration() throws Exception {
        final String stateName = "partitionable-list-state";
        testOperatorPartitionableListStateUpgrade(new org.apache.flink.api.common.state.ListStateDescriptor(stateName, new TestType.V1TestTypeSerializer()), // restore with a new serializer that requires reconfiguration
        new org.apache.flink.api.common.state.ListStateDescriptor(stateName, new TestType.ReconfigurationRequiringTestTypeSerializer()));
    }

    @Test
    public void testOperatorParitionableListStateRegistrationFailsIfNewSerializerIsIncompatible() throws Exception {
        final String stateName = "partitionable-list-state";
        try {
            testOperatorPartitionableListStateUpgrade(new org.apache.flink.api.common.state.ListStateDescriptor(stateName, new TestType.V1TestTypeSerializer()), // restore with a new incompatible serializer
            new org.apache.flink.api.common.state.ListStateDescriptor(stateName, new TestType.IncompatibleTestTypeSerializer()));
            Assert.fail("should have failed.");
        } catch (Exception e) {
            Assert.assertTrue(ExceptionUtils.findThrowable(e, StateMigrationException.class).isPresent());
        }
    }

    // -------------------------------------------------------------------------------
    // Operator state backend union list state tests
    // -------------------------------------------------------------------------------
    @Test
    public void testOperatorUnionListStateMigration() throws Exception {
        final String stateName = "union-list-state";
        testOperatorUnionListStateUpgrade(new org.apache.flink.api.common.state.ListStateDescriptor(stateName, new TestType.V1TestTypeSerializer()), // restore with a V2 serializer that has a different schema
        new org.apache.flink.api.common.state.ListStateDescriptor(stateName, new TestType.V2TestTypeSerializer()));
    }

    @Test
    public void testOperatorUnionListStateSerializerReconfiguration() throws Exception {
        final String stateName = "union-list-state";
        testOperatorUnionListStateUpgrade(new org.apache.flink.api.common.state.ListStateDescriptor(stateName, new TestType.V1TestTypeSerializer()), // restore with a new serializer that requires reconfiguration
        new org.apache.flink.api.common.state.ListStateDescriptor(stateName, new TestType.ReconfigurationRequiringTestTypeSerializer()));
    }

    @Test
    public void testOperatorUnionListStateRegistrationFailsIfNewSerializerIsIncompatible() throws Exception {
        final String stateName = "union-list-state";
        try {
            testOperatorUnionListStateUpgrade(new org.apache.flink.api.common.state.ListStateDescriptor(stateName, new TestType.V1TestTypeSerializer()), // restore with a new incompatible serializer
            new org.apache.flink.api.common.state.ListStateDescriptor(stateName, new TestType.IncompatibleTestTypeSerializer()));
            Assert.fail("should have failed.");
        } catch (Exception e) {
            Assert.assertTrue(ExceptionUtils.findThrowable(e, StateMigrationException.class).isPresent());
        }
    }

    // -------------------------------------------------------------------------------
    // Operator state backend broadcast state tests
    // -------------------------------------------------------------------------------
    @Test
    public void testBroadcastStateValueMigration() throws Exception {
        final String stateName = "broadcast-state";
        testBroadcastStateValueUpgrade(new org.apache.flink.api.common.state.MapStateDescriptor(stateName, IntSerializer.INSTANCE, new TestType.V1TestTypeSerializer()), // new value serializer is a V2 serializer with a different schema
        new org.apache.flink.api.common.state.MapStateDescriptor(stateName, IntSerializer.INSTANCE, new TestType.V2TestTypeSerializer()));
    }

    @Test
    public void testBroadcastStateKeyMigration() throws Exception {
        final String stateName = "broadcast-state";
        testBroadcastStateKeyUpgrade(new org.apache.flink.api.common.state.MapStateDescriptor(stateName, new TestType.V1TestTypeSerializer(), IntSerializer.INSTANCE), // new key serializer is a V2 serializer with a different schema
        new org.apache.flink.api.common.state.MapStateDescriptor(stateName, new TestType.V2TestTypeSerializer(), IntSerializer.INSTANCE));
    }

    @Test
    public void testBroadcastStateValueSerializerReconfiguration() throws Exception {
        final String stateName = "broadcast-state";
        testBroadcastStateValueUpgrade(new org.apache.flink.api.common.state.MapStateDescriptor(stateName, IntSerializer.INSTANCE, new TestType.V1TestTypeSerializer()), // new value serializer is a new serializer that requires reconfiguration
        new org.apache.flink.api.common.state.MapStateDescriptor(stateName, IntSerializer.INSTANCE, new TestType.ReconfigurationRequiringTestTypeSerializer()));
    }

    @Test
    public void testBroadcastStateKeySerializerReconfiguration() throws Exception {
        final String stateName = "broadcast-state";
        testBroadcastStateKeyUpgrade(new org.apache.flink.api.common.state.MapStateDescriptor(stateName, new TestType.V1TestTypeSerializer(), IntSerializer.INSTANCE), // new key serializer is a new serializer that requires reconfiguration
        new org.apache.flink.api.common.state.MapStateDescriptor(stateName, new TestType.ReconfigurationRequiringTestTypeSerializer(), IntSerializer.INSTANCE));
    }

    @Test
    public void testBroadcastStateRegistrationFailsIfNewValueSerializerIsIncompatible() throws Exception {
        final String stateName = "broadcast-state";
        try {
            testBroadcastStateValueUpgrade(new org.apache.flink.api.common.state.MapStateDescriptor(stateName, IntSerializer.INSTANCE, new TestType.V1TestTypeSerializer()), // new value serializer is incompatible
            new org.apache.flink.api.common.state.MapStateDescriptor(stateName, IntSerializer.INSTANCE, new TestType.IncompatibleTestTypeSerializer()));
            Assert.fail("should have failed.");
        } catch (Exception e) {
            Assert.assertTrue(ExceptionUtils.findThrowable(e, StateMigrationException.class).isPresent());
        }
    }

    @Test
    public void testBroadcastStateRegistrationFailsIfNewKeySerializerIsIncompatible() throws Exception {
        final String stateName = "broadcast-state";
        try {
            testBroadcastStateKeyUpgrade(new org.apache.flink.api.common.state.MapStateDescriptor(stateName, new TestType.V1TestTypeSerializer(), IntSerializer.INSTANCE), // new key serializer is incompatible
            new org.apache.flink.api.common.state.MapStateDescriptor(stateName, new TestType.IncompatibleTestTypeSerializer(), IntSerializer.INSTANCE));
            Assert.fail("should have failed.");
        } catch (Exception e) {
            Assert.assertTrue(ExceptionUtils.findThrowable(e, StateMigrationException.class).isPresent());
        }
    }

    // -------------------------------------------------------------------------------
    // Test types, serializers, and serializer snapshots
    // -------------------------------------------------------------------------------
    public static class CustomVoidNamespaceSerializer extends TypeSerializer<VoidNamespace> {
        private static final long serialVersionUID = 1L;

        public static final StateBackendMigrationTestBase.CustomVoidNamespaceSerializer INSTANCE = new StateBackendMigrationTestBase.CustomVoidNamespaceSerializer();

        @Override
        public boolean isImmutableType() {
            return true;
        }

        @Override
        public VoidNamespace createInstance() {
            return VoidNamespace.get();
        }

        @Override
        public VoidNamespace copy(VoidNamespace from) {
            return VoidNamespace.get();
        }

        @Override
        public VoidNamespace copy(VoidNamespace from, VoidNamespace reuse) {
            return VoidNamespace.get();
        }

        @Override
        public int getLength() {
            return 0;
        }

        @Override
        public void serialize(VoidNamespace record, DataOutputView target) throws IOException {
            // Make progress in the stream, write one byte.
            // 
            // We could just skip writing anything here, because of the way this is
            // used with the state backends, but if it is ever used somewhere else
            // (even though it is unlikely to happen), it would be a problem.
            target.write(0);
        }

        @Override
        public VoidNamespace deserialize(DataInputView source) throws IOException {
            source.readByte();
            return VoidNamespace.get();
        }

        @Override
        public VoidNamespace deserialize(VoidNamespace reuse, DataInputView source) throws IOException {
            source.readByte();
            return VoidNamespace.get();
        }

        @Override
        public void copy(DataInputView source, DataOutputView target) throws IOException {
            target.write(source.readByte());
        }

        @Override
        public TypeSerializer<VoidNamespace> duplicate() {
            return this;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof StateBackendMigrationTestBase.CustomVoidNamespaceSerializer;
        }

        @Override
        public int hashCode() {
            return getClass().hashCode();
        }

        @Override
        public TypeSerializerSnapshot<VoidNamespace> snapshotConfiguration() {
            return new StateBackendMigrationTestBase.CustomVoidNamespaceSerializerSnapshot();
        }
    }

    public static class CustomVoidNamespaceSerializerSnapshot implements TypeSerializerSnapshot<VoidNamespace> {
        @Override
        public TypeSerializer<VoidNamespace> restoreSerializer() {
            return new StateBackendMigrationTestBase.CustomVoidNamespaceSerializer();
        }

        @Override
        public TypeSerializerSchemaCompatibility<VoidNamespace> resolveSchemaCompatibility(TypeSerializer<VoidNamespace> newSerializer) {
            return TypeSerializerSchemaCompatibility.compatibleAsIs();
        }

        @Override
        public void writeSnapshot(DataOutputView out) throws IOException {
        }

        @Override
        public void readSnapshot(int readVersion, DataInputView in, ClassLoader userCodeClassLoader) throws IOException {
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof StateBackendMigrationTestBase.CustomVoidNamespaceSerializerSnapshot;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public int getCurrentVersion() {
            return 0;
        }
    }
}

