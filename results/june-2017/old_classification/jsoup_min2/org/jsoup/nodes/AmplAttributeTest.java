

package org.jsoup.nodes;


public class AmplAttributeTest {
    @org.junit.Test
    public void html() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    @org.junit.Test
    public void testWithSupplementaryCharacterInAttributeKeyAndValue() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_cf19() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_3 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_html_cf19__7 = // StatementAdderMethod cloned existing statement
attr.equals(vc_3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_html_cf19__7);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_cf50() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_0 = "value &";
        // AssertGenerator replace invocation
        java.lang.String o_html_cf50__7 = // StatementAdderMethod cloned existing statement
attr.setValue(String_vc_0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_html_cf50__7, "value &");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_cf35() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        // AssertGenerator replace invocation
        int o_html_cf35__5 = // StatementAdderMethod cloned existing statement
attr.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_html_cf35__5, 234891960);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_cf18() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2 = (java.lang.Object)null;
        // AssertGenerator replace invocation
        boolean o_html_cf18__7 = // StatementAdderMethod cloned existing statement
attr.equals(vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_html_cf18__7);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf23323() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        // AssertGenerator replace invocation
        int o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf23323__8 = // StatementAdderMethod cloned existing statement
attr.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf23323__8, 111849895);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf23314() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        // AssertGenerator replace invocation
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf23314__8 = // StatementAdderMethod cloned existing statement
attr.isDataAttribute();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf23314__8);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf23339() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        // AssertGenerator replace invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf23339__8 = // StatementAdderMethod cloned existing statement
attr.setValue(s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf23339__8, "A\ud844\udcc1B");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf23306() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        // StatementAdderOnAssert create null value
        java.lang.Object vc_8990 = (java.lang.Object)null;
        // AssertGenerator replace invocation
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf23306__10 = // StatementAdderMethod cloned existing statement
attr.equals(vc_8990);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf23306__10);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf23307() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_8991 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf23307__10 = // StatementAdderMethod cloned existing statement
attr.equals(vc_8991);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf23307__10);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }
}

