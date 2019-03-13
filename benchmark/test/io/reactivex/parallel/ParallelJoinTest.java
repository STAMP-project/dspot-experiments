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
package io.reactivex.parallel;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.subscribers.TestSubscriber;
import java.util.List;
import org.junit.Test;
import org.reactivestreams.Subscriber;


public class ParallelJoinTest {
    @Test
    public void overflowFastpath() {
        sequential(1).test(0).assertFailure(MissingBackpressureException.class);
    }

    @Test
    public void overflowSlowpath() {
        @SuppressWarnings("unchecked")
        final Subscriber<? super Integer>[] subs = new Subscriber[1];
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(1) {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                subs[0].onNext(2);
                subs[0].onNext(3);
            }
        };
        sequential(1).subscribe(ts);
        ts.assertFailure(MissingBackpressureException.class, 1);
    }

    @Test
    public void emptyBackpressured() {
        Flowable.empty().parallel().sequential().test(0).assertResult();
    }

    @Test
    public void overflowFastpathDelayError() {
        sequentialDelayError(1).test(0).requestMore(1).assertFailure(MissingBackpressureException.class, 1);
    }

    @Test
    public void overflowSlowpathDelayError() {
        @SuppressWarnings("unchecked")
        final Subscriber<? super Integer>[] subs = new Subscriber[1];
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(1) {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    subs[0].onNext(2);
                    subs[0].onNext(3);
                }
            }
        };
        sequentialDelayError(1).subscribe(ts);
        ts.request(1);
        ts.assertFailure(MissingBackpressureException.class, 1, 2);
    }

    @Test
    public void emptyBackpressuredDelayError() {
        Flowable.empty().parallel().sequentialDelayError().test(0).assertResult();
    }

    @Test
    public void delayError() {
        TestSubscriber<Integer> flow = Flowable.range(1, 2).parallel(2).map(new io.reactivex.functions.Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).sequentialDelayError().test().assertFailure(CompositeException.class);
        List<Throwable> error = TestHelper.errorList(flow);
        TestHelper.assertError(error, 0, TestException.class);
        TestHelper.assertError(error, 1, TestException.class);
    }

    @Test
    public void normalDelayError() {
        sequentialDelayError(1).test().assertResult(1);
    }

    @Test
    public void rangeDelayError() {
        sequentialDelayError(1).take(1).test().assertResult(1);
    }

    @Test
    public void rangeDelayErrorBackpressure() {
        sequentialDelayError(1).take(2).rebatchRequests(1).test().assertResult(1, 2);
    }

    @Test
    public void rangeDelayErrorBackpressure2() {
        sequentialDelayError(1).rebatchRequests(1).test().assertResult(1, 2, 3);
    }

    @Test
    public void delayErrorCancelBackpressured() {
        TestSubscriber<Integer> ts = sequentialDelayError(1).test(0);
        ts.cancel();
        ts.assertEmpty();
    }

    @Test
    public void delayErrorCancelBackpressured2() {
        TestSubscriber<Integer> ts = sequentialDelayError(1).test(0);
        ts.assertResult();
    }

    @Test
    public void consumerCancelsAfterOne() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(1) {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                cancel();
                onComplete();
            }
        };
        Flowable.range(1, 3).parallel(1).sequential().subscribe(ts);
        ts.assertResult(1);
    }

    @Test
    public void delayErrorConsumerCancelsAfterOne() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(1) {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                cancel();
                onComplete();
            }
        };
        Flowable.range(1, 3).parallel(1).sequentialDelayError().subscribe(ts);
        ts.assertResult(1);
    }

    @Test
    public void delayErrorDrainTrigger() {
        Flowable.range(1, 3).parallel(1).sequentialDelayError().test(0).requestMore(1).assertValues(1).requestMore(1).assertValues(1, 2).requestMore(1).assertResult(1, 2, 3);
    }

    @Test
    public void failedRailIsIgnored() {
        Flowable.range(1, 4).parallel(2).map(new io.reactivex.functions.Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) throws Exception {
                if (v == 1) {
                    throw new TestException();
                }
                return v;
            }
        }).sequentialDelayError().test().assertFailure(TestException.class, 2, 3, 4);
    }

    @Test
    public void failedRailIsIgnoredHidden() {
        Flowable.range(1, 4).hide().parallel(2).map(new io.reactivex.functions.Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) throws Exception {
                if (v == 1) {
                    throw new TestException();
                }
                return v;
            }
        }).sequentialDelayError().test().assertFailure(TestException.class, 2, 3, 4);
    }
}

