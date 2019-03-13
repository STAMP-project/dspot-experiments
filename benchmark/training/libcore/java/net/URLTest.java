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


import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import junit.framework.TestCase;
import libcore.util.SerializationTester;


// Adding a new test? Consider adding an equivalent test to URITest.java
public final class URLTest extends TestCase {
    public void testUrlParts() throws Exception {
        URL url = new URL("http://username:password@host:8080/directory/file?query#ref");
        TestCase.assertEquals("http", url.getProtocol());
        TestCase.assertEquals("username:password@host:8080", url.getAuthority());
        TestCase.assertEquals("username:password", url.getUserInfo());
        TestCase.assertEquals("host", url.getHost());
        TestCase.assertEquals(8080, url.getPort());
        TestCase.assertEquals(80, url.getDefaultPort());
        TestCase.assertEquals("/directory/file?query", url.getFile());
        TestCase.assertEquals("/directory/file", url.getPath());
        TestCase.assertEquals("query", url.getQuery());
        TestCase.assertEquals("ref", url.getRef());
    }

    // http://code.google.com/p/android/issues/detail?id=12724
    public void testExplicitPort() throws Exception {
        URL url = new URL("http://www.google.com:80/example?language[id]=2");
        TestCase.assertEquals("www.google.com", url.getHost());
        TestCase.assertEquals(80, url.getPort());
    }

    /**
     * Android's URL.equals() works as if the network is down. This is different
     * from the RI, which does potentially slow and inconsistent DNS lookups in
     * URL.equals.
     */
    public void testEqualsDoesNotDoHostnameResolution() throws Exception {
        for (InetAddress inetAddress : InetAddress.getAllByName("localhost")) {
            String address = inetAddress.getHostAddress();
            if (inetAddress instanceof Inet6Address) {
                address = ("[" + address) + "]";
            }
            URL urlByHostName = new URL("http://localhost/foo?bar=baz#quux");
            URL urlByAddress = new URL((("http://" + address) + "/foo?bar=baz#quux"));
            TestCase.assertFalse(((("Expected " + urlByHostName) + " to not equal ") + urlByAddress), urlByHostName.equals(urlByAddress));// fails on RI, which does DNS

        }
    }

    public void testEqualsCaseMapping() throws Exception {
        TestCase.assertEquals(new URL("HTTP://localhost/foo?bar=baz#quux"), new URL("HTTP://localhost/foo?bar=baz#quux"));
        TestCase.assertTrue(new URL("http://localhost/foo?bar=baz#quux").equals(new URL("http://LOCALHOST/foo?bar=baz#quux")));
        TestCase.assertFalse(new URL("http://localhost/foo?bar=baz#quux").equals(new URL("http://localhost/FOO?bar=baz#quux")));
        TestCase.assertFalse(new URL("http://localhost/foo?bar=baz#quux").equals(new URL("http://localhost/foo?BAR=BAZ#quux")));
        TestCase.assertFalse(new URL("http://localhost/foo?bar=baz#quux").equals(new URL("http://localhost/foo?bar=baz#QUUX")));
    }

    public void testFileEqualsWithEmptyHost() throws Exception {
        TestCase.assertEquals(new URL("file", "", (-1), "/a/"), new URL("file:/a/"));
    }

    public void testHttpEqualsWithEmptyHost() throws Exception {
        TestCase.assertEquals(new URL("http", "", 80, "/a/"), new URL("http:/a/"));
        TestCase.assertFalse(new URL("http", "", 80, "/a/").equals(new URL("http://host/a/")));
    }

    public void testFileEquals() throws Exception {
        TestCase.assertEquals(new URL("file", null, (-1), "/a"), new URL("file", null, (-1), "/a"));
        TestCase.assertFalse(new URL("file", null, (-1), "/a").equals(new URL("file", null, (-1), "/A")));
    }

    public void testJarEquals() throws Exception {
        TestCase.assertEquals(new URL("jar", null, (-1), "/a!b"), new URL("jar", null, (-1), "/a!b"));
        TestCase.assertFalse(new URL("jar", null, (-1), "/a!b").equals(new URL("jar", null, (-1), "/a!B")));
        TestCase.assertFalse(new URL("jar", null, (-1), "/a!b").equals(new URL("jar", null, (-1), "/A!b")));
    }

