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
package org.apache.hadoop.hdfs.tools;


import SystemErasureCodingPolicies.RS_10_4_POLICY_ID;
import SystemErasureCodingPolicies.RS_3_2_POLICY_ID;
import SystemErasureCodingPolicies.RS_6_3_POLICY_ID;
import SystemErasureCodingPolicies.XOR_2_1_POLICY_ID;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.SystemErasureCodingPolicies;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests some ECAdmin scenarios that are hard to test from
 * {@link org.apache.hadoop.cli.TestErasureCodingCLI}.
 */
public class TestECAdmin {
    public static final Logger LOG = LoggerFactory.getLogger(TestECAdmin.class);

    private Configuration conf = new Configuration();

    private MiniDFSCluster cluster;

    private ECAdmin admin = new ECAdmin(conf);

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private static final PrintStream OLD_OUT = System.out;

    private static final PrintStream OLD_ERR = System.err;

    private static final String RS_3_2 = SystemErasureCodingPolicies.getByID(RS_3_2_POLICY_ID).getName();

    private static final String RS_6_3 = SystemErasureCodingPolicies.getByID(RS_6_3_POLICY_ID).getName();

    private static final String RS_10_4 = SystemErasureCodingPolicies.getByID(RS_10_4_POLICY_ID).getName();

    private static final String XOR_2_1 = SystemErasureCodingPolicies.getByID(XOR_2_1_POLICY_ID).getName();

    @Rule
    public Timeout globalTimeout = new Timeout(300000, TimeUnit.MILLISECONDS);

    @Test
    public void testRS63MinDN() throws Exception {
        final int numDataNodes = 6;
        final int numRacks = 3;
        final int expectedNumDataNodes = 9;
        cluster = DFSTestUtil.setupCluster(conf, numDataNodes, numRacks, 0);
        int ret = runCommandWithParams("-verifyClusterSetup");
        Assert.assertEquals("Return value of the command is not successful", 2, ret);
        assertNotEnoughDataNodesMessage(TestECAdmin.RS_6_3, numDataNodes, expectedNumDataNodes);
    }

    @Test
    public void testRS104MinRacks() throws Exception {
        final String testPolicy = TestECAdmin.RS_10_4;
        final int numDataNodes = 15;
        final int numRacks = 3;
        final int expectedNumRacks = 4;
        cluster = DFSTestUtil.setupCluster(conf, numDataNodes, numRacks, 0);
        cluster.getFileSystem().disableErasureCodingPolicy(TestECAdmin.RS_6_3);
        cluster.getFileSystem().enableErasureCodingPolicy(testPolicy);
        int ret = runCommandWithParams("-verifyClusterSetup");
        Assert.assertEquals("Return value of the command is not successful", 2, ret);
        assertNotEnoughRacksMessage(testPolicy, numRacks, expectedNumRacks);
    }

    @Test
    public void testXOR21MinRacks() throws Exception {
        final String testPolicy = TestECAdmin.XOR_2_1;
        final int numDataNodes = 5;
        final int numRacks = 2;
        final int expectedNumRacks = 3;
        cluster = DFSTestUtil.setupCluster(conf, numDataNodes, numRacks, 0);
        cluster.getFileSystem().disableErasureCodingPolicy(TestECAdmin.RS_6_3);
        cluster.getFileSystem().enableErasureCodingPolicy(testPolicy);
        int ret = runCommandWithParams("-verifyClusterSetup");
        Assert.assertEquals("Return value of the command is not successful", 2, ret);
        assertNotEnoughRacksMessage(testPolicy, numRacks, expectedNumRacks);
    }

    @Test
    public void testRS32MinRacks() throws Exception {
        final String testPolicy = TestECAdmin.RS_3_2;
        final int numDataNodes = 5;
        final int numRacks = 2;
        final int expectedNumRacks = 3;
        cluster = DFSTestUtil.setupCluster(conf, numDataNodes, numRacks, 0);
        cluster.getFileSystem().disableErasureCodingPolicy(TestECAdmin.RS_6_3);
        cluster.getFileSystem().enableErasureCodingPolicy(testPolicy);
        int ret = runCommandWithParams("-verifyClusterSetup");
        Assert.assertEquals("Return value of the command is not successful", 2, ret);
        assertNotEnoughRacksMessage(testPolicy, numRacks, expectedNumRacks);
    }

    @Test
    public void testRS63Good() throws Exception {
        cluster = DFSTestUtil.setupCluster(conf, 9, 3, 0);
        int ret = runCommandWithParams("-verifyClusterSetup");
        Assert.assertEquals("Return value of the command is successful", 0, ret);
        Assert.assertTrue(("Result of cluster topology verify " + "should be logged correctly"), out.toString().contains(("The cluster setup can support EC policies: " + (TestECAdmin.RS_6_3))));
        Assert.assertTrue("Error output should be empty", err.toString().isEmpty());
    }

