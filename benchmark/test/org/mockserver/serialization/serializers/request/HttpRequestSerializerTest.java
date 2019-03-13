package org.mockserver.serialization.serializers.request;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.Cookie;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.JsonBody;
import org.mockserver.model.Parameter;
import org.mockserver.model.XPathBody;
import org.mockserver.serialization.ObjectMapperFactory;


public class HttpRequestSerializerTest {
    @Test
    public void shouldReturnFormattedRequestWithNoFieldsSet() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(HttpRequest.request()), Is.is("{ }"));
    }

    @Test
    public void shouldReturnFormattedRequestWithAllFieldsSet() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(HttpRequest.request().withMethod("GET").withPath("/some/path").withQueryStringParameters(Parameter.param("parameterOneName", "parameterOneValue")).withBody("some_body").withHeaders(new Header("name", "value")).withCookies(new Cookie("name", "[A-Z]{0,10}")).withSecure(true).withKeepAlive(true)), Is.is((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"method\" : \"GET\",") + (NEW_LINE)) + "  \"path\" : \"/some/path\",") + (NEW_LINE)) + "  \"queryStringParameters\" : {") + (NEW_LINE)) + "    \"parameterOneName\" : [ \"parameterOneValue\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"name\" : [ \"value\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"cookies\" : {") + (NEW_LINE)) + "    \"name\" : \"[A-Z]{0,10}\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"keepAlive\" : true,") + (NEW_LINE)) + "  \"secure\" : true,") + (NEW_LINE)) + "  \"body\" : \"some_body\"") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldReturnFormattedRequestWithJsonBodyInToString() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(HttpRequest.request().withMethod("GET").withPath("/some/path").withQueryStringParameters(Parameter.param("parameterOneName", "parameterOneValue")).withBody(JsonBody.json("{ \"key\": \"some_value\" }")).withHeaders(new Header("name", "value")).withCookies(new Cookie("name", "[A-Z]{0,10}"))), Is.is((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"method\" : \"GET\",") + (NEW_LINE)) + "  \"path\" : \"/some/path\",") + (NEW_LINE)) + "  \"queryStringParameters\" : {") + (NEW_LINE)) + "    \"parameterOneName\" : [ \"parameterOneValue\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"name\" : [ \"value\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"cookies\" : {") + (NEW_LINE)) + "    \"name\" : \"[A-Z]{0,10}\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"JSON\",") + (NEW_LINE)) + "    \"json\" : \"{ \\\"key\\\": \\\"some_value\\\" }\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldReturnFormattedRequestWithXPathBodyInToString() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(HttpRequest.request().withMethod("GET").withPath("/some/path").withQueryStringParameters(Parameter.param("parameterOneName", "parameterOneValue")).withBody(XPathBody.xpath("//some/xml/path")).withHeaders(new Header("name", "value")).withCookies(new Cookie("name", "[A-Z]{0,10}"))), Is.is((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"method\" : \"GET\",") + (NEW_LINE)) + "  \"path\" : \"/some/path\",") + (NEW_LINE)) + "  \"queryStringParameters\" : {") + (NEW_LINE)) + "    \"parameterOneName\" : [ \"parameterOneValue\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"name\" : [ \"value\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"cookies\" : {") + (NEW_LINE)) + "    \"name\" : \"[A-Z]{0,10}\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"XPATH\",") + (NEW_LINE)) + "    \"xpath\" : \"//some/xml/path\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")));
    }
}

