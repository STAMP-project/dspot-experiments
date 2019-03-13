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


import java.net.URI;
import java.net.URISyntaxException;
import junit.framework.TestCase;
import libcore.util.SerializationTester;


// Adding a new test? Consider adding an equivalent test to URLTest.java
public final class URITest extends TestCase {
    public void testUriParts() throws Exception {
        URI uri = new URI("http://username:password@host:8080/directory/file?query#ref");
        TestCase.assertEquals("http", uri.getScheme());
        TestCase.assertEquals("username:password@host:8080", uri.getAuthority());
        TestCase.assertEquals("username:password@host:8080", uri.getRawAuthority());
        TestCase.assertEquals("username:password", uri.getUserInfo());
        TestCase.assertEquals("username:password", uri.getRawUserInfo());
        TestCase.assertEquals("host", uri.getHost());
        TestCase.assertEquals(8080, uri.getPort());
        TestCase.assertEquals("/directory/file", uri.getPath());
        TestCase.assertEquals("/directory/file", uri.getRawPath());
        TestCase.assertEquals("query", uri.getQuery());
        TestCase.assertEquals("query", uri.getRawQuery());
        TestCase.assertEquals("ref", uri.getFragment());
        TestCase.assertEquals("ref", uri.getRawFragment());
        TestCase.assertEquals("//username:password@host:8080/directory/file?query", uri.getSchemeSpecificPart());
        TestCase.assertEquals("//username:password@host:8080/directory/file?query", uri.getRawSchemeSpecificPart());
    }

    public void testEqualsCaseMapping() throws Exception {
        TestCase.assertEquals(new URI("HTTP://localhost/foo?bar=baz#quux"), new URI("HTTP://localhost/foo?bar=baz#quux"));
        TestCase.assertEquals(new URI("http://localhost/foo?bar=baz#quux"), new URI("http://LOCALHOST/foo?bar=baz#quux"));
        TestCase.assertFalse(new URI("http://localhost/foo?bar=baz#quux").equals(new URI("http://localhost/FOO?bar=baz#quux")));
        TestCase.assertFalse(new URI("http://localhost/foo?bar=baz#quux").equals(new URI("http://localhost/foo?BAR=BAZ#quux")));
        TestCase.assertFalse(new URI("http://localhost/foo?bar=baz#quux").equals(new URI("http://localhost/foo?bar=baz#QUUX")));
    }

    public void testFileEqualsWithEmptyHost() throws Exception {
        TestCase.assertEquals(new URI("file", "", "/a/", null), new URI("file:/a/"));
        TestCase.assertEquals(new URI("file", null, "/a/", null), new URI("file:/a/"));
    }

    public void testUriSerialization() throws Exception {
        String s = "aced00057372000c6a6176612e6e65742e555249ac01782e439e49ab0300014c0006737472696e6" + ("77400124c6a6176612f6c616e672f537472696e673b787074002a687474703a2f2f757365723a706" + "1737340686f73742f706174682f66696c653f7175657279236861736878");
        URI uri = new URI("http://user:pass@host/path/file?query#hash");
        new SerializationTester<URI>(uri, s).test();
    }

    public void testEmptyHost() throws Exception {
        URI uri = new URI("http:///path");
        TestCase.assertEquals(null, uri.getHost());
        TestCase.assertEquals("/path", uri.getPath());
    }

    public void testNoHost() throws Exception {
        URI uri = new URI("http:/path");
        TestCase.assertEquals(null, uri.getHost());
        TestCase.assertEquals("/path", uri.getPath());
    }

    public void testNoPath() throws Exception {
        URI uri = new URI("http://host");
        TestCase.assertEquals("host", uri.getHost());
        TestCase.assertEquals("", uri.getPath());
    }

    public void testEmptyHostAndNoPath() throws Exception {
        try {
            new URI("http://");
            TestCase.fail();
        } catch (URISyntaxException expected) {
        }
    }

