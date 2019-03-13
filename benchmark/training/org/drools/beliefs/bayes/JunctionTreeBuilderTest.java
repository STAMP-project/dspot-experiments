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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;
import org.drools.core.util.bitmask.OpenBitSet;
import org.junit.Assert;
import org.junit.Test;


public class JunctionTreeBuilderTest {
    @Test
    public void testOpenBitSet() {
        OpenBitSet b1 = GraphTest.bitSet("00000111");
        OpenBitSet b2 = GraphTest.bitSet("00000111");
        OpenBitSet b3 = GraphTest.bitSet("00000110");
        OpenBitSet b4 = GraphTest.bitSet("00001110");
        Assert.assertEquals(0, OpenBitSet.andNotCount(b1, b2));// b1 and b3 are equal

        Assert.assertEquals(1, OpenBitSet.andNotCount(b2, b3));// b2 is not a subset of b3

        Assert.assertEquals(0, OpenBitSet.andNotCount(b3, b2));// b3 is a subset of b2

        Assert.assertEquals(1, OpenBitSet.andNotCount(b2, b4));// b2 is not a subset of b4

        Assert.assertEquals(1, OpenBitSet.andNotCount(b4, b2));// b4 is not a subset of b3

    }

    @Test
    public void testMoralize1() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = GraphTest.addNode(graph);
        GraphNode x1 = GraphTest.addNode(graph);
        GraphNode x2 = GraphTest.addNode(graph);
        GraphNode x3 = GraphTest.addNode(graph);
        GraphNode x4 = GraphTest.addNode(graph);
        GraphTest.connectParentToChildren(x2, x1);
        GraphTest.connectParentToChildren(x3, x1);
        GraphTest.connectParentToChildren(x4, x1);
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        GraphTest.assertLinkedNode(jtBuilder, new int[]{ 1, 2, 3, 4 });
        GraphTest.assertLinkedNode(jtBuilder, new int[]{ 2, 1 });
        GraphTest.assertLinkedNode(jtBuilder, new int[]{ 3, 1 });
        GraphTest.assertLinkedNode(jtBuilder, new int[]{ 4, 1 });
        jtBuilder.moralize();
        GraphTest.assertLinkedNode(jtBuilder, new int[]{ 1, 2, 3, 4 });
        GraphTest.assertLinkedNode(jtBuilder, new int[]{ 2, 1, 3, 4 });
        GraphTest.assertLinkedNode(jtBuilder, new int[]{ 3, 1, 2, 4 });
        GraphTest.assertLinkedNode(jtBuilder, new int[]{ 4, 1, 2, 3 });
    }

    @Test
    public void testMoralize2() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = GraphTest.addNode(graph);
        GraphNode x1 = GraphTest.addNode(graph);
        GraphNode x2 = GraphTest.addNode(graph);
        GraphNode x3 = GraphTest.addNode(graph);
        GraphNode x4 = GraphTest.addNode(graph);
        GraphNode x5 = GraphTest.addNode(graph);
        GraphNode x6 = GraphTest.addNode(graph);
        GraphTest.connectParentToChildren(x1, x2, x3);
        GraphTest.connectParentToChildren(x2, x4);
        GraphTest.connectParentToChildren(x4, x5);
        GraphTest.connectParentToChildren(x3, x5);
        GraphTest.connectParentToChildren(x6, x5);
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        jtBuilder.moralize();
        GraphTest.assertLinkedNode(jtBuilder, new int[]{ x1.getId(), 2, 3 });
        GraphTest.assertLinkedNode(jtBuilder, new int[]{ x2.getId(), 1, 4 });
        GraphTest.assertLinkedNode(jtBuilder, new int[]{ x3.getId(), 1, 4, 5, 6 });
        GraphTest.assertLinkedNode(jtBuilder, new int[]{ x4.getId(), 2, 3, 5, 6 });
        GraphTest.assertLinkedNode(jtBuilder, new int[]{ x5.getId(), 3, 4, 6 });
        GraphTest.assertLinkedNode(jtBuilder, new int[]{ x6.getId(), 3, 4, 5 });
    }

    @Test
    public void testEliminationCandidate1() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = GraphTest.addNode(graph);
        GraphNode x1 = GraphTest.addNode(graph);
        GraphNode x2 = GraphTest.addNode(graph);
        GraphNode x3 = GraphTest.addNode(graph);
        GraphNode x4 = GraphTest.addNode(graph);
        GraphTest.connectParentToChildren(x1, x2, x3, x4);
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        jtBuilder.moralize();
        EliminationCandidate vt1 = new EliminationCandidate(graph, jtBuilder.getAdjacencyMatrix(), x1);
        Assert.assertEquals(3, vt1.getNewEdgesRequired());
        Assert.assertEquals(GraphTest.bitSet("11110"), vt1.getCliqueBitSit());
    }

    @Test
    public void testEliminationCandidate2() {
        Graph graph = new BayesNetwork();
        GraphNode x0 = GraphTest.addNode(graph);
        GraphNode x1 = GraphTest.addNode(graph);
        GraphNode x2 = GraphTest.addNode(graph);
        GraphNode x3 = GraphTest.addNode(graph);
        GraphNode x4 = GraphTest.addNode(graph);
        GraphTest.connectParentToChildren(x1, x2, x3, x4);
        GraphTest.connectParentToChildren(x3, x4);
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        jtBuilder.moralize();
        EliminationCandidate vt1 = new EliminationCandidate(graph, jtBuilder.getAdjacencyMatrix(), x1);
        Assert.assertEquals(2, vt1.getNewEdgesRequired());
        Assert.assertEquals(GraphTest.bitSet("11110"), vt1.getCliqueBitSit());
    }

    @Test
    public void testCreateClique() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode dX0 = GraphTest.addNode(graph);
        GraphNode dX1 = GraphTest.addNode(graph);
        GraphNode dX2 = GraphTest.addNode(graph);
        GraphNode dX3 = GraphTest.addNode(graph);
        GraphNode dX4 = GraphTest.addNode(graph);
        GraphNode dX5 = GraphTest.addNode(graph);
        GraphTest.connectParentToChildren(dX1, dX2, dX3, dX4);
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        // do not moralize, as we want to test just the clique creation through elimination of the provided vertices
        Set<Integer> vertices = new HashSet<Integer>();
        boolean[] adjList = new boolean[]{ false, false, true, true, true, false };
        boolean[][] clonedAdjMatrix = JunctionTreeBuilder.cloneAdjacencyMarix(jtBuilder.getAdjacencyMatrix());
        jtBuilder.createClique(dX1.getId(), clonedAdjMatrix, vertices, adjList);
        Assert.assertEquals(3, vertices.size());
        Assert.assertTrue(vertices.containsAll(Arrays.asList(new Integer[]{ 2, 3, 4 })));
        GraphTest.assertLinkedNode(jtBuilder, 1, 2, 3, 4);
        GraphTest.assertLinkedNode(jtBuilder, 2, 1, 3, 4);
        GraphTest.assertLinkedNode(jtBuilder, 3, 1, 2, 4);
        GraphTest.assertLinkedNode(jtBuilder, 4, 1, 2, 3);
    }

    @Test
    public void testCliqueSuperSet() {
        Graph<BayesVariable> graph = new BayesNetwork();
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        List<OpenBitSet> cliques = new ArrayList<OpenBitSet>();
        OpenBitSet OpenBitSet1 = GraphTest.bitSet("00011110");
        jtBuilder.updateCliques(cliques, OpenBitSet1);
        Assert.assertEquals(1, cliques.size());
        // ignore subset
        OpenBitSet OpenBitSet2 = GraphTest.bitSet("00000110");
        jtBuilder.updateCliques(cliques, OpenBitSet2);
        Assert.assertEquals(1, cliques.size());
        Assert.assertEquals(OpenBitSet1, cliques.get(0));
        // add overlapping, as not a pure subset
        OpenBitSet OpenBitSet3 = GraphTest.bitSet("01000110");
        jtBuilder.updateCliques(cliques, OpenBitSet3);
        Assert.assertEquals(2, cliques.size());
        Assert.assertEquals(OpenBitSet1, cliques.get(0));
        Assert.assertEquals(OpenBitSet3, cliques.get(1));
    }

    @Test
    public void testPriorityQueueWithMinimalNewEdges() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = GraphTest.addNode(graph);
        GraphNode x1 = GraphTest.addNode(graph);
        GraphNode x2 = GraphTest.addNode(graph);
        GraphNode x3 = GraphTest.addNode(graph);
        GraphNode x4 = GraphTest.addNode(graph);
        GraphNode x5 = GraphTest.addNode(graph);
        GraphNode x6 = GraphTest.addNode(graph);
        GraphNode x7 = GraphTest.addNode(graph);
        GraphNode x8 = GraphTest.addNode(graph);
        GraphNode x9 = GraphTest.addNode(graph);
        GraphNode x10 = GraphTest.addNode(graph);
        GraphNode x11 = GraphTest.addNode(graph);
        GraphNode x12 = GraphTest.addNode(graph);
        // 3 new edges
        GraphTest.connectParentToChildren(x2, x1);
        GraphTest.connectParentToChildren(x3, x1);
        GraphTest.connectParentToChildren(x4, x1);
        // 1 new edge
        // we give this a high weight, to show required new edges is compared first
        GraphTest.connectParentToChildren(x6, x5);
        GraphTest.connectParentToChildren(x7, x5);
        x5.setContent(new BayesVariable<String>("x5", x0.getId(), new String[]{ "a", "b", "c" }, new double[][]{ new double[]{ 0.1, 0.1, 0.1 } }));
        x6.setContent(new BayesVariable<String>("x6", x0.getId(), new String[]{ "a", "b", "c" }, new double[][]{ new double[]{ 0.1, 0.1, 0.1 } }));
        x7.setContent(new BayesVariable<String>("x7", x0.getId(), new String[]{ "a", "b", "c" }, new double[][]{ new double[]{ 0.1, 0.1, 0.1 } }));
        // 6 new edges
        GraphTest.connectParentToChildren(x9, x8);
        GraphTest.connectParentToChildren(x10, x8);
        GraphTest.connectParentToChildren(x11, x8);
        GraphTest.connectParentToChildren(x12, x8);
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        // jtBuilder.moralize(); // don't moralize, as we want to force a simpler construction for required edges, for the purposes of testing
        PriorityQueue<EliminationCandidate> p = new PriorityQueue<EliminationCandidate>(graph.size());
        EliminationCandidate elmCandVert = new EliminationCandidate(graph, jtBuilder.getAdjacencyMatrix(), x1);
        p.add(elmCandVert);
        elmCandVert = new EliminationCandidate(graph, jtBuilder.getAdjacencyMatrix(), x5);
        p.add(elmCandVert);
        elmCandVert = new EliminationCandidate(graph, jtBuilder.getAdjacencyMatrix(), x8);
        p.add(elmCandVert);
        EliminationCandidate v = p.remove();
        int id = v.getV().getId();
        Assert.assertEquals(5, id);
        Assert.assertEquals(1, v.getNewEdgesRequired());
        v = p.remove();
        id = v.getV().getId();
        Assert.assertEquals(1, id);
        Assert.assertEquals(3, v.getNewEdgesRequired());
        v = p.remove();
        id = v.getV().getId();
        Assert.assertEquals(8, id);
        Assert.assertEquals(6, v.getNewEdgesRequired());
        Assert.assertEquals(0, p.size());
    }

    @Test
    public void testPriorityQueueWithMaximalCliqueWeight() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = GraphTest.addNode(graph);
        GraphNode x1 = GraphTest.addNode(graph);
        GraphNode x2 = GraphTest.addNode(graph);
        GraphNode x3 = GraphTest.addNode(graph);
        GraphNode x4 = GraphTest.addNode(graph);
        GraphNode x5 = GraphTest.addNode(graph);
        GraphNode x6 = GraphTest.addNode(graph);
        GraphNode x7 = GraphTest.addNode(graph);
        GraphNode x8 = GraphTest.addNode(graph);
        GraphNode x9 = GraphTest.addNode(graph);
        GraphNode x10 = GraphTest.addNode(graph);
        GraphNode x11 = GraphTest.addNode(graph);
        GraphNode x12 = GraphTest.addNode(graph);
        GraphTest.connectParentToChildren(x2, x1);
        GraphTest.connectParentToChildren(x3, x1);
        GraphTest.connectParentToChildren(x4, x1);
        x1.setContent(new BayesVariable<String>("x1", x0.getId(), new String[]{ "a", "b" }, new double[][]{ new double[]{ 0.1, 0.1 } }));
        x2.setContent(new BayesVariable<String>("x2", x0.getId(), new String[]{ "a", "b" }, new double[][]{ new double[]{ 0.1, 0.1 } }));
        x3.setContent(new BayesVariable<String>("x3", x0.getId(), new String[]{ "a", "b" }, new double[][]{ new double[]{ 0.1, 0.1 } }));
        x4.setContent(new BayesVariable<String>("x4", x0.getId(), new String[]{ "a", "b" }, new double[][]{ new double[]{ 0.1, 0.1 } }));
        GraphTest.connectParentToChildren(x6, x5);
        GraphTest.connectParentToChildren(x7, x5);
        GraphTest.connectParentToChildren(x8, x5);
        x5.setContent(new BayesVariable<String>("x5", x0.getId(), new String[]{ "a", "b", "c" }, new double[][]{ new double[]{ 0.1, 0.1, 0.1 } }));
        x6.setContent(new BayesVariable<String>("x6", x0.getId(), new String[]{ "a", "b", "c" }, new double[][]{ new double[]{ 0.1, 0.1, 0.1 } }));
        x7.setContent(new BayesVariable<String>("x7", x0.getId(), new String[]{ "a", "b", "c" }, new double[][]{ new double[]{ 0.1, 0.1, 0.1 } }));
        x8.setContent(new BayesVariable<String>("x8", x0.getId(), new String[]{ "a", "b", "c" }, new double[][]{ new double[]{ 0.1, 0.1, 0.1 } }));
        GraphTest.connectParentToChildren(x10, x9);
        GraphTest.connectParentToChildren(x11, x9);
        GraphTest.connectParentToChildren(x12, x9);
        x9.setContent(new BayesVariable<String>("x9", x0.getId(), new String[]{ "a" }, new double[][]{ new double[]{ 0.1 } }));
        x10.setContent(new BayesVariable<String>("x10", x0.getId(), new String[]{ "a" }, new double[][]{ new double[]{ 0.1 } }));
        x11.setContent(new BayesVariable<String>("x11", x0.getId(), new String[]{ "a", "b" }, new double[][]{ new double[]{ 0.1, 0.1 } }));
        x12.setContent(new BayesVariable<String>("x12", x0.getId(), new String[]{ "a", "b" }, new double[][]{ new double[]{ 0.1, 0.1 } }));
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        // jtBuilder.moralize(); // don't moralize, as we want to force a simpler construction for required edges, for the purposes of testing
        PriorityQueue<EliminationCandidate> p = new PriorityQueue<EliminationCandidate>(graph.size());
        EliminationCandidate elmCandVert = new EliminationCandidate(graph, jtBuilder.getAdjacencyMatrix(), x1);
        p.add(elmCandVert);
        elmCandVert = new EliminationCandidate(graph, jtBuilder.getAdjacencyMatrix(), x5);
        p.add(elmCandVert);
        elmCandVert = new EliminationCandidate(graph, jtBuilder.getAdjacencyMatrix(), x9);
        p.add(elmCandVert);
        EliminationCandidate v = p.remove();
        int id = v.getV().getId();
        Assert.assertEquals(9, id);
        Assert.assertEquals(4, v.getWeightRequired());
        v = p.remove();
        id = v.getV().getId();
        Assert.assertEquals(1, id);
        Assert.assertEquals(16, v.getWeightRequired());
        v = p.remove();
        id = v.getV().getId();
        Assert.assertEquals(5, id);
        Assert.assertEquals(81, v.getWeightRequired());
        Assert.assertEquals(0, p.size());
    }

    @Test
    public void testIterativeEliminationUsingEdgeAndWeight() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = GraphTest.addNode(graph);
        GraphNode x1 = GraphTest.addNode(graph);
        GraphNode x2 = GraphTest.addNode(graph);
        GraphNode x3 = GraphTest.addNode(graph);
        GraphNode x4 = GraphTest.addNode(graph);
        GraphNode x5 = GraphTest.addNode(graph);
        GraphNode x6 = GraphTest.addNode(graph);
        // *
        // / | \
        // *  | *
        // |  | |
        // *  | *
        // \  /
        // *
        GraphTest.connectParentToChildren(x1, x2);
        GraphTest.connectParentToChildren(x1, x3);
        GraphTest.connectParentToChildren(x1, x6);
        GraphTest.connectParentToChildren(x2, x4);
        GraphTest.connectParentToChildren(x3, x5);
        GraphTest.connectParentToChildren(x4, x6);
        GraphTest.connectParentToChildren(x5, x6);
        // need to ensure x5 followed by x4 are removed first
        x1.setContent(new BayesVariable<String>("x1", x0.getId(), new String[]{ "a", "b", "c", "d", "e", "f" }, new double[][]{ new double[]{ 0.1, 0.1, 0.1, 0.1, 0.1, 0.1 } }));
        x2.setContent(new BayesVariable<String>("x2", x0.getId(), new String[]{ "a", "b" }, new double[][]{ new double[]{ 0.1, 0.1 } }));
        x3.setContent(new BayesVariable<String>("x3", x0.getId(), new String[]{ "a", "b" }, new double[][]{ new double[]{ 0.1, 0.1 } }));
        x4.setContent(new BayesVariable<String>("x4", x0.getId(), new String[]{ "a", "b" }, new double[][]{ new double[]{ 0.1, 0.1 } }));
        x5.setContent(new BayesVariable<String>("x5", x0.getId(), new String[]{ "a" }, new double[][]{ new double[]{ 0.1 } }));
        x6.setContent(new BayesVariable<String>("x6", x0.getId(), new String[]{ "a", "b" }, new double[][]{ new double[]{ 0.1, 0.1 } }));
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        // jtBuilder.moralize(); // don't moralize, as we want to force a simpler construction for vertex elimination order and updates
        boolean[][] clonedAdjMatrix = jtBuilder.cloneAdjacencyMarix(jtBuilder.getAdjacencyMatrix());
        PriorityQueue<EliminationCandidate> p = new PriorityQueue<EliminationCandidate>(graph.size());
        Map<Integer, EliminationCandidate> elmVertMap = new HashMap<Integer, EliminationCandidate>();
        for (GraphNode<BayesVariable> v : graph) {
            if ((v.getId()) == 0) {
                continue;
            }
            EliminationCandidate elmCandVert = new EliminationCandidate(graph, clonedAdjMatrix, v);
            p.add(elmCandVert);
            elmVertMap.put(v.getId(), elmCandVert);
        }
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 1, 2, 3, 6 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 2, 1, 4 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 3, 1, 5 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 4, 2, 6 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 5, 3, 6 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 6, 1, 4, 5 });
        Assert.assertEquals(3, elmVertMap.get(1).getNewEdgesRequired());
        Assert.assertEquals(1, elmVertMap.get(2).getNewEdgesRequired());
        Assert.assertEquals(1, elmVertMap.get(3).getNewEdgesRequired());
        Assert.assertEquals(1, elmVertMap.get(4).getNewEdgesRequired());
        Assert.assertEquals(1, elmVertMap.get(5).getNewEdgesRequired());
        Assert.assertEquals(3, elmVertMap.get(6).getNewEdgesRequired());
        // 5 has the lowest new edges and weight
        EliminationCandidate v = p.remove();
        int id = v.getV().getId();
        Assert.assertEquals(5, id);
        Set<Integer> verticesToUpdate = new HashSet<Integer>();
        boolean[] adjList = clonedAdjMatrix[id];
        jtBuilder.createClique(5, clonedAdjMatrix, verticesToUpdate, adjList);
        Assert.assertEquals(4, verticesToUpdate.size());
        Assert.assertTrue(verticesToUpdate.containsAll(Arrays.asList(new Integer[]{ 1, 3, 6 })));
        jtBuilder.eliminateVertex(p, elmVertMap, clonedAdjMatrix, adjList, verticesToUpdate, v);
        // assert all new edges
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 1, 2, 3, 6 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 2, 1, 4 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 3, 1, 5, 6 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 4, 2, 6 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 5, 3, 6 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 6, 1, 3, 4, 5 });
        // assert new edges were correctly recalculated
        Assert.assertEquals(2, elmVertMap.get(1).getNewEdgesRequired());
        Assert.assertEquals(1, elmVertMap.get(2).getNewEdgesRequired());
        Assert.assertEquals(0, elmVertMap.get(3).getNewEdgesRequired());
        Assert.assertEquals(2, elmVertMap.get(6).getNewEdgesRequired());
        Assert.assertEquals(1, elmVertMap.get(4).getNewEdgesRequired());
        // 3 next as it has no new edges now, after recalculation
        v = p.remove();
        id = v.getV().getId();
        Assert.assertEquals(3, id);
        verticesToUpdate = new HashSet<Integer>();
        jtBuilder.createClique(3, clonedAdjMatrix, verticesToUpdate, adjList);
        jtBuilder.eliminateVertex(p, elmVertMap, clonedAdjMatrix, adjList, verticesToUpdate, v);
        // 4 is next
        v = p.remove();
        id = v.getV().getId();
        Assert.assertEquals(4, id);
        verticesToUpdate = new HashSet<Integer>();
        adjList = clonedAdjMatrix[id];
        jtBuilder.createClique(4, clonedAdjMatrix, verticesToUpdate, adjList);
        Assert.assertEquals(3, verticesToUpdate.size());
        Assert.assertTrue(verticesToUpdate.containsAll(Arrays.asList(new Integer[]{ 1, 2, 6 })));// don't forget 3 and 5 were already eliminated

        jtBuilder.eliminateVertex(p, elmVertMap, clonedAdjMatrix, adjList, verticesToUpdate, v);
        // assert all new edges
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 1, 2, 3, 6 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 2, 1, 4, 6 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 3, 1, 5, 6 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 4, 2, 6 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 5, 3, 6 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 6, 1, 2, 3, 4, 5 });
        // assert new edges were correctly recalculated
        Assert.assertEquals(0, elmVertMap.get(1).getNewEdgesRequired());
        Assert.assertEquals(0, elmVertMap.get(2).getNewEdgesRequired());
        Assert.assertEquals(0, elmVertMap.get(6).getNewEdgesRequired());
        // 1, 2 and 6 all have no new edges, and same cluster, so it uses id to ensure arbitrary is deterministic
        v = p.remove();
        id = v.getV().getId();
        Assert.assertEquals(1, id);
        verticesToUpdate = new HashSet<Integer>();
        jtBuilder.createClique(1, clonedAdjMatrix, verticesToUpdate, adjList);
        jtBuilder.eliminateVertex(p, elmVertMap, clonedAdjMatrix, adjList, verticesToUpdate, v);
        v = p.remove();
        id = v.getV().getId();
        Assert.assertEquals(2, id);
        verticesToUpdate = new HashSet<Integer>();
        jtBuilder.createClique(2, clonedAdjMatrix, verticesToUpdate, adjList);
        jtBuilder.eliminateVertex(p, elmVertMap, clonedAdjMatrix, adjList, verticesToUpdate, v);
        v = p.remove();
        id = v.getV().getId();
        Assert.assertEquals(6, id);
        verticesToUpdate = new HashSet<Integer>();
        jtBuilder.createClique(6, clonedAdjMatrix, verticesToUpdate, adjList);
        jtBuilder.eliminateVertex(p, elmVertMap, clonedAdjMatrix, adjList, verticesToUpdate, v);
        Assert.assertEquals(0, p.size());
    }

    @Test
    public void testTriangulate1() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = GraphTest.addNode(graph);
        GraphNode x1 = GraphTest.addNode(graph);
        GraphNode x2 = GraphTest.addNode(graph);
        GraphNode x3 = GraphTest.addNode(graph);
        GraphNode x4 = GraphTest.addNode(graph);
        GraphNode x5 = GraphTest.addNode(graph);
        GraphNode x6 = GraphTest.addNode(graph);
        // *
        // / | \
        // *  | *
        // |  | |
        // *  | *
        // \  /
        // *
        GraphTest.connectParentToChildren(x1, x2);
        GraphTest.connectParentToChildren(x1, x3);
        GraphTest.connectParentToChildren(x1, x6);
        GraphTest.connectParentToChildren(x2, x4);
        GraphTest.connectParentToChildren(x3, x5);
        GraphTest.connectParentToChildren(x4, x6);
        GraphTest.connectParentToChildren(x5, x6);
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        jtBuilder.moralize();
        jtBuilder.triangulate();
        // assert all new edges
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 1, 2, 3, 4, 5, 6 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 2, 1, 4 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 3, 1, 5 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 4, 1, 2, 5, 6 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 5, 1, 3, 4, 6 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 6, 1, 4, 5 });
    }

    @Test
    public void testTriangulate2() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = GraphTest.addNode(graph);
        GraphNode x1 = GraphTest.addNode(graph);
        GraphNode x2 = GraphTest.addNode(graph);
        GraphNode x3 = GraphTest.addNode(graph);
        GraphNode x4 = GraphTest.addNode(graph);
        GraphNode x5 = GraphTest.addNode(graph);
        GraphNode x6 = GraphTest.addNode(graph);
        // *
        // /
        // *
        // /  \
        // *   *
        // |   |
        // *   |
        // \  /
        // *
        GraphTest.connectParentToChildren(x1, x2);
        GraphTest.connectParentToChildren(x1, x3);
        GraphTest.connectParentToChildren(x2, x4);
        GraphTest.connectParentToChildren(x2, x6);
        GraphTest.connectParentToChildren(x3, x5);
        GraphTest.connectParentToChildren(x5, x6);
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        jtBuilder.moralize();
        List<OpenBitSet> cliques = jtBuilder.triangulate();
        // assert all new edges
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 1, 2, 3 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 2, 1, 3, 4, 5, 6 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 3, 1, 2, 5 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 4, 2 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 5, 2, 3, 6 });
        GraphTest.assertLinkedVertex(jtBuilder.getAdjacencyMatrix(), new int[]{ 6, 2, 5 });
        Assert.assertEquals(5, cliques.size());// 5th is 0, which is just a dummy V to get numbers aligned

        Assert.assertTrue(cliques.contains(GraphTest.bitSet("1110")));// x1, x2, x3 //a, b, c

        Assert.assertTrue(cliques.contains(GraphTest.bitSet("10100")));// x2, x4

        Assert.assertTrue(cliques.contains(GraphTest.bitSet("1100100")));// x2, x5, x6

        Assert.assertTrue(cliques.contains(GraphTest.bitSet("101100")));// x2, x3, x5

    }

    @Test
    public void testSepSetCompareWithDifferentMass() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = GraphTest.addNode(graph);
        GraphNode x1 = GraphTest.addNode(graph);
        GraphNode x2 = GraphTest.addNode(graph);
        GraphNode x3 = GraphTest.addNode(graph);
        GraphNode x4 = GraphTest.addNode(graph);
        GraphNode x5 = GraphTest.addNode(graph);
        GraphNode x6 = GraphTest.addNode(graph);
        OpenBitSet OpenBitSet1_1 = GraphTest.bitSet("00001110");
        OpenBitSet OpenBitSet1_2 = GraphTest.bitSet("01101100");
        SeparatorSet s1 = new SeparatorSet(OpenBitSet1_1, 0, OpenBitSet1_2, 0, graph);
        OpenBitSet OpenBitSet2_1 = GraphTest.bitSet("00001110");
        OpenBitSet OpenBitSet2_2 = GraphTest.bitSet("00100100");
        SeparatorSet s2 = new SeparatorSet(OpenBitSet2_1, 0, OpenBitSet2_2, 0, graph);
        List<SeparatorSet> list = new ArrayList<SeparatorSet>();
        list.add(s1);
        list.add(s2);
        Collections.sort(list);
        Assert.assertEquals(s1, list.get(0));
    }

    @Test
    public void testSepSetCompareWithDifferentCost() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = GraphTest.addNode(graph);
        GraphNode x1 = GraphTest.addNode(graph);
        GraphNode x2 = GraphTest.addNode(graph);
        GraphNode x3 = GraphTest.addNode(graph);
        GraphNode x4 = GraphTest.addNode(graph);
        GraphNode x5 = GraphTest.addNode(graph);
        GraphNode x6 = GraphTest.addNode(graph);
        x1.setContent(new BayesVariable<String>("x1", x0.getId(), new String[]{ "a" }, new double[][]{ new double[]{ 0.1 } }));
        x2.setContent(new BayesVariable<String>("x2", x0.getId(), new String[]{ "a", "b" }, new double[][]{ new double[]{ 0.1, 0.1 } }));
        x3.setContent(new BayesVariable<String>("x3", x0.getId(), new String[]{ "a", "b" }, new double[][]{ new double[]{ 0.1, 0.1 } }));
        OpenBitSet OpenBitSet1_1 = GraphTest.bitSet("00001110");
        OpenBitSet OpenBitSet1_2 = GraphTest.bitSet("01101100");
        SeparatorSet s1 = new SeparatorSet(OpenBitSet1_1, 0, OpenBitSet1_2, 0, graph);
        OpenBitSet OpenBitSet2_1 = GraphTest.bitSet("00001110");
        OpenBitSet OpenBitSet2_2 = GraphTest.bitSet("00100110");
        SeparatorSet s2 = new SeparatorSet(OpenBitSet2_1, 0, OpenBitSet2_2, 0, graph);
        List<SeparatorSet> list = new ArrayList<SeparatorSet>();
        list.add(s1);
        list.add(s2);
        Collections.sort(list);
        Assert.assertEquals(s1, list.get(0));
        // repeat, reversing the costs, to be sure no other factor is in play.
        x1.setContent(new BayesVariable<String>("x3", x0.getId(), new String[]{ "a", "b" }, new double[][]{ new double[]{ 0.1, 0.1 } }));
        x2.setContent(new BayesVariable<String>("x2", x0.getId(), new String[]{ "a", "b" }, new double[][]{ new double[]{ 0.1, 0.1 } }));
        x3.setContent(new BayesVariable<String>("x1", x0.getId(), new String[]{ "a" }, new double[][]{ new double[]{ 0.1 } }));
        s1 = new SeparatorSet(OpenBitSet1_1, 0, OpenBitSet1_2, 0, graph);
        s2 = new SeparatorSet(OpenBitSet2_1, 0, OpenBitSet2_2, 0, graph);
        list = new ArrayList<SeparatorSet>();
        list.add(s1);
        list.add(s2);
        Collections.sort(list);
        Assert.assertEquals(s2, list.get(0));// was s1 before

    }

    @Test
    public void testSepSetCompareWithSameIntersect() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = GraphTest.addNode(graph);
        GraphNode x1 = GraphTest.addNode(graph);
        GraphNode x2 = GraphTest.addNode(graph);
        GraphNode x3 = GraphTest.addNode(graph);
        GraphNode x4 = GraphTest.addNode(graph);
        GraphNode x5 = GraphTest.addNode(graph);
        GraphNode x6 = GraphTest.addNode(graph);
        OpenBitSet OpenBitSet1_1 = GraphTest.bitSet("00001110");
        OpenBitSet OpenBitSet1_2 = GraphTest.bitSet("01000010");
        SeparatorSet s1 = new SeparatorSet(OpenBitSet1_1, 0, OpenBitSet1_2, 0, graph);
        OpenBitSet OpenBitSet2_1 = GraphTest.bitSet("00001110");
        OpenBitSet OpenBitSet2_2 = GraphTest.bitSet("01000100");
        SeparatorSet s2 = new SeparatorSet(OpenBitSet2_1, 0, OpenBitSet2_2, 0, graph);
        List<SeparatorSet> list = new ArrayList<SeparatorSet>();
        list.add(s1);
        list.add(s2);
        Collections.sort(list);
        Assert.assertEquals(s1, list.get(0));
        // reverse the bits, to show the arbitrary is deterministic
        OpenBitSet1_2 = GraphTest.bitSet("01000100");
        s1 = new SeparatorSet(OpenBitSet1_1, 0, OpenBitSet1_2, 0, graph);
        OpenBitSet2_2 = GraphTest.bitSet("01000010");
        s2 = new SeparatorSet(OpenBitSet2_1, 0, OpenBitSet2_2, 0, graph);
        list = new ArrayList<SeparatorSet>();
        list.add(s1);
        list.add(s2);
        Collections.sort(list);
        Assert.assertEquals(s2, list.get(0));// was s1 before

    }

    @Test
    public void testJunctionTree() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = GraphTest.addNode(graph);
        GraphNode x1 = GraphTest.addNode(graph);
        GraphNode x2 = GraphTest.addNode(graph);
        GraphNode x3 = GraphTest.addNode(graph);
        GraphNode x4 = GraphTest.addNode(graph);
        GraphNode x5 = GraphTest.addNode(graph);
        GraphNode x6 = GraphTest.addNode(graph);
        OpenBitSet OpenBitSet1 = GraphTest.bitSet("00001110");
        OpenBitSet OpenBitSet2 = GraphTest.bitSet("00011100");
        OpenBitSet OpenBitSet3 = GraphTest.bitSet("00110000");
        OpenBitSet OpenBitSet4 = GraphTest.bitSet("01110000");
        List<OpenBitSet> cliques = new ArrayList<OpenBitSet>();
        cliques.add(OpenBitSet1);
        cliques.add(OpenBitSet2);
        cliques.add(OpenBitSet3);
        cliques.add(OpenBitSet4);
        List<SeparatorSet> separatorSets = new ArrayList<SeparatorSet>();
        for (int i = 0; i < (cliques.size()); i++) {
            OpenBitSet ci = cliques.get(i);
            for (int j = i + 1; j < (cliques.size()); j++) {
                OpenBitSet cj = cliques.get(j);
                if (ci.intersects(cj)) {
                    SeparatorSet separatorSet = new SeparatorSet(ci, 0, cj, 0, graph);
                }
            }
        }
        Collections.sort(separatorSets);
    }

    @Test
    public void testJunctionTreeNoPruning() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = GraphTest.addNode(graph);
        GraphNode x1 = GraphTest.addNode(graph);
        GraphNode x2 = GraphTest.addNode(graph);
        GraphNode x3 = GraphTest.addNode(graph);
        GraphNode x4 = GraphTest.addNode(graph);
        GraphNode x5 = GraphTest.addNode(graph);
        GraphNode x6 = GraphTest.addNode(graph);
        GraphNode x7 = GraphTest.addNode(graph);
        List<OpenBitSet> list = new ArrayList<OpenBitSet>();
        OpenBitSet OpenBitSet1 = GraphTest.bitSet("00001111");
        OpenBitSet OpenBitSet2 = GraphTest.bitSet("00111100");
        OpenBitSet OpenBitSet3 = GraphTest.bitSet("11100000");// linear

        OpenBitSet intersect1And2 = ((OpenBitSet) (OpenBitSet2.clone()));
        intersect1And2.and(OpenBitSet1);
        OpenBitSet intersect2And3 = ((OpenBitSet) (OpenBitSet2.clone()));
        intersect2And3.and(OpenBitSet3);
        list.add(OpenBitSet1);
        list.add(OpenBitSet2);
        list.add(OpenBitSet3);
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        JunctionTreeClique jtNode = jtBuilder.junctionTree(list, false).getRoot();
        Assert.assertEquals(OpenBitSet1, jtNode.getBitSet());
        Assert.assertEquals(1, jtNode.getChildren().size());
        JunctionTreeSeparator sep = jtNode.getChildren().get(0);
        Assert.assertEquals(OpenBitSet1, sep.getParent().getBitSet());
        Assert.assertEquals(OpenBitSet2, sep.getChild().getBitSet());
        Assert.assertEquals(intersect1And2, sep.getBitSet());
        jtNode = sep.getChild();
        Assert.assertEquals(OpenBitSet2, jtNode.getBitSet());
        Assert.assertEquals(1, jtNode.getChildren().size());
        sep = jtNode.getChildren().get(0);
        Assert.assertEquals(OpenBitSet2, sep.getParent().getBitSet());
        Assert.assertEquals(OpenBitSet3, sep.getChild().getBitSet());
        Assert.assertEquals(intersect2And3, sep.getBitSet());
    }

    @Test
    public void testJunctionWithPruning1() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = GraphTest.addNode(graph);
        GraphNode x1 = GraphTest.addNode(graph);
        GraphNode x2 = GraphTest.addNode(graph);
        GraphNode x3 = GraphTest.addNode(graph);
        GraphNode x4 = GraphTest.addNode(graph);
        GraphNode x5 = GraphTest.addNode(graph);
        GraphNode x6 = GraphTest.addNode(graph);
        GraphNode x7 = GraphTest.addNode(graph);
        List<OpenBitSet> list = new ArrayList<OpenBitSet>();
        OpenBitSet OpenBitSet1 = GraphTest.bitSet("00001111");
        OpenBitSet OpenBitSet2 = GraphTest.bitSet("00111100");
        OpenBitSet OpenBitSet3 = GraphTest.bitSet("11100001");// links to 2 and 1, but should still result in a single path. As the 3 -> 1 link, gets pruned

        OpenBitSet intersect1And2 = ((OpenBitSet) (OpenBitSet2.clone()));
        intersect1And2.and(OpenBitSet1);
        OpenBitSet intersect2And3 = ((OpenBitSet) (OpenBitSet2.clone()));
        intersect2And3.and(OpenBitSet3);
        list.add(OpenBitSet1);
        list.add(OpenBitSet2);
        list.add(OpenBitSet3);
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        JunctionTreeClique jtNode = jtBuilder.junctionTree(list, false).getRoot();
        Assert.assertEquals(OpenBitSet1, jtNode.getBitSet());
        Assert.assertEquals(2, jtNode.getChildren().size());
        JunctionTreeSeparator sep = jtNode.getChildren().get(0);
        Assert.assertEquals(OpenBitSet1, sep.getParent().getBitSet());
        Assert.assertEquals(OpenBitSet2, sep.getChild().getBitSet());
        Assert.assertEquals(0, sep.getChild().getChildren().size());
        sep = jtNode.getChildren().get(1);
        Assert.assertEquals(OpenBitSet1, sep.getParent().getBitSet());
        Assert.assertEquals(OpenBitSet3, sep.getChild().getBitSet());
        Assert.assertEquals(0, sep.getChild().getChildren().size());
    }

    @Test
    public void testJunctionWithPruning2() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = GraphTest.addNode(graph);
        GraphNode x1 = GraphTest.addNode(graph);
        GraphNode x2 = GraphTest.addNode(graph);
        GraphNode x3 = GraphTest.addNode(graph);
        GraphNode x4 = GraphTest.addNode(graph);
        GraphNode x5 = GraphTest.addNode(graph);
        GraphNode x6 = GraphTest.addNode(graph);
        GraphNode x7 = GraphTest.addNode(graph);
        List<OpenBitSet> list = new ArrayList<OpenBitSet>();
        OpenBitSet OpenBitSet1 = GraphTest.bitSet("00001111");
        OpenBitSet OpenBitSet2 = GraphTest.bitSet("00111100");
        OpenBitSet OpenBitSet3 = GraphTest.bitSet("11100000");
        OpenBitSet OpenBitSet4 = GraphTest.bitSet("00100001");
        OpenBitSet intersect1And2 = ((OpenBitSet) (OpenBitSet2.clone()));
        intersect1And2.and(OpenBitSet1);
        OpenBitSet intersect2And3 = ((OpenBitSet) (OpenBitSet2.clone()));
        intersect2And3.and(OpenBitSet3);
        OpenBitSet intersect1And4 = ((OpenBitSet) (OpenBitSet1.clone()));
        intersect1And4.and(OpenBitSet4);
        list.add(OpenBitSet1);
        list.add(OpenBitSet2);
        list.add(OpenBitSet3);
        list.add(OpenBitSet4);
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        JunctionTreeClique jtNode = jtBuilder.junctionTree(list, false).getRoot();
        JunctionTreeClique root = jtNode;
        Assert.assertEquals(OpenBitSet1, root.getBitSet());
        Assert.assertEquals(2, root.getChildren().size());
        JunctionTreeSeparator sep = root.getChildren().get(0);
        Assert.assertEquals(OpenBitSet1, sep.getParent().getBitSet());
        Assert.assertEquals(OpenBitSet2, sep.getChild().getBitSet());
        Assert.assertEquals(1, sep.getChild().getChildren().size());
        jtNode = sep.getChild();
        Assert.assertEquals(OpenBitSet2, jtNode.getBitSet());
        Assert.assertEquals(1, jtNode.getChildren().size());
        sep = jtNode.getChildren().get(0);
        Assert.assertEquals(OpenBitSet2, sep.getParent().getBitSet());
        Assert.assertEquals(OpenBitSet3, sep.getChild().getBitSet());
        Assert.assertEquals(intersect2And3, sep.getBitSet());
        Assert.assertEquals(0, sep.getChild().getChildren().size());
        sep = root.getChildren().get(1);
        Assert.assertEquals(OpenBitSet1, sep.getParent().getBitSet());
        Assert.assertEquals(OpenBitSet4, sep.getChild().getBitSet());
        Assert.assertEquals(intersect1And4, sep.getBitSet());
        Assert.assertEquals(0, sep.getChild().getChildren().size());
    }

    @Test
    public void testJunctionWithPruning3() {
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = GraphTest.addNode(graph);
        GraphNode x1 = GraphTest.addNode(graph);
        GraphNode x2 = GraphTest.addNode(graph);
        GraphNode x3 = GraphTest.addNode(graph);
        GraphNode x4 = GraphTest.addNode(graph);
        GraphNode x5 = GraphTest.addNode(graph);
        GraphNode x6 = GraphTest.addNode(graph);
        GraphNode x7 = GraphTest.addNode(graph);
        List<OpenBitSet> list = new ArrayList<OpenBitSet>();
        OpenBitSet OpenBitSet1 = GraphTest.bitSet("00001111");
        OpenBitSet OpenBitSet2 = GraphTest.bitSet("00011110");
        OpenBitSet OpenBitSet3 = GraphTest.bitSet("11100000");
        OpenBitSet OpenBitSet4 = GraphTest.bitSet("01100001");
        OpenBitSet intersect1And2 = ((OpenBitSet) (OpenBitSet2.clone()));
        intersect1And2.and(OpenBitSet1);
        OpenBitSet intersect2And3 = ((OpenBitSet) (OpenBitSet2.clone()));
        intersect2And3.and(OpenBitSet3);
        OpenBitSet intersect1And4 = ((OpenBitSet) (OpenBitSet1.clone()));
        intersect1And4.and(OpenBitSet4);
        OpenBitSet intersect3And4 = ((OpenBitSet) (OpenBitSet3.clone()));
        intersect3And4.and(OpenBitSet4);
        list.add(OpenBitSet1);
        list.add(OpenBitSet2);
        list.add(OpenBitSet3);
        list.add(OpenBitSet4);
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        JunctionTreeClique jtNode = jtBuilder.junctionTree(list, false).getRoot();
        JunctionTreeClique root = jtNode;
        Assert.assertEquals(OpenBitSet1, root.getBitSet());
        Assert.assertEquals(2, root.getChildren().size());
        JunctionTreeSeparator sep = root.getChildren().get(0);
        Assert.assertEquals(OpenBitSet1, sep.getParent().getBitSet());
        Assert.assertEquals(OpenBitSet2, sep.getChild().getBitSet());
        Assert.assertEquals(0, sep.getChild().getChildren().size());
        sep = root.getChildren().get(1);
        Assert.assertEquals(OpenBitSet1, sep.getParent().getBitSet());
        Assert.assertEquals(OpenBitSet4, sep.getChild().getBitSet());
        Assert.assertEquals(intersect1And4, sep.getBitSet());
        Assert.assertEquals(1, sep.getChild().getChildren().size());
        jtNode = sep.getChild();
        Assert.assertEquals(OpenBitSet4, jtNode.getBitSet());
        Assert.assertEquals(1, jtNode.getChildren().size());
        sep = jtNode.getChildren().get(0);
        Assert.assertEquals(OpenBitSet4, sep.getParent().getBitSet());
        Assert.assertEquals(OpenBitSet3, sep.getChild().getBitSet());
        Assert.assertEquals(intersect3And4, sep.getBitSet());
        Assert.assertEquals(0, sep.getChild().getChildren().size());
    }

    @Test
    public void testMapNodeToCliques() {
        Graph<BayesVariable> graph = new BayesNetwork();
        JunctionTreeBuilder tbuilder = new JunctionTreeBuilder(graph);
        GraphNode x0 = GraphTest.addNode(graph);
        GraphNode x1 = GraphTest.addNode(graph);
        GraphNode x2 = GraphTest.addNode(graph);
        GraphNode x3 = GraphTest.addNode(graph);
        GraphNode x4 = GraphTest.addNode(graph);
        GraphNode x5 = GraphTest.addNode(graph);
        GraphNode x6 = GraphTest.addNode(graph);
        GraphNode x7 = GraphTest.addNode(graph);
        OpenBitSet clique0 = GraphTest.bitSet("01010101");
        OpenBitSet clique1 = GraphTest.bitSet("10010001");
        OpenBitSet clique2 = GraphTest.bitSet("10111010");
        OpenBitSet[] nodeToCliques = new OpenBitSet[8];
        tbuilder.mapVarNodeToCliques(nodeToCliques, 0, clique0);
        tbuilder.mapVarNodeToCliques(nodeToCliques, 1, clique1);
        tbuilder.mapVarNodeToCliques(nodeToCliques, 2, clique2);
        Assert.assertEquals(GraphTest.bitSet("011"), nodeToCliques[0]);
        Assert.assertEquals(GraphTest.bitSet("100"), nodeToCliques[1]);
        Assert.assertEquals(GraphTest.bitSet("001"), nodeToCliques[2]);
        Assert.assertEquals(GraphTest.bitSet("100"), nodeToCliques[3]);
        Assert.assertEquals(GraphTest.bitSet("111"), nodeToCliques[4]);
        Assert.assertEquals(GraphTest.bitSet("100"), nodeToCliques[5]);
        Assert.assertEquals(GraphTest.bitSet("001"), nodeToCliques[6]);
        Assert.assertEquals(GraphTest.bitSet("110"), nodeToCliques[7]);
    }

    @Test
    public void testMapNodeToClique() {
        Graph<BayesVariable> graph = new BayesNetwork();
        JunctionTreeBuilder tbuilder = new JunctionTreeBuilder(graph);
        GraphNode<BayesVariable> x0 = GraphTest.addNode(graph);
        GraphNode<BayesVariable> x1 = GraphTest.addNode(graph);
        GraphNode<BayesVariable> x2 = GraphTest.addNode(graph);
        GraphNode<BayesVariable> x3 = GraphTest.addNode(graph);
        GraphNode<BayesVariable> x4 = GraphTest.addNode(graph);
        GraphNode<BayesVariable> x5 = GraphTest.addNode(graph);
        GraphNode<BayesVariable> x6 = GraphTest.addNode(graph);
        GraphNode<BayesVariable> x7 = GraphTest.addNode(graph);
        GraphTest.connectChildToParents(x1, x2, x3);
        GraphTest.connectChildToParents(x3, x6, x7);
        OpenBitSet clique0 = GraphTest.bitSet("01001110");
        OpenBitSet clique1 = GraphTest.bitSet("11001110");
        OpenBitSet clique2 = GraphTest.bitSet("11101000");
        OpenBitSet clique3 = GraphTest.bitSet("00010011");
        JunctionTreeClique jtNode0 = new JunctionTreeClique(0, graph, clique0);
        JunctionTreeClique jtNode1 = new JunctionTreeClique(1, graph, clique1);
        JunctionTreeClique jtNode2 = new JunctionTreeClique(2, graph, clique2);
        JunctionTreeClique jtNode3 = new JunctionTreeClique(3, graph, clique3);
        JunctionTreeClique[] jtNodes = new JunctionTreeClique[]{ jtNode0, jtNode1, jtNode2, jtNode3 };
        OpenBitSet[] nodeToCliques = new OpenBitSet[8];
        tbuilder.mapVarNodeToCliques(nodeToCliques, 0, clique0);
        tbuilder.mapVarNodeToCliques(nodeToCliques, 1, clique1);
        tbuilder.mapVarNodeToCliques(nodeToCliques, 2, clique2);
        tbuilder.mapVarNodeToCliques(nodeToCliques, 3, clique3);
        // int[] nodeToClique = new int[8];
        tbuilder.mapNodeToCliqueFamily(nodeToCliques, jtNodes);
        Assert.assertEquals(3, x0.getContent().getFamily());
        Assert.assertEquals(0, x1.getContent().getFamily());
        Assert.assertEquals(0, x2.getContent().getFamily());
        Assert.assertEquals(2, x3.getContent().getFamily());
        Assert.assertEquals(3, x4.getContent().getFamily());
        Assert.assertEquals(2, x5.getContent().getFamily());
        Assert.assertEquals(0, x6.getContent().getFamily());
        Assert.assertEquals(2, x7.getContent().getFamily());
    }

    @Test
    public void testFullExample1() {
        // from "Bayesian Belief Network Propagation Engine In Java"
        // the result here is slightly different, due to ordering, but it's still correct.
        // http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.135.7921&rep=rep1&type=pdf
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode xa = GraphTest.addNode(graph);
        GraphNode xb = GraphTest.addNode(graph);
        GraphNode xc = GraphTest.addNode(graph);
        GraphNode xd = GraphTest.addNode(graph);
        GraphNode xe = GraphTest.addNode(graph);
        GraphNode xf = GraphTest.addNode(graph);
        GraphNode xg = GraphTest.addNode(graph);
        GraphNode xh = GraphTest.addNode(graph);
        GraphTest.connectParentToChildren(xa, xb, xc);
        GraphTest.connectParentToChildren(xb, xd);
        GraphTest.connectParentToChildren(xc, xe, xg);
        GraphTest.connectParentToChildren(xd, xf);
        GraphTest.connectParentToChildren(xe, xf, xh);
        GraphTest.connectParentToChildren(xg, xh);
        OpenBitSet clique1 = GraphTest.bitSet("00111000");// d, e, f

        OpenBitSet clique2 = GraphTest.bitSet("00011100");// c, d, e

        OpenBitSet clique3 = GraphTest.bitSet("01010100");// c, e, g

        OpenBitSet clique4 = GraphTest.bitSet("11010000");// e, g, h

        OpenBitSet clique5 = GraphTest.bitSet("00001110");// b, c, d

        OpenBitSet clique6 = GraphTest.bitSet("00000111");// a, b, c

        OpenBitSet clique1And2 = GraphTest.bitSet("00011000");// d, e

        OpenBitSet clique2And3 = GraphTest.bitSet("00010100");// c, e

        OpenBitSet clique2And5 = GraphTest.bitSet("00001100");// c, d

        OpenBitSet clique3And4 = GraphTest.bitSet("01010000");// e, g

        OpenBitSet clique5And6 = GraphTest.bitSet("00000110");// b, c

        // clique1
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        JunctionTreeClique root = jtBuilder.build(false).getRoot();
        Assert.assertEquals(clique1, root.getBitSet());
        Assert.assertEquals(1, root.getChildren().size());
        // clique2
        JunctionTreeSeparator sep = root.getChildren().get(0);
        Assert.assertEquals(clique1And2, sep.getBitSet());
        JunctionTreeClique jtNode2 = sep.getChild();
        Assert.assertEquals(clique1, sep.getParent().getBitSet());
        Assert.assertEquals(clique2, jtNode2.getBitSet());
        Assert.assertEquals(2, jtNode2.getChildren().size());
        // clique3
        sep = jtNode2.getChildren().get(0);
        Assert.assertEquals(clique2And3, sep.getBitSet());
        JunctionTreeClique jtNode3 = sep.getChild();
        Assert.assertEquals(clique2, sep.getParent().getBitSet());
        Assert.assertEquals(clique3, jtNode3.getBitSet());
        Assert.assertEquals(1, jtNode3.getChildren().size());
        // clique4
        sep = jtNode3.getChildren().get(0);
        Assert.assertEquals(clique3And4, sep.getBitSet());
        JunctionTreeClique jtNode4 = sep.getChild();
        Assert.assertEquals(clique3, sep.getParent().getBitSet());
        Assert.assertEquals(clique4, jtNode4.getBitSet());
        Assert.assertEquals(0, jtNode4.getChildren().size());
        // clique5
        sep = jtNode2.getChildren().get(1);
        Assert.assertEquals(clique2And5, sep.getBitSet());
        JunctionTreeClique jtNode5 = sep.getChild();
        Assert.assertEquals(clique2, sep.getParent().getBitSet());
        Assert.assertEquals(clique5, jtNode5.getBitSet());
        Assert.assertEquals(1, jtNode5.getChildren().size());
        // clique 6
        sep = jtNode5.getChildren().get(0);
        Assert.assertEquals(clique5And6, sep.getBitSet());
        JunctionTreeClique jtNode6 = sep.getChild();
        Assert.assertEquals(clique5, sep.getParent().getBitSet());
        Assert.assertEquals(clique6, jtNode6.getBitSet());
        Assert.assertEquals(0, jtNode6.getChildren().size());
    }

    @Test
    public void testFullExample2() {
        // Bayesian Networks -  A Self-contained introduction with implementation remarks
        // http://www.mathcs.emory.edu/~whalen/Papers/BNs/Intros/BayesianNetworksTutorial.pdf
        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode xElectricity = GraphTest.addNode(graph);// 0

        GraphNode xTelecom = GraphTest.addNode(graph);// 1

        GraphNode xRail = GraphTest.addNode(graph);
        // 2
        GraphNode xAirTravel = GraphTest.addNode(graph);// 3

        GraphNode xTransportation = GraphTest.addNode(graph);// 4

        GraphNode xUtilities = GraphTest.addNode(graph);// 5

        GraphNode xUSBanks = GraphTest.addNode(graph);// 6

        GraphNode xUSStocks = GraphTest.addNode(graph);// 7

        GraphTest.connectParentToChildren(xElectricity, xRail, xAirTravel, xUtilities, xTelecom);
        GraphTest.connectParentToChildren(xTelecom, xUtilities, xUSBanks);
        GraphTest.connectParentToChildren(xRail, xTransportation);
        GraphTest.connectParentToChildren(xAirTravel, xTransportation);
        GraphTest.connectParentToChildren(xUtilities, xUSStocks);
        GraphTest.connectParentToChildren(xUSBanks, xUSStocks);
        GraphTest.connectParentToChildren(xTransportation, xUSStocks);
        OpenBitSet clique1 = GraphTest.bitSet("11110000");// Utilities, Transportation, USBanks, UStocks

        OpenBitSet clique2 = GraphTest.bitSet("01110001");// Electricity, Transportation, Utilities, USBanks

        OpenBitSet clique3 = GraphTest.bitSet("01100011");// Electricity, Telecom, Utilities, USBanks

        OpenBitSet clique4 = GraphTest.bitSet("00011101");// Electricity, Rail, AirTravel, Transportation

        OpenBitSet clique1And2 = GraphTest.bitSet("01110000");// Utilities, Transportation, USBanks

        OpenBitSet clique2And3 = GraphTest.bitSet("01100001");// Electricity, Utilities, USBanks

        OpenBitSet clique2And4 = GraphTest.bitSet("00010001");// Electricity, Transportation

        xElectricity.setContent(new BayesVariable<String>("Electricity", xElectricity.getId(), new String[]{ "Working", "Reduced", "NotWorking" }, new double[][]{ new double[]{ 0.6, 0.3, 0.099 } }));
        xTelecom.setContent(new BayesVariable<String>("Telecom", xTelecom.getId(), new String[]{ "Working", "Reduced", "NotWorking" }, new double[][]{ new double[]{ 0.544, 0.304, 0.151 } }));
        xRail.setContent(new BayesVariable<String>("Rail", xRail.getId(), new String[]{ "Working", "Reduced", "NotWorking" }, new double[][]{ new double[]{ 0.579, 0.23, 0.19 } }));
        xAirTravel.setContent(new BayesVariable<String>("AirTravel", xAirTravel.getId(), new String[]{ "Working", "Reduced", "NotWorking" }, new double[][]{ new double[]{ 0.449, 0.33, 0.219 } }));
        xTransportation.setContent(new BayesVariable<String>("Transportation", xTransportation.getId(), new String[]{ "Working", "Moderate", "Severe", "Failure" }, new double[][]{ new double[]{ 0.658, 0.167, 0.097, 0.077 } }));
        xUtilities.setContent(new BayesVariable<String>("Utilities", xUtilities.getId(), new String[]{ "Working", "Moderate", "Severe", "Failure" }, new double[][]{ new double[]{ 0.541, 0.272, 0.097, 0.088 } }));
        xUSBanks.setContent(new BayesVariable<String>("USBanks", xUSBanks.getId(), new String[]{ "Working", "Reduced", "NotWorking" }, new double[][]{ new double[]{ 0.488, 0.37, 0.141 } }));
        xUSStocks.setContent(new BayesVariable<String>("USStocks", xUSStocks.getId(), new String[]{ "Up", "Down", "Crash" }, new double[][]{ new double[]{ 0.433, 0.386, 0.179 } }));
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        JunctionTreeClique root = jtBuilder.build(false).getRoot();
        // clique1
        Assert.assertEquals(clique1, root.getBitSet());
        Assert.assertEquals(1, root.getChildren().size());
        // clique2
        JunctionTreeSeparator sep = root.getChildren().get(0);
        Assert.assertEquals(clique1And2, sep.getBitSet());
        JunctionTreeClique jtNode2 = sep.getChild();
        Assert.assertEquals(clique1, sep.getParent().getBitSet());
        Assert.assertEquals(clique2, jtNode2.getBitSet());
        Assert.assertEquals(2, jtNode2.getChildren().size());
        // clique3
        Assert.assertSame(sep, jtNode2.getParentSeparator());
        sep = jtNode2.getChildren().get(0);
        Assert.assertEquals(clique2And3, sep.getBitSet());
        JunctionTreeClique jtNode3 = sep.getChild();
        Assert.assertEquals(clique2, sep.getParent().getBitSet());
        Assert.assertEquals(clique3, jtNode3.getBitSet());
        Assert.assertEquals(0, jtNode3.getChildren().size());
        // clique4
        sep = jtNode2.getChildren().get(1);
        Assert.assertEquals(clique2And4, sep.getBitSet());
        JunctionTreeClique jtNode4 = sep.getChild();
        Assert.assertEquals(clique2, sep.getParent().getBitSet());
        Assert.assertEquals(clique4, jtNode4.getBitSet());
        Assert.assertEquals(0, jtNode4.getChildren().size());
    }
}

