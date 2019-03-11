package com.baeldung.commons.lang3.test;


import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.Test;


public class MutablePairUnitTest {
    private static MutablePair<String, String> mutablePair;

    @Test
    public void givenMutablePairInstance_whenCalledgetLeft_thenCorrect() {
        assertThat(MutablePairUnitTest.mutablePair.getLeft()).isEqualTo("leftElement");
    }

    @Test
    public void givenMutablePairInstance_whenCalledgetRight_thenCorrect() {
        assertThat(MutablePairUnitTest.mutablePair.getRight()).isEqualTo("rightElement");
    }

    @Test
    public void givenMutablePairInstance_whenCalledsetLeft_thenCorrect() {
        MutablePairUnitTest.mutablePair.setLeft("newLeftElement");
        assertThat(MutablePairUnitTest.mutablePair.getLeft()).isEqualTo("newLeftElement");
    }
}

