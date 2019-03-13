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
 * Tests for {@link HypercubeGraph}.
 */
public class HypercubeGraphTest extends GraphGeneratorTestBase {
    @Test
    public void testGraph() throws Exception {
        int dimensions = 3;
        Graph<LongValue, NullValue, NullValue> graph = generate();
        String vertices = "0; 1; 2; 3; 4; 5; 6; 7";
        String edges = "0,1; 0,2; 0,4; 1,0; 1,3; 1,5; 2,0; 2,3; 2,6; 3,1; 3,2; 3,7;" + "4,0; 4,5; 4,6; 5,1; 5,4; 5,7; 6,2; 6,4; 6,7; 7,3; 7,6; 7,5";
        TestUtils.compareGraph(graph, vertices, edges);
    }

    @Test
    public void testGraphMetrics() throws Exception {
        int dimensions = 10;
        Graph<LongValue, NullValue, NullValue> graph = generate();
        Assert.assertEquals((1L << dimensions), graph.numberOfVertices());
        Assert.assertEquals((dimensions * (1L << dimensions)), graph.numberOfEdges());
        long minInDegree = graph.inDegrees().min(1).collect().get(0).f1.getValue();
        long minOutDegree = graph.outDegrees().min(1).collect().get(0).f1.getValue();
        long maxInDegree = graph.inDegrees().max(1).collect().get(0).f1.getValue();
        long maxOutDegree = graph.outDegrees().max(1).collect().get(0).f1.getValue();
        Assert.assertEquals(dimensions, minInDegree);
        Assert.assertEquals(dimensions, minOutDegree);
        Assert.assertEquals(dimensions, maxInDegree);
        Assert.assertEquals(dimensions, maxOutDegree);
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

