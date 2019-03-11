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


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;


public class OldURLTest extends TestCase {
    private static final String helloWorldString = "Hello World";

    public void test_ConstructorLjava_lang_StringLjava_lang_StringILjava_lang_String() throws MalformedURLException {
        // Regression for HARMONY-83
        new URL("http", "apache.org", 123456789, "file");
        try {
            new URL("http", "apache.org", (-123), "file");
            TestCase.fail("Assert 0: Negative port should throw exception");
        } catch (MalformedURLException e) {
            // expected
        }
    }

    public void test_ConstructorLjava_lang_StringLjava_lang_StringLjava_lang_String() throws MalformedURLException {
        // Strange behavior in reference, the hostname contains a ':' so it gets wrapped in '[', ']'
        URL testURL = new URL("http", "www.apache.org:8082", "test.html#anch");
        TestCase.assertEquals("Assert 0: wrong protocol", "http", testURL.getProtocol());
        TestCase.assertEquals("Assert 1: wrong host", "[www.apache.org:8082]", testURL.getHost());
        TestCase.assertEquals("Assert 2: wrong port", (-1), testURL.getPort());
        TestCase.assertEquals("Assert 3: wrong file", "/test.html", testURL.getFile());
        TestCase.assertEquals("Assert 4: wrong anchor", "anch", testURL.getRef());
        try {
            new URL("hftp", "apache.org:8082", "test.html#anch");
            TestCase.fail("Assert 0: Invalid protocol");
        } catch (MalformedURLException e) {
            // expected
        }
    }

    public void test_java_protocol_handler_pkgs_prop() throws MalformedURLException {
        // Regression test for Harmony-3094
        final String HANDLER_PKGS = "java.protocol.handler.pkgs";
        System.setProperty(HANDLER_PKGS, "fake|org.apache.harmony.luni.tests.java.net");
        try {
            new URL("test_protocol", "", "fake.jar");
        } catch (MalformedURLException e) {
            // expected
        }
    }

    public void testHashCode() throws MalformedURLException {
        URL testURL1 = new URL("http", "www.apache.org:8080", "test.html#anch");
        URL testURL2 = new URL("http", "www.apache.org:8080", "test.html#anch");
        URL changedURL = new URL("http", "www.apache.org:8082", "test.html#anch");
        TestCase.assertEquals("Assert 0: error in hashCode: not same", testURL1.hashCode(), testURL2.hashCode());
        TestCase.assertFalse("Assert 0: error in hashCode: should be same", ((testURL1.hashCode()) == (changedURL.hashCode())));
    }

    public void testSetURLStreamHandlerFactory() throws IOException, IllegalAccessException, IllegalArgumentException, MalformedURLException {
        URLStreamHandlerFactory factory = new OldURLTest.MyURLStreamHandlerFactory();
        Field streamHandlerFactoryField = null;
        int counter = 0;
        File sampleFile = createTempHelloWorldFile();
        URL fileURL = sampleFile.toURL();
        Field[] fields = URL.class.getDeclaredFields();
        for (Field f : fields) {
            if (URLStreamHandlerFactory.class.equals(f.getType())) {
                counter++;
                streamHandlerFactoryField = f;
            }
        }
        if (counter != 1) {
            TestCase.fail("Error in test setup: not Factory found");
        }
        streamHandlerFactoryField.setAccessible(true);
        URLStreamHandlerFactory old = ((URLStreamHandlerFactory) (streamHandlerFactoryField.get(null)));
        try {
            streamHandlerFactoryField.set(null, factory);
            BufferedReader buf = new BufferedReader(new InputStreamReader(fileURL.openStream()), OldURLTest.helloWorldString.getBytes().length);
            String nextline;
            while ((nextline = buf.readLine()) != null) {
                TestCase.assertEquals(OldURLTest.helloWorldString, nextline);
            } 
            buf.close();
        } finally {
            streamHandlerFactoryField.set(null, old);
        }
    }

