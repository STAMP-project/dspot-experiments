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


import CacheConfig.PREFETCH_BLOCKS_ON_OPEN_KEY;
import KeyValue.Type;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ IOTests.class, SmallTests.class })
public class TestPrefetch {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestPrefetch.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final int NUM_VALID_KEY_TYPES = (Type.values().length) - 2;

    private static final int DATA_BLOCK_SIZE = 2048;

    private static final int NUM_KV = 1000;

    private static final Random RNG = new Random();

    private Configuration conf;

    private CacheConfig cacheConf;

    private FileSystem fs;

    private BlockCache blockCache;

    @Test
    public void testPrefetchSetInHCDWorks() {
        ColumnFamilyDescriptor columnFamilyDescriptor = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("f")).setPrefetchBlocksOnOpen(true).build();
        Configuration c = HBaseConfiguration.create();
        Assert.assertFalse(c.getBoolean(PREFETCH_BLOCKS_ON_OPEN_KEY, false));
        CacheConfig cc = new CacheConfig(c, columnFamilyDescriptor, blockCache);
        Assert.assertTrue(cc.shouldPrefetchOnOpen());
    }

    @Test
    public void testPrefetch() throws Exception {
        Path storeFile = writeStoreFile("TestPrefetch");
        readStoreFile(storeFile);
    }

    @Test
    public void testPrefetchRace() throws Exception {
        for (int i = 0; i < 10; i++) {
            Path storeFile = writeStoreFile(("TestPrefetchRace-" + i));
            readStoreFileLikeScanner(storeFile);
        }
    }
}

