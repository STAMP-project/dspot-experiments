package org.junit.runner;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.ExcludeCategories;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.junit.runner.manipulation.Filter;
import org.junit.runners.Suite;


public class FilterFactoriesTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TestName testName = new TestName();

    @Test
    public void shouldCreateFilterWithArguments() throws Exception {
        Filter filter = FilterFactories.createFilterFromFilterSpec(createSuiteRequest(), (((ExcludeCategories.class.getName()) + "=") + (FilterFactoriesTest.DummyCategory.class.getName())));
        MatcherAssert.assertThat(filter.describe(), CoreMatchers.startsWith("excludes "));
    }

    @Test
    public void shouldCreateFilterWithNoArguments() throws Exception {
        Filter filter = FilterFactories.createFilterFromFilterSpec(createSuiteRequest(), FilterFactoriesTest.FilterFactoryStub.class.getName());
        MatcherAssert.assertThat(filter, CoreMatchers.instanceOf(FilterFactoriesTest.DummyFilter.class));
    }

    @Test
    public void shouldPassOnDescriptionToFilterFactory() throws Exception {
        Request request = createSuiteRequest();
        Description description = request.getRunner().getDescription();
        Filter filter = FilterFactories.createFilterFromFilterSpec(request, FilterFactoriesTest.FilterFactoryStub.class.getName());
        // This assumption tested in shouldCreateFilterWithNoArguments()
        Assume.assumeThat(filter, CoreMatchers.instanceOf(FilterFactoriesTest.DummyFilter.class));
        FilterFactoriesTest.DummyFilter dummyFilter = ((FilterFactoriesTest.DummyFilter) (filter));
        MatcherAssert.assertThat(dummyFilter.getTopLevelDescription(), CoreMatchers.is(description));
    }

    @Test
    public void shouldCreateFilter() throws Exception {
        Filter filter = FilterFactories.createFilter(FilterFactoriesTest.FilterFactoryStub.class, new FilterFactoryParams(Description.createSuiteDescription(testName.getMethodName()), ""));
        MatcherAssert.assertThat(filter, CoreMatchers.instanceOf(FilterFactoriesTest.DummyFilter.class));
    }

    @Test
    public void shouldThrowExceptionIfNotFilterFactory() throws Exception {
        expectedException.expect(FilterFactory.FilterNotCreatedException.class);
        FilterFactories.createFilterFactory(FilterFactoriesTest.NonFilterFactory.class.getName());
    }

    @Test
    public void shouldThrowExceptionIfNotInstantiable() throws Exception {
        expectedException.expect(FilterFactory.FilterNotCreatedException.class);
        FilterFactories.createFilterFactory(FilterFactoriesTest.NonInstantiableFilterFactory.class);
    }

    public static class NonFilterFactory {}

    public static class NonInstantiableFilterFactory implements FilterFactory {
        private NonInstantiableFilterFactory() {
        }

        public Filter createFilter(FilterFactoryParams params) throws FilterFactory.FilterNotCreatedException {
            throw new FilterFactory.FilterNotCreatedException(new Exception("not implemented"));
        }
    }

    public static class FilterFactoryStub implements FilterFactory {
        public Filter createFilter(FilterFactoryParams params) {
            return new FilterFactoriesTest.DummyFilter(params.getTopLevelDescription());
        }
    }

    private static class DummyFilter extends Filter {
        private final Description fTopLevelDescription;

        public DummyFilter(Description topLevelDescription) {
            fTopLevelDescription = topLevelDescription;
        }

        public Description getTopLevelDescription() {
            return fTopLevelDescription;
        }

        @Override
        public boolean shouldRun(Description description) {
            return false;
        }

        @Override
        public String describe() {
            return null;
        }
    }

    public static class DummyCategory {}

    @RunWith(Suite.class)
    @Suite.SuiteClasses(FilterFactoriesTest.DummyTest.class)
    public static class DummySuite {}

    public static class DummyTest {
        @Test
        public void passes() {
        }
    }
}

