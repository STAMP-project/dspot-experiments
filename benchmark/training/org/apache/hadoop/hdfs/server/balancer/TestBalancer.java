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
package org.apache.hadoop.hdfs.server.balancer;


import Balancer.BALANCER_ID_PATH;
import Balancer.Cli;
import Balancer.LOG;
import BalancerParameters.Builder;
import BalancerParameters.DEFAULT;
import BalancingPolicy.Node.INSTANCE;
import DFSConfigKeys.DFS_BALANCER_GETBLOCKS_MIN_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_BALANCER_MAX_ITERATION_TIME_KEY;
import DFSConfigKeys.DFS_BALANCER_MOVERTHREADS_KEY;
import DFSConfigKeys.DFS_BLOCK_REPLICATOR_CLASSNAME_KEY;
import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_BYTES_PER_CHECKSUM_KEY;
import DFSConfigKeys.DFS_CLIENT_SOCKET_TIMEOUT_KEY;
import DFSConfigKeys.DFS_DATANODE_BALANCE_BANDWIDTHPERSEC_KEY;
import DFSConfigKeys.DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY;
import DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY;
import DFSConfigKeys.DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY;
import DFSConfigKeys.DFS_REPLICATION_KEY;
import ExitStatus.IN_PROGRESS;
import ExitStatus.IO_EXCEPTION;
import ExitStatus.NO_MOVE_BLOCK;
import ExitStatus.NO_MOVE_PROGRESS;
import ExitStatus.SUCCESS;
import ExitStatus.UNFINALIZED_UPGRADE;
import HdfsConstants.ONESSD_STORAGE_POLICY_NAME;
import HdfsConstants.RollingUpgradeAction.FINALIZE;
import HdfsConstants.RollingUpgradeAction.PREPARE;
import HdfsConstants.SafeModeAction.SAFEMODE_ENTER;
import HdfsConstants.SafeModeAction.SAFEMODE_LEAVE;
import java.io.File;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.NameNodeProxies;
import org.apache.hadoop.hdfs.StripedFileTestUtil;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.server.balancer.Balancer.Result;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockPlacementPolicy;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockPlacementPolicyWithUpgradeDomain;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.PlatformAssumptions;
import org.apache.hadoop.util.Time;
import org.apache.hadoop.util.Tool;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import static BalancerParameters.DEFAULT;


/**
 * This class tests if a balancer schedules tasks correctly.
 */
public class TestBalancer {
    private static final Logger LOG = LoggerFactory.getLogger(TestBalancer.class);

