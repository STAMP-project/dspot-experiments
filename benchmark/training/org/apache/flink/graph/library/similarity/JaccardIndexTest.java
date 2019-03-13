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
package org.apache.flink.graph.library.similarity;


import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.graph.asm.AsmTestBase;
import org.apache.flink.graph.asm.dataset.ChecksumHashCode.Checksum;
import org.apache.flink.graph.library.similarity.JaccardIndex.Result;
import org.apache.flink.test.util.TestBaseUtils;
import org.apache.flink.types.IntValue;
import org.apache.flink.types.LongValue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link JaccardIndex}.
 */
public class JaccardIndexTest extends AsmTestBase {
    @Test
    public void testSimpleGraph() throws Exception {
        DataSet<Result<IntValue>> ji = undirectedSimpleGraph.run(new JaccardIndex());
        String expectedResult = "(0,1,1,4)\n" + ((((((((("(0,2,1,4)\n" + "(0,3,2,4)\n") + "(1,2,2,4)\n") + "(1,3,1,6)\n") + "(1,4,1,3)\n") + "(1,5,1,3)\n") + "(2,3,1,6)\n") + "(2,4,1,3)\n") + "(2,5,1,3)\n") + "(4,5,1,1)\n");
        TestBaseUtils.compareResultAsText(ji.collect(), expectedResult);
    }

    @Test
    public void testWithSimpleGraphWithMinimumScore() throws Exception {
        DataSet<Result<IntValue>> ji = undirectedSimpleGraph.run(new JaccardIndex<IntValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setMinimumScore(1, 2));
        String expectedResult = "(0,3,2,4)\n" + ("(1,2,2,4)\n" + "(4,5,1,1)\n");
        TestBaseUtils.compareResultAsText(ji.collect(), expectedResult);
    }

    @Test
    public void testWithSimpleGraphWithMaximumScore() throws Exception {
        DataSet<Result<IntValue>> ji = undirectedSimpleGraph.run(new JaccardIndex<IntValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setMaximumScore(1, 2));
        String expectedResult = "(0,1,1,4)\n" + (((((((("(0,2,1,4)\n" + "(0,3,2,4)\n") + "(1,2,2,4)\n") + "(1,3,1,6)\n") + "(1,4,1,3)\n") + "(1,5,1,3)\n") + "(2,3,1,6)\n") + "(2,4,1,3)\n") + "(2,5,1,3)\n");
        TestBaseUtils.compareResultAsText(ji.collect(), expectedResult);
    }

    @Test
    public void testWithCompleteGraph() throws Exception {
        // all vertex pairs are linked
        long expectedCount = CombinatoricsUtils.binomialCoefficient(((int) (completeGraphVertexCount)), 2);
        // the intersection includes every vertex
        long expectedDistinctNeighborCount = completeGraphVertexCount;
        // the union only excludes the two vertices from the similarity score
        long expectedSharedNeighborCount = (completeGraphVertexCount) - 2;
        JaccardIndexTest.validate(completeGraph, expectedCount, expectedDistinctNeighborCount, expectedSharedNeighborCount);
    }

    @Test
    public void testWithEmptyGraphWithVertices() throws Exception {
        JaccardIndexTest.validate(emptyGraphWithVertices, 0, 0, 0);
    }

    @Test
    public void testWithEmptyGraphWithoutVertices() throws Exception {
        JaccardIndexTest.validate(emptyGraphWithoutVertices, 0, 0, 0);
    }

    @Test
    public void testWithStarGraph() throws Exception {
        // all leaf vertices form a triplet with all other leaf vertices;
        // only the center vertex is excluded
        long expectedCount = CombinatoricsUtils.binomialCoefficient((((int) (starGraphVertexCount)) - 1), 2);
        // the intersection includes only the center vertex
        long expectedDistinctNeighborCount = 1;
        // the union includes only the center vertex
        long expectedSharedNeighborCount = 1;
        JaccardIndexTest.validate(starGraph, expectedCount, expectedDistinctNeighborCount, expectedSharedNeighborCount);
    }

    @Test
    public void testWithRMatGraph() throws Exception {
        DataSet<Result<LongValue>> ji = undirectedRMatGraph(8, 8).run(new JaccardIndex<LongValue, org.apache.flink.types.NullValue, org.apache.flink.types.NullValue>().setGroupSize(4));
        Checksum checksum = new org.apache.flink.graph.asm.dataset.ChecksumHashCode<Result<LongValue>>().run(ji).execute();
        Assert.assertEquals(13954, checksum.getCount());
        Assert.assertEquals(29799011228939L, checksum.getChecksum());
    }
}

