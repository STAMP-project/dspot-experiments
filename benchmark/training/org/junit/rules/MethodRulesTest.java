package org.junit.rules;


import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;


@SuppressWarnings("deprecation")
public class MethodRulesTest {
    private static boolean ruleWasEvaluated;

    private static class TestMethodRule implements MethodRule {
        public Statement apply(final Statement base, FrameworkMethod method, Object target) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    MethodRulesTest.ruleWasEvaluated = true;
                    base.evaluate();
                }
            };
        }
    }

    public static class ExampleTest {
        @Rule
        public MethodRule example = new MethodRulesTest.TestMethodRule();

        @Test
        public void nothing() {
        }
    }

    abstract static class NonPublicExampleTest {
        @Rule
        public MethodRule example = new MethodRulesTest.TestMethodRule();

        @Test
        public void nothing() {
        }
    }

    @Test
    public void ruleIsIntroducedAndEvaluated() {
        MethodRulesTest.ruleWasEvaluated = false;
        Assert.assertThat(PrintableResult.testResult(MethodRulesTest.ExampleTest.class), ResultMatchers.isSuccessful());
        Assert.assertTrue(MethodRulesTest.ruleWasEvaluated);
    }

    public static class SonOfExampleTest extends MethodRulesTest.ExampleTest {}

    @Test
    public void ruleIsIntroducedAndEvaluatedOnSubclass() {
        MethodRulesTest.ruleWasEvaluated = false;
        Assert.assertThat(PrintableResult.testResult(MethodRulesTest.SonOfExampleTest.class), ResultMatchers.isSuccessful());
        Assert.assertTrue(MethodRulesTest.ruleWasEvaluated);
    }

    public static class SonOfNonPublicExampleTest extends MethodRulesTest.NonPublicExampleTest {}

    @Test
    public void ruleIsIntroducedAndEvaluatedOnSubclassOfNonPublicClass() {
        MethodRulesTest.ruleWasEvaluated = false;
        Assert.assertThat(PrintableResult.testResult(MethodRulesTest.SonOfNonPublicExampleTest.class), ResultMatchers.isSuccessful());
        Assert.assertTrue(MethodRulesTest.ruleWasEvaluated);
    }

    private static int runCount;

    private static class Increment implements MethodRule {
        public Statement apply(final Statement base, FrameworkMethod method, Object target) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    (MethodRulesTest.runCount)++;
                    base.evaluate();
                }
            };
        }
    }

    public static class MultipleRuleTest {
        @Rule
        public MethodRule incrementor1 = new MethodRulesTest.Increment();

        @Rule
        public MethodRule incrementor2 = new MethodRulesTest.Increment();

        @Test
        public void nothing() {
        }
    }

    @Test
    public void multipleRulesAreRun() {
        MethodRulesTest.runCount = 0;
        Assert.assertThat(PrintableResult.testResult(MethodRulesTest.MultipleRuleTest.class), ResultMatchers.isSuccessful());
        Assert.assertEquals(2, MethodRulesTest.runCount);
    }

    public static class NoRulesTest {
        public int x;

        @Test
        public void nothing() {
        }
    }

    @Test
    public void ignoreNonRules() {
        Assert.assertThat(PrintableResult.testResult(MethodRulesTest.NoRulesTest.class), ResultMatchers.isSuccessful());
    }

    private static String log;

    public static class OnFailureTest {
        @Rule
        public MethodRule watchman = new TestWatchman() {
            @Override
            public void failed(Throwable e, FrameworkMethod method) {
                MethodRulesTest.log += ((method.getName()) + " ") + (e.getClass().getSimpleName());
            }
        };

        @Test
        public void nothing() {
            Assert.fail();
        }
    }

    @Test
    public void onFailure() {
        MethodRulesTest.log = "";
        Assert.assertThat(PrintableResult.testResult(MethodRulesTest.OnFailureTest.class), ResultMatchers.failureCountIs(1));
        Assert.assertEquals("nothing AssertionError", MethodRulesTest.log);
    }

    public static class WatchmanTest {
        private static String watchedLog;

        @Rule
        public MethodRule watchman = new TestWatchman() {
            @Override
            public void failed(Throwable e, FrameworkMethod method) {
                MethodRulesTest.WatchmanTest.watchedLog += (((method.getName()) + " ") + (e.getClass().getSimpleName())) + "\n";
            }

            @Override
            public void succeeded(FrameworkMethod method) {
                MethodRulesTest.WatchmanTest.watchedLog += ((method.getName()) + " ") + "success!\n";
            }
        };

        @Test
        public void fails() {
            Assert.fail();
        }

        @Test
        public void succeeds() {
        }
    }

    @Test
    public void succeeded() {
        MethodRulesTest.WatchmanTest.watchedLog = "";
        Assert.assertThat(PrintableResult.testResult(MethodRulesTest.WatchmanTest.class), ResultMatchers.failureCountIs(1));
        Assert.assertThat(MethodRulesTest.WatchmanTest.watchedLog, CoreMatchers.containsString("fails AssertionError"));
        Assert.assertThat(MethodRulesTest.WatchmanTest.watchedLog, CoreMatchers.containsString("succeeds success!"));
    }

    public static class BeforesAndAfters {
        private static String watchedLog;

        @Before
        public void before() {
            MethodRulesTest.BeforesAndAfters.watchedLog += "before ";
        }

        @Rule
        public MethodRule watchman = new TestWatchman() {
            @Override
            public void starting(FrameworkMethod method) {
                MethodRulesTest.BeforesAndAfters.watchedLog += "starting ";
            }

            @Override
            public void finished(FrameworkMethod method) {
                MethodRulesTest.BeforesAndAfters.watchedLog += "finished ";
            }

            @Override
            public void succeeded(FrameworkMethod method) {
                MethodRulesTest.BeforesAndAfters.watchedLog += "succeeded ";
            }
        };

        @After
        public void after() {
            MethodRulesTest.BeforesAndAfters.watchedLog += "after ";
        }

        @Test
        public void succeeds() {
            MethodRulesTest.BeforesAndAfters.watchedLog += "test ";
        }
    }

    @Test
    public void beforesAndAfters() {
        MethodRulesTest.BeforesAndAfters.watchedLog = "";
        Assert.assertThat(PrintableResult.testResult(MethodRulesTest.BeforesAndAfters.class), ResultMatchers.isSuccessful());
        Assert.assertThat(MethodRulesTest.BeforesAndAfters.watchedLog, CoreMatchers.is("starting before test after succeeded finished "));
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
        Assert.assertThat(PrintableResult.testResult(MethodRulesTest.WrongTypedField.class), ResultMatchers.hasSingleFailureContaining("must implement MethodRule"));
    }

    public static class SonOfWrongTypedField extends MethodRulesTest.WrongTypedField {}

    @Test
    public void validateWrongTypedFieldInSuperclass() {
        Assert.assertThat(PrintableResult.testResult(MethodRulesTest.SonOfWrongTypedField.class), ResultMatchers.hasSingleFailureContaining("must implement MethodRule"));
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
        Assert.assertThat(PrintableResult.testResult(MethodRulesTest.PrivateRule.class), ResultMatchers.hasSingleFailureContaining("must be public"));
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
        public MethodRulesTest.CustomTestName counter = new MethodRulesTest.CustomTestName();

        @Test
        public void foo() {
            Assert.assertEquals("foo", counter.name);
        }
    }

    @Test
    public void useCustomMethodRule() {
        Assert.assertThat(PrintableResult.testResult(MethodRulesTest.UsesCustomMethodRule.class), ResultMatchers.isSuccessful());
    }

    public static class HasMethodReturningMethodRule {
        private MethodRule methodRule = new MethodRule() {
            public Statement apply(final Statement base, FrameworkMethod method, Object target) {
                return new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        MethodRulesTest.ruleWasEvaluated = true;
                        base.evaluate();
                    }
                };
            }
        };

        @Rule
        public MethodRule methodRule() {
            return methodRule;
        }

        @Test
        public void doNothing() {
        }
    }

    /**
     * If there are any public methods annotated with @Rule returning a {@link MethodRule}
     * then it should also be run.
     *
     * <p>This case has been added with
     * <a href="https://github.com/junit-team/junit4/issues/589">Issue #589</a> -
     * Support @Rule for methods works only for TestRule but not for MethodRule
     */
    @Test
    public void runsMethodRuleThatIsReturnedByMethod() {
        MethodRulesTest.ruleWasEvaluated = false;
        Assert.assertThat(PrintableResult.testResult(MethodRulesTest.HasMethodReturningMethodRule.class), ResultMatchers.isSuccessful());
        Assert.assertTrue(MethodRulesTest.ruleWasEvaluated);
    }

    public static class HasMultipleMethodsReturningMethodRule {
        @Rule
        public MethodRulesTest.Increment methodRule1() {
            return new MethodRulesTest.Increment();
        }

        @Rule
        public MethodRulesTest.Increment methodRule2() {
            return new MethodRulesTest.Increment();
        }

        @Test
        public void doNothing() {
        }
    }

    /**
     * If there are multiple public methods annotated with @Rule returning a {@link MethodRule}
     * then all the rules returned should be run.
     *
     * <p>This case has been added with
     * <a href="https://github.com/junit-team/junit4/issues/589">Issue #589</a> -
     * Support @Rule for methods works only for TestRule but not for MethodRule
     */
    @Test
    public void runsAllMethodRulesThatAreReturnedByMethods() {
        MethodRulesTest.runCount = 0;
        Assert.assertThat(PrintableResult.testResult(MethodRulesTest.HasMultipleMethodsReturningMethodRule.class), ResultMatchers.isSuccessful());
        Assert.assertEquals(2, MethodRulesTest.runCount);
    }

    public static class CallsMethodReturningRuleOnlyOnce {
        int callCount = 0;

        private static class Dummy implements MethodRule {
            public Statement apply(final Statement base, FrameworkMethod method, Object target) {
                return new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        base.evaluate();
                    }
                };
            }
        }

        @Rule
        public MethodRule methodRule() {
            (callCount)++;
            return new MethodRulesTest.CallsMethodReturningRuleOnlyOnce.Dummy();
        }

        @Test
        public void doNothing() {
            Assert.assertEquals(1, callCount);
        }
    }

    /**
     * If there are any public methods annotated with @Rule returning a {@link MethodRule}
     * then method should be called only once.
     *
     * <p>This case has been added with
     * <a href="https://github.com/junit-team/junit4/issues/589">Issue #589</a> -
     * Support @Rule for methods works only for TestRule but not for MethodRule
     */
    @Test
    public void callsMethodReturningRuleOnlyOnce() {
        Assert.assertThat(PrintableResult.testResult(MethodRulesTest.CallsMethodReturningRuleOnlyOnce.class), ResultMatchers.isSuccessful());
    }
}

