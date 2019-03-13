package org.mockserver.model;


import Body.Type.JSON_SCHEMA;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class JsonSchemaBodyTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        JsonSchemaBody jsonSchemaBody = new JsonSchemaBody("some_body");
        // then
        MatcherAssert.assertThat(jsonSchemaBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonSchemaBody.getType(), Is.is(JSON_SCHEMA));
    }

    @Test
    public void shouldReturnValueSetInStaticConstructor() {
        // when
        JsonSchemaBody jsonSchemaBody = JsonSchemaBody.jsonSchema("some_body");
        // then
        MatcherAssert.assertThat(jsonSchemaBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonSchemaBody.getType(), Is.is(JSON_SCHEMA));
    }

    @Test
    public void shouldLoadSchemaFromClasspath() {
        // when
        JsonSchemaBody jsonSchemaBody = JsonSchemaBody.jsonSchemaFromResource("org/mockserver/model/testJsonSchema.json");
        // then
        MatcherAssert.assertThat(jsonSchemaBody.getValue(), Is.is((((((((((((((((("{" + (NEW_LINE)) + "  \"type\": \"object\",") + (NEW_LINE)) + "  \"properties\": {") + (NEW_LINE)) + "    \"someField\": {") + (NEW_LINE)) + "      \"type\": \"string\"") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"required\": [\"someField\"]") + (NEW_LINE)) + "}")));
    }
}

