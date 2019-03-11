package org.mockserver.serialization.serializers.body;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.Not;
import org.mockserver.model.XPathBody;
import org.mockserver.serialization.ObjectMapperFactory;


public class XPathBodySerializerTest {
    @Test
    public void shouldSerializeXPathBody() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new XPathBody("\\some\\xpath")), Is.is("{\"type\":\"XPATH\",\"xpath\":\"\\\\some\\\\xpath\"}"));
    }

    @Test
    public void shouldSerializeXPathBodyWithNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(Not.not(new XPathBody("\\some\\xpath"))), Is.is("{\"not\":true,\"type\":\"XPATH\",\"xpath\":\"\\\\some\\\\xpath\"}"));
    }
}

