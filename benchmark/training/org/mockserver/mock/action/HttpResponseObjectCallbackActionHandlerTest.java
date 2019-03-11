package org.mockserver.mock.action;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockserver.callback.WebSocketClientRegistry;
import org.mockserver.callback.WebSocketResponseCallback;
import org.mockserver.mock.HttpStateHandler;
import org.mockserver.model.HttpObjectCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.responsewriter.ResponseWriter;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpResponseObjectCallbackActionHandlerTest {
    @Test
    public void shouldHandleHttpRequests() {
        // given
        WebSocketClientRegistry mockWebSocketClientRegistry = Mockito.mock(WebSocketClientRegistry.class);
        HttpStateHandler mockHttpStateHandler = Mockito.mock(HttpStateHandler.class);
        HttpObjectCallback httpObjectCallback = new HttpObjectCallback().withClientId("some_clientId");
        HttpRequest request = HttpRequest.request().withBody("some_body");
        ResponseWriter mockResponseWriter = Mockito.mock(ResponseWriter.class);
        Mockito.when(mockHttpStateHandler.getWebSocketClientRegistry()).thenReturn(mockWebSocketClientRegistry);
        // when
        new HttpResponseObjectCallbackActionHandler(mockHttpStateHandler).handle(Mockito.mock(ActionHandler.class), httpObjectCallback, request, mockResponseWriter, true);
        // then
        Mockito.verify(mockWebSocketClientRegistry).registerResponseCallbackHandler(ArgumentMatchers.any(String.class), ArgumentMatchers.any(WebSocketResponseCallback.class));
        Mockito.verify(mockWebSocketClientRegistry).sendClientMessage(ArgumentMatchers.eq("some_clientId"), ArgumentMatchers.any(HttpRequest.class));
    }
}

