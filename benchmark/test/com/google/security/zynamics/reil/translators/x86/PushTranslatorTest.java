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
public class PushTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.LITTLE_ENDIAN, new CpuPolicyX86(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final PushTranslator translator = new PushTranslator();

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    @Test
    public void testPushEsp() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("esp", BigInteger.valueOf(305419896), DWORD, DEFINED);
        final MockOperandTree operandTree = new MockOperandTree();
        operandTree.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "esp"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree);
        final IInstruction instruction = new MockInstruction("push", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(BigInteger.valueOf(4L), BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(305419896, interpreter.readMemoryDword(305419892));
        Assert.assertEquals(2, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testPushL08() throws InterpreterException, InternalTranslationException {
        final MockOperandTree operandTree = new MockOperandTree();
        operandTree.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "byte");
        operandTree.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "12"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree);
        final IInstruction instruction = new MockInstruction("push", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        // Even if you do a "push byte 0x08", this will put the dword 0x00000008
        // on the stack and decrement ESP by 4.
        Assert.assertEquals(BigInteger.valueOf(8188), interpreter.getVariableValue("esp"));
        Assert.assertEquals(BigInteger.valueOf(4L), BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(12, interpreter.readMemoryDword(8188));
        Assert.assertEquals(2, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testPushL16() throws InterpreterException, InternalTranslationException {
        final MockOperandTree operandTree = new MockOperandTree();
        operandTree.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "word");
        operandTree.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "1234"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree);
        final IInstruction instruction = new MockInstruction("push", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(BigInteger.valueOf(8190), interpreter.getVariableValue("esp"));
        Assert.assertEquals(BigInteger.valueOf(2L), BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(1234, interpreter.readMemoryWord(8190));
        Assert.assertEquals(2, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testPushL32() throws InterpreterException, InternalTranslationException {
        final MockOperandTree operandTree = new MockOperandTree();
        operandTree.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "12345678"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree);
        final IInstruction instruction = new MockInstruction("push", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(BigInteger.valueOf(8188), interpreter.getVariableValue("esp"));
        Assert.assertEquals(BigInteger.valueOf(4L), BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(12345678, interpreter.readMemoryDword(8188));
        Assert.assertEquals(2, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testPushR16() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("eax", BigInteger.valueOf(305419896), DWORD, DEFINED);
        final MockOperandTree operandTree = new MockOperandTree();
        operandTree.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "word");
        operandTree.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "ax"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree);
        final IInstruction instruction = new MockInstruction("push", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(BigInteger.valueOf(8190), interpreter.getVariableValue("esp"));
        Assert.assertEquals(BigInteger.valueOf(2L), BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(22136, interpreter.readMemoryWord(8190));
        Assert.assertEquals(3, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testPushR32() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("eax", BigInteger.valueOf(305419896), DWORD, DEFINED);
        final MockOperandTree operandTree = new MockOperandTree();
        operandTree.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "eax"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree);
        final IInstruction instruction = new MockInstruction("push", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(BigInteger.valueOf(8188), interpreter.getVariableValue("esp"));
        Assert.assertEquals(BigInteger.valueOf(4L), BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(305419896, interpreter.readMemoryDword(8188));
        Assert.assertEquals(3, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }
}

