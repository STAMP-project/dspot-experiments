/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.xml;


import XmlLanguageModule.NAME;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for the {@link XmlParser}.
 */
public class XmlParserTest {
    private static final String XML_TEST = "<?xml version=\"1.0\"?>\n" + ((((((((((((((((("<!DOCTYPE rootElement\n" + "[\n") + "<!ELEMENT rootElement (child1,child2)>\n") + "<!ELEMENT child1 (#PCDATA)>\n") + "<!ATTLIST child1 test CDATA #REQUIRED>\n") + "<!ELEMENT child2 (#PCDATA)>\n") + "\n") + "<!ENTITY pmd \"Copyright: PMD\">\n") + "]\n") + ">\n") + "<rootElement>\n") + "    <!-- that\'s a comment -->\n") + "    <child1 test=\"1\">entity: &pmd;\n") + "    </child1>\n") + "    <child2>\n") + "      <![CDATA[ cdata section ]]>\n") + "    </child2>\n") + "</rootElement>");

    private static final String XML_NAMESPACE_TEST = "<?xml version=\"1.0\"?>\n" + ((((((("<pmd:rootElement xmlns:pmd=\"http://pmd.sf.net\">\n" + "    <!-- that\'s a comment -->\n") + "    <pmd:child1 test=\"1\">entity: &amp;\n") + "    </pmd:child1>\n") + "    <pmd:child2>\n") + "      <![CDATA[ cdata section ]]>\n") + "    </pmd:child2>\n") + "</pmd:rootElement>");

    private static final String XML_INVALID_WITH_DTD = "<?xml version=\"1.0\"?>\n" + (((((((("<!DOCTYPE rootElement\n" + "[\n") + "<!ELEMENT rootElement (child)>\n") + "<!ELEMENT child (#PCDATA)>\n") + "]\n") + ">\n") + "<rootElement>\n") + "  <invalidChild></invalidChild>\n") + "</rootElement>");

    /**
     * See bug #1054: XML Rules ever report a line -1 and not the line/column
     * where the error occurs
     *
     * @throws Exception
     * 		any error
     */
    @Test
    public void testLineNumbers() throws Exception {
        LanguageVersionHandler xmlVersionHandler = LanguageRegistry.getLanguage(NAME).getDefaultVersion().getLanguageVersionHandler();
        Parser parser = xmlVersionHandler.getParser(xmlVersionHandler.getDefaultParserOptions());
        Node document = parser.parse(null, new StringReader(XmlParserTest.XML_TEST));
        assertNode(document, "document", 2);
        assertLineNumbers(document, 1, 1, 19, 14);
        Node dtdElement = document.jjtGetChild(0);
        assertNode(dtdElement, "rootElement", 0);
        assertLineNumbers(dtdElement, 2, 1, 11, 1);
        Node rootElement = document.jjtGetChild(1);
        assertNode(rootElement, "rootElement", 7);
        assertLineNumbers(rootElement, 12, 1, 19, 14);
        assertTextNode(rootElement.jjtGetChild(0), "\\n    ");
        assertLineNumbers(rootElement.jjtGetChild(0), 12, 14, 13, 4);
        assertNode(rootElement.jjtGetChild(1), "comment", 0);
        assertLineNumbers(rootElement.jjtGetChild(1), 13, 5, 13, 29);
        assertTextNode(rootElement.jjtGetChild(2), "\\n    ");
        assertLineNumbers(rootElement.jjtGetChild(2), 13, 30, 14, 4);
        Node child1 = rootElement.jjtGetChild(3);
        assertNode(child1, "child1", 1, "test", "1");
        assertLineNumbers(child1, 14, 5, 15, 13);
        assertTextNode(child1.jjtGetChild(0), "entity: Copyright: PMD\\n    ");
        assertLineNumbers(child1.jjtGetChild(0), 14, 22, 15, 4);
        assertTextNode(rootElement.jjtGetChild(4), "\\n    ");
        assertLineNumbers(rootElement.jjtGetChild(4), 15, 14, 16, 4);
        Node child2 = rootElement.jjtGetChild(5);
        assertNode(child2, "child2", 3);
        assertLineNumbers(child2, 16, 5, 18, 13);
        assertTextNode(child2.jjtGetChild(0), "\\n      ");
        assertLineNumbers(child2.jjtGetChild(0), 16, 13, 17, 6);
        assertTextNode(child2.jjtGetChild(1), " cdata section ", "cdata-section");
        assertLineNumbers(child2.jjtGetChild(1), 17, 7, 17, 33);
        assertTextNode(child2.jjtGetChild(2), "\\n    ");
        assertLineNumbers(child2.jjtGetChild(2), 17, 34, 18, 4);
        assertTextNode(rootElement.jjtGetChild(6), "\\n");
        assertLineNumbers(rootElement.jjtGetChild(6), 18, 14, 18, 14);
    }