    public void testURLString() throws MalformedURLException {
        URL testURL = new URL("ftp://myname@host.dom/etc/motd");
        TestCase.assertEquals("Assert 0: wrong protocol", "ftp", testURL.getProtocol());
        TestCase.assertEquals("Assert 1: wrong host", "host.dom", testURL.getHost());
        TestCase.assertEquals("Assert 2: wrong port", (-1), testURL.getPort());
        TestCase.assertEquals("Assert 3: wrong userInfo", "myname", testURL.getUserInfo());
        TestCase.assertEquals("Assert 4: wrong path", "/etc/motd", testURL.getPath());
        try {
            new URL("ftpmyname@host.dom/etc/motd");
            TestCase.fail("Assert 0: malformed URL should throw exception");
        } catch (MalformedURLException e) {
            // expected
        }
    }

    public void testURLURLString() throws MalformedURLException {
        URL gamelan = new URL("http://www.gamelan.com/pages/");
        URL gamelanNetwork = new URL(gamelan, "Gamelan.net.html");
        URL gamelanNetworkBottom = new URL(gamelanNetwork, "#BOTTOM");
        TestCase.assertEquals("Assert 0: wrong anchor", "BOTTOM", gamelanNetworkBottom.getRef());
        TestCase.assertEquals("Assert 1: wrong protocol", "http", gamelanNetworkBottom.getProtocol());
        // same protocol
        URL gamelanNetworBottom2 = new URL(gamelanNetwork, "http://www.gamelan.com/pages/Gamelan.net.html#BOTTOM");
        TestCase.assertEquals(gamelanNetwork.getProtocol(), gamelanNetworBottom2.getProtocol());
        // changed protocol
        URL gamelanNetworkBottom3 = new URL(gamelanNetwork, "ftp://www.gamelan2.com/pages/Gamelan.net.html#BOTTOM");
        URL absoluteNew = new URL("ftp://www.gamelan2.com/pages/Gamelan.net.html#BOTTOM");
        TestCase.assertEquals("Host of context URL instead of new URL", "ftp", gamelanNetworkBottom3.getProtocol());
        TestCase.assertTrue("URL is context URL instead of new URL", gamelanNetworkBottom3.sameFile(absoluteNew));
        // exception testing
        try {
            u = null;
            u1 = new URL(u, "somefile.java");
            TestCase.fail("didn't throw the expected MalFormedURLException");
        } catch (MalformedURLException e) {
            // ok
        }
        // Non existing protocol
        // exception testing
        try {
            u = new URL(gamelanNetwork, "someFancyNewProt://www.gamelan2.com/pages/Gamelan.net.html#BOTTOM");
            TestCase.assertTrue("someFancyNewProt".equalsIgnoreCase(u.getProtocol()));
        } catch (MalformedURLException e) {
            // ok
        }
    }

    public void testEqualsObject() throws MalformedURLException {
        URL testURL1 = new URL("http", "www.apache.org", 8080, "test.html");
        URL wrongProto = new URL("ftp", "www.apache.org", 8080, "test.html");
        URL wrongPort = new URL("http", "www.apache.org", 8082, "test.html");
        URL wrongHost = new URL("http", "www.apache2.org", 8080, "test.html");
        URL wrongRef = new URL("http", "www.apache.org", 8080, "test2.html#BOTTOM");
        URL testURL2 = new URL("http://www.apache.org:8080/test.html");
        TestCase.assertFalse("Assert 0: error in equals: not same", testURL1.equals(wrongProto));
        TestCase.assertFalse("Assert 1: error in equals: not same", testURL1.equals(wrongPort));
        TestCase.assertFalse("Assert 2: error in equals: not same", testURL1.equals(wrongHost));
        TestCase.assertFalse("Assert 3: error in equals: not same", testURL1.equals(wrongRef));
        TestCase.assertEquals(testURL1, testURL2);
        URL testURL3 = new URL("http", "www.apache.org", "/test.html");
        URL testURL4 = new URL("http://www.apache.org/test.html");
        TestCase.assertTrue("Assert 4: error in equals: same", testURL3.equals(testURL4));
    }

