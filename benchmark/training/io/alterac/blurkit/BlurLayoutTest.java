package io.alterac.blurkit;


import android.content.Context;
import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mock;


public class BlurLayoutTest {
    @Mock
    private Context mockContext;

    public static final int TEST_INT = 1;

    private static final float TEST_FLOAT = 1.1F;

    private BlurLayout blurLayout;

    @Test
    public void setFPSTest() {
        blurLayout.setFPS(BlurLayoutTest.TEST_INT);
        Assert.assertEquals(BlurLayoutTest.TEST_INT, blurLayout.getFPS());
    }

    @Test
    public void setDownscaleFactor() {
        blurLayout.setDownscaleFactor(BlurLayoutTest.TEST_FLOAT);
        Assert.assertEquals(BlurLayoutTest.TEST_FLOAT, blurLayout.getDownscaleFactor());
    }

    @Test
    public void setBlurRadiusTest() {
        blurLayout.setBlurRadius(BlurLayoutTest.TEST_INT);
        Assert.assertEquals(BlurLayoutTest.TEST_INT, blurLayout.getBlurRadius());
    }

    @Test
    public void setCornerRadiusTest() {
        blurLayout.setCornerRadius(BlurLayoutTest.TEST_FLOAT);
        Assert.assertEquals(BlurLayoutTest.TEST_FLOAT, blurLayout.getCornerRadius());
    }

    @Test
    public void unlockViewTest() {
        blurLayout.unlockView();
    }

    @Test
    public void lockViewTest() {
        blurLayout.lockView();
    }
}

