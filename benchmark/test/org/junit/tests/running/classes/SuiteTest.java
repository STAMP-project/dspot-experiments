package org.junit.tests.running.classes;


import java.util.List;
import junit.framework.JUnit4TestAdapter;
import junit.framework.TestResult;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.Suite;


public class SuiteTest {
    public static class TestA {
        @Test
        public void pass() {
        }
    }

    public static class TestB {
        @Test
        public void fail() {
            Assert.fail();
        }
    }

    @RunWith(Suite.class)
    @Suite.SuiteClasses({ SuiteTest.TestA.class, SuiteTest.TestB.class })
    public static class All {}

    @RunWith(Suite.class)
    @Suite.SuiteClasses(SuiteTest.TestA.class)
    static class NonPublicSuite {}

    @RunWith(Suite.class)
    @Suite.SuiteClasses(SuiteTest.TestA.class)
    static class NonPublicSuiteWithBeforeClass {
        @BeforeClass
        public static void doesNothing() {
        }
    }

    public static class InheritsAll extends SuiteTest.All {}

    @Test
    public void ensureTestIsRun() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(SuiteTest.All.class);
        Assert.assertEquals(2, result.getRunCount());
        Assert.assertEquals(1, result.getFailureCount());
    }

    @Test
    public void ensureInheritedTestIsRun() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(SuiteTest.InheritsAll.class);
        Assert.assertEquals(2, result.getRunCount());
        Assert.assertEquals(1, result.getFailureCount());
    }

    @Test
    public void suiteTestCountIsCorrect() throws Exception {
        Runner runner = Request.aClass(SuiteTest.All.class).getRunner();
        Assert.assertEquals(2, runner.testCount());
    }

    @Test
    public void suiteClassDoesNotNeedToBePublic() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(SuiteTest.NonPublicSuite.class);
        Assert.assertEquals(1, result.getRunCount());
        Assert.assertEquals(0, result.getFailureCount());
    }

    @Test
    public void nonPublicSuiteClassWithBeforeClassPasses() {
        Assert.assertThat(PrintableResult.testResult(SuiteTest.NonPublicSuiteWithBeforeClass.class), ResultMatchers.isSuccessful());
    }

    @Test
    public void ensureSuitesWorkWithForwardCompatibility() {
        junit.framework.Test test = new JUnit4TestAdapter(SuiteTest.All.class);
        TestResult result = new TestResult();
        test.run(result);
        Assert.assertEquals(2, result.runCount());
    }

    @Test
    public void forwardCompatibilityWorksWithGetTests() {
        JUnit4TestAdapter adapter = new JUnit4TestAdapter(SuiteTest.All.class);
        List<? extends junit.framework.Test> tests = adapter.getTests();
        Assert.assertEquals(2, tests.size());
    }

    @Test
    public void forwardCompatibilityWorksWithTestCount() {
        JUnit4TestAdapter adapter = new JUnit4TestAdapter(SuiteTest.All.class);
        Assert.assertEquals(2, adapter.countTestCases());
    }

    private static String log = "";

    @RunWith(Suite.class)
    @Suite.SuiteClasses({ SuiteTest.TestA.class, SuiteTest.TestB.class })
    public static class AllWithBeforeAndAfterClass {
        @BeforeClass
        public static void before() {
            SuiteTest.log += "before ";
        }

        @AfterClass
        public static void after() {
            SuiteTest.log += "after ";
        }
    }

    @Test
    public void beforeAndAfterClassRunOnSuite() {
        SuiteTest.log = "";
        JUnitCore.runClasses(SuiteTest.AllWithBeforeAndAfterClass.class);
        Assert.assertEquals("before after ", SuiteTest.log);
    }

    @RunWith(Suite.class)
    public static class AllWithOutAnnotation {}

    @Test
    public void withoutSuiteClassAnnotationProducesFailure() {
        Result result = JUnitCore.runClasses(SuiteTest.AllWithOutAnnotation.class);
        Assert.assertEquals(1, result.getFailureCount());
        String expected = String.format("class '%s' must have a SuiteClasses annotation", SuiteTest.AllWithOutAnnotation.class.getName());
        Assert.assertEquals(expected, result.getFailures().get(0).getMessage());
    }

    @RunWith(Suite.class)
    @Suite.SuiteClasses(SuiteTest.InfiniteLoop.class)
    public static class InfiniteLoop {}

    @Test
    public void whatHappensWhenASuiteHasACycle() {
        Result result = JUnitCore.runClasses(SuiteTest.InfiniteLoop.class);
        Assert.assertEquals(1, result.getFailureCount());
    }

    @RunWith(Suite.class)
    @Suite.SuiteClasses({ SuiteTest.BiInfiniteLoop.class, SuiteTest.BiInfiniteLoop.class })
    public static class BiInfiniteLoop {}

    @Test
    public void whatHappensWhenASuiteHasAForkingCycle() {
        Result result = JUnitCore.runClasses(SuiteTest.BiInfiniteLoop.class);
        Assert.assertEquals(2, result.getFailureCount());
    }

    // The interesting case here is that Hydra indirectly contains two copies of
    // itself (if it only contains one, Java's StackOverflowError eventually
    // bails us out)
    @RunWith(Suite.class)
    @Suite.SuiteClasses({ SuiteTest.Hercules.class })
    public static class Hydra {}

    @RunWith(Suite.class)
    @Suite.SuiteClasses({ SuiteTest.Hydra.class, SuiteTest.Hydra.class })
    public static class Hercules {}

    @Test
    public void whatHappensWhenASuiteContainsItselfIndirectly() {
        Result result = JUnitCore.runClasses(SuiteTest.Hydra.class);
        Assert.assertEquals(2, result.getFailureCount());
    }

    @RunWith(Suite.class)
    @Suite.SuiteClasses({  })
    public class WithoutDefaultConstructor {
        public WithoutDefaultConstructor(int i) {
        }
    }

    @Test
    public void suiteShouldBeOKwithNonDefaultConstructor() throws Exception {
        Result result = JUnitCore.runClasses(SuiteTest.WithoutDefaultConstructor.class);
        Assert.assertTrue(result.wasSuccessful());
    }

    @RunWith(Suite.class)
    public class NoSuiteClassesAnnotation {}

    @Test
    public void suiteShouldComplainAboutNoSuiteClassesAnnotation() {
        Assert.assertThat(PrintableResult.testResult(SuiteTest.NoSuiteClassesAnnotation.class), ResultMatchers.hasSingleFailureContaining("SuiteClasses"));
    }
}