    public void testSameFile() throws MalformedURLException {
        URL gamelan = new URL("file:///pages/index.html");
        URL gamelanFalse = new URL("file:///pages/out/index.html");
        URL gamelanNetwork = new URL(gamelan, "#BOTTOM");
        TestCase.assertTrue(gamelanNetwork.sameFile(gamelan));
        TestCase.assertFalse(gamelanNetwork.sameFile(gamelanFalse));
        // non trivial test
        URL url = new URL("http://web2.javasoft.com/some+file.html");
        URL url1 = new URL("http://web2.javasoft.com/some%20file.html");
        TestCase.assertFalse(url.sameFile(url1));
    }

    public void testGetContent() throws MalformedURLException {
        File sampleFile = createTempHelloWorldFile();
        // read content from file
        URL fileURL = sampleFile.toURL();
        try {
            InputStream output = ((InputStream) (fileURL.getContent()));
            TestCase.assertTrue(((output.available()) > 0));
            // ok
        } catch (Exception e) {
            TestCase.fail("Did not get output type from File URL");
        }
        // Exception test
        URL invalidFile = new URL("file:///nonexistenttestdir/tstfile");
        try {
            invalidFile.getContent();
            TestCase.fail("Access to invalid file worked");
        } catch (IOException e) {
            // ok
        }
    }

    public void testOpenStream() throws IOException, MalformedURLException {
        File sampleFile = createTempHelloWorldFile();
        // read content from file
        URL fileURL = sampleFile.toURL();
        BufferedReader dis = null;
        String inputLine;
        StringBuffer buf = new StringBuffer(32);
        try {
            dis = new BufferedReader(new InputStreamReader(fileURL.openStream()), 32);
            while ((inputLine = dis.readLine()) != null) {
                buf.append(inputLine);
            } 
            dis.close();
        } catch (IOException e) {
            TestCase.fail(("Unexpected error in test setup: " + (e.getMessage())));
        }
        TestCase.assertTrue("Assert 0: Nothing was read from file ", ((buf.length()) > 0));
        TestCase.assertEquals("Assert 1: Wrong stream content", "Hello World", buf.toString());
        // exception test
        URL invalidFile = new URL("file:///nonexistenttestdir/tstfile");
        try {
            dis = new BufferedReader(new InputStreamReader(invalidFile.openStream()), 32);
            while ((inputLine = dis.readLine()) != null) {
                buf.append(inputLine);
            } 
            TestCase.fail("Access to invalid file worked");
        } catch (Exception e) {
            // ok
        } finally {
            dis.close();
        }
    }

    public void testOpenConnection() throws IOException, MalformedURLException {
        File sampleFile = createTempHelloWorldFile();
        byte[] ba;
        InputStream is;
        String s;
        u = sampleFile.toURL();
        u.openConnection();
        is = ((InputStream) (u.getContent(new Class[]{ Object.class })));
        is.read((ba = new byte[4096]));
        s = new String(ba);
        TestCase.assertTrue((("Incorrect content " + (u)) + " does not contain: \"Hello World\""), ((s.indexOf("Hello World")) >= 0));
        try {
            URL u = new URL("https://a.xy.com/index.html");
            URLConnection conn = u.openConnection();
            conn.connect();
            TestCase.fail("Should not be able to read from this site.");
        } catch (IOException e) {
            // ok
        }
    }

