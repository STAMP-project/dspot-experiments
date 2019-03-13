/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/**
 * Copyright (c) 2007, 2018, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2017, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.web;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.Assert;
import org.junit.Test;

import static Util.REDACTED_USER_INFO;


/**
 * Test of the methods in <code>org.opengrok.indexer.web.Util</code>.
 */
public class UtilTest {
    private static Locale savedLocale;

    @Test
    public void htmlize() throws IOException {
        String[][] input_output = new String[][]{ new String[]{ "This is a test", "This is a test" }, new String[]{ "Newline\nshould become <br/>", "Newline<br/>should become &lt;br/&gt;" }, new String[]{ "Open & Grok", "Open &amp; Grok" }, new String[]{ "&amp;&lt;&gt;", "&amp;amp;&amp;lt;&amp;gt;" } };
        for (String[] in_out : input_output) {
            // 1 arg
            Assert.assertEquals(in_out[1], Util.htmlize(in_out[0]));
            // 2 args
            StringBuilder sb = new StringBuilder();
            Util.htmlize(in_out[0], sb);
            Assert.assertEquals(in_out[1], sb.toString());
        }
    }

    @Test
    public void breadcrumbPath() {
        Assert.assertEquals(null, Util.breadcrumbPath("/root/", null));
        Assert.assertEquals("", Util.breadcrumbPath("/root/", ""));
        Assert.assertEquals("<a href=\"/root/x\">x</a>", Util.breadcrumbPath("/root/", "x"));
        Assert.assertEquals("<a href=\"/root/xx\">xx</a>", Util.breadcrumbPath("/root/", "xx"));
        // parent directories have a trailing slash in href
        Assert.assertEquals("<a href=\"/r/a/\">a</a>/<a href=\"/r/a/b\">b</a>", Util.breadcrumbPath("/r/", "a/b"));
        // if basename is a dir (ends with file seperator), href link also
        // ends with a '/'
        Assert.assertEquals("<a href=\"/r/a/\">a</a>/<a href=\"/r/a/b/\">b</a>/", Util.breadcrumbPath("/r/", "a/b/"));
        // should work the same way with a '.' as file separator
        Assert.assertEquals(("<a href=\"/r/java/\">java</a>." + ("<a href=\"/r/java/lang/\">lang</a>." + "<a href=\"/r/java/lang/String\">String</a>")), Util.breadcrumbPath("/r/", "java.lang.String", '.'));
        // suffix added to the link?
        Assert.assertEquals("<a href=\"/root/xx&project=y\">xx</a>", Util.breadcrumbPath("/root/", "xx", '/', "&project=y", false));
        // compact: path needs to be resolved to /xx and no link is added
        // for the virtual root directory (parent) but emitted as plain text.
        // Prefix gets just prefixed as is and not mangled wrt. path -> "//"
        Assert.assertEquals("/<a href=\"/root//xx&project=y\">xx</a>", Util.breadcrumbPath("/root/", "../xx", '/', "&project=y", true));
        // relative pathes are resolved wrt. / , so path resolves to /a/c/d
        Assert.assertEquals(("/<a href=\"/r//a/\">a</a>/" + ("<a href=\"/r//a/c/\">c</a>/" + "<a href=\"/r//a/c/d\">d</a>")), Util.breadcrumbPath("/r/", "../a/b/../c//d", '/', "", true));
    }

    @Test
    public void redableSize() {
        Assert.assertEquals("0 ", Util.readableSize(0));
        Assert.assertEquals("1 ", Util.readableSize(1));
        Assert.assertEquals("-1 ", Util.readableSize((-1)));
        Assert.assertEquals("1,000 ", Util.readableSize(1000));
        Assert.assertEquals("1 KiB", Util.readableSize(1024));
        Assert.assertEquals("2.4 KiB", Util.readableSize(2500));
        Assert.assertEquals("<b>1.4 MiB</b>", Util.readableSize(1474560));
        Assert.assertEquals("<b>3.5 GiB</b>", Util.readableSize(3758489600L));
        Assert.assertEquals("<b>8,589,934,592 GiB</b>", Util.readableSize(Long.MAX_VALUE));
    }

