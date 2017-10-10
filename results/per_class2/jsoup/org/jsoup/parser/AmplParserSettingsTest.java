package org.jsoup.parser;


public class AmplParserSettingsTest {
    @org.junit.Test(timeout = 10000)
    public void caseSupport() {
        org.jsoup.parser.ParseSettings bothOn = new org.jsoup.parser.ParseSettings(true, true);
        org.jsoup.parser.ParseSettings bothOff = new org.jsoup.parser.ParseSettings(false, false);
        org.jsoup.parser.ParseSettings tagOn = new org.jsoup.parser.ParseSettings(true, false);
        org.jsoup.parser.ParseSettings attrOn = new org.jsoup.parser.ParseSettings(false, true);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport__9 = bothOn.normalizeTag("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport__10 = bothOn.normalizeAttribute("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport__10);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport__11 = bothOff.normalizeTag("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport__11);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport__12 = bothOff.normalizeAttribute("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport__12);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport__13 = tagOn.normalizeTag("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport__13);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport__14 = tagOn.normalizeAttribute("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport__14);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport__15 = attrOn.normalizeTag("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport__15);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport__16 = attrOn.normalizeAttribute("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport__13);
    }

    /* amplification of org.jsoup.parser.ParserSettingsTest#caseSupport */
    @org.junit.Test(timeout = 10000)
    public void caseSupport_literalMutationString42() {
        org.jsoup.parser.ParseSettings bothOn = new org.jsoup.parser.ParseSettings(true, true);
        org.jsoup.parser.ParseSettings bothOff = new org.jsoup.parser.ParseSettings(false, false);
        org.jsoup.parser.ParseSettings tagOn = new org.jsoup.parser.ParseSettings(true, false);
        org.jsoup.parser.ParseSettings attrOn = new org.jsoup.parser.ParseSettings(false, true);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString42__9 = bothOn.normalizeTag("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport_literalMutationString42__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString42__10 = bothOn.normalizeAttribute("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport_literalMutationString42__10);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString42__11 = bothOff.normalizeTag("FOO");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString42__12 = bothOff.normalizeAttribute("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport_literalMutationString42__12);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString42__13 = tagOn.normalizeTag("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport_literalMutationString42__13);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString42__14 = tagOn.normalizeAttribute("2OO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("2oo", o_caseSupport_literalMutationString42__14);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString42__15 = attrOn.normalizeTag("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport_literalMutationString42__15);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString42__16 = attrOn.normalizeAttribute("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport_literalMutationString42__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport_literalMutationString42__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport_literalMutationString42__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport_literalMutationString42__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport_literalMutationString42__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport_literalMutationString42__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("2oo", o_caseSupport_literalMutationString42__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport_literalMutationString42__9);
    }

    /* amplification of org.jsoup.parser.ParserSettingsTest#caseSupport */
    /* amplification of org.jsoup.parser.ParserSettingsTest#caseSupport_literalMutationString50 */
    @org.junit.Test(timeout = 10000)
    public void caseSupport_literalMutationString50_literalMutationString2588() {
        org.jsoup.parser.ParseSettings bothOn = new org.jsoup.parser.ParseSettings(true, true);
        org.jsoup.parser.ParseSettings bothOff = new org.jsoup.parser.ParseSettings(false, false);
        org.jsoup.parser.ParseSettings tagOn = new org.jsoup.parser.ParseSettings(true, false);
        org.jsoup.parser.ParseSettings attrOn = new org.jsoup.parser.ParseSettings(false, true);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString50__9 = bothOn.normalizeTag("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport_literalMutationString50__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString50__10 = bothOn.normalizeAttribute("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello <div>there</div> <span>now</span></span>", o_caseSupport_literalMutationString50__10);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString50__11 = bothOff.normalizeTag("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport_literalMutationString50__11);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString50__12 = bothOff.normalizeAttribute("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport_literalMutationString50__12);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString50__13 = tagOn.normalizeTag("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport_literalMutationString50__13);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString50__14 = tagOn.normalizeAttribute("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport_literalMutationString50__14);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString50__15 = attrOn.normalizeTag("^OO");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString50__16 = attrOn.normalizeAttribute("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport_literalMutationString50__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("^oo", o_caseSupport_literalMutationString50__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport_literalMutationString50__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport_literalMutationString50__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello <div>there</div> <span>now</span></span>", o_caseSupport_literalMutationString50__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport_literalMutationString50__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport_literalMutationString50__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport_literalMutationString50__12);
    }

    /* amplification of org.jsoup.parser.ParserSettingsTest#caseSupport */
    /* amplification of org.jsoup.parser.ParserSettingsTest#caseSupport_literalMutationString9 */
    /* amplification of org.jsoup.parser.ParserSettingsTest#caseSupport_literalMutationString9_literalMutationString331 */
    @org.junit.Test(timeout = 10000)
    public void caseSupport_literalMutationString9_literalMutationString331_literalMutationString9092() {
        org.jsoup.parser.ParseSettings bothOn = new org.jsoup.parser.ParseSettings(true, true);
        org.jsoup.parser.ParseSettings bothOff = new org.jsoup.parser.ParseSettings(false, false);
        org.jsoup.parser.ParseSettings tagOn = new org.jsoup.parser.ParseSettings(true, false);
        org.jsoup.parser.ParseSettings attrOn = new org.jsoup.parser.ParseSettings(false, true);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString9__9 = bothOn.normalizeTag("");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_caseSupport_literalMutationString9__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString9__10 = bothOn.normalizeAttribute("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello <div>there</div> <span>now</span></span>", o_caseSupport_literalMutationString9__10);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString9__11 = bothOff.normalizeTag("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport_literalMutationString9__11);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString9__12 = bothOff.normalizeAttribute("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport_literalMutationString9__12);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString9__13 = tagOn.normalizeTag("FOO");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString9__14 = tagOn.normalizeAttribute("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport_literalMutationString9__14);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString9__15 = attrOn.normalizeTag("<OO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<oo", o_caseSupport_literalMutationString9__15);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_caseSupport_literalMutationString9__16 = attrOn.normalizeAttribute("FOO");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport_literalMutationString9__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport_literalMutationString9__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport_literalMutationString9__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello <div>there</div> <span>now</span></span>", o_caseSupport_literalMutationString9__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("foo", o_caseSupport_literalMutationString9__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_caseSupport_literalMutationString9__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("FOO", o_caseSupport_literalMutationString9__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<oo", o_caseSupport_literalMutationString9__15);
    }
}

