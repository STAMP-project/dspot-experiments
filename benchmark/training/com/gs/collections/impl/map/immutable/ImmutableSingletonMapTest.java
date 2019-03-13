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
import Maps.fixedSize;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.ImmutableMap;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test for {@link ImmutableSingletonMap}.
 */
public class ImmutableSingletonMapTest extends ImmutableMemoryEfficientMapTestCase {
    @Override
    @Test
    public void equalsAndHashCode() {
        super.equalsAndHashCode();
        ImmutableMap<Integer, String> map1 = new ImmutableSingletonMap(1, "One");
        ImmutableMap<Integer, String> map2 = new ImmutableSingletonMap(1, "One");
        Verify.assertEqualsAndHashCode(map1, map2);
    }

    @Test
    public void equalsAndHashCodeWithNulls() {
        ImmutableMap<Integer, String> map1 = new ImmutableSingletonMap(null, null);
        MutableMap<Integer, String> map2 = fixedSize.of(((Integer) (null)), ((String) (null)));
        Verify.assertEqualsAndHashCode(map1, map2);
    }

    @Override
    @Test
    public void forEachValue() {
        super.forEachValue();
        MutableList<String> collection = mutable.of();
        this.classUnderTest().forEachValue(CollectionAddProcedure.on(collection));
        Assert.assertEquals(FastList.newListWith("1"), collection);
    }

    @Override
    @Test
    public void forEach() {
        super.forEach();
        MutableList<String> collection = mutable.of();
        this.classUnderTest().forEach(CollectionAddProcedure.on(collection));
        Assert.assertEquals(FastList.newListWith("1"), collection);
    }

    @Override
    @Test
    public void iterator() {
        super.iterator();
        MutableList<String> collection = mutable.of();
        for (String eachValue : this.classUnderTest()) {
            collection.add(eachValue);
        }
        Assert.assertEquals(FastList.newListWith("1"), collection);
    }

    @Override
    @Test
    public void forEachKey() {
        super.forEachKey();
        MutableList<Integer> collection = mutable.of();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        map.forEachKey(CollectionAddProcedure.on(collection));
        Assert.assertEquals(FastList.newListWith(1), collection);
    }

    @Override
    @Test
    public void getIfAbsent_function() {
        super.getIfAbsent_function();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        Assert.assertNull(map.get(4));
        Assert.assertEquals("4", map.getIfAbsent(4, new com.gs.collections.impl.block.function.PassThruFunction0("4")));
        Assert.assertEquals("1", map.getIfAbsent(1, new com.gs.collections.impl.block.function.PassThruFunction0("1")));
        Assert.assertEquals(UnifiedMap.newWithKeysValues(1, "1"), map);
    }

    @Override
    @Test
    public void getIfAbsent() {
        super.getIfAbsent();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        Assert.assertNull(map.get(4));
        Assert.assertEquals("4", map.getIfAbsentValue(4, "4"));
        Assert.assertEquals("1", map.getIfAbsentValue(1, "1"));
        Assert.assertEquals(UnifiedMap.newWithKeysValues(1, "1"), map);
    }

    @Override
    @Test
    public void getIfAbsentWith() {
        super.getIfAbsentWith();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        Assert.assertNull(map.get(4));
        Assert.assertEquals("4", map.getIfAbsentWith(4, String::valueOf, 4));
        Assert.assertEquals("1", map.getIfAbsentWith(1, String::valueOf, 1));
        Assert.assertEquals(UnifiedMap.newWithKeysValues(1, "1"), map);
    }

    @Override
    @Test
    public void ifPresentApply() {
        super.ifPresentApply();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        Assert.assertNull(map.ifPresentApply(4, Functions.<String>getPassThru()));
        Assert.assertEquals("1", map.ifPresentApply(1, Functions.<String>getPassThru()));
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
        ImmutableMap<Integer, Integer> map = new ImmutableSingletonMap(1, 1);
        map.forEachWith(( argument1, argument2) -> result.add((argument1 + argument2)), 10);
        Assert.assertEquals(FastList.newListWith(11), result);
    }

    @Override
    @Test
    public void forEachWithIndex() {
        super.forEachWithIndex();
        MutableList<String> result = mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableSingletonMap(1, "One");
        map.forEachWithIndex(( value, index) -> {
            result.add(value);
            result.add(String.valueOf(index));
        });
        Assert.assertEquals(FastList.newListWith("One", "0"), result);
    }

    @Override
    @Test
    public void keyValuesView() {
        super.keyValuesView();
        MutableList<String> result = mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableSingletonMap(1, "One");
        for (Pair<Integer, String> entry : map.keyValuesView()) {
            result.add(entry.getTwo());
        }
        Assert.assertEquals(FastList.newListWith("One"), result);
    }

    @Override
    @Test
    public void valuesView() {
        super.valuesView();
        MutableList<String> result = mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableSingletonMap(1, "One");
        for (String value : map.valuesView()) {
            result.add(value);
        }
        Assert.assertEquals(FastList.newListWith("One"), result);
    }

    @Override
    @Test
    public void keysView() {
        super.keysView();
        MutableList<Integer> result = mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableSingletonMap(1, "One");
        for (Integer key : map.keysView()) {
            result.add(key);
        }
        Assert.assertEquals(FastList.newListWith(1), result);
    }

    @Override
    @Test
    public void testToString() {
        ImmutableMap<Integer, String> map = new ImmutableSingletonMap(1, "One");
        Assert.assertEquals("{1=One}", map.toString());
    }

    @Test
    public void asLazyKeys() {
        MutableList<Integer> keys = fixedSize.of(1, 1).keysView().toSortedList();
        Assert.assertEquals(FastList.newListWith(1), keys);
    }

    @Test
    public void asLazyValues() {
        MutableList<Integer> values = fixedSize.of(1, 1).valuesView().toSortedList();
        Assert.assertEquals(FastList.newListWith(1), values);
    }
}

