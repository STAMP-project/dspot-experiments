package io.vavr.collection;


import java.util.Comparator;
import org.junit.Test;


public class ComparatorsTest {
    // -- naturalComparator()
    @Test
    public void shouldCompareTwoIntegersUsingNaturalOrder() {
        final Comparator<Integer> comparator = Comparators.naturalComparator();
        assertThat(comparator.compare(0, 1)).isEqualTo((-1));
        assertThat(comparator.compare(2, (-1))).isEqualTo(1);
        assertThat(comparator.compare(3, 3)).isEqualTo(0);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEWhenComparingNullAndIntegerUsingNaturalOrder() {
        Comparators.naturalComparator().compare(null, 1);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEWhenComparingIntegerAndNullUsingNaturalOrder() {
        Comparators.naturalComparator().compare(1, null);
    }
}

