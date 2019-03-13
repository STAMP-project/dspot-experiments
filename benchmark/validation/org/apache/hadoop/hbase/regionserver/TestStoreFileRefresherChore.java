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


import HConstants.EMPTY_END_ROW;
import HConstants.EMPTY_START_ROW;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.Stoppable;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.StoppableImplementation;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.Mockito;


@Category({ RegionServerTests.class, SmallTests.class })
public class TestStoreFileRefresherChore {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestStoreFileRefresherChore.class);

    private HBaseTestingUtility TEST_UTIL;

    private Path testDir;

    @Rule
    public TestName name = new TestName();

    static class FailingHRegionFileSystem extends HRegionFileSystem {
        boolean fail = false;

        FailingHRegionFileSystem(Configuration conf, FileSystem fs, Path tableDir, RegionInfo regionInfo) {
            super(conf, fs, tableDir, regionInfo);
        }

        @Override
        public Collection<StoreFileInfo> getStoreFiles(String familyName) throws IOException {
            if (fail) {
                throw new IOException("simulating FS failure");
            }
            return super.getStoreFiles(familyName);
        }
    }

    static class StaleStorefileRefresherChore extends StorefileRefresherChore {
        boolean isStale = false;

        public StaleStorefileRefresherChore(int period, HRegionServer regionServer, Stoppable stoppable) {
            super(period, false, regionServer, stoppable);
        }

        @Override
        protected boolean isRegionStale(String encodedName, long time) {
            return isStale;
        }
    }

    @Test
    public void testIsStale() throws IOException {
        int period = 0;
        byte[][] families = new byte[][]{ Bytes.toBytes("cf") };
        byte[] qf = Bytes.toBytes("cq");
        HRegionServer regionServer = Mockito.mock(HRegionServer.class);
        List<HRegion> regions = new ArrayList<>();
        Mockito.when(regionServer.getOnlineRegionsLocalContext()).thenReturn(regions);
        Mockito.when(regionServer.getConfiguration()).thenReturn(TEST_UTIL.getConfiguration());
        TableDescriptor htd = getTableDesc(TableName.valueOf(name.getMethodName()), 2, families);
        HRegion primary = initHRegion(htd, EMPTY_START_ROW, EMPTY_END_ROW, 0);
        HRegion replica1 = initHRegion(htd, EMPTY_START_ROW, EMPTY_END_ROW, 1);
        regions.add(primary);
        regions.add(replica1);
        TestStoreFileRefresherChore.StaleStorefileRefresherChore chore = new TestStoreFileRefresherChore.StaleStorefileRefresherChore(period, regionServer, new StoppableImplementation());
        // write some data to primary and flush
        putData(primary, 0, 100, qf, families);
        primary.flush(true);
        verifyData(primary, 0, 100, qf, families);
        verifyDataExpectFail(replica1, 0, 100, qf, families);
        chore();
        verifyData(replica1, 0, 100, qf, families);
        // simulate an fs failure where we cannot refresh the store files for the replica
        ((TestStoreFileRefresherChore.FailingHRegionFileSystem) (replica1.getRegionFileSystem())).fail = true;
        // write some more data to primary and flush
        putData(primary, 100, 100, qf, families);
        primary.flush(true);
        verifyData(primary, 0, 200, qf, families);
        chore();// should not throw ex, but we cannot refresh the store files

        verifyData(replica1, 0, 100, qf, families);
        verifyDataExpectFail(replica1, 100, 100, qf, families);
        chore.isStale = true;
        chore();// now after this, we cannot read back any value

        try {
            verifyData(replica1, 0, 100, qf, families);
            Assert.fail("should have failed with IOException");
        } catch (IOException ex) {
            // expected
        }
    }
}

