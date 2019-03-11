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


import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.core.testutils.CommonTestUtils;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link ReducingStateDescriptor}.
 */
public class ReducingStateDescriptorTest extends TestLogger {
    @Test
    public void testReducingStateDescriptor() throws Exception {
        ReduceFunction<String> reducer = ( a, b) -> a;
        TypeSerializer<String> serializer = new org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer(String.class, new ExecutionConfig());
        ReducingStateDescriptor<String> descr = new ReducingStateDescriptor("testName", reducer, serializer);
        Assert.assertEquals("testName", descr.getName());
        Assert.assertNotNull(descr.getSerializer());
        Assert.assertEquals(serializer, descr.getSerializer());
        Assert.assertEquals(reducer, descr.getReduceFunction());
        ReducingStateDescriptor<String> copy = CommonTestUtils.createCopySerializable(descr);
        Assert.assertEquals("testName", copy.getName());
        Assert.assertNotNull(copy.getSerializer());
        Assert.assertEquals(serializer, copy.getSerializer());
    }

    @Test
    public void testHashCodeEquals() throws Exception {
        final String name = "testName";
        final ReduceFunction<String> reducer = ( a, b) -> a;
        ReducingStateDescriptor<String> original = new ReducingStateDescriptor(name, reducer, String.class);
        ReducingStateDescriptor<String> same = new ReducingStateDescriptor(name, reducer, String.class);
        ReducingStateDescriptor<String> sameBySerializer = new ReducingStateDescriptor(name, reducer, StringSerializer.INSTANCE);
        // test that hashCode() works on state descriptors with initialized and uninitialized serializers
        Assert.assertEquals(original.hashCode(), same.hashCode());
        Assert.assertEquals(original.hashCode(), sameBySerializer.hashCode());
        Assert.assertEquals(original, same);
        Assert.assertEquals(original, sameBySerializer);
        // equality with a clone
        ReducingStateDescriptor<String> clone = CommonTestUtils.createCopySerializable(original);
        Assert.assertEquals(original, clone);
        // equality with an initialized
        clone.initializeSerializerUnlessSet(new ExecutionConfig());
        Assert.assertEquals(original, clone);
        original.initializeSerializerUnlessSet(new ExecutionConfig());
        Assert.assertEquals(original, same);
    }
}

