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


import java.util.HashMap;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.OptionalLong;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
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
public class LongCollectorTest {
    @Test
    public void testJoining() {
        String expected = LongStream.range(0, 10000).mapToObj(String::valueOf).collect(Collectors.joining(", "));
        Assert.assertEquals(expected, LongStreamEx.range(10000).collect(LongCollector.joining(", ")));
        Assert.assertEquals(expected, LongStreamEx.range(10000).parallel().collect(LongCollector.joining(", ")));
        String expected2 = LongStreamEx.range(0, 1000).boxed().toList().toString();
        Assert.assertEquals(expected2, LongStreamEx.range(1000).collect(LongCollector.joining(", ", "[", "]")));
        Assert.assertEquals(expected2, LongStreamEx.range(1000).parallel().collect(LongCollector.joining(", ", "[", "]")));
    }

    @Test
    public void testCounting() {
        Assert.assertEquals(5000L, ((long) (LongStreamEx.range(10000).atLeast(5000).collect(LongCollector.counting()))));
        Assert.assertEquals(5000L, ((long) (LongStreamEx.range(10000).parallel().atLeast(5000).collect(LongCollector.counting()))));
        Assert.assertEquals(5000, ((int) (LongStreamEx.range(10000).atLeast(5000).collect(LongCollector.countingInt()))));
        Assert.assertEquals(5000, ((int) (LongStreamEx.range(10000).parallel().atLeast(5000).collect(LongCollector.countingInt()))));
    }

    @Test
    public void testSumming() {
        Assert.assertEquals(3725, ((long) (LongStreamEx.range(100).atLeast(50).collect(LongCollector.summing()))));
        Assert.assertEquals(3725, ((long) (LongStreamEx.range(100).parallel().atLeast(50).collect(LongCollector.summing()))));
    }

    @Test
    public void testMin() {
        Assert.assertEquals(50, LongStreamEx.range(100).atLeast(50).collect(LongCollector.min()).getAsLong());
        Assert.assertFalse(LongStreamEx.range(100).atLeast(200).collect(LongCollector.min()).isPresent());
    }

    @Test
    public void testMax() {
        Assert.assertEquals(99, LongStreamEx.range(100).atLeast(50).collect(LongCollector.max()).getAsLong());
        Assert.assertEquals(99, LongStreamEx.range(100).parallel().atLeast(50).collect(LongCollector.max()).getAsLong());
        Assert.assertFalse(LongStreamEx.range(100).atLeast(200).collect(LongCollector.max()).isPresent());
    }

    @Test
    public void testSummarizing() {
        TestHelpers.withRandom(( r) -> {
            long[] data = LongStreamEx.of(r, 1000, 1, Long.MAX_VALUE).toArray();
            LongSummaryStatistics expected = LongStream.of(data).summaryStatistics();
            LongSummaryStatistics statistics = LongStreamEx.of(data).collect(LongCollector.summarizing());
            Assert.assertEquals(expected.getCount(), statistics.getCount());
            Assert.assertEquals(expected.getSum(), statistics.getSum());
            Assert.assertEquals(expected.getMax(), statistics.getMax());
            Assert.assertEquals(expected.getMin(), statistics.getMin());
            statistics = LongStreamEx.of(data).parallel().collect(LongCollector.summarizing());
            Assert.assertEquals(expected.getCount(), statistics.getCount());
            Assert.assertEquals(expected.getSum(), statistics.getSum());
            Assert.assertEquals(expected.getMax(), statistics.getMax());
            Assert.assertEquals(expected.getMin(), statistics.getMin());
        });
    }

    @Test
    public void testToArray() {
        Assert.assertArrayEquals(new long[]{ 0, 1, 2, 3, 4 }, LongStreamEx.of(0, 1, 2, 3, 4).collect(LongCollector.toArray()));
    }

    @Test
    public void testProduct() {
        Assert.assertEquals(24L, ((long) (LongStreamEx.of(1, 2, 3, 4).collect(LongCollector.reducing(1, ( a, b) -> a * b)))));
        Assert.assertEquals(24L, ((long) (LongStreamEx.of(1, 2, 3, 4).parallel().collect(LongCollector.reducing(1, ( a, b) -> a * b)))));
        Assert.assertEquals(24L, ((long) (LongStreamEx.of(1, 2, 3, 4).collect(LongCollector.reducing(( a, b) -> a * b).andThen(OptionalLong::getAsLong)))));
    }

