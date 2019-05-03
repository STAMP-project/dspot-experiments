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
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class AmplHtmlParserTest {
    @Test(timeout = 10000)
    public void parsesUnterminatedComments_add439801_literalMutationNumber439915_failAssert0() throws Exception {
        try {
            String html = "<p>Hello<!-- <tr><td>";
            Document doc = Jsoup.parse(html);
            Element p = doc.getElementsByTag("p").get(0);
            String o_parsesUnterminatedComments_add439801__7 = p.text();
            Node o_parsesUnterminatedComments_add439801__8 = p.childNode(0);
            TextNode text = ((TextNode) (p.childNode(-1)));
            text.getWholeText();
            Comment comment = ((Comment) (p.childNode(1)));
            comment.getData();
            org.junit.Assert.fail("parsesUnterminatedComments_add439801_literalMutationNumber439915 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void createsDocumentStructure_add417696_literalMutationString418137_literalMutationNumber425931_failAssert0() throws Exception {
        try {
            String html = "<meta name=keywords /><link rel=stylesheet /><title>jsoup</title><p>Hello world</p>";
            Document doc = Jsoup.parse(html);
            Element head = doc.head();
            Element body = doc.body();
            int o_createsDocumentStructure_add417696__8 = doc.children().size();
            int o_createsDocumentStructure_add417696__10 = doc.child(0).children().size();
            int o_createsDocumentStructure_add417696__13 = head.children().size();
            int o_createsDocumentStructure_add417696__15 = body.children().size();
            Element o_createsDocumentStructure_add417696__17 = head.getElementsByTag("meta").get(0);
            String o_createsDocumentStructure_add417696__19 = head.getElementsByTag("meta").get(-1).attr("");
            int o_createsDocumentStructure_add417696__22 = body.getElementsByTag("meta").size();
            String o_createsDocumentStructure_add417696__24 = doc.title();
            String o_createsDocumentStructure_add417696__25 = body.text();
            String o_createsDocumentStructure_add417696__26 = body.children().get(0).text();
            org.junit.Assert.fail("createsDocumentStructure_add417696_literalMutationString418137_literalMutationNumber425931 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void createsDocumentStructure_literalMutationNumber417651_failAssert0_literalMutationNumber420493_failAssert0_literalMutationString428995_failAssert0() throws Exception {
        try {
            {
                {
                    String html = "<meta name=keywords /><link rel=stylesheet /><title>jsoup</title><p>Hello world</p>";
                    Document doc = Jsoup.parse(html);
                    Element head = doc.head();
                    Element body = doc.body();
                    doc.children().size();
                    doc.child(-1).children().size();
                    head.children().size();
                    body.children().size();
                    head.getElementsByTag("meta").get(0).attr("name");
                    body.getElementsByTag("").size();
                    doc.title();
                    body.text();
                    body.children().get(0).text();
                    org.junit.Assert.fail("createsDocumentStructure_literalMutationNumber417651 should have thrown ArrayIndexOutOfBoundsException");
                }
                org.junit.Assert.fail("createsDocumentStructure_literalMutationNumber417651_failAssert0_literalMutationNumber420493 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("createsDocumentStructure_literalMutationNumber417651_failAssert0_literalMutationNumber420493_failAssert0_literalMutationString428995 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void createsDocumentStructure_literalMutationNumber417662_failAssert0_add421849_failAssert0_add432696_failAssert0() throws Exception {
        try {
            {
                {
                    String html = "<meta name=keywords /><link rel=stylesheet /><title>jsoup</title><p>Hello world</p>";
                    Document doc = Jsoup.parse(html);
                    Element head = doc.head();
                    Element body = doc.body();
                    doc.children().size();
                    doc.child(0).children().size();
                    head.children().size();
                    head.children().size();
                    body.children().size();
                    head.getElementsByTag("meta").get(-1).attr("name");
                    head.getElementsByTag("meta").get(-1).attr("name");
                    body.getElementsByTag("meta").size();
                    doc.title();
                    body.text();
                    body.children().get(0).text();
                    org.junit.Assert.fail("createsDocumentStructure_literalMutationNumber417662 should have thrown ArrayIndexOutOfBoundsException");
                }
                org.junit.Assert.fail("createsDocumentStructure_literalMutationNumber417662_failAssert0_add421849 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("createsDocumentStructure_literalMutationNumber417662_failAssert0_add421849_failAssert0_add432696 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void createsDocumentStructure_add417688_literalMutationNumber418323_failAssert0() throws Exception {
        try {
            String html = "<meta name=keywords /><link rel=stylesheet /><title>jsoup</title><p>Hello world</p>";
            Document doc = Jsoup.parse(html);
            Element head = doc.head();
            Element body = doc.body();
            int o_createsDocumentStructure_add417688__8 = doc.children().size();
            int o_createsDocumentStructure_add417688__10 = doc.child(-1).children().size();
            int o_createsDocumentStructure_add417688__13 = doc.child(0).children().size();
            int o_createsDocumentStructure_add417688__16 = head.children().size();
            int o_createsDocumentStructure_add417688__18 = body.children().size();
            String o_createsDocumentStructure_add417688__20 = head.getElementsByTag("meta").get(0).attr("name");
            int o_createsDocumentStructure_add417688__23 = body.getElementsByTag("meta").size();
            String o_createsDocumentStructure_add417688__25 = doc.title();
            String o_createsDocumentStructure_add417688__26 = body.text();
            String o_createsDocumentStructure_add417688__27 = body.children().get(0).text();
            org.junit.Assert.fail("createsDocumentStructure_add417688_literalMutationNumber418323 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesBaseTags_literalMutationNumber194692_failAssert0_literalMutationString199779_failAssert0() throws Exception {
        try {
            {
                String h = "<a href=1>#</a><base href='/2/'><a href='3'>#</a><base href='http://bar'><a href=/4>#</a>";
                Document doc = Jsoup.parse(h, "http://foo/");
                doc.baseUri();
                Elements anchors = doc.getElementsByTag("<span>Hello <div>there</div> <span>now</span></span>");
                anchors.size();
                anchors.get(-1).baseUri();
                anchors.get(1).baseUri();
                anchors.get(2).baseUri();
                anchors.get(0).absUrl("href");
                anchors.get(1).absUrl("href");
                anchors.get(2).absUrl("href");
                org.junit.Assert.fail("handlesBaseTags_literalMutationNumber194692 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("handlesBaseTags_literalMutationNumber194692_failAssert0_literalMutationString199779 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesBaseTags_literalMutationNumber194707_failAssert0() throws Exception {
        try {
            String h = "<a href=1>#</a><base href='/2/'><a href='3'>#</a><base href='http://bar'><a href=/4>#</a>";
            Document doc = Jsoup.parse(h, "http://foo/");
            doc.baseUri();
            Elements anchors = doc.getElementsByTag("a");
            anchors.size();
            anchors.get(0).baseUri();
            anchors.get(1).baseUri();
            anchors.get(2).baseUri();
            anchors.get(-1).absUrl("href");
            anchors.get(1).absUrl("href");
            anchors.get(2).absUrl("href");
            org.junit.Assert.fail("handlesBaseTags_literalMutationNumber194707 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesCdataAcrossBuffer_literalMutationNumber375752_failAssert0_add377211_failAssert0() throws Exception {
        try {
            {
                StringBuilder sb = new StringBuilder();
                while ((sb.length()) <= (CharacterReader.maxBufferLen)) {
                    sb.append("A suitable amount of CData.\n");
                } 
                String cdata = sb.toString();
                String h = ("<div><![CDATA[" + cdata) + "]]></div>";
                Document doc = Jsoup.parse(h);
                Element div = doc.selectFirst("div");
                div.textNodes().get(-1);
                CDataNode node = ((CDataNode) (div.textNodes().get(-1)));
                node.text();
                org.junit.Assert.fail("handlesCdataAcrossBuffer_literalMutationNumber375752 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("handlesCdataAcrossBuffer_literalMutationNumber375752_failAssert0_add377211 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cdataNodesAreTextNodes_add239414_literalMutationNumber239550_failAssert0() throws Exception {
        try {
            String h = "<p>One <![CDATA[ Two <& ]]> Three</p>";
            Document doc = Jsoup.parse(h);
            Element p = doc.selectFirst("p");
            List<TextNode> nodes = p.textNodes();
            String o_cdataNodesAreTextNodes_add239414__8 = nodes.get(-1).text();
            TextNode o_cdataNodesAreTextNodes_add239414__10 = nodes.get(1);
            String o_cdataNodesAreTextNodes_add239414__11 = nodes.get(1).text();
            String o_cdataNodesAreTextNodes_add239414__13 = nodes.get(2).text();
            org.junit.Assert.fail("cdataNodesAreTextNodes_add239414_literalMutationNumber239550 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesJavadocFont_add175945_literalMutationNumber176075_literalMutationNumber179355_failAssert0() throws Exception {
        try {
            String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
            Document doc = Jsoup.parse(h);
            Element a = doc.select("a").first();
            String o_handlesJavadocFont_add175945__7 = a.text();
            String o_handlesJavadocFont_add175945__8 = a.child(0).tagName();
            Element o_handlesJavadocFont_add175945__10 = a.child(0).child(-1);
            String o_handlesJavadocFont_add175945__12 = a.child(0).child(0).tagName();
            org.junit.Assert.fail("handlesJavadocFont_add175945_literalMutationNumber176075_literalMutationNumber179355 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesJavadocFont_literalMutationNumber175929_failAssert0_add177253_failAssert0_literalMutationNumber179582_failAssert0() throws Exception {
        try {
            {
                {
                    String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
                    Document doc = Jsoup.parse(h);
                    Element a = doc.select("a").first();
                    a.text();
                    a.child(0).tagName();
                    a.child(-1).child(0).tagName();
                    org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber175929 should have thrown ArrayIndexOutOfBoundsException");
                }
                org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber175929_failAssert0_add177253 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber175929_failAssert0_add177253_failAssert0_literalMutationNumber179582 should have thrown ArrayIndexOutOfBoundsException");
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
    public void testInvalidTableContents_literalMutationString124520_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.outputSettings().prettyPrint(true);
            String rendered = doc.toString();
            int endOfEmail = rendered.indexOf("Comment");
            int guarantee = rendered.indexOf("Why am I here?");
            boolean boolean_124 = endOfEmail > (-1);
            boolean boolean_125 = guarantee > (-1);
            boolean boolean_126 = guarantee > endOfEmail;
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString124520_failAssert0_add126170_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                doc.toString();
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_124 = endOfEmail > (-1);
                boolean boolean_125 = guarantee > (-1);
                boolean boolean_126 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520_failAssert0_add126170 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString124527_failAssert0_literalMutationString125697_failAssert0_literalMutationNumber132939_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "<span>Hello <div>there</div> <span>now</span></span>");
                    doc.outputSettings().prettyPrint(true);
                    String rendered = doc.toString();
                    int endOfEmail = rendered.indexOf("Comment");
                    int guarantee = rendered.indexOf("Why am I here?");
                    boolean boolean_130 = endOfEmail > 2;
                    boolean boolean_131 = guarantee > (-1);
                    boolean boolean_132 = guarantee > endOfEmail;
                    org.junit.Assert.fail("testInvalidTableContents_literalMutationString124527 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString124527_failAssert0_literalMutationString125697 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString124527_failAssert0_literalMutationString125697_failAssert0_literalMutationNumber132939 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString124527_failAssert0_literalMutationString125697_failAssert0null135733_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "<span>Hello <div>there</div> <span>now</span></span>");
                    doc.outputSettings().prettyPrint(true);
                    String rendered = doc.toString();
                    int endOfEmail = rendered.indexOf("Comment");
                    int guarantee = rendered.indexOf(null);
                    boolean boolean_130 = endOfEmail > (-1);
                    boolean boolean_131 = guarantee > (-1);
                    boolean boolean_132 = guarantee > endOfEmail;
                    org.junit.Assert.fail("testInvalidTableContents_literalMutationString124527 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString124527_failAssert0_literalMutationString125697 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString124527_failAssert0_literalMutationString125697_failAssert0null135733 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContentsnull124555_literalMutationString124959_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            doc.outputSettings().prettyPrint(true);
            String rendered = doc.toString();
            int endOfEmail = rendered.indexOf("Comment");
            int guarantee = rendered.indexOf("Why am I here?");
            boolean boolean_97 = endOfEmail > (-1);
            boolean boolean_98 = guarantee > (-1);
            boolean boolean_99 = guarantee > endOfEmail;
            org.junit.Assert.fail("testInvalidTableContentsnull124555_literalMutationString124959 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString124520_failAssert0_literalMutationBoolean125673_failAssert0_literalMutationString132683_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.outputSettings().prettyPrint(false);
                    String rendered = doc.toString();
                    int endOfEmail = rendered.indexOf("");
                    int guarantee = rendered.indexOf("Why am I here?");
                    boolean boolean_124 = endOfEmail > (-1);
                    boolean boolean_125 = guarantee > (-1);
                    boolean boolean_126 = guarantee > endOfEmail;
                    org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520_failAssert0_literalMutationBoolean125673 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520_failAssert0_literalMutationBoolean125673_failAssert0_literalMutationString132683 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString124525_failAssert0_add126213_failAssert0_literalMutationString132084_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.outputSettings().prettyPrint(true);
                    String rendered = doc.toString();
                    int endOfEmail = rendered.indexOf("Comment");
                    int guarantee = rendered.indexOf("Why am I here?");
                    boolean boolean_154 = endOfEmail > (-1);
                    boolean boolean_155 = guarantee > (-1);
                    boolean boolean_156 = guarantee > endOfEmail;
                    org.junit.Assert.fail("testInvalidTableContents_literalMutationString124525 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString124525_failAssert0_add126213 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString124525_failAssert0_add126213_failAssert0_literalMutationString132084 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString124520_failAssert0_literalMutationBoolean125673_failAssert0_add134624_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.outputSettings().prettyPrint(false);
                    doc.outputSettings().prettyPrint(false);
                    String rendered = doc.toString();
                    int endOfEmail = rendered.indexOf("Comment");
                    int guarantee = rendered.indexOf("Why am I here?");
                    boolean boolean_124 = endOfEmail > (-1);
                    boolean boolean_125 = guarantee > (-1);
                    boolean boolean_126 = guarantee > endOfEmail;
                    org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520_failAssert0_literalMutationBoolean125673 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520_failAssert0_literalMutationBoolean125673_failAssert0_add134624 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString124520_failAssert0_add126166_failAssert0() throws IOException {
        try {
            {
                ParseTest.getFile("");
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_124 = endOfEmail > (-1);
                boolean boolean_125 = guarantee > (-1);
                boolean boolean_126 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520_failAssert0_add126166 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContentsnull124555_literalMutationString124959_failAssert0_literalMutationString131306_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_97 = endOfEmail > (-1);
                boolean boolean_98 = guarantee > (-1);
                boolean boolean_99 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContentsnull124555_literalMutationString124959 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContentsnull124555_literalMutationString124959_failAssert0_literalMutationString131306 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString124520_failAssert0_literalMutationNumber125686_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_124 = endOfEmail > 2;
                boolean boolean_125 = guarantee > (-1);
                boolean boolean_126 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520_failAssert0_literalMutationNumber125686 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString124520_failAssert0null126404_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf(null);
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_124 = endOfEmail > (-1);
                boolean boolean_125 = guarantee > (-1);
                boolean boolean_126 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520_failAssert0null126404 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString124520_failAssert0null126405_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf(null);
                boolean boolean_124 = endOfEmail > (-1);
                boolean boolean_125 = guarantee > (-1);
                boolean boolean_126 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520_failAssert0null126405 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString124520_failAssert0_literalMutationBoolean125673_failAssert0null135696_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8");
                    doc.outputSettings().prettyPrint(false);
                    String rendered = doc.toString();
                    int endOfEmail = rendered.indexOf(null);
                    int guarantee = rendered.indexOf("Why am I here?");
                    boolean boolean_124 = endOfEmail > (-1);
                    boolean boolean_125 = guarantee > (-1);
                    boolean boolean_126 = guarantee > endOfEmail;
                    org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520_failAssert0_literalMutationBoolean125673 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520_failAssert0_literalMutationBoolean125673_failAssert0null135696 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString124527_failAssert0_literalMutationString125697_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "<span>Hello <div>there</div> <span>now</span></span>");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_130 = endOfEmail > (-1);
                boolean boolean_131 = guarantee > (-1);
                boolean boolean_132 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString124527 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString124527_failAssert0_literalMutationString125697 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_add124550_literalMutationString124918_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF-8");
            doc.outputSettings().prettyPrint(true);
            String rendered = doc.toString();
            int o_testInvalidTableContents_add124550__9 = rendered.indexOf("Comment");
            int endOfEmail = rendered.indexOf("Comment");
            int guarantee = rendered.indexOf("Why am I here?");
            boolean boolean_82 = endOfEmail > (-1);
            boolean boolean_83 = guarantee > (-1);
            boolean boolean_84 = guarantee > endOfEmail;
            org.junit.Assert.fail("testInvalidTableContents_add124550_literalMutationString124918 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString124527_failAssert0_literalMutationString125697_failAssert0_add134685_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "<span>Hello <div>there</div> <span>now</span></span>");
                    doc.outputSettings().prettyPrint(true);
                    String rendered = doc.toString();
                    int endOfEmail = rendered.indexOf("Comment");
                    rendered.indexOf("Why am I here?");
                    int guarantee = rendered.indexOf("Why am I here?");
                    boolean boolean_130 = endOfEmail > (-1);
                    boolean boolean_131 = guarantee > (-1);
                    boolean boolean_132 = guarantee > endOfEmail;
                    org.junit.Assert.fail("testInvalidTableContents_literalMutationString124527 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString124527_failAssert0_literalMutationString125697 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString124527_failAssert0_literalMutationString125697_failAssert0_add134685 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString124520_failAssert0_literalMutationBoolean125673_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(false);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_124 = endOfEmail > (-1);
                boolean boolean_125 = guarantee > (-1);
                boolean boolean_126 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520_failAssert0_literalMutationBoolean125673 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContentsnull124555_literalMutationString124959_failAssert0_add134279_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                int endOfEmail = rendered.indexOf("Comment");
                rendered.indexOf("Why am I here?");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_97 = endOfEmail > (-1);
                boolean boolean_98 = guarantee > (-1);
                boolean boolean_99 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContentsnull124555_literalMutationString124959 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContentsnull124555_literalMutationString124959_failAssert0_add134279 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInvalidTableContents_literalMutationString124520_failAssert0_add126171_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF-8");
                doc.outputSettings().prettyPrint(true);
                String rendered = doc.toString();
                rendered.indexOf("Comment");
                int endOfEmail = rendered.indexOf("Comment");
                int guarantee = rendered.indexOf("Why am I here?");
                boolean boolean_124 = endOfEmail > (-1);
                boolean boolean_125 = guarantee > (-1);
                boolean boolean_126 = guarantee > endOfEmail;
                org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInvalidTableContents_literalMutationString124520_failAssert0_add126171 should have thrown FileNotFoundException");
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
    public void testTemplateInsideTable_literalMutationString12_failAssert0null791_failAssert0_literalMutationString2051_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTrF-8");
                    doc.outputSettings().prettyPrint(true);
                    Elements templates = doc.body().getElementsByTag(null);
                    for (Element template : templates) {
                        boolean boolean_36 = (template.childNodes().size()) > 1;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString12 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString12_failAssert0null791 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString12_failAssert0null791_failAssert0_literalMutationString2051 should have thrown FileNotFoundException");
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
    public void testTemplateInsideTable_literalMutationString8_failAssert0_literalMutationString256_failAssert0_literalMutationString2354_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "");
                    doc.outputSettings().prettyPrint(true);
                    Elements templates = doc.body().getElementsByTag("template");
                    for (Element template : templates) {
                        boolean boolean_23 = (template.childNodes().size()) > 1;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString8 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString8_failAssert0_literalMutationString256 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString8_failAssert0_literalMutationString256_failAssert0_literalMutationString2354 should have thrown FileNotFoundException");
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
    public void testTemplateInsideTable_literalMutationString8_failAssert0_literalMutationString256_failAssert0_add5280_failAssert0() throws IOException {
        try {
            {
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
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString8_failAssert0_literalMutationString256_failAssert0_add5280 should have thrown FileNotFoundException");
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
    public void testTemplateInsideTable_literalMutationString8_failAssert0_literalMutationString256_failAssert0null6478_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "<span>Hello <div>there</div> <span>now</span></span>");
                    doc.outputSettings().prettyPrint(true);
                    Elements templates = doc.body().getElementsByTag(null);
                    for (Element template : templates) {
                        boolean boolean_23 = (template.childNodes().size()) > 1;
                    }
                    org.junit.Assert.fail("testTemplateInsideTable_literalMutationString8 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("testTemplateInsideTable_literalMutationString8_failAssert0_literalMutationString256 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTemplateInsideTable_literalMutationString8_failAssert0_literalMutationString256_failAssert0null6478 should have thrown FileNotFoundException");
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

