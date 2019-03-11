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
package io.reactivex.netty.protocol.tcp.client.events;


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
import io.reactivex.netty.protocol.tcp.client.MockTcpClientEventListener;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;


public class TcpClientEventPublisherTest {
    @Rule
    public TcpClientEventPublisherTest.PublisherRule rule = new TcpClientEventPublisherTest.PublisherRule();

    @Test(timeout = 60000)
    public void testOnConnectStart() throws Exception {
        rule.publisher.onConnectStart();
        rule.listener.assertMethodsCalled(ConnectStart);
    }

    @Test(timeout = 60000)
    public void testOnConnectSuccess() throws Exception {
        rule.publisher.onConnectSuccess(1, TimeUnit.MILLISECONDS);
        rule.listener.assertMethodsCalled(ConnectSuccess);
        MatcherAssert.assertThat("Listener not called with duration.", rule.listener.getDuration(), is(1L));
        MatcherAssert.assertThat("Listener not called with time unit.", rule.listener.getTimeUnit(), is(TimeUnit.MILLISECONDS));
    }

    @Test(timeout = 60000)
    public void testOnConnectFailed() throws Exception {
        final Throwable expected = new NullPointerException();
        rule.publisher.onConnectFailed(1, TimeUnit.MILLISECONDS, expected);
        rule.listener.assertMethodsCalled(ConnectFailed);
        MatcherAssert.assertThat("Listener not called with duration.", rule.listener.getDuration(), is(1L));
        MatcherAssert.assertThat("Listener not called with time unit.", rule.listener.getTimeUnit(), is(TimeUnit.MILLISECONDS));
        MatcherAssert.assertThat("Listener not called with error.", rule.listener.getRecievedError(), is(expected));
    }

    @Test(timeout = 60000)
    public void testOnPoolReleaseStart() throws Exception {
        rule.publisher.onPoolReleaseStart();
        rule.listener.assertMethodsCalled(ReleaseStart);
    }

    @Test(timeout = 60000)
    public void testOnPoolReleaseSuccess() throws Exception {
        rule.publisher.onPoolReleaseSuccess(1, TimeUnit.MILLISECONDS);
        rule.listener.assertMethodsCalled(ReleaseSuccess);
        MatcherAssert.assertThat("Listener not called with duration.", rule.listener.getDuration(), is(1L));
        MatcherAssert.assertThat("Listener not called with time unit.", rule.listener.getTimeUnit(), is(TimeUnit.MILLISECONDS));
    }

    @Test(timeout = 60000)
    public void testOnPoolReleaseFailed() throws Exception {
        final Throwable expected = new NullPointerException();
        rule.publisher.onPoolReleaseFailed(1, TimeUnit.MILLISECONDS, expected);
        rule.listener.assertMethodsCalled(ReleaseFailed);
        MatcherAssert.assertThat("Listener not called with duration.", rule.listener.getDuration(), is(1L));
        MatcherAssert.assertThat("Listener not called with time unit.", rule.listener.getTimeUnit(), is(TimeUnit.MILLISECONDS));
        MatcherAssert.assertThat("Listener not called with error.", rule.listener.getRecievedError(), is(expected));
    }

    @Test(timeout = 60000)
    public void testOnPooledConnectionEviction() throws Exception {
        rule.publisher.onPooledConnectionEviction();
        rule.listener.assertMethodsCalled(Eviction);
    }

    @Test(timeout = 60000)
    public void testOnPooledConnectionReuse() throws Exception {
        rule.publisher.onPooledConnectionReuse();
        rule.listener.assertMethodsCalled(Reuse);
    }

    @Test(timeout = 60000)
    public void testOnPoolAcquireStart() throws Exception {
        rule.publisher.onPoolAcquireStart();
        rule.listener.assertMethodsCalled(AcquireStart);
    }

    @Test(timeout = 60000)
    public void testOnPoolAcquireSuccess() throws Exception {
        rule.publisher.onPoolAcquireSuccess(1, TimeUnit.MILLISECONDS);
        rule.listener.assertMethodsCalled(AcquireSuccess);
        MatcherAssert.assertThat("Listener not called with duration.", rule.listener.getDuration(), is(1L));
        MatcherAssert.assertThat("Listener not called with time unit.", rule.listener.getTimeUnit(), is(TimeUnit.MILLISECONDS));
    }

    @Test(timeout = 60000)
    public void testOnPoolAcquireFailed() throws Exception {
        final Throwable expected = new NullPointerException();
        rule.publisher.onPoolAcquireFailed(1, TimeUnit.MILLISECONDS, expected);
        rule.listener.assertMethodsCalled(AcquireFailed);
        MatcherAssert.assertThat("Listener not called with duration.", rule.listener.getDuration(), is(1L));
        MatcherAssert.assertThat("Listener not called with time unit.", rule.listener.getTimeUnit(), is(TimeUnit.MILLISECONDS));
        MatcherAssert.assertThat("Listener not called with error.", rule.listener.getRecievedError(), is(expected));
    }

