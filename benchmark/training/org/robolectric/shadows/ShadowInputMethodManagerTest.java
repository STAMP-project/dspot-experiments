package org.robolectric.shadows;


import InputMethodManager.RESULT_HIDDEN;
import InputMethodManager.RESULT_UNCHANGED_HIDDEN;
import ShadowInputMethodManager.SoftInputVisibilityChangeHandler;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.view.inputmethod.InputMethodManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(AndroidJUnit4.class)
public class ShadowInputMethodManagerTest {
    private InputMethodManager manager;

    private ShadowInputMethodManager shadow;

    @Test
    public void shouldRecordSoftInputVisibility() {
        assertThat(shadow.isSoftInputVisible()).isFalse();
        manager.showSoftInput(null, 0);
        assertThat(shadow.isSoftInputVisible()).isTrue();
        manager.hideSoftInputFromWindow(null, 0);
        assertThat(shadow.isSoftInputVisible()).isFalse();
    }

    @Test
    public void hideSoftInputFromWindow_shouldNotifiyResult_hidden() {
        manager.showSoftInput(null, 0);
        ShadowInputMethodManagerTest.CapturingResultReceiver resultReceiver = new ShadowInputMethodManagerTest.CapturingResultReceiver(new Handler(Looper.getMainLooper()));
        manager.hideSoftInputFromWindow(null, 0, resultReceiver);
        assertThat(resultReceiver.resultCode).isEqualTo(RESULT_HIDDEN);
    }

    @Test
    public void hideSoftInputFromWindow_shouldNotifiyResult_alreadyHidden() {
        ShadowInputMethodManagerTest.CapturingResultReceiver resultReceiver = new ShadowInputMethodManagerTest.CapturingResultReceiver(new Handler(Looper.getMainLooper()));
        manager.hideSoftInputFromWindow(null, 0, resultReceiver);
        assertThat(resultReceiver.resultCode).isEqualTo(RESULT_UNCHANGED_HIDDEN);
    }

    @Test
    public void shouldToggleSoftInputVisibility() {
        assertThat(shadow.isSoftInputVisible()).isFalse();
        manager.toggleSoftInput(0, 0);
        assertThat(shadow.isSoftInputVisible()).isTrue();
        manager.toggleSoftInput(0, 0);
        assertThat(shadow.isSoftInputVisible()).isFalse();
    }

    @Test
    public void shouldNotifyHandlerWhenVisibilityChanged() {
        ShadowInputMethodManager.SoftInputVisibilityChangeHandler mockHandler = Mockito.mock(SoftInputVisibilityChangeHandler.class);
        shadow.setSoftInputVisibilityHandler(mockHandler);
        assertThat(shadow.isSoftInputVisible()).isFalse();
        manager.toggleSoftInput(0, 0);
        Mockito.verify(mockHandler).handleSoftInputVisibilityChange(true);
    }

    private static class CapturingResultReceiver extends ResultReceiver {
        private int resultCode = -1;

        public CapturingResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            this.resultCode = resultCode;
        }
    }
}

