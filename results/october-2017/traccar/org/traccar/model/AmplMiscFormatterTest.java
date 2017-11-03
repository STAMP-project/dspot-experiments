package org.traccar.model;


public class AmplMiscFormatterTest {
    @org.junit.Test
    public void testToString() throws java.lang.Exception {
        org.traccar.model.Position position = new org.traccar.model.Position();
        position.set("a", "1");
        position.set("b", "2");
        position.set("a", "3");
        org.junit.Assert.assertEquals("<info><a>3</a><b>2</b></info>", org.traccar.model.MiscFormatter.toXmlString(position.getAttributes()));
    }

    /* amplification of org.traccar.model.MiscFormatterTest#testToString */
    /* amplification of testToString_sd35 */
    @org.junit.Test(timeout = 10000)
    public void testToString_sd35_sd1917_failAssert7() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_arg1_653 = "=MtN|3}(#*QN,!y!oNPJ";
            java.lang.String __DSPOT_arg0_652 = "21isI%atY*_<Q;W[2+S(";
            org.traccar.model.Position position = new org.traccar.model.Position();
            position.set("a", "1");
            position.set("b", "2");
            position.set("a", "3");
            org.traccar.model.MiscFormatter.toXmlString(position.getAttributes());
            // StatementAdd: generate variable from return value
            java.lang.String __DSPOT_invoc_9 = // StatementAdd: add invocation of a method
            position.getAddress();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_9.replaceAll(__DSPOT_arg0_652, __DSPOT_arg1_653);
            org.junit.Assert.fail("testToString_sd35_sd1917 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.model.MiscFormatterTest#testToString */
    /* amplification of testToString_sd38 */
    @org.junit.Test(timeout = 10000)
    public void testToString_sd38_sd2162_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.model.Position position = new org.traccar.model.Position();
            position.set("a", "1");
            position.set("b", "2");
            position.set("a", "3");
            org.traccar.model.MiscFormatter.toXmlString(position.getAttributes());
            // StatementAdd: generate variable from return value
            java.util.Date __DSPOT_invoc_9 = // StatementAdd: add invocation of a method
            position.getFixTime();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_9.getTimezoneOffset();
            org.junit.Assert.fail("testToString_sd38_sd2162 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.model.MiscFormatterTest#testToString */
    /* amplification of testToString_sd36 */
    @org.junit.Test(timeout = 10000)
    public void testToString_sd36_sd2018_failAssert11() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.model.Position position = new org.traccar.model.Position();
            position.set("a", "1");
            position.set("b", "2");
            position.set("a", "3");
            org.traccar.model.MiscFormatter.toXmlString(position.getAttributes());
            // StatementAdd: generate variable from return value
            java.lang.String __DSPOT_invoc_9 = // StatementAdd: add invocation of a method
            position.getProtocol();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_9.toLowerCase();
            org.junit.Assert.fail("testToString_sd36_sd2018 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.model.MiscFormatterTest#testToString */
    /* amplification of testToString_sd37 */
    @org.junit.Test(timeout = 10000)
    public void testToString_sd37_sd2084_failAssert8() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.model.Position position = new org.traccar.model.Position();
            position.set("a", "1");
            position.set("b", "2");
            position.set("a", "3");
            org.traccar.model.MiscFormatter.toXmlString(position.getAttributes());
            // StatementAdd: generate variable from return value
            java.util.Date __DSPOT_invoc_9 = // StatementAdd: add invocation of a method
            position.getDeviceTime();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_9.getMinutes();
            org.junit.Assert.fail("testToString_sd37_sd2084 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.model.MiscFormatterTest#testToString */
    /* amplification of testToString_sd40 */
    @org.junit.Test(timeout = 10000)
    public void testToString_sd40_sd2309_failAssert10() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.model.WifiAccessPoint __DSPOT_wifiAccessPoint_849 = new org.traccar.model.WifiAccessPoint();
            org.traccar.model.Position position = new org.traccar.model.Position();
            position.set("a", "1");
            position.set("b", "2");
            position.set("a", "3");
            org.traccar.model.MiscFormatter.toXmlString(position.getAttributes());
            // StatementAdd: generate variable from return value
            org.traccar.model.Network __DSPOT_invoc_9 = // StatementAdd: add invocation of a method
            position.getNetwork();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_9.addWifiAccessPoint(__DSPOT_wifiAccessPoint_849);
            org.junit.Assert.fail("testToString_sd40_sd2309 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

