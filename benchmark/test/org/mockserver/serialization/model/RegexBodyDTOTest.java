package org.mockserver.serialization.model;


import Body.Type.REGEX;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.RegexBody;


/**
 *
 *
 * @author jamesdbloom
 */
public class RegexBodyDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        RegexBodyDTO regexBody = new RegexBodyDTO(new RegexBody("some_body"));
        // then
        MatcherAssert.assertThat(regexBody.getRegex(), Is.is("some_body"));
        MatcherAssert.assertThat(regexBody.getType(), Is.is(REGEX));
    }

    @Test
    public void shouldBuildCorrectObject() {
        // when
        RegexBody regexBody = buildObject();
        // then
        MatcherAssert.assertThat(regexBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(regexBody.getType(), Is.is(REGEX));
    }

    @Test
    public void shouldReturnCorrectObjectFromStaticBuilder() {
        MatcherAssert.assertThat(RegexBody.regex("some_body"), Is.is(new RegexBody("some_body")));
    }

    @Test
    public void shouldHandleNull() {
        // given
        String body = null;
        // when
        RegexBody regexBody = buildObject();
        // then
        MatcherAssert.assertThat(regexBody.getValue(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(regexBody.getType(), Is.is(REGEX));
    }

    @Test
    public void shouldHandleEmptyByteArray() {
        // given
        String body = "";
        // when
        RegexBody regexBody = buildObject();
        // then
        MatcherAssert.assertThat(regexBody.getValue(), Is.is(""));
        MatcherAssert.assertThat(regexBody.getType(), Is.is(REGEX));
    }
}

