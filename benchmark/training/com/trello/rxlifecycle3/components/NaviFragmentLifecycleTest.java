/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.trello.rxlifecycle3.components;


import FragmentEvent.ATTACH;
import FragmentEvent.CREATE;
import FragmentEvent.CREATE_VIEW;
import FragmentEvent.DESTROY;
import FragmentEvent.DESTROY_VIEW;
import FragmentEvent.DETACH;
import FragmentEvent.PAUSE;
import FragmentEvent.RESUME;
import FragmentEvent.START;
import FragmentEvent.STOP;
import android.os.Bundle;
import com.trello.navi2.Event;
import com.trello.navi2.NaviComponent;
import com.trello.navi2.internal.NaviEmitter;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.android.FragmentEvent;
import com.trello.rxlifecycle3.navi.NaviLifecycle;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;


public class NaviFragmentLifecycleTest {
    @Test
    public void testLifecycle() {
        NaviEmitter fragment = NaviEmitter.createFragmentEmitter();
        LifecycleProvider<FragmentEvent> provider = NaviLifecycle.createFragmentLifecycleProvider(fragment);
        TestObserver<FragmentEvent> testObserver = provider.lifecycle().test();
        fragment.onAttach(null);
        fragment.onCreate(null);
        fragment.onCreateView(null);
        fragment.onStart();
        fragment.onResume();
        fragment.onPause();
        fragment.onStop();
        fragment.onDestroyView();
        fragment.onDestroy();
        fragment.onDetach();
        testObserver.assertValues(ATTACH, CREATE, CREATE_VIEW, START, RESUME, PAUSE, STOP, DESTROY_VIEW, DESTROY, DETACH);
    }

    @Test
    public void testNonLifecycleEvents() {
        NaviEmitter fragment = NaviEmitter.createFragmentEmitter();
        LifecycleProvider<FragmentEvent> provider = NaviLifecycle.createFragmentLifecycleProvider(fragment);
        TestObserver<FragmentEvent> testObserver = provider.lifecycle().test();
        fragment.onRestoreInstanceState(new Bundle());
        testObserver.assertNoValues();
        testObserver.assertNoErrors();
    }

    @Test
    public void testBindUntilEvent() {
        NaviEmitter fragment = createFragmentEmitter();
        LifecycleProvider<FragmentEvent> provider = createFragmentLifecycleProvider(fragment);
        Observable<Object> observable = PublishSubject.create().hide();
        TestObserver<Object> testObserver = observable.compose(provider.bindUntilEvent(STOP)).test();
        fragment.onAttach(null);
        testObserver.assertNotComplete();
        fragment.onCreate(null);
        testObserver.assertNotComplete();
        fragment.onCreate(null);
        testObserver.assertNotComplete();
        fragment.onStart();
        testObserver.assertNotComplete();
        fragment.onResume();
        testObserver.assertNotComplete();
        fragment.onPause();
        testObserver.assertNotComplete();
        fragment.onStop();
        testObserver.assertComplete();
    }

