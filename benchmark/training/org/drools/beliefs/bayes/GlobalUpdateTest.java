/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.beliefs.bayes;


import java.util.ArrayList;
import java.util.List;
import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests that the iteration order for collect and distribute evidence is correct.
 * It tests from 4 different positions on the same network.  First individually for collect and then distribute, and then through globalUpdate request
 * Then it calls the globalUpdate that recurses the network and calls globalUpdate for each clique
 */
public class GlobalUpdateTest {
    Graph<BayesVariable> graph = new BayesNetwork();

    GraphNode x0 = GraphTest.addNode(graph);

    // 0
    // |
    // 3_2__1
    // |  |
    // 4  5
    // |
    // 7__6__8
    JunctionTreeClique n0 = new JunctionTreeClique(0, graph, GraphTest.bitSet("1"));

    JunctionTreeClique n1 = new JunctionTreeClique(1, graph, GraphTest.bitSet("1"));

    JunctionTreeClique n2 = new JunctionTreeClique(2, graph, GraphTest.bitSet("1"));

    JunctionTreeClique n3 = new JunctionTreeClique(3, graph, GraphTest.bitSet("1"));

    JunctionTreeClique n4 = new JunctionTreeClique(4, graph, GraphTest.bitSet("1"));

    JunctionTreeClique n5 = new JunctionTreeClique(5, graph, GraphTest.bitSet("1"));

    JunctionTreeClique n6 = new JunctionTreeClique(6, graph, GraphTest.bitSet("1"));

    JunctionTreeClique n7 = new JunctionTreeClique(7, graph, GraphTest.bitSet("1"));

    JunctionTreeClique n8 = new JunctionTreeClique(8, graph, GraphTest.bitSet("1"));

    JunctionTree tree;

    BayesInstance bayesInstance;

    final List<String> messageResults = new ArrayList<String>();

    final List<String> globalUpdateResults = new ArrayList<String>();

    @Test
    public void testCollectFromRootClique() {
        bayesInstance.collectEvidence(n0);
        Assert.assertEquals(asList("3:2", "4:2", "2:1", "7:6", "8:6", "6:5", "5:1", "1:0"), messageResults);
    }

    @Test
    public void testCollectFromMidTipClique() {
        bayesInstance.collectEvidence(n4);
        Assert.assertEquals(asList("0:1", "7:6", "8:6", "6:5", "5:1", "1:2", "3:2", "2:4"), messageResults);
    }

    @Test
    public void testCollectFromEndTipClique() {
        bayesInstance.collectEvidence(n7);
        Assert.assertEquals(asList("0:1", "3:2", "4:2", "2:1", "1:5", "5:6", "8:6", "6:7"), messageResults);
    }

    @Test
    public void testCollectFromMidClique() {
        bayesInstance.collectEvidence(n5);
        Assert.assertEquals(asList("0:1", "3:2", "4:2", "2:1", "1:5", "7:6", "8:6", "6:5"), messageResults);
    }

    @Test
    public void testDistributeFromRootClique() {
        bayesInstance.distributeEvidence(n0);
        Assert.assertEquals(asList("0:1", "1:2", "2:3", "2:4", "1:5", "5:6", "6:7", "6:8"), messageResults);
    }

    @Test
    public void testDistributeFromMidTipClique() {
        bayesInstance.distributeEvidence(n4);
        Assert.assertEquals(asList("4:2", "2:1", "1:0", "1:5", "5:6", "6:7", "6:8", "2:3"), messageResults);
    }

    @Test
    public void testDistributeFromEndTipClique() {
        bayesInstance.distributeEvidence(n7);
        Assert.assertEquals(asList("7:6", "6:5", "5:1", "1:0", "1:2", "2:3", "2:4", "6:8"), messageResults);
    }

    @Test
    public void testDistributeFromMidClique() {
        bayesInstance.distributeEvidence(n5);
        Assert.assertEquals(asList("5:1", "1:0", "1:2", "2:3", "2:4", "5:6", "6:7", "6:8"), messageResults);
    }

