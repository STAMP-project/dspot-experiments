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
    public void createsDocumentStructure_literalMutationNumber50767_failAssert0() throws Exception {
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
            org.junit.Assert.fail("createsDocumentStructure_literalMutationNumber50767 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void createsDocumentStructure_literalMutationNumber50784_failAssert0() throws Exception {
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
            org.junit.Assert.fail("createsDocumentStructure_literalMutationNumber50784 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesJavadocFont_literalMutationNumber14053_failAssert0() throws Exception {
        try {
            String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
            Document doc = Jsoup.parse(h);
            Element a = doc.select("a").first();
            a.text();
            a.child(0).tagName();
            a.child(-1).child(0).tagName();
            org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber14053 should have thrown ArrayIndexOutOfBoundsException");
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
}

