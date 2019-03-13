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
package com.gs.collections.test;


import AddFunction.INTEGER_TO_DOUBLE;
import AddFunction.INTEGER_TO_FLOAT;
import AddFunction.INTEGER_TO_INT;
import AddFunction.INTEGER_TO_LONG;
import Lists.immutable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.multimap.Multimap;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.api.partition.PartitionIterable;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.bag.sorted.mutable.TreeBag;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.map.sorted.mutable.TreeSortedMap;
import com.gs.collections.impl.test.SerializeTestHelper;
import com.gs.collections.impl.tuple.Tuples;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public interface RichIterableUniqueTestCase extends RichIterableTestCase {
    @Override
    @Test
    default void Object_PostSerializedEqualsAndHashCode() {
        Iterable<Integer> iterable = newWith(3, 2, 1);
        Object deserialized = SerializeTestHelper.serializeDeserialize(iterable);
        Assert.assertNotSame(iterable, deserialized);
    }

    @Override
    @Test
    default void Object_equalsAndHashCode() {
        assertPostSerializedEqualsAndHashCode(this.newWith(3, 2, 1));
        Assert.assertNotEquals(this.newWith(4, 3, 2, 1), this.newWith(3, 2, 1));
        Assert.assertNotEquals(this.newWith(3, 2, 1), this.newWith(4, 3, 2, 1));
        Assert.assertNotEquals(this.newWith(2, 1), this.newWith(3, 2, 1));
        Assert.assertNotEquals(this.newWith(3, 2, 1), this.newWith(2, 1));
        Assert.assertNotEquals(this.newWith(4, 2, 1), this.newWith(3, 2, 1));
        Assert.assertNotEquals(this.newWith(3, 2, 1), this.newWith(4, 2, 1));
    }

    @Test
    default void Iterable_sanity_check() {
        String s = "";
        assertThrows(IllegalStateException.class, () -> this.newWith(s, s));
    }

    @Override
    @Test
    default void InternalIterable_forEach() {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);
        MutableCollection<Integer> result = this.newMutableForFilter();
        iterable.forEach(Procedures.cast(( i) -> result.add((i + 10))));
        IterableTestCase.assertEquals(this.newMutableForFilter(13, 12, 11), result);
    }

    @Override
    @Test
    default void InternalIterable_forEachWith() {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);
        MutableCollection<Integer> result = this.newMutableForFilter();
        iterable.forEachWith(( argument1, argument2) -> result.add((argument1 + argument2)), 10);
        IterableTestCase.assertEquals(this.newMutableForFilter(13, 12, 11), result);
    }

    @Test
    default void RichIterable_size() {
        IterableTestCase.assertEquals(3, this.newWith(3, 2, 1).size());
    }

    @Test
    default void RichIterable_toArray() {
        Object[] array = this.newWith(3, 2, 1).toArray();
        Assert.assertArrayEquals(new Object[]{ 3, 2, 1 }, array);
    }

    @Override
    @Test
    default void RichIterable_select_reject() {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);
        IterableTestCase.assertEquals(this.getExpectedFiltered(4, 2), iterable.select(IntegerPredicates.isEven()));
        IterableTestCase.assertEquals(this.getExpectedFiltered(4, 2), iterable.select(IntegerPredicates.isEven(), this.<Integer>newMutableForFilter()));
        IterableTestCase.assertEquals(this.getExpectedFiltered(4, 3), iterable.selectWith(Predicates2.greaterThan(), 2));
        IterableTestCase.assertEquals(this.getExpectedFiltered(4, 3), iterable.selectWith(Predicates2.<Integer>greaterThan(), 2, this.<Integer>newMutableForFilter()));
        IterableTestCase.assertEquals(this.getExpectedFiltered(4, 2), iterable.reject(IntegerPredicates.isOdd()));
        IterableTestCase.assertEquals(this.getExpectedFiltered(4, 2), iterable.reject(IntegerPredicates.isOdd(), this.<Integer>newMutableForFilter()));
        IterableTestCase.assertEquals(this.getExpectedFiltered(4, 3), iterable.rejectWith(Predicates2.lessThan(), 3));
        IterableTestCase.assertEquals(this.getExpectedFiltered(4, 3), iterable.rejectWith(Predicates2.<Integer>lessThan(), 3, this.<Integer>newMutableForFilter()));
    }

    @Override
    @Test
    default void RichIterable_partition() {
        RichIterable<Integer> iterable = this.newWith((-3), (-2), (-1), 0, 1, 2, 3);
        PartitionIterable<Integer> partition = iterable.partition(IntegerPredicates.isEven());
        IterableTestCase.assertEquals(this.getExpectedFiltered((-2), 0, 2), partition.getSelected());
        IterableTestCase.assertEquals(this.getExpectedFiltered((-3), (-1), 1, 3), partition.getRejected());
        PartitionIterable<Integer> partitionWith = iterable.partitionWith(Predicates2.greaterThan(), 0);
        IterableTestCase.assertEquals(this.getExpectedFiltered(1, 2, 3), partitionWith.getSelected());
        IterableTestCase.assertEquals(this.getExpectedFiltered((-3), (-2), (-1), 0), partitionWith.getRejected());
    }

    @Override
    @Test
    default void RichIterable_selectInstancesOf() {
        RichIterable<Number> iterable = this.<Number>newWith(1, 2.0, 3, 4.0);
        IterableTestCase.assertEquals(this.getExpectedFiltered(), iterable.selectInstancesOf(String.class));
        IterableTestCase.assertEquals(this.getExpectedFiltered(1, 3), iterable.selectInstancesOf(Integer.class));
        IterableTestCase.assertEquals(this.getExpectedFiltered(1, 2.0, 3, 4.0), iterable.selectInstancesOf(Number.class));
    }

    @Override
    @Test
    default void RichIterable_collect() {
        RichIterable<Integer> iterable = this.newWith(13, 12, 11, 3, 2, 1);
        IterableTestCase.assertEquals(this.getExpectedTransformed(3, 2, 1, 3, 2, 1), iterable.collect(( i) -> i % 10));
        IterableTestCase.assertEquals(this.getExpectedTransformed(3, 2, 1, 3, 2, 1), iterable.collect(( i) -> i % 10, this.newMutableForTransform()));
        IterableTestCase.assertEquals(this.getExpectedTransformed(3, 2, 1, 3, 2, 1), iterable.collectWith(( i, mod) -> i % mod, 10));
        IterableTestCase.assertEquals(this.getExpectedTransformed(3, 2, 1, 3, 2, 1), iterable.collectWith(( i, mod) -> i % mod, 10, this.newMutableForTransform()));
    }

    @Override
    @Test
    default void RichIterable_collectIf() {
        IterableTestCase.assertEquals(this.getExpectedTransformed(3, 1, 3, 1), this.newWith(13, 12, 11, 3, 2, 1).collectIf(( i) -> (i % 2) != 0, ( i) -> i % 10));
        IterableTestCase.assertEquals(this.newMutableForTransform(3, 1, 3, 1), this.newWith(13, 12, 11, 3, 2, 1).collectIf(( i) -> (i % 2) != 0, ( i) -> i % 10, this.newMutableForTransform()));
    }

    @Override
    @Test
    default void RichIterable_collectPrimitive() {
        IterableTestCase.assertEquals(this.getExpectedBoolean(false, true, false), this.newWith(3, 2, 1).collectBoolean(( each) -> (each % 2) == 0));
        IterableTestCase.assertEquals(this.getExpectedBoolean(false, true, false), this.newWith(3, 2, 1).collectBoolean(( each) -> (each % 2) == 0, this.newBooleanForTransform()));
        RichIterable<Integer> iterable = this.newWith(13, 12, 11, 3, 2, 1);
        IterableTestCase.assertEquals(this.getExpectedByte(((byte) (3)), ((byte) (2)), ((byte) (1)), ((byte) (3)), ((byte) (2)), ((byte) (1))), iterable.collectByte(( each) -> ((byte) (each % 10))));
        IterableTestCase.assertEquals(this.getExpectedByte(((byte) (3)), ((byte) (2)), ((byte) (1)), ((byte) (3)), ((byte) (2)), ((byte) (1))), iterable.collectByte(( each) -> ((byte) (each % 10)), this.newByteForTransform()));
        IterableTestCase.assertEquals(this.getExpectedChar(((char) (3)), ((char) (2)), ((char) (1)), ((char) (3)), ((char) (2)), ((char) (1))), iterable.collectChar(( each) -> ((char) (each % 10))));
        IterableTestCase.assertEquals(this.getExpectedChar(((char) (3)), ((char) (2)), ((char) (1)), ((char) (3)), ((char) (2)), ((char) (1))), iterable.collectChar(( each) -> ((char) (each % 10)), this.newCharForTransform()));
        IterableTestCase.assertEquals(this.getExpectedDouble(3.0, 2.0, 1.0, 3.0, 2.0, 1.0), iterable.collectDouble(( each) -> ((double) (each % 10))));
        IterableTestCase.assertEquals(this.getExpectedDouble(3.0, 2.0, 1.0, 3.0, 2.0, 1.0), iterable.collectDouble(( each) -> ((double) (each % 10)), this.newDoubleForTransform()));
        IterableTestCase.assertEquals(this.getExpectedFloat(3.0F, 2.0F, 1.0F, 3.0F, 2.0F, 1.0F), iterable.collectFloat(( each) -> ((float) (each % 10))));
        IterableTestCase.assertEquals(this.getExpectedFloat(3.0F, 2.0F, 1.0F, 3.0F, 2.0F, 1.0F), iterable.collectFloat(( each) -> ((float) (each % 10)), this.newFloatForTransform()));
        IterableTestCase.assertEquals(this.getExpectedInt(3, 2, 1, 3, 2, 1), iterable.collectInt(( each) -> each % 10));
        IterableTestCase.assertEquals(this.getExpectedInt(3, 2, 1, 3, 2, 1), iterable.collectInt(( each) -> each % 10, this.newIntForTransform()));
        IterableTestCase.assertEquals(this.getExpectedLong(3, 2, 1, 3, 2, 1), iterable.collectLong(( each) -> each % 10));
        IterableTestCase.assertEquals(this.getExpectedLong(3, 2, 1, 3, 2, 1), iterable.collectLong(( each) -> each % 10, this.newLongForTransform()));
        IterableTestCase.assertEquals(this.getExpectedShort(((short) (3)), ((short) (2)), ((short) (1)), ((short) (3)), ((short) (2)), ((short) (1))), iterable.collectShort(( each) -> ((short) (each % 10))));
        IterableTestCase.assertEquals(this.getExpectedShort(((short) (3)), ((short) (2)), ((short) (1)), ((short) (3)), ((short) (2)), ((short) (1))), iterable.collectShort(( each) -> ((short) (each % 10)), this.newShortForTransform()));
    }

    @Override
    @Test
    default void RichIterable_flatCollect() {
        IterableTestCase.assertEquals(this.getExpectedTransformed(1, 2, 3, 1, 2, 1), this.newWith(3, 2, 1).flatCollect(Interval::oneTo));
        IterableTestCase.assertEquals(this.getExpectedTransformed(1, 2, 3, 1, 2, 1), this.newWith(3, 2, 1).flatCollect(Interval::oneTo, this.newMutableForTransform()));
    }

    @Override
    @Test
    default void RichIterable_count() {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);
        IterableTestCase.assertEquals(1, iterable.count(Integer.valueOf(3)::equals));
        IterableTestCase.assertEquals(1, iterable.count(Integer.valueOf(2)::equals));
        IterableTestCase.assertEquals(1, iterable.count(Integer.valueOf(1)::equals));
        IterableTestCase.assertEquals(0, iterable.count(Integer.valueOf(0)::equals));
        IterableTestCase.assertEquals(2, iterable.count(( i) -> (i % 2) != 0));
        IterableTestCase.assertEquals(3, iterable.count(( i) -> i > 0));
        IterableTestCase.assertEquals(1, iterable.countWith(Object::equals, 3));
        IterableTestCase.assertEquals(1, iterable.countWith(Object::equals, 2));
        IterableTestCase.assertEquals(1, iterable.countWith(Object::equals, 1));
        IterableTestCase.assertEquals(0, iterable.countWith(Object::equals, 0));
        IterableTestCase.assertEquals(3, iterable.countWith(Predicates2.greaterThan(), 0));
    }

    @Override
    @Test
    default void RichIterable_groupBy() {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);
        Function<Integer, Boolean> groupByFunction = ( object) -> IntegerPredicates.isOdd().accept(object);
        MutableMap<Boolean, RichIterable<Integer>> groupByExpected = UnifiedMap.newWithKeysValues(Boolean.TRUE, this.newMutableForFilter(3, 1), Boolean.FALSE, this.newMutableForFilter(4, 2));
        IterableTestCase.assertEquals(groupByExpected, iterable.groupBy(groupByFunction).toMap());
        Function<Integer, Boolean> function = (Integer object) -> true;
        MutableMultimap<Boolean, Integer> multimap2 = iterable.groupBy(groupByFunction, this.<Integer>newWith().groupBy(function).toMutable());
        IterableTestCase.assertEquals(groupByExpected, multimap2.toMap());
        Function<Integer, Iterable<Integer>> groupByEachFunction = ( integer) -> Interval.fromTo((-1), (-integer));
        MutableMap<Integer, RichIterable<Integer>> expectedGroupByEach = UnifiedMap.newWithKeysValues((-4), this.newMutableForFilter(4), (-3), this.newMutableForFilter(4, 3), (-2), this.newMutableForFilter(4, 3, 2), (-1), this.newMutableForFilter(4, 3, 2, 1));
        IterableTestCase.assertEquals(expectedGroupByEach, iterable.groupByEach(groupByEachFunction).toMap());
        Multimap<Integer, Integer> actualWithTarget = iterable.groupByEach(groupByEachFunction, this.<Integer>newWith().groupByEach(groupByEachFunction).toMutable());
        IterableTestCase.assertEquals(expectedGroupByEach, actualWithTarget.toMap());
    }

    @Override
    @Test
    default void RichIterable_aggregateBy_aggregateInPlaceBy() {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);
        MapIterable<String, Integer> aggregateBy = iterable.aggregateBy(Object::toString, () -> 0, ( integer1, integer2) -> integer1 + integer2);
        IterableTestCase.assertEquals(4, aggregateBy.get("4").intValue());
        IterableTestCase.assertEquals(3, aggregateBy.get("3").intValue());
        IterableTestCase.assertEquals(2, aggregateBy.get("2").intValue());
        IterableTestCase.assertEquals(1, aggregateBy.get("1").intValue());
        MapIterable<String, AtomicInteger> aggregateInPlaceBy = iterable.aggregateInPlaceBy(String::valueOf, AtomicInteger::new, AtomicInteger::addAndGet);
        IterableTestCase.assertEquals(4, aggregateInPlaceBy.get("4").intValue());
        IterableTestCase.assertEquals(3, aggregateInPlaceBy.get("3").intValue());
        IterableTestCase.assertEquals(2, aggregateInPlaceBy.get("2").intValue());
        IterableTestCase.assertEquals(1, aggregateInPlaceBy.get("1").intValue());
    }

    @Override
    @Test
    default void RichIterable_sumOfPrimitive() {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);
        Assert.assertEquals(10.0F, iterable.sumOfFloat(Integer::floatValue), 0.001);
        Assert.assertEquals(10.0, iterable.sumOfDouble(Integer::doubleValue), 0.001);
        Assert.assertEquals(10, iterable.sumOfInt(( integer) -> integer));
        Assert.assertEquals(10L, iterable.sumOfLong(Integer::longValue));
    }

    @Override
    @Test
    default void RichIterable_injectInto() {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);
        IterableTestCase.assertEquals(Integer.valueOf(11), iterable.injectInto(1, new com.gs.collections.api.block.function.Function2<Integer, Integer, Integer>() {
            private static final long serialVersionUID = 1L;

            public Integer value(Integer argument1, Integer argument2) {
                return argument1 + argument2;
            }
        }));
        IterableTestCase.assertEquals(Integer.valueOf(10), iterable.injectInto(0, new com.gs.collections.api.block.function.Function2<Integer, Integer, Integer>() {
            private static final long serialVersionUID = 1L;

            public Integer value(Integer argument1, Integer argument2) {
                return argument1 + argument2;
            }
        }));
    }

    @Override
    @Test
    default void RichIterable_injectInto_primitive() {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);
        Assert.assertEquals(11, iterable.injectInto(1, INTEGER_TO_INT));
        Assert.assertEquals(10, iterable.injectInto(0, INTEGER_TO_INT));
        Assert.assertEquals(11L, iterable.injectInto(1, INTEGER_TO_LONG));
        Assert.assertEquals(10L, iterable.injectInto(0, INTEGER_TO_LONG));
        Assert.assertEquals(11.0, iterable.injectInto(1, INTEGER_TO_DOUBLE), 0.001);
        Assert.assertEquals(10.0, iterable.injectInto(0, INTEGER_TO_DOUBLE), 0.001);
        Assert.assertEquals(11.0F, iterable.injectInto(1, INTEGER_TO_FLOAT), 0.001F);
        Assert.assertEquals(10.0F, iterable.injectInto(0, INTEGER_TO_FLOAT), 0.001F);
    }

    @Override
    @Test
    default void RichIterable_makeString_appendString() {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);
        IterableTestCase.assertEquals("4, 3, 2, 1", iterable.makeString());
        IterableTestCase.assertEquals("4/3/2/1", iterable.makeString("/"));
        IterableTestCase.assertEquals("[4/3/2/1]", iterable.makeString("[", "/", "]"));
        StringBuilder stringBuilder1 = new StringBuilder();
        iterable.appendString(stringBuilder1);
        IterableTestCase.assertEquals("4, 3, 2, 1", stringBuilder1.toString());
        StringBuilder stringBuilder2 = new StringBuilder();
        iterable.appendString(stringBuilder2, "/");
        IterableTestCase.assertEquals("4/3/2/1", stringBuilder2.toString());
        StringBuilder stringBuilder3 = new StringBuilder();
        iterable.appendString(stringBuilder3, "[", "/", "]");
        IterableTestCase.assertEquals("[4/3/2/1]", stringBuilder3.toString());
    }

    @Override
    @Test
    default void RichIterable_toString() {
        IterableTestCase.assertEquals("[4, 3, 2, 1]", this.newWith(4, 3, 2, 1).toString());
    }

    @Override
    @Test
    default void RichIterable_toList() {
        IterableTestCase.assertEquals(immutable.with(4, 3, 2, 1), this.newWith(4, 3, 2, 1).toList());
    }

    @Override
    @Test
    default void RichIterable_toSortedList() {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);
        IterableTestCase.assertEquals(immutable.with(1, 2, 3, 4), iterable.toSortedList());
        IterableTestCase.assertEquals(immutable.with(4, 3, 2, 1), iterable.toSortedList(Comparators.reverseNaturalOrder()));
        IterableTestCase.assertEquals(immutable.with(1, 2, 3, 4), iterable.toSortedListBy(Functions.identity()));
        IterableTestCase.assertEquals(immutable.with(4, 3, 2, 1), iterable.toSortedListBy(( each) -> each * (-1)));
    }

    @Override
    @Test
    default void RichIterable_toSet() {
        IterableTestCase.assertEquals(Sets.immutable.with(4, 3, 2, 1), this.newWith(4, 3, 2, 1).toSet());
    }

    @Override
    @Test
    default void RichIterable_toSortedSet() {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);
        IterableTestCase.assertEquals(SortedSets.immutable.with(1, 2, 3, 4), iterable.toSortedSet());
        IterableTestCase.assertEquals(SortedSets.immutable.with(Comparators.reverseNaturalOrder(), 4, 3, 2, 1), iterable.toSortedSet(Comparators.reverseNaturalOrder()));
        IterableTestCase.assertEquals(SortedSets.immutable.with(Comparators.byFunction(Functions.identity()), 1, 2, 3, 4), iterable.toSortedSetBy(Functions.identity()));
        IterableTestCase.assertEquals(SortedSets.immutable.with(Comparators.byFunction((Integer each) -> each * (-1)), 4, 3, 2, 1), iterable.toSortedSetBy(( each) -> each * (-1)));
    }

    @Override
    @Test
    default void RichIterable_toBag() {
        IterableTestCase.assertEquals(Bags.immutable.with(4, 3, 2, 1), this.newWith(4, 3, 2, 1).toBag());
    }

    @Override
    @Test
    default void RichIterable_toSortedBag() {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);
        IterableTestCase.assertEquals(TreeBag.newBagWith(1, 2, 3, 4), iterable.toSortedBag());
        IterableTestCase.assertEquals(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 4, 3, 2, 1), iterable.toSortedBag(Comparators.reverseNaturalOrder()));
        IterableTestCase.assertEquals(TreeBag.newBagWith(Comparators.byFunction(Functions.identity()), 1, 2, 3, 4), iterable.toSortedBagBy(Functions.identity()));
        IterableTestCase.assertEquals(TreeBag.newBagWith(Comparators.byFunction((Integer each) -> each * (-1)), 4, 3, 2, 1), iterable.toSortedBagBy(( each) -> each * (-1)));
    }

    @Override
    @Test
    default void RichIterable_toMap() {
        RichIterable<Integer> iterable = this.newWith(13, 12, 11, 3, 2, 1);
        IterableTestCase.assertEquals(UnifiedMap.newMapWith(Tuples.pair("13", 3), Tuples.pair("12", 2), Tuples.pair("11", 1), Tuples.pair("3", 3), Tuples.pair("2", 2), Tuples.pair("1", 1)), iterable.toMap(Object::toString, ( each) -> each % 10));
    }

    @Override
    @Test
    default void RichIterable_toSortedMap() {
        RichIterable<Integer> iterable = this.newWith(13, 12, 11, 3, 2, 1);
        Pair<String, Integer>[] pairs = new Pair[]{ Tuples.pair("13", 3), Tuples.pair("12", 2), Tuples.pair("11", 1), Tuples.pair("3", 3), Tuples.pair("2", 2), Tuples.pair("1", 1) };
        IterableTestCase.assertEquals(TreeSortedMap.newMapWith(pairs), iterable.toSortedMap(Object::toString, ( each) -> each % 10));
        IterableTestCase.assertEquals(TreeSortedMap.newMapWith(Comparators.reverseNaturalOrder(), pairs), iterable.toSortedMap(Comparators.reverseNaturalOrder(), Object::toString, ( each) -> each % 10));
    }
}

