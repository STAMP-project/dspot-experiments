package org.junit.rules;


import java.util.LinkedList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;


public class TestRuleTest {
    private static boolean wasRun;

    public static class ExampleTest {
        @Rule
        public TestRule example = new TestRule() {
            public Statement apply(final Statement base, Description description) {
                return new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        TestRuleTest.wasRun = true;
                        base.evaluate();
                    }
                };
            }
        };

        @Test
        public void nothing() {
        }
    }

    @Test
    public void ruleIsIntroducedAndEvaluated() {
        TestRuleTest.wasRun = false;
        JUnitCore.runClasses(TestRuleTest.ExampleTest.class);
        Assert.assertTrue(TestRuleTest.wasRun);
    }

    public static class BothKindsOfRule implements MethodRule , TestRule {
        public int applications = 0;

        public Statement apply(Statement base, FrameworkMethod method, Object target) {
            (applications)++;
            return base;
        }

        public Statement apply(Statement base, Description description) {
            (applications)++;
            return base;
        }
    }

    public static class OneFieldTwoKindsOfRule {
        @Rule
        public TestRuleTest.BothKindsOfRule both = new TestRuleTest.BothKindsOfRule();

        @Test
        public void onlyOnce() {
            Assert.assertEquals(1, both.applications);
        }
    }

    @Test
    public void onlyApplyOnceEvenIfImplementsBothInterfaces() {
        Assert.assertTrue(JUnitCore.runClasses(TestRuleTest.OneFieldTwoKindsOfRule.class).wasSuccessful());
    }

    public static class SonOfExampleTest extends TestRuleTest.ExampleTest {}

    @Test
    public void ruleIsIntroducedAndEvaluatedOnSubclass() {
        TestRuleTest.wasRun = false;
        JUnitCore.runClasses(TestRuleTest.SonOfExampleTest.class);
        Assert.assertTrue(TestRuleTest.wasRun);
    }

    private static int runCount;

    public static class MultipleRuleTest {
        private static class Increment implements TestRule {
            public Statement apply(final Statement base, Description description) {
                return new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        (TestRuleTest.runCount)++;
                        base.evaluate();
                    }
                };
            }
        }

        @Rule
        public TestRule incrementor1 = new TestRuleTest.MultipleRuleTest.Increment();

        @Rule
        public TestRule incrementor2 = new TestRuleTest.MultipleRuleTest.Increment();

        @Test
        public void nothing() {
        }
    }

    @Test
    public void multipleRulesAreRun() {
        TestRuleTest.runCount = 0;
        JUnitCore.runClasses(TestRuleTest.MultipleRuleTest.class);
        Assert.assertEquals(2, TestRuleTest.runCount);
    }

    public static class NoRulesTest {
        public int x;

        @Test
        public void nothing() {
        }
    }

    @Test
    public void ignoreNonRules() {
        Result result = JUnitCore.runClasses(TestRuleTest.NoRulesTest.class);
        Assert.assertEquals(0, result.getFailureCount());
    }

    private static String log;

    public static class BeforesAndAfters {
        private static StringBuilder watchedLog = new StringBuilder();

        @Before
        public void before() {
            TestRuleTest.BeforesAndAfters.watchedLog.append("before ");
        }

        @Rule
        public TestRule watcher = new LoggingTestWatcher(TestRuleTest.BeforesAndAfters.watchedLog);

        @After
        public void after() {
            TestRuleTest.BeforesAndAfters.watchedLog.append("after ");
        }

        @Test
        public void succeeds() {
            TestRuleTest.BeforesAndAfters.watchedLog.append("test ");
        }
    }

    @Test
    public void beforesAndAfters() {
        TestRuleTest.BeforesAndAfters.watchedLog = new StringBuilder();
        JUnitCore.runClasses(TestRuleTest.BeforesAndAfters.class);
        Assert.assertThat(TestRuleTest.BeforesAndAfters.watchedLog.toString(), CoreMatchers.is("starting before test after succeeded finished "));
    }

    public static class WrongTypedField {
        @Rule
        public int x = 5;

        @Test
        public void foo() {
        }
    }

    @Test
    public void validateWrongTypedField() {
        Assert.assertThat(PrintableResult.testResult(TestRuleTest.WrongTypedField.class), ResultMatchers.hasSingleFailureContaining("must implement MethodRule"));
    }

    public static class SonOfWrongTypedField extends TestRuleTest.WrongTypedField {}

    @Test
    public void validateWrongTypedFieldInSuperclass() {
        Assert.assertThat(PrintableResult.testResult(TestRuleTest.SonOfWrongTypedField.class), ResultMatchers.hasSingleFailureContaining("must implement MethodRule"));
    }

    public static class PrivateRule {
        @Rule
        private TestRule rule = new TestName();

        @Test
        public void foo() {
        }
    }

    @Test
    public void validatePrivateRule() {
        Assert.assertThat(PrintableResult.testResult(TestRuleTest.PrivateRule.class), ResultMatchers.hasSingleFailureContaining("must be public"));
    }

    public static class CustomTestName implements TestRule {
        public String name = null;

        public Statement apply(final Statement base, final Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    name = description.getMethodName();
                    base.evaluate();
                }
            };
        }
    }

    public static class UsesCustomMethodRule {
        @Rule
        public TestRuleTest.CustomTestName counter = new TestRuleTest.CustomTestName();

        @Test
        public void foo() {
            Assert.assertEquals("foo", counter.name);
        }
    }

    @Test
    public void useCustomMethodRule() {
        Assert.assertThat(PrintableResult.testResult(TestRuleTest.UsesCustomMethodRule.class), ResultMatchers.isSuccessful());
    }

    public static class MethodExampleTest {
        private TestRule example = new TestRule() {
            public Statement apply(final Statement base, Description description) {
                return new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        TestRuleTest.wasRun = true;
                        base.evaluate();
                    }
                };
            }
        };

        @Rule
        public TestRule getExample() {
            return example;
        }

        @Test
        public void nothing() {
        }
    }

    @Test
    public void methodRuleIsIntroducedAndEvaluated() {
        TestRuleTest.wasRun = false;
        JUnitCore.runClasses(TestRuleTest.MethodExampleTest.class);
        Assert.assertTrue(TestRuleTest.wasRun);
    }

    public static class MethodBothKindsOfRule implements MethodRule , TestRule {
        public int applications = 0;

        public Statement apply(Statement base, FrameworkMethod method, Object target) {
            (applications)++;
            return base;
        }

        public Statement apply(Statement base, Description description) {
            (applications)++;
            return base;
        }
    }

    public static class MethodOneFieldTwoKindsOfRule {
        private TestRuleTest.MethodBothKindsOfRule both = new TestRuleTest.MethodBothKindsOfRule();

        @Rule
        public TestRuleTest.MethodBothKindsOfRule getBoth() {
            return both;
        }

        @Test
        public void onlyOnce() {
            Assert.assertEquals(1, both.applications);
        }
    }

    @Test
    public void methodOnlyApplyOnceEvenIfImplementsBothInterfaces() {
        Assert.assertTrue(JUnitCore.runClasses(TestRuleTest.MethodOneFieldTwoKindsOfRule.class).wasSuccessful());
    }

    public static class MethodSonOfExampleTest extends TestRuleTest.MethodExampleTest {}

    @Test
    public void methodRuleIsIntroducedAndEvaluatedOnSubclass() {
        TestRuleTest.wasRun = false;
        JUnitCore.runClasses(TestRuleTest.MethodSonOfExampleTest.class);
        Assert.assertTrue(TestRuleTest.wasRun);
    }

    public static class MethodMultipleRuleTest {
        private static class Increment implements TestRule {
            public Statement apply(final Statement base, Description description) {
                return new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        (TestRuleTest.runCount)++;
                        base.evaluate();
                    }
                };
            }
        }

        private TestRule incrementor1 = new TestRuleTest.MethodMultipleRuleTest.Increment();

        @Rule
        public TestRule getIncrementor1() {
            return incrementor1;
        }

        private TestRule incrementor2 = new TestRuleTest.MethodMultipleRuleTest.Increment();

        @Rule
        public TestRule getIncrementor2() {
            return incrementor2;
        }

        @Test
        public void nothing() {
        }
    }

    @Test
    public void methodMultipleRulesAreRun() {
        TestRuleTest.runCount = 0;
        JUnitCore.runClasses(TestRuleTest.MethodMultipleRuleTest.class);
        Assert.assertEquals(2, TestRuleTest.runCount);
    }

    public static class MethodNoRulesTest {
        public int x;

        @Test
        public void nothing() {
        }
    }

    @Test
    public void methodIgnoreNonRules() {
        Result result = JUnitCore.runClasses(TestRuleTest.MethodNoRulesTest.class);
        Assert.assertEquals(0, result.getFailureCount());
    }

    public static class BeforesAndAftersAreEnclosedByRule {
        private static StringBuilder log;

        @Rule
        public TestRule watcher = new LoggingTestWatcher(TestRuleTest.BeforesAndAftersAreEnclosedByRule.log);

        @Before
        public void before() {
            TestRuleTest.BeforesAndAftersAreEnclosedByRule.log.append("before ");
        }

        @After
        public void after() {
            TestRuleTest.BeforesAndAftersAreEnclosedByRule.log.append("after ");
        }

        @Test
        public void succeeds() {
            TestRuleTest.BeforesAndAftersAreEnclosedByRule.log.append("test ");
        }
    }

    @Test
    public void beforesAndAftersAreEnclosedByRule() {
        TestRuleTest.BeforesAndAftersAreEnclosedByRule.log = new StringBuilder();
        JUnitCore.runClasses(TestRuleTest.BeforesAndAftersAreEnclosedByRule.class);
        Assert.assertEquals("starting before test after succeeded finished ", TestRuleTest.BeforesAndAftersAreEnclosedByRule.log.toString());
    }

    public static class MethodWrongTypedField {
        @Rule
        public int getX() {
            return 5;
        }

        @Test
        public void foo() {
        }
    }

    @Test
    public void methodValidateWrongTypedField() {
        Assert.assertThat(PrintableResult.testResult(TestRuleTest.MethodWrongTypedField.class), ResultMatchers.hasSingleFailureContaining("must return an implementation of MethodRule"));
    }

    public static class MethodSonOfWrongTypedField extends TestRuleTest.MethodWrongTypedField {}

    @Test
    public void methodValidateWrongTypedFieldInSuperclass() {
        Assert.assertThat(PrintableResult.testResult(TestRuleTest.MethodSonOfWrongTypedField.class), ResultMatchers.hasSingleFailureContaining("must return an implementation of MethodRule"));
    }

    public static class MethodPrivateRule {
        @Rule
        private TestRule getRule() {
            return new TestName();
        }

        @Test
        public void foo() {
        }
    }

    @Test
    public void methodValidatePrivateRule() {
        Assert.assertThat(PrintableResult.testResult(TestRuleTest.MethodPrivateRule.class), ResultMatchers.hasSingleFailureContaining("must be public"));
    }

    public static class MethodUsesCustomMethodRule {
        private TestRuleTest.CustomTestName counter = new TestRuleTest.CustomTestName();

        @Rule
        public TestRuleTest.CustomTestName getCounter() {
            return counter;
        }

        @Test
        public void foo() {
            Assert.assertEquals("foo", counter.name);
        }
    }

    @Test
    public void methodUseCustomMethodRule() {
        Assert.assertThat(PrintableResult.testResult(TestRuleTest.MethodUsesCustomMethodRule.class), ResultMatchers.isSuccessful());
    }

    private static final List<String> orderList = new LinkedList<String>();

    private static class OrderTestRule implements TestRule {
        private String name;

        public OrderTestRule(String name) {
            this.name = name;
        }

        public Statement apply(final Statement base, final Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    TestRuleTest.orderList.add(name);
                    base.evaluate();
                }
            };
        }
    }

    public static class UsesFieldAndMethodRule {
        @Rule
        public TestRuleTest.OrderTestRule orderMethod() {
            return new TestRuleTest.OrderTestRule("orderMethod");
        }

        @Rule
        public TestRuleTest.OrderTestRule orderField = new TestRuleTest.OrderTestRule("orderField");

        @Test
        public void foo() {
            Assert.assertEquals("orderField", TestRuleTest.orderList.get(0));
            Assert.assertEquals("orderMethod", TestRuleTest.orderList.get(1));
        }
    }

    @Test
    public void usesFieldAndMethodRule() {
        TestRuleTest.orderList.clear();
        Assert.assertThat(PrintableResult.testResult(TestRuleTest.UsesFieldAndMethodRule.class), ResultMatchers.isSuccessful());
    }

    public static class CallMethodOnlyOnceRule {
        int countOfMethodCalls = 0;

        private static class Dummy implements TestRule {
            public Statement apply(final Statement base, Description description) {
                return new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        base.evaluate();
                    }
                };
            }
        }

        @Rule
        public TestRuleTest.CallMethodOnlyOnceRule.Dummy both() {
            (countOfMethodCalls)++;
            return new TestRuleTest.CallMethodOnlyOnceRule.Dummy();
        }

        @Test
        public void onlyOnce() {
            Assert.assertEquals(1, countOfMethodCalls);
        }
    }

    @Test
    public void testCallMethodOnlyOnceRule() {
        Assert.assertTrue(JUnitCore.runClasses(TestRuleTest.CallMethodOnlyOnceRule.class).wasSuccessful());
    }

    private static final StringBuilder ruleLog = new StringBuilder();

    public static class TestRuleIsAroundMethodRule {
        @Rule
        public final MethodRule z = new LoggingMethodRule(TestRuleTest.ruleLog, "methodRule");

        @Rule
        public final TestRule a = new LoggingTestRule(TestRuleTest.ruleLog, "testRule");

        @Test
        public void foo() {
            TestRuleTest.ruleLog.append(" foo");
        }
    }

    @Test
    public void testRuleIsAroundMethodRule() {
        TestRuleTest.ruleLog.setLength(0);
        Result result = JUnitCore.runClasses(TestRuleTest.TestRuleIsAroundMethodRule.class);
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertEquals(" testRule.begin methodRule.begin foo methodRule.end testRule.end", TestRuleTest.ruleLog.toString());
    }

    public static class TestRuleOrdering {
        @Rule(order = 1)
        public final TestRule a = new LoggingTestRule(TestRuleTest.ruleLog, "outer");

        @Rule(order = 2)
        public final TestRule z = new LoggingTestRule(TestRuleTest.ruleLog, "inner");

        @Test
        public void foo() {
            TestRuleTest.ruleLog.append(" foo");
        }
    }

    @Test
    public void testRuleOrdering() {
        TestRuleTest.ruleLog.setLength(0);
        Result result = JUnitCore.runClasses(TestRuleTest.TestRuleOrdering.class);
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertEquals(" outer.begin inner.begin foo inner.end outer.end", TestRuleTest.ruleLog.toString());
    }

    public static class TestRuleOrderingWithMethodRule {
        @Rule(order = 1)
        public final MethodRule z = new LoggingMethodRule(TestRuleTest.ruleLog, "methodRule");

        @Rule(order = 2)
        public final TestRule a = new LoggingTestRule(TestRuleTest.ruleLog, "testRule");

        @Test
        public void foo() {
            TestRuleTest.ruleLog.append(" foo");
        }
    }

    @Test
    public void testRuleOrderingWithMethodRule() {
        TestRuleTest.ruleLog.setLength(0);
        Result result = JUnitCore.runClasses(TestRuleTest.TestRuleOrderingWithMethodRule.class);
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertEquals(" methodRule.begin testRule.begin foo testRule.end methodRule.end", TestRuleTest.ruleLog.toString());
    }
}

