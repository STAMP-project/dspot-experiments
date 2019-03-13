package org.mockserver.mock.action;


import com.google.common.util.concurrent.SettableFuture;
import java.net.InetSocketAddress;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockserver.client.NettyHttpClient;
import org.mockserver.model.HttpClassCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpForwardClassCallbackActionHandlerTest {
    private NettyHttpClient mockHttpClient;

    private HttpForwardClassCallbackActionHandler httpForwardClassCallbackActionHandler;

    @Test
    public void shouldHandleInvalidClass() throws Exception {
        // given
        SettableFuture<HttpResponse> httpResponse = SettableFuture.create();
        httpResponse.set(HttpResponse.notFoundResponse());
        HttpClassCallback httpClassCallback = HttpClassCallback.callback("org.mockserver.mock.action.FooBar");
        // when
        SettableFuture<HttpResponse> actualHttpRequest = httpForwardClassCallbackActionHandler.handle(httpClassCallback, HttpRequest.request().withBody("some_body")).getHttpResponse();
        // then
        MatcherAssert.assertThat(actualHttpRequest.get(), Is.is(HttpResponse.notFoundResponse()));
        Mockito.verifyZeroInteractions(mockHttpClient);
    }

    @Test
    public void shouldHandleValidLocalClass() throws Exception {
        // given
        SettableFuture<HttpResponse> httpResponse = SettableFuture.create();
        httpResponse.set(HttpResponse.response("some_response_body"));
        Mockito.when(mockHttpClient.sendRequest(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.isNull(InetSocketAddress.class))).thenReturn(httpResponse);
        HttpClassCallback httpClassCallback = HttpClassCallback.callback("org.mockserver.mock.action.HttpForwardClassCallbackActionHandlerTest$TestCallback");
        // when
        SettableFuture<HttpResponse> actualHttpRequest = httpForwardClassCallbackActionHandler.handle(httpClassCallback, HttpRequest.request().withBody("some_body")).getHttpResponse();
        // then
        MatcherAssert.assertThat(actualHttpRequest.get(), Is.is(httpResponse.get()));
        Mockito.verify(mockHttpClient).sendRequest(HttpRequest.request("some_path"), null);
    }

    public static class TestCallback implements ExpectationForwardCallback {
        @Override
        public HttpRequest handle(HttpRequest httpRequest) {
            return HttpRequest.request("some_path");
        }
    }
}

