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


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class FullLifecycleObserverTest {
    private LifecycleOwner mOwner;

    private Lifecycle mLifecycle;

    @Test
    public void eachEvent() {
        FullLifecycleObserver obj = Mockito.mock(FullLifecycleObserver.class);
        FullLifecycleObserverAdapter observer = new FullLifecycleObserverAdapter(obj, null);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.CREATED);
        observer.onStateChanged(mOwner, Event.ON_CREATE);
        InOrder inOrder = Mockito.inOrder(obj);
        inOrder.verify(obj).onCreate(mOwner);
        Mockito.reset(obj);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.STARTED);
        observer.onStateChanged(mOwner, Event.ON_START);
        inOrder.verify(obj).onStart(mOwner);
        Mockito.reset(obj);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.RESUMED);
        observer.onStateChanged(mOwner, Event.ON_RESUME);
        inOrder.verify(obj).onResume(mOwner);
        Mockito.reset(obj);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.STARTED);
        observer.onStateChanged(mOwner, Event.ON_PAUSE);
        inOrder.verify(obj).onPause(mOwner);
        Mockito.reset(obj);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.CREATED);
        observer.onStateChanged(mOwner, Event.ON_STOP);
        inOrder.verify(obj).onStop(mOwner);
        Mockito.reset(obj);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.INITIALIZED);
        observer.onStateChanged(mOwner, Event.ON_DESTROY);
        inOrder.verify(obj).onDestroy(mOwner);
        Mockito.reset(obj);
    }

    @Test
    public void fullLifecycleObserverAndLifecycleEventObserver() {
        class AllObservers implements FullLifecycleObserver , LifecycleEventObserver {
            @Override
            public void onCreate(LifecycleOwner owner) {
            }

            @Override
            public void onStart(LifecycleOwner owner) {
            }

            @Override
            public void onResume(LifecycleOwner owner) {
            }

            @Override
            public void onPause(LifecycleOwner owner) {
            }

            @Override
            public void onStop(LifecycleOwner owner) {
            }

            @Override
            public void onDestroy(LifecycleOwner owner) {
            }

            @Override
            public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
            }
        }
        AllObservers obj = Mockito.mock(AllObservers.class);
        FullLifecycleObserverAdapter observer = new FullLifecycleObserverAdapter(obj, obj);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.CREATED);
        observer.onStateChanged(mOwner, Event.ON_CREATE);
        InOrder inOrder = Mockito.inOrder(obj);
        inOrder.verify(obj).onCreate(mOwner);
        inOrder.verify(obj).onStateChanged(mOwner, Event.ON_CREATE);
        Mockito.reset(obj);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.STARTED);
        observer.onStateChanged(mOwner, Event.ON_START);
        inOrder.verify(obj).onStart(mOwner);
        inOrder.verify(obj).onStateChanged(mOwner, Event.ON_START);
        Mockito.reset(obj);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.RESUMED);
        observer.onStateChanged(mOwner, Event.ON_RESUME);
        inOrder.verify(obj).onResume(mOwner);
        inOrder.verify(obj).onStateChanged(mOwner, Event.ON_RESUME);
        Mockito.reset(obj);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.STARTED);
        observer.onStateChanged(mOwner, Event.ON_PAUSE);
        inOrder.verify(obj).onPause(mOwner);
        inOrder.verify(obj).onStateChanged(mOwner, Event.ON_PAUSE);
        Mockito.reset(obj);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.CREATED);
        observer.onStateChanged(mOwner, Event.ON_STOP);
        inOrder.verify(obj).onStop(mOwner);
        inOrder.verify(obj).onStateChanged(mOwner, Event.ON_STOP);
        Mockito.reset(obj);
        Mockito.when(mLifecycle.getCurrentState()).thenReturn(State.INITIALIZED);
        observer.onStateChanged(mOwner, Event.ON_DESTROY);
        inOrder.verify(obj).onDestroy(mOwner);
        inOrder.verify(obj).onStateChanged(mOwner, Event.ON_DESTROY);
        Mockito.reset(obj);
    }
}

