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
package com.linecorp.armeria.common;


import MediaType.PLAIN_TEXT_UTF_8;
import ServerCacheControl.DISABLED;
import io.netty.util.AsciiString;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.junit.Test;


public class HttpHeadersTest {
    @Test
    public void testCaseInsensitiveHeaderNames() throws Exception {
        final HttpHeaders headers = HttpHeaders.of(HttpHeaderNames.of("header1"), "value1", HttpHeaderNames.of("HEADER2"), "value2", HttpHeaderNames.of("Header3"), "VALUE3");
        assertThat(headers.get(HttpHeaderNames.of("HeAdEr1"))).isEqualTo("value1");
        assertThat(headers.get(HttpHeaderNames.of("header2"))).isEqualTo("value2");
        assertThat(headers.get(HttpHeaderNames.of("HEADER3"))).isEqualTo("VALUE3");
        assertThat(headers.names()).containsExactlyInAnyOrder(HttpHeaderNames.of("header1"), HttpHeaderNames.of("header2"), HttpHeaderNames.of("header3"));
    }

    @Test
    public void testInvalidHeaderName() throws Exception {
        assertThatThrownBy(() -> HttpHeaders.of(((AsciiString) (null)), "value1")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> HttpHeaders.of(of(""), "value1")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void contentType() {
        final HttpHeaders headers = HttpHeaders.of();
        headers.contentType(MediaType.ANY_TYPE);
        assertThat(headers.contentType()).isSameAs(MediaType.ANY_TYPE);
        assertThat(headers.get(HttpHeaderNames.CONTENT_TYPE)).isEqualTo(MediaType.ANY_TYPE.toString());
        headers.contentType(MediaType.ANY_APPLICATION_TYPE);
        assertThat(headers.contentType()).isSameAs(MediaType.ANY_APPLICATION_TYPE);
        assertThat(headers.get(HttpHeaderNames.CONTENT_TYPE)).isEqualTo(MediaType.ANY_APPLICATION_TYPE.toString());
        headers.setObject(HttpHeaderNames.CONTENT_TYPE, MediaType.ANY_TEXT_TYPE);
        assertThat(headers.contentType()).isSameAs(MediaType.ANY_TEXT_TYPE);
        assertThat(headers.get(HttpHeaderNames.CONTENT_TYPE)).isEqualTo(MediaType.ANY_TEXT_TYPE.toString());
        headers.set(HttpHeaderNames.CONTENT_TYPE, MediaType.ANY_AUDIO_TYPE.toString());
        assertThat(headers.contentType()).isSameAs(MediaType.ANY_AUDIO_TYPE);
        assertThat(headers.get(HttpHeaderNames.CONTENT_TYPE)).isEqualTo(MediaType.ANY_AUDIO_TYPE.toString());
    }

    @Test
    public void testSetObject() {
        final String expectedDate = "Mon, 3 Dec 2007 10:15:30 GMT";
        final Instant instant = Instant.parse("2007-12-03T10:15:30.00Z");
        final Date date = new Date(instant.toEpochMilli());
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(instant.toEpochMilli());
        final HttpHeaders headers = HttpHeaders.of();
        headers.setObject(HttpHeaderNames.DATE, date);
        headers.setObject(HttpHeaderNames.LAST_MODIFIED, instant);
        headers.setObject(HttpHeaderNames.IF_MODIFIED_SINCE, calendar);
        headers.setObject(HttpHeaderNames.CACHE_CONTROL, DISABLED);
        headers.setObject(HttpHeaderNames.CONTENT_TYPE, PLAIN_TEXT_UTF_8);
        assertThat(headers.get(HttpHeaderNames.DATE)).isEqualTo(expectedDate);
        assertThat(headers.get(HttpHeaderNames.LAST_MODIFIED)).isEqualTo(expectedDate);
        assertThat(headers.get(HttpHeaderNames.IF_MODIFIED_SINCE)).isEqualTo(expectedDate);
        assertThat(headers.get(HttpHeaderNames.CACHE_CONTROL)).isEqualTo("no-cache, no-store, must-revalidate");
        assertThat(headers.get(HttpHeaderNames.CONTENT_TYPE)).isEqualTo("text/plain; charset=utf-8");
    }
}

