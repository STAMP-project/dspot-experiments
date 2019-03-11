package com.github.dreamhead.moco.util;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class URLsTest {
    @Test
    public void should_join_path() {
        Assert.assertThat(URLs.join("base", "path"), CoreMatchers.is("base/path"));
        Assert.assertThat(URLs.join("base/", "path"), CoreMatchers.is("base/path"));
        Assert.assertThat(URLs.join("base", ""), CoreMatchers.is("base"));
        Assert.assertThat(URLs.join("base", "/path"), CoreMatchers.is("base/path"));
        Assert.assertThat(URLs.join("base/", "/path"), CoreMatchers.is("base/path"));
        Assert.assertThat(URLs.join("base", "path", "sub"), CoreMatchers.is("base/path/sub"));
    }

    @Test
    public void should_know_valid_url_character() {
        Assert.assertThat(URLs.isValidUrl("base"), CoreMatchers.is(true));
        Assert.assertThat(URLs.isValidUrl("base path"), CoreMatchers.is(false));
    }
}

