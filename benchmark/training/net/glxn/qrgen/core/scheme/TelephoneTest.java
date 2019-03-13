package net.glxn.qrgen.core.scheme;


import org.junit.Assert;
import org.junit.Test;


public class TelephoneTest {
    private static final String TEL = "+1-212-555-1212";

    @Test
    public void testParse() {
        Assert.assertTrue(Telephone.parse(("tel:" + (TelephoneTest.TEL))).getTelephone().equals(TelephoneTest.TEL));
    }

    @Test
    public void testToString() {
        Assert.assertTrue(Telephone.parse(("tel:" + (TelephoneTest.TEL))).toString().equals(("tel:" + (TelephoneTest.TEL))));
    }
}

