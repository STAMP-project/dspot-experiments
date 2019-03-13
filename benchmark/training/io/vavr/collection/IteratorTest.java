/**
 * __    __  __  __    __  ___
 * \  \  /  /    \  \  /  /  __/
 *  \  \/  /  /\  \  \/  /  /
 *   \____/__/  \__\____/__/
 *
 * Copyright 2014-2019 Vavr, http://vavr.io
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
package io.vavr.collection;


import io.vavr.Tuple;
import io.vavr.control.Option;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.Test;


public class IteratorTest extends AbstractTraversableTest {
    @Test(expected = NoSuchElementException.class)
    public void shouldFailOfEmptyArgList() {
        of().next();
    }

    // -- static narrow()
    @Test
    public void shouldNarrowIterator() {
        final Iterator.Iterator<Double> doubles = of(1.0);
        final Iterator.Iterator<Number> numbers = narrow(doubles);
        final int actual = numbers.concat(Iterator.Iterator.of(new BigDecimal("2.0"))).sum().intValue();
        assertThat(actual).isEqualTo(3);
    }

    // -- static ofAll()
    @Test(expected = NoSuchElementException.class)
    public void shouldFailOfEmptyIterable() {
        ofAll(List.empty()).next();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldFailOfEmptyBoolean() {
        ofAll(new boolean[0]).next();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldFailOfEmptyByte() {
        ofAll(new byte[0]).next();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldFailOfEmptyChar() {
        ofAll(new char[0]).next();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldFailOfEmptyDouble() {
        ofAll(new double[0]).next();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldFailOfEmptyFloat() {
        ofAll(new float[0]).next();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldFailOfEmptyInt() {
        ofAll(new int[0]).next();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldFailOfEmptyLong() {
        ofAll(new long[0]).next();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldFailOfEmptyShort() {
        ofAll(new short[0]).next();
    }

    // -- static concat()
    @Test
    public void shouldConcatEmptyIterableIterable() {
        final Iterable<Iterable<Integer>> empty = List.empty();
        assertThat(concat(empty)).isSameAs(Iterator.Iterator.empty());
    }

    @Test
    public void shouldConcatNonEmptyIterableIterable() {
        final Iterable<Iterable<Integer>> itIt = List.of(List.of(1, 2), List.of(3));
        assertThat(concat(itIt)).isEqualTo(Iterator.Iterator.of(1, 2, 3));
    }

    @Test
    public void shouldConcatEmptyArrayIterable() {
        assertThat(concat()).isSameAs(Iterator.Iterator.empty());
    }

    @Test
    public void shouldConcatNonEmptyArrayIterable() {
        assertThat(concat(List.of(1, 2), List.of(3))).isEqualTo(Iterator.Iterator.of(1, 2, 3));
    }

    @Test
    public void shouldConcatNestedConcatIterators() {
        assertThat(concat(List.of(1, 2), List.of(3), concat(List.of(4, 5)))).isEqualTo(Iterator.Iterator.of(1, 2, 3, 4, 5));
        assertThat(concat(concat(List.of(4, 5)), List.of(1, 2), List.of(3))).isEqualTo(Iterator.Iterator.of(4, 5, 1, 2, 3));
    }

    @Test
    public void shouldConcatToConcatIterator() {
        assertThat(concat(List.of(1, 2)).concat(List.of(3).iterator())).isEqualTo(Iterator.Iterator.of(1, 2, 3));
    }

    // -- fill(int, Supplier)
    @Test
    public void shouldReturnManyAfterFillWithConstantSupplier() {
        assertThat(fill(17, () -> 7)).hasSize(17);
    }

    // -- fill(int, T)
    @Test
    public void shouldReturnEmptyAfterFillWithZeroCount() {
        assertThat(fill(0, 7)).isEqualTo(empty());
    }

    @Test
    public void shouldReturnEmptyAfterFillWithNegativeCount() {
        assertThat(fill((-1), 7)).isEqualTo(empty());
    }

    @Test
    public void shouldReturnManyAfterFillWithConstant() {
        assertThat(fill(17, 7)).hasSize(17);
    }

    // -- concat
    @Test
    public void shouldConcatThisNonEmptyWithEmpty() {
        final Iterator.Iterator<Integer> it = Iterator.Iterator.of(1);
        assertThat(it.concat(Iterator.Iterator.<Integer>empty())).isSameAs(it);
    }

    @Test
    public void shouldConcatThisEmptyWithNonEmpty() {
        final Iterator.Iterator<Integer> it = Iterator.Iterator.of(1);
        assertThat(Iterator.Iterator.<Integer>empty().concat(it)).isSameAs(it);
    }

    @Test
    public void shouldConcatThisNonEmptyWithNonEmpty() {
        assertThat(Iterator.Iterator.of(1).concat(Iterator.Iterator.of(2))).isEqualTo(Iterator.Iterator.of(1, 2));
    }

    // -- transform
    @Test
    public void shouldTransform() {
        final Iterator.Iterator<?> it = Iterator.Iterator.of(1, 2).transform(( ii) -> ii.drop(1));
        assertThat(it).isEqualTo(Iterator.Iterator.of(2));
    }

    // -- static from(int)
    @Test
    public void shouldGenerateIntStream() {
        assertThat(from((-1)).take(3)).isEqualTo(Iterator.Iterator.of((-1), 0, 1));
    }

    @Test
    public void shouldGenerateOverflowingIntStream() {
        // noinspection NumericOverflow
        assertThat(from(Integer.MAX_VALUE).take(2)).isEqualTo(Iterator.Iterator.of(Integer.MAX_VALUE, ((Integer.MAX_VALUE) + 1)));
    }

    // -- static from(int, int)
    @Test
    public void shouldGenerateIntStreamWithStep() {
        assertThat(from((-1), 6).take(3)).isEqualTo(Iterator.Iterator.of((-1), 5, 11));
    }

    @Test
    public void shouldGenerateOverflowingIntStreamWithStep() {
        // noinspection NumericOverflow
        assertThat(from(Integer.MAX_VALUE, 2).take(2)).isEqualTo(Iterator.Iterator.of(Integer.MAX_VALUE, ((Integer.MAX_VALUE) + 2)));
    }

    // -- static from(long)
    @Test
    public void shouldGenerateLongStream() {
        assertThat(from((-1L)).take(3)).isEqualTo(Iterator.Iterator.of((-1L), 0L, 1L));
    }

    @Test
    public void shouldGenerateOverflowingLongStream() {
        // noinspection NumericOverflow
        assertThat(from(Long.MAX_VALUE).take(2)).isEqualTo(Iterator.Iterator.of(Long.MAX_VALUE, ((Long.MAX_VALUE) + 1)));
    }

    // -- static from(long, long)
    @Test
    public void shouldGenerateLongStreamWithStep() {
        assertThat(from((-1L), 5L).take(3)).isEqualTo(Iterator.Iterator.of((-1L), 4L, 9L));
    }

    @Test
    public void shouldGenerateOverflowingLongStreamWithStep() {
        // noinspection NumericOverflow
        assertThat(from(Long.MAX_VALUE, 2).take(2)).isEqualTo(Iterator.Iterator.of(Long.MAX_VALUE, ((Long.MAX_VALUE) + 2)));
    }

    // -- static continually(Supplier)
    @Test
    public void shouldGenerateInfiniteStreamBasedOnSupplier() {
        assertThat(continually(() -> 1).take(13).reduce(( i, j) -> i + j)).isEqualTo(13);
    }

    @Test
    public void shouldGenerateInfiniteStreamBasedOnConstant() {
        assertThat(continually(1).take(13).reduce(( i, j) -> i + j)).isEqualTo(13);
    }

    // -- static iterate(T, Function)
    @Test
    public void shouldGenerateInfiniteStreamBasedOnSupplierWithAccessToPreviousValue() {
        assertThat(iterate(2, ( i) -> i + 2).take(3).reduce(( i, j) -> i + j)).isEqualTo(12);
    }

    @Test
    public void shouldNotCallSupplierUntilNecessary() {
        assertThat(iterate(2, ( i) -> {
            throw new RuntimeException();
        }).head()).isEqualTo(2);
    }

    // -- static iterate(Supplier<Option>)
    static class OptionSupplier implements Supplier<Option<Integer>> {
        int cnt;

        final int end;

        OptionSupplier(int start) {
            this(start, Integer.MAX_VALUE);
        }

        OptionSupplier(int start, int end) {
            this.cnt = start;
            this.end = end;
        }

        @Override
        public Option<Integer> get() {
            Option<Integer> res;
            if ((cnt) < (end)) {
                res = Option.some(cnt);
            } else {
                res = Option.none();
            }
            (cnt)++;
            return res;
        }
    }

    @Test
    public void shouldGenerateInfiniteStreamBasedOnOptionSupplier() {
        assertThat(Iterator.Iterator.iterate(new IteratorTest.OptionSupplier(1)).take(5).reduce(( i, j) -> i + j)).isEqualTo(15);
    }

    @Test
    public void shouldGenerateFiniteStreamBasedOnOptionSupplier() {
        assertThat(Iterator.Iterator.iterate(new IteratorTest.OptionSupplier(1, 4)).take(50000).reduce(( i, j) -> i + j)).isEqualTo(6);
    }

    @Test
    public void shouldCreateDoubleRangeByFromInfinity() {
        assertThat(rangeBy(Double.NEGATIVE_INFINITY, 0.0, 1.0)).startsWith(Double.NEGATIVE_INFINITY, (-(Double.MAX_VALUE)));
        assertThat(rangeBy(Double.POSITIVE_INFINITY, 0.0, (-1.0))).startsWith(Double.POSITIVE_INFINITY, Double.MAX_VALUE);
    }

    @Test
    public void shouldCreateDoubleRangeClosedByFromInfinity() {
        assertThat(rangeClosedBy(Double.NEGATIVE_INFINITY, 0.0, 1.0)).startsWith(Double.NEGATIVE_INFINITY, (-(Double.MAX_VALUE)));
        assertThat(rangeClosedBy(Double.POSITIVE_INFINITY, 0.0, (-1.0))).startsWith(Double.POSITIVE_INFINITY, Double.MAX_VALUE);
    }

    @Test
    public void shouldCreateDoubleRangeByFromMaxToInfinity() {
        assertThat(rangeBy(Double.MAX_VALUE, Double.POSITIVE_INFINITY, 3.0E307)).isEqualTo(of(Double.MAX_VALUE));
        assertThat(rangeBy((-(Double.MAX_VALUE)), Double.NEGATIVE_INFINITY, (-3.0E307))).isEqualTo(of((-(Double.MAX_VALUE))));
    }

    @Test
    public void shouldCreateDoubleRangeClosedByFromMaxToInfinity() {
        assertThat(rangeClosedBy(Double.MAX_VALUE, Double.POSITIVE_INFINITY, 3.0E307)).isEqualTo(of(Double.MAX_VALUE));
        assertThat(rangeClosedBy((-(Double.MAX_VALUE)), Double.NEGATIVE_INFINITY, (-3.0E307))).isEqualTo(of((-(Double.MAX_VALUE))));
    }

    @Test
    @Override
    public void shouldTakeUntilAllOnFalseCondition() {
        final Iterator.Iterator<Integer> actual = of(1, 2, 3).takeUntil(( x) -> false);
        assertThat(actual).isEqualTo(of(1, 2, 3));
        assertThat(actual.hasNext()).isFalse();
    }

    @Test
    public void shouldTakeUntilAllOnTrueCondition() {
        final Iterator.Iterator<Integer> actual = of(1, 2, 3).takeUntil(( x) -> true);
        assertThat(actual).isEqualTo(empty());
        assertThat(actual.hasNext()).isFalse();
    }

    // -- hasNext
    @Test
    public void multipleHasNext() {
        multipleHasNext(() -> Iterator.Iterator.of(1));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3));
        multipleHasNext(() -> Iterator.Iterator.ofAll(true, true, false, true));
        multipleHasNext(() -> Iterator.Iterator.ofAll(new byte[]{ 1, 2, 3, 4 }));
        multipleHasNext(() -> Iterator.Iterator.ofAll(new char[]{ 1, 2, 3, 4 }));
        multipleHasNext(() -> Iterator.Iterator.ofAll(new double[]{ 1, 2, 3, 4 }));
        multipleHasNext(() -> Iterator.Iterator.ofAll(new float[]{ 1, 2, 3, 4 }));
        multipleHasNext(() -> Iterator.Iterator.ofAll(1, 2, 3, 4));
        multipleHasNext(() -> Iterator.Iterator.ofAll(new long[]{ 1, 2, 3, 4 }));
        multipleHasNext(() -> Iterator.Iterator.ofAll(new short[]{ 1, 2, 3, 4 }));
        multipleHasNext(() -> Iterator.Iterator.ofAll(Iterator.Iterator.of(1, 2, 3).toJavaList().iterator()));
        multipleHasNext(() -> Iterator.Iterator.ofAll(Iterator.Iterator.of(1, 2, 3).toJavaList()));
        multipleHasNext(() -> Iterator.Iterator.concat(List.of(Iterator.Iterator.empty(), Iterator.Iterator.of(1, 2, 3))));
        multipleHasNext(() -> Iterator.Iterator.concat(List.of(Iterator.Iterator.of(1, 2, 3), Iterator.Iterator.of(1, 2, 3))));
        multipleHasNext(() -> Iterator.Iterator.concat(Iterator.Iterator.of(1, 2, 3), Iterator.Iterator.of(1, 2, 3)));
        multipleHasNext(() -> Iterator.Iterator.continually(() -> 1), 5);
        multipleHasNext(() -> Iterator.Iterator.continually(1), 5);
        multipleHasNext(() -> Iterator.Iterator.fill(3, () -> 1));
        multipleHasNext(() -> Iterator.Iterator.from(1), 5);
        multipleHasNext(() -> Iterator.Iterator.from(1, 2), 5);
        multipleHasNext(() -> Iterator.Iterator.from(1L), 5);
        multipleHasNext(() -> Iterator.Iterator.from(1L, 2L), 5);
        multipleHasNext(() -> Iterator.Iterator.iterate(1, ( i) -> i + 1), 5);
        multipleHasNext(() -> Iterator.Iterator.iterate(new IteratorTest.OptionSupplier(1)), 5);
        multipleHasNext(() -> Iterator.Iterator.tabulate(10, ( i) -> i + 1));
        multipleHasNext(() -> Iterator.Iterator.unfold(10, ( x) -> x == 0 ? Option.none() : Option.of(new Tuple2<>((x - 1), x))));
        multipleHasNext(() -> Iterator.Iterator.unfoldLeft(10, ( x) -> x == 0 ? Option.none() : Option.of(new Tuple2<>((x - 1), x))));
        multipleHasNext(() -> Iterator.Iterator.unfoldRight(10, ( x) -> x == 0 ? Option.none() : Option.of(new Tuple2<>(x, (x - 1)))));
        multipleHasNext(() -> Iterator.Iterator.range('a', 'd'));
        multipleHasNext(() -> Iterator.Iterator.range(1, 4));
        multipleHasNext(() -> Iterator.Iterator.range(1L, 4L));
        multipleHasNext(() -> Iterator.Iterator.rangeClosed('a', 'd'));
        multipleHasNext(() -> Iterator.Iterator.rangeClosed(1, 4));
        multipleHasNext(() -> Iterator.Iterator.rangeClosed(1L, 4L));
        multipleHasNext(() -> Iterator.Iterator.rangeBy('a', 'd', 1));
        multipleHasNext(() -> Iterator.Iterator.rangeBy(1, 4, 1));
        multipleHasNext(() -> Iterator.Iterator.rangeBy(1.0, 4.0, 1));
        multipleHasNext(() -> Iterator.Iterator.rangeBy(1L, 4L, 1));
        multipleHasNext(() -> Iterator.Iterator.rangeClosedBy('a', 'd', 1));
        multipleHasNext(() -> Iterator.Iterator.rangeClosedBy(1, 4, 1));
        multipleHasNext(() -> Iterator.Iterator.rangeClosedBy(1.0, 4.0, 1));
        multipleHasNext(() -> Iterator.Iterator.rangeClosedBy(1L, 4L, 1));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3).concat(Iterator.Iterator.of(1, 2, 3)));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 1, 2, 1, 2).distinct());
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 1, 2, 1, 2).distinctBy(( e) -> e % 2));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 1, 2, 1, 2).distinctBy(Comparator.comparingInt(( e) -> e % 2)));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).drop(1));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).dropRight(1));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).dropUntil(( e) -> e == 3));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).dropWhile(( e) -> e == 1));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).filter(( e) -> e > 1));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).reject(( e) -> e <= 1));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).flatMap(( e) -> Iterator.Iterator.of(e, (e + 1))));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).grouped(2));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).intersperse((-1)));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3).map(( i) -> i * 2));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3).partition(( i) -> i < 2)._1);
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3).partition(( i) -> i < 2)._2);
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 2).replace(2, 42));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 2).replaceAll(2, 42));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3).retainAll(List.of(2)));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3).scanLeft(1, ( a, b) -> a + b));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3).scanRight(1, ( a, b) -> a + b));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3).slideBy(Function.identity()));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).sliding(2));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).sliding(2, 1));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).unzip(( i) -> Tuple.of(i, (i + 1)))._1);
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).unzip(( i) -> Tuple.of(i, (i + 1)))._2);
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).unzip3(( i) -> Tuple.of(i, (i + 1), (i + 2)))._1);
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).unzip3(( i) -> Tuple.of(i, (i + 1), (i + 2)))._2);
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).unzip3(( i) -> Tuple.of(i, (i + 1), (i + 2)))._3);
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).zip(Iterator.Iterator.from(1)));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).zipAll(Iterator.Iterator.of(1, 2), (-1), (-2)));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).zipWith(Iterator.Iterator.of(1, 2), ( a, b) -> a + b));
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).zipWithIndex());
        multipleHasNext(() -> Iterator.Iterator.of(1, 2, 3, 4).zipWithIndex(( a, i) -> a + i));
    }

    // -- unfoldRight
    @Test
    public void shouldUnfoldRightToEmpty() {
        assertThat(Iterator.Iterator.unfoldRight(0, ( x) -> Option.none())).isEqualTo(empty());
    }

    @Test
    public void shouldUnfoldRightSimpleList() {
        assertThat(Iterator.Iterator.unfoldRight(10, ( x) -> x == 0 ? Option.none() : Option.of(new Tuple2<>(x, (x - 1))))).isEqualTo(of(10, 9, 8, 7, 6, 5, 4, 3, 2, 1));
    }

    @Test
    public void shouldUnfoldLeftToEmpty() {
        assertThat(Iterator.Iterator.unfoldLeft(0, ( x) -> Option.none())).isEqualTo(empty());
    }

    @Test
    public void shouldUnfoldLeftSimpleList() {
        assertThat(Iterator.Iterator.unfoldLeft(10, ( x) -> x == 0 ? Option.none() : Option.of(new Tuple2<>((x - 1), x)))).isEqualTo(of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test
    public void shouldUnfoldToEmpty() {
        assertThat(Iterator.Iterator.unfold(0, ( x) -> Option.none())).isEqualTo(empty());
    }

    @Test
    public void shouldUnfoldSimpleList() {
        assertThat(Iterator.Iterator.unfold(10, ( x) -> x == 0 ? Option.none() : Option.of(new Tuple2<>((x - 1), x)))).isEqualTo(of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    // -- class initialization (see #1773)
    @Test(timeout = 5000)
    public void shouldNotDeadlockOnConcurrentClassInitialization() throws InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(new IteratorTest.ClassInitializer("io.vavr.collection.Iterator"));
        executorService.execute(new IteratorTest.ClassInitializer("io.vavr.collection.AbstractIterator"));
        executorService.shutdown();
        // try to access Vavr Iterator and it will hang
        Iterator.Iterator.empty().iterator();
    }

    static class ClassInitializer implements Runnable {
        private String type;

        ClassInitializer(String type) {
            this.type = type;
        }

        @Override
        public void run() {
            try {
                Class.forName(type);
            } catch (ClassNotFoundException e) {
                throw new Error(e);
            }
        }
    }

    // -- isLazy
    @Override
    @Test
    public void shouldVerifyLazyProperty() {
        assertThat(empty().isLazy()).isTrue();
        assertThat(of(1).isLazy()).isTrue();
    }

    // -- spliterator
    @Test
    public void shouldHaveOrderedSpliterator() {
        assertThat(of(1, 2, 3).spliterator().hasCharacteristics(Spliterator.ORDERED)).isTrue();
    }

    @Test
    public void shouldNotHaveSortedSpliterator() {
        assertThat(of(1, 2, 3).spliterator().hasCharacteristics(Spliterator.SORTED)).isFalse();
    }

    @Test
    public void shouldNotHaveSizedSpliterator() {
        assertThat(of(1, 2, 3).spliterator().hasCharacteristics(((Spliterator.SIZED) | (Spliterator.SUBSIZED)))).isFalse();
    }

    @Test
    public void shouldNotHaveDistinctSpliterator() {
        assertThat(of(1, 2, 3).spliterator().hasCharacteristics(Spliterator.DISTINCT)).isFalse();
    }

    @Test
    public void shouldNotReturnSizeWhenSpliterator() {
        assertThat(of(1, 2, 3).spliterator().getExactSizeIfKnown()).isEqualTo((-1));
    }

    // -- isSequential()
    @Test
    public void shouldReturnTrueWhenIsSequentialCalled() {
        assertThat(of(1, 2, 3).isSequential()).isTrue();
    }
}

