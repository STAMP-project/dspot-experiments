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


import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.math.IntegerSum;
import com.gs.collections.impl.math.Sum;
import com.gs.collections.impl.parallel.BatchIterable;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class UnifiedMapTest extends UnifiedMapTestCase {
    @Test
    public void newMap_throws() {
        Verify.assertThrows(IllegalArgumentException.class, () -> new UnifiedMap<Integer, Integer>((-1), 0.5F));
        Verify.assertThrows(IllegalArgumentException.class, () -> new UnifiedMap<Integer, Integer>(1, 0.0F));
        Verify.assertThrows(IllegalArgumentException.class, () -> new UnifiedMap<Integer, Integer>(1, (-0.5F)));
        Verify.assertThrows(IllegalArgumentException.class, () -> new UnifiedMap<Integer, Integer>(1, 1.5F));
    }

    @Test
    public void newMapTest() {
        for (int i = 1; i < 17; i++) {
            this.assertPresizedMap(i, 0.75F);
        }
        this.assertPresizedMap(31, 0.75F);
        this.assertPresizedMap(32, 0.75F);
        this.assertPresizedMap(34, 0.75F);
        this.assertPresizedMap(60, 0.75F);
        this.assertPresizedMap(64, 0.7F);
        this.assertPresizedMap(68, 0.7F);
        this.assertPresizedMap(60, 0.7F);
        this.assertPresizedMap(1025, 0.8F);
        this.assertPresizedMap(1024, 0.8F);
        this.assertPresizedMap(1025, 0.8F);
        this.assertPresizedMap(1024, 0.805F);
    }

    @Test
    public void constructorOfPairs() {
        Assert.assertEquals(UnifiedMap.newWithKeysValues(1, "one", 2, "two", 3, "three"), UnifiedMap.newMapWith(Tuples.pair(1, "one"), Tuples.pair(2, "two"), Tuples.pair(3, "three")));
    }

    @Test
    public void constructorOfIterableOfPairs() {
        Pair<Integer, String> pair1 = Tuples.pair(1, "One");
        Pair<Integer, String> pair2 = Tuples.pair(2, "Two");
        Pair<Integer, String> pair3 = Tuples.pair(3, "Three");
        Pair<Integer, String> pair4 = Tuples.pair(4, "Four");
        Assert.assertEquals(UnifiedMap.newMapWith(pair1, pair2, pair3, pair4), UnifiedMap.newMapWith(FastList.newListWith(pair1, pair2, pair3, pair4)));
        Assert.assertEquals(UnifiedMap.newMapWith(pair1, pair2, pair3, pair4), UnifiedMap.newMapWith(UnifiedSet.<Pair<Integer, String>>newSetWith(pair1, pair2, pair3, pair4)));
    }

    @Test
    public void batchForEach() {
        UnifiedMap<String, Integer> map = UnifiedMap.<String, Integer>newMap(5).withKeysValues("1", 1, "2", 2, "3", 3, "4", 4);
        this.batchForEachTestCases(map, 10);
        UnifiedMap<Integer, Integer> collisions = UnifiedMap.<Integer, Integer>newMap(5).withKeysValues(UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6);
        this.batchForEachChains(collisions, 21);
        UnifiedMap<Integer, Integer> nulls = UnifiedMap.<Integer, Integer>newMap(100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null);
        this.batchForEachNullHandling(nulls, 36);
        this.batchForEachEmptyBatchIterable(UnifiedMap.<Integer, Integer>newMap());
    }

    @Test
    public void batchForEachKey() {
        Set<Integer> keys = UnifiedMap.<Integer, String>newMap(5).withKeysValues(1, "1", 2, "2", 3, "3", 4, "4").keySet();
        this.batchForEachTestCases(((BatchIterable<Integer>) (keys)), 10);
        Set<Integer> collisions = UnifiedMap.<Integer, Integer>newMap(5).withKeysValues(UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6).keySet();
        this.batchForEachChains(((BatchIterable<Integer>) (collisions)), 57);
        Set<Integer> nulls = UnifiedMap.<Integer, Integer>newMap(100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null).keySet();
        this.batchForEachNullHandling(((BatchIterable<Integer>) (nulls)), 16);
        this.batchForEachEmptyBatchIterable(((BatchIterable<Integer>) (UnifiedMap.<Integer, Integer>newMap().keySet())));
    }

    @Test
    public void batchForEachValue() {
        Collection<Integer> values = UnifiedMap.<String, Integer>newMap(5).withKeysValues("1", 1, "2", 2, "3", 3, "4", 4).values();
        this.batchForEachTestCases(((BatchIterable<Integer>) (values)), 10);
        Collection<Integer> collisions = UnifiedMap.<Integer, Integer>newMap(5).withKeysValues(UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6).values();
        this.batchForEachChains(((BatchIterable<Integer>) (collisions)), 21);
        Collection<Integer> nulls = UnifiedMap.<Integer, Integer>newMap(100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null).values();
        this.batchForEachNullHandling(((BatchIterable<Integer>) (nulls)), 36);
        this.batchForEachEmptyBatchIterable(((BatchIterable<Integer>) (UnifiedMap.<Integer, Integer>newMap().values())));
    }

    @Test
    public void batchForEachEntry() {
        // Testing batch size of 1 to 16 with no chains
        BatchIterable<Map.Entry<Integer, Integer>> entries = ((BatchIterable<Map.Entry<Integer, Integer>>) (UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3, 4, 4).entrySet()));
        for (int sectionCount = 1; sectionCount <= 16; ++sectionCount) {
            Sum sum = new IntegerSum(0);
            for (int sectionIndex = 0; sectionIndex < sectionCount; ++sectionIndex) {
                entries.batchForEach(new UnifiedMapTest.EntrySumProcedure(sum), sectionIndex, sectionCount);
            }
            Assert.assertEquals(20, sum.getValue());
        }
    }

    @Test
    public void batchForEachEntry_chains() {
        BatchIterable<Map.Entry<Integer, Integer>> collisions = ((BatchIterable<Map.Entry<Integer, Integer>>) (UnifiedMap.<Integer, Integer>newMap(5).withKeysValues(UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6).entrySet()));
        // Testing 1 batch with chains
        Sum sum2 = new IntegerSum(0);
        // testing getBatchCount returns 1
        int numBatches = collisions.getBatchCount(100000);
        for (int i = 0; i < numBatches; ++i) {
            collisions.batchForEach(new UnifiedMapTest.EntrySumProcedure(sum2), i, numBatches);
        }
        Assert.assertEquals(1, numBatches);
        Assert.assertEquals(78, sum2.getValue());
        // Testing 3 batches with chains and uneven last batch
        Sum sum3 = new IntegerSum(0);
        for (int i = 0; i < 5; ++i) {
            collisions.batchForEach(new UnifiedMapTest.EntrySumProcedure(sum3), i, 5);
        }
        Assert.assertEquals(78, sum3.getValue());
    }

    @Test
    public void batchForEachEntry_null_handling() {
        // Testing batchForEach handling null keys and null values
        Sum sum4 = new IntegerSum(0);
        BatchIterable<Map.Entry<Integer, Integer>> nulls = ((BatchIterable<Map.Entry<Integer, Integer>>) (UnifiedMap.<Integer, Integer>newMap(100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null).entrySet()));
        for (int i = 0; i < (nulls.getBatchCount(7)); ++i) {
            nulls.batchForEach(( each) -> {
                sum4.add(((each.getKey()) == null ? 1 : each.getKey()));
                sum4.add(((each.getValue()) == null ? 1 : each.getValue()));
            }, i, nulls.getBatchCount(7));
        }
        Assert.assertEquals(52, sum4.getValue());
    }

    @Test
    public void batchForEachEntry_emptySet() {
        // Test batchForEach on empty set, it should simply do nothing and not throw any exceptions
        Sum sum5 = new IntegerSum(0);
        BatchIterable<Map.Entry<Integer, Integer>> empty = ((BatchIterable<Map.Entry<Integer, Integer>>) (UnifiedMap.newMap().entrySet()));
        empty.batchForEach(new UnifiedMapTest.EntrySumProcedure(sum5), 0, empty.getBatchCount(1));
        Assert.assertEquals(0, sum5.getValue());
    }

    @Test
    public void batchIterable_forEach() {
        UnifiedMap<String, Integer> map = UnifiedMap.<String, Integer>newMap(5).withKeysValues("1", 1, "2", 2, "3", 3, "4", 4);
        this.batchIterable_forEach(map, 10);
        UnifiedMap<Integer, Integer> collisions = UnifiedMap.<Integer, Integer>newMap(5).withKeysValues(UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6);
        this.batchIterable_forEach(collisions, 21);
        UnifiedMap<Integer, Integer> nulls = UnifiedMap.<Integer, Integer>newMap(100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null);
        this.batchIterable_forEachNullHandling(nulls, 33);
        this.batchIterable_forEachEmptyBatchIterable(UnifiedMap.<Integer, Integer>newMap());
    }

    @Test
    public void batchIterable_forEachKey() {
        Set<Integer> keys = UnifiedMap.<Integer, String>newMap(5).withKeysValues(1, "1", 2, "2", 3, "3", 4, "4").keySet();
        this.batchIterable_forEach(((BatchIterable<Integer>) (keys)), 10);
        Set<Integer> collisions = UnifiedMap.<Integer, Integer>newMap(5).withKeysValues(UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6).keySet();
        this.batchIterable_forEach(((BatchIterable<Integer>) (collisions)), 57);
        Set<Integer> nulls = UnifiedMap.<Integer, Integer>newMap(100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null).keySet();
        this.batchIterable_forEachNullHandling(((BatchIterable<Integer>) (nulls)), 15);
        this.batchIterable_forEachEmptyBatchIterable(((BatchIterable<Integer>) (UnifiedMap.<Integer, Integer>newMap().keySet())));
    }

    @Test
    public void batchIterable_forEachValue() {
        Collection<Integer> values = UnifiedMap.<String, Integer>newMap(5).withKeysValues("1", 1, "2", 2, "3", 3, "4", 4).values();
        this.batchIterable_forEach(((BatchIterable<Integer>) (values)), 10);
        Collection<Integer> collisions = UnifiedMap.<Integer, Integer>newMap(5).withKeysValues(UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6).values();
        this.batchIterable_forEach(((BatchIterable<Integer>) (collisions)), 21);
        Collection<Integer> nulls = UnifiedMap.<Integer, Integer>newMap(100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null).values();
        this.batchIterable_forEachNullHandling(((BatchIterable<Integer>) (nulls)), 33);
        this.batchIterable_forEachEmptyBatchIterable(((BatchIterable<Integer>) (UnifiedMap.<Integer, Integer>newMap().values())));
    }

    @Test
    public void batchIterable_forEachEntry() {
        BatchIterable<Map.Entry<Integer, Integer>> entries = ((BatchIterable<Map.Entry<Integer, Integer>>) (UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3, 4, 4).entrySet()));
        Sum sum = new IntegerSum(0);
        entries.forEach(new UnifiedMapTest.EntrySumProcedure(sum));
        Assert.assertEquals(20, sum.getValue());
    }

    @Test
    public void batchIterable_forEachEntry_chains() {
        BatchIterable<Map.Entry<Integer, Integer>> collisions = ((BatchIterable<Map.Entry<Integer, Integer>>) (UnifiedMap.<Integer, Integer>newMap(5).withKeysValues(UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, 1, 4).withKeysValues(2, 5, 3, 6).entrySet()));
        Sum sum = new IntegerSum(0);
        collisions.forEach(new UnifiedMapTest.EntrySumProcedure(sum));
        Assert.assertEquals(78, sum.getValue());
    }

    @Test
    public void batchIterable_forEachEntry_null_handling() {
        // Testing batchForEach handling null keys and null values
        Sum sum = new IntegerSum(0);
        BatchIterable<Map.Entry<Integer, Integer>> nulls = ((BatchIterable<Map.Entry<Integer, Integer>>) (UnifiedMap.<Integer, Integer>newMap(100).withKeysValues(null, 10, 1, null, 2, 11, 3, 12).withKeysValues(4, null, 5, null).entrySet()));
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
        BatchIterable<Map.Entry<Integer, Integer>> empty = ((BatchIterable<Map.Entry<Integer, Integer>>) (UnifiedMap.newMap().entrySet()));
        empty.forEach(new UnifiedMapTest.EntrySumProcedure(sum));
        Assert.assertEquals(0, sum.getValue());
    }

    @Test
    public void getMapMemoryUsedInWords() {
        UnifiedMap<String, String> map = UnifiedMap.newMap();
        Assert.assertEquals(34, map.getMapMemoryUsedInWords());
        map.put("1", "1");
        Assert.assertEquals(34, map.getMapMemoryUsedInWords());
        UnifiedMap<Integer, Integer> map2 = this.mapWithCollisionsOfSize(2);
        Assert.assertEquals(16, map2.getMapMemoryUsedInWords());
    }

    @Test
    public void getCollidingBuckets() {
        UnifiedMap<Object, Object> map = UnifiedMap.newMap();
        Assert.assertEquals(0, map.getCollidingBuckets());
        UnifiedMap<Integer, Integer> map2 = this.mapWithCollisionsOfSize(2);
        Assert.assertEquals(1, map2.getCollidingBuckets());
        map2.put(42, 42);
        Assert.assertEquals(1, map2.getCollidingBuckets());
        UnifiedMap<String, String> map3 = UnifiedMap.newWithKeysValues("Six", "6", "Bar", "-", "Three", "3", "Five", "5");
        Assert.assertEquals(2, map3.getCollidingBuckets());
    }

    @Override
    @Test
    public void getIfAbsentPut() {
        super.getIfAbsentPut();
        // this map is deliberately small to force a rehash to occur from the put method, in a map with a chained bucket
        UnifiedMap<Integer, Integer> map = UnifiedMap.newMap(2, 0.75F);
        UnifiedMapTestCase.COLLISIONS.subList(0, 5).forEach(Procedures.cast(( each) -> map.getIfAbsentPut(each, new PassThruFunction0<>(each))));
        Assert.assertEquals(this.mapWithCollisionsOfSize(5), map);
        // Test getting element present in chain
        UnifiedMap<Integer, Integer> map2 = UnifiedMap.newWithKeysValues(UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, UnifiedMapTestCase.COLLISION_4, 4);
        Assert.assertEquals(Integer.valueOf(3), map2.getIfAbsentPut(UnifiedMapTestCase.COLLISION_3, () -> {
            Assert.fail();
            return null;
        }));
        // Test rehashing while creating a new chained key
        UnifiedMap<Integer, Integer> map3 = UnifiedMap.<Integer, Integer>newMap(2, 0.75F).withKeysValues(1, UnifiedMapTestCase.COLLISION_1, 2, UnifiedMapTestCase.COLLISION_2, 3, UnifiedMapTestCase.COLLISION_3);
        Assert.assertEquals(UnifiedMapTestCase.COLLISION_4, map3.getIfAbsentPut(4, new com.gs.collections.impl.block.function.PassThruFunction0(UnifiedMapTestCase.COLLISION_4)));
        Assert.assertNull(map3.getIfAbsentPut(5, new com.gs.collections.impl.block.function.PassThruFunction0(null)));
    }

    @Override
    @Test
    public void getIfAbsentPut_block_throws() {
        super.getIfAbsentPut_block_throws();
        // this map is deliberately small to force a rehash to occur from the put method, in a map with a chained bucket
        UnifiedMap<Integer, Integer> map = UnifiedMap.newMap(2, 0.75F);
        UnifiedMapTestCase.COLLISIONS.subList(0, 5).forEach(Procedures.cast(( each) -> {
            Verify.assertThrows(.class, () -> map.getIfAbsentPut(each, () -> {
                throw new RuntimeException();
            }));
            map.put(each, each);
        }));
        Assert.assertEquals(this.mapWithCollisionsOfSize(5), map);
    }

    @Override
    @Test
    public void put() {
        super.put();
        // this map is deliberately small to force a rehash to occur from the put method, in a map with a chained bucket
        UnifiedMap<Integer, Integer> map = UnifiedMap.newMap(2, 0.75F);
        UnifiedMapTestCase.COLLISIONS.subList(0, 5).forEach(Procedures.cast(( each) -> Assert.assertNull(map.put(each, each))));
        Assert.assertEquals(this.mapWithCollisionsOfSize(5), map);
    }

    @Override
    @Test
    public void collectValues() {
        super.collectValues();
        UnifiedMap<String, Integer> map = UnifiedMap.<String, Integer>newMap().withKeysValues("1", 1, "2", 2, "3", 3, "4", 4);
        Assert.assertEquals(UnifiedMap.<String, String>newMap(5).withKeysValues("1", "11", "2", "22", "3", "33", "4", "44"), map.collectValues(( key, value) -> key + value));
        UnifiedMap<Integer, Integer> collisions = UnifiedMap.<Integer, Integer>newMap().withKeysValues(UnifiedMapTestCase.COLLISION_1, 1, UnifiedMapTestCase.COLLISION_2, 2, UnifiedMapTestCase.COLLISION_3, 3, 1, 4);
        Assert.assertEquals(UnifiedMap.<Integer, Integer>newMap().withKeysValues(UnifiedMapTestCase.COLLISION_1, ((UnifiedMapTestCase.COLLISION_1) + 1), UnifiedMapTestCase.COLLISION_2, ((UnifiedMapTestCase.COLLISION_2) + 2), UnifiedMapTestCase.COLLISION_3, ((UnifiedMapTestCase.COLLISION_3) + 3), 1, 5), collisions.collectValues(( key, value) -> key + value));
        UnifiedMap<Integer, Integer> nulls = UnifiedMap.<Integer, Integer>newMap().withKeysValues(null, 10, 1, null, 2, 11, 3, 12);
        Assert.assertEquals(UnifiedMap.<Integer, Boolean>newMap().withKeysValues(null, true, 1, true, 2, false, 3, false), nulls.collectValues(( key, value) -> (key == null) || (value == null)));
        UnifiedMap<Integer, Integer> empty = UnifiedMap.<Integer, Integer>newMap();
        Verify.assertEmpty(empty.collectValues(( key, value) -> key + value));
    }

    @Override
    @Test
    public void detect() {
        super.detect();
        UnifiedMap<Integer, String> collisions = UnifiedMap.<Integer, String>newMap().withKeysValues(UnifiedMapTestCase.COLLISION_1, "one", UnifiedMapTestCase.COLLISION_2, "two", UnifiedMapTestCase.COLLISION_3, "three");
        Assert.assertNull(collisions.detect(( key, value) -> (UnifiedMapTestCase.COLLISION_4.equals(key)) && ("four".equals(value))));
        Assert.assertEquals(Tuples.pair(UnifiedMapTestCase.COLLISION_1, "one"), collisions.detect(( key, value) -> (UnifiedMapTestCase.COLLISION_1.equals(key)) && ("one".equals(value))));
    }

    @Override
    @Test
    public void detect_value() {
        super.detect_value();
        UnifiedMap<Integer, String> collisions = UnifiedMap.<Integer, String>newMap().withKeysValues(UnifiedMapTestCase.COLLISION_1, "one", UnifiedMapTestCase.COLLISION_2, "two", UnifiedMapTestCase.COLLISION_3, "three");
        Assert.assertNull(collisions.detect("four"::equals));
        Assert.assertEquals("one", collisions.detect("one"::equals));
    }

    @Override
    @Test
    public void detectWith() {
        super.detectWith();
        UnifiedMap<Integer, String> collisions = UnifiedMap.<Integer, String>newMap().withKeysValues(UnifiedMapTestCase.COLLISION_1, "one", UnifiedMapTestCase.COLLISION_2, "two", UnifiedMapTestCase.COLLISION_3, "three");
        Assert.assertNull(collisions.detectWith((String value,String parameter) -> "value is four".equals((parameter + value)), "value is "));
        Assert.assertEquals("one", collisions.detectWith((String value,String parameter) -> "value is one".equals((parameter + value)), "value is "));
    }

    @Override
    @Test
    public void detectIfNone_value() {
        super.detectIfNone_value();
        UnifiedMap<Integer, String> collisions = UnifiedMap.<Integer, String>newMap().withKeysValues(UnifiedMapTestCase.COLLISION_1, "one", UnifiedMapTestCase.COLLISION_2, "two", UnifiedMapTestCase.COLLISION_3, "three");
        Assert.assertEquals("if none string", collisions.detectIfNone("four"::equals, () -> "if none string"));
        Assert.assertEquals("one", collisions.detectIfNone("one"::equals, () -> "if none string"));
    }

    @Override
    @Test
    public void detectWithIfNone() {
        super.detectWithIfNone();
        UnifiedMap<Integer, String> collisions = UnifiedMap.<Integer, String>newMap().withKeysValues(UnifiedMapTestCase.COLLISION_1, "one", UnifiedMapTestCase.COLLISION_2, "two", UnifiedMapTestCase.COLLISION_3, "three");
        Assert.assertEquals("if none string", collisions.detectWithIfNone((String value,String parameter) -> "value is four".equals((parameter + value)), "value is ", () -> "if none string"));
        Assert.assertEquals("one", collisions.detectWithIfNone((String value,String parameter) -> "value is one".equals((parameter + value)), "value is ", () -> "if none string"));
    }

    @Override
    @Test
    public void anySatisfy() {
        super.anySatisfy();
        UnifiedMap<Integer, String> collisions = UnifiedMap.<Integer, String>newMap().withKeysValues(UnifiedMapTestCase.COLLISION_1, "one", UnifiedMapTestCase.COLLISION_2, "two", UnifiedMapTestCase.COLLISION_3, "three");
        Assert.assertFalse(collisions.anySatisfy("four"::equals));
        Assert.assertTrue(collisions.anySatisfy("one"::equals));
    }

    @Override
    @Test
    public void anySatisfyWith() {
        super.anySatisfyWith();
        UnifiedMap<Integer, String> collisions = UnifiedMap.<Integer, String>newMap().withKeysValues(UnifiedMapTestCase.COLLISION_1, "one", UnifiedMapTestCase.COLLISION_2, "two", UnifiedMapTestCase.COLLISION_3, "three");
        Assert.assertTrue(collisions.anySatisfyWith(( value, parameter) -> "value is one".equals((parameter + value)), "value is "));
        Assert.assertFalse(collisions.anySatisfyWith(( value, parameter) -> "value is four".equals((parameter + value)), "value is "));
    }

    @Override
    @Test
    public void allSatisfy() {
        super.allSatisfy();
        UnifiedMap<Integer, String> collisions = UnifiedMap.<Integer, String>newMap().withKeysValues(UnifiedMapTestCase.COLLISION_1, "one", UnifiedMapTestCase.COLLISION_2, "two", UnifiedMapTestCase.COLLISION_3, "three");
        Assert.assertTrue(collisions.allSatisfy(( value) -> !(value.isEmpty())));
        Assert.assertFalse(collisions.allSatisfy(( value) -> (value.length()) > 3));
    }

    @Override
    @Test
    public void allSatisfyWith() {
        super.allSatisfyWith();
        UnifiedMap<Integer, String> collisions = UnifiedMap.<Integer, String>newMap().withKeysValues(UnifiedMapTestCase.COLLISION_1, "one", UnifiedMapTestCase.COLLISION_2, "two", UnifiedMapTestCase.COLLISION_3, "three");
        Assert.assertTrue(collisions.allSatisfyWith(Predicates2.instanceOf(), String.class));
        Assert.assertFalse(collisions.allSatisfyWith(String::equals, "one"));
    }

    @Override
    @Test
    public void noneSatisfy() {
        super.allSatisfy();
        UnifiedMap<Integer, String> collisions = UnifiedMap.<Integer, String>newMap().withKeysValues(UnifiedMapTestCase.COLLISION_1, "one", UnifiedMapTestCase.COLLISION_2, "two", UnifiedMapTestCase.COLLISION_3, "three");
        Assert.assertTrue(collisions.noneSatisfy("four"::equals));
        Assert.assertFalse(collisions.noneSatisfy("one"::equals));
    }

    @Override
    @Test
    public void noneSatisfyWith() {
        super.allSatisfyWith();
        UnifiedMap<Integer, String> collisions = UnifiedMap.<Integer, String>newMap().withKeysValues(UnifiedMapTestCase.COLLISION_1, "one", UnifiedMapTestCase.COLLISION_2, "two", UnifiedMapTestCase.COLLISION_3, "three");
        Assert.assertTrue(collisions.noneSatisfyWith(String::equals, "monkey"));
        Assert.assertFalse(collisions.allSatisfyWith(String::equals, "one"));
    }

    private static final class EntrySumProcedure implements Procedure<Map.Entry<Integer, Integer>> {
        private static final long serialVersionUID = 1L;

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

