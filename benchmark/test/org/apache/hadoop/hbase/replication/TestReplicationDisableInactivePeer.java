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
package org.apache.hadoop.hbase.replication;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.StartMiniClusterOption;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ ReplicationTests.class, LargeTests.class })
public class TestReplicationDisableInactivePeer extends TestReplicationBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicationDisableInactivePeer.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestReplicationDisableInactivePeer.class);

    /**
     * Test disabling an inactive peer. Add a peer which is inactive, trying to
     * insert, disable the peer, then activate the peer and make sure nothing is
     * replicated. In Addition, enable the peer and check the updates are
     * replicated.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDisableInactivePeer() throws Exception {
        TestReplicationBase.utility2.shutdownMiniHBaseCluster();
        byte[] rowkey = Bytes.toBytes("disable inactive peer");
        Put put = new Put(rowkey);
        put.addColumn(TestReplicationBase.famName, TestReplicationBase.row, TestReplicationBase.row);
        TestReplicationBase.htable1.put(put);
        // wait for the sleep interval of the master cluster to become long
        Thread.sleep(((TestReplicationBase.SLEEP_TIME) * (TestReplicationBase.NB_RETRIES)));
        // disable and start the peer
        TestReplicationBase.admin.disablePeer("2");
        StartMiniClusterOption option = StartMiniClusterOption.builder().numRegionServers(2).build();
        TestReplicationBase.utility2.startMiniHBaseCluster(option);
        Get get = new Get(rowkey);
        for (int i = 0; i < (TestReplicationBase.NB_RETRIES); i++) {
            Result res = TestReplicationBase.htable2.get(get);
            if ((res.size()) >= 1) {
                Assert.fail("Replication wasn't disabled");
            } else {
                TestReplicationDisableInactivePeer.LOG.info("Row not replicated, let's wait a bit more...");
                Thread.sleep(TestReplicationBase.SLEEP_TIME);
            }
        }
        // Test enable replication
        TestReplicationBase.admin.enablePeer("2");
        // wait since the sleep interval would be long
        Thread.sleep(((TestReplicationBase.SLEEP_TIME) * (TestReplicationBase.NB_RETRIES)));
        for (int i = 0; i < (TestReplicationBase.NB_RETRIES); i++) {
            Result res = TestReplicationBase.htable2.get(get);
            if (res.isEmpty()) {
                TestReplicationDisableInactivePeer.LOG.info("Row not available");
                Thread.sleep(((TestReplicationBase.SLEEP_TIME) * (TestReplicationBase.NB_RETRIES)));
            } else {
                Assert.assertArrayEquals(TestReplicationBase.row, res.value());
                return;
            }
        }
        Assert.fail("Waited too much time for put replication");
    }
}

