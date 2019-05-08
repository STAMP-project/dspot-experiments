package org.jsoup.parser;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.integration.ParseTest;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class AmplHtmlParserTest {
    @Ignore
    @Test
    public void handlesMisnestedAInDivs() {
        String h = "<a href='#1'><div><div><a href='#2'>child</a</div</div></a>";
        String w = "<a href=\"#1\"></a><div><a href=\"#1\"></a><div><a href=\"#1\"></a><a href=\"#2\">child</a></div></div>";
        Document doc = Jsoup.parse(h);
        Assert.assertEquals(StringUtil.normaliseWhitespace(w), StringUtil.normaliseWhitespace(doc.body().html()));
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString2197_failAssert0_literalMutationString3521_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UT-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_117 = endOfEmail > (-1);
                boolean boolean_118 = guarantee > (-1);
                boolean boolean_119 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString2197 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString2197_failAssert0_literalMutationString3521 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString2197_failAssert0_literalMutationString3532_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why am I h/re?");
                boolean boolean_117 = endOfEmail > (-1);
                boolean boolean_118 = guarantee > (-1);
                boolean boolean_119 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString2197 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString2197_failAssert0_literalMutationString3532 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString2197_failAssert0null4106_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf(null);
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_117 = endOfEmail > (-1);
                boolean boolean_118 = guarantee > (-1);
                boolean boolean_119 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString2197 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString2197_failAssert0null4106 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString2197_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.outputSettings().prettyPrint(true);
            String rendered = doc.toString();
            int endOfEmail = rendered.indexOf("Comment");
            int guarantee = rendered.indexOf("Why am I here?");
            boolean boolean_117 = endOfEmail > (-1);
            boolean boolean_118 = guarantee > (-1);
            boolean boolean_119 = guarantee > endOfEmail;
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString2197 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString2197_failAssert0_add3887_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                doc.toString();
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_117 = endOfEmail > (-1);
                boolean boolean_118 = guarantee > (-1);
                boolean boolean_119 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString2197 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString2197_failAssert0_add3887 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString2197_failAssert0_add3889_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                rendered.indexOf("Why am I here?");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_117 = endOfEmail > (-1);
                boolean boolean_118 = guarantee > (-1);
                boolean boolean_119 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString2197 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString2197_failAssert0_add3889 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString4_failAssert0_literalMutationString322_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_24 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString4 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString4_failAssert0_literalMutationString322 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_add681_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_29 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add681 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString3_failAssert0_literalMutationString439_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_30 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString3 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString3_failAssert0_literalMutationString439 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_add21_literalMutationString112_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document o_testTemplateInsideTable_add21__3 = Jsoup.parse(in, "UTF-8");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.outputSettings().prettyPrint(true);
            Elements templates = doc.body().getElementsByTag("template");
            for (Element template : templates) {
                boolean boolean_8 = (template.childNodes().size()) > 1;
            }
            org.junit.Assert.fail("testTemplateInsideTable_add21_literalMutationString112 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0null782_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_29 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0null782 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_add687_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_29 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add687 should have thrown FileNotFoundException");
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
                boolean boolean_29 = (template.childNodes().size()) > 1;
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_add24_literalMutationString166_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.outputSettings().prettyPrint(true);
            Elements o_testTemplateInsideTable_add24__7 = doc.body().getElementsByTag("template");
            Elements templates = doc.body().getElementsByTag("template");
            for (Element template : templates) {
                boolean boolean_15 = (template.childNodes().size()) > 1;
            }
            org.junit.Assert.fail("testTemplateInsideTable_add24_literalMutationString166 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString421_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_29 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString421 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesXmlDeclAndCommentsBeforeDoctype_literalMutationString1001_failAssert0_add1830_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                StringUtil.normaliseWhitespace(doc.html());
                doc.head();
                doc.head().select("title").text();
                org.junit.Assert.fail("handlesXmlDeclAndCommentsBeforeDoctype_literalMutationString1001 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("handlesXmlDeclAndCommentsBeforeDoctype_literalMutationString1001_failAssert0_add1830 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesXmlDeclAndCommentsBeforeDoctype_literalMutationString1016_literalMutationString1300_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            String o_handlesXmlDeclAndCommentsBeforeDoctype_literalMutationString1016__5 = StringUtil.normaliseWhitespace(doc.html());
            String o_handlesXmlDeclAndCommentsBeforeDoctype_literalMutationString1016__7 = doc.head().select("tiUtle").text();
            org.junit.Assert.fail("handlesXmlDeclAndCommentsBeforeDoctype_literalMutationString1016_literalMutationString1300 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesXmlDeclAndCommentsBeforeDoctype_literalMutationString1001_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            StringUtil.normaliseWhitespace(doc.html());
            doc.head().select("title").text();
            org.junit.Assert.fail("handlesXmlDeclAndCommentsBeforeDoctype_literalMutationString1001 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesXmlDeclAndCommentsBeforeDoctype_literalMutationString1001_failAssert0_literalMutationString1552_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                StringUtil.normaliseWhitespace(doc.html());
                doc.head().select("title").text();
                org.junit.Assert.fail("handlesXmlDeclAndCommentsBeforeDoctype_literalMutationString1001 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("handlesXmlDeclAndCommentsBeforeDoctype_literalMutationString1001_failAssert0_literalMutationString1552 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }
}

