

package org.traccar.helper;


public class AmplPatternBuilderTest {
    @org.junit.Test
    public void testPatternBuilder() {
        org.junit.Assert.assertEquals("\\$GPRMC", new org.traccar.helper.PatternBuilder().text("$GPRMC").toString());
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", new org.traccar.helper.PatternBuilder().number("(dd.x+)").toString());
        org.junit.Assert.assertEquals("a(?:bc)?", new org.traccar.helper.PatternBuilder().text("a").text("b").text("c").optional(2).toString());
        org.junit.Assert.assertEquals("a|b", new org.traccar.helper.PatternBuilder().expression("a|b").toString());
        org.junit.Assert.assertEquals("ab\\|", new org.traccar.helper.PatternBuilder().expression("ab|").toString());
        org.junit.Assert.assertEquals("|", new org.traccar.helper.PatternBuilder().or().toString());
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", new org.traccar.helper.PatternBuilder().number("|d|d|").toString());
    }

    /* amplification of org.traccar.helper.PatternBuilderTest#testPatternBuilder */
    @org.junit.Test(timeout = 10000)
    public void testPatternBuilder_cf45_failAssert40() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = new org.traccar.helper.PatternBuilder().text("$GPRMC").toString();
            // MethodAssertGenerator build local variable
            Object o_5_0 = new org.traccar.helper.PatternBuilder().number("(dd.x+)").toString();
            // MethodAssertGenerator build local variable
            Object o_9_0 = new org.traccar.helper.PatternBuilder().text("a").text("b").text("c").optional(2).toString();
            // MethodAssertGenerator build local variable
            Object o_16_0 = new org.traccar.helper.PatternBuilder().expression("a|b").toString();
            // MethodAssertGenerator build local variable
            Object o_20_0 = new org.traccar.helper.PatternBuilder().expression("ab|").toString();
            // MethodAssertGenerator build local variable
            Object o_24_0 = new org.traccar.helper.PatternBuilder().or().toString();
            // StatementAdderOnAssert create null value
            java.lang.String vc_8 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            org.traccar.helper.PatternBuilder vc_7 = new org.traccar.helper.PatternBuilder();
            // StatementAdderMethod cloned existing statement
            vc_7.binary(vc_8);
            // MethodAssertGenerator build local variable
            Object o_34_0 = new org.traccar.helper.PatternBuilder().number("|d|d|").toString();
            org.junit.Assert.fail("testPatternBuilder_cf45 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.helper.PatternBuilderTest#testPatternBuilder */
    @org.junit.Test(timeout = 10000)
    public void testPatternBuilder_cf71_failAssert55() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = new org.traccar.helper.PatternBuilder().text("$GPRMC").toString();
            // MethodAssertGenerator build local variable
            Object o_5_0 = new org.traccar.helper.PatternBuilder().number("(dd.x+)").toString();
            // MethodAssertGenerator build local variable
            Object o_9_0 = new org.traccar.helper.PatternBuilder().text("a").text("b").text("c").optional(2).toString();
            // MethodAssertGenerator build local variable
            Object o_16_0 = new org.traccar.helper.PatternBuilder().expression("a|b").toString();
            // MethodAssertGenerator build local variable
            Object o_20_0 = new org.traccar.helper.PatternBuilder().expression("ab|").toString();
            // MethodAssertGenerator build local variable
            Object o_24_0 = new org.traccar.helper.PatternBuilder().or().toString();
            // StatementAdderOnAssert create random local variable
            org.traccar.helper.PatternBuilder vc_27 = new org.traccar.helper.PatternBuilder();
            // StatementAdderMethod cloned existing statement
            vc_27.optional();
            // MethodAssertGenerator build local variable
            Object o_32_0 = new org.traccar.helper.PatternBuilder().number("|d|d|").toString();
            org.junit.Assert.fail("testPatternBuilder_cf71 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }
}

