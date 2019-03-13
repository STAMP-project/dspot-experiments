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


import QueueFuseable.ANY;
import QueueFuseable.ASYNC;
import QueueFuseable.SYNC;
import io.reactivex.Subscriber;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.Functions;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.UnicastProcessor;
import io.reactivex.subscribers.SubscriberFusion;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class FlowableDistinctTest {
    Subscriber<String> w;

    // nulls lead to exceptions
    final Function<String, String> TO_UPPER_WITH_EXCEPTION = new Function<String, String>() {
        @Override
        public String apply(String s) {
            if (s.equals("x")) {
                return "XX";
            }
            return s.toUpperCase();
        }
    };

    @Test
    public void testDistinctOfNone() {
        Flowable<String> src = Flowable.empty();
        src.distinct().subscribe(w);
        Mockito.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyString());
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(w, Mockito.times(1)).onComplete();
    }

    @Test
    public void testDistinctOfNoneWithKeySelector() {
        Flowable<String> src = Flowable.empty();
        src.distinct(TO_UPPER_WITH_EXCEPTION).subscribe(w);
        Mockito.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyString());
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(w, Mockito.times(1)).onComplete();
    }

    @Test
    public void testDistinctOfNormalSource() {
        Flowable<String> src = Flowable.just("a", "b", "c", "c", "c", "b", "b", "a", "e");
        src.distinct().subscribe(w);
        InOrder inOrder = Mockito.inOrder(w);
        inOrder.verify(w, Mockito.times(1)).onNext("a");
        inOrder.verify(w, Mockito.times(1)).onNext("b");
        inOrder.verify(w, Mockito.times(1)).onNext("c");
        inOrder.verify(w, Mockito.times(1)).onNext("e");
        inOrder.verify(w, Mockito.times(1)).onComplete();
        inOrder.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyString());
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDistinctOfNormalSourceWithKeySelector() {
        Flowable<String> src = Flowable.just("a", "B", "c", "C", "c", "B", "b", "a", "E");
        src.distinct(TO_UPPER_WITH_EXCEPTION).subscribe(w);
        InOrder inOrder = Mockito.inOrder(w);
        inOrder.verify(w, Mockito.times(1)).onNext("a");
        inOrder.verify(w, Mockito.times(1)).onNext("B");
        inOrder.verify(w, Mockito.times(1)).onNext("c");
        inOrder.verify(w, Mockito.times(1)).onNext("E");
        inOrder.verify(w, Mockito.times(1)).onComplete();
        inOrder.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyString());
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void error() {
        distinct().test().assertFailure(TestException.class);
    }

    @Test
    public void fusedSync() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        distinct().subscribe(ts);
        SubscriberFusion.assertFusion(ts, SYNC).assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void fusedAsync() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        UnicastProcessor<Integer> us = UnicastProcessor.create();
        us.distinct().subscribe(ts);
        TestHelper.emit(us, 1, 1, 2, 1, 3, 2, 4, 5, 4);
        SubscriberFusion.assertFusion(ts, ASYNC).assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void fusedClear() {
        distinct().subscribe(new FlowableSubscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                QueueSubscription<?> qs = ((QueueSubscription<?>) (s));
                Assert.assertFalse(qs.isEmpty());
                qs.clear();
                Assert.assertTrue(qs.isEmpty());
            }

            @Override
            public void onNext(Integer value) {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @Test
    public void collectionSupplierThrows() {
        Flowable.just(1).distinct(Functions.identity(), new Callable<Collection<Object>>() {
            @Override
            public Collection<Object> call() throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void collectionSupplierIsNull() {
        Flowable.just(1).distinct(Functions.identity(), new Callable<Collection<Object>>() {
            @Override
            public Collection<Object> call() throws Exception {
                return null;
            }
        }).test().assertFailure(NullPointerException.class).assertErrorMessage("The collectionSupplier returned a null collection. Null values are generally not allowed in 2.x operators and sources.");
    }

    @Test
    public void badSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            distinct().test().assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

