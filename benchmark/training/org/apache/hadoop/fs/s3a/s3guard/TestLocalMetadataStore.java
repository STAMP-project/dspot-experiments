/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.s3a.s3guard;


import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;


/**
 * MetadataStore unit test for {@link LocalMetadataStore}.
 */
public class TestLocalMetadataStore extends MetadataStoreTestBase {
    private static final class LocalMSContract extends AbstractMSContract {
        private FileSystem fs;

        private LocalMSContract() throws IOException {
            this(new Configuration());
        }

        private LocalMSContract(Configuration config) throws IOException {
            fs = FileSystem.getLocal(config);
        }

        @Override
        public FileSystem getFileSystem() {
            return fs;
        }

        @Override
        public MetadataStore getMetadataStore() throws IOException {
            LocalMetadataStore lms = new LocalMetadataStore();
            return lms;
        }
    }

    @Test
    public void testClearByAncestor() throws Exception {
        Cache<Path, LocalMetadataEntry> cache = CacheBuilder.newBuilder().build();
        // 1. Test paths without scheme/host
        TestLocalMetadataStore.assertClearResult(cache, "", "/", 0);
        TestLocalMetadataStore.assertClearResult(cache, "", "/dirA/dirB", 2);
        TestLocalMetadataStore.assertClearResult(cache, "", "/invalid", 5);
        // 2. Test paths w/ scheme/host
        String p = "s3a://fake-bucket-name";
        TestLocalMetadataStore.assertClearResult(cache, p, "/", 0);
        TestLocalMetadataStore.assertClearResult(cache, p, "/dirA/dirB", 2);
        TestLocalMetadataStore.assertClearResult(cache, p, "/invalid", 5);
    }

    static class TestTicker extends Ticker {
        private long myTicker = 0;

        @Override
        public long read() {
            return myTicker;
        }

        public void set(long val) {
            this.myTicker = val;
        }
    }

    /**
     * Test that time eviction in cache used in {@link LocalMetadataStore}
     * implementation working properly.
     *
     * The test creates a Ticker instance, which will be used to control the
     * internal clock of the cache to achieve eviction without having to wait
     * for the system clock.
     * The test creates 3 entry: 2nd and 3rd entry will survive the eviction,
     * because it will be created later than the 1st - using the ticker.
     */
    @Test
    public void testCacheTimedEvictionAfterWrite() {
        TestLocalMetadataStore.TestTicker testTicker = new TestLocalMetadataStore.TestTicker();
        final long t0 = testTicker.read();
        final long t1 = t0 + 100;
        final long t2 = t1 + 100;
        final long ttl = t1 + 50;// between t1 and t2

        Cache<Path, LocalMetadataEntry> cache = /* nanos to avoid conversions */
        CacheBuilder.newBuilder().expireAfterWrite(ttl, TimeUnit.NANOSECONDS).ticker(testTicker).build();
        String p = "s3a://fake-bucket-name";
        Path path1 = new Path((p + "/dirA/dirB/file1"));
        Path path2 = new Path((p + "/dirA/dirB/file2"));
        Path path3 = new Path((p + "/dirA/dirB/file3"));
        // Test time is t0
        TestLocalMetadataStore.populateEntry(cache, path1);
        // set new value on the ticker, so the next two entries will be added later
        testTicker.set(t1);// Test time is now t1

        TestLocalMetadataStore.populateEntry(cache, path2);
        TestLocalMetadataStore.populateEntry(cache, path3);
        assertEquals("Cache should contain 3 records before eviction", 3, cache.size());
        LocalMetadataEntry pm1 = cache.getIfPresent(path1);
        assertNotNull("PathMetadata should not be null before eviction", pm1);
        // set the ticker to a time when timed eviction should occur
        // for the first entry
        testTicker.set(t2);
        // call cleanup explicitly, as timed expiration is performed with
        // periodic maintenance during writes and occasionally during reads only
        cache.cleanUp();
        assertEquals("Cache size should be 2 after eviction", 2, cache.size());
        pm1 = cache.getIfPresent(path1);
        assertNull("PathMetadata should be null after eviction", pm1);
    }
}

