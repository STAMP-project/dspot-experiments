package com.bumptech.glide.manager;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class LifecycleTest {
    private ActivityFragmentLifecycle lifecycle;

    private LifecycleListener listener;

    @Test
    public void testNotifiesAddedListenerOnStart() {
        lifecycle.addListener(listener);
        lifecycle.onStart();
        Mockito.verify(listener).onStart();
    }

    @Test
    public void testNotifiesAddedListenerOfStartIfStarted() {
        lifecycle.onStart();
        lifecycle.addListener(listener);
        Mockito.verify(listener).onStart();
    }

    @Test
    public void testDoesNotNotifyAddedListenerOfStartIfDestroyed() {
        lifecycle.onStart();
        lifecycle.onStop();
        lifecycle.onDestroy();
        lifecycle.addListener(listener);
        Mockito.verify(listener, Mockito.never()).onStart();
    }

    @Test
    public void testDoesNotNotifyListenerOfStartIfStartedThenStopped() {
        lifecycle.onStart();
        lifecycle.onStop();
        lifecycle.addListener(listener);
        Mockito.verify(listener, Mockito.never()).onStart();
    }

    @Test
    public void testNotifiesAddedListenerOnStop() {
        lifecycle.onStart();
        lifecycle.addListener(listener);
        lifecycle.onStop();
        Mockito.verify(listener).onStop();
    }

    @Test
    public void testNotifiesAddedListenerOfStopIfStopped() {
        lifecycle.onStop();
        lifecycle.addListener(listener);
        Mockito.verify(listener).onStop();
    }

    @Test
    public void testDoesNotNotifyAddedListenerOfStopIfDestroyed() {
        lifecycle.onStart();
        lifecycle.onStop();
        lifecycle.onDestroy();
        lifecycle.addListener(listener);
        Mockito.verify(listener, Mockito.never()).onStop();
    }

    @Test
    public void testDoesNotNotifyListenerOfStopIfStoppedThenStarted() {
        lifecycle.onStop();
        lifecycle.onStart();
        lifecycle.addListener(listener);
        Mockito.verify(listener, Mockito.never()).onStop();
    }

    @Test
    public void testNotifiesAddedListenerOnDestroy() {
        lifecycle.addListener(listener);
        lifecycle.onDestroy();
        Mockito.verify(listener).onDestroy();
    }

    @Test
    public void testNotifiesAddedListenerOfDestroyIfDestroyed() {
        lifecycle.onDestroy();
        lifecycle.addListener(listener);
        Mockito.verify(listener).onDestroy();
    }

    @Test
    public void testNotifiesMultipleListeners() {
        lifecycle.onStart();
        int toNotify = 20;
        List<LifecycleListener> listeners = new ArrayList<>();
        for (int i = 0; i < toNotify; i++) {
            listeners.add(Mockito.mock(LifecycleListener.class));
        }
        for (LifecycleListener lifecycleListener : listeners) {
            lifecycle.addListener(lifecycleListener);
        }
        lifecycle.onStop();
        lifecycle.onDestroy();
        for (LifecycleListener lifecycleListener : listeners) {
            Mockito.verify(lifecycleListener).onStart();
            Mockito.verify(lifecycleListener).onStop();
            Mockito.verify(lifecycleListener).onDestroy();
        }
    }
}

