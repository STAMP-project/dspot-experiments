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
package com.linecorp.armeria.internal;


import ExtensionHeaderNames.STREAM_ID;
import HttpHeaderNames.AUTHORITY;
import HttpHeaderNames.AUTHORIZATION;
import HttpHeaderNames.CACHE_CONTROL;
import HttpHeaderNames.CONNECTION;
import HttpHeaderNames.CONTENT_ENCODING;
import HttpHeaderNames.CONTENT_LENGTH;
import HttpHeaderNames.CONTENT_RANGE;
import HttpHeaderNames.CONTENT_TYPE;
import HttpHeaderNames.COOKIE;
import HttpHeaderNames.DATE;
import HttpHeaderNames.EXPECT;
import HttpHeaderNames.LOCATION;
import HttpHeaderNames.MAX_FORWARDS;
import HttpHeaderNames.METHOD;
import HttpHeaderNames.PATH;
import HttpHeaderNames.PRAGMA;
import HttpHeaderNames.PROXY_AUTHENTICATE;
import HttpHeaderNames.PROXY_AUTHORIZATION;
import HttpHeaderNames.RANGE;
import HttpHeaderNames.RETRY_AFTER;
import HttpHeaderNames.SCHEME;
import HttpHeaderNames.STATUS;
import HttpHeaderNames.TE;
import HttpHeaderNames.TRAILER;
import HttpHeaderNames.TRANSFER_ENCODING;
import HttpHeaderNames.VARY;
import HttpHeaderNames.WARNING;
import HttpHeaderNames.WWW_AUTHENTICATE;
import HttpHeaderValues.GZIP;
import HttpHeaderValues.TRAILERS;
import HttpVersion.HTTP_1_1;
import MediaType.PLAIN_TEXT_UTF_8;
import com.linecorp.armeria.common.DefaultHttpHeaders;
import com.linecorp.armeria.common.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames.HOST;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Headers;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.junit.Test;


public class ArmeriaHttpUtilTest {
    @Test
    public void testConcatPaths() throws Exception {
        assertThat(ArmeriaHttpUtil.concatPaths(null, "a")).isEqualTo("/a");
        assertThat(ArmeriaHttpUtil.concatPaths(null, "/a")).isEqualTo("/a");
        assertThat(ArmeriaHttpUtil.concatPaths("", "a")).isEqualTo("/a");
        assertThat(ArmeriaHttpUtil.concatPaths("", "/a")).isEqualTo("/a");
        assertThat(ArmeriaHttpUtil.concatPaths("/", "a")).isEqualTo("/a");
        assertThat(ArmeriaHttpUtil.concatPaths("/", "/a")).isEqualTo("/a");
        assertThat(ArmeriaHttpUtil.concatPaths("/a", "b")).isEqualTo("/a/b");
        assertThat(ArmeriaHttpUtil.concatPaths("/a", "/b")).isEqualTo("/a/b");
        assertThat(ArmeriaHttpUtil.concatPaths("/a/", "/b")).isEqualTo("/a/b");
    }

    @Test
    public void testDecodePath() throws Exception {
        // Fast path
        final String pathThatDoesNotNeedDecode = "/foo_bar_baz";
        assertThat(ArmeriaHttpUtil.decodePath(pathThatDoesNotNeedDecode)).isSameAs(pathThatDoesNotNeedDecode);
        // Slow path
        assertThat(ArmeriaHttpUtil.decodePath("/foo%20bar\u007fbaz")).isEqualTo("/foo bar\u007fbaz");
        assertThat(ArmeriaHttpUtil.decodePath("/%C2%A2")).isEqualTo("/?");// Valid UTF-8 sequence

        assertThat(ArmeriaHttpUtil.decodePath("/%20\u0080")).isEqualTo("/ ?");// Unallowed character

        assertThat(ArmeriaHttpUtil.decodePath("/%")).isEqualTo("/?");// No digit

        assertThat(ArmeriaHttpUtil.decodePath("/%1")).isEqualTo("/?");// Only a single digit

        assertThat(ArmeriaHttpUtil.decodePath("/%G0")).isEqualTo("/?");// First digit is not hex.

        assertThat(ArmeriaHttpUtil.decodePath("/%0G")).isEqualTo("/?");// Second digit is not hex.

        assertThat(ArmeriaHttpUtil.decodePath("/%C3%28")).isEqualTo("/?(");// Invalid UTF-8 sequence

    }

