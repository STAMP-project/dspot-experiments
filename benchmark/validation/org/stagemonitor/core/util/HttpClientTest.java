package org.stagemonitor.core.util;


import HttpServletResponse.SC_UNAUTHORIZED;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.core.util.http.HttpRequestBuilder;
import org.stagemonitor.core.util.http.StatusCodeResponseHandler;


public class HttpClientTest extends AbstractEmbeddedServerTest {
    private HttpClient httpClient = new HttpClient();

    @Test
    public void testBasicAuth() throws Exception {
        final boolean[] handled = new boolean[]{ false };
        startWithHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                baseRequest.setHandled(true);
                Assert.assertEquals("Basic dXNlcjpwYXNz", request.getHeader("Authorization"));
                assertThat(request.getQueryString()).isEqualTo("bar=baz");
                handled[0] = true;
            }
        });
        Assert.assertEquals(Integer.valueOf(200), httpClient.send(HttpRequestBuilder.<Integer>forUrl((("http://user:pass@localhost:" + (getPort())) + "/foo?bar=baz")).successHandler(new StatusCodeResponseHandler()).build()));
        Assert.assertTrue(handled[0]);
    }

    @Test
    public void testBasicAuthException() throws Exception {
        startWithHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                baseRequest.setHandled(true);
                response.sendError(SC_UNAUTHORIZED);
            }
        });
        final String url = ("http://user:pass@localhost:" + (getPort())) + "/foo?bar=baz";
        final String urlWithoutAuth = ("http://localhost:" + (getPort())) + "/foo?bar=baz";
        httpClient.send(HttpRequestBuilder.<Integer>forUrl(url).successHandler(( httpRequest, is, statusCode, e) -> {
            fail();
            return null;
        }).errorHandler(( httpRequest, is, statusCode, e) -> {
            assertThat(statusCode).isEqualTo(401);
            assertThat(e.getMessage()).doesNotContain(url).contains(urlWithoutAuth);
            return null;
        }).build());
    }
}

