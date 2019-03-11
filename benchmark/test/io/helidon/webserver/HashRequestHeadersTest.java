/**
 * Copyright (c) 2017, 2018, 2019 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.webserver;


import HashRequestHeaders.HUC_ACCEPT_DEFAULT;
import Http.Header.ACCEPT;
import Http.Header.ACCEPT_DATETIME;
import Http.Header.CONTENT_LENGTH;
import Http.Header.CONTENT_TYPE;
import Http.Header.COOKIE;
import Http.Header.DATE;
import Http.Header.IF_MODIFIED_SINCE;
import Http.Header.IF_UNMODIFIED_SINCE;
import Http.Header.REFERER;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_OCTET_STREAM;
import MediaType.TEXT_HTML;
import MediaType.TEXT_PLAIN;
import MediaType.TEXT_XML;
import io.helidon.common.CollectionsHelper;
import io.helidon.common.http.Parameters;
import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.number.IsCloseTo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link HashRequestHeaders}.
 */
public class HashRequestHeadersTest {
    private static final ZonedDateTime ZDT = ZonedDateTime.of(2008, 6, 3, 11, 5, 30, 0, ZoneId.of("Z"));

    @Test
    public void allConcatenated() {
        HashRequestHeaders hs = withHeader("Foo", "val1", "val 2", "val3,val4", "val5");
        MatcherAssert.assertThat(hs.value("Foo").orElse(null), CoreMatchers.is("val1,val 2,val3,val4,val5"));
    }

    @Test
    public void allSepar() {
        HashRequestHeaders hs = withHeader("Foo", "val1", "val 2", "val3,val4", "val5", "val 6,val7,val 8", "val9");
        MatcherAssert.assertThat(hs.values("Foo"), IsCollectionContaining.hasItems("val1", "val 2", "val3", "val4", "val5", "val 6", "val7", "val 8", "val9"));
    }

    @Test
    public void contentType() {
        HashRequestHeaders hs = withHeader(CONTENT_TYPE, APPLICATION_JSON.toString());
        MatcherAssert.assertThat(hs.contentType().isPresent(), CoreMatchers.is(true));
        MatcherAssert.assertThat(hs.contentType().get(), CoreMatchers.is(APPLICATION_JSON));
    }

    @Test
    public void contentLength() {
        HashRequestHeaders hs = withHeader(CONTENT_LENGTH, "1024");
        MatcherAssert.assertThat(hs.contentLength().isPresent(), CoreMatchers.is(true));
        MatcherAssert.assertThat(hs.contentLength().getAsLong(), CoreMatchers.is(1024L));
    }

    @Test
    public void cookies() {
        HashRequestHeaders hs = withHeader(COOKIE, "foo=bar; aaa=bbb; c=what_the_hell; aaa=ccc", "$version=1; some=other; $Domain=google.com, aaa=eee, d=cool; $Domain=google.com; $Path=\"/foo\"");
        Parameters cookies = hs.cookies();
        MatcherAssert.assertThat(cookies.all("foo"), contains("bar"));
        MatcherAssert.assertThat(cookies.all("c"), contains("what_the_hell"));
        MatcherAssert.assertThat(cookies.all("aaa"), contains("bbb", "ccc", "eee"));
        MatcherAssert.assertThat(cookies.all("some"), contains("other"));
        MatcherAssert.assertThat(cookies.all("d"), contains("cool"));
    }

    @Test
    public void acceptedTypes() {
        HashRequestHeaders hs = withHeader(ACCEPT, "text/*;q=0.3, text/html;q=0.7, text/html;level=1, text/html;level=2;q=0.4");
        MatcherAssert.assertThat(hs.acceptedTypes().size(), CoreMatchers.is(4));
        MatcherAssert.assertThat(hs.acceptedTypes().get(0), CoreMatchers.is(createMt("text", "*", CollectionsHelper.mapOf("q", "0.3"))));
        MatcherAssert.assertThat(hs.acceptedTypes().get(0).qualityFactor(), IsCloseTo.closeTo(0, 0.3));
        MatcherAssert.assertThat(hs.acceptedTypes().get(1), CoreMatchers.is(createMt("text", "html", CollectionsHelper.mapOf("q", "0.7"))));
        MatcherAssert.assertThat(hs.acceptedTypes().get(2), CoreMatchers.is(createMt("text", "html", CollectionsHelper.mapOf("level", "1"))));
        MatcherAssert.assertThat(hs.acceptedTypes().get(3), CoreMatchers.is(createMt("text", "html", CollectionsHelper.mapOf("level", "2", "q", "0.4"))));
    }

