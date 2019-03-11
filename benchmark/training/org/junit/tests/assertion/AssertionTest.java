package org.junit.tests.assertion;


import java.io.IOException;
import java.math.BigDecimal;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Test;


/**
 * Tests for {@link org.junit.Assert}
 */
public class AssertionTest {
    // If you want to use 1.4 assertions, they will be reported correctly.
    // However, you need to add the -ea VM argument when running.
    // @Test (expected=AssertionError.class) public void error() {
    // assert false;
    // }
    private static final String ASSERTION_ERROR_EXPECTED = "AssertionError expected";

    @Test(expected = AssertionError.class)
    public void fails() {
        Assert.fail();
    }

    @Test
    public void failWithNoMessageToString() {
        try {
            Assert.fail();
        } catch (AssertionError exception) {
            Assert.assertEquals("java.lang.AssertionError", exception.toString());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void failWithMessageToString() {
        try {
            Assert.fail("woops!");
        } catch (AssertionError exception) {
            Assert.assertEquals("java.lang.AssertionError: woops!", exception.toString());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void arraysNotEqual() {
        assertArrayEqualsFailure(new Object[]{ "right" }, new Object[]{ "wrong" }, "arrays first differed at element [0]; expected:<[right]> but was:<[wrong]>");
    }

    @Test
    public void arraysNotEqualWithMessage() {
        assertArrayEqualsFailure("not equal", new Object[]{ "right" }, new Object[]{ "wrong" }, "not equal: arrays first differed at element [0]; expected:<[right]> but was:<[wrong]>");
    }

    @Test
    public void arraysExpectedNullMessage() {
        try {
            Assert.assertArrayEquals("not equal", null, new Object[]{ new Object() });
        } catch (AssertionError exception) {
            Assert.assertEquals("not equal: expected array was null", exception.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void arraysActualNullMessage() {
        try {
            Assert.assertArrayEquals("not equal", new Object[]{ new Object() }, null);
        } catch (AssertionError exception) {
            Assert.assertEquals("not equal: actual array was null", exception.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void arraysDifferentLengthDifferingAtStartMessage() {
        assertArrayEqualsFailure("not equal", new Object[]{ true }, new Object[]{ false, true }, "not equal: array lengths differed, expected.length=1 actual.length=2; arrays first differed at element [0]; expected:<true> but was:<false>");
    }

    @Test
    public void arraysDifferentLengthDifferingAtEndMessage() {
        assertArrayEqualsFailure("not equal", new Object[]{ true }, new Object[]{ true, false }, "not equal: array lengths differed, expected.length=1 actual.length=2; arrays first differed at element [1]; expected:<end of array> but was:<false>");
    }

    @Test
    public void arraysDifferentLengthDifferingAtEndAndExpectedArrayLongerMessage() {
        assertArrayEqualsFailure("not equal", new Object[]{ true, false }, new Object[]{ true }, "not equal: array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1]; expected:<false> but was:<end of array>");
    }

    @Test
    public void arraysElementsDiffer() {
        assertArrayEqualsFailure("not equal", new Object[]{ "this is a very long string in the middle of an array" }, new Object[]{ "this is another very long string in the middle of an array" }, "not equal: arrays first differed at element [0]; expected:<this is a[] very long string in...> but was:<this is a[nother] very long string in...>");
    }

    @Test
    public void arraysDifferAtElement0nullMessage() {
        assertArrayEqualsFailure(new Object[]{ true }, new Object[]{ false }, "arrays first differed at element [0]; expected:<true> but was:<false>");
    }

    @Test
    public void arraysDifferAtElement1nullMessage() {
        assertArrayEqualsFailure(new Object[]{ true, true }, new Object[]{ true, false }, "arrays first differed at element [1]; expected:<true> but was:<false>");
    }

    @Test
    public void arraysDifferAtElement0withMessage() {
        assertArrayEqualsFailure("message", new Object[]{ true }, new Object[]{ false }, "message: arrays first differed at element [0]; expected:<true> but was:<false>");
    }

    @Test
    public void arraysDifferAtElement1withMessage() {
        assertArrayEqualsFailure("message", new Object[]{ true, true }, new Object[]{ true, false }, "message: arrays first differed at element [1]; expected:<true> but was:<false>");
    }

    @Test
    public void multiDimensionalArraysAreEqual() {
        Assert.assertArrayEquals(new Object[][]{ new Object[]{ true, true }, new Object[]{ false, false } }, new Object[][]{ new Object[]{ true, true }, new Object[]{ false, false } });
    }

    @Test
    public void multiDimensionalIntArraysAreEqual() {
        int[][] int1 = new int[][]{ new int[]{ 1, 2, 3 }, new int[]{ 4, 5, 6 } };
        int[][] int2 = new int[][]{ new int[]{ 1, 2, 3 }, new int[]{ 4, 5, 6 } };
        Assert.assertArrayEquals(int1, int2);
    }

    @Test
    public void oneDimensionalPrimitiveArraysAreEqual() {
        Assert.assertArrayEquals(new boolean[]{ true }, new boolean[]{ true });
        Assert.assertArrayEquals(new byte[]{ 1 }, new byte[]{ 1 });
        Assert.assertArrayEquals(new char[]{ 1 }, new char[]{ 1 });
        Assert.assertArrayEquals(new short[]{ 1 }, new short[]{ 1 });
        Assert.assertArrayEquals(new int[]{ 1 }, new int[]{ 1 });
        Assert.assertArrayEquals(new long[]{ 1 }, new long[]{ 1 });
        Assert.assertArrayEquals(new double[]{ 1.0 }, new double[]{ 1.0 }, 1.0);
        Assert.assertArrayEquals(new float[]{ 1.0F }, new float[]{ 1.0F }, 1.0F);
    }

    @Test(expected = AssertionError.class)
    public void oneDimensionalDoubleArraysAreNotEqual() {
        Assert.assertArrayEquals(new double[]{ 1.0 }, new double[]{ 2.5 }, 1.0);
    }

    @Test(expected = AssertionError.class)
    public void oneDimensionalFloatArraysAreNotEqual() {
        Assert.assertArrayEquals(new float[]{ 1.0F }, new float[]{ 2.5F }, 1.0F);
    }

    @Test(expected = AssertionError.class)
    public void oneDimensionalBooleanArraysAreNotEqual() {
        Assert.assertArrayEquals(new boolean[]{ true }, new boolean[]{ false });
    }

    @Test(expected = AssertionError.class)
    public void IntegerDoesNotEqualLong() {
        Assert.assertEquals(new Integer(1), new Long(1));
    }

    @Test
    public void intsEqualLongs() {
        Assert.assertEquals(1, 1L);
    }

    @Test
    public void multiDimensionalArraysDeclaredAsOneDimensionalAreEqual() {
        Assert.assertArrayEquals(new Object[]{ new Object[]{ true, true }, new Object[]{ false, false } }, new Object[]{ new Object[]{ true, true }, new Object[]{ false, false } });
    }

    @Test
    public void multiDimensionalArraysAreNotEqual() {
        assertArrayEqualsFailure("message", new Object[][]{ new Object[]{ true, true }, new Object[]{ false, false } }, new Object[][]{ new Object[]{ true, true }, new Object[]{ true, false } }, "message: arrays first differed at element [1][0]; expected:<false> but was:<true>");
    }

    @Test
    public void multiDimensionalArraysAreNotEqualNoMessage() {
        assertArrayEqualsFailure(new Object[][]{ new Object[]{ true, true }, new Object[]{ false, false } }, new Object[][]{ new Object[]{ true, true }, new Object[]{ true, false } }, "arrays first differed at element [1][0]; expected:<false> but was:<true>");
    }

    @Test
    public void twoDimensionalArraysDifferentOuterLengthNotEqual() {
        assertArrayEqualsFailure("not equal", new Object[][]{ new Object[]{ true }, new Object[]{  } }, new Object[][]{ new Object[]{  } }, "not equal: array lengths differed, expected.length=1 actual.length=0; arrays first differed at element [0][0]; expected:<true> but was:<end of array>");
        assertArrayEqualsFailure("not equal", new Object[][]{ new Object[]{  }, new Object[]{ true } }, new Object[][]{ new Object[]{  } }, "not equal: array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1]; expected:<java.lang.Object[1]> but was:<end of array>");
        assertArrayEqualsFailure("not equal", new Object[][]{ new Object[]{  } }, new Object[][]{ new Object[]{ true }, new Object[]{  } }, "not equal: array lengths differed, expected.length=0 actual.length=1; arrays first differed at element [0][0]; expected:<end of array> but was:<true>");
        assertArrayEqualsFailure("not equal", new Object[][]{ new Object[]{  } }, new Object[][]{ new Object[]{  }, new Object[]{ true } }, "not equal: array lengths differed, expected.length=1 actual.length=2; arrays first differed at element [1]; expected:<end of array> but was:<java.lang.Object[1]>");
    }

    @Test
    public void primitiveArraysConvertedToStringCorrectly() {
        assertArrayEqualsFailure("not equal", new boolean[][]{ new boolean[]{  }, new boolean[]{ true } }, new boolean[][]{ new boolean[]{  } }, "not equal: array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1]; expected:<boolean[1]> but was:<end of array>");
        assertArrayEqualsFailure("not equal", new int[][]{ new int[]{  }, new int[]{ 23 } }, new int[][]{ new int[]{  } }, "not equal: array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1]; expected:<int[1]> but was:<end of array>");
    }

    @Test
    public void twoDimensionalArraysConvertedToStringCorrectly() {
        assertArrayEqualsFailure("not equal", new Object[][][]{ new Object[][]{  }, new Object[][]{ new Object[]{ true } } }, new Object[][][]{ new Object[][]{  } }, "not equal: array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1]; expected:<java.lang.Object[][1]> but was:<end of array>");
    }

    @Test
    public void twoDimensionalArraysDifferentInnerLengthNotEqual() {
        assertArrayEqualsFailure("not equal", new Object[][]{ new Object[]{ true }, new Object[]{  } }, new Object[][]{ new Object[]{  }, new Object[]{  } }, "not equal: array lengths differed, expected.length=1 actual.length=0; arrays first differed at element [0][0]; expected:<true> but was:<end of array>");
        assertArrayEqualsFailure("not equal", new Object[][]{ new Object[]{  }, new Object[]{ true } }, new Object[][]{ new Object[]{  }, new Object[]{  } }, "not equal: array lengths differed, expected.length=1 actual.length=0; arrays first differed at element [1][0]; expected:<true> but was:<end of array>");
        assertArrayEqualsFailure("not equal", new Object[][]{ new Object[]{  }, new Object[]{  } }, new Object[][]{ new Object[]{ true }, new Object[]{  } }, "not equal: array lengths differed, expected.length=0 actual.length=1; arrays first differed at element [0][0]; expected:<end of array> but was:<true>");
        assertArrayEqualsFailure("not equal", new Object[][]{ new Object[]{  }, new Object[]{  } }, new Object[][]{ new Object[]{  }, new Object[]{ true } }, "not equal: array lengths differed, expected.length=0 actual.length=1; arrays first differed at element [1][0]; expected:<end of array> but was:<true>");
    }

    @Test
    public void multiDimensionalArraysDifferentLengthMessage() {
        try {
            Assert.assertArrayEquals("message", new Object[][]{ new Object[]{ true, true }, new Object[]{ false, false } }, new Object[][]{ new Object[]{ true, true }, new Object[]{ false } });
        } catch (AssertionError exception) {
            Assert.assertEquals("message: array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1][1]; expected:<false> but was:<end of array>", exception.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void multiDimensionalArraysDifferentLengthNoMessage() {
        try {
            Assert.assertArrayEquals(new Object[][]{ new Object[]{ true, true }, new Object[]{ false, false } }, new Object[][]{ new Object[]{ true, true }, new Object[]{ false } });
        } catch (AssertionError exception) {
            Assert.assertEquals("array lengths differed, expected.length=2 actual.length=1; arrays first differed at element [1][1]; expected:<false> but was:<end of array>", exception.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void arraysWithNullElementEqual() {
        Object[] objects1 = new Object[]{ null };
        Object[] objects2 = new Object[]{ null };
        Assert.assertArrayEquals(objects1, objects2);
    }

    @Test
    public void stringsDifferWithUserMessage() {
        try {
            Assert.assertEquals("not equal", "one", "two");
        } catch (ComparisonFailure exception) {
            Assert.assertEquals("not equal expected:<[one]> but was:<[two]>", exception.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void arraysEqual() {
        Object element = new Object();
        Object[] objects1 = new Object[]{ element };
        Object[] objects2 = new Object[]{ element };
        Assert.assertArrayEquals(objects1, objects2);
    }

    @Test
    public void arraysEqualWithMessage() {
        Object element = new Object();
        Object[] objects1 = new Object[]{ element };
        Object[] objects2 = new Object[]{ element };
        Assert.assertArrayEquals("equal", objects1, objects2);
    }

    @Test
    public void equals() {
        Object o = new Object();
        Assert.assertEquals(o, o);
        Assert.assertEquals("abc", "abc");
        Assert.assertEquals(true, true);
        Assert.assertEquals(((byte) (1)), ((byte) (1)));
        Assert.assertEquals('a', 'a');
        Assert.assertEquals(((short) (1)), ((short) (1)));
        Assert.assertEquals(1, 1);// int by default, cast is unnecessary

        Assert.assertEquals(1L, 1L);
        Assert.assertEquals(1.0, 1.0, 0.0);
        Assert.assertEquals(1.0, 1.0, 0.0);
    }

    @Test(expected = AssertionError.class)
    public void notEqualsObjectWithNull() {
        Assert.assertEquals(new Object(), null);
    }

    @Test(expected = AssertionError.class)
    public void notEqualsNullWithObject() {
        Assert.assertEquals(null, new Object());
    }

    @Test
    public void notEqualsObjectWithNullWithMessage() {
        Object o = new Object();
        try {
            Assert.assertEquals("message", null, o);
        } catch (AssertionError e) {
            Assert.assertEquals((("message expected:<null> but was:<" + (o.toString())) + ">"), e.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void notEqualsNullWithObjectWithMessage() {
        Object o = new Object();
        try {
            Assert.assertEquals("message", o, null);
        } catch (AssertionError e) {
            Assert.assertEquals((("message expected:<" + (o.toString())) + "> but was:<null>"), e.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test(expected = AssertionError.class)
    public void objectsNotEquals() {
        Assert.assertEquals(new Object(), new Object());
    }

    @Test(expected = ComparisonFailure.class)
    public void stringsNotEqual() {
        Assert.assertEquals("abc", "def");
    }

    @Test(expected = AssertionError.class)
    public void booleansNotEqual() {
        Assert.assertEquals(true, false);
    }

    @Test(expected = AssertionError.class)
    public void bytesNotEqual() {
        Assert.assertEquals(((byte) (1)), ((byte) (2)));
    }

    @Test(expected = AssertionError.class)
    public void charsNotEqual() {
        Assert.assertEquals('a', 'b');
    }

    @Test(expected = AssertionError.class)
    public void shortsNotEqual() {
        Assert.assertEquals(((short) (1)), ((short) (2)));
    }

    @Test(expected = AssertionError.class)
    public void intsNotEqual() {
        Assert.assertEquals(1, 2);
    }

    @Test(expected = AssertionError.class)
    public void longsNotEqual() {
        Assert.assertEquals(1L, 2L);
    }

    @Test(expected = AssertionError.class)
    public void floatsNotEqual() {
        Assert.assertEquals(1.0, 2.0, 0.9);
    }

    @SuppressWarnings("deprecation")
    @Test(expected = AssertionError.class)
    public void floatsNotEqualWithoutDelta() {
        Assert.assertEquals(1.0, 1.1);
    }

    @Test
    public void floatsNotDoublesInArrays() {
        float delta = 4.444F;
        float[] f1 = new float[]{ 1.111F };
        float[] f2 = new float[]{ 5.555F };
        Assert.assertArrayEquals(f1, f2, delta);
    }

    @Test(expected = AssertionError.class)
    public void bigDecimalsNotEqual() {
        Assert.assertEquals(new BigDecimal("123.4"), new BigDecimal("123.0"));
    }

    @Test(expected = AssertionError.class)
    public void doublesNotEqual() {
        Assert.assertEquals(1.0, 2.0, 0.9);
    }

    @Test
    public void naNsAreEqual() {
        Assert.assertEquals(Float.NaN, Float.NaN, Float.POSITIVE_INFINITY);
        Assert.assertEquals(Double.NaN, Double.NaN, Double.POSITIVE_INFINITY);
    }

    @SuppressWarnings("unused")
    @Test
    public void nullNullmessage() {
        try {
            Assert.assertNull("junit");
        } catch (AssertionError e) {
            Assert.assertEquals("expected null, but was:<junit>", e.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @SuppressWarnings("unused")
    @Test
    public void nullWithMessage() {
        try {
            Assert.assertNull("message", "hello");
        } catch (AssertionError exception) {
            Assert.assertEquals("message expected null, but was:<hello>", exception.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void same() {
        Object o1 = new Object();
        Assert.assertSame(o1, o1);
    }

    @Test
    public void notSame() {
        Object o1 = new Object();
        Object o2 = new Object();
        Assert.assertNotSame(o1, o2);
    }

    @Test(expected = AssertionError.class)
    public void objectsNotSame() {
        Assert.assertSame(new Object(), new Object());
    }

    @Test(expected = AssertionError.class)
    public void objectsAreSame() {
        Object o = new Object();
        Assert.assertNotSame(o, o);
    }

    @Test
    public void sameWithMessage() {
        try {
            Assert.assertSame("not same", "hello", "good-bye");
        } catch (AssertionError exception) {
            Assert.assertEquals("not same expected same:<hello> was not:<good-bye>", exception.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void sameNullMessage() {
        try {
            Assert.assertSame("hello", "good-bye");
        } catch (AssertionError exception) {
            Assert.assertEquals("expected same:<hello> was not:<good-bye>", exception.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void notSameWithMessage() {
        Object o = new Object();
        try {
            Assert.assertNotSame("message", o, o);
        } catch (AssertionError exception) {
            Assert.assertEquals("message expected not same", exception.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void notSameNullMessage() {
        Object o = new Object();
        try {
            Assert.assertNotSame(o, o);
        } catch (AssertionError exception) {
            Assert.assertEquals("expected not same", exception.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void nullMessage() {
        try {
            Assert.fail(null);
        } catch (AssertionError exception) {
            // we used to expect getMessage() to return ""; see failWithNoMessageToString()
            Assert.assertNull(exception.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void nullMessageDisappearsWithStringAssertEquals() {
        try {
            Assert.assertEquals(null, "a", "b");
        } catch (ComparisonFailure e) {
            Assert.assertEquals("expected:<[a]> but was:<[b]>", e.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void nullMessageDisappearsWithAssertEquals() {
        try {
            Assert.assertEquals(null, 1, 2);
        } catch (AssertionError e) {
            Assert.assertEquals("expected:<1> but was:<2>", e.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test(expected = AssertionError.class)
    public void arraysDeclaredAsObjectAreComparedAsObjects() {
        Object a1 = new Object[]{ "abc" };
        Object a2 = new Object[]{ "abc" };
        Assert.assertEquals(a1, a2);
    }

    @Test
    public void implicitTypecastEquality() {
        byte b = 1;
        short s = 1;
        int i = 1;
        long l = 1L;
        float f = 1.0F;
        double d = 1.0;
        Assert.assertEquals(b, s);
        Assert.assertEquals(b, i);
        Assert.assertEquals(b, l);
        Assert.assertEquals(s, i);
        Assert.assertEquals(s, l);
        Assert.assertEquals(i, l);
        Assert.assertEquals(f, d, 0);
    }

    @Test
    public void errorMessageDistinguishesDifferentValuesWithSameToString() {
        try {
            Assert.assertEquals("4", new Integer(4));
        } catch (AssertionError e) {
            Assert.assertEquals("expected: java.lang.String<4> but was: java.lang.Integer<4>", e.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThatIncludesDescriptionOfTestedValueInErrorMessage() {
        String expected = "expected";
        String actual = "actual";
        String expectedMessage = "identifier\nExpected: \"expected\"\n     but: was \"actual\"";
        try {
            Assert.assertThat("identifier", actual, CoreMatchers.equalTo(expected));
        } catch (AssertionError e) {
            Assert.assertEquals(expectedMessage, e.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThatIncludesAdvancedMismatch() {
        String expectedMessage = "identifier\nExpected: is an instance of java.lang.Integer\n     but: \"actual\" is a java.lang.String";
        try {
            Assert.assertThat("identifier", "actual", CoreMatchers.is(CoreMatchers.instanceOf(Integer.class)));
        } catch (AssertionError e) {
            Assert.assertEquals(expectedMessage, e.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThatDescriptionCanBeElided() {
        String expected = "expected";
        String actual = "actual";
        String expectedMessage = "\nExpected: \"expected\"\n     but: was \"actual\"";
        try {
            Assert.assertThat(actual, CoreMatchers.equalTo(expected));
        } catch (AssertionError e) {
            Assert.assertEquals(expectedMessage, e.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void nullAndStringNullPrintCorrectError() {
        try {
            Assert.assertEquals(null, "null");
        } catch (AssertionError e) {
            Assert.assertEquals("expected: null<null> but was: java.lang.String<null>", e.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test(expected = AssertionError.class)
    public void stringNullAndNullWorksToo() {
        Assert.assertEquals("null", null);
    }

    private static class NullToString {
        @Override
        public String toString() {
            return null;
        }
    }

    @Test
    public void nullToString() {
        try {
            Assert.assertEquals(new AssertionTest.NullToString(), new AssertionTest.NullToString());
        } catch (AssertionError e) {
            Assert.assertEquals(("expected: org.junit.tests.assertion.AssertionTest$NullToString<null> but " + "was: org.junit.tests.assertion.AssertionTest$NullToString<null>"), e.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test(expected = AssertionError.class)
    public void compareBigDecimalAndInteger() {
        final BigDecimal bigDecimal = new BigDecimal("1.2");
        final Integer integer = Integer.valueOf("1");
        Assert.assertEquals(bigDecimal, integer);
    }

    @Test(expected = AssertionError.class)
    public void sameObjectIsNotEqual() {
        Object o = new Object();
        Assert.assertNotEquals(o, o);
    }

    @Test
    public void objectsWithDiferentReferencesAreNotEqual() {
        Assert.assertNotEquals(new Object(), new Object());
    }

    @Test
    public void assertNotEqualsIncludesCorrectMessage() {
        Integer value1 = new Integer(1);
        Integer value2 = new Integer(1);
        String message = "The values should be different";
        try {
            Assert.assertNotEquals(message, value1, value2);
        } catch (AssertionError e) {
            Assert.assertEquals(((message + ". Actual: ") + value1), e.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertNotEqualsIncludesTheValueBeingTested() {
        Integer value1 = new Integer(1);
        Integer value2 = new Integer(1);
        try {
            Assert.assertNotEquals(value1, value2);
        } catch (AssertionError e) {
            Assert.assertTrue(e.getMessage().contains(value1.toString()));
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertNotEqualsWorksWithPrimitiveTypes() {
        Assert.assertNotEquals(1L, 2L);
        Assert.assertNotEquals("The values should be different", 1L, 2L);
        Assert.assertNotEquals(1.0, 2.0, 0);
        Assert.assertNotEquals("The values should be different", 1.0, 2.0, 0);
        Assert.assertNotEquals(1.0F, 2.0F, 0.0F);
        Assert.assertNotEquals("The values should be different", 1.0F, 2.0F, 0.0F);
    }

    @Test(expected = AssertionError.class)
    public void assertNotEqualsConsidersDeltaCorrectly() {
        Assert.assertNotEquals(1.0, 0.9, 0.1);
    }

    @Test(expected = AssertionError.class)
    public void assertNotEqualsConsidersFloatDeltaCorrectly() {
        Assert.assertNotEquals(1.0F, 0.75F, 0.25F);
    }

    @Test(expected = AssertionError.class)
    public void assertNotEqualsIgnoresDeltaOnNaN() {
        Assert.assertNotEquals(Double.NaN, Double.NaN, 1);
    }

    @Test(expected = AssertionError.class)
    public void assertNotEqualsIgnoresFloatDeltaOnNaN() {
        Assert.assertNotEquals(Float.NaN, Float.NaN, 1.0F);
    }

    @Test(expected = AssertionError.class)
    public void assertThrowsRequiresAnExceptionToBeThrown() {
        Assert.assertThrows(Throwable.class, AssertionTest.nonThrowingRunnable());
    }

    @Test
    public void assertThrowsIncludesAnInformativeDefaultMessage() {
        try {
            Assert.assertThrows(Throwable.class, AssertionTest.nonThrowingRunnable());
        } catch (AssertionError ex) {
            Assert.assertEquals("expected java.lang.Throwable to be thrown, but nothing was thrown", ex.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsIncludesTheSpecifiedMessage() {
        try {
            Assert.assertThrows("Foobar", Throwable.class, AssertionTest.nonThrowingRunnable());
        } catch (AssertionError ex) {
            Assert.assertEquals("Foobar: expected java.lang.Throwable to be thrown, but nothing was thrown", ex.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsReturnsTheSameObjectThrown() {
        NullPointerException npe = new NullPointerException();
        Throwable throwable = Assert.assertThrows(Throwable.class, AssertionTest.throwingRunnable(npe));
        Assert.assertSame(npe, throwable);
    }

    @Test(expected = AssertionError.class)
    public void assertThrowsDetectsTypeMismatchesViaExplicitTypeHint() {
        NullPointerException npe = new NullPointerException();
        Assert.assertThrows(IOException.class, AssertionTest.throwingRunnable(npe));
    }

    @Test
    public void assertThrowsWrapsAndPropagatesUnexpectedExceptions() {
        NullPointerException npe = new NullPointerException("inner-message");
        try {
            Assert.assertThrows(IOException.class, AssertionTest.throwingRunnable(npe));
        } catch (AssertionError ex) {
            Assert.assertSame(npe, ex.getCause());
            Assert.assertEquals("inner-message", ex.getCause().getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsSuppliesACoherentErrorMessageUponTypeMismatch() {
        NullPointerException npe = new NullPointerException();
        try {
            Assert.assertThrows(IOException.class, AssertionTest.throwingRunnable(npe));
        } catch (AssertionError error) {
            Assert.assertEquals("unexpected exception type thrown; expected:<java.io.IOException> but was:<java.lang.NullPointerException>", error.getMessage());
            Assert.assertSame(npe, error.getCause());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsSuppliesTheSpecifiedMessageUponTypeMismatch() {
        NullPointerException npe = new NullPointerException();
        try {
            Assert.assertThrows("Foobar", IOException.class, AssertionTest.throwingRunnable(npe));
        } catch (AssertionError error) {
            Assert.assertEquals("Foobar: unexpected exception type thrown; expected:<java.io.IOException> but was:<java.lang.NullPointerException>", error.getMessage());
            Assert.assertSame(npe, error.getCause());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsUsesCanonicalNameUponTypeMismatch() {
        NullPointerException npe = new NullPointerException();
        try {
            Assert.assertThrows(AssertionTest.NestedException.class, AssertionTest.throwingRunnable(npe));
        } catch (AssertionError error) {
            Assert.assertEquals(("unexpected exception type thrown; expected:<org.junit.tests.assertion.AssertionTest.NestedException>" + " but was:<java.lang.NullPointerException>"), error.getMessage());
            Assert.assertSame(npe, error.getCause());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsUsesNameUponTypeMismatchWithAnonymousClass() {
        NullPointerException npe = new NullPointerException() {};
        try {
            Assert.assertThrows(IOException.class, AssertionTest.throwingRunnable(npe));
        } catch (AssertionError error) {
            Assert.assertEquals(("unexpected exception type thrown; expected:<java.io.IOException>" + " but was:<org.junit.tests.assertion.AssertionTest$1>"), error.getMessage());
            Assert.assertSame(npe, error.getCause());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    @Test
    public void assertThrowsUsesCanonicalNameWhenRequiredExceptionNotThrown() {
        try {
            Assert.assertThrows(AssertionTest.NestedException.class, AssertionTest.nonThrowingRunnable());
        } catch (AssertionError error) {
            Assert.assertEquals(("expected org.junit.tests.assertion.AssertionTest.NestedException to be thrown," + " but nothing was thrown"), error.getMessage());
            return;
        }
        throw new AssertionError(AssertionTest.ASSERTION_ERROR_EXPECTED);
    }

    private static class NestedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}

