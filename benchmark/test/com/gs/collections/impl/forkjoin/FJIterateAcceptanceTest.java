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
package com.gs.collections.impl.forkjoin;


import Lists.immutable;
import Lists.mutable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.parallel.AbstractProcedureCombiner;
import com.gs.collections.impl.parallel.ParallelIterate;
import com.gs.collections.impl.parallel.ProcedureFactory;
import com.gs.collections.impl.set.mutable.MultiReaderUnifiedSet;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.utility.ArrayIterate;
import com.gs.collections.impl.utility.LazyIterate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class FJIterateAcceptanceTest {
    private static final Procedure<Integer> EXCEPTION_PROCEDURE = ( value) -> {
        throw new RuntimeException("Thread death on its way!");
    };

    private static final ObjectIntProcedure<Integer> EXCEPTION_OBJECT_INT_PROCEDURE = ( object, index) -> {
        throw new RuntimeException("Thread death on its way!");
    };

    private static final Function<Integer, Collection<String>> INT_TO_TWO_STRINGS = ( integer) -> Lists.fixedSize.of(integer.toString(), integer.toString());

    private static final Function<Integer, String> EVEN_OR_ODD = ( value) -> (value % 2) == 0 ? "Even" : "Odd";

    private int count;

    private final MutableSet<String> threadNames = MultiReaderUnifiedSet.newSet();

    private ImmutableList<RichIterable<Integer>> iterables;

    private final ForkJoinPool executor = new ForkJoinPool(2);

    @Test
    public void testOneLevelCall() {
        new FJIterateAcceptanceTest.RecursiveProcedure().value(1);
        synchronized(this) {
            Assert.assertEquals("all iterations completed", 20000, this.count);
        }
    }

    @Test
    public void testNestedCall() {
        new FJIterateAcceptanceTest.RecursiveProcedure().value(2);
        synchronized(this) {
            Assert.assertEquals("all iterations completed", 419980, this.count);
        }
        Assert.assertTrue("uses multiple threads", ((this.threadNames.size()) > 1));
    }

    @Test
    public void testForEachUsingSet() {
        // Tests the default batch size calculations
        FJIterateAcceptanceTest.IntegerSum sum = new FJIterateAcceptanceTest.IntegerSum(0);
        MutableSet<Integer> set = Interval.toSet(1, 10000);
        FJIterate.forEach(set, new FJIterateAcceptanceTest.SumProcedure(sum), new FJIterateAcceptanceTest.SumCombiner(sum));
        Assert.assertEquals(50005000, sum.getSum());
        // Testing batch size 1
        FJIterateAcceptanceTest.IntegerSum sum2 = new FJIterateAcceptanceTest.IntegerSum(0);
        UnifiedSet<Integer> set2 = UnifiedSet.newSet(Interval.oneTo(100));
        FJIterate.forEach(set2, new FJIterateAcceptanceTest.SumProcedure(sum2), new FJIterateAcceptanceTest.SumCombiner(sum2), 1, set2.getBatchCount(set2.size()));
        Assert.assertEquals(5050, sum2.getSum());
        // Testing an uneven batch size
        FJIterateAcceptanceTest.IntegerSum sum3 = new FJIterateAcceptanceTest.IntegerSum(0);
        UnifiedSet<Integer> set3 = UnifiedSet.newSet(Interval.oneTo(100));
        FJIterate.forEach(set3, new FJIterateAcceptanceTest.SumProcedure(sum3), new FJIterateAcceptanceTest.SumCombiner(sum3), 1, set3.getBatchCount(13));
        Assert.assertEquals(5050, sum3.getSum());
        // Testing divideByZero exception by passing 1 as batchSize
        FJIterateAcceptanceTest.IntegerSum sum4 = new FJIterateAcceptanceTest.IntegerSum(0);
        UnifiedSet<Integer> set4 = UnifiedSet.newSet(Interval.oneTo(100));
        FJIterate.forEach(set4, new FJIterateAcceptanceTest.SumProcedure(sum4), new FJIterateAcceptanceTest.SumCombiner(sum4), 1);
        Assert.assertEquals(5050, sum4.getSum());
    }

    @Test
    public void testForEachUsingMap() {
        // Test the default batch size calculations
        FJIterateAcceptanceTest.IntegerSum sum1 = new FJIterateAcceptanceTest.IntegerSum(0);
        MutableMap<String, Integer> map1 = Interval.fromTo(1, 10000).toMap(String::valueOf, Functions.getIntegerPassThru());
        FJIterate.forEach(map1, new FJIterateAcceptanceTest.SumProcedure(sum1), new FJIterateAcceptanceTest.SumCombiner(sum1));
        Assert.assertEquals(50005000, sum1.getSum());
        // Testing batch size 1
        FJIterateAcceptanceTest.IntegerSum sum2 = new FJIterateAcceptanceTest.IntegerSum(0);
        UnifiedMap<String, Integer> map2 = ((UnifiedMap<String, Integer>) (Interval.fromTo(1, 100).toMap(String::valueOf, Functions.getIntegerPassThru())));
        FJIterate.forEach(map2, new FJIterateAcceptanceTest.SumProcedure(sum2), new FJIterateAcceptanceTest.SumCombiner(sum2), 1, map2.getBatchCount(map2.size()));
        Assert.assertEquals(5050, sum2.getSum());
        // Testing an uneven batch size
        FJIterateAcceptanceTest.IntegerSum sum3 = new FJIterateAcceptanceTest.IntegerSum(0);
        UnifiedMap<String, Integer> set3 = ((UnifiedMap<String, Integer>) (Interval.fromTo(1, 100).toMap(String::valueOf, Functions.getIntegerPassThru())));
        FJIterate.forEach(set3, new FJIterateAcceptanceTest.SumProcedure(sum3), new FJIterateAcceptanceTest.SumCombiner(sum3), 1, set3.getBatchCount(13));
        Assert.assertEquals(5050, sum3.getSum());
    }

    @Test
    public void testForEach() {
        FJIterateAcceptanceTest.IntegerSum sum1 = new FJIterateAcceptanceTest.IntegerSum(0);
        List<Integer> list1 = FJIterateAcceptanceTest.createIntegerList(16);
        FJIterate.forEach(list1, new FJIterateAcceptanceTest.SumProcedure(sum1), new FJIterateAcceptanceTest.SumCombiner(sum1), 1, ((list1.size()) / 2));
        Assert.assertEquals(16, sum1.getSum());
        FJIterateAcceptanceTest.IntegerSum sum2 = new FJIterateAcceptanceTest.IntegerSum(0);
        List<Integer> list2 = FJIterateAcceptanceTest.createIntegerList(7);
        FJIterate.forEach(list2, new FJIterateAcceptanceTest.SumProcedure(sum2), new FJIterateAcceptanceTest.SumCombiner(sum2));
        Assert.assertEquals(7, sum2.getSum());
        FJIterateAcceptanceTest.IntegerSum sum3 = new FJIterateAcceptanceTest.IntegerSum(0);
        List<Integer> list3 = FJIterateAcceptanceTest.createIntegerList(15);
        FJIterate.forEach(list3, new FJIterateAcceptanceTest.SumProcedure(sum3), new FJIterateAcceptanceTest.SumCombiner(sum3), 1, ((list3.size()) / 2));
        Assert.assertEquals(15, sum3.getSum());
        FJIterateAcceptanceTest.IntegerSum sum4 = new FJIterateAcceptanceTest.IntegerSum(0);
        List<Integer> list4 = FJIterateAcceptanceTest.createIntegerList(35);
        FJIterate.forEach(list4, new FJIterateAcceptanceTest.SumProcedure(sum4), new FJIterateAcceptanceTest.SumCombiner(sum4));
        Assert.assertEquals(35, sum4.getSum());
        FJIterateAcceptanceTest.IntegerSum sum5 = new FJIterateAcceptanceTest.IntegerSum(0);
        MutableList<Integer> list5 = FastList.newList(list4);
        FJIterate.forEach(list5, new FJIterateAcceptanceTest.SumProcedure(sum5), new FJIterateAcceptanceTest.SumCombiner(sum5));
        Assert.assertEquals(35, sum5.getSum());
        FJIterateAcceptanceTest.IntegerSum sum6 = new FJIterateAcceptanceTest.IntegerSum(0);
        List<Integer> list6 = FJIterateAcceptanceTest.createIntegerList(40);
        FJIterate.forEach(list6, new FJIterateAcceptanceTest.SumProcedure(sum6), new FJIterateAcceptanceTest.SumCombiner(sum6), 1, ((list6.size()) / 2));
        Assert.assertEquals(40, sum6.getSum());
        FJIterateAcceptanceTest.IntegerSum sum7 = new FJIterateAcceptanceTest.IntegerSum(0);
        MutableList<Integer> list7 = FastList.newList(list6);
        FJIterate.forEach(list7, new FJIterateAcceptanceTest.SumProcedure(sum7), new FJIterateAcceptanceTest.SumCombiner(sum7), 1, ((list6.size()) / 2));
        Assert.assertEquals(40, sum7.getSum());
    }

    @Test
    public void testForEachImmutableList() {
        FJIterateAcceptanceTest.IntegerSum sum1 = new FJIterateAcceptanceTest.IntegerSum(0);
        ImmutableList<Integer> list1 = immutable.ofAll(FJIterateAcceptanceTest.createIntegerList(16));
        FJIterate.forEach(list1, new FJIterateAcceptanceTest.SumProcedure(sum1), new FJIterateAcceptanceTest.SumCombiner(sum1), 1, ((list1.size()) / 2));
        Assert.assertEquals(16, sum1.getSum());
        FJIterateAcceptanceTest.IntegerSum sum2 = new FJIterateAcceptanceTest.IntegerSum(0);
        ImmutableList<Integer> list2 = immutable.ofAll(FJIterateAcceptanceTest.createIntegerList(7));
        FJIterate.forEach(list2, new FJIterateAcceptanceTest.SumProcedure(sum2), new FJIterateAcceptanceTest.SumCombiner(sum2));
        Assert.assertEquals(7, sum2.getSum());
        FJIterateAcceptanceTest.IntegerSum sum3 = new FJIterateAcceptanceTest.IntegerSum(0);
        ImmutableList<Integer> list3 = immutable.ofAll(FJIterateAcceptanceTest.createIntegerList(15));
        FJIterate.forEach(list3, new FJIterateAcceptanceTest.SumProcedure(sum3), new FJIterateAcceptanceTest.SumCombiner(sum3), 1, ((list3.size()) / 2));
        Assert.assertEquals(15, sum3.getSum());
        FJIterateAcceptanceTest.IntegerSum sum4 = new FJIterateAcceptanceTest.IntegerSum(0);
        ImmutableList<Integer> list4 = immutable.ofAll(FJIterateAcceptanceTest.createIntegerList(35));
        FJIterate.forEach(list4, new FJIterateAcceptanceTest.SumProcedure(sum4), new FJIterateAcceptanceTest.SumCombiner(sum4));
        Assert.assertEquals(35, sum4.getSum());
        FJIterateAcceptanceTest.IntegerSum sum5 = new FJIterateAcceptanceTest.IntegerSum(0);
        ImmutableList<Integer> list5 = FastList.newList(list4).toImmutable();
        FJIterate.forEach(list5, new FJIterateAcceptanceTest.SumProcedure(sum5), new FJIterateAcceptanceTest.SumCombiner(sum5));
        Assert.assertEquals(35, sum5.getSum());
        FJIterateAcceptanceTest.IntegerSum sum6 = new FJIterateAcceptanceTest.IntegerSum(0);
        ImmutableList<Integer> list6 = immutable.ofAll(FJIterateAcceptanceTest.createIntegerList(40));
        FJIterate.forEach(list6, new FJIterateAcceptanceTest.SumProcedure(sum6), new FJIterateAcceptanceTest.SumCombiner(sum6), 1, ((list6.size()) / 2));
        Assert.assertEquals(40, sum6.getSum());
        FJIterateAcceptanceTest.IntegerSum sum7 = new FJIterateAcceptanceTest.IntegerSum(0);
        ImmutableList<Integer> list7 = FastList.newList(list6).toImmutable();
        FJIterate.forEach(list7, new FJIterateAcceptanceTest.SumProcedure(sum7), new FJIterateAcceptanceTest.SumCombiner(sum7), 1, ((list6.size()) / 2));
        Assert.assertEquals(40, sum7.getSum());
    }

    @Test
    public void testForEachWithException() {
        Verify.assertThrows(RuntimeException.class, () -> FJIterate.forEach(FJIterateAcceptanceTest.createIntegerList(5), new PassThruProcedureFactory<>(EXCEPTION_PROCEDURE), new PassThruCombiner<>(), 1, 5));
    }

    @Test
    public void testForEachWithIndexToArrayUsingFastListSerialPath() {
        Integer[] array = new Integer[200];
        FastList<Integer> list = ((FastList<Integer>) (Interval.oneTo(200).toList()));
        Assert.assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        FJIterate.forEachWithIndex(list, ( each, index) -> array[index] = each);
        Assert.assertArrayEquals(array, list.toArray(new Integer[]{  }));
    }

    @Test
    public void testForEachWithIndexToArrayUsingFastList() {
        Integer[] array = new Integer[200];
        FastList<Integer> list = ((FastList<Integer>) (Interval.oneTo(200).toList()));
        Assert.assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        FJIterate.forEachWithIndex(list, ( each, index) -> array[index] = each, 10, 10);
        Assert.assertArrayEquals(array, list.toArray(new Integer[]{  }));
    }

    @Test
    public void testForEachWithIndexToArrayUsingImmutableList() {
        Integer[] array = new Integer[200];
        ImmutableList<Integer> list = Interval.oneTo(200).toList().toImmutable();
        Assert.assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        FJIterate.forEachWithIndex(list, ( each, index) -> array[index] = each, 10, 10);
        Assert.assertArrayEquals(array, list.toArray(new Integer[]{  }));
    }

    @Test
    public void testForEachWithIndexToArrayUsingArrayList() {
        Integer[] array = new Integer[200];
        MutableList<Integer> list = FastList.newList(Interval.oneTo(200));
        Assert.assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        FJIterate.forEachWithIndex(list, ( each, index) -> array[index] = each, 10, 10);
        Assert.assertArrayEquals(array, list.toArray(new Integer[]{  }));
    }

    @Test
    public void testForEachWithIndexToArrayUsingFixedArrayList() {
        Integer[] array = new Integer[10];
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Assert.assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        FJIterate.forEachWithIndex(list, ( each, index) -> array[index] = each, 1, 2);
        Assert.assertArrayEquals(array, list.toArray(new Integer[list.size()]));
    }

    @Test
    public void testForEachWithIndexException() {
        Verify.assertThrows(RuntimeException.class, () -> FJIterate.forEachWithIndex(FJIterateAcceptanceTest.createIntegerList(5), new PassThruObjectIntProcedureFactory<>(EXCEPTION_OBJECT_INT_PROCEDURE), new PassThruCombiner<>(), 1, 5));
    }

    @Test
    public void select() {
        this.iterables.forEach(Procedures.cast(this::basicSelect));
    }

    @Test
    public void selectSortedSet() {
        RichIterable<Integer> iterable = Interval.oneTo(20000).toSortedSet();
        Collection<Integer> actual1 = FJIterate.select(iterable, Predicates.greaterThan(10000));
        Collection<Integer> actual2 = FJIterate.select(iterable, Predicates.greaterThan(10000), true);
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
    public void aggregateInPlaceBy() {
        Procedure2<AtomicInteger, Integer> countAggregator = ( aggregate, value) -> aggregate.incrementAndGet();
        List<Integer> list = Interval.oneTo(20000);
        MutableMap<String, AtomicInteger> aggregation = FJIterate.aggregateInPlaceBy(list, FJIterateAcceptanceTest.EVEN_OR_ODD, AtomicInteger::new, countAggregator);
        Assert.assertEquals(10000, aggregation.get("Even").intValue());
        Assert.assertEquals(10000, aggregation.get("Odd").intValue());
        FJIterate.aggregateInPlaceBy(list, FJIterateAcceptanceTest.EVEN_OR_ODD, AtomicInteger::new, countAggregator, aggregation);
        Assert.assertEquals(20000, aggregation.get("Even").intValue());
        Assert.assertEquals(20000, aggregation.get("Odd").intValue());
    }

    @Test
    public void aggregateInPlaceByWithBatchSize() {
        MutableList<Integer> list = LazyIterate.adapt(Collections.nCopies(1000, 1)).concatenate(Collections.nCopies(2000, 2)).concatenate(Collections.nCopies(3000, 3)).toList().shuffleThis();
        MapIterable<String, AtomicInteger> aggregation = FJIterate.aggregateInPlaceBy(list, String::valueOf, AtomicInteger::new, AtomicInteger::addAndGet, 100);
        Assert.assertEquals(1000, aggregation.get("1").intValue());
        Assert.assertEquals(4000, aggregation.get("2").intValue());
        Assert.assertEquals(9000, aggregation.get("3").intValue());
    }

    private class RecursiveProcedure implements Procedure<Integer> {
        private static final long serialVersionUID = 1L;

        private final ForkJoinPool executorService = new ForkJoinPool(ParallelIterate.getDefaultMaxThreadPoolSize());

        @Override
        public void value(Integer level) {
            if (level > 0) {
                FJIterateAcceptanceTest.this.threadNames.add(Thread.currentThread().getName());
                this.executeFJIterate((level - 1), this.executorService);
            } else {
                this.simulateWork();
            }
        }

        private void simulateWork() {
            synchronized(FJIterateAcceptanceTest.this) {
                (FJIterateAcceptanceTest.this.count)++;
            }
        }

        private void executeFJIterate(int level, ForkJoinPool executorService) {
            MutableList<Integer> items = mutable.of();
            for (int i = 0; i < 20000; i++) {
                items.add(((i % 1000) == 0 ? level : 0));
            }
            FJIterate.forEach(items, new FJIterateAcceptanceTest.RecursiveProcedure(), executorService);
        }
    }

    public static final class IntegerSum {
        private int sum;

        public IntegerSum(int newSum) {
            this.sum = newSum;
        }

        public FJIterateAcceptanceTest.IntegerSum add(int value) {
            this.sum += value;
            return this;
        }

        public int getSum() {
            return this.sum;
        }
    }

    public static final class SumProcedure implements Function2<FJIterateAcceptanceTest.IntegerSum, Integer, FJIterateAcceptanceTest.IntegerSum> , Procedure<Integer> , ProcedureFactory<FJIterateAcceptanceTest.SumProcedure> {
        private static final long serialVersionUID = 1L;

        private final FJIterateAcceptanceTest.IntegerSum sum;

        public SumProcedure(FJIterateAcceptanceTest.IntegerSum newSum) {
            this.sum = newSum;
        }

        @Override
        public FJIterateAcceptanceTest.SumProcedure create() {
            return new FJIterateAcceptanceTest.SumProcedure(new FJIterateAcceptanceTest.IntegerSum(0));
        }

        @Override
        public FJIterateAcceptanceTest.IntegerSum value(FJIterateAcceptanceTest.IntegerSum s1, Integer s2) {
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

    public static final class SumCombiner extends AbstractProcedureCombiner<FJIterateAcceptanceTest.SumProcedure> {
        private static final long serialVersionUID = 1L;

        private final FJIterateAcceptanceTest.IntegerSum sum;

        public SumCombiner(FJIterateAcceptanceTest.IntegerSum initialSum) {
            super(true);
            this.sum = initialSum;
        }

        @Override
        public void combineOne(FJIterateAcceptanceTest.SumProcedure sumProcedure) {
            this.sum.add(sumProcedure.getSum());
        }
    }
}

