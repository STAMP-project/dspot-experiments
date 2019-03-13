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
package org.springframework.http.server.reactive;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.io.buffer.DataBuffer;


/**
 * Unit tests for {@link AbstractListenerReadPublisher}.
 *
 * @author Violeta Georgieva
 * @author Rossen Stoyanchev
 */
public class ListenerReadPublisherTests {
    private final ListenerReadPublisherTests.TestListenerReadPublisher publisher = new ListenerReadPublisherTests.TestListenerReadPublisher();

    private final ListenerReadPublisherTests.TestSubscriber subscriber = new ListenerReadPublisherTests.TestSubscriber();

    @Test
    public void twoReads() {
        this.subscriber.getSubscription().request(2);
        onDataAvailable();
        Assert.assertEquals(2, this.publisher.getReadCalls());
    }

    // SPR-17410
    @Test
    public void discardDataOnError() {
        this.subscriber.getSubscription().request(2);
        onDataAvailable();
        onError(new IllegalStateException());
        Assert.assertEquals(2, this.publisher.getReadCalls());
        Assert.assertEquals(1, this.publisher.getDiscardCalls());
    }

    // SPR-17410
    @Test
    public void discardDataOnCancel() {
        this.subscriber.getSubscription().request(2);
        this.subscriber.setCancelOnNext(true);
        onDataAvailable();
        Assert.assertEquals(1, this.publisher.getReadCalls());
        Assert.assertEquals(1, this.publisher.getDiscardCalls());
    }

    private static final class TestListenerReadPublisher extends AbstractListenerReadPublisher<DataBuffer> {
        private int readCalls = 0;

        private int discardCalls = 0;

        public TestListenerReadPublisher() {
            super("");
        }

        public int getReadCalls() {
            return this.readCalls;
        }

        public int getDiscardCalls() {
            return this.discardCalls;
        }

        @Override
        protected void checkOnDataAvailable() {
            // no-op
        }

        @Override
        protected DataBuffer read() {
            (this.readCalls)++;
            return Mockito.mock(DataBuffer.class);
        }

        @Override
        protected void readingPaused() {
            // No-op
        }

        @Override
        protected void discardData() {
            (this.discardCalls)++;
        }
    }

    private static final class TestSubscriber implements Subscriber<DataBuffer> {
        private Subscription subscription;

        private boolean cancelOnNext;

        public Subscription getSubscription() {
            return this.subscription;
        }

        public void setCancelOnNext(boolean cancelOnNext) {
            this.cancelOnNext = cancelOnNext;
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
        }

        @Override
        public void onNext(DataBuffer dataBuffer) {
            if (this.cancelOnNext) {
                this.subscription.cancel();
            }
        }

        @Override
        public void onError(Throwable t) {
        }

        @Override
        public void onComplete() {
        }
    }
}

