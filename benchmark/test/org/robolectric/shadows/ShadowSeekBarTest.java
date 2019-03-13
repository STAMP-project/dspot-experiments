package org.robolectric.shadows;


import SeekBar.OnSeekBarChangeListener;
import android.widget.SeekBar;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowSeekBarTest {
    private SeekBar seekBar;

    private ShadowSeekBar shadow;

    private OnSeekBarChangeListener listener;

    private List<String> transcript;

    @Test
    public void testOnSeekBarChangedListener() {
        assertThat(shadow.getOnSeekBarChangeListener()).isSameAs(listener);
        seekBar.setOnSeekBarChangeListener(null);
        assertThat(shadow.getOnSeekBarChangeListener()).isNull();
    }

    @Test
    public void testOnChangeNotification() {
        seekBar.setProgress(5);
        assertThat(transcript).containsExactly("onProgressChanged() - 5");
    }

    private class TestSeekBarChangedListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            transcript.add(("onProgressChanged() - " + progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
}

