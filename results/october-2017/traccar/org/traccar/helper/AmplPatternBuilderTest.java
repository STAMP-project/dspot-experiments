package org.traccar.helper;


public class AmplPatternBuilderTest {
    @org.junit.Test(timeout = 10000)
    public void testPatternBuilder() {
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder__1 = new org.traccar.helper.PatternBuilder().text("$GPRMC").toString();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder__4 = new org.traccar.helper.PatternBuilder().number("(dd.x+)").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", o_testPatternBuilder__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder__7 = new org.traccar.helper.PatternBuilder().text("a").text("b").text("c").optional(2).toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a(?:bc)?", o_testPatternBuilder__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder__13 = new org.traccar.helper.PatternBuilder().expression("a|b").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", o_testPatternBuilder__13);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder__16 = new org.traccar.helper.PatternBuilder().expression("ab|").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder__16);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder__19 = new org.traccar.helper.PatternBuilder().or().toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", o_testPatternBuilder__19);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder__22 = new org.traccar.helper.PatternBuilder().number("|d|d|").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", o_testPatternBuilder__22);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", o_testPatternBuilder__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", o_testPatternBuilder__1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a(?:bc)?", o_testPatternBuilder__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", o_testPatternBuilder__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", o_testPatternBuilder__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder__16);
    }

    /* amplification of org.traccar.helper.PatternBuilderTest#testPatternBuilder */
    @org.junit.Test(timeout = 10000)
    public void testPatternBuilder_literalMutationString3() {
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString3__1 = new org.traccar.helper.PatternBuilder().text("$G*RMC").toString();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString3__4 = new org.traccar.helper.PatternBuilder().number("(dd.x+)").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", o_testPatternBuilder_literalMutationString3__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString3__7 = new org.traccar.helper.PatternBuilder().text("a").text("b").text("c").optional(2).toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a(?:bc)?", o_testPatternBuilder_literalMutationString3__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString3__13 = new org.traccar.helper.PatternBuilder().expression("a|b").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", o_testPatternBuilder_literalMutationString3__13);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString3__16 = new org.traccar.helper.PatternBuilder().expression("ab|").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder_literalMutationString3__16);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString3__19 = new org.traccar.helper.PatternBuilder().or().toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", o_testPatternBuilder_literalMutationString3__19);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString3__22 = new org.traccar.helper.PatternBuilder().number("|d|d|").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", o_testPatternBuilder_literalMutationString3__22);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", o_testPatternBuilder_literalMutationString3__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a(?:bc)?", o_testPatternBuilder_literalMutationString3__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", o_testPatternBuilder_literalMutationString3__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$G\\*RMC", o_testPatternBuilder_literalMutationString3__1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", o_testPatternBuilder_literalMutationString3__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder_literalMutationString3__16);
    }

    /* amplification of org.traccar.helper.PatternBuilderTest#testPatternBuilder */
    /* amplification of org.traccar.helper.PatternBuilderTest#testPatternBuilder_literalMutationString32 */
    @org.junit.Test(timeout = 10000)
    public void testPatternBuilder_literalMutationString32_literalMutationNumber1471() {
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString32__1 = new org.traccar.helper.PatternBuilder().text("$GPRMC").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", o_testPatternBuilder_literalMutationString32__1);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString32__4 = new org.traccar.helper.PatternBuilder().number("(dd.x+)").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", o_testPatternBuilder_literalMutationString32__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString32__7 = new org.traccar.helper.PatternBuilder().text("a").text("b").text("c").optional(// TestDataMutator on numbers
        3).toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:abc)?", o_testPatternBuilder_literalMutationString32__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString32__13 = new org.traccar.helper.PatternBuilder().expression("L[{").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("L[{", o_testPatternBuilder_literalMutationString32__13);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString32__16 = new org.traccar.helper.PatternBuilder().expression("ab|").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder_literalMutationString32__16);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString32__19 = new org.traccar.helper.PatternBuilder().or().toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", o_testPatternBuilder_literalMutationString32__19);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString32__22 = new org.traccar.helper.PatternBuilder().number("|d|d|").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", o_testPatternBuilder_literalMutationString32__22);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", o_testPatternBuilder_literalMutationString32__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:abc)?", o_testPatternBuilder_literalMutationString32__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder_literalMutationString32__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("L[{", o_testPatternBuilder_literalMutationString32__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", o_testPatternBuilder_literalMutationString32__1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", o_testPatternBuilder_literalMutationString32__4);
    }

    /* amplification of org.traccar.helper.PatternBuilderTest#testPatternBuilder */
    /* amplification of org.traccar.helper.PatternBuilderTest#testPatternBuilder_literalMutationString32 */
    /* amplification of org.traccar.helper.PatternBuilderTest#testPatternBuilder_literalMutationString32_literalMutationString1484 */
    @org.junit.Test(timeout = 10000)
    public void testPatternBuilder_literalMutationString32_literalMutationString1484_literalMutationString9985() {
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString32__1 = new org.traccar.helper.PatternBuilder().text("").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_testPatternBuilder_literalMutationString32__1);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString32__4 = new org.traccar.helper.PatternBuilder().number("(dd.x+)").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", o_testPatternBuilder_literalMutationString32__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString32__7 = new org.traccar.helper.PatternBuilder().text("a").text("b").text("c").optional(2).toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a(?:bc)?", o_testPatternBuilder_literalMutationString32__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString32__13 = new org.traccar.helper.PatternBuilder().expression("L[{").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("L[{", o_testPatternBuilder_literalMutationString32__13);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString32__16 = new org.traccar.helper.PatternBuilder().expression("ab|").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder_literalMutationString32__16);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString32__19 = new org.traccar.helper.PatternBuilder().or().toString();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString32__22 = new org.traccar.helper.PatternBuilder().number("").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_testPatternBuilder_literalMutationString32__22);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder_literalMutationString32__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", o_testPatternBuilder_literalMutationString32__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("L[{", o_testPatternBuilder_literalMutationString32__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_testPatternBuilder_literalMutationString32__1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", o_testPatternBuilder_literalMutationString32__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a(?:bc)?", o_testPatternBuilder_literalMutationString32__7);
    }
}

