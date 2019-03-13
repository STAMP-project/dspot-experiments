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


import C.LENGTH_UNSET;
import android.net.Uri;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.testutil.FakeDataSet.FakeData;
import com.google.android.exoplayer2.testutil.FakeDataSource;
import com.google.android.exoplayer2.testutil.TestUtil;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSource;
import java.io.File;
import java.io.IOException;
import java.util.NavigableSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static CacheDataSource.FLAG_BLOCK_ON_CACHE;
import static CacheDataSource.FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS;


/**
 * Unit tests for {@link CacheDataSource}.
 */
@RunWith(RobolectricTestRunner.class)
public final class CacheDataSourceTest {
    private static final byte[] TEST_DATA = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    private static final int MAX_CACHE_FILE_SIZE = 3;

    private static final String CACHE_KEY_PREFIX = "myCacheKeyFactoryPrefix";

    private Uri testDataUri;

    private String fixedCacheKey;

    private String expectedCacheKey;

    private File tempFolder;

    private SimpleCache cache;

    private CacheKeyFactory cacheKeyFactory;

    @Test
    public void testMaxCacheFileSize() throws Exception {
        CacheDataSource cacheDataSource = createCacheDataSource(false, false);
        assertReadDataContentLength(cacheDataSource, false, false);
        for (String key : cache.getKeys()) {
            for (CacheSpan cacheSpan : cache.getCachedSpans(key)) {
                assertThat(((cacheSpan.length) <= (CacheDataSourceTest.MAX_CACHE_FILE_SIZE))).isTrue();
                assertThat(((cacheSpan.file.length()) <= (CacheDataSourceTest.MAX_CACHE_FILE_SIZE))).isTrue();
            }
        }
    }

    @Test
    public void testCacheAndReadUnboundedRequest() throws Exception {
        /* unboundedRequest= */
        /* simulateUnknownLength= */
        assertCacheAndRead(true, false);
    }

    @Test
    public void testCacheAndReadUnknownLength() throws Exception {
        /* unboundedRequest= */
        /* simulateUnknownLength= */
        assertCacheAndRead(false, true);
    }

    @Test
    public void testCacheAndReadUnboundedRequestUnknownLength() throws Exception {
        /* unboundedRequest= */
        /* simulateUnknownLength= */
        assertCacheAndRead(true, true);
    }

    @Test
    public void testCacheAndRead() throws Exception {
        /* unboundedRequest= */
        /* simulateUnknownLength= */
        assertCacheAndRead(false, false);
    }

    @Test
    public void testUnsatisfiableRange() throws Exception {
        // Bounded request but the content length is unknown. This forces all data to be cached but not
        // the length
        /* unboundedRequest= */
        /* simulateUnknownLength= */
        assertCacheAndRead(false, true);
        // Now do an unbounded request. This will read all of the data from cache and then try to read
        // more from upstream which will cause to a 416 so CDS will store the length.
        CacheDataSource cacheDataSource = /* setReadException= */
        /* simulateUnknownLength= */
        createCacheDataSource(true, true);
        /* unboundedRequest= */
        /* unknownLength= */
        assertReadDataContentLength(cacheDataSource, true, true);
        // If the user try to access off range then it should throw an IOException
        try {
            cacheDataSource = /* setReadException= */
            /* simulateUnknownLength= */
            createCacheDataSource(false, false);
            cacheDataSource.open(new DataSpec(testDataUri, CacheDataSourceTest.TEST_DATA.length, 5, fixedCacheKey));
            Assert.fail();
        } catch (IOException e) {
            // success
        }
    }

    @Test
    public void testCacheAndReadUnboundedRequestWithCacheKeyFactoryWithNullDataSpecCacheKey() throws Exception {
        fixedCacheKey = null;
        expectedCacheKey = cacheKeyFactory.buildCacheKey(/* key= */
        new DataSpec(testDataUri, CacheDataSourceTest.TEST_DATA.length, 5, null));
        /* unboundedRequest= */
        /* simulateUnknownLength= */
        assertCacheAndRead(true, false, cacheKeyFactory);
    }

