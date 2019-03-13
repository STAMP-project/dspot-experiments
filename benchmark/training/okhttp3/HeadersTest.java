/**
 * Copyright (C) 2012 Square, Inc.
 *
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
 */
package okhttp3;


import Util.EMPTY_HEADERS;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import okhttp3.internal.Internal;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http2.Header;
import okhttp3.internal.http2.Http2ExchangeCodec;
import org.junit.Assert;
import org.junit.Test;


public final class HeadersTest {
    static {
        Internal.initializeInstanceForTests();
    }

    @Test
    public void readNameValueBlockDropsForbiddenHeadersHttp2() throws IOException {
        Headers headerBlock = Headers.of(":status", "200 OK", ":version", "HTTP/1.1", "connection", "close");
        Request request = new Request.Builder().url("http://square.com/").build();
        Response response = Http2ExchangeCodec.readHttp2HeadersList(headerBlock, Protocol.HTTP_2).request(request).build();
        Headers headers = response.headers();
        Assert.assertEquals(1, headers.size());
        Assert.assertEquals(":version", headers.name(0));
        Assert.assertEquals("HTTP/1.1", headers.value(0));
    }

    @Test
    public void http2HeadersListDropsForbiddenHeadersHttp2() {
        Request request = new Request.Builder().url("http://square.com/").header("Connection", "upgrade").header("Upgrade", "websocket").header("Host", "square.com").header("TE", "gzip").build();
        List<Header> expected = TestUtil.headerEntries(":method", "GET", ":path", "/", ":authority", "square.com", ":scheme", "http");
        Assert.assertEquals(expected, Http2ExchangeCodec.http2HeadersList(request));
    }

    @Test
    public void http2HeadersListDontDropTeIfTrailersHttp2() {
        Request request = new Request.Builder().url("http://square.com/").header("TE", "trailers").build();
        List<Header> expected = TestUtil.headerEntries(":method", "GET", ":path", "/", ":scheme", "http", "te", "trailers");
        Assert.assertEquals(expected, Http2ExchangeCodec.http2HeadersList(request));
    }

    @Test
    public void ofTrims() {
        Headers headers = Headers.of("\t User-Agent \n", " \r OkHttp ");
        Assert.assertEquals("User-Agent", headers.name(0));
        Assert.assertEquals("OkHttp", headers.value(0));
    }

    @Test
    public void addParsing() {
        Headers headers = // Space after colon is not required.
        // Value whitespace is trimmed.
        // '\t' also counts as whitespace
        // Name trailing whitespace is trimmed.
        // Name leading whitespace is trimmed.
        new Headers.Builder().add("foo: bar").add(" foo: baz").add("foo : bak").add("\tkey\t:\tvalue\t").add("ping:  pong  ").add("kit:kat").build();
        Assert.assertEquals(Arrays.asList("bar", "baz", "bak"), headers.values("foo"));
        Assert.assertEquals(Arrays.asList("value"), headers.values("key"));
        Assert.assertEquals(Arrays.asList("pong"), headers.values("ping"));
        Assert.assertEquals(Arrays.asList("kat"), headers.values("kit"));
    }

