/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.deeplearning4j.graph.graph;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.apache.commons.lang3.ArrayUtils;
import org.deeplearning4j.graph.data.GraphLoader;
import org.deeplearning4j.graph.iterator.RandomWalkIterator;
import org.deeplearning4j.graph.iterator.WeightedRandomWalkIterator;
import org.deeplearning4j.graph.vertexfactory.VertexFactory;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;

import static NoEdgeHandling.EXCEPTION_ON_DISCONNECTED;


public class TestGraph {
    @Test(timeout = 10000L)
    public void testSimpleGraph() {
        Graph<String, String> graph = new Graph(10, false, new TestGraph.VFactory());
        Assert.assertEquals(10, graph.numVertices());
        for (int i = 0; i < 10; i++) {
            // Add some undirected edges
            String str = (i + "--") + ((i + 1) % 10);
            Edge<String> edge = new Edge(i, ((i + 1) % 10), str, false);
            graph.addEdge(edge);
        }
        for (int i = 0; i < 10; i++) {
            List<Edge<String>> edges = graph.getEdgesOut(i);
            Assert.assertEquals(2, edges.size());
            // expect for example 0->1 and 9->0
            Edge<String> first = edges.get(0);
            if ((first.getFrom()) == i) {
                // undirected edge: i -> i+1 (or 9 -> 0)
                Assert.assertEquals(i, first.getFrom());
                Assert.assertEquals(((i + 1) % 10), first.getTo());
            } else {
                // undirected edge: i-1 -> i (or 9 -> 0)
                Assert.assertEquals((((i + 10) - 1) % 10), first.getFrom());
                Assert.assertEquals(i, first.getTo());
            }
            Edge<String> second = edges.get(1);
            Assert.assertNotEquals(first.getFrom(), second.getFrom());
            if ((second.getFrom()) == i) {
                // undirected edge: i -> i+1 (or 9 -> 0)
                Assert.assertEquals(i, second.getFrom());
                Assert.assertEquals(((i + 1) % 10), second.getTo());
            } else {
                // undirected edge: i-1 -> i (or 9 -> 0)
                Assert.assertEquals((((i + 10) - 1) % 10), second.getFrom());
                Assert.assertEquals(i, second.getTo());
            }
        }
    }

    private static class VFactory implements VertexFactory<String> {
        @Override
        public Vertex<String> create(int vertexIdx) {
            return new Vertex(vertexIdx, String.valueOf(vertexIdx));
        }
    }

    @Test(timeout = 10000L)
    public void testRandomWalkIterator() {
        Graph<String, String> graph = new Graph(10, false, new TestGraph.VFactory());
        Assert.assertEquals(10, graph.numVertices());
        for (int i = 0; i < 10; i++) {
            // Add some undirected edges
            String str = (i + "--") + ((i + 1) % 10);
            Edge<String> edge = new Edge(i, ((i + 1) % 10), str, false);
            graph.addEdge(edge);
        }
        int walkLength = 4;
        RandomWalkIterator<String> iter = new RandomWalkIterator(graph, walkLength, 1235, EXCEPTION_ON_DISCONNECTED);
        int count = 0;
        Set<Integer> startIdxSet = new HashSet<>();
        while (iter.hasNext()) {
            count++;
            IVertexSequence<String> sequence = iter.next();
            int seqCount = 1;
            int first = sequence.next().vertexID();
            int previous = first;
            while (sequence.hasNext()) {
                // Possible next vertices for this particular graph: (previous+1)%10 or (previous-1+10)%10
                int left = ((previous - 1) + 10) % 10;
                int right = (previous + 1) % 10;
                int current = sequence.next().vertexID();
                TestCase.assertTrue(((((("expected: " + left) + " or ") + right) + ", got ") + current), ((current == left) || (current == right)));
                seqCount++;
                previous = current;
            } 
            Assert.assertEquals(seqCount, (walkLength + 1));// walk of 0 -> 1 element, walk of 2 -> 3 elements etc

            Assert.assertFalse(startIdxSet.contains(first));// Expect to see each node exactly once

            startIdxSet.add(first);
        } 
        Assert.assertEquals(10, count);// Expect exactly 10 starting nodes

        Assert.assertEquals(10, startIdxSet.size());
    }

