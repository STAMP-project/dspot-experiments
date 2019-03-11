package com.reactnativenavigation.utils;


import com.reactnativenavigation.BaseTest;
import org.junit.Test;
import org.mockito.Mockito;


public class UiThreadTest extends BaseTest {
    @Test
    public void postOnUiThread() throws Exception {
        Runnable task = Mockito.mock(Runnable.class);
        ShadowLooper.pauseMainLooper();
        UiThread.post(task);
        Mockito.verifyZeroInteractions(task);
        ShadowLooper.runUiThreadTasks();
        Mockito.verify(task, Mockito.times(1)).run();
    }

    @Test
    public void postDelayedOnUiThread() throws Exception {
        Runnable task = Mockito.mock(Runnable.class);
        UiThread.postDelayed(task, 1000);
        Mockito.verifyZeroInteractions(task);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        Mockito.verify(task, Mockito.times(1)).run();
    }
}

