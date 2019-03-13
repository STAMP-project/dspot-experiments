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
package com.gs.collections.impl.map.mutable;


import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.ImmutableEntry;
import com.gs.collections.impl.tuple.Tuples;
import com.gs.collections.impl.utility.Iterate;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test for {@link UnmodifiableMutableMap}.
 */
public class UnmodifiableMutableMapTest extends MutableMapTestCase {
    @Override
    @Test
    public void removeObject() {
        MutableMap<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.remove("One"));
    }

    @Override
    @Test
    public void removeKey() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.removeKey(1));
    }

    @Override
    @Test
    public void removeFromEntrySet() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.entrySet().remove(ImmutableEntry.of(2, "Two")));
    }

    @Override
    @Test
    public void removeAllFromEntrySet() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.entrySet().removeAll(FastList.newListWith(ImmutableEntry.of(2, "Two"))));
    }

    @Override
    @Test
    public void retainAllFromEntrySet() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.entrySet().retainAll(FastList.newListWith(ImmutableEntry.of(2, "Two"))));
    }

    @Override
    @Test
    public void clearEntrySet() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.entrySet().clear());
    }

    @Override
    @Test
    public void removeFromKeySet() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.keySet().remove(2));
    }

    @Override
    @Test
    public void removeNullFromKeySet() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.keySet().remove(null));
    }

    @Override
    @Test
    public void removeAllFromKeySet() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.keySet().removeAll(FastList.newListWith(1, 2)));
    }

    @Override
    @Test
    public void retainAllFromKeySet() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.keySet().retainAll(Lists.mutable.of()));
    }

    @Override
    @Test
    public void clearKeySet() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.keySet().clear());
    }

    @Override
    @Test
    public void removeFromValues() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.values().remove("Two"));
    }

    @Override
    @Test
    public void removeAllFromValues() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.values().removeAll(FastList.newListWith("One", "Two")));
    }

    @Override
    @Test
    public void removeNullFromValues() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.values().remove(null));
    }

    @Override
    @Test
    public void retainAllFromValues() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.values().retainAll(Lists.mutable.of()));
    }

    @Override
    @Test
    public void getIfAbsentPut() {
        Assert.assertEquals("3", this.newMapWithKeysValues(1, "1", 2, "2", 3, "3").getIfAbsentPut(3, ((Function0<String>) (() -> ""))));
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeysValues(1, "1", 2, "2", 3, "3").getIfAbsentPut(4, ((Function0<String>) (() -> ""))));
    }

    @Override
    @Test
    public void getIfAbsentPutValue() {
        Assert.assertEquals("3", this.newMapWithKeysValues(1, "1", 2, "2", 3, "3").getIfAbsentPut(3, ""));
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeysValues(1, "1", 2, "2", 3, "3").getIfAbsentPut(4, ""));
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void getIfAbsentPutWithKey() {
        this.newMapWithKeysValues(1, 1, 2, 2, 3, 3).getIfAbsentPutWithKey(4, Functions.getIntegerPassThru());
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void getIfAbsentPutWith() {
        this.newMapWithKeysValues(1, "1", 2, "2", 3, "3").getIfAbsentPutWith(4, String::valueOf, 4);
    }

    @Override
    @Test
    public void getIfAbsentPut_block_throws() {
        // Not applicable for unmodifiable adapter
    }

    @Override
    @Test
    public void getIfAbsentPutWith_block_throws() {
        // Not applicable for unmodifiable adapter
    }

    @Override
    @Test
    public void putAll() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "2");
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.putAll(null));
    }

    @Override
    @Test
    public void collectKeysAndValues() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "2");
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.collectKeysAndValues(null, null, null));
    }

    @Override
    @Test
    public void clear() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "2");
        Verify.assertThrows(UnsupportedOperationException.class, map::clear);
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void add() {
        this.newMapWithKeysValues(1, "One", 2, "Two").add(null);
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void withKeyValue() {
        this.newMapWithKeysValues(1, "One", 2, "2").withKeyValue(null, null);
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void withAllKeyValues() {
        this.newMapWithKeysValues("A", 1, "B", 2).withAllKeyValues(FastList.newListWith(Tuples.pair("B", 22), Tuples.pair("C", 3)));
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void withAllKeyValueArguments() {
        this.newMapWithKeysValues("A", 1, "B", 2).withAllKeyValueArguments(Tuples.pair("B", 22), Tuples.pair("C", 3));
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void withoutKey() {
        this.newMapWithKeysValues("A", 1, "B", 2).withoutKey("B");
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void withoutAllKeys() {
        this.newMapWithKeysValues("A", 1, "B", 2, "C", 3).withoutAllKeys(FastList.newListWith("A", "C"));
    }

    @Override
    @Test
    public void asUnmodifiable() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "2");
        Assert.assertSame(map, map.asUnmodifiable());
    }

    @Test
    public void entrySet() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "2").asUnmodifiable();
        Verify.assertThrows(UnsupportedOperationException.class, () -> map.entrySet().remove(null));
        Verify.assertThrows(UnsupportedOperationException.class, () -> Iterate.getFirst(map.entrySet()).setValue("Three"));
        Assert.assertEquals(this.newMapWithKeysValues(1, "One", 2, "2"), map);
    }

    @Test
    public void entrySetToArray() {
        MutableMap<Integer, String> map = this.newMapWithKeyValue(1, "One").asUnmodifiable();
        Object[] entries = map.entrySet().toArray();
        Assert.assertEquals(ImmutableEntry.of(1, "One"), entries[0]);
    }

    @Test
    public void entrySetToArrayWithTarget() {
        MutableMap<Integer, String> map = this.newMapWithKeyValue(1, "One").asUnmodifiable();
        Object[] entries = map.entrySet().toArray(new Object[]{  });
        Assert.assertEquals(ImmutableEntry.of(1, "One"), entries[0]);
    }

    @Override
    @Test
    public void put() {
        Verify.assertThrows(UnsupportedOperationException.class, UnmodifiableMutableMapTest.super::put);
    }

    @Override
    @Test
    public void testClone() {
        MutableMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two");
        MutableMap<Integer, String> clone = map.clone();
        Assert.assertSame(map, clone);
    }

    @Test(expected = UnsupportedOperationException.class)
    @Override
    public void updateValue() {
        this.<Integer, Integer>newMap().updateValue(0, () -> 0, Functions.identity());
    }

    @Test(expected = UnsupportedOperationException.class)
    @Override
    public void updateValue_collisions() {
        this.<Integer, Integer>newMap().updateValue(0, () -> 0, Functions.identity());
    }

    @Test(expected = UnsupportedOperationException.class)
    @Override
    public void updateValueWith() {
        this.<Integer, Integer>newMap().updateValueWith(0, () -> 0, ( integer, parameter) -> 0, "test");
    }

    @Test(expected = UnsupportedOperationException.class)
    @Override
    public void updateValueWith_collisions() {
        this.<Integer, Integer>newMap().updateValueWith(0, () -> 0, ( integer, parameter) -> 0, "test");
    }
}

