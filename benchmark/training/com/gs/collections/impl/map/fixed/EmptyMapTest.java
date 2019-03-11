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
import Maps.fixedSize;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.test.Verify;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test for {@link EmptyMap}.
 */
public class EmptyMapTest extends AbstractMemoryEfficientMutableMapTest {
    @Override
    @Test
    public void containsValue() {
        Assert.assertFalse(new EmptyMap().containsValue("One"));
    }

    @Test
    public void size() {
        Verify.assertEmpty(new EmptyMap());
    }

    @Test
    public void empty() {
        Verify.assertEmpty(new EmptyMap());
        Assert.assertFalse(new EmptyMap().notEmpty());
        Verify.assertEmpty(new EmptyMap());
        Assert.assertFalse(new EmptyMap().notEmpty());
        Verify.assertEmpty(fixedSize.of());
        Assert.assertFalse(fixedSize.of().notEmpty());
    }

    @Test
    public void viewsEmpty() {
        Verify.assertEmpty(new EmptyMap().entrySet());
        Verify.assertEmpty(new EmptyMap().values());
        Verify.assertEmpty(new EmptyMap().keySet());
    }

    @Test
    public void flipUniqueValues() {
        MutableMap<Object, Object> flip = new EmptyMap().flipUniqueValues();
        Verify.assertEmpty(flip);
        Verify.assertInstanceOf(EmptyMap.class, flip);
    }

    @Test
    public void testReadResolve() {
        Verify.assertInstanceOf(EmptyMap.class, fixedSize.of());
        Verify.assertPostSerializedIdentity(fixedSize.of());
    }

    @Override
    @Test
    public void testClone() {
        MutableMap<String, String> map = this.classUnderTest();
        Assert.assertSame(map, map.clone());
    }

    @Test
    public void iterations() {
        EmptyMapTest.StubProcedure<Object> procedure = new EmptyMapTest.StubProcedure<>();
        MutableMap<Object, Object> map = new EmptyMap();
        map.forEach(procedure);
        Assert.assertFalse(procedure.called);
        map.forEachKey(procedure);
        Assert.assertFalse(procedure.called);
        map.forEachValue(procedure);
        Assert.assertFalse(procedure.called);
        map.forEachKeyValue(procedure);
        Assert.assertFalse(procedure.called);
        map.forEachWith(procedure, new Object());
        Assert.assertFalse(procedure.called);
        map.forEachWithIndex(procedure);
        Assert.assertFalse(procedure.called);
    }

    @Override
    @Test
    public void testToString() {
        Assert.assertEquals("{}", new EmptyMap<Integer, String>().toString());
    }

    @Override
    @Test
    public void testEqualsAndHashCode() {
        Verify.assertEqualsAndHashCode(UnifiedMap.<String, String>newMap(), this.classUnderTest());
    }

    @Override
    @Test
    public void select() {
        MutableMap<String, String> map = this.classUnderTest();
        MutableMap<String, String> actual = map.select(( ignored1, ignored2) -> true);
        Verify.assertInstanceOf(EmptyMap.class, actual);
    }

    @Override
    @Test
    public void reject() {
        MutableMap<String, String> map = this.classUnderTest();
        MutableMap<String, String> actual = map.reject(( ignored1, ignored2) -> false);
        Verify.assertInstanceOf(EmptyMap.class, actual);
    }

    @Override
    @Test
    public void detect() {
        MutableMap<String, String> map = this.classUnderTest();
        Assert.assertNull(map.detect(( ignored1, ignored2) -> true));
    }

    @Override
    @Test
    public void allSatisfy() {
        MutableMap<String, String> map = this.classUnderTest();
        Assert.assertTrue(map.allSatisfy(( ignored) -> true));
    }

    @Override
    @Test
    public void anySatisfy() {
        MutableMap<String, String> map = this.classUnderTest();
        Assert.assertFalse(map.anySatisfy(( ignored) -> true));
    }

