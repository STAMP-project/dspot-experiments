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
package org.apache.flink.graph.asm.degree.annotate.undirected;


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
 * Tests for {@link VertexDegree}.
 */
public class VertexDegreeTest extends AsmTestBase {
    @Test
    public void testWithSimpleGraph() throws Exception {
        String expectedResult = "(0,2)\n" + (((("(1,3)\n" + "(2,3)\n") + "(3,4)\n") + "(4,1)\n") + "(5,1)");
        DataSet<Vertex<IntValue, LongValue>> degreeOnSourceId = undirectedSimpleGraph.run(new VertexDegree<IntValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setReduceOnTargetId(false));
        TestBaseUtils.compareResultAsText(degreeOnSourceId.collect(), expectedResult);
        DataSet<Vertex<IntValue, LongValue>> degreeOnTargetId = undirectedSimpleGraph.run(new VertexDegree<IntValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setReduceOnTargetId(true));
        TestBaseUtils.compareResultAsText(degreeOnTargetId.collect(), expectedResult);
    }

    @Test
    public void testWithCompleteGraph() throws Exception {
        long expectedDegree = (completeGraphVertexCount) - 1;
        DataSet<Vertex<LongValue, LongValue>> degreeOnSourceId = completeGraph.run(new VertexDegree<LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setReduceOnTargetId(false));
        for (Vertex<LongValue, LongValue> vertex : degreeOnSourceId.collect()) {
            Assert.assertEquals(expectedDegree, vertex.getValue().getValue());
        }
        DataSet<Vertex<LongValue, LongValue>> degreeOnTargetId = completeGraph.run(new VertexDegree<LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setReduceOnTargetId(true));
        for (Vertex<LongValue, LongValue> vertex : degreeOnTargetId.collect()) {
            Assert.assertEquals(expectedDegree, vertex.getValue().getValue());
        }
    }

    @Test
    public void testWithEmptyGraphWithVertices() throws Exception {
        DataSet<Vertex<LongValue, LongValue>> degree;
        degree = emptyGraphWithVertices.run(new VertexDegree<LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setIncludeZeroDegreeVertices(false));
        Assert.assertEquals(0, degree.collect().size());
        degree = emptyGraphWithVertices.run(new VertexDegree<LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setIncludeZeroDegreeVertices(true));
        String expectedResult = "(0,0)\n" + ("(1,0)\n" + "(2,0)");
        TestBaseUtils.compareResultAsText(degree.collect(), expectedResult);
    }

    @Test
    public void testWithEmptyGraphWithoutVertices() throws Exception {
        DataSet<Vertex<LongValue, LongValue>> degree;
        degree = emptyGraphWithoutVertices.run(new VertexDegree<LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setIncludeZeroDegreeVertices(false));
        Assert.assertEquals(0, degree.collect().size());
        degree = emptyGraphWithoutVertices.run(new VertexDegree<LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setIncludeZeroDegreeVertices(true));
        Assert.assertEquals(0, degree.collect().size());
    }

    @Test
    public void testWithRMatGraph() throws Exception {
        DataSet<Vertex<LongValue, LongValue>> degreeOnSourceId = undirectedRMatGraph(10, 16).run(new VertexDegree<LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setReduceOnTargetId(false));
        Checksum checksumOnSourceId = new org.apache.flink.graph.asm.dataset.ChecksumHashCode<Vertex<LongValue, LongValue>>().run(degreeOnSourceId).execute();
        Assert.assertEquals(902, checksumOnSourceId.getCount());
        Assert.assertEquals(14809904L, checksumOnSourceId.getChecksum());
        DataSet<Vertex<LongValue, LongValue>> degreeOnTargetId = undirectedRMatGraph(10, 16).run(new VertexDegree<LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setReduceOnTargetId(true));
        Checksum checksumOnTargetId = new org.apache.flink.graph.asm.dataset.ChecksumHashCode<Vertex<LongValue, LongValue>>().run(degreeOnTargetId).execute();
        Assert.assertEquals(checksumOnSourceId, checksumOnTargetId);
    }
}

