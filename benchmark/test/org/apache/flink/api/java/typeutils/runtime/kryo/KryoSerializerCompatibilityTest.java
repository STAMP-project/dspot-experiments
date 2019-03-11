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
package org.apache.flink.api.java.typeutils.runtime.kryo;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.TypeSerializerSchemaCompatibility;
import org.apache.flink.api.common.typeutils.TypeSerializerSerializationUtil;
import org.apache.flink.api.common.typeutils.TypeSerializerSnapshot;
import org.apache.flink.api.common.typeutils.TypeSerializerSnapshotSerializationUtil;
import org.apache.flink.core.memory.DataInputViewStreamWrapper;
import org.apache.flink.core.memory.DataOutputViewStreamWrapper;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests related to configuration snapshotting and reconfiguring for the {@link KryoSerializer}.
 */
public class KryoSerializerCompatibilityTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testMigrationStrategyForRemovedAvroDependency() throws Exception {
        KryoSerializer<KryoSerializerCompatibilityTest.TestClass> kryoSerializerForA = new KryoSerializer(KryoSerializerCompatibilityTest.TestClass.class, new ExecutionConfig());
        // read configuration again from bytes
        TypeSerializerSnapshot kryoSerializerConfigSnapshot;
        try (InputStream in = getClass().getResourceAsStream("/kryo-serializer-flink1.3-snapshot")) {
            kryoSerializerConfigSnapshot = TypeSerializerSnapshotSerializationUtil.readSerializerSnapshot(new DataInputViewStreamWrapper(in), Thread.currentThread().getContextClassLoader(), kryoSerializerForA);
        }
        @SuppressWarnings("unchecked")
        TypeSerializerSchemaCompatibility<KryoSerializerCompatibilityTest.TestClass> compatResult = kryoSerializerConfigSnapshot.resolveSchemaCompatibility(kryoSerializerForA);
        Assert.assertTrue(compatResult.isCompatibleAsIs());
    }

    @Test
    public void testDeserializingKryoSerializerWithoutAvro() throws Exception {
        final String resource = "serialized-kryo-serializer-1.3";
        TypeSerializer<?> serializer;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resource)) {
            DataInputViewStreamWrapper inView = new DataInputViewStreamWrapper(in);
            serializer = TypeSerializerSerializationUtil.tryReadSerializer(inView, getClass().getClassLoader());
        }
        Assert.assertNotNull(serializer);
        Assert.assertTrue((serializer instanceof KryoSerializer));
    }

    /**
     * Verifies that reconfiguration result is INCOMPATIBLE if data type has changed.
     */
    @Test
    public void testMigrationStrategyWithDifferentKryoType() throws Exception {
        KryoSerializer<KryoSerializerCompatibilityTest.TestClassA> kryoSerializerForA = new KryoSerializer(KryoSerializerCompatibilityTest.TestClassA.class, new ExecutionConfig());
        // snapshot configuration and serialize to bytes
        TypeSerializerSnapshot kryoSerializerConfigSnapshot = kryoSerializerForA.snapshotConfiguration();
        byte[] serializedConfig;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            TypeSerializerSnapshotSerializationUtil.writeSerializerSnapshot(new DataOutputViewStreamWrapper(out), kryoSerializerConfigSnapshot, kryoSerializerForA);
            serializedConfig = out.toByteArray();
        }
        KryoSerializer<KryoSerializerCompatibilityTest.TestClassB> kryoSerializerForB = new KryoSerializer(KryoSerializerCompatibilityTest.TestClassB.class, new ExecutionConfig());
        // read configuration again from bytes
        try (ByteArrayInputStream in = new ByteArrayInputStream(serializedConfig)) {
            kryoSerializerConfigSnapshot = TypeSerializerSnapshotSerializationUtil.readSerializerSnapshot(new DataInputViewStreamWrapper(in), Thread.currentThread().getContextClassLoader(), kryoSerializerForB);
        }
        @SuppressWarnings("unchecked")
        TypeSerializerSchemaCompatibility<KryoSerializerCompatibilityTest.TestClassB> compatResult = kryoSerializerConfigSnapshot.resolveSchemaCompatibility(kryoSerializerForB);
        Assert.assertTrue(compatResult.isIncompatible());
    }

    @Test
    public void testMigrationOfTypeWithAvroType() throws Exception {
        /* When Avro sees the schema "{"type" : "array", "items" : "boolean"}" it will create a field
        of type List<Integer> but the actual type will be GenericData.Array<Integer>. The
        KryoSerializer registers a special Serializer for this type that simply deserializes
        as ArrayList because Kryo cannot handle GenericData.Array well. Before Flink 1.4 Avro
        was always in the classpath but after 1.4 it's only present if the flink-avro jar is
        included. This test verifies that we can still deserialize data written pre-1.4.
         */
        class FakeAvroClass {
            public List<Integer> array;

            FakeAvroClass(List<Integer> array) {
                this.array = array;
            }
        }
        {
            ExecutionConfig executionConfig = new ExecutionConfig();
            KryoSerializer<FakeAvroClass> kryoSerializer = new KryoSerializer(FakeAvroClass.class, executionConfig);
            try (FileInputStream f = new FileInputStream("src/test/resources/type-with-avro-serialized-using-kryo");DataInputViewStreamWrapper inputView = new DataInputViewStreamWrapper(f)) {
                thrown.expectMessage("Could not find required Avro dependency");
                kryoSerializer.deserialize(inputView);
            }
        }
    }

    @Test
    public void testMigrationWithTypeDevoidOfAvroTypes() throws Exception {
        class FakeClass {
            public List<Integer> array;

            FakeClass(List<Integer> array) {
                this.array = array;
            }
        }
        {
            ExecutionConfig executionConfig = new ExecutionConfig();
            KryoSerializer<FakeClass> kryoSerializer = new KryoSerializer(FakeClass.class, executionConfig);
            try (FileInputStream f = new FileInputStream("src/test/resources/type-without-avro-serialized-using-kryo");DataInputViewStreamWrapper inputView = new DataInputViewStreamWrapper(f)) {
                FakeClass myTestClass = kryoSerializer.deserialize(inputView);
                Assert.assertThat(myTestClass.array.get(0), CoreMatchers.is(10));
                Assert.assertThat(myTestClass.array.get(1), CoreMatchers.is(20));
                Assert.assertThat(myTestClass.array.get(2), CoreMatchers.is(30));
            }
        }
    }

    /**
     * Tests that after reconfiguration, registration ids are reconfigured to
     * remain the same as the preceding KryoSerializer.
     */
    @Test
    public void testMigrationStrategyForDifferentRegistrationOrder() throws Exception {
        ExecutionConfig executionConfig = new ExecutionConfig();
        executionConfig.registerKryoType(KryoSerializerCompatibilityTest.TestClassA.class);
        executionConfig.registerKryoType(KryoSerializerCompatibilityTest.TestClassB.class);
        KryoSerializer<KryoSerializerCompatibilityTest.TestClass> kryoSerializer = new KryoSerializer(KryoSerializerCompatibilityTest.TestClass.class, executionConfig);
        // get original registration ids
        int testClassId = kryoSerializer.getKryo().getRegistration(KryoSerializerCompatibilityTest.TestClass.class).getId();
        int testClassAId = kryoSerializer.getKryo().getRegistration(KryoSerializerCompatibilityTest.TestClassA.class).getId();
        int testClassBId = kryoSerializer.getKryo().getRegistration(KryoSerializerCompatibilityTest.TestClassB.class).getId();
        // snapshot configuration and serialize to bytes
        TypeSerializerSnapshot kryoSerializerConfigSnapshot = kryoSerializer.snapshotConfiguration();
        byte[] serializedConfig;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            TypeSerializerSnapshotSerializationUtil.writeSerializerSnapshot(new DataOutputViewStreamWrapper(out), kryoSerializerConfigSnapshot, kryoSerializer);
            serializedConfig = out.toByteArray();
        }
        // use new config and instantiate new KryoSerializer
        executionConfig = new ExecutionConfig();
        executionConfig.registerKryoType(KryoSerializerCompatibilityTest.TestClassB.class);// test with B registered before A

        executionConfig.registerKryoType(KryoSerializerCompatibilityTest.TestClassA.class);
        kryoSerializer = new KryoSerializer(KryoSerializerCompatibilityTest.TestClass.class, executionConfig);
        // read configuration from bytes
        try (ByteArrayInputStream in = new ByteArrayInputStream(serializedConfig)) {
            kryoSerializerConfigSnapshot = TypeSerializerSnapshotSerializationUtil.readSerializerSnapshot(new DataInputViewStreamWrapper(in), Thread.currentThread().getContextClassLoader(), kryoSerializer);
        }
        // reconfigure - check reconfiguration result and that registration id remains the same
        @SuppressWarnings("unchecked")
        TypeSerializerSchemaCompatibility<KryoSerializerCompatibilityTest.TestClass> compatResult = kryoSerializerConfigSnapshot.resolveSchemaCompatibility(kryoSerializer);
        Assert.assertTrue(compatResult.isCompatibleWithReconfiguredSerializer());
        kryoSerializer = ((KryoSerializer<KryoSerializerCompatibilityTest.TestClass>) (compatResult.getReconfiguredSerializer()));
        Assert.assertEquals(testClassId, kryoSerializer.getKryo().getRegistration(KryoSerializerCompatibilityTest.TestClass.class).getId());
        Assert.assertEquals(testClassAId, kryoSerializer.getKryo().getRegistration(KryoSerializerCompatibilityTest.TestClassA.class).getId());
        Assert.assertEquals(testClassBId, kryoSerializer.getKryo().getRegistration(KryoSerializerCompatibilityTest.TestClassB.class).getId());
    }

    private static class TestClass {}

    private static class TestClassA {}

    private static class TestClassB {}

    private static class TestClassBSerializer extends Serializer {
        @Override
        public void write(Kryo kryo, Output output, Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object read(Kryo kryo, Input input, Class aClass) {
            throw new UnsupportedOperationException();
        }
    }
}

