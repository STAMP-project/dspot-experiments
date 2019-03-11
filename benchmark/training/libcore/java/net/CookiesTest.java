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
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import junit.framework.TestCase;


public class CookiesTest extends TestCase {
    private static final Map<String, List<String>> EMPTY_COOKIES_MAP = Collections.emptyMap();

    // RoboVM note: Added to restore default CookieHandler after each test
    private CookieHandler defaultCookieHandler;

    public void testNetscapeResponse() throws Exception {
        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);
        MockWebServer server = new MockWebServer();
        try {
            // RoboVM note: Modified to call server.shutdown() after test finishes.
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
        } finally {
            server.shutdown();
        }
    }

    public void testRfc2109Response() throws Exception {
        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);
        MockWebServer server = new MockWebServer();
        try {
            // RoboVM note: Modified to call server.shutdown() after test finishes.
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
        } finally {
            server.shutdown();
        }
    }

    public void testRfc2965Response() throws Exception {
        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);
        MockWebServer server = new MockWebServer();
        try {
            // RoboVM note: Modified to call server.shutdown() after test finishes.
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
        } finally {
            server.shutdown();
        }
    }

    public void testQuotedAttributeValues() throws Exception {
        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);
        MockWebServer server = new MockWebServer();
        try {
            // RoboVM note: Modified to call server.shutdown() after test finishes.
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
        } finally {
            server.shutdown();
        }
    }

    public void testResponseWithMultipleCookieHeaderLines() throws Exception {
        CookiesTest.TestCookieStore cookieStore = new CookiesTest.TestCookieStore();
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        cookieManager.put(new URI("http://android.com"), cookieHeaders("a=android", "b=banana"));
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
        CookiesTest.TestCookieStore cookieStore = new CookiesTest.TestCookieStore();
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        cookieManager.put(new URI("http://android.com/"), cookieHeaders("a=android"));
        TestCase.assertEquals("android.com", cookieStore.getCookie("a").getDomain());
    }

    public void testNonMatchingDomainsRejected() throws Exception {
        CookiesTest.TestCookieStore cookieStore = new CookiesTest.TestCookieStore();
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        cookieManager.put(new URI("http://android.com/"), cookieHeaders("a=android;domain=google.com"));
        TestCase.assertEquals(Collections.<HttpCookie>emptyList(), cookieStore.cookies);
    }

    public void testMatchingDomainsAccepted() throws Exception {
        CookiesTest.TestCookieStore cookieStore = new CookiesTest.TestCookieStore();
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        cookieManager.put(new URI("http://www.android.com/"), cookieHeaders("a=android;domain=.android.com"));
        TestCase.assertEquals(".android.com", cookieStore.getCookie("a").getDomain());
    }

    public void testPathDefaulting() throws Exception {
        CookiesTest.TestCookieStore cookieStore = new CookiesTest.TestCookieStore();
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        cookieManager.put(new URI("http://android.com/foo/bar"), cookieHeaders("a=android"));
        TestCase.assertEquals("/foo/", cookieStore.getCookie("a").getPath());
        cookieManager.put(new URI("http://android.com/"), cookieHeaders("b=banana"));
        TestCase.assertEquals("/", cookieStore.getCookie("b").getPath());
        cookieManager.put(new URI("http://android.com/foo/"), cookieHeaders("c=carrot"));
        TestCase.assertEquals("/foo/", cookieStore.getCookie("c").getPath());
    }

    public void testNonMatchingPathsRejected() throws Exception {
        CookiesTest.TestCookieStore cookieStore = new CookiesTest.TestCookieStore();
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        cookieManager.put(new URI("http://android.com/foo/bar"), cookieHeaders("a=android;path=/baz/bar"));
        TestCase.assertEquals("Expected to reject cookies whose path is not a prefix of the request path", Collections.<HttpCookie>emptyList(), cookieStore.cookies);// RI6 fails this

    }

    public void testMatchingPathsAccepted() throws Exception {
        CookiesTest.TestCookieStore cookieStore = new CookiesTest.TestCookieStore();
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        cookieManager.put(new URI("http://android.com/foo/bar/"), cookieHeaders("a=android;path=/foo"));
        TestCase.assertEquals("/foo", cookieStore.getCookie("a").getPath());
    }

    public void testNoCookieHeaderSentIfNoCookiesMatch() throws IOException, URISyntaxException {
        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        Map<String, List<String>> cookieHeaders = cookieManager.get(new URI("http://android.com/foo/bar/"), CookiesTest.EMPTY_COOKIES_MAP);
        TestCase.assertTrue(cookieHeaders.toString(), ((cookieHeaders.isEmpty()) || (((cookieHeaders.size()) == 1) && (cookieHeaders.get("Cookie").isEmpty()))));
    }

    public void testSendingCookiesFromStore() throws Exception {
        MockWebServer server = new MockWebServer();
        try {
            // RoboVM note: Modified to call server.shutdown() after test finishes.
            server.enqueue(new MockResponse());
            server.play();
            CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
            HttpCookie cookieA = new HttpCookie("a", "android");
            cookieA.setDomain(server.getCookieDomain());
            cookieA.setPath("/");
            cookieManager.getCookieStore().add(server.getUrl("/").toURI(), cookieA);
            HttpCookie cookieB = new HttpCookie("b", "banana");
            cookieB.setDomain(server.getCookieDomain());
            cookieB.setPath("/");
            cookieManager.getCookieStore().add(server.getUrl("/").toURI(), cookieB);
            CookieHandler.setDefault(cookieManager);
            get(server, "/");
            RecordedRequest request = server.takeRequest();
            List<String> receivedHeaders = request.getHeaders();
            assertContains(receivedHeaders, (((((("Cookie: $Version=\"1\"; " + "a=\"android\";$Path=\"/\";$Domain=\"") + (server.getCookieDomain())) + "\"; ") + "b=\"banana\";$Path=\"/\";$Domain=\"") + (server.getCookieDomain())) + "\""));
        } finally {
            server.shutdown();
        }
    }

    public void testRedirectsDoNotIncludeTooManyCookies() throws Exception {
        MockWebServer redirectTarget = new MockWebServer();
        try {
            // RoboVM note: Modified to call redirectTarget.shutdown() after test finishes.
            redirectTarget.enqueue(new MockResponse().setBody("A"));
            redirectTarget.play();
            MockWebServer redirectSource = new MockWebServer();
            try {
                // RoboVM note: Modified to call redirectSource.shutdown() after test finishes.
                redirectSource.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader(("Location: " + (redirectTarget.getUrl("/")))));
                redirectSource.play();
                CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
                HttpCookie cookie = new HttpCookie("c", "cookie");
                cookie.setDomain(redirectSource.getCookieDomain());
                cookie.setPath("/");
                String portList = Integer.toString(redirectSource.getPort());
                cookie.setPortlist(portList);
                cookieManager.getCookieStore().add(redirectSource.getUrl("/").toURI(), cookie);
                CookieHandler.setDefault(cookieManager);
                get(redirectSource, "/");
                RecordedRequest request = redirectSource.takeRequest();
                assertContains(request.getHeaders(), ((((("Cookie: $Version=\"1\"; " + "c=\"cookie\";$Path=\"/\";$Domain=\"") + (redirectSource.getCookieDomain())) + "\";$Port=\"") + portList) + "\""));
                for (String header : redirectTarget.takeRequest().getHeaders()) {
                    if (header.startsWith("Cookie")) {
                        TestCase.fail(header);
                    }
                }
            } finally {
                redirectSource.shutdown();
            }
        } finally {
            redirectTarget.shutdown();
        }
    }

    /**
     * Test which headers show up where. The cookie manager should be notified
     * of both user-specified and derived headers like {@code Host}. Headers
     * named {@code Cookie} or {@code Cookie2} that are returned by the cookie
     * manager should show up in the request and in {@code getRequestProperties}.
     */
    public void testHeadersSentToCookieHandler() throws IOException, InterruptedException {
        final Map<String, List<String>> cookieHandlerHeaders = new HashMap<String, List<String>>();
        CookieHandler.setDefault(new CookieManager() {
            @Override
            public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) throws IOException {
                cookieHandlerHeaders.putAll(requestHeaders);
                Map<String, List<String>> result = new HashMap<String, List<String>>();
                result.put("Cookie", Collections.singletonList("Bar=bar"));
                result.put("Cookie2", Collections.singletonList("Baz=baz"));
                result.put("Quux", Collections.singletonList("quux"));
                return result;
            }
        });
        MockWebServer server = new MockWebServer();
        try {
            // RoboVM note: Modified to call server.shutdown() after test finishes.
            server.enqueue(new MockResponse());
            server.play();
            HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
            TestCase.assertEquals(Collections.<String, List<String>>emptyMap(), connection.getRequestProperties());
            connection.setRequestProperty("Foo", "foo");
            connection.setDoOutput(true);
            connection.getOutputStream().write(5);
            connection.getOutputStream().close();
            connection.getInputStream().close();
            RecordedRequest request = server.takeRequest();
            assertContainsAll(cookieHandlerHeaders.keySet(), "Foo");
            assertContainsAll(cookieHandlerHeaders.keySet(), "Content-Type", "User-Agent", "Connection", "Host");
            TestCase.assertFalse(cookieHandlerHeaders.containsKey("Cookie"));
            /* The API specifies that calling getRequestProperties() on a connected instance should fail
            with an IllegalStateException, but the RI violates the spec and returns a valid map.
            http://www.mail-archive.com/net-dev@openjdk.java.net/msg01768.html
             */
            try {
                assertContainsAll(connection.getRequestProperties().keySet(), "Foo");
                assertContainsAll(connection.getRequestProperties().keySet(), "Content-Type", "Content-Length", "User-Agent", "Connection", "Host");
                assertContainsAll(connection.getRequestProperties().keySet(), "Cookie", "Cookie2");
                TestCase.assertFalse(connection.getRequestProperties().containsKey("Quux"));
            } catch (IllegalStateException expected) {
            }
            assertContainsAll(request.getHeaders(), "Foo: foo", "Cookie: Bar=bar", "Cookie2: Baz=baz");
            TestCase.assertFalse(request.getHeaders().contains("Quux: quux"));
        } finally {
            server.shutdown();
        }
    }

    public void testCookiesSentIgnoresCase() throws Exception {
        CookieHandler.setDefault(new CookieManager() {
            @Override
            public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) throws IOException {
                Map<String, List<String>> result = new HashMap<String, List<String>>();
                result.put("COOKIE", Collections.singletonList("Bar=bar"));
                result.put("cooKIE2", Collections.singletonList("Baz=baz"));
                return result;
            }
        });
        MockWebServer server = new MockWebServer();
        try {
            // RoboVM note: Modified to call server.shutdown() after test finishes.
            server.enqueue(new MockResponse());
            server.play();
            get(server, "/");
            RecordedRequest request = server.takeRequest();
            assertContainsAll(request.getHeaders(), "COOKIE: Bar=bar", "cooKIE2: Baz=baz");
            TestCase.assertFalse(request.getHeaders().contains("Quux: quux"));
        } finally {
            server.shutdown();
        }
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
        CookieStore cookieStore = new CookieManager().getCookieStore();
        HttpCookie cookieA = new HttpCookie("a", "android");
        cookieA.setDomain(".android.com");
        cookieA.setPath("/source");
        HttpCookie cookieB = new HttpCookie("b", "banana");
        cookieA.setDomain("code.google.com");
        cookieA.setPath("/p/android");
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
        CookieStore cookieStore = new CookieManager().getCookieStore();
        cookieStore.add(new URI("http://code.google.com/"), new HttpCookie("a", "android"));
        TestCase.assertTrue(cookieStore.removeAll());
        TestCase.assertEquals(Collections.<URI>emptyList(), cookieStore.getURIs());
        TestCase.assertEquals(Collections.<HttpCookie>emptyList(), cookieStore.getCookies());
        TestCase.assertFalse("Expected removeAll() to return false when the call doesn't mutate the store", cookieStore.removeAll());// RI6 fails this

    }

    public void testCookieStoreAddAcceptsConflictingUri() throws URISyntaxException {
        CookieStore cookieStore = new CookieManager().getCookieStore();
        HttpCookie cookieA = new HttpCookie("a", "android");
        cookieA.setDomain(".android.com");
        cookieA.setPath("/source/");
        cookieStore.add(new URI("http://google.com/source/"), cookieA);
        TestCase.assertEquals(Arrays.asList(cookieA), cookieStore.getCookies());
    }

    public void testCookieStoreRemoveRequiresUri() throws URISyntaxException {
        CookieStore cookieStore = new CookieManager().getCookieStore();
        HttpCookie cookieA = new HttpCookie("a", "android");
        cookieStore.add(new URI("http://android.com/source/"), cookieA);
        // RI6 fails this
        TestCase.assertFalse("Expected remove() to take the cookie URI into account.", cookieStore.remove(new URI("http://code.google.com/"), cookieA));
        TestCase.assertEquals(Arrays.asList(cookieA), cookieStore.getCookies());
    }

    public void testCookieStoreUriUsesHttpSchemeAlways() throws URISyntaxException {
        CookieStore cookieStore = new CookieManager().getCookieStore();
        cookieStore.add(new URI("https://a.com/"), new HttpCookie("a", "android"));
        TestCase.assertEquals(Arrays.asList(new URI("http://a.com")), cookieStore.getURIs());
    }

    public void testCookieStoreUriDropsUserInfo() throws URISyntaxException {
        CookieStore cookieStore = new CookieManager().getCookieStore();
        cookieStore.add(new URI("http://jesse:secret@a.com/"), new HttpCookie("a", "android"));
        TestCase.assertEquals(Arrays.asList(new URI("http://a.com")), cookieStore.getURIs());
    }

    public void testCookieStoreUriKeepsHost() throws URISyntaxException {
        CookieStore cookieStore = new CookieManager().getCookieStore();
        cookieStore.add(new URI("http://b.com/"), new HttpCookie("a", "android"));
        TestCase.assertEquals(Arrays.asList(new URI("http://b.com")), cookieStore.getURIs());
    }

    public void testCookieStoreUriDropsPort() throws URISyntaxException {
        CookieStore cookieStore = new CookieManager().getCookieStore();
        cookieStore.add(new URI("http://a.com:443/"), new HttpCookie("a", "android"));
        TestCase.assertEquals(Arrays.asList(new URI("http://a.com")), cookieStore.getURIs());
    }

    public void testCookieStoreUriDropsPath() throws URISyntaxException {
        CookieStore cookieStore = new CookieManager().getCookieStore();
        cookieStore.add(new URI("http://a.com/a/"), new HttpCookie("a", "android"));
        TestCase.assertEquals(Arrays.asList(new URI("http://a.com")), cookieStore.getURIs());
    }

    public void testCookieStoreUriDropsFragment() throws URISyntaxException {
        CookieStore cookieStore = new CookieManager().getCookieStore();
        cookieStore.add(new URI("http://a.com/a/foo#fragment"), new HttpCookie("a", "android"));
        TestCase.assertEquals(Arrays.asList(new URI("http://a.com")), cookieStore.getURIs());
    }

    public void testCookieStoreUriDropsQuery() throws URISyntaxException {
        CookieStore cookieStore = new CookieManager().getCookieStore();
        cookieStore.add(new URI("http://a.com/a/foo?query=value"), new HttpCookie("a", "android"));
        TestCase.assertEquals(Arrays.asList(new URI("http://a.com")), cookieStore.getURIs());
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
}

