package com.intuit.karate.http;


import FileUtils.UTF8;
import StringUtils.Pair;
import com.intuit.karate.Match;
import com.intuit.karate.StringUtils;
import java.util.Arrays;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author pthomas3
 */
public class HttpUtilsTest {
    @Test
    public void testParseContentTypeCharset() {
        Assert.assertEquals(UTF8, HttpUtils.parseContentTypeCharset("application/json; charset=UTF-8"));
        Assert.assertEquals(UTF8, HttpUtils.parseContentTypeCharset("application/json; charset = UTF-8 "));
        Assert.assertEquals(UTF8, HttpUtils.parseContentTypeCharset("application/json; charset=UTF-8; version=1.2.3"));
        Assert.assertEquals(UTF8, HttpUtils.parseContentTypeCharset("application/json; charset = UTF-8 ; version=1.2.3"));
    }

    @Test
    public void testParseContentTypeParams() {
        Map<String, String> map = HttpUtils.parseContentTypeParams("application/json");
        Assert.assertNull(map);
        map = HttpUtils.parseContentTypeParams("application/json; charset=UTF-8");
        Match.equals(map, "{ charset: 'UTF-8' }");
        map = HttpUtils.parseContentTypeParams("application/json; charset = UTF-8 ");
        Match.equals(map, "{ charset: 'UTF-8' }");
        map = HttpUtils.parseContentTypeParams("application/json; charset=UTF-8; version=1.2.3");
        Match.equals(map, "{ charset: 'UTF-8', version: '1.2.3' }");
        map = HttpUtils.parseContentTypeParams("application/json; charset = UTF-8 ; version=1.2.3");
        Match.equals(map, "{ charset: 'UTF-8', version: '1.2.3' }");
        map = HttpUtils.parseContentTypeParams("application/vnd.app.test+json;ton-version=1");
        Match.equals(map, "{ 'ton-version': '1' }");
    }

    @Test
    public void testParseUriPathPatterns() {
        Map<String, String> map = HttpUtils.parseUriPattern("/cats/{id}", "/cats/1");
        Match.equals(map, "{ id: '1' }");
        map = HttpUtils.parseUriPattern("/cats/{id}/", "/cats/1");// trailing slash

        Match.equals(map, "{ id: '1' }");
        map = HttpUtils.parseUriPattern("/cats/{id}", "/cats/1/");// trailing slash

        Match.equals(map, "{ id: '1' }");
        map = HttpUtils.parseUriPattern("/cats/{id}", "/foo/bar");
        Match.equals(map, null);
        map = HttpUtils.parseUriPattern("/cats", "/cats/1");// exact match

        Match.equals(map, null);
        map = HttpUtils.parseUriPattern("/{path}/{id}", "/cats/1");
        Match.equals(map, "{ path: 'cats', id: '1' }");
    }

    @Test
    public void testParseCookieString() {
        String header = "Set-Cookie: foo=\"bar\";Version=1";
        Map<String, Cookie> map = HttpUtils.parseCookieHeaderString(header);
        Match.equals(map, "{ foo: '#object' }");// only one entry

        Match.contains(map.get("foo"), "{ name: 'foo', value: 'bar' }");
    }

    @Test
    public void testCreateCookieString() {
        Cookie c1 = new Cookie("foo", "bar");
        Cookie c2 = new Cookie("hello", "world");
        String header = HttpUtils.createCookieHeaderValue(Arrays.asList(c1, c2));
        Match.equalsText(header, "foo=bar; hello=world");
    }

    @Test
    public void testUriParsing() {
        StringUtils.Pair pair = HttpUtils.parseUriIntoUrlBaseAndPath("http://foo/bar");
        Match.equalsText(pair.left, "http://foo");
        Match.equalsText(pair.right, "/bar");
        pair = HttpUtils.parseUriIntoUrlBaseAndPath("/bar");
        Match.equalsText(pair.left, null);
        Match.equalsText(pair.right, "/bar");
        pair = HttpUtils.parseUriIntoUrlBaseAndPath("/bar?baz=ban");
        Match.equalsText(pair.left, null);
        Match.equalsText(pair.right, "/bar?baz=ban");
        pair = HttpUtils.parseUriIntoUrlBaseAndPath("http://foo/bar?baz=ban");
        Match.equalsText(pair.left, "http://foo");
        Match.equalsText(pair.right, "/bar?baz=ban");
        pair = HttpUtils.parseUriIntoUrlBaseAndPath("localhost:50856");
        Match.equalsText(pair.left, null);
        Match.equalsText(pair.right, "");
        pair = HttpUtils.parseUriIntoUrlBaseAndPath("127.0.0.1:50856");
        Match.equalsText(pair.left, null);
        Match.equalsText(pair.right, "");
    }
}

