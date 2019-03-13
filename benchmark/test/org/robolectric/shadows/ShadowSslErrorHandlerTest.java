package org.robolectric.shadows;


import android.webkit.SslErrorHandler;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowSslErrorHandlerTest {
    private SslErrorHandler handler;

    @Test
    public void shouldRecordCancel() {
        assertThat(Shadows.shadowOf(handler).wasCancelCalled()).isFalse();
        handler.cancel();
        assertThat(Shadows.shadowOf(handler).wasCancelCalled()).isTrue();
    }

    @Test
    public void shouldRecordProceed() {
        assertThat(Shadows.shadowOf(handler).wasProceedCalled()).isFalse();
        handler.proceed();
        assertThat(Shadows.shadowOf(handler).wasProceedCalled()).isTrue();
    }
}

