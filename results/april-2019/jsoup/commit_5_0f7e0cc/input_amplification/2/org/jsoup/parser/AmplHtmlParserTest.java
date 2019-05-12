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
    public void handlesJavadocFont_literalMutationNumber30398_failAssert0_add31677_failAssert0() throws Exception {
        try {
            {
                String h = "<TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">    <A HREF=\"deprecated-list.html\"><FONT CLASS=\"NavBarFont1\"><B>Deprecated</B></FONT></A>&nbsp;</TD>";
                Document doc = Jsoup.parse(h);
                doc.select("a").first();
                Element a = doc.select("a").first();
                a.text();
                a.child(-1).tagName();
                a.child(0).child(0).tagName();
                org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber30398 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("handlesJavadocFont_literalMutationNumber30398_failAssert0_add31677 should have thrown ArrayIndexOutOfBoundsException");
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

