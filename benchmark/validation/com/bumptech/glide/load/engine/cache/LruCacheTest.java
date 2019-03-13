package com.bumptech.glide.load.engine.cache;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.util.LruCache;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class LruCacheTest {
    // 1MB
    private static final int SIZE = 2;

    private LruCache<String, Object> cache;

    private LruCacheTest.CacheListener listener;

    private String currentKey;

    @Test
    public void testCanAddAndRetrieveItem() {
        String key = getKey();
        Object object = new Object();
        cache.put(key, object);
        Assert.assertEquals(object, cache.get(key));
    }

    @Test
    public void testCanPutNullItemWithoutChangingSize() {
        String key = getKey();
        cache.put(key, null);
        for (int i = 0; i < (LruCacheTest.SIZE); i++) {
            cache.put(getKey(), new Object());
        }
        Mockito.verify(listener, Mockito.never()).onItemRemoved(ArgumentMatchers.anyObject());
    }

    @Test
    public void testReplacingNonNullItemWithNullItemDecreasesSize() {
        String key = getKey();
        Object initialValue = new Object();
        cache.put(key, initialValue);
        cache.put(key, null);
        for (int i = 0; i < (LruCacheTest.SIZE); i++) {
            cache.put(getKey(), new Object());
        }
        Mockito.verify(listener).onItemRemoved(initialValue);
    }

    @Test
    public void testReplacingNullItemWIthNullItemIncreasesSize() {
        String key = getKey();
        cache.put(key, null);
        cache.put(key, new Object());
        for (int i = 0; i < (LruCacheTest.SIZE); i++) {
            cache.put(getKey(), new Object());
        }
        Mockito.verify(listener).onItemRemoved(ArgumentMatchers.anyObject());
    }

    @Test
    public void testReplacingNonNullItemWithNonNullItemUpdatesSize() {
        String key = getKey();
        Object initialValue = new Object();
        cache.put(key, initialValue);
        cache.put(key, new Object());
        for (int i = 0; i < ((LruCacheTest.SIZE) - 1); i++) {
            cache.put(getKey(), new Object());
        }
        Mockito.verify(listener).onItemRemoved(initialValue);
        Mockito.verify(listener, Mockito.never()).onItemRemoved(AdditionalMatchers.not(ArgumentMatchers.eq(initialValue)));
    }

    @Test
    public void testCacheContainsAddedBitmap() {
        final String key = getKey();
        cache.put(key, new Object());
        Assert.assertTrue(cache.contains(key));
    }

    @Test
    public void testEmptyCacheDoesNotContainKey() {
        Assert.assertFalse(cache.contains(getKey()));
    }

    @Test
    public void testItIsSizeLimited() {
        for (int i = 0; i < (LruCacheTest.SIZE); i++) {
            cache.put(getKey(), new Object());
        }
        Mockito.verify(listener, Mockito.never()).onItemRemoved(ArgumentMatchers.anyObject());
        cache.put(getKey(), new Object());
        Mockito.verify(listener).onItemRemoved(ArgumentMatchers.anyObject());
    }

    @Test
    public void testLeastRecentlyAddKeyEvictedFirstIfGetsAreEqual() {
        Object first = new Object();
        cache.put(getKey(), first);
        cache.put(getKey(), new Object());
        cache.put(getKey(), new Object());
        Mockito.verify(listener).onItemRemoved(ArgumentMatchers.eq(first));
        Mockito.verify(listener, Mockito.times(1)).onItemRemoved(ArgumentMatchers.any(Object.class));
    }

    @Test
    public void testLeastRecentlyUsedKeyEvictedFirst() {
        String mostRecentlyUsedKey = getKey();
        Object mostRecentlyUsedObject = new Object();
        String leastRecentlyUsedKey = getKey();
        Object leastRecentlyUsedObject = new Object();
        cache.put(mostRecentlyUsedKey, mostRecentlyUsedObject);
        cache.put(leastRecentlyUsedKey, leastRecentlyUsedObject);
        cache.get(mostRecentlyUsedKey);
        cache.put(getKey(), new Object());
        Mockito.verify(listener).onItemRemoved(ArgumentMatchers.eq(leastRecentlyUsedObject));
        Mockito.verify(listener, Mockito.times(1)).onItemRemoved(ArgumentMatchers.any(Object.class));
    }

    @Test
    public void testItemLargerThanCacheIsImmediatelyEvicted() {
        Object tooLarge = new Object();
        Mockito.when(listener.getSize(ArgumentMatchers.eq(tooLarge))).thenReturn(((LruCacheTest.SIZE) + 1));
        cache.put(getKey(), tooLarge);
        Mockito.verify(listener).onItemRemoved(ArgumentMatchers.eq(tooLarge));
    }

    @Test
    public void testItemLargerThanCacheDoesNotCauseAdditionalEvictions() {
        cache.put(getKey(), new Object());
        Object tooLarge = new Object();
        Mockito.when(listener.getSize(ArgumentMatchers.eq(tooLarge))).thenReturn(((LruCacheTest.SIZE) + 1));
        cache.put(getKey(), tooLarge);
        Mockito.verify(listener, Mockito.times(1)).onItemRemoved(ArgumentMatchers.anyObject());
    }

    @Test
    public void testClearMemoryRemovesAllItems() {
        String first = getKey();
        String second = getKey();
        cache.put(first, new Object());
        cache.put(second, new Object());
        cache.clearMemory();
        Assert.assertFalse(cache.contains(first));
        Assert.assertFalse(cache.contains(second));
    }

    @Test
    public void testCanPutSameItemMultipleTimes() {
        String key = getKey();
        Object value = new Object();
        for (int i = 0; i < ((LruCacheTest.SIZE) * 2); i++) {
            cache.put(key, value);
        }
        Mockito.verify(listener, Mockito.never()).onItemRemoved(ArgumentMatchers.anyObject());
    }

    @Test
    public void put_withSameValueTwice_doesNotEvictItems() {
        String key = getKey();
        Object value = new Object();
        cache.put(key, value);
        cache.put(key, value);
        Mockito.verify(listener, Mockito.never()).onItemRemoved(ArgumentMatchers.anyObject());
    }

    @Test
    public void put_withExistingNullValue_doesNotNotifyListener() {
        String key = getKey();
        /* item= */
        cache.put(key, null);
        cache.put(key, new Object());
        Mockito.verify(listener, Mockito.never()).onItemRemoved(ArgumentMatchers.anyObject());
    }

    @Test
    public void put_withNullValue_withSizeGreaterThanMaximum_notifiesListener() {
        String key = getKey();
        Mockito.when(listener.getSize(null)).thenReturn(((int) ((cache.getMaxSize()) * 2)));
        cache.put(key, null);
        Mockito.verify(listener).onItemRemoved(ArgumentMatchers.anyObject());
    }

    @Test
    public void testCanIncreaseSizeDynamically() {
        int sizeMultiplier = 2;
        cache.setSizeMultiplier(sizeMultiplier);
        for (int i = 0; i < ((LruCacheTest.SIZE) * sizeMultiplier); i++) {
            cache.put(getKey(), new Object());
        }
        Mockito.verify(listener, Mockito.never()).onItemRemoved(ArgumentMatchers.anyObject());
    }

    @Test
    public void testCanDecreaseSizeDynamically() {
        for (int i = 0; i < (LruCacheTest.SIZE); i++) {
            cache.put(getKey(), new Object());
        }
        Mockito.verify(listener, Mockito.never()).onItemRemoved(ArgumentMatchers.anyObject());
        cache.setSizeMultiplier(0.5F);
        Mockito.verify(listener).onItemRemoved(ArgumentMatchers.anyObject());
    }

    @Test
    public void testCanResetSizeDynamically() {
        int sizeMultiplier = 2;
        cache.setSizeMultiplier(sizeMultiplier);
        for (int i = 0; i < ((LruCacheTest.SIZE) * sizeMultiplier); i++) {
            cache.put(getKey(), new Object());
        }
        cache.setSizeMultiplier(1);
        Mockito.verify(listener, Mockito.times(sizeMultiplier)).onItemRemoved(ArgumentMatchers.anyObject());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfMultiplierLessThanZero() {
        cache.setSizeMultiplier((-1));
    }

    @Test
    public void testCanHandleZeroAsMultiplier() {
        for (int i = 0; i < (LruCacheTest.SIZE); i++) {
            cache.put(getKey(), new Object());
        }
        cache.setSizeMultiplier(0);
        Mockito.verify(listener, Mockito.times(LruCacheTest.SIZE)).onItemRemoved(ArgumentMatchers.anyObject());
    }

    @Test
    public void testCanRemoveKeys() {
        String key = getKey();
        Object value = new Object();
        cache.put(key, value);
        cache.remove(key);
        Assert.assertNull(cache.get(key));
        Assert.assertFalse(cache.contains(key));
    }

    @Test
    public void testDecreasesSizeWhenRemovesKey() {
        String key = getKey();
        Object value = new Object();
        cache.put(key, value);
        for (int i = 0; i < ((LruCacheTest.SIZE) - 1); i++) {
            cache.put(getKey(), value);
        }
        cache.remove(key);
        cache.put(key, value);
        Mockito.verify(listener, Mockito.never()).onItemRemoved(ArgumentMatchers.anyObject());
    }

    @Test
    public void testDoesNotCallListenerWhenRemovesKey() {
        String key = getKey();
        cache.put(key, new Object());
        cache.remove(key);
        Mockito.verify(listener, Mockito.never()).onItemRemoved(ArgumentMatchers.anyObject());
    }

    @Test
    public void testGetMaxSizeReturnsCurrentMaxSizeOfCache() {
        Assert.assertEquals(LruCacheTest.SIZE, cache.getMaxSize());
    }

    @Test
    public void testGetMaxSizeChangesIfMaxSizeChanges() {
        int multiplier = 2;
        cache.setSizeMultiplier(multiplier);
        Assert.assertEquals(((LruCacheTest.SIZE) * multiplier), cache.getMaxSize());
    }

    @Test
    public void getCurrentSizeReturnsZeroForEmptyCache() {
        Assert.assertEquals(0, cache.getCurrentSize());
    }

    @Test
    public void testGetCurrentSizeIncreasesAsSizeIncreases() {
        cache.put(getKey(), new Object());
        Assert.assertEquals(1, cache.getCurrentSize());
        cache.put(getKey(), new Object());
        Assert.assertEquals(2, cache.getCurrentSize());
    }

    @Test
    public void testGetCurrentSizeDoesNotChangeWhenSizeMultiplierChangesIfNoItemsAreEvicted() {
        cache.put(getKey(), new Object());
        Assert.assertEquals(1, cache.getCurrentSize());
        cache.setSizeMultiplier(2);
        Assert.assertEquals(1, cache.getCurrentSize());
    }

    @Test
    public void testGetCurrentSizeChangesIfItemsAreEvictedWhenSizeMultiplierChanges() {
        for (int i = 0; i < (LruCacheTest.SIZE); i++) {
            cache.put(getKey(), new Object());
        }
        Assert.assertEquals(LruCacheTest.SIZE, cache.getCurrentSize());
        cache.setSizeMultiplier(0.5F);
        Assert.assertEquals(((LruCacheTest.SIZE) / 2), cache.getCurrentSize());
    }

    private interface CacheListener {
        void onItemRemoved(Object item);

        int getSize(Object item);
    }

    private static class TestLruCache extends LruCache<String, Object> {
        private final LruCacheTest.CacheListener listener;

        TestLruCache(int size, LruCacheTest.CacheListener listener) {
            super(size);
            this.listener = listener;
        }

        @Override
        protected void onItemEvicted(@NonNull
        String key, @Nullable
        Object item) {
            listener.onItemRemoved(item);
        }

        @Override
        protected int getSize(@Nullable
        Object item) {
            return listener.getSize(item);
        }
    }
}

