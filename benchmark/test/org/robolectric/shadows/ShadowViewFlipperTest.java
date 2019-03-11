package org.robolectric.shadows;


import android.widget.ViewFlipper;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowViewFlipperTest {
    protected ViewFlipper flipper;

    @Test
    public void testStartFlipping() {
        flipper.startFlipping();
        Assert.assertTrue("flipping", flipper.isFlipping());
    }

    @Test
    public void testStopFlipping() {
        flipper.stopFlipping();
        Assert.assertFalse("flipping", flipper.isFlipping());
    }
}

