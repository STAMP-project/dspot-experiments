package junit.tests.framework;


import junit.framework.AssertionFailedError;
import junit.framework.TestCase;


public class FloatAssertTest extends TestCase {
    /**
     * Test for the special Double.NaN value.
     */
    public void testAssertEqualsNaNFails() {
        try {
            TestCase.assertEquals(1.234F, Float.NaN, 0.0);
            TestCase.fail();
        } catch (AssertionFailedError e) {
        }
    }

    public void testAssertNaNEqualsFails() {
        try {
            TestCase.assertEquals(Float.NaN, 1.234F, 0.0);
            TestCase.fail();
        } catch (AssertionFailedError e) {
        }
    }

    public void testAssertNaNEqualsNaN() {
        TestCase.assertEquals(Float.NaN, Float.NaN, 0.0);
    }

    public void testAssertPosInfinityNotEqualsNegInfinity() {
        try {
            TestCase.assertEquals(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, 0.0);
            TestCase.fail();
        } catch (AssertionFailedError e) {
        }
    }

    public void testAssertPosInfinityNotEquals() {
        try {
            TestCase.assertEquals(Float.POSITIVE_INFINITY, 1.23F, 0.0);
            TestCase.fail();
        } catch (AssertionFailedError e) {
        }
    }

    public void testAssertPosInfinityEqualsInfinity() {
        TestCase.assertEquals(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, 0.0);
    }

    public void testAssertNegInfinityEqualsInfinity() {
        TestCase.assertEquals(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, 0.0);
    }

    public void testAllInfinities() {
        try {
            TestCase.assertEquals(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
            TestCase.fail();
        } catch (AssertionFailedError e) {
        }
    }
}

