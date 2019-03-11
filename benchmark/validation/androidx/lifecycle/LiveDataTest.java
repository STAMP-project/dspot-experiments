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


import Lifecycle.Event.ON_START;
import Lifecycle.Event.ON_STOP;
import Lifecycle.State.CREATED;
import Lifecycle.State.STARTED;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static Event.ON_STOP;


@SuppressWarnings({ "unchecked" })
@RunWith(JUnit4.class)
public class LiveDataTest {
    @Rule
    public InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    private LiveDataTest.PublicLiveData<String> mLiveData;

    private LiveDataTest.MethodExec mActiveObserversChanged;

    private LifecycleOwner mOwner;

    private LifecycleRegistry mRegistry;

    private LifecycleOwner mOwner2;

    private LifecycleRegistry mRegistry2;

    private LifecycleOwner mOwner3;

    private Lifecycle mLifecycle3;

    private Observer<String> mObserver3;

    private LifecycleOwner mOwner4;

    private Lifecycle mLifecycle4;

    private Observer<String> mObserver4;

    private boolean mInObserver;

    @Test
    public void testObserverToggle() {
        Observer<String> observer = ((Observer<String>) (Mockito.mock(Observer.class)));
        mLiveData.observe(mOwner, observer);
        Mockito.verify(mActiveObserversChanged, Mockito.never()).onCall(ArgumentMatchers.anyBoolean());
        MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(true));
        MatcherAssert.assertThat(mLiveData.hasActiveObservers(), CoreMatchers.is(false));
        mLiveData.removeObserver(observer);
        Mockito.verify(mActiveObserversChanged, Mockito.never()).onCall(ArgumentMatchers.anyBoolean());
        MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(false));
        MatcherAssert.assertThat(mLiveData.hasActiveObservers(), CoreMatchers.is(false));
    }

    @Test
    public void testActiveObserverToggle() {
        Observer<String> observer = ((Observer<String>) (Mockito.mock(Observer.class)));
        mLiveData.observe(mOwner, observer);
        Mockito.verify(mActiveObserversChanged, Mockito.never()).onCall(ArgumentMatchers.anyBoolean());
        MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(true));
        MatcherAssert.assertThat(mLiveData.hasActiveObservers(), CoreMatchers.is(false));
        mRegistry.handleLifecycleEvent(Event.ON_START);
        Mockito.verify(mActiveObserversChanged).onCall(true);
        MatcherAssert.assertThat(mLiveData.hasActiveObservers(), CoreMatchers.is(true));
        Mockito.reset(mActiveObserversChanged);
        mRegistry.handleLifecycleEvent(Event.ON_STOP);
        Mockito.verify(mActiveObserversChanged).onCall(false);
        MatcherAssert.assertThat(mLiveData.hasActiveObservers(), CoreMatchers.is(false));
        MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(true));
        Mockito.reset(mActiveObserversChanged);
        mRegistry.handleLifecycleEvent(Event.ON_START);
        Mockito.verify(mActiveObserversChanged).onCall(true);
        MatcherAssert.assertThat(mLiveData.hasActiveObservers(), CoreMatchers.is(true));
        MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(true));
        Mockito.reset(mActiveObserversChanged);
        mLiveData.removeObserver(observer);
        Mockito.verify(mActiveObserversChanged).onCall(false);
        MatcherAssert.assertThat(mLiveData.hasActiveObservers(), CoreMatchers.is(false));
        MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(false));
        Mockito.verifyNoMoreInteractions(mActiveObserversChanged);
    }

    @Test
    public void testReAddSameObserverTuple() {
        Observer<String> observer = ((Observer<String>) (Mockito.mock(Observer.class)));
        mLiveData.observe(mOwner, observer);
        mLiveData.observe(mOwner, observer);
        MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(true));
    }

    @Test
    public void testAdd2ObserversWithSameOwnerAndRemove() {
        Observer<String> o1 = ((Observer<String>) (Mockito.mock(Observer.class)));
        Observer<String> o2 = ((Observer<String>) (Mockito.mock(Observer.class)));
        mLiveData.observe(mOwner, o1);
        mLiveData.observe(mOwner, o2);
        MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(true));
        Mockito.verify(mActiveObserversChanged, Mockito.never()).onCall(ArgumentMatchers.anyBoolean());
        mRegistry.handleLifecycleEvent(Event.ON_START);
        Mockito.verify(mActiveObserversChanged).onCall(true);
        mLiveData.setValue("a");
        Mockito.verify(o1).onChanged("a");
        Mockito.verify(o2).onChanged("a");
        mLiveData.removeObservers(mOwner);
        MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(false));
        MatcherAssert.assertThat(mRegistry.getObserverCount(), CoreMatchers.is(0));
    }

    @Test
    public void testAddSameObserverIn2LifecycleOwners() {
        Observer<String> observer = ((Observer<String>) (Mockito.mock(Observer.class)));
        mLiveData.observe(mOwner, observer);
        Throwable throwable = null;
        try {
            mLiveData.observe(mOwner2, observer);
        } catch (Throwable t) {
            throwable = t;
        }
        MatcherAssert.assertThat(throwable, CoreMatchers.instanceOf(IllegalArgumentException.class));
        // noinspection ConstantConditions
        MatcherAssert.assertThat(throwable.getMessage(), CoreMatchers.is("Cannot add the same observer with different lifecycles"));
    }

    @Test
    public void testRemoveDestroyedObserver() {
        Observer<String> observer = ((Observer<String>) (Mockito.mock(Observer.class)));
        mLiveData.observe(mOwner, observer);
        mRegistry.handleLifecycleEvent(Event.ON_START);
        Mockito.verify(mActiveObserversChanged).onCall(true);
        MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(true));
        MatcherAssert.assertThat(mLiveData.hasActiveObservers(), CoreMatchers.is(true));
        Mockito.reset(mActiveObserversChanged);
        mRegistry.handleLifecycleEvent(Event.ON_DESTROY);
        MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(false));
        MatcherAssert.assertThat(mLiveData.hasActiveObservers(), CoreMatchers.is(false));
        Mockito.verify(mActiveObserversChanged).onCall(false);
    }

    @Test
    public void testInactiveRegistry() {
        Observer<String> observer = ((Observer<String>) (Mockito.mock(Observer.class)));
        mRegistry.handleLifecycleEvent(Event.ON_DESTROY);
        mLiveData.observe(mOwner, observer);
        MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(false));
    }

    @Test
    public void testNotifyActiveInactive() {
        Observer<String> observer = ((Observer<String>) (Mockito.mock(Observer.class)));
        mRegistry.handleLifecycleEvent(Event.ON_CREATE);
        mLiveData.observe(mOwner, observer);
        mLiveData.setValue("a");
        Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.anyString());
        mRegistry.handleLifecycleEvent(Event.ON_START);
        Mockito.verify(observer).onChanged("a");
        mLiveData.setValue("b");
        Mockito.verify(observer).onChanged("b");
        mRegistry.handleLifecycleEvent(Event.ON_STOP);
        mLiveData.setValue("c");
        Mockito.verify(observer, Mockito.never()).onChanged("c");
        mRegistry.handleLifecycleEvent(Event.ON_START);
        Mockito.verify(observer).onChanged("c");
        Mockito.reset(observer);
        mRegistry.handleLifecycleEvent(Event.ON_STOP);
        mRegistry.handleLifecycleEvent(Event.ON_START);
        Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.anyString());
    }

    @Test
    public void testStopObservingOwner_onDestroy() {
        Observer<String> observer = ((Observer<String>) (Mockito.mock(Observer.class)));
        mRegistry.handleLifecycleEvent(Event.ON_CREATE);
        mLiveData.observe(mOwner, observer);
        MatcherAssert.assertThat(mRegistry.getObserverCount(), CoreMatchers.is(1));
        mRegistry.handleLifecycleEvent(Event.ON_DESTROY);
        MatcherAssert.assertThat(mRegistry.getObserverCount(), CoreMatchers.is(0));
    }

    @Test
    public void testStopObservingOwner_onStopObserving() {
        Observer<String> observer = ((Observer<String>) (Mockito.mock(Observer.class)));
        mRegistry.handleLifecycleEvent(Event.ON_CREATE);
        mLiveData.observe(mOwner, observer);
        MatcherAssert.assertThat(mRegistry.getObserverCount(), CoreMatchers.is(1));
        mLiveData.removeObserver(observer);
        MatcherAssert.assertThat(mRegistry.getObserverCount(), CoreMatchers.is(0));
    }

    @Test
    public void testActiveChangeInCallback() {
        mRegistry.handleLifecycleEvent(Event.ON_START);
        Observer<String> observer1 = Mockito.spy(new Observer<String>() {
            @Override
            public void onChanged(@Nullable
            String s) {
                mRegistry.handleLifecycleEvent(Event.ON_STOP);
                MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(true));
                MatcherAssert.assertThat(mLiveData.hasActiveObservers(), CoreMatchers.is(false));
            }
        });
        final Observer observer2 = Mockito.mock(Observer.class);
        mLiveData.observe(mOwner, observer1);
        mLiveData.observe(mOwner, observer2);
        mLiveData.setValue("bla");
        Mockito.verify(observer1).onChanged(ArgumentMatchers.anyString());
        Mockito.verify(observer2, Mockito.never()).onChanged(ArgumentMatchers.anyString());
        MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(true));
        MatcherAssert.assertThat(mLiveData.hasActiveObservers(), CoreMatchers.is(false));
    }

    @Test
    public void testActiveChangeInCallback2() {
        Observer<String> observer1 = Mockito.spy(new Observer<String>() {
            @Override
            public void onChanged(@Nullable
            String s) {
                MatcherAssert.assertThat(mInObserver, CoreMatchers.is(false));
                mInObserver = true;
                mRegistry.handleLifecycleEvent(Event.ON_START);
                MatcherAssert.assertThat(mLiveData.hasActiveObservers(), CoreMatchers.is(true));
                mInObserver = false;
            }
        });
        final Observer observer2 = Mockito.spy(new LiveDataTest.FailReentranceObserver());
        mLiveData.observeForever(observer1);
        mLiveData.observe(mOwner, observer2);
        mLiveData.setValue("bla");
        Mockito.verify(observer1).onChanged(ArgumentMatchers.anyString());
        Mockito.verify(observer2).onChanged(ArgumentMatchers.anyString());
        MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(true));
        MatcherAssert.assertThat(mLiveData.hasActiveObservers(), CoreMatchers.is(true));
    }

    @Test
    public void testObserverRemovalInCallback() {
        mRegistry.handleLifecycleEvent(Event.ON_START);
        Observer<String> observer = Mockito.spy(new Observer<String>() {
            @Override
            public void onChanged(@Nullable
            String s) {
                MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(true));
                mLiveData.removeObserver(this);
                MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(false));
            }
        });
        mLiveData.observe(mOwner, observer);
        mLiveData.setValue("bla");
        Mockito.verify(observer).onChanged(ArgumentMatchers.anyString());
        MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(false));
    }

    @Test
    public void testObserverAdditionInCallback() {
        mRegistry.handleLifecycleEvent(Event.ON_START);
        final Observer observer2 = Mockito.spy(new LiveDataTest.FailReentranceObserver());
        Observer<String> observer1 = Mockito.spy(new Observer<String>() {
            @Override
            public void onChanged(@Nullable
            String s) {
                MatcherAssert.assertThat(mInObserver, CoreMatchers.is(false));
                mInObserver = true;
                mLiveData.observe(mOwner, observer2);
                MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(true));
                MatcherAssert.assertThat(mLiveData.hasActiveObservers(), CoreMatchers.is(true));
                mInObserver = false;
            }
        });
        mLiveData.observe(mOwner, observer1);
        mLiveData.setValue("bla");
        Mockito.verify(observer1).onChanged(ArgumentMatchers.anyString());
        Mockito.verify(observer2).onChanged(ArgumentMatchers.anyString());
        MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(true));
        MatcherAssert.assertThat(mLiveData.hasActiveObservers(), CoreMatchers.is(true));
    }

    @Test
    public void testObserverWithoutLifecycleOwner() {
        Observer<String> observer = ((Observer<String>) (Mockito.mock(Observer.class)));
        mLiveData.setValue("boring");
        mLiveData.observeForever(observer);
        Mockito.verify(mActiveObserversChanged).onCall(true);
        Mockito.verify(observer).onChanged("boring");
        mLiveData.setValue("tihs");
        Mockito.verify(observer).onChanged("tihs");
        mLiveData.removeObserver(observer);
        Mockito.verify(mActiveObserversChanged).onCall(false);
        mLiveData.setValue("boring");
        Mockito.reset(observer);
        Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.anyString());
    }

    @Test
    public void testSetValueDuringSetValue() {
        mRegistry.handleLifecycleEvent(Event.ON_START);
        final Observer observer1 = Mockito.spy(new Observer<String>() {
            @Override
            public void onChanged(String o) {
                MatcherAssert.assertThat(mInObserver, CoreMatchers.is(false));
                mInObserver = true;
                if (o.equals("bla")) {
                    mLiveData.setValue("gt");
                }
                mInObserver = false;
            }
        });
        final Observer observer2 = Mockito.spy(new LiveDataTest.FailReentranceObserver());
        mLiveData.observe(mOwner, observer1);
        mLiveData.observe(mOwner, observer2);
        mLiveData.setValue("bla");
        Mockito.verify(observer1, Mockito.atMost(2)).onChanged("gt");
        Mockito.verify(observer2, Mockito.atMost(2)).onChanged("gt");
    }

    @Test
    public void testRemoveDuringSetValue() {
        mRegistry.handleLifecycleEvent(Event.ON_START);
        final Observer observer1 = Mockito.spy(new Observer<String>() {
            @Override
            public void onChanged(String o) {
                mLiveData.removeObserver(this);
            }
        });
        Observer<String> observer2 = ((Observer<String>) (Mockito.mock(Observer.class)));
        mLiveData.observeForever(observer1);
        mLiveData.observe(mOwner, observer2);
        mLiveData.setValue("gt");
        Mockito.verify(observer2).onChanged("gt");
    }

    @Test
    public void testDataChangeDuringStateChange() {
        mRegistry.handleLifecycleEvent(Event.ON_START);
        mRegistry.addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(ON_STOP)
            public void onStop() {
                // change data in onStop, observer should not be called!
                mLiveData.setValue("b");
            }
        });
        Observer<String> observer = ((Observer<String>) (Mockito.mock(Observer.class)));
        mLiveData.setValue("a");
        mLiveData.observe(mOwner, observer);
        Mockito.verify(observer).onChanged("a");
        mRegistry.handleLifecycleEvent(Event.ON_PAUSE);
        mRegistry.handleLifecycleEvent(Event.ON_STOP);
        Mockito.verify(observer, Mockito.never()).onChanged("b");
        mRegistry.handleLifecycleEvent(Event.ON_RESUME);
        Mockito.verify(observer).onChanged("b");
    }

    @Test
    public void testNotCallInactiveWithObserveForever() {
        mRegistry.handleLifecycleEvent(Event.ON_START);
        Observer<String> observer = ((Observer<String>) (Mockito.mock(Observer.class)));
        Observer<String> observer2 = ((Observer<String>) (Mockito.mock(Observer.class)));
        mLiveData.observe(mOwner, observer);
        mLiveData.observeForever(observer2);
        Mockito.verify(mActiveObserversChanged).onCall(true);
        Mockito.reset(mActiveObserversChanged);
        mRegistry.handleLifecycleEvent(Event.ON_STOP);
        Mockito.verify(mActiveObserversChanged, Mockito.never()).onCall(ArgumentMatchers.anyBoolean());
        mRegistry.handleLifecycleEvent(Event.ON_START);
        Mockito.verify(mActiveObserversChanged, Mockito.never()).onCall(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testRemoveDuringAddition() {
        mRegistry.handleLifecycleEvent(Event.ON_START);
        mLiveData.setValue("bla");
        mLiveData.observeForever(new Observer<String>() {
            @Override
            public void onChanged(@Nullable
            String s) {
                mLiveData.removeObserver(this);
            }
        });
        MatcherAssert.assertThat(mLiveData.hasActiveObservers(), CoreMatchers.is(false));
        InOrder inOrder = Mockito.inOrder(mActiveObserversChanged);
        inOrder.verify(mActiveObserversChanged).onCall(true);
        inOrder.verify(mActiveObserversChanged).onCall(false);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testRemoveDuringBringingUpToState() {
        mLiveData.setValue("bla");
        mLiveData.observeForever(new Observer<String>() {
            @Override
            public void onChanged(@Nullable
            String s) {
                mLiveData.removeObserver(this);
            }
        });
        mRegistry.handleLifecycleEvent(Event.ON_RESUME);
        MatcherAssert.assertThat(mLiveData.hasActiveObservers(), CoreMatchers.is(false));
        InOrder inOrder = Mockito.inOrder(mActiveObserversChanged);
        inOrder.verify(mActiveObserversChanged).onCall(true);
        inOrder.verify(mActiveObserversChanged).onCall(false);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void setValue_neverActive_observerOnChangedNotCalled() {
        Observer<String> observer = ((Observer<String>) (Mockito.mock(Observer.class)));
        mLiveData.observe(mOwner, observer);
        mLiveData.setValue("1");
        Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.anyString());
    }

    @Test
    public void setValue_twoObserversTwoStartedOwners_onChangedCalledOnBoth() {
        Observer<String> observer1 = Mockito.mock(Observer.class);
        Observer<String> observer2 = Mockito.mock(Observer.class);
        mLiveData.observe(mOwner, observer1);
        mLiveData.observe(mOwner2, observer2);
        mRegistry.handleLifecycleEvent(ON_START);
        mRegistry2.handleLifecycleEvent(ON_START);
        mLiveData.setValue("1");
        Mockito.verify(observer1).onChanged("1");
        Mockito.verify(observer2).onChanged("1");
    }

    @Test
    public void setValue_twoObserversOneStartedOwner_onChangedCalledOnOneCorrectObserver() {
        Observer<String> observer1 = Mockito.mock(Observer.class);
        Observer<String> observer2 = Mockito.mock(Observer.class);
        mLiveData.observe(mOwner, observer1);
        mLiveData.observe(mOwner2, observer2);
        mRegistry.handleLifecycleEvent(ON_START);
        mLiveData.setValue("1");
        Mockito.verify(observer1).onChanged("1");
        Mockito.verify(observer2, Mockito.never()).onChanged(ArgumentMatchers.anyString());
    }

    @Test
    public void setValue_twoObserversBothStartedAfterSetValue_onChangedCalledOnBoth() {
        Observer<String> observer1 = Mockito.mock(Observer.class);
        Observer<String> observer2 = Mockito.mock(Observer.class);
        mLiveData.observe(mOwner, observer1);
        mLiveData.observe(mOwner2, observer2);
        mLiveData.setValue("1");
        mRegistry.handleLifecycleEvent(ON_START);
        mRegistry2.handleLifecycleEvent(ON_START);
        Mockito.verify(observer1).onChanged("1");
        Mockito.verify(observer1).onChanged("1");
    }

    @Test
    public void setValue_twoObserversOneStartedAfterSetValue_onChangedCalledOnCorrectObserver() {
        Observer<String> observer1 = Mockito.mock(Observer.class);
        Observer<String> observer2 = Mockito.mock(Observer.class);
        mLiveData.observe(mOwner, observer1);
        mLiveData.observe(mOwner2, observer2);
        mLiveData.setValue("1");
        mRegistry.handleLifecycleEvent(ON_START);
        Mockito.verify(observer1).onChanged("1");
        Mockito.verify(observer2, Mockito.never()).onChanged(ArgumentMatchers.anyString());
    }

    @Test
    public void setValue_twoObserversOneStarted_liveDataBecomesActive() {
        Observer<String> observer1 = Mockito.mock(Observer.class);
        Observer<String> observer2 = Mockito.mock(Observer.class);
        mLiveData.observe(mOwner, observer1);
        mLiveData.observe(mOwner2, observer2);
        mRegistry.handleLifecycleEvent(ON_START);
        Mockito.verify(mActiveObserversChanged).onCall(true);
    }

    @Test
    public void setValue_twoObserversOneStopped_liveDataStaysActive() {
        Observer<String> observer1 = Mockito.mock(Observer.class);
        Observer<String> observer2 = Mockito.mock(Observer.class);
        mLiveData.observe(mOwner, observer1);
        mLiveData.observe(mOwner2, observer2);
        mRegistry.handleLifecycleEvent(ON_START);
        mRegistry2.handleLifecycleEvent(ON_START);
        Mockito.verify(mActiveObserversChanged).onCall(true);
        Mockito.reset(mActiveObserversChanged);
        mRegistry.handleLifecycleEvent(ON_STOP);
        Mockito.verify(mActiveObserversChanged, Mockito.never()).onCall(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void setValue_lifecycleIsCreatedNoEvent_liveDataBecomesInactiveAndObserverNotCalled() {
        // Arrange.
        mLiveData.observe(mOwner3, mObserver3);
        LifecycleEventObserver lifecycleObserver = getLiveDataInternalObserver(mLifecycle3);
        Mockito.when(mLifecycle3.getCurrentState()).thenReturn(STARTED);
        lifecycleObserver.onStateChanged(mOwner3, ON_START);
        Mockito.when(mLifecycle3.getCurrentState()).thenReturn(CREATED);
        Mockito.reset(mActiveObserversChanged);
        Mockito.reset(mObserver3);
        // Act.
        mLiveData.setValue("1");
        // Assert.
        Mockito.verify(mActiveObserversChanged).onCall(false);
        Mockito.verify(mObserver3, Mockito.never()).onChanged(ArgumentMatchers.anyString());
    }

    /* Arrange: LiveData was made inactive via SetValue (because the Lifecycle it was
    observing was in the CREATED state and no event was dispatched).
    Act: Lifecycle enters Started state and dispatches event.
    Assert: LiveData becomes active and dispatches new value to observer.
     */
    @Test
    public void test_liveDataInactiveViaSetValueThenLifecycleResumes() {
        // Arrange.
        mLiveData.observe(mOwner3, mObserver3);
        LifecycleEventObserver lifecycleObserver = getLiveDataInternalObserver(mLifecycle3);
        Mockito.when(mLifecycle3.getCurrentState()).thenReturn(STARTED);
        lifecycleObserver.onStateChanged(mOwner3, ON_START);
        Mockito.when(mLifecycle3.getCurrentState()).thenReturn(CREATED);
        mLiveData.setValue("1");
        Mockito.reset(mActiveObserversChanged);
        Mockito.reset(mObserver3);
        // Act.
        Mockito.when(mLifecycle3.getCurrentState()).thenReturn(STARTED);
        lifecycleObserver.onStateChanged(mOwner3, ON_START);
        // Assert.
        Mockito.verify(mActiveObserversChanged).onCall(true);
        Mockito.verify(mObserver3).onChanged("1");
    }

    /* Arrange: One of two Lifecycles enter the CREATED state without sending an event.
    Act: Lifecycle's setValue method is called with new value.
    Assert: LiveData stays active and new value is dispatched to Lifecycle that is still at least
    STARTED.
     */
    @Test
    public void setValue_oneOfTwoLifecyclesAreCreatedNoEvent() {
        // Arrange.
        mLiveData.observe(mOwner3, mObserver3);
        mLiveData.observe(mOwner4, mObserver4);
        LifecycleEventObserver lifecycleObserver3 = getLiveDataInternalObserver(mLifecycle3);
        LifecycleEventObserver lifecycleObserver4 = getLiveDataInternalObserver(mLifecycle4);
        Mockito.when(mLifecycle3.getCurrentState()).thenReturn(STARTED);
        Mockito.when(mLifecycle4.getCurrentState()).thenReturn(STARTED);
        lifecycleObserver3.onStateChanged(mOwner3, ON_START);
        lifecycleObserver4.onStateChanged(mOwner4, ON_START);
        Mockito.when(mLifecycle3.getCurrentState()).thenReturn(CREATED);
        Mockito.reset(mActiveObserversChanged);
        Mockito.reset(mObserver3);
        Mockito.reset(mObserver4);
        // Act.
        mLiveData.setValue("1");
        // Assert.
        Mockito.verify(mActiveObserversChanged, Mockito.never()).onCall(ArgumentMatchers.anyBoolean());
        Mockito.verify(mObserver3, Mockito.never()).onChanged(ArgumentMatchers.anyString());
        Mockito.verify(mObserver4).onChanged("1");
    }

    /* Arrange: Two observed Lifecycles enter the CREATED state without sending an event.
    Act: Lifecycle's setValue method is called with new value.
    Assert: LiveData becomes inactive and nothing is dispatched to either observer.
     */
    @Test
    public void setValue_twoLifecyclesAreCreatedNoEvent() {
        // Arrange.
        mLiveData.observe(mOwner3, mObserver3);
        mLiveData.observe(mOwner4, mObserver4);
        LifecycleEventObserver lifecycleObserver3 = getLiveDataInternalObserver(mLifecycle3);
        LifecycleEventObserver lifecycleObserver4 = getLiveDataInternalObserver(mLifecycle4);
        Mockito.when(mLifecycle3.getCurrentState()).thenReturn(STARTED);
        Mockito.when(mLifecycle4.getCurrentState()).thenReturn(STARTED);
        lifecycleObserver3.onStateChanged(mOwner3, ON_START);
        lifecycleObserver4.onStateChanged(mOwner4, ON_START);
        Mockito.when(mLifecycle3.getCurrentState()).thenReturn(CREATED);
        Mockito.when(mLifecycle4.getCurrentState()).thenReturn(CREATED);
        Mockito.reset(mActiveObserversChanged);
        Mockito.reset(mObserver3);
        Mockito.reset(mObserver4);
        // Act.
        mLiveData.setValue("1");
        // Assert.
        Mockito.verify(mActiveObserversChanged).onCall(false);
        Mockito.verify(mObserver3, Mockito.never()).onChanged(ArgumentMatchers.anyString());
        Mockito.verify(mObserver3, Mockito.never()).onChanged(ArgumentMatchers.anyString());
    }

    /* Arrange: LiveData was made inactive via SetValue (because both Lifecycles it was
    observing were in the CREATED state and no event was dispatched).
    Act: One Lifecycle enters STARTED state and dispatches lifecycle event.
    Assert: LiveData becomes active and dispatches new value to observer associated with started
    Lifecycle.
     */
    @Test
    public void test_liveDataInactiveViaSetValueThenOneLifecycleResumes() {
        // Arrange.
        mLiveData.observe(mOwner3, mObserver3);
        mLiveData.observe(mOwner4, mObserver4);
        LifecycleEventObserver lifecycleObserver3 = getLiveDataInternalObserver(mLifecycle3);
        LifecycleEventObserver lifecycleObserver4 = getLiveDataInternalObserver(mLifecycle4);
        Mockito.when(mLifecycle3.getCurrentState()).thenReturn(STARTED);
        Mockito.when(mLifecycle4.getCurrentState()).thenReturn(STARTED);
        lifecycleObserver3.onStateChanged(mOwner3, ON_START);
        lifecycleObserver4.onStateChanged(mOwner4, ON_START);
        Mockito.when(mLifecycle3.getCurrentState()).thenReturn(CREATED);
        Mockito.when(mLifecycle4.getCurrentState()).thenReturn(CREATED);
        mLiveData.setValue("1");
        Mockito.reset(mActiveObserversChanged);
        Mockito.reset(mObserver3);
        Mockito.reset(mObserver4);
        // Act.
        Mockito.when(mLifecycle3.getCurrentState()).thenReturn(STARTED);
        lifecycleObserver3.onStateChanged(mOwner3, ON_START);
        // Assert.
        Mockito.verify(mActiveObserversChanged).onCall(true);
        Mockito.verify(mObserver3).onChanged("1");
        Mockito.verify(mObserver4, Mockito.never()).onChanged(ArgumentMatchers.anyString());
    }

    @Test
    public void nestedForeverObserver() {
        mLiveData.setValue(".");
        mLiveData.observeForever(new Observer<String>() {
            @Override
            public void onChanged(@Nullable
            String s) {
                mLiveData.observeForever(Mockito.mock(Observer.class));
                mLiveData.removeObserver(this);
            }
        });
        Mockito.verify(mActiveObserversChanged, Mockito.only()).onCall(true);
    }

    @Test
    public void readdForeverObserver() {
        Observer observer = Mockito.mock(Observer.class);
        mLiveData.observeForever(observer);
        mLiveData.observeForever(observer);
        mLiveData.removeObserver(observer);
        MatcherAssert.assertThat(mLiveData.hasObservers(), CoreMatchers.is(false));
    }

    @Test
    public void initialValue() {
        MutableLiveData<String> mutableLiveData = new MutableLiveData("foo");
        Observer<String> observer = Mockito.mock(Observer.class);
        mRegistry.handleLifecycleEvent(Event.ON_START);
        mutableLiveData.observe(mOwner, observer);
        Mockito.verify(observer).onChanged("foo");
    }

    @SuppressWarnings("WeakerAccess")
    static class PublicLiveData<T> extends LiveData<T> {
        // cannot spy due to internal calls
        public LiveDataTest.MethodExec activeObserversChanged;

        @Override
        protected void onActive() {
            if ((activeObserversChanged) != null) {
                activeObserversChanged.onCall(true);
            }
        }

        @Override
        protected void onInactive() {
            if ((activeObserversChanged) != null) {
                activeObserversChanged.onCall(false);
            }
        }
    }

    private class FailReentranceObserver<T> implements Observer<T> {
        @Override
        public void onChanged(@Nullable
        T t) {
            MatcherAssert.assertThat(mInObserver, CoreMatchers.is(false));
        }
    }

    interface MethodExec {
        void onCall(boolean value);
    }
}

