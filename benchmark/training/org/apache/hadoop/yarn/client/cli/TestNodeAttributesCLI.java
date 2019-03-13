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


import AttributeMappingOperationType.ADD;
import AttributeMappingOperationType.REMOVE;
import AttributeMappingOperationType.REPLACE;
import NodeAttributeType.STRING;
import NodeAttributesCLI.INVALID_COMMAND_USAGE;
import NodeAttributesCLI.INVALID_MAPPING_ERR_MSG;
import NodeAttributesCLI.MISSING_ARGUMENT;
import NodeAttributesCLI.NO_MAPPING_ERR_MSG;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.GetAttributesToNodesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetAttributesToNodesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodeAttributesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodeAttributesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetNodesToAttributesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetNodesToAttributesResponse;
import org.apache.hadoop.yarn.api.records.NodeAttribute;
import org.apache.hadoop.yarn.api.records.NodeAttributeInfo;
import org.apache.hadoop.yarn.api.records.NodeAttributeKey;
import org.apache.hadoop.yarn.api.records.NodeToAttributeValue;
import org.apache.hadoop.yarn.server.api.ResourceManagerAdministrationProtocol;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeToAttributes;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodesToAttributesMappingRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for TestNodeAttributesCLI.
 */
public class TestNodeAttributesCLI {
    private static final Logger LOG = LoggerFactory.getLogger(TestNodeAttributesCLI.class);

    private ResourceManagerAdministrationProtocol admin;

    private ApplicationClientProtocol client;

    private NodesToAttributesMappingRequest nodeToAttrRequest;

    private NodeAttributesCLI nodeAttributesCLI;

    private ByteArrayOutputStream errOutBytes = new ByteArrayOutputStream();

    private ByteArrayOutputStream sysOutBytes = new ByteArrayOutputStream();

    private String errOutput;

    private String sysOutput;

    @Test
    public void testHelp() throws Exception {
        String[] args = new String[]{ "-help", "-replace" };
        Assert.assertTrue("It should have succeeded help for replace", (0 == (runTool(args))));
        assertErrorContains(("-replace <\"node1:attribute[(type)][=value],attribute1" + "[=value],attribute2  node2:attribute2[=value],attribute3\">"));
        assertErrorContains(("Replace the node to attributes mapping information at" + ((((" the ResourceManager with the new mapping. Currently supported" + " attribute type. And string is the default type too. Attribute value") + " if not specified for string type value will be considered as empty") + " string. Replaced node-attributes should not violate the existing") + " attribute to attribute type mapping.")));
        args = new String[]{ "-help", "-remove" };
        Assert.assertTrue("It should have succeeded help for replace", (0 == (runTool(args))));
        assertErrorContains(("-remove <\"node1:attribute,attribute1" + " node2:attribute2\">"));
        assertErrorContains(("Removes the specified node to attributes mapping" + " information at the ResourceManager"));
        args = new String[]{ "-help", "-add" };
        Assert.assertTrue("It should have succeeded help for replace", (0 == (runTool(args))));
        assertErrorContains(("-add <\"node1:attribute[(type)][=value]," + ("attribute1[=value],attribute2  node2:attribute2[=value]," + "attribute3\">")));
        assertErrorContains(("Adds or updates the node to attributes mapping" + ((((" information at the ResourceManager. Currently supported attribute" + " type is string. And string is the default type too. Attribute value") + " if not specified for string type value will be considered as empty") + " string. Added or updated node-attributes should not violate the") + " existing attribute to attribute type mapping.")));
        args = new String[]{ "-help", "-failOnUnknownNodes" };
        Assert.assertTrue("It should have succeeded help for replace", (0 == (runTool(args))));
        assertErrorContains("-failOnUnknownNodes");
        assertErrorContains(("Can be used optionally along with [add,remove," + ("replace] options. When set, command will fail if specified nodes " + "are unknown.")));
        args = new String[]{ "-help", "-list" };
        Assert.assertTrue("It should have succeeded help for replace", (0 == (runTool(args))));
        assertErrorContains("-list");
        assertErrorContains("List all attributes in cluster");
        args = new String[]{ "-help", "-nodes" };
        Assert.assertTrue("It should have succeeded help for replace", (0 == (runTool(args))));
        assertErrorContains("-nodes");
        assertErrorContains(("Works with [list] to specify node hostnames whose mappings " + "are required to be displayed."));
        args = new String[]{ "-help", "-attributes" };
        Assert.assertTrue("It should have succeeded help for replace", (0 == (runTool(args))));
        assertErrorContains("-attributes");
        assertErrorContains(("Works with [attributestonodes] to specify attributes whose mapping " + "are required to be displayed."));
        args = new String[]{ "-help", "-attributestonodes" };
        Assert.assertTrue("It should have succeeded help for replace", (0 == (runTool(args))));
        assertErrorContains("-attributestonodes");
        assertErrorContains(("Displays mapping of attributes to nodes and attribute " + "values grouped by attributes"));
    }

