package net.glxn.qrgen.core.scheme;


import org.junit.Assert;
import org.junit.Test;


public class EMailTest {
    private static final String MAIL = "email@address.com";

    @Test
    public void testParse() {
        Assert.assertTrue(EMail.parse(("mailto:" + (EMailTest.MAIL))).getEmail().equals(EMailTest.MAIL));
    }

    @Test
    public void testToString() {
        Assert.assertTrue(EMail.parse(("mailto:" + (EMailTest.MAIL))).toString().equals(("mailto:" + (EMailTest.MAIL))));
    }
}

