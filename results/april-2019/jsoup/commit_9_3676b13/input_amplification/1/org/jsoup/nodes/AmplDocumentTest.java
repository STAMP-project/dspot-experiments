package org.jsoup.nodes;


import java.util.Collection;
import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static org.jsoup.nodes.Document.OutputSettings.Syntax.xml;


public class AmplDocumentTest {
    private static final String charsetUtf8 = "UTF-8";

    private static final String charsetIso8859 = "ISO-8859-1";

    @Ignore
    @Test
    public void testOverflowClone() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            builder.insert(0, "<i>");
            builder.append("</i>");
        }
        Document doc = Jsoup.parse(builder.toString());
        doc.clone();
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull1941() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull1941__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull1941__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull1941__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull1941__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull1941__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull1942() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull1942__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull1942__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull1942__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull1942__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull1942__7);
    }

    private Document createHtmlDocument(String charset) {
        final Document doc = Document.createShell("");
        doc.head().appendElement("meta").attr("charset", charset);
        doc.head().appendElement("meta").attr("name", "charset").attr("content", charset);
        return doc;
    }

    private Document createXmlDocument(String version, String charset, boolean addDecl) {
        final Document doc = new Document("");
        doc.appendElement("root").text("node");
        doc.outputSettings().syntax(xml);
        if (addDecl) {
            XmlDeclaration decl = new XmlDeclaration("xml", false);
            decl.attr("version", version);
            decl.attr("encoding", charset);
            doc.prependChild(decl);
        }
        return doc;
    }
}

