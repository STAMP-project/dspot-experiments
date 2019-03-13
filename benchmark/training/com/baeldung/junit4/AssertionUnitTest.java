package com.baeldung.junit4;


import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("Test case for assertions")
public class AssertionUnitTest {
    @Test
    @DisplayName("Arrays should be equals")
    public void whenAssertingArraysEquality_thenEqual() {
        char[] expected = new char[]{ 'J', 'u', 'p', 'i', 't', 'e', 'r' };
        char[] actual = "Jupiter".toCharArray();
        Assert.assertArrayEquals("Arrays should be equal", expected, actual);
    }

    @Test
    public void givenMultipleAssertion_whenAssertingAll_thenOK() {
        Assert.assertEquals("4 is 2 times 2", 4, (2 * 2));
        Assert.assertEquals("java", "JAVA".toLowerCase());
        Assert.assertEquals("null is equal to null", null, null);
    }

    @Test
    public void testAssertThatHasItems() {
        Assert.assertThat(Arrays.asList("Java", "Kotlin", "Scala"), CoreMatchers.hasItems("Java", "Kotlin"));
    }
}

