package junit.tests.framework;


import junit.framework.AssertionFailedError;
import junit.framework.ComparisonFailure;
import junit.framework.TestCase;


public class AssertTest extends TestCase {
    /* In the tests that follow, we can't use standard formatting
    for exception tests:
        try {
            somethingThatShouldThrow();
            fail();
        catch (AssertionFailedError e) {
        }
    because fail() would never be reported.
     */
    public void testFail() {
        // Also, we are testing fail, so we can't rely on fail() working.
        // We have to throw the exception manually.
        try {
            TestCase.fail();
        } catch (AssertionFailedError e) {
            return;
        }
        throw new AssertionFailedError();
    }

    public void testAssertionFailedErrorToStringWithNoMessage() {
        // Also, we are testing fail, so we can't rely on fail() working.
        // We have to throw the exception manually.
        try {
            TestCase.fail();
        } catch (AssertionFailedError e) {
            TestCase.assertEquals("junit.framework.AssertionFailedError", e.toString());
            return;
        }
        throw new AssertionFailedError();
    }

    public void testAssertionFailedErrorToStringWithMessage() {
        // Also, we are testing fail, so we can't rely on fail() working.
        // We have to throw the exception manually.
        try {
            TestCase.fail("woops!");
        } catch (AssertionFailedError e) {
            TestCase.assertEquals("junit.framework.AssertionFailedError: woops!", e.toString());
            return;
        }
        throw new AssertionFailedError();
    }

    public void testAssertEquals() {
        Object o = new Object();
        TestCase.assertEquals(o, o);
        try {
            TestCase.assertEquals(new Object(), new Object());
        } catch (AssertionFailedError e) {
            return;
        }
        TestCase.fail();
    }

    public void testAssertEqualsNull() {
        TestCase.assertEquals(((Object) (null)), ((Object) (null)));
    }

    public void testAssertStringEquals() {
        TestCase.assertEquals("a", "a");
    }

    public void testAssertNullNotEqualsString() {
        try {
            TestCase.assertEquals(null, "foo");
            TestCase.fail();
        } catch (ComparisonFailure e) {
        }
    }

    public void testAssertStringNotEqualsNull() {
        try {
            TestCase.assertEquals("foo", null);
            TestCase.fail();
        } catch (ComparisonFailure e) {
            e.getMessage();// why no assertion?

        }
    }

    public void testAssertNullNotEqualsNull() {
        try {
            TestCase.assertEquals(null, new Object());
        } catch (AssertionFailedError e) {
            e.getMessage();// why no assertion?

            return;
        }
        TestCase.fail();
    }

    public void testAssertNull() {
        TestCase.assertNull(null);
        try {
            TestCase.assertNull(new Object());
        } catch (AssertionFailedError e) {
            return;
        }
        TestCase.fail();
    }

    public void testAssertNotNull() {
        TestCase.assertNotNull(new Object());
        try {
            TestCase.assertNotNull(null);
        } catch (AssertionFailedError e) {
            return;
        }
        TestCase.fail();
    }

    public void testAssertTrue() {
        TestCase.assertTrue(true);
        try {
            TestCase.assertTrue(false);
        } catch (AssertionFailedError e) {
            return;
        }
        TestCase.fail();
    }

    public void testAssertFalse() {
        TestCase.assertFalse(false);
        try {
            TestCase.assertFalse(true);
        } catch (AssertionFailedError e) {
            return;
        }
        TestCase.fail();
    }

    public void testAssertSame() {
        Object o = new Object();
        TestCase.assertSame(o, o);
        try {
            TestCase.assertSame(new Integer(1), new Integer(1));
        } catch (AssertionFailedError e) {
            return;
        }
        TestCase.fail();
    }

    public void testAssertNotSame() {
        TestCase.assertNotSame(new Integer(1), null);
        TestCase.assertNotSame(null, new Integer(1));
        TestCase.assertNotSame(new Integer(1), new Integer(1));
        try {
            Integer obj = new Integer(1);
            TestCase.assertNotSame(obj, obj);
        } catch (AssertionFailedError e) {
            return;
        }
        TestCase.fail();
    }

    public void testAssertNotSameFailsNull() {
        try {
            TestCase.assertNotSame(null, null);
        } catch (AssertionFailedError e) {
            return;
        }
        TestCase.fail();
    }
}

