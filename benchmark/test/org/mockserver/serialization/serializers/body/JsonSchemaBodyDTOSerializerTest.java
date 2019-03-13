package org.mockserver.serialization.serializers.body;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.JsonSchemaBody;
import org.mockserver.serialization.ObjectMapperFactory;


public class JsonSchemaBodyDTOSerializerTest {
    @Test
    public void shouldSerializeJsonBodyDTO() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.JsonSchemaBodyDTO(new JsonSchemaBody("{\"type\": \"object\", \"properties\": {\"id\": {\"type\": \"integer\"}}, \"required\": [\"id\"]}"), false)), Is.is("{\"type\":\"JSON_SCHEMA\",\"jsonSchema\":\"{\\\"type\\\": \\\"object\\\", \\\"properties\\\": {\\\"id\\\": {\\\"type\\\": \\\"integer\\\"}}, \\\"required\\\": [\\\"id\\\"]}\"}"));
    }

    @Test
    public void shouldSerializeJsonBodyDTOWithNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.JsonSchemaBodyDTO(new JsonSchemaBody("{\"type\": \"object\", \"properties\": {\"id\": {\"type\": \"integer\"}}, \"required\": [\"id\"]}"), true)), Is.is("{\"not\":true,\"type\":\"JSON_SCHEMA\",\"jsonSchema\":\"{\\\"type\\\": \\\"object\\\", \\\"properties\\\": {\\\"id\\\": {\\\"type\\\": \\\"integer\\\"}}, \\\"required\\\": [\\\"id\\\"]}\"}"));
    }
}

