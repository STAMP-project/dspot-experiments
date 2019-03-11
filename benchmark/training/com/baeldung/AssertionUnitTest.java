package com.baeldung;


import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit test that demonstrate the different assertions available within JUnit 4
 */
@DisplayName("Test case for assertions")
public class AssertionUnitTest {
    @Test
    @DisplayName("Arrays should be equals")
    public void whenAssertingArraysEquality_thenEqual() {
        char[] expected = new char[]{ 'J', 'u', 'p', 'i', 't', 'e', 'r' };
        char[] actual = "Jupiter".toCharArray();
        Assertions.assertArrayEquals(expected, actual, "Arrays should be equal");
    }

    @Test
    @DisplayName("The area of two polygons should be equal")
    public void whenAssertingEquality_thenEqual() {
        float square = 2 * 2;
        float rectangle = 2 * 2;
        Assertions.assertEquals(square, rectangle);
    }

    @Test
    public void whenAssertingEqualityWithDelta_thenEqual() {
        float square = 2 * 2;
        float rectangle = 3 * 2;
        float delta = 2;
        Assertions.assertEquals(square, rectangle, delta);
    }

    @Test
    public void whenAssertingConditions_thenVerified() {
        Assertions.assertTrue((5 > 4), "5 is greater the 4");
        Assertions.assertTrue((null == null), "null is equal to null");
    }

    @Test
    public void whenAssertingNull_thenTrue() {
        Object cat = null;
        Assertions.assertNull(cat, () -> "The cat should be null");
    }

    @Test
    public void whenAssertingNotNull_thenTrue() {
        Object dog = new Object();
        Assertions.assertNotNull(dog, () -> "The dog should not be null");
    }

    @Test
    public void whenAssertingSameObject_thenSuccessfull() {
        String language = "Java";
        Optional<String> optional = Optional.of(language);
        Assertions.assertSame(language, optional.get());
    }

    @Test
    public void givenBooleanSupplier_whenAssertingCondition_thenVerified() {
        BooleanSupplier condition = () -> 5 > 6;
        Assertions.assertFalse(condition, "5 is not greater then 6");
    }

    @Test
    public void givenMultipleAssertion_whenAssertingAll_thenOK() {
        Assertions.assertAll("heading", () -> Assertions.assertEquals(4, (2 * 2), "4 is 2 times 2"), () -> Assertions.assertEquals("java", "JAVA".toLowerCase()), () -> Assertions.assertEquals(null, null, "null is equal to null"));
    }

    @Test
    public void givenTwoLists_whenAssertingIterables_thenEquals() {
        Iterable<String> al = new ArrayList<>(Arrays.asList("Java", "Junit", "Test"));
        Iterable<String> ll = new LinkedList<>(Arrays.asList("Java", "Junit", "Test"));
        Assertions.assertIterableEquals(al, ll);
    }

    @Test
    public void whenAssertingTimeout_thenNotExceeded() {
        Assertions.assertTimeout(Duration.ofSeconds(2), () -> {
            // code that requires less then 2 minutes to execute
            Thread.sleep(1000);
        });
    }

    @Test
    public void whenAssertingTimeoutPreemptively_thenNotExceeded() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
            // code that requires less then 2 minutes to execute
            Thread.sleep(1000);
        });
    }

    @Test
    public void whenAssertingEquality_thenNotEqual() {
        Integer value = 5;// result of an algorithm

        Assertions.assertNotEquals(0, value, "The result cannot be 0");
    }

    @Test
    public void whenAssertingEqualityListOfStrings_thenEqual() {
        List<String> expected = Arrays.asList("Java", "\\d+", "JUnit");
        List<String> actual = Arrays.asList("Java", "11", "JUnit");
        Assertions.assertLinesMatch(expected, actual);
    }

    @Test
    public void testConvertToDoubleThrowException() {
        String age = "eighteen";
        Assertions.assertThrows(NumberFormatException.class, () -> {
            AssertionUnitTest.convertToInt(age);
        });
        Assertions.assertThrows(NumberFormatException.class, () -> {
            AssertionUnitTest.convertToInt(age);
        });
    }
}

