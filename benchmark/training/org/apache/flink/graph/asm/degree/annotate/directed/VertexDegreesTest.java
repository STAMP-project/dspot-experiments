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
import org.apache.flink.graph.asm.degree.annotate.directed.VertexDegrees.Degrees;
import org.apache.flink.test.util.TestBaseUtils;
import org.apache.flink.types.IntValue;
import org.apache.flink.types.LongValue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link VertexDegrees}.
 */
public class VertexDegreesTest extends AsmTestBase {
    @Test
    public void testWithDirectedSimpleGraph() throws Exception {
        DataSet<Vertex<IntValue, Degrees>> degrees = directedSimpleGraph.run(new VertexDegrees());
        String expectedResult = "(0,(2,2,0))\n" + (((("(1,(3,0,3))\n" + "(2,(3,2,1))\n") + "(3,(4,2,2))\n") + "(4,(1,0,1))\n") + "(5,(1,1,0))");
        TestBaseUtils.compareResultAsText(degrees.collect(), expectedResult);
    }

    @Test
    public void testWithUndirectedSimpleGraph() throws Exception {
        DataSet<Vertex<IntValue, Degrees>> degrees = undirectedSimpleGraph.run(new VertexDegrees());
        String expectedResult = "(0,(2,2,2))\n" + (((("(1,(3,3,3))\n" + "(2,(3,3,3))\n") + "(3,(4,4,4))\n") + "(4,(1,1,1))\n") + "(5,(1,1,1))");
        TestBaseUtils.compareResultAsText(degrees.collect(), expectedResult);
    }

    @Test
    public void testWithEmptyGraphWithVertices() throws Exception {
        DataSet<Vertex<LongValue, Degrees>> degreesWithoutZeroDegreeVertices = emptyGraphWithVertices.run(new VertexDegrees<LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setIncludeZeroDegreeVertices(false));
        Assert.assertEquals(0, degreesWithoutZeroDegreeVertices.collect().size());
        DataSet<Vertex<LongValue, Degrees>> degreesWithZeroDegreeVertices = emptyGraphWithVertices.run(new VertexDegrees<LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setIncludeZeroDegreeVertices(true));
        String expectedResult = "(0,(0,0,0))\n" + ("(1,(0,0,0))\n" + "(2,(0,0,0))");
        TestBaseUtils.compareResultAsText(degreesWithZeroDegreeVertices.collect(), expectedResult);
    }

    @Test
    public void testWithEmptyGraphWithoutVertices() throws Exception {
        DataSet<Vertex<LongValue, Degrees>> degreesWithoutZeroDegreeVertices = emptyGraphWithoutVertices.run(new VertexDegrees<LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setIncludeZeroDegreeVertices(false));
        Assert.assertEquals(0, degreesWithoutZeroDegreeVertices.collect().size());
        DataSet<Vertex<LongValue, Degrees>> degreesWithZeroDegreeVertices = emptyGraphWithoutVertices.run(new VertexDegrees<LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setIncludeZeroDegreeVertices(true));
        Assert.assertEquals(0, degreesWithZeroDegreeVertices.collect().size());
    }

    @Test
    public void testWithRMatGraph() throws Exception {
        DataSet<Vertex<LongValue, Degrees>> degrees = directedRMatGraph(10, 16).run(new VertexDegrees());
        Checksum checksum = new org.apache.flink.graph.asm.dataset.ChecksumHashCode<Vertex<LongValue, Degrees>>().run(degrees).execute();
        Assert.assertEquals(902, checksum.getCount());
        Assert.assertEquals(1800402753642L, checksum.getChecksum());
    }
}

