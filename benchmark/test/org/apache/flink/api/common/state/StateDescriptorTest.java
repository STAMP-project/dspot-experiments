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
package org.apache.flink.api.common.state;


import java.io.File;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.testutils.CommonTestUtils;
import org.junit.Assert;
import org.junit.Test;

import static Type.VALUE;


/**
 * Tests for the common/shared functionality of {@link StateDescriptor}.
 */
public class StateDescriptorTest {
    // ------------------------------------------------------------------------
    // Tests for serializer initialization
    // ------------------------------------------------------------------------
    @Test
    public void testInitializeWithSerializer() throws Exception {
        final TypeSerializer<String> serializer = StringSerializer.INSTANCE;
        final StateDescriptorTest.TestStateDescriptor<String> descr = new StateDescriptorTest.TestStateDescriptor("test", serializer);
        Assert.assertTrue(descr.isSerializerInitialized());
        Assert.assertNotNull(descr.getSerializer());
        Assert.assertTrue(((descr.getSerializer()) instanceof StringSerializer));
        // this should not have any effect
        descr.initializeSerializerUnlessSet(new ExecutionConfig());
        Assert.assertTrue(descr.isSerializerInitialized());
        Assert.assertNotNull(descr.getSerializer());
        Assert.assertTrue(((descr.getSerializer()) instanceof StringSerializer));
        StateDescriptorTest.TestStateDescriptor<String> clone = CommonTestUtils.createCopySerializable(descr);
        Assert.assertTrue(clone.isSerializerInitialized());
        Assert.assertNotNull(clone.getSerializer());
        Assert.assertTrue(((clone.getSerializer()) instanceof StringSerializer));
    }

    @Test
    public void testInitializeSerializerBeforeSerialization() throws Exception {
        final StateDescriptorTest.TestStateDescriptor<String> descr = new StateDescriptorTest.TestStateDescriptor<>("test", String.class);
        Assert.assertFalse(descr.isSerializerInitialized());
        try {
            descr.getSerializer();
            Assert.fail("should fail with an exception");
        } catch (IllegalStateException ignored) {
        }
        descr.initializeSerializerUnlessSet(new ExecutionConfig());
        Assert.assertTrue(descr.isSerializerInitialized());
        Assert.assertNotNull(descr.getSerializer());
        Assert.assertTrue(((descr.getSerializer()) instanceof StringSerializer));
        StateDescriptorTest.TestStateDescriptor<String> clone = CommonTestUtils.createCopySerializable(descr);
        Assert.assertTrue(clone.isSerializerInitialized());
        Assert.assertNotNull(clone.getSerializer());
        Assert.assertTrue(((clone.getSerializer()) instanceof StringSerializer));
    }

    @Test
    public void testInitializeSerializerAfterSerialization() throws Exception {
        final StateDescriptorTest.TestStateDescriptor<String> descr = new StateDescriptorTest.TestStateDescriptor<>("test", String.class);
        Assert.assertFalse(descr.isSerializerInitialized());
        try {
            descr.getSerializer();
            Assert.fail("should fail with an exception");
        } catch (IllegalStateException ignored) {
        }
        StateDescriptorTest.TestStateDescriptor<String> clone = CommonTestUtils.createCopySerializable(descr);
        Assert.assertFalse(clone.isSerializerInitialized());
        try {
            clone.getSerializer();
            Assert.fail("should fail with an exception");
        } catch (IllegalStateException ignored) {
        }
        clone.initializeSerializerUnlessSet(new ExecutionConfig());
        Assert.assertTrue(clone.isSerializerInitialized());
        Assert.assertNotNull(clone.getSerializer());
        Assert.assertTrue(((clone.getSerializer()) instanceof StringSerializer));
    }

    @Test
    public void testInitializeSerializerAfterSerializationWithCustomConfig() throws Exception {
        // guard our test assumptions.
        Assert.assertEquals("broken test assumption", (-1), new KryoSerializer(String.class, new ExecutionConfig()).getKryo().getRegistration(File.class).getId());
        final ExecutionConfig config = new ExecutionConfig();
        config.registerKryoType(File.class);
        final StateDescriptorTest.TestStateDescriptor<Path> original = new StateDescriptorTest.TestStateDescriptor("test", Path.class);
        StateDescriptorTest.TestStateDescriptor<Path> clone = CommonTestUtils.createCopySerializable(original);
        clone.initializeSerializerUnlessSet(config);
        // serialized one (later initialized) carries the registration
        Assert.assertTrue(((((KryoSerializer<?>) (clone.getSerializer())).getKryo().getRegistration(File.class).getId()) > 0));
    }