    @Test
    public void testReplace() throws Exception {
        // --------------------------------
        // failure scenarios
        // --------------------------------
        // parenthesis not match
        String[] args = new String[]{ "-replace", "x(" };
        Assert.assertTrue("It should have failed as no node is specified", (0 != (runTool(args))));
        assertFailureMessageContains(INVALID_MAPPING_ERR_MSG);
        // parenthesis not match
        args = new String[]{ "-replace", "x:(=abc" };
        Assert.assertTrue("It should have failed as no closing parenthesis is not specified", (0 != (runTool(args))));
        assertFailureMessageContains("Attribute for node x is not properly configured : (=abc");
        args = new String[]{ "-replace", "x:()=abc" };
        Assert.assertTrue("It should have failed as no type specified inside parenthesis", (0 != (runTool(args))));
        assertFailureMessageContains("Attribute for node x is not properly configured : ()=abc");
        args = new String[]{ "-replace", ":x(string)" };
        Assert.assertTrue("It should have failed as no node is specified", (0 != (runTool(args))));
        assertFailureMessageContains("Node name cannot be empty");
        // Not expected key=value specifying inner parenthesis
        args = new String[]{ "-replace", "x:(key=value)" };
        Assert.assertTrue((0 != (runTool(args))));
        assertFailureMessageContains("Attribute for node x is not properly configured : (key=value)");
        // Should fail as no attributes specified
        args = new String[]{ "-replace" };
        Assert.assertTrue("Should fail as no attribute mappings specified", (0 != (runTool(args))));
        assertFailureMessageContains(MISSING_ARGUMENT);
        // no labels, should fail
        args = new String[]{ "-replace", "-failOnUnknownNodes", "x:key(string)=value,key2=val2" };
        Assert.assertTrue("Should fail as no attribute mappings specified for replace", (0 != (runTool(args))));
        assertFailureMessageContains(MISSING_ARGUMENT);
        // no labels, should fail
        args = new String[]{ "-replace", " " };
        Assert.assertTrue((0 != (runTool(args))));
        assertFailureMessageContains(NO_MAPPING_ERR_MSG);
        args = new String[]{ "-replace", ", " };
        Assert.assertTrue((0 != (runTool(args))));
        assertFailureMessageContains(INVALID_MAPPING_ERR_MSG);
        // --------------------------------
        // success scenarios
        // --------------------------------
        args = new String[]{ "-replace", "x:key(string)=value,key2=val2 y:key2=val23,key3 z:key4" };
        Assert.assertTrue("Should not fail as attribute has been properly mapped", (0 == (runTool(args))));
        List<NodeToAttributes> nodeAttributesList = new ArrayList<>();
        List<NodeAttribute> attributes = new ArrayList<>();
        attributes.add(NodeAttribute.newInstance("key", STRING, "value"));
        attributes.add(NodeAttribute.newInstance("key2", STRING, "val2"));
        nodeAttributesList.add(NodeToAttributes.newInstance("x", attributes));
        // for node y
        attributes = new ArrayList();
        attributes.add(NodeAttribute.newInstance("key2", STRING, "val23"));
        attributes.add(NodeAttribute.newInstance("key3", STRING, ""));
        nodeAttributesList.add(NodeToAttributes.newInstance("y", attributes));
        // for node y
        attributes = new ArrayList();
        attributes.add(NodeAttribute.newInstance("key2", STRING, "val23"));
        attributes.add(NodeAttribute.newInstance("key3", STRING, ""));
        nodeAttributesList.add(NodeToAttributes.newInstance("y", attributes));
        // for node z
        attributes = new ArrayList();
        attributes.add(NodeAttribute.newInstance("key4", STRING, ""));
        nodeAttributesList.add(NodeToAttributes.newInstance("z", attributes));
        NodesToAttributesMappingRequest expected = NodesToAttributesMappingRequest.newInstance(REPLACE, nodeAttributesList, false);
        Assert.assertTrue(nodeToAttrRequest.equals(expected));
    }

