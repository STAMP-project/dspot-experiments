/**
 * Copyright 2002-2015 the original author or authors.
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


import TransportType.WEBSOCKET;
import TransportType.XHR_STREAMING;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;


/**
 * Unit tests for {@link DefaultTransportRequest}.
 *
 * @author Rossen Stoyanchev
 */
public class DefaultTransportRequestTests {
    private static final Jackson2SockJsMessageCodec CODEC = new Jackson2SockJsMessageCodec();

    private SettableListenableFuture<WebSocketSession> connectFuture;

    private ListenableFutureCallback<WebSocketSession> connectCallback;

    private TestTransport webSocketTransport;

    private TestTransport xhrTransport;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void connect() throws Exception {
        DefaultTransportRequest request = createTransportRequest(this.webSocketTransport, WEBSOCKET);
        request.connect(null, this.connectFuture);
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        this.webSocketTransport.getConnectCallback().onSuccess(session);
        Assert.assertSame(session, this.connectFuture.get());
    }

    @Test
    public void fallbackAfterTransportError() throws Exception {
        DefaultTransportRequest request1 = createTransportRequest(this.webSocketTransport, WEBSOCKET);
        DefaultTransportRequest request2 = createTransportRequest(this.xhrTransport, XHR_STREAMING);
        request1.setFallbackRequest(request2);
        request1.connect(null, this.connectFuture);
        // Transport error => fallback
        this.webSocketTransport.getConnectCallback().onFailure(new IOException("Fake exception 1"));
        Assert.assertFalse(this.connectFuture.isDone());
        Assert.assertTrue(this.xhrTransport.invoked());
        // Transport error => no more fallback
        this.xhrTransport.getConnectCallback().onFailure(new IOException("Fake exception 2"));
        Assert.assertTrue(this.connectFuture.isDone());
        this.thrown.expect(ExecutionException.class);
        this.thrown.expectMessage("Fake exception 2");
        this.connectFuture.get();
    }

    @Test
    public void fallbackAfterTimeout() throws Exception {
        TaskScheduler scheduler = Mockito.mock(TaskScheduler.class);
        Runnable sessionCleanupTask = Mockito.mock(Runnable.class);
        DefaultTransportRequest request1 = createTransportRequest(this.webSocketTransport, WEBSOCKET);
        DefaultTransportRequest request2 = createTransportRequest(this.xhrTransport, XHR_STREAMING);
        request1.setFallbackRequest(request2);
        request1.setTimeoutScheduler(scheduler);
        request1.addTimeoutTask(sessionCleanupTask);
        request1.connect(null, this.connectFuture);
        Assert.assertTrue(this.webSocketTransport.invoked());
        Assert.assertFalse(this.xhrTransport.invoked());
        // Get and invoke the scheduled timeout task
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(scheduler).schedule(taskCaptor.capture(), ArgumentMatchers.any(Date.class));
        Mockito.verifyNoMoreInteractions(scheduler);
        taskCaptor.getValue().run();
        Assert.assertTrue(this.xhrTransport.invoked());
        Mockito.verify(sessionCleanupTask).run();
    }
}

