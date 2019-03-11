package net.glxn.qrgen.core.scheme;


import org.junit.Assert;
import org.junit.Test;


public class GooglePlayTest {
    private static final String APP = "de.pawlidi.android";

    private static final String CODE = ("{{{market://details?id=" + (GooglePlayTest.APP)) + "}}}";

    @Test
    public void testParse() {
        Assert.assertTrue(GooglePlay.parse(GooglePlayTest.CODE).getAppPackage().equals(GooglePlayTest.APP));
    }

    @Test
    public void testToString() {
        Assert.assertTrue(GooglePlay.parse(GooglePlayTest.CODE).toString().equals(GooglePlayTest.CODE));
    }
}

