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
package com.gs.collections.impl.map.fixed;


import Lists.mutable;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.api.tuple.Twin;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test for {@link TripletonMap}.
 */
public class TripletonMapTest extends AbstractMemoryEfficientMutableMapTest {
    @Override
    @Test
    public void containsValue() {
        Assert.assertTrue(this.classUnderTest().containsValue("One"));
    }

    @Override
    @Test
    public void forEachKeyValue() {
        MutableList<String> collection = mutable.of();
        MutableMap<Integer, String> map = new TripletonMap(1, "One", 2, "Two", 3, "Three");
        map.forEachKeyValue(( key, value) -> collection.add((key + value)));
        Assert.assertEquals(FastList.newListWith("1One", "2Two", "3Three"), collection);
    }

    @Test
    public void flipUniqueValues() {
        MutableMap<Integer, String> map = new TripletonMap(1, "One", 2, "Two", 3, "Three");
        MutableMap<String, Integer> flip = map.flipUniqueValues();
        Verify.assertInstanceOf(TripletonMap.class, flip);
        Assert.assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Two", 2, "Three", 3), flip);
        Verify.assertThrows(IllegalStateException.class, () -> new TripletonMap<>(1, "One", 2, "One", 3, "Three").flipUniqueValues());
        Verify.assertThrows(IllegalStateException.class, new Runnable() {
            public void run() {
                new TripletonMap(1, "One", 2, "Three", 3, "Three").flipUniqueValues();
            }
        });
        Verify.assertThrows(IllegalStateException.class, () -> new TripletonMap<>(1, "One", 2, "Two", 3, "One").flipUniqueValues());
    }

    @Override
    @Test
    public void nonUniqueWithKeyValue() {
        Twin<String> twin1 = Tuples.twin("1", "1");
        Twin<String> twin2 = Tuples.twin("2", "2");
        Twin<String> twin3 = Tuples.twin("3", "3");
        TripletonMap<Twin<String>, Twin<String>> map = new TripletonMap(twin1, twin1, twin2, twin2, twin3, twin3);
        Twin<String> twin4 = Tuples.twin("1", "1");
        map.withKeyValue(twin4, twin4);
        Twin<String> twin5 = Tuples.twin("2", "2");
        map.withKeyValue(twin5, twin5);
        Twin<String> twin6 = Tuples.twin("3", "3");
        map.withKeyValue(twin6, twin6);
        Assert.assertSame(map.getKey1(), twin1);
        Assert.assertSame(map.getKey2(), twin2);
        Assert.assertSame(map.getKey3(), twin3);
        Assert.assertSame(map.get(twin1), twin4);
        Assert.assertSame(map.get(twin2), twin5);
        Assert.assertSame(map.get(twin3), twin6);
    }

    @Override
    @Test
    public void forEachValue() {
        MutableList<String> collection = mutable.of();
        MutableMap<Integer, String> map = new TripletonMap(1, "1", 2, "2", 3, "3");
        map.forEachValue(CollectionAddProcedure.on(collection));
        Assert.assertEquals(FastList.newListWith("1", "2", "3"), collection);
    }

    @Override
    @Test
    public void forEach() {
        MutableList<String> collection = mutable.of();
        MutableMap<Integer, String> map = new TripletonMap(1, "1", 2, "2", 3, "3");
        map.forEach(CollectionAddProcedure.on(collection));
        Assert.assertEquals(FastList.newListWith("1", "2", "3"), collection);
    }

    @Override
    @Test
    public void forEachKey() {
        MutableList<Integer> collection = mutable.of();
        MutableMap<Integer, String> map = new TripletonMap(1, "1", 2, "2", 3, "3");
        map.forEachKey(CollectionAddProcedure.on(collection));
        Assert.assertEquals(FastList.newListWith(1, 2, 3), collection);
    }

