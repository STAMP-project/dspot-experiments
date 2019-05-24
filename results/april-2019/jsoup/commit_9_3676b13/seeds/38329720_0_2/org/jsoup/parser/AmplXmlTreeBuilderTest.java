package org.jsoup.parser;


import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.XmlDeclaration;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class AmplXmlTreeBuilderTest {
    @Ignore
    @Test
    public void testSupplyParserToConnection() throws IOException {
        String xmlUrl = "http://direct.infohound.net/tools/jsoup-xml-test.xml";
        Document xmlDoc = Jsoup.connect(xmlUrl).parser(Parser.xmlParser()).get();
        Document htmlDoc = Jsoup.connect(xmlUrl).parser(Parser.htmlParser()).get();
        Document autoXmlDoc = Jsoup.connect(xmlUrl).get();
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", TextUtil.stripNewlines(xmlDoc.html()));
        Assert.assertFalse(htmlDoc.equals(xmlDoc));
        Assert.assertEquals(xmlDoc, autoXmlDoc);
        Assert.assertEquals(1, htmlDoc.select("head").size());
        Assert.assertEquals(0, xmlDoc.select("head").size());
        Assert.assertEquals(0, autoXmlDoc.select("head").size());
    }

    @Test(timeout = 10000)
    public void testParseDeclarationAttributes_literalMutationString1003() throws Exception {
        String xml = "<?xml version=\'1\' encoding%\'UTF-8\' something=\'else\'?><val>One</val>";
        Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
        XmlDeclaration decl = ((XmlDeclaration) (doc.childNode(0)));
        String o_testParseDeclarationAttributes_literalMutationString1003__7 = decl.attr("version");
        Assert.assertEquals("1", o_testParseDeclarationAttributes_literalMutationString1003__7);
        String o_testParseDeclarationAttributes_literalMutationString1003__8 = decl.attr("encoding");
        Assert.assertEquals("", o_testParseDeclarationAttributes_literalMutationString1003__8);
        String o_testParseDeclarationAttributes_literalMutationString1003__9 = decl.attr("something");
        Assert.assertEquals("else", o_testParseDeclarationAttributes_literalMutationString1003__9);
        String o_testParseDeclarationAttributes_literalMutationString1003__10 = decl.getWholeDeclaration();
        Assert.assertEquals("version=\"1\" encoding%\'UTF-8\'=\"\" something=\"else\"", o_testParseDeclarationAttributes_literalMutationString1003__10);
        String o_testParseDeclarationAttributes_literalMutationString1003__11 = decl.outerHtml();
        Assert.assertEquals("<?xml version=\"1\" encoding%\'UTF-8\'=\"\" something=\"else\"?>", o_testParseDeclarationAttributes_literalMutationString1003__11);
        Assert.assertEquals("1", o_testParseDeclarationAttributes_literalMutationString1003__7);
        Assert.assertEquals("", o_testParseDeclarationAttributes_literalMutationString1003__8);
        Assert.assertEquals("else", o_testParseDeclarationAttributes_literalMutationString1003__9);
        Assert.assertEquals("version=\"1\" encoding%\'UTF-8\'=\"\" something=\"else\"", o_testParseDeclarationAttributes_literalMutationString1003__10);
    }
}