    @Test
    public void testPartitioning() {
        long[] expectedEven = LongStream.range(0, 1000).map(( i) -> i * 2).toArray();
        long[] expectedOdd = LongStream.range(0, 1000).map(( i) -> (i * 2) + 1).toArray();
        Map<Boolean, long[]> oddEven = LongStreamEx.range(2000).collect(LongCollector.partitioningBy(( i) -> (i % 2) == 0));
        Assert.assertArrayEquals(expectedEven, oddEven.get(true));
        Assert.assertArrayEquals(expectedOdd, oddEven.get(false));
        oddEven = LongStreamEx.range(2000).parallel().collect(LongCollector.partitioningBy(( i) -> (i % 2) == 0));
        Assert.assertArrayEquals(expectedEven, oddEven.get(true));
        Assert.assertArrayEquals(expectedOdd, oddEven.get(false));
    }

    @Test
    public void testParts() {
        LongCollector<?, Map<Boolean, String>> collector = LongCollector.partitioningBy(( i) -> (i % 2) == 0, LongCollector.mapping(( i) -> i / 3, LongCollector.joining(",")));
        LongCollector<?, Map<Boolean, String>> collector2 = LongCollector.partitioningBy(( i) -> (i % 2) == 0, LongCollector.mappingToObj(( i) -> i / 3, LongCollector.joining(",")));
        Map<Boolean, String> expected = new HashMap<>();
        expected.put(true, "0,0,1,2,2");
        expected.put(false, "0,1,1,2,3");
        Map<Boolean, String> parts = LongStreamEx.range(10).parallel().collect(collector);
        Assert.assertEquals(expected, parts);
        Assert.assertEquals(parts, expected);
        Map<Boolean, String> parts2 = LongStreamEx.range(10).parallel().collect(collector2);
        Assert.assertEquals(expected, parts2);
        Assert.assertEquals(parts2, expected);
    }

    @Test
    public void testGroupingBy() {
        Map<Long, long[]> collected = LongStreamEx.range(2000).collect(LongCollector.groupingBy(( i) -> i % 3));
        for (long i = 0; i < 3; i++) {
            long rem = i;
            Assert.assertArrayEquals(LongStream.range(0, 2000).filter(( a) -> (a % 3) == rem).toArray(), collected.get(i));
        }
        collected = LongStreamEx.range(2000).parallel().collect(LongCollector.groupingBy(( i) -> i % 3));
        for (long i = 0; i < 3; i++) {
            long rem = i;
            Assert.assertArrayEquals(LongStream.range(0, 2000).filter(( a) -> (a % 3) == rem).toArray(), collected.get(i));
        }
    }

    @Test
    public void testAsCollector() {
        Assert.assertEquals(10000499500L, ((long) (LongStream.range(10000000, 10001000).boxed().collect(LongCollector.summing()))));
        Assert.assertEquals(10000499500L, ((long) (LongStream.range(10000000, 10001000).boxed().parallel().collect(LongCollector.summing()))));
        Assert.assertEquals(1000, ((long) (LongStream.range(0, 1000).boxed().collect(LongCollector.counting()))));
    }

    @Test
    public void testAdaptor() {
        Assert.assertEquals(10000499500L, ((long) (LongStreamEx.range(10000000, 10001000).collect(LongCollector.of(LongCollector.summing())))));
        Assert.assertEquals(10000499500L, ((long) (LongStreamEx.range(10000000, 10001000).collect(LongCollector.of(Collectors.summingLong(Long::longValue))))));
    }

    @Test
    public void testAveraging() {
        Assert.assertFalse(LongStreamEx.empty().collect(LongCollector.averaging()).isPresent());
        Assert.assertEquals(Long.MAX_VALUE, LongStreamEx.of(Long.MAX_VALUE, Long.MAX_VALUE).collect(LongCollector.averaging()).getAsDouble(), 1);
        Assert.assertEquals(Long.MAX_VALUE, LongStreamEx.of(Long.MAX_VALUE, Long.MAX_VALUE).parallel().collect(LongCollector.averaging()).getAsDouble(), 1);
    }

    @Test
    public void testToBooleanArray() {
        Assert.assertArrayEquals(new boolean[0], LongStreamEx.empty().collect(LongCollector.toBooleanArray(( x) -> true)));
        boolean[] expected = new boolean[]{ true, false, false, false, true };
        Assert.assertArrayEquals(expected, LongStreamEx.of(Long.MIN_VALUE, Integer.MIN_VALUE, 0, Integer.MAX_VALUE, Long.MAX_VALUE).collect(LongCollector.toBooleanArray(( x) -> (x < Integer.MIN_VALUE) || (x > Integer.MAX_VALUE))));
        Assert.assertArrayEquals(expected, LongStreamEx.of(Long.MIN_VALUE, Integer.MIN_VALUE, 0, Integer.MAX_VALUE, Long.MAX_VALUE).parallel().collect(LongCollector.toBooleanArray(( x) -> (x < Integer.MIN_VALUE) || (x > Integer.MAX_VALUE))));
    }
}

