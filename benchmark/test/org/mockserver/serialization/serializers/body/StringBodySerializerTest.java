package org.mockserver.serialization.serializers.body;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.net.MediaType;
import java.nio.charset.StandardCharsets;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.Not;
import org.mockserver.model.StringBody;
import org.mockserver.serialization.ObjectMapperFactory;


public class StringBodySerializerTest {
    @Test
    public void shouldSerializeStringBody() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new StringBody("string_body")), Is.is("\"string_body\""));
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new StringBody("string_body", false)), Is.is("\"string_body\""));
    }

    @Test
    public void shouldSerializeStringBodyDTOWithSubString() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new StringBody("string_body", true)), Is.is("{\"type\":\"STRING\",\"string\":\"string_body\",\"subString\":true}"));
    }

    @Test
    public void shouldSerializeStringBodyWithCharset() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new StringBody("string_body", StandardCharsets.UTF_16)), Is.is("{\"type\":\"STRING\",\"string\":\"string_body\",\"contentType\":\"text/plain; charset=utf-16\"}"));
    }

    @Test
    public void shouldSerializeStringBodyWithContentType() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new StringBody("string_body", MediaType.ATOM_UTF_8)), Is.is("{\"type\":\"STRING\",\"string\":\"string_body\",\"contentType\":\"application/atom+xml; charset=utf-8\"}"));
    }

    @Test
    public void shouldSerializeStringBodyWithNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(Not.not(new StringBody("string_body"))), Is.is("{\"not\":true,\"type\":\"STRING\",\"string\":\"string_body\"}"));
    }

    @Test
    public void shouldSerializeStringBodyWithCharsetAndNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(Not.not(new StringBody("string_body", StandardCharsets.UTF_16))), Is.is("{\"not\":true,\"type\":\"STRING\",\"string\":\"string_body\",\"contentType\":\"text/plain; charset=utf-16\"}"));
    }

    @Test
    public void shouldSerializeStringBodyWithContentTypeAndNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(Not.not(new StringBody("string_body", MediaType.ATOM_UTF_8))), Is.is("{\"not\":true,\"type\":\"STRING\",\"string\":\"string_body\",\"contentType\":\"application/atom+xml; charset=utf-8\"}"));
    }
}

