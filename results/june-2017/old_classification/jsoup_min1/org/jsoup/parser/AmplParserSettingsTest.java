

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
        // AssertGenerator replace invocation
        org.jsoup.parser.Parser o_caseSupport_cf147__25 = // StatementAdderMethod cloned existing statement
vc_58.xmlParser();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsoup.parser.Parser)o_caseSupport_cf147__25).getErrors());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.Parser)o_caseSupport_cf147__25).isTrackErrors());
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
    /* amplification of org.jsoup.parser.ParserSettingsTest#caseSupport_cf51 */
    @org.junit.Test(timeout = 10000)
    public void caseSupport_cf51_failAssert49_literalMutation1619() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.ParseSettings bothOn = new org.jsoup.parser.ParseSettings(true, true);
            org.jsoup.parser.ParseSettings bothOff = new org.jsoup.parser.ParseSettings(true, false);
            org.jsoup.parser.ParseSettings tagOn = new org.jsoup.parser.ParseSettings(true, false);
            org.jsoup.parser.ParseSettings attrOn = new org.jsoup.parser.ParseSettings(false, true);
            // MethodAssertGenerator build local variable
            Object o_9_0 = bothOn.normalizeTag("FOO");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_9_0, "FOO");
            // MethodAssertGenerator build local variable
            Object o_11_0 = bothOn.normalizeAttribute("FOO");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, "FOO");
            // MethodAssertGenerator build local variable
            Object o_13_0 = bothOff.normalizeTag("FOO");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_13_0, "FOO");
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
            // StatementAdderOnAssert create null value
            java.lang.String vc_4 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.parser.Parser vc_2 = (org.jsoup.parser.Parser)null;
            // StatementAdderMethod cloned existing statement
            vc_2.unescapeEntities(vc_4, boolean_vc_1);
            // MethodAssertGenerator build local variable
            Object o_31_0 = attrOn.normalizeAttribute("FOO");
            org.junit.Assert.fail("caseSupport_cf51 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.parser.ParserSettingsTest#caseSupport */
    /* amplification of org.jsoup.parser.ParserSettingsTest#caseSupport_cf55 */
    @org.junit.Test(timeout = 10000)
    public void caseSupport_cf55_cf737_failAssert56_literalMutation3490() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.ParseSettings bothOn = new org.jsoup.parser.ParseSettings(false, true);
            org.jsoup.parser.ParseSettings bothOff = new org.jsoup.parser.ParseSettings(false, false);
            org.jsoup.parser.ParseSettings tagOn = new org.jsoup.parser.ParseSettings(true, false);
            org.jsoup.parser.ParseSettings attrOn = new org.jsoup.parser.ParseSettings(false, true);
            // MethodAssertGenerator build local variable
            Object o_9_0 = bothOn.normalizeTag("FOO");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_9_0, "foo");
            // MethodAssertGenerator build local variable
            Object o_11_0 = bothOn.normalizeAttribute("FOO");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, "FOO");
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
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_5 = new java.lang.String();
            // StatementAdderOnAssert create null value
            org.jsoup.parser.Parser vc_2 = (org.jsoup.parser.Parser)null;
            // AssertGenerator replace invocation
            java.lang.String o_caseSupport_cf55__29 = // StatementAdderMethod cloned existing statement
vc_2.unescapeEntities(vc_5, boolean_vc_1);
            // MethodAssertGenerator build local variable
            Object o_31_0 = o_caseSupport_cf55__29;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_31_0, "");
            // StatementAdderOnAssert create null value
            java.lang.String vc_190 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_190);
            // StatementAdderOnAssert create null value
            org.jsoup.parser.Parser vc_188 = (org.jsoup.parser.Parser)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_188);
            // StatementAdderMethod cloned existing statement
            vc_188.unescapeEntities(vc_190, boolean_vc_1);
            // MethodAssertGenerator build local variable
            Object o_39_0 = attrOn.normalizeAttribute("FOO");
            org.junit.Assert.fail("caseSupport_cf55_cf737 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

