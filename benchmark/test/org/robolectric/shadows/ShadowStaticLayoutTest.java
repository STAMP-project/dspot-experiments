package org.robolectric.shadows;


import Layout.Alignment;
import android.text.TextPaint;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowStaticLayoutTest {
    @Test
    public void generate_shouldNotThrowException() {
        new android.text.StaticLayout("Hello!", new TextPaint(), 100, Alignment.ALIGN_LEFT, 1.2F, 1.0F, true);
    }
}

