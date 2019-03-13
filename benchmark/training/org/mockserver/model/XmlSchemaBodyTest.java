package org.mockserver.model;


import Body.Type.XML_SCHEMA;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class XmlSchemaBodyTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        XmlSchemaBody xmlSchemaBody = new XmlSchemaBody("some_body");
        // then
        MatcherAssert.assertThat(xmlSchemaBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(xmlSchemaBody.getType(), Is.is(XML_SCHEMA));
    }

    @Test
    public void shouldReturnValueSetInStaticConstructor() {
        // when
        XmlSchemaBody xmlSchemaBody = XmlSchemaBody.xmlSchema("some_body");
        // then
        MatcherAssert.assertThat(xmlSchemaBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(xmlSchemaBody.getType(), Is.is(XML_SCHEMA));
    }

    @Test
    public void shouldLoadSchemaFromClasspath() {
        // when
        XmlSchemaBody xmlSchemaBody = XmlSchemaBody.xmlSchemaFromResource("org/mockserver/model/testXmlSchema.xsd");
        // then
        MatcherAssert.assertThat(xmlSchemaBody.getValue(), Is.is((((((((((((((((((((((((((((((((((((((((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (NEW_LINE)) + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">") + (NEW_LINE)) + "    <!-- XML Schema Generated from XML Document on Wed Jun 28 2017 21:52:45 GMT+0100 (BST) -->") + (NEW_LINE)) + "    <!-- with XmlGrid.net Free Online Service http://xmlgrid.net -->") + (NEW_LINE)) + "    <xs:element name=\"notes\">") + (NEW_LINE)) + "        <xs:complexType>") + (NEW_LINE)) + "            <xs:sequence>") + (NEW_LINE)) + "                <xs:element name=\"note\" maxOccurs=\"unbounded\">") + (NEW_LINE)) + "                    <xs:complexType>") + (NEW_LINE)) + "                        <xs:sequence>") + (NEW_LINE)) + "                            <xs:element name=\"to\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"from\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"heading\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"body\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                        </xs:sequence>") + (NEW_LINE)) + "                    </xs:complexType>") + (NEW_LINE)) + "                </xs:element>") + (NEW_LINE)) + "            </xs:sequence>") + (NEW_LINE)) + "        </xs:complexType>") + (NEW_LINE)) + "    </xs:element>") + (NEW_LINE)) + "</xs:schema>")));
    }
}

