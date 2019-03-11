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
package io.reactivex.netty.protocol.http.client.events;


import ClientEvent.AcquireFailed;
import ClientEvent.AcquireStart;
import ClientEvent.AcquireSuccess;
import ClientEvent.ConnectFailed;
import ClientEvent.ConnectStart;
import ClientEvent.ConnectSuccess;
import ClientEvent.Eviction;
import ClientEvent.ReleaseFailed;
import ClientEvent.ReleaseStart;
import ClientEvent.ReleaseSuccess;
import ClientEvent.Reuse;
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
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static io.reactivex.netty.protocol.http.client.events.HttpClientEventsListenerImpl.HttpEvent.ProcessingComplete;
import static io.reactivex.netty.protocol.http.client.events.HttpClientEventsListenerImpl.HttpEvent.ReqSubmitted;
import static io.reactivex.netty.protocol.http.client.events.HttpClientEventsListenerImpl.HttpEvent.ReqWriteFailed;
import static io.reactivex.netty.protocol.http.client.events.HttpClientEventsListenerImpl.HttpEvent.ReqWriteStart;
import static io.reactivex.netty.protocol.http.client.events.HttpClientEventsListenerImpl.HttpEvent.ReqWriteSuccess;
import static io.reactivex.netty.protocol.http.client.events.HttpClientEventsListenerImpl.HttpEvent.ResContentReceived;
import static io.reactivex.netty.protocol.http.client.events.HttpClientEventsListenerImpl.HttpEvent.ResHeadersReceived;
import static io.reactivex.netty.protocol.http.client.events.HttpClientEventsListenerImpl.HttpEvent.ResReceiveComplete;
import static io.reactivex.netty.protocol.http.client.events.HttpClientEventsListenerImpl.HttpEvent.RespFailed;


public class HttpClientEventPublisherTest {
    @Rule
    public final HttpClientEventPublisherTest.PublisherRule rule = new HttpClientEventPublisherTest.PublisherRule();

    @Test(timeout = 60000)
    public void testOnRequestSubmitted() throws Exception {
        rule.publisher.onRequestSubmitted();
        rule.listener.assertMethodCalled(ReqSubmitted);
    }

    @Test(timeout = 60000)
    public void testOnRequestWriteStart() throws Exception {
        rule.publisher.onRequestWriteStart();
        rule.listener.assertMethodCalled(ReqWriteStart);
    }

    @Test(timeout = 60000)
    public void testOnRequestWriteComplete() throws Exception {
        rule.publisher.onRequestWriteComplete(1, TimeUnit.MILLISECONDS);
        rule.listener.assertMethodCalled(ReqWriteSuccess);
        MatcherAssert.assertThat("Listener not called with duration.", rule.listener.getDuration(), is(1L));
        MatcherAssert.assertThat("Listener not called with time unit.", rule.listener.getTimeUnit(), is(TimeUnit.MILLISECONDS));
    }

    @Test(timeout = 60000)
    public void testOnRequestWriteFailed() throws Exception {
        final Throwable expected = new NullPointerException();
        rule.publisher.onRequestWriteFailed(1, TimeUnit.MILLISECONDS, expected);
        rule.listener.assertMethodCalled(ReqWriteFailed);
        MatcherAssert.assertThat("Listener not called with duration.", rule.listener.getDuration(), is(1L));
        MatcherAssert.assertThat("Listener not called with time unit.", rule.listener.getTimeUnit(), is(TimeUnit.MILLISECONDS));
        MatcherAssert.assertThat("Listener not called with error.", rule.listener.getRecievedError(), is(expected));
    }

    @Test(timeout = 60000)
    public void testOnResponseHeadersReceived() throws Exception {
        rule.publisher.onResponseHeadersReceived(200, 1, TimeUnit.MILLISECONDS);
        rule.listener.assertMethodCalled(ResHeadersReceived);
        MatcherAssert.assertThat("Listener not called with response code.", rule.listener.getResponseCode(), is(200));
        MatcherAssert.assertThat("Listener not called with duration.", rule.listener.getDuration(), is(1L));
        MatcherAssert.assertThat("Listener not called with time unit.", rule.listener.getTimeUnit(), is(TimeUnit.MILLISECONDS));
    }

