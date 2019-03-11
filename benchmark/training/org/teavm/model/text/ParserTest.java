/**
 * Copyright 2016 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.model.text;


import org.junit.Assert;
import org.junit.Test;
import org.teavm.model.BasicBlock;
import org.teavm.model.Program;


public class ParserTest {
    @Test
    public void simple() throws Exception {
        Program program = runTest("simple");
        Assert.assertEquals(2, program.basicBlockCount());
        Assert.assertEquals(4, program.variableCount());
        Assert.assertEquals(4, program.basicBlockAt(0).instructionCount());
        Assert.assertEquals(1, program.basicBlockAt(1).instructionCount());
    }

    @Test
    public void conditional() throws Exception {
        Program program = runTest("conditional");
        Assert.assertEquals(7, program.basicBlockCount());
        for (int i = 0; i < 7; ++i) {
            Assert.assertEquals(1, program.basicBlockAt(i).instructionCount());
        }
    }

    @Test
    public void phi() throws Exception {
        Program program = runTest("phi");
        Assert.assertEquals(4, program.basicBlockCount());
        Assert.assertEquals(2, program.basicBlockAt(3).getPhis().size());
    }

    @Test
    public void constant() throws Exception {
        Program program = runTest("constant");
        Assert.assertEquals(1, program.basicBlockCount());
        BasicBlock block = program.basicBlockAt(0);
        Assert.assertEquals(7, block.instructionCount());
    }

    @Test
    public void invocation() throws Exception {
        Program program = runTest("invocation");
        Assert.assertEquals(1, program.basicBlockCount());
    }

    @Test
    public void casting() throws Exception {
        Program program = runTest("casting");
        Assert.assertEquals(1, program.basicBlockCount());
    }

    @Test
    public void operations() throws Exception {
        runTest("operations");
    }

    @Test
    public void create() throws Exception {
        Program program = runTest("create");
        Assert.assertEquals(1, program.basicBlockCount());
    }

    @Test
    public void fields() throws Exception {
        runTest("fields");
    }

    @Test
    public void switchInsn() throws Exception {
        runTest("switchInsn");
    }

    @Test
    public void exceptions() throws Exception {
        runTest("exceptions");
    }
}

