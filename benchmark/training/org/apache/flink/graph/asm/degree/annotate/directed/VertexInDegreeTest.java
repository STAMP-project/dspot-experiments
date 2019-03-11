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
package org.apache.flink.graph.asm.degree.annotate.directed;


import org.apache.flink.api.java.DataSet;
import org.apache.flink.graph.Vertex;
import org.apache.flink.graph.asm.AsmTestBase;
import org.apache.flink.graph.asm.dataset.ChecksumHashCode.Checksum;
import org.apache.flink.test.util.TestBaseUtils;
import org.apache.flink.types.IntValue;
import org.apache.flink.types.LongValue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link VertexInDegree}.
 */
public class VertexInDegreeTest extends AsmTestBase {
    @Test
    public void testWithDirectedSimpleGraph() throws Exception {
        DataSet<Vertex<IntValue, LongValue>> inDegree = directedSimpleGraph.run(new VertexInDegree<IntValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setIncludeZeroDegreeVertices(true));
        String expectedResult = "(0,0)\n" + (((("(1,3)\n" + "(2,1)\n") + "(3,2)\n") + "(4,1)\n") + "(5,0)");
        TestBaseUtils.compareResultAsText(inDegree.collect(), expectedResult);
    }

    @Test
    public void testWithUndirectedSimpleGraph() throws Exception {
        DataSet<Vertex<IntValue, LongValue>> inDegree = undirectedSimpleGraph.run(new VertexInDegree<IntValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setIncludeZeroDegreeVertices(true));
        String expectedResult = "(0,2)\n" + (((("(1,3)\n" + "(2,3)\n") + "(3,4)\n") + "(4,1)\n") + "(5,1)");
        TestBaseUtils.compareResultAsText(inDegree.collect(), expectedResult);
    }

    @Test
    public void testWithEmptyGraphWithVertices() throws Exception {
        DataSet<Vertex<LongValue, LongValue>> inDegreeWithoutZeroDegreeVertices = emptyGraphWithVertices.run(new VertexInDegree<LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setIncludeZeroDegreeVertices(false));
        Assert.assertEquals(0, inDegreeWithoutZeroDegreeVertices.collect().size());
        DataSet<Vertex<LongValue, LongValue>> inDegreeWithZeroDegreeVertices = emptyGraphWithVertices.run(new VertexInDegree<LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setIncludeZeroDegreeVertices(true));
        String expectedResult = "(0,0)\n" + ("(1,0)\n" + "(2,0)");
        TestBaseUtils.compareResultAsText(inDegreeWithZeroDegreeVertices.collect(), expectedResult);
    }

    @Test
    public void testWithEmptyGraphWithoutVertices() throws Exception {
        DataSet<Vertex<LongValue, LongValue>> inDegreeWithoutZeroDegreeVertices = emptyGraphWithoutVertices.run(new VertexInDegree<LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setIncludeZeroDegreeVertices(false));
        Assert.assertEquals(0, inDegreeWithoutZeroDegreeVertices.collect().size());
        DataSet<Vertex<LongValue, LongValue>> inDegreeWithZeroDegreeVertices = emptyGraphWithoutVertices.run(new VertexInDegree<LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setIncludeZeroDegreeVertices(true));
        Assert.assertEquals(0, inDegreeWithZeroDegreeVertices.collect().size());
    }

    @Test
    public void testWithRMatGraph() throws Exception {
        DataSet<Vertex<LongValue, LongValue>> inDegree = directedRMatGraph(10, 16).run(new VertexInDegree<LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setIncludeZeroDegreeVertices(true));
        Checksum checksum = new org.apache.flink.graph.asm.dataset.ChecksumHashCode<Vertex<LongValue, LongValue>>().run(inDegree).execute();
        Assert.assertEquals(902, checksum.getCount());
        Assert.assertEquals(14801029L, checksum.getChecksum());
    }
}

