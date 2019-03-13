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
package com.hazelcast.util;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ItemCounterTest extends HazelcastTestSupport {
    private ItemCounter<Object> counter;

    @Test
    public void testKeySet() {
        counter.add("key1", 1);
        counter.add("key2", 1);
        Assert.assertEquals(new HashSet<String>(Arrays.asList("key1", "key2")), counter.keySet());
    }

    @Test
    public void testDescendingKeys() {
        counter.add("key1", 2);
        counter.add("key2", 3);
        counter.add("key3", 1);
        Assert.assertEquals(Arrays.asList("key2", "key1", "key3"), counter.descendingKeys());
    }

    @Test
    public void testGet_returnsZeroWhenEmpty() {
        long count = counter.get(new Object());
        Assert.assertEquals(0, count);
    }

    @Test
    public void testGet_returnsPreviouslySetValue() {
        long value = Long.MAX_VALUE;
        Object object = new Object();
        counter.set(object, value);
        long count = counter.get(object);
        Assert.assertEquals(value, count);
    }

    @Test
    public void testSet_overridePreviousValue() {
        long value = Long.MAX_VALUE;
        Object object = new Object();
        counter.set(object, Long.MIN_VALUE);
        long oldTotal = counter.total();
        counter.set(object, value);
        long count = counter.get(object);
        Assert.assertEquals(value, count);
        Assert.assertEquals(Long.MAX_VALUE, counter.total());
    }

    @Test
    public void testAdd_whenNoPreviousValueExist() {
        Object object = new Object();
        long delta = 1;
        counter.add(object, delta);
        long count = counter.get(object);
        Assert.assertEquals(delta, count);
        Assert.assertEquals(1, counter.total());
    }

    @Test
    public void testAdd_increaseWhenPreviousValueDoesExist() {
        Object object = new Object();
        long initialValue = 1;
        long delta = 1;
        counter.set(object, initialValue);
        counter.add(object, delta);
        long count = counter.get(object);
        Assert.assertEquals((delta + initialValue), count);
        Assert.assertEquals(2, counter.total());
    }

    @Test
    public void testReset_allValuesAreSetToZeroOnReset() {
        Object object1 = new Object();
        Object object2 = new Object();
        long initialValue1 = Long.MAX_VALUE;
        long initialValue2 = Long.MIN_VALUE;
        counter.set(object1, initialValue1);
        counter.set(object2, initialValue2);
        counter.reset();
        long count1 = counter.get(object1);
        long count2 = counter.get(object1);
        Assert.assertEquals(0, count1);
        Assert.assertEquals(0, count2);
        Assert.assertEquals(0, counter.total());
    }

    @Test
    public void testGetAndSet_asSetWhenNoPreviousValueExist() {
        Object object = new Object();
        long newValue = Long.MAX_VALUE;
        long count = counter.getAndSet(object, newValue);
        Assert.assertEquals(count, 0);
        count = counter.get(object);
        Assert.assertEquals(newValue, count);
        Assert.assertEquals(newValue, counter.total());
    }

    @Test
    public void testGetAndSet_overridePreviousValue() {
        Object object = new Object();
        long initialValue = 10;
        long newValue = 20;
        counter.set(object, initialValue);
        long count = counter.getAndSet(object, newValue);
        Assert.assertEquals(count, initialValue);
        count = counter.get(object);
        Assert.assertEquals(newValue, count);
        Assert.assertEquals(count, counter.total());
    }

    @Test
    public void testEquals_returnsTrueOnSameInstance() {
        Assert.assertTrue(counter.equals(counter));
    }

    @Test
    public void testEquals_returnsFalseOnNull() {
        Assert.assertFalse(counter.equals(null));
    }

    @Test
    public void testEquals_returnsFalseDifferentClass() {
        Assert.assertFalse(counter.equals(new Object()));
    }

    @Test
    public void testEquals_returnsTrueOnTheSameData() {
        Object object1 = new Object();
        ItemCounter<Object> otherCounter = new ItemCounter<Object>();
        counter.set(object1, Long.MAX_VALUE);
        otherCounter.set(object1, Long.MAX_VALUE);
        Assert.assertTrue(counter.equals(otherCounter));
    }

    @Test
    public void testEquals_returnsFalseOnTheDifferentData() {
        Object object1 = new Object();
        ItemCounter<Object> otherCounter = new ItemCounter<Object>();
        counter.set(object1, Long.MAX_VALUE);
        otherCounter.set(object1, Long.MIN_VALUE);
        Assert.assertFalse(counter.equals(otherCounter));
    }

    @Test
    public void testHashCode_doesNotThrowExceptionWhenEmpty() {
        counter.hashCode();
    }

    @Test
    public void testHashCode_sameHashCodeOnTheSameData() {
        ItemCounter<Object> otherCounter = new ItemCounter<Object>();
        int hashCode = counter.hashCode();
        int otherHashCode = otherCounter.hashCode();
        Assert.assertEquals(hashCode, otherHashCode);
    }
}

