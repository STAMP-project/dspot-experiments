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
package org.apache.hadoop.yarn.server.federation.resolver;


import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.federation.store.records.SubClusterId;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test {@link SubClusterResolver} against correct and malformed Federation
 * machine lists.
 */
public class TestDefaultSubClusterResolver {
    private static YarnConfiguration conf;

    private static SubClusterResolver resolver;

    @Test
    public void testGetSubClusterForNode() throws YarnException {
        TestDefaultSubClusterResolver.setUpGoodFile();
        // All lowercase, no whitespace in machine list file
        Assert.assertEquals(SubClusterId.newInstance("subcluster1"), TestDefaultSubClusterResolver.resolver.getSubClusterForNode("node1"));
        // Leading and trailing whitespace in machine list file
        Assert.assertEquals(SubClusterId.newInstance("subcluster2"), TestDefaultSubClusterResolver.resolver.getSubClusterForNode("node2"));
        // Node name capitalization in machine list file
        Assert.assertEquals(SubClusterId.newInstance("subcluster3"), TestDefaultSubClusterResolver.resolver.getSubClusterForNode("node3"));
        try {
            TestDefaultSubClusterResolver.resolver.getSubClusterForNode("nodeDoesNotExist");
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertTrue(e.getMessage().startsWith("Cannot find subClusterId for node"));
        }
    }

    @Test
    public void testGetSubClusterForNodeMalformedFile() throws YarnException {
        setUpMalformedFile();
        try {
            TestDefaultSubClusterResolver.resolver.getSubClusterForNode("node1");
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertTrue(e.getMessage().startsWith("Cannot find subClusterId for node"));
        }
        try {
            TestDefaultSubClusterResolver.resolver.getSubClusterForNode("node2");
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertTrue(e.getMessage().startsWith("Cannot find subClusterId for node"));
        }
        Assert.assertEquals(SubClusterId.newInstance("subcluster3"), TestDefaultSubClusterResolver.resolver.getSubClusterForNode("node3"));
        try {
            TestDefaultSubClusterResolver.resolver.getSubClusterForNode("nodeDoesNotExist");
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertTrue(e.getMessage().startsWith("Cannot find subClusterId for node"));
        }
    }

    @Test
    public void testGetSubClusterForNodeNoFile() throws YarnException {
        setUpNonExistentFile();
        try {
            TestDefaultSubClusterResolver.resolver.getSubClusterForNode("node1");
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertTrue(e.getMessage().startsWith("Cannot find subClusterId for node"));
        }
    }

    @Test
    public void testGetSubClustersForRack() throws YarnException {
        TestDefaultSubClusterResolver.setUpGoodFile();
        Set<SubClusterId> rack1Expected = new HashSet<SubClusterId>();
        rack1Expected.add(SubClusterId.newInstance("subcluster1"));
        rack1Expected.add(SubClusterId.newInstance("subcluster2"));
        Set<SubClusterId> rack2Expected = new HashSet<SubClusterId>();
        rack2Expected.add(SubClusterId.newInstance("subcluster3"));
        // Two subclusters have nodes in rack1
        Assert.assertEquals(rack1Expected, TestDefaultSubClusterResolver.resolver.getSubClustersForRack("rack1"));
        // Two nodes are in rack2, but both belong to subcluster3
        Assert.assertEquals(rack2Expected, TestDefaultSubClusterResolver.resolver.getSubClustersForRack("rack2"));
        try {
            TestDefaultSubClusterResolver.resolver.getSubClustersForRack("rackDoesNotExist");
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertTrue(e.getMessage().startsWith("Cannot resolve rack"));
        }
    }

    @Test
    public void testGetSubClustersForRackNoFile() throws YarnException {
        setUpNonExistentFile();
        try {
            TestDefaultSubClusterResolver.resolver.getSubClustersForRack("rack1");
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertTrue(e.getMessage().startsWith("Cannot resolve rack"));
        }
    }
}

