package com.baeldung.commons.lang3.test;


import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.junit.Test;


public class ImmutableTripleUnitTest {
    private static ImmutableTriple<String, String, String> immutableTriple;

    @Test
    public void givenImmutableTripleInstance_whenCalledgetLeft_thenCorrect() {
        assertThat(ImmutableTripleUnitTest.immutableTriple.getLeft()).isEqualTo("leftElement");
    }

    @Test
    public void givenImmutableTripleInstance_whenCalledgetMiddle_thenCorrect() {
        assertThat(ImmutableTripleUnitTest.immutableTriple.getMiddle()).isEqualTo("middleElement");
    }

    @Test
    public void givenImmutableInstance_whenCalledgetRight_thenCorrect() {
        assertThat(ImmutableTripleUnitTest.immutableTriple.getRight()).isEqualTo("rightElement");
    }
}

