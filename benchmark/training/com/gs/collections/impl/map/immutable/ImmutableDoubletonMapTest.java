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


import Lists.mutable;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.ImmutableMap;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test for {@link ImmutableDoubletonMap}.
 */
public class ImmutableDoubletonMapTest extends ImmutableMemoryEfficientMapTestCase {
    @Override
    @Test
    public void equalsAndHashCode() {
        super.equalsAndHashCode();
        ImmutableMap<Integer, String> map1 = new ImmutableDoubletonMap(1, "One", 2, "Two");
        ImmutableMap<Integer, String> map2 = new ImmutableDoubletonMap(1, "One", 2, "Two");
        Verify.assertEqualsAndHashCode(map1, map2);
    }

    @Override
    @Test
    public void forEachValue() {
        super.forEachValue();
        MutableList<String> collection = mutable.of();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        map.forEachValue(CollectionAddProcedure.on(collection));
        Assert.assertEquals(FastList.newListWith("1", "2"), collection);
    }

    @Override
    @Test
    public void forEachKey() {
        super.forEachKey();
        MutableList<Integer> collection = mutable.of();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        map.forEachKey(CollectionAddProcedure.on(collection));
        Assert.assertEquals(FastList.newListWith(1, 2), collection);
    }

    @Override
    @Test
    public void getIfAbsent_function() {
        super.getIfAbsent_function();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        Assert.assertNull(map.get(4));
        Assert.assertEquals("4", map.getIfAbsent(4, new com.gs.collections.impl.block.function.PassThruFunction0("4")));
        Assert.assertNull(map.get(4));
    }

    @Override
    @Test
    public void getIfAbsent() {
        super.getIfAbsent();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        Assert.assertNull(map.get(4));
        Assert.assertEquals("4", map.getIfAbsentValue(4, "4"));
        Assert.assertNull(map.get(4));
    }

    @Override
    @Test
    public void ifPresentApply() {
        super.ifPresentApply();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        Assert.assertNull(map.ifPresentApply(4, Functions.<String>getPassThru()));
        Assert.assertEquals("1", map.ifPresentApply(1, Functions.<String>getPassThru()));
        Assert.assertEquals("2", map.ifPresentApply(2, Functions.<String>getPassThru()));
    }

    @Override
    @Test
    public void notEmpty() {
        super.notEmpty();
        Assert.assertTrue(this.classUnderTest().notEmpty());
    }

    @Override
    @Test
    public void forEachWith() {
        super.forEachWith();
        MutableList<Integer> result = mutable.of();
        ImmutableMap<Integer, Integer> map = new ImmutableDoubletonMap(1, 1, 2, 2);
        map.forEachWith(( argument1, argument2) -> result.add((argument1 + argument2)), 10);
        Assert.assertEquals(FastList.newListWith(11, 12), result);
    }

    @Override
    @Test
    public void forEachWithIndex() {
        super.forEachWithIndex();
        MutableList<String> result = mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableDoubletonMap(1, "One", 2, "Two");
        map.forEachWithIndex(( value, index) -> {
            result.add(value);
            result.add(String.valueOf(index));
        });
        Assert.assertEquals(FastList.newListWith("One", "0", "Two", "1"), result);
    }

    @Override
    @Test
    public void keyValuesView() {
        super.keyValuesView();
        MutableList<String> result = mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableDoubletonMap(1, "One", 2, "Two");
        for (Pair<Integer, String> entry : map.keyValuesView()) {
            result.add(entry.getTwo());
        }
        Assert.assertEquals(FastList.newListWith("One", "Two"), result);
    }

    @Override
    @Test
    public void valuesView() {
        super.valuesView();
        MutableList<String> result = mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableDoubletonMap(1, "One", 2, "Two");
        for (String value : map.valuesView()) {
            result.add(value);
        }
        Assert.assertEquals(FastList.newListWith("One", "Two"), result);
    }

    @Override
    @Test
    public void keysView() {
        super.keysView();
        MutableList<Integer> result = mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableDoubletonMap(1, "One", 2, "Two");
        for (Integer key : map.keysView()) {
            result.add(key);
        }
        Assert.assertEquals(FastList.newListWith(1, 2), result);
    }

    @Override
    @Test
    public void testToString() {
        ImmutableMap<Integer, String> map = new ImmutableDoubletonMap(1, "One", 2, "Two");
        Assert.assertEquals("{1=One, 2=Two}", map.toString());
    }

    @Test
    public void asLazyKeys() {
        MutableList<Integer> keys = new ImmutableDoubletonMap(1, 1, 2, 2).keysView().toSortedList();
        Assert.assertEquals(FastList.newListWith(1, 2), keys);
    }

    @Test
    public void asLazyValues() {
        MutableList<Integer> values = new ImmutableDoubletonMap(1, 1, 2, 2).valuesView().toSortedList();
        Assert.assertEquals(FastList.newListWith(1, 2), values);
    }

    @Test
    public void asLazyEntries() {
        MutableList<Pair<Integer, Integer>> values = new ImmutableDoubletonMap(1, 1, 2, 2).keyValuesView().toSortedList(Comparators.byFunction(((Function<Pair<Integer, ?>, Integer>) (Pair::getOne))));
        Assert.assertEquals(FastList.newListWith(Tuples.pair(1, 1), Tuples.pair(2, 2)), values);
    }
}

