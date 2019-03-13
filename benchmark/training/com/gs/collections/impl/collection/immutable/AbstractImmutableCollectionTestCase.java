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
package com.gs.collections.impl.collection.immutable;


import AddFunction.INTEGER;
import AddFunction.INTEGER_TO_DOUBLE;
import AddFunction.INTEGER_TO_FLOAT;
import AddFunction.INTEGER_TO_INT;
import AddFunction.INTEGER_TO_LONG;
import Lists.mutable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.collection.ImmutableCollection;
import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.api.collection.primitive.ImmutableBooleanCollection;
import com.gs.collections.api.collection.primitive.ImmutableByteCollection;
import com.gs.collections.api.collection.primitive.ImmutableCharCollection;
import com.gs.collections.api.collection.primitive.ImmutableDoubleCollection;
import com.gs.collections.api.collection.primitive.ImmutableFloatCollection;
import com.gs.collections.api.collection.primitive.ImmutableIntCollection;
import com.gs.collections.api.collection.primitive.ImmutableLongCollection;
import com.gs.collections.api.collection.primitive.ImmutableShortCollection;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.partition.PartitionImmutableCollection;
import com.gs.collections.api.set.sorted.MutableSortedSet;
import com.gs.collections.impl.Counter;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.PrimitiveFunctions;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.set.sorted.mutable.TreeSortedSet;
import com.gs.collections.impl.test.Verify;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractImmutableCollectionTestCase {
    public static final Predicate<Integer> ERROR_THROWING_PREDICATE = ( each) -> {
        throw new AssertionError();
    };

    public static final Predicates2<Integer, Class<Integer>> ERROR_THROWING_PREDICATE_2 = new Predicates2<Integer, Class<Integer>>() {
        public boolean accept(Integer argument1, Class<Integer> argument2) {
            throw new AssertionError();
        }
    };

    @Test
    public void selectWith() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assert.assertEquals(this.<Integer>newMutable().withAll(integers).select(IntegerPredicates.isOdd()), integers.selectWith(Predicates2.in(), iList(1, 3, 5, 7, 9)));
    }

    @Test
    public void selectWith_target() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assert.assertEquals(this.<Integer>newMutable().with(101).withAll(integers).select(IntegerPredicates.isOdd()), integers.selectWith(Predicates2.in(), iList(1, 3, 5, 7, 9), this.<Integer>newMutable().with(101)));
    }

    @Test
    public void rejectWith() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assert.assertEquals(this.<Integer>newMutable().withAll(integers).reject(IntegerPredicates.isOdd()), integers.rejectWith(Predicates2.in(), iList(1, 3, 5, 7, 9)));
    }

    @Test
    public void rejectWith_target() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assert.assertEquals(this.<Integer>newMutable().with(100).withAll(integers).reject(IntegerPredicates.isOdd()), integers.rejectWith(Predicates2.in(), iList(1, 3, 5, 7, 9), this.<Integer>newMutable().with(100)));
    }

    @Test
    public void partition() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        PartitionImmutableCollection<Integer> partition = integers.partition(IntegerPredicates.isOdd());
        Assert.assertEquals(integers.select(IntegerPredicates.isOdd()), partition.getSelected());
        Assert.assertEquals(integers.select(IntegerPredicates.isEven()), partition.getRejected());
    }

    @Test
    public void partitionWith() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        PartitionImmutableCollection<Integer> partition = integers.partitionWith(Predicates2.in(), integers.select(IntegerPredicates.isOdd()));
        Assert.assertEquals(integers.select(IntegerPredicates.isOdd()), partition.getSelected());
        Assert.assertEquals(integers.select(IntegerPredicates.isEven()), partition.getRejected());
    }

    @Test
    public void collectWith() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableCollection<String> expected = integers.collect(Functions.chain(String::valueOf, ( string) -> string + "!"));
        ImmutableCollection<String> actual = integers.collectWith(( argument1, argument2) -> argument1 + argument2, "!");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void collect_target() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableCollection<String> strings = this.<String>newMutable();
        integers.forEach(Procedures.cast(( each) -> strings.add(each.toString())));
        MutableCollection<String> target = this.<String>newMutable();
        MutableCollection<String> actual = integers.collect(String::valueOf, target);
        Assert.assertEquals(strings, actual);
        Assert.assertSame(target, actual);
    }

    @Test
    public void collectWith_target() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableCollection<String> expected = this.<String>newMutable().with("?").withAll(integers.collect(Functions.chain(String::valueOf, ( string) -> string + "!")));
        MutableCollection<String> targetCollection = this.<String>newMutable().with("?");
        MutableCollection<String> actual = integers.collectWith(( argument1, argument2) -> argument1 + argument2, "!", targetCollection);
        Assert.assertEquals(expected, actual);
        Assert.assertSame(targetCollection, actual);
    }

    @Test
    public void injectInto() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Integer result = integers.injectInto(0, INTEGER);
        Assert.assertEquals(FastList.newList(integers).injectInto(0, INTEGER_TO_INT), result.intValue());
    }

    @Test
    public void injectIntoInt() {
        Assert.assertEquals(this.classUnderTest().injectInto(0, INTEGER).longValue(), this.classUnderTest().injectInto(0, INTEGER_TO_INT));
    }

    @Test
    public void injectIntoLong() {
        Assert.assertEquals(this.classUnderTest().injectInto(0, INTEGER).longValue(), this.classUnderTest().injectInto(0, INTEGER_TO_LONG));
    }

    @Test
    public void injectIntoDouble() {
        Assert.assertEquals(this.classUnderTest().injectInto(0, INTEGER).doubleValue(), this.classUnderTest().injectInto(0, INTEGER_TO_DOUBLE), 0.0);
    }

    @Test
    public void injectIntoFloat() {
        Assert.assertEquals(this.classUnderTest().injectInto(0, INTEGER).floatValue(), this.classUnderTest().injectInto(0, INTEGER_TO_FLOAT), 0.0);
    }

    @Test
    public void sumFloat() {
        Assert.assertEquals(this.classUnderTest().injectInto(0, INTEGER_TO_FLOAT), this.classUnderTest().sumOfFloat(Integer::floatValue), 0.0);
    }

    @Test
    public void sumDouble() {
        Assert.assertEquals(this.classUnderTest().injectInto(0, INTEGER_TO_DOUBLE), this.classUnderTest().sumOfDouble(Integer::doubleValue), 0.0);
    }

    @Test
    public void sumInteger() {
        Assert.assertEquals(this.classUnderTest().injectInto(0, INTEGER_TO_INT), this.classUnderTest().sumOfInt(( integer) -> integer));
    }

    @Test
    public void sumLong() {
        Assert.assertEquals(this.classUnderTest().injectInto(0, INTEGER_TO_LONG), this.classUnderTest().sumOfLong(Integer::longValue));
    }

    @Test
    public void makeString() {
        Assert.assertEquals(FastList.newList(this.classUnderTest()).toString(), (('[' + (this.classUnderTest().makeString())) + ']'));
        Assert.assertEquals(FastList.newList(this.classUnderTest()).toString(), (('[' + (this.classUnderTest().makeString(", "))) + ']'));
        Assert.assertEquals(FastList.newList(this.classUnderTest()).toString(), this.classUnderTest().makeString("[", ", ", "]"));
    }

    @Test
    public void appendString() {
        Appendable builder1 = new StringBuilder();
        this.classUnderTest().appendString(builder1);
        Assert.assertEquals(FastList.newList(this.classUnderTest()).toString(), (('[' + (builder1.toString())) + ']'));
        Appendable builder2 = new StringBuilder();
        this.classUnderTest().appendString(builder2, ", ");
        Assert.assertEquals(FastList.newList(this.classUnderTest()).toString(), (('[' + (builder2.toString())) + ']'));
        Appendable builder3 = new StringBuilder();
        this.classUnderTest().appendString(builder3, "[", ", ", "]");
        Assert.assertEquals(FastList.newList(this.classUnderTest()).toString(), builder3.toString());
    }

    @Test
    public void testToString() {
        Assert.assertEquals(FastList.newList(this.classUnderTest()).toString(), this.classUnderTest().toString());
    }

    @Test
    public void select() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assert.assertEquals(integers, integers.select(Predicates.lessThan(((integers.size()) + 1))));
        Verify.assertIterableEmpty(integers.select(Predicates.greaterThan(integers.size())));
    }

    @Test
    public void selectInstancesOf() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableCollection<Integer> result = integers.selectInstancesOf(Integer.class);
        Assert.assertEquals(integers, result);
    }

    @Test
    public void reject() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Verify.assertIterableEmpty(integers.reject(Predicates.lessThan(((integers.size()) + 1))));
        Assert.assertEquals(integers, integers.reject(Predicates.greaterThan(integers.size())));
    }

    @Test
    public void collect() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assert.assertEquals(integers, integers.collect(Functions.getIntegerPassThru()));
    }

    @Test
    public void collectBoolean() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableBooleanCollection immutableCollection = integers.collectBoolean(PrimitiveFunctions.integerIsPositive());
        Verify.assertSize(1, immutableCollection);
    }

    @Test
    public void collectByte() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableByteCollection immutableCollection = integers.collectByte(PrimitiveFunctions.unboxIntegerToByte());
        Verify.assertSize(integers.size(), immutableCollection);
        Assert.assertEquals(integers, immutableCollection.collect(Integer::valueOf));
    }

    @Test
    public void collectChar() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableCharCollection immutableCollection = integers.collectChar(PrimitiveFunctions.unboxIntegerToChar());
        Verify.assertSize(integers.size(), immutableCollection);
        Assert.assertEquals(integers, immutableCollection.collect(Integer::valueOf));
    }

    @Test
    public void collectDouble() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableDoubleCollection immutableCollection = integers.collectDouble(PrimitiveFunctions.unboxIntegerToDouble());
        Verify.assertSize(integers.size(), immutableCollection);
        Assert.assertEquals(integers, immutableCollection.collect(( doubleParameter) -> Integer.valueOf(((int) (doubleParameter)))));
    }

    @Test
    public void collectFloat() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableFloatCollection immutableCollection = integers.collectFloat(PrimitiveFunctions.unboxIntegerToFloat());
        Verify.assertSize(integers.size(), immutableCollection);
        Assert.assertEquals(integers, immutableCollection.collect(( floatParameter) -> Integer.valueOf(((int) (floatParameter)))));
    }

    @Test
    public void collectInt() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableIntCollection immutableCollection = integers.collectInt(PrimitiveFunctions.unboxIntegerToInt());
        Verify.assertSize(integers.size(), immutableCollection);
        Assert.assertEquals(integers, immutableCollection.collect(Integer::valueOf));
    }

    @Test
    public void collectLong() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableLongCollection immutableCollection = integers.collectLong(PrimitiveFunctions.unboxIntegerToLong());
        Verify.assertSize(integers.size(), immutableCollection);
        Assert.assertEquals(integers, immutableCollection.collect(( longParameter) -> Integer.valueOf(((int) (longParameter)))));
    }

    @Test
    public void collectShort() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableShortCollection immutableCollection = integers.collectShort(PrimitiveFunctions.unboxIntegerToShort());
        Verify.assertSize(integers.size(), immutableCollection);
        Assert.assertEquals(integers, immutableCollection.collect(Integer::valueOf));
    }

    @Test
    public void flatCollect() {
        RichIterable<String> actual = this.classUnderTest().flatCollect(( integer) -> Lists.fixedSize.of(String.valueOf(integer)));
        ImmutableCollection<String> expected = this.classUnderTest().collect(String::valueOf);
        Assert.assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void chunk_zero_throws() {
        this.classUnderTest().chunk(0);
    }

    @Test
    public void detect() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assert.assertEquals(Integer.valueOf(1), integers.detect(Predicates.equal(1)));
        Assert.assertNull(integers.detect(Predicates.equal(((integers.size()) + 1))));
    }

    @Test
    public void detectWith() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assert.assertEquals(Integer.valueOf(1), integers.detectWith(Object::equals, Integer.valueOf(1)));
        Assert.assertNull(integers.detectWith(Object::equals, Integer.valueOf(((integers.size()) + 1))));
        FastList<String> strings = FastList.newListWith("1", "2", "3");
        Assert.assertEquals("1", strings.detectWith(Object::equals, "1"));
    }

    @Test
    public void detectIfNone() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Function0<Integer> function = new com.gs.collections.impl.block.function.PassThruFunction0(((integers.size()) + 1));
        Assert.assertEquals(Integer.valueOf(1), integers.detectIfNone(Predicates.equal(1), function));
        Assert.assertEquals(Integer.valueOf(((integers.size()) + 1)), integers.detectIfNone(Predicates.equal(((integers.size()) + 1)), function));
    }

    @Test
    public void detectWithIfNone() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Integer sum = Integer.valueOf(((integers.size()) + 1));
        Function0<Integer> function = new com.gs.collections.impl.block.function.PassThruFunction0(sum);
        Assert.assertEquals(Integer.valueOf(1), integers.detectWithIfNone(Object::equals, Integer.valueOf(1), function));
        Assert.assertEquals(sum, integers.detectWithIfNone(Object::equals, sum, function));
    }

    @Test
    public void allSatisfy() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assert.assertTrue(integers.allSatisfy(Integer.class::isInstance));
        Assert.assertFalse(integers.allSatisfy(Integer.valueOf(0)::equals));
    }

    @Test
    public void allSatisfyWith() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assert.assertTrue(integers.allSatisfyWith(Predicates2.instanceOf(), Integer.class));
        Assert.assertFalse(integers.allSatisfyWith(Object::equals, 0));
    }

    @Test
    public void noneSatisfy() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assert.assertTrue(integers.noneSatisfy(String.class::isInstance));
        Assert.assertFalse(integers.noneSatisfy(Integer.valueOf(1)::equals));
    }

    @Test
    public void noneSatisfyWith() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assert.assertTrue(integers.noneSatisfyWith(Predicates2.instanceOf(), String.class));
        Assert.assertFalse(integers.noneSatisfyWith(Object::equals, 1));
    }

    @Test
    public void anySatisfy() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assert.assertFalse(integers.anySatisfy(String.class::isInstance));
        Assert.assertTrue(integers.anySatisfy(Integer.class::isInstance));
    }

    @Test
    public void anySatisfyWith() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assert.assertFalse(integers.anySatisfyWith(Predicates2.instanceOf(), String.class));
        Assert.assertTrue(integers.anySatisfyWith(Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void count() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assert.assertEquals(integers.size(), integers.count(Integer.class::isInstance));
        Assert.assertEquals(0, integers.count(String.class::isInstance));
    }

    @Test
    public void countWith() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assert.assertEquals(integers.size(), integers.countWith(Predicates2.instanceOf(), Integer.class));
        Assert.assertEquals(0, integers.countWith(Predicates2.instanceOf(), String.class));
    }

    @Test
    public void collectIf() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assert.assertEquals(integers, integers.collectIf(Integer.class::isInstance, Functions.getIntegerPassThru()));
    }

    @Test
    public void getFirst() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assert.assertEquals(Integer.valueOf(1), integers.getFirst());
    }

    @Test
    public void getLast() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assert.assertEquals(Integer.valueOf(integers.size()), integers.getLast());
    }

    @Test
    public void isEmpty() {
        ImmutableCollection<Integer> immutableCollection = this.classUnderTest();
        Assert.assertFalse(immutableCollection.isEmpty());
        Assert.assertTrue(immutableCollection.notEmpty());
    }

    @Test
    public void iterator() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            Integer integer = iterator.next();
            Assert.assertEquals((i + 1), integer.intValue());
        }
        Verify.assertThrows(NoSuchElementException.class, ((Runnable) (iterator::next)));
    }

    @Test
    public void toArray() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableList<Integer> copy = FastList.newList(integers);
        Assert.assertArrayEquals(integers.toArray(), copy.toArray());
        Assert.assertArrayEquals(integers.toArray(new Integer[integers.size()]), copy.toArray(new Integer[integers.size()]));
    }

    @Test
    public void toSortedList() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableList<Integer> copy = FastList.newList(integers);
        MutableList<Integer> list = integers.toSortedList(Collections.<Integer>reverseOrder());
        Assert.assertEquals(copy.sortThis(Collections.<Integer>reverseOrder()), list);
        MutableList<Integer> list2 = integers.toSortedList();
        Assert.assertEquals(copy.sortThis(), list2);
    }

    @Test
    public void toSortedSet() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableSortedSet<Integer> set = integers.toSortedSet();
        Verify.assertListsEqual(integers.toSortedList(), set.toList());
    }

    @Test
    public void toSortedSetWithComparator() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableSortedSet<Integer> set = integers.toSortedSet(Comparators.<Integer>reverseNaturalOrder());
        Assert.assertEquals(integers.toSet(), set);
        Assert.assertEquals(integers.toSortedList(Comparators.<Integer>reverseNaturalOrder()), set.toList());
    }

    @Test
    public void toSortedSetBy() {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableSortedSet<Integer> set = integers.toSortedSetBy(String::valueOf);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(integers), set);
    }

    @Test
    public void forLoop() {
        ImmutableCollection<Integer> immutableCollection = this.classUnderTest();
        for (Integer each : immutableCollection) {
            Assert.assertNotNull(each);
        }
    }

    @Test(expected = NullPointerException.class)
    public void min_null_throws() {
        this.classUnderTestWithNull().min(Integer::compareTo);
    }

    @Test(expected = NullPointerException.class)
    public void max_null_throws() {
        this.classUnderTestWithNull().max(Integer::compareTo);
    }

    @Test
    public void min() {
        Assert.assertEquals(Integer.valueOf(1), this.classUnderTest().min(Integer::compareTo));
    }

    @Test
    public void max() {
        Assert.assertEquals(Integer.valueOf(1), this.classUnderTest().max(Comparators.reverse(Integer::compareTo)));
    }

    @Test(expected = NullPointerException.class)
    public void min_null_throws_without_comparator() {
        this.classUnderTestWithNull().min();
    }

    @Test(expected = NullPointerException.class)
    public void max_null_throws_without_comparator() {
        this.classUnderTestWithNull().max();
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
        Assert.assertEquals(Integer.valueOf(this.classUnderTest().size()), this.classUnderTest().maxBy(Functions.getIntegerPassThru()));
    }

    @Test
    public void iteratorRemove() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().iterator().remove());
    }

    @Test
    public void add() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ((Collection<Integer>) (this.classUnderTest())).add(1));
    }

    @Test
    public void remove() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ((Collection<Integer>) (this.classUnderTest())).remove(Integer.valueOf(1)));
    }

    @Test
    public void clear() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ((Collection<Integer>) (this.classUnderTest())).clear());
    }

    @Test
    public void removeAll() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ((Collection<Integer>) (this.classUnderTest())).removeAll(Lists.fixedSize.of()));
    }

    @Test
    public void retainAll() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ((Collection<Integer>) (this.classUnderTest())).retainAll(Lists.fixedSize.of()));
    }

    @Test
    public void addAll() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ((Collection<Integer>) (this.classUnderTest())).addAll(Lists.fixedSize.<Integer>of()));
    }

    @Test
    public void aggregateByMutating() {
        Procedure2<Counter, Integer> sumAggregator = Counter::add;
        MapIterable<String, Counter> actual = this.classUnderTest().aggregateInPlaceBy(String::valueOf, Counter::new, sumAggregator);
        MapIterable<String, Counter> expected = this.classUnderTest().toBag().aggregateInPlaceBy(String::valueOf, Counter::new, sumAggregator);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void aggregateByNonMutating() {
        Function2<Integer, Integer, Integer> sumAggregator = ( integer1, integer2) -> integer1 + integer2;
        MapIterable<String, Integer> actual = this.classUnderTest().aggregateBy(String::valueOf, () -> 0, sumAggregator);
        MapIterable<String, Integer> expected = this.classUnderTest().toBag().aggregateBy(String::valueOf, () -> 0, sumAggregator);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void tap() {
        MutableList<Integer> tapResult = mutable.of();
        ImmutableCollection<Integer> collection = this.classUnderTest();
        Assert.assertSame(collection, collection.tap(tapResult::add));
        Assert.assertEquals(collection.toList(), tapResult);
    }
}

