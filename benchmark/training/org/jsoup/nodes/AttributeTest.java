package org.jsoup.nodes;


import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;


public class AttributeTest {
    @Test
    public void html() {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", attr.html());
        Assert.assertEquals(attr.html(), attr.toString());
    }

    @Test
    public void testWithSupplementaryCharacterInAttributeKeyAndValue() {
        String s = new String(Character.toChars(135361));
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals((((s + "=\"A") + s) + "B\""), attr.html());
        Assert.assertEquals(attr.html(), attr.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void validatesKeysNotEmpty() {
        Attribute attr = new Attribute(" ", "Check");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validatesKeysNotEmptyViaSet() {
        Attribute attr = new Attribute("One", "Check");
        attr.setKey(" ");
    }

    @Test
    public void booleanAttributesAreEmptyStringValues() {
        Document doc = Jsoup.parse("<div hidden>");
        Attributes attributes = doc.body().child(0).attributes();
        Assert.assertEquals("", attributes.get("hidden"));
        Attribute first = attributes.iterator().next();
        Assert.assertEquals("hidden", first.getKey());
        Assert.assertEquals("", first.getValue());
    }

    @Test
    public void settersOnOrphanAttribute() {
        Attribute attr = new Attribute("one", "two");
        attr.setKey("three");
        String oldVal = attr.setValue("four");
        Assert.assertEquals("two", oldVal);
        Assert.assertEquals("three", attr.getKey());
        Assert.assertEquals("four", attr.getValue());
        Assert.assertEquals(null, attr.parent);
    }
}

