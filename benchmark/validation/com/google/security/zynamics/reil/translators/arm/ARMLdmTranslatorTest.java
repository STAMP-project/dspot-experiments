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
public class ARMLdmTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.BIG_ENDIAN, new CpuPolicyARM(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final ARMLdmTranslator translator = new ARMLdmTranslator();

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    final OperandSize dw = OperandSize.DWORD;

    final OperandSize wd = OperandSize.WORD;

    final OperandSize bt = OperandSize.BYTE;

    @Test
    public void testLdmDA() throws InterpreterException, InternalTranslationException {
        // DA == FA for Ldm
        interpreter.setRegister("R0", BigInteger.valueOf(32924L), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(32972L), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(20L), dw, DEFINED);
        interpreter.setRegister("R3", BigInteger.valueOf(2L), dw, DEFINED);
        interpreter.setRegister("R4", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R5", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R6", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R7", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R8", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R9", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R10", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R11", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ONE, bt, DEFINED);
        interpreter.setRegister("N", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Z", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("V", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Q", BigInteger.ZERO, bt, DEFINED);
        interpreter.setMemory(32896L, 2L, 4);
        interpreter.setMemory(32900L, 3L, 4);
        interpreter.setMemory(32904L, 4L, 4);
        interpreter.setMemory(32908L, 5L, 4);
        interpreter.setMemory(32912L, 6L, 4);
        interpreter.setMemory(32916L, 7L, 4);
        interpreter.setMemory(32920L, 8L, 4);
        interpreter.setMemory(32924L, 1L, 4);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.EXPRESSION_LIST, "{"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R4"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R5"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R6"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R7"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R8"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R9"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R10"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R11"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("LDMDA", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(32924L), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(32972L), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(20L), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.valueOf(2L), interpreter.getVariableValue("R3"));
        Assert.assertEquals(BigInteger.valueOf(2L), interpreter.getVariableValue("R4"));
        Assert.assertEquals(BigInteger.valueOf(3L), interpreter.getVariableValue("R5"));
        Assert.assertEquals(BigInteger.valueOf(4L), interpreter.getVariableValue("R6"));
        Assert.assertEquals(BigInteger.valueOf(5L), interpreter.getVariableValue("R7"));
        Assert.assertEquals(BigInteger.valueOf(6L), interpreter.getVariableValue("R8"));
        Assert.assertEquals(BigInteger.valueOf(7L), interpreter.getVariableValue("R9"));
        Assert.assertEquals(BigInteger.valueOf(8L), interpreter.getVariableValue("R10"));
        Assert.assertEquals(BigInteger.valueOf(1L), interpreter.getVariableValue("R11"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("N"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Z"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("V"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Q"));
        Assert.assertEquals(BigInteger.valueOf((8 * 4)), BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(18, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testLdmDB() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(32924L), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(32972L), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(20L), dw, DEFINED);
        interpreter.setRegister("R3", BigInteger.valueOf(2L), dw, DEFINED);
        interpreter.setRegister("R4", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R5", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R6", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R7", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R8", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R9", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R10", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R11", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ONE, bt, DEFINED);
        interpreter.setRegister("N", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Z", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("V", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Q", BigInteger.ZERO, bt, DEFINED);
        interpreter.setMemory(32892L, 1L, 4);
        interpreter.setMemory(32896L, 2L, 4);
        interpreter.setMemory(32900L, 3L, 4);
        interpreter.setMemory(32904L, 4L, 4);
        interpreter.setMemory(32908L, 5L, 4);
        interpreter.setMemory(32912L, 6L, 4);
        interpreter.setMemory(32916L, 7L, 4);
        interpreter.setMemory(32920L, 8L, 4);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.EXPRESSION_LIST, "{"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R4"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R5"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R6"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R7"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R8"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R9"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R10"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R11"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("LDMDB", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(32924L), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(32972L), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(20L), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.valueOf(2L), interpreter.getVariableValue("R3"));
        Assert.assertEquals(BigInteger.valueOf(1L), interpreter.getVariableValue("R4"));
        Assert.assertEquals(BigInteger.valueOf(2L), interpreter.getVariableValue("R5"));
        Assert.assertEquals(BigInteger.valueOf(3L), interpreter.getVariableValue("R6"));
        Assert.assertEquals(BigInteger.valueOf(4L), interpreter.getVariableValue("R7"));
        Assert.assertEquals(BigInteger.valueOf(5L), interpreter.getVariableValue("R8"));
        Assert.assertEquals(BigInteger.valueOf(6L), interpreter.getVariableValue("R9"));
        Assert.assertEquals(BigInteger.valueOf(7L), interpreter.getVariableValue("R10"));
        Assert.assertEquals(BigInteger.valueOf(8L), interpreter.getVariableValue("R11"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("N"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Z"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("V"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Q"));
        Assert.assertEquals(BigInteger.valueOf((8 * 4)), BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(18, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testLdmFD() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(32924L), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(32972L), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(20L), dw, DEFINED);
        interpreter.setRegister("R3", BigInteger.valueOf(2L), dw, DEFINED);
        interpreter.setRegister("R4", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R5", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R6", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R7", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R8", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R9", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R10", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R11", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ONE, bt, DEFINED);
        interpreter.setRegister("N", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Z", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("V", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Q", BigInteger.ZERO, bt, DEFINED);
        interpreter.setMemory(32924L, 1L, 4);
        interpreter.setMemory(32928L, 2L, 4);
        interpreter.setMemory(32932L, 3L, 4);
        interpreter.setMemory(32936L, 4L, 4);
        interpreter.setMemory(32940L, 5L, 4);
        interpreter.setMemory(32944L, 6L, 4);
        interpreter.setMemory(32948L, 7L, 4);
        interpreter.setMemory(32952L, 8L, 4);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.EXPRESSION_LIST, "{"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R4"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R5"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R6"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R7"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R8"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R9"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R10"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R11"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        // FD is IA
        final IInstruction instruction = new MockInstruction("LDMFD", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(32924L), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(32972L), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(20L), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.valueOf(2L), interpreter.getVariableValue("R3"));
        Assert.assertEquals(BigInteger.valueOf(1L), interpreter.getVariableValue("R4"));
        Assert.assertEquals(BigInteger.valueOf(2L), interpreter.getVariableValue("R5"));
        Assert.assertEquals(BigInteger.valueOf(3L), interpreter.getVariableValue("R6"));
        Assert.assertEquals(BigInteger.valueOf(4L), interpreter.getVariableValue("R7"));
        Assert.assertEquals(BigInteger.valueOf(5L), interpreter.getVariableValue("R8"));
        Assert.assertEquals(BigInteger.valueOf(6L), interpreter.getVariableValue("R9"));
        Assert.assertEquals(BigInteger.valueOf(7L), interpreter.getVariableValue("R10"));
        Assert.assertEquals(BigInteger.valueOf(8L), interpreter.getVariableValue("R11"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("N"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Z"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("V"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Q"));
        Assert.assertEquals(BigInteger.valueOf((8 * 4)), BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(18, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testLdmIA() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(32864L), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(32944L), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(20L), dw, DEFINED);
        interpreter.setRegister("R3", BigInteger.valueOf(2L), dw, DEFINED);
        interpreter.setRegister("R4", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R5", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R6", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R7", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R8", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R9", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R10", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R11", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ONE, bt, DEFINED);
        interpreter.setRegister("N", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Z", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("V", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Q", BigInteger.ZERO, bt, DEFINED);
        interpreter.setMemory(32864L, 1L, 4);
        interpreter.setMemory(32868L, 2L, 4);
        interpreter.setMemory(32872L, 3L, 4);
        interpreter.setMemory(32876L, 4L, 4);
        interpreter.setMemory(32880L, 5L, 4);
        interpreter.setMemory(32884L, 6L, 4);
        interpreter.setMemory(32888L, 7L, 4);
        interpreter.setMemory(32892L, 8L, 4);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "!"));
        operandTree1.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.EXPRESSION_LIST, "{"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R4"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R5"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R6"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R7"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R8"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R9"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R10"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R11"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("LDMIA", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(32896L), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(32944L), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(20L), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.valueOf(2L), interpreter.getVariableValue("R3"));
        Assert.assertEquals(BigInteger.valueOf(1L), interpreter.getVariableValue("R4"));
        Assert.assertEquals(BigInteger.valueOf(2L), interpreter.getVariableValue("R5"));
        Assert.assertEquals(BigInteger.valueOf(3L), interpreter.getVariableValue("R6"));
        Assert.assertEquals(BigInteger.valueOf(4L), interpreter.getVariableValue("R7"));
        Assert.assertEquals(BigInteger.valueOf(5L), interpreter.getVariableValue("R8"));
        Assert.assertEquals(BigInteger.valueOf(6L), interpreter.getVariableValue("R9"));
        Assert.assertEquals(BigInteger.valueOf(7L), interpreter.getVariableValue("R10"));
        Assert.assertEquals(BigInteger.valueOf(8L), interpreter.getVariableValue("R11"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("N"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Z"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("V"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Q"));
        Assert.assertEquals(BigInteger.valueOf((8 * 4)), BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(18, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testLdmIB() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(32924L), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(32972L), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(20L), dw, DEFINED);
        interpreter.setRegister("R3", BigInteger.valueOf(2L), dw, DEFINED);
        interpreter.setRegister("R4", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R5", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R6", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R7", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R8", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R9", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R10", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("R11", BigInteger.valueOf(0L), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ONE, bt, DEFINED);
        interpreter.setRegister("N", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Z", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("V", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Q", BigInteger.ZERO, bt, DEFINED);
        interpreter.setMemory(32928L, 2L, 4);
        interpreter.setMemory(32932L, 3L, 4);
        interpreter.setMemory(32936L, 4L, 4);
        interpreter.setMemory(32940L, 5L, 4);
        interpreter.setMemory(32944L, 6L, 4);
        interpreter.setMemory(32948L, 7L, 4);
        interpreter.setMemory(32952L, 8L, 4);
        interpreter.setMemory(32956L, 1L, 4);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.EXPRESSION_LIST, "{"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R4"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R5"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R6"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R7"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R8"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R9"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R10"));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R11"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("LDMIB", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(32924L), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(32972L), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(20L), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.valueOf(2L), interpreter.getVariableValue("R3"));
        Assert.assertEquals(BigInteger.valueOf(2L), interpreter.getVariableValue("R4"));
        Assert.assertEquals(BigInteger.valueOf(3L), interpreter.getVariableValue("R5"));
        Assert.assertEquals(BigInteger.valueOf(4L), interpreter.getVariableValue("R6"));
        Assert.assertEquals(BigInteger.valueOf(5L), interpreter.getVariableValue("R7"));
        Assert.assertEquals(BigInteger.valueOf(6L), interpreter.getVariableValue("R8"));
        Assert.assertEquals(BigInteger.valueOf(7L), interpreter.getVariableValue("R9"));
        Assert.assertEquals(BigInteger.valueOf(8L), interpreter.getVariableValue("R10"));
        Assert.assertEquals(BigInteger.valueOf(1L), interpreter.getVariableValue("R11"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("N"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Z"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("V"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Q"));
        Assert.assertEquals(BigInteger.valueOf((8 * 4)), BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(18, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }
}

