package com.baeldung.string.interview;


import org.junit.Test;


public class StringToIntegerUnitTest {
    @Test
    public void givenString_whenParsingInt_shouldConvertToInt() {
        String givenString = "42";
        int result = Integer.parseInt(givenString);
        assertThat(result).isEqualTo(42);
    }
}

