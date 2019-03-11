package junit.tests.framework;


import junit.framework.AssertionFailedError;
import junit.framework.TestCase;


public class DoublePrecisionAssertTest extends TestCase {
    /**
     * Test for the special Double.NaN value.
     */
    public void testAssertEqualsNaNFails() {
        try {
            TestCase.assertEquals(1.234, Double.NaN, 0.0);
            TestCase.fail();
        } catch (AssertionFailedError e) {
        }
    }

    public void testAssertNaNEqualsFails() {
        try {
            TestCase.assertEquals(Double.NaN, 1.234, 0.0);
            TestCase.fail();
        } catch (AssertionFailedError e) {
        }
    }

    public void testAssertNaNEqualsNaN() {
        TestCase.assertEquals(Double.NaN, Double.NaN, 0.0);
    }

    public void testAssertPosInfinityNotEqualsNegInfinity() {
        try {
            TestCase.assertEquals(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 0.0);
            TestCase.fail();
        } catch (AssertionFailedError e) {
        }
    }

    public void testAssertPosInfinityNotEquals() {
        try {
            TestCase.assertEquals(Double.POSITIVE_INFINITY, 1.23, 0.0);
            TestCase.fail();
        } catch (AssertionFailedError e) {
        }
    }

    public void testAssertPosInfinityEqualsInfinity() {
        TestCase.assertEquals(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0);
    }

    public void testAssertNegInfinityEqualsInfinity() {
        TestCase.assertEquals(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 0.0);
    }
}

