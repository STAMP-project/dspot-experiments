package org.mockserver.serialization;


import java.io.IOException;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.Cookie;
import org.mockserver.model.Cookies;
import org.mockserver.model.Delay;
import org.mockserver.model.Header;
import org.mockserver.model.Headers;
import org.mockserver.model.NottableString;
import org.mockserver.model.Parameter;
import org.mockserver.model.Parameters;
import org.mockserver.model.StringBody;


/**
 *
 *
 * @author jamesdbloom
 */
public class WebSocketMessageSerializerTest {
    @Test
    public void shouldDeserializeCompleteResponse() throws IOException, ClassNotFoundException {
        // given
        String requestBytes = ((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"type\" : \"org.mockserver.model.HttpResponse\",") + (NEW_LINE)) + "  \"value\" : \"{") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"statusCode\\\" : 123,") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"headers\\\" : [ {") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "    \\\"name\\\" : \\\"someHeaderName\\\",") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "    \\\"values\\\" : [ \\\"someHeaderValue\\\" ]") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  } ],") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"cookies\\\" : [ {") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "    \\\"name\\\" : \\\"someCookieName\\\",") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "    \\\"value\\\" : \\\"someCookieValue\\\"") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  } ],") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"body\\\" : \\\"somebody\\\",") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"delay\\\" : {") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "    \\\"timeUnit\\\" : \\\"SECONDS\\\",") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "    \\\"value\\\" : 5") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  }") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "}\"") + (NEW_LINE)) + "}";
        // when
        Object httpResponse = deserialize(requestBytes);
        // then
        Assert.assertEquals(new HttpResponseDTO().setStatusCode(123).setBody(BodyWithContentTypeDTO.createDTO(StringBody.exact("somebody"))).setHeaders(new Headers().withEntries(Header.header("someHeaderName", "someHeaderValue"))).setCookies(new Cookies().withEntries(Cookie.cookie("someCookieName", "someCookieValue"))).setDelay(new DelayDTO(Delay.seconds(5))).buildObject(), httpResponse);
    }

    @Test
    public void shouldSerializeCompleteResponse() throws IOException {
        // when
        String jsonHttpResponse = new WebSocketMessageSerializer(new MockServerLogger()).serialize(new HttpResponseDTO().setStatusCode(123).setBody(BodyWithContentTypeDTO.createDTO(StringBody.exact("somebody"))).setHeaders(new Headers().withEntries(Header.header("someHeaderName", "someHeaderValue"))).setCookies(new Cookies().withEntries(Cookie.cookie("someCookieName", "someCookieValue"))).setDelay(new DelayDTO(Delay.minutes(1))).buildObject());
        // then
        Assert.assertEquals((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"type\" : \"org.mockserver.model.HttpResponse\",") + (NEW_LINE)) + "  \"value\" : \"{") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"statusCode\\\" : 123,") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"headers\\\" : {") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "    \\\"someHeaderName\\\" : [ \\\"someHeaderValue\\\" ]") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  },") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"cookies\\\" : {") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "    \\\"someCookieName\\\" : \\\"someCookieValue\\\"") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  },") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"body\\\" : \\\"somebody\\\",") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"delay\\\" : {") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "    \\\"timeUnit\\\" : \\\"MINUTES\\\",") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "    \\\"value\\\" : 1") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  }") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "}\"") + (NEW_LINE)) + "}"), jsonHttpResponse);
    }

    @Test
    public void shouldDeserializeCompleteRequest() throws IOException, ClassNotFoundException {
        // given
        String requestBytes = ((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"type\" : \"org.mockserver.model.HttpRequest\",") + (NEW_LINE)) + "  \"value\" : \"{") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"method\\\" : \\\"someMethod\\\",") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"path\\\" : \\\"somePath\\\",") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"queryStringParameters\\\" : {") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "    \\\"queryParameterName\\\" : [ \\\"queryParameterValue\\\" ]") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  },") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"headers\\\" : {") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "    \\\"someHeaderName\\\" : [ \\\"someHeaderValue\\\" ]") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  },") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"cookies\\\" : {") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "    \\\"someCookieName\\\" : \\\"someCookieValue\\\"") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  },") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"keepAlive\\\" : false,") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"secure\\\" : true,") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"body\\\" : \\\"somebody\\\"") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "}\"") + (NEW_LINE)) + "}";
        // when
        Object httpRequest = deserialize(requestBytes);
        // then
        Assert.assertEquals(new HttpRequestDTO().setMethod(NottableString.string("someMethod")).setPath(NottableString.string("somePath")).setQueryStringParameters(new Parameters().withEntries(Parameter.param("queryParameterName", "queryParameterValue"))).setBody(BodyDTO.createDTO(StringBody.exact("somebody"))).setHeaders(new Headers().withEntries(Header.header("someHeaderName", "someHeaderValue"))).setCookies(new Cookies().withEntries(Cookie.cookie("someCookieName", "someCookieValue"))).setSecure(true).setKeepAlive(false).buildObject(), httpRequest);
    }

    @Test
    public void shouldSerializeCompleteRequest() throws IOException {
        // when
        String jsonHttpRequest = new WebSocketMessageSerializer(new MockServerLogger()).serialize(new HttpRequestDTO().setMethod(NottableString.string("someMethod")).setPath(NottableString.string("somePath")).setQueryStringParameters(new Parameters().withEntries(Parameter.param("queryParameterName", "queryParameterValue"))).setBody(BodyDTO.createDTO(StringBody.exact("somebody"))).setHeaders(new Headers().withEntries(Header.header("someHeaderName", "someHeaderValue"))).setCookies(new Cookies().withEntries(Cookie.cookie("someCookieName", "someCookieValue"))).setSecure(true).setKeepAlive(false).buildObject());
        // then
        Assert.assertEquals((((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"type\" : \"org.mockserver.model.HttpRequest\",") + (NEW_LINE)) + "  \"value\" : \"{") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"method\\\" : \\\"someMethod\\\",") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"path\\\" : \\\"somePath\\\",") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"queryStringParameters\\\" : {") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "    \\\"queryParameterName\\\" : [ \\\"queryParameterValue\\\" ]") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  },") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"headers\\\" : {") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "    \\\"someHeaderName\\\" : [ \\\"someHeaderValue\\\" ]") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  },") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"cookies\\\" : {") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "    \\\"someCookieName\\\" : \\\"someCookieValue\\\"") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  },") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"keepAlive\\\" : false,") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"secure\\\" : true,") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "  \\\"body\\\" : \\\"somebody\\\"") + (StringEscapeUtils.escapeJava(NEW_LINE))) + "}\"") + (NEW_LINE)) + "}"), jsonHttpRequest);
    }
}

