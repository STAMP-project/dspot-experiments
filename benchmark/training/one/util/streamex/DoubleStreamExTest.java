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


import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
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
public class DoubleStreamExTest {
    @Test
    public void testCreate() {
        Assert.assertArrayEquals(new double[]{  }, DoubleStreamEx.empty().toArray(), 0.0);
        // double check is intended
        Assert.assertArrayEquals(new double[]{  }, DoubleStreamEx.empty().toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1 }, DoubleStreamEx.of(1).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1 }, DoubleStreamEx.of(OptionalDouble.of(1)).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{  }, DoubleStreamEx.of(OptionalDouble.empty()).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, 2, 3 }, DoubleStreamEx.of(1, 2, 3).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 4, 6 }, DoubleStreamEx.of(new double[]{ 2, 4, 6, 8, 10 }, 1, 3).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, 2, 3 }, DoubleStreamEx.of(1.0F, 2.0F, 3.0F).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 4, 6 }, DoubleStreamEx.of(new float[]{ 2.0F, 4.0F, 6.0F, 8.0F, 10.0F }, 1, 3).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, 2, 3 }, DoubleStreamEx.of(DoubleStream.of(1, 2, 3)).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, 2, 3 }, DoubleStreamEx.of(Arrays.asList(1.0, 2.0, 3.0)).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, 2, 4, 8, 16 }, DoubleStreamEx.iterate(1, ( x) -> x * 2).limit(5).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, 1, 1, 1 }, DoubleStreamEx.generate(() -> 1).limit(4).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, 1, 1, 1 }, DoubleStreamEx.constant(1.0, 4).toArray(), 0.0);
        Assert.assertEquals(10, DoubleStreamEx.of(new Random(), 10).count());
        Assert.assertArrayEquals(DoubleStreamEx.of(new Random(1), 10).toArray(), DoubleStreamEx.of(new Random(1)).limit(10).toArray(), 0.0);
        Assert.assertTrue(DoubleStreamEx.of(new Random(), 100, 1, 10).allMatch(( x) -> (x >= 1) && (x < 10)));
        Assert.assertArrayEquals(DoubleStreamEx.of(new Random(1), 100, 1, 10).toArray(), DoubleStreamEx.of(new Random(1), 1, 10).limit(100).toArray(), 0.0);
        DoubleStream stream = DoubleStreamEx.of(1, 2, 3);
        Assert.assertSame(stream, DoubleStreamEx.of(stream));
        Assert.assertArrayEquals(new double[]{ 1, 5, 3 }, DoubleStreamEx.of(Spliterators.spliterator(new double[]{ 1, 5, 3 }, 0)).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, 5, 3 }, DoubleStreamEx.of(Spliterators.iterator(Spliterators.spliterator(new double[]{ 1, 5, 3 }, 0))).toArray(), 0.0);
        Assert.assertArrayEquals(new double[0], DoubleStreamEx.of(Spliterators.iterator(Spliterators.emptyDoubleSpliterator())).parallel().toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 2, 4, 6 }, DoubleStreamEx.of(new Double[]{ 2.0, 4.0, 6.0 }).toArray(), 0.0);
    }

    @Test
    public void testOfDoubleBuffer() {
        double[] data = LongStreamEx.range(100).asDoubleStream().toArray();
        Assert.assertArrayEquals(data, DoubleStreamEx.of(DoubleBuffer.wrap(data)).toArray(), 0.0);
        Assert.assertArrayEquals(LongStreamEx.range(50, 70).asDoubleStream().toArray(), DoubleStreamEx.of(DoubleBuffer.wrap(data, 50, 20)).toArray(), 0.0);
        Assert.assertArrayEquals(data, DoubleStreamEx.of(DoubleBuffer.wrap(data)).parallel().toArray(), 0.0);
        Assert.assertArrayEquals(LongStreamEx.range(50, 70).asDoubleStream().toArray(), DoubleStreamEx.of(DoubleBuffer.wrap(data, 50, 20)).parallel().toArray(), 0.0);
    }

    @Test
    public void testIterate() {
        Assert.assertArrayEquals(new double[]{ 1, 2, 4, 8, 16 }, DoubleStreamEx.iterate(1, ( x) -> x * 2).limit(5).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, 2, 4, 8, 16, 32, 64 }, DoubleStreamEx.iterate(1, ( x) -> x < 100, ( x) -> x * 2).toArray(), 0.0);
        Assert.assertEquals(0, DoubleStreamEx.iterate(0, ( x) -> x < 0, ( x) -> 1 / x).count());
        Assert.assertFalse(DoubleStreamEx.iterate(1, ( x) -> x < 100, ( x) -> x * 2).findFirst(( x) -> x == 10).isPresent());
        TestHelpers.checkSpliterator("iterate", () -> DoubleStreamEx.iterate(1, ( x) -> x < 100, ( x) -> x * 2).spliterator());
    }

    @Test
    public void testBasics() {
        Assert.assertFalse(DoubleStreamEx.of(1).isParallel());
        Assert.assertTrue(DoubleStreamEx.of(1).parallel().isParallel());
        Assert.assertFalse(DoubleStreamEx.of(1).parallel().sequential().isParallel());
        AtomicInteger i = new AtomicInteger();
        try (DoubleStreamEx s = DoubleStreamEx.of(1).onClose(i::incrementAndGet)) {
            Assert.assertEquals(1, s.count());
        }
        Assert.assertEquals(1, i.get());
        Assert.assertEquals(6, IntStreamEx.range(0, 4).asDoubleStream().sum(), 0);
        Assert.assertEquals(3, IntStreamEx.range(0, 4).asDoubleStream().max().getAsDouble(), 0);
        Assert.assertEquals(0, IntStreamEx.range(0, 4).asDoubleStream().min().getAsDouble(), 0);
        Assert.assertEquals(1.5, IntStreamEx.range(0, 4).asDoubleStream().average().getAsDouble(), 1.0E-6);
        Assert.assertEquals(4, IntStreamEx.range(0, 4).asDoubleStream().summaryStatistics().getCount());
        Assert.assertArrayEquals(new double[]{ 1, 2, 3 }, IntStreamEx.range(0, 5).asDoubleStream().skip(1).limit(3).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, 2, 3 }, DoubleStreamEx.of(3, 1, 2).sorted().toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, 2, 3 }, DoubleStreamEx.of(1, 2, 1, 3, 2).distinct().toArray(), 0.0);
        Assert.assertArrayEquals(new int[]{ 2, 4, 6 }, IntStreamEx.range(1, 4).asDoubleStream().mapToInt(( x) -> ((int) (x)) * 2).toArray());
        Assert.assertArrayEquals(new long[]{ 2, 4, 6 }, IntStreamEx.range(1, 4).asDoubleStream().mapToLong(( x) -> ((long) (x)) * 2).toArray());
        Assert.assertArrayEquals(new double[]{ 2, 4, 6 }, IntStreamEx.range(1, 4).asDoubleStream().map(( x) -> x * 2).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, 3 }, IntStreamEx.range(0, 5).asDoubleStream().filter(( x) -> (x % 2) == 1).toArray(), 0.0);
        Assert.assertEquals(6.0, DoubleStreamEx.of(1.0, 2.0, 3.0).reduce(Double::sum).getAsDouble(), 0.0);
        Assert.assertEquals(Long.MAX_VALUE, LongStreamEx.rangeClosed(1, Long.MAX_VALUE).asDoubleStream().spliterator().getExactSizeIfKnown());
        Assert.assertArrayEquals(new double[]{ 4, 2, 0, -2, -4 }, DoubleStreamEx.zip(new double[]{ 5, 4, 3, 2, 1 }, new double[]{ 1, 2, 3, 4, 5 }, ( a, b) -> a - b).toArray(), 0.0);
        Assert.assertEquals("1.0; 0.5; 0.25; 0.125", DoubleStreamEx.of(1.0, 0.5, 0.25, 0.125).mapToObj(String::valueOf).joining("; "));
        List<Double> list = new ArrayList<>();
        DoubleStreamEx.of(1.0, 0.5, 0.25, 0.125).forEach(list::add);
        Assert.assertEquals(Arrays.asList(1.0, 0.5, 0.25, 0.125), list);
        list = new ArrayList<>();
        DoubleStreamEx.of(1.0, 0.5, 0.25, 0.125).parallel().forEachOrdered(list::add);
        Assert.assertEquals(Arrays.asList(1.0, 0.5, 0.25, 0.125), list);
        Assert.assertFalse(DoubleStreamEx.of(1.0, 2.0, 2.5).anyMatch(( x) -> x < 0.0));
        Assert.assertTrue(DoubleStreamEx.of(1.0, 2.0, 2.5).anyMatch(( x) -> x >= 2.5));
        Assert.assertTrue(DoubleStreamEx.of(1.0, 2.0, 2.5).noneMatch(( x) -> x < 0.0));
        Assert.assertFalse(DoubleStreamEx.of(1.0, 2.0, 2.5).noneMatch(( x) -> x >= 2.5));
        Assert.assertEquals(5.0, DoubleStreamEx.of(1.0, 2.0, 2.5).reduce(1, ( a, b) -> a * b), 0.0);
        Assert.assertTrue(DoubleStreamEx.of(1, 2, 3).spliterator().hasCharacteristics(Spliterator.ORDERED));
        Assert.assertFalse(DoubleStreamEx.of(1, 2, 3).unordered().spliterator().hasCharacteristics(Spliterator.ORDERED));
        PrimitiveIterator.OfDouble iterator = DoubleStreamEx.of(1.0, 2.0, 3.0).iterator();
        Assert.assertEquals(1.0, iterator.next(), 0.0);
        Assert.assertEquals(2.0, iterator.next(), 0.0);
        Assert.assertEquals(3.0, iterator.next(), 0.0);
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testFlatMap() {
        Assert.assertArrayEquals(new int[]{ 1, 5, 2, 3, 3, 2, 0, 9 }, DoubleStreamEx.of(1.5, 2.3, 3.2, 0.9).flatMapToInt(( x) -> IntStreamEx.of(((int) (Math.floor(x))), ((int) (Math.round((10 * (x - (Math.floor(x))))))))).toArray());
        Assert.assertEquals("1:.:5:2:2:.:3:3:.:2:0:.:9", DoubleStreamEx.of(1.5, 22.3, 3.2, 0.9).flatMapToObj(( x) -> StreamEx.split(String.valueOf(x), "")).joining(":"));
        Assert.assertArrayEquals(new double[]{ 0.0, 0.0, 1.0, 0.0, 1.0, 2.0 }, DoubleStreamEx.of(1, 2, 3).flatMap(( x) -> IntStreamEx.range(((int) (x))).asDoubleStream()).toArray(), 0.0);
        Assert.assertArrayEquals(new long[]{ 4607182418800017408L, 4607182418800017408L, 4611686018427387904L, 4611686018427387904L, 9221120237041090560L, 9221120237041090560L }, DoubleStreamEx.of(1, 2, Double.NaN).flatMapToLong(( x) -> LongStream.of(Double.doubleToLongBits(x), Double.doubleToRawLongBits(x))).toArray());
    }

    @Test
    public void testPrepend() {
        Assert.assertArrayEquals(new double[]{ -1, 0, 1, 2, 3 }, DoubleStreamEx.of(1, 2, 3).prepend((-1), 0).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ -1, 0, 1, 2, 3 }, DoubleStreamEx.of(1, 2, 3).prepend(DoubleStream.of((-1), 0)).toArray(), 0.0);
        DoubleStreamEx s = DoubleStreamEx.of(1, 2, 3);
        Assert.assertSame(s, s.prepend());
    }

    @Test
    public void testAppend() {
        Assert.assertArrayEquals(new double[]{ 1, 2, 3, 4, 5 }, DoubleStreamEx.of(1, 2, 3).append(4, 5).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, 2, 3, 4, 5 }, DoubleStreamEx.of(1, 2, 3).append(DoubleStream.of(4, 5)).toArray(), 0.0);
        DoubleStreamEx s = DoubleStreamEx.of(1, 2, 3);
        Assert.assertSame(s, s.append());
    }

    @Test
    public void testRanges() {
        Assert.assertArrayEquals(new double[]{ 5, 4, Double.POSITIVE_INFINITY }, DoubleStreamEx.of(1, 5, 3, 4, (-1), Double.POSITIVE_INFINITY).greater(3).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{  }, DoubleStreamEx.of(1, 5, 3, 4, (-1), Double.POSITIVE_INFINITY).greater(Double.POSITIVE_INFINITY).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 5, 3, 4, Double.POSITIVE_INFINITY }, DoubleStreamEx.of(1, 5, 3, 4, (-1), Double.POSITIVE_INFINITY).atLeast(3).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ Double.POSITIVE_INFINITY }, DoubleStreamEx.of(1, 5, 3, 4, (-1), Double.POSITIVE_INFINITY).atLeast(Double.POSITIVE_INFINITY).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, -1 }, DoubleStreamEx.of(1, 5, 3, 4, (-1), Double.POSITIVE_INFINITY).less(3).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, 3, -1 }, DoubleStreamEx.of(1, 5, 3, 4, (-1), Double.POSITIVE_INFINITY).atMost(3).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, 3, 4, -1 }, DoubleStreamEx.of(1, 5, 3, 4, (-1), Double.POSITIVE_INFINITY).atMost(4).toArray(), 0.0);
    }

    @Test
    public void testFind() {
        Assert.assertEquals(6.0, LongStreamEx.range(1, 10).asDoubleStream().findFirst(( i) -> i > 5).getAsDouble(), 0.0);
        Assert.assertFalse(LongStreamEx.range(1, 10).asDoubleStream().findAny(( i) -> i > 10).isPresent());
    }

    @Test
    public void testRemove() {
        Assert.assertArrayEquals(new double[]{ 1, 2 }, DoubleStreamEx.of(1, 2, 3).remove(( x) -> x > 2).toArray(), 0.0);
    }

    @Test
    public void testSort() {
        Assert.assertArrayEquals(new double[]{ 3, 2, 1 }, DoubleStreamEx.of(1, 2, 3).sortedByDouble(( x) -> -x).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ Double.NaN, Double.NaN, Double.POSITIVE_INFINITY, Double.MAX_VALUE, 1000, 1, Double.MIN_VALUE, 0, -0.0, (-(Double.MIN_VALUE)) * 3, -10, (-(Double.MAX_VALUE)) / 1.1, -(Double.MAX_VALUE), Double.NEGATIVE_INFINITY }, DoubleStreamEx.of(0, 1, ((-(Double.MIN_VALUE)) * 3), 1000, (-10), (-(Double.MAX_VALUE)), Double.POSITIVE_INFINITY, Double.NaN, Double.NEGATIVE_INFINITY, Double.NaN, Double.MAX_VALUE, (-0.0), Double.MIN_VALUE, ((-(Double.MAX_VALUE)) / 1.1)).reverseSorted().toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, 10, 2, 21, 9 }, DoubleStreamEx.of(1, 10, 2, 9, 21).sortedBy(String::valueOf).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 0.4, 1.5, 1.3, 2.3, 2.1, 2.0, 3.7 }, DoubleStreamEx.of(1.5, 2.3, 1.3, 2.1, 3.7, 0.4, 2.0).sortedByInt(( x) -> ((int) (x))).toArray(), 0.0);
        Assert.assertArrayEquals(new double[]{ 0.4, 1.5, 1.3, 2.3, 2.1, 2.0, 3.7 }, DoubleStreamEx.of(1.5, 2.3, 1.3, 2.1, 3.7, 0.4, 2.0).sortedByLong(( x) -> ((long) (x))).toArray(), 0.0);
    }

    @Test
    public void testMinMax() {
        DoubleStreamExTest.checkEmpty(( s) -> s.maxBy(Double::valueOf), ( s) -> s.maxByInt(( x) -> ((int) (x))), ( s) -> s.maxByLong(( x) -> ((long) (x))), ( s) -> s.maxByDouble(( x) -> x), ( s) -> s.minBy(Double::valueOf), ( s) -> s.minByInt(( x) -> ((int) (x))), ( s) -> s.minByLong(( x) -> ((long) (x))), ( s) -> s.minByDouble(( x) -> x));
        Assert.assertEquals(9, IntStreamEx.range(5, 12).asDoubleStream().max(Comparator.comparing(String::valueOf)).getAsDouble(), 0.0);
        Assert.assertEquals(10, IntStreamEx.range(5, 12).asDoubleStream().min(Comparator.comparing(String::valueOf)).getAsDouble(), 0.0);
        Assert.assertEquals(9, IntStreamEx.range(5, 12).asDoubleStream().maxBy(String::valueOf).getAsDouble(), 0.0);
        Assert.assertEquals(10, IntStreamEx.range(5, 12).asDoubleStream().minBy(String::valueOf).getAsDouble(), 0.0);
        Assert.assertEquals(5, IntStreamEx.range(5, 12).asDoubleStream().maxByDouble(( x) -> 1.0 / x).getAsDouble(), 0.0);
        Assert.assertEquals(11, IntStreamEx.range(5, 12).asDoubleStream().minByDouble(( x) -> 1.0 / x).getAsDouble(), 0.0);
        Assert.assertEquals(29.0, DoubleStreamEx.of(15, 8, 31, 47, 19, 29).maxByInt(( x) -> ((int) (((x % 10) * 10) + (x / 10)))).getAsDouble(), 0.0);
        Assert.assertEquals(31.0, DoubleStreamEx.of(15, 8, 31, 47, 19, 29).minByInt(( x) -> ((int) (((x % 10) * 10) + (x / 10)))).getAsDouble(), 0.0);
        Assert.assertEquals(29.0, DoubleStreamEx.of(15, 8, 31, 47, 19, 29).maxByLong(( x) -> ((long) (((x % 10) * 10) + (x / 10)))).getAsDouble(), 0.0);
        Assert.assertEquals(31.0, DoubleStreamEx.of(15, 8, 31, 47, 19, 29).minByLong(( x) -> ((long) (((x % 10) * 10) + (x / 10)))).getAsDouble(), 0.0);
        Supplier<DoubleStreamEx> s = () -> DoubleStreamEx.of(1, 50, 120, 35, 130, 12, 0);
        DoubleToIntFunction intKey = ( x) -> String.valueOf(x).length();
        DoubleToLongFunction longKey = ( x) -> String.valueOf(x).length();
        DoubleUnaryOperator doubleKey = ( x) -> String.valueOf(x).length();
        DoubleFunction<Integer> objKey = ( x) -> String.valueOf(x).length();
        List<Function<DoubleStreamEx, OptionalDouble>> minFns = Arrays.asList(( is) -> is.minByInt(intKey), ( is) -> is.minByLong(longKey), ( is) -> is.minByDouble(doubleKey), ( is) -> is.minBy(objKey));
        List<Function<DoubleStreamEx, OptionalDouble>> maxFns = Arrays.asList(( is) -> is.maxByInt(intKey), ( is) -> is.maxByLong(longKey), ( is) -> is.maxByDouble(doubleKey), ( is) -> is.maxBy(objKey));
        minFns.forEach(( fn) -> assertEquals(1, fn.apply(s.get()).getAsDouble(), 0.0));
        minFns.forEach(( fn) -> assertEquals(1, fn.apply(s.get().parallel()).getAsDouble(), 0.0));
        maxFns.forEach(( fn) -> assertEquals(120, fn.apply(s.get()).getAsDouble(), 0.0));
        maxFns.forEach(( fn) -> assertEquals(120, fn.apply(s.get().parallel()).getAsDouble(), 0.0));
    }

    @Test
    public void testPairMap() {
        Assert.assertEquals(0, DoubleStreamEx.of().pairMap(Double::sum).count());
        Assert.assertEquals(0, DoubleStreamEx.of(1.0).pairMap(Double::sum).count());
        int[] data = new Random(1).ints(1000, 1, 1000).toArray();
        double[] expected = new double[(data.length) - 1];
        for (int i = 0; i < (expected.length); i++)
            expected[i] = ((data[(i + 1)]) - (data[i])) * 1.23;

        double[] result = IntStreamEx.of(data).parallel().asDoubleStream().pairMap(( a, b) -> (b - a) * 1.23).toArray();
        Assert.assertArrayEquals(expected, result, 0.0);
        result = IntStreamEx.of(data).asDoubleStream().pairMap(( a, b) -> (b - a) * 1.23).toArray();
        Assert.assertArrayEquals(expected, result, 0.0);
        Assert.assertEquals(984.0, IntStreamEx.of(data).asDoubleStream().parallel().pairMap(( a, b) -> Math.abs((a - b))).max().getAsDouble(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1.0, 1.0 }, DoubleStreamEx.of(1.0, 2.0, 3.0).append().parallel().pairMap(( a, b) -> b - a).toArray(), 0.0);
        Assert.assertArrayEquals(LongStreamEx.range(1, 100).asDoubleStream().toArray(), LongStreamEx.range(100).map(( i) -> (i * (i + 1)) / 2).append(LongStream.empty()).asDoubleStream().parallel().pairMap(( a, b) -> b - a).toArray(), 0.0);
        Assert.assertArrayEquals(LongStreamEx.range(1, 100).asDoubleStream().toArray(), LongStreamEx.range(100).map(( i) -> (i * (i + 1)) / 2).prepend(LongStream.empty()).asDoubleStream().parallel().pairMap(( a, b) -> b - a).toArray(), 0.0);
        Assert.assertEquals(1, LongStreamEx.range(1000).mapToDouble(( x) -> x * x).pairMap(( a, b) -> b - a).pairMap(( a, b) -> b - a).distinct().count());
        Assert.assertFalse(LongStreamEx.range(1000).asDoubleStream().greater(2000).parallel().pairMap(( a, b) -> a).findFirst().isPresent());
    }

    @Test
    public void testToFloatArray() {
        float[] expected = new float[10000];
        for (int i = 0; i < (expected.length); i++)
            expected[i] = i;

        Assert.assertArrayEquals(expected, IntStreamEx.range(0, 10000).asDoubleStream().toFloatArray(), 0.0F);
        Assert.assertArrayEquals(expected, IntStreamEx.range(0, 10000).asDoubleStream().parallel().toFloatArray(), 0.0F);
        Assert.assertArrayEquals(expected, IntStreamEx.range(0, 10000).asDoubleStream().greater((-1)).toFloatArray(), 0.0F);
        Assert.assertArrayEquals(expected, IntStreamEx.range(0, 10000).asDoubleStream().parallel().greater((-1)).toFloatArray(), 0.0F);
    }

    @Test
    public void testJoining() {
        Assert.assertEquals("0.4,5.0,3.6,4.8", DoubleStreamEx.of(0.4, 5.0, 3.6, 4.8).joining(","));
        Assert.assertEquals("0.4,5.0,3.6,4.8", DoubleStreamEx.of(0.4, 5.0, 3.6, 4.8).parallel().joining(","));
        Assert.assertEquals("[0.4,5.0,3.6,4.8]", DoubleStreamEx.of(0.4, 5.0, 3.6, 4.8).joining(",", "[", "]"));
        Assert.assertEquals("[0.4,5.0,3.6,4.8]", DoubleStreamEx.of(0.4, 5.0, 3.6, 4.8).parallel().joining(",", "[", "]"));
    }

    @Test
    public void testMapToEntry() {
        Map<Integer, List<Double>> map = DoubleStreamEx.of(0.3, 0.5, 1.3, 0.7, 1.9, 2.1).mapToEntry(( x) -> ((int) (x)), ( x) -> x).grouping();
        Assert.assertEquals(Arrays.asList(0.3, 0.5, 0.7), map.get(0));
        Assert.assertEquals(Arrays.asList(1.3, 1.9), map.get(1));
        Assert.assertEquals(Arrays.asList(2.1), map.get(2));
    }

    @Test
    public void testRecreate() {
        Assert.assertEquals(500, DoubleStreamEx.iterate(0, ( i) -> i + 1).skipOrdered(1).greater(0).boxed().parallel().findAny(( i) -> i == 500).get(), 0.0);
        Assert.assertEquals(500, DoubleStreamEx.iterate(0, ( i) -> i + 1).parallel().skipOrdered(1).greater(0).boxed().findAny(( i) -> i == 500).get(), 0.0);
    }

    @Test
    public void testTakeWhile() {
        Assert.assertArrayEquals(LongStreamEx.range(100).asDoubleStream().toArray(), DoubleStreamEx.iterate(0, ( i) -> i + 1).takeWhile(( i) -> i < 100).toArray(), 0.0);
        Assert.assertEquals(0, DoubleStreamEx.iterate(0, ( i) -> i + 1).takeWhile(( i) -> i < 0).count());
        Assert.assertEquals(1, DoubleStreamEx.of(1, 3, 2).takeWhile(( i) -> i < 3).count());
        Assert.assertEquals(3, DoubleStreamEx.of(1, 2, 3).takeWhile(( i) -> i < 100).count());
    }

    @Test
    public void testTakeWhileInclusive() {
        Assert.assertArrayEquals(LongStreamEx.range(101).asDoubleStream().toArray(), DoubleStreamEx.iterate(0, ( i) -> i + 1).takeWhileInclusive(( i) -> i < 100).toArray(), 0.0);
        Assert.assertEquals(1, DoubleStreamEx.iterate(0, ( i) -> i + 1).takeWhileInclusive(( i) -> i < 0).count());
        Assert.assertEquals(2, DoubleStreamEx.of(1, 3, 2).takeWhileInclusive(( i) -> i < 3).count());
        Assert.assertEquals(3, DoubleStreamEx.of(1, 2, 3).takeWhileInclusive(( i) -> i < 100).count());
    }

    @Test
    public void testDropWhile() {
        Assert.assertArrayEquals(new double[]{ 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 }, LongStreamEx.range(100).asDoubleStream().dropWhile(( i) -> (i % 10) < 5).limit(10).toArray(), 0.0);
        Assert.assertEquals(100, LongStreamEx.range(100).asDoubleStream().sorted().dropWhile(( i) -> (i % 10) < 0).count());
        Assert.assertEquals(0, LongStreamEx.range(100).asDoubleStream().dropWhile(( i) -> (i % 10) < 10).count());
        Assert.assertEquals(OptionalDouble.of(0), LongStreamEx.range(100).asDoubleStream().dropWhile(( i) -> (i % 10) < 0).findFirst());
        Assert.assertEquals(OptionalDouble.empty(), LongStreamEx.range(100).asDoubleStream().dropWhile(( i) -> (i % 10) < 10).findFirst());
        Spliterator.OfDouble spltr = LongStreamEx.range(100).asDoubleStream().dropWhile(( i) -> (i % 10) < 1).spliterator();
        Assert.assertTrue(spltr.tryAdvance((double x) -> Assert.assertEquals(1, x, 0.0)));
        DoubleStream.Builder builder = DoubleStream.builder();
        spltr.forEachRemaining(builder);
        Assert.assertArrayEquals(LongStreamEx.range(2, 100).asDoubleStream().toArray(), builder.build().toArray(), 0.0);
    }

    @Test
    public void testIndexOf() {
        Assert.assertEquals(11, LongStreamEx.range(50, 100).asDoubleStream().indexOf(( x) -> x > 60).getAsLong());
        Assert.assertFalse(LongStreamEx.range(50, 100).asDoubleStream().indexOf(( x) -> x < 0).isPresent());
        Assert.assertEquals(11, LongStreamEx.range(50, 100).asDoubleStream().parallel().indexOf(( x) -> x > 60).getAsLong());
        Assert.assertFalse(LongStreamEx.range(50, 100).asDoubleStream().parallel().indexOf(( x) -> x < 0).isPresent());
    }

    @Test
    public void testFoldLeft() {
        // non-associative
        DoubleBinaryOperator accumulator = ( x, y) -> (x + y) * (x + y);
        Assert.assertEquals(2322576, DoubleStreamEx.constant(3, 4).foldLeft(accumulator).orElse((-1)), 0.0);
        Assert.assertEquals(2322576, DoubleStreamEx.constant(3, 4).parallel().foldLeft(accumulator).orElse((-1)), 0.0);
        Assert.assertFalse(DoubleStreamEx.empty().foldLeft(accumulator).isPresent());
        Assert.assertEquals(144, DoubleStreamEx.of(1, 2, 3).foldLeft(0.0, accumulator), 144);
        Assert.assertEquals(144, DoubleStreamEx.of(1, 2, 3).parallel().foldLeft(0.0, accumulator), 144);
    }

    @Test
    public void testMapFirstLast() {
        Assert.assertArrayEquals(new double[]{ -1, 2, 3, 4, 7 }, DoubleStreamEx.of(1, 2, 3, 4, 5).mapFirst(( x) -> x - 2.0).mapLast(( x) -> x + 2.0).toArray(), 0.0);
    }

    @Test
    public void testPeekFirst() {
        double[] input = new double[]{ 1, 10, 100, 1000 };
        AtomicReference<Double> firstElement = new AtomicReference<>();
        Assert.assertArrayEquals(new double[]{ 10, 100, 1000 }, DoubleStreamEx.of(input).peekFirst(firstElement::set).skip(1).toArray(), 0.0);
        Assert.assertEquals(1, firstElement.get(), 0.0);
        Assert.assertArrayEquals(new double[]{ 10, 100, 1000 }, DoubleStreamEx.of(input).skip(1).peekFirst(firstElement::set).toArray(), 0.0);
        Assert.assertEquals(10, firstElement.get(), 0.0);
        firstElement.set((-1.0));
        Assert.assertArrayEquals(new double[]{  }, DoubleStreamEx.of(input).skip(4).peekFirst(firstElement::set).toArray(), 0.0);
        Assert.assertEquals((-1), firstElement.get(), 0.0);
    }

    @Test
    public void testPeekLast() {
        double[] input = new double[]{ 1, 10, 100, 1000 };
        AtomicReference<Double> lastElement = new AtomicReference<>();
        Assert.assertArrayEquals(new double[]{ 1, 10, 100 }, DoubleStreamEx.of(input).peekLast(lastElement::set).limit(3).toArray(), 0.0);
        Assert.assertNull(lastElement.get());
        Assert.assertArrayEquals(new double[]{ 1, 10, 100 }, DoubleStreamEx.of(input).less(1000).peekLast(lastElement::set).limit(3).toArray(), 0.0);
        Assert.assertEquals(100, lastElement.get(), 0.0);
        Assert.assertArrayEquals(input, DoubleStreamEx.of(input).peekLast(lastElement::set).limit(4).toArray(), 0.0);
        Assert.assertEquals(1000, lastElement.get(), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, 10, 100 }, DoubleStreamEx.of(input).limit(3).peekLast(lastElement::set).toArray(), 0.0);
        Assert.assertEquals(100, lastElement.get(), 0.0);
    }

    @Test
    public void testScanLeft() {
        Assert.assertArrayEquals(new double[]{ 0, 1, 3, 6, 10, 15, 21, 28, 36, 45 }, LongStreamEx.range(10).asDoubleStream().scanLeft(Double::sum), 0.0);
        Assert.assertArrayEquals(new double[]{ 0, 1, 3, 6, 10, 15, 21, 28, 36, 45 }, LongStreamEx.range(10).asDoubleStream().parallel().scanLeft(Double::sum), 0.0);
        Assert.assertArrayEquals(new double[]{ 0, 1, 3, 6, 10, 15, 21, 28, 36, 45 }, LongStreamEx.range(10).asDoubleStream().filter(( x) -> true).scanLeft(Double::sum), 0.0);
        Assert.assertArrayEquals(new double[]{ 0, 1, 3, 6, 10, 15, 21, 28, 36, 45 }, LongStreamEx.range(10).asDoubleStream().filter(( x) -> true).parallel().scanLeft(Double::sum), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, 1, 2, 6, 24, 120 }, LongStreamEx.rangeClosed(1, 5).asDoubleStream().scanLeft(1, ( a, b) -> a * b), 0.0);
        Assert.assertArrayEquals(new double[]{ 1, 1, 2, 6, 24, 120 }, LongStreamEx.rangeClosed(1, 5).asDoubleStream().parallel().scanLeft(1, ( a, b) -> a * b), 0.0);
    }

    @Test
    public void testProduce() {
        Scanner sc = new Scanner("1.0 2.5 3 -4.6 test");
        sc.useLocale(Locale.ENGLISH);
        Assert.assertArrayEquals(new double[]{ 1, 2.5, 3, -4.6 }, DoubleStreamExTest.scannerDoubles(sc).stream().toArray(), 0.0);
        Assert.assertEquals("test", sc.next());
    }

    @Test
    public void testPrefix() {
        Assert.assertArrayEquals(new double[]{ 1, 3, 6, 10, 20 }, DoubleStreamEx.of(1, 2, 3, 4, 10).prefix(Double::sum).toArray(), 0.0);
        Assert.assertEquals(OptionalDouble.of(10), DoubleStreamEx.of(1, 2, 3, 4, 10).prefix(Double::sum).findFirst(( x) -> x > 7));
        Assert.assertEquals(OptionalDouble.empty(), DoubleStreamEx.of(1, 2, 3, 4, 10).prefix(Double::sum).findFirst(( x) -> x > 20));
    }

    @Test
    public void testIntersperse() {
        Assert.assertArrayEquals(new double[]{ 1, 0, 10, 0, 100, 0, 1000 }, DoubleStreamEx.of(1, 10, 100, 1000).intersperse(0).toArray(), 0.0);
        Assert.assertEquals(0L, IntStreamEx.empty().intersperse(1).count());
    }
}

