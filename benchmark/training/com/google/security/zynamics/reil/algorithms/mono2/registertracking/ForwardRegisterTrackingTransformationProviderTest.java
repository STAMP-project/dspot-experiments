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
public class ForwardRegisterTrackingTransformationProviderTest {
    @Test
    public void testTransformAddBothInputRegisterAreTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createAdd(0, DWORD, "ecx", DWORD, "ebx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformAdd(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ebx", "ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformAddEmptyState() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createAdd(0, DWORD, "eax", DWORD, "ebx", DWORD, "ecx");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformAdd(instruction, new RegisterSetLatticeElement());
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformAddFirstInputRegisterIsTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createAdd(0, DWORD, "ecx", DWORD, "ebx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformAdd(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformAddSecondInputRegisterIsTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createAdd(0, DWORD, "ecx", DWORD, "ebx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformAdd(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ebx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformAndBothInputRegisterAreTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createAnd(0, DWORD, "ecx", DWORD, "ebx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformAnd(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx", "ebx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformAndFirstInputRegisterIsTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createAnd(0, DWORD, "ecx", DWORD, "ebx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformAnd(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformAndSecondInputRegisterIsTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createAnd(0, DWORD, "ebx", DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformAnd(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformAndZeroFirstArgument() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createAnd(0, DWORD, String.valueOf(0), DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformAnd(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformAndZeroSecondArgument() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createAnd(0, DWORD, "ecx", DWORD, String.valueOf(0), DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformAnd(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformAndZeroSecondArgumentTeintedRegisterIsThirdArgument() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createAnd(0, DWORD, "eax", DWORD, String.valueOf(0), DWORD, "ecx");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformAnd(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformBisz() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createBisz(0, DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformBisz(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformBshFirstOperandTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createBsh(0, DWORD, "eax", DWORD, "ebx", DWORD, "ecx");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformBsh(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformBshSecondOperandTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createBsh(0, DWORD, "eax", DWORD, "ebx", DWORD, "ecx");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformBsh(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ebx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformDivFirstOperandTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createDiv(0, DWORD, "eax", DWORD, "ebx", DWORD, "ecx");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformDiv(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformDivSecondOperandTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createDiv(0, DWORD, "eax", DWORD, "ebx", DWORD, "ecx");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformDiv(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ebx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformJccFunctionCallClearAllIsCallFalse() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(true, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createJcc(0, DWORD, "eax", DWORD, "ecx", "isCall", "false");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformJcc(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformJccFunctionCallClearAllIsCallTrue() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(true, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createJcc(0, DWORD, "eax", DWORD, "ecx", "isCall", "true");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformJcc(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformJccFunctionCallClearSet() {
        final Set<String> cleared = new TreeSet<String>();
        cleared.add("ecx");
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, cleared, false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createJcc(0, DWORD, "eax", DWORD, "ecx", "isCall", "true");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformJcc(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformJccNoFunctionCallClear() {
        final Set<String> cleared = new TreeSet<String>();
        cleared.add("ecx");
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, cleared, false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createJcc(0, DWORD, "eax", DWORD, "ecx");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformJcc(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformJccNoTaintconditionVariable() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createJcc(0, DWORD, "eax", DWORD, "ecx");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformJcc(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformLdm() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createLdm(0, DWORD, "eax", DWORD, "ecx");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformLdm(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformLdmFirstOperandTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createLdm(0, DWORD, "eax", DWORD, "ecx");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformLdm(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformModBothInputRegisterIsTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createMod(0, DWORD, "ebx", DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformMod(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx", "ebx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ebx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformModFirstInputRegisterIsTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createMod(0, DWORD, "ecx", DWORD, "ebx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformMod(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformModSecondInputRegisterIsTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createMod(0, DWORD, "ebx", DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformMod(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformMulFirstInputIsZero() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createMul(0, DWORD, String.valueOf("0"), DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformMul(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertFalse(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformMulFirstInputRegisterIsTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createMul(0, DWORD, "ecx", DWORD, "ebx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformMul(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformMulSecondInputIsZero() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createMul(0, DWORD, "ecx", DWORD, String.valueOf("0"), DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformMul(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertFalse(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformMulSecondInputRegisterIsTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createMul(0, DWORD, "ebx", DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformMul(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformMulThirdOutputRegisterIsTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createMul(0, DWORD, "0", DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformMul(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformNop() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createNop(0);
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformAdd(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformOrFirstAllBits() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createOr(0, BYTE, String.valueOf(255L), BYTE, "ecx", BYTE, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformOr(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertFalse(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformOrFirstinputRegisterIsTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createOr(0, DWORD, "ecx", DWORD, "ebx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformOr(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformOrSecondAllBits() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createOr(0, DWORD, "ecx", DWORD, String.valueOf(4294967295L), DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformOr(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertFalse(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformOrSecondinputRegisterIsTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createOr(0, DWORD, "ebx", DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformOr(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformStm() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createStm(0, DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformStm(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertFalse(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformStr() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createStr(0, DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformStr(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformSubFirstInPutRegisterIsTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createSub(0, DWORD, "ecx", DWORD, "ebx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformSub(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformSubIdenticalInput() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createSub(0, DWORD, "ecx", DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformSub(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx", "eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertFalse(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformUndef() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createUndef(0, DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformUndef(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformUnknown() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createUnknown(0);
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformUndef(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformXorFirstInputOperandIsTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createXor(0, DWORD, "ecx", DWORD, "ebx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformXor(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }

    @Test
    public void testTransformXorSameOperands() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createXor(0, DWORD, "ecx", DWORD, "ecx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformXor(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx", "eax"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getReadRegisters().isEmpty());
        Assert.assertFalse(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().contains("eax"));
    }

    @Test
    public void testTransformXorSecondInputOperandIsTainted() {
        final RegisterTrackingTransformationProvider transformationProvider = new RegisterTrackingTransformationProvider(new RegisterTrackingOptions(false, new TreeSet<String>(), false, AnalysisDirection.DOWN));
        final ReilInstruction instruction = ReilHelpers.createXor(0, DWORD, "ecx", DWORD, "ebx", DWORD, "eax");
        final Pair<RegisterSetLatticeElement, RegisterSetLatticeElement> transformationResult = transformationProvider.transformXor(instruction, ForwardRegisterTrackingTransformationProviderTest.createTaintedState("ecx"));
        Assert.assertNull(transformationResult.second());
        transformationResult.first().onInstructionExit();
        Assert.assertTrue(transformationResult.first().getNewlyTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getReadRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("eax"));
        Assert.assertTrue(transformationResult.first().getTaintedRegisters().contains("ecx"));
        Assert.assertTrue(transformationResult.first().getUntaintedRegisters().isEmpty());
        Assert.assertTrue(transformationResult.first().getUpdatedRegisters().isEmpty());
    }
}

