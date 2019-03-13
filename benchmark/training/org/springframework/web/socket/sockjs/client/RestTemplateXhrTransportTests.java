/**
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.socket.sockjs.client;


import HttpMethod.POST;
import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.OK;
import StompCommand.SEND;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompEncoder;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;
import org.springframework.web.socket.sockjs.frame.SockJsFrame;


/**
 * Unit tests for {@link RestTemplateXhrTransport}.
 *
 * @author Rossen Stoyanchev
 */
public class RestTemplateXhrTransportTests {
    private static final Jackson2SockJsMessageCodec CODEC = new Jackson2SockJsMessageCodec();

    private final WebSocketHandler webSocketHandler = Mockito.mock(WebSocketHandler.class);

    @Test
    public void connectReceiveAndClose() throws Exception {
        String body = "o\n" + ("a[\"foo\"]\n" + "c[3000,\"Go away!\"]");
        ClientHttpResponse response = response(OK, body);
        connect(response);
        Mockito.verify(this.webSocketHandler).afterConnectionEstablished(ArgumentMatchers.any());
        Mockito.verify(this.webSocketHandler).handleMessage(ArgumentMatchers.any(), ArgumentMatchers.eq(new TextMessage("foo")));
        Mockito.verify(this.webSocketHandler).afterConnectionClosed(ArgumentMatchers.any(), ArgumentMatchers.eq(new CloseStatus(3000, "Go away!")));
        Mockito.verifyNoMoreInteractions(this.webSocketHandler);
    }

    @Test
    public void connectReceiveAndCloseWithPrelude() throws Exception {
        StringBuilder sb = new StringBuilder(2048);
        for (int i = 0; i < 2048; i++) {
            sb.append('h');
        }
        String body = ((((sb.toString()) + "\n") + "o\n") + "a[\"foo\"]\n") + "c[3000,\"Go away!\"]";
        ClientHttpResponse response = response(OK, body);
        connect(response);
        Mockito.verify(this.webSocketHandler).afterConnectionEstablished(ArgumentMatchers.any());
        Mockito.verify(this.webSocketHandler).handleMessage(ArgumentMatchers.any(), ArgumentMatchers.eq(new TextMessage("foo")));
        Mockito.verify(this.webSocketHandler).afterConnectionClosed(ArgumentMatchers.any(), ArgumentMatchers.eq(new CloseStatus(3000, "Go away!")));
        Mockito.verifyNoMoreInteractions(this.webSocketHandler);
    }

    @Test
    public void connectReceiveAndCloseWithStompFrame() throws Exception {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(SEND);
        accessor.setDestination("/destination");
        MessageHeaders headers = accessor.getMessageHeaders();
        Message<byte[]> message = MessageBuilder.createMessage("body".getBytes(StandardCharsets.UTF_8), headers);
        byte[] bytes = new StompEncoder().encode(message);
        TextMessage textMessage = new TextMessage(bytes);
        SockJsFrame frame = SockJsFrame.messageFrame(new Jackson2SockJsMessageCodec(), textMessage.getPayload());
        String body = (("o\n" + (frame.getContent())) + "\n") + "c[3000,\"Go away!\"]";
        ClientHttpResponse response = response(OK, body);
        connect(response);
        Mockito.verify(this.webSocketHandler).afterConnectionEstablished(ArgumentMatchers.any());
        Mockito.verify(this.webSocketHandler).handleMessage(ArgumentMatchers.any(), ArgumentMatchers.eq(textMessage));
        Mockito.verify(this.webSocketHandler).afterConnectionClosed(ArgumentMatchers.any(), ArgumentMatchers.eq(new CloseStatus(3000, "Go away!")));
        Mockito.verifyNoMoreInteractions(this.webSocketHandler);
    }

    @Test
    public void connectFailure() throws Exception {
        final HttpServerErrorException expected = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        RestOperations restTemplate = Mockito.mock(RestOperations.class);
        BDDMockito.given(restTemplate.execute(((URI) (ArgumentMatchers.any())), ArgumentMatchers.eq(POST), ArgumentMatchers.any(), ArgumentMatchers.any())).willThrow(expected);
        final CountDownLatch latch = new CountDownLatch(1);
        connect(restTemplate).addCallback(new org.springframework.util.concurrent.ListenableFutureCallback<WebSocketSession>() {
            @Override
            public void onSuccess(WebSocketSession result) {
            }

            @Override
            public void onFailure(Throwable ex) {
                if (ex == expected) {
                    latch.countDown();
                }
            }
        });
        Mockito.verifyNoMoreInteractions(this.webSocketHandler);
    }

    @Test
    public void errorResponseStatus() throws Exception {
        connect(response(OK, "o\n"), response(INTERNAL_SERVER_ERROR, "Oops"));
        Mockito.verify(this.webSocketHandler).afterConnectionEstablished(ArgumentMatchers.any());
        Mockito.verify(this.webSocketHandler).handleTransportError(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(this.webSocketHandler).afterConnectionClosed(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(this.webSocketHandler);
    }

    @Test
    public void responseClosedAfterDisconnected() throws Exception {
        String body = "o\n" + ("c[3000,\"Go away!\"]\n" + "a[\"foo\"]\n");
        ClientHttpResponse response = response(OK, body);
        connect(response);
        Mockito.verify(this.webSocketHandler).afterConnectionEstablished(ArgumentMatchers.any());
        Mockito.verify(this.webSocketHandler).afterConnectionClosed(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(this.webSocketHandler);
        Mockito.verify(response).close();
    }

    private static class TestRestTemplate extends RestTemplate {
        private Queue<ClientHttpResponse> responses = new LinkedBlockingDeque<>();

        private TestRestTemplate(ClientHttpResponse... responses) {
            this.responses.addAll(Arrays.asList(responses));
        }

        @Override
        public <T> T execute(URI url, HttpMethod method, @Nullable
        RequestCallback callback, @Nullable
        ResponseExtractor<T> extractor) throws RestClientException {
            try {
                extractor.extractData(this.responses.remove());
            } catch (Throwable t) {
                throw new RestClientException("Failed to invoke extractor", t);
            }
            return null;
        }
    }
}

