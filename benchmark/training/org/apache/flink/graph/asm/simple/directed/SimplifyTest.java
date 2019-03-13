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
package org.apache.flink.graph.asm.simple.directed;


import org.apache.flink.graph.Graph;
import org.apache.flink.graph.asm.AsmTestBase;
import org.apache.flink.graph.generator.TestUtils;
import org.apache.flink.test.util.TestBaseUtils;
import org.apache.flink.types.IntValue;
import org.apache.flink.types.NullValue;
import org.junit.Test;


/**
 * Tests for {@link Simplify}.
 */
public class SimplifyTest extends AsmTestBase {
    protected Graph<IntValue, NullValue, NullValue> graph;

    @Test
    public void test() throws Exception {
        String expectedResult = "(0,1,(null))\n" + ("(0,2,(null))\n" + "(1,0,(null))");
        Graph<IntValue, NullValue, NullValue> simpleGraph = graph.run(new Simplify());
        TestBaseUtils.compareResultAsText(simpleGraph.getEdges().collect(), expectedResult);
    }

    @Test
    public void testParallelism() throws Exception {
        int parallelism = 2;
        Graph<IntValue, NullValue, NullValue> simpleGraph = graph.run(new Simplify());
        simpleGraph.getVertices().output(new org.apache.flink.api.java.io.DiscardingOutputFormat());
        simpleGraph.getEdges().output(new org.apache.flink.api.java.io.DiscardingOutputFormat());
        TestUtils.verifyParallelism(env, parallelism);
    }
}

