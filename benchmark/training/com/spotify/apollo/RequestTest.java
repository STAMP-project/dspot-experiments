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


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import okio.ByteString;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class RequestTest {
    @Test
    public void shouldReturnNullForMissingParameterWithDefaultMethod() throws Exception {
        MatcherAssert.assertThat(request("/foo").parameter("missing"), CoreMatchers.is(Optional.empty()));
    }

    @Test
    public void shouldReturnNullForMissingParameter() throws Exception {
        MatcherAssert.assertThat(request("/foo").parameter("missing"), CoreMatchers.is(Optional.empty()));
    }

    @Test
    public void shouldReturnFirstParameterValueWithDefaultImplementation() throws Exception {
        MatcherAssert.assertThat(request("/foo?name=value1&name=value2").parameter("name"), CoreMatchers.is(Optional.of("value1")));
    }

    @Test
    public void shouldReturnFirstParameterValue() throws Exception {
        MatcherAssert.assertThat(request("/foo?name=value1&name=value2").parameter("name"), CoreMatchers.is(Optional.of("value1")));
    }

    @Test
    public void shouldReturnAllParameterValues() throws Exception {
        MatcherAssert.assertThat(request("/foo?name=value1&name=value2").parameters().get("name"), CoreMatchers.is(ImmutableList.of("value1", "value2")));
    }

    @Test
    public void shouldReturnNullForMissingHeaderWithDefaultMethod() throws Exception {
        MatcherAssert.assertThat(request("/foo").header("missing"), CoreMatchers.is(Optional.empty()));
    }

    @Test
    public void shouldReturnNullForMissingHeader() throws Exception {
        MatcherAssert.assertThat(request("/foo").header("missing"), CoreMatchers.is(Optional.empty()));
    }

    @Test
    public void shouldReturnHeaderWithDefaultMethod() throws Exception {
        MatcherAssert.assertThat(requestWithHeader("/foo", "header", "value").header("header"), CoreMatchers.is(Optional.of("value")));
    }

    @Test
    public void shouldReturnHeader() throws Exception {
        MatcherAssert.assertThat(requestWithHeader("/foo", "header", "value").header("header"), CoreMatchers.is(Optional.of("value")));
    }

    @Test
    public void shouldReturnHeaderCaseInsensitive() throws Exception {
        MatcherAssert.assertThat(requestWithHeader("/foo", "hEaDEr", "value").header("HeadeR"), CoreMatchers.is(Optional.of("value")));
    }

    @Test
    public void shouldReturnHeadersPreservingLetterCase() throws Exception {
        List<Map.Entry<String, String>> headerEntries = requestWithHeader("/foo", "HEAder", "value").headerEntries();
        MatcherAssert.assertThat(headerEntries.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(headerEntries.get(0).getKey(), CoreMatchers.is("HEAder"));
    }

    @Test
    public void shouldReturnNoPayload() throws Exception {
        MatcherAssert.assertThat(request("/foo").payload(), CoreMatchers.is(Optional.empty()));
    }

    @Test
    public void shouldReturnPayload() throws Exception {
        ByteString payload = ByteString.encodeUtf8("payload");
        MatcherAssert.assertThat(requestWithPayload("/foo", payload).payload(), CoreMatchers.is(Optional.of(payload)));
    }

    @Test
    public void shouldAllowModifyingUri() throws Exception {
        MatcherAssert.assertThat(request("/foo").withUri("/fie").uri(), CoreMatchers.is("/fie"));
    }

    @Test
    public void shouldMergeHeaders() throws Exception {
        Map<String, String> newHeaders = ImmutableMap.of("newHeader", "value1", "newHeader2", "value2");
        MatcherAssert.assertThat(requestWithHeader("/foo", "old", "value").withHeaders(newHeaders).headers(), CoreMatchers.is(ImmutableMap.of("old", "value", "newHeader", "value1", "newHeader2", "value2")));
    }

    @Test
    public void shouldReplaceExistingHeader() throws Exception {
        Map<String, String> newHeaders = ImmutableMap.of("newHeader", "value1", "old", "value2");
        MatcherAssert.assertThat(requestWithHeader("/foo", "old", "value").withHeaders(newHeaders).headers(), CoreMatchers.is(ImmutableMap.of("old", "value2", "newHeader", "value1")));
    }

    @Test
    public void shouldClearHeaders() throws Exception {
        MatcherAssert.assertThat(requestWithHeader("/foo", "old", "value").clearHeaders().headers(), CoreMatchers.is(ImmutableMap.of()));
    }

    @Test
    public void shouldNotHaveTTLByDefault() throws Exception {
        MatcherAssert.assertThat(request("/foo").ttl(), CoreMatchers.is(Optional.empty()));
    }

    @Test
    public void shouldSetTtl() throws Exception {
        MatcherAssert.assertThat(request("/foo").withTtl(Duration.ofSeconds(1)).ttl().get(), CoreMatchers.is(Duration.ofSeconds(1)));
    }
}

