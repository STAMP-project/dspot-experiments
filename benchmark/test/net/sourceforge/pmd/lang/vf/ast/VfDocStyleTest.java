/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.vf.ast;


import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test parsing of a VF in document style, by checking the generated AST.
 * Original @author pieter_van_raemdonck - Application Engineers NV/SA -
 * www.ae.be
 *
 * @author sergey.gorbaty - VF adaptation
 */
public class VfDocStyleTest extends AbstractVfNodesTest {
    /**
     * Smoke test for VF parser.
     */
    @Test
    public void testSimplestVf() {
        assertNumberOfNodes(ASTElement.class, VfDocStyleTest.TEST_SIMPLEST_HTML, 1);
    }

    /**
     * Test the information on a Element and Attribute.
     */
    @Test
    public void testElementAttributeAndNamespace() {
        Set<VfNode> nodes = getNodes(null, VfDocStyleTest.TEST_ELEMENT_AND_NAMESPACE);
        Set<ASTElement> elementNodes = getNodesOfType(ASTElement.class, nodes);
        Assert.assertEquals("One element node expected!", 1, elementNodes.size());
        ASTElement element = elementNodes.iterator().next();
        Assert.assertEquals("Correct name expected!", "h:html", element.getName());
        Assert.assertEquals("Has namespace prefix!", true, element.isHasNamespacePrefix());
        Assert.assertEquals("Element is empty!", true, element.isEmpty());
        Assert.assertEquals("Correct namespace prefix of element expected!", "h", element.getNamespacePrefix());
        Assert.assertEquals("Correct local name of element expected!", "html", element.getLocalName());
        Set<ASTAttribute> attributeNodes = getNodesOfType(ASTAttribute.class, nodes);
        Assert.assertEquals("One attribute node expected!", 1, attributeNodes.size());
        ASTAttribute attribute = attributeNodes.iterator().next();
        Assert.assertEquals("Correct name expected!", "MyNsPrefix:MyAttr", attribute.getName());
        Assert.assertEquals("Has namespace prefix!", true, attribute.isHasNamespacePrefix());
        Assert.assertEquals("Correct namespace prefix of element expected!", "MyNsPrefix", attribute.getNamespacePrefix());
        Assert.assertEquals("Correct local name of element expected!", "MyAttr", attribute.getLocalName());
    }

    /**
     * Test exposing a bug of parsing error when having a hash as last character
     * in an attribute value.
     */
    @Test
    public void testAttributeValueContainingHash() {
        Set<VfNode> nodes = getNodes(null, VfDocStyleTest.TEST_ATTRIBUTE_VALUE_CONTAINING_HASH);
        Set<ASTAttribute> attributes = getNodesOfType(ASTAttribute.class, nodes);
        Assert.assertEquals("Three attributes expected!", 3, attributes.size());
        List<ASTAttribute> attrsList = new java.util.ArrayList(attributes);
        Collections.sort(attrsList, new Comparator<ASTAttribute>() {
            public int compare(ASTAttribute arg0, ASTAttribute arg1) {
                return arg0.getName().compareTo(arg1.getName());
            }
        });
        ASTAttribute attr = attrsList.get(0);
        Assert.assertEquals("Correct attribute name expected!", "foo", attr.getName());
        Assert.assertEquals("Correct attribute value expected!", "CREATE", attr.getFirstDescendantOfType(ASTText.class).getImage());
        attr = attrsList.get(1);
        Assert.assertEquals("Correct attribute name expected!", "href", attr.getName());
        Assert.assertEquals("Correct attribute value expected!", "#", attr.getFirstDescendantOfType(ASTText.class).getImage());
        attr = attrsList.get(2);
        Assert.assertEquals("Correct attribute name expected!", "something", attr.getName());
        Assert.assertEquals("Correct attribute value expected!", "#yes#", attr.getFirstDescendantOfType(ASTText.class).getImage());
    }

    /**
     * Test correct parsing of CDATA.
     */
    @Test
    public void testCData() {
        Set<ASTCData> cdataNodes = getNodes(ASTCData.class, VfDocStyleTest.TEST_CDATA);
        Assert.assertEquals("One CDATA node expected!", 1, cdataNodes.size());
        ASTCData cdata = cdataNodes.iterator().next();
        Assert.assertEquals("Content incorrectly parsed!", " some <cdata> ]] ]> ", cdata.getImage());
    }

