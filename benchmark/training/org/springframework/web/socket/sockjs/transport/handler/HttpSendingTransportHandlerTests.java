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


import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.AbstractHttpRequestTests;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.frame.SockJsFrame;
import org.springframework.web.socket.sockjs.frame.SockJsFrameFormat;
import org.springframework.web.socket.sockjs.transport.session.AbstractSockJsSession;
import org.springframework.web.socket.sockjs.transport.session.StreamingSockJsSession;
import org.springframework.web.socket.sockjs.transport.session.StubSockJsServiceConfig;


/**
 * Test fixture for {@link AbstractHttpSendingTransportHandler} and sub-classes.
 *
 * @author Rossen Stoyanchev
 */
public class HttpSendingTransportHandlerTests extends AbstractHttpRequestTests {
    private WebSocketHandler webSocketHandler;

    private StubSockJsServiceConfig sockJsConfig;

    private TaskScheduler taskScheduler;

    @Test
    public void handleRequestXhr() throws Exception {
        XhrPollingTransportHandler transportHandler = new XhrPollingTransportHandler();
        transportHandler.initialize(this.sockJsConfig);
        AbstractSockJsSession session = transportHandler.createSession("1", this.webSocketHandler, null);
        transportHandler.handleRequest(this.request, this.response, this.webSocketHandler, session);
        Assert.assertEquals("application/javascript;charset=UTF-8", this.response.getHeaders().getContentType().toString());
        Assert.assertEquals("o\n", this.servletResponse.getContentAsString());
        Assert.assertFalse("Polling request should complete after open frame", this.servletRequest.isAsyncStarted());
        Mockito.verify(this.webSocketHandler).afterConnectionEstablished(session);
        resetRequestAndResponse();
        transportHandler.handleRequest(this.request, this.response, this.webSocketHandler, session);
        Assert.assertTrue("Polling request should remain open", this.servletRequest.isAsyncStarted());
        Mockito.verify(this.taskScheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Date.class));
        resetRequestAndResponse();
        transportHandler.handleRequest(this.request, this.response, this.webSocketHandler, session);
        Assert.assertFalse("Request should have been rejected", this.servletRequest.isAsyncStarted());
        Assert.assertEquals("c[2010,\"Another connection still open\"]\n", this.servletResponse.getContentAsString());
    }

    @Test
    public void handleRequestXhrStreaming() throws Exception {
        XhrStreamingTransportHandler transportHandler = new XhrStreamingTransportHandler();
        transportHandler.initialize(this.sockJsConfig);
        AbstractSockJsSession session = transportHandler.createSession("1", this.webSocketHandler, null);
        transportHandler.handleRequest(this.request, this.response, this.webSocketHandler, session);
        Assert.assertEquals("application/javascript;charset=UTF-8", this.response.getHeaders().getContentType().toString());
        Assert.assertTrue("Streaming request not started", this.servletRequest.isAsyncStarted());
        Mockito.verify(this.webSocketHandler).afterConnectionEstablished(session);
    }

    @Test
    public void htmlFileTransport() throws Exception {
        HtmlFileTransportHandler transportHandler = new HtmlFileTransportHandler();
        transportHandler.initialize(this.sockJsConfig);
        StreamingSockJsSession session = transportHandler.createSession("1", this.webSocketHandler, null);
        transportHandler.handleRequest(this.request, this.response, this.webSocketHandler, session);
        Assert.assertEquals(500, this.servletResponse.getStatus());
        Assert.assertEquals("\"callback\" parameter required", this.servletResponse.getContentAsString());
        resetRequestAndResponse();
        setRequest("POST", "/");
        this.servletRequest.setQueryString("c=callback");
        this.servletRequest.addParameter("c", "callback");
        transportHandler.handleRequest(this.request, this.response, this.webSocketHandler, session);
        Assert.assertEquals("text/html;charset=UTF-8", this.response.getHeaders().getContentType().toString());
        Assert.assertTrue("Streaming request not started", this.servletRequest.isAsyncStarted());
        Mockito.verify(this.webSocketHandler).afterConnectionEstablished(session);
    }

    @Test
    public void eventSourceTransport() throws Exception {
        EventSourceTransportHandler transportHandler = new EventSourceTransportHandler();
        transportHandler.initialize(this.sockJsConfig);
        StreamingSockJsSession session = transportHandler.createSession("1", this.webSocketHandler, null);
        transportHandler.handleRequest(this.request, this.response, this.webSocketHandler, session);
        Assert.assertEquals("text/event-stream;charset=UTF-8", this.response.getHeaders().getContentType().toString());
        Assert.assertTrue("Streaming request not started", this.servletRequest.isAsyncStarted());
        Mockito.verify(this.webSocketHandler).afterConnectionEstablished(session);
    }

    @Test
    public void frameFormats() throws Exception {
        this.servletRequest.setQueryString("c=callback");
        this.servletRequest.addParameter("c", "callback");
        SockJsFrame frame = SockJsFrame.openFrame();
        SockJsFrameFormat format = new XhrPollingTransportHandler().getFrameFormat(this.request);
        String formatted = format.format(frame);
        Assert.assertEquals(((frame.getContent()) + "\n"), formatted);
        format = new XhrStreamingTransportHandler().getFrameFormat(this.request);
        formatted = format.format(frame);
        Assert.assertEquals(((frame.getContent()) + "\n"), formatted);
        format = new HtmlFileTransportHandler().getFrameFormat(this.request);
        formatted = format.format(frame);
        Assert.assertEquals((("<script>\np(\"" + (frame.getContent())) + "\");\n</script>\r\n"), formatted);
        format = new EventSourceTransportHandler().getFrameFormat(this.request);
        formatted = format.format(frame);
        Assert.assertEquals((("data: " + (frame.getContent())) + "\r\n\r\n"), formatted);
    }
}