    @Test
    public void readableLine() throws Exception {
        StringWriter out = new StringWriter();
        // hmmm - where do meaningful tests start?
        Util.readableLine(42, out, null, null, null, null);
        Assert.assertEquals("\n<a class=\"l\" name=\"42\" href=\"#42\">42</a>", out.toString());
        out.getBuffer().setLength(0);// clear buffer

        Util.readableLine(110, out, null, null, null, null);
        Assert.assertEquals("\n<a class=\"hl\" name=\"110\" href=\"#110\">110</a>", out.toString());
    }

    @Test
    public void path2uid() {
        Assert.assertEquals("\u0000etc\u0000passwd\u0000date", Util.path2uid("/etc/passwd", "date"));
    }

    @Test
    public void fixPathIfWindows() {
        if (Util.isWindows()) {
            Assert.assertEquals("/var/opengrok", Util.fixPathIfWindows("\\var\\opengrok"));
        }
    }

    @Test
    public void uid2url() {
        Assert.assertEquals("/etc/passwd", Util.uid2url(Util.path2uid("/etc/passwd", "date")));
    }

    @Test
    public void URIEncode() {
        Assert.assertEquals("", Util.URIEncode(""));
        Assert.assertEquals("a+b", Util.URIEncode("a b"));
        Assert.assertEquals("a%23b", Util.URIEncode("a#b"));
        Assert.assertEquals("a%2Fb", Util.URIEncode("a/b"));
        Assert.assertEquals("README.txt", Util.URIEncode("README.txt"));
    }

    @Test
    public void URIEncodePath() {
        Assert.assertEquals("", Util.URIEncodePath(""));
        Assert.assertEquals("/", Util.URIEncodePath("/"));
        Assert.assertEquals("a", Util.URIEncodePath("a"));
        Assert.assertEquals("%09", Util.URIEncodePath("\t"));
        Assert.assertEquals("a%2Bb", Util.URIEncodePath("a+b"));
        Assert.assertEquals("a%20b", Util.URIEncodePath("a b"));
        Assert.assertEquals("/a//x/yz/%23%23/%20/%20%3F", Util.URIEncodePath("/a//x/yz/##/ / ?"));
        Assert.assertEquals("foo%3A%3Abar%3A%3Atest.js", Util.URIEncodePath("foo::bar::test.js"));
        Assert.assertEquals("bl%C3%A5b%C3%A6rsyltet%C3%B8y", Util.URIEncodePath("bl\u00e5b\u00e6rsyltet\u00f8y"));
    }

    @Test
    public void formQuoteEscape() {
        Assert.assertEquals("", Util.formQuoteEscape(null));
        Assert.assertEquals("abc", Util.formQuoteEscape("abc"));
        Assert.assertEquals("&quot;abc&quot;", Util.formQuoteEscape("\"abc\""));
        Assert.assertEquals("&amp;aring;", Util.formQuoteEscape("&aring;"));
    }

