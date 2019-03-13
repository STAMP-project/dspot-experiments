/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.web.socket.sockjs.transport.handler;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.web.socket.AbstractHttpRequestTests;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.SockJsMessageDeliveryException;
import org.springframework.web.socket.sockjs.transport.session.StubSockJsServiceConfig;
import org.springframework.web.socket.sockjs.transport.session.TestHttpSockJsSession;


/**
 * Test fixture for {@link AbstractHttpReceivingTransportHandler} and sub-classes
 * {@link XhrReceivingTransportHandler}.
 *
 * @author Rossen Stoyanchev
 */
public class HttpReceivingTransportHandlerTests extends AbstractHttpRequestTests {
    @Test
    public void readMessagesXhr() throws Exception {
        this.servletRequest.setContent("[\"x\"]".getBytes("UTF-8"));
        handleRequest(new XhrReceivingTransportHandler());
        Assert.assertEquals(204, this.servletResponse.getStatus());
    }

    @Test
    public void readMessagesBadContent() throws Exception {
        this.servletRequest.setContent("".getBytes("UTF-8"));
        handleRequestAndExpectFailure();
        this.servletRequest.setContent("[\"x]".getBytes("UTF-8"));
        handleRequestAndExpectFailure();
    }

    @Test(expected = IllegalArgumentException.class)
    public void readMessagesNoSession() throws Exception {
        WebSocketHandler webSocketHandler = Mockito.mock(WebSocketHandler.class);
        new XhrReceivingTransportHandler().handleRequest(this.request, this.response, webSocketHandler, null);
    }

    @Test
    public void delegateMessageException() throws Exception {
        StubSockJsServiceConfig sockJsConfig = new StubSockJsServiceConfig();
        this.servletRequest.setContent("[\"x\"]".getBytes("UTF-8"));
        WebSocketHandler wsHandler = Mockito.mock(WebSocketHandler.class);
        TestHttpSockJsSession session = new TestHttpSockJsSession("1", sockJsConfig, wsHandler, null);
        delegateConnectionEstablished();
        BDDMockito.willThrow(new Exception()).given(wsHandler).handleMessage(session, new TextMessage("x"));
        try {
            XhrReceivingTransportHandler transportHandler = new XhrReceivingTransportHandler();
            transportHandler.initialize(sockJsConfig);
            transportHandler.handleRequest(this.request, this.response, wsHandler, session);
            Assert.fail("Expected exception");
        } catch (SockJsMessageDeliveryException ex) {
            Assert.assertNull(session.getCloseStatus());
        }
    }
}

