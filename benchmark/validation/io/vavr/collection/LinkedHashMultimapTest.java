package io.vavr.collection;


import java.math.BigDecimal;
import java.util.Spliterator;
import org.junit.Test;


public class LinkedHashMultimapTest extends AbstractMultimapTest {
    // -- narrow
    @Test
    public void shouldNarrowMap() {
        final LinkedHashMultimap<Integer, Number> int2doubleMap = ((LinkedHashMultimap<Integer, Number>) (this.<Integer, Number>emptyMap().put(1, 1.0)));
        final LinkedHashMultimap<Number, Number> number2numberMap = LinkedHashMultimap.narrow(int2doubleMap);
        final int actual = number2numberMap.put(new BigDecimal("2"), new BigDecimal("2.0")).values().sum().intValue();
        assertThat(actual).isEqualTo(3);
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
        final Multimap<Integer, Integer> map = LinkedHashMultimap.withSeq().of(1, 2, 3, 4);
        assertThat(map.isSequential()).isTrue();
    }
}

