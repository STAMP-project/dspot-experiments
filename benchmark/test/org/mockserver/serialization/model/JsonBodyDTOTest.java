package org.mockserver.serialization.model;


import Body.Type.JSON;
import com.google.common.net.MediaType;
import java.nio.charset.StandardCharsets;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.matchers.MatchType;
import org.mockserver.model.JsonBody;


/**
 *
 *
 * @author jamesdbloom
 */
public class JsonBodyDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        JsonBodyDTO jsonBody = new JsonBodyDTO(new JsonBody("some_body"));
        // then
        MatcherAssert.assertThat(jsonBody.getJson(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(MatchType.ONLY_MATCHING_FIELDS));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json"));
    }

    @Test
    public void shouldReturnValuesSetInConstructorWithMatchType() {
        // when
        JsonBodyDTO jsonBody = new JsonBodyDTO(new JsonBody("some_body", MatchType.STRICT));
        // then
        MatcherAssert.assertThat(jsonBody.getJson(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(MatchType.STRICT));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json"));
    }

    @Test
    public void shouldReturnValuesSetInConstructorWithMatchTypeAndCharset() {
        // when
        JsonBodyDTO jsonBody = new JsonBodyDTO(new JsonBody("some_body", StandardCharsets.UTF_16, MatchType.STRICT));
        // then
        MatcherAssert.assertThat(jsonBody.getJson(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(MatchType.STRICT));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json; charset=utf-16"));
    }

    @Test
    public void shouldReturnValuesSetInConstructorWithMatchTypeAndMediaType() {
        // when
        JsonBodyDTO jsonBody = new JsonBodyDTO(new JsonBody("some_body", MediaType.JSON_UTF_8, MatchType.STRICT));
        // then
        MatcherAssert.assertThat(jsonBody.getJson(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(MatchType.STRICT));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json; charset=utf-8"));
    }

    @Test
    public void shouldBuildCorrectObject() {
        // when
        JsonBody jsonBody = buildObject();
        // then
        MatcherAssert.assertThat(jsonBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(MatchType.ONLY_MATCHING_FIELDS));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json"));
    }

    @Test
    public void shouldBuildCorrectObjectWithMatchType() {
        // when
        JsonBody jsonBody = new JsonBodyDTO(new JsonBody("some_body", MatchType.STRICT)).buildObject();
        // then
        MatcherAssert.assertThat(jsonBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(MatchType.STRICT));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json"));
    }

    @Test
    public void shouldBuildCorrectObjectWithMatchTypeAndCharset() {
        // when
        JsonBody jsonBody = new JsonBodyDTO(new JsonBody("some_body", StandardCharsets.UTF_16, MatchType.STRICT)).buildObject();
        // then
        MatcherAssert.assertThat(jsonBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(MatchType.STRICT));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json; charset=utf-16"));
    }

    @Test
    public void shouldHandleNull() {
        // given
        String body = null;
        // when
        JsonBody jsonBody = buildObject();
        // then
        MatcherAssert.assertThat(jsonBody.getValue(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
    }

    @Test
    public void shouldHandleEmptyByteArray() {
        // given
        String body = "";
        // when
        JsonBody jsonBody = buildObject();
        // then
        MatcherAssert.assertThat(jsonBody.getValue(), Is.is(""));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
    }
}

