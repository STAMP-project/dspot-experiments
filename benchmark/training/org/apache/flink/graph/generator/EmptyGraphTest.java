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
package org.apache.flink.graph.generator;


import org.apache.flink.graph.Graph;
import org.apache.flink.types.LongValue;
import org.apache.flink.types.NullValue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link EmptyGraph}.
 */
public class EmptyGraphTest extends GraphGeneratorTestBase {
    @Test
    public void testGraph() throws Exception {
        Graph<LongValue, NullValue, NullValue> graph = generate();
        String vertices = "0; 1; 2; 3; 4; 5; 6; 7; 8; 9";
        String edges = null;
        TestUtils.compareGraph(graph, vertices, edges);
    }

    @Test
    public void testGraphMetrics() throws Exception {
        int vertexCount = 100;
        Graph<LongValue, NullValue, NullValue> graph = generate();
        Assert.assertEquals(vertexCount, graph.numberOfVertices());
        Assert.assertEquals(0, graph.numberOfEdges());
        long maxInDegree = graph.inDegrees().max(1).collect().get(0).f1.getValue();
        long maxOutDegree = graph.outDegrees().max(1).collect().get(0).f1.getValue();
        Assert.assertEquals(0, maxInDegree);
        Assert.assertEquals(0, maxOutDegree);
    }

    @Test
    public void testParallelism() throws Exception {
        int parallelism = 2;
        Graph<LongValue, NullValue, NullValue> graph = generate();
        graph.getVertices().output(new org.apache.flink.api.java.io.DiscardingOutputFormat());
        graph.getEdges().output(new org.apache.flink.api.java.io.DiscardingOutputFormat());
        TestUtils.verifyParallelism(env, parallelism);
    }
}