    @Test(timeout = 10000L)
    public void testWeightedRandomWalkIterator() throws Exception {
        // Load a directed, weighted graph from file
        String path = new ClassPathResource("deeplearning4j-graph/WeightedGraph.txt").getTempFileFromArchive().getAbsolutePath();
        int numVertices = 9;
        String delim = ",";
        String[] ignoreLinesStartingWith = new String[]{ "//" };// Comment lines start with "//"

        IGraph<String, Double> graph = GraphLoader.loadWeightedEdgeListFile(path, numVertices, delim, true, ignoreLinesStartingWith);
        Assert.assertEquals(numVertices, graph.numVertices());
        int[] vertexOutDegrees = new int[]{ 2, 2, 1, 2, 2, 1, 1, 1, 1 };
        for (int i = 0; i < numVertices; i++)
            Assert.assertEquals(vertexOutDegrees[i], graph.getVertexDegree(i));

        int[][] edges = new int[][]{ new int[]{ 1, 3 }// 0->1 and 1->3
        // 0->1 and 1->3
        // 0->1 and 1->3
        , new int[]{ 2, 4 }// 1->2 and 1->4
        // 1->2 and 1->4
        // 1->2 and 1->4
        , new int[]{ 5 }// etc
        // etc
        // etc
        , new int[]{ 4, 6 }, new int[]{ 5, 7 }, new int[]{ 8 }, new int[]{ 7 }, new int[]{ 8 }, new int[]{ 0 } };
        double[][] edgeWeights = new double[][]{ new double[]{ 1, 3 }, new double[]{ 12, 14 }, new double[]{ 25 }, new double[]{ 34, 36 }, new double[]{ 45, 47 }, new double[]{ 58 }, new double[]{ 67 }, new double[]{ 78 }, new double[]{ 80 } };
        double[][] edgeWeightsNormalized = new double[edgeWeights.length][0];
        for (int i = 0; i < (edgeWeights.length); i++) {
            double sum = 0.0;
            for (int j = 0; j < (edgeWeights[i].length); j++)
                sum += edgeWeights[i][j];

            edgeWeightsNormalized[i] = new double[edgeWeights[i].length];
            for (int j = 0; j < (edgeWeights[i].length); j++)
                edgeWeightsNormalized[i][j] = (edgeWeights[i][j]) / sum;

        }
        int walkLength = 5;
        WeightedRandomWalkIterator<String> iterator = new WeightedRandomWalkIterator(graph, walkLength, 12345);
        int walkCount = 0;
        Set<Integer> set = new HashSet<>();
        while (iterator.hasNext()) {
            IVertexSequence<String> walk = iterator.next();
            Assert.assertEquals((walkLength + 1), walk.sequenceLength());// Walk length of 5 -> 6 vertices (inc starting point)

            int thisWalkCount = 0;
            boolean first = true;
            int lastVertex = -1;
            while (walk.hasNext()) {
                Vertex<String> vertex = walk.next();
                if (first) {
                    Assert.assertFalse(set.contains(vertex.vertexID()));
                    set.add(vertex.vertexID());
                    lastVertex = vertex.vertexID();
                    first = false;
                } else {
                    // Ensure that a directed edge exists from lastVertex -> vertex
                    int currVertex = vertex.vertexID();
                    TestCase.assertTrue(ArrayUtils.contains(edges[lastVertex], currVertex));
                    lastVertex = currVertex;
                }
                thisWalkCount++;
            } 
            Assert.assertEquals((walkLength + 1), thisWalkCount);// Walk length of 5 -> 6 vertices (inc starting point)

            walkCount++;
        } 
        double[][] transitionProb = new double[numVertices][numVertices];
        int nWalks = 2000;
        for (int i = 0; i < nWalks; i++) {
            iterator.reset();
            while (iterator.hasNext()) {
                IVertexSequence<String> seq = iterator.next();
                int last = -1;
                while (seq.hasNext()) {
                    int curr = seq.next().vertexID();
                    if (last != (-1)) {
                        transitionProb[last][curr] += 1.0;
                    }
                    last = curr;
                } 
            } 
        }
        for (int i = 0; i < (transitionProb.length); i++) {
            double sum = 0.0;
            for (int j = 0; j < (transitionProb[i].length); j++)
                sum += transitionProb[i][j];

            for (int j = 0; j < (transitionProb[i].length); j++)
                transitionProb[i][j] /= sum;

            System.out.println(Arrays.toString(transitionProb[i]));
        }
        // Check that transition probs are essentially correct (within bounds of random variation)
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                if (!(ArrayUtils.contains(edges[i], j))) {
                    Assert.assertEquals(0.0, transitionProb[i][j], 0.0);
                } else {
                    int idx = ArrayUtils.indexOf(edges[i], j);
                    Assert.assertEquals(edgeWeightsNormalized[i][idx], transitionProb[i][j], 0.01);
                }
            }
        }
        for (int i = 0; i < numVertices; i++)
            TestCase.assertTrue(set.contains(i));

        Assert.assertEquals(numVertices, walkCount);
    }
}

