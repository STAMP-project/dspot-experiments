

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
    public void html_cf81() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_2 = "value &";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_2, "value &");
        // StatementAdderMethod cloned existing statement
        attr.setKey(String_vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsoup.nodes.Attribute)attr).getValue(), "value &");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsoup.nodes.Attribute)attr).getKey(), "value &");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsoup.nodes.Attribute)attr).html(), "value &=\"value &amp;\"");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_cf30() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        // AssertGenerator replace invocation
        int o_html_cf30__5 = // StatementAdderMethod cloned existing statement
attr.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_html_cf30__5, 234891960);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_cf54() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        // AssertGenerator replace invocation
        org.jsoup.nodes.Attribute o_html_cf54__5 = // StatementAdderMethod cloned existing statement
attr.clone();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_html_cf54__5.equals(attr));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsoup.nodes.Attribute)o_html_cf54__5).getKey(), "key");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsoup.nodes.Attribute)o_html_cf54__5).getValue(), "value &");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsoup.nodes.Attribute)o_html_cf54__5).html(), "key=\"value &amp;\"");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_cf21() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        // AssertGenerator replace invocation
        boolean o_html_cf21__5 = // StatementAdderMethod cloned existing statement
attr.isDataAttribute();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_html_cf21__5);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_cf45() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_0 = "key=\"value &amp;\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_0, "key=\"value &amp;\"");
        // AssertGenerator replace invocation
        java.lang.String o_html_cf45__7 = // StatementAdderMethod cloned existing statement
attr.setValue(String_vc_0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_html_cf45__7, "value &");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_cf14() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_3 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_html_cf14__7 = // StatementAdderMethod cloned existing statement
attr.equals(vc_3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_html_cf14__7);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf10816() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        // StatementAdderMethod cloned existing statement
        attr.setKey(s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsoup.nodes.Attribute)attr).getValue(), "A\ud844\udcc1B");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsoup.nodes.Attribute)attr).getKey(), "\ud844\udcc1");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsoup.nodes.Attribute)attr).html(), "\ud844\udcc1=\"A\ud844\udcc1B\"");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf10749() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        // AssertGenerator replace invocation
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10749__8 = // StatementAdderMethod cloned existing statement
attr.isDataAttribute();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10749__8);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf10817() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_335 = "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_335, "B\"");
        // StatementAdderMethod cloned existing statement
        attr.setKey(String_vc_335);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsoup.nodes.Attribute)attr).getValue(), "A\ud844\udcc1B");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsoup.nodes.Attribute)attr).getKey(), "B\"");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsoup.nodes.Attribute)attr).html(), "B\"=\"A\ud844\udcc1B\"");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf10761() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        // AssertGenerator replace invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10761__8 = // StatementAdderMethod cloned existing statement
attr.getKey();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10761__8, "\ud844\udcc1");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf10775() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_333 = "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_333, "B\"");
        // AssertGenerator replace invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10775__10 = // StatementAdderMethod cloned existing statement
attr.setValue(String_vc_333);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10775__10, "A\ud844\udcc1B");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf10742() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_4665 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10742__10 = // StatementAdderMethod cloned existing statement
attr.equals(vc_4665);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10742__10);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf10764() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        // AssertGenerator replace invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10764__8 = // StatementAdderMethod cloned existing statement
attr.getValue();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10764__8, "A\ud844\udcc1B");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf10785() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        // AssertGenerator replace invocation
        org.jsoup.nodes.Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10785__8 = // StatementAdderMethod cloned existing statement
attr.clone();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10785__8).html(), "\ud844\udcc1=\"A\ud844\udcc1B\"");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10785__8.equals(attr));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10785__8).getKey(), "\ud844\udcc1");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10785__8).getValue(), "A\ud844\udcc1B");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf10774() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        // AssertGenerator replace invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10774__8 = // StatementAdderMethod cloned existing statement
attr.setValue(s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10774__8, "A\ud844\udcc1B");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf10746() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        // AssertGenerator replace invocation
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10746__8 = // StatementAdderMethod cloned existing statement
attr.isBooleanAttribute();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10746__8);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf10758() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        // AssertGenerator replace invocation
        int o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10758__8 = // StatementAdderMethod cloned existing statement
attr.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10758__8, 111849895);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }
}