    public void testToURI() throws MalformedURLException, URISyntaxException {
        String testHTTPURLString = "http://www.gamelan.com/pages/";
        String testFTPURLString = "ftp://myname@host.dom/etc/motd";
        URL testHTTPURL = new URL(testHTTPURLString);
        URL testFTPURL = new URL(testFTPURLString);
        URI testHTTPURI = testHTTPURL.toURI();
        URI testFTPURI = testFTPURL.toURI();
        TestCase.assertEquals(testHTTPURI.toString(), testHTTPURLString);
        TestCase.assertEquals(testFTPURI.toString(), testFTPURLString);
        // Exception test
        String[] constructorTestsInvalid = new String[]{ "http:///a path#frag"// space char in path, not in escaped
        , // octet form, with no host
        "http://host/a[path#frag"// an illegal char, not in escaped
        , // octet form, should throw an
        // exception
        "http://host/a%path#frag"// invalid escape sequence in path
        , "http://host/a%#frag"// incomplete escape sequence in path
        , "http://host#a frag"// space char in fragment, not in
        , // escaped octet form, no path
        "http://host/a#fr#ag"// illegal char in fragment
        , "http:///path#fr%ag"// invalid escape sequence in fragment,
        , // with no host
        "http://host/path#frag%"// incomplete escape sequence in
        , // fragment
        "http://host/path?a query#frag"// space char in query, not
        , // in escaped octet form
        "http://host?query%ag"// invalid escape sequence in query, no
        , // path
        "http:///path?query%"// incomplete escape sequence in query,
        , // with no host
        "mailto:user^name@fklkf.com"// invalid char in scheme
         };
        for (String malformedURI : Arrays.asList(constructorTestsInvalid)) {
            try {
                URL urlQuery = new URL("http://host/a%path#frag");
                urlQuery.toURI();
                TestCase.fail("Exception expected");
            } catch (URISyntaxException e) {
                // ok
            }
        }
    }

    public void testToString() throws MalformedURLException {
        URL testHTTPURL = new URL("http://www.gamelan.com/pages/");
        URL testFTPURL = new URL("ftp://myname@host.dom/etc/motd");
        TestCase.assertEquals(testHTTPURL.toString(), testHTTPURL.toExternalForm());
        TestCase.assertEquals(testFTPURL.toString(), testFTPURL.toExternalForm());
        TestCase.assertEquals("http://www.gamelan.com/pages/", testHTTPURL.toString());
    }

    public void testToExternalForm() throws MalformedURLException {
        URL testHTTPURL = new URL("http://www.gamelan.com/pages/");
        URL testFTPURL = new URL("ftp://myname@host.dom/etc/motd");
        TestCase.assertEquals(testHTTPURL.toString(), testHTTPURL.toExternalForm());
        TestCase.assertEquals(testFTPURL.toString(), testFTPURL.toExternalForm());
        TestCase.assertEquals("http://www.gamelan.com/pages/", testHTTPURL.toExternalForm());
    }

    public void testGetFile() throws MalformedURLException {
        File sampleFile = createTempHelloWorldFile();
        // read content from file
        URL fileURL = sampleFile.toURL();
        TestCase.assertNotNull(fileURL);
        TestCase.assertEquals(sampleFile.getPath(), fileURL.getFile());
    }

    public void testGetPort() throws MalformedURLException {
        URL testHTTPURL = new URL("http://www.gamelan.com/pages/");
        URL testFTPURL = new URL("ftp://myname@host.dom/etc/motd");
        TestCase.assertEquals((-1), testFTPURL.getPort());
        TestCase.assertEquals((-1), testHTTPURL.getPort());
        URL testHTTPURL8082 = new URL("http://www.gamelan.com:8082/pages/");
        TestCase.assertEquals(8082, testHTTPURL8082.getPort());
    }

    public void testGetProtocol() throws MalformedURLException {
        URL testHTTPURL = new URL("http://www.gamelan.com/pages/");
        URL testHTTPSURL = new URL("https://www.gamelan.com/pages/");
        URL testFTPURL = new URL("ftp://myname@host.dom/etc/motd");
        URL testFile = new URL("file:///pages/index.html");
        URL testJarURL = new URL("jar:file:///bar.jar!/foo.jar!/Bugs/HelloWorld.class");
        TestCase.assertTrue("http".equalsIgnoreCase(testHTTPURL.getProtocol()));
        TestCase.assertTrue("https".equalsIgnoreCase(testHTTPSURL.getProtocol()));
        TestCase.assertTrue("ftp".equalsIgnoreCase(testFTPURL.getProtocol()));
        TestCase.assertTrue("file".equalsIgnoreCase(testFile.getProtocol()));
        TestCase.assertTrue("jar".equalsIgnoreCase(testJarURL.getProtocol()));
    }

