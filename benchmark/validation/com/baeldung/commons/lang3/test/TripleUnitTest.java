package com.baeldung.commons.lang3.test;


import org.apache.commons.lang3.tuple.Triple;
import org.junit.Test;


public class TripleUnitTest {
    private static Triple<String, String, String> triple;

    @Test
    public void givenTripleInstance_whenCalledgetLeft_thenCorrect() {
        assertThat(TripleUnitTest.triple.getLeft()).isEqualTo("leftElement");
    }

    @Test
    public void givenTripleInstance_whenCalledgetMiddle_thenCorrect() {
        assertThat(TripleUnitTest.triple.getMiddle()).isEqualTo("middleElement");
    }

    @Test
    public void givenTripleInstance_whenCalledgetRight_thenCorrect() {
        assertThat(TripleUnitTest.triple.getRight()).isEqualTo("rightElement");
    }
}

