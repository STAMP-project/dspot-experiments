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
package com.gs.collections.impl.map.immutable;


import Lists.immutable;
import Lists.mutable;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.ImmutableMapIterable;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.fixed.ArrayAdapter;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


public abstract class ImmutableMapIterableTestCase {
    @Test
    public void equalsAndHashCode() {
        MutableMap<Integer, String> expected = this.equalUnifiedMap();
        Verify.assertEqualsAndHashCode(expected, this.classUnderTest());
        Verify.assertPostSerializedEqualsAndHashCode(this.classUnderTest());
    }

    @Test
    public void forEachKeyValue() {
        MutableSet<Integer> actualKeys = UnifiedSet.newSet();
        MutableSet<String> actualValues = UnifiedSet.newSet();
        this.classUnderTest().forEachKeyValue(( key, value) -> {
            actualKeys.add(key);
            actualValues.add(value);
        });
        MutableSet<Integer> expectedKeys = this.expectedKeys();
        Assert.assertEquals(expectedKeys, actualKeys);
        MutableSet<String> expectedValues = expectedKeys.collect(String::valueOf);
        Assert.assertEquals(expectedValues, actualValues);
    }

    @Test
    public void forEachValue() {
        MutableSet<String> actualValues = UnifiedSet.newSet();
        this.classUnderTest().forEachValue(CollectionAddProcedure.on(actualValues));
        Assert.assertEquals(this.expectedValues(), actualValues);
    }

    @Test
    public void tap() {
        MutableList<String> tapResult = mutable.of();
        ImmutableMapIterable<Integer, String> map = this.classUnderTest();
        Assert.assertSame(map, map.tap(tapResult::add));
        Assert.assertEquals(map.toList(), tapResult);
    }

    @Test
    public void forEach() {
        MutableSet<String> actualValues = UnifiedSet.newSet();
        this.classUnderTest().forEach(CollectionAddProcedure.on(actualValues));
        Assert.assertEquals(this.expectedValues(), actualValues);
    }

    @Test
    public void flipUniqueValues() {
        ImmutableMapIterable<Integer, String> immutableMap = this.classUnderTest();
        Assert.assertEquals(Interval.oneTo(this.size()).toMap(String::valueOf, Functions.getIntegerPassThru()), immutableMap.flipUniqueValues());
    }

    @Test
    public void iterator() {
        MutableSet<String> actualValues = UnifiedSet.newSet();
        for (String eachValue : this.classUnderTest()) {
            actualValues.add(eachValue);
        }
        Assert.assertEquals(this.expectedValues(), actualValues);
    }