    @Test
    public void diffline() {
        String[][] tests = new String[][]{ new String[]{ "if (a < b && foo < bar && c > d)", "if (a < b && foo > bar && c > d)", "if (a &lt; b &amp;&amp; foo <span class=\"d\">&lt;</span> bar &amp;&amp; c &gt; d)", "if (a &lt; b &amp;&amp; foo <span class=\"a\">&gt;</span> bar &amp;&amp; c &gt; d)" }, new String[]{ "foo << 1", "foo >> 1", "foo <span class=\"d\">&lt;&lt;</span> 1", "foo <span class=\"a\">&gt;&gt;</span> 1" }, new String[]{ "\"(ses_id, mer_id, pass_id, \" + refCol +\" , mer_ref, amnt, " + ("cur, ps_id, ret_url, d_req_time, d_req_mil, h_resp_time, " + "h_resp_mil) \""), "\"(ses_id, mer_id, pass_id, \" + refCol +\" , mer_ref, amnt, " + ("cur, ps_id, ret_url, exp_url, d_req_time, d_req_mil, " + "h_resp_time, h_resp_mil) \""), "&quot;(ses_id, mer_id, pass_id, &quot; + refCol +&quot; , mer_ref, amnt, " + ("cur, ps_id, ret_url, d_req_time, d_req_mil, h_resp_time, " + "h_resp_mil) &quot;"), "&quot;(ses_id, mer_id, pass_id, &quot; + refCol +&quot; , mer_ref, amnt, " + ("cur, ps_id, ret_url, <span class=\"a\">exp_url, " + "</span>d_req_time, d_req_mil, h_resp_time, h_resp_mil) &quot;") }, new String[]{ "\"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\", values);", "\"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\", values);", "&quot;VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)&quot;, values);", "&quot;VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?<span " + "class=\"a\">, ?</span>)&quot;, values);" }, new String[]{ "char    *config_list = NULL;", "char    **config_list = NULL;", "char    *config_list = NULL;", "char    *<span class=\"a\">*</span>config_list = NULL;" }, new String[]{ "char    **config_list = NULL;", "char    *config_list = NULL;", "char    *<span class=\"d\">*</span>config_list = NULL;", "char    *config_list = NULL;" }, new String[]{ "* An error occured or there is non-numeric stuff at the end", "* An error occurred or there is non-numeric stuff at the end", "* An error occured or there is non-numeric stuff at the end", "* An error occur<span class=\"a\">r</span>ed or there is " + "non-numeric stuff at the end" } };
        for (int i = 0; i < (tests.length); i++) {
            String[] strings = Util.diffline(new StringBuilder(tests[i][0]), new StringBuilder(tests[i][1]));
            Assert.assertEquals(((("" + i) + ",") + 0), tests[i][2], strings[0]);
            Assert.assertEquals(((("" + i) + ",") + 1), tests[i][3], strings[1]);
        }
    }

    @Test
    public void testEncode() {
        String[][] tests = new String[][]{ new String[]{ "Test <code>title</code>", "Test&nbsp;&#60;code&#62;title&#60;/code&#62;" }, new String[]{ "ahoj", "ahoj" }, new String[]{ "<>|&\"\'", "&#60;&#62;|&#38;&#34;&#39;" }, new String[]{ "tab\ttab", "tab&nbsp;&nbsp;&nbsp;&nbsp;tab" }, new String[]{ "multi\nline\t\nstring", "multi&lt;br/&gt;line&nbsp;&nbsp;&nbsp;&nbsp;&lt;br/&gt;string" } };
        for (String[] test : tests) {
            Assert.assertEquals(test[1], Util.encode(test[0]));
        }
    }

    @Test
    public void dumpConfiguration() throws Exception {
        StringBuilder out = new StringBuilder();
        Util.dumpConfiguration(out);
        String s = out.toString();
        // Verify that we got a table.
        Assert.assertTrue(s.startsWith("<table"));
        // Verify that the output is well-formed.
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + s;
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
    }

    @Test
    public void jsStringLiteral() {
        Assert.assertEquals("\"abc\\n\\r\\\"\\\\\"", Util.jsStringLiteral("abc\n\r\"\\"));
    }

    @Test
    public void stripPathPrefix() {
        Assert.assertEquals("/", Util.stripPathPrefix("/", "/"));
        Assert.assertEquals("/abc", Util.stripPathPrefix("/abc", "/abc"));
        Assert.assertEquals("/abc/", Util.stripPathPrefix("/abc", "/abc/"));
        Assert.assertEquals("/abc", Util.stripPathPrefix("/abc/", "/abc"));
        Assert.assertEquals("/abc/", Util.stripPathPrefix("/abc/", "/abc/"));
        Assert.assertEquals("abc", Util.stripPathPrefix("/", "/abc"));
        Assert.assertEquals("abc/def", Util.stripPathPrefix("/", "/abc/def"));
        Assert.assertEquals("def", Util.stripPathPrefix("/abc", "/abc/def"));
        Assert.assertEquals("def", Util.stripPathPrefix("/abc/", "/abc/def"));
        Assert.assertEquals("/abcdef", Util.stripPathPrefix("/abc", "/abcdef"));
        Assert.assertEquals("/abcdef", Util.stripPathPrefix("/abc/", "/abcdef"));
        Assert.assertEquals("def/ghi", Util.stripPathPrefix("/abc", "/abc/def/ghi"));
        Assert.assertEquals("def/ghi", Util.stripPathPrefix("/abc/", "/abc/def/ghi"));
    }

