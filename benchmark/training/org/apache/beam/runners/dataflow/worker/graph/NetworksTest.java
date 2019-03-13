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
package org.apache.beam.runners.dataflow.worker.graph;


import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Sets;
import org.apache.beam.vendor.guava.v20_0.com.google.common.graph.MutableNetwork;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link Networks}.
 */
@RunWith(JUnit4.class)
public class NetworksTest {
    @Test
    public void testTopologicalSortWithEmptyNetwork() {
        Assert.assertThat(Networks.topologicalOrder(NetworksTest.createEmptyNetwork()), empty());
    }

    @Test
    public void testTopologicalSort() {
        MutableNetwork<String, String> network = NetworksTest.createNetwork();
        Set<String> sortedNodes = Networks.topologicalOrder(network);
        Map<String, Integer> nodeToPosition = new HashMap<>(sortedNodes.size());
        int i = 0;
        for (String node : sortedNodes) {
            nodeToPosition.put(node, i);
            i += 1;
        }
        for (String node : network.nodes()) {
            for (String descendant : Sets.difference(Networks.reachableNodes(network, ImmutableSet.of(node), Collections.<String>emptySet()), Sets.newHashSet(node))) {
                Assert.assertThat(String.format(("Expected position of node %s to be before descendant %s," + " order returned %s for network %s"), node, descendant, sortedNodes, network), nodeToPosition.get(descendant), Matchers.greaterThan(nodeToPosition.get(node)));
            }
        }
    }

    @Test
    public void testReachableNodesWithEmptyNetwork() {
        Assert.assertThat(Networks.reachableNodes(NetworksTest.createEmptyNetwork(), Collections.<String>emptySet(), Collections.<String>emptySet()), empty());
    }

    @Test
    public void testReachableNodesFromAllRoots() {
        Assert.assertEquals(NetworksTest.createNetwork().nodes(), Networks.reachableNodes(NetworksTest.createNetwork(), ImmutableSet.of("A", "D", "I", "M", "O"), Collections.<String>emptySet()));
    }

    @Test
    public void testReachableNodesFromAllRootsToAllRoots() {
        Assert.assertEquals(ImmutableSet.of("A", "D", "I", "M", "O"), Networks.reachableNodes(NetworksTest.createNetwork(), ImmutableSet.of("A", "D", "I", "M", "O"), ImmutableSet.of("A", "D", "I", "M", "O")));
    }

    @Test
    public void testReachableNodesWithPathAroundBoundaryNode() {
        // Since there is a path around J, we will include E, G, and H
        Assert.assertEquals(ImmutableSet.of("I", "J", "E", "G", "H", "K", "L"), Networks.reachableNodes(NetworksTest.createNetwork(), ImmutableSet.of("I"), ImmutableSet.of("J")));
    }

    @Test
    public void testNodeReplacementInEmptyNetwork() {
        MutableNetwork<String, String> network = NetworksTest.createEmptyNetwork();
        Networks.replaceDirectedNetworkNodes(network, new Function<String, String>() {
            @Override
            @Nullable
            public String apply(@Nullable
            String input) {
                return input.toLowerCase();
            }
        });
        Assert.assertThat(network.nodes(), empty());
    }

    @Test
    public void testNodeReplacement() {
        Function<String, String> function = new Function<String, String>() {
            @Override
            public String apply(String input) {
                return ((("E".equals(input)) || ("J".equals(input))) || ("M".equals(input))) || ("O".equals(input)) ? input.toLowerCase() : input;
            }
        };
        Function<String, String> spiedFunction = Mockito.spy(function);
        MutableNetwork<String, String> network = NetworksTest.createNetwork();
        Networks.replaceDirectedNetworkNodes(network, spiedFunction);
        MutableNetwork<String, String> originalNetwork = NetworksTest.createNetwork();
        for (String node : originalNetwork.nodes()) {
            Assert.assertEquals(originalNetwork.successors(node).stream().map(function).collect(Collectors.toCollection(HashSet::new)), network.successors(function.apply(node)));
            // Ensure that the transform function was only called once
            Mockito.verify(spiedFunction, Mockito.times(1)).apply(node);
        }
        Assert.assertEquals(network.nodes(), originalNetwork.nodes().stream().map(function).collect(Collectors.toCollection(HashSet::new)));
    }

    @Test
    public void testAllPathsFromRootsToLeaves() {
        // Expected paths:
        // D
        // A, B, C, F
        // A, B, E, G
        // A, B, E, G (again)
        // A, B, E, H
        // I, J, E, G
        // I, J, E, G (again)
        // I, J, E, H
        // I, E, G
        // I, E, G (again)
        // I, E, H
        // I, K, L
        // M, N, L
        // M, N, L (again)
        // O
        List<List<String>> expectedPaths = ImmutableList.of(ImmutableList.of("D"), ImmutableList.of("A", "B", "C", "F"), ImmutableList.of("A", "B", "E", "G"), ImmutableList.of("A", "B", "E", "G"), ImmutableList.of("A", "B", "E", "H"), ImmutableList.of("I", "J", "E", "G"), ImmutableList.of("I", "J", "E", "G"), ImmutableList.of("I", "J", "E", "H"), ImmutableList.of("I", "E", "G"), ImmutableList.of("I", "E", "G"), ImmutableList.of("I", "E", "H"), ImmutableList.of("I", "K", "L"), ImmutableList.of("M", "N", "L"), ImmutableList.of("M", "N", "L"), ImmutableList.of("O"));
        MutableNetwork<String, String> network = NetworksTest.createNetwork();
        List<List<String>> actualPaths = Networks.allPathsFromRootsToLeaves(network);
        Assert.assertThat(actualPaths, Matchers.containsInAnyOrder(expectedPaths.toArray()));
        Assert.assertEquals(actualPaths.size(), expectedPaths.size());
    }
}

