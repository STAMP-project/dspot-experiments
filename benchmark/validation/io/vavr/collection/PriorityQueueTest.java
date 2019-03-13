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


import io.vavr.TestComparators;
import io.vavr.Tuple;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Spliterator;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import org.junit.Test;


public class PriorityQueueTest extends AbstractTraversableTest {
    private final io.vavr.collection.List<Integer> values = io.vavr.collection.List.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5, 8, 9, 7, 9, 3, 2, 3, 8, 4, 6, 2, 6, 4, 3, 3, 8, 3, 2, 7, 9, 5, 0, 2, 8, 8, 4, 1, 9, 7, 1, 6, 9, 3, 9, 9, 3, 7, 5, 1, 0);

    @Test
    @Override
    public void shouldScanLeftWithNonComparable() {
        // The resulting type would need a comparator
    }

    @Test
    @Override
    public void shouldScanRightWithNonComparable() {
        // The resulting type would need a comparator
    }

    @Test
    public void shouldScanWithNonComparable() {
        // makes no sense because sorted sets contain ordered elements
    }

    @Test
    public void shouldCreateFromStream() {
        final PriorityQueue<Integer> source = PriorityQueue.ofAll(values.toJavaStream());
        assertThat(source).isEqualTo(ofAll(values));
    }

    @Test
    public void shouldReturnOrdered() {
        final PriorityQueue<Integer> source = of(3, 1, 4);
        assertThat(source.isOrdered()).isTrue();
    }

    // -- static narrow
    @Test
    public void shouldNarrowPriorityQueue() {
        final PriorityQueue<Double> doubles = PriorityQueue.of(PriorityQueue, TestComparators.toStringComparator(), 1.0);
        final PriorityQueue<Number> numbers = PriorityQueue.narrow(PriorityQueue, doubles);
        final int actual = numbers.enqueue(new BigDecimal("2.0")).sum().intValue();
        assertThat(actual).isEqualTo(3);
    }

    // -- fill(int, Supplier)
    @Test
    public void shouldReturnManyAfterFillWithConstantSupplier() {
        assertThat(fill(17, () -> 7)).hasSize(17).containsOnly(7);
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
        assertThat(fill(17, 7)).hasSize(17).containsOnly(7);
    }

    // -- toList
    @Test
    public void toListIsSortedAccordingToComparator() {
        final Comparator<Integer> comparator = PriorityQueueTest.composedComparator();
        final PriorityQueue<Integer> queue = PriorityQueue.ofAll(comparator, values);
        assertThat(queue.toList()).isEqualTo(values.sorted(comparator));
    }

    // -- merge
    @Test
    public void shouldMergeTwoPriorityQueues() {
        final PriorityQueue<Integer> source = of(3, 1, 4, 1, 5);
        final PriorityQueue<Integer> target = of(9, 2, 6, 5, 3);
        assertThat(source.merge(target)).isEqualTo(of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3));
        assertThat(PriorityQueue.of(PriorityQueue, 3).merge(PriorityQueue.of(PriorityQueue, 1))).isEqualTo(of(3, 1));
    }

    // -- distinct
    @Test
    public void shouldComputeDistinctOfNonEmptyTraversableUsingKeyExtractor() {
        final Comparator<String> comparator = Comparator.comparingInt(( o) -> o.charAt(1));
        assertThat(PriorityQueue.of(PriorityQueue, comparator, "5c", "1a", "3a", "1a", "2a", "4b", "3b").distinct().map(( s) -> s.substring(1))).isEqualTo(of("a", "b", "c"));
    }

    // -- removeAll
    @Test
    public void shouldRemoveAllElements() {
        assertThat(of(3, 1, 4, 1, 5, 9, 2, 6).removeAll(of(1, 9, 1, 2))).isEqualTo(of(3, 4, 5, 6));
    }

    // -- enqueueAll
    @Test
    public void shouldEnqueueAllElements() {
        assertThat(of(3, 1, 4).enqueueAll(of(1, 5, 9, 2))).isEqualTo(of(3, 1, 4, 1, 5, 9, 2));
    }

    // -- peek
    @Test(expected = NoSuchElementException.class)
    public void shouldFailPeekOfEmpty() {
        empty().peek();
    }

    // -- dequeue
    @Test
    public void shouldDeque() {
        assertThat(of(3, 1, 4, 1, 5).dequeue()).isEqualTo(Tuple.of(1, of(3, 4, 1, 5)));
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldFailDequeueOfEmpty() {
        empty().dequeue();
    }

    // -- toPriorityQueue
    @Test
    public void shouldKeepInstanceOfPriorityQueue() {
        final PriorityQueue<Integer> queue = PriorityQueue.of(PriorityQueue, 1, 3, 2);
        assertThat(queue.toPriorityQueue()).isSameAs(queue);
    }

    // -- property based tests
    @Test
    public void shouldBehaveExactlyLikeAnotherPriorityQueue() {
        for (int i = 0; i < 10; i++) {
            final Random random = getRandom(987654321);
            final PriorityQueue<Integer> mutablePriorityQueue = new PriorityQueue<>();
            PriorityQueue<Integer> functionalPriorityQueue = PriorityQueue.empty(PriorityQueue);
            final int size = 100000;
            for (int j = 0; j < size; j++) {
                /* Insert */
                if (((random.nextInt()) % 3) == 0) {
                    assertMinimumsAreEqual(mutablePriorityQueue, functionalPriorityQueue);
                    final int value = (random.nextInt(size)) - (size / 2);
                    mutablePriorityQueue.add(value);
                    functionalPriorityQueue = functionalPriorityQueue.enqueue(value);
                }
                assertMinimumsAreEqual(mutablePriorityQueue, functionalPriorityQueue);
                /* Delete */
                if (((random.nextInt()) % 5) == 0) {
                    if (!(mutablePriorityQueue.isEmpty())) {
                        mutablePriorityQueue.poll();
                    }
                    if (!(functionalPriorityQueue.isEmpty())) {
                        functionalPriorityQueue = functionalPriorityQueue.tail();
                    }
                    assertMinimumsAreEqual(mutablePriorityQueue, functionalPriorityQueue);
                }
            }
            final Collection<Integer> oldValues = mutablePriorityQueue.stream().sorted().collect(Collectors.toList());
            final Collection<Integer> newValues = functionalPriorityQueue.toJavaList();
            assertThat(oldValues).isEqualTo(newValues);
        }
    }

    // -- spliterator
    @Test
    public void shouldHaveSortedSpliterator() {
        assertThat(of(1, 2, 3).spliterator().hasCharacteristics(Spliterator.SORTED)).isTrue();
    }

    @Test
    public void shouldHaveOrderedSpliterator() {
        assertThat(of(1, 2, 3).spliterator().hasCharacteristics(Spliterator.ORDERED)).isTrue();
    }

    @Test
    public void shouldHaveSizedSpliterator() {
        assertThat(of(1, 2, 3).spliterator().hasCharacteristics(((Spliterator.SIZED) | (Spliterator.SUBSIZED)))).isTrue();
    }

    @Test
    public void shouldNotHaveDistinctSpliterator() {
        assertThat(of(1, 2, 3).spliterator().hasCharacteristics(Spliterator.DISTINCT)).isFalse();
    }

    @Test
    public void shouldReturnSizeWhenSpliterator() {
        assertThat(of(1, 2, 3).spliterator().getExactSizeIfKnown()).isEqualTo(3);
    }

    // -- isSequential()
    @Test
    public void shouldReturnFalseWhenIsSequentialCalled() {
        assertThat(of(1, 2, 3).isSequential()).isFalse();
    }
}

