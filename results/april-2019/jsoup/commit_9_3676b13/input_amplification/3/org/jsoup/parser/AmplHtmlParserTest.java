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
    public void handlesJavadocFont_literalMutationNumber55313_failAssert0_add56658_failAssert0() throws Exception {
        try {
            {
                String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
                Document doc = Jsoup.parse(h);
                Element a = doc.select("a").first();
                a.text();
                a.child(0).tagName();
                a.child(0).tagName();
                a.child(0).child(-1).tagName();
                org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber55313 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber55313_failAssert0_add56658 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesJavadocFont_literalMutationNumber55304_literalMutationNumber55677_failAssert0() throws Exception {
        try {
            String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
            Document doc = Jsoup.parse(h);
            Element a = doc.select("a").first();
            String o_handlesJavadocFont_literalMutationNumber55304__7 = a.text();
            String o_handlesJavadocFont_literalMutationNumber55304__8 = a.child(0).tagName();
            String o_handlesJavadocFont_literalMutationNumber55304__11 = a.child(-1).child(0).tagName();
            org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber55304_literalMutationNumber55677 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesJavadocFont_add55322_literalMutationNumber55963_failAssert0() throws Exception {
        try {
            String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
            Document doc = Jsoup.parse(h);
            Element a = doc.select("a").first();
            String o_handlesJavadocFont_add55322__7 = a.text();
            Element o_handlesJavadocFont_add55322__8 = a.child(0);
            String o_handlesJavadocFont_add55322__9 = a.child(0).tagName();
            String o_handlesJavadocFont_add55322__11 = a.child(0).child(-1).tagName();
            org.junit.Assert.fail("handlesJavadocFont_add55322_literalMutationNumber55963 should have thrown ArrayIndexOutOfBoundsException");
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

