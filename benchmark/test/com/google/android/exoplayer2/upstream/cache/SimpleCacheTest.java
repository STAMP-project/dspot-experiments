/**
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.upstream.cache;


import C.UTF8_NAME;
import RuntimeEnvironment.application;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.cache.Cache.CacheException;
import com.google.android.exoplayer2.util.Util;
import java.io.File;
import java.util.NavigableSet;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link SimpleCache}.
 */
@RunWith(RobolectricTestRunner.class)
public class SimpleCacheTest {
    private static final String KEY_1 = "key1";

    private static final String KEY_2 = "key2";

    private File cacheDir;

    @Test
    public void testCommittingOneFile() throws Exception {
        SimpleCache simpleCache = getSimpleCache();
        CacheSpan cacheSpan1 = simpleCache.startReadWrite(SimpleCacheTest.KEY_1, 0);
        assertThat(cacheSpan1.isCached).isFalse();
        assertThat(cacheSpan1.isOpenEnded()).isTrue();
        assertThat(simpleCache.startReadWriteNonBlocking(SimpleCacheTest.KEY_1, 0)).isNull();
        NavigableSet<CacheSpan> cachedSpans = simpleCache.getCachedSpans(SimpleCacheTest.KEY_1);
        assertThat(cachedSpans.isEmpty()).isTrue();
        assertThat(simpleCache.getCacheSpace()).isEqualTo(0);
        assertThat(cacheDir.listFiles()).hasLength(0);
        SimpleCacheTest.addCache(simpleCache, SimpleCacheTest.KEY_1, 0, 15);
        Set<String> cachedKeys = simpleCache.getKeys();
        assertThat(cachedKeys).containsExactly(SimpleCacheTest.KEY_1);
        cachedSpans = simpleCache.getCachedSpans(SimpleCacheTest.KEY_1);
        assertThat(cachedSpans).contains(cacheSpan1);
        assertThat(simpleCache.getCacheSpace()).isEqualTo(15);
        simpleCache.releaseHoleSpan(cacheSpan1);
        CacheSpan cacheSpan2 = simpleCache.startReadWrite(SimpleCacheTest.KEY_1, 0);
        assertThat(cacheSpan2.isCached).isTrue();
        assertThat(cacheSpan2.isOpenEnded()).isFalse();
        assertThat(cacheSpan2.length).isEqualTo(15);
        SimpleCacheTest.assertCachedDataReadCorrect(cacheSpan2);
    }

    @Test
    public void testReadCacheWithoutReleasingWriteCacheSpan() throws Exception {
        SimpleCache simpleCache = getSimpleCache();
        CacheSpan cacheSpan1 = simpleCache.startReadWrite(SimpleCacheTest.KEY_1, 0);
        SimpleCacheTest.addCache(simpleCache, SimpleCacheTest.KEY_1, 0, 15);
        CacheSpan cacheSpan2 = simpleCache.startReadWrite(SimpleCacheTest.KEY_1, 0);
        SimpleCacheTest.assertCachedDataReadCorrect(cacheSpan2);
        simpleCache.releaseHoleSpan(cacheSpan1);
    }

    @Test
    public void testSetGetLength() throws Exception {
        SimpleCache simpleCache = getSimpleCache();
        assertThat(simpleCache.getContentLength(SimpleCacheTest.KEY_1)).isEqualTo(C.LENGTH_UNSET);
        simpleCache.setContentLength(SimpleCacheTest.KEY_1, 15);
        assertThat(simpleCache.getContentLength(SimpleCacheTest.KEY_1)).isEqualTo(15);
        simpleCache.startReadWrite(SimpleCacheTest.KEY_1, 0);
        SimpleCacheTest.addCache(simpleCache, SimpleCacheTest.KEY_1, 0, 15);
        simpleCache.setContentLength(SimpleCacheTest.KEY_1, 150);
        assertThat(simpleCache.getContentLength(SimpleCacheTest.KEY_1)).isEqualTo(150);
        SimpleCacheTest.addCache(simpleCache, SimpleCacheTest.KEY_1, 140, 10);
        simpleCache.release();
        // Check if values are kept after cache is reloaded.
        SimpleCache simpleCache2 = getSimpleCache();
        assertThat(simpleCache2.getContentLength(SimpleCacheTest.KEY_1)).isEqualTo(150);
        // Removing the last span shouldn't cause the length be change next time cache loaded
        SimpleCacheSpan lastSpan = simpleCache2.startReadWrite(SimpleCacheTest.KEY_1, 145);
        simpleCache2.removeSpan(lastSpan);
        simpleCache2.release();
        simpleCache2 = getSimpleCache();
        assertThat(simpleCache2.getContentLength(SimpleCacheTest.KEY_1)).isEqualTo(150);
    }

