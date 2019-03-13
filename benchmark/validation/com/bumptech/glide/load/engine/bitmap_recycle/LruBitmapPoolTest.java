package com.bumptech.glide.load.engine.bitmap_recycle;


import Bitmap.Config.ARGB_4444;
import Bitmap.Config.ARGB_8888;
import Bitmap.Config.RGB_565;
import android.graphics.Bitmap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 28)
public class LruBitmapPoolTest {
    private static final int MAX_SIZE = 10;

    private static final Set<Bitmap.Config> ALLOWED_CONFIGS = Collections.singleton(ARGB_8888);

    private LruBitmapPoolTest.MockStrategy strategy;

    private LruBitmapPool pool;

    @Test
    public void testICanAddAndGetABitmap() {
        fillPool(pool, 1);
        pool.put(createMutableBitmap());
        Assert.assertNotNull(pool.get(100, 100, ARGB_8888));
    }

    @Test
    public void testImmutableBitmapsAreNotAdded() {
        Bitmap bitmap = createMutableBitmap();
        Shadows.shadowOf(bitmap).setMutable(false);
        pool.put(bitmap);
        assertThat(strategy.bitmaps).isEmpty();
    }

    @Test
    public void testItIsSizeLimited() {
        fillPool(pool, ((LruBitmapPoolTest.MAX_SIZE) + 2));
        Assert.assertEquals(2, strategy.numRemoves);
    }

    @Test
    public void testBitmapLargerThanPoolIsNotAdded() {
        strategy = new LruBitmapPoolTest.MockStrategy() {
            @Override
            public int getSize(Bitmap bitmap) {
                return 4;
            }
        };
        pool = new LruBitmapPool(3, strategy, LruBitmapPoolTest.ALLOWED_CONFIGS);
        pool.put(createMutableBitmap());
        Assert.assertEquals(0, strategy.numRemoves);
        Assert.assertEquals(0, strategy.numPuts);
    }

    @Test
    public void testClearMemoryRemovesAllBitmaps() {
        fillPool(pool, LruBitmapPoolTest.MAX_SIZE);
        pool.clearMemory();
        Assert.assertEquals(LruBitmapPoolTest.MAX_SIZE, strategy.numRemoves);
    }

    @Test
    public void testEvictedBitmapsAreRecycled() {
        fillPool(pool, LruBitmapPoolTest.MAX_SIZE);
        List<Bitmap> bitmaps = new ArrayList<>(LruBitmapPoolTest.MAX_SIZE);
        bitmaps.addAll(strategy.bitmaps);
        pool.clearMemory();
        for (Bitmap b : bitmaps) {
            Assert.assertTrue(b.isRecycled());
        }
    }

    @Test
    public void testTrimMemoryUiHiddenOrLessRemovesHalfOfBitmaps() {
        testTrimMemory(LruBitmapPoolTest.MAX_SIZE, TRIM_MEMORY_UI_HIDDEN, ((LruBitmapPoolTest.MAX_SIZE) / 2));
    }

    @Test
    public void testTrimMemoryRunningCriticalRemovesHalfOfBitmaps() {
        testTrimMemory(LruBitmapPoolTest.MAX_SIZE, TRIM_MEMORY_RUNNING_CRITICAL, ((LruBitmapPoolTest.MAX_SIZE) / 2));
    }

    @Test
    public void testTrimMemoryUiHiddenOrLessRemovesNoBitmapsIfPoolLessThanHalfFull() {
        testTrimMemory(((LruBitmapPoolTest.MAX_SIZE) / 2), TRIM_MEMORY_UI_HIDDEN, 0);
    }

    @Test
    public void testTrimMemoryBackgroundOrGreaterRemovesAllBitmaps() {
        for (int trimLevel : new int[]{ TRIM_MEMORY_BACKGROUND, TRIM_MEMORY_COMPLETE }) {
            testTrimMemory(LruBitmapPoolTest.MAX_SIZE, trimLevel, LruBitmapPoolTest.MAX_SIZE);
        }
    }

