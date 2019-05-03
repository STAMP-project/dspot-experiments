package org.jsoup.parser;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.integration.ParseTest;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class AmplHtmlParserTest {
    @Test(timeout = 10000)
    public void createsDocumentStructure_literalMutationNumber98677_failAssert0() throws Exception {
        try {
            String html = "<meta name=keywords /><link rel=stylesheet /><title>jsoup</title><p>Hello world</p>";
            Document doc = Jsoup.parse(html);
            Element head = doc.head();
            Element body = doc.body();
            doc.children().size();
            doc.child(0).children().size();
            head.children().size();
            body.children().size();
            head.getElementsByTag("meta").get(0).attr("name");
            body.getElementsByTag("meta").size();
            doc.title();
            body.text();
            body.children().get(-1).text();
            org.junit.Assert.fail("createsDocumentStructure_literalMutationNumber98677 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void createsDocumentStructure_literalMutationNumber98679_literalMutationNumber100550_failAssert0() throws Exception {
        try {
            String html = "<meta name=keywords /><link rel=stylesheet /><title>jsoup</title><p>Hello world</p>";
            Document doc = Jsoup.parse(html);
            Element head = doc.head();
            Element body = doc.body();
            int o_createsDocumentStructure_literalMutationNumber98679__8 = doc.children().size();
            int o_createsDocumentStructure_literalMutationNumber98679__10 = doc.child(0).children().size();
            int o_createsDocumentStructure_literalMutationNumber98679__13 = head.children().size();
            int o_createsDocumentStructure_literalMutationNumber98679__15 = body.children().size();
            String o_createsDocumentStructure_literalMutationNumber98679__17 = head.getElementsByTag("meta").get(0).attr("name");
            int o_createsDocumentStructure_literalMutationNumber98679__20 = body.getElementsByTag("meta").size();
            String o_createsDocumentStructure_literalMutationNumber98679__22 = doc.title();
            String o_createsDocumentStructure_literalMutationNumber98679__23 = body.text();
            String o_createsDocumentStructure_literalMutationNumber98679__24 = body.children().get(-1).text();
            org.junit.Assert.fail("createsDocumentStructure_literalMutationNumber98679_literalMutationNumber100550 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cdataNodesAreTextNodes_literalMutationNumber55683_failAssert0_add57009_failAssert0() throws Exception {
        try {
            {
                String h = "<p>One <![CDATA[ Two <& ]]> Three</p>";
                Document doc = Jsoup.parse(h);
                Element p = doc.selectFirst("p");
                List<TextNode> nodes = p.textNodes();
                nodes.get(-1).text();
                nodes.get(1);
                nodes.get(1).text();
                nodes.get(2).text();
                org.junit.Assert.fail("cdataNodesAreTextNodes_literalMutationNumber55683 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("cdataNodesAreTextNodes_literalMutationNumber55683_failAssert0_add57009 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesJavadocFont_literalMutationNumber36676_literalMutationNumber37203_failAssert0() throws Exception {
        try {
            String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
            Document doc = Jsoup.parse(h);
            Element a = doc.select("a").first();
            String o_handlesJavadocFont_literalMutationNumber36676__7 = a.text();
            String o_handlesJavadocFont_literalMutationNumber36676__8 = a.child(-1).tagName();
            String o_handlesJavadocFont_literalMutationNumber36676__11 = a.child(0).child(0).tagName();
            org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber36676_literalMutationNumber37203 should have thrown ArrayIndexOutOfBoundsException");
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
    public void testInvalidTableContents_literalMutationString26850_failAssert0_add28554_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_172 = endOfEmail > (-1);
                boolean boolean_173 = guarantee > (-1);
                boolean boolean_174 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString26850 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString26850_failAssert0_add28554 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString26850_failAssert0_literalMutationString28254_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("<span>Hello <div>there</div> <span>now</span></span>");
                boolean boolean_172 = endOfEmail > (-1);
                boolean boolean_173 = guarantee > (-1);
                boolean boolean_174 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString26850 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString26850_failAssert0_literalMutationString28254 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString26874_literalMutationString27627_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.outputSettings().prettyPrint(true);
            String rendered = doc.toString();
            int endOfEmail = rendered.indexOf("Comment");
            int guarantee = rendered.indexOf("Why amI here?");
            boolean boolean_151 = endOfEmail > (-1);
            boolean boolean_152 = guarantee > (-1);
            boolean boolean_153 = guarantee > endOfEmail;
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString26874_literalMutationString27627 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString26850_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.outputSettings().prettyPrint(true);
            String rendered = doc.toString();
            int endOfEmail = rendered.indexOf("Comment");
            int guarantee = rendered.indexOf("Why am I here?");
            boolean boolean_172 = endOfEmail > (-1);
            boolean boolean_173 = guarantee > (-1);
            boolean boolean_174 = guarantee > endOfEmail;
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString26850 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString26871_literalMutationString27525_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.outputSettings().prettyPrint(true);
            String rendered = doc.toString();
            int endOfEmail = rendered.indexOf("Comment");
            int guarantee = rendered.indexOf("<span>Hello <div>there</div> <span>now</span></span>");
            boolean boolean_142 = endOfEmail > (-1);
            boolean boolean_143 = guarantee > (-1);
            boolean boolean_144 = guarantee > endOfEmail;
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString26871_literalMutationString27525 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString10_failAssert0_literalMutationString352_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "U+F-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_28 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString10 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString10_failAssert0_literalMutationString352 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString446_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "<span>Hello <div>there</div> <span>now</span></span>");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_35 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString446 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_add692_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                doc.body().getElementsByTag("template");
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_35 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add692 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString445_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_35 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString445 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString8_failAssert0_literalMutationString256_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "<span>Hello <div>there</div> <span>now</span></span>");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_23 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString8 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString8_failAssert0_literalMutationString256 should have thrown FileNotFoundException");
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
                boolean boolean_35 = (template.childNodes().size()) > 1;
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationNumber462_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_35 = (template.childNodes().size()) > 0;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationNumber462 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_add689_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Jsoup.parse(in, "UTF-8");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_35 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add689 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0null787_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag(null);
                for (Element template : templates) {
                    boolean boolean_35 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0null787 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0null786_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_35 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0null786 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_add694_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    template.childNodes().size();
                    boolean boolean_35 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add694 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }
}

