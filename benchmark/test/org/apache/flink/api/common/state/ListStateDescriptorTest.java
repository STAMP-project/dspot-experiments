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
package org.apache.flink.api.common.state;


import java.util.List;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.base.ListSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.core.testutils.CommonTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link ListStateDescriptor}.
 */
public class ListStateDescriptorTest {
    @Test
    public void testListStateDescriptor() throws Exception {
        TypeSerializer<String> serializer = new org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer(String.class, new ExecutionConfig());
        ListStateDescriptor<String> descr = new ListStateDescriptor("testName", serializer);
        Assert.assertEquals("testName", descr.getName());
        Assert.assertNotNull(descr.getSerializer());
        Assert.assertTrue(((descr.getSerializer()) instanceof ListSerializer));
        Assert.assertNotNull(descr.getElementSerializer());
        Assert.assertEquals(serializer, descr.getElementSerializer());
        ListStateDescriptor<String> copy = CommonTestUtils.createCopySerializable(descr);
        Assert.assertEquals("testName", copy.getName());
        Assert.assertNotNull(copy.getSerializer());
        Assert.assertTrue(((copy.getSerializer()) instanceof ListSerializer));
        Assert.assertNotNull(copy.getElementSerializer());
        Assert.assertEquals(serializer, copy.getElementSerializer());
    }

    @Test
    public void testHashCodeEquals() throws Exception {
        final String name = "testName";
        ListStateDescriptor<String> original = new ListStateDescriptor(name, String.class);
        ListStateDescriptor<String> same = new ListStateDescriptor(name, String.class);
        ListStateDescriptor<String> sameBySerializer = new ListStateDescriptor(name, StringSerializer.INSTANCE);
        // test that hashCode() works on state descriptors with initialized and uninitialized serializers
        Assert.assertEquals(original.hashCode(), same.hashCode());
        Assert.assertEquals(original.hashCode(), sameBySerializer.hashCode());
        Assert.assertEquals(original, same);
        Assert.assertEquals(original, sameBySerializer);
        // equality with a clone
        ListStateDescriptor<String> clone = CommonTestUtils.createCopySerializable(original);
        Assert.assertEquals(original, clone);
        // equality with an initialized
        clone.initializeSerializerUnlessSet(new ExecutionConfig());
        Assert.assertEquals(original, clone);
        original.initializeSerializerUnlessSet(new ExecutionConfig());
        Assert.assertEquals(original, same);
    }

    /**
     * FLINK-6775.
     *
     * <p>Tests that the returned serializer is duplicated. This allows to
     * share the state descriptor.
     */
    @Test
    public void testSerializerDuplication() {
        // we need a serializer that actually duplicates for testing (a stateful one)
        // we use Kryo here, because it meets these conditions
        TypeSerializer<String> statefulSerializer = new org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer(String.class, new ExecutionConfig());
        ListStateDescriptor<String> descr = new ListStateDescriptor("foobar", statefulSerializer);
        TypeSerializer<String> serializerA = descr.getElementSerializer();
        TypeSerializer<String> serializerB = descr.getElementSerializer();
        // check that the retrieved serializers are not the same
        Assert.assertNotSame(serializerA, serializerB);
        TypeSerializer<List<String>> listSerializerA = descr.getSerializer();
        TypeSerializer<List<String>> listSerializerB = descr.getSerializer();
        Assert.assertNotSame(listSerializerA, listSerializerB);
    }
}

