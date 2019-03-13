package com.github.dreamhead.moco;


import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoRedirectStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_redirect_to_expected_url() throws IOException {
        runWithConfiguration("redirect.json");
        Assert.assertThat(helper.get(remoteUrl("/redirect")), CoreMatchers.is("foo"));
    }

    @Test
    public void should_redirect_to_expected_url_with_template() throws IOException {
        runWithConfiguration("redirect.json");
        Assert.assertThat(helper.get(remoteUrl("/redirect-with-template")), CoreMatchers.is("foo"));
    }

    @Test
    public void should_redirect_to_expected_url_with_path_resource() throws IOException {
        runWithConfiguration("redirect.json");
        Assert.assertThat(helper.get(remoteUrl("/redirect-with-path-resource")), CoreMatchers.is("foo"));
    }
}

