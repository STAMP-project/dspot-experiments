/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.reil.translators.ppc;


import OperandSize.DWORD;
import ReilRegisterStatus.DEFINED;
import com.google.common.collect.Lists;
import com.google.security.zynamics.reil.ReilInstruction;
import com.google.security.zynamics.reil.TestHelpers;
import com.google.security.zynamics.reil.interpreter.CpuPolicyPPC;
import com.google.security.zynamics.reil.interpreter.EmptyInterpreterPolicy;
import com.google.security.zynamics.reil.interpreter.Endianness;
import com.google.security.zynamics.reil.interpreter.InterpreterException;
import com.google.security.zynamics.reil.interpreter.ReilInterpreter;
import com.google.security.zynamics.reil.translators.InternalTranslationException;
import com.google.security.zynamics.reil.translators.StandardEnvironment;
import com.google.security.zynamics.zylib.disassembly.ExpressionType;
import com.google.security.zynamics.zylib.disassembly.IInstruction;
import com.google.security.zynamics.zylib.disassembly.MockInstruction;
import com.google.security.zynamics.zylib.disassembly.MockOperandTree;
import com.google.security.zynamics.zylib.disassembly.MockOperandTreeNode;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class StmwTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.BIG_ENDIAN, new CpuPolicyPPC(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final StmwTranslator translator = new StmwTranslator();

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    @Test
    public void testALL() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("%r1", BigInteger.valueOf(996), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "byte");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "%r2"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.MEMDEREF, "["));
        operandTree2.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "+"));
        operandTree2.root.m_children.get(0).getChildren().get(0).getChildren().add(new MockOperandTreeNode(ExpressionType.REGISTER, "%r1"));
        operandTree2.root.m_children.get(0).getChildren().get(0).getChildren().add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "4"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("stmw", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(996), interpreter.getVariableValue("%r1"));
        Assert.assertEquals(BigInteger.valueOf(286331153), BigInteger.valueOf(interpreter.readMemoryDword(1000)));
        Assert.assertEquals(BigInteger.valueOf(572662306), BigInteger.valueOf(interpreter.readMemoryDword(1004)));
        Assert.assertEquals(BigInteger.valueOf(858993459), BigInteger.valueOf(interpreter.readMemoryDword(1008)));
        Assert.assertEquals(BigInteger.valueOf(1145324612), BigInteger.valueOf(interpreter.readMemoryDword(1012)));
        Assert.assertEquals(BigInteger.valueOf(1431655765), BigInteger.valueOf(interpreter.readMemoryDword(1016)));
        Assert.assertEquals(BigInteger.valueOf(1717986918), BigInteger.valueOf(interpreter.readMemoryDword(1020)));
        Assert.assertEquals(BigInteger.valueOf(2004318071), BigInteger.valueOf(interpreter.readMemoryDword(1024)));
        Assert.assertEquals(BigInteger.valueOf(-2004318072), BigInteger.valueOf(interpreter.readMemoryDword(1028)));
        Assert.assertEquals(BigInteger.valueOf(-1717986919), BigInteger.valueOf(interpreter.readMemoryDword(1032)));
        Assert.assertEquals(BigInteger.valueOf(269488144), BigInteger.valueOf(interpreter.readMemoryDword(1036)));
        Assert.assertEquals(BigInteger.valueOf(286331153), BigInteger.valueOf(interpreter.readMemoryDword(1040)));
        Assert.assertEquals(BigInteger.valueOf(303174162), BigInteger.valueOf(interpreter.readMemoryDword(1044)));
        Assert.assertEquals(BigInteger.valueOf(320017171), BigInteger.valueOf(interpreter.readMemoryDword(1048)));
        Assert.assertEquals(BigInteger.valueOf(336860180), BigInteger.valueOf(interpreter.readMemoryDword(1052)));
        Assert.assertEquals(BigInteger.valueOf(353703189), BigInteger.valueOf(interpreter.readMemoryDword(1056)));
        Assert.assertEquals(BigInteger.valueOf(370546198), BigInteger.valueOf(interpreter.readMemoryDword(1060)));
        Assert.assertEquals(BigInteger.valueOf(387389207), BigInteger.valueOf(interpreter.readMemoryDword(1064)));
        Assert.assertEquals(BigInteger.valueOf(404232216), BigInteger.valueOf(interpreter.readMemoryDword(1068)));
        Assert.assertEquals(BigInteger.valueOf(421075225), BigInteger.valueOf(interpreter.readMemoryDword(1072)));
        Assert.assertEquals(BigInteger.valueOf(538976288), BigInteger.valueOf(interpreter.readMemoryDword(1076)));
        Assert.assertEquals(BigInteger.valueOf(555819297), BigInteger.valueOf(interpreter.readMemoryDword(1080)));
        Assert.assertEquals(BigInteger.valueOf(572662306), BigInteger.valueOf(interpreter.readMemoryDword(1084)));
        Assert.assertEquals(BigInteger.valueOf(589505315), BigInteger.valueOf(interpreter.readMemoryDword(1088)));
        Assert.assertEquals(BigInteger.valueOf(606348324), BigInteger.valueOf(interpreter.readMemoryDword(1092)));
        Assert.assertEquals(BigInteger.valueOf(623191333), BigInteger.valueOf(interpreter.readMemoryDword(1096)));
        Assert.assertEquals(BigInteger.valueOf(640034342), BigInteger.valueOf(interpreter.readMemoryDword(1100)));
        Assert.assertEquals(BigInteger.valueOf(656877351), BigInteger.valueOf(interpreter.readMemoryDword(1104)));
        Assert.assertEquals(BigInteger.valueOf(673720360), BigInteger.valueOf(interpreter.readMemoryDword(1108)));
        Assert.assertEquals(BigInteger.valueOf(690563369), BigInteger.valueOf(interpreter.readMemoryDword(1112)));
        Assert.assertEquals(BigInteger.valueOf(-1), BigInteger.valueOf(interpreter.readMemoryDword(1116)));
        Assert.assertEquals(BigInteger.valueOf(120L), BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(32, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }
}

