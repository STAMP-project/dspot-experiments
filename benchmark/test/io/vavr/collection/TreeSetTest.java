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
import io.vavr.Value;
import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.Test;


public class TreeSetTest extends AbstractSortedSetTest {
    // -- collector
    @Test
    public void shouldCollectEmpty() {
        final TreeSet<Integer> actual = Stream.<Integer>empty().collect(TreeSet.collector());
        assertThat(actual).isEmpty();
    }

    @Test
    public void shouldCollectNonEmpty() {
        final TreeSet<Integer> actual = Stream.of(1, 2, 3).collect(TreeSet.collector());
        final TreeSet<Integer> expected = of(1, 2, 3);
        assertThat(actual).isEqualTo(expected);
    }

    // -- construct
    @Test
    public void shouldConstructEmptySetWithExplicitComparator() {
        final TreeSet<Integer> ts = TreeSet.<Integer>of(Comparators.naturalComparator().reversed()).addAll(Array.ofAll(1, 2, 3));
        assertThat(ts.toArray()).isEqualTo(Array.of(3, 2, 1));
    }

    @Test
    public void shouldConstructStreamFromEmptyJavaStream() {
        final TreeSet<Integer> actual = ofJavaStream(Stream.<Integer>empty());
        final TreeSet<Integer> expected = empty();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldConstructStreamFromNonEmptyJavaStream() {
        final TreeSet<Integer> actual = TreeSet.ofAll(Comparators.naturalComparator(), Stream.of(1, 2, 3));
        final TreeSet<Integer> expected = of(1, 2, 3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldConstructStreamFromNonEmptyJavaStreamWithoutComparator() {
        final TreeSet<Integer> actual = ofJavaStream(Stream.of(1, 2, 3));
        final TreeSet<Integer> expected = of(1, 2, 3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldConstructFromTreeSetWithoutComparator() {
        final TreeSet<Integer> actual = TreeSet.ofAll(TreeSet.of(1));
        final TreeSet<Integer> expected = of(1);
        assertThat(actual).isEqualTo(expected);
    }

    // -- static narrow
    @Test
    public void shouldNarrowTreeSet() {
        final TreeSet<Double> doubles = TreeSet.of(TestComparators.toStringComparator(), 1.0);
        final TreeSet<Number> numbers = TreeSet.narrow(doubles);
        final int actual = numbers.add(new BigDecimal("2.0")).sum().intValue();
        assertThat(actual).isEqualTo(3);
    }

    // -- addAll
    @Test
    public void shouldKeepComparator() {
        final List<Integer> actual = TreeSet.empty(TreeSetTest.inverseIntComparator()).addAll(TreeSet.of(1, 2, 3)).toList();
        final List<Integer> expected = List.of(3, 2, 1);
        assertThat(actual).isEqualTo(expected);
    }

    // -- removeAll
    @Test
    public void shouldKeepComparatorOnRemoveAll() {
        final TreeSet<Integer> ts = TreeSet.of(Comparators.naturalComparator().reversed(), 1, 2, 3).removeAll(Array.ofAll(1, 2, 3)).addAll(Array.ofAll(4, 5, 6));
        assertThat(ts.toArray()).isEqualTo(Array.of(6, 5, 4));
    }

    // -- diff
    @Test
    public void shouldCalculateDiffIfNotTreeSet() {
        final TreeSet<Integer> actual = of(1, 2, 3).diff(HashSet.of(1, 2));
        final TreeSet<Integer> expected = of(3);
        assertThat(actual).isEqualTo(expected);
    }

    // -- union
    @Test
    public void shouldCalculateUnionIfNotTreeSet() {
        final TreeSet<Integer> actual = of(1, 2, 3).union(HashSet.of(4));
        final TreeSet<Integer> expected = of(1, 2, 3, 4);
        assertThat(actual).isEqualTo(expected);
    }

    // -- intersect
    @Test
    public void shouldCalculateEmptyIntersectIfNotTreeSet() {
        final TreeSet<Integer> actual = of(1, 2, 3).intersect(HashSet.of(4));
        assertThat(actual).isEmpty();
    }

    @Test
    public void shouldCalculateIntersectIfNotTreeSet() {
        final TreeSet<Integer> actual = of(1, 2, 3).intersect(HashSet.of(3));
        final TreeSet<Integer> expected = of(3);
        assertThat(actual).isEqualTo(expected);
    }

    // -- fill
    @Test
    public void shouldFillWithoutComparator() {
        final TreeSet<Integer> actual = TreeSet.fill(3, () -> 1);
        final TreeSet<Integer> expected = of(1, 1, 1);
        assertThat(actual).isEqualTo(expected);
    }

    // -- tabulate
    @Test
    public void shouldTabulateWithoutComparator() {
        final TreeSet<Integer> actual = TreeSet.tabulate(3, Function.identity());
        final TreeSet<Integer> expected = of(0, 1, 2);
        assertThat(actual).isEqualTo(expected);
    }

    // -- toSortedSet
    @Test
    public void shouldReturnSelfOnConvertToSortedSet() {
        final Value<Integer> value = of(1, 2, 3);
        assertThat(value.toSortedSet()).isSameAs(value);
    }

    @Test
    public void shouldReturnSelfOnConvertToSortedSetWithSameComparator() {
        final TreeSet<Integer> value = of(1, 2, 3);
        assertThat(value.toSortedSet(value.comparator())).isSameAs(value);
    }

    @Test
    public void shouldNotReturnSelfOnConvertToSortedSetWithDifferentComparator() {
        final Value<Integer> value = of(1, 2, 3);
        assertThat(value.toSortedSet(Integer::compareTo)).isNotSameAs(value);
    }

    @Test
    public void shouldPreserveComparatorOnConvertToSortedSetWithoutDistinctComparator() {
        final Value<Integer> value = TreeSet.of(Comparators.naturalComparator().reversed(), 1, 2, 3);
        assertThat(value.toSortedSet().mkString(",")).isEqualTo("3,2,1");
    }

    // -- transform()
    @Test
    public void shouldTransform() {
        final String transformed = of(42).transform(( v) -> String.valueOf(v.get()));
        assertThat(transformed).isEqualTo("42");
    }
}

