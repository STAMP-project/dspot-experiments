package org.jsoup.parser;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.helper.StringUtil;
import org.jsoup.integration.ParseTest;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class AmplHtmlParserTest {
    @Test(timeout = 10000)
    public void parsesUnterminatedComments_literalMutationNumber69399_failAssert0_literalMutationNumber69915_failAssert0() throws Exception {
        try {
            {
                String html = "<p>Hello<!-- <tr><td>";
                Document doc = Jsoup.parse(html);
                Element p = doc.getElementsByTag("p").get(-1);
                p.text();
                TextNode text = ((TextNode) (p.childNode(0)));
                text.getWholeText();
                Comment comment = ((Comment) (p.childNode(0)));
                comment.getData();
                org.junit.Assert.fail("parsesUnterminatedComments_literalMutationNumber69399 should have thrown ClassCastException");
            }
            org.junit.Assert.fail("parsesUnterminatedComments_literalMutationNumber69399_failAssert0_literalMutationNumber69915 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void createsDocumentStructure_literalMutationNumber63635_literalMutationNumber64189_failAssert0() throws Exception {
        try {
            String html = "<meta name=keywords /><link rel=stylesheet /><title>jsoup</title><p>Hello world</p>";
            Document doc = Jsoup.parse(html);
            Element head = doc.head();
            Element body = doc.body();
            int o_createsDocumentStructure_literalMutationNumber63635__8 = doc.children().size();
            int o_createsDocumentStructure_literalMutationNumber63635__10 = doc.child(-1).children().size();
            int o_createsDocumentStructure_literalMutationNumber63635__14 = head.children().size();
            int o_createsDocumentStructure_literalMutationNumber63635__16 = body.children().size();
            String o_createsDocumentStructure_literalMutationNumber63635__18 = head.getElementsByTag("meta").get(0).attr("name");
            int o_createsDocumentStructure_literalMutationNumber63635__21 = body.getElementsByTag("meta").size();
            String o_createsDocumentStructure_literalMutationNumber63635__23 = doc.title();
            String o_createsDocumentStructure_literalMutationNumber63635__24 = body.text();
            String o_createsDocumentStructure_literalMutationNumber63635__25 = body.children().get(0).text();
            org.junit.Assert.fail("createsDocumentStructure_literalMutationNumber63635_literalMutationNumber64189 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesDataOnlyTags_literalMutationString60067_literalMutationNumber60570_failAssert0() throws Exception {
        try {
            String t = "<style>font-family: bold</style>";
            List<Element> tels = Jsoup.parse(t).getElementsByTag("style");
            String o_handlesDataOnlyTags_literalMutationString60067__5 = tels.get(0).data();
            String o_handlesDataOnlyTags_literalMutationString60067__7 = tels.get(-1).text();
            String s = "<p>HellUo</p><script>obj.insert(\'<a rel=\"none\" />\');\ni++;</script><p>There</p>";
            Document doc = Jsoup.parse(s);
            String o_handlesDataOnlyTags_literalMutationString60067__12 = doc.text();
            String o_handlesDataOnlyTags_literalMutationString60067__13 = doc.data();
            org.junit.Assert.fail("handlesDataOnlyTags_literalMutationString60067_literalMutationNumber60570 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesJavadocFont_add20562_literalMutationNumber21000_failAssert0() throws Exception {
        try {
            String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
            Document doc = Jsoup.parse(h);
            Elements o_handlesJavadocFont_add20562__4 = doc.select("a");
            Element a = doc.select("a").first();
            String o_handlesJavadocFont_add20562__8 = a.text();
            String o_handlesJavadocFont_add20562__9 = a.child(-1).tagName();
            String o_handlesJavadocFont_add20562__11 = a.child(0).child(0).tagName();
            org.junit.Assert.fail("handlesJavadocFont_add20562_literalMutationNumber21000 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesJavadocFont_add20568_literalMutationNumber21061_failAssert0() throws Exception {
        try {
            String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
            Document doc = Jsoup.parse(h);
            Element a = doc.select("a").first();
            String o_handlesJavadocFont_add20568__7 = a.text();
            String o_handlesJavadocFont_add20568__8 = a.child(0).tagName();
            Element o_handlesJavadocFont_add20568__10 = a.child(0);
            String o_handlesJavadocFont_add20568__11 = a.child(-1).child(0).tagName();
            org.junit.Assert.fail("handlesJavadocFont_add20568_literalMutationNumber21061 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesJavadocFont_add20566_literalMutationNumber20968_failAssert0() throws Exception {
        try {
            String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
            Document doc = Jsoup.parse(h);
            Element a = doc.select("a").first();
            String o_handlesJavadocFont_add20566__7 = a.text();
            String o_handlesJavadocFont_add20566__8 = a.child(0).tagName();
            String o_handlesJavadocFont_add20566__10 = a.child(-1).child(0).tagName();
            String o_handlesJavadocFont_add20566__13 = a.child(0).child(0).tagName();
            org.junit.Assert.fail("handlesJavadocFont_add20566_literalMutationNumber20968 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesJavadocFont_literalMutationNumber20546_failAssert0_add21928_failAssert0() throws Exception {
        try {
            {
                String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
                Document doc = Jsoup.parse(h);
                doc.select("a");
                Element a = doc.select("a").first();
                a.text();
                a.child(-1).tagName();
                a.child(0).child(0).tagName();
                org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber20546 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber20546_failAssert0_add21928 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesJavadocFont_add20560_literalMutationNumber21133_failAssert0() throws Exception {
        try {
            String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
            Document o_handlesJavadocFont_add20560__2 = Jsoup.parse(h);
            Document doc = Jsoup.parse(h);
            Element a = doc.select("a").first();
            String o_handlesJavadocFont_add20560__8 = a.text();
            String o_handlesJavadocFont_add20560__9 = a.child(-1).tagName();
            String o_handlesJavadocFont_add20560__11 = a.child(0).child(0).tagName();
            org.junit.Assert.fail("handlesJavadocFont_add20560_literalMutationNumber21133 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesJavadocFont_literalMutationNumber20556_failAssert0_add21867_failAssert0() throws Exception {
        try {
            {
                String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
                Document doc = Jsoup.parse(h);
                doc.select("a").first();
                Element a = doc.select("a").first();
                a.text();
                a.child(0).tagName();
                a.child(0).child(-1).tagName();
                org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber20556 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber20556_failAssert0_add21867 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesJavadocFont_literalMutationNumber20551_failAssert0_add21922_failAssert0() throws Exception {
        try {
            {
                String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
                Document doc = Jsoup.parse(h);
                Element a = doc.select("a").first();
                a.text();
                a.child(0).tagName();
                a.child(-1).child(0).tagName();
                a.child(-1).child(0).tagName();
                org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber20551 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber20551_failAssert0_add21922 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesJavadocFont_literalMutationNumber20556_failAssert0_add21875_failAssert0() throws Exception {
        try {
            {
                String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
                Document doc = Jsoup.parse(h);
                Element a = doc.select("a").first();
                a.text();
                a.child(0).tagName();
                a.child(0).child(-1).tagName();
                org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber20556 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber20556_failAssert0_add21875 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesJavadocFont_literalMutationNumber20551_failAssert0_add21916_failAssert0() throws Exception {
        try {
            {
                String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
                Jsoup.parse(h);
                Document doc = Jsoup.parse(h);
                Element a = doc.select("a").first();
                a.text();
                a.child(0).tagName();
                a.child(-1).child(0).tagName();
                org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber20551 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber20551_failAssert0_add21916 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
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
    public void associatedFormControlsWithDisjointForms_literalMutationNumber8989_literalMutationNumber9274_failAssert0() throws Exception {
        try {
            String html = "<table><tr><form><input type=hidden id=1><td><input type=text id=2></td><tr></table>";
            Document doc = Jsoup.parse(html);
            Element el = doc.select("form").first();
            boolean boolean_75 = el instanceof FormElement;
            FormElement form = ((FormElement) (el));
            Elements controls = form.elements();
            int o_associatedFormControlsWithDisjointForms_literalMutationNumber8989__12 = controls.size();
            String o_associatedFormControlsWithDisjointForms_literalMutationNumber8989__13 = controls.get(-1).id();
            String o_associatedFormControlsWithDisjointForms_literalMutationNumber8989__16 = controls.get(1).id();
            String o_associatedFormControlsWithDisjointForms_literalMutationNumber8989__18 = TextUtil.stripNewlines(doc.body().html());
            org.junit.Assert.fail("associatedFormControlsWithDisjointForms_literalMutationNumber8989_literalMutationNumber9274 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void associatedFormControlsWithDisjointForms_add9001_literalMutationNumber9510_failAssert0() throws Exception {
        try {
            String html = "<table><tr><form><input type=hidden id=1><td><input type=text id=2></td><tr></table>";
            Document doc = Jsoup.parse(html);
            Element el = doc.select("form").first();
            boolean boolean_93 = el instanceof FormElement;
            FormElement form = ((FormElement) (el));
            Elements controls = form.elements();
            int o_associatedFormControlsWithDisjointForms_add9001__12 = controls.size();
            String o_associatedFormControlsWithDisjointForms_add9001__13 = controls.get(-1).id();
            String o_associatedFormControlsWithDisjointForms_add9001__15 = controls.get(0).id();
            String o_associatedFormControlsWithDisjointForms_add9001__17 = controls.get(1).id();
            String o_associatedFormControlsWithDisjointForms_add9001__19 = TextUtil.stripNewlines(doc.body().html());
            org.junit.Assert.fail("associatedFormControlsWithDisjointForms_add9001_literalMutationNumber9510 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void associatedFormControlsWithDisjointForms_literalMutationNumber8994_literalMutationNumber9296_failAssert0() throws Exception {
        try {
            String html = "<table><tr><form><input type=hidden id=1><td><input type=text id=2></td><tr></table>";
            Document doc = Jsoup.parse(html);
            Element el = doc.select("form").first();
            boolean boolean_77 = el instanceof FormElement;
            FormElement form = ((FormElement) (el));
            Elements controls = form.elements();
            int o_associatedFormControlsWithDisjointForms_literalMutationNumber8994__12 = controls.size();
            String o_associatedFormControlsWithDisjointForms_literalMutationNumber8994__13 = controls.get(-1).id();
            String o_associatedFormControlsWithDisjointForms_literalMutationNumber8994__15 = controls.get(0).id();
            String o_associatedFormControlsWithDisjointForms_literalMutationNumber8994__18 = TextUtil.stripNewlines(doc.body().html());
            org.junit.Assert.fail("associatedFormControlsWithDisjointForms_literalMutationNumber8994_literalMutationNumber9296 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void associatedFormControlsWithDisjointForms_literalMutationNumber8987_failAssert0_add10315_failAssert0() throws Exception {
        try {
            {
                String html = "<table><tr><form><input type=hidden id=1><td><input type=text id=2></td><tr></table>";
                Document doc = Jsoup.parse(html);
                Element el = doc.select("form").first();
                boolean boolean_66 = el instanceof FormElement;
                FormElement form = ((FormElement) (el));
                Elements controls = form.elements();
                controls.size();
                controls.get(-1).id();
                controls.get(1);
                controls.get(1).id();
                TextUtil.stripNewlines(doc.body().html());
                org.junit.Assert.fail("associatedFormControlsWithDisjointForms_literalMutationNumber8987 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("associatedFormControlsWithDisjointForms_literalMutationNumber8987_failAssert0_add10315 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContentsnull14714_failAssert0_literalMutationString16037_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf(null);
                boolean boolean_176 = endOfEmail > (-1);
                boolean boolean_177 = guarantee > (-1);
                boolean boolean_178 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContentsnull14714 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testInvalidTableContentsnull14714_failAssert0_literalMutationString16037 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString14677_failAssert0_add16336_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                rendered.indexOf("Comment");
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_149 = endOfEmail > (-1);
                boolean boolean_150 = guarantee > (-1);
                boolean boolean_151 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString14677 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString14677_failAssert0_add16336 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString14688_failAssert0_literalMutationString15770_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "tgF{g");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_131 = endOfEmail > (-1);
                boolean boolean_132 = guarantee > (-1);
                boolean boolean_133 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString14688 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString14688_failAssert0_literalMutationString15770 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString14677_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.outputSettings().prettyPrint(true);
            String rendered = doc.toString();
            int endOfEmail = rendered.indexOf("Comment");
            int guarantee = rendered.indexOf("Why am I here?");
            boolean boolean_149 = endOfEmail > (-1);
            boolean boolean_150 = guarantee > (-1);
            boolean boolean_151 = guarantee > endOfEmail;
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString14677 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString14677_failAssert0_literalMutationString15889_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("CZmment");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_149 = endOfEmail > (-1);
                boolean boolean_150 = guarantee > (-1);
                boolean boolean_151 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString14677 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString14677_failAssert0_literalMutationString15889 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString14677_failAssert0null16569_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf(null);
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_149 = endOfEmail > (-1);
                boolean boolean_150 = guarantee > (-1);
                boolean boolean_151 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString14677 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString14677_failAssert0null16569 should have thrown FileNotFoundException");
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

