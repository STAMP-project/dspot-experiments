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


import io.reactivex.disposables.Disposables;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.TestException;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableMergeDelayErrorTest {
    Observer<String> stringObserver;

    @Test
    public void testErrorDelayed1() {
        final Observable<String> o1 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestErrorObservable("four", null, "six"));// we expect to lose "six" from the source (and it should never be sent by the source since onError was called

        final Observable<String> o2 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestErrorObservable("one", "two", "three"));
        Observable<String> m = Observable.mergeDelayError(o1, o2);
        m.subscribe(stringObserver);
        Mockito.verify(stringObserver, Mockito.times(1)).onError(ArgumentMatchers.any(NullPointerException.class));
        Mockito.verify(stringObserver, Mockito.never()).onComplete();
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("one");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("two");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("three");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("four");
        Mockito.verify(stringObserver, Mockito.times(0)).onNext("five");
        // despite not expecting it ... we don't do anything to prevent it if the source Observable keeps sending after onError
        // inner Observable errors are considered terminal for that source
        // verify(stringObserver, times(1)).onNext("six");
        // inner Observable errors are considered terminal for that source
    }

    @Test
    public void testErrorDelayed2() {
        final Observable<String> o1 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestErrorObservable("one", "two", "three"));
        final Observable<String> o2 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestErrorObservable("four", null, "six"));// we expect to lose "six" from the source (and it should never be sent by the source since onError was called

        final Observable<String> o3 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestErrorObservable("seven", "eight", null));
        final Observable<String> o4 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestErrorObservable("nine"));
        Observable<String> m = Observable.mergeDelayError(o1, o2, o3, o4);
        m.subscribe(stringObserver);
        Mockito.verify(stringObserver, Mockito.times(1)).onError(ArgumentMatchers.any(CompositeException.class));
        Mockito.verify(stringObserver, Mockito.never()).onComplete();
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("one");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("two");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("three");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("four");
        Mockito.verify(stringObserver, Mockito.times(0)).onNext("five");
        // despite not expecting it ... we don't do anything to prevent it if the source Observable keeps sending after onError
        // inner Observable errors are considered terminal for that source
        // verify(stringObserver, times(1)).onNext("six");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("seven");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("eight");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("nine");
    }

    @Test
    public void testErrorDelayed3() {
        final Observable<String> o1 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestErrorObservable("one", "two", "three"));
        final Observable<String> o2 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestErrorObservable("four", "five", "six"));
        final Observable<String> o3 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestErrorObservable("seven", "eight", null));
        final Observable<String> o4 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestErrorObservable("nine"));
        Observable<String> m = Observable.mergeDelayError(o1, o2, o3, o4);
        m.subscribe(stringObserver);
        Mockito.verify(stringObserver, Mockito.times(1)).onError(ArgumentMatchers.any(NullPointerException.class));
        Mockito.verify(stringObserver, Mockito.never()).onComplete();
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("one");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("two");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("three");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("four");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("five");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("six");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("seven");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("eight");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("nine");
    }

    @Test
    public void testErrorDelayed4() {
        final Observable<String> o1 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestErrorObservable("one", "two", "three"));
        final Observable<String> o2 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestErrorObservable("four", "five", "six"));
        final Observable<String> o3 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestErrorObservable("seven", "eight"));
        final Observable<String> o4 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestErrorObservable("nine", null));
        Observable<String> m = Observable.mergeDelayError(o1, o2, o3, o4);
        m.subscribe(stringObserver);
        Mockito.verify(stringObserver, Mockito.times(1)).onError(ArgumentMatchers.any(NullPointerException.class));
        Mockito.verify(stringObserver, Mockito.never()).onComplete();
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("one");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("two");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("three");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("four");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("five");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("six");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("seven");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("eight");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("nine");
    }

    @Test
    public void testErrorDelayed4WithThreading() {
        final ObservableMergeDelayErrorTest.TestAsyncErrorObservable o1 = new ObservableMergeDelayErrorTest.TestAsyncErrorObservable("one", "two", "three");
        final ObservableMergeDelayErrorTest.TestAsyncErrorObservable o2 = new ObservableMergeDelayErrorTest.TestAsyncErrorObservable("four", "five", "six");
        final ObservableMergeDelayErrorTest.TestAsyncErrorObservable o3 = new ObservableMergeDelayErrorTest.TestAsyncErrorObservable("seven", "eight");
        // throw the error at the very end so no onComplete will be called after it
        final ObservableMergeDelayErrorTest.TestAsyncErrorObservable o4 = new ObservableMergeDelayErrorTest.TestAsyncErrorObservable("nine", null);
        Observable<String> m = Observable.mergeDelayError(Observable.unsafeCreate(o1), Observable.unsafeCreate(o2), Observable.unsafeCreate(o3), Observable.unsafeCreate(o4));
        m.subscribe(stringObserver);
        try {
            o1.t.join();
            o2.t.join();
            o3.t.join();
            o4.t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("one");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("two");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("three");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("four");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("five");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("six");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("seven");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("eight");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("nine");
        Mockito.verify(stringObserver, Mockito.times(1)).onError(ArgumentMatchers.any(NullPointerException.class));
        Mockito.verify(stringObserver, Mockito.never()).onComplete();
    }

    @Test
    public void testCompositeErrorDelayed1() {
        final Observable<String> o1 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestErrorObservable("four", null, "six"));// we expect to lose "six" from the source (and it should never be sent by the source since onError was called

        final Observable<String> o2 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestErrorObservable("one", "two", null));
        Observable<String> m = Observable.mergeDelayError(o1, o2);
        m.subscribe(stringObserver);
        Mockito.verify(stringObserver, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringObserver, Mockito.never()).onComplete();
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("one");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("two");
        Mockito.verify(stringObserver, Mockito.times(0)).onNext("three");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("four");
        Mockito.verify(stringObserver, Mockito.times(0)).onNext("five");
        // despite not expecting it ... we don't do anything to prevent it if the source Observable keeps sending after onError
        // inner Observable errors are considered terminal for that source
        // verify(stringObserver, times(1)).onNext("six");
    }

    @Test
    public void testCompositeErrorDelayed2() {
        final Observable<String> o1 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestErrorObservable("four", null, "six"));// we expect to lose "six" from the source (and it should never be sent by the source since onError was called

        final Observable<String> o2 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestErrorObservable("one", "two", null));
        Observable<String> m = Observable.mergeDelayError(o1, o2);
        ObservableMergeDelayErrorTest.CaptureObserver w = new ObservableMergeDelayErrorTest.CaptureObserver();
        m.subscribe(w);
        Assert.assertNotNull(w.e);
        Assert.assertEquals(2, size());
        // if (w.e instanceof CompositeException) {
        // assertEquals(2, ((CompositeException) w.e).getExceptions().size());
        // w.e.printStackTrace();
        // } else {
        // fail("Expecting CompositeException");
        // }
    }

    /**
     * The unit tests below are from OperationMerge and should ensure the normal merge functionality is correct.
     */
    @Test
    public void testMergeObservableOfObservables() {
        final Observable<String> o1 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestSynchronousObservable());
        final Observable<String> o2 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestSynchronousObservable());
        Observable<Observable<String>> observableOfObservables = Observable.unsafeCreate(new ObservableSource<Observable<String>>() {
            @Override
            public void subscribe(Observer<? extends Observable<String>> observer) {
                observer.onSubscribe(Disposables.empty());
                // simulate what would happen in an Observable
                observer.onNext(o1);
                observer.onNext(o2);
                observer.onComplete();
            }
        });
        Observable<String> m = Observable.mergeDelayError(observableOfObservables);
        m.subscribe(stringObserver);
        Mockito.verify(stringObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringObserver, Mockito.times(1)).onComplete();
        Mockito.verify(stringObserver, Mockito.times(2)).onNext("hello");
    }

    @Test
    public void testMergeArray() {
        final Observable<String> o1 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestSynchronousObservable());
        final Observable<String> o2 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestSynchronousObservable());
        Observable<String> m = Observable.mergeDelayError(o1, o2);
        m.subscribe(stringObserver);
        Mockito.verify(stringObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringObserver, Mockito.times(2)).onNext("hello");
        Mockito.verify(stringObserver, Mockito.times(1)).onComplete();
    }

    @Test
    public void testMergeList() {
        final Observable<String> o1 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestSynchronousObservable());
        final Observable<String> o2 = Observable.unsafeCreate(new ObservableMergeDelayErrorTest.TestSynchronousObservable());
        List<Observable<String>> listOfObservables = new java.util.ArrayList<Observable<String>>();
        listOfObservables.add(o1);
        listOfObservables.add(o2);
        Observable<String> m = Observable.mergeDelayError(Observable.fromIterable(listOfObservables));
        m.subscribe(stringObserver);
        Mockito.verify(stringObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringObserver, Mockito.times(1)).onComplete();
        Mockito.verify(stringObserver, Mockito.times(2)).onNext("hello");
    }

    @Test
    public void testMergeArrayWithThreading() {
        final ObservableMergeDelayErrorTest.TestASynchronousObservable o1 = new ObservableMergeDelayErrorTest.TestASynchronousObservable();
        final ObservableMergeDelayErrorTest.TestASynchronousObservable o2 = new ObservableMergeDelayErrorTest.TestASynchronousObservable();
        Observable<String> m = Observable.mergeDelayError(unsafeCreate(o1), unsafeCreate(o2));
        m.subscribe(stringObserver);
        try {
            o1.t.join();
            o2.t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Mockito.verify(stringObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringObserver, Mockito.times(2)).onNext("hello");
        Mockito.verify(stringObserver, Mockito.times(1)).onComplete();
    }

    @Test(timeout = 1000L)
    public void testSynchronousError() {
        final Observable<Observable<String>> o1 = Observable.error(new RuntimeException("unit test"));
        final CountDownLatch latch = new CountDownLatch(1);
        Observable.mergeDelayError(o1).subscribe(new DefaultObserver<String>() {
            @Override
            public void onComplete() {
                Assert.fail("Expected onError path");
            }

            @Override
            public void onError(Throwable e) {
                latch.countDown();
            }

            @Override
            public void onNext(String s) {
                Assert.fail("Expected onError path");
            }
        });
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Assert.fail("interrupted");
        }
    }

    private static class TestSynchronousObservable implements ObservableSource<String> {
        @Override
        public void subscribe(Observer<? super String> observer) {
            observer.onSubscribe(Disposables.empty());
            observer.onNext("hello");
            observer.onComplete();
        }
    }

    private static class TestASynchronousObservable implements ObservableSource<String> {
        Thread t;

        @Override
        public void subscribe(final Observer<? super String> observer) {
            observer.onSubscribe(Disposables.empty());
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    observer.onNext("hello");
                    observer.onComplete();
                }
            });
            t.start();
        }
    }

    private static class TestErrorObservable implements ObservableSource<String> {
        String[] valuesToReturn;

        TestErrorObservable(String... values) {
            valuesToReturn = values;
        }

        @Override
        public void subscribe(Observer<? super String> observer) {
            observer.onSubscribe(Disposables.empty());
            boolean errorThrown = false;
            for (String s : valuesToReturn) {
                if (s == null) {
                    System.out.println("throwing exception");
                    observer.onError(new NullPointerException());
                    errorThrown = true;
                    // purposefully not returning here so it will continue calling onNext
                    // so that we also test that we handle bad sequences like this
                } else {
                    observer.onNext(s);
                }
            }
            if (!errorThrown) {
                observer.onComplete();
            }
        }
    }

    private static class TestAsyncErrorObservable implements ObservableSource<String> {
        String[] valuesToReturn;

        TestAsyncErrorObservable(String... values) {
            valuesToReturn = values;
        }

        Thread t;

        @Override
        public void subscribe(final Observer<? super String> observer) {
            observer.onSubscribe(Disposables.empty());
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (String s : valuesToReturn) {
                        if (s == null) {
                            System.out.println("throwing exception");
                            try {
                                Thread.sleep(100);
                            } catch (Throwable e) {
                            }
                            observer.onError(new NullPointerException());
                            return;
                        } else {
                            observer.onNext(s);
                        }
                    }
                    System.out.println("subscription complete");
                    observer.onComplete();
                }
            });
            t.start();
        }
    }

    private static class CaptureObserver extends DefaultObserver<String> {
        volatile Throwable e;

        @Override
        public void onComplete() {
        }

        @Override
        public void onError(Throwable e) {
            this.e = e;
        }

        @Override
        public void onNext(String args) {
        }
    }

    @Test
    public void testErrorInParentObservable() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.mergeDelayError(Observable.just(just(1), just(2)).startWith(Observable.<Integer>error(new RuntimeException()))).subscribe(to);
        to.awaitTerminalEvent();
        to.assertTerminated();
        to.assertValues(1, 2);
        Assert.assertEquals(1, to.errorCount());
    }

    @Test
    public void testErrorInParentObservableDelayed() throws Exception {
        for (int i = 0; i < 50; i++) {
            final ObservableMergeDelayErrorTest.TestASynchronous1sDelayedObservable o1 = new ObservableMergeDelayErrorTest.TestASynchronous1sDelayedObservable();
            final ObservableMergeDelayErrorTest.TestASynchronous1sDelayedObservable o2 = new ObservableMergeDelayErrorTest.TestASynchronous1sDelayedObservable();
            Observable<Observable<String>> parentObservable = Observable.unsafeCreate(new ObservableSource<Observable<String>>() {
                @Override
                public void subscribe(Observer<? extends Observable<String>> op) {
                    op.onSubscribe(Disposables.empty());
                    op.onNext(io.reactivex.Observable.unsafeCreate(o1));
                    op.onNext(io.reactivex.Observable.unsafeCreate(o2));
                    op.onError(new NullPointerException("throwing exception in parent"));
                }
            });
            Observer<String> stringObserver = mockObserver();
            TestObserver<String> to = new TestObserver<String>(stringObserver);
            Observable<String> m = Observable.mergeDelayError(parentObservable);
            m.subscribe(to);
            System.out.println(("testErrorInParentObservableDelayed | " + i));
            to.awaitTerminalEvent(2000, TimeUnit.MILLISECONDS);
            to.assertTerminated();
            Mockito.verify(stringObserver, Mockito.times(2)).onNext("hello");
            Mockito.verify(stringObserver, Mockito.times(1)).onError(ArgumentMatchers.any(NullPointerException.class));
            Mockito.verify(stringObserver, Mockito.never()).onComplete();
        }
    }

    private static class TestASynchronous1sDelayedObservable implements ObservableSource<String> {
        Thread t;

        @Override
        public void subscribe(final Observer<? super String> observer) {
            observer.onSubscribe(Disposables.empty());
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        observer.onError(e);
                    }
                    observer.onNext("hello");
                    observer.onComplete();
                }
            });
            t.start();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mergeIterableDelayError() {
        Observable.mergeDelayError(Arrays.asList(just(1), just(2))).test().assertResult(1, 2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mergeArrayDelayError() {
        Observable.mergeArrayDelayError(just(1), just(2)).test().assertResult(1, 2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mergeIterableDelayErrorWithError() {
        Observable.mergeDelayError(Arrays.asList(just(1).concatWith(<Integer>error(new TestException())), just(2))).test().assertFailure(TestException.class, 1, 2);
    }

    @Test
    public void mergeDelayError() {
        Observable.mergeDelayError(Observable.just(just(1), just(2))).test().assertResult(1, 2);
    }

    @Test
    public void mergeDelayErrorWithError() {
        Observable.mergeDelayError(Observable.just(just(1).concatWith(<Integer>error(new TestException())), just(2))).test().assertFailure(TestException.class, 1, 2);
    }

    @Test
    public void mergeDelayErrorMaxConcurrency() {
        Observable.mergeDelayError(Observable.just(just(1), just(2)), 1).test().assertResult(1, 2);
    }

    @Test
    public void mergeDelayErrorWithErrorMaxConcurrency() {
        Observable.mergeDelayError(Observable.just(just(1).concatWith(<Integer>error(new TestException())), just(2)), 1).test().assertFailure(TestException.class, 1, 2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mergeIterableDelayErrorMaxConcurrency() {
        Observable.mergeDelayError(Arrays.asList(just(1), just(2)), 1).test().assertResult(1, 2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mergeIterableDelayErrorWithErrorMaxConcurrency() {
        Observable.mergeDelayError(Arrays.asList(just(1).concatWith(<Integer>error(new TestException())), just(2)), 1).test().assertFailure(TestException.class, 1, 2);
    }

    @Test
    public void mergeDelayError3() {
        Observable.mergeDelayError(just(1), just(2), just(3)).test().assertResult(1, 2, 3);
    }

    @Test
    public void mergeDelayError3WithError() {
        Observable.mergeDelayError(just(1), just(2).concatWith(<Integer>error(new TestException())), just(3)).test().assertFailure(TestException.class, 1, 2, 3);
    }
}

