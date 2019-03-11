package com.baeldung.iterators;


import java.util.ConcurrentModificationException;
import org.junit.Test;


/**
 * Source code https://github.com/eugenp/tutorials
 *
 * @author Santosh Thakur
 */
public class IteratorsUnitTest {
    @Test
    public void whenFailFast_ThenThrowsException() {
        assertThatThrownBy(() -> {
            failFast1();
        }).isInstanceOf(ConcurrentModificationException.class);
    }

    @Test
    public void whenFailFast_ThenThrowsExceptionInSecondIteration() {
        assertThatThrownBy(() -> {
            failFast2();
        }).isInstanceOf(ConcurrentModificationException.class);
    }

    @Test
    public void whenFailSafe_ThenDoesNotThrowException() {
        assertThat(Iterators.failSafe1()).isGreaterThanOrEqualTo(0);
    }
}

