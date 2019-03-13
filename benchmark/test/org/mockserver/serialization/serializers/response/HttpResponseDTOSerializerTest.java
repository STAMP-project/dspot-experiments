package org.mockserver.serialization.serializers.response;


import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.concurrent.TimeUnit;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.ConnectionOptions;
import org.mockserver.model.Cookie;
import org.mockserver.model.Delay;
import org.mockserver.model.Header;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.mockserver.serialization.ObjectMapperFactory;


public class HttpResponseDTOSerializerTest {
    @Test
    public void shouldReturnFormattedResponseWithNoFieldsSet() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new org.mockserver.serialization.model.HttpResponseDTO(HttpResponse.response())), Is.is("{ }"));
    }

    @Test
    public void shouldReturnFormattedResponseWithAllFieldsSet() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new org.mockserver.serialization.model.HttpResponseDTO(HttpResponse.response().withStatusCode(302).withReasonPhrase("randomReason").withBody("some_body").withHeaders(new Header("header_name", "header_value")).withCookies(new Cookie("cookie_name", "cookie_value")).withDelay(new Delay(TimeUnit.MICROSECONDS, 1)).withConnectionOptions(ConnectionOptions.connectionOptions().withSuppressContentLengthHeader(true).withContentLengthHeaderOverride(50).withSuppressConnectionHeader(true).withKeepAliveOverride(true).withCloseSocket(true)))), Is.is((((((((((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"statusCode\" : 302,") + (NEW_LINE)) + "  \"reasonPhrase\" : \"randomReason\",") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"header_name\" : [ \"header_value\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"cookies\" : {") + (NEW_LINE)) + "    \"cookie_name\" : \"cookie_value\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"body\" : \"some_body\",") + (NEW_LINE)) + "  \"delay\" : {") + (NEW_LINE)) + "    \"timeUnit\" : \"MICROSECONDS\",") + (NEW_LINE)) + "    \"value\" : 1") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"connectionOptions\" : {") + (NEW_LINE)) + "    \"suppressContentLengthHeader\" : true,") + (NEW_LINE)) + "    \"contentLengthHeaderOverride\" : 50,") + (NEW_LINE)) + "    \"suppressConnectionHeader\" : true,") + (NEW_LINE)) + "    \"keepAliveOverride\" : true,") + (NEW_LINE)) + "    \"closeSocket\" : true") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldReturnFormattedResponseWithJsonBodyInToString() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new org.mockserver.serialization.model.HttpResponseDTO(HttpResponse.response().withStatusCode(302).withBody(JsonBody.json("{ \"key\": \"some_value\" }")).withHeaders(new Header("header_name", "header_value")).withCookies(new Cookie("cookie_name", "cookie_value")))), Is.is((((((((((((((((((("{" + (NEW_LINE)) + "  \"statusCode\" : 302,") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"header_name\" : [ \"header_value\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"cookies\" : {") + (NEW_LINE)) + "    \"cookie_name\" : \"cookie_value\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"body\" : \"{ \\\"key\\\": \\\"some_value\\\" }\"") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldReturnFormattedResponseWithDefaultStatusCode() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new org.mockserver.serialization.model.HttpResponseDTO(HttpResponse.response().withStatusCode(200).withHeaders(new Header("header_name", "header_value")).withCookies(new Cookie("cookie_name", "cookie_value")))), Is.is((((((((((((((((("{" + (NEW_LINE)) + "  \"statusCode\" : 200,") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"header_name\" : [ \"header_value\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"cookies\" : {") + (NEW_LINE)) + "    \"cookie_name\" : \"cookie_value\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")));
    }
}

