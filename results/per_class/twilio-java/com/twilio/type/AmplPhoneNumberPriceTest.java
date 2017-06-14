

package com.twilio.type;


/**
 * Test class for {@link PhoneNumberPrice}.
 */
public class AmplPhoneNumberPriceTest extends com.twilio.type.TypeTest {
    @org.junit.Test
    public void testFromJson() throws java.io.IOException {
        java.lang.String json = "{\n" + ((("    \"base_price\": 1.00,\n" + "    \"current_price\": 2.00,\n") + "    \"type\": \"mobile\"\n") + "}");
        com.twilio.type.PhoneNumberPrice pnp = fromJson(json, com.twilio.type.PhoneNumberPrice.class);
        org.junit.Assert.assertEquals(1.0, pnp.getBasePrice(), 0.0);
        org.junit.Assert.assertEquals(2.0, pnp.getCurrentPrice(), 0.0);
        org.junit.Assert.assertEquals(com.twilio.type.PhoneNumberPrice.Type.MOBILE, pnp.getType());
    }

    /* amplification of com.twilio.type.PhoneNumberPriceTest#testFromJson */
    @org.junit.Test(timeout = 10000)
    public void testFromJson_cf39() throws java.io.IOException {
        java.lang.String json = "{\n" + ((("    \"base_price\": 1.00,\n" + "    \"current_price\": 2.00,\n") + "    \"type\": \"mobile\"\n") + "}");
        com.twilio.type.PhoneNumberPrice pnp = fromJson(json, com.twilio.type.PhoneNumberPrice.class);
        org.junit.Assert.assertEquals(1.0, pnp.getBasePrice(), 0.0);
        org.junit.Assert.assertEquals(2.0, pnp.getCurrentPrice(), 0.0);
        // AssertGenerator replace invocation
        java.lang.String o_testFromJson_cf39__8 = // StatementAdderMethod cloned existing statement
pnp.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testFromJson_cf39__8, "PhoneNumberPrice{base_price=1.0, current_price=2.0, type=mobile}");
        org.junit.Assert.assertEquals(com.twilio.type.PhoneNumberPrice.Type.MOBILE, pnp.getType());
    }

    /* amplification of com.twilio.type.PhoneNumberPriceTest#testFromJson */
    @org.junit.Test(timeout = 10000)
    public void testFromJson_cf23() throws java.io.IOException {
        java.lang.String json = "{\n" + ((("    \"base_price\": 1.00,\n" + "    \"current_price\": 2.00,\n") + "    \"type\": \"mobile\"\n") + "}");
        com.twilio.type.PhoneNumberPrice pnp = fromJson(json, com.twilio.type.PhoneNumberPrice.class);
        org.junit.Assert.assertEquals(1.0, pnp.getBasePrice(), 0.0);
        org.junit.Assert.assertEquals(2.0, pnp.getCurrentPrice(), 0.0);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_3 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testFromJson_cf23__10 = // StatementAdderMethod cloned existing statement
pnp.equals(vc_3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testFromJson_cf23__10);
        org.junit.Assert.assertEquals(com.twilio.type.PhoneNumberPrice.Type.MOBILE, pnp.getType());
    }

    /* amplification of com.twilio.type.PhoneNumberPriceTest#testFromJson */
    @org.junit.Test(timeout = 10000)
    public void testFromJson_cf22() throws java.io.IOException {
        java.lang.String json = "{\n" + ((("    \"base_price\": 1.00,\n" + "    \"current_price\": 2.00,\n") + "    \"type\": \"mobile\"\n") + "}");
        com.twilio.type.PhoneNumberPrice pnp = fromJson(json, com.twilio.type.PhoneNumberPrice.class);
        org.junit.Assert.assertEquals(1.0, pnp.getBasePrice(), 0.0);
        org.junit.Assert.assertEquals(2.0, pnp.getCurrentPrice(), 0.0);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        // AssertGenerator replace invocation
        boolean o_testFromJson_cf22__10 = // StatementAdderMethod cloned existing statement
pnp.equals(vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testFromJson_cf22__10);
        org.junit.Assert.assertEquals(com.twilio.type.PhoneNumberPrice.Type.MOBILE, pnp.getType());
    }
}

