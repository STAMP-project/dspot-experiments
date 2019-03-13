/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.serialization.impl;


import ConstantSerializers.TheByteArraySerializer;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.serialization.Serializer;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.nio.ByteOrder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ByteArraySerializerAdapterTest {
    private ByteArraySerializerAdapter adapter;

    private TheByteArraySerializer serializer;

    private InternalSerializationService mockSerializationService;

    @Test
    public void testAdaptor() throws Exception {
        byte[] testByteArray = new byte[]{ 1, 2, 3 };
        ByteArrayObjectDataOutput out = new ByteArrayObjectDataOutput(10, mockSerializationService, ByteOrder.BIG_ENDIAN);
        ByteArrayObjectDataInput in = new ByteArrayObjectDataInput(out.buffer, mockSerializationService, ByteOrder.BIG_ENDIAN);
        adapter.write(out, testByteArray);
        byte[] read = ((byte[]) (adapter.read(in)));
        Serializer impl = adapter.getImpl();
        Assert.assertArrayEquals(testByteArray, read);
        Assert.assertEquals(serializer, impl);
        Assert.assertEquals(serializer.getTypeId(), adapter.getTypeId());
    }

    @Test
    public void testAdaptorEqualAndHashCode() throws Exception {
        ByteArraySerializerAdapter theOther = new ByteArraySerializerAdapter(serializer);
        ByteArraySerializerAdapter theEmptyOne = new ByteArraySerializerAdapter(null);
        Assert.assertEquals(adapter, adapter);
        Assert.assertEquals(adapter, theOther);
        Assert.assertNotEquals(adapter, null);
        Assert.assertNotEquals(adapter, "Not An Adaptor");
        Assert.assertNotEquals(adapter, theEmptyOne);
        Assert.assertEquals(adapter.hashCode(), serializer.hashCode());
        Assert.assertEquals(0, theEmptyOne.hashCode());
    }

    @Test
    public void testString() throws Exception {
        Assert.assertNotNull(adapter.toString());
    }
}

