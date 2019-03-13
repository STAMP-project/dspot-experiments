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
import ReilRegisterStatus.UNDEFINED;
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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class SarTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.LITTLE_ENDIAN, new CpuPolicyX86(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final SarTranslator translator = new SarTranslator();

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    @Test
    public void testDWordBoundaryShift() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("AF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("CF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("OF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("SF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("ZF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("eax", BigInteger.valueOf(2147483647), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "eax"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "3"));
        final IInstruction instruction = new MockInstruction("sar", Lists.newArrayList(operandTree1, operandTree2));
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(5, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(268435455), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("CF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("ZF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("SF"));
        Assert.assertFalse(interpreter.isDefined("AF"));
        Assert.assertFalse(interpreter.isDefined("OF"));
        Assert.assertTrue(interpreter.isDefined("CF"));
        Assert.assertTrue(interpreter.isDefined("SF"));
        Assert.assertTrue(interpreter.isDefined("ZF"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testDWordShiftNegative() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("AF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("CF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("OF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("SF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("ZF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("eax", BigInteger.valueOf(-1), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "eax"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "3"));
        final IInstruction instruction = new MockInstruction("sar", Lists.newArrayList(operandTree1, operandTree2));
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(5, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(new BigInteger("FFFFFFFF", 16), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("CF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("ZF"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("SF"));
        Assert.assertFalse(interpreter.isDefined("AF"));
        Assert.assertFalse(interpreter.isDefined("OF"));
        Assert.assertTrue(interpreter.isDefined("CF"));
        Assert.assertTrue(interpreter.isDefined("SF"));
        Assert.assertTrue(interpreter.isDefined("ZF"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testDWordShiftPositive() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("AF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("CF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("OF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("PF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("SF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("ZF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("eax", BigInteger.valueOf(21), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "eax"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "1"));
        final IInstruction instruction = new MockInstruction("sar", Lists.newArrayList(operandTree1, operandTree2));
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(6, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(10), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("CF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("ZF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("SF"));
        Assert.assertFalse(interpreter.isDefined("AF"));
        Assert.assertTrue(interpreter.isDefined("OF"));
        Assert.assertTrue(interpreter.isDefined("CF"));
        Assert.assertTrue(interpreter.isDefined("SF"));
        Assert.assertTrue(interpreter.isDefined("ZF"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    // Test -9/4
    @Test
    public void testSignedShift() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("AF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("CF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("OF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("PF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("SF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("ZF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("eax", BigInteger.valueOf(-9), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "eax"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "2"));
        final IInstruction instruction = new MockInstruction("sar", Lists.newArrayList(operandTree1, operandTree2));
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(5, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(new BigInteger("FFFFFFFD", 16), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("CF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("ZF"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("SF"));
        Assert.assertFalse(interpreter.isDefined("AF"));
        Assert.assertFalse(interpreter.isDefined("OF"));
        Assert.assertTrue(interpreter.isDefined("CF"));
        Assert.assertTrue(interpreter.isDefined("SF"));
        Assert.assertTrue(interpreter.isDefined("ZF"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testSingleShift() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("AF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("CF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("OF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("PF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("SF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("ZF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("eax", BigInteger.valueOf(17), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "eax"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "1"));
        final IInstruction instruction = new MockInstruction("sar", Lists.newArrayList(operandTree1, operandTree2));
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(6, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(8), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("CF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("OF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("ZF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("SF"));
        Assert.assertFalse(interpreter.isDefined("AF"));
        Assert.assertTrue(interpreter.isDefined("OF"));
        Assert.assertTrue(interpreter.isDefined("CF"));
        Assert.assertTrue(interpreter.isDefined("SF"));
        Assert.assertTrue(interpreter.isDefined("ZF"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testWordShift() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("AF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("CF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("OF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("PF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("SF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("ZF", BigInteger.ZERO, DWORD, UNDEFINED);
        interpreter.setRegister("eax", BigInteger.valueOf(65535), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "eax"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "3"));
        final IInstruction instruction = new MockInstruction("sar", Lists.newArrayList(operandTree1, operandTree2));
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        System.out.println(("registers: " + (TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()))));
        Assert.assertEquals(5, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(8191), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("CF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("ZF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("SF"));
        Assert.assertFalse(interpreter.isDefined("AF"));
        Assert.assertFalse(interpreter.isDefined("OF"));
        Assert.assertTrue(interpreter.isDefined("CF"));
        Assert.assertTrue(interpreter.isDefined("SF"));
        Assert.assertTrue(interpreter.isDefined("ZF"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testZeroShift() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("AF", BigInteger.ONE, DWORD, UNDEFINED);
        interpreter.setRegister("CF", BigInteger.ONE, DWORD, UNDEFINED);
        interpreter.setRegister("OF", BigInteger.ONE, DWORD, UNDEFINED);
        interpreter.setRegister("SF", BigInteger.ONE, DWORD, UNDEFINED);
        interpreter.setRegister("ZF", BigInteger.ONE, DWORD, UNDEFINED);
        interpreter.setRegister("eax", BigInteger.valueOf(291), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "eax"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "0"));
        final IInstruction instruction = new MockInstruction("sar", Lists.newArrayList(operandTree1, operandTree2));
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(2, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(291), interpreter.getVariableValue("eax"));
        Assert.assertFalse(interpreter.isDefined("AF"));
        Assert.assertFalse(interpreter.isDefined("CF"));
        Assert.assertFalse(interpreter.isDefined("OF"));
        Assert.assertFalse(interpreter.isDefined("SF"));
        Assert.assertFalse(interpreter.isDefined("ZF"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }
}

