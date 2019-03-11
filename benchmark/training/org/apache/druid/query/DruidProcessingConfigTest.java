/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query;


import DruidProcessingConfig.MAX_DEFAULT_PROCESSING_BUFFER_SIZE_BYTES;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import java.util.Properties;
import org.apache.druid.utils.RuntimeInfo;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class DruidProcessingConfigTest {
    private static final long bufferSize = (1024L * 1024L) * 1024L;

    private static final int numProcessors = 4;

    private static final long directSize = (DruidProcessingConfigTest.bufferSize) * ((3L + 2L) + 1L);

    private static final long heapSize = (DruidProcessingConfigTest.bufferSize) * 2L;

    @Test
    public void testDefaultsMultiProcessor() {
        Injector injector = DruidProcessingConfigTest.makeInjector(DruidProcessingConfigTest.numProcessors, DruidProcessingConfigTest.directSize, DruidProcessingConfigTest.heapSize);
        DruidProcessingConfig config = injector.getInstance(DruidProcessingConfig.class);
        Assert.assertEquals(Integer.MAX_VALUE, config.poolCacheMaxCount());
        Assert.assertEquals(((DruidProcessingConfigTest.numProcessors) - 1), config.getNumThreads());
        Assert.assertEquals(Math.max(2, ((config.getNumThreads()) / 4)), config.getNumMergeBuffers());
        Assert.assertEquals(0, config.columnCacheSizeBytes());
        Assert.assertFalse(config.isFifo());
        Assert.assertEquals(System.getProperty("java.io.tmpdir"), config.getTmpDir());
        Assert.assertEquals(DruidProcessingConfigTest.bufferSize, config.intermediateComputeSizeBytes());
    }

    @Test
    public void testDefaultsSingleProcessor() {
        Injector injector = DruidProcessingConfigTest.makeInjector(1, ((DruidProcessingConfigTest.bufferSize) * 4L), DruidProcessingConfigTest.heapSize);
        DruidProcessingConfig config = injector.getInstance(DruidProcessingConfig.class);
        Assert.assertEquals(Integer.MAX_VALUE, config.poolCacheMaxCount());
        Assert.assertTrue(((config.getNumThreads()) == 1));
        Assert.assertEquals(Math.max(2, ((config.getNumThreads()) / 4)), config.getNumMergeBuffers());
        Assert.assertEquals(0, config.columnCacheSizeBytes());
        Assert.assertFalse(config.isFifo());
        Assert.assertEquals(System.getProperty("java.io.tmpdir"), config.getTmpDir());
        Assert.assertEquals(DruidProcessingConfigTest.bufferSize, config.intermediateComputeSizeBytes());
    }

    @Test
    public void testDefaultsLargeDirect() {
        // test that auto sized buffer is no larger than 1
        Injector injector = DruidProcessingConfigTest.makeInjector(1, ((DruidProcessingConfigTest.bufferSize) * 100L), DruidProcessingConfigTest.heapSize);
        DruidProcessingConfig config = injector.getInstance(DruidProcessingConfig.class);
        Assert.assertEquals(MAX_DEFAULT_PROCESSING_BUFFER_SIZE_BYTES, config.intermediateComputeSizeBytes());
    }

    @Test
    public void testReplacements() {
        Properties props = new Properties();
        props.setProperty("druid.processing.buffer.sizeBytes", "1");
        props.setProperty("druid.processing.buffer.poolCacheMaxCount", "1");
        props.setProperty("druid.processing.numThreads", "256");
        props.setProperty("druid.processing.columnCache.sizeBytes", "1");
        props.setProperty("druid.processing.fifo", "true");
        props.setProperty("druid.processing.tmpDir", "/test/path");
        Injector injector = DruidProcessingConfigTest.makeInjector(DruidProcessingConfigTest.numProcessors, DruidProcessingConfigTest.directSize, DruidProcessingConfigTest.heapSize, props, ImmutableMap.of("base_path", "druid.processing"));
        DruidProcessingConfig config = injector.getInstance(DruidProcessingConfig.class);
        Assert.assertEquals(1, config.intermediateComputeSizeBytes());// heh

        Assert.assertEquals(1, config.poolCacheMaxCount());
        Assert.assertEquals(256, config.getNumThreads());
        Assert.assertEquals(64, config.getNumMergeBuffers());
        Assert.assertEquals(1, config.columnCacheSizeBytes());
        Assert.assertTrue(config.isFifo());
        Assert.assertEquals("/test/path", config.getTmpDir());
    }

    static class MockRuntimeInfo extends RuntimeInfo {
        private final int availableProcessors;

        private final long maxHeapSize;

        private final long directSize;

        MockRuntimeInfo(int availableProcessors, long directSize, long maxHeapSize) {
            this.availableProcessors = availableProcessors;
            this.directSize = directSize;
            this.maxHeapSize = maxHeapSize;
        }

        @Override
        public int getAvailableProcessors() {
            return availableProcessors;
        }

        @Override
        public long getMaxHeapSizeBytes() {
            return maxHeapSize;
        }

        @Override
        public long getDirectMemorySizeBytes() {
            return directSize;
        }
    }
}

