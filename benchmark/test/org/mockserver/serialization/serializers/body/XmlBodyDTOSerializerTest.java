package org.mockserver.serialization.serializers.body;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.net.MediaType;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.XmlBody;
import org.mockserver.serialization.ObjectMapperFactory;


public class XmlBodyDTOSerializerTest {
    @Test
    public void shouldSerializeXmlBodyDTO() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.XmlBodyDTO(new XmlBody("<some><xml></xml></some>"))), Is.is("{\"type\":\"XML\",\"xml\":\"<some><xml></xml></some>\"}"));
    }

    @Test
    public void shouldSerializeXmlBodyDTOWithContentType() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.XmlBodyDTO(new XmlBody("<some><xml></xml></some>", MediaType.XML_UTF_8))), Is.is("{\"type\":\"XML\",\"xml\":\"<some><xml></xml></some>\",\"contentType\":\"text/xml; charset=utf-8\"}"));
    }

    @Test
    public void shouldSerializeXmlBodyDTOWithNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.XmlBodyDTO(new XmlBody("<some><xml></xml></some>"), true)), Is.is("{\"not\":true,\"type\":\"XML\",\"xml\":\"<some><xml></xml></some>\"}"));
    }

    @Test
    public void shouldSerializeXmlBodyDTOWithNotWithContentType() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new org.mockserver.serialization.model.XmlBodyDTO(new XmlBody("<some><xml></xml></some>", MediaType.XML_UTF_8), true)), Is.is("{\"not\":true,\"type\":\"XML\",\"xml\":\"<some><xml></xml></some>\",\"contentType\":\"text/xml; charset=utf-8\"}"));
    }
}