    /**
     * Test parsing of Doctype declaration.
     */
    @Test
    public void testDoctype() {
        Set<VfNode> nodes = getNodes(null, VfDocStyleTest.TEST_DOCTYPE);
        Set<ASTDoctypeDeclaration> docTypeDeclarations = getNodesOfType(ASTDoctypeDeclaration.class, nodes);
        Assert.assertEquals("One doctype declaration expected!", 1, docTypeDeclarations.size());
        ASTDoctypeDeclaration docTypeDecl = docTypeDeclarations.iterator().next();
        Assert.assertEquals("Correct doctype-name expected!", "html", docTypeDecl.getName());
        Set<ASTDoctypeExternalId> externalIds = getNodesOfType(ASTDoctypeExternalId.class, nodes);
        Assert.assertEquals("One doctype external id expected!", 1, externalIds.size());
        ASTDoctypeExternalId externalId = externalIds.iterator().next();
        Assert.assertEquals("Correct external public id expected!", "-//W3C//DTD XHTML 1.1//EN", externalId.getPublicId());
        Assert.assertEquals("Correct external uri expected!", "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd", externalId.getUri());
    }

    /**
     * Test parsing of HTML &lt;script&gt; element.
     */
    @Test
    public void testHtmlScript() {
        Set<ASTHtmlScript> scripts = getNodes(ASTHtmlScript.class, VfDocStyleTest.TEST_HTML_SCRIPT);
        Assert.assertEquals("One script expected!", 1, scripts.size());
        ASTHtmlScript script = scripts.iterator().next();
        ASTText text = script.getFirstChildOfType(ASTText.class);
        Assert.assertEquals("Correct script content expected!", "Script!", text.getImage());
    }

    /**
     * Test parsing of EL in attribute of an element.
     */
    @Test
    public void testELInTagValue() {
        Set<ASTElement> elememts = getNodes(ASTElement.class, VfDocStyleTest.TEST_EL_IN_TAG_ATTRIBUTE);
        Assert.assertEquals("One element expected!", 1, elememts.size());
        ASTElement element = elememts.iterator().next();
        ASTAttributeValue attribute = element.getFirstDescendantOfType(ASTAttributeValue.class);
        ASTIdentifier id = attribute.getFirstDescendantOfType(ASTIdentifier.class);
        Assert.assertEquals("Correct identifier expected", "foo", id.getImage());
    }

    /**
     * Test parsing of EL in attribute of an element that also has a comment.
     */
    @Test
    public void testELInTagValueWithCommentDQ() {
        Set<ASTElement> elememts = getNodes(ASTElement.class, VfDocStyleTest.TEST_EL_IN_TAG_ATTRIBUTE_WITH_COMMENT);
        Assert.assertEquals("One element expected!", 1, elememts.size());
        ASTElement element = elememts.iterator().next();
        ASTElExpression elExpr = element.getFirstDescendantOfType(ASTElExpression.class);
        ASTIdentifier id = elExpr.getFirstDescendantOfType(ASTIdentifier.class);
        Assert.assertEquals("Correct identifier expected", "init", id.getImage());
    }

    /**
     * Test parsing of EL in attribute of an element that also has a comment.
     */
    @Test
    public void testELInTagValueWithCommentSQ() {
        Set<ASTElement> elememts = getNodes(ASTElement.class, VfDocStyleTest.TEST_EL_IN_TAG_ATTRIBUTE_WITH_COMMENT_SQ);
        Assert.assertEquals("One element expected!", 1, elememts.size());
        ASTElement element = elememts.iterator().next();
        ASTElExpression elExpr = element.getFirstDescendantOfType(ASTElExpression.class);
        ASTIdentifier id = elExpr.getFirstDescendantOfType(ASTIdentifier.class);
        Assert.assertEquals("Correct identifier expected", "init", id.getImage());
    }

    /**
     * Test parsing of EL in HTML &lt;script&gt; element.
     */
    @Test
    public void testELInHtmlScript() {
        Set<ASTHtmlScript> scripts = getNodes(ASTHtmlScript.class, VfDocStyleTest.TEST_EL_IN_HTML_SCRIPT);
        Assert.assertEquals("One script expected!", 1, scripts.size());
        ASTHtmlScript script = scripts.iterator().next();
        ASTText text = script.getFirstChildOfType(ASTText.class);
        Assert.assertEquals("Correct script content expected!", "vartext=", text.getImage());
        ASTElExpression el = script.getFirstChildOfType(ASTElExpression.class);
        ASTIdentifier id = el.getFirstDescendantOfType(ASTIdentifier.class);
        Assert.assertEquals("Correct EL content expected!", "elInScript", id.getImage());
    }

    /**
     * Test parsing of inline comment in EL.
     */
    @Test
    public void testInlineCommentInEL() {
        Set<ASTHtmlScript> scripts = getNodes(ASTHtmlScript.class, VfDocStyleTest.TEST_EL_IN_HTML_SCRIPT_WITH_COMMENT);
        Assert.assertEquals("One script expected!", 1, scripts.size());
        ASTHtmlScript script = scripts.iterator().next();
        ASTText text = script.getFirstChildOfType(ASTText.class);
        Assert.assertEquals("Correct script content expected!", "vartext=", text.getImage());
        ASTElExpression el = script.getFirstChildOfType(ASTElExpression.class);
        ASTIdentifier id = el.getFirstDescendantOfType(ASTIdentifier.class);
        Assert.assertEquals("Correct EL content expected!", "elInScript", id.getImage());
    }

