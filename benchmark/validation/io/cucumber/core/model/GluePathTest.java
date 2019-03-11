package io.cucumber.core.model;


import java.io.File;
import java.net.URI;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class GluePathTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void can_parse_empty_glue_path() {
        URI uri = GluePath.parse("");
        Assert.assertThat(uri.getScheme(), CoreMatchers.is("classpath"));
        Assert.assertThat(uri.getSchemeSpecificPart(), CoreMatchers.is("/"));
    }

    @Test
    public void can_parse_root_package() {
        URI uri = GluePath.parse("classpath:/");
        Assert.assertThat(uri.getScheme(), CoreMatchers.is("classpath"));
        Assert.assertThat(uri.getSchemeSpecificPart(), CoreMatchers.is("/"));
    }

    @Test
    public void can_parse_eclipse_plugin_default_glue() {
        // The eclipse plugin uses `classpath:` as the default
        URI uri = GluePath.parse("classpath:");
        Assert.assertThat(uri.getScheme(), CoreMatchers.is("classpath"));
        Assert.assertThat(uri.getSchemeSpecificPart(), CoreMatchers.is("/"));
    }

    @Test
    public void can_parse_classpath_form() {
        URI uri = GluePath.parse("classpath:com/example/app");
        Assert.assertThat(uri.getScheme(), CoreMatchers.is("classpath"));
        Assert.assertThat(uri.getSchemeSpecificPart(), CoreMatchers.is("com/example/app"));
    }

    @Test
    public void can_parse_relative_path_form() {
        URI uri = GluePath.parse("com/example/app");
        Assert.assertThat(uri.getScheme(), CoreMatchers.is("classpath"));
        Assert.assertThat(uri.getSchemeSpecificPart(), CoreMatchers.is("com/example/app"));
    }

    @Test
    public void can_parse_absolute_path_form() {
        URI uri = GluePath.parse("/com/example/app");
        Assert.assertThat(uri.getScheme(), CoreMatchers.is("classpath"));
        Assert.assertThat(uri.getSchemeSpecificPart(), CoreMatchers.is("/com/example/app"));
    }

    @Test
    public void can_parse_package_form() {
        URI uri = GluePath.parse("com.example.app");
        Assert.assertThat(uri.getScheme(), CoreMatchers.is("classpath"));
        Assert.assertThat(uri.getSchemeSpecificPart(), CoreMatchers.is("com/example/app"));
    }

    @Test
    public void glue_path_must_have_class_path_scheme() {
        expectedException.expectMessage("The glue path must have a classpath scheme");
        GluePath.parse("file:com/example/app");
    }

    @Test
    public void glue_path_must_have_valid_identifier_parts() {
        expectedException.expectMessage("The glue path contained invalid identifiers");
        GluePath.parse("01-examples");
    }

    @Test
    public void can_parse_windows_path_form() {
        Assume.assumeThat(File.separatorChar, CoreMatchers.is('\\'));// Requires windows

        URI uri = GluePath.parse("com\\example\\app");
        Assert.assertThat(uri.getScheme(), CoreMatchers.is("classpath"));
        Assert.assertEquals("com/example/app", uri.getSchemeSpecificPart());
    }

    @Test
    public void absolute_windows_path_form_is_not_valid() {
        Assume.assumeThat(File.separatorChar, CoreMatchers.is('\\'));// Requires windows

        expectedException.expectMessage("The glue path must have a classpath scheme");
        GluePath.parse("C:\\com\\example\\app");
    }
}

