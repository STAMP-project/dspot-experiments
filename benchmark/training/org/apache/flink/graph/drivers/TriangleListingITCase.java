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
 * Tests for {@link TriangleListing}.
 */
@RunWith(Parameterized.class)
public class TriangleListingITCase extends CopyableValueDriverBaseITCase {
    public TriangleListingITCase(String idType, TestExecutionMode mode) {
        super(idType, mode);
    }

    @Test
    public void testLongDescription() throws Exception {
        String expected = regexSubstring(new TriangleListing().getLongDescription());
        expectedOutputFromException(new String[]{ "--algorithm", "TriangleListing" }, expected, ProgramParametrizationException.class);
    }

    @Test
    public void testHashWithDirectedRMatGraph() throws Exception {
        String expected = (((((((((((((((((("\n" + (new Checksum(61410, 131751978901411L))) + "\n\n") + "Triadic census:\n") + "  003: 1,679,209\n") + "  012: 267,130\n") + "  102: 57,972\n") + "  021d: 8,496\n") + "  021u: 8,847\n") + "  021c: 17,501\n") + "  111d: 13,223\n") + "  111u: 12,865\n") + "  030t: 1,674\n") + "  030c: 572\n") + "  201: 5,678\n") + "  120d: 1,066\n") + "  120u: 896\n") + "  120c: 2,011\n") + "  210: 2,867\n") + "  300: 1,149\n";
        expectedOutput(parameters(8, "directed", "hash"), expected);
    }

    @Test
    public void testHashWithUndirectedRMatGraph() throws Exception {
        String expected = (((((("\n" + (new Checksum(61410, 131847301932256L))) + "\n\n") + "Triadic census:\n") + "  03: 1,679,209\n") + "  12: 325,102\n") + "  21: 66,610\n") + "  30: 10,235\n";
        expectedOutput(parameters(8, "undirected", "hash"), expected);
    }

    @Test
    public void testPrintWithDirectedRMatGraph() throws Exception {
        // skip 'char' since it is not printed as a number
        Assume.assumeFalse(((idType.equals("char")) || (idType.equals("nativeChar"))));
        expectedOutputChecksum(parameters(8, "directed", "print"), new Checksum(61410, 131775627144330L));
    }

    @Test
    public void testPrintWithUndirectedRMatGraph() throws Exception {
        // skip 'char' since it is not printed as a number
        Assume.assumeFalse(((idType.equals("char")) || (idType.equals("nativeChar"))));
        expectedOutputChecksum(parameters(8, "undirected", "print"), new Checksum(61410, 132010059662222L));
    }

    @Test
    public void testParallelism() throws Exception {
        String[] largeOperators = new String[]{ "FlatMap \\(Permute triangle vertices\\)", "Join \\(Triangle listing\\)", "GroupReduce \\(Generate triplets\\)" };
        TestUtils.verifyParallelism(parameters(8, "directed", "print"), largeOperators);
        TestUtils.verifyParallelism(parameters(8, "undirected", "print"), largeOperators);
    }
}

