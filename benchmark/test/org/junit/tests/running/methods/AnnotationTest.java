package org.junit.tests.running.methods;


import java.util.Collection;
import java.util.HashSet;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


public class AnnotationTest extends TestCase {
    static boolean run;

    public static class SimpleTest {
        @Test
        public void success() {
            AnnotationTest.run = true;
        }
    }

    public void testAnnotatedMethod() throws Exception {
        JUnitCore runner = new JUnitCore();
        runner.run(AnnotationTest.SimpleTest.class);
        TestCase.assertTrue(AnnotationTest.run);
    }

    @RunWith(JUnit4.class)
    public static class SimpleTestWithFutureProofExplicitRunner {
        @Test
        public void success() {
            AnnotationTest.run = true;
        }
    }

    public void testAnnotatedMethodWithFutureProofExplicitRunner() throws Exception {
        JUnitCore runner = new JUnitCore();
        runner.run(AnnotationTest.SimpleTestWithFutureProofExplicitRunner.class);
        TestCase.assertTrue(AnnotationTest.run);
    }

    public static class SetupTest {
        @Before
        public void before() {
            AnnotationTest.run = true;
        }

        @Test
        public void success() {
        }
    }

    public void testSetup() throws Exception {
        JUnitCore runner = new JUnitCore();
        runner.run(AnnotationTest.SetupTest.class);
        TestCase.assertTrue(AnnotationTest.run);
    }

    public static class TeardownTest {
        @After
        public void after() {
            AnnotationTest.run = true;
        }

        @Test
        public void success() {
        }
    }

    public void testTeardown() throws Exception {
        JUnitCore runner = new JUnitCore();
        runner.run(AnnotationTest.TeardownTest.class);
        TestCase.assertTrue(AnnotationTest.run);
    }

    public static class FailureTest {
        @Test
        public void error() throws Exception {
            Assert.fail();
        }
    }

