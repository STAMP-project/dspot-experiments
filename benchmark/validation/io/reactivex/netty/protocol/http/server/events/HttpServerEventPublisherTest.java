/**
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.netty.protocol.http.server.events;


import Event.BytesRead;
import Event.BytesWritten;
import Event.CloseFailed;
import Event.CloseStart;
import Event.CloseSuccess;
import Event.CustomEvent;
import Event.CustomEventWithDuration;
import Event.CustomEventWithDurationAndError;
import Event.CustomEventWithError;
import Event.FlushStart;
import Event.FlushSuccess;
import Event.WriteFailed;
import Event.WriteStart;
import Event.WriteSuccess;
import io.reactivex.netty.protocol.tcp.server.events.TcpServerEventPublisher;
import io.reactivex.netty.test.util.MockTcpServerEventListener;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static io.reactivex.netty.protocol.http.server.events.HttpServerEventsListenerImpl.HttpEvent.HandlingFailed;
import static io.reactivex.netty.protocol.http.server.events.HttpServerEventsListenerImpl.HttpEvent.HandlingStart;
import static io.reactivex.netty.protocol.http.server.events.HttpServerEventsListenerImpl.HttpEvent.HandlingSuccess;
import static io.reactivex.netty.protocol.http.server.events.HttpServerEventsListenerImpl.HttpEvent.ReqContentReceived;
import static io.reactivex.netty.protocol.http.server.events.HttpServerEventsListenerImpl.HttpEvent.ReqHdrsReceived;
import static io.reactivex.netty.protocol.http.server.events.HttpServerEventsListenerImpl.HttpEvent.ReqReceiveComplete;
import static io.reactivex.netty.protocol.http.server.events.HttpServerEventsListenerImpl.HttpEvent.ReqRecv;
import static io.reactivex.netty.protocol.http.server.events.HttpServerEventsListenerImpl.HttpEvent.RespWriteFailed;
import static io.reactivex.netty.protocol.http.server.events.HttpServerEventsListenerImpl.HttpEvent.RespWriteStart;
import static io.reactivex.netty.protocol.http.server.events.HttpServerEventsListenerImpl.HttpEvent.RespWriteSuccess;
import static io.reactivex.netty.test.util.MockTcpServerEventListener.ServerEvent.NewClient;


public class HttpServerEventPublisherTest {
    @Rule
    public final HttpServerEventPublisherTest.PublisherRule rule = new HttpServerEventPublisherTest.PublisherRule();

    @Test(timeout = 60000)
    public void testOnNewRequestReceived() throws Exception {
        rule.publisher.onNewRequestReceived();
        rule.listener.assertMethodsCalled(ReqRecv);
    }

    @Test(timeout = 60000)
    public void testOnRequestHandlingStart() throws Exception {
        rule.publisher.onRequestHandlingStart(1, TimeUnit.MILLISECONDS);
        rule.listener.assertMethodsCalled(HandlingStart);
        MatcherAssert.assertThat("Listener not called with duration.", rule.listener.getDuration(), is(1L));
        MatcherAssert.assertThat("Listener not called with time unit.", rule.listener.getTimeUnit(), is(TimeUnit.MILLISECONDS));
    }

    @Test(timeout = 60000)
    public void testOnRequestHandlingSuccess() throws Exception {
        rule.publisher.onRequestHandlingSuccess(1, TimeUnit.MILLISECONDS);
        rule.listener.assertMethodsCalled(HandlingSuccess);
        MatcherAssert.assertThat("Listener not called with duration.", rule.listener.getDuration(), is(1L));
        MatcherAssert.assertThat("Listener not called with time unit.", rule.listener.getTimeUnit(), is(TimeUnit.MILLISECONDS));
    }

    @Test(timeout = 60000)
    public void testOnRequestHandlingFailed() throws Exception {
        final Throwable expected = new NullPointerException();
        rule.publisher.onRequestHandlingFailed(1, TimeUnit.MILLISECONDS, expected);
        rule.listener.assertMethodsCalled(HandlingFailed);
        MatcherAssert.assertThat("Listener not called with duration.", rule.listener.getDuration(), is(1L));
        MatcherAssert.assertThat("Listener not called with time unit.", rule.listener.getTimeUnit(), is(TimeUnit.MILLISECONDS));
        MatcherAssert.assertThat("Listener not called with error.", rule.listener.getRecievedError(), is(expected));
    }

    @Test(timeout = 60000)
    public void testOnRequestHeadersReceived() throws Exception {
        rule.publisher.onRequestHeadersReceived();
        rule.listener.assertMethodsCalled(ReqHdrsReceived);
    }

    @Test(timeout = 60000)
    public void testOnRequestContentReceived() throws Exception {
        rule.publisher.onRequestContentReceived();
        rule.listener.assertMethodsCalled(ReqContentReceived);
    }

    @Test(timeout = 60000)
    public void testOnRequestReceiveComplete() throws Exception {
        rule.publisher.onRequestReceiveComplete(1, TimeUnit.MILLISECONDS);
        rule.listener.assertMethodsCalled(ReqReceiveComplete);
        MatcherAssert.assertThat("Listener not called with duration.", rule.listener.getDuration(), is(1L));
        MatcherAssert.assertThat("Listener not called with time unit.", rule.listener.getTimeUnit(), is(TimeUnit.MILLISECONDS));
    }

    @Test(timeout = 60000)
    public void testOnResponseWriteStart() throws Exception {
        rule.publisher.onResponseWriteStart();
        rule.listener.assertMethodsCalled(RespWriteStart);
    }

    @Test(timeout = 60000)
    public void testOnResponseWriteSuccess() throws Exception {
        rule.publisher.onResponseWriteSuccess(1, TimeUnit.MILLISECONDS, 200);
        rule.listener.assertMethodsCalled(RespWriteSuccess);
        MatcherAssert.assertThat("Listener not called with duration.", rule.listener.getDuration(), is(1L));
        MatcherAssert.assertThat("Listener not called with time unit.", rule.listener.getTimeUnit(), is(TimeUnit.MILLISECONDS));
        MatcherAssert.assertThat("Listener not called with response code.", rule.listener.getResponseCode(), is(200));
    }

    @Test(timeout = 60000)
    public void testOnResponseWriteFailed() throws Exception {
        final Throwable expected = new NullPointerException();
        rule.publisher.onResponseWriteFailed(1, TimeUnit.MILLISECONDS, expected);
        rule.listener.assertMethodsCalled(RespWriteFailed);
        MatcherAssert.assertThat("Listener not called with duration.", rule.listener.getDuration(), is(1L));
        MatcherAssert.assertThat("Listener not called with time unit.", rule.listener.getTimeUnit(), is(TimeUnit.MILLISECONDS));
        MatcherAssert.assertThat("Listener not called with error.", rule.listener.getRecievedError(), is(expected));
    }

    @Test(timeout = 60000)
    public void testOnConnectionCloseFailed() throws Exception {
        rule.publisher.onConnectionCloseFailed(1, TimeUnit.MILLISECONDS, new NullPointerException());
        rule.listener.getTcpDelegate().getConnDelegate().assertMethodsCalled(CloseFailed);
    }

    @Test(timeout = 60000)
    public void testOnConnectionCloseSuccess() throws Exception {
        rule.publisher.onConnectionCloseSuccess(1, TimeUnit.MILLISECONDS);
        rule.listener.getTcpDelegate().getConnDelegate().assertMethodsCalled(CloseSuccess);
    }

    @Test(timeout = 60000)
    public void testOnConnectionCloseStart() throws Exception {
        rule.publisher.onConnectionCloseStart();
        rule.listener.getTcpDelegate().getConnDelegate().assertMethodsCalled(CloseStart);
    }

    @Test(timeout = 60000)
    public void testOnWriteFailed() throws Exception {
        rule.publisher.onWriteFailed(1, TimeUnit.MILLISECONDS, new NullPointerException());
        rule.listener.getTcpDelegate().getConnDelegate().assertMethodsCalled(WriteFailed);
    }

    @Test(timeout = 60000)
    public void testOnWriteSuccess() throws Exception {
        rule.publisher.onWriteSuccess(1, TimeUnit.MILLISECONDS);
        rule.listener.getTcpDelegate().getConnDelegate().assertMethodsCalled(WriteSuccess);
    }

    @Test(timeout = 60000)
    public void testOnWriteStart() throws Exception {
        rule.publisher.onWriteStart();
        rule.listener.getTcpDelegate().getConnDelegate().assertMethodsCalled(WriteStart);
    }

    @Test(timeout = 60000)
    public void testOnFlushSuccess() throws Exception {
        rule.publisher.onFlushComplete(1, TimeUnit.MILLISECONDS);
        rule.listener.getTcpDelegate().getConnDelegate().assertMethodsCalled(FlushSuccess);
    }

    @Test(timeout = 60000)
    public void testOnFlushStart() throws Exception {
        rule.publisher.onFlushStart();
        rule.listener.getTcpDelegate().getConnDelegate().assertMethodsCalled(FlushStart);
    }

    @Test(timeout = 60000)
    public void testOnByteRead() throws Exception {
        rule.publisher.onByteRead(1);
        rule.listener.getTcpDelegate().getConnDelegate().assertMethodsCalled(BytesRead);
    }

    @Test(timeout = 60000)
    public void testOnByteWritten() throws Exception {
        rule.publisher.onByteWritten(1);
        rule.listener.getTcpDelegate().getConnDelegate().assertMethodsCalled(BytesWritten);
    }

    @Test(timeout = 60000)
    public void testOnConnectionHandlingFailed() throws Exception {
        rule.publisher.onConnectionHandlingFailed(1, TimeUnit.MILLISECONDS, new NullPointerException());
        rule.listener.getTcpDelegate().assertMethodsCalled(MockTcpServerEventListener.ServerEvent.HandlingFailed);
    }

    @Test(timeout = 60000)
    public void testOnConnectionHandlingSuccess() throws Exception {
        rule.publisher.onConnectionHandlingSuccess(1, TimeUnit.MILLISECONDS);
        rule.listener.getTcpDelegate().assertMethodsCalled(MockTcpServerEventListener.ServerEvent.HandlingSuccess);
    }

    @Test(timeout = 60000)
    public void testOnConnectionHandlingStart() throws Exception {
        rule.publisher.onConnectionHandlingStart(1, TimeUnit.MILLISECONDS);
        rule.listener.getTcpDelegate().assertMethodsCalled(MockTcpServerEventListener.ServerEvent.HandlingStart);
    }

    @Test(timeout = 60000)
    public void testOnNewClientConnected() throws Exception {
        rule.publisher.onNewClientConnected();
        rule.listener.getTcpDelegate().assertMethodsCalled(NewClient);
    }

    @Test(timeout = 60000)
    public void testOnCustomEvent() throws Exception {
        rule.publisher.onCustomEvent("Hello");
        rule.listener.getTcpDelegate().getConnDelegate().assertMethodsCalled(CustomEvent);
    }

    @Test(timeout = 60000)
    public void testOnCustomEventWithError() throws Exception {
        rule.publisher.onCustomEvent("Hello", new NullPointerException());
        rule.listener.getTcpDelegate().getConnDelegate().assertMethodsCalled(CustomEventWithError);
    }

    @Test(timeout = 60000)
    public void testOnCustomEventWithDuration() throws Exception {
        rule.publisher.onCustomEvent("Hello", 1, TimeUnit.MINUTES);
        rule.listener.getTcpDelegate().getConnDelegate().assertMethodsCalled(CustomEventWithDuration);
    }

    @Test(timeout = 60000)
    public void testOnCustomEventWithDurationAndError() throws Exception {
        rule.publisher.onCustomEvent("Hello", 1, TimeUnit.MINUTES, new NullPointerException());
        rule.listener.getTcpDelegate().getConnDelegate().assertMethodsCalled(CustomEventWithDurationAndError);
    }

    @Test(timeout = 60000)
    public void testPublishingEnabled() throws Exception {
        MatcherAssert.assertThat("Publishing not enabled.", rule.publisher.publishingEnabled(), is(true));
    }

    @Test(timeout = 60000)
    public void testCopy() throws Exception {
        HttpServerEventPublisher copy = rule.publisher.copy(rule.publisher.getTcpDelegate().copy());
        MatcherAssert.assertThat("Publisher not copied.", copy, is(not(sameInstance(rule.publisher))));
        MatcherAssert.assertThat("Listeners not copied.", copy.getListeners(), is(not(sameInstance(rule.publisher.getListeners()))));
        MatcherAssert.assertThat("Delegate not copied.", copy.getTcpDelegate(), is(not(sameInstance(rule.publisher.getTcpDelegate()))));
    }

    public static class PublisherRule extends ExternalResource {
        private HttpServerEventsListenerImpl listener;

        private HttpServerEventPublisher publisher;

        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    listener = new HttpServerEventsListenerImpl();
                    publisher = new HttpServerEventPublisher(new TcpServerEventPublisher());
                    publisher.subscribe(listener);
                    base.evaluate();
                }
            };
        }
    }
}

