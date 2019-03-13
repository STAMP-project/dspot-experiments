/**
 * Copyright 2014-2019 Real Logic Ltd.
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
package org.agrona.collections;


import Hashing.DEFAULT_LOAD_FACTOR;
import Int2ObjectHashMap.KeyIterator;
import Int2ObjectHashMap.MIN_CAPACITY;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class Int2ObjectHashMapTest {
    final Int2ObjectHashMap<String> intToObjectMap;

    public Int2ObjectHashMapTest() {
        intToObjectMap = newMap(DEFAULT_LOAD_FACTOR, MIN_CAPACITY);
    }

    @Test
    public void shouldDoPutAndThenGet() {
        final String value = "Seven";
        intToObjectMap.put(7, value);
        Assert.assertThat(intToObjectMap.get(7), Is.is(value));
    }

    @Test
    public void shouldReplaceExistingValueForTheSameKey() {
        final int key = 7;
        final String value = "Seven";
        intToObjectMap.put(key, value);
        final String newValue = "New Seven";
        final String oldValue = intToObjectMap.put(key, newValue);
        Assert.assertThat(intToObjectMap.get(key), Is.is(newValue));
        Assert.assertThat(oldValue, Is.is(value));
        Assert.assertThat(intToObjectMap.size(), Is.is(1));
    }

    @Test
    public void shouldGrowWhenThresholdExceeded() {
        final float loadFactor = 0.5F;
        final int initialCapacity = 32;
        final Int2ObjectHashMap<String> map = newMap(loadFactor, initialCapacity);
        for (int i = 0; i < 16; i++) {
            map.put(i, Integer.toString(i));
        }
        Assert.assertThat(map.resizeThreshold(), Is.is(16));
        Assert.assertThat(map.capacity(), Is.is(initialCapacity));
        Assert.assertThat(map.size(), Is.is(16));
        map.put(16, "16");
        Assert.assertThat(map.resizeThreshold(), Is.is(initialCapacity));
        Assert.assertThat(map.capacity(), Is.is(64));
        Assert.assertThat(map.size(), Is.is(17));
        Assert.assertThat(map.get(16), IsEqual.equalTo("16"));
        Assert.assertThat(((double) (loadFactor)), Matchers.closeTo(map.loadFactor(), 0.0F));
    }

    @Test
    public void shouldHandleCollisionAndThenLinearProbe() {
        final float loadFactor = 0.5F;
        final int initialCapacity = 32;
        final Int2ObjectHashMap<String> map = newMap(loadFactor, initialCapacity);
        final int key = 7;
        final String value = "Seven";
        map.put(key, value);
        final int collisionKey = key + (map.capacity());
        final String collisionValue = Integer.toString(collisionKey);
        map.put(collisionKey, collisionValue);
        Assert.assertThat(map.get(key), Is.is(value));
        Assert.assertThat(map.get(collisionKey), Is.is(collisionValue));
        Assert.assertThat(((double) (loadFactor)), Matchers.closeTo(map.loadFactor(), 0.0F));
    }

    @Test
    public void shouldClearCollection() {
        for (int i = 0; i < 15; i++) {
            intToObjectMap.put(i, Integer.toString(i));
        }
        Assert.assertThat(intToObjectMap.size(), Is.is(15));
        Assert.assertThat(intToObjectMap.get(1), Is.is("1"));
        intToObjectMap.clear();
        Assert.assertThat(intToObjectMap.size(), Is.is(0));
        Assert.assertNull(intToObjectMap.get(1));
    }

    @Test
    public void shouldCompactCollection() {
        final int totalItems = 50;
        for (int i = 0; i < totalItems; i++) {
            intToObjectMap.put(i, Integer.toString(i));
        }
        for (int i = 0, limit = totalItems - 4; i < limit; i++) {
            intToObjectMap.remove(i);
        }
        final int capacityBeforeCompaction = intToObjectMap.capacity();
        intToObjectMap.compact();
        Assert.assertThat(intToObjectMap.capacity(), lessThan(capacityBeforeCompaction));
    }

    @Test
    public void shouldContainValue() {
        final int key = 7;
        final String value = "Seven";
        intToObjectMap.put(key, value);
        Assert.assertTrue(intToObjectMap.containsValue(value));
        Assert.assertFalse(intToObjectMap.containsValue("NoKey"));
    }

    @Test
    public void shouldContainKey() {
        final int key = 7;
        final String value = "Seven";
        intToObjectMap.put(key, value);
        Assert.assertTrue(intToObjectMap.containsKey(key));
        Assert.assertFalse(intToObjectMap.containsKey(0));
    }

    @Test
    public void shouldRemoveEntry() {
        final int key = 7;
        final String value = "Seven";
        intToObjectMap.put(key, value);
        Assert.assertTrue(intToObjectMap.containsKey(key));
        intToObjectMap.remove(key);
        Assert.assertFalse(intToObjectMap.containsKey(key));
    }

    @Test
    public void shouldRemoveEntryAndCompactCollisionChain() {
        final int key = 12;
        final String value = "12";
        intToObjectMap.put(key, value);
        intToObjectMap.put(13, "13");
        final int collisionKey = key + (intToObjectMap.capacity());
        final String collisionValue = Integer.toString(collisionKey);
        intToObjectMap.put(collisionKey, collisionValue);
        intToObjectMap.put(14, "14");
        Assert.assertThat(intToObjectMap.remove(key), Is.is(value));
    }

    @Test
    public void shouldIterateValues() {
        final Collection<String> initialSet = new HashSet<>();
        for (int i = 0; i < 11; i++) {
            final String value = Integer.toString(i);
            intToObjectMap.put(i, value);
            initialSet.add(value);
        }
        final Collection<String> copyToSet = new HashSet<>();
        for (final String s : intToObjectMap.values()) {
            // noinspection UseBulkOperation
            copyToSet.add(s);
        }
        Assert.assertThat(copyToSet, Is.is(initialSet));
    }

    @Test
    public void shouldIterateKeysGettingIntAsPrimitive() {
        final Collection<Integer> initialSet = new HashSet<>();
        for (int i = 0; i < 11; i++) {
            final String value = Integer.toString(i);
            intToObjectMap.put(i, value);
            initialSet.add(i);
        }
        final Collection<Integer> copyToSet = new HashSet<>();
        for (final Int2ObjectHashMap.KeyIterator iter = intToObjectMap.keySet().iterator(); iter.hasNext();) {
            copyToSet.add(iter.nextInt());
        }
        Assert.assertThat(copyToSet, Is.is(initialSet));
    }

    @Test
    public void shouldIterateKeys() {
        final Collection<Integer> initialSet = new HashSet<>();
        for (int i = 0; i < 11; i++) {
            final String value = Integer.toString(i);
            intToObjectMap.put(i, value);
            initialSet.add(i);
        }
        assertIterateKeys(initialSet);
        assertIterateKeys(initialSet);
        assertIterateKeys(initialSet);
    }

    @Test
    public void shouldIterateAndHandleRemove() {
        final Collection<Integer> initialSet = new HashSet<>();
        final int count = 11;
        for (int i = 0; i < count; i++) {
            final String value = Integer.toString(i);
            intToObjectMap.put(i, value);
            initialSet.add(i);
        }
        final Collection<Integer> copyOfSet = new HashSet<>();
        int i = 0;
        for (final Iterator<Integer> iter = intToObjectMap.keySet().iterator(); iter.hasNext();) {
            final Integer item = iter.next();
            if ((i++) == 7) {
                iter.remove();
            } else {
                copyOfSet.add(item);
            }
        }
        final int reducedSetSize = count - 1;
        Assert.assertThat(initialSet.size(), Is.is(count));
        Assert.assertThat(intToObjectMap.size(), Is.is(reducedSetSize));
        Assert.assertThat(copyOfSet.size(), Is.is(reducedSetSize));
    }

    @Test
    public void shouldIterateEntries() {
        final int count = 11;
        for (int i = 0; i < count; i++) {
            final String value = Integer.toString(i);
            intToObjectMap.put(i, value);
        }
        iterateEntries();
        iterateEntries();
        iterateEntries();
        final String testValue = "Wibble";
        for (final Map.Entry<Integer, String> entry : intToObjectMap.entrySet()) {
            Assert.assertThat(String.valueOf(entry.getKey()), IsEqual.equalTo(entry.getValue()));
            if ((entry.getKey()) == 7) {
                entry.setValue(testValue);
            }
        }
        Assert.assertThat(intToObjectMap.get(7), IsEqual.equalTo(testValue));
    }

    @Test
    public void shouldGenerateStringRepresentation() {
        final int[] testEntries = new int[]{ 3, 1, 19, 7, 11, 12, 7 };
        for (final int testEntry : testEntries) {
            intToObjectMap.put(testEntry, String.valueOf(testEntry));
        }
        final String mapAsAString = "{1=1, 19=19, 3=3, 7=7, 11=11, 12=12}";
        Assert.assertThat(intToObjectMap.toString(), IsEqual.equalTo(mapAsAString));
    }

    @Test
    public void shouldCopyConstructAndBeEqual() {
        final int[] testEntries = new int[]{ 3, 1, 19, 7, 11, 12, 7 };
        for (final int testEntry : testEntries) {
            intToObjectMap.put(testEntry, String.valueOf(testEntry));
        }
        final Int2ObjectHashMap<String> mapCopy = new Int2ObjectHashMap(intToObjectMap);
        Assert.assertThat(mapCopy, Is.is(intToObjectMap));
    }

    @Test
    public void shouldAllowNullValuesWithNullMapping() {
        final Int2ObjectHashMap<String> map = new Int2ObjectHashMap<String>() {
            private final Object nullRef = new Object();

            protected Object mapNullValue(final Object value) {
                return value == null ? nullRef : value;
            }

            protected String unmapNullValue(final Object value) {
                return value == (nullRef) ? null : ((String) (value));
            }
        };
        map.put(0, null);
        map.put(1, "one");
        Assert.assertThat(map.get(0), Matchers.nullValue());
        Assert.assertThat(map.get(1), Is.is("one"));
        Assert.assertThat(map.get((-1)), Matchers.nullValue());
        Assert.assertThat(map.containsKey(0), Is.is(true));
        Assert.assertThat(map.containsKey(1), Is.is(true));
        Assert.assertThat(map.containsKey((-1)), Is.is(false));
        Assert.assertThat(map.values(), Matchers.containsInAnyOrder(null, "one"));
        Assert.assertThat(map.keySet(), Matchers.containsInAnyOrder(0, 1));
        Assert.assertThat(map.size(), Is.is(2));
        map.remove(0);
        Assert.assertThat(map.get(0), Matchers.nullValue());
        Assert.assertThat(map.get(1), Is.is("one"));
        Assert.assertThat(map.get((-1)), Matchers.nullValue());
        Assert.assertThat(map.containsKey(0), Is.is(false));
        Assert.assertThat(map.containsKey(1), Is.is(true));
        Assert.assertThat(map.containsKey((-1)), Is.is(false));
        Assert.assertThat(map.size(), Is.is(1));
    }
}