    // ------------------------------------------------------------------------
    // Tests for serializer initialization
    // ------------------------------------------------------------------------
    /**
     * FLINK-6775, tests that the returned serializer is duplicated.
     * This allows to share the state descriptor across threads.
     */
    @Test
    public void testSerializerDuplication() throws Exception {
        // we need a serializer that actually duplicates for testing (a stateful one)
        // we use Kryo here, because it meets these conditions
        TypeSerializer<String> statefulSerializer = new KryoSerializer(String.class, new ExecutionConfig());
        StateDescriptorTest.TestStateDescriptor<String> descr = new StateDescriptorTest.TestStateDescriptor("foobar", statefulSerializer);
        TypeSerializer<String> serializerA = descr.getSerializer();
        TypeSerializer<String> serializerB = descr.getSerializer();
        // check that the retrieved serializers are not the same
        Assert.assertNotSame(serializerA, serializerB);
    }

    // ------------------------------------------------------------------------
    // Test hashCode() and equals()
    // ------------------------------------------------------------------------
    @Test
    public void testHashCodeAndEquals() throws Exception {
        final String name = "testName";
        StateDescriptorTest.TestStateDescriptor<String> original = new StateDescriptorTest.TestStateDescriptor<>(name, String.class);
        StateDescriptorTest.TestStateDescriptor<String> same = new StateDescriptorTest.TestStateDescriptor<>(name, String.class);
        StateDescriptorTest.TestStateDescriptor<String> sameBySerializer = new StateDescriptorTest.TestStateDescriptor(name, StringSerializer.INSTANCE);
        // test that hashCode() works on state descriptors with initialized and uninitialized serializers
        Assert.assertEquals(original.hashCode(), same.hashCode());
        Assert.assertEquals(original.hashCode(), sameBySerializer.hashCode());
        Assert.assertEquals(original, same);
        Assert.assertEquals(original, sameBySerializer);
        // equality with a clone
        StateDescriptorTest.TestStateDescriptor<String> clone = CommonTestUtils.createCopySerializable(original);
        Assert.assertEquals(original, clone);
        // equality with an initialized
        clone.initializeSerializerUnlessSet(new ExecutionConfig());
        Assert.assertEquals(original, clone);
        original.initializeSerializerUnlessSet(new ExecutionConfig());
        Assert.assertEquals(original, same);
    }

    @Test
    public void testEqualsSameNameAndTypeDifferentClass() throws Exception {
        final String name = "test name";
        final StateDescriptorTest.TestStateDescriptor<String> descr1 = new StateDescriptorTest.TestStateDescriptor<>(name, String.class);
        final StateDescriptorTest.OtherTestStateDescriptor<String> descr2 = new StateDescriptorTest.OtherTestStateDescriptor<>(name, String.class);
        Assert.assertNotEquals(descr1, descr2);
    }

    // ------------------------------------------------------------------------
    // Mock implementations and test types
    // ------------------------------------------------------------------------
    private static class TestStateDescriptor<T> extends StateDescriptor<State, T> {
        private static final long serialVersionUID = 1L;

        TestStateDescriptor(String name, TypeSerializer<T> serializer) {
            super(name, serializer, null);
        }

        TestStateDescriptor(String name, TypeInformation<T> typeInfo) {
            super(name, typeInfo, null);
        }

        TestStateDescriptor(String name, Class<T> type) {
            super(name, type, null);
        }

        @Override
        public Type getType() {
            return VALUE;
        }
    }

    private static class OtherTestStateDescriptor<T> extends StateDescriptor<State, T> {
        private static final long serialVersionUID = 1L;

        OtherTestStateDescriptor(String name, TypeSerializer<T> serializer) {
            super(name, serializer, null);
        }

        OtherTestStateDescriptor(String name, TypeInformation<T> typeInfo) {
            super(name, typeInfo, null);
        }

        OtherTestStateDescriptor(String name, Class<T> type) {
            super(name, type, null);
        }

        @Override
        public Type getType() {
            return Type.VALUE;
        }
    }
}

