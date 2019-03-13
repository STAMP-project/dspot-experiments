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


import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.query.SampleTestObjects;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CachedQueryEntryTest extends QueryEntryTest {
    @Test
    public void getKey_caching() {
        QueryableEntry entry = createEntry("key", "value");
        Assert.assertSame(entry.getKey(), entry.getKey());
    }

    @Test
    public void getValue_caching() {
        QueryableEntry entry = createEntry("key", "value");
        Assert.assertSame(entry.getValue(), entry.getValue());
    }

    @Test
    public void getKeyData_caching() {
        QueryableEntry entry = createEntry("key", "value");
        Assert.assertSame(entry.getKeyData(), entry.getKeyData());
    }

    @Test
    public void getValueData_caching() {
        QueryableEntry entry = createEntry("key", "value");
        Assert.assertSame(entry.getValueData(), entry.getValueData());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInit_whenKeyIsNull_thenThrowIllegalArgumentException() {
        createEntry(null, new Object(), newExtractor());
    }

    @Test
    public void testGetKey() {
        String keyObject = "key";
        Data keyData = serializationService.toData(keyObject);
        QueryableEntry entry = createEntry(keyData, new Object(), newExtractor());
        Object key = entry.getKey();
        Assert.assertEquals(keyObject, key);
    }

    @Test
    public void testGetTargetObject_givenKeyDataIsPortable_whenKeyFlagIsTrue_thenReturnKeyData() {
        Data keyData = mockPortableData();
        QueryableEntry entry = createEntry(keyData, new Object(), newExtractor());
        Object targetObject = entry.getTargetObject(true);
        Assert.assertSame(keyData, targetObject);
    }

    @Test
    public void testGetTargetObject_givenKeyDataIsNotPortable_whenKeyFlagIsTrue_thenReturnKeyObject() {
        Object keyObject = "key";
        Data keyData = serializationService.toData(keyObject);
        QueryableEntry entry = createEntry(keyData, new Object(), newExtractor());
        Object targetObject = entry.getTargetObject(true);
        Assert.assertEquals(keyObject, targetObject);
    }

    @Test
    public void testGetTargetObject_givenValueIsDataAndPortable_whenKeyFlagIsFalse_thenReturnValueData() {
        Data key = serializationService.toData("indexedKey");
        Data value = serializationService.toData(new SampleTestObjects.PortableEmployee(30, "peter"));
        QueryableEntry entry = createEntry(key, value, newExtractor());
        Object targetObject = entry.getTargetObject(false);
        Assert.assertEquals(value, targetObject);
    }

    @Test
    public void testGetTargetObject_givenValueIsData_whenKeyFlagIsFalse_thenReturnValueObject() {
        Data key = serializationService.toData("key");
        Data value = serializationService.toData("value");
        QueryableEntry entry = createEntry(key, value, newExtractor());
        Object targetObject = entry.getTargetObject(false);
        Assert.assertEquals("value", targetObject);
    }

    @Test
    public void testGetTargetObject_givenValueIsPortable_whenKeyFlagIsFalse_thenReturnValueData() {
        Data key = serializationService.toData("indexedKey");
        Portable value = new SampleTestObjects.PortableEmployee(30, "peter");
        QueryableEntry entry = createEntry(key, value, newExtractor());
        Object targetObject = entry.getTargetObject(false);
        Assert.assertEquals(serializationService.toData(value), targetObject);
    }

    @Test
    public void testGetTargetObject_givenValueIsNotPortable_whenKeyFlagIsFalse_thenReturnValueObject() {
        Data key = serializationService.toData("key");
        String value = "value";
        QueryableEntry entry = createEntry(key, value, newExtractor());
        Object targetObject = entry.getTargetObject(false);
        Assert.assertSame(value, targetObject);
    }

    @Test(expected = NullPointerException.class)
    public void testGetTargetObject_givenInstanceIsNotInitialized_whenKeyFlagIsFalse_thenThrowNPE() {
        QueryableEntry entry = createEntry();
        entry.getTargetObject(false);
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    public void testEquals_givenSameInstance_thenReturnTrue() {
        CachedQueryEntry entry1 = createEntry("key");
        Assert.assertTrue(entry1.equals(entry1));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testEquals_givenOtherIsNull_thenReturnFalse() {
        CachedQueryEntry entry1 = createEntry("key");
        CachedQueryEntry entry2 = null;
        Assert.assertFalse(entry1.equals(entry2));
    }

    @Test
    public void testEquals_givenOtherIsDifferentClass_thenReturnFalse() {
        CachedQueryEntry entry1 = createEntry("key");
        Object entry2 = new Object();
        Assert.assertFalse(entry1.equals(entry2));
    }

    @Test
    public void testEquals_givenOtherHasDifferentKey_thenReturnFalse() {
        CachedQueryEntry entry1 = createEntry("key1");
        CachedQueryEntry entry2 = createEntry("key2");
        Assert.assertFalse(entry1.equals(entry2));
    }

    @Test
    public void testEquals_givenOtherHasEqualKey_thenReturnTrue() {
        CachedQueryEntry entry1 = createEntry("key");
        CachedQueryEntry entry2 = createEntry("key");
        Assert.assertTrue(entry1.equals(entry2));
    }

    @Test
    public void testHashCode() {
        CachedQueryEntry entry = createEntry("key");
        Assert.assertEquals(entry.hashCode(), entry.hashCode());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void givenNewEntry_whenSetValue_thenThrowUnsupportedOperationException() {
        CachedQueryEntry<Object, Object> entry = createEntry("key");
        entry.setValue(new Object());
    }
}

