package net.glxn.qrgen.core.scheme;


import org.junit.Assert;
import org.junit.Test;


public class MeCardTest {
    private static final String MECARD = "MECARD:N:Owen,Sean;ADR:76 9th Avenue, 4th Floor, New York, NY 10011;TEL:12125551212;EMAIL:srowen@example.com;;";

    @Test
    public void testParse() {
        MeCard meCard = MeCard.parse(MeCardTest.MECARD);
        Assert.assertEquals("Owen,Sean", meCard.getName());
        Assert.assertEquals("76 9th Avenue, 4th Floor, New York, NY 10011", meCard.getAddress());
        Assert.assertEquals("12125551212", meCard.getTelephone());
        Assert.assertEquals("srowen@example.com", meCard.getEmail());
    }

    @Test
    public void testToString() {
        MeCard meCard = new MeCard();
        meCard.setName("Owen,Sean");
        meCard.setAddress("76 9th Avenue, 4th Floor, New York, NY 10011");
        meCard.setTelephone("12125551212");
        meCard.setEmail("srowen@example.com");
        Assert.assertEquals(MeCardTest.MECARD, meCard.toString());
    }
}

