package org.mockserver.serialization.model;


import Body.Type.XPATH;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.XPathBody;


/**
 *
 *
 * @author jamesdbloom
 */
public class XPathBodyDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        XPathBodyDTO xpathBody = new XPathBodyDTO(new XPathBody("some_body"));
        // then
        MatcherAssert.assertThat(xpathBody.getXPath(), Is.is("some_body"));
        MatcherAssert.assertThat(xpathBody.getType(), Is.is(XPATH));
    }

    @Test
    public void shouldBuildCorrectObject() {
        // when
        XPathBody xPathBody = buildObject();
        // then
        MatcherAssert.assertThat(xPathBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(xPathBody.getType(), Is.is(XPATH));
    }

    @Test
    public void shouldReturnCorrectObjectFromStaticBuilder() {
        MatcherAssert.assertThat(XPathBody.xpath("some_body"), Is.is(new XPathBody("some_body")));
    }
}