    @Test
    public void testRemove() throws Exception {
        // --------------------------------
        // failure scenarios
        // --------------------------------
        // parenthesis not match
        String[] args = new String[]{ "-remove", "x:" };
        Assert.assertTrue("It should have failed as no node is specified", (0 != (runTool(args))));
        assertFailureMessageContains(("Attributes cannot be null or empty for Operation [remove] on the " + "node x"));
        // --------------------------------
        // success scenarios
        // --------------------------------
        args = new String[]{ "-remove", "x:key2,key3 z:key4", "-failOnUnknownNodes" };
        Assert.assertTrue("Should not fail as attribute has been properly mapped", (0 == (runTool(args))));
        List<NodeToAttributes> nodeAttributesList = new ArrayList<>();
        List<NodeAttribute> attributes = new ArrayList<>();
        attributes.add(NodeAttribute.newInstance("key2", STRING, ""));
        attributes.add(NodeAttribute.newInstance("key3", STRING, ""));
        nodeAttributesList.add(NodeToAttributes.newInstance("x", attributes));
        // for node z
        attributes = new ArrayList();
        attributes.add(NodeAttribute.newInstance("key4", STRING, ""));
        nodeAttributesList.add(NodeToAttributes.newInstance("z", attributes));
        NodesToAttributesMappingRequest expected = NodesToAttributesMappingRequest.newInstance(REMOVE, nodeAttributesList, true);
        Assert.assertTrue(nodeToAttrRequest.equals(expected));
    }

    @Test
    public void testAdd() throws Exception {
        // --------------------------------
        // failure scenarios
        // --------------------------------
        // parenthesis not match
        String[] args = new String[]{ "-add", "x:" };
        Assert.assertTrue("It should have failed as no node is specified", (0 != (runTool(args))));
        assertFailureMessageContains("Attributes cannot be null or empty for Operation [add] on the node x");
        // --------------------------------
        // success scenarios
        // --------------------------------
        args = new String[]{ "-add", "x:key2=123,key3=abc z:key4(string)", "-failOnUnknownNodes" };
        Assert.assertTrue("Should not fail as attribute has been properly mapped", (0 == (runTool(args))));
        List<NodeToAttributes> nodeAttributesList = new ArrayList<>();
        List<NodeAttribute> attributes = new ArrayList<>();
        attributes.add(NodeAttribute.newInstance("key2", STRING, "123"));
        attributes.add(NodeAttribute.newInstance("key3", STRING, "abc"));
        nodeAttributesList.add(NodeToAttributes.newInstance("x", attributes));
        // for node z
        attributes = new ArrayList();
        attributes.add(NodeAttribute.newInstance("key4", STRING, ""));
        nodeAttributesList.add(NodeToAttributes.newInstance("z", attributes));
        NodesToAttributesMappingRequest expected = NodesToAttributesMappingRequest.newInstance(ADD, nodeAttributesList, true);
        Assert.assertTrue(nodeToAttrRequest.equals(expected));
        // --------------------------------
        // with Duplicate mappings for a host
        // --------------------------------
        args = new String[]{ "-add", "x:key2=123,key3=abc x:key4(string)", "-failOnUnknownNodes" };
        Assert.assertTrue("Should not fail as attribute has been properly mapped", (0 == (runTool(args))));
        nodeAttributesList = new ArrayList();
        attributes = new ArrayList();
        attributes.add(NodeAttribute.newInstance("key4", STRING, ""));
        nodeAttributesList.add(NodeToAttributes.newInstance("x", attributes));
        expected = NodesToAttributesMappingRequest.newInstance(ADD, nodeAttributesList, true);
        Assert.assertTrue(nodeToAttrRequest.equals(expected));
    }

