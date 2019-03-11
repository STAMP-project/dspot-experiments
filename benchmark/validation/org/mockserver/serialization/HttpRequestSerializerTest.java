package org.mockserver.serialization;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.NottableString;
import org.mockserver.serialization.model.HttpRequestDTO;
import org.mockserver.validator.jsonschema.JsonSchemaHttpRequestValidator;

import static org.mockserver.model.Cookie.cookie;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.Parameter.param;
import static org.mockserver.model.StringBody.exact;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpRequestSerializerTest {
    private final HttpRequest fullHttpRequest = new HttpRequest().withMethod("GET").withPath("somepath").withQueryStringParameters(new Parameter("queryStringParameterNameOne", "queryStringParameterValueOne_One", "queryStringParameterValueOne_Two"), new Parameter("queryStringParameterNameTwo", "queryStringParameterValueTwo_One")).withBody(new StringBody("someBody")).withHeaders(new Header("headerName", "headerValue")).withCookies(new Cookie("cookieName", "cookieValue")).withSecure(true).withKeepAlive(true);

    private final HttpRequestDTO fullHttpRequestDTO = new HttpRequestDTO().setMethod(NottableString.string("GET")).setPath(NottableString.string("somepath")).setQueryStringParameters(new Parameters().withEntries(org.mockserver.model.Parameter.param("queryStringParameterNameOne", "queryStringParameterValueOne_One", "queryStringParameterValueOne_Two"), param("queryStringParameterNameTwo", "queryStringParameterValueTwo_One"))).setBody(new org.mockserver.serialization.model.StringBodyDTO(exact("someBody"))).setHeaders(new Headers().withEntries(header("headerName", "headerValue"))).setCookies(new Cookies().withEntries(cookie("cookieName", "cookieValue"))).setSecure(true).setKeepAlive(true);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectWriter objectWriter;

    @Mock
    private JsonSchemaHttpRequestValidator httpRequestValidator;

    @InjectMocks
    private HttpRequestSerializer httpRequestSerializer;

    @Test
    public void deserialize() throws IOException {
        // given
        Mockito.when(httpRequestValidator.isValid(ArgumentMatchers.eq("requestBytes"))).thenReturn("");
        Mockito.when(objectMapper.readValue(ArgumentMatchers.eq("requestBytes"), ArgumentMatchers.same(HttpRequestDTO.class))).thenReturn(fullHttpRequestDTO);
        // when
        HttpRequest httpRequest = httpRequestSerializer.deserialize("requestBytes");
        // then
        Assert.assertEquals(fullHttpRequest, httpRequest);
    }

    @Test
    public void serialize() throws IOException {
        // given
        Mockito.when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        // when
        httpRequestSerializer.serialize(fullHttpRequest);
        // then
        Mockito.verify(objectMapper).writerWithDefaultPrettyPrinter();
        Mockito.verify(objectWriter).writeValueAsString(fullHttpRequestDTO);
    }

    @Test
    public void shouldSerializeArray() throws IOException {
        // given
        Mockito.when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        // when
        httpRequestSerializer.serialize(new HttpRequest[]{ fullHttpRequest, fullHttpRequest });
        // then
        Mockito.verify(objectMapper).writerWithDefaultPrettyPrinter();
        Mockito.verify(objectWriter).writeValueAsString(new HttpRequestDTO[]{ fullHttpRequestDTO, fullHttpRequestDTO });
    }
}

