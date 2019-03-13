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
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Spliterator;
import org.junit.Test;


public abstract class AbstractSortedSetTest extends AbstractSetTest {
    // -- static narrow
    @Test
    public void shouldNarrowSortedSet() {
        final SortedSet<Double> doubles = of(TestComparators.toStringComparator(), 1.0);
        final SortedSet<Number> numbers = SortedSet.narrow(doubles);
        final int actual = numbers.add(new BigDecimal("2.0")).sum().intValue();
        assertThat(actual).isEqualTo(3);
    }

    @Test
    public void shouldReturnComparator() {
        assertThat(of(1).comparator()).isNotNull();
    }

    @Override
    @Test
    public void shouldPreserveSingletonInstanceOnDeserialization() {
        // not possible, because the empty instance stores information about the underlying comparator
    }

    @Override
    @Test
    public void shouldScanWithNonComparable() {
        // makes no sense because sorted sets contain ordered elements
    }

    @Override
    @Test
    public void shouldNarrowSet() {
        // makes no sense because disjoint types share not the same ordering
    }

    @Override
    @Test
    public void shouldNarrowTraversable() {
        // makes no sense because disjoint types share not the same ordering
    }

    // -- head
    @Test
    public void shouldReturnHeadOfNonEmptyHavingNaturalOrder() {
        assertThat(of(Comparator.naturalOrder(), 1, 2, 3, 4).head()).isEqualTo(1);
    }

    @Test
    public void shouldReturnHeadOfNonEmptyHavingReversedOrder() {
        assertThat(of(Comparator.reverseOrder(), 1, 2, 3, 4).head()).isEqualTo(4);
    }

    // -- init
    @Test
    public void shouldReturnInitOfNonEmptyHavingNaturalOrder() {
        assertThat(of(Comparator.naturalOrder(), 1, 2, 3, 4).init()).isEqualTo(of(Comparator.naturalOrder(), 1, 2, 3));
    }

    @Test
    public void shouldReturnInitOfNonEmptyHavingReversedOrder() {
        assertThat(of(Comparator.reverseOrder(), 1, 2, 3, 4).init()).isEqualTo(of(Comparator.naturalOrder(), 2, 3, 4));
    }

    // -- last
    @Test
    public void shouldReturnLastOfNonEmptyHavingNaturalOrder() {
        assertThat(of(Comparator.naturalOrder(), 1, 2, 3, 4).last()).isEqualTo(4);
    }

    @Test
    public void shouldReturnLastOfNonEmptyHavingReversedOrder() {
        assertThat(of(Comparator.reverseOrder(), 1, 2, 3, 4).last()).isEqualTo(1);
    }

    // -- tail
    @Test
    public void shouldReturnTailOfNonEmptyHavingNaturalOrder() {
        assertThat(of(Comparator.naturalOrder(), 1, 2, 3, 4).tail()).isEqualTo(of(Comparator.naturalOrder(), 2, 3, 4));
    }

    @Test
    public void shouldReturnTailOfNonEmptyHavingReversedOrder() {
        assertThat(of(Comparator.reverseOrder(), 1, 2, 3, 4).tail()).isEqualTo(of(Comparator.naturalOrder(), 1, 2, 3));
    }

    // -- equals
    @Test
    public void shouldBeEqualWhenHavingSameElementsAndDifferentOrder() {
        final SortedSet<Integer> set1 = of(Comparator.naturalOrder(), 1, 2, 3);
        final SortedSet<Integer> set2 = of(Comparator.reverseOrder(), 3, 2, 1);
        assertThat(set1).isEqualTo(set2);
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

    // -- isSequential()
    @Test
    public void shouldReturnFalseWhenIsSequentialCalled() {
        assertThat(of(1, 2, 3).isSequential()).isFalse();
    }
}

