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


import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test handling of changes to the number of a peer's regionservers.
 */
@RunWith(Parameterized.class)
@Category({ ReplicationTests.class, LargeTests.class })
public class TestReplicationChangingPeerRegionservers extends TestReplicationBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicationChangingPeerRegionservers.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestReplicationChangingPeerRegionservers.class);

    @Parameterized.Parameter(0)
    public boolean serialPeer;

    @Parameterized.Parameter(1)
    public boolean syncPeer;

    @Test
    public void testChangingNumberOfPeerRegionServers() throws IOException, InterruptedException {
        TestReplicationChangingPeerRegionservers.LOG.info("testSimplePutDelete");
        MiniHBaseCluster peerCluster = TestReplicationBase.utility2.getMiniHBaseCluster();
        int numRS = peerCluster.getRegionServerThreads().size();
        doPutTest(Bytes.toBytes(1));
        int rsToStop = ((peerCluster.getServerWithMeta()) == 0) ? 1 : 0;
        peerCluster.stopRegionServer(rsToStop);
        peerCluster.waitOnRegionServer(rsToStop);
        // Sanity check
        Assert.assertEquals((numRS - 1), peerCluster.getRegionServerThreads().size());
        doPutTest(Bytes.toBytes(2));
        peerCluster.startRegionServer();
        // Sanity check
        Assert.assertEquals(numRS, peerCluster.getRegionServerThreads().size());
        doPutTest(Bytes.toBytes(3));
    }
}