    public void testUrlSerialization() throws Exception {
        String s = "aced00057372000c6a6176612e6e65742e55524c962537361afce472030006490004706f72744c0" + ((("009617574686f726974797400124c6a6176612f6c616e672f537472696e673b4c000466696c65710" + "07e00014c0004686f737471007e00014c000870726f746f636f6c71007e00014c000372656671007") + "e00017870ffffffff74000e757365723a7061737340686f73747400102f706174682f66696c653f7") + "175657279740004686f7374740004687474707400046861736878");
        URL url = new URL("http://user:pass@host/path/file?query#hash");
        new SerializationTester<URL>(url, s).test();
    }

    /**
     * The serialized form of a URL includes its hash code. But the hash code
     * is not documented. Check that we don't return a deserialized hash code
     * from a deserialized value.
     */
    public void testUrlSerializationWithHashCode() throws Exception {
        String s = "aced00057372000c6a6176612e6e65742e55524c962537361afce47203000749000868617368436" + (((("f6465490004706f72744c0009617574686f726974797400124c6a6176612f6c616e672f537472696" + "e673b4c000466696c6571007e00014c0004686f737471007e00014c000870726f746f636f6c71007") + "e00014c000372656671007e00017870cdf0efacffffffff74000e757365723a7061737340686f737") + "47400102f706174682f66696c653f7175657279740004686f7374740004687474707400046861736") + "878");
        final URL url = new URL("http://user:pass@host/path/file?query#hash");
        new SerializationTester<URL>(url, s) {
            @Override
            protected void verify(URL deserialized) {
                TestCase.assertEquals(url.hashCode(), deserialized.hashCode());
            }
        }.test();
    }

    public void testOnlySupportedProtocols() {
        try {
            new URL("abcd://host");
            TestCase.fail();
        } catch (MalformedURLException expected) {
        }
    }

    public void testOmittedHost() throws Exception {
        URL url = new URL("http:///path");
        TestCase.assertEquals("", url.getHost());
        TestCase.assertEquals("/path", url.getFile());
        TestCase.assertEquals("/path", url.getPath());
    }

    public void testNoHost() throws Exception {
        URL url = new URL("http:/path");
        TestCase.assertEquals("http", url.getProtocol());
        TestCase.assertEquals(null, url.getAuthority());
        TestCase.assertEquals(null, url.getUserInfo());
        TestCase.assertEquals("", url.getHost());
        TestCase.assertEquals((-1), url.getPort());
        TestCase.assertEquals(80, url.getDefaultPort());
        TestCase.assertEquals("/path", url.getFile());
        TestCase.assertEquals("/path", url.getPath());
        TestCase.assertEquals(null, url.getQuery());
        TestCase.assertEquals(null, url.getRef());
    }

    public void testNoPath() throws Exception {
        URL url = new URL("http://host");
        TestCase.assertEquals("host", url.getHost());
        TestCase.assertEquals("", url.getFile());
        TestCase.assertEquals("", url.getPath());
    }

    public void testEmptyHostAndNoPath() throws Exception {
        URL url = new URL("http://");
        TestCase.assertEquals("http", url.getProtocol());
        TestCase.assertEquals("", url.getAuthority());
        TestCase.assertEquals(null, url.getUserInfo());
        TestCase.assertEquals("", url.getHost());
        TestCase.assertEquals((-1), url.getPort());
        TestCase.assertEquals(80, url.getDefaultPort());
        TestCase.assertEquals("", url.getFile());
        TestCase.assertEquals("", url.getPath());
        TestCase.assertEquals(null, url.getQuery());
        TestCase.assertEquals(null, url.getRef());
    }

