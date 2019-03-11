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
package org.apache.flink.graph.library;


import SummarizationData.EXPECTED_EDGES_ABSENT_VALUES;
import SummarizationData.EXPECTED_EDGES_WITH_VALUES;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.graph.Edge;
import org.apache.flink.graph.Graph;
import org.apache.flink.graph.Vertex;
import org.apache.flink.graph.asm.translate.TranslateFunction;
import org.apache.flink.graph.examples.data.SummarizationData;
import org.apache.flink.graph.library.Summarization.EdgeValue;
import org.apache.flink.test.util.MultipleProgramsTestBase;
import org.apache.flink.types.NullValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link Summarization}.
 */
@RunWith(Parameterized.class)
public class SummarizationITCase extends MultipleProgramsTestBase {
    private static final Pattern TOKEN_SEPARATOR = Pattern.compile(";");

    private static final Pattern ID_SEPARATOR = Pattern.compile(",");

    public SummarizationITCase(TestExecutionMode mode) {
        super(mode);
    }

    @Test
    public void testWithVertexAndEdgeStringValues() throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        Graph<Long, String, String> input = Graph.fromDataSet(SummarizationData.getVertices(env), SummarizationData.getEdges(env), env);
        List<Vertex<Long, Summarization.VertexValue<String>>> summarizedVertices = new ArrayList<>();
        List<Edge<Long, EdgeValue<String>>> summarizedEdges = new ArrayList<>();
        Graph<Long, Summarization.VertexValue<String>, EdgeValue<String>> output = input.run(new Summarization());
        output.getVertices().output(new org.apache.flink.api.java.io.LocalCollectionOutputFormat(summarizedVertices));
        output.getEdges().output(new org.apache.flink.api.java.io.LocalCollectionOutputFormat(summarizedEdges));
        env.execute();
        validateVertices(SummarizationData.EXPECTED_VERTICES, summarizedVertices);
        validateEdges(EXPECTED_EDGES_WITH_VALUES, summarizedEdges);
    }

    @Test
    public void testWithVertexAndAbsentEdgeStringValues() throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        Graph<Long, String, NullValue> input = Graph.fromDataSet(SummarizationData.getVertices(env), SummarizationData.getEdges(env), env).run(new org.apache.flink.graph.asm.translate.TranslateEdgeValues(new org.apache.flink.graph.asm.translate.translators.ToNullValue()));
        List<Vertex<Long, Summarization.VertexValue<String>>> summarizedVertices = new ArrayList<>();
        List<Edge<Long, EdgeValue<NullValue>>> summarizedEdges = new ArrayList<>();
        Graph<Long, Summarization.VertexValue<String>, EdgeValue<NullValue>> output = input.run(new Summarization());
        output.getVertices().output(new org.apache.flink.api.java.io.LocalCollectionOutputFormat(summarizedVertices));
        output.getEdges().output(new org.apache.flink.api.java.io.LocalCollectionOutputFormat(summarizedEdges));
        env.execute();
        validateVertices(SummarizationData.EXPECTED_VERTICES, summarizedVertices);
        validateEdges(EXPECTED_EDGES_ABSENT_VALUES, summarizedEdges);
    }

    @Test
    public void testWithVertexAndEdgeLongValues() throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        Graph<Long, Long, Long> input = Graph.fromDataSet(SummarizationData.getVertices(env), SummarizationData.getEdges(env), env).run(new org.apache.flink.graph.asm.translate.TranslateVertexValues(new SummarizationITCase.StringToLong())).run(new org.apache.flink.graph.asm.translate.TranslateEdgeValues(new SummarizationITCase.StringToLong()));
        List<Vertex<Long, Summarization.VertexValue<Long>>> summarizedVertices = new ArrayList<>();
        List<Edge<Long, EdgeValue<Long>>> summarizedEdges = new ArrayList<>();
        Graph<Long, Summarization.VertexValue<Long>, EdgeValue<Long>> output = input.run(new Summarization());
        output.getVertices().output(new org.apache.flink.api.java.io.LocalCollectionOutputFormat(summarizedVertices));
        output.getEdges().output(new org.apache.flink.api.java.io.LocalCollectionOutputFormat(summarizedEdges));
        env.execute();
        validateVertices(SummarizationData.EXPECTED_VERTICES, summarizedVertices);
        validateEdges(EXPECTED_EDGES_WITH_VALUES, summarizedEdges);
    }

    private static class StringToLong implements TranslateFunction<String, Long> {
        @Override
        public Long translate(String value, Long reuse) throws Exception {
            return Long.parseLong(value);
        }
    }
}

