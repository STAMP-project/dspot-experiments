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
    public void testMetaCharsetUpdateXmlDisabledNoChanges_add24092null29023() throws Exception {
        Document o_testMetaCharsetUpdateXmlDisabledNoChanges_add24092__1 = createXmlDocument("dontTouch", "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (o_testMetaCharsetUpdateXmlDisabledNoChanges_add24092__1)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChanges_add24092__1)).isBlock());
        Assert.assertTrue(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChanges_add24092__1)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (o_testMetaCharsetUpdateXmlDisabledNoChanges_add24092__1)).toString());
        Assert.assertFalse(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChanges_add24092__1)).hasParent());
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24092__5 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24092__5);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24092__8 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24092__8);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24092__9 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24092__9);
        Assert.assertFalse(((Collection) (((Document) (o_testMetaCharsetUpdateXmlDisabledNoChanges_add24092__1)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChanges_add24092__1)).isBlock());
        Assert.assertTrue(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChanges_add24092__1)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (o_testMetaCharsetUpdateXmlDisabledNoChanges_add24092__1)).toString());
        Assert.assertFalse(((Document) (o_testMetaCharsetUpdateXmlDisabledNoChanges_add24092__1)).hasParent());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24092__5);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24092__8);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083null29004() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__7 = selectedNode.attr("Q1pW0o$<");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24086null29073() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24086__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24086__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24086__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24086__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24086__8 = selectedNode.attr("");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24086__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24086__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24086__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_add24095null29030() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24095__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24095__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24095__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24095__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24095__8 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24095__8);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24095__9 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24095__9);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24095__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24095__7);
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24095__8);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_add24094null29036() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24094__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24094__4);
        Node o_testMetaCharsetUpdateXmlDisabledNoChanges_add24094__5 = doc.childNode(0);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (o_testMetaCharsetUpdateXmlDisabledNoChanges_add24094__5)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (o_testMetaCharsetUpdateXmlDisabledNoChanges_add24094__5)).toString());
        Assert.assertTrue(((XmlDeclaration) (o_testMetaCharsetUpdateXmlDisabledNoChanges_add24094__5)).hasParent());
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24094__8 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24094__8);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24094__9 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24094__9);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24094__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (o_testMetaCharsetUpdateXmlDisabledNoChanges_add24094__5)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (o_testMetaCharsetUpdateXmlDisabledNoChanges_add24094__5)).toString());
        Assert.assertTrue(((XmlDeclaration) (o_testMetaCharsetUpdateXmlDisabledNoChanges_add24094__5)).hasParent());
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24094__8);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24043null29125() throws Exception {
        final Document doc = createXmlDocument("don[tTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"don[tTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24043__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"don[tTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24043__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"don[tTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"don[tTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24043__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24043__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24043__8 = selectedNode.attr("version");
        Assert.assertEquals("don[tTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24043__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"don[tTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"don[tTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24043__4);
        Assert.assertEquals("version=\"don[tTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"don[tTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24043__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087null28924() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__8 = selectedNode.attr(" Hello\nthere \u00a0  ");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24084null29128() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24084__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24084__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24084__7 = selectedNode.attr("ensoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24084__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24084__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24084__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24084__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24084__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24080null28920() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24080__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24080__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24080__7 = selectedNode.attr("");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24080__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24080__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24080__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24080__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24080__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087null28925() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__8 = selectedNode.attr(" Hello\nthere \u00a0  ");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_add24096null29040() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__8);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__9 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__9);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__7);
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__8);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull24097_literalMutationString24324() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__8 = selectedNode.attr(" Hello\nthere \u00a0  ");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24040null29137() throws Exception {
        final Document doc = createXmlDocument(" Hello\nthere \u00a0  ", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\" Hello\nthere &nbsp;  \" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24040__4 = doc.toString();
        Assert.assertEquals("<?xml version=\" Hello\nthere &nbsp;  \" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24040__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\" Hello\nthere &nbsp;  \" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\" Hello\nthere &nbsp;  \" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24040__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24040__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24040__8 = selectedNode.attr("version");
        Assert.assertEquals(" Hello\nthere \u00a0  ", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24040__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\" Hello\nthere &nbsp;  \" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\" Hello\nthere &nbsp;  \" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24040__4);
        Assert.assertEquals("version=\" Hello\nthere &nbsp;  \" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\" Hello\nthere &nbsp;  \" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24040__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull24097_add27560() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        selectedNode.hasParent();
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24085null28961() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24085__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24085__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24085__7 = selectedNode.attr("ecoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24085__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24085__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24085__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24085__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24085__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_add24093null29026() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        doc.toString();
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24093__5 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24093__5);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24093__8 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24093__8);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24093__9 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24093__9);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24093__5);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24093__8);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull24098_add27573() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        ((Document) (doc)).getAllElements().isEmpty();
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull24097() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24097__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull24098() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_add24096null29039() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__8);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__9 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__9);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__7);
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_add24096__8);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24041null29017() throws Exception {
        final Document doc = createXmlDocument("ne}kmhsN ", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"ne}kmhsN \" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24041__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"ne}kmhsN \" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24041__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"ne}kmhsN \" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"ne}kmhsN \" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24041__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24041__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24041__8 = selectedNode.attr("version");
        Assert.assertEquals("ne}kmhsN ", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24041__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"ne}kmhsN \" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"ne}kmhsN \" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24041__4);
        Assert.assertEquals("version=\"ne}kmhsN \" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"ne}kmhsN \" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24041__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull24098_literalMutationString24354() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "<R/root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n<R/root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n<R/root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24098__7);
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

