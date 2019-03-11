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
package com.gs.collections.impl.set.mutable;


import Lists.fixedSize;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.Pool;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.math.IntegerSum;
import com.gs.collections.impl.math.Sum;
import com.gs.collections.impl.math.SumProcedure;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.test.domain.Key;
import com.gs.collections.impl.utility.ArrayIterate;
import java.util.Iterator;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test suite for {@link UnifiedSet}.
 */
public class UnifiedSetTest extends AbstractMutableSetTestCase {
    @Override
    @Test
    public void with() {
        Verify.assertEqualsAndHashCode(UnifiedSet.newSetWith("1"), UnifiedSet.newSet().with("1"));
        Verify.assertEqualsAndHashCode(UnifiedSet.newSetWith("1", "2"), UnifiedSet.newSet().with("1", "2"));
        Verify.assertEqualsAndHashCode(UnifiedSet.newSetWith("1", "2", "3"), UnifiedSet.newSet().with("1", "2", "3"));
        Verify.assertEqualsAndHashCode(UnifiedSet.newSetWith("1", "2", "3", "4"), UnifiedSet.newSet().with("1", "2", "3", "4"));
        MutableSet<String> list = UnifiedSet.<String>newSet().with("A").withAll(fixedSize.of("1", "2")).withAll(fixedSize.<String>of()).withAll(Sets.fixedSize.of("3", "4"));
        Verify.assertEqualsAndHashCode(UnifiedSet.newSetWith("A", "1", "2", "3", "4"), list);
    }

    @Test
    public void newSet_throws() {
        Verify.assertThrows(IllegalArgumentException.class, () -> new UnifiedSet<Integer>((-1), 0.5F));
        Verify.assertThrows(IllegalArgumentException.class, () -> new UnifiedSet<Integer>(1, (-0.5F)));
        Verify.assertThrows(IllegalArgumentException.class, () -> new UnifiedSet<Integer>(1, 0.0F));
        Verify.assertThrows(IllegalArgumentException.class, () -> new UnifiedSet<Integer>(1, 1.5F));
    }

    @Test
    public void newSetWithIterable() {
        MutableSet<Integer> integers = UnifiedSet.newSet(Interval.oneTo(3));
        Assert.assertEquals(UnifiedSet.newSetWith(1, 2, 3), integers);
    }

    @Override
    @Test
    public void add() {
        super.add();
        // force rehashing at each step of adding a new colliding entry
        for (int i = 0; i < (AbstractMutableSetTestCase.COLLISIONS.size()); i++) {
            UnifiedSet<Integer> unifiedSet = UnifiedSet.<Integer>newSet(i, 0.75F).withAll(AbstractMutableSetTestCase.COLLISIONS.subList(0, i));
            if (i == 2) {
                unifiedSet.add(Integer.valueOf(1));
            }
            if (i == 4) {
                unifiedSet.add(Integer.valueOf(1));
                unifiedSet.add(Integer.valueOf(2));
            }
            Integer value = AbstractMutableSetTestCase.COLLISIONS.get(i);
            Assert.assertTrue(unifiedSet.add(value));
        }
        // Rehashing Case A: a bucket with only one entry and a low capacity forcing a rehash, where the trigging element goes in the bucket
        // set up a chained bucket
        UnifiedSet<Integer> caseA = UnifiedSet.<Integer>newSet(2).with(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2);
        // clear the bucket to one element
        caseA.remove(AbstractMutableSetTestCase.COLLISION_2);
        // increase the occupied count to the threshold
        caseA.add(Integer.valueOf(1));
        caseA.add(Integer.valueOf(2));
        // add the colliding value back and force the rehash
        Assert.assertTrue(caseA.add(AbstractMutableSetTestCase.COLLISION_2));
        // Rehashing Case B: a bucket with only one entry and a low capacity forcing a rehash, where the triggering element is not in the chain
        // set up a chained bucket
        UnifiedSet<Integer> caseB = UnifiedSet.<Integer>newSet(2).with(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2);
        // clear the bucket to one element
        caseB.remove(AbstractMutableSetTestCase.COLLISION_2);
        // increase the occupied count to the threshold
        caseB.add(Integer.valueOf(1));
        caseB.add(Integer.valueOf(2));
        // add a new value and force the rehash
        Assert.assertTrue(caseB.add(3));
    }