    static {
        GenericTestUtils.setLogLevel(Balancer.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(Dispatcher.LOG, Level.DEBUG);
    }

    static final long CAPACITY = 5000L;

    static final String RACK0 = "/rack0";

    static final String RACK1 = "/rack1";

    static final String RACK2 = "/rack2";

    private static final String fileName = "/tmp.txt";

    static final Path filePath = new Path(TestBalancer.fileName);

    private static final String username = "balancer";

    private static String principal;

    private static File baseDir;

    private static String keystoresDir;

    private static String sslConfDir;

    private static MiniKdc kdc;

    private static File keytabFile;

    private MiniDFSCluster cluster;

    ClientProtocol client;

    static final long TIMEOUT = 40000L;// msec


    static final double CAPACITY_ALLOWED_VARIANCE = 0.005;// 0.5%


    static final double BALANCE_ALLOWED_VARIANCE = 0.11;// 10%+delta


    static final int DEFAULT_BLOCK_SIZE = 100;

    static final int DEFAULT_RAM_DISK_BLOCK_SIZE = (5 * 1024) * 1024;

    private static final Random r = new Random();

    static {
        TestBalancer.initTestSetup();
    }

    private final ErasureCodingPolicy ecPolicy = StripedFileTestUtil.getDefaultECPolicy();

    private final int dataBlocks = ecPolicy.getNumDataUnits();

    private final int parityBlocks = ecPolicy.getNumParityUnits();

    private final int groupSize = (dataBlocks) + (parityBlocks);

    private final int cellSize = ecPolicy.getCellSize();

    private final int stripesPerBlock = 4;

    private final int defaultBlockSize = (cellSize) * (stripesPerBlock);

    /**
     * Make sure that balancer can't move pinned blocks.
     * If specified favoredNodes when create file, blocks will be pinned use
     * sticky bit.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 100000)
    public void testBalancerWithPinnedBlocks() throws Exception {
        // This test assumes stick-bit based block pin mechanism available only
        // in Linux/Unix. It can be unblocked on Windows when HDFS-7759 is ready to
        // provide a different mechanism for Windows.
        PlatformAssumptions.assumeNotWindows();
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        conf.setBoolean(DFSConfigKeys.DFS_DATANODE_BLOCK_PINNING_ENABLED, true);
        long[] capacities = new long[]{ TestBalancer.CAPACITY, TestBalancer.CAPACITY };
        String[] hosts = new String[]{ "host0", "host1" };
        String[] racks = new String[]{ TestBalancer.RACK0, TestBalancer.RACK1 };
        int numOfDatanodes = capacities.length;
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(capacities.length).hosts(hosts).racks(racks).simulatedCapacities(capacities).build();
        cluster.waitActive();
        client = NameNodeProxies.createProxy(conf, cluster.getFileSystem(0).getUri(), ClientProtocol.class).getProxy();
        // fill up the cluster to be 80% full
        long totalCapacity = TestBalancer.sum(capacities);
        long totalUsedSpace = (totalCapacity * 8) / 10;
        InetSocketAddress[] favoredNodes = new InetSocketAddress[numOfDatanodes];
        for (int i = 0; i < (favoredNodes.length); i++) {
            // DFSClient will attempt reverse lookup. In case it resolves
            // "127.0.0.1" to "localhost", we manually specify the hostname.
            int port = cluster.getDataNodes().get(i).getXferAddress().getPort();
            favoredNodes[i] = new InetSocketAddress(hosts[i], port);
        }
        DFSTestUtil.createFile(cluster.getFileSystem(0), TestBalancer.filePath, false, 1024, (totalUsedSpace / numOfDatanodes), TestBalancer.DEFAULT_BLOCK_SIZE, ((short) (numOfDatanodes)), 0, false, favoredNodes);
        // start up an empty node with the same capacity
        cluster.startDataNodes(conf, 1, true, null, new String[]{ TestBalancer.RACK2 }, new long[]{ TestBalancer.CAPACITY });
        totalCapacity += TestBalancer.CAPACITY;
        // run balancer and validate results
        TestBalancer.waitForHeartBeat(totalUsedSpace, totalCapacity, client, cluster);
        // start rebalancing
        Collection<URI> namenodes = DFSUtil.getInternalNsRpcUris(conf);
        int r = Balancer.run(namenodes, DEFAULT, conf);
        Assert.assertEquals(NO_MOVE_PROGRESS.getExitCode(), r);
    }

    /**
     * Verify balancer won't violate the default block placement policy.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 100000)
    public void testRackPolicyAfterBalance() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        long[] capacities = new long[]{ TestBalancer.CAPACITY, TestBalancer.CAPACITY };
        String[] hosts = new String[]{ "host0", "host1" };
        String[] racks = new String[]{ TestBalancer.RACK0, TestBalancer.RACK1 };
        runBalancerAndVerifyBlockPlacmentPolicy(conf, capacities, hosts, racks, null, TestBalancer.CAPACITY, "host2", TestBalancer.RACK1, null);
    }

    /**
     * Verify balancer won't violate upgrade domain block placement policy.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 100000)
    public void testUpgradeDomainPolicyAfterBalance() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        conf.setClass(DFS_BLOCK_REPLICATOR_CLASSNAME_KEY, BlockPlacementPolicyWithUpgradeDomain.class, BlockPlacementPolicy.class);
        long[] capacities = new long[]{ TestBalancer.CAPACITY, TestBalancer.CAPACITY, TestBalancer.CAPACITY };
        String[] hosts = new String[]{ "host0", "host1", "host2" };
        String[] racks = new String[]{ TestBalancer.RACK0, TestBalancer.RACK1, TestBalancer.RACK1 };
        String[] UDs = new String[]{ "ud0", "ud1", "ud2" };
        runBalancerAndVerifyBlockPlacmentPolicy(conf, capacities, hosts, racks, UDs, TestBalancer.CAPACITY, "host3", TestBalancer.RACK2, "ud2");
    }

    /**
     * Class which contains information about the
     * new nodes to be added to the cluster for balancing.
     */
    abstract static class NewNodeInfo {
        Set<String> nodesToBeExcluded = new HashSet<String>();

        Set<String> nodesToBeIncluded = new HashSet<String>();

        abstract String[] getNames();

        abstract int getNumberofNewNodes();

        abstract int getNumberofIncludeNodes();

        abstract int getNumberofExcludeNodes();

        public Set<String> getNodesToBeIncluded() {
            return nodesToBeIncluded;
        }

        public Set<String> getNodesToBeExcluded() {
            return nodesToBeExcluded;
        }
    }

    /**
     * The host names of new nodes are specified
     */
    static class HostNameBasedNodes extends TestBalancer.NewNodeInfo {
        String[] hostnames;

        public HostNameBasedNodes(String[] hostnames, Set<String> nodesToBeExcluded, Set<String> nodesToBeIncluded) {
            this.hostnames = hostnames;
            this.nodesToBeExcluded = nodesToBeExcluded;
            this.nodesToBeIncluded = nodesToBeIncluded;
        }

        @Override
        String[] getNames() {
            return hostnames;
        }

        @Override
        int getNumberofNewNodes() {
            return hostnames.length;
        }

        @Override
        int getNumberofIncludeNodes() {
            return nodesToBeIncluded.size();
        }

        @Override
        int getNumberofExcludeNodes() {
            return nodesToBeExcluded.size();
        }
    }

    /**
     * The number of data nodes to be started are specified.
     * The data nodes will have same host name, but different port numbers.
     */
    static class PortNumberBasedNodes extends TestBalancer.NewNodeInfo {
        int newNodes;

        int excludeNodes;

        int includeNodes;

        public PortNumberBasedNodes(int newNodes, int excludeNodes, int includeNodes) {
            this.newNodes = newNodes;
            this.excludeNodes = excludeNodes;
            this.includeNodes = includeNodes;
        }

        @Override
        String[] getNames() {
            return null;
        }

        @Override
        int getNumberofNewNodes() {
            return newNodes;
        }

        @Override
        int getNumberofIncludeNodes() {
            return includeNodes;
        }

        @Override
        int getNumberofExcludeNodes() {
            return excludeNodes;
        }
    }

    @Test(timeout = 100000)
    public void testUnknownDatanodeSimple() throws Exception {
        Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        testUnknownDatanode(conf);
    }

    /**
     * Test parse method in Balancer#Cli class with threshold value out of
     * boundaries.
     */
    @Test(timeout = 100000)
    public void testBalancerCliParseWithThresholdOutOfBoundaries() {
        String[] parameters = new String[]{ "-threshold", "0" };
        String reason = "IllegalArgumentException is expected when threshold value" + " is out of boundary.";
        try {
            Cli.parse(parameters);
            Assert.fail(reason);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Number out of range: threshold = 0.0", e.getMessage());
        }
        parameters = new String[]{ "-threshold", "101" };
        try {
            Cli.parse(parameters);
            Assert.fail(reason);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Number out of range: threshold = 101.0", e.getMessage());
        }
    }

    /**
     * Test a cluster with even distribution,
     * then a new empty node is added to the cluster
     */
    @Test(timeout = 100000)
    public void testBalancer0() throws Exception {
        testBalancer0Internal(new HdfsConfiguration());
    }

    /**
     * Test unevenly distributed cluster
     */
    @Test(timeout = 100000)
    public void testBalancer1() throws Exception {
        testBalancer1Internal(new HdfsConfiguration());
    }

    @Test(expected = HadoopIllegalArgumentException.class)
    public void testBalancerWithZeroThreadsForMove() throws Exception {
        Configuration conf = new HdfsConfiguration();
        conf.setInt(DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY, 0);
        testBalancer1Internal(conf);
    }

    @Test(timeout = 100000)
    public void testBalancerWithNonZeroThreadsForMove() throws Exception {
        Configuration conf = new HdfsConfiguration();
        conf.setInt(DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY, 8);
        testBalancer1Internal(conf);
    }

    @Test(timeout = 100000)
    public void testBalancer2() throws Exception {
        testBalancer2Internal(new HdfsConfiguration());
    }

    /**
     * Test parse method in Balancer#Cli class with wrong number of params
     */
    @Test(timeout = 100000)
    public void testBalancerCliParseWithWrongParams() {
        String[] parameters = new String[]{ "-threshold" };
        String reason = "IllegalArgumentException is expected when value is not specified";
        try {
            Cli.parse(parameters);
            Assert.fail(reason);
        } catch (IllegalArgumentException e) {
        }
        parameters = new String[]{ "-policy" };
        try {
            Cli.parse(parameters);
            Assert.fail(reason);
        } catch (IllegalArgumentException e) {
        }
        parameters = new String[]{ "-threshold", "1", "-policy" };
        try {
            Cli.parse(parameters);
            Assert.fail(reason);
        } catch (IllegalArgumentException e) {
        }
        parameters = new String[]{ "-threshold", "1", "-include" };
        try {
            Cli.parse(parameters);
            Assert.fail(reason);
        } catch (IllegalArgumentException e) {
        }
        parameters = new String[]{ "-threshold", "1", "-exclude" };
        try {
            Cli.parse(parameters);
            Assert.fail(reason);
        } catch (IllegalArgumentException e) {
        }
        parameters = new String[]{ "-include", "-f" };
        try {
            Cli.parse(parameters);
            Assert.fail(reason);
        } catch (IllegalArgumentException e) {
        }
        parameters = new String[]{ "-exclude", "-f" };
        try {
            Cli.parse(parameters);
            Assert.fail(reason);
        } catch (IllegalArgumentException e) {
        }
        parameters = new String[]{ "-include", "testnode1", "-exclude", "testnode2" };
        try {
            Cli.parse(parameters);
            Assert.fail("IllegalArgumentException is expected when both -exclude and -include are specified");
        } catch (IllegalArgumentException e) {
        }
        parameters = new String[]{ "-blockpools" };
        try {
            Cli.parse(parameters);
            Assert.fail(("IllegalArgumentException is expected when a value " + "is not specified for the blockpool flag"));
        } catch (IllegalArgumentException e) {
        }
        parameters = new String[]{ "-source" };
        try {
            Cli.parse(parameters);
            Assert.fail((reason + " for -source parameter"));
        } catch (IllegalArgumentException ignored) {
            // expected
        }
    }

    @Test
    public void testBalancerCliParseBlockpools() {
        String[] parameters = new String[]{ "-blockpools", "bp-1,bp-2,bp-3" };
        BalancerParameters p = Cli.parse(parameters);
        Assert.assertEquals(3, p.getBlockPools().size());
        parameters = new String[]{ "-blockpools", "bp-1" };
        p = Cli.parse(parameters);
        Assert.assertEquals(1, p.getBlockPools().size());
        parameters = new String[]{ "-blockpools", "bp-1,,bp-2" };
        p = Cli.parse(parameters);
        Assert.assertEquals(3, p.getBlockPools().size());
        parameters = new String[]{ "-blockpools", "bp-1," };
        p = Cli.parse(parameters);
        Assert.assertEquals(1, p.getBlockPools().size());
    }

    /**
     * Verify balancer exits 0 on success.
     */
    @Test(timeout = 100000)
    public void testExitZeroOnSuccess() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        oneNodeTest(conf, true);
    }

