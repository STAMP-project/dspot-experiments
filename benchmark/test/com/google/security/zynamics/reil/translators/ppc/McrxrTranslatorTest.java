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
public class McrxrTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.BIG_ENDIAN, new CpuPolicyPPC(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final McrxrTranslator translator = new McrxrTranslator();

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    @Test
    public void testCR0Bits() throws InterpreterException, InternalTranslationException {
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "byte");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.IMMEDIATE_INTEGER, "1"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1);
        final IInstruction instruction = new MockInstruction("mcrxr", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("XEROV"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("XERSO"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("XERCA"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("CR0EQ"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("CR0LT"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("CR0GT"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("CR0SO"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR1EQ"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("CR1LT"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("CR1GT"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR1SO"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR2EQ"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR2LT"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR2GT"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR2SO"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR3EQ"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR3LT"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR3GT"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR3SO"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("CR4EQ"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("CR4LT"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("CR4GT"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("CR4SO"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR5EQ"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR5LT"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR5GT"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR5SO"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR6EQ"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR6LT"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR6GT"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR6SO"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR7EQ"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR7LT"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR7GT"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("CR7SO"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(36, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }
}

