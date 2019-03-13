/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.web.util;


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Med Belamachi
 */
public class UriUtilsTests {
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Test
    public void encodeScheme() {
        Assert.assertEquals("Invalid encoded result", "foobar+-.", UriUtils.encodeScheme("foobar+-.", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded result", "foo%20bar", UriUtils.encodeScheme("foo bar", UriUtilsTests.CHARSET));
    }

    @Test
    public void encodeUserInfo() {
        Assert.assertEquals("Invalid encoded result", "foobar:", UriUtils.encodeUserInfo("foobar:", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded result", "foo%20bar", UriUtils.encodeUserInfo("foo bar", UriUtilsTests.CHARSET));
    }

    @Test
    public void encodeHost() {
        Assert.assertEquals("Invalid encoded result", "foobar", UriUtils.encodeHost("foobar", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded result", "foo%20bar", UriUtils.encodeHost("foo bar", UriUtilsTests.CHARSET));
    }

    @Test
    public void encodePort() {
        Assert.assertEquals("Invalid encoded result", "80", UriUtils.encodePort("80", UriUtilsTests.CHARSET));
    }

    @Test
    public void encodePath() {
        Assert.assertEquals("Invalid encoded result", "/foo/bar", UriUtils.encodePath("/foo/bar", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded result", "/foo%20bar", UriUtils.encodePath("/foo bar", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded result", "/Z%C3%BCrich", UriUtils.encodePath("/Z\u00fcrich", UriUtilsTests.CHARSET));
    }

    @Test
    public void encodePathSegment() {
        Assert.assertEquals("Invalid encoded result", "foobar", UriUtils.encodePathSegment("foobar", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded result", "%2Ffoo%2Fbar", UriUtils.encodePathSegment("/foo/bar", UriUtilsTests.CHARSET));
    }

    @Test
    public void encodeQuery() {
        Assert.assertEquals("Invalid encoded result", "foobar", UriUtils.encodeQuery("foobar", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded result", "foo%20bar", UriUtils.encodeQuery("foo bar", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded result", "foobar/+", UriUtils.encodeQuery("foobar/+", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded result", "T%C5%8Dky%C5%8D", UriUtils.encodeQuery("T\u014dky\u014d", UriUtilsTests.CHARSET));
    }

    @Test
    public void encodeQueryParam() {
        Assert.assertEquals("Invalid encoded result", "foobar", UriUtils.encodeQueryParam("foobar", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded result", "foo%20bar", UriUtils.encodeQueryParam("foo bar", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded result", "foo%26bar", UriUtils.encodeQueryParam("foo&bar", UriUtilsTests.CHARSET));
    }

    @Test
    public void encodeFragment() {
        Assert.assertEquals("Invalid encoded result", "foobar", UriUtils.encodeFragment("foobar", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded result", "foo%20bar", UriUtils.encodeFragment("foo bar", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded result", "foobar/", UriUtils.encodeFragment("foobar/", UriUtilsTests.CHARSET));
    }

    @Test
    public void encode() {
        Assert.assertEquals("Invalid encoded result", "foo", UriUtils.encode("foo", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded result", "http%3A%2F%2Fexample.com%2Ffoo%20bar", UriUtils.encode("http://example.com/foo bar", UriUtilsTests.CHARSET));
    }

    @Test
    public void decode() {
        Assert.assertEquals("Invalid encoded URI", "", UriUtils.decode("", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded URI", "foobar", UriUtils.decode("foobar", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded URI", "foo bar", UriUtils.decode("foo%20bar", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded URI", "foo+bar", UriUtils.decode("foo%2bbar", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded result", "T\u014dky\u014d", UriUtils.decode("T%C5%8Dky%C5%8D", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded result", "/Z\u00fcrich", UriUtils.decode("/Z%C3%BCrich", UriUtilsTests.CHARSET));
        Assert.assertEquals("Invalid encoded result", "T\u014dky\u014d", UriUtils.decode("T\u014dky\u014d", UriUtilsTests.CHARSET));
    }

    @Test(expected = IllegalArgumentException.class)
    public void decodeInvalidSequence() {
        UriUtils.decode("foo%2", UriUtilsTests.CHARSET);
    }

    @Test
    public void extractFileExtension() {
        Assert.assertEquals("html", UriUtils.extractFileExtension("index.html"));
        Assert.assertEquals("html", UriUtils.extractFileExtension("/index.html"));
        Assert.assertEquals("html", UriUtils.extractFileExtension("/products/view.html"));
        Assert.assertEquals("html", UriUtils.extractFileExtension("/products/view.html#/a"));
        Assert.assertEquals("html", UriUtils.extractFileExtension("/products/view.html#/path/a"));
        Assert.assertEquals("html", UriUtils.extractFileExtension("/products/view.html#/path/a.do"));
        Assert.assertEquals("html", UriUtils.extractFileExtension("/products/view.html#aaa?bbb"));
        Assert.assertEquals("html", UriUtils.extractFileExtension("/products/view.html#aaa.xml?bbb"));
        Assert.assertEquals("html", UriUtils.extractFileExtension("/products/view.html?param=a"));
        Assert.assertEquals("html", UriUtils.extractFileExtension("/products/view.html?param=/path/a"));
        Assert.assertEquals("html", UriUtils.extractFileExtension("/products/view.html?param=/path/a.do"));
        Assert.assertEquals("html", UriUtils.extractFileExtension("/products/view.html?param=/path/a#/path/a"));
        Assert.assertEquals("html", UriUtils.extractFileExtension("/products/view.html?param=/path/a.do#/path/a.do"));
        Assert.assertEquals("html", UriUtils.extractFileExtension("/products;q=11/view.html?param=/path/a.do"));
        Assert.assertEquals("html", UriUtils.extractFileExtension("/products;q=11/view.html;r=22?param=/path/a.do"));
        Assert.assertEquals("html", UriUtils.extractFileExtension("/products;q=11/view.html;r=22;s=33?param=/path/a.do"));
    }
}