    public void testNoHostAndNoPath() throws Exception {
        URL url = new URL("http:");
        TestCase.assertEquals("http", url.getProtocol());
        TestCase.assertEquals(null, url.getAuthority());
        TestCase.assertEquals(null, url.getUserInfo());
        TestCase.assertEquals("", url.getHost());
        TestCase.assertEquals((-1), url.getPort());
        TestCase.assertEquals(80, url.getDefaultPort());
        TestCase.assertEquals("", url.getFile());
        TestCase.assertEquals("", url.getPath());
        TestCase.assertEquals(null, url.getQuery());
        TestCase.assertEquals(null, url.getRef());
    }

    public void testAtSignInUserInfo() throws Exception {
        try {
            new URL("http://user@userhost.com:password@host");
            TestCase.fail();
        } catch (MalformedURLException expected) {
        }
    }

    public void testUserNoPassword() throws Exception {
        URL url = new URL("http://user@host");
        TestCase.assertEquals("user@host", url.getAuthority());
        TestCase.assertEquals("user", url.getUserInfo());
        TestCase.assertEquals("host", url.getHost());
    }

    public void testUserNoPasswordExplicitPort() throws Exception {
        URL url = new URL("http://user@host:8080");
        TestCase.assertEquals("user@host:8080", url.getAuthority());
        TestCase.assertEquals("user", url.getUserInfo());
        TestCase.assertEquals("host", url.getHost());
        TestCase.assertEquals(8080, url.getPort());
    }

    public void testUserPasswordHostPort() throws Exception {
        URL url = new URL("http://user:password@host:8080");
        TestCase.assertEquals("user:password@host:8080", url.getAuthority());
        TestCase.assertEquals("user:password", url.getUserInfo());
        TestCase.assertEquals("host", url.getHost());
        TestCase.assertEquals(8080, url.getPort());
    }

    public void testUserPasswordEmptyHostPort() throws Exception {
        URL url = new URL("http://user:password@:8080");
        TestCase.assertEquals("user:password@:8080", url.getAuthority());
        TestCase.assertEquals("user:password", url.getUserInfo());
        TestCase.assertEquals("", url.getHost());
        TestCase.assertEquals(8080, url.getPort());
    }

    public void testUserPasswordEmptyHostEmptyPort() throws Exception {
        URL url = new URL("http://user:password@");
        TestCase.assertEquals("user:password@", url.getAuthority());
        TestCase.assertEquals("user:password", url.getUserInfo());
        TestCase.assertEquals("", url.getHost());
        TestCase.assertEquals((-1), url.getPort());
    }

    public void testPathOnly() throws Exception {
        URL url = new URL("http://host/path");
        TestCase.assertEquals("/path", url.getFile());
        TestCase.assertEquals("/path", url.getPath());
    }

    public void testQueryOnly() throws Exception {
        URL url = new URL("http://host?query");
        TestCase.assertEquals("?query", url.getFile());
        TestCase.assertEquals("", url.getPath());
        TestCase.assertEquals("query", url.getQuery());
    }

    public void testFragmentOnly() throws Exception {
        URL url = new URL("http://host#fragment");
        TestCase.assertEquals("", url.getFile());
        TestCase.assertEquals("", url.getPath());
        TestCase.assertEquals("fragment", url.getRef());
    }

    public void testAtSignInPath() throws Exception {
        URL url = new URL("http://host/file@foo");
        TestCase.assertEquals("/file@foo", url.getFile());
        TestCase.assertEquals("/file@foo", url.getPath());
        TestCase.assertEquals(null, url.getUserInfo());
    }

    public void testColonInPath() throws Exception {
        URL url = new URL("http://host/file:colon");
        TestCase.assertEquals("/file:colon", url.getFile());
        TestCase.assertEquals("/file:colon", url.getPath());
    }

    public void testSlashInQuery() throws Exception {
        URL url = new URL("http://host/file?query/path");
        TestCase.assertEquals("/file?query/path", url.getFile());
        TestCase.assertEquals("/file", url.getPath());
        TestCase.assertEquals("query/path", url.getQuery());
    }

    public void testQuestionMarkInQuery() throws Exception {
        URL url = new URL("http://host/file?query?another");
        TestCase.assertEquals("/file?query?another", url.getFile());
        TestCase.assertEquals("/file", url.getPath());
        TestCase.assertEquals("query?another", url.getQuery());
    }

