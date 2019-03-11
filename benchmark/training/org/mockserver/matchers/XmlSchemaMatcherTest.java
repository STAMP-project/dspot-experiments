package org.mockserver.matchers;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.validator.xmlschema.XmlSchemaValidator;
import org.slf4j.Logger;
import org.slf4j.event.Level;


/**
 *
 *
 * @author jamesdbloom
 */
public class XmlSchemaMatcherTest {
    private static boolean disableSystemOut;

    private final String XML_SCHEMA = ((((((((((((((((((((((((((((((((((((((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (NEW_LINE)) + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">") + (NEW_LINE)) + "    <!-- XML Schema Generated from XML Document on Wed Jun 28 2017 21:52:45 GMT+0100 (BST) -->") + (NEW_LINE)) + "    <!-- with XmlGrid.net Free Online Service http://xmlgrid.net -->") + (NEW_LINE)) + "    <xs:element name=\"notes\">") + (NEW_LINE)) + "        <xs:complexType>") + (NEW_LINE)) + "            <xs:sequence>") + (NEW_LINE)) + "                <xs:element name=\"note\" maxOccurs=\"unbounded\">") + (NEW_LINE)) + "                    <xs:complexType>") + (NEW_LINE)) + "                        <xs:sequence>") + (NEW_LINE)) + "                            <xs:element name=\"to\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"from\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"heading\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"body\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                        </xs:sequence>") + (NEW_LINE)) + "                    </xs:complexType>") + (NEW_LINE)) + "                </xs:element>") + (NEW_LINE)) + "            </xs:sequence>") + (NEW_LINE)) + "        </xs:complexType>") + (NEW_LINE)) + "    </xs:element>") + (NEW_LINE)) + "</xs:schema>";

    protected Logger logger;

    @Mock
    private XmlSchemaValidator mockXmlSchemaValidator;

    @InjectMocks
    private XmlSchemaMatcher xmlSchemaMatcher;

    @Test
    public void shouldMatchXml() {
        // given
        String xml = "some_xml";
        Mockito.when(mockXmlSchemaValidator.isValid(xml)).thenReturn("");
        // then
        Assert.assertTrue(xmlSchemaMatcher.matches(null, xml));
    }

    @Test
    public void shouldNotMatchXml() {
        Level originalLevel = ConfigurationProperties.logLevel();
        try {
            // given
            ConfigurationProperties.logLevel("TRACE");
            String xml = "some_xml";
            Mockito.when(mockXmlSchemaValidator.isValid(xml)).thenReturn("validator_error");
            // when
            Assert.assertFalse(xmlSchemaMatcher.matches(null, xml));
            // then
            Mockito.verify(logger).trace((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("Failed to match [" + (NEW_LINE)) + (NEW_LINE)) + "\tsome_xml") + (NEW_LINE)) + (NEW_LINE)) + " ] with schema [") + (NEW_LINE)) + (NEW_LINE)) + "\t<?xml version=\"1.0\" encoding=\"UTF-8\"?>") + (NEW_LINE)) + "\t<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">") + (NEW_LINE)) + "\t    <!-- XML Schema Generated from XML Document on Wed Jun 28 2017 21:52:45 GMT+0100 (BST) -->") + (NEW_LINE)) + "\t    <!-- with XmlGrid.net Free Online Service http://xmlgrid.net -->") + (NEW_LINE)) + "\t    <xs:element name=\"notes\">") + (NEW_LINE)) + "\t        <xs:complexType>") + (NEW_LINE)) + "\t            <xs:sequence>") + (NEW_LINE)) + "\t                <xs:element name=\"note\" maxOccurs=\"unbounded\">") + (NEW_LINE)) + "\t                    <xs:complexType>") + (NEW_LINE)) + "\t                        <xs:sequence>") + (NEW_LINE)) + "\t                            <xs:element name=\"to\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "\t                            <xs:element name=\"from\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "\t                            <xs:element name=\"heading\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "\t                            <xs:element name=\"body\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "\t                        </xs:sequence>") + (NEW_LINE)) + "\t                    </xs:complexType>") + (NEW_LINE)) + "\t                </xs:element>") + (NEW_LINE)) + "\t            </xs:sequence>") + (NEW_LINE)) + "\t        </xs:complexType>") + (NEW_LINE)) + "\t    </xs:element>") + (NEW_LINE)) + "\t</xs:schema>") + (NEW_LINE)) + (NEW_LINE)) + " ] because [") + (NEW_LINE)) + (NEW_LINE)) + "\tvalidator_error") + (NEW_LINE)) + (NEW_LINE)) + " ]"));
        } finally {
            ConfigurationProperties.logLevel(originalLevel.toString());
        }
    }

    @Test
    public void shouldHandleExpectation() {
        Level originalLevel = ConfigurationProperties.logLevel();
        try {
            // given
            ConfigurationProperties.logLevel("TRACE");
            String xml = "some_xml";
            Mockito.when(mockXmlSchemaValidator.isValid(xml)).thenThrow(new RuntimeException("TEST_EXCEPTION"));
            // when
            Assert.assertFalse(xmlSchemaMatcher.matches(null, xml));
            // then
            Mockito.verify(logger).trace((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("Failed to match [" + (NEW_LINE)) + (NEW_LINE)) + "\tsome_xml") + (NEW_LINE)) + (NEW_LINE)) + " ] with schema [") + (NEW_LINE)) + (NEW_LINE)) + "\t<?xml version=\"1.0\" encoding=\"UTF-8\"?>") + (NEW_LINE)) + "\t<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">") + (NEW_LINE)) + "\t    <!-- XML Schema Generated from XML Document on Wed Jun 28 2017 21:52:45 GMT+0100 (BST) -->") + (NEW_LINE)) + "\t    <!-- with XmlGrid.net Free Online Service http://xmlgrid.net -->") + (NEW_LINE)) + "\t    <xs:element name=\"notes\">") + (NEW_LINE)) + "\t        <xs:complexType>") + (NEW_LINE)) + "\t            <xs:sequence>") + (NEW_LINE)) + "\t                <xs:element name=\"note\" maxOccurs=\"unbounded\">") + (NEW_LINE)) + "\t                    <xs:complexType>") + (NEW_LINE)) + "\t                        <xs:sequence>") + (NEW_LINE)) + "\t                            <xs:element name=\"to\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "\t                            <xs:element name=\"from\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "\t                            <xs:element name=\"heading\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "\t                            <xs:element name=\"body\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "\t                        </xs:sequence>") + (NEW_LINE)) + "\t                    </xs:complexType>") + (NEW_LINE)) + "\t                </xs:element>") + (NEW_LINE)) + "\t            </xs:sequence>") + (NEW_LINE)) + "\t        </xs:complexType>") + (NEW_LINE)) + "\t    </xs:element>") + (NEW_LINE)) + "\t</xs:schema>") + (NEW_LINE)) + (NEW_LINE)) + " ] because [") + (NEW_LINE)) + (NEW_LINE)) + "\tTEST_EXCEPTION") + (NEW_LINE)) + (NEW_LINE)) + " ]"));
        } finally {
            ConfigurationProperties.logLevel(originalLevel.toString());
        }
    }

    @Test
    public void showHaveCorrectEqualsBehaviour() {
        MockServerLogger mockServerLogger = new MockServerLogger();
        Assert.assertEquals(new XmlSchemaMatcher(mockServerLogger, XML_SCHEMA), new XmlSchemaMatcher(mockServerLogger, XML_SCHEMA));
    }
}

