package junit.tests.extensions;


import junit.extensions.RepeatedTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;


/**
 * Testing the RepeatedTest support.
 */
public class RepeatedTestTest extends TestCase {
    private TestSuite fSuite;

    public static class SuccessTest extends TestCase {
        @Override
        public void runTest() {
        }
    }

    public RepeatedTestTest(String name) {
        super(name);
        fSuite = new TestSuite();
        fSuite.addTest(new RepeatedTestTest.SuccessTest());
        fSuite.addTest(new RepeatedTestTest.SuccessTest());
    }

    public void testRepeatedOnce() {
        Test test = new RepeatedTest(fSuite, 1);
        TestCase.assertEquals(2, test.countTestCases());
        TestResult result = new TestResult();
        test.run(result);
        TestCase.assertEquals(2, result.runCount());
    }

    public void testRepeatedMoreThanOnce() {
        Test test = new RepeatedTest(fSuite, 3);
        TestCase.assertEquals(6, test.countTestCases());
        TestResult result = new TestResult();
        test.run(result);
        TestCase.assertEquals(6, result.runCount());
    }

    public void testRepeatedZero() {
        Test test = new RepeatedTest(fSuite, 0);
        TestCase.assertEquals(0, test.countTestCases());
        TestResult result = new TestResult();
        test.run(result);
        TestCase.assertEquals(0, result.runCount());
    }

    public void testRepeatedNegative() {
        try {
            new RepeatedTest(fSuite, (-1));
        } catch (IllegalArgumentException e) {
            TestCase.assertTrue(e.getMessage().contains(">="));
            return;
        }
        TestCase.fail("Should throw an IllegalArgumentException");
    }
}

