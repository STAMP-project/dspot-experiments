package org.mockserver.serialization.serializers.body;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.net.MediaType;
import java.nio.charset.StandardCharsets;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.Not;
import org.mockserver.model.XmlBody;
import org.mockserver.serialization.ObjectMapperFactory;


public class XmlBodySerializerTest {
    @Test
    public void shouldSerializeXmlBody() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new XmlBody("<some><xml></xml></some>")), Is.is("{\"type\":\"XML\",\"xml\":\"<some><xml></xml></some>\"}"));
    }

    @Test
    public void shouldSerializeXmlBodyWithNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(Not.not(new XmlBody("<some><xml></xml></some>"))), Is.is("{\"not\":true,\"type\":\"XML\",\"xml\":\"<some><xml></xml></some>\"}"));
    }

    @Test
    public void shouldSerializeXmlBodyWithContentType() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new XmlBody("<some><xml></xml></some>", MediaType.ATOM_UTF_8)), Is.is("{\"contentType\":\"application/atom+xml; charset=utf-8\",\"type\":\"XML\",\"xml\":\"<some><xml></xml></some>\"}"));
    }

    @Test
    public void shouldSerializeXmlBodyWithCharsetAndNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(Not.not(new XmlBody("<some><xml></xml></some>", StandardCharsets.UTF_16))), Is.is("{\"not\":true,\"contentType\":\"application/xml; charset=utf-16\",\"type\":\"XML\",\"xml\":\"<some><xml></xml></some>\"}"));
    }

    @Test
    public void shouldSerializeXmlBodyWithContentTypeAndNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(Not.not(new XmlBody("<some><xml></xml></some>", MediaType.ATOM_UTF_8))), Is.is("{\"not\":true,\"contentType\":\"application/atom+xml; charset=utf-8\",\"type\":\"XML\",\"xml\":\"<some><xml></xml></some>\"}"));
    }
}

