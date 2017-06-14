

package com.twilio.type;


/**
 * Test class for {@link PhoneNumberCapabilities}.
 */
public class AmplPhoneNumberCapabilitiesTest extends com.twilio.type.TypeTest {
    @org.junit.Test
    public void testFromJson() throws java.io.IOException {
        java.lang.String json = "{\n" + ((("    \"MMS\": true,\n" + "    \"SMS\": false,\n") + "    \"voice\": true\n") + "}");
        com.twilio.type.PhoneNumberCapabilities pnc = fromJson(json, com.twilio.type.PhoneNumberCapabilities.class);
        org.junit.Assert.assertTrue(pnc.getMms());
        org.junit.Assert.assertTrue(pnc.getVoice());
        org.junit.Assert.assertFalse(pnc.getSms());
    }

    /* amplification of com.twilio.type.PhoneNumberCapabilitiesTest#testFromJson */
    @org.junit.Test(timeout = 10000)
    public void testFromJson_cf39() throws java.io.IOException {
        java.lang.String json = "{\n" + ((("    \"MMS\": true,\n" + "    \"SMS\": false,\n") + "    \"voice\": true\n") + "}");
        com.twilio.type.PhoneNumberCapabilities pnc = fromJson(json, com.twilio.type.PhoneNumberCapabilities.class);
        org.junit.Assert.assertTrue(pnc.getMms());
        org.junit.Assert.assertTrue(pnc.getVoice());
        // AssertGenerator replace invocation
        java.lang.String o_testFromJson_cf39__8 = // StatementAdderMethod cloned existing statement
pnc.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testFromJson_cf39__8, "PhoneNumberCapabilities{MMS=true, SMS=false, voice=true}");
        org.junit.Assert.assertFalse(pnc.getSms());
    }

    /* amplification of com.twilio.type.PhoneNumberCapabilitiesTest#testFromJson */
    @org.junit.Test(timeout = 10000)
    public void testFromJson_cf23() throws java.io.IOException {
        java.lang.String json = "{\n" + ((("    \"MMS\": true,\n" + "    \"SMS\": false,\n") + "    \"voice\": true\n") + "}");
        com.twilio.type.PhoneNumberCapabilities pnc = fromJson(json, com.twilio.type.PhoneNumberCapabilities.class);
        org.junit.Assert.assertTrue(pnc.getMms());
        org.junit.Assert.assertTrue(pnc.getVoice());
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_3 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testFromJson_cf23__10 = // StatementAdderMethod cloned existing statement
pnc.equals(vc_3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testFromJson_cf23__10);
        org.junit.Assert.assertFalse(pnc.getSms());
    }

    /* amplification of com.twilio.type.PhoneNumberCapabilitiesTest#testFromJson */
    @org.junit.Test(timeout = 10000)
    public void testFromJson_cf36() throws java.io.IOException {
        java.lang.String json = "{\n" + ((("    \"MMS\": true,\n" + "    \"SMS\": false,\n") + "    \"voice\": true\n") + "}");
        com.twilio.type.PhoneNumberCapabilities pnc = fromJson(json, com.twilio.type.PhoneNumberCapabilities.class);
        org.junit.Assert.assertTrue(pnc.getMms());
        org.junit.Assert.assertTrue(pnc.getVoice());
        // AssertGenerator replace invocation
        int o_testFromJson_cf36__8 = // StatementAdderMethod cloned existing statement
pnc.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testFromJson_cf36__8, 1252360);
        org.junit.Assert.assertFalse(pnc.getSms());
    }

    /* amplification of com.twilio.type.PhoneNumberCapabilitiesTest#testFromJson */
    @org.junit.Test(timeout = 10000)
    public void testFromJson_cf22() throws java.io.IOException {
        java.lang.String json = "{\n" + ((("    \"MMS\": true,\n" + "    \"SMS\": false,\n") + "    \"voice\": true\n") + "}");
        com.twilio.type.PhoneNumberCapabilities pnc = fromJson(json, com.twilio.type.PhoneNumberCapabilities.class);
        org.junit.Assert.assertTrue(pnc.getMms());
        org.junit.Assert.assertTrue(pnc.getVoice());
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        // AssertGenerator replace invocation
        boolean o_testFromJson_cf22__10 = // StatementAdderMethod cloned existing statement
pnc.equals(vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testFromJson_cf22__10);
        org.junit.Assert.assertFalse(pnc.getSms());
    }
}

