package junit.tests.framework;


import junit.framework.ComparisonFailure;
import junit.framework.TestCase;


public class ComparisonFailureTest extends TestCase {
    // Most of the tests are in ComparisonCompactorTest
    public void testConnection() {
        ComparisonFailure failure = new ComparisonFailure("warning", "Mary had a little lamb", "Mary had the little lamb");
        TestCase.assertEquals("warning expected:<Mary had [a] little lamb> but was:<Mary had [the] little lamb>", failure.getMessage());
    }

    // This is like an instanceof test.
    public void testThrowing() {
        try {
            TestCase.assertEquals("a", "b");
        } catch (ComparisonFailure e) {
            return;
        }
        TestCase.fail();
    }

    public void testExceptionToStringWithMessage() {
        try {
            TestCase.assertEquals("woops!", "a", "b");
        } catch (ComparisonFailure e) {
            if (!(e.toString().startsWith("junit.framework.ComparisonFailure: woops! expected:<"))) {
                TestCase.fail(("Unexpected message: " + e));
            }
            return;
        }
        TestCase.fail();
    }

    public void testExceptionToStringWithoutMessage() {
        try {
            TestCase.assertEquals("a", "b");
        } catch (ComparisonFailure e) {
            if (!(e.toString().startsWith("junit.framework.ComparisonFailure: expected:<"))) {
                TestCase.fail(("Unexpected message: " + e));
            }
            return;
        }
        TestCase.fail();
    }
}