    public void testAtSignInQuery() throws Exception {
        URL url = new URL("http://host/file?query@at");
        TestCase.assertEquals("/file?query@at", url.getFile());
        TestCase.assertEquals("/file", url.getPath());
        TestCase.assertEquals("query@at", url.getQuery());
    }

    public void testColonInQuery() throws Exception {
        URL url = new URL("http://host/file?query:colon");
        TestCase.assertEquals("/file?query:colon", url.getFile());
        TestCase.assertEquals("/file", url.getPath());
        TestCase.assertEquals("query:colon", url.getQuery());
    }

    public void testQuestionMarkInFragment() throws Exception {
        URL url = new URL("http://host/file#fragment?query");
        TestCase.assertEquals("/file", url.getFile());
        TestCase.assertEquals("/file", url.getPath());
        TestCase.assertEquals(null, url.getQuery());
        TestCase.assertEquals("fragment?query", url.getRef());
    }

    public void testColonInFragment() throws Exception {
        URL url = new URL("http://host/file#fragment:80");
        TestCase.assertEquals("/file", url.getFile());
        TestCase.assertEquals("/file", url.getPath());
        TestCase.assertEquals((-1), url.getPort());
        TestCase.assertEquals("fragment:80", url.getRef());
    }

    public void testSlashInFragment() throws Exception {
        URL url = new URL("http://host/file#fragment/path");
        TestCase.assertEquals("/file", url.getFile());
        TestCase.assertEquals("/file", url.getPath());
        TestCase.assertEquals("fragment/path", url.getRef());
    }

    public void testSlashInFragmentCombiningConstructor() throws Exception {
        URL url = new URL("http", "host", "/file#fragment/path");
        TestCase.assertEquals("/file", url.getFile());
        TestCase.assertEquals("/file", url.getPath());
        TestCase.assertEquals("fragment/path", url.getRef());
    }

    public void testHashInFragment() throws Exception {
        URL url = new URL("http://host/file#fragment#another");
        TestCase.assertEquals("/file", url.getFile());
        TestCase.assertEquals("/file", url.getPath());
        TestCase.assertEquals("fragment#another", url.getRef());
    }

    public void testEmptyPort() throws Exception {
        URL url = new URL("http://host:/");
        TestCase.assertEquals((-1), url.getPort());
    }

    public void testNonNumericPort() throws Exception {
        try {
            new URL("http://host:x/");
            TestCase.fail();
        } catch (MalformedURLException expected) {
        }
    }

    public void testNegativePort() throws Exception {
        try {
            new URL("http://host:-2/");
            TestCase.fail();
        } catch (MalformedURLException expected) {
        }
    }

    public void testNegativePortEqualsPlaceholder() throws Exception {
        try {
            new URL("http://host:-1/");
            TestCase.fail();// RI fails this

        } catch (MalformedURLException expected) {
        }
    }

    public void testRelativePathOnQuery() throws Exception {
        URL base = new URL("http://host/file?query/x");
        URL url = new URL(base, "another");
        TestCase.assertEquals("http://host/another", url.toString());
        TestCase.assertEquals("/another", url.getFile());
        TestCase.assertEquals("/another", url.getPath());
        TestCase.assertEquals(null, url.getQuery());
        TestCase.assertEquals(null, url.getRef());
    }

    public void testRelativeFragmentOnQuery() throws Exception {
        URL base = new URL("http://host/file?query/x#fragment");
        URL url = new URL(base, "#another");
        TestCase.assertEquals("http://host/file?query/x#another", url.toString());
        TestCase.assertEquals("/file?query/x", url.getFile());
        TestCase.assertEquals("/file", url.getPath());
        TestCase.assertEquals("query/x", url.getQuery());
        TestCase.assertEquals("another", url.getRef());
    }

    public void testPathContainsRelativeParts() throws Exception {
        URL url = new URL("http://host/a/b/../c");
        TestCase.assertEquals("http://host/a/c", url.toString());// RI doesn't canonicalize

    }

