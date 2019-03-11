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
package org.apache.flink.graph.asm.degree.filter.undirected;


import org.apache.flink.graph.Graph;
import org.apache.flink.graph.asm.AsmTestBase;
import org.apache.flink.graph.asm.dataset.ChecksumHashCode.Checksum;
import org.apache.flink.test.util.TestBaseUtils;
import org.apache.flink.types.IntValue;
import org.apache.flink.types.LongValue;
import org.apache.flink.types.NullValue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link MaximumDegree}.
 */
public class MaximumDegreeTest extends AsmTestBase {
    @Test
    public void testWithSimpleGraph() throws Exception {
        Graph<IntValue, NullValue, NullValue> graph = undirectedSimpleGraph.run(new MaximumDegree(3));
        String expectedVerticesResult = "(0,(null))\n" + ((("(1,(null))\n" + "(2,(null))\n") + "(4,(null))\n") + "(5,(null))");
        TestBaseUtils.compareResultAsText(graph.getVertices().collect(), expectedVerticesResult);
        String expectedEdgesResult = "(0,1,(null))\n" + (((("(0,2,(null))\n" + "(1,0,(null))\n") + "(1,2,(null))\n") + "(2,0,(null))\n") + "(2,1,(null))");
        TestBaseUtils.compareResultAsText(graph.getEdges().collect(), expectedEdgesResult);
    }

    @Test
    public void testWithEmptyGraphWithVertices() throws Exception {
        Graph<LongValue, NullValue, NullValue> graph = emptyGraphWithVertices.run(new MaximumDegree(1));
        Assert.assertEquals(emptyGraphVertexCount, graph.getVertices().collect().size());
        Assert.assertEquals(0, graph.getEdges().collect().size());
    }

    @Test
    public void testWithEmptyGraphWithoutVertices() throws Exception {
        Graph<LongValue, NullValue, NullValue> graph = emptyGraphWithoutVertices.run(new MaximumDegree(1));
        Assert.assertEquals(0, graph.getVertices().collect().size());
        Assert.assertEquals(0, graph.getEdges().collect().size());
    }

    @Test
    public void testWithRMatGraph() throws Exception {
        Checksum checksum = undirectedRMatGraph(10, 16).run(new MaximumDegree(16)).run(new org.apache.flink.graph.library.metric.ChecksumHashCode()).execute();
        Assert.assertEquals(805, checksum.getCount());
        Assert.assertEquals(134384451L, checksum.getChecksum());
    }
}