    @Test
    public void testCacheAndReadUnknownLengthWithCacheKeyFactoryOverridingWithNullDataSpecCacheKey() throws Exception {
        fixedCacheKey = null;
        expectedCacheKey = cacheKeyFactory.buildCacheKey(/* key= */
        new DataSpec(testDataUri, CacheDataSourceTest.TEST_DATA.length, 5, null));
        /* unboundedRequest= */
        /* simulateUnknownLength= */
        assertCacheAndRead(false, true, cacheKeyFactory);
    }

    @Test
    public void testCacheAndReadUnboundedRequestUnknownLengthWithCacheKeyFactoryWithNullDataSpecCacheKey() throws Exception {
        fixedCacheKey = null;
        expectedCacheKey = cacheKeyFactory.buildCacheKey(/* key= */
        new DataSpec(testDataUri, CacheDataSourceTest.TEST_DATA.length, 5, null));
        /* unboundedRequest= */
        /* simulateUnknownLength= */
        assertCacheAndRead(true, true, cacheKeyFactory);
    }

    @Test
    public void testCacheAndReadWithCacheKeyFactoryWithNullDataSpecCacheKey() throws Exception {
        fixedCacheKey = null;
        expectedCacheKey = cacheKeyFactory.buildCacheKey(/* key= */
        new DataSpec(testDataUri, CacheDataSourceTest.TEST_DATA.length, 5, null));
        /* unboundedRequest= */
        /* simulateUnknownLength= */
        assertCacheAndRead(false, false, cacheKeyFactory);
    }

    @Test
    public void testUnsatisfiableRangeWithCacheKeyFactoryNullDataSpecCacheKey() throws Exception {
        fixedCacheKey = null;
        expectedCacheKey = cacheKeyFactory.buildCacheKey(/* key= */
        new DataSpec(testDataUri, CacheDataSourceTest.TEST_DATA.length, 5, null));
        // Bounded request but the content length is unknown. This forces all data to be cached but not
        // the length
        /* unboundedRequest= */
        /* simulateUnknownLength= */
        assertCacheAndRead(false, true, cacheKeyFactory);
        // Now do an unbounded request. This will read all of the data from cache and then try to read
        // more from upstream which will cause to a 416 so CDS will store the length.
        CacheDataSource cacheDataSource = /* setReadException= */
        /* simulateUnknownLength= */
        createCacheDataSource(true, true, cacheKeyFactory);
        /* unboundedRequest= */
        /* unknownLength= */
        assertReadDataContentLength(cacheDataSource, true, true);
        // If the user try to access off range then it should throw an IOException
        try {
            cacheDataSource = /* setReadException= */
            /* simulateUnknownLength= */
            createCacheDataSource(false, false, cacheKeyFactory);
            cacheDataSource.open(new DataSpec(testDataUri, CacheDataSourceTest.TEST_DATA.length, 5, fixedCacheKey));
            Assert.fail();
        } catch (IOException e) {
            // success
        }
    }

    @Test
    public void testCacheAndReadUnboundedRequestWithCacheKeyFactoryOverridingDataSpecCacheKey() throws Exception {
        fixedCacheKey = CacheUtil.generateKey(testDataUri);
        expectedCacheKey = cacheKeyFactory.buildCacheKey(new DataSpec(testDataUri, CacheDataSourceTest.TEST_DATA.length, 5, fixedCacheKey));
        assertCacheAndRead(true, false, cacheKeyFactory);
    }

    @Test
    public void testCacheAndReadUnknownLengthWithCacheKeyFactoryOverridingDataSpecCacheKey() throws Exception {
        fixedCacheKey = CacheUtil.generateKey(testDataUri);
        expectedCacheKey = cacheKeyFactory.buildCacheKey(new DataSpec(testDataUri, CacheDataSourceTest.TEST_DATA.length, 5, fixedCacheKey));
        assertCacheAndRead(false, true, cacheKeyFactory);
    }