    public void testRelativePathAndFragment() throws Exception {
        URL base = new URL("http://host/file");
        TestCase.assertEquals("http://host/another#fragment", new URL(base, "another#fragment").toString());
    }

    public void testRelativeParentDirectory() throws Exception {
        URL base = new URL("http://host/a/b/c");
        TestCase.assertEquals("http://host/a/d", new URL(base, "../d").toString());
    }

    public void testRelativeChildDirectory() throws Exception {
        URL base = new URL("http://host/a/b/c");
        TestCase.assertEquals("http://host/a/b/d/e", new URL(base, "d/e").toString());
    }

    public void testRelativeRootDirectory() throws Exception {
        URL base = new URL("http://host/a/b/c");
        TestCase.assertEquals("http://host/d", new URL(base, "/d").toString());
    }

    public void testRelativeFullUrl() throws Exception {
        URL base = new URL("http://host/a/b/c");
        TestCase.assertEquals("http://host2/d/e", new URL(base, "http://host2/d/e").toString());
        TestCase.assertEquals("https://host2/d/e", new URL(base, "https://host2/d/e").toString());
    }

    public void testRelativeDifferentScheme() throws Exception {
        URL base = new URL("http://host/a/b/c");
        TestCase.assertEquals("https://host2/d/e", new URL(base, "https://host2/d/e").toString());
    }

    public void testRelativeDifferentAuthority() throws Exception {
        URL base = new URL("http://host/a/b/c");
        TestCase.assertEquals("http://another/d/e", new URL(base, "//another/d/e").toString());
    }

    public void testRelativeWithScheme() throws Exception {
        URL base = new URL("http://host/a/b/c");
        TestCase.assertEquals("http://host/a/b/c", new URL(base, "http:").toString());
        TestCase.assertEquals("http://host/", new URL(base, "http:/").toString());
    }

    public void testMalformedUrlsRefusedByFirefoxAndChrome() throws Exception {
        URL base = new URL("http://host/a/b/c");
        TestCase.assertEquals("http://", new URL(base, "http://").toString());// fails on RI; path retained

        TestCase.assertEquals("http://", new URL(base, "//").toString());// fails on RI

        TestCase.assertEquals("https:", new URL(base, "https:").toString());
        TestCase.assertEquals("https:/", new URL(base, "https:/").toString());
        TestCase.assertEquals("https://", new URL(base, "https://").toString());
    }

    public void testRfc1808NormalExamples() throws Exception {
        URL base = new URL("http://a/b/c/d;p?q");
        TestCase.assertEquals("https:h", new URL(base, "https:h").toString());
        TestCase.assertEquals("http://a/b/c/g", new URL(base, "g").toString());
        TestCase.assertEquals("http://a/b/c/g", new URL(base, "./g").toString());
        TestCase.assertEquals("http://a/b/c/g/", new URL(base, "g/").toString());
        TestCase.assertEquals("http://a/g", new URL(base, "/g").toString());
        TestCase.assertEquals("http://g", new URL(base, "//g").toString());
        TestCase.assertEquals("http://a/b/c/d;p?y", new URL(base, "?y").toString());// RI fails; file lost

        TestCase.assertEquals("http://a/b/c/g?y", new URL(base, "g?y").toString());
        TestCase.assertEquals("http://a/b/c/d;p?q#s", new URL(base, "#s").toString());
        TestCase.assertEquals("http://a/b/c/g#s", new URL(base, "g#s").toString());
        TestCase.assertEquals("http://a/b/c/g?y#s", new URL(base, "g?y#s").toString());
        TestCase.assertEquals("http://a/b/c/;x", new URL(base, ";x").toString());
        TestCase.assertEquals("http://a/b/c/g;x", new URL(base, "g;x").toString());
        TestCase.assertEquals("http://a/b/c/g;x?y#s", new URL(base, "g;x?y#s").toString());
        TestCase.assertEquals("http://a/b/c/d;p?q", new URL(base, "").toString());
        TestCase.assertEquals("http://a/b/c/", new URL(base, ".").toString());
        TestCase.assertEquals("http://a/b/c/", new URL(base, "./").toString());
        TestCase.assertEquals("http://a/b/", new URL(base, "..").toString());
        TestCase.assertEquals("http://a/b/", new URL(base, "../").toString());
        TestCase.assertEquals("http://a/b/g", new URL(base, "../g").toString());
        TestCase.assertEquals("http://a/", new URL(base, "../..").toString());
        TestCase.assertEquals("http://a/", new URL(base, "../../").toString());
        TestCase.assertEquals("http://a/g", new URL(base, "../../g").toString());
    }

