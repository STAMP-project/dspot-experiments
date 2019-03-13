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


import Int2IntHashMap.EntryIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class Int2IntHashMapTest {
    static final int MISSING_VALUE = -1;

    final Int2IntHashMap map;

    public Int2IntHashMapTest() {
        this(new Int2IntHashMap(Int2IntHashMapTest.MISSING_VALUE));
    }

    Int2IntHashMapTest(final Int2IntHashMap map) {
        this.map = map;
    }

    @Test
    public void shouldInitiallyBeEmpty() {
        Assert.assertEquals(0, map.size());
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void getShouldReturnMissingValueWhenEmpty() {
        Assert.assertEquals(Int2IntHashMapTest.MISSING_VALUE, map.get(1));
    }

    @Test
    public void boxedGetShouldReturnNull() {
        Assert.assertNull(map.get(((Integer) (1))));
    }

    @Test
    public void getShouldReturnMissingValueWhenThereIsNoElement() {
        map.put(1, 1);
        Assert.assertEquals(Int2IntHashMapTest.MISSING_VALUE, map.get(2));
    }

    @Test
    public void getShouldReturnPutValues() {
        map.put(1, 1);
        Assert.assertEquals(1, map.get(1));
    }

    @Test
    public void putShouldReturnOldValue() {
        map.put(1, 1);
        Assert.assertEquals(1, map.put(1, 2));
    }

    @Test
    public void clearShouldResetSize() {
        map.put(1, 1);
        map.put(100, 100);
        map.clear();
        Assert.assertEquals(0, map.size());
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void clearShouldRemoveValues() {
        map.put(1, 1);
        map.put(100, 100);
        map.clear();
        Assert.assertEquals(Int2IntHashMapTest.MISSING_VALUE, map.get(1));
        Assert.assertEquals(Int2IntHashMapTest.MISSING_VALUE, map.get(100));
    }

    @Test
    public void forEachShouldLoopOverEveryElement() {
        map.put(1, 1);
        map.put(100, 100);
        final IntIntConsumer mockConsumer = Mockito.mock(IntIntConsumer.class);
        map.intForEach(mockConsumer);
        final InOrder inOrder = Mockito.inOrder(mockConsumer);
        inOrder.verify(mockConsumer).accept(1, 1);
        inOrder.verify(mockConsumer).accept(100, 100);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void shouldNotContainKeyOfAMissingKey() {
        Assert.assertFalse(map.containsKey(1));
    }

    @Test
    public void shouldContainKeyOfAPresentKey() {
        map.put(1, 1);
        Assert.assertTrue(map.containsKey(1));
    }

    @Test
    public void shouldNotContainValueForAMissingEntry() {
        Assert.assertFalse(map.containsValue(1));
    }

    @Test
    public void shouldContainValueForAPresentEntry() {
        map.put(1, 1);
        Assert.assertTrue(map.containsValue(1));
    }

    @Test
    public void shouldExposeValidKeySet() {
        map.put(1, 1);
        map.put(2, 2);
        assertCollectionContainsElements(map.keySet());
    }

    @Test
    public void shouldExposeValidValueSet() {
        map.put(1, 1);
        map.put(2, 2);
        assertCollectionContainsElements(map.values());
    }

    @Test
    public void shouldPutAllMembersOfAnotherHashMap() {
        addTwoElements();
        final Map<Integer, Integer> other = new HashMap<>();
        other.put(1, 2);
        other.put(3, 4);
        map.putAll(other);
        Assert.assertEquals(3, map.size());
        Assert.assertEquals(2, map.get(1));
        Assert.assertEquals(3, map.get(2));
        Assert.assertEquals(4, map.get(3));
    }

    @Test
    public void shouldIterateKeys() {
        addTwoElements();
        assertIteratesKeys();
    }

    @Test
    public void shouldIterateKeysFromBeginningEveryTime() {
        shouldIterateKeys();
        assertIteratesKeys();
    }

    @Test
    public void shouldIterateKeysWithoutHasNext() {
        addTwoElements();
        assertIterateKeysWithoutHasNext();
    }

    @Test
    public void shouldIterateKeysWithoutHasNextFromBeginningEveryTime() {
        shouldIterateKeysWithoutHasNext();
        assertIterateKeysWithoutHasNext();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldExceptionForEmptyIteration() {
        keyIterator().next();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldExceptionWhenRunningOutOfElements() {
        addTwoElements();
        final Iterator<Integer> iterator = keyIterator();
        iterator.next();
        iterator.next();
        iterator.next();
    }

    @Test
    public void shouldIterateValues() {
        addTwoElements();
        assertIteratesValues();
    }

    @Test
    public void shouldIterateValuesFromBeginningEveryTime() {
        shouldIterateValues();
        assertIteratesValues();
    }

    @Test
    public void entrySetShouldContainEntries() {
        addTwoElements();
        entrySetContainsTwoElements();
    }

    @Test
    public void entrySetIteratorShouldContainEntriesEveryIteration() {
        addTwoElements();
        entrySetContainsTwoElements();
        entrySetContainsTwoElements();
    }

    @Test
    public void removeShouldReturnMissing() {
        Assert.assertEquals(Int2IntHashMapTest.MISSING_VALUE, map.remove(1));
    }

    @Test
    public void removeShouldReturnValueRemoved() {
        map.put(1, 2);
        Assert.assertEquals(2, map.remove(1));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void removeShouldRemoveEntry() {
        map.put(1, 2);
        map.remove(1);
        Assert.assertTrue(map.isEmpty());
        Assert.assertFalse(map.containsKey(1));
        Assert.assertFalse(map.containsValue(2));
    }

    @Test
    public void shouldOnlyRemoveTheSpecifiedEntry() {
        IntStream.range(0, 8).forEach(( i) -> map.put(i, (i * 2)));
        map.remove(5);
        IntStream.range(0, 8).filter(( i) -> i != 5L).forEach(( i) -> {
            Assert.assertTrue(map.containsKey(i));
            Assert.assertTrue(map.containsValue((2 * i)));
        });
    }

    @Test
    public void shouldResizeWhenMoreElementsAreAdded() {
        IntStream.range(0, 100).forEach(( key) -> {
            final int value = key * 2;
            Assert.assertEquals(Int2IntHashMapTest.MISSING_VALUE, map.put(key, value));
            Assert.assertEquals(value, map.get(key));
        });
    }

    @Test
    public void shouldHaveNoMinValueForEmptyCollection() {
        Assert.assertEquals(Int2IntHashMapTest.MISSING_VALUE, map.minValue());
    }

    @Test
    public void shouldFindMinValue() {
        addValues(map);
        Assert.assertEquals((-5), map.minValue());
    }

    @Test
    public void shouldHaveNoMaxValueForEmptyCollection() {
        Assert.assertEquals(Int2IntHashMapTest.MISSING_VALUE, map.maxValue());
    }

    @Test
    public void shouldFindMaxValue() {
        addValues(map);
        Assert.assertEquals(10, map.maxValue());
    }

    @Test
    public void sizeShouldReturnNumberOfEntries() {
        final int count = 100;
        for (int key = 0; key < count; key++) {
            map.put(key, 1);
        }
        Assert.assertEquals(count, map.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotSupportLoadFactorOfGreaterThanOne() {
        new Int2IntHashMap(4, 2, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotSupportLoadFactorOfOne() {
        new Int2IntHashMap(4, 1, 0);
    }

    @Test
    public void correctSizeAfterRehash() {
        final Int2IntHashMap map = new Int2IntHashMap(16, 0.6F, (-1));
        IntStream.range(1, 17).forEach(( i) -> map.put(i, i));
        Assert.assertEquals("Map has correct size", 16, map.size());
        final List<Integer> keys = new java.util.ArrayList(map.keySet());
        keys.forEach(map::remove);
        Assert.assertTrue("Map isn't empty", map.isEmpty());
    }

    @Test
    public void shouldComputeIfAbsent() {
        final int testKey = 7;
        final int testValue = 7;
        Assert.assertEquals(map.missingValue(), map.get(testKey));
        Assert.assertThat(map.computeIfAbsent(testKey, ( i) -> testValue), Is.is(testValue));
        Assert.assertThat(map.get(testKey), Is.is(testValue));
    }

    @Test
    public void shouldComputeIfAbsentBoxed() {
        final Map<Integer, Integer> map = this.map;
        final int testKey = 7;
        final int testValue = 7;
        Assert.assertThat(map.computeIfAbsent(testKey, ( i) -> testValue), Is.is(testValue));
        Assert.assertThat(map.get(testKey), Is.is(testValue));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowMissingValueAsValue() {
        map.put(1, Int2IntHashMapTest.MISSING_VALUE);
    }

    @Test
    public void shouldAllowMissingValueAsKey() {
        map.put(Int2IntHashMapTest.MISSING_VALUE, 1);
        Assert.assertEquals(1, map.get(Int2IntHashMapTest.MISSING_VALUE));
        Assert.assertTrue(map.containsKey(Int2IntHashMapTest.MISSING_VALUE));
        Assert.assertEquals(1, map.size());
        final int[] tuple = new int[2];
        map.intForEach(( k, v) -> {
            tuple[0] = k;
            tuple[1] = v;
        });
        Assert.assertEquals(Int2IntHashMapTest.MISSING_VALUE, tuple[0]);
        Assert.assertEquals(1, tuple[1]);
        Assert.assertEquals(1, map.remove(Int2IntHashMapTest.MISSING_VALUE));
        Assert.assertEquals(0, map.size());
        Assert.assertEquals(Int2IntHashMapTest.MISSING_VALUE, map.get(Int2IntHashMapTest.MISSING_VALUE));
    }

    @Test
    public void shouldNotContainMissingValue() {
        Assert.assertFalse(map.containsValue(Int2IntHashMapTest.MISSING_VALUE));
        map.put(Int2IntHashMapTest.MISSING_VALUE, 1);
        Assert.assertFalse(map.containsValue(Int2IntHashMapTest.MISSING_VALUE));
    }

    @Test
    public void emptyMapsShouldBeEqual() {
        Assert.assertEquals(map, new Int2IntHashMap(Int2IntHashMapTest.MISSING_VALUE));
        Assert.assertEquals(map, new HashMap<Integer, Integer>());
    }

    @Test
    public void shouldEqualPrimitiveMapWithSameContents() {
        final Int2IntHashMap otherMap = new Int2IntHashMap(Int2IntHashMapTest.MISSING_VALUE);
        addValues(map);
        addValues(otherMap);
        Assert.assertEquals(map, otherMap);
    }

    @Test
    public void shouldEqualPrimitiveMapWithSameContentsAndDifferentMissingValue() {
        final Int2IntHashMap otherMap = new Int2IntHashMap((-2));
        addValues(map);
        addValues(otherMap);
        Assert.assertEquals(map, otherMap);
    }

    @Test
    public void shouldEqualHashMapWithSameContents() {
        final Map<Integer, Integer> otherMap = new HashMap<>();
        addValues(map);
        addValues(otherMap);
        Assert.assertEquals(map, otherMap);
    }

    @Test
    public void shouldNotEqualPrimitiveMapWithDifferentContents() {
        final Int2IntHashMap otherMap = new Int2IntHashMap(Int2IntHashMapTest.MISSING_VALUE);
        addValues(map);
        addAValue(otherMap);
        Assert.assertNotEquals(map, otherMap);
    }

    @Test
    public void shouldNotEqualHashMapWithDifferentContents() {
        final Map<Integer, Integer> otherMap = new HashMap<>();
        addValues(map);
        addAValue(otherMap);
        Assert.assertNotEquals(map, otherMap);
    }

    @Test
    public void emptyMapsShouldHaveEqualHashCodes() {
        assertHashcodeEquals(map, new Int2IntHashMap(Int2IntHashMapTest.MISSING_VALUE));
        assertHashcodeEquals(map, new HashMap<Integer, Integer>());
    }

    @Test
    public void shouldHaveEqualHashcodePrimitiveMapWithSameContents() {
        final Int2IntHashMap otherMap = new Int2IntHashMap(Int2IntHashMapTest.MISSING_VALUE);
        addValues(map);
        addValues(otherMap);
        assertHashcodeEquals(map, otherMap);
    }

    @Test
    public void shouldHaveEqualHashcodePrimitiveMapWithSameContentsAndDifferentMissingValue() {
        final Int2IntHashMap otherMap = new Int2IntHashMap((-2));
        addValues(map);
        addValues(otherMap);
        assertHashcodeEquals(map, otherMap);
    }

    @Test
    public void shouldHaveEqualHashcodeHashMapWithSameContents() {
        final Map<Integer, Integer> otherMap = new HashMap<>();
        addValues(map);
        addValues(otherMap);
        assertHashcodeEquals(map, otherMap);
    }

    @Test
    public void shouldNotHaveEqualHashcodePrimitiveMapWithDifferentContents() {
        final Int2IntHashMap otherMap = new Int2IntHashMap(Int2IntHashMapTest.MISSING_VALUE);
        addValues(map);
        addAValue(otherMap);
        assertHashcodeNotEquals(map, otherMap);
    }

    @Test
    public void shouldNotHaveEqualHashcodeHashMapWithDifferentContents() {
        final Map<Integer, Integer> otherMap = new HashMap<>();
        addValues(map);
        addAValue(otherMap);
        assertHashcodeNotEquals(map, otherMap);
    }

    @Test
    public void computeIfAbsentUsingImplementation() {
        final Int2IntHashMap int2IntHashMap = new Int2IntHashMap((-1));
        final int key = 0;
        final int result = int2IntHashMap.computeIfAbsent(key, ( k) -> k);
        Assert.assertEquals(key, result);
    }

    @Test
    public void computeIfAbsentUsingInterface() {
        final Map<Integer, Integer> map = new Int2IntHashMap((-1));
        final int key = 0;
        final int result = map.computeIfAbsent(key, ( k) -> k);
        Assert.assertEquals(key, result);
    }

    @Test
    public void shouldGenerateStringRepresentation() {
        final int[] testEntries = new int[]{ 3, 1, 19, 7, 11, 12, 7 };
        for (final int testEntry : testEntries) {
            map.put(testEntry, (testEntry + 1000));
        }
        final String mapAsAString = "{12=1012, 11=1011, 7=1007, 19=1019, 3=1003, 1=1001}";
        Assert.assertThat(map.toString(), IsEqual.equalTo(mapAsAString));
    }

    @Test
    public void shouldIterateEntriesBySpecialisedType() {
        final Map<Integer, Integer> expected = new HashMap<>();
        final Int2IntHashMap map = new Int2IntHashMap(Integer.MIN_VALUE);
        IntStream.range(1, 10).forEachOrdered(( i) -> {
            map.put(i, (-i));
            expected.put(i, (-i));
        });
        final Map<Integer, Integer> actual = new HashMap<>();
        final Int2IntHashMap.EntryIterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            iter.next();
            actual.put(iter.getIntKey(), iter.getIntValue());
        } 
        Assert.assertEquals(expected, actual);
    }
}

