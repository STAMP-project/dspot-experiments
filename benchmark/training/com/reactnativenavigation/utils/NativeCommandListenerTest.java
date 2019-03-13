package com.reactnativenavigation.utils;


import com.facebook.react.bridge.Promise;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.react.EventEmitter;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class NativeCommandListenerTest extends BaseTest {
    private static final String COMMAND_ID = "someCommand";

    private static final String CHILD_ID = "someChild";

    private static final long NOW = 1535374334;

    private EventEmitter eventEmitter;

    private Promise promise;

    private NativeCommandListener uut;

    @Test
    public void onSuccess() {
        uut.onSuccess(NativeCommandListenerTest.CHILD_ID);
        Mockito.verify(promise, Mockito.times(1)).resolve(NativeCommandListenerTest.CHILD_ID);
    }

    @Test
    public void onSuccess_emitsNavigationEvent() {
        uut.onSuccess(NativeCommandListenerTest.CHILD_ID);
        Mockito.verify(eventEmitter, Mockito.times(1)).emitCommandCompleted(NativeCommandListenerTest.COMMAND_ID, NativeCommandListenerTest.NOW);
    }

    @Test
    public void onError() {
        uut.onError("something which is wrong");
        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        Mockito.verify(promise, Mockito.times(1)).reject(captor.capture());
        assertThat(captor.getValue().getMessage().equals("something which is wrong"));
    }
}