    /**
     * Verifies the default parsing behavior of the XML parser.
     */
    @Test
    public void testDefaultParsing() {
        LanguageVersionHandler xmlVersionHandler = LanguageRegistry.getLanguage(NAME).getDefaultVersion().getLanguageVersionHandler();
        Parser parser = xmlVersionHandler.getParser(xmlVersionHandler.getDefaultParserOptions());
        Node document = parser.parse(null, new StringReader(XmlParserTest.XML_TEST));
        assertNode(document, "document", 2);
        Node dtdElement = document.jjtGetChild(0);
        assertNode(dtdElement, "rootElement", 0);
        Node rootElement = document.jjtGetChild(1);
        assertNode(rootElement, "rootElement", 7);
        assertTextNode(rootElement.jjtGetChild(0), "\\n    ");
        assertNode(rootElement.jjtGetChild(1), "comment", 0);
        assertTextNode(rootElement.jjtGetChild(2), "\\n    ");
        Node child1 = rootElement.jjtGetChild(3);
        assertNode(child1, "child1", 1, "test", "1");
        assertTextNode(child1.jjtGetChild(0), "entity: Copyright: PMD\\n    ");
        assertTextNode(rootElement.jjtGetChild(4), "\\n    ");
        Node child2 = rootElement.jjtGetChild(5);
        assertNode(child2, "child2", 3);
        assertTextNode(child2.jjtGetChild(0), "\\n      ");
        assertTextNode(child2.jjtGetChild(1), " cdata section ", "cdata-section");
        assertTextNode(child2.jjtGetChild(2), "\\n    ");
        assertTextNode(rootElement.jjtGetChild(6), "\\n");
    }

    /**
     * Verifies the parsing behavior of the XML parser with coalescing enabled.
     */
    @Test
    public void testParsingCoalescingEnabled() {
        LanguageVersionHandler xmlVersionHandler = LanguageRegistry.getLanguage(NAME).getDefaultVersion().getLanguageVersionHandler();
        XmlParserOptions parserOptions = new XmlParserOptions();
        parserOptions.setCoalescing(true);
        Parser parser = xmlVersionHandler.getParser(parserOptions);
        Node document = parser.parse(null, new StringReader(XmlParserTest.XML_TEST));
        assertNode(document, "document", 2);
        Node dtdElement = document.jjtGetChild(0);
        assertNode(dtdElement, "rootElement", 0);
        Node rootElement = document.jjtGetChild(1);
        assertNode(rootElement, "rootElement", 7);
        assertTextNode(rootElement.jjtGetChild(0), "\\n    ");
        assertNode(rootElement.jjtGetChild(1), "comment", 0);
        assertTextNode(rootElement.jjtGetChild(2), "\\n    ");
        Node child1 = rootElement.jjtGetChild(3);
        assertNode(child1, "child1", 1, "test", "1");
        assertTextNode(child1.jjtGetChild(0), "entity: Copyright: PMD\\n    ");
        assertTextNode(rootElement.jjtGetChild(4), "\\n    ");
        Node child2 = rootElement.jjtGetChild(5);
        assertNode(child2, "child2", 1);
        assertTextNode(child2.jjtGetChild(0), "\\n       cdata section \\n    ");
        assertTextNode(rootElement.jjtGetChild(6), "\\n");
    }

