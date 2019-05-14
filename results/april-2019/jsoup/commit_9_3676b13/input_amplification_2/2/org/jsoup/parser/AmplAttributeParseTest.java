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
    public void parsesBooleanAttributes_add1339() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_add1339__6 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_add1339__6);
        String o_parsesBooleanAttributes_add1339__7 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_add1339__7);
        String o_parsesBooleanAttributes_add1339__8 = el.attr("empty");
        Assert.assertEquals("", o_parsesBooleanAttributes_add1339__8);
        List<Attribute> attributes = el.attributes().asList();
        int o_parsesBooleanAttributes_add1339__12 = attributes.size();
        Assert.assertEquals(3, ((int) (o_parsesBooleanAttributes_add1339__12)));
        boolean boolean_65 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_add1339__15 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add1339__15)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_add1339__15)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add1339__15)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add1339__15)).getKey());
        boolean boolean_66 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_67 = (attributes.get(2)) instanceof BooleanAttribute;
        el.outerHtml();
        Assert.assertEquals("123", o_parsesBooleanAttributes_add1339__6);
        Assert.assertEquals("", o_parsesBooleanAttributes_add1339__7);
        Assert.assertEquals("", o_parsesBooleanAttributes_add1339__8);
        Assert.assertEquals(3, ((int) (o_parsesBooleanAttributes_add1339__12)));
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add1339__15)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_add1339__15)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add1339__15)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add1339__15)).getKey());
    }

    @Test(timeout = 10000)
    public void parsesBooleanAttributes_literalMutationNumber1314_add4397() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_literalMutationNumber1314__6 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_literalMutationNumber1314__6);
        String o_parsesBooleanAttributes_literalMutationNumber1314__7 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationNumber1314__7);
        String o_parsesBooleanAttributes_literalMutationNumber1314__8 = el.attr("empty");
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationNumber1314__8);
        List<Attribute> attributes = el.attributes().asList();
        int o_parsesBooleanAttributes_literalMutationNumber1314__12 = attributes.size();
        boolean boolean_92 = (attributes.get(1)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_literalMutationNumber1314_add4397__24 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber1314_add4397__24)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber1314_add4397__24)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber1314_add4397__24)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber1314_add4397__24)).getKey());
        boolean boolean_93 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_94 = (attributes.get(2)) instanceof BooleanAttribute;
        el.outerHtml();
        Assert.assertEquals("123", o_parsesBooleanAttributes_literalMutationNumber1314__6);
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationNumber1314__7);
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationNumber1314__8);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber1314_add4397__24)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber1314_add4397__24)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber1314_add4397__24)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber1314_add4397__24)).getKey());
    }

    @Test(timeout = 10000)
    public void parsesBooleanAttributes_literalMutationString1307_add4735() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_literalMutationString1307__6 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_literalMutationString1307__6);
        String o_parsesBooleanAttributes_literalMutationString1307__7 = el.attr("bolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString1307__7);
        String o_parsesBooleanAttributes_literalMutationString1307__8 = el.attr("empty");
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString1307__8);
        List<Attribute> attributes = el.attributes().asList();
        int o_parsesBooleanAttributes_literalMutationString1307__12 = attributes.size();
        boolean boolean_212 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_literalMutationString1307_add4735__23 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString1307_add4735__23)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString1307_add4735__23)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString1307_add4735__23)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString1307_add4735__23)).getKey());
        boolean boolean_213 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_214 = (attributes.get(2)) instanceof BooleanAttribute;
        el.outerHtml();
        Assert.assertEquals("123", o_parsesBooleanAttributes_literalMutationString1307__6);
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString1307__7);
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString1307__8);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString1307_add4735__23)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString1307_add4735__23)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString1307_add4735__23)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString1307_add4735__23)).getKey());
    }

    @Test(timeout = 10000)
    public void parsesBooleanAttributes_literalMutationString1306_add4384() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_literalMutationString1306__6 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_literalMutationString1306__6);
        String o_parsesBooleanAttributes_literalMutationString1306__7 = el.attr("bAoolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString1306__7);
        String o_parsesBooleanAttributes_literalMutationString1306__8 = el.attr("empty");
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString1306__8);
        List<Attribute> attributes = el.attributes().asList();
        int o_parsesBooleanAttributes_literalMutationString1306__12 = attributes.size();
        boolean boolean_89 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_literalMutationString1306_add4384__23 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString1306_add4384__23)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString1306_add4384__23)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString1306_add4384__23)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString1306_add4384__23)).getKey());
        boolean boolean_90 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_91 = (attributes.get(2)) instanceof BooleanAttribute;
        el.outerHtml();
        Assert.assertEquals("123", o_parsesBooleanAttributes_literalMutationString1306__6);
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString1306__7);
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString1306__8);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString1306_add4384__23)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString1306_add4384__23)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString1306_add4384__23)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString1306_add4384__23)).getKey());
    }

    @Test(timeout = 10000)
    public void parsesBooleanAttributes_add1331_add4188() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Document o_parsesBooleanAttributes_add1331__2 = Jsoup.parse(html);
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_add1331__7 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_add1331__7);
        String o_parsesBooleanAttributes_add1331__8 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_add1331__8);
        String o_parsesBooleanAttributes_add1331__9 = el.attr("empty");
        Assert.assertEquals("", o_parsesBooleanAttributes_add1331__9);
        List<Attribute> attributes = el.attributes().asList();
        int o_parsesBooleanAttributes_add1331__13 = attributes.size();
        boolean boolean_47 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_add1331_add4188__26 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add1331_add4188__26)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_add1331_add4188__26)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add1331_add4188__26)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add1331_add4188__26)).getKey());
        boolean boolean_48 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_49 = (attributes.get(2)) instanceof BooleanAttribute;
        el.outerHtml();
        Assert.assertEquals("123", o_parsesBooleanAttributes_add1331__7);
        Assert.assertEquals("", o_parsesBooleanAttributes_add1331__8);
        Assert.assertEquals("", o_parsesBooleanAttributes_add1331__9);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add1331_add4188__26)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_add1331_add4188__26)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add1331_add4188__26)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add1331_add4188__26)).getKey());
    }
}

