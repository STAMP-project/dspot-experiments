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
package org.apache.hadoop.hbase.coprocessor.example;


import java.io.IOException;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HStore;
import org.apache.hadoop.hbase.regionserver.RegionServerServices;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.wal.WAL;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(MediumTests.class)
public class TestRefreshHFilesEndpoint extends TestRefreshHFilesBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRefreshHFilesEndpoint.class);

    @Test
    public void testRefreshRegionHFilesEndpoint() throws Exception {
        TestRefreshHFilesBase.setUp(HRegion.class.getName());
        addHFilesToRegions();
        Assert.assertEquals(2, TestRefreshHFilesBase.HTU.getNumHFiles(TestRefreshHFilesBase.TABLE_NAME, TestRefreshHFilesBase.FAMILY));
        callRefreshRegionHFilesEndPoint();
        Assert.assertEquals(4, TestRefreshHFilesBase.HTU.getNumHFiles(TestRefreshHFilesBase.TABLE_NAME, TestRefreshHFilesBase.FAMILY));
    }

    @Test(expected = IOException.class)
    public void testRefreshRegionHFilesEndpointWithException() throws IOException {
        TestRefreshHFilesBase.setUp(TestRefreshHFilesEndpoint.HRegionForRefreshHFilesEP.class.getName());
        callRefreshRegionHFilesEndPoint();
    }

    public static class HRegionForRefreshHFilesEP extends HRegion {
        TestRefreshHFilesEndpoint.HStoreWithFaultyRefreshHFilesAPI store;

        public HRegionForRefreshHFilesEP(final Path tableDir, final WAL wal, final FileSystem fs, final Configuration confParam, final RegionInfo regionInfo, final TableDescriptor htd, final RegionServerServices rsServices) {
            super(tableDir, wal, fs, confParam, regionInfo, htd, rsServices);
        }

        @Override
        public List<HStore> getStores() {
            List<HStore> list = new java.util.ArrayList(stores.size());
            /**
             * This is used to trigger the custom definition (faulty)
             * of refresh HFiles API.
             */
            try {
                if ((this.store) == null) {
                    store = new TestRefreshHFilesEndpoint.HStoreWithFaultyRefreshHFilesAPI(this, ColumnFamilyDescriptorBuilder.of(TestRefreshHFilesBase.FAMILY), this.conf);
                }
                list.add(store);
            } catch (IOException ioe) {
                TestRefreshHFilesBase.LOG.info("Couldn't instantiate custom store implementation", ioe);
            }
            list.addAll(stores.values());
            return list;
        }
    }

    public static class HStoreWithFaultyRefreshHFilesAPI extends HStore {
        public HStoreWithFaultyRefreshHFilesAPI(final HRegion region, final ColumnFamilyDescriptor family, final Configuration confParam) throws IOException {
            super(region, family, confParam);
        }

        @Override
        public void refreshStoreFiles() throws IOException {
            throw new IOException();
        }
    }
}

