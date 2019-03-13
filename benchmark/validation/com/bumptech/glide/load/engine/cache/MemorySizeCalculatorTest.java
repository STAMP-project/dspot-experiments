package com.bumptech.glide.load.engine.cache;


import Context.ACTIVITY_SERVICE;
import MemorySizeCalculator.Builder;
import MemorySizeCalculator.ScreenDimensions;
import RuntimeEnvironment.application;
import android.app.ActivityManager;
import com.bumptech.glide.tests.Util;
import com.google.common.collect.Range;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowActivityManager;

import static MemorySizeCalculator.BYTES_PER_ARGB_8888_PIXEL;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 19, shadows = MemorySizeCalculatorTest.LowRamActivityManager.class)
public class MemorySizeCalculatorTest {
    private MemorySizeCalculatorTest.MemorySizeHarness harness;

    private int initialSdkVersion;

    @Test
    public void testDefaultMemoryCacheSizeIsTwiceScreenSize() {
        setMemoryClass(getLargeEnoughMemoryClass());
        float memoryCacheSize = harness.getCalculator().getMemoryCacheSize();
        assertThat(memoryCacheSize).isEqualTo(((harness.getScreenSize()) * (harness.memoryCacheScreens)));
    }

    @Test
    public void testCanSetCustomMemoryCacheSize() {
        harness.memoryCacheScreens = 9.5F;
        setMemoryClass(getLargeEnoughMemoryClass());
        float memoryCacheSize = harness.getCalculator().getMemoryCacheSize();
        assertThat(memoryCacheSize).isEqualTo(((harness.getScreenSize()) * (harness.memoryCacheScreens)));
    }

    @Test
    public void testDefaultMemoryCacheSizeIsLimitedByMemoryClass() {
        final int memoryClassBytes = Math.round((((harness.getScreenSize()) * (harness.memoryCacheScreens)) * (harness.sizeMultiplier)));
        setMemoryClass((memoryClassBytes / (1024 * 1024)));
        float memoryCacheSize = harness.getCalculator().getMemoryCacheSize();
        assertThat(memoryCacheSize).isIn(Range.atMost((memoryClassBytes * (harness.sizeMultiplier))));
    }

    @Test
    public void testDefaultBitmapPoolSize() {
        setMemoryClass(getLargeEnoughMemoryClass());
        float bitmapPoolSize = harness.getCalculator().getBitmapPoolSize();
        assertThat(bitmapPoolSize).isEqualTo(((harness.getScreenSize()) * (harness.bitmapPoolScreens)));
    }

    @Test
    public void testCanSetCustomBitmapPoolSize() {
        harness.bitmapPoolScreens = 2.0F;
        setMemoryClass(getLargeEnoughMemoryClass());
        float bitmapPoolSize = harness.getCalculator().getBitmapPoolSize();
        assertThat(bitmapPoolSize).isEqualTo(((harness.getScreenSize()) * (harness.bitmapPoolScreens)));
    }

    @Test
    public void testDefaultBitmapPoolSizeIsLimitedByMemoryClass() {
        final int memoryClassBytes = Math.round((((harness.getScreenSize()) * (harness.bitmapPoolScreens)) * (harness.sizeMultiplier)));
        setMemoryClass((memoryClassBytes / (1024 * 1024)));
        int bitmapPoolSize = harness.getCalculator().getBitmapPoolSize();
        assertThat(((float) (bitmapPoolSize))).isIn(Range.atMost((memoryClassBytes * (harness.sizeMultiplier))));
    }

    @Test
    public void testCumulativePoolAndMemoryCacheSizeAreLimitedByMemoryClass() {
        final int memoryClassBytes = Math.round((((harness.getScreenSize()) * ((harness.bitmapPoolScreens) + (harness.memoryCacheScreens))) * (harness.sizeMultiplier)));
        setMemoryClass((memoryClassBytes / (1024 * 1024)));
        int memoryCacheSize = harness.getCalculator().getMemoryCacheSize();
        int bitmapPoolSize = harness.getCalculator().getBitmapPoolSize();
        assertThat((((float) (memoryCacheSize)) + bitmapPoolSize)).isIn(Range.atMost((memoryClassBytes * (harness.sizeMultiplier))));
    }

    @Test
    public void testCumulativePoolAndMemoryCacheSizesAreSmallerOnLowMemoryDevices() {
        setMemoryClass(((getLargeEnoughMemoryClass()) / 2));
        final int normalMemoryCacheSize = harness.getCalculator().getMemoryCacheSize();
        final int normalBitmapPoolSize = harness.getCalculator().getBitmapPoolSize();
        Util.setSdkVersionInt(10);
        // Keep the bitmap pool size constant, even though normally it would change.
        harness.byteArrayPoolSizeBytes *= 2;
        final int smallMemoryCacheSize = harness.getCalculator().getMemoryCacheSize();
        final int smallBitmapPoolSize = harness.getCalculator().getBitmapPoolSize();
        assertThat(smallMemoryCacheSize).isLessThan(normalMemoryCacheSize);
        assertThat(smallBitmapPoolSize).isLessThan(normalBitmapPoolSize);
    }

    @Test
    public void testByteArrayPoolSize_withLowRamDevice_isHalfTheSpecifiedBytes() {
        MemorySizeCalculatorTest.LowRamActivityManager activityManager = Shadow.extract(harness.activityManager);
        setMemoryClass(getLargeEnoughMemoryClass());
        activityManager.setIsLowRam();
        int byteArrayPoolSize = harness.getCalculator().getArrayPoolSizeInBytes();
        assertThat(byteArrayPoolSize).isEqualTo(((harness.byteArrayPoolSizeBytes) / 2));
    }

    private static class MemorySizeHarness {
        final int pixelSize = 500;

        final int bytesPerPixel = BYTES_PER_ARGB_8888_PIXEL;

        float memoryCacheScreens = Builder.MEMORY_CACHE_TARGET_SCREENS;

        float bitmapPoolScreens = Builder.BITMAP_POOL_TARGET_SCREENS;

        final float sizeMultiplier = Builder.MAX_SIZE_MULTIPLIER;

        int byteArrayPoolSizeBytes = Builder.ARRAY_POOL_SIZE_BYTES;

        final ActivityManager activityManager = ((ActivityManager) (application.getSystemService(ACTIVITY_SERVICE)));

        final ScreenDimensions screenDimensions = Mockito.mock(ScreenDimensions.class);

        MemorySizeCalculator getCalculator() {
            Mockito.when(screenDimensions.getWidthPixels()).thenReturn(pixelSize);
            Mockito.when(screenDimensions.getHeightPixels()).thenReturn(pixelSize);
            return setMemoryCacheScreens(memoryCacheScreens).setBitmapPoolScreens(bitmapPoolScreens).setMaxSizeMultiplier(sizeMultiplier).setActivityManager(activityManager).setScreenDimensions(screenDimensions).setArrayPoolSize(byteArrayPoolSizeBytes).build();
        }

        int getScreenSize() {
            return ((pixelSize) * (pixelSize)) * (bytesPerPixel);
        }
    }

    @Implements(ActivityManager.class)
    public static final class LowRamActivityManager extends ShadowActivityManager {
        private boolean isLowRam;

        void setIsLowRam() {
            this.isLowRam = true;
        }

        @Implementation
        @Override
        public boolean isLowRamDevice() {
            return isLowRam;
        }
    }
}

