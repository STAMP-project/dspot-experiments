/**
 * Copyright (C) 2010 The Android Open Source Project
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
/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.net;


import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;
import com.google.mockwebserver.RecordedRequest;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.RandomAccess;
import java.util.TreeMap;
import junit.framework.TestCase;


public abstract class AbstractCookiesTest extends TestCase {
    private static final Map<String, List<String>> EMPTY_COOKIES_MAP = Collections.emptyMap();

    private CookieHandler defaultHandler;

    private CookieManager cookieManager;

    private CookieStore cookieStore;

    public void testNetscapeResponse() throws Exception {
        CookieManager cookieManager = new CookieManager(createCookieStore(), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);
        MockWebServer server = new MockWebServer();
        server.play();
        server.enqueue(new MockResponse().addHeader((((("Set-Cookie: a=android; " + (("expires=Fri, 31-Dec-9999 23:59:59 GMT; " + "path=/path; ") + "domain=")) + (server.getCookieDomain())) + "; ") + "secure")));
        get(server, "/path/foo");
        List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
        TestCase.assertEquals(1, cookies.size());
        HttpCookie cookie = cookies.get(0);
        TestCase.assertEquals("a", cookie.getName());
        TestCase.assertEquals("android", cookie.getValue());
        TestCase.assertEquals(null, cookie.getComment());
        TestCase.assertEquals(null, cookie.getCommentURL());
        TestCase.assertEquals(false, cookie.getDiscard());
        TestCase.assertEquals(server.getCookieDomain(), cookie.getDomain());
        TestCase.assertTrue(((cookie.getMaxAge()) > 100000000000L));
        TestCase.assertEquals("/path", cookie.getPath());
        TestCase.assertEquals(true, cookie.getSecure());
        TestCase.assertEquals(0, cookie.getVersion());
    }

    public void testRfc2109Response() throws Exception {
        CookieManager cookieManager = new CookieManager(createCookieStore(), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);
        MockWebServer server = new MockWebServer();
        server.play();
        server.enqueue(new MockResponse().addHeader(((((((("Set-Cookie: a=android; " + ("Comment=this cookie is delicious; " + "Domain=")) + (server.getCookieDomain())) + "; ") + "Max-Age=60; ") + "Path=/path; ") + "Secure; ") + "Version=1")));
        get(server, "/path/foo");
        List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
        TestCase.assertEquals(1, cookies.size());
        HttpCookie cookie = cookies.get(0);
        TestCase.assertEquals("a", cookie.getName());
        TestCase.assertEquals("android", cookie.getValue());
        TestCase.assertEquals("this cookie is delicious", cookie.getComment());
        TestCase.assertEquals(null, cookie.getCommentURL());
        TestCase.assertEquals(false, cookie.getDiscard());
        TestCase.assertEquals(server.getCookieDomain(), cookie.getDomain());
        TestCase.assertEquals(60, cookie.getMaxAge());
        TestCase.assertEquals("/path", cookie.getPath());
        TestCase.assertEquals(true, cookie.getSecure());
        TestCase.assertEquals(1, cookie.getVersion());
    }

    public void testRfc2965Response() throws Exception {
        CookieManager cookieManager = new CookieManager(createCookieStore(), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);
        MockWebServer server = new MockWebServer();
        server.play();
        server.enqueue(new MockResponse().addHeader((((((((((("Set-Cookie2: a=android; " + ((("Comment=this cookie is delicious; " + "CommentURL=http://google.com/; ") + "Discard; ") + "Domain=")) + (server.getCookieDomain())) + "; ") + "Max-Age=60; ") + "Path=/path; ") + "Port=\"80,443,") + (server.getPort())) + "\"; ") + "Secure; ") + "Version=1")));
        get(server, "/path/foo");
        List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
        TestCase.assertEquals(1, cookies.size());
        HttpCookie cookie = cookies.get(0);
        TestCase.assertEquals("a", cookie.getName());
        TestCase.assertEquals("android", cookie.getValue());
        TestCase.assertEquals("this cookie is delicious", cookie.getComment());
        TestCase.assertEquals("http://google.com/", cookie.getCommentURL());
        TestCase.assertEquals(true, cookie.getDiscard());
        TestCase.assertEquals(server.getCookieDomain(), cookie.getDomain());
        TestCase.assertEquals(60, cookie.getMaxAge());
        TestCase.assertEquals("/path", cookie.getPath());
        TestCase.assertEquals(("80,443," + (server.getPort())), cookie.getPortlist());
        TestCase.assertEquals(true, cookie.getSecure());
        TestCase.assertEquals(1, cookie.getVersion());
    }

    public void testQuotedAttributeValues() throws Exception {
        CookieManager cookieManager = new CookieManager(createCookieStore(), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);
        MockWebServer server = new MockWebServer();
        server.play();
        server.enqueue(new MockResponse().addHeader((((((((((("Set-Cookie2: a=\"android\"; " + ((("Comment=\"this cookie is delicious\"; " + "CommentURL=\"http://google.com/\"; ") + "Discard; ") + "Domain=\"")) + (server.getCookieDomain())) + "\"; ") + "Max-Age=\"60\"; ") + "Path=\"/path\"; ") + "Port=\"80,443,") + (server.getPort())) + "\"; ") + "Secure; ") + "Version=\"1\"")));
        get(server, "/path/foo");
        List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
        TestCase.assertEquals(1, cookies.size());
        HttpCookie cookie = cookies.get(0);
        TestCase.assertEquals("a", cookie.getName());
        TestCase.assertEquals("android", cookie.getValue());
        TestCase.assertEquals("this cookie is delicious", cookie.getComment());
        TestCase.assertEquals("http://google.com/", cookie.getCommentURL());
        TestCase.assertEquals(true, cookie.getDiscard());
        TestCase.assertEquals(server.getCookieDomain(), cookie.getDomain());
        TestCase.assertEquals(60, cookie.getMaxAge());
        TestCase.assertEquals("/path", cookie.getPath());
        TestCase.assertEquals(("80,443," + (server.getPort())), cookie.getPortlist());
        TestCase.assertEquals(true, cookie.getSecure());
        TestCase.assertEquals(1, cookie.getVersion());
    }

    public void testResponseWithMultipleCookieHeaderLines() throws Exception {
        AbstractCookiesTest.TestCookieStore cookieStore = new AbstractCookiesTest.TestCookieStore();
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        cookieManager.put(new URI("http://android.com"), AbstractCookiesTest.cookieHeaders("a=android", "b=banana"));
        List<HttpCookie> cookies = sortedCopy(cookieStore.cookies);
        TestCase.assertEquals(2, cookies.size());
        HttpCookie cookieA = cookies.get(0);
        TestCase.assertEquals("a", cookieA.getName());
        TestCase.assertEquals("android", cookieA.getValue());
        HttpCookie cookieB = cookies.get(1);
        TestCase.assertEquals("b", cookieB.getName());
        TestCase.assertEquals("banana", cookieB.getValue());
    }

    public void testDomainDefaulting() throws Exception {
        AbstractCookiesTest.TestCookieStore cookieStore = new AbstractCookiesTest.TestCookieStore();
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        cookieManager.put(new URI("http://android.com/"), AbstractCookiesTest.cookieHeaders("a=android"));
        TestCase.assertEquals("android.com", cookieStore.getCookie("a").getDomain());
    }

    public void testNonMatchingDomainsRejected() throws Exception {
        AbstractCookiesTest.TestCookieStore cookieStore = new AbstractCookiesTest.TestCookieStore();
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        cookieManager.put(new URI("http://android.com/"), AbstractCookiesTest.cookieHeaders("a=android;domain=google.com"));
        TestCase.assertEquals(Collections.<HttpCookie>emptyList(), cookieStore.cookies);
    }

    public void testMatchingDomainsAccepted() throws Exception {
        AbstractCookiesTest.TestCookieStore cookieStore = new AbstractCookiesTest.TestCookieStore();
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        cookieManager.put(new URI("http://www.android.com/"), AbstractCookiesTest.cookieHeaders("a=android;domain=.android.com"));
        TestCase.assertEquals(".android.com", cookieStore.getCookie("a").getDomain());
    }

    public void testPathDefaulting() throws Exception {
        AbstractCookiesTest.TestCookieStore cookieStore = new AbstractCookiesTest.TestCookieStore();
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        cookieManager.put(new URI("http://android.com/foo/bar"), AbstractCookiesTest.cookieHeaders("a=android"));
        TestCase.assertEquals("/foo/", cookieStore.getCookie("a").getPath());
        cookieManager.put(new URI("http://android.com/"), AbstractCookiesTest.cookieHeaders("b=banana"));
        TestCase.assertEquals("/", cookieStore.getCookie("b").getPath());
        cookieManager.put(new URI("http://android.com/foo/"), AbstractCookiesTest.cookieHeaders("c=carrot"));
        TestCase.assertEquals("/foo/", cookieStore.getCookie("c").getPath());
    }

    public void testNonMatchingPathsRejected() throws Exception {
        AbstractCookiesTest.TestCookieStore cookieStore = new AbstractCookiesTest.TestCookieStore();
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        cookieManager.put(new URI("http://android.com/foo/bar"), AbstractCookiesTest.cookieHeaders("a=android;path=/baz/bar"));
        TestCase.assertEquals("Expected to reject cookies whose path is not a prefix of the request path", Collections.<HttpCookie>emptyList(), cookieStore.cookies);// RI6 fails this

    }

    public void testMatchingPathsAccepted() throws Exception {
        AbstractCookiesTest.TestCookieStore cookieStore = new AbstractCookiesTest.TestCookieStore();
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        cookieManager.put(new URI("http://android.com/foo/bar/"), AbstractCookiesTest.cookieHeaders("a=android;path=/foo"));
        TestCase.assertEquals("/foo", cookieStore.getCookie("a").getPath());
    }

    public void testNoCookieHeaderSentIfNoCookiesMatch() throws IOException, URISyntaxException {
        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        Map<String, List<String>> cookieHeaders = cookieManager.get(new URI("http://android.com/foo/bar/"), AbstractCookiesTest.EMPTY_COOKIES_MAP);
        TestCase.assertTrue(cookieHeaders.toString(), ((cookieHeaders.isEmpty()) || (((cookieHeaders.size()) == 1) && (cookieHeaders.get("Cookie").isEmpty()))));
    }

    public void testCookieManagerGet_schemeChecks() throws Exception {
        CookieManager cookieManager = new CookieManager(createCookieStore(), null);
        cookieManager.put(new URI("http://a.com/"), AbstractCookiesTest.cookieHeaders("a1=android"));
        cookieManager.put(new URI("https://a.com/"), AbstractCookiesTest.cookieHeaders("a2=android"));
        cookieManager.put(new URI("https://a.com/"), AbstractCookiesTest.cookieHeaders("a3=android; Secure"));
        AbstractCookiesTest.assertManagerCookiesMatch(cookieManager, "http://a.com/", "a1=android; a2=android");
        AbstractCookiesTest.assertManagerCookiesMatch(cookieManager, "https://a.com/", "a1=android; a2=android; a3=android");
    }

    public void testCookieManagerGet_hostChecks() throws Exception {
        CookieManager cookieManager = new CookieManager(createCookieStore(), null);
        cookieManager.put(new URI("http://a.com/"), AbstractCookiesTest.cookieHeaders("a1=android"));
        cookieManager.put(new URI("http://b.com/"), AbstractCookiesTest.cookieHeaders("b1=android"));
        AbstractCookiesTest.assertManagerCookiesMatch(cookieManager, "http://a.com/", "a1=android");
        AbstractCookiesTest.assertManagerCookiesMatch(cookieManager, "http://b.com/", "b1=android");
    }

    public void testCookieManagerGet_portChecks() throws Exception {
        CookieManager cookieManager = new CookieManager(createCookieStore(), null);
        cookieManager.put(new URI("http://a.com:443/"), AbstractCookiesTest.cookieHeaders("a1=android"));
        cookieManager.put(new URI("http://a.com:8080/"), AbstractCookiesTest.cookieHeaders("a2=android"));
        cookieManager.put(new URI("http://a.com:8080/"), AbstractCookiesTest.cookieHeaders("a3=android; Port=8080"));
        AbstractCookiesTest.assertManagerCookiesMatch(cookieManager, "http://a.com/", "a1=android; a2=android");
        AbstractCookiesTest.assertManagerCookiesMatch(cookieManager, "http://a.com:8080/", "a1=android; a2=android; a3=android");
    }

    public void testCookieManagerGet_pathChecks() throws Exception {
        CookieManager cookieManager = new CookieManager(createCookieStore(), null);
        cookieManager.put(new URI("http://a.com/"), AbstractCookiesTest.cookieHeaders("a1=android"));
        cookieManager.put(new URI("http://a.com/path1"), AbstractCookiesTest.cookieHeaders("a2=android; Path=\"/path1\""));
        cookieManager.put(new URI("http://a.com/path2"), AbstractCookiesTest.cookieHeaders("a3=android; Path=\"/path2\""));
        AbstractCookiesTest.assertManagerCookiesMatch(cookieManager, "http://a.com/notpath", "a1=android");
        AbstractCookiesTest.assertManagerCookiesMatch(cookieManager, "http://a.com/path1", "a1=android; a2=android");
    }

    public void testSendingCookiesFromStore() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse());
        server.play();
        CookieManager cookieManager = new CookieManager(createCookieStore(), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        HttpCookie cookieA = AbstractCookiesTest.createCookie("a", "android", server.getCookieDomain(), "/");
        cookieManager.getCookieStore().add(server.getUrl("/").toURI(), cookieA);
        HttpCookie cookieB = AbstractCookiesTest.createCookie("b", "banana", server.getCookieDomain(), "/");
        cookieManager.getCookieStore().add(server.getUrl("/").toURI(), cookieB);
        CookieHandler.setDefault(cookieManager);
        get(server, "/");
        RecordedRequest request = server.takeRequest();
        List<String> receivedHeaders = request.getHeaders();
        assertContains(receivedHeaders, (((((("Cookie: $Version=\"1\"; " + "a=\"android\";$Path=\"/\";$Domain=\"") + (server.getCookieDomain())) + "\"; ") + "b=\"banana\";$Path=\"/\";$Domain=\"") + (server.getCookieDomain())) + "\""));
    }

    // TODO(user): b/65289980.
    // public void testRedirectsDoNotIncludeTooManyCookies() throws Exception {
    // MockWebServer redirectTarget = new MockWebServer();
    // redirectTarget.enqueue(new MockResponse().setBody("A"));
    // redirectTarget.play();
    // 
    // MockWebServer redirectSource = new MockWebServer();
    // redirectSource.enqueue(new MockResponse()
    // .setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP)
    // .addHeader("Location: " + redirectTarget.getUrl("/")));
    // redirectSource.play();
    // 
    // CookieManager cookieManager = new CookieManager(createCookieStore(),
    // ACCEPT_ORIGINAL_SERVER);
    // HttpCookie cookie = createCookie("c", "cookie", redirectSource.getCookieDomain(), "/");
    // String portList = Integer.toString(redirectSource.getPort());
    // cookie.setPortlist(portList);
    // cookieManager.getCookieStore().add(redirectSource.getUrl("/").toURI(), cookie);
    // CookieHandler.setDefault(cookieManager);
    // 
    // get(redirectSource, "/");
    // RecordedRequest request = redirectSource.takeRequest();
    // 
    // assertContains(request.getHeaders(), "Cookie: $Version=\"1\"; "
    // + "c=\"cookie\";$Path=\"/\";$Domain=\"" + redirectSource.getCookieDomain()
    // + "\";$Port=\"" + portList + "\"");
    // 
    // for (String header : redirectTarget.takeRequest().getHeaders()) {
    // if (header.startsWith("Cookie")) {
    // fail(header);
    // }
    // }
    // }
    /**
     * Test which headers show up where. The cookie manager should be notified
     * of both user-specified and derived headers like {@code Host}. Headers
     * named {@code Cookie} or {@code Cookie2} that are returned by the cookie
     * manager should show up in the request and in {@code getRequestProperties}.
     */
    // TODO(user): b/65289980.
    // public void testHeadersSentToCookieHandler() throws IOException, InterruptedException {
    // final Map<String, List<String>> cookieHandlerHeaders = new HashMap<String, List<String>>();
    // CookieHandler.setDefault(new CookieManager(createCookieStore(), null) {
    // @Override
    // public Map<String, List<String>> get(URI uri,
    // Map<String, List<String>> requestHeaders) throws IOException {
    // cookieHandlerHeaders.putAll(requestHeaders);
    // Map<String, List<String>> result = new HashMap<String, List<String>>();
    // result.put("Cookie", Collections.singletonList("Bar=bar"));
    // result.put("Cookie2", Collections.singletonList("Baz=baz"));
    // result.put("Quux", Collections.singletonList("quux"));
    // return result;
    // }
    // });
    // MockWebServer server = new MockWebServer();
    // server.enqueue(new MockResponse());
    // server.play();
    // 
    // HttpURLConnection connection = (HttpURLConnection) server.getUrl("/").openConnection();
    // assertEquals(Collections.<String, List<String>>emptyMap(),
    // connection.getRequestProperties());
    // 
    // connection.setRequestProperty("Foo", "foo");
    // connection.setDoOutput(true);
    // connection.getOutputStream().write(5);
    // connection.getOutputStream().close();
    // connection.getInputStream().close();
    // 
    // RecordedRequest request = server.takeRequest();
    // 
    // assertContainsAll(cookieHandlerHeaders.keySet(), "Foo");
    // assertContainsAll(cookieHandlerHeaders.keySet(),
    // "Content-Type", "User-Agent", "Connection", "Host");
    // assertFalse(cookieHandlerHeaders.containsKey("Cookie"));
    // 
    // /*
    // * The API specifies that calling getRequestProperties() on a connected instance should fail
    // * with an IllegalStateException, but the RI violates the spec and returns a valid map.
    // * http://www.mail-archive.com/net-dev@openjdk.java.net/msg01768.html
    // */
    // try {
    // assertContainsAll(connection.getRequestProperties().keySet(), "Foo");
    // assertContainsAll(connection.getRequestProperties().keySet(),
    // "Content-Type", "Content-Length", "User-Agent", "Connection", "Host");
    // assertContainsAll(connection.getRequestProperties().keySet(), "Cookie", "Cookie2");
    // assertFalse(connection.getRequestProperties().containsKey("Quux"));
    // } catch (IllegalStateException expected) {
    // }
    // 
    // assertContainsAll(request.getHeaders(), "Foo: foo", "Cookie: Bar=bar", "Cookie2: Baz=baz");
    // assertFalse(request.getHeaders().contains("Quux: quux"));
    // }
    public void testCookiesSentIgnoresCase() throws Exception {
        CookieHandler.setDefault(new CookieManager(createCookieStore(), null) {
            @Override
            public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) throws IOException {
                Map<String, List<String>> result = new HashMap<String, List<String>>();
                result.put("COOKIE", Collections.singletonList("Bar=bar"));
                result.put("cooKIE2", Collections.singletonList("Baz=baz"));
                return result;
            }
        });
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse());
        server.play();
        get(server, "/");
        RecordedRequest request = server.takeRequest();
        assertContainsAll(request.getHeaders(), "COOKIE: Bar=bar", "cooKIE2: Baz=baz");
        TestCase.assertFalse(request.getHeaders().contains("Quux: quux"));
    }

    /**
     * RFC 2109 and RFC 2965 disagree here. 2109 says two equals strings match only if they are
     * fully-qualified domain names. 2965 says two equal strings always match. We're testing for
     * 2109 behavior because it's more widely used, it's more conservative, and it's what the RI
     * does.
     */
    public void testDomainMatchesOnLocalAddresses() {
        TestCase.assertFalse(HttpCookie.domainMatches("localhost", "localhost"));
        TestCase.assertFalse(HttpCookie.domainMatches("b", "b"));
    }

    public void testDomainMatchesOnIpAddress() {
        TestCase.assertTrue(HttpCookie.domainMatches("127.0.0.1", "127.0.0.1"));
        TestCase.assertFalse(HttpCookie.domainMatches("127.0.0.1", "127.0.0.0"));
        TestCase.assertFalse(HttpCookie.domainMatches("127.0.0.1", "localhost"));
    }

    public void testDomainMatchesCaseMapping() {
        testDomainMatchesCaseMapping(Locale.US);
    }

    public void testDomainMatchesCaseMappingExoticLocale() {
        testDomainMatchesCaseMapping(new Locale("tr", "TR"));
    }

    /**
     * From the spec, "If an explicitly specified value does not start with a dot, the user agent
     * supplies a leading dot.". This prepending doesn't happen in setDomain.
     */
    public void testDomainNotAutomaticallyPrefixedWithDot() {
        HttpCookie cookie = new HttpCookie("Foo", "foo");
        cookie.setDomain("localhost");
        TestCase.assertEquals("localhost", cookie.getDomain());
    }

    public void testCookieStoreNullUris() {
        CookieStore cookieStore = new CookieManager(createCookieStore(), null).getCookieStore();
        HttpCookie cookieA = AbstractCookiesTest.createCookie("a", "android", ".android.com", "/source");
        HttpCookie cookieB = AbstractCookiesTest.createCookie("b", "banana", "code.google.com", "/p/android");
        try {
            cookieStore.add(null, cookieA);
        } catch (NullPointerException expected) {
            // the RI crashes even though the cookie does get added to the store; sigh
            expected.printStackTrace();
        }
        TestCase.assertEquals(Arrays.asList(cookieA), cookieStore.getCookies());
        try {
            cookieStore.add(null, cookieB);
        } catch (NullPointerException expected) {
        }
        TestCase.assertEquals(Arrays.asList(cookieA, cookieB), cookieStore.getCookies());
        try {
            cookieStore.get(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        TestCase.assertEquals(Collections.<URI>emptyList(), cookieStore.getURIs());
        TestCase.assertTrue(cookieStore.remove(null, cookieA));
        TestCase.assertEquals(Arrays.asList(cookieB), cookieStore.getCookies());
        TestCase.assertTrue(cookieStore.removeAll());
        TestCase.assertEquals(Collections.<URI>emptyList(), cookieStore.getURIs());
        TestCase.assertEquals(Collections.<HttpCookie>emptyList(), cookieStore.getCookies());
    }

    public void testCookieStoreRemoveAll() throws URISyntaxException {
        CookieStore cookieStore = new CookieManager(createCookieStore(), null).getCookieStore();
        cookieStore.add(new URI("http://code.google.com/"), new HttpCookie("a", "android"));
        TestCase.assertTrue(cookieStore.removeAll());
        TestCase.assertEquals(Collections.<URI>emptyList(), cookieStore.getURIs());
        TestCase.assertEquals(Collections.<HttpCookie>emptyList(), cookieStore.getCookies());
        TestCase.assertFalse("Expected removeAll() to return false when the call doesn't mutate the store", cookieStore.removeAll());// RI6 fails this

    }

    public void testCookieStoreAddAcceptsConflictingUri() throws URISyntaxException {
        CookieStore cookieStore = new CookieManager().getCookieStore();
        HttpCookie cookieA = AbstractCookiesTest.createCookie("a", "android", ".android.com", "/source/");
        cookieStore.add(new URI("http://google.com/source/"), cookieA);
        TestCase.assertEquals(Arrays.asList(cookieA), cookieStore.getCookies());
    }

    public void testCookieStoreRemoveRequiresUri() throws URISyntaxException {
        CookieStore cookieStore = new CookieManager(createCookieStore(), null).getCookieStore();
        HttpCookie cookieA = new HttpCookie("a", "android");
        cookieStore.add(new URI("http://android.com/source/"), cookieA);
        // RI6 fails this
        TestCase.assertFalse("Expected remove() to take the cookie URI into account.", cookieStore.remove(new URI("http://code.google.com/"), cookieA));
        TestCase.assertEquals(Arrays.asList(cookieA), cookieStore.getCookies());
    }

    public void testCookieStoreUriUsesHttpSchemeAlways() throws URISyntaxException {
        CookieStore cookieStore = new CookieManager(createCookieStore(), null).getCookieStore();
        cookieStore.add(new URI("https://a.com/"), new HttpCookie("a", "android"));
        TestCase.assertEquals(Arrays.asList(new URI("http://a.com")), cookieStore.getURIs());
    }

    public void testCookieStoreUriDropsUserInfo() throws URISyntaxException {
        CookieStore cookieStore = new CookieManager(createCookieStore(), null).getCookieStore();
        cookieStore.add(new URI("http://jesse:secret@a.com/"), new HttpCookie("a", "android"));
        TestCase.assertEquals(Arrays.asList(new URI("http://a.com")), cookieStore.getURIs());
    }

    public void testCookieStoreUriKeepsHost() throws URISyntaxException {
        CookieStore cookieStore = new CookieManager(createCookieStore(), null).getCookieStore();
        cookieStore.add(new URI("http://b.com/"), new HttpCookie("a", "android"));
        TestCase.assertEquals(Arrays.asList(new URI("http://b.com")), cookieStore.getURIs());
    }

    public void testCookieStoreUriDropsPort() throws URISyntaxException {
        CookieStore cookieStore = new CookieManager(createCookieStore(), null).getCookieStore();
        cookieStore.add(new URI("http://a.com:443/"), new HttpCookie("a", "android"));
        TestCase.assertEquals(Arrays.asList(new URI("http://a.com")), cookieStore.getURIs());
    }

    public void testCookieStoreUriDropsPath() throws URISyntaxException {
        CookieStore cookieStore = new CookieManager(createCookieStore(), null).getCookieStore();
        cookieStore.add(new URI("http://a.com/a/"), new HttpCookie("a", "android"));
        TestCase.assertEquals(Arrays.asList(new URI("http://a.com")), cookieStore.getURIs());
    }

    public void testCookieStoreUriDropsFragment() throws URISyntaxException {
        CookieStore cookieStore = new CookieManager(createCookieStore(), null).getCookieStore();
        cookieStore.add(new URI("http://a.com/a/foo#fragment"), new HttpCookie("a", "android"));
        TestCase.assertEquals(Arrays.asList(new URI("http://a.com")), cookieStore.getURIs());
    }

    public void testCookieStoreUriDropsQuery() throws URISyntaxException {
        CookieStore cookieStore = new CookieManager(createCookieStore(), null).getCookieStore();
        cookieStore.add(new URI("http://a.com/a/foo?query=value"), new HttpCookie("a", "android"));
        TestCase.assertEquals(Arrays.asList(new URI("http://a.com")), cookieStore.getURIs());
    }

    public void testCookieStoreGet() throws Exception {
        CookieStore cookieStore = new CookieManager(createCookieStore(), null).getCookieStore();
        HttpCookie cookiePort1 = AbstractCookiesTest.createCookie("a1", "android", "a.com", "/path1");
        HttpCookie cookiePort2 = AbstractCookiesTest.createCookie("a2", "android", "a.com", "/path2");
        HttpCookie secureCookie = AbstractCookiesTest.createCookie("a3", "android", "a.com", "/path3");
        secureCookie.setSecure(true);
        HttpCookie notSecureCookie = AbstractCookiesTest.createCookie("a4", "android", "a.com", "/path4");
        HttpCookie bCookie = AbstractCookiesTest.createCookie("b1", "android", "b.com", "/path5");
        cookieStore.add(new URI("http://a.com:443/path1"), cookiePort1);
        cookieStore.add(new URI("http://a.com:8080/path2"), cookiePort2);
        cookieStore.add(new URI("https://a.com:443/path3"), secureCookie);
        cookieStore.add(new URI("https://a.com:443/path4"), notSecureCookie);
        cookieStore.add(new URI("https://b.com:8080/path5"), bCookie);
        List<HttpCookie> expectedStoreCookies = new ArrayList<>();
        expectedStoreCookies.add(cookiePort1);
        expectedStoreCookies.add(cookiePort2);
        expectedStoreCookies.add(secureCookie);
        expectedStoreCookies.add(notSecureCookie);
        // The default CookieStore implementation on Android is currently responsible for matching
        // the host/domain but not handling other cookie rules: it ignores the scheme (e.g. "secure"
        // checks), port and path.
        // The tests below fail on the RI. It looks like in the RI it is CookieStoreImpl that is
        // enforcing "secure" checks.
        TestCase.assertEquals(expectedStoreCookies, cookieStore.get(new URI("http://a.com:443/anypath")));
        TestCase.assertEquals(expectedStoreCookies, cookieStore.get(new URI("http://a.com:8080/anypath")));
        TestCase.assertEquals(expectedStoreCookies, cookieStore.get(new URI("https://a.com/anypath")));
        TestCase.assertEquals(expectedStoreCookies, cookieStore.get(new URI("http://a.com/anypath")));
    }

    /**
     * Regression test for http://b/25682357 /
     * https://code.google.com/p/android/issues/detail?id=193475
     * CookieStoreImpl.get(URI) not handling ports properly in the absence of an explicit cookie
     * Domain.
     */
    public void testCookieStoreGetWithPort() throws Exception {
        CookieStore cookieStore = new CookieManager(createCookieStore(), null).getCookieStore();
        HttpCookie cookie = new HttpCookie("theme", "light");
        // Deliberately not setting the cookie domain or path.
        cookieStore.add(new URI("http://a.com:12345"), cookie);
        // CookieStoreImpl must ignore the port during retrieval when domain is not set.
        TestCase.assertEquals(1, cookieStore.get(new URI("http://a.com:12345/path1")).size());
        TestCase.assertEquals(1, cookieStore.get(new URI("http://a.com/path1")).size());
    }

    public void testCookieStoreGetWithSecure() throws Exception {
        CookieStore cookieStore = new CookieManager(createCookieStore(), null).getCookieStore();
        HttpCookie cookie = AbstractCookiesTest.createCookie("theme", "light", "a.com", "/path");
        cookie.setSecure(true);
        cookieStore.add(new URI("https://a.com/path"), cookie);
        // CookieStoreImpl on Android ignores the "Secure" attribute. The RI implements the secure
        // check in CookieStoreImpl. For safety / app compatibility, if this is changed Android
        // should probably implement it in both places.
        TestCase.assertEquals(1, cookieStore.get(new URI("http://a.com/path")).size());
        TestCase.assertEquals(1, cookieStore.get(new URI("https://a.com/path")).size());
    }

    public void testCookieStoreEviction() throws Exception {
        CookieStore cookieStore = new CookieManager(createCookieStore(), null).getCookieStore();
        HttpCookie themeCookie = AbstractCookiesTest.createCookie("theme", "light", "a.com", "/");
        cookieStore.add(new URI("http://a.com/"), themeCookie);
        HttpCookie sidCookie = AbstractCookiesTest.createCookie("sid", "mysid", "a.com", "/");
        cookieStore.add(new URI("http://a.com/"), sidCookie);
        HttpCookie replacementThemeCookie = AbstractCookiesTest.createCookie("theme", "dark", "a.com", "/");
        cookieStore.add(new URI("http://a.com/"), replacementThemeCookie);
        // toString() is used below to avoid confusion with assertEquals():
        // HttpCookie.equals() is implemented so that it only checks name, path and domain
        // attributes but we also want to check the value.
        TestCase.assertEquals(("[sid=\"mysid\";$Path=\"/\";$Domain=\"a.com\", " + "theme=\"dark\";$Path=\"/\";$Domain=\"a.com\"]"), cookieStore.get(new URI("http://a.com/")).toString());
        HttpCookie replacementSidCookie = AbstractCookiesTest.createCookie("sid", "mynewsid", "A.cOm", "/");
        cookieStore.add(new URI("http://a.com/"), replacementSidCookie);
        TestCase.assertEquals(("[theme=\"dark\";$Path=\"/\";$Domain=\"a.com\", " + "sid=\"mynewsid\";$Path=\"/\";$Domain=\"a.com\"]"), cookieStore.get(new URI("http://a.com/")).toString());
    }

    /**
     * CookieStoreImpl has a strict requirement on HttpCookie.equals() to enable replacement of
     * cookies with the same name.
     */
    public void testCookieEquality() throws Exception {
        HttpCookie baseCookie = AbstractCookiesTest.createCookie("theme", "light", "a.com", "/");
        // None of the attributes immediately below should affect equality otherwise CookieStoreImpl
        // eviction will not work as intended.
        HttpCookie valueCookie = AbstractCookiesTest.createCookie("theme", "light", "a.com", "/");
        valueCookie.setValue("dark");
        valueCookie.setPortlist("1234");
        valueCookie.setSecure(true);
        valueCookie.setComment("comment");
        valueCookie.setCommentURL("commentURL");
        valueCookie.setDiscard(true);
        valueCookie.setMaxAge(12345L);
        valueCookie.setVersion(1);
        TestCase.assertEquals(baseCookie, valueCookie);
        // Changing any of the 3 main identity attributes should render cookies unequal.
        AbstractCookiesTest.assertNotEquals(AbstractCookiesTest.createCookie("theme2", "light", "a.com", "/"), baseCookie);
        AbstractCookiesTest.assertNotEquals(AbstractCookiesTest.createCookie("theme", "light", "b.com", "/"), baseCookie);
        AbstractCookiesTest.assertNotEquals(AbstractCookiesTest.createCookie("theme", "light", "a.com", "/path"), baseCookie);
    }

    static class TestCookieStore implements CookieStore {
        private final List<HttpCookie> cookies = new ArrayList<HttpCookie>();

        public void add(URI uri, HttpCookie cookie) {
            cookies.add(cookie);
        }

        public HttpCookie getCookie(String name) {
            for (HttpCookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
            throw new IllegalArgumentException(((("No cookie " + name) + " in ") + (cookies)));
        }

        public List<HttpCookie> get(URI uri) {
            throw new UnsupportedOperationException();
        }

        public List<HttpCookie> getCookies() {
            throw new UnsupportedOperationException();
        }

        public List<URI> getURIs() {
            throw new UnsupportedOperationException();
        }

        public boolean remove(URI uri, HttpCookie cookie) {
            throw new UnsupportedOperationException();
        }

        public boolean removeAll() {
            throw new UnsupportedOperationException();
        }
    }

    // 
    // Start of org.apache.harmony.tests.java.net.CookieManagerTest.
    // 
    /**
     * java.net.CookieStore#add(URI, HttpCookie)
     *
     * @since 1.6
     */
    public void test_add_LURI_LHttpCookie() throws URISyntaxException {
        URI uri = new URI("http://harmony.test.unit.org");
        HttpCookie cookie = new HttpCookie("name1", "value1");
        cookie.setDiscard(true);
        // This needn't throw. We should use the cookie's domain, if set.
        // If no domain is set, this cookie will languish in the store until
        // it expires.
        cookieStore.add(null, cookie);
        try {
            cookieStore.add(uri, null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            cookieStore.add(null, null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        cookieStore.add(uri, cookie);
        List<HttpCookie> list = cookieStore.get(uri);
        TestCase.assertEquals(1, list.size());
        TestCase.assertTrue(list.contains(cookie));
        HttpCookie cookie2 = new HttpCookie("  NaME1   ", "  TESTVALUE1   ");
        cookieStore.add(uri, cookie2);
        list = cookieStore.get(uri);
        TestCase.assertEquals(1, list.size());
        TestCase.assertEquals("  TESTVALUE1   ", list.get(0).getValue());
        TestCase.assertTrue(list.contains(cookie2));
        // domain and path attributes works
        HttpCookie anotherCookie = new HttpCookie("name1", "value1");
        anotherCookie.setDomain("domain");
        anotherCookie.setPath("Path");
        cookieStore.add(uri, anotherCookie);
        list = cookieStore.get(uri);
        TestCase.assertEquals(2, list.size());
        TestCase.assertNull(list.get(0).getDomain());
        TestCase.assertEquals("domain", list.get(1).getDomain());
        TestCase.assertEquals("Path", list.get(1).getPath());
        URI uri2 = new URI("http://.test.unit.org");
        HttpCookie cookie3 = new HttpCookie("NaME2", "VALUE2");
        cookieStore.add(uri2, cookie3);
        list = cookieStore.get(uri2);
        TestCase.assertEquals(1, list.size());
        TestCase.assertEquals("VALUE2", list.get(0).getValue());
        list = cookieStore.getCookies();
        TestCase.assertEquals(3, list.size());
        // expired cookie won't be selected.
        HttpCookie cookie4 = new HttpCookie("cookie4", "value4");
        cookie4.setMaxAge((-2));
        TestCase.assertTrue(cookie4.hasExpired());
        cookieStore.add(uri2, cookie4);
        list = cookieStore.getCookies();
        TestCase.assertEquals(3, list.size());
        TestCase.assertFalse(cookieStore.remove(uri2, cookie4));
        cookie4.setMaxAge(3000);
        cookie4.setDomain("domain");
        cookie4.setPath("path");
        cookieStore.add(uri2, cookie4);
        list = cookieStore.get(uri2);
        TestCase.assertEquals(2, list.size());
        cookieStore.add(uri, cookie4);
        list = cookieStore.get(uri);
        TestCase.assertEquals(3, list.size());
        list = cookieStore.get(uri2);
        TestCase.assertEquals(2, list.size());
        list = cookieStore.getCookies();
        TestCase.assertEquals(4, list.size());
        URI baduri = new URI("bad_url");
        HttpCookie cookie6 = new HttpCookie("cookie5", "value5");
        cookieStore.add(baduri, cookie6);
        list = cookieStore.get(baduri);
        TestCase.assertTrue(list.contains(cookie6));
    }

    /**
     * java.net.CookieStore#get(URI)
     *
     * @since 1.6
     */
    public void test_get_LURI() throws URISyntaxException {
        try {
            cookieStore.get(null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        URI uri1 = new URI("http://get.uri1.test.org");
        List<HttpCookie> list = cookieStore.get(uri1);
        TestCase.assertTrue(list.isEmpty());
        HttpCookie cookie1 = new HttpCookie("cookie_name1", "cookie_value1");
        HttpCookie cookie2 = new HttpCookie("cookie_name2", "cookie_value2");
        cookieStore.add(uri1, cookie1);
        cookieStore.add(uri1, cookie2);
        URI uri2 = new URI("http://get.uri2.test.org");
        HttpCookie cookie3 = new HttpCookie("cookie_name3", "cookie_value3");
        cookieStore.add(uri2, cookie3);
        list = cookieStore.get(uri1);
        TestCase.assertEquals(2, list.size());
        list = cookieStore.get(uri2);
        TestCase.assertEquals(1, list.size());
        // domain-match cookies also be selected.
        HttpCookie cookie4 = new HttpCookie("cookie_name4", "cookie_value4");
        cookie4.setDomain(".uri1.test.org");
        cookieStore.add(uri2, cookie4);
        list = cookieStore.get(uri1);
        TestCase.assertEquals(3, list.size());
        cookieStore.add(uri1, cookie4);
        list = cookieStore.get(uri1);
        TestCase.assertEquals(3, list.size());
        list = cookieStore.get(uri2);
        TestCase.assertEquals(2, list.size());
        // expired cookie won't be selected.
        HttpCookie cookie5 = new HttpCookie("cookie_name5", "cookie_value5");
        cookie5.setMaxAge((-333));
        TestCase.assertTrue(cookie5.hasExpired());
        cookieStore.add(uri1, cookie5);
        list = cookieStore.get(uri1);
        TestCase.assertEquals(3, list.size());
        TestCase.assertFalse(cookieStore.remove(uri1, cookie5));
        list = cookieStore.getCookies();
        TestCase.assertEquals(4, list.size());
        cookie4.setMaxAge((-123));
        list = cookieStore.get(uri1);
        TestCase.assertEquals(2, list.size());
        list = cookieStore.getCookies();
        TestCase.assertEquals(3, list.size());
        // expired cookies are also deleted even if it domain-matches the URI
        HttpCookie cookie6 = new HttpCookie("cookie_name6", "cookie_value6");
        cookie6.setMaxAge((-2));
        cookie6.setDomain(".uri1.test.org");
        cookieStore.add(uri2, cookie6);
        list = cookieStore.get(uri1);
        TestCase.assertEquals(2, list.size());
        TestCase.assertFalse(cookieStore.remove(null, cookie6));
        URI uri3 = new URI("http://get.uri3.test.org");
        TestCase.assertTrue(cookieStore.get(uri3).isEmpty());
        URI baduri = new URI("invalid_uri");
        TestCase.assertTrue(cookieStore.get(baduri).isEmpty());
    }

    /**
     * java.net.CookieStore#getCookies()
     *
     * @since 1.6
     */
    public void test_getCookies() throws URISyntaxException {
        List<HttpCookie> list = cookieStore.getCookies();
        TestCase.assertTrue(list.isEmpty());
        TestCase.assertTrue((list instanceof RandomAccess));
        HttpCookie cookie1 = new HttpCookie("cookie_name", "cookie_value");
        URI uri1 = new URI("http://getcookies1.test.org");
        cookieStore.add(uri1, cookie1);
        list = cookieStore.getCookies();
        TestCase.assertTrue(list.contains(cookie1));
        HttpCookie cookie2 = new HttpCookie("cookie_name2", "cookie_value2");
        URI uri2 = new URI("http://getcookies2.test.org");
        cookieStore.add(uri2, cookie2);
        list = cookieStore.getCookies();
        TestCase.assertEquals(2, list.size());
        TestCase.assertTrue(list.contains(cookie1));
        TestCase.assertTrue(list.contains(cookie2));
        // duplicated cookie won't be selected.
        cookieStore.add(uri2, cookie1);
        list = cookieStore.getCookies();
        TestCase.assertEquals(2, list.size());
        // expired cookie won't be selected.
        HttpCookie cookie3 = new HttpCookie("cookie_name3", "cookie_value3");
        cookie3.setMaxAge((-1357));
        cookieStore.add(uri1, cookie3);
        list = cookieStore.getCookies();
        TestCase.assertEquals(2, list.size());
        try {
            list.add(new HttpCookie("readOnlyName", "readOnlyValue"));
            TestCase.fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            list.remove(new HttpCookie("readOnlyName", "readOnlyValue"));
            TestCase.fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    /**
     * java.net.CookieStore#getURIs()
     *
     * @since 1.6
     */
    public void test_getURIs() throws URISyntaxException {
        List<URI> list = cookieStore.getURIs();
        TestCase.assertTrue(list.isEmpty());
        URI uri1 = new URI("http://geturis1.test.com");
        HttpCookie cookie1 = new HttpCookie("cookie_name1", "cookie_value1");
        cookieStore.add(uri1, cookie1);
        list = cookieStore.getURIs();
        TestCase.assertEquals("geturis1.test.com", list.get(0).getHost());
        HttpCookie cookie2 = new HttpCookie("cookie_name2", "cookie_value2");
        cookieStore.add(uri1, cookie2);
        list = cookieStore.getURIs();
        TestCase.assertEquals(1, list.size());
        URI uri2 = new URI("http://geturis2.test.com");
        cookieStore.add(uri2, cookie2);
        list = cookieStore.getURIs();
        TestCase.assertEquals(2, list.size());
        TestCase.assertTrue(list.contains(uri1));
        TestCase.assertTrue(list.contains(uri2));
    }

    /**
     * java.net.CookieStore#remove(URI, HttpCookie)
     *
     * @since 1.6
     */
    public void test_remove_LURI_LHttpCookie() throws URISyntaxException {
        URI uri1 = new URI("http://remove1.test.com");
        HttpCookie cookie1 = new HttpCookie("cookie_name1", "cookie_value1");
        try {
            cookieStore.remove(uri1, null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        TestCase.assertFalse(cookieStore.remove(uri1, cookie1));
        TestCase.assertFalse(cookieStore.remove(null, cookie1));
        cookieStore.add(uri1, cookie1);
        URI uri2 = new URI("http://remove2.test.com");
        HttpCookie cookie2 = new HttpCookie("cookie_name2", "cookie_value2");
        cookieStore.add(uri2, cookie2);
        TestCase.assertTrue(cookieStore.remove(uri1, cookie1));
        TestCase.assertFalse(cookieStore.remove(uri1, cookie1));
        TestCase.assertEquals(2, cookieStore.getURIs().size());
        TestCase.assertEquals(1, cookieStore.getCookies().size());
        TestCase.assertTrue(cookieStore.remove(uri2, cookie2));
        TestCase.assertFalse(cookieStore.remove(uri2, cookie2));
        TestCase.assertEquals(2, cookieStore.getURIs().size());
        TestCase.assertEquals(0, cookieStore.getCookies().size());
        TestCase.assertTrue(cookieStore.removeAll());
        cookieStore.add(uri1, cookie1);
        cookieStore.add(uri2, cookie2);
        HttpCookie cookie3 = new HttpCookie("cookie_name3", "cookie_value3");
        TestCase.assertFalse(cookieStore.remove(null, cookie3));
        // No guarantees on behavior if we call remove with a different
        // uri from the one originally associated with the cookie.
        TestCase.assertFalse(cookieStore.remove(null, cookie1));
        TestCase.assertTrue(cookieStore.remove(uri1, cookie1));
        TestCase.assertFalse(cookieStore.remove(uri1, cookie1));
        TestCase.assertEquals(2, cookieStore.getURIs().size());
        TestCase.assertEquals(1, cookieStore.getCookies().size());
        TestCase.assertTrue(cookieStore.remove(uri2, cookie2));
        TestCase.assertFalse(cookieStore.remove(uri2, cookie2));
        TestCase.assertEquals(2, cookieStore.getURIs().size());
        TestCase.assertEquals(0, cookieStore.getCookies().size());
        cookieStore.removeAll();
        // expired cookies can also be deleted.
        cookie2.setMaxAge((-34857));
        cookieStore.add(uri2, cookie2);
        TestCase.assertTrue(cookieStore.remove(uri2, cookie2));
        TestCase.assertFalse(cookieStore.remove(uri2, cookie2));
        TestCase.assertEquals(0, cookieStore.getCookies().size());
        cookie2.setMaxAge(34857);
        cookieStore.add(uri1, cookie1);
        cookieStore.add(uri2, cookie1);
        cookieStore.add(uri2, cookie2);
        TestCase.assertTrue(cookieStore.remove(uri1, cookie1));
        TestCase.assertFalse(cookieStore.remove(uri1, cookie1));
        TestCase.assertTrue(cookieStore.get(uri2).contains(cookie1));
        TestCase.assertTrue(cookieStore.get(uri2).contains(cookie2));
        TestCase.assertEquals(0, cookieStore.get(uri1).size());
        cookieStore.remove(uri2, cookie2);
        cookieStore.removeAll();
        cookieStore.add(uri2, cookie2);
        cookieStore.add(uri1, cookie1);
        TestCase.assertEquals(2, cookieStore.getCookies().size());
        TestCase.assertFalse(cookieStore.remove(uri2, cookie1));
        TestCase.assertTrue(cookieStore.remove(uri1, cookie1));
        TestCase.assertEquals(2, cookieStore.getURIs().size());
        TestCase.assertEquals(1, cookieStore.getCookies().size());
        TestCase.assertTrue(cookieStore.getCookies().contains(cookie2));
        cookieStore.removeAll();
        URI uri3 = new URI("http://remove3.test.com");
        URI uri4 = new URI("http://test.com");
        HttpCookie cookie4 = new HttpCookie("cookie_name4", "cookie_value4");
        cookie4.setDomain(".test.com");
        cookie2.setMaxAge((-34857));
        cookie3.setMaxAge((-22));
        cookie4.setMaxAge((-45));
        cookieStore.add(uri1, cookie1);
        cookieStore.add(uri2, cookie2);
        cookieStore.add(uri3, cookie3);
        cookieStore.add(uri4, cookie4);
        TestCase.assertEquals(0, cookieStore.get(uri2).size());
        TestCase.assertFalse(cookieStore.remove(uri2, cookie2));
        TestCase.assertTrue(cookieStore.remove(uri3, cookie3));
        TestCase.assertFalse(cookieStore.remove(uri4, cookie4));
    }

    /**
     * java.net.CookieStore#test_removeAll()
     *
     * @since 1.6
     */
    public void test_removeAll() throws URISyntaxException {
        TestCase.assertFalse(cookieStore.removeAll());
        URI uri1 = new URI("http://removeAll1.test.com");
        HttpCookie cookie1 = new HttpCookie("cookie_name1", "cookie_value1");
        cookieStore.add(uri1, cookie1);
        URI uri2 = new URI("http://removeAll2.test.com");
        HttpCookie cookie2 = new HttpCookie("cookie_name2", "cookie_value2");
        cookieStore.add(uri2, cookie2);
        TestCase.assertTrue(cookieStore.removeAll());
        TestCase.assertTrue(cookieStore.getURIs().isEmpty());
        TestCase.assertTrue(cookieStore.getCookies().isEmpty());
        TestCase.assertFalse(cookieStore.removeAll());
    }

    /**
     * {@link java.net.CookieManager#get(java.net.URI, java.util.Map)} &
     * {@link java.net.CookieManager#put(java.net.URI, java.util.Map)}
     * IllegalArgumentException
     *
     * @since 1.6
     */
    public void test_Put_Get_LURI_LMap_exception() throws IOException, URISyntaxException {
        // get
        checkValidParams4Get(new URI(""), null);
        checkValidParams4Get(new URI("http://www.test.com"), null);
        checkValidParams4Get(null, null);
        checkValidParams4Get(null, new HashMap<String, List<String>>());
        // put
        checkValidParams4Put(new URI(""), null);
        checkValidParams4Put(new URI("http://www.test.com"), null);
        checkValidParams4Put(null, null);
        checkValidParams4Put(null, new HashMap<String, List<String>>());
    }

    /**
     * Unlike the RI, we flatten all matching cookies into a single Cookie header
     * instead of sending down multiple cookie headers. Also, when no cookies match
     * a given URI, we leave the requestHeaders unmodified.
     *
     * @since 1.6
     */
    public void test_Put_Get_LURI_LMap() throws IOException, URISyntaxException {
        // cookie-key | (content, URI)...
        String[][] cookies = new String[][]{ new String[]{ "Set-cookie", "Set-cookie:PREF=test;path=/;domain=.b.c;", "http://a.b.c/", "Set-cookie:PREF1=test2;path=/;domain=.beg.com;", "http://a.b.c/" }, new String[]{ "Set-cookie2", "Set-cookie2:NAME1=VALUE1;path=/te;domain=.b.c;", "http://a.b.c/test" }, new String[]{ "Set-cookie", "Set-cookie2:NAME=VALUE;path=/;domain=.beg.com;", "http://a.beg.com/test", "Set-cookie2:NAME1=VALUE1;path=/;domain=.beg.com;", "http://a.beg.com/test" }, new String[]{ "Set-cookie2", "Set-cookie3:NAME=VALUE;path=/;domain=.test.org;", "http://a.test.org/test" }, new String[]{ null, "Set-cookie3:NAME=VALUE;path=/te;domain=.test.org;", "http://a.test.org/test" }, new String[]{ "Set-cookie2", "lala", "http://a.test.org/test" } };
        // requires path of cookie is the prefix of uri
        // domain of cookie must match that of uri
        Map<String, List<String>> responseHeaders = AbstractCookiesTest.addCookie(new String[][]{ cookies[0], cookies[1] });
        CookieManager manager = store(new String[][]{ cookies[0], cookies[1] }, responseHeaders, null);
        HashMap<String, List<String>> dummyMap = new HashMap<String, List<String>>();
        Map<String, List<String>> map = manager.get(new URI("http://a.b.c/"), dummyMap);
        TestCase.assertEquals(1, map.size());
        List<String> list = map.get("Cookie");
        TestCase.assertEquals(1, list.size());
        // requires path of cookie is the prefix of uri
        map = manager.get(new URI("http://a.b.c/te"), dummyMap);
        list = map.get("Cookie");
        TestCase.assertEquals(1, list.size());
        TestCase.assertTrue(list.get(0).contains("PREF=test"));
        // Cookies from "/test" should *not* match the URI "/te".
        TestCase.assertFalse(list.get(0).contains("NAME=VALUE"));
        // If all cookies are of version 1, then $version=1 will be added
        // ,no matter the value cookie-key
        responseHeaders = AbstractCookiesTest.addCookie(new String[][]{ cookies[2] });
        manager = store(new String[][]{ cookies[2] }, responseHeaders, null);
        map = manager.get(new URI("http://a.beg.com/test"), dummyMap);
        list = map.get("Cookie");
        TestCase.assertEquals(1, list.size());
        TestCase.assertTrue(list.get(0).startsWith("$Version=\"1\""));
        // cookie-key will not have effect on determining cookie version
        responseHeaders = AbstractCookiesTest.addCookie(new String[][]{ cookies[3] });
        manager = store(new String[][]{ cookies[3] }, responseHeaders, null);
        map = manager.get(new URI("http://a.test.org/"), responseHeaders);
        list = map.get("Cookie");
        TestCase.assertEquals(1, list.size());
        TestCase.assertEquals("Set-cookie3:NAME=VALUE", list.get(0));
        // When key is null, no cookie can be stored/retrieved, even if policy =
        // ACCEPT_ALL
        responseHeaders = AbstractCookiesTest.addCookie(new String[][]{ cookies[4] });
        manager = store(new String[][]{ cookies[4] }, responseHeaders, CookiePolicy.ACCEPT_ALL);
        map = manager.get(new URI("http://a.test.org/"), responseHeaders);
        list = map.get("Cookie");
        TestCase.assertNull(list);
        // All cookies will be rejected if policy == ACCEPT_NONE
        responseHeaders = AbstractCookiesTest.addCookie(new String[][]{ cookies[3] });
        manager = store(new String[][]{ cookies[3] }, responseHeaders, CookiePolicy.ACCEPT_NONE);
        map = manager.get(new URI("http://a.test.org/"), responseHeaders);
        list = map.get("Cookie");
        TestCase.assertNull(list);
        responseHeaders = AbstractCookiesTest.addCookie(new String[][]{ cookies[5] });
        manager = store(new String[][]{ cookies[5] }, responseHeaders, CookiePolicy.ACCEPT_ALL);
        list = map.get("Cookie");
        TestCase.assertNull(list);
        try {
            map.put(null, null);
            TestCase.fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    /**
     * {@link java.net.CookieManager#CookieManager()}
     *
     * @since 1.6
     */
    public void test_CookieManager() {
        CookieManager cookieManager = new CookieManager();
        TestCase.assertNotNull(cookieManager);
        TestCase.assertNotNull(cookieManager.getCookieStore());
    }

    /**
     * {@link java.net.CookieManager#CookieManager(java.net.CookieStore, java.net.CookiePolicy)}
     *
     * @since 1.6
     */
    public void testCookieManager_LCookieStore_LCookiePolicy() {
        class DummyStore implements CookieStore {
            public String getName() {
                return "A dummy store";
            }

            public void add(URI uri, HttpCookie cookie) {
                // expected
            }

            public List<HttpCookie> get(URI uri) {
                return null;
            }

            public List<HttpCookie> getCookies() {
                return null;
            }

            public List<URI> getURIs() {
                return null;
            }

            public boolean remove(URI uri, HttpCookie cookie) {
                return false;
            }

            public boolean removeAll() {
                return false;
            }
        }
        CookieStore store = new DummyStore();
        CookieManager cookieManager = new CookieManager(store, CookiePolicy.ACCEPT_ALL);
        TestCase.assertEquals("A dummy store", ((DummyStore) (cookieManager.getCookieStore())).getName());
        TestCase.assertSame(store, cookieManager.getCookieStore());
    }

    /**
     * {@link java.net.CookieManager#setCookiePolicy(java.net.CookiePolicy)}
     *
     * @since 1.6
     */
    public void test_SetCookiePolicy_LCookiePolicy() throws IOException, URISyntaxException {
        // Policy = ACCEPT_NONE
        CookieManager manager = new CookieManager(createCookieStore(), null);
        manager.setCookiePolicy(CookiePolicy.ACCEPT_NONE);
        Map<String, List<String>> responseHeaders = new TreeMap<String, List<String>>();
        URI uri = new URI("http://a.b.c");
        manager.put(uri, responseHeaders);
        Map<String, List<String>> map = manager.get(uri, new HashMap<String, List<String>>());
        TestCase.assertEquals(0, map.size());
        // Policy = ACCEPT_ALL
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        responseHeaders = new TreeMap<String, List<String>>();
        ArrayList<String> list = new ArrayList<String>();
        list.add("test=null");
        responseHeaders.put("Set-cookie", list);
        uri = new URI("http://b.c.d");
        manager.put(uri, responseHeaders);
        map = manager.get(uri, new HashMap<String, List<String>>());
        TestCase.assertEquals(1, map.size());
    }

    /**
     * {@link java.net.CookieManager#getCookieStore()}
     *
     * @since 1.6
     */
    public void test_GetCookieStore() {
        CookieManager cookieManager = new CookieManager(createCookieStore(), null);
        CookieStore store = cookieManager.getCookieStore();
        TestCase.assertNotNull(store);
    }

    // http://b/25763487
    public void testCookieWithNullPath() throws Exception {
        AbstractCookiesTest.FakeSingleCookieStore fscs = new AbstractCookiesTest.FakeSingleCookieStore();
        CookieManager cm = new CookieManager(fscs, CookiePolicy.ACCEPT_ALL);
        HttpCookie cookie = new HttpCookie("foo", "bar");
        cookie.setDomain("http://www.foo.com");
        cookie.setVersion(0);
        fscs.setNextCookie(cookie);
        Map<String, List<String>> cookieHeaders = cm.get(new URI("http://www.foo.com/log/me/in"), Collections.EMPTY_MAP);
        List<String> cookies = cookieHeaders.get("Cookie");
        TestCase.assertEquals("foo=bar", cookies.get(0));
    }

    /**
     * A cookie store that always returns one cookie per URI (without any sort of
     * rule matching). The cookie that's returned is provided via a call to setNextCookie
     */
    public static class FakeSingleCookieStore implements CookieStore {
        private List<HttpCookie> cookies;

        void setNextCookie(HttpCookie cookie) {
            cookies = Collections.singletonList(cookie);
        }

        @Override
        public void add(URI uri, HttpCookie cookie) {
        }

        @Override
        public List<HttpCookie> get(URI uri) {
            return cookies;
        }

        @Override
        public List<HttpCookie> getCookies() {
            return cookies;
        }

        @Override
        public List<URI> getURIs() {
            return null;
        }

        @Override
        public boolean remove(URI uri, HttpCookie cookie) {
            cookies = Collections.EMPTY_LIST;
            return true;
        }

        @Override
        public boolean removeAll() {
            cookies = Collections.EMPTY_LIST;
            return true;
        }
    }
}

