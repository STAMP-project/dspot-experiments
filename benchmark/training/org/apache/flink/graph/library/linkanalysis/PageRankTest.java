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
package org.apache.flink.graph.library.linkanalysis;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.graph.asm.AsmTestBase;
import org.apache.flink.graph.library.linkanalysis.PageRank.Result;
import org.apache.flink.types.IntValue;
import org.apache.flink.types.LongValue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link PageRank}.
 */
public class PageRankTest extends AsmTestBase {
    private static final double DAMPING_FACTOR = 0.85;

    /* This test result can be verified with the following Python script.

    import networkx as nx

    graph=nx.read_edgelist('directedSimpleGraph.csv', delimiter=',', create_using=nx.DiGraph())
    pagerank=nx.algorithms.link_analysis.pagerank(graph, tol=0.000001)

    for key in sorted(pagerank):
    print('{}: {}'.format(key, pagerank[key]))
     */
    @Test
    public void testWithSimpleGraph() throws Exception {
        DataSet<Result<IntValue>> pr = new PageRank<IntValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>(PageRankTest.DAMPING_FACTOR, 20).run(directedSimpleGraph);
        List<Double> expectedResults = new ArrayList<>();
        expectedResults.add(0.0909212166211);
        expectedResults.add(0.279516064311);
        expectedResults.add(0.129562719068);
        expectedResults.add(0.223268406353);
        expectedResults.add(0.185810377026);
        expectedResults.add(0.0909212166211);
        for (Result<IntValue> result : pr.collect()) {
            int id = result.getVertexId0().getValue();
            Assert.assertEquals(expectedResults.get(id), result.getPageRankScore().getValue(), AsmTestBase.ACCURACY);
        }
    }

    @Test
    public void testWithCompleteGraph() throws Exception {
        PageRankTest.validate(completeGraph, completeGraphVertexCount, (1.0 / (completeGraphVertexCount)));
    }

    @Test
    public void testWithEmptyGraphWithVertices() throws Exception {
        PageRankTest.validate(emptyGraphWithVertices, emptyGraphVertexCount, (1.0 / (emptyGraphVertexCount)));
    }

    @Test
    public void testWithEmptyGraphWithoutVertices() throws Exception {
        PageRankTest.validate(emptyGraphWithoutVertices, 0, Double.NaN);
    }

    /* This test result can be verified with the following Python script.

    import networkx as nx

    graph=nx.read_edgelist('directedRMatGraph.csv', delimiter=',', create_using=nx.DiGraph())
    pagerank=nx.algorithms.link_analysis.pagerank(graph, tol=0.000001)

    for key in [0, 1, 2, 8, 13, 29, 109, 394, 652, 1020]:
    print('{}: {}'.format(key, pagerank[str(key)]))
     */
    @Test
    public void testWithRMatGraph() throws Exception {
        DataSet<Result<LongValue>> pr = new PageRank<LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>(PageRankTest.DAMPING_FACTOR, AsmTestBase.ACCURACY).run(directedRMatGraph(10, 16));
        Map<Long, Result<LongValue>> results = new HashMap<>();
        for (Result<LongValue> result : new org.apache.flink.graph.asm.dataset.Collect<Result<LongValue>>().run(pr).execute()) {
            results.put(result.getVertexId0().getValue(), result);
        }
        Assert.assertEquals(902, results.size());
        Map<Long, Double> expectedResults = new HashMap<>();
        // a pseudo-random selection of results, both high and low
        expectedResults.put(0L, 0.0271152394743);
        expectedResults.put(1L, 0.0132848430616);
        expectedResults.put(2L, 0.0121819700294);
        expectedResults.put(8L, 0.0115923214664);
        expectedResults.put(13L, 0.00183241122822);
        expectedResults.put(29L, 8.48190646547E-4);
        expectedResults.put(109L, 3.0846825644E-4);
        expectedResults.put(394L, 8.28826945546E-4);
        expectedResults.put(652L, 6.83948671035E-4);
        expectedResults.put(1020L, 2.50442325034E-4);
        for (Map.Entry<Long, Double> expected : expectedResults.entrySet()) {
            double value = results.get(expected.getKey()).getPageRankScore().getValue();
            Assert.assertEquals(expected.getValue(), value, AsmTestBase.ACCURACY);
        }
    }
}

