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
package org.apache.flink.graph.test.examples;


import java.util.List;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.graph.Graph;
import org.apache.flink.graph.Vertex;
import org.apache.flink.graph.examples.data.PageRankData;
import org.apache.flink.test.util.MultipleProgramsTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link PageRank}.
 */
@RunWith(Parameterized.class)
public class PageRankITCase extends MultipleProgramsTestBase {
    public PageRankITCase(TestExecutionMode mode) {
        super(mode);
    }

    @Test
    public void testPageRankWithThreeIterations() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        Graph<Long, Double, Double> inputGraph = Graph.fromDataSet(PageRankData.getDefaultEdgeDataSet(env), new PageRankITCase.InitMapper(), env);
        List<Vertex<Long, Double>> result = inputGraph.run(new org.apache.flink.graph.examples.PageRank(0.85, 3)).collect();
        compareWithDelta(result, 0.01);
    }

    @Test
    public void testGSAPageRankWithThreeIterations() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        Graph<Long, Double, Double> inputGraph = Graph.fromDataSet(PageRankData.getDefaultEdgeDataSet(env), new PageRankITCase.InitMapper(), env);
        List<Vertex<Long, Double>> result = inputGraph.run(new org.apache.flink.graph.examples.GSAPageRank(0.85, 3)).collect();
        compareWithDelta(result, 0.01);
    }

    @Test
    public void testPageRankWithThreeIterationsAndNumOfVertices() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        Graph<Long, Double, Double> inputGraph = Graph.fromDataSet(PageRankData.getDefaultEdgeDataSet(env), new PageRankITCase.InitMapper(), env);
        List<Vertex<Long, Double>> result = inputGraph.run(new org.apache.flink.graph.examples.PageRank(0.85, 3)).collect();
        compareWithDelta(result, 0.01);
    }

    @Test
    public void testGSAPageRankWithThreeIterationsAndNumOfVertices() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        Graph<Long, Double, Double> inputGraph = Graph.fromDataSet(PageRankData.getDefaultEdgeDataSet(env), new PageRankITCase.InitMapper(), env);
        List<Vertex<Long, Double>> result = inputGraph.run(new org.apache.flink.graph.examples.GSAPageRank(0.85, 3)).collect();
        compareWithDelta(result, 0.01);
    }

    @SuppressWarnings("serial")
    private static final class InitMapper implements MapFunction<Long, Double> {
        public Double map(Long value) {
            return 1.0;
        }
    }
}

