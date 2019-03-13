package org.robolectric.shadows;


import Display.DEFAULT_DISPLAY;
import Display.STATE_DOZE_SUSPEND;
import DisplayMetrics.DENSITY_HIGH;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManagerGlobal;
import android.os.Build.VERSION_CODES;
import android.util.DisplayMetrics;
import android.view.Display;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
public class ShadowDisplayTest {
    private Display display;

    private ShadowDisplay shadow;

    @Test
    public void shouldProvideDisplayMetrics() throws Exception {
        shadow.setDensity(1.5F);
        shadow.setDensityDpi(DENSITY_HIGH);
        shadow.setScaledDensity(1.6F);
        shadow.setWidth(1024);
        shadow.setHeight(600);
        shadow.setRealWidth(1400);
        shadow.setRealHeight(900);
        shadow.setXdpi(183.0F);
        shadow.setYdpi(184.0F);
        shadow.setRefreshRate(123.0F);
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        Assert.assertEquals(1.5F, metrics.density, 0.05);
        Assert.assertEquals(DENSITY_HIGH, metrics.densityDpi);
        Assert.assertEquals(1.6F, metrics.scaledDensity, 0.05);
        Assert.assertEquals(1024, metrics.widthPixels);
        Assert.assertEquals(600, metrics.heightPixels);
        Assert.assertEquals(183.0F, metrics.xdpi, 0.05);
        Assert.assertEquals(184.0F, metrics.ydpi, 0.05);
        metrics = new DisplayMetrics();
        display.getRealMetrics(metrics);
        Assert.assertEquals(1.5F, metrics.density, 0.05);
        Assert.assertEquals(DENSITY_HIGH, metrics.densityDpi);
        Assert.assertEquals(1.6F, metrics.scaledDensity, 0.05);
        Assert.assertEquals(1400, metrics.widthPixels);
        Assert.assertEquals(900, metrics.heightPixels);
        Assert.assertEquals(183.0F, metrics.xdpi, 0.05);
        Assert.assertEquals(184.0F, metrics.ydpi, 0.05);
        Assert.assertEquals(0, 123.0F, display.getRefreshRate());
    }

    @Test
    public void changedStateShouldApplyToOtherInstancesOfSameDisplay() throws Exception {
        shadow.setName("another name");
        shadow.setWidth(1024);
        shadow.setHeight(600);
        display = DisplayManagerGlobal.getInstance().getRealDisplay(DEFAULT_DISPLAY);
        Assert.assertEquals(1024, display.getWidth());
        Assert.assertEquals(600, display.getHeight());
        Assert.assertEquals("another name", display.getName());
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void stateChangeShouldApplyToOtherInstancesOfSameDisplay_postKitKatFields() throws Exception {
        shadow.setState(STATE_DOZE_SUSPEND);
        display = DisplayManagerGlobal.getInstance().getRealDisplay(DEFAULT_DISPLAY);
        Assert.assertEquals(STATE_DOZE_SUSPEND, display.getState());
    }

    @Test
    public void shouldProvideDisplaySize() throws Exception {
        Point outSmallestSize = new Point();
        Point outLargestSize = new Point();
        Point outSize = new Point();
        Rect outRect = new Rect();
        shadow.setWidth(400);
        shadow.setHeight(600);
        shadow.setRealWidth(480);
        shadow.setRealHeight(800);
        display.getCurrentSizeRange(outSmallestSize, outLargestSize);
        Assert.assertEquals(400, outSmallestSize.x);
        Assert.assertEquals(400, outSmallestSize.y);
        Assert.assertEquals(600, outLargestSize.x);
        Assert.assertEquals(600, outLargestSize.y);
        display.getSize(outSize);
        Assert.assertEquals(400, outSize.x);
        Assert.assertEquals(600, outSize.y);
        display.getRectSize(outRect);
        Assert.assertEquals(400, outRect.width());
        Assert.assertEquals(600, outRect.height());
        display.getRealSize(outSize);
        Assert.assertEquals(480, outSize.x);
        Assert.assertEquals(800, outSize.y);
    }

    @Test
    public void shouldProvideWeirdDisplayInformation() {
        shadow.setName("foo");
        shadow.setFlags(123);
        Assert.assertEquals("foo", display.getName());
        Assert.assertEquals(123, display.getFlags());
        display = DisplayManagerGlobal.getInstance().getRealDisplay(DEFAULT_DISPLAY);
        Assert.assertEquals(123, display.getFlags());
    }

    /**
     * The {@link android.view.Display#getOrientation()} method is deprecated, but for
     * testing purposes, return the value gotten from {@link android.view.Display#getRotation()}
     */
    @Test
    public void deprecatedGetOrientation_returnsGetRotation() {
        int testValue = 33;
        shadow.setRotation(testValue);
        Assert.assertEquals(testValue, display.getOrientation());
    }
}

