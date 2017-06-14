

package org.jsoup.parser;


public class AmplParserSettingsTest {
    @org.junit.Test
    public void caseSupport() {
        org.jsoup.parser.ParseSettings bothOn = new org.jsoup.parser.ParseSettings(true, true);
        org.jsoup.parser.ParseSettings bothOff = new org.jsoup.parser.ParseSettings(false, false);
        org.jsoup.parser.ParseSettings tagOn = new org.jsoup.parser.ParseSettings(true, false);
        org.jsoup.parser.ParseSettings attrOn = new org.jsoup.parser.ParseSettings(false, true);
        org.junit.Assert.assertEquals("FOO", bothOn.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("FOO", bothOn.normalizeAttribute("FOO"));
        org.junit.Assert.assertEquals("foo", bothOff.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("foo", bothOff.normalizeAttribute("FOO"));
        org.junit.Assert.assertEquals("FOO", tagOn.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("foo", tagOn.normalizeAttribute("FOO"));
        org.junit.Assert.assertEquals("foo", attrOn.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("FOO", attrOn.normalizeAttribute("FOO"));
    }

    /* amplification of org.jsoup.parser.ParserSettingsTest#caseSupport */
    @org.junit.Test(timeout = 10000)
    public void caseSupport_cf54() {
        org.jsoup.parser.ParseSettings bothOn = new org.jsoup.parser.ParseSettings(true, true);
        org.jsoup.parser.ParseSettings bothOff = new org.jsoup.parser.ParseSettings(false, false);
        org.jsoup.parser.ParseSettings tagOn = new org.jsoup.parser.ParseSettings(true, false);
        org.jsoup.parser.ParseSettings attrOn = new org.jsoup.parser.ParseSettings(false, true);
        org.junit.Assert.assertEquals("FOO", bothOn.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("FOO", bothOn.normalizeAttribute("FOO"));
        org.junit.Assert.assertEquals("foo", bothOff.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("foo", bothOff.normalizeAttribute("FOO"));
        org.junit.Assert.assertEquals("FOO", tagOn.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("foo", tagOn.normalizeAttribute("FOO"));
        org.junit.Assert.assertEquals("foo", attrOn.normalizeTag("FOO"));
        // StatementAdderOnAssert create random local variable
        boolean vc_6 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_6);
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_0 = "FOO";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_0, "FOO");
        // StatementAdderOnAssert create null value
        org.jsoup.parser.Parser vc_2 = (org.jsoup.parser.Parser)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        // AssertGenerator replace invocation
        java.lang.String o_caseSupport_cf54__29 = // StatementAdderMethod cloned existing statement
vc_2.unescapeEntities(String_vc_0, vc_6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_caseSupport_cf54__29, "FOO");
        org.junit.Assert.assertEquals("FOO", attrOn.normalizeAttribute("FOO"));
    }

    /* amplification of org.jsoup.parser.ParserSettingsTest#caseSupport */
    @org.junit.Test(timeout = 10000)
    public void caseSupport_cf147() {
        org.jsoup.parser.ParseSettings bothOn = new org.jsoup.parser.ParseSettings(true, true);
        org.jsoup.parser.ParseSettings bothOff = new org.jsoup.parser.ParseSettings(false, false);
        org.jsoup.parser.ParseSettings tagOn = new org.jsoup.parser.ParseSettings(true, false);
        org.jsoup.parser.ParseSettings attrOn = new org.jsoup.parser.ParseSettings(false, true);
        org.junit.Assert.assertEquals("FOO", bothOn.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("FOO", bothOn.normalizeAttribute("FOO"));
        org.junit.Assert.assertEquals("foo", bothOff.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("foo", bothOff.normalizeAttribute("FOO"));
        org.junit.Assert.assertEquals("FOO", tagOn.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("foo", tagOn.normalizeAttribute("FOO"));
        org.junit.Assert.assertEquals("foo", attrOn.normalizeTag("FOO"));
        // StatementAdderOnAssert create null value
        org.jsoup.parser.Parser vc_58 = (org.jsoup.parser.Parser)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_58);
        // AssertGenerator replace invocation
        org.jsoup.parser.Parser o_caseSupport_cf147__25 = // StatementAdderMethod cloned existing statement
vc_58.xmlParser();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_caseSupport_cf147__25.isTrackErrors());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_caseSupport_cf147__25.getErrors());
        org.junit.Assert.assertEquals("FOO", attrOn.normalizeAttribute("FOO"));
    }

    /* amplification of org.jsoup.parser.ParserSettingsTest#caseSupport */
    @org.junit.Test(timeout = 10000)
    public void caseSupport_cf55() {
        org.jsoup.parser.ParseSettings bothOn = new org.jsoup.parser.ParseSettings(true, true);
        org.jsoup.parser.ParseSettings bothOff = new org.jsoup.parser.ParseSettings(false, false);
        org.jsoup.parser.ParseSettings tagOn = new org.jsoup.parser.ParseSettings(true, false);
        org.jsoup.parser.ParseSettings attrOn = new org.jsoup.parser.ParseSettings(false, true);
        org.junit.Assert.assertEquals("FOO", bothOn.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("FOO", bothOn.normalizeAttribute("FOO"));
        org.junit.Assert.assertEquals("foo", bothOff.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("foo", bothOff.normalizeAttribute("FOO"));
        org.junit.Assert.assertEquals("FOO", tagOn.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("foo", tagOn.normalizeAttribute("FOO"));
        org.junit.Assert.assertEquals("foo", attrOn.normalizeTag("FOO"));
        // StatementAdderOnAssert create literal from method
        boolean boolean_vc_1 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(boolean_vc_1);
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_5 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_5, "");
        // StatementAdderOnAssert create null value
        org.jsoup.parser.Parser vc_2 = (org.jsoup.parser.Parser)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        // AssertGenerator replace invocation
        java.lang.String o_caseSupport_cf55__29 = // StatementAdderMethod cloned existing statement
vc_2.unescapeEntities(vc_5, boolean_vc_1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_caseSupport_cf55__29, "");
        org.junit.Assert.assertEquals("FOO", attrOn.normalizeAttribute("FOO"));
    }

    /* amplification of org.jsoup.parser.ParserSettingsTest#caseSupport */
    @org.junit.Test(timeout = 10000)
    public void caseSupport_cf133() {
        org.jsoup.parser.ParseSettings bothOn = new org.jsoup.parser.ParseSettings(true, true);
        org.jsoup.parser.ParseSettings bothOff = new org.jsoup.parser.ParseSettings(false, false);
        org.jsoup.parser.ParseSettings tagOn = new org.jsoup.parser.ParseSettings(true, false);
        org.jsoup.parser.ParseSettings attrOn = new org.jsoup.parser.ParseSettings(false, true);
        org.junit.Assert.assertEquals("FOO", bothOn.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("FOO", bothOn.normalizeAttribute("FOO"));
        org.junit.Assert.assertEquals("foo", bothOff.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("foo", bothOff.normalizeAttribute("FOO"));
        org.junit.Assert.assertEquals("FOO", tagOn.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("foo", tagOn.normalizeAttribute("FOO"));
        org.junit.Assert.assertEquals("foo", attrOn.normalizeTag("FOO"));
        // StatementAdderOnAssert create null value
        org.jsoup.parser.Parser vc_45 = (org.jsoup.parser.Parser)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_45);
        // AssertGenerator replace invocation
        org.jsoup.parser.Parser o_caseSupport_cf133__25 = // StatementAdderMethod cloned existing statement
vc_45.htmlParser();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_caseSupport_cf133__25.isTrackErrors());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_caseSupport_cf133__25.getErrors());
        org.junit.Assert.assertEquals("FOO", attrOn.normalizeAttribute("FOO"));
    }

    /* amplification of org.jsoup.parser.ParserSettingsTest#caseSupport */
    @org.junit.Test(timeout = 10000)
    public void caseSupport_cf52_failAssert50() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.ParseSettings bothOn = new org.jsoup.parser.ParseSettings(true, true);
            org.jsoup.parser.ParseSettings bothOff = new org.jsoup.parser.ParseSettings(false, false);
            org.jsoup.parser.ParseSettings tagOn = new org.jsoup.parser.ParseSettings(true, false);
            org.jsoup.parser.ParseSettings attrOn = new org.jsoup.parser.ParseSettings(false, true);
            // MethodAssertGenerator build local variable
            Object o_9_0 = bothOn.normalizeTag("FOO");
            // MethodAssertGenerator build local variable
            Object o_11_0 = bothOn.normalizeAttribute("FOO");
            // MethodAssertGenerator build local variable
            Object o_13_0 = bothOff.normalizeTag("FOO");
            // MethodAssertGenerator build local variable
            Object o_15_0 = bothOff.normalizeAttribute("FOO");
            // MethodAssertGenerator build local variable
            Object o_17_0 = tagOn.normalizeTag("FOO");
            // MethodAssertGenerator build local variable
            Object o_19_0 = tagOn.normalizeAttribute("FOO");
            // MethodAssertGenerator build local variable
            Object o_21_0 = attrOn.normalizeTag("FOO");
            // StatementAdderOnAssert create random local variable
            boolean vc_6 = true;
            // StatementAdderOnAssert create null value
            java.lang.String vc_4 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.parser.Parser vc_2 = (org.jsoup.parser.Parser)null;
            // StatementAdderMethod cloned existing statement
            vc_2.unescapeEntities(vc_4, vc_6);
            // MethodAssertGenerator build local variable
            Object o_31_0 = attrOn.normalizeAttribute("FOO");
            org.junit.Assert.fail("caseSupport_cf52 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.parser.ParserSettingsTest#caseSupport */
    @org.junit.Test(timeout = 10000)
    public void caseSupport_cf51_failAssert49_literalMutation1656() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.ParseSettings bothOn = new org.jsoup.parser.ParseSettings(true, true);
            org.jsoup.parser.ParseSettings bothOff = new org.jsoup.parser.ParseSettings(false, false);
            org.jsoup.parser.ParseSettings tagOn = new org.jsoup.parser.ParseSettings(true, false);
            org.jsoup.parser.ParseSettings attrOn = new org.jsoup.parser.ParseSettings(false, true);
            // MethodAssertGenerator build local variable
            Object o_9_0 = bothOn.normalizeTag("FOO");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_9_0, "FOO");
            // MethodAssertGenerator build local variable
            Object o_11_0 = bothOn.normalizeAttribute("");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, "");
            // MethodAssertGenerator build local variable
            Object o_13_0 = bothOff.normalizeTag("FOO");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_13_0, "foo");
            // MethodAssertGenerator build local variable
            Object o_15_0 = bothOff.normalizeAttribute("FOO");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_15_0, "foo");
            // MethodAssertGenerator build local variable
            Object o_17_0 = tagOn.normalizeTag("FOO");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_17_0, "FOO");
            // MethodAssertGenerator build local variable
            Object o_19_0 = tagOn.normalizeAttribute("FOO");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_19_0, "foo");
            // MethodAssertGenerator build local variable
            Object o_21_0 = attrOn.normalizeTag("FOO");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_21_0, "foo");
            // StatementAdderOnAssert create literal from method
            boolean boolean_vc_1 = true;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(boolean_vc_1);
            // StatementAdderOnAssert create null value
            java.lang.String vc_4 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4);
            // StatementAdderOnAssert create null value
            org.jsoup.parser.Parser vc_2 = (org.jsoup.parser.Parser)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2);
            // StatementAdderMethod cloned existing statement
            vc_2.unescapeEntities(vc_4, boolean_vc_1);
            // MethodAssertGenerator build local variable
            Object o_31_0 = attrOn.normalizeAttribute("FOO");
            org.junit.Assert.fail("caseSupport_cf51 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.parser.ParserSettingsTest#caseSupport */
    @org.junit.Test(timeout = 10000)
    public void caseSupport_cf133_cf1357_failAssert31() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.ParseSettings bothOn = new org.jsoup.parser.ParseSettings(true, true);
            org.jsoup.parser.ParseSettings bothOff = new org.jsoup.parser.ParseSettings(false, false);
            org.jsoup.parser.ParseSettings tagOn = new org.jsoup.parser.ParseSettings(true, false);
            org.jsoup.parser.ParseSettings attrOn = new org.jsoup.parser.ParseSettings(false, true);
            // MethodAssertGenerator build local variable
            Object o_9_0 = bothOn.normalizeTag("FOO");
            // MethodAssertGenerator build local variable
            Object o_11_0 = bothOn.normalizeAttribute("FOO");
            // MethodAssertGenerator build local variable
            Object o_13_0 = bothOff.normalizeTag("FOO");
            // MethodAssertGenerator build local variable
            Object o_15_0 = bothOff.normalizeAttribute("FOO");
            // MethodAssertGenerator build local variable
            Object o_17_0 = tagOn.normalizeTag("FOO");
            // MethodAssertGenerator build local variable
            Object o_19_0 = tagOn.normalizeAttribute("FOO");
            // MethodAssertGenerator build local variable
            Object o_21_0 = attrOn.normalizeTag("FOO");
            // StatementAdderOnAssert create null value
            org.jsoup.parser.Parser vc_45 = (org.jsoup.parser.Parser)null;
            // MethodAssertGenerator build local variable
            Object o_25_0 = vc_45;
            // AssertGenerator replace invocation
            org.jsoup.parser.Parser o_caseSupport_cf133__25 = // StatementAdderMethod cloned existing statement
vc_45.htmlParser();
            // MethodAssertGenerator build local variable
            Object o_29_0 = o_caseSupport_cf133__25.isTrackErrors();
            // MethodAssertGenerator build local variable
            Object o_31_0 = o_caseSupport_cf133__25.getErrors();
            // StatementAdderMethod cloned existing statement
            vc_45.settings();
            // MethodAssertGenerator build local variable
            Object o_35_0 = attrOn.normalizeAttribute("FOO");
            org.junit.Assert.fail("caseSupport_cf133_cf1357 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