    @Test
    public void testCacheAndReadUnboundedRequestUnknownLengthWithCacheKeyFactoryOverridingDataSpecCacheKey() throws Exception {
        fixedCacheKey = CacheUtil.generateKey(testDataUri);
        expectedCacheKey = cacheKeyFactory.buildCacheKey(new DataSpec(testDataUri, CacheDataSourceTest.TEST_DATA.length, 5, fixedCacheKey));
        /* unboundedRequest= */
        /* simulateUnknownLength= */
        assertCacheAndRead(true, true, cacheKeyFactory);
    }

    @Test
    public void testCacheAndReadWithCacheKeyFactoryOverridingDataSpecCacheKey() throws Exception {
        fixedCacheKey = CacheUtil.generateKey(testDataUri);
        expectedCacheKey = cacheKeyFactory.buildCacheKey(new DataSpec(testDataUri, CacheDataSourceTest.TEST_DATA.length, 5, fixedCacheKey));
        /* unboundedRequest= */
        /* simulateUnknownLength= */
        assertCacheAndRead(false, false, cacheKeyFactory);
    }

    @Test
    public void testUnsatisfiableRangeWithCacheKeyFactoryOverridingDataSpecCacheKey() throws Exception {
        fixedCacheKey = CacheUtil.generateKey(testDataUri);
        expectedCacheKey = cacheKeyFactory.buildCacheKey(new DataSpec(testDataUri, CacheDataSourceTest.TEST_DATA.length, 5, fixedCacheKey));
        // Bounded request but the content length is unknown. This forces all data to be cached but not
        // the length
        /* unboundedRequest= */
        /* simulateUnknownLength= */
        assertCacheAndRead(false, true, cacheKeyFactory);
        // Now do an unbounded request. This will read all of the data from cache and then try to read
        // more from upstream which will cause to a 416 so CDS will store the length.
        CacheDataSource cacheDataSource = /* setReadException= */
        /* simulateUnknownLength= */
        createCacheDataSource(true, true, cacheKeyFactory);
        /* unboundedRequest= */
        /* unknownLength= */
        assertReadDataContentLength(cacheDataSource, true, true);
        // If the user try to access off range then it should throw an IOException
        try {
            cacheDataSource = /* setReadException= */
            /* simulateUnknownLength= */
            createCacheDataSource(false, false, cacheKeyFactory);
            cacheDataSource.open(new DataSpec(testDataUri, CacheDataSourceTest.TEST_DATA.length, 5, fixedCacheKey));
            Assert.fail();
        } catch (IOException e) {
            // success
        }
    }

    @Test
    public void testContentLengthEdgeCases() throws Exception {
        // Read partial at EOS but don't cross it so length is unknown
        CacheDataSource cacheDataSource = createCacheDataSource(false, true);
        assertReadData(cacheDataSource, true, ((CacheDataSourceTest.TEST_DATA.length) - 2), 2);
        assertThat(cache.getContentLength(expectedCacheKey)).isEqualTo(LENGTH_UNSET);
        // Now do an unbounded request for whole data. This will cause a bounded request from upstream.
        // End of data from upstream shouldn't be mixed up with EOS and cause length set wrong.
        cacheDataSource = createCacheDataSource(false, true);
        assertReadDataContentLength(cacheDataSource, true, true);
        // Now the length set correctly do an unbounded request with offset
        assertThat(cacheDataSource.open(new DataSpec(testDataUri, ((CacheDataSourceTest.TEST_DATA.length) - 2), C.LENGTH_UNSET, expectedCacheKey))).isEqualTo(2);
        // An unbounded request with offset for not cached content
        assertThat(cacheDataSource.open(new DataSpec(Uri.parse("notCachedUri"), ((CacheDataSourceTest.TEST_DATA.length) - 2), C.LENGTH_UNSET, null))).isEqualTo(LENGTH_UNSET);
    }

