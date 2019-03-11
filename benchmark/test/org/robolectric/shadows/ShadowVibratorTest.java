package org.robolectric.shadows;


import android.os.Build.VERSION_CODES;
import android.os.Vibrator;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowVibratorTest {
    private Vibrator vibrator;

    @Test
    public void hasVibrator() {
        assertThat(vibrator.hasVibrator()).isTrue();
        Shadows.shadowOf(vibrator).setHasVibrator(false);
        assertThat(vibrator.hasVibrator()).isFalse();
    }

    @Config(minSdk = VERSION_CODES.O)
    @Test
    public void hasAmplitudeControl() {
        assertThat(vibrator.hasAmplitudeControl()).isFalse();
        Shadows.shadowOf(vibrator).setHasAmplitudeControl(true);
        assertThat(vibrator.hasAmplitudeControl()).isTrue();
    }

    @Test
    public void vibrateMilliseconds() {
        vibrator.vibrate(5000);
        assertThat(Shadows.shadowOf(vibrator).isVibrating()).isTrue();
        assertThat(Shadows.shadowOf(vibrator).getMilliseconds()).isEqualTo(5000L);
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
        assertThat(Shadows.shadowOf(vibrator).isVibrating()).isFalse();
    }

    @Test
    public void vibratePattern() {
        long[] pattern = new long[]{ 0, 200 };
        vibrator.vibrate(pattern, 1);
        assertThat(Shadows.shadowOf(vibrator).isVibrating()).isTrue();
        assertThat(Shadows.shadowOf(vibrator).getPattern()).isEqualTo(pattern);
        assertThat(Shadows.shadowOf(vibrator).getRepeat()).isEqualTo(1);
    }

    @Test
    public void cancelled() {
        vibrator.vibrate(5000);
        assertThat(Shadows.shadowOf(vibrator).isVibrating()).isTrue();
        assertThat(Shadows.shadowOf(vibrator).isCancelled()).isFalse();
        vibrator.cancel();
        assertThat(Shadows.shadowOf(vibrator).isVibrating()).isFalse();
        assertThat(Shadows.shadowOf(vibrator).isCancelled()).isTrue();
    }
}