    @Test
    public void testGlobalUpdateFromRootClique() {
        bayesInstance.globalUpdate(n0);
        Assert.assertEquals(// n0
        // n0
        asList("3:2", "4:2", "2:1", "7:6", "8:6", "6:5", "5:1", "1:0", "0:1", "1:2", "2:3", "2:4", "1:5", "5:6", "6:7", "6:8"), messageResults);
        Assert.assertEquals(asList("0"), globalUpdateResults);
    }

    @Test
    public void testGlobalUpdateFromMidTipClique() {
        bayesInstance.globalUpdate(n4);
        Assert.assertEquals(// n4
        // n4
        asList("0:1", "7:6", "8:6", "6:5", "5:1", "1:2", "3:2", "2:4", "4:2", "2:1", "1:0", "1:5", "5:6", "6:7", "6:8", "2:3"), messageResults);
        Assert.assertEquals(asList("4"), globalUpdateResults);
    }

    @Test
    public void testGlobalUpdateFromEndTipClique() {
        bayesInstance.globalUpdate(n7);
        Assert.assertEquals(// n7
        // n7
        asList("0:1", "3:2", "4:2", "2:1", "1:5", "5:6", "8:6", "6:7", "7:6", "6:5", "5:1", "1:0", "1:2", "2:3", "2:4", "6:8"), messageResults);
        Assert.assertEquals(asList("7"), globalUpdateResults);
    }

    @Test
    public void testGlobalUpdateFromMidClique() {
        bayesInstance.globalUpdate(n5);
        Assert.assertEquals(// n5
        // n5
        asList("0:1", "3:2", "4:2", "2:1", "1:5", "7:6", "8:6", "6:5", "5:1", "1:0", "1:2", "2:3", "2:4", "5:6", "6:7", "6:8"), messageResults);
        Assert.assertEquals(asList("5"), globalUpdateResults);
    }

    @Test
    public void testDistributeFromGlobalUpdate() {
        bayesInstance.globalUpdate();
        Assert.assertEquals(// n0
        // n0
        // "0:1", "3:2", "4:2", "2:1", "7:6", "8:6", "6:5", "5:1", //n1
        // "1:0", "1:2", "2:3", "2:4", "1:5", "5:6", "6:7", "6:8", //n1
        // "0:1", "7:6", "8:6", "6:5", "5:1", "1:2", "3:2", "4:2", //n2
        // "2:1", "1:0", "1:5", "5:6", "6:7", "6:8", "2:3", "2:4", //n2
        // "0:1", "7:6", "8:6", "6:5", "5:1", "1:2", "4:2", "2:3", //n3
        // "3:2", "2:1", "1:0", "1:5", "5:6", "6:7", "6:8", "2:4", //n3
        // "0:1", "7:6", "8:6", "6:5", "5:1", "1:2", "3:2", "2:4", //n4
        // "4:2", "2:1", "1:0", "1:5", "5:6", "6:7", "6:8", "2:3", //n4
        // "0:1", "3:2", "4:2", "2:1", "1:5", "7:6", "8:6", "6:5", //n5
        // "5:1", "1:0", "1:2", "2:3", "2:4", "5:6", "6:7", "6:8", //n5
        // "0:1", "3:2", "4:2", "2:1", "1:5", "5:6", "7:6", "8:6", //n6
        // "6:5", "5:1", "1:0", "1:2", "2:3", "2:4", "6:7", "6:8", //n6
        // "0:1", "3:2", "4:2", "2:1", "1:5", "5:6", "8:6", "6:7", //n7
        // "7:6", "6:5", "5:1", "1:0", "1:2", "2:3", "2:4", "6:8", //n7
        // "0:1", "3:2", "4:2", "2:1", "1:5", "5:6", "7:6", "6:8", //n8
        // "8:6", "6:5", "5:1", "1:0", "1:2", "2:3", "2:4", "6:7"  //n8
        asList("3:2", "4:2", "2:1", "7:6", "8:6", "6:5", "5:1", "1:0", "0:1", "1:2", "2:3", "2:4", "1:5", "5:6", "6:7", "6:8"), messageResults);
        // assertEquals( asList( "0", "1", "2", "3", "4", "5", "6", "7", "8"), globalUpdateResults);
        Assert.assertEquals(asList("0"), globalUpdateResults);
    }
}

