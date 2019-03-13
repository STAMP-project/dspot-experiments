package com.bumptech.glide.load.engine.prefill;


import Bitmap.Config.ARGB_4444;
import Bitmap.Config.ARGB_8888;
import Bitmap.Config.RGB_565;
import BitmapPreFillRunner.Clock;
import BitmapPreFillRunner.INITIAL_BACKOFF_MS;
import BitmapPreFillRunner.MAX_BACKOFF_MS;
import BitmapPreFillRunner.MAX_DURATION_MS;
import BitmapPreFillRunner.TAG;
import Log.VERBOSE;
import android.graphics.Bitmap;
import android.os.Handler;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.engine.cache.MemoryCacheAdapter;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.util.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static BitmapPreFillRunner.BACKOFF_RATIO;
import static BitmapPreFillRunner.INITIAL_BACKOFF_MS;
import static BitmapPreFillRunner.MAX_DURATION_MS;
import static com.bumptech.glide.tests.Util.anyResource;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class BitmapPreFillRunnerTest {
    @Mock
    private Clock clock;

    @Mock
    private BitmapPool pool;

    @Mock
    private MemoryCache cache;

    @Mock
    private Handler mainHandler;

    private final List<Bitmap> addedBitmaps = new ArrayList<>();

    @Test
    public void testAllocatesABitmapPerSizeInAllocationOrder() {
        PreFillType size = new PreFillType.Builder(100).setConfig(ARGB_8888).build();
        final int toAdd = 3;
        Map<PreFillType, Integer> allocationOrder = new HashMap<>();
        allocationOrder.put(size, toAdd);
        BitmapPreFillRunner handler = getHandler(allocationOrder);
        handler.run();
        Bitmap expected = Bitmap.createBitmap(size.getWidth(), size.getHeight(), size.getConfig());
        // TODO(b/20335397): This code was relying on Bitmap equality which Robolectric removed
        // assertThat(addedBitmaps).containsExactly(expected, expected, expected);
    }

    @Test
    public void testAllocatesBitmapsInOrderGivenByAllocationOrder() {
        PreFillType smallWidth = new PreFillType.Builder(50, 100).setConfig(ARGB_8888).build();
        PreFillType smallHeight = new PreFillType.Builder(100, 50).setConfig(RGB_565).build();
        PreFillType[] expectedOrder = new PreFillType[]{ smallWidth, smallHeight, smallWidth, smallHeight };
        HashMap<PreFillType, Integer> allocationOrder = new HashMap<>();
        allocationOrder.put(smallWidth, 2);
        allocationOrder.put(smallHeight, 2);
        BitmapPreFillRunner handler = getHandler(allocationOrder);
        handler.run();
        Bitmap[] expectedBitmaps = new Bitmap[expectedOrder.length];
        for (int i = 0; i < (expectedBitmaps.length); i++) {
            PreFillType current = expectedOrder[i];
            expectedBitmaps[i] = Bitmap.createBitmap(current.getWidth(), current.getHeight(), current.getConfig());
        }
        Bitmap current = addedBitmaps.get(0);
        for (int i = 1; i < (addedBitmaps.size()); i++) {
            Assert.assertNotEquals(current, addedBitmaps.get(i));
            current = addedBitmaps.get(i);
        }
        assertThat(addedBitmaps).hasSize(4);
    }

    @Test
    public void testStopsAllocatingBitmapsUntilNextIdleCallIfAllocationsTakeLongerThanLimit() {
        PreFillType size = new PreFillType.Builder(1).setConfig(ARGB_8888).build();
        Map<PreFillType, Integer> allocationOrder = new HashMap<>();
        allocationOrder.put(size, 3);
        Mockito.when(clock.now()).thenReturn(0L).thenReturn(0L).thenReturn(MAX_DURATION_MS);
        BitmapPreFillRunner handler = getHandler(allocationOrder);
        handler.run();
        assertThat(addedBitmaps).hasSize(1);
        handler.run();
        assertThat(addedBitmaps).hasSize(3);
    }

    @Test
    public void testPreFillHandlerDoesNotPostIfHasNoBitmapsToAllocate() {
        BitmapPreFillRunner handler = getHandler(new HashMap<PreFillType, Integer>());
        handler.run();
        Mockito.verify(mainHandler, Mockito.never()).postDelayed(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.anyLong());
    }

    @Test
    public void testPreFillHandlerPostsIfHasBitmapsToAllocateAfterRunning() {
        PreFillType size = new PreFillType.Builder(1).setConfig(ARGB_8888).build();
        Map<PreFillType, Integer> allocationOrder = new HashMap<>();
        allocationOrder.put(size, 2);
        BitmapPreFillRunner handler = getHandler(allocationOrder);
        Mockito.when(clock.now()).thenReturn(0L).thenReturn(0L).thenReturn(MAX_DURATION_MS);
        handler.run();
        Mockito.verify(mainHandler).postDelayed(ArgumentMatchers.eq(handler), ArgumentMatchers.anyLong());
    }

    @Test
    public void testPreFillHandlerPostsWithBackoffIfHasBitmapsToAllocateAfterRunning() {
        PreFillType size = new PreFillType.Builder(1).setConfig(ARGB_8888).build();
        Map<PreFillType, Integer> allocationOrder = new HashMap<>();
        allocationOrder.put(size, 100);
        BitmapPreFillRunner handler = getHandler(allocationOrder);
        Mockito.when(clock.now()).thenReturn(0L).thenReturn(0L).thenReturn(MAX_DURATION_MS);
        handler.run();
        Mockito.verify(mainHandler).postDelayed(ArgumentMatchers.eq(handler), ArgumentMatchers.eq(INITIAL_BACKOFF_MS));
        Mockito.when(clock.now()).thenReturn(MAX_DURATION_MS).thenReturn(((MAX_DURATION_MS) + ((INITIAL_BACKOFF_MS) * (BACKOFF_RATIO))));
        handler.run();
        Mockito.verify(mainHandler).postDelayed(ArgumentMatchers.eq(handler), ArgumentMatchers.eq(((INITIAL_BACKOFF_MS) * (BACKOFF_RATIO))));
        Mockito.when(clock.now()).thenReturn(0L).thenReturn(MAX_DURATION_MS);
        handler.run();
        Mockito.when(clock.now()).thenReturn(0L).thenReturn(MAX_DURATION_MS);
        handler.run();
        Mockito.when(clock.now()).thenReturn(0L).thenReturn(MAX_DURATION_MS);
        handler.run();
        Mockito.when(clock.now()).thenReturn(0L).thenReturn(MAX_DURATION_MS);
        handler.run();
        Mockito.verify(mainHandler, Mockito.atLeastOnce()).postDelayed(ArgumentMatchers.eq(handler), ArgumentMatchers.eq(MAX_BACKOFF_MS));
    }

    @Test
    public void testPreFillHandlerDoesNotPostIfHasBitmapsButIsCancelled() {
        PreFillType size = new PreFillType.Builder(1).setConfig(ARGB_8888).build();
        Map<PreFillType, Integer> allocationOrder = new HashMap<>();
        allocationOrder.put(size, 2);
        BitmapPreFillRunner handler = getHandler(allocationOrder);
        Mockito.when(clock.now()).thenReturn(0L).thenReturn(0L).thenReturn(MAX_DURATION_MS);
        handler.cancel();
        handler.run();
        Mockito.verify(mainHandler, Mockito.never()).postDelayed(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.anyLong());
    }

    @Test
    public void testAddsBitmapsToMemoryCacheIfMemoryCacheHasEnoughSpaceRemaining() {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);
        Mockito.when(cache.getMaxSize()).thenReturn(Long.valueOf(Util.getBitmapByteSize(bitmap)));
        PreFillType size = new PreFillType.Builder(bitmap.getWidth(), bitmap.getHeight()).setConfig(bitmap.getConfig()).build();
        Map<PreFillType, Integer> allocationOrder = new HashMap<>();
        allocationOrder.put(size, 1);
        getHandler(allocationOrder).run();
        Mockito.verify(cache).put(ArgumentMatchers.any(Key.class), anyResource());
        Mockito.verify(pool, Mockito.never()).put(ArgumentMatchers.any(Bitmap.class));
        // TODO(b/20335397): This code was relying on Bitmap equality which Robolectric removed
        // assertThat(addedBitmaps).containsExactly(bitmap);
    }

    @Test
    public void testAddsBitmapsToBitmapPoolIfMemoryCacheIsFull() {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);
        Mockito.when(cache.getMaxSize()).thenReturn(0L);
        PreFillType size = new PreFillType.Builder(bitmap.getWidth(), bitmap.getHeight()).setConfig(bitmap.getConfig()).build();
        Map<PreFillType, Integer> allocationOrder = new HashMap<>();
        allocationOrder.put(size, 1);
        getHandler(allocationOrder).run();
        Mockito.verify(cache, Mockito.never()).put(ArgumentMatchers.any(Key.class), anyResource());
        // TODO(b/20335397): This code was relying on Bitmap equality which Robolectric removed
        // verify(pool).put(eq(bitmap));
        // assertThat(addedBitmaps).containsExactly(bitmap);
    }

    @Test
    public void testAddsBitmapsToPoolIfMemoryCacheIsNotFullButCannotFitBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);
        Mockito.when(cache.getMaxSize()).thenReturn((((long) (Util.getBitmapByteSize(bitmap))) / 2));
        PreFillType size = new PreFillType.Builder(bitmap.getWidth(), bitmap.getHeight()).setConfig(bitmap.getConfig()).build();
        Map<PreFillType, Integer> allocationOrder = new HashMap<>();
        allocationOrder.put(size, 1);
        getHandler(allocationOrder).run();
        Mockito.verify(cache, Mockito.never()).put(ArgumentMatchers.any(Key.class), anyResource());
        // TODO(b/20335397): This code was relying on Bitmap equality which Robolectric removed
        // verify(pool).put(eq(bitmap));
        // assertThat(addedBitmaps).containsExactly(bitmap);
    }

    @Test
    public void testDoesAGetFromPoolBeforeAddingForEachSize() {
        Bitmap first = Bitmap.createBitmap(100, 100, ARGB_4444);
        PreFillType firstSize = new PreFillType.Builder(first.getWidth(), first.getHeight()).setConfig(first.getConfig()).build();
        Bitmap second = Bitmap.createBitmap(200, 200, RGB_565);
        PreFillType secondSize = new PreFillType.Builder(second.getWidth(), second.getHeight()).setConfig(second.getConfig()).build();
        Map<PreFillType, Integer> allocationOrder = new HashMap<>();
        allocationOrder.put(firstSize, 1);
        allocationOrder.put(secondSize, 1);
        getHandler(allocationOrder).run();
        InOrder firstOrder = Mockito.inOrder(pool);
        firstOrder.verify(pool).getDirty(ArgumentMatchers.eq(first.getWidth()), ArgumentMatchers.eq(first.getHeight()), ArgumentMatchers.eq(first.getConfig()));
        // TODO(b/20335397): This code was relying on Bitmap equality which Robolectric removed
        // firstOrder.verify(pool).put(eq(first));
        InOrder secondOrder = Mockito.inOrder(pool);
        secondOrder.verify(pool).getDirty(ArgumentMatchers.eq(second.getWidth()), ArgumentMatchers.eq(second.getHeight()), ArgumentMatchers.eq(second.getConfig()));
        // TODO(b/20335397): This code was relying on Bitmap equality which Robolectric removed
        // secondOrder.verify(pool).put(eq(second));
    }

    @Test
    public void testDoesNotGetMoreThanOncePerSize() {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_4444);
        PreFillType size = new PreFillType.Builder(bitmap.getWidth(), bitmap.getHeight()).setConfig(bitmap.getConfig()).build();
        final int numBitmaps = 5;
        Map<PreFillType, Integer> allocationOrder = new HashMap<>();
        allocationOrder.put(size, numBitmaps);
        getHandler(allocationOrder).run();
        InOrder order = Mockito.inOrder(pool);
        order.verify(pool).getDirty(ArgumentMatchers.eq(bitmap.getWidth()), ArgumentMatchers.eq(bitmap.getHeight()), ArgumentMatchers.eq(bitmap.getConfig()));
        // TODO(b/20335397): This code was relying on Bitmap equality which Robolectric removed
        // order.verify(pool, times(numBitmaps)).put(eq(bitmap));
    }

    @Test
    public void allocate_whenBitmapPoolIsAtCapacity_doesNotLogWithRecycledBitmap() {
        ShadowLog.setLoggable(TAG, VERBOSE);
        int dimensions = 10;
        Bitmap.Config config = Config.ARGB_8888;
        int bitmapByteSize = Util.getBitmapByteSize(dimensions, dimensions, config);
        PreFillType preFillType = new PreFillType.Builder(dimensions).setConfig(config).build();
        Map<PreFillType, Integer> allocationOrder = new HashMap<>();
        allocationOrder.put(preFillType, 1);
        PreFillQueue queue = new PreFillQueue(allocationOrder);
        BitmapPreFillRunner runner = new BitmapPreFillRunner(new LruBitmapPool((bitmapByteSize - 1)), new MemoryCacheAdapter(), queue);
        runner.allocate();
    }

    private static final class AddBitmapPoolAnswer implements Answer<Void> {
        private final List<Bitmap> bitmaps;

        AddBitmapPoolAnswer(List<Bitmap> bitmaps) {
            this.bitmaps = bitmaps;
        }

        @Override
        public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
            Bitmap bitmap = ((Bitmap) (invocationOnMock.getArguments()[0]));
            bitmaps.add(bitmap);
            return null;
        }
    }

    private static final class AddBitmapCacheAnswer implements Answer<Resource<?>> {
        private final List<Bitmap> bitmaps;

        AddBitmapCacheAnswer(List<Bitmap> bitmaps) {
            this.bitmaps = bitmaps;
        }

        @Override
        public Resource<?> answer(InvocationOnMock invocationOnMock) throws Throwable {
            BitmapResource resource = ((BitmapResource) (invocationOnMock.getArguments()[1]));
            bitmaps.add(resource.get());
            return null;
        }
    }
}