    @Test
    public void testUnknownLengthContentReadInOneConnectionAndLengthIsResolved() throws Exception {
        FakeDataSource upstream = new FakeDataSource();
        upstream.getDataSet().newData(testDataUri).appendReadData(CacheDataSourceTest.TEST_DATA).setSimulateUnknownLength(true);
        CacheDataSource cacheDataSource = new CacheDataSource(cache, upstream, 0);
        int flags = DataSpec.FLAG_ALLOW_CACHING_UNKNOWN_LENGTH;
        cacheDataSource.open(new DataSpec(testDataUri, 0, C.LENGTH_UNSET, expectedCacheKey, flags));
        TestUtil.readToEnd(cacheDataSource);
        cacheDataSource.close();
        assertThat(upstream.getAndClearOpenedDataSpecs()).hasLength(1);
        assertThat(cache.getContentLength(expectedCacheKey)).isEqualTo(CacheDataSourceTest.TEST_DATA.length);
    }

    @Test
    public void testIgnoreCacheForUnsetLengthRequests() throws Exception {
        FakeDataSource upstream = new FakeDataSource();
        upstream.getDataSet().setData(testDataUri, CacheDataSourceTest.TEST_DATA);
        CacheDataSource cacheDataSource = new CacheDataSource(cache, upstream, FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS);
        cacheDataSource.open(new DataSpec(testDataUri, 0, C.LENGTH_UNSET, expectedCacheKey));
        TestUtil.readToEnd(cacheDataSource);
        cacheDataSource.close();
        assertThat(cache.getKeys()).isEmpty();
    }

    @Test
    public void testReadOnlyCache() throws Exception {
        CacheDataSource cacheDataSource = createCacheDataSource(false, false, 0, null);
        assertReadDataContentLength(cacheDataSource, false, false);
        CacheAsserts.assertCacheEmpty(cache);
    }

    @Test
    public void testSwitchToCacheSourceWithReadOnlyCacheDataSource() throws Exception {
        // Create a fake data source with a 1 MB default data.
        FakeDataSource upstream = new FakeDataSource();
        FakeData fakeData = upstream.getDataSet().newDefaultData().appendReadData(((1024 * 1024) - 1));
        // Insert an action just before the end of the data to fail the test if reading from upstream
        // reaches end of the data.
        fakeData.appendReadAction(() -> fail("Read from upstream shouldn't reach to the end of the data.")).appendReadData(1);
        // Create cache read-only CacheDataSource.
        CacheDataSource cacheDataSource = new CacheDataSource(cache, upstream, new FileDataSource(), null, 0, null);
        // Open source and read some data from upstream as the data hasn't cached yet.
        DataSpec dataSpec = new DataSpec(testDataUri, 0, C.LENGTH_UNSET, fixedCacheKey);
        cacheDataSource.open(dataSpec);
        byte[] buffer = new byte[1024];
        cacheDataSource.read(buffer, 0, buffer.length);
        // Cache the data.
        // Although we use another FakeDataSource instance, it shouldn't matter.
        FakeDataSource upstream2 = new FakeDataSource(new FakeDataSource().getDataSet().newDefaultData().appendReadData((1024 * 1024)).endData());
        /* counters= */
        /* isCanceled= */
        CacheUtil.cache(dataSpec, cache, upstream2, null, null);
        // Read the rest of the data.
        TestUtil.readToEnd(cacheDataSource);
        cacheDataSource.close();
    }

