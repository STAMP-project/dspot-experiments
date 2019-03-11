package com.baeldung.commons.lang3.test;


import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;


public class ImmutablePairUnitTest {
    private static ImmutablePair<String, String> immutablePair;

    @Test
    public void givenImmutablePairInstance_whenCalledgetLeft_thenCorrect() {
        assertThat(ImmutablePairUnitTest.immutablePair.getLeft()).isEqualTo("leftElement");
    }

    @Test
    public void givenImmutablePairInstance_whenCalledgetRight_thenCorrect() {
        assertThat(ImmutablePairUnitTest.immutablePair.getRight()).isEqualTo("rightElement");
    }

    @Test
    public void givenImmutablePairInstance_whenCalledof_thenCorrect() {
        assertThat(ImmutablePair.of("leftElement", "rightElement")).isInstanceOf(ImmutablePair.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void givenImmutablePairInstance_whenCalledSetValue_thenThrowUnsupportedOperationException() {
        ImmutablePairUnitTest.immutablePair.setValue("newValue");
    }
}

