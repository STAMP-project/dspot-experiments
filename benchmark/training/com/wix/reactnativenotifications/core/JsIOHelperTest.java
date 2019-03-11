package com.wix.reactnativenotifications.core;


import DeviceEventManagerModule.RCTDeviceEventEmitter;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class JsIOHelperTest {
    @Mock
    RCTDeviceEventEmitter mRCTDeviceEventEmitter;

    @Mock
    ReactContext mReactContext;

    @Test
    public void sendEventToJS_hasReactContext_emitsEventToJs() throws Exception {
        WritableMap data = Mockito.mock(WritableMap.class);
        final JsIOHelper uut = createUUT();
        boolean result = uut.sendEventToJS("my-event", data, mReactContext);
        Assert.assertTrue(result);
        Mockito.verify(mRCTDeviceEventEmitter).emit("my-event", data);
    }

    @Test
    public void sendEventToJS_noReactContext_returnsFalse() throws Exception {
        WritableMap data = Mockito.mock(WritableMap.class);
        final JsIOHelper uut = createUUT();
        boolean result = uut.sendEventToJS("my-event", data, null);
        Assert.assertFalse(result);
        Mockito.verify(mRCTDeviceEventEmitter, Mockito.never()).emit(ArgumentMatchers.anyString(), ArgumentMatchers.any(WritableMap.class));
    }
}

