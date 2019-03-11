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
package org.apache.flink.graph.library.metric.directed;


import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.flink.graph.asm.AsmTestBase;
import org.apache.flink.graph.library.metric.directed.VertexMetrics.Result;
import org.junit.Test;


/**
 * Tests for {@link VertexMetrics}.
 */
public class VertexMetricsTest extends AsmTestBase {
    @Test
    public void testWithSimpleGraph() throws Exception {
        VertexMetricsTest.validate(directedSimpleGraph, false, new Result(6, 7, 0, 13, 4, 2, 3, 6), (7.0F / 6), (7.0F / 30));
    }

    @Test
    public void testWithCompleteGraph() throws Exception {
        long expectedDegree = (completeGraphVertexCount) - 1;
        long expectedBidirectionalEdges = ((completeGraphVertexCount) * expectedDegree) / 2;
        long expectedMaximumTriplets = CombinatoricsUtils.binomialCoefficient(((int) (expectedDegree)), 2);
        long expectedTriplets = (completeGraphVertexCount) * expectedMaximumTriplets;
        Result expectedResult = new Result(completeGraphVertexCount, 0, expectedBidirectionalEdges, expectedTriplets, expectedDegree, expectedDegree, expectedDegree, expectedMaximumTriplets);
        VertexMetricsTest.validate(completeGraph, false, expectedResult, expectedDegree, 1.0F);
    }

    @Test
    public void testWithEmptyGraphWithVertices() throws Exception {
        VertexMetricsTest.validate(emptyGraphWithVertices, false, new Result(0, 0, 0, 0, 0, 0, 0, 0), Float.NaN, Float.NaN);
        VertexMetricsTest.validate(emptyGraphWithVertices, true, new Result(3, 0, 0, 0, 0, 0, 0, 0), 0.0F, 0.0F);
    }

    @Test
    public void testWithEmptyGraphWithoutVertices() throws Exception {
        Result expectedResult = new Result(0, 0, 0, 0, 0, 0, 0, 0);
        VertexMetricsTest.validate(emptyGraphWithoutVertices, false, expectedResult, Float.NaN, Float.NaN);
        VertexMetricsTest.validate(emptyGraphWithoutVertices, true, expectedResult, Float.NaN, Float.NaN);
    }

    @Test
    public void testWithRMatGraph() throws Exception {
        Result expectedResult = new Result(902, 8875, 1567, 1003442, 463, 334, 342, 106953);
        VertexMetricsTest.validate(directedRMatGraph(10, 16), false, expectedResult, 13.313747F, 0.0147766F);
    }
}

