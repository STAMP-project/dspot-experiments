/**
 * Copyright 2015 Goldman Sachs.
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
package com.gs.collections.impl.map.sorted.immutable;


import Lists.immutable;
import Lists.mutable;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.ImmutableMap;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.map.sorted.ImmutableSortedMap;
import com.gs.collections.api.map.sorted.MutableSortedMap;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.fixed.ArrayAdapter;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.MapIterableTestCase;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test for {@link ImmutableSortedMap}.
 */
public abstract class ImmutableSortedMapTestCase extends MapIterableTestCase {
    private static final Comparator<? super Integer> REV_INT_COMPARATOR = Comparators.reverseNaturalOrder();

    @Test
    public void castToSortedMap() {
        ImmutableSortedMap<Integer, String> immutable = this.classUnderTest();
        SortedMap<Integer, String> map = immutable.castToSortedMap();
        Assert.assertSame(immutable, map);
        Assert.assertEquals(immutable, new HashMap(map));
        ImmutableSortedMap<Integer, String> revImmutable = this.classUnderTest(ImmutableSortedMapTestCase.REV_INT_COMPARATOR);
        SortedMap<Integer, String> revMap = revImmutable.castToSortedMap();
        Assert.assertSame(revImmutable, revMap);
        Assert.assertEquals(revImmutable, new HashMap(revMap));
    }

    @Override
    @Test
    public void toSortedMap() {
        super.toSortedMap();
        ImmutableSortedMap<Integer, String> immutable = this.classUnderTest();
        MutableSortedMap<Integer, String> map = immutable.toSortedMap();
        Assert.assertNotSame(immutable, map);
        Assert.assertEquals(immutable, map);
        ImmutableSortedMap<Integer, String> revImmutable = this.classUnderTest(ImmutableSortedMapTestCase.REV_INT_COMPARATOR);
        MutableSortedMap<Integer, String> revMap = revImmutable.toSortedMap();
        Assert.assertNotSame(revImmutable, revMap);
        Assert.assertEquals(revImmutable, revMap);
    }

    @Override
    @Test
    public void equalsAndHashCode() {
        super.equalsAndHashCode();
        MutableMap<Integer, String> expected = this.equalUnifiedMap();
        MutableSortedMap<Integer, String> sortedMap = this.equalSortedMap();
        Verify.assertEqualsAndHashCode(expected, this.classUnderTest());
        Verify.assertEqualsAndHashCode(sortedMap, this.classUnderTest());
        Verify.assertEqualsAndHashCode(expected, this.classUnderTest(ImmutableSortedMapTestCase.REV_INT_COMPARATOR));
        Verify.assertEqualsAndHashCode(sortedMap, this.classUnderTest(ImmutableSortedMapTestCase.REV_INT_COMPARATOR));
    }

    @Override
    @Test
    public void forEachKeyValue() {
        super.forEachKeyValue();
        MutableList<Integer> actualKeys = mutable.of();
        MutableList<String> actualValues = mutable.of();
        this.classUnderTest().forEachKeyValue(( key, value) -> {
            actualKeys.add(key);
            actualValues.add(value);
        });
        MutableList<Integer> expectedKeys = this.expectedKeys();
        Verify.assertListsEqual(expectedKeys, actualKeys);
        MutableList<String> expectedValues = expectedKeys.collect(String::valueOf);
        Verify.assertListsEqual(expectedValues, actualValues);
        MutableList<Integer> revActualKeys = mutable.of();
        MutableList<String> revActualValues = mutable.of();
        this.classUnderTest(ImmutableSortedMapTestCase.REV_INT_COMPARATOR).forEachKeyValue(( key, value) -> {
            revActualKeys.add(key);
            revActualValues.add(value);
        });
        MutableList<Integer> reverseKeys = expectedKeys.reverseThis();
        Verify.assertListsEqual(reverseKeys, revActualKeys);
        MutableList<String> reverseValues = expectedValues.reverseThis();
        Verify.assertListsEqual(reverseValues, revActualValues);
    }

