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


import OperandSize.BYTE;
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
public class AddTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.LITTLE_ENDIAN, new CpuPolicyX86(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final AddTranslator translator = new AddTranslator();

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    @Test
    public void testC() throws InterpreterException, InternalTranslationException {
        // Set carry but not overflow
        interpreter.setRegister("CF", BigInteger.ZERO, BYTE, DEFINED);
        interpreter.setRegister("eax", BigInteger.valueOf(4294967295L), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "eax"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "1"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("add", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(6, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("CF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("OF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("SF"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("ZF"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testCO() throws InterpreterException, InternalTranslationException {
        // Set carry and overflow
        interpreter.setRegister("CF", BigInteger.ZERO, BYTE, DEFINED);
        interpreter.setRegister("eax", BigInteger.valueOf(2147483648L), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "eax"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "2147483648"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("add", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(6, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(0), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("CF"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("OF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("SF"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("ZF"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testO() throws InterpreterException, InternalTranslationException {
        // Set overflow but not carry
        interpreter.setRegister("CF", BigInteger.ZERO, BYTE, DEFINED);
        interpreter.setRegister("eax", BigInteger.valueOf(2147483647L), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "eax"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "1"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("add", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(6, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(2147483648L), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CF"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("OF"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("SF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("ZF"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testR32R32() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("CF", BigInteger.ZERO, BYTE, DEFINED);
        interpreter.setRegister("eax", BigInteger.valueOf(8192), DWORD, DEFINED);
        interpreter.setRegister("ebx", BigInteger.valueOf(12288), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "eax"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "ebx"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("add", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(7, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(20480), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.valueOf(12288), interpreter.getVariableValue("ebx"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("OF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("SF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("ZF"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testR32R32C() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("CF", BigInteger.ONE, BYTE, DEFINED);
        interpreter.setRegister("eax", BigInteger.valueOf(8192), DWORD, DEFINED);
        interpreter.setRegister("ebx", BigInteger.valueOf(12288), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "eax"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "ebx"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("add", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(7, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(20480), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.valueOf(12288), interpreter.getVariableValue("ebx"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("OF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("SF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("ZF"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testR32Same() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("CF", BigInteger.ZERO, BYTE, DEFINED);
        interpreter.setRegister("eax", BigInteger.valueOf(8192), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "eax"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "eax"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("add", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(6, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(16384), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("OF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("SF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("ZF"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testR32SameC() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("CF", BigInteger.ONE, BYTE, DEFINED);
        interpreter.setRegister("eax", BigInteger.valueOf(8192), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "eax"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "eax"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("add", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(6, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(16384), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("OF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("SF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("ZF"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }
}