    /**
     * Verifies the parsing behavior of the XML parser if entities are not
     * expanded.
     */
    @Test
    public void testParsingDoNotExpandEntities() {
        LanguageVersionHandler xmlVersionHandler = LanguageRegistry.getLanguage(NAME).getDefaultVersion().getLanguageVersionHandler();
        XmlParserOptions parserOptions = new XmlParserOptions();
        parserOptions.setExpandEntityReferences(false);
        Parser parser = xmlVersionHandler.getParser(parserOptions);
        Node document = parser.parse(null, new StringReader(XmlParserTest.XML_TEST));
        assertNode(document, "document", 2);
        Node dtdElement = document.jjtGetChild(0);
        assertNode(dtdElement, "rootElement", 0);
        Node rootElement = document.jjtGetChild(1);
        assertNode(rootElement, "rootElement", 7);
        assertTextNode(rootElement.jjtGetChild(0), "\\n    ");
        assertNode(rootElement.jjtGetChild(1), "comment", 0);
        assertTextNode(rootElement.jjtGetChild(2), "\\n    ");
        Node child1 = rootElement.jjtGetChild(3);
        assertNode(child1, "child1", 3, "test", "1");
        assertTextNode(child1.jjtGetChild(0), "entity: ");
        assertNode(child1.jjtGetChild(1), "pmd", 0);
        assertTextNode(child1.jjtGetChild(2), "Copyright: PMD\\n    ");
        assertTextNode(rootElement.jjtGetChild(4), "\\n    ");
        Node child2 = rootElement.jjtGetChild(5);
        assertNode(child2, "child2", 3);
        assertTextNode(child2.jjtGetChild(0), "\\n      ");
        assertTextNode(child2.jjtGetChild(1), " cdata section ", "cdata-section");
        assertTextNode(child2.jjtGetChild(2), "\\n    ");
        assertTextNode(rootElement.jjtGetChild(6), "\\n");
    }

    /**
     * Verifies the parsing behavior of the XML parser if ignoring comments.
     */
    @Test
    public void testParsingIgnoreComments() {
        LanguageVersionHandler xmlVersionHandler = LanguageRegistry.getLanguage(NAME).getDefaultVersion().getLanguageVersionHandler();
        XmlParserOptions parserOptions = new XmlParserOptions();
        parserOptions.setIgnoringComments(true);
        Parser parser = xmlVersionHandler.getParser(parserOptions);
        Node document = parser.parse(null, new StringReader(XmlParserTest.XML_TEST));
        assertNode(document, "document", 2);
        Node dtdElement = document.jjtGetChild(0);
        assertNode(dtdElement, "rootElement", 0);
        Node rootElement = document.jjtGetChild(1);
        assertNode(rootElement, "rootElement", 5);
        assertTextNode(rootElement.jjtGetChild(0), "\\n    \\n    ");
        Node child1 = rootElement.jjtGetChild(1);
        assertNode(child1, "child1", 1, "test", "1");
        assertTextNode(child1.jjtGetChild(0), "entity: Copyright: PMD\\n    ");
        assertTextNode(rootElement.jjtGetChild(2), "\\n    ");
        Node child2 = rootElement.jjtGetChild(3);
        assertNode(child2, "child2", 3);
        assertTextNode(child2.jjtGetChild(0), "\\n      ");
        assertTextNode(child2.jjtGetChild(1), " cdata section ", "cdata-section");
        assertTextNode(child2.jjtGetChild(2), "\\n    ");
        assertTextNode(rootElement.jjtGetChild(4), "\\n");
    }

