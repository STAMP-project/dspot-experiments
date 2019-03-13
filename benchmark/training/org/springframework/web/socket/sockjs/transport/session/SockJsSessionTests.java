/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.web.socket.sockjs.transport.session;


import CloseStatus.BAD_DATA;
import CloseStatus.GOING_AWAY;
import CloseStatus.NORMAL;
import CloseStatus.SERVER_ERROR;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.SockJsMessageDeliveryException;
import org.springframework.web.socket.sockjs.SockJsTransportFailureException;
import org.springframework.web.socket.sockjs.frame.SockJsFrame;


/**
 * Test fixture for {@link AbstractSockJsSession}.
 *
 * @author Rossen Stoyanchev
 */
public class SockJsSessionTests extends AbstractSockJsSessionTests<TestSockJsSession> {
    @Test
    public void getTimeSinceLastActive() throws Exception {
        Thread.sleep(1);
        long time1 = this.session.getTimeSinceLastActive();
        Assert.assertTrue((time1 > 0));
        Thread.sleep(1);
        long time2 = this.session.getTimeSinceLastActive();
        Assert.assertTrue((time2 > time1));
        this.session.delegateConnectionEstablished();
        Thread.sleep(1);
        this.session.setActive(false);
        Assert.assertTrue(((this.session.getTimeSinceLastActive()) > 0));
        this.session.setActive(true);
        Assert.assertEquals(0, this.session.getTimeSinceLastActive());
    }

    @Test
    public void delegateConnectionEstablished() throws Exception {
        assertNew();
        this.session.delegateConnectionEstablished();
        assertOpen();
        Mockito.verify(this.webSocketHandler).afterConnectionEstablished(this.session);
    }

    @Test
    public void delegateError() throws Exception {
        Exception ex = new Exception();
        this.session.delegateError(ex);
        Mockito.verify(this.webSocketHandler).handleTransportError(this.session, ex);
    }

    @Test
    public void delegateMessages() throws Exception {
        String msg1 = "message 1";
        String msg2 = "message 2";
        this.session.delegateMessages(msg1, msg2);
        Mockito.verify(this.webSocketHandler).handleMessage(this.session, new TextMessage(msg1));
        Mockito.verify(this.webSocketHandler).handleMessage(this.session, new TextMessage(msg2));
        Mockito.verifyNoMoreInteractions(this.webSocketHandler);
    }

    @Test
    public void delegateMessagesWithErrorAndConnectionClosing() throws Exception {
        WebSocketHandler wsHandler = new org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator(this.webSocketHandler);
        TestSockJsSession sockJsSession = new TestSockJsSession("1", this.sockJsConfig, wsHandler, Collections.<String, Object>emptyMap());
        String msg1 = "message 1";
        String msg2 = "message 2";
        String msg3 = "message 3";
        BDDMockito.willThrow(new IOException()).given(this.webSocketHandler).handleMessage(sockJsSession, new TextMessage(msg2));
        sockJsSession.delegateConnectionEstablished();
        try {
            sockJsSession.delegateMessages(msg1, msg2, msg3);
            Assert.fail("expected exception");
        } catch (SockJsMessageDeliveryException ex) {
            Assert.assertEquals(Collections.singletonList(msg3), ex.getUndeliveredMessages());
            Mockito.verify(this.webSocketHandler).afterConnectionEstablished(sockJsSession);
            Mockito.verify(this.webSocketHandler).handleMessage(sockJsSession, new TextMessage(msg1));
            Mockito.verify(this.webSocketHandler).handleMessage(sockJsSession, new TextMessage(msg2));
            Mockito.verify(this.webSocketHandler).afterConnectionClosed(sockJsSession, SERVER_ERROR);
            Mockito.verifyNoMoreInteractions(this.webSocketHandler);
        }
    }

    @Test
    public void delegateConnectionClosed() throws Exception {
        this.session.delegateConnectionEstablished();
        this.session.delegateConnectionClosed(GOING_AWAY);
        assertClosed();
        Assert.assertEquals(1, this.session.getNumberOfLastActiveTimeUpdates());
        Mockito.verify(this.webSocketHandler).afterConnectionClosed(this.session, GOING_AWAY);
    }

    @Test
    public void closeWhenNotOpen() throws Exception {
        assertNew();
        this.session.close();
        Assert.assertNull("Close not ignored for a new session", this.session.getCloseStatus());
        this.session.delegateConnectionEstablished();
        assertOpen();
        this.session.close();
        assertClosed();
        Assert.assertEquals(3000, this.session.getCloseStatus().getCode());
        this.session.close(SERVER_ERROR);
        Assert.assertEquals("Close should be ignored if already closed", 3000, this.session.getCloseStatus().getCode());
    }

