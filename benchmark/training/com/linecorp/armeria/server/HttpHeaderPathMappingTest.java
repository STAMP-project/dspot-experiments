/**
 * Copyright 2017 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server;


import HttpMethod.DELETE;
import HttpMethod.GET;
import HttpMethod.POST;
import HttpMethod.PUT;
import MediaType.JSON_UTF_8;
import MediaType.PLAIN_TEXT_UTF_8;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.linecorp.armeria.common.MediaTypeSet;
import org.junit.Test;

import static com.linecorp.armeria.common.MediaType.FORM_DATA;
import static com.linecorp.armeria.common.MediaType.JSON_UTF_8;
import static com.linecorp.armeria.common.MediaType.PLAIN_TEXT_UTF_8;


public class HttpHeaderPathMappingTest {
    private static final String PATH = "/test";

    private static final MediaTypeSet PRODUCIBLE_MEDIA_TYPES = new MediaTypeSet(JSON_UTF_8, FORM_DATA, PLAIN_TEXT_UTF_8);

    @Test
    public void testLoggerName() {
        HttpHeaderPathMapping mapping;
        mapping = new HttpHeaderPathMapping(PathMapping.of(HttpHeaderPathMappingTest.PATH), ImmutableSet.of(GET), ImmutableList.of(PLAIN_TEXT_UTF_8), ImmutableList.of(JSON_UTF_8));
        assertThat(mapping.loggerName()).isEqualTo("test.GET.consumes.text_plain.produces.application_json");
        mapping = new HttpHeaderPathMapping(PathMapping.of(HttpHeaderPathMappingTest.PATH), ImmutableSet.of(GET), ImmutableList.of(), ImmutableList.of(PLAIN_TEXT_UTF_8, JSON_UTF_8));
        assertThat(mapping.loggerName()).isEqualTo("test.GET.produces.text_plain.application_json");
        mapping = new HttpHeaderPathMapping(PathMapping.of(HttpHeaderPathMappingTest.PATH), ImmutableSet.of(GET, POST), ImmutableList.of(PLAIN_TEXT_UTF_8, JSON_UTF_8), ImmutableList.of());
        assertThat(mapping.loggerName()).isEqualTo("test.GET_POST.consumes.text_plain.application_json");
    }

    @Test
    public void testMetricName() {
        HttpHeaderPathMapping mapping;
        mapping = new HttpHeaderPathMapping(PathMapping.of(HttpHeaderPathMappingTest.PATH), ImmutableSet.of(GET), ImmutableList.of(PLAIN_TEXT_UTF_8), ImmutableList.of(JSON_UTF_8));
        assertThat(mapping.meterTag()).isEqualTo("exact:/test,methods:GET,consumes:text/plain,produces:application/json");
        mapping = new HttpHeaderPathMapping(PathMapping.of(HttpHeaderPathMappingTest.PATH), ImmutableSet.of(GET), ImmutableList.of(), ImmutableList.of(PLAIN_TEXT_UTF_8, JSON_UTF_8));
        assertThat(mapping.meterTag()).isEqualTo("exact:/test,methods:GET,produces:text/plain,application/json");
        mapping = new HttpHeaderPathMapping(PathMapping.of(HttpHeaderPathMappingTest.PATH), ImmutableSet.of(GET, POST), ImmutableList.of(PLAIN_TEXT_UTF_8, JSON_UTF_8), ImmutableList.of());
        assertThat(mapping.meterTag()).isEqualTo("exact:/test,methods:GET,POST,consumes:text/plain,application/json");
    }

    @Test
    public void testHttpHeader() {
        final HttpHeaderPathMapping mapping = new HttpHeaderPathMapping(PathMapping.of(HttpHeaderPathMappingTest.PATH), ImmutableSet.of(GET, POST), ImmutableList.of(), ImmutableList.of());// No media type negotiation

        assertThat(mapping.apply(HttpHeaderPathMappingTest.method(GET)).isPresent()).isTrue();
        assertThat(mapping.apply(HttpHeaderPathMappingTest.method(POST)).isPresent()).isTrue();
        // Always the lowest score because the media type negotiation is not supported.
        assertThat(mapping.apply(HttpHeaderPathMappingTest.method(GET)).hasLowestScore()).isTrue();
        assertThat(mapping.apply(HttpHeaderPathMappingTest.method(POST)).hasLowestScore()).isTrue();
        assertThat(mapping.apply(HttpHeaderPathMappingTest.method(PUT)).isPresent()).isFalse();
        assertThat(mapping.apply(HttpHeaderPathMappingTest.method(DELETE)).isPresent()).isFalse();
    }

    @Test
    public void testConsumeType() {
        final HttpHeaderPathMapping mapping = new HttpHeaderPathMapping(PathMapping.of(HttpHeaderPathMappingTest.PATH), ImmutableSet.of(POST), ImmutableList.of(JSON_UTF_8), ImmutableList.of());
        assertThat(mapping.apply(HttpHeaderPathMappingTest.consumeType(POST, JSON_UTF_8)).isPresent()).isTrue();
        assertThat(mapping.apply(HttpHeaderPathMappingTest.consumeType(POST, com.linecorp.armeria.common.MediaType.create("application", "json"))).isPresent()).isFalse();
    }

    @Test
    public void testProduceType() {
        final HttpHeaderPathMapping mapping = new HttpHeaderPathMapping(PathMapping.of(HttpHeaderPathMappingTest.PATH), ImmutableSet.of(GET), ImmutableList.of(), ImmutableList.of(JSON_UTF_8));
        assertThat(mapping.apply(HttpHeaderPathMappingTest.produceType(GET, "*/*")).isPresent()).isTrue();
        assertThat(mapping.apply(HttpHeaderPathMappingTest.produceType(GET, "application/json;charset=UTF-8")).isPresent()).isTrue();
        PathMappingResult result;
        result = mapping.apply(HttpHeaderPathMappingTest.produceType(GET, "application/json;charset=UTF-8;q=0.8,text/plain;q=0.9"));
        assertThat(result.isPresent()).isTrue();
        assertThat(result.score()).isEqualTo((-1));
        result = mapping.apply(HttpHeaderPathMappingTest.produceType(GET, "application/json;charset=UTF-8,text/plain;q=0.9"));
        assertThat(result.isPresent()).isTrue();
        assertThat(result.hasHighestScore()).isTrue();
        assertThat(mapping.apply(HttpHeaderPathMappingTest.produceType(GET, "application/x-www-form-urlencoded")).isPresent()).isFalse();
    }
}

