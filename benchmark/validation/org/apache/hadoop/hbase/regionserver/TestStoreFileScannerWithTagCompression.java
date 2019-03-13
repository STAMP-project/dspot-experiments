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
package org.apache.hadoop.hbase.regionserver;


import DataBlockEncoding.PREFIX;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValueUtil;
import org.apache.hadoop.hbase.PrivateCellUtil;
import org.apache.hadoop.hbase.Tag;
import org.apache.hadoop.hbase.io.hfile.CacheConfig;
import org.apache.hadoop.hbase.io.hfile.HFileContext;
import org.apache.hadoop.hbase.io.hfile.HFileContextBuilder;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ RegionServerTests.class, SmallTests.class })
public class TestStoreFileScannerWithTagCompression {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestStoreFileScannerWithTagCompression.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static Configuration conf = TestStoreFileScannerWithTagCompression.TEST_UTIL.getConfiguration();

    private static CacheConfig cacheConf = new CacheConfig(TestStoreFileScannerWithTagCompression.TEST_UTIL.getConfiguration());

    private static String ROOT_DIR = getDataTestDir("TestStoreFileScannerWithTagCompression").toString();

    private static FileSystem fs = null;

    @Test
    public void testReseek() throws Exception {
        // write the file
        Path f = new Path(TestStoreFileScannerWithTagCompression.ROOT_DIR, "testReseek");
        HFileContext meta = new HFileContextBuilder().withBlockSize((8 * 1024)).withIncludesTags(true).withCompressTags(true).withDataBlockEncoding(PREFIX).build();
        // Make a store file and write data to it.
        StoreFileWriter writer = new StoreFileWriter.Builder(TestStoreFileScannerWithTagCompression.conf, TestStoreFileScannerWithTagCompression.cacheConf, TestStoreFileScannerWithTagCompression.fs).withFilePath(f).withFileContext(meta).build();
        writeStoreFile(writer);
        writer.close();
        StoreFileReader reader = new StoreFileReader(TestStoreFileScannerWithTagCompression.fs, f, TestStoreFileScannerWithTagCompression.cacheConf, true, new AtomicInteger(0), true, TestStoreFileScannerWithTagCompression.conf);
        StoreFileScanner s = reader.getStoreFileScanner(false, false, false, 0, 0, false);
        try {
            // Now do reseek with empty KV to position to the beginning of the file
            KeyValue k = KeyValueUtil.createFirstOnRow(Bytes.toBytes("k2"));
            s.reseek(k);
            Cell kv = s.next();
            kv = s.next();
            kv = s.next();
            byte[] key5 = Bytes.toBytes("k5");
            Assert.assertTrue(Bytes.equals(key5, 0, key5.length, kv.getRowArray(), kv.getRowOffset(), kv.getRowLength()));
            List<Tag> tags = PrivateCellUtil.getTags(kv);
            Assert.assertEquals(1, tags.size());
            Assert.assertEquals("tag3", Bytes.toString(Tag.cloneValue(tags.get(0))));
        } finally {
            s.close();
        }
    }
}

