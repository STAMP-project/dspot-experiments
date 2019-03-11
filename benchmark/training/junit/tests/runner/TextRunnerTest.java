package junit.tests.runner;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class TextRunnerTest extends TestCase {
    public void testFailure() throws Exception {
        execTest("junit.tests.framework.Failure", false);
    }

    public void testSuccess() throws Exception {
        execTest("junit.tests.framework.Success", true);
    }

    public void testError() throws Exception {
        execTest("junit.tests.BogusDude", false);
    }

    public void testRunReturnsResult() {
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int arg0) throws IOException {
            }
        }));
        try {
            TestResult result = TestRunner.run(new TestSuite());
            TestCase.assertTrue(result.wasSuccessful());
        } finally {
            System.setOut(oldOut);
        }
    }
}

