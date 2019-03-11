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
package androidx.lifecycle;


import androidx.annotation.Nullable;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.processors.ReplayProcessor;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.AsyncSubject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.reactivestreams.Subscription;


public class LiveDataReactiveStreamsTest {
    @Rule
    public final TestRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private LifecycleOwner mLifecycleOwner;

    private final List<String> mLiveDataOutput = new ArrayList<>();

    private final Observer<String> mObserver = new Observer<String>() {
        @Override
        public void onChanged(@Nullable
        String s) {
            mLiveDataOutput.add(s);
        }
    };

    private final ReplayProcessor<String> mOutputProcessor = ReplayProcessor.create();

    private static final TestScheduler sBackgroundScheduler = new TestScheduler();

    @Test
    public void convertsFromPublisher() {
        PublishProcessor<String> processor = PublishProcessor.create();
        LiveData<String> liveData = LiveDataReactiveStreams.fromPublisher(processor);
        liveData.observe(mLifecycleOwner, mObserver);
        processor.onNext("foo");
        processor.onNext("bar");
        processor.onNext("baz");
        MatcherAssert.assertThat(mLiveDataOutput, CoreMatchers.is(Arrays.asList("foo", "bar", "baz")));
    }

    @Test
    public void convertsFromPublisherSubscribeWithDelay() {
        PublishProcessor<String> processor = PublishProcessor.create();
        processor.delaySubscription(100, TimeUnit.SECONDS, LiveDataReactiveStreamsTest.sBackgroundScheduler);
        LiveData<String> liveData = LiveDataReactiveStreams.fromPublisher(processor);
        liveData.observe(mLifecycleOwner, mObserver);
        processor.onNext("foo");
        liveData.removeObserver(mObserver);
        LiveDataReactiveStreamsTest.sBackgroundScheduler.triggerActions();
        liveData.observe(mLifecycleOwner, mObserver);
        processor.onNext("bar");
        processor.onNext("baz");
        MatcherAssert.assertThat(mLiveDataOutput, CoreMatchers.is(Arrays.asList("foo", "foo", "bar", "baz")));
    }

    @Test
    public void convertsFromPublisherThrowsException() {
        PublishProcessor<String> processor = PublishProcessor.create();
        LiveData<String> liveData = LiveDataReactiveStreams.fromPublisher(processor);
        liveData.observe(mLifecycleOwner, mObserver);
        IllegalStateException exception = new IllegalStateException("test exception");
        try {
            processor.onError(exception);
            Assert.fail("Runtime Exception expected");
        } catch (RuntimeException ex) {
            Assert.assertEquals(ex.getCause(), exception);
        }
    }

    @Test
    public void convertsFromPublisherWithMultipleObservers() {
        final List<String> output2 = new ArrayList<>();
        PublishProcessor<String> processor = PublishProcessor.create();
        LiveData<String> liveData = LiveDataReactiveStreams.fromPublisher(processor);
        liveData.observe(mLifecycleOwner, mObserver);
        processor.onNext("foo");
        processor.onNext("bar");
        // The second observer should only get the newest value and any later values.
        liveData.observe(mLifecycleOwner, new Observer<String>() {
            @Override
            public void onChanged(@Nullable
            String s) {
                output2.add(s);
            }
        });
        processor.onNext("baz");
        MatcherAssert.assertThat(mLiveDataOutput, CoreMatchers.is(Arrays.asList("foo", "bar", "baz")));
        MatcherAssert.assertThat(output2, CoreMatchers.is(Arrays.asList("bar", "baz")));
    }

    @Test
    public void convertsFromPublisherWithMultipleObserversAfterInactive() {
        final List<String> output2 = new ArrayList<>();
        PublishProcessor<String> processor = PublishProcessor.create();
        LiveData<String> liveData = LiveDataReactiveStreams.fromPublisher(processor);
        liveData.observe(mLifecycleOwner, mObserver);
        processor.onNext("foo");
        processor.onNext("bar");
        // The second observer should only get the newest value and any later values.
        liveData.observe(mLifecycleOwner, new Observer<String>() {
            @Override
            public void onChanged(@Nullable
            String s) {
                output2.add(s);
            }
        });
        liveData.removeObserver(mObserver);
        processor.onNext("baz");
        MatcherAssert.assertThat(mLiveDataOutput, CoreMatchers.is(Arrays.asList("foo", "bar")));
        MatcherAssert.assertThat(output2, CoreMatchers.is(Arrays.asList("bar", "baz")));
    }

    @Test
    public void convertsFromPublisherAfterInactive() {
        PublishProcessor<String> processor = PublishProcessor.create();
        LiveData<String> liveData = LiveDataReactiveStreams.fromPublisher(processor);
        liveData.observe(mLifecycleOwner, mObserver);
        processor.onNext("foo");
        liveData.removeObserver(mObserver);
        processor.onNext("bar");
        liveData.observe(mLifecycleOwner, mObserver);
        processor.onNext("baz");
        MatcherAssert.assertThat(mLiveDataOutput, CoreMatchers.is(Arrays.asList("foo", "foo", "baz")));
    }

    @Test
    public void convertsFromPublisherManagesSubscriptions() {
        PublishProcessor<String> processor = PublishProcessor.create();
        LiveData<String> liveData = LiveDataReactiveStreams.fromPublisher(processor);
        MatcherAssert.assertThat(processor.hasSubscribers(), CoreMatchers.is(false));
        liveData.observe(mLifecycleOwner, mObserver);
        // once the live data is active, there's a subscriber
        MatcherAssert.assertThat(processor.hasSubscribers(), CoreMatchers.is(true));
        liveData.removeObserver(mObserver);
        // once the live data is inactive, the subscriber is removed
        MatcherAssert.assertThat(processor.hasSubscribers(), CoreMatchers.is(false));
    }

