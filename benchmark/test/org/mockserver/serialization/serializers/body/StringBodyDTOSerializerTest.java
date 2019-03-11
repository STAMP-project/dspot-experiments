package org.mockserver.serialization.serializers.body;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.net.MediaType;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.Not;
import org.mockserver.model.StringBody;
import org.mockserver.serialization.ObjectMapperFactory;


public class StringBodyDTOSerializerTest {
    @Test
    public void shouldSerializeStringBodyDTO() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.StringBodyDTO(new StringBody("string_body"))), Is.is("\"string_body\""));
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.StringBodyDTO(new StringBody("string_body", false))), Is.is("\"string_body\""));
    }

    @Test
    public void shouldSerializeStringBodyDTOWithSubString() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.StringBodyDTO(new StringBody("string_body", true))), Is.is("{\"type\":\"STRING\",\"string\":\"string_body\",\"subString\":true}"));
    }

    @Test
    public void shouldSerializeStringBodyDTOWithCharset() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.StringBodyDTO(new StringBody("string_body", MediaType.PLAIN_TEXT_UTF_8))), Is.is("{\"type\":\"STRING\",\"string\":\"string_body\",\"contentType\":\"text/plain; charset=utf-8\"}"));
    }

    @Test
    public void shouldSerializeStringBodyDTOWithNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.StringBodyDTO(Not.not(new StringBody("string_body")))), Is.is("{\"not\":true,\"type\":\"STRING\",\"string\":\"string_body\"}"));
    }

    @Test
    public void shouldSerializeStringBodyDTOWithCharsetAndNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.StringBodyDTO(Not.not(new StringBody("string_body", MediaType.PLAIN_TEXT_UTF_8)))), Is.is("{\"not\":true,\"type\":\"STRING\",\"string\":\"string_body\",\"contentType\":\"text/plain; charset=utf-8\"}"));
    }
}

