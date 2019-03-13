package org.mockserver.serialization;


import HttpForward.Scheme.HTTPS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;
import org.mockserver.mock.Expectation;
import org.mockserver.model.Cookie;
import org.mockserver.model.Header;
import org.mockserver.model.HttpForward;
import org.mockserver.model.NottableString;
import org.mockserver.model.Parameter;
import org.mockserver.model.StringBody;
import org.mockserver.validator.jsonschema.JsonSchemaExpectationValidator;


/**
 *
 *
 * @author jamesdbloom
 */
public class ExpectationWithForwardSerializerTest {
    private final Expectation fullExpectation = new Expectation(new HttpRequest().withMethod("GET").withPath("somePath").withQueryStringParameters(Parameter.param("queryParameterName", "queryParameterValue")).withBody("somebody").withHeaders(Header.header("headerName", "headerValue")).withCookies(Cookie.cookie("cookieName", "cookieValue")), Times.once(), TimeToLive.exactly(TimeUnit.HOURS, 2L)).thenForward(HttpForward.forward().withHost("some_host").withPort(9090).withScheme(HTTPS));

    private final ExpectationDTO fullExpectationDTO = new ExpectationDTO().setHttpRequest(new HttpRequestDTO().setMethod(NottableString.string("GET")).setPath(NottableString.string("somePath")).setQueryStringParameters(new Parameters().withEntries(Parameter.param("queryParameterName", "queryParameterValue"))).setBody(BodyDTO.createDTO(StringBody.exact("somebody"))).setHeaders(new Headers().withEntries(Header.header("headerName", "headerValue"))).setCookies(new Cookies().withEntries(Cookie.cookie("cookieName", "cookieValue")))).setHttpForward(new HttpForwardDTO(new HttpForward().withHost("some_host").withPort(9090).withScheme(HTTPS))).setTimes(new org.mockserver.serialization.model.TimesDTO(Times.once())).setTimeToLive(new TimeToLiveDTO(TimeToLive.exactly(TimeUnit.HOURS, 2L)));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectWriter objectWriter;

    @Mock
    private JsonArraySerializer jsonArraySerializer;

    @Mock
    private JsonSchemaExpectationValidator expectationValidator;

    @InjectMocks
    private ExpectationSerializer expectationSerializer = new ExpectationSerializer(new MockServerLogger());

    @Test
    public void shouldSerializeObject() throws IOException {
        // given
        Mockito.when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        // when
        expectationSerializer.serialize(fullExpectation);
        // then
        Mockito.verify(objectMapper).writerWithDefaultPrettyPrinter();
        Mockito.verify(objectWriter).writeValueAsString(fullExpectationDTO);
    }

    @Test
    public void shouldSerializeArray() throws IOException {
        // given
        Mockito.when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        // when
        expectationSerializer.serialize(new Expectation[]{ fullExpectation, fullExpectation });
        // then
        Mockito.verify(objectMapper).writerWithDefaultPrettyPrinter();
        Mockito.verify(objectWriter).writeValueAsString(new ExpectationDTO[]{ fullExpectationDTO, fullExpectationDTO });
    }

    @Test
    public void shouldDeserializeObject() throws IOException {
        // given
        Mockito.when(objectMapper.readValue(ArgumentMatchers.eq("requestBytes"), ArgumentMatchers.same(ExpectationDTO.class))).thenReturn(fullExpectationDTO);
        Mockito.when(expectationValidator.isValid("requestBytes")).thenReturn("");
        // when
        Expectation expectation = expectationSerializer.deserialize("requestBytes");
        // then
        Assert.assertThat(expectation, Is.is(fullExpectation));
    }

    @Test
    public void shouldDeserializeArray() throws IOException {
        // given
        Mockito.when(jsonArraySerializer.returnJSONObjects("requestBytes")).thenReturn(Arrays.asList("requestBytes", "requestBytes"));
        Mockito.when(expectationValidator.isValid("requestBytes")).thenReturn("");
        Mockito.when(objectMapper.readValue(ArgumentMatchers.eq("requestBytes"), ArgumentMatchers.same(ExpectationDTO.class))).thenReturn(fullExpectationDTO);
        // when
        Expectation[] expectations = expectationSerializer.deserializeArray("requestBytes");
        // then
        Assert.assertArrayEquals(new Expectation[]{ fullExpectation, fullExpectation }, expectations);
    }

    @Test
    public void shouldDeserializeObjectWithError() throws IOException {
        // given
        Mockito.when(objectMapper.readValue(ArgumentMatchers.eq("requestBytes"), ArgumentMatchers.same(ExpectationDTO.class))).thenReturn(fullExpectationDTO);
        Mockito.when(expectationValidator.isValid("requestBytes")).thenReturn("an error");
        // then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("an error");
        // when
        expectationSerializer.deserialize("requestBytes");
    }

    @Test
    public void shouldDeserializeArrayWithError() throws IOException {
        // given
        Mockito.when(jsonArraySerializer.returnJSONObjects("requestBytes")).thenReturn(Arrays.asList("requestBytes", "requestBytes"));
        Mockito.when(expectationValidator.isValid("requestBytes")).thenReturn("an error");
        Mockito.when(objectMapper.readValue(ArgumentMatchers.eq("requestBytes"), ArgumentMatchers.same(ExpectationDTO.class))).thenReturn(fullExpectationDTO);
        // then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(((((((("" + "[") + (NEW_LINE)) + "  an error,") + (NEW_LINE)) + "  an error") + (NEW_LINE)) + "]"));
        // when
        expectationSerializer.deserializeArray("requestBytes");
    }
}