    @Test
    public void addThrowsOnEmptyName() {
        try {
            new Headers.Builder().add(": bar");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            new Headers.Builder().add(" : bar");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void addThrowsOnNoColon() {
        try {
            new Headers.Builder().add("foo bar");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void addThrowsOnMultiColon() {
        try {
            new Headers.Builder().add(":status: 200 OK");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void addUnsafeNonAsciiRejectsUnicodeName() {
        try {
            Headers headers = new Headers.Builder().addUnsafeNonAscii("h?ader1", "value1").build();
            Assert.fail("Should have complained about invalid value");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Unexpected char 0xe9 at 1 in header name: h?ader1", expected.getMessage());
        }
    }

    @Test
    public void addUnsafeNonAsciiAcceptsUnicodeValue() {
        Headers headers = new Headers.Builder().addUnsafeNonAscii("header1", "valu?1").build();
        Assert.assertEquals("header1: valu\u00e91\n", headers.toString());
    }

    @Test
    public void ofThrowsOddNumberOfHeaders() {
        try {
            Headers.of("User-Agent", "OkHttp", "Content-Length");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void ofThrowsOnNull() {
        try {
            Headers.of("User-Agent", null);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void ofThrowsOnEmptyName() {
        try {
            Headers.of("", "OkHttp");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void ofAcceptsEmptyValue() {
        Headers headers = Headers.of("User-Agent", "");
        Assert.assertEquals("", headers.value(0));
    }

    @Test
    public void ofMakesDefensiveCopy() {
        String[] namesAndValues = new String[]{ "User-Agent", "OkHttp" };
        Headers headers = Headers.of(namesAndValues);
        namesAndValues[1] = "Chrome";
        Assert.assertEquals("OkHttp", headers.value(0));
    }

    @Test
    public void ofRejectsNullChar() {
        try {
            Headers.of("User-Agent", "Square\u0000OkHttp");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void ofMapThrowsOnNull() {
        try {
            Headers.of(Collections.singletonMap("User-Agent", null));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void ofMapThrowsOnEmptyName() {
        try {
            Headers.of(Collections.singletonMap("", "OkHttp"));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void ofMapThrowsOnBlankName() {
        try {
            Headers.of(Collections.singletonMap(" ", "OkHttp"));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void ofMapAcceptsEmptyValue() {
        Headers headers = Headers.of(Collections.singletonMap("User-Agent", ""));
        Assert.assertEquals("", headers.value(0));
    }

    @Test
    public void ofMapTrimsKey() {
        Headers headers = Headers.of(Collections.singletonMap(" User-Agent ", "OkHttp"));
        Assert.assertEquals("User-Agent", headers.name(0));
    }

    @Test
    public void ofMapTrimsValue() {
        Headers headers = Headers.of(Collections.singletonMap("User-Agent", " OkHttp "));
        Assert.assertEquals("OkHttp", headers.value(0));
    }

    @Test
    public void ofMapMakesDefensiveCopy() {
        Map<String, String> namesAndValues = new LinkedHashMap<>();
        namesAndValues.put("User-Agent", "OkHttp");
        Headers headers = Headers.of(namesAndValues);
        namesAndValues.put("User-Agent", "Chrome");
        Assert.assertEquals("OkHttp", headers.value(0));
    }

    @Test
    public void ofMapRejectsNullCharInName() {
        try {
            Headers.of(Collections.singletonMap("User-\u0000Agent", "OkHttp"));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void ofMapRejectsNullCharInValue() {
        try {
            Headers.of(Collections.singletonMap("User-Agent", "Square\u0000OkHttp"));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void toMultimapGroupsHeaders() {
        Headers headers = Headers.of("cache-control", "no-cache", "cache-control", "no-store", "user-agent", "OkHttp");
        Map<String, List<String>> headerMap = headers.toMultimap();
        Assert.assertEquals(2, headerMap.get("cache-control").size());
        Assert.assertEquals(1, headerMap.get("user-agent").size());
    }

    @Test
    public void toMultimapUsesCanonicalCase() {
        Headers headers = Headers.of("cache-control", "no-store", "Cache-Control", "no-cache", "User-Agent", "OkHttp");
        Map<String, List<String>> headerMap = headers.toMultimap();
        Assert.assertEquals(2, headerMap.get("cache-control").size());
        Assert.assertEquals(1, headerMap.get("user-agent").size());
    }

    @Test
    public void toMultimapAllowsCaseInsensitiveGet() {
        Headers headers = Headers.of("cache-control", "no-store", "Cache-Control", "no-cache");
        Map<String, List<String>> headerMap = headers.toMultimap();
        Assert.assertEquals(2, headerMap.get("cache-control").size());
        Assert.assertEquals(2, headerMap.get("Cache-Control").size());
    }

    @Test
    public void nameIndexesAreStrict() {
        Headers headers = Headers.of("a", "b", "c", "d");
        try {
            headers.name((-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
        Assert.assertEquals("a", headers.name(0));
        Assert.assertEquals("c", headers.name(1));
        try {
            headers.name(2);
            Assert.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test
    public void valueIndexesAreStrict() {
        Headers headers = Headers.of("a", "b", "c", "d");
        try {
            headers.value((-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
        Assert.assertEquals("b", headers.value(0));
        Assert.assertEquals("d", headers.value(1));
        try {
            headers.value(2);
            Assert.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test
    public void builderRejectsUnicodeInHeaderName() {
        try {
            new Headers.Builder().add("h?ader1", "value1");
            Assert.fail("Should have complained about invalid name");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Unexpected char 0xe9 at 1 in header name: h?ader1", expected.getMessage());
        }
    }

    @Test
    public void builderRejectsUnicodeInHeaderValue() {
        try {
            new Headers.Builder().add("header1", "valu?1");
            Assert.fail("Should have complained about invalid value");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Unexpected char 0xe9 at 4 in header1 value: valu?1", expected.getMessage());
        }
    }

    @Test
    public void varargFactoryRejectsUnicodeInHeaderName() {
        try {
            Headers.of("h?ader1", "value1");
            Assert.fail("Should have complained about invalid value");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Unexpected char 0xe9 at 1 in header name: h?ader1", expected.getMessage());
        }
    }

    @Test
    public void varargFactoryRejectsUnicodeInHeaderValue() {
        try {
            Headers.of("header1", "valu?1");
            Assert.fail("Should have complained about invalid value");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Unexpected char 0xe9 at 4 in header1 value: valu?1", expected.getMessage());
        }
    }

    @Test
    public void mapFactoryRejectsUnicodeInHeaderName() {
        try {
            Headers.of(Collections.singletonMap("h?ader1", "value1"));
            Assert.fail("Should have complained about invalid value");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Unexpected char 0xe9 at 1 in header name: h?ader1", expected.getMessage());
        }
    }

    @Test
    public void mapFactoryRejectsUnicodeInHeaderValue() {
        try {
            Headers.of(Collections.singletonMap("header1", "valu?1"));
            Assert.fail("Should have complained about invalid value");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Unexpected char 0xe9 at 4 in header1 value: valu?1", expected.getMessage());
        }
    }

    @Test
    public void headersEquals() {
        Headers headers1 = new Headers.Builder().add("Connection", "close").add("Transfer-Encoding", "chunked").build();
        Headers headers2 = new Headers.Builder().add("Connection", "close").add("Transfer-Encoding", "chunked").build();
        Assert.assertEquals(headers1, headers2);
        Assert.assertEquals(headers1.hashCode(), headers2.hashCode());
    }

    @Test
    public void headersNotEquals() {
        Headers headers1 = new Headers.Builder().add("Connection", "close").add("Transfer-Encoding", "chunked").build();
        Headers headers2 = new Headers.Builder().add("Connection", "keep-alive").add("Transfer-Encoding", "chunked").build();
        Assert.assertNotEquals(headers1, headers2);
        Assert.assertNotEquals(headers1.hashCode(), headers2.hashCode());
    }

    @Test
    public void headersToString() {
        Headers headers = new Headers.Builder().add("A", "a").add("B", "bb").build();
        Assert.assertEquals("A: a\nB: bb\n", headers.toString());
    }

    @Test
    public void headersAddAll() {
        Headers sourceHeaders = new Headers.Builder().add("A", "aa").add("a", "aa").add("B", "bb").build();
        Headers headers = new Headers.Builder().add("A", "a").addAll(sourceHeaders).add("C", "c").build();
        Assert.assertEquals("A: a\nA: aa\na: aa\nB: bb\nC: c\n", headers.toString());
    }

    /**
     * See https://github.com/square/okhttp/issues/2780.
     */
    @Test
    public void testDigestChallengeWithStrictRfc2617Header() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", ("Digest realm=\"myrealm\", nonce=\"fjalskdflwejrlaskdfjlaskdjflaks" + "jdflkasdf\", qop=\"auth\", stale=\"FALSE\"")).build();
        List<Challenge> challenges = HttpHeaders.parseChallenges(headers, "WWW-Authenticate");
        Assert.assertEquals(1, challenges.size());
        Assert.assertEquals("Digest", challenges.get(0).scheme());
        Assert.assertEquals("myrealm", challenges.get(0).realm());
        Map<String, String> expectedAuthParams = new LinkedHashMap<>();
        expectedAuthParams.put("realm", "myrealm");
        expectedAuthParams.put("nonce", "fjalskdflwejrlaskdfjlaskdjflaksjdflkasdf");
        expectedAuthParams.put("qop", "auth");
        expectedAuthParams.put("stale", "FALSE");
        Assert.assertEquals(expectedAuthParams, challenges.get(0).authParams());
    }

    @Test
    public void testDigestChallengeWithDifferentlyOrderedAuthParams() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", ("Digest qop=\"auth\", realm=\"myrealm\", nonce=\"fjalskdflwejrlask" + "dfjlaskdjflaksjdflkasdf\", stale=\"FALSE\"")).build();
        List<Challenge> challenges = HttpHeaders.parseChallenges(headers, "WWW-Authenticate");
        Assert.assertEquals(1, challenges.size());
        Assert.assertEquals("Digest", challenges.get(0).scheme());
        Assert.assertEquals("myrealm", challenges.get(0).realm());
        Map<String, String> expectedAuthParams = new LinkedHashMap<>();
        expectedAuthParams.put("realm", "myrealm");
        expectedAuthParams.put("nonce", "fjalskdflwejrlaskdfjlaskdjflaksjdflkasdf");
        expectedAuthParams.put("qop", "auth");
        expectedAuthParams.put("stale", "FALSE");
        Assert.assertEquals(expectedAuthParams, challenges.get(0).authParams());
    }

    @Test
    public void testDigestChallengeWithDifferentlyOrderedAuthParams2() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", ("Digest qop=\"auth\", nonce=\"fjalskdflwejrlaskdfjlaskdjflaksjdflk" + "asdf\", realm=\"myrealm\", stale=\"FALSE\"")).build();
        List<Challenge> challenges = HttpHeaders.parseChallenges(headers, "WWW-Authenticate");
        Assert.assertEquals(1, challenges.size());
        Assert.assertEquals("Digest", challenges.get(0).scheme());
        Assert.assertEquals("myrealm", challenges.get(0).realm());
        Map<String, String> expectedAuthParams = new LinkedHashMap<>();
        expectedAuthParams.put("realm", "myrealm");
        expectedAuthParams.put("nonce", "fjalskdflwejrlaskdfjlaskdjflaksjdflkasdf");
        expectedAuthParams.put("qop", "auth");
        expectedAuthParams.put("stale", "FALSE");
        Assert.assertEquals(expectedAuthParams, challenges.get(0).authParams());
    }

    @Test
    public void testDigestChallengeWithMissingRealm() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", ("Digest qop=\"auth\", underrealm=\"myrealm\", nonce=\"fjalskdflwej" + "rlaskdfjlaskdjflaksjdflkasdf\", stale=\"FALSE\"")).build();
        List<Challenge> challenges = HttpHeaders.parseChallenges(headers, "WWW-Authenticate");
        Assert.assertEquals(1, challenges.size());
        Assert.assertEquals("Digest", challenges.get(0).scheme());
        Assert.assertNull(challenges.get(0).realm());
        Map<String, String> expectedAuthParams = new LinkedHashMap<>();
        expectedAuthParams.put("underrealm", "myrealm");
        expectedAuthParams.put("nonce", "fjalskdflwejrlaskdfjlaskdjflaksjdflkasdf");
        expectedAuthParams.put("qop", "auth");
        expectedAuthParams.put("stale", "FALSE");
        Assert.assertEquals(expectedAuthParams, challenges.get(0).authParams());
    }

    @Test
    public void testDigestChallengeWithAdditionalSpaces() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", ("Digest qop=\"auth\",    realm=\"myrealm\", nonce=\"fjalskdflwejrl" + "askdfjlaskdjflaksjdflkasdf\", stale=\"FALSE\"")).build();
        List<Challenge> challenges = HttpHeaders.parseChallenges(headers, "WWW-Authenticate");
        Assert.assertEquals(1, challenges.size());
        Assert.assertEquals("Digest", challenges.get(0).scheme());
        Assert.assertEquals("myrealm", challenges.get(0).realm());
        Map<String, String> expectedAuthParams = new LinkedHashMap<>();
        expectedAuthParams.put("realm", "myrealm");
        expectedAuthParams.put("nonce", "fjalskdflwejrlaskdfjlaskdjflaksjdflkasdf");
        expectedAuthParams.put("qop", "auth");
        expectedAuthParams.put("stale", "FALSE");
        Assert.assertEquals(expectedAuthParams, challenges.get(0).authParams());
    }

    @Test
    public void testDigestChallengeWithAdditionalSpacesBeforeFirstAuthParam() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", ("Digest    realm=\"myrealm\", nonce=\"fjalskdflwejrlaskdfjlaskdjfl" + "aksjdflkasdf\", qop=\"auth\", stale=\"FALSE\"")).build();
        List<Challenge> challenges = HttpHeaders.parseChallenges(headers, "WWW-Authenticate");
        Assert.assertEquals(1, challenges.size());
        Assert.assertEquals("Digest", challenges.get(0).scheme());
        Assert.assertEquals("myrealm", challenges.get(0).realm());
        Map<String, String> expectedAuthParams = new LinkedHashMap<>();
        expectedAuthParams.put("realm", "myrealm");
        expectedAuthParams.put("nonce", "fjalskdflwejrlaskdfjlaskdjflaksjdflkasdf");
        expectedAuthParams.put("qop", "auth");
        expectedAuthParams.put("stale", "FALSE");
        Assert.assertEquals(expectedAuthParams, challenges.get(0).authParams());
    }

    @Test
    public void testDigestChallengeWithCamelCasedNames() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", ("DiGeSt qop=\"auth\", rEaLm=\"myrealm\", nonce=\"fjalskdflwejrlask" + "dfjlaskdjflaksjdflkasdf\", stale=\"FALSE\"")).build();
        List<Challenge> challenges = HttpHeaders.parseChallenges(headers, "WWW-Authenticate");
        Assert.assertEquals(1, challenges.size());
        Assert.assertEquals("DiGeSt", challenges.get(0).scheme());
        Assert.assertEquals("myrealm", challenges.get(0).realm());
        Map<String, String> expectedAuthParams = new LinkedHashMap<>();
        expectedAuthParams.put("realm", "myrealm");
        expectedAuthParams.put("nonce", "fjalskdflwejrlaskdfjlaskdjflaksjdflkasdf");
        expectedAuthParams.put("qop", "auth");
        expectedAuthParams.put("stale", "FALSE");
        Assert.assertEquals(expectedAuthParams, challenges.get(0).authParams());
    }

    @Test
    public void testDigestChallengeWithCamelCasedNames2() {
        // Strict RFC 2617 camelcased.
        Headers headers = new Headers.Builder().add("WWW-Authenticate", ("DIgEsT rEaLm=\"myrealm\", nonce=\"fjalskdflwejrlaskdfjlaskdjflaks" + "jdflkasdf\", qop=\"auth\", stale=\"FALSE\"")).build();
        List<Challenge> challenges = HttpHeaders.parseChallenges(headers, "WWW-Authenticate");
        Assert.assertEquals(1, challenges.size());
        Assert.assertEquals("DIgEsT", challenges.get(0).scheme());
        Assert.assertEquals("myrealm", challenges.get(0).realm());
        Map<String, String> expectedAuthParams = new LinkedHashMap<>();
        expectedAuthParams.put("realm", "myrealm");
        expectedAuthParams.put("nonce", "fjalskdflwejrlaskdfjlaskdjflaksjdflkasdf");
        expectedAuthParams.put("qop", "auth");
        expectedAuthParams.put("stale", "FALSE");
        Assert.assertEquals(expectedAuthParams, challenges.get(0).authParams());
    }

    @Test
    public void testDigestChallengeWithTokenFormOfAuthParam() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", "Digest realm=myrealm").build();
        List<Challenge> challenges = HttpHeaders.parseChallenges(headers, "WWW-Authenticate");
        Assert.assertEquals(1, challenges.size());
        Assert.assertEquals("Digest", challenges.get(0).scheme());
        Assert.assertEquals("myrealm", challenges.get(0).realm());
        Assert.assertEquals(Collections.singletonMap("realm", "myrealm"), challenges.get(0).authParams());
    }

    @Test
    public void testDigestChallengeWithoutAuthParams() {
        // Scheme only.
        Headers headers = new Headers.Builder().add("WWW-Authenticate", "Digest").build();
        List<Challenge> challenges = HttpHeaders.parseChallenges(headers, "WWW-Authenticate");
        Assert.assertEquals(1, challenges.size());
        Assert.assertEquals("Digest", challenges.get(0).scheme());
        Assert.assertNull(challenges.get(0).realm());
        Assert.assertEquals(Collections.emptyMap(), challenges.get(0).authParams());
    }

    @Test
    public void basicChallenge() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate: Basic realm=\"protected area\"").build();
        Assert.assertEquals(Collections.singletonList(new Challenge("Basic", Collections.singletonMap("realm", "protected area"))), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void basicChallengeWithCharset() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate: Basic realm=\"protected area\", charset=\"UTF-8\"").build();
        Map<String, String> expectedAuthParams = new LinkedHashMap<>();
        expectedAuthParams.put("realm", "protected area");
        expectedAuthParams.put("charset", "UTF-8");
        Assert.assertEquals(Collections.singletonList(new Challenge("Basic", expectedAuthParams)), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void basicChallengeWithUnexpectedCharset() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate: Basic realm=\"protected area\", charset=\"US-ASCII\"").build();
        Map<String, String> expectedAuthParams = new LinkedHashMap<>();
        expectedAuthParams.put("realm", "protected area");
        expectedAuthParams.put("charset", "US-ASCII");
        Assert.assertEquals(Collections.singletonList(new Challenge("Basic", expectedAuthParams)), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void separatorsBeforeFirstChallenge() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", " ,  , Basic realm=myrealm").build();
        Assert.assertEquals(Collections.singletonList(new Challenge("Basic", Collections.singletonMap("realm", "myrealm"))), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void spacesAroundKeyValueSeparator() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", "Basic realm = \"myrealm\"").build();
        Assert.assertEquals(Collections.singletonList(new Challenge("Basic", Collections.singletonMap("realm", "myrealm"))), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void multipleChallengesInOneHeader() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", "Basic realm = \"myrealm\",Digest").build();
        Assert.assertEquals(Arrays.asList(new Challenge("Basic", Collections.singletonMap("realm", "myrealm")), new Challenge("Digest", Collections.emptyMap())), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void multipleChallengesWithSameSchemeButDifferentRealmInOneHeader() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", "Basic realm = \"myrealm\",Basic realm=myotherrealm").build();
        Assert.assertEquals(Arrays.asList(new Challenge("Basic", Collections.singletonMap("realm", "myrealm")), new Challenge("Basic", Collections.singletonMap("realm", "myotherrealm"))), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void separatorsBeforeFirstAuthParam() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", "Digest, Basic ,,realm=\"myrealm\"").build();
        Assert.assertEquals(Arrays.asList(new Challenge("Digest", Collections.emptyMap()), new Challenge("Basic", Collections.singletonMap("realm", "myrealm"))), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void onlyCommaBetweenChallenges() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", "Digest,Basic realm=\"myrealm\"").build();
        Assert.assertEquals(Arrays.asList(new Challenge("Digest", Collections.emptyMap()), new Challenge("Basic", Collections.singletonMap("realm", "myrealm"))), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void multipleSeparatorsBetweenChallenges() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", "Digest,,,, Basic ,,realm=\"myrealm\"").build();
        Assert.assertEquals(Arrays.asList(new Challenge("Digest", Collections.emptyMap()), new Challenge("Basic", Collections.singletonMap("realm", "myrealm"))), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void unknownAuthParams() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", "Digest,,,, Basic ,,foo=bar,realm=\"myrealm\"").build();
        Map<String, String> expectedAuthParams = new LinkedHashMap<>();
        expectedAuthParams.put("realm", "myrealm");
        expectedAuthParams.put("foo", "bar");
        Assert.assertEquals(Arrays.asList(new Challenge("Digest", Collections.emptyMap()), new Challenge("Basic", expectedAuthParams)), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void escapedCharactersInQuotedString() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", "Digest,,,, Basic ,,,realm=\"my\\\\\\\"r\\ealm\"").build();
        Assert.assertEquals(Arrays.asList(new Challenge("Digest", Collections.emptyMap()), new Challenge("Basic", Collections.singletonMap("realm", "my\\\"realm"))), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void commaInQuotedStringAndBeforeFirstChallenge() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", ",Digest,,,, Basic ,,,realm=\"my, realm,\"").build();
        Assert.assertEquals(Arrays.asList(new Challenge("Digest", Collections.emptyMap()), new Challenge("Basic", Collections.singletonMap("realm", "my, realm,"))), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void unescapedDoubleQuoteInQuotedStringWithEvenNumberOfBackslashesInFront() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", "Digest,,,, Basic ,,,realm=\"my\\\\\\\\\"r\\ealm\"").build();
        Assert.assertEquals(Arrays.asList(new Challenge("Digest", Collections.emptyMap())), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void unescapedDoubleQuoteInQuotedString() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", "Digest,,,, Basic ,,,realm=\"my\"realm\"").build();
        Assert.assertEquals(Arrays.asList(new Challenge("Digest", Collections.emptyMap())), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void token68InsteadOfAuthParams() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", "Other abc==").build();
        Assert.assertEquals(Collections.singletonList(new Challenge("Other", Collections.singletonMap(null, "abc=="))), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void token68AndAuthParams() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", "Other abc==, realm=myrealm").build();
        Assert.assertEquals(Arrays.asList(new Challenge("Other", Collections.singletonMap(null, "abc=="))), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void repeatedAuthParamKey() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", "Other realm=myotherrealm, realm=myrealm").build();
        Assert.assertEquals(Collections.emptyList(), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void multipleAuthenticateHeaders() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", "Digest").add("WWW-Authenticate", "Basic realm=myrealm").build();
        Assert.assertEquals(Arrays.asList(new Challenge("Digest", Collections.emptyMap()), new Challenge("Basic", Collections.singletonMap("realm", "myrealm"))), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void multipleAuthenticateHeadersInDifferentOrder() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", "Basic realm=myrealm").add("WWW-Authenticate", "Digest").build();
        Assert.assertEquals(Arrays.asList(new Challenge("Basic", Collections.singletonMap("realm", "myrealm")), new Challenge("Digest", Collections.emptyMap())), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void multipleBasicAuthenticateHeaders() {
        Headers headers = new Headers.Builder().add("WWW-Authenticate", "Basic realm=myrealm").add("WWW-Authenticate", "Basic realm=myotherrealm").build();
        Assert.assertEquals(Arrays.asList(new Challenge("Basic", Collections.singletonMap("realm", "myrealm")), new Challenge("Basic", Collections.singletonMap("realm", "myotherrealm"))), HttpHeaders.parseChallenges(headers, "WWW-Authenticate"));
    }

    @Test
    public void byteCount() {
        Assert.assertEquals(0L, EMPTY_HEADERS.byteCount());
        Assert.assertEquals(10L, new Headers.Builder().add("abc", "def").build().byteCount());
        Assert.assertEquals(20L, new Headers.Builder().add("abc", "def").add("ghi", "jkl").build().byteCount());
    }

    @Test
    public void addDate() {
        Date expected = new Date(0L);
        Headers headers = new Headers.Builder().add("testDate", expected).build();
        Assert.assertEquals("Thu, 01 Jan 1970 00:00:00 GMT", headers.get("testDate"));
        Assert.assertEquals(new Date(0L), headers.getDate("testDate"));
    }

    @Test
    public void addDateNull() {
        try {
            new Headers.Builder().add("testDate", ((Date) (null))).build();
            Assert.fail();
        } catch (NullPointerException expected) {
            Assert.assertEquals("value for name testDate == null", expected.getMessage());
        }
    }

    @Test
    public void addInstant() {
        Instant expected = Instant.ofEpochMilli(0L);
        Headers headers = new Headers.Builder().add("Test-Instant", expected).build();
        Assert.assertEquals("Thu, 01 Jan 1970 00:00:00 GMT", headers.get("Test-Instant"));
        Assert.assertEquals(expected, headers.getInstant("Test-Instant"));
    }

    @Test
    public void addInstantNull() {
        try {
            new Headers.Builder().add("Test-Instant", ((Instant) (null))).build();
            Assert.fail();
        } catch (NullPointerException expected) {
            Assert.assertEquals("value for name Test-Instant == null", expected.getMessage());
        }
    }

    @Test
    public void setDate() {
        Date expected = new Date(1000);
        Headers headers = new Headers.Builder().add("testDate", new Date(0L)).set("testDate", expected).build();
        Assert.assertEquals("Thu, 01 Jan 1970 00:00:01 GMT", headers.get("testDate"));
        Assert.assertEquals(expected, headers.getDate("testDate"));
    }

    @Test
    public void setDateNull() {
        try {
            new Headers.Builder().set("testDate", ((Date) (null))).build();
            Assert.fail();
        } catch (NullPointerException expected) {
            Assert.assertEquals("value for name testDate == null", expected.getMessage());
        }
    }

    @Test
    public void setInstant() {
        Instant expected = Instant.ofEpochMilli(1000L);
        Headers headers = new Headers.Builder().add("Test-Instant", Instant.ofEpochMilli(0L)).set("Test-Instant", expected).build();
        Assert.assertEquals("Thu, 01 Jan 1970 00:00:01 GMT", headers.get("Test-Instant"));
        Assert.assertEquals(expected, headers.getInstant("Test-Instant"));
    }

    @Test
    public void setInstantNull() {
        try {
            new Headers.Builder().set("Test-Instant", ((Instant) (null))).build();
            Assert.fail();
        } catch (NullPointerException expected) {
            Assert.assertEquals("value for name Test-Instant == null", expected.getMessage());
        }
    }
}

