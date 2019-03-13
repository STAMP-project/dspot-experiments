package org.mockserver.serialization.serializers.body;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.RegexBody;
import org.mockserver.serialization.ObjectMapperFactory;


public class RegexBodyDTOSerializerTest {
    @Test
    public void shouldSerializeRegexBodyDTO() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.RegexBodyDTO(new RegexBody("some[a-zA-Z]*"))), Is.is("{\"type\":\"REGEX\",\"regex\":\"some[a-zA-Z]*\"}"));
    }

    @Test
    public void shouldSerializeRegexBodyDTOWithNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.RegexBodyDTO(new RegexBody("some[a-zA-Z]*"), true)), Is.is("{\"not\":true,\"type\":\"REGEX\",\"regex\":\"some[a-zA-Z]*\"}"));
    }
}

