package org.mockserver.url;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class URLParserTest {
    @Test
    public void shouldDetectPath() {
        // isn't path
        Assert.assertTrue(URLParser.isFullUrl("http://www.mock-server.com/some/path"));
        Assert.assertTrue(URLParser.isFullUrl("https://www.mock-server.com/some/path"));
        Assert.assertTrue(URLParser.isFullUrl("//www.mock-server.com/some/path"));
        // is path
        Assert.assertFalse(URLParser.isFullUrl(null));
        Assert.assertFalse(URLParser.isFullUrl("/some/path"));
        Assert.assertFalse(URLParser.isFullUrl("some/path"));
    }

    @Test
    public void shouldReturnPath() {
        Assert.assertThat(URLParser.returnPath("http://www.mock-server.com/some/path"), Is.is("/some/path"));
        Assert.assertThat(URLParser.returnPath("https://www.mock-server.com/some/path"), Is.is("/some/path"));
        Assert.assertThat(URLParser.returnPath("https://www.abc123.com/some/path"), Is.is("/some/path"));
        Assert.assertThat(URLParser.returnPath("https://www.abc.123.com/some/path"), Is.is("/some/path"));
        Assert.assertThat(URLParser.returnPath("http://Administrator:password@192.168.50.70:8091/some/path"), Is.is("/some/path"));
        Assert.assertThat(URLParser.returnPath("https://Administrator:password@www.abc.123.com/some/path"), Is.is("/some/path"));
        Assert.assertThat(URLParser.returnPath("//www.abc.123.com/some/path"), Is.is("/some/path"));
        Assert.assertThat(URLParser.returnPath("//Administrator:password@www.abc.123.com/some/path"), Is.is("/some/path"));
        Assert.assertThat(URLParser.returnPath("/some/path"), Is.is("/some/path"));
        Assert.assertThat(URLParser.returnPath("/123/456"), Is.is("/123/456"));
    }

    @Test
    public void shouldStripQueryString() {
        Assert.assertThat(URLParser.returnPath("http://www.mock-server.com/some/path?foo=bar"), Is.is("/some/path"));
        Assert.assertThat(URLParser.returnPath("https://www.mock-server.com/some/path?foo=bar&bar=foo"), Is.is("/some/path"));
        Assert.assertThat(URLParser.returnPath("https://www.abc123.com/some/path?foo=foo%3Dbar%26bar%3Dfoo%26bar%3Dfoo"), Is.is("/some/path"));
        Assert.assertThat(URLParser.returnPath("https://www.abc.123.com/some/path?foo=bar&bar=foo&bar=foo"), Is.is("/some/path"));
        Assert.assertThat(URLParser.returnPath("http://Administrator:password@192.168.50.70:8091/some/path?foo=bar&bar=foo"), Is.is("/some/path"));
        Assert.assertThat(URLParser.returnPath("https://Administrator:password@www.abc.123.com/some/path?foo=bar"), Is.is("/some/path"));
        Assert.assertThat(URLParser.returnPath("//www.abc.123.com/some/path?foo=bar"), Is.is("/some/path"));
        Assert.assertThat(URLParser.returnPath("//Administrator:password@www.abc.123.com/some/path?foo=foo%3Dbar%26bar%3Dfoo%26bar%3Dfoo&foo=foo%3Dbar%26bar%3Dfoo%26bar%3Dfoo"), Is.is("/some/path"));
        Assert.assertThat(URLParser.returnPath("/some/path?foo=foo%3Dbar%26bar%3Dfoo%26bar%3Dfoo&foo=foo%3Dbar%26bar%3Dfoo%26bar%3Dfoo"), Is.is("/some/path"));
        Assert.assertThat(URLParser.returnPath("/123/456%3Ffoo%3Dbar%26bar%3Dfoo%26bar%3Dfoo"), Is.is("/123/456%3Ffoo%3Dbar%26bar%3Dfoo%26bar%3Dfoo"));
    }
}

