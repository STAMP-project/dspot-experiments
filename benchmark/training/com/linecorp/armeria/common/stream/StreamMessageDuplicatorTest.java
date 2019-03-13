/**
 * Copyright 2017 LINE Corporation
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


import ImmediateEventExecutor.INSTANCE;
import com.linecorp.armeria.common.stream.AbstractStreamMessageDuplicator.DownstreamSubscription;
import com.linecorp.armeria.common.stream.AbstractStreamMessageDuplicator.SignalQueue;
import com.linecorp.armeria.common.stream.AbstractStreamMessageDuplicator.StreamMessageProcessor;
import com.linecorp.armeria.common.util.Exceptions;
import com.linecorp.armeria.testing.internal.AnticipatedException;
import io.netty.buffer.ByteBuf;
import io.netty.util.IllegalReferenceCountException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StreamMessageDuplicatorTest {
    private static final Logger logger = LoggerFactory.getLogger(StreamMessageDuplicatorTest.class);

    @Rule
    public TestRule globalTimeout = new DisableOnDebug(new Timeout(10, TimeUnit.SECONDS));

    @Test
    public void subscribeTwice() {
        @SuppressWarnings("unchecked")
        final StreamMessage<String> publisher = Mockito.mock(StreamMessage.class);
        Mockito.when(publisher.completionFuture()).thenReturn(new CompletableFuture());
        final StreamMessageDuplicatorTest.StreamMessageDuplicator duplicator = new StreamMessageDuplicatorTest.StreamMessageDuplicator(publisher);
        @SuppressWarnings("unchecked")
        final ArgumentCaptor<StreamMessageProcessor<String>> processorCaptor = ArgumentCaptor.forClass(StreamMessageProcessor.class);
        Mockito.verify(publisher).subscribe(processorCaptor.capture(), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(true));
        Mockito.verify(publisher).subscribe(ArgumentMatchers.any(), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(true));
        final Subscriber<String> subscriber1 = StreamMessageDuplicatorTest.subscribeWithMock(duplicateStream());
        final Subscriber<String> subscriber2 = StreamMessageDuplicatorTest.subscribeWithMock(duplicateStream());
        // Publisher's subscribe() is not invoked when a new subscriber subscribes.
        Mockito.verify(publisher).subscribe(ArgumentMatchers.any(), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(true));
        final StreamMessageProcessor<String> processor = processorCaptor.getValue();
        // Verify that the propagated triggers onSubscribe().
        Mockito.verify(subscriber1, Mockito.never()).onSubscribe(ArgumentMatchers.any());
        Mockito.verify(subscriber2, Mockito.never()).onSubscribe(ArgumentMatchers.any());
        processor.onSubscribe(Mockito.mock(Subscription.class));
        Mockito.verify(subscriber1).onSubscribe(ArgumentMatchers.any(DownstreamSubscription.class));
        Mockito.verify(subscriber2).onSubscribe(ArgumentMatchers.any(DownstreamSubscription.class));
        close();
    }

    @Test
    public void closePublisherNormally() throws Exception {
        final DefaultStreamMessage<String> publisher = new DefaultStreamMessage();
        final StreamMessageDuplicatorTest.StreamMessageDuplicator duplicator = new StreamMessageDuplicatorTest.StreamMessageDuplicator(publisher);
        final CompletableFuture<String> future1 = StreamMessageDuplicatorTest.subscribe(duplicateStream());
        final CompletableFuture<String> future2 = StreamMessageDuplicatorTest.subscribe(duplicateStream());
        StreamMessageDuplicatorTest.writeData(publisher);
        publisher.close();
        assertThat(future1.get()).isEqualTo("Armeria is awesome.");
        assertThat(future2.get()).isEqualTo("Armeria is awesome.");
        close();
    }

    @Test
    public void closePublisherExceptionally() throws Exception {
        final DefaultStreamMessage<String> publisher = new DefaultStreamMessage();
        final StreamMessageDuplicatorTest.StreamMessageDuplicator duplicator = new StreamMessageDuplicatorTest.StreamMessageDuplicator(publisher);
        final CompletableFuture<String> future1 = StreamMessageDuplicatorTest.subscribe(duplicateStream());
        final CompletableFuture<String> future2 = StreamMessageDuplicatorTest.subscribe(duplicateStream());
        StreamMessageDuplicatorTest.writeData(publisher);
        publisher.close(Exceptions.clearTrace(new AnticipatedException()));
        assertThatThrownBy(future1::join).hasCauseInstanceOf(AnticipatedException.class);
        assertThatThrownBy(future2::join).hasCauseInstanceOf(AnticipatedException.class);
        close();
    }

    @Test
    public void subscribeAfterPublisherClosed() throws Exception {
        final DefaultStreamMessage<String> publisher = new DefaultStreamMessage();
        final StreamMessageDuplicatorTest.StreamMessageDuplicator duplicator = new StreamMessageDuplicatorTest.StreamMessageDuplicator(publisher);
        final CompletableFuture<String> future1 = StreamMessageDuplicatorTest.subscribe(duplicateStream());
        StreamMessageDuplicatorTest.writeData(publisher);
        publisher.close();
        assertThat(future1.get()).isEqualTo("Armeria is awesome.");
        // Still subscribable.
        final CompletableFuture<String> future2 = StreamMessageDuplicatorTest.subscribe(duplicateStream());
        assertThat(future2.get()).isEqualTo("Armeria is awesome.");
        close();
    }

    @Test
    public void childStreamIsNotClosedWhenDemandIsNotEnough() throws Exception {
        final DefaultStreamMessage<String> publisher = new DefaultStreamMessage();
        final StreamMessageDuplicatorTest.StreamMessageDuplicator duplicator = new StreamMessageDuplicatorTest.StreamMessageDuplicator(publisher);
        final CompletableFuture<String> future1 = new CompletableFuture<>();
        final StreamMessageDuplicatorTest.StringSubscriber subscriber = new StreamMessageDuplicatorTest.StringSubscriber(future1, 2);
        final StreamMessage<String> sm = duplicator.duplicateStream();
        sm.completionFuture().whenComplete(subscriber);
        sm.subscribe(subscriber);
        final CompletableFuture<String> future2 = StreamMessageDuplicatorTest.subscribe(duplicateStream(), 3);
        StreamMessageDuplicatorTest.writeData(publisher);
        publisher.close();
        assertThat(future2.get()).isEqualTo("Armeria is awesome.");
        assertThat(future1.isDone()).isEqualTo(false);
        subscriber.requestAnother();
        assertThat(future1.get()).isEqualTo("Armeria is awesome.");
        close();
    }

    @Test
    public void abortPublisherWithSubscribers() {
        final DefaultStreamMessage<String> publisher = new DefaultStreamMessage();
        final StreamMessageDuplicatorTest.StreamMessageDuplicator duplicator = new StreamMessageDuplicatorTest.StreamMessageDuplicator(publisher);
        final CompletableFuture<String> future = StreamMessageDuplicatorTest.subscribe(duplicateStream());
        publisher.abort();
        assertThatThrownBy(future::join).hasCauseInstanceOf(AbortedStreamException.class);
        close();
    }

    @Test
    public void abortPublisherWithoutSubscriber() {
        final DefaultStreamMessage<String> publisher = new DefaultStreamMessage();
        final StreamMessageDuplicatorTest.StreamMessageDuplicator duplicator = new StreamMessageDuplicatorTest.StreamMessageDuplicator(publisher);
        publisher.abort();
        // Completed exceptionally once a subscriber subscribes.
        final CompletableFuture<String> future = StreamMessageDuplicatorTest.subscribe(duplicateStream());
        assertThatThrownBy(future::join).hasCauseInstanceOf(AbortedStreamException.class);
        close();
    }

    @Test
    public void abortChildStream() {
        final DefaultStreamMessage<String> publisher = new DefaultStreamMessage();
        final StreamMessageDuplicatorTest.StreamMessageDuplicator duplicator = new StreamMessageDuplicatorTest.StreamMessageDuplicator(publisher);
        final StreamMessage<String> sm1 = duplicator.duplicateStream();
        final CompletableFuture<String> future1 = StreamMessageDuplicatorTest.subscribe(sm1);
        final StreamMessage<String> sm2 = duplicator.duplicateStream();
        final CompletableFuture<String> future2 = StreamMessageDuplicatorTest.subscribe(sm2);
        sm1.abort();
        assertThatThrownBy(future1::join).hasCauseInstanceOf(AbortedStreamException.class);
        // Aborting from another subscriber does not affect other subscribers.
        assertThat(sm2.isOpen()).isTrue();
        sm2.abort();
        assertThatThrownBy(future2::join).hasCauseInstanceOf(AbortedStreamException.class);
        close();
    }

    @Test
    public void closeMulticastStreamFactory() {
        final DefaultStreamMessage<String> publisher = new DefaultStreamMessage();
        final StreamMessageDuplicatorTest.StreamMessageDuplicator duplicator = new StreamMessageDuplicatorTest.StreamMessageDuplicator(publisher);
        close();
        assertThatThrownBy(duplicator::duplicateStream).isInstanceOf(IllegalStateException.class);
    }

    /**
     * A test for the {@link SignalQueue} in {@link AbstractStreamMessageDuplicator}.
     * Queue expansion behaves differently when odd/even number head wrap-around happens.
     */
    @Test
    public void circularQueueOddNumHeadWrapAround() {
        final SignalQueue queue = new SignalQueue(( obj) -> 4);
        StreamMessageDuplicatorTest.add(queue, 0, 10);
        assertThat(queue.size()).isEqualTo(10);
        queue.requestRemovalAheadOf(8);
        assertThat(queue.size()).isEqualTo(10);// removing elements happens when adding a element

        int removedLength = queue.addAndRemoveIfRequested(10);
        assertThat(removedLength).isEqualTo((8 * 4));
        assertThat(queue.size()).isEqualTo(3);// 11 - 8 elements

        StreamMessageDuplicatorTest.add(queue, 11, 20);
        queue.requestRemovalAheadOf(20);// head wrap around happens

        assertThat(queue.elements.length).isEqualTo(16);
        removedLength = queue.addAndRemoveIfRequested(20);
        assertThat(removedLength).isEqualTo((12 * 4));
        StreamMessageDuplicatorTest.add(queue, 21, 40);
        // queue expansion happens
        assertThat(queue.elements.length).isEqualTo(32);
        for (int i = 20; i < 40; i++) {
            assertThat(queue.get(i)).isEqualTo(i);
        }
        assertThat(queue.size()).isEqualTo(20);
    }

    /**
     * A test for the {@link SignalQueue} in {@link AbstractStreamMessageDuplicator}.
     * Queue expansion behaves differently when odd/even number head wrap-around happens.
     */
    @Test
    public void circularQueueEvenNumHeadWrapAround() {
        final SignalQueue queue = new SignalQueue(( obj) -> 4);
        StreamMessageDuplicatorTest.add(queue, 0, 10);
        queue.requestRemovalAheadOf(10);
        StreamMessageDuplicatorTest.add(queue, 10, 20);
        queue.requestRemovalAheadOf(20);// first head wrap around

        StreamMessageDuplicatorTest.add(queue, 20, 30);
        queue.requestRemovalAheadOf(30);
        StreamMessageDuplicatorTest.add(queue, 30, 40);
        queue.requestRemovalAheadOf(40);// second head wrap around

        StreamMessageDuplicatorTest.add(queue, 40, 60);
        // queue expansion happens
        for (int i = 40; i < 60; i++) {
            assertThat(queue.get(i)).isEqualTo(i);
        }
    }

    @Test
    public void lastDuplicateStream() {
        final DefaultStreamMessage<ByteBuf> publisher = new DefaultStreamMessage();
        final StreamMessageDuplicatorTest.ByteBufDuplicator duplicator = new StreamMessageDuplicatorTest.ByteBufDuplicator(publisher);
        duplicateStream().subscribe(new StreamMessageDuplicatorTest.ByteBufSubscriber(), INSTANCE);
        duplicator.duplicateStream(true).subscribe(new StreamMessageDuplicatorTest.ByteBufSubscriber(), INSTANCE);
        // duplicateStream() is not allowed anymore.
        assertThatThrownBy(duplicator::duplicateStream).isInstanceOf(IllegalStateException.class);
        // Only used to read refCnt, not an actual reference.
        final ByteBuf[] bufs = new ByteBuf[30];
        for (int i = 0; i < 30; i++) {
            final ByteBuf buf = StreamMessageDuplicatorTest.newUnpooledBuffer();
            bufs[i] = buf;
            publisher.write(buf);
            assertThat(buf.refCnt()).isOne();
        }
        for (int i = 0; i < 25; i++) {
            // first 25 signals are removed from the queue.
            assertThat(bufs[i].refCnt()).isZero();
        }
        for (int i = 25; i < 30; i++) {
            // rest of them are still in the queue.
            assertThat(bufs[i].refCnt()).isOne();
        }
        close();
        for (int i = 25; i < 30; i++) {
            // rest of them are cleared after calling duplicator.close()
            assertThat(bufs[i].refCnt()).isZero();
        }
    }

    @Test
    public void raiseExceptionInOnNext() {
        final DefaultStreamMessage<ByteBuf> publisher = new DefaultStreamMessage();
        final StreamMessageDuplicatorTest.ByteBufDuplicator duplicator = new StreamMessageDuplicatorTest.ByteBufDuplicator(publisher);
        final ByteBuf buf = StreamMessageDuplicatorTest.newUnpooledBuffer();
        publisher.write(buf);
        assertThat(buf.refCnt()).isOne();
        // Release the buf after writing to the publisher which must not happen!
        buf.release();
        final StreamMessageDuplicatorTest.ByteBufSubscriber subscriber = new StreamMessageDuplicatorTest.ByteBufSubscriber();
        duplicateStream().subscribe(subscriber, INSTANCE);
        assertThatThrownBy(() -> subscriber.completionFuture().get()).hasCauseInstanceOf(IllegalReferenceCountException.class);
    }

    private static class StreamMessageDuplicator extends AbstractStreamMessageDuplicator<String, StreamMessage<String>> {
        StreamMessageDuplicator(StreamMessage<String> publisher) {
            super(publisher, String::length, INSTANCE, 0);
        }

        @Override
        public StreamMessage<String> doDuplicateStream(StreamMessage<String> delegate) {
            return new StreamMessageWrapper(delegate);
        }
    }

    private static class StringSubscriber implements BiConsumer<Void, Throwable> , Subscriber<String> {
        private final CompletableFuture<String> future;

        private final StringBuffer sb = new StringBuffer();

        private final long demand;

        private Subscription subscription;

        StringSubscriber(CompletableFuture<String> future, long demand) {
            this.future = future;
            this.demand = demand;
        }

        @Override
        public void onSubscribe(Subscription s) {
            StreamMessageDuplicatorTest.logger.debug("{}: onSubscribe({})", this, Integer.toHexString(System.identityHashCode(s)));
            subscription = s;
            s.request(demand);
        }

        @Override
        public void onNext(String s) {
            StreamMessageDuplicatorTest.logger.debug("{}: onNext(\"{}\")", this, s);
            sb.append(s);
        }

        @Override
        @SuppressWarnings("UnnecessaryCallToStringValueOf")
        public void onError(Throwable t) {
            StreamMessageDuplicatorTest.logger.debug("{}: onError({})", this, String.valueOf(t), t);
        }

        @Override
        public void onComplete() {
            StreamMessageDuplicatorTest.logger.debug("{}: onComplete()", this);
        }

        @Override
        @SuppressWarnings("UnnecessaryCallToStringValueOf")
        public void accept(Void aVoid, Throwable cause) {
            StreamMessageDuplicatorTest.logger.debug("{}: completionFuture({})", this, String.valueOf(cause), cause);
            if (cause != null) {
                future.completeExceptionally(cause);
            } else {
                future.complete(sb.toString());
            }
        }

        void requestAnother() {
            subscription.request(1);
        }

        @Override
        public String toString() {
            return Integer.toHexString(hashCode());
        }
    }

    private static class ByteBufDuplicator extends AbstractStreamMessageDuplicator<ByteBuf, StreamMessage<ByteBuf>> {
        ByteBufDuplicator(StreamMessage<ByteBuf> publisher) {
            super(publisher, ByteBuf::capacity, INSTANCE, 0);
        }

        @Override
        protected StreamMessage<ByteBuf> doDuplicateStream(StreamMessage<ByteBuf> delegate) {
            return new StreamMessageWrapper(delegate);
        }
    }

    private static class ByteBufSubscriber implements Subscriber<ByteBuf> {
        private final CompletableFuture<Void> completionFuture = new CompletableFuture<>();

        public CompletableFuture<Void> completionFuture() {
            return completionFuture;
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(ByteBuf o) {
        }

        @Override
        public void onError(Throwable throwable) {
            completionFuture.completeExceptionally(throwable);
        }

        @Override
        public void onComplete() {
            completionFuture.complete(null);
        }
    }
}