    @Test
    public void iteratorThrows() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> {
            Iterator<String> iterator = this.classUnderTest().iterator();
            iterator.remove();
        });
    }

    @Test
    public void forEachKey() {
        MutableSet<Integer> actualKeys = UnifiedSet.newSet();
        this.classUnderTest().forEachKey(CollectionAddProcedure.on(actualKeys));
        Assert.assertEquals(this.expectedKeys(), actualKeys);
    }

    @Test
    public void get() {
        Integer absentKey = (this.size()) + 1;
        String absentValue = String.valueOf(absentKey);
        // Absent key behavior
        ImmutableMapIterable<Integer, String> classUnderTest = this.classUnderTest();
        Assert.assertNull(classUnderTest.get(absentKey));
        Assert.assertFalse(classUnderTest.containsValue(absentValue));
        // Present key behavior
        Assert.assertEquals("1", classUnderTest.get(1));
        // Still unchanged
        Assert.assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Test
    public void getIfAbsent_function() {
        Integer absentKey = (this.size()) + 1;
        String absentValue = String.valueOf(absentKey);
        // Absent key behavior
        ImmutableMapIterable<Integer, String> classUnderTest = this.classUnderTest();
        Assert.assertEquals(absentValue, classUnderTest.getIfAbsent(absentKey, new com.gs.collections.impl.block.function.PassThruFunction0(absentValue)));
        // Present key behavior
        Assert.assertEquals("1", classUnderTest.getIfAbsent(1, new com.gs.collections.impl.block.function.PassThruFunction0(absentValue)));
        // Still unchanged
        Assert.assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Test
    public void getIfAbsent() {
        Integer absentKey = (this.size()) + 1;
        String absentValue = String.valueOf(absentKey);
        // Absent key behavior
        ImmutableMapIterable<Integer, String> classUnderTest = this.classUnderTest();
        Assert.assertEquals(absentValue, classUnderTest.getIfAbsentValue(absentKey, absentValue));
        // Present key behavior
        Assert.assertEquals("1", classUnderTest.getIfAbsentValue(1, absentValue));
        // Still unchanged
        Assert.assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Test
    public void getIfAbsentWith() {
        Integer absentKey = (this.size()) + 1;
        String absentValue = String.valueOf(absentKey);
        // Absent key behavior
        ImmutableMapIterable<Integer, String> classUnderTest = this.classUnderTest();
        Assert.assertEquals(absentValue, classUnderTest.getIfAbsentWith(absentKey, String::valueOf, absentValue));
        // Present key behavior
        Assert.assertEquals("1", classUnderTest.getIfAbsentWith(1, String::valueOf, absentValue));
        // Still unchanged
        Assert.assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Test
    public void ifPresentApply() {
        Integer absentKey = (this.size()) + 1;
        ImmutableMapIterable<Integer, String> classUnderTest = this.classUnderTest();
        Assert.assertNull(classUnderTest.ifPresentApply(absentKey, Functions.<String>getPassThru()));
        Assert.assertEquals("1", classUnderTest.ifPresentApply(1, Functions.<String>getPassThru()));
    }

    @Test
    public void notEmpty() {
        Assert.assertTrue(this.classUnderTest().notEmpty());
    }

    @Test
    public void forEachWith() {
        Object actualParameter = new Object();
        MutableSet<String> actualValues = UnifiedSet.newSet();
        MutableList<Object> actualParameters = mutable.of();
        this.classUnderTest().forEachWith(( eachValue, parameter) -> {
            actualValues.add(eachValue);
            actualParameters.add(parameter);
        }, actualParameter);
        Assert.assertEquals(this.expectedKeys().collect(String::valueOf), actualValues);
        Assert.assertEquals(Collections.nCopies(this.size(), actualParameter), actualParameters);
    }

    @Test
    public void forEachWithIndex() {
        MutableSet<String> actualValues = UnifiedSet.newSet();
        MutableList<Integer> actualIndices = mutable.of();
        this.classUnderTest().forEachWithIndex(( eachValue, index) -> {
            actualValues.add(eachValue);
            actualIndices.add(index);
        });
        Assert.assertEquals(this.expectedKeys().collect(String::valueOf), actualValues);
        Assert.assertEquals(this.expectedIndices(), actualIndices);
    }

    @Test
    public void keyValuesView() {
        MutableSet<Integer> actualKeys = UnifiedSet.newSet();
        MutableSet<String> actualValues = UnifiedSet.newSet();
        for (Pair<Integer, String> entry : this.classUnderTest().keyValuesView()) {
            actualKeys.add(entry.getOne());
            actualValues.add(entry.getTwo());
        }
        MutableSet<Integer> expectedKeys = this.expectedKeys();
        Assert.assertEquals(expectedKeys, actualKeys);
        MutableSet<String> expectedValues = expectedKeys.collect(String::valueOf);
        Assert.assertEquals(expectedValues, actualValues);
    }

    @Test
    public void valuesView() {
        MutableSet<String> actualValues = UnifiedSet.newSet();
        for (String eachValue : this.classUnderTest().valuesView()) {
            actualValues.add(eachValue);
        }
        MutableSet<String> expectedValues = this.expectedValues();
        Assert.assertEquals(expectedValues, actualValues);
    }

    @Test
    public void keysView() {
        MutableSet<Integer> actualKeys = UnifiedSet.newSet();
        for (Integer eachKey : this.classUnderTest().keysView()) {
            actualKeys.add(eachKey);
        }
        Assert.assertEquals(this.expectedKeys(), actualKeys);
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
    public void put() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ((Map<Integer, String>) (this.classUnderTest())).put(null, null));
    }

    @Test
    public void remove() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ((Map<Integer, String>) (this.classUnderTest())).remove(null));
    }

    @Test
    public void newWithKeyValue() {
        ImmutableMapIterable<Integer, String> immutable = this.classUnderTest();
        ImmutableMapIterable<Integer, String> immutable2 = immutable.newWithKeyValue(Integer.MAX_VALUE, Integer.toString(Integer.MAX_VALUE));
        Verify.assertSize(((immutable.size()) + 1), immutable2);
    }

    @Test
    public void newWithAllKeyValuePairs() {
        ImmutableMapIterable<Integer, String> immutable = this.classUnderTest();
        ImmutableMapIterable<Integer, String> immutable2 = immutable.newWithAllKeyValueArguments(Tuples.pair(Integer.MAX_VALUE, Integer.toString(Integer.MAX_VALUE)), Tuples.pair(Integer.MIN_VALUE, Integer.toString(Integer.MIN_VALUE)));
        Verify.assertSize(((immutable.size()) + 2), immutable2);
    }

    @Test
    public void newWithAllKeyValues() {
        ImmutableMapIterable<Integer, String> immutable = this.classUnderTest();
        ImmutableMapIterable<Integer, String> immutable2 = immutable.newWithAllKeyValues(ArrayAdapter.newArrayWith(Tuples.pair(Integer.MAX_VALUE, Integer.toString(Integer.MAX_VALUE)), Tuples.pair(Integer.MIN_VALUE, Integer.toString(Integer.MIN_VALUE))));
        Verify.assertSize(((immutable.size()) + 2), immutable2);
    }

    @Test
    public void newWithoutKey() {
        ImmutableMapIterable<Integer, String> immutable = this.classUnderTest();
        ImmutableMapIterable<Integer, String> immutable3 = immutable.newWithoutKey(Integer.MAX_VALUE);
        Verify.assertSize(immutable.size(), immutable3);
    }

    @Test
    public void newWithoutKeys() {
        ImmutableMapIterable<Integer, String> immutable = this.classUnderTest();
        ImmutableMapIterable<Integer, String> immutable2 = immutable.newWithoutAllKeys(immutable.keysView());
        ImmutableMapIterable<Integer, String> immutable3 = immutable.newWithoutAllKeys(immutable.<Integer>of());
        Assert.assertEquals(immutable, immutable3);
        Assert.assertEquals(Maps.immutable.of(), immutable2);
    }
}

