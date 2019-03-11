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
public class LahfTranslatorTest {
    private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.LITTLE_ENDIAN, new CpuPolicyX86(), new EmptyInterpreterPolicy());

    private final StandardEnvironment environment = new StandardEnvironment();

    private final LahfTranslator translator = new LahfTranslator();

    private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

    @Test
    public void testAuxiliaryIntoCleared() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("SF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("ZF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("AF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("PF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("CF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("eax", BigInteger.ZERO, DWORD, DEFINED);
        final MockInstruction instruction = new MockInstruction("lahf", new ArrayList<com.google.security.zynamics.zylib.disassembly.MockOperandTree>());
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(7, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(4608), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testAuxiliaryIntoSet() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("SF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("ZF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("AF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("PF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("CF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("eax", BigInteger.valueOf(4294967295L), DWORD, DEFINED);
        final MockInstruction instruction = new MockInstruction("lahf", new ArrayList<com.google.security.zynamics.zylib.disassembly.MockOperandTree>());
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(7, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(4294952959L), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testCarryIntoCleared() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("SF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("ZF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("AF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("PF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("CF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("eax", BigInteger.ZERO, DWORD, DEFINED);
        final MockInstruction instruction = new MockInstruction("lahf", new ArrayList<com.google.security.zynamics.zylib.disassembly.MockOperandTree>());
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(7, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(768), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testCarryIntoSet() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("SF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("ZF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("AF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("PF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("CF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("eax", BigInteger.valueOf(4294967295L), DWORD, DEFINED);
        final MockInstruction instruction = new MockInstruction("lahf", new ArrayList<com.google.security.zynamics.zylib.disassembly.MockOperandTree>());
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(7, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(4294956799L), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testParityIntoCleared() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("SF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("ZF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("AF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("PF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("CF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("eax", BigInteger.ZERO, DWORD, DEFINED);
        final MockInstruction instruction = new MockInstruction("lahf", new ArrayList<com.google.security.zynamics.zylib.disassembly.MockOperandTree>());
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(7, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(1536), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testParityIntoSet() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("SF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("ZF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("AF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("PF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("CF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("eax", BigInteger.valueOf(4294967295L), DWORD, DEFINED);
        final MockInstruction instruction = new MockInstruction("lahf", new ArrayList<com.google.security.zynamics.zylib.disassembly.MockOperandTree>());
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(7, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(4294956031L), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testSignIntoCleared() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("SF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("ZF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("AF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("PF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("CF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("eax", BigInteger.ZERO, DWORD, DEFINED);
        final MockInstruction instruction = new MockInstruction("lahf", new ArrayList<com.google.security.zynamics.zylib.disassembly.MockOperandTree>());
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(7, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(33280), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testSignIntoSet() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("SF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("ZF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("AF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("PF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("CF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("eax", BigInteger.valueOf(4294967295L), DWORD, DEFINED);
        final MockInstruction instruction = new MockInstruction("lahf", new ArrayList<com.google.security.zynamics.zylib.disassembly.MockOperandTree>());
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(7, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(4294924287L), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testZeroIntoCleared() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("SF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("ZF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("AF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("PF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("CF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("eax", BigInteger.ZERO, DWORD, DEFINED);
        final MockInstruction instruction = new MockInstruction("lahf", new ArrayList<com.google.security.zynamics.zylib.disassembly.MockOperandTree>());
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(7, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(16896), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }

    @Test
    public void testZeroIntoSet() throws InterpreterException, InternalTranslationException {
        interpreter.setRegister("SF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("ZF", BigInteger.ZERO, DWORD, DEFINED);
        interpreter.setRegister("AF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("PF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("CF", BigInteger.ONE, DWORD, DEFINED);
        interpreter.setRegister("eax", BigInteger.valueOf(4294967295L), DWORD, DEFINED);
        final MockInstruction instruction = new MockInstruction("lahf", new ArrayList<com.google.security.zynamics.zylib.disassembly.MockOperandTree>());
        translator.translate(environment, instruction, instructions);
        interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(256));
        Assert.assertEquals(7, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());
        Assert.assertEquals(BigInteger.valueOf(4294940671L), interpreter.getVariableValue("eax"));
        Assert.assertEquals(BigInteger.ZERO, BigInteger.valueOf(interpreter.getMemorySize()));
    }
}

