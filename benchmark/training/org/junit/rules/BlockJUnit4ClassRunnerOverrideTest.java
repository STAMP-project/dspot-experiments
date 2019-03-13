package org.junit.rules;


import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;


public class BlockJUnit4ClassRunnerOverrideTest {
    public static class FlipBitRule implements MethodRule {
        public Statement apply(final Statement base, FrameworkMethod method, final Object target) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    target.getClass().getField("flipBit").set(target, true);
                    base.evaluate();
                }
            };
        }
    }

    public static class OverrideRulesRunner extends BlockJUnit4ClassRunner {
        public OverrideRulesRunner(Class<?> klass) throws InitializationError {
            super(klass);
        }

        @Override
        protected List<MethodRule> rules(Object test) {
            final LinkedList<MethodRule> methodRules = new LinkedList<MethodRule>(super.rules(test));
            methodRules.add(new BlockJUnit4ClassRunnerOverrideTest.FlipBitRule());
            return methodRules;
        }
    }

    @RunWith(BlockJUnit4ClassRunnerOverrideTest.OverrideRulesRunner.class)
    public static class OverrideRulesTest {
        public boolean flipBit = false;

        @Test
        public void testFlipBit() {
            Assert.assertTrue(flipBit);
        }
    }

    @Test
    public void overrideRulesMethod() {
        Assert.assertThat(PrintableResult.testResult(BlockJUnit4ClassRunnerOverrideTest.OverrideTestRulesTest.class), ResultMatchers.isSuccessful());
    }

    public static class OverrideTestRulesRunner extends BlockJUnit4ClassRunner {
        public OverrideTestRulesRunner(Class<?> klass) throws InitializationError {
            super(klass);
        }

        @Override
        protected List<TestRule> getTestRules(final Object test) {
            final LinkedList<TestRule> methodRules = new LinkedList<TestRule>(super.getTestRules(test));
            methodRules.add(new TestRule() {
                public Statement apply(Statement base, Description description) {
                    return new BlockJUnit4ClassRunnerOverrideTest.FlipBitRule().apply(base, null, test);
                }
            });
            return methodRules;
        }
    }

    @RunWith(BlockJUnit4ClassRunnerOverrideTest.OverrideTestRulesRunner.class)
    public static class OverrideTestRulesTest extends BlockJUnit4ClassRunnerOverrideTest.OverrideRulesTest {}

    @Test
    public void overrideTestRulesMethod() {
        Assert.assertThat(PrintableResult.testResult(BlockJUnit4ClassRunnerOverrideTest.OverrideRulesTest.class), ResultMatchers.isSuccessful());
    }

    /**
     * Runner for testing override of {@link org.junit.runners.BlockJUnit4ClassRunner#createTest(org.junit.runners.model.FrameworkMethod)}
     * by setting the {@link org.junit.runners.model.FrameworkMethod} in a field
     * of the test class so it can be compared with the test method that is being
     * executed.
     */
    public static class OverrideCreateTestRunner extends BlockJUnit4ClassRunner {
        public OverrideCreateTestRunner(final Class<?> klass) throws InitializationError {
            super(klass);
            assert klass.equals(BlockJUnit4ClassRunnerOverrideTest.OverrideCreateTest.class);
        }

        @Override
        protected Object createTest(FrameworkMethod method) {
            final BlockJUnit4ClassRunnerOverrideTest.OverrideCreateTest obj = new BlockJUnit4ClassRunnerOverrideTest.OverrideCreateTest();
            obj.method = method;
            return obj;
        }
    }

    @RunWith(BlockJUnit4ClassRunnerOverrideTest.OverrideCreateTestRunner.class)
    public static class OverrideCreateTest {
        public FrameworkMethod method;

        @Test
        public void testMethodA() {
            Assert.assertEquals("testMethodA", method.getMethod().getName());
        }

        @Test
        public void testMethodB() {
            Assert.assertEquals("testMethodB", method.getMethod().getName());
        }
    }

    @Test
    public void overrideCreateTestMethod() {
        Assert.assertThat(PrintableResult.testResult(BlockJUnit4ClassRunnerOverrideTest.OverrideCreateTest.class), ResultMatchers.isSuccessful());
    }

    /**
     * Runner for testing override of {@link org.junit.runners.BlockJUnit4ClassRunner#createTest()}
     * is still called by default if no other {@code createTest} method override
     * is in place. This is tested by setting a boolean flag in a field of the
     * test class so it can be checked to confirm that the createTest method was
     * called.
     */
    public static class CreateTestDefersToNoArgCreateTestRunner extends BlockJUnit4ClassRunner {
        public CreateTestDefersToNoArgCreateTestRunner(final Class<?> klass) throws InitializationError {
            super(klass);
            assert klass.equals(BlockJUnit4ClassRunnerOverrideTest.CreateTestDefersToNoArgCreateTestTest.class);
        }

        @Override
        protected Object createTest() {
            final BlockJUnit4ClassRunnerOverrideTest.CreateTestDefersToNoArgCreateTestTest obj = new BlockJUnit4ClassRunnerOverrideTest.CreateTestDefersToNoArgCreateTestTest();
            obj.createTestCalled = true;
            return obj;
        }
    }

    @RunWith(BlockJUnit4ClassRunnerOverrideTest.CreateTestDefersToNoArgCreateTestRunner.class)
    public static class CreateTestDefersToNoArgCreateTestTest {
        public boolean createTestCalled = false;

        @Test
        public void testCreateTestCalled() {
            Assert.assertEquals(true, createTestCalled);
        }
    }

    @Test
    public void createTestDefersToNoArgCreateTest() {
        Assert.assertThat(PrintableResult.testResult(BlockJUnit4ClassRunnerOverrideTest.CreateTestDefersToNoArgCreateTestTest.class), ResultMatchers.isSuccessful());
    }
}

