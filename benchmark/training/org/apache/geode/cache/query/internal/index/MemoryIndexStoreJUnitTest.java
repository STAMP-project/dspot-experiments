/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.cache.query.internal.index;


import QueryService.UNDEFINED;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.apache.geode.cache.query.internal.index.AbstractIndex.InternalIndexStatistics;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.internal.cache.RegionEntry;
import org.junit.Assert;
import org.junit.Test;


public class MemoryIndexStoreJUnitTest {
    org.apache.geode.cache.Region region;

    GemFireCacheImpl cache;

    InternalIndexStatistics mockStats;

    MemoryIndexStore store;

    RegionEntry[] mockEntries;

    int numMockEntries = 10;

    GemFireCacheImpl actualInstance;

    @Test
    public void createIteratorWhenCacheNulledWhenShuttingDownShouldNotThrowNPE() {
        store.get("T");
    }

    @Test
    public void testSizeOfStoreReturnsNumberOfKeysAndNotActualNumberOfValues() {
        IntStream.range(0, 150).forEach(( i) -> {
            try {
                store.addMapping((i % 3), createRegionEntry(i, new Object()));
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        });
        Assert.assertEquals(150, numObjectsInStore(store));
    }

    @Test
    public void testAddEnoughEntriesToCreateAConcurrentHashSet() {
        IntStream.range(0, 150).forEach(( i) -> {
            try {
                store.addMapping(1, createRegionEntry(i, new Object()));
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        });
        Assert.assertEquals(150, numObjectsInStore(store));
    }

    @Test
    public void testUpdateAgainstAConcurrentHashSet() throws Exception {
        IntStream.range(0, 150).forEach(( i) -> {
            try {
                store.addMapping(1, createRegionEntry(1, new Object()));
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        });
        RegionEntry entry = createRegionEntry(1, new Object());
        store.addMapping(1, entry);
        store.updateMapping(2, 1, entry, entry.getValue(null));
        Assert.assertEquals(151, numObjectsInStore(store));
    }

    @Test
    public void testCanAddObjectWithUndefinedKey() throws Exception {
        store.addMapping(UNDEFINED, mockEntries[0]);
        Assert.assertEquals(1, numObjectsIterated(store.get(UNDEFINED)));
        Assert.assertEquals(0, numObjectsInStore(store));
    }

    @Test
    public void testCanAddManyObjectsWithUndefinedKey() throws Exception {
        for (int i = 0; i < (mockEntries.length); i++) {
            store.addMapping(UNDEFINED, mockEntries[i]);
        }
        Assert.assertEquals(mockEntries.length, numObjectsIterated(store.get(UNDEFINED)));
        // Undefined will not return without an explicit get for UNDEFINED);
        Assert.assertEquals(0, numObjectsInStore(store));
    }

    @Test
    public void testIteratorWithStartInclusiveAndNoKeysToRemoveReturnsCorrectNumberOfResults() throws Exception {
        addMockedEntries(numMockEntries);
        Assert.assertEquals(2, numObjectsIterated(store.iterator(((numMockEntries) - 2), true, null)));
    }

    @Test
    public void testIteratorWithStartExclusiveAndNoKeysToRemoveReturnsCorrectNumberOfResults() throws Exception {
        addMockedEntries(numMockEntries);
        Assert.assertEquals(1, numObjectsIterated(store.iterator(((numMockEntries) - 2), false, null)));
    }

    @Test
    public void testIteratorWithStartInclusiveAndKeyToRemoveReturnsCorrectNumberOfResults() throws Exception {
        addMockedEntries(numMockEntries);
        Set keysToRemove = new HashSet();
        keysToRemove.add("1");
        Assert.assertEquals(9, numObjectsIterated(store.iterator(1, true, keysToRemove)));
    }

    @Test
    public void testIteratorWithStartExclusiveAndKeyToRemoveReturnsCorrectNumberOfResults() throws Exception {
        addMockedEntries(numMockEntries);
        Set keysToRemove = new HashSet();
        keysToRemove.add("1");
        Assert.assertEquals(8, numObjectsIterated(store.iterator(1, false, keysToRemove)));
    }

    @Test
    public void testStartAndEndInclusiveReturnsCorrectResults() throws Exception {
        addMockedEntries(numMockEntries);
        Assert.assertEquals(6, numObjectsIterated(store.iterator(1, true, 6, true, null)));
    }

    @Test
    public void testStartInclusiveAndEndExclusiveReturnsCorrectResults() throws Exception {
        addMockedEntries(numMockEntries);
        Assert.assertEquals(5, numObjectsIterated(store.iterator(1, true, 6, false, null)));
    }

    @Test
    public void testStartExclusiveAndEndExclusiveReturnsCorrectResults() throws Exception {
        addMockedEntries(numMockEntries);
        Assert.assertEquals(4, numObjectsIterated(store.iterator(1, false, 6, false, null)));
    }

    @Test
    public void testStartExclusiveAndEndInclusiveReturnsCorrectResults() throws Exception {
        addMockedEntries(numMockEntries);
        Assert.assertEquals(5, numObjectsIterated(store.iterator(1, false, 6, true, null)));
    }

    @Test
    public void testStartIsNull() throws Exception {
        addMockedEntries(numMockEntries);
        Assert.assertEquals(6, numObjectsIterated(store.iterator(null, false, 6, false, null)));
    }

    @Test
    public void testDescendingIteratorReturnsExpectedOrderOfEntries() throws Exception {
        RegionEntry mockEntry1 = mockEntries[0];
        RegionEntry mockEntry2 = mockEntries[1];
        store.addMapping("1", mockEntry1);
        store.addMapping("2", mockEntry2);
        Iterator iteratorFirst = store.descendingIterator(null);
        Assert.assertEquals(2, numObjectsIterated(iteratorFirst));
        Iterator iterator = store.descendingIterator(null);
        iterator.hasNext();
        Assert.assertEquals(mockEntry2, getRegionEntry());
        iterator.hasNext();
        Assert.assertEquals(mockEntry1, getRegionEntry());
    }

    @Test
    public void testDescendingIteratorWithRemovedKeysReturnsExpectedOrderOfEntries() throws Exception {
        RegionEntry mockEntry1 = mockEntries[0];
        RegionEntry mockEntry2 = mockEntries[1];
        RegionEntry mockEntry3 = mockEntries[2];
        store.addMapping("1", mockEntry1);
        store.addMapping("2", mockEntry2);
        store.addMapping("3", mockEntry3);
        Set keysToRemove = new HashSet();
        keysToRemove.add("2");
        Iterator iteratorFirst = store.descendingIterator(keysToRemove);
        Assert.assertEquals(2, numObjectsIterated(iteratorFirst));
        // keysToRemove has been modified by the store, we need to readd the key to remove
        keysToRemove.add("2");
        Iterator iterator = store.descendingIterator(keysToRemove);
        iterator.hasNext();
        Assert.assertEquals(mockEntry3, getRegionEntry());
        iterator.hasNext();
        Assert.assertEquals(mockEntry1, getRegionEntry());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testDescendingIteratorWithMultipleRemovedKeysReturnsExpectedOrderOfEntries() throws Exception {
        RegionEntry mockEntry1 = mockEntries[0];
        RegionEntry mockEntry2 = mockEntries[1];
        RegionEntry mockEntry3 = mockEntries[2];
        store.addMapping("1", mockEntry1);
        store.addMapping("2", mockEntry2);
        store.addMapping("3", mockEntry3);
        Set keysToRemove = new HashSet();
        keysToRemove.add("2");
        keysToRemove.add("1");
        Iterator iteratorFirst = store.descendingIterator(keysToRemove);
        Assert.assertEquals(1, numObjectsIterated(iteratorFirst));
        // keysToRemove has been modified by the store, we need to readd the key to remove
        keysToRemove.add("2");
        keysToRemove.add("1");
        Iterator iterator = store.descendingIterator(keysToRemove);
        iterator.hasNext();
        Assert.assertEquals(mockEntry3, getRegionEntry());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testSizeWithKeyArgumentReturnsCorrectSize() throws Exception {
        RegionEntry mockEntry1 = mockEntries[0];
        RegionEntry mockEntry2 = mockEntries[1];
        store.addMapping("1", mockEntry1);
        store.addMapping("2", mockEntry2);
        Assert.assertEquals(1, store.size("1"));
    }

    @Test
    public void testGetReturnsExpectedIteratorValue() throws Exception {
        RegionEntry mockEntry1 = mockEntries[0];
        RegionEntry mockEntry2 = mockEntries[1];
        store.addMapping("1", mockEntry1);
        store.addMapping("2", mockEntry2);
        Assert.assertEquals(1, numObjectsIterated(store.get("1")));
    }

    @Test
    public void testGetReturnsExpectedIteratorWithMultipleValues() throws Exception {
        RegionEntry mockEntry1 = mockEntries[0];
        RegionEntry mockEntry2 = mockEntries[1];
        RegionEntry mockEntry3 = mockEntries[2];
        RegionEntry mockEntry4 = mockEntries[3];
        store.addMapping("1", mockEntry1);
        store.addMapping("1", mockEntry2);
        store.addMapping("1", mockEntry3);
        store.addMapping("2", mockEntry4);
        Assert.assertEquals(3, numObjectsIterated(store.get("1")));
        Assert.assertEquals(4, numObjectsInStore(store));
    }

    @Test
    public void testGetWithIndexOnKeysReturnsExpectedIteratorValues() throws Exception {
        RegionEntry mockEntry1 = mockEntries[0];
        RegionEntry mockEntry2 = mockEntries[1];
        store.setIndexOnValues(false);
        store.setIndexOnRegionKeys(true);
        store.addMapping("1", mockEntry1);
        store.addMapping("2", mockEntry2);
        Assert.assertEquals(1, numObjectsIterated(store.get("1")));
    }

    @Test
    public void testCorrectlyRemovesEntryProvidedTheWrongKey() throws Exception {
        RegionEntry mockEntry1 = mockEntries[0];
        RegionEntry mockEntry2 = mockEntries[1];
        store.addMapping("1", mockEntry1);
        store.addMapping("2", mockEntry2);
        store.removeMapping("1", mockEntry2);
        Assert.assertEquals(1, numObjectsInStore(store));
        Assert.assertTrue(objectContainedIn(store, mockEntry1));
    }

    @Test
    public void testRemoveMappingRemovesFromBackingMap() throws Exception {
        RegionEntry mockEntry1 = mockEntries[0];
        RegionEntry mockEntry2 = mockEntries[1];
        store.addMapping("1", mockEntry1);
        store.addMapping("2", mockEntry2);
        store.removeMapping("1", mockEntry1);
        Assert.assertEquals(1, numObjectsInStore(store));
        Assert.assertTrue(objectContainedIn(store, mockEntry2));
    }

    @Test
    public void testAddMappingAddsToBackingMap() throws Exception {
        RegionEntry mockEntry1 = mockEntries[0];
        RegionEntry mockEntry2 = mockEntries[1];
        store.addMapping("1", mockEntry1);
        store.addMapping("2", mockEntry2);
        Assert.assertEquals(2, numObjectsInStore(store));
        Assert.assertTrue(objectContainedIn(store, mockEntry1));
        Assert.assertTrue(objectContainedIn(store, mockEntry2));
    }

    @Test
    public void testClear() throws Exception {
        RegionEntry mockEntry1 = mockEntries[0];
        RegionEntry mockEntry2 = mockEntries[1];
        store.addMapping("1", mockEntry1);
        store.addMapping("1", mockEntry2);
        store.clear();
        Assert.assertEquals(0, numObjectsInStore(store));
    }
}

