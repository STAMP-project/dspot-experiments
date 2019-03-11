package com.baeldung.stringisnumeric;


import org.junit.Test;


public class CoreJavaIsNumericUnitTest {
    @Test
    public void whenUsingCoreJava_thenTrue() {
        // Valid Numbers
        assertThat(CoreJavaIsNumericUnitTest.isNumeric("22")).isTrue();
        assertThat(CoreJavaIsNumericUnitTest.isNumeric("5.05")).isTrue();
        assertThat(CoreJavaIsNumericUnitTest.isNumeric("-200")).isTrue();
        assertThat(CoreJavaIsNumericUnitTest.isNumeric("10.0d")).isTrue();
        assertThat(CoreJavaIsNumericUnitTest.isNumeric("   22   ")).isTrue();
        // Invalid Numbers
        assertThat(CoreJavaIsNumericUnitTest.isNumeric(null)).isFalse();
        assertThat(CoreJavaIsNumericUnitTest.isNumeric("")).isFalse();
        assertThat(CoreJavaIsNumericUnitTest.isNumeric("abc")).isFalse();
    }
}

