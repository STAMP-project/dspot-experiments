package org.jsoup.parser;


import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.BooleanAttribute;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;


public class AmplAttributeParseTest {
    @Test(timeout = 10000)
    public void parsesBooleanAttributes_literalMutationString8928_literalMutationNumber9746_add22614() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_literalMutationString8928__6 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_literalMutationString8928__6);
        String o_parsesBooleanAttributes_literalMutationString8928__7 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString8928__7);
        String o_parsesBooleanAttributes_literalMutationString8928__8 = el.attr("epty");
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString8928__8);
        List<Attribute> attributes = el.attributes().asList();
        int o_parsesBooleanAttributes_literalMutationString8928__12 = attributes.size();
        boolean boolean_83 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_literalMutationString8928_literalMutationNumber9746_add22614__24 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString8928_literalMutationNumber9746_add22614__24)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString8928_literalMutationNumber9746_add22614__24)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString8928_literalMutationNumber9746_add22614__24)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString8928_literalMutationNumber9746_add22614__24)).getKey());
        boolean boolean_84 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_85 = (attributes.get(2)) instanceof BooleanAttribute;
        String o_parsesBooleanAttributes_literalMutationString8928__19 = el.outerHtml();
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", o_parsesBooleanAttributes_literalMutationString8928__19);
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Assert.assertEquals("123", o_parsesBooleanAttributes_literalMutationString8928__6);
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString8928__7);
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString8928__8);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString8928_literalMutationNumber9746_add22614__24)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString8928_literalMutationNumber9746_add22614__24)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString8928_literalMutationNumber9746_add22614__24)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString8928_literalMutationNumber9746_add22614__24)).getKey());
    }

    @Test(timeout = 10000)
    public void parsesBooleanAttributes_add8951_add12196() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_add8951__6 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_add8951__6);
        String o_parsesBooleanAttributes_add8951__7 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_add8951__7);
        String o_parsesBooleanAttributes_add8951__8 = el.attr("empty");
        Assert.assertEquals("", o_parsesBooleanAttributes_add8951__8);
        Attributes o_parsesBooleanAttributes_add8951__9 = el.attributes();
        List<Attribute> attributes = el.attributes().asList();
        int o_parsesBooleanAttributes_add8951__13 = attributes.size();
        boolean boolean_185 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_add8951_add12196__26 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add8951_add12196__26)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_add8951_add12196__26)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add8951_add12196__26)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add8951_add12196__26)).getKey());
        boolean boolean_186 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_187 = (attributes.get(2)) instanceof BooleanAttribute;
        String o_parsesBooleanAttributes_add8951__20 = el.outerHtml();
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", o_parsesBooleanAttributes_add8951__20);
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Assert.assertEquals("123", o_parsesBooleanAttributes_add8951__6);
        Assert.assertEquals("", o_parsesBooleanAttributes_add8951__7);
        Assert.assertEquals("", o_parsesBooleanAttributes_add8951__8);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add8951_add12196__26)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_add8951_add12196__26)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add8951_add12196__26)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add8951_add12196__26)).getKey());
    }

    @Test(timeout = 10000)
    public void parsesBooleanAttributes_literalMutationString8924_add12049_add22837() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Element o_parsesBooleanAttributes_literalMutationString8924_add12049__2 = Jsoup.parse(html).select("a").first();
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_literalMutationString8924__6 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_literalMutationString8924__6);
        String o_parsesBooleanAttributes_literalMutationString8924__7 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString8924__7);
        String o_parsesBooleanAttributes_literalMutationString8924__8 = el.attr("<span>Hello <div>there</div> <span>now</span></span>");
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString8924__8);
        List<Attribute> attributes = el.attributes().asList();
        int o_parsesBooleanAttributes_literalMutationString8924__12 = attributes.size();
        boolean boolean_134 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_literalMutationString8924_add12049_add22837__28 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString8924_add12049_add22837__28)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString8924_add12049_add22837__28)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString8924_add12049_add22837__28)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString8924_add12049_add22837__28)).getKey());
        boolean boolean_135 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_136 = (attributes.get(2)) instanceof BooleanAttribute;
        String o_parsesBooleanAttributes_literalMutationString8924__19 = el.outerHtml();
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", o_parsesBooleanAttributes_literalMutationString8924__19);
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Assert.assertEquals("123", o_parsesBooleanAttributes_literalMutationString8924__6);
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString8924__7);
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationString8924__8);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString8924_add12049_add22837__28)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString8924_add12049_add22837__28)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString8924_add12049_add22837__28)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationString8924_add12049_add22837__28)).getKey());
    }

    @Test(timeout = 10000)
    public void parsesBooleanAttributes_add8954() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_add8954__6 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_add8954__6);
        String o_parsesBooleanAttributes_add8954__7 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_add8954__7);
        String o_parsesBooleanAttributes_add8954__8 = el.attr("empty");
        Assert.assertEquals("", o_parsesBooleanAttributes_add8954__8);
        List<Attribute> attributes = el.attributes().asList();
        int o_parsesBooleanAttributes_add8954__12 = attributes.size();
        Assert.assertEquals(3, ((int) (o_parsesBooleanAttributes_add8954__12)));
        boolean boolean_200 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_add8954__15 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add8954__15)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_add8954__15)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add8954__15)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add8954__15)).getKey());
        boolean boolean_201 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_202 = (attributes.get(2)) instanceof BooleanAttribute;
        String o_parsesBooleanAttributes_add8954__20 = el.outerHtml();
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", o_parsesBooleanAttributes_add8954__20);
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Assert.assertEquals("123", o_parsesBooleanAttributes_add8954__6);
        Assert.assertEquals("", o_parsesBooleanAttributes_add8954__7);
        Assert.assertEquals("", o_parsesBooleanAttributes_add8954__8);
        Assert.assertEquals(3, ((int) (o_parsesBooleanAttributes_add8954__12)));
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add8954__15)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_add8954__15)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add8954__15)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add8954__15)).getKey());
    }

    @Test(timeout = 10000)
    public void parsesBooleanAttributes_literalMutationNumber8930_failAssert0_literalMutationString11199_failAssert0() throws Exception {
        try {
            {
                String html = "<a normal=\"123\" boolean empty=\"\"></a>";
                Element el = Jsoup.parse(html).select("a").first();
                el.attr("normal");
                el.attr("boolean");
                el.attr("");
                List<Attribute> attributes = el.attributes().asList();
                attributes.size();
                boolean boolean_59 = (attributes.get(-1)) instanceof BooleanAttribute;
                boolean boolean_60 = (attributes.get(1)) instanceof BooleanAttribute;
                boolean boolean_61 = (attributes.get(2)) instanceof BooleanAttribute;
                el.outerHtml();
                org.junit.Assert.fail("parsesBooleanAttributes_literalMutationNumber8930 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("parsesBooleanAttributes_literalMutationNumber8930_failAssert0_literalMutationString11199 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void parsesBooleanAttributes_add8951_add12196_add22317() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_add8951__6 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_add8951__6);
        String o_parsesBooleanAttributes_add8951__7 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_add8951__7);
        String o_parsesBooleanAttributes_add8951__8 = el.attr("empty");
        Assert.assertEquals("", o_parsesBooleanAttributes_add8951__8);
        Attributes o_parsesBooleanAttributes_add8951__9 = el.attributes();
        List<Attribute> attributes = el.attributes().asList();
        int o_parsesBooleanAttributes_add8951__13 = attributes.size();
        boolean boolean_185 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_add8951_add12196_add22317__26 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add8951_add12196_add22317__26)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_add8951_add12196_add22317__26)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add8951_add12196_add22317__26)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add8951_add12196_add22317__26)).getKey());
        Attribute o_parsesBooleanAttributes_add8951_add12196__26 = attributes.get(1);
        boolean boolean_186 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_187 = (attributes.get(2)) instanceof BooleanAttribute;
        String o_parsesBooleanAttributes_add8951__20 = el.outerHtml();
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", o_parsesBooleanAttributes_add8951__20);
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Assert.assertEquals("123", o_parsesBooleanAttributes_add8951__6);
        Assert.assertEquals("", o_parsesBooleanAttributes_add8951__7);
        Assert.assertEquals("", o_parsesBooleanAttributes_add8951__8);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add8951_add12196_add22317__26)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_add8951_add12196_add22317__26)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add8951_add12196_add22317__26)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add8951_add12196_add22317__26)).getKey());
    }

    @Test(timeout = 10000)
    public void parsesBooleanAttributes_literalMutationNumber8943_add12020() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_literalMutationNumber8943__6 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_literalMutationNumber8943__6);
        String o_parsesBooleanAttributes_literalMutationNumber8943__7 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationNumber8943__7);
        String o_parsesBooleanAttributes_literalMutationNumber8943__8 = el.attr("empty");
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationNumber8943__8);
        List<Attribute> attributes = el.attributes().asList();
        int o_parsesBooleanAttributes_literalMutationNumber8943__12 = attributes.size();
        boolean boolean_122 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_literalMutationNumber8943_add12020__23 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber8943_add12020__23)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber8943_add12020__23)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber8943_add12020__23)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber8943_add12020__23)).getKey());
        boolean boolean_123 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_124 = (attributes.get(0)) instanceof BooleanAttribute;
        String o_parsesBooleanAttributes_literalMutationNumber8943__20 = el.outerHtml();
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", o_parsesBooleanAttributes_literalMutationNumber8943__20);
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Assert.assertEquals("123", o_parsesBooleanAttributes_literalMutationNumber8943__6);
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationNumber8943__7);
        Assert.assertEquals("", o_parsesBooleanAttributes_literalMutationNumber8943__8);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber8943_add12020__23)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber8943_add12020__23)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber8943_add12020__23)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_literalMutationNumber8943_add12020__23)).getKey());
    }
}

