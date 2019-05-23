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
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88458null93435() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88458__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88458__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88458__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88458__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88458__8 = selectedNode.attr("veQrsion");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88458__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88458__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88458__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420null93479_literalMutationString97219() throws Exception {
        final Document doc = createXmlDocument(null, "do8ntTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<roqot>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<roqot>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"do8ntTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7 = selectedNode.attr("encoding");
        Assert.assertEquals("do8ntTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<roqot>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4);
        Assert.assertEquals("version=\"\" encoding=\"do8ntTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("do8ntTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454null93419null100773() throws Exception {
        final Document doc = createXmlDocument(null, null, true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__7 = selectedNode.attr("enco=ing");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__4);
        Assert.assertEquals("version=\"\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420null93479() throws Exception {
        final Document doc = createXmlDocument(null, "do8ntTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"do8ntTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7 = selectedNode.attr("encoding");
        Assert.assertEquals("do8ntTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4);
        Assert.assertEquals("version=\"\" encoding=\"do8ntTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("do8ntTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88416null93428() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88416__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88416__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88416__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88416__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88416__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88416__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88416__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88416__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull88468_literalMutationString90003() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__7 = selectedNode.attr("o26<d;SK");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414null93313() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88417null93317() throws Exception {
        final Document doc = createXmlDocument(null, "doatTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"doatTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88417__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"doatTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88417__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"doatTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"doatTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88417__7 = selectedNode.attr("encoding");
        Assert.assertEquals("doatTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88417__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88417__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88417__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"doatTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"doatTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88417__4);
        Assert.assertEquals("version=\"\" encoding=\"doatTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"doatTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("doatTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88417__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88410_add92493null100854() throws Exception {
        final Document doc = createXmlDocument("", null, true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88410__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88410__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88410__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88410__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88410__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88410__8);
        selectedNode.toString();
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88410__4);
        Assert.assertEquals("version=\"\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88410__7);
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88410__8);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454null93419() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__7 = selectedNode.attr("enco=ing");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88421null93298() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88421__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88421__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88421__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88421__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88421__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88421__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88421__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88421__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420null93479_add100390() throws Exception {
        final Document doc = createXmlDocument(null, "do8ntTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"do8ntTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        selectedNode.hasParent();
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7 = selectedNode.attr("encoding");
        Assert.assertEquals("do8ntTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4);
        Assert.assertEquals("version=\"\" encoding=\"do8ntTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("do8ntTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414null93313null100761() throws Exception {
        final Document doc = createXmlDocument(null, null, true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4);
        Assert.assertEquals("version=\"\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420null93479_literalMutationString97230() throws Exception {
        final Document doc = createXmlDocument(null, "do8ntTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</rot>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</rot>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"do8ntTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7 = selectedNode.attr("encoding");
        Assert.assertEquals("do8ntTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</rot>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4);
        Assert.assertEquals("version=\"\" encoding=\"do8ntTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("do8ntTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88417_literalMutationNumber89113null100688() throws Exception {
        final Document doc = createXmlDocument(null, "doatTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"doatTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88417__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"doatTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88417__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"doatTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"doatTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88417__7 = selectedNode.attr("encoding");
        Assert.assertEquals("doatTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88417__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88417__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88417__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"doatTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"doatTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88417__4);
        Assert.assertEquals("version=\"\" encoding=\"doatTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"doatTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("doatTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88417__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull88468() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__8);
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88461null93503() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88461__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88461__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88461__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88461__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88461__8 = selectedNode.attr("Hrg)to*");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88461__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88461__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88461__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414null93313_add99403() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414null93313_add99403__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414null93313_add99403__4);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414null93313_add99403__4);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454null93419_add99500() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454null93419_add99500__9 = selectedNode.attr("enco=ing");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454null93419_add99500__9);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__7 = selectedNode.attr("enco=ing");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454null93419_add99500__9);
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454null93419_literalMutationString95266() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + ((" Hello\nthere \u00a0  " + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n Hello\nthere \u00a0   node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__7 = selectedNode.attr("enco=ing");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n Hello\nthere \u00a0   node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88454__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88453null93456() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88453__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88453__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88453__7 = selectedNode.attr("wUBzR Bw");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88453__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88453__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88453__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88453__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88453__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull88469() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__8);
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull88468_add92499() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        doc.hasText();
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88419null93447() throws Exception {
        final Document doc = createXmlDocument(null, "dotTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dotTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88419__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dotTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88419__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dotTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dotTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88419__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dotTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88419__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88419__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88419__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dotTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dotTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88419__4);
        Assert.assertEquals("version=\"\" encoding=\"dotTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dotTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dotTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88419__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull88469_literalMutationString90030() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<rot>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<rot>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<rot>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414null93313_literalMutationString95099() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7 = selectedNode.attr("");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88423_literalMutationString88884null100672() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "" + (("<Coot>\n" + " node\n") + "</root>");
        Assert.assertEquals("<Coot>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88423__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88423__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88423__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88423__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88423__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88423__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<Coot>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88423__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88423__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull88468_literalMutationString90003_literalMutationString94124() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=m\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=m\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__7 = selectedNode.attr("o26<d;SK");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=m\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88415null93484() throws Exception {
        final Document doc = createXmlDocument("dontoTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontoTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88415__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontoTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88415__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontoTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontoTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88415__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88415__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88415__8 = selectedNode.attr("version");
        Assert.assertEquals("dontoTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88415__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontoTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontoTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88415__4);
        Assert.assertEquals("version=\"dontoTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontoTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88415__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420_literalMutationNumber91235null100918() throws Exception {
        final Document doc = createXmlDocument(null, "do8ntTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"do8ntTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7 = selectedNode.attr("encoding");
        Assert.assertEquals("do8ntTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4);
        Assert.assertEquals("version=\"\" encoding=\"do8ntTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("do8ntTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420null93480() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull88468_literalMutationString90003_add98969() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        selectedNode.toString();
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__7 = selectedNode.attr("o26<d;SK");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88461_add93207null100793() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88461__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88461__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88461__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88461__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88461__8 = selectedNode.attr("Hrg)to*");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88461__8);
        selectedNode.hasParent();
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88461__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88461__7);
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88461__8);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414_literalMutationString89027null100700() throws Exception {
        final Document doc = createXmlDocument("donTouch", null, true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"donTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"donTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"donTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"donTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__8 = selectedNode.attr("version");
        Assert.assertEquals("donTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"donTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"donTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4);
        Assert.assertEquals("version=\"donTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"donTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull88468_literalMutationString90003null100692() throws Exception {
        final Document doc = createXmlDocument(null, null, true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__7 = selectedNode.attr("o26<d;SK");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__4);
        Assert.assertEquals("version=\"\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88468__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414null93313_add99415() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__8);
        doc.hasParent();
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7);
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__8);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull88469_add92531() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469_add92531__12 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469_add92531__12);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469__7);
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull88469_add92531__12);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414null93313_literalMutationString95091() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</roo>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</roo>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</roo>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88414__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420null93479_add100387() throws Exception {
        final Document doc = createXmlDocument(null, "do8ntTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4);
        Node o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420null93479_add100387__7 = doc.childNode(0);
        Assert.assertEquals("version=\"\" encoding=\"do8ntTouch\"", ((XmlDeclaration) (o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420null93479_add100387__7)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>", ((XmlDeclaration) (o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420null93479_add100387__7)).toString());
        Assert.assertTrue(((XmlDeclaration) (o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420null93479_add100387__7)).hasParent());
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"do8ntTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7 = selectedNode.attr("encoding");
        Assert.assertEquals("do8ntTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__4);
        Assert.assertEquals("version=\"\" encoding=\"do8ntTouch\"", ((XmlDeclaration) (o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420null93479_add100387__7)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>", ((XmlDeclaration) (o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420null93479_add100387__7)).toString());
        Assert.assertTrue(((XmlDeclaration) (o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420null93479_add100387__7)).hasParent());
        Assert.assertEquals("version=\"\" encoding=\"do8ntTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"do8ntTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("do8ntTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString88420__7);
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

