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


import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;
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
public class LongStreamExTest {
    final LongConsumer EMPTY = ( l) -> {
        // nothing
    };

    @Test
    public void testCreate() {
        Assert.assertArrayEquals(new long[]{  }, LongStreamEx.empty().toArray());
        // double test is intended
        Assert.assertArrayEquals(new long[]{  }, LongStreamEx.empty().toArray());
        Assert.assertArrayEquals(new long[]{ 1 }, LongStreamEx.of(1).toArray());
        Assert.assertArrayEquals(new long[]{ 1 }, LongStreamEx.of(OptionalLong.of(1)).toArray());
        Assert.assertArrayEquals(new long[]{  }, LongStreamEx.of(OptionalLong.empty()).toArray());
        Assert.assertArrayEquals(new long[]{ 1, 2, 3 }, LongStreamEx.of(1, 2, 3).toArray());
        Assert.assertArrayEquals(new long[]{ 4, 6 }, LongStreamEx.of(new long[]{ 2, 4, 6, 8, 10 }, 1, 3).toArray());
        Assert.assertArrayEquals(new long[]{ 1, 2, 3 }, LongStreamEx.of(LongStream.of(1, 2, 3)).toArray());
        Assert.assertArrayEquals(new long[]{ 1, 2, 3 }, LongStreamEx.of(Arrays.asList(1L, 2L, 3L)).toArray());
        Assert.assertArrayEquals(new long[]{ 1, 2, 3 }, LongStreamEx.range(1L, 4L).toArray());
        Assert.assertArrayEquals(new long[]{ 0, 1, 2 }, LongStreamEx.range(3L).toArray());
        Assert.assertArrayEquals(new long[]{ 1, 2, 3 }, LongStreamEx.rangeClosed(1, 3).toArray());
        Assert.assertArrayEquals(new long[]{ 1, 1, 1, 1 }, LongStreamEx.generate(() -> 1).limit(4).toArray());
        Assert.assertArrayEquals(new long[]{ 1, 1, 1, 1 }, LongStreamEx.constant(1L, 4).toArray());
        Assert.assertEquals(10, LongStreamEx.of(new Random(), 10).count());
        Assert.assertTrue(LongStreamEx.of(new Random(), 100, 1, 10).allMatch(( x) -> (x >= 1) && (x < 10)));
        Assert.assertArrayEquals(LongStreamEx.of(new Random(1), 100, 1, 10).toArray(), LongStreamEx.of(new Random(1), 1, 10).limit(100).toArray());
        Assert.assertArrayEquals(LongStreamEx.of(new Random(1), 100).toArray(), LongStreamEx.of(new Random(1)).limit(100).toArray());
        LongStream stream = LongStreamEx.of(1, 2, 3);
        Assert.assertSame(stream, LongStreamEx.of(stream));
        Assert.assertArrayEquals(new long[]{ 4, 2, 0, -2, -4 }, LongStreamEx.zip(new long[]{ 5, 4, 3, 2, 1 }, new long[]{ 1, 2, 3, 4, 5 }, ( a, b) -> a - b).toArray());
        Assert.assertArrayEquals(new long[]{ 1, 5, 3 }, LongStreamEx.of(Spliterators.spliterator(new long[]{ 1, 5, 3 }, 0)).toArray());
        Assert.assertArrayEquals(new long[]{ 1, 5, 3 }, LongStreamEx.of(Spliterators.iterator(Spliterators.spliterator(new long[]{ 1, 5, 3 }, 0))).toArray());
        Assert.assertArrayEquals(new long[0], LongStreamEx.of(Spliterators.iterator(Spliterators.emptyLongSpliterator())).parallel().toArray());
        Assert.assertArrayEquals(new long[]{ 2, 4, 6 }, LongStreamEx.of(new Long[]{ 2L, 4L, 6L }).toArray());
    }

    @Test
    public void testOfLongBuffer() {
        long[] data = LongStreamEx.range(100).toArray();
        Assert.assertArrayEquals(data, LongStreamEx.of(LongBuffer.wrap(data)).toArray());
        Assert.assertArrayEquals(LongStreamEx.range(50, 70).toArray(), LongStreamEx.of(LongBuffer.wrap(data, 50, 20)).toArray());
        Assert.assertArrayEquals(data, LongStreamEx.of(LongBuffer.wrap(data)).parallel().toArray());
        Assert.assertArrayEquals(LongStreamEx.range(50, 70).toArray(), LongStreamEx.of(LongBuffer.wrap(data, 50, 20)).parallel().toArray());
    }

    @Test
    public void testIterate() {
        Assert.assertArrayEquals(new long[]{ 1, 2, 4, 8, 16 }, LongStreamEx.iterate(1, ( x) -> x * 2).limit(5).toArray());
        Assert.assertArrayEquals(new long[]{ 1, 2, 4, 8, 16, 32, 64 }, LongStreamEx.iterate(1, ( x) -> x < 100, ( x) -> x * 2).toArray());
        Assert.assertEquals(0, LongStreamEx.iterate(0, ( x) -> x < 0, ( x) -> 1 / x).count());
        Assert.assertFalse(LongStreamEx.iterate(1, ( x) -> x < 100, ( x) -> x * 2).has(10));
        TestHelpers.checkSpliterator("iterate", () -> LongStreamEx.iterate(1, ( x) -> x < 100, ( x) -> x * 2).spliterator());
    }

