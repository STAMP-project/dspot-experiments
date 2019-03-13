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
package org.apache.hadoop.hbase.regionserver.wal;


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.wal.WAL;
import org.apache.hadoop.hbase.wal.WALFactory;
import org.apache.hadoop.hbase.wal.WALProvider;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Ensure configuration changes are having an effect on WAL.
 * There is a lot of reflection around WAL setup; could be skipping Configuration changes.
 */
@RunWith(Parameterized.class)
@Category({ RegionServerTests.class, SmallTests.class })
public class TestWALConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(TestWALConfiguration.class);

    static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWALConfiguration.class);

    @Rule
    public TestName name = new TestName();

    @Parameterized.Parameter
    public String walProvider;

    /**
     * Test blocksize change from HBASE-20520 takes on both asycnfs and old wal provider.
     * Hard to verify more than this given the blocksize is passed down to HDFS on create -- not
     * kept local to the streams themselves.
     */
    @Test
    public void testBlocksizeDefaultsToTwiceHDFSBlockSize() throws IOException {
        TableName tableName = TableName.valueOf("test");
        final WALFactory walFactory = new WALFactory(TestWALConfiguration.TEST_UTIL.getConfiguration(), this.walProvider);
        Configuration conf = TestWALConfiguration.TEST_UTIL.getConfiguration();
        WALProvider provider = walFactory.getWALProvider();
        // Get a WAL instance from the provider. Check its blocksize.
        WAL wal = provider.getWAL(null);
        if (wal instanceof AbstractFSWAL) {
            long expectedDefaultBlockSize = WALUtil.getWALBlockSize(conf, FileSystem.get(conf), getDataTestDir());
            long blocksize = ((AbstractFSWAL) (wal)).blocksize;
            Assert.assertEquals(expectedDefaultBlockSize, blocksize);
            TestWALConfiguration.LOG.info("Found blocksize of {} on {}", blocksize, wal);
        } else {
            Assert.fail(("Unknown provider " + provider));
        }
    }
}

