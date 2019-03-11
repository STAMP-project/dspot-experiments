package org.junit.tests.junit3compatibility;


import junit.framework.AssertionFailedError;
import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestListener;
import junit.framework.TestResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;


public class ForwardCompatibilityTest extends TestCase {
    static String fLog;

    public static class NewTest {
        @Before
        public void before() {
            ForwardCompatibilityTest.fLog += "before ";
        }

        @After
        public void after() {
            ForwardCompatibilityTest.fLog += "after ";
        }

        @Test
        public void test() {
            ForwardCompatibilityTest.fLog += "test ";
        }
    }

    public void testCompatibility() {
        ForwardCompatibilityTest.fLog = "";
        TestResult result = new TestResult();
        junit.framework.Test adapter = new JUnit4TestAdapter(ForwardCompatibilityTest.NewTest.class);
        adapter.run(result);
        TestCase.assertEquals("before test after ", ForwardCompatibilityTest.fLog);
    }

    public void testToString() {
        JUnit4TestAdapter adapter = new JUnit4TestAdapter(ForwardCompatibilityTest.NewTest.class);
        junit.framework.Test test = adapter.getTests().get(0);
        TestCase.assertEquals(String.format("test(%s)", ForwardCompatibilityTest.NewTest.class.getName()), test.toString());
    }

    public void testUseGlobalCache() {
        JUnit4TestAdapter adapter1 = new JUnit4TestAdapter(ForwardCompatibilityTest.NewTest.class);
        JUnit4TestAdapter adapter2 = new JUnit4TestAdapter(ForwardCompatibilityTest.NewTest.class);
        TestCase.assertSame(adapter1.getTests().get(0), adapter2.getTests().get(0));
    }

    static Exception exception = new Exception();

    public static class ErrorTest {
        @Test
        public void error() throws Exception {
            throw ForwardCompatibilityTest.exception;
        }
    }

    public void testException() {
        TestResult result = new TestResult();
        junit.framework.Test adapter = new JUnit4TestAdapter(ForwardCompatibilityTest.ErrorTest.class);
        adapter.run(result);
        TestCase.assertEquals(ForwardCompatibilityTest.exception, result.errors().nextElement().thrownException());
    }

    public void testNotifyResult() {
        JUnit4TestAdapter adapter = new JUnit4TestAdapter(ForwardCompatibilityTest.ErrorTest.class);
        TestResult result = new TestResult();
        final StringBuffer log = new StringBuffer();
        result.addListener(new TestListener() {
            public void startTest(junit.framework.Test test) {
                log.append(" start ").append(test);
            }

            public void endTest(junit.framework.Test test) {
                log.append(" end ").append(test);
            }

            public void addFailure(junit.framework.Test test, AssertionFailedError t) {
                log.append(" failure ").append(test);
            }

            public void addError(junit.framework.Test test, Throwable e) {
                log.append((" error " + test));
            }
        });
        adapter.run(result);
        String testName = String.format("error(%s)", ForwardCompatibilityTest.ErrorTest.class.getName());
        TestCase.assertEquals(String.format(" start %s error %s end %s", testName, testName, testName), log.toString());
    }

    public static class NoExceptionTest {
        @Test(expected = Exception.class)
        public void succeed() throws Exception {
        }
    }

    public void testNoException() {
        TestResult result = new TestResult();
        junit.framework.Test adapter = new JUnit4TestAdapter(ForwardCompatibilityTest.NoExceptionTest.class);
        adapter.run(result);
        TestCase.assertFalse(result.wasSuccessful());
    }

    public static class ExpectedTest {
        @Test(expected = Exception.class)
        public void expected() throws Exception {
            throw new Exception();
        }
    }

    public void testExpected() {
        TestResult result = new TestResult();
        junit.framework.Test adapter = new JUnit4TestAdapter(ForwardCompatibilityTest.ExpectedTest.class);
        adapter.run(result);
        TestCase.assertTrue(result.wasSuccessful());
    }

    public static class UnExpectedExceptionTest {
        @Test(expected = Exception.class)
        public void expected() throws Exception {
            throw new Error();
        }
    }

    static String log;

    public static class BeforeClassTest {
        @BeforeClass
        public static void beforeClass() {
            ForwardCompatibilityTest.log += "before class ";
        }

        @Before
        public void before() {
            ForwardCompatibilityTest.log += "before ";
        }

        @Test
        public void one() {
            ForwardCompatibilityTest.log += "test ";
        }

        @Test
        public void two() {
            ForwardCompatibilityTest.log += "test ";
        }

        @After
        public void after() {
            ForwardCompatibilityTest.log += "after ";
        }

        @AfterClass
        public static void afterClass() {
            ForwardCompatibilityTest.log += "after class ";
        }
    }

    public void testBeforeAndAfterClass() {
        ForwardCompatibilityTest.log = "";
        TestResult result = new TestResult();
        junit.framework.Test adapter = new JUnit4TestAdapter(ForwardCompatibilityTest.BeforeClassTest.class);
        adapter.run(result);
        TestCase.assertEquals("before class before test after before test after after class ", ForwardCompatibilityTest.log);
    }

    public static class ExceptionInBeforeTest {
        @Before
        public void error() {
            throw new Error();
        }

        @Test
        public void nothing() {
        }
    }

    public void testExceptionInBefore() {
        TestResult result = new TestResult();
        junit.framework.Test adapter = new JUnit4TestAdapter(ForwardCompatibilityTest.ExceptionInBeforeTest.class);
        adapter.run(result);
        TestCase.assertEquals(1, result.errorCount());
    }

    public static class InvalidMethodTest {
        @BeforeClass
        public void shouldBeStatic() {
        }

        @Test
        public void aTest() {
        }
    }

    public void testInvalidMethod() {
        TestResult result = new TestResult();
        junit.framework.Test adapter = new JUnit4TestAdapter(ForwardCompatibilityTest.InvalidMethodTest.class);
        adapter.run(result);
        TestCase.assertEquals(1, result.errorCount());
        TestFailure failure = result.errors().nextElement();
        TestCase.assertTrue(failure.exceptionMessage().contains("Method shouldBeStatic() should be static"));
    }

    private static boolean wasRun = false;

    public static class MarkerRunner extends Runner {
        public MarkerRunner(Class<?> klass) {
        }

        @Override
        public void run(RunNotifier notifier) {
            ForwardCompatibilityTest.wasRun = true;
        }

        @Override
        public int testCount() {
            return 0;
        }

        @Override
        public Description getDescription() {
            return Description.EMPTY;
        }
    }

    @RunWith(ForwardCompatibilityTest.MarkerRunner.class)
    public static class NoTests {}

    public void testRunWithClass() {
        ForwardCompatibilityTest.wasRun = false;
        TestResult result = new TestResult();
        junit.framework.Test adapter = new JUnit4TestAdapter(ForwardCompatibilityTest.NoTests.class);
        adapter.run(result);
        TestCase.assertTrue(ForwardCompatibilityTest.wasRun);
    }

    public void testToStringSuite() {
        junit.framework.Test adapter = new JUnit4TestAdapter(ForwardCompatibilityTest.NoTests.class);
        TestCase.assertEquals(ForwardCompatibilityTest.NoTests.class.getName(), adapter.toString());
    }
}

