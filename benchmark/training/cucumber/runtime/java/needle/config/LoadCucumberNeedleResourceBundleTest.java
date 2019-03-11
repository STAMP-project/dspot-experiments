package cucumber.runtime.java.needle.config;


import java.util.ResourceBundle;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static LoadResourceBundle.INSTANCE;


public class LoadCucumberNeedleResourceBundleTest {
    private final LoadResourceBundle function = INSTANCE;

    @Test
    public void shouldReturnEmptyResourceBundleWhenResourceDoesNotExist() throws Exception {
        final ResourceBundle resourceBundle = function.apply("does-not-exist");
        Assert.assertNotNull(resourceBundle);
        Assert.assertThat(resourceBundle, CoreMatchers.is(LoadResourceBundle.EMPTY_RESOURCE_BUNDLE));
    }

    @Test
    public void shouldReturnExistingResourceBundle() throws Exception {
        final ResourceBundle resourceBundle = function.apply("empty");
        Assert.assertNotNull(resourceBundle);
        Assert.assertTrue(resourceBundle.keySet().isEmpty());
    }

    @Test
    public void shouldAlwaysReturnEmptyForEmptyResourceBundle() throws Exception {
        final ResourceBundle resourceBundle = LoadResourceBundle.EMPTY_RESOURCE_BUNDLE;
        Assert.assertNotNull(resourceBundle.getObject("foo"));
        Assert.assertThat(resourceBundle.getString("foo"), CoreMatchers.is(""));
        Assert.assertFalse(resourceBundle.getKeys().hasMoreElements());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenResourceNameIsNull() {
        function.apply(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenResourceNameIsEmpty() {
        function.apply("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenResourceNameIsBlank() {
        function.apply(" ");
    }
}

