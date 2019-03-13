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
package com.gs.collections.impl.map.strategy.mutable;


import com.gs.collections.api.block.HashingStrategy;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.map.mutable.UnifiedMapTestCase;
import com.gs.collections.impl.math.IntegerSum;
import com.gs.collections.impl.math.Sum;
import com.gs.collections.impl.parallel.BatchIterable;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.test.domain.Person;
import com.gs.collections.impl.tuple.ImmutableEntry;
import com.gs.collections.impl.tuple.Tuples;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class UnifiedMapWithHashingStrategyTest extends UnifiedMapTestCase {
    // Not using the static factor method in order to have concrete types for test cases
    private static final HashingStrategy<Integer> INTEGER_HASHING_STRATEGY = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<Integer>() {
        public int computeHashCode(Integer object) {
            return object.hashCode();
        }

        public boolean equals(Integer object1, Integer object2) {
            return object1.equals(object2);
        }
    });

    private static final HashingStrategy<String> STRING_HASHING_STRATEGY = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<String>() {
        public int computeHashCode(String object) {
            return object.hashCode();
        }

        public boolean equals(String object1, String object2) {
            return object1.equals(object2);
        }
    });

    private static final HashingStrategy<Person> FIRST_NAME_HASHING_STRATEGY = HashingStrategies.fromFunction(Person.TO_FIRST);

    private static final HashingStrategy<Person> LAST_NAME_HASHING_STRATEGY = HashingStrategies.fromFunction(Person.TO_LAST);

    private static final Person JOHNSMITH = new Person("John", "Smith");

    private static final Person JANESMITH = new Person("Jane", "Smith");

    private static final Person JOHNDOE = new Person("John", "Doe");

    private static final Person JANEDOE = new Person("Jane", "Doe");

    @Test
    public void constructorOfPairs() {
        Assert.assertEquals(UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 1, "one", 2, "two", 3, "three"), UnifiedMapWithHashingStrategy.newMapWith(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, Tuples.pair(1, "one"), Tuples.pair(2, "two"), Tuples.pair(3, "three")));
    }

    @Test
    public void constructorOfIterableOfPairs() {
        Pair<Integer, String> pair1 = Tuples.pair(1, "One");
        Pair<Integer, String> pair2 = Tuples.pair(2, "Two");
        Pair<Integer, String> pair3 = Tuples.pair(3, "Three");
        Pair<Integer, String> pair4 = Tuples.pair(4, "Four");
        UnifiedMapWithHashingStrategy<Integer, String> expected = UnifiedMapWithHashingStrategy.newMapWith(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, pair1, pair2, pair3, pair4);
        UnifiedMapWithHashingStrategy<Integer, String> actual1 = UnifiedMapWithHashingStrategy.newMapWith(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, FastList.<Pair<Integer, String>>newListWith(pair1, pair2, pair3, pair4));
        Assert.assertEquals(expected, actual1);
        Assert.assertEquals(expected.hashingStrategy(), actual1.hashingStrategy());
        UnifiedMapWithHashingStrategy<Integer, String> actual2 = UnifiedMapWithHashingStrategy.newMapWith(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, UnifiedSet.<Pair<Integer, String>>newSetWith(pair1, pair2, pair3, pair4));
        Assert.assertEquals(expected, actual2);
        Assert.assertEquals(expected.hashingStrategy(), actual2.hashingStrategy());
    }

    @Test
    public void newMap_throws() {
        Verify.assertThrows(IllegalArgumentException.class, () -> new UnifiedMapWithHashingStrategy<Integer, Integer>(INTEGER_HASHING_STRATEGY, (-1), 0.5F));
        Verify.assertThrows(IllegalArgumentException.class, () -> new UnifiedMapWithHashingStrategy<Integer, Integer>(INTEGER_HASHING_STRATEGY, 1, (-0.5F)));
        Verify.assertThrows(IllegalArgumentException.class, () -> new UnifiedMapWithHashingStrategy<Integer, Integer>(INTEGER_HASHING_STRATEGY, 1, 1.5F));
    }

    @Override
    @Test
    public void selectMap() {
        super.selectMap();
        UnifiedMapWithHashingStrategy<Person, Integer> map = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, UnifiedMapWithHashingStrategyTest.JOHNDOE, 1, UnifiedMapWithHashingStrategyTest.JANEDOE, 2, UnifiedMapWithHashingStrategyTest.JOHNSMITH, 3, UnifiedMapWithHashingStrategyTest.JANESMITH, 4);
        Assert.assertEquals(UnifiedMap.newWithKeysValues(UnifiedMapWithHashingStrategyTest.JOHNDOE, 2), map.select(( argument1, argument2) -> "Doe".equals(argument1.getLastName())));
    }

    @Override
    @Test
    public void rejectMap() {
        super.rejectMap();
        UnifiedMapWithHashingStrategy<Person, Integer> map = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, UnifiedMapWithHashingStrategyTest.JOHNDOE, 1, UnifiedMapWithHashingStrategyTest.JANEDOE, 2, UnifiedMapWithHashingStrategyTest.JOHNSMITH, 3, UnifiedMapWithHashingStrategyTest.JANESMITH, 4);
        Assert.assertEquals(UnifiedMap.newWithKeysValues(UnifiedMapWithHashingStrategyTest.JOHNDOE, 2), map.reject(( argument1, argument2) -> "Smith".equals(argument1.getLastName())));
    }

    @Override
    @Test
    public void collectMap() {
        super.collectMap();
        UnifiedMapWithHashingStrategy<Person, Integer> map = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, UnifiedMapWithHashingStrategyTest.JOHNDOE, 1, UnifiedMapWithHashingStrategyTest.JOHNSMITH, 2, UnifiedMapWithHashingStrategyTest.JANEDOE, 3, UnifiedMapWithHashingStrategyTest.JANESMITH, 4);
        Function2<Person, Integer, Pair<Integer, Person>> function = (Person argument1,Integer argument2) -> Tuples.pair(argument2, argument1);
        MutableMap<Integer, Person> collect = map.collect(function);
        Verify.assertSetsEqual(UnifiedSet.newSetWith(3, 4), collect.keySet());
        Verify.assertContainsAll(collect.values(), UnifiedMapWithHashingStrategyTest.JOHNDOE, UnifiedMapWithHashingStrategyTest.JOHNSMITH);
    }

    @Test
    public void contains_with_hashing_strategy() {
        UnifiedMapWithHashingStrategy<Person, Integer> map = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, UnifiedMapWithHashingStrategyTest.JOHNDOE, 1, UnifiedMapWithHashingStrategyTest.JANEDOE, 2, UnifiedMapWithHashingStrategyTest.JOHNSMITH, 3, UnifiedMapWithHashingStrategyTest.JANESMITH, 4);
        Assert.assertTrue(map.containsKey(UnifiedMapWithHashingStrategyTest.JOHNDOE));
        Assert.assertTrue(map.containsValue(2));
        Assert.assertTrue(map.containsKey(UnifiedMapWithHashingStrategyTest.JOHNSMITH));
        Assert.assertTrue(map.containsValue(4));
        Assert.assertTrue(map.containsKey(UnifiedMapWithHashingStrategyTest.JANEDOE));
        Assert.assertTrue(map.containsKey(UnifiedMapWithHashingStrategyTest.JANESMITH));
        Assert.assertFalse(map.containsValue(1));
        Assert.assertFalse(map.containsValue(3));
    }

    @Test
    public void remove_with_hashing_strategy() {
        UnifiedMapWithHashingStrategy<Person, Integer> map = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, UnifiedMapWithHashingStrategyTest.JOHNDOE, 1, UnifiedMapWithHashingStrategyTest.JANEDOE, 2, UnifiedMapWithHashingStrategyTest.JOHNSMITH, 3, UnifiedMapWithHashingStrategyTest.JANESMITH, 4);
        // Testing removing people
        Assert.assertEquals(2, map.remove(UnifiedMapWithHashingStrategyTest.JANEDOE).intValue());
        Assert.assertEquals(4, map.remove(UnifiedMapWithHashingStrategyTest.JOHNSMITH).intValue());
        Verify.assertEmpty(map);
        // Testing removing from a chain
        UnifiedMapWithHashingStrategy<Integer, Integer> map2 = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, UnifiedMapTestCase.COLLISION_4, 4);
        Assert.assertEquals(4, map2.remove(UnifiedMapTestCase.COLLISION_4).intValue());
        Assert.assertEquals(1, map2.remove(UnifiedMapTestCase.COLLISION_1).intValue());
        Verify.assertSize(2, map2);
        // Testing removing null from a chain
        UnifiedMapWithHashingStrategy<Integer, Integer> map3 = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, UnifiedMapTestCase.COLLISION_1, 1, null, 2, 3, 3, 4, null);
        Assert.assertEquals(2, map3.remove(null).intValue());
        Verify.assertSize(3, map3);
        Assert.assertNull(map3.remove(4));
        Verify.assertSize(2, map3);
    }

    @Test
    public void keySet_isEmpty() {
        Set<Integer> keySet = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 1, 1, 2, 2).keySet();
        Assert.assertFalse(keySet.isEmpty());
        keySet.clear();
        Verify.assertEmpty(keySet);
    }

    @Test
    public void keySet_with_hashing_strategy() {
        Set<Person> people = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, UnifiedMapWithHashingStrategyTest.JOHNDOE, 1, UnifiedMapWithHashingStrategyTest.JANEDOE, 2, UnifiedMapWithHashingStrategyTest.JOHNSMITH, 3, UnifiedMapWithHashingStrategyTest.JANESMITH, 4).keySet();
        Verify.assertSize(2, people);
        Verify.assertContains(UnifiedMapWithHashingStrategyTest.JANEDOE, people);
        Verify.assertContains(UnifiedMapWithHashingStrategyTest.JOHNDOE, people);
        Verify.assertContains(UnifiedMapWithHashingStrategyTest.JANESMITH, people);
        Verify.assertContains(UnifiedMapWithHashingStrategyTest.JOHNSMITH, people);
    }

    @Test
    public void keySet_Iterator_removeFromNonChain() {
        Set<Integer> keys = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 1, 1, 2, 2, 3, 3, 4, 4).keySet();
        Iterator<Integer> keysIterator = keys.iterator();
        keysIterator.next();
        keysIterator.remove();
        Verify.assertSetsEqual(UnifiedSet.newSetWith(2, 3, 4), keys);
    }

    @Test
    public void weakEntryToString() {
        Iterator<Map.Entry<Integer, Integer>> iterator = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 1, 1).entrySet().iterator();
        Map.Entry<Integer, Integer> element = iterator.next();
        Assert.assertEquals("1=1", element.toString());
    }

    @Override
    @Test
    public void valuesCollection_Iterator_remove() {
        // a map with a chain, remove one
        UnifiedMapWithHashingStrategy<Integer, Integer> map = this.mapWithCollisionsOfSize(3);
        Iterator<Integer> iterator = map.iterator();
        iterator.next();
        iterator.remove();
        Verify.assertSize(2, map);
        // remove all values in chain
        iterator.next();
        iterator.remove();
        iterator.next();
        iterator.remove();
        Verify.assertEmpty(map);
    }

    @Test
    public void entry_equals_with_hashingStrategy() {
        UnifiedMapWithHashingStrategy<Integer, Integer> map = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 1, 1, 2, 2, 3, 3, 4, 4);
        Iterator<Map.Entry<Integer, Integer>> entryIterator = map.entrySet().iterator();
        Map.Entry<Integer, Integer> entry = entryIterator.next();
        ImmutableEntry<Integer, Integer> immutableEntry = ImmutableEntry.of(entry.getKey(), entry.getValue());
        Verify.assertEqualsAndHashCode(immutableEntry, entry);
    }

    @Test
    public void entrySet_with_hashing_strategy() {
        Set<Map.Entry<Person, Integer>> entries = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, UnifiedMapWithHashingStrategyTest.JOHNDOE, 1, UnifiedMapWithHashingStrategyTest.JANEDOE, 2, UnifiedMapWithHashingStrategyTest.JOHNSMITH, 3, UnifiedMapWithHashingStrategyTest.JANESMITH, 4).entrySet();
        Verify.assertSize(2, entries);
        Verify.assertContains(ImmutableEntry.of(UnifiedMapWithHashingStrategyTest.JOHNDOE, 2), entries);
        Verify.assertContains(ImmutableEntry.of(UnifiedMapWithHashingStrategyTest.JANEDOE, 2), entries);
        Verify.assertContains(ImmutableEntry.of(UnifiedMapWithHashingStrategyTest.JOHNSMITH, 4), entries);
        Verify.assertContains(ImmutableEntry.of(UnifiedMapWithHashingStrategyTest.JANESMITH, 4), entries);
        Verify.assertNotContains(ImmutableEntry.of(UnifiedMapWithHashingStrategyTest.JOHNDOE, 1), entries);
        Verify.assertNotContains(ImmutableEntry.of(UnifiedMapWithHashingStrategyTest.JANESMITH, 3), entries);
        Assert.assertTrue(entries.remove(ImmutableEntry.of(UnifiedMapWithHashingStrategyTest.JANESMITH, 4)));
        Verify.assertNotContains(ImmutableEntry.of(UnifiedMapWithHashingStrategyTest.JOHNSMITH, 4), entries);
        Assert.assertTrue(entries.remove(ImmutableEntry.of(UnifiedMapWithHashingStrategyTest.JOHNDOE, 2)));
        Verify.assertEmpty(entries);
    }

    @Test
    public void valuesCollection_containsAll() {
        Collection<Integer> values = this.newMapWithKeysValues(1, 1, 2, 2, 3, 3, 4, 4).values();
        Assert.assertTrue(values.containsAll(FastList.newListWith(1, 2)));
        Assert.assertTrue(values.containsAll(FastList.newListWith(1, 2, 3, 4)));
        Assert.assertFalse(values.containsAll(FastList.newListWith(1, 2, 3, 4, 5)));
    }

    @Test
    public void batchForEach() {
        UnifiedMapWithHashingStrategy<String, Integer> map = UnifiedMapWithHashingStrategy.<String, Integer>newMap(UnifiedMapWithHashingStrategyTest.STRING_HASHING_STRATEGY, 5).withKeysValues("1", 1, "2", 2, "3", 3, "4", 4);
        this.batchForEachTestCases(map, 10);
        UnifiedMapWithHashingStrategy<Integer, Integer> collisions = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 5).withKeysValues(UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6);
        this.batchForEachChains(collisions, 21);
        UnifiedMapWithHashingStrategy<Integer, Integer> nulls = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null);
        this.batchForEachNullHandling(nulls, 36);
        this.batchForEachEmptyBatchIterable(UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY));
    }

    @Test
    public void batchForEachKey() {
        Set<Integer> keys = UnifiedMapWithHashingStrategy.<Integer, String>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 5).withKeysValues(1, "1", 2, "2", 3, "3", 4, "4").keySet();
        this.batchForEachTestCases(((BatchIterable<Integer>) (keys)), 10);
        Set<Integer> collisions = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 5).withKeysValues(UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6).keySet();
        this.batchForEachChains(((BatchIterable<Integer>) (collisions)), 57);
        Set<Integer> nulls = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null).keySet();
        this.batchForEachNullHandling(((BatchIterable<Integer>) (nulls)), 16);
        this.batchForEachEmptyBatchIterable(((BatchIterable<Integer>) (UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY).keySet())));
    }

    @Test
    public void batchForEachValue() {
        Collection<Integer> values = UnifiedMapWithHashingStrategy.<String, Integer>newMap(UnifiedMapWithHashingStrategyTest.STRING_HASHING_STRATEGY, 5).withKeysValues("1", 1, "2", 2, "3", 3, "4", 4).values();
        this.batchForEachTestCases(((BatchIterable<Integer>) (values)), 10);
        Collection<Integer> collisions = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 5).withKeysValues(UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6).values();
        this.batchForEachChains(((BatchIterable<Integer>) (collisions)), 21);
        Collection<Integer> nulls = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null).values();
        this.batchForEachNullHandling(((BatchIterable<Integer>) (nulls)), 36);
        this.batchForEachEmptyBatchIterable(((BatchIterable<Integer>) (UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY).values())));
    }

    @Test
    public void batchForEachEntry() {
        // Testing batch size of 1 to 16 with no chains
        BatchIterable<Map.Entry<Integer, Integer>> entries = ((BatchIterable<Map.Entry<Integer, Integer>>) (UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 1, 1, 2, 2, 3, 3, 4, 4).entrySet()));
        for (int sectionCount = 1; sectionCount <= 16; ++sectionCount) {
            Sum sum = new IntegerSum(0);
            for (int sectionIndex = 0; sectionIndex < sectionCount; ++sectionIndex) {
                entries.batchForEach(new UnifiedMapWithHashingStrategyTest.EntrySumProcedure(sum), sectionIndex, sectionCount);
            }
            Assert.assertEquals(20, sum.getValue());
        }
    }

    @Test
    public void batchForEachEntry_chains() {
        BatchIterable<Map.Entry<Integer, Integer>> collisions = ((BatchIterable<Map.Entry<Integer, Integer>>) (UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 5).withKeysValues(UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6).entrySet()));
        // Testing 1 batch with chains
        Sum sum2 = new IntegerSum(0);
        // testing getBatchCount returns 1
        int batchCount = collisions.getBatchCount(100000);
        for (int i = 0; i < batchCount; ++i) {
            collisions.batchForEach(new UnifiedMapWithHashingStrategyTest.EntrySumProcedure(sum2), i, batchCount);
        }
        Assert.assertEquals(1, batchCount);
        Assert.assertEquals(78, sum2.getValue());
        // Testing 3 batches with chains and uneven last batch
        Sum sum3 = new IntegerSum(0);
        for (int i = 0; i < 5; ++i) {
            collisions.batchForEach(new UnifiedMapWithHashingStrategyTest.EntrySumProcedure(sum3), i, 5);
        }
        Assert.assertEquals(78, sum3.getValue());
    }

    @Test
    public void batchForEachEntry_null_handling() {
        // Testing batchForEach handling null keys and null values
        Sum sum4 = new IntegerSum(0);
        BatchIterable<Map.Entry<Integer, Integer>> nulls = ((BatchIterable<Map.Entry<Integer, Integer>>) (UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null).entrySet()));
        int numBatches = nulls.getBatchCount(7);
        for (int i = 0; i < numBatches; ++i) {
            nulls.batchForEach(( each) -> {
                sum4.add(((each.getKey()) == null ? 1 : each.getKey()));
                sum4.add(((each.getValue()) == null ? 1 : each.getValue()));
            }, i, numBatches);
        }
        Assert.assertEquals(52, sum4.getValue());
    }

    @Test
    public void batchForEachEntry_emptySet() {
        // Test batchForEach on empty set, it should simply do nothing and not throw any exceptions
        Sum sum5 = new IntegerSum(0);
        BatchIterable<Map.Entry<Integer, Integer>> empty = ((BatchIterable<Map.Entry<Integer, Integer>>) (UnifiedMapWithHashingStrategy.newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY).entrySet()));
        empty.batchForEach(new UnifiedMapWithHashingStrategyTest.EntrySumProcedure(sum5), 0, empty.getBatchCount(1));
        Assert.assertEquals(0, sum5.getValue());
    }

    @Test
    public void batchIterable_forEach() {
        UnifiedMapWithHashingStrategy<String, Integer> map = UnifiedMapWithHashingStrategy.<String, Integer>newMap(UnifiedMapWithHashingStrategyTest.STRING_HASHING_STRATEGY, 5).withKeysValues("1", 1, "2", 2, "3", 3, "4", 4);
        this.batchIterable_forEach(map, 10);
        UnifiedMapWithHashingStrategy<Integer, Integer> collisions = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 5).withKeysValues(UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6);
        this.batchIterable_forEach(collisions, 21);
        UnifiedMapWithHashingStrategy<Integer, Integer> nulls = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null);
        this.batchIterable_forEachNullHandling(nulls, 33);
        this.batchIterable_forEachEmptyBatchIterable(UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY));
    }

    @Test
    public void batchIterable_forEachKey() {
        Set<Integer> keys = UnifiedMapWithHashingStrategy.<Integer, String>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 5).withKeysValues(1, "1", 2, "2", 3, "3", 4, "4").keySet();
        this.batchIterable_forEach(((BatchIterable<Integer>) (keys)), 10);
        Set<Integer> collisions = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 5).withKeysValues(UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6).keySet();
        this.batchIterable_forEach(((BatchIterable<Integer>) (collisions)), 57);
        Set<Integer> nulls = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null).keySet();
        this.batchIterable_forEachNullHandling(((BatchIterable<Integer>) (nulls)), 15);
        this.batchIterable_forEachEmptyBatchIterable(((BatchIterable<Integer>) (UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY).keySet())));
    }

    @Test
    public void batchIterable_forEachValue() {
        Collection<Integer> values = UnifiedMapWithHashingStrategy.<String, Integer>newMap(UnifiedMapWithHashingStrategyTest.STRING_HASHING_STRATEGY, 5).withKeysValues("1", 1, "2", 2, "3", 3, "4", 4).values();
        this.batchIterable_forEach(((BatchIterable<Integer>) (values)), 10);
        Collection<Integer> collisions = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 5).withKeysValues(UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6).values();
        this.batchIterable_forEach(((BatchIterable<Integer>) (collisions)), 21);
        Collection<Integer> nulls = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null).values();
        this.batchIterable_forEachNullHandling(((BatchIterable<Integer>) (nulls)), 33);
        this.batchIterable_forEachEmptyBatchIterable(((BatchIterable<Integer>) (UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY).values())));
    }

    @Test
    public void batchIterable_forEachEntry() {
        BatchIterable<Map.Entry<Integer, Integer>> entries = ((BatchIterable<Map.Entry<Integer, Integer>>) (UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 1, 1, 2, 2, 3, 3, 4, 4).entrySet()));
        Sum sum = new IntegerSum(0);
        entries.forEach(new UnifiedMapWithHashingStrategyTest.EntrySumProcedure(sum));
        Assert.assertEquals(20, sum.getValue());
    }

    @Test
    public void batchIterable_forEachEntry_chains() {
        BatchIterable<Map.Entry<Integer, Integer>> collisions = ((BatchIterable<Map.Entry<Integer, Integer>>) (UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 5).withKeysValues(UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6).entrySet()));
        Sum sum = new IntegerSum(0);
        collisions.forEach(new UnifiedMapWithHashingStrategyTest.EntrySumProcedure(sum));
        Assert.assertEquals(78, sum.getValue());
    }

    @Test
    public void batchIterable_forEachEntry_null_handling() {
        // Testing batchForEach handling null keys and null values
        Sum sum = new IntegerSum(0);
        BatchIterable<Map.Entry<Integer, Integer>> nulls = ((BatchIterable<Map.Entry<Integer, Integer>>) (UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null).entrySet()));
        nulls.forEach(( each) -> {
            sum.add(((each.getKey()) == null ? 0 : each.getKey()));
            sum.add(((each.getValue()) == null ? 0 : each.getValue()));
        });
        Assert.assertEquals(48, sum.getValue());
    }

    @Test
    public void batchIterable_forEachEntry_emptySet() {
        // Test forEach on empty set, it should simply do nothing and not throw any exceptions
        Sum sum = new IntegerSum(0);
        BatchIterable<Map.Entry<Integer, Integer>> empty = ((BatchIterable<Map.Entry<Integer, Integer>>) (UnifiedMapWithHashingStrategy.newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY).entrySet()));
        empty.forEach(new UnifiedMapWithHashingStrategyTest.EntrySumProcedure(sum));
        Assert.assertEquals(0, sum.getValue());
    }

    @Override
    @Test
    public void forEachKeyValue() {
        super.forEachKeyValue();
        // Testing full chain
        UnifiedSet<Integer> keys = UnifiedSet.newSet();
        UnifiedSet<Integer> values = UnifiedSet.newSet();
        UnifiedMapWithHashingStrategy<Integer, Integer> map = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, UnifiedMapTestCase.COLLISION_4, 4).withKeysValues(1, 5);
        map.forEachKeyValue(( argument1, argument2) -> {
            keys.add(argument1);
            values.add(argument2);
        });
        Verify.assertSetsEqual(UnifiedSet.newSetWith(UnifiedMapTestCase.COLLISION_1, UnifiedMapTestCase.COLLISION_2, UnifiedMapTestCase.COLLISION_3, UnifiedMapTestCase.COLLISION_4, 1), keys);
        Verify.assertSetsEqual(UnifiedSet.newSetWith(1, 2, 3, 4, 5), values);
        // Testing when chain contains null
        UnifiedSet<Integer> keys2 = UnifiedSet.newSet();
        UnifiedSet<Integer> values2 = UnifiedSet.newSet();
        UnifiedMapWithHashingStrategy<Integer, Integer> map2 = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, 1, 4);
        map2.forEachKeyValue(( argument1, argument2) -> {
            keys2.add(argument1);
            values2.add(argument2);
        });
        Verify.assertSetsEqual(UnifiedSet.newSetWith(UnifiedMapTestCase.COLLISION_1, UnifiedMapTestCase.COLLISION_2, UnifiedMapTestCase.COLLISION_3, 1), keys2);
        Verify.assertSetsEqual(UnifiedSet.newSetWith(1, 2, 3, 4), values2);
    }

    @Test
    public void getMapMemoryUsedInWords() {
        UnifiedMapWithHashingStrategy<String, String> map = UnifiedMapWithHashingStrategy.newMap(UnifiedMapWithHashingStrategyTest.STRING_HASHING_STRATEGY);
        Assert.assertEquals(34, map.getMapMemoryUsedInWords());
        map.put("1", "1");
        Assert.assertEquals(34, map.getMapMemoryUsedInWords());
        UnifiedMapWithHashingStrategy<Integer, Integer> map2 = this.mapWithCollisionsOfSize(2);
        Assert.assertEquals(16, map2.getMapMemoryUsedInWords());
    }

    @Test
    public void getHashingStrategy() {
        UnifiedMapWithHashingStrategy<Integer, Object> map = UnifiedMapWithHashingStrategy.newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY);
        Assert.assertSame(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, map.hashingStrategy());
    }

    @Test
    public void getCollidingBuckets() {
        UnifiedMapWithHashingStrategy<Object, Object> map = UnifiedMapWithHashingStrategy.newMap(HashingStrategies.defaultStrategy());
        Assert.assertEquals(0, map.getCollidingBuckets());
        UnifiedMapWithHashingStrategy<Integer, Integer> map2 = this.mapWithCollisionsOfSize(2);
        Assert.assertEquals(1, map2.getCollidingBuckets());
        map2.put(42, 42);
        Assert.assertEquals(1, map2.getCollidingBuckets());
        UnifiedMapWithHashingStrategy<String, String> map3 = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.STRING_HASHING_STRATEGY, "Six", "6", "Bar", "-", "Three", "3", "Five", "5");
        Assert.assertEquals(2, map3.getCollidingBuckets());
    }

    @Override
    @Test
    public void getIfAbsentPut() {
        super.getIfAbsentPut();
        // this map is deliberately small to force a rehash to occur from the put method, in a map with a chained bucket
        UnifiedMapWithHashingStrategy<Integer, Integer> map = UnifiedMapWithHashingStrategy.newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 2, 0.75F);
        UnifiedMapTestCase.MORE_COLLISIONS.forEach(Procedures.cast(( each) -> map.getIfAbsentPut(each, new PassThruFunction0<>(each))));
        Assert.assertEquals(this.mapWithCollisionsOfSize(9), map);
        // Testing getting element present in chain
        UnifiedMapWithHashingStrategy<Integer, Integer> map2 = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, UnifiedMapTestCase.COLLISION_4, 4);
        Assert.assertEquals(2, map2.getIfAbsentPut(UnifiedMapTestCase.COLLISION_2, () -> {
            Assert.fail();
            return null;
        }).intValue());
        // Testing rehashing while creating a new chained key
        UnifiedMapWithHashingStrategy<Integer, Integer> map3 = UnifiedMapWithHashingStrategy.<Integer, Integer>newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 2, 0.75F).withKeysValues(UnifiedMapTestCase.COLLISION_1, 1, 2, 2, 3, 3);
        Assert.assertEquals(4, map3.getIfAbsentPut(UnifiedMapTestCase.COLLISION_2, new com.gs.collections.impl.block.function.PassThruFunction0(4)).intValue());
    }

    @Override
    @Test
    public void getIfAbsentPutValue() {
        super.getIfAbsentPutValue();
        // this map is deliberately small to force a rehash to occur from the put method, in a map with a chained bucket
        UnifiedMapWithHashingStrategy<Integer, Integer> map = UnifiedMapWithHashingStrategy.newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 2, 0.75F);
        UnifiedMapTestCase.MORE_COLLISIONS.forEach(Procedures.cast(( each) -> map.getIfAbsentPut(each, each)));
        Assert.assertEquals(this.mapWithCollisionsOfSize(9), map);
        // Testing getting element present in chain
        UnifiedMapWithHashingStrategy<Integer, Integer> map2 = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, UnifiedMapTestCase.COLLISION_4, 4);
        Assert.assertEquals(Integer.valueOf(2), map2.getIfAbsentPut(UnifiedMapTestCase.COLLISION_2, Integer.valueOf(5)));
        Assert.assertEquals(Integer.valueOf(5), map2.getIfAbsentPut(5, Integer.valueOf(5)));
    }

    @Override
    @Test
    public void getIfAbsentPutWith() {
        super.getIfAbsentPutWith();
        // this map is deliberately small to force a rehash to occur from the put method, in a map with a chained bucket
        UnifiedMapWithHashingStrategy<Integer, Integer> map = UnifiedMapWithHashingStrategy.newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 2, 0.75F);
        UnifiedMapTestCase.MORE_COLLISIONS.forEach(Procedures.cast(( each) -> map.getIfAbsentPutWith(each, Functions.getIntegerPassThru(), each)));
        Assert.assertEquals(this.mapWithCollisionsOfSize(9), map);
        // Testing getting element present in chain
        UnifiedMapWithHashingStrategy<Integer, Integer> map2 = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, UnifiedMapTestCase.COLLISION_4, 4);
        Assert.assertEquals(Integer.valueOf(2), map2.getIfAbsentPutWith(UnifiedMapTestCase.COLLISION_2, Functions.getIntegerPassThru(), Integer.valueOf(5)));
        Assert.assertEquals(Integer.valueOf(5), map2.getIfAbsentPutWith(5, Functions.getIntegerPassThru(), Integer.valueOf(5)));
    }

    @Test
    public void equals_with_hashing_strategy() {
        UnifiedMapWithHashingStrategy<Person, Integer> map1 = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, UnifiedMapWithHashingStrategyTest.JOHNDOE, 1, UnifiedMapWithHashingStrategyTest.JANEDOE, 1, UnifiedMapWithHashingStrategyTest.JOHNSMITH, 1, UnifiedMapWithHashingStrategyTest.JANESMITH, 1);
        UnifiedMapWithHashingStrategy<Person, Integer> map2 = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.FIRST_NAME_HASHING_STRATEGY, UnifiedMapWithHashingStrategyTest.JOHNDOE, 1, UnifiedMapWithHashingStrategyTest.JANEDOE, 1, UnifiedMapWithHashingStrategyTest.JOHNSMITH, 1, UnifiedMapWithHashingStrategyTest.JANESMITH, 1);
        Assert.assertEquals(map1, map2);
        Assert.assertEquals(map2, map1);
        Assert.assertNotEquals(map1.hashCode(), map2.hashCode());
        UnifiedMapWithHashingStrategy<Person, Integer> map3 = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, UnifiedMapWithHashingStrategyTest.JOHNDOE, 1, UnifiedMapWithHashingStrategyTest.JANEDOE, 2, UnifiedMapWithHashingStrategyTest.JOHNSMITH, 3, UnifiedMapWithHashingStrategyTest.JANESMITH, 4);
        UnifiedMapWithHashingStrategy<Person, Integer> map4 = UnifiedMapWithHashingStrategy.newMap(map3);
        HashMap<Person, Integer> hashMap = new HashMap(map3);
        Verify.assertEqualsAndHashCode(map3, map4);
        Assert.assertTrue((((map3.equals(hashMap)) && (hashMap.equals(map3))) && ((map3.hashCode()) != (hashMap.hashCode()))));
        UnifiedMap<Person, Integer> unifiedMap = UnifiedMap.newWithKeysValues(UnifiedMapWithHashingStrategyTest.JOHNDOE, 1, UnifiedMapWithHashingStrategyTest.JANEDOE, 1, UnifiedMapWithHashingStrategyTest.JOHNSMITH, 1, UnifiedMapWithHashingStrategyTest.JANESMITH, 1);
        UnifiedMapWithHashingStrategy<Person, Integer> map5 = UnifiedMapWithHashingStrategy.newMap(UnifiedMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, unifiedMap);
        Assert.assertNotEquals(map5, unifiedMap);
    }

    @Override
    @Test
    public void put() {
        super.put();
        // this map is deliberately small to force a rehash to occur from the put method, in a map with a chained bucket
        UnifiedMapWithHashingStrategy<Integer, Integer> map = UnifiedMapWithHashingStrategy.newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 2, 0.75F);
        UnifiedMapTestCase.COLLISIONS.forEach(0, 4, ( each) -> Assert.assertNull(map.put(each, each)));
        Assert.assertEquals(this.mapWithCollisionsOfSize(5), map);
    }

    @Test
    public void put_get_with_hashing_strategy() {
        UnifiedMapWithHashingStrategy<Integer, Integer> map = UnifiedMapWithHashingStrategy.newMap(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY);
        // Testing putting values in non chains
        Assert.assertNull(map.put(1, 1));
        Assert.assertNull(map.put(2, 2));
        Assert.assertNull(map.put(3, 3));
        Assert.assertNull(map.put(4, 4));
        Assert.assertNull(map.put(5, null));
        // Testing getting values from no chains
        Assert.assertEquals(1, map.get(1).intValue());
        Assert.assertEquals(2, map.get(2).intValue());
        Assert.assertNull(map.get(5));
        // Testing putting and getting elements in a chain
        Assert.assertNull(map.put(UnifiedMapTestCase.COLLISION_1, 1));
        Assert.assertNull(map.get(UnifiedMapTestCase.COLLISION_2));
        Assert.assertNull(map.put(UnifiedMapTestCase.COLLISION_2, 2));
        Assert.assertNull(map.get(UnifiedMapTestCase.COLLISION_3));
        Assert.assertNull(map.put(UnifiedMapTestCase.COLLISION_3, null));
        Assert.assertNull(map.put(UnifiedMapTestCase.COLLISION_4, 4));
        Assert.assertNull(map.put(UnifiedMapTestCase.COLLISION_5, 5));
        Assert.assertEquals(1, map.get(UnifiedMapTestCase.COLLISION_1).intValue());
        Assert.assertEquals(5, map.get(UnifiedMapTestCase.COLLISION_5).intValue());
        Assert.assertNull(map.get(UnifiedMapTestCase.COLLISION_3));
        map.remove(UnifiedMapTestCase.COLLISION_2);
        Assert.assertNull(map.get(UnifiedMapTestCase.COLLISION_2));
        // Testing for casting exceptions
        HashingStrategy<Person> lastName = new HashingStrategy<Person>() {
            public int computeHashCode(Person object) {
                return object.getLastName().hashCode();
            }

            public boolean equals(Person object1, Person object2) {
                return object1.equals(object2);
            }
        };
        UnifiedMapWithHashingStrategy<Person, Integer> map2 = UnifiedMapWithHashingStrategy.newMap(lastName);
        Assert.assertNull(map2.put(new Person("abe", "smith"), 1));
        Assert.assertNull(map2.put(new Person("brad", "smith"), 2));
        Assert.assertNull(map2.put(new Person("charlie", "smith"), 3));
    }

    @Test
    public void hashingStrategy() {
        UnifiedMapWithHashingStrategy<Integer, Integer> map = UnifiedMapWithHashingStrategy.newWithKeysValues(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 1, 1, 2, 2);
        Assert.assertSame(UnifiedMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, map.hashingStrategy());
    }

    private static final class EntrySumProcedure implements Procedure<Map.Entry<Integer, Integer>> {
        private final Sum sum;

        private EntrySumProcedure(Sum sum) {
            this.sum = sum;
        }

        @Override
        public void value(Map.Entry<Integer, Integer> each) {
            this.sum.add(each.getKey());
            this.sum.add(each.getValue());
        }
    }
}