    public void testNoHostAndNoPath() throws Exception {
        try {
            new URI("http:");
            TestCase.fail();
        } catch (URISyntaxException expected) {
        }
    }

    public void testAtSignInUserInfo() throws Exception {
        URI uri = new URI("http://user@userhost.com:password@host");
        TestCase.assertEquals("user@userhost.com:password@host", uri.getAuthority());
        TestCase.assertEquals(null, uri.getUserInfo());
        TestCase.assertEquals(null, uri.getHost());
    }

    public void testUserNoPassword() throws Exception {
        URI uri = new URI("http://user@host");
        TestCase.assertEquals("user@host", uri.getAuthority());
        TestCase.assertEquals("user", uri.getUserInfo());
        TestCase.assertEquals("host", uri.getHost());
    }

    public void testUserNoPasswordExplicitPort() throws Exception {
        URI uri = new URI("http://user@host:8080");
        TestCase.assertEquals("user@host:8080", uri.getAuthority());
        TestCase.assertEquals("user", uri.getUserInfo());
        TestCase.assertEquals("host", uri.getHost());
        TestCase.assertEquals(8080, uri.getPort());
    }

    public void testUserPasswordHostPort() throws Exception {
        URI uri = new URI("http://user:password@host:8080");
        TestCase.assertEquals("user:password@host:8080", uri.getAuthority());
        TestCase.assertEquals("user:password", uri.getUserInfo());
        TestCase.assertEquals("host", uri.getHost());
        TestCase.assertEquals(8080, uri.getPort());
    }

    public void testUserPasswordEmptyHostPort() throws Exception {
        URI uri = new URI("http://user:password@:8080");
        TestCase.assertEquals("user:password@:8080", uri.getAuthority());
        // from RI. this is curious
        TestCase.assertEquals(null, uri.getUserInfo());
        TestCase.assertEquals(null, uri.getHost());
        TestCase.assertEquals((-1), uri.getPort());
    }

    public void testUserPasswordEmptyHostEmptyPort() throws Exception {
        URI uri = new URI("http://user:password@:");
        TestCase.assertEquals("user:password@:", uri.getAuthority());
        // from RI. this is curious
        TestCase.assertEquals(null, uri.getUserInfo());
        TestCase.assertEquals(null, uri.getHost());
        TestCase.assertEquals((-1), uri.getPort());
    }

    public void testPathOnly() throws Exception {
        URI uri = new URI("http://host/path");
        TestCase.assertEquals("host", uri.getHost());
        TestCase.assertEquals("/path", uri.getPath());
    }

    public void testQueryOnly() throws Exception {
        URI uri = new URI("http://host?query");
        TestCase.assertEquals("host", uri.getHost());
        TestCase.assertEquals("", uri.getPath());
        TestCase.assertEquals("query", uri.getQuery());
    }

    public void testFragmentOnly() throws Exception {
        URI uri = new URI("http://host#fragment");
        TestCase.assertEquals("host", uri.getHost());
        TestCase.assertEquals("", uri.getPath());
        TestCase.assertEquals(null, uri.getQuery());
        TestCase.assertEquals("fragment", uri.getFragment());
    }

    public void testAtSignInPath() throws Exception {
        URI uri = new URI("http://host/file@foo");
        TestCase.assertEquals("/file@foo", uri.getPath());
        TestCase.assertEquals(null, uri.getUserInfo());
    }

    public void testColonInPath() throws Exception {
        URI uri = new URI("http://host/file:colon");
        TestCase.assertEquals("/file:colon", uri.getPath());
    }

    public void testSlashInQuery() throws Exception {
        URI uri = new URI("http://host/file?query/path");
        TestCase.assertEquals("/file", uri.getPath());
        TestCase.assertEquals("query/path", uri.getQuery());
    }

    public void testQuestionMarkInQuery() throws Exception {
        URI uri = new URI("http://host/file?query?another");
        TestCase.assertEquals("/file", uri.getPath());
        TestCase.assertEquals("query?another", uri.getQuery());
    }

