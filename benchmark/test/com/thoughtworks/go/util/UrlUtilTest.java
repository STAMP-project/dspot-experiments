/**
 * Copyright 2016 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.util;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class UrlUtilTest {
    @Test
    public void shouldEncodeUrl() {
        Assert.assertThat(UrlUtil.encodeInUtf8("a%b"), Matchers.is("a%25b"));
    }

    @Test
    public void shouldEncodeAllPartsInUrl() {
        Assert.assertThat(UrlUtil.encodeInUtf8("a%b/c%d"), Matchers.is("a%25b/c%25d"));
    }

    @Test
    public void shouldKeepPrecedingSlash() {
        Assert.assertThat(UrlUtil.encodeInUtf8("/a%b/c%d"), Matchers.is("/a%25b/c%25d"));
    }

    @Test
    public void shouldKeepTrailingSlash() {
        Assert.assertThat(UrlUtil.encodeInUtf8("a%b/c%d/"), Matchers.is("a%25b/c%25d/"));
    }

    @Test
    public void shouldAppendQueryString() throws Exception {
        Assert.assertThat(UrlUtil.urlWithQuery("http://baz.quux", "foo", "bar"), Matchers.is("http://baz.quux?foo=bar"));
        Assert.assertThat(UrlUtil.urlWithQuery("http://baz.quux?bang=boom&hello=world", "foo", "bar"), Matchers.is("http://baz.quux?bang=boom&hello=world&foo=bar"));
        Assert.assertThat(UrlUtil.urlWithQuery("http://baz.quux:1000/hello/world?bang=boom", "foo", "bar"), Matchers.is("http://baz.quux:1000/hello/world?bang=boom&foo=bar"));
        Assert.assertThat(UrlUtil.urlWithQuery("http://baz.quux:1000/hello/world?bang=boom%20bang&quux=bar/baz&sha1=2jmj7l5rSw0yVb%2FvlWAYkK%2FYBwk%3D", "foo", "bar\\baz"), Matchers.is("http://baz.quux:1000/hello/world?bang=boom+bang&quux=bar%2Fbaz&sha1=2jmj7l5rSw0yVb%2FvlWAYkK%2FYBwk%3D&foo=bar%5Cbaz"));
        Assert.assertThat(UrlUtil.urlWithQuery("http://baz.quux:1000/hello/world?bang=boom#in_hell", "foo", "bar"), Matchers.is("http://baz.quux:1000/hello/world?bang=boom&foo=bar#in_hell"));
        Assert.assertThat(UrlUtil.urlWithQuery("http://user:loser@baz.quux:1000/hello/world#in_hell", "foo", "bar"), Matchers.is("http://user:loser@baz.quux:1000/hello/world?foo=bar#in_hell"));
    }

    @Test
    public void shouldGetGivenQueryParamFromUrl() throws Exception {
        String url = "http://localhost:8153?code=123&new_code=xyz";
        Assert.assertThat(UrlUtil.getQueryParamFromUrl(url, "code"), Matchers.is("123"));
        Assert.assertThat(UrlUtil.getQueryParamFromUrl(url, "new_code"), Matchers.is("xyz"));
    }

    @Test
    public void shouldReturnEmptyStringIfQueryParamIsNotAvailable() throws Exception {
        String url = "http://localhost:8153?code=123&new_code=xyz";
        Assert.assertThat(UrlUtil.getQueryParamFromUrl(url, "not_available"), Matchers.is(""));
    }

    @Test
    public void shouldReturnEmptyStringIfUrlIsInvalid() throws Exception {
        String url = "this is not valid url";
        Assert.assertThat(UrlUtil.getQueryParamFromUrl(url, "param"), Matchers.is(""));
    }

    @Test
    public void concatPathWithBaseUrl() throws Exception {
        Assert.assertThat(UrlUtil.concatPath("http://foo", "bar"), Matchers.is("http://foo/bar"));
        Assert.assertThat(UrlUtil.concatPath("http://foo/", "bar"), Matchers.is("http://foo/bar"));
    }
}

