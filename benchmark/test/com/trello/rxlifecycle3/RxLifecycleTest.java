package com.trello.rxlifecycle3;


import Lifecycle.Event;
import Lifecycle.Event.ON_CREATE;
import Lifecycle.Event.ON_DESTROY;
import Lifecycle.Event.ON_PAUSE;
import Lifecycle.Event.ON_RESUME;
import Lifecycle.Event.ON_START;
import Lifecycle.Event.ON_STOP;
import androidx.lifecycle.Lifecycle;
import com.trello.lifecycle2.android.lifecycle.RxLifecycleAndroidLifecycle;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.BehaviorSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RxLifecycleTest {
    private Observable<Object> observable;

    @Test
    public void testBindUntilLifecycleEvent() {
        BehaviorSubject<Lifecycle.Event> lifecycle = BehaviorSubject.create();
        TestObserver<Object> testObserver = observable.compose(RxLifecycle.bindUntilEvent(lifecycle, ON_STOP)).test();
        lifecycle.onNext(ON_CREATE);
        testObserver.assertNotComplete();
        lifecycle.onNext(ON_START);
        testObserver.assertNotComplete();
        lifecycle.onNext(ON_RESUME);
        testObserver.assertNotComplete();
        lifecycle.onNext(ON_PAUSE);
        testObserver.assertNotComplete();
        lifecycle.onNext(ON_STOP);
        testObserver.assertComplete();
    }

    @Test
    public void testBindLifecycle() {
        BehaviorSubject<Lifecycle.Event> lifecycle = BehaviorSubject.create();
        lifecycle.onNext(ON_CREATE);
        TestObserver<Object> createObserver = observable.compose(RxLifecycleAndroidLifecycle.bindLifecycle(lifecycle)).test();
        lifecycle.onNext(ON_START);
        createObserver.assertNotComplete();
        TestObserver<Object> startObserver = observable.compose(RxLifecycleAndroidLifecycle.bindLifecycle(lifecycle)).test();
        lifecycle.onNext(ON_RESUME);
        createObserver.assertNotComplete();
        startObserver.assertNotComplete();
        TestObserver<Object> resumeObserver = observable.compose(RxLifecycleAndroidLifecycle.bindLifecycle(lifecycle)).test();
        lifecycle.onNext(ON_PAUSE);
        createObserver.assertNotComplete();
        startObserver.assertNotComplete();
        resumeObserver.assertComplete();
        TestObserver<Object> pauseObserver = observable.compose(RxLifecycleAndroidLifecycle.bindLifecycle(lifecycle)).test();
        lifecycle.onNext(ON_STOP);
        createObserver.assertNotComplete();
        startObserver.assertComplete();
        pauseObserver.assertComplete();
        TestObserver<Object> stopObserver = observable.compose(RxLifecycleAndroidLifecycle.bindLifecycle(lifecycle)).test();
        lifecycle.onNext(ON_DESTROY);
        createObserver.assertComplete();
        stopObserver.assertComplete();
    }

    @Test
    public void testEndsImmediatelyOutsideLifecycle() {
        BehaviorSubject<Lifecycle.Event> lifecycle = BehaviorSubject.create();
        lifecycle.onNext(ON_DESTROY);
        TestObserver<Object> testObserver = observable.compose(RxLifecycleAndroidLifecycle.bindLifecycle(lifecycle)).test();
        testObserver.assertComplete();
    }

    // Null checks
    @Test(expected = NullPointerException.class)
    public void testBindLifecycleThrowsOnNull() {
        // noinspection ResourceType
        RxLifecycleAndroidLifecycle.bindLifecycle(null);
    }
}

