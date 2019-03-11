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
package org.apache.flink.graph.drivers;


import org.apache.flink.client.program.ProgramParametrizationException;
import org.apache.flink.graph.asm.dataset.ChecksumHashCode.Checksum;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link EdgeList}.
 */
@RunWith(Parameterized.class)
public class EdgeListITCase extends NonTransformableDriverBaseITCase {
    public EdgeListITCase(String idType, TestExecutionMode mode) {
        super(idType, mode);
    }

    @Test
    public void testLongDescription() throws Exception {
        String expected = regexSubstring(new EdgeList().getLongDescription());
        expectedOutputFromException(new String[]{ "--algorithm", "EdgeList" }, expected, ProgramParametrizationException.class);
    }

    @Test
    public void testHashWithCirculantGraph() throws Exception {
        expectedChecksum(getCirculantGraphParameters("hash"), 168, 110208);
    }

    @Test
    public void testPrintWithCirculantGraph() throws Exception {
        // skip 'char' since it is not printed as a number
        Assume.assumeFalse(((idType.equals("char")) || (idType.equals("nativeChar"))));
        expectedOutputChecksum(getCirculantGraphParameters("print"), new Checksum(168, 325826456764L));
    }

    @Test
    public void testParallelismWithCirculantGraph() throws Exception {
        TestUtils.verifyParallelism(getCirculantGraphParameters("print"));
    }

    @Test
    public void testHashWithCompleteGraph() throws Exception {
        expectedChecksum(getCompleteGraphParameters("hash"), 1722, 1129632L);
    }

    @Test
    public void testPrintWithCompleteGraph() throws Exception {
        // skip 'char' since it is not printed as a number
        Assume.assumeFalse(((idType.equals("char")) || (idType.equals("nativeChar"))));
        expectedOutputChecksum(getCompleteGraphParameters("print"), new Checksum(1722, 3371710858136L));
    }

    @Test
    public void testParallelismWithCompleteGraph() throws Exception {
        TestUtils.verifyParallelism(getCompleteGraphParameters("print"));
    }

    @Test
    public void testHashWithCycleGraph() throws Exception {
        expectedChecksum(getCycleGraphParameters("hash"), 84, 55104L);
    }

    @Test
    public void testPrintWithCycleGraph() throws Exception {
        // skip 'char' since it is not printed as a number
        Assume.assumeFalse(((idType.equals("char")) || (idType.equals("nativeChar"))));
        expectedOutputChecksum(getCycleGraphParameters("print"), new Checksum(84, 168209641418L));
    }

    @Test
    public void testParallelismWithCycleGraph() throws Exception {
        TestUtils.verifyParallelism(getCycleGraphParameters("print"));
    }

    @Test
    public void testHashWithEchoGraph() throws Exception {
        expectedChecksum(getEchoGraphParameters("hash"), 546, 358176L);
    }

    @Test
    public void testPrintWithEchoGraph() throws Exception {
        // skip 'char' since it is not printed as a number
        Assume.assumeFalse(((idType.equals("char")) || (idType.equals("nativeChar"))));
        expectedOutputChecksum(getEchoGraphParameters("print"), new Checksum(546, 1061277110218L));
    }

    @Test
    public void testParallelismWithEchoGraph() throws Exception {
        TestUtils.verifyParallelism(getEchoGraphParameters("print"));
    }

    @Test
    public void testHashWithEmptyGraph() throws Exception {
        expectedChecksum(getEmptyGraphParameters("hash"), 0, 0L);
    }

    @Test
    public void testPrintWithEmptyGraph() throws Exception {
        expectedOutputChecksum(getEmptyGraphParameters("print"), new Checksum(0, 0L));
    }

    @Test
    public void testParallelismWithEmptyGraph() throws Exception {
        TestUtils.verifyParallelism(getEmptyGraphParameters("print"));
    }

    @Test
    public void testHashWithGridGraph() throws Exception {
        expectedChecksum(getGridGraphParameters("hash"), 130, 60320L);
    }

    @Test
    public void testPrintWithGridGraph() throws Exception {
        // skip 'char' since it is not printed as a number
        Assume.assumeFalse(((idType.equals("char")) || (idType.equals("nativeChar"))));
        expectedOutputChecksum(getGridGraphParameters("print"), new Checksum(130, 219638736110L));
    }

    @Test
    public void testParallelismWithGridGraph() throws Exception {
        TestUtils.verifyParallelism(getGridGraphParameters("print"));
    }

    @Test
    public void testHashWithHypercubeGraph() throws Exception {
        expectedChecksum(getHypercubeGraphParameters("hash"), 896, 1820672L);
    }

