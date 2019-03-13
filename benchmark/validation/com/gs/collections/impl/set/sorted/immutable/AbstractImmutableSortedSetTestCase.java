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


import AddFunction.INTEGER;
import Lists.mutable;
import SortedSets.immutable;
import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.sorted.MutableSortedMap;
import com.gs.collections.api.multimap.sortedset.ImmutableSortedSetMultimap;
import com.gs.collections.api.partition.set.sorted.PartitionImmutableSortedSet;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.sorted.ImmutableSortedSet;
import com.gs.collections.api.set.sorted.MutableSortedSet;
import com.gs.collections.api.set.sorted.SortedSetIterable;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.function.NegativeIntervalFunction;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.AddToList;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.multimap.set.sorted.TreeSortedSetMultimap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.set.sorted.mutable.TreeSortedSet;
import com.gs.collections.impl.stack.mutable.ArrayStack;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractImmutableSortedSetTestCase {
    @Test(expected = NullPointerException.class)
    public void noSupportForNull() {
        this.classUnderTest().newWith(null);
    }

    @Test
    public void equalsAndHashCode() {
        ImmutableSortedSet<Integer> immutable = this.classUnderTest();
        MutableSortedSet<Integer> mutable = TreeSortedSet.newSet(immutable);
        Verify.assertEqualsAndHashCode(mutable, immutable);
        Verify.assertPostSerializedEqualsAndHashCode(immutable);
        Assert.assertNotEquals(FastList.newList(mutable), immutable);
    }

    @Test
    public void newWith() {
        ImmutableSortedSet<Integer> immutable = this.classUnderTest();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(Interval.fromTo(0, immutable.size())), immutable.newWith(0).castToSortedSet());
        Assert.assertSame(immutable, immutable.newWith(immutable.size()));
        ImmutableSortedSet<Integer> set = this.classUnderTest(Comparators.<Integer>reverseNaturalOrder());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(Comparators.<Integer>reverseNaturalOrder(), Interval.oneTo(((set.size()) + 1))), set.newWith(((set.size()) + 1)).castToSortedSet());
    }

    @Test
    public void newWithout() {
        ImmutableSortedSet<Integer> immutable = this.classUnderTest();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(Interval.oneTo(((immutable.size()) - 1))), immutable.newWithout(immutable.size()).castToSortedSet());
        Assert.assertSame(immutable, immutable.newWithout(((immutable.size()) + 1)));
        ImmutableSortedSet<Integer> set = this.classUnderTest(Comparators.<Integer>reverseNaturalOrder());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(Comparators.<Integer>reverseNaturalOrder(), Interval.oneTo(((set.size()) - 1))), set.newWithout(set.size()).castToSortedSet());
    }

    @Test
    public void newWithAll() {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        ImmutableSortedSet<Integer> withAll = set.newWithAll(UnifiedSet.newSet(Interval.fromTo(1, ((set.size()) + 1))));
        Assert.assertNotEquals(set, withAll);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(Comparators.<Integer>reverseNaturalOrder(), Interval.fromTo(1, ((set.size()) + 1))), withAll.castToSortedSet());
    }

    @Test
    public void newWithoutAll() {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        ImmutableSortedSet<Integer> withoutAll = set.newWithoutAll(set);
        Assert.assertEquals(immutable.<Integer>of(), withoutAll);
        Assert.assertEquals(Sets.immutable.<Integer>of(), withoutAll);
        ImmutableSortedSet<Integer> largeWithoutAll = set.newWithoutAll(Interval.fromTo(101, 150));
        Assert.assertEquals(set, largeWithoutAll);
        ImmutableSortedSet<Integer> largeWithoutAll2 = set.newWithoutAll(UnifiedSet.newSet(Interval.fromTo(151, 199)));
        Assert.assertEquals(set, largeWithoutAll2);
    }

    @Test
    public void contains() {
        ImmutableSortedSet<Integer> set1 = this.classUnderTest();
        for (int i = 1; i <= (set1.size()); i++) {
            Verify.assertContains(i, set1.castToSortedSet());
        }
        Verify.assertNotContains(Integer.valueOf(((set1.size()) + 1)), set1.castToSortedSet());
        SortedSet<Integer> set2 = new java.util.TreeSet(set1.castToSortedSet());
        Verify.assertThrows(ClassCastException.class, () -> set1.contains(new Object()));
        Verify.assertThrows(ClassCastException.class, () -> set2.contains(new Object()));
        Verify.assertThrows(ClassCastException.class, () -> set1.contains("1"));
        Verify.assertThrows(ClassCastException.class, () -> set2.contains("1"));
        Verify.assertThrows(NullPointerException.class, () -> set1.contains(null));
        Verify.assertThrows(NullPointerException.class, () -> set2.contains(null));
    }

    @Test
    public void containsAllArray() {
        ImmutableSortedSet<Integer> set1 = this.classUnderTest();
        SortedSet<Integer> set2 = new java.util.TreeSet(set1.castToSortedSet());
        Assert.assertTrue(set1.containsAllArguments(set1.toArray()));
        Verify.assertThrows(NullPointerException.class, () -> set1.containsAllArguments(null, null));
        Verify.assertThrows(NullPointerException.class, () -> set2.containsAll(FastList.newListWith(null, null)));
    }

    @Test
    public void containsAllIterable() {
        ImmutableSortedSet<Integer> set1 = this.classUnderTest();
        SortedSet<Integer> set2 = new java.util.TreeSet(set1.castToSortedSet());
        Assert.assertTrue(set1.containsAllIterable(Interval.oneTo(set1.size())));
        Verify.assertThrows(NullPointerException.class, () -> set1.containsAllIterable(FastList.newListWith(null, null)));
        Verify.assertThrows(NullPointerException.class, () -> set2.containsAll(FastList.newListWith(null, null)));
    }

    @Test
    public void tap() {
        MutableList<Integer> tapResult = mutable.of();
        ImmutableSortedSet<Integer> collection = this.classUnderTest();
        Assert.assertSame(collection, collection.tap(tapResult::add));
        Assert.assertEquals(collection.toList(), tapResult);
    }

    @Test
    public void forEach() {
        MutableSet<Integer> result = UnifiedSet.newSet();
        ImmutableSortedSet<Integer> collection = this.classUnderTest();
        collection.forEach(CollectionAddProcedure.on(result));
        Assert.assertEquals(collection, result);
    }

    @Test
    public void forEachWith() {
        MutableList<Integer> result = mutable.of();
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        set.forEachWith(( argument1, argument2) -> result.add((argument1 + argument2)), 0);
        Verify.assertListsEqual(result, set.toList());
    }

    @Test
    public void forEachWithIndex() {
        MutableList<Integer> result = mutable.of();
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        set.forEachWithIndex(( object, index) -> result.add(object));
        Verify.assertListsEqual(result, set.toList());
    }

    @Test
    public void select() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertIterableEmpty(integers.select(Predicates.greaterThan(integers.size())));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(Comparators.<Integer>reverseNaturalOrder(), Interval.oneTo(((integers.size()) - 1))), integers.select(Predicates.lessThan(integers.size())).castToSortedSet());
    }

    @Test
    public void selectWith() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertIterableEmpty(integers.selectWith(Predicates2.<Integer>greaterThan(), integers.size()));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(Comparators.<Integer>reverseNaturalOrder(), Interval.oneTo(((integers.size()) - 1))), integers.selectWith(Predicates2.<Integer>lessThan(), integers.size()).castToSortedSet());
    }

    @Test
    public void selectToTarget() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Verify.assertListsEqual(integers.toList(), integers.select(Predicates.lessThan(((integers.size()) + 1)), FastList.<Integer>newList()));
        Verify.assertEmpty(integers.select(Predicates.greaterThan(integers.size()), FastList.<Integer>newList()));
    }

    @Test
    public void reject() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(FastList.newList(integers.reject(Predicates.lessThan(((integers.size()) + 1)))));
        Verify.assertSortedSetsEqual(integers.castToSortedSet(), integers.reject(Predicates.greaterThan(integers.size())).castToSortedSet());
    }

    @Test
    public void rejectWith() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertIterableEmpty(integers.rejectWith(Predicates2.<Integer>lessThanOrEqualTo(), integers.size()));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(Comparators.<Integer>reverseNaturalOrder(), Interval.oneTo(((integers.size()) - 1))), integers.rejectWith(Predicates2.<Integer>greaterThanOrEqualTo(), integers.size()).castToSortedSet());
    }

    @Test
    public void rejectToTarget() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Verify.assertEmpty(integers.reject(Predicates.lessThan(((integers.size()) + 1)), FastList.<Integer>newList()));
        Verify.assertListsEqual(integers.toList(), integers.reject(Predicates.greaterThan(integers.size()), FastList.<Integer>newList()));
    }

    @Test
    public void selectInstancesOf() {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assert.assertEquals(set, set.selectInstancesOf(Integer.class));
        Verify.assertIterableEmpty(set.selectInstancesOf(Double.class));
        Assert.assertEquals(Collections.<Integer>reverseOrder(), set.selectInstancesOf(Integer.class).comparator());
        Assert.assertEquals(Collections.<Double>reverseOrder(), set.selectInstancesOf(Double.class).comparator());
    }

    @Test
    public void partition() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        PartitionImmutableSortedSet<Integer> partition = integers.partition(Predicates.greaterThan(integers.size()));
        Verify.assertIterableEmpty(partition.getSelected());
        Assert.assertEquals(integers, partition.getRejected());
        Assert.assertEquals(Collections.<Integer>reverseOrder(), partition.getSelected().comparator());
        Assert.assertEquals(Collections.<Integer>reverseOrder(), partition.getRejected().comparator());
    }

    @Test
    public void partitionWith() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        PartitionImmutableSortedSet<Integer> partition = integers.partitionWith(Predicates2.<Integer>greaterThan(), integers.size());
        Verify.assertIterableEmpty(partition.getSelected());
        Assert.assertEquals(integers, partition.getRejected());
        Assert.assertEquals(Collections.<Integer>reverseOrder(), partition.getSelected().comparator());
        Assert.assertEquals(Collections.<Integer>reverseOrder(), partition.getRejected().comparator());
    }

    @Test
    public void partitionWhile() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        PartitionImmutableSortedSet<Integer> partition1 = integers.partitionWhile(Predicates.greaterThan(integers.size()));
        Verify.assertIterableEmpty(partition1.getSelected());
        Assert.assertEquals(integers, partition1.getRejected());
        Assert.assertEquals(Collections.<Integer>reverseOrder(), partition1.getSelected().comparator());
        Assert.assertEquals(Collections.<Integer>reverseOrder(), partition1.getRejected().comparator());
        PartitionImmutableSortedSet<Integer> partition2 = integers.partitionWhile(Predicates.lessThanOrEqualTo(integers.size()));
        Assert.assertEquals(integers, partition2.getSelected());
        Verify.assertIterableEmpty(partition2.getRejected());
    }

    @Test
    public void takeWhile() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        ImmutableSortedSet<Integer> take1 = integers.takeWhile(Predicates.greaterThan(integers.size()));
        Verify.assertIterableEmpty(take1);
        Assert.assertEquals(Collections.<Integer>reverseOrder(), take1.comparator());
        ImmutableSortedSet<Integer> take2 = integers.takeWhile(Predicates.lessThanOrEqualTo(integers.size()));
        Assert.assertEquals(integers, take2);
        Assert.assertEquals(Collections.<Integer>reverseOrder(), take2.comparator());
    }

    @Test
    public void dropWhile() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        ImmutableSortedSet<Integer> drop1 = integers.dropWhile(Predicates.greaterThan(integers.size()));
        Assert.assertEquals(integers, drop1);
        Assert.assertEquals(Collections.<Integer>reverseOrder(), drop1.comparator());
        ImmutableSortedSet<Integer> drop2 = integers.dropWhile(Predicates.lessThanOrEqualTo(integers.size()));
        Verify.assertIterableEmpty(drop2);
        Assert.assertEquals(Collections.<Integer>reverseOrder(), drop2.comparator());
    }

    @Test
    public void collect() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertListsEqual(integers.toList(), integers.collect(Functions.getIntegerPassThru()).castToList());
    }

    @Test
    public void collectWith() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertListsEqual(integers.toList(), integers.collectWith(( value, parameter) -> value / parameter, 1).castToList());
    }

    @Test
    public void collectToTarget() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Assert.assertEquals(integers, integers.collect(Functions.getIntegerPassThru(), UnifiedSet.<Integer>newSet()));
        Verify.assertListsEqual(integers.toList(), integers.collect(Functions.getIntegerPassThru(), FastList.<Integer>newList()));
    }

    @Test
    public void flatCollect() {
        ImmutableList<String> actual = this.classUnderTest(Collections.<Integer>reverseOrder()).flatCollect(( integer) -> Lists.fixedSize.of(String.valueOf(integer)));
        ImmutableList<String> expected = this.classUnderTest(Collections.<Integer>reverseOrder()).collect(String::valueOf);
        Assert.assertEquals(expected, actual);
        Verify.assertListsEqual(expected.toList(), actual.toList());
    }

    @Test
    public void flatCollectWithTarget() {
        MutableSet<String> actual = this.classUnderTest().flatCollect(( integer) -> Lists.fixedSize.of(String.valueOf(integer)), UnifiedSet.<String>newSet());
        ImmutableList<String> expected = this.classUnderTest().collect(String::valueOf);
        Verify.assertSetsEqual(expected.toSet(), actual);
    }

    @Test
    public void zip() {
        ImmutableSortedSet<Integer> immutableSet = this.classUnderTest(Collections.<Integer>reverseOrder());
        List<Object> nulls = Collections.nCopies(immutableSet.size(), null);
        List<Object> nullsPlusOne = Collections.nCopies(((immutableSet.size()) + 1), null);
        List<Object> nullsMinusOne = Collections.nCopies(((immutableSet.size()) - 1), null);
        ImmutableList<Pair<Integer, Object>> pairs = immutableSet.zip(nulls);
        Assert.assertEquals(immutableSet.toList(), pairs.collect(((Function<Pair<Integer, ?>, Integer>) (Pair::getOne))));
        Verify.assertListsEqual(FastList.newList(Interval.fromTo(immutableSet.size(), 1)), pairs.collect(((Function<Pair<Integer, ?>, Integer>) (Pair::getOne))).toList());
        Assert.assertEquals(FastList.newList(nulls), pairs.collect(((Function<Pair<?, Object>, Object>) (Pair::getTwo))));
        ImmutableList<Pair<Integer, Object>> pairsPlusOne = immutableSet.zip(nullsPlusOne);
        Assert.assertEquals(immutableSet.toList(), pairsPlusOne.collect(((Function<Pair<Integer, ?>, Integer>) (Pair::getOne))));
        Verify.assertListsEqual(FastList.newList(Interval.fromTo(immutableSet.size(), 1)), pairsPlusOne.collect(((Function<Pair<Integer, ?>, Integer>) (Pair::getOne))).castToList());
        Assert.assertEquals(FastList.newList(nulls), pairsPlusOne.collect(((Function<Pair<?, Object>, Object>) (Pair::getTwo))));
        ImmutableList<Pair<Integer, Object>> pairsMinusOne = immutableSet.zip(nullsMinusOne);
        Verify.assertListsEqual(FastList.newList(Interval.fromTo(immutableSet.size(), 2)), pairsMinusOne.collect(((Function<Pair<Integer, ?>, Integer>) (Pair::getOne))).castToList());
        Assert.assertEquals(immutableSet.zip(nulls), immutableSet.zip(nulls, FastList.<Pair<Integer, Object>>newList()));
        FastList<AbstractImmutableSortedSetTestCase.Holder> holders = FastList.newListWith(new AbstractImmutableSortedSetTestCase.Holder(1), new AbstractImmutableSortedSetTestCase.Holder(2), new AbstractImmutableSortedSetTestCase.Holder(3));
        ImmutableList<Pair<Integer, AbstractImmutableSortedSetTestCase.Holder>> zipped = immutableSet.zip(holders);
        Verify.assertSize(3, zipped.castToList());
        AbstractImmutableSortedSetTestCase.Holder two = new AbstractImmutableSortedSetTestCase.Holder((-1));
        AbstractImmutableSortedSetTestCase.Holder two1 = new AbstractImmutableSortedSetTestCase.Holder((-1));
        Assert.assertEquals(Tuples.pair(10, two1), zipped.newWith(Tuples.pair(10, two)).getLast());
        Assert.assertEquals(Tuples.pair(1, new AbstractImmutableSortedSetTestCase.Holder(3)), this.classUnderTest().zip(holders.reverseThis()).getFirst());
    }

    @Test
    public void zipWithIndex() {
        ImmutableSortedSet<Integer> immutableSet = this.classUnderTest(Collections.<Integer>reverseOrder());
        ImmutableSortedSet<Pair<Integer, Integer>> pairs = immutableSet.zipWithIndex();
        Assert.assertEquals(immutableSet.toList(), pairs.collect(((Function<Pair<Integer, ?>, Integer>) (Pair::getOne))));
        Assert.assertEquals(Interval.zeroTo(((immutableSet.size()) - 1)).toList(), pairs.collect(((Function<Pair<?, Integer>, Integer>) (Pair::getTwo))));
        Assert.assertEquals(immutableSet.zipWithIndex(), immutableSet.zipWithIndex(UnifiedSet.<Pair<Integer, Integer>>newSet()));
        Verify.assertListsEqual(TreeSortedSet.newSet(Collections.<Integer>reverseOrder(), Interval.oneTo(immutableSet.size())).toList(), pairs.collect(((Function<Pair<Integer, ?>, Integer>) (Pair::getOne))).toList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void chunk_zero_throws() {
        this.classUnderTest().chunk(0);
    }

    @Test
    public void chunk_large_size() {
        Assert.assertEquals(this.classUnderTest(), this.classUnderTest().chunk(10).getFirst());
        Verify.assertInstanceOf(ImmutableSortedSet.class, this.classUnderTest().chunk(10).getFirst());
    }

    @Test
    public void detect() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Assert.assertEquals(Integer.valueOf(1), integers.detect(Predicates.equal(1)));
        Assert.assertNull(integers.detect(Predicates.equal(((integers.size()) + 1))));
    }

    @Test
    public void detectWith() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Assert.assertEquals(Integer.valueOf(1), integers.detectWith(Object::equals, Integer.valueOf(1)));
        Assert.assertNull(integers.detectWith(Object::equals, Integer.valueOf(((integers.size()) + 1))));
    }

    @Test
    public void detectWithIfNone() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Function0<Integer> function = new com.gs.collections.impl.block.function.PassThruFunction0(((integers.size()) + 1));
        Integer sum = Integer.valueOf(((integers.size()) + 1));
        Assert.assertEquals(Integer.valueOf(1), integers.detectWithIfNone(Object::equals, Integer.valueOf(1), function));
        Assert.assertEquals(Integer.valueOf(((integers.size()) + 1)), integers.detectWithIfNone(Object::equals, sum, function));
    }

    @Test
    public void detectIfNone() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Function0<Integer> function = new com.gs.collections.impl.block.function.PassThruFunction0(((integers.size()) + 1));
        Assert.assertEquals(Integer.valueOf(1), integers.detectIfNone(Predicates.equal(1), function));
        Assert.assertEquals(Integer.valueOf(((integers.size()) + 1)), integers.detectIfNone(Predicates.equal(((integers.size()) + 1)), function));
    }

    @Test
    public void detectIndex() {
        ImmutableSortedSet<Integer> integers1 = this.classUnderTest();
        Assert.assertEquals(1, integers1.detectIndex(( integer) -> (integer % 2) == 0));
        Assert.assertEquals(0, integers1.detectIndex(( integer) -> (integer % 2) != 0));
        Assert.assertEquals((-1), integers1.detectIndex(( integer) -> (integer % 5) == 0));
        ImmutableSortedSet<Integer> integers2 = this.classUnderTest(Comparators.reverseNaturalOrder());
        Assert.assertEquals(0, integers2.detectIndex(( integer) -> (integer % 2) == 0));
        Assert.assertEquals(1, integers2.detectIndex(( integer) -> (integer % 2) != 0));
        Assert.assertEquals((-1), integers2.detectIndex(( integer) -> (integer % 5) == 0));
    }

    @Test
    public void corresponds() {
        ImmutableSortedSet<Integer> integers1 = this.classUnderTest();
        Assert.assertFalse(integers1.corresponds(this.classUnderTest().newWith(100), Predicates2.alwaysTrue()));
        ImmutableList<Integer> integers2 = integers1.collect(( integers) -> integers + 1);
        Assert.assertTrue(integers1.corresponds(integers2, Predicates2.lessThan()));
        Assert.assertFalse(integers1.corresponds(integers2, Predicates2.greaterThan()));
        ImmutableSortedSet<Integer> integers3 = this.classUnderTest(Comparators.reverseNaturalOrder());
        Assert.assertFalse(integers3.corresponds(integers1, Predicates2.equal()));
    }

    @Test
    public void allSatisfy() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Assert.assertTrue(integers.allSatisfy(Integer.class::isInstance));
        Assert.assertFalse(integers.allSatisfy(Integer.valueOf(0)::equals));
    }

    @Test
    public void anySatisfy() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Assert.assertFalse(integers.anySatisfy(String.class::isInstance));
        Assert.assertTrue(integers.anySatisfy(Integer.class::isInstance));
    }

    @Test
    public void count() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Assert.assertEquals(integers.size(), integers.count(Integer.class::isInstance));
        Assert.assertEquals(0, integers.count(String.class::isInstance));
    }

    @Test
    public void collectIf() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertListsEqual(integers.toList(), integers.collectIf(Integer.class::isInstance, Functions.getIntegerPassThru()).toList());
    }

    @Test
    public void collectIfToTarget() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Verify.assertSetsEqual(integers.toSet(), integers.collectIf(Integer.class::isInstance, Functions.getIntegerPassThru(), UnifiedSet.<Integer>newSet()));
    }

    @Test
    public void getFirst() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Assert.assertEquals(Integer.valueOf(1), integers.getFirst());
        ImmutableSortedSet<Integer> revInt = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assert.assertEquals(Integer.valueOf(revInt.size()), revInt.getFirst());
    }

    @Test
    public void getLast() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Assert.assertEquals(Integer.valueOf(integers.size()), integers.getLast());
        ImmutableSortedSet<Integer> revInt = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assert.assertEquals(Integer.valueOf(1), revInt.getLast());
    }

    @Test
    public void isEmpty() {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        Assert.assertFalse(set.isEmpty());
        Assert.assertTrue(set.notEmpty());
    }

    @Test
    public void iterator() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            Integer integer = iterator.next();
            Assert.assertEquals((i + 1), integer.intValue());
        }
        Verify.assertThrows(NoSuchElementException.class, ((Runnable) (iterator::next)));
        Iterator<Integer> intItr = integers.iterator();
        intItr.next();
        Verify.assertThrows(UnsupportedOperationException.class, intItr::remove);
    }

    @Test
    public void injectInto() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        Integer result = integers.injectInto(0, INTEGER);
        Assert.assertEquals(FastList.newList(integers).injectInto(0, AddFunction.INTEGER_TO_INT), result.intValue());
    }

    @Test
    public void toArray() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        MutableList<Integer> copy = FastList.newList(integers);
        Assert.assertArrayEquals(integers.toArray(), copy.toArray());
        Assert.assertArrayEquals(integers.toArray(new Integer[integers.size()]), copy.toArray(new Integer[integers.size()]));
    }

    @Test
    public void testToString() {
        Assert.assertEquals(FastList.newList(this.classUnderTest()).toString(), this.classUnderTest().toString());
    }

    @Test
    public void makeString() {
        Assert.assertEquals(FastList.newList(this.classUnderTest()).makeString(), this.classUnderTest().makeString());
    }

    @Test
    public void appendString() {
        Appendable builder = new StringBuilder();
        this.classUnderTest().appendString(builder);
        Assert.assertEquals(FastList.newList(this.classUnderTest()).makeString(), builder.toString());
    }

    @Test
    public void toList() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        MutableList<Integer> list = integers.toList();
        Verify.assertEqualsAndHashCode(FastList.newList(integers), list);
    }

    @Test
    public void toSortedList() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        MutableList<Integer> copy = FastList.newList(integers);
        MutableList<Integer> list = integers.toSortedList(Collections.<Integer>reverseOrder());
        Assert.assertEquals(copy.sortThis(Collections.<Integer>reverseOrder()), list);
        MutableList<Integer> list2 = integers.toSortedList();
        Verify.assertListsEqual(copy.sortThis(), list2);
    }

    @Test
    public void toSortedListBy() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        MutableList<Integer> list = integers.toSortedListBy(String::valueOf);
        Assert.assertEquals(integers.toList(), list);
    }

    @Test
    public void toSortedSet() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        MutableSortedSet<Integer> set = integers.toSortedSet();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3, 4), set);
    }

    @Test
    public void toSortedSetWithComparator() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        MutableSortedSet<Integer> set = integers.toSortedSet(Collections.<Integer>reverseOrder());
        Assert.assertEquals(integers.toSet(), set);
        Assert.assertEquals(integers.toSortedList(Comparators.<Integer>reverseNaturalOrder()), set.toList());
    }

    @Test
    public void toSortedSetBy() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        MutableSortedSet<Integer> set = integers.toSortedSetBy(String::valueOf);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(integers), set);
    }

    @Test
    public void toSortedMap() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        MutableSortedMap<Integer, String> map = integers.toSortedMap(Functions.getIntegerPassThru(), String::valueOf);
        Verify.assertMapsEqual(integers.toMap(Functions.getIntegerPassThru(), String::valueOf), map);
        Verify.assertListsEqual(Interval.oneTo(integers.size()), map.keySet().toList());
    }

    @Test
    public void toSortedMap_with_comparator() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        MutableSortedMap<Integer, String> map = integers.toSortedMap(Comparators.<Integer>reverseNaturalOrder(), Functions.getIntegerPassThru(), String::valueOf);
        Verify.assertMapsEqual(integers.toMap(Functions.getIntegerPassThru(), String::valueOf), map);
        Verify.assertListsEqual(Interval.fromTo(integers.size(), 1), map.keySet().toList());
    }

    @Test
    public void forLoop() {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        for (Integer each : set) {
            Assert.assertNotNull(each);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void iteratorRemove() {
        this.classUnderTest().iterator().remove();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void add() {
        this.classUnderTest().castToSortedSet().add(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void remove() {
        this.classUnderTest().castToSortedSet().remove(Integer.valueOf(1));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void clear() {
        this.classUnderTest().castToSortedSet().clear();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeAll() {
        this.classUnderTest().castToSortedSet().removeAll(Lists.fixedSize.of());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void retainAll() {
        this.classUnderTest().castToSortedSet().retainAll(Lists.fixedSize.of());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addAll() {
        this.classUnderTest().castToSortedSet().addAll(Lists.fixedSize.<Integer>of());
    }

    @Test
    public void min() {
        Assert.assertEquals(Integer.valueOf(1), this.classUnderTest().min(Integer::compareTo));
    }

    @Test
    public void max() {
        Assert.assertEquals(Integer.valueOf(1), this.classUnderTest().max(Comparators.reverse(Integer::compareTo)));
    }

    @Test
    public void min_without_comparator() {
        Assert.assertEquals(Integer.valueOf(1), this.classUnderTest().min());
    }

    @Test
    public void max_without_comparator() {
        Assert.assertEquals(Integer.valueOf(this.classUnderTest().size()), this.classUnderTest().max());
    }

    @Test
    public void minBy() {
        Assert.assertEquals(Integer.valueOf(1), this.classUnderTest().minBy(String::valueOf));
    }

    @Test
    public void maxBy() {
        Assert.assertEquals(Integer.valueOf(this.classUnderTest().size()), this.classUnderTest().maxBy(String::valueOf));
    }

    @Test
    public void groupBy() {
        ImmutableSortedSet<Integer> undertest = this.classUnderTest();
        ImmutableSortedSetMultimap<Integer, Integer> actual = undertest.groupBy(Functions.<Integer>getPassThru());
        ImmutableSortedSetMultimap<Integer, Integer> expected = TreeSortedSet.newSet(undertest).groupBy(Functions.<Integer>getPassThru()).toImmutable();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void groupByEach() {
        ImmutableSortedSet<Integer> undertest = this.classUnderTest(Collections.<Integer>reverseOrder());
        NegativeIntervalFunction function = new NegativeIntervalFunction();
        ImmutableSortedSetMultimap<Integer, Integer> actual = undertest.groupByEach(function);
        ImmutableSortedSetMultimap<Integer, Integer> expected = TreeSortedSet.newSet(undertest).groupByEach(function).toImmutable();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void groupByWithTarget() {
        ImmutableSortedSet<Integer> undertest = this.classUnderTest();
        TreeSortedSetMultimap<Integer, Integer> actual = undertest.groupBy(Functions.<Integer>getPassThru(), TreeSortedSetMultimap.<Integer, Integer>newMultimap());
        TreeSortedSetMultimap<Integer, Integer> expected = TreeSortedSet.newSet(undertest).groupBy(Functions.<Integer>getPassThru());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void groupByEachWithTarget() {
        ImmutableSortedSet<Integer> undertest = this.classUnderTest();
        NegativeIntervalFunction function = new NegativeIntervalFunction();
        TreeSortedSetMultimap<Integer, Integer> actual = undertest.groupByEach(function, TreeSortedSetMultimap.<Integer, Integer>newMultimap());
        TreeSortedSetMultimap<Integer, Integer> expected = TreeSortedSet.newSet(undertest).groupByEach(function);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void groupByUniqueKey() {
        Assert.assertEquals(this.classUnderTest().toBag().groupByUniqueKey(( id) -> id), this.classUnderTest().groupByUniqueKey(( id) -> id));
    }

    @Test(expected = IllegalStateException.class)
    public void groupByUniqueKey_throws() {
        this.classUnderTest(Collections.<Integer>reverseOrder()).groupByUniqueKey(Functions.getFixedValue(1));
    }

    @Test
    public void groupByUniqueKey_target() {
        Assert.assertEquals(this.classUnderTest().groupByUniqueKey(( id) -> id), this.classUnderTest().groupByUniqueKey(( id) -> id, UnifiedMap.<Integer, Integer>newMap()));
    }

    @Test(expected = IllegalStateException.class)
    public void groupByUniqueKey_target_throws() {
        this.classUnderTest(Collections.<Integer>reverseOrder()).groupByUniqueKey(( id) -> id, UnifiedMap.newWithKeysValues(2, 2));
    }

    @Test
    public void union() {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        ImmutableSortedSet<Integer> union = set.union(UnifiedSet.newSet(Interval.fromTo(set.size(), ((set.size()) + 3))));
        Verify.assertSize(((set.size()) + 3), union.castToSortedSet());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(Interval.oneTo(((set.size()) + 3))), union.castToSortedSet());
        Assert.assertEquals(set, set.union(UnifiedSet.<Integer>newSet()));
    }

    @Test
    public void unionInto() {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        MutableSet<Integer> union = set.unionInto(UnifiedSet.newSet(Interval.fromTo(set.size(), ((set.size()) + 3))), UnifiedSet.<Integer>newSet());
        Verify.assertSize(((set.size()) + 3), union);
        Assert.assertTrue(union.containsAllIterable(Interval.oneTo(((set.size()) + 3))));
        Assert.assertEquals(set, set.unionInto(UnifiedSet.<Integer>newSetWith(), UnifiedSet.<Integer>newSet()));
    }

    @Test
    public void intersect() {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        ImmutableSortedSet<Integer> intersect = set.intersect(UnifiedSet.newSet(Interval.oneTo(((set.size()) + 2))));
        Verify.assertSize(set.size(), intersect.castToSortedSet());
        Verify.assertSortedSetsEqual(set.castToSortedSet(), intersect.castToSortedSet());
    }

    @Test
    public void intersectInto() {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        MutableSet<Integer> intersect = set.intersectInto(UnifiedSet.newSet(Interval.oneTo(((set.size()) + 2))), UnifiedSet.<Integer>newSet());
        Verify.assertSize(set.size(), intersect);
        Assert.assertEquals(set, intersect);
        Verify.assertEmpty(set.intersectInto(UnifiedSet.newSet(Interval.fromTo(((set.size()) + 1), ((set.size()) + 4))), UnifiedSet.<Integer>newSet()));
    }

    @Test
    public void difference() {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        ImmutableSortedSet<Integer> difference = set.difference(UnifiedSet.newSet(Interval.fromTo(2, ((set.size()) + 1))));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1), difference.castToSortedSet());
        ImmutableSortedSet<Integer> difference2 = set.difference(UnifiedSet.newSet(Interval.fromTo(2, ((set.size()) + 2))));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1), difference2.castToSortedSet());
    }

    @Test
    public void differenceInto() {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        MutableSet<Integer> difference = set.differenceInto(UnifiedSet.newSet(Interval.fromTo(2, ((set.size()) + 1))), UnifiedSet.<Integer>newSet());
        Verify.assertSetsEqual(UnifiedSet.newSetWith(1), difference);
    }

    @Test
    public void symmetricDifference() {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        ImmutableSortedSet<Integer> difference = set.symmetricDifference(UnifiedSet.newSet(Interval.fromTo(2, ((set.size()) + 1))));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Comparators.<Integer>reverseNaturalOrder(), 1, ((set.size()) + 1)), difference.castToSortedSet());
    }

    @Test
    public void symmetricDifferenceInto() {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        MutableSet<Integer> difference = set.symmetricDifferenceInto(UnifiedSet.newSet(Interval.fromTo(2, ((set.size()) + 1))), UnifiedSet.<Integer>newSet());
        Verify.assertSetsEqual(UnifiedSet.newSetWith(1, ((set.size()) + 1)), difference);
    }

    @Test
    public void isSubsetOf() {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        Assert.assertTrue(set.isSubsetOf(set));
    }

    @Test
    public void isProperSubsetOf() {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        Assert.assertTrue(set.isProperSubsetOf(Interval.oneTo(((set.size()) + 1)).toSet()));
        Assert.assertFalse(set.isProperSubsetOf(set));
    }

    @Test
    public void powerSet() {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        ImmutableSortedSet<SortedSetIterable<Integer>> powerSet = set.powerSet();
        Verify.assertSize(((int) (StrictMath.pow(2, set.size()))), powerSet.castToSortedSet());
        Verify.assertContains(UnifiedSet.<String>newSet(), powerSet.toSet());
        Verify.assertContains(set, powerSet.toSet());
        Verify.assertInstanceOf(ImmutableSortedSet.class, powerSet);
        Verify.assertInstanceOf(ImmutableSortedSet.class, powerSet.getLast());
    }

    @Test
    public void cartesianProduct() {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        LazyIterable<Pair<Integer, Integer>> cartesianProduct = set.cartesianProduct(UnifiedSet.newSet(Interval.oneTo(set.size())));
        Assert.assertEquals(((set.size()) * (set.size())), cartesianProduct.size());
        Assert.assertEquals(set, cartesianProduct.select(Predicates.attributeEqual(((Function<Pair<?, Integer>, Integer>) (Pair::getTwo)), 1)).collect(((Function<Pair<Integer, ?>, Integer>) (Pair::getOne))).toSet());
    }

    @Test
    public void distinct() {
        ImmutableSortedSet<Integer> set1 = this.classUnderTest();
        Assert.assertSame(set1, set1.distinct());
        ImmutableSortedSet<Integer> set2 = this.classUnderTest(Comparators.reverseNaturalOrder());
        Assert.assertSame(set2, set2.distinct());
    }

    @Test
    public void indexOf() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Comparators.<Integer>reverseNaturalOrder());
        Assert.assertEquals(0, integers.indexOf(4));
        Assert.assertEquals(1, integers.indexOf(3));
        Assert.assertEquals(2, integers.indexOf(2));
        Assert.assertEquals(3, integers.indexOf(1));
        Assert.assertEquals((-1), integers.indexOf(0));
        Assert.assertEquals((-1), integers.indexOf(5));
    }

    @Test
    public void forEachFromTo() {
        MutableList<Integer> result = FastList.newList();
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Comparators.<Integer>reverseNaturalOrder());
        integers.forEach(1, 2, result::add);
        Assert.assertEquals(Lists.immutable.with(3, 2), result);
        MutableList<Integer> result2 = FastList.newList();
        integers.forEach(0, 3, result2::add);
        Assert.assertEquals(Lists.immutable.with(4, 3, 2, 1), result2);
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach((-1), 0, result::add));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(0, (-1), result::add));
        Verify.assertThrows(IllegalArgumentException.class, () -> integers.forEach(2, 1, result::add));
    }

    @Test
    public void forEachWithIndexWithFromTo() {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Comparators.<Integer>reverseNaturalOrder());
        StringBuilder builder = new StringBuilder();
        integers.forEachWithIndex(1, 2, ( each, index) -> builder.append(each).append(index));
        Assert.assertEquals("3122", builder.toString());
        MutableList<Integer> result2 = mutable.of();
        integers.forEachWithIndex(0, 3, new AddToList(result2));
        Assert.assertEquals(Lists.immutable.with(4, 3, 2, 1), result2);
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex((-1), 0, new AddToList(result2)));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(0, (-1), new AddToList(result2)));
        Verify.assertThrows(IllegalArgumentException.class, () -> integers.forEachWithIndex(2, 1, new AddToList(result2)));
    }

    @Test
    public void toStack() {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Comparators.reverseNaturalOrder());
        Assert.assertEquals(ArrayStack.newStackWith(4, 3, 2, 1), set.toStack());
    }

    @Test
    public void toImmutable() {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        ImmutableSortedSet<Integer> actual = set.toImmutable();
        Assert.assertEquals(set, actual);
        Assert.assertSame(set, actual);
    }

    @Test
    public void take() {
        ImmutableSortedSet<Integer> integers1 = this.classUnderTest();
        Assert.assertEquals(immutable.of(integers1.comparator()), integers1.take(0));
        Assert.assertSame(integers1.comparator(), integers1.take(0).comparator());
        Assert.assertEquals(immutable.of(integers1.comparator(), 1, 2, 3), integers1.take(3));
        Assert.assertSame(integers1.comparator(), integers1.take(3).comparator());
        Assert.assertEquals(immutable.of(integers1.comparator(), 1, 2, 3), integers1.take(((integers1.size()) - 1)));
        ImmutableSortedSet<Integer> integers2 = this.classUnderTest(Comparators.reverseNaturalOrder());
        Assert.assertSame(integers2, integers2.take(integers2.size()));
        Assert.assertSame(integers2, integers2.take(10));
        Assert.assertSame(integers2, integers2.take(Integer.MAX_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void take_throws() {
        this.classUnderTest().take((-1));
    }

    @Test
    public void drop() {
        ImmutableSortedSet<Integer> integers1 = this.classUnderTest();
        Assert.assertSame(integers1, integers1.drop(0));
        Assert.assertSame(integers1.comparator(), integers1.drop(0).comparator());
        Assert.assertEquals(immutable.of(integers1.comparator(), 4), integers1.drop(3));
        Assert.assertSame(integers1.comparator(), integers1.drop(3).comparator());
        Assert.assertEquals(immutable.of(integers1.comparator(), 4), integers1.drop(((integers1.size()) - 1)));
        ImmutableSortedSet<Integer> expectedSet = immutable.of(Comparators.reverseNaturalOrder());
        ImmutableSortedSet<Integer> integers2 = this.classUnderTest(Comparators.reverseNaturalOrder());
        Assert.assertEquals(expectedSet, integers2.drop(integers2.size()));
        Assert.assertEquals(expectedSet, integers2.drop(10));
        Assert.assertEquals(expectedSet, integers2.drop(Integer.MAX_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void drop_throws() {
        this.classUnderTest().drop((-1));
    }

    private static final class Holder {
        private final int number;

        private Holder(int i) {
            this.number = i;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((this.getClass()) != (o.getClass()))) {
                return false;
            }
            AbstractImmutableSortedSetTestCase.Holder holder = ((AbstractImmutableSortedSetTestCase.Holder) (o));
            return (this.number) == (holder.number);
        }

        @Override
        public int hashCode() {
            return this.number;
        }

        @Override
        public String toString() {
            return String.valueOf(this.number);
        }
    }
}

