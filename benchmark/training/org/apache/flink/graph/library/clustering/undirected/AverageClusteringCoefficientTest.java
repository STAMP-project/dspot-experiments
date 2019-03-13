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
package org.apache.flink.graph.library.clustering.undirected;


import org.apache.flink.graph.asm.AsmTestBase;
import org.junit.Test;


/**
 * Tests for {@link AverageClusteringCoefficient}.
 */
public class AverageClusteringCoefficientTest extends AsmTestBase {
    @Test
    public void testWithSimpleGraph() throws Exception {
        // see results in LocalClusteringCoefficientTest.testSimpleGraph
        AverageClusteringCoefficientTest.validate(undirectedSimpleGraph, 6, (((((1.0 / 1) + (2.0 / 3)) + (2.0 / 3)) + (1.0 / 6)) / 6));
    }

    @Test
    public void testWithCompleteGraph() throws Exception {
        AverageClusteringCoefficientTest.validate(completeGraph, completeGraphVertexCount, 1.0);
    }

    @Test
    public void testWithEmptyGraphWithVertices() throws Exception {
        AverageClusteringCoefficientTest.validate(emptyGraphWithVertices, emptyGraphVertexCount, 0);
    }

    @Test
    public void testWithEmptyGraphWithoutVertices() throws Exception {
        AverageClusteringCoefficientTest.validate(emptyGraphWithoutVertices, 0, Double.NaN);
    }

    @Test
    public void testWithRMatGraph() throws Exception {
        AverageClusteringCoefficientTest.validate(undirectedRMatGraph(10, 16), 902, 0.42173);
    }
}

