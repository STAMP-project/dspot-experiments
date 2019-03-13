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
package com.gs.collections.impl.lazy;


import AddFunction.INTEGER;
import Lists.mutable;
import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.collection.primitive.MutableBooleanCollection;
import com.gs.collections.api.collection.primitive.MutableByteCollection;
import com.gs.collections.api.collection.primitive.MutableCharCollection;
import com.gs.collections.api.collection.primitive.MutableDoubleCollection;
import com.gs.collections.api.collection.primitive.MutableFloatCollection;
import com.gs.collections.api.collection.primitive.MutableIntCollection;
import com.gs.collections.api.collection.primitive.MutableLongCollection;
import com.gs.collections.api.collection.primitive.MutableShortCollection;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.map.sorted.MutableSortedMap;
import com.gs.collections.api.multimap.Multimap;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.api.partition.PartitionIterable;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.sorted.MutableSortedSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.PrimitiveFunctions;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.block.function.NegativeIntervalFunction;
import com.gs.collections.impl.list.Interval;
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
import com.gs.collections.impl.multimap.list.FastListMultimap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.set.sorted.mutable.TreeSortedSet;
import com.gs.collections.impl.test.Verify;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractLazyIterableTestCase {
    private final LazyIterable<Integer> lazyIterable = this.newWith(1, 2, 3, 4, 5, 6, 7);

    @Test
    public void toArray() {
        Assert.assertArrayEquals(FastList.newListWith(1, 2).toArray(), this.lazyIterable.select(Predicates.lessThan(3)).toArray());
        Assert.assertArrayEquals(FastList.newListWith(1, 2).toArray(), this.lazyIterable.select(Predicates.lessThan(3)).toArray(new Object[2]));
    }

    @Test
    public void contains() {
        Assert.assertTrue(this.lazyIterable.contains(3));
        Assert.assertFalse(this.lazyIterable.contains(8));
    }

    @Test
    public void containsAllIterable() {
        Assert.assertTrue(this.lazyIterable.containsAllIterable(FastList.newListWith(3)));
        Assert.assertFalse(this.lazyIterable.containsAllIterable(FastList.newListWith(8)));
    }

    @Test
    public void containsAllArray() {
        Assert.assertTrue(this.lazyIterable.containsAllArguments(3));
        Assert.assertFalse(this.lazyIterable.containsAllArguments(8));
    }

    @Test
    public void select() {
        Assert.assertEquals(FastList.newListWith(1, 2), this.lazyIterable.select(Predicates.lessThan(3)).toList());
    }

    @Test
    public void selectWith() {
        Assert.assertEquals(FastList.newListWith(1, 2), this.lazyIterable.selectWith(Predicates2.<Integer>lessThan(), 3, FastList.<Integer>newList()));
    }

    @Test
    public void selectWithTarget() {
        Assert.assertEquals(FastList.newListWith(1, 2), this.lazyIterable.select(Predicates.lessThan(3), FastList.<Integer>newList()));
    }

    @Test
    public void reject() {
        Assert.assertEquals(FastList.newListWith(3, 4, 5, 6, 7), this.lazyIterable.reject(Predicates.lessThan(3)).toList());
    }

    @Test
    public void rejectWith() {
        Assert.assertEquals(FastList.newListWith(3, 4, 5, 6, 7), this.lazyIterable.rejectWith(Predicates2.<Integer>lessThan(), 3, FastList.<Integer>newList()));
    }

    @Test
    public void rejectWithTarget() {
        Assert.assertEquals(FastList.newListWith(3, 4, 5, 6, 7), this.lazyIterable.reject(Predicates.lessThan(3), FastList.<Integer>newList()));
    }

    @Test
    public void partition() {
        PartitionIterable<Integer> partition = this.lazyIterable.partition(IntegerPredicates.isEven());
        Assert.assertEquals(iList(2, 4, 6), partition.getSelected());
        Assert.assertEquals(iList(1, 3, 5, 7), partition.getRejected());
    }

    @Test
    public void partitionWith() {
        PartitionIterable<Integer> partition = this.lazyIterable.partitionWith(Predicates2.in(), this.lazyIterable.select(IntegerPredicates.isEven()));
        Assert.assertEquals(iList(2, 4, 6), partition.getSelected());
        Assert.assertEquals(iList(1, 3, 5, 7), partition.getRejected());
    }

    @Test
    public void selectInstancesOf() {
        Assert.assertEquals(FastList.newListWith(1, 3, 5), this.newWith(1, 2.0, 3, 4.0, 5).selectInstancesOf(Integer.class).toList());
    }

    @Test
    public void collect() {
        Assert.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6", "7"), this.lazyIterable.collect(String::valueOf).toList());
    }

    @Test
    public void collectBoolean() {
        Assert.assertEquals(BooleanArrayList.newListWith(true, true, true, true, true, true, true), this.lazyIterable.collectBoolean(PrimitiveFunctions.integerIsPositive()).toList());
    }

    @Test
    public void collectBooleanWithTarget() {
        MutableBooleanCollection target = new BooleanArrayList();
        MutableBooleanCollection result = this.lazyIterable.collectBoolean(PrimitiveFunctions.integerIsPositive(), target);
        Assert.assertEquals(BooleanArrayList.newListWith(true, true, true, true, true, true, true), result.toList());
        Assert.assertSame("Target list sent as parameter not returned", target, result);
    }

    @Test
    public void collectByte() {
        Assert.assertEquals(ByteArrayList.newListWith(((byte) (1)), ((byte) (2)), ((byte) (3)), ((byte) (4)), ((byte) (5)), ((byte) (6)), ((byte) (7))), this.lazyIterable.collectByte(PrimitiveFunctions.unboxIntegerToByte()).toList());
    }

    @Test
    public void collectByteWithTarget() {
        MutableByteCollection target = new ByteArrayList();
        MutableByteCollection result = this.lazyIterable.collectByte(PrimitiveFunctions.unboxIntegerToByte(), target);
        Assert.assertEquals(ByteArrayList.newListWith(((byte) (1)), ((byte) (2)), ((byte) (3)), ((byte) (4)), ((byte) (5)), ((byte) (6)), ((byte) (7))), result.toList());
        Assert.assertSame("Target list sent as parameter not returned", target, result);
    }

    @Test
    public void collectChar() {
        Assert.assertEquals(CharArrayList.newListWith(((char) (1)), ((char) (2)), ((char) (3)), ((char) (4)), ((char) (5)), ((char) (6)), ((char) (7))), this.lazyIterable.collectChar(PrimitiveFunctions.unboxIntegerToChar()).toList());
    }

    @Test
    public void collectCharWithTarget() {
        MutableCharCollection target = new CharArrayList();
        MutableCharCollection result = this.lazyIterable.collectChar(PrimitiveFunctions.unboxIntegerToChar(), target);
        Assert.assertEquals(CharArrayList.newListWith(((char) (1)), ((char) (2)), ((char) (3)), ((char) (4)), ((char) (5)), ((char) (6)), ((char) (7))), result.toList());
        Assert.assertSame("Target list sent as parameter not returned", target, result);
    }

    @Test
    public void collectDouble() {
        Assert.assertEquals(DoubleArrayList.newListWith(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0), this.lazyIterable.collectDouble(PrimitiveFunctions.unboxIntegerToDouble()).toList());
    }

    @Test
    public void collectDoubleWithTarget() {
        MutableDoubleCollection target = new DoubleArrayList();
        MutableDoubleCollection result = this.lazyIterable.collectDouble(PrimitiveFunctions.unboxIntegerToDouble(), target);
        Assert.assertEquals(DoubleArrayList.newListWith(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0), result.toList());
        Assert.assertSame("Target list sent as parameter not returned", target, result);
    }

    @Test
    public void collectFloat() {
        Assert.assertEquals(FloatArrayList.newListWith(1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F, 7.0F), this.lazyIterable.collectFloat(PrimitiveFunctions.unboxIntegerToFloat()).toList());
    }

    @Test
    public void collectFloatWithTarget() {
        MutableFloatCollection target = new FloatArrayList();
        MutableFloatCollection result = this.lazyIterable.collectFloat(PrimitiveFunctions.unboxIntegerToFloat(), target);
        Assert.assertEquals(FloatArrayList.newListWith(1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F, 7.0F), result.toList());
        Assert.assertSame("Target list sent as parameter not returned", target, result);
    }

    @Test
    public void collectInt() {
        Assert.assertEquals(IntArrayList.newListWith(1, 2, 3, 4, 5, 6, 7), this.lazyIterable.collectInt(PrimitiveFunctions.unboxIntegerToInt()).toList());
    }

    @Test
    public void collectIntWithTarget() {
        MutableIntCollection target = new IntArrayList();
        MutableIntCollection result = this.lazyIterable.collectInt(PrimitiveFunctions.unboxIntegerToInt(), target);
        Assert.assertEquals(IntArrayList.newListWith(1, 2, 3, 4, 5, 6, 7), result.toList());
        Assert.assertSame("Target list sent as parameter not returned", target, result);
    }

    @Test
    public void collectLong() {
        Assert.assertEquals(LongArrayList.newListWith(1L, 2L, 3L, 4L, 5L, 6L, 7L), this.lazyIterable.collectLong(PrimitiveFunctions.unboxIntegerToLong()).toList());
    }

    @Test
    public void collectLongWithTarget() {
        MutableLongCollection target = new LongArrayList();
        MutableLongCollection result = this.lazyIterable.collectLong(PrimitiveFunctions.unboxIntegerToLong(), target);
        Assert.assertEquals(LongArrayList.newListWith(1L, 2L, 3L, 4L, 5L, 6L, 7L), result.toList());
        Assert.assertSame("Target list sent as parameter not returned", target, result);
    }

    @Test
    public void collectShort() {
        Assert.assertEquals(ShortArrayList.newListWith(((short) (1)), ((short) (2)), ((short) (3)), ((short) (4)), ((short) (5)), ((short) (6)), ((short) (7))), this.lazyIterable.collectShort(PrimitiveFunctions.unboxIntegerToShort()).toList());
    }

    @Test
    public void collectShortWithTarget() {
        MutableShortCollection target = new ShortArrayList();
        MutableShortCollection result = this.lazyIterable.collectShort(PrimitiveFunctions.unboxIntegerToShort(), target);
        Assert.assertEquals(ShortArrayList.newListWith(((short) (1)), ((short) (2)), ((short) (3)), ((short) (4)), ((short) (5)), ((short) (6)), ((short) (7))), result.toList());
        Assert.assertSame("Target list sent as parameter not returned", target, result);
    }

    @Test
    public void collectWith() {
        Assert.assertEquals(FastList.newListWith("1 ", "2 ", "3 ", "4 ", "5 ", "6 ", "7 "), this.lazyIterable.collectWith(( argument1, argument2) -> argument1 + argument2, " ", FastList.<String>newList()));
    }

    @Test
    public void collectWithTarget() {
        Assert.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6", "7"), this.lazyIterable.collect(String::valueOf, FastList.<String>newList()));
    }

    @Test
    public void take() {
        LazyIterable<Integer> lazyIterable = this.lazyIterable;
        Assert.assertEquals(FastList.newList(), lazyIterable.take(0).toList());
        Assert.assertEquals(FastList.newListWith(1), lazyIterable.take(1).toList());
        Assert.assertEquals(FastList.newListWith(1, 2), lazyIterable.take(2).toList());
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6), lazyIterable.take(((lazyIterable.size()) - 1)).toList());
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7), lazyIterable.take(lazyIterable.size()).toList());
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7), lazyIterable.take(10).toList());
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7), lazyIterable.take(Integer.MAX_VALUE).toList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void take_negative_throws() {
        this.lazyIterable.take((-1));
    }

    @Test
    public void drop() {
        LazyIterable<Integer> lazyIterable = this.lazyIterable;
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7), lazyIterable.drop(0).toList());
        Assert.assertEquals(FastList.newListWith(3, 4, 5, 6, 7), lazyIterable.drop(2).toList());
        Assert.assertEquals(FastList.newListWith(7), lazyIterable.drop(((lazyIterable.size()) - 1)).toList());
        Assert.assertEquals(FastList.newList(), lazyIterable.drop(lazyIterable.size()).toList());
        Assert.assertEquals(FastList.newList(), lazyIterable.drop(10).toList());
        Assert.assertEquals(FastList.newList(), lazyIterable.drop(Integer.MAX_VALUE).toList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void drop_negative_throws() {
        this.lazyIterable.drop((-1));
    }

    @Test
    public void detect() {
        Assert.assertEquals(Integer.valueOf(3), this.lazyIterable.detect(Integer.valueOf(3)::equals));
        Assert.assertNull(this.lazyIterable.detect(Integer.valueOf(8)::equals));
    }

    @Test
    public void detectWith() {
        Assert.assertEquals(Integer.valueOf(3), this.lazyIterable.detectWith(Object::equals, Integer.valueOf(3)));
        Assert.assertNull(this.lazyIterable.detectWith(Object::equals, Integer.valueOf(8)));
    }

    @Test
    public void detectWithIfNone() {
        Function0<Integer> function = new com.gs.collections.impl.block.function.PassThruFunction0(Integer.valueOf(1000));
        Assert.assertEquals(Integer.valueOf(3), this.lazyIterable.detectWithIfNone(Object::equals, Integer.valueOf(3), function));
        Assert.assertEquals(Integer.valueOf(1000), this.lazyIterable.detectWithIfNone(Object::equals, Integer.valueOf(8), function));
    }

    @Test(expected = NoSuchElementException.class)
    public void min_empty_throws() {
        this.<Integer>newWith().min(Integer::compareTo);
    }

    @Test(expected = NoSuchElementException.class)
    public void max_empty_throws() {
        this.<Integer>newWith().max(Integer::compareTo);
    }

    @Test(expected = NullPointerException.class)
    public void min_null_throws() {
        this.newWith(1, null, 2).min(Integer::compareTo);
    }

    @Test(expected = NullPointerException.class)
    public void max_null_throws() {
        this.newWith(1, null, 2).max(Integer::compareTo);
    }

    @Test
    public void min() {
        Assert.assertEquals(Integer.valueOf(1), this.newWith(1, 3, 2).min(Integer::compareTo));
    }

    @Test
    public void max() {
        Assert.assertEquals(Integer.valueOf(3), this.newWith(1, 3, 2).max(Integer::compareTo));
    }

    @Test
    public void minBy() {
        Assert.assertEquals(Integer.valueOf(1), this.newWith(1, 3, 2).minBy(String::valueOf));
    }

    @Test
    public void maxBy() {
        Assert.assertEquals(Integer.valueOf(3), this.newWith(1, 3, 2).maxBy(String::valueOf));
    }

    @Test(expected = NoSuchElementException.class)
    public void min_empty_throws_without_comparator() {
        this.newWith().min();
    }

    @Test(expected = NoSuchElementException.class)
    public void max_empty_throws_without_comparator() {
        this.newWith().max();
    }

    @Test(expected = NullPointerException.class)
    public void min_null_throws_without_comparator() {
        this.newWith(1, null, 2).min();
    }

    @Test(expected = NullPointerException.class)
    public void max_null_throws_without_comparator() {
        this.newWith(1, null, 2).max();
    }

    @Test
    public void min_without_comparator() {
        Assert.assertEquals(Integer.valueOf(1), this.newWith(3, 1, 2).min());
    }

    @Test
    public void max_without_comparator() {
        Assert.assertEquals(Integer.valueOf(3), this.newWith(1, 3, 2).max());
    }

    @Test
    public void detectIfNone() {
        Function0<Integer> function = new com.gs.collections.impl.block.function.PassThruFunction0(9);
        Assert.assertEquals(Integer.valueOf(3), this.lazyIterable.detectIfNone(Integer.valueOf(3)::equals, function));
        Assert.assertEquals(Integer.valueOf(9), this.lazyIterable.detectIfNone(Integer.valueOf(8)::equals, function));
    }

    @Test
    public void anySatisfy() {
        Assert.assertFalse(this.lazyIterable.anySatisfy(String.class::isInstance));
        Assert.assertTrue(this.lazyIterable.anySatisfy(Integer.class::isInstance));
    }

    @Test
    public void anySatisfyWith() {
        Assert.assertFalse(this.lazyIterable.anySatisfyWith(Predicates2.instanceOf(), String.class));
        Assert.assertTrue(this.lazyIterable.anySatisfyWith(Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void allSatisfy() {
        Assert.assertTrue(this.lazyIterable.allSatisfy(Integer.class::isInstance));
        Assert.assertFalse(this.lazyIterable.allSatisfy(Integer.valueOf(1)::equals));
    }

    @Test
    public void allSatisfyWith() {
        Assert.assertTrue(this.lazyIterable.allSatisfyWith(Predicates2.instanceOf(), Integer.class));
        Assert.assertFalse(this.lazyIterable.allSatisfyWith(Object::equals, 1));
    }

    @Test
    public void noneSatisfy() {
        Assert.assertFalse(this.lazyIterable.noneSatisfy(Integer.class::isInstance));
        Assert.assertTrue(this.lazyIterable.noneSatisfy(String.class::isInstance));
    }

    @Test
    public void noneSatisfyWith() {
        Assert.assertFalse(this.lazyIterable.noneSatisfyWith(Predicates2.instanceOf(), Integer.class));
        Assert.assertTrue(this.lazyIterable.noneSatisfyWith(Predicates2.instanceOf(), String.class));
    }

    @Test
    public void count() {
        Assert.assertEquals(7, this.lazyIterable.count(Integer.class::isInstance));
    }

    @Test
    public void collectIf() {
        Assert.assertEquals(FastList.newListWith("1", "2", "3"), this.newWith(1, 2, 3).collectIf(Integer.class::isInstance, String::valueOf).toList());
    }

    @Test
    public void collectIfWithTarget() {
        Assert.assertEquals(FastList.newListWith("1", "2", "3"), this.newWith(1, 2, 3).collectIf(Integer.class::isInstance, String::valueOf, FastList.<String>newList()));
    }

    @Test
    public void getFirst() {
        Assert.assertEquals(Integer.valueOf(1), this.newWith(1, 2, 3).getFirst());
        Assert.assertNotEquals(Integer.valueOf(3), this.newWith(1, 2, 3).getFirst());
    }

    @Test
    public void getLast() {
        Assert.assertNotEquals(Integer.valueOf(1), this.newWith(1, 2, 3).getLast());
        Assert.assertEquals(Integer.valueOf(3), this.newWith(1, 2, 3).getLast());
    }

    @Test
    public void isEmpty() {
        Assert.assertTrue(this.newWith().isEmpty());
        Assert.assertTrue(this.newWith(1, 2).notEmpty());
    }

    @Test
    public void injectInto() {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        Integer result = objects.injectInto(1, INTEGER);
        Assert.assertEquals(Integer.valueOf(7), result);
    }

    @Test
    public void toList() {
        MutableList<Integer> list = this.newWith(1, 2, 3, 4).toList();
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4), list);
    }

    @Test
    public void toSortedListNaturalOrdering() {
        RichIterable<Integer> integers = this.newWith(2, 1, 5, 3, 4);
        MutableList<Integer> list = integers.toSortedList();
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5), list);
    }

    @Test
    public void toSortedList() {
        RichIterable<Integer> integers = this.newWith(2, 4, 1, 3);
        MutableList<Integer> list = integers.toSortedList(Collections.<Integer>reverseOrder());
        Assert.assertEquals(FastList.newListWith(4, 3, 2, 1), list);
    }

    @Test
    public void toSortedListBy() {
        LazyIterable<Integer> integers = this.newWith(2, 4, 1, 3);
        MutableList<Integer> list = integers.toSortedListBy(String::valueOf);
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4), list);
    }

    @Test
    public void toSortedSet() {
        LazyIterable<Integer> integers = this.newWith(2, 4, 1, 3, 2, 1, 3, 4);
        MutableSortedSet<Integer> set = integers.toSortedSet();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3, 4), set);
    }

    @Test
    public void toSortedSet_with_comparator() {
        LazyIterable<Integer> integers = this.newWith(2, 4, 4, 2, 1, 4, 1, 3);
        MutableSortedSet<Integer> set = integers.toSortedSet(Collections.<Integer>reverseOrder());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.<Integer>reverseOrder(), 1, 2, 3, 4), set);
    }

    @Test
    public void toSortedSetBy() {
        LazyIterable<Integer> integers = this.newWith(2, 4, 1, 3);
        MutableSortedSet<Integer> set = integers.toSortedSetBy(String::valueOf);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3, 4), set);
    }

    @Test
    public void toSet() {
        RichIterable<Integer> integers = this.newWith(1, 2, 3, 4);
        MutableSet<Integer> set = integers.toSet();
        Assert.assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4), set);
    }

    @Test
    public void toMap() {
        RichIterable<Integer> integers = this.newWith(1, 2, 3, 4);
        MutableMap<String, String> map = integers.toMap(String::valueOf, String::valueOf);
        Assert.assertEquals(UnifiedMap.newWithKeysValues("1", "1", "2", "2", "3", "3", "4", "4"), map);
    }

    @Test
    public void toSortedMap() {
        LazyIterable<Integer> integers = this.newWith(1, 2, 3);
        MutableSortedMap<Integer, String> map = integers.toSortedMap(Functions.getIntegerPassThru(), String::valueOf);
        Verify.assertMapsEqual(TreeSortedMap.newMapWith(1, "1", 2, "2", 3, "3"), map);
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3), map.keySet().toList());
    }

    @Test
    public void toSortedMap_with_comparator() {
        LazyIterable<Integer> integers = this.newWith(1, 2, 3);
        MutableSortedMap<Integer, String> map = integers.toSortedMap(Comparators.<Integer>reverseNaturalOrder(), Functions.getIntegerPassThru(), String::valueOf);
        Verify.assertMapsEqual(TreeSortedMap.newMapWith(Comparators.<Integer>reverseNaturalOrder(), 1, "1", 2, "2", 3, "3"), map);
        Verify.assertListsEqual(FastList.newListWith(3, 2, 1), map.keySet().toList());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("[1, 2, 3]", this.newWith(1, 2, 3).toString());
    }

    @Test
    public void makeString() {
        Assert.assertEquals("[1, 2, 3]", (('[' + (this.newWith(1, 2, 3).makeString())) + ']'));
    }

    @Test
    public void appendString() {
        Appendable builder = new StringBuilder();
        this.newWith(1, 2, 3).appendString(builder);
        Assert.assertEquals("1, 2, 3", builder.toString());
    }

    @Test
    public void groupBy() {
        Function<Integer, Boolean> isOddFunction = ( object) -> IntegerPredicates.isOdd().accept(object);
        MutableMap<Boolean, RichIterable<Integer>> expected = UnifiedMap.<Boolean, RichIterable<Integer>>newWithKeysValues(Boolean.TRUE, FastList.newListWith(1, 3, 5, 7), Boolean.FALSE, FastList.newListWith(2, 4, 6));
        Multimap<Boolean, Integer> multimap = this.lazyIterable.groupBy(isOddFunction);
        Assert.assertEquals(expected, multimap.toMap());
        Multimap<Boolean, Integer> multimap2 = this.lazyIterable.groupBy(isOddFunction, FastListMultimap.<Boolean, Integer>newMultimap());
        Assert.assertEquals(expected, multimap2.toMap());
    }

    @Test
    public void groupByEach() {
        MutableMultimap<Integer, Integer> expected = FastListMultimap.newMultimap();
        for (int i = 1; i < 8; i++) {
            expected.putAll((-i), Interval.fromTo(i, 7));
        }
        Multimap<Integer, Integer> actual = this.lazyIterable.groupByEach(new NegativeIntervalFunction());
        Assert.assertEquals(expected, actual);
        Multimap<Integer, Integer> actualWithTarget = this.lazyIterable.groupByEach(new NegativeIntervalFunction(), FastListMultimap.<Integer, Integer>newMultimap());
        Assert.assertEquals(expected, actualWithTarget);
    }

    @Test
    public void groupByUniqueKey() {
        Assert.assertEquals(UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3), this.newWith(1, 2, 3).groupByUniqueKey(( id) -> id));
    }

    @Test(expected = IllegalStateException.class)
    public void groupByUniqueKey_throws() {
        this.newWith(1, 2, 3).groupByUniqueKey(Functions.getFixedValue(1));
    }

    @Test
    public void groupByUniqueKey_target() {
        MutableMap<Integer, Integer> integers = this.newWith(1, 2, 3).groupByUniqueKey(( id) -> id, UnifiedMap.newWithKeysValues(0, 0));
        Assert.assertEquals(UnifiedMap.newWithKeysValues(0, 0, 1, 1, 2, 2, 3, 3), integers);
    }

    @Test(expected = IllegalStateException.class)
    public void groupByUniqueKey_target_throws() {
        this.newWith(1, 2, 3).groupByUniqueKey(( id) -> id, UnifiedMap.newWithKeysValues(2, 2));
    }

    @Test
    public void zip() {
        List<Object> nulls = Collections.nCopies(this.lazyIterable.size(), null);
        List<Object> nullsPlusOne = Collections.nCopies(((this.lazyIterable.size()) + 1), null);
        List<Object> nullsMinusOne = Collections.nCopies(((this.lazyIterable.size()) - 1), null);
        LazyIterable<Pair<Integer, Object>> pairs = this.lazyIterable.zip(nulls);
        Assert.assertEquals(this.lazyIterable.toSet(), pairs.collect(((Function<Pair<Integer, ?>, Integer>) (Pair::getOne))).toSet());
        Assert.assertEquals(nulls, pairs.collect(((Function<Pair<?, Object>, Object>) (Pair::getTwo)), mutable.of()));
        LazyIterable<Pair<Integer, Object>> pairsPlusOne = this.lazyIterable.zip(nullsPlusOne);
        Assert.assertEquals(this.lazyIterable.toSet(), pairsPlusOne.collect(((Function<Pair<Integer, ?>, Integer>) (Pair::getOne))).toSet());
        Assert.assertEquals(nulls, pairsPlusOne.collect(((Function<Pair<?, Object>, Object>) (Pair::getTwo)), mutable.of()));
        LazyIterable<Pair<Integer, Object>> pairsMinusOne = this.lazyIterable.zip(nullsMinusOne);
        Assert.assertEquals(((this.lazyIterable.size()) - 1), pairsMinusOne.size());
        Assert.assertTrue(this.lazyIterable.containsAllIterable(pairsMinusOne.collect(((Function<Pair<Integer, ?>, Integer>) (Pair::getOne)))));
        Assert.assertEquals(this.lazyIterable.zip(nulls).toSet(), this.lazyIterable.zip(nulls, UnifiedSet.<Pair<Integer, Object>>newSet()));
    }

    @Test
    public void zipWithIndex() {
        LazyIterable<Pair<Integer, Integer>> pairs = this.lazyIterable.zipWithIndex();
        Assert.assertEquals(this.lazyIterable.toSet(), pairs.collect(((Function<Pair<Integer, ?>, Integer>) (Pair::getOne))).toSet());
        Assert.assertEquals(Interval.zeroTo(((this.lazyIterable.size()) - 1)).toSet(), pairs.collect(((Function<Pair<?, Integer>, Integer>) (Pair::getTwo)), UnifiedSet.<Integer>newSet()));
        Assert.assertEquals(this.lazyIterable.zipWithIndex().toSet(), this.lazyIterable.zipWithIndex(UnifiedSet.<Pair<Integer, Integer>>newSet()));
    }

    @Test
    public void chunk() {
        LazyIterable<RichIterable<Integer>> groups = this.lazyIterable.chunk(2);
        RichIterable<Integer> sizes = groups.collect(RichIterable::size);
        Assert.assertEquals(Bags.mutable.of(2, 2, 2, 1), sizes.toBag());
    }

    @Test(expected = IllegalArgumentException.class)
    public void chunk_zero_throws() {
        this.lazyIterable.chunk(0);
    }

    @Test
    public void chunk_large_size() {
        Assert.assertEquals(this.lazyIterable.toBag(), this.lazyIterable.chunk(10).getFirst().toBag());
    }

    @Test
    public void tap() {
        StringBuilder tapStringBuilder = new StringBuilder();
        Procedure<Integer> appendProcedure = Procedures.append(tapStringBuilder);
        LazyIterable<Integer> list = this.lazyIterable.tap(appendProcedure);
        Verify.assertIterablesEqual(this.lazyIterable, list);
        Assert.assertEquals("1234567", tapStringBuilder.toString());
    }

    @Test
    public void asLazy() {
        Assert.assertSame(this.lazyIterable, this.lazyIterable.asLazy());
    }

    @Test
    public void flatCollect() {
        LazyIterable<Integer> collection = this.newWith(1, 2, 3, 4);
        Function<Integer, MutableList<String>> function = ( object) -> FastList.newListWith(String.valueOf(object));
        Verify.assertListsEqual(FastList.newListWith("1", "2", "3", "4"), collection.flatCollect(function).toSortedList());
        Verify.assertSetsEqual(UnifiedSet.newSetWith("1", "2", "3", "4"), collection.flatCollect(function, UnifiedSet.<String>newSet()));
    }

    @Test
    public void distinct() {
        LazyIterable<Integer> integers = this.newWith(3, 2, 2, 4, 1, 3, 1, 5);
        Assert.assertEquals(HashBag.newBagWith(1, 2, 3, 4, 5), integers.distinct().toBag());
    }
}

