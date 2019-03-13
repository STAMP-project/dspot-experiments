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
package org.apache.hadoop.hbase.io.hfile;


import BlockCacheUtil.CachedBlocksByFile;
import HConstants.BUCKET_CACHE_IOENGINE_KEY;
import HConstants.BUCKET_CACHE_SIZE_KEY;
import java.io.IOException;
import java.util.Objects;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static CacheConfig.DEFAULT_IN_MEMORY;


@Category({ IOTests.class, SmallTests.class })
public class TestBlockCacheReporting {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBlockCacheReporting.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestBlockCacheReporting.class);

    private Configuration conf;

    @Test
    public void testBucketCache() throws IOException {
        this.conf.set(BUCKET_CACHE_IOENGINE_KEY, "offheap");
        this.conf.setInt(BUCKET_CACHE_SIZE_KEY, 100);
        BlockCache blockCache = BlockCacheFactory.createBlockCache(this.conf);
        Assert.assertTrue((blockCache instanceof CombinedBlockCache));
        logPerBlock(blockCache);
        final int count = 3;
        addDataAndHits(blockCache, count);
        // The below has no asserts.  It is just exercising toString and toJSON code.
        TestBlockCacheReporting.LOG.info(Objects.toString(blockCache.getStats()));
        BlockCacheUtil.CachedBlocksByFile cbsbf = logPerBlock(blockCache);
        TestBlockCacheReporting.LOG.info(Objects.toString(cbsbf));
        logPerFile(cbsbf);
        bucketCacheReport(blockCache);
        TestBlockCacheReporting.LOG.info(BlockCacheUtil.toJSON(cbsbf));
    }

    @Test
    public void testLruBlockCache() throws IOException {
        CacheConfig cc = new CacheConfig(this.conf);
        Assert.assertTrue(((DEFAULT_IN_MEMORY) == (cc.isInMemory())));
        BlockCache blockCache = BlockCacheFactory.createBlockCache(this.conf);
        Assert.assertTrue((blockCache instanceof LruBlockCache));
        logPerBlock(blockCache);
        addDataAndHits(blockCache, 3);
        // The below has no asserts.  It is just exercising toString and toJSON code.
        TestBlockCacheReporting.LOG.info(((((("count=" + (blockCache.getBlockCount())) + ", currentSize=") + (blockCache.getCurrentSize())) + ", freeSize=") + (blockCache.getFreeSize())));
        TestBlockCacheReporting.LOG.info(Objects.toString(blockCache.getStats()));
        BlockCacheUtil.CachedBlocksByFile cbsbf = logPerBlock(blockCache);
        TestBlockCacheReporting.LOG.info(Objects.toString(cbsbf));
        logPerFile(cbsbf);
        bucketCacheReport(blockCache);
        TestBlockCacheReporting.LOG.info(BlockCacheUtil.toJSON(cbsbf));
    }
}

