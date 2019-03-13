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


import SyncReplicationState.ACTIVE;
import SyncReplicationState.DOWNGRADE_ACTIVE;
import SyncReplicationState.STANDBY;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionServerCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionServerCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionServerObserver;
import org.apache.hadoop.hbase.master.replication.TransitPeerSyncReplicationStateProcedure;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Testcase for HBASE-21441.
 */
@Category({ ReplicationTests.class, LargeTests.class })
public class TestSyncReplicationNewRSJoinBetweenRefreshes extends SyncReplicationTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSyncReplicationNewRSJoinBetweenRefreshes.class);

    private static boolean HALT;

    private static CountDownLatch ARRIVE;

    private static CountDownLatch RESUME;

    public static final class HaltCP implements RegionServerCoprocessor , RegionServerObserver {
        @Override
        public Optional<RegionServerObserver> getRegionServerObserver() {
            return Optional.of(this);
        }

        @Override
        public void postExecuteProcedures(ObserverContext<RegionServerCoprocessorEnvironment> ctx) throws IOException {
            synchronized(TestSyncReplicationNewRSJoinBetweenRefreshes.HaltCP.class) {
                if (!(TestSyncReplicationNewRSJoinBetweenRefreshes.HALT)) {
                    return;
                }
                SyncReplicationTestBase.UTIL1.getMiniHBaseCluster().getMaster().getProcedures().stream().filter(( p) -> p instanceof TransitPeerSyncReplicationStateProcedure).filter(( p) -> !(p.isFinished())).map(( p) -> ((TransitPeerSyncReplicationStateProcedure) (p))).findFirst().ifPresent(( proc) -> {
                    // this is the next state of REFRESH_PEER_SYNC_REPLICATION_STATE_ON_RS_BEGIN_VALUE
                    if ((proc.getCurrentStateId()) == REOPEN_ALL_REGIONS_IN_PEER_VALUE) {
                        // tell the main thread to start a new region server
                        org.apache.hadoop.hbase.replication.ARRIVE.countDown();
                        try {
                            // wait for the region server to online
                            org.apache.hadoop.hbase.replication.RESUME.await();
                        } catch ( e) {
                            throw new <e>RuntimeException();
                        }
                        org.apache.hadoop.hbase.replication.HALT = false;
                    }
                });
            }
        }
    }

    @Test
    public void test() throws IOException, InterruptedException {
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, STANDBY);
        SyncReplicationTestBase.UTIL1.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, ACTIVE);
        TestSyncReplicationNewRSJoinBetweenRefreshes.ARRIVE = new CountDownLatch(1);
        TestSyncReplicationNewRSJoinBetweenRefreshes.RESUME = new CountDownLatch(1);
        TestSyncReplicationNewRSJoinBetweenRefreshes.HALT = true;
        Thread t = new Thread(() -> {
            try {
                SyncReplicationTestBase.UTIL1.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, DOWNGRADE_ACTIVE);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        t.start();
        TestSyncReplicationNewRSJoinBetweenRefreshes.ARRIVE.await();
        SyncReplicationTestBase.UTIL1.getMiniHBaseCluster().startRegionServer();
        TestSyncReplicationNewRSJoinBetweenRefreshes.RESUME.countDown();
        t.join();
        Assert.assertEquals(DOWNGRADE_ACTIVE, SyncReplicationTestBase.UTIL1.getAdmin().getReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID));
    }
}

