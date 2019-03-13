package org.mockserver.serialization.serializers.body;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.net.MediaType;
import java.nio.charset.StandardCharsets;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.BinaryBody;
import org.mockserver.serialization.Base64Converter;
import org.mockserver.serialization.ObjectMapperFactory;


public class BinaryBodyDTOSerializerTest {
    private final Base64Converter base64Converter = new Base64Converter();

    @Test
    public void shouldSerializeBinaryBodyDTO() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.BinaryBodyDTO(new BinaryBody("someBytes".getBytes(StandardCharsets.UTF_8)))), Is.is((("{\"type\":\"BINARY\",\"base64Bytes\":\"" + (base64Converter.bytesToBase64String("someBytes".getBytes(StandardCharsets.UTF_8)))) + "\"}")));
    }

    @Test
    public void shouldSerializeBinaryBodyDTOWithContentType() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.BinaryBodyDTO(new BinaryBody("someBytes".getBytes(StandardCharsets.UTF_8), MediaType.ANY_VIDEO_TYPE))), Is.is((("{\"type\":\"BINARY\",\"base64Bytes\":\"" + (base64Converter.bytesToBase64String("someBytes".getBytes(StandardCharsets.UTF_8)))) + "\",\"contentType\":\"video/*\"}")));
    }

    @Test
    public void shouldSerializeBinaryBodyDTOWithNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.BinaryBodyDTO(new BinaryBody("someBytes".getBytes(StandardCharsets.UTF_8)), true)), Is.is((("{\"not\":true,\"type\":\"BINARY\",\"base64Bytes\":\"" + (base64Converter.bytesToBase64String("someBytes".getBytes(StandardCharsets.UTF_8)))) + "\"}")));
    }

    @Test
    public void shouldSerializeBinaryBodyDTOWithNotWithContentType() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.BinaryBodyDTO(new BinaryBody("someBytes".getBytes(StandardCharsets.UTF_8), MediaType.ANY_AUDIO_TYPE), true)), Is.is((("{\"not\":true,\"type\":\"BINARY\",\"base64Bytes\":\"" + (base64Converter.bytesToBase64String("someBytes".getBytes(StandardCharsets.UTF_8)))) + "\",\"contentType\":\"audio/*\"}")));
    }
}

