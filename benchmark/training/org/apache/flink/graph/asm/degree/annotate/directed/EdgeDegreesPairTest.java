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
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.graph.Edge;
import org.apache.flink.graph.asm.AsmTestBase;
import org.apache.flink.graph.asm.dataset.ChecksumHashCode.Checksum;
import org.apache.flink.graph.asm.degree.annotate.directed.VertexDegrees.Degrees;
import org.apache.flink.test.util.TestBaseUtils;
import org.apache.flink.types.IntValue;
import org.apache.flink.types.LongValue;
import org.apache.flink.types.NullValue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link EdgeDegreesPair}.
 */
public class EdgeDegreesPairTest extends AsmTestBase {
    @Test
    public void testWithSimpleGraph() throws Exception {
        String expectedResult = "(0,1,((null),(2,2,0),(3,0,3)))\n" + ((((("(0,2,((null),(2,2,0),(3,2,1)))\n" + "(2,1,((null),(3,2,1),(3,0,3)))\n") + "(2,3,((null),(3,2,1),(4,2,2)))\n") + "(3,1,((null),(4,2,2),(3,0,3)))\n") + "(3,4,((null),(4,2,2),(1,0,1)))\n") + "(5,3,((null),(1,1,0),(4,2,2)))");
        DataSet<Edge<IntValue, Tuple3<NullValue, Degrees, Degrees>>> degreesPair = directedSimpleGraph.run(new EdgeDegreesPair());
        TestBaseUtils.compareResultAsText(degreesPair.collect(), expectedResult);
    }

    @Test
    public void testWithEmptyGraphWithVertices() throws Exception {
        DataSet<Edge<LongValue, Tuple3<NullValue, Degrees, Degrees>>> degreesPair = emptyGraphWithVertices.run(new EdgeDegreesPair());
        Assert.assertEquals(0, degreesPair.collect().size());
    }

    @Test
    public void testWithEmptyGraphWithoutVertices() throws Exception {
        DataSet<Edge<LongValue, Tuple3<NullValue, Degrees, Degrees>>> degreesPair = emptyGraphWithoutVertices.run(new EdgeDegreesPair());
        Assert.assertEquals(0, degreesPair.collect().size());
    }

    @Test
    public void testWithRMatGraph() throws Exception {
        DataSet<Edge<LongValue, Tuple3<NullValue, Degrees, Degrees>>> degreesPair = directedRMatGraph(10, 16).run(new EdgeDegreesPair());
        Checksum checksum = new org.apache.flink.graph.asm.dataset.ChecksumHashCode<Edge<LongValue, Tuple3<NullValue, Degrees, Degrees>>>().run(degreesPair).execute();
        Assert.assertEquals(12009, checksum.getCount());
        Assert.assertEquals(25769422553763L, checksum.getChecksum());
    }
}

