package org.mockserver.serialization.serializers.body;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.JsonPathBody;
import org.mockserver.serialization.ObjectMapperFactory;


public class JsonPathBodyDTOSerializerTest {
    @Test
    public void shouldSerializeJsonPathBodyDTO() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.JsonPathBodyDTO(new JsonPathBody("\\some\\path"))), Is.is("{\"type\":\"JSON_PATH\",\"jsonPath\":\"\\\\some\\\\path\"}"));
    }

    @Test
    public void shouldSerializeJsonPathBodyDTOWithNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.JsonPathBodyDTO(new JsonPathBody("\\some\\path"), true)), Is.is("{\"not\":true,\"type\":\"JSON_PATH\",\"jsonPath\":\"\\\\some\\\\path\"}"));
    }
}