    @Test
    public void testReloadCache() throws Exception {
        SimpleCache simpleCache = getSimpleCache();
        // write data
        CacheSpan cacheSpan1 = simpleCache.startReadWrite(SimpleCacheTest.KEY_1, 0);
        SimpleCacheTest.addCache(simpleCache, SimpleCacheTest.KEY_1, 0, 15);
        simpleCache.releaseHoleSpan(cacheSpan1);
        simpleCache.release();
        // Reload cache
        simpleCache = getSimpleCache();
        // read data back
        CacheSpan cacheSpan2 = simpleCache.startReadWrite(SimpleCacheTest.KEY_1, 0);
        SimpleCacheTest.assertCachedDataReadCorrect(cacheSpan2);
    }

    @Test
    public void testReloadCacheWithoutRelease() throws Exception {
        SimpleCache simpleCache = getSimpleCache();
        // Write data for KEY_1.
        CacheSpan cacheSpan1 = simpleCache.startReadWrite(SimpleCacheTest.KEY_1, 0);
        SimpleCacheTest.addCache(simpleCache, SimpleCacheTest.KEY_1, 0, 15);
        simpleCache.releaseHoleSpan(cacheSpan1);
        // Write and remove data for KEY_2.
        CacheSpan cacheSpan2 = simpleCache.startReadWrite(SimpleCacheTest.KEY_2, 0);
        SimpleCacheTest.addCache(simpleCache, SimpleCacheTest.KEY_2, 0, 15);
        simpleCache.releaseHoleSpan(cacheSpan2);
        simpleCache.removeSpan(simpleCache.getCachedSpans(SimpleCacheTest.KEY_2).first());
        // Don't release the cache. This means the index file wont have been written to disk after the
        // data for KEY_2 was removed. Move the cache instead, so we can reload it without failing the
        // folder locking check.
        File cacheDir2 = Util.createTempFile(application, "ExoPlayerTest");
        cacheDir2.delete();
        cacheDir.renameTo(cacheDir2);
        // Reload the cache from its new location.
        simpleCache = new SimpleCache(cacheDir2, new NoOpCacheEvictor());
        // Read data back for KEY_1.
        CacheSpan cacheSpan3 = simpleCache.startReadWrite(SimpleCacheTest.KEY_1, 0);
        SimpleCacheTest.assertCachedDataReadCorrect(cacheSpan3);
        // Check the entry for KEY_2 was removed when the cache was reloaded.
        assertThat(simpleCache.getCachedSpans(SimpleCacheTest.KEY_2)).isEmpty();
        Util.recursiveDelete(cacheDir2);
    }

    @Test
    public void testEncryptedIndex() throws Exception {
        byte[] key = "Bar12345Bar12345".getBytes(UTF8_NAME);// 128 bit key

        SimpleCache simpleCache = getEncryptedSimpleCache(key);
        // write data
        CacheSpan cacheSpan1 = simpleCache.startReadWrite(SimpleCacheTest.KEY_1, 0);
        SimpleCacheTest.addCache(simpleCache, SimpleCacheTest.KEY_1, 0, 15);
        simpleCache.releaseHoleSpan(cacheSpan1);
        simpleCache.release();
        // Reload cache
        simpleCache = getEncryptedSimpleCache(key);
        // read data back
        CacheSpan cacheSpan2 = simpleCache.startReadWrite(SimpleCacheTest.KEY_1, 0);
        SimpleCacheTest.assertCachedDataReadCorrect(cacheSpan2);
    }

    @Test
    public void testEncryptedIndexWrongKey() throws Exception {
        byte[] key = "Bar12345Bar12345".getBytes(UTF8_NAME);// 128 bit key

        SimpleCache simpleCache = getEncryptedSimpleCache(key);
        // write data
        CacheSpan cacheSpan1 = simpleCache.startReadWrite(SimpleCacheTest.KEY_1, 0);
        SimpleCacheTest.addCache(simpleCache, SimpleCacheTest.KEY_1, 0, 15);
        simpleCache.releaseHoleSpan(cacheSpan1);
        simpleCache.release();
        // Reload cache
        byte[] key2 = "Foo12345Foo12345".getBytes(UTF8_NAME);// 128 bit key

        simpleCache = getEncryptedSimpleCache(key2);
        // Cache should be cleared
        assertThat(simpleCache.getKeys()).isEmpty();
        assertThat(cacheDir.listFiles()).hasLength(0);
    }

    @Test
    public void testEncryptedIndexLostKey() throws Exception {
        byte[] key = "Bar12345Bar12345".getBytes(UTF8_NAME);// 128 bit key

        SimpleCache simpleCache = getEncryptedSimpleCache(key);
        // write data
        CacheSpan cacheSpan1 = simpleCache.startReadWrite(SimpleCacheTest.KEY_1, 0);
        SimpleCacheTest.addCache(simpleCache, SimpleCacheTest.KEY_1, 0, 15);
        simpleCache.releaseHoleSpan(cacheSpan1);
        simpleCache.release();
        // Reload cache
        simpleCache = getSimpleCache();
        // Cache should be cleared
        assertThat(simpleCache.getKeys()).isEmpty();
        assertThat(cacheDir.listFiles()).hasLength(0);
    }

