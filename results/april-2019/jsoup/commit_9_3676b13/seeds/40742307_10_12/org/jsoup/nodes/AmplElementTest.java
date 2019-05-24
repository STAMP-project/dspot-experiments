package org.jsoup.nodes;


import java.util.Collection;
import java.util.List;
import org.jsoup.parser.Tag;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";

    @Test(timeout = 10000)
    public void testAddBooleanAttribute_add4337() throws Exception {
        Element div = new Element(Tag.valueOf("div"), "");
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Element) (div)).hasParent());
        Element o_testAddBooleanAttribute_add4337__4 = div.attr("true", true);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4337__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add4337__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add4337__4)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4337__4)).hasParent());
        Element o_testAddBooleanAttribute_add4337__5 = div.attr("false", "value");
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4337__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add4337__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add4337__5)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4337__5)).hasParent());
        Element o_testAddBooleanAttribute_add4337__6 = div.attr("false", false);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4337__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add4337__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add4337__6)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4337__6)).hasParent());
        boolean o_testAddBooleanAttribute_add4337__7 = div.hasAttr("true");
        Assert.assertTrue(o_testAddBooleanAttribute_add4337__7);
        String o_testAddBooleanAttribute_add4337__8 = div.attr("true");
        Assert.assertEquals("", o_testAddBooleanAttribute_add4337__8);
        List<Attribute> attributes = div.attributes().asList();
        int o_testAddBooleanAttribute_add4337__12 = attributes.size();
        Assert.assertEquals(1, ((int) (o_testAddBooleanAttribute_add4337__12)));
        Attribute o_testAddBooleanAttribute_add4337__13 = attributes.get(0);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add4337__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add4337__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add4337__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add4337__13)).getKey());
        boolean boolean_721 = (attributes.get(0)) instanceof BooleanAttribute;
        boolean o_testAddBooleanAttribute_add4337__16 = div.hasAttr("false");
        Assert.assertFalse(o_testAddBooleanAttribute_add4337__16);
        div.outerHtml();
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Element) (div)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4337__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add4337__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add4337__4)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4337__4)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4337__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add4337__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add4337__5)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4337__5)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4337__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add4337__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add4337__6)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4337__6)).hasParent());
        Assert.assertTrue(o_testAddBooleanAttribute_add4337__7);
        Assert.assertEquals("", o_testAddBooleanAttribute_add4337__8);
        Assert.assertEquals(1, ((int) (o_testAddBooleanAttribute_add4337__12)));
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add4337__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add4337__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add4337__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add4337__13)).getKey());
        Assert.assertFalse(o_testAddBooleanAttribute_add4337__16);
    }
}

