/**
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.xml;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Construct a DOM and then interrogate it.
 */
public class DomTest extends TestCase {
    private Transformer transformer;

    private DocumentBuilder builder;

    private DOMImplementation domImplementation;

    private final String xml = "<!DOCTYPE menu [" + ((((((((((((((((("  <!ENTITY sp \"Maple Syrup\">" + "  <!NOTATION png SYSTEM \"image/png\">") + "]>") + "<menu>\n") + "  <item xmlns=\"http://food\" xmlns:a=\"http://addons\">\n") + "    <name a:standard=\"strawberry\" deluxe=\"&sp;\">Waffles</name>\n") + "    <description xmlns=\"http://marketing\">Belgian<![CDATA[ waffles & strawberries (< 5g ]]>of fat)</description>\n") + "    <a:option>Whipped Cream</a:option>\n") + "    <a:option>&sp;</a:option>\n") + "    <?wafflemaker square shape?>\n") + "    <nutrition>\n") + "      <a:vitamins xmlns:a=\"http://usda\">\n") + "        <!-- add other vitamins? --> \n") + "        <a:vitaminc>60%</a:vitaminc>\n") + "      </a:vitamins>\n") + "    </nutrition>\n") + "  </item>\n") + "</menu>");

    private Document document;

    private DocumentType doctype;

    private Entity sp;

    private Notation png;

    private Element menu;

    private Element item;

    private Attr itemXmlns;

    private Attr itemXmlnsA;

    private Element name;

    private Attr standard;

    private Attr deluxe;

    private Text waffles;

    private Element description;

    private Text descriptionText1;

    private CDATASection descriptionText2;

    private Text descriptionText3;

    private Element option1;

    private Element option2;

    private Node option2Reference;// resolved to Text on RI, an EntityReference on Dalvik


    private ProcessingInstruction wafflemaker;

    private Element nutrition;

    private Element vitamins;

    private Attr vitaminsXmlnsA;

    private Comment comment;

    private Element vitaminc;

    private Text vitamincText;

    private List<Node> allNodes;

    /**
     * Android's parsed DOM doesn't include entity declarations. These nodes will
     * only be tested for implementations that support them.
     */
    public void testEntityDeclarations() {
        TestCase.assertNotNull("This implementation does not parse entity declarations", sp);
    }

    /**
     * Android's parsed DOM doesn't include notations. These nodes will only be
     * tested for implementations that support them.
     */
    public void testNotations() {
        TestCase.assertNotNull("This implementation does not parse notations", png);
    }

