/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.animated.util;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link AnimatedDrawableUtil}.
 */
@RunWith(RobolectricTestRunner.class)
public class AnimatedDrawableUtilTest {
    @Test
    public void testGetFrameTimeStampsFromDurations() {
        int[] frameDurationsMs = new int[]{ 30, 30, 60, 30, 30 };
        AnimatedDrawableUtil util = new AnimatedDrawableUtil();
        int[] frameTimestampsMs = util.getFrameTimeStampsFromDurations(frameDurationsMs);
        int[] expected = new int[]{ 0, 30, 60, 120, 150 };
        Assert.assertArrayEquals(expected, frameTimestampsMs);
    }

    @Test
    public void testGetFrameTimeStampsFromDurationsWithEmptyArray() {
        int[] frameDurationsMs = new int[0];
        AnimatedDrawableUtil util = new AnimatedDrawableUtil();
        int[] frameTimestampsMs = util.getFrameTimeStampsFromDurations(frameDurationsMs);
        Assert.assertEquals(0, frameTimestampsMs.length);
    }

    @Test
    public void testGetFrameForTimestampMs() {
        int[] frameTimestampsMs = new int[]{ 0, 50, 75, 100, 200 };
        AnimatedDrawableUtil util = new AnimatedDrawableUtil();
        Assert.assertEquals(0, util.getFrameForTimestampMs(frameTimestampsMs, 0));
        Assert.assertEquals(0, util.getFrameForTimestampMs(frameTimestampsMs, 1));
        Assert.assertEquals(0, util.getFrameForTimestampMs(frameTimestampsMs, 49));
        Assert.assertEquals(1, util.getFrameForTimestampMs(frameTimestampsMs, 50));
        Assert.assertEquals(1, util.getFrameForTimestampMs(frameTimestampsMs, 74));
        Assert.assertEquals(2, util.getFrameForTimestampMs(frameTimestampsMs, 75));
        Assert.assertEquals(2, util.getFrameForTimestampMs(frameTimestampsMs, 76));
        Assert.assertEquals(2, util.getFrameForTimestampMs(frameTimestampsMs, 99));
        Assert.assertEquals(3, util.getFrameForTimestampMs(frameTimestampsMs, 100));
        Assert.assertEquals(3, util.getFrameForTimestampMs(frameTimestampsMs, 101));
        Assert.assertEquals(4, util.getFrameForTimestampMs(frameTimestampsMs, 200));
    }

    @Test
    public void testIsOutsideRange() {
        Assert.assertTrue(AnimatedDrawableUtil.isOutsideRange((-1), (-1), 1));// Always outside range

        // Test before, within, and after 2 through 5.
        int start = 2;
        int end = 5;
        Assert.assertTrue(AnimatedDrawableUtil.isOutsideRange(start, end, 1));
        Assert.assertFalse(AnimatedDrawableUtil.isOutsideRange(start, end, 2));
        Assert.assertFalse(AnimatedDrawableUtil.isOutsideRange(start, end, 3));
        Assert.assertFalse(AnimatedDrawableUtil.isOutsideRange(start, end, 4));
        Assert.assertFalse(AnimatedDrawableUtil.isOutsideRange(start, end, 5));
        Assert.assertTrue(AnimatedDrawableUtil.isOutsideRange(start, end, 6));
        // Test wrapping case when start is greater than end
        // Test before, within, and after 4 through 1
        start = 4;
        end = 1;
        Assert.assertFalse(AnimatedDrawableUtil.isOutsideRange(start, end, 0));
        Assert.assertFalse(AnimatedDrawableUtil.isOutsideRange(start, end, 1));
        Assert.assertTrue(AnimatedDrawableUtil.isOutsideRange(start, end, 2));
        Assert.assertTrue(AnimatedDrawableUtil.isOutsideRange(start, end, 3));
        Assert.assertFalse(AnimatedDrawableUtil.isOutsideRange(start, end, 4));
        Assert.assertFalse(AnimatedDrawableUtil.isOutsideRange(start, end, 5));
        // Test cases where start == end
        start = 2;
        end = 2;
        Assert.assertTrue(AnimatedDrawableUtil.isOutsideRange(start, end, 1));
        Assert.assertFalse(AnimatedDrawableUtil.isOutsideRange(start, end, 2));
        Assert.assertTrue(AnimatedDrawableUtil.isOutsideRange(start, end, 3));
    }
}

