package org.mockserver.model;


import Body.Type.REGEX;
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
public class RegexBodyTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        RegexBody regexBody = new RegexBody("some_body");
        // then
        MatcherAssert.assertThat(regexBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(regexBody.getType(), Is.is(REGEX));
        MatcherAssert.assertThat(regexBody.getContentType(), Matchers.nullValue());
        MatcherAssert.assertThat(regexBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_8));
    }

    @Test
    public void shouldReturnValuesFromStaticBuilder() {
        // when
        RegexBody regexBody = RegexBody.regex("some_body");
        // then
        MatcherAssert.assertThat(regexBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(regexBody.getType(), Is.is(REGEX));
        MatcherAssert.assertThat(regexBody.getContentType(), Matchers.nullValue());
        MatcherAssert.assertThat(regexBody.getCharset(StandardCharsets.UTF_8), Is.is(StandardCharsets.UTF_8));
    }
}

