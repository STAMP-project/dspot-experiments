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
package org.apache.hadoop.yarn.client.cli;


import DecommissionType.FORCEFUL;
import DecommissionType.GRACEFUL;
import HAServiceProtocol.StateChangeRequestInfo;
import ResourceOption.OVER_COMMIT_TIMEOUT_MILLIS_DEFAULT;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Map;
import org.apache.hadoop.ha.HAServiceProtocol;
import org.apache.hadoop.ha.HAServiceStatus;
import org.apache.hadoop.service.Service.STATE;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceInformation;
import org.apache.hadoop.yarn.api.records.ResourceOption;
import org.apache.hadoop.yarn.nodelabels.CommonNodeLabelsManager;
import org.apache.hadoop.yarn.server.api.ResourceManagerAdministrationProtocol;
import org.apache.hadoop.yarn.server.api.protocolrecords.CheckForDecommissioningNodesRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.CheckForDecommissioningNodesResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshAdminAclsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshClusterMaxPriorityRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshNodesRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshQueuesRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshServiceAclsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshSuperUserGroupsConfigurationRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshUserToGroupsMappingsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.UpdateNodeResourceRequest;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestRMAdminCLI {
    private ResourceManagerAdministrationProtocol admin;

    private HAServiceProtocol haadmin;

    private RMAdminCLI rmAdminCLI;

    private RMAdminCLI rmAdminCLIWithHAEnabled;

    private CommonNodeLabelsManager dummyNodeLabelsManager;

    private boolean remoteAdminServiceAccessed = false;

    private static final String HOST_A = "1.2.3.1";

    private static final String HOST_B = "1.2.3.2";

    private static File dest;

    @Test
    public void testRefreshQueues() throws Exception {
        String[] args = new String[]{ "-refreshQueues" };
        Assert.assertEquals(0, rmAdminCLI.run(args));
        Mockito.verify(admin).refreshQueues(ArgumentMatchers.any(RefreshQueuesRequest.class));
    }

    @Test
    public void testRefreshUserToGroupsMappings() throws Exception {
        String[] args = new String[]{ "-refreshUserToGroupsMappings" };
        Assert.assertEquals(0, rmAdminCLI.run(args));
        Mockito.verify(admin).refreshUserToGroupsMappings(ArgumentMatchers.any(RefreshUserToGroupsMappingsRequest.class));
    }

    @Test
    public void testRefreshSuperUserGroupsConfiguration() throws Exception {
        String[] args = new String[]{ "-refreshSuperUserGroupsConfiguration" };
        Assert.assertEquals(0, rmAdminCLI.run(args));
        Mockito.verify(admin).refreshSuperUserGroupsConfiguration(ArgumentMatchers.any(RefreshSuperUserGroupsConfigurationRequest.class));
    }

    @Test
    public void testRefreshAdminAcls() throws Exception {
        String[] args = new String[]{ "-refreshAdminAcls" };
        Assert.assertEquals(0, rmAdminCLI.run(args));
        Mockito.verify(admin).refreshAdminAcls(ArgumentMatchers.any(RefreshAdminAclsRequest.class));
    }

    @Test
    public void testRefreshClusterMaxPriority() throws Exception {
        String[] args = new String[]{ "-refreshClusterMaxPriority" };
        Assert.assertEquals(0, rmAdminCLI.run(args));
        Mockito.verify(admin).refreshClusterMaxPriority(ArgumentMatchers.any(RefreshClusterMaxPriorityRequest.class));
    }

    @Test
    public void testRefreshServiceAcl() throws Exception {
        String[] args = new String[]{ "-refreshServiceAcl" };
        Assert.assertEquals(0, rmAdminCLI.run(args));
        Mockito.verify(admin).refreshServiceAcls(ArgumentMatchers.any(RefreshServiceAclsRequest.class));
    }

    @Test
    public void testUpdateNodeResource() throws Exception {
        String nodeIdStr = "0.0.0.0:0";
        int memSize = 2048;
        int cores = 2;
        String[] args = new String[]{ "-updateNodeResource", nodeIdStr, Integer.toString(memSize), Integer.toString(cores) };
        Assert.assertEquals(0, rmAdminCLI.run(args));
        ArgumentCaptor<UpdateNodeResourceRequest> argument = ArgumentCaptor.forClass(UpdateNodeResourceRequest.class);
        Mockito.verify(admin).updateNodeResource(argument.capture());
        UpdateNodeResourceRequest request = argument.getValue();
        Map<NodeId, ResourceOption> resourceMap = request.getNodeResourceMap();
        NodeId nodeId = NodeId.fromString(nodeIdStr);
        Resource expectedResource = Resources.createResource(memSize, cores);
        ResourceOption resource = resourceMap.get(nodeId);
        Assert.assertNotNull((("resource for " + nodeIdStr) + " shouldn't be null."), resource);
        Assert.assertEquals((("resource value for " + nodeIdStr) + " is not as expected."), ResourceOption.newInstance(expectedResource, OVER_COMMIT_TIMEOUT_MILLIS_DEFAULT), resource);
    }

    @Test
    public void testUpdateNodeResourceWithOverCommitTimeout() throws Exception {
        String nodeIdStr = "0.0.0.0:0";
        int memSize = 2048;
        int cores = 2;
        int timeout = 1000;
        String[] args = new String[]{ "-updateNodeResource", nodeIdStr, Integer.toString(memSize), Integer.toString(cores), Integer.toString(timeout) };
        Assert.assertEquals(0, rmAdminCLI.run(args));
        ArgumentCaptor<UpdateNodeResourceRequest> argument = ArgumentCaptor.forClass(UpdateNodeResourceRequest.class);
        Mockito.verify(admin).updateNodeResource(argument.capture());
        UpdateNodeResourceRequest request = argument.getValue();
        Map<NodeId, ResourceOption> resourceMap = request.getNodeResourceMap();
        NodeId nodeId = NodeId.fromString(nodeIdStr);
        Resource expectedResource = Resources.createResource(memSize, cores);
        ResourceOption resource = resourceMap.get(nodeId);
        Assert.assertNotNull((("resource for " + nodeIdStr) + " shouldn't be null."), resource);
        Assert.assertEquals((("resource value for " + nodeIdStr) + " is not as expected."), ResourceOption.newInstance(expectedResource, timeout), resource);
    }

    @Test
    public void testUpdateNodeResourceWithInvalidValue() throws Exception {
        String nodeIdStr = "0.0.0.0:0";
        int memSize = -2048;
        int cores = 2;
        String[] args = new String[]{ "-updateNodeResource", nodeIdStr, Integer.toString(memSize), Integer.toString(cores) };
        // execution of command line is expected to be failed
        Assert.assertEquals((-1), rmAdminCLI.run(args));
        // verify admin protocol never calls.
        Mockito.verify(admin, Mockito.times(0)).updateNodeResource(ArgumentMatchers.any(UpdateNodeResourceRequest.class));
    }

    @Test
    public void testUpdateNodeResourceTypes() throws Exception {
        String nodeIdStr = "0.0.0.0:0";
        String resourceTypes = "memory-mb=1Gi,vcores=1,resource1=3Gi,resource2=2m";
        String[] args = new String[]{ "-updateNodeResource", nodeIdStr, resourceTypes };
        Assert.assertEquals(0, rmAdminCLI.run(args));
        ArgumentCaptor<UpdateNodeResourceRequest> argument = ArgumentCaptor.forClass(UpdateNodeResourceRequest.class);
        Mockito.verify(admin).updateNodeResource(argument.capture());
        UpdateNodeResourceRequest request = argument.getValue();
        Map<NodeId, ResourceOption> resourceMap = request.getNodeResourceMap();
        NodeId nodeId = NodeId.fromString(nodeIdStr);
        Resource expectedResource = Resource.newInstance(1024, 1);
        expectedResource.setResourceInformation("resource1", ResourceInformation.newInstance("resource1", "Gi", 3));
        expectedResource.setResourceInformation("resource2", ResourceInformation.newInstance("resource2", "m", 2));
        ResourceOption resource = resourceMap.get(nodeId);
        // Ensure memory-mb has been converted to "Mi"
        Assert.assertEquals(1024, resource.getResource().getResourceInformation("memory-mb").getValue());
        Assert.assertEquals("Mi", resource.getResource().getResourceInformation("memory-mb").getUnits());
        Assert.assertNotNull((("resource for " + nodeIdStr) + " shouldn't be null."), resource);
        Assert.assertEquals((("resource value for " + nodeIdStr) + " is not as expected."), ResourceOption.newInstance(expectedResource, OVER_COMMIT_TIMEOUT_MILLIS_DEFAULT), resource);
    }

    @Test
    public void testUpdateNodeResourceTypesWithOverCommitTimeout() throws Exception {
        String nodeIdStr = "0.0.0.0:0";
        String resourceTypes = "memory-mb=1024Mi,vcores=1,resource1=3Gi,resource2=2m";
        int timeout = 1000;
        String[] args = new String[]{ "-updateNodeResource", nodeIdStr, resourceTypes, Integer.toString(timeout) };
        Assert.assertEquals(0, rmAdminCLI.run(args));
        ArgumentCaptor<UpdateNodeResourceRequest> argument = ArgumentCaptor.forClass(UpdateNodeResourceRequest.class);
        Mockito.verify(admin).updateNodeResource(argument.capture());
        UpdateNodeResourceRequest request = argument.getValue();
        Map<NodeId, ResourceOption> resourceMap = request.getNodeResourceMap();
        NodeId nodeId = NodeId.fromString(nodeIdStr);
        Resource expectedResource = Resource.newInstance(1024, 1);
        expectedResource.setResourceInformation("resource1", ResourceInformation.newInstance("resource1", "Gi", 3));
        expectedResource.setResourceInformation("resource2", ResourceInformation.newInstance("resource2", "m", 2));
        ResourceOption resource = resourceMap.get(nodeId);
        Assert.assertNotNull((("resource for " + nodeIdStr) + " shouldn't be null."), resource);
        Assert.assertEquals((("resource value for " + nodeIdStr) + " is not as expected."), ResourceOption.newInstance(expectedResource, timeout), resource);
    }

    @Test
    public void testUpdateNodeResourceTypesWithoutMandatoryResources() throws Exception {
        String nodeIdStr = "0.0.0.0:0";
        String resourceTypes = "resource1=3Gi,resource2=2m";
        String[] args = new String[]{ "-updateNodeResource", nodeIdStr, resourceTypes };
        Assert.assertEquals((-1), rmAdminCLI.run(args));
        // verify admin protocol never calls.
        Mockito.verify(admin, Mockito.times(0)).updateNodeResource(ArgumentMatchers.any(UpdateNodeResourceRequest.class));
    }

    @Test
    public void testUpdateNodeResourceTypesWithInvalidResource() throws Exception {
        String nodeIdStr = "0.0.0.0:0";
        String resourceTypes = "memory-mb=1024Mi,vcores=1,resource1=3Gi,resource3=2m";
        String[] args = new String[]{ "-updateNodeResource", nodeIdStr, resourceTypes };
        // execution of command line is expected to be failed
        Assert.assertEquals((-1), rmAdminCLI.run(args));
        // verify admin protocol never calls.
        Mockito.verify(admin, Mockito.times(0)).updateNodeResource(ArgumentMatchers.any(UpdateNodeResourceRequest.class));
    }

    @Test
    public void testUpdateNodeResourceTypesWithInvalidResourceValue() throws Exception {
        String nodeIdStr = "0.0.0.0:0";
        String resourceTypes = "memory-mb=1024Mi,vcores=1,resource1=ABDC,resource2=2m";
        String[] args = new String[]{ "-updateNodeResource", nodeIdStr, resourceTypes };
        // execution of command line is expected to be failed
        Assert.assertEquals((-1), rmAdminCLI.run(args));
        // verify admin protocol never calls.
        Mockito.verify(admin, Mockito.times(0)).updateNodeResource(ArgumentMatchers.any(UpdateNodeResourceRequest.class));
    }

    @Test
    public void testUpdateNodeResourceTypesWithInvalidResourceUnit() throws Exception {
        String nodeIdStr = "0.0.0.0:0";
        String resourceTypes = "memory-mb=1024Mi,vcores=1,resource1=2XYZ,resource2=2m";
        String[] args = new String[]{ "-updateNodeResource", nodeIdStr, resourceTypes };
        // execution of command line is expected to be failed
        Assert.assertEquals((-1), rmAdminCLI.run(args));
        // verify admin protocol never calls.
        Mockito.verify(admin, Mockito.times(0)).updateNodeResource(ArgumentMatchers.any(UpdateNodeResourceRequest.class));
    }

    @Test
    public void testUpdateNodeResourceTypesWithNonAlphaResourceUnit() throws Exception {
        String nodeIdStr = "0.0.0.0:0";
        String resourceTypes = "memory-mb=1024M i,vcores=1,resource1=2G,resource2=2m";
        String[] args = new String[]{ "-updateNodeResource", nodeIdStr, resourceTypes };
        // execution of command line is expected to be failed
        Assert.assertEquals((-1), rmAdminCLI.run(args));
        // verify admin protocol never calls.
        Mockito.verify(admin, Mockito.times(0)).updateNodeResource(ArgumentMatchers.any(UpdateNodeResourceRequest.class));
    }

    @Test
    public void testUpdateNodeResourceTypesWithInvalidResourceFormat() throws Exception {
        String nodeIdStr = "0.0.0.0:0";
        String resourceTypes = "memory-mb=1024Mi,vcores=1,resource2";
        String[] args = new String[]{ "-updateNodeResource", nodeIdStr, resourceTypes };
        // execution of command line is expected to be failed
        Assert.assertEquals((-1), rmAdminCLI.run(args));
        // verify admin protocol never calls.
        Mockito.verify(admin, Mockito.times(0)).updateNodeResource(ArgumentMatchers.any(UpdateNodeResourceRequest.class));
    }

    @Test
    public void testRefreshNodes() throws Exception {
        String[] args = new String[]{ "-refreshNodes" };
        Assert.assertEquals(0, rmAdminCLI.run(args));
        Mockito.verify(admin).refreshNodes(ArgumentMatchers.any(RefreshNodesRequest.class));
    }

    @Test
    public void testRefreshNodesGracefulBeforeTimeout() throws Exception {
        // graceful decommission before timeout
        String[] args = new String[]{ "-refreshNodes", "-g", "1", "-client" };
        CheckForDecommissioningNodesResponse response = Records.newRecord(CheckForDecommissioningNodesResponse.class);
        HashSet<NodeId> decomNodes = new HashSet<NodeId>();
        response.setDecommissioningNodes(decomNodes);
        Mockito.when(admin.checkForDecommissioningNodes(ArgumentMatchers.any(CheckForDecommissioningNodesRequest.class))).thenReturn(response);
        Assert.assertEquals(0, rmAdminCLI.run(args));
        Mockito.verify(admin).refreshNodes(RefreshNodesRequest.newInstance(GRACEFUL, 1));
        Mockito.verify(admin, Mockito.never()).refreshNodes(RefreshNodesRequest.newInstance(FORCEFUL));
    }

    @Test
    public void testRefreshNodesGracefulHitTimeout() throws Exception {
        // Forceful decommission when timeout occurs
        String[] forcefulDecomArgs = new String[]{ "-refreshNodes", "-g", "1", "-client" };
        HashSet<NodeId> decomNodes = new HashSet<NodeId>();
        CheckForDecommissioningNodesResponse response = Records.newRecord(CheckForDecommissioningNodesResponse.class);
        response.setDecommissioningNodes(decomNodes);
        decomNodes.add(NodeId.newInstance("node1", 100));
        response.setDecommissioningNodes(decomNodes);
        Mockito.when(admin.checkForDecommissioningNodes(ArgumentMatchers.any(CheckForDecommissioningNodesRequest.class))).thenReturn(response);
        Assert.assertEquals(0, rmAdminCLI.run(forcefulDecomArgs));
        Mockito.verify(admin).refreshNodes(RefreshNodesRequest.newInstance(FORCEFUL));
    }

    @Test
    public void testRefreshNodesGracefulInfiniteTimeout() throws Exception {
        String[] infiniteTimeoutArgs = new String[]{ "-refreshNodes", "-g", "-1", "-client" };
        testRefreshNodesGracefulInfiniteTimeout(infiniteTimeoutArgs);
    }

    @Test
    public void testRefreshNodesGracefulNoTimeout() throws Exception {
        // no timeout (infinite timeout)
        String[] noTimeoutArgs = new String[]{ "-refreshNodes", "-g", "-client" };
        testRefreshNodesGracefulInfiniteTimeout(noTimeoutArgs);
    }

    @Test
    public void testRefreshNodesGracefulInvalidArgs() throws Exception {
        // invalid graceful timeout parameter
        String[] invalidArgs = new String[]{ "-refreshNodes", "-ginvalid", "invalid", "-client" };
        Assert.assertEquals((-1), rmAdminCLI.run(invalidArgs));
        // invalid timeout
        String[] invalidTimeoutArgs = new String[]{ "-refreshNodes", "-g", "invalid", "-client" };
        Assert.assertEquals((-1), rmAdminCLI.run(invalidTimeoutArgs));
        // negative timeout
        String[] negativeTimeoutArgs = new String[]{ "-refreshNodes", "-g", "-1000", "-client" };
        Assert.assertEquals((-1), rmAdminCLI.run(negativeTimeoutArgs));
        // invalid tracking mode
        String[] invalidTrackingArgs = new String[]{ "-refreshNodes", "-g", "1", "-foo" };
        Assert.assertEquals((-1), rmAdminCLI.run(invalidTrackingArgs));
    }

    @Test
    public void testGetGroups() throws Exception {
        Mockito.when(admin.getGroupsForUser(ArgumentMatchers.eq("admin"))).thenReturn(new String[]{ "group1", "group2" });
        PrintStream origOut = System.out;
        PrintStream out = Mockito.mock(PrintStream.class);
        System.setOut(out);
        try {
            String[] args = new String[]{ "-getGroups", "admin" };
            Assert.assertEquals(0, rmAdminCLI.run(args));
            Mockito.verify(admin).getGroupsForUser(ArgumentMatchers.eq("admin"));
            Mockito.verify(out).println(ArgumentMatchers.argThat(((ArgumentMatcher<StringBuilder>) (( arg) -> ("" + arg).equals("admin : group1 group2")))));
        } finally {
            System.setOut(origOut);
        }
    }

    @Test
    public void testTransitionToActive() throws Exception {
        String[] args = new String[]{ "-transitionToActive", "rm1" };
        // RM HA is disabled.
        // transitionToActive should not be executed
        Assert.assertEquals((-1), rmAdminCLI.run(args));
        Mockito.verify(haadmin, Mockito.never()).transitionToActive(ArgumentMatchers.any(StateChangeRequestInfo.class));
        // Now RM HA is enabled.
        // transitionToActive should be executed
        Assert.assertEquals(0, rmAdminCLIWithHAEnabled.run(args));
        Mockito.verify(haadmin).transitionToActive(ArgumentMatchers.any(StateChangeRequestInfo.class));
        // HAAdmin#isOtherTargetNodeActive should check state of non-target node.
        Mockito.verify(haadmin, Mockito.times(1)).getServiceStatus();
    }

    @Test
    public void testTransitionToStandby() throws Exception {
        String[] args = new String[]{ "-transitionToStandby", "rm1" };
        // RM HA is disabled.
        // transitionToStandby should not be executed
        Assert.assertEquals((-1), rmAdminCLI.run(args));
        Mockito.verify(haadmin, Mockito.never()).transitionToStandby(ArgumentMatchers.any(StateChangeRequestInfo.class));
        // Now RM HA is enabled.
        // transitionToActive should be executed
        Assert.assertEquals(0, rmAdminCLIWithHAEnabled.run(args));
        Mockito.verify(haadmin).transitionToStandby(ArgumentMatchers.any(StateChangeRequestInfo.class));
    }

    @Test
    public void testGetServiceState() throws Exception {
        String[] args = new String[]{ "-getServiceState", "rm1" };
        // RM HA is disabled.
        // getServiceState should not be executed
        Assert.assertEquals((-1), rmAdminCLI.run(args));
        Mockito.verify(haadmin, Mockito.never()).getServiceStatus();
        // Now RM HA is enabled.
        // getServiceState should be executed
        Assert.assertEquals(0, rmAdminCLIWithHAEnabled.run(args));
        Mockito.verify(haadmin).getServiceStatus();
    }

    @Test
    public void testGetAllServiceState() throws Exception {
        HAServiceStatus standbyStatus = setReadyToBecomeActive();
        Mockito.doReturn(standbyStatus).when(haadmin).getServiceStatus();
        ByteArrayOutputStream dataOut = new ByteArrayOutputStream();
        rmAdminCLIWithHAEnabled.setOut(new PrintStream(dataOut));
        String[] args = new String[]{ "-getAllServiceState" };
        Assert.assertEquals(0, rmAdminCLIWithHAEnabled.run(args));
        Assert.assertTrue(dataOut.toString().contains(String.format("%-50s %-10s", (((TestRMAdminCLI.HOST_A) + ":") + 12346), standbyStatus.getState())));
        Assert.assertTrue(dataOut.toString().contains(String.format("%-50s %-10s", (((TestRMAdminCLI.HOST_B) + ":") + 12346), standbyStatus.getState())));
        rmAdminCLIWithHAEnabled.setOut(System.out);
    }

    @Test
    public void testCheckHealth() throws Exception {
        String[] args = new String[]{ "-checkHealth", "rm1" };
        // RM HA is disabled.
        // getServiceState should not be executed
        Assert.assertEquals((-1), rmAdminCLI.run(args));
        Mockito.verify(haadmin, Mockito.never()).monitorHealth();
        // Now RM HA is enabled.
        // getServiceState should be executed
        Assert.assertEquals(0, rmAdminCLIWithHAEnabled.run(args));
        Mockito.verify(haadmin).monitorHealth();
    }

    /**
     * Test printing of help messages
     */
    @Test
    public void testHelp() throws Exception {
        PrintStream oldOutPrintStream = System.out;
        PrintStream oldErrPrintStream = System.err;
        ByteArrayOutputStream dataOut = new ByteArrayOutputStream();
        ByteArrayOutputStream dataErr = new ByteArrayOutputStream();
        System.setOut(new PrintStream(dataOut));
        System.setErr(new PrintStream(dataErr));
        try {
            String[] args = new String[]{ "-help" };
            Assert.assertEquals(0, rmAdminCLI.run(args));
            oldOutPrintStream.println(dataOut);
            Assert.assertTrue(dataOut.toString().contains("rmadmin is the command to execute YARN administrative commands."));
            Assert.assertTrue(dataOut.toString().contains(("yarn rmadmin [-refreshQueues] [-refreshNodes " + (((((((((((((("[-g|graceful [timeout in seconds] -client|server]] " + "[-refreshNodesResources] [-refresh") + "SuperUserGroupsConfiguration] [-refreshUserToGroupsMappings] ") + "[-refreshAdminAcls] [-refreshServiceAcl] [-getGroup ") + "[username]] [-addToClusterNodeLabels ") + "<\"label1(exclusive=true),label2(exclusive=false),label3\">] ") + "[-removeFromClusterNodeLabels <label1,label2,label3>] ") + "[-replaceLabelsOnNode ") + "<\"node1[:port]=label1,label2 node2[:port]=label1\"> ") + "[-failOnUnknownNodes]] ") + "[-directlyAccessNodeLabelStore] [-refreshClusterMaxPriority] ") + "[-updateNodeResource [NodeID] [MemSize] [vCores] ") + "([OvercommitTimeout]) or -updateNodeResource ") + "[NodeID] [ResourceTypes] ([OvercommitTimeout])] ") + "[-help [cmd]]"))));
            Assert.assertTrue(dataOut.toString().contains(("-refreshQueues: Reload the queues' acls, states and scheduler " + "specific properties.")));
            Assert.assertTrue(dataOut.toString().contains(("-refreshNodes [-g|graceful [timeout in seconds]" + (" -client|server]: " + "Refresh the hosts information at the ResourceManager."))));
            Assert.assertTrue(dataOut.toString().contains(("-refreshNodesResources: Refresh resources of NodeManagers at the " + "ResourceManager.")));
            Assert.assertTrue(dataOut.toString().contains("-refreshUserToGroupsMappings: Refresh user-to-groups mappings"));
            Assert.assertTrue(dataOut.toString().contains(("-refreshSuperUserGroupsConfiguration: Refresh superuser proxy" + " groups mappings")));
            Assert.assertTrue(dataOut.toString().contains(("-refreshAdminAcls: Refresh acls for administration of " + "ResourceManager")));
            Assert.assertTrue(dataOut.toString().contains(("-refreshServiceAcl: Reload the service-level authorization" + " policy file")));
            Assert.assertTrue(dataOut.toString().contains(("-help [cmd]: Displays help for the given command or all " + "commands if none")));
            testError(new String[]{ "-help", "-refreshQueues" }, "Usage: yarn rmadmin [-refreshQueues]", dataErr, 0);
            testError(new String[]{ "-help", "-refreshNodes" }, ("Usage: yarn rmadmin [-refreshNodes [-g|graceful " + "[timeout in seconds] -client|server]]"), dataErr, 0);
            testError(new String[]{ "-help", "-refreshNodesResources" }, "Usage: yarn rmadmin [-refreshNodesResources]", dataErr, 0);
            testError(new String[]{ "-help", "-refreshUserToGroupsMappings" }, "Usage: yarn rmadmin [-refreshUserToGroupsMappings]", dataErr, 0);
            testError(new String[]{ "-help", "-refreshSuperUserGroupsConfiguration" }, "Usage: yarn rmadmin [-refreshSuperUserGroupsConfiguration]", dataErr, 0);
            testError(new String[]{ "-help", "-refreshAdminAcls" }, "Usage: yarn rmadmin [-refreshAdminAcls]", dataErr, 0);
            testError(new String[]{ "-help", "-refreshServiceAcl" }, "Usage: yarn rmadmin [-refreshServiceAcl]", dataErr, 0);
            testError(new String[]{ "-help", "-getGroups" }, "Usage: yarn rmadmin [-getGroups [username]]", dataErr, 0);
            testError(new String[]{ "-help", "-transitionToActive" }, ("Usage: yarn rmadmin [-transitionToActive [--forceactive]" + " <serviceId>]"), dataErr, 0);
            testError(new String[]{ "-help", "-transitionToStandby" }, "Usage: yarn rmadmin [-transitionToStandby <serviceId>]", dataErr, 0);
            testError(new String[]{ "-help", "-getServiceState" }, "Usage: yarn rmadmin [-getServiceState <serviceId>]", dataErr, 0);
            testError(new String[]{ "-help", "-checkHealth" }, "Usage: yarn rmadmin [-checkHealth <serviceId>]", dataErr, 0);
            testError(new String[]{ "-help", "-failover" }, ("Usage: yarn rmadmin " + ("[-failover [--forcefence] [--forceactive] " + "<serviceId> <serviceId>]")), dataErr, 0);
            testError(new String[]{ "-help", "-badParameter" }, "Usage: yarn rmadmin", dataErr, 0);
            testError(new String[]{ "-badParameter" }, "badParameter: Unknown command", dataErr, (-1));
            // Test -help when RM HA is enabled
            Assert.assertEquals(0, rmAdminCLIWithHAEnabled.run(args));
            oldOutPrintStream.println(dataOut);
            String expectedHelpMsg = "yarn rmadmin [-refreshQueues] [-refreshNodes [-g|graceful " + ((((((((((((((((("[timeout in seconds] -client|server]] " + "[-refreshNodesResources] [-refreshSuperUserGroupsConfiguration] ") + "[-refreshUserToGroupsMappings] ") + "[-refreshAdminAcls] [-refreshServiceAcl] [-getGroup") + " [username]] [-addToClusterNodeLabels <\"label1(exclusive=true),") + "label2(exclusive=false),label3\">]") + " [-removeFromClusterNodeLabels <label1,label2,label3>] [-replaceLabelsOnNode ") + "<\"node1[:port]=label1,label2 node2[:port]=label1\"> ") + "[-failOnUnknownNodes]] [-directlyAccessNodeLabelStore] ") + "[-refreshClusterMaxPriority] ") + "[-updateNodeResource [NodeID] [MemSize] [vCores] ") + "([OvercommitTimeout]) ") + "or -updateNodeResource [NodeID] [ResourceTypes] ") + "([OvercommitTimeout])] ") + "[-transitionToActive [--forceactive] <serviceId>] ") + "[-transitionToStandby <serviceId>] ") + "[-getServiceState <serviceId>] [-getAllServiceState] ") + "[-checkHealth <serviceId>] [-help [cmd]]");
            String actualHelpMsg = dataOut.toString();
            Assert.assertTrue(String.format((((("Help messages: %n " + actualHelpMsg) + " %n doesn't include expected ") + "messages: %n") + expectedHelpMsg)), actualHelpMsg.contains(expectedHelpMsg));
        } finally {
            System.setOut(oldOutPrintStream);
            System.setErr(oldErrPrintStream);
        }
    }

    @Test
    public void testException() throws Exception {
        PrintStream oldErrPrintStream = System.err;
        ByteArrayOutputStream dataErr = new ByteArrayOutputStream();
        System.setErr(new PrintStream(dataErr));
        try {
            Mockito.when(admin.refreshQueues(ArgumentMatchers.any(RefreshQueuesRequest.class))).thenThrow(new IOException("test exception"));
            String[] args = new String[]{ "-refreshQueues" };
            Assert.assertEquals((-1), rmAdminCLI.run(args));
            Mockito.verify(admin).refreshQueues(ArgumentMatchers.any(RefreshQueuesRequest.class));
            Assert.assertTrue(dataErr.toString().contains("refreshQueues: test exception"));
        } finally {
            System.setErr(oldErrPrintStream);
        }
    }

    @Test
    public void testAccessLocalNodeLabelManager() throws Exception {
        Assert.assertFalse(((dummyNodeLabelsManager.getServiceState()) == (STATE.STOPPED)));
        String[] args = new String[]{ "-addToClusterNodeLabels", "x,y", "-directlyAccessNodeLabelStore" };
        Assert.assertEquals(0, rmAdminCLI.run(args));
        Assert.assertTrue(dummyNodeLabelsManager.getClusterNodeLabelNames().containsAll(ImmutableSet.of("x", "y")));
        // reset localNodeLabelsManager
        dummyNodeLabelsManager.removeFromClusterNodeLabels(ImmutableSet.of("x", "y"));
        // change the sequence of "-directlyAccessNodeLabelStore" and labels,
        // should fail
        args = new String[]{ "-addToClusterNodeLabels", "-directlyAccessNodeLabelStore", "x,y" };
        Assert.assertEquals((-1), rmAdminCLI.run(args));
        // local node labels manager will be close after running
        Assert.assertTrue(((dummyNodeLabelsManager.getServiceState()) == (STATE.STOPPED)));
    }

    @Test
    public void testAccessRemoteNodeLabelManager() throws Exception {
        String[] args = new String[]{ "-addToClusterNodeLabels", "x,y" };
        Assert.assertEquals(0, rmAdminCLI.run(args));
        // localNodeLabelsManager shouldn't accessed
        Assert.assertTrue(dummyNodeLabelsManager.getClusterNodeLabelNames().isEmpty());
        // remote node labels manager accessed
        Assert.assertTrue(remoteAdminServiceAccessed);
    }

    @Test
    public void testAddToClusterNodeLabels() throws Exception {
        // successfully add labels
        String[] args = new String[]{ "-addToClusterNodeLabels", "x", "-directlyAccessNodeLabelStore" };
        Assert.assertEquals(0, rmAdminCLI.run(args));
        Assert.assertTrue(dummyNodeLabelsManager.getClusterNodeLabelNames().containsAll(ImmutableSet.of("x")));
        // no labels, should fail
        args = new String[]{ "-addToClusterNodeLabels" };
        Assert.assertTrue((0 != (rmAdminCLI.run(args))));
        // no labels, should fail
        args = new String[]{ "-addToClusterNodeLabels", "-directlyAccessNodeLabelStore" };
        Assert.assertTrue((0 != (rmAdminCLI.run(args))));
        // no labels, should fail at client validation
        args = new String[]{ "-addToClusterNodeLabels", " " };
        Assert.assertTrue((0 != (rmAdminCLI.run(args))));
        // no labels, should fail at client validation
        args = new String[]{ "-addToClusterNodeLabels", " , " };
        Assert.assertTrue((0 != (rmAdminCLI.run(args))));
        // successfully add labels
        args = new String[]{ "-addToClusterNodeLabels", ",x,,", "-directlyAccessNodeLabelStore" };
        Assert.assertEquals(0, rmAdminCLI.run(args));
        Assert.assertTrue(dummyNodeLabelsManager.getClusterNodeLabelNames().containsAll(ImmutableSet.of("x")));
    }

    @Test
    public void testAddToClusterNodeLabelsWithExclusivitySetting() throws Exception {
        // Parenthese not match
        String[] args = new String[]{ "-addToClusterNodeLabels", "x(" };
        Assert.assertTrue((0 != (rmAdminCLI.run(args))));
        args = new String[]{ "-addToClusterNodeLabels", "x)" };
        Assert.assertTrue((0 != (rmAdminCLI.run(args))));
        // Not expected key=value specifying inner parentese
        args = new String[]{ "-addToClusterNodeLabels", "x(key=value)" };
        Assert.assertTrue((0 != (rmAdminCLI.run(args))));
        // Not key is expected, but value not
        args = new String[]{ "-addToClusterNodeLabels", "x(exclusive=)" };
        Assert.assertTrue((0 != (rmAdminCLI.run(args))));
        // key=value both set
        args = new String[]{ "-addToClusterNodeLabels", "w,x(exclusive=true), y(exclusive=false),z()", "-directlyAccessNodeLabelStore" };
        Assert.assertTrue((0 == (rmAdminCLI.run(args))));
        Assert.assertTrue(dummyNodeLabelsManager.isExclusiveNodeLabel("w"));
        Assert.assertTrue(dummyNodeLabelsManager.isExclusiveNodeLabel("x"));
        Assert.assertFalse(dummyNodeLabelsManager.isExclusiveNodeLabel("y"));
        Assert.assertTrue(dummyNodeLabelsManager.isExclusiveNodeLabel("z"));
        // key=value both set, and some spaces need to be handled
        args = new String[]{ "-addToClusterNodeLabels", "a (exclusive= true) , b( exclusive =false),c  ", "-directlyAccessNodeLabelStore" };
        Assert.assertTrue((0 == (rmAdminCLI.run(args))));
        Assert.assertTrue(dummyNodeLabelsManager.isExclusiveNodeLabel("a"));
        Assert.assertFalse(dummyNodeLabelsManager.isExclusiveNodeLabel("b"));
        Assert.assertTrue(dummyNodeLabelsManager.isExclusiveNodeLabel("c"));
    }

    @Test
    public void testRemoveFromClusterNodeLabels() throws Exception {
        // Successfully remove labels
        dummyNodeLabelsManager.addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("x", "y"));
        String[] args = new String[]{ "-removeFromClusterNodeLabels", "x,,y", "-directlyAccessNodeLabelStore" };
        Assert.assertEquals(0, rmAdminCLI.run(args));
        Assert.assertTrue(dummyNodeLabelsManager.getClusterNodeLabelNames().isEmpty());
        // no labels, should fail
        args = new String[]{ "-removeFromClusterNodeLabels" };
        Assert.assertTrue((0 != (rmAdminCLI.run(args))));
        // no labels, should fail
        args = new String[]{ "-removeFromClusterNodeLabels", "-directlyAccessNodeLabelStore" };
        Assert.assertTrue((0 != (rmAdminCLI.run(args))));
        // no labels, should fail at client validation
        args = new String[]{ "-removeFromClusterNodeLabels", " " };
        Assert.assertTrue((0 != (rmAdminCLI.run(args))));
        // no labels, should fail at client validation
        args = new String[]{ "-removeFromClusterNodeLabels", ", " };
        Assert.assertTrue((0 != (rmAdminCLI.run(args))));
    }

    @Test
    public void testReplaceLabelsOnNode() throws Exception {
        // Successfully replace labels
        dummyNodeLabelsManager.addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("x", "y", "Y"));
        String[] args = new String[]{ "-replaceLabelsOnNode", "node1:8000,x node2:8000=y node3,x node4=Y", "-directlyAccessNodeLabelStore" };
        Assert.assertEquals(0, rmAdminCLI.run(args));
        Assert.assertTrue(dummyNodeLabelsManager.getNodeLabels().containsKey(NodeId.newInstance("node1", 8000)));
        Assert.assertTrue(dummyNodeLabelsManager.getNodeLabels().containsKey(NodeId.newInstance("node2", 8000)));
        Assert.assertTrue(dummyNodeLabelsManager.getNodeLabels().containsKey(NodeId.newInstance("node3", 0)));
        Assert.assertTrue(dummyNodeLabelsManager.getNodeLabels().containsKey(NodeId.newInstance("node4", 0)));
        // no labels, should fail
        args = new String[]{ "-replaceLabelsOnNode" };
        Assert.assertTrue((0 != (rmAdminCLI.run(args))));
        // no labels, should fail
        args = new String[]{ "-replaceLabelsOnNode", "-failOnUnknownNodes" };
        Assert.assertTrue((0 != (rmAdminCLI.run(args))));
        // no labels, should fail
        args = new String[]{ "-replaceLabelsOnNode", "-directlyAccessNodeLabelStore" };
        Assert.assertTrue((0 != (rmAdminCLI.run(args))));
        // no labels, should fail
        args = new String[]{ "-replaceLabelsOnNode", " " };
        Assert.assertTrue((0 != (rmAdminCLI.run(args))));
        args = new String[]{ "-replaceLabelsOnNode", ", " };
        Assert.assertTrue((0 != (rmAdminCLI.run(args))));
    }

    @Test
    public void testReplaceMultipleLabelsOnSingleNode() throws Exception {
        // Successfully replace labels
        dummyNodeLabelsManager.addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("x", "y"));
        String[] args = new String[]{ "-replaceLabelsOnNode", "node1,x,y", "-directlyAccessNodeLabelStore" };
        Assert.assertTrue((0 != (rmAdminCLI.run(args))));
    }

    @Test
    public void testRemoveLabelsOnNodes() throws Exception {
        // Successfully replace labels
        dummyNodeLabelsManager.addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("x", "y"));
        String[] args = new String[]{ "-replaceLabelsOnNode", "node1=x node2=y", "-directlyAccessNodeLabelStore" };
        Assert.assertTrue((0 == (rmAdminCLI.run(args))));
        args = new String[]{ "-replaceLabelsOnNode", "node1= node2=", "-directlyAccessNodeLabelStore" };
        Assert.assertTrue("Labels should get replaced even '=' is used ", (0 == (rmAdminCLI.run(args))));
    }

    @Test
    public void testRMHAErrorUsage() throws Exception {
        ByteArrayOutputStream errOutBytes = new ByteArrayOutputStream();
        rmAdminCLIWithHAEnabled.setErrOut(new PrintStream(errOutBytes));
        try {
            String[] args = new String[]{ "-failover" };
            Assert.assertEquals((-1), rmAdminCLIWithHAEnabled.run(args));
            String errOut = new String(errOutBytes.toByteArray(), Charsets.UTF_8);
            errOutBytes.reset();
            Assert.assertTrue(errOut.contains("Usage: rmadmin"));
        } finally {
            rmAdminCLIWithHAEnabled.setErrOut(System.err);
        }
    }
}

