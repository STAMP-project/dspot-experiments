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
import Object2IntHashMap.MIN_CAPACITY;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class Object2IntHashMapTest {
    static final int MISSING_VALUE = -1;

    final Object2IntHashMap<String> objectToIntMap;

    public Object2IntHashMapTest() {
        objectToIntMap = newMap(DEFAULT_LOAD_FACTOR, MIN_CAPACITY);
    }

    @Test
    public void shouldDoPutAndThenGet() {
        final String key = "Seven";
        objectToIntMap.put(key, 7);
        Assert.assertThat(objectToIntMap.get(key), Is.is(7));
    }

    @Test
    public void shouldReplaceExistingValueForTheSameKey() {
        final int value = 7;
        final String key = "Seven";
        objectToIntMap.put(key, value);
        final int newValue = 8;
        final int oldValue = objectToIntMap.put(key, newValue);
        Assert.assertThat(objectToIntMap.get(key), Is.is(newValue));
        Assert.assertThat(oldValue, Is.is(value));
        Assert.assertThat(objectToIntMap.size(), Is.is(1));
    }

    @Test
    public void shouldGrowWhenThresholdExceeded() {
        final float loadFactor = 0.5F;
        final int initialCapacity = 32;
        final Object2IntHashMap<String> map = newMap(loadFactor, initialCapacity);
        for (int i = 0; i < 16; i++) {
            map.put(Integer.toString(i), i);
        }
        Assert.assertThat(map.resizeThreshold(), Is.is(16));
        Assert.assertThat(map.capacity(), Is.is(initialCapacity));
        Assert.assertThat(map.size(), Is.is(16));
        map.put("16", 16);
        Assert.assertThat(map.resizeThreshold(), Is.is(initialCapacity));
        Assert.assertThat(map.capacity(), Is.is(64));
        Assert.assertThat(map.size(), Is.is(17));
        Assert.assertThat(map.getValue("16"), IsEqual.equalTo(16));
        Assert.assertThat(((double) (loadFactor)), Matchers.closeTo(map.loadFactor(), 0.0F));
    }

    @Test
    public void shouldHandleCollisionAndThenLinearProbe() {
        final float loadFactor = 0.5F;
        final int initialCapacity = 32;
        final Object2IntHashMap<Integer> map = newMap(loadFactor, initialCapacity);
        final int value = 7;
        final Integer key = 7;
        map.put(key, value);
        final Integer collisionKey = key + (map.capacity());
        final int collisionValue = collisionKey;
        map.put(collisionKey, collisionValue);
        Assert.assertThat(map.get(key), Is.is(value));
        Assert.assertThat(map.get(collisionKey), Is.is(collisionValue));
        Assert.assertThat(((double) (loadFactor)), Matchers.closeTo(map.loadFactor(), 0.0F));
    }

    @Test
    public void shouldClearCollection() {
        for (int i = 0; i < 15; i++) {
            objectToIntMap.put(Integer.toString(i), i);
        }
        Assert.assertThat(objectToIntMap.size(), Is.is(15));
        Assert.assertThat(objectToIntMap.getValue(Integer.toString(1)), Is.is(1));
        objectToIntMap.clear();
        Assert.assertThat(objectToIntMap.size(), Is.is(0));
        Assert.assertEquals(Object2IntHashMapTest.MISSING_VALUE, objectToIntMap.getValue("1"));
    }

    @Test
    public void shouldCompactCollection() {
        final int totalItems = 50;
        for (int i = 0; i < totalItems; i++) {
            objectToIntMap.put(Integer.toString(i), i);
        }
        for (int i = 0, limit = totalItems - 4; i < limit; i++) {
            objectToIntMap.remove(Integer.toString(i));
        }
        final int capacityBeforeCompaction = objectToIntMap.capacity();
        objectToIntMap.compact();
        Assert.assertThat(objectToIntMap.capacity(), lessThan(capacityBeforeCompaction));
    }

    @Test
    public void shouldContainValue() {
        final int value = 7;
        final String key = "Seven";
        objectToIntMap.put(key, value);
        Assert.assertTrue(objectToIntMap.containsValue(value));
        Assert.assertFalse(objectToIntMap.containsValue(8));
    }

    @Test
    public void shouldContainKey() {
        final int value = 7;
        final String key = "Seven";
        objectToIntMap.put(key, value);
        Assert.assertTrue(objectToIntMap.containsKey(key));
        Assert.assertFalse(objectToIntMap.containsKey("Eight"));
    }

    @Test
    public void shouldRemoveEntry() {
        final int value = 7;
        final String key = "Seven";
        objectToIntMap.put(key, value);
        Assert.assertTrue(objectToIntMap.containsKey(key));
        objectToIntMap.remove(key);
        Assert.assertFalse(objectToIntMap.containsKey(key));
    }

    @Test
    public void shouldRemoveEntryAndCompactCollisionChain() {
        final float loadFactor = 0.5F;
        final Object2IntHashMap<Integer> objectToIntMap = new Object2IntHashMap(32, loadFactor, Object2IntHashMapTest.MISSING_VALUE);
        final int value = 12;
        final Integer key = 12;
        objectToIntMap.put(key, value);
        objectToIntMap.put(Integer.valueOf(13), 13);
        final int collisionKey = key + (objectToIntMap.capacity());
        final int collisionValue = collisionKey;
        objectToIntMap.put(Integer.valueOf(collisionKey), collisionValue);
        objectToIntMap.put(Integer.valueOf(14), 14);
        Assert.assertThat(objectToIntMap.remove(key), Is.is(value));
    }

    @Test
    public void shouldIterateValuesGettingIntAsPrimitive() {
        final Collection<Integer> initialSet = new HashSet<>();
        for (int i = 0; i < 11; i++) {
            final String key = Integer.toString(i);
            objectToIntMap.put(key, i);
            initialSet.add(i);
        }
        final Collection<Integer> copyToSet = new HashSet<>();
        for (final Object2IntHashMap<String>.ValueIterator iter = objectToIntMap.values().iterator(); iter.hasNext();) {
            copyToSet.add(iter.nextInt());
        }
        Assert.assertThat(copyToSet, Is.is(initialSet));
    }

    @Test
    public void shouldIterateValues() {
        final Collection<Integer> initialSet = new HashSet<>();
        for (int i = 0; i < 11; i++) {
            final String key = Integer.toString(i);
            objectToIntMap.put(key, i);
            initialSet.add(i);
        }
        final Collection<Integer> copyToSet = new HashSet<>();
        for (final Integer key : objectToIntMap.values()) {
            // noinspection UseBulkOperation
            copyToSet.add(key);
        }
        Assert.assertThat(copyToSet, Is.is(initialSet));
    }

    @Test
    public void shouldIterateKeys() {
        final Collection<String> initialSet = new HashSet<>();
        for (int i = 0; i < 11; i++) {
            final String key = Integer.toString(i);
            objectToIntMap.put(key, i);
            initialSet.add(key);
        }
        assertIterateKeys(initialSet);
        assertIterateKeys(initialSet);
        assertIterateKeys(initialSet);
    }

    @Test
    public void shouldIterateAndHandleRemove() {
        final Collection<String> initialSet = new HashSet<>();
        final int count = 11;
        for (int i = 0; i < count; i++) {
            final String key = Integer.toString(i);
            objectToIntMap.put(key, i);
            initialSet.add(key);
        }
        final Collection<String> copyOfSet = new HashSet<>();
        int i = 0;
        for (final Iterator<String> iter = objectToIntMap.keySet().iterator(); iter.hasNext();) {
            final String item = iter.next();
            if ((i++) == 7) {
                iter.remove();
            } else {
                copyOfSet.add(item);
            }
        }
        final int reducedSetSize = count - 1;
        Assert.assertThat(initialSet.size(), Is.is(count));
        Assert.assertThat(objectToIntMap.size(), Is.is(reducedSetSize));
        Assert.assertThat(copyOfSet.size(), Is.is(reducedSetSize));
    }

    @Test
    public void shouldIterateEntries() {
        final int count = 11;
        for (int i = 0; i < count; i++) {
            final String key = Integer.toString(i);
            objectToIntMap.put(key, i);
        }
        iterateEntries();
        iterateEntries();
        iterateEntries();
        final Integer testValue = 100;
        for (final Map.Entry<String, Integer> entry : objectToIntMap.entrySet()) {
            Assert.assertThat(entry.getKey(), IsEqual.equalTo(String.valueOf(entry.getValue())));
            if (entry.getKey().equals("7")) {
                entry.setValue(testValue);
            }
        }
        Assert.assertThat(objectToIntMap.getValue("7"), IsEqual.equalTo(testValue));
    }

    private static class ControlledHash {
        private final int value;

        public static Object2IntHashMapTest.ControlledHash[] create(final int... values) {
            final Object2IntHashMapTest.ControlledHash[] result = new Object2IntHashMapTest.ControlledHash[values.length];
            for (int i = 0; i < (values.length); i++) {
                result[i] = new Object2IntHashMapTest.ControlledHash(values[i]);
            }
            return result;
        }

        ControlledHash(final int value) {
            super();
            this.value = value;
        }

        public String toString() {
            return Integer.toString(value);
        }

        public int hashCode() {
            return (value) * 31;
        }

        public boolean equals(final Object obj) {
            if ((this) == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if ((getClass()) != (obj.getClass())) {
                return false;
            }
            final Object2IntHashMapTest.ControlledHash other = ((Object2IntHashMapTest.ControlledHash) (obj));
            return (value) == (other.value);
        }
    }

    @Test
    public void shouldGenerateStringRepresentation() {
        final Object2IntHashMap<Object2IntHashMapTest.ControlledHash> objectToIntMap = new Object2IntHashMap(Object2IntHashMapTest.MISSING_VALUE);
        final Object2IntHashMapTest.ControlledHash[] testEntries = Object2IntHashMapTest.ControlledHash.create(3, 1, 19, 7, 11, 12, 7);
        for (final Object2IntHashMapTest.ControlledHash testEntry : testEntries) {
            objectToIntMap.put(testEntry, testEntry.value);
        }
        final String mapAsAString = "{1=1, 19=19, 3=3, 7=7, 11=11, 12=12}";
        Assert.assertThat(objectToIntMap.toString(), IsEqual.equalTo(mapAsAString));
    }

    @Test
    public void shouldCopyConstructAndBeEqual() {
        final int[] testEntries = new int[]{ 3, 1, 19, 7, 11, 12, 7 };
        for (final int testEntry : testEntries) {
            objectToIntMap.put(String.valueOf(testEntry), testEntry);
        }
        final Object2IntHashMap<String> mapCopy = new Object2IntHashMap(objectToIntMap);
        Assert.assertThat(mapCopy, Is.is(objectToIntMap));
    }
}