    @Test
    public void testSwitchToCacheSourceWithNonBlockingCacheDataSource() throws Exception {
        // Create a fake data source with a 1 MB default data.
        FakeDataSource upstream = new FakeDataSource();
        FakeData fakeData = upstream.getDataSet().newDefaultData().appendReadData(((1024 * 1024) - 1));
        // Insert an action just before the end of the data to fail the test if reading from upstream
        // reaches end of the data.
        fakeData.appendReadAction(() -> fail("Read from upstream shouldn't reach to the end of the data.")).appendReadData(1);
        // Lock the content on the cache.
        SimpleCacheSpan cacheSpan = cache.startReadWriteNonBlocking(expectedCacheKey, 0);
        assertThat(cacheSpan).isNotNull();
        assertThat(cacheSpan.isHoleSpan()).isTrue();
        // Create non blocking CacheDataSource.
        CacheDataSource cacheDataSource = new CacheDataSource(cache, upstream, 0);
        // Open source and read some data from upstream without writing to cache as the data is locked.
        DataSpec dataSpec = new DataSpec(testDataUri, 0, C.LENGTH_UNSET, fixedCacheKey);
        cacheDataSource.open(dataSpec);
        byte[] buffer = new byte[1024];
        cacheDataSource.read(buffer, 0, buffer.length);
        // Unlock the span.
        cache.releaseHoleSpan(cacheSpan);
        CacheAsserts.assertCacheEmpty(cache);
        // Cache the data.
        // Although we use another FakeDataSource instance, it shouldn't matter.
        FakeDataSource upstream2 = new FakeDataSource(new FakeDataSource().getDataSet().newDefaultData().appendReadData((1024 * 1024)).endData());
        /* counters= */
        /* isCanceled= */
        CacheUtil.cache(dataSpec, cache, upstream2, null, null);
        // Read the rest of the data.
        TestUtil.readToEnd(cacheDataSource);
        cacheDataSource.close();
    }

    @Test
    public void testDeleteCachedWhileReadingFromUpstreamWithReadOnlyCacheDataSourceDoesNotCrash() throws Exception {
        // Create a fake data source with a 1 KB default data.
        FakeDataSource upstream = new FakeDataSource();
        upstream.getDataSet().newDefaultData().appendReadData(1024).endData();
        // Cache the latter half of the data.
        DataSpec dataSpec = new DataSpec(testDataUri, 512, C.LENGTH_UNSET, fixedCacheKey);
        /* counters= */
        /* isCanceled= */
        CacheUtil.cache(dataSpec, cache, upstream, null, null);
        // Create cache read-only CacheDataSource.
        CacheDataSource cacheDataSource = new CacheDataSource(cache, upstream, new FileDataSource(), null, 0, null);
        // Open source and read some data from upstream as the data hasn't cached yet.
        dataSpec = new DataSpec(testDataUri, 0, C.LENGTH_UNSET, fixedCacheKey);
        cacheDataSource.open(dataSpec);
        TestUtil.readExactly(cacheDataSource, 100);
        // Delete cached data.
        CacheUtil.remove(cache, expectedCacheKey);
        CacheAsserts.assertCacheEmpty(cache);
        // Read the rest of the data.
        TestUtil.readToEnd(cacheDataSource);
        cacheDataSource.close();
    }

    @Test
    public void testDeleteCachedWhileReadingFromUpstreamWithBlockingCacheDataSourceDoesNotBlock() throws Exception {
        // Create a fake data source with a 1 KB default data.
        FakeDataSource upstream = new FakeDataSource();
        int dataLength = 1024;
        upstream.getDataSet().newDefaultData().appendReadData(dataLength).endData();
        // Cache the latter half of the data.
        int halfDataLength = 512;
        DataSpec dataSpec = new DataSpec(testDataUri, halfDataLength, C.LENGTH_UNSET, fixedCacheKey);
        /* counters= */
        /* isCanceled= */
        CacheUtil.cache(dataSpec, cache, upstream, null, null);
        // Create blocking CacheDataSource.
        CacheDataSource cacheDataSource = new CacheDataSource(cache, upstream, FLAG_BLOCK_ON_CACHE);
        dataSpec = new DataSpec(testDataUri, 0, C.LENGTH_UNSET, fixedCacheKey);
        cacheDataSource.open(dataSpec);
        // Read the first half from upstream as it hasn't cached yet.
        TestUtil.readExactly(cacheDataSource, halfDataLength);
        // Delete the cached latter half.
        NavigableSet<CacheSpan> cachedSpans = cache.getCachedSpans(expectedCacheKey);
        for (CacheSpan cachedSpan : cachedSpans) {
            if ((cachedSpan.position) >= halfDataLength) {
                cache.removeSpan(cachedSpan);
            }
        }
        // Read the rest of the data.
        TestUtil.readToEnd(cacheDataSource);
        cacheDataSource.close();
    }
}