    @Override
    @Test
    public void forEachValue() {
        super.forEachValue();
        MutableList<String> actualValues = mutable.of();
        this.classUnderTest().forEachValue(CollectionAddProcedure.on(actualValues));
        Verify.assertListsEqual(this.expectedValues(), actualValues);
        MutableList<String> revActualValues = mutable.of();
        this.classUnderTest(ImmutableSortedMapTestCase.REV_INT_COMPARATOR).forEachValue(CollectionAddProcedure.on(revActualValues));
        Verify.assertListsEqual(this.expectedValues().reverseThis(), revActualValues);
    }

    @Override
    @Test
    public void tap() {
        super.tap();
        MutableList<String> tapResult = mutable.of();
        ImmutableSortedMap<Integer, String> map = this.classUnderTest();
        Assert.assertSame(map, map.tap(tapResult::add));
        Assert.assertEquals(map.toList(), tapResult);
        MutableList<String> revTapResult = mutable.of();
        ImmutableSortedMap<Integer, String> revMap = this.classUnderTest(ImmutableSortedMapTestCase.REV_INT_COMPARATOR);
        Assert.assertSame(revMap, revMap.tap(revTapResult::add));
        Assert.assertEquals(revMap.toList(), revTapResult);
    }

    @Override
    @Test
    public void forEach() {
        super.forEach();
        MutableList<String> actualValues = mutable.of();
        this.classUnderTest().forEach(CollectionAddProcedure.on(actualValues));
        Verify.assertListsEqual(this.expectedValues(), actualValues);
        MutableList<String> revActualValues = mutable.of();
        this.classUnderTest(ImmutableSortedMapTestCase.REV_INT_COMPARATOR).forEach(CollectionAddProcedure.on(revActualValues));
        Verify.assertListsEqual(this.expectedValues().reverseThis(), revActualValues);
    }

    @Override
    @Test
    public void iterator() {
        super.iterator();
        MutableList<String> actualValues = mutable.of();
        for (String eachValue : this.classUnderTest()) {
            actualValues.add(eachValue);
        }
        Verify.assertListsEqual(this.expectedValues(), actualValues);
        MutableList<String> revActualValues = mutable.of();
        for (String eachValue : this.classUnderTest(ImmutableSortedMapTestCase.REV_INT_COMPARATOR)) {
            revActualValues.add(eachValue);
        }
        Verify.assertListsEqual(this.expectedValues().reverseThis(), revActualValues);
    }

