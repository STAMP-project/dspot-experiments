/**
 * -\-\-
 * Spotify Apollo API Interfaces
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
package com.spotify.apollo;


import Status.OK;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

import static Family.SUCCESSFUL;


public class ResponseTest {
    @Test
    public void allowsOverrideHeaderValues() {
        Response<?> response = Response.forStatus(OK).withHeader("Content-Type", "application/json").withHeader("Content-Type", "application/protobuf");
        Assert.assertEquals(Optional.of("application/protobuf"), response.header("Content-Type"));
    }

    @Test
    public void allowsAddingMultipleHeaders() {
        Map<String, String> headers = ImmutableMap.of("Content-Type", "application/protobuf", "Content-Length", "123");
        Response<?> response = Response.forStatus(OK).withHeader("Content-Type", "application/json").withHeaders(headers);
        Assert.assertEquals(Optional.of("application/protobuf"), response.header("Content-Type"));
        Assert.assertEquals(Optional.of("123"), response.header("Content-Length"));
    }

    @Test
    public void shouldReturnHeaderCaseInsensitive() throws Exception {
        MatcherAssert.assertThat(Response.ok().withHeader("hEaDEr", "value").header("HeadeR"), CoreMatchers.is(Optional.of("value")));
    }

    @Test
    public void shouldReturnHeaderEntriesPreservingLetterCase() throws Exception {
        List<Map.Entry<String, String>> headerEntries = Response.ok().withHeader("HEAder", "value").headerEntries();
        MatcherAssert.assertThat(headerEntries.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(headerEntries.get(0).getKey(), CoreMatchers.is("HEAder"));
    }

    @Test
    public void shouldReturnHeadersMapPreservingLetterCase() throws Exception {
        Map<String, String> headers = Response.ok().withHeader("HEAder", "value").headers();
        MatcherAssert.assertThat(headers.get("HEAder"), CoreMatchers.is("value"));
    }

    @Test
    public void allowsOverrideHeaderValuesInHeadersMap() {
        Response<?> response = Response.forStatus(OK).withHeader("Content-Type", "application/json").withHeader("Content-Type", "application/protobuf");
        Assert.assertEquals("application/protobuf", response.headers().get("Content-Type"));
    }

    @Test
    public void shouldHaveSingletonOK() throws Exception {
        Response<Object> ok1 = Response.ok();
        Response<Object> ok2 = Response.forStatus(OK);
        Assert.assertSame(ok1, ok2);
    }

    @Test
    public void shouldNotIgnoreCustomOk() throws Exception {
        Response<Object> ok1 = Response.ok();
        Response<Object> ok2 = Response.forStatus(new ResponseTest.CustomOK());
        Assert.assertNotSame(ok1, ok2);
    }

    static class CustomOK implements StatusType {
        @Override
        public int code() {
            return 200;
        }

        @Override
        public String reasonPhrase() {
            return "Is more than OK";
        }

        @Override
        public Family family() {
            return SUCCESSFUL;
        }

        @Override
        public StatusType withReasonPhrase(String reasonPhrase) {
            return this;
        }
    }
}

