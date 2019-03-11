package junit.tests.framework;


import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;


/**
 * Test class used in SuiteTest
 */
public class TestListenerTest extends TestCase implements TestListener {
    private TestResult fResult;

    private int fStartCount;

    private int fEndCount;

    private int fFailureCount;

    private int fErrorCount;

    public void testError() {
        TestCase test = new TestCase("noop") {
            @Override
            public void runTest() {
                throw new Error();
            }
        };
        test.run(fResult);
        TestCase.assertEquals(1, fErrorCount);
        TestCase.assertEquals(1, fEndCount);
    }

    public void testFailure() {
        TestCase test = new TestCase("noop") {
            @Override
            public void runTest() {
                TestCase.fail();
            }
        };
        test.run(fResult);
        TestCase.assertEquals(1, fFailureCount);
        TestCase.assertEquals(1, fEndCount);
    }

    public void testStartStop() {
        TestCase test = new TestCase("noop") {
            @Override
            public void runTest() {
            }
        };
        test.run(fResult);
        TestCase.assertEquals(1, fStartCount);
        TestCase.assertEquals(1, fEndCount);
    }
}

