package com.baeldung.junit;


import java.util.Arrays;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test that demonstrate the different assertions available within JUnit 4
 */
public class AssertionsUnitTest {
    @Test
    public void whenAssertingEquality_thenEqual() {
        String expected = "Baeldung";
        String actual = "Baeldung";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void whenAssertingEqualityWithMessage_thenEqual() {
        String expected = "Baeldung";
        String actual = "Baeldung";
        Assert.assertEquals("failure - strings are not equal", expected, actual);
    }

    @Test
    public void whenAssertingArraysEquality_thenEqual() {
        char[] expected = new char[]{ 'J', 'u', 'n', 'i', 't' };
        char[] actual = "Junit".toCharArray();
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void givenNullArrays_whenAssertingArraysEquality_thenEqual() {
        int[] expected = null;
        int[] actual = null;
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void whenAssertingNull_thenTrue() {
        Object car = null;
        Assert.assertNull("The car should be null", car);
    }

    @Test
    public void whenAssertingNotNull_thenTrue() {
        Object car = new Object();
        Assert.assertNotNull("The car should not be null", car);
    }

    @Test
    public void whenAssertingNotSameObject_thenDifferent() {
        Object cat = new Object();
        Object dog = new Object();
        Assert.assertNotSame(cat, dog);
    }

    @Test
    public void whenAssertingSameObject_thenSame() {
        Object cat = new Object();
        Assert.assertSame(cat, cat);
    }

    @Test
    public void whenAssertingConditions_thenVerified() {
        Assert.assertTrue("5 is greater then 4", (5 > 4));
        Assert.assertFalse("5 is not greater then 6", (5 > 6));
    }

    @Test
    public void when_thenNotFailed() {
        try {
            methodThatShouldThrowException();
            Assert.fail("Exception not thrown");
        } catch (UnsupportedOperationException e) {
            Assert.assertEquals("Operation Not Supported", e.getMessage());
        }
    }

    @Test
    public void testAssertThatHasItems() {
        Assert.assertThat(Arrays.asList("Java", "Kotlin", "Scala"), IsCollectionContaining.hasItems("Java", "Kotlin"));
    }
}