    public void testAtSignInQuery() throws Exception {
        URI uri = new URI("http://host/file?query@at");
        TestCase.assertEquals("/file", uri.getPath());
        TestCase.assertEquals("query@at", uri.getQuery());
    }

    public void testColonInQuery() throws Exception {
        URI uri = new URI("http://host/file?query:colon");
        TestCase.assertEquals("/file", uri.getPath());
        TestCase.assertEquals("query:colon", uri.getQuery());
    }

    public void testQuestionMarkInFragment() throws Exception {
        URI uri = new URI("http://host/file#fragment?query");
        TestCase.assertEquals("/file", uri.getPath());
        TestCase.assertEquals(null, uri.getQuery());
        TestCase.assertEquals("fragment?query", uri.getFragment());
    }

    public void testColonInFragment() throws Exception {
        URI uri = new URI("http://host/file#fragment:80");
        TestCase.assertEquals("/file", uri.getPath());
        TestCase.assertEquals((-1), uri.getPort());
        TestCase.assertEquals("fragment:80", uri.getFragment());
    }

    public void testSlashInFragment() throws Exception {
        URI uri = new URI("http://host/file#fragment/path");
        TestCase.assertEquals("/file", uri.getPath());
        TestCase.assertEquals("fragment/path", uri.getFragment());
    }

    public void testHashInFragment() throws Exception {
        try {
            // This is not consistent with java.net.URL
            new URI("http://host/file#fragment#another");
            TestCase.fail();
        } catch (URISyntaxException expected) {
        }
    }

    public void testEmptyPort() throws Exception {
        URI uri = new URI("http://host:/");
        TestCase.assertEquals((-1), uri.getPort());
    }

    public void testNonNumericPort() throws Exception {
        URI uri = new URI("http://host:x/");
        // From the RI. This is curious
        TestCase.assertEquals(null, uri.getHost());
        TestCase.assertEquals((-1), uri.getPort());
    }

    public void testNegativePort() throws Exception {
        URI uri = new URI("http://host:-2/");
        // From the RI. This is curious
        TestCase.assertEquals(null, uri.getHost());
        TestCase.assertEquals((-1), uri.getPort());
    }

    public void testNegativePortEqualsPlaceholder() throws Exception {
        URI uri = new URI("http://host:-1/");
        // From the RI. This is curious
        TestCase.assertEquals(null, uri.getHost());
        TestCase.assertEquals((-1), uri.getPort());
    }

    public void testRelativePathOnQuery() throws Exception {
        URI base = new URI("http://host/file?query/x");
        URI uri = base.resolve("another");
        TestCase.assertEquals("http://host/another", uri.toString());
        TestCase.assertEquals("/another", uri.getPath());
        TestCase.assertEquals(null, uri.getQuery());
        TestCase.assertEquals(null, uri.getFragment());
    }

    public void testRelativeFragmentOnQuery() throws Exception {
        URI base = new URI("http://host/file?query/x#fragment");
        URI uri = base.resolve("#another");
        TestCase.assertEquals("http://host/file?query/x#another", uri.toString());
        TestCase.assertEquals("/file", uri.getPath());
        TestCase.assertEquals("query/x", uri.getQuery());
        TestCase.assertEquals("another", uri.getFragment());
    }

    public void testPathContainsRelativeParts() throws Exception {
        URI uri = new URI("http://host/a/b/../c");
        // assertEquals("http://host/a/c", uri.toString()); // RI doesn't canonicalize
    }

    public void testRelativePathAndFragment() throws Exception {
        URI base = new URI("http://host/file");
        TestCase.assertEquals("http://host/another#fragment", base.resolve("another#fragment").toString());
    }

    public void testRelativeParentDirectory() throws Exception {
        URI base = new URI("http://host/a/b/c");
        TestCase.assertEquals("http://host/a/d", base.resolve("../d").toString());
    }

    public void testRelativeChildDirectory() throws Exception {
        URI base = new URI("http://host/a/b/c");
        TestCase.assertEquals("http://host/a/b/d/e", base.resolve("d/e").toString());
    }

