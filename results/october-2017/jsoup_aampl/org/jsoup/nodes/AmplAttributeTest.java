package org.jsoup.nodes;


public class AmplAttributeTest {
    @org.junit.Test
    public void html() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_0 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_0);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue__7 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue__8 = attr.html();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__8);
    }
}

