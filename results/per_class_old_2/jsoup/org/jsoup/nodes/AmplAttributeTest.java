

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

    @org.junit.Test(timeout = 10000)
    public void html_cf81() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        java.lang.String String_vc_2 = "value &";
        org.junit.Assert.assertEquals(String_vc_2, "value &");
        attr.setKey(String_vc_2);
        org.junit.Assert.assertEquals(attr.html(), "value &=\"value &amp;\"");
        org.junit.Assert.assertEquals(attr.getValue(), "value &");
        org.junit.Assert.assertEquals(attr.getKey(), "value &");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    @org.junit.Test(timeout = 10000)
    public void html_cf30() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        int o_html_cf30__5 = attr.hashCode();
        org.junit.Assert.assertEquals(o_html_cf30__5, 234891960);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    @org.junit.Test(timeout = 10000)
    public void html_cf54() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        org.jsoup.nodes.Attribute o_html_cf54__5 = attr.clone();
        org.junit.Assert.assertTrue(o_html_cf54__5.equals(attr));
        org.junit.Assert.assertEquals(o_html_cf54__5.getKey(), "key");
        org.junit.Assert.assertEquals(o_html_cf54__5.html(), "key=\"value &amp;\"");
        org.junit.Assert.assertEquals(o_html_cf54__5.getValue(), "value &");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    @org.junit.Test(timeout = 10000)
    public void html_cf21() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        boolean o_html_cf21__5 = attr.isDataAttribute();
        org.junit.Assert.assertFalse(o_html_cf21__5);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    @org.junit.Test(timeout = 10000)
    public void html_cf45() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        java.lang.String String_vc_0 = "key=\"value &amp;\"";
        org.junit.Assert.assertEquals(String_vc_0, "key=\"value &amp;\"");
        java.lang.String o_html_cf45__7 = attr.setValue(String_vc_0);
        org.junit.Assert.assertEquals(o_html_cf45__7, "value &");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    @org.junit.Test(timeout = 10000)
    public void html_cf33() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        java.lang.String o_html_cf33__5 = attr.getKey();
        org.junit.Assert.assertEquals(o_html_cf33__5, "key");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    @org.junit.Test(timeout = 10000)
    public void html_cf14() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        java.lang.Object vc_3 = new java.lang.Object();
        boolean o_html_cf14__7 = attr.equals(vc_3);
        org.junit.Assert.assertFalse(o_html_cf14__7);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    @org.junit.Test(timeout = 10000)
    public void html_cf36() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        java.lang.String o_html_cf36__5 = attr.getValue();
        org.junit.Assert.assertEquals(o_html_cf36__5, "value &");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    @org.junit.Test(timeout = 10000)
    public void html_cf46() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        java.lang.String vc_23 = new java.lang.String();
        org.junit.Assert.assertEquals(vc_23, "");
        java.lang.String o_html_cf46__7 = attr.setValue(vc_23);
        org.junit.Assert.assertEquals(o_html_cf46__7, "value &");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    @org.junit.Test(timeout = 10000)
    public void html_cf18() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        org.junit.Assert.assertEquals("key=\"value &amp;\"", attr.html());
        boolean o_html_cf18__5 = attr.isBooleanAttribute();
        org.junit.Assert.assertFalse(o_html_cf18__5);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf10816() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        attr.setKey(s);
        org.junit.Assert.assertEquals(attr.html(), "\ud844\udcc1=\"A\ud844\udcc1B\"");
        org.junit.Assert.assertEquals(attr.getValue(), "A\ud844\udcc1B");
        org.junit.Assert.assertEquals(attr.getKey(), "\ud844\udcc1");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf10749() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10749__8 = attr.isDataAttribute();
        org.junit.Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10749__8);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf10817() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        java.lang.String String_vc_335 = "B";
        org.junit.Assert.assertEquals(String_vc_335, "B");
        attr.setKey(String_vc_335);
        org.junit.Assert.assertEquals(attr.html(), "B=\"A\ud844\udcc1B\"");
        org.junit.Assert.assertEquals(attr.getValue(), "A\ud844\udcc1B");
        org.junit.Assert.assertEquals(attr.getKey(), "B");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf10742() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        java.lang.Object vc_4665 = new java.lang.Object();
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10742__10 = attr.equals(vc_4665);
        org.junit.Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10742__10);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf10775() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        java.lang.String String_vc_333 = "A";
        org.junit.Assert.assertEquals(String_vc_333, "A");
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10775__10 = attr.setValue(String_vc_333);
        org.junit.Assert.assertEquals(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10775__10, "A\ud844\udcc1B");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf10785() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        org.jsoup.nodes.Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10785__8 = attr.clone();
        org.junit.Assert.assertEquals(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10785__8.html(), "\ud844\udcc1=\"A\ud844\udcc1B\"");
        org.junit.Assert.assertEquals(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10785__8.getValue(), "A\ud844\udcc1B");
        org.junit.Assert.assertTrue(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10785__8.equals(attr));
        org.junit.Assert.assertEquals(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10785__8.getKey(), "\ud844\udcc1");
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf10746() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10746__8 = attr.isBooleanAttribute();
        org.junit.Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10746__8);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_cf10758() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        int o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10758__8 = attr.hashCode();
        org.junit.Assert.assertEquals(o_testWithSupplementaryCharacterInAttributeKeyAndValue_cf10758__8, 111849895);
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutation10733() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(67680));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(s, "\ud802\udc60");
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        org.junit.Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        org.junit.Assert.assertEquals(attr.html(), attr.toString());
    }
}

