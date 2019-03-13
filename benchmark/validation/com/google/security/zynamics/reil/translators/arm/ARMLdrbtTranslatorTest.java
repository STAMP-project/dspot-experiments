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
package com.google.security.zynamics.reil.translators.arm;


import ReilRegisterStatus.DEFINED;
import com.google.common.collect.Lists;
import com.google.security.zynamics.reil.OperandSize;
import com.google.security.zynamics.reil.ReilInstruction;
import com.google.security.zynamics.reil.TestHelpers;
import com.google.security.zynamics.reil.interpreter.CpuPolicyARM;
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
public class ARMLdrbtTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.LITTLE_ENDIAN, new CpuPolicyARM(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final ARMLdrbtTranslator translator = new ARMLdrbtTranslator();

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    final OperandSize dw = OperandSize.DWORD;

    final OperandSize wd = OperandSize.WORD;

    final OperandSize bt = OperandSize.BYTE;

    @Test
    public void testLdrEPostIndexedASR() throws InterpreterException, InternalTranslationException {
        // LDRBT r7,[r1],r3, ASR #2
        interpreter.setRegister("R0", BigInteger.valueOf(134217692L), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(33061L), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(70L), dw, DEFINED);
        interpreter.setRegister("R3", BigInteger.valueOf(4532738L), dw, DEFINED);
        interpreter.setRegister("R4", BigInteger.valueOf(33060L), dw, DEFINED);
        interpreter.setRegister("R5", BigInteger.valueOf(38564L), dw, DEFINED);
        interpreter.setRegister("R6", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R7", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R8", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R9", BigInteger.valueOf(255L), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ONE, bt, DEFINED);
        interpreter.setRegister("N", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Z", BigInteger.ONE, bt, DEFINED);
        interpreter.setRegister("V", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Q", BigInteger.ZERO, bt, DEFINED);
        interpreter.setMemory(33061L, 1953722985L, 4);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R7"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, ","));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.MEMDEREF, "["));
        operandTree2.root.getChildren().get(0).getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R1"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "ASR"));
        operandTree2.root.getChildren().get(0).getChildren().get(1).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R3"));
        operandTree2.root.getChildren().get(0).getChildren().get(1).m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, String.valueOf(2)));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("LDRBT", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(134217692L), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(1166245L), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(70L), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.valueOf(4532738L), interpreter.getVariableValue("R3"));
        Assert.assertEquals(BigInteger.valueOf(33060L), interpreter.getVariableValue("R4"));
        Assert.assertEquals(BigInteger.valueOf(38564L), interpreter.getVariableValue("R5"));
        Assert.assertEquals(BigInteger.valueOf(0L), interpreter.getVariableValue("R6"));
        Assert.assertEquals(BigInteger.valueOf(105L), interpreter.getVariableValue("R7"));
        Assert.assertEquals(BigInteger.valueOf(0L), interpreter.getVariableValue("R8"));
        Assert.assertEquals(BigInteger.valueOf(255L), interpreter.getVariableValue("R9"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("N"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("Z"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("V"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Q"));
        Assert.assertEquals(BigInteger.valueOf(4), BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(16, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }
}