    @Test
    public void testGetCachedLength() throws Exception {
        SimpleCache simpleCache = getSimpleCache();
        CacheSpan cacheSpan = simpleCache.startReadWrite(SimpleCacheTest.KEY_1, 0);
        // No cached bytes, returns -'length'
        assertThat(simpleCache.getCachedLength(SimpleCacheTest.KEY_1, 0, 100)).isEqualTo((-100));
        // Position value doesn't affect the return value
        assertThat(simpleCache.getCachedLength(SimpleCacheTest.KEY_1, 20, 100)).isEqualTo((-100));
        SimpleCacheTest.addCache(simpleCache, SimpleCacheTest.KEY_1, 0, 15);
        // Returns the length of a single span
        assertThat(simpleCache.getCachedLength(SimpleCacheTest.KEY_1, 0, 100)).isEqualTo(15);
        // Value is capped by the 'length'
        assertThat(simpleCache.getCachedLength(SimpleCacheTest.KEY_1, 0, 10)).isEqualTo(10);
        SimpleCacheTest.addCache(simpleCache, SimpleCacheTest.KEY_1, 15, 35);
        // Returns the length of two adjacent spans
        assertThat(simpleCache.getCachedLength(SimpleCacheTest.KEY_1, 0, 100)).isEqualTo(50);
        SimpleCacheTest.addCache(simpleCache, SimpleCacheTest.KEY_1, 60, 10);
        // Not adjacent span doesn't affect return value
        assertThat(simpleCache.getCachedLength(SimpleCacheTest.KEY_1, 0, 100)).isEqualTo(50);
        // Returns length of hole up to the next cached span
        assertThat(simpleCache.getCachedLength(SimpleCacheTest.KEY_1, 55, 100)).isEqualTo((-5));
        simpleCache.releaseHoleSpan(cacheSpan);
    }

    /* Tests https://github.com/google/ExoPlayer/issues/3260 case. */
    @Test
    public void testExceptionDuringEvictionByLeastRecentlyUsedCacheEvictorNotHang() throws Exception {
        CachedContentIndex index = Mockito.spy(new CachedContentIndex(cacheDir));
        SimpleCache simpleCache = new SimpleCache(cacheDir, new LeastRecentlyUsedCacheEvictor(20), index);
        // Add some content.
        CacheSpan cacheSpan = simpleCache.startReadWrite(SimpleCacheTest.KEY_1, 0);
        SimpleCacheTest.addCache(simpleCache, SimpleCacheTest.KEY_1, 0, 15);
        // Make index.store() throw exception from now on.
        Mockito.doAnswer(( invocation) -> {
            throw new CacheException("SimpleCacheTest");
        }).when(index).store();
        // Adding more content will make LeastRecentlyUsedCacheEvictor evict previous content.
        try {
            SimpleCacheTest.addCache(simpleCache, SimpleCacheTest.KEY_1, 15, 15);
            assertWithMessage("Exception was expected").fail();
        } catch (CacheException e) {
            // do nothing.
        }
        simpleCache.releaseHoleSpan(cacheSpan);
        // Although store() has failed, it should remove the first span and add the new one.
        NavigableSet<CacheSpan> cachedSpans = simpleCache.getCachedSpans(SimpleCacheTest.KEY_1);
        assertThat(cachedSpans).isNotEmpty();
        assertThat(cachedSpans).hasSize(1);
        assertThat(cachedSpans.pollFirst().position).isEqualTo(15);
    }

    @Test
    public void testUsingReleasedSimpleCacheThrowsException() throws Exception {
        SimpleCache simpleCache = new SimpleCache(cacheDir, new NoOpCacheEvictor());
        simpleCache.release();
        try {
            simpleCache.startReadWriteNonBlocking(SimpleCacheTest.KEY_1, 0);
            assertWithMessage("Exception was expected").fail();
        } catch (RuntimeException e) {
            // Expected. Do nothing.
        }
    }

    @Test
    public void testMultipleSimpleCacheWithSameCacheDirThrowsException() throws Exception {
        new SimpleCache(cacheDir, new NoOpCacheEvictor());
        try {
            new SimpleCache(cacheDir, new NoOpCacheEvictor());
            assertWithMessage("Exception was expected").fail();
        } catch (IllegalStateException e) {
            // Expected. Do nothing.
        }
    }

    @Test
    public void testMultipleSimpleCacheWithSameCacheDirDoesNotThrowsExceptionAfterRelease() throws Exception {
        SimpleCache simpleCache = new SimpleCache(cacheDir, new NoOpCacheEvictor());
        simpleCache.release();
        new SimpleCache(cacheDir, new NoOpCacheEvictor());
    }
}

