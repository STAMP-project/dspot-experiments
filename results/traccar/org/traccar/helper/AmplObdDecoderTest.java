

package org.traccar.helper;


public class AmplObdDecoderTest {
    @org.junit.Test
    public void testDecode() {
        org.junit.Assert.assertEquals(83, org.traccar.helper.ObdDecoder.decode(1, "057b").getValue());
        org.junit.Assert.assertEquals(1225, org.traccar.helper.ObdDecoder.decode(1, "0C1324").getValue());
        org.junit.Assert.assertEquals(20, org.traccar.helper.ObdDecoder.decode(1, "0D14").getValue());
        org.junit.Assert.assertEquals(64050, org.traccar.helper.ObdDecoder.decode(1, "31fa32").getValue());
        org.junit.Assert.assertEquals(25, org.traccar.helper.ObdDecoder.decode(1, "2F41").getValue());
    }

    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode */
    @org.junit.Test(timeout = 1000)
    public void testDecode_cf39() {
        org.junit.Assert.assertEquals(83, org.traccar.helper.ObdDecoder.decode(1, "057b").getValue());
        org.junit.Assert.assertEquals(1225, org.traccar.helper.ObdDecoder.decode(1, "0C1324").getValue());
        org.junit.Assert.assertEquals(20, org.traccar.helper.ObdDecoder.decode(1, "0D14").getValue());
        org.junit.Assert.assertEquals(64050, org.traccar.helper.ObdDecoder.decode(1, "31fa32").getValue());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_2 = "0D14";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_2, "0D14");
        // StatementAdderOnAssert create null value
        org.traccar.helper.ObdDecoder vc_5 = (org.traccar.helper.ObdDecoder)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_5);
        // AssertGenerator replace invocation
        java.util.Map.Entry<java.lang.String, java.lang.Object> o_testDecode_cf39__17 = // StatementAdderMethod cloned existing statement
vc_5.decodeCodes(String_vc_2);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.AbstractMap.SimpleEntry)o_testDecode_cf39__17).getValue(), "P0D14");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.AbstractMap.SimpleEntry)o_testDecode_cf39__17).getKey(), "dtcs");
        org.junit.Assert.assertEquals(25, org.traccar.helper.ObdDecoder.decode(1, "2F41").getValue());
    }

    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode */
    @org.junit.Test(timeout = 1000)
    public void testDecode_cf38_failAssert27() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = org.traccar.helper.ObdDecoder.decode(1, "057b").getValue();
            // MethodAssertGenerator build local variable
            Object o_4_0 = org.traccar.helper.ObdDecoder.decode(1, "0C1324").getValue();
            // MethodAssertGenerator build local variable
            Object o_7_0 = org.traccar.helper.ObdDecoder.decode(1, "0D14").getValue();
            // MethodAssertGenerator build local variable
            Object o_10_0 = org.traccar.helper.ObdDecoder.decode(1, "31fa32").getValue();
            // StatementAdderOnAssert create null value
            java.lang.String vc_7 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.traccar.helper.ObdDecoder vc_5 = (org.traccar.helper.ObdDecoder)null;
            // StatementAdderMethod cloned existing statement
            vc_5.decodeCodes(vc_7);
            // MethodAssertGenerator build local variable
            Object o_19_0 = org.traccar.helper.ObdDecoder.decode(1, "2F41").getValue();
            org.junit.Assert.fail("testDecode_cf38 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode */
    @org.junit.Test(timeout = 1000)
    public void testDecode_cf40() {
        org.junit.Assert.assertEquals(83, org.traccar.helper.ObdDecoder.decode(1, "057b").getValue());
        org.junit.Assert.assertEquals(1225, org.traccar.helper.ObdDecoder.decode(1, "0C1324").getValue());
        org.junit.Assert.assertEquals(20, org.traccar.helper.ObdDecoder.decode(1, "0D14").getValue());
        org.junit.Assert.assertEquals(64050, org.traccar.helper.ObdDecoder.decode(1, "31fa32").getValue());
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_8 = new java.lang.String();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_8, "");
        // StatementAdderOnAssert create null value
        org.traccar.helper.ObdDecoder vc_5 = (org.traccar.helper.ObdDecoder)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_5);
        // AssertGenerator replace invocation
        java.util.Map.Entry<java.lang.String, java.lang.Object> o_testDecode_cf40__17 = // StatementAdderMethod cloned existing statement
