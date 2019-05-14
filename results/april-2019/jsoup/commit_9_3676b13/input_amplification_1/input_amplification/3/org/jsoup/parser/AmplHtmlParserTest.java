package org.jsoup.parser;


import java.util.Collection;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class AmplHtmlParserTest {
    @Test(timeout = 10000)
    public void handlesDataOnlyTags_literalMutationString153222() throws Exception {
        String t = "<style>font-family: bold</style>";
        List<Element> tels = Jsoup.parse(t).getElementsByTag("style");
        String o_handlesDataOnlyTags_literalMutationString153222__5 = tels.get(0).data();
        Assert.assertEquals("font-family: bold", o_handlesDataOnlyTags_literalMutationString153222__5);
        String o_handlesDataOnlyTags_literalMutationString153222__7 = tels.get(0).text();
        Assert.assertEquals("", o_handlesDataOnlyTags_literalMutationString153222__7);
        String s = "<p>Hello</p>Tscript>obj.insert(\'<a rel=\"none\" />\');\ni++;</script><p>There</p>";
        Document doc = Jsoup.parse(s);
        String o_handlesDataOnlyTags_literalMutationString153222__12 = doc.text();
        Assert.assertEquals("HelloTscript>obj.insert(\'\'); i++; There", o_handlesDataOnlyTags_literalMutationString153222__12);
        String o_handlesDataOnlyTags_literalMutationString153222__13 = doc.data();
        Assert.assertEquals("", o_handlesDataOnlyTags_literalMutationString153222__13);
        Assert.assertEquals("font-family: bold", o_handlesDataOnlyTags_literalMutationString153222__5);
        Assert.assertEquals("", o_handlesDataOnlyTags_literalMutationString153222__7);
        Assert.assertEquals("HelloTscript>obj.insert(\'\'); i++; There", o_handlesDataOnlyTags_literalMutationString153222__12);
    }

    @Test(timeout = 10000)
    public void handlesDataOnlyTags_literalMutationString153222_add154638() throws Exception {
        String t = "<style>font-family: bold</style>";
        List<Element> tels = Jsoup.parse(t).getElementsByTag("style");
        String o_handlesDataOnlyTags_literalMutationString153222__5 = tels.get(0).data();
        Assert.assertEquals("font-family: bold", o_handlesDataOnlyTags_literalMutationString153222__5);
        String o_handlesDataOnlyTags_literalMutationString153222__7 = tels.get(0).text();
        Assert.assertEquals("", o_handlesDataOnlyTags_literalMutationString153222__7);
        String s = "<p>Hello</p>Tscript>obj.insert(\'<a rel=\"none\" />\');\ni++;</script><p>There</p>";
        Document o_handlesDataOnlyTags_literalMutationString153222_add154638__14 = Jsoup.parse(s);
        Assert.assertFalse(((Collection) (((Document) (o_handlesDataOnlyTags_literalMutationString153222_add154638__14)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (o_handlesDataOnlyTags_literalMutationString153222_add154638__14)).isBlock());
        Assert.assertTrue(((Document) (o_handlesDataOnlyTags_literalMutationString153222_add154638__14)).hasText());
        Assert.assertFalse(((Document) (o_handlesDataOnlyTags_literalMutationString153222_add154638__14)).hasParent());
        Document doc = Jsoup.parse(s);
        String o_handlesDataOnlyTags_literalMutationString153222__12 = doc.text();
        Assert.assertEquals("HelloTscript>obj.insert(\'\'); i++; There", o_handlesDataOnlyTags_literalMutationString153222__12);
        String o_handlesDataOnlyTags_literalMutationString153222__13 = doc.data();
        Assert.assertEquals("", o_handlesDataOnlyTags_literalMutationString153222__13);
        Assert.assertEquals("font-family: bold", o_handlesDataOnlyTags_literalMutationString153222__5);
        Assert.assertEquals("", o_handlesDataOnlyTags_literalMutationString153222__7);
        Assert.assertFalse(((Collection) (((Document) (o_handlesDataOnlyTags_literalMutationString153222_add154638__14)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (o_handlesDataOnlyTags_literalMutationString153222_add154638__14)).isBlock());
        Assert.assertTrue(((Document) (o_handlesDataOnlyTags_literalMutationString153222_add154638__14)).hasText());
        Assert.assertFalse(((Document) (o_handlesDataOnlyTags_literalMutationString153222_add154638__14)).hasParent());
        Assert.assertEquals("HelloTscript>obj.insert(\'\'); i++; There", o_handlesDataOnlyTags_literalMutationString153222__12);
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

