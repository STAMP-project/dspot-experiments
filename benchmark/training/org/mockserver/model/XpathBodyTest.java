package org.mockserver.model;


import Body.Type.XPATH;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class XpathBodyTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        XPathBody xPathBody = new XPathBody("some_body");
        // then
        MatcherAssert.assertThat(xPathBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(xPathBody.getType(), Is.is(XPATH));
        MatcherAssert.assertThat(xPathBody.getContentType(), Matchers.nullValue());
        MatcherAssert.assertThat(xPathBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_8));
    }

    @Test
    public void shouldReturnValuesFromStaticBuilder() {
        // when
        XPathBody xPathBody = XPathBody.xpath("some_body");
        // then
        MatcherAssert.assertThat(xPathBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(xPathBody.getType(), Is.is(XPATH));
        MatcherAssert.assertThat(xPathBody.getContentType(), Matchers.nullValue());
        MatcherAssert.assertThat(xPathBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_8));
    }
}

