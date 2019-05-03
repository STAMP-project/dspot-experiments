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
    public void testInvalidTableContents_literalMutationString7283_literalMutationString7572_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.outputSettings().prettyPrint(true);
            String rendered = doc.toString();
            int endOfEmail = rendered.indexOf("<span>Hello <div>there</div> <span>now</span></span>");
            int guarantee = rendered.indexOf("Why am I here?");
            boolean boolean_72 = endOfEmail > (-1);
            boolean boolean_73 = guarantee > (-1);
            boolean boolean_74 = guarantee > endOfEmail;
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString7283_literalMutationString7572 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString7270_failAssert0_add8934_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings();
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_99 = endOfEmail > (-1);
                boolean boolean_100 = guarantee > (-1);
                boolean boolean_101 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString7270 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString7270_failAssert0_add8934 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString7270_failAssert0_add8937_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                rendered.indexOf("Why am I here?");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_99 = endOfEmail > (-1);
                boolean boolean_100 = guarantee > (-1);
                boolean boolean_101 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString7270 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString7270_failAssert0_add8937 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString7270_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.outputSettings().prettyPrint(true);
            String rendered = doc.toString();
            int endOfEmail = rendered.indexOf("Comment");
            int guarantee = rendered.indexOf("Why am I here?");
            boolean boolean_99 = endOfEmail > (-1);
            boolean boolean_100 = guarantee > (-1);
            boolean boolean_101 = guarantee > endOfEmail;
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString7270 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString7270_failAssert0null9163_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf(null);
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_99 = endOfEmail > (-1);
                boolean boolean_100 = guarantee > (-1);
                boolean boolean_101 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString7270 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString7270_failAssert0null9163 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString7270_failAssert0null9164_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf(null);
                boolean boolean_99 = endOfEmail > (-1);
                boolean boolean_100 = guarantee > (-1);
                boolean boolean_101 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString7270 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString7270_failAssert0null9164 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString7284_literalMutationString7502_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.outputSettings().prettyPrint(true);
            String rendered = doc.toString();
            int endOfEmail = rendered.indexOf(".V>+:d]");
            int guarantee = rendered.indexOf("Why am I here?");
            boolean boolean_60 = endOfEmail > (-1);
            boolean boolean_61 = guarantee > (-1);
            boolean boolean_62 = guarantee > endOfEmail;
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString7284_literalMutationString7502 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString7270_failAssert0_literalMutationNumber8514_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_99 = endOfEmail > (-1);
                boolean boolean_100 = guarantee > 0;
                boolean boolean_101 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString7270 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString7270_failAssert0_literalMutationNumber8514 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString7270_failAssert0_literalMutationString8502_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why wm I here?");
                boolean boolean_99 = endOfEmail > (-1);
                boolean boolean_100 = guarantee > (-1);
                boolean boolean_101 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString7270 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString7270_failAssert0_literalMutationString8502 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString7272_failAssert0_literalMutationString8413_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_90 = endOfEmail > (-1);
                boolean boolean_91 = guarantee > (-1);
                boolean boolean_92 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString7272 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString7272_failAssert0_literalMutationString8413 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString7285_literalMutationString7881_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.outputSettings().prettyPrint(true);
            String rendered = doc.toString();
            int endOfEmail = rendered.indexOf("C/omment");
            int guarantee = rendered.indexOf("Why am I here?");
            boolean boolean_126 = endOfEmail > (-1);
            boolean boolean_127 = guarantee > (-1);
            boolean boolean_128 = guarantee > endOfEmail;
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString7285_literalMutationString7881 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_add653_failAssert0_add5522_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Jsoup.parse(in, "UTF-8");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.outputSettings().prettyPrint(true);
                    Elements templates = doc.body().getElementsByTag("template");
                    for (Element template : templates) {
                        template.childNodes();
                        boolean boolean_16 = (template.childNodes().size()) > 1;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add653 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add653_failAssert0_add5522 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0null770_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_16 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0null770 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0null771_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag(null);
                for (Element template : templates) {
                    boolean boolean_16 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0null771 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_add657_failAssert0_add5952_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.outputSettings().prettyPrint(true);
                    doc.outputSettings().prettyPrint(true);
                    doc.body();
                    Elements templates = doc.body().getElementsByTag("template");
                    for (Element template : templates) {
                        boolean boolean_16 = (template.childNodes().size()) > 1;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add657 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add657_failAssert0_add5952 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString350_failAssert0_literalMutationString2203_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "One <b>Two <b>Three");
                    doc.outputSettings().prettyPrint(true);
                    Elements templates = doc.body().getElementsByTag("template");
                    for (Element template : templates) {
                        boolean boolean_16 = (template.childNodes().size()) > 1;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString350 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString350_failAssert0_literalMutationString2203 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_add657_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                doc.body();
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_16 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add657 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString352_failAssert0_add5198_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF8");
                    doc.outputSettings().prettyPrint(true);
                    Elements templates = doc.body().getElementsByTag("template");
                    for (Element template : templates) {
                        template.childNodes();
                        boolean boolean_16 = (template.childNodes().size()) > 1;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString352 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString352_failAssert0_add5198 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTablenull32_failAssert0_literalMutationString214_failAssert0_add5073_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.outputSettings().prettyPrint(true);
                    Elements templates = doc.body().getElementsByTag(null);
                    for (Element template : templates) {
                        boolean boolean_7 = (template.childNodes().size()) > 1;
                    }
                    org.junit.Assert.fail("testTemplateInsideTablenull32 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testTemplateInsideTablenull32_failAssert0_literalMutationString214 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTablenull32_failAssert0_literalMutationString214_failAssert0_add5073 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTablenull32_failAssert0_literalMutationString214_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag(null);
                for (Element template : templates) {
                    boolean boolean_7 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTablenull32 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testTemplateInsideTablenull32_failAssert0_literalMutationString214 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationNumber362_failAssert0_add5786_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.outputSettings().prettyPrint(true);
                    doc.body();
                    Elements templates = doc.body().getElementsByTag("template");
                    for (Element template : templates) {
                        boolean boolean_16 = (template.childNodes().size()) > 2;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationNumber362 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationNumber362_failAssert0_add5786 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_add657_failAssert0_literalMutationString3997_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "<span>Hello <div>there</div> <span>now</span></span>");
                    doc.outputSettings().prettyPrint(true);
                    doc.body();
                    Elements templates = doc.body().getElementsByTag("template");
                    for (Element template : templates) {
                        boolean boolean_16 = (template.childNodes().size()) > 1;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add657 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add657_failAssert0_literalMutationString3997 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationNumber362_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_16 = (template.childNodes().size()) > 2;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationNumber362 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_add657_failAssert0_literalMutationString4004_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.outputSettings().prettyPrint(true);
                    doc.body();
                    Elements templates = doc.body().getElementsByTag("w*:Bca-}");
                    for (Element template : templates) {
                        boolean boolean_16 = (template.childNodes().size()) > 1;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add657 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add657_failAssert0_literalMutationString4004 should have thrown FileNotFoundException");
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
                boolean boolean_16 = (template.childNodes().size()) > 1;
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString352_failAssert0null6441_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF8");
                    doc.outputSettings().prettyPrint(true);
                    Elements templates = doc.body().getElementsByTag(null);
                    for (Element template : templates) {
                        boolean boolean_16 = (template.childNodes().size()) > 1;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString352 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString352_failAssert0null6441 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_add653_failAssert0_literalMutationBoolean2985_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Jsoup.parse(in, "UTF-8");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.outputSettings().prettyPrint(false);
                    Elements templates = doc.body().getElementsByTag("template");
                    for (Element template : templates) {
                        boolean boolean_16 = (template.childNodes().size()) > 1;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add653 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add653_failAssert0_literalMutationBoolean2985 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString352_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_16 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString352 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString350_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "<span>Hello <div>there</div> <span>now</span></span>");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_16 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString350 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTablenull32_failAssert0_literalMutationString214_failAssert0_literalMutationString1865_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTN-8");
                    doc.outputSettings().prettyPrint(true);
                    Elements templates = doc.body().getElementsByTag(null);
                    for (Element template : templates) {
                        boolean boolean_7 = (template.childNodes().size()) > 1;
                    }
                    org.junit.Assert.fail("testTemplateInsideTablenull32 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testTemplateInsideTablenull32_failAssert0_literalMutationString214 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTablenull32_failAssert0_literalMutationString214_failAssert0_literalMutationString1865 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_add653_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Jsoup.parse(in, "UTF-8");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_16 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add653 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString353_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "YTF-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_16 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString353 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_add660_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_16 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add660 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString9_failAssert0_literalMutationString511_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF5-8");
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_27 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString9 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString9_failAssert0_literalMutationString511 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_add654_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                doc.outputSettings().prettyPrint(true);
                Elements templates = doc.body().getElementsByTag("template");
                for (Element template : templates) {
                    boolean boolean_16 = (template.childNodes().size()) > 1;
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add654 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString350_failAssert0_add5203_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "<span>Hello <div>there</div> <span>now</span></span>");
                    doc.outputSettings();
                    doc.outputSettings().prettyPrint(true);
                    Elements templates = doc.body().getElementsByTag("template");
                    for (Element template : templates) {
                        boolean boolean_16 = (template.childNodes().size()) > 1;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString350 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString350_failAssert0_add5203 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_add657_failAssert0_add5959_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.outputSettings().prettyPrint(true);
                    doc.body();
                    Elements templates = doc.body().getElementsByTag("template");
                    for (Element template : templates) {
                        boolean boolean_16 = (template.childNodes().size()) > 1;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add657 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_add657_failAssert0_add5959 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString352_failAssert0_literalMutationString2183_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "<span>Hello <div>there</div> <span>now</span></span>");
                    doc.outputSettings().prettyPrint(true);
                    Elements templates = doc.body().getElementsByTag("template");
                    for (Element template : templates) {
                        boolean boolean_16 = (template.childNodes().size()) > 1;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString352 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationString352_failAssert0_literalMutationString2183 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationNumber362_failAssert0null6687_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.outputSettings().prettyPrint(true);
                    Elements templates = doc.body().getElementsByTag(null);
                    for (Element template : templates) {
                        boolean boolean_16 = (template.childNodes().size()) > 2;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationNumber362 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString1_failAssert0_literalMutationNumber362_failAssert0null6687 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }
}