    @Test(timeout = 60000)
    public void testOnResponseContentReceived() throws Exception {
        rule.publisher.onResponseContentReceived();
        rule.listener.assertMethodCalled(ResContentReceived);
    }

    @Test(timeout = 60000)
    public void testOnResponseReceiveComplete() throws Exception {
        rule.publisher.onResponseReceiveComplete(1, TimeUnit.MILLISECONDS);
        rule.listener.assertMethodCalled(ResReceiveComplete);
        MatcherAssert.assertThat("Listener not called with duration.", rule.listener.getDuration(), is(1L));
        MatcherAssert.assertThat("Listener not called with time unit.", rule.listener.getTimeUnit(), is(TimeUnit.MILLISECONDS));
    }

    @Test(timeout = 60000)
    public void testOnResponseFailed() throws Exception {
        final Throwable expected = new NullPointerException();
        rule.publisher.onResponseFailed(expected);
        rule.listener.assertMethodCalled(RespFailed);
        MatcherAssert.assertThat("Listener not called with error.", rule.listener.getRecievedError(), is(expected));
    }

    @Test(timeout = 60000)
    public void testOnRequestProcessingComplete() throws Exception {
        rule.publisher.onRequestProcessingComplete(1, TimeUnit.MILLISECONDS);
        rule.listener.assertMethodCalled(ProcessingComplete);
        MatcherAssert.assertThat("Listener not called with duration.", rule.listener.getDuration(), is(1L));
        MatcherAssert.assertThat("Listener not called with time unit.", rule.listener.getTimeUnit(), is(TimeUnit.MILLISECONDS));
    }

