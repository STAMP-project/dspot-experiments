/**
 * Original work Copyright 2015 Real Logic Ltd.
 * Modified work Copyright (c) 2015-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.util.collection;


import Long2ObjectHashMap.KeyIterator;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class Long2ObjectHashMapTest {
    private final Long2ObjectHashMap<String> longToObjectMap = new Long2ObjectHashMap<String>();

    @Test
    public void shouldDoPutAndThenGet() {
        final String value = "Seven";
        longToObjectMap.put(7, value);
        Assert.assertThat(longToObjectMap.get(7), Is.is(value));
    }

    @Test
    public void shouldReplaceExistingValueForTheSameKey() {
        final long key = 7;
        final String value = "Seven";
        longToObjectMap.put(key, value);
        final String newValue = "New Seven";
        final String oldValue = longToObjectMap.put(key, newValue);
        Assert.assertThat(longToObjectMap.get(key), Is.is(newValue));
        Assert.assertThat(oldValue, Is.is(value));
        Assert.assertThat(Long.valueOf(longToObjectMap.size()), Is.is(Long.valueOf(1)));
    }

    @Test
    public void shouldGrowWhenThresholdExceeded() {
        final double loadFactor = 0.5;
        final Long2ObjectHashMap<String> map = new Long2ObjectHashMap<String>(32, loadFactor);
        for (int i = 0; i < 16; i++) {
            map.put(i, Long.toString(i));
        }
        Assert.assertThat(Long.valueOf(map.resizeThreshold()), Is.is(Long.valueOf(16)));
        Assert.assertThat(Long.valueOf(map.capacity()), Is.is(Long.valueOf(32)));
        Assert.assertThat(Long.valueOf(map.size()), Is.is(Long.valueOf(16)));
        map.put(16, "16");
        Assert.assertThat(Long.valueOf(map.resizeThreshold()), Is.is(Long.valueOf(32)));
        Assert.assertThat(Long.valueOf(map.capacity()), Is.is(Long.valueOf(64)));
        Assert.assertThat(Long.valueOf(map.size()), Is.is(Long.valueOf(17)));
        Assert.assertThat(map.get(16), IsEqual.equalTo("16"));
        Assert.assertThat(loadFactor, closeTo(map.loadFactor(), 0.0));
    }

    @Test
    public void shouldHandleCollisionAndThenLinearProbe() {
        final double loadFactor = 0.5;
        final Long2ObjectHashMap<String> map = new Long2ObjectHashMap<String>(32, loadFactor);
        final long key = 7;
        final String value = "Seven";
        map.put(key, value);
        final long collisionKey = key + (map.capacity());
        final String collisionValue = Long.toString(collisionKey);
        map.put(collisionKey, collisionValue);
        Assert.assertThat(map.get(key), Is.is(value));
        Assert.assertThat(map.get(collisionKey), Is.is(collisionValue));
        Assert.assertThat(loadFactor, closeTo(map.loadFactor(), 0.0));
    }

    @Test
    public void shouldClearCollection() {
        for (int i = 0; i < 15; i++) {
            longToObjectMap.put(i, Long.toString(i));
        }
        Assert.assertThat(Long.valueOf(longToObjectMap.size()), Is.is(Long.valueOf(15)));
        Assert.assertThat(longToObjectMap.get(1), Is.is("1"));
        longToObjectMap.clear();
        Assert.assertThat(Long.valueOf(longToObjectMap.size()), Is.is(Long.valueOf(0)));
        Assert.assertNull(longToObjectMap.get(1));
    }

    @Test
    public void shouldCompactCollection() {
        final int totalItems = 50;
        for (int i = 0; i < totalItems; i++) {
            longToObjectMap.put(i, Long.toString(i));
        }
        for (int i = 0, limit = totalItems - 4; i < limit; i++) {
            longToObjectMap.remove(i);
        }
        final int capacityBeforeCompaction = longToObjectMap.capacity();
        longToObjectMap.compact();
        Assert.assertThat(Long.valueOf(longToObjectMap.capacity()), lessThan(Long.valueOf(capacityBeforeCompaction)));
    }

    @Test
    public void shouldContainValue() {
        final long key = 7;
        final String value = "Seven";
        longToObjectMap.put(key, value);
        Assert.assertTrue(longToObjectMap.containsValue(value));
        Assert.assertFalse(longToObjectMap.containsValue("NoKey"));
    }

    @Test
    public void shouldContainKey() {
        final long key = 7;
        final String value = "Seven";
        longToObjectMap.put(key, value);
        Assert.assertTrue(longToObjectMap.containsKey(key));
        Assert.assertFalse(longToObjectMap.containsKey(0));
    }

    @Test
    public void shouldRemoveEntry() {
        final long key = 7;
        final String value = "Seven";
        longToObjectMap.put(key, value);
        Assert.assertTrue(longToObjectMap.containsKey(key));
        longToObjectMap.remove(key);
        Assert.assertFalse(longToObjectMap.containsKey(key));
    }

    @Test
    public void shouldRemoveEntryAndCompactCollisionChain() {
        final long key = 12;
        final String value = "12";
        longToObjectMap.put(key, value);
        longToObjectMap.put(13, "13");
        final long collisionKey = key + (longToObjectMap.capacity());
        final String collisionValue = Long.toString(collisionKey);
        longToObjectMap.put(collisionKey, collisionValue);
        longToObjectMap.put(14, "14");
        Assert.assertThat(longToObjectMap.remove(key), Is.is(value));
    }

    @Test
    public void shouldIterateValues() {
        final Collection<String> initialSet = new HashSet<String>();
        for (int i = 0; i < 11; i++) {
            final String value = Long.toString(i);
            longToObjectMap.put(i, value);
            initialSet.add(value);
        }
        final Collection<String> copyToSet = new HashSet<String>(longToObjectMap.values());
        Assert.assertThat(copyToSet, Is.is(initialSet));
    }

    @Test
    public void shouldIterateKeysGettingLongAsPrimitive() {
        final Collection<Long> initialSet = new HashSet<Long>();
        for (int i = 0; i < 11; i++) {
            final String value = Long.toString(i);
            longToObjectMap.put(i, value);
            initialSet.add(Long.valueOf(i));
        }
        final Collection<Long> copyToSet = new HashSet<Long>();
        for (final Long2ObjectHashMap.KeyIterator iter = longToObjectMap.keySet().iterator(); iter.hasNext();) {
            copyToSet.add(Long.valueOf(iter.nextLong()));
        }
        Assert.assertThat(copyToSet, Is.is(initialSet));
    }

    @Test
    public void shouldIterateKeys() {
        final Collection<Long> initialSet = new HashSet<Long>();
        for (int i = 0; i < 11; i++) {
            final String value = Long.toString(i);
            longToObjectMap.put(i, value);
            initialSet.add(Long.valueOf(i));
        }
        final Collection<Long> copyToSet = new HashSet<Long>(longToObjectMap.keySet());
        Assert.assertThat(copyToSet, Is.is(initialSet));
    }

    @Test
    public void shouldIterateAndHandleRemove() {
        final Collection<Long> initialSet = new HashSet<Long>();
        final int count = 11;
        for (int i = 0; i < count; i++) {
            final String value = Long.toString(i);
            longToObjectMap.put(i, value);
            initialSet.add(Long.valueOf(i));
        }
        final Collection<Long> copyOfSet = new HashSet<Long>();
        int i = 0;
        for (final Iterator<Long> iter = longToObjectMap.keySet().iterator(); iter.hasNext();) {
            final Long item = iter.next();
            if ((i++) == 7) {
                iter.remove();
            } else {
                copyOfSet.add(item);
            }
        }
        Assert.assertThat(Long.valueOf(initialSet.size()), Is.is(Long.valueOf(count)));
        final int reducedSetSize = count - 1;
        Assert.assertThat(Long.valueOf(longToObjectMap.size()), Is.is(Long.valueOf(reducedSetSize)));
        Assert.assertThat(Long.valueOf(copyOfSet.size()), Is.is(Long.valueOf(reducedSetSize)));
    }

    @Test
    public void shouldIterateEntries() {
        final int count = 11;
        for (int i = 0; i < count; i++) {
            final String value = Long.toString(i);
            longToObjectMap.put(i, value);
        }
        final String testValue = "Wibble";
        for (final Map.Entry<Long, String> entry : longToObjectMap.entrySet()) {
            Assert.assertThat(entry.getKey(), IsEqual.equalTo(Long.valueOf(entry.getValue())));
            if ((entry.getKey().intValue()) == 7) {
                entry.setValue(testValue);
            }
        }
        Assert.assertThat(longToObjectMap.get(7), IsEqual.equalTo(testValue));
    }

    @Test
    public void shouldGenerateStringRepresentation() {
        final int[] testEntries = new int[]{ 3, 1, 19, 7, 11, 12, 7 };
        for (final int testEntry : testEntries) {
            longToObjectMap.put(testEntry, String.valueOf(testEntry));
        }
        final String mapAsAString = "{11=11, 7=7, 3=3, 12=12, 19=19, 1=1}";
        Assert.assertThat(longToObjectMap.toString(), IsEqual.equalTo(mapAsAString));
    }

    @Test
    public void sizeShouldReturnNumberOfEntries() {
        final int count = 100;
        for (int key = 0; key < count; key++) {
            longToObjectMap.put(key, "value");
        }
        Assert.assertEquals(count, longToObjectMap.size());
    }
}

