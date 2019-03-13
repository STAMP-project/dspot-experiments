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


import io.reactivex.TestHelper;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Observer;
import io.reactivex.exceptions.TestException;
import io.reactivex.plugins.RxJavaPlugins;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableDoOnEachTest {
    Observer<String> subscribedObserver;

    Observer<String> sideEffectObserver;

    @Test
    public void testDoOnEach() {
        Observable<String> base = Observable.just("a", "b", "c");
        Observable<String> doOnEach = base.doOnEach(sideEffectObserver);
        doOnEach.subscribe(subscribedObserver);
        // ensure the leaf Observer is still getting called
        Mockito.verify(subscribedObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscribedObserver, Mockito.times(1)).onNext("a");
        Mockito.verify(subscribedObserver, Mockito.times(1)).onNext("b");
        Mockito.verify(subscribedObserver, Mockito.times(1)).onNext("c");
        Mockito.verify(subscribedObserver, Mockito.times(1)).onComplete();
        // ensure our injected Observer is getting called
        Mockito.verify(sideEffectObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(sideEffectObserver, Mockito.times(1)).onNext("a");
        Mockito.verify(sideEffectObserver, Mockito.times(1)).onNext("b");
        Mockito.verify(sideEffectObserver, Mockito.times(1)).onNext("c");
        Mockito.verify(sideEffectObserver, Mockito.times(1)).onComplete();
    }

    @Test
    public void testDoOnEachWithError() {
        Observable<String> base = Observable.just("one", "fail", "two", "three", "fail");
        Observable<String> errs = base.map(new Function<String, String>() {
            @Override
            public String apply(String s) {
                if ("fail".equals(s)) {
                    throw new RuntimeException("Forced Failure");
                }
                return s;
            }
        });
        Observable<String> doOnEach = errs.doOnEach(sideEffectObserver);
        doOnEach.subscribe(subscribedObserver);
        Mockito.verify(subscribedObserver, Mockito.times(1)).onNext("one");
        Mockito.verify(subscribedObserver, Mockito.never()).onNext("two");
        Mockito.verify(subscribedObserver, Mockito.never()).onNext("three");
        Mockito.verify(subscribedObserver, Mockito.never()).onComplete();
        Mockito.verify(subscribedObserver, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(sideEffectObserver, Mockito.times(1)).onNext("one");
        Mockito.verify(sideEffectObserver, Mockito.never()).onNext("two");
        Mockito.verify(sideEffectObserver, Mockito.never()).onNext("three");
        Mockito.verify(sideEffectObserver, Mockito.never()).onComplete();
        Mockito.verify(sideEffectObserver, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDoOnEachWithErrorInCallback() {
        Observable<String> base = Observable.just("one", "two", "fail", "three");
        Observable<String> doOnEach = base.doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) {
                if ("fail".equals(s)) {
                    throw new RuntimeException("Forced Failure");
                }
            }
        });
        doOnEach.subscribe(subscribedObserver);
        Mockito.verify(subscribedObserver, Mockito.times(1)).onNext("one");
        Mockito.verify(subscribedObserver, Mockito.times(1)).onNext("two");
        Mockito.verify(subscribedObserver, Mockito.never()).onNext("three");
        Mockito.verify(subscribedObserver, Mockito.never()).onComplete();
        Mockito.verify(subscribedObserver, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testIssue1451Case1() {
        // https://github.com/Netflix/RxJava/issues/1451
        final int expectedCount = 3;
        final AtomicInteger count = new AtomicInteger();
        for (int i = 0; i < expectedCount; i++) {
            Observable.just(Boolean.TRUE, Boolean.FALSE).takeWhile(new Predicate<Boolean>() {
                @Override
                public boolean test(Boolean value) {
                    return value;
                }
            }).toList().doOnSuccess(new Consumer<java.util.List<Boolean>>() {
                @Override
                public void accept(java.util.List<Boolean> booleans) {
                    count.incrementAndGet();
                }
            }).subscribe();
        }
        Assert.assertEquals(expectedCount, count.get());
    }

    @Test
    public void testIssue1451Case2() {
        // https://github.com/Netflix/RxJava/issues/1451
        final int expectedCount = 3;
        final AtomicInteger count = new AtomicInteger();
        for (int i = 0; i < expectedCount; i++) {
            Observable.just(Boolean.TRUE, Boolean.FALSE, Boolean.FALSE).takeWhile(new Predicate<Boolean>() {
                @Override
                public boolean test(Boolean value) {
                    return value;
                }
            }).toList().doOnSuccess(new Consumer<java.util.List<Boolean>>() {
                @Override
                public void accept(java.util.List<Boolean> booleans) {
                    count.incrementAndGet();
                }
            }).subscribe();
        }
        Assert.assertEquals(expectedCount, count.get());
    }

    // FIXME crashing ObservableSource can't propagate to an Observer
    // @Test
    // public void testFatalError() {
    // try {
    // Observable.just(1, 2, 3)
    // .flatMap(new Function<Integer, Observable<?>>() {
    // @Override
    // public Observable<?> apply(Integer integer) {
    // return Observable.create(new ObservableSource<Object>() {
    // @Override
    // public void accept(Observer<Object> o) {
    // throw new NullPointerException("Test NPE");
    // }
    // });
    // }
    // })
    // .doOnNext(new Consumer<Object>() {
    // @Override
    // public void accept(Object o) {
    // System.out.println("Won't come here");
    // }
    // })
    // .subscribe();
    // fail("should have thrown an exception");
    // } catch (OnErrorNotImplementedException e) {
    // assertTrue(e.getCause() instanceof NullPointerException);
    // assertEquals(e.getCause().getMessage(), "Test NPE");
    // System.out.println("Received exception: " + e);
    // }
    // }
    @Test
    public void onErrorThrows() {
        TestObserver<Object> to = TestObserver.create();
        Observable.error(new TestException()).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) {
                throw new TestException();
            }
        }).subscribe(to);
        to.assertNoValues();
        to.assertNotComplete();
        to.assertError(CompositeException.class);
        CompositeException ex = ((CompositeException) (to.errors().get(0)));
        java.util.List<Throwable> exceptions = ex.getExceptions();
        Assert.assertEquals(2, exceptions.size());
        Assert.assertTrue(((exceptions.get(0)) instanceof TestException));
        Assert.assertTrue(((exceptions.get(1)) instanceof TestException));
    }

    @Test
    public void ignoreCancel() {
        java.util.List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Observable.wrap(new ObservableSource<Object>() {
                @Override
                public void subscribe(Observer<? super Object> observer) {
                    observer.onSubscribe(io.reactivex.disposables.Disposables.empty());
                    observer.onNext(1);
                    observer.onNext(2);
                    observer.onError(new IOException());
                    observer.onComplete();
                }
            }).doOnNext(new Consumer<Object>() {
                @Override
                public void accept(Object e) throws Exception {
                    throw new TestException();
                }
            }).test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, IOException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onErrorAfterCrash() {
        java.util.List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Observable.wrap(new ObservableSource<Object>() {
                @Override
                public void subscribe(Observer<? super Object> observer) {
                    observer.onSubscribe(io.reactivex.disposables.Disposables.empty());
                    observer.onError(new TestException());
                }
            }).doAfterTerminate(new Action() {
                @Override
                public void run() throws Exception {
                    throw new IOException();
                }
            }).test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, IOException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onCompleteAfterCrash() {
        java.util.List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Observable.wrap(new ObservableSource<Object>() {
                @Override
                public void subscribe(Observer<? super Object> observer) {
                    observer.onSubscribe(io.reactivex.disposables.Disposables.empty());
                    observer.onComplete();
                }
            }).doAfterTerminate(new Action() {
                @Override
                public void run() throws Exception {
                    throw new IOException();
                }
            }).test().assertResult();
            TestHelper.assertUndeliverable(errors, 0, IOException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onCompleteCrash() {
        Observable.wrap(new ObservableSource<Object>() {
            @Override
            public void subscribe(Observer<? super Object> observer) {
                observer.onSubscribe(io.reactivex.disposables.Disposables.empty());
                observer.onComplete();
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                throw new IOException();
            }
        }).test().assertFailure(IOException.class);
    }

    @Test
    public void ignoreCancelConditional() {
        java.util.List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Observable.wrap(new ObservableSource<Object>() {
                @Override
                public void subscribe(Observer<? super Object> observer) {
                    observer.onSubscribe(io.reactivex.disposables.Disposables.empty());
                    observer.onNext(1);
                    observer.onNext(2);
                    observer.onError(new IOException());
                    observer.onComplete();
                }
            }).doOnNext(new Consumer<Object>() {
                @Override
                public void accept(Object e) throws Exception {
                    throw new TestException();
                }
            }).filter(io.reactivex.internal.functions.Functions.alwaysTrue()).test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, IOException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onErrorAfterCrashConditional() {
        java.util.List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Observable.wrap(new ObservableSource<Object>() {
                @Override
                public void subscribe(Observer<? super Object> observer) {
                    observer.onSubscribe(io.reactivex.disposables.Disposables.empty());
                    observer.onError(new TestException());
                }
            }).doAfterTerminate(new Action() {
                @Override
                public void run() throws Exception {
                    throw new IOException();
                }
            }).filter(io.reactivex.internal.functions.Functions.alwaysTrue()).test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, IOException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onCompleteAfter() {
        final int[] call = new int[]{ 0 };
        Observable.just(1).doAfterTerminate(new Action() {
            @Override
            public void run() throws Exception {
                (call[0])++;
            }
        }).test().assertResult(1);
        Assert.assertEquals(1, call[0]);
    }

    @Test
    public void onCompleteAfterCrashConditional() {
        java.util.List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Observable.wrap(new ObservableSource<Object>() {
                @Override
                public void subscribe(Observer<? super Object> observer) {
                    observer.onSubscribe(io.reactivex.disposables.Disposables.empty());
                    observer.onComplete();
                }
            }).doAfterTerminate(new Action() {
                @Override
                public void run() throws Exception {
                    throw new IOException();
                }
            }).filter(io.reactivex.internal.functions.Functions.alwaysTrue()).test().assertResult();
            TestHelper.assertUndeliverable(errors, 0, IOException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onCompleteCrashConditional() {
        Observable.wrap(new ObservableSource<Object>() {
            @Override
            public void subscribe(Observer<? super Object> observer) {
                observer.onSubscribe(io.reactivex.disposables.Disposables.empty());
                observer.onComplete();
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                throw new IOException();
            }
        }).filter(io.reactivex.internal.functions.Functions.alwaysTrue()).test().assertFailure(IOException.class);
    }

    @Test
    public void onErrorOnErrorCrashConditional() {
        TestObserver<Object> to = Observable.error(new TestException("Outer")).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                throw new TestException("Inner");
            }
        }).filter(io.reactivex.internal.functions.Functions.alwaysTrue()).test().assertFailure(CompositeException.class);
        java.util.List<Throwable> errors = TestHelper.compositeList(to.errors().get(0));
        TestHelper.assertError(errors, 0, TestException.class, "Outer");
        TestHelper.assertError(errors, 1, TestException.class, "Inner");
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Observable.just(1).doOnEach(new TestObserver<Integer>()));
    }

    @Test
    public void doubleOnSubscribe() {
        checkDoubleOnSubscribeObservable(new Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Observable<Object> o) throws Exception {
                return o.doOnEach(new TestObserver<Object>());
            }
        });
    }
}

