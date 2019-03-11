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


import HdfsServerConstants.StartupOption.UPGRADE;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.qjournal.MiniQJMHACluster;
import org.apache.hadoop.hdfs.server.namenode.ha.BootstrapStandby;
import org.junit.Assert;
import org.junit.Test;


public class TestDFSAdminWithHA {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private MiniQJMHACluster cluster;

    private Configuration conf;

    private DFSAdmin admin;

    private static final PrintStream oldOut = System.out;

    private static final PrintStream oldErr = System.err;

    private static final String NSID = "ns1";

    private static String newLine = System.getProperty("line.separator");

    @Test(timeout = 30000)
    public void testSetSafeMode() throws Exception {
        setUpHaCluster(false);
        // Enter safemode
        int exitCode = admin.run(new String[]{ "-safemode", "enter" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        String message = "Safe mode is ON in.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
        // Get safemode
        exitCode = admin.run(new String[]{ "-safemode", "get" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        message = "Safe mode is ON in.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
        // Leave safemode
        exitCode = admin.run(new String[]{ "-safemode", "leave" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        message = "Safe mode is OFF in.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
        // Get safemode
        exitCode = admin.run(new String[]{ "-safemode", "get" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        message = "Safe mode is OFF in.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testSaveNamespace() throws Exception {
        setUpHaCluster(false);
        // Safe mode should be turned ON in order to create namespace image.
        int exitCode = admin.run(new String[]{ "-safemode", "enter" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        String message = "Safe mode is ON in.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
        exitCode = admin.run(new String[]{ "-saveNamespace" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        message = "Save namespace successful for.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testSaveNamespaceNN1UpNN2Down() throws Exception {
        setUpHaCluster(false);
        // Safe mode should be turned ON in order to create namespace image.
        int exitCode = admin.run(new String[]{ "-safemode", "enter" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        String message = "Safe mode is ON in.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
        cluster.getDfsCluster().shutdownNameNode(1);
        // 
        exitCode = admin.run(new String[]{ "-saveNamespace" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        message = (("Save namespace successful for.*" + (TestDFSAdminWithHA.newLine)) + "Save namespace failed for.*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }

    @Test(timeout = 30000)
    public void testSaveNamespaceNN1DownNN2Up() throws Exception {
        setUpHaCluster(false);
        // Safe mode should be turned ON in order to create namespace image.
        int exitCode = admin.run(new String[]{ "-safemode", "enter" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        String message = "Safe mode is ON in.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
        cluster.getDfsCluster().shutdownNameNode(0);
        exitCode = admin.run(new String[]{ "-saveNamespace" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        message = (("Save namespace failed for.*" + (TestDFSAdminWithHA.newLine)) + "Save namespace successful for.*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }

    @Test(timeout = 30000)
    public void testSaveNamespaceNN1DownNN2Down() throws Exception {
        setUpHaCluster(false);
        // Safe mode should be turned ON in order to create namespace image.
        int exitCode = admin.run(new String[]{ "-safemode", "enter" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        String message = "Safe mode is ON in.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
        cluster.getDfsCluster().shutdownNameNode(0);
        cluster.getDfsCluster().shutdownNameNode(1);
        exitCode = admin.run(new String[]{ "-saveNamespace" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        message = "Save namespace failed for.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testRestoreFailedStorage() throws Exception {
        setUpHaCluster(false);
        int exitCode = admin.run(new String[]{ "-restoreFailedStorage", "check" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        String message = "restoreFailedStorage is set to false for.*";
        // Default is false
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
        exitCode = admin.run(new String[]{ "-restoreFailedStorage", "true" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        message = "restoreFailedStorage is set to true for.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
        exitCode = admin.run(new String[]{ "-restoreFailedStorage", "false" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        message = "restoreFailedStorage is set to false for.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testRestoreFailedStorageNN1UpNN2Down() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(1);
        int exitCode = admin.run(new String[]{ "-restoreFailedStorage", "check" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = (("restoreFailedStorage is set to false for.*" + (TestDFSAdminWithHA.newLine)) + "restoreFailedStorage failed for.*") + (TestDFSAdminWithHA.newLine);
        // Default is false
        assertOutputMatches(message);
        exitCode = admin.run(new String[]{ "-restoreFailedStorage", "true" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        message = (("restoreFailedStorage is set to true for.*" + (TestDFSAdminWithHA.newLine)) + "restoreFailedStorage failed for.*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
        exitCode = admin.run(new String[]{ "-restoreFailedStorage", "false" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        message = (("restoreFailedStorage is set to false for.*" + (TestDFSAdminWithHA.newLine)) + "restoreFailedStorage failed for.*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }

    @Test(timeout = 30000)
    public void testRestoreFailedStorageNN1DownNN2Up() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(0);
        int exitCode = admin.run(new String[]{ "-restoreFailedStorage", "check" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = (("restoreFailedStorage failed for.*" + (TestDFSAdminWithHA.newLine)) + "restoreFailedStorage is set to false for.*") + (TestDFSAdminWithHA.newLine);
        // Default is false
        assertOutputMatches(message);
        exitCode = admin.run(new String[]{ "-restoreFailedStorage", "true" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        message = (("restoreFailedStorage failed for.*" + (TestDFSAdminWithHA.newLine)) + "restoreFailedStorage is set to true for.*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
        exitCode = admin.run(new String[]{ "-restoreFailedStorage", "false" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        message = (("restoreFailedStorage failed for.*" + (TestDFSAdminWithHA.newLine)) + "restoreFailedStorage is set to false for.*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }

    @Test(timeout = 30000)
    public void testRestoreFailedStorageNN1DownNN2Down() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(0);
        cluster.getDfsCluster().shutdownNameNode(1);
        int exitCode = admin.run(new String[]{ "-restoreFailedStorage", "check" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = "restoreFailedStorage failed for.*";
        // Default is false
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
        exitCode = admin.run(new String[]{ "-restoreFailedStorage", "true" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        message = "restoreFailedStorage failed for.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
        exitCode = admin.run(new String[]{ "-restoreFailedStorage", "false" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        message = "restoreFailedStorage failed for.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testRefreshNodes() throws Exception {
        setUpHaCluster(false);
        int exitCode = admin.run(new String[]{ "-refreshNodes" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        String message = "Refresh nodes successful for.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testRefreshNodesNN1UpNN2Down() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(1);
        int exitCode = admin.run(new String[]{ "-refreshNodes" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = (("Refresh nodes successful for.*" + (TestDFSAdminWithHA.newLine)) + "Refresh nodes failed for.*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }

    @Test(timeout = 30000)
    public void testRefreshNodesNN1DownNN2Up() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(0);
        int exitCode = admin.run(new String[]{ "-refreshNodes" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = (("Refresh nodes failed for.*" + (TestDFSAdminWithHA.newLine)) + "Refresh nodes successful for.*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }

    @Test(timeout = 30000)
    public void testRefreshNodesNN1DownNN2Down() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(0);
        cluster.getDfsCluster().shutdownNameNode(1);
        int exitCode = admin.run(new String[]{ "-refreshNodes" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = "Refresh nodes failed for.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testSetBalancerBandwidth() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().transitionToActive(0);
        int exitCode = admin.run(new String[]{ "-setBalancerBandwidth", "10" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        String message = "Balancer bandwidth is set to 10";
        assertOutputMatches((message + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testSetBalancerBandwidthNN1UpNN2Down() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(1);
        cluster.getDfsCluster().transitionToActive(0);
        int exitCode = admin.run(new String[]{ "-setBalancerBandwidth", "10" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        String message = "Balancer bandwidth is set to 10";
        assertOutputMatches((message + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testSetBalancerBandwidthNN1DownNN2Up() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(0);
        cluster.getDfsCluster().transitionToActive(1);
        int exitCode = admin.run(new String[]{ "-setBalancerBandwidth", "10" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        String message = "Balancer bandwidth is set to 10";
        assertOutputMatches((message + (TestDFSAdminWithHA.newLine)));
    }

    @Test
    public void testSetBalancerBandwidthNN1DownNN2Down() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(0);
        cluster.getDfsCluster().shutdownNameNode(1);
        int exitCode = admin.run(new String[]{ "-setBalancerBandwidth", "10" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = (("Balancer bandwidth is set failed." + (TestDFSAdminWithHA.newLine)) + ".*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }

    @Test(timeout = 30000)
    public void testSetNegativeBalancerBandwidth() throws Exception {
        setUpHaCluster(false);
        int exitCode = admin.run(new String[]{ "-setBalancerBandwidth", "-10" });
        Assert.assertEquals("Negative bandwidth value must fail the command", (-1), exitCode);
    }

    @Test(timeout = 30000)
    public void testMetaSave() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().transitionToActive(0);
        int exitCode = admin.run(new String[]{ "-metasave", "dfs.meta" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        String messageFromActiveNN = "Created metasave file dfs.meta " + "in the log directory of namenode.*";
        String messageFromStandbyNN = "Skip Standby NameNode, since it " + "cannot perform metasave operation";
        assertOutputMatches((((messageFromActiveNN + (TestDFSAdminWithHA.newLine)) + messageFromStandbyNN) + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testMetaSaveNN1UpNN2Down() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().transitionToActive(0);
        cluster.getDfsCluster().shutdownNameNode(1);
        int exitCode = admin.run(new String[]{ "-metasave", "dfs.meta" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = (((("Created metasave file dfs.meta in the log directory" + " of namenode.*") + (TestDFSAdminWithHA.newLine)) + "Created metasave file dfs.meta in the log directory") + " of namenode.*failed") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }

    @Test(timeout = 30000)
    public void testMetaSaveNN1DownNN2Up() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().transitionToActive(1);
        cluster.getDfsCluster().shutdownNameNode(0);
        int exitCode = admin.run(new String[]{ "-metasave", "dfs.meta" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = (((("Created metasave file dfs.meta in the log directory" + " of namenode.*failed") + (TestDFSAdminWithHA.newLine)) + "Created metasave file dfs.meta in the log directory") + " of namenode.*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }

    @Test(timeout = 30000)
    public void testMetaSaveNN1DownNN2Down() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(0);
        cluster.getDfsCluster().shutdownNameNode(1);
        int exitCode = admin.run(new String[]{ "-metasave", "dfs.meta" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = "Created metasave file dfs.meta in the log directory" + " of namenode.*failed";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testRefreshServiceAcl() throws Exception {
        setUpHaCluster(true);
        int exitCode = admin.run(new String[]{ "-refreshServiceAcl" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        String message = "Refresh service acl successful for.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testRefreshServiceAclNN1UpNN2Down() throws Exception {
        setUpHaCluster(true);
        cluster.getDfsCluster().shutdownNameNode(1);
        int exitCode = admin.run(new String[]{ "-refreshServiceAcl" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = (("Refresh service acl successful for.*" + (TestDFSAdminWithHA.newLine)) + "Refresh service acl failed for.*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }

    @Test(timeout = 30000)
    public void testRefreshServiceAclNN1DownNN2Up() throws Exception {
        setUpHaCluster(true);
        cluster.getDfsCluster().shutdownNameNode(0);
        int exitCode = admin.run(new String[]{ "-refreshServiceAcl" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = (("Refresh service acl failed for.*" + (TestDFSAdminWithHA.newLine)) + "Refresh service acl successful for.*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }

    @Test(timeout = 30000)
    public void testRefreshServiceAclNN1DownNN2Down() throws Exception {
        setUpHaCluster(true);
        cluster.getDfsCluster().shutdownNameNode(0);
        cluster.getDfsCluster().shutdownNameNode(1);
        int exitCode = admin.run(new String[]{ "-refreshServiceAcl" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = "Refresh service acl failed for.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testRefreshUserToGroupsMappings() throws Exception {
        setUpHaCluster(false);
        int exitCode = admin.run(new String[]{ "-refreshUserToGroupsMappings" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        String message = "Refresh user to groups mapping successful for.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testRefreshUserToGroupsMappingsNN1UpNN2Down() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(1);
        int exitCode = admin.run(new String[]{ "-refreshUserToGroupsMappings" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = (("Refresh user to groups mapping successful for.*" + (TestDFSAdminWithHA.newLine)) + "Refresh user to groups mapping failed for.*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }

    @Test(timeout = 30000)
    public void testRefreshUserToGroupsMappingsNN1DownNN2Up() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(0);
        int exitCode = admin.run(new String[]{ "-refreshUserToGroupsMappings" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = (("Refresh user to groups mapping failed for.*" + (TestDFSAdminWithHA.newLine)) + "Refresh user to groups mapping successful for.*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }

    @Test(timeout = 30000)
    public void testRefreshUserToGroupsMappingsNN1DownNN2Down() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(0);
        cluster.getDfsCluster().shutdownNameNode(1);
        int exitCode = admin.run(new String[]{ "-refreshUserToGroupsMappings" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = "Refresh user to groups mapping failed for.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testRefreshSuperUserGroupsConfiguration() throws Exception {
        setUpHaCluster(false);
        int exitCode = admin.run(new String[]{ "-refreshSuperUserGroupsConfiguration" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        String message = "Refresh super user groups configuration successful for.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testRefreshSuperUserGroupsConfigurationNN1UpNN2Down() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(1);
        int exitCode = admin.run(new String[]{ "-refreshSuperUserGroupsConfiguration" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = (("Refresh super user groups configuration successful for.*" + (TestDFSAdminWithHA.newLine)) + "Refresh super user groups configuration failed for.*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }

    @Test(timeout = 30000)
    public void testRefreshSuperUserGroupsConfigurationNN1DownNN2Up() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(0);
        int exitCode = admin.run(new String[]{ "-refreshSuperUserGroupsConfiguration" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = (("Refresh super user groups configuration failed for.*" + (TestDFSAdminWithHA.newLine)) + "Refresh super user groups configuration successful for.*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }

    @Test(timeout = 30000)
    public void testRefreshSuperUserGroupsConfigurationNN1DownNN2Down() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(0);
        cluster.getDfsCluster().shutdownNameNode(1);
        int exitCode = admin.run(new String[]{ "-refreshSuperUserGroupsConfiguration" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = "Refresh super user groups configuration failed for.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testRefreshCallQueue() throws Exception {
        setUpHaCluster(false);
        int exitCode = admin.run(new String[]{ "-refreshCallQueue" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        String message = "Refresh call queue successful for.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testRefreshCallQueueNN1UpNN2Down() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(1);
        int exitCode = admin.run(new String[]{ "-refreshCallQueue" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = (("Refresh call queue successful for.*" + (TestDFSAdminWithHA.newLine)) + "Refresh call queue failed for.*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }

    @Test(timeout = 30000)
    public void testRefreshCallQueueNN1DownNN2Up() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(0);
        int exitCode = admin.run(new String[]{ "-refreshCallQueue" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = (("Refresh call queue failed for.*" + (TestDFSAdminWithHA.newLine)) + "Refresh call queue successful for.*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }

    @Test(timeout = 30000)
    public void testRefreshCallQueueNN1DownNN2Down() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(0);
        cluster.getDfsCluster().shutdownNameNode(1);
        int exitCode = admin.run(new String[]{ "-refreshCallQueue" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = "Refresh call queue failed for.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testFinalizeUpgrade() throws Exception {
        setUpHaCluster(false);
        int exitCode = admin.run(new String[]{ "-finalizeUpgrade" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = ".*Cannot finalize with no NameNode active";
        assertOutputMatches((message + (TestDFSAdminWithHA.newLine)));
        cluster.getDfsCluster().transitionToActive(0);
        exitCode = admin.run(new String[]{ "-finalizeUpgrade" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        message = "Finalize upgrade successful for.*";
        assertOutputMatches((((message + (TestDFSAdminWithHA.newLine)) + message) + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 30000)
    public void testFinalizeUpgradeNN1UpNN2Down() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(1);
        cluster.getDfsCluster().transitionToActive(0);
        int exitCode = admin.run(new String[]{ "-finalizeUpgrade" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = (("Finalize upgrade successful for .*" + (TestDFSAdminWithHA.newLine)) + "Finalize upgrade failed for .*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }

    @Test(timeout = 30000)
    public void testFinalizeUpgradeNN1DownNN2Up() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(0);
        cluster.getDfsCluster().transitionToActive(1);
        int exitCode = admin.run(new String[]{ "-finalizeUpgrade" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = (("Finalize upgrade failed for .*" + (TestDFSAdminWithHA.newLine)) + "Finalize upgrade successful for .*") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }

    @Test(timeout = 30000)
    public void testFinalizeUpgradeNN1DownNN2Down() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(0);
        cluster.getDfsCluster().shutdownNameNode(1);
        int exitCode = admin.run(new String[]{ "-finalizeUpgrade" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = ".*2 exceptions.*";
        assertOutputMatches((message + (TestDFSAdminWithHA.newLine)));
    }

    @Test(timeout = 300000)
    public void testUpgradeCommand() throws Exception {
        final String finalizedMsg = "Upgrade finalized for.*";
        final String notFinalizedMsg = "Upgrade not finalized for.*";
        final String failMsg = ("Getting upgrade status failed for.*" + (TestDFSAdminWithHA.newLine)) + "upgrade: .*";
        final String finalizeSuccessMsg = "Finalize upgrade successful for.*";
        setUpHaCluster(false);
        MiniDFSCluster dfsCluster = cluster.getDfsCluster();
        // Before upgrade is initialized, the query should return upgrade
        // finalized (as no upgrade is in progress)
        String message = ((finalizedMsg + (TestDFSAdminWithHA.newLine)) + finalizedMsg) + (TestDFSAdminWithHA.newLine);
        verifyUpgradeQueryOutput(message, 0);
        // Shutdown the NNs
        dfsCluster.shutdownNameNode(0);
        dfsCluster.shutdownNameNode(1);
        // Start NN1 with -upgrade option
        dfsCluster.getNameNodeInfos()[0].setStartOpt(UPGRADE);
        dfsCluster.restartNameNode(0, true);
        // Running -upgrade query should return "not finalized" for NN1 and
        // connection exception for NN2 (as NN2 is down)
        message = notFinalizedMsg + (TestDFSAdminWithHA.newLine);
        verifyUpgradeQueryOutput(message, (-1));
        String errorMsg = failMsg + (TestDFSAdminWithHA.newLine);
        verifyUpgradeQueryOutput(errorMsg, (-1));
        // Bootstrap the standby (NN2) with the upgraded info.
        int rc = BootstrapStandby.run(new String[]{ "-force" }, dfsCluster.getConfiguration(1));
        Assert.assertEquals(0, rc);
        out.reset();
        // Restart NN2.
        dfsCluster.restartNameNode(1);
        // Both NNs should return "not finalized" msg for -upgrade query
        message = ((notFinalizedMsg + (TestDFSAdminWithHA.newLine)) + notFinalizedMsg) + (TestDFSAdminWithHA.newLine);
        verifyUpgradeQueryOutput(message, 0);
        // Finalize the upgrade
        int exitCode = admin.run(new String[]{ "-upgrade", "finalize" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
        message = ((finalizeSuccessMsg + (TestDFSAdminWithHA.newLine)) + finalizeSuccessMsg) + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
        // NNs should return "upgrade finalized" msg
        message = ((finalizedMsg + (TestDFSAdminWithHA.newLine)) + finalizedMsg) + (TestDFSAdminWithHA.newLine);
        verifyUpgradeQueryOutput(message, 0);
    }

    @Test(timeout = 30000)
    public void testListOpenFilesNN1UpNN2Down() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(1);
        cluster.getDfsCluster().transitionToActive(0);
        int exitCode = admin.run(new String[]{ "-listOpenFiles" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
    }

    @Test(timeout = 30000)
    public void testListOpenFilesNN1DownNN2Up() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(0);
        cluster.getDfsCluster().transitionToActive(1);
        int exitCode = admin.run(new String[]{ "-listOpenFiles" });
        Assert.assertEquals(err.toString().trim(), 0, exitCode);
    }

    @Test
    public void testListOpenFilesNN1DownNN2Down() throws Exception {
        setUpHaCluster(false);
        cluster.getDfsCluster().shutdownNameNode(0);
        cluster.getDfsCluster().shutdownNameNode(1);
        int exitCode = admin.run(new String[]{ "-listOpenFiles" });
        Assert.assertNotEquals(err.toString().trim(), 0, exitCode);
        String message = ((".*" + (TestDFSAdminWithHA.newLine)) + "List open files failed.") + (TestDFSAdminWithHA.newLine);
        assertOutputMatches(message);
    }
}

