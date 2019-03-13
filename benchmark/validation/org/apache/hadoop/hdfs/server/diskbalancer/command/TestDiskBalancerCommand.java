/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.hdfs.server.diskbalancer.command;


import DFSConfigKeys.DFS_DISK_BALANCER_ENABLED;
import DFSConfigKeys.DFS_DISK_BALANCER_PLAN_VALID_INTERVAL;
import StartupOption.ROLLBACK;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.diskbalancer.DiskBalancerTestUtil;
import org.apache.hadoop.hdfs.server.diskbalancer.connectors.ClusterConnector;
import org.apache.hadoop.hdfs.server.diskbalancer.connectors.ConnectorFactory;
import org.apache.hadoop.hdfs.server.diskbalancer.datamodel.DiskBalancerCluster;
import org.apache.hadoop.hdfs.server.diskbalancer.datamodel.DiskBalancerDataNode;
import org.apache.hadoop.hdfs.tools.DiskBalancerCLI;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.LambdaTestUtils;
import org.apache.hadoop.test.PathUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests various CLI commands of DiskBalancer.
 */
public class TestDiskBalancerCommand {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private MiniDFSCluster cluster;

    private URI clusterJson;

    private Configuration conf = new HdfsConfiguration();

    private static final int DEFAULT_BLOCK_SIZE = 1024;

    private static final int FILE_LEN = 200 * 1024;

    private static final long CAPCACITY = 300 * 1024;

    private static final long[] CAPACITIES = new long[]{ TestDiskBalancerCommand.CAPCACITY, TestDiskBalancerCommand.CAPCACITY };

