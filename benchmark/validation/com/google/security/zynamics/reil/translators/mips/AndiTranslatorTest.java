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
public class AndiTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.LITTLE_ENDIAN, new CpuPolicyMips(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final AndiTranslator translator = new AndiTranslator();

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    @Test
    public void testAndiOne() throws InterpreterException, InternalTranslationException {
        // andi $3, $31, 0x0F0F
        interpreter.setRegister("$v1", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("$ra", BigInteger.valueOf(4194328L), DWORD, DEFINED);
        // interpreter.setRegister("$", BigInteger.ZERO, OperandSize.DWORD, ReilRegisterStatus.DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$v1"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$ra"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, String.valueOf(3855L)));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("andi", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        // check correct outcome
        Assert.assertEquals(3, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(8L), interpreter.getVariableValue("$v1"));
        Assert.assertEquals(BigInteger.valueOf(4194328L), interpreter.getVariableValue("$ra"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testAndiTwo() throws InterpreterException, InternalTranslationException {
        // andi $3, $31, 0x0F0F
        interpreter.setRegister("$a0", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("$a3", BigInteger.ZERO, DWORD, DEFINED);
        // interpreter.setRegister("$", BigInteger.ZERO, OperandSize.DWORD, ReilRegisterStatus.DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$a0"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "$a3"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, String.valueOf(4660L)));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("andi", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        // check correct outcome
        Assert.assertEquals(3, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("$a0"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("$a3"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }
}

