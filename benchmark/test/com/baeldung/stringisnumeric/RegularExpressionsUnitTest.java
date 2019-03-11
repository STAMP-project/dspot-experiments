package com.baeldung.stringisnumeric;


import org.junit.Test;


public class RegularExpressionsUnitTest {
    @Test
    public void whenUsingRegularExpressions_thenTrue() {
        // Valid Numbers
        assertThat(RegularExpressionsUnitTest.isNumeric("22")).isTrue();
        assertThat(RegularExpressionsUnitTest.isNumeric("5.05")).isTrue();
        assertThat(RegularExpressionsUnitTest.isNumeric("-200")).isTrue();
        // Invalid Numbers
        assertThat(RegularExpressionsUnitTest.isNumeric("abc")).isFalse();
    }
}

