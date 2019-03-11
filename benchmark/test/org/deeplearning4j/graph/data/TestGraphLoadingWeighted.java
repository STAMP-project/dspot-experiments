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
package org.deeplearning4j.graph.data;


import java.io.IOException;
import java.util.List;
import junit.framework.TestCase;
import org.apache.commons.lang3.ArrayUtils;
import org.deeplearning4j.graph.api.Edge;
import org.deeplearning4j.graph.api.IGraph;
import org.deeplearning4j.graph.data.impl.WeightedEdgeLineProcessor;
import org.deeplearning4j.graph.graph.Graph;
import org.deeplearning4j.graph.vertexfactory.StringVertexFactory;
import org.deeplearning4j.graph.vertexfactory.VertexFactory;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;


public class TestGraphLoadingWeighted {
    @Test(timeout = 10000L)
    public void testWeightedDirected() throws IOException {
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
        for (int i = 0; i < numVertices; i++) {
            List<Edge<Double>> edgeList = graph.getEdgesOut(i);
            Assert.assertEquals(edges[i].length, edgeList.size());
            for (Edge<Double> e : edgeList) {
                int from = e.getFrom();
                int to = e.getTo();
                double weight = e.getValue();
                Assert.assertEquals(i, from);
                TestCase.assertTrue(ArrayUtils.contains(edges[i], to));
                int idx = ArrayUtils.indexOf(edges[i], to);
                Assert.assertEquals(edgeWeights[i][idx], weight, 0.0);
            }
        }
        System.out.println(graph);
    }

    @Test(timeout = 10000L)
    public void testWeightedDirectedV2() throws Exception {
        String path = new ClassPathResource("deeplearning4j-graph/WeightedGraph.txt").getTempFileFromArchive().getAbsolutePath();
        int numVertices = 9;
        String delim = ",";
        boolean directed = true;
        String[] ignoreLinesStartingWith = new String[]{ "//" };// Comment lines start with "//"

        IGraph<String, Double> graph = GraphLoader.loadWeightedEdgeListFile(path, numVertices, delim, directed, false, ignoreLinesStartingWith);
        Assert.assertEquals(numVertices, graph.numVertices());
        // EdgeLineProcessor: used to convert lines -> edges
        EdgeLineProcessor<Double> edgeLineProcessor = new WeightedEdgeLineProcessor(delim, directed, ignoreLinesStartingWith);
        // Vertex factory: used to create vertex objects, given an index for the vertex
        VertexFactory<String> vertexFactory = new StringVertexFactory();
        Graph<String, Double> graph2 = GraphLoader.loadGraph(path, edgeLineProcessor, vertexFactory, numVertices, false);
        Assert.assertEquals(graph, graph2);
    }
}

