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
import io.reactivex.disposables.CompositeException;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Test;


public class ObservableUsingTest {
    interface Resource {
        String getTextFromWeb();

        void dispose();
    }

    private static class DisposeAction implements Consumer<ObservableUsingTest.Resource> {
        @Override
        public void accept(ObservableUsingTest.Resource r) {
            r.dispose();
        }
    }

    private final io.reactivex.Consumer<Disposable> disposeSubscription = new Consumer<Disposable>() {
        @Override
        public void accept(Disposable d) {
            d.dispose();
        }
    };

    @Test
    public void testUsing() {
        performTestUsing(false);
    }

    @Test
    public void testUsingEagerly() {
        performTestUsing(true);
    }

    @Test
    public void testUsingWithSubscribingTwice() {
        performTestUsingWithSubscribingTwice(false);
    }

    @Test
    public void testUsingWithSubscribingTwiceDisposeEagerly() {
        performTestUsingWithSubscribingTwice(true);
    }

    @Test(expected = TestException.class)
    public void testUsingWithResourceFactoryError() {
        performTestUsingWithResourceFactoryError(false);
    }

    @Test(expected = TestException.class)
    public void testUsingWithResourceFactoryErrorDisposeEagerly() {
        performTestUsingWithResourceFactoryError(true);
    }

    @Test
    public void testUsingWithObservableFactoryError() {
        performTestUsingWithObservableFactoryError(false);
    }

    @Test
    public void testUsingWithObservableFactoryErrorDisposeEagerly() {
        performTestUsingWithObservableFactoryError(true);
    }

    @Test
    public void testUsingDisposesEagerlyBeforeCompletion() {
        final List<String> events = new ArrayList<String>();
        Callable<ObservableUsingTest.Resource> resourceFactory = ObservableUsingTest.createResourceFactory(events);
        final Action completion = ObservableUsingTest.createOnCompletedAction(events);
        final Action unsub = ObservableUsingTest.createUnsubAction(events);
        Function<ObservableUsingTest.Resource, Observable<String>> observableFactory = new Function<ObservableUsingTest.Resource, Observable<String>>() {
            @Override
            public Observable<String> apply(io.reactivex.internal.operators.observable.Resource resource) {
                return io.reactivex.Observable.fromArray(resource.getTextFromWeb().split(" "));
            }
        };
        Observer<String> observer = mockObserver();
        Observable<String> o = Observable.using(resourceFactory, observableFactory, new ObservableUsingTest.DisposeAction(), true).doOnDispose(unsub).doOnComplete(completion);
        o.safeSubscribe(observer);
        Assert.assertEquals(/* , "unsub" */
        Arrays.asList("disposed", "completed"), events);
    }

    @Test
    public void testUsingDoesNotDisposesEagerlyBeforeCompletion() {
        final List<String> events = new ArrayList<String>();
        Callable<ObservableUsingTest.Resource> resourceFactory = ObservableUsingTest.createResourceFactory(events);
        final Action completion = ObservableUsingTest.createOnCompletedAction(events);
        final Action unsub = ObservableUsingTest.createUnsubAction(events);
        Function<ObservableUsingTest.Resource, Observable<String>> observableFactory = new Function<ObservableUsingTest.Resource, Observable<String>>() {
            @Override
            public Observable<String> apply(io.reactivex.internal.operators.observable.Resource resource) {
                return io.reactivex.Observable.fromArray(resource.getTextFromWeb().split(" "));
            }
        };
        Observer<String> observer = mockObserver();
        Observable<String> o = Observable.using(resourceFactory, observableFactory, new ObservableUsingTest.DisposeAction(), false).doOnDispose(unsub).doOnComplete(completion);
        o.safeSubscribe(observer);
        Assert.assertEquals(/* "unsub", */
        Arrays.asList("completed", "disposed"), events);
    }

    @Test
    public void testUsingDisposesEagerlyBeforeError() {
        final List<String> events = new ArrayList<String>();
        Callable<ObservableUsingTest.Resource> resourceFactory = ObservableUsingTest.createResourceFactory(events);
        final Consumer<Throwable> onError = ObservableUsingTest.createOnErrorAction(events);
        final Action unsub = ObservableUsingTest.createUnsubAction(events);
        Function<ObservableUsingTest.Resource, Observable<String>> observableFactory = new Function<ObservableUsingTest.Resource, Observable<String>>() {
            @Override
            public Observable<String> apply(io.reactivex.internal.operators.observable.Resource resource) {
                return io.reactivex.Observable.fromArray(resource.getTextFromWeb().split(" ")).concatWith(io.reactivex.Observable.<String>error(new RuntimeException()));
            }
        };
        Observer<String> observer = mockObserver();
        Observable<String> o = Observable.using(resourceFactory, observableFactory, new ObservableUsingTest.DisposeAction(), true).doOnDispose(unsub).doOnError(onError);
        o.safeSubscribe(observer);
        Assert.assertEquals(/* , "unsub" */
        Arrays.asList("disposed", "error"), events);
    }

