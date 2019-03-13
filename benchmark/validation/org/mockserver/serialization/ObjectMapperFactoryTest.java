package org.mockserver.serialization;


import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.matchers.Times;
import org.mockserver.model.Cookie;
import org.mockserver.model.Cookies;
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
public class ObjectMapperFactoryTest {
    @Test
    public void shouldDeserializeCompleteObject() throws IOException {
        // given
        String json = ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"httpRequest\" : {") + (NEW_LINE)) + "    \"method\" : \"someMethod\",") + (NEW_LINE)) + "    \"path\" : \"somePath\",") + (NEW_LINE)) + "    \"queryStringParameters\" : [ {") + (NEW_LINE)) + "      \"name\" : \"queryStringParameterNameOne\",") + (NEW_LINE)) + "      \"values\" : [ \"queryStringParameterValueOne_One\", \"queryStringParameterValueOne_Two\" ]") + (NEW_LINE)) + "    }, {") + (NEW_LINE)) + "      \"name\" : \"queryStringParameterNameTwo\",") + (NEW_LINE)) + "      \"values\" : [ \"queryStringParameterValueTwo_One\" ]") + (NEW_LINE)) + "    } ],") + (NEW_LINE)) + "    \"body\" : {") + (NEW_LINE)) + "      \"type\" : \"STRING\",") + (NEW_LINE)) + "      \"string\" : \"someBody\"") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    \"cookies\" : [ {") + (NEW_LINE)) + "      \"name\" : \"someCookieName\",") + (NEW_LINE)) + "      \"value\" : \"someCookieValue\"") + (NEW_LINE)) + "    } ],") + (NEW_LINE)) + "    \"headers\" : [ {") + (NEW_LINE)) + "      \"name\" : \"someHeaderName\",") + (NEW_LINE)) + "      \"values\" : [ \"someHeaderValue\" ]") + (NEW_LINE)) + "    } ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"httpResponse\" : {") + (NEW_LINE)) + "    \"statusCode\" : 304,") + (NEW_LINE)) + "    \"body\" : \"someBody\",") + (NEW_LINE)) + "    \"cookies\" : [ {") + (NEW_LINE)) + "      \"name\" : \"someCookieName\",") + (NEW_LINE)) + "      \"value\" : \"someCookieValue\"") + (NEW_LINE)) + "    } ],") + (NEW_LINE)) + "    \"headers\" : [ {") + (NEW_LINE)) + "      \"name\" : \"someHeaderName\",") + (NEW_LINE)) + "      \"values\" : [ \"someHeaderValue\" ]") + (NEW_LINE)) + "    } ],") + (NEW_LINE)) + "    \"delay\" : {") + (NEW_LINE)) + "      \"timeUnit\" : \"MICROSECONDS\",") + (NEW_LINE)) + "      \"value\" : 1") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"times\" : {") + (NEW_LINE)) + "    \"remainingTimes\" : 5,") + (NEW_LINE)) + "    \"unlimited\" : false") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}";
        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);
        // then
        Assert.assertEquals(new ExpectationDTO().setHttpRequest(new HttpRequestDTO().setMethod(NottableString.string("someMethod")).setPath(NottableString.string("somePath")).setQueryStringParameters(new Parameters().withEntries(Parameter.param("queryStringParameterNameOne", "queryStringParameterValueOne_One", "queryStringParameterValueOne_Two"), Parameter.param("queryStringParameterNameTwo", "queryStringParameterValueTwo_One"))).setBody(new StringBodyDTO(new StringBody("someBody"))).setHeaders(new Headers().withEntries(Header.header("someHeaderName", "someHeaderValue"))).setCookies(new Cookies().withEntries(Cookie.cookie("someCookieName", "someCookieValue")))).setHttpResponse(new HttpResponseDTO().setStatusCode(304).setBody(new StringBodyDTO(new StringBody("someBody"))).setHeaders(new Headers().withEntries(Header.header("someHeaderName", "someHeaderValue"))).setCookies(new Cookies().withEntries(Cookie.cookie("someCookieName", "someCookieValue"))).setDelay(new DelayDTO().setTimeUnit(TimeUnit.MICROSECONDS).setValue(1))).setTimes(new org.mockserver.serialization.model.TimesDTO(Times.exactly(5))), expectationDTO);
    }
}