    @Test
    public void testListAttributes() throws Exception {
        // GetClusterNodeAttributesRequest
        Mockito.when(client.getClusterNodeAttributes(ArgumentMatchers.any(GetClusterNodeAttributesRequest.class))).thenAnswer(new Answer<GetClusterNodeAttributesResponse>() {
            @Override
            public GetClusterNodeAttributesResponse answer(InvocationOnMock invocation) throws Throwable {
                GetClusterNodeAttributesRequest nodeAttrReq = ((GetClusterNodeAttributesRequest) (invocation.getArguments()[0]));
                return GetClusterNodeAttributesResponse.newInstance(ImmutableSet.of(NodeAttributeInfo.newInstance(NodeAttributeKey.newInstance("GPU"), STRING)));
            }
        });
        // --------------------------------
        // Success scenarios
        // --------------------------------
        String[] args = new String[]{ "-list" };
        Assert.assertTrue("It should be success since it list all attributes", (0 == (runTool(args))));
        assertSysOutContains("Attribute\t           Type", "rm.yarn.io/GPU\t         STRING");
    }

    @Test
    public void testNodeToAttributes() throws Exception {
        // GetNodesToAttributesRequest response
        Mockito.when(client.getNodesToAttributes(ArgumentMatchers.any(GetNodesToAttributesRequest.class))).thenAnswer(new Answer<GetNodesToAttributesResponse>() {
            @Override
            public GetNodesToAttributesResponse answer(InvocationOnMock invocation) throws Throwable {
                GetNodesToAttributesRequest nodeToAttributes = ((GetNodesToAttributesRequest) (invocation.getArguments()[0]));
                return GetNodesToAttributesResponse.newInstance(ImmutableMap.<String, Set<NodeAttribute>>builder().put("hostname", ImmutableSet.of(NodeAttribute.newInstance("GPU", STRING, "ARM"))).build());
            }
        });
        // --------------------------------
        // Failure scenarios
        // --------------------------------
        String[] args = new String[]{ "-nodetoattributes", "-nodes" };
        Assert.assertTrue("It should not success since nodes are not specified", (0 != (runTool(args))));
        assertErrorContains(INVALID_COMMAND_USAGE);
        // Missing argument for nodes
        args = new String[]{ "-nodestoattributes", "-nodes" };
        Assert.assertTrue("It should not success since nodes are not specified", (0 != (runTool(args))));
        assertErrorContains(MISSING_ARGUMENT);
        // --------------------------------
        // Success with hostname param
        // --------------------------------
        args = new String[]{ "-nodestoattributes", "-nodes", "hostname" };
        Assert.assertTrue("Should return hostname to attributed list", (0 == (runTool(args))));
        assertSysOutContains("hostname");
    }

    @Test
    public void testAttributesToNodes() throws Exception {
        // GetAttributesToNodesResponse response
        Mockito.when(client.getAttributesToNodes(ArgumentMatchers.any(GetAttributesToNodesRequest.class))).thenAnswer(new Answer<GetAttributesToNodesResponse>() {
            @Override
            public GetAttributesToNodesResponse answer(InvocationOnMock invocation) throws Throwable {
                GetAttributesToNodesRequest attrToNodes = ((GetAttributesToNodesRequest) (invocation.getArguments()[0]));
                return GetAttributesToNodesResponse.newInstance(ImmutableMap.<NodeAttributeKey, List<NodeToAttributeValue>>builder().put(NodeAttributeKey.newInstance("GPU"), ImmutableList.of(NodeToAttributeValue.newInstance("host1", "ARM"))).build());
            }
        });
        // --------------------------------
        // Success scenarios
        // --------------------------------
        String[] args = new String[]{ "-attributestonodes" };
        Assert.assertTrue("It should be success since it list all attributes", (0 == (runTool(args))));
        assertSysOutContains("Hostname\tAttribute-value", "rm.yarn.io/GPU :", "host1\t            ARM");
        // --------------------------------
        // fail scenario argument filter missing
        // --------------------------------
        args = new String[]{ "-attributestonodes", "-attributes" };
        Assert.assertTrue("It should not success since attributes for filter are not specified", (0 != (runTool(args))));
        assertErrorContains(MISSING_ARGUMENT);
        // --------------------------------
        // fail scenario argument filter missing
        // --------------------------------
        args = new String[]{ "-attributestonodes", "-attributes", "fail/da/fail" };
        Assert.assertTrue("It should not success since attributes format is not correct", (0 != (runTool(args))));
        assertErrorContains(("Attribute format not correct. Should be <[prefix]/[name]> " + ":fail/da/fail"));
    }
}

