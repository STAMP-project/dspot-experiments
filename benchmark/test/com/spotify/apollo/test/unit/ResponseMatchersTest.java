/**
 * -\-\-
 * Spotify Apollo Testing Helpers
 * --
 * Copyright (C) 2013 - 2015 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.apollo.test.unit;


import com.spotify.apollo.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ResponseMatchersTest {
    private static final Response<Void> NO_HEADER = Response.ok();

    private static final Response<Void> FOO_BAR_HEADER = Response.<Void>ok().withHeader("foo", "bar");

    private static final Response<String> NO_PAYLOAD = Response.ok();

    private static final Response<String> FOO_PAYLOAD = Response.forPayload("foo");

    @Test
    public void hasNoHeadersMatcherMatchesResponseWithoutHeader() throws Exception {
        Assert.assertThat(ResponseMatchersTest.NO_HEADER, hasNoHeaders());
    }

    @Test
    public void hasNoHeadersMatcherDoesNotMatchResponseWithSomeHeader() throws Exception {
        Assert.assertThat(ResponseMatchersTest.FOO_BAR_HEADER, CoreMatchers.not(hasNoHeaders()));
    }

    @Test
    public void hasHeaderMatcherMatchesResponseWithMatchingHeader() throws Exception {
        Assert.assertThat(ResponseMatchersTest.FOO_BAR_HEADER, hasHeader("foo", CoreMatchers.is("bar")));
    }

    @Test
    public void hasHeaderMatcherMatchesResponseWithMultipleHeadersAndMatchingHeader() throws Exception {
        Response<Void> multiMatchingResponse = ResponseMatchersTest.FOO_BAR_HEADER.withHeader("foo2", "bar2");
        Assert.assertThat(multiMatchingResponse, hasHeader("foo", CoreMatchers.is("bar")));
    }

    @Test
    public void hasHeaderMatcherDoesNotMatchResponseWithNoHeaders() throws Exception {
        Assert.assertThat(ResponseMatchersTest.NO_HEADER, CoreMatchers.not(hasHeader("foo", CoreMatchers.is("bar"))));
    }

    @Test
    public void hasHeaderMatcherDoesNotMatchResponseWithMissingHeader() throws Exception {
        Assert.assertThat(ResponseMatchersTest.FOO_BAR_HEADER, CoreMatchers.not(hasHeader("bazz", CoreMatchers.is("bar"))));
    }

    @Test
    public void hasHeaderMatcherDoesNotMatchResponseWithHeaderNotMatchingValue() throws Exception {
        Assert.assertThat(ResponseMatchersTest.FOO_BAR_HEADER, CoreMatchers.not(hasHeader("foo", CoreMatchers.is("bazz"))));
    }

    @Test
    public void doesNotHaveHeaderMatcherDoesNotMatchResponseWithMatchingHeader() throws Exception {
        Assert.assertThat(ResponseMatchersTest.FOO_BAR_HEADER, CoreMatchers.not(doesNotHaveHeader("foo")));
    }

    @Test
    public void doesNotHaveHeaderMatcherMatchesResponseWithNonMatchingHeader() throws Exception {
        Assert.assertThat(ResponseMatchersTest.FOO_BAR_HEADER, doesNotHaveHeader("bazz"));
    }

    @Test
    public void doesNotHaveHeaderMatcherMatchesResponseWithNoHeaders() throws Exception {
        Assert.assertThat(ResponseMatchersTest.NO_HEADER, doesNotHaveHeader("foo"));
    }

    @Test
    public void hasNoPayloadMatcherMatchesResponseWithoutPayload() throws Exception {
        Assert.assertThat(ResponseMatchersTest.NO_PAYLOAD, hasNoPayload());
    }

    @Test
    public void hasNoPayloadMatcherDoesNotMatchesResponseWithPayload() throws Exception {
        Assert.assertThat(ResponseMatchersTest.FOO_PAYLOAD, CoreMatchers.not(ResponseMatchers.ResponseMatchers.<String>hasNoPayload()));
    }

    @Test
    public void hasPayloadMatcherMatchesResponseWithMatchingPayload() throws Exception {
        Assert.assertThat(ResponseMatchersTest.FOO_PAYLOAD, hasPayload(CoreMatchers.is("foo")));
    }

    @Test
    public void hasPayloadMatcherDoesNotMatchResponseWithNonMatchingPayload() throws Exception {
        Assert.assertThat(ResponseMatchersTest.FOO_PAYLOAD, CoreMatchers.not(hasPayload(CoreMatchers.is("bar"))));
    }

    @Test
    public void hasPayloadMatcherDoesNotMatchResponseWithNoPayload() throws Exception {
        Assert.assertThat(ResponseMatchersTest.NO_PAYLOAD, CoreMatchers.not(hasPayload(CoreMatchers.is("foo"))));
    }

    @Test
    public void hasPayloadMatcherDoesCallPayloadMatcherWhenResponseHasNoPayload() throws Exception {
        Matcher<String> payloadMatcher = Mockito.spy(CoreMatchers.is("foo"));
        Assert.assertThat(ResponseMatchersTest.NO_PAYLOAD, CoreMatchers.not(hasPayload(payloadMatcher)));
        Mockito.verify(payloadMatcher, Mockito.times(0)).matches(ArgumentMatchers.any());
    }

    @Test
    public void hasStatusMatcherMatchesResponseWithMatchingStatusType() throws Exception {
        Assert.assertThat(Response.ok(), hasStatus(StatusTypeMatchers.withCode(200)));
    }

    @Test
    public void hasStatusMatcherDoesNotMatchResponseWithNonMatchingStatusType() throws Exception {
        Assert.assertThat(Response.ok(), CoreMatchers.not(hasStatus(StatusTypeMatchers.withCode(400))));
    }
}

