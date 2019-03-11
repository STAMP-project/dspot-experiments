package org.mockserver.serialization.serializers.body;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.JsonPathBody;
import org.mockserver.model.Not;
import org.mockserver.serialization.ObjectMapperFactory;


public class JsonPathBodySerializerTest {
    @Test
    public void shouldSerializeJsonPathBody() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new JsonPathBody("\\some\\path")), Is.is("{\"type\":\"JSON_PATH\",\"jsonPath\":\"\\\\some\\\\path\"}"));
    }

    @Test
    public void shouldSerializeJsonPathBodyWithNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(Not.not(new JsonPathBody("\\some\\path"))), Is.is("{\"not\":true,\"type\":\"JSON_PATH\",\"jsonPath\":\"\\\\some\\\\path\"}"));
    }
}

