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
package org.apache.hadoop.hbase.master.assignment;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.zookeeper.MiniZooKeeperCluster;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Testcase for HBASE-20792.
 */
@Category({ LargeTests.class, MasterTests.class })
public class TestRegionMoveAndAbandon {
    private static final Logger LOG = LoggerFactory.getLogger(TestRegionMoveAndAbandon.class);

    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionMoveAndAbandon.class);

    @Rule
    public TestName name = new TestName();

    private HBaseTestingUtility UTIL;

    private MiniHBaseCluster cluster;

    private MiniZooKeeperCluster zkCluster;

    private HRegionServer rs1;

    private HRegionServer rs2;

    private TableName tableName;

    private RegionInfo regionInfo;

    @Test
    public void test() throws Exception {
        TestRegionMoveAndAbandon.LOG.info("Moving {} to {}", regionInfo, rs2.getServerName());
        // Move to RS2
        UTIL.moveRegionAndWait(regionInfo, rs2.getServerName());
        TestRegionMoveAndAbandon.LOG.info("Moving {} to {}", regionInfo, rs1.getServerName());
        // Move to RS1
        UTIL.moveRegionAndWait(regionInfo, rs1.getServerName());
        TestRegionMoveAndAbandon.LOG.info("Killing RS {}", rs1.getServerName());
        // Stop RS1
        cluster.killRegionServer(rs1.getServerName());
        // Region should get moved to RS2
        UTIL.waitTableAvailable(tableName, 30000);
        // Restart the master
        TestRegionMoveAndAbandon.LOG.info("Killing master {}", cluster.getMaster().getServerName());
        cluster.killMaster(cluster.getMaster().getServerName());
        // Stop RS2
        TestRegionMoveAndAbandon.LOG.info("Killing RS {}", rs2.getServerName());
        cluster.killRegionServer(rs2.getServerName());
        // Start up everything again
        TestRegionMoveAndAbandon.LOG.info("Starting cluster");
        UTIL.getMiniHBaseCluster().startMaster();
        UTIL.ensureSomeRegionServersAvailable(2);
        UTIL.waitFor(30000, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                try (Table nsTable = UTIL.getConnection().getTable(tableName)) {
                    // Doesn't matter what we're getting. We just want to make sure we can access the region
                    nsTable.get(new Get(Bytes.toBytes("a")));
                    return true;
                }
            }
        });
    }
}

