/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util;


import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * URIUtil Tests.
 */
@SuppressWarnings("SpellCheckingInspection")
public class URIUtilTest {
    // TODO: Parameterize
    @Test
    public void testEncodePath() {
        // test basic encode/decode
        StringBuilder buf = new StringBuilder();
        buf.setLength(0);
        URIUtil.encodePath(buf, "/foo%23+;,:=/b a r/?info ");
        Assertions.assertEquals("/foo%2523+%3B,:=/b%20a%20r/%3Finfo%20", buf.toString());
        Assertions.assertEquals("/foo%2523+%3B,:=/b%20a%20r/%3Finfo%20", URIUtil.encodePath("/foo%23+;,:=/b a r/?info "));
        buf.setLength(0);
        URIUtil.encodeString(buf, "foo%23;,:=b a r", ";,= ");
        Assertions.assertEquals("foo%2523%3b%2c:%3db%20a%20r", buf.toString());
        buf.setLength(0);
        URIUtil.encodePath(buf, "/context/\'list\'/\"me\"/;<script>window.alert(\'xss\');</script>");
        Assertions.assertEquals("/context/%27list%27/%22me%22/%3B%3Cscript%3Ewindow.alert(%27xss%27)%3B%3C/script%3E", buf.toString());
        buf.setLength(0);
        URIUtil.encodePath(buf, "test\u00f6?\u00f6:\u00df");
        Assertions.assertEquals("test%C3%B6%3F%C3%B6:%C3%9F", buf.toString());
        buf.setLength(0);
        URIUtil.encodePath(buf, "test?\u00f6?\u00f6:\u00df");
        Assertions.assertEquals("test%3F%C3%B6%3F%C3%B6:%C3%9F", buf.toString());
    }

    // TODO: Parameterize
    @Test
    public void testDecodePath() {
        Assertions.assertEquals(URIUtil.decodePath("xx/foo/barxx", 2, 8), "/foo/bar");
        Assertions.assertEquals("/foo/bar", URIUtil.decodePath("/foo/bar"));
        Assertions.assertEquals("/f o/b r", URIUtil.decodePath("/f%20o/b%20r"));
        Assertions.assertEquals("/foo/bar", URIUtil.decodePath("/foo;ignore/bar;ignore"));
        Assertions.assertEquals("/f??/bar", URIUtil.decodePath("/f\u00e4\u00e4;ignore/bar;ignore"));
        Assertions.assertEquals("/f\u0629\u0629%23/bar", URIUtil.decodePath("/f%d8%a9%d8%a9%2523;ignore/bar;ignore"));
        Assertions.assertEquals("foo%23;,:=b a r", URIUtil.decodePath("foo%2523%3b%2c:%3db%20a%20r;rubbish"));
        Assertions.assertEquals("/foo/bar%23;,:=b a r=", URIUtil.decodePath("xxx/foo/bar%2523%3b%2c:%3db%20a%20r%3Dxxx;rubbish", 3, 35));
        Assertions.assertEquals("f\u00e4\u00e4%23;,:=b a r=", URIUtil.decodePath("f??%2523%3b%2c:%3db%20a%20r%3D"));
        Assertions.assertEquals("f\u0629\u0629%23;,:=b a r", URIUtil.decodePath("f%d8%a9%d8%a9%2523%3b%2c:%3db%20a%20r"));
        // Test for null character (real world ugly test case)
        byte[] oddBytes = new byte[]{ '/', 0, '/' };
        String odd = new String(oddBytes, StandardCharsets.ISO_8859_1);
        Assertions.assertEquals(odd, URIUtil.decodePath("/%00/"));
    }