    @Override
    @Test
    public void getIfAbsentPut() {
        MutableMap<Integer, String> map = new TripletonMap(1, "1", 2, "2", 3, "3");
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.getIfAbsentPut(4, new PassThruFunction0<>("4")));
        Assert.assertEquals("1", map.getIfAbsentPut(1, new com.gs.collections.impl.block.function.PassThruFunction0("1")));
    }

    @Override
    @Test
    public void getIfAbsentPutWith() {
        MutableMap<Integer, String> map = new TripletonMap(1, "1", 2, "2", 3, "3");
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.getIfAbsentPutWith(4, String::valueOf, 4));
        Assert.assertEquals("1", map.getIfAbsentPutWith(1, String::valueOf, 1));
    }

    @Override
    @Test
    public void getIfAbsent_function() {
        MutableMap<Integer, String> map = new TripletonMap(1, "1", 2, "2", 3, "3");
        Assert.assertNull(map.get(4));
        Assert.assertEquals("4", map.getIfAbsent(4, new com.gs.collections.impl.block.function.PassThruFunction0("4")));
        Assert.assertNull(map.get(4));
    }

    @Override
    @Test
    public void getIfAbsent() {
        MutableMap<Integer, String> map = new TripletonMap(1, "1", 2, "2", 3, "3");
        Assert.assertNull(map.get(4));
        Assert.assertEquals("4", map.getIfAbsentValue(4, "4"));
        Assert.assertNull(map.get(4));
    }

    @Override
    @Test
    public void getIfAbsentWith() {
        MutableMap<Integer, String> map = new TripletonMap(1, "1", 2, "2", 3, "3");
        Assert.assertNull(map.get(4));
        Assert.assertEquals("4", map.getIfAbsentWith(4, String::valueOf, 4));
        Assert.assertNull(map.get(4));
    }

    @Override
    @Test
    public void ifPresentApply() {
        MutableMap<Integer, String> map = new TripletonMap(1, "1", 2, "2", 3, "3");
        Assert.assertNull(map.ifPresentApply(4, Functions.<String>getPassThru()));
        Assert.assertEquals("1", map.ifPresentApply(1, Functions.<String>getPassThru()));
        Assert.assertEquals("2", map.ifPresentApply(2, Functions.<String>getPassThru()));
        Assert.assertEquals("3", map.ifPresentApply(3, Functions.<String>getPassThru()));
    }

    @Override
    @Test
    public void notEmpty() {
        Assert.assertTrue(new TripletonMap(1, "1", 2, "2", 3, "3").notEmpty());
    }

    @Override
    @Test
    public void forEachWith() {
        MutableList<Integer> result = mutable.of();
        MutableMap<Integer, Integer> map = new TripletonMap(1, 1, 2, 2, 3, 3);
        map.forEachWith(( argument1, argument2) -> result.add((argument1 + argument2)), 10);
        Assert.assertEquals(FastList.newListWith(11, 12, 13), result);
    }

    @Override
    @Test
    public void forEachWithIndex() {
        MutableList<String> result = mutable.of();
        MutableMap<Integer, String> map = new TripletonMap(1, "One", 2, "Two", 3, "Three");
        map.forEachWithIndex(( value, index) -> {
            result.add(value);
            result.add(String.valueOf(index));
        });
        Assert.assertEquals(FastList.newListWith("One", "0", "Two", "1", "Three", "2"), result);
    }

    @Override
    @Test
    public void entrySet() {
        MutableList<String> result = mutable.of();
        MutableMap<Integer, String> map = new TripletonMap(1, "One", 2, "Two", 3, "Three");
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            result.add(entry.getValue());
        }
        Assert.assertEquals(FastList.newListWith("One", "Two", "Three"), result);
    }

    @Override
    @Test
    public void values() {
        MutableList<String> result = mutable.of();
        MutableMap<Integer, String> map = new TripletonMap(1, "One", 2, "Two", 3, "Three");
        for (String value : map.values()) {
            result.add(value);
        }
        Assert.assertEquals(FastList.newListWith("One", "Two", "Three"), result);
    }

    @Override
    @Test
    public void keySet() {
        MutableList<Integer> result = mutable.of();
        MutableMap<Integer, String> map = new TripletonMap(1, "One", 2, "Two", 3, "Three");
        for (Integer key : map.keySet()) {
            result.add(key);
        }
        Assert.assertEquals(FastList.newListWith(1, 2, 3), result);
    }

    @Override
    @Test
    public void testToString() {
        MutableMap<Integer, String> map = new TripletonMap(1, "One", 2, "Two", 3, "Three");
        Assert.assertEquals("{1=One, 2=Two, 3=Three}", map.toString());
    }

    @Override
    @Test
    public void testEqualsAndHashCode() {
        Verify.assertEqualsAndHashCode(UnifiedMap.newWithKeysValues("1", "One", "2", "Two", "3", "Three"), this.classUnderTest());
    }

    @Override
    @Test
    public void testClone() {
        MutableMap<Integer, String> map = new TripletonMap(1, "One", 2, "Two", 3, "Three");
        Verify.assertShallowClone(map);
    }

    @Override
    @Test
    public void select() {
        MutableMap<String, String> map = this.classUnderTest();
        MutableMap<String, String> empty = map.select(( ignored1, ignored2) -> false);
        Verify.assertInstanceOf(EmptyMap.class, empty);
        MutableMap<String, String> full = map.select(( ignored1, ignored2) -> true);
        Verify.assertInstanceOf(TripletonMap.class, full);
        Assert.assertEquals(map, full);
        MutableMap<String, String> one = map.select(( argument1, argument2) -> "1".equals(argument1));
        Verify.assertInstanceOf(SingletonMap.class, one);
        Assert.assertEquals(new SingletonMap("1", "One"), one);
        MutableMap<String, String> two = map.select(( argument1, argument2) -> "2".equals(argument1));
        Verify.assertInstanceOf(SingletonMap.class, two);
        Assert.assertEquals(new SingletonMap("2", "Two"), two);
        MutableMap<String, String> three = map.select(( argument1, argument2) -> "3".equals(argument1));
        Verify.assertInstanceOf(SingletonMap.class, three);
        Assert.assertEquals(new SingletonMap("3", "Three"), three);
        MutableMap<String, String> oneAndThree = map.select(( argument1, argument2) -> ("1".equals(argument1)) || ("3".equals(argument1)));
        Verify.assertInstanceOf(DoubletonMap.class, oneAndThree);
        Assert.assertEquals(new DoubletonMap("1", "One", "3", "Three"), oneAndThree);
        MutableMap<String, String> oneAndTwo = map.select(( argument1, argument2) -> ("1".equals(argument1)) || ("2".equals(argument1)));
        Verify.assertInstanceOf(DoubletonMap.class, oneAndTwo);
        Assert.assertEquals(new DoubletonMap("1", "One", "2", "Two"), oneAndTwo);
        MutableMap<String, String> twoAndThree = map.select(( argument1, argument2) -> ("2".equals(argument1)) || ("3".equals(argument1)));
        Verify.assertInstanceOf(DoubletonMap.class, twoAndThree);
        Assert.assertEquals(new DoubletonMap("2", "Two", "3", "Three"), twoAndThree);
    }

    @Override
    @Test
    public void reject() {
        MutableMap<String, String> map = this.classUnderTest();
        MutableMap<String, String> empty = map.reject(( ignored1, ignored2) -> true);
        Verify.assertInstanceOf(EmptyMap.class, empty);
        MutableMap<String, String> full = map.reject(( ignored1, ignored2) -> false);
        Verify.assertInstanceOf(TripletonMap.class, full);
        Assert.assertEquals(map, full);
        MutableMap<String, String> one = map.reject(( argument1, argument2) -> ("2".equals(argument1)) || ("3".equals(argument1)));
        Verify.assertInstanceOf(SingletonMap.class, one);
        Assert.assertEquals(new SingletonMap("1", "One"), one);
        MutableMap<String, String> two = map.reject(( argument1, argument2) -> ("1".equals(argument1)) || ("3".equals(argument1)));
        Verify.assertInstanceOf(SingletonMap.class, two);
        Assert.assertEquals(new SingletonMap("2", "Two"), two);
        MutableMap<String, String> three = map.reject(( argument1, argument2) -> ("1".equals(argument1)) || ("2".equals(argument1)));
        Verify.assertInstanceOf(SingletonMap.class, three);
        Assert.assertEquals(new SingletonMap("3", "Three"), three);
        MutableMap<String, String> oneAndThree = map.reject(( argument1, argument2) -> "2".equals(argument1));
        Verify.assertInstanceOf(DoubletonMap.class, oneAndThree);
        Assert.assertEquals(new DoubletonMap("1", "One", "3", "Three"), oneAndThree);
        MutableMap<String, String> oneAndTwo = map.reject(( argument1, argument2) -> "3".equals(argument1));
        Verify.assertInstanceOf(DoubletonMap.class, oneAndTwo);
        Assert.assertEquals(new DoubletonMap("1", "One", "2", "Two"), oneAndTwo);
        MutableMap<String, String> twoAndThree = map.reject(( argument1, argument2) -> "1".equals(argument1));
        Verify.assertInstanceOf(DoubletonMap.class, twoAndThree);
        Assert.assertEquals(new DoubletonMap("2", "Two", "3", "Three"), twoAndThree);
    }

    @Override
    @Test
    public void detect() {
        MutableMap<String, String> map = this.classUnderTest();
        Pair<String, String> one = map.detect(( ignored1, ignored2) -> true);
        Assert.assertEquals(Tuples.pair("1", "One"), one);
        Pair<String, String> two = map.detect(( argument1, argument2) -> "2".equals(argument1));
        Assert.assertEquals(Tuples.pair("2", "Two"), two);
        Pair<String, String> three = map.detect(( argument1, argument2) -> "3".equals(argument1));
        Assert.assertEquals(Tuples.pair("3", "Three"), three);
        Assert.assertNull(map.detect(( ignored1, ignored2) -> false));
    }

    @Override
    @Test
    public void iterator() {
        MutableList<String> collection = mutable.of();
        MutableMap<Integer, String> map = new TripletonMap(1, "1", 2, "2", 3, "3");
        for (String eachValue : map) {
            collection.add(eachValue);
        }
        Assert.assertEquals(FastList.newListWith("1", "2", "3"), collection);
    }

    @Override
    @Test
    public void asLazyKeys() {
        Assert.assertEquals(FastList.newListWith("1", "2", "3"), this.classUnderTest().keysView().toSortedList());
    }

    @Override
    @Test
    public void asLazyValues() {
        Assert.assertEquals(Bags.mutable.of("One", "Two", "Three"), this.classUnderTest().valuesView().toBag());
    }
}