    @Test
    public void testParseCacheControl() {
        final Map<String, String> values = new LinkedHashMap<>();
        final BiConsumer<String, String> cb = ( name, value) -> assertThat(values.put(name, value)).isNull();
        // Make sure an effectively empty string does not invoke a callback.
        ArmeriaHttpUtil.parseCacheControl("", cb);
        assertThat(values).isEmpty();
        ArmeriaHttpUtil.parseCacheControl(" \t ", cb);
        assertThat(values).isEmpty();
        ArmeriaHttpUtil.parseCacheControl(" ,,=, =,= ,", cb);
        assertThat(values).isEmpty();
        // Name only.
        ArmeriaHttpUtil.parseCacheControl("no-cache", cb);
        assertThat(values).hasSize(1).containsEntry("no-cache", null);
        values.clear();
        ArmeriaHttpUtil.parseCacheControl(" no-cache ", cb);
        assertThat(values).hasSize(1).containsEntry("no-cache", null);
        values.clear();
        ArmeriaHttpUtil.parseCacheControl("no-cache ,", cb);
        assertThat(values).hasSize(1).containsEntry("no-cache", null);
        values.clear();
        // Name and value.
        ArmeriaHttpUtil.parseCacheControl("max-age=86400", cb);
        assertThat(values).hasSize(1).containsEntry("max-age", "86400");
        values.clear();
        ArmeriaHttpUtil.parseCacheControl(" max-age = 86400 ", cb);
        assertThat(values).hasSize(1).containsEntry("max-age", "86400");
        values.clear();
        ArmeriaHttpUtil.parseCacheControl(" max-age = 86400 ,", cb);
        assertThat(values).hasSize(1).containsEntry("max-age", "86400");
        values.clear();
        ArmeriaHttpUtil.parseCacheControl("max-age=\"86400\"", cb);
        assertThat(values).hasSize(1).containsEntry("max-age", "86400");
        values.clear();
        ArmeriaHttpUtil.parseCacheControl(" max-age = \"86400\" ", cb);
        assertThat(values).hasSize(1).containsEntry("max-age", "86400");
        values.clear();
        ArmeriaHttpUtil.parseCacheControl(" max-age = \"86400\" ,", cb);
        assertThat(values).hasSize(1).containsEntry("max-age", "86400");
        values.clear();
        // Multiple names and values.
        ArmeriaHttpUtil.parseCacheControl("a,b=c,d,e=\"f\",g", cb);
        assertThat(values).hasSize(5).containsEntry("a", null).containsEntry("b", "c").containsEntry("d", null).containsEntry("e", "f").containsEntry("g", null);
    }

    @Test
    public void outboundCookiesMustBeMergedForHttp1() throws Http2Exception {
        final HttpHeaders in = new DefaultHttpHeaders();
        in.add(COOKIE, "a=b; c=d");
        in.add(COOKIE, "e=f;g=h");
        in.addObject(CONTENT_TYPE, PLAIN_TEXT_UTF_8);
        in.add(COOKIE, "i=j");
        in.add(COOKIE, "k=l;");
        final io.netty.handler.codec.http.HttpHeaders out = new io.netty.handler.codec.http.DefaultHttpHeaders();
        ArmeriaHttpUtil.toNettyHttp1(0, in, out, HTTP_1_1, false, true);
        assertThat(out.getAll(COOKIE)).containsExactly("a=b; c=d; e=f; g=h; i=j; k=l");
    }

    @Test
    public void outboundCookiesMustBeSplitForHttp2() {
        final HttpHeaders in = new DefaultHttpHeaders();
        in.add(COOKIE, "a=b; c=d");
        in.add(COOKIE, "e=f;g=h");
        in.addObject(CONTENT_TYPE, PLAIN_TEXT_UTF_8);
        in.add(COOKIE, "i=j");
        in.add(COOKIE, "k=l;");
        final Http2Headers out = ArmeriaHttpUtil.toNettyHttp2(in, true);
        assertThat(out.getAll(COOKIE)).containsExactly("a=b", "c=d", "e=f", "g=h", "i=j", "k=l");
    }

    @Test
    public void inboundCookiesMustBeMergedForHttp1() {
        final io.netty.handler.codec.http.HttpHeaders in = new io.netty.handler.codec.http.DefaultHttpHeaders();
        final HttpHeaders out = new DefaultHttpHeaders();
        in.add(COOKIE, "a=b; c=d");
        in.add(COOKIE, "e=f;g=h");
        in.add(CONTENT_TYPE, PLAIN_TEXT_UTF_8);
        in.add(COOKIE, "i=j");
        in.add(COOKIE, "k=l;");
        ArmeriaHttpUtil.toArmeria(in, out);
        assertThat(out.getAll(COOKIE)).containsExactly("a=b; c=d; e=f; g=h; i=j; k=l");
    }

