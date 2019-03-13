package org.mockserver.mock.action;


import HttpForward.Scheme.HTTP;
import HttpForward.Scheme.HTTPS;
import com.google.common.util.concurrent.SettableFuture;
import java.net.InetSocketAddress;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsSame;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockserver.client.NettyHttpClient;
import org.mockserver.model.HttpForward;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpForwardActionHandlerTest {
    private HttpForwardActionHandler httpForwardActionHandler;

    private NettyHttpClient mockHttpClient;

    @Test
    public void shouldHandleHttpRequests() {
        // given
        SettableFuture<HttpResponse> responseFuture = SettableFuture.create();
        HttpRequest httpRequest = HttpRequest.request();
        HttpForward httpForward = HttpForward.forward().withHost("some_host").withPort(1080).withScheme(HTTP);
        Mockito.when(mockHttpClient.sendRequest(httpRequest, new InetSocketAddress(httpForward.getHost(), httpForward.getPort()))).thenReturn(responseFuture);
        // when
        SettableFuture<HttpResponse> actualHttpResponse = httpForwardActionHandler.handle(httpForward, httpRequest).getHttpResponse();
        // then
        MatcherAssert.assertThat(actualHttpResponse, Is.is(IsSame.sameInstance(responseFuture)));
        Mockito.verify(mockHttpClient).sendRequest(httpRequest.withSecure(false), new InetSocketAddress(httpForward.getHost(), httpForward.getPort()));
    }

    @Test
    public void shouldHandleSecureHttpRequests() {
        // given
        SettableFuture<HttpResponse> httpResponse = SettableFuture.create();
        HttpRequest httpRequest = HttpRequest.request();
        HttpForward httpForward = HttpForward.forward().withHost("some_host").withPort(1080).withScheme(HTTPS);
        Mockito.when(mockHttpClient.sendRequest(httpRequest, new InetSocketAddress(httpForward.getHost(), httpForward.getPort()))).thenReturn(httpResponse);
        // when
        SettableFuture<HttpResponse> actualHttpResponse = httpForwardActionHandler.handle(httpForward, httpRequest).getHttpResponse();
        // then
        MatcherAssert.assertThat(actualHttpResponse, Is.is(IsSame.sameInstance(httpResponse)));
        Mockito.verify(mockHttpClient).sendRequest(httpRequest.withSecure(true), new InetSocketAddress(httpForward.getHost(), httpForward.getPort()));
    }
}