    @Test
    public void convertsFromAsyncPublisher() {
        Flowable<String> input = Flowable.just("foo").concatWith(Flowable.just("bar", "baz").observeOn(LiveDataReactiveStreamsTest.sBackgroundScheduler));
        LiveData<String> liveData = LiveDataReactiveStreams.fromPublisher(input);
        liveData.observe(mLifecycleOwner, mObserver);
        MatcherAssert.assertThat(mLiveDataOutput, CoreMatchers.is(Collections.singletonList("foo")));
        LiveDataReactiveStreamsTest.sBackgroundScheduler.triggerActions();
        MatcherAssert.assertThat(mLiveDataOutput, CoreMatchers.is(Arrays.asList("foo", "bar", "baz")));
    }

    @Test
    public void convertsToPublisherWithSyncData() {
        MutableLiveData<String> liveData = new MutableLiveData();
        liveData.setValue("foo");
        MatcherAssert.assertThat(liveData.getValue(), CoreMatchers.is("foo"));
        Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(mLifecycleOwner, liveData)).subscribe(mOutputProcessor);
        liveData.setValue("bar");
        liveData.setValue("baz");
        MatcherAssert.assertThat(mOutputProcessor.getValues(new String[]{  }), CoreMatchers.is(new String[]{ "foo", "bar", "baz" }));
    }

    @Test
    public void convertingToPublisherIsCancelable() {
        MutableLiveData<String> liveData = new MutableLiveData();
        liveData.setValue("foo");
        MatcherAssert.assertThat(liveData.getValue(), CoreMatchers.is("foo"));
        Disposable disposable = Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(mLifecycleOwner, liveData)).subscribe(new io.reactivex.functions.Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                mLiveDataOutput.add(s);
            }
        });
        liveData.setValue("bar");
        liveData.setValue("baz");
        MatcherAssert.assertThat(liveData.hasObservers(), CoreMatchers.is(true));
        disposable.dispose();
        liveData.setValue("fizz");
        liveData.setValue("buzz");
        MatcherAssert.assertThat(mLiveDataOutput, CoreMatchers.is(Arrays.asList("foo", "bar", "baz")));
        // Canceling disposable should also remove livedata mObserver.
        MatcherAssert.assertThat(liveData.hasObservers(), CoreMatchers.is(false));
    }

    @Test
    public void convertsToPublisherWithBackpressure() {
        MutableLiveData<String> liveData = new MutableLiveData();
        final AsyncSubject<Subscription> subscriptionSubject = AsyncSubject.create();
        Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(mLifecycleOwner, liveData)).subscribe(new org.reactivestreams.Subscriber<String>() {
            @Override
            public void onSubscribe(Subscription s) {
                subscriptionSubject.onNext(s);
                subscriptionSubject.onComplete();
            }

            @Override
            public void onNext(String s) {
                mOutputProcessor.onNext(s);
            }

            @Override
            public void onError(Throwable t) {
                throw new RuntimeException(t);
            }

            @Override
            public void onComplete() {
            }
        });
        // Subscription should have happened synchronously. If it didn't, this will deadlock.
        final Subscription subscription = subscriptionSubject.blockingSingle();
        subscription.request(1);
        MatcherAssert.assertThat(mOutputProcessor.getValues(new String[]{  }), CoreMatchers.is(new String[]{  }));
        liveData.setValue("foo");
        MatcherAssert.assertThat(mOutputProcessor.getValues(new String[]{  }), CoreMatchers.is(new String[]{ "foo" }));
        subscription.request(2);
        liveData.setValue("baz");
        liveData.setValue("fizz");
        MatcherAssert.assertThat(mOutputProcessor.getValues(new String[]{  }), CoreMatchers.is(new String[]{ "foo", "baz", "fizz" }));
        // 'nyan' will be dropped as there is nothing currently requesting a stream.
        liveData.setValue("nyan");
        liveData.setValue("cat");
        MatcherAssert.assertThat(mOutputProcessor.getValues(new String[]{  }), CoreMatchers.is(new String[]{ "foo", "baz", "fizz" }));
        // When a new request comes in, the latest value will be pushed.
        subscription.request(1);
        MatcherAssert.assertThat(mOutputProcessor.getValues(new String[]{  }), CoreMatchers.is(new String[]{ "foo", "baz", "fizz", "cat" }));
    }

    @Test
    public void convertsToPublisherWithAsyncData() {
        MutableLiveData<String> liveData = new MutableLiveData();
        Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(mLifecycleOwner, liveData)).observeOn(LiveDataReactiveStreamsTest.sBackgroundScheduler).subscribe(mOutputProcessor);
        liveData.setValue("foo");
        MatcherAssert.assertThat(mOutputProcessor.getValues(new String[]{  }), CoreMatchers.is(new String[]{  }));
        LiveDataReactiveStreamsTest.sBackgroundScheduler.triggerActions();
        MatcherAssert.assertThat(mOutputProcessor.getValues(new String[]{  }), CoreMatchers.is(new String[]{ "foo" }));
        liveData.setValue("bar");
        liveData.setValue("baz");
        MatcherAssert.assertThat(mOutputProcessor.getValues(new String[]{  }), CoreMatchers.is(new String[]{ "foo" }));
        LiveDataReactiveStreamsTest.sBackgroundScheduler.triggerActions();
        MatcherAssert.assertThat(mOutputProcessor.getValues(new String[]{  }), CoreMatchers.is(new String[]{ "foo", "bar", "baz" }));
    }
}

