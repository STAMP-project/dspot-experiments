package org.jsoup.parser;


import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class AmplHtmlParserTest {
    @Test(timeout = 10000)
    public void createsDocumentStructure_literalMutationNumber236139_literalMutationNumber237493_failAssert0() throws Exception {
        try {
            String html = "<meta name=keywords /><link rel=stylesheet /><title>jsoup</title><p>Hello world</p>";
            Document doc = Jsoup.parse(html);
            Element head = doc.head();
            Element body = doc.body();
            int o_createsDocumentStructure_literalMutationNumber236139__8 = doc.children().size();
            int o_createsDocumentStructure_literalMutationNumber236139__10 = doc.child(-1).children().size();
            int o_createsDocumentStructure_literalMutationNumber236139__13 = head.children().size();
            int o_createsDocumentStructure_literalMutationNumber236139__15 = body.children().size();
            String o_createsDocumentStructure_literalMutationNumber236139__17 = head.getElementsByTag("meta").get(0).attr("name");
            int o_createsDocumentStructure_literalMutationNumber236139__21 = body.getElementsByTag("meta").size();
            String o_createsDocumentStructure_literalMutationNumber236139__23 = doc.title();
            String o_createsDocumentStructure_literalMutationNumber236139__24 = body.text();
            String o_createsDocumentStructure_literalMutationNumber236139__25 = body.children().get(0).text();
            org.junit.Assert.fail("createsDocumentStructure_literalMutationNumber236139_literalMutationNumber237493 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesDataOnlyTags_literalMutationNumber225111_failAssert0_add226870_failAssert0() throws Exception {
        try {
            {
                String t = "<style>font-family: bold</style>";
                List<Element> tels = Jsoup.parse(t).getElementsByTag("style");
                tels.get(0).data();
                tels.get(-1).text();
                String s = "<p>Hello</p><script>obj.insert(\'<a rel=\"none\" />\');\ni++;</script><p>There</p>";
                Document doc = Jsoup.parse(s);
                doc.text();
                doc.data();
                doc.data();
                org.junit.Assert.fail("handlesDataOnlyTags_literalMutationNumber225111 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("handlesDataOnlyTags_literalMutationNumber225111_failAssert0_add226870 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesJavadocFont_literalMutationNumber97949_failAssert0() throws Exception {
        try {
            String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
            Document doc = Jsoup.parse(h);
            Element a = doc.select("a").first();
            a.text();
            a.child(-1).tagName();
            a.child(0).child(0).tagName();
            org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber97949 should have thrown ArrayIndexOutOfBoundsException");
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
    public void selfClosingOnNonvoidIsError_literalMutationNumber75635_remove77611_literalMutationNumber78133_failAssert0() throws Exception {
        try {
            String html = "<p>test</p><div /><div>Two</div>";
            Parser parser = Parser.htmlParser().setTrackErrors(5);
            Document o_selfClosingOnNonvoidIsError_literalMutationNumber75635__5 = parser.parseInput(html, "");
            int o_selfClosingOnNonvoidIsError_literalMutationNumber75635__6 = parser.getErrors().size();
            parser.getErrors().get(-1).toString();
            boolean o_selfClosingOnNonvoidIsError_literalMutationNumber75635__12 = Jsoup.isValid(html, Whitelist.relaxed());
            String clean = Jsoup.clean(html, Whitelist.relaxed());
            org.junit.Assert.fail("selfClosingOnNonvoidIsError_literalMutationNumber75635_remove77611_literalMutationNumber78133 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void selfClosingOnNonvoidIsError_literalMutationNumber75637_remove77609_literalMutationNumber78113_failAssert0() throws Exception {
        try {
            String html = "<p>test</p><div /><div>Two</div>";
            Parser parser = Parser.htmlParser().setTrackErrors(5);
            Document o_selfClosingOnNonvoidIsError_literalMutationNumber75637__5 = parser.parseInput(html, "");
            int o_selfClosingOnNonvoidIsError_literalMutationNumber75637__6 = parser.getErrors().size();
            parser.getErrors().get(-1).toString();
            boolean o_selfClosingOnNonvoidIsError_literalMutationNumber75637__12 = Jsoup.isValid(html, Whitelist.relaxed());
            String clean = Jsoup.clean(html, Whitelist.relaxed());
            org.junit.Assert.fail("selfClosingOnNonvoidIsError_literalMutationNumber75637_remove77609_literalMutationNumber78113 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }
}