    @Test
    public void testPrintWithHypercubeGraph() throws Exception {
        // skip 'char' since it is not printed as a number
        Assume.assumeFalse(((idType.equals("char")) || (idType.equals("nativeChar"))));
        expectedOutputChecksum(getHypercubeGraphParameters("print"), new Checksum(896, 2140033397682L));
    }

    @Test
    public void testParallelismWithHypercubeGraph() throws Exception {
        TestUtils.verifyParallelism(getHypercubeGraphParameters("print"));
    }

    @Test
    public void testHashWithPathGraph() throws Exception {
        expectedChecksum(getPathGraphParameters("hash"), 82, 53792L);
    }

    @Test
    public void testPrintWithPathGraph() throws Exception {
        // skip 'char' since it is not printed as a number
        Assume.assumeFalse(((idType.equals("char")) || (idType.equals("nativeChar"))));
        expectedOutputChecksum(getPathGraphParameters("print"), new Checksum(82, 165824091330L));
    }

    @Test
    public void testParallelismWithPathGraph() throws Exception {
        TestUtils.verifyParallelism(getPathGraphParameters("print"));
    }

    @Test
    public void testHashWithRMatGraph() throws Exception {
        expectedChecksum(getRMatGraphParameters("hash", null), 2048, 2024745);
    }

    @Test
    public void testPrintWithRMatGraph() throws Exception {
        // skip 'char' since it is not printed as a number
        Assume.assumeFalse(((idType.equals("char")) || (idType.equals("nativeChar"))));
        expectedOutputChecksum(getRMatGraphParameters("print", null), new Checksum(2048, 3260812599045L));
    }

    @Test
    public void testParallelismWithRMatGraph() throws Exception {
        TestUtils.verifyParallelism(getRMatGraphParameters("print", null));
    }

    @Test
    public void testHashWithDirectedRMatGraph() throws Exception {
        expectedChecksum(getRMatGraphParameters("hash", "directed"), 1168, 1407421L);
    }

    @Test
    public void testPrintWithDirectedRMatGraph() throws Exception {
        // skip 'char' since it is not printed as a number
        Assume.assumeFalse(((idType.equals("char")) || (idType.equals("nativeChar"))));
        expectedOutputChecksum(getRMatGraphParameters("print", "directed"), new Checksum(1168, 2260053586781L));
    }

    @Test
    public void testParallelismWithDirectedRMatGraph() throws Exception {
        TestUtils.verifyParallelism(getRMatGraphParameters("print", "directed"));
    }

    @Test
    public void testHashWithUndirectedRMatGraph() throws Exception {
        expectedChecksum(getRMatGraphParameters("hash", "undirected"), 1854, 2369824L);
    }

    @Test
    public void testPrintWithUndirectedRMatGraph() throws Exception {
        // skip 'char' since it is not printed as a number
        Assume.assumeFalse(((idType.equals("char")) || (idType.equals("nativeChar"))));
        expectedOutputChecksum(getRMatGraphParameters("print", "undirected"), new Checksum(1854, 3779126632802L));
    }

    @Test
    public void testParallelismWithUndirectedRMatGraph() throws Exception {
        TestUtils.verifyParallelism(getRMatGraphParameters("print", "undirected"));
    }

    @Test
    public void testHashWithSingletonEdgeGraph() throws Exception {
        expectedChecksum(getSingletonEdgeGraphParameters("hash"), 84, 111552L);
    }

    @Test
    public void testPrintWithSingletonEdgeGraph() throws Exception {
        // skip 'char' since it is not printed as a number
        Assume.assumeFalse(((idType.equals("char")) || (idType.equals("nativeChar"))));
        expectedOutputChecksum(getSingletonEdgeGraphParameters("print"), new Checksum(84, 199076416922L));
    }

    @Test
    public void testParallelismWithSingletonEdgeGraph() throws Exception {
        TestUtils.verifyParallelism(getSingletonEdgeGraphParameters("print"));
    }

    @Test
    public void testHashWithStarGraph() throws Exception {
        expectedChecksum(getStarGraphParameters("hash"), 82, 27552L);
    }

    @Test
    public void testPrintWithStarGraph() throws Exception {
        // skip 'char' since it is not printed as a number
        Assume.assumeFalse(((idType.equals("char")) || (idType.equals("nativeChar"))));
        expectedOutputChecksum(getStarGraphParameters("print"), new Checksum(82, 76978040552L));
    }

    @Test
    public void testParallelismWithStarGraph() throws Exception {
        TestUtils.verifyParallelism(getStarGraphParameters("print"));
    }
}

