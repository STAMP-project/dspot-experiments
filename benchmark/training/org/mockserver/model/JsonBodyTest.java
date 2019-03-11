package org.mockserver.model;


import Body.Type.JSON;
import MatchType.ONLY_MATCHING_FIELDS;
import MatchType.STRICT;
import com.google.common.net.MediaType;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.matchers.MatchType;


/**
 *
 *
 * @author jamesdbloom
 */
public class JsonBodyTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        JsonBody jsonBody = new JsonBody("some_body");
        // then
        MatcherAssert.assertThat(jsonBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(ONLY_MATCHING_FIELDS));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json"));
    }

    @Test
    public void shouldReturnValuesSetInConstructorWithMatchType() {
        // when
        JsonBody jsonBody = new JsonBody("some_body", MatchType.STRICT);
        // then
        MatcherAssert.assertThat(jsonBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(STRICT));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json"));
    }

    @Test
    public void shouldReturnValuesSetInConstructorWithMatchTypeAndCharset() {
        // when
        JsonBody jsonBody = new JsonBody("some_body", StandardCharsets.UTF_16, MatchType.STRICT);
        // then
        MatcherAssert.assertThat(jsonBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(STRICT));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json; charset=utf-16"));
    }

    @Test
    public void shouldReturnValuesSetInConstructorWithMatchTypeAndMediaType() {
        // when
        JsonBody jsonBody = new JsonBody("some_body", MediaType.JSON_UTF_8, MatchType.STRICT);
        // then
        MatcherAssert.assertThat(jsonBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(STRICT));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json; charset=utf-8"));
    }

    @Test
    public void shouldReturnValuesFromStaticBuilder() {
        // when
        JsonBody jsonBody = JsonBody.json("some_body");
        // then
        MatcherAssert.assertThat(jsonBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(MatchType.ONLY_MATCHING_FIELDS));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json"));
    }

    @Test
    public void shouldReturnValuesFromStaticBuilderWithMatchType() {
        // when
        JsonBody jsonBody = JsonBody.json("some_body", MatchType.STRICT);
        // then
        MatcherAssert.assertThat(jsonBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(MatchType.STRICT));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json"));
    }

    @Test
    public void shouldReturnValuesFromStaticBuilderWithCharsetAndMatchType() {
        // when
        JsonBody jsonBody = JsonBody.json("some_body", StandardCharsets.UTF_16, MatchType.STRICT);
        // then
        MatcherAssert.assertThat(jsonBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(MatchType.STRICT));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json; charset=utf-16"));
    }

    @Test
    public void shouldReturnValuesFromStaticBuilderWithMediaTypeAndMatchType() {
        // when
        JsonBody jsonBody = JsonBody.json("some_body", MediaType.JSON_UTF_8, MatchType.STRICT);
        // then
        MatcherAssert.assertThat(jsonBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(MatchType.STRICT));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json; charset=utf-8"));
    }

    @Test
    public void shouldReturnValuesFromStaticBuilderWithMatchTypeAndCharset() {
        // when
        JsonBody jsonBody = JsonBody.json("some_body", StandardCharsets.UTF_16, MatchType.STRICT);
        // then
        MatcherAssert.assertThat(jsonBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(MatchType.STRICT));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json; charset=utf-16"));
    }

    @Test
    public void shouldReturnValuesFromStaticObjectBuilder() {
        // when
        JsonBody jsonBody = JsonBody.json(new JsonBodyTest.TestObject());
        // then
        MatcherAssert.assertThat(jsonBody.getValue(), Is.is("{\"fieldOne\":\"valueOne\",\"fieldTwo\":\"valueTwo\"}"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(MatchType.ONLY_MATCHING_FIELDS));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json"));
    }

    @Test
    public void shouldReturnValuesFromStaticObjectBuilderWithMatchType() {
        // when
        JsonBody jsonBody = JsonBody.json(new JsonBodyTest.TestObject(), MatchType.STRICT);
        // then
        MatcherAssert.assertThat(jsonBody.getValue(), Is.is("{\"fieldOne\":\"valueOne\",\"fieldTwo\":\"valueTwo\"}"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(MatchType.STRICT));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json"));
    }

    @Test
    public void shouldReturnValuesFromStaticObjectBuilderWithCharsetAndMatchType() {
        // when
        JsonBody jsonBody = JsonBody.json(new JsonBodyTest.TestObject(), StandardCharsets.UTF_16, MatchType.STRICT);
        // then
        MatcherAssert.assertThat(jsonBody.getValue(), Is.is("{\"fieldOne\":\"valueOne\",\"fieldTwo\":\"valueTwo\"}"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(MatchType.STRICT));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json; charset=utf-16"));
    }

    @Test
    public void shouldReturnValuesFromStaticObjectBuilderWithMediaTypeAndMatchType() {
        // when
        JsonBody jsonBody = JsonBody.json(new JsonBodyTest.TestObject(), MediaType.JSON_UTF_8, MatchType.STRICT);
        // then
        MatcherAssert.assertThat(jsonBody.getValue(), Is.is("{\"fieldOne\":\"valueOne\",\"fieldTwo\":\"valueTwo\"}"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(MatchType.STRICT));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json; charset=utf-8"));
    }

    @Test
    public void shouldReturnValuesFromStaticObjectBuilderWithMatchTypeAndCharset() {
        // when
        JsonBody jsonBody = JsonBody.json(new JsonBodyTest.TestObject(), StandardCharsets.UTF_16, MatchType.STRICT);
        // then
        MatcherAssert.assertThat(jsonBody.getValue(), Is.is("{\"fieldOne\":\"valueOne\",\"fieldTwo\":\"valueTwo\"}"));
        MatcherAssert.assertThat(jsonBody.getType(), Is.is(JSON));
        MatcherAssert.assertThat(jsonBody.getMatchType(), Is.is(MatchType.STRICT));
        MatcherAssert.assertThat(jsonBody.getContentType(), Is.is("application/json; charset=utf-16"));
    }

    public class TestObject {
        private String fieldOne = "valueOne";

        private String fieldTwo = "valueTwo";

        public String getFieldOne() {
            return fieldOne;
        }

        public void setFieldOne(String fieldOne) {
            this.fieldOne = fieldOne;
        }

        public String getFieldTwo() {
            return fieldTwo;
        }

        public void setFieldTwo(String fieldTwo) {
            this.fieldTwo = fieldTwo;
        }
    }
}

