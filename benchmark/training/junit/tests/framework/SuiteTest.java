package junit.tests.framework;


import java.util.Collections;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;


/**
 * A fixture for testing the "auto" test suite feature.
 */
public class SuiteTest extends TestCase {
    protected TestResult fResult;

    public SuiteTest(String name) {
        super(name);
    }

    public void testInheritedTests() {
        TestSuite suite = new TestSuite(InheritedTestCase.class);
        suite.run(fResult);
        TestCase.assertTrue(fResult.wasSuccessful());
        TestCase.assertEquals(2, fResult.runCount());
    }

    public void testNoTestCaseClass() {
        Test t = new TestSuite(NoTestCaseClass.class);
        t.run(fResult);
        TestCase.assertEquals(1, fResult.runCount());// warning test

        TestCase.assertTrue((!(fResult.wasSuccessful())));
    }

    public void testNoTestCases() {
        Test t = new TestSuite(NoTestCases.class);
        t.run(fResult);
        TestCase.assertTrue(((fResult.runCount()) == 1));// warning test

        TestCase.assertTrue(((fResult.failureCount()) == 1));
        TestCase.assertTrue((!(fResult.wasSuccessful())));
    }

    public void testNotExistingTestCase() {
        Test t = new SuiteTest("notExistingMethod");
        t.run(fResult);
        TestCase.assertTrue(((fResult.runCount()) == 1));
        TestCase.assertTrue(((fResult.failureCount()) == 1));
        TestCase.assertTrue(((fResult.errorCount()) == 0));
    }

    public void testNotPublicTestCase() {
        TestSuite suite = new TestSuite(NotPublicTestCase.class);
        // 1 public test case + 1 warning for the non-public test case
        TestCase.assertEquals(2, suite.countTestCases());
    }

    public void testNotVoidTestCase() {
        TestSuite suite = new TestSuite(NotVoidTestCase.class);
        TestCase.assertTrue(((suite.countTestCases()) == 1));
    }

    public void testOneTestCase() {
        TestSuite t = new TestSuite(OneTestCase.class);
        t.run(fResult);
        TestCase.assertTrue(((fResult.runCount()) == 1));
        TestCase.assertTrue(((fResult.failureCount()) == 0));
        TestCase.assertTrue(((fResult.errorCount()) == 0));
        TestCase.assertTrue(fResult.wasSuccessful());
    }

    public void testOneTestCaseEclipseSeesSameStructureAs381() {
        TestSuite t = new TestSuite(ThreeTestCases.class);
        TestCase.assertEquals(3, Collections.list(t.tests()).size());
    }

    public void testShadowedTests() {
        TestSuite suite = new TestSuite(OverrideTestCase.class);
        suite.run(fResult);
        TestCase.assertEquals(1, fResult.runCount());
    }

    public void testAddTestSuite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(OneTestCase.class);
        suite.run(fResult);
        TestCase.assertEquals(1, fResult.runCount());
    }

    public void testCreateSuiteFromArray() {
        TestSuite suite = new TestSuite(OneTestCase.class, DoublePrecisionAssertTest.class);
        TestCase.assertEquals(2, suite.testCount());
        TestCase.assertEquals("junit.tests.framework.DoublePrecisionAssertTest", ((TestSuite) (suite.testAt(1))).getName());
        TestCase.assertEquals("junit.tests.framework.OneTestCase", ((TestSuite) (suite.testAt(0))).getName());
    }
}

