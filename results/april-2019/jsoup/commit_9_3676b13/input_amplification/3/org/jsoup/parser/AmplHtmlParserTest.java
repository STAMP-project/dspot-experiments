package org.jsoup.parser;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.integration.ParseTest;
import org.jsoup.nodes.CDataNode;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class AmplHtmlParserTest {
    @Test(timeout = 10000)
    public void parsesUnterminatedComments_literalMutationNumber333540_failAssert0_literalMutationNumber334346_failAssert0() throws Exception {
        try {
            {
                String html = "<p>Hello<!-- <tr><td>";
                Document doc = Jsoup.parse(html);
                Element p = doc.getElementsByTag("p").get(-1);
                p.text();
                TextNode text = ((TextNode) (p.childNode(0)));
                text.getWholeText();
                Comment comment = ((Comment) (p.childNode(2)));
                comment.getData();
                org.junit.Assert.fail("parsesUnterminatedComments_literalMutationNumber333540 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("parsesUnterminatedComments_literalMutationNumber333540_failAssert0_literalMutationNumber334346 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void parsesUnterminatedComments_literalMutationNumber333531_failAssert0_add334729_failAssert0() throws Exception {
        try {
            {
                String html = "<p>Hello<!-- <tr><td>";
                Document doc = Jsoup.parse(html);
                Element p = doc.getElementsByTag("p").get(-1);
                p.text();
                p.childNode(0);
                TextNode text = ((TextNode) (p.childNode(0)));
                text.getWholeText();
                Comment comment = ((Comment) (p.childNode(1)));
                comment.getData();
                org.junit.Assert.fail("parsesUnterminatedComments_literalMutationNumber333531 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("parsesUnterminatedComments_literalMutationNumber333531_failAssert0_add334729 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void parsesUnterminatedComments_literalMutationNumber333531_failAssert0_add334725_failAssert0() throws Exception {
        try {
            {
                String html = "<p>Hello<!-- <tr><td>";
                Jsoup.parse(html);
                Document doc = Jsoup.parse(html);
                Element p = doc.getElementsByTag("p").get(-1);
                p.text();
                TextNode text = ((TextNode) (p.childNode(0)));
                text.getWholeText();
                Comment comment = ((Comment) (p.childNode(1)));
                comment.getData();
                org.junit.Assert.fail("parsesUnterminatedComments_literalMutationNumber333531 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("parsesUnterminatedComments_literalMutationNumber333531_failAssert0_add334725 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void dropsUnterminatedAttribute_literalMutationString127657_literalMutationString127708() throws Exception {
        String h1 = "<span>Hello <div>there</div <span>now</span></span>";
        Assert.assertEquals("<span>Hello <div>there</div <span>now</span></span>", h1);
        Document doc = Jsoup.parse(h1);
        String o_dropsUnterminatedAttribute_literalMutationString127657__5 = doc.text();
        Assert.assertEquals("Hello therenow", o_dropsUnterminatedAttribute_literalMutationString127657__5);
        Assert.assertEquals("<span>Hello <div>there</div <span>now</span></span>", h1);
    }

    @Test(timeout = 10000)
    public void createsDocumentStructure_literalMutationNumber311624_failAssert0() throws Exception {
        try {
            String html = "<meta name=keywords /><link rel=stylesheet /><title>jsoup</title><p>Hello world</p>";
            Document doc = Jsoup.parse(html);
            Element head = doc.head();
            Element body = doc.body();
            doc.children().size();
            doc.child(0).children().size();
            head.children().size();
            body.children().size();
            head.getElementsByTag("meta").get(-1).attr("name");
            body.getElementsByTag("meta").size();
            doc.title();
            body.text();
            body.children().get(0).text();
            org.junit.Assert.fail("createsDocumentStructure_literalMutationNumber311624 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void createsDocumentStructure_literalMutationNumber311624_failAssert0_add315784_failAssert0() throws Exception {
        try {
            {
                String html = "<meta name=keywords /><link rel=stylesheet /><title>jsoup</title><p>Hello world</p>";
                Document doc = Jsoup.parse(html);
                Element head = doc.head();
                Element body = doc.body();
                doc.children().size();
                doc.child(0).children().size();
                head.children().size();
                body.children().size();
                head.getElementsByTag("meta").get(-1).attr("name");
                body.getElementsByTag("meta").size();
                doc.title();
                doc.title();
                body.text();
                body.children().get(0).text();
                org.junit.Assert.fail("createsDocumentStructure_literalMutationNumber311624 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("createsDocumentStructure_literalMutationNumber311624_failAssert0_add315784 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void createsDocumentStructure_literalMutationNumber311624_failAssert0_add315776_failAssert0() throws Exception {
        try {
            {
                String html = "<meta name=keywords /><link rel=stylesheet /><title>jsoup</title><p>Hello world</p>";
                Document doc = Jsoup.parse(html);
                Element head = doc.head();
                Element body = doc.body();
                doc.children().size();
                doc.child(0).children().size();
                head.children();
                head.children().size();
                body.children().size();
                head.getElementsByTag("meta").get(-1).attr("name");
                body.getElementsByTag("meta").size();
                doc.title();
                body.text();
                body.children().get(0).text();
                org.junit.Assert.fail("createsDocumentStructure_literalMutationNumber311624 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("createsDocumentStructure_literalMutationNumber311624_failAssert0_add315776 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesDataOnlyTags_literalMutationNumber293256_failAssert0_add294873_failAssert0() throws Exception {
        try {
            {
                String t = "<style>font-family: bold</style>";
                List<Element> tels = Jsoup.parse(t).getElementsByTag("style");
                tels.get(-1).data();
                tels.get(0).text();
                String s = "<p>Hello</p><script>obj.insert(\'<a rel=\"none\" />\');\ni++;</script><p>There</p>";
                Document doc = Jsoup.parse(s);
                doc.text();
                doc.text();
                doc.data();
                org.junit.Assert.fail("handlesDataOnlyTags_literalMutationNumber293256 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("handlesDataOnlyTags_literalMutationNumber293256_failAssert0_add294873 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesDataOnlyTags_literalMutationNumber293256_failAssert0_literalMutationNumber294165_failAssert0() throws Exception {
        try {
            {
                String t = "<style>font-family: bold</style>";
                List<Element> tels = Jsoup.parse(t).getElementsByTag("style");
                tels.get(-1).data();
                tels.get(0).text();
                String s = "<p>Hello</p><script>obj.insert(\'<a rel=\"none\" />\');\ni++;</script><p>There</p>";
                Document doc = Jsoup.parse(s);
                doc.text();
                doc.data();
                org.junit.Assert.fail("handlesDataOnlyTags_literalMutationNumber293256 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("handlesDataOnlyTags_literalMutationNumber293256_failAssert0_literalMutationNumber294165 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesDataOnlyTags_literalMutationString293247_literalMutationNumber293800_failAssert0() throws Exception {
        try {
            String t = "<style>fo4t-family: bold</style>";
            List<Element> tels = Jsoup.parse(t).getElementsByTag("style");
            String o_handlesDataOnlyTags_literalMutationString293247__5 = tels.get(0).data();
            String o_handlesDataOnlyTags_literalMutationString293247__7 = tels.get(-1).text();
            String s = "<p>Hello</p><script>obj.insert(\'<a rel=\"none\" />\');\ni++;</script><p>There</p>";
            Document doc = Jsoup.parse(s);
            String o_handlesDataOnlyTags_literalMutationString293247__12 = doc.text();
            String o_handlesDataOnlyTags_literalMutationString293247__13 = doc.data();
            org.junit.Assert.fail("handlesDataOnlyTags_literalMutationString293247_literalMutationNumber293800 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void roundTripsCdata_literalMutationNumber231630_failAssert0_add232343_failAssert0() throws Exception {
        try {
            {
                String h = "<div id=1><![CDATA[\n<html>\n <foo><&amp;]]></div>";
                Document doc = Jsoup.parse(h);
                Element div = doc.getElementById("1");
                div.text();
                div.children().size();
                div.childNodeSize();
                div.outerHtml();
                div.textNodes();
                CDataNode cdata = ((CDataNode) (div.textNodes().get(-1)));
                cdata.text();
                org.junit.Assert.fail("roundTripsCdata_literalMutationNumber231630 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("roundTripsCdata_literalMutationNumber231630_failAssert0_add232343 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesJavadocFont_literalMutationNumber115137_literalMutationNumber115364_failAssert0() throws Exception {
        try {
            String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
            Document doc = Jsoup.parse(h);
            Element a = doc.select("a").first();
            String o_handlesJavadocFont_literalMutationNumber115137__7 = a.text();
            String o_handlesJavadocFont_literalMutationNumber115137__8 = a.child(0).tagName();
            String o_handlesJavadocFont_literalMutationNumber115137__11 = a.child(-1).child(0).tagName();
            org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber115137_literalMutationNumber115364 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesJavadocFont_literalMutationNumber115136_add116199_literalMutationNumber118298_failAssert0() throws Exception {
        try {
            String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
            Document doc = Jsoup.parse(h);
            Elements o_handlesJavadocFont_literalMutationNumber115136_add116199__4 = doc.select("a");
            Element a = doc.select("a").first();
            String o_handlesJavadocFont_literalMutationNumber115136__7 = a.text();
            String o_handlesJavadocFont_literalMutationNumber115136__8 = a.child(-1).tagName();
            String o_handlesJavadocFont_literalMutationNumber115136__11 = a.child(0).child(0).tagName();
            org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber115136_add116199_literalMutationNumber118298 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Ignore
    @Test
    public void handlesMisnestedAInDivs() {
        String h = "<a href='#1'><div><div><a href='#2'>child</a</div</div></a>";
        String w = "<a href=\"#1\"></a><div><a href=\"#1\"></a><div><a href=\"#1\"></a><a href=\"#2\">child</a></div></div>";
        Document doc = Jsoup.parse(h);
        Assert.assertEquals(StringUtil.normaliseWhitespace(w), StringUtil.normaliseWhitespace(doc.body().html()));
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString81339_failAssert0_literalMutationString82298_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "}IYx<");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_101 = endOfEmail > (-1);
                boolean boolean_102 = guarantee > (-1);
                boolean boolean_103 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString81339 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString81339_failAssert0_literalMutationString82298 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContentsnull81376_failAssert0_literalMutationString82700_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf(null);
                boolean boolean_182 = endOfEmail > (-1);
                boolean boolean_183 = guarantee > (-1);
                boolean boolean_184 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContentsnull81376 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testInvalidTableContentsnull81376_failAssert0_literalMutationString82700 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_add81367_literalMutationNumber82099_literalMutationString83824_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.outputSettings();
            doc.outputSettings().prettyPrint(true);
            String rendered = doc.toString();
            int endOfEmail = rendered.indexOf("Comment");
            int guarantee = rendered.indexOf("Why am I here?");
            boolean boolean_194 = endOfEmail > 0;
            boolean boolean_195 = guarantee > (-1);
            boolean boolean_196 = guarantee > endOfEmail;
            org.junit.Assert.fail("testInvalidTableContents_add81367_literalMutationNumber82099_literalMutationString83824 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContentsnull81376_failAssert0_literalMutationString82700_failAssert0_add91426_failAssert0() throws IOException {
        try {
            {
                {
                    ParseTest.getFile("");
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.outputSettings().prettyPrint(true);
                    String rendered = doc.toString();
                    int endOfEmail = rendered.indexOf("Comment");
                    int guarantee = rendered.indexOf(null);
                    boolean boolean_182 = endOfEmail > (-1);
                    boolean boolean_183 = guarantee > (-1);
                    boolean boolean_184 = guarantee > endOfEmail;
                    org.junit.Assert.fail("testInvalidTableContentsnull81376 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testInvalidTableContentsnull81376_failAssert0_literalMutationString82700 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContentsnull81376_failAssert0_literalMutationString82700_failAssert0_add91426 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString81339_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.outputSettings().prettyPrint(true);
            String rendered = doc.toString();
            int endOfEmail = rendered.indexOf("Comment");
            int guarantee = rendered.indexOf("Why am I here?");
            boolean boolean_101 = endOfEmail > (-1);
            boolean boolean_102 = guarantee > (-1);
            boolean boolean_103 = guarantee > endOfEmail;
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString81339 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString81339_failAssert0_literalMutationBoolean82301_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(false);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_101 = endOfEmail > (-1);
                boolean boolean_102 = guarantee > (-1);
                boolean boolean_103 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString81339 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString81339_failAssert0_literalMutationBoolean82301 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString81339_failAssert0_add82942_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                rendered.indexOf("Comment");
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_101 = endOfEmail > (-1);
                boolean boolean_102 = guarantee > (-1);
                boolean boolean_103 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString81339 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString81339_failAssert0_add82942 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString81339_failAssert0_add82944_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_101 = endOfEmail > (-1);
                boolean boolean_102 = guarantee > (-1);
                boolean boolean_103 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString81339 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString81339_failAssert0_add82944 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContentsnull81376_failAssert0_literalMutationString82700_failAssert0_literalMutationNumber89364_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.outputSettings().prettyPrint(true);
                    String rendered = doc.toString();
                    int endOfEmail = rendered.indexOf("Comment");
                    int guarantee = rendered.indexOf(null);
                    boolean boolean_182 = endOfEmail > (-1);
                    boolean boolean_183 = guarantee > 2;
                    boolean boolean_184 = guarantee > endOfEmail;
                    org.junit.Assert.fail("testInvalidTableContentsnull81376 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testInvalidTableContentsnull81376_failAssert0_literalMutationString82700 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContentsnull81376_failAssert0_literalMutationString82700_failAssert0_literalMutationNumber89364 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString412_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTP-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_22 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString412 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0null780_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_22 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0null780 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString11_failAssert0null757_failAssert0_literalMutationString2138_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UF-8");
                    doc.outputSettings().prettyPrint(true);
                    Elements templates = doc.body().getElementsByTag(null);
                    for (Element template : templates) {
                        boolean boolean_12 = (template.childNodes().size()) > 1;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString11 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString11_failAssert0null757 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString11_failAssert0null757_failAssert0_literalMutationString2138 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0null781_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag(null);
                for (Element template : templates) {
                    boolean boolean_22 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0null781 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString6_failAssert0_literalMutationString258_failAssert0_literalMutationString3511_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.outputSettings().prettyPrint(true);
                    Elements templates = doc.body().getElementsByTag("<span>Hello <div>there</div> <span>now</span></span>");
                    for (Element template : templates) {
                        boolean boolean_11 = (template.childNodes().size()) > 1;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString6 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString6_failAssert0_literalMutationString258 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString6_failAssert0_literalMutationString258_failAssert0_literalMutationString3511 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString7_failAssert0_literalMutationNumber356_failAssert0_literalMutationString1902_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "");
                    doc.outputSettings().prettyPrint(true);
                    Elements templates = doc.body().getElementsByTag("template");
                    for (Element template : templates) {
                        boolean boolean_17 = (template.childNodes().size()) > 2;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString7 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString7_failAssert0_literalMutationNumber356 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString7_failAssert0_literalMutationNumber356_failAssert0_literalMutationString1902 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_add672_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_22 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add672 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_add674_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                doc.body().getElementsByTag("template");
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_22 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add674 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_add670_failAssert0() throws IOException {
        try {
            {
                ParseTest.getFile("");
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_22 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add670 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString10_failAssert0_add642_failAssert0_literalMutationString3969_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "U+F-8");
                    doc.outputSettings().prettyPrint(true);
                    Elements templates = doc.body().getElementsByTag("template");
                    for (Element template : templates) {
                        boolean boolean_15 = (template.childNodes().size()) > 1;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString10 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString10_failAssert0_add642 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString10_failAssert0_add642_failAssert0_literalMutationString3969 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.outputSettings().prettyPrint(true);
            Elements templates = doc.body().getElementsByTag("template");
            for (Element template : templates) {
                boolean boolean_22 = (template.childNodes().size()) > 1;
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationNumber423_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_22 = (template.childNodes().size()) > 0;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationNumber423 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString421_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("tempate");
                for (Element template : templates) {
                    boolean boolean_22 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString421 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }
}

