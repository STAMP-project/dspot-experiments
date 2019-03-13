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


import MediaType.APPLICATION_JSON;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;


/**
 * Unit tests for
 * {@link org.springframework.web.socket.sockjs.client.AbstractXhrTransport}.
 *
 * @author Rossen Stoyanchev
 */
public class XhrTransportTests {
    @Test
    public void infoResponse() throws Exception {
        XhrTransportTests.TestXhrTransport transport = new XhrTransportTests.TestXhrTransport();
        transport.infoResponseToReturn = new ResponseEntity("body", HttpStatus.OK);
        Assert.assertEquals("body", executeInfoRequest(new URI("http://example.com/info"), null));
    }

    @Test(expected = HttpServerErrorException.class)
    public void infoResponseError() throws Exception {
        XhrTransportTests.TestXhrTransport transport = new XhrTransportTests.TestXhrTransport();
        transport.infoResponseToReturn = new ResponseEntity("body", HttpStatus.BAD_REQUEST);
        Assert.assertEquals("body", executeInfoRequest(new URI("http://example.com/info"), null));
    }

    @Test
    public void sendMessage() throws Exception {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("foo", "bar");
        requestHeaders.setContentType(APPLICATION_JSON);
        XhrTransportTests.TestXhrTransport transport = new XhrTransportTests.TestXhrTransport();
        transport.sendMessageResponseToReturn = new ResponseEntity(HttpStatus.NO_CONTENT);
        URI url = new URI("http://example.com");
        transport.executeSendRequest(url, requestHeaders, new TextMessage("payload"));
        Assert.assertEquals(2, transport.actualSendRequestHeaders.size());
        Assert.assertEquals("bar", transport.actualSendRequestHeaders.getFirst("foo"));
        Assert.assertEquals(APPLICATION_JSON, transport.actualSendRequestHeaders.getContentType());
    }

    @Test(expected = HttpServerErrorException.class)
    public void sendMessageError() throws Exception {
        XhrTransportTests.TestXhrTransport transport = new XhrTransportTests.TestXhrTransport();
        transport.sendMessageResponseToReturn = new ResponseEntity(HttpStatus.BAD_REQUEST);
        URI url = new URI("http://example.com");
        transport.executeSendRequest(url, new HttpHeaders(), new TextMessage("payload"));
    }

    @Test
    public void connect() throws Exception {
        HttpHeaders handshakeHeaders = new HttpHeaders();
        handshakeHeaders.setOrigin("foo");
        TransportRequest request = Mockito.mock(TransportRequest.class);
        BDDMockito.given(request.getSockJsUrlInfo()).willReturn(new SockJsUrlInfo(new URI("http://example.com")));
        BDDMockito.given(request.getHandshakeHeaders()).willReturn(handshakeHeaders);
        BDDMockito.given(request.getHttpRequestHeaders()).willReturn(new HttpHeaders());
        XhrTransportTests.TestXhrTransport transport = new XhrTransportTests.TestXhrTransport();
        WebSocketHandler handler = Mockito.mock(WebSocketHandler.class);
        transport.connect(request, handler);
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(request).getSockJsUrlInfo();
        Mockito.verify(request).addTimeoutTask(captor.capture());
        Mockito.verify(request).getTransportUrl();
        Mockito.verify(request).getHandshakeHeaders();
        Mockito.verify(request).getHttpRequestHeaders();
        Mockito.verifyNoMoreInteractions(request);
        Assert.assertEquals(1, transport.actualHandshakeHeaders.size());
        Assert.assertEquals("foo", transport.actualHandshakeHeaders.getOrigin());
        Assert.assertFalse(transport.actualSession.isDisconnected());
        captor.getValue().run();
        Assert.assertTrue(transport.actualSession.isDisconnected());
    }

    private static class TestXhrTransport extends AbstractXhrTransport {
        private ResponseEntity<String> infoResponseToReturn;

        private ResponseEntity<String> sendMessageResponseToReturn;

        private HttpHeaders actualSendRequestHeaders;

        private HttpHeaders actualHandshakeHeaders;

        private XhrClientSockJsSession actualSession;

        @Override
        protected ResponseEntity<String> executeInfoRequestInternal(URI infoUrl, HttpHeaders headers) {
            return this.infoResponseToReturn;
        }

        @Override
        protected ResponseEntity<String> executeSendRequestInternal(URI url, HttpHeaders headers, TextMessage message) {
            this.actualSendRequestHeaders = headers;
            return this.sendMessageResponseToReturn;
        }

        @Override
        protected void connectInternal(TransportRequest request, WebSocketHandler handler, URI receiveUrl, HttpHeaders handshakeHeaders, XhrClientSockJsSession session, SettableListenableFuture<WebSocketSession> connectFuture) {
            this.actualHandshakeHeaders = handshakeHeaders;
            this.actualSession = session;
        }
    }
}