    @Test
    public void endOfStreamSet() {
        final Http2Headers in = new DefaultHttp2Headers();
        final HttpHeaders out = ArmeriaHttpUtil.toArmeria(in, true);
        assertThat(out.isEndOfStream()).isTrue();
        final HttpHeaders out2 = ArmeriaHttpUtil.toArmeria(in, false);
        assertThat(out2.isEndOfStream()).isFalse();
    }

    @Test
    public void inboundCookiesMustBeMergedForHttp2() {
        final Http2Headers in = new DefaultHttp2Headers();
        in.add(COOKIE, "a=b; c=d");
        in.add(COOKIE, "e=f;g=h");
        in.addObject(CONTENT_TYPE, PLAIN_TEXT_UTF_8);
        in.add(COOKIE, "i=j");
        in.add(COOKIE, "k=l;");
        final HttpHeaders out = ArmeriaHttpUtil.toArmeria(in, false);
        assertThat(out.getAll(COOKIE)).containsExactly("a=b; c=d; e=f; g=h; i=j; k=l");
    }

    @Test
    public void setHttp2AuthorityWithoutUserInfo() {
        final HttpHeaders headers = new DefaultHttpHeaders();
        ArmeriaHttpUtil.setHttp2Authority("foo", headers);
        assertThat(headers.authority()).isEqualTo("foo");
    }

    @Test
    public void setHttp2AuthorityWithUserInfo() {
        final HttpHeaders headers = new DefaultHttpHeaders();
        ArmeriaHttpUtil.setHttp2Authority("info@foo", headers);
        assertThat(headers.authority()).isEqualTo("foo");
        ArmeriaHttpUtil.setHttp2Authority("@foo.bar", headers);
        assertThat(headers.authority()).isEqualTo("foo.bar");
    }

    @Test
    public void setHttp2AuthorityNullOrEmpty() {
        final HttpHeaders headers = new DefaultHttpHeaders();
        ArmeriaHttpUtil.setHttp2Authority(null, headers);
        assertThat(headers.authority()).isNull();
        ArmeriaHttpUtil.setHttp2Authority("", headers);
        assertThat(headers.authority()).isEmpty();
    }

    @Test(expected = IllegalArgumentException.class)
    public void setHttp2AuthorityWithEmptyAuthority() {
        ArmeriaHttpUtil.setHttp2Authority("info@", new DefaultHttpHeaders());
    }

    @Test
    public void stripTEHeaders() {
        final io.netty.handler.codec.http.HttpHeaders in = new io.netty.handler.codec.http.DefaultHttpHeaders();
        in.add(TE, GZIP);
        final HttpHeaders out = new DefaultHttpHeaders();
        ArmeriaHttpUtil.toArmeria(in, out);
        assertThat(out).isEmpty();
    }

    @Test
    public void stripTEHeadersExcludingTrailers() {
        final io.netty.handler.codec.http.HttpHeaders in = new io.netty.handler.codec.http.DefaultHttpHeaders();
        in.add(TE, GZIP);
        in.add(TE, TRAILERS);
        final HttpHeaders out = new DefaultHttpHeaders();
        ArmeriaHttpUtil.toArmeria(in, out);
        assertThat(out.get(TE)).isEqualTo(TRAILERS.toString());
    }

    @Test
    public void stripTEHeadersCsvSeparatedExcludingTrailers() {
        final io.netty.handler.codec.http.HttpHeaders in = new io.netty.handler.codec.http.DefaultHttpHeaders();
        in.add(TE, (((HttpHeaderValues.GZIP) + ",") + (HttpHeaderValues.TRAILERS)));
        final HttpHeaders out = new DefaultHttpHeaders();
        ArmeriaHttpUtil.toArmeria(in, out);
        assertThat(out.get(TE)).isEqualTo(TRAILERS.toString());
    }

    @Test
    public void stripTEHeadersCsvSeparatedAccountsForValueSimilarToTrailers() {
        final io.netty.handler.codec.http.HttpHeaders in = new io.netty.handler.codec.http.DefaultHttpHeaders();
        in.add(TE, ((((HttpHeaderValues.GZIP) + ",") + (HttpHeaderValues.TRAILERS)) + "foo"));
        final HttpHeaders out = new DefaultHttpHeaders();
        ArmeriaHttpUtil.toArmeria(in, out);
        assertThat(out.contains(TE)).isFalse();
    }

    @Test
    public void stripTEHeadersAccountsForValueSimilarToTrailers() {
        final io.netty.handler.codec.http.HttpHeaders in = new io.netty.handler.codec.http.DefaultHttpHeaders();
        in.add(TE, ((HttpHeaderValues.TRAILERS) + "foo"));
        final HttpHeaders out = new DefaultHttpHeaders();
        ArmeriaHttpUtil.toArmeria(in, out);
        assertThat(out.contains(TE)).isFalse();
    }

