/**
 * Copyright (C) 2017 The Android Open Source Project
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


import C.LENGTH_UNSET;
import CacheUtil.DEFAULT_CACHE_KEY_FACTORY;
import android.net.Uri;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.testutil.FakeDataSet;
import com.google.android.exoplayer2.testutil.FakeDataSource;
import com.google.android.exoplayer2.testutil.TestUtil;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.cache.CacheUtil.CachingCounters;
import java.io.EOFException;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static CacheUtil.DEFAULT_BUFFER_SIZE_BYTES;


/**
 * Tests {@link CacheUtil}.
 */
@RunWith(RobolectricTestRunner.class)
public final class CacheUtilTest {
    /**
     * Abstract fake Cache implementation used by the test. This class must be public so Mockito can
     * create a proxy for it.
     */
    public abstract static class AbstractFakeCache implements Cache {
        // This array is set to alternating length of cached and not cached regions in tests:
        // spansAndGaps = {<length of 1st cached region>, <length of 1st not cached region>,
        // <length of 2nd cached region>, <length of 2nd not cached region>, ... }
        // Ideally it should end with a cached region but it shouldn't matter for any code.
        private int[] spansAndGaps;

        private long contentLength;

        private void init() {
            spansAndGaps = new int[]{  };
            contentLength = C.LENGTH_UNSET;
        }

        @Override
        public long getCachedLength(String key, long position, long length) {
            for (int i = 0; i < (spansAndGaps.length); i++) {
                int spanOrGap = spansAndGaps[i];
                if (position < spanOrGap) {
                    long left = Math.min((spanOrGap - position), length);
                    return (i & 1) == 1 ? -left : left;
                }
                position -= spanOrGap;
            }
            return -length;
        }

        @Override
        public long getContentLength(String key) {
            return contentLength;
        }
    }

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private CacheUtilTest.AbstractFakeCache mockCache;

    private File tempFolder;

    private SimpleCache cache;

    @Test
    public void testGenerateKey() throws Exception {
        assertThat(CacheUtil.generateKey(EMPTY)).isNotNull();
        Uri testUri = Uri.parse("test");
        String key = CacheUtil.generateKey(testUri);
        assertThat(key).isNotNull();
        // Should generate the same key for the same input
        assertThat(CacheUtil.generateKey(testUri)).isEqualTo(key);
        // Should generate different key for different input
        assertThat(key.equals(CacheUtil.generateKey(parse("test2")))).isFalse();
    }

    @Test
    public void testGetKey() throws Exception {
        Uri testUri = Uri.parse("test");
        String key = "key";
        // If DataSpec.key is present, returns it
        assertThat(CacheUtil.getKey(new DataSpec(testUri, 0, C.LENGTH_UNSET, key))).isEqualTo(key);
        // If not generates a new one using DataSpec.uri
        assertThat(CacheUtil.getKey(new DataSpec(testUri, 0, C.LENGTH_UNSET, null))).isEqualTo(CacheUtil.generateKey(testUri));
    }

    @Test
    public void testDefaultCacheKeyFactory_buildCacheKey() throws Exception {
        Uri testUri = Uri.parse("test");
        String key = "key";
        // If DataSpec.key is present, returns it
        assertThat(DEFAULT_CACHE_KEY_FACTORY.buildCacheKey(new DataSpec(testUri, 0, C.LENGTH_UNSET, key))).isEqualTo(key);
        // If not generates a new one using DataSpec.uri
        assertThat(DEFAULT_CACHE_KEY_FACTORY.buildCacheKey(new DataSpec(testUri, 0, C.LENGTH_UNSET, null))).isEqualTo(CacheUtil.generateKey(testUri));
    }

    @Test
    public void testGetCachedNoData() throws Exception {
        CachingCounters counters = new CachingCounters();
        CacheUtil.getCached(new DataSpec(Uri.parse("test")), mockCache, counters);
        CacheUtilTest.assertCounters(counters, 0, 0, LENGTH_UNSET);
    }

    @Test
    public void testGetCachedDataUnknownLength() throws Exception {
        // Mock there is 100 bytes cached at the beginning
        mockCache.spansAndGaps = new int[]{ 100 };
        CachingCounters counters = new CachingCounters();
        CacheUtil.getCached(new DataSpec(Uri.parse("test")), mockCache, counters);
        CacheUtilTest.assertCounters(counters, 100, 0, LENGTH_UNSET);
    }

    @Test
    public void testGetCachedNoDataKnownLength() throws Exception {
        mockCache.contentLength = 1000;
        CachingCounters counters = new CachingCounters();
        CacheUtil.getCached(new DataSpec(Uri.parse("test")), mockCache, counters);
        CacheUtilTest.assertCounters(counters, 0, 0, 1000);
    }

    @Test
    public void testGetCached() throws Exception {
        mockCache.contentLength = 1000;
        mockCache.spansAndGaps = new int[]{ 100, 100, 200 };
        CachingCounters counters = new CachingCounters();
        CacheUtil.getCached(new DataSpec(Uri.parse("test")), mockCache, counters);
        CacheUtilTest.assertCounters(counters, 300, 0, 1000);
    }

    @Test
    public void testCache() throws Exception {
        FakeDataSet fakeDataSet = new FakeDataSet().setRandomData("test_data", 100);
        FakeDataSource dataSource = new FakeDataSource(fakeDataSet);
        CachingCounters counters = new CachingCounters();
        /* isCanceled= */
        CacheUtil.cache(new DataSpec(Uri.parse("test_data")), cache, dataSource, counters, null);
        CacheUtilTest.assertCounters(counters, 0, 100, 100);
        CacheAsserts.assertCachedData(cache, fakeDataSet);
    }

