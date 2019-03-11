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
public class LmwTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.BIG_ENDIAN, new CpuPolicyPPC(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final LmwTranslator translator = new LmwTranslator();

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    @Test
    public void testALL() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("%r2", BigInteger.valueOf(0L), DWORD, DEFINED);
        interpreter.setRegister("%r1", BigInteger.valueOf(996), DWORD, DEFINED);
        interpreter.setMemory(1000, 286331153, 4);
        interpreter.setMemory(1004, 572662306, 4);
        interpreter.setMemory(1008, 858993459, 4);
        interpreter.setMemory(1012, 1145324612, 4);
        interpreter.setMemory(1016, 1431655765, 4);
        interpreter.setMemory(1020, 1717986918, 4);
        interpreter.setMemory(1024, 2004318071, 4);
        interpreter.setMemory(1028, -2004318072, 4);
        interpreter.setMemory(1032, -1717986919, 4);
        interpreter.setMemory(1036, 269488144, 4);
        interpreter.setMemory(1040, 286331153, 4);
        interpreter.setMemory(1044, 303174162, 4);
        interpreter.setMemory(1048, 320017171, 4);
        interpreter.setMemory(1052, 336860180, 4);
        interpreter.setMemory(1056, 353703189, 4);
        interpreter.setMemory(1060, 370546198, 4);
        interpreter.setMemory(1064, 387389207, 4);
        interpreter.setMemory(1068, 404232216, 4);
        interpreter.setMemory(1072, 421075225, 4);
        interpreter.setMemory(1076, 538976288, 4);
        interpreter.setMemory(1080, 555819297, 4);
        interpreter.setMemory(1084, 572662306, 4);
        interpreter.setMemory(1088, 589505315, 4);
        interpreter.setMemory(1092, 606348324, 4);
        interpreter.setMemory(1096, 623191333, 4);
        interpreter.setMemory(1100, 640034342, 4);
        interpreter.setMemory(1104, 656877351, 4);
        interpreter.setMemory(1108, 673720360, 4);
        interpreter.setMemory(1112, 690563369, 4);
        interpreter.setMemory(1116, -1, 4);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "byte");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "rtoc"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.MEMDEREF, "["));
        operandTree2.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "+"));
        operandTree2.root.m_children.get(0).getChildren().get(0).getChildren().add(new MockOperandTreeNode(ExpressionType.REGISTER, "%r1"));
        operandTree2.root.m_children.get(0).getChildren().get(0).getChildren().add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "4"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("lmw", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(996), interpreter.getVariableValue("%r1"));
        Assert.assertEquals(BigInteger.valueOf(286331153), interpreter.getVariableValue("%r2"));
        Assert.assertEquals(BigInteger.valueOf(572662306), interpreter.getVariableValue("%r3"));
        Assert.assertEquals(BigInteger.valueOf(858993459), interpreter.getVariableValue("%r4"));
        Assert.assertEquals(BigInteger.valueOf(1145324612), interpreter.getVariableValue("%r5"));
        Assert.assertEquals(BigInteger.valueOf(1431655765), interpreter.getVariableValue("%r6"));
        Assert.assertEquals(BigInteger.valueOf(1717986918), interpreter.getVariableValue("%r7"));
        Assert.assertEquals(BigInteger.valueOf(2004318071), interpreter.getVariableValue("%r8"));
        Assert.assertEquals(BigInteger.valueOf(2290649224L), interpreter.getVariableValue("%r9"));
        Assert.assertEquals(BigInteger.valueOf(2576980377L), interpreter.getVariableValue("%r10"));
        Assert.assertEquals(BigInteger.valueOf(269488144), interpreter.getVariableValue("%r11"));
        Assert.assertEquals(BigInteger.valueOf(286331153), interpreter.getVariableValue("%r12"));
        Assert.assertEquals(BigInteger.valueOf(303174162), interpreter.getVariableValue("%r13"));
        Assert.assertEquals(BigInteger.valueOf(320017171), interpreter.getVariableValue("%r14"));
        Assert.assertEquals(BigInteger.valueOf(336860180), interpreter.getVariableValue("%r15"));
        Assert.assertEquals(BigInteger.valueOf(353703189), interpreter.getVariableValue("%r16"));
        Assert.assertEquals(BigInteger.valueOf(370546198), interpreter.getVariableValue("%r17"));
        Assert.assertEquals(BigInteger.valueOf(387389207), interpreter.getVariableValue("%r18"));
        Assert.assertEquals(BigInteger.valueOf(404232216), interpreter.getVariableValue("%r19"));
        Assert.assertEquals(BigInteger.valueOf(421075225), interpreter.getVariableValue("%r20"));
        Assert.assertEquals(BigInteger.valueOf(538976288), interpreter.getVariableValue("%r21"));
        Assert.assertEquals(BigInteger.valueOf(555819297), interpreter.getVariableValue("%r22"));
        Assert.assertEquals(BigInteger.valueOf(572662306), interpreter.getVariableValue("%r23"));
        Assert.assertEquals(BigInteger.valueOf(589505315), interpreter.getVariableValue("%r24"));
        Assert.assertEquals(BigInteger.valueOf(606348324), interpreter.getVariableValue("%r25"));
        Assert.assertEquals(BigInteger.valueOf(623191333), interpreter.getVariableValue("%r26"));
        Assert.assertEquals(BigInteger.valueOf(640034342), interpreter.getVariableValue("%r27"));
        Assert.assertEquals(BigInteger.valueOf(656877351), interpreter.getVariableValue("%r28"));
        Assert.assertEquals(BigInteger.valueOf(673720360), interpreter.getVariableValue("%r29"));
        Assert.assertEquals(BigInteger.valueOf(690563369), interpreter.getVariableValue("%r30"));
        Assert.assertEquals(BigInteger.valueOf(4294967295L), interpreter.getVariableValue("%r31"));
        Assert.assertEquals(BigInteger.valueOf(120L), BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(32, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }
}