    /**
     * Test parsing of quoted EL in HTML &lt;script&gt; element.
     */
    @Test
    public void testQuotedELInHtmlScript() {
        Set<ASTHtmlScript> scripts = getNodes(ASTHtmlScript.class, VfDocStyleTest.TEST_QUOTED_EL_IN_HTML_SCRIPT);
        Assert.assertEquals("One script expected!", 1, scripts.size());
        ASTHtmlScript script = scripts.iterator().next();
        ASTText text = script.getFirstChildOfType(ASTText.class);
        Assert.assertEquals("Correct script content expected!", "vartext='textHere", text.getImage());
        ASTElExpression el = script.getFirstChildOfType(ASTElExpression.class);
        ASTIdentifier id = el.getFirstDescendantOfType(ASTIdentifier.class);
        Assert.assertEquals("Correct EL content expected!", "elInScript", id.getImage());
    }

    /**
     * Test parsing of HTML &lt;script src="x"/&gt; element. It might not be
     * valid html but it is likely to appear in .page files.
     */
    @Test
    public void testImportHtmlScript() {
        Set<ASTHtmlScript> scripts = getNodes(ASTHtmlScript.class, VfDocStyleTest.TEST_IMPORT_JAVASCRIPT);
        Assert.assertEquals("One script expected!", 1, scripts.size());
        ASTHtmlScript script = scripts.iterator().next();
        List<ASTAttribute> attr = script.findDescendantsOfType(ASTAttribute.class);
        Assert.assertEquals("One script expected!", 1, attr.size());
        ASTAttribute att = attr.iterator().next();
        ASTAttributeValue val = att.getFirstChildOfType(ASTAttributeValue.class);
        ASTText text = val.getFirstChildOfType(ASTText.class);
        Assert.assertEquals("filename.js", text.getImage());
    }

    /**
     * Test parsing of HTML &lt;script&gt; element.
     */
    @Test
    public void testHtmlScriptWithAttribute() {
        Set<ASTHtmlScript> scripts = getNodes(ASTHtmlScript.class, VfDocStyleTest.TEST_HTML_SCRIPT_WITH_ATTRIBUTE);
        Assert.assertEquals("One script expected!", 1, scripts.size());
        ASTHtmlScript script = scripts.iterator().next();
        ASTText text = script.getFirstChildOfType(ASTText.class);
        Assert.assertEquals("Correct script content expected!", "Script!", text.getImage());
        List<ASTText> attrs = script.findDescendantsOfType(ASTText.class);
        Assert.assertTrue("text/javascript".equals(attrs.get(0).getImage()));
    }

    /**
     * A complex script containing HTML comments, escapes, quotes, etc.
     */
    @Test
    public void testComplexHtmlScript() {
        Set<ASTHtmlScript> script = getNodes(ASTHtmlScript.class, VfDocStyleTest.TEST_COMPLEX_SCRIPT);
        Assert.assertEquals("One script expected!", 1, script.size());
        ASTHtmlScript next = script.iterator().next();
        ASTText text = next.getFirstChildOfType(ASTText.class);
        Assert.assertTrue(text.getImage().contains("<!--"));
    }

    /**
     * Test parsing of HTML &lt;style&gt; element.
     */
    @Test
    public void testInlineCss() {
        Set<ASTElement> elements = getNodes(ASTElement.class, VfDocStyleTest.TEST_INLINE_STYLE);
        Assert.assertEquals("Two elements expected!", 3, elements.size());
    }

    /**
     * Test parsing of HTML text within element.
     */
    @Test
    public void testTextInTag() {
        Set<ASTText> scripts = getNodes(ASTText.class, VfDocStyleTest.TEST_TEXT_IN_TAG);
        Assert.assertEquals("One text chunk expected!", 1, scripts.size());
        ASTText script = scripts.iterator().next();
        Assert.assertEquals("Correct content expected!", " some text ", script.getImage());
    }

    /**
     * Test parsing of HTML with no spaces between tags. Parser is likely in
     * this scenario.
     */
    @Test
    public void noSpacesBetweenTags() {
        Set<ASTElement> scripts = getNodes(ASTElement.class, VfDocStyleTest.TEST_TAGS_NO_SPACE);
        Assert.assertEquals("Two tags expected!", 2, scripts.size());
        List<ASTElement> elmts = sortNodesByName(scripts);
        Iterator<ASTElement> iterator = elmts.iterator();
        ASTElement script = iterator.next();
        Assert.assertEquals("Correct content expected!", "a", script.getName());
        script = iterator.next();
        Assert.assertEquals("Correct content expected!", "b", script.getName());
    }

