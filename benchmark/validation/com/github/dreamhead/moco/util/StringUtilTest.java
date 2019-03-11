package com.github.dreamhead.moco.util;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class StringUtilTest {
    @Test
    public void should_strip_null_to_empty_string() {
        Assert.assertThat(Strings.strip(null), CoreMatchers.is(""));
    }

    @Test
    public void should_strip_empty_to_empty_string() {
        Assert.assertThat(Strings.strip(""), CoreMatchers.is(""));
    }

    @Test
    public void should_strip_ordinary_string_as_it_is() {
        Assert.assertThat(Strings.strip("foo"), CoreMatchers.is("foo"));
        Assert.assertThat(Strings.strip("bar"), CoreMatchers.is("bar"));
    }

    @Test
    public void should_strip_string_with_leading_whitespace() {
        Assert.assertThat(Strings.strip(" foo"), CoreMatchers.is("foo"));
        Assert.assertThat(Strings.strip("  bar"), CoreMatchers.is("bar"));
    }

    @Test
    public void should_strip_string_with_end_whitespace() {
        Assert.assertThat(Strings.strip("foo "), CoreMatchers.is("foo"));
        Assert.assertThat(Strings.strip("bar  "), CoreMatchers.is("bar"));
    }

    @Test
    public void should_strip_string_with_both_leading_and_end_whitespace() {
        Assert.assertThat(Strings.strip(" foo "), CoreMatchers.is("foo"));
        Assert.assertThat(Strings.strip("  bar  "), CoreMatchers.is("bar"));
    }
}