    @Test
    public void testCacheSetOffsetAndLength() throws Exception {
        FakeDataSet fakeDataSet = new FakeDataSet().setRandomData("test_data", 100);
        FakeDataSource dataSource = new FakeDataSource(fakeDataSet);
        Uri testUri = Uri.parse("test_data");
        DataSpec dataSpec = new DataSpec(testUri, 10, 20, null);
        CachingCounters counters = new CachingCounters();
        /* isCanceled= */
        CacheUtil.cache(dataSpec, cache, dataSource, counters, null);
        CacheUtilTest.assertCounters(counters, 0, 20, 20);
        /* isCanceled= */
        CacheUtil.cache(new DataSpec(testUri), cache, dataSource, counters, null);
        CacheUtilTest.assertCounters(counters, 20, 80, 100);
        CacheAsserts.assertCachedData(cache, fakeDataSet);
    }

    @Test
    public void testCacheUnknownLength() throws Exception {
        FakeDataSet fakeDataSet = new FakeDataSet().newData("test_data").setSimulateUnknownLength(true).appendReadData(TestUtil.buildTestData(100)).endData();
        FakeDataSource dataSource = new FakeDataSource(fakeDataSet);
        DataSpec dataSpec = new DataSpec(Uri.parse("test_data"));
        CachingCounters counters = new CachingCounters();
        /* isCanceled= */
        CacheUtil.cache(dataSpec, cache, dataSource, counters, null);
        CacheUtilTest.assertCounters(counters, 0, 100, 100);
        CacheAsserts.assertCachedData(cache, fakeDataSet);
    }

    @Test
    public void testCacheUnknownLengthPartialCaching() throws Exception {
        FakeDataSet fakeDataSet = new FakeDataSet().newData("test_data").setSimulateUnknownLength(true).appendReadData(TestUtil.buildTestData(100)).endData();
        FakeDataSource dataSource = new FakeDataSource(fakeDataSet);
        Uri testUri = Uri.parse("test_data");
        DataSpec dataSpec = new DataSpec(testUri, 10, 20, null);
        CachingCounters counters = new CachingCounters();
        /* isCanceled= */
        CacheUtil.cache(dataSpec, cache, dataSource, counters, null);
        CacheUtilTest.assertCounters(counters, 0, 20, 20);
        /* isCanceled= */
        CacheUtil.cache(new DataSpec(testUri), cache, dataSource, counters, null);
        CacheUtilTest.assertCounters(counters, 20, 80, 100);
        CacheAsserts.assertCachedData(cache, fakeDataSet);
    }

    @Test
    public void testCacheLengthExceedsActualDataLength() throws Exception {
        FakeDataSet fakeDataSet = new FakeDataSet().setRandomData("test_data", 100);
        FakeDataSource dataSource = new FakeDataSource(fakeDataSet);
        Uri testUri = Uri.parse("test_data");
        DataSpec dataSpec = new DataSpec(testUri, 0, 1000, null);
        CachingCounters counters = new CachingCounters();
        /* isCanceled= */
        CacheUtil.cache(dataSpec, cache, dataSource, counters, null);
        CacheUtilTest.assertCounters(counters, 0, 100, 1000);
        CacheAsserts.assertCachedData(cache, fakeDataSet);
    }

    @Test
    public void testCacheThrowEOFException() throws Exception {
        FakeDataSet fakeDataSet = new FakeDataSet().setRandomData("test_data", 100);
        FakeDataSource dataSource = new FakeDataSource(fakeDataSet);
        Uri testUri = Uri.parse("test_data");
        DataSpec dataSpec = new DataSpec(testUri, 0, 1000, null);
        try {
            /* priorityTaskManager= */
            /* priority= */
            /* counters= */
            /* isCanceled= */
            /* enableEOFException= */
            CacheUtil.cache(dataSpec, cache, new CacheDataSource(cache, dataSource), new byte[DEFAULT_BUFFER_SIZE_BYTES], null, 0, null, null, true);
            Assert.fail();
        } catch (EOFException e) {
            // Do nothing.
        }
    }

    @Test
    public void testCachePolling() throws Exception {
        final CachingCounters counters = new CachingCounters();
        FakeDataSet fakeDataSet = new FakeDataSet().newData("test_data").appendReadData(TestUtil.buildTestData(100)).appendReadAction(() -> assertCounters(counters, 0, 100, 300)).appendReadData(TestUtil.buildTestData(100)).appendReadAction(() -> assertCounters(counters, 0, 200, 300)).appendReadData(TestUtil.buildTestData(100)).endData();
        FakeDataSource dataSource = new FakeDataSource(fakeDataSet);
        /* isCanceled= */
        CacheUtil.cache(new DataSpec(Uri.parse("test_data")), cache, dataSource, counters, null);
        CacheUtilTest.assertCounters(counters, 0, 300, 300);
        CacheAsserts.assertCachedData(cache, fakeDataSet);
    }

    @Test
    public void testRemove() throws Exception {
        FakeDataSet fakeDataSet = new FakeDataSet().setRandomData("test_data", 100);
        FakeDataSource dataSource = new FakeDataSource(fakeDataSet);
        Uri uri = Uri.parse("test_data");
        // set maxCacheFileSize to 10 to make sure there are multiple spans
        /* priorityTaskManager= */
        /* priority= */
        /* counters= */
        /* isCanceled= */
        CacheUtil.cache(new DataSpec(uri), cache, new CacheDataSource(cache, dataSource, 0, 10), new byte[DEFAULT_BUFFER_SIZE_BYTES], null, 0, null, null, true);
        CacheUtil.remove(cache, CacheUtil.generateKey(uri));
        CacheAsserts.assertCacheEmpty(cache);
    }
}

