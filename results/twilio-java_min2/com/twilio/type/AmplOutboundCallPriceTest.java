

package com.twilio.type;


/**
 * Test class for {@link OutboundCallPrice}.
 */
public class AmplOutboundCallPriceTest extends com.twilio.type.TypeTest {
    @org.junit.Test
    public void testFromJson() throws java.io.IOException {
        java.lang.String json = "{\n" + (("    \"base_price\": 1.00,\n" + "    \"current_price\": 2.00\n") + "}");
        com.twilio.type.OutboundCallPrice ocp = fromJson(json, com.twilio.type.OutboundCallPrice.class);
        org.junit.Assert.assertEquals(1.0, ocp.getBasePrice(), 0.0);
        org.junit.Assert.assertEquals(2.0, ocp.getCurrentPrice(), 0.0);
    }

    /* amplification of com.twilio.type.OutboundCallPriceTest#testFromJson */
    @org.junit.Test(timeout = 10000)
    public void testFromJson_cf46() throws java.io.IOException {
        java.lang.String json = "{\n" + (("    \"base_price\": 1.00,\n" + "    \"current_price\": 2.00\n") + "}");
        com.twilio.type.OutboundCallPrice ocp = fromJson(json, com.twilio.type.OutboundCallPrice.class);
        org.junit.Assert.assertEquals(1.0, ocp.getBasePrice(), 0.0);
        // AssertGenerator replace invocation
        java.lang.String o_testFromJson_cf46__6 = // StatementAdderMethod cloned existing statement
ocp.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testFromJson_cf46__6, "OutboundCallPrice{base_price=1.0, current_price=2.0}");
        org.junit.Assert.assertEquals(2.0, ocp.getCurrentPrice(), 0.0);
    }

    /* amplification of com.twilio.type.OutboundCallPriceTest#testFromJson */
    @org.junit.Test(timeout = 10000)
    public void testFromJson_cf32() throws java.io.IOException {
        java.lang.String json = "{\n" + (("    \"base_price\": 1.00,\n" + "    \"current_price\": 2.00\n") + "}");
        com.twilio.type.OutboundCallPrice ocp = fromJson(json, com.twilio.type.OutboundCallPrice.class);
        org.junit.Assert.assertEquals(1.0, ocp.getBasePrice(), 0.0);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2 = (java.lang.Object)null;
        // AssertGenerator replace invocation
        boolean o_testFromJson_cf32__8 = // StatementAdderMethod cloned existing statement
ocp.equals(vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testFromJson_cf32__8);
        org.junit.Assert.assertEquals(2.0, ocp.getCurrentPrice(), 0.0);
    }

    /* amplification of com.twilio.type.OutboundCallPriceTest#testFromJson */
    @org.junit.Test(timeout = 10000)
    public void testFromJson_cf43() throws java.io.IOException {
        java.lang.String json = "{\n" + (("    \"base_price\": 1.00,\n" + "    \"current_price\": 2.00\n") + "}");
        com.twilio.type.OutboundCallPrice ocp = fromJson(json, com.twilio.type.OutboundCallPrice.class);
        org.junit.Assert.assertEquals(1.0, ocp.getBasePrice(), 0.0);
        // AssertGenerator replace invocation
        int o_testFromJson_cf43__6 = // StatementAdderMethod cloned existing statement
ocp.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testFromJson_cf43__6, -32504895);
        org.junit.Assert.assertEquals(2.0, ocp.getCurrentPrice(), 0.0);
    }

    /* amplification of com.twilio.type.OutboundCallPriceTest#testFromJson */
    @org.junit.Test(timeout = 10000)
    public void testFromJson_cf33() throws java.io.IOException {
        java.lang.String json = "{\n" + (("    \"base_price\": 1.00,\n" + "    \"current_price\": 2.00\n") + "}");
        com.twilio.type.OutboundCallPrice ocp = fromJson(json, com.twilio.type.OutboundCallPrice.class);
        org.junit.Assert.assertEquals(1.0, ocp.getBasePrice(), 0.0);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_3 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testFromJson_cf33__8 = // StatementAdderMethod cloned existing statement
ocp.equals(vc_3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testFromJson_cf33__8);
        org.junit.Assert.assertEquals(2.0, ocp.getCurrentPrice(), 0.0);
    }
}

