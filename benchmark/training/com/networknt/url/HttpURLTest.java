/**
 * Copyright 2015-2017 Norconex Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.networknt.url;


import org.junit.Assert;
import org.junit.Test;


public class HttpURLTest {
    private final String absURL = "https://www.example.com/a/b/c.html?blah";

    private String s;

    private String t;

    @Test
    public void testKeepProtocolUpperCase() {
        s = "HTTP://www.example.com";
        t = "HTTP://www.example.com";
        Assert.assertEquals(t, new HttpURL(s).toString());
    }

    @Test
    public void testToAbsoluteRelativeToProtocol() {
        s = "//www.relative.com/e/f.html";
        t = "https://www.relative.com/e/f.html";
        Assert.assertEquals(t, HttpURL.toAbsolute(absURL, s));
    }

    @Test
    public void testToAbsoluteRelativeToDomainName() {
        s = "/e/f.html";
        t = "https://www.example.com/e/f.html";
        Assert.assertEquals(t, HttpURL.toAbsolute(absURL, s));
    }

    @Test
    public void testToAbsoluteRelativeToFullPageURL() {
        s = "?name=john";
        t = "https://www.example.com/a/b/c.html?name=john";
        Assert.assertEquals(t, HttpURL.toAbsolute(absURL, s));
    }

    @Test
    public void testToAbsoluteRelativeToLastDirectory() {
        s = "g.html";
        t = "https://www.example.com/a/b/g.html";
        Assert.assertEquals(t, HttpURL.toAbsolute(absURL, s));
    }

    @Test
    public void testToAbsoluteAbsoluteURL() {
        s = "http://www.sample.com/xyz.html";
        t = "http://www.sample.com/xyz.html";
        Assert.assertEquals(t, HttpURL.toAbsolute(absURL, s));
    }

    // Test for issue https://github.com/Norconex/collector-http/issues/225
    @Test
    public void testFromDomainNoTrailSlashToRelativeNoLeadSlash() {
        s = "http://www.sample.com";
        t = "http://www.sample.com/xyz.html";
        Assert.assertEquals(t, HttpURL.toAbsolute(s, "xyz.html"));
    }

    @Test
    public void testHttpProtocolNoPort() {
        s = "http://www.example.com/blah";
        t = "http://www.example.com/blah";
        Assert.assertEquals(t, new HttpURL(s).toString());
    }

    @Test
    public void testHttpProtocolDefaultPort() {
        s = "http://www.example.com:80/blah";
        t = "http://www.example.com/blah";
        Assert.assertEquals(t, new HttpURL(s).toString());
    }

    @Test
    public void testHttpProtocolNonDefaultPort() {
        s = "http://www.example.com:81/blah";
        t = "http://www.example.com:81/blah";
        Assert.assertEquals(t, new HttpURL(s).toString());
    }

    @Test
    public void testHttpsProtocolNoPort() {
        s = "https://www.example.com/blah";
        t = "https://www.example.com/blah";
        Assert.assertEquals(t, new HttpURL(s).toString());
    }

    @Test
    public void testHttpsProtocolDefaultPort() {
        s = "https://www.example.com:443/blah";
        t = "https://www.example.com/blah";
        Assert.assertEquals(t, new HttpURL(s).toString());
    }

    @Test
    public void testHttpsProtocolNonDefaultPort() {
        s = "https://www.example.com:444/blah";
        t = "https://www.example.com:444/blah";
        Assert.assertEquals(t, new HttpURL(s).toString());
    }

    @Test
    public void testNonHttpProtocolNoPort() {
        s = "ftp://ftp.example.com/dir";
        t = "ftp://ftp.example.com/dir";
        Assert.assertEquals(t, new HttpURL(s).toString());
    }

    @Test
    public void testNonHttpProtocolWithPort() {
        s = "ftp://ftp.example.com:20/dir";
        t = "ftp://ftp.example.com:20/dir";
        Assert.assertEquals(t, new HttpURL(s).toString());
    }

    @Test
    public void testInvalidURL() {
        s = "http://www.example.com/\"path\"";
        t = "http://www.example.com/%22path%22";
        Assert.assertEquals(t, new HttpURL(s).toString());
    }

    @Test
    public void testURLWithLeadingTrailingSpaces() {
        s = "  http://www.example.com/path  ";
        t = "http://www.example.com/path";
        Assert.assertEquals(t, new HttpURL(s).toString());
    }

    @Test
    public void testNullOrBlankURLs() {
        s = null;
        t = "";
        Assert.assertEquals(t, new HttpURL(s).toString());
        s = "";
        t = "";
        Assert.assertEquals(t, new HttpURL(s).toString());
        s = "  ";
        t = "";
        Assert.assertEquals(t, new HttpURL(s).toString());
    }

    @Test
    public void testRelativeURLs() {
        s = "./blah";
        t = "./blah";
        Assert.assertEquals(t, new HttpURL(s).toString());
        s = "/blah";
        t = "/blah";
        Assert.assertEquals(t, new HttpURL(s).toString());
        s = "blah?param=value#frag";
        t = "blah?param=value#frag";
        Assert.assertEquals(t, new HttpURL(s).toString());
    }

    @Test
    public void testFileProtocol() {
        // Encode non-URI characters
        s = "file:///etc/some dir/my file.txt";
        t = "file:///etc/some%20dir/my%20file.txt";
        Assert.assertEquals(t, new HttpURL(s).toString());
        s = "file://./dir/another-dir/path";
        t = "file://./dir/another-dir/path";
        Assert.assertEquals(t, new HttpURL(s).toString());
        s = "file://localhost/c:/WINDOWS/??.txt";
        t = "file://localhost/c:/WINDOWS/%C3%A9%C3%A0.txt";
        Assert.assertEquals(t, new HttpURL(s).toString());
        s = "file:///c:/WINDOWS/file.txt";
        t = "file:///c:/WINDOWS/file.txt";
        Assert.assertEquals(t, new HttpURL(s).toString());
        s = "file:/c:/WINDOWS/file.txt";
        t = "file:///c:/WINDOWS/file.txt";
        Assert.assertEquals(t, new HttpURL(s).toString());
    }
}