    /**
     * the $ sign might trick the parser into thinking an EL is next. He should
     * be able to treat it as plain text
     */
    @Test
    public void unclosedTagsWithDollar() {
        Set<ASTText> scripts = getNodes(ASTText.class, VfDocStyleTest.TEST_TAGS_WITH_DOLLAR);
        Assert.assertEquals("Two text chunks expected!", 2, scripts.size());
        ASTText script = scripts.iterator().next();
        Assert.assertEquals("Correct content expected!", " $ ", script.getImage());
    }

    /**
     * Make sure EL expressions aren't treated as plain text when they are
     * around unclosed tags.
     */
    @Test
    public void unclosedTagsWithELWithin() {
        Set<ASTElement> element = getNodes(ASTElement.class, VfDocStyleTest.TEST_TAGS_WITH_EL_WITHIN);
        Assert.assertEquals("One element expected!", 1, element.size());
        for (ASTElement elem : element) {
            ASTContent content = elem.getFirstChildOfType(ASTContent.class);
            List<ASTElExpression> els = content.findChildrenOfType(ASTElExpression.class);
            Assert.assertEquals("Two EL expressions expected!", 2, els.size());
            ASTElExpression node = ((ASTElExpression) (content.jjtGetChild(0)));
            ASTIdentifier id = node.getFirstDescendantOfType(ASTIdentifier.class);
            Assert.assertEquals("Correct content expected!", "expr1", id.getImage());
            node = ((ASTElExpression) (content.jjtGetChild(1)));
            id = node.getFirstDescendantOfType(ASTIdentifier.class);
            Assert.assertEquals("Correct content expected!", "expr2", id.getImage());
        }
    }

    /**
     * Test parsing of HTML &lt;script&gt; element.
     */
    @Test
    public void textAfterOpenAndClosedTag() {
        Set<ASTElement> nodes = getNodes(ASTElement.class, VfDocStyleTest.TEST_TEXT_AFTER_OPEN_AND_CLOSED_TAG);
        Assert.assertEquals("Two elements expected!", 2, nodes.size());
        List<ASTElement> elmts = sortNodesByName(nodes);
        Assert.assertEquals("First element should be a", "a", elmts.get(0).getName());
        Assert.assertFalse("first element should be closed", elmts.get(0).isUnclosed());
        Assert.assertEquals("Second element should be b", "b", elmts.get(1).getName());
        Assert.assertTrue("Second element should not be closed", elmts.get(1).isUnclosed());
        Set<ASTText> text = getNodes(ASTText.class, VfDocStyleTest.TEST_TEXT_AFTER_OPEN_AND_CLOSED_TAG);
        Assert.assertEquals("Two text chunks expected!", 2, text.size());
    }

