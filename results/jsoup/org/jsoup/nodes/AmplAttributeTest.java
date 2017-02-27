

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
}

