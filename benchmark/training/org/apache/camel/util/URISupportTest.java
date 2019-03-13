/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.util;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class URISupportTest {
    @Test
    public void testNormalizeEndpointUri() throws Exception {
        String out1 = URISupport.normalizeUri("smtp://localhost?username=davsclaus&password=secret");
        String out2 = URISupport.normalizeUri("smtp://localhost?password=secret&username=davsclaus");
        Assert.assertEquals(out1, out2);
        out1 = URISupport.normalizeUri("smtp://localhost?username=davsclaus&password=secret");
        out2 = URISupport.normalizeUri("smtp:localhost?password=secret&username=davsclaus");
        Assert.assertEquals(out1, out2);
        out1 = URISupport.normalizeUri("smtp:localhost?password=secret&username=davsclaus");
        out2 = URISupport.normalizeUri("smtp://localhost?username=davsclaus&password=secret");
        Assert.assertEquals(out1, out2);
        out1 = URISupport.normalizeUri("seda:foo?concurrentConsumer=2");
        out2 = URISupport.normalizeUri("seda:foo?concurrentConsumer=2");
        Assert.assertEquals(out1, out2);
        out1 = URISupport.normalizeUri("seda:foo?concurrentConsumer=2");
        out2 = URISupport.normalizeUri("seda:foo");
        Assert.assertNotSame(out1, out2);
        out1 = URISupport.normalizeUri("foo:?test=1");
        out2 = URISupport.normalizeUri("foo://?test=1");
        Assert.assertEquals("foo://?test=1", out2);
        Assert.assertEquals(out1, out2);
    }

    @Test
    public void testNormalizeEndpointUriNoParam() throws Exception {
        String out1 = URISupport.normalizeUri("direct:foo");
        String out2 = URISupport.normalizeUri("direct:foo");
        Assert.assertEquals(out1, out2);
        out1 = URISupport.normalizeUri("direct://foo");
        out2 = URISupport.normalizeUri("direct://foo");
        Assert.assertEquals(out1, out2);
        out1 = URISupport.normalizeUri("direct:foo");
        out2 = URISupport.normalizeUri("direct://foo");
        Assert.assertEquals(out1, out2);
        out1 = URISupport.normalizeUri("direct://foo");
        out2 = URISupport.normalizeUri("direct:foo");
        Assert.assertEquals(out1, out2);
        out1 = URISupport.normalizeUri("direct://foo");
        out2 = URISupport.normalizeUri("direct:bar");
        Assert.assertNotSame(out1, out2);
    }

    @Test
    public void testNormalizeEndpointUriWithFragments() throws Exception {
        String out1 = URISupport.normalizeUri("irc://someserver/#camel?user=davsclaus");
        String out2 = URISupport.normalizeUri("irc:someserver/#camel?user=davsclaus");
        Assert.assertEquals(out1, out2);
        out1 = URISupport.normalizeUri("irc://someserver/#camel?user=davsclaus");
        out2 = URISupport.normalizeUri("irc:someserver/#camel?user=hadrian");
        Assert.assertNotSame(out1, out2);
    }

    @Test
    public void testNormalizeHttpEndpoint() throws Exception {
        String out1 = URISupport.normalizeUri("http://www.google.com?q=Camel");
        String out2 = URISupport.normalizeUri("http:www.google.com?q=Camel");
        Assert.assertEquals(out1, out2);
        Assert.assertTrue("Should have //", out1.startsWith("http://"));
        Assert.assertTrue("Should have //", out2.startsWith("http://"));
    }

    @Test
    public void testNormalizeIPv6HttpEndpoint() throws Exception {
        String result = URISupport.normalizeUri("http://[2a00:8a00:6000:40::1413]:30300/test");
        Assert.assertEquals("http://[2a00:8a00:6000:40::1413]:30300/test", result);
    }

    @Test
    public void testNormalizeHttpEndpointUnicodedParameter() throws Exception {
        String out = URISupport.normalizeUri("http://www.google.com?q=S\u00f8ren");
        Assert.assertEquals("http://www.google.com?q=S%C3%B8ren", out);
    }

    @Test
    public void testParseParametersUnicodedValue() throws Exception {
        String out = URISupport.normalizeUri("http://www.google.com?q=S\u00f8ren");
        URI uri = new URI(out);
        Map<String, Object> parameters = URISupport.parseParameters(uri);
        Assert.assertEquals(1, parameters.size());
        Assert.assertEquals("S\u00f8ren", parameters.get("q"));
    }

    @Test
    public void testNormalizeHttpEndpointURLEncodedParameter() throws Exception {
        String out = URISupport.normalizeUri("http://www.google.com?q=S%C3%B8ren%20Hansen");
        Assert.assertEquals("http://www.google.com?q=S%C3%B8ren+Hansen", out);
    }

    @Test
    public void testParseParametersURLEncodeddValue() throws Exception {
        String out = URISupport.normalizeUri("http://www.google.com?q=S%C3%B8ren+Hansen");
        URI uri = new URI(out);
        Map<String, Object> parameters = URISupport.parseParameters(uri);
        Assert.assertEquals(1, parameters.size());
        Assert.assertEquals("S\u00f8ren Hansen", parameters.get("q"));
    }

    @Test
    public void testNormalizeUriWhereParamererIsFaulty() throws Exception {
        String out = URISupport.normalizeUri("stream:uri?file:///d:/temp/data/log/quickfix.log&scanStream=true");
        Assert.assertNotNull(out);
    }

    @Test
    public void testCreateRemaingURI() throws Exception {
        URI original = new URI("http://camel.apache.org");
        Map<String, Object> param = new HashMap<>();
        param.put("foo", "123");
        URI newUri = URISupport.createRemainingURI(original, param);
        Assert.assertNotNull(newUri);
        String s = newUri.toString();
        Assert.assertEquals("http://camel.apache.org?foo=123", s);
    }

    @Test
    public void testCreateURIWithQueryHasOneFragment() throws Exception {
        URI uri = new URI("smtp://localhost#fragmentOne");
        URI resultUri = URISupport.createURIWithQuery(uri, null);
        Assert.assertNotNull(resultUri);
        Assert.assertEquals("smtp://localhost#fragmentOne", resultUri.toString());
    }

    @Test
    public void testCreateURIWithQueryHasOneFragmentAndQueryParameter() throws Exception {
        URI uri = new URI("smtp://localhost#fragmentOne");
        URI resultUri = URISupport.createURIWithQuery(uri, "utm_campaign=launch");
        Assert.assertNotNull(resultUri);
        Assert.assertEquals("smtp://localhost?utm_campaign=launch#fragmentOne", resultUri.toString());
    }

    @Test
    public void testNormalizeEndpointWithEqualSignInParameter() throws Exception {
        String out = URISupport.normalizeUri("jms:queue:foo?selector=somekey='somevalue'&foo=bar");
        Assert.assertNotNull(out);
        // Camel will safe encode the URI
        Assert.assertEquals("jms://queue:foo?foo=bar&selector=somekey%3D%27somevalue%27", out);
    }

    @Test
    public void testNormalizeEndpointWithPercentSignInParameter() throws Exception {
        String out = URISupport.normalizeUri("http://someendpoint?username=james&password=%25test");
        Assert.assertNotNull(out);
        // Camel will safe encode the URI
        Assert.assertEquals("http://someendpoint?password=%25test&username=james", out);
    }

    @Test
    public void testParseParameters() throws Exception {
        URI u = new URI("quartz:myGroup/myTimerName?cron=0+0+*+*+*+?");
        Map<String, Object> params = URISupport.parseParameters(u);
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("0 0 * * * ?", params.get("cron"));
        u = new URI("quartz:myGroup/myTimerName?cron=0+0+*+*+*+?&bar=123");
        params = URISupport.parseParameters(u);
        Assert.assertEquals(2, params.size());
        Assert.assertEquals("0 0 * * * ?", params.get("cron"));
        Assert.assertEquals("123", params.get("bar"));
    }

    @Test
    public void testCreateRemainingURIEncoding() throws Exception {
        // the uri is already encoded, but we create a new one with new query parameters
        String uri = "http://localhost:23271/myapp/mytest?columns=name%2Ctotalsens%2Cupsens&username=apiuser";
        // these are the parameters which is tricky to encode
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("foo", "abc def");
        map.put("bar", "123,456");
        map.put("name", "S\u00f8ren");// danish letter

        // create new uri with the parameters
        URI out = URISupport.createRemainingURI(new URI(uri), map);
        Assert.assertNotNull(out);
        Assert.assertEquals("http://localhost:23271/myapp/mytest?foo=abc+def&bar=123%2C456&name=S%C3%B8ren", out.toString());
        Assert.assertEquals("http://localhost:23271/myapp/mytest?foo=abc+def&bar=123%2C456&name=S%C3%B8ren", out.toASCIIString());
    }

    @Test
    public void testNormalizeEndpointUriWithDualParameters() throws Exception {
        String out1 = URISupport.normalizeUri("smtp://localhost?to=foo&to=bar&from=me");
        Assert.assertEquals("smtp://localhost?from=me&to=foo&to=bar", out1);
        String out2 = URISupport.normalizeUri("smtp://localhost?to=foo&to=bar&from=me&from=you");
        Assert.assertEquals("smtp://localhost?from=me&from=you&to=foo&to=bar", out2);
    }

    @Test
    public void testSanitizeUriWithUserInfo() {
        String uri = "jt400://GEORGE:HARRISON@LIVERPOOL/QSYS.LIB/BEATLES.LIB/PENNYLANE.DTAQ";
        String expected = "jt400://GEORGE:xxxxxx@LIVERPOOL/QSYS.LIB/BEATLES.LIB/PENNYLANE.DTAQ";
        Assert.assertEquals(expected, URISupport.sanitizeUri(uri));
    }

    @Test
    public void testSanitizeUriWithUserInfoAndColonPassword() {
        String uri = "sftp://USERNAME:HARRISON:COLON@sftp.server.test";
        String expected = "sftp://USERNAME:xxxxxx@sftp.server.test";
        Assert.assertEquals(expected, URISupport.sanitizeUri(uri));
    }

    @Test
    public void testSanitizePathWithUserInfo() {
        String path = "GEORGE:HARRISON@LIVERPOOL/QSYS.LIB/BEATLES.LIB/PENNYLANE.PGM";
        String expected = "GEORGE:xxxxxx@LIVERPOOL/QSYS.LIB/BEATLES.LIB/PENNYLANE.PGM";
        Assert.assertEquals(expected, URISupport.sanitizePath(path));
    }

    @Test
    public void testSanitizePathWithUserInfoAndColonPassword() {
        String path = "USERNAME:HARRISON:COLON@sftp.server.test";
        String expected = "USERNAME:xxxxxx@sftp.server.test";
        Assert.assertEquals(expected, URISupport.sanitizePath(path));
    }

    @Test
    public void testSanitizePathWithoutSensitiveInfoIsUnchanged() {
        String path = "myhost:8080/mypath";
        Assert.assertEquals(path, URISupport.sanitizePath(path));
    }

    @Test
    public void testSanitizeUriWithRawPassword() {
        String uri1 = "http://foo?username=me&password=RAW(me#@123)&foo=bar";
        String uri2 = "http://foo?username=me&password=RAW{me#@123}&foo=bar";
        String expected = "http://foo?username=me&password=xxxxxx&foo=bar";
        Assert.assertEquals(expected, URISupport.sanitizeUri(uri1));
        Assert.assertEquals(expected, URISupport.sanitizeUri(uri2));
    }

    @Test
    public void testSanitizeUriRawUnsafePassword() {
        String uri1 = "sftp://localhost/target?password=RAW(beforeAmp&afterAmp)&username=jrandom";
        String uri2 = "sftp://localhost/target?password=RAW{beforeAmp&afterAmp}&username=jrandom";
        String expected = "sftp://localhost/target?password=xxxxxx&username=jrandom";
        Assert.assertEquals(expected, URISupport.sanitizeUri(uri1));
        Assert.assertEquals(expected, URISupport.sanitizeUri(uri2));
    }

    @Test
    public void testNormalizeEndpointUriWithUserInfoSpecialSign() throws Exception {
        String out1 = URISupport.normalizeUri("ftp://us%40r:t%st@localhost:21000/tmp3/camel?foo=us@r");
        Assert.assertEquals("ftp://us%40r:t%25st@localhost:21000/tmp3/camel?foo=us%40r", out1);
        String out2 = URISupport.normalizeUri("ftp://us%40r:t%25st@localhost:21000/tmp3/camel?foo=us@r");
        Assert.assertEquals("ftp://us%40r:t%25st@localhost:21000/tmp3/camel?foo=us%40r", out2);
        String out3 = URISupport.normalizeUri("ftp://us@r:t%st@localhost:21000/tmp3/camel?foo=us@r");
        Assert.assertEquals("ftp://us%40r:t%25st@localhost:21000/tmp3/camel?foo=us%40r", out3);
        String out4 = URISupport.normalizeUri("ftp://us@r:t%25st@localhost:21000/tmp3/camel?foo=us@r");
        Assert.assertEquals("ftp://us%40r:t%25st@localhost:21000/tmp3/camel?foo=us%40r", out4);
    }

    @Test
    public void testSpecialUriFromXmppComponent() throws Exception {
        String out1 = URISupport.normalizeUri("xmpp://camel-user@localhost:123/test-user@localhost?password=secret&serviceName=someCoolChat");
        Assert.assertEquals("xmpp://camel-user@localhost:123/test-user@localhost?password=secret&serviceName=someCoolChat", out1);
    }

    @Test
    public void testRawParameter() throws Exception {
        String out = URISupport.normalizeUri("xmpp://camel-user@localhost:123/test-user@localhost?password=RAW(++?w0rd)&serviceName=some chat");
        Assert.assertEquals("xmpp://camel-user@localhost:123/test-user@localhost?password=RAW(++?w0rd)&serviceName=some+chat", out);
        String out2 = URISupport.normalizeUri("xmpp://camel-user@localhost:123/test-user@localhost?password=RAW(foo %% bar)&serviceName=some chat");
        // Just make sure the RAW parameter can be resolved rightly, we need to replace the % into %25
        Assert.assertEquals("xmpp://camel-user@localhost:123/test-user@localhost?password=RAW(foo %25%25 bar)&serviceName=some+chat", out2);
    }

    @Test
    public void testRawParameterCurly() throws Exception {
        String out = URISupport.normalizeUri("xmpp://camel-user@localhost:123/test-user@localhost?password=RAW{++?w0rd}&serviceName=some chat");
        Assert.assertEquals("xmpp://camel-user@localhost:123/test-user@localhost?password=RAW{++?w0rd}&serviceName=some+chat", out);
        String out2 = URISupport.normalizeUri("xmpp://camel-user@localhost:123/test-user@localhost?password=RAW{foo %% bar}&serviceName=some chat");
        // Just make sure the RAW parameter can be resolved rightly, we need to replace the % into %25
        Assert.assertEquals("xmpp://camel-user@localhost:123/test-user@localhost?password=RAW{foo %25%25 bar}&serviceName=some+chat", out2);
    }

    @Test
    public void testParseQuery() throws Exception {
        Map<String, Object> map = URISupport.parseQuery("password=secret&serviceName=somechat");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("secret", map.get("password"));
        Assert.assertEquals("somechat", map.get("serviceName"));
        map = URISupport.parseQuery("password=RAW(++?w0rd)&serviceName=somechat");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("RAW(++?w0rd)", map.get("password"));
        Assert.assertEquals("somechat", map.get("serviceName"));
        map = URISupport.parseQuery("password=RAW(++?)w&rd)&serviceName=somechat");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("RAW(++?)w&rd)", map.get("password"));
        Assert.assertEquals("somechat", map.get("serviceName"));
        map = URISupport.parseQuery("password=RAW(%2520w&rd)&serviceName=somechat");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("RAW(%2520w&rd)", map.get("password"));
        Assert.assertEquals("somechat", map.get("serviceName"));
    }

    @Test
    public void testParseQueryCurly() throws Exception {
        Map<String, Object> map = URISupport.parseQuery("password=RAW{++?w0rd}&serviceName=somechat");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("RAW{++?w0rd}", map.get("password"));
        Assert.assertEquals("somechat", map.get("serviceName"));
        map = URISupport.parseQuery("password=RAW{++?)w&rd}&serviceName=somechat");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("RAW{++?)w&rd}", map.get("password"));
        Assert.assertEquals("somechat", map.get("serviceName"));
        map = URISupport.parseQuery("password=RAW{%2520w&rd}&serviceName=somechat");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("RAW{%2520w&rd}", map.get("password"));
        Assert.assertEquals("somechat", map.get("serviceName"));
    }

    @Test
    public void testParseQueryLenient() throws Exception {
        try {
            URISupport.parseQuery("password=secret&serviceName=somechat&", false, false);
            Assert.fail("Should have thrown exception");
        } catch (URISyntaxException e) {
            // expected
        }
        Map<String, Object> map = URISupport.parseQuery("password=secret&serviceName=somechat&", false, true);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("secret", map.get("password"));
        Assert.assertEquals("somechat", map.get("serviceName"));
    }

    @Test
    public void testScanRaw() {
        List<Pair<Integer>> pairs1 = URISupport.scanRaw("password=RAW(++?5w0rd)&serviceName=somechat");
        Assert.assertEquals(1, pairs1.size());
        Assert.assertEquals(new Pair(9, 21), pairs1.get(0));
        List<Pair<Integer>> pairs2 = URISupport.scanRaw("password=RAW{++?5w0rd}&serviceName=somechat");
        Assert.assertEquals(1, pairs2.size());
        Assert.assertEquals(new Pair(9, 21), pairs2.get(0));
        List<Pair<Integer>> pairs3 = URISupport.scanRaw("password=RAW{++?)&0rd}&serviceName=somechat");
        Assert.assertEquals(1, pairs3.size());
        Assert.assertEquals(new Pair(9, 21), pairs3.get(0));
        List<Pair<Integer>> pairs4 = URISupport.scanRaw("password1=RAW(++?}&0rd)&password2=RAW{++?)&0rd}&serviceName=somechat");
        Assert.assertEquals(2, pairs4.size());
        Assert.assertEquals(new Pair(10, 22), pairs4.get(0));
        Assert.assertEquals(new Pair(34, 46), pairs4.get(1));
    }

    @Test
    public void testIsRaw() {
        List<Pair<Integer>> pairs = Arrays.asList(new Pair(3, 5), new Pair(8, 10));
        for (int i = 0; i < 3; i++) {
            Assert.assertFalse(URISupport.isRaw(i, pairs));
        }
        for (int i = 3; i < 6; i++) {
            Assert.assertTrue(URISupport.isRaw(i, pairs));
        }
        for (int i = 6; i < 8; i++) {
            Assert.assertFalse(URISupport.isRaw(i, pairs));
        }
        for (int i = 8; i < 11; i++) {
            Assert.assertTrue(URISupport.isRaw(i, pairs));
        }
        for (int i = 11; i < 15; i++) {
            Assert.assertFalse(URISupport.isRaw(i, pairs));
        }
    }

    @Test
    public void testResolveRawParameterValues() throws Exception {
        Map<String, Object> map = URISupport.parseQuery("password=secret&serviceName=somechat");
        URISupport.resolveRawParameterValues(map);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("secret", map.get("password"));
        Assert.assertEquals("somechat", map.get("serviceName"));
        map = URISupport.parseQuery("password=RAW(++?w0rd)&serviceName=somechat");
        URISupport.resolveRawParameterValues(map);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("++?w0rd", map.get("password"));
        Assert.assertEquals("somechat", map.get("serviceName"));
        map = URISupport.parseQuery("password=RAW(++?)w&rd)&serviceName=somechat");
        URISupport.resolveRawParameterValues(map);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("++?)w&rd", map.get("password"));
        Assert.assertEquals("somechat", map.get("serviceName"));
    }

    @Test
    public void testResolveRawParameterValuesCurly() throws Exception {
        Map<String, Object> map = URISupport.parseQuery("password=RAW{++?w0rd}&serviceName=somechat");
        URISupport.resolveRawParameterValues(map);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("++?w0rd", map.get("password"));
        Assert.assertEquals("somechat", map.get("serviceName"));
        map = URISupport.parseQuery("password=RAW{++?)w&rd}&serviceName=somechat");
        URISupport.resolveRawParameterValues(map);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("++?)w&rd", map.get("password"));
        Assert.assertEquals("somechat", map.get("serviceName"));
    }

    @Test
    public void testAppendParameterToUriAndReplaceExistingOne() throws Exception {
        Map<String, Object> newParameters = new HashMap<>();
        newParameters.put("foo", "456");
        newParameters.put("bar", "yes");
        String newUri = URISupport.appendParametersToURI("stub:foo?foo=123", newParameters);
        Assert.assertEquals("stub://foo?foo=456&bar=yes", newUri);
    }

    @Test
    public void testPathAndQueryOf() {
        Assert.assertEquals("/", URISupport.pathAndQueryOf(URI.create("http://localhost")));
        Assert.assertEquals("/", URISupport.pathAndQueryOf(URI.create("http://localhost:80")));
        Assert.assertEquals("/", URISupport.pathAndQueryOf(URI.create("http://localhost:80/")));
        Assert.assertEquals("/path", URISupport.pathAndQueryOf(URI.create("http://localhost:80/path")));
        Assert.assertEquals("/path/", URISupport.pathAndQueryOf(URI.create("http://localhost:80/path/")));
        Assert.assertEquals("/path?query=value", URISupport.pathAndQueryOf(URI.create("http://localhost:80/path?query=value")));
    }

    @Test
    public void shouldStripPrefixes() {
        assertThat(URISupport.stripPrefix(null, null)).isNull();
        assertThat(URISupport.stripPrefix("", null)).isEmpty();
        assertThat(URISupport.stripPrefix(null, "")).isNull();
        assertThat(URISupport.stripPrefix("", "")).isEmpty();
        assertThat(URISupport.stripPrefix("a", "b")).isEqualTo("a");
        assertThat(URISupport.stripPrefix("a", "a")).isEmpty();
        assertThat(URISupport.stripPrefix("ab", "b")).isEqualTo("ab");
        assertThat(URISupport.stripPrefix("a", "ab")).isEqualTo("a");
    }

    @Test
    public void shouldStripSuffixes() {
        assertThat(URISupport.stripSuffix(null, null)).isNull();
        assertThat(URISupport.stripSuffix("", null)).isEmpty();
        assertThat(URISupport.stripSuffix(null, "")).isNull();
        assertThat(URISupport.stripSuffix("", "")).isEmpty();
        assertThat(URISupport.stripSuffix("a", "b")).isEqualTo("a");
        assertThat(URISupport.stripSuffix("a", "a")).isEmpty();
        assertThat(URISupport.stripSuffix("ab", "b")).isEqualTo("a");
        assertThat(URISupport.stripSuffix("a", "ab")).isEqualTo("a");
    }

    @Test
    public void shouldJoinPaths() {
        assertThat(URISupport.joinPaths(null, null)).isEmpty();
        assertThat(URISupport.joinPaths("", null)).isEmpty();
        assertThat(URISupport.joinPaths(null, "")).isEmpty();
        assertThat(URISupport.joinPaths("", "")).isEmpty();
        assertThat(URISupport.joinPaths("a", "")).isEqualTo("a");
        assertThat(URISupport.joinPaths("a", "b")).isEqualTo("a/b");
        assertThat(URISupport.joinPaths("/a", "b")).isEqualTo("/a/b");
        assertThat(URISupport.joinPaths("/a", "b/")).isEqualTo("/a/b/");
        assertThat(URISupport.joinPaths("/a/", "b/")).isEqualTo("/a/b/");
        assertThat(URISupport.joinPaths("/a/", "/b/")).isEqualTo("/a/b/");
        assertThat(URISupport.joinPaths("a", "b", "c")).isEqualTo("a/b/c");
        assertThat(URISupport.joinPaths("a", null, "c")).isEqualTo("a/c");
        assertThat(URISupport.joinPaths("/a/", "/b", "c/", "/d/")).isEqualTo("/a/b/c/d/");
        assertThat(URISupport.joinPaths("/a/", "/b", "c/", null)).isEqualTo("/a/b/c/");
        assertThat(URISupport.joinPaths("/a/", null, null, null)).isEqualTo("/a/");
        assertThat(URISupport.joinPaths("a/", "/b", null, null)).isEqualTo("a/b");
    }
}

