package org.mockserver.serialization.serializers.body;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.net.MediaType;
import java.nio.charset.StandardCharsets;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.BinaryBody;
import org.mockserver.serialization.ObjectMapperFactory;


public class BinaryBodySerializerTest {
    @Test
    public void shouldSerializeBinaryBody() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new BinaryBody("some_bytes".getBytes(StandardCharsets.UTF_8))), Is.is("{\"type\":\"BINARY\",\"base64Bytes\":\"c29tZV9ieXRlcw==\"}"));
    }

    @Test
    public void shouldSerializeBinaryBodyWithContentType() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new BinaryBody("some_bytes".getBytes(StandardCharsets.UTF_8), MediaType.APPLICATION_BINARY)), Is.is("{\"contentType\":\"application/binary\",\"type\":\"BINARY\",\"base64Bytes\":\"c29tZV9ieXRlcw==\"}"));
    }
}

