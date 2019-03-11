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


import Lifecycle.Event.ON_DESTROY;
import Lifecycle.Event.ON_START;
import Lifecycle.Event.ON_STOP;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class MediatorLiveDataTest {
    @Rule
    public InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    private LifecycleOwner mOwner;

    private LifecycleRegistry mRegistry;

    private MediatorLiveData<String> mMediator;

    private LiveData<String> mSource;

    private boolean mSourceActive;

    @Test
    public void testSingleDelivery() {
        Observer observer = Mockito.mock(Observer.class);
        mMediator.addSource(mSource, observer);
        mSource.setValue("flatfoot");
        Mockito.verify(observer).onChanged("flatfoot");
        mRegistry.handleLifecycleEvent(ON_STOP);
        Mockito.reset(observer);
        Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.any());
    }

    @Test
    public void testChangeWhileInactive() {
        Observer observer = Mockito.mock(Observer.class);
        mMediator.addSource(mSource, observer);
        mMediator.observe(mOwner, Mockito.mock(Observer.class));
        mSource.setValue("one");
        Mockito.verify(observer).onChanged("one");
        mRegistry.handleLifecycleEvent(ON_STOP);
        Mockito.reset(observer);
        mSource.setValue("flatfoot");
        mRegistry.handleLifecycleEvent(ON_START);
        Mockito.verify(observer).onChanged("flatfoot");
    }

    @Test
    public void testAddSourceToActive() {
        mSource.setValue("flatfoot");
        Observer observer = Mockito.mock(Observer.class);
        mMediator.addSource(mSource, observer);
        Mockito.verify(observer).onChanged("flatfoot");
    }

    @Test
    public void testAddSourceToInActive() {
        mSource.setValue("flatfoot");
        mRegistry.handleLifecycleEvent(ON_STOP);
        Observer observer = Mockito.mock(Observer.class);
        mMediator.addSource(mSource, observer);
        Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.any());
        mRegistry.handleLifecycleEvent(ON_START);
        Mockito.verify(observer).onChanged("flatfoot");
    }

    @Test
    public void testRemoveSource() {
        mSource.setValue("flatfoot");
        Observer observer = Mockito.mock(Observer.class);
        mMediator.addSource(mSource, observer);
        Mockito.verify(observer).onChanged("flatfoot");
        mMediator.removeSource(mSource);
        Mockito.reset(observer);
        mSource.setValue("failure");
        Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.any());
    }

    @Test
    public void testSourceInactive() {
        Observer observer = Mockito.mock(Observer.class);
        mMediator.addSource(mSource, observer);
        MatcherAssert.assertThat(mSourceActive, CoreMatchers.is(true));
        mRegistry.handleLifecycleEvent(ON_STOP);
        MatcherAssert.assertThat(mSourceActive, CoreMatchers.is(false));
        mRegistry.handleLifecycleEvent(ON_START);
        MatcherAssert.assertThat(mSourceActive, CoreMatchers.is(true));
    }

    @Test
    public void testNoLeakObserver() {
        // Imitates a destruction of a ViewModel: a listener of LiveData is destroyed,
        // a reference to MediatorLiveData is cleaned up. In this case we shouldn't leak
        // MediatorLiveData as an observer of mSource.
        MatcherAssert.assertThat(mSource.hasObservers(), CoreMatchers.is(false));
        Observer observer = Mockito.mock(Observer.class);
        mMediator.addSource(mSource, observer);
        MatcherAssert.assertThat(mSource.hasObservers(), CoreMatchers.is(true));
        mRegistry.handleLifecycleEvent(ON_STOP);
        mRegistry.handleLifecycleEvent(ON_DESTROY);
        mMediator = null;
        MatcherAssert.assertThat(mSource.hasObservers(), CoreMatchers.is(false));
    }

    @Test
    public void testMultipleSources() {
        Observer observer1 = Mockito.mock(Observer.class);
        mMediator.addSource(mSource, observer1);
        MutableLiveData<Integer> source2 = new MutableLiveData();
        Observer observer2 = Mockito.mock(Observer.class);
        mMediator.addSource(source2, observer2);
        mSource.setValue("flatfoot");
        Mockito.verify(observer1).onChanged("flatfoot");
        Mockito.verify(observer2, Mockito.never()).onChanged(ArgumentMatchers.any());
        Mockito.reset(observer1, observer2);
        source2.setValue(1703);
        Mockito.verify(observer1, Mockito.never()).onChanged(ArgumentMatchers.any());
        Mockito.verify(observer2).onChanged(1703);
        Mockito.reset(observer1, observer2);
        mRegistry.handleLifecycleEvent(ON_STOP);
        mSource.setValue("failure");
        source2.setValue(0);
        Mockito.verify(observer1, Mockito.never()).onChanged(ArgumentMatchers.any());
        Mockito.verify(observer2, Mockito.never()).onChanged(ArgumentMatchers.any());
    }

    @Test
    public void removeSourceDuringOnActive() {
        // to trigger ConcurrentModificationException,
        // we have to call remove from a collection during "for" loop.
        // ConcurrentModificationException is thrown from next() method of an iterator
        // so this modification shouldn't be at the last iteration,
        // because if it is a last iteration, then next() wouldn't be called.
        // And the last: an order of an iteration over sources is not defined,
        // so I have to call it remove operation  from all observers.
        mRegistry.handleLifecycleEvent(ON_STOP);
        Observer<String> removingObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable
            String s) {
                mMediator.removeSource(mSource);
            }
        };
        mMediator.addSource(mSource, removingObserver);
        MutableLiveData<String> source2 = new MutableLiveData();
        source2.setValue("nana");
        mMediator.addSource(source2, removingObserver);
        mSource.setValue("petjack");
        mRegistry.handleLifecycleEvent(ON_START);
    }

    @Test(expected = IllegalArgumentException.class)
    public void reAddSameSourceWithDifferentObserver() {
        mMediator.addSource(mSource, Mockito.mock(Observer.class));
        mMediator.addSource(mSource, Mockito.mock(Observer.class));
    }

    @Test
    public void addSameSourceWithSameObserver() {
        Observer observer = Mockito.mock(Observer.class);
        mMediator.addSource(mSource, observer);
        mMediator.addSource(mSource, observer);
        // no exception was thrown
    }

    @Test
    public void addSourceDuringOnActive() {
        mRegistry.handleLifecycleEvent(ON_STOP);
        mSource.setValue("a");
        mMediator.addSource(mSource, new Observer<String>() {
            @Override
            public void onChanged(@Nullable
            String s) {
                MutableLiveData<String> source = new MutableLiveData();
                source.setValue("b");
                mMediator.addSource(source, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable
                    String s) {
                        mMediator.setValue("c");
                    }
                });
            }
        });
        mRegistry.handleLifecycleEvent(ON_START);
        MatcherAssert.assertThat(mMediator.getValue(), CoreMatchers.is("c"));
    }
}

