package com.bumptech.glide.load.resource.bitmap;


import DownsampleStrategy.AT_LEAST;
import DownsampleStrategy.AT_MOST;
import DownsampleStrategy.CENTER_OUTSIDE;
import DownsampleStrategy.FIT_CENTER;
import DownsampleStrategy.NONE;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class DownsampleStrategyTest {
    @Test
    public void testAtMost_withSourceSmallerInOneDimensions_returnsScaleFactorForLargestDimension() {
        assertThat(AT_MOST.getScaleFactor(100, 200, 200, 200)).isEqualTo(1.0F);
        assertThat(AT_MOST.getScaleFactor(200, 100, 200, 200)).isEqualTo(1.0F);
        assertThat(AT_MOST.getScaleFactor(270, 480, 724, 440)).isEqualTo((1 / 2.0F));
        assertThat(AT_MOST.getScaleFactor(400, 200, 200, 200)).isEqualTo((1 / 2.0F));
        assertThat(AT_MOST.getScaleFactor(800, 200, 200, 200)).isEqualTo((1 / 4.0F));
    }

    @Test
    public void testAtMost_withSourceExactlyEqualToRequested_returnsScaleFactorOfOne() {
        assertThat(AT_MOST.getScaleFactor(100, 100, 100, 100)).isEqualTo(1.0F);
        assertThat(AT_MOST.getScaleFactor(1234, 452, 1234, 452)).isEqualTo(1.0F);
        assertThat(AT_MOST.getScaleFactor(341, 122, 341, 122)).isEqualTo(1.0F);
    }

    @Test
    public void testAtMost_withSourceLessThanTwiceRequestedSize_returnsScaleFactorOfTwo() {
        assertThat(AT_MOST.getScaleFactor(150, 150, 100, 100)).isEqualTo((1 / 2.0F));
        assertThat(AT_MOST.getScaleFactor(101, 101, 100, 100)).isEqualTo((1 / 2.0F));
        assertThat(AT_MOST.getScaleFactor(199, 199, 100, 100)).isEqualTo((1 / 2.0F));
    }

    @Test
    public void testAtMost_withSourceGreaterThanRequestedSize_returnsPowerOfTwoScaleFactor() {
        assertThat(AT_MOST.getScaleFactor(200, 200, 100, 100)).isEqualTo((1 / 2.0F));
        assertThat(AT_MOST.getScaleFactor(300, 300, 100, 100)).isEqualTo((1 / 4.0F));
        assertThat(AT_MOST.getScaleFactor(400, 400, 100, 100)).isEqualTo((1 / 4.0F));
        assertThat(AT_MOST.getScaleFactor(1000, 200, 100, 100)).isEqualTo((1 / 16.0F));
        assertThat(AT_MOST.getScaleFactor(1000, 1000, 100, 100)).isEqualTo((1 / 16.0F));
    }

    @Test
    public void testAtMost_withSourceGreaterInOneDimension_returnsScaleFactorOfLargestDimension() {
        assertThat(AT_MOST.getScaleFactor(101, 200, 100, 100)).isEqualTo((1 / 2.0F));
        assertThat(AT_MOST.getScaleFactor(199, 200, 100, 100)).isEqualTo((1 / 2.0F));
        assertThat(AT_MOST.getScaleFactor(400, 200, 100, 100)).isEqualTo((1 / 4.0F));
        assertThat(AT_MOST.getScaleFactor(1000, 400, 100, 100)).isEqualTo((1 / 16.0F));
    }

    @Test
    public void testAtLeast_withSourceSmallerInOneDimension_returnsScaleFactorOfOne() {
        assertThat(AT_LEAST.getScaleFactor(100, 200, 200, 200)).isEqualTo(1.0F);
        assertThat(AT_LEAST.getScaleFactor(200, 100, 200, 200)).isEqualTo(1.0F);
        assertThat(AT_LEAST.getScaleFactor(270, 480, 724, 440)).isEqualTo(1.0F);
    }

    @Test
    public void testAtLeast_withSourceExactlyEqualToRequested_returnsScaleFactorOfOne() {
        assertThat(AT_LEAST.getScaleFactor(100, 100, 100, 100)).isEqualTo(1.0F);
        assertThat(AT_LEAST.getScaleFactor(1234, 452, 1234, 452)).isEqualTo(1.0F);
        assertThat(AT_LEAST.getScaleFactor(341, 122, 341, 122)).isEqualTo(1.0F);
    }

    @Test
    public void testAtLeast_withSourceLessThanTwiceRequestedSize_returnsScaleFactorOfOne() {
        assertThat(AT_LEAST.getScaleFactor(150, 150, 100, 100)).isEqualTo(1.0F);
        assertThat(AT_LEAST.getScaleFactor(101, 101, 100, 100)).isEqualTo(1.0F);
        assertThat(AT_LEAST.getScaleFactor(199, 199, 100, 100)).isEqualTo(1.0F);
    }

    @Test
    public void testAtLeast_withSourceGreaterThanRequestedSize_returnsPowerOfTwoScaleFactor() {
        assertThat(AT_LEAST.getScaleFactor(200, 200, 100, 100)).isEqualTo((1 / 2.0F));
        assertThat(AT_LEAST.getScaleFactor(300, 300, 100, 100)).isEqualTo((1 / 2.0F));
        assertThat(AT_LEAST.getScaleFactor(400, 400, 100, 100)).isEqualTo((1 / 4.0F));
        assertThat(AT_LEAST.getScaleFactor(1000, 200, 100, 100)).isEqualTo((1 / 2.0F));
        assertThat(AT_LEAST.getScaleFactor(1000, 1000, 100, 100)).isEqualTo((1 / 8.0F));
    }

    @Test
    public void testAtLeast_withSourceGreaterInOneDimension_returnsScaleFactorOfSmallestDimension() {
        assertThat(AT_LEAST.getScaleFactor(101, 200, 100, 100)).isEqualTo(1.0F);
        assertThat(AT_LEAST.getScaleFactor(199, 200, 100, 100)).isEqualTo(1.0F);
        assertThat(AT_LEAST.getScaleFactor(400, 200, 100, 100)).isEqualTo((1 / 2.0F));
        assertThat(AT_LEAST.getScaleFactor(1000, 400, 100, 100)).isEqualTo((1 / 4.0F));
    }

    @Test
    public void testCenterInside_scalesImageToFitWithinRequestedBounds() {
        assertThat(FIT_CENTER.getScaleFactor(100, 200, 300, 300)).isEqualTo((300 / 200.0F));
        assertThat(FIT_CENTER.getScaleFactor(270, 480, 724, 440)).isEqualTo((440 / 480.0F));
        assertThat(FIT_CENTER.getScaleFactor(100, 100, 100, 100)).isEqualTo(1.0F);
    }

    @Test
    public void testCenterOutside_scalesImageToFitAroundRequestedBounds() {
        assertThat(CENTER_OUTSIDE.getScaleFactor(100, 200, 300, 300)).isEqualTo((300 / 100.0F));
        assertThat(CENTER_OUTSIDE.getScaleFactor(270, 480, 724, 440)).isEqualTo((724 / 270.0F));
        assertThat(CENTER_OUTSIDE.getScaleFactor(100, 100, 100, 100)).isEqualTo(1.0F);
    }

    @Test
    public void testNone_alwaysReturnsOne() {
        assertThat(NONE.getScaleFactor(100, 100, 100, 100)).isEqualTo(1.0F);
        assertThat(NONE.getScaleFactor(200, 200, 100, 100)).isEqualTo(1.0F);
        assertThat(NONE.getScaleFactor(100, 100, 200, 200)).isEqualTo(1.0F);
    }
}

