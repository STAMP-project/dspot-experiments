package org.junit.tests.running.methods;


import java.util.List;
import junit.framework.JUnit4TestAdapter;
import junit.framework.TestResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;


public class TestMethodTest {
    @SuppressWarnings("all")
    public static class EverythingWrong {
        private EverythingWrong() {
        }

        @BeforeClass
        public void notStaticBC() {
        }

        @BeforeClass
        static void notPublicBC() {
        }

        @BeforeClass
        public static int nonVoidBC() {
            return 0;
        }

        @BeforeClass
        public static void argumentsBC(int i) {
        }

        @BeforeClass
        public static void fineBC() {
        }

        @AfterClass
        public void notStaticAC() {
        }

        @AfterClass
        static void notPublicAC() {
        }

        @AfterClass
        public static int nonVoidAC() {
            return 0;
        }

        @AfterClass
        public static void argumentsAC(int i) {
        }

        @AfterClass
        public static void fineAC() {
        }

        @After
        public static void staticA() {
        }

        @After
        void notPublicA() {
        }

        @After
        public int nonVoidA() {
            return 0;
        }

        @After
        public void argumentsA(int i) {
        }

        @After
        public void fineA() {
        }

        @Before
        public static void staticB() {
        }

        @Before
        void notPublicB() {
        }

        @Before
        public int nonVoidB() {
            return 0;
        }

        @Before
        public void argumentsB(int i) {
        }

        @Before
        public void fineB() {
        }

        @Test
        public static void staticT() {
        }

        @Test
        void notPublicT() {
        }

        @Test
        public int nonVoidT() {
            return 0;
        }

        @Test
        public void argumentsT(int i) {
        }

        @Test
        public void fineT() {
        }
    }

    @Test
    public void testFailures() throws Exception {
        List<Throwable> problems = validateAllMethods(TestMethodTest.EverythingWrong.class);
        int errorCount = 1 + (4 * 5);// missing constructor plus four invalid methods for each annotation */

        Assert.assertEquals(errorCount, problems.size());
    }

    public static class SuperWrong {
        @Test
        void notPublic() {
        }
    }

    public static class SubWrong extends TestMethodTest.SuperWrong {
        @Test
        public void justFine() {
        }
    }

    @Test
    public void validateInheritedMethods() throws Exception {
        List<Throwable> problems = validateAllMethods(TestMethodTest.SubWrong.class);
        Assert.assertEquals(1, problems.size());
    }

    public static class SubShadows extends TestMethodTest.SuperWrong {
        @Override
        @Test
        public void notPublic() {
        }
    }

    @Test
    public void dontValidateShadowedMethods() throws Exception {
        List<Throwable> problems = validateAllMethods(TestMethodTest.SubShadows.class);
        Assert.assertTrue(problems.isEmpty());
    }

    public static class IgnoredTest {
        @Test
        public void valid() {
        }

        @Ignore
        @Test
        public void ignored() {
        }

        @Ignore("For testing purposes")
        @Test
        public void withReason() {
        }
    }

    @Test
    public void ignoreRunner() {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(TestMethodTest.IgnoredTest.class);
        Assert.assertEquals(2, result.getIgnoreCount());
        Assert.assertEquals(1, result.getRunCount());
    }

    @Test
    public void compatibility() {
        TestResult result = new TestResult();
        new JUnit4TestAdapter(TestMethodTest.IgnoredTest.class).run(result);
        Assert.assertEquals(1, result.runCount());
    }

    public static class Confused {
        @Test
        public void a(Object b) {
        }

        @Test
        public void a() {
        }
    }

    @Test(expected = InitializationError.class)
    public void overloaded() throws InitializationError {
        new BlockJUnit4ClassRunner(TestMethodTest.Confused.class);
    }

    public static class ConstructorParameter {
        public ConstructorParameter(Object something) {
        }

        @Test
        public void a() {
        }
    }

    @Test(expected = InitializationError.class)
    public void constructorParameter() throws InitializationError {
        new BlockJUnit4ClassRunner(TestMethodTest.ConstructorParameter.class);
    }

    public static class OnlyTestIsIgnored {
        @Ignore
        @Test
        public void ignored() {
        }
    }

    @Test
    public void onlyIgnoredMethodsIsStillFineTestClass() {
        Result result = JUnitCore.runClasses(TestMethodTest.OnlyTestIsIgnored.class);
        Assert.assertEquals(0, result.getFailureCount());
        Assert.assertEquals(1, result.getIgnoreCount());
    }
}

