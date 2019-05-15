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
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24235null29299() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + ((" Hello\nthere \u00a0  " + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n Hello\nthere \u00a0   node\n</root>", xmlCharset);
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24235__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24235__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24235__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24235__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n Hello\nthere \u00a0   node\n</root>", xmlCharset);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24235__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24234null29344() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root\n node\n</root>", xmlCharset);
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24234__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24234__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24234__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24234__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root\n node\n</root>", xmlCharset);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24234__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24243null29372() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " nDode\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n nDode\n</root>", xmlCharset);
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24243__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24243__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24243__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24243__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n nDode\n</root>", xmlCharset);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24243__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24240null29230() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " Hello\nthere \u00a0  ") + "</root>");
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24240__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24240__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24240__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24240__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24240__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24230null29217() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "4e{_xS][R8jeG!}m9G&2nmDwiq[(MS/Tgmf!(GRJ%c9}bWWj;" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("4e{_xS][R8jeG!}m9G&2nmDwiq[(MS/Tgmf!(GRJ%c9}bWWj;<root>\n node\n</root>", xmlCharset);
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24230__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24230__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24230__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24230__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("4e{_xS][R8jeG!}m9G&2nmDwiq[(MS/Tgmf!(GRJ%c9}bWWj;<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24230__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24252null29150() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24252__8 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24252__8);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24252__9 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24252__9);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24252__8);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24238null29288() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " noe\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n noe\n</root>", xmlCharset);
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24238__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24238__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24238__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24238__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n noe\n</root>", xmlCharset);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24238__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24228null29368() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontToucnh\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontToucnh\"?>\n<root>\n node\n</root>", xmlCharset);
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24228__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24228__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24228__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24228__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontToucnh\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24228__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24246null29213() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "<k/root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n<k/root>", xmlCharset);
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24246__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24246__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24246__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24246__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n<k/root>", xmlCharset);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24246__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24242null29291() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " ntde\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n ntde\n</root>", xmlCharset);
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24242__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24242__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24242__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24242__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n ntde\n</root>", xmlCharset);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24242__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull24271() throws Exception {
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
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24271__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24271__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24271__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24271__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24271__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24248null29332() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + ",/root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n,/root>", xmlCharset);
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24248__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24248__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24248__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24248__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n,/root>", xmlCharset);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24248__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24244null29328() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n", xmlCharset);
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24244__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24244__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24244__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24244__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n", xmlCharset);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24244__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24253null29307() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24253__8 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24253__8);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24253__9 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24253__9);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24253__8);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24231null29336() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" Bncoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" Bncoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24231__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24231__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24231__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24231__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" Bncoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24231__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull24272() throws Exception {
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
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24272__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24272__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24272__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24272__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24272__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24239null29225() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + "") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n</root>", xmlCharset);
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24239__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24239__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24239__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24239__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n</root>", xmlCharset);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24239__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24245null29169() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + " Hello\nthere \u00a0  ");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n Hello\nthere \u00a0  ", xmlCharset);
        doc.toString();
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24245__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24245__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24245__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24245__8);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n Hello\nthere \u00a0  ", xmlCharset);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24245__7);
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

