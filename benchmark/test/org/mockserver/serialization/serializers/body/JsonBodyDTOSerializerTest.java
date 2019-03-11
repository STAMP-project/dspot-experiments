package org.mockserver.serialization.serializers.body;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.net.MediaType;
import java.nio.charset.StandardCharsets;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.matchers.MatchType;
import org.mockserver.model.JsonBody;
import org.mockserver.serialization.ObjectMapperFactory;
import org.mockserver.serialization.model.JsonBodyDTO;


public class JsonBodyDTOSerializerTest {
    @Test
    public void shouldSerializeJsonBodyDTO() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new JsonBodyDTO(new JsonBody("{fieldOne: \"valueOne\", \"fieldTwo\": \"valueTwo\"}"))), Is.is("{\"type\":\"JSON\",\"json\":\"{fieldOne: \\\"valueOne\\\", \\\"fieldTwo\\\": \\\"valueTwo\\\"}\"}"));
    }

    @Test
    public void shouldSerializeJsonBodyWithDefaultMatchType() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new JsonBodyDTO(new JsonBody("{fieldOne: \"valueOne\", \"fieldTwo\": \"valueTwo\"}", MatchType.ONLY_MATCHING_FIELDS))), Is.is("{\"type\":\"JSON\",\"json\":\"{fieldOne: \\\"valueOne\\\", \\\"fieldTwo\\\": \\\"valueTwo\\\"}\"}"));
    }

    @Test
    public void shouldSerializeJsonBodyWithNoneDefaultMatchType() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new JsonBodyDTO(new JsonBody("{fieldOne: \"valueOne\", \"fieldTwo\": \"valueTwo\"}", MatchType.STRICT))), Is.is("{\"type\":\"JSON\",\"json\":\"{fieldOne: \\\"valueOne\\\", \\\"fieldTwo\\\": \\\"valueTwo\\\"}\",\"matchType\":\"STRICT\"}"));
    }

    @Test
    public void shouldSerializeJsonBodyWithNoneDefaultMatchTypeAndCharset() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new JsonBodyDTO(new JsonBody("{fieldOne: \"valueOne\", \"fieldTwo\": \"valueTwo\"}", StandardCharsets.UTF_16, MatchType.STRICT))), Is.is("{\"contentType\":\"application/json; charset=utf-16\",\"type\":\"JSON\",\"json\":\"{fieldOne: \\\"valueOne\\\", \\\"fieldTwo\\\": \\\"valueTwo\\\"}\",\"matchType\":\"STRICT\"}"));
    }

    @Test
    public void shouldSerializeJsonBodyWithNoneDefaultMatchTypeAndContentType() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new JsonBodyDTO(new JsonBody("{fieldOne: \"valueOne\", \"fieldTwo\": \"valueTwo\"}", MediaType.JSON_UTF_8, MatchType.STRICT))), Is.is("{\"contentType\":\"application/json; charset=utf-8\",\"type\":\"JSON\",\"json\":\"{fieldOne: \\\"valueOne\\\", \\\"fieldTwo\\\": \\\"valueTwo\\\"}\",\"matchType\":\"STRICT\"}"));
    }

    @Test
    public void shouldSerializeJsonBodyDTOWithNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new JsonBodyDTO(new JsonBody("{fieldOne: \"valueOne\", \"fieldTwo\": \"valueTwo\"}", MatchType.STRICT), true)), Is.is("{\"not\":true,\"type\":\"JSON\",\"json\":\"{fieldOne: \\\"valueOne\\\", \\\"fieldTwo\\\": \\\"valueTwo\\\"}\",\"matchType\":\"STRICT\"}"));
    }
}