    @Test
    public void quoteEL() {
        Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class, VfDocStyleTest.TEST_QUOTE_EL);
        Assert.assertEquals("One attribute expected!", 1, attributes.size());
        ASTAttributeValue attr = attributes.iterator().next();
        List<ASTElExpression> els = attr.findChildrenOfType(ASTElExpression.class);
        Assert.assertEquals("Must be 1!", 1, els.size());
        ASTExpression expr = els.get(0).getFirstChildOfType(ASTExpression.class);
        ASTIdentifier id = expr.getFirstChildOfType(ASTIdentifier.class);
        Assert.assertEquals("Expected to detect proper value for attribute!", "something", id.getImage());
    }

    /**
     * smoke test for a non-quoted attribute value
     */
    @Test
    public void quoteAttrValue() {
        Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class, VfDocStyleTest.TEST_ATTR);
        Assert.assertEquals("One attribute expected!", 1, attributes.size());
        ASTAttributeValue attr = attributes.iterator().next();
        ASTText text = attr.getFirstChildOfType(ASTText.class);
        Assert.assertEquals("Expected to detect proper value for attribute!", "yes|", text.getImage());
    }

    /**
     * tests whether parse correctly interprets empty non quote attribute
     */
    @Test
    public void noQuoteAttrEmpty() {
        Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class, VfDocStyleTest.TEST_EMPTY_ATTR);
        Assert.assertEquals("two attributes expected!", 2, attributes.size());
        Iterator<ASTAttributeValue> iterator = attributes.iterator();
        ASTAttributeValue attr = iterator.next();
        if ("http://someHost:/some_URL".equals(attr.getImage())) {
            // we have to employ this nasty work-around
            // in order to ensure that we check the proper attribute
            attr = iterator.next();
        }
        Assert.assertEquals("Expected to detect proper value for attribute!", null, attr.getImage());
    }

    /**
     * tests whether parse correctly interprets an tab instead of an attribute
     */
    @Test
    public void singleQuoteAttrTab() {
        Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class, VfDocStyleTest.TEST_TAB_ATTR);
        Assert.assertEquals("One attribute expected!", 1, attributes.size());
        Iterator<ASTAttributeValue> iterator = attributes.iterator();
        ASTAttributeValue attr = iterator.next();
        ASTText text = attr.getFirstChildOfType(ASTText.class);
        Assert.assertEquals("Expected to detect proper value for attribute!", "\t", text.getImage());
    }

    @Test
    public void unclosedTag() {
        Set<ASTElement> elements = getNodes(ASTElement.class, VfDocStyleTest.TEST_UNCLOSED_SIMPLE);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        Assert.assertEquals("2 tags expected", 2, elements.size());
        Assert.assertEquals("First element should be sorted tag:if", "tag:if", sortedElmnts.get(0).getName());
        Assert.assertEquals("Second element should be tag:someTag", "tag:someTag", sortedElmnts.get(1).getName());
        Assert.assertTrue(sortedElmnts.get(0).isEmpty());
        Assert.assertTrue(sortedElmnts.get(0).isUnclosed());
        Assert.assertFalse(sortedElmnts.get(1).isEmpty());
        Assert.assertFalse(sortedElmnts.get(1).isUnclosed());
    }

    @Test
    public void unclosedTagAndNoQuotesForAttribute() {
        Set<ASTElement> elements = getNodes(ASTElement.class, VfDocStyleTest.TEST_UNCLOSED_ATTR);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        Assert.assertEquals("2 tags expected", 2, elements.size());
        Assert.assertEquals("First element should be sorted tag:if", "tag:if", sortedElmnts.get(0).getName());
        Assert.assertEquals("Second element should be tag:someTag", "tag:someTag", sortedElmnts.get(1).getName());
        Assert.assertTrue(sortedElmnts.get(0).isEmpty());
        Assert.assertTrue(sortedElmnts.get(0).isUnclosed());
        Assert.assertFalse(sortedElmnts.get(1).isEmpty());
        Assert.assertFalse(sortedElmnts.get(1).isUnclosed());
    }

    @Test
    public void unclosedTagMultipleLevels() {
        Set<ASTElement> elements = getNodes(ASTElement.class, VfDocStyleTest.TEST_UNCLOSED_MULTIPLE_LEVELS);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        Assert.assertEquals("3 tags expected", 3, elements.size());
        Assert.assertEquals("First element should be sorted tag:someTag", "tag:someTag", sortedElmnts.get(0).getName());
        Assert.assertEquals("Second element should be tag:someTag", "tag:someTag", sortedElmnts.get(1).getName());
        Assert.assertEquals("Third element should be tag:x", "tag:x", sortedElmnts.get(2).getName());
        Assert.assertFalse(sortedElmnts.get(0).isEmpty());
        Assert.assertFalse(sortedElmnts.get(0).isUnclosed());
        Assert.assertTrue(sortedElmnts.get(1).isEmpty());
        Assert.assertTrue(sortedElmnts.get(1).isUnclosed());
        Assert.assertFalse(sortedElmnts.get(2).isEmpty());
        Assert.assertFalse(sortedElmnts.get(2).isUnclosed());
    }

    /**
     * &lt;html&gt; &lt;a1&gt; &lt;a2/&gt; &lt;b/&gt; &lt;/a1&gt; &lt;/html&gt;
     */
    @Test
    public void nestedEmptyTags() {
        Set<ASTElement> elements = getNodes(ASTElement.class, VfDocStyleTest.TEST_MULTIPLE_EMPTY_TAGS);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        Assert.assertEquals("4 tags expected", 4, elements.size());
        Assert.assertEquals("First element should a1", "a1", sortedElmnts.get(0).getName());
        Assert.assertEquals("Second element should be a2", "a2", sortedElmnts.get(1).getName());
        Assert.assertEquals("Third element should be b", "b", sortedElmnts.get(2).getName());
        Assert.assertEquals("Third element should be html", "html", sortedElmnts.get(3).getName());
        // a1
        Assert.assertFalse(sortedElmnts.get(0).isEmpty());
        Assert.assertFalse(sortedElmnts.get(0).isUnclosed());
        // a2
        Assert.assertTrue(sortedElmnts.get(1).isEmpty());
        Assert.assertFalse(sortedElmnts.get(1).isUnclosed());
        // b
        Assert.assertTrue(sortedElmnts.get(2).isEmpty());
        Assert.assertFalse(sortedElmnts.get(2).isUnclosed());
        // html
        Assert.assertFalse(sortedElmnts.get(3).isEmpty());
        Assert.assertFalse(sortedElmnts.get(3).isUnclosed());
    }

    /**
     * &lt;html&gt; &lt;a1&gt; &lt;a2&gt; &lt;a3&gt; &lt;/a2&gt; &lt;/a1&gt;
     * &lt;b/&gt; &lt;a4/&gt; &lt;/html&gt;
     */
    @Test
    public void nestedMultipleTags() {
        Set<ASTElement> elements = getNodes(ASTElement.class, VfDocStyleTest.TEST_MULTIPLE_NESTED_TAGS);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        Assert.assertEquals("4 tags expected", 6, elements.size());
        Assert.assertEquals("First element should a1", "a1", sortedElmnts.get(0).getName());
        Assert.assertEquals("Second element should be a2", "a2", sortedElmnts.get(1).getName());
        Assert.assertEquals("Third element should be a3", "a3", sortedElmnts.get(2).getName());
        Assert.assertEquals("Forth element should be a4", "a4", sortedElmnts.get(3).getName());
        Assert.assertEquals("Fifth element should be b", "b", sortedElmnts.get(4).getName());
        Assert.assertEquals("Sixth element should be html", "html", sortedElmnts.get(5).getName());
        // a1 not empty and closed
        Assert.assertFalse(sortedElmnts.get(0).isEmpty());
        Assert.assertFalse(sortedElmnts.get(0).isUnclosed());
        // a2 not empty and closed
        Assert.assertFalse(sortedElmnts.get(1).isEmpty());
        Assert.assertFalse(sortedElmnts.get(1).isUnclosed());
        // a3 empty and not closed
        Assert.assertTrue(sortedElmnts.get(2).isEmpty());
        Assert.assertTrue(sortedElmnts.get(2).isUnclosed());
        // a4 empty but closed
        Assert.assertTrue(sortedElmnts.get(3).isEmpty());
        Assert.assertFalse(sortedElmnts.get(3).isUnclosed());
        // b empty but closed
        Assert.assertTrue(sortedElmnts.get(4).isEmpty());
        Assert.assertFalse(sortedElmnts.get(4).isUnclosed());
        // html not empty and closed
        Assert.assertFalse(sortedElmnts.get(5).isEmpty());
        Assert.assertFalse(sortedElmnts.get(5).isUnclosed());
    }

    /**
     * will test &lt;x&gt; &lt;a&gt; &lt;b&gt; &lt;b&gt; &lt;/x&gt; &lt;/a&gt;
     * &lt;/x&gt; . Here x is the first tag to be closed thus rendering the next
     * close of a (&lt;/a&gt;) to be disregarded.
     */
    @Test
    public void unclosedParentTagClosedBeforeChild() {
        Set<ASTElement> elements = getNodes(ASTElement.class, VfDocStyleTest.TEST_UNCLOSED_END_AFTER_PARENT_CLOSE);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        Assert.assertEquals("4 tags expected", 4, elements.size());
        Assert.assertEquals("First element should be 'a'", "a", sortedElmnts.get(0).getName());
        Assert.assertEquals("Second element should be b", "b", sortedElmnts.get(1).getName());
        Assert.assertEquals("Third element should be b", "b", sortedElmnts.get(2).getName());
        Assert.assertEquals("Forth element should be x", "x", sortedElmnts.get(3).getName());
        // a
        Assert.assertTrue(sortedElmnts.get(0).isEmpty());
        Assert.assertTrue(sortedElmnts.get(0).isUnclosed());
        // b
        Assert.assertTrue(sortedElmnts.get(1).isEmpty());
        Assert.assertTrue(sortedElmnts.get(1).isUnclosed());
        // b
        Assert.assertTrue(sortedElmnts.get(2).isEmpty());
        Assert.assertTrue(sortedElmnts.get(2).isUnclosed());
        // x
        Assert.assertFalse(sortedElmnts.get(3).isEmpty());
        Assert.assertFalse(sortedElmnts.get(3).isUnclosed());
    }

    /**
     * &lt;x&gt; &lt;a&gt; &lt;b&gt; &lt;b&gt; &lt;/z&gt; &lt;/a&gt; &lt;/x&gt;
     * An unmatched closing of 'z' appears randomly in the document. This should
     * be disregarded and structure of children and parents should not be
     * influenced. in other words &lt;/a&gt; should close the first &lt;a&gt;
     * tag , &lt;/x&gt; should close the first &lt;x&gt;, etc.
     */
    @Test
    public void unmatchedTagDoesNotInfluenceStructure() {
        Set<ASTElement> elements = getNodes(ASTElement.class, VfDocStyleTest.TEST_UNCLOSED_UNMATCHED_CLOSING_TAG);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        Assert.assertEquals("4 tags expected", 4, elements.size());
        Assert.assertEquals("First element should be 'a'", "a", sortedElmnts.get(0).getName());
        Assert.assertEquals("Second element should be b", "b", sortedElmnts.get(1).getName());
        Assert.assertEquals("Third element should be b", "b", sortedElmnts.get(2).getName());
        Assert.assertEquals("Forth element should be x", "x", sortedElmnts.get(3).getName());
        // a is not empty and closed
        Assert.assertFalse(sortedElmnts.get(0).isEmpty());
        Assert.assertFalse(sortedElmnts.get(0).isUnclosed());
        // b empty and unclosed
        Assert.assertTrue(sortedElmnts.get(1).isEmpty());
        Assert.assertTrue(sortedElmnts.get(1).isUnclosed());
        // b empty and unclosed
        Assert.assertTrue(sortedElmnts.get(2).isEmpty());
        Assert.assertTrue(sortedElmnts.get(2).isUnclosed());
        // x not empty and closed
        Assert.assertFalse(sortedElmnts.get(3).isEmpty());
        Assert.assertFalse(sortedElmnts.get(3).isUnclosed());
    }

    /**
     * &lt;a&gt; &lt;x&gt; &lt;a&gt; &lt;b&gt; &lt;b&gt; &lt;/z&gt; &lt;/a&gt;
     * &lt;/x&gt; An unmatched closing of 'z' appears randomly in the document.
     * This should be disregarded and structure of children and parents should
     * not be influenced. Also un unclosed &lt;a&gt; tag appears at the start of
     * the document
     */
    @Test
    public void unclosedStartTagWithUnmatchedCloseOfDifferentTag() {
        Set<ASTElement> elements = getNodes(ASTElement.class, VfDocStyleTest.TEST_UNCLOSED_START_TAG_WITH_UNMATCHED_CLOSE);
        List<ASTElement> sortedElmnts = sortNodesByName(elements);
        Assert.assertEquals("5 tags expected", 5, elements.size());
        Assert.assertEquals("First element should be 'a'", "a", sortedElmnts.get(0).getName());
        Assert.assertEquals("Second element should be a", "a", sortedElmnts.get(1).getName());
        Assert.assertEquals("Third element should be b", "b", sortedElmnts.get(2).getName());
        Assert.assertEquals("Forth element should be b", "b", sortedElmnts.get(3).getName());
        Assert.assertEquals("Fifth element should be x", "x", sortedElmnts.get(4).getName());
        // first a is empty and unclosed
        Assert.assertTrue(sortedElmnts.get(0).isEmpty());
        Assert.assertTrue(sortedElmnts.get(0).isUnclosed());
        // second a not empty and closed
        Assert.assertFalse(sortedElmnts.get(1).isEmpty());
        Assert.assertFalse(sortedElmnts.get(1).isUnclosed());
        // b empty and unclosed
        Assert.assertTrue(sortedElmnts.get(2).isEmpty());
        Assert.assertTrue(sortedElmnts.get(2).isUnclosed());
        // b empty and unclosed
        Assert.assertTrue(sortedElmnts.get(3).isEmpty());
        Assert.assertTrue(sortedElmnts.get(3).isUnclosed());
        // x not empty and closed
        Assert.assertFalse(sortedElmnts.get(4).isEmpty());
        Assert.assertFalse(sortedElmnts.get(4).isUnclosed());
    }

    @Test
    public void noQuoteAttrWithJspEL() {
        Set<ASTAttributeValue> attributes = getNodes(ASTAttributeValue.class, VfDocStyleTest.TEST_NO_QUOTE_ATTR_WITH_EL);
        Assert.assertEquals("One attribute expected!", 1, attributes.size());
        Iterator<ASTAttributeValue> iterator = attributes.iterator();
        ASTAttributeValue attr = iterator.next();
        ASTIdentifier id = attr.getFirstDescendantOfType(ASTIdentifier.class);
        Assert.assertEquals("Expected to detect proper value for EL in attribute!", "something", id.getImage());
    }

    private static final String TEST_SIMPLEST_HTML = "<html/>";

    private static final String TEST_ELEMENT_AND_NAMESPACE = "<h:html MyNsPrefix:MyAttr='MyValue'/>";

    private static final String TEST_CDATA = "<html><![CDATA[ some <cdata> ]] ]> ]]></html>";

    private static final String TEST_DOCTYPE = "<?xml version=\"1.0\" standalone=\'yes\'?>\n" + (("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" " + "\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n") + "<greeting>Hello, world!</greeting>");

    private static final String TEST_ATTRIBUTE_VALUE_CONTAINING_HASH = "<tag:if something=\"#yes#\" foo=\"CREATE\">  <a href=\"#\">foo</a> </tag:if>";

    private static final String TEST_HTML_SCRIPT = "<html><head><script>Script!</script></head></html>";

    private static final String TEST_EL_IN_TAG_ATTRIBUTE = "<apex:page action=\"{!foo}\">text</apex:page>";

    private static final String TEST_EL_IN_TAG_ATTRIBUTE_WITH_COMMENT = "<apex:page action=\"{!/*comment here*/init}\">text</apex:page>";

    private static final String TEST_EL_IN_TAG_ATTRIBUTE_WITH_COMMENT_SQ = "<apex:page action='{!/*comment here*/init}'>text</apex:page>";

    private static final String TEST_EL_IN_HTML_SCRIPT = "<html><head><script>var text={!elInScript};</script></head></html>";

    private static final String TEST_EL_IN_HTML_SCRIPT_WITH_COMMENT = "<html><head><script>var text={!/*junk1*/elInScript/*junk2*/};</script></head></html>";

    private static final String TEST_QUOTED_EL_IN_HTML_SCRIPT = "<html><head><script>var text='textHere{!elInScript}';</script></head></html>";

    private static final String TEST_IMPORT_JAVASCRIPT = "<html><head><script src=\"filename.js\" /></head></html>";

    private static final String TEST_HTML_SCRIPT_WITH_ATTRIBUTE = "<html><head><script type=\"text/javascript\">Script!</script></head></html>";

    private static final String TEST_COMPLEX_SCRIPT = "<HTML><BODY><!--Java Script-->" + (((((("<SCRIPT language='JavaScript' type='text/javascript'>" + "<!--function calcDays(){") + " date1 = date1.split(\"-\");  date2 = date2.split(\"-\");") + " var sDate = new Date(date1[0]+\"/\"+date1[1]+\"/\"+date1[2]);") + " var eDate = new Date(date2[0]+\"/\"+date2[1]+\"/\"+date2[2]);") + " onload=calcDays;//-->") + "</SCRIPT></BODY></HTML>;");

    private static final String TEST_INLINE_STYLE = "<html><head><style> div { color:red; } </style></head></html>";

    private static final String TEST_TEXT_IN_TAG = "<a> some text </a>";

    private static final String TEST_TAGS_NO_SPACE = "<a><b></a>";

    private static final String TEST_TAGS_WITH_DOLLAR = "<a> $ <b> $ </a>";

    private static final String TEST_TAGS_WITH_EL_WITHIN = "<a>{!expr1}{!expr2}</a>";

    private static final String TEST_TEXT_AFTER_OPEN_AND_CLOSED_TAG = "<a> some text <b> some text </a>";

    private static final String TEST_QUOTE_EL = "<tag:if something=\"{!something}\" > </tag:if>";

    private static final String TEST_ATTR = "<tag:if something=\"yes|\" > </tag:if>";

    private static final String TEST_EMPTY_ATTR = "<tag:if something= >  <a href=\"http://someHost:/some_URL\" >foo</a> </tag:if>";

    private static final String TEST_TAB_ATTR = "<tag:if something=\'\t\' >   </tag:if>";

    private static final String TEST_UNCLOSED_SIMPLE = "<tag:someTag> <tag:if someting=\"x\" > </tag:someTag>";

    /**
     * someTag is closed just once
     */
    private static final String TEST_UNCLOSED_MULTIPLE_LEVELS = "<tag:x> <tag:someTag> <tag:someTag someting=\"x\" > </tag:someTag> </tag:x>";

    /**
     * nested empty tags
     */
    private static final String TEST_MULTIPLE_EMPTY_TAGS = "<html> <a1> <a2/> <b/> </a1> </html>";

    /**
     * multiple nested tags with some tags unclosed
     */
    private static final String TEST_MULTIPLE_NESTED_TAGS = "<html> <a1> <a2> <a3> </a2> </a1> <b/> <a4/> </html>";

    /**
     * </x> will close before </a>, thus leaving <a> to remain unclosed
     */
    private static final String TEST_UNCLOSED_END_AFTER_PARENT_CLOSE = "<x> <a> <b> <b> </x> </a> aa </x> bb </x>";

    /**
     * </z> is just a dangling closing tag not matching any parent. The parser
     * should disregard it
     */
    private static final String TEST_UNCLOSED_UNMATCHED_CLOSING_TAG = "<x> <a> <b> <b> </z> </a> </x>";

    /**
     * First <a> tag does not close. The first closing of </a> will match the
     * second opening of a. Another rogue </z> is there for testing compliance
     */
    private static final String TEST_UNCLOSED_START_TAG_WITH_UNMATCHED_CLOSE = "<a> <x> <a> <b> <b> </z> </a> </x>";

    private static final String TEST_UNCLOSED_ATTR = "<tag:someTag> <tag:if someting='x' > </tag:someTag>";

    private static final String TEST_NO_QUOTE_ATTR_WITH_EL = "<apex:someTag something={!something} > foo </apex:someTag>";
}

