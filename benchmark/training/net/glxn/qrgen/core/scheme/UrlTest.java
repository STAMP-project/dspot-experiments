package net.glxn.qrgen.core.scheme;


import org.junit.Assert;
import org.junit.Test;


public class UrlTest {
    private static final String URL = "http://github.com/kenglxn/QRGen";

    @Test
    public void testParseString() {
        Assert.assertTrue(Url.parse(UrlTest.URL).getUrl().equals(UrlTest.URL));
    }

    @Test
    public void testToString() {
        Assert.assertTrue(Url.parse(UrlTest.URL).toString().equals(UrlTest.URL));
    }
}

