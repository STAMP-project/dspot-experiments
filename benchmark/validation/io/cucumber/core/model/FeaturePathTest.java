package io.cucumber.core.model;


import java.io.File;
import java.net.URI;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class FeaturePathTest {
    @Test(expected = IllegalArgumentException.class)
    public void can_parse_empty_feature_path() {
        URI uri = FeaturePath.parse("");
        Assert.assertThat(uri.getScheme(), CoreMatchers.is("classpath"));
        Assert.assertThat(uri.getSchemeSpecificPart(), CoreMatchers.is("/"));
    }

    @Test
    public void can_parse_root_package() {
        URI uri = FeaturePath.parse("classpath:/");
        Assert.assertThat(uri.getScheme(), CoreMatchers.is("classpath"));
        Assert.assertThat(uri.getSchemeSpecificPart(), CoreMatchers.is("/"));
    }

    @Test
    public void can_parse_eclipse_plugin_default_glue() {
        // The eclipse plugin uses `classpath:` as the default
        URI uri = FeaturePath.parse("classpath:");
        Assert.assertThat(uri.getScheme(), CoreMatchers.is("classpath"));
        Assert.assertThat(uri.getSchemeSpecificPart(), CoreMatchers.is("/"));
    }

    @Test
    public void can_parse_classpath_form() {
        URI uri = FeaturePath.parse("classpath:/path/to/file.feature");
        Assert.assertEquals("classpath", uri.getScheme());
        Assert.assertEquals("/path/to/file.feature", uri.getSchemeSpecificPart());
    }

    @Test
    public void can_parse_classpath_directory_form() {
        URI uri = FeaturePath.parse("classpath:/path/to");
        Assert.assertEquals("classpath", uri.getScheme());
        Assert.assertEquals("/path/to", uri.getSchemeSpecificPart());
    }

    @Test
    public void can_parse_absolute_file_form() {
        URI uri = FeaturePath.parse("file:/path/to/file.feature");
        Assert.assertEquals("file", uri.getScheme());
        Assert.assertEquals("/path/to/file.feature", uri.getSchemeSpecificPart());
    }

    @Test
    public void can_parse_absolute_directory_form() {
        URI uri = FeaturePath.parse("file:/path/to");
        Assert.assertEquals("file", uri.getScheme());
        Assert.assertEquals("/path/to", uri.getSchemeSpecificPart());
    }

    @Test
    public void can_parse_relative_file_form() {
        URI uri = FeaturePath.parse("file:path/to/file.feature");
        Assert.assertEquals("file", uri.getScheme());
        Assert.assertEquals("path/to/file.feature", uri.getSchemeSpecificPart());
    }

    @Test
    public void can_parse_absolute_path_form() {
        URI uri = FeaturePath.parse("/path/to/file.feature");
        Assert.assertEquals("file", uri.getScheme());
        // Use File to work out the drive letter on windows.
        File file = new File("/path/to/file.feature");
        Assert.assertEquals(file.toURI().getSchemeSpecificPart(), uri.getSchemeSpecificPart());
    }

    @Test
    public void can_parse_relative_path_form() {
        URI uri = FeaturePath.parse("path/to/file.feature");
        Assert.assertEquals("file", uri.getScheme());
        Assert.assertEquals("path/to/file.feature", uri.getSchemeSpecificPart());
    }

    @Test
    public void can_parse_windows_path_form() {
        Assume.assumeThat(File.separatorChar, CoreMatchers.is('\\'));// Requires windows

        URI uri = FeaturePath.parse("path\\to\\file.feature");
        Assert.assertEquals("file", uri.getScheme());
        Assert.assertEquals("path/to/file.feature", uri.getSchemeSpecificPart());
    }

    @Test
    public void can_parse_windows_absolute_path_form() {
        Assume.assumeThat(File.separatorChar, CoreMatchers.is('\\'));// Requires windows

        URI uri = FeaturePath.parse("C:\\path\\to\\file.feature");
        Assert.assertEquals("file", uri.getScheme());
        Assert.assertEquals("/C:/path/to/file.feature", uri.getSchemeSpecificPart());
    }

    @Test
    public void can_parse_whitespace_in_path() {
        URI uri = FeaturePath.parse("path/to the/file.feature");
        Assert.assertEquals("file", uri.getScheme());
        Assert.assertEquals("path/to the/file.feature", uri.getSchemeSpecificPart());
    }

    @Test
    public void can_parse_windows_file_path_with_standard_file_separator() {
        Assume.assumeThat(System.getProperty("os.name"), FeaturePathTest.isWindows());
        URI uri = FeaturePath.parse("C:/path/to/file.feature");
        Assert.assertEquals("file", uri.getScheme());
        Assert.assertEquals("/C:/path/to/file.feature", uri.getSchemeSpecificPart());
    }
}

