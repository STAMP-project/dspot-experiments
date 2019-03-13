package io.vavr.collection;


import java.math.BigDecimal;
import java.util.Spliterator;
import org.junit.Test;


public class TreeMultimapTest extends AbstractMultimapTest {
    // -- static narrow
    @Test
    public void shouldNarrowMap() {
        final TreeMultimap<Integer, Number> int2doubleMap = ((TreeMultimap<Integer, Number>) (this.<Integer, Number>emptyMap().put(1, 1.0)));
        final TreeMultimap<Number, Number> number2numberMap = TreeMultimap.narrow(int2doubleMap);
        final int actual = number2numberMap.put(2, new BigDecimal("2.0")).values().sum().intValue();
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

