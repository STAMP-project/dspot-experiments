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
package org.apache.flink.graph.library.clustering.directed;


import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.flink.graph.asm.AsmTestBase;
import org.apache.flink.graph.library.clustering.directed.TriadicCensus.Result;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link TriadicCensus}.
 */
public class TriadicCensusTest extends AsmTestBase {
    @Test
    public void testWithUndirectedSimpleGraph() throws Exception {
        Result expectedResult = new Result(3, 0, 8, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 2);
        Result triadCensus = new TriadicCensus<org.apache.flink.types.IntValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().run(undirectedSimpleGraph).execute();
        Assert.assertEquals(expectedResult, triadCensus);
    }

    @Test
    public void testWithDirectedSimpleGraph() throws Exception {
        Result expectedResult = new Result(3, 8, 0, 1, 2, 4, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0);
        Result triadCensus = new TriadicCensus<org.apache.flink.types.IntValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().run(directedSimpleGraph).execute();
        Assert.assertEquals(expectedResult, triadCensus);
    }

    @Test
    public void testWithCompleteGraph() throws Exception {
        long expectedDegree = (completeGraphVertexCount) - 1;
        long expectedCount = ((completeGraphVertexCount) * (CombinatoricsUtils.binomialCoefficient(((int) (expectedDegree)), 2))) / 3;
        Result expectedResult = new Result(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, expectedCount);
        Result triadCensus = new TriadicCensus<org.apache.flink.types.LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().run(completeGraph).execute();
        Assert.assertEquals(expectedResult, triadCensus);
    }

    @Test
    public void testWithEmptyGraphWithVertices() throws Exception {
        Result expectedResult = new Result(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        Result triadCensus = new TriadicCensus<org.apache.flink.types.LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().run(emptyGraphWithVertices).execute();
        Assert.assertEquals(expectedResult, triadCensus);
    }

    @Test
    public void testWithEmptyGraphWithoutVertices() throws Exception {
        Result expectedResult = new Result(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        Result triadCensus = new TriadicCensus<org.apache.flink.types.LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().run(emptyGraphWithoutVertices).execute();
        Assert.assertEquals(expectedResult, triadCensus);
    }

    @Test
    public void testWithUndirectedRMatGraph() throws Exception {
        Result expectedResult = new Result(113435893, 0, 7616063, 0, 0, 0, 0, 0, 0, 0, 778295, 0, 0, 0, 0, 75049);
        Result triadCensus = new TriadicCensus<org.apache.flink.types.LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().run(undirectedRMatGraph(10, 16)).execute();
        Assert.assertEquals(expectedResult, triadCensus);
    }

    /* This test result can be verified with the following Python script.

    import networkx as nx

    graph=nx.read_edgelist('directedRMatGraph.csv', delimiter=',', create_using=nx.DiGraph())
    census=nx.algorithms.triads.triadic_census(graph)
    for key in ['003', '012', '102', '021D', '021U', '021C', '111D', '111U', \
    '030T', '030C', '201', '120D', '120U', '120C', '210', '300']:
    print('{}: {}'.format(key, census[key]))
     */
    @Test
    public void testWithDirectedRMatGraph() throws Exception {
        Result expectedResult = new Result(113435893, 6632528, 983535, 118574, 118566, 237767, 129773, 130041, 16981, 5535, 43574, 7449, 7587, 15178, 17368, 4951);
        Result triadCensus = new TriadicCensus<org.apache.flink.types.LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().run(directedRMatGraph(10, 16)).execute();
        Assert.assertEquals(expectedResult, triadCensus);
    }
}

