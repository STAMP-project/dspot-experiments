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


import Lifecycle.Event.ON_CREATE;
import Lifecycle.Event.ON_DESTROY;
import Lifecycle.Event.ON_PAUSE;
import Lifecycle.Event.ON_RESUME;
import Lifecycle.Event.ON_START;
import Lifecycle.Event.ON_STOP;
import androidx.annotation.NonNull;
import androidx.lifecycle.observers.DerivedSequence1;
import androidx.lifecycle.observers.DerivedSequence2;
import androidx.lifecycle.observers.DerivedWithNewMethods;
import androidx.lifecycle.observers.DerivedWithNoNewMethods;
import androidx.lifecycle.observers.DerivedWithOverridenMethodsWithLfAnnotation;
import androidx.lifecycle.observers.InterfaceImpl1;
import androidx.lifecycle.observers.InterfaceImpl2;
import androidx.lifecycle.observers.InterfaceImpl3;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static Event.ON_ANY;


@RunWith(JUnit4.class)
public class LifecyclingTest {
    @Test
    public void testDerivedWithNewLfMethodsNoGeneratedAdapter() {
        LifecycleEventObserver callback = Lifecycling.lifecycleEventObserver(new DerivedWithNewMethods());
        MatcherAssert.assertThat(callback, CoreMatchers.instanceOf(ReflectiveGenericLifecycleObserver.class));
    }

    @Test
    public void testDerivedWithNoNewLfMethodsNoGeneratedAdapter() {
        LifecycleEventObserver callback = Lifecycling.lifecycleEventObserver(new DerivedWithNoNewMethods());
        MatcherAssert.assertThat(callback, CoreMatchers.instanceOf(SingleGeneratedAdapterObserver.class));
    }

    @Test
    public void testDerivedWithOverridenMethodsNoGeneratedAdapter() {
        LifecycleEventObserver callback = Lifecycling.lifecycleEventObserver(new DerivedWithOverridenMethodsWithLfAnnotation());
        // that is not effective but...
        MatcherAssert.assertThat(callback, CoreMatchers.instanceOf(ReflectiveGenericLifecycleObserver.class));
    }

    @Test
    public void testInterfaceImpl1NoGeneratedAdapter() {
        LifecycleEventObserver callback = Lifecycling.lifecycleEventObserver(new InterfaceImpl1());
        MatcherAssert.assertThat(callback, CoreMatchers.instanceOf(SingleGeneratedAdapterObserver.class));
    }

    @Test
    public void testInterfaceImpl2NoGeneratedAdapter() {
        LifecycleEventObserver callback = Lifecycling.lifecycleEventObserver(new InterfaceImpl2());
        MatcherAssert.assertThat(callback, CoreMatchers.instanceOf(CompositeGeneratedAdaptersObserver.class));
    }

    @Test
    public void testInterfaceImpl3NoGeneratedAdapter() {
        LifecycleEventObserver callback = Lifecycling.lifecycleEventObserver(new InterfaceImpl3());
        MatcherAssert.assertThat(callback, CoreMatchers.instanceOf(CompositeGeneratedAdaptersObserver.class));
    }

    @Test
    public void testDerivedSequence() {
        LifecycleEventObserver callback2 = Lifecycling.lifecycleEventObserver(new DerivedSequence2());
        MatcherAssert.assertThat(callback2, CoreMatchers.instanceOf(ReflectiveGenericLifecycleObserver.class));
        LifecycleEventObserver callback1 = Lifecycling.lifecycleEventObserver(new DerivedSequence1());
        MatcherAssert.assertThat(callback1, CoreMatchers.instanceOf(SingleGeneratedAdapterObserver.class));
    }

    // MUST BE HERE TILL Lifecycle 3.0.0 release for back-compatibility with other modules
    @Test
    public void testDeprecatedGenericLifecycleObserver() {
        GenericLifecycleObserver genericLifecycleObserver = new GenericLifecycleObserver() {
            @Override
            public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
            }
        };
        LifecycleEventObserver observer = Lifecycling.lifecycleEventObserver(genericLifecycleObserver);
        MatcherAssert.assertThat(observer, CoreMatchers.is(observer));
    }

    // MUST BE HERE TILL Lifecycle 3.0.0 release for back-compatibility with other modules
    @Test
    public void testDeprecatedLifecyclingCallback() {
        GenericLifecycleObserver genericLifecycleObserver = new GenericLifecycleObserver() {
            @Override
            public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
            }
        };
        LifecycleEventObserver observer = Lifecycling.getCallback(genericLifecycleObserver);
        MatcherAssert.assertThat(observer, CoreMatchers.is(observer));
    }

    @Test
    public void fullLifecycleObserverAndAnnotations() {
        class AnnotatedFullLifecycleObserver implements FullLifecycleObserver {
            @OnLifecycleEvent(ON_ANY)
            public void onAny() {
                throw new IllegalStateException(("Annotations in FullLifecycleObserver " + "must not be called"));
            }

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
        }
        LifecycleEventObserver callback = Lifecycling.lifecycleEventObserver(new AnnotatedFullLifecycleObserver());
        // check that neither of these calls fail
        callback.onStateChanged(new LifecyclingTest.DefaultLifecycleOwner(), ON_CREATE);
        callback.onStateChanged(new LifecyclingTest.DefaultLifecycleOwner(), ON_START);
        callback.onStateChanged(new LifecyclingTest.DefaultLifecycleOwner(), ON_RESUME);
        callback.onStateChanged(new LifecyclingTest.DefaultLifecycleOwner(), ON_PAUSE);
        callback.onStateChanged(new LifecyclingTest.DefaultLifecycleOwner(), ON_STOP);
        callback.onStateChanged(new LifecyclingTest.DefaultLifecycleOwner(), ON_DESTROY);
    }

    @Test
    public void lifecycleEventObserverAndAnnotations() {
        class AnnotatedLifecycleEventObserver implements LifecycleEventObserver {
            @OnLifecycleEvent(Event.ON_ANY)
            public void onAny() {
                throw new IllegalStateException(("Annotations in FullLifecycleObserver " + "must not be called"));
            }

            @Override
            public void onStateChanged(@NonNull
            LifecycleOwner source, @NonNull
            Lifecycle.Event event) {
            }
        }
        LifecycleEventObserver callback = Lifecycling.lifecycleEventObserver(new AnnotatedLifecycleEventObserver());
        // check that neither of these calls fail
        callback.onStateChanged(new LifecyclingTest.DefaultLifecycleOwner(), ON_CREATE);
        callback.onStateChanged(new LifecyclingTest.DefaultLifecycleOwner(), ON_START);
        callback.onStateChanged(new LifecyclingTest.DefaultLifecycleOwner(), ON_RESUME);
        callback.onStateChanged(new LifecyclingTest.DefaultLifecycleOwner(), ON_PAUSE);
        callback.onStateChanged(new LifecyclingTest.DefaultLifecycleOwner(), ON_STOP);
        callback.onStateChanged(new LifecyclingTest.DefaultLifecycleOwner(), ON_DESTROY);
    }

    static class DefaultLifecycleOwner implements LifecycleOwner {
        @NonNull
        @Override
        public Lifecycle getLifecycle() {
            throw new UnsupportedOperationException("getLifecycle is not supported");
        }
    }
}

