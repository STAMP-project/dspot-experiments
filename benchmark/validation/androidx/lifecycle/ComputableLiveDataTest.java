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


import Lifecycle.Event;
import Lifecycle.Event.ON_CREATE;
import Lifecycle.Event.ON_START;
import Lifecycle.Event.ON_STOP;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;
import androidx.arch.core.executor.TaskExecutorWithFakeMainThread;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class ComputableLiveDataTest {
    private TaskExecutor mTaskExecutor;

    private ComputableLiveDataTest.TestLifecycleOwner mLifecycleOwner;

    @Test
    public void noComputeWithoutObservers() {
        final ComputableLiveDataTest.TestComputable computable = new ComputableLiveDataTest.TestComputable();
        Mockito.verify(mTaskExecutor, Mockito.never()).executeOnDiskIO(computable.mRefreshRunnable);
        Mockito.verify(mTaskExecutor, Mockito.never()).executeOnDiskIO(computable.mInvalidationRunnable);
    }

    @Test
    public void noConcurrentCompute() throws InterruptedException {
        TaskExecutorWithFakeMainThread executor = new TaskExecutorWithFakeMainThread(2);
        ArchTaskExecutor.getInstance().setDelegate(executor);
        try {
            // # of compute calls
            final Semaphore computeCounter = new Semaphore(0);
            // available permits for computation
            final Semaphore computeLock = new Semaphore(0);
            final ComputableLiveDataTest.TestComputable computable = new ComputableLiveDataTest.TestComputable(1, 2) {
                @Override
                protected Integer compute() {
                    try {
                        computeCounter.release(1);
                        computeLock.tryAcquire(1, 20, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        throw new AssertionError(e);
                    }
                    return super.compute();
                }
            };
            final ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
            // noinspection unchecked
            final Observer<Integer> observer = Mockito.mock(Observer.class);
            executor.postToMainThread(new Runnable() {
                @Override
                public void run() {
                    getLiveData().observeForever(observer);
                    Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.anyInt());
                }
            });
            // wait for first compute call
            MatcherAssert.assertThat(computeCounter.tryAcquire(1, 2, TimeUnit.SECONDS), CoreMatchers.is(true));
            // re-invalidate while in compute
            invalidate();
            invalidate();
            invalidate();
            invalidate();
            // ensure another compute call does not arrive
            MatcherAssert.assertThat(computeCounter.tryAcquire(1, 2, TimeUnit.SECONDS), CoreMatchers.is(false));
            // allow computation to finish
            computeLock.release(2);
            // wait for the second result, first will be skipped due to invalidation during compute
            Mockito.verify(observer, Mockito.timeout(2000)).onChanged(captor.capture());
            MatcherAssert.assertThat(captor.getAllValues(), CoreMatchers.is(Collections.singletonList(2)));
            Mockito.reset(observer);
            // allow all computations to run, there should not be any.
            computeLock.release(100);
            // unfortunately, Mockito.after is not available in 1.9.5
            executor.drainTasks(2);
            // assert no other results arrive
            Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.anyInt());
        } finally {
            ArchTaskExecutor.getInstance().setDelegate(null);
        }
    }

    @Test
    public void addingObserverShouldTriggerAComputation() {
        ComputableLiveDataTest.TestComputable computable = new ComputableLiveDataTest.TestComputable(1);
        mLifecycleOwner.handleEvent(ON_CREATE);
        final AtomicInteger mValue = new AtomicInteger((-1));
        getLiveData().observe(mLifecycleOwner, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable
            Integer integer) {
                // noinspection ConstantConditions
                mValue.set(integer);
            }
        });
        Mockito.verify(mTaskExecutor, Mockito.never()).executeOnDiskIO(ArgumentMatchers.any(Runnable.class));
        MatcherAssert.assertThat(mValue.get(), CoreMatchers.is((-1)));
        mLifecycleOwner.handleEvent(ON_START);
        Mockito.verify(mTaskExecutor).executeOnDiskIO(computable.mRefreshRunnable);
        MatcherAssert.assertThat(mValue.get(), CoreMatchers.is(1));
    }

    @Test
    public void customExecutor() {
        Executor customExecutor = Mockito.mock(Executor.class);
        ComputableLiveDataTest.TestComputable computable = new ComputableLiveDataTest.TestComputable(customExecutor, 1);
        mLifecycleOwner.handleEvent(ON_CREATE);
        getLiveData().observe(mLifecycleOwner, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable
            Integer integer) {
                // ignored
            }
        });
        Mockito.verify(mTaskExecutor, Mockito.never()).executeOnDiskIO(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(customExecutor, Mockito.never()).execute(ArgumentMatchers.any(Runnable.class));
        mLifecycleOwner.handleEvent(ON_START);
        Mockito.verify(mTaskExecutor, Mockito.never()).executeOnDiskIO(computable.mRefreshRunnable);
        Mockito.verify(customExecutor).execute(computable.mRefreshRunnable);
    }

    @Test
    public void invalidationShouldNotReTriggerComputationIfObserverIsInActive() {
        ComputableLiveDataTest.TestComputable computable = new ComputableLiveDataTest.TestComputable(1, 2);
        mLifecycleOwner.handleEvent(ON_START);
        final AtomicInteger mValue = new AtomicInteger((-1));
        getLiveData().observe(mLifecycleOwner, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable
            Integer integer) {
                // noinspection ConstantConditions
                mValue.set(integer);
            }
        });
        MatcherAssert.assertThat(mValue.get(), CoreMatchers.is(1));
        mLifecycleOwner.handleEvent(ON_STOP);
        invalidate();
        Mockito.reset(mTaskExecutor);
        Mockito.verify(mTaskExecutor, Mockito.never()).executeOnDiskIO(computable.mRefreshRunnable);
        MatcherAssert.assertThat(mValue.get(), CoreMatchers.is(1));
    }

    @Test
    public void invalidationShouldReTriggerQueryIfObserverIsActive() {
        ComputableLiveDataTest.TestComputable computable = new ComputableLiveDataTest.TestComputable(1, 2);
        mLifecycleOwner.handleEvent(ON_START);
        final AtomicInteger mValue = new AtomicInteger((-1));
        getLiveData().observe(mLifecycleOwner, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable
            Integer integer) {
                // noinspection ConstantConditions
                mValue.set(integer);
            }
        });
        MatcherAssert.assertThat(mValue.get(), CoreMatchers.is(1));
        invalidate();
        MatcherAssert.assertThat(mValue.get(), CoreMatchers.is(2));
    }

    static class TestComputable extends ComputableLiveData<Integer> {
        final int[] mValues;

        AtomicInteger mValueCounter = new AtomicInteger();

        TestComputable(@NonNull
        Executor executor, int... values) {
            super(executor);
            mValues = values;
        }

        TestComputable(int... values) {
            mValues = values;
        }

        @Override
        protected Integer compute() {
            return mValues[mValueCounter.getAndIncrement()];
        }
    }

    static class TestLifecycleOwner implements LifecycleOwner {
        private LifecycleRegistry mLifecycle;

        TestLifecycleOwner() {
            mLifecycle = new LifecycleRegistry(this);
        }

        @Override
        public Lifecycle getLifecycle() {
            return mLifecycle;
        }

        void handleEvent(Lifecycle.Event event) {
            mLifecycle.handleLifecycleEvent(event);
        }
    }
}

