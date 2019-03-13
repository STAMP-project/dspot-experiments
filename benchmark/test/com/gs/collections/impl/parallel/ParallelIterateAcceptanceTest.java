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
package com.gs.collections.impl.parallel;


import Lists.immutable;
import Lists.mutable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.multimap.Multimap;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.multimap.bag.HashBagMultimap;
import com.gs.collections.impl.multimap.bag.MultiReaderHashBagMultimap;
import com.gs.collections.impl.multimap.bag.SynchronizedPutHashBagMultimap;
import com.gs.collections.impl.multimap.list.MultiReaderFastListMultimap;
import com.gs.collections.impl.multimap.set.MultiReaderUnifiedSetMultimap;
import com.gs.collections.impl.multimap.set.SynchronizedPutUnifiedSetMultimap;
import com.gs.collections.impl.set.mutable.MultiReaderUnifiedSet;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.utility.ArrayIterate;
import com.gs.collections.impl.utility.LazyIterate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class ParallelIterateAcceptanceTest {
    private static final Procedure<Integer> EXCEPTION_PROCEDURE = ( value) -> {
        throw new RuntimeException("Thread death on its way!");
    };

    private static final ObjectIntProcedure<Integer> EXCEPTION_OBJECT_INT_PROCEDURE = ( object, index) -> {
        throw new RuntimeException("Thread death on its way!");
    };

    private static final Function<Integer, Collection<String>> INT_TO_TWO_STRINGS = ( integer) -> Lists.fixedSize.of(integer.toString(), integer.toString());

    private static final Function0<AtomicInteger> ATOMIC_INTEGER_NEW = AtomicInteger::new;

    private static final Function<Integer, String> EVEN_OR_ODD = ( value) -> (value % 2) == 0 ? "Even" : "Odd";

    private int count;

    private final MutableSet<String> threadNames = MultiReaderUnifiedSet.newSet();

    private ImmutableList<RichIterable<Integer>> iterables;

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    @Test
    public void testOneLevelCall() {
        new ParallelIterateAcceptanceTest.RecursiveProcedure().value(1);
        synchronized(this) {
            Assert.assertEquals("all iterations completed", 20000, this.count);
        }
    }

    @Test
    public void testNestedCall() {
        new ParallelIterateAcceptanceTest.RecursiveProcedure().value(2);
        synchronized(this) {
            Assert.assertEquals("all iterations completed", 419980, this.count);
        }
        Assert.assertTrue("uses multiple threads", ((this.threadNames.size()) > 1));
    }

    @Test
    public void testForEachUsingSet() {
        // Tests the default batch size calculations
        ParallelIterateAcceptanceTest.IntegerSum sum = new ParallelIterateAcceptanceTest.IntegerSum(0);
        MutableSet<Integer> set = Interval.toSet(1, 10000);
        ParallelIterate.forEach(set, new ParallelIterateAcceptanceTest.SumProcedure(sum), new ParallelIterateAcceptanceTest.SumCombiner(sum));
        Assert.assertEquals(50005000, sum.getSum());
        // Testing batch size 1
        ParallelIterateAcceptanceTest.IntegerSum sum2 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        UnifiedSet<Integer> set2 = UnifiedSet.newSet(Interval.oneTo(100));
        ParallelIterate.forEach(set2, new ParallelIterateAcceptanceTest.SumProcedure(sum2), new ParallelIterateAcceptanceTest.SumCombiner(sum2), 1, set2.getBatchCount(set2.size()));
        Assert.assertEquals(5050, sum2.getSum());
        // Testing an uneven batch size
        ParallelIterateAcceptanceTest.IntegerSum sum3 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        UnifiedSet<Integer> set3 = UnifiedSet.newSet(Interval.oneTo(100));
        ParallelIterate.forEach(set3, new ParallelIterateAcceptanceTest.SumProcedure(sum3), new ParallelIterateAcceptanceTest.SumCombiner(sum3), 1, set3.getBatchCount(13));
        Assert.assertEquals(5050, sum3.getSum());
        // Testing divideByZero exception by passing 1 as batchSize
        ParallelIterateAcceptanceTest.IntegerSum sum4 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        UnifiedSet<Integer> set4 = UnifiedSet.newSet(Interval.oneTo(100));
        ParallelIterate.forEach(set4, new ParallelIterateAcceptanceTest.SumProcedure(sum4), new ParallelIterateAcceptanceTest.SumCombiner(sum4), 1);
        Assert.assertEquals(5050, sum4.getSum());
    }

    @Test
    public void testForEachUsingMap() {
        // Test the default batch size calculations
        ParallelIterateAcceptanceTest.IntegerSum sum1 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        MutableMap<String, Integer> map1 = Interval.fromTo(1, 10000).toMap(String::valueOf, Functions.getIntegerPassThru());
        ParallelIterate.forEach(map1, new ParallelIterateAcceptanceTest.SumProcedure(sum1), new ParallelIterateAcceptanceTest.SumCombiner(sum1));
        Assert.assertEquals(50005000, sum1.getSum());
        // Testing batch size 1
        ParallelIterateAcceptanceTest.IntegerSum sum2 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        UnifiedMap<String, Integer> map2 = ((UnifiedMap<String, Integer>) (Interval.fromTo(1, 100).toMap(String::valueOf, Functions.getIntegerPassThru())));
        ParallelIterate.forEach(map2, new ParallelIterateAcceptanceTest.SumProcedure(sum2), new ParallelIterateAcceptanceTest.SumCombiner(sum2), 1, map2.getBatchCount(map2.size()));
        Assert.assertEquals(5050, sum2.getSum());
        // Testing an uneven batch size
        ParallelIterateAcceptanceTest.IntegerSum sum3 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        UnifiedMap<String, Integer> set3 = ((UnifiedMap<String, Integer>) (Interval.fromTo(1, 100).toMap(String::valueOf, Functions.getIntegerPassThru())));
        ParallelIterate.forEach(set3, new ParallelIterateAcceptanceTest.SumProcedure(sum3), new ParallelIterateAcceptanceTest.SumCombiner(sum3), 1, set3.getBatchCount(13));
        Assert.assertEquals(5050, sum3.getSum());
    }

    @Test
    public void testForEach() {
        ParallelIterateAcceptanceTest.IntegerSum sum1 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        List<Integer> list1 = ParallelIterateAcceptanceTest.createIntegerList(16);
        ParallelIterate.forEach(list1, new ParallelIterateAcceptanceTest.SumProcedure(sum1), new ParallelIterateAcceptanceTest.SumCombiner(sum1), 1, ((list1.size()) / 2));
        Assert.assertEquals(16, sum1.getSum());
        ParallelIterateAcceptanceTest.IntegerSum sum2 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        List<Integer> list2 = ParallelIterateAcceptanceTest.createIntegerList(7);
        ParallelIterate.forEach(list2, new ParallelIterateAcceptanceTest.SumProcedure(sum2), new ParallelIterateAcceptanceTest.SumCombiner(sum2));
        Assert.assertEquals(7, sum2.getSum());
        ParallelIterateAcceptanceTest.IntegerSum sum3 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        List<Integer> list3 = ParallelIterateAcceptanceTest.createIntegerList(15);
        ParallelIterate.forEach(list3, new ParallelIterateAcceptanceTest.SumProcedure(sum3), new ParallelIterateAcceptanceTest.SumCombiner(sum3), 1, ((list3.size()) / 2));
        Assert.assertEquals(15, sum3.getSum());
        ParallelIterateAcceptanceTest.IntegerSum sum4 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        List<Integer> list4 = ParallelIterateAcceptanceTest.createIntegerList(35);
        ParallelIterate.forEach(list4, new ParallelIterateAcceptanceTest.SumProcedure(sum4), new ParallelIterateAcceptanceTest.SumCombiner(sum4));
        Assert.assertEquals(35, sum4.getSum());
        ParallelIterateAcceptanceTest.IntegerSum sum5 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        MutableList<Integer> list5 = FastList.newList(list4);
        ParallelIterate.forEach(list5, new ParallelIterateAcceptanceTest.SumProcedure(sum5), new ParallelIterateAcceptanceTest.SumCombiner(sum5));
        Assert.assertEquals(35, sum5.getSum());
        ParallelIterateAcceptanceTest.IntegerSum sum6 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        List<Integer> list6 = ParallelIterateAcceptanceTest.createIntegerList(40);
        ParallelIterate.forEach(list6, new ParallelIterateAcceptanceTest.SumProcedure(sum6), new ParallelIterateAcceptanceTest.SumCombiner(sum6), 1, ((list6.size()) / 2));
        Assert.assertEquals(40, sum6.getSum());
        ParallelIterateAcceptanceTest.IntegerSum sum7 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        MutableList<Integer> list7 = FastList.newList(list6);
        ParallelIterate.forEach(list7, new ParallelIterateAcceptanceTest.SumProcedure(sum7), new ParallelIterateAcceptanceTest.SumCombiner(sum7), 1, ((list6.size()) / 2));
        Assert.assertEquals(40, sum7.getSum());
    }

    @Test
    public void testForEachImmutableList() {
        ParallelIterateAcceptanceTest.IntegerSum sum1 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        ImmutableList<Integer> list1 = immutable.ofAll(ParallelIterateAcceptanceTest.createIntegerList(16));
        ParallelIterate.forEach(list1, new ParallelIterateAcceptanceTest.SumProcedure(sum1), new ParallelIterateAcceptanceTest.SumCombiner(sum1), 1, ((list1.size()) / 2));
        Assert.assertEquals(16, sum1.getSum());
        ParallelIterateAcceptanceTest.IntegerSum sum2 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        ImmutableList<Integer> list2 = immutable.ofAll(ParallelIterateAcceptanceTest.createIntegerList(7));
        ParallelIterate.forEach(list2, new ParallelIterateAcceptanceTest.SumProcedure(sum2), new ParallelIterateAcceptanceTest.SumCombiner(sum2));
        Assert.assertEquals(7, sum2.getSum());
        ParallelIterateAcceptanceTest.IntegerSum sum3 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        ImmutableList<Integer> list3 = immutable.ofAll(ParallelIterateAcceptanceTest.createIntegerList(15));
        ParallelIterate.forEach(list3, new ParallelIterateAcceptanceTest.SumProcedure(sum3), new ParallelIterateAcceptanceTest.SumCombiner(sum3), 1, ((list3.size()) / 2));
        Assert.assertEquals(15, sum3.getSum());
        ParallelIterateAcceptanceTest.IntegerSum sum4 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        ImmutableList<Integer> list4 = immutable.ofAll(ParallelIterateAcceptanceTest.createIntegerList(35));
        ParallelIterate.forEach(list4, new ParallelIterateAcceptanceTest.SumProcedure(sum4), new ParallelIterateAcceptanceTest.SumCombiner(sum4));
        Assert.assertEquals(35, sum4.getSum());
        ParallelIterateAcceptanceTest.IntegerSum sum5 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        ImmutableList<Integer> list5 = FastList.newList(list4).toImmutable();
        ParallelIterate.forEach(list5, new ParallelIterateAcceptanceTest.SumProcedure(sum5), new ParallelIterateAcceptanceTest.SumCombiner(sum5));
        Assert.assertEquals(35, sum5.getSum());
        ParallelIterateAcceptanceTest.IntegerSum sum6 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        ImmutableList<Integer> list6 = immutable.ofAll(ParallelIterateAcceptanceTest.createIntegerList(40));
        ParallelIterate.forEach(list6, new ParallelIterateAcceptanceTest.SumProcedure(sum6), new ParallelIterateAcceptanceTest.SumCombiner(sum6), 1, ((list6.size()) / 2));
        Assert.assertEquals(40, sum6.getSum());
        ParallelIterateAcceptanceTest.IntegerSum sum7 = new ParallelIterateAcceptanceTest.IntegerSum(0);
        ImmutableList<Integer> list7 = FastList.newList(list6).toImmutable();
        ParallelIterate.forEach(list7, new ParallelIterateAcceptanceTest.SumProcedure(sum7), new ParallelIterateAcceptanceTest.SumCombiner(sum7), 1, ((list6.size()) / 2));
        Assert.assertEquals(40, sum7.getSum());
    }

    @Test
    public void testForEachWithException() {
        Verify.assertThrows(RuntimeException.class, () -> ParallelIterate.forEach(ParallelIterateAcceptanceTest.createIntegerList(5), new PassThruProcedureFactory<>(EXCEPTION_PROCEDURE), new PassThruCombiner<>(), 1, 5));
    }

    @Test
    public void testForEachWithIndexToArrayUsingFastListSerialPath() {
        Integer[] array = new Integer[200];
        FastList<Integer> list = ((FastList<Integer>) (Interval.oneTo(200).toList()));
        Assert.assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        ParallelIterate.forEachWithIndex(list, ( each, index) -> array[index] = each);
        Assert.assertArrayEquals(array, list.toArray(new Integer[]{  }));
    }

    @Test
    public void testForEachWithIndexToArrayUsingFastList() {
        Integer[] array = new Integer[200];
        FastList<Integer> list = ((FastList<Integer>) (Interval.oneTo(200).toList()));
        Assert.assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        ParallelIterate.forEachWithIndex(list, ( each, index) -> array[index] = each, 10, 10);
        Assert.assertArrayEquals(array, list.toArray(new Integer[]{  }));
    }

    @Test
    public void testForEachWithIndexToArrayUsingImmutableList() {
        Integer[] array = new Integer[200];
        ImmutableList<Integer> list = Interval.oneTo(200).toList().toImmutable();
        Assert.assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        ParallelIterate.forEachWithIndex(list, ( each, index) -> array[index] = each, 10, 10);
        Assert.assertArrayEquals(array, list.toArray(new Integer[]{  }));
    }

    @Test
    public void testForEachWithIndexToArrayUsingArrayList() {
        Integer[] array = new Integer[200];
        List<Integer> list = new java.util.ArrayList(Interval.oneTo(200));
        Assert.assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        ParallelIterate.forEachWithIndex(list, ( each, index) -> array[index] = each, 10, 10);
        Assert.assertArrayEquals(array, list.toArray(new Integer[]{  }));
    }

    @Test
    public void testForEachWithIndexToArrayUsingFixedArrayList() {
        Integer[] array = new Integer[10];
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Assert.assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        ParallelIterate.forEachWithIndex(list, ( each, index) -> array[index] = each, 1, 2);
        Assert.assertArrayEquals(array, list.toArray(new Integer[list.size()]));
    }

    @Test
    public void testForEachWithIndexException() {
        Verify.assertThrows(RuntimeException.class, () -> ParallelIterate.forEachWithIndex(ParallelIterateAcceptanceTest.createIntegerList(5), new PassThruObjectIntProcedureFactory<>(EXCEPTION_OBJECT_INT_PROCEDURE), new PassThruCombiner<>(), 1, 5));
    }

    @Test
    public void select() {
        this.iterables.forEach(Procedures.cast(this::basicSelect));
    }

    @Test
    public void selectSortedSet() {
        RichIterable<Integer> iterable = Interval.oneTo(20000).toSortedSet();
        Collection<Integer> actual1 = ParallelIterate.select(iterable, Predicates.greaterThan(10000));
        Collection<Integer> actual2 = ParallelIterate.select(iterable, Predicates.greaterThan(10000), true);
        RichIterable<Integer> expected = iterable.select(Predicates.greaterThan(10000));
        Assert.assertSame(expected.getClass(), actual1.getClass());
        Assert.assertSame(expected.getClass(), actual2.getClass());
        Assert.assertEquals((((expected.getClass().getSimpleName()) + '/') + (actual1.getClass().getSimpleName())), expected, actual1);
        Assert.assertEquals((((expected.getClass().getSimpleName()) + '/') + (actual2.getClass().getSimpleName())), expected, actual2);
    }

    @Test
    public void count() {
        this.iterables.forEach(Procedures.cast(this::basicCount));
    }

    @Test
    public void reject() {
        this.iterables.forEach(Procedures.cast(this::basicReject));
    }

    @Test
    public void collect() {
        this.iterables.forEach(Procedures.cast(this::basicCollect));
    }

    @Test
    public void collectIf() {
        this.iterables.forEach(Procedures.cast(this::basicCollectIf));
    }

    @Test
    public void flatCollect() {
        this.iterables.forEach(Procedures.cast(this::basicFlatCollect));
    }

    @Test
    public void groupBy() {
        FastList<Integer> iterable = FastList.newWithNValues(10000000, new Function0<Integer>() {
            private int current;

            public Integer value() {
                if ((this.current) < 4) {
                    return Integer.valueOf(((this.current)++));
                }
                this.current = 0;
                return Integer.valueOf(4);
            }
        });
        iterable.shuffleThis();
        Multimap<String, Integer> expected = iterable.toBag().groupBy(String::valueOf);
        Multimap<String, Integer> expectedAsSet = iterable.toSet().groupBy(String::valueOf);
        Multimap<String, Integer> result1 = ParallelIterate.groupBy(iterable.toList(), String::valueOf, 100);
        Assert.assertEquals(expected, HashBagMultimap.newMultimap(result1));
        Multimap<String, Integer> result2 = ParallelIterate.groupBy(iterable.toList(), String::valueOf);
        Assert.assertEquals(expected, HashBagMultimap.newMultimap(result2));
        Multimap<String, Integer> result3 = ParallelIterate.groupBy(iterable.toSet(), String::valueOf, SynchronizedPutUnifiedSetMultimap.<String, Integer>newMultimap(), 100);
        Assert.assertEquals(expectedAsSet, result3);
        Multimap<String, Integer> result4 = ParallelIterate.groupBy(iterable.toSet(), String::valueOf, SynchronizedPutUnifiedSetMultimap.<String, Integer>newMultimap());
        Assert.assertEquals(expectedAsSet, result4);
        Multimap<String, Integer> result5 = ParallelIterate.groupBy(iterable.toSortedSet(), String::valueOf, SynchronizedPutUnifiedSetMultimap.<String, Integer>newMultimap(), 100);
        Assert.assertEquals(expectedAsSet, result5);
        Multimap<String, Integer> result6 = ParallelIterate.groupBy(iterable.toSortedSet(), String::valueOf, SynchronizedPutUnifiedSetMultimap.<String, Integer>newMultimap());
        Assert.assertEquals(expectedAsSet, result6);
        Multimap<String, Integer> result7 = ParallelIterate.groupBy(iterable.toBag(), String::valueOf, SynchronizedPutHashBagMultimap.<String, Integer>newMultimap(), 100);
        Assert.assertEquals(expected, result7);
        Multimap<String, Integer> result8 = ParallelIterate.groupBy(iterable.toBag(), String::valueOf, SynchronizedPutHashBagMultimap.<String, Integer>newMultimap());
        Assert.assertEquals(expected, result8);
        Multimap<String, Integer> result9 = ParallelIterate.groupBy(iterable.toList().toImmutable(), String::valueOf);
        Assert.assertEquals(expected, HashBagMultimap.newMultimap(result9));
        Multimap<String, Integer> result10 = ParallelIterate.groupBy(iterable.toSortedList(), String::valueOf, 100);
        Assert.assertEquals(expected, HashBagMultimap.newMultimap(result10));
        Multimap<String, Integer> result11 = ParallelIterate.groupBy(iterable.toSortedList(), String::valueOf);
        Assert.assertEquals(expected, HashBagMultimap.newMultimap(result11));
        Multimap<String, Integer> result12 = ParallelIterate.groupBy(iterable, String::valueOf, MultiReaderFastListMultimap.<String, Integer>newMultimap(), 100);
        Assert.assertEquals(expected, HashBagMultimap.newMultimap(result12));
        Multimap<String, Integer> result13 = ParallelIterate.groupBy(iterable, String::valueOf, MultiReaderFastListMultimap.<String, Integer>newMultimap());
        Assert.assertEquals(expected, HashBagMultimap.newMultimap(result13));
        Multimap<String, Integer> result14 = ParallelIterate.groupBy(iterable, String::valueOf, MultiReaderHashBagMultimap.<String, Integer>newMultimap(), 100);
        Assert.assertEquals(expected, result14);
        Multimap<String, Integer> result15 = ParallelIterate.groupBy(iterable, String::valueOf, MultiReaderHashBagMultimap.<String, Integer>newMultimap());
        Assert.assertEquals(expected, result15);
        Multimap<String, Integer> result16 = ParallelIterate.groupBy(iterable, String::valueOf, MultiReaderUnifiedSetMultimap.<String, Integer>newMultimap(), 100);
        Assert.assertEquals(expectedAsSet, result16);
        Multimap<String, Integer> result17 = ParallelIterate.groupBy(iterable, String::valueOf, MultiReaderUnifiedSetMultimap.<String, Integer>newMultimap());
        Assert.assertEquals(expectedAsSet, result17);
    }

    @Test
    public void aggregateInPlaceBy() {
        Procedure2<AtomicInteger, Integer> countAggregator = ( aggregate, value) -> aggregate.incrementAndGet();
        List<Integer> list = Interval.oneTo(20000);
        MutableMap<String, AtomicInteger> aggregation = ParallelIterate.aggregateInPlaceBy(list, ParallelIterateAcceptanceTest.EVEN_OR_ODD, ParallelIterateAcceptanceTest.ATOMIC_INTEGER_NEW, countAggregator);
        Assert.assertEquals(10000, aggregation.get("Even").intValue());
        Assert.assertEquals(10000, aggregation.get("Odd").intValue());
        ParallelIterate.aggregateInPlaceBy(list, ParallelIterateAcceptanceTest.EVEN_OR_ODD, ParallelIterateAcceptanceTest.ATOMIC_INTEGER_NEW, countAggregator, aggregation);
        Assert.assertEquals(20000, aggregation.get("Even").intValue());
        Assert.assertEquals(20000, aggregation.get("Odd").intValue());
    }

    @Test
    public void aggregateInPlaceByWithBatchSize() {
        MutableList<Integer> list = LazyIterate.adapt(Collections.nCopies(1000, 1)).concatenate(Collections.nCopies(2000, 2)).concatenate(Collections.nCopies(3000, 3)).toList().shuffleThis();
        MapIterable<String, AtomicInteger> aggregation = ParallelIterate.aggregateInPlaceBy(list, String::valueOf, ParallelIterateAcceptanceTest.ATOMIC_INTEGER_NEW, AtomicInteger::addAndGet, 100);
        Assert.assertEquals(1000, aggregation.get("1").intValue());
        Assert.assertEquals(4000, aggregation.get("2").intValue());
        Assert.assertEquals(9000, aggregation.get("3").intValue());
    }

    private class RecursiveProcedure implements Procedure<Integer> {
        private static final long serialVersionUID = 1L;

        private final ExecutorService executorService = ParallelIterate.newPooledExecutor("ParallelIterateTest", false);

        @Override
        public void value(Integer level) {
            if (level > 0) {
                ParallelIterateAcceptanceTest.this.threadNames.add(Thread.currentThread().getName());
                this.executeParallelIterate((level - 1), this.executorService);
            } else {
                this.simulateWork();
            }
        }

        private void simulateWork() {
            synchronized(ParallelIterateAcceptanceTest.this) {
                (ParallelIterateAcceptanceTest.this.count)++;
            }
        }

        private void executeParallelIterate(int level, ExecutorService executorService) {
            MutableList<Integer> items = mutable.of();
            for (int i = 0; i < 20000; i++) {
                items.add(((i % 1000) == 0 ? level : 0));
            }
            ParallelIterate.forEach(items, new ParallelIterateAcceptanceTest.RecursiveProcedure(), executorService);
        }
    }

    public static final class IntegerSum {
        private int sum;

        public IntegerSum(int newSum) {
            this.sum = newSum;
        }

        public ParallelIterateAcceptanceTest.IntegerSum add(int value) {
            this.sum += value;
            return this;
        }

        public int getSum() {
            return this.sum;
        }
    }

    public static final class SumProcedure implements Function2<ParallelIterateAcceptanceTest.IntegerSum, Integer, ParallelIterateAcceptanceTest.IntegerSum> , Procedure<Integer> , ProcedureFactory<ParallelIterateAcceptanceTest.SumProcedure> {
        private static final long serialVersionUID = 1L;

        private final ParallelIterateAcceptanceTest.IntegerSum sum;

        public SumProcedure(ParallelIterateAcceptanceTest.IntegerSum newSum) {
            this.sum = newSum;
        }

        @Override
        public ParallelIterateAcceptanceTest.SumProcedure create() {
            return new ParallelIterateAcceptanceTest.SumProcedure(new ParallelIterateAcceptanceTest.IntegerSum(0));
        }

        @Override
        public ParallelIterateAcceptanceTest.IntegerSum value(ParallelIterateAcceptanceTest.IntegerSum s1, Integer s2) {
            return s1.add(s2);
        }

        @Override
        public void value(Integer object) {
            this.sum.add(object);
        }

        public int getSum() {
            return this.sum.getSum();
        }
    }

    public static final class SumCombiner extends AbstractProcedureCombiner<ParallelIterateAcceptanceTest.SumProcedure> {
        private static final long serialVersionUID = 1L;

        private final ParallelIterateAcceptanceTest.IntegerSum sum;

        public SumCombiner(ParallelIterateAcceptanceTest.IntegerSum initialSum) {
            super(true);
            this.sum = initialSum;
        }

        @Override
        public void combineOne(ParallelIterateAcceptanceTest.SumProcedure sumProcedure) {
            this.sum.add(sumProcedure.getSum());
        }
    }
}