    public void testRfc1808AbnormalExampleTooManyDotDotSequences() throws Exception {
        URL base = new URL("http://a/b/c/d;p?q");
        TestCase.assertEquals("http://a/g", new URL(base, "../../../g").toString());// RI doesn't normalize

        TestCase.assertEquals("http://a/g", new URL(base, "../../../../g").toString());
    }

    public void testRfc1808AbnormalExampleRemoveDotSegments() throws Exception {
        URL base = new URL("http://a/b/c/d;p?q");
        TestCase.assertEquals("http://a/g", new URL(base, "/./g").toString());// RI doesn't normalize

        TestCase.assertEquals("http://a/g", new URL(base, "/../g").toString());// RI doesn't normalize

        TestCase.assertEquals("http://a/b/c/g.", new URL(base, "g.").toString());
        TestCase.assertEquals("http://a/b/c/.g", new URL(base, ".g").toString());
        TestCase.assertEquals("http://a/b/c/g..", new URL(base, "g..").toString());
        TestCase.assertEquals("http://a/b/c/..g", new URL(base, "..g").toString());
    }

    public void testRfc1808AbnormalExampleNonsensicalDots() throws Exception {
        URL base = new URL("http://a/b/c/d;p?q");
        TestCase.assertEquals("http://a/b/g", new URL(base, "./../g").toString());
        TestCase.assertEquals("http://a/b/c/g/", new URL(base, "./g/.").toString());
        TestCase.assertEquals("http://a/b/c/g/h", new URL(base, "g/./h").toString());
        TestCase.assertEquals("http://a/b/c/h", new URL(base, "g/../h").toString());
        TestCase.assertEquals("http://a/b/c/g;x=1/y", new URL(base, "g;x=1/./y").toString());
        TestCase.assertEquals("http://a/b/c/y", new URL(base, "g;x=1/../y").toString());
    }

    public void testRfc1808AbnormalExampleRelativeScheme() throws Exception {
        URL base = new URL("http://a/b/c/d;p?q");
        // this result is permitted; strict parsers prefer "http:g"
        TestCase.assertEquals("http://a/b/c/g", new URL(base, "http:g").toString());
    }

    public void testRfc1808AbnormalExampleQueryOrFragmentDots() throws Exception {
        URL base = new URL("http://a/b/c/d;p?q");
        TestCase.assertEquals("http://a/b/c/g?y/./x", new URL(base, "g?y/./x").toString());
        TestCase.assertEquals("http://a/b/c/g?y/../x", new URL(base, "g?y/../x").toString());
        TestCase.assertEquals("http://a/b/c/g#s/./x", new URL(base, "g#s/./x").toString());
        TestCase.assertEquals("http://a/b/c/g#s/../x", new URL(base, "g#s/../x").toString());
    }

    public void testSquareBracketsInUserInfo() throws Exception {
        URL url = new URL("http://user:[::1]@host");
        TestCase.assertEquals("user:[::1]", url.getUserInfo());
        TestCase.assertEquals("host", url.getHost());
    }

    public void testComposeUrl() throws Exception {
        URL url = new URL("http", "host", "a");
        TestCase.assertEquals("http", url.getProtocol());
        TestCase.assertEquals("host", url.getAuthority());
        TestCase.assertEquals("host", url.getHost());
        TestCase.assertEquals("/a", url.getFile());// RI fails; doesn't insert '/' separator

        TestCase.assertEquals("http://host/a", url.toString());// fails on RI

    }

