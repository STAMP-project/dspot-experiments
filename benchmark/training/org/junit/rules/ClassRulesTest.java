/**
 * Created Oct 19, 2009
 */
package org.junit.rules;


import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.MethodSorters;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;


/**
 * Tests to exercise class-level rules.
 */
public class ClassRulesTest {
    public static class Counter extends ExternalResource {
        public int count = 0;

        @Override
        protected void before() throws Throwable {
            (count)++;
        }
    }

    public static class ExampleTestWithClassRule {
        @ClassRule
        public static ClassRulesTest.Counter counter = new ClassRulesTest.Counter();

        @Test
        public void firstTest() {
            Assert.assertEquals(1, ClassRulesTest.ExampleTestWithClassRule.counter.count);
        }

        @Test
        public void secondTest() {
            Assert.assertEquals(1, ClassRulesTest.ExampleTestWithClassRule.counter.count);
        }
    }

    @Test
    public void ruleIsAppliedOnce() {
        ClassRulesTest.ExampleTestWithClassRule.counter.count = 0;
        JUnitCore.runClasses(ClassRulesTest.ExampleTestWithClassRule.class);
        Assert.assertEquals(1, ClassRulesTest.ExampleTestWithClassRule.counter.count);
    }

    public static class SubclassOfTestWithClassRule extends ClassRulesTest.ExampleTestWithClassRule {}

    @Test
    public void ruleIsIntroducedAndEvaluatedOnSubclass() {
        ClassRulesTest.ExampleTestWithClassRule.counter.count = 0;
        JUnitCore.runClasses(ClassRulesTest.SubclassOfTestWithClassRule.class);
        Assert.assertEquals(1, ClassRulesTest.ExampleTestWithClassRule.counter.count);
    }

