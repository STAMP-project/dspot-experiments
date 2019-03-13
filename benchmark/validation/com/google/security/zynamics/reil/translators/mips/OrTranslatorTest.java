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
package com.google.security.zynamics.reil.translators.mips;


import OperandSize.DWORD;
import ReilRegisterStatus.DEFINED;
import com.google.common.collect.Lists;
import com.google.security.zynamics.reil.ReilInstruction;
import com.google.security.zynamics.reil.TestHelpers;
import com.google.security.zynamics.reil.interpreter.CpuPolicyMips;
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
public class OrTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.LITTLE_ENDIAN, new CpuPolicyMips(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final OrTranslator translator = new OrTranslator();

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    @Test
    public void testMsubu1PositivePositive() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("$v0", BigInteger.valueOf(3735928559L), DWORD, DEFINED);
        interpreter.setRegister("$v1", BigInteger.valueOf(7L), DWORD, DEFINED);
        interpreter.setRegister("$v2", BigInteger.valueOf(3L), DWORD, DEFINED);
        final MockOperandTree operandTree0 = new MockOperandTree();
        operandTree0.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree0.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$v0"));
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$v1"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$v2"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree0, operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("or", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        // check correct outcome
        Assert.assertEquals(4, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(7L), interpreter.getVariableValue("$v0"));
        Assert.assertEquals(BigInteger.valueOf(7L), interpreter.getVariableValue("$v1"));
        Assert.assertEquals(BigInteger.valueOf(3L), interpreter.getVariableValue("$v2"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testMsubu2PositiveNegative() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("$v0", BigInteger.valueOf(3735928559L), DWORD, DEFINED);
        interpreter.setRegister("$v1", BigInteger.valueOf(7L), DWORD, DEFINED);
        interpreter.setRegister("$v2", BigInteger.valueOf(4294967293L), DWORD, DEFINED);
        final MockOperandTree operandTree0 = new MockOperandTree();
        operandTree0.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree0.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$v0"));
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$v1"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$v2"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree0, operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("or", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        // check correct outcome
        Assert.assertEquals(4, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(4294967295L), interpreter.getVariableValue("$v0"));
        Assert.assertEquals(BigInteger.valueOf(7L), interpreter.getVariableValue("$v1"));
        Assert.assertEquals(BigInteger.valueOf(4294967293L), interpreter.getVariableValue("$v2"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testMsubu3NegativePositive() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("$v0", BigInteger.valueOf(3735928559L), DWORD, DEFINED);
        interpreter.setRegister("$v1", BigInteger.valueOf(4294967289L), DWORD, DEFINED);
        interpreter.setRegister("$v2", BigInteger.valueOf(3L), DWORD, DEFINED);
        final MockOperandTree operandTree0 = new MockOperandTree();
        operandTree0.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree0.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$v0"));
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$v1"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$v2"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree0, operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("or", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        // check correct outcome
        Assert.assertEquals(4, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(4294967291L), interpreter.getVariableValue("$v0"));
        Assert.assertEquals(BigInteger.valueOf(4294967289L), interpreter.getVariableValue("$v1"));
        Assert.assertEquals(BigInteger.valueOf(3L), interpreter.getVariableValue("$v2"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testMsubu4NegativeNegative() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("$v0", BigInteger.valueOf(3735928559L), DWORD, DEFINED);
        interpreter.setRegister("$v1", BigInteger.valueOf(4294967289L), DWORD, DEFINED);
        interpreter.setRegister("$v2", BigInteger.valueOf(4294967293L), DWORD, DEFINED);
        final MockOperandTree operandTree0 = new MockOperandTree();
        operandTree0.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree0.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$v0"));
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$v1"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$v2"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree0, operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("or", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        // check correct outcome
        Assert.assertEquals(4, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(4294967293L), interpreter.getVariableValue("$v0"));
        Assert.assertEquals(BigInteger.valueOf(4294967289L), interpreter.getVariableValue("$v1"));
        Assert.assertEquals(BigInteger.valueOf(4294967293L), interpreter.getVariableValue("$v2"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }
}