    public void testGetRef() throws MalformedURLException {
        URL gamelan = new URL("http://www.gamelan.com/pages/");
        String output = gamelan.getRef();
        TestCase.assertTrue(((output == null) || (output.equals(""))));
        URL gamelanNetwork = new URL(gamelan, "Gamelan.net.html#BOTTOM");
        TestCase.assertEquals("BOTTOM", gamelanNetwork.getRef());
        URL gamelanNetwork2 = new URL("http", "www.gamelan.com", "Gamelan.network.html#BOTTOM");
        TestCase.assertEquals("BOTTOM", gamelanNetwork2.getRef());
    }

    public void testGetQuery() throws MalformedURLException {
        URL urlQuery = new URL("http://www.example.com/index.html?attrib1=value1&attrib2=value&attrib3#anchor");
        URL urlNoQuery = new URL("http://www.example.com/index.html#anchor");
        TestCase.assertEquals("attrib1=value1&attrib2=value&attrib3", urlQuery.getQuery());
        String output = urlNoQuery.getQuery();
        TestCase.assertTrue(((output == null) || ("".equals(output))));
    }

    public void testGetPath() throws MalformedURLException {
        URL url = new URL("http://www.example.com");
        String output = url.getPath();
        TestCase.assertTrue((("".equals(output)) || (output == null)));
        URL url2 = new URL(url, "/foo/index.html");
        TestCase.assertEquals("/foo/index.html", url2.getPath());
    }

    public void testGetUserInfo() throws MalformedURLException {
        URL urlNoUserInfo = new URL("http://www.java2s.com:8080");
        URL url = new URL("ftp://myUser:password@host.dom/etc/motd");
        TestCase.assertEquals("Assert 0: Wrong user", "myUser:password", url.getUserInfo());
        String userInfo = urlNoUserInfo.getUserInfo();
        TestCase.assertTrue((("".equals(userInfo)) || (null == userInfo)));
    }

    public void testGetAuthority() throws MalformedURLException, URISyntaxException {
        // legal authority information userInfo (user,password),domain,port
        URL url = new URL("http://www.java2s.com:8080");
        TestCase.assertEquals("Assert 0: Wrong authority ", "www.java2s.com:8080", url.getAuthority());
        URL ftpURL = new URL("ftp://myname@host.dom/etc/motd");
        TestCase.assertEquals("Assert 1: Wrong authority ", "myname@host.dom", ftpURL.getAuthority());
        URI testURI = new URI("/relative/URI/with/absolute/path/to/resource.txt");
        String output = testURI.getAuthority();
        TestCase.assertTrue((("".equals(output)) || (null == output)));
    }

    public void testGetDefaultPort() throws MalformedURLException {
        URL testHTTPURL = new URL("http://www.gamelan.com/pages/");
        URL testFTPURL = new URL("ftp://myname@host.dom/etc/motd");
        TestCase.assertEquals(21, testFTPURL.getDefaultPort());
        TestCase.assertEquals(80, testHTTPURL.getDefaultPort());
    }

    // start HARMONY branch
    public static class MyHandler extends URLStreamHandler {
        protected URLConnection openConnection(URL u) throws IOException {
            return null;
        }
    }

    URL u;

    URL u1;

    URL u2;

    boolean caught = false;

    static boolean isSelectCalled;

    static class MockProxySelector extends ProxySelector {
        public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
            System.out.println("connection failed");
        }

