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
package io.reactivex;


import io.reactivex.exceptions.TestException;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.reactivestreams.Publisher;


public class XFlatMapTest {
    @Rule
    public Retry retry = new Retry(5, 1000, true);

    static final int SLEEP_AFTER_CANCEL = 500;

    final CyclicBarrier cb = new CyclicBarrier(2);

    @Test
    public void flowableFlowable() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestSubscriber<Integer> ts = Flowable.just(1).subscribeOn(Schedulers.io()).flatMap(new io.reactivex.functions.Function<Integer, Publisher<Integer>>() {
                @Override
                public Publisher<Integer> apply(Integer v) throws Exception {
                    sleep();
                    return Flowable.<Integer>error(new TestException());
                }
            }).test();
            cb.await();
            beforeCancelSleep(ts);
            ts.cancel();
            Thread.sleep(XFlatMapTest.SLEEP_AFTER_CANCEL);
            ts.assertEmpty();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void flowableSingle() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestSubscriber<Integer> ts = Flowable.just(1).subscribeOn(Schedulers.io()).flatMapSingle(new io.reactivex.functions.Function<Integer, Single<Integer>>() {
                @Override
                public Single<Integer> apply(Integer v) throws Exception {
                    sleep();
                    return Single.<Integer>error(new TestException());
                }
            }).test();
            cb.await();
            beforeCancelSleep(ts);
            ts.cancel();
            Thread.sleep(XFlatMapTest.SLEEP_AFTER_CANCEL);
            ts.assertEmpty();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void flowableMaybe() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestSubscriber<Integer> ts = Flowable.just(1).subscribeOn(Schedulers.io()).flatMapMaybe(new io.reactivex.functions.Function<Integer, Maybe<Integer>>() {
                @Override
                public Maybe<Integer> apply(Integer v) throws Exception {
                    sleep();
                    return Maybe.<Integer>error(new TestException());
                }
            }).test();
            cb.await();
            beforeCancelSleep(ts);
            ts.cancel();
            Thread.sleep(XFlatMapTest.SLEEP_AFTER_CANCEL);
            ts.assertEmpty();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void flowableCompletable() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Void> to = Flowable.just(1).subscribeOn(Schedulers.io()).flatMapCompletable(new io.reactivex.functions.Function<Integer, Completable>() {
                @Override
                public Completable apply(Integer v) throws Exception {
                    sleep();
                    return Completable.error(new TestException());
                }
            }).test();
            cb.await();
            beforeCancelSleep(to);
            to.cancel();
            Thread.sleep(XFlatMapTest.SLEEP_AFTER_CANCEL);
            to.assertEmpty();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void flowableCompletable2() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestSubscriber<Void> ts = Flowable.just(1).subscribeOn(Schedulers.io()).flatMapCompletable(new io.reactivex.functions.Function<Integer, Completable>() {
                @Override
                public Completable apply(Integer v) throws Exception {
                    sleep();
                    return Completable.error(new TestException());
                }
            }).<Void>toFlowable().test();
            cb.await();
            beforeCancelSleep(ts);
            ts.cancel();
            Thread.sleep(XFlatMapTest.SLEEP_AFTER_CANCEL);
            ts.assertEmpty();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void observableFlowable() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Integer> to = Observable.just(1).subscribeOn(Schedulers.io()).flatMap(new io.reactivex.functions.Function<Integer, Observable<Integer>>() {
                @Override
                public io.reactivex.observers.Observable<Integer> apply(Integer v) throws Exception {
                    sleep();
                    return Observable.<Integer>error(new TestException());
                }
            }).test();
            cb.await();
            beforeCancelSleep(to);
            to.cancel();
            Thread.sleep(XFlatMapTest.SLEEP_AFTER_CANCEL);
            to.assertEmpty();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void observerSingle() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Integer> to = Observable.just(1).subscribeOn(Schedulers.io()).flatMapSingle(new io.reactivex.functions.Function<Integer, Single<Integer>>() {
                @Override
                public Single<Integer> apply(Integer v) throws Exception {
                    sleep();
                    return Single.<Integer>error(new TestException());
                }
            }).test();
            cb.await();
            beforeCancelSleep(to);
            to.cancel();
            Thread.sleep(XFlatMapTest.SLEEP_AFTER_CANCEL);
            to.assertEmpty();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void observerMaybe() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Integer> to = Observable.just(1).subscribeOn(Schedulers.io()).flatMapMaybe(new io.reactivex.functions.Function<Integer, Maybe<Integer>>() {
                @Override
                public Maybe<Integer> apply(Integer v) throws Exception {
                    sleep();
                    return Maybe.<Integer>error(new TestException());
                }
            }).test();
            cb.await();
            beforeCancelSleep(to);
            to.cancel();
            Thread.sleep(XFlatMapTest.SLEEP_AFTER_CANCEL);
            to.assertEmpty();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void observerCompletable() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Void> to = Observable.just(1).subscribeOn(Schedulers.io()).flatMapCompletable(new io.reactivex.functions.Function<Integer, Completable>() {
                @Override
                public Completable apply(Integer v) throws Exception {
                    sleep();
                    return Completable.error(new TestException());
                }
            }).test();
            cb.await();
            beforeCancelSleep(to);
            to.cancel();
            Thread.sleep(XFlatMapTest.SLEEP_AFTER_CANCEL);
            to.assertEmpty();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void observerCompletable2() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Void> to = Observable.just(1).subscribeOn(Schedulers.io()).flatMapCompletable(new io.reactivex.functions.Function<Integer, Completable>() {
                @Override
                public Completable apply(Integer v) throws Exception {
                    sleep();
                    return Completable.error(new TestException());
                }
            }).<Void>toObservable().test();
            cb.await();
            beforeCancelSleep(to);
            to.cancel();
            Thread.sleep(XFlatMapTest.SLEEP_AFTER_CANCEL);
            to.assertEmpty();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void singleSingle() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Integer> to = Single.just(1).subscribeOn(Schedulers.io()).flatMap(new io.reactivex.functions.Function<Integer, Single<Integer>>() {
                @Override
                public Single<Integer> apply(Integer v) throws Exception {
                    sleep();
                    return Single.<Integer>error(new TestException());
                }
            }).test();
            cb.await();
            beforeCancelSleep(to);
            to.cancel();
            Thread.sleep(XFlatMapTest.SLEEP_AFTER_CANCEL);
            to.assertEmpty();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void singleMaybe() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Integer> to = Single.just(1).subscribeOn(Schedulers.io()).flatMapMaybe(new io.reactivex.functions.Function<Integer, Maybe<Integer>>() {
                @Override
                public Maybe<Integer> apply(Integer v) throws Exception {
                    sleep();
                    return Maybe.<Integer>error(new TestException());
                }
            }).test();
            cb.await();
            beforeCancelSleep(to);
            to.cancel();
            Thread.sleep(XFlatMapTest.SLEEP_AFTER_CANCEL);
            to.assertEmpty();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void singleCompletable() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Void> to = Single.just(1).subscribeOn(Schedulers.io()).flatMapCompletable(new io.reactivex.functions.Function<Integer, Completable>() {
                @Override
                public Completable apply(Integer v) throws Exception {
                    sleep();
                    return Completable.error(new TestException());
                }
            }).test();
            cb.await();
            beforeCancelSleep(to);
            to.cancel();
            Thread.sleep(XFlatMapTest.SLEEP_AFTER_CANCEL);
            to.assertEmpty();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void singleCompletable2() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Integer> to = Single.just(1).subscribeOn(Schedulers.io()).flatMapCompletable(new io.reactivex.functions.Function<Integer, Completable>() {
                @Override
                public Completable apply(Integer v) throws Exception {
                    sleep();
                    return Completable.error(new TestException());
                }
            }).toSingleDefault(0).test();
            cb.await();
            beforeCancelSleep(to);
            to.cancel();
            Thread.sleep(XFlatMapTest.SLEEP_AFTER_CANCEL);
            to.assertEmpty();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void maybeSingle() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Integer> to = Maybe.just(1).subscribeOn(Schedulers.io()).flatMapSingle(new io.reactivex.functions.Function<Integer, Single<Integer>>() {
                @Override
                public Single<Integer> apply(Integer v) throws Exception {
                    sleep();
                    return Single.<Integer>error(new TestException());
                }
            }).test();
            cb.await();
            beforeCancelSleep(to);
            to.cancel();
            Thread.sleep(XFlatMapTest.SLEEP_AFTER_CANCEL);
            to.assertEmpty();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void maybeMaybe() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Integer> to = Maybe.just(1).subscribeOn(Schedulers.io()).flatMap(new io.reactivex.functions.Function<Integer, Maybe<Integer>>() {
                @Override
                public Maybe<Integer> apply(Integer v) throws Exception {
                    sleep();
                    return Maybe.<Integer>error(new TestException());
                }
            }).test();
            cb.await();
            beforeCancelSleep(to);
            to.cancel();
            Thread.sleep(XFlatMapTest.SLEEP_AFTER_CANCEL);
            to.assertEmpty();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void maybeCompletable() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Void> to = Maybe.just(1).subscribeOn(Schedulers.io()).flatMapCompletable(new io.reactivex.functions.Function<Integer, Completable>() {
                @Override
                public Completable apply(Integer v) throws Exception {
                    sleep();
                    return Completable.error(new TestException());
                }
            }).test();
            cb.await();
            beforeCancelSleep(to);
            to.cancel();
            Thread.sleep(XFlatMapTest.SLEEP_AFTER_CANCEL);
            to.assertEmpty();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void maybeCompletable2() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Void> to = Maybe.just(1).subscribeOn(Schedulers.io()).flatMapCompletable(new io.reactivex.functions.Function<Integer, Completable>() {
                @Override
                public Completable apply(Integer v) throws Exception {
                    sleep();
                    return Completable.error(new TestException());
                }
            }).<Void>toMaybe().test();
            cb.await();
            beforeCancelSleep(to);
            to.cancel();
            Thread.sleep(XFlatMapTest.SLEEP_AFTER_CANCEL);
            to.assertEmpty();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

