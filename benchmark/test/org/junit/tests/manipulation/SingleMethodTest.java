package org.junit.tests.manipulation;


import java.util.Arrays;
import java.util.List;
import junit.framework.JUnit4TestAdapter;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;


public class SingleMethodTest {
    public static int count;

    public static class OneTimeSetup {
        @BeforeClass
        public static void once() {
            (SingleMethodTest.count)++;
        }

        @Test
        public void one() {
        }

        @Test
        public void two() {
        }
    }

    @Test
    public void oneTimeSetup() throws Exception {
        SingleMethodTest.count = 0;
        Runner runner = Request.method(SingleMethodTest.OneTimeSetup.class, "one").getRunner();
        Result result = new JUnitCore().run(runner);
        Assert.assertEquals(1, SingleMethodTest.count);
        Assert.assertEquals(1, result.getRunCount());
    }

    @RunWith(Parameterized.class)
    public static class ParameterizedOneTimeSetup {
        @Parameterized.Parameters
        public static List<Object[]> params() {
            return Arrays.asList(new Object[]{ 1 }, new Object[]{ 2 });
        }

        public ParameterizedOneTimeSetup(int x) {
        }

        @Test
        public void one() {
        }
    }

    @Test
    public void parameterizedFilterToSingleMethod() throws Exception {
        SingleMethodTest.count = 0;
        Runner runner = Request.method(SingleMethodTest.ParameterizedOneTimeSetup.class, "one[0]").getRunner();
        Result result = new JUnitCore().run(runner);
        Assert.assertEquals(1, result.getRunCount());
    }

    @RunWith(Parameterized.class)
    public static class ParameterizedOneTimeBeforeClass {
        @Parameterized.Parameters
        public static List<Object[]> params() {
            return Arrays.asList(new Object[]{ 1 }, new Object[]{ 2 });
        }

        public ParameterizedOneTimeBeforeClass(int x) {
        }

        @BeforeClass
        public static void once() {
            (SingleMethodTest.count)++;
        }

        @Test
        public void one() {
        }
    }

    @Test
    public void parameterizedBeforeClass() throws Exception {
        SingleMethodTest.count = 0;
        JUnitCore.runClasses(SingleMethodTest.ParameterizedOneTimeBeforeClass.class);
        Assert.assertEquals(1, SingleMethodTest.count);
    }

    @Test
    public void filteringAffectsPlan() throws Exception {
        Runner runner = Request.method(SingleMethodTest.OneTimeSetup.class, "one").getRunner();
        Assert.assertEquals(1, runner.testCount());
    }

    @Test
    public void nonexistentMethodCreatesFailure() throws Exception {
        Assert.assertEquals(1, new JUnitCore().run(Request.method(SingleMethodTest.OneTimeSetup.class, "thisMethodDontExist")).getFailureCount());
    }

    @Test(expected = NoTestsRemainException.class)
    public void filteringAwayEverythingThrowsException() throws NoTestsRemainException {
        Filterable runner = ((Filterable) (Request.aClass(SingleMethodTest.OneTimeSetup.class).getRunner()));
        runner.filter(new Filter() {
            @Override
            public boolean shouldRun(Description description) {
                return false;
            }

            @Override
            public String describe() {
                return null;
            }
        });
    }

    public static class TestOne {
        @Test
        public void a() {
        }

        @Test
        public void b() {
        }
    }

    public static class TestTwo {
        @Test
        public void a() {
        }

        @Test
        public void b() {
        }
    }

    @RunWith(Suite.class)
    @Suite.SuiteClasses({ SingleMethodTest.TestOne.class, SingleMethodTest.TestTwo.class })
    public static class OneTwoSuite {}

    @Test
    public void eliminateUnnecessaryTreeBranches() throws Exception {
        Runner runner = Request.aClass(SingleMethodTest.OneTwoSuite.class).filterWith(Description.createTestDescription(SingleMethodTest.TestOne.class, "a")).getRunner();
        Description description = runner.getDescription();
        Assert.assertEquals(1, description.getChildren().size());
    }

    public static class HasSuiteMethod {
        @Test
        public void a() {
        }

        @Test
        public void b() {
        }

        public static junit.framework.Test suite() {
            return new JUnit4TestAdapter(SingleMethodTest.HasSuiteMethod.class);
        }
    }

    @Test
    public void classesWithSuiteMethodsAreFiltered() {
        int testCount = Request.method(SingleMethodTest.HasSuiteMethod.class, "a").getRunner().getDescription().testCount();
        Assert.assertThat(testCount, CoreMatchers.is(1));
    }
}

