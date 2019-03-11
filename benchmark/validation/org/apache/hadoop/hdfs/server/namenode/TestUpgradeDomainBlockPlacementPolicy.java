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
package org.apache.hadoop.hdfs.server.namenode;


import DatanodeInfo.AdminStates;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.PermissionStatus;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.DatanodeID;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockPlacementStatus;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocols;
import org.apache.hadoop.hdfs.util.HostsFileWriter;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * End-to-end test case for upgrade domain
 * The test configs upgrade domain for nodes via admin json
 * config file and put some nodes to decommission state.
 * The test then verifies replicas are placed on the nodes that
 * satisfy the upgrade domain policy.
 */
public class TestUpgradeDomainBlockPlacementPolicy {
    private static final short REPLICATION_FACTOR = ((short) (3));

    private static final int DEFAULT_BLOCK_SIZE = 1024;

    static final String[] racks = new String[]{ "/RACK1", "/RACK1", "/RACK1", "/RACK2", "/RACK2", "/RACK2" };

    static final String[] hosts = new String[]{ "host1", "host2", "host3", "host4", "host5", "host6" };

    static final String[] upgradeDomains = new String[]{ "ud5", "ud2", "ud3", "ud1", "ud2", "ud4" };

    static final Set<DatanodeID> expectedDatanodeIDs = new HashSet<>();

    private MiniDFSCluster cluster = null;

    private NamenodeProtocols nameNodeRpc = null;

    private FSNamesystem namesystem = null;

    private PermissionStatus perm = null;

    private HostsFileWriter hostsFileWriter = new HostsFileWriter();

    @Test
    public void testPlacement() throws Exception {
        final long fileSize = (TestUpgradeDomainBlockPlacementPolicy.DEFAULT_BLOCK_SIZE) * 5;
        final String testFile = new String("/testfile");
        final Path path = new Path(testFile);
        DFSTestUtil.createFile(cluster.getFileSystem(), path, fileSize, TestUpgradeDomainBlockPlacementPolicy.REPLICATION_FACTOR, 1000L);
        LocatedBlocks locatedBlocks = cluster.getFileSystem().getClient().getLocatedBlocks(path.toString(), 0, fileSize);
        for (LocatedBlock block : locatedBlocks.getLocatedBlocks()) {
            Set<DatanodeInfo> locs = new HashSet<>();
            for (DatanodeInfo datanodeInfo : block.getLocations()) {
                if ((datanodeInfo.getAdminState()) == (AdminStates.NORMAL)) {
                    locs.add(datanodeInfo);
                }
            }
            for (DatanodeID datanodeID : TestUpgradeDomainBlockPlacementPolicy.expectedDatanodeIDs) {
                Assert.assertTrue(locs.contains(datanodeID));
            }
        }
    }

    @Test(timeout = 300000)
    public void testPlacementAfterDecommission() throws Exception {
        final long fileSize = (TestUpgradeDomainBlockPlacementPolicy.DEFAULT_BLOCK_SIZE) * 5;
        final String testFile = new String("/testfile");
        final Path path = new Path(testFile);
        DFSTestUtil.createFile(cluster.getFileSystem(), path, fileSize, TestUpgradeDomainBlockPlacementPolicy.REPLICATION_FACTOR, 1000L);
        // Decommission some nodes and wait until decommissions have finished.
        refreshDatanodeAdminProperties2();
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                boolean successful = true;
                LocatedBlocks locatedBlocks;
                try {
                    locatedBlocks = cluster.getFileSystem().getClient().getLocatedBlocks(path.toString(), 0, fileSize);
                } catch (IOException ioe) {
                    return false;
                }
                for (LocatedBlock block : locatedBlocks.getLocatedBlocks()) {
                    Set<DatanodeInfo> locs = new HashSet<>();
                    for (DatanodeInfo datanodeInfo : block.getLocations()) {
                        if ((datanodeInfo.getAdminState()) == (AdminStates.NORMAL)) {
                            locs.add(datanodeInfo);
                        }
                    }
                    for (DatanodeID datanodeID : TestUpgradeDomainBlockPlacementPolicy.expectedDatanodeIDs) {
                        successful = successful && (locs.contains(datanodeID));
                    }
                }
                return successful;
            }
        }, 1000, 60000);
        // Verify block placement policy of each block.
        LocatedBlocks locatedBlocks;
        locatedBlocks = cluster.getFileSystem().getClient().getLocatedBlocks(path.toString(), 0, fileSize);
        for (LocatedBlock block : locatedBlocks.getLocatedBlocks()) {
            BlockPlacementStatus status = cluster.getNamesystem().getBlockManager().getBlockPlacementPolicy().verifyBlockPlacement(block.getLocations(), TestUpgradeDomainBlockPlacementPolicy.REPLICATION_FACTOR);
            Assert.assertTrue(status.isPlacementPolicySatisfied());
        }
    }
}

