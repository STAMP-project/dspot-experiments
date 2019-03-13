package org.mockserver.server;


import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.mappers.MockServerResponseToHttpServletResponseEncoder;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.mockserver.servlet.responsewriter.ServletResponseWriter;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 *
 *
 * @author jamesdbloom
 */
public class ServletResponseWriterTest {
    @Mock
    private MockServerResponseToHttpServletResponseEncoder mockServerResponseToHttpServletResponseEncoder;

    @InjectMocks
    private ServletResponseWriter servletResponseWriter;

    private MockHttpServletResponse httpServletResponse;

    @Test
    public void shouldWriteBasicResponse() {
        // given
        HttpRequest request = HttpRequest.request("some_request");
        HttpResponse response = HttpResponse.response("some_response");
        // when
        servletResponseWriter.writeResponse(request, response, false);
        // then
        Mockito.verify(mockServerResponseToHttpServletResponseEncoder).mapMockServerResponseToHttpServletResponse(HttpResponse.response("some_response").withHeader("connection", "close"), httpServletResponse);
    }

    @Test
    public void shouldWriteContentTypeForStringBody() {
        // given
        HttpRequest request = HttpRequest.request("some_request");
        HttpResponse response = HttpResponse.response().withBody("some_response", StandardCharsets.UTF_8);
        // when
        servletResponseWriter.writeResponse(request, response, false);
        // then
        Mockito.verify(mockServerResponseToHttpServletResponseEncoder).mapMockServerResponseToHttpServletResponse(HttpResponse.response().withHeader("connection", "close").withBody("some_response", StandardCharsets.UTF_8), httpServletResponse);
    }

    @Test
    public void shouldWriteContentTypeForJsonBody() {
        // given
        HttpRequest request = HttpRequest.request("some_request");
        HttpResponse response = HttpResponse.response().withBody(JsonBody.json("some_response"));
        // when
        servletResponseWriter.writeResponse(request, response, false);
        // then
        Mockito.verify(mockServerResponseToHttpServletResponseEncoder).mapMockServerResponseToHttpServletResponse(HttpResponse.response().withHeader("connection", "close").withBody(JsonBody.json("some_response")), httpServletResponse);
    }

    @Test
    public void shouldWriteNullResponse() {
        // given
        HttpRequest request = HttpRequest.request("some_request");
        // when
        servletResponseWriter.writeResponse(request, null, false);
        // then
        Mockito.verify(mockServerResponseToHttpServletResponseEncoder).mapMockServerResponseToHttpServletResponse(HttpResponse.notFoundResponse().withHeader("connection", "close"), httpServletResponse);
    }

    @Test
    public void shouldWriteAddCORSHeaders() {
        boolean enableCORSForAllResponses = ConfigurationProperties.enableCORSForAllResponses();
        try {
            // given
            ConfigurationProperties.enableCORSForAllResponses(true);
            HttpRequest request = HttpRequest.request("some_request");
            HttpResponse response = HttpResponse.response("some_response");
            // when
            servletResponseWriter.writeResponse(request, response, false);
            // then
            Mockito.verify(mockServerResponseToHttpServletResponseEncoder).mapMockServerResponseToHttpServletResponse(response.withHeader("Access-Control-Allow-Origin", "*").withHeader("Access-Control-Allow-Methods", "CONNECT, DELETE, GET, HEAD, OPTIONS, POST, PUT, PATCH, TRACE").withHeader("Access-Control-Allow-Headers", "Allow, Content-Encoding, Content-Length, Content-Type, ETag, Expires, Last-Modified, Location, Server, Vary, Authorization").withHeader("Access-Control-Expose-Headers", "Allow, Content-Encoding, Content-Length, Content-Type, ETag, Expires, Last-Modified, Location, Server, Vary, Authorization").withHeader("Access-Control-Max-Age", "300").withHeader("X-CORS", "MockServer CORS support enabled by default, to disable ConfigurationProperties.enableCORSForAPI(false) or -Dmockserver.enableCORSForAPI=false").withHeader("connection", "close"), httpServletResponse);
        } finally {
            ConfigurationProperties.enableCORSForAllResponses(enableCORSForAllResponses);
        }
    }
}

