package com.github.dreamhead.moco;


import com.google.common.collect.ImmutableMultimap;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoContainTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_match_uri() throws IOException {
        runWithConfiguration("contain.json");
        Assert.assertThat(helper.get(remoteUrl("/foo/bar")), CoreMatchers.is("uri_match"));
        Assert.assertThat(helper.get(remoteUrl("/bar/foo")), CoreMatchers.is("uri_match"));
        Assert.assertThat(helper.get(remoteUrl("/bar/foo/blah")), CoreMatchers.is("uri_match"));
    }

    @Test
    public void should_match_text() throws IOException {
        runWithConfiguration("contain.json");
        Assert.assertThat(helper.postContent(remoteUrl("/text-match"), "foobar"), CoreMatchers.is("text_match"));
        Assert.assertThat(helper.postContent(remoteUrl("/text-match"), "barfoo"), CoreMatchers.is("text_match"));
        Assert.assertThat(helper.postContent(remoteUrl("/text-match"), "foobarblah"), CoreMatchers.is("text_match"));
    }

    @Test
    public void should_match_header() throws IOException {
        runWithConfiguration("contain.json");
        Assert.assertThat(helper.getWithHeader(remoteUrl("/header-match"), ImmutableMultimap.of("foo", "bar/blah")), CoreMatchers.is("header_match"));
        Assert.assertThat(helper.getWithHeader(remoteUrl("/header-match"), ImmutableMultimap.of("foo", "application/bar")), CoreMatchers.is("header_match"));
        Assert.assertThat(helper.getWithHeader(remoteUrl("/header-match"), ImmutableMultimap.of("foo", "application/bar/blah")), CoreMatchers.is("header_match"));
    }

    @Test
    public void should_match_query() throws IOException {
        runWithConfiguration("contain.json");
        Assert.assertThat(helper.get(remoteUrl("/query-match?foo=barblah")), CoreMatchers.is("query_match"));
        Assert.assertThat(helper.get(remoteUrl("/query-match?foo=blahbar")), CoreMatchers.is("query_match"));
        Assert.assertThat(helper.get(remoteUrl("/query-match?foo=blbarah")), CoreMatchers.is("query_match"));
    }
}

