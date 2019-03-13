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


import Compression.Algorithm.NONE;
import DataBlockEncoding.FAST_DIFF;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@SuppressWarnings("deprecation")
@Category({ RegionServerTests.class, SmallTests.class })
public class TestBlocksScanned {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBlocksScanned.class);

    private static byte[] FAMILY = Bytes.toBytes("family");

    private static byte[] COL = Bytes.toBytes("col");

    private static byte[] START_KEY = Bytes.toBytes("aaa");

    private static byte[] END_KEY = Bytes.toBytes("zzz");

    private static int BLOCK_SIZE = 70;

    private static HBaseTestingUtility TEST_UTIL = null;

    private Configuration conf;

    private Path testDir;

    @Test
    public void testBlocksScanned() throws Exception {
        byte[] tableName = Bytes.toBytes("TestBlocksScanned");
        HTableDescriptor table = new HTableDescriptor(TableName.valueOf(tableName));
        table.addFamily(new HColumnDescriptor(TestBlocksScanned.FAMILY).setMaxVersions(10).setBlockCacheEnabled(true).setBlocksize(TestBlocksScanned.BLOCK_SIZE).setCompressionType(NONE));
        _testBlocksScanned(table);
    }

    @Test
    public void testBlocksScannedWithEncoding() throws Exception {
        byte[] tableName = Bytes.toBytes("TestBlocksScannedWithEncoding");
        HTableDescriptor table = new HTableDescriptor(TableName.valueOf(tableName));
        table.addFamily(new HColumnDescriptor(TestBlocksScanned.FAMILY).setMaxVersions(10).setBlockCacheEnabled(true).setDataBlockEncoding(FAST_DIFF).setBlocksize(TestBlocksScanned.BLOCK_SIZE).setCompressionType(NONE));
        _testBlocksScanned(table);
    }
}

