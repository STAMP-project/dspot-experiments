package org.mockserver.serialization.serializers.body;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.Not;
import org.mockserver.model.RegexBody;
import org.mockserver.serialization.ObjectMapperFactory;


public class RegexBodySerializerTest {
    @Test
    public void shouldSerializeRegexBody() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new RegexBody("some[a-zA-Z]*")), Is.is("{\"type\":\"REGEX\",\"regex\":\"some[a-zA-Z]*\"}"));
    }

    @Test
    public void shouldSerializeRegexBodyWithNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(Not.not(new RegexBody("some[a-zA-Z]*"))), Is.is("{\"not\":true,\"type\":\"REGEX\",\"regex\":\"some[a-zA-Z]*\"}"));
    }
}

