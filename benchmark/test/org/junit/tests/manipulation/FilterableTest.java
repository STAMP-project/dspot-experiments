package org.junit.tests.manipulation;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Filter;
import org.junit.runners.Parameterized;


public class FilterableTest {
    public static class FilteredRunner extends Parameterized {
        public FilteredRunner(Class<?> klass) throws Throwable {
            super(klass);
            filter(new Filter() {
                @Override
                public boolean shouldRun(Description description) {
                    return !(description.getDisplayName().contains("skip"));
                }

                @Override
                public String describe() {
                    return "skip methods containing the word 'skip'";
                }
            });
        }
    }

    @RunWith(FilterableTest.FilteredRunner.class)
    public static class FilteredTest {
        @Parameterized.Parameters
        public static List<Object[]> parameters() {
            return Arrays.asList(new Object[]{ 3 }, new Object[]{ 4 });
        }

        public FilteredTest(int x) {
        }

        @Test
        public void skipThis() {
            Assert.fail();
        }

        @Test
        public void runThis() {
        }
    }

    @Test
    public void testFilterInRunnerConstructor() {
        Result result = JUnitCore.runClasses(FilterableTest.FilteredTest.class);
        Assert.assertTrue(result.wasSuccessful());
    }
}

