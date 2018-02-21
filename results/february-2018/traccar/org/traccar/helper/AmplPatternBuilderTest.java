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
}

