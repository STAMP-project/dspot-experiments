package org.junit.tests.junit3compatibility;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.ResultPrinter;
import junit.textui.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class ForwardCompatibilityPrintingTest extends TestCase {
    static class TestResultPrinter extends ResultPrinter {
        TestResultPrinter(PrintStream writer) {
            super(writer);
        }

        /* Spoof printing time so the tests are deterministic */
        @Override
        protected String elapsedTimeAsString(long runTime) {
            return "0";
        }
    }

    public void testError() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        TestRunner runner = new TestRunner(new ForwardCompatibilityPrintingTest.TestResultPrinter(new PrintStream(output)));
        String expected = expected(new String[]{ ".E", "Time: 0", "Errors here", "", "FAILURES!!!", "Tests run: 1,  Failures: 0,  Errors: 1", "" });
        ResultPrinter printer = new ForwardCompatibilityPrintingTest.TestResultPrinter(new PrintStream(output)) {
            @Override
            public void printErrors(TestResult result) {
                getWriter().println("Errors here");
            }
        };
        runner.setPrinter(printer);
        TestSuite suite = new TestSuite();
        suite.addTest(new TestCase() {
            @Override
            public void runTest() throws Exception {
                throw new Exception();
            }
        });
        runner.doRun(suite);
        TestCase.assertEquals(expected, output.toString());
    }

    public static class ATest {
        @Test
        public void error() {
            Assert.fail();
        }
    }

    public void testErrorAdapted() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        TestRunner runner = new TestRunner(new ForwardCompatibilityPrintingTest.TestResultPrinter(new PrintStream(output)));
        String expected = expected(new String[]{ ".E", "Time: 0", "Errors here", "", "FAILURES!!!", "Tests run: 1,  Failures: 0,  Errors: 1", "" });
        ResultPrinter printer = new ForwardCompatibilityPrintingTest.TestResultPrinter(new PrintStream(output)) {
            @Override
            public void printErrors(TestResult result) {
                getWriter().println("Errors here");
            }
        };
        runner.setPrinter(printer);
        runner.doRun(new JUnit4TestAdapter(ForwardCompatibilityPrintingTest.ATest.class));
        TestCase.assertEquals(expected, output.toString());
    }
}

