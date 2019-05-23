package org.jsoup.parser;


import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.BooleanAttribute;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;


public class AmplAttributeParseTest {
    @Test(timeout = 10000)
    public void parsesBooleanAttributes_add4942() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_add4942__6 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_add4942__6);
        String o_parsesBooleanAttributes_add4942__7 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_add4942__7);
        String o_parsesBooleanAttributes_add4942__8 = el.attr("empty");
        Assert.assertEquals("", o_parsesBooleanAttributes_add4942__8);
        List<Attribute> attributes = el.attributes().asList();
        int o_parsesBooleanAttributes_add4942__12 = attributes.size();
        Assert.assertEquals(3, ((int) (o_parsesBooleanAttributes_add4942__12)));
        boolean boolean_35 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_add4942__15 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add4942__15)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_add4942__15)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add4942__15)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add4942__15)).getKey());
        boolean boolean_36 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_37 = (attributes.get(2)) instanceof BooleanAttribute;
        el.outerHtml();
        Assert.assertEquals("123", o_parsesBooleanAttributes_add4942__6);
        Assert.assertEquals("", o_parsesBooleanAttributes_add4942__7);
        Assert.assertEquals("", o_parsesBooleanAttributes_add4942__8);
        Assert.assertEquals(3, ((int) (o_parsesBooleanAttributes_add4942__12)));
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add4942__15)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_add4942__15)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add4942__15)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add4942__15)).getKey());
    }

    @Test(timeout = 10000)
    public void parsesBooleanAttributes_literalMutationString4916_add8053() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_literalMutationString4916__6 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_literalMutationString4916__6);
        String o_parsesBooleanAttributes_literalMutationString4916__7 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString4916__7);
        String o_parsesBooleanAttributes_literalMutationString4916__8 = el.attr("emlty");
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString4916__8);
        List<Attribute> attributes = el.attributes().asList();
        int o_parsesBooleanAttributes_literalMutationString4916__12 = attributes.size();
        boolean boolean_107 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_literalMutationString4916_add8053__23 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString4916_add8053__23)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString4916_add8053__23)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString4916_add8053__23)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString4916_add8053__23)).getKey());
        boolean boolean_108 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_109 = (attributes.get(2)) instanceof BooleanAttribute;
        el.outerHtml();
        Assert.assertEquals("123", o_parsesBooleanAttributes_literalMutationString4916__6);
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString4916__7);
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString4916__8);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString4916_add8053__23)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString4916_add8053__23)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString4916_add8053__23)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString4916_add8053__23)).getKey());
    }

    @Test(timeout = 10000)
    public void parsesBooleanAttributes_literalMutationString4899_add8335_add13486() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_literalMutationString4899__6 = el.attr("");
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString4899__6);
        String o_parsesBooleanAttributes_literalMutationString4899__7 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString4899__7);
        String o_parsesBooleanAttributes_literalMutationString4899__8 = el.attr("empty");
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString4899__8);
        List<Attribute> o_parsesBooleanAttributes_literalMutationString4899_add8335__15 = el.attributes().asList();
        List<Attribute> attributes = el.attributes().asList();
        int o_parsesBooleanAttributes_literalMutationString4899__12 = attributes.size();
        boolean boolean_206 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_literalMutationString4899_add8335_add13486__27 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString4899_add8335_add13486__27)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString4899_add8335_add13486__27)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString4899_add8335_add13486__27)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString4899_add8335_add13486__27)).getKey());
        boolean boolean_207 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_208 = (attributes.get(2)) instanceof BooleanAttribute;
        el.outerHtml();
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString4899__6);
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString4899__7);
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString4899__8);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString4899_add8335_add13486__27)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString4899_add8335_add13486__27)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString4899_add8335_add13486__27)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString4899_add8335_add13486__27)).getKey());
    }
}

