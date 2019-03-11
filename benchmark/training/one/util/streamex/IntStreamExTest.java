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


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
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
public class IntStreamExTest {
    private static final byte[] EVEN_BYTES = new byte[]{ 2, 4, 6, 8, 10 };

    @Test
    public void testCreate() {
        Assert.assertArrayEquals(new int[]{  }, IntStreamEx.empty().toArray());
        // double test is intended
        Assert.assertArrayEquals(new int[]{  }, IntStreamEx.empty().toArray());
        Assert.assertArrayEquals(new int[]{ 1 }, IntStreamEx.of(1).toArray());
        Assert.assertArrayEquals(new int[]{ 1 }, IntStreamEx.of(OptionalInt.of(1)).toArray());
        Assert.assertArrayEquals(new int[]{  }, IntStreamEx.of(OptionalInt.empty()).toArray());
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, IntStreamEx.of(1, 2, 3).toArray());
        Assert.assertArrayEquals(new int[]{ 4, 6 }, IntStreamEx.of(new int[]{ 2, 4, 6, 8, 10 }, 1, 3).toArray());
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, IntStreamEx.of(new byte[]{ 1, 2, 3 }).toArray());
        Assert.assertArrayEquals(new int[]{ 4, 6 }, IntStreamEx.of(IntStreamExTest.EVEN_BYTES, 1, 3).toArray());
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, IntStreamEx.of(new short[]{ 1, 2, 3 }).toArray());
        Assert.assertArrayEquals(new int[]{ 4, 6 }, IntStreamEx.of(new short[]{ 2, 4, 6, 8, 10 }, 1, 3).toArray());
        Assert.assertArrayEquals(new int[]{ 'a', 'b', 'c' }, IntStreamEx.of('a', 'b', 'c').toArray());
        Assert.assertArrayEquals(new int[]{ '1', 'b' }, IntStreamEx.of(new char[]{ 'a', '1', 'b', '2', 'c', '3' }, 1, 3).toArray());
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, IntStreamEx.of(IntStream.of(1, 2, 3)).toArray());
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, IntStreamEx.of(Arrays.asList(1, 2, 3)).toArray());
        Assert.assertArrayEquals(new int[]{ 0, 1, 2 }, IntStreamEx.range(3).toArray());
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, IntStreamEx.range(1, 4).toArray());
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, IntStreamEx.rangeClosed(1, 3).toArray());
        Assert.assertArrayEquals(new int[]{ 1, 1, 1, 1 }, IntStreamEx.generate(() -> 1).limit(4).toArray());
        Assert.assertArrayEquals(new int[]{ 1, 1, 1, 1 }, IntStreamEx.constant(1, 4).toArray());
        Assert.assertArrayEquals(new int[]{ 'a', 'b', 'c' }, IntStreamEx.ofChars("abc").toArray());
        Assert.assertEquals(10, IntStreamEx.of(new Random(), 10).count());
        Assert.assertTrue(IntStreamEx.of(new Random(), 100, 1, 10).allMatch(( x) -> (x >= 1) && (x < 10)));
        Assert.assertArrayEquals(IntStreamEx.of(new Random(1), 100, 1, 10).toArray(), IntStreamEx.of(new Random(1), 1, 10).limit(100).toArray());
        IntStream stream = IntStreamEx.of(1, 2, 3);
        Assert.assertSame(stream, IntStreamEx.of(stream));
        Assert.assertArrayEquals(new int[]{ 4, 2, 0, -2, -4 }, IntStreamEx.zip(new int[]{ 5, 4, 3, 2, 1 }, new int[]{ 1, 2, 3, 4, 5 }, ( a, b) -> a - b).toArray());
        Assert.assertArrayEquals(new int[]{ 1, 5, 3 }, IntStreamEx.of(Spliterators.spliterator(new int[]{ 1, 5, 3 }, 0)).toArray());
        Assert.assertArrayEquals(new int[]{ 1, 5, 3 }, IntStreamEx.of(Spliterators.iterator(Spliterators.spliterator(new int[]{ 1, 5, 3 }, 0))).toArray());
        Assert.assertArrayEquals(new int[0], IntStreamEx.of(Spliterators.iterator(Spliterators.emptyIntSpliterator())).parallel().toArray());
        BitSet bs = new BitSet();
        bs.set(1);
        bs.set(3);
        bs.set(5);
        Assert.assertArrayEquals(new int[]{ 1, 3, 5 }, IntStreamEx.of(bs).toArray());
        Assert.assertArrayEquals(new int[]{ 2, 4, 6 }, IntStreamEx.of(new Integer[]{ 2, 4, 6 }).toArray());
    }

    @Test
    public void testOfIntBuffer() {
        int[] data = IntStreamEx.range(100).toArray();
        Assert.assertArrayEquals(data, IntStreamEx.of(IntBuffer.wrap(data)).toArray());
        Assert.assertArrayEquals(IntStreamEx.range(50, 70).toArray(), IntStreamEx.of(IntBuffer.wrap(data, 50, 20)).toArray());
        Assert.assertArrayEquals(data, IntStreamEx.of(IntBuffer.wrap(data)).parallel().toArray());
        Assert.assertArrayEquals(IntStreamEx.range(50, 70).toArray(), IntStreamEx.of(IntBuffer.wrap(data, 50, 20)).parallel().toArray());
    }

    @Test
    public void testIterate() {
        Assert.assertArrayEquals(new int[]{ 1, 2, 4, 8, 16 }, IntStreamEx.iterate(1, ( x) -> x * 2).limit(5).toArray());
        Assert.assertArrayEquals(new int[]{ 1, 2, 4, 8, 16, 32, 64 }, IntStreamEx.iterate(1, ( x) -> x < 100, ( x) -> x * 2).toArray());
        Assert.assertEquals(0, IntStreamEx.iterate(0, ( x) -> x < 0, ( x) -> 1 / x).count());
        Assert.assertFalse(IntStreamEx.iterate(1, ( x) -> x < 100, ( x) -> x * 2).has(10));
        TestHelpers.checkSpliterator("iterate", () -> IntStreamEx.iterate(1, ( x) -> x < 100, ( x) -> x * 2).spliterator());
    }

    @Test
    public void testInts() {
        Assert.assertEquals(Integer.MAX_VALUE, IntStreamEx.ints().spliterator().getExactSizeIfKnown());
        Assert.assertArrayEquals(new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, IntStreamEx.ints().limit(10).toArray());
    }

    @Test
    public void testRangeStep() {
        Assert.assertArrayEquals(new int[]{ 0 }, IntStreamEx.range(0, 1000, 100000).toArray());
        Assert.assertArrayEquals(new int[]{ 0 }, IntStreamEx.range(0, 1000, 1000).toArray());
        Assert.assertArrayEquals(new int[]{ 0, (Integer.MAX_VALUE) - 1 }, IntStreamEx.range(0, Integer.MAX_VALUE, ((Integer.MAX_VALUE) - 1)).toArray());
        Assert.assertArrayEquals(new int[]{ Integer.MIN_VALUE, -1, (Integer.MAX_VALUE) - 1 }, IntStreamEx.range(Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE).toArray());
        Assert.assertArrayEquals(new int[]{ Integer.MIN_VALUE, -1 }, IntStreamEx.range(Integer.MIN_VALUE, ((Integer.MAX_VALUE) - 1), Integer.MAX_VALUE).toArray());
        Assert.assertArrayEquals(new int[]{ Integer.MAX_VALUE, -1 }, IntStreamEx.range(Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE).toArray());
        Assert.assertArrayEquals(new int[]{ Integer.MAX_VALUE }, IntStreamEx.range(Integer.MAX_VALUE, 0, Integer.MIN_VALUE).toArray());
        Assert.assertArrayEquals(new int[]{ 1, (Integer.MIN_VALUE) + 1 }, IntStreamEx.range(1, Integer.MIN_VALUE, Integer.MIN_VALUE).toArray());
        Assert.assertArrayEquals(new int[]{ 0 }, IntStreamEx.range(0, Integer.MIN_VALUE, Integer.MIN_VALUE).toArray());
        Assert.assertArrayEquals(new int[]{ 0, 2, 4, 6, 8 }, IntStreamEx.range(0, 9, 2).toArray());
        Assert.assertArrayEquals(new int[]{ 0, 2, 4, 6 }, IntStreamEx.range(0, 8, 2).toArray());
        Assert.assertArrayEquals(new int[]{ 0, -2, -4, -6, -8 }, IntStreamEx.range(0, (-9), (-2)).toArray());
        Assert.assertArrayEquals(new int[]{ 0, -2, -4, -6 }, IntStreamEx.range(0, (-8), (-2)).toArray());
        Assert.assertArrayEquals(new int[]{ 5, 4, 3, 2, 1, 0 }, IntStreamEx.range(5, (-1), (-1)).toArray());
        Assert.assertEquals(((Integer.MAX_VALUE) + 1L), IntStreamEx.range(Integer.MIN_VALUE, Integer.MAX_VALUE, 2).spliterator().getExactSizeIfKnown());
        Assert.assertEquals(Integer.MAX_VALUE, IntStreamEx.range(Integer.MIN_VALUE, ((Integer.MAX_VALUE) - 1), 2).spliterator().getExactSizeIfKnown());
        Assert.assertEquals(((Integer.MAX_VALUE) + 1L), IntStreamEx.range(Integer.MAX_VALUE, Integer.MIN_VALUE, (-2)).spliterator().getExactSizeIfKnown());
        Assert.assertEquals(Integer.MAX_VALUE, IntStreamEx.range(Integer.MAX_VALUE, ((Integer.MIN_VALUE) + 1), (-2)).spliterator().getExactSizeIfKnown());
        Assert.assertEquals((((Integer.MAX_VALUE) * 2L) + 1L), IntStreamEx.range(Integer.MIN_VALUE, Integer.MAX_VALUE, 1).spliterator().getExactSizeIfKnown());
        Assert.assertEquals((((Integer.MAX_VALUE) * 2L) + 1L), IntStreamEx.range(Integer.MAX_VALUE, Integer.MIN_VALUE, (-1)).spliterator().getExactSizeIfKnown());
        Assert.assertEquals(0, IntStreamEx.range(0, (-1000), 1).count());
        Assert.assertEquals(0, IntStreamEx.range(0, 1000, (-1)).count());
        Assert.assertEquals(0, IntStreamEx.range(0, 0, (-1)).count());
        Assert.assertEquals(0, IntStreamEx.range(0, 0, 1).count());
        Assert.assertEquals(0, IntStreamEx.range(0, (-1000), 2).count());
        Assert.assertEquals(0, IntStreamEx.range(0, 1000, (-2)).count());
        Assert.assertEquals(0, IntStreamEx.range(0, 0, (-2)).count());
        Assert.assertEquals(0, IntStreamEx.range(0, 0, 2).count());
        Assert.assertEquals(0, IntStreamEx.range(0, Integer.MIN_VALUE, 2).spliterator().getExactSizeIfKnown());
        Assert.assertEquals(0, IntStreamEx.range(0, Integer.MAX_VALUE, (-2)).spliterator().getExactSizeIfKnown());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangeIllegalStep() {
        IntStreamEx.range(0, 1000, 0);
    }

    @Test
    public void testRangeClosedStep() {
        Assert.assertArrayEquals(new int[]{ 0 }, IntStreamEx.rangeClosed(0, 1000, 100000).toArray());
        Assert.assertArrayEquals(new int[]{ 0, 1000 }, IntStreamEx.rangeClosed(0, 1000, 1000).toArray());
        Assert.assertArrayEquals(new int[]{ 0, (Integer.MAX_VALUE) - 1 }, IntStreamEx.rangeClosed(0, Integer.MAX_VALUE, ((Integer.MAX_VALUE) - 1)).toArray());
        Assert.assertArrayEquals(new int[]{ 0, Integer.MAX_VALUE }, IntStreamEx.rangeClosed(0, Integer.MAX_VALUE, Integer.MAX_VALUE).toArray());
        Assert.assertArrayEquals(new int[]{ Integer.MIN_VALUE, -1, (Integer.MAX_VALUE) - 1 }, IntStreamEx.rangeClosed(Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE).toArray());
        Assert.assertArrayEquals(new int[]{ Integer.MIN_VALUE, -1, (Integer.MAX_VALUE) - 1 }, IntStreamEx.rangeClosed(Integer.MIN_VALUE, ((Integer.MAX_VALUE) - 1), Integer.MAX_VALUE).toArray());
        Assert.assertArrayEquals(new int[]{ Integer.MAX_VALUE, -1 }, IntStreamEx.rangeClosed(Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE).toArray());
        Assert.assertArrayEquals(new int[]{ Integer.MAX_VALUE }, IntStreamEx.rangeClosed(Integer.MAX_VALUE, 0, Integer.MIN_VALUE).toArray());
        Assert.assertArrayEquals(new int[]{ 0, Integer.MIN_VALUE }, IntStreamEx.rangeClosed(0, Integer.MIN_VALUE, Integer.MIN_VALUE).toArray());
        Assert.assertArrayEquals(new int[]{ 0, 2, 4, 6, 8 }, IntStreamEx.rangeClosed(0, 9, 2).toArray());
        Assert.assertArrayEquals(new int[]{ 0, 2, 4, 6, 8 }, IntStreamEx.rangeClosed(0, 8, 2).toArray());
        Assert.assertArrayEquals(new int[]{ 0, 2, 4, 6 }, IntStreamEx.rangeClosed(0, 7, 2).toArray());
        Assert.assertArrayEquals(new int[]{ 0, -2, -4, -6, -8 }, IntStreamEx.rangeClosed(0, (-9), (-2)).toArray());
        Assert.assertArrayEquals(new int[]{ 0, -2, -4, -6, -8 }, IntStreamEx.rangeClosed(0, (-8), (-2)).toArray());
        Assert.assertArrayEquals(new int[]{ 0, -2, -4, -6 }, IntStreamEx.rangeClosed(0, (-7), (-2)).toArray());
        Assert.assertArrayEquals(new int[]{ 5, 4, 3, 2, 1, 0 }, IntStreamEx.rangeClosed(5, 0, (-1)).toArray());
        Assert.assertEquals(((Integer.MAX_VALUE) + 1L), IntStreamEx.rangeClosed(Integer.MIN_VALUE, Integer.MAX_VALUE, 2).spliterator().getExactSizeIfKnown());
        Assert.assertEquals(((Integer.MAX_VALUE) + 1L), IntStreamEx.rangeClosed(Integer.MIN_VALUE, ((Integer.MAX_VALUE) - 1), 2).spliterator().getExactSizeIfKnown());
        Assert.assertEquals(Integer.MAX_VALUE, IntStreamEx.rangeClosed(Integer.MIN_VALUE, ((Integer.MAX_VALUE) - 2), 2).spliterator().getExactSizeIfKnown());
        Assert.assertEquals(((Integer.MAX_VALUE) + 1L), IntStreamEx.rangeClosed(Integer.MAX_VALUE, Integer.MIN_VALUE, (-2)).spliterator().getExactSizeIfKnown());
        Assert.assertEquals(((Integer.MAX_VALUE) + 1L), IntStreamEx.rangeClosed(Integer.MAX_VALUE, ((Integer.MIN_VALUE) + 1), (-2)).spliterator().getExactSizeIfKnown());
        Assert.assertEquals((((Integer.MAX_VALUE) * 2L) + 2L), IntStreamEx.rangeClosed(Integer.MIN_VALUE, Integer.MAX_VALUE, 1).spliterator().getExactSizeIfKnown());
        Assert.assertEquals((((Integer.MAX_VALUE) * 2L) + 2L), IntStreamEx.rangeClosed(Integer.MAX_VALUE, Integer.MIN_VALUE, (-1)).spliterator().getExactSizeIfKnown());
        Assert.assertEquals(0, IntStreamEx.rangeClosed(0, (-1000), 1).count());
        Assert.assertEquals(0, IntStreamEx.rangeClosed(0, 1000, (-1)).count());
        Assert.assertEquals(0, IntStreamEx.rangeClosed(0, 1, (-1)).count());
        Assert.assertEquals(0, IntStreamEx.rangeClosed(0, (-1), 1).count());
        Assert.assertEquals(0, IntStreamEx.rangeClosed(0, (-1000), 2).count());
        Assert.assertEquals(0, IntStreamEx.rangeClosed(0, 1000, (-2)).count());
        Assert.assertEquals(0, IntStreamEx.rangeClosed(0, 1, (-2)).count());
        Assert.assertEquals(0, IntStreamEx.rangeClosed(0, (-1), 2).count());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testArrayOffsetUnderflow() {
        IntStreamEx.of(IntStreamExTest.EVEN_BYTES, (-1), 3).findAny();
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testArrayOffsetWrong() {
        IntStreamEx.of(IntStreamExTest.EVEN_BYTES, 3, 1).findAny();
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testArrayLengthOverflow() {
        IntStreamEx.of(IntStreamExTest.EVEN_BYTES, 3, 6).findAny();
    }

    @Test
    public void testArrayLengthOk() {
        Assert.assertEquals(10, IntStreamEx.of(IntStreamExTest.EVEN_BYTES, 3, 5).skip(1).findFirst().getAsInt());
    }

    @Test
    public void testOfIndices() {
        Assert.assertArrayEquals(new int[]{  }, IntStreamEx.ofIndices(new int[0]).toArray());
        Assert.assertArrayEquals(new int[]{ 0, 1, 2 }, IntStreamEx.ofIndices(new int[]{ 5, -100, 1 }).toArray());
        Assert.assertArrayEquals(new int[]{ 0, 2 }, IntStreamEx.ofIndices(new int[]{ 5, -100, 1 }, ( i) -> i > 0).toArray());
        Assert.assertArrayEquals(new int[]{ 0, 1, 2 }, IntStreamEx.ofIndices(new long[]{ 5, -100, 1 }).toArray());
        Assert.assertArrayEquals(new int[]{ 0, 2 }, IntStreamEx.ofIndices(new long[]{ 5, -100, 1 }, ( i) -> i > 0).toArray());
        Assert.assertArrayEquals(new int[]{ 0, 1, 2 }, IntStreamEx.ofIndices(new double[]{ 5, -100, 1 }).toArray());
        Assert.assertArrayEquals(new int[]{ 0, 2 }, IntStreamEx.ofIndices(new double[]{ 5, -100, 1 }, ( i) -> i > 0).toArray());
        Assert.assertArrayEquals(new int[]{ 0, 1, 2 }, IntStreamEx.ofIndices(new String[]{ "a", "b", "c" }).toArray());
        Assert.assertArrayEquals(new int[]{ 1 }, IntStreamEx.ofIndices(new String[]{ "a", "", "c" }, String::isEmpty).toArray());
        Assert.assertArrayEquals(new int[]{ 0, 1, 2 }, IntStreamEx.ofIndices(Arrays.asList("a", "b", "c")).toArray());
        Assert.assertArrayEquals(new int[]{ 1 }, IntStreamEx.ofIndices(Arrays.asList("a", "", "c"), String::isEmpty).toArray());
    }

    @Test
    public void testBasics() {
        Assert.assertFalse(IntStreamEx.of(1).isParallel());
        Assert.assertTrue(IntStreamEx.of(1).parallel().isParallel());
        Assert.assertFalse(IntStreamEx.of(1).parallel().sequential().isParallel());
        AtomicInteger i = new AtomicInteger();
        try (IntStreamEx s = IntStreamEx.of(1).onClose(i::incrementAndGet)) {
            Assert.assertEquals(1, s.count());
        }
        Assert.assertEquals(1, i.get());
        Assert.assertEquals(6, IntStreamEx.range(0, 4).sum());
        Assert.assertEquals(3, IntStreamEx.range(0, 4).max().getAsInt());
        Assert.assertEquals(0, IntStreamEx.range(0, 4).min().getAsInt());
        Assert.assertEquals(1.5, IntStreamEx.range(0, 4).average().getAsDouble(), 1.0E-6);
        Assert.assertEquals(4, IntStreamEx.range(0, 4).summaryStatistics().getCount());
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, IntStreamEx.range(0, 5).skip(1).limit(3).toArray());
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, IntStreamEx.of(3, 1, 2).sorted().toArray());
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, IntStreamEx.of(1, 2, 1, 3, 2).distinct().toArray());
        Assert.assertArrayEquals(new int[]{ 2, 4, 6 }, IntStreamEx.range(1, 4).map(( x) -> x * 2).toArray());
        Assert.assertArrayEquals(new long[]{ 2, 4, 6 }, IntStreamEx.range(1, 4).mapToLong(( x) -> x * 2).toArray());
        Assert.assertArrayEquals(new double[]{ 2, 4, 6 }, IntStreamEx.range(1, 4).mapToDouble(( x) -> x * 2).toArray(), 0.0);
        Assert.assertArrayEquals(new int[]{ 1, 3 }, IntStreamEx.range(0, 5).filter(( x) -> (x % 2) == 1).toArray());
        Assert.assertEquals(6, IntStreamEx.of(1, 2, 3).reduce(Integer::sum).getAsInt());
        Assert.assertEquals(Integer.MAX_VALUE, IntStreamEx.rangeClosed(1, Integer.MAX_VALUE).spliterator().getExactSizeIfKnown());
        Assert.assertTrue(IntStreamEx.of(1, 2, 3).spliterator().hasCharacteristics(Spliterator.ORDERED));
        Assert.assertFalse(IntStreamEx.of(1, 2, 3).unordered().spliterator().hasCharacteristics(Spliterator.ORDERED));
        PrimitiveIterator.OfInt iterator = IntStreamEx.of(1, 2, 3).iterator();
        Assert.assertEquals(1, iterator.nextInt());
        Assert.assertEquals(2, iterator.nextInt());
        Assert.assertEquals(3, iterator.nextInt());
        Assert.assertFalse(iterator.hasNext());
        List<Integer> list = new ArrayList<>();
        IntStreamEx.range(10).parallel().forEachOrdered(list::add);
        Assert.assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), list);
        Assert.assertTrue(IntStreamEx.empty().noneMatch(( x) -> true));
        Assert.assertFalse(IntStreamEx.of(1).noneMatch(( x) -> true));
        Assert.assertTrue(IntStreamEx.of(1).noneMatch(( x) -> false));
    }

    @Test
    public void testFlatMap() {
        long[][] vals = new long[][]{ new long[]{ 1, 2, 3 }, new long[]{ 2, 3, 4 }, new long[]{ 5, 4, Long.MAX_VALUE, Long.MIN_VALUE } };
        Assert.assertArrayEquals(new long[]{ 1, 2, 3, 2, 3, 4, 5, 4, Long.MAX_VALUE, Long.MIN_VALUE }, IntStreamEx.ofIndices(vals).flatMapToLong(( idx) -> Arrays.stream(vals[idx])).toArray());
        String expected = IntStream.range(0, 200).boxed().flatMap(( i) -> IntStream.range(0, i).mapToObj(( j) -> (i + ":") + j)).collect(Collectors.joining("/"));
        String res = IntStreamEx.range(200).flatMapToObj(( i) -> IntStreamEx.range(i).mapToObj(( j) -> (i + ":") + j)).joining("/");
        String parallel = IntStreamEx.range(200).parallel().flatMapToObj(( i) -> IntStreamEx.range(i).mapToObj(( j) -> (i + ":") + j)).joining("/");
        Assert.assertEquals(expected, res);
        Assert.assertEquals(expected, parallel);
        double[] fractions = IntStreamEx.range(1, 5).flatMapToDouble(( i) -> IntStreamEx.range(1, i).mapToDouble(( j) -> ((double) (j)) / i)).toArray();
        Assert.assertArrayEquals(new double[]{ 1 / 2.0, 1 / 3.0, 2 / 3.0, 1 / 4.0, 2 / 4.0, 3 / 4.0 }, fractions, 1.0E-6);
        Assert.assertArrayEquals(new int[]{ 0, 0, 1, 0, 1, 2 }, IntStreamEx.of(1, 2, 3).flatMap(IntStreamEx::range).toArray());
    }

    @Test
    public void testElements() {
        Assert.assertEquals(Arrays.asList("f", "d", "b"), IntStreamEx.of(5, 3, 1).elements("abcdef".split("")).toList());
        Assert.assertEquals(Arrays.asList("f", "d", "b"), IntStreamEx.of(5, 3, 1).elements(Arrays.asList("a", "b", "c", "d", "e", "f")).toList());
        Assert.assertArrayEquals(new int[]{ 10, 6, 2 }, IntStreamEx.of(5, 3, 1).elements(new int[]{ 0, 2, 4, 6, 8, 10 }).toArray());
        Assert.assertArrayEquals(new long[]{ 10, 6, 2 }, IntStreamEx.of(5, 3, 1).elements(new long[]{ 0, 2, 4, 6, 8, 10 }).toArray());
        Assert.assertArrayEquals(new double[]{ 10, 6, 2 }, IntStreamEx.of(5, 3, 1).elements(new double[]{ 0, 2, 4, 6, 8, 10 }).toArray(), 0.0);
    }

    @Test
    public void testPrepend() {
        Assert.assertArrayEquals(new int[]{ -1, 0, 1, 2, 3 }, IntStreamEx.of(1, 2, 3).prepend((-1), 0).toArray());
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, IntStreamEx.of(1, 2, 3).prepend().toArray());
        Assert.assertArrayEquals(new int[]{ 10, 11, 0, 1, 2, 3 }, IntStreamEx.range(0, 4).prepend(IntStreamEx.range(10, 12)).toArray());
    }

    @Test
    public void testAppend() {
        Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4, 5 }, IntStreamEx.of(1, 2, 3).append(4, 5).toArray());
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, IntStreamEx.of(1, 2, 3).append().toArray());
        Assert.assertArrayEquals(new int[]{ 0, 1, 2, 3, 10, 11 }, IntStreamEx.range(0, 4).append(IntStreamEx.range(10, 12)).toArray());
    }

    @Test
    public void testHas() {
        Assert.assertTrue(IntStreamEx.range(1, 4).has(3));
        Assert.assertFalse(IntStreamEx.range(1, 4).has(4));
    }

    @Test
    public void testWithout() {
        Assert.assertArrayEquals(new int[]{ 1, 2 }, IntStreamEx.range(1, 4).without(3).toArray());
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, IntStreamEx.range(1, 4).without(5).toArray());
        IntStreamEx ise = IntStreamEx.range(5);
        Assert.assertSame(ise, ise.without());
        Assert.assertArrayEquals(new int[]{ 0, 1, 3, 4 }, IntStreamEx.range(5).without(new int[]{ 2 }).toArray());
        Assert.assertArrayEquals(new int[]{ 0 }, IntStreamEx.range(5).without(1, 2, 3, 4, 5, 6).toArray());
    }

    @Test
    public void testRanges() {
        Assert.assertArrayEquals(new int[]{ 5, 4, Integer.MAX_VALUE }, IntStreamEx.of(1, 5, 3, 4, (-1), Integer.MAX_VALUE).greater(3).toArray());
        Assert.assertArrayEquals(new int[]{ 5, 3, 4, Integer.MAX_VALUE }, IntStreamEx.of(1, 5, 3, 4, (-1), Integer.MAX_VALUE).atLeast(3).toArray());
        Assert.assertArrayEquals(new int[]{ 1, -1 }, IntStreamEx.of(1, 5, 3, 4, (-1), Integer.MAX_VALUE).less(3).toArray());
        Assert.assertArrayEquals(new int[]{ 1, 3, -1 }, IntStreamEx.of(1, 5, 3, 4, (-1), Integer.MAX_VALUE).atMost(3).toArray());
        Assert.assertArrayEquals(new int[]{ 1, 3, 4, -1 }, IntStreamEx.of(1, 5, 3, 4, (-1), Integer.MAX_VALUE).atMost(4).toArray());
    }

    @Test
    public void testToBitSet() {
        Assert.assertEquals("{0, 1, 2, 3, 4}", IntStreamEx.range(5).toBitSet().toString());
        Assert.assertEquals("{0, 2, 3, 4, 10}", IntStreamEx.of(0, 2, 0, 3, 0, 4, 0, 10).parallel().toBitSet().toString());
    }

    @Test
    public void testAs() {
        Assert.assertEquals(4, IntStreamEx.range(0, 5).asLongStream().findAny(( x) -> x > 3).getAsLong());
        Assert.assertEquals(4.0, IntStreamEx.range(0, 5).asDoubleStream().findAny(( x) -> x > 3).getAsDouble(), 0.0);
    }

    @Test
    public void testFind() {
        Assert.assertEquals(6, IntStreamEx.range(1, 10).findFirst(( i) -> i > 5).getAsInt());
        Assert.assertFalse(IntStreamEx.range(1, 10).findAny(( i) -> i > 10).isPresent());
    }

    @Test
    public void testRemove() {
        Assert.assertArrayEquals(new int[]{ 1, 2 }, IntStreamEx.of(1, 2, 3).remove(( x) -> x > 2).toArray());
    }

    @Test
    public void testSort() {
        Assert.assertArrayEquals(new int[]{ 0, 3, 6, 1, 4, 7, 2, 5, 8 }, IntStreamEx.range(0, 9).sortedByInt(( i) -> ((i % 3) * 3) + (i / 3)).toArray());
        Assert.assertArrayEquals(new int[]{ 0, 3, 6, 1, 4, 7, 2, 5, 8 }, IntStreamEx.range(0, 9).sortedByLong(( i) -> ((((long) (i)) % 3) * Integer.MAX_VALUE) + (i / 3)).toArray());
        Assert.assertArrayEquals(new int[]{ 8, 7, 6, 5, 4, 3, 2, 1 }, IntStreamEx.range(1, 9).sortedByDouble(( i) -> 1.0 / i).toArray());
        Assert.assertArrayEquals(new int[]{ 10, 11, 5, 6, 7, 8, 9 }, IntStreamEx.range(5, 12).sortedBy(String::valueOf).toArray());
        Assert.assertArrayEquals(new int[]{ Integer.MAX_VALUE, 1000, 1, 0, -10, Integer.MIN_VALUE }, IntStreamEx.of(0, 1, 1000, (-10), Integer.MIN_VALUE, Integer.MAX_VALUE).reverseSorted().toArray());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("LOWERCASE", IntStreamEx.ofChars("lowercase").map(( c) -> Character.toUpperCase(((char) (c)))).charsToString());
        Assert.assertEquals("LOWERCASE", IntStreamEx.ofCodePoints("lowercase").map(Character::toUpperCase).codePointsToString());
    }

    @Test
    public void testMinMax() {
        IntStreamExTest.checkEmpty(( s) -> s.maxBy(Integer::valueOf), ( s) -> s.maxByInt(( x) -> x), ( s) -> s.maxByLong(( x) -> x), ( s) -> s.maxByDouble(( x) -> x), ( s) -> s.minBy(Integer::valueOf), ( s) -> s.minByInt(( x) -> x), ( s) -> s.minByLong(( x) -> x), ( s) -> s.minByDouble(( x) -> x));
        Assert.assertEquals(9, IntStreamEx.range(5, 12).max(Comparator.comparing(String::valueOf)).getAsInt());
        Assert.assertEquals(10, IntStreamEx.range(5, 12).min(Comparator.comparing(String::valueOf)).getAsInt());
        Assert.assertEquals(9, IntStreamEx.range(5, 12).maxBy(String::valueOf).getAsInt());
        Assert.assertEquals(10, IntStreamEx.range(5, 12).minBy(String::valueOf).getAsInt());
        Assert.assertEquals(5, IntStreamEx.range(5, 12).maxByDouble(( x) -> 1.0 / x).getAsInt());
        Assert.assertEquals(11, IntStreamEx.range(5, 12).minByDouble(( x) -> 1.0 / x).getAsInt());
        Assert.assertEquals(29, IntStreamEx.of(15, 8, 31, 47, 19, 29).maxByInt(( x) -> ((x % 10) * 10) + (x / 10)).getAsInt());
        Assert.assertEquals(31, IntStreamEx.of(15, 8, 31, 47, 19, 29).minByInt(( x) -> ((x % 10) * 10) + (x / 10)).getAsInt());
        Assert.assertEquals(29, IntStreamEx.of(15, 8, 31, 47, 19, 29).maxByLong(( x) -> (Long.MIN_VALUE + ((x % 10) * 10)) + (x / 10)).getAsInt());
        Assert.assertEquals(31, IntStreamEx.of(15, 8, 31, 47, 19, 29).minByLong(( x) -> (Long.MIN_VALUE + ((x % 10) * 10)) + (x / 10)).getAsInt());
        Supplier<IntStreamEx> s = () -> IntStreamEx.of(1, 50, 120, 35, 130, 12, 0);
        IntUnaryOperator intKey = ( x) -> String.valueOf(x).length();
        IntToLongFunction longKey = ( x) -> String.valueOf(x).length();
        IntToDoubleFunction doubleKey = ( x) -> String.valueOf(x).length();
        IntFunction<Integer> objKey = ( x) -> String.valueOf(x).length();
        List<Function<IntStreamEx, OptionalInt>> minFns = Arrays.asList(( is) -> is.minByInt(intKey), ( is) -> is.minByLong(longKey), ( is) -> is.minByDouble(doubleKey), ( is) -> is.minBy(objKey));
        List<Function<IntStreamEx, OptionalInt>> maxFns = Arrays.asList(( is) -> is.maxByInt(intKey), ( is) -> is.maxByLong(longKey), ( is) -> is.maxByDouble(doubleKey), ( is) -> is.maxBy(objKey));
        minFns.forEach(( fn) -> assertEquals(1, fn.apply(s.get()).getAsInt()));
        minFns.forEach(( fn) -> assertEquals(1, fn.apply(s.get().parallel()).getAsInt()));
        maxFns.forEach(( fn) -> assertEquals(120, fn.apply(s.get()).getAsInt()));
        maxFns.forEach(( fn) -> assertEquals(120, fn.apply(s.get().parallel()).getAsInt()));
    }

    @Test
    public void testPairMap() {
        Assert.assertEquals(0, IntStreamEx.range(0).pairMap(Integer::sum).count());
        Assert.assertEquals(0, IntStreamEx.range(1).pairMap(Integer::sum).count());
        Assert.assertEquals(Collections.singletonMap(1, 9999L), IntStreamEx.range(10000).pairMap(( a, b) -> b - a).boxed().groupingBy(Function.identity(), Collectors.counting()));
        Assert.assertEquals(Collections.singletonMap(1, 9999L), IntStreamEx.range(10000).parallel().pairMap(( a, b) -> b - a).boxed().groupingBy(Function.identity(), Collectors.counting()));
        Assert.assertEquals("Test Capitalization Stream", IntStreamEx.ofChars("test caPiTaliZation streaM").parallel().prepend(0).pairMap(( c1, c2) -> (!(Character.isLetter(c1))) && (Character.isLetter(c2)) ? Character.toTitleCase(c2) : Character.toLowerCase(c2)).charsToString());
        Assert.assertArrayEquals(IntStreamEx.range(9999).toArray(), IntStreamExTest.dropLast(IntStreamEx.range(10000)).toArray());
        TestHelpers.withRandom(( r) -> {
            int[] data = r.ints(1000, 1, 1000).toArray();
            int[] expected = new int[(data.length) - 1];
            int lastSquare = (data[0]) * (data[0]);
            for (int i = 0; i < (expected.length); i++) {
                int newSquare = (data[(i + 1)]) * (data[(i + 1)]);
                expected[i] = newSquare - lastSquare;
                lastSquare = newSquare;
            }
            int[] result = IntStreamEx.of(data).map(( x) -> x * x).pairMap(( a, b) -> b - a).toArray();
            Assert.assertArrayEquals(expected, result);
        });
        Assert.assertEquals(1, IntStreamEx.range(1000).map(( x) -> x * x).pairMap(( a, b) -> b - a).pairMap(( a, b) -> b - a).distinct().count());
        Assert.assertArrayEquals(IntStreamEx.constant(1, 100).toArray(), IntStreamEx.iterate(0, ( i) -> i + 1).parallel().pairMap(( a, b) -> b - a).limit(100).toArray());
        Assert.assertFalse(IntStreamEx.range(1000).greater(2000).parallel().pairMap(( a, b) -> a).findFirst().isPresent());
    }

    @Test
    public void testToByteArray() {
        byte[] expected = new byte[10000];
        for (int i = 0; i < (expected.length); i++)
            expected[i] = ((byte) (i));

        Assert.assertArrayEquals(expected, IntStreamEx.range(0, 10000).toByteArray());
        Assert.assertArrayEquals(expected, IntStreamEx.range(0, 10000).parallel().toByteArray());
        Assert.assertArrayEquals(expected, IntStreamEx.range(0, 10000).greater((-1)).toByteArray());
        Assert.assertArrayEquals(expected, IntStreamEx.range(0, 10000).parallel().greater((-1)).toByteArray());
    }

    @Test
    public void testToCharArray() {
        char[] expected = new char[10000];
        for (int i = 0; i < (expected.length); i++)
            expected[i] = ((char) (i));

        Assert.assertArrayEquals(expected, IntStreamEx.range(0, 10000).toCharArray());
        Assert.assertArrayEquals(expected, IntStreamEx.range(0, 10000).parallel().toCharArray());
        Assert.assertArrayEquals(expected, IntStreamEx.range(0, 10000).greater((-1)).toCharArray());
        Assert.assertArrayEquals(expected, IntStreamEx.range(0, 10000).parallel().greater((-1)).toCharArray());
    }

    @Test
    public void testToShortArray() {
        short[] expected = new short[10000];
        for (int i = 0; i < (expected.length); i++)
            expected[i] = ((short) (i));

        Assert.assertArrayEquals(expected, IntStreamEx.range(0, 10000).toShortArray());
        Assert.assertArrayEquals(expected, IntStreamEx.range(0, 10000).parallel().toShortArray());
        Assert.assertArrayEquals(expected, IntStreamEx.range(0, 10000).greater((-1)).toShortArray());
        Assert.assertArrayEquals(expected, IntStreamEx.range(0, 10000).parallel().greater((-1)).toShortArray());
    }

    @Test
    public void testJoining() {
        Assert.assertEquals("0,1,2,3,4,5,6,7,8,9", IntStreamEx.range(10).joining(","));
        Assert.assertEquals("0,1,2,3,4,5,6,7,8,9", IntStreamEx.range(10).parallel().joining(","));
        Assert.assertEquals("[0,1,2,3,4,5,6,7,8,9]", IntStreamEx.range(10).joining(",", "[", "]"));
        Assert.assertEquals("[0,1,2,3,4,5,6,7,8,9]", IntStreamEx.range(10).parallel().joining(",", "[", "]"));
    }

    @Test
    public void testMapToEntry() {
        Map<Integer, List<Integer>> result = IntStreamEx.range(10).mapToEntry(( x) -> x % 2, ( x) -> x).grouping();
        Assert.assertEquals(Arrays.asList(0, 2, 4, 6, 8), result.get(0));
        Assert.assertEquals(Arrays.asList(1, 3, 5, 7, 9), result.get(1));
    }

    static final class HundredIterator implements PrimitiveIterator.OfInt {
        int i = 0;

        @Override
        public boolean hasNext() {
            return (i) < 100;
        }

        @Override
        public int nextInt() {
            return (i)++;
        }
    }

    @Test
    public void testRecreate() {
        Set<Integer> expected = IntStreamEx.range(1, 100).boxed().toSet();
        Assert.assertEquals(expected, IntStreamEx.of(StreamSupport.intStream(Spliterators.spliteratorUnknownSize(new IntStreamExTest.HundredIterator(), Spliterator.ORDERED), false)).skip(1).boxed().toSet());
        Assert.assertEquals(expected, IntStreamEx.of(StreamSupport.intStream(Spliterators.spliteratorUnknownSize(new IntStreamExTest.HundredIterator(), Spliterator.ORDERED), true)).skip(1).boxed().toCollection(HashSet::new));
        Assert.assertEquals(expected, IntStreamEx.of(StreamSupport.intStream(Spliterators.spliteratorUnknownSize(new IntStreamExTest.HundredIterator(), Spliterator.ORDERED), true)).skipOrdered(1).boxed().toSet());
        Assert.assertEquals(expected, IntStreamEx.of(StreamSupport.intStream(Spliterators.spliterator(new IntStreamExTest.HundredIterator(), 100, ((Spliterator.ORDERED) | (Spliterator.CONCURRENT))), true)).skipOrdered(1).boxed().toSet());
        Assert.assertEquals(expected, IntStreamEx.of(StreamSupport.intStream(Spliterators.spliteratorUnknownSize(new IntStreamExTest.HundredIterator(), Spliterator.ORDERED), true)).unordered().skipOrdered(1).boxed().toCollection(HashSet::new));
        Assert.assertEquals(expected, IntStreamEx.iterate(0, ( i) -> i + 1).skip(1).greater(0).limit(99).boxed().toSet());
        Assert.assertEquals(500, ((int) (IntStreamEx.iterate(0, ( i) -> i + 1).skipOrdered(1).greater(0).boxed().parallel().findAny(( i) -> i == 500).get())));
        Assert.assertEquals(expected, IntStreamEx.iterate(0, ( i) -> i + 1).skipOrdered(1).greater(0).limit(99).boxed().parallel().toSet());
    }

    @Test
    public void testTakeWhile() {
        Assert.assertArrayEquals(IntStreamEx.range(100).toArray(), IntStreamEx.iterate(0, ( i) -> i + 1).takeWhile(( i) -> i < 100).toArray());
        Assert.assertEquals(0, IntStreamEx.empty().takeWhile(( i) -> true).count());
        Assert.assertEquals(0, IntStreamEx.iterate(0, ( i) -> i + 1).takeWhile(( i) -> i < 0).count());
        Assert.assertEquals(1, IntStreamEx.of(1, 3, 2).takeWhile(( i) -> i < 3).count());
        Assert.assertEquals(3, IntStreamEx.of(1, 2, 3).takeWhile(( i) -> i < 100).count());
    }

    @Test
    public void testTakeWhileInclusive() {
        Assert.assertArrayEquals(IntStreamEx.range(101).toArray(), IntStreamEx.iterate(0, ( i) -> i + 1).takeWhileInclusive(( i) -> i < 100).toArray());
        Assert.assertEquals(0, IntStreamEx.empty().takeWhileInclusive(( i) -> true).count());
        Assert.assertEquals(1, IntStreamEx.iterate(0, ( i) -> i + 1).takeWhileInclusive(( i) -> i < 0).count());
        Assert.assertEquals(2, IntStreamEx.of(1, 3, 2).takeWhileInclusive(( i) -> i < 3).count());
        Assert.assertEquals(3, IntStreamEx.of(1, 2, 3).takeWhileInclusive(( i) -> i < 100).count());
    }

    @Test
    public void testDropWhile() {
        Assert.assertArrayEquals(new int[]{ 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 }, IntStreamEx.range(100).dropWhile(( i) -> (i % 10) < 5).limit(10).toArray());
        Assert.assertEquals(100, IntStreamEx.range(100).dropWhile(( i) -> (i % 10) < 0).count());
        Assert.assertEquals(0, IntStreamEx.range(100).dropWhile(( i) -> (i % 10) < 10).count());
        Assert.assertEquals(OptionalInt.of(0), IntStreamEx.range(100).dropWhile(( i) -> (i % 10) < 0).findFirst());
        Assert.assertEquals(OptionalInt.empty(), IntStreamEx.range(100).dropWhile(( i) -> (i % 10) < 10).findFirst());
        Spliterator.OfInt spltr = IntStreamEx.range(100).dropWhile(( i) -> (i % 10) < 1).spliterator();
        Assert.assertTrue(spltr.tryAdvance((int x) -> Assert.assertEquals(1, x)));
        IntStream.Builder builder = IntStream.builder();
        spltr.forEachRemaining(builder);
        Assert.assertArrayEquals(IntStreamEx.range(2, 100).toArray(), builder.build().toArray());
    }

    @Test
    public void testIndexOf() {
        Assert.assertEquals(5, IntStreamEx.range(50, 100).indexOf(55).getAsLong());
        Assert.assertFalse(IntStreamEx.range(50, 100).indexOf(200).isPresent());
        Assert.assertEquals(5, IntStreamEx.range(50, 100).parallel().indexOf(55).getAsLong());
        Assert.assertFalse(IntStreamEx.range(50, 100).parallel().indexOf(200).isPresent());
        Assert.assertEquals(11, IntStreamEx.range(50, 100).indexOf(( x) -> x > 60).getAsLong());
        Assert.assertFalse(IntStreamEx.range(50, 100).indexOf(( x) -> x < 0).isPresent());
        Assert.assertEquals(11, IntStreamEx.range(50, 100).parallel().indexOf(( x) -> x > 60).getAsLong());
        Assert.assertFalse(IntStreamEx.range(50, 100).parallel().indexOf(( x) -> x < 0).isPresent());
    }

    @Test
    public void testFoldLeft() {
        // non-associative
        IntBinaryOperator accumulator = ( x, y) -> (x + y) * (x + y);
        Assert.assertEquals(2322576, IntStreamEx.constant(3, 4).foldLeft(accumulator).orElse((-1)));
        Assert.assertEquals(2322576, IntStreamEx.constant(3, 4).parallel().foldLeft(accumulator).orElse((-1)));
        Assert.assertFalse(IntStreamEx.empty().foldLeft(accumulator).isPresent());
        Assert.assertEquals(144, IntStreamEx.rangeClosed(1, 3).foldLeft(0, accumulator));
        Assert.assertEquals(144, IntStreamEx.rangeClosed(1, 3).parallel().foldLeft(0, accumulator));
    }

    @Test
    public void testMapFirstLast() {
        // capitalize
        String str = "testString";
        Assert.assertEquals("TestString", IntStreamEx.ofCodePoints(str).mapFirst(Character::toUpperCase).codePointsToString());
        TestHelpers.streamEx(() -> StreamEx.of(1, 2, 3, 4, 5), ( s) -> Assert.assertArrayEquals(new int[]{ -3, 2, 3, 4, 9 }, s.get().mapToInt(Integer::intValue).mapFirst(( x) -> x - 2).mapLast(( x) -> x + 2).mapFirst(( x) -> x - 2).mapLast(( x) -> x + 2).toArray()));
    }

    @Test
    public void testPeekFirst() {
        int[] input = new int[]{ 1, 10, 100, 1000 };
        AtomicInteger firstElement = new AtomicInteger();
        Assert.assertArrayEquals(new int[]{ 10, 100, 1000 }, IntStreamEx.of(input).peekFirst(firstElement::set).skip(1).toArray());
        Assert.assertEquals(1, firstElement.get());
        Assert.assertArrayEquals(new int[]{ 10, 100, 1000 }, IntStreamEx.of(input).skip(1).peekFirst(firstElement::set).toArray());
        Assert.assertEquals(10, firstElement.get());
        firstElement.set((-1));
        Assert.assertArrayEquals(new int[]{  }, IntStreamEx.of(input).skip(4).peekFirst(firstElement::set).toArray());
        Assert.assertEquals((-1), firstElement.get());
    }

    @Test
    public void testPeekLast() {
        int[] input = new int[]{ 1, 10, 100, 1000 };
        AtomicInteger lastElement = new AtomicInteger((-1));
        Assert.assertArrayEquals(new int[]{ 1, 10, 100 }, IntStreamEx.of(input).peekLast(lastElement::set).limit(3).toArray());
        Assert.assertEquals((-1), lastElement.get());
        Assert.assertArrayEquals(new int[]{ 1, 10, 100 }, IntStreamEx.of(input).less(1000).peekLast(lastElement::set).limit(3).toArray());
        Assert.assertEquals(100, lastElement.get());
        Assert.assertArrayEquals(input, IntStreamEx.of(input).peekLast(lastElement::set).limit(4).toArray());
        Assert.assertEquals(1000, lastElement.get());
        Assert.assertArrayEquals(new int[]{ 1, 10, 100 }, IntStreamEx.of(input).limit(3).peekLast(lastElement::set).toArray());
        Assert.assertEquals(100, lastElement.get());
    }

    @Test
    public void testScanLeft() {
        Assert.assertArrayEquals(new int[]{ 0, 1, 3, 6, 10, 15, 21, 28, 36, 45 }, IntStreamEx.range(10).scanLeft(Integer::sum));
        Assert.assertArrayEquals(new int[]{ 0, 1, 3, 6, 10, 15, 21, 28, 36, 45 }, IntStreamEx.range(10).parallel().scanLeft(Integer::sum));
        Assert.assertArrayEquals(new int[]{ 0, 1, 3, 6, 10, 15, 21, 28, 36, 45 }, IntStreamEx.range(10).filter(( x) -> true).scanLeft(Integer::sum));
        Assert.assertArrayEquals(new int[]{ 0, 1, 3, 6, 10, 15, 21, 28, 36, 45 }, IntStreamEx.range(10).filter(( x) -> true).parallel().scanLeft(Integer::sum));
        Assert.assertArrayEquals(new int[]{ 1, 1, 2, 6, 24, 120 }, IntStreamEx.rangeClosed(1, 5).scanLeft(1, ( a, b) -> a * b));
        Assert.assertArrayEquals(new int[]{ 1, 1, 2, 6, 24, 120 }, IntStreamEx.rangeClosed(1, 5).parallel().scanLeft(1, ( a, b) -> a * b));
    }

    @Test
    public void testProduce() {
        Scanner sc = new Scanner("1 2 3 4 20000000000 test");
        Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4 }, IntStreamExTest.scannerInts(sc).stream().toArray());
        Assert.assertEquals("20000000000", sc.next());
    }

    @Test
    public void testOfInputStream() {
        byte[] data = new byte[]{ 5, 3, 10, 1, 4, -1 };
        try (IntStream s = IntStreamEx.of(new ByteArrayInputStream(data))) {
            Assert.assertEquals(22, s.map(( b) -> ((byte) (b))).sum());
        }
        try (IntStream s = IntStreamEx.of(new ByteArrayInputStream(data))) {
            Assert.assertEquals(278, s.sum());
        }
    }

    @Test
    public void testAsInputStream() throws IOException {
        AtomicBoolean flag = new AtomicBoolean(false);
        InputStream is = IntStreamEx.range(256).onClose(() -> flag.set(true)).asByteInputStream();
        byte[] data = new byte[256];
        Assert.assertEquals(2, is.skip(2));
        Assert.assertEquals(254, is.read(data));
        Assert.assertEquals((-1), is.read());
        for (int i = 0; i < 254; i++) {
            Assert.assertEquals(((byte) (i + 2)), data[i]);
        }
        Assert.assertEquals(0, data[254]);
        Assert.assertEquals(0, data[255]);
        Assert.assertFalse(flag.get());
        is.close();
        Assert.assertTrue(flag.get());
    }

    @Test
    public void testPrefix() {
        Assert.assertArrayEquals(new int[]{ 1, 3, 6, 10, 20 }, IntStreamEx.of(1, 2, 3, 4, 10).prefix(Integer::sum).toArray());
        Assert.assertEquals(OptionalInt.of(10), IntStreamEx.of(1, 2, 3, 4, 10).prefix(Integer::sum).findFirst(( x) -> x > 7));
        Assert.assertEquals(OptionalInt.empty(), IntStreamEx.of(1, 2, 3, 4, 10).prefix(Integer::sum).findFirst(( x) -> x > 20));
    }

    @Test
    public void testIntersperse() {
        Assert.assertArrayEquals(new int[]{ 1, 0, 10, 0, 100, 0, 1000 }, IntStreamEx.of(1, 10, 100, 1000).intersperse(0).toArray());
        Assert.assertEquals(0L, IntStreamEx.empty().intersperse(1).count());
    }
}

