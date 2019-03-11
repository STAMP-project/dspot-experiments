package org.mockserver.serialization.model;


import Body.Type.XML_SCHEMA;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.XmlSchemaBody;


/**
 *
 *
 * @author jamesdbloom
 */
public class XmlSchemaBodyDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        XmlSchemaBodyDTO xmlSchemaBodyDTO = new XmlSchemaBodyDTO(new XmlSchemaBody("some_body"));
        // then
        MatcherAssert.assertThat(xmlSchemaBodyDTO.getXml(), Is.is("some_body"));
        MatcherAssert.assertThat(xmlSchemaBodyDTO.getType(), Is.is(XML_SCHEMA));
    }

    @Test
    public void shouldBuildCorrectObject() {
        // when
        XmlSchemaBody xmlSchemaBody = buildObject();
        // then
        MatcherAssert.assertThat(xmlSchemaBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(xmlSchemaBody.getType(), Is.is(XML_SCHEMA));
    }

    @Test
    public void shouldReturnCorrectObjectFromStaticBuilder() {
        MatcherAssert.assertThat(XmlSchemaBody.xmlSchema("some_body"), Is.is(new XmlSchemaBody("some_body")));
    }
}

