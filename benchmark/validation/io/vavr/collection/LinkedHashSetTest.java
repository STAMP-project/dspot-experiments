package io.vavr.collection;


import io.vavr.Value;
import java.math.BigDecimal;
import java.util.Spliterator;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class LinkedHashSetTest extends AbstractSetTest {
    @Test
    public void shouldKeepOrder() {
        final List<Integer> actual = LinkedHashSet.<Integer>empty().add(3).add(2).add(1).toList();
        assertThat(actual).isEqualTo(List.of(3, 2, 1));
    }

    // -- static narrow
    @Test
    public void shouldNarrowLinkedHashSet() {
        final LinkedHashSet<Double> doubles = of(1.0);
        final LinkedHashSet<Number> numbers = LinkedHashSet.narrow(doubles);
        final int actual = numbers.add(new BigDecimal("2.0")).sum().intValue();
        assertThat(actual).isEqualTo(3);
    }

    // -- replace
    @Test
    public void shouldReturnSameInstanceIfReplacingNonExistingElement() {
        final Set<Integer> set = LinkedHashSet.of(1, 2, 3);
        final Set<Integer> actual = set.replace(4, 0);
        assertThat(actual).isSameAs(set);
    }

    @Test
    public void shouldPreserveOrderWhenReplacingExistingElement() {
        final Set<Integer> set = LinkedHashSet.of(1, 2, 3);
        final Set<Integer> actual = set.replace(2, 0);
        final Set<Integer> expected = LinkedHashSet.of(1, 0, 3);
        assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(List.ofAll(actual)).isEqualTo(List.ofAll(expected));
    }

    @Test
    public void shouldPreserveOrderWhenReplacingExistingElementAndRemoveOtherIfElementAlreadyExists() {
        final Set<Integer> set = LinkedHashSet.of(1, 2, 3, 4, 5);
        final Set<Integer> actual = set.replace(2, 4);
        final Set<Integer> expected = LinkedHashSet.of(1, 4, 3, 5);
        assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(List.ofAll(actual)).isEqualTo(List.ofAll(expected));
    }

    @Test
    public void shouldReturnSameInstanceWhenReplacingExistingElementWithIdentity() {
        final Set<Integer> set = LinkedHashSet.of(1, 2, 3);
        final Set<Integer> actual = set.replace(2, 2);
        assertThat(actual).isSameAs(set);
    }

    // -- transform
    @Test
    public void shouldTransform() {
        final String transformed = of(42).transform(( v) -> String.valueOf(v.get()));
        assertThat(transformed).isEqualTo("42");
    }

    // -- toLinkedSet
    @Test
    public void shouldReturnSelfOnConvertToLinkedSet() {
        final Value<Integer> value = of(1, 2, 3);
        assertThat(value.toLinkedSet()).isSameAs(value);
    }

    // -- spliterator
    @Test
    public void shouldNotHaveSortedSpliterator() {
        assertThat(of(1, 2, 3).spliterator().hasCharacteristics(Spliterator.SORTED)).isFalse();
    }

    @Test
    public void shouldHaveOrderedSpliterator() {
        assertThat(of(1, 2, 3).spliterator().hasCharacteristics(Spliterator.ORDERED)).isTrue();
    }

    // -- isSequential()
    @Test
    public void shouldReturnTrueWhenIsSequentialCalled() {
        assertThat(of(1, 2, 3).isSequential()).isTrue();
    }
}

