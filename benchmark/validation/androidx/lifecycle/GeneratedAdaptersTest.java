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
import Lifecycle.Event.ON_PAUSE;
import Lifecycle.Event.ON_RESUME;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static Event.ON_ANY;


@RunWith(JUnit4.class)
public class GeneratedAdaptersTest {
    private LifecycleOwner mOwner;

    @SuppressWarnings("FieldCanBeLocal")
    private Lifecycle mLifecycle;

    static class SimpleObserver implements LifecycleObserver {
        List<String> mLog;

        SimpleObserver(List<String> log) {
            mLog = log;
        }

        @OnLifecycleEvent(Event.ON_CREATE)
        void onCreate() {
            mLog.add("onCreate");
        }
    }

    @Test
    public void testSimpleSingleGeneratedAdapter() {
        List<String> actual = new ArrayList<>();
        LifecycleEventObserver callback = Lifecycling.lifecycleEventObserver(new GeneratedAdaptersTest.SimpleObserver(actual));
        callback.onStateChanged(mOwner, ON_CREATE);
        MatcherAssert.assertThat(callback, CoreMatchers.instanceOf(SingleGeneratedAdapterObserver.class));
        MatcherAssert.assertThat(actual, CoreMatchers.is(Collections.singletonList("onCreate")));
    }

    static class TestObserver implements LifecycleObserver {
        List<String> mLog;

        TestObserver(List<String> log) {
            mLog = log;
        }

        @OnLifecycleEvent(Event.ON_CREATE)
        void onCreate() {
            mLog.add("onCreate");
        }

        @OnLifecycleEvent(ON_ANY)
        void onAny() {
            mLog.add("onAny");
        }
    }

    @Test
    public void testOnAny() {
        List<String> actual = new ArrayList<>();
        LifecycleEventObserver callback = Lifecycling.lifecycleEventObserver(new GeneratedAdaptersTest.TestObserver(actual));
        callback.onStateChanged(mOwner, ON_CREATE);
        MatcherAssert.assertThat(callback, CoreMatchers.instanceOf(SingleGeneratedAdapterObserver.class));
        MatcherAssert.assertThat(actual, CoreMatchers.is(Arrays.asList("onCreate", "onAny")));
    }

    interface OnPauses extends LifecycleObserver {
        @OnLifecycleEvent(Event.ON_PAUSE)
        void onPause();

        @OnLifecycleEvent(Event.ON_PAUSE)
        void onPause(LifecycleOwner owner);
    }

    interface OnPauseResume extends LifecycleObserver {
        @OnLifecycleEvent(Event.ON_PAUSE)
        void onPause();

        @OnLifecycleEvent(Event.ON_RESUME)
        void onResume();
    }

    class Impl1 implements GeneratedAdaptersTest.OnPauseResume , GeneratedAdaptersTest.OnPauses {
        List<String> mLog;

        Impl1(List<String> log) {
            mLog = log;
        }

        @Override
        public void onPause() {
            mLog.add("onPause_0");
        }

        @Override
        public void onResume() {
            mLog.add("onResume");
        }

        @Override
        public void onPause(LifecycleOwner owner) {
            mLog.add("onPause_1");
        }
    }

    @Test
    public void testClashingInterfaces() {
        List<String> actual = new ArrayList<>();
        LifecycleEventObserver callback = Lifecycling.lifecycleEventObserver(new GeneratedAdaptersTest.Impl1(actual));
        callback.onStateChanged(mOwner, ON_PAUSE);
        MatcherAssert.assertThat(callback, CoreMatchers.instanceOf(CompositeGeneratedAdaptersObserver.class));
        MatcherAssert.assertThat(actual, CoreMatchers.is(Arrays.asList("onPause_0", "onPause_1")));
        actual.clear();
        callback.onStateChanged(mOwner, ON_RESUME);
        MatcherAssert.assertThat(actual, CoreMatchers.is(Collections.singletonList("onResume")));
    }

    class Base implements LifecycleObserver {
        List<String> mLog;

        Base(List<String> log) {
            mLog = log;
        }

        @OnLifecycleEvent(Event.ON_ANY)
        void onAny() {
            mLog.add("onAny_0");
        }

        @OnLifecycleEvent(Event.ON_ANY)
        void onAny(LifecycleOwner owner) {
            mLog.add("onAny_1");
        }

        @OnLifecycleEvent(Event.ON_RESUME)
        void onResume() {
            mLog.add("onResume");
        }
    }

    interface OnAny extends LifecycleObserver {
        @OnLifecycleEvent(Event.ON_ANY)
        void onAny();

        @OnLifecycleEvent(Event.ON_ANY)
        void onAny(LifecycleOwner owner, Lifecycle.Event event);
    }

    class Derived extends GeneratedAdaptersTest.Base implements GeneratedAdaptersTest.OnAny {
        Derived(List<String> log) {
            super(log);
        }

        @Override
        public void onAny() {
            super.onAny();
        }

        @Override
        public void onAny(LifecycleOwner owner, Lifecycle.Event event) {
            mLog.add("onAny_2");
            MatcherAssert.assertThat(event, CoreMatchers.is(Event.ON_RESUME));
        }
    }

    @Test
    public void testClashingClassAndInterface() {
        List<String> actual = new ArrayList<>();
        LifecycleEventObserver callback = Lifecycling.lifecycleEventObserver(new GeneratedAdaptersTest.Derived(actual));
        callback.onStateChanged(mOwner, ON_RESUME);
        MatcherAssert.assertThat(callback, CoreMatchers.instanceOf(CompositeGeneratedAdaptersObserver.class));
        MatcherAssert.assertThat(actual, CoreMatchers.is(Arrays.asList("onResume", "onAny_0", "onAny_1", "onAny_2")));
    }
}

