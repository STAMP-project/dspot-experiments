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
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.disassembly.ExpressionType;
import com.google.security.zynamics.zylib.disassembly.IInstruction;
import com.google.security.zynamics.zylib.disassembly.MockInstruction;
import com.google.security.zynamics.zylib.disassembly.MockOperandTree;
import com.google.security.zynamics.zylib.disassembly.MockOperandTreeNode;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class LoopTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.LITTLE_ENDIAN, new CpuPolicyX86(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final LoopTranslator translator = new LoopTranslator();

    private final DecTranslator decTranslator = new DecTranslator();

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    @Test
    public void testSimple() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("eax", BigInteger.valueOf(3), DWORD, DEFINED);
        interpreter.setRegister("ecx", BigInteger.valueOf(5), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "eax"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1);
        final IInstruction instruction = new MockInstruction("dec", operands);
        final ArrayList<ReilInstruction> instructionsDec = new ArrayList<ReilInstruction>();
        decTranslator.translate(environment, instruction, instructionsDec);
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "256"));
        final MockInstruction instruction2 = new MockInstruction("loop", Lists.newArrayList(operandTree2));
        instruction2.address = new CAddress(257);
        translator.translate(environment, instruction2, instructions);
        final HashMap<BigInteger, List<ReilInstruction>> mapping = new HashMap<BigInteger, List<ReilInstruction>>();
        mapping.put(BigInteger.valueOf(instructions.get(0).getAddress().toLong()), instructions);
        mapping.put(BigInteger.valueOf(instructionsDec.get(0).getAddress().toLong()), instructionsDec);
        interpreter.interpret(mapping, BigInteger.valueOf(256));
        Assert.assertEquals(6, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(4294967294L), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("ecx"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("ZF"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("SF"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("OF"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }
}