    @Test(timeout = 60000)
    public void testOnConnectionCloseFailed() throws Exception {
        final Throwable expected = new NullPointerException();
        rule.publisher.onConnectionCloseFailed(1, TimeUnit.MILLISECONDS, expected);
        rule.listener.getTcpDelegate().assertMethodsCalled(CloseFailed);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnConnectStart() throws Exception {
        rule.publisher.onConnectStart();
        rule.listener.getTcpDelegate().assertMethodsCalled(ConnectStart);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnConnectSuccess() throws Exception {
        rule.publisher.onConnectSuccess(1, TimeUnit.MILLISECONDS);
        rule.listener.getTcpDelegate().assertMethodsCalled(ConnectSuccess);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnConnectFailed() throws Exception {
        rule.publisher.onConnectFailed(1, TimeUnit.MILLISECONDS, new NullPointerException());
        rule.listener.getTcpDelegate().assertMethodsCalled(ConnectFailed);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnPoolReleaseStart() throws Exception {
        rule.publisher.onPoolReleaseStart();
        rule.listener.getTcpDelegate().assertMethodsCalled(ReleaseStart);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnPoolReleaseSuccess() throws Exception {
        rule.publisher.onPoolReleaseSuccess(1, TimeUnit.MILLISECONDS);
        rule.listener.getTcpDelegate().assertMethodsCalled(ReleaseSuccess);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnPoolReleaseFailed() throws Exception {
        rule.publisher.onPoolReleaseFailed(1, TimeUnit.MILLISECONDS, new NullPointerException());
        rule.listener.getTcpDelegate().assertMethodsCalled(ReleaseFailed);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnPooledConnectionEviction() throws Exception {
        rule.publisher.onPooledConnectionEviction();
        rule.listener.getTcpDelegate().assertMethodsCalled(Eviction);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnPooledConnectionReuse() throws Exception {
        rule.publisher.onPooledConnectionReuse();
        rule.listener.getTcpDelegate().assertMethodsCalled(Reuse);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnPoolAcquireStart() throws Exception {
        rule.publisher.onPoolAcquireStart();
        rule.listener.getTcpDelegate().assertMethodsCalled(AcquireStart);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnPoolAcquireSuccess() throws Exception {
        rule.publisher.onPoolAcquireSuccess(1, TimeUnit.MILLISECONDS);
        rule.listener.getTcpDelegate().assertMethodsCalled(AcquireSuccess);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnPoolAcquireFailed() throws Exception {
        rule.publisher.onPoolAcquireFailed(1, TimeUnit.MILLISECONDS, new NullPointerException());
        rule.listener.getTcpDelegate().assertMethodsCalled(AcquireFailed);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnByteRead() throws Exception {
        rule.publisher.onByteRead(1);
        rule.listener.getTcpDelegate().assertMethodsCalled(BytesRead);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnByteWritten() throws Exception {
        rule.publisher.onByteWritten(1);
        rule.listener.getTcpDelegate().assertMethodsCalled(BytesWritten);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnFlushStart() throws Exception {
        rule.publisher.onFlushStart();
        rule.listener.getTcpDelegate().assertMethodsCalled(FlushStart);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnFlushSuccess() throws Exception {
        rule.publisher.onFlushComplete(1, TimeUnit.MILLISECONDS);
        rule.listener.getTcpDelegate().assertMethodsCalled(FlushSuccess);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnWriteStart() throws Exception {
        rule.publisher.onWriteStart();
        rule.listener.getTcpDelegate().assertMethodsCalled(WriteStart);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnWriteSuccess() throws Exception {
        rule.publisher.onWriteSuccess(1, TimeUnit.MILLISECONDS);
        rule.listener.getTcpDelegate().assertMethodsCalled(WriteSuccess);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnWriteFailed() throws Exception {
        rule.publisher.onWriteFailed(1, TimeUnit.MILLISECONDS, new NullPointerException());
        rule.listener.getTcpDelegate().assertMethodsCalled(WriteFailed);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnConnectionCloseStart() throws Exception {
        rule.publisher.onConnectionCloseStart();
        rule.listener.getTcpDelegate().assertMethodsCalled(CloseStart);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnConnectionCloseSuccess() throws Exception {
        rule.publisher.onConnectionCloseSuccess(1, TimeUnit.MILLISECONDS);
        rule.listener.getTcpDelegate().assertMethodsCalled(CloseSuccess);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnCustomEvent() throws Exception {
        rule.publisher.onCustomEvent("Hello");
        rule.listener.getTcpDelegate().assertMethodsCalled(CustomEvent);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnCustomEventWithError() throws Exception {
        rule.publisher.onCustomEvent("Hello", new NullPointerException());
        rule.listener.getTcpDelegate().assertMethodsCalled(CustomEventWithError);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnCustomEventWithDuration() throws Exception {
        rule.publisher.onCustomEvent("Hello", 1, TimeUnit.MINUTES);
        rule.listener.getTcpDelegate().assertMethodsCalled(CustomEventWithDuration);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testOnCustomEventWithDurationAndError() throws Exception {
        rule.publisher.onCustomEvent("Hello", 1, TimeUnit.MINUTES, new NullPointerException());
        rule.listener.getTcpDelegate().assertMethodsCalled(CustomEventWithDurationAndError);// Test for TCP should verify rest

    }

    @Test(timeout = 60000)
    public void testPublishingEnabled() throws Exception {
        MatcherAssert.assertThat("Publishing not enabled.", rule.publisher.publishingEnabled(), is(true));
    }

    public static class PublisherRule extends ExternalResource {
        private HttpClientEventsListenerImpl listener;

        private HttpClientEventPublisher publisher;

        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    listener = new HttpClientEventsListenerImpl();
                    publisher = new HttpClientEventPublisher();
                    publisher.subscribe(listener);
                    base.evaluate();
                }
            };
        }
    }
}

