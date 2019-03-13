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
package com.hazelcast.cache.impl;


import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DeferredValueTest {
    private SerializationService serializationService;

    private String expected;

    private Data serializedValue;

    // for deferred value sets tests
    private Set<String> valueSet;

    private Set<DeferredValue<String>> deferredSet;

    // adaptedSet is backed by deferredSet
    private Set<String> adaptedSet;

    @Test
    public void testValue_isSame_whenConstructedWithValue() {
        DeferredValue<String> deferredValue = DeferredValue.withValue(expected);
        Assert.assertSame(expected, deferredValue.get(serializationService));
    }

    @Test
    public void testValue_whenConstructedWithSerializedValue() {
        DeferredValue<String> deferredValue = DeferredValue.withSerializedValue(serializedValue);
        Assert.assertEquals(expected, deferredValue.get(serializationService));
    }

    @Test
    public void testSerializedValue_isSame_whenConstructedWithSerializedValue() {
        DeferredValue<String> deferredValue = DeferredValue.withSerializedValue(serializedValue);
        Assert.assertSame(serializedValue, deferredValue.getSerializedValue(serializationService));
    }

    @Test
    public void testSerializedValue_whenConstructedWithValue() {
        DeferredValue<String> deferredValue = DeferredValue.withValue(expected);
        Assert.assertEquals(serializedValue, deferredValue.getSerializedValue(serializationService));
    }

    @Test
    public void testEquals_WithValue() {
        DeferredValue<String> v1 = DeferredValue.withValue(expected);
        DeferredValue<String> v2 = DeferredValue.withValue(expected);
        Assert.assertEquals(v1, v2);
    }

    @Test
    public void testEquals_WithSerializedValue() {
        DeferredValue<String> v1 = DeferredValue.withSerializedValue(serializedValue);
        DeferredValue<String> v2 = DeferredValue.withSerializedValue(serializedValue);
        Assert.assertEquals(v1, v2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEquals_WithValueAndSerializedValue() {
        DeferredValue<String> v1 = DeferredValue.withValue(expected);
        DeferredValue<String> v2 = DeferredValue.withSerializedValue(serializedValue);
        Assert.assertEquals(v1, v2);
    }

    @Test
    public void testNullValue_returnsNull() {
        DeferredValue deferredValue = DeferredValue.withNullValue();
        Assert.assertNull(deferredValue.getSerializedValue(serializationService));
        Assert.assertNull(deferredValue.get(serializationService));
    }

    @Test
    public void testCopy_whenNullValue() {
        DeferredValue nullValue = DeferredValue.withNullValue();
        DeferredValue copy = nullValue.shallowCopy();
        Assert.assertNull(copy.getSerializedValue(serializationService));
        Assert.assertNull(copy.get(serializationService));
    }

    @Test
    public void testCopy_whenSerializedValue() {
        DeferredValue<String> v1 = DeferredValue.withSerializedValue(serializedValue);
        DeferredValue<String> v2 = v1.shallowCopy();
        Assert.assertEquals(v1, v2);
    }

    @Test
    public void testCopy_whenValue() {
        DeferredValue<String> v1 = DeferredValue.withValue(expected);
        DeferredValue<String> v2 = v1.shallowCopy();
        Assert.assertEquals(v1, v2);
    }

    @Test
    public void test_setOfValues() {
        Assert.assertTrue(deferredSet.contains(DeferredValue.withValue("1")));
        Assert.assertTrue(deferredSet.contains(DeferredValue.withValue("2")));
        Assert.assertTrue(deferredSet.contains(DeferredValue.withValue("3")));
        Assert.assertFalse(deferredSet.contains(DeferredValue.withValue("4")));
    }

    @Test
    public void test_adaptedSet() {
        Assert.assertTrue(adaptedSet.containsAll(valueSet));
        Assert.assertFalse(adaptedSet.isEmpty());
        Assert.assertEquals(3, adaptedSet.size());
        // add "4" -> "1", "2", "3", "4"
        adaptedSet.add("4");
        Assert.assertTrue(deferredSet.contains(DeferredValue.withValue("4")));
        // remove "1" -> "2", "3", "4"
        adaptedSet.remove("1");
        Assert.assertFalse(deferredSet.contains(DeferredValue.withValue("1")));
        // retain just "2" & "3"
        adaptedSet.retainAll(valueSet);
        Assert.assertEquals(2, adaptedSet.size());
        List<String> currentContents = Arrays.asList("2", "3");
        Assert.assertTrue(adaptedSet.containsAll(currentContents));
        Assert.assertTrue(adaptedSet.contains("2"));
        // toArray
        Object[] valuesAsArray = adaptedSet.toArray();
        Assert.assertEquals(2, valuesAsArray.length);
        Assert.assertArrayEquals(new Object[]{ "2", "3" }, valuesAsArray);
        String[] stringArray = adaptedSet.toArray(new String[0]);
        Assert.assertEquals(2, stringArray.length);
        Assert.assertArrayEquals(new Object[]{ "2", "3" }, stringArray);
        // iterator
        Iterator<String> iterator = adaptedSet.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("2", iterator.next());
        // iterator.remove -> just "3" left in set
        iterator.remove();
        Assert.assertEquals(1, adaptedSet.size());
        Assert.assertEquals("3", deferredSet.iterator().next().get(serializationService));
        // removeAll
        adaptedSet.removeAll(valueSet);
        Assert.assertTrue(adaptedSet.isEmpty());
        // addAll
        adaptedSet.addAll(valueSet);
        Assert.assertEquals(3, adaptedSet.size());
        Assert.assertTrue(adaptedSet.containsAll(valueSet));
        // clear
        adaptedSet.clear();
        Assert.assertTrue(adaptedSet.isEmpty());
        Assert.assertTrue(deferredSet.isEmpty());
    }
}

