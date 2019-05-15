package org.jsoup.nodes;


import java.util.Collection;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.parser.Tag;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";

    @Test(timeout = 10000)
    public void testSetText_literalMutationString162085_failAssert0_literalMutationNumber163047_failAssert0() throws Exception {
        try {
            {
                String h = "";
                Document doc = Jsoup.parse(h);
                doc.text();
                doc.select("p").get(-1).text();
                Element div = doc.getElementById("1").text("Gone");
                div.text();
                doc.select("p").size();
                org.junit.Assert.fail("testSetText_literalMutationString162085 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testSetText_literalMutationString162085_failAssert0_literalMutationNumber163047 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAddBooleanAttribute_literalMutationString323685_add329671_add338746() throws Exception {
        Element div = new Element(Tag.valueOf("div"), "");
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Element) (div)).hasParent());
        Element o_testAddBooleanAttribute_literalMutationString323685__4 = div.attr("true", true);
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).hasParent());
        Element o_testAddBooleanAttribute_literalMutationString323685__5 = div.attr("false", "value");
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).hasParent());
        Element o_testAddBooleanAttribute_literalMutationString323685__6 = div.attr("false", false);
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).hasParent());
        boolean o_testAddBooleanAttribute_literalMutationString323685__7 = div.hasAttr("true");
        String o_testAddBooleanAttribute_literalMutationString323685__8 = div.attr("true");
        Assert.assertEquals("", o_testAddBooleanAttribute_literalMutationString323685__8);
        List<Attribute> attributes = div.attributes().asList();
        int o_testAddBooleanAttribute_literalMutationString323685__12 = attributes.size();
        Attribute o_testAddBooleanAttribute_literalMutationString323685_add329671__25 = attributes.get(0);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).getKey());
        boolean boolean_707 = (attributes.get(0)) instanceof BooleanAttribute;
        boolean o_testAddBooleanAttribute_literalMutationString323685__15 = div.hasAttr("4-&l$");
        div.outerHtml();
        ((Element) (div)).getAllElements().isEmpty();
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Element) (div)).hasParent());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).hasParent());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).hasParent());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).hasParent());
        Assert.assertEquals("", o_testAddBooleanAttribute_literalMutationString323685__8);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).getKey());
    }

    @Test(timeout = 10000)
    public void testAddBooleanAttribute_literalMutationString323685_add329671_literalMutationString333271() throws Exception {
        Element div = new Element(Tag.valueOf("div"), "");
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Element) (div)).hasParent());
        Element o_testAddBooleanAttribute_literalMutationString323685__4 = div.attr("true", true);
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).hasParent());
        Element o_testAddBooleanAttribute_literalMutationString323685__5 = div.attr("false", "va{lue");
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).hasParent());
        Element o_testAddBooleanAttribute_literalMutationString323685__6 = div.attr("false", false);
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).hasParent());
        boolean o_testAddBooleanAttribute_literalMutationString323685__7 = div.hasAttr("true");
        String o_testAddBooleanAttribute_literalMutationString323685__8 = div.attr("true");
        Assert.assertEquals("", o_testAddBooleanAttribute_literalMutationString323685__8);
        List<Attribute> attributes = div.attributes().asList();
        int o_testAddBooleanAttribute_literalMutationString323685__12 = attributes.size();
        Attribute o_testAddBooleanAttribute_literalMutationString323685_add329671__25 = attributes.get(0);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).getKey());
        boolean boolean_707 = (attributes.get(0)) instanceof BooleanAttribute;
        boolean o_testAddBooleanAttribute_literalMutationString323685__15 = div.hasAttr("4-&l$");
        div.outerHtml();
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Element) (div)).hasParent());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).hasParent());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).hasParent());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).hasParent());
        Assert.assertEquals("", o_testAddBooleanAttribute_literalMutationString323685__8);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).getKey());
    }

    @Test(timeout = 10000)
    public void testAddBooleanAttribute_literalMutationString323685_add329671_remove342311() throws Exception {
        Element div = new Element(Tag.valueOf("div"), "");
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Element) (div)).hasParent());
        Element o_testAddBooleanAttribute_literalMutationString323685__4 = div.attr("true", true);
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).hasParent());
        Element o_testAddBooleanAttribute_literalMutationString323685__5 = div.attr("false", "value");
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).hasParent());
        Element o_testAddBooleanAttribute_literalMutationString323685__6 = div.attr("false", false);
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).hasParent());
        boolean o_testAddBooleanAttribute_literalMutationString323685__7 = div.hasAttr("true");
        String o_testAddBooleanAttribute_literalMutationString323685__8 = div.attr("true");
        Assert.assertEquals("", o_testAddBooleanAttribute_literalMutationString323685__8);
        List<Attribute> attributes = div.attributes().asList();
        int o_testAddBooleanAttribute_literalMutationString323685__12 = attributes.size();
        Attribute o_testAddBooleanAttribute_literalMutationString323685_add329671__25 = attributes.get(0);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).getKey());
        boolean boolean_707 = (attributes.get(0)) instanceof BooleanAttribute;
        boolean o_testAddBooleanAttribute_literalMutationString323685__15 = div.hasAttr("4-&l$");
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Element) (div)).hasParent());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).hasParent());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).hasParent());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).isBlock());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).hasParent());
        Assert.assertEquals("", o_testAddBooleanAttribute_literalMutationString323685__8);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).getKey());
    }

    @Test(timeout = 10000)
    public void testAddBooleanAttribute_literalMutationString323685_add329671() throws Exception {
        Element div = new Element(Tag.valueOf("div"), "");
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Element) (div)).hasParent());
        Element o_testAddBooleanAttribute_literalMutationString323685__4 = div.attr("true", true);
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).hasText());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).hasParent());
        Element o_testAddBooleanAttribute_literalMutationString323685__5 = div.attr("false", "value");
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).hasText());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).hasParent());
        Element o_testAddBooleanAttribute_literalMutationString323685__6 = div.attr("false", false);
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).hasText());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).hasParent());
        boolean o_testAddBooleanAttribute_literalMutationString323685__7 = div.hasAttr("true");
        String o_testAddBooleanAttribute_literalMutationString323685__8 = div.attr("true");
        Assert.assertEquals("", o_testAddBooleanAttribute_literalMutationString323685__8);
        List<Attribute> attributes = div.attributes().asList();
        int o_testAddBooleanAttribute_literalMutationString323685__12 = attributes.size();
        Attribute o_testAddBooleanAttribute_literalMutationString323685_add329671__25 = attributes.get(0);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).getKey());
        boolean boolean_707 = (attributes.get(0)) instanceof BooleanAttribute;
        boolean o_testAddBooleanAttribute_literalMutationString323685__15 = div.hasAttr("4-&l$");
        div.outerHtml();
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Element) (div)).hasParent());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).hasText());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__4)).hasParent());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).hasText());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__5)).hasParent());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).hasText());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString323685__6)).hasParent());
        Assert.assertEquals("", o_testAddBooleanAttribute_literalMutationString323685__8);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString323685_add329671__25)).getKey());
    }

    @Test(timeout = 10000)
    public void testAddBooleanAttribute_add323697() throws Exception {
        Element div = new Element(Tag.valueOf("div"), "");
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Element) (div)).hasParent());
        Element o_testAddBooleanAttribute_add323697__4 = div.attr("true", true);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add323697__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add323697__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add323697__4)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add323697__4)).hasParent());
        Element o_testAddBooleanAttribute_add323697__5 = div.attr("false", "value");
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add323697__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add323697__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add323697__5)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add323697__5)).hasParent());
        Element o_testAddBooleanAttribute_add323697__6 = div.attr("false", false);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add323697__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add323697__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add323697__6)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add323697__6)).hasParent());
        boolean o_testAddBooleanAttribute_add323697__7 = div.hasAttr("true");
        Assert.assertTrue(o_testAddBooleanAttribute_add323697__7);
        String o_testAddBooleanAttribute_add323697__8 = div.attr("true");
        Assert.assertEquals("", o_testAddBooleanAttribute_add323697__8);
        List<Attribute> attributes = div.attributes().asList();
        int o_testAddBooleanAttribute_add323697__12 = attributes.size();
        Assert.assertEquals(1, ((int) (o_testAddBooleanAttribute_add323697__12)));
        Attribute o_testAddBooleanAttribute_add323697__13 = attributes.get(0);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add323697__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add323697__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add323697__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add323697__13)).getKey());
        boolean boolean_689 = (attributes.get(0)) instanceof BooleanAttribute;
        boolean o_testAddBooleanAttribute_add323697__16 = div.hasAttr("false");
        Assert.assertFalse(o_testAddBooleanAttribute_add323697__16);
        div.outerHtml();
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Element) (div)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add323697__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add323697__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add323697__4)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add323697__4)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add323697__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add323697__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add323697__5)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add323697__5)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add323697__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add323697__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add323697__6)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add323697__6)).hasParent());
        Assert.assertTrue(o_testAddBooleanAttribute_add323697__7);
        Assert.assertEquals("", o_testAddBooleanAttribute_add323697__8);
        Assert.assertEquals(1, ((int) (o_testAddBooleanAttribute_add323697__12)));
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add323697__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add323697__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add323697__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add323697__13)).getKey());
        Assert.assertFalse(o_testAddBooleanAttribute_add323697__16);
    }
}

