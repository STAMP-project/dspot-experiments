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
public class StoswTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.LITTLE_ENDIAN, new CpuPolicyX86(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final StoswTranslator translator = new StoswTranslator();

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    @Test
    public void testDFCleared() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("eax", BigInteger.valueOf(305419896), DWORD, DEFINED);
        interpreter.setRegister("edi", BigInteger.valueOf(4096), DWORD, DEFINED);
        interpreter.setRegister("DF", BigInteger.valueOf(0), BYTE, DEFINED);
        final MockInstruction instruction = new MockInstruction("stosw", new ArrayList<com.google.security.zynamics.zylib.disassembly.MockOperandTree>());
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(4, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(305419896), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.valueOf(4098), interpreter.getVariableValue("edi"));
        Assert.assertEquals(BigInteger.valueOf(2L), BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(22136, interpreter.getMemory().load(4096, 2));
    }

    @Test
    public void testDFSet() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("eax", BigInteger.valueOf(305419896), DWORD, DEFINED);
        interpreter.setRegister("edi", BigInteger.valueOf(4096), DWORD, DEFINED);
        interpreter.setRegister("DF", BigInteger.valueOf(1), BYTE, DEFINED);
        final MockInstruction instruction = new MockInstruction("stosw", new ArrayList<com.google.security.zynamics.zylib.disassembly.MockOperandTree>());
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(4, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(305419896), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.valueOf(4094), interpreter.getVariableValue("edi"));
        Assert.assertEquals(BigInteger.valueOf(2L), BigInteger.valueOf(interpreter.getMemorySize()));
        Assert.assertEquals(22136, interpreter.getMemory().load(4096, 2));
    }
}