    @Override
    @Test
    public void addAllIterable() {
        super.addAllIterable();
        // test adding a fully populated chained bucket
        MutableSet<Integer> expected = UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4, AbstractMutableSetTestCase.COLLISION_5, AbstractMutableSetTestCase.COLLISION_6, AbstractMutableSetTestCase.COLLISION_7);
        Assert.assertTrue(UnifiedSet.<Integer>newSet().addAllIterable(expected));
        // add an odd-sized collection to a set with a small max to ensure that its capacity is maintained after the operation.
        UnifiedSet<Integer> tiny = UnifiedSet.newSet(0);
        Assert.assertTrue(tiny.addAllIterable(FastList.newListWith(AbstractMutableSetTestCase.COLLISION_1)));
    }

    @Test
    public void get() {
        UnifiedSet<Integer> set = UnifiedSet.<Integer>newSet(AbstractMutableSetTestCase.SIZE).withAll(AbstractMutableSetTestCase.COLLISIONS);
        set.removeAll(AbstractMutableSetTestCase.COLLISIONS);
        for (Integer integer : AbstractMutableSetTestCase.COLLISIONS) {
            Assert.assertNull(set.get(integer));
            Assert.assertNull(set.get(null));
            set.add(integer);
            // noinspection UnnecessaryBoxing,CachedNumberConstructorCall,BoxingBoxedValue
            Assert.assertSame(integer, set.get(new Integer(integer)));
        }
        Assert.assertEquals(AbstractMutableSetTestCase.COLLISIONS.toSet(), set);
        // the pool interface supports getting null keys
        UnifiedSet<Integer> chainedWithNull = UnifiedSet.newSetWith(null, AbstractMutableSetTestCase.COLLISION_1);
        Verify.assertContains(null, chainedWithNull);
        Assert.assertNull(chainedWithNull.get(null));
        // getting a non-existent from a chain with one slot should short-circuit to return null
        UnifiedSet<Integer> chainedWithOneSlot = UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2);
        chainedWithOneSlot.remove(AbstractMutableSetTestCase.COLLISION_2);
        Assert.assertNull(chainedWithOneSlot.get(AbstractMutableSetTestCase.COLLISION_2));
    }

    @Test
    public void put() {
        int size = AbstractMutableSetTestCase.MORE_COLLISIONS.size();
        for (int i = 1; i <= size; i++) {
            Pool<Integer> unifiedSet = UnifiedSet.<Integer>newSet(1).withAll(AbstractMutableSetTestCase.MORE_COLLISIONS.subList(0, (i - 1)));
            Integer newValue = AbstractMutableSetTestCase.MORE_COLLISIONS.get((i - 1));
            Assert.assertSame(newValue, unifiedSet.put(newValue));
            // noinspection UnnecessaryBoxing,CachedNumberConstructorCall,BoxingBoxedValue
            Assert.assertSame(newValue, unifiedSet.put(new Integer(newValue)));
        }
        // assert that all redundant puts into a each position of chain bucket return the original element added
        Pool<Integer> set = UnifiedSet.<Integer>newSet(4).with(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4);
        for (int i = 0; i < (set.size()); i++) {
            Integer value = AbstractMutableSetTestCase.COLLISIONS.get(i);
            Assert.assertSame(value, set.put(value));
        }
        // force rehashing at each step of putting a new colliding entry
        for (int i = 0; i < (AbstractMutableSetTestCase.COLLISIONS.size()); i++) {
            Pool<Integer> pool = UnifiedSet.<Integer>newSet(i).withAll(AbstractMutableSetTestCase.COLLISIONS.subList(0, i));
            if (i == 2) {
                pool.put(Integer.valueOf(1));
            }
            if (i == 4) {
                pool.put(Integer.valueOf(1));
                pool.put(Integer.valueOf(2));
            }
            Integer value = AbstractMutableSetTestCase.COLLISIONS.get(i);
            Assert.assertSame(value, pool.put(value));
        }
        // cover one case not covered in the above: a bucket with only one entry and a low capacity forcing a rehash
        // set up a chained bucket
        Pool<Integer> pool = UnifiedSet.<Integer>newSet(2).with(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2);
        // clear the bucket to one element
        pool.removeFromPool(AbstractMutableSetTestCase.COLLISION_2);
        // increase the occupied count to the threshold
        pool.put(Integer.valueOf(1));
        pool.put(Integer.valueOf(2));
        // put the colliding value back and force the rehash
        Assert.assertSame(AbstractMutableSetTestCase.COLLISION_2, pool.put(AbstractMutableSetTestCase.COLLISION_2));
        // put chained items into a pool without causing a rehash
        Pool<Integer> olympicPool = UnifiedSet.newSet();
        Assert.assertSame(AbstractMutableSetTestCase.COLLISION_1, olympicPool.put(AbstractMutableSetTestCase.COLLISION_1));
        Assert.assertSame(AbstractMutableSetTestCase.COLLISION_2, olympicPool.put(AbstractMutableSetTestCase.COLLISION_2));
    }

    @Test
    public void removeFromPool() {
        Pool<Integer> unifiedSet = UnifiedSet.<Integer>newSet(8).withAll(AbstractMutableSetTestCase.COLLISIONS);
        AbstractMutableSetTestCase.COLLISIONS.reverseForEach(( each) -> {
            Assert.assertNull(unifiedSet.removeFromPool(null));
            Assert.assertSame(each, unifiedSet.removeFromPool(each));
            Assert.assertNull(unifiedSet.removeFromPool(each));
            Assert.assertNull(unifiedSet.removeFromPool(null));
            Assert.assertNull(unifiedSet.removeFromPool(AbstractMutableSetTestCase.COLLISION_10));
        });
        Assert.assertEquals(UnifiedSet.<Integer>newSet(), unifiedSet);
        AbstractMutableSetTestCase.COLLISIONS.forEach(Procedures.cast(( each) -> {
            Pool<Integer> unifiedSet2 = UnifiedSet.<Integer>newSet(8).withAll(AbstractMutableSetTestCase.COLLISIONS);
            Assert.assertNull(unifiedSet2.removeFromPool(null));
            Assert.assertSame(each, unifiedSet2.removeFromPool(each));
            Assert.assertNull(unifiedSet2.removeFromPool(each));
            Assert.assertNull(unifiedSet2.removeFromPool(null));
            Assert.assertNull(unifiedSet2.removeFromPool(AbstractMutableSetTestCase.COLLISION_10));
        }));
        // search a chain for a non-existent element
        Pool<Integer> chain = UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4);
        Assert.assertNull(chain.removeFromPool(AbstractMutableSetTestCase.COLLISION_5));
        // search a deep chain for a non-existent element
        Pool<Integer> deepChain = UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4, AbstractMutableSetTestCase.COLLISION_5, AbstractMutableSetTestCase.COLLISION_6, AbstractMutableSetTestCase.COLLISION_7);
        Assert.assertNull(deepChain.removeFromPool(AbstractMutableSetTestCase.COLLISION_8));
        // search for a non-existent element
        Pool<Integer> empty = UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1);
        Assert.assertNull(empty.removeFromPool(AbstractMutableSetTestCase.COLLISION_2));
    }

    @Test
    public void serialization() {
        int size = AbstractMutableSetTestCase.COLLISIONS.size();
        for (int i = 1; i < size; i++) {
            MutableSet<Integer> set = UnifiedSet.<Integer>newSet(AbstractMutableSetTestCase.SIZE).withAll(AbstractMutableSetTestCase.COLLISIONS.subList(0, i));
            Verify.assertPostSerializedEqualsAndHashCode(set);
            set.add(null);
            Verify.assertPostSerializedEqualsAndHashCode(set);
        }
        UnifiedSet<Integer> nullBucketZero = UnifiedSet.newSetWith(null, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2);
        Verify.assertPostSerializedEqualsAndHashCode(nullBucketZero);
        UnifiedSet<Integer> simpleSetWithNull = UnifiedSet.newSetWith(null, 1, 2);
        Verify.assertPostSerializedEqualsAndHashCode(simpleSetWithNull);
    }

    @Test
    public void null_behavior() {
        UnifiedSet<Integer> unifiedSet = UnifiedSet.<Integer>newSet(10).withAll(AbstractMutableSetTestCase.MORE_COLLISIONS);
        AbstractMutableSetTestCase.MORE_COLLISIONS.clone().reverseForEach(( each) -> {
            Assert.assertTrue(unifiedSet.add(null));
            Assert.assertFalse(unifiedSet.add(null));
            Verify.assertContains(null, unifiedSet);
            Verify.assertPostSerializedEqualsAndHashCode(unifiedSet);
            Assert.assertTrue(unifiedSet.remove(null));
            Assert.assertFalse(unifiedSet.remove(null));
            Verify.assertNotContains(null, unifiedSet);
            Verify.assertPostSerializedEqualsAndHashCode(unifiedSet);
            Assert.assertNull(unifiedSet.put(null));
            Assert.assertNull(unifiedSet.put(null));
            Assert.assertNull(unifiedSet.removeFromPool(null));
            Assert.assertNull(unifiedSet.removeFromPool(null));
            Verify.assertContains(each, unifiedSet);
            Assert.assertTrue(unifiedSet.remove(each));
            Assert.assertFalse(unifiedSet.remove(each));
            Verify.assertNotContains(each, unifiedSet);
        });
    }

    @Override
    @Test
    public void equalsAndHashCode() {
        super.equalsAndHashCode();
        UnifiedSet<Integer> singleCollisionBucket = UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2);
        singleCollisionBucket.remove(AbstractMutableSetTestCase.COLLISION_2);
        Assert.assertEquals(singleCollisionBucket, UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1));
        Verify.assertEqualsAndHashCode(UnifiedSet.newSetWith(null, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3), UnifiedSet.newSetWith(null, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3));
        Verify.assertEqualsAndHashCode(UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, null, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3), UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, null, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3));
        Verify.assertEqualsAndHashCode(UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, null, AbstractMutableSetTestCase.COLLISION_3), UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, null, AbstractMutableSetTestCase.COLLISION_3));
        Verify.assertEqualsAndHashCode(UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, null), UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, null));
    }

    @Test
    public void constructor_from_UnifiedSet() {
        Verify.assertEqualsAndHashCode(new java.util.HashSet(AbstractMutableSetTestCase.MORE_COLLISIONS), UnifiedSet.newSet(AbstractMutableSetTestCase.MORE_COLLISIONS));
    }

    @Test
    public void copyConstructor() {
        // test copying a chained bucket
        MutableSet<Integer> set = UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4, AbstractMutableSetTestCase.COLLISION_5, AbstractMutableSetTestCase.COLLISION_6, AbstractMutableSetTestCase.COLLISION_7);
        Verify.assertEqualsAndHashCode(set, UnifiedSet.newSet(set));
    }

    @Test
    public void newSet() {
        for (int i = 1; i < 17; i++) {
            this.assertPresizedSet(i, 0.75F);
        }
        this.assertPresizedSet(31, 0.75F);
        this.assertPresizedSet(32, 0.75F);
        this.assertPresizedSet(34, 0.75F);
        this.assertPresizedSet(60, 0.75F);
        this.assertPresizedSet(64, 0.7F);
        this.assertPresizedSet(68, 0.7F);
        this.assertPresizedSet(60, 0.7F);
        this.assertPresizedSet(1025, 0.8F);
        this.assertPresizedSet(1024, 0.8F);
        this.assertPresizedSet(1025, 0.8F);
        this.assertPresizedSet(1024, 0.805F);
    }

    @Test
    public void batchForEach() {
        // Testing batch size of 1 to 16 with no chains
        UnifiedSet<Integer> set = UnifiedSet.<Integer>newSet(10).with(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        for (int sectionCount = 1; sectionCount <= 16; ++sectionCount) {
            Sum sum = new IntegerSum(0);
            for (int sectionIndex = 0; sectionIndex < sectionCount; ++sectionIndex) {
                set.batchForEach(new SumProcedure(sum), sectionIndex, sectionCount);
            }
            Assert.assertEquals(55, sum.getValue());
        }
        // Testing 1 batch with chains
        Sum sum2 = new IntegerSum(0);
        UnifiedSet<Integer> set2 = UnifiedSet.<Integer>newSet(3).with(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, 1, 2);
        int numBatches = set2.getBatchCount(100);
        for (int i = 0; i < numBatches; ++i) {
            set2.batchForEach(new SumProcedure(sum2), i, numBatches);
        }
        Assert.assertEquals(1, numBatches);
        Assert.assertEquals(54, sum2.getValue());
        // Testing batch size of 3 with chains and uneven last batch
        Sum sum3 = new IntegerSum(0);
        UnifiedSet<Integer> set3 = UnifiedSet.<Integer>newSet(4, 1.0F).with(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, 1, 2, 3, 4, 5);
        int numBatches2 = set3.getBatchCount(3);
        for (int i = 0; i < numBatches2; ++i) {
            set3.batchForEach(new SumProcedure(sum3), i, numBatches2);
        }
        Assert.assertEquals(32, sum3.getValue());
        // Test batchForEach on empty set, it should simply do nothing and not throw any exceptions
        Sum sum4 = new IntegerSum(0);
        UnifiedSet<Integer> set4 = UnifiedSet.newSet();
        set4.batchForEach(new SumProcedure(sum4), 0, set4.getBatchCount(1));
        Assert.assertEquals(0, sum4.getValue());
    }

    @Override
    @Test
    public void toArray() {
        super.toArray();
        int size = AbstractMutableSetTestCase.COLLISIONS.size();
        for (int i = 1; i < size; i++) {
            MutableSet<Integer> set = UnifiedSet.<Integer>newSet(AbstractMutableSetTestCase.SIZE).withAll(AbstractMutableSetTestCase.COLLISIONS.subList(0, i));
            Object[] objects = set.toArray();
            Assert.assertEquals(set, UnifiedSet.newSetWith(objects));
        }
        MutableSet<Integer> deepChain = UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4, AbstractMutableSetTestCase.COLLISION_5, AbstractMutableSetTestCase.COLLISION_6);
        Assert.assertArrayEquals(new Integer[]{ AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4, AbstractMutableSetTestCase.COLLISION_5, AbstractMutableSetTestCase.COLLISION_6 }, deepChain.toArray());
        MutableSet<Integer> minimumChain = UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2);
        minimumChain.remove(AbstractMutableSetTestCase.COLLISION_2);
        Assert.assertArrayEquals(new Integer[]{ AbstractMutableSetTestCase.COLLISION_1 }, minimumChain.toArray());
        MutableSet<Integer> set = UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4);
        Integer[] target = new Integer[]{ Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1) };
        Integer[] actual = set.toArray(target);
        ArrayIterate.sort(actual, actual.length, Comparators.safeNullsHigh(Integer::compareTo));
        Assert.assertArrayEquals(new Integer[]{ AbstractMutableSetTestCase.COLLISION_1, 1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4, null }, actual);
    }

    @Test
    public void iterator_remove() {
        int size = AbstractMutableSetTestCase.MORE_COLLISIONS.size();
        for (int i = 0; i < size; i++) {
            MutableSet<Integer> actual = UnifiedSet.<Integer>newSet(AbstractMutableSetTestCase.SIZE).withAll(AbstractMutableSetTestCase.MORE_COLLISIONS);
            Iterator<Integer> iterator = actual.iterator();
            for (int j = 0; j <= i; j++) {
                Assert.assertTrue(iterator.hasNext());
                iterator.next();
            }
            iterator.remove();
            MutableSet<Integer> expected = UnifiedSet.newSet(AbstractMutableSetTestCase.MORE_COLLISIONS);
            expected.remove(AbstractMutableSetTestCase.MORE_COLLISIONS.get(i));
            Assert.assertEquals(expected, actual);
        }
        // remove the last element from within a 2-level long chain that is fully populated
        MutableSet<Integer> set = UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4, AbstractMutableSetTestCase.COLLISION_5, AbstractMutableSetTestCase.COLLISION_6, AbstractMutableSetTestCase.COLLISION_7);
        Iterator<Integer> iterator1 = set.iterator();
        for (int i = 0; i < 7; i++) {
            iterator1.next();
        }
        iterator1.remove();
        Assert.assertEquals(UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4, AbstractMutableSetTestCase.COLLISION_5, AbstractMutableSetTestCase.COLLISION_6), set);
        // remove the second-to-last element from a 2-level long chain that that has one empty slot
        Iterator<Integer> iterator2 = set.iterator();
        for (int i = 0; i < 6; i++) {
            iterator2.next();
        }
        iterator2.remove();
        Assert.assertEquals(UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4, AbstractMutableSetTestCase.COLLISION_5), set);
        // Testing removing the last element in a fully populated chained bucket
        MutableSet<Integer> set2 = this.newWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4);
        Iterator<Integer> iterator3 = set2.iterator();
        for (int i = 0; i < 3; ++i) {
            iterator3.next();
        }
        iterator3.next();
        iterator3.remove();
        Verify.assertSetsEqual(UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3), set2);
    }

    @Test
    public void setKeyPreservation() {
        Key key = new Key("key");
        Key duplicateKey1 = new Key("key");
        MutableSet<Key> set1 = UnifiedSet.<Key>newSet().with(key, duplicateKey1);
        Verify.assertSize(1, set1);
        Verify.assertContains(key, set1);
        Assert.assertSame(key, set1.getFirst());
        Key duplicateKey2 = new Key("key");
        MutableSet<Key> set2 = UnifiedSet.<Key>newSet().with(key, duplicateKey1, duplicateKey2);
        Verify.assertSize(1, set2);
        Verify.assertContains(key, set2);
        Assert.assertSame(key, set2.getFirst());
        Key duplicateKey3 = new Key("key");
        MutableSet<Key> set3 = UnifiedSet.<Key>newSet().with(key, new Key("not a dupe"), duplicateKey3);
        Verify.assertSize(2, set3);
        Verify.assertContainsAll(set3, key, new Key("not a dupe"));
        Assert.assertSame(key, set3.detect(key::equals));
    }

    @Test
    public void withSameIfNotModified() {
        UnifiedSet<Integer> integers = UnifiedSet.newSet();
        Assert.assertEquals(UnifiedSet.newSetWith(1, 2), integers.with(1, 2));
        Assert.assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4), integers.with(2, 3, 4));
        Assert.assertSame(integers, integers.with(5, 6, 7));
    }

    @Override
    @Test
    public void retainAll() {
        super.retainAll();
        MutableSet<Object> setWithNull = this.newWith(((Object) (null)));
        Assert.assertFalse(setWithNull.retainAll(FastList.newListWith(((Object) (null)))));
        Assert.assertEquals(UnifiedSet.newSetWith(((Object) (null))), setWithNull);
    }

    @Test(expected = NullPointerException.class)
    public void asParallelNullExecutorService() {
        this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).asParallel(null, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void asParallelLessThanOneBatchSize() {
        this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).asParallel(Executors.newFixedThreadPool(10), 0);
    }
}