    @Test
    public void hucDefaultAccept() {
        try {
            HashRequestHeaders hs = withHeader(ACCEPT, HUC_ACCEPT_DEFAULT);
            MatcherAssert.assertThat(hs.acceptedTypes().get(0), CoreMatchers.is(TEXT_HTML));
            MatcherAssert.assertThat(hs.acceptedTypes().get(1), CoreMatchers.is(createMt("image", "gif")));
            MatcherAssert.assertThat(hs.acceptedTypes().get(2), CoreMatchers.is(createMt("image", "jpeg")));
            MatcherAssert.assertThat(hs.acceptedTypes().get(3), CoreMatchers.is(createMt("*", "*", CollectionsHelper.mapOf("q", ".2"))));
        } catch (IllegalStateException ex) {
            Assertions.fail(ex.getMessage(), ex);
        }
    }

    @Test
    public void isAccepted() {
        HashRequestHeaders hs = withHeader(ACCEPT, "text/*;q=0.3, application/json;q=0.7");
        MatcherAssert.assertThat(hs.isAccepted(TEXT_HTML), CoreMatchers.is(true));
        MatcherAssert.assertThat(hs.isAccepted(TEXT_XML), CoreMatchers.is(true));
        MatcherAssert.assertThat(hs.isAccepted(APPLICATION_JSON), CoreMatchers.is(true));
        MatcherAssert.assertThat(hs.isAccepted(APPLICATION_OCTET_STREAM), CoreMatchers.is(false));
    }

    @Test
    public void bestAccepted() {
        HashRequestHeaders hs = withHeader(ACCEPT, "text/*;q=0.3, text/html;q=0.7, text/xml;q=0.4");
        MatcherAssert.assertThat(hs.bestAccepted(APPLICATION_JSON, TEXT_PLAIN, null, TEXT_XML).orElse(null), CoreMatchers.is(TEXT_XML));
        MatcherAssert.assertThat(hs.bestAccepted(APPLICATION_JSON, TEXT_HTML, TEXT_XML).orElse(null), CoreMatchers.is(TEXT_HTML));
        MatcherAssert.assertThat(hs.bestAccepted(APPLICATION_JSON, TEXT_PLAIN).orElse(null), CoreMatchers.is(TEXT_PLAIN));
        MatcherAssert.assertThat(hs.bestAccepted(APPLICATION_JSON).isPresent(), CoreMatchers.is(false));
        MatcherAssert.assertThat(hs.bestAccepted().isPresent(), CoreMatchers.is(false));
    }

    @Test
    public void acceptDatetime() {
        HashRequestHeaders hs = withHeader(ACCEPT_DATETIME, "Tue, 3 Jun 2008 11:05:30 GMT");
        MatcherAssert.assertThat(hs.acceptDatetime().orElse(null), CoreMatchers.is(HashRequestHeadersTest.ZDT));
    }

    @Test
    public void date() {
        HashRequestHeaders hs = withHeader(DATE, "Tue, 3 Jun 2008 11:05:30 GMT");
        MatcherAssert.assertThat(hs.date().orElse(null), CoreMatchers.is(HashRequestHeadersTest.ZDT));
    }

    @Test
    public void ifModifiedSince() {
        HashRequestHeaders hs = withHeader(IF_MODIFIED_SINCE, "Tue, 3 Jun 2008 11:05:30 GMT");
        MatcherAssert.assertThat(hs.ifModifiedSince().orElse(null), CoreMatchers.is(HashRequestHeadersTest.ZDT));
    }

    @Test
    public void ifUnmodifiedSince() {
        HashRequestHeaders hs = withHeader(IF_UNMODIFIED_SINCE, "Tue, 3 Jun 2008 11:05:30 GMT");
        MatcherAssert.assertThat(hs.ifUnmodifiedSince().orElse(null), CoreMatchers.is(HashRequestHeadersTest.ZDT));
    }

    @Test
    public void referer() {
        HashRequestHeaders hs = withHeader(REFERER, "http://www.google.com");
        MatcherAssert.assertThat(hs.referer().map(URI::toString).orElse(null), CoreMatchers.is("http://www.google.com"));
    }
}

