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
package com.hazelcast.query.impl;


import QueryConstants.KEY_ATTRIBUTE_NAME;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.query.SampleTestObjects;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsSame;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueryEntryTest extends HazelcastTestSupport {
    protected InternalSerializationService serializationService;

    // ========================== getAttribute ===========================================
    @Test
    public void getAttribute_whenValueIsPortableObject_thenConvertedToData() {
        Data key = serializationService.toData("indexedKey");
        Portable value = new SampleTestObjects.PortableEmployee(30, "peter");
        QueryableEntry queryEntry = createEntry(key, value, newExtractor());
        // in the portable-data, the attribute 'name' is called 'n'. So if we can retrieve on n
        // correctly it shows that we have used the Portable data, not the actual Portable object
        Object result = queryEntry.getAttributeValue("n");
        Assert.assertEquals("peter", result);
    }

    @Test
    public void getAttribute_whenKeyIsPortableObject_thenConvertedToData() {
        Data key = serializationService.toData(new SampleTestObjects.PortableEmployee(30, "peter"));
        QueryEntryTest.SerializableObject value = new QueryEntryTest.SerializableObject();
        QueryableEntry queryEntry = createEntry(key, value, newExtractor());
        // in the portable-data, the attribute 'name' is called 'n'. So if we can retrieve on n
        // correctly it shows that we have used the Portable data, not the actual Portable object
        Object result = queryEntry.getAttributeValue(((KEY_ATTRIBUTE_NAME.value()) + ".n"));
        Assert.assertEquals("peter", result);
    }

    @Test
    public void getAttribute_whenKeyPortableObjectThenConvertedToData() {
        Data key = serializationService.toData(new SampleTestObjects.PortableEmployee(30, "peter"));
        QueryEntryTest.SerializableObject value = new QueryEntryTest.SerializableObject();
        QueryableEntry queryEntry = createEntry(key, value, newExtractor());
        Object result = queryEntry.getAttributeValue(((KEY_ATTRIBUTE_NAME.value()) + ".n"));
        Assert.assertEquals("peter", result);
    }

    @Test
    public void getAttribute_whenValueInObjectFormatThenNoSerialization() {
        Data key = serializationService.toData(new QueryEntryTest.SerializableObject());
        QueryEntryTest.SerializableObject value = new QueryEntryTest.SerializableObject();
        value.name = "somename";
        QueryableEntry queryEntry = createEntry(key, value, newExtractor());
        Object result = queryEntry.getAttributeValue("name");
        Assert.assertEquals("somename", result);
        Assert.assertEquals(0, value.deserializationCount);
        Assert.assertEquals(0, value.serializationCount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInit_whenKeyIsNull_thenThrowIllegalArgumentException() {
        createEntry(null, new QueryEntryTest.SerializableObject(), newExtractor());
    }

    @Test
    public void test_init() {
        Data dataKey = serializationService.toData("dataKey");
        Data dataValue = serializationService.toData("dataValue");
        QueryableEntry queryEntry = createEntry(dataKey, dataValue, newExtractor());
        Object objectValue = queryEntry.getValue();
        Object objectKey = queryEntry.getKey();
        initEntry(queryEntry, serializationService, serializationService.toData(objectKey), objectValue, newExtractor());
        // compare references of objects since they should be cloned after QueryEntry#init call.
        Assert.assertTrue("Old dataKey should not be here", (dataKey != (queryEntry.getKeyData())));
        Assert.assertTrue("Old dataValue should not be here", (dataValue != (queryEntry.getValueData())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_init_nullKey() {
        createEntry(null, "value", newExtractor());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test_setValue() {
        QueryableEntry queryEntry = createEntry(Mockito.mock(Data.class), "value", newExtractor());
        queryEntry.setValue("anyValue");
    }

    @Test(expected = NullPointerException.class)
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void test_equality_empty() {
        QueryableEntry entryKeyLeft = createEntry();
        QueryableEntry entryKeyRight = createEntry();
        entryKeyLeft.equals(entryKeyRight);
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    public void test_equality_same() {
        QueryableEntry entry = createEntry();
        Assert.assertTrue(entry.equals(entry));
    }

    @Test
    public void test_equality_differentType() {
        QueryableEntry entry = createEntry();
        Assert.assertFalse(entry.equals("string"));
    }

    @Test
    @SuppressWarnings("ObjectEqualsNull")
    public void test_equality_null() {
        QueryableEntry entry = createEntry();
        Assert.assertFalse(entry.equals(null));
    }

    @Test
    public void test_equality_differentKey() {
        QueryableEntry queryEntry = createEntry("dataKey", "dataValue");
        QueryableEntry queryEntryOther = createEntry("dataKeyOther", "dataValue");
        Assert.assertFalse(queryEntry.equals(queryEntryOther));
    }

    @Test
    public void test_equality_sameKey() {
        QueryableEntry queryEntry = createEntry("dataKey", "dataValue");
        QueryableEntry queryEntryOther = createEntry("dataKey", "dataValueOther");
        Assert.assertTrue(queryEntry.equals(queryEntryOther));
    }

    @Test
    public void getKey_caching() {
        QueryableEntry entry = createEntry("key", "value");
        MatcherAssert.assertThat(entry.getKey(), IsNot.not(IsSame.sameInstance(entry.getKey())));
    }

    @Test
    public void getValue_caching() {
        QueryableEntry entry = createEntry("key", "value");
        MatcherAssert.assertThat(entry.getValue(), IsNot.not(IsSame.sameInstance(entry.getValue())));
    }

    @Test
    public void getKeyData_caching() {
        QueryableEntry entry = createEntry("key", "value");
        MatcherAssert.assertThat(entry.getKeyData(), IsSame.sameInstance(entry.getKeyData()));
    }

    @Test
    public void getValueData_caching() {
        QueryableEntry entry = createEntry("key", "value");
        MatcherAssert.assertThat(entry.getValueData(), IsSame.sameInstance(entry.getValueData()));
    }

    @SuppressWarnings("unused")
    private static class SerializableObject implements DataSerializable {
        private int serializationCount;

        private int deserializationCount;

        private String name;

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            (serializationCount)++;
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            (deserializationCount)++;
        }
    }
}

