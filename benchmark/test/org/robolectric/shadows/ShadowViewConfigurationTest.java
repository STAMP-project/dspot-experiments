package org.robolectric.shadows;


import android.app.Application;
import android.view.ViewConfiguration;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowViewConfigurationTest {
    private Application context;

    @Test
    public void methodsShouldReturnAndroidConstants() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        Assert.assertEquals(10, ViewConfiguration.getScrollBarSize());
        Assert.assertEquals(250, ViewConfiguration.getScrollBarFadeDuration());
        Assert.assertEquals(300, ViewConfiguration.getScrollDefaultDelay());
        Assert.assertEquals(12, ViewConfiguration.getFadingEdgeLength());
        Assert.assertEquals(125, ViewConfiguration.getPressedStateDuration());
        Assert.assertEquals(500, ViewConfiguration.getLongPressTimeout());
        Assert.assertEquals(115, ViewConfiguration.getTapTimeout());
        Assert.assertEquals(500, ViewConfiguration.getJumpTapTimeout());
        Assert.assertEquals(300, ViewConfiguration.getDoubleTapTimeout());
        Assert.assertEquals(12, ViewConfiguration.getEdgeSlop());
        Assert.assertEquals(16, ViewConfiguration.getTouchSlop());
        Assert.assertEquals(16, ViewConfiguration.getWindowTouchSlop());
        Assert.assertEquals(50, ViewConfiguration.getMinimumFlingVelocity());
        Assert.assertEquals(4000, ViewConfiguration.getMaximumFlingVelocity());
        Assert.assertEquals(((320 * 480) * 4), ViewConfiguration.getMaximumDrawingCacheSize());
        Assert.assertEquals(3000, ViewConfiguration.getZoomControlsTimeout());
        Assert.assertEquals(500, ViewConfiguration.getGlobalActionKeyTimeout());
        assertThat(ViewConfiguration.getScrollFriction()).isEqualTo(0.015F);
        assertThat(context.getResources().getDisplayMetrics().density).isEqualTo(1.0F);
        Assert.assertEquals(10, viewConfiguration.getScaledScrollBarSize());
        Assert.assertEquals(12, viewConfiguration.getScaledFadingEdgeLength());
        Assert.assertEquals(12, viewConfiguration.getScaledEdgeSlop());
        Assert.assertEquals(16, viewConfiguration.getScaledTouchSlop());
        Assert.assertEquals(32, viewConfiguration.getScaledPagingTouchSlop());
        Assert.assertEquals(100, viewConfiguration.getScaledDoubleTapSlop());
        Assert.assertEquals(16, viewConfiguration.getScaledWindowTouchSlop());
        Assert.assertEquals(50, viewConfiguration.getScaledMinimumFlingVelocity());
        Assert.assertEquals(4000, viewConfiguration.getScaledMaximumFlingVelocity());
    }

    @Test
    public void methodsShouldReturnScaledAndroidConstantsDependingOnPixelDensity() {
        context.getResources().getDisplayMetrics().density = 1.5F;
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        Assert.assertEquals(15, viewConfiguration.getScaledScrollBarSize());
        Assert.assertEquals(18, viewConfiguration.getScaledFadingEdgeLength());
        Assert.assertEquals(18, viewConfiguration.getScaledEdgeSlop());
        Assert.assertEquals(24, viewConfiguration.getScaledTouchSlop());
        Assert.assertEquals(48, viewConfiguration.getScaledPagingTouchSlop());
        Assert.assertEquals(150, viewConfiguration.getScaledDoubleTapSlop());
        Assert.assertEquals(24, viewConfiguration.getScaledWindowTouchSlop());
        Assert.assertEquals(75, viewConfiguration.getScaledMinimumFlingVelocity());
        Assert.assertEquals(6000, viewConfiguration.getScaledMaximumFlingVelocity());
    }

    @Test
    public void testHasPermanentMenuKey() throws Exception {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        assertThat(viewConfiguration.hasPermanentMenuKey()).isFalse();
        ShadowViewConfiguration shadowViewConfiguration = Shadows.shadowOf(viewConfiguration);
        shadowViewConfiguration.setHasPermanentMenuKey(true);
        assertThat(viewConfiguration.hasPermanentMenuKey()).isTrue();
    }
}

