package org.mockserver.serialization.model;


import Body.Type.JSON_SCHEMA;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.JsonSchemaBody;


/**
 *
 *
 * @author jamesdbloom
 */
public class JsonSchemaBodyDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        JsonSchemaBodyDTO jsonSchemaBodyDTO = new JsonSchemaBodyDTO(new JsonSchemaBody("some_body"));
        // then
        MatcherAssert.assertThat(jsonSchemaBodyDTO.getJson(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonSchemaBodyDTO.getType(), Is.is(JSON_SCHEMA));
    }

    @Test
    public void shouldBuildCorrectObject() {
        // when
        JsonSchemaBody jsonSchemaBody = buildObject();
        // then
        MatcherAssert.assertThat(jsonSchemaBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonSchemaBody.getType(), Is.is(JSON_SCHEMA));
    }

    @Test
    public void shouldReturnCorrectObjectFromStaticBuilder() {
        MatcherAssert.assertThat(JsonSchemaBody.jsonSchema("some_body"), Is.is(new JsonSchemaBody("some_body")));
    }

    @Test
    public void shouldHandleNull() {
        // given
        String body = null;
        // when
        JsonSchemaBody jsonSchemaBody = buildObject();
        // then
        MatcherAssert.assertThat(jsonSchemaBody.getValue(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(jsonSchemaBody.getType(), Is.is(JSON_SCHEMA));
    }

    @Test
    public void shouldHandleEmptyByteArray() {
        // given
        String body = "";
        // when
        JsonSchemaBody jsonSchemaBody = buildObject();
        // then
        MatcherAssert.assertThat(jsonSchemaBody.getValue(), Is.is(""));
        MatcherAssert.assertThat(jsonSchemaBody.getType(), Is.is(JSON_SCHEMA));
    }
}