vc_5.decodeCodes(vc_8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_testDecode_cf40__17);
        org.junit.Assert.assertEquals(25, org.traccar.helper.ObdDecoder.decode(1, "2F41").getValue());
    }

    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode */
    @org.junit.Test(timeout = 1000)
    public void testDecode_cf39_cf510() {
        org.junit.Assert.assertEquals(83, org.traccar.helper.ObdDecoder.decode(1, "057b").getValue());
        org.junit.Assert.assertEquals(1225, org.traccar.helper.ObdDecoder.decode(1, "0C1324").getValue());
        org.junit.Assert.assertEquals(20, org.traccar.helper.ObdDecoder.decode(1, "0D14").getValue());
        org.junit.Assert.assertEquals(64050, org.traccar.helper.ObdDecoder.decode(1, "31fa32").getValue());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_2 = "0D14";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_2, "0D14");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_2, "0D14");
        // StatementAdderOnAssert create null value
        org.traccar.helper.ObdDecoder vc_5 = (org.traccar.helper.ObdDecoder)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_5);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_5);
        // AssertGenerator replace invocation
        java.util.Map.Entry<java.lang.String, java.lang.Object> o_testDecode_cf39__17 = // StatementAdderMethod cloned existing statement
vc_5.decodeCodes(String_vc_2);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.AbstractMap.SimpleEntry)o_testDecode_cf39__17).getValue(), "P0D14");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.AbstractMap.SimpleEntry)o_testDecode_cf39__17).getKey(), "dtcs");
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_69 = new java.lang.String();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_69, "");
        // StatementAdderOnAssert create random local variable
        int vc_67 = 2135633507;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_67, 2135633507);
        // StatementAdderOnAssert create null value
        org.traccar.helper.ObdDecoder vc_65 = (org.traccar.helper.ObdDecoder)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_65);
        // AssertGenerator replace invocation
        java.util.Map.Entry<java.lang.String, java.lang.Object> o_testDecode_cf39_cf510__33 = // StatementAdderMethod cloned existing statement
vc_65.decode(vc_67, vc_69);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_testDecode_cf39_cf510__33);
        org.junit.Assert.assertEquals(25, org.traccar.helper.ObdDecoder.decode(1, "2F41").getValue());
    }

    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode */
    @org.junit.Test(timeout = 1000)
    public void testDecode_cf30_cf351_cf5301() {
        org.junit.Assert.assertEquals(83, org.traccar.helper.ObdDecoder.decode(1, "057b").getValue());
        org.junit.Assert.assertEquals(1225, org.traccar.helper.ObdDecoder.decode(1, "0C1324").getValue());
        org.junit.Assert.assertEquals(20, org.traccar.helper.ObdDecoder.decode(1, "0D14").getValue());
        org.junit.Assert.assertEquals(64050, org.traccar.helper.ObdDecoder.decode(1, "31fa32").getValue());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_1 = "2F41";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_1, "2F41");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_1, "2F41");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_1, "2F41");
        // StatementAdderOnAssert create random local variable
        int vc_2 = 344656050;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2, 344656050);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2, 344656050);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2, 344656050);
        // StatementAdderOnAssert create null value
        org.traccar.helper.ObdDecoder vc_0 = (org.traccar.helper.ObdDecoder)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_0);
        // AssertGenerator replace invocation
        java.util.Map.Entry<java.lang.String, java.lang.Object> o_testDecode_cf30__19 = // StatementAdderMethod cloned existing statement
vc_0.decode(vc_2, String_vc_1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_testDecode_cf30__19);
        // AssertGenerator replace invocation
        java.util.Map.Entry<java.lang.String, java.lang.Object> o_testDecode_cf30_cf351__29 = // StatementAdderMethod cloned existing statement
vc_0.decodeCodes(String_vc_1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.AbstractMap.SimpleEntry)o_testDecode_cf30_cf351__29).getKey(), "dtcs");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.AbstractMap.SimpleEntry)o_testDecode_cf30_cf351__29).getValue(), "P2F41");
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_213 = "2F41";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_213, "2F41");
        // StatementAdderOnAssert create null value
        org.traccar.helper.ObdDecoder vc_689 = (org.traccar.helper.ObdDecoder)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_689);
        // AssertGenerator replace invocation
        java.util.Map.Entry<java.lang.String, java.lang.Object> o_testDecode_cf30_cf351_cf5301__45 = // StatementAdderMethod cloned existing statement
