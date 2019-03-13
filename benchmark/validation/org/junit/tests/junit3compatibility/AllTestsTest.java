package org.junit.tests.junit3compatibility;


import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;


public class AllTestsTest {
    private static boolean run;

    public static class OneTest extends TestCase {
        public void testSomething() {
            AllTestsTest.run = true;
        }
    }

    @RunWith(AllTests.class)
    public static class All {
        public static Test suite() {
            TestSuite suite = new TestSuite();
            suite.addTestSuite(AllTestsTest.OneTest.class);
            return suite;
        }
    }

    @org.junit.Test
    public void ensureTestIsRun() {
        JUnitCore runner = new JUnitCore();
        AllTestsTest.run = false;// Have to explicitly set run here because the runner might independently run OneTest above

        runner.run(AllTestsTest.All.class);
        Assert.assertTrue(AllTestsTest.run);
    }

    @org.junit.Test
    public void correctTestCount() throws Throwable {
        AllTests tests = new AllTests(AllTestsTest.All.class);
        Assert.assertEquals(1, tests.testCount());
    }

    @org.junit.Test
    public void someUsefulDescription() throws Throwable {
        AllTests tests = new AllTests(AllTestsTest.All.class);
        Assert.assertThat(tests.getDescription().toString(), CoreMatchers.containsString("OneTest"));
    }

    public static class JUnit4Test {
        @org.junit.Test
        public void testSomething() {
            AllTestsTest.run = true;
        }
    }

    @RunWith(AllTests.class)
    public static class AllJUnit4 {
        public static Test suite() {
            TestSuite suite = new TestSuite();
            suite.addTest(new JUnit4TestAdapter(AllTestsTest.JUnit4Test.class));
            return suite;
        }
    }

    @org.junit.Test
    public void correctTestCountAdapted() throws Throwable {
        AllTests tests = new AllTests(AllTestsTest.AllJUnit4.class);
        Assert.assertEquals(1, tests.testCount());
    }

    @RunWith(AllTests.class)
    public static class BadSuiteMethod {
        public static Test suite() {
            throw new RuntimeException("can't construct");
        }
    }

    @org.junit.Test(expected = RuntimeException.class)
    public void exceptionThrownWhenSuiteIsBad() throws Throwable {
        new AllTests(AllTestsTest.BadSuiteMethod.class);
    }
}

