/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.xml;


import XmlParserOptions.COALESCING_DESCRIPTOR;
import XmlParserOptions.EXPAND_ENTITY_REFERENCES_DESCRIPTOR;
import XmlParserOptions.IGNORING_COMMENTS_DESCRIPTOR;
import XmlParserOptions.IGNORING_ELEMENT_CONTENT_WHITESPACE_DESCRIPTOR;
import XmlParserOptions.NAMESPACE_AWARE_DESCRIPTOR;
import XmlParserOptions.VALIDATING_DESCRIPTOR;
import XmlParserOptions.XINCLUDE_AWARE_DESCRIPTOR;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ParserOptionsTest;
import net.sourceforge.pmd.lang.xml.rule.AbstractXmlRule;
import net.sourceforge.pmd.properties.BooleanProperty;
import org.junit.Assert;
import org.junit.Test;

import static XmlParserOptions.COALESCING_DESCRIPTOR;
import static XmlParserOptions.EXPAND_ENTITY_REFERENCES_DESCRIPTOR;
import static XmlParserOptions.IGNORING_COMMENTS_DESCRIPTOR;
import static XmlParserOptions.IGNORING_ELEMENT_CONTENT_WHITESPACE_DESCRIPTOR;
import static XmlParserOptions.NAMESPACE_AWARE_DESCRIPTOR;
import static XmlParserOptions.VALIDATING_DESCRIPTOR;
import static XmlParserOptions.XINCLUDE_AWARE_DESCRIPTOR;


public class XmlParserOptionsTest {
    @Test
    public void testDefaults() throws Exception {
        XmlParserOptions options = new XmlParserOptions();
        Assert.assertFalse(options.isCoalescing());
        Assert.assertTrue(options.isExpandEntityReferences());
        Assert.assertFalse(options.isIgnoringComments());
        Assert.assertFalse(options.isIgnoringElementContentWhitespace());
        Assert.assertTrue(options.isNamespaceAware());
        Assert.assertFalse(options.isValidating());
        Assert.assertFalse(options.isXincludeAware());
        XmlParserOptionsTest.MyRule rule = new XmlParserOptionsTest.MyRule();
        options = ((XmlParserOptions) (getParserOptions()));
        Assert.assertFalse(options.isCoalescing());
        Assert.assertTrue(options.isExpandEntityReferences());
        Assert.assertFalse(options.isIgnoringComments());
        Assert.assertFalse(options.isIgnoringElementContentWhitespace());
        Assert.assertTrue(options.isNamespaceAware());
        Assert.assertFalse(options.isValidating());
        Assert.assertFalse(options.isXincludeAware());
    }

    @Test
    public void testConstructor() throws Exception {
        XmlParserOptionsTest.MyRule rule = new XmlParserOptionsTest.MyRule();
        rule.setProperty(COALESCING_DESCRIPTOR, true);
        Assert.assertTrue(isCoalescing());
        rule.setProperty(COALESCING_DESCRIPTOR, false);
        Assert.assertFalse(isCoalescing());
        rule.setProperty(EXPAND_ENTITY_REFERENCES_DESCRIPTOR, true);
        Assert.assertTrue(isExpandEntityReferences());
        rule.setProperty(EXPAND_ENTITY_REFERENCES_DESCRIPTOR, false);
        Assert.assertFalse(isExpandEntityReferences());
        rule.setProperty(IGNORING_COMMENTS_DESCRIPTOR, true);
        Assert.assertTrue(isIgnoringComments());
        rule.setProperty(IGNORING_COMMENTS_DESCRIPTOR, false);
        Assert.assertFalse(isIgnoringComments());
        rule.setProperty(IGNORING_ELEMENT_CONTENT_WHITESPACE_DESCRIPTOR, true);
        Assert.assertTrue(isIgnoringElementContentWhitespace());
        rule.setProperty(IGNORING_ELEMENT_CONTENT_WHITESPACE_DESCRIPTOR, false);
        Assert.assertFalse(isIgnoringElementContentWhitespace());
        rule.setProperty(NAMESPACE_AWARE_DESCRIPTOR, true);
        Assert.assertTrue(isNamespaceAware());
        rule.setProperty(NAMESPACE_AWARE_DESCRIPTOR, false);
        Assert.assertFalse(isNamespaceAware());
        rule.setProperty(VALIDATING_DESCRIPTOR, true);
        Assert.assertTrue(isValidating());
        rule.setProperty(VALIDATING_DESCRIPTOR, false);
        Assert.assertFalse(isValidating());
        rule.setProperty(XINCLUDE_AWARE_DESCRIPTOR, true);
        Assert.assertTrue(isXincludeAware());
        rule.setProperty(XINCLUDE_AWARE_DESCRIPTOR, false);
        Assert.assertFalse(isXincludeAware());
    }

