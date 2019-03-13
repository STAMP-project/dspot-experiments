package io.vavr.collection;


import java.math.BigDecimal;
import java.util.Spliterator;
import org.junit.Test;


public class HashMultimapTest extends AbstractMultimapTest {
    // -- static narrow
    @Test
    public void shouldNarrowMap() {
        final HashMultimap<Integer, Number> int2doubleMap = ((HashMultimap<Integer, Number>) (this.<Integer, Number>emptyMap().put(1, 1.0)));
        final HashMultimap<Number, Number> number2numberMap = HashMultimap.narrow(int2doubleMap);
        final int actual = number2numberMap.put(new BigDecimal("2"), new BigDecimal("2.0")).values().sum().intValue();
        assertThat(actual).isEqualTo(3);
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
    public void shouldReturnTrueWhenIsSequentialCalled() {
        assertThat(of(1, 2, 3).isSequential()).isFalse();
    }
}

