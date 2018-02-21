package org.jsoup.nodes;


import org.junit.Assert;
import org.junit.Test;


public class AmplAttributeTest {
    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue() {
        String s = new String(Character.toChars(135361));
        // AssertGenerator add assertion
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        String String_0 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_0);
        // AssertGenerator create local variable with return value of invocation
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue__7 = attr.html();
        // AssertGenerator add assertion
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__7);
        // AssertGenerator create local variable with return value of invocation
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue__8 = attr.html();
        // AssertGenerator create local variable with return value of invocation
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue__9 = attr.toString();
        // AssertGenerator add assertion
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__9);
        // AssertGenerator add assertion
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__7);
        // AssertGenerator add assertion
        Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_0);
        // AssertGenerator add assertion
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__8);
    }

    @Test(timeout = 10000)
    public void html() {
        Attribute attr = new Attribute("key", "value &");
        // AssertGenerator add assertion
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator create local variable with return value of invocation
        String o_html__3 = attr.html();
        // AssertGenerator add assertion
        Assert.assertEquals("key=\"value &amp;\"", o_html__3);
        // AssertGenerator create local variable with return value of invocation
        String o_html__4 = attr.html();
        // AssertGenerator add assertion
        Assert.assertEquals("key=\"value &amp;\"", o_html__4);
        // AssertGenerator create local variable with return value of invocation
        String o_html__5 = attr.toString();
        // AssertGenerator add assertion
        Assert.assertEquals("key=\"value &amp;\"", o_html__5);
        // AssertGenerator add assertion
        Assert.assertEquals("key=\"value &amp;\"", o_html__3);
        // AssertGenerator add assertion
        Assert.assertEquals("key=\"value &amp;\"", o_html__4);
        // AssertGenerator add assertion
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
    }
}