    public void testRunFailure() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(AnnotationTest.FailureTest.class);
        TestCase.assertEquals(1, result.getRunCount());
        TestCase.assertEquals(1, result.getFailureCount());
        TestCase.assertEquals(AssertionError.class, result.getFailures().get(0).getException().getClass());
    }

    public static class SetupFailureTest {
        @Before
        public void before() {
            throw new Error();
        }

        @Test
        public void test() {
            AnnotationTest.run = true;
        }
    }

    public void testSetupFailure() throws Exception {
        JUnitCore core = new JUnitCore();
        Result runner = core.run(AnnotationTest.SetupFailureTest.class);
        TestCase.assertEquals(1, runner.getRunCount());
        TestCase.assertEquals(1, runner.getFailureCount());
        TestCase.assertEquals(Error.class, runner.getFailures().get(0).getException().getClass());
        TestCase.assertFalse(AnnotationTest.run);
    }

    public static class TeardownFailureTest {
        @After
        public void after() {
            throw new Error();
        }

        @Test
        public void test() {
        }
    }

    public void testTeardownFailure() throws Exception {
        JUnitCore core = new JUnitCore();
        Result runner = core.run(AnnotationTest.TeardownFailureTest.class);
        TestCase.assertEquals(1, runner.getRunCount());
        TestCase.assertEquals(1, runner.getFailureCount());
        TestCase.assertEquals(Error.class, runner.getFailures().get(0).getException().getClass());
    }

    public static class TestAndTeardownFailureTest {
        @After
        public void after() {
            throw new Error("hereAfter");
        }

        @Test
        public void test() throws Exception {
            throw new Exception("inTest");
        }
    }

    public void testTestAndTeardownFailure() throws Exception {
        JUnitCore core = new JUnitCore();
        Result runner = core.run(AnnotationTest.TestAndTeardownFailureTest.class);
        TestCase.assertEquals(1, runner.getRunCount());
        TestCase.assertEquals(2, runner.getFailureCount());
        MatcherAssert.assertThat(runner.getFailures().toString(), CoreMatchers.allOf(CoreMatchers.containsString("hereAfter"), CoreMatchers.containsString("inTest")));
    }

    public static class TeardownAfterFailureTest {
        @After
        public void after() {
            AnnotationTest.run = true;
        }

        @Test
        public void test() throws Exception {
            throw new Exception();
        }
    }

    public void testTeardownAfterFailure() throws Exception {
        JUnitCore runner = new JUnitCore();
        runner.run(AnnotationTest.TeardownAfterFailureTest.class);
        TestCase.assertTrue(AnnotationTest.run);
    }

    static int count;

    static Collection<Object> tests;

    public static class TwoTests {
        @Test
        public void one() {
            (AnnotationTest.count)++;
            AnnotationTest.tests.add(this);
        }

        @Test
        public void two() {
            (AnnotationTest.count)++;
            AnnotationTest.tests.add(this);
        }
    }

    public void testTwoTests() throws Exception {
        AnnotationTest.count = 0;
        AnnotationTest.tests = new HashSet<Object>();
        JUnitCore runner = new JUnitCore();
        runner.run(AnnotationTest.TwoTests.class);
        TestCase.assertEquals(2, AnnotationTest.count);
        TestCase.assertEquals(2, AnnotationTest.tests.size());
    }

    public static class OldTest extends TestCase {
        public void test() {
            AnnotationTest.run = true;
        }
    }

    public void testOldTest() throws Exception {
        JUnitCore runner = new JUnitCore();
        runner.run(AnnotationTest.OldTest.class);
        TestCase.assertTrue(AnnotationTest.run);
    }

    public static class OldSuiteTest extends TestCase {
        public void testOne() {
            AnnotationTest.run = true;
        }
    }

    public void testOldSuiteTest() throws Exception {
        TestSuite suite = new TestSuite(AnnotationTest.OldSuiteTest.class);
        JUnitCore runner = new JUnitCore();
        runner.run(suite);
        TestCase.assertTrue(AnnotationTest.run);
    }

    public static class ExceptionTest {
        @Test(expected = Error.class)
        public void expectedException() {
            throw new Error();
        }
    }

    public void testException() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(AnnotationTest.ExceptionTest.class);
        TestCase.assertEquals(0, result.getFailureCount());
    }

    public static class NoExceptionTest {
        @Test(expected = Error.class)
        public void expectedException() {
        }
    }

    public void testExceptionNotThrown() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(AnnotationTest.NoExceptionTest.class);
        TestCase.assertEquals(1, result.getFailureCount());
        TestCase.assertEquals("Expected exception: java.lang.Error", result.getFailures().get(0).getMessage());
    }

    public static class OneTimeSetup {
        @BeforeClass
        public static void once() {
            (AnnotationTest.count)++;
        }

        @Test
        public void one() {
        }

        @Test
        public void two() {
        }
    }

    public void testOneTimeSetup() throws Exception {
        AnnotationTest.count = 0;
        JUnitCore core = new JUnitCore();
        core.run(AnnotationTest.OneTimeSetup.class);
        TestCase.assertEquals(1, AnnotationTest.count);
    }

    public static class OneTimeTeardown {
        @AfterClass
        public static void once() {
            (AnnotationTest.count)++;
        }

        @Test
        public void one() {
        }

        @Test
        public void two() {
        }
    }

    public void testOneTimeTeardown() throws Exception {
        AnnotationTest.count = 0;
        JUnitCore core = new JUnitCore();
        core.run(AnnotationTest.OneTimeTeardown.class);
        TestCase.assertEquals(1, AnnotationTest.count);
    }

    static String log;

    public static class OrderTest {
        @BeforeClass
        public static void onceBefore() {
            AnnotationTest.log += "beforeClass ";
        }

        @Before
        public void before() {
            AnnotationTest.log += "before ";
        }

        @Test
        public void test() {
            AnnotationTest.log += "test ";
        }

        @After
        public void after() {
            AnnotationTest.log += "after ";
        }

        @AfterClass
        public static void onceAfter() {
            AnnotationTest.log += "afterClass ";
        }
    }

    public void testOrder() throws Exception {
        AnnotationTest.log = "";
        JUnitCore core = new JUnitCore();
        core.run(AnnotationTest.OrderTest.class);
        TestCase.assertEquals("beforeClass before test after afterClass ", AnnotationTest.log);
    }

    public static class NonStaticOneTimeSetup {
        @BeforeClass
        public void once() {
        }

        @Test
        public void aTest() {
        }
    }

    public void testNonStaticOneTimeSetup() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(AnnotationTest.NonStaticOneTimeSetup.class);
        TestCase.assertEquals(1, result.getFailureCount());
    }

    public static class ErrorInBeforeClass {
        @BeforeClass
        public static void before() throws Exception {
            throw new Exception();
        }

        @Test
        public void test() {
            AnnotationTest.run = true;
        }
    }

    public void testErrorInBeforeClass() throws Exception {
        AnnotationTest.run = false;
        JUnitCore core = new JUnitCore();
        Result result = core.run(AnnotationTest.ErrorInBeforeClass.class);
        TestCase.assertFalse(AnnotationTest.run);
        TestCase.assertEquals(1, result.getFailureCount());
        Description description = result.getFailures().get(0).getDescription();
        TestCase.assertEquals(AnnotationTest.ErrorInBeforeClass.class.getName(), description.getDisplayName());
    }

    public static class ErrorInAfterClass {
        @Test
        public void test() {
            AnnotationTest.run = true;
        }

        @AfterClass
        public static void after() throws Exception {
            throw new Exception();
        }
    }

    public void testErrorInAfterClass() throws Exception {
        AnnotationTest.run = false;
        JUnitCore core = new JUnitCore();
        Result result = core.run(AnnotationTest.ErrorInAfterClass.class);
        TestCase.assertTrue(AnnotationTest.run);
        TestCase.assertEquals(1, result.getFailureCount());
    }

    static class SuperInheritance {
        @BeforeClass
        public static void beforeClassSuper() {
            AnnotationTest.log += "Before class super ";
        }

        @AfterClass
        public static void afterClassSuper() {
            AnnotationTest.log += "After class super ";
        }

        @Before
        public void beforeSuper() {
            AnnotationTest.log += "Before super ";
        }

        @After
        public void afterSuper() {
            AnnotationTest.log += "After super ";
        }
    }

    public static class SubInheritance extends AnnotationTest.SuperInheritance {
        @BeforeClass
        public static void beforeClassSub() {
            AnnotationTest.log += "Before class sub ";
        }

        @AfterClass
        public static void afterClassSub() {
            AnnotationTest.log += "After class sub ";
        }

        @Before
        public void beforeSub() {
            AnnotationTest.log += "Before sub ";
        }

        @After
        public void afterSub() {
            AnnotationTest.log += "After sub ";
        }

        @Test
        public void test() {
            AnnotationTest.log += "Test ";
        }
    }

    public void testOrderingOfInheritance() throws Exception {
        AnnotationTest.log = "";
        JUnitCore core = new JUnitCore();
        core.run(AnnotationTest.SubInheritance.class);
        TestCase.assertEquals("Before class super Before class sub Before super Before sub Test After sub After super After class sub After class super ", AnnotationTest.log);
    }

    public abstract static class SuperShadowing {
        @Rule
        public TestRule rule() {
            return new ExternalResource() {
                @Override
                protected void before() throws Throwable {
                    AnnotationTest.log += "super.rule().before() ";
                }

                @Override
                protected void after() {
                    AnnotationTest.log += "super.rule().after() ";
                }
            };
        }

        @Before
        public void before() {
            AnnotationTest.log += "super.before() ";
        }

        @After
        public void after() {
            AnnotationTest.log += "super.after() ";
        }
    }

    public static class SubShadowing extends AnnotationTest.SuperShadowing {
        @Override
        @Rule
        public TestRule rule() {
            return new ExternalResource() {
                @Override
                protected void before() throws Throwable {
                    AnnotationTest.log += "sub.rule().before() ";
                }

                @Override
                protected void after() {
                    AnnotationTest.log += "sub.rule().after() ";
                }
            };
        }

        @Override
        @Before
        public void before() {
            super.before();
            AnnotationTest.log += "sub.before() ";
        }

        @Before
        public void anotherBefore() {
            AnnotationTest.log += "sub.anotherBefore() ";
        }

        @Override
        @After
        public void after() {
            AnnotationTest.log += "sub.after() ";
            super.after();
        }

        @After
        public void anotherAfter() {
            AnnotationTest.log += "sub.anotherAfter() ";
        }

        @Test
        public void test() {
            AnnotationTest.log += "Test ";
        }
    }

    public void testShadowing() throws Exception {
        AnnotationTest.log = "";
        MatcherAssert.assertThat(PrintableResult.testResult(AnnotationTest.SubShadowing.class), ResultMatchers.isSuccessful());
        TestCase.assertEquals(("sub.rule().before() sub.anotherBefore() super.before() sub.before() " + ("Test " + "sub.anotherAfter() sub.after() super.after() sub.rule().after() ")), AnnotationTest.log);
    }

    public abstract static class SuperStaticMethodShadowing {
        @ClassRule
        public static TestRule rule() {
            return new ExternalResource() {
                @Override
                protected void before() throws Throwable {
                    AnnotationTest.log += "super.rule().before() ";
                }

                @Override
                protected void after() {
                    AnnotationTest.log += "super.rule().after() ";
                }
            };
        }
    }

    public static class SubStaticMethodShadowing extends AnnotationTest.SuperStaticMethodShadowing {
        @ClassRule
        public static TestRule rule() {
            return new ExternalResource() {
                @Override
                protected void before() throws Throwable {
                    AnnotationTest.log += "sub.rule().before() ";
                }

                @Override
                protected void after() {
                    AnnotationTest.log += "sub.rule().after() ";
                }
            };
        }

        @Test
        public void test() {
            AnnotationTest.log += "Test ";
        }
    }

    public void testStaticMethodsCanBeTreatedAsShadowed() throws Exception {
        AnnotationTest.log = "";
        MatcherAssert.assertThat(PrintableResult.testResult(AnnotationTest.SubStaticMethodShadowing.class), ResultMatchers.isSuccessful());
        TestCase.assertEquals(("sub.rule().before() " + ("Test " + "sub.rule().after() ")), AnnotationTest.log);
    }

    public abstract static class SuperFieldShadowing {
        @Rule
        public final TestRule rule = new ExternalResource() {
            @Override
            protected void before() throws Throwable {
                AnnotationTest.log += "super.rule.before() ";
            }

            @Override
            protected void after() {
                AnnotationTest.log += "super.rule.after() ";
            }
        };
    }

    public static class SubFieldShadowing extends AnnotationTest.SuperFieldShadowing {
        @Rule
        public final TestRule rule = new ExternalResource() {
            @Override
            protected void before() throws Throwable {
                AnnotationTest.log += "sub.rule.before() ";
            }

            @Override
            protected void after() {
                AnnotationTest.log += "sub.rule.after() ";
            }
        };

        @Test
        public void test() {
            AnnotationTest.log += "Test ";
        }
    }

    public void testFieldsNeverTreatedAsShadowed() throws Exception {
        AnnotationTest.log = "";
        MatcherAssert.assertThat(PrintableResult.testResult(AnnotationTest.SubFieldShadowing.class), ResultMatchers.isSuccessful());
        TestCase.assertEquals(("super.rule.before() sub.rule.before() " + ("Test " + "sub.rule.after() super.rule.after() ")), AnnotationTest.log);
    }

    public abstract static class SuperStaticFieldShadowing {
        @ClassRule
        public static TestRule rule = new ExternalResource() {
            @Override
            protected void before() throws Throwable {
                AnnotationTest.log += "super.rule.before() ";
            }

            @Override
            protected void after() {
                AnnotationTest.log += "super.rule.after() ";
            }
        };
    }

    public static class SubStaticFieldShadowing extends AnnotationTest.SuperStaticFieldShadowing {
        @ClassRule
        public static TestRule rule = new ExternalResource() {
            @Override
            protected void before() throws Throwable {
                AnnotationTest.log += "sub.rule.before() ";
            }

            @Override
            protected void after() {
                AnnotationTest.log += "sub.rule.after() ";
            }
        };

        @Test
        public void test() {
            AnnotationTest.log += "Test ";
        }
    }

    public void testStaticFieldsCanBeTreatedAsShadowed() throws Exception {
        AnnotationTest.log = "";
        MatcherAssert.assertThat(PrintableResult.testResult(AnnotationTest.SubStaticFieldShadowing.class), ResultMatchers.isSuccessful());
        TestCase.assertEquals(("sub.rule.before() " + ("Test " + "sub.rule.after() ")), AnnotationTest.log);
    }

    public static class SuperTest {
        @Test
        public void one() {
            AnnotationTest.log += "Super";
        }

        @Test
        public void two() {
            AnnotationTest.log += "Two";
        }
    }

    public static class SubTest extends AnnotationTest.SuperTest {
        @Override
        @Test
        public void one() {
            AnnotationTest.log += "Sub";
        }
    }

    public void testTestInheritance() throws Exception {
        AnnotationTest.log = "";
        JUnitCore core = new JUnitCore();
        core.run(AnnotationTest.SubTest.class);
        // The order in which the test methods are called is unspecified
        TestCase.assertTrue(AnnotationTest.log.contains("Sub"));
        TestCase.assertTrue(AnnotationTest.log.contains("Two"));
        TestCase.assertFalse(AnnotationTest.log.contains("Super"));
    }

    public static class RunAllAfters {
        @Before
        public void good() {
        }

        @Before
        public void bad() {
            throw new Error();
        }

        @Test
        public void empty() {
        }

        @After
        public void one() {
            AnnotationTest.log += "one";
        }

        @After
        public void two() {
            AnnotationTest.log += "two";
        }
    }

    public void testRunAllAfters() {
        AnnotationTest.log = "";
        JUnitCore core = new JUnitCore();
        core.run(AnnotationTest.RunAllAfters.class);
        TestCase.assertTrue(AnnotationTest.log.contains("one"));
        TestCase.assertTrue(AnnotationTest.log.contains("two"));
    }

    public static class RunAllAftersRegardless {
        @Test
        public void empty() {
        }

        @After
        public void one() {
            AnnotationTest.log += "one";
            throw new Error();
        }

        @After
        public void two() {
            AnnotationTest.log += "two";
            throw new Error();
        }
    }

    public void testRunAllAftersRegardless() {
        AnnotationTest.log = "";
        JUnitCore core = new JUnitCore();
        Result result = core.run(AnnotationTest.RunAllAftersRegardless.class);
        TestCase.assertTrue(AnnotationTest.log.contains("one"));
        TestCase.assertTrue(AnnotationTest.log.contains("two"));
        TestCase.assertEquals(2, result.getFailureCount());
    }

    public static class RunAllAfterClasses {
        @Before
        public void good() {
        }

        @BeforeClass
        public static void bad() {
            throw new Error();
        }

        @Test
        public void empty() {
        }

        @AfterClass
        public static void one() {
            AnnotationTest.log += "one";
        }

        @AfterClass
        public static void two() {
            AnnotationTest.log += "two";
        }
    }

    public void testRunAllAfterClasses() {
        AnnotationTest.log = "";
        JUnitCore core = new JUnitCore();
        core.run(AnnotationTest.RunAllAfterClasses.class);
        TestCase.assertTrue(AnnotationTest.log.contains("one"));
        TestCase.assertTrue(AnnotationTest.log.contains("two"));
    }

    public static class RunAllAfterClassesRegardless {
        @Test
        public void empty() {
        }

        @AfterClass
        public static void one() {
            AnnotationTest.log += "one";
            throw new Error();
        }

        @AfterClass
        public static void two() {
            AnnotationTest.log += "two";
            throw new Error();
        }
    }

    public void testRunAllAfterClassesRegardless() {
        AnnotationTest.log = "";
        JUnitCore core = new JUnitCore();
        Result result = core.run(AnnotationTest.RunAllAfterClassesRegardless.class);
        TestCase.assertTrue(AnnotationTest.log.contains("one"));
        TestCase.assertTrue(AnnotationTest.log.contains("two"));
        TestCase.assertEquals(2, result.getFailureCount());
    }
}

