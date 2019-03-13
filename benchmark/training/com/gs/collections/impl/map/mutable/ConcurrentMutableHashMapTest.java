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


import com.gs.collections.api.map.ConcurrentMutableMap;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.partition.PartitionIterable;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.ImmutableEntry;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test for {@link ConcurrentMutableHashMap}.
 */
public class ConcurrentMutableHashMapTest extends ConcurrentHashMapTestCase {
    @Test
    public void putIfAbsent() {
        ConcurrentMutableMap<Integer, Integer> map = this.newMapWithKeysValues(1, 1, 2, 2);
        Assert.assertEquals(Integer.valueOf(1), map.putIfAbsent(1, 1));
        Assert.assertNull(map.putIfAbsent(3, 3));
    }

    @Test
    public void replace() {
        ConcurrentMutableMap<Integer, Integer> map = this.newMapWithKeysValues(1, 1, 2, 2);
        Assert.assertEquals(Integer.valueOf(1), map.replace(1, 1));
        Assert.assertNull(map.replace(3, 3));
    }

    @Test
    public void replaceWithOldValue() {
        ConcurrentMutableMap<Integer, Integer> map = this.newMapWithKeysValues(1, 1, 2, 2);
        Assert.assertTrue(map.replace(1, 1, 1));
        Assert.assertFalse(map.replace(2, 3, 3));
    }

    @Test
    public void removeWithKeyValue() {
        ConcurrentMutableMap<Integer, Integer> map = this.newMapWithKeysValues(1, 1, 2, 2);
        Assert.assertTrue(map.remove(1, 1));
        Assert.assertFalse(map.remove(2, 3));
    }

    @Override
    @Test
    public void removeFromEntrySet() {
        MutableMap<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Assert.assertTrue(map.entrySet().remove(ImmutableEntry.of("Two", 2)));
        Assert.assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);
        Assert.assertFalse(map.entrySet().remove(ImmutableEntry.of("Four", 4)));
        Assert.assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);
    }

    @Override
    @Test
    public void removeAllFromEntrySet() {
        MutableMap<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Assert.assertTrue(map.entrySet().removeAll(FastList.newListWith(ImmutableEntry.of("One", 1), ImmutableEntry.of("Three", 3))));
        Assert.assertEquals(UnifiedMap.newWithKeysValues("Two", 2), map);
        Assert.assertFalse(map.entrySet().removeAll(FastList.newListWith(ImmutableEntry.of("Four", 4))));
        Assert.assertEquals(UnifiedMap.newWithKeysValues("Two", 2), map);
    }

    @Override
    @Test
    public void keySetEqualsAndHashCode() {
        MutableMap<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Verify.assertEqualsAndHashCode(UnifiedSet.newSetWith("One", "Two", "Three"), map.keySet());
    }

    @Override
    @Test
    public void partition_value() {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2, "C", 3, "D", 4);
        PartitionIterable<Integer> partition = map.partition(IntegerPredicates.isEven());
        Assert.assertEquals(iSet(2, 4), partition.getSelected().toSet());
        Assert.assertEquals(iSet(1, 3), partition.getRejected().toSet());
    }

    @Override
    @Test
    public void partitionWith_value() {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2, "C", 3, "D", 4);
        PartitionIterable<Integer> partition = map.partitionWith(Predicates2.in(), map.select(IntegerPredicates.isEven()));
        Assert.assertEquals(iSet(2, 4), partition.getSelected().toSet());
        Assert.assertEquals(iSet(1, 3), partition.getRejected().toSet());
    }
}