    public static class CustomCounter implements TestRule {
        public int count = 0;

        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    (count)++;
                    base.evaluate();
                }
            };
        }
    }

    public static class ExampleTestWithCustomClassRule {
        @ClassRule
        public static ClassRulesTest.CustomCounter counter = new ClassRulesTest.CustomCounter();

        @Test
        public void firstTest() {
            Assert.assertEquals(1, ClassRulesTest.ExampleTestWithCustomClassRule.counter.count);
        }

        @Test
        public void secondTest() {
            Assert.assertEquals(1, ClassRulesTest.ExampleTestWithCustomClassRule.counter.count);
        }
    }

    @Test
    public void customRuleIsAppliedOnce() {
        ClassRulesTest.ExampleTestWithCustomClassRule.counter.count = 0;
        Result result = JUnitCore.runClasses(ClassRulesTest.ExampleTestWithCustomClassRule.class);
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertEquals(1, ClassRulesTest.ExampleTestWithCustomClassRule.counter.count);
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
                    ClassRulesTest.orderList.add(name);
                    base.evaluate();
                }
            };
        }
    }

    public static class UsesFieldAndMethodRule {
        @ClassRule
        public static ClassRulesTest.OrderTestRule orderMethod() {
            return new ClassRulesTest.OrderTestRule("orderMethod");
        }

        @ClassRule
        public static ClassRulesTest.OrderTestRule orderField = new ClassRulesTest.OrderTestRule("orderField");

        @Test
        public void foo() {
            Assert.assertEquals("orderField", ClassRulesTest.orderList.get(0));
            Assert.assertEquals("orderMethod", ClassRulesTest.orderList.get(1));
        }
    }

    @Test
    public void usesFieldAndMethodRule() {
        ClassRulesTest.orderList.clear();
        Assert.assertThat(PrintableResult.testResult(ClassRulesTest.UsesFieldAndMethodRule.class), ResultMatchers.isSuccessful());
    }

    public static class MethodExampleTestWithClassRule {
        private static ClassRulesTest.Counter counter = new ClassRulesTest.Counter();

        @ClassRule
        public static ClassRulesTest.Counter getCounter() {
            return ClassRulesTest.MethodExampleTestWithClassRule.counter;
        }

        @Test
        public void firstTest() {
            Assert.assertEquals(1, ClassRulesTest.MethodExampleTestWithClassRule.counter.count);
        }

        @Test
        public void secondTest() {
            Assert.assertEquals(1, ClassRulesTest.MethodExampleTestWithClassRule.counter.count);
        }
    }

    @Test
    public void methodRuleIsAppliedOnce() {
        ClassRulesTest.MethodExampleTestWithClassRule.counter.count = 0;
        JUnitCore.runClasses(ClassRulesTest.MethodExampleTestWithClassRule.class);
        Assert.assertEquals(1, ClassRulesTest.MethodExampleTestWithClassRule.counter.count);
    }

    public static class MethodSubclassOfTestWithClassRule extends ClassRulesTest.MethodExampleTestWithClassRule {}

    @Test
    public void methodRuleIsIntroducedAndEvaluatedOnSubclass() {
        ClassRulesTest.MethodExampleTestWithClassRule.counter.count = 0;
        JUnitCore.runClasses(ClassRulesTest.MethodSubclassOfTestWithClassRule.class);
        Assert.assertEquals(1, ClassRulesTest.MethodExampleTestWithClassRule.counter.count);
    }

    public static class MethodExampleTestWithCustomClassRule {
        private static ClassRulesTest.CustomCounter counter = new ClassRulesTest.CustomCounter();

        @ClassRule
        public static ClassRulesTest.CustomCounter getCounter() {
            return ClassRulesTest.MethodExampleTestWithCustomClassRule.counter;
        }

        @Test
        public void firstTest() {
            Assert.assertEquals(1, ClassRulesTest.MethodExampleTestWithCustomClassRule.counter.count);
        }

        @Test
        public void secondTest() {
            Assert.assertEquals(1, ClassRulesTest.MethodExampleTestWithCustomClassRule.counter.count);
        }
    }

    @Test
    public void methodCustomRuleIsAppliedOnce() {
        ClassRulesTest.MethodExampleTestWithCustomClassRule.counter.count = 0;
        Result result = JUnitCore.runClasses(ClassRulesTest.MethodExampleTestWithCustomClassRule.class);
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertEquals(1, ClassRulesTest.MethodExampleTestWithCustomClassRule.counter.count);
    }

    public static class CallMethodOnlyOnceRule {
        static int countOfMethodCalls = 0;

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

        @ClassRule
        public static ClassRulesTest.CallMethodOnlyOnceRule.Dummy both() {
            (ClassRulesTest.CallMethodOnlyOnceRule.countOfMethodCalls)++;
            return new ClassRulesTest.CallMethodOnlyOnceRule.Dummy();
        }

        @Test
        public void onlyOnce() {
            Assert.assertEquals(1, ClassRulesTest.CallMethodOnlyOnceRule.countOfMethodCalls);
        }
    }

    @Test
    public void testCallMethodOnlyOnceRule() {
        ClassRulesTest.CallMethodOnlyOnceRule.countOfMethodCalls = 0;
        Assert.assertTrue(JUnitCore.runClasses(ClassRulesTest.CallMethodOnlyOnceRule.class).wasSuccessful());
    }

    private static final StringBuilder log = new StringBuilder();

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class ClassRuleOrdering {
        @ClassRule(order = 1)
        public static TestRule a() {
            return new LoggingTestRule(ClassRulesTest.log, "outer");
        }

        @ClassRule(order = 2)
        public static TestRule z() {
            return new LoggingTestRule(ClassRulesTest.log, "inner");
        }

        @Test
        public void foo() {
            ClassRulesTest.log.append(" foo");
        }

        @Test
        public void bar() {
            ClassRulesTest.log.append(" bar");
        }
    }

    @Test
    public void classRuleOrdering() {
        ClassRulesTest.log.setLength(0);
        Result result = JUnitCore.runClasses(ClassRulesTest.ClassRuleOrdering.class);
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertEquals(" outer.begin inner.begin bar foo inner.end outer.end", ClassRulesTest.log.toString());
    }

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class ClassRuleOrderingDefault {
        @ClassRule
        public static TestRule a() {
            return new LoggingTestRule(ClassRulesTest.log, "outer");
        }

        @ClassRule
        public static TestRule b() {
            return new LoggingTestRule(ClassRulesTest.log, "inner");
        }

        @Test
        public void foo() {
            ClassRulesTest.log.append(" foo");
        }

        @Test
        public void bar() {
            ClassRulesTest.log.append(" bar");
        }
    }

    @Test
    public void classRuleOrderingDefault() {
        ClassRulesTest.log.setLength(0);
        Result result = JUnitCore.runClasses(ClassRulesTest.ClassRuleOrderingDefault.class);
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertEquals(" inner.begin outer.begin bar foo outer.end inner.end", ClassRulesTest.log.toString());
    }

    public static class RunnerWithClassRuleAddedProgrammatically extends BlockJUnit4ClassRunner {
        public RunnerWithClassRuleAddedProgrammatically(Class testClass) throws InitializationError {
            super(testClass);
        }

        @Override
        protected List<TestRule> classRules() {
            final List<TestRule> rules = super.classRules();
            rules.add(new LoggingTestRule(ClassRulesTest.log, "fromCode"));
            return rules;
        }
    }

    @RunWith(ClassRulesTest.RunnerWithClassRuleAddedProgrammatically.class)
    public static class ClassRulesModifiableListEmpty {
        @Test
        public void test() {
            ClassRulesTest.log.append(" test");
        }
    }

    @Test
    public void classRulesModifiableListEmpty() {
        ClassRulesTest.log.setLength(0);
        Result result = JUnitCore.runClasses(ClassRulesTest.ClassRulesModifiableListEmpty.class);
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertEquals(" fromCode.begin test fromCode.end", ClassRulesTest.log.toString());
    }

    @RunWith(ClassRulesTest.RunnerWithClassRuleAddedProgrammatically.class)
    public static class ClassRulesModifiableList {
        @ClassRule
        public static TestRule classRule() {
            return new LoggingTestRule(ClassRulesTest.log, "classRule");
        }

        @Test
        public void test() {
            ClassRulesTest.log.append(" test");
        }
    }

    @Test
    public void classRulesModifiableList() {
        ClassRulesTest.log.setLength(0);
        Result result = JUnitCore.runClasses(ClassRulesTest.ClassRulesModifiableList.class);
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertEquals(" fromCode.begin classRule.begin test classRule.end fromCode.end", ClassRulesTest.log.toString());
    }
}

