package net.glxn.qrgen.core.scheme;


import Encoding.UTF_8;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashSet;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ExtendableQRCodeSchemeParserTest {
    @Test
    public void getSupportedSchemes() {
        Set<Class<?>> expectedTypes = new LinkedHashSet<Class<?>>();
        expectedTypes.add(Girocode.class);
        expectedTypes.add(VCard.class);
        expectedTypes.add(Wifi.class);
        expectedTypes.add(BizCard.class);
        expectedTypes.add(EMail.class);
        expectedTypes.add(EnterpriseWifi.class);
        expectedTypes.add(GeoInfo.class);
        expectedTypes.add(GooglePlay.class);
        expectedTypes.add(ICal.class);
        expectedTypes.add(KddiAu.class);
        expectedTypes.add(MeCard.class);
        expectedTypes.add(MMS.class);
        expectedTypes.add(SMS.class);
        expectedTypes.add(Telephone.class);
        expectedTypes.add(Url.class);
        expectedTypes.add(Foo.class);
        Assert.assertEquals(expectedTypes, createParser().getSupportedSchemes());
    }

    @Test
    public void parseWifi() throws Exception {
        Object scheme = createParser().parse("WIFI:S:some weird SSID;T:WPA;P:aintNoSecret;H:true;");
        Assert.assertNotNull(scheme);
        Assert.assertThat(scheme, CoreMatchers.is(Wifi.class));
        Wifi wifi = ((Wifi) (scheme));
        Assert.assertEquals("some weird SSID", wifi.getSsid());
        Assert.assertEquals("WPA", wifi.getAuthentication());
        Assert.assertEquals("aintNoSecret", wifi.getPsk());
        Assert.assertEquals(true, wifi.isHidden());
    }

    @Test
    public void parseVCard() throws Exception {
        Object scheme = createParser().parse(("BEGIN:VCARD\n"// 
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
        Assert.assertNotNull(scheme);
        Assert.assertThat(scheme, CoreMatchers.is(VCard.class));
        VCard vcard = ((VCard) (scheme));
        Assert.assertEquals("Cookiemonster", vcard.getName());
        Assert.assertEquals("Sesamestreet 1", vcard.getAddress());
        Assert.assertEquals("CTV", vcard.getCompany());
        Assert.assertEquals("cookiemonster@sesamestreet.com", vcard.getEmail());
        Assert.assertEquals("monster", vcard.getTitle());
        Assert.assertEquals("www.sesamestreet.com", vcard.getWebsite());
        Assert.assertEquals("0023478324", vcard.getPhoneNumber());
        Assert.assertEquals("more cookies, please", vcard.getNote());
    }

    @Test
    public void parseGirocode() throws Exception {
        Object scheme = createParser().parse(("BCD\n"// 
         + (((((((((("001\n"// 
         + "1\n")// 
         + "SCT\n")// 
         + "DAAABCDGGD\n")// 
         + "Miss Marple\n")// 
         + "DE91300776014444814989\n")// 
         + "EUR27.06\n")// 
         + "xyz\n")// 
         + "reference\n")// 
         + "for a good prupose\n")// 
         + "Watch this Girocode :-)")));
        Assert.assertNotNull(scheme);
        Assert.assertThat(scheme, CoreMatchers.is(Girocode.class));
        Girocode girocode = ((Girocode) (scheme));
        Assert.assertEquals(UTF_8, girocode.getEncoding());
        Assert.assertEquals("DAAABCDGGD", girocode.getBic());
        Assert.assertEquals("Miss Marple", girocode.getName());
        Assert.assertEquals("DE91300776014444814989", girocode.getIban());
        Assert.assertEquals("EUR27.06", girocode.getAmount());
        Assert.assertEquals("xyz", girocode.getPurposeCode());
        Assert.assertEquals("reference", girocode.getReference());
        Assert.assertEquals("for a good prupose", girocode.getText());
        Assert.assertEquals("Watch this Girocode :-)", girocode.getHint());
    }

    @Test
    public void parseUrlCode() throws Exception {
        Object scheme = createParser().parse("http://www.github.org/QRCode");
        Assert.assertNotNull(scheme);
        Assert.assertThat(scheme, CoreMatchers.is(Url.class));
        Url urlCode = ((Url) (scheme));
        Assert.assertEquals("http://www.github.org/QRCode", urlCode.getUrl());
    }

    @Test(expected = UnsupportedEncodingException.class)
    public void parseUnknownScheme() throws Exception {
        Object o = createParser().parse("xx");
        System.out.println(o);
    }

    @Test
    public void useParserExtension() throws Exception {
        Object scheme = createParser().parse("foo:bar");
        Assert.assertNotNull(scheme);
        Assert.assertThat(scheme, CoreMatchers.is(Foo.class));
    }
}