    @Override
    @Test
    public void noneSatisfy() {
        MutableMap<String, String> map = this.classUnderTest();
        Assert.assertTrue(map.noneSatisfy(( ignored) -> true));
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void max() {
        this.classUnderTest().max();
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void maxBy() {
        this.classUnderTest().maxBy(Functions.getStringPassThru());
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void min() {
        this.classUnderTest().min();
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void minBy() {
        this.classUnderTest().minBy(Functions.getStringPassThru());
    }

    private static class StubProcedure<T> implements Procedure<T> , Procedure2<T, T> , ObjectIntProcedure<T> {
        private static final long serialVersionUID = 1L;

        private boolean called = false;

        @Override
        public void value(T each) {
            this.called = true;
        }

        @Override
        public void value(T argument1, T argument2) {
            this.called = true;
        }

        @Override
        public void value(T each, int index) {
            this.called = true;
        }
    }

    @Override
    @Test
    public void forEachValue() {
        MutableList<String> collection = mutable.of();
        MutableMap<Integer, String> map = new EmptyMap();
        map.forEachValue(CollectionAddProcedure.on(collection));
        Verify.assertEmpty(collection);
    }

    @Override
    @Test
    public void forEach() {
        MutableList<String> collection = mutable.of();
        MutableMap<Integer, String> map = new EmptyMap();
        map.forEach(CollectionAddProcedure.on(collection));
        Verify.assertEmpty(collection);
    }

    @Override
    @Test
    public void forEachKey() {
        MutableList<Integer> collection = mutable.of();
        MutableMap<Integer, String> map = new EmptyMap();
        map.forEachKey(CollectionAddProcedure.on(collection));
        Verify.assertEmpty(collection);
    }

    @Override
    @Test
    public void forEachWith() {
        MutableList<Integer> result = mutable.of();
        MutableMap<Integer, Integer> map = new EmptyMap();
        map.forEachWith(( argument1, argument2) -> result.add((argument1 + argument2)), 10);
        Verify.assertEmpty(result);
    }

    @Override
    @Test
    public void forEachWithIndex() {
        MutableList<String> result = mutable.of();
        MutableMap<Integer, String> map = new EmptyMap();
        map.forEachWithIndex(( value, index) -> {
            result.add(value);
            result.add(String.valueOf(index));
        });
        Verify.assertEmpty(result);
    }

    @Override
    @Test
    public void forEachKeyValue() {
        MutableList<String> collection = mutable.of();
        MutableMap<Integer, String> map = new EmptyMap();
        map.forEachKeyValue(( key, value) -> collection.add((key + value)));
        Verify.assertEmpty(collection);
    }

    @Override
    @Test
    public void asLazyKeys() {
        Verify.assertIterableEmpty(this.classUnderTest().keysView());
    }

    @Override
    @Test
    public void asLazyValues() {
        Verify.assertIterableEmpty(this.classUnderTest().valuesView());
    }

    @Override
    @Test
    public void getIfAbsentPut() {
        MutableMap<Integer, String> map = new EmptyMap();
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.getIfAbsentPut(4, new PassThruFunction0<>("4")));
    }

    @Override
    @Test
    public void getIfAbsentPutWith() {
        MutableMap<Integer, String> map = new EmptyMap();
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.getIfAbsentPutWith(4, String::valueOf, 4));
    }

    @Override
    @Test
    public void getIfAbsent_function() {
        MutableMap<Integer, String> map = new EmptyMap();
        Assert.assertNull(map.get(4));
        Assert.assertEquals("4", map.getIfAbsent(4, new com.gs.collections.impl.block.function.PassThruFunction0("4")));
        Assert.assertNull(map.get(4));
    }

    @Override
    @Test
    public void getIfAbsent() {
        MutableMap<Integer, String> map = new EmptyMap();
        Assert.assertNull(map.get(4));
        Assert.assertEquals("4", map.getIfAbsentValue(4, "4"));
        Assert.assertNull(map.get(4));
    }

    @Override
    @Test
    public void getIfAbsentWith() {
        MutableMap<Integer, String> map = new EmptyMap();
        Assert.assertNull(map.get(4));
        Assert.assertEquals("4", map.getIfAbsentWith(4, String::valueOf, 4));
        Assert.assertNull(map.get(4));
    }

    @Override
    @Test
    public void ifPresentApply() {
        MutableMap<Integer, String> map = new EmptyMap();
        Assert.assertNull(map.ifPresentApply(4, Functions.<String>getPassThru()));
    }

    @Override
    @Test
    public void notEmpty() {
        Assert.assertFalse(new EmptyMap<Integer, String>().notEmpty());
    }

    @Override
    @Test
    public void entrySet() {
        Verify.assertEmpty(new EmptyMap<Integer, String>().entrySet());
    }

    @Override
    @Test
    public void values() {
        Verify.assertEmpty(new EmptyMap<Integer, String>().values());
    }

    @Override
    @Test
    public void keySet() {
        Verify.assertEmpty(new EmptyMap<Integer, String>().keySet());
    }

    @Override
    @Test
    public void nonUniqueWithKeyValue() {
        // Not applicable for EmptyMap
    }

    @Override
    @Test
    public void iterator() {
        MutableList<String> collection = mutable.of();
        MutableMap<Integer, String> map = new EmptyMap();
        for (String eachValue : map) {
            collection.add(eachValue);
        }
        Assert.assertEquals(FastList.newListWith(), collection);
    }
}

