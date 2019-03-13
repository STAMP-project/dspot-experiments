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


import OperandSize.BYTE;
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
public class ARMAddTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.BIG_ENDIAN, new CpuPolicyARM(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final ARMAddTranslator translator = new ARMAddTranslator();

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    final OperandSize dw = OperandSize.DWORD;

    final OperandSize wd = OperandSize.WORD;

    final OperandSize bt = OperandSize.BYTE;

    @Test
    public void testSimpleASR() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(2233), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ONE, bt, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R2"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "ASR"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R1"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "1"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("ADD", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(2233), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(2453), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(5, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testSimpleASRS() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(2233), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("N", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Z", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("V", BigInteger.ZERO, bt, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R2"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "ASR"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R1"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "1"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("ADDS", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(2233), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(2453), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("N"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Z"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("V"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(8, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testSimpleASRSregister() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(2233), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(1476L), dw, DEFINED);
        interpreter.setRegister("R12", BigInteger.valueOf(15L), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("N", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Z", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("V", BigInteger.ZERO, bt, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R2"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "ASR"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R1"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R12"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("ADDS", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(2233), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(1337L), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.valueOf(15L), interpreter.getVariableValue("R12"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("N"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Z"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("V"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(9, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testSimpleImmediate() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(2233), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(4455), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ONE, bt, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R2"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, String.valueOf(1234)));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("ADD", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(2233), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(2571), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(5, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testSimpleImmediateS() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(2233), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(2147486101L), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ONE, bt, DEFINED);
        interpreter.setRegister("N", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Z", BigInteger.ONE, bt, DEFINED);
        interpreter.setRegister("V", BigInteger.ZERO, bt, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R2"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, String.valueOf(1234)));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("ADDS", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(2233), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(2571), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("N"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Z"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("V"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(8, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testSimpleLSL() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(2233), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(3570), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ONE, bt, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R2"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "LSL"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R1"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "1"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("ADD", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(2233), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(5803), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(5, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testSimpleLSLS() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(2233), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(3570), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("N", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Z", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("V", BigInteger.ZERO, bt, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R2"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "LSL"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R1"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "1"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("ADDS", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(2233), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(5803), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("N"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Z"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("V"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(8, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testSimpleLSLSregister() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(2233), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(2453L), dw, DEFINED);
        interpreter.setRegister("R3", BigInteger.valueOf(4L), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("N", BigInteger.ONE, bt, DEFINED);
        interpreter.setRegister("Z", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("V", BigInteger.ZERO, bt, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R2"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "LSL"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R1"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R3"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("ADDS", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(2233), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(37065L), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.valueOf(4L), interpreter.getVariableValue("R3"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("N"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Z"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("V"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(9, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testSimpleLSR() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(2233), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(5803), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ONE, bt, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R2"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "LSR"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R1"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "12"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("ADD", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(2233), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(5, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testSimpleLSRS() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(2233), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(5804), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("N", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Z", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("V", BigInteger.ZERO, bt, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R2"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "LSR"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R1"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "12"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("ADDS", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(2233), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("N"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Z"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("V"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(8, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testSimpleLSRSregister() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(2233), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(37065L), dw, DEFINED);
        interpreter.setRegister("R3", BigInteger.valueOf(4L), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("N", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Z", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("V", BigInteger.ZERO, bt, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R2"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "LSR"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R1"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R3"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("ADDS", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(2233), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(1476L), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.valueOf(4L), interpreter.getVariableValue("R3"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("N"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Z"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("V"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(9, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testSimpleRegister() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(2233), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(2571), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ONE, bt, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R2"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R1"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("ADD", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(2233), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(3570), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(5, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testSimpleRegisterS() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(2233), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(2572), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("N", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Z", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("V", BigInteger.ZERO, bt, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R2"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R1"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("ADDS", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(2233), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(3570), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("N"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Z"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("V"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(8, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testSimpleROR() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(2233), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(2453), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ONE, bt, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R2"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "ROR"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R1"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "5"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("ADD", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(2233), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(3355444606L), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(5, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testSimpleRORS() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(2233), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(2453), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("N", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Z", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("V", BigInteger.ZERO, bt, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R2"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "ROR"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R1"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "5"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("ADDS", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(2233), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(3355444606L), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("N"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Z"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("V"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(8, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testSimpleRORSregister() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(2233), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(1337L), dw, DEFINED);
        interpreter.setRegister("R3", BigInteger.valueOf(4L), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("N", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Z", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("V", BigInteger.ZERO, bt, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R2"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "ROR"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R1"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R3"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("ADDS", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(2233), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(2415920580L), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.valueOf(4L), interpreter.getVariableValue("R3"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("N"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Z"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("V"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(9, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testSimpleRRX() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(2233), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(3355444607L), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ONE, bt, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R2"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "RRX"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R1"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("ADD", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(2233), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(2147486101L), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(5, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testSimpleRRXS() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(1337), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(2233), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(3355444606L), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ZERO, BYTE, DEFINED);
        interpreter.setRegister("N", BigInteger.ONE, BYTE, DEFINED);
        interpreter.setRegister("Z", BigInteger.ZERO, BYTE, DEFINED);
        interpreter.setRegister("V", BigInteger.ZERO, BYTE, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R2"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "RRX"));
        operandTree3.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R1"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("ADDS", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(1337), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(2233), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(2453L), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("N"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Z"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("V"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(8, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }
}

