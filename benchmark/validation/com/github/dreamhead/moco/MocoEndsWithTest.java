package com.github.dreamhead.moco;


import com.google.common.collect.ImmutableMultimap;
import com.google.common.net.HttpHeaders;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoEndsWithTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_match_uri() throws IOException {
        runWithConfiguration("ends_with.json");
        Assert.assertThat(helper.get(remoteUrl("/bar/foo")), CoreMatchers.is("uri_match"));
        Assert.assertThat(helper.get(remoteUrl("/blah/foo")), CoreMatchers.is("uri_match"));
    }

    @Test
    public void should_match_text() throws IOException {
        runWithConfiguration("ends_with.json");
        Assert.assertThat(helper.postContent(remoteUrl("/text-match"), "barfoo"), CoreMatchers.is("text_match"));
        Assert.assertThat(helper.postContent(remoteUrl("/text-match"), "blahfoo"), CoreMatchers.is("text_match"));
    }

    @Test
    public void should_match_header() throws IOException {
        runWithConfiguration("ends_with.json");
        Assert.assertThat(helper.getWithHeader(remoteUrl("/header-match"), ImmutableMultimap.of(HttpHeaders.CONTENT_TYPE, "application/json")), CoreMatchers.is("header_match"));
    }

    @Test
    public void should_match_query() throws IOException {
        runWithConfiguration("ends_with.json");
        Assert.assertThat(helper.get(remoteUrl("/query-match?foo=bar")), CoreMatchers.is("query_match"));
    }
}

