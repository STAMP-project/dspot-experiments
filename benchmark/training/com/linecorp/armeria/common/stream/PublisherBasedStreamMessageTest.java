/**
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.common.stream;


import com.linecorp.armeria.common.stream.PublisherBasedStreamMessage.AbortableSubscriber;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;


public class PublisherBasedStreamMessageTest {
    /**
     * Tests if {@link PublisherBasedStreamMessage#abort()} cancels the {@link Subscription}, and tests if
     * the abort operation is idempotent.
     */
    @Test
    public void testAbortWithEarlyOnSubscribe() {
        final PublisherBasedStreamMessageTest.AbortTest test = new PublisherBasedStreamMessageTest.AbortTest();
        test.prepare();
        test.invokeOnSubscribe();
        test.abortAndAwait();
        test.verify();
        // Try to abort again, which should do nothing.
        test.abortAndAwait();
        test.verify();
    }

    /**
     * Tests if {@link PublisherBasedStreamMessage#abort()} cancels the {@link Subscription} even if
     * {@link Subscriber#onSubscribe(Subscription)} was invoked by the delegate {@link Publisher} after
     * {@link PublisherBasedStreamMessage#abort()} is called.
     */
    @Test
    public void testAbortWithLateOnSubscribe() {
        final PublisherBasedStreamMessageTest.AbortTest test = new PublisherBasedStreamMessageTest.AbortTest();
        test.prepare();
        test.abort();
        test.invokeOnSubscribe();
        test.awaitAbort();
        test.verify();
    }

    /**
     * Tests if {@link PublisherBasedStreamMessage#abort()} prohibits further subscription.
     */
    @Test
    public void testAbortWithoutSubscriber() {
        @SuppressWarnings("unchecked")
        final Publisher<Integer> delegate = Mockito.mock(Publisher.class);
        final PublisherBasedStreamMessage<Integer> p = new PublisherBasedStreamMessage(delegate);
        p.abort();
        // Publisher should not be involved at all because we are aborting without subscribing.
        Mockito.verify(delegate, Mockito.never()).subscribe(ArgumentMatchers.any());
    }

    private static final class AbortTest {
        private PublisherBasedStreamMessage<Integer> publisher;

        private AbortableSubscriber subscriberWrapper;

        private Subscription subscription;

        PublisherBasedStreamMessageTest.AbortTest prepare() {
            // Create a mock delegate Publisher which will be wrapped by PublisherBasedStreamMessage.
            final ArgumentCaptor<AbortableSubscriber> subscriberCaptor = ArgumentCaptor.forClass(AbortableSubscriber.class);
            @SuppressWarnings("unchecked")
            final Publisher<Integer> delegate = Mockito.mock(Publisher.class);
            @SuppressWarnings("unchecked")
            final Subscriber<Integer> subscriber = Mockito.mock(Subscriber.class);
            publisher = new PublisherBasedStreamMessage(delegate);
            // Subscribe.
            publisher.subscribe(subscriber);
            Mockito.verify(delegate).subscribe(subscriberCaptor.capture());
            // Capture the actual Subscriber implementation.
            subscriberWrapper = subscriberCaptor.getValue();
            // Prepare a mock Subscription.
            subscription = Mockito.mock(Subscription.class);
            return this;
        }

        void invokeOnSubscribe() {
            // Call the subscriber.onSubscriber() with the mock Subscription to emulate
            // that the delegate triggers onSubscribe().
            subscriberWrapper.onSubscribe(subscription);
        }

        void abort() {
            publisher.abort();
        }

        void abortAndAwait() {
            abort();
            awaitAbort();
        }

        void awaitAbort() {
            assertThatThrownBy(() -> publisher.completionFuture().join()).hasCauseInstanceOf(AbortedStreamException.class);
        }

        void verify() {
            // Ensure subscription.cancel() has been invoked.
            Mockito.verify(subscription).cancel();
            // Ensure completionFuture is complete exceptionally.
            assertThat(publisher.completionFuture()).isCompletedExceptionally();
            assertThatThrownBy(() -> publisher.completionFuture().get()).hasCauseExactlyInstanceOf(AbortedStreamException.class);
        }
    }
}