    @Test(timeout = 60000)
    public void testOnByteRead() throws Exception {
        rule.publisher.onByteRead(1);
        rule.listener.assertMethodsCalled(BytesRead);// Test for Connection publisher should verify rest

    }

    @Test(timeout = 60000)
    public void testOnByteWritten() throws Exception {
        rule.publisher.onByteWritten(1);
        rule.listener.assertMethodsCalled(BytesWritten);// Test for Connection publisher should verify rest

    }

    @Test(timeout = 60000)
    public void testOnFlushStart() throws Exception {
        rule.publisher.onFlushStart();
        rule.listener.assertMethodsCalled(FlushStart);// Test for Connection publisher should verify rest

    }

    @Test(timeout = 60000)
    public void testOnFlushSuccess() throws Exception {
        rule.publisher.onFlushComplete(1, TimeUnit.MILLISECONDS);
        rule.listener.assertMethodsCalled(FlushSuccess);// Test for Connection publisher should verify rest

    }

    @Test(timeout = 60000)
    public void testOnWriteStart() throws Exception {
        rule.publisher.onWriteStart();
        rule.listener.assertMethodsCalled(WriteStart);// Test for Connection publisher should verify rest

    }

    @Test(timeout = 60000)
    public void testOnWriteSuccess() throws Exception {
        rule.publisher.onWriteSuccess(1, TimeUnit.MILLISECONDS);
        rule.listener.assertMethodsCalled(WriteSuccess);// Test for Connection publisher should verify rest

    }

    @Test(timeout = 60000)
    public void testOnWriteFailed() throws Exception {
        rule.publisher.onWriteFailed(1, TimeUnit.MILLISECONDS, new NullPointerException());
        rule.listener.assertMethodsCalled(WriteFailed);// Test for Connection publisher should verify rest

    }

    @Test(timeout = 60000)
    public void testOnConnectionCloseStart() throws Exception {
        rule.publisher.onConnectionCloseStart();
        rule.listener.assertMethodsCalled(CloseStart);// Test for Connection publisher should verify rest

    }

    @Test(timeout = 60000)
    public void testOnConnectionCloseSuccess() throws Exception {
        rule.publisher.onConnectionCloseSuccess(1, TimeUnit.MILLISECONDS);
        rule.listener.assertMethodsCalled(CloseSuccess);// Test for Connection publisher should verify rest

    }

    @Test(timeout = 60000)
    public void testOnConnectionCloseFailed() throws Exception {
        rule.publisher.onConnectionCloseFailed(1, TimeUnit.MILLISECONDS, new NullPointerException());
        rule.listener.assertMethodsCalled(CloseFailed);// Test for Connection publisher should verify rest

    }

    @Test(timeout = 60000)
    public void testPublishingEnabled() throws Exception {
        MatcherAssert.assertThat("Publishing not enabled.", rule.publisher.publishingEnabled(), is(true));
    }

    @Test(timeout = 60000)
    public void testCustomEvent() throws Exception {
        rule.publisher.onCustomEvent("Hello");
        rule.listener.assertMethodsCalled(CustomEvent);// Test for Connection publisher should verify rest

    }

    @Test(timeout = 60000)
    public void testCustomEventWithError() throws Exception {
        rule.publisher.onCustomEvent("Hello", new NullPointerException());
        rule.listener.assertMethodsCalled(CustomEventWithError);// Test for Connection publisher should verify rest

    }

    @Test(timeout = 60000)
    public void testCustomEventWithDuration() throws Exception {
        rule.publisher.onCustomEvent("Hello", 1, TimeUnit.MINUTES);
        rule.listener.assertMethodsCalled(CustomEventWithDuration);// Test for Connection publisher should verify rest

    }

    @Test(timeout = 60000)
    public void testCustomEventWithDurationAndError() throws Exception {
        rule.publisher.onCustomEvent("Hello", 1, TimeUnit.MINUTES, new NullPointerException());
        rule.listener.assertMethodsCalled(CustomEventWithDurationAndError);// Test for Connection publisher should verify rest

    }

    @Test(timeout = 60000)
    public void testCopy() throws Exception {
        TcpClientEventPublisher copy = rule.publisher.copy();
        MatcherAssert.assertThat("Publisher not copied.", copy, is(not(sameInstance(rule.publisher))));
        MatcherAssert.assertThat("Listeners not copied.", copy.getListeners(), is(not(sameInstance(rule.publisher.getListeners()))));
    }

    public static class PublisherRule extends ExternalResource {
        private MockTcpClientEventListener listener;

        private TcpClientEventPublisher publisher;

        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    listener = new MockTcpClientEventListener();
                    publisher = new TcpClientEventPublisher();
                    publisher.subscribe(listener);
                    base.evaluate();
                }
            };
        }
    }
}

