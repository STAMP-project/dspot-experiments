package org.robolectric.shadows;


import android.view.accessibility.CaptioningManager;
import android.view.accessibility.CaptioningManager.CaptioningChangeListener;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


/**
 * Tests for the ShadowCaptioningManager.
 */
@RunWith(AndroidJUnit4.class)
@Config(minSdk = 19)
public final class ShadowCaptioningManagerTest {
    @Mock
    private CaptioningChangeListener captioningChangeListener;

    private CaptioningManager captioningManager;

    @Test
    public void setEnabled_true() {
        assertThat(captioningManager.isEnabled()).isFalse();
        Shadows.shadowOf(captioningManager).setEnabled(true);
        assertThat(captioningManager.isEnabled()).isTrue();
    }

    @Test
    public void setEnabled_false() {
        Shadows.shadowOf(captioningManager).setEnabled(false);
        assertThat(captioningManager.isEnabled()).isFalse();
    }

    @Test
    public void setFontScale_changesValueOfGetFontScale() {
        float fontScale = 1.5F;
        Shadows.shadowOf(captioningManager).setFontScale(fontScale);
        assertThat(captioningManager.getFontScale()).isWithin(0.001F).of(fontScale);
    }

    @Test
    public void setFontScale_notifiesObservers() {
        float fontScale = 1.5F;
        captioningManager.addCaptioningChangeListener(captioningChangeListener);
        Shadows.shadowOf(captioningManager).setFontScale(fontScale);
        Mockito.verify(captioningChangeListener).onFontScaleChanged(fontScale);
    }

    @Test
    public void addCaptioningChangeListener_doesNotRegisterSameListenerTwice() {
        float fontScale = 1.5F;
        captioningManager.addCaptioningChangeListener(captioningChangeListener);
        captioningManager.addCaptioningChangeListener(captioningChangeListener);
        Shadows.shadowOf(captioningManager).setFontScale(fontScale);
        Mockito.verify(captioningChangeListener).onFontScaleChanged(fontScale);
    }

    @Test
    public void removeCaptioningChangeListener_unregistersFontScaleListener() {
        captioningManager.addCaptioningChangeListener(captioningChangeListener);
        captioningManager.removeCaptioningChangeListener(captioningChangeListener);
        Shadows.shadowOf(captioningManager).setFontScale(1.5F);
        Mockito.verifyZeroInteractions(captioningChangeListener);
    }
}

