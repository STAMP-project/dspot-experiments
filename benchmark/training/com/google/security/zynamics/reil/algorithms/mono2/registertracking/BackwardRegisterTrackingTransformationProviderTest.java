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
package com.google.security.zynamics.reil.algorithms.mono2.registertracking;


import OperandSize.BYTE;
import OperandSize.DWORD;
import com.google.security.zynamics.reil.ReilHelpers;
import com.google.security.zynamics.reil.ReilInstruction;
import com.google.security.zynamics.reil.algorithms.mono2.common.enums.AnalysisDirection;
import com.google.security.zynamics.zylib.general.Pair;
import java.util.Set;
import java.util.TreeSet;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class BackwardRegisterTrackingTransformationProviderTest {
    @Test
    public void testTransformAddEmptyState() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createAdd(0, DWORD, "eax", DWORD, "ebx", DWORD, "ecx");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformAdd(instruction, new RegisterSetLatticeElement());
        Assert.assertNull(transformationResult.second());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().isEmpty());
    }

    @Test
    public void testTransformAddOutputIsTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createAdd(0, DWORD, "ecx", DWORD, "ebx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformAdd(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        Assert.assertFalse(transformationResult.first().isTainted("eax"));
        Assert.assertTrue(transformationResult.first().isTainted("ecx"));
        Assert.assertTrue(transformationResult.first().isTainted("ebx"));
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformAnd() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createAnd(0, DWORD, "ecx", DWORD, "ebx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformAnd(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        Assert.assertFalse(transformationResult.first().isTainted("eax"));
        Assert.assertTrue(transformationResult.first().isTainted("ecx"));
        Assert.assertTrue(transformationResult.first().isTainted("ebx"));
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformAndZeroFirstArgument() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createAnd(0, DWORD, String.valueOf(0), DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformAnd(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        Assert.assertFalse(transformationResult.first().isTainted("eax"));
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("eax"));
    }

    @Test
    public void testTransformAndZeroSecondArgument() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createAnd(0, DWORD, "ecx", DWORD, String.valueOf(0), DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformAnd(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        Assert.assertFalse(transformationResult.first().isTainted("eax"));
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("eax"));
    }

    @Test
    public void testTransformBisz() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createBisz(0, DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformBisz(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
    }

    @Test
    public void testTransformBsh() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createBsh(0, DWORD, "eax", DWORD, "ebx", DWORD, "ecx");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformBsh(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertFalse(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ebx"));
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("ecx"));
    }

    @Test
    public void testTransformDiv() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createDiv(0, DWORD, "eax", DWORD, "ebx", DWORD, "ecx");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformDiv(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertFalse(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ebx"));
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("ecx"));
    }

    @Test
    public void testTransformJccFunctionCallClearAll() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(true, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createJcc(0, DWORD, "eax", DWORD, "ecx", "isCall", "true");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformJcc(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformJccFunctionCallClearSet() {
        final Set<String> cleared = new TreeSet<String>();
        cleared.add("ecx");
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, cleared, false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createJcc(0, DWORD, "eax", DWORD, "ecx", "isCall", "true");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformJcc(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformJccNoFunctionCallClear() {
        final Set<String> cleared = new TreeSet<String>();
        cleared.add("ecx");
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, cleared, false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createJcc(0, DWORD, "eax", DWORD, "ecx");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformJcc(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformJccNoTaintconditionVariable() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createJcc(0, DWORD, "eax", DWORD, "ecx");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformJcc(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertFalse(transformationResult.first().getTaintedRegisters().contains("eax"));
    }

    @Test
    public void testTransformLdm() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createLdm(0, DWORD, "eax", DWORD, "ecx");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformLdm(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformMod() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createMod(0, DWORD, "ecx", DWORD, "ebx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformMod(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        Assert.assertFalse(transformationResult.first().isTainted("eax"));
        Assert.assertTrue(transformationResult.first().isTainted("ecx"));
        Assert.assertTrue(transformationResult.first().isTainted("ebx"));
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformMul() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createMul(0, DWORD, "ecx", DWORD, "ebx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformMul(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        Assert.assertFalse(transformationResult.first().isTainted("eax"));
        Assert.assertTrue(transformationResult.first().isTainted("ecx"));
        Assert.assertTrue(transformationResult.first().isTainted("ebx"));
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformMulFirstZero() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createMul(0, DWORD, "ecx", DWORD, String.valueOf("0"), DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformMul(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("eax"));
    }

    @Test
    public void testTransformMulSecondZero() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createMul(0, DWORD, String.valueOf("0"), DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformMul(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("eax"));
    }

    @Test
    public void testTransformNop() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createNop(0);
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformAdd(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
    }

    @Test
    public void testTransformOr() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createOr(0, DWORD, "ecx", DWORD, "ebx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformOr(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("eax"));
    }

    @Test
    public void testTransformOrFirstAllBits() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createOr(0, DWORD, "ecx", DWORD, String.valueOf(4294967295L), DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformOr(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("eax"));
    }

    @Test
    public void testTransformOrSecondAllBits() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createOr(0, BYTE, String.valueOf(255L), BYTE, "ecx", BYTE, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformOr(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("eax"));
    }

    @Test
    public void testTransformStm() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createStm(0, DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformStm(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
    }

    @Test
    public void testTransformStr() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createStr(0, DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformStr(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("eax"));
    }

    @Test
    public void testTransformSub() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createSub(0, DWORD, "ecx", DWORD, "ebx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformSub(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        Assert.assertFalse(transformationResult.first().isTainted("eax"));
        Assert.assertTrue(transformationResult.first().isTainted("ecx"));
        Assert.assertTrue(transformationResult.first().isTainted("ebx"));
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformSubIdenticalInput() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createSub(0, DWORD, "ecx", DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformSub(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformUndef() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createUndef(0, DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformUndef(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformUnknown() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createUnknown(0);
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformUndef(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformXor() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createXor(0, DWORD, "ecx", DWORD, "ebx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformXor(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformXorSameOperands() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.UP));
        final ReilInstruction instruction = ReilHelpers.createXor(0, DWORD, "ecx", DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformXor(instruction, BackwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("eax"));
    }
}

