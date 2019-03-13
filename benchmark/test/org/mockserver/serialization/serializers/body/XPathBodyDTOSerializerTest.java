package org.mockserver.serialization.serializers.body;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.XPathBody;
import org.mockserver.serialization.ObjectMapperFactory;


public class XPathBodyDTOSerializerTest {
    @Test
    public void shouldSerializeXPathBodyDTO() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.XPathBodyDTO(new XPathBody("\\some\\xpath"))), Is.is("{\"type\":\"XPATH\",\"xpath\":\"\\\\some\\\\xpath\"}"));
    }

    @Test
    public void shouldSerializeXPathBodyDTOWithNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.XPathBodyDTO(new XPathBody("\\some\\xpath"), true)), Is.is("{\"not\":true,\"type\":\"XPATH\",\"xpath\":\"\\\\some\\\\xpath\"}"));
    }
}

