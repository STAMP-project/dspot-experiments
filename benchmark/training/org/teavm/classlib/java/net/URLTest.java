/**
 * Copyright 2018 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.net;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class URLTest {
    URL u;

    URL u1;

    URL u2;

    URL u3;

    URL u4;

    URL u5;

    boolean caught;

    @Test
    public void test_ConstructorLjava_lang_String() throws IOException {
        // Tests for multiple URL instantiation basic parsing test
        u = new URL("http://www.yahoo1.com:8080/dir1/dir2/test.cgi?point1.html#anchor1");
        Assert.assertEquals("u returns a wrong protocol", "http", u.getProtocol());
        Assert.assertEquals("u returns a wrong host", "www.yahoo1.com", u.getHost());
        Assert.assertEquals("u returns a wrong port", 8080, u.getPort());
        Assert.assertEquals("u returns a wrong file", "/dir1/dir2/test.cgi?point1.html", u.getFile());
        Assert.assertEquals("u returns a wrong anchor", "anchor1", u.getRef());
        // test for no file
        u1 = new URL("http://www.yahoo2.com:9999");
        Assert.assertEquals("u1 returns a wrong protocol", "http", u1.getProtocol());
        Assert.assertEquals("u1 returns a wrong host", "www.yahoo2.com", u1.getHost());
        Assert.assertEquals("u1 returns a wrong port", 9999, u1.getPort());
        Assert.assertTrue("u1 returns a wrong file", u1.getFile().equals(""));
        Assert.assertNull("u1 returns a wrong anchor", u1.getRef());
        // test for no port
        u2 = new URL("http://www.yahoo3.com/dir1/dir2/test.cgi?point1.html#anchor1");
        Assert.assertEquals("u2 returns a wrong protocol", "http", u2.getProtocol());
        Assert.assertEquals("u2 returns a wrong host", "www.yahoo3.com", u2.getHost());
        Assert.assertEquals("u2 returns a wrong port", (-1), u2.getPort());
        Assert.assertEquals("u2 returns a wrong file", "/dir1/dir2/test.cgi?point1.html", u2.getFile());
        Assert.assertEquals("u2 returns a wrong anchor", "anchor1", u2.getRef());
        // test for no port
        URL u2a = new URL("file://www.yahoo3.com/dir1/dir2/test.cgi#anchor1");
        Assert.assertEquals("u2a returns a wrong protocol", "file", u2a.getProtocol());
        Assert.assertEquals("u2a returns a wrong host", "www.yahoo3.com", u2a.getHost());
        Assert.assertEquals("u2a returns a wrong port", (-1), u2a.getPort());
        Assert.assertEquals("u2a returns a wrong file", "/dir1/dir2/test.cgi", u2a.getFile());
        Assert.assertEquals("u2a returns a wrong anchor", "anchor1", u2a.getRef());
        // test for no file, no port
        u3 = new URL("http://www.yahoo4.com/");
        Assert.assertEquals("u3 returns a wrong protocol", "http", u3.getProtocol());
        Assert.assertEquals("u3 returns a wrong host", "www.yahoo4.com", u3.getHost());
        Assert.assertEquals("u3 returns a wrong port", (-1), u3.getPort());
        Assert.assertEquals("u3 returns a wrong file", "/", u3.getFile());
        Assert.assertNull("u3 returns a wrong anchor", u3.getRef());
        // test for no file, no port
        URL u3a = new URL("file://www.yahoo4.com/");
        Assert.assertEquals("u3a returns a wrong protocol", "file", u3a.getProtocol());
        Assert.assertEquals("u3a returns a wrong host", "www.yahoo4.com", u3a.getHost());
        Assert.assertEquals("u3a returns a wrong port", (-1), u3a.getPort());
        Assert.assertEquals("u3a returns a wrong file", "/", u3a.getFile());
        Assert.assertNull("u3a returns a wrong anchor", u3a.getRef());
        // test for no file, no port
        URL u3b = new URL("file://www.yahoo4.com");
        Assert.assertEquals("u3b returns a wrong protocol", "file", u3b.getProtocol());
        Assert.assertEquals("u3b returns a wrong host", "www.yahoo4.com", u3b.getHost());
        Assert.assertEquals("u3b returns a wrong port", (-1), u3b.getPort());
        Assert.assertTrue("u3b returns a wrong file", u3b.getFile().equals(""));
        Assert.assertNull("u3b returns a wrong anchor", u3b.getRef());
        // test for non-port ":" and wierd characters occurrences
        u4 = new URL("http://www.yahoo5.com/di!@$%^&*()_+r1/di:::r2/test.cgi?point1.html#anchor1");
        Assert.assertEquals("u4 returns a wrong protocol", "http", u4.getProtocol());
        Assert.assertEquals("u4 returns a wrong host", "www.yahoo5.com", u4.getHost());
        Assert.assertEquals("u4 returns a wrong port", (-1), u4.getPort());
        Assert.assertEquals("u4 returns a wrong file", "/di!@$%^&*()_+r1/di:::r2/test.cgi?point1.html", u4.getFile());
        Assert.assertEquals("u4 returns a wrong anchor", "anchor1", u4.getRef());
        u5 = new URL("file:/testing.tst");
        Assert.assertEquals("u5 returns a wrong protocol", "file", u5.getProtocol());
        Assert.assertTrue("u5 returns a wrong host", u5.getHost().equals(""));
        Assert.assertEquals("u5 returns a wrong port", (-1), u5.getPort());
        Assert.assertEquals("u5 returns a wrong file", "/testing.tst", u5.getFile());
        Assert.assertNull("u5 returns a wrong anchor", u5.getRef());
        URL u5a = new URL("file:testing.tst");
        Assert.assertEquals("u5a returns a wrong protocol", "file", u5a.getProtocol());
        Assert.assertTrue("u5a returns a wrong host", u5a.getHost().equals(""));
        Assert.assertEquals("u5a returns a wrong port", (-1), u5a.getPort());
        Assert.assertEquals("u5a returns a wrong file", "testing.tst", u5a.getFile());
        Assert.assertNull("u5a returns a wrong anchor", u5a.getRef());
        URL u6 = new URL("http://host:/file");
        Assert.assertEquals("u6 return a wrong port", (-1), u6.getPort());
        URL u7 = new URL("file:../../file.txt");
        Assert.assertTrue(("u7 returns a wrong file: " + (u7.getFile())), u7.getFile().equals("../../file.txt"));
        URL u8 = new URL("http://[fec0::1:20d:60ff:fe24:7410]:35/file.txt");
        Assert.assertTrue(("u8 returns a wrong protocol " + (u8.getProtocol())), u8.getProtocol().equals("http"));
        Assert.assertTrue(("u8 returns a wrong host " + (u8.getHost())), u8.getHost().equals("[fec0::1:20d:60ff:fe24:7410]"));
        Assert.assertTrue(("u8 returns a wrong port " + (u8.getPort())), ((u8.getPort()) == 35));
        Assert.assertTrue(("u8 returns a wrong file " + (u8.getFile())), u8.getFile().equals("/file.txt"));
        Assert.assertNull(("u8 returns a wrong anchor " + (u8.getRef())), u8.getRef());
        URL u9 = new URL("file://[fec0::1:20d:60ff:fe24:7410]/file.txt#sogood");
        Assert.assertTrue(("u9 returns a wrong protocol " + (u9.getProtocol())), u9.getProtocol().equals("file"));
        Assert.assertTrue(("u9 returns a wrong host " + (u9.getHost())), u9.getHost().equals("[fec0::1:20d:60ff:fe24:7410]"));
        Assert.assertTrue(("u9 returns a wrong port " + (u9.getPort())), ((u9.getPort()) == (-1)));
        Assert.assertTrue(("u9 returns a wrong file " + (u9.getFile())), u9.getFile().equals("/file.txt"));
        Assert.assertTrue(("u9 returns a wrong anchor " + (u9.getRef())), u9.getRef().equals("sogood"));
        URL u10 = new URL("file://[fec0::1:20d:60ff:fe24:7410]");
        Assert.assertTrue(("u10 returns a wrong protocol " + (u10.getProtocol())), u10.getProtocol().equals("file"));
        Assert.assertTrue(("u10 returns a wrong host " + (u10.getHost())), u10.getHost().equals("[fec0::1:20d:60ff:fe24:7410]"));
        Assert.assertTrue(("u10 returns a wrong port " + (u10.getPort())), ((u10.getPort()) == (-1)));
        URL u11 = new URL("file:////file.txt");
        Assert.assertNull(("u11 returns a wrong authority " + (u11.getAuthority())), u11.getAuthority());
        Assert.assertTrue(("u11 returns a wrong file " + (u11.getFile())), u11.getFile().equals("////file.txt"));
        URL u12 = new URL("file:///file.txt");
        Assert.assertTrue("u12 returns a wrong authority", u12.getAuthority().equals(""));
        Assert.assertTrue(("u12 returns a wrong file " + (u12.getFile())), u12.getFile().equals("/file.txt"));
        // test for error catching
        // Bad HTTP format - no "//"
        u = new URL("http:www.yahoo5.com::22/dir1/di:::r2/test.cgi?point1.html#anchor1");
        caught = false;
        try {
            u = new URL("http://www.yahoo5.com::22/dir1/di:::r2/test.cgi?point1.html#anchor1");
        } catch (MalformedURLException e) {
            caught = true;
        }
        Assert.assertTrue("Should have throw MalformedURLException", caught);
        // unknown protocol
        try {
            u = new URL("myProtocol://www.yahoo.com:22");
        } catch (MalformedURLException e) {
            caught = true;
        }
        Assert.assertTrue("3 Failed to throw MalformedURLException", caught);
        caught = false;
        // no protocol
        try {
            u = new URL("www.yahoo.com");
        } catch (MalformedURLException e) {
            caught = true;
        }
        Assert.assertTrue("4 Failed to throw MalformedURLException", caught);
        caught = false;
        URL u1 = null;
        try {
            // No leading or trailing spaces.
            u1 = new URL("file:/some/path");
            Assert.assertEquals("5 got wrong file length1", 10, u1.getFile().length());
            // Leading spaces.
            u1 = new URL("  file:/some/path");
            Assert.assertEquals("5 got wrong file length2", 10, u1.getFile().length());
            // Trailing spaces.
            u1 = new URL("file:/some/path  ");
            Assert.assertEquals("5 got wrong file length3", 10, u1.getFile().length());
            // Leading and trailing.
            u1 = new URL("  file:/some/path ");
            Assert.assertEquals("5 got wrong file length4", 10, u1.getFile().length());
            // in-place spaces.
            u1 = new URL("  file:  /some/path ");
            Assert.assertEquals("5 got wrong file length5", 12, u1.getFile().length());
        } catch (MalformedURLException e) {
            Assert.fail(("5 Did not expect the exception " + e));
        }
        // testing jar protocol with relative path
        // to make sure it's not canonicalized
        try {
            String file = "file:/a!/b/../d";
            u = new URL(("jar:" + file));
            Assert.assertEquals("Wrong file (jar protocol, relative path)", file, u.getFile());
        } catch (MalformedURLException e) {
            Assert.fail(("Unexpected exception (jar protocol, relative path)" + e));
        }
    }

    @Test
    public void test_ConstructorLjava_net_URLLjava_lang_String() throws Exception {
        // Test for method java.net.URL(java.net.URL, java.lang.String)
        u = new URL("http://www.yahoo.com");
        URL uf = new URL("file://www.yahoo.com");
        // basic ones
        u1 = new URL(u, "file.java");
        Assert.assertEquals("1 returns a wrong protocol", "http", u1.getProtocol());
        Assert.assertEquals("1 returns a wrong host", "www.yahoo.com", u1.getHost());
        Assert.assertEquals("1 returns a wrong port", (-1), u1.getPort());
        Assert.assertEquals("1 returns a wrong file", "/file.java", u1.getFile());
        Assert.assertNull("1 returns a wrong anchor", u1.getRef());
        URL u1f = new URL(uf, "file.java");
        Assert.assertEquals("1f returns a wrong protocol", "file", u1f.getProtocol());
        Assert.assertEquals("1f returns a wrong host", "www.yahoo.com", u1f.getHost());
        Assert.assertEquals("1f returns a wrong port", (-1), u1f.getPort());
        Assert.assertEquals("1f returns a wrong file", "/file.java", u1f.getFile());
        Assert.assertNull("1f returns a wrong anchor", u1f.getRef());
        u1 = new URL(u, "dir1/dir2/../file.java");
        Assert.assertEquals("3 returns a wrong protocol", "http", u1.getProtocol());
        Assert.assertTrue(("3 returns a wrong host: " + (u1.getHost())), u1.getHost().equals("www.yahoo.com"));
        Assert.assertEquals("3 returns a wrong port", (-1), u1.getPort());
        Assert.assertEquals("3 returns a wrong file", "/dir1/dir2/../file.java", u1.getFile());
        Assert.assertNull("3 returns a wrong anchor", u1.getRef());
        u1 = new URL(u, "http:dir1/dir2/../file.java");
        Assert.assertEquals("3a returns a wrong protocol", "http", u1.getProtocol());
        Assert.assertTrue(("3a returns a wrong host: " + (u1.getHost())), u1.getHost().equals(""));
        Assert.assertEquals("3a returns a wrong port", (-1), u1.getPort());
        Assert.assertEquals("3a returns a wrong file", "dir1/dir2/../file.java", u1.getFile());
        Assert.assertNull("3a returns a wrong anchor", u1.getRef());
        u = new URL("http://www.apache.org/testing/");
        u1 = new URL(u, "file.java");
        Assert.assertEquals("4 returns a wrong protocol", "http", u1.getProtocol());
        Assert.assertEquals("4 returns a wrong host", "www.apache.org", u1.getHost());
        Assert.assertEquals("4 returns a wrong port", (-1), u1.getPort());
        Assert.assertEquals("4 returns a wrong file", "/testing/file.java", u1.getFile());
        Assert.assertNull("4 returns a wrong anchor", u1.getRef());
        uf = new URL("file://www.apache.org/testing/");
        u1f = new URL(uf, "file.java");
        Assert.assertEquals("4f returns a wrong protocol", "file", u1f.getProtocol());
        Assert.assertEquals("4f returns a wrong host", "www.apache.org", u1f.getHost());
        Assert.assertEquals("4f returns a wrong port", (-1), u1f.getPort());
        Assert.assertEquals("4f returns a wrong file", "/testing/file.java", u1f.getFile());
        Assert.assertNull("4f returns a wrong anchor", u1f.getRef());
        uf = new URL("file:/testing/");
        u1f = new URL(uf, "file.java");
        Assert.assertEquals("4fa returns a wrong protocol", "file", u1f.getProtocol());
        Assert.assertTrue("4fa returns a wrong host", u1f.getHost().equals(""));
        Assert.assertEquals("4fa returns a wrong port", (-1), u1f.getPort());
        Assert.assertEquals("4fa returns a wrong file", "/testing/file.java", u1f.getFile());
        Assert.assertNull("4fa returns a wrong anchor", u1f.getRef());
        uf = new URL("file:testing/");
        u1f = new URL(uf, "file.java");
        Assert.assertEquals("4fb returns a wrong protocol", "file", u1f.getProtocol());
        Assert.assertTrue("4fb returns a wrong host", u1f.getHost().equals(""));
        Assert.assertEquals("4fb returns a wrong port", (-1), u1f.getPort());
        Assert.assertEquals("4fb returns a wrong file", "testing/file.java", u1f.getFile());
        Assert.assertNull("4fb returns a wrong anchor", u1f.getRef());
        u1f = new URL(uf, "file:file.java");
        Assert.assertEquals("4fc returns a wrong protocol", "file", u1f.getProtocol());
        Assert.assertTrue("4fc returns a wrong host", u1f.getHost().equals(""));
        Assert.assertEquals("4fc returns a wrong port", (-1), u1f.getPort());
        Assert.assertEquals("4fc returns a wrong file", "file.java", u1f.getFile());
        Assert.assertNull("4fc returns a wrong anchor", u1f.getRef());
        u1f = new URL(uf, "file:");
        Assert.assertEquals("4fd returns a wrong protocol", "file", u1f.getProtocol());
        Assert.assertTrue("4fd returns a wrong host", u1f.getHost().equals(""));
        Assert.assertEquals("4fd returns a wrong port", (-1), u1f.getPort());
        Assert.assertTrue("4fd returns a wrong file", u1f.getFile().equals(""));
        Assert.assertNull("4fd returns a wrong anchor", u1f.getRef());
        u = new URL("http://www.apache.org/testing");
        u1 = new URL(u, "file.java");
        Assert.assertEquals("5 returns a wrong protocol", "http", u1.getProtocol());
        Assert.assertEquals("5 returns a wrong host", "www.apache.org", u1.getHost());
        Assert.assertEquals("5 returns a wrong port", (-1), u1.getPort());
        Assert.assertEquals("5 returns a wrong file", "/file.java", u1.getFile());
        Assert.assertNull("5 returns a wrong anchor", u1.getRef());
        uf = new URL("file://www.apache.org/testing");
        u1f = new URL(uf, "file.java");
        Assert.assertEquals("5f returns a wrong protocol", "file", u1f.getProtocol());
        Assert.assertEquals("5f returns a wrong host", "www.apache.org", u1f.getHost());
        Assert.assertEquals("5f returns a wrong port", (-1), u1f.getPort());
        Assert.assertEquals("5f returns a wrong file", "/file.java", u1f.getFile());
        Assert.assertNull("5f returns a wrong anchor", u1f.getRef());
        uf = new URL("file:/testing");
        u1f = new URL(uf, "file.java");
        Assert.assertEquals("5fa returns a wrong protocol", "file", u1f.getProtocol());
        Assert.assertTrue("5fa returns a wrong host", u1f.getHost().equals(""));
        Assert.assertEquals("5fa returns a wrong port", (-1), u1f.getPort());
        Assert.assertEquals("5fa returns a wrong file", "/file.java", u1f.getFile());
        Assert.assertNull("5fa returns a wrong anchor", u1f.getRef());
        uf = new URL("file:testing");
        u1f = new URL(uf, "file.java");
        Assert.assertEquals("5fb returns a wrong protocol", "file", u1f.getProtocol());
        Assert.assertTrue("5fb returns a wrong host", u1f.getHost().equals(""));
        Assert.assertEquals("5fb returns a wrong port", (-1), u1f.getPort());
        Assert.assertEquals("5fb returns a wrong file", "file.java", u1f.getFile());
        Assert.assertNull("5fb returns a wrong anchor", u1f.getRef());
        u = new URL("http://www.apache.org/testing/foobaz");
        u1 = new URL(u, "/file.java");
        Assert.assertEquals("6 returns a wrong protocol", "http", u1.getProtocol());
        Assert.assertEquals("6 returns a wrong host", "www.apache.org", u1.getHost());
        Assert.assertEquals("6 returns a wrong port", (-1), u1.getPort());
        Assert.assertEquals("6 returns a wrong file", "/file.java", u1.getFile());
        Assert.assertNull("6 returns a wrong anchor", u1.getRef());
        uf = new URL("file://www.apache.org/testing/foobaz");
        u1f = new URL(uf, "/file.java");
        Assert.assertEquals("6f returns a wrong protocol", "file", u1f.getProtocol());
        Assert.assertEquals("6f returns a wrong host", "www.apache.org", u1f.getHost());
        Assert.assertEquals("6f returns a wrong port", (-1), u1f.getPort());
        Assert.assertEquals("6f returns a wrong file", "/file.java", u1f.getFile());
        Assert.assertNull("6f returns a wrong anchor", u1f.getRef());
        u = new URL("http://www.apache.org:8000/testing/foobaz");
        u1 = new URL(u, "/file.java");
        Assert.assertEquals("7 returns a wrong protocol", "http", u1.getProtocol());
        Assert.assertEquals("7 returns a wrong host", "www.apache.org", u1.getHost());
        Assert.assertEquals("7 returns a wrong port", 8000, u1.getPort());
        Assert.assertEquals("7 returns a wrong file", "/file.java", u1.getFile());
        Assert.assertNull("7 returns a wrong anchor", u1.getRef());
        u = new URL("http://www.apache.org/index.html");
        u1 = new URL(u, "#bar");
        Assert.assertEquals("8 returns a wrong host", "www.apache.org", u1.getHost());
        Assert.assertEquals("8 returns a wrong file", "/index.html", u1.getFile());
        Assert.assertEquals("8 returns a wrong anchor", "bar", u1.getRef());
        u = new URL("http://www.apache.org/index.html#foo");
        u1 = new URL(u, "http:#bar");
        Assert.assertEquals("9 returns a wrong host", "www.apache.org", u1.getHost());
        Assert.assertEquals("9 returns a wrong file", "/index.html", u1.getFile());
        Assert.assertEquals("9 returns a wrong anchor", "bar", u1.getRef());
        u = new URL("http://www.apache.org/index.html");
        u1 = new URL(u, "");
        Assert.assertEquals("10 returns a wrong host", "www.apache.org", u1.getHost());
        Assert.assertEquals("10 returns a wrong file", "/index.html", u1.getFile());
        Assert.assertNull("10 returns a wrong anchor", u1.getRef());
        uf = new URL("file://www.apache.org/index.html");
        u1f = new URL(uf, "");
        Assert.assertEquals("10f returns a wrong host", "www.apache.org", u1.getHost());
        Assert.assertEquals("10f returns a wrong file", "/index.html", u1.getFile());
        Assert.assertNull("10f returns a wrong anchor", u1.getRef());
        u = new URL("http://www.apache.org/index.html");
        u1 = new URL(u, "http://www.apache.org");
        Assert.assertEquals("11 returns a wrong host", "www.apache.org", u1.getHost());
        Assert.assertTrue("11 returns a wrong file", u1.getFile().equals(""));
        Assert.assertNull("11 returns a wrong anchor", u1.getRef());
        // test for question mark processing
        u = new URL("http://www.foo.com/d0/d1/d2/cgi-bin?foo=bar/baz");
        // test for relative file and out of bound "/../" processing
        u1 = new URL(u, "../dir1/./dir2/../file.java");
        Assert.assertTrue(("A) returns a wrong file: " + (u1.getFile())), u1.getFile().equals("/d0/d1/dir1/file.java"));
        // test for absolute and relative file processing
        u1 = new URL(u, "/../dir1/./dir2/../file.java");
        Assert.assertEquals("B) returns a wrong file", "/../dir1/./dir2/../file.java", u1.getFile());
        try {
            // u should raise a MalFormedURLException because u, the context is
            // null
            u = null;
            u1 = new URL(u, "file.java");
            Assert.fail("didn't throw the expected MalFormedURLException");
        } catch (MalformedURLException e) {
            // valid
        }
    }

    @Test
    public void test_ConstructorLjava_lang_StringLjava_lang_StringLjava_lang_String() throws MalformedURLException {
        u = new URL("http", "www.yahoo.com", "test.html#foo");
        Assert.assertEquals("http", u.getProtocol());
        Assert.assertEquals("www.yahoo.com", u.getHost());
        Assert.assertEquals((-1), u.getPort());
        Assert.assertEquals("test.html", u.getFile());
        Assert.assertEquals("foo", u.getRef());
        // Strange behavior in reference, the hostname contains a ':' so it gets
        // wrapped in '[', ']'
        URL testURL = new URL("http", "www.apache.org:8080", "test.html#anch");
        Assert.assertEquals("wrong protocol", "http", testURL.getProtocol());
        Assert.assertEquals("wrong host", "[www.apache.org:8080]", testURL.getHost());
        Assert.assertEquals("wrong port", (-1), testURL.getPort());
        Assert.assertEquals("wrong file", "test.html", testURL.getFile());
        Assert.assertEquals("wrong anchor", "anch", testURL.getRef());
    }

    @Test
    public void test_ConstructorLjava_lang_StringLjava_lang_StringILjava_lang_String() throws MalformedURLException {
        u = new URL("http", "www.yahoo.com", 8080, "test.html#foo");
        Assert.assertEquals("SSIS returns a wrong protocol", "http", u.getProtocol());
        Assert.assertEquals("SSIS returns a wrong host", "www.yahoo.com", u.getHost());
        Assert.assertEquals("SSIS returns a wrong port", 8080, u.getPort());
        Assert.assertEquals("SSIS returns a wrong file", "test.html", u.getFile());
        Assert.assertTrue(("SSIS returns a wrong anchor: " + (u.getRef())), u.getRef().equals("foo"));
        // Regression for HARMONY-83
        new URL("http", "apache.org", 123456789, "file");
        try {
            new URL("http", "apache.org", (-123), "file");
            Assert.fail("Assert 0: Negative port should throw exception");
        } catch (MalformedURLException e) {
            // expected
        }
    }

    @Test
    public void test_ConstructorLjava_lang_StringLjava_lang_StringILjava_lang_StringLjava_net_URLStreamHandler() throws Exception {
        // Test for method java.net.URL(java.lang.String, java.lang.String, int,
        // java.lang.String, java.net.URLStreamHandler)
        u = new URL("http", "www.yahoo.com", 8080, "test.html#foo", null);
        Assert.assertEquals("SSISH1 returns a wrong protocol", "http", u.getProtocol());
        Assert.assertEquals("SSISH1 returns a wrong host", "www.yahoo.com", u.getHost());
        Assert.assertEquals("SSISH1 returns a wrong port", 8080, u.getPort());
        Assert.assertEquals("SSISH1 returns a wrong file", "test.html", u.getFile());
        Assert.assertTrue(("SSISH1 returns a wrong anchor: " + (u.getRef())), u.getRef().equals("foo"));
    }

    @Test
    public void test_equalsLjava_lang_Object() throws MalformedURLException {
        u = new URL("http://www.apache.org:8080/dir::23??????????test.html");
        u1 = new URL("http://www.apache.org:8080/dir::23??????????test.html");
        Assert.assertTrue("A) equals returns false for two identical URLs", u.equals(u1));
        Assert.assertTrue("return true for null comparison", (!(u1.equals(null))));
        u = new URL("ftp://www.apache.org:8080/dir::23??????????test.html");
        Assert.assertTrue("Returned true for non-equal URLs", (!(u.equals(u1))));
        // Regression for HARMONY-6556
        u = new URL("file", null, 0, "/test.txt");
        u1 = new URL("file", null, 0, "/test.txt");
        Assert.assertEquals(u, u1);
        u = new URL("file", "first.invalid", 0, "/test.txt");
        u1 = new URL("file", "second.invalid", 0, "/test.txt");
        Assert.assertFalse(u.equals(u1));
    }

    @Test
    public void test_sameFileLjava_net_URL() throws Exception {
        // Test for method boolean java.net.URL.sameFile(java.net.URL)
        u = new URL("http://www.yahoo.com");
        u1 = new URL("http", "www.yahoo.com", "");
        Assert.assertTrue("Should be the same1", u.sameFile(u1));
        u = new URL("http://www.yahoo.com/dir1/dir2/test.html#anchor1");
        u1 = new URL("http://www.yahoo.com/dir1/dir2/test.html#anchor2");
        Assert.assertTrue("Should be the same ", u.sameFile(u1));
        // regression test for Harmony-1040
        u = new URL("file", null, (-1), "/d:/somedir/");
        u1 = new URL("file:/d:/somedir/");
        Assert.assertFalse(u.sameFile(u1));
        // regression test for Harmony-2136
        URL url1 = new URL("file:///anyfile");
        URL url2 = new URL("file://localhost/anyfile");
        Assert.assertTrue(url1.sameFile(url2));
        url1 = new URL("http:///anyfile");
        url2 = new URL("http://localhost/anyfile");
        Assert.assertFalse(url1.sameFile(url2));
        url1 = new URL("ftp:///anyfile");
        url2 = new URL("ftp://localhost/anyfile");
        Assert.assertFalse(url1.sameFile(url2));
        url1 = new URL("jar:file:///anyfile.jar!/");
        url2 = new URL("jar:file://localhost/anyfile.jar!/");
        Assert.assertFalse(url1.sameFile(url2));
    }

    @Test
    public void test_toString() {
        // Test for method java.lang.String java.net.URL.toString()
        try {
            u1 = new URL("http://www.yahoo2.com:9999");
            u = new URL("http://www.yahoo1.com:8080/dir1/dir2/test.cgi?point1.html#anchor1");
            Assert.assertEquals("a) Does not return the right url string", "http://www.yahoo1.com:8080/dir1/dir2/test.cgi?point1.html#anchor1", u.toString());
            Assert.assertEquals("b) Does not return the right url string", "http://www.yahoo2.com:9999", u1.toString());
            Assert.assertTrue("c) Does not return the right url string", u.equals(new URL(u.toString())));
        } catch (Exception e) {
            // Do nothing
        }
    }

    @Test
    public void test_toExternalForm() {
        try {
            u1 = new URL("http://www.yahoo2.com:9999");
            u = new URL("http://www.yahoo1.com:8080/dir1/dir2/test.cgi?point1.html#anchor1");
            Assert.assertEquals("a) Does not return the right url string", "http://www.yahoo1.com:8080/dir1/dir2/test.cgi?point1.html#anchor1", u.toString());
            Assert.assertEquals("b) Does not return the right url string", "http://www.yahoo2.com:9999", u1.toString());
            Assert.assertTrue("c) Does not return the right url string", u.equals(new URL(u.toString())));
            u = new URL("http:index");
            Assert.assertEquals("2 wrong external form", "http:index", u.toExternalForm());
            u = new URL("http", null, "index");
            Assert.assertEquals("2 wrong external form", "http:index", u.toExternalForm());
        } catch (Exception e) {
            // Do nothing
        }
    }

    @Test
    public void test_getFile() throws Exception {
        // Test for method java.lang.String java.net.URL.getFile()
        u = new URL("http", "www.yahoo.com:8080", 1233, "test/!@$%^&*/test.html#foo");
        Assert.assertEquals("returns a wrong file", "test/!@$%^&*/test.html", u.getFile());
        u = new URL("http", "www.yahoo.com:8080", 1233, "");
        Assert.assertTrue("returns a wrong file", u.getFile().equals(""));
    }

    @Test
    public void test_getHost() throws MalformedURLException {
        // Regression for HARMONY-60
        String ipv6Host = "FEDC:BA98:7654:3210:FEDC:BA98:7654:3210";
        URL url = new URL("http", ipv6Host, (-1), "myfile");
        Assert.assertEquals((("[" + ipv6Host) + "]"), url.getHost());
    }

    @Test
    public void test_getPort() throws Exception {
        // Test for method int java.net.URL.getPort()
        u = new URL("http://member12.c++.com:9999");
        Assert.assertTrue(("return wrong port number " + (u.getPort())), ((u.getPort()) == 9999));
        u = new URL("http://member12.c++.com:9999/");
        Assert.assertEquals("return wrong port number", 9999, u.getPort());
    }

    @Test
    public void test_getDefaultPort() throws MalformedURLException {
        u = new URL("http://member12.c++.com:9999");
        Assert.assertEquals(80, u.getDefaultPort());
        u = new URL("ftp://member12.c++.com:9999/");
        Assert.assertEquals(21, u.getDefaultPort());
    }

    @Test
    public void test_getProtocol() throws Exception {
        // Test for method java.lang.String java.net.URL.getProtocol()
        u = new URL("http://www.yahoo2.com:9999");
        Assert.assertTrue(("u returns a wrong protocol: " + (u.getProtocol())), u.getProtocol().equals("http"));
    }

    @Test
    public void test_getRef() {
        // Test for method java.lang.String java.net.URL.getRef()
        try {
            u1 = new URL("http://www.yahoo2.com:9999");
            u = new URL("http://www.yahoo1.com:8080/dir1/dir2/test.cgi?point1.html#anchor1");
            Assert.assertEquals("returns a wrong anchor1", "anchor1", u.getRef());
            Assert.assertNull("returns a wrong anchor2", u1.getRef());
            u1 = new URL("http://www.yahoo2.com#ref");
            Assert.assertEquals("returns a wrong anchor3", "ref", u1.getRef());
            u1 = new URL("http://www.yahoo2.com/file#ref1#ref2");
            Assert.assertEquals("returns a wrong anchor4", "ref1#ref2", u1.getRef());
        } catch (MalformedURLException e) {
            Assert.fail(("Incorrect URL format : " + (e.getMessage())));
        }
    }

    @Test
    public void test_getAuthority() throws MalformedURLException {
        URL testURL = new URL("http", "hostname", 80, "/java?q1#ref");
        Assert.assertEquals("hostname:80", testURL.getAuthority());
        Assert.assertEquals("hostname", testURL.getHost());
        Assert.assertNull(testURL.getUserInfo());
        Assert.assertEquals("/java?q1", testURL.getFile());
        Assert.assertEquals("/java", testURL.getPath());
        Assert.assertEquals("q1", testURL.getQuery());
        Assert.assertEquals("ref", testURL.getRef());
        testURL = new URL("http", "u:p@home", 80, "/java?q1#ref");
        Assert.assertEquals("[u:p@home]:80", testURL.getAuthority());
        Assert.assertEquals("[u:p@home]", testURL.getHost());
        Assert.assertNull(testURL.getUserInfo());
        Assert.assertEquals("/java?q1", testURL.getFile());
        Assert.assertEquals("/java", testURL.getPath());
        Assert.assertEquals("q1", testURL.getQuery());
        Assert.assertEquals("ref", testURL.getRef());
        testURL = new URL("http", "home", (-1), "/java");
        Assert.assertEquals("wrong authority2", "home", testURL.getAuthority());
        Assert.assertNull("wrong userInfo2", testURL.getUserInfo());
        Assert.assertEquals("wrong host2", "home", testURL.getHost());
        Assert.assertEquals("wrong file2", "/java", testURL.getFile());
        Assert.assertEquals("wrong path2", "/java", testURL.getPath());
        Assert.assertNull("wrong query2", testURL.getQuery());
        Assert.assertNull("wrong ref2", testURL.getRef());
    }

    @Test
    public void test_toURI() throws Exception {
        u = new URL("http://www.apache.org");
        URI uri = u.toURI();
        Assert.assertTrue(u.equals(uri.toURL()));
    }

    @Test
    public void test_ConstructorLnullLjava_lang_StringILjava_lang_String() throws Exception {
        // Regression for HARMONY-1131
        try {
            new URL(null, "1", 0, "file");
            Assert.fail("NullPointerException expected, but nothing was thrown!");
        } catch (NullPointerException e) {
            // Expected NullPointerException
        }
    }

    @Test
    public void test_ConstructorLnullLjava_lang_StringLjava_lang_String() throws Exception {
        // Regression for HARMONY-1131
        try {
            new URL(null, "1", "file");
            Assert.fail("NullPointerException expected, but nothing was thrown!");
        } catch (NullPointerException e) {
            // Expected NullPointerException
        }
    }

    @Test
    public void test_toExternalForm_Absolute() throws MalformedURLException {
        String strURL = "http://localhost?name=value";
        URL url = new URL(strURL);
        Assert.assertEquals(strURL, url.toExternalForm());
        strURL = "http://localhost?name=value/age=12";
        url = new URL(strURL);
        Assert.assertEquals(strURL, url.toExternalForm());
    }

    @Test
    public void test_toExternalForm_Relative() throws MalformedURLException {
        String strURL = "http://a/b/c/d;p?q";
        String ref = "?y";
        URL url = new URL(new URL(strURL), ref);
        Assert.assertEquals("http://a/b/c/?y", url.toExternalForm());
    }

    // Regression test for HARMONY-6254
    // Bogus handler forces file part of URL to be null
    static class MyHandler2 extends URLStreamHandler {
        @Override
        protected URLConnection openConnection(URL arg0) throws IOException {
            return null;
        }

        @Override
        protected void setURL(URL u, String protocol, String host, int port, String authority, String userInfo, String file, String query, String ref) {
            super.setURL(u, protocol, host, port, authority, userInfo, null, query, ref);
        }
    }

    @Test
    public void test_toExternalForm_Null() throws IOException {
        URLStreamHandler myHandler = new URLTest.MyHandler2();
        URL url = new URL(null, "foobar://example.com/foobar", myHandler);
        String s = url.toExternalForm();
        Assert.assertEquals("Got wrong URL external form", "foobar://example.com", s);
    }

    static class MyURLStreamHandler extends URLStreamHandler {
        @Override
        protected URLConnection openConnection(URL arg0) throws IOException {
            return null;
        }
    }

    static class MyURLStreamHandlerFactory implements URLStreamHandlerFactory {
        public static URLTest.MyURLStreamHandler handler = new URLTest.MyURLStreamHandler();

        @Override
        public URLStreamHandler createURLStreamHandler(String arg0) {
            URLTest.MyURLStreamHandlerFactory.handler = new URLTest.MyURLStreamHandler();
            return URLTest.MyURLStreamHandlerFactory.handler;
        }
    }
}

