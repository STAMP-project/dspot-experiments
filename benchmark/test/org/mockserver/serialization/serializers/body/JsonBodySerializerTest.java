package org.mockserver.serialization.serializers.body;


import MatchType.ONLY_MATCHING_FIELDS;
import MatchType.STRICT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.net.MediaType;
import java.nio.charset.StandardCharsets;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.JsonBody;
import org.mockserver.model.Not;
import org.mockserver.serialization.ObjectMapperFactory;


public class JsonBodySerializerTest {
    public class TestObject {
        private String fieldOne = "valueOne";

        private String fieldTwo = "valueTwo";

        public String getFieldOne() {
            return fieldOne;
        }

        public void setFieldOne(String fieldOne) {
            this.fieldOne = fieldOne;
        }

        public String getFieldTwo() {
            return fieldTwo;
        }

        public void setFieldTwo(String fieldTwo) {
            this.fieldTwo = fieldTwo;
        }
    }

    @Test
    public void shouldSerializeJsonBodyAsObject() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(JsonBody.json(new JsonBodySerializerTest.TestObject())), Is.is("{\"type\":\"JSON\",\"json\":\"{\\\"fieldOne\\\":\\\"valueOne\\\",\\\"fieldTwo\\\":\\\"valueTwo\\\"}\"}"));
    }

    @Test
    public void shouldSerializeJsonBody() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(JsonBody.json("{fieldOne: \"valueOne\", \"fieldTwo\": \"valueTwo\"}")), Is.is("{\"type\":\"JSON\",\"json\":\"{fieldOne: \\\"valueOne\\\", \\\"fieldTwo\\\": \\\"valueTwo\\\"}\"}"));
    }

    @Test
    public void shouldSerializeJsonBodyWithDefaultMatchType() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(JsonBody.json("{fieldOne: \"valueOne\", \"fieldTwo\": \"valueTwo\"}", ONLY_MATCHING_FIELDS)), Is.is("{\"type\":\"JSON\",\"json\":\"{fieldOne: \\\"valueOne\\\", \\\"fieldTwo\\\": \\\"valueTwo\\\"}\"}"));
    }

    @Test
    public void shouldSerializeJsonBodyWithMatchType() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(JsonBody.json("{fieldOne: \"valueOne\", \"fieldTwo\": \"valueTwo\"}", STRICT)), Is.is("{\"type\":\"JSON\",\"json\":\"{fieldOne: \\\"valueOne\\\", \\\"fieldTwo\\\": \\\"valueTwo\\\"}\",\"matchType\":\"STRICT\"}"));
    }

    @Test
    public void shouldSerializeJsonBodyWithNoneDefaultMatchTypeAndCharset() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(JsonBody.json("{fieldOne: \"valueOne\", \"fieldTwo\": \"valueTwo\"}", StandardCharsets.UTF_16, STRICT)), Is.is("{\"contentType\":\"application/json; charset=utf-16\",\"type\":\"JSON\",\"json\":\"{fieldOne: \\\"valueOne\\\", \\\"fieldTwo\\\": \\\"valueTwo\\\"}\",\"matchType\":\"STRICT\"}"));
    }

    @Test
    public void shouldSerializeJsonBodyWithNoneDefaultMatchTypeAndContentType() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(JsonBody.json("{fieldOne: \"valueOne\", \"fieldTwo\": \"valueTwo\"}", MediaType.JSON_UTF_8, STRICT)), Is.is("{\"contentType\":\"application/json; charset=utf-8\",\"type\":\"JSON\",\"json\":\"{fieldOne: \\\"valueOne\\\", \\\"fieldTwo\\\": \\\"valueTwo\\\"}\",\"matchType\":\"STRICT\"}"));
    }

    @Test
    public void shouldSerializeJsonBodyWithMatchTypeWithNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(Not.not(JsonBody.json("{fieldOne: \"valueOne\", \"fieldTwo\": \"valueTwo\"}", STRICT))), Is.is("{\"not\":true,\"type\":\"JSON\",\"json\":\"{fieldOne: \\\"valueOne\\\", \\\"fieldTwo\\\": \\\"valueTwo\\\"}\",\"matchType\":\"STRICT\"}"));
    }
}

