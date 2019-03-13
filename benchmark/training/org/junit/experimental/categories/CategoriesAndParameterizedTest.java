package org.junit.experimental.categories;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;


public class CategoriesAndParameterizedTest {
    public static class Token {}

    @RunWith(Parameterized.class)
    public static class ParameterizedTestWithoutCategory {
        @Parameterized.Parameters
        public static Iterable<String> getParameters() {
            return Arrays.asList("first", "second");
        }

        @Parameterized.Parameter
        public String value;

        @Test
        public void testSomething() {
            Assert.assertTrue(true);
        }
    }

    @Category(CategoriesAndParameterizedTest.Token.class)
    public static class TestThatAvoidsNoTestRemainsException {
        @Test
        public void testSomething() {
            Assert.assertTrue(true);
        }
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(CategoriesAndParameterizedTest.Token.class)
    @Suite.SuiteClasses({ CategoriesAndParameterizedTest.TestThatAvoidsNoTestRemainsException.class, CategoriesAndParameterizedTest.ParameterizedTestWithoutCategory.class })
    public static class SuiteWithParameterizedTestWithoutCategory {}

    @Test
    public void doesNotRunTestsWithoutCategory() {
        Result result = new JUnitCore().run(CategoriesAndParameterizedTest.SuiteWithParameterizedTestWithoutCategory.class);
        Assert.assertEquals(1, result.getRunCount());
        Assert.assertEquals(0, result.getFailureCount());
    }

    @RunWith(Parameterized.class)
    @Category(CategoriesAndParameterizedTest.Token.class)
    public static class ParameterizedTestWithCategory {
        @Parameterized.Parameters
        public static Iterable<String> getParameters() {
            return Arrays.asList("first", "second");
        }

        @Parameterized.Parameter
        public String value;

        @Test
        public void testSomething() {
            Assert.assertTrue(true);
        }
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(CategoriesAndParameterizedTest.Token.class)
    @Suite.SuiteClasses({ CategoriesAndParameterizedTest.ParameterizedTestWithCategory.class })
    public static class SuiteWithParameterizedTestWithCategory {}

    @Test
    public void runsTestsWithoutCategory() {
        Result result = new JUnitCore().run(CategoriesAndParameterizedTest.SuiteWithParameterizedTestWithCategory.class);
        Assert.assertEquals(2, result.getRunCount());
        Assert.assertEquals(0, result.getFailureCount());
    }

    @RunWith(Parameterized.class)
    public static class ParameterizedTestWithMethodWithCategory {
        @Parameterized.Parameters
        public static Iterable<String> getParameters() {
            return Arrays.asList("first", "second");
        }

        @Parameterized.Parameter
        public String value;

        @Test
        @Category(CategoriesAndParameterizedTest.Token.class)
        public void testSomething() {
            Assert.assertTrue(true);
        }

        @Test
        public void testThatIsNotExecuted() {
            Assert.assertTrue(true);
        }
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(CategoriesAndParameterizedTest.Token.class)
    @Suite.SuiteClasses({ CategoriesAndParameterizedTest.ParameterizedTestWithMethodWithCategory.class })
    public static class SuiteWithParameterizedTestWithMethodWithCategory {}

    @Test
    public void runsTestMethodWithCategory() {
        Result result = new JUnitCore().run(CategoriesAndParameterizedTest.SuiteWithParameterizedTestWithMethodWithCategory.class);
        Assert.assertEquals(2, result.getRunCount());
        Assert.assertEquals(0, result.getFailureCount());
    }
}