    /**
     * Test a cluster with even distribution,
     * then three nodes are added to the cluster,
     * runs balancer with two of the nodes in the exclude list
     */
    @Test(timeout = 100000)
    public void testBalancerWithExcludeList() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        Set<String> excludeHosts = new HashSet<String>();
        excludeHosts.add("datanodeY");
        excludeHosts.add("datanodeZ");
        doTest(conf, new long[]{ TestBalancer.CAPACITY, TestBalancer.CAPACITY }, new String[]{ TestBalancer.RACK0, TestBalancer.RACK1 }, TestBalancer.CAPACITY, TestBalancer.RACK2, new TestBalancer.HostNameBasedNodes(new String[]{ "datanodeX", "datanodeY", "datanodeZ" }, excludeHosts, DEFAULT.getIncludedNodes()), false, false);
    }

    /**
     * Test a cluster with even distribution,
     * then three nodes are added to the cluster,
     * runs balancer with two of the nodes in the exclude list
     */
    @Test(timeout = 100000)
    public void testBalancerWithExcludeListWithPorts() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        doTest(conf, new long[]{ TestBalancer.CAPACITY, TestBalancer.CAPACITY }, new String[]{ TestBalancer.RACK0, TestBalancer.RACK1 }, TestBalancer.CAPACITY, TestBalancer.RACK2, new TestBalancer.PortNumberBasedNodes(3, 2, 0), false, false);
    }

    /**
     * Test a cluster with even distribution,
     * then three nodes are added to the cluster,
     * runs balancer with two of the nodes in the exclude list
     */
    @Test(timeout = 100000)
    public void testBalancerCliWithExcludeList() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        Set<String> excludeHosts = new HashSet<String>();
        excludeHosts.add("datanodeY");
        excludeHosts.add("datanodeZ");
        doTest(conf, new long[]{ TestBalancer.CAPACITY, TestBalancer.CAPACITY }, new String[]{ TestBalancer.RACK0, TestBalancer.RACK1 }, TestBalancer.CAPACITY, TestBalancer.RACK2, new TestBalancer.HostNameBasedNodes(new String[]{ "datanodeX", "datanodeY", "datanodeZ" }, excludeHosts, DEFAULT.getIncludedNodes()), true, false);
    }

    /**
     * Test a cluster with even distribution,
     * then three nodes are added to the cluster,
     * runs balancer with two of the nodes in the exclude list
     */
    @Test(timeout = 100000)
    public void testBalancerCliWithExcludeListWithPorts() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        doTest(conf, new long[]{ TestBalancer.CAPACITY, TestBalancer.CAPACITY }, new String[]{ TestBalancer.RACK0, TestBalancer.RACK1 }, TestBalancer.CAPACITY, TestBalancer.RACK2, new TestBalancer.PortNumberBasedNodes(3, 2, 0), true, false);
    }

    /**
     * Test a cluster with even distribution,
     * then three nodes are added to the cluster,
     * runs balancer with two of the nodes in the exclude list in a file
     */
    @Test(timeout = 100000)
    public void testBalancerCliWithExcludeListInAFile() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        Set<String> excludeHosts = new HashSet<String>();
        excludeHosts.add("datanodeY");
        excludeHosts.add("datanodeZ");
        doTest(conf, new long[]{ TestBalancer.CAPACITY, TestBalancer.CAPACITY }, new String[]{ TestBalancer.RACK0, TestBalancer.RACK1 }, TestBalancer.CAPACITY, TestBalancer.RACK2, new TestBalancer.HostNameBasedNodes(new String[]{ "datanodeX", "datanodeY", "datanodeZ" }, excludeHosts, DEFAULT.getIncludedNodes()), true, true);
    }

    /**
     * Test a cluster with even distribution,G
     * then three nodes are added to the cluster,
     * runs balancer with two of the nodes in the exclude list
     */
    @Test(timeout = 100000)
    public void testBalancerCliWithExcludeListWithPortsInAFile() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        doTest(conf, new long[]{ TestBalancer.CAPACITY, TestBalancer.CAPACITY }, new String[]{ TestBalancer.RACK0, TestBalancer.RACK1 }, TestBalancer.CAPACITY, TestBalancer.RACK2, new TestBalancer.PortNumberBasedNodes(3, 2, 0), true, true);
    }

    /**
     * Test a cluster with even distribution,
     * then three nodes are added to the cluster,
     * runs balancer with two of the nodes in the include list
     */
    @Test(timeout = 100000)
    public void testBalancerWithIncludeList() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        Set<String> includeHosts = new HashSet<String>();
        includeHosts.add("datanodeY");
        doTest(conf, new long[]{ TestBalancer.CAPACITY, TestBalancer.CAPACITY }, new String[]{ TestBalancer.RACK0, TestBalancer.RACK1 }, TestBalancer.CAPACITY, TestBalancer.RACK2, new TestBalancer.HostNameBasedNodes(new String[]{ "datanodeX", "datanodeY", "datanodeZ" }, DEFAULT.getExcludedNodes(), includeHosts), false, false);
    }

    /**
     * Test a cluster with even distribution,
     * then three nodes are added to the cluster,
     * runs balancer with two of the nodes in the include list
     */
    @Test(timeout = 100000)
    public void testBalancerWithIncludeListWithPorts() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        doTest(conf, new long[]{ TestBalancer.CAPACITY, TestBalancer.CAPACITY }, new String[]{ TestBalancer.RACK0, TestBalancer.RACK1 }, TestBalancer.CAPACITY, TestBalancer.RACK2, new TestBalancer.PortNumberBasedNodes(3, 0, 1), false, false);
    }

    /**
     * Test a cluster with even distribution,
     * then three nodes are added to the cluster,
     * runs balancer with two of the nodes in the include list
     */
    @Test(timeout = 100000)
    public void testBalancerCliWithIncludeList() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        Set<String> includeHosts = new HashSet<String>();
        includeHosts.add("datanodeY");
        doTest(conf, new long[]{ TestBalancer.CAPACITY, TestBalancer.CAPACITY }, new String[]{ TestBalancer.RACK0, TestBalancer.RACK1 }, TestBalancer.CAPACITY, TestBalancer.RACK2, new TestBalancer.HostNameBasedNodes(new String[]{ "datanodeX", "datanodeY", "datanodeZ" }, DEFAULT.getExcludedNodes(), includeHosts), true, false);
    }

    /**
     * Test a cluster with even distribution,
     * then three nodes are added to the cluster,
     * runs balancer with two of the nodes in the include list
     */
    @Test(timeout = 100000)
    public void testBalancerCliWithIncludeListWithPorts() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        doTest(conf, new long[]{ TestBalancer.CAPACITY, TestBalancer.CAPACITY }, new String[]{ TestBalancer.RACK0, TestBalancer.RACK1 }, TestBalancer.CAPACITY, TestBalancer.RACK2, new TestBalancer.PortNumberBasedNodes(3, 0, 1), true, false);
    }

    /**
     * Test a cluster with even distribution,
     * then three nodes are added to the cluster,
     * runs balancer with two of the nodes in the include list
     */
    @Test(timeout = 100000)
    public void testBalancerCliWithIncludeListInAFile() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        Set<String> includeHosts = new HashSet<String>();
        includeHosts.add("datanodeY");
        doTest(conf, new long[]{ TestBalancer.CAPACITY, TestBalancer.CAPACITY }, new String[]{ TestBalancer.RACK0, TestBalancer.RACK1 }, TestBalancer.CAPACITY, TestBalancer.RACK2, new TestBalancer.HostNameBasedNodes(new String[]{ "datanodeX", "datanodeY", "datanodeZ" }, DEFAULT.getExcludedNodes(), includeHosts), true, true);
    }

    /**
     * Test a cluster with even distribution,
     * then three nodes are added to the cluster,
     * runs balancer with two of the nodes in the include list
     */
    @Test(timeout = 100000)
    public void testBalancerCliWithIncludeListWithPortsInAFile() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        doTest(conf, new long[]{ TestBalancer.CAPACITY, TestBalancer.CAPACITY }, new String[]{ TestBalancer.RACK0, TestBalancer.RACK1 }, TestBalancer.CAPACITY, TestBalancer.RACK2, new TestBalancer.PortNumberBasedNodes(3, 0, 1), true, true);
    }

    @Test(timeout = 100000)
    public void testMaxIterationTime() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        int blockSize = (10 * 1024) * 1024;// 10MB block size

        conf.setLong(DFS_BLOCK_SIZE_KEY, blockSize);
        conf.setInt(DFS_BYTES_PER_CHECKSUM_KEY, blockSize);
        // limit the worker thread count of Balancer to have only 1 queue per DN
        conf.setInt(DFS_BALANCER_MOVERTHREADS_KEY, 1);
        // limit the bandwitdh to 1 packet per sec to emulate slow block moves
        conf.setLong(DFS_DATANODE_BALANCE_BANDWIDTHPERSEC_KEY, (64 * 1024));
        // set client socket timeout to have an IN_PROGRESS notification back from
        // the DataNode about the copy in every second.
        conf.setLong(DFS_CLIENT_SOCKET_TIMEOUT_KEY, 2000L);
        // set max iteration time to 2 seconds to timeout before moving any block
        conf.setLong(DFS_BALANCER_MAX_ITERATION_TIME_KEY, 2000L);
        // setup the cluster
        final long capacity = 10L * blockSize;
        final long[] dnCapacities = new long[]{ capacity, capacity };
        final short rep = 1;
        final long seed = 16448250;
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).build();
        try {
            cluster.getConfiguration(0).setInt(DFS_REPLICATION_KEY, 1);
            conf.setInt(DFS_REPLICATION_KEY, 1);
            cluster.startDataNodes(conf, 1, true, null, null, dnCapacities);
            cluster.waitClusterUp();
            cluster.waitActive();
            final Path path = new Path("/testMaxIterationTime.dat");
            DistributedFileSystem fs = cluster.getFileSystem();
            // fill the DN to 40%
            DFSTestUtil.createFile(fs, path, (4L * blockSize), rep, seed);
            // start a new DN
            cluster.startDataNodes(conf, 1, true, null, null, dnCapacities);
            cluster.triggerHeartbeats();
            // setup Balancer and run one iteration
            List<NameNodeConnector> connectors = Collections.emptyList();
            try {
                BalancerParameters bParams = DEFAULT;
                connectors = NameNodeConnector.newNameNodeConnectors(DFSUtil.getInternalNsRpcUris(conf), Balancer.class.getSimpleName(), BALANCER_ID_PATH, conf, bParams.getMaxIdleIteration());
                for (NameNodeConnector nnc : connectors) {
                    TestBalancer.LOG.info(("NNC to work on: " + nnc));
                    Balancer b = new Balancer(nnc, bParams, conf);
                    long startTime = Time.monotonicNow();
                    Result r = b.runOneIteration();
                    long runtime = (Time.monotonicNow()) - startTime;
                    Assert.assertEquals("We expect ExitStatus.IN_PROGRESS to be reported.", IN_PROGRESS, r.exitStatus);
                    // accept runtime if it is under 3.5 seconds, as we need to wait for
                    // IN_PROGRESS report from DN, and some spare to be able to finish.
                    // NOTE: This can be a source of flaky tests, if the box is busy,
                    // assertion here is based on the following: Balancer is already set
                    // up, iteration gets the blocks from the NN, and makes the decision
                    // to move 2 blocks. After that the PendingMoves are scheduled, and
                    // DataNode heartbeats in for the Balancer every second, iteration is
                    // two seconds long. This means that it will fail if the setup and the
                    // heartbeat from the DataNode takes more than 500ms, as the iteration
                    // should end at the 3rd second from start. As the number of
                    // operations seems to be pretty low, and all comm happens locally, I
                    // think the possibility of a failure due to node busyness is low.
                    Assert.assertTrue((("Unexpected iteration runtime: " + runtime) + "ms > 3.5s"), (runtime < 3500));
                }
            } finally {
                for (NameNodeConnector nnc : connectors) {
                    IOUtils.cleanupWithLogger(null, nnc);
                }
            }
        } finally {
            cluster.shutdown(true, true);
        }
    }

    /* Test Balancer with Ram_Disk configured
    One DN has two files on RAM_DISK, other DN has no files on RAM_DISK.
    Then verify that the balancer does not migrate files on RAM_DISK across DN.
     */
    @Test(timeout = 300000)
    public void testBalancerWithRamDisk() throws Exception {
        final int SEED = 1027565;
        final short REPL_FACT = 1;
        Configuration conf = new Configuration();
        final int defaultRamDiskCapacity = 10;
        final long ramDiskStorageLimit = (((long) (defaultRamDiskCapacity)) * (TestBalancer.DEFAULT_RAM_DISK_BLOCK_SIZE)) + ((TestBalancer.DEFAULT_RAM_DISK_BLOCK_SIZE) - 1);
        final long diskStorageLimit = (((long) (defaultRamDiskCapacity)) * (TestBalancer.DEFAULT_RAM_DISK_BLOCK_SIZE)) + ((TestBalancer.DEFAULT_RAM_DISK_BLOCK_SIZE) - 1);
        TestBalancer.initConfWithRamDisk(conf, ramDiskStorageLimit);
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).storageCapacities(new long[]{ ramDiskStorageLimit, diskStorageLimit }).storageTypes(new StorageType[]{ StorageType.RAM_DISK, StorageType.DEFAULT }).build();
        cluster.waitActive();
        // Create few files on RAM_DISK
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        final Path path1 = new Path((("/" + METHOD_NAME) + ".01.dat"));
        final Path path2 = new Path((("/" + METHOD_NAME) + ".02.dat"));
        DistributedFileSystem fs = cluster.getFileSystem();
        DFSClient client = fs.getClient();
        DFSTestUtil.createFile(fs, path1, true, TestBalancer.DEFAULT_RAM_DISK_BLOCK_SIZE, (4 * (TestBalancer.DEFAULT_RAM_DISK_BLOCK_SIZE)), TestBalancer.DEFAULT_RAM_DISK_BLOCK_SIZE, REPL_FACT, SEED, true);
        DFSTestUtil.createFile(fs, path2, true, TestBalancer.DEFAULT_RAM_DISK_BLOCK_SIZE, (1 * (TestBalancer.DEFAULT_RAM_DISK_BLOCK_SIZE)), TestBalancer.DEFAULT_RAM_DISK_BLOCK_SIZE, REPL_FACT, SEED, true);
        // Sleep for a short time to allow the lazy writer thread to do its job
        Thread.sleep((6 * 1000));
        // Add another fresh DN with the same type/capacity without files on RAM_DISK
        StorageType[][] storageTypes = new StorageType[][]{ new StorageType[]{ StorageType.RAM_DISK, StorageType.DEFAULT } };
        long[][] storageCapacities = new long[][]{ new long[]{ ramDiskStorageLimit, diskStorageLimit } };
        cluster.startDataNodes(conf, REPL_FACT, storageTypes, true, null, null, null, storageCapacities, null, false, false, false, null);
        cluster.triggerHeartbeats();
        Collection<URI> namenodes = DFSUtil.getInternalNsRpcUris(conf);
        // Run Balancer
        final BalancerParameters p = DEFAULT;
        final int r = Balancer.run(namenodes, p, conf);
        // Validate no RAM_DISK block should be moved
        Assert.assertEquals(NO_MOVE_PROGRESS.getExitCode(), r);
        // Verify files are still on RAM_DISK
        DFSTestUtil.verifyFileReplicasOnStorageType(fs, client, path1, StorageType.RAM_DISK);
        DFSTestUtil.verifyFileReplicasOnStorageType(fs, client, path2, StorageType.RAM_DISK);
    }

    /**
     * Check that the balancer exits when there is an unfinalized upgrade.
     */
    @Test(timeout = 300000)
    public void testBalancerDuringUpgrade() throws Exception {
        final int SEED = 1027565;
        Configuration conf = new HdfsConfiguration();
        conf.setLong(DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY, 1);
        conf.setInt(DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY, 500);
        conf.setLong(DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY, 1);
        conf.setLong(DFS_BALANCER_GETBLOCKS_MIN_BLOCK_SIZE_KEY, 1L);
        final int BLOCK_SIZE = 1024 * 1024;
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).storageCapacities(new long[]{ BLOCK_SIZE * 10 }).storageTypes(new StorageType[]{ StorageType.DEFAULT }).storagesPerDatanode(1).build();
        cluster.waitActive();
        // Create a file on the single DN
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        final Path path1 = new Path((("/" + METHOD_NAME) + ".01.dat"));
        DistributedFileSystem fs = cluster.getFileSystem();
        DFSTestUtil.createFile(fs, path1, BLOCK_SIZE, (BLOCK_SIZE * 2), BLOCK_SIZE, ((short) (1)), SEED);
        // Add another DN with the same capacity, cluster is now unbalanced
        cluster.startDataNodes(conf, 1, true, null, null);
        cluster.triggerHeartbeats();
        Collection<URI> namenodes = DFSUtil.getInternalNsRpcUris(conf);
        // Run balancer
        final BalancerParameters p = DEFAULT;
        fs.setSafeMode(SAFEMODE_ENTER);
        fs.rollingUpgrade(PREPARE);
        fs.setSafeMode(SAFEMODE_LEAVE);
        // Rolling upgrade should abort the balancer
        Assert.assertEquals(UNFINALIZED_UPGRADE.getExitCode(), Balancer.run(namenodes, p, conf));
        // Should work with the -runDuringUpgrade flag.
        BalancerParameters.Builder b = new BalancerParameters.Builder();
        b.setRunDuringUpgrade(true);
        final BalancerParameters runDuringUpgrade = b.build();
        Assert.assertEquals(SUCCESS.getExitCode(), Balancer.run(namenodes, runDuringUpgrade, conf));
        // Finalize the rolling upgrade
        fs.rollingUpgrade(FINALIZE);
        // Should also work after finalization.
        Assert.assertEquals(SUCCESS.getExitCode(), Balancer.run(namenodes, p, conf));
    }

    /**
     * Test special case. Two replicas belong to same block should not in same node.
     * We have 2 nodes.
     * We have a block in (DN0,SSD) and (DN1,DISK).
     * Replica in (DN0,SSD) should not be moved to (DN1,SSD).
     * Otherwise DN1 has 2 replicas.
     */
    @Test(timeout = 100000)
    public void testTwoReplicaShouldNotInSameDN() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        int blockSize = (5 * 1024) * 1024;
        conf.setLong(DFS_BLOCK_SIZE_KEY, blockSize);
        conf.setLong(DFS_HEARTBEAT_INTERVAL_KEY, 1L);
        conf.setLong(DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY, 1L);
        conf.setLong(DFS_BALANCER_GETBLOCKS_MIN_BLOCK_SIZE_KEY, 1L);
        int numOfDatanodes = 2;
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(2).racks(new String[]{ "/default/rack0", "/default/rack0" }).storagesPerDatanode(2).storageTypes(new StorageType[][]{ new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK } }).storageCapacities(new long[][]{ new long[]{ 100 * blockSize, 20 * blockSize }, new long[]{ 20 * blockSize, 100 * blockSize } }).build();
        cluster.waitActive();
        // set "/bar" directory with ONE_SSD storage policy.
        DistributedFileSystem fs = cluster.getFileSystem();
        Path barDir = new Path("/bar");
        fs.mkdir(barDir, new FsPermission(((short) (777))));
        fs.setStoragePolicy(barDir, ONESSD_STORAGE_POLICY_NAME);
        // Insert 30 blocks. So (DN0,SSD) and (DN1,DISK) are about half full,
        // and (DN0,SSD) and (DN1,DISK) are about 15% full.
        long fileLen = 30 * blockSize;
        // fooFile has ONE_SSD policy. So
        // (DN0,SSD) and (DN1,DISK) have 2 replicas belong to same block.
        // (DN0,DISK) and (DN1,SSD) have 2 replicas belong to same block.
        Path fooFile = new Path(barDir, "foo");
        TestBalancer.createFile(cluster, fooFile, fileLen, ((short) (numOfDatanodes)), 0);
        // update space info
        cluster.triggerHeartbeats();
        BalancerParameters p = DEFAULT;
        Collection<URI> namenodes = DFSUtil.getInternalNsRpcUris(conf);
        final int r = Balancer.run(namenodes, p, conf);
        // Replica in (DN0,SSD) was not moved to (DN1,SSD), because (DN1,DISK)
        // already has one. Otherwise DN1 will have 2 replicas.
        // For same reason, no replicas were moved.
        Assert.assertEquals(NO_MOVE_PROGRESS.getExitCode(), r);
    }

    /**
     * Test running many balancer simultaneously.
     *
     * Case-1: First balancer is running. Now, running second one should get
     * "Another balancer is running. Exiting.." IOException and fail immediately
     *
     * Case-2: When running second balancer 'balancer.id' file exists but the
     * lease doesn't exists. Now, the second balancer should run successfully.
     */
    @Test(timeout = 100000)
    public void testManyBalancerSimultaneously() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        // add an empty node with half of the capacities(4 * CAPACITY) & the same
        // rack
        long[] capacities = new long[]{ 4 * (TestBalancer.CAPACITY) };
        String[] racks = new String[]{ TestBalancer.RACK0 };
        long newCapacity = 2 * (TestBalancer.CAPACITY);
        String newRack = TestBalancer.RACK0;
        TestBalancer.LOG.info(("capacities = " + (long2String(capacities))));
        TestBalancer.LOG.info(("racks      = " + (Arrays.asList(racks))));
        TestBalancer.LOG.info(("newCapacity= " + newCapacity));
        TestBalancer.LOG.info(("newRack    = " + newRack));
        TestBalancer.LOG.info(("useTool    = " + false));
        Assert.assertEquals(capacities.length, racks.length);
        int numOfDatanodes = capacities.length;
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(capacities.length).racks(racks).simulatedCapacities(capacities).build();
        cluster.waitActive();
        client = NameNodeProxies.createProxy(conf, cluster.getFileSystem(0).getUri(), ClientProtocol.class).getProxy();
        long totalCapacity = TestBalancer.sum(capacities);
        // fill up the cluster to be 30% full
        final long totalUsedSpace = (totalCapacity * 3) / 10;
        TestBalancer.createFile(cluster, TestBalancer.filePath, (totalUsedSpace / numOfDatanodes), ((short) (numOfDatanodes)), 0);
        // start up an empty node with the same capacity and on the same rack
        cluster.startDataNodes(conf, 1, true, null, new String[]{ newRack }, new long[]{ newCapacity });
        // Case1: Simulate first balancer by creating 'balancer.id' file. It
        // will keep this file until the balancing operation is completed.
        FileSystem fs = cluster.getFileSystem(0);
        final FSDataOutputStream out = fs.create(BALANCER_ID_PATH, false);
        out.writeBytes(InetAddress.getLocalHost().getHostName());
        out.hflush();
        Assert.assertTrue("'balancer.id' file doesn't exist!", fs.exists(BALANCER_ID_PATH));
        // start second balancer
        final String[] args = new String[]{ "-policy", "datanode" };
        final Tool tool = new org.apache.hadoop.hdfs.server.balancer.Balancer.Cli();
        tool.setConf(conf);
        int exitCode = tool.run(args);// start balancing

        Assert.assertEquals("Exit status code mismatches", IO_EXCEPTION.getExitCode(), exitCode);
        // Case2: Release lease so that another balancer would be able to
        // perform balancing.
        out.close();
        Assert.assertTrue("'balancer.id' file doesn't exist!", fs.exists(BALANCER_ID_PATH));
        exitCode = tool.run(args);// start balancing

        Assert.assertEquals("Exit status code mismatches", SUCCESS.getExitCode(), exitCode);
    }

    /**
     * Balancer should not move blocks with size < minBlockSize.
     */
    @Test(timeout = 60000)
    public void testMinBlockSizeAndSourceNodes() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        final short replication = 3;
        final long[] lengths = new long[]{ 10, 10, 10, 10 };
        final long[] capacities = new long[replication];
        final long totalUsed = (capacities.length) * (TestBalancer.sum(lengths));
        Arrays.fill(capacities, 1000);
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(capacities.length).simulatedCapacities(capacities).build();
        final DistributedFileSystem dfs = cluster.getFileSystem();
        cluster.waitActive();
        client = NameNodeProxies.createProxy(conf, dfs.getUri(), ClientProtocol.class).getProxy();
        // fill up the cluster to be 80% full
        for (int i = 0; i < (lengths.length); i++) {
            final long size = lengths[i];
            final Path p = new Path(((("/file" + i) + "_size") + size));
            try (OutputStream out = dfs.create(p)) {
                for (int j = 0; j < size; j++) {
                    out.write(j);
                }
            }
        }
        // start up an empty node with the same capacity
        cluster.startDataNodes(conf, capacities.length, true, null, null, capacities);
        TestBalancer.LOG.info(("capacities    = " + (Arrays.toString(capacities))));
        TestBalancer.LOG.info(("totalUsedSpace= " + totalUsed));
        TestBalancer.LOG.info(((("lengths       = " + (Arrays.toString(lengths))) + ", #=") + (lengths.length)));
        TestBalancer.waitForHeartBeat(totalUsed, ((2 * (capacities[0])) * (capacities.length)), client, cluster);
        final Collection<URI> namenodes = DFSUtil.getInternalNsRpcUris(conf);
        {
            // run Balancer with min-block-size=50
            final BalancerParameters p = Cli.parse(new String[]{ "-policy", INSTANCE.getName(), "-threshold", "1" });
            Assert.assertEquals(p.getBalancingPolicy(), INSTANCE);
            Assert.assertEquals(p.getThreshold(), 1.0, 0.001);
            conf.setLong(DFS_BALANCER_GETBLOCKS_MIN_BLOCK_SIZE_KEY, 50);
            final int r = Balancer.run(namenodes, p, conf);
            Assert.assertEquals(NO_MOVE_PROGRESS.getExitCode(), r);
        }
        conf.setLong(DFS_BALANCER_GETBLOCKS_MIN_BLOCK_SIZE_KEY, 1);
        {
            // run Balancer with empty nodes as source nodes
            final Set<String> sourceNodes = new HashSet<>();
            final List<DataNode> datanodes = cluster.getDataNodes();
            for (int i = capacities.length; i < (datanodes.size()); i++) {
                sourceNodes.add(datanodes.get(i).getDisplayName());
            }
            final BalancerParameters p = Cli.parse(new String[]{ "-policy", INSTANCE.getName(), "-threshold", "1", "-source", StringUtils.join(sourceNodes, ',') });
            Assert.assertEquals(p.getBalancingPolicy(), INSTANCE);
            Assert.assertEquals(p.getThreshold(), 1.0, 0.001);
            Assert.assertEquals(p.getSourceNodes(), sourceNodes);
            conf.setLong(DFS_BALANCER_GETBLOCKS_MIN_BLOCK_SIZE_KEY, 50);
            final int r = Balancer.run(namenodes, p, conf);
            Assert.assertEquals(NO_MOVE_BLOCK.getExitCode(), r);
        }
        {
            // run Balancer with a filled node as a source node
            final Set<String> sourceNodes = new HashSet<>();
            final List<DataNode> datanodes = cluster.getDataNodes();
            sourceNodes.add(datanodes.get(0).getDisplayName());
            final BalancerParameters p = Cli.parse(new String[]{ "-policy", INSTANCE.getName(), "-threshold", "1", "-source", StringUtils.join(sourceNodes, ',') });
            Assert.assertEquals(p.getBalancingPolicy(), INSTANCE);
            Assert.assertEquals(p.getThreshold(), 1.0, 0.001);
            Assert.assertEquals(p.getSourceNodes(), sourceNodes);
            conf.setLong(DFS_BALANCER_GETBLOCKS_MIN_BLOCK_SIZE_KEY, 1);
            final int r = Balancer.run(namenodes, p, conf);
            Assert.assertEquals(NO_MOVE_BLOCK.getExitCode(), r);
        }
        {
            // run Balancer with all filled node as source nodes
            final Set<String> sourceNodes = new HashSet<>();
            final List<DataNode> datanodes = cluster.getDataNodes();
            for (int i = 0; i < (capacities.length); i++) {
                sourceNodes.add(datanodes.get(i).getDisplayName());
            }
            final BalancerParameters p = Cli.parse(new String[]{ "-policy", INSTANCE.getName(), "-threshold", "1", "-source", StringUtils.join(sourceNodes, ',') });
            Assert.assertEquals(p.getBalancingPolicy(), INSTANCE);
            Assert.assertEquals(p.getThreshold(), 1.0, 0.001);
            Assert.assertEquals(p.getSourceNodes(), sourceNodes);
            conf.setLong(DFS_BALANCER_GETBLOCKS_MIN_BLOCK_SIZE_KEY, 1);
            final int r = Balancer.run(namenodes, p, conf);
            Assert.assertEquals(SUCCESS.getExitCode(), r);
        }
    }

    @Test(timeout = 200000)
    public void testBalancerWithStripedFile() throws Exception {
        Configuration conf = new Configuration();
        initConfWithStripe(conf);
        NameNodeConnector.setWrite2IdFile(true);
        doTestBalancerWithStripedFile(conf);
        NameNodeConnector.setWrite2IdFile(false);
    }

    /**
     * Test Balancer runs fine when logging in with a keytab in kerberized env.
     * Reusing testUnknownDatanode here for basic functionality testing.
     */
    @Test(timeout = 300000)
    public void testBalancerWithKeytabs() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        try {
            TestBalancer.initSecureConf(conf);
            final UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(TestBalancer.principal, TestBalancer.keytabFile.getAbsolutePath());
            ugi.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    // verify that balancer runs Ok.
                    testUnknownDatanode(conf);
                    // verify that UGI was logged in using keytab.
                    Assert.assertTrue(UserGroupInformation.isLoginKeytabBased());
                    return null;
                }
            });
        } finally {
            // Reset UGI so that other tests are not affected.
            UserGroupInformation.reset();
            UserGroupInformation.setConfiguration(new Configuration());
        }
    }

    private static int numGetBlocksCalls;

    private static long startGetBlocksTime;

    private static long endGetBlocksTime;
}

