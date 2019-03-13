package junit.tests.extensions;


import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.tests.WasRun;


/**
 * A test case testing the extensions to the testing framework.
 */
public class ExtensionTest extends TestCase {
    static class TornDown extends TestSetup {
        boolean fTornDown = false;

        TornDown(Test test) {
            super(test);
        }

        @Override
        protected void tearDown() {
            fTornDown = true;
        }
    }

    public void testRunningErrorInTestSetup() {
        TestCase test = new TestCase("failure") {
            @Override
            public void runTest() {
                TestCase.fail();
            }
        };
        TestSetup wrapper = new TestSetup(test);
        TestResult result = new TestResult();
        wrapper.run(result);
        TestCase.assertTrue((!(result.wasSuccessful())));
    }

    public void testRunningErrorsInTestSetup() {
        TestCase failure = new TestCase("failure") {
            @Override
            public void runTest() {
                TestCase.fail();
            }
        };
        TestCase error = new TestCase("error") {
            @Override
            public void runTest() {
                throw new Error();
            }
        };
        TestSuite suite = new TestSuite();
        suite.addTest(failure);
        suite.addTest(error);
        TestSetup wrapper = new TestSetup(suite);
        TestResult result = new TestResult();
        wrapper.run(result);
        TestCase.assertEquals(1, result.failureCount());
        TestCase.assertEquals(1, result.errorCount());
    }

    public void testSetupErrorDontTearDown() {
        WasRun test = new WasRun();
        ExtensionTest.TornDown wrapper = new ExtensionTest.TornDown(test) {
            @SuppressWarnings("deprecation")
            @Override
            public void setUp() {
                Assert.fail();
            }
        };
        TestResult result = new TestResult();
        wrapper.run(result);
        TestCase.assertTrue((!(wrapper.fTornDown)));
    }

    public void testSetupErrorInTestSetup() {
        WasRun test = new WasRun();
        TestSetup wrapper = new TestSetup(test) {
            @SuppressWarnings("deprecation")
            @Override
            public void setUp() {
                Assert.fail();
            }
        };
        TestResult result = new TestResult();
        wrapper.run(result);
        TestCase.assertTrue((!(test.fWasRun)));
        TestCase.assertTrue((!(result.wasSuccessful())));
    }
}

