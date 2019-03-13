/**
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.room;


import InvalidationTracker.Observer;
import androidx.arch.core.executor.testing.CountingTaskExecutorRule;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class RxRoomTest {
    @Rule
    public CountingTaskExecutorRule mExecutor = new CountingTaskExecutorRule();

    private RoomDatabase mDatabase;

    private InvalidationTracker mInvalidationTracker;

    private List<InvalidationTracker.Observer> mAddedObservers = new ArrayList<>();

    @Test
    public void basicAddRemove_Flowable() {
        Flowable<Object> flowable = RxRoom.createFlowable(mDatabase, "a", "b");
        Mockito.verify(mInvalidationTracker, Mockito.never()).addObserver(ArgumentMatchers.any(Observer.class));
        Disposable disposable = flowable.subscribe();
        Mockito.verify(mInvalidationTracker).addObserver(ArgumentMatchers.any(Observer.class));
        MatcherAssert.assertThat(mAddedObservers.size(), CoreMatchers.is(1));
        InvalidationTracker.Observer observer = mAddedObservers.get(0);
        disposable.dispose();
        Mockito.verify(mInvalidationTracker).removeObserver(observer);
        disposable = flowable.subscribe();
        Mockito.verify(mInvalidationTracker, Mockito.times(2)).addObserver(ArgumentMatchers.any(Observer.class));
        MatcherAssert.assertThat(mAddedObservers.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(mAddedObservers.get(1), CoreMatchers.not(CoreMatchers.sameInstance(observer)));
        InvalidationTracker.Observer observer2 = mAddedObservers.get(1);
        disposable.dispose();
        Mockito.verify(mInvalidationTracker).removeObserver(observer2);
    }

    @Test
    public void basicAddRemove_Observable() {
        Observable<Object> observable = RxRoom.createObservable(mDatabase, "a", "b");
        Mockito.verify(mInvalidationTracker, Mockito.never()).addObserver(ArgumentMatchers.any(Observer.class));
        Disposable disposable = observable.subscribe();
        Mockito.verify(mInvalidationTracker).addObserver(ArgumentMatchers.any(Observer.class));
        MatcherAssert.assertThat(mAddedObservers.size(), CoreMatchers.is(1));
        InvalidationTracker.Observer observer = mAddedObservers.get(0);
        disposable.dispose();
        Mockito.verify(mInvalidationTracker).removeObserver(observer);
        disposable = observable.subscribe();
        Mockito.verify(mInvalidationTracker, Mockito.times(2)).addObserver(ArgumentMatchers.any(Observer.class));
        MatcherAssert.assertThat(mAddedObservers.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(mAddedObservers.get(1), CoreMatchers.not(CoreMatchers.sameInstance(observer)));
        InvalidationTracker.Observer observer2 = mAddedObservers.get(1);
        disposable.dispose();
        Mockito.verify(mInvalidationTracker).removeObserver(observer2);
    }

    @Test
    public void basicNotify_Flowable() {
        String[] tables = new String[]{ "a", "b" };
        Set<String> tableSet = new HashSet<>(Arrays.asList(tables));
        Flowable<Object> flowable = RxRoom.createFlowable(mDatabase, tables);
        RxRoomTest.CountingConsumer consumer = new RxRoomTest.CountingConsumer();
        Disposable disposable = flowable.subscribe(consumer);
        MatcherAssert.assertThat(mAddedObservers.size(), CoreMatchers.is(1));
        InvalidationTracker.Observer observer = mAddedObservers.get(0);
        MatcherAssert.assertThat(consumer.mCount, CoreMatchers.is(1));
        observer.onInvalidated(tableSet);
        MatcherAssert.assertThat(consumer.mCount, CoreMatchers.is(2));
        observer.onInvalidated(tableSet);
        MatcherAssert.assertThat(consumer.mCount, CoreMatchers.is(3));
        disposable.dispose();
        observer.onInvalidated(tableSet);
        MatcherAssert.assertThat(consumer.mCount, CoreMatchers.is(3));
    }

    @Test
    public void basicNotify_Observable() {
        String[] tables = new String[]{ "a", "b" };
        Set<String> tableSet = new HashSet<>(Arrays.asList(tables));
        Observable<Object> observable = RxRoom.createObservable(mDatabase, tables);
        RxRoomTest.CountingConsumer consumer = new RxRoomTest.CountingConsumer();
        Disposable disposable = observable.subscribe(consumer);
        MatcherAssert.assertThat(mAddedObservers.size(), CoreMatchers.is(1));
        InvalidationTracker.Observer observer = mAddedObservers.get(0);
        MatcherAssert.assertThat(consumer.mCount, CoreMatchers.is(1));
        observer.onInvalidated(tableSet);
        MatcherAssert.assertThat(consumer.mCount, CoreMatchers.is(2));
        observer.onInvalidated(tableSet);
        MatcherAssert.assertThat(consumer.mCount, CoreMatchers.is(3));
        disposable.dispose();
        observer.onInvalidated(tableSet);
        MatcherAssert.assertThat(consumer.mCount, CoreMatchers.is(3));
    }

    @Test
    public void internalCallable_Flowable() throws Exception {
        final AtomicReference<String> value = new AtomicReference<>(null);
        String[] tables = new String[]{ "a", "b" };
        Set<String> tableSet = new HashSet<>(Arrays.asList(tables));
        final Flowable<String> flowable = RxRoom.createFlowable(mDatabase, tables, new Callable<String>() {
            @Override
            public String call() throws Exception {
                return value.get();
            }
        });
        final RxRoomTest.CountingConsumer consumer = new RxRoomTest.CountingConsumer();
        flowable.subscribe(consumer);
        drain();
        InvalidationTracker.Observer observer = mAddedObservers.get(0);
        // no value because it is null
        MatcherAssert.assertThat(consumer.mCount, CoreMatchers.is(0));
        value.set("bla");
        observer.onInvalidated(tableSet);
        drain();
        // get value
        MatcherAssert.assertThat(consumer.mCount, CoreMatchers.is(1));
        observer.onInvalidated(tableSet);
        drain();
        // get value
        MatcherAssert.assertThat(consumer.mCount, CoreMatchers.is(2));
        value.set(null);
        observer.onInvalidated(tableSet);
        drain();
        // no value
        MatcherAssert.assertThat(consumer.mCount, CoreMatchers.is(2));
    }

    @Test
    public void internalCallable_Observable() throws Exception {
        final AtomicReference<String> value = new AtomicReference<>(null);
        String[] tables = new String[]{ "a", "b" };
        Set<String> tableSet = new HashSet<>(Arrays.asList(tables));
        final Observable<String> flowable = RxRoom.createObservable(mDatabase, tables, new Callable<String>() {
            @Override
            public String call() throws Exception {
                return value.get();
            }
        });
        final RxRoomTest.CountingConsumer consumer = new RxRoomTest.CountingConsumer();
        flowable.subscribe(consumer);
        drain();
        InvalidationTracker.Observer observer = mAddedObservers.get(0);
        // no value because it is null
        MatcherAssert.assertThat(consumer.mCount, CoreMatchers.is(0));
        value.set("bla");
        observer.onInvalidated(tableSet);
        drain();
        // get value
        MatcherAssert.assertThat(consumer.mCount, CoreMatchers.is(1));
        observer.onInvalidated(tableSet);
        drain();
        // get value
        MatcherAssert.assertThat(consumer.mCount, CoreMatchers.is(2));
        value.set(null);
        observer.onInvalidated(tableSet);
        drain();
        // no value
        MatcherAssert.assertThat(consumer.mCount, CoreMatchers.is(2));
    }

    @Test
    public void exception_Flowable() throws Exception {
        final Flowable<String> flowable = RxRoom.createFlowable(mDatabase, new String[]{ "a" }, new Callable<String>() {
            @Override
            public String call() throws Exception {
                throw new Exception("i want exception");
            }
        });
        TestSubscriber<String> subscriber = new TestSubscriber();
        flowable.subscribe(subscriber);
        drain();
        MatcherAssert.assertThat(subscriber.errorCount(), CoreMatchers.is(1));
        MatcherAssert.assertThat(subscriber.errors().get(0).getMessage(), CoreMatchers.is("i want exception"));
    }

    @Test
    public void exception_Observable() throws Exception {
        final Observable<String> flowable = RxRoom.createObservable(mDatabase, new String[]{ "a" }, new Callable<String>() {
            @Override
            public String call() throws Exception {
                throw new Exception("i want exception");
            }
        });
        TestObserver<String> observer = new TestObserver();
        flowable.subscribe(observer);
        drain();
        MatcherAssert.assertThat(observer.errorCount(), CoreMatchers.is(1));
        MatcherAssert.assertThat(observer.errors().get(0).getMessage(), CoreMatchers.is("i want exception"));
    }

    private static class CountingConsumer implements Consumer<Object> {
        int mCount = 0;

        @Override
        public void accept(@NonNull
        Object o) throws Exception {
            (mCount)++;
        }
    }
}