    public void testComposeUrlWithNullHost() throws Exception {
        URL url = new URL("http", null, "a");
        TestCase.assertEquals("http", url.getProtocol());
        TestCase.assertEquals(null, url.getAuthority());
        TestCase.assertEquals(null, url.getHost());
        TestCase.assertEquals("a", url.getFile());
        TestCase.assertEquals("http:a", url.toString());// fails on RI

    }

    public void testFileUrlExtraLeadingSlashes() throws Exception {
        URL url = new URL("file:////foo");
        TestCase.assertEquals("", url.getAuthority());// RI returns null

        TestCase.assertEquals("//foo", url.getPath());
        TestCase.assertEquals("file:////foo", url.toString());
    }

    public void testFileUrlWithAuthority() throws Exception {
        URL url = new URL("file://x/foo");
        TestCase.assertEquals("x", url.getAuthority());
        TestCase.assertEquals("/foo", url.getPath());
        TestCase.assertEquals("file://x/foo", url.toString());
    }

    /**
     * The RI is not self-consistent on missing authorities, returning either
     * null or the empty string depending on the number of slashes in the path.
     * We always treat '//' as the beginning of an authority.
     */
    public void testEmptyAuthority() throws Exception {
        URL url = new URL("http:///foo");
        TestCase.assertEquals("", url.getAuthority());
        TestCase.assertEquals("/foo", url.getPath());
        TestCase.assertEquals("http:///foo", url.toString());// RI drops '//'

    }

    public void testHttpUrlExtraLeadingSlashes() throws Exception {
        URL url = new URL("http:////foo");
        TestCase.assertEquals("", url.getAuthority());// RI returns null

        TestCase.assertEquals("//foo", url.getPath());
        TestCase.assertEquals("http:////foo", url.toString());
    }

    public void testFileUrlRelativePath() throws Exception {
        URL base = new URL("file:a/b/c");
        TestCase.assertEquals("file:a/b/d", new URL(base, "d").toString());
    }

    public void testFileUrlDottedPath() throws Exception {
        URL url = new URL("file:../a/b");
        TestCase.assertEquals("../a/b", url.getPath());
        TestCase.assertEquals("file:../a/b", url.toString());
    }

    public void testParsingDotAsHostname() throws Exception {
        URL url = new URL("http://./");
        TestCase.assertEquals(".", url.getAuthority());
        TestCase.assertEquals(".", url.getHost());
    }

    public void testSquareBracketsWithIPv4() throws Exception {
        try {
            new URL("http://[192.168.0.1]/");
            TestCase.fail();
        } catch (MalformedURLException expected) {
        }
        URL url = new URL("http", "[192.168.0.1]", "/");
        TestCase.assertEquals("[192.168.0.1]", url.getHost());
    }

    public void testSquareBracketsWithHostname() throws Exception {
        try {
            new URL("http://[www.android.com]/");
            TestCase.fail();
        } catch (MalformedURLException expected) {
        }
        URL url = new URL("http", "[www.android.com]", "/");
        TestCase.assertEquals("[www.android.com]", url.getHost());
    }

    public void testIPv6WithoutSquareBrackets() throws Exception {
        try {
            new URL("http://fe80::1234/");
            TestCase.fail();
        } catch (MalformedURLException expected) {
        }
        URL url = new URL("http", "fe80::1234", "/");
        TestCase.assertEquals("[fe80::1234]", url.getHost());
    }

    public void testIpv6WithSquareBrackets() throws Exception {
        URL url = new URL("http://[::1]:2/");
        TestCase.assertEquals("[::1]", url.getHost());
        TestCase.assertEquals(2, url.getPort());
    }

    public void testEqualityWithNoPath() throws Exception {
        TestCase.assertFalse(new URL("http://android.com").equals(new URL("http://android.com/")));
    }

    public void testUrlDoesNotEncodeParts() throws Exception {
        URL url = new URL("http", "host", 80, "/doc|search?q=green robots#over 6\"");
        TestCase.assertEquals("http", url.getProtocol());
        TestCase.assertEquals("host:80", url.getAuthority());
        TestCase.assertEquals("/doc|search", url.getPath());
        TestCase.assertEquals("q=green robots", url.getQuery());
        TestCase.assertEquals("over 6\"", url.getRef());
        TestCase.assertEquals("http://host:80/doc|search?q=green robots#over 6\"", url.toString());
    }

