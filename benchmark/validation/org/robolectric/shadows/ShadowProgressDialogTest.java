package org.robolectric.shadows;


import ProgressDialog.STYLE_HORIZONTAL;
import ProgressDialog.STYLE_SPINNER;
import android.app.ProgressDialog;
import android.view.View;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowProgressDialogTest {
    private ProgressDialog dialog;

    private ShadowProgressDialog shadow;

    @Test
    public void shouldExtendAlertDialog() {
        assertThat(shadow).isInstanceOf(ShadowAlertDialog.class);
    }

    @Test
    public void shouldPutTheMessageIntoTheView() {
        String message = "This is only a test";
        shadow.callOnCreate(null);
        View dialogView = shadow.getView();
        assertThat(Shadows.shadowOf(dialogView).innerText()).doesNotContain(message);
        dialog.setMessage(message);
        assertThat(Shadows.shadowOf(shadow.getView()).innerText()).contains(message);
    }

    @Test
    public void shouldSetIndeterminate() {
        assertThat(dialog.isIndeterminate()).isFalse();
        dialog.setIndeterminate(true);
        assertThat(dialog.isIndeterminate()).isTrue();
        dialog.setIndeterminate(false);
        assertThat(dialog.isIndeterminate()).isFalse();
    }

    @Test
    public void shouldSetMax() {
        assertThat(dialog.getMax()).isEqualTo(0);
        dialog.setMax(41);
        assertThat(dialog.getMax()).isEqualTo(41);
    }

    @Test
    public void shouldSetProgress() {
        assertThat(dialog.getProgress()).isEqualTo(0);
        dialog.setProgress(42);
        assertThat(dialog.getProgress()).isEqualTo(42);
    }

    @Test
    public void shouldGetProgressStyle() {
        assertThat(shadow.getProgressStyle()).isEqualTo(STYLE_SPINNER);
        dialog.setProgressStyle(STYLE_HORIZONTAL);
        assertThat(shadow.getProgressStyle()).isEqualTo(STYLE_HORIZONTAL);
        dialog.setProgressStyle(STYLE_SPINNER);
        assertThat(shadow.getProgressStyle()).isEqualTo(STYLE_SPINNER);
    }

    @Test
    public void horizontalStyle_shouldGetMessage() {
        String message = "This is only a test";
        shadow.callOnCreate(null);
        dialog.setProgressStyle(STYLE_HORIZONTAL);
        dialog.setMessage(message);
        assertThat(shadow.getMessage().toString()).contains(message);
    }

    @Test
    public void spinnerStyle_shouldGetMessage() {
        String message = "This is only a test";
        shadow.callOnCreate(null);
        dialog.setProgressStyle(STYLE_SPINNER);
        dialog.setMessage(message);
        assertThat(shadow.getMessage().toString()).contains(message);
    }
}

