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
    public void parsesBooleanAttributes_add1626() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_add1626__6 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_add1626__6);
        String o_parsesBooleanAttributes_add1626__7 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_add1626__7);
        String o_parsesBooleanAttributes_add1626__8 = el.attr("empty");
        Assert.assertEquals("", o_parsesBooleanAttributes_add1626__8);
        List<Attribute> attributes = el.attributes().asList();
        int o_parsesBooleanAttributes_add1626__12 = attributes.size();
        Assert.assertEquals(3, ((int) (o_parsesBooleanAttributes_add1626__12)));
        boolean boolean_194 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_add1626__15 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add1626__15)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_add1626__15)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add1626__15)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add1626__15)).getKey());
        boolean boolean_195 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_196 = (attributes.get(2)) instanceof BooleanAttribute;
        String o_parsesBooleanAttributes_add1626__20 = el.outerHtml();
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", o_parsesBooleanAttributes_add1626__20);
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Assert.assertEquals("123", o_parsesBooleanAttributes_add1626__6);
        Assert.assertEquals("", o_parsesBooleanAttributes_add1626__7);
        Assert.assertEquals("", o_parsesBooleanAttributes_add1626__8);
        Assert.assertEquals(3, ((int) (o_parsesBooleanAttributes_add1626__12)));
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add1626__15)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_add1626__15)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add1626__15)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add1626__15)).getKey());
    }

    @Test(timeout = 10000)
    public void parsesBooleanAttributes_literalMutationNumber1604_add4718() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_literalMutationNumber1604__6 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_literalMutationNumber1604__6);
        String o_parsesBooleanAttributes_literalMutationNumber1604__7 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationNumber1604__7);
        String o_parsesBooleanAttributes_literalMutationNumber1604__8 = el.attr("empty");
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationNumber1604__8);
        List<Attribute> attributes = el.attributes().asList();
        int o_parsesBooleanAttributes_literalMutationNumber1604__12 = attributes.size();
        boolean boolean_137 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_literalMutationNumber1604_add4718__24 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber1604_add4718__24)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber1604_add4718__24)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber1604_add4718__24)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber1604_add4718__24)).getKey());
        boolean boolean_138 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_139 = (attributes.get(2)) instanceof BooleanAttribute;
        String o_parsesBooleanAttributes_literalMutationNumber1604__20 = el.outerHtml();
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", o_parsesBooleanAttributes_literalMutationNumber1604__20);
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Assert.assertEquals("123", o_parsesBooleanAttributes_literalMutationNumber1604__6);
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationNumber1604__7);
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationNumber1604__8);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber1604_add4718__24)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber1604_add4718__24)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber1604_add4718__24)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber1604_add4718__24)).getKey());
    }
}

