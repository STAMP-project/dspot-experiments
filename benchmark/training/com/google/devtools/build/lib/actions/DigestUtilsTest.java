/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.actions;


import DigestHashFunction.MD5;
import DigestHashFunction.SHA1;
import com.google.common.cache.CacheStats;
import com.google.devtools.build.lib.actions.cache.DigestUtils;
import com.google.devtools.build.lib.clock.BlazeClock;
import com.google.devtools.build.lib.vfs.DigestHashFunction;
import com.google.devtools.build.lib.vfs.FileSystem;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.inmemoryfs.InMemoryFileSystem;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.CheckReturnValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for DigestUtils.
 */
@RunWith(JUnit4.class)
public class DigestUtilsTest {
    /**
     * Ensures that digest calculation is synchronized for files greater than
     * {@link DigestUtils#MULTI_THREADED_DIGEST_MAX_FILE_SIZE} bytes if the digest is not
     * available cheaply, so machines with rotating drives don't become unusable.
     */
    @Test
    public void testCalculationConcurrency() throws Exception {
        final int small = DigestUtils.MULTI_THREADED_DIGEST_MAX_FILE_SIZE;
        final int large = (DigestUtils.MULTI_THREADED_DIGEST_MAX_FILE_SIZE) + 1;
        for (DigestHashFunction hf : Arrays.asList(MD5, SHA1)) {
            DigestUtilsTest.assertDigestCalculationConcurrency(true, true, small, small, hf);
            DigestUtilsTest.assertDigestCalculationConcurrency(true, true, large, large, hf);
            DigestUtilsTest.assertDigestCalculationConcurrency(true, false, small, small, hf);
            DigestUtilsTest.assertDigestCalculationConcurrency(true, false, small, large, hf);
            DigestUtilsTest.assertDigestCalculationConcurrency(false, false, large, large, hf);
        }
    }

    /**
     * Helper class to assert the cache statistics.
     */
    private static class CacheStatsChecker {
        /**
         * Cache statistics, grabbed at construction time.
         */
        private final CacheStats stats;

        private int expectedEvictionCount;

        private int expectedHitCount;

        private int expectedMissCount;

        CacheStatsChecker() {
            this.stats = DigestUtils.getCacheStats();
        }

        @CheckReturnValue
        DigestUtilsTest.CacheStatsChecker evictionCount(int count) {
            expectedEvictionCount = count;
            return this;
        }

        @CheckReturnValue
        DigestUtilsTest.CacheStatsChecker hitCount(int count) {
            expectedHitCount = count;
            return this;
        }

        @CheckReturnValue
        DigestUtilsTest.CacheStatsChecker missCount(int count) {
            expectedMissCount = count;
            return this;
        }

        void check() throws Exception {
            assertThat(stats.evictionCount()).isEqualTo(expectedEvictionCount);
            assertThat(stats.hitCount()).isEqualTo(expectedHitCount);
            assertThat(stats.missCount()).isEqualTo(expectedMissCount);
        }
    }

    @Test
    public void testCache() throws Exception {
        final AtomicInteger getFastDigestCounter = new AtomicInteger(0);
        final AtomicInteger getDigestCounter = new AtomicInteger(0);
        FileSystem tracingFileSystem = new InMemoryFileSystem(BlazeClock.instance()) {
            @Override
            protected byte[] getFastDigest(Path path) throws IOException {
                getFastDigestCounter.incrementAndGet();
                return null;
            }

            @Override
            protected byte[] getDigest(Path path) throws IOException {
                getDigestCounter.incrementAndGet();
                return super.getDigest(path);
            }
        };
        DigestUtils.configureCache(2);
        final Path file1 = tracingFileSystem.getPath("/1.txt");
        final Path file2 = tracingFileSystem.getPath("/2.txt");
        final Path file3 = tracingFileSystem.getPath("/3.txt");
        FileSystemUtils.writeContentAsLatin1(file1, "some contents");
        FileSystemUtils.writeContentAsLatin1(file2, "some other contents");
        FileSystemUtils.writeContentAsLatin1(file3, "and something else");
        byte[] digest1 = DigestUtils.getDigestOrFail(file1, file1.getFileSize());
        assertThat(getFastDigestCounter.get()).isEqualTo(1);
        assertThat(getDigestCounter.get()).isEqualTo(1);
        new DigestUtilsTest.CacheStatsChecker().evictionCount(0).hitCount(0).missCount(1).check();
        byte[] digest2 = DigestUtils.getDigestOrFail(file1, file1.getFileSize());
        assertThat(getFastDigestCounter.get()).isEqualTo(2);
        assertThat(getDigestCounter.get()).isEqualTo(1);
        new DigestUtilsTest.CacheStatsChecker().evictionCount(0).hitCount(1).missCount(1).check();
        assertThat(digest2).isEqualTo(digest1);
        // Evict the digest for the previous file.
        DigestUtils.getDigestOrFail(file2, file2.getFileSize());
        DigestUtils.getDigestOrFail(file3, file3.getFileSize());
        new DigestUtilsTest.CacheStatsChecker().evictionCount(1).hitCount(1).missCount(3).check();
        // And now try to recompute it.
        byte[] digest3 = DigestUtils.getDigestOrFail(file1, file1.getFileSize());
        new DigestUtilsTest.CacheStatsChecker().evictionCount(2).hitCount(1).missCount(4).check();
        assertThat(digest3).isEqualTo(digest1);
    }
}

