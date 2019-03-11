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
package org.apache.flink.graph.library.metric.undirected;


import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.flink.graph.asm.AsmTestBase;
import org.apache.flink.graph.library.metric.undirected.VertexMetrics.Result;
import org.junit.Test;


/**
 * Tests for {@link VertexMetrics}.
 */
public class VertexMetricsTest extends AsmTestBase {
    @Test
    public void testWithSimpleGraph() throws Exception {
        VertexMetricsTest.validate(undirectedSimpleGraph, false, new Result(6, 7, 13, 4, 6), (14.0F / 6), (7.0F / 15));
    }

    @Test
    public void testWithCompleteGraph() throws Exception {
        long expectedDegree = (completeGraphVertexCount) - 1;
        long expectedEdges = ((completeGraphVertexCount) * expectedDegree) / 2;
        long expectedMaximumTriplets = CombinatoricsUtils.binomialCoefficient(((int) (expectedDegree)), 2);
        long expectedTriplets = (completeGraphVertexCount) * expectedMaximumTriplets;
        Result expectedResult = new Result(completeGraphVertexCount, expectedEdges, expectedTriplets, expectedDegree, expectedMaximumTriplets);
        VertexMetricsTest.validate(completeGraph, false, expectedResult, expectedDegree, 1.0F);
    }

    @Test
    public void testWithEmptyGraphWithVertices() throws Exception {
        VertexMetricsTest.validate(emptyGraphWithVertices, false, new Result(0, 0, 0, 0, 0), Float.NaN, Float.NaN);
        VertexMetricsTest.validate(emptyGraphWithVertices, true, new Result(3, 0, 0, 0, 0), 0.0F, 0.0F);
    }

    @Test
    public void testWithEmptyGraphWithoutVertices() throws Exception {
        Result expectedResult = new Result(0, 0, 0, 0, 0);
        VertexMetricsTest.validate(emptyGraphWithoutVertices, false, expectedResult, Float.NaN, Float.NaN);
        VertexMetricsTest.validate(emptyGraphWithoutVertices, true, expectedResult, Float.NaN, Float.NaN);
    }

    @Test
    public void testWithRMatGraph() throws Exception {
        Result expectedResult = new Result(902, 10442, 1003442, 463, 106953);
        VertexMetricsTest.validate(undirectedRMatGraph(10, 16), false, expectedResult, 23.152994F, 0.0256969F);
    }
}