    @Test
    public void testLongs() {
        Assert.assertEquals(Long.MAX_VALUE, LongStreamEx.longs().spliterator().getExactSizeIfKnown());
        Assert.assertArrayEquals(new long[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, LongStreamEx.longs().limit(10).toArray());
    }

    @Test
    public void testRangeStep() {
        Assert.assertArrayEquals(new long[]{ 0 }, LongStreamEx.range(0, 1000, 100000).toArray());
        Assert.assertArrayEquals(new long[]{ 0, (Long.MAX_VALUE) - 1 }, LongStreamEx.range(0, Long.MAX_VALUE, ((Long.MAX_VALUE) - 1)).toArray());
        Assert.assertArrayEquals(new long[]{ Long.MIN_VALUE, -1, (Long.MAX_VALUE) - 1 }, LongStreamEx.range(Long.MIN_VALUE, Long.MAX_VALUE, Long.MAX_VALUE).toArray());
        Assert.assertArrayEquals(new long[]{ Long.MIN_VALUE, -1 }, LongStreamEx.range(Long.MIN_VALUE, ((Long.MAX_VALUE) - 1), Long.MAX_VALUE).toArray());
        Assert.assertArrayEquals(new long[]{ Long.MAX_VALUE, -1 }, LongStreamEx.range(Long.MAX_VALUE, Long.MIN_VALUE, Long.MIN_VALUE).toArray());
        Assert.assertArrayEquals(new long[]{ Long.MAX_VALUE }, LongStreamEx.range(Long.MAX_VALUE, 0, Long.MIN_VALUE).toArray());
        Assert.assertArrayEquals(new long[]{ 1, (Long.MIN_VALUE) + 1 }, LongStreamEx.range(1, Long.MIN_VALUE, Long.MIN_VALUE).toArray());
        Assert.assertArrayEquals(new long[]{ 0 }, LongStreamEx.range(0, Long.MIN_VALUE, Long.MIN_VALUE).toArray());
        Assert.assertArrayEquals(new long[]{ 0, 2, 4, 6, 8 }, LongStreamEx.range(0, 9, 2).toArray());
        Assert.assertArrayEquals(new long[]{ 0, 2, 4, 6 }, LongStreamEx.range(0, 8, 2).toArray());
        Assert.assertArrayEquals(new long[]{ 0, -2, -4, -6, -8 }, LongStreamEx.range(0, (-9), (-2)).toArray());
        Assert.assertArrayEquals(new long[]{ 0, -2, -4, -6 }, LongStreamEx.range(0, (-8), (-2)).toArray());
        Assert.assertArrayEquals(new long[]{ 5, 4, 3, 2, 1, 0 }, LongStreamEx.range(5, (-1), (-1)).toArray());
        Assert.assertEquals(((Integer.MAX_VALUE) + 1L), LongStreamEx.range(Integer.MIN_VALUE, Integer.MAX_VALUE, 2).spliterator().getExactSizeIfKnown());
        Assert.assertEquals(Long.MAX_VALUE, LongStreamEx.range(Long.MIN_VALUE, ((Long.MAX_VALUE) - 1), 2).spliterator().getExactSizeIfKnown());
        Spliterator.OfLong spliterator = LongStreamEx.range(Long.MAX_VALUE, Long.MIN_VALUE, (-2)).spliterator();
        Assert.assertEquals((-1), spliterator.getExactSizeIfKnown());
        Assert.assertTrue(spliterator.tryAdvance(EMPTY));
        Assert.assertEquals(Long.MAX_VALUE, spliterator.estimateSize());
        Assert.assertTrue(spliterator.tryAdvance(EMPTY));
        Assert.assertEquals(((Long.MAX_VALUE) - 1), spliterator.estimateSize());
        Assert.assertEquals(Long.MAX_VALUE, LongStreamEx.range(Long.MAX_VALUE, ((Long.MIN_VALUE) + 1), (-2)).spliterator().getExactSizeIfKnown());
        Assert.assertEquals((-1), LongStreamEx.range(Long.MIN_VALUE, Long.MAX_VALUE, 1).spliterator().getExactSizeIfKnown());
        Assert.assertEquals((-1), LongStreamEx.range(Long.MAX_VALUE, Long.MIN_VALUE, (-1)).spliterator().getExactSizeIfKnown());
        Assert.assertEquals(0, LongStreamEx.range(0, (-1000), 1).count());
        Assert.assertEquals(0, LongStreamEx.range(0, 1000, (-1)).count());
        Assert.assertEquals(0, LongStreamEx.range(0, 0, (-1)).count());
        Assert.assertEquals(0, LongStreamEx.range(0, 0, 1).count());
        Assert.assertEquals(0, LongStreamEx.range(0, (-1000), 2).count());
        Assert.assertEquals(0, LongStreamEx.range(0, 1000, (-2)).count());
        Assert.assertEquals(0, LongStreamEx.range(0, 0, (-2)).count());
        Assert.assertEquals(0, LongStreamEx.range(0, 0, 2).count());
        Assert.assertEquals(0, LongStreamEx.range(0, Long.MIN_VALUE, 2).spliterator().getExactSizeIfKnown());
        Assert.assertEquals(0, LongStreamEx.range(0, Long.MAX_VALUE, (-2)).spliterator().getExactSizeIfKnown());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangeIllegalStep() {
        LongStreamEx.range(0, 1000, 0);
    }

    @Test
    public void testRangeClosedStep() {
        Assert.assertArrayEquals(new long[]{ 0 }, LongStreamEx.rangeClosed(0, 1000, 100000).toArray());
        Assert.assertArrayEquals(new long[]{ 0, 1000 }, LongStreamEx.rangeClosed(0, 1000, 1000).toArray());
        Assert.assertArrayEquals(new long[]{ 0, (Long.MAX_VALUE) - 1 }, LongStreamEx.rangeClosed(0, ((Long.MAX_VALUE) - 1), ((Long.MAX_VALUE) - 1)).toArray());
        Assert.assertArrayEquals(new long[]{ Long.MIN_VALUE, -1, (Long.MAX_VALUE) - 1 }, LongStreamEx.rangeClosed(Long.MIN_VALUE, ((Long.MAX_VALUE) - 1), Long.MAX_VALUE).toArray());
        Assert.assertArrayEquals(new long[]{ Long.MIN_VALUE, -1 }, LongStreamEx.rangeClosed(Long.MIN_VALUE, ((Long.MAX_VALUE) - 2), Long.MAX_VALUE).toArray());
        Assert.assertArrayEquals(new long[]{ Long.MAX_VALUE, -1 }, LongStreamEx.rangeClosed(Long.MAX_VALUE, Long.MIN_VALUE, Long.MIN_VALUE).toArray());
        Assert.assertArrayEquals(new long[]{ Long.MAX_VALUE }, LongStreamEx.rangeClosed(Long.MAX_VALUE, 0, Long.MIN_VALUE).toArray());
        Assert.assertArrayEquals(new long[]{ 0, Long.MIN_VALUE }, LongStreamEx.rangeClosed(0, Long.MIN_VALUE, Long.MIN_VALUE).toArray());
        Assert.assertArrayEquals(new long[]{ 0, 2, 4, 6, 8 }, LongStreamEx.rangeClosed(0, 9, 2).toArray());
        Assert.assertArrayEquals(new long[]{ 0, 2, 4, 6, 8 }, LongStreamEx.rangeClosed(0, 8, 2).toArray());
        Assert.assertArrayEquals(new long[]{ 0, 2, 4, 6 }, LongStreamEx.rangeClosed(0, 7, 2).toArray());
        Assert.assertArrayEquals(new long[]{ 0, -2, -4, -6, -8 }, LongStreamEx.rangeClosed(0, (-9), (-2)).toArray());
        Assert.assertArrayEquals(new long[]{ 0, -2, -4, -6, -8 }, LongStreamEx.rangeClosed(0, (-8), (-2)).toArray());
        Assert.assertArrayEquals(new long[]{ 0, -2, -4, -6 }, LongStreamEx.rangeClosed(0, (-7), (-2)).toArray());
        Assert.assertArrayEquals(new long[]{ 5, 4, 3, 2, 1, 0 }, LongStreamEx.rangeClosed(5, 0, (-1)).toArray());
        Assert.assertEquals(((Integer.MAX_VALUE) + 1L), LongStreamEx.rangeClosed(Integer.MIN_VALUE, Integer.MAX_VALUE, 2).spliterator().getExactSizeIfKnown());
        Assert.assertEquals(Long.MAX_VALUE, LongStreamEx.rangeClosed(Long.MIN_VALUE, ((Long.MAX_VALUE) - 2), 2).spliterator().getExactSizeIfKnown());
        Spliterator.OfLong spliterator = LongStreamEx.rangeClosed(Long.MAX_VALUE, Long.MIN_VALUE, (-2)).spliterator();
        Assert.assertEquals((-1), spliterator.getExactSizeIfKnown());
        Assert.assertTrue(spliterator.tryAdvance(EMPTY));
        Assert.assertEquals(Long.MAX_VALUE, spliterator.estimateSize());
        Assert.assertTrue(spliterator.tryAdvance(EMPTY));
        Assert.assertEquals(((Long.MAX_VALUE) - 1), spliterator.estimateSize());
        Assert.assertEquals(Long.MAX_VALUE, LongStreamEx.rangeClosed(Long.MAX_VALUE, ((Long.MIN_VALUE) + 2), (-2)).spliterator().getExactSizeIfKnown());
        Assert.assertEquals((-1), LongStreamEx.rangeClosed(Long.MIN_VALUE, Long.MAX_VALUE, 1).spliterator().getExactSizeIfKnown());
        Assert.assertEquals((-1), LongStreamEx.rangeClosed(Long.MAX_VALUE, Long.MIN_VALUE, (-1)).spliterator().getExactSizeIfKnown());
        Assert.assertEquals(0, LongStreamEx.rangeClosed(0, (-1000), 1).count());
        Assert.assertEquals(0, LongStreamEx.rangeClosed(0, 1000, (-1)).count());
        Assert.assertEquals(0, LongStreamEx.rangeClosed(0, 1, (-1)).count());
        Assert.assertEquals(0, LongStreamEx.rangeClosed(0, (-1), 1).count());
        Assert.assertEquals(0, LongStreamEx.rangeClosed(0, (-1000), 2).count());
        Assert.assertEquals(0, LongStreamEx.rangeClosed(0, 1000, (-2)).count());
        Assert.assertEquals(0, LongStreamEx.rangeClosed(0, 1, (-2)).count());
        Assert.assertEquals(0, LongStreamEx.rangeClosed(0, (-1), 2).count());
        Assert.assertEquals(0, LongStreamEx.rangeClosed(0, Long.MIN_VALUE, 2).spliterator().getExactSizeIfKnown());
        Assert.assertEquals(0, LongStreamEx.rangeClosed(0, Long.MAX_VALUE, (-2)).spliterator().getExactSizeIfKnown());
    }

    @Test
    public void testBasics() {
        Assert.assertFalse(LongStreamEx.of(1).isParallel());
        Assert.assertTrue(LongStreamEx.of(1).parallel().isParallel());
        Assert.assertFalse(LongStreamEx.of(1).parallel().sequential().isParallel());
        AtomicInteger i = new AtomicInteger();
        try (LongStreamEx s = LongStreamEx.of(1).onClose(i::incrementAndGet)) {
            Assert.assertEquals(1, s.count());
        }
        Assert.assertEquals(1, i.get());
        Assert.assertEquals(6, LongStreamEx.range(0, 4).sum());
        Assert.assertEquals(3, LongStreamEx.range(0, 4).max().getAsLong());
        Assert.assertEquals(0, LongStreamEx.range(0, 4).min().getAsLong());
        Assert.assertEquals(1.5, LongStreamEx.range(0, 4).average().getAsDouble(), 1.0E-6);
        Assert.assertEquals(4, LongStreamEx.range(0, 4).summaryStatistics().getCount());
        Assert.assertArrayEquals(new long[]{ 1, 2, 3 }, LongStreamEx.range(0, 5).skip(1).limit(3).toArray());
        Assert.assertArrayEquals(new long[]{ 1, 2, 3 }, LongStreamEx.of(3, 1, 2).sorted().toArray());
        Assert.assertArrayEquals(new long[]{ 1, 2, 3 }, LongStreamEx.of(1, 2, 1, 3, 2).distinct().toArray());
        Assert.assertArrayEquals(new int[]{ 2, 4, 6 }, LongStreamEx.range(1, 4).mapToInt(( x) -> ((int) (x)) * 2).toArray());
        Assert.assertArrayEquals(new long[]{ 2, 4, 6 }, LongStreamEx.range(1, 4).map(( x) -> x * 2).toArray());
        Assert.assertArrayEquals(new double[]{ 2, 4, 6 }, LongStreamEx.range(1, 4).mapToDouble(( x) -> x * 2).toArray(), 0.0);
        Assert.assertArrayEquals(new long[]{ 1, 3 }, LongStreamEx.range(0, 5).filter(( x) -> (x % 2) == 1).toArray());
        Assert.assertEquals(6, LongStreamEx.of(1, 2, 3).reduce(Long::sum).getAsLong());
        Assert.assertEquals(Long.MAX_VALUE, LongStreamEx.rangeClosed(1, Long.MAX_VALUE).spliterator().getExactSizeIfKnown());
        Assert.assertTrue(LongStreamEx.of(1, 2, 3).spliterator().hasCharacteristics(Spliterator.ORDERED));
        Assert.assertFalse(LongStreamEx.of(1, 2, 3).unordered().spliterator().hasCharacteristics(Spliterator.ORDERED));
        PrimitiveIterator.OfLong iterator = LongStreamEx.of(1, 2, 3).iterator();
        Assert.assertEquals(1L, iterator.nextLong());
        Assert.assertEquals(2L, iterator.nextLong());
        Assert.assertEquals(3L, iterator.nextLong());
        Assert.assertFalse(iterator.hasNext());
        AtomicInteger idx = new AtomicInteger();
        long[] result = new long[500];
        LongStreamEx.range(1000).atLeast(500).parallel().forEachOrdered(( val) -> result[idx.getAndIncrement()] = val);
        Assert.assertArrayEquals(LongStreamEx.range(500, 1000).toArray(), result);
        Assert.assertTrue(LongStreamEx.empty().noneMatch(( x) -> true));
        Assert.assertFalse(LongStreamEx.of(1).noneMatch(( x) -> true));
        Assert.assertTrue(LongStreamEx.of(1).noneMatch(( x) -> false));
    }

    @Test
    public void testForEach() {
        List<Long> list = new ArrayList<>();
        LongStreamEx.of(1).forEach(list::add);
        Assert.assertEquals(Arrays.asList(1L), list);
    }

    @Test
    public void testFlatMap() {
        Assert.assertArrayEquals(new long[]{ 0, 0, 1, 0, 1, 2 }, LongStreamEx.of(1, 2, 3).flatMap(LongStreamEx::range).toArray());
        Assert.assertArrayEquals(new int[]{ 1, 5, 1, 4, 2, 0, 9, 2, 2, 3, 3, 7, 2, 0, 3, 6, 8, 5, 4, 7, 7, 5, 8, 0, 7 }, LongStreamEx.of(15, 14, 20, Long.MAX_VALUE).flatMapToInt(( n) -> String.valueOf(n).chars().map(( x) -> x - '0')).toArray());
        String expected = LongStreamEx.range(200).boxed().flatMap(( i) -> LongStreamEx.range(0, i).mapToObj(( j) -> (i + ":") + j)).joining("/");
        String res = LongStreamEx.range(200).flatMapToObj(( i) -> LongStreamEx.range(i).mapToObj(( j) -> (i + ":") + j)).joining("/");
        String parallel = LongStreamEx.range(200).parallel().flatMapToObj(( i) -> LongStreamEx.range(i).mapToObj(( j) -> (i + ":") + j)).joining("/");
        Assert.assertEquals(expected, res);
        Assert.assertEquals(expected, parallel);
        double[] fractions = LongStreamEx.range(1, 5).flatMapToDouble(( i) -> LongStreamEx.range(1, i).mapToDouble(( j) -> ((double) (j)) / i)).toArray();
        Assert.assertArrayEquals(new double[]{ 1 / 2.0, 1 / 3.0, 2 / 3.0, 1 / 4.0, 2 / 4.0, 3 / 4.0 }, fractions, 1.0E-6);
    }

    @Test
    public void testPrepend() {
        Assert.assertArrayEquals(new long[]{ -1, 0, 1, 2, 3 }, LongStreamEx.of(1, 2, 3).prepend((-1), 0).toArray());
        Assert.assertArrayEquals(new long[]{ 1, 2, 3 }, LongStreamEx.of(1, 2, 3).prepend().toArray());
        Assert.assertArrayEquals(new long[]{ 10, 11, 0, 1, 2, 3 }, LongStreamEx.range(0, 4).prepend(LongStreamEx.range(10, 12)).toArray());
    }

    @Test
    public void testAppend() {
        Assert.assertArrayEquals(new long[]{ 1, 2, 3, 4, 5 }, LongStreamEx.of(1, 2, 3).append(4, 5).toArray());
        Assert.assertArrayEquals(new long[]{ 1, 2, 3 }, LongStreamEx.of(1, 2, 3).append().toArray());
        Assert.assertArrayEquals(new long[]{ 0, 1, 2, 3, 10, 11 }, LongStreamEx.range(0, 4).append(LongStreamEx.range(10, 12)).toArray());
    }

    @Test
    public void testHas() {
        Assert.assertTrue(LongStreamEx.range(1, 4).has(3));
        Assert.assertFalse(LongStreamEx.range(1, 4).has(4));
    }

    @Test
    public void testWithout() {
        Assert.assertArrayEquals(new long[]{ 1, 2 }, LongStreamEx.range(1, 4).without(3).toArray());
        Assert.assertArrayEquals(new long[]{ 1, 2, 3 }, LongStreamEx.range(1, 4).without(5).toArray());
        LongStreamEx lse = LongStreamEx.range(5);
        Assert.assertSame(lse, lse.without());
        Assert.assertArrayEquals(new long[]{ 0, 1, 3, 4 }, LongStreamEx.range(5).without(new long[]{ 2 }).toArray());
        Assert.assertArrayEquals(new long[]{ 0 }, LongStreamEx.range(5).without(1, 2, 3, 4, 5, 6).toArray());
    }

    @Test
    public void testRanges() {
        Assert.assertArrayEquals(new long[]{ 5, 4, Long.MAX_VALUE }, LongStreamEx.of(1, 5, 3, 4, (-1), Long.MAX_VALUE).greater(3).toArray());
        Assert.assertArrayEquals(new long[]{  }, LongStreamEx.of(1, 5, 3, 4, (-1), Long.MAX_VALUE).greater(Long.MAX_VALUE).toArray());
        Assert.assertArrayEquals(new long[]{ 5, 3, 4, Long.MAX_VALUE }, LongStreamEx.of(1, 5, 3, 4, (-1), Long.MAX_VALUE).atLeast(3).toArray());
        Assert.assertArrayEquals(new long[]{ Long.MAX_VALUE }, LongStreamEx.of(1, 5, 3, 4, (-1), Long.MAX_VALUE).atLeast(Long.MAX_VALUE).toArray());
        Assert.assertArrayEquals(new long[]{ 1, -1 }, LongStreamEx.of(1, 5, 3, 4, (-1), Long.MAX_VALUE).less(3).toArray());
        Assert.assertArrayEquals(new long[]{ 1, 3, -1 }, LongStreamEx.of(1, 5, 3, 4, (-1), Long.MAX_VALUE).atMost(3).toArray());
        Assert.assertArrayEquals(new long[]{ 1, 3, 4, -1 }, LongStreamEx.of(1, 5, 3, 4, (-1), Long.MAX_VALUE).atMost(4).toArray());
    }

    @Test
    public void testFind() {
        Assert.assertEquals(6, LongStreamEx.range(1, 10).findFirst(( i) -> i > 5).getAsLong());
        Assert.assertFalse(LongStreamEx.range(1, 10).findAny(( i) -> i > 10).isPresent());
    }

    @Test
    public void testRemove() {
        Assert.assertArrayEquals(new long[]{ 1, 2 }, LongStreamEx.of(1, 2, 3).remove(( x) -> x > 2).toArray());
    }

    @Test
    public void testSort() {
        Assert.assertArrayEquals(new long[]{ 0, 3, 6, 1, 4, 7, 2, 5, 8 }, LongStreamEx.range(0, 9).sortedByLong(( i) -> ((i % 3) * 3) + (i / 3)).toArray());
        Assert.assertArrayEquals(new long[]{ 0, 4, 8, 1, 5, 9, 2, 6, 3, 7 }, LongStreamEx.range(0, 10).sortedByInt(( i) -> ((int) (i)) % 4).toArray());
        Assert.assertArrayEquals(new long[]{ 10, 11, 5, 6, 7, 8, 9 }, LongStreamEx.range(5, 12).sortedBy(String::valueOf).toArray());
        Assert.assertArrayEquals(new long[]{ Long.MAX_VALUE, 1000, 1, 0, -10, Long.MIN_VALUE }, LongStreamEx.of(0, 1, 1000, (-10), Long.MIN_VALUE, Long.MAX_VALUE).reverseSorted().toArray());
        Assert.assertArrayEquals(new long[]{ Long.MAX_VALUE, Long.MIN_VALUE, (Long.MIN_VALUE) + 1, (Long.MAX_VALUE) - 1 }, LongStreamEx.of(Long.MIN_VALUE, ((Long.MIN_VALUE) + 1), ((Long.MAX_VALUE) - 1), Long.MAX_VALUE).sortedByLong(( l) -> l + 1).toArray());
        Assert.assertArrayEquals(new long[]{ -10, Long.MIN_VALUE, Long.MAX_VALUE, 1000, 1, 0 }, LongStreamEx.of(0, 1, 1000, (-10), Long.MIN_VALUE, Long.MAX_VALUE).sortedByDouble(( x) -> 1.0 / x).toArray());
    }

    @Test
    public void testMinMax() {
        LongStreamExTest.checkEmpty(( s) -> s.maxBy(Long::valueOf), ( s) -> s.maxByInt(( x) -> ((int) (x))), ( s) -> s.maxByLong(( x) -> x), ( s) -> s.maxByDouble(( x) -> x), ( s) -> s.minBy(Long::valueOf), ( s) -> s.minByInt(( x) -> ((int) (x))), ( s) -> s.minByLong(( x) -> x), ( s) -> s.minByDouble(( x) -> x));
        Assert.assertEquals(9, LongStreamEx.range(5, 12).max(Comparator.comparing(String::valueOf)).getAsLong());
        Assert.assertEquals(10, LongStreamEx.range(5, 12).min(Comparator.comparing(String::valueOf)).getAsLong());
        Assert.assertEquals(9, LongStreamEx.range(5, 12).maxBy(String::valueOf).getAsLong());
        Assert.assertEquals(10, LongStreamEx.range(5, 12).minBy(String::valueOf).getAsLong());
        Assert.assertEquals(5, LongStreamEx.range(5, 12).maxByDouble(( x) -> 1.0 / x).getAsLong());
        Assert.assertEquals(11, LongStreamEx.range(5, 12).minByDouble(( x) -> 1.0 / x).getAsLong());
        Assert.assertEquals(29, LongStreamEx.of(15, 8, 31, 47, 19, 29).maxByInt(( x) -> ((int) (((x % 10) * 10) + (x / 10)))).getAsLong());
        Assert.assertEquals(31, LongStreamEx.of(15, 8, 31, 47, 19, 29).minByInt(( x) -> ((int) (((x % 10) * 10) + (x / 10)))).getAsLong());
        Assert.assertEquals(29, LongStreamEx.of(15, 8, 31, 47, 19, 29).maxByLong(( x) -> ((x % 10) * 10) + (x / 10)).getAsLong());
        Assert.assertEquals(31, LongStreamEx.of(15, 8, 31, 47, 19, 29).minByLong(( x) -> ((x % 10) * 10) + (x / 10)).getAsLong());
        Supplier<LongStreamEx> s = () -> LongStreamEx.of(1, 50, 120, 35, 130, 12, 0);
        LongToIntFunction intKey = ( x) -> String.valueOf(x).length();
        LongUnaryOperator longKey = ( x) -> String.valueOf(x).length();
        LongToDoubleFunction doubleKey = ( x) -> String.valueOf(x).length();
        LongFunction<Integer> objKey = ( x) -> String.valueOf(x).length();
        List<Function<LongStreamEx, OptionalLong>> minFns = Arrays.asList(( is) -> is.minByInt(intKey), ( is) -> is.minByLong(longKey), ( is) -> is.minByDouble(doubleKey), ( is) -> is.minBy(objKey));
        List<Function<LongStreamEx, OptionalLong>> maxFns = Arrays.asList(( is) -> is.maxByInt(intKey), ( is) -> is.maxByLong(longKey), ( is) -> is.maxByDouble(doubleKey), ( is) -> is.maxBy(objKey));
        minFns.forEach(( fn) -> assertEquals(1, fn.apply(s.get()).getAsLong()));
        minFns.forEach(( fn) -> assertEquals(1, fn.apply(s.get().parallel()).getAsLong()));
        maxFns.forEach(( fn) -> assertEquals(120, fn.apply(s.get()).getAsLong()));
        maxFns.forEach(( fn) -> assertEquals(120, fn.apply(s.get().parallel()).getAsLong()));
    }

    @Test
    public void testPairMap() {
        Assert.assertEquals(0, LongStreamEx.range(0).pairMap(Long::sum).count());
        Assert.assertEquals(0, LongStreamEx.range(1).pairMap(Long::sum).count());
        Assert.assertArrayEquals(new long[]{ 6, 7, 8, 9, 10 }, LongStreamEx.of(1, 5, 2, 6, 3, 7).pairMap(Long::sum).toArray());
        Assert.assertArrayEquals(LongStreamEx.range(999).map(( x) -> (x * 2) + 1).toArray(), LongStreamEx.range(1000).parallel().map(( x) -> x * x).pairMap(( a, b) -> b - a).toArray());
        Assert.assertArrayEquals(LongStreamEx.range(1, 100).toArray(), LongStreamEx.range(100).map(( i) -> (i * (i + 1)) / 2).append(LongStream.empty()).parallel().pairMap(( a, b) -> b - a).toArray());
        Assert.assertArrayEquals(LongStreamEx.range(1, 100).toArray(), LongStreamEx.range(100).map(( i) -> (i * (i + 1)) / 2).prepend(LongStream.empty()).parallel().pairMap(( a, b) -> b - a).toArray());
        Assert.assertEquals(1, LongStreamEx.range(1000).map(( x) -> x * x).pairMap(( a, b) -> b - a).pairMap(( a, b) -> b - a).distinct().count());
        Assert.assertFalse(LongStreamEx.range(1000).greater(2000).parallel().pairMap(( a, b) -> a).findFirst().isPresent());
    }

    @Test
    public void testJoining() {
        Assert.assertEquals("0,1,2,3,4,5,6,7,8,9", LongStreamEx.range(10).joining(","));
        Assert.assertEquals("0,1,2,3,4,5,6,7,8,9", LongStreamEx.range(10).parallel().joining(","));
        Assert.assertEquals("[0,1,2,3,4,5,6,7,8,9]", LongStreamEx.range(10).joining(",", "[", "]"));
        Assert.assertEquals("[0,1,2,3,4,5,6,7,8,9]", LongStreamEx.range(10).parallel().joining(",", "[", "]"));
    }

    @Test
    public void testMapToEntry() {
        Map<Long, List<Long>> result = LongStreamEx.range(10).mapToEntry(( x) -> x % 2, ( x) -> x).grouping();
        Assert.assertEquals(Arrays.asList(0L, 2L, 4L, 6L, 8L), result.get(0L));
        Assert.assertEquals(Arrays.asList(1L, 3L, 5L, 7L, 9L), result.get(1L));
    }

    @Test
    public void testRecreate() {
        Assert.assertEquals(500, ((long) (LongStreamEx.iterate(0, ( i) -> i + 1).skipOrdered(1).greater(0).boxed().parallel().findAny(( i) -> i == 500).get())));
        Assert.assertEquals(500, ((long) (LongStreamEx.iterate(0, ( i) -> i + 1).parallel().skipOrdered(1).greater(0).boxed().findAny(( i) -> i == 500).get())));
    }

    @Test
    public void testTakeWhile() {
        Assert.assertArrayEquals(LongStreamEx.range(100).toArray(), LongStreamEx.iterate(0, ( i) -> i + 1).takeWhile(( i) -> i < 100).toArray());
        Assert.assertEquals(0, LongStreamEx.iterate(0, ( i) -> i + 1).takeWhile(( i) -> i < 0).count());
        Assert.assertEquals(1, LongStreamEx.of(1, 3, 2).takeWhile(( i) -> i < 3).count());
        Assert.assertEquals(3, LongStreamEx.of(1, 2, 3).takeWhile(( i) -> i < 100).count());
    }

    @Test
    public void testTakeWhileInclusive() {
        Assert.assertArrayEquals(LongStreamEx.range(101).toArray(), LongStreamEx.iterate(0, ( i) -> i + 1).takeWhileInclusive(( i) -> i < 100).toArray());
        Assert.assertEquals(1, LongStreamEx.iterate(0, ( i) -> i + 1).takeWhileInclusive(( i) -> i < 0).count());
        Assert.assertEquals(2, LongStreamEx.of(1, 3, 2).takeWhileInclusive(( i) -> i < 3).count());
        Assert.assertEquals(3, LongStreamEx.of(1, 2, 3).takeWhileInclusive(( i) -> i < 100).count());
    }

    @Test
    public void testDropWhile() {
        Assert.assertArrayEquals(new long[]{ 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 }, LongStreamEx.range(100).dropWhile(( i) -> (i % 10) < 5).limit(10).toArray());
        Assert.assertEquals(100, LongStreamEx.range(100).dropWhile(( i) -> (i % 10) < 0).count());
        Assert.assertEquals(0, LongStreamEx.range(100).dropWhile(( i) -> (i % 10) < 10).count());
        Assert.assertEquals(OptionalLong.of(0), LongStreamEx.range(100).dropWhile(( i) -> (i % 10) < 0).findFirst());
        Assert.assertEquals(OptionalLong.empty(), LongStreamEx.range(100).dropWhile(( i) -> (i % 10) < 10).findFirst());
        Spliterator.OfLong spltr = LongStreamEx.range(100).dropWhile(( i) -> (i % 10) < 1).spliterator();
        Assert.assertTrue(spltr.tryAdvance((long x) -> Assert.assertEquals(1, x)));
        LongStream.Builder builder = LongStream.builder();
        spltr.forEachRemaining(builder);
        Assert.assertArrayEquals(LongStreamEx.range(2, 100).toArray(), builder.build().toArray());
    }

    @Test
    public void testIndexOf() {
        Assert.assertEquals(5, LongStreamEx.range(50, 100).indexOf(55).getAsLong());
        Assert.assertFalse(LongStreamEx.range(50, 100).indexOf(200).isPresent());
        Assert.assertEquals(5, LongStreamEx.range(50, 100).parallel().indexOf(55).getAsLong());
        Assert.assertFalse(LongStreamEx.range(50, 100).parallel().indexOf(200).isPresent());
        Assert.assertEquals(11, LongStreamEx.range(50, 100).indexOf(( x) -> x > 60).getAsLong());
        Assert.assertFalse(LongStreamEx.range(50, 100).indexOf(( x) -> x < 0).isPresent());
        Assert.assertEquals(11, LongStreamEx.range(50, 100).parallel().indexOf(( x) -> x > 60).getAsLong());
        Assert.assertFalse(LongStreamEx.range(50, 100).parallel().indexOf(( x) -> x < 0).isPresent());
    }

    @Test
    public void testFoldLeft() {
        // non-associative
        LongBinaryOperator accumulator = ( x, y) -> (x + y) * (x + y);
        Assert.assertEquals(2322576, LongStreamEx.constant(3, 4).foldLeft(accumulator).orElse((-1)));
        Assert.assertEquals(2322576, LongStreamEx.constant(3, 4).parallel().foldLeft(accumulator).orElse((-1)));
        Assert.assertFalse(LongStreamEx.empty().foldLeft(accumulator).isPresent());
        Assert.assertEquals(144, LongStreamEx.rangeClosed(1, 3).foldLeft(0L, accumulator));
        Assert.assertEquals(144, LongStreamEx.rangeClosed(1, 3).parallel().foldLeft(0L, accumulator));
    }

    @Test
    public void testMapFirstLast() {
        Assert.assertArrayEquals(new long[]{ -1, 2, 3, 4, 7 }, LongStreamEx.of(1, 2, 3, 4, 5).mapFirst(( x) -> x - 2L).mapLast(( x) -> x + 2L).toArray());
    }

    @Test
    public void testPeekFirst() {
        long[] input = new long[]{ 1, 10, 100, 1000 };
        AtomicLong firstElement = new AtomicLong();
        Assert.assertArrayEquals(new long[]{ 10, 100, 1000 }, LongStreamEx.of(input).peekFirst(firstElement::set).skip(1).toArray());
        Assert.assertEquals(1, firstElement.get());
        Assert.assertArrayEquals(new long[]{ 10, 100, 1000 }, LongStreamEx.of(input).skip(1).peekFirst(firstElement::set).toArray());
        Assert.assertEquals(10, firstElement.get());
        firstElement.set((-1));
        Assert.assertArrayEquals(new long[]{  }, LongStreamEx.of(input).skip(4).peekFirst(firstElement::set).toArray());
        Assert.assertEquals((-1), firstElement.get());
    }

    @Test
    public void testPeekLast() {
        long[] input = new long[]{ 1, 10, 100, 1000 };
        AtomicLong lastElement = new AtomicLong((-1));
        Assert.assertArrayEquals(new long[]{ 1, 10, 100 }, LongStreamEx.of(input).peekLast(lastElement::set).limit(3).toArray());
        Assert.assertEquals((-1), lastElement.get());
        Assert.assertArrayEquals(new long[]{ 1, 10, 100 }, LongStreamEx.of(input).less(1000).peekLast(lastElement::set).limit(3).toArray());
        Assert.assertEquals(100, lastElement.get());
        Assert.assertArrayEquals(input, LongStreamEx.of(input).peekLast(lastElement::set).limit(4).toArray());
        Assert.assertEquals(1000, lastElement.get());
        Assert.assertArrayEquals(new long[]{ 1, 10, 100 }, LongStreamEx.of(input).limit(3).peekLast(lastElement::set).toArray());
        Assert.assertEquals(100, lastElement.get());
    }

    @Test
    public void testScanLeft() {
        Assert.assertArrayEquals(new long[]{ 0, 1, 3, 6, 10, 15, 21, 28, 36, 45 }, LongStreamEx.range(10).scanLeft(Long::sum));
        Assert.assertArrayEquals(new long[]{ 0, 1, 3, 6, 10, 15, 21, 28, 36, 45 }, LongStreamEx.range(10).parallel().scanLeft(Long::sum));
        Assert.assertArrayEquals(new long[]{ 0, 1, 3, 6, 10, 15, 21, 28, 36, 45 }, LongStreamEx.range(10).filter(( x) -> true).scanLeft(Long::sum));
        Assert.assertArrayEquals(new long[]{ 0, 1, 3, 6, 10, 15, 21, 28, 36, 45 }, LongStreamEx.range(10).filter(( x) -> true).parallel().scanLeft(Long::sum));
        Assert.assertArrayEquals(new long[]{ 1, 1, 2, 6, 24, 120 }, LongStreamEx.rangeClosed(1, 5).scanLeft(1, ( a, b) -> a * b));
        Assert.assertArrayEquals(new long[]{ 1, 1, 2, 6, 24, 120 }, LongStreamEx.rangeClosed(1, 5).parallel().scanLeft(1, ( a, b) -> a * b));
    }

    @Test
    public void testProduce() {
        Scanner sc = new Scanner("1 2 3 4 20000000000 test");
        Assert.assertArrayEquals(new long[]{ 1, 2, 3, 4, 20000000000L }, LongStreamExTest.scannerLongs(sc).stream().toArray());
        Assert.assertEquals("test", sc.next());
    }

    @Test
    public void testPrefix() {
        Assert.assertArrayEquals(new long[]{ 1, 3, 6, 10, 20 }, LongStreamEx.of(1, 2, 3, 4, 10).prefix(Long::sum).toArray());
        Assert.assertEquals(OptionalLong.of(10), LongStreamEx.of(1, 2, 3, 4, 10).prefix(Long::sum).findFirst(( x) -> x > 7));
        Assert.assertEquals(OptionalLong.empty(), LongStreamEx.of(1, 2, 3, 4, 10).prefix(Long::sum).findFirst(( x) -> x > 20));
    }

    @Test
    public void testIntersperse() {
        Assert.assertArrayEquals(new long[]{ 1, 0, 10, 0, 100, 0, 1000 }, LongStreamEx.of(1, 10, 100, 1000).intersperse(0).toArray());
        Assert.assertEquals(0L, IntStreamEx.empty().intersperse(1).count());
    }
}

