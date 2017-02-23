

package com.twilio.converter;


/**
 * Test class for {@link Promoter}
 */
public class AmplPromoterTest {
    @org.junit.Test
    public void testPromoteUri() {
        java.net.URI uri = com.twilio.converter.Promoter.uriFromString("https://trunking.twilio.com/v1/Trunks/TK123/OriginationUrls");
        org.junit.Assert.assertEquals("https://trunking.twilio.com/v1/Trunks/TK123/OriginationUrls", uri.toString());
    }

    @org.junit.Test
    public void testPromoteList() {
        java.lang.String s = "hi";
        org.junit.Assert.assertEquals(com.google.common.collect.Lists.newArrayList(s), com.twilio.converter.Promoter.listOfOne(s));
    }
}