    @Test
    public void testSlider() {
        /* Test if contains all five pages for 55 results paginated by 10 */
        for (int i = 0; i < 10; i++) {
            for (int j = 1; j <= 5; j++) {
                Assert.assertTrue(("Contains page " + j), contains(((">" + j) + "<")));
            }
        }
        Assert.assertFalse("Does not contain page 1", contains(">1<"));
        Assert.assertFalse("Does not contain page 5", contains(">5<"));
        Assert.assertFalse("Does not contain page 1", contains(">1<"));
    }

    @Test
    public void testIsUrl() {
        Assert.assertTrue(Util.isHttpUri("http://www.example.com"));
        Assert.assertTrue(Util.isHttpUri("http://example.com"));
        Assert.assertTrue(Util.isHttpUri("https://example.com"));
        Assert.assertTrue(Util.isHttpUri("https://www.example.com"));
        Assert.assertTrue(Util.isHttpUri("http://www.example.com?param=1&param2=2"));
        Assert.assertTrue(Util.isHttpUri("http://www.example.com/other/page"));
        Assert.assertTrue(Util.isHttpUri("https://www.example.com?param=1&param2=2"));
        Assert.assertTrue(Util.isHttpUri("https://www.example.com/other/page"));
        Assert.assertTrue(Util.isHttpUri("http://www.example.com:80/other/page"));
        Assert.assertTrue(Util.isHttpUri("http://www.example.com:8080/other/page"));
        Assert.assertTrue(Util.isHttpUri("https://www.example.com:80/other/page"));
        Assert.assertTrue(Util.isHttpUri("https://www.example.com:8080/other/page"));
        Assert.assertFalse(Util.isHttpUri("git@github.com:OpenGrok/OpenGrok"));
        Assert.assertFalse(Util.isHttpUri("hg@mercurial.com:OpenGrok/OpenGrok"));
        Assert.assertFalse(Util.isHttpUri("ssh://git@github.com:OpenGrok/OpenGrok"));
        Assert.assertFalse(Util.isHttpUri("ldap://example.com/OpenGrok/OpenGrok"));
        Assert.assertFalse(Util.isHttpUri("smtp://example.com/OpenGrok/OpenGrok"));
    }

    @Test
    public void testRedactUrl() {
        Assert.assertEquals("/foo/bar", Util.redactUrl("/foo/bar"));
        Assert.assertEquals("http://foo/bar?r=xxx", Util.redactUrl("http://foo/bar?r=xxx"));
        Assert.assertEquals((("http://" + (REDACTED_USER_INFO)) + "@foo/bar?r=xxx"), Util.redactUrl("http://user@foo/bar?r=xxx"));
        Assert.assertEquals((("http://" + (REDACTED_USER_INFO)) + "@foo/bar?r=xxx"), Util.redactUrl("http://user:pass@foo/bar?r=xxx"));
    }