    @Test
    public void stripTEHeadersAccountsForOWS() {
        final io.netty.handler.codec.http.HttpHeaders in = new io.netty.handler.codec.http.DefaultHttpHeaders();
        in.add(TE, ((" " + (HttpHeaderValues.TRAILERS)) + ' '));
        final HttpHeaders out = new DefaultHttpHeaders();
        ArmeriaHttpUtil.toArmeria(in, out);
        assertThat(out.get(TE)).isEqualTo(TRAILERS.toString());
    }

    @Test
    public void stripConnectionHeadersAndNominees() {
        final io.netty.handler.codec.http.HttpHeaders in = new io.netty.handler.codec.http.DefaultHttpHeaders();
        in.add(CONNECTION, "foo");
        in.add("foo", "bar");
        final HttpHeaders out = new DefaultHttpHeaders();
        ArmeriaHttpUtil.toArmeria(in, out);
        assertThat(out).isEmpty();
    }

    @Test
    public void stripConnectionNomineesWithCsv() {
        final io.netty.handler.codec.http.HttpHeaders in = new io.netty.handler.codec.http.DefaultHttpHeaders();
        in.add(CONNECTION, "foo,  bar");
        in.add("foo", "baz");
        in.add("bar", "qux");
        in.add("hello", "world");
        final HttpHeaders out = new DefaultHttpHeaders();
        ArmeriaHttpUtil.toArmeria(in, out);
        assertThat(out).hasSize(1);
        assertThat(out.get(com.linecorp.armeria.common.HttpHeaderNames.of("hello"))).isEqualTo("world");
    }

    @Test
    public void excludeBlacklistHeadersWhileHttp2ToHttp1() throws Http2Exception {
        final HttpHeaders in = new DefaultHttpHeaders();
        in.add(TRAILER, "foo");
        in.add(AUTHORITY, "bar");// Translated to host

        in.add(PATH, "dummy");
        in.add(METHOD, "dummy");
        in.add(SCHEME, "dummy");
        in.add(STATUS, "dummy");
        in.add(TRANSFER_ENCODING, "dummy");
        in.add(STREAM_ID.text(), "dummy");
        in.add(ExtensionHeaderNames.SCHEME.text(), "dummy");
        in.add(ExtensionHeaderNames.PATH.text(), "dummy");
        final io.netty.handler.codec.http.HttpHeaders out = new io.netty.handler.codec.http.DefaultHttpHeaders();
        ArmeriaHttpUtil.toNettyHttp1(0, in, out, HTTP_1_1, false, false);
        assertThat(out).isEqualTo(new io.netty.handler.codec.http.DefaultHttpHeaders().add(io.netty.handler.codec.http.HttpHeaderNames.TRAILER, "foo").add(HOST, "bar"));
    }

    @Test
    public void excludeBlacklistInTrailingHeaders() throws Http2Exception {
        final HttpHeaders in = new DefaultHttpHeaders();
        in.add(com.linecorp.armeria.common.HttpHeaderNames.of("foo"), "bar");
        in.add(TRANSFER_ENCODING, "dummy");
        in.add(CONTENT_LENGTH, "dummy");
        in.add(CACHE_CONTROL, "dummy");
        in.add(EXPECT, "dummy");
        in.add(HttpHeaderNames.HOST, "dummy");
        in.add(MAX_FORWARDS, "dummy");
        in.add(PRAGMA, "dummy");
        in.add(RANGE, "dummy");
        in.add(TE, "dummy");
        in.add(WWW_AUTHENTICATE, "dummy");
        in.add(AUTHORIZATION, "dummy");
        in.add(PROXY_AUTHENTICATE, "dummy");
        in.add(PROXY_AUTHORIZATION, "dummy");
        in.add(DATE, "dummy");
        in.add(LOCATION, "dummy");
        in.add(RETRY_AFTER, "dummy");
        in.add(VARY, "dummy");
        in.add(WARNING, "dummy");
        in.add(CONTENT_ENCODING, "dummy");
        in.add(CONTENT_TYPE, "dummy");
        in.add(CONTENT_RANGE, "dummy");
        in.add(TRAILER, "dummy");
        final io.netty.handler.codec.http.HttpHeaders outHttp1 = new io.netty.handler.codec.http.DefaultHttpHeaders();
        ArmeriaHttpUtil.toNettyHttp1(0, in, outHttp1, HTTP_1_1, true, false);
        assertThat(outHttp1).isEqualTo(new io.netty.handler.codec.http.DefaultHttpHeaders().add("foo", "bar"));
        final Http2Headers outHttp2 = ArmeriaHttpUtil.toNettyHttp2(in, true);
        assertThat(outHttp2).isEqualTo(new DefaultHttp2Headers().add("foo", "bar"));
    }
}