    // TODO: Parameterize
    @Test
    public void testAddEncodedPaths() {
        Assertions.assertEquals(URIUtil.addEncodedPaths(null, null), null, "null+null");
        Assertions.assertEquals(URIUtil.addEncodedPaths(null, ""), "", "null+");
        Assertions.assertEquals(URIUtil.addEncodedPaths(null, "bbb"), "bbb", "null+bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths(null, "/"), "/", "null+/");
        Assertions.assertEquals(URIUtil.addEncodedPaths(null, "/bbb"), "/bbb", "null+/bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("", null), "", "+null");
        Assertions.assertEquals(URIUtil.addEncodedPaths("", ""), "", "+");
        Assertions.assertEquals(URIUtil.addEncodedPaths("", "bbb"), "bbb", "+bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("", "/"), "/", "+/");
        Assertions.assertEquals(URIUtil.addEncodedPaths("", "/bbb"), "/bbb", "+/bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa", null), "aaa", "aaa+null");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa", ""), "aaa", "aaa+");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa", "bbb"), "aaa/bbb", "aaa+bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa", "/"), "aaa/", "aaa+/");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa", "/bbb"), "aaa/bbb", "aaa+/bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("/", null), "/", "/+null");
        Assertions.assertEquals(URIUtil.addEncodedPaths("/", ""), "/", "/+");
        Assertions.assertEquals(URIUtil.addEncodedPaths("/", "bbb"), "/bbb", "/+bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("/", "/"), "/", "/+/");
        Assertions.assertEquals(URIUtil.addEncodedPaths("/", "/bbb"), "/bbb", "/+/bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/", null), "aaa/", "aaa/+null");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/", ""), "aaa/", "aaa/+");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/", "bbb"), "aaa/bbb", "aaa/+bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/", "/"), "aaa/", "aaa/+/");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/", "/bbb"), "aaa/bbb", "aaa/+/bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths(";JS", null), ";JS", ";JS+null");
        Assertions.assertEquals(URIUtil.addEncodedPaths(";JS", ""), ";JS", ";JS+");
        Assertions.assertEquals(URIUtil.addEncodedPaths(";JS", "bbb"), "bbb;JS", ";JS+bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths(";JS", "/"), "/;JS", ";JS+/");
        Assertions.assertEquals(URIUtil.addEncodedPaths(";JS", "/bbb"), "/bbb;JS", ";JS+/bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa;JS", null), "aaa;JS", "aaa;JS+null");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa;JS", ""), "aaa;JS", "aaa;JS+");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa;JS", "bbb"), "aaa/bbb;JS", "aaa;JS+bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa;JS", "/"), "aaa/;JS", "aaa;JS+/");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa;JS", "/bbb"), "aaa/bbb;JS", "aaa;JS+/bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/;JS", null), "aaa/;JS", "aaa;JS+null");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/;JS", ""), "aaa/;JS", "aaa;JS+");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/;JS", "bbb"), "aaa/bbb;JS", "aaa;JS+bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/;JS", "/"), "aaa/;JS", "aaa;JS+/");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/;JS", "/bbb"), "aaa/bbb;JS", "aaa;JS+/bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("?A=1", null), "?A=1", "?A=1+null");
        Assertions.assertEquals(URIUtil.addEncodedPaths("?A=1", ""), "?A=1", "?A=1+");
        Assertions.assertEquals(URIUtil.addEncodedPaths("?A=1", "bbb"), "bbb?A=1", "?A=1+bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("?A=1", "/"), "/?A=1", "?A=1+/");
        Assertions.assertEquals(URIUtil.addEncodedPaths("?A=1", "/bbb"), "/bbb?A=1", "?A=1+/bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa?A=1", null), "aaa?A=1", "aaa?A=1+null");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa?A=1", ""), "aaa?A=1", "aaa?A=1+");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa?A=1", "bbb"), "aaa/bbb?A=1", "aaa?A=1+bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa?A=1", "/"), "aaa/?A=1", "aaa?A=1+/");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa?A=1", "/bbb"), "aaa/bbb?A=1", "aaa?A=1+/bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/?A=1", null), "aaa/?A=1", "aaa?A=1+null");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/?A=1", ""), "aaa/?A=1", "aaa?A=1+");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/?A=1", "bbb"), "aaa/bbb?A=1", "aaa?A=1+bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/?A=1", "/"), "aaa/?A=1", "aaa?A=1+/");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/?A=1", "/bbb"), "aaa/bbb?A=1", "aaa?A=1+/bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths(";JS?A=1", null), ";JS?A=1", ";JS?A=1+null");
        Assertions.assertEquals(URIUtil.addEncodedPaths(";JS?A=1", ""), ";JS?A=1", ";JS?A=1+");
        Assertions.assertEquals(URIUtil.addEncodedPaths(";JS?A=1", "bbb"), "bbb;JS?A=1", ";JS?A=1+bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths(";JS?A=1", "/"), "/;JS?A=1", ";JS?A=1+/");
        Assertions.assertEquals(URIUtil.addEncodedPaths(";JS?A=1", "/bbb"), "/bbb;JS?A=1", ";JS?A=1+/bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa;JS?A=1", null), "aaa;JS?A=1", "aaa;JS?A=1+null");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa;JS?A=1", ""), "aaa;JS?A=1", "aaa;JS?A=1+");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa;JS?A=1", "bbb"), "aaa/bbb;JS?A=1", "aaa;JS?A=1+bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa;JS?A=1", "/"), "aaa/;JS?A=1", "aaa;JS?A=1+/");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa;JS?A=1", "/bbb"), "aaa/bbb;JS?A=1", "aaa;JS?A=1+/bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/;JS?A=1", null), "aaa/;JS?A=1", "aaa;JS?A=1+null");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/;JS?A=1", ""), "aaa/;JS?A=1", "aaa;JS?A=1+");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/;JS?A=1", "bbb"), "aaa/bbb;JS?A=1", "aaa;JS?A=1+bbb");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/;JS?A=1", "/"), "aaa/;JS?A=1", "aaa;JS?A=1+/");
        Assertions.assertEquals(URIUtil.addEncodedPaths("aaa/;JS?A=1", "/bbb"), "aaa/bbb;JS?A=1", "aaa;JS?A=1+/bbb");
    }

    // TODO: Parameterize
    @Test
    public void testAddDecodedPaths() {
        Assertions.assertEquals(URIUtil.addPaths(null, null), null, "null+null");
        Assertions.assertEquals(URIUtil.addPaths(null, ""), "", "null+");
        Assertions.assertEquals(URIUtil.addPaths(null, "bbb"), "bbb", "null+bbb");
        Assertions.assertEquals(URIUtil.addPaths(null, "/"), "/", "null+/");
        Assertions.assertEquals(URIUtil.addPaths(null, "/bbb"), "/bbb", "null+/bbb");
        Assertions.assertEquals(URIUtil.addPaths("", null), "", "+null");
        Assertions.assertEquals(URIUtil.addPaths("", ""), "", "+");
        Assertions.assertEquals(URIUtil.addPaths("", "bbb"), "bbb", "+bbb");
        Assertions.assertEquals(URIUtil.addPaths("", "/"), "/", "+/");
        Assertions.assertEquals(URIUtil.addPaths("", "/bbb"), "/bbb", "+/bbb");
        Assertions.assertEquals(URIUtil.addPaths("aaa", null), "aaa", "aaa+null");
        Assertions.assertEquals(URIUtil.addPaths("aaa", ""), "aaa", "aaa+");
        Assertions.assertEquals(URIUtil.addPaths("aaa", "bbb"), "aaa/bbb", "aaa+bbb");
        Assertions.assertEquals(URIUtil.addPaths("aaa", "/"), "aaa/", "aaa+/");
        Assertions.assertEquals(URIUtil.addPaths("aaa", "/bbb"), "aaa/bbb", "aaa+/bbb");
        Assertions.assertEquals(URIUtil.addPaths("/", null), "/", "/+null");
        Assertions.assertEquals(URIUtil.addPaths("/", ""), "/", "/+");
        Assertions.assertEquals(URIUtil.addPaths("/", "bbb"), "/bbb", "/+bbb");
        Assertions.assertEquals(URIUtil.addPaths("/", "/"), "/", "/+/");
        Assertions.assertEquals(URIUtil.addPaths("/", "/bbb"), "/bbb", "/+/bbb");
        Assertions.assertEquals(URIUtil.addPaths("aaa/", null), "aaa/", "aaa/+null");
        Assertions.assertEquals(URIUtil.addPaths("aaa/", ""), "aaa/", "aaa/+");
        Assertions.assertEquals(URIUtil.addPaths("aaa/", "bbb"), "aaa/bbb", "aaa/+bbb");
        Assertions.assertEquals(URIUtil.addPaths("aaa/", "/"), "aaa/", "aaa/+/");
        Assertions.assertEquals(URIUtil.addPaths("aaa/", "/bbb"), "aaa/bbb", "aaa/+/bbb");
        Assertions.assertEquals(URIUtil.addPaths(";JS", null), ";JS", ";JS+null");
        Assertions.assertEquals(URIUtil.addPaths(";JS", ""), ";JS", ";JS+");
        Assertions.assertEquals(URIUtil.addPaths(";JS", "bbb"), ";JS/bbb", ";JS+bbb");
        Assertions.assertEquals(URIUtil.addPaths(";JS", "/"), ";JS/", ";JS+/");
        Assertions.assertEquals(URIUtil.addPaths(";JS", "/bbb"), ";JS/bbb", ";JS+/bbb");
        Assertions.assertEquals(URIUtil.addPaths("aaa;JS", null), "aaa;JS", "aaa;JS+null");
        Assertions.assertEquals(URIUtil.addPaths("aaa;JS", ""), "aaa;JS", "aaa;JS+");
        Assertions.assertEquals(URIUtil.addPaths("aaa;JS", "bbb"), "aaa;JS/bbb", "aaa;JS+bbb");
        Assertions.assertEquals(URIUtil.addPaths("aaa;JS", "/"), "aaa;JS/", "aaa;JS+/");
        Assertions.assertEquals(URIUtil.addPaths("aaa;JS", "/bbb"), "aaa;JS/bbb", "aaa;JS+/bbb");
        Assertions.assertEquals(URIUtil.addPaths("aaa/;JS", null), "aaa/;JS", "aaa;JS+null");
        Assertions.assertEquals(URIUtil.addPaths("aaa/;JS", ""), "aaa/;JS", "aaa;JS+");
        Assertions.assertEquals(URIUtil.addPaths("aaa/;JS", "bbb"), "aaa/;JS/bbb", "aaa;JS+bbb");
        Assertions.assertEquals(URIUtil.addPaths("aaa/;JS", "/"), "aaa/;JS/", "aaa;JS+/");
        Assertions.assertEquals(URIUtil.addPaths("aaa/;JS", "/bbb"), "aaa/;JS/bbb", "aaa;JS+/bbb");
        Assertions.assertEquals(URIUtil.addPaths("?A=1", null), "?A=1", "?A=1+null");
        Assertions.assertEquals(URIUtil.addPaths("?A=1", ""), "?A=1", "?A=1+");
        Assertions.assertEquals(URIUtil.addPaths("?A=1", "bbb"), "?A=1/bbb", "?A=1+bbb");
        Assertions.assertEquals(URIUtil.addPaths("?A=1", "/"), "?A=1/", "?A=1+/");
        Assertions.assertEquals(URIUtil.addPaths("?A=1", "/bbb"), "?A=1/bbb", "?A=1+/bbb");
        Assertions.assertEquals(URIUtil.addPaths("aaa?A=1", null), "aaa?A=1", "aaa?A=1+null");
        Assertions.assertEquals(URIUtil.addPaths("aaa?A=1", ""), "aaa?A=1", "aaa?A=1+");
        Assertions.assertEquals(URIUtil.addPaths("aaa?A=1", "bbb"), "aaa?A=1/bbb", "aaa?A=1+bbb");
        Assertions.assertEquals(URIUtil.addPaths("aaa?A=1", "/"), "aaa?A=1/", "aaa?A=1+/");
        Assertions.assertEquals(URIUtil.addPaths("aaa?A=1", "/bbb"), "aaa?A=1/bbb", "aaa?A=1+/bbb");
        Assertions.assertEquals(URIUtil.addPaths("aaa/?A=1", null), "aaa/?A=1", "aaa?A=1+null");
        Assertions.assertEquals(URIUtil.addPaths("aaa/?A=1", ""), "aaa/?A=1", "aaa?A=1+");
        Assertions.assertEquals(URIUtil.addPaths("aaa/?A=1", "bbb"), "aaa/?A=1/bbb", "aaa?A=1+bbb");
        Assertions.assertEquals(URIUtil.addPaths("aaa/?A=1", "/"), "aaa/?A=1/", "aaa?A=1+/");
        Assertions.assertEquals(URIUtil.addPaths("aaa/?A=1", "/bbb"), "aaa/?A=1/bbb", "aaa?A=1+/bbb");
    }

    // TODO: Parameterize
    @Test
    public void testCompactPath() {
        Assertions.assertEquals("/foo/bar", URIUtil.compactPath("/foo/bar"));
        Assertions.assertEquals("/foo/bar?a=b//c", URIUtil.compactPath("/foo/bar?a=b//c"));
        Assertions.assertEquals("/foo/bar", URIUtil.compactPath("//foo//bar"));
        Assertions.assertEquals("/foo/bar?a=b//c", URIUtil.compactPath("//foo//bar?a=b//c"));
        Assertions.assertEquals("/foo/bar", URIUtil.compactPath("/foo///bar"));
        Assertions.assertEquals("/foo/bar?a=b//c", URIUtil.compactPath("/foo///bar?a=b//c"));
    }

    // TODO: Parameterize
    @Test
    public void testParentPath() {
        Assertions.assertEquals("/aaa/", URIUtil.parentPath("/aaa/bbb/"), "parent /aaa/bbb/");
        Assertions.assertEquals("/aaa/", URIUtil.parentPath("/aaa/bbb"), "parent /aaa/bbb");
        Assertions.assertEquals("/", URIUtil.parentPath("/aaa/"), "parent /aaa/");
        Assertions.assertEquals("/", URIUtil.parentPath("/aaa"), "parent /aaa");
        Assertions.assertEquals(null, URIUtil.parentPath("/"), "parent /");
        Assertions.assertEquals(null, URIUtil.parentPath(null), "parent null");
    }

    // TODO: Parameterize
    @Test
    public void testEqualsIgnoreEncoding() {
        Assertions.assertTrue(URIUtil.equalsIgnoreEncodings("http://example.com/foo/bar", "http://example.com/foo/bar"));
        Assertions.assertTrue(URIUtil.equalsIgnoreEncodings("/barry's", "/barry%27s"));
        Assertions.assertTrue(URIUtil.equalsIgnoreEncodings("/barry%27s", "/barry's"));
        Assertions.assertTrue(URIUtil.equalsIgnoreEncodings("/barry%27s", "/barry%27s"));
        Assertions.assertTrue(URIUtil.equalsIgnoreEncodings("/b rry's", "/b%20rry%27s"));
        Assertions.assertTrue(URIUtil.equalsIgnoreEncodings("/b rry%27s", "/b%20rry's"));
        Assertions.assertTrue(URIUtil.equalsIgnoreEncodings("/b rry%27s", "/b%20rry%27s"));
        Assertions.assertTrue(URIUtil.equalsIgnoreEncodings("/foo%2fbar", "/foo%2fbar"));
        Assertions.assertTrue(URIUtil.equalsIgnoreEncodings("/foo%2fbar", "/foo%2Fbar"));
        Assertions.assertFalse(URIUtil.equalsIgnoreEncodings("ABC", "abc"));
        Assertions.assertFalse(URIUtil.equalsIgnoreEncodings("/barry's", "/barry%26s"));
        Assertions.assertFalse(URIUtil.equalsIgnoreEncodings("/foo/bar", "/foo%2fbar"));
        Assertions.assertFalse(URIUtil.equalsIgnoreEncodings("/foo2fbar", "/foo/bar"));
    }

    // TODO: Parameterize
    @Test
    public void testEqualsIgnoreEncoding_JarFile() {
        URI uriA = URI.create("jar:file:/path/to/main.jar!/META-INF/versions/");
        URI uriB = URI.create("jar:file:/path/to/main.jar!/META-INF/%76ersions/");
        Assertions.assertTrue(URIUtil.equalsIgnoreEncodings(uriA, uriB));
        uriA = URI.create("JAR:FILE:/path/to/main.jar!/META-INF/versions/");
        uriB = URI.create("jar:file:/path/to/main.jar!/META-INF/versions/");
        Assertions.assertTrue(URIUtil.equalsIgnoreEncodings(uriA, uriB));
    }

    // TODO: Parameterize
    @Test
    public void testJarSource() throws Exception {
        MatcherAssert.assertThat(URIUtil.getJarSource("file:///tmp/"), Matchers.is("file:///tmp/"));
        MatcherAssert.assertThat(URIUtil.getJarSource("jar:file:///tmp/foo.jar"), Matchers.is("file:///tmp/foo.jar"));
        MatcherAssert.assertThat(URIUtil.getJarSource("jar:file:///tmp/foo.jar!/some/path"), Matchers.is("file:///tmp/foo.jar"));
        MatcherAssert.assertThat(URIUtil.getJarSource(new URI("file:///tmp/")), Matchers.is(new URI("file:///tmp/")));
        MatcherAssert.assertThat(URIUtil.getJarSource(new URI("jar:file:///tmp/foo.jar")), Matchers.is(new URI("file:///tmp/foo.jar")));
        MatcherAssert.assertThat(URIUtil.getJarSource(new URI("jar:file:///tmp/foo.jar!/some/path")), Matchers.is(new URI("file:///tmp/foo.jar")));
    }
}

