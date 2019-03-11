/**
 * Copyright 2014 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.collections.impl.map.mutable;


import com.gs.collections.api.map.MutableMap;
import com.gs.collections.impl.CollidingInt;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.test.Verify;
import java.util.Comparator;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UnifiedMapAcceptanceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnifiedMapAcceptanceTest.class);

    private static final Comparator<Map.Entry<CollidingInt, String>> ENTRY_COMPARATOR = ( o1, o2) -> o1.getKey().compareTo(o2.getKey());

    private static final Comparator<String> VALUE_COMPARATOR = ( o1, o2) -> (Integer.parseInt(o1.substring(1))) - (Integer.parseInt(o2.substring(1)));

    @Test
    public void forEachWithIndexWithChainedValues() {
        UnifiedMap<CollidingInt, String> map = UnifiedMap.newMap();
        int size = 100000;
        for (int i = 0; i < size; i++) {
            map.put(new CollidingInt(i, 3), UnifiedMapAcceptanceTest.createVal(i));
        }
        int[] intArray = new int[1];
        intArray[0] = -1;
        map.forEachWithIndex(( value, index) -> {
            Assert.assertEquals(index, ((intArray[0]) + 1));
            intArray[0] = index;
        });
    }

    @Test
    public void getMapMemoryUsedInWords() {
        UnifiedMap<String, String> map = UnifiedMap.newMap();
        Assert.assertEquals(34, map.getMapMemoryUsedInWords());
        map.put("1", "1");
        Assert.assertEquals(34, map.getMapMemoryUsedInWords());
    }

    @Test
    public void getCollidingBuckets() {
        UnifiedMap<Object, Object> map = UnifiedMap.newMap();
        Assert.assertEquals(0, map.getCollidingBuckets());
    }

    // todo: tests with null values
    // todo: keyset.removeAll(some collection where one of the keys is associated with null in the map) == true
    // todo: entryset.add(key associated with null) == true
    // todo: entryset.contains(entry with null value) == true
    @Test
    public void unifiedMapWithCollisions() {
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisions(0, 2);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisions(1, 2);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisions(2, 2);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisions(3, 2);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisions(4, 2);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisions(0, 4);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisions(1, 4);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisions(2, 4);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisions(3, 4);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisions(4, 4);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisions(0, 8);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisions(1, 8);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisions(2, 8);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisions(3, 8);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisions(4, 8);
    }

    @Test
    public void unifiedMapWithCollisionsAndNullKey() {
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisionsAndNullKey(0, 2);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisionsAndNullKey(1, 2);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisionsAndNullKey(2, 2);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisionsAndNullKey(3, 2);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisionsAndNullKey(4, 2);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisionsAndNullKey(0, 4);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisionsAndNullKey(1, 4);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisionsAndNullKey(2, 4);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisionsAndNullKey(3, 4);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisionsAndNullKey(4, 4);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisionsAndNullKey(0, 8);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisionsAndNullKey(1, 8);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisionsAndNullKey(2, 8);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisionsAndNullKey(3, 8);
        UnifiedMapAcceptanceTest.assertUnifiedMapWithCollisionsAndNullKey(4, 8);
    }

    @Test
    public void unifiedMap() {
        UnifiedMap<Integer, String> map = UnifiedMap.newMap();
        int size = 100000;
        for (int i = 0; i < size; i++) {
            map.put(i, UnifiedMapAcceptanceTest.createVal(i));
        }
        Verify.assertSize(size, map);
        for (int i = 0; i < size; i++) {
            Assert.assertTrue(map.containsKey(i));
            Assert.assertEquals(UnifiedMapAcceptanceTest.createVal(i), map.get(i));
        }
        for (int i = 0; i < size; i += 2) {
            Assert.assertEquals(UnifiedMapAcceptanceTest.createVal(i), map.remove(i));
        }
        Verify.assertSize((size / 2), map);
        for (int i = 1; i < size; i += 2) {
            Assert.assertTrue(map.containsKey(i));
            Assert.assertEquals(UnifiedMapAcceptanceTest.createVal(i), map.get(i));
        }
    }

    @Test
    public void unifiedMapClear() {
        UnifiedMapAcceptanceTest.assertUnifiedMapClear(0);
        UnifiedMapAcceptanceTest.assertUnifiedMapClear(1);
        UnifiedMapAcceptanceTest.assertUnifiedMapClear(2);
        UnifiedMapAcceptanceTest.assertUnifiedMapClear(3);
    }

    @Test
    public void unifiedMapForEachEntry() {
        UnifiedMapAcceptanceTest.assertUnifiedMapForEachEntry(0);
        UnifiedMapAcceptanceTest.assertUnifiedMapForEachEntry(1);
        UnifiedMapAcceptanceTest.assertUnifiedMapForEachEntry(2);
        UnifiedMapAcceptanceTest.assertUnifiedMapForEachEntry(3);
    }

    @Test
    public void unifiedMapForEachKey() {
        UnifiedMapAcceptanceTest.assertUnifiedMapForEachKey(0);
        UnifiedMapAcceptanceTest.assertUnifiedMapForEachKey(1);
        UnifiedMapAcceptanceTest.assertUnifiedMapForEachKey(2);
        UnifiedMapAcceptanceTest.assertUnifiedMapForEachKey(3);
    }

    @Test
    public void unifiedMapForEachValue() {
        UnifiedMapAcceptanceTest.assertUnifiedMapForEachValue(0);
        UnifiedMapAcceptanceTest.assertUnifiedMapForEachValue(1);
        UnifiedMapAcceptanceTest.assertUnifiedMapForEachValue(2);
        UnifiedMapAcceptanceTest.assertUnifiedMapForEachValue(3);
    }

    @Test
    public void equalsWithNullValue() {
        MutableMap<Integer, Integer> map1 = UnifiedMap.newWithKeysValues(1, null, 2, 2);
        MutableMap<Integer, Integer> map2 = UnifiedMap.newWithKeysValues(2, 2, 3, 3);
        Assert.assertNotEquals(map1, map2);
    }

    @Test
    public void unifiedMapEqualsAndHashCode() {
        UnifiedMapAcceptanceTest.assertUnifiedMapEqualsAndHashCode(0);
        UnifiedMapAcceptanceTest.assertUnifiedMapEqualsAndHashCode(1);
        UnifiedMapAcceptanceTest.assertUnifiedMapEqualsAndHashCode(2);
        UnifiedMapAcceptanceTest.assertUnifiedMapEqualsAndHashCode(3);
    }

    @Test
    public void unifiedMapPutAll() {
        UnifiedMapAcceptanceTest.assertUnifiedMapPutAll(0);
        UnifiedMapAcceptanceTest.assertUnifiedMapPutAll(1);
        UnifiedMapAcceptanceTest.assertUnifiedMapPutAll(2);
        UnifiedMapAcceptanceTest.assertUnifiedMapPutAll(3);
    }

    @Test
    public void unifiedMapPutAllWithHashMap() {
        UnifiedMapAcceptanceTest.assertUnifiedMapPutAllWithHashMap(0);
        UnifiedMapAcceptanceTest.assertUnifiedMapPutAllWithHashMap(1);
        UnifiedMapAcceptanceTest.assertUnifiedMapPutAllWithHashMap(2);
        UnifiedMapAcceptanceTest.assertUnifiedMapPutAllWithHashMap(3);
    }

    @Test
    public void unifiedMapReplace() {
        UnifiedMapAcceptanceTest.assertUnifiedMapReplace(0);
        UnifiedMapAcceptanceTest.assertUnifiedMapReplace(1);
        UnifiedMapAcceptanceTest.assertUnifiedMapReplace(2);
        UnifiedMapAcceptanceTest.assertUnifiedMapReplace(3);
    }

    @Test
    public void unifiedMapContainsValue() {
        UnifiedMapAcceptanceTest.runUnifiedMapContainsValue(0);
        UnifiedMapAcceptanceTest.runUnifiedMapContainsValue(1);
        UnifiedMapAcceptanceTest.runUnifiedMapContainsValue(2);
        UnifiedMapAcceptanceTest.runUnifiedMapContainsValue(3);
    }

    @Test
    public void unifiedMapKeySet() {
        UnifiedMapAcceptanceTest.runUnifiedMapKeySet(0);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySet(1);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySet(2);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySet(3);
    }

    @Test
    public void unifiedMapKeySetRetainAll() {
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetRetainAll(0);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetRetainAll(1);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetRetainAll(2);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetRetainAll(3);
    }

    @Test
    public void unifiedMapKeySetRemoveAll() {
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetRemoveAll(0);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetRemoveAll(1);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetRemoveAll(2);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetRemoveAll(3);
    }

    @Test
    public void unifiedMapKeySetToArray() {
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetToArray(0);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetToArray(1);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetToArray(2);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetToArray(3);
    }

    @Test
    public void unifiedMapKeySetIterator() {
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIterator(0);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIterator(1);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIterator(2);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIterator(3);
    }

    @Test
    public void unifiedMapKeySetIteratorRemove() {
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemove(0, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemove(1, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemove(2, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemove(3, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemove(0, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemove(1, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemove(2, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemove(3, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemove(0, 4);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemove(1, 4);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemove(2, 4);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemove(3, 4);
    }

    @Test
    public void unifiedMapKeySetIteratorRemoveFlip() {
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemoveFlip(0, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemoveFlip(1, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemoveFlip(2, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemoveFlip(3, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemoveFlip(0, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemoveFlip(1, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemoveFlip(2, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemoveFlip(3, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemoveFlip(0, 4);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemoveFlip(1, 4);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemoveFlip(2, 4);
        UnifiedMapAcceptanceTest.runUnifiedMapKeySetIteratorRemoveFlip(3, 4);
    }

    // entry set tests
    @Test
    public void unifiedMapEntrySet() {
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySet(0);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySet(1);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySet(2);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySet(3);
    }

    @Test
    public void unifiedMapEntrySetRetainAll() {
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetRetainAll(0);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetRetainAll(1);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetRetainAll(2);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetRetainAll(3);
    }

    @Test
    public void unifiedMapEntrySetRemoveAll() {
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetRemoveAll(0);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetRemoveAll(1);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetRemoveAll(2);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetRemoveAll(3);
    }

    @Test
    public void unifiedMapEntrySetToArray() {
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetToArray(0);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetToArray(1);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetToArray(2);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetToArray(3);
    }

    @Test
    public void unifiedMapEntrySetIterator() {
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIterator(0);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIterator(1);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIterator(2);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIterator(3);
    }

    @Test
    public void unifiedMapEntrySetIteratorSetValue() {
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorSetValue(0);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorSetValue(1);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorSetValue(2);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorSetValue(3);
    }

    @Test
    public void unifiedMapEntrySetIteratorRemove() {
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemove(0, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemove(1, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemove(2, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemove(3, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemove(0, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemove(1, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemove(2, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemove(3, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemove(0, 4);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemove(1, 4);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemove(2, 4);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemove(3, 4);
    }

    @Test
    public void unifiedMapEntrySetIteratorRemoveFlip() {
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemoveFlip(0, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemoveFlip(1, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemoveFlip(2, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemoveFlip(3, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemoveFlip(0, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemoveFlip(1, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemoveFlip(2, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemoveFlip(3, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemoveFlip(0, 4);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemoveFlip(1, 4);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemoveFlip(2, 4);
        UnifiedMapAcceptanceTest.runUnifiedMapEntrySetIteratorRemoveFlip(3, 4);
    }

    // values collection
    @Test
    public void unifiedMapValues() {
        UnifiedMapAcceptanceTest.runUnifiedMapValues(0);
        UnifiedMapAcceptanceTest.runUnifiedMapValues(1);
        UnifiedMapAcceptanceTest.runUnifiedMapValues(2);
        UnifiedMapAcceptanceTest.runUnifiedMapValues(3);
    }

    @Test
    public void unifiedMapValuesRetainAll() {
        UnifiedMapAcceptanceTest.runUnifiedMapValuesRetainAll(0);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesRetainAll(1);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesRetainAll(2);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesRetainAll(3);
    }

    @Test
    public void unifiedMapValuesRemoveAll() {
        UnifiedMapAcceptanceTest.runUnifiedMapValuesRemoveAll(0);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesRemoveAll(1);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesRemoveAll(2);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesRemoveAll(3);
    }

    @Test
    public void unifiedMapValuesToArray() {
        UnifiedMapAcceptanceTest.runUnifiedMapValuesToArray(0);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesToArray(1);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesToArray(2);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesToArray(3);
    }

    @Test
    public void unifiedMapValuesIterator() {
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIterator(0);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIterator(1);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIterator(2);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIterator(3);
    }

    @Test
    public void unifiedMapValuesIteratorRemove() {
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemove(0, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemove(1, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemove(2, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemove(3, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemove(0, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemove(1, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemove(2, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemove(3, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemove(0, 4);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemove(1, 4);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemove(2, 4);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemove(3, 4);
    }

    @Test
    public void unifiedMapValuesIteratorRemoveFlip() {
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemoveFlip(0, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemoveFlip(1, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemoveFlip(2, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemoveFlip(3, 2);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemoveFlip(0, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemoveFlip(1, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemoveFlip(2, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemoveFlip(3, 3);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemoveFlip(0, 4);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemoveFlip(1, 4);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemoveFlip(2, 4);
        UnifiedMapAcceptanceTest.runUnifiedMapValuesIteratorRemoveFlip(3, 4);
    }

    @Test
    public void unifiedMapSerialize() {
        UnifiedMapAcceptanceTest.runUnifiedMapSerialize(0);
        UnifiedMapAcceptanceTest.runUnifiedMapSerialize(1);
        UnifiedMapAcceptanceTest.runUnifiedMapSerialize(2);
        UnifiedMapAcceptanceTest.runUnifiedMapSerialize(3);
    }

    public static final class Entry implements Map.Entry<CollidingInt, String> {
        private final CollidingInt key;

        private String value;

        private Entry(CollidingInt key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public CollidingInt getKey() {
            return this.key;
        }

        @Override
        public String getValue() {
            return this.value;
        }

        @Override
        public String setValue(String value) {
            String ret = this.value;
            this.value = value;
            return ret;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> entry = ((Map.Entry<?, ?>) (o));
            if (!(Comparators.nullSafeEquals(this.key, entry.getKey()))) {
                return false;
            }
            return Comparators.nullSafeEquals(this.value, entry.getValue());
        }

        @Override
        public int hashCode() {
            return (this.key) == null ? 0 : this.key.hashCode();
        }
    }

    @Test
    public void unifiedMapToString() {
        UnifiedMap<Object, Object> map = UnifiedMap.<Object, Object>newWithKeysValues(1, "One", 2, "Two");
        Verify.assertContains("1=One", map.toString());
        Verify.assertContains("2=Two", map.toString());
        map.put("value is 'self'", map);
        Verify.assertContains("value is 'self'=(this Map)", map.toString());
    }
}

