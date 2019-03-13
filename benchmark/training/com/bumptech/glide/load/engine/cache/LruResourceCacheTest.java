package com.bumptech.glide.load.engine.cache;


import ComponentCallbacks2.TRIM_MEMORY_BACKGROUND;
import ComponentCallbacks2.TRIM_MEMORY_MODERATE;
import ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL;
import ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN;
import android.support.annotation.NonNull;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.tests.Util;
import com.bumptech.glide.util.LruCache;
import java.security.MessageDigest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class LruResourceCacheTest {
    @Test
    public void put_withExistingItem_updatesSizeCorrectly() {
        LruResourceCacheTest.PutWithExistingEntryHarness harness = new LruResourceCacheTest.PutWithExistingEntryHarness();
        harness.cache.put(harness.key, harness.first);
        harness.cache.put(harness.key, harness.second);
        assertThat(harness.cache.getCurrentSize()).isEqualTo(harness.second.getSize());
    }

    @Test
    public void put_withExistingItem_evictsExistingItem() {
        LruResourceCacheTest.PutWithExistingEntryHarness harness = new LruResourceCacheTest.PutWithExistingEntryHarness();
        harness.cache.put(harness.key, harness.first);
        harness.cache.put(harness.key, harness.second);
        Mockito.verify(harness.listener).onResourceRemoved(harness.first);
    }

    @Test
    public void get_afterPutWithExistingItem_returnsNewItem() {
        LruResourceCacheTest.PutWithExistingEntryHarness harness = new LruResourceCacheTest.PutWithExistingEntryHarness();
        harness.cache.put(harness.key, harness.first);
        harness.cache.put(harness.key, harness.second);
        assertThat(harness.cache.get(harness.key)).isEqualTo(harness.second);
    }

    @Test
    public void onItemEvicted_withNullValue_doesNotNotifyListener() {
        LruResourceCacheTest.PutWithExistingEntryHarness harness = new LruResourceCacheTest.PutWithExistingEntryHarness();
        harness.cache.onItemEvicted(new LruResourceCacheTest.MockKey(), null);
        Mockito.verify(harness.listener, Mockito.never()).onResourceRemoved(Util.anyResource());
    }

    @Test
    public void clearMemory_afterPutWithExistingItem_evictsOnlyNewItem() {
        LruResourceCacheTest.PutWithExistingEntryHarness harness = new LruResourceCacheTest.PutWithExistingEntryHarness();
        harness.cache.put(harness.key, harness.first);
        harness.cache.put(harness.key, harness.second);
        Mockito.verify(harness.listener).onResourceRemoved(harness.first);
        Mockito.verify(harness.listener, Mockito.never()).onResourceRemoved(harness.second);
        harness.cache.clearMemory();
        Mockito.verify(harness.listener, Mockito.times(1)).onResourceRemoved(harness.first);
        Mockito.verify(harness.listener).onResourceRemoved(harness.second);
    }

    @Test
    public void testTrimMemoryBackground() {
        LruResourceCacheTest.TrimClearMemoryCacheHarness harness = new LruResourceCacheTest.TrimClearMemoryCacheHarness();
        harness.resourceCache.trimMemory(TRIM_MEMORY_BACKGROUND);
        Mockito.verify(harness.listener).onResourceRemoved(ArgumentMatchers.eq(harness.first));
        Mockito.verify(harness.listener).onResourceRemoved(ArgumentMatchers.eq(harness.second));
    }

    @Test
    public void testTrimMemoryModerate() {
        LruResourceCacheTest.TrimClearMemoryCacheHarness harness = new LruResourceCacheTest.TrimClearMemoryCacheHarness();
        harness.resourceCache.trimMemory(TRIM_MEMORY_MODERATE);
        Mockito.verify(harness.listener).onResourceRemoved(harness.first);
        Mockito.verify(harness.listener).onResourceRemoved(harness.second);
    }

    @Test
    public void testTrimMemoryUiHidden() {
        LruResourceCacheTest.TrimClearMemoryCacheHarness harness = new LruResourceCacheTest.TrimClearMemoryCacheHarness();
        harness.resourceCache.trimMemory(TRIM_MEMORY_UI_HIDDEN);
        Mockito.verify(harness.listener).onResourceRemoved(harness.first);
        Mockito.verify(harness.listener, Mockito.never()).onResourceRemoved(harness.second);
    }

    @Test
    public void testTrimMemoryRunningCritical() {
        LruResourceCacheTest.TrimClearMemoryCacheHarness harness = new LruResourceCacheTest.TrimClearMemoryCacheHarness();
        harness.resourceCache.trimMemory(TRIM_MEMORY_RUNNING_CRITICAL);
        Mockito.verify(harness.listener).onResourceRemoved(harness.first);
        Mockito.verify(harness.listener, Mockito.never()).onResourceRemoved(harness.second);
    }

    @Test
    public void testResourceRemovedListenerIsNotifiedWhenResourceIsRemoved() {
        LruResourceCache resourceCache = new LruResourceCache(100);
        Resource<?> resource = Util.mockResource();
        Mockito.when(resource.getSize()).thenReturn(200);
        MemoryCache.ResourceRemovedListener listener = Mockito.mock(MemoryCache.ResourceRemovedListener.class);
        resourceCache.setResourceRemovedListener(listener);
        resourceCache.put(new LruResourceCacheTest.MockKey(), resource);
        Mockito.verify(listener).onResourceRemoved(ArgumentMatchers.eq(resource));
    }

    @Test
    public void testSizeIsBasedOnResource() {
        LruResourceCache resourceCache = new LruResourceCache(100);
        Resource<?> first = getResource(50);
        LruResourceCacheTest.MockKey firstKey = new LruResourceCacheTest.MockKey();
        resourceCache.put(firstKey, first);
        Resource<?> second = getResource(50);
        LruResourceCacheTest.MockKey secondKey = new LruResourceCacheTest.MockKey();
        resourceCache.put(secondKey, second);
        Assert.assertTrue(resourceCache.contains(firstKey));
        Assert.assertTrue(resourceCache.contains(secondKey));
        Resource<?> third = getResource(50);
        LruResourceCacheTest.MockKey thirdKey = new LruResourceCacheTest.MockKey();
        resourceCache.put(thirdKey, third);
        Assert.assertFalse(resourceCache.contains(firstKey));
        Assert.assertTrue(resourceCache.contains(secondKey));
        Assert.assertTrue(resourceCache.contains(thirdKey));
    }

    @Test
    public void testPreventEviction() {
        final MemoryCache cache = new LruResourceCache(100);
        final Resource<?> first = getResource(30);
        final Key firstKey = new LruResourceCacheTest.MockKey();
        cache.put(firstKey, first);
        Resource<?> second = getResource(30);
        Key secondKey = new LruResourceCacheTest.MockKey();
        cache.put(secondKey, second);
        Resource<?> third = getResource(30);
        Key thirdKey = new LruResourceCacheTest.MockKey();
        cache.put(thirdKey, third);
        cache.setResourceRemovedListener(new MemoryCache.ResourceRemovedListener() {
            @Override
            public void onResourceRemoved(@NonNull
            Resource<?> removed) {
                if (removed == first) {
                    cache.put(firstKey, first);
                }
            }
        });
        // trims from 100 to 50, having 30+30+30 items, it should trim to 1 item
        cache.trimMemory(TRIM_MEMORY_UI_HIDDEN);
        // and that 1 item must be first, because it's forced to return to cache in the listener
        @SuppressWarnings("unchecked")
        LruCache<Key, Resource<?>> lruCache = ((LruCache<Key, Resource<?>>) (cache));
        Assert.assertTrue(lruCache.contains(firstKey));
        Assert.assertFalse(lruCache.contains(secondKey));
        Assert.assertFalse(lruCache.contains(thirdKey));
    }

    private static class MockKey implements Key {
        @Override
        public void updateDiskCacheKey(@NonNull
        MessageDigest messageDigest) {
            messageDigest.update(toString().getBytes(CHARSET));
        }
    }

    private static class PutWithExistingEntryHarness {
        final LruResourceCache cache = new LruResourceCache(100);

        final Resource<?> first = Util.mockResource();

        final Resource<?> second = Util.mockResource();

        final MemoryCache.ResourceRemovedListener listener = Mockito.mock(MemoryCache.ResourceRemovedListener.class);

        final Key key = new LruResourceCacheTest.MockKey();

        PutWithExistingEntryHarness() {
            Mockito.when(first.getSize()).thenReturn(50);
            Mockito.when(second.getSize()).thenReturn(50);
            cache.setResourceRemovedListener(listener);
        }
    }

    private static class TrimClearMemoryCacheHarness {
        final LruResourceCache resourceCache = new LruResourceCache(100);

        final Resource<?> first = Util.mockResource();

        final Resource<?> second = Util.mockResource();

        final MemoryCache.ResourceRemovedListener listener = Mockito.mock(MemoryCache.ResourceRemovedListener.class);

        TrimClearMemoryCacheHarness() {
            Mockito.when(first.getSize()).thenReturn(50);
            Mockito.when(second.getSize()).thenReturn(50);
            resourceCache.put(new LruResourceCacheTest.MockKey(), first);
            resourceCache.put(new LruResourceCacheTest.MockKey(), second);
            resourceCache.setResourceRemovedListener(listener);
        }
    }
}

