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
import io.vavr.Tuple2;
import io.vavr.Value;
import java.math.BigDecimal;
import java.util.Spliterator;
import org.junit.Assert;
import org.junit.Test;


public class HashSetTest extends AbstractSetTest {
    // -- static narrow
    @Test
    public void shouldNarrowHashSet() {
        final HashSet<Double> doubles = of(1.0);
        final HashSet<Number> numbers = HashSet.narrow(doubles);
        final int actual = numbers.add(new BigDecimal("2.0")).sum().intValue();
        assertThat(actual).isEqualTo(3);
    }

    // -- slideBy is not expected to work for larger subsequences, due to unspecified iteration order
    @Test
    public void shouldSlideNonNilBySomeClassifier() {
        // ignore
    }

    // TODO move to traversable
    // -- zip
    @Test
    public void shouldZipNils() {
        final HashSet<Tuple2<Object, Object>> actual = empty().zip(empty());
        assertThat(actual).isEqualTo(empty());
    }

    @Test
    public void shouldZipEmptyAndNonNil() {
        final HashSet<Tuple2<Object, Integer>> actual = empty().zip(of(1));
        assertThat(actual).isEqualTo(empty());
    }

    @Test
    public void shouldZipNonEmptyAndNil() {
        final HashSet<Tuple2<Integer, Integer>> actual = of(1).zip(empty());
        assertThat(actual).isEqualTo(empty());
    }

    @Test
    public void shouldZipNonNilsIfThisIsSmaller() {
        final HashSet<Tuple2<Integer, String>> actual = of(1, 2).zip(of("a", "b", "c"));
        final HashSet<Tuple2<Integer, String>> expected = of(Tuple.of(1, "a"), Tuple.of(2, "b"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipNonNilsIfThatIsSmaller() {
        final HashSet<Tuple2<Integer, String>> actual = of(1, 2, 3).zip(of("a", "b"));
        final HashSet<Tuple2<Integer, String>> expected = of(Tuple.of(1, "a"), Tuple.of(2, "b"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipNonNilsOfSameSize() {
        final HashSet<Tuple2<Integer, String>> actual = of(1, 2, 3).zip(of("a", "b", "c"));
        final HashSet<Tuple2<Integer, String>> expected = of(Tuple.of(1, "a"), Tuple.of(2, "b"), Tuple.of(3, "c"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIfZipWithThatIsNull() {
        empty().zip(null);
    }

    // TODO move to traversable
    // -- zipAll
    @Test
    public void shouldZipAllNils() {
        // ignore
    }

    @Test
    public void shouldZipAllEmptyAndNonNil() {
        // ignore
    }

    @Test
    public void shouldZipAllNonEmptyAndNil() {
        final HashSet<?> actual = of(1).zipAll(empty(), null, null);
        final HashSet<Tuple2<Integer, Object>> expected = of(Tuple.of(1, null));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipAllNonNilsIfThisIsSmaller() {
        final HashSet<Tuple2<Integer, String>> actual = of(1, 2).zipAll(of("a", "b", "c"), 9, "z");
        final HashSet<Tuple2<Integer, String>> expected = of(Tuple.of(1, "a"), Tuple.of(2, "b"), Tuple.of(9, "c"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipAllNonNilsIfThatIsSmaller() {
        final HashSet<Tuple2<Integer, String>> actual = of(1, 2, 3).zipAll(of("a", "b"), 9, "z");
        final HashSet<Tuple2<Integer, String>> expected = of(Tuple.of(1, "a"), Tuple.of(2, "b"), Tuple.of(3, "z"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipAllNonNilsOfSameSize() {
        final HashSet<Tuple2<Integer, String>> actual = of(1, 2, 3).zipAll(of("a", "b", "c"), 9, "z");
        final HashSet<Tuple2<Integer, String>> expected = of(Tuple.of(1, "a"), Tuple.of(2, "b"), Tuple.of(3, "c"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIfZipAllWithThatIsNull() {
        empty().zipAll(null, null, null);
    }

    // TODO move to traversable
    // -- zipWithIndex
    @Test
    public void shouldZipNilWithIndex() {
        assertThat(this.<String>empty().zipWithIndex()).isEqualTo(this.<Tuple2<String, Integer>>empty());
    }

    @Test
    public void shouldZipNonNilWithIndex() {
        final HashSet<Tuple2<String, Integer>> actual = of("a", "b", "c").zipWithIndex();
        final HashSet<Tuple2<String, Integer>> expected = of(Tuple.of("a", 0), Tuple.of("b", 1), Tuple.of("c", 2));
        assertThat(actual).isEqualTo(expected);
    }

    // -- transform()
    @Test
    public void shouldTransform() {
        final String transformed = of(42).transform(( v) -> String.valueOf(v.get()));
        assertThat(transformed).isEqualTo("42");
    }

    @Test
    public void shouldBeEqual() {
        Assert.assertTrue(HashSet.of(1).equals(HashSet.of(1)));
    }

    // -- toSet
    @Test
    public void shouldReturnSelfOnConvertToSet() {
        final Value<Integer> value = of(1, 2, 3);
        assertThat(value.toSet()).isSameAs(value);
    }

    // -- spliterator
    @Test
    public void shouldNotHaveSortedSpliterator() {
        assertThat(of(1, 2, 3).spliterator().hasCharacteristics(Spliterator.SORTED)).isFalse();
    }

    @Test
    public void shouldNotHaveOrderedSpliterator() {
        assertThat(of(1, 2, 3).spliterator().hasCharacteristics(Spliterator.ORDERED)).isFalse();
    }

    // -- isSequential()
    @Test
    public void shouldReturnFalseWhenIsSequentialCalled() {
        assertThat(of(1, 2, 3).isSequential()).isFalse();
    }
}

