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
import org.junit.Test;


/**
 * Tests for {@link GlobalClusteringCoefficient}.
 */
public class GlobalClusteringCoefficientTest extends AsmTestBase {
    @Test
    public void testWithSimpleGraph() throws Exception {
        GlobalClusteringCoefficientTest.validate(directedSimpleGraph, 13, 6);
    }

    @Test
    public void testWithCompleteGraph() throws Exception {
        long expectedDegree = (completeGraphVertexCount) - 1;
        long expectedCount = (completeGraphVertexCount) * (CombinatoricsUtils.binomialCoefficient(((int) (expectedDegree)), 2));
        GlobalClusteringCoefficientTest.validate(completeGraph, expectedCount, expectedCount);
    }

    @Test
    public void testWithEmptyGraphWithVertices() throws Exception {
        GlobalClusteringCoefficientTest.validate(emptyGraphWithVertices, 0, 0);
    }

    @Test
    public void testWithEmptyGraphWithoutVertices() throws Exception {
        GlobalClusteringCoefficientTest.validate(emptyGraphWithoutVertices, 0, 0);
    }

    @Test
    public void testWithRMatGraph() throws Exception {
        GlobalClusteringCoefficientTest.validate(directedRMatGraph(10, 16), 1003442, 225147);
    }
}

