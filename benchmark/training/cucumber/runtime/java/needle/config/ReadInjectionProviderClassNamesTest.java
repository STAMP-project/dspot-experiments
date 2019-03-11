package cucumber.runtime.java.needle.config;


import cucumber.runtime.java.needle.test.injectionprovider.SimpleNameGetterProvider;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;

import static ReadInjectionProviderClassNames.INSTANCE;


public class ReadInjectionProviderClassNamesTest {
    private final ReadInjectionProviderClassNames function = INSTANCE;

    @Test
    public void shouldReturnProviderFromCucumberNeedleProperties() throws Exception {
        final Set<String> classNames = function.apply(loadBundle(CucumberNeedleConfiguration.RESOURCE_CUCUMBER_NEEDLE));
        Assert.assertNotNull(classNames);
        Assert.assertThat(classNames.size(), CoreMatchers.is(1));
        Assert.assertThat(classNames.iterator().next(), CoreMatchers.is(SimpleNameGetterProvider.class.getCanonicalName()));
    }

    @Test
    public void shouldReturnEmptySetWhenResourceBundleIsNull() {
        final Set<String> classNames = function.apply(null);
        Assert.assertNotNull(classNames);
        Assert.assertThat(classNames.isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void shouldReturnEmptySetWhenPropertyIsNotSet() {
        final Set<String> classNames = function.apply(loadBundle("resource-bundles/empty"));
        Assert.assertNotNull(classNames);
        Assert.assertThat(classNames.isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void shouldReturnEmptySetWhenPropertyIsEmpty() {
        final Set<String> classNames = function.apply(loadBundle("resource-bundles/no-classname"));
        Assert.assertNotNull(classNames);
        Assert.assertThat(classNames.isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void shouldReturnOneTrimmedClassName() throws Exception {
        final Set<String> classNames = function.apply(loadBundle("resource-bundles/one-classname"));
        Assert.assertThat(classNames.size(), CoreMatchers.is(1));
        final String first = classNames.iterator().next();
        Assert.assertThat(first, CoreMatchers.is("java.lang.String"));
    }

    @Test
    public void shouldReturnTwoTrimmedClassNames() throws Exception {
        final Set<String> classNames = function.apply(loadBundle("resource-bundles/two-classname"));
        Assert.assertThat(classNames.size(), CoreMatchers.is(2));
        Assert.assertThat(classNames, IsCollectionContaining.hasItems("java.lang.String", "java.util.Set"));
    }
}