    @Test
    public void testNoECEnabled() throws Exception {
        cluster = DFSTestUtil.setupCluster(conf, 9, 3, 0);
        cluster.getFileSystem().disableErasureCodingPolicy(TestECAdmin.RS_6_3);
        int ret = runCommandWithParams("-verifyClusterSetup");
        Assert.assertEquals("Return value of the command is successful", 0, ret);
        Assert.assertTrue(("Result of cluster topology verify " + "should be logged correctly"), out.toString().contains("No erasure coding policy is given"));
        Assert.assertTrue("Error output should be empty", err.toString().isEmpty());
    }

    @Test
    public void testUnsuccessfulEnablePolicyMessage() throws Exception {
        final String testPolicy = TestECAdmin.RS_3_2;
        final int numDataNodes = 5;
        final int numRacks = 2;
        cluster = DFSTestUtil.setupCluster(conf, numDataNodes, numRacks, 0);
        cluster.getFileSystem().disableErasureCodingPolicy(TestECAdmin.RS_6_3);
        final int ret = runCommandWithParams("-enablePolicy", "-policy", testPolicy);
        Assert.assertEquals("Return value of the command is successful", 0, ret);
        Assert.assertTrue("Enabling policy should be logged", out.toString().contains((("Erasure coding policy " + testPolicy) + " is enabled")));
        Assert.assertTrue("Warning about cluster topology should be printed", err.toString().contains(((("Warning: The cluster setup does not support " + "EC policy ") + testPolicy) + ". Reason:")));
        Assert.assertTrue("Warning about cluster topology should be printed", err.toString().contains((" racks are required for the erasure coding policies: " + testPolicy)));
    }

    @Test
    public void testSuccessfulEnablePolicyMessage() throws Exception {
        final String testPolicy = TestECAdmin.RS_3_2;
        cluster = DFSTestUtil.setupCluster(conf, 5, 3, 0);
        cluster.getFileSystem().disableErasureCodingPolicy(TestECAdmin.RS_6_3);
        final int ret = runCommandWithParams("-enablePolicy", "-policy", testPolicy);
        Assert.assertEquals("Return value of the command is successful", 0, ret);
        Assert.assertTrue("Enabling policy should be logged", out.toString().contains((("Erasure coding policy " + testPolicy) + " is enabled")));
        Assert.assertFalse("Warning about cluster topology should not be printed", out.toString().contains("Warning: The cluster setup does not support"));
        Assert.assertTrue("Error output should be empty", err.toString().isEmpty());
    }

    @Test
    public void testEnableNonExistentPolicyMessage() throws Exception {
        cluster = DFSTestUtil.setupCluster(conf, 5, 3, 0);
        cluster.getFileSystem().disableErasureCodingPolicy(TestECAdmin.RS_6_3);
        final int ret = runCommandWithParams("-enablePolicy", "-policy", "NonExistentPolicy");
        Assert.assertEquals("Return value of the command is unsuccessful", 2, ret);
        Assert.assertFalse(("Enabling policy should not be logged when " + "it was unsuccessful"), out.toString().contains("is enabled"));
        Assert.assertTrue("Error message should be printed", err.toString().contains(("RemoteException: The policy name " + "NonExistentPolicy does not exist")));
    }

    @Test
    public void testVerifyClusterSetupWithGivenPolicies() throws Exception {
        final int numDataNodes = 5;
        final int numRacks = 2;
        cluster = DFSTestUtil.setupCluster(conf, numDataNodes, numRacks, 0);
        int ret = runCommandWithParams("-verifyClusterSetup", "-policy", TestECAdmin.RS_3_2);
        Assert.assertEquals("Return value of the command is not successful", 2, ret);
        assertNotEnoughRacksMessage(TestECAdmin.RS_3_2, numRacks, 3);
        resetOutputs();
        ret = runCommandWithParams("-verifyClusterSetup", "-policy", TestECAdmin.RS_10_4, TestECAdmin.RS_3_2);
        Assert.assertEquals("Return value of the command is not successful", 2, ret);
        assertNotEnoughDataNodesMessage((((TestECAdmin.RS_10_4) + ", ") + (TestECAdmin.RS_3_2)), numDataNodes, 14);
        resetOutputs();
        ret = runCommandWithParams("-verifyClusterSetup", "-policy", "invalidPolicy");
        Assert.assertEquals("Return value of the command is not successful", (-1), ret);
        Assert.assertTrue("Error message should be logged", err.toString().contains(("The given erasure coding policy invalidPolicy " + "does not exist.")));
        resetOutputs();
        ret = runCommandWithParams("-verifyClusterSetup", "-policy");
        Assert.assertEquals("Return value of the command is not successful", (-1), ret);
        Assert.assertTrue("Error message should be logged", err.toString().contains(("NotEnoughArgumentsException: Not enough arguments: " + "expected 1 but got 0")));
    }
}

