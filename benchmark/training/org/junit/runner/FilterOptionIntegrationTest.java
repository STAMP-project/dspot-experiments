package org.junit.runner;


import java.util.HashSet;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.experimental.categories.ExcludeCategories;
import org.junit.experimental.categories.IncludeCategories;
import org.junit.runner.notification.RunListener;


public class FilterOptionIntegrationTest {
    private static final String INCLUDES_DUMMY_CATEGORY_0 = (("--filter=" + (IncludeCategories.class.getName())) + "=") + (FilterOptionIntegrationTest.DummyCategory0.class.getName());

    private static final String EXCLUDES_DUMMY_CATEGORY_1 = (("--filter=" + (ExcludeCategories.class.getName())) + "=") + (FilterOptionIntegrationTest.DummyCategory1.class.getName());

    private JUnitCore jUnitCore = new JUnitCore();

    private FilterOptionIntegrationTest.TestListener testListener = new FilterOptionIntegrationTest.TestListener();

    @Test
    public void shouldRunAllTests() {
        Result result = runJUnit(FilterOptionIntegrationTest.DummyTestClass.class.getName(), FilterOptionIntegrationTest.DummyTestClass0.class.getName(), FilterOptionIntegrationTest.DummyTestClass1.class.getName(), FilterOptionIntegrationTest.DummyTestClass01.class.getName(), FilterOptionIntegrationTest.DummyTestClass0TestMethod1.class.getName());
        assertWasRun(FilterOptionIntegrationTest.DummyTestClass.class);
        assertWasRun(FilterOptionIntegrationTest.DummyTestClass0.class);
        assertWasRun(FilterOptionIntegrationTest.DummyTestClass1.class);
        assertWasRun(FilterOptionIntegrationTest.DummyTestClass01.class);
        assertWasRun(FilterOptionIntegrationTest.DummyTestClass0TestMethod1.class);
        MatcherAssert.assertThat("runCount does not match", result.getRunCount(), CoreMatchers.is(5));
        MatcherAssert.assertThat("failureCount does not match", result.getFailureCount(), CoreMatchers.is(0));
    }

    @Test
    public void shouldExcludeSomeTests() {
        Result result = runJUnit(FilterOptionIntegrationTest.EXCLUDES_DUMMY_CATEGORY_1, FilterOptionIntegrationTest.DummyTestClass.class.getName(), FilterOptionIntegrationTest.DummyTestClass0.class.getName(), FilterOptionIntegrationTest.DummyTestClass1.class.getName(), FilterOptionIntegrationTest.DummyTestClass01.class.getName(), FilterOptionIntegrationTest.DummyTestClass0TestMethod1.class.getName());
        assertWasRun(FilterOptionIntegrationTest.DummyTestClass.class);
        assertWasRun(FilterOptionIntegrationTest.DummyTestClass0.class);
        assertWasNotRun(FilterOptionIntegrationTest.DummyTestClass1.class);
        assertWasNotRun(FilterOptionIntegrationTest.DummyTestClass01.class);
        assertWasNotRun(FilterOptionIntegrationTest.DummyTestClass0TestMethod1.class);
        MatcherAssert.assertThat("runCount does not match", result.getRunCount(), CoreMatchers.is(2));
        MatcherAssert.assertThat("failureCount does not match", result.getFailureCount(), CoreMatchers.is(0));
    }

    @Test
    public void shouldIncludeSomeTests() {
        Result result = runJUnit(FilterOptionIntegrationTest.INCLUDES_DUMMY_CATEGORY_0, FilterOptionIntegrationTest.DummyTestClass.class.getName(), FilterOptionIntegrationTest.DummyTestClass0.class.getName(), FilterOptionIntegrationTest.DummyTestClass1.class.getName(), FilterOptionIntegrationTest.DummyTestClass01.class.getName(), FilterOptionIntegrationTest.DummyTestClass0TestMethod1.class.getName());
        assertWasNotRun(FilterOptionIntegrationTest.DummyTestClass.class);
        assertWasRun(FilterOptionIntegrationTest.DummyTestClass0.class);
        assertWasNotRun(FilterOptionIntegrationTest.DummyTestClass1.class);
        assertWasRun(FilterOptionIntegrationTest.DummyTestClass01.class);
        assertWasRun(FilterOptionIntegrationTest.DummyTestClass0TestMethod1.class);
        MatcherAssert.assertThat("runCount does not match", result.getRunCount(), CoreMatchers.is(3));
        MatcherAssert.assertThat("failureCount does not match", result.getFailureCount(), CoreMatchers.is(0));
    }

    @Test
    public void shouldCombineFilters() {
        Result result = runJUnit(FilterOptionIntegrationTest.INCLUDES_DUMMY_CATEGORY_0, FilterOptionIntegrationTest.EXCLUDES_DUMMY_CATEGORY_1, FilterOptionIntegrationTest.DummyTestClass.class.getName(), FilterOptionIntegrationTest.DummyTestClass0.class.getName(), FilterOptionIntegrationTest.DummyTestClass1.class.getName(), FilterOptionIntegrationTest.DummyTestClass01.class.getName(), FilterOptionIntegrationTest.DummyTestClass0TestMethod1.class.getName());
        assertWasNotRun(FilterOptionIntegrationTest.DummyTestClass.class);
        assertWasRun(FilterOptionIntegrationTest.DummyTestClass0.class);
        assertWasNotRun(FilterOptionIntegrationTest.DummyTestClass1.class);
        assertWasNotRun(FilterOptionIntegrationTest.DummyTestClass01.class);
        assertWasNotRun(FilterOptionIntegrationTest.DummyTestClass0TestMethod1.class);
        MatcherAssert.assertThat("runCount does not match", result.getRunCount(), CoreMatchers.is(1));
        MatcherAssert.assertThat("failureCount does not match", result.getFailureCount(), CoreMatchers.is(0));
    }

    private static class TestListener extends RunListener {
        private Set<String> startedTests = new HashSet<String>();

        private Set<String> finishedTests = new HashSet<String>();

        @Override
        public void testFinished(final Description description) {
            finishedTests.add(description.getClassName());
        }

        private boolean testFinished(final Class<?> testClass) {
            return finishedTests.contains(testClass.getName());
        }

        @Override
        public void testStarted(final Description description) {
            startedTests.add(description.getClassName());
        }

        private boolean testStarted(final Class<?> testClass) {
            return startedTests.contains(testClass.getName());
        }

        public boolean wasRun(final Class<?> testClass) {
            return (testStarted(testClass)) && (testFinished(testClass));
        }
    }

    public static class DummyTestClass {
        @Test
        public void dummyTest() {
        }
    }

    @Category(FilterOptionIntegrationTest.DummyCategory0.class)
    public static class DummyTestClass0 {
        @Test
        public void dummyTest() {
        }
    }

    @Category(FilterOptionIntegrationTest.DummyCategory1.class)
    public static class DummyTestClass1 {
        @Test
        public void dummyTest() {
        }
    }

    @Category({ FilterOptionIntegrationTest.DummyCategory0.class, FilterOptionIntegrationTest.DummyCategory1.class })
    public static class DummyTestClass01 {
        @Test
        public void dummyTest() {
        }
    }

    @Category(FilterOptionIntegrationTest.DummyCategory0.class)
    public static class DummyTestClass0TestMethod1 {
        @Category(FilterOptionIntegrationTest.DummyCategory1.class)
        @Test
        public void dummyTest() {
        }
    }

    public static interface DummyCategory0 {}

    public static interface DummyCategory1 {}
}