    @Test
    public void testLinkify() throws MalformedURLException, URISyntaxException {
        Assert.assertTrue(Util.linkify("http://www.example.com").matches("<a.*?href=\"http://www\\.example\\.com\".*?>.*?</a>"));
        Assert.assertTrue(Util.linkify("https://example.com").matches("<a.*?href=\"https://example\\.com\".*?>.*?</a>"));
        Assert.assertTrue(Util.linkify("http://www.example.com?param=1&param2=2").matches("<a.*?href=\"http://www\\.example\\.com\\?param=1&param2=2\".*?>.*?</a>"));
        Assert.assertTrue(Util.linkify("https://www.example.com:8080/other/page").matches("<a.*?href=\"https://www\\.example\\.com:8080/other/page\".*?>.*?</a>"));
        Assert.assertTrue(contains("target=\"_blank\""));
        Assert.assertTrue(contains("target=\"_blank\""));
        Assert.assertTrue(contains("target=\"_blank\""));
        Assert.assertTrue(contains("target=\"_blank\""));
        Assert.assertFalse(contains("target=\"_blank\""));
        Assert.assertFalse(contains("target=\"_blank\""));
        Assert.assertFalse(contains("target=\"_blank\""));
        Assert.assertFalse(contains("target=\"_blank\""));
        Assert.assertTrue(Util.linkify("git@github.com:OpenGrok/OpenGrok").equals("git@github.com:OpenGrok/OpenGrok"));
        Assert.assertTrue(Util.linkify("hg@mercurial.com:OpenGrok/OpenGrok").equals("hg@mercurial.com:OpenGrok/OpenGrok"));
        Assert.assertTrue(Util.linkify("ssh://git@github.com:OpenGrok/OpenGrok").equals("ssh://git@github.com:OpenGrok/OpenGrok"));
        Assert.assertTrue(Util.linkify("ldap://example.com/OpenGrok/OpenGrok").equals("ldap://example.com/OpenGrok/OpenGrok"));
        Assert.assertTrue(Util.linkify("smtp://example.com/OpenGrok/OpenGrok").equals("smtp://example.com/OpenGrok/OpenGrok"));
        Assert.assertTrue(Util.linkify("just some crazy text").equals("just some crazy text"));
        // escaping url
        Assert.assertTrue(Util.linkify("http://www.example.com/\"quotation\"/else").contains((("href=\"" + (Util.encodeURL("http://www.example.com/\"quotation\"/else"))) + "\"")));
        Assert.assertTrue(Util.linkify("https://example.com/><\"").contains((("href=\"" + (Util.encodeURL("https://example.com/><\""))) + "\"")));
        Assert.assertTrue(Util.linkify("http://www.example.com?param=1&param2=2&param3=\"quoted>\"").contains((("href=\"" + (Util.encodeURL("http://www.example.com?param=1&param2=2&param3=\"quoted>\""))) + "\"")));
        // escaping titles
        Assert.assertTrue(Util.linkify("http://www.example.com/\"quotation\"/else").contains((("title=\"Link to " + (Util.encode("http://www.example.com/\"quotation\"/else"))) + "\"")));
        Assert.assertTrue(Util.linkify("https://example.com/><\"").contains((("title=\"Link to " + (Util.encode("https://example.com/><\""))) + "\"")));
        Assert.assertTrue(Util.linkify("http://www.example.com?param=1&param2=2&param3=\"quoted>\"").contains((("title=\"Link to " + (Util.encode("http://www.example.com?param=1&param2=2&param3=\"quoted>\""))) + "\"")));
    }

    @Test
    public void testBuildLink() throws MalformedURLException, URISyntaxException {
        Assert.assertEquals("<a href=\"http://www.example.com\">link</a>", Util.buildLink("link", "http://www.example.com"));
        Assert.assertEquals("<a href=\"http://www.example.com?url=xasw&beta=gama\">link</a>", Util.buildLink("link", "http://www.example.com?url=xasw&beta=gama"));
        String link = Util.buildLink("link", "http://www.example.com", true);
        Assert.assertTrue(link.contains("href=\"http://www.example.com\""));
        Assert.assertTrue(link.contains("target=\"_blank\""));
        link = Util.buildLink("link", "http://www.example.com?url=xasw&beta=gama", true);
        Assert.assertTrue(link.contains("href=\"http://www.example.com?url=xasw&beta=gama\""));
        Assert.assertTrue(link.contains("target=\"_blank\""));
        Map<String, String> attrs = new TreeMap<>();
        attrs.put("href", "https://www.example.com/abcd/acbd");
        attrs.put("title", "Some important title");
        attrs.put("data-id", "123456");
        link = Util.buildLink("link", attrs);
        Assert.assertTrue(link.contains("href=\"https://www.example.com/abcd/acbd\""));
        Assert.assertTrue(link.contains("title=\"Some important title\""));
        Assert.assertTrue(link.contains("data-id=\"123456\""));
    }