    @Test
    public void testUsingDoesNotDisposesEagerlyBeforeError() {
        final List<String> events = new ArrayList<String>();
        final Callable<ObservableUsingTest.Resource> resourceFactory = ObservableUsingTest.createResourceFactory(events);
        final Consumer<Throwable> onError = ObservableUsingTest.createOnErrorAction(events);
        final Action unsub = ObservableUsingTest.createUnsubAction(events);
        Function<ObservableUsingTest.Resource, Observable<String>> observableFactory = new Function<ObservableUsingTest.Resource, Observable<String>>() {
            @Override
            public Observable<String> apply(io.reactivex.internal.operators.observable.Resource resource) {
                return io.reactivex.Observable.fromArray(resource.getTextFromWeb().split(" ")).concatWith(io.reactivex.Observable.<String>error(new RuntimeException()));
            }
        };
        Observer<String> observer = mockObserver();
        Observable<String> o = Observable.using(resourceFactory, observableFactory, new ObservableUsingTest.DisposeAction(), false).doOnDispose(unsub).doOnError(onError);
        o.safeSubscribe(observer);
        Assert.assertEquals(/* "unsub", */
        Arrays.asList("error", "disposed"), events);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Observable.using(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return 1;
            }
        }, new Function<Object, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Object v) throws Exception {
                return never();
            }
        }, Functions.emptyConsumer()));
    }

    @Test
    public void supplierDisposerCrash() {
        TestObserver<Object> to = Observable.using(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return 1;
            }
        }, new Function<Object, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Object v) throws Exception {
                throw new TestException("First");
            }
        }, new Consumer<Object>() {
            @Override
            public void accept(Object e) throws Exception {
                throw new TestException("Second");
            }
        }).test().assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.compositeList(to.errors().get(0));
        TestHelper.assertError(errors, 0, TestException.class, "First");
        TestHelper.assertError(errors, 1, TestException.class, "Second");
    }

    @Test
    public void eagerOnErrorDisposerCrash() {
        TestObserver<Object> to = Observable.using(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return 1;
            }
        }, new Function<Object, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Object v) throws Exception {
                return error(new TestException("First"));
            }
        }, new Consumer<Object>() {
            @Override
            public void accept(Object e) throws Exception {
                throw new TestException("Second");
            }
        }).test().assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.compositeList(to.errors().get(0));
        TestHelper.assertError(errors, 0, TestException.class, "First");
        TestHelper.assertError(errors, 1, TestException.class, "Second");
    }

    @Test
    public void eagerOnCompleteDisposerCrash() {
        Observable.using(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return 1;
            }
        }, new Function<Object, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Object v) throws Exception {
                return empty();
            }
        }, new Consumer<Object>() {
            @Override
            public void accept(Object e) throws Exception {
                throw new TestException("Second");
            }
        }).test().assertFailureAndMessage(TestException.class, "Second");
    }

    @Test
    public void nonEagerDisposerCrash() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            using(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return 1;
                }
            }, new Function<Object, ObservableSource<Object>>() {
                @Override
                public io.reactivex.ObservableSource<Object> apply(Object v) throws Exception {
                    return empty();
                }
            }, new Consumer<Object>() {
                @Override
                public void accept(Object e) throws Exception {
                    throw new TestException("Second");
                }
            }, false).test().assertResult();
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void sourceSupplierReturnsNull() {
        Observable.using(Functions.justCallable(1), Functions.justFunction(((Observable<Object>) (null))), Functions.emptyConsumer()).test().assertFailureAndMessage(NullPointerException.class, "The sourceSupplier returned a null ObservableSource");
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeObservable(new Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public ObservableSource<Object> apply(Observable<Object> o) throws Exception {
                return io.reactivex.Observable.using(Functions.justCallable(1), Functions.justFunction(o), Functions.emptyConsumer());
            }
        });
    }

    @Test
    public void eagerDisposedOnComplete() {
        final TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.using(Functions.justCallable(1), Functions.justFunction(new Observable<Integer>() {
            @Override
            protected void subscribeActual(Observer<? extends Integer> observer) {
                observer.onSubscribe(Disposables.empty());
                to.cancel();
                observer.onComplete();
            }
        }), Functions.emptyConsumer(), true).subscribe(to);
    }

    @Test
    public void eagerDisposedOnError() {
        final TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.using(Functions.justCallable(1), Functions.justFunction(new Observable<Integer>() {
            @Override
            protected void subscribeActual(Observer<? extends Integer> observer) {
                observer.onSubscribe(Disposables.empty());
                to.cancel();
                observer.onError(new TestException());
            }
        }), Functions.emptyConsumer(), true).subscribe(to);
    }
}

