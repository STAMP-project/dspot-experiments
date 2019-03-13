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
package org.apache.hadoop.hbase.client;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MediumTests.class, ClientTests.class })
public class TestFlushFromClient {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFlushFromClient.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestFlushFromClient.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static AsyncConnection asyncConn;

    private static final byte[][] SPLITS = new byte[][]{ Bytes.toBytes("3"), Bytes.toBytes("7") };

    private static final List<byte[]> ROWS = Arrays.asList(Bytes.toBytes("1"), Bytes.toBytes("4"), Bytes.toBytes("8"));

    private static final byte[] FAMILY = Bytes.toBytes("f1");

    @Rule
    public TestName name = new TestName();

    public TableName tableName;

    @Test
    public void testFlushTable() throws Exception {
        try (Admin admin = TestFlushFromClient.TEST_UTIL.getAdmin()) {
            admin.flush(tableName);
            Assert.assertFalse(getRegionInfo().stream().anyMatch(( r) -> (r.getMemStoreDataSize()) != 0));
        }
    }

    @Test
    public void testAsyncFlushTable() throws Exception {
        AsyncAdmin admin = TestFlushFromClient.asyncConn.getAdmin();
        admin.flush(tableName).get();
        Assert.assertFalse(getRegionInfo().stream().anyMatch(( r) -> (r.getMemStoreDataSize()) != 0));
    }

    @Test
    public void testFlushRegion() throws Exception {
        try (Admin admin = TestFlushFromClient.TEST_UTIL.getAdmin()) {
            for (HRegion r : getRegionInfo()) {
                admin.flushRegion(r.getRegionInfo().getRegionName());
                TimeUnit.SECONDS.sleep(1);
                Assert.assertEquals(0, r.getMemStoreDataSize());
            }
        }
    }

    @Test
    public void testAsyncFlushRegion() throws Exception {
        AsyncAdmin admin = TestFlushFromClient.asyncConn.getAdmin();
        for (HRegion r : getRegionInfo()) {
            admin.flushRegion(r.getRegionInfo().getRegionName()).get();
            TimeUnit.SECONDS.sleep(1);
            Assert.assertEquals(0, r.getMemStoreDataSize());
        }
    }

    @Test
    public void testFlushRegionServer() throws Exception {
        try (Admin admin = TestFlushFromClient.TEST_UTIL.getAdmin()) {
            for (HRegionServer rs : TestFlushFromClient.TEST_UTIL.getHBaseCluster().getLiveRegionServerThreads().stream().map(JVMClusterUtil.RegionServerThread::getRegionServer).collect(Collectors.toList())) {
                admin.flushRegionServer(rs.getServerName());
                Assert.assertFalse(getRegionInfo(rs).stream().anyMatch(( r) -> (r.getMemStoreDataSize()) != 0));
            }
        }
    }

    @Test
    public void testAsyncFlushRegionServer() throws Exception {
        AsyncAdmin admin = TestFlushFromClient.asyncConn.getAdmin();
        for (HRegionServer rs : TestFlushFromClient.TEST_UTIL.getHBaseCluster().getLiveRegionServerThreads().stream().map(JVMClusterUtil.RegionServerThread::getRegionServer).collect(Collectors.toList())) {
            admin.flushRegionServer(rs.getServerName()).get();
            Assert.assertFalse(getRegionInfo(rs).stream().anyMatch(( r) -> (r.getMemStoreDataSize()) != 0));
        }
    }
}

