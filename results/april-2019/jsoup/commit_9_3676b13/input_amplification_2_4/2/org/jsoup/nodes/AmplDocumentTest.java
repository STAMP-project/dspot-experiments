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
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24071null29018() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" en:oding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" en:oding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24071__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24071__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24071__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24071__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24071__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24071__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" en:oding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24071__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24071__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24086null29087() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + " Hello\nthere \u00a0  ");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n Hello\nthere \u00a0  ", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24086__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24086__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24086__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24086__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24086__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24086__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n Hello\nthere \u00a0  ", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24086__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24086__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24074null28953() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<rootZ>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<rootZ>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24074__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24074__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24074__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24074__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24074__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24074__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<rootZ>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24074__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24074__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083null29013() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " ngde\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n ngde\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n ngde\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24088null28949() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "<!root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n<!root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24088__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24088__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24088__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24088__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24088__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24088__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n<!root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24088__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24088__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087null29005() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "y({[J})");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\ny({[J})", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\ny({[J})", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083null29014() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " ngde\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n ngde\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n ngde\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24083__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087null29006() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "y({[J})");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\ny({[J})", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\ny({[J})", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24087__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24081null29156() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " Hello\nthere \u00a0  ") + "</root>");
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24081__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24081__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24081__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24081__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24081__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24081__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24081__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24081__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24082null29080() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + "jRu^4Q") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\njRu^4Q</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24082__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24082__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24082__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24082__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24082__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24082__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\njRu^4Q</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24082__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24082__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24084null28930() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " noAde\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n noAde\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24084__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24084__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24084__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24084__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24084__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24084__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n noAde\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24084__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24084__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24075null29099() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24075__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24075__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24075__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24075__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24075__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24075__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24075__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24075__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24082null29079() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + "jRu^4Q") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\njRu^4Q</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24082__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24082__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24082__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24082__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24082__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24082__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\njRu^4Q</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24082__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24082__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24079null28962() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " noe\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n noe\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24079__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24079__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24079__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24079__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24079__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24079__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n noe\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24079__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24079__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull24112() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24112__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24112__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24112__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24112__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24112__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24112__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24112__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24112__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChangesnull24113() throws Exception {
        final Document doc = createXmlDocument("dontTouch", null, true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24113__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24113__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24113__7 = selectedNode.attr("encoding");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24113__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChangesnull24113__8 = selectedNode.attr("version");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24113__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24113__4);
        Assert.assertEquals("version=\"dontTouch\" encoding=\"\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChangesnull24113__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24072null29139() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xmlO version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xmlO version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24072__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24072__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24072__7 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24072__7);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24072__8 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24072__8);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xmlO version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24072__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationString24072__7);
    }

    @Test(timeout = 10000)
    public void testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24094null28937() throws Exception {
        final Document doc = createXmlDocument(null, "dontTouch", true);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String xmlCharset = "<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n" + (("<root>\n" + " node\n") + "</root>");
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24094__4 = doc.toString();
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24094__4);
        XmlDeclaration selectedNode = ((XmlDeclaration) (doc.childNode(0)));
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24094__8 = selectedNode.attr("encoding");
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24094__8);
        String o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24094__9 = selectedNode.attr("version");
        Assert.assertEquals("", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24094__9);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<?xml version=\"dontTouch\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", xmlCharset);
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>\n<root>\n node\n</root>", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24094__4);
        Assert.assertEquals("version=\"\" encoding=\"dontTouch\"", ((XmlDeclaration) (selectedNode)).getWholeDeclaration());
        Assert.assertEquals("<?xml version=\"\" encoding=\"dontTouch\"?>", ((XmlDeclaration) (selectedNode)).toString());
        Assert.assertTrue(((XmlDeclaration) (selectedNode)).hasParent());
        Assert.assertEquals("dontTouch", o_testMetaCharsetUpdateXmlDisabledNoChanges_literalMutationNumber24094__8);
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

