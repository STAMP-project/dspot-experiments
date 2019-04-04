package org.jsoup.nodes;


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
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull147473() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Document) (doc)).getAllElements().isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull147473__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull147473__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull147473__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull147473__8);
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Document) (doc)).getAllElements().isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull147473__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull147472() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Document) (doc)).getAllElements().isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull147472__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull147472__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull147472__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull147472__8);
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Document) (doc)).getAllElements().isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull147472__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull147474_failAssert267null149185() throws Exception {
        try {
            final Document doc = createXmlDocument("dontTouch", null, true);
            Assert.assertFalse(((Document) (doc)).isBlock());
            Assert.assertFalse(((Document) (doc)).getAllElements().isEmpty());
            Assert.assertTrue(((Document) (doc)).hasText());
            Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
            Assert.assertFalse(((Document) (doc)).hasParent());
            final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
            Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
            doc.toString();
            XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
            Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
            Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
            Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
            selectedNode.attr(null);
            selectedNode.attr("version");
            org.junit.Assert.fail("testMetaCharsetUpdateXmlDisabledNoChangesnull147474 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268null149138() throws Exception {
        try {
            final Document doc = createXmlDocument("dontTouch", null, true);
            Assert.assertFalse(((Document) (doc)).isBlock());
            Assert.assertFalse(((Document) (doc)).getAllElements().isEmpty());
            Assert.assertTrue(((Document) (doc)).hasText());
            Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
            Assert.assertFalse(((Document) (doc)).hasParent());
            final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
            Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
            doc.toString();
            XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
            Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
            Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
            Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
            String o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268null149138__9 = selectedNode.attr("encoding");
            Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268null149138__9);
            selectedNode.attr(null);
            org.junit.Assert.fail("testMetaCharsetUpdateXmlDisabledNoChangesnull147475 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull147474_failAssert267null149179() throws Exception {
        try {
            final Document doc = createXmlDocument(null, "dontTouch", true);
            Assert.assertFalse(((Document) (doc)).isBlock());
            Assert.assertFalse(((Document) (doc)).getAllElements().isEmpty());
            Assert.assertTrue(((Document) (doc)).hasText());
            Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
            Assert.assertFalse(((Document) (doc)).hasParent());
            final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
            Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
            doc.toString();
            XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
            Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
            Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
            Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
            selectedNode.attr(null);
            selectedNode.attr("version");
            org.junit.Assert.fail("testMetaCharsetUpdateXmlDisabledNoChangesnull147474 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268_add148753null165479() throws Exception {
        try {
            Document o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268_add148753__3 = createXmlDocument("dontTouch", "dontTouch", true);
            Assert.assertFalse(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268_add148753__3)).getAllElements().isEmpty());
            Assert.assertTrue(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268_add148753__3)).hasText());
            Assert.assertFalse(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268_add148753__3)).isBlock());
            Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268_add148753__3)).toString());
            Assert.assertFalse(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268_add148753__3)).hasParent());
            final Document doc = createXmlDocument(null, "dontTouch", true);
            Assert.assertFalse(((Document) (doc)).getAllElements().isEmpty());
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
            String o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268_add148753__10 = selectedNode.attr("encoding");
            Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268_add148753__10);
            selectedNode.attr(null);
            org.junit.Assert.fail("testMetaCharsetUpdateXmlDisabledNoChangesnull147475 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull147474_failAssert267_add148825null163891() throws Exception {
        try {
            Document o_testMetaCharsetUpdateXmlDisabledNoChangesnull147474_failAssert267_add148825__3 = createXmlDocument("dontTouch", "dontTouch", true);
            Assert.assertFalse(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147474_failAssert267_add148825__3)).getAllElements().isEmpty());
            Assert.assertTrue(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147474_failAssert267_add148825__3)).hasText());
            Assert.assertFalse(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147474_failAssert267_add148825__3)).isBlock());
            Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147474_failAssert267_add148825__3)).toString());
            Assert.assertFalse(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147474_failAssert267_add148825__3)).hasParent());
            final Document doc = createXmlDocument("dontTouch", null, true);
            Assert.assertFalse(((Document) (doc)).getAllElements().isEmpty());
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
            selectedNode.attr(null);
            selectedNode.attr("version");
            org.junit.Assert.fail("testMetaCharsetUpdateXmlDisabledNoChangesnull147474 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268_add148753null165484() throws Exception {
        try {
            Document o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268_add148753__3 = createXmlDocument("dontTouch", "dontTouch", true);
            Assert.assertFalse(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268_add148753__3)).getAllElements().isEmpty());
            Assert.assertTrue(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268_add148753__3)).hasText());
            Assert.assertFalse(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268_add148753__3)).isBlock());
            Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268_add148753__3)).toString());
            Assert.assertFalse(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268_add148753__3)).hasParent());
            final Document doc = createXmlDocument("dontTouch", null, true);
            Assert.assertFalse(((Document) (doc)).getAllElements().isEmpty());
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
            String o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268_add148753__10 = selectedNode.attr("encoding");
            Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268_add148753__10);
            selectedNode.attr(null);
            org.junit.Assert.fail("testMetaCharsetUpdateXmlDisabledNoChangesnull147475 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull147474_failAssert267_add148825null163861() throws Exception {
        try {
            Document o_testMetaCharsetUpdateXmlDisabledNoChangesnull147474_failAssert267_add148825__3 = createXmlDocument("dontTouch", "dontTouch", true);
            Assert.assertFalse(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147474_failAssert267_add148825__3)).getAllElements().isEmpty());
            Assert.assertTrue(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147474_failAssert267_add148825__3)).hasText());
            Assert.assertFalse(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147474_failAssert267_add148825__3)).isBlock());
            Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147474_failAssert267_add148825__3)).toString());
            Assert.assertFalse(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChangesnull147474_failAssert267_add148825__3)).hasParent());
            final Document doc = createXmlDocument(null, "dontTouch", true);
            Assert.assertFalse(((Document) (doc)).getAllElements().isEmpty());
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
            selectedNode.attr(null);
            selectedNode.attr("version");
            org.junit.Assert.fail("testMetaCharsetUpdateXmlDisabledNoChangesnull147474 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268null149133() throws Exception {
        try {
            final Document doc = createXmlDocument(null, "dontTouch", true);
            Assert.assertFalse(((Document) (doc)).isBlock());
            Assert.assertFalse(((Document) (doc)).getAllElements().isEmpty());
            Assert.assertTrue(((Document) (doc)).hasText());
            Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
            Assert.assertFalse(((Document) (doc)).hasParent());
            final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
            Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
            doc.toString();
            XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
            Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
            Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
            Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
            String o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268null149133__9 = selectedNode.attr("encoding");
            Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull147475_failAssert268null149133__9);
            selectedNode.attr(null);
            org.junit.Assert.fail("testMetaCharsetUpdateXmlDisabledNoChangesnull147475 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
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

