package org.jsoup.parser;


import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class AmplHtmlParserTest {
    @Test(timeout = 10000)
    public void createsDocumentStructure_literalMutationString51048_literalMutationNumber51665_failAssert0() throws Exception {
        try {
            String html = "<meta name=keywords /><link rel=stylesheet /><title>jsoup</title><p>Hello world</p>";
            Document doc = Jsoup.parse(html);
            Element head = doc.head();
            Element body = doc.body();
            int o_createsDocumentStructure_literalMutationString51048__8 = doc.children().size();
            int o_createsDocumentStructure_literalMutationString51048__10 = doc.child(0).children().size();
            int o_createsDocumentStructure_literalMutationString51048__13 = head.children().size();
            int o_createsDocumentStructure_literalMutationString51048__15 = body.children().size();
            String o_createsDocumentStructure_literalMutationString51048__17 = head.getElementsByTag("meta").get(-1).attr("name");
            int o_createsDocumentStructure_literalMutationString51048__20 = body.getElementsByTag("}CE[").size();
            String o_createsDocumentStructure_literalMutationString51048__22 = doc.title();
            String o_createsDocumentStructure_literalMutationString51048__23 = body.text();
            String o_createsDocumentStructure_literalMutationString51048__24 = body.children().get(0).text();
            org.junit.Assert.fail("createsDocumentStructure_literalMutationString51048_literalMutationNumber51665 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void createsDocumentStructure_literalMutationNumber51024_failAssert0null55785_failAssert0() throws Exception {
        try {
            {
                String html = "<meta name=keywords /><link rel=stylesheet /><title>jsoup</title><p>Hello world</p>";
                Document doc = Jsoup.parse(html);
                Element head = doc.head();
                Element body = doc.body();
                doc.children().size();
                doc.child(-1).children().size();
                head.children().size();
                body.children().size();
                head.getElementsByTag(null).get(0).attr("name");
                body.getElementsByTag("meta").size();
                doc.title();
                body.text();
                body.children().get(0).text();
                org.junit.Assert.fail("createsDocumentStructure_literalMutationNumber51024 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("createsDocumentStructure_literalMutationNumber51024_failAssert0null55785 should have thrown ArrayIndexOutOfBoundsException");
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
}

