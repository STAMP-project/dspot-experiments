/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package io.reactivex.internal.operators.flowable;


import io.reactivex.Flowable;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.operators.flowable.BlockingFlowableIterable.BlockingFlowableIterator;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public class BlockingFlowableToIteratorTest {
    @Test
    public void testToIterator() {
        Flowable<String> obs = Flowable.just("one", "two", "three");
        Iterator<String> it = obs.blockingIterable().iterator();
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("one", it.next());
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("two", it.next());
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("three", it.next());
        Assert.assertEquals(false, it.hasNext());
    }

    @Test(expected = TestException.class)
    public void testToIteratorWithException() {
        Flowable<String> obs = Flowable.unsafeCreate(new Publisher<String>() {
            @Override
            public void subscribe(Subscriber<? super String> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                subscriber.onNext("one");
                subscriber.onError(new TestException());
            }
        });
        Iterator<String> it = obs.blockingIterable().iterator();
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("one", it.next());
        Assert.assertEquals(true, it.hasNext());
        it.next();
    }

    @Test
    public void testIteratorExertBackpressure() {
        final BlockingFlowableToIteratorTest.Counter src = new BlockingFlowableToIteratorTest.Counter();
        Flowable<Integer> obs = Flowable.fromIterable(new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return src;
            }
        });
        Iterator<Integer> it = obs.blockingIterable().iterator();
        while (it.hasNext()) {
            // Correct backpressure should cause this interleaved behavior.
            // We first request RxRingBuffer.SIZE. Then in increments of
            // SubscriberIterator.LIMIT.
            int i = it.next();
            int expected = (i - (i % ((Flowable.bufferSize()) - ((Flowable.bufferSize()) >> 2)))) + (Flowable.bufferSize());
            expected = Math.min(expected, BlockingFlowableToIteratorTest.Counter.MAX);
            Assert.assertEquals(expected, src.count);
        } 
    }

    public static final class Counter implements Iterator<Integer> {
        static final int MAX = 5 * (Flowable.bufferSize());

        public int count;

        @Override
        public boolean hasNext() {
            return (count) < (BlockingFlowableToIteratorTest.Counter.MAX);
        }

        @Override
        public Integer next() {
            return ++(count);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void remove() {
        BlockingFlowableIterator<Integer> it = new BlockingFlowableIterator<Integer>(128);
        it.remove();
    }

    @Test
    public void dispose() {
        BlockingFlowableIterator<Integer> it = new BlockingFlowableIterator<Integer>(128);
        Assert.assertFalse(it.isDisposed());
        it.dispose();
        Assert.assertTrue(it.isDisposed());
    }

    @Test
    public void interruptWait() {
        BlockingFlowableIterator<Integer> it = new BlockingFlowableIterator<Integer>(128);
        try {
            Thread.currentThread().interrupt();
            it.hasNext();
        } catch (RuntimeException ex) {
            Assert.assertTrue(ex.toString(), ((ex.getCause()) instanceof InterruptedException));
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void emptyThrowsNoSuch() {
        BlockingFlowableIterator<Integer> it = new BlockingFlowableIterator<Integer>(128);
        it.onComplete();
        it.next();
    }

    @Test(expected = MissingBackpressureException.class)
    public void overflowQueue() {
        Iterator<Integer> it = blockingIterable(1).iterator();
        it.next();
    }
}

