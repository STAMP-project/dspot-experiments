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
package io.reactivex.internal.operators.observable;


import io.reactivex.Function;
import io.reactivex.TestHelper;
import io.reactivex.disposables.Disposables;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableWithLatestFromTest {
    static final BiFunction<Integer, Integer, Integer> COMBINER = new BiFunction<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer t1, Integer t2) {
            return (t1 << 8) + t2;
        }
    };

    static final BiFunction<Integer, Integer, Integer> COMBINER_ERROR = new BiFunction<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer t1, Integer t2) {
            throw new TestException("Forced failure");
        }
    };

    @Test
    public void testSimple() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> other = PublishSubject.create();
        Observer<Integer> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        Observable<Integer> result = source.withLatestFrom(other, ObservableWithLatestFromTest.COMBINER);
        result.subscribe(o);
        source.onNext(1);
        inOrder.verify(o, Mockito.never()).onNext(ArgumentMatchers.anyInt());
        other.onNext(1);
        inOrder.verify(o, Mockito.never()).onNext(ArgumentMatchers.anyInt());
        source.onNext(2);
        inOrder.verify(o).onNext(((2 << 8) + 1));
        other.onNext(2);
        inOrder.verify(o, Mockito.never()).onNext(ArgumentMatchers.anyInt());
        other.onComplete();
        inOrder.verify(o, Mockito.never()).onComplete();
        source.onNext(3);
        inOrder.verify(o).onNext(((3 << 8) + 2));
        source.onComplete();
        inOrder.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testEmptySource() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> other = PublishSubject.create();
        Observable<Integer> result = source.withLatestFrom(other, ObservableWithLatestFromTest.COMBINER);
        TestObserver<Integer> to = new TestObserver<Integer>();
        result.subscribe(to);
        Assert.assertTrue(source.hasObservers());
        Assert.assertTrue(other.hasObservers());
        other.onNext(1);
        source.onComplete();
        to.assertNoErrors();
        to.assertTerminated();
        to.assertNoValues();
        Assert.assertFalse(source.hasObservers());
        Assert.assertFalse(other.hasObservers());
    }

    @Test
    public void testEmptyOther() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> other = PublishSubject.create();
        Observable<Integer> result = source.withLatestFrom(other, ObservableWithLatestFromTest.COMBINER);
        TestObserver<Integer> to = new TestObserver<Integer>();
        result.subscribe(to);
        Assert.assertTrue(source.hasObservers());
        Assert.assertTrue(other.hasObservers());
        source.onNext(1);
        source.onComplete();
        to.assertNoErrors();
        to.assertTerminated();
        to.assertNoValues();
        Assert.assertFalse(source.hasObservers());
        Assert.assertFalse(other.hasObservers());
    }

    @Test
    public void testUnsubscription() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> other = PublishSubject.create();
        Observable<Integer> result = source.withLatestFrom(other, ObservableWithLatestFromTest.COMBINER);
        TestObserver<Integer> to = new TestObserver<Integer>();
        result.subscribe(to);
        Assert.assertTrue(source.hasObservers());
        Assert.assertTrue(other.hasObservers());
        other.onNext(1);
        source.onNext(1);
        to.dispose();
        to.assertValue(((1 << 8) + 1));
        to.assertNoErrors();
        to.assertNotComplete();
        Assert.assertFalse(source.hasObservers());
        Assert.assertFalse(other.hasObservers());
    }

    @Test
    public void testSourceThrows() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> other = PublishSubject.create();
        Observable<Integer> result = source.withLatestFrom(other, ObservableWithLatestFromTest.COMBINER);
        TestObserver<Integer> to = new TestObserver<Integer>();
        result.subscribe(to);
        Assert.assertTrue(source.hasObservers());
        Assert.assertTrue(other.hasObservers());
        other.onNext(1);
        source.onNext(1);
        source.onError(new TestException());
        to.assertTerminated();
        to.assertValue(((1 << 8) + 1));
        to.assertError(TestException.class);
        to.assertNotComplete();
        Assert.assertFalse(source.hasObservers());
        Assert.assertFalse(other.hasObservers());
    }

    @Test
    public void testOtherThrows() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> other = PublishSubject.create();
        Observable<Integer> result = source.withLatestFrom(other, ObservableWithLatestFromTest.COMBINER);
        TestObserver<Integer> to = new TestObserver<Integer>();
        result.subscribe(to);
        Assert.assertTrue(source.hasObservers());
        Assert.assertTrue(other.hasObservers());
        other.onNext(1);
        source.onNext(1);
        other.onError(new TestException());
        to.assertTerminated();
        to.assertValue(((1 << 8) + 1));
        to.assertNotComplete();
        to.assertError(TestException.class);
        Assert.assertFalse(source.hasObservers());
        Assert.assertFalse(other.hasObservers());
    }

    @Test
    public void testFunctionThrows() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> other = PublishSubject.create();
        Observable<Integer> result = source.withLatestFrom(other, ObservableWithLatestFromTest.COMBINER_ERROR);
        TestObserver<Integer> to = new TestObserver<Integer>();
        result.subscribe(to);
        Assert.assertTrue(source.hasObservers());
        Assert.assertTrue(other.hasObservers());
        other.onNext(1);
        source.onNext(1);
        to.assertTerminated();
        to.assertNotComplete();
        to.assertNoValues();
        to.assertError(TestException.class);
        Assert.assertFalse(source.hasObservers());
        Assert.assertFalse(other.hasObservers());
    }

    @Test
    public void testNoDownstreamUnsubscribe() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> other = PublishSubject.create();
        Observable<Integer> result = source.withLatestFrom(other, ObservableWithLatestFromTest.COMBINER);
        TestObserver<Integer> to = new TestObserver<Integer>();
        result.subscribe(to);
        source.onComplete();
        // 2.0.2 - not anymore
        // assertTrue("Not cancelled!", ts.isCancelled());
    }

    static final Function<Object[], String> toArray = new Function<Object[], String>() {
        @Override
        public String apply(Object[] args) {
            return Arrays.toString(args);
        }
    };

    @Test
    public void manySources() {
        PublishSubject<String> ps1 = PublishSubject.create();
        PublishSubject<String> ps2 = PublishSubject.create();
        PublishSubject<String> ps3 = PublishSubject.create();
        PublishSubject<String> main = PublishSubject.create();
        TestObserver<String> to = new TestObserver<String>();
        main.withLatestFrom(new Observable[]{ ps1, ps2, ps3 }, ObservableWithLatestFromTest.toArray).subscribe(to);
        main.onNext("1");
        to.assertNoValues();
        ps1.onNext("a");
        to.assertNoValues();
        ps2.onNext("A");
        to.assertNoValues();
        ps3.onNext("=");
        to.assertNoValues();
        main.onNext("2");
        to.assertValues("[2, a, A, =]");
        ps2.onNext("B");
        to.assertValues("[2, a, A, =]");
        ps3.onComplete();
        to.assertValues("[2, a, A, =]");
        ps1.onNext("b");
        main.onNext("3");
        to.assertValues("[2, a, A, =]", "[3, b, B, =]");
        main.onComplete();
        to.assertValues("[2, a, A, =]", "[3, b, B, =]");
        to.assertNoErrors();
        to.assertComplete();
        Assert.assertFalse("ps1 has subscribers?", ps1.hasObservers());
        Assert.assertFalse("ps2 has subscribers?", ps2.hasObservers());
        Assert.assertFalse("ps3 has subscribers?", ps3.hasObservers());
    }

    @Test
    public void manySourcesIterable() {
        PublishSubject<String> ps1 = PublishSubject.create();
        PublishSubject<String> ps2 = PublishSubject.create();
        PublishSubject<String> ps3 = PublishSubject.create();
        PublishSubject<String> main = PublishSubject.create();
        TestObserver<String> to = new TestObserver<String>();
        main.withLatestFrom(Arrays.<Observable<?>>asList(ps1, ps2, ps3), ObservableWithLatestFromTest.toArray).subscribe(to);
        main.onNext("1");
        to.assertNoValues();
        ps1.onNext("a");
        to.assertNoValues();
        ps2.onNext("A");
        to.assertNoValues();
        ps3.onNext("=");
        to.assertNoValues();
        main.onNext("2");
        to.assertValues("[2, a, A, =]");
        ps2.onNext("B");
        to.assertValues("[2, a, A, =]");
        ps3.onComplete();
        to.assertValues("[2, a, A, =]");
        ps1.onNext("b");
        main.onNext("3");
        to.assertValues("[2, a, A, =]", "[3, b, B, =]");
        main.onComplete();
        to.assertValues("[2, a, A, =]", "[3, b, B, =]");
        to.assertNoErrors();
        to.assertComplete();
        Assert.assertFalse("ps1 has subscribers?", ps1.hasObservers());
        Assert.assertFalse("ps2 has subscribers?", ps2.hasObservers());
        Assert.assertFalse("ps3 has subscribers?", ps3.hasObservers());
    }

    @Test
    public void manySourcesIterableSweep() {
        for (String val : new String[]{ "1"/* , null */
         }) {
            int n = 35;
            for (int i = 0; i < n; i++) {
                List<Observable<?>> sources = new java.util.ArrayList<Observable<?>>();
                List<String> expected = new java.util.ArrayList<String>();
                expected.add(val);
                for (int j = 0; j < i; j++) {
                    sources.add(Observable.just(val));
                    expected.add(String.valueOf(val));
                }
                TestObserver<String> to = new TestObserver<String>();
                PublishSubject<String> main = PublishSubject.create();
                main.withLatestFrom(sources, ObservableWithLatestFromTest.toArray).subscribe(to);
                to.assertNoValues();
                main.onNext(val);
                main.onComplete();
                to.assertValue(expected.toString());
                to.assertNoErrors();
                to.assertComplete();
            }
        }
    }

    @Test
    public void withEmpty() {
        TestObserver<String> to = new TestObserver<String>();
        range(1, 3).withLatestFrom(new Observable<?>[]{ io.reactivex.Observable.just(1), io.reactivex.Observable.empty() }, ObservableWithLatestFromTest.toArray).subscribe(to);
        to.assertNoValues();
        to.assertNoErrors();
        to.assertComplete();
    }

    @Test
    public void withError() {
        TestObserver<String> to = new TestObserver<String>();
        range(1, 3).withLatestFrom(new Observable<?>[]{ io.reactivex.Observable.just(1), io.reactivex.Observable.error(new TestException()) }, ObservableWithLatestFromTest.toArray).subscribe(to);
        to.assertNoValues();
        to.assertError(TestException.class);
        to.assertNotComplete();
    }

    @Test
    public void withMainError() {
        TestObserver<String> to = new TestObserver<String>();
        error(new TestException()).withLatestFrom(new Observable<?>[]{ io.reactivex.Observable.just(1), io.reactivex.Observable.just(1) }, ObservableWithLatestFromTest.toArray).subscribe(to);
        to.assertNoValues();
        to.assertError(TestException.class);
        to.assertNotComplete();
    }

    @Test
    public void with2Others() {
        Observable<Integer> just = just(1);
        TestObserver<List<Integer>> to = new TestObserver<List<Integer>>();
        just.withLatestFrom(just, just, new Function3<Integer, Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer a, Integer b, Integer c) {
                return Arrays.asList(a, b, c);
            }
        }).subscribe(to);
        to.assertValue(Arrays.asList(1, 1, 1));
        to.assertNoErrors();
        to.assertComplete();
    }

    @Test
    public void with3Others() {
        Observable<Integer> just = just(1);
        TestObserver<List<Integer>> to = new TestObserver<List<Integer>>();
        just.withLatestFrom(just, just, just, new Function4<Integer, Integer, Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer a, Integer b, Integer c, Integer d) {
                return Arrays.asList(a, b, c, d);
            }
        }).subscribe(to);
        to.assertValue(Arrays.asList(1, 1, 1, 1));
        to.assertNoErrors();
        to.assertComplete();
    }

    @Test
    public void with4Others() {
        Observable<Integer> just = just(1);
        TestObserver<List<Integer>> to = new TestObserver<List<Integer>>();
        just.withLatestFrom(just, just, just, just, new Function5<Integer, Integer, Integer, Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer a, Integer b, Integer c, Integer d, Integer e) {
                return Arrays.asList(a, b, c, d, e);
            }
        }).subscribe(to);
        to.assertValue(Arrays.asList(1, 1, 1, 1, 1));
        to.assertNoErrors();
        to.assertComplete();
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(just(1).withLatestFrom(just(2), new BiFunction<Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b) throws Exception {
                return a;
            }
        }));
        TestHelper.checkDisposed(just(1).withLatestFrom(just(2), just(3), new Function3<Integer, Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b, Integer c) throws Exception {
                return a;
            }
        }));
    }

    @Test
    public void manyIteratorThrows() {
        just(1).withLatestFrom(new io.reactivex.internal.util.CrashingMappedIterable<Observable<Integer>>(1, 100, 100, new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer v) throws Exception {
                return io.reactivex.Observable.just(2);
            }
        }), new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] a) throws Exception {
                return a;
            }
        }).test().assertFailureAndMessage(TestException.class, "iterator()");
    }

    @Test
    public void manyCombinerThrows() {
        just(1).withLatestFrom(just(2), just(3), new Function3<Integer, Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b, Integer c) throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void manyErrors() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Observable<Integer>() {
                @Override
                protected void subscribeActual(Observer<? extends Integer> observer) {
                    observer.onSubscribe(Disposables.empty());
                    observer.onError(new TestException("First"));
                    observer.onNext(1);
                    observer.onError(new TestException("Second"));
                    observer.onComplete();
                }
            }.withLatestFrom(just(2), just(3), new Function3<Integer, Integer, Integer, Object>() {
                @Override
                public Object apply(Integer a, Integer b, Integer c) throws Exception {
                    return a;
                }
            }).test().assertFailureAndMessage(TestException.class, "First");
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void combineToNull1() {
        just(1).withLatestFrom(just(2), new BiFunction<Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b) throws Exception {
                return null;
            }
        }).test().assertFailure(NullPointerException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void combineToNull2() {
        just(1).withLatestFrom(Arrays.asList(just(2), just(3)), new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] o) throws Exception {
                return null;
            }
        }).test().assertFailure(NullPointerException.class);
    }

    @Test
    public void zeroOtherCombinerReturnsNull() {
        just(1).withLatestFrom(new Observable[0], Functions.justFunction(null)).test().assertFailureAndMessage(NullPointerException.class, "The combiner returned a null value");
    }
}

