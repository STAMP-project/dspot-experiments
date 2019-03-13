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
package com.google.security.zynamics.reil.translators.ppc;


import OperandSize.BYTE;
import OperandSize.DWORD;
import ReilRegisterStatus.DEFINED;
import com.google.common.collect.Lists;
import com.google.security.zynamics.reil.ReilInstruction;
import com.google.security.zynamics.reil.TestHelpers;
import com.google.security.zynamics.reil.interpreter.CpuPolicyPPC;
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
public class ClrrwiDotTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.BIG_ENDIAN, new CpuPolicyPPC(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final ClrrwiDotTranslator translator = new ClrrwiDotTranslator();

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    @Test
    public void testSimple() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("%r0", BigInteger.valueOf(0L), BYTE, DEFINED);
        interpreter.setRegister("%r18", BigInteger.valueOf(809058687L), DWORD, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "%r0"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "%r18"));
        final MockOperandTree operandTree3 = new MockOperandTree();
        operandTree3.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "dword");
        operandTree3.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "5"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2, operandTree3);
        final IInstruction instruction = new MockInstruction("clrrwi.", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(809058656L), interpreter.getVariableValue("%r0"));
        Assert.assertEquals(BigInteger.valueOf(809058687L), interpreter.getVariableValue("%r18"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR0LT"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("CR0GT"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR0EQ"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR0SO"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(8, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }
}