    /**
     * Verifies the parsing behavior of the XML parser if ignoring whitespaces
     * in elements.
     */
    @Test
    public void testParsingIgnoreElementContentWhitespace() {
        LanguageVersionHandler xmlVersionHandler = LanguageRegistry.getLanguage(NAME).getDefaultVersion().getLanguageVersionHandler();
        XmlParserOptions parserOptions = new XmlParserOptions();
        parserOptions.setIgnoringElementContentWhitespace(true);
        Parser parser = xmlVersionHandler.getParser(parserOptions);
        Node document = parser.parse(null, new StringReader(XmlParserTest.XML_TEST));
        assertNode(document, "document", 2);
        Node dtdElement = document.jjtGetChild(0);
        assertNode(dtdElement, "rootElement", 0);
        Node rootElement = document.jjtGetChild(1);
        assertNode(rootElement, "rootElement", 3);
        assertNode(rootElement.jjtGetChild(0), "comment", 0);
        Node child1 = rootElement.jjtGetChild(1);
        assertNode(child1, "child1", 1, "test", "1");
        assertTextNode(child1.jjtGetChild(0), "entity: Copyright: PMD\\n    ");
        Node child2 = rootElement.jjtGetChild(2);
        assertNode(child2, "child2", 3);
        assertTextNode(child2.jjtGetChild(0), "\\n      ");
        assertTextNode(child2.jjtGetChild(1), " cdata section ", "cdata-section");
        assertTextNode(child2.jjtGetChild(2), "\\n    ");
    }

    /**
     * Verifies the default parsing behavior of the XML parser with namespaces.
     */
    @Test
    public void testDefaultParsingNamespaces() {
        LanguageVersionHandler xmlVersionHandler = LanguageRegistry.getLanguage(NAME).getDefaultVersion().getLanguageVersionHandler();
        Parser parser = xmlVersionHandler.getParser(xmlVersionHandler.getDefaultParserOptions());
        Node document = parser.parse(null, new StringReader(XmlParserTest.XML_NAMESPACE_TEST));
        assertNode(document, "document", 1);
        Node rootElement = document.jjtGetChild(0);
        assertNode(rootElement, "pmd:rootElement", 7, "xmlns:pmd", "http://pmd.sf.net");
        Assert.assertEquals("http://pmd.sf.net", getNode().getNamespaceURI());
        Assert.assertEquals("pmd", getNode().getPrefix());
        Assert.assertEquals("rootElement", getNode().getLocalName());
        Assert.assertEquals("pmd:rootElement", getNode().getNodeName());
        assertTextNode(rootElement.jjtGetChild(0), "\\n    ");
        assertNode(rootElement.jjtGetChild(1), "comment", 0);
        assertTextNode(rootElement.jjtGetChild(2), "\\n    ");
        Node child1 = rootElement.jjtGetChild(3);
        assertNode(child1, "pmd:child1", 1, "test", "1");
        assertTextNode(child1.jjtGetChild(0), "entity: &\\n    ");
        assertTextNode(rootElement.jjtGetChild(4), "\\n    ");
        Node child2 = rootElement.jjtGetChild(5);
        assertNode(child2, "pmd:child2", 3);
        assertTextNode(child2.jjtGetChild(0), "\\n      ");
        assertTextNode(child2.jjtGetChild(1), " cdata section ", "cdata-section");
        assertTextNode(child2.jjtGetChild(2), "\\n    ");
        assertTextNode(rootElement.jjtGetChild(6), "\\n");
    }