    @Test
    public void iteratorThrows() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> {
            Iterator<String> iterator = this.classUnderTest().iterator();
            iterator.remove();
        });
    }

    @Override
    @Test
    public void forEachKey() {
        super.forEachKey();
        MutableList<Integer> actualKeys = mutable.of();
        this.classUnderTest().forEachKey(CollectionAddProcedure.on(actualKeys));
        Verify.assertListsEqual(this.expectedKeys(), actualKeys);
        MutableList<Integer> revActualKeys = mutable.of();
        this.classUnderTest(ImmutableSortedMapTestCase.REV_INT_COMPARATOR).forEachKey(CollectionAddProcedure.on(revActualKeys));
        Verify.assertListsEqual(this.expectedKeys().reverseThis(), revActualKeys);
    }

    @Test
    public void get() {
        Integer absentKey = (this.size()) + 1;
        String absentValue = String.valueOf(absentKey);
        // Absent key behavior
        ImmutableSortedMap<Integer, String> classUnderTest = this.classUnderTest();
        Assert.assertNull(classUnderTest.get(absentKey));
        Assert.assertFalse(classUnderTest.containsValue(absentValue));
        // Present key behavior
        Assert.assertEquals("1", classUnderTest.get(1));
        // Still unchanged
        Assert.assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Override
    @Test
    public void getIfAbsent() {
        super.getIfAbsent();
        Integer absentKey = (this.size()) + 1;
        String absentValue = String.valueOf(absentKey);
        // Absent key behavior
        ImmutableSortedMap<Integer, String> classUnderTest = this.classUnderTest();
        Assert.assertEquals(absentValue, classUnderTest.getIfAbsent(absentKey, new com.gs.collections.impl.block.function.PassThruFunction0(absentValue)));
        // Present key behavior
        Assert.assertEquals("1", classUnderTest.getIfAbsent(1, new com.gs.collections.impl.block.function.PassThruFunction0(absentValue)));
        // Still unchanged
        Assert.assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Test
    public void getIfAbsentValue() {
        Integer absentKey = (this.size()) + 1;
        String absentValue = String.valueOf(absentKey);
        // Absent key behavior
        ImmutableSortedMap<Integer, String> classUnderTest = this.classUnderTest();
        Assert.assertEquals(absentValue, classUnderTest.getIfAbsentValue(absentKey, absentValue));
        // Present key behavior
        Assert.assertEquals("1", classUnderTest.getIfAbsentValue(1, absentValue));
        // Still unchanged
        Assert.assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Override
    @Test
    public void getIfAbsentWith() {
        super.getIfAbsentWith();
        Integer absentKey = (this.size()) + 1;
        String absentValue = String.valueOf(absentKey);
        // Absent key behavior
        ImmutableSortedMap<Integer, String> classUnderTest = this.classUnderTest();
        Assert.assertEquals(absentValue, classUnderTest.getIfAbsentWith(absentKey, String::valueOf, absentValue));
        // Present key behavior
        Assert.assertEquals("1", classUnderTest.getIfAbsentWith(1, String::valueOf, absentValue));
        // Still unchanged
        Assert.assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Override
    @Test
    public void forEachWith() {
        super.forEachWith();
        Object actualParameter = new Object();
        MutableList<String> actualValues = mutable.of();
        MutableList<Object> actualParameters = mutable.of();
        this.classUnderTest().forEachWith(( eachValue, parameter) -> {
            actualValues.add(eachValue);
            actualParameters.add(parameter);
        }, actualParameter);
        Verify.assertListsEqual(this.expectedKeys().collect(String::valueOf), actualValues);
        Verify.assertListsEqual(Collections.nCopies(this.size(), actualParameter), actualParameters);
        MutableList<String> revActualValues = mutable.of();
        MutableList<Object> revActualParameters = mutable.of();
        this.classUnderTest(ImmutableSortedMapTestCase.REV_INT_COMPARATOR).forEachWith(( eachValue, parameter) -> {
            revActualValues.add(eachValue);
            revActualParameters.add(parameter);
        }, actualParameter);
        Verify.assertListsEqual(this.expectedKeys().collect(String::valueOf).reverseThis(), revActualValues);
        Verify.assertListsEqual(Collections.nCopies(this.size(), actualParameter), revActualParameters);
    }

    @Override
    @Test
    public void forEachWithIndex() {
        super.forEachWithIndex();
        MutableList<String> actualValues = mutable.of();
        MutableList<Integer> actualIndices = mutable.of();
        this.classUnderTest().forEachWithIndex(( eachValue, index) -> {
            actualValues.add(eachValue);
            actualIndices.add(index);
        });
        Verify.assertListsEqual(this.expectedKeys().collect(String::valueOf), actualValues);
        Verify.assertListsEqual(this.expectedIndices(), actualIndices);
        MutableList<String> revActualValues = mutable.of();
        MutableList<Integer> revActualIndices = mutable.of();
        this.classUnderTest(ImmutableSortedMapTestCase.REV_INT_COMPARATOR).forEachWithIndex(( eachValue, index) -> {
            revActualValues.add(eachValue);
            revActualIndices.add(index);
        });
        Verify.assertListsEqual(this.expectedKeys().collect(String::valueOf).reverseThis(), revActualValues);
        Verify.assertListsEqual(this.expectedIndices(), revActualIndices);
    }

    @Override
    @Test
    public void valuesView() {
        super.valuesView();
        MutableList<String> actualValues = mutable.of();
        for (String eachValue : this.classUnderTest().valuesView()) {
            actualValues.add(eachValue);
        }
        MutableList<String> expectedValues = this.expectedValues();
        Verify.assertListsEqual(expectedValues, actualValues);
        MutableList<String> revActualValues = mutable.of();
        for (String eachValue : this.classUnderTest(ImmutableSortedMapTestCase.REV_INT_COMPARATOR).valuesView()) {
            revActualValues.add(eachValue);
        }
        Verify.assertListsEqual(this.expectedValues().reverseThis(), revActualValues);
    }

    @Override
    @Test
    public void keysView() {
        super.keysView();
        MutableList<Integer> actualKeys = mutable.of();
        for (Integer eachKey : this.classUnderTest().keysView()) {
            actualKeys.add(eachKey);
        }
        Verify.assertListsEqual(this.expectedKeys(), actualKeys);
        MutableList<Integer> revActualKeys = mutable.of();
        for (Integer eachKey : this.classUnderTest(ImmutableSortedMapTestCase.REV_INT_COMPARATOR).keysView()) {
            revActualKeys.add(eachKey);
        }
        Verify.assertListsEqual(this.expectedKeys().reverseThis(), revActualKeys);
    }

    @Test
    public void putAll() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ((Map<Integer, String>) (this.classUnderTest())).putAll(null));
    }

    @Test
    public void clear() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ((Map<Integer, String>) (this.classUnderTest())).clear());
    }

    @Test
    public void entrySet() {
        ImmutableSortedMap<Integer, String> immutableSortedMap = this.classUnderTest();
        Map<Integer, String> map = new HashMap(immutableSortedMap.castToSortedMap());
        Assert.assertEquals(map.entrySet(), immutableSortedMap.castToSortedMap().entrySet());
        Set<Map.Entry<Integer, String>> entries = immutableSortedMap.castToSortedMap().entrySet();
        MutableList<Map.Entry<Integer, String>> entriesList = FastList.newList(entries);
        MutableList<Map.Entry<Integer, String>> sortedEntryList = entriesList.toSortedListBy(Functions.getKeyFunction());
        Assert.assertEquals(sortedEntryList, entriesList);
    }

    @Override
    @Test
    public void selectMap() {
        super.selectMap();
        ImmutableSortedMap<Integer, String> map = this.classUnderTest();
        ImmutableSortedMap<Integer, String> select = map.select(( argument1, argument2) -> argument1 < (this.size()));
        Verify.assertListsEqual(Interval.oneTo(((this.size()) - 1)), select.keysView().toList());
        Verify.assertListsEqual(Interval.oneTo(((this.size()) - 1)).collect(String::valueOf).toList(), select.valuesView().toList());
        ImmutableSortedMap<Integer, String> revMap = this.classUnderTest(ImmutableSortedMapTestCase.REV_INT_COMPARATOR);
        ImmutableSortedMap<Integer, String> revSelect = revMap.select(( argument1, argument2) -> argument1 < (this.size()));
        Verify.assertListsEqual(Interval.oneTo(((this.size()) - 1)).reverseThis(), revSelect.keysView().toList());
        Verify.assertListsEqual(Interval.oneTo(((this.size()) - 1)).collect(String::valueOf).toList().reverseThis(), revSelect.valuesView().toList());
    }

    @Override
    @Test
    public void rejectMap() {
        super.rejectMap();
        ImmutableSortedMap<Integer, String> map = this.classUnderTest();
        ImmutableSortedMap<Integer, String> reject = map.reject(( argument1, argument2) -> argument1 == 1);
        Verify.assertListsEqual(Interval.fromTo(2, this.size()), reject.keysView().toList());
        Verify.assertListsEqual(Interval.fromTo(2, this.size()).collect(String::valueOf).toList(), reject.valuesView().toList());
        ImmutableSortedMap<Integer, String> revMap = this.classUnderTest(ImmutableSortedMapTestCase.REV_INT_COMPARATOR);
        ImmutableSortedMap<Integer, String> revReject = revMap.reject(( argument1, argument2) -> argument1 == 1);
        Verify.assertListsEqual(Interval.fromTo(2, this.size()).reverseThis(), revReject.keysView().toList());
        Verify.assertListsEqual(Interval.fromTo(2, this.size()).collect(String::valueOf).toList().reverseThis(), revReject.valuesView().toList());
    }

    @Override
    @Test
    public void collectMap() {
        super.collectMap();
        ImmutableSortedMap<Integer, String> map = this.classUnderTest();
        Function2<Integer, String, Pair<String, Integer>> function = (Integer argument1,String argument2) -> Tuples.pair(argument2, argument1);
        ImmutableMap<String, Integer> collect = map.collect(function);
        Verify.assertSetsEqual(Interval.oneTo(this.size()).collect(String::valueOf).toSet(), collect.keysView().toSet());
        Verify.assertSetsEqual(Interval.oneTo(this.size()).toSet(), collect.valuesView().toSet());
        ImmutableSortedMap<Integer, String> revMap = this.classUnderTest(ImmutableSortedMapTestCase.REV_INT_COMPARATOR);
        ImmutableMap<String, Integer> revCollect = revMap.collect(function);
        Verify.assertSetsEqual(Interval.oneTo(this.size()).collect(String::valueOf).toSet(), revCollect.keysView().toSet());
        Verify.assertSetsEqual(Interval.oneTo(this.size()).toSet(), revCollect.valuesView().toSet());
    }

    @Override
    @Test
    public void collectValues() {
        super.collectValues();
        ImmutableSortedMap<Integer, String> map = this.classUnderTest();
        ImmutableSortedMap<Integer, Integer> result = map.collectValues(( argument1, argument2) -> argument1);
        Verify.assertListsEqual(result.keysView().toList(), result.valuesView().toList());
        ImmutableSortedMap<Integer, String> revMap = this.classUnderTest(ImmutableSortedMapTestCase.REV_INT_COMPARATOR);
        ImmutableSortedMap<Integer, Integer> revResult = revMap.collectValues(( argument1, argument2) -> argument1);
        Verify.assertListsEqual(revResult.keysView().toList(), revResult.valuesView().toList());
    }

    @Test
    public void newWithKeyValue() {
        ImmutableSortedMap<Integer, String> immutable = this.classUnderTest();
        ImmutableSortedMap<Integer, String> immutable2 = immutable.newWithKeyValue(Integer.MAX_VALUE, Integer.toString(Integer.MAX_VALUE));
        Verify.assertSize(((immutable.size()) + 1), immutable2.castToSortedMap());
    }

    @Test
    public void newWithAllKeyValuePairs() {
        ImmutableSortedMap<Integer, String> immutable = this.classUnderTest();
        ImmutableSortedMap<Integer, String> immutable2 = immutable.newWithAllKeyValueArguments(Tuples.pair(Integer.MAX_VALUE, Integer.toString(Integer.MAX_VALUE)), Tuples.pair(Integer.MIN_VALUE, Integer.toString(Integer.MIN_VALUE)));
        Verify.assertSize(((immutable.size()) + 2), immutable2.castToSortedMap());
    }

    @Test
    public void newWithAllKeyValues() {
        ImmutableSortedMap<Integer, String> immutable = this.classUnderTest();
        ImmutableSortedMap<Integer, String> immutable2 = immutable.newWithAllKeyValues(ArrayAdapter.newArrayWith(Tuples.pair(Integer.MAX_VALUE, Integer.toString(Integer.MAX_VALUE)), Tuples.pair(Integer.MIN_VALUE, Integer.toString(Integer.MIN_VALUE))));
        Verify.assertSize(((immutable.size()) + 2), immutable2.castToSortedMap());
    }

    @Test
    public void newWithoutKey() {
        ImmutableSortedMap<Integer, String> immutable = this.classUnderTest();
        ImmutableSortedMap<Integer, String> immutable3 = immutable.newWithoutKey(Integer.MAX_VALUE);
        Verify.assertSize(immutable.size(), immutable3.castToSortedMap());
    }

    @Test
    public void newWithoutAllKeys() {
        ImmutableSortedMap<Integer, String> immutable = this.classUnderTest();
        ImmutableSortedMap<Integer, String> immutable2 = immutable.newWithoutAllKeys(immutable.keysView());
        ImmutableSortedMap<Integer, String> immutable3 = immutable.newWithoutAllKeys(immutable.<Integer>of());
        Assert.assertEquals(immutable, immutable3);
        Assert.assertEquals(Maps.immutable.of(), immutable2);
    }

    @Test
    public void toImmutable() {
        ImmutableSortedMap<Integer, String> immutableSortedMap = this.classUnderTest();
        Assert.assertSame(immutableSortedMap, immutableSortedMap.toImmutable());
    }

    @Test
    public void put() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ((Map<Integer, String>) (this.classUnderTest())).put(null, null));
    }

    @Test
    public void remove() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ((Map<Integer, String>) (this.classUnderTest())).remove(null));
    }

    @Test
    public void take() {
        ImmutableSortedMap<Integer, String> strings1 = this.classUnderTest();
        Assert.assertEquals(SortedMaps.immutable.of(strings1.comparator()), strings1.take(0));
        Assert.assertSame(strings1.comparator(), strings1.take(0).comparator());
        Assert.assertEquals(SortedMaps.immutable.of(strings1.comparator(), 1, "1", 2, "2", 3, "3"), strings1.take(3));
        Assert.assertSame(strings1.comparator(), strings1.take(3).comparator());
        Assert.assertEquals(SortedMaps.immutable.of(strings1.comparator(), 1, "1", 2, "2", 3, "3"), strings1.take(((strings1.size()) - 1)));
        ImmutableSortedMap<Integer, String> strings2 = this.classUnderTest(Comparators.reverseNaturalOrder());
        Assert.assertSame(strings2, strings2.take(strings2.size()));
        Assert.assertSame(strings2, strings2.take(10));
        Assert.assertSame(strings2, strings2.take(Integer.MAX_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void take_throws() {
        this.classUnderTest().take((-1));
    }

    @Test
    public void drop() {
        ImmutableSortedMap<Integer, String> strings1 = this.classUnderTest();
        Assert.assertSame(strings1, strings1.drop(0));
        Assert.assertSame(strings1.comparator(), strings1.drop(0).comparator());
        Assert.assertEquals(SortedMaps.immutable.of(strings1.comparator(), 4, "4"), strings1.drop(3));
        Assert.assertSame(strings1.comparator(), strings1.drop(3).comparator());
        Assert.assertEquals(SortedMaps.immutable.of(strings1.comparator(), 4, "4"), strings1.drop(((strings1.size()) - 1)));
        ImmutableSortedMap<Integer, String> expectedMap = SortedMaps.immutable.of(Comparators.reverseNaturalOrder());
        ImmutableSortedMap<Integer, String> strings2 = this.classUnderTest(Comparators.reverseNaturalOrder());
        Assert.assertEquals(expectedMap, strings2.drop(strings2.size()));
        Assert.assertEquals(expectedMap, strings2.drop(10));
        Assert.assertEquals(expectedMap, strings2.drop(Integer.MAX_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void drop_throws() {
        this.classUnderTest().drop((-1));
    }
}