    @Test
    public void testPassesArgb888ToStrategyAsConfigForRequestsWithNullConfigsOnGet() {
        LruPoolStrategy strategy = Mockito.mock(LruPoolStrategy.class);
        LruBitmapPool pool = new LruBitmapPool(100, strategy, LruBitmapPoolTest.ALLOWED_CONFIGS);
        Bitmap expected = createMutableBitmap();
        Mockito.when(strategy.get(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(ARGB_8888))).thenReturn(expected);
        Bitmap result = pool.get(100, 100, null);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testPassesArgb8888ToStrategyAsConfigForRequestsWithNullConfigsOnGetDirty() {
        LruPoolStrategy strategy = Mockito.mock(LruPoolStrategy.class);
        LruBitmapPool pool = new LruBitmapPool(100, strategy, LruBitmapPoolTest.ALLOWED_CONFIGS);
        Bitmap expected = createMutableBitmap();
        Mockito.when(strategy.get(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(ARGB_8888))).thenReturn(expected);
        Bitmap result = pool.getDirty(100, 100, null);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void get_withNullConfig_andEmptyPool_returnsNewArgb8888Bitmap() {
        Bitmap result = /* config= */
        pool.get(100, 100, null);
        assertThat(result.getConfig()).isEqualTo(ARGB_8888);
    }

    @Test
    public void getDirty_withNullConfig_andEmptyPool_returnsNewArgb8888Bitmap() {
        Bitmap result = /* config= */
        pool.getDirty(100, 100, null);
        assertThat(result.getConfig()).isEqualTo(ARGB_8888);
    }

    @Test
    public void testCanIncreaseSizeDynamically() {
        int sizeMultiplier = 2;
        pool.setSizeMultiplier(2);
        fillPool(pool, ((LruBitmapPoolTest.MAX_SIZE) * sizeMultiplier));
        Assert.assertEquals(0, strategy.numRemoves);
    }

    @Test
    public void testCanDecreaseSizeDynamically() {
        fillPool(pool, LruBitmapPoolTest.MAX_SIZE);
        Assert.assertEquals(0, strategy.numRemoves);
        float sizeMultiplier = 0.5F;
        pool.setSizeMultiplier(sizeMultiplier);
        Assert.assertEquals(Math.round(((LruBitmapPoolTest.MAX_SIZE) * sizeMultiplier)), strategy.numRemoves);
    }

    @Test
    public void testCanResetSizeDynamically() {
        int sizeMultiplier = 2;
        pool.setSizeMultiplier(sizeMultiplier);
        fillPool(pool, ((LruBitmapPoolTest.MAX_SIZE) * sizeMultiplier));
        pool.setSizeMultiplier(1);
        Assert.assertEquals((((LruBitmapPoolTest.MAX_SIZE) * sizeMultiplier) - (LruBitmapPoolTest.MAX_SIZE)), strategy.numRemoves);
    }

    @Test
    public void testCanGetCurrentMaxSize() {
        Assert.assertEquals(LruBitmapPoolTest.MAX_SIZE, pool.getMaxSize());
    }

    @Test
    public void testMaxSizeChangesAfterSizeMultiplier() {
        pool.setSizeMultiplier(2);
        Assert.assertEquals((2 * (LruBitmapPoolTest.MAX_SIZE)), pool.getMaxSize());
    }

    @Test
    public void testBitmapsWithDisallowedConfigsAreIgnored() {
        pool = new LruBitmapPool(100, strategy, Collections.singleton(ARGB_4444));
        Bitmap bitmap = createMutableBitmap(RGB_565);
        pool.put(bitmap);
        Assert.assertEquals(0, strategy.numPuts);
    }

    @Test
    @Config(sdk = 19)
    public void testBitmapsWithAllowedNullConfigsAreAllowed() {
        pool = new LruBitmapPool(100, strategy, Collections.<Bitmap.Config>singleton(null));
        Bitmap bitmap = createMutableBitmap();
        bitmap.setConfig(null);
        pool.put(bitmap);
        Assert.assertEquals(1, strategy.numPuts);
    }

    private static class MockStrategy implements LruPoolStrategy {
        private final ArrayDeque<Bitmap> bitmaps = new ArrayDeque<>();

        private int numRemoves;

        private int numPuts;

        @Override
        public void put(Bitmap bitmap) {
            (numPuts)++;
            bitmaps.add(bitmap);
        }

        @Override
        public Bitmap get(int width, int height, Bitmap.Config config) {
            return bitmaps.isEmpty() ? null : bitmaps.removeLast();
        }

        @Override
        public Bitmap removeLast() {
            (numRemoves)++;
            return bitmaps.removeLast();
        }

        @Override
        public String logBitmap(Bitmap bitmap) {
            return null;
        }

        @Override
        public String logBitmap(int width, int height, Bitmap.Config config) {
            return null;
        }

        @Override
        public int getSize(Bitmap bitmap) {
            return 1;
        }
    }
}

