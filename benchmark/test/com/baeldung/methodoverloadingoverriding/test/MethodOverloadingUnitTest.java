package com.baeldung.methodoverloadingoverriding.test;


import com.baeldung.methodoverloadingoverriding.util.Multiplier;
import org.junit.Test;


public class MethodOverloadingUnitTest {
    private static Multiplier multiplier;

    @Test
    public void givenMultiplierInstance_whenCalledMultiplyWithTwoIntegers_thenOneAssertion() {
        assertThat(MethodOverloadingUnitTest.multiplier.multiply(10, 10)).isEqualTo(100);
    }

    @Test
    public void givenMultiplierInstance_whenCalledMultiplyWithTreeIntegers_thenOneAssertion() {
        assertThat(MethodOverloadingUnitTest.multiplier.multiply(10, 10, 10)).isEqualTo(1000);
    }

    @Test
    public void givenMultiplierInstance_whenCalledMultiplyWithIntDouble_thenOneAssertion() {
        assertThat(MethodOverloadingUnitTest.multiplier.multiply(10, 10.5)).isEqualTo(105.0);
    }

    @Test
    public void givenMultiplierInstance_whenCalledMultiplyWithDoubleDouble_thenOneAssertion() {
        assertThat(MethodOverloadingUnitTest.multiplier.multiply(10.5, 10.5)).isEqualTo(110.25);
    }

    @Test
    public void givenMultiplierInstance_whenCalledMultiplyWithIntIntAndMatching_thenNoTypePromotion() {
        assertThat(MethodOverloadingUnitTest.multiplier.multiply(10, 10)).isEqualTo(100);
    }
}

