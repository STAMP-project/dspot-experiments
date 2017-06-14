

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

    @org.junit.Test(timeout = 10000)
    public void testPatternBuilder_cf45_failAssert40() {
        try {
            java.lang.Object o_1_0 = new org.traccar.helper.PatternBuilder().text("$GPRMC").toString();
            java.lang.Object o_5_0 = new org.traccar.helper.PatternBuilder().number("(dd.x+)").toString();
            java.lang.Object o_9_0 = new org.traccar.helper.PatternBuilder().text("a").text("b").text("c").optional(2).toString();
            java.lang.Object o_16_0 = new org.traccar.helper.PatternBuilder().expression("a|b").toString();
            java.lang.Object o_20_0 = new org.traccar.helper.PatternBuilder().expression("ab|").toString();
            java.lang.Object o_24_0 = new org.traccar.helper.PatternBuilder().or().toString();
            java.lang.String vc_8 = ((java.lang.String) (null));
            org.traccar.helper.PatternBuilder vc_7 = new org.traccar.helper.PatternBuilder();
            vc_7.binary(vc_8);
            java.lang.Object o_34_0 = new org.traccar.helper.PatternBuilder().number("|d|d|").toString();
            org.junit.Assert.fail("testPatternBuilder_cf45 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPatternBuilder_cf71_failAssert55() {
        try {
            java.lang.Object o_1_0 = new org.traccar.helper.PatternBuilder().text("$GPRMC").toString();
            java.lang.Object o_5_0 = new org.traccar.helper.PatternBuilder().number("(dd.x+)").toString();
            java.lang.Object o_9_0 = new org.traccar.helper.PatternBuilder().text("a").text("b").text("c").optional(2).toString();
            java.lang.Object o_16_0 = new org.traccar.helper.PatternBuilder().expression("a|b").toString();
            java.lang.Object o_20_0 = new org.traccar.helper.PatternBuilder().expression("ab|").toString();
            java.lang.Object o_24_0 = new org.traccar.helper.PatternBuilder().or().toString();
            org.traccar.helper.PatternBuilder vc_27 = new org.traccar.helper.PatternBuilder();
            vc_27.optional();
            java.lang.Object o_32_0 = new org.traccar.helper.PatternBuilder().number("|d|d|").toString();
            org.junit.Assert.fail("testPatternBuilder_cf71 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }
}

