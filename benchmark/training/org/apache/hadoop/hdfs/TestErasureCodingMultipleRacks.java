/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs;


import BlockPlacementPolicy.LOG;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


/**
 * Test erasure coding block placement with skewed # nodes per rack.
 */
public class TestErasureCodingMultipleRacks {
    public static final Logger LOG = LoggerFactory.getLogger(TestErasureCodingMultipleRacks.class);

    static {
        GenericTestUtils.setLogLevel(BlockPlacementPolicy.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(BlockPlacementPolicyDefault.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(BlockPlacementPolicyRackFaultTolerant.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(NetworkTopology.LOG, Level.DEBUG);
    }

    @Rule
    public Timeout globalTimeout = new Timeout(300000);

    private MiniDFSCluster cluster;

    private ErasureCodingPolicy ecPolicy;

    private Configuration conf;

    private DistributedFileSystem dfs;

    // Extreme case.
    @Test
    public void testSkewedRack1() throws Exception {
        final int dataUnits = ecPolicy.getNumDataUnits();
        final int parityUnits = ecPolicy.getNumParityUnits();
        setupCluster((dataUnits + parityUnits), 2, 1);
        final int filesize = (ecPolicy.getNumDataUnits()) * (ecPolicy.getCellSize());
        byte[] contents = new byte[filesize];
        final Path path = new Path("/testfile");
        TestErasureCodingMultipleRacks.LOG.info(("Writing file " + path));
        DFSTestUtil.writeFile(dfs, path, contents);
        BlockLocation[] blocks = dfs.getFileBlockLocations(path, 0, Long.MAX_VALUE);
        Assert.assertEquals(((ecPolicy.getNumDataUnits()) + (ecPolicy.getNumParityUnits())), blocks[0].getHosts().length);
    }

    // 1 rack has many nodes, other racks have single node. Extreme case.
    @Test
    public void testSkewedRack2() throws Exception {
        final int dataUnits = ecPolicy.getNumDataUnits();
        final int parityUnits = ecPolicy.getNumParityUnits();
        setupCluster((dataUnits + (parityUnits * 2)), dataUnits, (dataUnits - 1));
        final int filesize = (ecPolicy.getNumDataUnits()) * (ecPolicy.getCellSize());
        byte[] contents = new byte[filesize];
        final Path path = new Path("/testfile");
        TestErasureCodingMultipleRacks.LOG.info(("Writing file " + path));
        DFSTestUtil.writeFile(dfs, path, contents);
        BlockLocation[] blocks = dfs.getFileBlockLocations(path, 0, Long.MAX_VALUE);
        Assert.assertEquals(((ecPolicy.getNumDataUnits()) + (ecPolicy.getNumParityUnits())), blocks[0].getHosts().length);
    }

    // 2 racks have sufficient nodes, other racks has 1. Should be able to
    // tolerate 1 rack failure.
    @Test
    public void testSkewedRack3() throws Exception {
        final int dataUnits = ecPolicy.getNumDataUnits();
        final int parityUnits = ecPolicy.getNumParityUnits();
        // Create enough extra DNs on the 2 racks to test even placement.
        // Desired placement is parityUnits replicas on the 2 racks, and 1 replica
        // on the rest of the racks (which only have 1 DN)
        int numRacks = (dataUnits - parityUnits) + 2;
        setupCluster((dataUnits + (parityUnits * 4)), numRacks, (dataUnits - parityUnits));
        final int filesize = (ecPolicy.getNumDataUnits()) * (ecPolicy.getCellSize());
        byte[] contents = new byte[filesize];
        for (int i = 0; i < 10; ++i) {
            final Path path = new Path(("/testfile" + i));
            TestErasureCodingMultipleRacks.LOG.info(("Writing file " + path));
            DFSTestUtil.writeFile(dfs, path, contents);
            ExtendedBlock extendedBlock = DFSTestUtil.getFirstBlock(dfs, path);
            // Wait for replication to finish before testing
            DFSTestUtil.waitForReplication(cluster, extendedBlock, numRacks, ((ecPolicy.getNumDataUnits()) + (ecPolicy.getNumParityUnits())), 0);
            BlockLocation[] blocks = dfs.getFileBlockLocations(path, 0, Long.MAX_VALUE);
            Assert.assertEquals(((ecPolicy.getNumDataUnits()) + (ecPolicy.getNumParityUnits())), blocks[0].getHosts().length);
            assertRackFailureTolerated(blocks[0].getTopologyPaths());
        }
    }
}

