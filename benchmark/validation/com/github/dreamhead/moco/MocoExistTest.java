package com.github.dreamhead.moco;


import com.google.common.collect.ImmutableMultimap;
import com.google.common.net.HttpHeaders;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoExistTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_exist_text() throws IOException {
        runWithConfiguration("exist.json");
        Assert.assertThat(helper.postContent(remoteUrl("/text-match"), "anything"), CoreMatchers.is("text_match"));
        Assert.assertThat(helper.postContent(remoteUrl("/text-match"), "whatever"), CoreMatchers.is("text_match"));
    }

    @Test
    public void should_not_exist_text() throws IOException {
        runWithConfiguration("exist.json");
        Assert.assertThat(helper.get(remoteUrl("/text-not-match")), CoreMatchers.is("text_not_match"));
    }

    @Test
    public void should_match_header() throws IOException {
        runWithConfiguration("exist.json");
        Assert.assertThat(helper.getWithHeader(remoteUrl("/header-match"), ImmutableMultimap.of(HttpHeaders.CONTENT_TYPE, "application/json")), CoreMatchers.is("header_match"));
        Assert.assertThat(helper.getWithHeader(remoteUrl("/header-match"), ImmutableMultimap.of(HttpHeaders.CONTENT_TYPE, "application/xml")), CoreMatchers.is("header_match"));
    }

    @Test
    public void should_not_match_header() throws IOException {
        runWithConfiguration("exist.json");
        Assert.assertThat(helper.get(remoteUrl("/header-not-match")), CoreMatchers.is("header_not_match"));
    }

    @Test
    public void should_match_query() throws IOException {
        runWithConfiguration("exist.json");
        Assert.assertThat(helper.get(remoteUrl("/query-not-match")), CoreMatchers.is("query_not_match"));
    }
}

