package org.mockserver.serialization.serializers.body;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.JsonSchemaBody;
import org.mockserver.model.Not;
import org.mockserver.serialization.ObjectMapperFactory;


public class JsonSchemaBodySerializerTest {
    @Test
    public void shouldSerializeJsonBody() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new JsonSchemaBody("{\"type\": \"object\", \"properties\": {\"id\": {\"type\": \"integer\"}}, \"required\": [\"id\"]}")), Is.is("{\"type\":\"JSON_SCHEMA\",\"jsonSchema\":\"{\\\"type\\\": \\\"object\\\", \\\"properties\\\": {\\\"id\\\": {\\\"type\\\": \\\"integer\\\"}}, \\\"required\\\": [\\\"id\\\"]}\"}"));
    }

    @Test
    public void shouldSerializeJsonBodyWithNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(Not.not(new JsonSchemaBody("{\"type\": \"object\", \"properties\": {\"id\": {\"type\": \"integer\"}}, \"required\": [\"id\"]}"))), Is.is("{\"not\":true,\"type\":\"JSON_SCHEMA\",\"jsonSchema\":\"{\\\"type\\\": \\\"object\\\", \\\"properties\\\": {\\\"id\\\": {\\\"type\\\": \\\"integer\\\"}}, \\\"required\\\": [\\\"id\\\"]}\"}"));
    }
}

