package org.robolectric.shadows;


import android.content.Context;
import android.widget.AbsSeekBar;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowAbsSeekBarTest {
    @Test
    public void testInheritance() {
        // TODO: this seems to test static typing - compiler enforces this ;)
        ShadowAbsSeekBarTest.TestAbsSeekBar seekBar = new ShadowAbsSeekBarTest.TestAbsSeekBar(ApplicationProvider.getApplicationContext());
        ShadowAbsSeekBar shadow = Shadows.shadowOf(seekBar);
        assertThat(shadow).isInstanceOf(ShadowProgressBar.class);
    }

    private static class TestAbsSeekBar extends AbsSeekBar {
        public TestAbsSeekBar(Context context) {
            super(context);
        }
    }
}

