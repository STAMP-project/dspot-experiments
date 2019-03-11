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


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;

import static Event.ON_ANY;
import static Event.ON_CREATE;
import static Event.ON_DESTROY;
import static Event.ON_PAUSE;
import static Event.ON_RESUME;
import static Event.ON_START;
import static Event.ON_STOP;


@RunWith(JUnit4.class)
public class ReflectiveGenericLifecycleObserverTest {
    private LifecycleOwner mOwner;

    private Lifecycle mLifecycle;

    @Test
    public void anyState() {
        ReflectiveGenericLifecycleObserverTest.AnyStateListener obj = Mockito.mock(ReflectiveGenericLifecycleObserverTest.AnyStateListener.class);
        ReflectiveGenericLifecycleObserver observer = new ReflectiveGenericLifecycleObserver(obj);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.STARTED);
        observer.onStateChanged(mOwner, Event.ON_CREATE);
        Mockito.verify(obj).onAnyState(mOwner, Event.ON_CREATE);
        Mockito.reset(obj);
        observer.onStateChanged(mOwner, Event.ON_START);
        Mockito.verify(obj).onAnyState(mOwner, Event.ON_START);
        Mockito.reset(obj);
        observer.onStateChanged(mOwner, Event.ON_RESUME);
        Mockito.verify(obj).onAnyState(mOwner, Event.ON_RESUME);
        Mockito.reset(obj);
        observer.onStateChanged(mOwner, Event.ON_PAUSE);
        Mockito.verify(obj).onAnyState(mOwner, Event.ON_PAUSE);
        Mockito.reset(obj);
        observer.onStateChanged(mOwner, Event.ON_STOP);
        Mockito.verify(obj).onAnyState(mOwner, Event.ON_STOP);
        Mockito.reset(obj);
        observer.onStateChanged(mOwner, Event.ON_DESTROY);
        Mockito.verify(obj).onAnyState(mOwner, Event.ON_DESTROY);
        Mockito.reset(obj);
    }

    private static class AnyStateListener implements LifecycleObserver {
        @OnLifecycleEvent(ON_ANY)
        void onAnyState(LifecycleOwner owner, Lifecycle.Event event) {
        }
    }

    @Test
    public void singleMethod() {
        ReflectiveGenericLifecycleObserverTest.CreatedStateListener obj = Mockito.mock(ReflectiveGenericLifecycleObserverTest.CreatedStateListener.class);
        ReflectiveGenericLifecycleObserver observer = new ReflectiveGenericLifecycleObserver(obj);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.CREATED);
        observer.onStateChanged(mOwner, Event.ON_CREATE);
        Mockito.verify(obj).onCreated();
        Mockito.verify(obj).onCreated(mOwner);
    }

    private static class CreatedStateListener implements LifecycleObserver {
        @OnLifecycleEvent(ON_CREATE)
        void onCreated() {
        }

        @SuppressWarnings("UnusedParameters")
        @OnLifecycleEvent(ON_CREATE)
        void onCreated(LifecycleOwner provider) {
        }
    }

    @Test
    public void eachEvent() {
        ReflectiveGenericLifecycleObserverTest.AllMethodsListener obj = Mockito.mock(ReflectiveGenericLifecycleObserverTest.AllMethodsListener.class);
        ReflectiveGenericLifecycleObserver observer = new ReflectiveGenericLifecycleObserver(obj);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.CREATED);
        observer.onStateChanged(mOwner, Event.ON_CREATE);
        Mockito.verify(obj).created();
        Mockito.reset(obj);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.STARTED);
        observer.onStateChanged(mOwner, Event.ON_START);
        Mockito.verify(obj).started();
        Mockito.reset(obj);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.RESUMED);
        observer.onStateChanged(mOwner, Event.ON_RESUME);
        Mockito.verify(obj).resumed();
        Mockito.reset(obj);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.STARTED);
        observer.onStateChanged(mOwner, Event.ON_PAUSE);
        Mockito.verify(obj).paused();
        Mockito.reset(obj);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.CREATED);
        observer.onStateChanged(mOwner, Event.ON_STOP);
        Mockito.verify(obj).stopped();
        Mockito.reset(obj);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.INITIALIZED);
        observer.onStateChanged(mOwner, Event.ON_DESTROY);
        Mockito.verify(obj).destroyed();
        Mockito.reset(obj);
    }

    private static class AllMethodsListener implements LifecycleObserver {
        @OnLifecycleEvent(Event.ON_CREATE)
        void created() {
        }

        @OnLifecycleEvent(ON_START)
        void started() {
        }

        @OnLifecycleEvent(ON_RESUME)
        void resumed() {
        }

        @OnLifecycleEvent(ON_PAUSE)
        void paused() {
        }

        @OnLifecycleEvent(ON_STOP)
        void stopped() {
        }

        @OnLifecycleEvent(ON_DESTROY)
        void destroyed() {
        }
    }

    @Test
    public void testFailingObserver() {
        class UnprecedentedError extends Error {}
        LifecycleObserver obj = new LifecycleObserver() {
            @OnLifecycleEvent(Event.ON_START)
            void started() {
                throw new UnprecedentedError();
            }
        };
        ReflectiveGenericLifecycleObserver observer = new ReflectiveGenericLifecycleObserver(obj);
        try {
            observer.onStateChanged(mOwner, Event.ON_START);
            Assert.fail();
        } catch (Exception e) {
            MatcherAssert.assertThat("exception cause is wrong", ((e.getCause()) instanceof UnprecedentedError));
        }
    }

    @Test
    public void testPrivateObserverMethods() {
        class ObserverWithPrivateMethod implements LifecycleObserver {
            boolean mCalled = false;

            @OnLifecycleEvent(Event.ON_START)
            private void started() {
                mCalled = true;
            }
        }
        ObserverWithPrivateMethod obj = Mockito.mock(ObserverWithPrivateMethod.class);
        ReflectiveGenericLifecycleObserver observer = new ReflectiveGenericLifecycleObserver(obj);
        observer.onStateChanged(mOwner, Event.ON_START);
        MatcherAssert.assertThat(obj.mCalled, Is.is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongFirstParam1() {
        LifecycleObserver observer = new LifecycleObserver() {
            @OnLifecycleEvent(Event.ON_START)
            private void started(Lifecycle.Event e) {
            }
        };
        new ReflectiveGenericLifecycleObserver(observer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongFirstParam2() {
        LifecycleObserver observer = new LifecycleObserver() {
            @OnLifecycleEvent(Event.ON_ANY)
            private void started(Lifecycle l, Lifecycle.Event e) {
            }
        };
        new ReflectiveGenericLifecycleObserver(observer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongSecondParam() {
        LifecycleObserver observer = new LifecycleObserver() {
            @OnLifecycleEvent(Event.ON_START)
            private void started(LifecycleOwner owner, Lifecycle l) {
            }
        };
        new ReflectiveGenericLifecycleObserver(observer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThreeParams() {
        LifecycleObserver observer = new LifecycleObserver() {
            @OnLifecycleEvent(Event.ON_ANY)
            private void started(LifecycleOwner owner, Lifecycle.Event e, int i) {
            }
        };
        new ReflectiveGenericLifecycleObserver(observer);
    }

    static class BaseClass1 implements LifecycleObserver {
        @OnLifecycleEvent(Event.ON_START)
        void foo(LifecycleOwner owner) {
        }
    }

    static class DerivedClass1 extends ReflectiveGenericLifecycleObserverTest.BaseClass1 {
        @Override
        @OnLifecycleEvent(Event.ON_STOP)
        void foo(LifecycleOwner owner) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSuper1() {
        new ReflectiveGenericLifecycleObserver(new ReflectiveGenericLifecycleObserverTest.DerivedClass1());
    }

    static class BaseClass2 implements LifecycleObserver {
        @OnLifecycleEvent(Event.ON_START)
        void foo(LifecycleOwner owner) {
        }
    }

    static class DerivedClass2 extends ReflectiveGenericLifecycleObserverTest.BaseClass1 {
        @OnLifecycleEvent(Event.ON_STOP)
        void foo() {
        }
    }

    @Test
    public void testValidSuper1() {
        ReflectiveGenericLifecycleObserverTest.DerivedClass2 obj = Mockito.mock(ReflectiveGenericLifecycleObserverTest.DerivedClass2.class);
        ReflectiveGenericLifecycleObserver observer = new ReflectiveGenericLifecycleObserver(obj);
        observer.onStateChanged(Mockito.mock(LifecycleOwner.class), Event.ON_START);
        Mockito.verify(obj).foo(Matchers.<LifecycleOwner>any());
        Mockito.verify(obj, Mockito.never()).foo();
        Mockito.reset(obj);
        observer.onStateChanged(Mockito.mock(LifecycleOwner.class), Event.ON_STOP);
        Mockito.verify(obj).foo();
        Mockito.verify(obj, Mockito.never()).foo(Matchers.<LifecycleOwner>any());
    }

    static class BaseClass3 implements LifecycleObserver {
        @OnLifecycleEvent(Event.ON_START)
        void foo(LifecycleOwner owner) {
        }
    }

    interface Interface3 extends LifecycleObserver {
        @OnLifecycleEvent(Event.ON_STOP)
        void foo(LifecycleOwner owner);
    }

    static class DerivedClass3 extends ReflectiveGenericLifecycleObserverTest.BaseClass3 implements ReflectiveGenericLifecycleObserverTest.Interface3 {
        @Override
        public void foo(LifecycleOwner owner) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSuper2() {
        new ReflectiveGenericLifecycleObserver(new ReflectiveGenericLifecycleObserverTest.DerivedClass3());
    }

    static class BaseClass4 implements LifecycleObserver {
        @OnLifecycleEvent(Event.ON_START)
        void foo(LifecycleOwner owner) {
        }
    }

    interface Interface4 extends LifecycleObserver {
        @OnLifecycleEvent(Event.ON_START)
        void foo(LifecycleOwner owner);
    }

    static class DerivedClass4 extends ReflectiveGenericLifecycleObserverTest.BaseClass4 implements ReflectiveGenericLifecycleObserverTest.Interface4 {
        @Override
        @OnLifecycleEvent(Event.ON_START)
        public void foo(LifecycleOwner owner) {
        }

        @OnLifecycleEvent(Event.ON_START)
        public void foo() {
        }
    }

    @Test
    public void testValidSuper2() {
        ReflectiveGenericLifecycleObserverTest.DerivedClass4 obj = Mockito.mock(ReflectiveGenericLifecycleObserverTest.DerivedClass4.class);
        ReflectiveGenericLifecycleObserver observer = new ReflectiveGenericLifecycleObserver(obj);
        observer.onStateChanged(Mockito.mock(LifecycleOwner.class), Event.ON_START);
        Mockito.verify(obj).foo(Matchers.<LifecycleOwner>any());
        Mockito.verify(obj).foo();
    }

    interface InterfaceStart extends LifecycleObserver {
        @OnLifecycleEvent(Event.ON_START)
        void foo(LifecycleOwner owner);
    }

    interface InterfaceStop extends LifecycleObserver {
        @OnLifecycleEvent(Event.ON_STOP)
        void foo(LifecycleOwner owner);
    }

    static class DerivedClass5 implements ReflectiveGenericLifecycleObserverTest.InterfaceStart , ReflectiveGenericLifecycleObserverTest.InterfaceStop {
        @Override
        public void foo(LifecycleOwner owner) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSuper3() {
        new ReflectiveGenericLifecycleObserver(new ReflectiveGenericLifecycleObserverTest.DerivedClass5());
    }
}