    /**
     * Tests if it's allowed to submit and execute plan when Datanode is in status
     * other than REGULAR.
     */
    @Test(timeout = 60000)
    public void testSubmitPlanInNonRegularStatus() throws Exception {
        final int numDatanodes = 1;
        MiniDFSCluster miniCluster = null;
        final Configuration hdfsConf = new HdfsConfiguration();
        try {
            /* new cluster with imbalanced capacity */
            miniCluster = DiskBalancerTestUtil.newImbalancedCluster(hdfsConf, numDatanodes, TestDiskBalancerCommand.CAPACITIES, TestDiskBalancerCommand.DEFAULT_BLOCK_SIZE, TestDiskBalancerCommand.FILE_LEN, ROLLBACK);
            /* get full path of plan */
            final String planFileFullName = runAndVerifyPlan(miniCluster, hdfsConf);
            try {
                /* run execute command */
                final String cmdLine = String.format("hdfs diskbalancer -%s %s", DiskBalancerCLI.EXECUTE, planFileFullName);
                runCommand(cmdLine, hdfsConf, miniCluster);
            } catch (RemoteException e) {
                Assert.assertThat(e.getClassName(), CoreMatchers.containsString("DiskBalancerException"));
                Assert.assertThat(e.toString(), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("Datanode is in special state"), CoreMatchers.containsString("Disk balancing not permitted."))));
            }
        } finally {
            if (miniCluster != null) {
                miniCluster.shutdown();
            }
        }
    }

    /**
     * Tests running multiple commands under on setup. This mainly covers
     * {@link org.apache.hadoop.hdfs.server.diskbalancer.command.Command#close}
     */
    @Test(timeout = 60000)
    public void testRunMultipleCommandsUnderOneSetup() throws Exception {
        final int numDatanodes = 1;
        MiniDFSCluster miniCluster = null;
        final Configuration hdfsConf = new HdfsConfiguration();
        try {
            /* new cluster with imbalanced capacity */
            miniCluster = DiskBalancerTestUtil.newImbalancedCluster(hdfsConf, numDatanodes, TestDiskBalancerCommand.CAPACITIES, TestDiskBalancerCommand.DEFAULT_BLOCK_SIZE, TestDiskBalancerCommand.FILE_LEN);
            /* get full path of plan */
            final String planFileFullName = runAndVerifyPlan(miniCluster, hdfsConf);
            /* run execute command */
            final String cmdLine = String.format("hdfs diskbalancer -%s %s", DiskBalancerCLI.EXECUTE, planFileFullName);
            runCommand(cmdLine, hdfsConf, miniCluster);
        } finally {
            if (miniCluster != null) {
                miniCluster.shutdown();
            }
        }
    }

    @Test(timeout = 600000)
    public void testDiskBalancerExecuteOptionPlanValidityWithException() throws Exception {
        final int numDatanodes = 1;
        final Configuration hdfsConf = new HdfsConfiguration();
        hdfsConf.setBoolean(DFS_DISK_BALANCER_ENABLED, true);
        hdfsConf.set(DFS_DISK_BALANCER_PLAN_VALID_INTERVAL, "0d");
        /* new cluster with imbalanced capacity */
        final MiniDFSCluster miniCluster = DiskBalancerTestUtil.newImbalancedCluster(hdfsConf, numDatanodes, TestDiskBalancerCommand.CAPACITIES, TestDiskBalancerCommand.DEFAULT_BLOCK_SIZE, TestDiskBalancerCommand.FILE_LEN);
        try {
            /* get full path of plan */
            final String planFileFullName = runAndVerifyPlan(miniCluster, hdfsConf);
            /* run execute command */
            final String cmdLine = String.format("hdfs diskbalancer -%s %s", DiskBalancerCLI.EXECUTE, planFileFullName);
            LambdaTestUtils.intercept(RemoteException.class, "DiskBalancerException", "Plan was generated more than 0d ago", () -> {
                runCommand(cmdLine, hdfsConf, miniCluster);
            });
        } finally {
            if (miniCluster != null) {
                miniCluster.shutdown();
            }
        }
    }

    @Test(timeout = 600000)
    public void testDiskBalancerExecutePlanValidityWithOutUnitException() throws Exception {
        final int numDatanodes = 1;
        final Configuration hdfsConf = new HdfsConfiguration();
        hdfsConf.setBoolean(DFS_DISK_BALANCER_ENABLED, true);
        hdfsConf.set(DFS_DISK_BALANCER_PLAN_VALID_INTERVAL, "0");
        /* new cluster with imbalanced capacity */
        final MiniDFSCluster miniCluster = DiskBalancerTestUtil.newImbalancedCluster(hdfsConf, numDatanodes, TestDiskBalancerCommand.CAPACITIES, TestDiskBalancerCommand.DEFAULT_BLOCK_SIZE, TestDiskBalancerCommand.FILE_LEN);
        try {
            /* get full path of plan */
            final String planFileFullName = runAndVerifyPlan(miniCluster, hdfsConf);
            /* run execute command */
            final String cmdLine = String.format("hdfs diskbalancer -%s %s", DiskBalancerCLI.EXECUTE, planFileFullName);
            LambdaTestUtils.intercept(RemoteException.class, "DiskBalancerException", "Plan was generated more than 0ms ago", () -> {
                runCommand(cmdLine, hdfsConf, miniCluster);
            });
        } finally {
            if (miniCluster != null) {
                miniCluster.shutdown();
            }
        }
    }

    @Test(timeout = 600000)
    public void testDiskBalancerForceExecute() throws Exception {
        final int numDatanodes = 1;
        final Configuration hdfsConf = new HdfsConfiguration();
        hdfsConf.setBoolean(DFS_DISK_BALANCER_ENABLED, true);
        hdfsConf.set(DFS_DISK_BALANCER_PLAN_VALID_INTERVAL, "0d");
        /* new cluster with imbalanced capacity */
        final MiniDFSCluster miniCluster = DiskBalancerTestUtil.newImbalancedCluster(hdfsConf, numDatanodes, TestDiskBalancerCommand.CAPACITIES, TestDiskBalancerCommand.DEFAULT_BLOCK_SIZE, TestDiskBalancerCommand.FILE_LEN);
        try {
            /* get full path of plan */
            final String planFileFullName = runAndVerifyPlan(miniCluster, hdfsConf);
            /* run execute command */
            final String cmdLine = String.format("hdfs diskbalancer -%s %s -%s", DiskBalancerCLI.EXECUTE, planFileFullName, DiskBalancerCLI.SKIPDATECHECK);
            // Disk Balancer should execute the plan, as skipDateCheck Option is
            // specified
            runCommand(cmdLine, hdfsConf, miniCluster);
        } finally {
            if (miniCluster != null) {
                miniCluster.shutdown();
            }
        }
    }

    @Test(timeout = 600000)
    public void testDiskBalancerExecuteOptionPlanValidity() throws Exception {
        final int numDatanodes = 1;
        final Configuration hdfsConf = new HdfsConfiguration();
        hdfsConf.setBoolean(DFS_DISK_BALANCER_ENABLED, true);
        hdfsConf.set(DFS_DISK_BALANCER_PLAN_VALID_INTERVAL, "600s");
        /* new cluster with imbalanced capacity */
        final MiniDFSCluster miniCluster = DiskBalancerTestUtil.newImbalancedCluster(hdfsConf, numDatanodes, TestDiskBalancerCommand.CAPACITIES, TestDiskBalancerCommand.DEFAULT_BLOCK_SIZE, TestDiskBalancerCommand.FILE_LEN);
        try {
            /* get full path of plan */
            final String planFileFullName = runAndVerifyPlan(miniCluster, hdfsConf);
            /* run execute command */
            final String cmdLine = String.format("hdfs diskbalancer -%s %s", DiskBalancerCLI.EXECUTE, planFileFullName);
            // Plan is valid for 600 seconds, sleeping for 10seconds, so now
            // diskbalancer should execute the plan
            Thread.sleep(10000);
            runCommand(cmdLine, hdfsConf, miniCluster);
        } finally {
            if (miniCluster != null) {
                miniCluster.shutdown();
            }
        }
    }

    /* test basic report */
    @Test(timeout = 60000)
    public void testReportSimple() throws Exception {
        final String cmdLine = "hdfs diskbalancer -report";
        final List<String> outputs = runCommand(cmdLine);
        Assert.assertThat(outputs.get(0), CoreMatchers.containsString("Processing report command"));
        Assert.assertThat(outputs.get(1), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("No top limit specified"), CoreMatchers.containsString("using default top value"), CoreMatchers.containsString("100"))));
        Assert.assertThat(outputs.get(2), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("Reporting top"), CoreMatchers.containsString("64"), CoreMatchers.containsString("DataNode(s) benefiting from running DiskBalancer"))));
        Assert.assertThat(outputs.get(32), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("30/64 null[null:0]"), CoreMatchers.containsString("a87654a9-54c7-4693-8dd9-c9c7021dc340"), CoreMatchers.containsString("9 volumes with node data density 1.97"))));
    }

    /* test basic report with negative top limit */
    @Test(timeout = 60000)
    public void testReportWithNegativeTopLimit() throws Exception {
        final String cmdLine = "hdfs diskbalancer -report -top -32";
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Top limit input should be a positive numeric value");
        runCommand(cmdLine);
    }

    /* test less than 64 DataNode(s) as total, e.g., -report -top 32 */
    @Test(timeout = 60000)
    public void testReportLessThanTotal() throws Exception {
        final String cmdLine = "hdfs diskbalancer -report -top 32";
        final List<String> outputs = runCommand(cmdLine);
        Assert.assertThat(outputs.get(0), CoreMatchers.containsString("Processing report command"));
        Assert.assertThat(outputs.get(1), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("Reporting top"), CoreMatchers.containsString("32"), CoreMatchers.containsString("DataNode(s) benefiting from running DiskBalancer"))));
        Assert.assertThat(outputs.get(31), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("30/32 null[null:0]"), CoreMatchers.containsString("a87654a9-54c7-4693-8dd9-c9c7021dc340"), CoreMatchers.containsString("9 volumes with node data density 1.97"))));
    }

    /**
     * This test simulates DiskBalancerCLI Report command run from a shell
     * with a generic option 'fs'.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testReportWithGenericOptionFS() throws Exception {
        final String topReportArg = "5";
        final String reportArgs = String.format("-%s file:%s -%s -%s %s", "fs", clusterJson.getPath(), DiskBalancerCLI.REPORT, "top", topReportArg);
        final String cmdLine = String.format("%s", reportArgs);
        final List<String> outputs = runCommand(cmdLine);
        Assert.assertThat(outputs.get(0), CoreMatchers.containsString("Processing report command"));
        Assert.assertThat(outputs.get(1), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("Reporting top"), CoreMatchers.containsString(topReportArg), CoreMatchers.containsString("DataNode(s) benefiting from running DiskBalancer"))));
    }

    /* test more than 64 DataNode(s) as total, e.g., -report -top 128 */
    @Test(timeout = 60000)
    public void testReportMoreThanTotal() throws Exception {
        final String cmdLine = "hdfs diskbalancer -report -top 128";
        final List<String> outputs = runCommand(cmdLine);
        Assert.assertThat(outputs.get(0), CoreMatchers.containsString("Processing report command"));
        Assert.assertThat(outputs.get(1), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("Reporting top"), CoreMatchers.containsString("64"), CoreMatchers.containsString("DataNode(s) benefiting from running DiskBalancer"))));
        Assert.assertThat(outputs.get(31), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("30/64 null[null:0]"), CoreMatchers.containsString("a87654a9-54c7-4693-8dd9-c9c7021dc340"), CoreMatchers.containsString("9 volumes with node data density 1.97"))));
    }

    /* test invalid top limit, e.g., -report -top xx */
    @Test(timeout = 60000)
    public void testReportInvalidTopLimit() throws Exception {
        final String cmdLine = "hdfs diskbalancer -report -top xx";
        final List<String> outputs = runCommand(cmdLine);
        Assert.assertThat(outputs.get(0), CoreMatchers.containsString("Processing report command"));
        Assert.assertThat(outputs.get(1), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("Top limit input is not numeric"), CoreMatchers.containsString("using default top value"), CoreMatchers.containsString("100"))));
        Assert.assertThat(outputs.get(2), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("Reporting top"), CoreMatchers.containsString("64"), CoreMatchers.containsString("DataNode(s) benefiting from running DiskBalancer"))));
        Assert.assertThat(outputs.get(32), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("30/64 null[null:0]"), CoreMatchers.containsString("a87654a9-54c7-4693-8dd9-c9c7021dc340"), CoreMatchers.containsString("9 volumes with node data density 1.97"))));
    }

    @Test(timeout = 60000)
    public void testReportNode() throws Exception {
        final String cmdLine = "hdfs diskbalancer -report -node " + "a87654a9-54c7-4693-8dd9-c9c7021dc340";
        final List<String> outputs = runCommand(cmdLine);
        Assert.assertThat(outputs.get(0), CoreMatchers.containsString("Processing report command"));
        Assert.assertThat(outputs.get(1), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("Reporting volume information for DataNode"), CoreMatchers.containsString("a87654a9-54c7-4693-8dd9-c9c7021dc340"))));
        Assert.assertThat(outputs.get(2), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("null[null:0]"), CoreMatchers.containsString("a87654a9-54c7-4693-8dd9-c9c7021dc340"), CoreMatchers.containsString("9 volumes with node data density 1.97"))));
        Assert.assertThat(outputs.get(3), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("DISK"), CoreMatchers.containsString("/tmp/disk/KmHefYNURo"), CoreMatchers.containsString("0.20 used: 39160240782/200000000000"), CoreMatchers.containsString("0.80 free: 160839759218/200000000000"))));
        Assert.assertThat(outputs.get(4), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("DISK"), CoreMatchers.containsString("/tmp/disk/Mxfcfmb24Y"), CoreMatchers.containsString("0.92 used: 733099315216/800000000000"), CoreMatchers.containsString("0.08 free: 66900684784/800000000000"))));
        Assert.assertThat(outputs.get(5), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("DISK"), CoreMatchers.containsString("/tmp/disk/xx3j3ph3zd"), CoreMatchers.containsString("0.72 used: 289544224916/400000000000"), CoreMatchers.containsString("0.28 free: 110455775084/400000000000"))));
        Assert.assertThat(outputs.get(6), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("RAM_DISK"), CoreMatchers.containsString("/tmp/disk/BoBlQFxhfw"), CoreMatchers.containsString("0.60 used: 477590453390/800000000000"), CoreMatchers.containsString("0.40 free: 322409546610/800000000000"))));
        Assert.assertThat(outputs.get(7), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("RAM_DISK"), CoreMatchers.containsString("/tmp/disk/DtmAygEU6f"), CoreMatchers.containsString("0.34 used: 134602910470/400000000000"), CoreMatchers.containsString("0.66 free: 265397089530/400000000000"))));
        Assert.assertThat(outputs.get(8), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("RAM_DISK"), CoreMatchers.containsString("/tmp/disk/MXRyYsCz3U"), CoreMatchers.containsString("0.55 used: 438102096853/800000000000"), CoreMatchers.containsString("0.45 free: 361897903147/800000000000"))));
        Assert.assertThat(outputs.get(9), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("SSD"), CoreMatchers.containsString("/tmp/disk/BGe09Y77dI"), CoreMatchers.containsString("0.89 used: 890446265501/1000000000000"), CoreMatchers.containsString("0.11 free: 109553734499/1000000000000"))));
        Assert.assertThat(outputs.get(10), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("SSD"), CoreMatchers.containsString("/tmp/disk/JX3H8iHggM"), CoreMatchers.containsString("0.31 used: 2782614512957/9000000000000"), CoreMatchers.containsString("0.69 free: 6217385487043/9000000000000"))));
        Assert.assertThat(outputs.get(11), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("SSD"), CoreMatchers.containsString("/tmp/disk/uLOYmVZfWV"), CoreMatchers.containsString("0.75 used: 1509592146007/2000000000000"), CoreMatchers.containsString("0.25 free: 490407853993/2000000000000"))));
    }

    @Test(timeout = 60000)
    public void testReportNodeWithoutJson() throws Exception {
        String dataNodeUuid = cluster.getDataNodes().get(0).getDatanodeUuid();
        final String planArg = String.format("-%s -%s %s", DiskBalancerCLI.REPORT, DiskBalancerCLI.NODE, dataNodeUuid);
        final String cmdLine = String.format("hdfs diskbalancer %s", planArg);
        List<String> outputs = runCommand(cmdLine, cluster);
        Assert.assertThat(outputs.get(0), CoreMatchers.containsString("Processing report command"));
        Assert.assertThat(outputs.get(1), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("Reporting volume information for DataNode"), CoreMatchers.containsString(dataNodeUuid))));
        Assert.assertThat(outputs.get(2), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString(dataNodeUuid), CoreMatchers.containsString("2 volumes with node data density 0.00"))));
        Assert.assertThat(outputs.get(3), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("DISK"), CoreMatchers.containsString(new Path(cluster.getInstanceStorageDir(0, 0).getAbsolutePath()).toString()), CoreMatchers.containsString("0.00"), CoreMatchers.containsString("1.00"))));
        Assert.assertThat(outputs.get(4), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("DISK"), CoreMatchers.containsString(new Path(cluster.getInstanceStorageDir(0, 1).getAbsolutePath()).toString()), CoreMatchers.containsString("0.00"), CoreMatchers.containsString("1.00"))));
    }

    @Test(timeout = 60000)
    public void testReadClusterFromJson() throws Exception {
        ClusterConnector jsonConnector = ConnectorFactory.getCluster(clusterJson, conf);
        DiskBalancerCluster diskBalancerCluster = new DiskBalancerCluster(jsonConnector);
        diskBalancerCluster.readClusterInfo();
        Assert.assertEquals(64, diskBalancerCluster.getNodes().size());
    }

    /* test -plan  DataNodeID */
    @Test(timeout = 60000)
    public void testPlanNode() throws Exception {
        final String planArg = String.format("-%s %s", DiskBalancerCLI.PLAN, cluster.getDataNodes().get(0).getDatanodeUuid());
        final String cmdLine = String.format("hdfs diskbalancer %s", planArg);
        runCommand(cmdLine, cluster);
    }

    /* test -plan  DataNodeID */
    @Test(timeout = 60000)
    public void testPlanJsonNode() throws Exception {
        final String planArg = String.format("-%s %s", DiskBalancerCLI.PLAN, "a87654a9-54c7-4693-8dd9-c9c7021dc340");
        final Path testPath = new Path(PathUtils.getTestPath(getClass()), GenericTestUtils.getMethodName());
        final String cmdLine = String.format("hdfs diskbalancer -out %s %s", testPath, planArg);
        runCommand(cmdLine);
    }

    /* Test that illegal arguments are handled correctly */
    @Test(timeout = 60000)
    public void testIllegalArgument() throws Exception {
        final String planArg = String.format("-%s %s", DiskBalancerCLI.PLAN, "a87654a9-54c7-4693-8dd9-c9c7021dc340");
        final String cmdLine = String.format("hdfs diskbalancer %s -report", planArg);
        // -plan and -report cannot be used together.
        // tests the validate command line arguments function.
        thrown.expect(IllegalArgumentException.class);
        runCommand(cmdLine);
    }

    @Test(timeout = 60000)
    public void testCancelCommand() throws Exception {
        final String cancelArg = String.format("-%s %s", DiskBalancerCLI.CANCEL, "nosuchplan");
        final String nodeArg = String.format("-%s %s", DiskBalancerCLI.NODE, cluster.getDataNodes().get(0).getDatanodeUuid());
        // Port:Host format is expected. So cancel command will throw.
        thrown.expect(IllegalArgumentException.class);
        final String cmdLine = String.format("hdfs diskbalancer  %s %s", cancelArg, nodeArg);
        runCommand(cmdLine);
    }

    /* Makes an invalid query attempt to non-existent Datanode. */
    @Test(timeout = 60000)
    public void testQueryCommand() throws Exception {
        final String queryArg = String.format("-%s %s", DiskBalancerCLI.QUERY, cluster.getDataNodes().get(0).getDatanodeUuid());
        thrown.expect(UnknownHostException.class);
        final String cmdLine = String.format("hdfs diskbalancer %s", queryArg);
        runCommand(cmdLine);
    }

    @Test(timeout = 60000)
    public void testHelpCommand() throws Exception {
        final String helpArg = String.format("-%s", DiskBalancerCLI.HELP);
        final String cmdLine = String.format("hdfs diskbalancer %s", helpArg);
        runCommand(cmdLine);
    }

    @Test
    public void testPrintFullPathOfPlan() throws Exception {
        String parent = GenericTestUtils.getRandomizedTempPath();
        MiniDFSCluster miniCluster = null;
        try {
            Configuration hdfsConf = new HdfsConfiguration();
            List<String> outputs = null;
            /* new cluster with imbalanced capacity */
            miniCluster = DiskBalancerTestUtil.newImbalancedCluster(hdfsConf, 1, TestDiskBalancerCommand.CAPACITIES, TestDiskBalancerCommand.DEFAULT_BLOCK_SIZE, TestDiskBalancerCommand.FILE_LEN);
            /* run plan command */
            final String cmdLine = String.format("hdfs diskbalancer -%s %s -%s %s", DiskBalancerCLI.PLAN, miniCluster.getDataNodes().get(0).getDatanodeUuid(), DiskBalancerCLI.OUTFILE, parent);
            outputs = runCommand(cmdLine, hdfsConf, miniCluster);
            /* get full path */
            final String planFileFullName = new Path(parent, miniCluster.getDataNodes().get(0).getDatanodeUuid()).toString();
            /* verify the path of plan */
            Assert.assertEquals(("There must be two lines: the 1st is writing plan to," + " the 2nd is actual full path of plan file."), 2, outputs.size());
            Assert.assertThat(outputs.get(0), CoreMatchers.containsString("Writing plan to"));
            Assert.assertThat(outputs.get(1), CoreMatchers.containsString(planFileFullName));
        } finally {
            if (miniCluster != null) {
                miniCluster.shutdown();
            }
        }
    }

    /**
     * Making sure that we can query the node without having done a submit.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDiskBalancerQueryWithoutSubmit() throws Exception {
        Configuration conf = new HdfsConfiguration();
        conf.setBoolean(DFS_DISK_BALANCER_ENABLED, true);
        final int numDatanodes = 2;
        File basedir = new File(GenericTestUtils.getRandomizedTempPath());
        MiniDFSCluster miniDFSCluster = new MiniDFSCluster.Builder(conf, basedir).numDataNodes(numDatanodes).build();
        try {
            miniDFSCluster.waitActive();
            DataNode dataNode = miniDFSCluster.getDataNodes().get(0);
            final String queryArg = String.format("-query localhost:%d", dataNode.getIpcPort());
            final String cmdLine = String.format("hdfs diskbalancer %s", queryArg);
            runCommand(cmdLine);
        } finally {
            miniDFSCluster.shutdown();
        }
    }

    @Test(timeout = 60000)
    public void testGetNodeList() throws Exception {
        ClusterConnector jsonConnector = ConnectorFactory.getCluster(clusterJson, conf);
        DiskBalancerCluster diskBalancerCluster = new DiskBalancerCluster(jsonConnector);
        diskBalancerCluster.readClusterInfo();
        int nodeNum = 5;
        StringBuilder listArg = new StringBuilder();
        for (int i = 0; i < nodeNum; i++) {
            listArg.append(diskBalancerCluster.getNodes().get(i).getDataNodeUUID()).append(",");
        }
        ReportCommand command = new ReportCommand(conf, null);
        command.setCluster(diskBalancerCluster);
        List<DiskBalancerDataNode> nodeList = command.getNodes(listArg.toString());
        Assert.assertEquals(nodeNum, nodeList.size());
    }

    @Test(timeout = 60000)
    public void testReportCommandWithMultipleNodes() throws Exception {
        String dataNodeUuid1 = cluster.getDataNodes().get(0).getDatanodeUuid();
        String dataNodeUuid2 = cluster.getDataNodes().get(1).getDatanodeUuid();
        final String planArg = String.format("-%s -%s %s,%s", DiskBalancerCLI.REPORT, DiskBalancerCLI.NODE, dataNodeUuid1, dataNodeUuid2);
        final String cmdLine = String.format("hdfs diskbalancer %s", planArg);
        List<String> outputs = runCommand(cmdLine, cluster);
        verifyOutputsOfReportCommand(outputs, dataNodeUuid1, dataNodeUuid2, true);
    }

    @Test(timeout = 60000)
    public void testReportCommandWithInvalidNode() throws Exception {
        String dataNodeUuid1 = cluster.getDataNodes().get(0).getDatanodeUuid();
        String invalidNode = "invalidNode";
        final String planArg = String.format("-%s -%s %s,%s", DiskBalancerCLI.REPORT, DiskBalancerCLI.NODE, dataNodeUuid1, invalidNode);
        final String cmdLine = String.format("hdfs diskbalancer %s", planArg);
        List<String> outputs = runCommand(cmdLine, cluster);
        Assert.assertThat(outputs.get(0), CoreMatchers.containsString("Processing report command"));
        Assert.assertThat(outputs.get(1), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("Reporting volume information for DataNode"), CoreMatchers.containsString(dataNodeUuid1), CoreMatchers.containsString(invalidNode))));
        String invalidNodeInfo = String.format(("The node(s) '%s' not found. " + "Please make sure that '%s' exists in the cluster."), invalidNode, invalidNode);
        Assert.assertTrue(outputs.get(2).contains(invalidNodeInfo));
    }

    @Test(timeout = 60000)
    public void testReportCommandWithNullNodes() throws Exception {
        // don't input nodes
        final String planArg = String.format("-%s -%s ,", DiskBalancerCLI.REPORT, DiskBalancerCLI.NODE);
        final String cmdLine = String.format("hdfs diskbalancer %s", planArg);
        List<String> outputs = runCommand(cmdLine, cluster);
        String invalidNodeInfo = "The number of input nodes is 0. " + "Please input the valid nodes.";
        Assert.assertTrue(outputs.get(2).contains(invalidNodeInfo));
    }

    @Test(timeout = 60000)
    public void testReportCommandWithReadingHostFile() throws Exception {
        final String testDir = GenericTestUtils.getTestDir().getAbsolutePath();
        File includeFile = new File(testDir, "diskbalancer.include");
        String filePath = testDir + "/diskbalancer.include";
        String dataNodeUuid1 = cluster.getDataNodes().get(0).getDatanodeUuid();
        String dataNodeUuid2 = cluster.getDataNodes().get(1).getDatanodeUuid();
        FileWriter fw = new FileWriter(filePath);
        fw.write("#This-is-comment\n");
        fw.write((dataNodeUuid1 + "\n"));
        fw.write((dataNodeUuid2 + "\n"));
        fw.close();
        final String planArg = String.format("-%s -%s file://%s", DiskBalancerCLI.REPORT, DiskBalancerCLI.NODE, filePath);
        final String cmdLine = String.format("hdfs diskbalancer %s", planArg);
        List<String> outputs = runCommand(cmdLine, cluster);
        verifyOutputsOfReportCommand(outputs, dataNodeUuid1, dataNodeUuid2, false);
        includeFile.delete();
    }

    @Test(timeout = 60000)
    public void testReportCommandWithInvalidHostFilePath() throws Exception {
        final String testDir = GenericTestUtils.getTestDir().getAbsolutePath();
        String invalidFilePath = testDir + "/diskbalancer-invalid.include";
        final String planArg = String.format("-%s -%s file://%s", DiskBalancerCLI.REPORT, DiskBalancerCLI.NODE, invalidFilePath);
        final String cmdLine = String.format("hdfs diskbalancer %s", planArg);
        List<String> outputs = runCommand(cmdLine, cluster);
        String invalidNodeInfo = String.format("The input host file path 'file://%s' is not a valid path.", invalidFilePath);
        Assert.assertTrue(outputs.get(2).contains(invalidNodeInfo));
    }
}