    @Test
    public void testSetters() {
        XmlParserOptions options = new XmlParserOptions();
        options.setSuppressMarker("foo");
        Assert.assertEquals("foo", options.getSuppressMarker());
        options.setSuppressMarker(null);
        Assert.assertNull(options.getSuppressMarker());
        options.setCoalescing(true);
        Assert.assertTrue(options.isCoalescing());
        options.setCoalescing(false);
        Assert.assertFalse(options.isCoalescing());
        options.setExpandEntityReferences(true);
        Assert.assertTrue(options.isExpandEntityReferences());
        options.setExpandEntityReferences(false);
        Assert.assertFalse(options.isExpandEntityReferences());
        options.setIgnoringComments(true);
        Assert.assertTrue(options.isIgnoringComments());
        options.setIgnoringComments(false);
        Assert.assertFalse(options.isIgnoringComments());
        options.setIgnoringElementContentWhitespace(true);
        Assert.assertTrue(options.isIgnoringElementContentWhitespace());
        options.setIgnoringElementContentWhitespace(false);
        Assert.assertFalse(options.isIgnoringElementContentWhitespace());
        options.setNamespaceAware(true);
        Assert.assertTrue(options.isNamespaceAware());
        options.setNamespaceAware(false);
        Assert.assertFalse(options.isNamespaceAware());
        options.setValidating(true);
        Assert.assertTrue(options.isValidating());
        options.setValidating(false);
        Assert.assertFalse(options.isValidating());
        options.setXincludeAware(true);
        Assert.assertTrue(options.isXincludeAware());
        options.setXincludeAware(false);
        Assert.assertFalse(options.isXincludeAware());
    }

    @Test
    public void testEqualsHashcode() throws Exception {
        BooleanProperty[] properties = new BooleanProperty[]{ COALESCING_DESCRIPTOR, EXPAND_ENTITY_REFERENCES_DESCRIPTOR, IGNORING_COMMENTS_DESCRIPTOR, IGNORING_ELEMENT_CONTENT_WHITESPACE_DESCRIPTOR, NAMESPACE_AWARE_DESCRIPTOR, VALIDATING_DESCRIPTOR, XINCLUDE_AWARE_DESCRIPTOR };
        for (int i = 0; i < (properties.length); i++) {
            BooleanProperty property = properties[i];
            XmlParserOptionsTest.MyRule rule = new XmlParserOptionsTest.MyRule();
            rule.setProperty(property, true);
            ParserOptions options1 = rule.getParserOptions();
            rule.setProperty(property, false);
            ParserOptions options2 = rule.getParserOptions();
            rule.setProperty(property, true);
            ParserOptions options3 = rule.getParserOptions();
            rule.setProperty(property, false);
            ParserOptions options4 = rule.getParserOptions();
            ParserOptionsTest.verifyOptionsEqualsHashcode(options1, options2, options3, options4);
        }
        XmlParserOptions options1 = new XmlParserOptions();
        options1.setSuppressMarker("foo");
        XmlParserOptions options2 = new XmlParserOptions();
        options2.setSuppressMarker("bar");
        XmlParserOptions options3 = new XmlParserOptions();
        options3.setSuppressMarker("foo");
        XmlParserOptions options4 = new XmlParserOptions();
        options4.setSuppressMarker("bar");
        ParserOptionsTest.verifyOptionsEqualsHashcode(options1, options2, options3, options4);
    }

    private static final class MyRule extends AbstractXmlRule {}
}

