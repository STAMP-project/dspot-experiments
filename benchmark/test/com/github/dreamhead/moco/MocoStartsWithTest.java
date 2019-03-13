package com.github.dreamhead.moco;


import com.google.common.collect.ImmutableMultimap;
import com.google.common.net.HttpHeaders;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoStartsWithTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_match_uri() throws IOException {
        runWithConfiguration("starts_with.json");
        Assert.assertThat(helper.get(remoteUrl("/foo/bar")), CoreMatchers.is("uri_match"));
        Assert.assertThat(helper.get(remoteUrl("/foo/blah")), CoreMatchers.is("uri_match"));
    }

    @Test
    public void should_match_text() throws IOException {
        runWithConfiguration("starts_with.json");
        Assert.assertThat(helper.postContent(remoteUrl("/text-match"), "foobar"), CoreMatchers.is("text_match"));
        Assert.assertThat(helper.postContent(remoteUrl("/text-match"), "fooblah"), CoreMatchers.is("text_match"));
    }

    @Test
    public void should_match_header() throws IOException {
        runWithConfiguration("starts_with.json");
        Assert.assertThat(helper.getWithHeader(remoteUrl("/header-match"), ImmutableMultimap.of(HttpHeaders.CONTENT_TYPE, "application/json")), CoreMatchers.is("header_match"));
        Assert.assertThat(helper.getWithHeader(remoteUrl("/header-match"), ImmutableMultimap.of(HttpHeaders.CONTENT_TYPE, "application/xml")), CoreMatchers.is("header_match"));
    }

    @Test
    public void should_match_query() throws IOException {
        runWithConfiguration("starts_with.json");
        Assert.assertThat(helper.get(remoteUrl("/query-match?foo=bar")), CoreMatchers.is("query_match"));
        Assert.assertThat(helper.get(remoteUrl("/query-match?foo=blah")), CoreMatchers.is("query_match"));
    }
}

