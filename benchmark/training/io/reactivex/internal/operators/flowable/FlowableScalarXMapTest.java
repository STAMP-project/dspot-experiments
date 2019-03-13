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


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.subscribers.TestSubscriber;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Test;


public class FlowableScalarXMapTest {
    @Test
    public void utilityClass() {
        TestHelper.checkUtilityClass(FlowableScalarXMap.class);
    }

    static final class CallablePublisher implements Publisher<Integer> , Callable<Integer> {
        @Override
        public void subscribe(Subscriber<? super Integer> s) {
            EmptySubscription.error(new TestException(), s);
        }

        @Override
        public Integer call() throws Exception {
            throw new TestException();
        }
    }

    static final class EmptyCallablePublisher implements Publisher<Integer> , Callable<Integer> {
        @Override
        public void subscribe(Subscriber<? super Integer> s) {
            EmptySubscription.complete(s);
        }

        @Override
        public Integer call() throws Exception {
            return null;
        }
    }

    static final class OneCallablePublisher implements Publisher<Integer> , Callable<Integer> {
        @Override
        public void subscribe(Subscriber<? super Integer> s) {
            s.onSubscribe(new ScalarSubscription<Integer>(s, 1));
        }

        @Override
        public Integer call() throws Exception {
            return 1;
        }
    }

    @Test
    public void tryScalarXMap() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Assert.assertTrue(FlowableScalarXMap.tryScalarXMapSubscribe(new FlowableScalarXMapTest.CallablePublisher(), ts, new io.reactivex.functions.Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer f) throws Exception {
                return Flowable.just(1);
            }
        }));
        ts.assertFailure(TestException.class);
    }

    @Test
    public void emptyXMap() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Assert.assertTrue(FlowableScalarXMap.tryScalarXMapSubscribe(new FlowableScalarXMapTest.EmptyCallablePublisher(), ts, new io.reactivex.functions.Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer f) throws Exception {
                return Flowable.just(1);
            }
        }));
        ts.assertResult();
    }

    @Test
    public void mapperCrashes() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Assert.assertTrue(FlowableScalarXMap.tryScalarXMapSubscribe(new FlowableScalarXMapTest.OneCallablePublisher(), ts, new io.reactivex.functions.Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer f) throws Exception {
                throw new TestException();
            }
        }));
        ts.assertFailure(TestException.class);
    }

    @Test
    public void mapperToJust() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Assert.assertTrue(FlowableScalarXMap.tryScalarXMapSubscribe(new FlowableScalarXMapTest.OneCallablePublisher(), ts, new io.reactivex.functions.Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer f) throws Exception {
                return Flowable.just(1);
            }
        }));
        ts.assertResult(1);
    }

    @Test
    public void mapperToEmpty() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Assert.assertTrue(FlowableScalarXMap.tryScalarXMapSubscribe(new FlowableScalarXMapTest.OneCallablePublisher(), ts, new io.reactivex.functions.Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer f) throws Exception {
                return Flowable.empty();
            }
        }));
        ts.assertResult();
    }

    @Test
    public void mapperToCrashingCallable() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Assert.assertTrue(FlowableScalarXMap.tryScalarXMapSubscribe(new FlowableScalarXMapTest.OneCallablePublisher(), ts, new io.reactivex.functions.Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer f) throws Exception {
                return new FlowableScalarXMapTest.CallablePublisher();
            }
        }));
        ts.assertFailure(TestException.class);
    }

    @Test
    public void scalarMapToEmpty() {
        FlowableScalarXMap.scalarXMap(1, new io.reactivex.functions.Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer v) throws Exception {
                return Flowable.empty();
            }
        }).test().assertResult();
    }

    @Test
    public void scalarMapToCrashingCallable() {
        FlowableScalarXMap.scalarXMap(1, new io.reactivex.functions.Function<Integer, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(Integer v) throws Exception {
                return new FlowableScalarXMapTest.CallablePublisher();
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void scalarDisposableStateCheck() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        ScalarSubscription<Integer> sd = new ScalarSubscription<Integer>(ts, 1);
        ts.onSubscribe(sd);
        Assert.assertFalse(sd.isCancelled());
        Assert.assertTrue(sd.isEmpty());
        sd.request(1);
        Assert.assertFalse(sd.isCancelled());
        Assert.assertTrue(sd.isEmpty());
        ts.assertResult(1);
        try {
            sd.offer(1);
            Assert.fail("Should have thrown");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        try {
            sd.offer(1, 2);
            Assert.fail("Should have thrown");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
    }

    @Test
    public void scalarDisposableRunDisposeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
            final ScalarSubscription<Integer> sd = new ScalarSubscription<Integer>(ts, 1);
            ts.onSubscribe(sd);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    sd.request(1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    sd.cancel();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void cancelled() {
        ScalarSubscription<Integer> scalar = new ScalarSubscription<Integer>(new TestSubscriber<Integer>(), 1);
        Assert.assertFalse(scalar.isCancelled());
        scalar.cancel();
        Assert.assertTrue(scalar.isCancelled());
    }
}

