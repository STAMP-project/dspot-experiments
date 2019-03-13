/**
 * Copyright 2015, 2017 StreamEx contributors
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
package one.util.streamex;


import java.util.BitSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 *
 *
 * @author Tagir Valeev
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IntCollectorTest {
    @Test
    public void testJoining() {
        String expected = IntStream.range(0, 10000).mapToObj(String::valueOf).collect(Collectors.joining(", "));
        Assert.assertEquals(expected, IntStreamEx.range(10000).collect(IntCollector.joining(", ")));
        Assert.assertEquals(expected, IntStreamEx.range(10000).parallel().collect(IntCollector.joining(", ")));
        String expected2 = IntStreamEx.range(0, 1000).boxed().toList().toString();
        Assert.assertEquals(expected2, IntStreamEx.range(1000).collect(IntCollector.joining(", ", "[", "]")));
        Assert.assertEquals(expected2, IntStreamEx.range(1000).parallel().collect(IntCollector.joining(", ", "[", "]")));
    }

    @Test
    public void testCounting() {
        Assert.assertEquals(5000L, ((long) (IntStreamEx.range(10000).atLeast(5000).collect(IntCollector.counting()))));
        Assert.assertEquals(5000L, ((long) (IntStreamEx.range(10000).parallel().atLeast(5000).collect(IntCollector.counting()))));
        Assert.assertEquals(5000, ((int) (IntStreamEx.range(10000).atLeast(5000).collect(IntCollector.countingInt()))));
        Assert.assertEquals(5000, ((int) (IntStreamEx.range(10000).parallel().atLeast(5000).collect(IntCollector.countingInt()))));
    }

    @Test
    public void testReducing() {
        Assert.assertEquals(120, ((int) (IntStreamEx.rangeClosed(1, 5).collect(IntCollector.reducing(1, ( a, b) -> a * b)))));
        Assert.assertEquals(120, ((int) (IntStreamEx.rangeClosed(1, 5).parallel().collect(IntCollector.reducing(1, ( a, b) -> a * b)))));
    }

    @Test
    public void testCollectingAndThen() {
        Assert.assertEquals(9, ((int) (IntStreamEx.rangeClosed(1, 5).collect(IntCollector.joining(",").andThen(String::length)))));
    }

    @Test
    public void testSumming() {
        Assert.assertEquals(3725, ((int) (IntStreamEx.range(100).atLeast(50).collect(IntCollector.summing()))));
        Assert.assertEquals(3725, ((int) (IntStreamEx.range(100).parallel().atLeast(50).collect(IntCollector.summing()))));
        TestHelpers.withRandom(( r) -> {
            int[] input = IntStreamEx.of(r, 10000, 1, 1000).toArray();
            Map<Boolean, Integer> expected = IntStream.of(input).boxed().collect(Collectors.partitioningBy(( i) -> (i % 2) == 0, Collectors.summingInt(Integer::intValue)));
            Map<Boolean, Integer> sumEvenOdd = IntStreamEx.of(input).collect(IntCollector.partitioningBy(( i) -> (i % 2) == 0, IntCollector.summing()));
            Assert.assertEquals(expected, sumEvenOdd);
            sumEvenOdd = IntStreamEx.of(input).parallel().collect(IntCollector.partitioningBy(( i) -> (i % 2) == 0, IntCollector.summing()));
            Assert.assertEquals(expected, sumEvenOdd);
        });
    }

    @Test
    public void testMin() {
        Assert.assertEquals(50, IntStreamEx.range(100).atLeast(50).collect(IntCollector.min()).getAsInt());
        Assert.assertFalse(IntStreamEx.range(100).atLeast(200).collect(IntCollector.min()).isPresent());
    }

    @Test
    public void testMax() {
        Assert.assertEquals(99, IntStreamEx.range(100).atLeast(50).collect(IntCollector.max()).getAsInt());
        Assert.assertEquals(99, IntStreamEx.range(100).parallel().atLeast(50).collect(IntCollector.max()).getAsInt());
        Assert.assertFalse(IntStreamEx.range(100).atLeast(200).collect(IntCollector.max()).isPresent());
    }

    @Test
    public void testSummarizing() {
        TestHelpers.withRandom(( r) -> {
            int[] data = IntStreamEx.of(r, 1000, 1, Integer.MAX_VALUE).toArray();
            IntSummaryStatistics expected = IntStream.of(data).summaryStatistics();
            IntSummaryStatistics statistics = IntStreamEx.of(data).collect(IntCollector.summarizing());
            Assert.assertEquals(expected.getCount(), statistics.getCount());
            Assert.assertEquals(expected.getSum(), statistics.getSum());
            Assert.assertEquals(expected.getMax(), statistics.getMax());
            Assert.assertEquals(expected.getMin(), statistics.getMin());
            statistics = IntStreamEx.of(data).parallel().collect(IntCollector.summarizing());
            Assert.assertEquals(expected.getCount(), statistics.getCount());
            Assert.assertEquals(expected.getSum(), statistics.getSum());
            Assert.assertEquals(expected.getMax(), statistics.getMax());
            Assert.assertEquals(expected.getMin(), statistics.getMin());
        });
    }

    @Test
    public void testToArray() {
        Assert.assertArrayEquals(new int[]{ 0, 1, 2, 3, 4 }, IntStreamEx.of(0, 1, 2, 3, 4).collect(IntCollector.toArray()));
        Assert.assertArrayEquals(IntStreamEx.range(1000).toByteArray(), IntStreamEx.range(1000).collect(IntCollector.toByteArray()));
        Assert.assertArrayEquals(IntStreamEx.range(1000).toCharArray(), IntStreamEx.range(1000).collect(IntCollector.toCharArray()));
        Assert.assertArrayEquals(IntStreamEx.range(1000).toShortArray(), IntStreamEx.range(1000).collect(IntCollector.toShortArray()));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Test
    public void testPartitioning() {
        int[] expectedEven = IntStream.range(0, 1000).map(( i) -> i * 2).toArray();
        int[] expectedOdd = IntStream.range(0, 1000).map(( i) -> (i * 2) + 1).toArray();
        Map<Boolean, int[]> oddEven = IntStreamEx.range(2000).collect(IntCollector.partitioningBy(( i) -> (i % 2) == 0));
        Assert.assertTrue(oddEven.containsKey(true));
        Assert.assertTrue(oddEven.containsKey(false));
        Assert.assertFalse(oddEven.containsKey(null));
        Assert.assertFalse(oddEven.containsKey(0));
        Assert.assertArrayEquals(expectedEven, oddEven.get(true));
        Assert.assertArrayEquals(expectedOdd, oddEven.get(false));
        Assert.assertNull(oddEven.get(null));
        Assert.assertNull(oddEven.get(0));
        Assert.assertEquals(2, oddEven.entrySet().size());
        oddEven = IntStreamEx.range(2000).parallel().collect(IntCollector.partitioningBy(( i) -> (i % 2) == 0));
        Assert.assertArrayEquals(expectedEven, oddEven.get(true));
        Assert.assertArrayEquals(expectedOdd, oddEven.get(false));
        IntCollector<?, Map<Boolean, int[]>> partitionMapToArray = IntCollector.partitioningBy(( i) -> (i % 2) == 0, IntCollector.mapping(( i) -> i / 2, IntCollector.toArray()));
        oddEven = IntStreamEx.range(2000).collect(partitionMapToArray);
        int[] ints = IntStreamEx.range(1000).toArray();
        Assert.assertArrayEquals(ints, oddEven.get(true));
        Assert.assertArrayEquals(ints, oddEven.get(false));
        Map<Boolean, IntSummaryStatistics> sums = IntStreamEx.rangeClosed(0, 100).collect(IntCollector.partitioningBy(( i) -> (i % 2) == 0, IntCollector.summarizing()));
        Assert.assertEquals(2500, sums.get(false).getSum());
        Assert.assertEquals(2550, sums.get(true).getSum());
    }

    @Test
    public void testSumBySign() {
        TestHelpers.withRandom(( r) -> {
            int[] input = r.ints(2000, (-1000), 1000).toArray();
            Map<Boolean, Integer> sums = IntStreamEx.of(input).collect(IntCollector.partitioningBy(( i) -> i > 0, IntCollector.summing()));
            Map<Boolean, Integer> sumsBoxed = IntStream.of(input).boxed().collect(Collectors.partitioningBy(( i) -> i > 0, Collectors.summingInt(Integer::intValue)));
            Assert.assertEquals(sumsBoxed, sums);
        });
    }

    @Test
    public void testGroupingBy() {
        Map<Integer, int[]> collected = IntStreamEx.range(2000).collect(IntCollector.groupingBy(( i) -> i % 3));
        for (int i = 0; i < 3; i++) {
            int rem = i;
            Assert.assertArrayEquals(IntStream.range(0, 2000).filter(( a) -> (a % 3) == rem).toArray(), collected.get(i));
        }
        collected = IntStreamEx.range(2000).parallel().collect(IntCollector.groupingBy(( i) -> i % 3));
        for (int i = 0; i < 3; i++) {
            int rem = i;
            Assert.assertArrayEquals(IntStream.range(0, 2000).filter(( a) -> (a % 3) == rem).toArray(), collected.get(i));
        }
        Map<Integer, BitSet> mapBitSet = IntStreamEx.range(10).collect(IntCollector.groupingBy(( i) -> i % 3, IntCollector.toBitSet()));
        Assert.assertEquals("{0, 3, 6, 9}", mapBitSet.get(0).toString());
        Assert.assertEquals("{1, 4, 7}", mapBitSet.get(1).toString());
        Assert.assertEquals("{2, 5, 8}", mapBitSet.get(2).toString());
    }

    @Test
    public void testByDigit() {
        TestHelpers.withRandom(( r) -> {
            int[] input = r.ints(2000, (-1000), 1000).toArray();
            IntCollector<?, Map<Integer, List<Integer>>> collector = IntCollector.groupingBy(( i) -> i % 10, IntCollector.of(Collectors.toList()));
            Map<Integer, List<Integer>> groups = IntStreamEx.of(input).collect(collector);
            Map<Integer, List<Integer>> groupsBoxed = IntStream.of(input).boxed().collect(Collectors.groupingBy(( i) -> i % 10));
            Assert.assertEquals(groupsBoxed, groups);
        });
    }

    @Test
    public void testAsCollector() {
        Assert.assertEquals(499500, ((int) (IntStream.range(0, 1000).boxed().collect(IntCollector.summing()))));
        Assert.assertEquals(499500, ((int) (IntStream.range(0, 1000).boxed().parallel().collect(IntCollector.summing()))));
        Assert.assertEquals(1000, ((long) (IntStream.range(0, 1000).boxed().collect(IntCollector.counting()))));
    }

    @Test
    public void testAdaptor() {
        Assert.assertEquals(499500, ((int) (IntStreamEx.range(0, 1000).collect(IntCollector.of(IntCollector.summing())))));
        Assert.assertEquals(499500, ((int) (IntStreamEx.range(0, 1000).collect(IntCollector.of(Collectors.summingInt(Integer::intValue))))));
    }

    @Test
    public void testMapping() {
        Assert.assertArrayEquals(IntStreamEx.range(1000).asDoubleStream().toArray(), IntStreamEx.range(1000).collect(IntCollector.mappingToObj(( i) -> ((double) (i)), DoubleCollector.toArray())), 0.0);
    }

    @Test
    public void testAveraging() {
        Assert.assertFalse(IntStreamEx.empty().collect(IntCollector.averaging()).isPresent());
        Assert.assertEquals(Integer.MAX_VALUE, IntStreamEx.of(Integer.MAX_VALUE, Integer.MAX_VALUE).collect(IntCollector.averaging()).getAsDouble(), 1);
        Assert.assertEquals(Integer.MAX_VALUE, IntStreamEx.of(Integer.MAX_VALUE, Integer.MAX_VALUE).parallel().collect(IntCollector.averaging()).getAsDouble(), 1);
    }

    @Test
    public void testToBooleanArray() {
        Assert.assertArrayEquals(new boolean[0], IntStreamEx.empty().collect(IntCollector.toBooleanArray(( x) -> true)));
        boolean[] expected = new boolean[]{ true, false, false, true };
        Assert.assertArrayEquals(expected, IntStreamEx.of((-1), 2, 3, (-4)).collect(IntCollector.toBooleanArray(( x) -> x < 0)));
        Assert.assertArrayEquals(expected, IntStreamEx.of((-1), 2, 3, (-4)).parallel().collect(IntCollector.toBooleanArray(( x) -> x < 0)));
    }
}

