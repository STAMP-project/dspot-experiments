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
package com.hazelcast.map.impl;


import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestJavaSerializationUtils;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LazyMapEntryTest extends HazelcastTestSupport {
    private LazyMapEntry entry = new LazyMapEntry();

    private InternalSerializationService serializationService = new DefaultSerializationServiceBuilder().build();

    @Test
    public void testJavaSerialization() throws IOException, ClassNotFoundException {
        Data keyData = serializationService.toData("keyData");
        Data valueData = serializationService.toData("valueData");
        entry.init(serializationService, keyData, valueData, null);
        LazyMapEntry copy = TestJavaSerializationUtils.serializeAndDeserialize(entry);
        Assert.assertEquals(entry, copy);
    }

    @Test
    public void testIdentifiedDataSerializableSerialization() throws IOException, ClassNotFoundException {
        Data keyData = serializationService.toData("keyData");
        Data valueData = serializationService.toData("valueData");
        entry.init(serializationService, keyData, valueData, null);
        LazyMapEntry copy = serializationService.toObject(serializationService.toData(entry));
        Assert.assertEquals(entry, copy);
    }

    @Test
    public void test_init() {
        Data keyData = serializationService.toData("keyData");
        Data valueData = serializationService.toData("valueData");
        entry.init(serializationService, keyData, valueData, null);
        Object valueObject = entry.getValue();
        entry.init(serializationService, keyData, valueObject, null);
        Assert.assertTrue("Old valueData should not be here", (valueData != (entry.getValueData())));
    }

    @Test
    public void test_init_doesNotSerializeObject() {
        Data key = serializationService.toData("keyData");
        LazyMapEntryTest.MyObject value = new LazyMapEntryTest.MyObject();
        entry.init(serializationService, key, value, null);
        Assert.assertEquals(0, value.serializedCount);
    }

    @Test
    public void test_init_doesNotDeserializeObject() {
        LazyMapEntryTest.MyObject keyObject = new LazyMapEntryTest.MyObject();
        LazyMapEntryTest.MyObject valueObject = new LazyMapEntryTest.MyObject();
        Data keyData = serializationService.toData(keyObject);
        Data valueData = serializationService.toData(valueObject);
        entry.init(serializationService, keyData, valueData, null);
        Assert.assertEquals(1, keyObject.serializedCount);
        Assert.assertEquals(1, valueObject.serializedCount);
        Assert.assertEquals(0, keyObject.deserializedCount);
        Assert.assertEquals(0, valueObject.deserializedCount);
    }

    @Test
    public void testLazyDeserializationWorks() {
        LazyMapEntryTest.MyObject keyObject = new LazyMapEntryTest.MyObject();
        LazyMapEntryTest.MyObject valueObject = new LazyMapEntryTest.MyObject();
        Data keyData = serializationService.toData(keyObject);
        Data valueData = serializationService.toData(valueObject);
        entry.init(serializationService, keyData, valueData, null);
        Object key = entry.getKey();
        Object value = entry.getValue();
        HazelcastTestSupport.assertInstanceOf(LazyMapEntryTest.MyObject.class, key);
        HazelcastTestSupport.assertInstanceOf(LazyMapEntryTest.MyObject.class, value);
        Assert.assertEquals(1, ((LazyMapEntryTest.MyObject) (key)).deserializedCount);
        Assert.assertEquals(1, ((LazyMapEntryTest.MyObject) (value)).deserializedCount);
    }

    private static class MyObject implements DataSerializable , Serializable {
        int serializedCount = 0;

        int deserializedCount = 0;

        public MyObject() {
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeInt((++(serializedCount)));
            out.writeInt(deserializedCount);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            serializedCount = in.readInt();
            deserializedCount = (in.readInt()) + 1;
        }

        @Override
        public String toString() {
            return (((("MyObject{" + "deserializedCount=") + (deserializedCount)) + ", serializedCount=") + (serializedCount)) + '}';
        }
    }
}