    /**
     * Verifies the default parsing behavior of the XML parser with namespaces
     * but not namespace aware.
     */
    @Test
    public void testParsingNotNamespaceAware() {
        LanguageVersionHandler xmlVersionHandler = LanguageRegistry.getLanguage(NAME).getDefaultVersion().getLanguageVersionHandler();
        XmlParserOptions parserOptions = new XmlParserOptions();
        parserOptions.setNamespaceAware(false);
        Parser parser = xmlVersionHandler.getParser(parserOptions);
        Node document = parser.parse(null, new StringReader(XmlParserTest.XML_NAMESPACE_TEST));
        assertNode(document, "document", 1);
        Node rootElement = document.jjtGetChild(0);
        assertNode(rootElement, "pmd:rootElement", 7, "xmlns:pmd", "http://pmd.sf.net");
        Assert.assertNull(getNode().getNamespaceURI());
        Assert.assertNull(getNode().getPrefix());
        Assert.assertNull(getNode().getLocalName());
        Assert.assertEquals("pmd:rootElement", getNode().getNodeName());
        assertTextNode(rootElement.jjtGetChild(0), "\\n    ");
        assertNode(rootElement.jjtGetChild(1), "comment", 0);
        assertTextNode(rootElement.jjtGetChild(2), "\\n    ");
        Node child1 = rootElement.jjtGetChild(3);
        assertNode(child1, "pmd:child1", 1, "test", "1");
        assertTextNode(child1.jjtGetChild(0), "entity: &\\n    ");
        assertTextNode(rootElement.jjtGetChild(4), "\\n    ");
        Node child2 = rootElement.jjtGetChild(5);
        assertNode(child2, "pmd:child2", 3);
        assertTextNode(child2.jjtGetChild(0), "\\n      ");
        assertTextNode(child2.jjtGetChild(1), " cdata section ", "cdata-section");
        assertTextNode(child2.jjtGetChild(2), "\\n    ");
        assertTextNode(rootElement.jjtGetChild(6), "\\n");
    }

    /**
     * Verifies the parsing behavior of the XML parser with validation on.
     *
     * @throws UnsupportedEncodingException
     * 		error
     */
    @Test
    public void testParsingWithValidation() throws UnsupportedEncodingException {
        LanguageVersionHandler xmlVersionHandler = LanguageRegistry.getLanguage(NAME).getDefaultVersion().getLanguageVersionHandler();
        XmlParserOptions parserOptions = new XmlParserOptions();
        parserOptions.setValidating(true);
        Parser parser = xmlVersionHandler.getParser(parserOptions);
        PrintStream oldErr = System.err;
        Locale oldLocale = Locale.getDefault();
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            System.setErr(new PrintStream(bos));
            Locale.setDefault(Locale.ENGLISH);
            Node document = parser.parse(null, new StringReader(XmlParserTest.XML_INVALID_WITH_DTD));
            Assert.assertNotNull(document);
            String output = bos.toString("UTF-8");
            Assert.assertTrue(output.contains("Element type \"invalidChild\" must be declared."));
            Assert.assertTrue(output.contains("The content of element type \"rootElement\" must match \"(child)\"."));
            Assert.assertEquals(2, document.jjtGetNumChildren());
            Assert.assertEquals("invalidChild", String.valueOf(document.jjtGetChild(1).jjtGetChild(1)));
        } finally {
            System.setErr(oldErr);
            Locale.setDefault(oldLocale);
        }
    }

    @Test
    public void testWithProcessingInstructions() {
        String xml = "<?xml version=\"1.0\"?><?mypi?><!DOCTYPE testDoc [<!ENTITY myentity \"e\">]><!--Comment--><foo abc=\"abc\"><bar>TEXT</bar><![CDATA[cdata!]]>&gt;&myentity;&lt;</foo>";
        LanguageVersionHandler xmlVersionHandler = LanguageRegistry.getLanguage(NAME).getDefaultVersion().getLanguageVersionHandler();
        XmlParserOptions options = ((XmlParserOptions) (xmlVersionHandler.getDefaultParserOptions()));
        options.setExpandEntityReferences(false);
        Parser parser = xmlVersionHandler.getParser(options);
        Node document = parser.parse(null, new StringReader(xml));
        Assert.assertNotNull(document);
        assertNode(document.jjtGetChild(0), "mypi", 0);
        assertLineNumbers(document.jjtGetChild(0), 1, 22, 1, 29);
    }

    @Test
    public void testBug1518() throws Exception {
        String xml = IOUtils.toString(XmlParserTest.class.getResourceAsStream("parsertests/bug1518.xml"), StandardCharsets.UTF_8);
        Node document = parseXml(xml);
        Assert.assertNotNull(document);
    }

    @Test
    public void testAutoclosingElementLength() {
        final String xml = "<elementName att1='foo' att2='bar' att3='other' />";
        assertLineNumbers(parseXml(xml), 1, 1, 1, xml.length());
    }
}

