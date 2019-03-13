package org.elasticsearch.hadoop.util.encoding;


import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HttpEncodingToolsTest {
    @Test
    public void encodeUri() throws Exception {
        Assert.assertThat(HttpEncodingTools.encodeUri("http://127.0.0.1:9200/t?st/typ?/_search?x=x;x&y=y:?&z=z|?"), CoreMatchers.is("http://127.0.0.1:9200/t%C3%A9st/typ%C3%A9/_search?x=x;x&y=y:%C2%A5&z=z%7C%CE%A9"));
    }

    @Test
    public void encodePath() throws Exception {
        Assert.assertThat(HttpEncodingTools.encodePath(""), CoreMatchers.is(""));
        Assert.assertThat(HttpEncodingTools.encodePath("/a"), CoreMatchers.is("/a"));
        Assert.assertThat(HttpEncodingTools.encodePath("/a/b"), CoreMatchers.is("/a/b"));
        Assert.assertThat(HttpEncodingTools.encodePath("/a/b/_c"), CoreMatchers.is("/a/b/_c"));
        Assert.assertThat(HttpEncodingTools.encodePath("/????/???????/_search"), CoreMatchers.is("/%C3%A5%E2%88%AB%C3%A7%E2%88%82/%CB%9A%C2%AC%C2%B5%C3%B1%C3%B8%CF%80%C5%93/_search"));
    }

    @Test
    public void encode() throws Exception {
        Assert.assertThat(HttpEncodingTools.encode("?x=x&y=y|?&z=?"), CoreMatchers.is("%3Fx%3Dx%26y%3Dy%7C%C2%A5%26z%3D%CE%A9"));
    }

    @Test
    public void decode() throws Exception {
        Assert.assertThat(HttpEncodingTools.decode("http://127.0.0.1:9200/%C3%A5%E2%88%AB%C3%A7%E2%88%82/%CB%9A%C2%AC%C2%B5%C3%B1%C3%B8%CF%80%C5%93/_search?x=x&y=y%7C%C2%A5&z=%CE%A9"), CoreMatchers.is("http://127.0.0.1:9200/????/???????/_search?x=x&y=y|?&z=?"));
    }

    @Test
    public void testMultiAmpersandEscapeSimple() {
        Assert.assertThat(HttpEncodingTools.concatenateAndUriEncode(Arrays.asList("&a", "$b", "#c", "!d", "/e", ":f"), ","), CoreMatchers.is("%26a,%24b,%23c,%21d,%2Fe,%3Af"));
    }

    @Test
    public void tokenizeAndUriDecode() throws Exception {
        Assert.assertThat(HttpEncodingTools.tokenizeAndUriDecode("%26a,%24b,%23c,%21d,%2Fe,%3Af", ","), Matchers.containsInAnyOrder("&a", "$b", "#c", "!d", "/e", ":f"));
    }

    @Test
    public void testSingleAmpersandEscape() {
        String uri = HttpEncodingTools.encode("&c");
        Assert.assertThat(uri, CoreMatchers.is("%26c"));
    }

    @Test
    public void testEscapePercent() {
        String uri = HttpEncodingTools.encode("%s");
        Assert.assertThat(uri, CoreMatchers.is("%25s"));
    }
}

