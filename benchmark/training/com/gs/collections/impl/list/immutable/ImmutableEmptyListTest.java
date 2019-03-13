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
package com.gs.collections.impl.list.immutable;


import Lists.immutable;
import Lists.mutable;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.ListIterable;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.partition.list.PartitionImmutableList;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.block.factory.ObjectIntProcedures;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.collection.immutable.AbstractImmutableCollectionTestCase;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public class ImmutableEmptyListTest extends AbstractImmutableListTestCase {
    @Override
    @Test
    public void indexOf() {
        Assert.assertEquals((-1), this.classUnderTest().indexOf(1));
        Assert.assertEquals((-1), this.classUnderTest().indexOf(null));
        ImmutableList<Integer> immutableList = this.classUnderTest().newWith(null);
        Assert.assertEquals(((immutableList.size()) - 1), immutableList.indexOf(null));
        Assert.assertEquals((-1), this.classUnderTest().indexOf(Integer.MAX_VALUE));
    }

    @Override
    @Test
    public void lastIndexOf() {
        Assert.assertEquals((-1), this.classUnderTest().lastIndexOf(1));
        Assert.assertEquals((-1), this.classUnderTest().lastIndexOf(null));
        Assert.assertEquals((-1), this.classUnderTest().lastIndexOf(null));
        ImmutableList<Integer> immutableList = this.classUnderTest().newWith(null);
        Assert.assertEquals(((immutableList.size()) - 1), immutableList.lastIndexOf(null));
        Assert.assertEquals((-1), this.classUnderTest().lastIndexOf(Integer.MAX_VALUE));
    }

    @Test
    public void newWithout() {
        Assert.assertSame(immutable.of(), immutable.of().newWithout(1));
        Assert.assertSame(immutable.of(), immutable.of().newWithoutAll(Interval.oneTo(3)));
    }

    @Override
    @Test
    public void reverseForEach() {
        ImmutableList<Integer> list = immutable.of();
        MutableList<Integer> result = mutable.of();
        list.reverseForEach(CollectionAddProcedure.on(result));
        Assert.assertEquals(list, result);
    }

    @Override
    @Test
    public void forEachFromTo() {
        MutableList<Integer> result = mutable.of();
        MutableList<Integer> reverseResult = mutable.of();
        ImmutableList<Integer> list = this.classUnderTest();
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> list.forEach(0, ((list.size()) - 1), CollectionAddProcedure.on(result)));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> list.forEach(((list.size()) - 1), 0, CollectionAddProcedure.on(reverseResult)));
    }

    @Override
    @Test
    public void forEachWithIndexFromTo() {
        MutableList<Integer> result = mutable.of();
        MutableList<Integer> reverseResult = mutable.of();
        ImmutableList<Integer> list = this.classUnderTest();
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> list.forEachWithIndex(0, ((list.size()) - 1), ObjectIntProcedures.fromProcedure(CollectionAddProcedure.on(result))));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> list.forEachWithIndex(((list.size()) - 1), 0, ObjectIntProcedures.fromProcedure(CollectionAddProcedure.on(reverseResult))));
    }

    @Override
    @Test
    public void detect() {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assert.assertNull(integers.detect(Integer.valueOf(1)::equals));
    }

    @Override
    @Test
    public void detectWith() {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assert.assertNull(integers.detectWith(Object::equals, Integer.valueOf(1)));
    }

    @Override
    @Test
    public void distinct() {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assert.assertNotNull(integers.distinct());
        Assert.assertTrue(integers.isEmpty());
    }

    @Override
    @Test
    public void distinctWithHashingStrategy() {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assert.assertNotNull(integers.distinct(HashingStrategies.defaultStrategy()));
        Assert.assertTrue(integers.isEmpty());
    }

    @Override
    @Test
    public void countWith() {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assert.assertEquals(0, integers.countWith(AbstractImmutableCollectionTestCase.ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void corresponds() {
        // Evaluates true for all empty lists and false for all non-empty lists
        Assert.assertTrue(this.classUnderTest().corresponds(mutable.of(), Predicates2.alwaysFalse()));
        ImmutableList<Integer> integers = this.classUnderTest().newWith(Integer.valueOf(1));
        Assert.assertFalse(this.classUnderTest().corresponds(integers, Predicates2.alwaysTrue()));
    }

    @Override
    @Test
    public void allSatisfy() {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assert.assertTrue(integers.allSatisfy(AbstractImmutableCollectionTestCase.ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void anySatisfy() {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assert.assertFalse(integers.anySatisfy(AbstractImmutableCollectionTestCase.ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void getFirst() {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assert.assertNull(integers.getFirst());
    }

    @Override
    @Test
    public void getLast() {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assert.assertNull(integers.getLast());
    }

    @Override
    @Test
    public void isEmpty() {
        ImmutableList<Integer> list = this.classUnderTest();
        Assert.assertTrue(list.isEmpty());
        Assert.assertFalse(list.notEmpty());
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

    @Test
    @Override
    public void min_null_throws() {
        // Not applicable for empty collections
        super.min_null_throws();
    }

    @Test
    @Override
    public void max_null_throws() {
        // Not applicable for empty collections
        super.max_null_throws();
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

    @Test
    @Override
    public void min_null_throws_without_comparator() {
        // Not applicable for empty collections
        super.min_null_throws_without_comparator();
    }

    @Test
    @Override
    public void max_null_throws_without_comparator() {
        // Not applicable for empty collections
        super.max_null_throws_without_comparator();
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
    @Test(expected = IndexOutOfBoundsException.class)
    public void subList() {
        this.classUnderTest().subList(0, 1);
    }

    @Override
    @Test
    public void zip() {
        ImmutableList<Integer> immutableList = this.classUnderTest();
        List<Object> nulls = Collections.nCopies(immutableList.size(), null);
        List<Object> nullsPlusOne = Collections.nCopies(((immutableList.size()) + 1), null);
        ImmutableList<Pair<Integer, Object>> pairs = immutableList.zip(nulls);
        Assert.assertEquals(immutableList, pairs.collect(((Function<Pair<Integer, ?>, Integer>) (Pair::getOne))));
        Assert.assertEquals(nulls, pairs.collect(((Function<Pair<?, Object>, Object>) (Pair::getTwo))));
        ImmutableList<Pair<Integer, Object>> pairsPlusOne = immutableList.zip(nullsPlusOne);
        Assert.assertEquals(immutableList, pairsPlusOne.collect(((Function<Pair<Integer, ?>, Integer>) (Pair::getOne))));
        Assert.assertEquals(nulls, pairsPlusOne.collect(((Function<Pair<?, Object>, Object>) (Pair::getTwo))));
        Assert.assertEquals(immutableList.zip(nulls), immutableList.zip(nulls, FastList.<Pair<Integer, Object>>newList()));
    }

    @Override
    @Test
    public void zipWithIndex() {
        ImmutableList<Integer> immutableList = this.classUnderTest();
        ImmutableList<Pair<Integer, Integer>> pairs = immutableList.zipWithIndex();
        Assert.assertEquals(immutableList, pairs.collect(((Function<Pair<Integer, ?>, Integer>) (Pair::getOne))));
        Assert.assertEquals(FastList.<Integer>newList(), pairs.collect(((Function<Pair<?, Integer>, Integer>) (Pair::getTwo))));
        Assert.assertEquals(immutableList.zipWithIndex(), immutableList.zipWithIndex(FastList.<Pair<Integer, Integer>>newList()));
    }

    @Test
    public void chunk() {
        Assert.assertEquals(mutable.of(), this.classUnderTest().chunk(2));
    }

    @Override
    @Test(expected = IllegalArgumentException.class)
    public void chunk_zero_throws() {
        this.classUnderTest().chunk(0);
    }

    @Override
    @Test
    public void chunk_large_size() {
        Assert.assertEquals(this.classUnderTest(), this.classUnderTest().chunk(10));
        Verify.assertInstanceOf(ImmutableList.class, this.classUnderTest().chunk(10));
    }

    @Override
    @Test
    public void equalsAndHashCode() {
        ImmutableList<Integer> immutable = this.classUnderTest();
        MutableList<Integer> mutable = FastList.newList(immutable);
        Verify.assertEqualsAndHashCode(immutable, mutable);
        Verify.assertPostSerializedIdentity(immutable);
        Assert.assertNotEquals(immutable, UnifiedSet.newSet(mutable));
    }

    @Override
    @Test
    public void take() {
        ImmutableList<Integer> immutableList = this.classUnderTest();
        Assert.assertSame(immutableList, immutableList.take(0));
        Assert.assertSame(immutableList, immutableList.take(10));
        Assert.assertSame(immutableList, immutableList.take(Integer.MAX_VALUE));
    }

    @Override
    @Test
    public void takeWhile() {
        Assert.assertEquals(immutable.of(), this.classUnderTest().takeWhile(( ignored) -> true));
        Assert.assertEquals(immutable.of(), this.classUnderTest().takeWhile(( ignored) -> false));
    }

    @Override
    @Test
    public void drop() {
        super.drop();
        ImmutableList<Integer> immutableList = this.classUnderTest();
        Assert.assertSame(immutableList, immutableList.drop(10));
        Assert.assertSame(immutableList, immutableList.drop(0));
        Assert.assertSame(immutableList, immutableList.drop(Integer.MAX_VALUE));
    }

    @Override
    @Test
    public void dropWhile() {
        super.dropWhile();
        Assert.assertEquals(immutable.of(), this.classUnderTest().dropWhile(( ignored) -> true));
        Assert.assertEquals(immutable.of(), this.classUnderTest().dropWhile(( ignored) -> false));
    }

    @Override
    @Test
    public void partitionWhile() {
        super.partitionWhile();
        PartitionImmutableList<Integer> partition1 = this.classUnderTest().partitionWhile(( ignored) -> true);
        Assert.assertEquals(immutable.of(), partition1.getSelected());
        Assert.assertEquals(immutable.of(), partition1.getRejected());
        PartitionImmutableList<Integer> partiton2 = this.classUnderTest().partitionWhile(( ignored) -> false);
        Assert.assertEquals(immutable.of(), partiton2.getSelected());
        Assert.assertEquals(immutable.of(), partiton2.getRejected());
    }

    @Override
    @Test
    public void listIterator() {
        ListIterator<Integer> it = this.classUnderTest().listIterator();
        Assert.assertFalse(it.hasPrevious());
        Assert.assertEquals((-1), it.previousIndex());
        Assert.assertEquals(0, it.nextIndex());
        Verify.assertThrows(NoSuchElementException.class, ((Runnable) (it::next)));
        Verify.assertThrows(UnsupportedOperationException.class, it::remove);
        Verify.assertThrows(UnsupportedOperationException.class, () -> it.add(null));
    }

    @Override
    @Test
    public void collect_target() {
        MutableList<Integer> targetCollection = FastList.newList();
        MutableList<Integer> actual = this.classUnderTest().collect(( object) -> {
            throw new AssertionError();
        }, targetCollection);
        Assert.assertEquals(targetCollection, actual);
        Assert.assertSame(targetCollection, actual);
    }

    @Override
    @Test
    public void collectWith_target() {
        MutableList<Integer> targetCollection = FastList.newList();
        MutableList<Integer> actual = this.classUnderTest().collectWith(( argument1, argument2) -> {
            throw new AssertionError();
        }, 1, targetCollection);
        Assert.assertEquals(targetCollection, actual);
        Assert.assertSame(targetCollection, actual);
    }

    @Test
    public void binarySearch() {
        ListIterable<Integer> sortedList = this.classUnderTest();
        Assert.assertEquals((-1), sortedList.binarySearch(1));
    }

    @Test
    public void binarySearchWithComparator() {
        ListIterable<Integer> sortedList = this.classUnderTest();
        Assert.assertEquals((-1), sortedList.binarySearch(1, Integer::compareTo));
    }

    @Override
    @Test
    public void detectIndex() {
        // any predicate will result in -1
        Assert.assertEquals((-1), this.classUnderTest().detectIndex(Predicates.alwaysTrue()));
    }

    @Override
    @Test
    public void detectLastIndex() {
        // any predicate will result in -1
        Assert.assertEquals((-1), this.classUnderTest().detectLastIndex(Predicates.alwaysTrue()));
    }
}