    @Test
    public void testBindToLifecycle() {
        NaviEmitter fragment = createFragmentEmitter();
        LifecycleProvider<FragmentEvent> provider = createFragmentLifecycleProvider(fragment);
        Observable<Object> observable = PublishSubject.create().hide();
        fragment.onAttach(null);
        TestObserver<Object> attachObserver = observable.compose(provider.bindToLifecycle()).test();
        fragment.onCreate(null);
        attachObserver.assertNotComplete();
        TestObserver<Object> createObserver = observable.compose(provider.bindToLifecycle()).test();
        fragment.onCreateView(null);
        attachObserver.assertNotComplete();
        createObserver.assertNotComplete();
        TestObserver<Object> createViewObserver = observable.compose(provider.bindToLifecycle()).test();
        fragment.onStart();
        attachObserver.assertNotComplete();
        createObserver.assertNotComplete();
        createViewObserver.assertNotComplete();
        TestObserver<Object> startObserver = observable.compose(provider.bindToLifecycle()).test();
        fragment.onResume();
        attachObserver.assertNotComplete();
        createObserver.assertNotComplete();
        createViewObserver.assertNotComplete();
        startObserver.assertNotComplete();
        TestObserver<Object> resumeObserver = observable.compose(provider.bindToLifecycle()).test();
        fragment.onPause();
        attachObserver.assertNotComplete();
        createObserver.assertNotComplete();
        createViewObserver.assertNotComplete();
        startObserver.assertNotComplete();
        resumeObserver.assertComplete();
        TestObserver<Object> pauseObserver = observable.compose(provider.bindToLifecycle()).test();
        fragment.onStop();
        attachObserver.assertNotComplete();
        createObserver.assertNotComplete();
        createViewObserver.assertNotComplete();
        startObserver.assertComplete();
        pauseObserver.assertComplete();
        TestObserver<Object> stopObserver = observable.compose(provider.bindToLifecycle()).test();
        fragment.onDestroyView();
        attachObserver.assertNotComplete();
        createObserver.assertNotComplete();
        createViewObserver.assertComplete();
        stopObserver.assertComplete();
        TestObserver<Object> destroyViewObserver = observable.compose(provider.bindToLifecycle()).test();
        fragment.onDestroy();
        attachObserver.assertNotComplete();
        createObserver.assertComplete();
        destroyViewObserver.assertComplete();
        TestObserver<Object> destroyObserver = observable.compose(provider.bindToLifecycle()).test();
        fragment.onDetach();
        attachObserver.assertComplete();
        destroyObserver.assertComplete();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadHandler() {
        HashSet<Event<?>> notEnoughEvents = new HashSet<>();
        notEnoughEvents.add(Event.ATTACH);
        NaviComponent badHandler = new NaviEmitter(notEnoughEvents);
        // noinspection CheckResult
        NaviLifecycle.createFragmentLifecycleProvider(badHandler);
    }

    @Test
    public void testPersistance() {
        NaviEmitter fragment = NaviEmitter.createFragmentEmitter();
        LifecycleProvider<FragmentEvent> provider = NaviLifecycle.createFragmentLifecycleProvider(fragment);
        TestObserver<FragmentEvent> testObserver = provider.lifecycle().test();
        fragment.onAttach(null);
        fragment.onCreate(null);
        fragment.onCreateView(null);
        fragment.onStart();
        fragment.onResume();
        fragment.onPause();
        fragment.onStop();
        fragment.onDestroyView();
        fragment.onDestroy();
        fragment.onDetach();
        // Verify that you can remain subscribed until the Fragment is completely gone
        fragment.onAttach(null);
        testObserver.assertValues(ATTACH, CREATE, CREATE_VIEW, START, RESUME, PAUSE, STOP, DESTROY_VIEW, DESTROY, DETACH, ATTACH);
    }

    @Test
    public void testLeakFree() {
        NaviEmitter fragment = NaviEmitter.createFragmentEmitter();
        LifecycleProvider<FragmentEvent> provider = NaviLifecycle.createFragmentLifecycleProvider(fragment);
        WeakReference<NaviEmitter> fragmentRef = new WeakReference(fragment);
        WeakReference<LifecycleProvider<FragmentEvent>> providerRef = new WeakReference(provider);
        Observable<Object> observable = PublishSubject.create().hide();
        TestObserver<Object> testObserver = observable.compose(provider.bindUntilEvent(STOP)).test();
        fragment.onAttach(null);
        fragment.onCreate(null);
        fragment.onCreateView(null);
        fragment.onStart();
        fragment.onResume();
        fragment.onPause();
        fragment.onStop();
        fragment.onDestroyView();
        fragment.onDestroy();
        fragment.onDetach();
        fragment = null;
        provider = null;
        TestUtil.cleanGarbage();
        Assert.assertNull(fragmentRef.get());
        Assert.assertNull(providerRef.get());
    }
}

