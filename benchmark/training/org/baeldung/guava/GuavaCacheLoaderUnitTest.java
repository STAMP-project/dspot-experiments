package org.baeldung.guava;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.Test;


public class GuavaCacheLoaderUnitTest {
    int callCount = 0;

    @Test
    public void givenAMap_whenAddingValues_thenCanTreatThemAsCache() {
        Map<String, String> cache = Maps.newHashMap();
        cache.put("foo", "cachedValueForFoo");
        cache.put("bar", "cachedValueForBar");
        assertThat(cache.get("foo")).isEqualTo("cachedValueForFoo");
        assertThat(cache.get("bar")).isEqualTo("cachedValueForBar");
    }

    @Test
    public void givenCacheLoader_whenGettingItemTwice_shouldOnlyCallOnce() throws ExecutionException {
        final LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder().build(new CacheLoader<String, String>() {
            @Override
            public String load(final String s) throws Exception {
                return slowMethod(s);
            }
        });
        String value = loadingCache.get("key");
        value = loadingCache.get("key");
        assertThat(callCount).isEqualTo(1);
        assertThat(value).isEqualTo("key");
    }

    @Test
    public void givenCacheLoader_whenRefreshingItem_shouldCallAgain() throws ExecutionException {
        final LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder().build(new CacheLoader<String, String>() {
            @Override
            public String load(final String s) throws Exception {
                return slowMethod(s);
            }
        });
        String value = loadingCache.get("key");
        loadingCache.refresh("key");
        assertThat(callCount).isEqualTo(2);
        assertThat(value).isEqualTo("key");
    }
}

