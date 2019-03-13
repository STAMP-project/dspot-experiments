package org.mockserver.serialization.serializers.request;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.Cookie;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.JsonBody;
import org.mockserver.model.JsonPathBody;
import org.mockserver.model.JsonSchemaBody;
import org.mockserver.model.Parameter;
import org.mockserver.model.ParameterBody;
import org.mockserver.model.RegexBody;
import org.mockserver.model.XPathBody;
import org.mockserver.model.XmlBody;
import org.mockserver.model.XmlSchemaBody;
import org.mockserver.serialization.ObjectMapperFactory;


public class HttpRequestDTOSerializerTest {
    @Test
    public void shouldReturnFormattedRequestWithNoFieldsSet() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new org.mockserver.serialization.model.HttpRequestDTO(HttpRequest.request())), Is.is("{ }"));
    }

    @Test
    public void shouldReturnFormattedRequestWithAllFieldsSet() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new org.mockserver.serialization.model.HttpRequestDTO(HttpRequest.request().withMethod("GET").withPath("/some/path").withQueryStringParameters(Parameter.param("parameterOneName", "parameterOneValue")).withBody("some_body").withHeaders(new Header("name", "value")).withCookies(new Cookie("name", "[A-Z]{0,10}")).withKeepAlive(true).withSecure(true))), Is.is((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"method\" : \"GET\",") + (NEW_LINE)) + "  \"path\" : \"/some/path\",") + (NEW_LINE)) + "  \"queryStringParameters\" : {") + (NEW_LINE)) + "    \"parameterOneName\" : [ \"parameterOneValue\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"name\" : [ \"value\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"cookies\" : {") + (NEW_LINE)) + "    \"name\" : \"[A-Z]{0,10}\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"keepAlive\" : true,") + (NEW_LINE)) + "  \"secure\" : true,") + (NEW_LINE)) + "  \"body\" : \"some_body\"") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldReturnFormattedRequestWithJsonBodyInToString() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new org.mockserver.serialization.model.HttpRequestDTO(HttpRequest.request().withMethod("GET").withPath("/some/path").withQueryStringParameters(Parameter.param("parameterOneName", "parameterOneValue")).withBody(JsonBody.json("{ \"key\": \"some_value\" }")).withHeaders(new Header("name", "value")).withCookies(new Cookie("name", "[A-Z]{0,10}")))), Is.is((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"method\" : \"GET\",") + (NEW_LINE)) + "  \"path\" : \"/some/path\",") + (NEW_LINE)) + "  \"queryStringParameters\" : {") + (NEW_LINE)) + "    \"parameterOneName\" : [ \"parameterOneValue\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"name\" : [ \"value\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"cookies\" : {") + (NEW_LINE)) + "    \"name\" : \"[A-Z]{0,10}\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"JSON\",") + (NEW_LINE)) + "    \"json\" : \"{ \\\"key\\\": \\\"some_value\\\" }\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldReturnFormattedRequestWithJsonSchemaBodyInToString() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new JsonSchemaBody("{\"type\": \"object\", \"properties\": {\"id\": {\"type\": \"integer\"}}, \"required\": [\"id\"]}")), Is.is((((((("{" + (NEW_LINE)) + "  \"type\" : \"JSON_SCHEMA\",") + (NEW_LINE)) + "  \"jsonSchema\" : \"{\\\"type\\\": \\\"object\\\", \\\"properties\\\": {\\\"id\\\": {\\\"type\\\": \\\"integer\\\"}}, \\\"required\\\": [\\\"id\\\"]}\"") + (NEW_LINE)) + "}")));
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new org.mockserver.serialization.model.HttpRequestDTO(HttpRequest.request().withMethod("GET").withPath("/some/path").withQueryStringParameters(Parameter.param("parameterOneName", "parameterOneValue")).withBody(JsonSchemaBody.jsonSchema("{\"type\": \"object\", \"properties\": {\"id\": {\"type\": \"integer\"}}, \"required\": [\"id\"]}")).withHeaders(new Header("name", "value")).withCookies(new Cookie("name", "[A-Z]{0,10}")))), Is.is((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"method\" : \"GET\",") + (NEW_LINE)) + "  \"path\" : \"/some/path\",") + (NEW_LINE)) + "  \"queryStringParameters\" : {") + (NEW_LINE)) + "    \"parameterOneName\" : [ \"parameterOneValue\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"name\" : [ \"value\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"cookies\" : {") + (NEW_LINE)) + "    \"name\" : \"[A-Z]{0,10}\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"JSON_SCHEMA\",") + (NEW_LINE)) + "    \"jsonSchema\" : \"{\\\"type\\\": \\\"object\\\", \\\"properties\\\": {\\\"id\\\": {\\\"type\\\": \\\"integer\\\"}}, \\\"required\\\": [\\\"id\\\"]}\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldReturnFormattedRequestWithJsonPathBodyInToString() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new org.mockserver.serialization.model.HttpRequestDTO(HttpRequest.request().withMethod("GET").withPath("/some/path").withQueryStringParameters(Parameter.param("parameterOneName", "parameterOneValue")).withBody(JsonPathBody.jsonPath("$..book[?(@.price <= $['expensive'])]")).withHeaders(new Header("name", "value")).withCookies(new Cookie("name", "[A-Z]{0,10}")))), Is.is((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"method\" : \"GET\",") + (NEW_LINE)) + "  \"path\" : \"/some/path\",") + (NEW_LINE)) + "  \"queryStringParameters\" : {") + (NEW_LINE)) + "    \"parameterOneName\" : [ \"parameterOneValue\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"name\" : [ \"value\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"cookies\" : {") + (NEW_LINE)) + "    \"name\" : \"[A-Z]{0,10}\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"JSON_PATH\",") + (NEW_LINE)) + "    \"jsonPath\" : \"$..book[?(@.price <= $[\'expensive\'])]\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldReturnFormattedRequestWithXmlBodyInToString() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new org.mockserver.serialization.model.HttpRequestDTO(HttpRequest.request().withMethod("GET").withPath("/some/path").withQueryStringParameters(Parameter.param("parameterOneName", "parameterOneValue")).withBody(XmlBody.xml("<some><xml></xml></some>")).withHeaders(new Header("name", "value")).withCookies(new Cookie("name", "[A-Z]{0,10}")))), Is.is((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"method\" : \"GET\",") + (NEW_LINE)) + "  \"path\" : \"/some/path\",") + (NEW_LINE)) + "  \"queryStringParameters\" : {") + (NEW_LINE)) + "    \"parameterOneName\" : [ \"parameterOneValue\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"name\" : [ \"value\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"cookies\" : {") + (NEW_LINE)) + "    \"name\" : \"[A-Z]{0,10}\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"XML\",") + (NEW_LINE)) + "    \"xml\" : \"<some><xml></xml></some>\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldReturnFormattedRequestWithXmlSchemaBodyInToString() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new XmlSchemaBody("{\"type\": \"object\", \"properties\": {\"id\": {\"type\": \"integer\"}}, \"required\": [\"id\"]}")), Is.is((((((("{" + (NEW_LINE)) + "  \"type\" : \"XML_SCHEMA\",") + (NEW_LINE)) + "  \"xmlSchema\" : \"{\\\"type\\\": \\\"object\\\", \\\"properties\\\": {\\\"id\\\": {\\\"type\\\": \\\"integer\\\"}}, \\\"required\\\": [\\\"id\\\"]}\"") + (NEW_LINE)) + "}")));
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new org.mockserver.serialization.model.HttpRequestDTO(HttpRequest.request().withMethod("GET").withPath("/some/path").withQueryStringParameters(Parameter.param("parameterOneName", "parameterOneValue")).withBody(XmlSchemaBody.xmlSchema("{\"type\": \"object\", \"properties\": {\"id\": {\"type\": \"integer\"}}, \"required\": [\"id\"]}")).withHeaders(new Header("name", "value")).withCookies(new Cookie("name", "[A-Z]{0,10}")))), Is.is((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"method\" : \"GET\",") + (NEW_LINE)) + "  \"path\" : \"/some/path\",") + (NEW_LINE)) + "  \"queryStringParameters\" : {") + (NEW_LINE)) + "    \"parameterOneName\" : [ \"parameterOneValue\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"name\" : [ \"value\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"cookies\" : {") + (NEW_LINE)) + "    \"name\" : \"[A-Z]{0,10}\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"XML_SCHEMA\",") + (NEW_LINE)) + "    \"xmlSchema\" : \"{\\\"type\\\": \\\"object\\\", \\\"properties\\\": {\\\"id\\\": {\\\"type\\\": \\\"integer\\\"}}, \\\"required\\\": [\\\"id\\\"]}\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldReturnFormattedRequestWithXPathBodyInToString() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new org.mockserver.serialization.model.HttpRequestDTO(HttpRequest.request().withMethod("GET").withPath("/some/path").withQueryStringParameters(Parameter.param("parameterOneName", "parameterOneValue")).withBody(XPathBody.xpath("//some/xml/path")).withHeaders(new Header("name", "value")).withCookies(new Cookie("name", "[A-Z]{0,10}")))), Is.is((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"method\" : \"GET\",") + (NEW_LINE)) + "  \"path\" : \"/some/path\",") + (NEW_LINE)) + "  \"queryStringParameters\" : {") + (NEW_LINE)) + "    \"parameterOneName\" : [ \"parameterOneValue\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"name\" : [ \"value\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"cookies\" : {") + (NEW_LINE)) + "    \"name\" : \"[A-Z]{0,10}\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"XPATH\",") + (NEW_LINE)) + "    \"xpath\" : \"//some/xml/path\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldReturnFormattedRequestWithRegexBodyInToString() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new org.mockserver.serialization.model.HttpRequestDTO(HttpRequest.request().withMethod("GET").withPath("/some/path").withQueryStringParameters(Parameter.param("parameterOneName", "parameterOneValue")).withBody(RegexBody.regex("[a-z]{1,3}")).withHeaders(new Header("name", "value")).withCookies(new Cookie("name", "[A-Z]{0,10}")))), Is.is((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"method\" : \"GET\",") + (NEW_LINE)) + "  \"path\" : \"/some/path\",") + (NEW_LINE)) + "  \"queryStringParameters\" : {") + (NEW_LINE)) + "    \"parameterOneName\" : [ \"parameterOneValue\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"name\" : [ \"value\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"cookies\" : {") + (NEW_LINE)) + "    \"name\" : \"[A-Z]{0,10}\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"REGEX\",") + (NEW_LINE)) + "    \"regex\" : \"[a-z]{1,3}\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldReturnFormattedRequestWithParameterBodyInToString() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new org.mockserver.serialization.model.HttpRequestDTO(HttpRequest.request().withMethod("GET").withPath("/some/path").withQueryStringParameters(Parameter.param("parameterOneName", "parameterOneValue")).withBody(ParameterBody.params(Parameter.param("queryStringParameterOneName", "queryStringParameterOneValueOne", "queryStringParameterOneValueTwo"), Parameter.param("queryStringParameterTwoName", "queryStringParameterTwoValue"))).withHeaders(new Header("name", "value")).withCookies(new Cookie("name", "[A-Z]{0,10}")))), Is.is((((((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"method\" : \"GET\",") + (NEW_LINE)) + "  \"path\" : \"/some/path\",") + (NEW_LINE)) + "  \"queryStringParameters\" : {") + (NEW_LINE)) + "    \"parameterOneName\" : [ \"parameterOneValue\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"name\" : [ \"value\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"cookies\" : {") + (NEW_LINE)) + "    \"name\" : \"[A-Z]{0,10}\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "    \"parameters\" : {") + (NEW_LINE)) + "      \"queryStringParameterOneName\" : [ \"queryStringParameterOneValueOne\", \"queryStringParameterOneValueTwo\" ],") + (NEW_LINE)) + "      \"queryStringParameterTwoName\" : [ \"queryStringParameterTwoValue\" ]") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")));
    }
}