        public List<Proxy> select(URI uri) {
            OldURLTest.isSelectCalled = true;
            ArrayList<Proxy> proxyList = new ArrayList<Proxy>(1);
            proxyList.add(Proxy.NO_PROXY);
            return proxyList;
        }
    }

    static class MockSecurityManager extends SecurityManager {
        public void checkConnect(String host, int port) {
            if ("127.0.0.1".equals(host)) {
                throw new SecurityException("permission is not allowed");
            }
        }

        public void checkPermission(Permission permission) {
            if ("setSecurityManager".equals(permission.getName())) {
                return;
            }
            super.checkPermission(permission);
        }
    }

    static class MyURLStreamHandler extends URLStreamHandler {
        @Override
        protected URLConnection openConnection(URL arg0) throws IOException {
            try {
                URLConnection con = arg0.openConnection();
                con.setDoInput(true);
                con.connect();
                return con;
            } catch (Throwable e) {
                return null;
            }
        }

        public void parse(URL url, String spec, int start, int end) {
            parseURL(url, spec, start, end);
        }
    }

    static class MyURLStreamHandlerFactory implements URLStreamHandlerFactory {
        public static OldURLTest.MyURLStreamHandler handler = new OldURLTest.MyURLStreamHandler();

        public URLStreamHandler createURLStreamHandler(String arg0) {
            OldURLTest.MyURLStreamHandlerFactory.handler = new OldURLTest.MyURLStreamHandler();
            return OldURLTest.MyURLStreamHandlerFactory.handler;
        }
    }

    /**
     * URLStreamHandler implementation class necessary for tests.
     */
    private class TestURLStreamHandler extends URLStreamHandler {
        public URLConnection openConnection(URL arg0) throws IOException {
            try {
                URLConnection con = arg0.openConnection();
                con.setDoInput(true);
                con.connect();
                return con;
            } catch (Throwable e) {
                return null;
            }
        }

        public URLConnection openConnection(URL arg0, Proxy proxy) throws IOException {
            return super.openConnection(u, proxy);
        }
    }

    public void test_ConstructorLjava_lang_StringLjava_lang_StringILjava_lang_StringLjava_net_URLStreamHandler() throws Exception {
        u = new URL("http", "www.yahoo.com", 8080, "test.html#foo", null);
        TestCase.assertEquals("SSISH1 returns a wrong protocol", "http", u.getProtocol());
        TestCase.assertEquals("SSISH1 returns a wrong host", "www.yahoo.com", u.getHost());
        TestCase.assertEquals("SSISH1 returns a wrong port", 8080, u.getPort());
        TestCase.assertEquals("SSISH1 returns a wrong file", "/test.html", u.getFile());
        TestCase.assertTrue(("SSISH1 returns a wrong anchor: " + (u.getRef())), u.getRef().equals("foo"));
        u = new URL("http", "www.yahoo.com", 8080, "test.html#foo", new OldURLTest.MyHandler());
        TestCase.assertEquals("SSISH2 returns a wrong protocol", "http", u.getProtocol());
        TestCase.assertEquals("SSISH2 returns a wrong host", "www.yahoo.com", u.getHost());
        TestCase.assertEquals("SSISH2 returns a wrong port", 8080, u.getPort());
        TestCase.assertEquals("SSISH2 returns a wrong file", "/test.html", u.getFile());
        TestCase.assertTrue(("SSISH2 returns a wrong anchor: " + (u.getRef())), u.getRef().equals("foo"));
        OldURLTest.TestURLStreamHandler lh = new OldURLTest.TestURLStreamHandler();
        u = new URL("http", "www.yahoo.com", 8080, "test.html#foo", lh);
        try {
            new URL(null, "1", 0, "file", lh);
            TestCase.fail("Exception expected, but nothing was thrown!");
        } catch (MalformedURLException e) {
            // ok
        } catch (NullPointerException e) {
            // Expected NullPointerException
        }
    }

    public void test_getContent_LJavaLangClass() throws Exception {
        File sampleFile = createTempHelloWorldFile();
        byte[] ba;
        String s;
        InputStream is = null;
        try {
            u = new URL("file:///data/tmp/hyts_htmltest.html");
            is = ((InputStream) (u.getContent(new Class[]{ InputStream.class })));
            is.read((ba = new byte[4096]));
            TestCase.fail("No error occurred reading from nonexisting file");
        } catch (IOException e) {
            // ok
        }
        try {
            u = new URL("file:///data/tmp/hyts_htmltest.html");
            is = ((InputStream) (u.getContent(new Class[]{ String.class, InputStream.class })));
            is.read((ba = new byte[4096]));
            TestCase.fail("No error occurred reading from nonexisting file");
        } catch (IOException e) {
            // ok
        }
        // Check for null
        u = sampleFile.toURL();
        u.openConnection();
        TestCase.assertNotNull(u);
        s = ((String) (u.getContent(new Class[]{ String.class })));
        TestCase.assertNull(s);
    }

    public void testURLURLStringURLStreamHandler() throws MalformedURLException {
        u = new URL("http://www.yahoo.com");
        // basic ones
        u1 = new URL(u, "file.java", new OldURLTest.MyHandler());
        TestCase.assertEquals("1 returns a wrong protocol", "http", u1.getProtocol());
        TestCase.assertEquals("1 returns a wrong host", "www.yahoo.com", u1.getHost());
        TestCase.assertEquals("1 returns a wrong port", (-1), u1.getPort());
        TestCase.assertEquals("1 returns a wrong file", "/file.java", u1.getFile());
        TestCase.assertNull("1 returns a wrong anchor", u1.getRef());
        u1 = new URL(u, "systemresource:/+/FILE0/test.java", new OldURLTest.MyHandler());
        TestCase.assertEquals("2 returns a wrong protocol", "systemresource", u1.getProtocol());
        TestCase.assertTrue("2 returns a wrong host", u1.getHost().equals(""));
        TestCase.assertEquals("2 returns a wrong port", (-1), u1.getPort());
        TestCase.assertEquals("2 returns a wrong file", "/+/FILE0/test.java", u1.getFile());
        TestCase.assertNull("2 returns a wrong anchor", u1.getRef());
        u1 = new URL(u, "dir1/dir2/../file.java", null);
        TestCase.assertEquals("3 returns a wrong protocol", "http", u1.getProtocol());
        TestCase.assertEquals("3 returns a wrong host", "www.yahoo.com", u1.getHost());
        TestCase.assertEquals("3 returns a wrong port", (-1), u1.getPort());
        TestCase.assertEquals("3 returns a wrong file", "/dir1/file.java", u1.getFile());
        TestCase.assertNull("3 returns a wrong anchor", u1.getRef());
        // test for question mark processing
        u = new URL("http://www.foo.com/d0/d1/d2/cgi-bin?foo=bar/baz");
        // test for relative file and out of bound "/../" processing
        u1 = new URL(u, "../dir1/dir2/../file.java", new OldURLTest.MyHandler());
        TestCase.assertTrue(("A) returns a wrong file: " + (u1.getFile())), u1.getFile().equals("/d0/d1/dir1/file.java"));
        // test for absolute and relative file processing
        u1 = new URL(u, "/../dir1/dir2/../file.java", null);
        TestCase.assertEquals("B) returns a wrong file", "/dir1/file.java", u1.getFile());
        URL one;
        try {
            one = new URL("http://www.ibm.com");
        } catch (MalformedURLException ex) {
            // Should not happen.
            throw new RuntimeException(ex.getMessage());
        }
        try {
            new URL(one, ((String) (null)), null);
            TestCase.fail("Specifying null spec on URL constructor should throw MalformedURLException");
        } catch (MalformedURLException e) {
            // expected
        }
    }

    public void test_toExternalForm_Relative() throws MalformedURLException {
        String strURL = "http://a/b/c/d;p?q";
        String ref = "?y";
        URL url = new URL(new URL(strURL), ref);
        TestCase.assertEquals("http://a/b/c/d;p?y", url.toExternalForm());
    }

    public void test_toExternalForm_Absolute() throws MalformedURLException {
        String strURL = "http://localhost?name=value";
        URL url = new URL(strURL);
        TestCase.assertEquals(strURL, url.toExternalForm());
        strURL = "http://localhost?name=value/age=12";
        url = new URL(strURL);
        TestCase.assertEquals(strURL, url.toExternalForm());
    }
}