    public void testRelativeRootDirectory() throws Exception {
        URI base = new URI("http://host/a/b/c");
        TestCase.assertEquals("http://host/d", base.resolve("/d").toString());
    }

    public void testRelativeFullUrl() throws Exception {
        URI base = new URI("http://host/a/b/c");
        TestCase.assertEquals("http://host2/d/e", base.resolve("http://host2/d/e").toString());
        TestCase.assertEquals("https://host2/d/e", base.resolve("https://host2/d/e").toString());
    }

    public void testRelativeDifferentScheme() throws Exception {
        URI base = new URI("http://host/a/b/c");
        TestCase.assertEquals("https://host2/d/e", base.resolve("https://host2/d/e").toString());
    }

    public void testRelativeDifferentAuthority() throws Exception {
        URI base = new URI("http://host/a/b/c");
        TestCase.assertEquals("http://another/d/e", base.resolve("//another/d/e").toString());
    }

    public void testRelativeWithScheme() throws Exception {
        URI base = new URI("http://host/a/b/c");
        try {
            base.resolve("http:");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertEquals("http:/", base.resolve("http:/").toString());
    }

    public void testMalformedUrlsRefusedByFirefoxAndChrome() throws Exception {
        URI base = new URI("http://host/a/b/c");
        try {
            base.resolve("http://");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            base.resolve("//");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            base.resolve("https:");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertEquals("https:/", base.resolve("https:/").toString());
        try {
            base.resolve("https://");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testRfc1808NormalExamples() throws Exception {
        URI base = new URI("http://a/b/c/d;p?q");
        TestCase.assertEquals("https:h", base.resolve("https:h").toString());
        TestCase.assertEquals("http://a/b/c/g", base.resolve("g").toString());
        TestCase.assertEquals("http://a/b/c/g", base.resolve("./g").toString());
        TestCase.assertEquals("http://a/b/c/g/", base.resolve("g/").toString());
        TestCase.assertEquals("http://a/g", base.resolve("/g").toString());
        TestCase.assertEquals("http://g", base.resolve("//g").toString());
        TestCase.assertEquals("http://a/b/c/d;p?y", base.resolve("?y").toString());// RI fails; loses file

        TestCase.assertEquals("http://a/b/c/g?y", base.resolve("g?y").toString());
        TestCase.assertEquals("http://a/b/c/d;p?q#s", base.resolve("#s").toString());
        TestCase.assertEquals("http://a/b/c/g#s", base.resolve("g#s").toString());
        TestCase.assertEquals("http://a/b/c/g?y#s", base.resolve("g?y#s").toString());
        TestCase.assertEquals("http://a/b/c/;x", base.resolve(";x").toString());
        TestCase.assertEquals("http://a/b/c/g;x", base.resolve("g;x").toString());
        TestCase.assertEquals("http://a/b/c/g;x?y#s", base.resolve("g;x?y#s").toString());
        TestCase.assertEquals("http://a/b/c/d;p?q", base.resolve("").toString());// RI returns http://a/b/c/

        TestCase.assertEquals("http://a/b/c/", base.resolve(".").toString());
        TestCase.assertEquals("http://a/b/c/", base.resolve("./").toString());
        TestCase.assertEquals("http://a/b/", base.resolve("..").toString());
        TestCase.assertEquals("http://a/b/", base.resolve("../").toString());
        TestCase.assertEquals("http://a/b/g", base.resolve("../g").toString());
        TestCase.assertEquals("http://a/", base.resolve("../..").toString());
        TestCase.assertEquals("http://a/", base.resolve("../../").toString());
        TestCase.assertEquals("http://a/g", base.resolve("../../g").toString());
    }

    public void testRfc1808AbnormalExampleTooManyDotDotSequences() throws Exception {
        URI base = new URI("http://a/b/c/d;p?q");
        TestCase.assertEquals("http://a/g", base.resolve("../../../g").toString());// RI doesn't normalize

        TestCase.assertEquals("http://a/g", base.resolve("../../../../g").toString());// fails on RI

    }

    public void testRfc1808AbnormalExampleRemoveDotSegments() throws Exception {
        URI base = new URI("http://a/b/c/d;p?q");
        TestCase.assertEquals("http://a/g", base.resolve("/./g").toString());// RI doesn't normalize

        TestCase.assertEquals("http://a/g", base.resolve("/../g").toString());// fails on RI

        TestCase.assertEquals("http://a/b/c/g.", base.resolve("g.").toString());
        TestCase.assertEquals("http://a/b/c/.g", base.resolve(".g").toString());
        TestCase.assertEquals("http://a/b/c/g..", base.resolve("g..").toString());
        TestCase.assertEquals("http://a/b/c/..g", base.resolve("..g").toString());
    }

    public void testRfc1808AbnormalExampleNonsensicalDots() throws Exception {
        URI base = new URI("http://a/b/c/d;p?q");
        TestCase.assertEquals("http://a/b/g", base.resolve("./../g").toString());
        TestCase.assertEquals("http://a/b/c/g/", base.resolve("./g/.").toString());
        TestCase.assertEquals("http://a/b/c/g/h", base.resolve("g/./h").toString());
        TestCase.assertEquals("http://a/b/c/h", base.resolve("g/../h").toString());
        TestCase.assertEquals("http://a/b/c/g;x=1/y", base.resolve("g;x=1/./y").toString());
        TestCase.assertEquals("http://a/b/c/y", base.resolve("g;x=1/../y").toString());
    }

    public void testRfc1808AbnormalExampleRelativeScheme() throws Exception {
        URI base = new URI("http://a/b/c/d;p?q");
        URI uri = base.resolve("http:g");
        TestCase.assertEquals("http:g", uri.toString());// this is an opaque URI

        TestCase.assertEquals(true, uri.isOpaque());
        TestCase.assertEquals(true, uri.isAbsolute());
    }

    public void testRfc1808AbnormalExampleQueryOrFragmentDots() throws Exception {
        URI base = new URI("http://a/b/c/d;p?q");
        TestCase.assertEquals("http://a/b/c/g?y/./x", base.resolve("g?y/./x").toString());
        TestCase.assertEquals("http://a/b/c/g?y/../x", base.resolve("g?y/../x").toString());
        TestCase.assertEquals("http://a/b/c/g#s/./x", base.resolve("g#s/./x").toString());
        TestCase.assertEquals("http://a/b/c/g#s/../x", base.resolve("g#s/../x").toString());
    }

    public void testSquareBracketsInUserInfo() throws Exception {
        try {
            new URI("http://user:[::1]@host");
            TestCase.fail();
        } catch (URISyntaxException expected) {
        }
    }

    public void testFileUriExtraLeadingSlashes() throws Exception {
        URI uri = new URI("file:////foo");
        TestCase.assertEquals(null, uri.getAuthority());
        TestCase.assertEquals("//foo", uri.getPath());
        TestCase.assertEquals("file:////foo", uri.toString());
    }

    public void testFileUrlWithAuthority() throws Exception {
        URI uri = new URI("file://x/foo");
        TestCase.assertEquals("x", uri.getAuthority());
        TestCase.assertEquals("/foo", uri.getPath());
        TestCase.assertEquals("file://x/foo", uri.toString());
    }

    public void testEmptyAuthority() throws Exception {
        URI uri = new URI("http:///foo");
        TestCase.assertEquals(null, uri.getAuthority());
        TestCase.assertEquals("/foo", uri.getPath());
        TestCase.assertEquals("http:///foo", uri.toString());
    }

    public void testHttpUrlExtraLeadingSlashes() throws Exception {
        URI uri = new URI("http:////foo");
        TestCase.assertEquals(null, uri.getAuthority());
        TestCase.assertEquals("//foo", uri.getPath());
        TestCase.assertEquals("http:////foo", uri.toString());
    }

    public void testFileUrlRelativePath() throws Exception {
        URI base = new URI("file:/a/b/c");
        TestCase.assertEquals("file:/a/b/d", base.resolve("d").toString());
    }

    public void testFileUrlDottedPath() throws Exception {
        URI url = new URI("file:../a/b");
        TestCase.assertTrue(url.isOpaque());
        TestCase.assertNull(url.getPath());
    }

    /**
     * Regression test for http://b/issue?id=2604061
     */
    public void testParsingDotAsHostname() throws Exception {
        TestCase.assertEquals(null, new URI("http://./").getHost());
    }

    public void testSquareBracketsWithIPv4() throws Exception {
        try {
            new URI("http://[192.168.0.1]/");
            TestCase.fail();
        } catch (URISyntaxException e) {
        }
    }

    public void testSquareBracketsWithHostname() throws Exception {
        try {
            new URI("http://[google.com]/");
            TestCase.fail();
        } catch (URISyntaxException e) {
        }
    }

    public void testIPv6WithoutSquareBrackets() throws Exception {
        TestCase.assertEquals(null, new URI("http://fe80::1234/").getHost());
    }

    public void testEqualityWithNoPath() throws Exception {
        TestCase.assertFalse(new URI("http://android.com").equals(new URI("http://android.com/")));
    }

    public void testRelativize() throws Exception {
        URI a = new URI("http://host/a/b");
        URI b = new URI("http://host/a/b/c");
        TestCase.assertEquals("b/c", a.relativize(b).toString());// RI assumes a directory

    }

    public void testParseServerAuthorityInvalidAuthority() throws Exception {
        URI uri = new URI("http://host:-2/");
        TestCase.assertEquals("host:-2", uri.getAuthority());
        TestCase.assertNull(uri.getHost());
        TestCase.assertEquals((-1), uri.getPort());
        try {
            uri.parseServerAuthority();
            TestCase.fail();
        } catch (URISyntaxException expected) {
        }
    }

    public void testParseServerAuthorityOmittedAuthority() throws Exception {
        URI uri = new URI("http:file");
        uri.parseServerAuthority();// does nothing!

        TestCase.assertNull(uri.getAuthority());
        TestCase.assertNull(uri.getHost());
        TestCase.assertEquals((-1), uri.getPort());
    }

    public void testEncodingParts() throws Exception {
        URI uri = new URI("http", "user:pa55w?rd", "host", 80, "/doc|search", "q=green robots", "over 6\"");
        TestCase.assertEquals("http", uri.getScheme());
        TestCase.assertEquals("user:pa55w?rd@host:80", uri.getAuthority());
        TestCase.assertEquals("user:pa55w%3Frd@host:80", uri.getRawAuthority());
        TestCase.assertEquals("user:pa55w?rd", uri.getUserInfo());
        TestCase.assertEquals("user:pa55w%3Frd", uri.getRawUserInfo());
        TestCase.assertEquals("/doc|search", uri.getPath());
        TestCase.assertEquals("/doc%7Csearch", uri.getRawPath());
        TestCase.assertEquals("q=green robots", uri.getQuery());
        TestCase.assertEquals("q=green%20robots", uri.getRawQuery());
        TestCase.assertEquals("over 6\"", uri.getFragment());
        TestCase.assertEquals("over%206%22", uri.getRawFragment());
        TestCase.assertEquals("//user:pa55w?rd@host:80/doc|search?q=green robots", uri.getSchemeSpecificPart());
        TestCase.assertEquals("//user:pa55w%3Frd@host:80/doc%7Csearch?q=green%20robots", uri.getRawSchemeSpecificPart());
        TestCase.assertEquals("http://user:pa55w%3Frd@host:80/doc%7Csearch?q=green%20robots#over%206%22", uri.toString());
    }

    public void testSchemeCaseIsNotCanonicalized() throws Exception {
        URI uri = new URI("HTTP://host/path");
        TestCase.assertEquals("HTTP", uri.getScheme());
    }

    public void testEmptyAuthorityWithPath() throws Exception {
        URI uri = new URI("http:///path");
        TestCase.assertEquals(null, uri.getAuthority());
        TestCase.assertEquals("/path", uri.getPath());
    }

    public void testEmptyAuthorityWithQuery() throws Exception {
        URI uri = new URI("http://?query");
        TestCase.assertEquals(null, uri.getAuthority());
        TestCase.assertEquals("", uri.getPath());
        TestCase.assertEquals("query", uri.getQuery());
    }

    public void testEmptyAuthorityWithFragment() throws Exception {
        URI uri = new URI("http://#fragment");
        TestCase.assertEquals(null, uri.getAuthority());
        TestCase.assertEquals("", uri.getPath());
        TestCase.assertEquals("fragment", uri.getFragment());
    }

    public void testEncodingConstructorsRefuseRelativePath() throws Exception {
        try {
            new URI("http", "host", "relative", null);
            TestCase.fail();
        } catch (URISyntaxException expected) {
        }
        try {
            new URI("http", "host", "relative", null, null);
            TestCase.fail();
        } catch (URISyntaxException expected) {
        }
        try {
            new URI("http", null, "host", (-1), "relative", null, null);
            TestCase.fail();
        } catch (URISyntaxException expected) {
        }
    }

    public void testEncodingConstructorsAcceptEmptyPath() throws Exception {
        TestCase.assertEquals("", new URI("http", "host", "", null).getPath());
        TestCase.assertEquals("", new URI("http", "host", "", null, null).getPath());
        TestCase.assertEquals("", new URI("http", null, "host", (-1), "", null, null).getPath());
    }

    public void testResolveRelativeAndAbsolute() throws Exception {
        URI absolute = new URI("http://android.com/");
        URI relative = new URI("robots.txt");
        TestCase.assertEquals(absolute, absolute.resolve(absolute));
        TestCase.assertEquals(new URI("http://android.com/robots.txt"), absolute.resolve(relative));
        TestCase.assertEquals(absolute, relative.resolve(absolute));
        TestCase.assertEquals(relative, relative.resolve(relative));
    }

    public void testRelativizeRelativeAndAbsolute() throws Exception {
        URI absolute = new URI("http://android.com/");
        URI relative = new URI("robots.txt");
        TestCase.assertEquals(relative, absolute.relativize(new URI("http://android.com/robots.txt")));
        TestCase.assertEquals(new URI(""), absolute.relativize(absolute));
        TestCase.assertEquals(relative, absolute.relativize(relative));
        TestCase.assertEquals(absolute, relative.relativize(absolute));
        TestCase.assertEquals(new URI(""), relative.relativize(relative));
    }

    public void testPartContainsSpace() throws Exception {
        try {
            new URI("ht tp://host/");
            TestCase.fail();
        } catch (URISyntaxException expected) {
        }
        try {
            new URI("http://user name@host/");
            TestCase.fail();
        } catch (URISyntaxException expected) {
        }
        try {
            new URI("http://ho st/");
            TestCase.fail();
        } catch (URISyntaxException expected) {
        }
        try {
            new URI("http://host:80 80/");
            TestCase.fail();
        } catch (URISyntaxException expected) {
        }
        try {
            new URI("http://host/fi le");
            TestCase.fail();
        } catch (URISyntaxException expected) {
        }
        try {
            new URI("http://host/file?que ry");
            TestCase.fail();
        } catch (URISyntaxException expected) {
        }
        try {
            new URI("http://host/file?query#re f");
            TestCase.fail();
        } catch (URISyntaxException expected) {
        }
    }

    // http://code.google.com/p/android/issues/detail?id=37577
    public void testUnderscore() throws Exception {
        URI uri = new URI("http://a_b.c.d.net/");
        TestCase.assertEquals("a_b.c.d.net", uri.getAuthority());
        // The RFC's don't permit underscores in hostnames, and neither does URI (unlike URL).
        TestCase.assertNull(uri.getHost());
    }
}

