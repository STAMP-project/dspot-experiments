/**
 * Copyright 2016 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.concurrent;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class PromiseCombinerTest {
    @Mock
    private Promise<Void> p1;

    private GenericFutureListener<Future<Void>> l1;

    private final PromiseCombinerTest.GenericFutureListenerConsumer l1Consumer = new PromiseCombinerTest.GenericFutureListenerConsumer() {
        @Override
        public void accept(GenericFutureListener<Future<Void>> listener) {
            l1 = listener;
        }
    };

    @Mock
    private Promise<Void> p2;

    private GenericFutureListener<Future<Void>> l2;

    private final PromiseCombinerTest.GenericFutureListenerConsumer l2Consumer = new PromiseCombinerTest.GenericFutureListenerConsumer() {
        @Override
        public void accept(GenericFutureListener<Future<Void>> listener) {
            l2 = listener;
        }
    };

    @Mock
    private Promise<Void> p3;

    private PromiseCombiner combiner;

    @Test
    public void testNullArgument() {
        try {
            combiner.finish(null);
            Assert.fail();
        } catch (NullPointerException expected) {
            // expected
        }
        combiner.finish(p1);
        Mockito.verify(p1).trySuccess(null);
    }

    @Test
    public void testNullAggregatePromise() {
        combiner.finish(p1);
        Mockito.verify(p1).trySuccess(null);
    }

    @Test(expected = NullPointerException.class)
    public void testAddNullPromise() {
        combiner.add(null);
    }

    @Test(expected = NullPointerException.class)
    public void testAddAllNullPromise() {
        combiner.addAll(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddAfterFinish() {
        combiner.finish(p1);
        combiner.add(p2);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IllegalStateException.class)
    public void testAddAllAfterFinish() {
        combiner.finish(p1);
        combiner.addAll(p2);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IllegalStateException.class)
    public void testFinishCalledTwiceThrows() {
        combiner.finish(p1);
        combiner.finish(p1);
    }

    @Test
    public void testAddAllSuccess() throws Exception {
        PromiseCombinerTest.mockSuccessPromise(p1, l1Consumer);
        PromiseCombinerTest.mockSuccessPromise(p2, l2Consumer);
        combiner.addAll(p1, p2);
        combiner.finish(p3);
        l1.operationComplete(p1);
        PromiseCombinerTest.verifyNotCompleted(p3);
        l2.operationComplete(p2);
        PromiseCombinerTest.verifySuccess(p3);
    }

    @Test
    public void testAddSuccess() throws Exception {
        PromiseCombinerTest.mockSuccessPromise(p1, l1Consumer);
        PromiseCombinerTest.mockSuccessPromise(p2, l2Consumer);
        combiner.add(p1);
        l1.operationComplete(p1);
        combiner.add(p2);
        l2.operationComplete(p2);
        PromiseCombinerTest.verifyNotCompleted(p3);
        combiner.finish(p3);
        PromiseCombinerTest.verifySuccess(p3);
    }

    @Test
    public void testAddAllFail() throws Exception {
        RuntimeException e1 = new RuntimeException("fake exception 1");
        RuntimeException e2 = new RuntimeException("fake exception 2");
        PromiseCombinerTest.mockFailedPromise(p1, e1, l1Consumer);
        PromiseCombinerTest.mockFailedPromise(p2, e2, l2Consumer);
        combiner.addAll(p1, p2);
        combiner.finish(p3);
        l1.operationComplete(p1);
        PromiseCombinerTest.verifyNotCompleted(p3);
        l2.operationComplete(p2);
        PromiseCombinerTest.verifyFail(p3, e1);
    }

    @Test
    public void testAddFail() throws Exception {
        RuntimeException e1 = new RuntimeException("fake exception 1");
        RuntimeException e2 = new RuntimeException("fake exception 2");
        PromiseCombinerTest.mockFailedPromise(p1, e1, l1Consumer);
        PromiseCombinerTest.mockFailedPromise(p2, e2, l2Consumer);
        combiner.add(p1);
        l1.operationComplete(p1);
        combiner.add(p2);
        l2.operationComplete(p2);
        PromiseCombinerTest.verifyNotCompleted(p3);
        combiner.finish(p3);
        PromiseCombinerTest.verifyFail(p3, e1);
    }

    @Test
    public void testEventExecutor() {
        EventExecutor executor = Mockito.mock(EventExecutor.class);
        Mockito.when(executor.inEventLoop()).thenReturn(false);
        combiner = new PromiseCombiner(executor);
        Future<?> future = Mockito.mock(Future.class);
        try {
            combiner.add(future);
            Assert.fail();
        } catch (IllegalStateException expected) {
            // expected
        }
        try {
            combiner.addAll(future);
            Assert.fail();
        } catch (IllegalStateException expected) {
            // expected
        }
        @SuppressWarnings("unchecked")
        Promise<Void> promise = ((Promise<Void>) (Mockito.mock(Promise.class)));
        try {
            combiner.finish(promise);
            Assert.fail();
        } catch (IllegalStateException expected) {
            // expected
        }
    }

    interface GenericFutureListenerConsumer {
        void accept(GenericFutureListener<Future<Void>> listener);
    }
}

