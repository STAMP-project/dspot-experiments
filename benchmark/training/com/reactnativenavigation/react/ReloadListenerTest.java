package com.reactnativenavigation.react;


import com.reactnativenavigation.BaseTest;
import org.junit.Test;
import org.mockito.Mockito;


public class ReloadListenerTest extends BaseTest {
    private ReloadHandler uut;

    private Runnable handler;

    @Test
    public void onSuccess_viewsAreDestroyed() {
        uut.setOnReloadListener(handler);
        uut.onSuccess();
        Mockito.verify(handler).run();
    }
}

