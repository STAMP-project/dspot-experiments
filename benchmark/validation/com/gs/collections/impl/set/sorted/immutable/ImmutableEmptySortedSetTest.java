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
package com.gs.collections.impl.set.sorted.immutable;


import Lists.mutable;
import SortedSets.immutable;
import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.sorted.MutableSortedMap;
import com.gs.collections.api.partition.set.sorted.PartitionImmutableSortedSet;
import com.gs.collections.api.set.sorted.ImmutableSortedSet;
import com.gs.collections.api.set.sorted.MutableSortedSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.PrimitiveFunctions;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.AddToList;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.list.mutable.primitive.ByteArrayList;
import com.gs.collections.impl.list.mutable.primitive.CharArrayList;
import com.gs.collections.impl.list.mutable.primitive.DoubleArrayList;
import com.gs.collections.impl.list.mutable.primitive.FloatArrayList;
import com.gs.collections.impl.list.mutable.primitive.IntArrayList;
import com.gs.collections.impl.list.mutable.primitive.LongArrayList;
import com.gs.collections.impl.list.mutable.primitive.ShortArrayList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.map.sorted.mutable.TreeSortedMap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.set.sorted.mutable.TreeSortedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public class ImmutableEmptySortedSetTest extends AbstractImmutableSortedSetTestCase {
    @Test
    public void testContainsAll() {
        Assert.assertTrue(this.classUnderTest().containsAllIterable(UnifiedSet.<Integer>newSet()));
        Assert.assertFalse(this.classUnderTest().containsAllIterable(UnifiedSet.newSetWith(1)));
    }

    @Test
    public void testNewSortedSet() {
        Assert.assertSame(immutable.of(), immutable.ofAll(FastList.newList()));
        Assert.assertSame(immutable.of(), immutable.ofSortedSet(TreeSortedSet.newSet()));
        Assert.assertNotSame(immutable.of(), immutable.ofSortedSet(TreeSortedSet.newSet(Comparators.reverseNaturalOrder())));
    }

    @Override
    @Test
    public void newWith() {
        Assert.assertEquals(UnifiedSet.newSetWith(1), this.classUnderTest().newWith(1));
        Assert.assertSame(immutable.empty(), immutable.of(FastList.newList().toArray()));
        Assert.assertEquals(immutable.empty(), immutable.of(Comparators.naturalOrder(), FastList.newList().toArray()));
        Assert.assertEquals(Comparators.<Integer>reverseNaturalOrder(), this.classUnderTest(Comparators.<Integer>reverseNaturalOrder()).newWith(1).comparator());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Comparators.<Integer>reverseNaturalOrder(), 1, 2), this.classUnderTest(Comparators.<Integer>reverseNaturalOrder()).newWith(1).newWith(2).castToSortedSet());
    }

    @Override
    @Test
    public void newWithout() {
        Assert.assertEquals(immutable.empty(), immutable.empty().newWithout(1));
        Assert.assertSame(immutable.empty(), immutable.empty().newWithout(1));
        Assert.assertEquals(immutable.empty(), immutable.empty().newWithoutAll(Interval.oneTo(3)));
        Assert.assertSame(immutable.empty(), immutable.empty().newWithoutAll(Interval.oneTo(3)));
        Assert.assertEquals(Comparators.<Integer>reverseNaturalOrder(), this.classUnderTest(Comparators.<Integer>reverseNaturalOrder()).newWithout(1).comparator());
    }

    @Override
    @Test
    public void detect() {
        Assert.assertNull(this.classUnderTest().detect(Integer.valueOf(1)::equals));
    }

    @Override
    @Test
    public void detectWith() {
        Assert.assertNull(this.classUnderTest().detectWith(Object::equals, Integer.valueOf(1)));
    }

    @Override
    @Test
    public void detectIndex() {
        // Any predicate will result in -1
        Assert.assertEquals(Integer.valueOf((-1)), Integer.valueOf(this.classUnderTest().detectIndex(Predicates.alwaysTrue())));
    }

    @Override
    @Test
    public void corresponds() {
        // Evaluates true for all empty sets and false for all non-empty sets
        ImmutableSortedSet<Integer> integers1 = this.classUnderTest();
        Assert.assertTrue(integers1.corresponds(mutable.of(), Predicates2.alwaysFalse()));
        ImmutableSortedSet<Integer> integers2 = integers1.newWith(Integer.valueOf(1));
        Assert.assertFalse(integers2.corresponds(integers1, Predicates2.alwaysTrue()));
    }

    @Override
    @Test
    public void allSatisfy() {
        Assert.assertTrue(this.classUnderTest().allSatisfy(Integer.class::isInstance));
    }

    @Override
    @Test
    public void anySatisfy() {
        Assert.assertFalse(this.classUnderTest().anySatisfy(Integer.class::isInstance));
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void getFirst() {
        this.classUnderTest().getFirst();
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void getLast() {
        this.classUnderTest().getLast();
    }

    @Override
    @Test
    public void isEmpty() {
        Verify.assertIterableEmpty(this.classUnderTest());
        Assert.assertFalse(this.classUnderTest().notEmpty());
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void min() {
        this.classUnderTest().min(Integer::compareTo);
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void max() {
        this.classUnderTest().max(Integer::compareTo);
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void min_without_comparator() {
        this.classUnderTest().min();
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void max_without_comparator() {
        this.classUnderTest().max();
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void minBy() {
        this.classUnderTest().minBy(String::valueOf);
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void maxBy() {
        this.classUnderTest().maxBy(String::valueOf);
    }

    @Override
    @Test
    public void zip() {
        ImmutableSortedSet<Integer> immutableSet = this.classUnderTest(Comparators.reverseNaturalOrder());
        ImmutableList<Pair<Integer, Integer>> pairs = immutableSet.zip(Interval.oneTo(10));
        Assert.assertEquals(FastList.<Pair<Integer, Integer>>newList(), pairs);
        Assert.assertEquals(UnifiedSet.<Pair<Integer, Integer>>newSet(), immutableSet.zip(Interval.oneTo(10), UnifiedSet.<Pair<Integer, Integer>>newSet()));
        ImmutableList<Pair<Integer, Integer>> pairsWithExtras = pairs.newWith(Tuples.pair(1, 5)).newWith(Tuples.pair(5, 1));
        Assert.assertEquals(FastList.newListWith(Tuples.pair(1, 5), Tuples.pair(5, 1)), pairsWithExtras.toList());
    }

    @Override
    @Test
    public void zipWithIndex() {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        ImmutableSortedSet<Pair<Integer, Integer>> pairs = set.zipWithIndex();
        Assert.assertEquals(UnifiedSet.<Pair<Integer, Integer>>newSet(), pairs);
        Assert.assertEquals(UnifiedSet.<Pair<Integer, Integer>>newSet(), set.zipWithIndex(UnifiedSet.<Pair<Integer, Integer>>newSet()));
        Assert.assertNotNull(pairs.comparator());
        ImmutableSortedSet<Pair<Integer, Integer>> pairsWithExtras = pairs.newWith(Tuples.pair(1, 5)).newWith(Tuples.pair(5, 1));
        Assert.assertEquals(FastList.newListWith(Tuples.pair(1, 5), Tuples.pair(5, 1)), pairsWithExtras.toList());
    }

    @Test
    public void chunk() {
        Verify.assertIterableEmpty(this.classUnderTest().chunk(2));
    }

    @Override
    @Test(expected = IllegalArgumentException.class)
    public void chunk_zero_throws() {
        this.classUnderTest().chunk(0);
    }

    @Override
    @Test
    public void chunk_large_size() {
        Verify.assertIterableEmpty(this.classUnderTest().chunk(10));
    }

    @Override
    @Test
    public void union() {
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith("a", "b", "c"), immutable.<String>empty().union(UnifiedSet.newSetWith("a", "b", "c")).castToSortedSet());
        Verify.assertListsEqual(FastList.newListWith(3, 2, 1), this.classUnderTest(Comparators.<Integer>reverseNaturalOrder()).union(UnifiedSet.newSetWith(1, 2, 3)).toList());
    }

    @Override
    @Test
    public void unionInto() {
        Assert.assertEquals(UnifiedSet.newSetWith("a", "b", "c"), immutable.<String>empty().unionInto(UnifiedSet.newSetWith("a", "b", "c"), UnifiedSet.<String>newSet()));
    }

    @Override
    @Test
    public void intersect() {
        Assert.assertEquals(UnifiedSet.<String>newSet(), immutable.<String>empty().intersect(UnifiedSet.newSetWith("1", "2", "3")));
        Assert.assertEquals(Comparators.<Integer>reverseNaturalOrder(), this.classUnderTest(Comparators.<Integer>reverseNaturalOrder()).intersect(UnifiedSet.newSetWith(1, 2, 3)).comparator());
    }

    @Override
    @Test
    public void intersectInto() {
        Assert.assertEquals(UnifiedSet.<String>newSet(), immutable.<String>empty().intersectInto(UnifiedSet.newSetWith("1", "2", "3"), UnifiedSet.<String>newSet()));
    }

    @Override
    @Test
    public void difference() {
        Assert.assertEquals(UnifiedSet.<String>newSet(), immutable.<String>empty().difference(UnifiedSet.newSetWith("not present")));
        Assert.assertEquals(Comparators.<Integer>reverseNaturalOrder(), this.classUnderTest(Comparators.<Integer>reverseNaturalOrder()).difference(UnifiedSet.newSetWith(1, 2, 3)).comparator());
    }

    @Override
    @Test
    public void differenceInto() {
        ImmutableSortedSet<String> set = immutable.empty();
        Assert.assertEquals(UnifiedSet.<String>newSet(), set.differenceInto(UnifiedSet.newSetWith("not present"), UnifiedSet.<String>newSet()));
        Verify.assertListsEqual(FastList.newListWith(3, 2, 1), this.classUnderTest(Comparators.<Integer>reverseNaturalOrder()).union(UnifiedSet.newSetWith(1, 2, 3)).toList());
    }

    @Override
    @Test
    public void symmetricDifference() {
        Assert.assertEquals(UnifiedSet.newSetWith("not present"), immutable.<String>empty().symmetricDifference(UnifiedSet.newSetWith("not present")));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Comparators.<Integer>reverseNaturalOrder(), 1, 2, 3, 4), immutable.of(Comparators.<Integer>reverseNaturalOrder()).symmetricDifference(UnifiedSet.newSetWith(1, 3, 2, 4)).castToSortedSet());
    }

    @Override
    @Test
    public void symmetricDifferenceInto() {
        Assert.assertEquals(UnifiedSet.newSetWith("not present"), immutable.<String>empty().symmetricDifferenceInto(UnifiedSet.newSetWith("not present"), UnifiedSet.<String>newSet()));
    }

    @Override
    @Test
    public void equalsAndHashCode() {
        ImmutableSortedSet<Integer> immutable = this.classUnderTest();
        Verify.assertEqualsAndHashCode(UnifiedSet.newSet(), immutable);
        Verify.assertPostSerializedIdentity(immutable);
        Assert.assertNotEquals(mutable.empty(), immutable);
        ImmutableSortedSet<Integer> setWithComparator = this.classUnderTest(Comparators.reverseNaturalOrder());
        Verify.assertEqualsAndHashCode(UnifiedSet.newSet(), setWithComparator);
        Verify.assertPostSerializedEqualsAndHashCode(setWithComparator);
    }

    @Override
    @Test
    public void contains() {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        Verify.assertNotContains(Integer.valueOf(1), set.castToSortedSet());
        Verify.assertEmpty(set.castToSortedSet());
        Verify.assertThrows(NullPointerException.class, () -> set.contains(null));
    }

    @Override
    @Test
    public void containsAllIterable() {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        Assert.assertFalse(set.containsAllIterable(FastList.newListWith(1, 2, 3)));
        Assert.assertTrue(set.containsAllIterable(set));
    }

    @Override
    @Test
    public void iterator() {
        Iterator<Integer> iterator = this.classUnderTest().iterator();
        Assert.assertFalse(iterator.hasNext());
        Verify.assertThrows(NoSuchElementException.class, ((Runnable) (iterator::next)));
    }

    @Override
    @Test
    public void select() {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        Verify.assertEmpty(set.select(Predicates.lessThan(4)).castToSortedSet());
        Assert.assertEquals(Collections.<Integer>reverseOrder(), set.select(Predicates.lessThan(3)).comparator());
    }

    @Override
    @Test
    public void selectWith() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(integers.selectWith(Predicates2.<Integer>lessThan(), 4).castToSortedSet());
        Assert.assertEquals(Collections.<Integer>reverseOrder(), integers.selectWith(Predicates2.<Integer>lessThan(), 3).comparator());
    }

    @Override
    @Test
    public void reject() {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        Verify.assertEmpty(set.reject(Predicates.lessThan(3)).castToSortedSet());
        Assert.assertEquals(Collections.<Integer>reverseOrder(), set.reject(Predicates.lessThan(3)).comparator());
    }

    @Override
    @Test
    public void rejectWith() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(integers.rejectWith(Predicates2.<Integer>greaterThanOrEqualTo(), 4).castToSortedSet());
        Assert.assertEquals(Collections.<Integer>reverseOrder(), integers.rejectWith(Predicates2.<Integer>greaterThanOrEqualTo(), 4).comparator());
    }

    @Override
    @Test
    public void partition() {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        PartitionImmutableSortedSet<Integer> partition = set.partition(Predicates.lessThan(4));
        Verify.assertIterableEmpty(partition.getSelected());
        Verify.assertIterableEmpty(partition.getRejected());
        Assert.assertEquals(Collections.<Integer>reverseOrder(), partition.getSelected().comparator());
        Assert.assertEquals(Collections.<Integer>reverseOrder(), partition.getRejected().comparator());
    }

    @Override
    @Test
    public void partitionWith() {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        PartitionImmutableSortedSet<Integer> partition = set.partitionWith(Predicates2.<Integer>lessThan(), 4);
        Verify.assertIterableEmpty(partition.getSelected());
        Verify.assertIterableEmpty(partition.getRejected());
        Assert.assertEquals(Collections.<Integer>reverseOrder(), partition.getSelected().comparator());
        Assert.assertEquals(Collections.<Integer>reverseOrder(), partition.getRejected().comparator());
    }

    @Override
    @Test
    public void collect() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertIterableEmpty(integers.collect(Functions.getIntegerPassThru()));
    }

    @Override
    @Test
    public void collectWith() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertIterableEmpty(integers.collectWith(( value, parameter) -> value / parameter, 1));
    }

    @Override
    @Test
    public void collectToTarget() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        ImmutableSortedSet<Integer> collect = integers.collect(Functions.getIntegerPassThru(), TreeSortedSet.newSet(Collections.<Integer>reverseOrder())).toImmutable();
        Verify.assertIterableEmpty(collect);
        Assert.assertEquals(Collections.<Integer>reverseOrder(), collect.comparator());
    }

    @Override
    @Test
    public void partitionWhile() {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        PartitionImmutableSortedSet<Integer> partition = set.partitionWhile(Predicates.lessThan(4));
        Verify.assertIterableEmpty(partition.getSelected());
        Verify.assertIterableEmpty(partition.getRejected());
        Assert.assertEquals(Collections.<Integer>reverseOrder(), partition.getSelected().comparator());
        Assert.assertEquals(Collections.<Integer>reverseOrder(), partition.getRejected().comparator());
    }

    @Override
    @Test
    public void takeWhile() {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        ImmutableSortedSet<Integer> take = set.takeWhile(Predicates.lessThan(4));
        Verify.assertIterableEmpty(take);
        Assert.assertEquals(Collections.<Integer>reverseOrder(), take.comparator());
    }

    @Override
    @Test
    public void dropWhile() {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        ImmutableSortedSet<Integer> drop = set.dropWhile(Predicates.lessThan(4));
        Verify.assertIterableEmpty(drop);
        Assert.assertEquals(Collections.<Integer>reverseOrder(), drop.comparator());
    }

    @Override
    @Test
    public void selectInstancesOf() {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        Verify.assertEmpty(set.selectInstancesOf(Integer.class).castToSortedSet());
        Assert.assertEquals(Collections.<Double>reverseOrder(), set.selectInstancesOf(Double.class).comparator());
    }

    @Override
    @Test
    public void toSortedSet() {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assert.assertNull(set.toSortedSet().comparator());
        Verify.assertEmpty(set.toSortedSet());
    }

    @Override
    @Test
    public void toSortedSetWithComparator() {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        Assert.assertEquals(Collections.<Integer>reverseOrder(), set.toSortedSet(Collections.<Integer>reverseOrder()).comparator());
        Verify.assertEmpty(set.toSortedSet());
    }

    @Test(expected = NoSuchElementException.class)
    public void first() {
        this.classUnderTest().castToSortedSet().first();
    }

    @Test(expected = NoSuchElementException.class)
    public void last() {
        this.classUnderTest().castToSortedSet().last();
    }

    @Test
    public void compareTo() {
        Assert.assertEquals(0, ((long) (this.classUnderTest().compareTo(this.classUnderTest()))));
        Assert.assertEquals((-1), this.classUnderTest().compareTo(TreeSortedSet.newSetWith(1)));
        Assert.assertEquals((-4), this.classUnderTest().compareTo(TreeSortedSet.newSetWith(1, 2, 3, 4)));
    }

    @Override
    @Test
    public void cartesianProduct() {
        LazyIterable<Pair<Integer, Integer>> emptyProduct = this.classUnderTest().cartesianProduct(immutable.of(1, 2, 3));
        Verify.assertEmpty(emptyProduct.toList());
        LazyIterable<Pair<Integer, Integer>> empty2 = this.classUnderTest().cartesianProduct(TreeSortedSet.<Integer>newSet());
        Verify.assertEmpty(empty2.toList());
    }

    @Override
    @Test
    public void indexOf() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Comparators.<Integer>reverseNaturalOrder());
        Assert.assertEquals((-1), integers.indexOf(4));
        Assert.assertEquals((-1), integers.indexOf(3));
        Assert.assertEquals((-1), integers.indexOf(2));
        Assert.assertEquals((-1), integers.indexOf(1));
        Assert.assertEquals((-1), integers.indexOf(0));
        Assert.assertEquals((-1), integers.indexOf(5));
    }

    @Override
    @Test
    public void forEachFromTo() {
        MutableSortedSet<Integer> result = TreeSortedSet.<Integer>newSet(Comparators.reverseNaturalOrder());
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Comparators.<Integer>reverseNaturalOrder());
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach((-1), 0, result::add));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(0, (-1), result::add));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(0, 0, result::add));
    }

    @Override
    @Test
    public void forEachWithIndexWithFromTo() {
        MutableList<Integer> result = mutable.empty();
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Comparators.<Integer>reverseNaturalOrder());
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex((-1), 0, new AddToList(result)));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(0, (-1), new AddToList(result)));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(0, 0, new AddToList(result)));
    }

    @Override
    @Test
    public void powerSet() {
        Verify.assertSize(1, this.classUnderTest().powerSet().castToSortedSet());
        Assert.assertEquals(immutable.of(immutable.<Integer>empty()), this.classUnderTest().powerSet());
    }

    @Override
    @Test
    public void toSortedMap() {
        MutableSortedMap<Integer, Integer> map = this.classUnderTest().toSortedMap(Functions.getIntegerPassThru(), Functions.getIntegerPassThru());
        Verify.assertEmpty(map);
        Verify.assertInstanceOf(TreeSortedMap.class, map);
    }

    @Override
    @Test
    public void toSortedMap_with_comparator() {
        MutableSortedMap<Integer, Integer> map = this.classUnderTest().toSortedMap(Comparators.<Integer>reverseNaturalOrder(), Functions.getIntegerPassThru(), Functions.getIntegerPassThru());
        Verify.assertEmpty(map);
        Verify.assertInstanceOf(TreeSortedMap.class, map);
        Assert.assertEquals(Comparators.<Integer>reverseNaturalOrder(), map.comparator());
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void subSet() {
        this.classUnderTest().castToSortedSet().subSet(1, 4);
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void headSet() {
        this.classUnderTest().castToSortedSet().headSet(4);
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void tailSet() {
        this.classUnderTest().castToSortedSet().tailSet(1);
    }

    @Override
    @Test
    public void collectBoolean() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assert.assertEquals(BooleanArrayList.newListWith(), integers.collectBoolean(PrimitiveFunctions.integerIsPositive()));
    }

    @Override
    @Test
    public void collectByte() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assert.assertEquals(ByteArrayList.newListWith(), integers.collectByte(PrimitiveFunctions.unboxIntegerToByte()));
    }

    @Override
    @Test
    public void collectChar() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assert.assertEquals(CharArrayList.newListWith(), integers.collectChar(( integer) -> ((char) ((integer.intValue()) + 64))));
    }

    @Override
    @Test
    public void collectDouble() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assert.assertEquals(DoubleArrayList.newListWith(), integers.collectDouble(PrimitiveFunctions.unboxIntegerToDouble()));
    }

    @Override
    @Test
    public void collectFloat() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assert.assertEquals(FloatArrayList.newListWith(), integers.collectFloat(PrimitiveFunctions.unboxIntegerToFloat()));
    }

    @Override
    @Test
    public void collectInt() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assert.assertEquals(IntArrayList.newListWith(), integers.collectInt(PrimitiveFunctions.unboxIntegerToInt()));
    }

    @Override
    @Test
    public void collectLong() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assert.assertEquals(LongArrayList.newListWith(), integers.collectLong(PrimitiveFunctions.unboxIntegerToLong()));
    }

    @Override
    @Test
    public void collectShort() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assert.assertEquals(ShortArrayList.newListWith(), integers.collectShort(PrimitiveFunctions.unboxIntegerToShort()));
    }

    @Override
    @Test
    public void groupByUniqueKey_throws() {
        super.groupByUniqueKey_throws();
        Assert.assertEquals(UnifiedMap.newMap().toImmutable(), this.classUnderTest().groupByUniqueKey(( id) -> id));
    }

    @Override
    @Test
    public void groupByUniqueKey_target_throws() {
        super.groupByUniqueKey_target_throws();
        Assert.assertEquals(UnifiedMap.newMap(), this.classUnderTest().groupByUniqueKey(( id) -> id, UnifiedMap.<Integer, Integer>newMap()));
    }

    @Override
    @Test
    public void take() {
        Assert.assertEquals(this.classUnderTest(), this.classUnderTest().take(2));
    }

    @Override
    @Test
    public void drop() {
        Assert.assertEquals(this.classUnderTest(), this.classUnderTest().drop(2));
    }
}