    @Test(expected = MalformedURLException.class)
    public void testBuildLinkInvalidUrl1() throws MalformedURLException, URISyntaxException {
        Util.buildLink("link", "www.example.com");// invalid protocol

    }

    @Test(expected = URISyntaxException.class)
    public void testBuildLinkInvalidUrl2() throws MalformedURLException, URISyntaxException {
        Util.buildLink("link", "http://www.exa\"mp\"le.com");// invalid authority

    }

    @Test
    public void testLinkifyPattern() {
        String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " + (((((("sed do eiusmod tempor incididunt as per 12345698 ut labore et dolore magna " + "aliqua. bug3333fff Ut enim ad minim veniam, quis nostrud exercitation ") + "ullamco laboris nisi ut aliquip ex ea introduced in 9791216541 commodo consequat. ") + "Duis aute irure dolor in reprehenderit in voluptate velit ") + "esse cillum dolore eu fixes 132469187 fugiat nulla pariatur. Excepteur sint ") + "occaecat bug6478abc cupidatat non proident, sunt in culpa qui officia ") + "deserunt mollit anim id est laborum.");
        String expected = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " + ((((((((("sed do eiusmod tempor incididunt as per " + "<a href=\"http://www.example.com?bug=12345698\" target=\"_blank\">12345698</a> ut labore et dolore magna ") + "aliqua. bug3333fff Ut enim ad minim veniam, quis nostrud exercitation ") + "ullamco laboris nisi ut aliquip ex ea introduced in ") + "<a href=\"http://www.example.com?bug=9791216541\" target=\"_blank\">9791216541</a> commodo consequat. ") + "Duis aute irure dolor in reprehenderit in voluptate velit ") + "esse cillum dolore eu fixes ") + "<a href=\"http://www.example.com?bug=132469187\" target=\"_blank\">132469187</a> fugiat nulla pariatur. Excepteur sint ") + "occaecat bug6478abc cupidatat non proident, sunt in culpa qui officia ") + "deserunt mollit anim id est laborum.");
        String expected2 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " + (((((((("sed do eiusmod tempor incididunt as per 12345698 ut labore et dolore magna " + "aliqua. ") + "<a href=\"http://www.other-example.com?bug=3333\" target=\"_blank\">bug3333fff</a> Ut enim ad minim veniam, quis nostrud exercitation ") + "ullamco laboris nisi ut aliquip ex ea introduced in 9791216541 commodo consequat. ") + "Duis aute irure dolor in reprehenderit in voluptate velit ") + "esse cillum dolore eu fixes 132469187 fugiat nulla pariatur. Excepteur sint ") + "occaecat ") + "<a href=\"http://www.other-example.com?bug=6478\" target=\"_blank\">bug6478abc</a> cupidatat non proident, sunt in culpa qui officia ") + "deserunt mollit anim id est laborum.");
        Assert.assertEquals(expected, Util.linkifyPattern(text, Pattern.compile("\\b([0-9]{8,})\\b"), "$1", "http://www.example.com?bug=$1"));
        Assert.assertEquals(expected2, Util.linkifyPattern(text, Pattern.compile("\\b(bug([0-9]{4})\\w{3})\\b"), "$1", "http://www.other-example.com?bug=$2"));
    }

    @Test
    public void testCompleteUrl() {
        HttpServletRequest req = new DummyHttpServletRequest() {
            @Override
            public int getServerPort() {
                return 8080;
            }

            @Override
            public String getServerName() {
                return "www.example.com";
            }

            @Override
            public String getScheme() {
                return "http";
            }

            @Override
            public StringBuffer getRequestURL() {
                return new StringBuffer(((((((getScheme()) + "://") + (getServerName())) + ':') + (getServerPort())) + "/source/location/undefined"));
            }
        };
        /**
         * Absolute including hostname.
         */
        Assert.assertEquals("http://opengrok.com/user=", Util.completeUrl("http://opengrok.com/user=", req));
        Assert.assertEquals("http://opengrok.cz.grok.com/user=", Util.completeUrl("http://opengrok.cz.grok.com/user=", req));
        Assert.assertEquals("http://opengrok.com/user=123&id=", Util.completeUrl("http://opengrok.com/user=123&id=", req));
        /**
         * Absolute/relative without the hostname.
         */
        Assert.assertEquals("http://www.example.com:8080/cgi-bin/user=", Util.completeUrl("/cgi-bin/user=", req));
        Assert.assertEquals("http://www.example.com:8080/cgi-bin/user=123&id=", Util.completeUrl("/cgi-bin/user=123&id=", req));
        Assert.assertEquals("http://www.example.com:8080/source/location/undefined/cgi-bin/user=", Util.completeUrl("cgi-bin/user=", req));
        Assert.assertEquals("http://www.example.com:8080/source/location/undefined/cgi-bin/user=123&id=", Util.completeUrl("cgi-bin/user=123&id=", req));
        Assert.assertEquals("http://www.example.com:8080/source/location/undefined", Util.completeUrl("", req));
        /**
         * Escaping should work.
         */
        Assert.assertEquals("http://www.example.com:8080/cgi-%22bin/user=123&id=", Util.completeUrl("/cgi-\"bin/user=123&id=", req));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getQueryParamsNullTest() {
        Util.getQueryParams(null);
    }

    @Test
    public void getQueryParamsEmptyTest() throws MalformedURLException {
        URL url = new URL("http://test.com/test");
        Assert.assertTrue(Util.getQueryParams(url).isEmpty());
    }

    @Test
    public void getQueryParamsEmptyTest2() throws MalformedURLException {
        URL url = new URL("http://test.com/test?");
        Assert.assertTrue(Util.getQueryParams(url).isEmpty());
    }

    @Test
    public void getQueryParamsSingleTest() throws MalformedURLException {
        URL url = new URL("http://test.com?param1=value1");
        Map<String, List<String>> params = Util.getQueryParams(url);
        Assert.assertEquals(1, params.size());
        Assert.assertThat(params.get("param1"), contains("value1"));
    }

    @Test
    public void getQueryParamsMultipleTest() throws MalformedURLException {
        URL url = new URL("http://test.com?param1=value1&param2=value2");
        Map<String, List<String>> params = Util.getQueryParams(url);
        Assert.assertEquals(2, params.size());
        Assert.assertThat(params.get("param1"), contains("value1"));
        Assert.assertThat(params.get("param2"), contains("value2"));
    }

    @Test
    public void getQueryParamsMultipleSameTest() throws MalformedURLException {
        URL url = new URL("http://test.com?param1=value1&param1=value2");
        Map<String, List<String>> params = Util.getQueryParams(url);
        Assert.assertEquals(1, params.size());
        Assert.assertThat(params.get("param1"), contains("value1", "value2"));
    }

    @Test
    public void getQueryParamsEncodedTest() throws MalformedURLException {
        URL url = new URL("http://test.com?param1=%3Fvalue%3F");
        Map<String, List<String>> params = Util.getQueryParams(url);
        Assert.assertEquals(1, params.size());
        Assert.assertThat(params.get("param1"), contains("?value?"));
    }

    @Test
    public void getQueryParamsEmptyValueTest() throws MalformedURLException {
        URL url = new URL("http://test.com?param1=");
        Map<String, List<String>> params = Util.getQueryParams(url);
        Assert.assertThat(params.get("param1"), contains(""));
    }

    @Test
    public void getQueryParamsEmptyAndNormalValuesCombinedTest() throws MalformedURLException {
        URL url = new URL("http://test.com?param1=value1&param2=&param3&param4=value4");
        Map<String, List<String>> params = Util.getQueryParams(url);
        Assert.assertThat(params.get("param1"), contains("value1"));
        Assert.assertThat(params.get("param2"), contains(""));
        Assert.assertTrue(params.containsKey("param3"));
        Assert.assertThat(params.get("param4"), contains("value4"));
    }
}