    public void testLookupNamespaceURIByPrefix() {
        TestCase.assertEquals(null, doctype.lookupNamespaceURI("a"));
        if ((sp) != null) {
            TestCase.assertEquals(null, sp.lookupNamespaceURI("a"));
        }
        if ((png) != null) {
            TestCase.assertEquals(null, png.lookupNamespaceURI("a"));
        }
        TestCase.assertEquals(null, document.lookupNamespaceURI("a"));
        TestCase.assertEquals(null, menu.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://addons", item.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://addons", itemXmlns.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://addons", itemXmlnsA.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://addons", name.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://addons", standard.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://addons", deluxe.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://addons", description.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://addons", descriptionText1.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://addons", descriptionText2.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://addons", descriptionText3.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://addons", option1.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://addons", option2.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://addons", option2Reference.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://addons", wafflemaker.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://addons", nutrition.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://usda", vitamins.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://usda", vitaminsXmlnsA.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://usda", comment.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://usda", vitaminc.lookupNamespaceURI("a"));
        TestCase.assertEquals("http://usda", vitamincText.lookupNamespaceURI("a"));
    }

    public void testLookupNamespaceURIWithNullPrefix() {
        TestCase.assertEquals(null, document.lookupNamespaceURI(null));
        TestCase.assertEquals(null, doctype.lookupNamespaceURI(null));
        if ((sp) != null) {
            TestCase.assertEquals(null, sp.lookupNamespaceURI(null));
        }
        if ((png) != null) {
            TestCase.assertEquals(null, png.lookupNamespaceURI(null));
        }
        TestCase.assertEquals(null, menu.lookupNamespaceURI(null));
        TestCase.assertEquals("http://food", item.lookupNamespaceURI(null));
        TestCase.assertEquals("http://food", itemXmlns.lookupNamespaceURI(null));
        TestCase.assertEquals("http://food", itemXmlnsA.lookupNamespaceURI(null));
        TestCase.assertEquals("http://food", name.lookupNamespaceURI(null));
        TestCase.assertEquals("http://food", standard.lookupNamespaceURI(null));
        TestCase.assertEquals("http://food", deluxe.lookupNamespaceURI(null));
        TestCase.assertEquals("http://marketing", description.lookupNamespaceURI(null));
        TestCase.assertEquals("http://marketing", descriptionText1.lookupNamespaceURI(null));
        TestCase.assertEquals("http://marketing", descriptionText2.lookupNamespaceURI(null));
        TestCase.assertEquals("http://marketing", descriptionText3.lookupNamespaceURI(null));
        TestCase.assertEquals("http://food", option1.lookupNamespaceURI(null));
        TestCase.assertEquals("http://food", option2.lookupNamespaceURI(null));
        TestCase.assertEquals("http://food", option2Reference.lookupNamespaceURI(null));
        TestCase.assertEquals("http://food", wafflemaker.lookupNamespaceURI(null));
        TestCase.assertEquals("http://food", nutrition.lookupNamespaceURI(null));
        TestCase.assertEquals("http://food", vitamins.lookupNamespaceURI(null));
        TestCase.assertEquals("http://food", vitaminsXmlnsA.lookupNamespaceURI(null));
        TestCase.assertEquals("http://food", comment.lookupNamespaceURI(null));
        TestCase.assertEquals("http://food", vitaminc.lookupNamespaceURI(null));
        TestCase.assertEquals("http://food", vitamincText.lookupNamespaceURI(null));
    }

    public void testLookupNamespaceURIWithXmlnsPrefix() {
        for (Node node : allNodes) {
            TestCase.assertEquals(null, node.lookupNamespaceURI("xmlns"));
        }
    }

    public void testLookupPrefixWithShadowedUri() {
        TestCase.assertEquals(null, document.lookupPrefix("http://addons"));
        TestCase.assertEquals(null, doctype.lookupPrefix("http://addons"));
        if ((sp) != null) {
            TestCase.assertEquals(null, sp.lookupPrefix("http://addons"));
        }
        if ((png) != null) {
            TestCase.assertEquals(null, png.lookupPrefix("http://addons"));
        }
        TestCase.assertEquals(null, menu.lookupPrefix("http://addons"));
        TestCase.assertEquals("a", item.lookupPrefix("http://addons"));
        TestCase.assertEquals("a", itemXmlns.lookupPrefix("http://addons"));
        TestCase.assertEquals("a", itemXmlnsA.lookupPrefix("http://addons"));
        TestCase.assertEquals("a", name.lookupPrefix("http://addons"));
        TestCase.assertEquals("a", standard.lookupPrefix("http://addons"));
        TestCase.assertEquals("a", deluxe.lookupPrefix("http://addons"));
        TestCase.assertEquals("a", description.lookupPrefix("http://addons"));
        TestCase.assertEquals("a", descriptionText1.lookupPrefix("http://addons"));
        TestCase.assertEquals("a", descriptionText2.lookupPrefix("http://addons"));
        TestCase.assertEquals("a", descriptionText3.lookupPrefix("http://addons"));
        TestCase.assertEquals("a", option1.lookupPrefix("http://addons"));
        TestCase.assertEquals("a", option2.lookupPrefix("http://addons"));
        TestCase.assertEquals("a", option2Reference.lookupPrefix("http://addons"));
        TestCase.assertEquals("a", wafflemaker.lookupPrefix("http://addons"));
        TestCase.assertEquals("a", nutrition.lookupPrefix("http://addons"));
        TestCase.assertEquals(null, vitamins.lookupPrefix("http://addons"));
        TestCase.assertEquals(null, vitaminsXmlnsA.lookupPrefix("http://addons"));
        TestCase.assertEquals(null, comment.lookupPrefix("http://addons"));
        TestCase.assertEquals(null, vitaminc.lookupPrefix("http://addons"));
        TestCase.assertEquals(null, vitamincText.lookupPrefix("http://addons"));
    }

    public void testLookupPrefixWithUnusedUri() {
        for (Node node : allNodes) {
            TestCase.assertEquals(null, node.lookupPrefix("http://unused"));
        }
    }

    public void testLookupPrefixWithNullUri() {
        for (Node node : allNodes) {
            TestCase.assertEquals(null, node.lookupPrefix(null));
        }
    }

    public void testLookupPrefixWithShadowingUri() {
        TestCase.assertEquals(null, document.lookupPrefix("http://usda"));
        TestCase.assertEquals(null, doctype.lookupPrefix("http://usda"));
        if ((sp) != null) {
            TestCase.assertEquals(null, sp.lookupPrefix("http://usda"));
        }
        if ((png) != null) {
            TestCase.assertEquals(null, png.lookupPrefix("http://usda"));
        }
        TestCase.assertEquals(null, menu.lookupPrefix("http://usda"));
        TestCase.assertEquals(null, item.lookupPrefix("http://usda"));
        TestCase.assertEquals(null, itemXmlns.lookupPrefix("http://usda"));
        TestCase.assertEquals(null, itemXmlnsA.lookupPrefix("http://usda"));
        TestCase.assertEquals(null, name.lookupPrefix("http://usda"));
        TestCase.assertEquals(null, standard.lookupPrefix("http://usda"));
        TestCase.assertEquals(null, deluxe.lookupPrefix("http://usda"));
        TestCase.assertEquals(null, description.lookupPrefix("http://usda"));
        TestCase.assertEquals(null, descriptionText1.lookupPrefix("http://usda"));
        TestCase.assertEquals(null, descriptionText2.lookupPrefix("http://usda"));
        TestCase.assertEquals(null, descriptionText3.lookupPrefix("http://usda"));
        TestCase.assertEquals(null, option1.lookupPrefix("http://usda"));
        TestCase.assertEquals(null, option2.lookupPrefix("http://usda"));
        TestCase.assertEquals(null, option2Reference.lookupPrefix("http://usda"));
        TestCase.assertEquals(null, wafflemaker.lookupPrefix("http://usda"));
        TestCase.assertEquals(null, nutrition.lookupPrefix("http://usda"));
        TestCase.assertEquals("a", vitamins.lookupPrefix("http://usda"));
        TestCase.assertEquals("a", vitaminsXmlnsA.lookupPrefix("http://usda"));
        TestCase.assertEquals("a", comment.lookupPrefix("http://usda"));
        TestCase.assertEquals("a", vitaminc.lookupPrefix("http://usda"));
        TestCase.assertEquals("a", vitamincText.lookupPrefix("http://usda"));
    }

    public void testIsDefaultNamespace() {
        TestCase.assertFalse(document.isDefaultNamespace("http://food"));
        TestCase.assertFalse(doctype.isDefaultNamespace("http://food"));
        if ((sp) != null) {
            TestCase.assertFalse(sp.isDefaultNamespace("http://food"));
        }
        if ((png) != null) {
            TestCase.assertFalse(png.isDefaultNamespace("http://food"));
        }
        TestCase.assertFalse(menu.isDefaultNamespace("http://food"));
        TestCase.assertTrue(item.isDefaultNamespace("http://food"));
        TestCase.assertTrue(itemXmlns.isDefaultNamespace("http://food"));
        TestCase.assertTrue(itemXmlnsA.isDefaultNamespace("http://food"));
        TestCase.assertTrue(name.isDefaultNamespace("http://food"));
        TestCase.assertTrue(standard.isDefaultNamespace("http://food"));
        TestCase.assertTrue(deluxe.isDefaultNamespace("http://food"));
        TestCase.assertFalse(description.isDefaultNamespace("http://food"));
        TestCase.assertFalse(descriptionText1.isDefaultNamespace("http://food"));
        TestCase.assertFalse(descriptionText2.isDefaultNamespace("http://food"));
        TestCase.assertFalse(descriptionText3.isDefaultNamespace("http://food"));
        TestCase.assertTrue(option1.isDefaultNamespace("http://food"));
        TestCase.assertTrue(option2.isDefaultNamespace("http://food"));
        TestCase.assertTrue(option2Reference.isDefaultNamespace("http://food"));
        TestCase.assertTrue(wafflemaker.isDefaultNamespace("http://food"));
        TestCase.assertTrue(nutrition.isDefaultNamespace("http://food"));
        TestCase.assertTrue(vitamins.isDefaultNamespace("http://food"));
        TestCase.assertTrue(vitaminsXmlnsA.isDefaultNamespace("http://food"));
        TestCase.assertTrue(comment.isDefaultNamespace("http://food"));
        TestCase.assertTrue(vitaminc.isDefaultNamespace("http://food"));
        TestCase.assertTrue(vitamincText.isDefaultNamespace("http://food"));
    }

    /**
     * Xerces fails this test. It returns false always for entity, notation,
     * document fragment and document type nodes. This contradicts its own
     * behaviour on lookupNamespaceURI(null).
     */
    public void testIsDefaultNamespaceNull_XercesBugs() {
        String message = "isDefaultNamespace() should be consistent with lookupNamespaceURI(null)";
        TestCase.assertTrue(message, doctype.isDefaultNamespace(null));
        if ((sp) != null) {
            TestCase.assertTrue(message, sp.isDefaultNamespace(null));
        }
        if ((png) != null) {
            TestCase.assertTrue(message, png.isDefaultNamespace(null));
        }
    }

    public void testIsDefaultNamespaceNull() {
        TestCase.assertTrue(document.isDefaultNamespace(null));
        TestCase.assertTrue(menu.isDefaultNamespace(null));
        TestCase.assertFalse(item.isDefaultNamespace(null));
        TestCase.assertFalse(itemXmlns.isDefaultNamespace(null));
        TestCase.assertFalse(itemXmlnsA.isDefaultNamespace(null));
        TestCase.assertFalse(name.isDefaultNamespace(null));
        TestCase.assertFalse(standard.isDefaultNamespace(null));
        TestCase.assertFalse(deluxe.isDefaultNamespace(null));
        TestCase.assertFalse(description.isDefaultNamespace(null));
        TestCase.assertFalse(descriptionText1.isDefaultNamespace(null));
        TestCase.assertFalse(descriptionText2.isDefaultNamespace(null));
        TestCase.assertFalse(descriptionText3.isDefaultNamespace(null));
        TestCase.assertFalse(option1.isDefaultNamespace(null));
        TestCase.assertFalse(option2.isDefaultNamespace(null));
        TestCase.assertFalse(option2Reference.isDefaultNamespace(null));
        TestCase.assertFalse(wafflemaker.isDefaultNamespace(null));
        TestCase.assertFalse(nutrition.isDefaultNamespace(null));
        TestCase.assertFalse(vitamins.isDefaultNamespace(null));
        TestCase.assertFalse(vitaminsXmlnsA.isDefaultNamespace(null));
        TestCase.assertFalse(comment.isDefaultNamespace(null));
        TestCase.assertFalse(vitaminc.isDefaultNamespace(null));
        TestCase.assertFalse(vitamincText.isDefaultNamespace(null));
    }

    public void testDoctypeSetTextContent() throws TransformerException {
        String original = domToString(document);
        doctype.setTextContent("foobar");// strangely, this is specified to no-op

        TestCase.assertEquals(original, domToString(document));
    }

    public void testDocumentSetTextContent() throws TransformerException {
        String original = domToString(document);
        document.setTextContent("foobar");// strangely, this is specified to no-op

        TestCase.assertEquals(original, domToString(document));
    }

    public void testElementSetTextContent() throws TransformerException {
        String original = domToString(document);
        nutrition.setTextContent("foobar");
        String expected = original.replaceFirst("(?s)<nutrition>.*</nutrition>", "<nutrition>foobar</nutrition>");
        TestCase.assertEquals(expected, domToString(document));
    }

    public void testEntitySetTextContent() throws TransformerException {
        if ((sp) == null) {
            return;
        }
        try {
            sp.setTextContent("foobar");
            TestCase.fail();// is this implementation-specific behaviour?

        } catch (DOMException e) {
        }
    }

    public void testNotationSetTextContent() throws TransformerException {
        if ((png) == null) {
            return;
        }
        String original = domToString(document);
        png.setTextContent("foobar");
        String expected = original.replace("image/png", "foobar");
        TestCase.assertEquals(expected, domToString(document));
    }

    /**
     * Tests setTextContent on entity references. Although the other tests can
     * act on a parsed DOM, this needs to use a programmatically constructed DOM
     * because the parser may have replaced the entity reference with the
     * corresponding text.
     */
    public void testEntityReferenceSetTextContent() throws TransformerException {
        document = builder.newDocument();
        Element root = document.createElement("menu");
        document.appendChild(root);
        EntityReference entityReference = document.createEntityReference("sp");
        root.appendChild(entityReference);
        try {
            entityReference.setTextContent("Lite Syrup");
            TestCase.fail();
        } catch (DOMException e) {
        }
    }

    public void testAttributeSetTextContent() throws TransformerException {
        String original = domToString(document);
        standard.setTextContent("foobar");
        String expected = original.replace("standard=\"strawberry\"", "standard=\"foobar\"");
        TestCase.assertEquals(expected, domToString(document));
    }

    public void testTextSetTextContent() throws TransformerException {
        String original = domToString(document);
        descriptionText1.setTextContent("foobar");
        String expected = original.replace(">Belgian<!", ">foobar<!");
        TestCase.assertEquals(expected, domToString(document));
    }

    public void testCdataSetTextContent() throws TransformerException {
        String original = domToString(document);
        descriptionText2.setTextContent("foobar");
        String expected = original.replace(" waffles & strawberries (< 5g ", "foobar");
        TestCase.assertEquals(expected, domToString(document));
    }

    public void testProcessingInstructionSetTextContent() throws TransformerException {
        String original = domToString(document);
        wafflemaker.setTextContent("foobar");
        String expected = original.replace(" square shape?>", " foobar?>");
        TestCase.assertEquals(expected, domToString(document));
    }

    public void testCommentSetTextContent() throws TransformerException {
        String original = domToString(document);
        comment.setTextContent("foobar");
        String expected = original.replace("-- add other vitamins? --", "--foobar--");
        TestCase.assertEquals(expected, domToString(document));
    }

    public void testCoreFeature() {
        assertFeature("Core", null);
        assertFeature("Core", "");
        assertFeature("Core", "1.0");
        assertFeature("Core", "2.0");
        assertFeature("Core", "3.0");
        assertFeature("CORE", "3.0");
        assertFeature("+Core", "3.0");
        assertNoFeature("Core", "4.0");
    }

    public void testXmlFeature() {
        assertFeature("XML", null);
        assertFeature("XML", "");
        assertFeature("XML", "1.0");
        assertFeature("XML", "2.0");
        assertFeature("XML", "3.0");
        assertFeature("Xml", "3.0");
        assertFeature("+XML", "3.0");
        assertNoFeature("XML", "4.0");
    }

    /**
     * The RI fails this test.
     * http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407/core.html#Document3-version
     */
    public void testXmlVersionFeature() {
        assertFeature("XMLVersion", null);
        assertFeature("XMLVersion", "");
        assertFeature("XMLVersion", "1.0");
        assertFeature("XMLVersion", "1.1");
        assertFeature("XMLVERSION", "1.1");
        assertFeature("+XMLVersion", "1.1");
        assertNoFeature("XMLVersion", "1.2");
        assertNoFeature("XMLVersion", "2.0");
        assertNoFeature("XMLVersion", "2.0");
    }

    public void testLoadSaveFeature() {
        assertFeature("LS", "3.0");
    }

    public void testElementTraversalFeature() {
        assertFeature("ElementTraversal", "1.0");
    }

    public void testIsSupported() {
        // we don't independently test the features; instead just assume the
        // implementation calls through to hasFeature (as tested above)
        for (Node node : allNodes) {
            TestCase.assertTrue(node.isSupported("XML", null));
            TestCase.assertTrue(node.isSupported("XML", "3.0"));
            TestCase.assertFalse(node.isSupported("foo", null));
            TestCase.assertFalse(node.isSupported("foo", "bar"));
        }
    }

    public void testGetFeature() {
        // we don't independently test the features; instead just assume the
        // implementation calls through to hasFeature (as tested above)
        for (Node node : allNodes) {
            TestCase.assertSame(node, node.getFeature("XML", null));
            TestCase.assertSame(node, node.getFeature("XML", "3.0"));
            TestCase.assertNull(node.getFeature("foo", null));
            TestCase.assertNull(node.getFeature("foo", "bar"));
        }
    }

    public void testNodeEqualsPositive() throws Exception {
        DomTest copy = new DomTest();
        copy.setUp();
        for (int i = 0; i < (allNodes.size()); i++) {
            Node a = allNodes.get(i);
            Node b = copy.allNodes.get(i);
            TestCase.assertTrue(a.isEqualNode(b));
        }
    }

    public void testNodeEqualsNegative() throws Exception {
        for (Node a : allNodes) {
            for (Node b : allNodes) {
                TestCase.assertEquals((a == b), a.isEqualNode(b));
            }
        }
    }

    public void testNodeEqualsNegativeRecursive() throws Exception {
        DomTest copy = new DomTest();
        copy.setUp();
        copy.vitaminc.setTextContent("55%");
        // changing anything about a node should break equality for all parents
        TestCase.assertFalse(document.isEqualNode(copy.document));
        TestCase.assertFalse(menu.isEqualNode(copy.menu));
        TestCase.assertFalse(item.isEqualNode(copy.item));
        TestCase.assertFalse(nutrition.isEqualNode(copy.nutrition));
        TestCase.assertFalse(vitamins.isEqualNode(copy.vitamins));
        TestCase.assertFalse(vitaminc.isEqualNode(copy.vitaminc));
        // but not siblings
        TestCase.assertTrue(doctype.isEqualNode(copy.doctype));
        TestCase.assertTrue(description.isEqualNode(copy.description));
        TestCase.assertTrue(option1.isEqualNode(copy.option1));
    }

    public void testNodeEqualsNull() {
        for (Node node : allNodes) {
            try {
                node.isEqualNode(null);
                TestCase.fail();
            } catch (NullPointerException e) {
            }
        }
    }

    public void testIsElementContentWhitespaceWithoutDeclaration() throws Exception {
        String xml = "<menu>    <item/>   </menu>";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Text text = ((Text) (factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml))).getDocumentElement().getChildNodes().item(0)));
        TestCase.assertFalse(text.isElementContentWhitespace());
    }

    public void testIsElementContentWhitespaceWithDeclaration() throws Exception {
        String xml = "<!DOCTYPE menu [\n" + (("  <!ELEMENT menu (item)*>\n" + "  <!ELEMENT item (#PCDATA)>\n") + "]><menu>    <item/>   </menu>");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Text text = ((Text) (factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml))).getDocumentElement().getChildNodes().item(0)));
        TestCase.assertTrue("This implementation does not recognize element content whitespace", text.isElementContentWhitespace());
    }

    public void testGetWholeTextFirst() {
        TestCase.assertEquals("Belgian waffles & strawberries (< 5g of fat)", descriptionText1.getWholeText());
    }

    public void testGetWholeTextMiddle() {
        TestCase.assertEquals("This implementation doesn't include preceding nodes in getWholeText()", "Belgian waffles & strawberries (< 5g of fat)", descriptionText2.getWholeText());
    }

    public void testGetWholeTextLast() {
        TestCase.assertEquals("This implementation doesn't include preceding nodes in getWholeText()", "Belgian waffles & strawberries (< 5g of fat)", descriptionText3.getWholeText());
    }

    public void testGetWholeTextOnly() {
        TestCase.assertEquals("60%", vitamincText.getWholeText());
    }

    public void testGetWholeTextWithEntityReference() {
        EntityReference spReference = document.createEntityReference("sp");
        description.insertBefore(spReference, descriptionText2);
        TestCase.assertEquals("This implementation doesn't resolve entity references in getWholeText()", "BelgianMaple Syrup waffles & strawberries (< 5g of fat)", descriptionText1.getWholeText());
    }

    public void testReplaceWholeTextFirst() throws TransformerException {
        String original = domToString(document);
        Text replacement = descriptionText1.replaceWholeText("Eggos");
        TestCase.assertSame(descriptionText1, replacement);
        String expected = original.replace("Belgian<![CDATA[ waffles & strawberries (< 5g ]]>of fat)", "Eggos");
        TestCase.assertEquals(expected, domToString(document));
    }

    public void testReplaceWholeTextMiddle() throws TransformerException {
        String original = domToString(document);
        Text replacement = descriptionText2.replaceWholeText("Eggos");
        TestCase.assertSame(descriptionText2, replacement);
        String expected = original.replace("Belgian<![CDATA[ waffles & strawberries (< 5g ]]>of fat)", "<![CDATA[Eggos]]>");
        TestCase.assertEquals("This implementation doesn't remove preceding nodes in replaceWholeText()", expected, domToString(document));
    }

    public void testReplaceWholeTextLast() throws TransformerException {
        String original = domToString(document);
        Text replacement = descriptionText3.replaceWholeText("Eggos");
        TestCase.assertSame(descriptionText3, replacement);
        String expected = original.replace("Belgian<![CDATA[ waffles & strawberries (< 5g ]]>of fat)", "Eggos");
        TestCase.assertEquals("This implementation doesn't remove preceding nodes in replaceWholeText()", expected, domToString(document));
    }

    public void testReplaceWholeTextOnly() throws TransformerException {
        String original = domToString(document);
        Text replacement = vitamincText.replaceWholeText("70%");
        TestCase.assertEquals(Node.TEXT_NODE, replacement.getNodeType());
        TestCase.assertSame(vitamincText, replacement);
        String expected = original.replace("60%", "70%");
        TestCase.assertEquals(expected, domToString(document));
    }

    public void testReplaceWholeTextFirstWithNull() throws TransformerException {
        String original = domToString(document);
        TestCase.assertNull(descriptionText1.replaceWholeText(null));
        String expected = original.replaceFirst(">.*</description>", "/>");
        TestCase.assertEquals("This implementation doesn't remove adjacent nodes in replaceWholeText(null)", expected, domToString(document));
    }

    public void testReplaceWholeTextMiddleWithNull() throws TransformerException {
        String original = domToString(document);
        TestCase.assertNull(descriptionText2.replaceWholeText(null));
        String expected = original.replaceFirst(">.*</description>", "/>");
        TestCase.assertEquals("This implementation doesn't remove adjacent nodes in replaceWholeText(null)", expected, domToString(document));
    }

    public void testReplaceWholeTextLastWithNull() throws TransformerException {
        String original = domToString(document);
        TestCase.assertNull(descriptionText3.replaceWholeText(null));
        String expected = original.replaceFirst(">.*</description>", "/>");
        TestCase.assertEquals("This implementation doesn't remove adjacent nodes in replaceWholeText(null)", expected, domToString(document));
    }

    public void testReplaceWholeTextFirstWithEmptyString() throws TransformerException {
        String original = domToString(document);
        TestCase.assertNull(descriptionText1.replaceWholeText(""));
        String expected = original.replaceFirst(">.*</description>", "/>");
        TestCase.assertEquals("This implementation doesn't remove adjacent nodes in replaceWholeText(null)", expected, domToString(document));
    }

    public void testReplaceWholeTextOnlyWithEmptyString() throws TransformerException {
        String original = domToString(document);
        TestCase.assertNull(vitamincText.replaceWholeText(""));
        String expected = original.replaceFirst(">.*</a:vitaminc>", "/>");
        TestCase.assertEquals(expected, domToString(document));
    }

    public void testUserDataAttachments() {
        Object a = new Object();
        Object b = new Object();
        for (Node node : allNodes) {
            node.setUserData("a", a, null);
            node.setUserData("b", b, null);
        }
        for (Node node : allNodes) {
            TestCase.assertSame(a, node.getUserData("a"));
            TestCase.assertSame(b, node.getUserData("b"));
            TestCase.assertEquals(null, node.getUserData("c"));
            TestCase.assertEquals(null, node.getUserData("A"));
        }
    }

    public void testUserDataRejectsNullKey() {
        try {
            menu.setUserData(null, "apple", null);
            TestCase.fail();
        } catch (NullPointerException e) {
        }
        try {
            menu.getUserData(null);
            TestCase.fail();
        } catch (NullPointerException e) {
        }
    }

    public void testValueOfNewAttributesIsEmptyString() {
        TestCase.assertEquals("", document.createAttribute("bar").getValue());
        TestCase.assertEquals("", document.createAttributeNS("http://foo", "bar").getValue());
    }

    public void testCloneNode() throws Exception {
        document = builder.parse(new InputSource(new StringReader(("<menu " + ("xmlns:f=\"http://food\" xmlns:a=\"http://addons\">" + "<f:item a:standard=\"strawberry\" deluxe=\"yes\">Waffles</f:item></menu>")))));
        name = ((Element) (document.getFirstChild().getFirstChild()));
        Element clonedName = ((Element) (name.cloneNode(true)));
        TestCase.assertNull(clonedName.getParentNode());
        TestCase.assertNull(clonedName.getNextSibling());
        TestCase.assertNull(clonedName.getPreviousSibling());
        TestCase.assertEquals("http://food", clonedName.getNamespaceURI());
        TestCase.assertEquals("f:item", clonedName.getNodeName());
        TestCase.assertEquals("item", clonedName.getLocalName());
        TestCase.assertEquals("http://food", clonedName.getNamespaceURI());
        TestCase.assertEquals("yes", clonedName.getAttribute("deluxe"));
        TestCase.assertEquals("strawberry", clonedName.getAttribute("a:standard"));
        TestCase.assertEquals("strawberry", clonedName.getAttributeNS("http://addons", "standard"));
        TestCase.assertEquals(1, name.getChildNodes().getLength());
        Text clonedChild = ((Text) (clonedName.getFirstChild()));
        TestCase.assertSame(clonedName, clonedChild.getParentNode());
        TestCase.assertNull(clonedChild.getNextSibling());
        TestCase.assertNull(clonedChild.getPreviousSibling());
        TestCase.assertEquals("Waffles", clonedChild.getTextContent());
    }

    /**
     * We can't use the namespace-aware factory method for non-namespace-aware
     * nodes. http://code.google.com/p/android/issues/detail?id=2735
     */
    public void testCloneNodeNotNamespaceAware() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        builder = factory.newDocumentBuilder();
        document = builder.parse(new InputSource(new StringReader(("<menu " + ("xmlns:f=\"http://food\" xmlns:a=\"http://addons\">" + "<f:item a:standard=\"strawberry\" deluxe=\"yes\">Waffles</f:item></menu>")))));
        name = ((Element) (document.getFirstChild().getFirstChild()));
        Element clonedName = ((Element) (name.cloneNode(true)));
        TestCase.assertNull(clonedName.getNamespaceURI());
        TestCase.assertEquals("f:item", clonedName.getNodeName());
        TestCase.assertNull(clonedName.getLocalName());
        TestCase.assertNull(clonedName.getNamespaceURI());
        TestCase.assertEquals("yes", clonedName.getAttribute("deluxe"));
        TestCase.assertEquals("strawberry", clonedName.getAttribute("a:standard"));
        TestCase.assertEquals("", clonedName.getAttributeNS("http://addons", "standard"));
    }

    /**
     * A shallow clone requires cloning the attributes but not the child nodes.
     */
    public void testUserDataHandlerNotifiedOfShallowClones() {
        DomTest.RecordingHandler handler = new DomTest.RecordingHandler();
        name.setUserData("a", "apple", handler);
        name.setUserData("b", "banana", handler);
        standard.setUserData("c", "cat", handler);
        waffles.setUserData("d", "dog", handler);
        Element clonedName = ((Element) (name.cloneNode(false)));
        Attr clonedStandard = clonedName.getAttributeNode("a:standard");
        Set<String> expected = new HashSet<String>();
        expected.add(notification(UserDataHandler.NODE_CLONED, "a", "apple", name, clonedName));
        expected.add(notification(UserDataHandler.NODE_CLONED, "b", "banana", name, clonedName));
        expected.add(notification(UserDataHandler.NODE_CLONED, "c", "cat", standard, clonedStandard));
        TestCase.assertEquals(expected, handler.calls);
    }

    /**
     * A deep clone requires cloning both the attributes and the child nodes.
     */
    public void testUserDataHandlerNotifiedOfDeepClones() {
        DomTest.RecordingHandler handler = new DomTest.RecordingHandler();
        name.setUserData("a", "apple", handler);
        name.setUserData("b", "banana", handler);
        standard.setUserData("c", "cat", handler);
        waffles.setUserData("d", "dog", handler);
        Element clonedName = ((Element) (name.cloneNode(true)));
        Attr clonedStandard = clonedName.getAttributeNode("a:standard");
        Text clonedWaffles = ((Text) (clonedName.getChildNodes().item(0)));
        Set<String> expected = new HashSet<String>();
        expected.add(notification(UserDataHandler.NODE_CLONED, "a", "apple", name, clonedName));
        expected.add(notification(UserDataHandler.NODE_CLONED, "b", "banana", name, clonedName));
        expected.add(notification(UserDataHandler.NODE_CLONED, "c", "cat", standard, clonedStandard));
        expected.add(notification(UserDataHandler.NODE_CLONED, "d", "dog", waffles, clonedWaffles));
        TestCase.assertEquals(expected, handler.calls);
    }

    /**
     * A shallow import requires importing the attributes but not the child
     * nodes.
     */
    public void testUserDataHandlerNotifiedOfShallowImports() {
        DomTest.RecordingHandler handler = new DomTest.RecordingHandler();
        name.setUserData("a", "apple", handler);
        name.setUserData("b", "banana", handler);
        standard.setUserData("c", "cat", handler);
        waffles.setUserData("d", "dog", handler);
        Document newDocument = builder.newDocument();
        Element importedName = ((Element) (newDocument.importNode(name, false)));
        Attr importedStandard = importedName.getAttributeNode("a:standard");
        Set<String> expected = new HashSet<String>();
        expected.add(notification(UserDataHandler.NODE_IMPORTED, "a", "apple", name, importedName));
        expected.add(notification(UserDataHandler.NODE_IMPORTED, "b", "banana", name, importedName));
        expected.add(notification(UserDataHandler.NODE_IMPORTED, "c", "cat", standard, importedStandard));
        TestCase.assertEquals(expected, handler.calls);
    }

    /**
     * A deep import requires cloning both the attributes and the child nodes.
     */
    public void testUserDataHandlerNotifiedOfDeepImports() {
        DomTest.RecordingHandler handler = new DomTest.RecordingHandler();
        name.setUserData("a", "apple", handler);
        name.setUserData("b", "banana", handler);
        standard.setUserData("c", "cat", handler);
        waffles.setUserData("d", "dog", handler);
        Document newDocument = builder.newDocument();
        Element importedName = ((Element) (newDocument.importNode(name, true)));
        Attr importedStandard = importedName.getAttributeNode("a:standard");
        Text importedWaffles = ((Text) (importedName.getChildNodes().item(0)));
        Set<String> expected = new HashSet<String>();
        expected.add(notification(UserDataHandler.NODE_IMPORTED, "a", "apple", name, importedName));
        expected.add(notification(UserDataHandler.NODE_IMPORTED, "b", "banana", name, importedName));
        expected.add(notification(UserDataHandler.NODE_IMPORTED, "c", "cat", standard, importedStandard));
        expected.add(notification(UserDataHandler.NODE_IMPORTED, "d", "dog", waffles, importedWaffles));
        TestCase.assertEquals(expected, handler.calls);
    }

    public void testImportNodeDeep() throws TransformerException {
        String original = domToStringStripElementWhitespace(document);
        Document newDocument = builder.newDocument();
        Element importedItem = ((Element) (newDocument.importNode(item, true)));
        assertDetached(item.getParentNode(), importedItem);
        newDocument.appendChild(importedItem);
        String expected = original.replaceAll("</?menu>", "");
        TestCase.assertEquals(expected, domToStringStripElementWhitespace(newDocument));
    }

    public void testImportNodeShallow() throws TransformerException {
        Document newDocument = builder.newDocument();
        Element importedItem = ((Element) (newDocument.importNode(item, false)));
        assertDetached(item.getParentNode(), importedItem);
        newDocument.appendChild(importedItem);
        TestCase.assertEquals("<item xmlns=\"http://food\" xmlns:a=\"http://addons\"/>", domToString(newDocument));
    }

    public void testNodeAdoption() throws Exception {
        for (Node node : allNodes) {
            if ((((node == (document)) || (node == (doctype))) || (node == (sp))) || (node == (png))) {
                assertNotAdoptable(node);
            } else {
                adoptAndCheck(node);
            }
        }
    }

    public void testAdoptionImmediatelyAfterParsing() throws Exception {
        Document newDocument = builder.newDocument();
        try {
            TestCase.assertSame(name, newDocument.adoptNode(name));
            TestCase.assertSame(newDocument, name.getOwnerDocument());
            TestCase.assertSame(newDocument, standard.getOwnerDocument());
            TestCase.assertSame(newDocument, waffles.getOwnerDocument());
        } catch (Throwable e) {
            AssertionFailedError failure = new AssertionFailedError(("This implementation fails to adopt nodes before the " + "document has been traversed"));
            failure.initCause(e);
            throw failure;
        }
    }

    /**
     * There should be notifications for adopted node itself but none of its
     * children. The DOM spec is vague on this, so we're consistent with the RI.
     */
    public void testUserDataHandlerNotifiedOfOnlyShallowAdoptions() throws Exception {
        /* Force a traversal of the document, otherwise this test may fail for
        an unrelated reason on version 5 of the RI. That behavior is
        exercised by testAdoptionImmediatelyAfterParsing().
         */
        domToString(document);
        DomTest.RecordingHandler handler = new DomTest.RecordingHandler();
        name.setUserData("a", "apple", handler);
        name.setUserData("b", "banana", handler);
        standard.setUserData("c", "cat", handler);
        waffles.setUserData("d", "dog", handler);
        Document newDocument = builder.newDocument();
        TestCase.assertSame(name, newDocument.adoptNode(name));
        TestCase.assertSame(newDocument, name.getOwnerDocument());
        TestCase.assertSame(newDocument, standard.getOwnerDocument());
        TestCase.assertSame(newDocument, waffles.getOwnerDocument());
        Set<String> expected = new HashSet<String>();
        expected.add(notification(UserDataHandler.NODE_ADOPTED, "a", "apple", name, null));
        expected.add(notification(UserDataHandler.NODE_ADOPTED, "b", "banana", name, null));
        TestCase.assertEquals(expected, handler.calls);
    }

    public void testBaseUriRelativeUriResolution() throws Exception {
        File file = File.createTempFile("DomTest.java", "xml");
        File parentFile = file.getParentFile();
        FileWriter writer = new FileWriter(file);
        writer.write(("<a>" + (((((((("  <b xml:base=\"b1/b2\">" + "    <c>") + "      <d xml:base=\"../d1/d2\"><e/></d>") + "    </c>") + "  </b>") + "  <h xml:base=\"h1/h2/\">") + "    <i xml:base=\"../i1/i2\"/>") + "  </h>") + "</a>")));
        writer.close();
        document = builder.parse(file);
        assertFileUriEquals("", file.getPath(), document.getBaseURI());
        assertFileUriEquals("", file.getPath(), document.getDocumentURI());
        Element a = document.getDocumentElement();
        assertFileUriEquals("", file.getPath(), a.getBaseURI());
        String message = "This implementation's getBaseURI() doesn't handle relative URIs";
        Element b = ((Element) (a.getChildNodes().item(1)));
        Element c = ((Element) (b.getChildNodes().item(1)));
        Element d = ((Element) (c.getChildNodes().item(1)));
        Element e = ((Element) (d.getChildNodes().item(0)));
        Element h = ((Element) (a.getChildNodes().item(3)));
        Element i = ((Element) (h.getChildNodes().item(1)));
        assertFileUriEquals(message, (parentFile + "/b1/b2"), b.getBaseURI());
        assertFileUriEquals(message, (parentFile + "/b1/b2"), c.getBaseURI());
        assertFileUriEquals(message, (parentFile + "/d1/d2"), d.getBaseURI());
        assertFileUriEquals(message, (parentFile + "/d1/d2"), e.getBaseURI());
        assertFileUriEquals(message, (parentFile + "/h1/h2/"), h.getBaseURI());
        assertFileUriEquals(message, (parentFile + "/h1/i1/i2"), i.getBaseURI());
    }

    /**
     * According to the <a href="http://www.w3.org/TR/xmlbase/">XML Base</a>
     * spec, fragments (like "#frag" or "") should not be dereferenced.
     */
    public void testBaseUriResolutionWithHashes() throws Exception {
        document = builder.parse(new InputSource(new StringReader(("<a xml:base=\"http://a1/a2\">" + ((((("  <b xml:base=\"b1#b2\"/>" + "  <c xml:base=\"#c1\">") + "    <d xml:base=\"\"/>") + "  </c>") + "  <e xml:base=\"\"/>") + "</a>")))));
        Element a = document.getDocumentElement();
        TestCase.assertEquals("http://a1/a2", a.getBaseURI());
        String message = "This implementation's getBaseURI() doesn't handle " + "relative URIs with hashes";
        Element b = ((Element) (a.getChildNodes().item(1)));
        Element c = ((Element) (a.getChildNodes().item(3)));
        Element d = ((Element) (c.getChildNodes().item(1)));
        Element e = ((Element) (a.getChildNodes().item(5)));
        TestCase.assertEquals(message, "http://a1/b1#b2", b.getBaseURI());
        TestCase.assertEquals(message, "http://a1/a2#c1", c.getBaseURI());
        TestCase.assertEquals(message, "http://a1/a2#c1", d.getBaseURI());
        TestCase.assertEquals(message, "http://a1/a2", e.getBaseURI());
    }

    public void testBaseUriInheritedForProcessingInstructions() {
        document.setDocumentURI("http://d1/d2");
        TestCase.assertEquals("http://d1/d2", wafflemaker.getBaseURI());
    }

    public void testBaseUriInheritedForEntities() {
        if ((sp) == null) {
            return;
        }
        document.setDocumentURI("http://d1/d2");
        TestCase.assertEquals("http://d1/d2", sp.getBaseURI());
    }

    public void testBaseUriNotInheritedForNotations() {
        if ((png) == null) {
            return;
        }
        document.setDocumentURI("http://d1/d2");
        TestCase.assertNull(png.getBaseURI());
    }

    public void testBaseUriNotInheritedForDoctypes() {
        document.setDocumentURI("http://d1/d2");
        TestCase.assertNull(doctype.getBaseURI());
    }

    public void testBaseUriNotInheritedForAttributes() {
        document.setDocumentURI("http://d1/d2");
        TestCase.assertNull(itemXmlns.getBaseURI());
        TestCase.assertNull(itemXmlnsA.getBaseURI());
        TestCase.assertNull(standard.getBaseURI());
        TestCase.assertNull(vitaminsXmlnsA.getBaseURI());
    }

    public void testBaseUriNotInheritedForTextsOrCdatas() {
        document.setDocumentURI("http://d1/d2");
        TestCase.assertNull(descriptionText1.getBaseURI());
        TestCase.assertNull(descriptionText2.getBaseURI());
        TestCase.assertNull(option2Reference.getBaseURI());
    }

    public void testBaseUriNotInheritedForComments() {
        document.setDocumentURI("http://d1/d2");
        TestCase.assertNull(descriptionText1.getBaseURI());
        TestCase.assertNull(descriptionText2.getBaseURI());
    }

    public void testBaseUriNotInheritedForEntityReferences() {
        document.setDocumentURI("http://d1/d2");
        TestCase.assertNull(option2Reference.getBaseURI());
    }

    public void testProgrammaticElementIds() {
        vitaminc.setAttribute("name", "c");
        TestCase.assertFalse(vitaminc.getAttributeNode("name").isId());
        TestCase.assertNull(document.getElementById("c"));
        // set the ID attribute...
        vitaminc.setIdAttribute("name", true);
        TestCase.assertTrue(vitaminc.getAttributeNode("name").isId());
        TestCase.assertSame(vitaminc, document.getElementById("c"));
        // ... and then take it away
        vitaminc.setIdAttribute("name", false);
        TestCase.assertFalse(vitaminc.getAttributeNode("name").isId());
        TestCase.assertNull(document.getElementById("c"));
    }

    public void testMultipleIdsOnOneElement() {
        vitaminc.setAttribute("name", "c");
        vitaminc.setIdAttribute("name", true);
        vitaminc.setAttribute("atc", "a11g");
        vitaminc.setIdAttribute("atc", true);
        TestCase.assertTrue(vitaminc.getAttributeNode("name").isId());
        TestCase.assertTrue(vitaminc.getAttributeNode("atc").isId());
        TestCase.assertSame(vitaminc, document.getElementById("c"));
        TestCase.assertSame(vitaminc, document.getElementById("a11g"));
        TestCase.assertNull(document.getElementById("g"));
    }

    public void testAttributeNamedIdIsNotAnIdByDefault() {
        String message = "This implementation incorrectly interprets the " + "\"id\" attribute as an identifier by default.";
        vitaminc.setAttribute("id", "c");
        TestCase.assertNull(message, document.getElementById("c"));
    }

    public void testElementTypeInfo() {
        TypeInfo typeInfo = description.getSchemaTypeInfo();
        TestCase.assertNull(typeInfo.getTypeName());
        TestCase.assertNull(typeInfo.getTypeNamespace());
        TestCase.assertFalse(typeInfo.isDerivedFrom("x", "y", TypeInfo.DERIVATION_UNION));
    }

    public void testAttributeTypeInfo() {
        TypeInfo typeInfo = standard.getSchemaTypeInfo();
        TestCase.assertNull(typeInfo.getTypeName());
        TestCase.assertNull(typeInfo.getTypeNamespace());
        TestCase.assertFalse(typeInfo.isDerivedFrom("x", "y", TypeInfo.DERIVATION_UNION));
    }

    public void testRenameElement() {
        document.renameNode(description, null, "desc");
        TestCase.assertEquals("desc", description.getTagName());
        TestCase.assertEquals("desc", description.getLocalName());
        TestCase.assertEquals(null, description.getPrefix());
        TestCase.assertEquals(null, description.getNamespaceURI());
    }

    public void testRenameElementWithPrefix() {
        try {
            document.renameNode(description, null, "a:desc");
            TestCase.fail();
        } catch (DOMException e) {
        }
    }

    public void testRenameElementWithNamespace() {
        document.renameNode(description, "http://sales", "desc");
        TestCase.assertEquals("desc", description.getTagName());
        TestCase.assertEquals("desc", description.getLocalName());
        TestCase.assertEquals(null, description.getPrefix());
        TestCase.assertEquals("http://sales", description.getNamespaceURI());
    }

    public void testRenameElementWithPrefixAndNamespace() {
        document.renameNode(description, "http://sales", "a:desc");
        TestCase.assertEquals("a:desc", description.getTagName());
        TestCase.assertEquals("desc", description.getLocalName());
        TestCase.assertEquals("a", description.getPrefix());
        TestCase.assertEquals("http://sales", description.getNamespaceURI());
    }

    public void testRenameAttribute() {
        document.renameNode(deluxe, null, "special");
        TestCase.assertEquals("special", deluxe.getName());
        TestCase.assertEquals("special", deluxe.getLocalName());
        TestCase.assertEquals(null, deluxe.getPrefix());
        TestCase.assertEquals(null, deluxe.getNamespaceURI());
    }

    public void testRenameAttributeWithPrefix() {
        try {
            document.renameNode(deluxe, null, "a:special");
            TestCase.fail();
        } catch (DOMException e) {
        }
    }

    public void testRenameAttributeWithNamespace() {
        document.renameNode(deluxe, "http://sales", "special");
        TestCase.assertEquals("special", deluxe.getName());
        TestCase.assertEquals("special", deluxe.getLocalName());
        TestCase.assertEquals(null, deluxe.getPrefix());
        TestCase.assertEquals("http://sales", deluxe.getNamespaceURI());
    }

    public void testRenameAttributeWithPrefixAndNamespace() {
        document.renameNode(deluxe, "http://sales", "a:special");
        TestCase.assertEquals("a:special", deluxe.getName());
        TestCase.assertEquals("special", deluxe.getLocalName());
        TestCase.assertEquals("a", deluxe.getPrefix());
        TestCase.assertEquals("http://sales", deluxe.getNamespaceURI());
    }

    public void testUserDataHandlerNotifiedOfRenames() {
        DomTest.RecordingHandler handler = new DomTest.RecordingHandler();
        description.setUserData("a", "apple", handler);
        deluxe.setUserData("b", "banana", handler);
        standard.setUserData("c", "cat", handler);
        document.renameNode(deluxe, null, "special");
        document.renameNode(description, null, "desc");
        Set<String> expected = new HashSet<String>();
        expected.add(notification(UserDataHandler.NODE_RENAMED, "a", "apple", description, null));
        expected.add(notification(UserDataHandler.NODE_RENAMED, "b", "banana", deluxe, null));
        TestCase.assertEquals(expected, handler.calls);
    }

    public void testRenameToInvalid() {
        try {
            document.renameNode(description, null, "xmlns:foo");
            TestCase.fail();
        } catch (DOMException e) {
        }
        try {
            document.renameNode(description, null, "xml:foo");
            TestCase.fail();
        } catch (DOMException e) {
        }
        try {
            document.renameNode(deluxe, null, "xmlns");
            TestCase.fail();
        } catch (DOMException e) {
        }
    }

    public void testRenameNodeOtherThanElementOrAttribute() {
        for (Node node : allNodes) {
            if (((node.getNodeType()) == (Node.ATTRIBUTE_NODE)) || ((node.getNodeType()) == (Node.ELEMENT_NODE))) {
                continue;
            }
            try {
                document.renameNode(node, null, "foo");
                TestCase.fail();
            } catch (DOMException e) {
            }
        }
    }

    public void testDocumentDoesNotHaveWhitespaceChildren() throws IOException, SAXException {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>  \n" + ("   <foo/>\n" + "  \n");
        document = builder.parse(new InputSource(new StringReader(xml)));
        TestCase.assertEquals("Document nodes shouldn't have text children", 1, document.getChildNodes().getLength());
    }

    public void testDocumentAddChild() throws IOException, SAXException {
        try {
            document.appendChild(document.createTextNode("   "));
            TestCase.fail("Document nodes shouldn't accept child nodes");
        } catch (DOMException e) {
        }
    }

    public void testIterateForwardsThroughInnerNodeSiblings() throws Exception {
        document = builder.parse(new InputSource(new StringReader("<root><child/><child/></root>")));
        Node root = document.getDocumentElement();
        Node current = root.getChildNodes().item(0);
        while ((current.getNextSibling()) != null) {
            current = current.getNextSibling();
        } 
        TestCase.assertEquals(root.getChildNodes().item(((root.getChildNodes().getLength()) - 1)), current);
    }

    public void testIterateBackwardsThroughInnerNodeSiblings() throws Exception {
        document = builder.parse(new InputSource(new StringReader("<root><child/><child/></root>")));
        Node root = document.getDocumentElement();
        Node current = root.getChildNodes().item(((root.getChildNodes().getLength()) - 1));
        while ((current.getPreviousSibling()) != null) {
            current = current.getPreviousSibling();
        } 
        TestCase.assertEquals(root.getChildNodes().item(0), current);
    }

    public void testIterateForwardsThroughLeafNodeSiblings() throws Exception {
        document = builder.parse(new InputSource(new StringReader("<root> <!-- --> </root>")));
        Node root = document.getDocumentElement();
        Node current = root.getChildNodes().item(0);
        while ((current.getNextSibling()) != null) {
            current = current.getNextSibling();
        } 
        TestCase.assertEquals(root.getChildNodes().item(((root.getChildNodes().getLength()) - 1)), current);
    }

    public void testIterateBackwardsThroughLeafNodeSiblings() throws Exception {
        document = builder.parse(new InputSource(new StringReader("<root> <!-- --> </root>")));
        Node root = document.getDocumentElement();
        Node current = root.getChildNodes().item(((root.getChildNodes().getLength()) - 1));
        while ((current.getPreviousSibling()) != null) {
            current = current.getPreviousSibling();
        } 
        TestCase.assertEquals(root.getChildNodes().item(0), current);
    }

    public void testPublicIdAndSystemId() throws Exception {
        document = builder.parse(new InputSource(new StringReader((" <!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\"" + (" \"http://www.w3.org/TR/html4/strict.dtd\">" + "<html></html>")))));
        doctype = document.getDoctype();
        TestCase.assertEquals("html", doctype.getName());
        TestCase.assertEquals("-//W3C//DTD HTML 4.01//EN", doctype.getPublicId());
        TestCase.assertEquals("http://www.w3.org/TR/html4/strict.dtd", doctype.getSystemId());
    }

    public void testSystemIdOnly() throws Exception {
        document = builder.parse(new InputSource(new StringReader((" <!DOCTYPE html SYSTEM \"http://www.w3.org/TR/html4/strict.dtd\">" + "<html></html>"))));
        doctype = document.getDoctype();
        TestCase.assertEquals("html", doctype.getName());
        TestCase.assertNull(doctype.getPublicId());
        TestCase.assertEquals("http://www.w3.org/TR/html4/strict.dtd", doctype.getSystemId());
    }

    public void testSingleQuotedPublicIdAndSystemId() throws Exception {
        document = builder.parse(new InputSource(new StringReader((" <!DOCTYPE html PUBLIC '-//W3C//DTD HTML 4.01//EN'" + (" 'http://www.w3.org/TR/html4/strict.dtd'>" + "<html></html>")))));
        doctype = document.getDoctype();
        TestCase.assertEquals("html", doctype.getName());
        TestCase.assertEquals("-//W3C//DTD HTML 4.01//EN", doctype.getPublicId());
        TestCase.assertEquals("http://www.w3.org/TR/html4/strict.dtd", doctype.getSystemId());
    }

    public void testGetElementsByTagNameNs() {
        NodeList elements = item.getElementsByTagNameNS("http://addons", "option");
        TestCase.assertEquals(option1, elements.item(0));
        TestCase.assertEquals(option2, elements.item(1));
        TestCase.assertEquals(2, elements.getLength());
    }

    public void testGetElementsByTagNameWithNamespacePrefix() {
        NodeList elements = item.getElementsByTagName("a:option");
        TestCase.assertEquals(option1, elements.item(0));
        TestCase.assertEquals(option2, elements.item(1));
        TestCase.assertEquals(2, elements.getLength());
    }

    // http://code.google.com/p/android/issues/detail?id=17907
    public void testGetElementsByTagNameWithoutNamespacePrefix() {
        NodeList elements = item.getElementsByTagName("nutrition");
        TestCase.assertEquals(nutrition, elements.item(0));
        TestCase.assertEquals(1, elements.getLength());
    }

    public void testGetElementsByTagNameWithWildcard() {
        NodeList elements = item.getElementsByTagName("*");
        TestCase.assertEquals(name, elements.item(0));
        TestCase.assertEquals(description, elements.item(1));
        TestCase.assertEquals(option1, elements.item(2));
        TestCase.assertEquals(option2, elements.item(3));
        TestCase.assertEquals(nutrition, elements.item(4));
        TestCase.assertEquals(vitamins, elements.item(5));
        TestCase.assertEquals(vitaminc, elements.item(6));
        TestCase.assertEquals(7, elements.getLength());
    }

    public void testGetElementsByTagNameNsWithWildcard() {
        NodeList elements = item.getElementsByTagNameNS("*", "*");
        TestCase.assertEquals(name, elements.item(0));
        TestCase.assertEquals(description, elements.item(1));
        TestCase.assertEquals(option1, elements.item(2));
        TestCase.assertEquals(option2, elements.item(3));
        TestCase.assertEquals(nutrition, elements.item(4));
        TestCase.assertEquals(vitamins, elements.item(5));
        TestCase.assertEquals(vitaminc, elements.item(6));
        TestCase.assertEquals(7, elements.getLength());
    }

    /**
     * Documents shouldn't contain document fragments.
     * http://code.google.com/p/android/issues/detail?id=2735
     */
    public void testAddingADocumentFragmentAddsItsChildren() {
        Element a = document.createElement("a");
        Element b = document.createElement("b");
        Element c = document.createElement("c");
        DocumentFragment fragment = document.createDocumentFragment();
        fragment.appendChild(a);
        fragment.appendChild(b);
        fragment.appendChild(c);
        Node returned = menu.appendChild(fragment);
        TestCase.assertSame(fragment, returned);
        NodeList children = menu.getChildNodes();
        TestCase.assertEquals(6, children.getLength());
        TestCase.assertTrue(((children.item(0)) instanceof Text));// whitespace

        TestCase.assertEquals(item, children.item(1));
        TestCase.assertTrue(((children.item(2)) instanceof Text));// whitespace

        TestCase.assertEquals(a, children.item(3));
        TestCase.assertEquals(b, children.item(4));
        TestCase.assertEquals(c, children.item(5));
    }

    public void testReplacingWithADocumentFragmentInsertsItsChildren() {
        Element a = document.createElement("a");
        Element b = document.createElement("b");
        Element c = document.createElement("c");
        DocumentFragment fragment = document.createDocumentFragment();
        fragment.appendChild(a);
        fragment.appendChild(b);
        fragment.appendChild(c);
        Node returned = menu.replaceChild(fragment, item);
        TestCase.assertSame(item, returned);
        NodeList children = menu.getChildNodes();
        TestCase.assertEquals(5, children.getLength());
        TestCase.assertTrue(((children.item(0)) instanceof Text));// whitespace

        TestCase.assertEquals(a, children.item(1));
        TestCase.assertEquals(b, children.item(2));
        TestCase.assertEquals(c, children.item(3));
        TestCase.assertTrue(((children.item(4)) instanceof Text));// whitespace

    }

    public void testCoalescingOffByDefault() {
        TestCase.assertFalse(DocumentBuilderFactory.newInstance().isCoalescing());
    }

    public void testCoalescingOn() throws Exception {
        String xml = "<foo>abc<![CDATA[def]]>ghi</foo>";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setCoalescing(true);
        document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        Element documentElement = document.getDocumentElement();
        Text text = ((Text) (documentElement.getFirstChild()));
        TestCase.assertEquals("abcdefghi", text.getTextContent());
        TestCase.assertNull(text.getNextSibling());
    }

    public void testCoalescingOff() throws Exception {
        String xml = "<foo>abc<![CDATA[def]]>ghi</foo>";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setCoalescing(false);
        document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        Element documentElement = document.getDocumentElement();
        Text abc = ((Text) (documentElement.getFirstChild()));
        TestCase.assertEquals("abc", abc.getTextContent());
        CDATASection def = ((CDATASection) (abc.getNextSibling()));
        TestCase.assertEquals("def", def.getTextContent());
        Text ghi = ((Text) (def.getNextSibling()));
        TestCase.assertEquals("ghi", ghi.getTextContent());
        TestCase.assertNull(ghi.getNextSibling());
    }

    public void testExpandingEntityReferencesOnByDefault() {
        TestCase.assertTrue(DocumentBuilderFactory.newInstance().isExpandEntityReferences());
    }

    public void testExpandingEntityReferencesOn() throws Exception {
        String xml = "<!DOCTYPE foo [ <!ENTITY def \"DEF\"> ]>" + "<foo>abc&def;ghi</foo>";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setExpandEntityReferences(true);
        document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        Element documentElement = document.getDocumentElement();
        Text text = ((Text) (documentElement.getFirstChild()));
        TestCase.assertEquals("This implementation doesn't expand entity references", "abcDEFghi", text.getTextContent());
        TestCase.assertNull(text.getNextSibling());
    }

    public void testExpandingEntityReferencesOff() throws Exception {
        String xml = "<!DOCTYPE foo [ <!ENTITY def \"DEF\"> ]>" + "<foo>abc&def;ghi</foo>";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setExpandEntityReferences(false);
        document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        Element documentElement = document.getDocumentElement();
        Text abc = ((Text) (documentElement.getFirstChild()));
        TestCase.assertEquals("abc", abc.getTextContent());
        EntityReference def = ((EntityReference) (abc.getNextSibling()));
        TestCase.assertEquals("def", def.getNodeName());
        Text ghi = ((Text) (def.getNextSibling()));
        TestCase.assertNull(ghi.getNextSibling());
        /* We expect the entity reference to contain one child Text node "DEF".
        The RI's entity reference contains no children. Instead it stashes
        "DEF" in the next sibling node.
         */
        TestCase.assertEquals("Expected text value only and no expanded entity data", "ghi", ghi.getTextContent());
        NodeList defChildren = def.getChildNodes();
        TestCase.assertEquals("This implementation doesn't include children in entity references", 1, defChildren.getLength());
        TestCase.assertEquals("DEF", defChildren.item(0).getTextContent());
    }

    /**
     * Predefined entities should always be expanded.
     * https://code.google.com/p/android/issues/detail?id=225
     */
    public void testExpandingEntityReferencesOffDoesNotImpactPredefinedEntities() throws Exception {
        String xml = "<foo>abc&amp;def</foo>";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setExpandEntityReferences(false);
        document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        Element documentElement = document.getDocumentElement();
        Text text = ((Text) (documentElement.getFirstChild()));
        TestCase.assertEquals("abc&def", text.getTextContent());
        TestCase.assertNull(text.getNextSibling());
    }

    public void testExpandingEntityReferencesOffDoesNotImpactCharacterEntities() throws Exception {
        String xml = "<foo>abc&#38;def&#x26;ghi</foo>";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setExpandEntityReferences(false);
        document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        Element documentElement = document.getDocumentElement();
        Text text = ((Text) (documentElement.getFirstChild()));
        TestCase.assertEquals("abc&def&ghi", text.getTextContent());
        TestCase.assertNull(text.getNextSibling());
    }

    // http://code.google.com/p/android/issues/detail?id=24530
    public void testInsertBefore() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document d = factory.newDocumentBuilder().newDocument();
        d.appendChild(d.createElement("root"));
        d.getFirstChild().insertBefore(d.createElement("foo"), null);
        TestCase.assertEquals("foo", d.getFirstChild().getFirstChild().getNodeName());
        TestCase.assertEquals("foo", d.getFirstChild().getLastChild().getNodeName());
        d.getFirstChild().insertBefore(d.createElement("bar"), null);
        TestCase.assertEquals("foo", d.getFirstChild().getFirstChild().getNodeName());
        TestCase.assertEquals("bar", d.getFirstChild().getLastChild().getNodeName());
    }

    public void testBomAndByteInput() throws Exception {
        byte[] xml = new byte[]{ ((byte) (239)), ((byte) (187)), ((byte) (191)), '<', 'i', 'n', 'p', 'u', 't', '/', '>' };
        document = builder.parse(new InputSource(new ByteArrayInputStream(xml)));
        TestCase.assertEquals("input", document.getDocumentElement().getNodeName());
    }

    public void testBomAndByteInputWithExplicitCharset() throws Exception {
        byte[] xml = new byte[]{ ((byte) (239)), ((byte) (187)), ((byte) (191)), '<', 'i', 'n', 'p', 'u', 't', '/', '>' };
        InputSource inputSource = new InputSource(new ByteArrayInputStream(xml));
        inputSource.setEncoding("UTF-8");
        document = builder.parse(inputSource);
        TestCase.assertEquals("input", document.getDocumentElement().getNodeName());
    }

    public void testBomAndCharacterInput() throws Exception {
        InputSource inputSource = new InputSource(new StringReader("\ufeff<input/>"));
        inputSource.setEncoding("UTF-8");
        try {
            builder.parse(inputSource);
            TestCase.fail();
        } catch (SAXException expected) {
        }
    }

    private class RecordingHandler implements UserDataHandler {
        final Set<String> calls = new HashSet<String>();

        public void handle(short operation, String key, Object data, Node src, Node dst) {
            calls.add(notification(operation, key, data, src, dst));
        }
    }
}