vc_689.decode(vc_2, String_vc_213);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_testDecode_cf30_cf351_cf5301__45);
        org.junit.Assert.assertEquals(25, org.traccar.helper.ObdDecoder.decode(1, "2F41").getValue());
    }

    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode */
    @org.junit.Test(timeout = 1000)
    public void testDecode_cf30_cf300_cf6154_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = org.traccar.helper.ObdDecoder.decode(1, "057b").getValue();
            // MethodAssertGenerator build local variable
            Object o_4_0 = org.traccar.helper.ObdDecoder.decode(1, "0C1324").getValue();
            // MethodAssertGenerator build local variable
            Object o_7_0 = org.traccar.helper.ObdDecoder.decode(1, "0D14").getValue();
            // MethodAssertGenerator build local variable
            Object o_10_0 = org.traccar.helper.ObdDecoder.decode(1, "31fa32").getValue();
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1 = "2F41";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_1, "2F41");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_1, "2F41");
            // StatementAdderOnAssert create random local variable
            int vc_2 = 344656050;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_2, 344656050);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_2, 344656050);
            // StatementAdderOnAssert create null value
            org.traccar.helper.ObdDecoder vc_0 = (org.traccar.helper.ObdDecoder)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_0);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_0);
            // AssertGenerator replace invocation
            java.util.Map.Entry<java.lang.String, java.lang.Object> o_testDecode_cf30__19 = // StatementAdderMethod cloned existing statement
vc_0.decode(vc_2, String_vc_1);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(o_testDecode_cf30__19);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_13 = "2F41";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_13, "2F41");
            // StatementAdderOnAssert create null value
            org.traccar.helper.ObdDecoder vc_39 = (org.traccar.helper.ObdDecoder)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_39);
            // AssertGenerator replace invocation
            java.util.Map.Entry<java.lang.String, java.lang.Object> o_testDecode_cf30_cf300__33 = // StatementAdderMethod cloned existing statement
vc_39.decode(vc_2, String_vc_13);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(o_testDecode_cf30_cf300__33);
            // StatementAdderOnAssert create null value
            java.lang.String vc_800 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.traccar.helper.ObdDecoder vc_798 = (org.traccar.helper.ObdDecoder)null;
            // StatementAdderMethod cloned existing statement
            vc_798.decodeCodes(vc_800);
            // MethodAssertGenerator build local variable
            Object o_53_0 = org.traccar.helper.ObdDecoder.decode(1, "2F41").getValue();
            org.junit.Assert.fail("testDecode_cf30_cf300_cf6154 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode */
    @org.junit.Test(timeout = 1000)
    public void testDecode_cf30_cf315_cf8793() {
        org.junit.Assert.assertEquals(83, org.traccar.helper.ObdDecoder.decode(1, "057b").getValue());
        org.junit.Assert.assertEquals(1225, org.traccar.helper.ObdDecoder.decode(1, "0C1324").getValue());
        org.junit.Assert.assertEquals(20, org.traccar.helper.ObdDecoder.decode(1, "0D14").getValue());
        org.junit.Assert.assertEquals(64050, org.traccar.helper.ObdDecoder.decode(1, "31fa32").getValue());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_1 = "2F41";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_1, "2F41");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_1, "2F41");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_1, "2F41");
        // StatementAdderOnAssert create random local variable
        int vc_2 = 344656050;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2, 344656050);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2, 344656050);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2, 344656050);
        // StatementAdderOnAssert create null value
        org.traccar.helper.ObdDecoder vc_0 = (org.traccar.helper.ObdDecoder)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_0);
        // AssertGenerator replace invocation
        java.util.Map.Entry<java.lang.String, java.lang.Object> o_testDecode_cf30__19 = // StatementAdderMethod cloned existing statement
vc_0.decode(vc_2, String_vc_1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_testDecode_cf30__19);
        // AssertGenerator replace invocation
        java.util.Map.Entry<java.lang.String, java.lang.Object> o_testDecode_cf30_cf315__29 = // StatementAdderMethod cloned existing statement
vc_0.decode(vc_2, String_vc_1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_testDecode_cf30_cf315__29);
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_1139 = new java.lang.String();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1139, "");
        // StatementAdderOnAssert create null value
        org.traccar.helper.ObdDecoder vc_1136 = (org.traccar.helper.ObdDecoder)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1136);
        // AssertGenerator replace invocation
        java.util.Map.Entry<java.lang.String, java.lang.Object> o_testDecode_cf30_cf315_cf8793__43 = // StatementAdderMethod cloned existing statement
vc_1136.decodeCodes(vc_1139);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_testDecode_cf30_cf315_cf8793__43);
        org.junit.Assert.assertEquals(25, org.traccar.helper.ObdDecoder.decode(1, "2F41").getValue());
    }
}

