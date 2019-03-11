package net.glxn.qrgen.core.scheme;


import org.junit.Assert;
import org.junit.Test;


public class VCardTest {
    @Test
    public void parse() {
        VCard vcard = VCard.parse(("BEGIN:VCARD\n"// 
         + ((((((((("VERSION:3.0\n"// 
         + "N:Cookiemonster\n")// 
         + "ORG:CTV\n")// 
         + "TITLE:monster\n")// 
         + "TEL:0023478324\n")// 
         + "URL:www.sesamestreet.com\n")// 
         + "EMAIL:cookiemonster@sesamestreet.com\n")// 
         + "ADR:Sesamestreet 1\n")// 
         + "NOTE:more cookies, please\n")// 
         + "END:VCARD")));
        Assert.assertEquals("Cookiemonster", vcard.getName());
        Assert.assertEquals("Sesamestreet 1", vcard.getAddress());
        Assert.assertEquals("CTV", vcard.getCompany());
        Assert.assertEquals("cookiemonster@sesamestreet.com", vcard.getEmail());
        Assert.assertEquals("monster", vcard.getTitle());
        Assert.assertEquals("www.sesamestreet.com", vcard.getWebsite());
        Assert.assertEquals("0023478324", vcard.getPhoneNumber());
        Assert.assertEquals("more cookies, please", vcard.getNote());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseNull() {
        VCard.parse(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseEmptyString() {
        VCard.parse("");
    }

    @Test
    public void vcardWithName() {
        VCard vcard = new VCard("Herbert");
        Assert.assertEquals("Herbert", vcard.getName());
    }

    @Test
    public void testToString() {
        VCard vcard = new VCard();
        vcard.setName("Cookiemonster");
        vcard.setAddress("Sesamestreet 1");
        vcard.setCompany("CTV");
        vcard.setEmail("cookiemonster@sesamestreet.com");
        vcard.setTitle("monster");
        vcard.setWebsite("www.sesamestreet.com");
        vcard.setPhoneNumber("0023478324");
        vcard.setNote("more cookies, please");
        Assert.assertEquals(("BEGIN:VCARD\n"// 
         + ((((((((("VERSION:3.0\n"// 
         + "N:Cookiemonster\n")// 
         + "ORG:CTV\n")// 
         + "TITLE:monster\n")// 
         + "TEL:0023478324\n")// 
         + "URL:www.sesamestreet.com\n")// 
         + "EMAIL:cookiemonster@sesamestreet.com\n")// 
         + "ADR:Sesamestreet 1\n")// 
         + "NOTE:more cookies, please\n")// 
         + "END:VCARD")), vcard.toString());
    }
}

