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


import CreateFlag.APPEND;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockPlacementPolicy;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


public class TestFavoredNodesEndToEnd {
    {
        GenericTestUtils.setLogLevel(LoggerFactory.getLogger(BlockPlacementPolicy.class), Level.TRACE);
    }

    private static MiniDFSCluster cluster;

    private static Configuration conf;

    private static final int NUM_DATA_NODES = 10;

    private static final int NUM_FILES = 10;

    private static final byte[] SOME_BYTES = new String("foo").getBytes();

    private static DistributedFileSystem dfs;

    private static ArrayList<DataNode> datanodes;

    @Test(timeout = 180000)
    public void testFavoredNodesEndToEnd() throws Exception {
        // create 10 files with random preferred nodes
        for (int i = 0; i < (TestFavoredNodesEndToEnd.NUM_FILES); i++) {
            Random rand = new Random(((System.currentTimeMillis()) + i));
            // pass a new created rand so as to get a uniform distribution each time
            // without too much collisions (look at the do-while loop in getDatanodes)
            InetSocketAddress[] datanode = getDatanodes(rand);
            Path p = new Path(("/filename" + i));
            FSDataOutputStream out = TestFavoredNodesEndToEnd.dfs.create(p, FsPermission.getDefault(), true, 4096, ((short) (3)), 4096L, null, datanode);
            out.write(TestFavoredNodesEndToEnd.SOME_BYTES);
            out.close();
            BlockLocation[] locations = getBlockLocations(p);
            // verify the files got created in the right nodes
            for (BlockLocation loc : locations) {
                String[] hosts = loc.getNames();
                String[] hosts1 = getStringForInetSocketAddrs(datanode);
                Assert.assertTrue(compareNodes(hosts, hosts1));
            }
        }
    }

    @Test(timeout = 180000)
    public void testWhenFavoredNodesNotPresent() throws Exception {
        // when we ask for favored nodes but the nodes are not there, we should
        // get some other nodes. In other words, the write to hdfs should not fail
        // and if we do getBlockLocations on the file, we should see one blklocation
        // and three hosts for that
        InetSocketAddress[] arbitraryAddrs = new InetSocketAddress[3];
        for (int i = 0; i < 3; i++) {
            arbitraryAddrs[i] = getArbitraryLocalHostAddr();
        }
        Path p = new Path("/filename-foo-bar");
        FSDataOutputStream out = TestFavoredNodesEndToEnd.dfs.create(p, FsPermission.getDefault(), true, 4096, ((short) (3)), 4096L, null, arbitraryAddrs);
        out.write(TestFavoredNodesEndToEnd.SOME_BYTES);
        out.close();
        getBlockLocations(p);
    }

    @Test(timeout = 180000)
    public void testWhenSomeNodesAreNotGood() throws Exception {
        // 4 favored nodes
        final InetSocketAddress[] addrs = new InetSocketAddress[4];
        final String[] hosts = new String[addrs.length];
        for (int i = 0; i < (addrs.length); i++) {
            addrs[i] = TestFavoredNodesEndToEnd.datanodes.get(i).getXferAddress();
            hosts[i] = ((addrs[i].getAddress().getHostAddress()) + ":") + (addrs[i].getPort());
        }
        // make some datanode not "good" so that even if the client prefers it,
        // the namenode would not give it as a replica to write to
        DatanodeInfo d = TestFavoredNodesEndToEnd.cluster.getNameNode().getNamesystem().getBlockManager().getDatanodeManager().getDatanodeByXferAddr(addrs[0].getAddress().getHostAddress(), addrs[0].getPort());
        // set the decommission status to true so that
        // BlockPlacementPolicyDefault.isGoodTarget returns false for this dn
        d.setDecommissioned();
        Path p = new Path("/filename-foo-bar-baz");
        final short replication = ((short) (3));
        FSDataOutputStream out = TestFavoredNodesEndToEnd.dfs.create(p, FsPermission.getDefault(), true, 4096, replication, 4096L, null, addrs);
        out.write(TestFavoredNodesEndToEnd.SOME_BYTES);
        out.close();
        // reset the state
        d.stopDecommission();
        BlockLocation[] locations = getBlockLocations(p);
        Assert.assertEquals(replication, locations[0].getNames().length);
        // also make sure that the datanode[0] is not in the list of hosts
        for (int i = 0; i < replication; i++) {
            final String loc = locations[0].getNames()[i];
            int j = 0;
            for (; (j < (hosts.length)) && (!(loc.equals(hosts[j]))); j++);
            Assert.assertTrue(("j=" + j), (j > 0));
            Assert.assertTrue(((((("loc=" + loc) + " not in host list ") + (Arrays.asList(hosts))) + ", j=") + j), (j < (hosts.length)));
        }
    }

    @Test(timeout = 180000)
    public void testFavoredNodesEndToEndForAppend() throws Exception {
        // create 10 files with random preferred nodes
        for (int i = 0; i < (TestFavoredNodesEndToEnd.NUM_FILES); i++) {
            Random rand = new Random(((System.currentTimeMillis()) + i));
            // pass a new created rand so as to get a uniform distribution each time
            // without too much collisions (look at the do-while loop in getDatanodes)
            InetSocketAddress[] datanode = getDatanodes(rand);
            Path p = new Path(("/filename" + i));
            // create and close the file.
            TestFavoredNodesEndToEnd.dfs.create(p, FsPermission.getDefault(), true, 4096, ((short) (3)), 4096L, null, null).close();
            // re-open for append
            FSDataOutputStream out = TestFavoredNodesEndToEnd.dfs.append(p, EnumSet.of(APPEND), 4096, null, datanode);
            out.write(TestFavoredNodesEndToEnd.SOME_BYTES);
            out.close();
            BlockLocation[] locations = getBlockLocations(p);
            // verify the files got created in the right nodes
            for (BlockLocation loc : locations) {
                String[] hosts = loc.getNames();
                String[] hosts1 = getStringForInetSocketAddrs(datanode);
                Assert.assertTrue(compareNodes(hosts, hosts1));
            }
        }
    }

    @Test(timeout = 180000)
    public void testCreateStreamBuilderFavoredNodesEndToEnd() throws Exception {
        // create 10 files with random preferred nodes
        for (int i = 0; i < (TestFavoredNodesEndToEnd.NUM_FILES); i++) {
            Random rand = new Random(((System.currentTimeMillis()) + i));
            // pass a new created rand so as to get a uniform distribution each time
            // without too much collisions (look at the do-while loop in getDatanodes)
            InetSocketAddress[] dns = getDatanodes(rand);
            Path p = new Path(("/filename" + i));
            FSDataOutputStream out = TestFavoredNodesEndToEnd.dfs.createFile(p).favoredNodes(dns).build();
            out.write(TestFavoredNodesEndToEnd.SOME_BYTES);
            out.close();
            BlockLocation[] locations = getBlockLocations(p);
            // verify the files got created in the right nodes
            for (BlockLocation loc : locations) {
                String[] hosts = loc.getNames();
                String[] hosts1 = getStringForInetSocketAddrs(dns);
                Assert.assertTrue(compareNodes(hosts, hosts1));
            }
        }
    }
}

