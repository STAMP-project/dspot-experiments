package com.reactnativenavigation.utils;


import com.reactnativenavigation.BaseTest;
import org.junit.Test;
import org.mockito.Mockito;


public class UiUtilsTest extends BaseTest {
    @Test
    public void runOnPreDrawOnce() {
        View view = new View(newActivity());
        Runnable task = Mockito.mock(Runnable.class);
        Mockito.verifyZeroInteractions(task);
        UiUtils.runOnPreDrawOnce(view, task);
        dispatchPreDraw(view);
        Mockito.verify(task, Mockito.times(1)).run();
    }
}