    public void testSchemeCaseIsCanonicalized() throws Exception {
        URL url = new URL("HTTP://host/path");
        TestCase.assertEquals("http", url.getProtocol());
    }

    public void testEmptyAuthorityWithPath() throws Exception {
        URL url = new URL("http:///path");
        TestCase.assertEquals("", url.getAuthority());
        TestCase.assertEquals("/path", url.getPath());
    }

    public void testEmptyAuthorityWithQuery() throws Exception {
        URL url = new URL("http://?query");
        TestCase.assertEquals("", url.getAuthority());
        TestCase.assertEquals("", url.getPath());
        TestCase.assertEquals("query", url.getQuery());
    }

    public void testEmptyAuthorityWithFragment() throws Exception {
        URL url = new URL("http://#fragment");
        TestCase.assertEquals("", url.getAuthority());
        TestCase.assertEquals("", url.getPath());
        TestCase.assertEquals("fragment", url.getRef());
    }

    public void testCombiningConstructorsMakeRelativePathsAbsolute() throws Exception {
        TestCase.assertEquals("/relative", new URL("http", "host", "relative").getPath());
        TestCase.assertEquals("/relative", new URL("http", "host", (-1), "relative").getPath());
        TestCase.assertEquals("/relative", new URL("http", "host", (-1), "relative", null).getPath());
    }

    public void testCombiningConstructorsDoNotMakeEmptyPathsAbsolute() throws Exception {
        TestCase.assertEquals("", new URL("http", "host", "").getPath());
        TestCase.assertEquals("", new URL("http", "host", (-1), "").getPath());
        TestCase.assertEquals("", new URL("http", "host", (-1), "", null).getPath());
    }

    public void testPartContainsSpace() throws Exception {
        try {
            new URL("ht tp://host/");
            TestCase.fail();
        } catch (MalformedURLException expected) {
        }
        TestCase.assertEquals("user name", new URL("http://user name@host/").getUserInfo());
        TestCase.assertEquals("ho st", new URL("http://ho st/").getHost());
        try {
            new URL("http://host:80 80/");
            TestCase.fail();
        } catch (MalformedURLException expected) {
        }
        TestCase.assertEquals("/fi le", new URL("http://host/fi le").getFile());
        TestCase.assertEquals("que ry", new URL("http://host/file?que ry").getQuery());
        TestCase.assertEquals("re f", new URL("http://host/file?query#re f").getRef());
    }

    // http://code.google.com/p/android/issues/detail?id=37577
    public void testUnderscore() throws Exception {
        URL url = new URL("http://a_b.c.d.net/");
        TestCase.assertEquals("a_b.c.d.net", url.getAuthority());
        // The RFC's don't permit underscores in hostnames, but URL accepts them (unlike URI).
        TestCase.assertEquals("a_b.c.d.net", url.getHost());
    }

    // http://b/7369778
    public void testToURILeniantThrowsURISyntaxExceptionWithPartialTrailingEscape() throws Exception {
        // make sure if there a partial trailing escape that we don't throw the wrong exception
        URL[] badUrls = new URL[]{ new URL("http://example.com/?foo=%%bar"), new URL("http://example.com/?foo=%%bar%"), new URL("http://example.com/?foo=%%bar%2"), new URL("http://example.com/?foo=%%bar%%"), new URL("http://example.com/?foo=%%bar%%%"), new URL("http://example.com/?foo=%%bar%%%%") };
        for (URL badUrl : badUrls) {
            try {
                toURILenient();
                TestCase.fail();
            } catch (URISyntaxException expected) {
            }
        }
        // make sure we properly handle an normal escape at the end of a string
        String[] goodUrls = new String[]{ "http://example.com/?foo=bar", "http://example.com/?foo=bar%20" };
        for (String goodUrl : goodUrls) {
            TestCase.assertEquals(new URI(goodUrl), toURILenient());
        }
    }
}

