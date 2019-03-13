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
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.core.testutils.CommonTestUtils;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link ValueStateDescriptor}.
 */
public class ValueStateDescriptorTest extends TestLogger {
    @Test
    public void testHashCodeEquals() throws Exception {
        final String name = "testName";
        ValueStateDescriptor<String> original = new ValueStateDescriptor(name, String.class);
        ValueStateDescriptor<String> same = new ValueStateDescriptor(name, String.class);
        ValueStateDescriptor<String> sameBySerializer = new ValueStateDescriptor(name, StringSerializer.INSTANCE);
        // test that hashCode() works on state descriptors with initialized and uninitialized serializers
        Assert.assertEquals(original.hashCode(), same.hashCode());
        Assert.assertEquals(original.hashCode(), sameBySerializer.hashCode());
        Assert.assertEquals(original, same);
        Assert.assertEquals(original, sameBySerializer);
        // equality with a clone
        ValueStateDescriptor<String> clone = CommonTestUtils.createCopySerializable(original);
        Assert.assertEquals(original, clone);
        // equality with an initialized
        clone.initializeSerializerUnlessSet(new ExecutionConfig());
        Assert.assertEquals(original, clone);
        original.initializeSerializerUnlessSet(new ExecutionConfig());
        Assert.assertEquals(original, same);
    }

    @Test
    public void testVeryLargeDefaultValue() throws Exception {
        // ensure that we correctly read very large data when deserializing the default value
        TypeSerializer<String> serializer = new org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer(String.class, new ExecutionConfig());
        byte[] data = new byte[200000];
        for (int i = 0; i < 200000; i++) {
            data[i] = 65;
        }
        data[199000] = '\u0000';
        String defaultValue = new String(data, ConfigConstants.DEFAULT_CHARSET);
        ValueStateDescriptor<String> descr = new ValueStateDescriptor("testName", serializer, defaultValue);
        Assert.assertEquals("testName", descr.getName());
        Assert.assertEquals(defaultValue, descr.getDefaultValue());
        Assert.assertNotNull(descr.getSerializer());
        Assert.assertEquals(serializer, descr.getSerializer());
        ValueStateDescriptor<String> copy = CommonTestUtils.createCopySerializable(descr);
        Assert.assertEquals("testName", copy.getName());
        Assert.assertEquals(defaultValue, copy.getDefaultValue());
        Assert.assertNotNull(copy.getSerializer());
        Assert.assertEquals(serializer, copy.getSerializer());
    }
}

