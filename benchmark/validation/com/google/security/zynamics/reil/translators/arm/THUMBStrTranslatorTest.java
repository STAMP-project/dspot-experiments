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
public class THUMBStrTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.BIG_ENDIAN, new CpuPolicyARM(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final THUMBStrTranslator translator = new THUMBStrTranslator();

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    final OperandSize dw = OperandSize.DWORD;

    final OperandSize wd = OperandSize.WORD;

    final OperandSize bt = OperandSize.BYTE;

    @Test
    public void testSTRB() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("R0", BigInteger.valueOf(570425310L), dw, DEFINED);
        interpreter.setRegister("R1", BigInteger.valueOf(23L), dw, DEFINED);
        interpreter.setRegister("R2", BigInteger.valueOf(34L), dw, DEFINED);
        interpreter.setRegister("C", BigInteger.ONE, bt, DEFINED);
        interpreter.setRegister("N", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Z", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("V", BigInteger.ZERO, bt, DEFINED);
        interpreter.setRegister("Q", BigInteger.ZERO, bt, DEFINED);
        final MockOperandTree operandTree1 = new MockOperandTree();
        operandTree1.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree1.root.m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R0"));
        final MockOperandTree operandTree2 = new MockOperandTree();
        operandTree2.root = new MockOperandTreeNode(ExpressionType.SIZE_PREFIX, "b4");
        operandTree2.root.m_children.add(new MockOperandTreeNode(ExpressionType.MEMDEREF, "["));
        operandTree2.root.getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.OPERATOR, ","));
        operandTree2.root.getChildren().get(0).getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R1"));
        operandTree2.root.getChildren().get(0).getChildren().get(0).m_children.add(new MockOperandTreeNode(ExpressionType.REGISTER, "R2"));
        final List<MockOperandTree> operands = Lists.newArrayList(operandTree1, operandTree2);
        final IInstruction instruction = new MockInstruction("STR", operands);
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256L));
        Assert.assertEquals(BigInteger.valueOf(570425310L), interpreter.getVariableValue("R0"));
        Assert.assertEquals(BigInteger.valueOf(23L), interpreter.getVariableValue("R1"));
        Assert.assertEquals(BigInteger.valueOf(34L), interpreter.getVariableValue("R2"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("C"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("N"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Z"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("V"));
        Assert.assertEquals(BigInteger.ZERO, interpreter.getVariableValue("Q"));
        Assert.assertEquals(BigInteger.valueOf(570425310L), BigInteger.valueOf(interpreter.readMemoryDword(57L)));
        Assert.assertEquals(BigInteger.valueOf(4), BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(9, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
    }
}

