package junit.tests.extensions;


import junit.extensions.RepeatedTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;


/**
 * Testing the ActiveTest support
 */
public class ActiveTestTest extends TestCase {
    public static class SuccessTest extends TestCase {
        @Override
        public void runTest() {
        }
    }

    public void testActiveTest() {
        Test test = createActiveTestSuite();
        TestResult result = new TestResult();
        test.run(result);
        TestCase.assertEquals(100, result.runCount());
        TestCase.assertEquals(0, result.failureCount());
        TestCase.assertEquals(0, result.errorCount());
    }

    public void testActiveRepeatedTest() {
        Test test = new RepeatedTest(createActiveTestSuite(), 5);
        TestResult result = new TestResult();
        test.run(result);
        TestCase.assertEquals(500, result.runCount());
        TestCase.assertEquals(0, result.failureCount());
        TestCase.assertEquals(0, result.errorCount());
    }

    public void testActiveRepeatedTest0() {
        Test test = new RepeatedTest(createActiveTestSuite(), 0);
        TestResult result = new TestResult();
        test.run(result);
        TestCase.assertEquals(0, result.runCount());
        TestCase.assertEquals(0, result.failureCount());
        TestCase.assertEquals(0, result.errorCount());
    }

    public void testActiveRepeatedTest1() {
        Test test = new RepeatedTest(createActiveTestSuite(), 1);
        TestResult result = new TestResult();
        test.run(result);
        TestCase.assertEquals(100, result.runCount());
        TestCase.assertEquals(0, result.failureCount());
        TestCase.assertEquals(0, result.errorCount());
    }
}

