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


import OperandSize.BYTE;
import OperandSize.DWORD;
import ReilRegisterStatus.DEFINED;
import com.google.security.zynamics.reil.OperandSize;
import com.google.security.zynamics.reil.ReilInstruction;
import com.google.security.zynamics.reil.TestHelpers;
import com.google.security.zynamics.reil.interpreter.CpuPolicyX86;
import com.google.security.zynamics.reil.interpreter.EmptyInterpreterPolicy;
import com.google.security.zynamics.reil.interpreter.Endianness;
import com.google.security.zynamics.reil.interpreter.InterpreterException;
import com.google.security.zynamics.reil.interpreter.ReilInterpreter;
import com.google.security.zynamics.reil.translators.InternalTranslationException;
import com.google.security.zynamics.reil.translators.StandardEnvironment;
import com.google.security.zynamics.zylib.disassembly.MockInstruction;
import java.math.BigInteger;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class RepneCmpsbTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.LITTLE_ENDIAN, new CpuPolicyX86(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final RepneTranslator translator = new RepneTranslator(new CmpsGenerator(), OperandSize.BYTE);

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    @Test
    public void testSearchForward() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("eax", BigInteger.valueOf(305419896), DWORD, DEFINED);
        interpreter.setRegister("esi", BigInteger.valueOf(4096), DWORD, DEFINED);
        interpreter.setRegister("edi", BigInteger.valueOf(8192), DWORD, DEFINED);
        interpreter.setRegister("ecx", BigInteger.valueOf(4), DWORD, DEFINED);
        interpreter.setRegister("DF", BigInteger.valueOf(0), BYTE, DEFINED);
        interpreter.setRegister("ZF", BigInteger.valueOf(0), BYTE, DEFINED);
        interpreter.getMemory().store(4096, 305419896, 4);
        interpreter.getMemory().store(8192, 305419897, 4);
        final MockInstruction instruction = new MockInstruction("repe scasb", new ArrayList<com.google.security.zynamics.zylib.disassembly.MockOperandTree>());
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(10, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(305419896), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.valueOf(4098), interpreter.getVariableValue("esi"));
        Assert.assertEquals(BigInteger.valueOf(8194), interpreter.getVariableValue("edi"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("ZF"));
        Assert.assertEquals(BigInteger.valueOf(2), interpreter.getVariableValue("ecx"));
        Assert.assertEquals(BigInteger.valueOf(8L), BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testSearchForward2() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("eax", BigInteger.valueOf(305419896), DWORD, DEFINED);
        interpreter.setRegister("esi", BigInteger.valueOf(4096), DWORD, DEFINED);
        interpreter.setRegister("edi", BigInteger.valueOf(8192), DWORD, DEFINED);
        interpreter.setRegister("ecx", BigInteger.valueOf(4), DWORD, DEFINED);
        interpreter.setRegister("DF", BigInteger.valueOf(0), BYTE, DEFINED);
        interpreter.setRegister("ZF", BigInteger.valueOf(0), BYTE, DEFINED);
        interpreter.getMemory().store(4096, 305419896, 4);
        interpreter.getMemory().store(8192, 288642680, 4);
        final MockInstruction instruction = new MockInstruction("repe scasb", new ArrayList<com.google.security.zynamics.zylib.disassembly.MockOperandTree>());
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(10, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(305419896), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.valueOf(4097), interpreter.getVariableValue("esi"));
        Assert.assertEquals(BigInteger.valueOf(8193), interpreter.getVariableValue("edi"));
        Assert.assertEquals(BigInteger.ONE, interpreter.getVariableValue("ZF"));
        Assert.assertEquals(BigInteger.valueOf(3), interpreter.getVariableValue("ecx"));
        Assert.assertEquals(BigInteger.valueOf(8L), BigInteger.valueOf(interpreter.getMemorySize()));
    }
}

