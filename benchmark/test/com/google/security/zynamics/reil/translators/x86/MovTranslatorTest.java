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
package com.google.security.zynamics.reil.translators.x86;


import OperandSize.DWORD;
import ReilRegisterStatus.DEFINED;
import com.google.common.collect.Lists;
import com.google.security.zynamics.reil.ReilInstruction;
import com.google.security.zynamics.reil.TestHelpers;
import com.google.security.zynamics.reil.interpreter.CpuPolicyX86;
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
public class MovTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.LITTLE_ENDIAN, new CpuPolicyX86(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final MovTranslator translator = new MovTranslator();

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    @Test
    public void testLiteral() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("eax", BigInteger.valueOf(4294967295L), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "eax"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "1"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("mov", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(2, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testLoadWord() throws InterpreterException, InternalTranslationException {
        // mov ax, word [ebp + 5]
        interpreter.setRegister("eax", BigInteger.valueOf(0), DWORD, DEFINED);
        interpreter.setRegister("ebx", BigInteger.valueOf(5), DWORD, DEFINED);
        interpreter.setRegister("ebp", BigInteger.valueOf(995), DWORD, DEFINED);
        interpreter.setMemory(1000, 305419896, 4);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "word");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "ax"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "word");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.MEMDEREF, "["));
        operandTree2.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "+"));
        operandTree2.root.m_children.get(0).m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "ebp"));
        operandTree2.root.m_children.get(0).m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "5"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("mov", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(4, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(22136), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.valueOf(4L), BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testRegister() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("eax", BigInteger.valueOf(4294967295L), DWORD, DEFINED);
        interpreter.setRegister("ebx", BigInteger.valueOf(5), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "eax"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "ebx"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("mov", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(3, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(5), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }
}

