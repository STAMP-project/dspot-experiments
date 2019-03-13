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
package org.apache.flink.api.common.typeutils;


import java.io.IOException;
import java.util.Arrays;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.core.memory.DataInputDeserializer;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputSerializer;
import org.apache.flink.core.memory.DataOutputView;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test suite for the {@link CompositeTypeSerializerSnapshot}.
 */
public class CompositeTypeSerializerSnapshotTest {
    // ------------------------------------------------------------------------------------------------
    // Scope: tests CompositeTypeSerializerSnapshot#resolveSchemaCompatibility
    // ------------------------------------------------------------------------------------------------
    @Test
    public void testIncompatiblePrecedence() throws IOException {
        final String OUTER_CONFIG = "outer-config";
        final TypeSerializer<?>[] testNestedSerializers = new TypeSerializer<?>[]{ new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.COMPATIBLE_AS_IS), new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.COMPATIBLE_AFTER_MIGRATION), new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.INCOMPATIBLE), new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.COMPATIBLE_WITH_RECONFIGURED_SERIALIZER) };
        TypeSerializerSchemaCompatibility<String> compatibility = snapshotCompositeSerializerAndGetSchemaCompatibilityAfterRestore(testNestedSerializers, testNestedSerializers, OUTER_CONFIG, OUTER_CONFIG);
        Assert.assertTrue(compatibility.isIncompatible());
    }

    @Test
    public void testCompatibleAfterMigrationPrecedence() throws IOException {
        final String OUTER_CONFIG = "outer-config";
        TypeSerializer<?>[] testNestedSerializers = new TypeSerializer<?>[]{ new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.COMPATIBLE_AS_IS), new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.COMPATIBLE_AFTER_MIGRATION), new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.COMPATIBLE_WITH_RECONFIGURED_SERIALIZER), new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.COMPATIBLE_AS_IS) };
        TypeSerializerSchemaCompatibility<String> compatibility = snapshotCompositeSerializerAndGetSchemaCompatibilityAfterRestore(testNestedSerializers, testNestedSerializers, OUTER_CONFIG, OUTER_CONFIG);
        Assert.assertTrue(compatibility.isCompatibleAfterMigration());
    }

    @Test
    public void testCompatibleWithReconfiguredSerializerPrecedence() throws IOException {
        final String OUTER_CONFIG = "outer-config";
        TypeSerializer<?>[] testNestedSerializers = new TypeSerializer<?>[]{ new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.COMPATIBLE_AS_IS), new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.COMPATIBLE_WITH_RECONFIGURED_SERIALIZER), new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.COMPATIBLE_AS_IS) };
        TypeSerializerSchemaCompatibility<String> compatibility = snapshotCompositeSerializerAndGetSchemaCompatibilityAfterRestore(testNestedSerializers, testNestedSerializers, OUTER_CONFIG, OUTER_CONFIG);
        Assert.assertTrue(compatibility.isCompatibleWithReconfiguredSerializer());
        CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer reconfiguredSerializer = ((CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer) (compatibility.getReconfiguredSerializer()));
        TypeSerializer<?>[] reconfiguredNestedSerializers = reconfiguredSerializer.getNestedSerializers();
        // nested serializer at index 1 should strictly be a ReconfiguredNestedSerializer
        Assert.assertTrue(((reconfiguredNestedSerializers[0].getClass()) == (CompositeTypeSerializerSnapshotTest.NestedSerializer.class)));
        Assert.assertTrue(((reconfiguredNestedSerializers[1].getClass()) == (CompositeTypeSerializerSnapshotTest.ReconfiguredNestedSerializer.class)));
        Assert.assertTrue(((reconfiguredNestedSerializers[2].getClass()) == (CompositeTypeSerializerSnapshotTest.NestedSerializer.class)));
    }

    @Test
    public void testCompatibleAsIsPrecedence() throws IOException {
        final String OUTER_CONFIG = "outer-config";
        TypeSerializer<?>[] testNestedSerializers = new TypeSerializer<?>[]{ new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.COMPATIBLE_AS_IS), new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.COMPATIBLE_AS_IS) };
        TypeSerializerSchemaCompatibility<String> compatibility = snapshotCompositeSerializerAndGetSchemaCompatibilityAfterRestore(testNestedSerializers, testNestedSerializers, OUTER_CONFIG, OUTER_CONFIG);
        Assert.assertTrue(compatibility.isCompatibleAsIs());
    }

    @Test
    public void testOuterSnapshotCompatibilityPrecedence() throws IOException {
        final String INIT_OUTER_CONFIG = "outer-config";
        final String INCOMPAT_OUTER_CONFIG = "incompat-outer-config";
        TypeSerializer<?>[] testNestedSerializers = new TypeSerializer<?>[]{ new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.COMPATIBLE_AS_IS) };
        TypeSerializerSchemaCompatibility<String> compatibility = snapshotCompositeSerializerAndGetSchemaCompatibilityAfterRestore(testNestedSerializers, testNestedSerializers, INIT_OUTER_CONFIG, INCOMPAT_OUTER_CONFIG);
        // even though nested serializers are compatible, incompatibility of the outer
        // snapshot should have higher precedence in the final result
        Assert.assertTrue(compatibility.isIncompatible());
    }

    @Test
    public void testNestedFieldSerializerArityMismatchPrecedence() throws IOException {
        final String OUTER_CONFIG = "outer-config";
        final TypeSerializer<?>[] initialNestedSerializers = new TypeSerializer<?>[]{ new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.COMPATIBLE_AS_IS) };
        final TypeSerializer<?>[] newNestedSerializers = new TypeSerializer<?>[]{ new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.COMPATIBLE_AS_IS), new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.COMPATIBLE_AS_IS), new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.COMPATIBLE_AS_IS) };
        TypeSerializerSchemaCompatibility<String> compatibility = snapshotCompositeSerializerAndGetSchemaCompatibilityAfterRestore(initialNestedSerializers, newNestedSerializers, OUTER_CONFIG, OUTER_CONFIG);
        // arity mismatch in the nested serializers should return incompatible as the result
        Assert.assertTrue(compatibility.isIncompatible());
    }

    // ------------------------------------------------------------------------------------------------
    // Scope: tests CompositeTypeSerializerSnapshot#restoreSerializer
    // ------------------------------------------------------------------------------------------------
    @Test
    public void testRestoreCompositeTypeSerializer() throws IOException {
        // the target compatibilities of the nested serializers doesn't matter,
        // because we're only testing the restore serializer
        TypeSerializer<?>[] testNestedSerializers = new TypeSerializer<?>[]{ new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.COMPATIBLE_AS_IS), new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.INCOMPATIBLE), new CompositeTypeSerializerSnapshotTest.NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility.COMPATIBLE_AFTER_MIGRATION) };
        CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer testSerializer = new CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer("outer-config", testNestedSerializers);
        TypeSerializerSnapshot<String> testSerializerSnapshot = testSerializer.snapshotConfiguration();
        DataOutputSerializer out = new DataOutputSerializer(128);
        TypeSerializerSnapshot.writeVersionedSnapshot(out, testSerializerSnapshot);
        DataInputDeserializer in = new DataInputDeserializer(out.getCopyOfBuffer());
        testSerializerSnapshot = TypeSerializerSnapshot.readVersionedSnapshot(in, Thread.currentThread().getContextClassLoader());
        // now, restore the composite type serializer;
        // the restored nested serializer should be a RestoredNestedSerializer
        testSerializer = ((CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer) (testSerializerSnapshot.restoreSerializer()));
        Assert.assertTrue(((testSerializer.getNestedSerializers()[0].getClass()) == (CompositeTypeSerializerSnapshotTest.RestoredNestedSerializer.class)));
        Assert.assertTrue(((testSerializer.getNestedSerializers()[1].getClass()) == (CompositeTypeSerializerSnapshotTest.RestoredNestedSerializer.class)));
        Assert.assertTrue(((testSerializer.getNestedSerializers()[2].getClass()) == (CompositeTypeSerializerSnapshotTest.RestoredNestedSerializer.class)));
    }

    // ------------------------------------------------------------------------------------------------
    // Test utilities
    // ------------------------------------------------------------------------------------------------
    /**
     * A simple composite serializer used for testing.
     * It can be configured with an array of nested serializers, as well as outer configuration (represented as String).
     */
    public static class TestCompositeTypeSerializer extends TypeSerializer<String> {
        private static final long serialVersionUID = -545688468997398105L;

        private static final StringSerializer delegateSerializer = StringSerializer.INSTANCE;

        private final String outerConfiguration;

        private final TypeSerializer<?>[] nestedSerializers;

        TestCompositeTypeSerializer(String outerConfiguration, TypeSerializer<?>[] nestedSerializers) {
            this.outerConfiguration = outerConfiguration;
            this.nestedSerializers = nestedSerializers;
        }

        public String getOuterConfiguration() {
            return outerConfiguration;
        }

        TypeSerializer<?>[] getNestedSerializers() {
            return nestedSerializers;
        }

        @Override
        public TypeSerializerSnapshot<String> snapshotConfiguration() {
            return new CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializerSnapshot(this);
        }

        // --------------------------------------------------------------------------------
        // Serialization delegation
        // --------------------------------------------------------------------------------
        @Override
        public String deserialize(String reuse, DataInputView source) throws IOException {
            return CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer.delegateSerializer.deserialize(reuse, source);
        }

        @Override
        public String deserialize(DataInputView source) throws IOException {
            return CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer.delegateSerializer.deserialize(source);
        }

        @Override
        public void serialize(String record, DataOutputView target) throws IOException {
            CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer.delegateSerializer.serialize(record, target);
        }

        @Override
        public void copy(DataInputView source, DataOutputView target) throws IOException {
            CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer.delegateSerializer.copy(source, target);
        }

        @Override
        public String copy(String from) {
            return CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer.delegateSerializer.copy(from);
        }

        @Override
        public String copy(String from, String reuse) {
            return CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer.delegateSerializer.copy(from, reuse);
        }

        @Override
        public String createInstance() {
            return CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer.delegateSerializer.createInstance();
        }

        @Override
        public TypeSerializer<String> duplicate() {
            return this;
        }

        @Override
        public boolean isImmutableType() {
            return false;
        }

        @Override
        public int getLength() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer) {
                return Arrays.equals(nestedSerializers, ((CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer) (obj)).getNestedSerializers());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(nestedSerializers);
        }
    }

    /**
     * Snapshot class for the {@link TestCompositeTypeSerializer}.
     */
    public static class TestCompositeTypeSerializerSnapshot extends CompositeTypeSerializerSnapshot<String, CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer> {
        private String outerConfiguration;

        public TestCompositeTypeSerializerSnapshot() {
            super(CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer.class);
        }

        TestCompositeTypeSerializerSnapshot(CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer serializer) {
            super(serializer);
            this.outerConfiguration = serializer.getOuterConfiguration();
        }

        @Override
        protected CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer createOuterSerializerWithNestedSerializers(TypeSerializer<?>[] nestedSerializers) {
            return new CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer(outerConfiguration, nestedSerializers);
        }

        @Override
        protected TypeSerializer<?>[] getNestedSerializers(CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer outerSerializer) {
            return outerSerializer.getNestedSerializers();
        }

        @Override
        protected void writeOuterSnapshot(DataOutputView out) throws IOException {
            out.writeUTF(outerConfiguration);
        }

        @Override
        public void readOuterSnapshot(int readOuterSnapshotVersion, DataInputView in, ClassLoader userCodeClassLoader) throws IOException {
            Assert.assertEquals(getCurrentOuterSnapshotVersion(), readOuterSnapshotVersion);
            this.outerConfiguration = in.readUTF();
        }

        @Override
        protected boolean isOuterSnapshotCompatible(CompositeTypeSerializerSnapshotTest.TestCompositeTypeSerializer newSerializer) {
            return outerConfiguration.equals(newSerializer.getOuterConfiguration());
        }

        @Override
        public int getCurrentOuterSnapshotVersion() {
            return 1;
        }
    }

    public enum TargetCompatibility {

        COMPATIBLE_AS_IS,
        COMPATIBLE_AFTER_MIGRATION,
        COMPATIBLE_WITH_RECONFIGURED_SERIALIZER,
        INCOMPATIBLE;}

    /**
     * Used as nested serializers in the test composite serializer.
     * A nested serializer can be configured with a {@link TargetCompatibility},
     * which indicates what the result of the schema compatibility check should be
     * when a new instance of it is being checked for compatibility.
     */
    public static class NestedSerializer extends TypeSerializer<String> {
        private static final long serialVersionUID = -6175000932620623446L;

        private static final StringSerializer delegateSerializer = StringSerializer.INSTANCE;

        private final CompositeTypeSerializerSnapshotTest.TargetCompatibility targetCompatibility;

        NestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility targetCompatibility) {
            this.targetCompatibility = targetCompatibility;
        }

        @Override
        public TypeSerializerSnapshot<String> snapshotConfiguration() {
            return new CompositeTypeSerializerSnapshotTest.NestedSerializerSnapshot(targetCompatibility);
        }

        // --------------------------------------------------------------------------------
        // Serialization delegation
        // --------------------------------------------------------------------------------
        @Override
        public String deserialize(String reuse, DataInputView source) throws IOException {
            return CompositeTypeSerializerSnapshotTest.NestedSerializer.delegateSerializer.deserialize(reuse, source);
        }

        @Override
        public String deserialize(DataInputView source) throws IOException {
            return CompositeTypeSerializerSnapshotTest.NestedSerializer.delegateSerializer.deserialize(source);
        }

        @Override
        public void serialize(String record, DataOutputView target) throws IOException {
            CompositeTypeSerializerSnapshotTest.NestedSerializer.delegateSerializer.serialize(record, target);
        }

        @Override
        public void copy(DataInputView source, DataOutputView target) throws IOException {
            CompositeTypeSerializerSnapshotTest.NestedSerializer.delegateSerializer.copy(source, target);
        }

        @Override
        public String copy(String from) {
            return CompositeTypeSerializerSnapshotTest.NestedSerializer.delegateSerializer.copy(from);
        }

        @Override
        public String copy(String from, String reuse) {
            return CompositeTypeSerializerSnapshotTest.NestedSerializer.delegateSerializer.copy(from, reuse);
        }

        @Override
        public String createInstance() {
            return CompositeTypeSerializerSnapshotTest.NestedSerializer.delegateSerializer.createInstance();
        }

        @Override
        public TypeSerializer<String> duplicate() {
            return this;
        }

        @Override
        public boolean isImmutableType() {
            return false;
        }

        @Override
        public int getLength() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return (targetCompatibility) == (((CompositeTypeSerializerSnapshotTest.NestedSerializer) (obj)).targetCompatibility);
        }

        @Override
        public int hashCode() {
            return targetCompatibility.hashCode();
        }
    }

    /**
     * Snapshot of the {@link NestedSerializer}.
     */
    public static class NestedSerializerSnapshot implements TypeSerializerSnapshot<String> {
        private CompositeTypeSerializerSnapshotTest.TargetCompatibility targetCompatibility;

        public NestedSerializerSnapshot() {
        }

        public NestedSerializerSnapshot(CompositeTypeSerializerSnapshotTest.TargetCompatibility targetCompatibility) {
            this.targetCompatibility = targetCompatibility;
        }

        @Override
        public void writeSnapshot(DataOutputView out) throws IOException {
            out.writeInt(targetCompatibility.ordinal());
        }

        @Override
        public void readSnapshot(int readVersion, DataInputView in, ClassLoader userCodeClassLoader) throws IOException {
            this.targetCompatibility = CompositeTypeSerializerSnapshotTest.TargetCompatibility.values()[in.readInt()];
        }

        @Override
        public TypeSerializerSchemaCompatibility<String> resolveSchemaCompatibility(TypeSerializer<String> newSerializer) {
            // checks the exact class instead of using instanceof;
            // this ensures that we get a new serializer, and not a ReconfiguredNestedSerializer or RestoredNestedSerializer
            if ((newSerializer.getClass()) == (CompositeTypeSerializerSnapshotTest.NestedSerializer.class)) {
                switch (targetCompatibility) {
                    case COMPATIBLE_AS_IS :
                        return TypeSerializerSchemaCompatibility.compatibleAsIs();
                    case COMPATIBLE_AFTER_MIGRATION :
                        return TypeSerializerSchemaCompatibility.compatibleAfterMigration();
                    case COMPATIBLE_WITH_RECONFIGURED_SERIALIZER :
                        return TypeSerializerSchemaCompatibility.compatibleWithReconfiguredSerializer(new CompositeTypeSerializerSnapshotTest.ReconfiguredNestedSerializer(targetCompatibility));
                    case INCOMPATIBLE :
                        return TypeSerializerSchemaCompatibility.incompatible();
                    default :
                        throw new IllegalStateException("Unexpected target compatibility.");
                }
            }
            throw new IllegalArgumentException(("Expected the new serializer to be of class " + (CompositeTypeSerializerSnapshotTest.NestedSerializer.class)));
        }

        @Override
        public TypeSerializer<String> restoreSerializer() {
            return new CompositeTypeSerializerSnapshotTest.RestoredNestedSerializer(targetCompatibility);
        }

        @Override
        public int getCurrentVersion() {
            return 1;
        }
    }

    /**
     * A variant of the {@link NestedSerializer} used only when creating a reconfigured instance
     * of the serializer. This is used in tests as a tag to identify that the correct serializer
     * instances are being used.
     */
    static class ReconfiguredNestedSerializer extends CompositeTypeSerializerSnapshotTest.NestedSerializer {
        private static final long serialVersionUID = -1396401178636869659L;

        public ReconfiguredNestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility targetCompatibility) {
            super(targetCompatibility);
        }
    }

    /**
     * A variant of the {@link NestedSerializer} used only when creating a restored instance
     * of the serializer. This is used in tests as a tag to identify that the correct serializer
     * instances are being used.
     */
    static class RestoredNestedSerializer extends CompositeTypeSerializerSnapshotTest.NestedSerializer {
        private static final long serialVersionUID = -1396401178636869659L;

        public RestoredNestedSerializer(CompositeTypeSerializerSnapshotTest.TargetCompatibility targetCompatibility) {
            super(targetCompatibility);
        }
    }
}

