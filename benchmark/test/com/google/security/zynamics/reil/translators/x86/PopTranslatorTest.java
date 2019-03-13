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
public class PopTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.LITTLE_ENDIAN, new CpuPolicyX86(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final PopTranslator translator = new PopTranslator();

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    @Test
    public void testPopSegment() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("esp", BigInteger.valueOf(8192), DWORD, DEFINED);
        interpreter.setMemory(8192, 291, 4);
        final MockOperandTree operandTree = new MockOperandTree();
        operandTree.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "word");
        operandTree.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "ds"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree);
        final IInstruction instruction = new MockInstruction("pop", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(BigInteger.valueOf(8194), interpreter.getVariableValue("esp"));
        Assert.assertEquals(3, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(291L), interpreter.getVariableValue("ds"));
    }

    @Test
    public void testPopToMemory() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("eax", BigInteger.valueOf(4096), DWORD, DEFINED);
        interpreter.setRegister("esp", BigInteger.valueOf(8192), DWORD, DEFINED);
        interpreter.setRegister("dsbase", BigInteger.valueOf(0), DWORD, DEFINED);
        interpreter.setMemory(8192, 291, 4);
        final MockOperandTree operandTree = new MockOperandTree();
        operandTree.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree.root.m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "ds:"));
        operandTree.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.MEMDEREF, "["));
        operandTree.root.m_children.get(0).m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "eax"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree);
        final IInstruction instruction = new MockInstruction("pop", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(BigInteger.valueOf(8196), interpreter.getVariableValue("esp"));
        Assert.assertEquals(BigInteger.valueOf(8L), BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(291, interpreter.readMemoryDword(4096));
        Assert.assertEquals(4, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }

    @Test
    public void testPopToMemoryWithSegmentRegister() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("eax", BigInteger.valueOf(4096), DWORD, DEFINED);
        interpreter.setRegister("esp", BigInteger.valueOf(8192), DWORD, DEFINED);
        interpreter.setRegister("dsbase", BigInteger.valueOf(12288), DWORD, DEFINED);
        interpreter.setMemory(8192, 291, 4);
        final MockOperandTree operandTree = new MockOperandTree();
        operandTree.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree.root.m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, "ds:"));
        operandTree.root.m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.MEMDEREF, "["));
        operandTree.root.m_children.get(0).m_children.get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "eax"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree);
        final IInstruction instruction = new MockInstruction("pop", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(BigInteger.valueOf(8196), interpreter.getVariableValue("esp"));
        Assert.assertEquals(BigInteger.valueOf(8L), BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(291, interpreter.readMemoryDword(16384));
        Assert.assertEquals(4, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }
}

