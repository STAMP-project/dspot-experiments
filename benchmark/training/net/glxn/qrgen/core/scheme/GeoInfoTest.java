package net.glxn.qrgen.core.scheme;


import org.junit.Assert;
import org.junit.Test;


public class GeoInfoTest {
    private static final String GEO_INFO = "geo:40.71872,-73.98905,100";

    @Test
    public void testParseString() {
        Assert.assertTrue(((GeoInfo.parse(GeoInfoTest.GEO_INFO).getPoints().size()) == 3));
    }

    @Test
    public void testToString() {
        Assert.assertTrue(GeoInfo.parse(GeoInfoTest.GEO_INFO).toString().equals(GeoInfoTest.GEO_INFO));
    }
}

