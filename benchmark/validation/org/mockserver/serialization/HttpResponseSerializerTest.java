package org.mockserver.serialization;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockserver.model.Cookie;
import org.mockserver.model.Header;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.StringBody;
import org.mockserver.serialization.model.BodyWithContentTypeDTO;
import org.mockserver.serialization.model.HttpResponseDTO;
import org.mockserver.validator.jsonschema.JsonSchemaHttpResponseValidator;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpResponseSerializerTest {
    private final HttpResponse fullHttpResponse = new HttpResponse().withStatusCode(123).withReasonPhrase("randomPhrase").withBody(StringBody.exact("somebody")).withHeaders(Header.header("headerName", "headerValue")).withCookies(Cookie.cookie("cookieName", "cookieValue")).withDelay(new Delay(TimeUnit.MICROSECONDS, 3));

    private final HttpResponseDTO fullHttpResponseDTO = new HttpResponseDTO().setStatusCode(123).setReasonPhrase("randomPhrase").setBody(BodyWithContentTypeDTO.createDTO(StringBody.exact("somebody"))).setHeaders(new Headers().withEntries(Header.header("headerName", "headerValue"))).setCookies(new Cookies().withEntries(Cookie.cookie("cookieName", "cookieValue"))).setDelay(new org.mockserver.serialization.model.DelayDTO(new Delay(TimeUnit.MICROSECONDS, 3)));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectWriter objectWriter;

    @Mock
    private JsonSchemaHttpResponseValidator httpResponseValidator;

    @InjectMocks
    private HttpResponseSerializer httpResponseSerializer;

    @Test
    public void deserialize() throws IOException {
        // given
        Mockito.when(httpResponseValidator.isValid(ArgumentMatchers.eq("responseBytes"))).thenReturn("");
        Mockito.when(objectMapper.readValue(ArgumentMatchers.eq("responseBytes"), ArgumentMatchers.same(HttpResponseDTO.class))).thenReturn(fullHttpResponseDTO);
        // when
        HttpResponse httpResponse = httpResponseSerializer.deserialize("responseBytes");
        // then
        Assert.assertEquals(fullHttpResponse, httpResponse);
    }

    @Test
    public void serialize() throws IOException {
        // given
        Mockito.when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        // when
        httpResponseSerializer.serialize(fullHttpResponse);
        // then
        Mockito.verify(objectMapper).writerWithDefaultPrettyPrinter();
        Mockito.verify(objectWriter).writeValueAsString(fullHttpResponseDTO);
    }

    @Test
    public void shouldSerializeArray() throws IOException {
        // given
        Mockito.when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        // when
        httpResponseSerializer.serialize(new HttpResponse[]{ fullHttpResponse, fullHttpResponse });
        // then
        Mockito.verify(objectMapper).writerWithDefaultPrettyPrinter();
        Mockito.verify(objectWriter).writeValueAsString(new HttpResponseDTO[]{ fullHttpResponseDTO, fullHttpResponseDTO });
    }
}

