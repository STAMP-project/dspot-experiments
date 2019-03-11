package org.mockserver.serialization.model;


import Body.Type.XML;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.mockserver.model.XmlBody;


/**
 *
 *
 * @author jamesdbloom
 */
public class XmlBodyDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        XmlBodyDTO xpathBody = new XmlBodyDTO(new XmlBody("some_body"));
        // then
        MatcherAssert.assertThat(xpathBody.getXml(), Is.is("some_body"));
        MatcherAssert.assertThat(xpathBody.getType(), Is.is(XML));
    }

    @Test
    public void shouldBuildCorrectObject() {
        // when
        XmlBody xmlBody = buildObject();
        // then
        MatcherAssert.assertThat(xmlBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(xmlBody.getType(), Is.is(XML));
    }

    @Test
    public void shouldReturnCorrectObjectFromStaticBuilder() {
        MatcherAssert.assertThat(XmlBody.xml("some_body"), Is.is(new XmlBody("some_body")));
    }

    @Test
    public void shouldHandleNull() {
        // given
        String body = null;
        // when
        XmlBody xmlBody = buildObject();
        // then
        MatcherAssert.assertThat(xmlBody.getValue(), IsNull.nullValue());
        MatcherAssert.assertThat(xmlBody.getType(), Is.is(XML));
    }

    @Test
    public void shouldHandleEmptyByteArray() {
        // given
        String body = "";
        // when
        XmlBody xmlBody = buildObject();
        // then
        MatcherAssert.assertThat(xmlBody.getValue(), Is.is(""));
        MatcherAssert.assertThat(xmlBody.getType(), Is.is(XML));
    }
}

