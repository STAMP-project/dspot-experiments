package org.jsoup.parser;


import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.BooleanAttribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;


public class AmplAttributeParseTest {
    @Test(timeout = 10000)
    public void parsesBooleanAttributes_add8698() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_add8698__6 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_add8698__6);
        String o_parsesBooleanAttributes_add8698__7 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_add8698__7);
        String o_parsesBooleanAttributes_add8698__8 = el.attr("empty");
        Assert.assertEquals("", o_parsesBooleanAttributes_add8698__8);
        List<Attribute> attributes = el.attributes().asList();
        attributes.size();
        boolean boolean_188 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_add8698__15 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add8698__15)).toString());
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add8698__15)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add8698__15)).getKey());
        boolean boolean_189 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_190 = (attributes.get(2)) instanceof BooleanAttribute;
        String o_parsesBooleanAttributes_add8698__20 = el.outerHtml();
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", o_parsesBooleanAttributes_add8698__20);
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Assert.assertEquals("123", o_parsesBooleanAttributes_add8698__6);
        Assert.assertEquals("", o_parsesBooleanAttributes_add8698__7);
        Assert.assertEquals("", o_parsesBooleanAttributes_add8698__8);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add8698__15)).toString());
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add8698__15)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add8698__15)).getKey());
    }

    @Test(timeout = 10000)
    public void parsesBooleanAttributes_add8688_add12556_add19831() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Document o_parsesBooleanAttributes_add8688_add12556__2 = Jsoup.parse(html);
        Element o_parsesBooleanAttributes_add8688__2 = Jsoup.parse(html).select("a").first();
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_add8688__9 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_add8688__9);
        String o_parsesBooleanAttributes_add8688__10 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_add8688__10);
        String o_parsesBooleanAttributes_add8688__11 = el.attr("empty");
        Assert.assertEquals("", o_parsesBooleanAttributes_add8688__11);
        List<Attribute> attributes = el.attributes().asList();
        attributes.size();
        boolean boolean_158 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_add8688_add12556_add19831__29 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add8688_add12556_add19831__29)).toString());
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add8688_add12556_add19831__29)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add8688_add12556_add19831__29)).getKey());
        boolean boolean_159 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_160 = (attributes.get(2)) instanceof BooleanAttribute;
        String o_parsesBooleanAttributes_add8688__22 = el.outerHtml();
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", o_parsesBooleanAttributes_add8688__22);
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Assert.assertEquals("123", o_parsesBooleanAttributes_add8688__9);
        Assert.assertEquals("", o_parsesBooleanAttributes_add8688__10);
        Assert.assertEquals("", o_parsesBooleanAttributes_add8688__11);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add8688_add12556_add19831__29)).toString());
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add8688_add12556_add19831__29)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add8688_add12556_add19831__29)).getKey());
    }

    @Test(timeout = 10000)
    public void parsesBooleanAttributes_add8690_add12592() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Document o_parsesBooleanAttributes_add8690__2 = Jsoup.parse(html);
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_add8690__7 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_add8690__7);
        String o_parsesBooleanAttributes_add8690__8 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_add8690__8);
        String o_parsesBooleanAttributes_add8690__9 = el.attr("empty");
        Assert.assertEquals("", o_parsesBooleanAttributes_add8690__9);
        List<Attribute> attributes = el.attributes().asList();
        attributes.size();
        boolean boolean_164 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_add8690_add12592__24 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add8690_add12592__24)).toString());
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add8690_add12592__24)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add8690_add12592__24)).getKey());
        boolean boolean_165 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_166 = (attributes.get(2)) instanceof BooleanAttribute;
        String o_parsesBooleanAttributes_add8690__20 = el.outerHtml();
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", o_parsesBooleanAttributes_add8690__20);
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Assert.assertEquals("123", o_parsesBooleanAttributes_add8690__7);
        Assert.assertEquals("", o_parsesBooleanAttributes_add8690__8);
        Assert.assertEquals("", o_parsesBooleanAttributes_add8690__9);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add8690_add12592__24)).toString());
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add8690_add12592__24)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add8690_add12592__24)).getKey());
    }

    @Test(timeout = 10000)
    public void parsesBooleanAttributes_add8688_add12606_add22815() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Element o_parsesBooleanAttributes_add8688__2 = Jsoup.parse(html).select("a").first();
        Document o_parsesBooleanAttributes_add8688_add12606__7 = Jsoup.parse(html);
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_add8688__9 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_add8688__9);
        String o_parsesBooleanAttributes_add8688__10 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_add8688__10);
        String o_parsesBooleanAttributes_add8688__11 = el.attr("empty");
        Assert.assertEquals("", o_parsesBooleanAttributes_add8688__11);
        List<Attribute> attributes = el.attributes().asList();
        attributes.size();
        boolean boolean_158 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_add8688_add12606_add22815__29 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add8688_add12606_add22815__29)).toString());
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add8688_add12606_add22815__29)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add8688_add12606_add22815__29)).getKey());
        boolean boolean_159 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_160 = (attributes.get(2)) instanceof BooleanAttribute;
        String o_parsesBooleanAttributes_add8688__22 = el.outerHtml();
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", o_parsesBooleanAttributes_add8688__22);
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Assert.assertEquals("123", o_parsesBooleanAttributes_add8688__9);
        Assert.assertEquals("", o_parsesBooleanAttributes_add8688__10);
        Assert.assertEquals("", o_parsesBooleanAttributes_add8688__11);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add8688_add12606_add22815__29)).toString());
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add8688_add12606_add22815__29)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add8688_add12606_add22815__29)).getKey());
    }

    @Test(timeout = 10000)
    public void parsesBooleanAttributes_add8688_add12637() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Element o_parsesBooleanAttributes_add8688__2 = Jsoup.parse(html).select("a").first();
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_add8688__9 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_add8688__9);
        String o_parsesBooleanAttributes_add8688__10 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_add8688__10);
        String o_parsesBooleanAttributes_add8688__11 = el.attr("empty");
        Assert.assertEquals("", o_parsesBooleanAttributes_add8688__11);
        List<Attribute> attributes = el.attributes().asList();
        attributes.size();
        boolean boolean_158 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_add8688_add12637__26 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add8688_add12637__26)).toString());
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add8688_add12637__26)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add8688_add12637__26)).getKey());
        boolean boolean_159 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_160 = (attributes.get(2)) instanceof BooleanAttribute;
        String o_parsesBooleanAttributes_add8688__22 = el.outerHtml();
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", o_parsesBooleanAttributes_add8688__22);
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Assert.assertEquals("123", o_parsesBooleanAttributes_add8688__9);
        Assert.assertEquals("", o_parsesBooleanAttributes_add8688__10);
        Assert.assertEquals("", o_parsesBooleanAttributes_add8688__11);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add8688_add12637__26)).toString());
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add8688_add12637__26)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add8688_add12637__26)).getKey());
    }
}