    @Test
    public void closeWhenNotActive() throws Exception {
        this.session.delegateConnectionEstablished();
        assertOpen();
        this.session.setActive(false);
        this.session.close();
        Assert.assertEquals(Collections.emptyList(), this.session.getSockJsFramesWritten());
    }

    @Test
    public void close() throws Exception {
        this.session.delegateConnectionEstablished();
        assertOpen();
        this.session.setActive(true);
        this.session.close();
        Assert.assertEquals(1, this.session.getSockJsFramesWritten().size());
        Assert.assertEquals(SockJsFrame.closeFrameGoAway(), this.session.getSockJsFramesWritten().get(0));
        Assert.assertEquals(1, this.session.getNumberOfLastActiveTimeUpdates());
        Assert.assertTrue(this.session.didCancelHeartbeat());
        Assert.assertEquals(new CloseStatus(3000, "Go away!"), this.session.getCloseStatus());
        assertClosed();
        Mockito.verify(this.webSocketHandler).afterConnectionClosed(this.session, new CloseStatus(3000, "Go away!"));
    }

    @Test
    public void closeWithWriteFrameExceptions() throws Exception {
        this.session.setExceptionOnWrite(new IOException());
        this.session.delegateConnectionEstablished();
        this.session.setActive(true);
        this.session.close();
        Assert.assertEquals(new CloseStatus(3000, "Go away!"), this.session.getCloseStatus());
        assertClosed();
    }

    @Test
    public void closeWithWebSocketHandlerExceptions() throws Exception {
        BDDMockito.willThrow(new Exception()).given(this.webSocketHandler).afterConnectionClosed(this.session, NORMAL);
        this.session.delegateConnectionEstablished();
        this.session.setActive(true);
        this.session.close(NORMAL);
        Assert.assertEquals(NORMAL, this.session.getCloseStatus());
        assertClosed();
    }

    @Test
    public void tryCloseWithWebSocketHandlerExceptions() throws Exception {
        this.session.delegateConnectionEstablished();
        this.session.setActive(true);
        this.session.tryCloseWithSockJsTransportError(new Exception(), BAD_DATA);
        Assert.assertEquals(BAD_DATA, this.session.getCloseStatus());
        assertClosed();
    }

    @Test
    public void writeFrame() throws Exception {
        this.session.writeFrame(SockJsFrame.openFrame());
        Assert.assertEquals(1, this.session.getSockJsFramesWritten().size());
        Assert.assertEquals(SockJsFrame.openFrame(), this.session.getSockJsFramesWritten().get(0));
    }

    @Test
    public void writeFrameIoException() throws Exception {
        this.session.setExceptionOnWrite(new IOException());
        this.session.delegateConnectionEstablished();
        try {
            this.session.writeFrame(SockJsFrame.openFrame());
            Assert.fail("expected exception");
        } catch (SockJsTransportFailureException ex) {
            Assert.assertEquals(SERVER_ERROR, this.session.getCloseStatus());
            Mockito.verify(this.webSocketHandler).afterConnectionClosed(this.session, SERVER_ERROR);
        }
    }

    @Test
    public void sendHeartbeat() throws Exception {
        this.session.setActive(true);
        this.session.sendHeartbeat();
        Assert.assertEquals(1, this.session.getSockJsFramesWritten().size());
        Assert.assertEquals(SockJsFrame.heartbeatFrame(), this.session.getSockJsFramesWritten().get(0));
        Mockito.verify(this.taskScheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Date.class));
        Mockito.verifyNoMoreInteractions(this.taskScheduler);
    }

    @Test
    public void scheduleHeartbeatNotActive() throws Exception {
        this.session.setActive(false);
        scheduleHeartbeat();
        Mockito.verifyNoMoreInteractions(this.taskScheduler);
    }

    @Test
    public void sendHeartbeatWhenDisabled() throws Exception {
        disableHeartbeat();
        this.session.setActive(true);
        this.session.sendHeartbeat();
        Assert.assertEquals(Collections.emptyList(), this.session.getSockJsFramesWritten());
    }

    @Test
    public void scheduleAndCancelHeartbeat() throws Exception {
        ScheduledFuture<?> task = Mockito.mock(ScheduledFuture.class);
        BDDMockito.willReturn(task).given(this.taskScheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Date.class));
        this.session.setActive(true);
        scheduleHeartbeat();
        Mockito.verify(this.taskScheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Date.class));
        Mockito.verifyNoMoreInteractions(this.taskScheduler);
        BDDMockito.given(task.isCancelled()).willReturn(false);
        BDDMockito.given(task.cancel(false)).willReturn(true);
        this.session.cancelHeartbeat();
        Mockito.verify(task).cancel(false);
        Mockito.verifyNoMoreInteractions(task);
    }
}

